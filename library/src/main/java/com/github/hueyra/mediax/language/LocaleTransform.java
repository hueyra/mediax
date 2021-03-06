package com.github.hueyra.mediax.language;

import java.util.Locale;

public class LocaleTransform {
    public static Locale getLanguage(int language) {
        switch (language) {
            case LanguageConfig.ENGLISH:
                // 英语-美国
                return Locale.ENGLISH;
            case LanguageConfig.TRADITIONAL_CHINESE:
                // 繁体中文
                return Locale.TRADITIONAL_CHINESE;
            case LanguageConfig.KOREA:
                // 韩语
                return Locale.KOREA;
            case LanguageConfig.GERMANY:
                // 德语
                return Locale.GERMANY;
            case LanguageConfig.FRANCE:
                // 法语
                return Locale.FRANCE;
            case LanguageConfig.JAPAN:
                // 日语
                return Locale.JAPAN;
            case LanguageConfig.VIETNAM:
                // 越南语
                return new Locale("vi");
            case LanguageConfig.SPANISH:
                // 西班牙语
                return new Locale("es", "ES");
            default:
                // 简体中文
                return Locale.CHINESE;
        }
    }
}
