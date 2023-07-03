package tengy.Windows.OpenSubtitles;

import java.util.HashMap;
import java.util.Map;

public enum Language {
    AFRIKAANS("Afrikaans", "af", "afr"),
    ALBANIAN("Albanian", "sq", "alb"),
    ARABIC("Arabic", "ar", "ara"),
    ARAGONESE("Aragonese", "an", "arg"),
    ARMENIAN("Armenian", "hy", "arm"),
    ASTURIAN("Asturian", "at", "ast"),
    BASQUE("Basque", "eu", "baq"),
    BELARUSIAN("Belarusian", "be", "bel"),
    BENGALI("Bengali", "bn", "ben"),
    BOSNIAN("Bosnian", "bs", "bos"),
    BRETON("Breton", "br", "bre"),
    BULGARIAN("Bulgarian", "bg", "bul"),
    BURMESE("Burmese", "my", "bur"),
    CATALAN("Catalan", "ca", "cat"),
    CHINESE_SIMPLIFIED("Chinese (simplified)", "zh-cn", "chi"),
    CHINESE_TRADITIONAL("Chinese (traditional)", "zh-tw", "zht"),
    CHINESE_BILINGUAL("Chinese bilingual", "ze", "zhe"),
    CROATIAN("Croatian", "hr", "hrv"),
    CZECH("Czech", "cs", "cze"),
    DANISH("Danish", "da", "dan"),
    DUTCH("Dutch", "nl", "dut"),
    ENGLISH("English", "en", "eng"),
    ESPERANTO("Esperanto", "eo", "epo"),
    ESTONIAN("Estonian", "et", "est"),
    FINNISH("Finnish", "fi", "fin"),
    FRENCH("French", "fr", "fre"),
    GALICIAN("Galician", "gl", "glg"),
    GEORGIAN("Georgian", "ka", "geo"),
    GERMAN("German", "de", "ger"),
    GREEK("Greek", "el", "ell"),
    HEBREW("Hebrew", "he", "heb"),
    HINDI("Hindi", "hi", "hin"),
    HUNGARIAN("Hungarian", "hu", "hun"),
    ICELANDIC("Icelandic", "is", "ice"),
    INDONESIAN("Indonesian", "id", "ind"),
    ITALIAN("Italian", "it", "ita"),
    JAPANESE("Japanese", "ja", "jpn"),
    KAZAKH("Kazakh", "kk", "kaz"),
    KHMER("Khmer", "km", "khm"),
    KOREAN("Korean", "ko", "kor"),
    LATVIAN("Latvian", "lv", "lav"),
    LITHUANIAN("Lithuanian", "lt", "lit"),
    LUXEMBOURGISH("Luxembourgish", "lb", "ltz"),
    MACEDONIAN("Macedonian", "mk", "mac"),
    MALAY("Malay", "ms", "may"),
    MALAYALAM("Malayalam", "ml", "mal"),
    MANIPURI("Manipuri", "ma", "mni"),
    MONGOLIAN("Mongolian", "mn", "mon"),
    MONTENEGRIN("Montenegrin", "me", "mne"),
    NORWEGIAN("Norwegian", "no", "nor"),
    OCCITAN("Occitan", "oc", "oci"),
    PERSIAN("Persian", "fa", "per"),
    POLISH("Polish", "pl", "pol"),
    PORTUGUESE("Portuguese", "pt-pt", "por"),
    PORTUGUESE_BR("Portuguese (BR)", "pt-br", "pob"),
    ROMANIAN("Romanian", "ro", "rum"),
    RUSSIAN("Russian", "ru", "rus"),
    SERBIAN("Serbian", "sr", "scc"),
    SINHALESE("Sinhalese", "si", "sin"),
    SLOVAK("Slovak", "sk", "slo"),
    SLOVENIAN("Slovenian", "sl", "slv"),
    SPANISH("Spanish", "es", "spa"),
    SWAHILI("Swahili", "sw", "swa"),
    SWEDISH("Swedish", "sv", "swe"),
    SYRIAC("Syriac", "sy", "syr"),
    TAGALOG("Tagalog", "tl", "tgl"),
    TAMIL("Tamil", "ta", "tam"),
    TELUGU("Telugu", "te", "tel"),
    THAI("Thai", "th", "tha"),
    TURKISH("Turkish", "tr", "tur"),
    UKRAINIAN("Ukrainian", "uk", "ukr"),
    URDU("Urdu", "ur", "urd"),
    UZBEK("Uzbek", "uz", "uzb"),
    VIETNAMESE("Vietnamese", "vi", "vie");

    private final String displayName;
    private final String twoLetterCode;
    private final String threeLetterCode;

    private static final Map<String, Language> lookup = new HashMap<>();

    private static final Map<String, String> codes = new HashMap<>();


    static {
        for(Language language : Language.values()){
            lookup.put(language.getDisplayName(), language);
            codes.put(language.getTwoLetterCode(), language.getThreeLetterCode());
        }
    }

    Language(String displayName, String twoLetterCode, String threeLetterCode){
        this.displayName = displayName;
        this.twoLetterCode = twoLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getTwoLetterCode(){
        return twoLetterCode;
    }

    public String getThreeLetterCode(){
        return threeLetterCode;
    }

    public static Language get(String displayName){
        return lookup.get(displayName);
    }

    public static String getThreeLetterCodeFromTwoLetterCode(String twoLetterCode){
        return codes.get(twoLetterCode);
    }
}

