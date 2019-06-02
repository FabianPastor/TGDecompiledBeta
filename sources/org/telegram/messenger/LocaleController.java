package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackDifference;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_langPackString;
import org.telegram.tgnet.TLRPC.TL_langPackStringDeleted;
import org.telegram.tgnet.TLRPC.TL_langPackStringPluralized;
import org.telegram.tgnet.TLRPC.TL_langpack_getDifference;
import org.telegram.tgnet.TLRPC.TL_langpack_getLangPack;
import org.telegram.tgnet.TLRPC.TL_langpack_getLanguages;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.Vector;

public class LocaleController {
    private static volatile LocaleController Instance = null;
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private HashMap<String, PluralRules> allRules = new HashMap();
    private boolean changingConfiguration;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private String currentSystemLocale;
    public FastDateFormat formatterBannedUntil;
    public FastDateFormat formatterBannedUntilThisYear;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterDayMonth;
    public FastDateFormat formatterScheduleDay;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public ArrayList<LocaleInfo> languages;
    public HashMap<String, LocaleInfo> languagesDict;
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap();
    private ArrayList<LocaleInfo> otherLanguages;
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages;
    public HashMap<String, LocaleInfo> remoteLanguagesDict;
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    public ArrayList<LocaleInfo> unofficialLanguages;

    public static class LocaleInfo {
        public String baseLangCode;
        public int baseVersion;
        public boolean builtIn;
        public boolean isRtl;
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String pluralLangCode;
        public int serverIndex;
        public String shortName;
        public int version;

        public String getSaveString() {
            String str = this.baseLangCode;
            if (str == null) {
                str = "";
            }
            String str2;
            if (TextUtils.isEmpty(this.pluralLangCode)) {
                str2 = this.shortName;
            } else {
                str2 = this.pluralLangCode;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.name);
            String str3 = "|";
            stringBuilder.append(str3);
            stringBuilder.append(this.nameEnglish);
            stringBuilder.append(str3);
            stringBuilder.append(this.shortName);
            stringBuilder.append(str3);
            stringBuilder.append(this.pathToFile);
            stringBuilder.append(str3);
            stringBuilder.append(this.version);
            stringBuilder.append(str3);
            stringBuilder.append(str);
            stringBuilder.append(str3);
            stringBuilder.append(this.pluralLangCode);
            stringBuilder.append(str3);
            stringBuilder.append(this.isRtl);
            stringBuilder.append(str3);
            stringBuilder.append(this.baseVersion);
            stringBuilder.append(str3);
            stringBuilder.append(this.serverIndex);
            return stringBuilder.toString();
        }

        public static LocaleInfo createWithString(String str) {
            LocaleInfo localeInfo = null;
            if (!(str == null || str.length() == 0)) {
                String[] split = str.split("\\|");
                if (split.length >= 4) {
                    localeInfo = new LocaleInfo();
                    boolean z = false;
                    localeInfo.name = split[0];
                    localeInfo.nameEnglish = split[1];
                    localeInfo.shortName = split[2].toLowerCase();
                    localeInfo.pathToFile = split[3];
                    if (split.length >= 5) {
                        localeInfo.version = Utilities.parseInt(split[4]).intValue();
                    }
                    localeInfo.baseLangCode = split.length >= 6 ? split[5] : "";
                    localeInfo.pluralLangCode = split.length >= 7 ? split[6] : localeInfo.shortName;
                    if (split.length >= 8) {
                        if (Utilities.parseInt(split[7]).intValue() == 1) {
                            z = true;
                        }
                        localeInfo.isRtl = z;
                    }
                    if (split.length >= 9) {
                        localeInfo.baseVersion = Utilities.parseInt(split[8]).intValue();
                    }
                    if (split.length >= 10) {
                        localeInfo.serverIndex = Utilities.parseInt(split[9]).intValue();
                    } else {
                        localeInfo.serverIndex = Integer.MAX_VALUE;
                    }
                    if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                        localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
                    }
                }
            }
            return localeInfo;
        }

        public File getPathToFile() {
            String str = ".xml";
            File filesDirFixed;
            StringBuilder stringBuilder;
            if (isRemote()) {
                filesDirFixed = ApplicationLoader.getFilesDirFixed();
                stringBuilder = new StringBuilder();
                stringBuilder.append("remote_");
                stringBuilder.append(this.shortName);
                stringBuilder.append(str);
                return new File(filesDirFixed, stringBuilder.toString());
            } else if (isUnofficial()) {
                filesDirFixed = ApplicationLoader.getFilesDirFixed();
                stringBuilder = new StringBuilder();
                stringBuilder.append("unofficial_");
                stringBuilder.append(this.shortName);
                stringBuilder.append(str);
                return new File(filesDirFixed, stringBuilder.toString());
            } else {
                return !TextUtils.isEmpty(this.pathToFile) ? new File(this.pathToFile) : null;
            }
        }

        public File getPathToBaseFile() {
            if (!isUnofficial()) {
                return null;
            }
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unofficial_base_");
            stringBuilder.append(this.shortName);
            stringBuilder.append(".xml");
            return new File(filesDirFixed, stringBuilder.toString());
        }

        public String getKey() {
            StringBuilder stringBuilder;
            if (this.pathToFile != null && !isRemote() && !isUnofficial()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("local_");
                stringBuilder.append(this.shortName);
                return stringBuilder.toString();
            } else if (!isUnofficial()) {
                return this.shortName;
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("unofficial_");
                stringBuilder.append(this.shortName);
                return stringBuilder.toString();
            }
        }

        public boolean hasBaseLang() {
            return (!isUnofficial() || TextUtils.isEmpty(this.baseLangCode) || this.baseLangCode.equals(this.shortName)) ? false : true;
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isUnofficial() {
            return "unofficial".equals(this.pathToFile);
        }

        public boolean isLocal() {
            return (TextUtils.isEmpty(this.pathToFile) || isRemote() || isUnofficial()) ? false : true;
        }

        public boolean isBuiltIn() {
            return this.builtIn;
        }

        public String getLangCode() {
            return this.shortName.replace("_", "-");
        }

        public String getBaseLangCode() {
            String str = this.baseLangCode;
            return str == null ? "" : str.replace("_", "-");
        }
    }

    public static abstract class PluralRules {
        public abstract int quantityForNumber(int i);
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new -$$Lambda$LocaleController$TimeZoneChangedReceiver$-tvar_vTwTBeR7FivZoJHGJKUvY(this));
        }

        public /* synthetic */ void lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver() {
            if (!LocaleController.this.formatterDayMonth.getTimeZone().equals(TimeZone.getDefault())) {
                LocaleController.getInstance().recreateFormatters();
            }
        }
    }

    public static class PluralRules_Arabic extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i2 < 3 || i2 > 10) {
                return (i2 < 11 || i2 > 99) ? 0 : 16;
            } else {
                return 8;
            }
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            i %= 10;
            if (i == 1 && i2 != 11) {
                return 2;
            }
            if (i < 2 || i > 4 || (i2 >= 12 && i2 <= 14)) {
                return (i == 0 || ((i >= 5 && i <= 9) || (i2 >= 11 && i2 <= 14))) ? 16 : 0;
            } else {
                return 8;
            }
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? 1 : i == 1 ? 2 : i == 2 ? 4 : i == 3 ? 8 : i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : (i < 2 || i > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int i) {
            return (i < 0 || i >= 2) ? 0 : 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? 1 : (i <= 0 || i >= 2) ? 0 : 2;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return (i % 10 != 1 || i % 100 == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            i %= 10;
            if (i != 1 || (i2 >= 11 && i2 <= 19)) {
                return (i < 2 || i > 9 || (i2 >= 11 && i2 <= 19)) ? 0 : 8;
            } else {
                return 2;
            }
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int i) {
            return (i % 10 != 1 || i == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0 || (i2 >= 2 && i2 <= 10)) {
                return 8;
            }
            return (i2 < 11 || i2 > 19) ? 0 : 16;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int i) {
            return 0;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i == 1) {
                return 2;
            }
            if (i3 < 2 || i3 > 4 || (i2 >= 12 && i2 <= 14)) {
                return ((i3 < 0 || i3 > 1) && ((i3 < 5 || i3 > 9) && (i2 < 12 || i2 > 14))) ? 0 : 16;
            } else {
                return 8;
            }
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            return (i == 0 || (i2 >= 1 && i2 <= 19)) ? 8 : 0;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int i) {
            i %= 100;
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            return (i < 3 || i > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int i) {
            return (i < 0 || i > 1) ? (i < 2 || i > 10) ? 0 : 8 : 2;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : i == 2 ? 4 : 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? 1 : i == 1 ? 2 : i == 2 ? 4 : i == 3 ? 8 : i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int i) {
            return (i == 0 || i == 1) ? 2 : 0;
        }
    }

    private String stringForQuantity(int i) {
        return i != 1 ? i != 2 ? i != 4 ? i != 8 ? i != 16 ? "other" : "many" : "few" : "two" : "one" : "zero";
    }

    public static LocaleController getInstance() {
        LocaleController localeController = Instance;
        if (localeController == null) {
            synchronized (LocaleController.class) {
                localeController = Instance;
                if (localeController == null) {
                    localeController = new LocaleController();
                    Instance = localeController;
                }
            }
        }
        return localeController;
    }

    public LocaleController() {
        int i;
        LocaleInfo localeInfo;
        LocaleInfo languageFromDict;
        boolean z = false;
        this.changingConfiguration = false;
        this.languages = new ArrayList();
        this.unofficialLanguages = new ArrayList();
        this.remoteLanguages = new ArrayList();
        this.remoteLanguagesDict = new HashMap();
        this.languagesDict = new HashMap();
        this.otherLanguages = new ArrayList();
        r2 = new String[56];
        String str = "en";
        r2[5] = str;
        r2[6] = "eo";
        r2[7] = "es";
        r2[8] = "et";
        r2[9] = "fi";
        r2[10] = "fo";
        r2[11] = "gl";
        r2[12] = "he";
        r2[13] = "iw";
        r2[14] = "it";
        r2[15] = "nb";
        r2[16] = "nl";
        r2[17] = "nn";
        r2[18] = "no";
        r2[19] = "sv";
        r2[20] = "af";
        r2[21] = "bg";
        r2[22] = "bn";
        r2[23] = "ca";
        r2[24] = "eu";
        r2[25] = "fur";
        r2[26] = "fy";
        r2[27] = "gu";
        r2[28] = "ha";
        r2[29] = "is";
        r2[30] = "ku";
        r2[31] = "lb";
        r2[32] = "ml";
        r2[33] = "mr";
        r2[34] = "nah";
        r2[35] = "ne";
        r2[36] = "om";
        r2[37] = "or";
        r2[38] = "pa";
        r2[39] = "pap";
        r2[40] = "ps";
        r2[41] = "so";
        r2[42] = "sq";
        r2[43] = "sw";
        r2[44] = "ta";
        r2[45] = "te";
        r2[46] = "tk";
        r2[47] = "ur";
        r2[48] = "zu";
        r2[49] = "mn";
        r2[50] = "gsw";
        r2[51] = "chr";
        r2[52] = "rm";
        r2[53] = "pt";
        r2[54] = "an";
        r2[55] = "ast";
        addRules(r2, new PluralRules_One());
        addRules(new String[]{"cs", "sk"}, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"hr", "ru", "sr", "uk", "be", "bs", "sh"}, new PluralRules_Balkan());
        addRules(new String[]{"lv"}, new PluralRules_Latvian());
        addRules(new String[]{"lt"}, new PluralRules_Lithuanian());
        addRules(new String[]{"pl"}, new PluralRules_Polish());
        addRules(new String[]{"ro", "mo"}, new PluralRules_Romanian());
        addRules(new String[]{"sl"}, new PluralRules_Slovenian());
        addRules(new String[]{"ar"}, new PluralRules_Arabic());
        addRules(new String[]{"mk"}, new PluralRules_Macedonian());
        addRules(new String[]{"cy"}, new PluralRules_Welsh());
        addRules(new String[]{"br"}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "English";
        localeInfo2.nameEnglish = "English";
        localeInfo2.pluralLangCode = str;
        localeInfo2.shortName = str;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Italiano";
        localeInfo2.nameEnglish = "Italian";
        String str2 = "it";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Español";
        localeInfo2.nameEnglish = "Spanish";
        str2 = "es";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Deutsch";
        localeInfo2.nameEnglish = "German";
        str2 = "de";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Nederlands";
        localeInfo2.nameEnglish = "Dutch";
        str2 = "nl";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "العربية";
        localeInfo2.nameEnglish = "Arabic";
        str2 = "ar";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        localeInfo2.isRtl = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Português (Brasil)";
        localeInfo2.nameEnglish = "Portuguese (Brazil)";
        str2 = "pt_br";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        localeInfo2 = new LocaleInfo();
        localeInfo2.name = "한국어";
        localeInfo2.nameEnglish = "Korean";
        str2 = "ko";
        localeInfo2.pluralLangCode = str2;
        localeInfo2.shortName = str2;
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$NNZIDoHieDUSrd9BgBq36GRonDE(this));
        }
        for (i = 0; i < this.otherLanguages.size(); i++) {
            localeInfo = (LocaleInfo) this.otherLanguages.get(i);
            this.languages.add(localeInfo);
            this.languagesDict.put(localeInfo.getKey(), localeInfo);
        }
        for (i = 0; i < this.remoteLanguages.size(); i++) {
            localeInfo = (LocaleInfo) this.remoteLanguages.get(i);
            languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.version = localeInfo.version;
                languageFromDict.baseVersion = localeInfo.baseVersion;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                this.remoteLanguages.set(i, languageFromDict);
            } else {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        for (i = 0; i < this.unofficialLanguages.size(); i++) {
            localeInfo = (LocaleInfo) this.unofficialLanguages.get(i);
            languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.version = localeInfo.version;
                languageFromDict.baseVersion = localeInfo.baseVersion;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                this.unofficialLanguages.set(i, languageFromDict);
            } else {
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = MessagesController.getGlobalMainSettings().getString("language", null);
            if (string != null) {
                localeInfo2 = getLanguageFromDict(string);
                if (localeInfo2 != null) {
                    z = true;
                }
            } else {
                localeInfo2 = null;
            }
            if (localeInfo2 == null && this.systemDefaultLocale.getLanguage() != null) {
                localeInfo2 = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo2 == null) {
                localeInfo2 = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
                if (localeInfo2 == null) {
                    localeInfo2 = getLanguageFromDict(str);
                }
            }
            applyLanguage(localeInfo2, z, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$U712xrnt9cCu1iJYrRj7Tc-xRYo(this));
    }

    public /* synthetic */ void lambda$new$0$LocaleController() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public /* synthetic */ void lambda$new$1$LocaleController() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public LocaleInfo getLanguageFromDict(String str) {
        return str == null ? null : (LocaleInfo) this.languagesDict.get(str.toLowerCase().replace("-", "_"));
    }

    private void addRules(String[] strArr, PluralRules pluralRules) {
        for (Object put : strArr) {
            this.allRules.put(put, pluralRules);
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void reloadCurrentRemoteLocale(int i, String str) {
        if (str != null) {
            str = str.replace("-", "_");
        }
        if (str != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!(str.equals(localeInfo.shortName) || str.equals(this.currentLocaleInfo.baseLangCode))) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, str, true, i);
    }

    public void checkUpdateForCurrentRemoteLocale(int i, int i2, int i3) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null) {
            return;
        }
        if (localeInfo == null || localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
            if (this.currentLocaleInfo.hasBaseLang()) {
                localeInfo = this.currentLocaleInfo;
                if (localeInfo.baseVersion < i3) {
                    applyRemoteLanguage(localeInfo, localeInfo.baseLangCode, false, i);
                }
            }
            LocaleInfo localeInfo2 = this.currentLocaleInfo;
            if (localeInfo2.version < i2) {
                applyRemoteLanguage(localeInfo2, localeInfo2.shortName, false, i);
            }
        }
    }

    private String getLocaleString(Locale locale) {
        String str = "en";
        if (locale == null) {
            return str;
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(country);
        if (variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(variant);
        return stringBuilder.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        String str = "en";
        if (systemDefaultLocale == null) {
            return str;
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            stringBuilder.append('-');
        }
        stringBuilder.append(country);
        if (variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(variant);
        return stringBuilder.toString();
    }

    public static String getLocaleStringIso639() {
        Locale locale = getInstance().currentLocale;
        String str = "en";
        if (locale == null) {
            return str;
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            stringBuilder.append('-');
        }
        stringBuilder.append(country);
        if (variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(variant);
        return stringBuilder.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A:{RETURN} */
    public static java.lang.String getLocaleAlias(java.lang.String r16) {
        /*
        r0 = r16;
        r1 = 0;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r3 = r16.hashCode();
        r4 = 3325; // 0xcfd float:4.66E-42 double:1.643E-320;
        r5 = "fil";
        r6 = "yi";
        r7 = "tl";
        r8 = "no";
        r9 = "nb";
        r10 = "jw";
        r11 = "jv";
        r12 = "ji";
        r13 = "iw";
        r14 = "in";
        r15 = "id";
        r2 = "he";
        if (r3 == r4) goto L_0x00b3;
    L_0x0027:
        r4 = 3355; // 0xd1b float:4.701E-42 double:1.6576E-320;
        if (r3 == r4) goto L_0x00ab;
    L_0x002b:
        r4 = 3365; // 0xd25 float:4.715E-42 double:1.6625E-320;
        if (r3 == r4) goto L_0x00a3;
    L_0x002f:
        r4 = 3374; // 0xd2e float:4.728E-42 double:1.667E-320;
        if (r3 == r4) goto L_0x009b;
    L_0x0033:
        r4 = 3391; // 0xd3f float:4.752E-42 double:1.6754E-320;
        if (r3 == r4) goto L_0x0093;
    L_0x0037:
        r4 = 3508; // 0xdb4 float:4.916E-42 double:1.733E-320;
        if (r3 == r4) goto L_0x008a;
    L_0x003b:
        r4 = 3521; // 0xdc1 float:4.934E-42 double:1.7396E-320;
        if (r3 == r4) goto L_0x0082;
    L_0x003f:
        r4 = 3704; // 0xe78 float:5.19E-42 double:1.83E-320;
        if (r3 == r4) goto L_0x007a;
    L_0x0043:
        r4 = 3856; // 0xvar_ float:5.403E-42 double:1.905E-320;
        if (r3 == r4) goto L_0x0071;
    L_0x0047:
        r4 = 101385; // 0x18CLASSNAME float:1.4207E-40 double:5.0091E-319;
        if (r3 == r4) goto L_0x0068;
    L_0x004c:
        r4 = 3404; // 0xd4c float:4.77E-42 double:1.682E-320;
        if (r3 == r4) goto L_0x005f;
    L_0x0050:
        r4 = 3405; // 0xd4d float:4.771E-42 double:1.6823E-320;
        if (r3 == r4) goto L_0x0056;
    L_0x0054:
        goto L_0x00bb;
    L_0x0056:
        r0 = r0.equals(r10);
        if (r0 == 0) goto L_0x00bb;
    L_0x005c:
        r0 = 2;
        goto L_0x00bc;
    L_0x005f:
        r0 = r0.equals(r11);
        if (r0 == 0) goto L_0x00bb;
    L_0x0065:
        r0 = 8;
        goto L_0x00bc;
    L_0x0068:
        r0 = r0.equals(r5);
        if (r0 == 0) goto L_0x00bb;
    L_0x006e:
        r0 = 10;
        goto L_0x00bc;
    L_0x0071:
        r0 = r0.equals(r6);
        if (r0 == 0) goto L_0x00bb;
    L_0x0077:
        r0 = 11;
        goto L_0x00bc;
    L_0x007a:
        r0 = r0.equals(r7);
        if (r0 == 0) goto L_0x00bb;
    L_0x0080:
        r0 = 4;
        goto L_0x00bc;
    L_0x0082:
        r0 = r0.equals(r8);
        if (r0 == 0) goto L_0x00bb;
    L_0x0088:
        r0 = 3;
        goto L_0x00bc;
    L_0x008a:
        r0 = r0.equals(r9);
        if (r0 == 0) goto L_0x00bb;
    L_0x0090:
        r0 = 9;
        goto L_0x00bc;
    L_0x0093:
        r0 = r0.equals(r12);
        if (r0 == 0) goto L_0x00bb;
    L_0x0099:
        r0 = 5;
        goto L_0x00bc;
    L_0x009b:
        r0 = r0.equals(r13);
        if (r0 == 0) goto L_0x00bb;
    L_0x00a1:
        r0 = 1;
        goto L_0x00bc;
    L_0x00a3:
        r0 = r0.equals(r14);
        if (r0 == 0) goto L_0x00bb;
    L_0x00a9:
        r0 = 0;
        goto L_0x00bc;
    L_0x00ab:
        r0 = r0.equals(r15);
        if (r0 == 0) goto L_0x00bb;
    L_0x00b1:
        r0 = 6;
        goto L_0x00bc;
    L_0x00b3:
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x00bb;
    L_0x00b9:
        r0 = 7;
        goto L_0x00bc;
    L_0x00bb:
        r0 = -1;
    L_0x00bc:
        switch(r0) {
            case 0: goto L_0x00cb;
            case 1: goto L_0x00ca;
            case 2: goto L_0x00c9;
            case 3: goto L_0x00c8;
            case 4: goto L_0x00c7;
            case 5: goto L_0x00c6;
            case 6: goto L_0x00c5;
            case 7: goto L_0x00c4;
            case 8: goto L_0x00c3;
            case 9: goto L_0x00c2;
            case 10: goto L_0x00c1;
            case 11: goto L_0x00c0;
            default: goto L_0x00bf;
        };
    L_0x00bf:
        return r1;
    L_0x00c0:
        return r12;
    L_0x00c1:
        return r7;
    L_0x00c2:
        return r8;
    L_0x00c3:
        return r10;
    L_0x00c4:
        return r13;
    L_0x00c5:
        return r14;
    L_0x00c6:
        return r6;
    L_0x00c7:
        return r5;
    L_0x00c8:
        return r9;
    L_0x00c9:
        return r11;
    L_0x00ca:
        return r2;
    L_0x00cb:
        return r15;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getLocaleAlias(java.lang.String):java.lang.String");
    }

    public boolean applyLanguageFile(File file, int i) {
        String str = "|";
        String str2 = "&";
        try {
            HashMap localeFileStrings = getLocaleFileStrings(file);
            String str3 = (String) localeFileStrings.get("LanguageName");
            String str4 = (String) localeFileStrings.get("LanguageNameInEnglish");
            String str5 = (String) localeFileStrings.get("LanguageCode");
            if (!(str3 == null || str3.length() <= 0 || str4 == null || str4.length() <= 0 || str5 == null || str5.length() <= 0 || str3.contains(str2))) {
                if (!str3.contains(str)) {
                    if (!str4.contains(str2)) {
                        if (!str4.contains(str)) {
                            if (!(str5.contains(str2) || str5.contains(str) || str5.contains("/"))) {
                                if (!str5.contains("\\")) {
                                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str5);
                                    stringBuilder.append(".xml");
                                    File file2 = new File(filesDirFixed, stringBuilder.toString());
                                    if (!AndroidUtilities.copyFile(file, file2)) {
                                        return false;
                                    }
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("local_");
                                    stringBuilder2.append(str5.toLowerCase());
                                    LocaleInfo languageFromDict = getLanguageFromDict(stringBuilder2.toString());
                                    if (languageFromDict == null) {
                                        languageFromDict = new LocaleInfo();
                                        languageFromDict.name = str3;
                                        languageFromDict.nameEnglish = str4;
                                        languageFromDict.shortName = str5.toLowerCase();
                                        languageFromDict.pluralLangCode = languageFromDict.shortName;
                                        languageFromDict.pathToFile = file2.getAbsolutePath();
                                        this.languages.add(languageFromDict);
                                        this.languagesDict.put(languageFromDict.getKey(), languageFromDict);
                                        this.otherLanguages.add(languageFromDict);
                                        saveOtherLanguages();
                                    }
                                    LocaleInfo localeInfo = languageFromDict;
                                    this.localeValues = localeFileStrings;
                                    applyLanguage(localeInfo, true, false, true, false, i);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    private void saveOtherLanguages() {
        String str;
        String saveString;
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        int i2 = 0;
        while (true) {
            str = "&";
            if (i2 >= this.otherLanguages.size()) {
                break;
            }
            saveString = ((LocaleInfo) this.otherLanguages.get(i2)).getSaveString();
            if (saveString != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str);
                }
                stringBuilder.append(saveString);
            }
            i2++;
        }
        edit.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            saveString = ((LocaleInfo) this.remoteLanguages.get(i2)).getSaveString();
            if (saveString != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str);
                }
                stringBuilder.append(saveString);
            }
        }
        edit.putString("remote", stringBuilder.toString());
        stringBuilder.setLength(0);
        while (i < this.unofficialLanguages.size()) {
            String saveString2 = ((LocaleInfo) this.unofficialLanguages.get(i)).getSaveString();
            if (saveString2 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str);
                }
                stringBuilder.append(saveString2);
            }
            i++;
        }
        edit.putString("unofficial", stringBuilder.toString());
        edit.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int i) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo localeInfo2 = null;
            if (this.systemDefaultLocale.getLanguage() != null) {
                localeInfo2 = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo2 == null) {
                localeInfo2 = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (localeInfo2 == null) {
                localeInfo2 = getLanguageFromDict("en");
            }
            applyLanguage(localeInfo2, true, false, i);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        int i = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String string = sharedPreferences.getString("locales", null);
        String str = "&";
        if (!TextUtils.isEmpty(string)) {
            for (String createWithString : string.split(str)) {
                LocaleInfo createWithString2 = LocaleInfo.createWithString(createWithString);
                if (createWithString2 != null) {
                    this.otherLanguages.add(createWithString2);
                }
            }
        }
        string = sharedPreferences.getString("remote", null);
        String str2 = "_";
        String createWithString3 = "-";
        if (!TextUtils.isEmpty(string)) {
            for (String createWithString4 : string.split(str)) {
                LocaleInfo createWithString5 = LocaleInfo.createWithString(createWithString4);
                createWithString5.shortName = createWithString5.shortName.replace(createWithString3, str2);
                if (!(this.remoteLanguagesDict.containsKey(createWithString5.getKey()) || createWithString5 == null)) {
                    this.remoteLanguages.add(createWithString5);
                    this.remoteLanguagesDict.put(createWithString5.getKey(), createWithString5);
                }
            }
        }
        String string2 = sharedPreferences.getString("unofficial", null);
        if (!TextUtils.isEmpty(string2)) {
            String[] split = string2.split(str);
            int length = split.length;
            while (i < length) {
                LocaleInfo createWithString6 = LocaleInfo.createWithString(split[i]);
                createWithString6.shortName = createWithString6.shortName.replace(createWithString3, str2);
                if (createWithString6 != null) {
                    this.unofficialLanguages.add(createWithString6);
                }
                i++;
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x00da A:{SYNTHETIC, Splitter:B:61:0x00da} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00ea A:{SYNTHETIC, Splitter:B:68:0x00ea} */
    private java.util.HashMap<java.lang.String, java.lang.String> getLocaleFileStrings(java.io.File r13, boolean r14) {
        /*
        r12 = this;
        r0 = 0;
        r12.reloadLastFile = r0;
        r1 = 1;
        r2 = 0;
        r3 = r13.exists();	 Catch:{ Exception -> 0x00d2 }
        if (r3 != 0) goto L_0x0011;
    L_0x000b:
        r13 = new java.util.HashMap;	 Catch:{ Exception -> 0x00d2 }
        r13.<init>();	 Catch:{ Exception -> 0x00d2 }
        return r13;
    L_0x0011:
        r3 = new java.util.HashMap;	 Catch:{ Exception -> 0x00d2 }
        r3.<init>();	 Catch:{ Exception -> 0x00d2 }
        r4 = android.util.Xml.newPullParser();	 Catch:{ Exception -> 0x00d2 }
        r5 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x00d2 }
        r5.<init>(r13);	 Catch:{ Exception -> 0x00d2 }
        r13 = "UTF-8";
        r4.setInput(r5, r13);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r13 = r4.getEventType();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r6 = r2;
        r7 = r6;
        r8 = r7;
    L_0x002b:
        if (r13 == r1) goto L_0x00c1;
    L_0x002d:
        r9 = 2;
        if (r13 != r9) goto L_0x0040;
    L_0x0030:
        r13 = r4.getName();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r7 = r4.getAttributeCount();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r7 <= 0) goto L_0x003e;
    L_0x003a:
        r6 = r4.getAttributeValue(r0);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
    L_0x003e:
        r7 = r13;
        goto L_0x009b;
    L_0x0040:
        r9 = 4;
        if (r13 != r9) goto L_0x0095;
    L_0x0043:
        if (r6 == 0) goto L_0x009b;
    L_0x0045:
        r13 = r4.getText();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r13 == 0) goto L_0x0093;
    L_0x004b:
        r13 = r13.trim();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r8 = "&lt;";
        r9 = "<";
        if (r14 == 0) goto L_0x0072;
    L_0x0055:
        r13 = r13.replace(r9, r8);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r8 = ">";
        r9 = "&gt;";
        r13 = r13.replace(r8, r9);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r8 = "'";
        r9 = "\\'";
        r13 = r13.replace(r8, r9);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r8 = "& ";
        r9 = "&amp; ";
        r13 = r13.replace(r8, r9);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        goto L_0x0093;
    L_0x0072:
        r10 = "\\n";
        r11 = "\n";
        r13 = r13.replace(r10, r11);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r10 = "\\";
        r11 = "";
        r13 = r13.replace(r10, r11);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r8 = r13.replace(r8, r9);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r9 = r12.reloadLastFile;	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r9 != 0) goto L_0x009b;
    L_0x008a:
        r13 = r8.equals(r13);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r13 != 0) goto L_0x009b;
    L_0x0090:
        r12.reloadLastFile = r1;	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        goto L_0x009b;
    L_0x0093:
        r8 = r13;
        goto L_0x009b;
    L_0x0095:
        r9 = 3;
        if (r13 != r9) goto L_0x009b;
    L_0x0098:
        r6 = r2;
        r7 = r6;
        r8 = r7;
    L_0x009b:
        if (r7 == 0) goto L_0x00bb;
    L_0x009d:
        r13 = "string";
        r13 = r7.equals(r13);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r13 == 0) goto L_0x00bb;
    L_0x00a5:
        if (r8 == 0) goto L_0x00bb;
    L_0x00a7:
        if (r6 == 0) goto L_0x00bb;
    L_0x00a9:
        r13 = r8.length();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r13 == 0) goto L_0x00bb;
    L_0x00af:
        r13 = r6.length();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        if (r13 == 0) goto L_0x00bb;
    L_0x00b5:
        r3.put(r6, r8);	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        r6 = r2;
        r7 = r6;
        r8 = r7;
    L_0x00bb:
        r13 = r4.next();	 Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        goto L_0x002b;
    L_0x00c1:
        r5.close();	 Catch:{ Exception -> 0x00c5 }
        goto L_0x00c9;
    L_0x00c5:
        r13 = move-exception;
        org.telegram.messenger.FileLog.e(r13);
    L_0x00c9:
        return r3;
    L_0x00ca:
        r13 = move-exception;
        goto L_0x00e8;
    L_0x00cc:
        r13 = move-exception;
        r2 = r5;
        goto L_0x00d3;
    L_0x00cf:
        r13 = move-exception;
        r5 = r2;
        goto L_0x00e8;
    L_0x00d2:
        r13 = move-exception;
    L_0x00d3:
        org.telegram.messenger.FileLog.e(r13);	 Catch:{ all -> 0x00cf }
        r12.reloadLastFile = r1;	 Catch:{ all -> 0x00cf }
        if (r2 == 0) goto L_0x00e2;
    L_0x00da:
        r2.close();	 Catch:{ Exception -> 0x00de }
        goto L_0x00e2;
    L_0x00de:
        r13 = move-exception;
        org.telegram.messenger.FileLog.e(r13);
    L_0x00e2:
        r13 = new java.util.HashMap;
        r13.<init>();
        return r13;
    L_0x00e8:
        if (r5 == 0) goto L_0x00f2;
    L_0x00ea:
        r5.close();	 Catch:{ Exception -> 0x00ee }
        goto L_0x00f2;
    L_0x00ee:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
    L_0x00f2:
        goto L_0x00f4;
    L_0x00f3:
        throw r13;
    L_0x00f4:
        goto L_0x00f3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getLocaleFileStrings(java.io.File, boolean):java.util.HashMap");
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        applyLanguage(localeInfo, z, z2, false, false, i);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, boolean z3, boolean z4, int i) {
        if (localeInfo != null) {
            boolean hasBaseLang = localeInfo.hasBaseLang();
            File pathToFile = localeInfo.getPathToFile();
            File pathToBaseFile = localeInfo.getPathToBaseFile();
            String str = localeInfo.shortName;
            if (!z2) {
                ConnectionsManager.setLangCode(localeInfo.getLangCode());
            }
            if (getLanguageFromDict(localeInfo.getKey()) == null) {
                if (localeInfo.isRemote()) {
                    this.remoteLanguages.add(localeInfo);
                    this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
                    this.languages.add(localeInfo);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo);
                    saveOtherLanguages();
                } else if (localeInfo.isUnofficial()) {
                    this.unofficialLanguages.add(localeInfo);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo);
                    saveOtherLanguages();
                }
            }
            if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (z4 || !pathToFile.exists() || (hasBaseLang && !pathToBaseFile.exists()))) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("reload locale because one of file doesn't exist");
                    stringBuilder.append(pathToFile);
                    stringBuilder.append(" ");
                    stringBuilder.append(pathToBaseFile);
                    FileLog.d(stringBuilder.toString());
                }
                if (z2) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$rPqRyQsgkE1_kSvpx5ngOlMyIY4(this, localeInfo, i));
                } else {
                    applyRemoteLanguage(localeInfo, null, true, i);
                }
            }
            try {
                String[] split;
                Locale locale;
                str = "_";
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    split = localeInfo.pluralLangCode.split(str);
                } else if (TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    split = localeInfo.shortName.split(str);
                } else {
                    split = localeInfo.baseLangCode.split(str);
                }
                if (split.length == 1) {
                    locale = new Locale(split[0]);
                } else {
                    locale = new Locale(split[0], split[1]);
                }
                if (z) {
                    this.languageOverride = localeInfo.shortName;
                    Editor edit = MessagesController.getGlobalMainSettings().edit();
                    edit.putString("language", localeInfo.getKey());
                    edit.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!z3) {
                    this.localeValues = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    if (hasBaseLang) {
                        this.localeValues.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                }
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!(this.currentLocaleInfo == null || TextUtils.isEmpty(this.currentLocaleInfo.pluralLangCode))) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(split[0]);
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                        if (this.currentPluralRules == null) {
                            this.currentPluralRules = new PluralRules_None();
                        }
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration configuration = new Configuration();
                configuration.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                if (this.reloadLastFile) {
                    if (z2) {
                        AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$TG7Fd9ju6kk9LWo1RB9e1CZ_6mk(this, i));
                    } else {
                        reloadCurrentRemoteLocale(i, null);
                    }
                    this.reloadLastFile = false;
                }
            } catch (Exception e) {
                FileLog.e(e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    public /* synthetic */ void lambda$applyLanguage$2$LocaleController(LocaleInfo localeInfo, int i) {
        applyRemoteLanguage(localeInfo, null, true, i);
    }

    public /* synthetic */ void lambda$applyLanguage$3$LocaleController(int i) {
        reloadCurrentRemoteLocale(i, null);
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", NUM) : localeInfo.name;
    }

    private String getStringInternal(String str, int i) {
        String str2 = BuildVars.USE_CLOUD_STRINGS ? (String) this.localeValues.get(str) : null;
        if (str2 == null) {
            try {
                str2 = ApplicationLoader.applicationContext.getString(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (str2 != null) {
            return str2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LOC_ERR:");
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    public static String getServerString(String str) {
        String str2 = (String) getInstance().localeValues.get(str);
        if (str2 != null) {
            return str2;
        }
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName());
        return identifier != 0 ? ApplicationLoader.applicationContext.getString(identifier) : str2;
    }

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    public static String getPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LOC_ERR:");
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
        String stringForQuantity = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append("_");
        stringBuilder2.append(stringForQuantity);
        str = stringBuilder2.toString();
        return getString(str, ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LOC_ERR:");
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
        String stringForQuantity = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append("_");
        stringBuilder2.append(stringForQuantity);
        str = stringBuilder2.toString();
        return formatString(str, ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(i));
    }

    public static String formatString(String str, int i, Object... objArr) {
        try {
            String str2 = BuildVars.USE_CLOUD_STRINGS ? (String) getInstance().localeValues.get(str) : null;
            if (str2 == null) {
                str2 = ApplicationLoader.applicationContext.getString(i);
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str2, objArr);
            }
            return String.format(str2, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LOC_ERR: ");
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
    }

    public static String formatTTLString(int i) {
        if (i < 60) {
            return formatPluralString("Seconds", i);
        }
        if (i < 3600) {
            return formatPluralString("Minutes", i / 60);
        } else if (i < 86400) {
            return formatPluralString("Hours", (i / 60) / 60);
        } else {
            String str = "Days";
            if (i < 604800) {
                return formatPluralString(str, ((i / 60) / 60) / 24);
            }
            int i2 = ((i / 60) / 60) / 24;
            String str2 = "Weeks";
            if (i % 7 == 0) {
                return formatPluralString(str2, i2 / 7);
            }
            return String.format("%s %s", new Object[]{formatPluralString(str2, i2 / 7), formatPluralString(str, i2 % 7)});
        }
    }

    public String formatCurrencyString(long j, String str) {
        double d;
        String toUpperCase = str.toUpperCase();
        Object obj = j < 0 ? 1 : null;
        long abs = Math.abs(j);
        Currency instance = Currency.getInstance(toUpperCase);
        Object obj2 = -1;
        String str2 = "IRR";
        switch (toUpperCase.hashCode()) {
            case 65726:
                if (toUpperCase.equals("BHD")) {
                    obj2 = 2;
                    break;
                }
                break;
            case 65759:
                if (toUpperCase.equals("BIF")) {
                    obj2 = 9;
                    break;
                }
                break;
            case 66267:
                if (toUpperCase.equals("BYR")) {
                    obj2 = 10;
                    break;
                }
                break;
            case 66813:
                if (toUpperCase.equals("CLF")) {
                    obj2 = null;
                    break;
                }
                break;
            case 66823:
                if (toUpperCase.equals("CLP")) {
                    obj2 = 11;
                    break;
                }
                break;
            case 67122:
                if (toUpperCase.equals("CVE")) {
                    obj2 = 12;
                    break;
                }
                break;
            case 67712:
                if (toUpperCase.equals("DJF")) {
                    obj2 = 13;
                    break;
                }
                break;
            case 70719:
                if (toUpperCase.equals("GNF")) {
                    obj2 = 14;
                    break;
                }
                break;
            case 72732:
                if (toUpperCase.equals("IQD")) {
                    obj2 = 3;
                    break;
                }
                break;
            case 72777:
                if (toUpperCase.equals(str2)) {
                    obj2 = 1;
                    break;
                }
                break;
            case 72801:
                if (toUpperCase.equals("ISK")) {
                    obj2 = 15;
                    break;
                }
                break;
            case 73631:
                if (toUpperCase.equals("JOD")) {
                    obj2 = 4;
                    break;
                }
                break;
            case 73683:
                if (toUpperCase.equals("JPY")) {
                    obj2 = 16;
                    break;
                }
                break;
            case 74532:
                if (toUpperCase.equals("KMF")) {
                    obj2 = 17;
                    break;
                }
                break;
            case 74704:
                if (toUpperCase.equals("KRW")) {
                    obj2 = 18;
                    break;
                }
                break;
            case 74840:
                if (toUpperCase.equals("KWD")) {
                    obj2 = 5;
                    break;
                }
                break;
            case 75863:
                if (toUpperCase.equals("LYD")) {
                    obj2 = 6;
                    break;
                }
                break;
            case 76263:
                if (toUpperCase.equals("MGA")) {
                    obj2 = 19;
                    break;
                }
                break;
            case 76618:
                if (toUpperCase.equals("MRO")) {
                    obj2 = 29;
                    break;
                }
                break;
            case 78388:
                if (toUpperCase.equals("OMR")) {
                    obj2 = 7;
                    break;
                }
                break;
            case 79710:
                if (toUpperCase.equals("PYG")) {
                    obj2 = 20;
                    break;
                }
                break;
            case 81569:
                if (toUpperCase.equals("RWF")) {
                    obj2 = 21;
                    break;
                }
                break;
            case 83210:
                if (toUpperCase.equals("TND")) {
                    obj2 = 8;
                    break;
                }
                break;
            case 83974:
                if (toUpperCase.equals("UGX")) {
                    obj2 = 22;
                    break;
                }
                break;
            case 84517:
                if (toUpperCase.equals("UYI")) {
                    obj2 = 23;
                    break;
                }
                break;
            case 85132:
                if (toUpperCase.equals("VND")) {
                    obj2 = 24;
                    break;
                }
                break;
            case 85367:
                if (toUpperCase.equals("VUV")) {
                    obj2 = 25;
                    break;
                }
                break;
            case 86653:
                if (toUpperCase.equals("XAF")) {
                    obj2 = 26;
                    break;
                }
                break;
            case 87087:
                if (toUpperCase.equals("XOF")) {
                    obj2 = 27;
                    break;
                }
                break;
            case 87118:
                if (toUpperCase.equals("XPF")) {
                    obj2 = 28;
                    break;
                }
                break;
        }
        String str3 = " %.0f";
        String str4 = " %.2f";
        switch (obj2) {
            case null:
                d = (double) abs;
                Double.isNaN(d);
                d /= 10000.0d;
                str3 = " %.4f";
                break;
            case 1:
                double d2 = (double) (((float) abs) / 100.0f);
                if (abs % 100 != 0) {
                    str3 = str4;
                }
                d = d2;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                d = (double) abs;
                Double.isNaN(d);
                d /= 1000.0d;
                str3 = " %.3f";
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                d = (double) abs;
                break;
            case 29:
                d = (double) abs;
                Double.isNaN(d);
                d /= 10.0d;
                str3 = " %.1f";
                break;
            default:
                d = (double) abs;
                Double.isNaN(d);
                d /= 100.0d;
                str3 = str4;
                break;
        }
        String str5 = "-";
        String str6 = "";
        if (instance != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
            currencyInstance.setCurrency(instance);
            if (toUpperCase.equals(str2)) {
                currencyInstance.setMaximumFractionDigits(0);
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (obj == null) {
                str5 = str6;
            }
            stringBuilder.append(str5);
            stringBuilder.append(currencyInstance.format(d));
            return stringBuilder.toString();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        if (obj == null) {
            str5 = str6;
        }
        stringBuilder2.append(str5);
        Locale locale2 = Locale.US;
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(toUpperCase);
        stringBuilder3.append(str3);
        stringBuilder2.append(String.format(locale2, stringBuilder3.toString(), new Object[]{Double.valueOf(d)}));
        return stringBuilder2.toString();
    }

    public java.lang.String formatCurrencyDecimalString(long r10, java.lang.String r12, boolean r13) {
        /*
        r9 = this;
        r12 = r12.toUpperCase();
        r10 = java.lang.Math.abs(r10);
        r0 = r12.hashCode();
        r1 = 1;
        r2 = 0;
        switch(r0) {
            case 65726: goto L_0x015f;
            case 65759: goto L_0x0154;
            case 66267: goto L_0x0149;
            case 66813: goto L_0x013f;
            case 66823: goto L_0x0134;
            case 67122: goto L_0x0129;
            case 67712: goto L_0x011e;
            case 70719: goto L_0x0113;
            case 72732: goto L_0x0109;
            case 72777: goto L_0x00ff;
            case 72801: goto L_0x00f3;
            case 73631: goto L_0x00e8;
            case 73683: goto L_0x00dc;
            case 74532: goto L_0x00d0;
            case 74704: goto L_0x00c4;
            case 74840: goto L_0x00b9;
            case 75863: goto L_0x00ae;
            case 76263: goto L_0x00a2;
            case 76618: goto L_0x0096;
            case 78388: goto L_0x008b;
            case 79710: goto L_0x007f;
            case 81569: goto L_0x0073;
            case 83210: goto L_0x0067;
            case 83974: goto L_0x005b;
            case 84517: goto L_0x004f;
            case 85132: goto L_0x0043;
            case 85367: goto L_0x0037;
            case 86653: goto L_0x002b;
            case 87087: goto L_0x001f;
            case 87118: goto L_0x0013;
            default: goto L_0x0011;
        };
    L_0x0011:
        goto L_0x0169;
    L_0x0013:
        r0 = "XPF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x001b:
        r0 = 28;
        goto L_0x016a;
    L_0x001f:
        r0 = "XOF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0027:
        r0 = 27;
        goto L_0x016a;
    L_0x002b:
        r0 = "XAF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0033:
        r0 = 26;
        goto L_0x016a;
    L_0x0037:
        r0 = "VUV";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x003f:
        r0 = 25;
        goto L_0x016a;
    L_0x0043:
        r0 = "VND";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x004b:
        r0 = 24;
        goto L_0x016a;
    L_0x004f:
        r0 = "UYI";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0057:
        r0 = 23;
        goto L_0x016a;
    L_0x005b:
        r0 = "UGX";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0063:
        r0 = 22;
        goto L_0x016a;
    L_0x0067:
        r0 = "TND";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x006f:
        r0 = 8;
        goto L_0x016a;
    L_0x0073:
        r0 = "RWF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x007b:
        r0 = 21;
        goto L_0x016a;
    L_0x007f:
        r0 = "PYG";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0087:
        r0 = 20;
        goto L_0x016a;
    L_0x008b:
        r0 = "OMR";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0093:
        r0 = 7;
        goto L_0x016a;
    L_0x0096:
        r0 = "MRO";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x009e:
        r0 = 29;
        goto L_0x016a;
    L_0x00a2:
        r0 = "MGA";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00aa:
        r0 = 19;
        goto L_0x016a;
    L_0x00ae:
        r0 = "LYD";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00b6:
        r0 = 6;
        goto L_0x016a;
    L_0x00b9:
        r0 = "KWD";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00c1:
        r0 = 5;
        goto L_0x016a;
    L_0x00c4:
        r0 = "KRW";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00cc:
        r0 = 18;
        goto L_0x016a;
    L_0x00d0:
        r0 = "KMF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00d8:
        r0 = 17;
        goto L_0x016a;
    L_0x00dc:
        r0 = "JPY";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00e4:
        r0 = 16;
        goto L_0x016a;
    L_0x00e8:
        r0 = "JOD";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00f0:
        r0 = 4;
        goto L_0x016a;
    L_0x00f3:
        r0 = "ISK";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x00fb:
        r0 = 15;
        goto L_0x016a;
    L_0x00ff:
        r0 = "IRR";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0107:
        r0 = 1;
        goto L_0x016a;
    L_0x0109:
        r0 = "IQD";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0111:
        r0 = 3;
        goto L_0x016a;
    L_0x0113:
        r0 = "GNF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x011b:
        r0 = 14;
        goto L_0x016a;
    L_0x011e:
        r0 = "DJF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0126:
        r0 = 13;
        goto L_0x016a;
    L_0x0129:
        r0 = "CVE";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0131:
        r0 = 12;
        goto L_0x016a;
    L_0x0134:
        r0 = "CLP";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x013c:
        r0 = 11;
        goto L_0x016a;
    L_0x013f:
        r0 = "CLF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0147:
        r0 = 0;
        goto L_0x016a;
    L_0x0149:
        r0 = "BYR";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0151:
        r0 = 10;
        goto L_0x016a;
    L_0x0154:
        r0 = "BIF";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x015c:
        r0 = 9;
        goto L_0x016a;
    L_0x015f:
        r0 = "BHD";
        r0 = r12.equals(r0);
        if (r0 == 0) goto L_0x0169;
    L_0x0167:
        r0 = 2;
        goto L_0x016a;
    L_0x0169:
        r0 = -1;
    L_0x016a:
        r3 = " %.0f";
        r4 = " %.2f";
        switch(r0) {
            case 0: goto L_0x01a5;
            case 1: goto L_0x0193;
            case 2: goto L_0x0186;
            case 3: goto L_0x0186;
            case 4: goto L_0x0186;
            case 5: goto L_0x0186;
            case 6: goto L_0x0186;
            case 7: goto L_0x0186;
            case 8: goto L_0x0186;
            case 9: goto L_0x0184;
            case 10: goto L_0x0184;
            case 11: goto L_0x0184;
            case 12: goto L_0x0184;
            case 13: goto L_0x0184;
            case 14: goto L_0x0184;
            case 15: goto L_0x0184;
            case 16: goto L_0x0184;
            case 17: goto L_0x0184;
            case 18: goto L_0x0184;
            case 19: goto L_0x0184;
            case 20: goto L_0x0184;
            case 21: goto L_0x0184;
            case 22: goto L_0x0184;
            case 23: goto L_0x0184;
            case 24: goto L_0x0184;
            case 25: goto L_0x0184;
            case 26: goto L_0x0184;
            case 27: goto L_0x0184;
            case 28: goto L_0x0184;
            case 29: goto L_0x017a;
            default: goto L_0x0171;
        };
    L_0x0171:
        r10 = (double) r10;
        r5 = NUM; // 0xNUM float:0.0 double:100.0;
        java.lang.Double.isNaN(r10);
        r10 = r10 / r5;
        r3 = r4;
        goto L_0x01b1;
    L_0x017a:
        r10 = (double) r10;
        r3 = NUM; // 0xNUM float:0.0 double:10.0;
        java.lang.Double.isNaN(r10);
        r10 = r10 / r3;
        r3 = " %.1f";
        goto L_0x01b1;
    L_0x0184:
        r10 = (double) r10;
        goto L_0x01b1;
    L_0x0186:
        r10 = (double) r10;
        r3 = NUM; // 0x408fNUM float:0.0 double:1000.0;
        java.lang.Double.isNaN(r10);
        r10 = r10 / r3;
        r3 = " %.3f";
        goto L_0x01b1;
    L_0x0193:
        r0 = (float) r10;
        r5 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = r0 / r5;
        r5 = (double) r0;
        r7 = 100;
        r10 = r10 % r7;
        r7 = 0;
        r0 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));
        if (r0 != 0) goto L_0x01a2;
    L_0x01a1:
        goto L_0x01a3;
    L_0x01a2:
        r3 = r4;
    L_0x01a3:
        r10 = r5;
        goto L_0x01b1;
    L_0x01a5:
        r10 = (double) r10;
        r3 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        java.lang.Double.isNaN(r10);
        r10 = r10 / r3;
        r3 = " %.4f";
    L_0x01b1:
        r0 = java.util.Locale.US;
        if (r13 == 0) goto L_0x01b6;
    L_0x01b5:
        goto L_0x01c7;
    L_0x01b6:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "";
        r12.append(r13);
        r12.append(r3);
        r12 = r12.toString();
    L_0x01c7:
        r13 = new java.lang.Object[r1];
        r10 = java.lang.Double.valueOf(r10);
        r13[r2] = r10;
        r10 = java.lang.String.format(r0, r12, r13);
        r10 = r10.trim();
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.formatCurrencyDecimalString(long, java.lang.String, boolean):java.lang.String");
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str, objArr);
            }
            return String.format(str, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LOC_ERR: ");
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
    }

    public static String formatCallDuration(int i) {
        String str = "Minutes";
        if (i > 3600) {
            String formatPluralString = formatPluralString("Hours", i / 3600);
            i = (i % 3600) / 60;
            if (i > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(formatPluralString);
                stringBuilder.append(", ");
                stringBuilder.append(formatPluralString(str, i));
                formatPluralString = stringBuilder.toString();
            }
            return formatPluralString;
        } else if (i > 60) {
            return formatPluralString(str, i / 60);
        } else {
            return formatPluralString("Seconds", i);
        }
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (!this.changingConfiguration) {
            String displayName;
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            Locale locale = configuration.locale;
            this.systemDefaultLocale = locale;
            LocaleInfo localeInfo;
            if (this.languageOverride != null) {
                localeInfo = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(localeInfo, false, false, UserConfig.selectedAccount);
            } else if (locale != null) {
                displayName = locale.getDisplayName();
                String displayName2 = this.currentLocale.getDisplayName();
                if (!(displayName == null || displayName2 == null || displayName.equals(displayName2))) {
                    recreateFormatters();
                }
                this.currentLocale = locale;
                localeInfo = this.currentLocaleInfo;
                if (!(localeInfo == null || TextUtils.isEmpty(localeInfo.pluralLangCode))) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get("en");
                    }
                }
            }
            String systemLocaleStringIso639 = getSystemLocaleStringIso639();
            displayName = this.currentSystemLocale;
            if (!(displayName == null || systemLocaleStringIso639.equals(displayName))) {
                this.currentSystemLocale = systemLocaleStringIso639;
                ConnectionsManager.setSystemLangCode(this.currentSystemLocale);
            }
        }
    }

    public static String formatDateChat(long j) {
        try {
            j *= 1000;
            Calendar.getInstance().setTimeInMillis(j);
            if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return getInstance().chatDate.format(j);
            }
            return getInstance().chatFullDate.format(j);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j));
            }
            if (i3 + 1 == i && i2 == i4) {
                return getString("Yesterday", NUM);
            }
            if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(j));
            }
            return getInstance().formatterYear.format(new Date(j));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
            } else {
                String str = "formatDateAtTime";
                if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                    return formatString(str, NUM, getInstance().formatterDayMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
                }
                return formatString(str, NUM, getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j));
            }
            if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
            }
            String str = "formatDateAtTime";
            if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return formatString(str, NUM, getInstance().chatDate.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            }
            return formatString(str, NUM, getInstance().chatFullDate.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            String str = "LocationUpdatedFormatted";
            Object[] objArr;
            if (i3 == i && i2 == i4) {
                int currentTime = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (j / 1000))) / 60;
                if (currentTime < 1) {
                    return getString("LocationUpdatedJustNow", NUM);
                }
                if (currentTime < 60) {
                    return formatPluralString("UpdatedMinutes", currentTime);
                }
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
                return formatString(str, NUM, objArr);
            } else if (i3 + 1 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
                return formatString(str, NUM, objArr);
            } else {
                String str2 = "formatDateAtTime";
                if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                    objArr = new Object[]{getInstance().formatterDayMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j))};
                    return formatString(str, NUM, formatString(str2, NUM, objArr));
                }
                objArr = new Object[]{getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j))};
                return formatString(str, NUM, formatString(str2, NUM, objArr));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int i) {
        int i2 = (i / 60) / 60;
        i -= (i2 * 60) * 60;
        int i3 = i / 60;
        i -= i3 * 60;
        int i4 = 1;
        if (i2 != 0) {
            Object[] objArr = new Object[1];
            if (i3 <= 30) {
                i4 = 0;
            }
            objArr[0] = Integer.valueOf(i2 + i4);
            return String.format("%dh", objArr);
        }
        String str = "%d";
        if (i3 != 0) {
            Object[] objArr2 = new Object[1];
            if (i <= 30) {
                i4 = 0;
            }
            objArr2[0] = Integer.valueOf(i3 + i4);
            return String.format(str, objArr2);
        }
        return String.format(str, new Object[]{Integer.valueOf(i)});
    }

    public static String formatDateOnline(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            String str = "LastSeenFormatted";
            Object[] objArr;
            if (i3 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
                return formatString(str, NUM, objArr);
            } else if (i3 + 1 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j)));
                return formatString(str, NUM, objArr);
            } else {
                str = "LastSeenDateFormatted";
                String str2 = "formatDateAtTime";
                if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                    objArr = new Object[]{getInstance().formatterDayMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j))};
                    return formatString(str, NUM, formatString(str2, NUM, objArr));
                }
                objArr = new Object[]{getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j))};
                return formatString(str, NUM, formatString(str2, NUM, objArr));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String str, String str2) {
        if (str == null || str.length() == 0) {
            str = str2;
        }
        try {
            locale = FastDateFormat.getInstance(str, locale);
            return locale;
        } catch (Exception unused) {
            return FastDateFormat.getInstance(str2, locale);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x016e  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0181  */
    public void recreateFormatters() {
        /*
        r7 = this;
        r0 = r7.currentLocale;
        if (r0 != 0) goto L_0x0008;
    L_0x0004:
        r0 = java.util.Locale.getDefault();
    L_0x0008:
        r1 = r0.getLanguage();
        if (r1 != 0) goto L_0x0010;
    L_0x000e:
        r1 = "en";
    L_0x0010:
        r1 = r1.toLowerCase();
        r2 = r1.length();
        r3 = 1;
        r4 = "ar";
        r5 = 2;
        if (r2 != r5) goto L_0x003c;
    L_0x001e:
        r2 = r1.equals(r4);
        if (r2 != 0) goto L_0x0067;
    L_0x0024:
        r2 = "fa";
        r2 = r1.equals(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x002c:
        r2 = "he";
        r2 = r1.equals(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x0034:
        r2 = "iw";
        r2 = r1.equals(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x003c:
        r2 = "ar_";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x0044:
        r2 = "fa_";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x004c:
        r2 = "he_";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x0054:
        r2 = "iw_";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0067;
    L_0x005c:
        r2 = r7.currentLocaleInfo;
        if (r2 == 0) goto L_0x0065;
    L_0x0060:
        r2 = r2.isRtl;
        if (r2 == 0) goto L_0x0065;
    L_0x0064:
        goto L_0x0067;
    L_0x0065:
        r2 = 0;
        goto L_0x0068;
    L_0x0067:
        r2 = 1;
    L_0x0068:
        isRTL = r2;
        r2 = "ko";
        r6 = r1.equals(r2);
        if (r6 == 0) goto L_0x0073;
    L_0x0072:
        r3 = 2;
    L_0x0073:
        nameDisplayOrder = r3;
        r3 = NUM; // 0x7f0d0b0d float:1.8747853E38 double:1.0531311753E-314;
        r5 = "formatterMonth";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "dd MMM";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.formatterDayMonth = r3;
        r3 = NUM; // 0x7f0d0b13 float:1.8747865E38 double:1.053131178E-314;
        r5 = "formatterYear";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "dd.MM.yy";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.formatterYear = r3;
        r3 = NUM; // 0x7f0d0b14 float:1.8747867E38 double:1.0531311787E-314;
        r5 = "formatterYearMax";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "dd.MM.yyyy";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.formatterYearMax = r3;
        r3 = NUM; // 0x7f0d0aee float:1.874779E38 double:1.05313116E-314;
        r5 = "chatDate";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "d MMMM";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.chatDate = r3;
        r3 = NUM; // 0x7f0d0aef float:1.8747792E38 double:1.0531311604E-314;
        r5 = "chatFullDate";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "d MMMM yyyy";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.chatFullDate = r3;
        r3 = NUM; // 0x7f0d0b12 float:1.8747863E38 double:1.0531311777E-314;
        r5 = "formatterWeek";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "EEE";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.formatterWeek = r3;
        r3 = NUM; // 0x7f0d0b06 float:1.8747838E38 double:1.053131172E-314;
        r5 = "formatDateScheduleDay";
        r3 = r7.getStringInternal(r5, r3);
        r5 = "EEE MMM d";
        r3 = r7.createFormatter(r0, r3, r5);
        r7.formatterScheduleDay = r3;
        r3 = r1.toLowerCase();
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0104;
    L_0x00f6:
        r1 = r1.toLowerCase();
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x0101;
    L_0x0100:
        goto L_0x0104;
    L_0x0101:
        r1 = java.util.Locale.US;
        goto L_0x0105;
    L_0x0104:
        r1 = r0;
    L_0x0105:
        r2 = is24HourFormat;
        if (r2 == 0) goto L_0x010f;
    L_0x0109:
        r2 = NUM; // 0x7f0d0b0c float:1.874785E38 double:1.053131175E-314;
        r3 = "formatterDay24H";
        goto L_0x0114;
    L_0x010f:
        r2 = NUM; // 0x7f0d0b0b float:1.8747849E38 double:1.0531311743E-314;
        r3 = "formatterDay12H";
    L_0x0114:
        r2 = r7.getStringInternal(r3, r2);
        r3 = is24HourFormat;
        if (r3 == 0) goto L_0x011f;
    L_0x011c:
        r3 = "HH:mm";
        goto L_0x0121;
    L_0x011f:
        r3 = "h:mm a";
    L_0x0121:
        r1 = r7.createFormatter(r1, r2, r3);
        r7.formatterDay = r1;
        r1 = is24HourFormat;
        if (r1 == 0) goto L_0x0131;
    L_0x012b:
        r1 = NUM; // 0x7f0d0b11 float:1.874786E38 double:1.053131177E-314;
        r2 = "formatterStats24H";
        goto L_0x0136;
    L_0x0131:
        r1 = NUM; // 0x7f0d0b10 float:1.8747859E38 double:1.0531311767E-314;
        r2 = "formatterStats12H";
    L_0x0136:
        r1 = r7.getStringInternal(r2, r1);
        r2 = is24HourFormat;
        r3 = "MMM dd yyyy, HH:mm";
        r4 = "MMM dd yyyy, h:mm a";
        if (r2 == 0) goto L_0x0144;
    L_0x0142:
        r2 = r3;
        goto L_0x0145;
    L_0x0144:
        r2 = r4;
    L_0x0145:
        r1 = r7.createFormatter(r0, r1, r2);
        r7.formatterStats = r1;
        r1 = is24HourFormat;
        if (r1 == 0) goto L_0x0155;
    L_0x014f:
        r1 = NUM; // 0x7f0d0b08 float:1.8747842E38 double:1.053131173E-314;
        r2 = "formatterBannedUntil24H";
        goto L_0x015a;
    L_0x0155:
        r1 = NUM; // 0x7f0d0b07 float:1.874784E38 double:1.0531311723E-314;
        r2 = "formatterBannedUntil12H";
    L_0x015a:
        r1 = r7.getStringInternal(r2, r1);
        r2 = is24HourFormat;
        if (r2 == 0) goto L_0x0163;
    L_0x0162:
        goto L_0x0164;
    L_0x0163:
        r3 = r4;
    L_0x0164:
        r1 = r7.createFormatter(r0, r1, r3);
        r7.formatterBannedUntil = r1;
        r1 = is24HourFormat;
        if (r1 == 0) goto L_0x0174;
    L_0x016e:
        r1 = NUM; // 0x7f0d0b0a float:1.8747847E38 double:1.053131174E-314;
        r2 = "formatterBannedUntilThisYear24H";
        goto L_0x0179;
    L_0x0174:
        r1 = NUM; // 0x7f0d0b09 float:1.8747845E38 double:1.0531311733E-314;
        r2 = "formatterBannedUntilThisYear12H";
    L_0x0179:
        r1 = r7.getStringInternal(r2, r1);
        r2 = is24HourFormat;
        if (r2 == 0) goto L_0x0184;
    L_0x0181:
        r2 = "MMM dd, HH:mm";
        goto L_0x0186;
    L_0x0184:
        r2 = "MMM dd, h:mm a";
    L_0x0186:
        r0 = r7.createFormatter(r0, r1, r2);
        r7.formatterBannedUntilThisYear = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.recreateFormatters():void");
    }

    public static boolean isRTLCharacter(char c) {
        return Character.getDirectionality(c) == (byte) 1 || Character.getDirectionality(c) == (byte) 2 || Character.getDirectionality(c) == (byte) 16 || Character.getDirectionality(c) == (byte) 17;
    }

    public static String formatSectionDate(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(1);
            instance.setTimeInMillis(j);
            int i2 = instance.get(1);
            int i3 = instance.get(2);
            String[] strArr = new String[]{getString("January", NUM), getString("February", NUM), getString("March", NUM), getString("April", NUM), getString("May", NUM), getString("June", NUM), getString("July", NUM), getString("August", NUM), getString("September", NUM), getString("October", NUM), getString("November", NUM), getString("December", NUM)};
            if (i == i2) {
                return strArr[i3];
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(strArr[i3]);
            stringBuilder.append(" ");
            stringBuilder.append(i2);
            return stringBuilder.toString();
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(1);
            instance.setTimeInMillis(j);
            if (i == instance.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(j));
            }
            return getInstance().formatterBannedUntil.format(new Date(j));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long j) {
        j *= 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            instance.setTimeInMillis(j);
            int i2 = instance.get(6);
            if (Math.abs(System.currentTimeMillis() - j) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(j));
            }
            i2 -= i;
            if (i2 != 0) {
                if (i2 != -1 || System.currentTimeMillis() - j >= 28800000) {
                    if (i2 <= -7 || i2 > -1) {
                        return getInstance().formatterDayMonth.format(new Date(j));
                    }
                    return getInstance().formatterWeek.format(new Date(j));
                }
            }
            return getInstance().formatterDay.format(new Date(j));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int i, int[] iArr) {
        int i2;
        StringBuilder stringBuilder = new StringBuilder();
        int i3 = 0;
        while (true) {
            i2 = i / 1000;
            if (i2 <= 0) {
                break;
            }
            stringBuilder.append("K");
            i3 = (i % 1000) / 100;
            i = i2;
        }
        if (iArr != null) {
            double d = (double) i;
            double d2 = (double) i3;
            Double.isNaN(d2);
            d2 /= 10.0d;
            Double.isNaN(d);
            double d3 = d + d2;
            for (i2 = 0; i2 < stringBuilder.length(); i2++) {
                d3 *= 1000.0d;
            }
            iArr[0] = (int) d3;
        }
        if (i3 == 0 || stringBuilder.length() <= 0) {
            if (stringBuilder.length() == 2) {
                return String.format(Locale.US, "%dM", new Object[]{Integer.valueOf(i)});
            }
            return String.format(Locale.US, "%d%s", new Object[]{Integer.valueOf(i), stringBuilder.toString()});
        } else if (stringBuilder.length() == 2) {
            return String.format(Locale.US, "%d.%dM", new Object[]{Integer.valueOf(i), Integer.valueOf(i3)});
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(i), Integer.valueOf(i3), stringBuilder.toString()});
        }
    }

    public static String formatUserStatus(int i, User user) {
        return formatUserStatus(i, user, null);
    }

    public static String formatUserStatus(int i, User user, boolean[] zArr) {
        UserStatus userStatus;
        if (user != null) {
            UserStatus userStatus2 = user.status;
            if (userStatus2 != null && userStatus2.expires == 0) {
                if (userStatus2 instanceof TL_userStatusRecently) {
                    userStatus2.expires = -100;
                } else if (userStatus2 instanceof TL_userStatusLastWeek) {
                    userStatus2.expires = -101;
                } else if (userStatus2 instanceof TL_userStatusLastMonth) {
                    userStatus2.expires = -102;
                }
            }
        }
        String str = "Online";
        if (user != null) {
            userStatus = user.status;
            if (userStatus != null && userStatus.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                return getString(str, NUM);
            }
        }
        if (user != null) {
            userStatus = user.status;
            if (!(userStatus == null || userStatus.expires == 0 || UserObject.isDeleted(user) || (user instanceof TL_userEmpty))) {
                i = ConnectionsManager.getInstance(i).getCurrentTime();
                int i2 = user.status.expires;
                if (i2 > i) {
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                    return getString(str, NUM);
                } else if (i2 == -1) {
                    return getString("Invisible", NUM);
                } else {
                    if (i2 == -100) {
                        return getString("Lately", NUM);
                    }
                    if (i2 == -101) {
                        return getString("WithinAWeek", NUM);
                    }
                    if (i2 == -102) {
                        return getString("WithinAMonth", NUM);
                    }
                    return formatDateOnline((long) i2);
                }
            }
        }
        return getString("ALongTimeAgo", NUM);
    }

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TL_langPackDifference tL_langPackDifference, int i) {
        if (this.currentLocaleInfo != null) {
            String toLowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (toLowerCase.equals(this.currentLocaleInfo.shortName) || toLowerCase.equals(this.currentLocaleInfo.baseLangCode)) {
                lambda$null$9$LocaleController(this.currentLocaleInfo, tL_langPackDifference, i);
            }
        }
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void lambda$null$9$LocaleController(LocaleInfo localeInfo, TL_langPackDifference tL_langPackDifference, int i) {
        if (!(tL_langPackDifference == null || tL_langPackDifference.strings.isEmpty() || localeInfo == null || localeInfo.isLocal())) {
            String toLowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            int i2 = toLowerCase.equals(localeInfo.shortName) ? 0 : toLowerCase.equals(localeInfo.baseLangCode) ? 1 : -1;
            if (i2 != -1) {
                File pathToFile;
                if (i2 == 0) {
                    pathToFile = localeInfo.getPathToFile();
                } else {
                    pathToFile = localeInfo.getPathToBaseFile();
                }
                try {
                    HashMap hashMap;
                    if (tL_langPackDifference.from_version == 0) {
                        hashMap = new HashMap();
                    } else {
                        hashMap = getLocaleFileStrings(pathToFile, true);
                    }
                    for (int i3 = 0; i3 < tL_langPackDifference.strings.size(); i3++) {
                        LangPackString langPackString = (LangPackString) tL_langPackDifference.strings.get(i3);
                        if (langPackString instanceof TL_langPackString) {
                            hashMap.put(langPackString.key, escapeString(langPackString.value));
                        } else if (langPackString instanceof TL_langPackStringPluralized) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_zero");
                            Object obj = "";
                            hashMap.put(stringBuilder.toString(), langPackString.zero_value != null ? escapeString(langPackString.zero_value) : obj);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_one");
                            hashMap.put(stringBuilder.toString(), langPackString.one_value != null ? escapeString(langPackString.one_value) : obj);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_two");
                            hashMap.put(stringBuilder.toString(), langPackString.two_value != null ? escapeString(langPackString.two_value) : obj);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_few");
                            hashMap.put(stringBuilder.toString(), langPackString.few_value != null ? escapeString(langPackString.few_value) : obj);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_many");
                            hashMap.put(stringBuilder.toString(), langPackString.many_value != null ? escapeString(langPackString.many_value) : obj);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(langPackString.key);
                            stringBuilder.append("_other");
                            String stringBuilder2 = stringBuilder.toString();
                            if (langPackString.other_value != null) {
                                obj = escapeString(langPackString.other_value);
                            }
                            hashMap.put(stringBuilder2, obj);
                        } else if (langPackString instanceof TL_langPackStringDeleted) {
                            hashMap.remove(langPackString.key);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("save locale file to ");
                        stringBuilder3.append(pathToFile);
                        FileLog.d(stringBuilder3.toString());
                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile));
                    bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    bufferedWriter.write("<resources>\n");
                    for (Entry entry : hashMap.entrySet()) {
                        bufferedWriter.write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[]{entry.getKey(), entry.getValue()}));
                    }
                    bufferedWriter.write("</resources>");
                    bufferedWriter.close();
                    boolean hasBaseLang = localeInfo.hasBaseLang();
                    HashMap localeFileStrings = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    if (hasBaseLang) {
                        localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                    AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$byYfLkXSOjQxKwpHcMJU1BFCcao(this, localeInfo, i2, tL_langPackDifference, localeFileStrings));
                } catch (Exception unused) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$saveRemoteLocaleStrings$4$LocaleController(LocaleInfo localeInfo, int i, TL_langPackDifference tL_langPackDifference, HashMap hashMap) {
        if (localeInfo != null) {
            if (i == 0) {
                localeInfo.version = tL_langPackDifference.version;
            } else {
                localeInfo.baseVersion = tL_langPackDifference.version;
            }
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                String[] split;
                Locale locale;
                String str = "_";
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    split = localeInfo.pluralLangCode.split(str);
                } else if (TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    split = localeInfo.shortName.split(str);
                } else {
                    split = localeInfo.baseLangCode.split(str);
                }
                if (split.length == 1) {
                    locale = new Locale(split[0]);
                } else {
                    locale = new Locale(split[0], split[1]);
                }
                this.languageOverride = localeInfo.shortName;
                Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putString("language", localeInfo.getKey());
                edit.commit();
                this.localeValues = hashMap;
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!(this.currentLocaleInfo == null || TextUtils.isEmpty(this.currentLocaleInfo.pluralLangCode))) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get("en");
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration configuration = new Configuration();
                configuration.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int i) {
        if (!this.loadingRemoteLanguages) {
            this.loadingRemoteLanguages = true;
            ConnectionsManager.getInstance(i).sendRequest(new TL_langpack_getLanguages(), new -$$Lambda$LocaleController$OO-St8W4lBDCp1N4EzTA2EggA1M(this, i), 8);
        }
    }

    public /* synthetic */ void lambda$loadRemoteLanguages$6$LocaleController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$Y6efM2co6N3bq4nUuZDi5VasNV4(this, tLObject, i));
        }
    }

    public /* synthetic */ void lambda$null$5$LocaleController(TLObject tLObject, int i) {
        int i2;
        this.loadingRemoteLanguages = false;
        Vector vector = (Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (i2 = 0; i2 < size; i2++) {
            ((LocaleInfo) this.remoteLanguages.get(i2)).serverIndex = Integer.MAX_VALUE;
        }
        size = vector.objects.size();
        for (i2 = 0; i2 < size; i2++) {
            TL_langPackLanguage tL_langPackLanguage = (TL_langPackLanguage) vector.objects.get(i2);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("loaded lang ");
                stringBuilder.append(tL_langPackLanguage.name);
                FileLog.d(stringBuilder.toString());
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = tL_langPackLanguage.name;
            localeInfo.name = tL_langPackLanguage.native_name;
            localeInfo.shortName = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
            String str = tL_langPackLanguage.base_lang_code;
            if (str != null) {
                localeInfo.baseLangCode = str.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = tL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = tL_langPackLanguage.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = i2;
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
                languageFromDict = localeInfo;
            } else {
                languageFromDict.nameEnglish = localeInfo.nameEnglish;
                languageFromDict.name = localeInfo.name;
                languageFromDict.baseLangCode = localeInfo.baseLangCode;
                languageFromDict.pluralLangCode = localeInfo.pluralLangCode;
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.serverIndex = localeInfo.serverIndex;
            }
            if (!this.remoteLanguagesDict.containsKey(languageFromDict.getKey())) {
                this.remoteLanguages.add(languageFromDict);
                this.remoteLanguagesDict.put(languageFromDict.getKey(), languageFromDict);
            }
        }
        int i3 = 0;
        while (i3 < this.remoteLanguages.size()) {
            LocaleInfo localeInfo2 = (LocaleInfo) this.remoteLanguages.get(i3);
            if (localeInfo2.serverIndex == Integer.MAX_VALUE && localeInfo2 != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("remove lang ");
                    stringBuilder2.append(localeInfo2.getKey());
                    FileLog.d(stringBuilder2.toString());
                }
                this.remoteLanguages.remove(i3);
                this.remoteLanguagesDict.remove(localeInfo2.getKey());
                this.languages.remove(localeInfo2);
                this.languagesDict.remove(localeInfo2.getKey());
                i3--;
            }
            i3++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        applyLanguage(this.currentLocaleInfo, true, false, i);
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, String str, boolean z, int i) {
        if (localeInfo == null) {
            return;
        }
        if (localeInfo == null || localeInfo.isRemote() || localeInfo.isUnofficial()) {
            String str2 = "";
            if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion == 0 || z) {
                    TL_langpack_getLangPack tL_langpack_getLangPack = new TL_langpack_getLangPack();
                    tL_langpack_getLangPack.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack, new -$$Lambda$LocaleController$C7GeSsDILXp-14TJUT5eNNANB6o(this, localeInfo, i), 8);
                } else if (localeInfo.hasBaseLang()) {
                    TL_langpack_getDifference tL_langpack_getDifference = new TL_langpack_getDifference();
                    tL_langpack_getDifference.from_version = localeInfo.baseVersion;
                    tL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                    tL_langpack_getDifference.lang_pack = str2;
                    ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference, new -$$Lambda$LocaleController$s_X_ikLryDU_N2xVuPEFJJqkOk0(this, localeInfo, i), 8);
                }
            }
            if (str != null && !str.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version == 0 || z) {
                for (int i2 = 0; i2 < 3; i2++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TL_langpack_getLangPack tL_langpack_getLangPack2 = new TL_langpack_getLangPack();
                tL_langpack_getLangPack2.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack2, new -$$Lambda$LocaleController$h0eSozyCLW0Mpog1omLBJ_00K-I(this, localeInfo, i), 8);
                return;
            }
            TL_langpack_getDifference tL_langpack_getDifference2 = new TL_langpack_getDifference();
            tL_langpack_getDifference2.from_version = localeInfo.version;
            tL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
            tL_langpack_getDifference2.lang_pack = str2;
            ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference2, new -$$Lambda$LocaleController$JT38tfE7-oOUNrZDivXXSv-b-vg(this, localeInfo, i), 8);
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$8$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$sjCg-VQEumFUYAgaZUDI3dk-dz4(this, localeInfo, tLObject, i));
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$10$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$e6CsiR21q6PI2LDNrAzXcAWuMTk(this, localeInfo, tLObject, i));
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$12$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$BieyCwrY21__jPaUyC1vcjWcFcw(this, localeInfo, tLObject, i));
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$14$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocaleController$9Gyg-n_UT33Nf2VkVVHA1KrlD90(this, localeInfo, tLObject, i));
        }
    }

    public String getTranslitString(String str) {
        return getTranslitString(str, true, false);
    }

    public String getTranslitString(String str, boolean z) {
        return getTranslitString(str, true, z);
    }

    public String getTranslitString(String str, boolean z, boolean z2) {
        if (str == null) {
            return null;
        }
        String str2;
        String str3;
        Object obj;
        String str4 = "h";
        String str5 = "z";
        String str6 = "g";
        String str7 = "d";
        String str8 = "n";
        String str9 = "t";
        String str10 = "s";
        String str11 = "l";
        String str12 = "i";
        String str13 = "r";
        String str14 = "u";
        String str15 = "a";
        String str16 = "e";
        String str17 = "o";
        if (this.ruTranslitChars == null) {
            str2 = str4;
            this.ruTranslitChars = new HashMap(33);
            this.ruTranslitChars.put("а", str15);
            str3 = str15;
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", str6);
            this.ruTranslitChars.put("д", str7);
            this.ruTranslitChars.put("е", str16);
            this.ruTranslitChars.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", str5);
            this.ruTranslitChars.put("и", str12);
            this.ruTranslitChars.put("й", str12);
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", str11);
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", str8);
            this.ruTranslitChars.put("о", str17);
            this.ruTranslitChars.put("п", "p");
            this.ruTranslitChars.put("р", str13);
            this.ruTranslitChars.put("с", str10);
            this.ruTranslitChars.put("т", str9);
            this.ruTranslitChars.put("у", str14);
            this.ruTranslitChars.put("ф", "f");
            obj = str2;
            this.ruTranslitChars.put("х", obj);
            str2 = str9;
            this.ruTranslitChars.put("ц", "ts");
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", str12);
            this.ruTranslitChars.put("ь", "");
            this.ruTranslitChars.put("ъ", "");
            this.ruTranslitChars.put("э", str16);
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        } else {
            str2 = str9;
            str3 = str15;
            obj = str4;
        }
        if (this.translitChars == null) {
            this.translitChars = new HashMap(487);
            this.translitChars.put("ȼ", "c");
            this.translitChars.put("ᶇ", str8);
            this.translitChars.put("ɖ", str7);
            str4 = "y";
            this.translitChars.put("ỿ", str4);
            this.translitChars.put("ᴓ", str17);
            this.translitChars.put("ø", str17);
            String str18 = str5;
            str5 = str3;
            this.translitChars.put("ḁ", str5);
            this.translitChars.put("ʯ", obj);
            this.translitChars.put("ŷ", str4);
            str3 = str8;
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", str14);
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", str11);
            this.translitChars.put("ɪ", str12);
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", str13);
            this.translitChars.put("ě", str16);
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", str17);
            this.translitChars.put("ⱹ", str13);
            this.translitChars.put("ồ", str17);
            this.translitChars.put("ǐ", str12);
            this.translitChars.put("ꝕ", "p");
            this.translitChars.put("ý", str4);
            this.translitChars.put("ḝ", str16);
            this.translitChars.put("ₒ", str17);
            this.translitChars.put("ⱥ", str5);
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", str16);
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", obj);
            this.translitChars.put("ᵬ", "b");
            this.translitChars.put("ṣ", str10);
            this.translitChars.put("đ", str7);
            this.translitChars.put("ỗ", str17);
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", str5);
            this.translitChars.put("ɏ", str4);
            this.translitChars.put("ʌ", "v");
            this.translitChars.put("ꝓ", "p");
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", str7);
            this.translitChars.put("ᴌ", str11);
            this.translitChars.put("ė", str16);
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", str13);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", str17);
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            this.translitChars.put("ᶃ", str6);
            str9 = str3;
            this.translitChars.put("ṋ", str9);
            str3 = str7;
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", str6);
            this.translitChars.put("ǳ", "dz");
            str8 = str18;
            this.translitChars.put("ź", str8);
            Object obj2 = obj;
            this.translitChars.put("ꜷ", "au");
            this.translitChars.put("ǖ", str14);
            this.translitChars.put("ᵹ", str6);
            this.translitChars.put("ȯ", str17);
            this.translitChars.put("ɐ", str5);
            this.translitChars.put("ą", str5);
            this.translitChars.put("õ", str17);
            this.translitChars.put("ɻ", str13);
            this.translitChars.put("ꝍ", str17);
            this.translitChars.put("ǟ", str5);
            this.translitChars.put("ȴ", str11);
            this.translitChars.put("ʂ", str10);
            this.translitChars.put("ﬂ", "fl");
            this.translitChars.put("ȉ", str12);
            this.translitChars.put("ⱻ", str16);
            this.translitChars.put("ṉ", str9);
            this.translitChars.put("ï", str12);
            this.translitChars.put("ñ", str9);
            this.translitChars.put("ᴉ", str12);
            str15 = str2;
            this.translitChars.put("ʇ", str15);
            this.translitChars.put("ẓ", str8);
            this.translitChars.put("ỷ", str4);
            this.translitChars.put("ȳ", str4);
            this.translitChars.put("ṩ", str10);
            this.translitChars.put("ɽ", str13);
            this.translitChars.put("ĝ", str6);
            this.translitChars.put("ᴝ", str14);
            str2 = str4;
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", str12);
            this.translitChars.put("ť", str15);
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", str11);
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", str14);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", str5);
            this.translitChars.put("ǘ", str14);
            this.translitChars.put("ꞅ", str10);
            this.translitChars.put("ᵣ", str13);
            this.translitChars.put("ᴀ", str5);
            this.translitChars.put("ƃ", "b");
            Object obj3 = obj2;
            this.translitChars.put("ḩ", obj3);
            this.translitChars.put("ṧ", str10);
            this.translitChars.put("ₑ", str16);
            this.translitChars.put("ʜ", obj3);
            str18 = str10;
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            str10 = str3;
            this.translitChars.put("ḋ", str10);
            str3 = str11;
            this.translitChars.put("ƣ", "oi");
            this.translitChars.put("ꝑ", "p");
            this.translitChars.put("ħ", obj3);
            this.translitChars.put("ⱴ", "v");
            this.translitChars.put("ẇ", "w");
            this.translitChars.put("ǹ", str9);
            this.translitChars.put("ɯ", "m");
            this.translitChars.put("ɡ", str6);
            this.translitChars.put("ɴ", str9);
            this.translitChars.put("ᴘ", "p");
            this.translitChars.put("ᵥ", "v");
            this.translitChars.put("ū", str14);
            this.translitChars.put("ḃ", "b");
            this.translitChars.put("ṗ", "p");
            this.translitChars.put("å", str5);
            this.translitChars.put("ɕ", "c");
            this.translitChars.put("ọ", str17);
            this.translitChars.put("ắ", str5);
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", str13);
            this.translitChars.put("ô", str17);
            this.translitChars.put("ǿ", str17);
            this.translitChars.put("ṳ", str14);
            this.translitChars.put("ȥ", str8);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", str10);
            this.translitChars.put("ȇ", str16);
            this.translitChars.put("ȕ", str14);
            this.translitChars.put("ȵ", str9);
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", str5);
            this.translitChars.put("ǩ", "k");
            this.translitChars.put("ĩ", str12);
            this.translitChars.put("ṵ", str14);
            this.translitChars.put("ŧ", str15);
            this.translitChars.put("ɾ", str13);
            this.translitChars.put("ƙ", "k");
            this.translitChars.put("ṫ", str15);
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", str5);
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", str3);
            this.translitChars.put("ᶂ", "f");
            str11 = str18;
            this.translitChars.put("ᵴ", str11);
            this.translitChars.put("ꞃ", str13);
            str18 = str6;
            this.translitChars.put("ᶌ", "v");
            this.translitChars.put("ɵ", str17);
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", str14);
            this.translitChars.put("ẑ", str8);
            this.translitChars.put("ṹ", str14);
            this.translitChars.put("ň", str9);
            this.translitChars.put("ʍ", "w");
            this.translitChars.put("ầ", str5);
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", "b");
            this.translitChars.put("ɼ", str13);
            this.translitChars.put("ò", str17);
            this.translitChars.put("ẘ", "w");
            this.translitChars.put("ɗ", str10);
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", str14);
            this.translitChars.put("ᶀ", "b");
            this.translitChars.put("ǜ", str14);
            this.translitChars.put("ẹ", str16);
            this.translitChars.put("ǡ", str5);
            this.translitChars.put("ɥ", obj3);
            this.translitChars.put("ṏ", str17);
            this.translitChars.put("ǔ", str14);
            str6 = str2;
            this.translitChars.put("ʎ", str6);
            this.translitChars.put("ȱ", str17);
            this.translitChars.put("ệ", str16);
            this.translitChars.put("ế", str16);
            this.translitChars.put("ĭ", str12);
            this.translitChars.put("ⱸ", str16);
            this.translitChars.put("ṯ", str15);
            this.translitChars.put("ᶑ", str10);
            this.translitChars.put("ḧ", obj3);
            this.translitChars.put("ṥ", str11);
            this.translitChars.put("ë", str16);
            str2 = str15;
            this.translitChars.put("ᴍ", "m");
            this.translitChars.put("ö", str17);
            this.translitChars.put("é", str16);
            this.translitChars.put("ı", str12);
            this.translitChars.put("ď", str10);
            this.translitChars.put("ᵯ", "m");
            this.translitChars.put("ỵ", str6);
            this.translitChars.put("ŵ", "w");
            this.translitChars.put("ề", str16);
            this.translitChars.put("ứ", str14);
            this.translitChars.put("ƶ", str8);
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", str10);
            this.translitChars.put("ŭ", str14);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", str16);
            this.translitChars.put("ǚ", str14);
            this.translitChars.put("ġ", str18);
            this.translitChars.put("ṙ", str13);
            this.translitChars.put("ƞ", str9);
            this.translitChars.put("ḗ", str16);
            this.translitChars.put("ẝ", str11);
            this.translitChars.put("ᶁ", str10);
            this.translitChars.put("ķ", "k");
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", str16);
            this.translitChars.put("ợ", str17);
            this.translitChars.put("ḿ", "m");
            this.translitChars.put("ꜰ", "f");
            this.translitChars.put("ẵ", str5);
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", "m");
            this.translitChars.put("ᵽ", "p");
            this.translitChars.put("ữ", str14);
            this.translitChars.put("ⱪ", "k");
            this.translitChars.put("ḥ", obj3);
            str15 = str2;
            this.translitChars.put("ţ", str15);
            str2 = str17;
            this.translitChars.put("ᵱ", "p");
            this.translitChars.put("ṁ", "m");
            this.translitChars.put("á", str5);
            this.translitChars.put("ᴎ", str9);
            this.translitChars.put("ꝟ", "v");
            this.translitChars.put("è", str16);
            this.translitChars.put("ᶎ", str8);
            this.translitChars.put("ꝺ", str10);
            this.translitChars.put("ᶈ", "p");
            this.translitChars.put("ɫ", str3);
            this.translitChars.put("ᴢ", str8);
            this.translitChars.put("ɱ", "m");
            this.translitChars.put("ṝ", str13);
            this.translitChars.put("ṽ", "v");
            this.translitChars.put("ũ", str14);
            this.translitChars.put("ß", "ss");
            this.translitChars.put("ĥ", obj3);
            this.translitChars.put("ᵵ", str15);
            this.translitChars.put("ʐ", str8);
            this.translitChars.put("ṟ", str13);
            this.translitChars.put("ɲ", str9);
            this.translitChars.put("à", str5);
            this.translitChars.put("ẙ", str6);
            this.translitChars.put("ỳ", str6);
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", str14);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", str5);
            this.translitChars.put("ʑ", str8);
            this.translitChars.put("ẛ", str11);
            this.translitChars.put("ḭ", str12);
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", str8);
            this.translitChars.put("ÿ", str6);
            this.translitChars.put("ǝ", str16);
            str4 = str2;
            this.translitChars.put("ǭ", str4);
            this.translitChars.put("ᴅ", str10);
            str2 = str9;
            str9 = str3;
            this.translitChars.put("ᶅ", str9);
            this.translitChars.put("ù", str14);
            this.translitChars.put("ạ", str5);
            str3 = str10;
            this.translitChars.put("ḅ", "b");
            this.translitChars.put("ụ", str14);
            this.translitChars.put("ằ", str5);
            this.translitChars.put("ᴛ", str15);
            this.translitChars.put("ƴ", str6);
            this.translitChars.put("ⱦ", str15);
            this.translitChars.put("ⱡ", str9);
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", str8);
            this.translitChars.put("ḫ", obj3);
            this.translitChars.put("ⱳ", "w");
            this.translitChars.put("ḵ", "k");
            this.translitChars.put("ờ", str4);
            this.translitChars.put("î", str12);
            str10 = str18;
            this.translitChars.put("ģ", str10);
            this.translitChars.put("ȅ", str16);
            this.translitChars.put("ȧ", str5);
            this.translitChars.put("ẳ", str5);
            obj2 = obj3;
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", str15);
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", "c");
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", str14);
            this.translitChars.put("ỉ", str12);
            this.translitChars.put("ᴚ", str13);
            this.translitChars.put("ś", str11);
            this.translitChars.put("ꝋ", str4);
            this.translitChars.put("ỹ", str6);
            this.translitChars.put("ṡ", str11);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", str5);
            this.translitChars.put("ẗ", str15);
            this.translitChars.put("ĺ", str9);
            this.translitChars.put("ž", str8);
            this.translitChars.put("ᵺ", "th");
            str7 = str3;
            this.translitChars.put("ƌ", str7);
            this.translitChars.put("ș", str11);
            this.translitChars.put("š", str11);
            this.translitChars.put("ᶙ", str14);
            this.translitChars.put("ẽ", str16);
            this.translitChars.put("ẜ", str11);
            this.translitChars.put("ɇ", str16);
            this.translitChars.put("ṷ", str14);
            this.translitChars.put("ố", str4);
            this.translitChars.put("ȿ", str11);
            str3 = str6;
            this.translitChars.put("ᴠ", "v");
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", str4);
            this.translitChars.put("ɛ", str16);
            this.translitChars.put("ǻ", str5);
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", str4);
            this.translitChars.put("ȋ", str12);
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", str7);
            this.translitChars.put("ⱬ", str8);
            this.translitChars.put("ẁ", "w");
            this.translitChars.put("ᶏ", str5);
            this.translitChars.put("ꞇ", str15);
            this.translitChars.put("ğ", str10);
            str6 = str2;
            this.translitChars.put("ɳ", str6);
            this.translitChars.put("ʛ", str10);
            this.translitChars.put("ᴜ", str14);
            this.translitChars.put("ẩ", str5);
            this.translitChars.put("ṅ", str6);
            this.translitChars.put("ɨ", str12);
            this.translitChars.put("ᴙ", str13);
            this.translitChars.put("ǎ", str5);
            this.translitChars.put("ſ", str11);
            this.translitChars.put("ȫ", str4);
            this.translitChars.put("ɿ", str13);
            this.translitChars.put("ƭ", str15);
            this.translitChars.put("ḯ", str12);
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", "v");
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", "m");
            this.translitChars.put("ż", str8);
            this.translitChars.put("ĕ", str16);
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", str4);
            this.translitChars.put("ễ", str16);
            this.translitChars.put("ɬ", str9);
            this.translitChars.put("ị", str12);
            this.translitChars.put("ᵭ", str7);
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", str9);
            this.translitChars.put("ŕ", str13);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", str15);
            this.translitChars.put("ā", str5);
            this.translitChars.put("ḙ", str16);
            this.translitChars.put("ᴑ", str4);
            this.translitChars.put("ç", "c");
            this.translitChars.put("ᶊ", str11);
            this.translitChars.put("ặ", str5);
            this.translitChars.put("ų", str14);
            this.translitChars.put("ả", str5);
            this.translitChars.put("ǥ", str10);
            this.translitChars.put("ꝁ", "k");
            this.translitChars.put("ẕ", str8);
            this.translitChars.put("ŝ", str11);
            this.translitChars.put("ḕ", str16);
            this.translitChars.put("ɠ", str10);
            this.translitChars.put("ꝉ", str9);
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", str4);
            this.translitChars.put("ę", str16);
            this.translitChars.put("ổ", str4);
            this.translitChars.put("ƫ", str15);
            this.translitChars.put("ǫ", str4);
            this.translitChars.put("i̇", str12);
            str6 = str2;
            this.translitChars.put("ṇ", str6);
            this.translitChars.put("ć", "c");
            this.translitChars.put("ᵷ", str10);
            this.translitChars.put("ẅ", "w");
            this.translitChars.put("ḑ", str7);
            this.translitChars.put("ḹ", str9);
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", str13);
            this.translitChars.put("ļ", str9);
            this.translitChars.put("ȑ", str13);
            this.translitChars.put("ȭ", str4);
            this.translitChars.put("ᵰ", str6);
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", str9);
            this.translitChars.put("ä", str5);
            this.translitChars.put("ƥ", "p");
            this.translitChars.put("ỏ", str4);
            this.translitChars.put("į", str12);
            this.translitChars.put("ȓ", str13);
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", str10);
            this.translitChars.put("ṻ", str14);
            this.translitChars.put("ō", str4);
            this.translitChars.put("ľ", str9);
            this.translitChars.put("ẃ", "w");
            this.translitChars.put("ț", str15);
            this.translitChars.put("ń", str6);
            this.translitChars.put("ɍ", str13);
            this.translitChars.put("ȃ", str5);
            this.translitChars.put("ü", str14);
            this.translitChars.put("ꞁ", str9);
            this.translitChars.put("ᴐ", str4);
            this.translitChars.put("ớ", str4);
            this.translitChars.put("ᴃ", "b");
            this.translitChars.put("ɹ", str13);
            this.translitChars.put("ᵲ", str13);
            str7 = str3;
            this.translitChars.put("ʏ", str7);
            this.translitChars.put("ᵮ", "f");
            Object obj4 = obj2;
            this.translitChars.put("ⱨ", obj4);
            this.translitChars.put("ŏ", str4);
            this.translitChars.put("ú", str14);
            this.translitChars.put("ṛ", str13);
            this.translitChars.put("ʮ", obj4);
            this.translitChars.put("ó", str4);
            this.translitChars.put("ů", str14);
            this.translitChars.put("ỡ", str4);
            str18 = str11;
            this.translitChars.put("ṕ", "p");
            this.translitChars.put("ᶖ", str12);
            this.translitChars.put("ự", str14);
            this.translitChars.put("ã", str5);
            this.translitChars.put("ᵢ", str12);
            this.translitChars.put("ṱ", str15);
            this.translitChars.put("ể", str16);
            this.translitChars.put("ử", str14);
            this.translitChars.put("í", str12);
            this.translitChars.put("ɔ", str4);
            this.translitChars.put("ɺ", str13);
            this.translitChars.put("ɢ", str10);
            this.translitChars.put("ř", str13);
            this.translitChars.put("ẖ", obj4);
            this.translitChars.put("ű", str14);
            this.translitChars.put("ȍ", str4);
            this.translitChars.put("ḻ", str9);
            this.translitChars.put("ḣ", obj4);
            this.translitChars.put("ȶ", str15);
            this.translitChars.put("ņ", str6);
            this.translitChars.put("ᶒ", str16);
            this.translitChars.put("ì", str12);
            this.translitChars.put("ẉ", "w");
            this.translitChars.put("ē", str16);
            this.translitChars.put("ᴇ", str16);
            this.translitChars.put("ł", str9);
            this.translitChars.put("ộ", str4);
            this.translitChars.put("ɭ", str9);
            this.translitChars.put("ẏ", str7);
            this.translitChars.put("ᴊ", "j");
            this.translitChars.put("ḱ", "k");
            this.translitChars.put("ṿ", "v");
            this.translitChars.put("ȩ", str16);
            this.translitChars.put("â", str5);
            str6 = str18;
            this.translitChars.put("ş", str6);
            this.translitChars.put("ŗ", str13);
            this.translitChars.put("ʋ", "v");
            this.translitChars.put("ₐ", str5);
            this.translitChars.put("ↄ", "c");
            this.translitChars.put("ᶓ", str16);
            this.translitChars.put("ɰ", "m");
            this.translitChars.put("ᴡ", "w");
            this.translitChars.put("ȏ", str4);
            this.translitChars.put("č", "c");
            this.translitChars.put("ǵ", str10);
            this.translitChars.put("ĉ", "c");
            this.translitChars.put("ᶗ", str4);
            this.translitChars.put("ꝃ", "k");
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", str4);
            this.translitChars.put("ꜱ", str6);
            this.translitChars.put("ṓ", str4);
            this.translitChars.put("ȟ", obj4);
            this.translitChars.put("ő", str4);
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", str16);
        }
        StringBuilder stringBuilder = new StringBuilder(str.length());
        int length = str.length();
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            int i3 = i2 + 1;
            str5 = str.substring(i2, i3);
            if (z2) {
                str4 = str5.toLowerCase();
                i2 = str5.equals(str4) ^ 1;
            } else {
                String str19 = str5;
                i2 = i;
                str4 = str19;
            }
            str8 = (String) this.translitChars.get(str4);
            if (str8 == null && z) {
                str8 = (String) this.ruTranslitChars.get(str4);
            }
            if (str8 != null) {
                if (z2 && i2 != 0) {
                    if (str8.length() > 1) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str8.substring(0, 1).toUpperCase());
                        stringBuilder2.append(str8.substring(1));
                        str8 = stringBuilder2.toString();
                    } else {
                        str8 = str8.toUpperCase();
                    }
                }
                stringBuilder.append(str8);
            } else {
                if (z2) {
                    char charAt = str4.charAt(0);
                    if ((charAt < 'a' || charAt > 'z' || charAt < '0' || charAt > '9') && charAt != ' ' && charAt != '\'' && charAt != ',' && charAt != '.' && charAt != '&' && charAt != '-' && charAt != '/') {
                        return null;
                    }
                    if (i2 != 0) {
                        str4 = str4.toUpperCase();
                    }
                }
                stringBuilder.append(str4);
            }
            i = i2;
            i2 = i3;
        }
        return stringBuilder.toString();
    }

    public static String addNbsp(String str) {
        return str.replace(' ', 160);
    }
}
