package com.carelink.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
public class HospitalDataSqlGeneratorTest {

    private final RestClient restClient = RestClient.create();

    @Value("${HIRA_API_KEY}")
    private String apiKey;

    private static final String API_URL = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList";
    private static final int NUM_OF_ROWS = 1000;

    // 서울: 110000, 경기: 310000
    private static final Map<String, String> SIDO_CODES = Map.of(
            "110000", "서울",
            "310000", "경기"
    );

    @Test
    public void generateFlywaySql() throws IOException {
        // ykiho(요양기관 고유키)로 중복 제거
        Map<String, Element> uniqueHospitals = new LinkedHashMap<>();

        System.out.println("병원 데이터 Flyway SQL 추출 시작...");

        for (Map.Entry<String, String> sido : SIDO_CODES.entrySet()) {
            String sidoCd = sido.getKey();
            String sidoName = sido.getValue();
            int pageNo = 1;
            System.out.println(sidoName + "(" + sidoCd + ") 수집 시작...");

            while (true) {
                try {
                    URI uri = UriComponentsBuilder.fromUriString(API_URL)
                            .queryParam("serviceKey", apiKey)
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", NUM_OF_ROWS)
                            .queryParam("sidoCd", sidoCd)
                            .build(true)
                            .toUri();

                    byte[] responseBytes = restClient.get()
                            .uri(uri)
                            .retrieve()
                            .body(byte[].class);

                    Document doc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(new ByteArrayInputStream(responseBytes));

                    NodeList items = doc.getElementsByTagName("item");

                    if (items.getLength() == 0) {
                        System.out.println(sidoName + " 수집 완료.");
                        break;
                    }

                    for (int i = 0; i < items.getLength(); i++) {
                        Element item = (Element) items.item(i);
                        String ykiho = getTag(item, "ykiho");
                        if (ykiho.isEmpty() || uniqueHospitals.containsKey(ykiho)) continue;
                        uniqueHospitals.put(ykiho, item);
                    }

                    System.out.printf("%s %d페이지 완료 (누적: %d건)%n", sidoName, pageNo, uniqueHospitals.size());
                    pageNo++;

                } catch (Exception e) {
                    System.err.println("에러 발생 (" + sidoName + " page " + pageNo + "): " + e.getMessage());
                    break;
                }
            }
        }

        String filename = "V5__insert_hospital_data_seoul_gyeonggi.sql";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            writer.write("-- 병원 정보 데이터 삽입\n\n");

            for (Element item : uniqueHospitals.values()) {
                String name       = escapeSql(getTag(item, "yadmNm"));
                String address    = escapeSql(getTag(item, "addr"));
                String department = escapeSql(getTag(item, "clCdNm"));
                String phone      = escapeSql(getTag(item, "telno"));
                String ypos       = getTag(item, "YPos");
                String xpos       = getTag(item, "XPos");
                String latitude   = ypos.isEmpty() ? "NULL" : ypos;
                String longitude  = xpos.isEmpty() ? "NULL" : xpos;
                String sidoNm     = escapeSql(getTag(item, "sidoCdNm"));
                String sgguNm     = escapeSql(getTag(item, "sgguCdNm"));
                String homepage   = escapeSql(getTag(item, "hospUrl"));

                writer.write(String.format(
                        "INSERT INTO hospital (name, address, department, phone, latitude, longitude, sido_nm, sggu_nm, homepage) " +
                        "VALUES ('%s', '%s', '%s', '%s', %s, %s, '%s', '%s', '%s');\n",
                        name, address, department, phone,
                        latitude, longitude,
                        sidoNm, sgguNm, homepage
                ));
            }
        }

        System.out.println("완료! 총 " + uniqueHospitals.size() + "개의 병원 데이터가 '" + filename + "'에 저장되었습니다.");
        System.out.println("src/main/resources/db/migration 폴더로 이동시키세요!");
    }

    private String getTag(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        if (nl.getLength() == 0) return "";
        return nl.item(0).getTextContent().trim();
    }

    private String escapeSql(String value) {
        if (value == null) return "";
        return value
                .replace("'", "''")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\u00A0", " ");
    }
}
