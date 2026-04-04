package com.carelink.translation.service;


//언어 받아서 다른 언어로 변경 인터페이스
public interface TranslationService {
    String translate(String content, String sourceLanguage, String targetLanguage);
    String[] translatePair(String title, String content, String sourceLanguage, String targetLanguage);
}