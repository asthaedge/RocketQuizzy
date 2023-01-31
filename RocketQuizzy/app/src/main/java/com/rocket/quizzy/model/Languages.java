package com.rocket.quizzy.model;

public class Languages {
    public static String[] languages() {
        String[] strings = new String[]{
                "Select Your Language :-",
                "Amharic",
                "Arabic",
                "Basque",
                "Bengali",
                "English (UK)",
                "Portuguese (Brazil)",
                "Bulgarian",
                "Catalan",
                "Cherokee",
                "Croatian",
                "Czech",
                "Danish	",
                "Dutch",
                "English (US)",
                "Estonian",
                "Filipino",
                "Finnish",
                "French",
                "German",
                "Greek",
                "Gujarati",
                "Hebrew",
                "Hindi",
                "Hungarian",
                "Icelandic",
                "Indonesian",
                "Italian",
                "Japanese",
                "Kannada",
                "Korean",
                "Latvian",
                "Lithuanian",
                "Malay",
                "Malayalam",
                "Marathi",
                "Norwegian",
                "Polish",
                "Portuguese (Portugal)",
                "Romanian",
                "Russian",
                "Serbian",
                "Chinese (PRC)",
                "Slovak",
                "Slovenian",
                "Spanish",
                "Swahili",
                "Swedish",
                "Tamil",
                "Telugu",
                "Thai",
                "Chinese (Taiwan)",
                "Turkish",
                "Urdu",
                "Ukrainian",
                "Vietnamese",
                "Welsh",
        };
        return strings;
    }

    public static String getLanguageKey(String lang) {
        switch (lang) {
            case "Amharic":
                return "am";
            case "Arabic":
                return "ar";
            case "Basque":
                return "eu";
            case "Bengali":
                return "bn";
            case "English (UK)":
                return "en-GB";
            case "Portuguese (Brazil)":
                return "pt-BR";
            case "Bulgarian":
                return "bg";
            case "Catalan":
                return "ca";
            case "Cherokee":
                return "chr";
            case "Croatian":
                return "hr";
            case "Czech":
                return "cs";
            case "Danish":
                return "da";
            case "Dutch":
                return "nl";
            case "English (US)":
                return "en";
            case "Estonian":
                return "et";
            case "Filipino":
                return "fil";
            case "Finnish":
                return "fi";
            case "French":
                return "fr";
            case "German":
                return "de";
            case "Greek":
                return "el";
            case "Gujarati":
                return "gu";
            case "Hebrew":
                return "iw";
            case "Hindi":
                return "hi";
            case "Hungarian":
                return "hu";
            case "Icelandic":
                return "is";
            case "Indonesian":
                return "id";
            case "Italian":
                return "it";
            case "Japanese":
                return "ja";
            case "Kannada":
                return "kn";
            case "Korean":
                return "ko";
            case "Latvian":
                return "lv";
            case "Lithuanian":
                return "lt";
            case "Malay":
                return "ms";
            case "Malayalam":
                return "ml";
            case "Marathi":
                return "mr";
            case "Norwegian":
                return "no";
            case "Polish":
                return "pl";
            case "Portuguese (Portugal)":
                return "pt-PT";
            case "Romanian":
                return "ro";
            case "Russian":
                return "ru";
            case "Serbian":
                return "sr";
            case "Chinese (PRC)":
                return "zh-CN";
            case "Slovak":
                return "sk";
            case "Slovenian":
                return "sl";
            case "Spanish":
                return "es";
            case "Swahili":
                return "sw";
            case "Swedish":
                return "sv";
            case "Tamil":
                return "ta";
            case "Telugu":
                return "te";
            case "Thai":
                return "th";
            case "Chinese (Taiwan)":
                return "zh-TW";
            case "Turkish":
                return "tr";
            case "Urdu":
                return "ur";
            case "Ukrainian":
                return "uk";
            case "Vietnamese":
                return "vi";
            case "Welsh":
                return "cy";
            default:
                return null;
        }
    }
}
