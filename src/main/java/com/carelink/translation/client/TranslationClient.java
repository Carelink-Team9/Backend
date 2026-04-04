package com.carelink.translation.client;

public interface TranslationClient {
    String translate(String content, String sourceLanguage, String targetLanguage);

    default String[] translatePair(String title, String content, String sourceLanguage, String targetLanguage) {
        return new String[]{
            translate(title, sourceLanguage, targetLanguage),
            translate(content, sourceLanguage, targetLanguage)
        };
    }
}