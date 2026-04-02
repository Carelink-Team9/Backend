package com.carelink.translation.client;

public interface TranslationClient {
    String translate(String content, String sourceLanguage, String targetLanguage);
}