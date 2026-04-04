
package com.carelink.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
public class DrugDataSqlGeneratorTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient = RestClient.create();

    @Value("${EASY_DRUG_API_KEY}")
    private String apiKey;

    private static final String API_URL = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

    @Test
    public void generateFlywaySql() throws IOException {
        int pageNo = 1;
        Map<String, JsonNode> uniqueDrugs = new HashMap<>();

        System.out.println("🚀 Spring Boot: Flyway SQL 추출 시작...");

        while (true) {
            try {
                URI uri = UriComponentsBuilder.fromUriString(API_URL)
                        .queryParam("serviceKey", apiKey)
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", 100)
                        .queryParam("type", "json")
                        .build(true)
                        .toUri();

                String responseBody = restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(String.class);

                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode itemsNode = rootNode.path("body").path("items");

                if (itemsNode.isMissingNode() || itemsNode.isEmpty()) {
                    break;
                }

                for (JsonNode item : itemsNode) {
                    String itemSeq = item.path("itemSeq").asText(null);
                    if (itemSeq == null || uniqueDrugs.containsKey(itemSeq)) {
                        continue;
                    }
                    uniqueDrugs.put(itemSeq, item);
                }

                System.out.println("✅ " + pageNo + "페이지 데이터 수집 완료");
                pageNo++;

            } catch (Exception e) {
                System.err.println("❌ 에러 발생: " + e.getMessage());
                break;
            }
        }

        // V3으로 파일명 지정 (V1, V2 이미 존재)
        String filename = "V3__insert_drug_data.sql";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("-- 약 정보 데이터 삽입\n\n");

            for (Map.Entry<String, JsonNode> entry : uniqueDrugs.entrySet()) {
                String seq = entry.getKey();
                JsonNode item = entry.getValue();

                String name = escapeSql(item.path("itemName").asText(""));
                String efficacy = escapeSql(item.path("efcyQesitm").asText(""));

                String warnTxt = item.path("atpnWarnQesitm").asText("");
                String atpnTxt = item.path("atpnQesitm").asText("");
                String cautionRaw = warnTxt.isEmpty() ? atpnTxt : "[" + warnTxt + "] " + atpnTxt;
                String caution = escapeSql(cautionRaw);

                String useMethod = escapeSql(item.path("useMethodQesitm").asText(""));
                String intrcQesitm = escapeSql(item.path("intrcQesitm").asText(""));  // 상호작용
                String seQesitm = escapeSql(item.path("seQesitm").asText(""));        // 부작용

                // API의 openDe 필드(YYYYMMDD) 파싱
                String openDe = item.path("openDe").asText("");
                String openedAtSql = (openDe.length() == 8)
                        ? "'" + openDe.substring(0, 4) + "-" + openDe.substring(4, 6) + "-" + openDe.substring(6, 8) + " 00:00:00'"
                        : "NULL";

                String sql = String.format(
                        "INSERT INTO drug (item_seq, name, efficacy, caution, use_method, intrc_qesitm, se_qesitm, opened_at) " +
                                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s);\n",
                        seq, name, efficacy, caution, useMethod, intrcQesitm, seQesitm, openedAtSql
                );
                writer.write(sql);
            }
        }

        System.out.println("🎉 성공! 총 " + uniqueDrugs.size() + "개의 약 정보가 '" + filename + "' 파일로 생성되었습니다.");
        System.out.println("해당 파일을 src/main/resources/db/migration 폴더로 이동시키세요!");
    }

    private String escapeSql(String value) {
        if (value == null) return "";
        return value
                .replace("'", "''")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\u00A0", " ");  // NBSP 제거
    }
}