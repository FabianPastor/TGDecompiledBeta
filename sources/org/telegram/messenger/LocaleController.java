package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
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
import java.util.Map;
import java.util.TimeZone;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$LangPackString;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_langPackDifference;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_langPackString;
import org.telegram.tgnet.TLRPC$TL_langPackStringDeleted;
import org.telegram.tgnet.TLRPC$TL_langPackStringPluralized;
import org.telegram.tgnet.TLRPC$TL_langpack_getDifference;
import org.telegram.tgnet.TLRPC$TL_langpack_getLangPack;
import org.telegram.tgnet.TLRPC$TL_langpack_getLanguages;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC$TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC$TL_userStatusRecently;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;

public class LocaleController {
    private static volatile LocaleController Instance = null;
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    private static char[] defaultNumbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private static char[][] otherNumbers = {new char[]{1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641}, new char[]{1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785}, new char[]{2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415}, new char[]{2790, 2791, 2792, 2793, 2794, 2795, 2796, 2797, 2798, 2799}, new char[]{2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671}, new char[]{2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543}, new char[]{3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311}, new char[]{2918, 2919, 2920, 2921, 2922, 2923, 2924, 2925, 2926, 2927}, new char[]{3430, 3431, 3432, 3433, 3434, 3435, 3436, 3437, 3438, 3439}, new char[]{3046, 3047, 3048, 3049, 3050, 3051, 3052, 3053, 3054, 3055}, new char[]{3174, 3175, 3176, 3177, 3178, 3179, 3180, 3181, 3182, 3183}, new char[]{4160, 4161, 4162, 4163, 4164, 4165, 4166, 4167, 4168, 4169}, new char[]{3872, 3873, 3874, 3875, 3876, 3877, 3878, 3879, 3880, 3881}, new char[]{6160, 6161, 6162, 6163, 6164, 6165, 6166, 6167, 6168, 6169}, new char[]{6112, 6113, 6114, 6115, 6116, 6117, 6118, 6119, 6120, 6121}, new char[]{3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673}, new char[]{3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801}, new char[]{43472, 43473, 43474, 43475, 43476, 43477, 43478, 43479, 43480, 43481}};
    private static HashMap<Integer, String> resourcesCacheMap = new HashMap<>();
    private static Boolean useImperialSystemType;
    private HashMap<String, PluralRules> allRules = new HashMap<>();
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
    public FastDateFormat formatterMonthYear;
    public FastDateFormat formatterScheduleDay;
    public FastDateFormat[] formatterScheduleSend = new FastDateFormat[15];
    public FastDateFormat formatterScheduleYear;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterWeekLong;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public ArrayList<LocaleInfo> languages;
    public HashMap<String, LocaleInfo> languagesDict;
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages;
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages;
    public HashMap<String, LocaleInfo> remoteLanguagesDict;
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    public ArrayList<LocaleInfo> unofficialLanguages;

    public static abstract class PluralRules {
        /* access modifiers changed from: package-private */
        public abstract int quantityForNumber(int i);
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return (i < 2 || i > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int i) {
            return (i < 0 || i >= 2) ? 0 : 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return i == 1 ? 2 : 0;
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

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int i) {
            if (i < 0 || i > 1) {
                return (i < 2 || i > 10) ? 0 : 8;
            }
            return 2;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return i == 2 ? 4 : 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
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

    private class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new LocaleController$TimeZoneChangedReceiver$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0() {
            if (!LocaleController.this.formatterDayMonth.getTimeZone().equals(TimeZone.getDefault())) {
                LocaleController.getInstance().recreateFormatters();
            }
        }
    }

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
            TextUtils.isEmpty(this.pluralLangCode);
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version + "|" + str + "|" + this.pluralLangCode + "|" + (this.isRtl ? 1 : 0) + "|" + this.baseVersion + "|" + this.serverIndex;
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
            if (isRemote()) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed, "remote_" + this.shortName + ".xml");
            } else if (isUnofficial()) {
                File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed2, "unofficial_" + this.shortName + ".xml");
            } else if (!TextUtils.isEmpty(this.pathToFile)) {
                return new File(this.pathToFile);
            } else {
                return null;
            }
        }

        public File getPathToBaseFile() {
            if (!isUnofficial()) {
                return null;
            }
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            return new File(filesDirFixed, "unofficial_base_" + this.shortName + ".xml");
        }

        public String getKey() {
            if (this.pathToFile != null && !isRemote() && !isUnofficial()) {
                return "local_" + this.shortName;
            } else if (!isUnofficial()) {
                return this.shortName;
            } else {
                return "unofficial_" + this.shortName;
            }
        }

        public boolean hasBaseLang() {
            return isUnofficial() && !TextUtils.isEmpty(this.baseLangCode) && !this.baseLangCode.equals(this.shortName);
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isUnofficial() {
            return "unofficial".equals(this.pathToFile);
        }

        public boolean isLocal() {
            return !TextUtils.isEmpty(this.pathToFile) && !isRemote() && !isUnofficial();
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
        LocaleInfo localeInfo;
        boolean z = false;
        this.changingConfiguration = false;
        this.languages = new ArrayList<>();
        this.unofficialLanguages = new ArrayList<>();
        this.remoteLanguages = new ArrayList<>();
        this.remoteLanguagesDict = new HashMap<>();
        this.languagesDict = new HashMap<>();
        this.otherLanguages = new ArrayList<>();
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        addRules(new String[]{"cs", "sk"}, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"ru", "uk", "be"}, new PluralRules_Balkan());
        addRules(new String[]{"sr", "hr", "bs", "sh"}, new PluralRules_Serbian());
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
        localeInfo2.pluralLangCode = "en";
        localeInfo2.shortName = "en";
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Italiano";
        localeInfo3.nameEnglish = "Italian";
        localeInfo3.pluralLangCode = "it";
        localeInfo3.shortName = "it";
        localeInfo3.pathToFile = null;
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Español";
        localeInfo4.nameEnglish = "Spanish";
        localeInfo4.pluralLangCode = "es";
        localeInfo4.shortName = "es";
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Deutsch";
        localeInfo5.nameEnglish = "German";
        localeInfo5.pluralLangCode = "de";
        localeInfo5.shortName = "de";
        localeInfo5.pathToFile = null;
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "Nederlands";
        localeInfo6.nameEnglish = "Dutch";
        localeInfo6.pluralLangCode = "nl";
        localeInfo6.shortName = "nl";
        localeInfo6.pathToFile = null;
        localeInfo6.builtIn = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "العربية";
        localeInfo7.nameEnglish = "Arabic";
        localeInfo7.pluralLangCode = "ar";
        localeInfo7.shortName = "ar";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        localeInfo7.isRtl = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "Português (Brasil)";
        localeInfo8.nameEnglish = "Portuguese (Brazil)";
        localeInfo8.pluralLangCode = "pt_br";
        localeInfo8.shortName = "pt_br";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        LocaleInfo localeInfo9 = new LocaleInfo();
        localeInfo9.name = "한국어";
        localeInfo9.nameEnglish = "Korean";
        localeInfo9.pluralLangCode = "ko";
        localeInfo9.shortName = "ko";
        localeInfo9.pathToFile = null;
        localeInfo9.builtIn = true;
        this.languages.add(localeInfo9);
        this.languagesDict.put(localeInfo9.shortName, localeInfo9);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda1(this));
        }
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            LocaleInfo localeInfo10 = this.otherLanguages.get(i);
            this.languages.add(localeInfo10);
            this.languagesDict.put(localeInfo10.getKey(), localeInfo10);
        }
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            LocaleInfo localeInfo11 = this.remoteLanguages.get(i2);
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo11.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo11.pathToFile;
                languageFromDict.version = localeInfo11.version;
                languageFromDict.baseVersion = localeInfo11.baseVersion;
                languageFromDict.serverIndex = localeInfo11.serverIndex;
                this.remoteLanguages.set(i2, languageFromDict);
            } else {
                this.languages.add(localeInfo11);
                this.languagesDict.put(localeInfo11.getKey(), localeInfo11);
            }
        }
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            LocaleInfo localeInfo12 = this.unofficialLanguages.get(i3);
            LocaleInfo languageFromDict2 = getLanguageFromDict(localeInfo12.getKey());
            if (languageFromDict2 != null) {
                languageFromDict2.pathToFile = localeInfo12.pathToFile;
                languageFromDict2.version = localeInfo12.version;
                languageFromDict2.baseVersion = localeInfo12.baseVersion;
                languageFromDict2.serverIndex = localeInfo12.serverIndex;
                this.unofficialLanguages.set(i3, languageFromDict2);
            } else {
                this.languagesDict.put(localeInfo12.getKey(), localeInfo12);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = MessagesController.getGlobalMainSettings().getString("language", (String) null);
            if (string != null) {
                localeInfo = getLanguageFromDict(string);
                if (localeInfo != null) {
                    z = true;
                }
            } else {
                localeInfo = null;
            }
            if (localeInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                localeInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo == null && (localeInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                localeInfo = getLanguageFromDict("en");
            }
            applyLanguage(localeInfo, z, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public static String getLanguageFlag(String str) {
        if (str.length() != 2 || str.equals("YL")) {
            return null;
        }
        if (str.equals("XG")) {
            return "🛰";
        }
        if (str.equals("XV")) {
            return "🌍";
        }
        char[] charArray = str.toCharArray();
        return new String(new char[]{CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[0] + 61861), CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[1] + 61861)});
    }

    public LocaleInfo getLanguageFromDict(String str) {
        if (str == null) {
            return null;
        }
        return this.languagesDict.get(str.toLowerCase().replace("-", "_"));
    }

    public LocaleInfo getBuiltinLanguageByPlural(String str) {
        String str2;
        for (LocaleInfo next : this.languagesDict.values()) {
            String str3 = next.pathToFile;
            if (str3 != null && str3.equals("remote") && (str2 = next.pluralLangCode) != null && str2.equals(str)) {
                return next;
            }
        }
        return null;
    }

    private void addRules(String[] strArr, PluralRules pluralRules) {
        for (String put : strArr) {
            this.allRules.put(put, pluralRules);
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void reloadCurrentRemoteLocale(int i, String str, boolean z) {
        if (str != null) {
            str = str.replace("-", "_");
        }
        if (str != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!str.equals(localeInfo.shortName) && !str.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, str, z, i);
    }

    public void checkUpdateForCurrentRemoteLocale(int i, int i2, int i3) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null) {
            return;
        }
        if (localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
            if (this.currentLocaleInfo.hasBaseLang()) {
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                if (localeInfo2.baseVersion < i3) {
                    applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, i);
                }
            }
            LocaleInfo localeInfo3 = this.currentLocaleInfo;
            if (localeInfo3.version < i2) {
                applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, i);
            }
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('_');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale2 = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale2 == null) {
            return "en";
        }
        String language = systemDefaultLocale2.getLanguage();
        String country = systemDefaultLocale2.getCountry();
        String variant = systemDefaultLocale2.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        if (localeInfo != null) {
            return localeInfo.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleAlias(String str) {
        if (str == null) {
            return null;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 3325:
                if (str.equals("he")) {
                    c = 0;
                    break;
                }
                break;
            case 3355:
                if (str.equals("id")) {
                    c = 1;
                    break;
                }
                break;
            case 3365:
                if (str.equals("in")) {
                    c = 2;
                    break;
                }
                break;
            case 3374:
                if (str.equals("iw")) {
                    c = 3;
                    break;
                }
                break;
            case 3391:
                if (str.equals("ji")) {
                    c = 4;
                    break;
                }
                break;
            case 3404:
                if (str.equals("jv")) {
                    c = 5;
                    break;
                }
                break;
            case 3405:
                if (str.equals("jw")) {
                    c = 6;
                    break;
                }
                break;
            case 3508:
                if (str.equals("nb")) {
                    c = 7;
                    break;
                }
                break;
            case 3521:
                if (str.equals("no")) {
                    c = 8;
                    break;
                }
                break;
            case 3704:
                if (str.equals("tl")) {
                    c = 9;
                    break;
                }
                break;
            case 3856:
                if (str.equals("yi")) {
                    c = 10;
                    break;
                }
                break;
            case 101385:
                if (str.equals("fil")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "iw";
            case 1:
                return "in";
            case 2:
                return "id";
            case 3:
                return "he";
            case 4:
                return "yi";
            case 5:
                return "jw";
            case 6:
                return "jv";
            case 7:
                return "no";
            case 8:
                return "nb";
            case 9:
                return "fil";
            case 10:
                return "ji";
            case 11:
                return "tl";
            default:
                return null;
        }
    }

    public boolean applyLanguageFile(File file, int i) {
        try {
            HashMap<String, String> localeFileStrings = getLocaleFileStrings(file);
            String str = localeFileStrings.get("LanguageName");
            String str2 = localeFileStrings.get("LanguageNameInEnglish");
            String str3 = localeFileStrings.get("LanguageCode");
            if (str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str3 != null && str3.length() > 0 && !str.contains("&")) {
                if (!str.contains("|")) {
                    if (!str2.contains("&")) {
                        if (!str2.contains("|")) {
                            if (!str3.contains("&") && !str3.contains("|") && !str3.contains("/")) {
                                if (!str3.contains("\\")) {
                                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                                    File file2 = new File(filesDirFixed, str3 + ".xml");
                                    if (!AndroidUtilities.copyFile(file, file2)) {
                                        return false;
                                    }
                                    LocaleInfo languageFromDict = getLanguageFromDict("local_" + str3.toLowerCase());
                                    if (languageFromDict == null) {
                                        languageFromDict = new LocaleInfo();
                                        languageFromDict.name = str;
                                        languageFromDict.nameEnglish = str2;
                                        String lowerCase = str3.toLowerCase();
                                        languageFromDict.shortName = lowerCase;
                                        languageFromDict.pluralLangCode = lowerCase;
                                        languageFromDict.pathToFile = file2.getAbsolutePath();
                                        this.languages.add(languageFromDict);
                                        this.languagesDict.put(languageFromDict.getKey(), languageFromDict);
                                        this.otherLanguages.add(languageFromDict);
                                        saveOtherLanguages();
                                    }
                                    this.localeValues = localeFileStrings;
                                    applyLanguage(languageFromDict, true, false, true, false, i);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return false;
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            String saveString = this.otherLanguages.get(i).getSaveString();
            if (saveString != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString);
            }
        }
        edit.putString("locales", sb.toString());
        sb.setLength(0);
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            String saveString2 = this.remoteLanguages.get(i2).getSaveString();
            if (saveString2 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString2);
            }
        }
        edit.putString("remote", sb.toString());
        sb.setLength(0);
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            String saveString3 = this.unofficialLanguages.get(i3).getSaveString();
            if (saveString3 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString3);
            }
        }
        edit.putString("unofficial", sb.toString());
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
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String string = sharedPreferences.getString("locales", (String) null);
        if (!TextUtils.isEmpty(string)) {
            for (String createWithString : string.split("&")) {
                LocaleInfo createWithString2 = LocaleInfo.createWithString(createWithString);
                if (createWithString2 != null) {
                    this.otherLanguages.add(createWithString2);
                }
            }
        }
        String string2 = sharedPreferences.getString("remote", (String) null);
        if (!TextUtils.isEmpty(string2)) {
            for (String createWithString3 : string2.split("&")) {
                LocaleInfo createWithString4 = LocaleInfo.createWithString(createWithString3);
                createWithString4.shortName = createWithString4.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(createWithString4.getKey())) {
                    this.remoteLanguages.add(createWithString4);
                    this.remoteLanguagesDict.put(createWithString4.getKey(), createWithString4);
                }
            }
        }
        String string3 = sharedPreferences.getString("unofficial", (String) null);
        if (!TextUtils.isEmpty(string3)) {
            for (String createWithString5 : string3.split("&")) {
                LocaleInfo createWithString6 = LocaleInfo.createWithString(createWithString5);
                if (createWithString6 != null) {
                    createWithString6.shortName = createWithString6.shortName.replace("-", "_");
                    this.unofficialLanguages.add(createWithString6);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x00d7 A[SYNTHETIC, Splitter:B:59:0x00d7] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00e7 A[SYNTHETIC, Splitter:B:66:0x00e7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, java.lang.String> getLocaleFileStrings(java.io.File r13, boolean r14) {
        /*
            r12 = this;
            r0 = 0
            r12.reloadLastFile = r0
            r1 = 1
            r2 = 0
            boolean r3 = r13.exists()     // Catch:{ Exception -> 0x00cf }
            if (r3 != 0) goto L_0x0011
            java.util.HashMap r13 = new java.util.HashMap     // Catch:{ Exception -> 0x00cf }
            r13.<init>()     // Catch:{ Exception -> 0x00cf }
            return r13
        L_0x0011:
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x00cf }
            r3.<init>()     // Catch:{ Exception -> 0x00cf }
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ Exception -> 0x00cf }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00cf }
            r5.<init>(r13)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r13 = "UTF-8"
            r4.setInput(r5, r13)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            int r13 = r4.getEventType()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x002b:
            if (r13 == r1) goto L_0x00be
            r9 = 2
            if (r13 != r9) goto L_0x003f
            java.lang.String r7 = r4.getName()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            int r13 = r4.getAttributeCount()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r13 <= 0) goto L_0x0098
            java.lang.String r6 = r4.getAttributeValue(r0)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            goto L_0x0098
        L_0x003f:
            r9 = 4
            if (r13 != r9) goto L_0x0092
            if (r6 == 0) goto L_0x0098
            java.lang.String r8 = r4.getText()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r8 == 0) goto L_0x0098
            java.lang.String r13 = r8.trim()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r8 = "&lt;"
            java.lang.String r9 = "<"
            if (r14 == 0) goto L_0x0071
            java.lang.String r13 = r13.replace(r9, r8)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r8 = ">"
            java.lang.String r9 = "&gt;"
            java.lang.String r13 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r8 = "'"
            java.lang.String r9 = "\\'"
            java.lang.String r13 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r8 = "& "
            java.lang.String r9 = "&amp; "
            java.lang.String r8 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            goto L_0x0098
        L_0x0071:
            java.lang.String r10 = "\\n"
            java.lang.String r11 = "\n"
            java.lang.String r13 = r13.replace(r10, r11)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r10 = "\\"
            java.lang.String r11 = ""
            java.lang.String r13 = r13.replace(r10, r11)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            java.lang.String r8 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            boolean r9 = r12.reloadLastFile     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r9 != 0) goto L_0x0098
            boolean r13 = r8.equals(r13)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r13 != 0) goto L_0x0098
            r12.reloadLastFile = r1     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            goto L_0x0098
        L_0x0092:
            r9 = 3
            if (r13 != r9) goto L_0x0098
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x0098:
            if (r7 == 0) goto L_0x00b8
            java.lang.String r13 = "string"
            boolean r13 = r7.equals(r13)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r13 == 0) goto L_0x00b8
            if (r8 == 0) goto L_0x00b8
            if (r6 == 0) goto L_0x00b8
            int r13 = r8.length()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r13 == 0) goto L_0x00b8
            int r13 = r6.length()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            if (r13 == 0) goto L_0x00b8
            r3.put(r6, r8)     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x00b8:
            int r13 = r4.next()     // Catch:{ Exception -> 0x00ca, all -> 0x00c7 }
            goto L_0x002b
        L_0x00be:
            r5.close()     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c6
        L_0x00c2:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x00c6:
            return r3
        L_0x00c7:
            r13 = move-exception
            r2 = r5
            goto L_0x00e5
        L_0x00ca:
            r13 = move-exception
            r2 = r5
            goto L_0x00d0
        L_0x00cd:
            r13 = move-exception
            goto L_0x00e5
        L_0x00cf:
            r13 = move-exception
        L_0x00d0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ all -> 0x00cd }
            r12.reloadLastFile = r1     // Catch:{ all -> 0x00cd }
            if (r2 == 0) goto L_0x00df
            r2.close()     // Catch:{ Exception -> 0x00db }
            goto L_0x00df
        L_0x00db:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x00df:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            return r13
        L_0x00e5:
            if (r2 == 0) goto L_0x00ef
            r2.close()     // Catch:{ Exception -> 0x00eb }
            goto L_0x00ef
        L_0x00eb:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x00ef:
            goto L_0x00f1
        L_0x00f0:
            throw r13
        L_0x00f1:
            goto L_0x00f0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getLocaleFileStrings(java.io.File, boolean):java.util.HashMap");
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        applyLanguage(localeInfo, z, z2, false, false, i);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, boolean z3, boolean z4, int i) {
        boolean z5;
        String[] strArr;
        Locale locale;
        LocaleInfo localeInfo2 = localeInfo;
        boolean z6 = z4;
        int i2 = i;
        if (localeInfo2 != null) {
            boolean hasBaseLang = localeInfo.hasBaseLang();
            File pathToFile = localeInfo.getPathToFile();
            File pathToBaseFile = localeInfo.getPathToBaseFile();
            if (!z2) {
                ConnectionsManager.setLangCode(localeInfo.getLangCode());
            }
            if (getLanguageFromDict(localeInfo.getKey()) == null) {
                if (localeInfo.isRemote()) {
                    this.remoteLanguages.add(localeInfo2);
                    this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo2);
                    this.languages.add(localeInfo2);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo2);
                    saveOtherLanguages();
                } else if (localeInfo.isUnofficial()) {
                    this.unofficialLanguages.add(localeInfo2);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo2);
                    saveOtherLanguages();
                }
            }
            if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (z6 || !pathToFile.exists() || (hasBaseLang && !pathToBaseFile.exists()))) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
                }
                if (z2) {
                    AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda4(this, localeInfo2, i2));
                } else {
                    applyRemoteLanguage(localeInfo2, (String) null, true, i2);
                }
                z5 = true;
            } else {
                z5 = false;
            }
            try {
                if (!TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    strArr = localeInfo2.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo2.baseLangCode)) {
                    strArr = localeInfo2.baseLangCode.split("_");
                } else {
                    strArr = localeInfo2.shortName.split("_");
                }
                if (strArr.length == 1) {
                    locale = new Locale(strArr[0]);
                } else {
                    locale = new Locale(strArr[0], strArr[1]);
                }
                if (z) {
                    this.languageOverride = localeInfo2.shortName;
                    SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                    edit.putString("language", localeInfo.getKey());
                    edit.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!z3) {
                    HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    this.localeValues = localeFileStrings;
                    if (hasBaseLang) {
                        localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                }
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo2;
                if (!TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(strArr[0]);
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        PluralRules pluralRules2 = this.allRules.get(this.currentLocale.getLanguage());
                        this.currentPluralRules = pluralRules2;
                        if (pluralRules2 == null) {
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
                        AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda3(this, i2, z6));
                    } else {
                        reloadCurrentRemoteLocale(i2, (String) null, z6);
                    }
                    this.reloadLastFile = false;
                }
                if (!z5) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyLanguage$2(LocaleInfo localeInfo, int i) {
        applyRemoteLanguage(localeInfo, (String) null, true, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyLanguage$3(int i, boolean z) {
        reloadCurrentRemoteLocale(i, (String) null, z);
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", NUM) : localeInfo.name;
    }

    private String getStringInternal(String str, int i) {
        return getStringInternal(str, (String) null, i);
    }

    private String getStringInternal(String str, String str2, int i) {
        String str3 = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(str) : null;
        if (str3 == null) {
            if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                str3 = this.localeValues.get(str2);
            }
            if (str3 == null) {
                try {
                    str3 = ApplicationLoader.applicationContext.getString(i);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
        if (str3 != null) {
            return str3;
        }
        return "LOC_ERR:" + str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000e, code lost:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext.getResources().getIdentifier(r4, "string", org.telegram.messenger.ApplicationLoader.applicationContext.getPackageName());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getServerString(java.lang.String r4) {
        /*
            org.telegram.messenger.LocaleController r0 = getInstance()
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r0.localeValues
            java.lang.Object r0 = r0.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 != 0) goto L_0x0028
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r1 = r1.getResources()
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r2 = r2.getPackageName()
            java.lang.String r3 = "string"
            int r4 = r1.getIdentifier(r4, r3, r2)
            if (r4 == 0) goto L_0x0028
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r0 = r0.getString(r4)
        L_0x0028:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getServerString(java.lang.String):java.lang.String");
    }

    public static String getString(int i) {
        String str = resourcesCacheMap.get(Integer.valueOf(i));
        if (str == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(i);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(i);
            hashMap.put(valueOf, resourceEntryName);
            str = resourceEntryName;
        }
        return getString(str, i);
    }

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    public static String getString(String str, String str2, int i) {
        return getInstance().getStringInternal(str, str2, i);
    }

    public static String getString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "LOC_ERR:" + str;
        }
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName());
        if (identifier != 0) {
            return getString(str, identifier);
        }
        return getServerString(str);
    }

    public static String getPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return getString(str2, str + "_other", ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return formatString(str2, str + "_other", ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(i));
    }

    public static String formatPluralStringComma(String str, int i) {
        if (str != null) {
            try {
                if (str.length() != 0) {
                    if (getInstance().currentPluralRules != null) {
                        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
                        StringBuilder sb = new StringBuilder(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i)}));
                        for (int length = sb.length() - 3; length > 0; length -= 3) {
                            sb.insert(length, ',');
                        }
                        String str3 = null;
                        String str4 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str2) : null;
                        if (str4 == null) {
                            if (BuildVars.USE_CLOUD_STRINGS) {
                                str3 = getInstance().localeValues.get(str + "_other");
                            }
                            str4 = str3;
                        }
                        if (str4 == null) {
                            str4 = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
                        }
                        String replace = str4.replace("%1$d", "%1$s");
                        if (getInstance().currentLocale != null) {
                            return String.format(getInstance().currentLocale, replace, new Object[]{sb});
                        }
                        return String.format(replace, new Object[]{sb});
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return "LOC_ERR: " + str;
            }
        }
        return "LOC_ERR:" + str;
    }

    public static String formatString(String str, int i, Object... objArr) {
        return formatString(str, (String) null, i, objArr);
    }

    public static String formatString(String str, String str2, int i, Object... objArr) {
        try {
            String str3 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str) : null;
            if (str3 == null) {
                if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                    str3 = getInstance().localeValues.get(str2);
                }
                if (str3 == null) {
                    str3 = ApplicationLoader.applicationContext.getString(i);
                }
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str3, objArr);
            }
            return String.format(str3, objArr);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatTTLString(int i) {
        if (i < 60) {
            return formatPluralString("Seconds", i);
        }
        if (i < 3600) {
            return formatPluralString("Minutes", i / 60);
        }
        if (i < 86400) {
            return formatPluralString("Hours", (i / 60) / 60);
        }
        if (i < 604800) {
            return formatPluralString("Days", ((i / 60) / 60) / 24);
        }
        if (i >= 2592000 && i <= 2678400) {
            return formatPluralString("Months", (((i / 60) / 60) / 24) / 30);
        }
        int i2 = ((i / 60) / 60) / 24;
        if (i % 7 == 0) {
            return formatPluralString("Weeks", i2 / 7);
        }
        return String.format("%s %s", new Object[]{formatPluralString("Weeks", i2 / 7), formatPluralString("Days", i2 % 7)});
    }

    public static String fixNumbers(CharSequence charSequence) {
        StringBuilder sb = new StringBuilder(charSequence);
        int length = sb.length();
        for (int i = 0; i < length; i++) {
            char charAt = sb.charAt(i);
            if (!((charAt >= '0' && charAt <= '9') || charAt == '.' || charAt == ',')) {
                int i2 = 0;
                while (i2 < otherNumbers.length) {
                    int i3 = 0;
                    while (true) {
                        char[][] cArr = otherNumbers;
                        if (i3 >= cArr[i2].length) {
                            break;
                        } else if (charAt == cArr[i2][i3]) {
                            sb.setCharAt(i, defaultNumbers[i3]);
                            i2 = otherNumbers.length;
                            break;
                        } else {
                            i3++;
                        }
                    }
                    i2++;
                }
            }
        }
        return sb.toString();
    }

    public String formatCurrencyString(long j, String str) {
        return formatCurrencyString(j, true, true, false, str);
    }

    public String formatCurrencyString(long j, boolean z, boolean z2, boolean z3, String str) {
        double d;
        int length;
        String upperCase = str.toUpperCase();
        boolean z4 = j < 0;
        long abs = Math.abs(j);
        Currency instance = Currency.getInstance(upperCase);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = 8;
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = 9;
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = 10;
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = 12;
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = 13;
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
        }
        String str2 = " %.2f";
        String str3 = " %.0f";
        switch (c) {
            case 0:
            case 8:
            case 11:
            case 15:
            case 16:
            case 19:
            case 22:
                double d2 = (double) abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 10:
            case 12:
            case 13:
            case 14:
            case 17:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                d = (double) abs;
                str2 = str3;
                break;
            case 3:
                double d3 = (double) abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case 9:
                double d4 = (double) (((float) abs) / 100.0f);
                if (z && abs % 100 == 0) {
                    str2 = str3;
                }
                d = d4;
                break;
            case 18:
                double d5 = (double) abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = (double) abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                break;
        }
        if (z2) {
            str3 = str2;
        }
        String str4 = "-";
        if (instance != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
            currencyInstance.setCurrency(instance);
            if (z3) {
                currencyInstance.setGroupingUsed(false);
            }
            if (!z2 || (z && upperCase.equals("IRR"))) {
                currencyInstance.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!z4) {
                str4 = "";
            }
            sb.append(str4);
            sb.append(currencyInstance.format(d));
            String sb2 = sb.toString();
            int indexOf = sb2.indexOf(upperCase);
            if (indexOf < 0 || (length = indexOf + upperCase.length()) >= sb2.length() || sb2.charAt(length) == ' ') {
                return sb2;
            }
            return sb2.substring(0, length) + " " + sb2.substring(length);
        }
        StringBuilder sb3 = new StringBuilder();
        if (!z4) {
            str4 = "";
        }
        sb3.append(str4);
        Locale locale2 = Locale.US;
        sb3.append(String.format(locale2, upperCase + str3, new Object[]{Double.valueOf(d)}));
        return sb3.toString();
    }

    public static int getCurrencyExpDivider(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 65726:
                if (str.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (str.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (str.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (str.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (str.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (str.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (str.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (str.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (str.equals("IQD")) {
                    c = 8;
                    break;
                }
                break;
            case 72801:
                if (str.equals("ISK")) {
                    c = 9;
                    break;
                }
                break;
            case 73631:
                if (str.equals("JOD")) {
                    c = 10;
                    break;
                }
                break;
            case 73683:
                if (str.equals("JPY")) {
                    c = 11;
                    break;
                }
                break;
            case 74532:
                if (str.equals("KMF")) {
                    c = 12;
                    break;
                }
                break;
            case 74704:
                if (str.equals("KRW")) {
                    c = 13;
                    break;
                }
                break;
            case 74840:
                if (str.equals("KWD")) {
                    c = 14;
                    break;
                }
                break;
            case 75863:
                if (str.equals("LYD")) {
                    c = 15;
                    break;
                }
                break;
            case 76263:
                if (str.equals("MGA")) {
                    c = 16;
                    break;
                }
                break;
            case 76618:
                if (str.equals("MRO")) {
                    c = 17;
                    break;
                }
                break;
            case 78388:
                if (str.equals("OMR")) {
                    c = 18;
                    break;
                }
                break;
            case 79710:
                if (str.equals("PYG")) {
                    c = 19;
                    break;
                }
                break;
            case 81569:
                if (str.equals("RWF")) {
                    c = 20;
                    break;
                }
                break;
            case 83210:
                if (str.equals("TND")) {
                    c = 21;
                    break;
                }
                break;
            case 83974:
                if (str.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (str.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (str.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (str.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (str.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (str.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (str.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 8:
            case 10:
            case 14:
            case 15:
            case 18:
            case 21:
                return 1000;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 11:
            case 12:
            case 13:
            case 16:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                return 1;
            case 3:
                return 10000;
            case 17:
                return 10;
            default:
                return 100;
        }
    }

    public String formatCurrencyDecimalString(long j, String str, boolean z) {
        double d;
        String upperCase = str.toUpperCase();
        long abs = Math.abs(j);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = 8;
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = 9;
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = 10;
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = 12;
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = 13;
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
        }
        String str2 = " %.2f";
        switch (c) {
            case 0:
            case 8:
            case 11:
            case 15:
            case 16:
            case 19:
            case 22:
                double d2 = (double) abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 10:
            case 12:
            case 13:
            case 14:
            case 17:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                d = (double) abs;
                str2 = " %.0f";
                break;
            case 3:
                double d3 = (double) abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case 9:
                double d4 = (double) (((float) abs) / 100.0f);
                if (abs % 100 == 0) {
                    str2 = " %.0f";
                }
                d = d4;
                break;
            case 18:
                double d5 = (double) abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = (double) abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                break;
        }
        Locale locale = Locale.US;
        if (!z) {
            upperCase = "" + str2;
        }
        return String.format(locale, upperCase, new Object[]{Double.valueOf(d)}).trim();
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str, objArr);
            }
            return String.format(str, objArr);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatDuration(int i) {
        if (i <= 0) {
            return formatPluralString("Seconds", 0);
        }
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(formatPluralString("Hours", i2));
        }
        if (i3 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Minutes", i3));
        }
        if (i4 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Seconds", i4));
        }
        return sb.toString();
    }

    public static String formatCallDuration(int i) {
        if (i > 3600) {
            String formatPluralString = formatPluralString("Hours", i / 3600);
            int i2 = (i % 3600) / 60;
            if (i2 <= 0) {
                return formatPluralString;
            }
            return formatPluralString + ", " + formatPluralString("Minutes", i2);
        } else if (i > 60) {
            return formatPluralString("Minutes", i / 60);
        } else {
            return formatPluralString("Seconds", i);
        }
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            Locale locale = configuration.locale;
            this.systemDefaultLocale = locale;
            if (this.languageOverride != null) {
                LocaleInfo localeInfo = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(localeInfo, false, false, UserConfig.selectedAccount);
            } else if (locale != null) {
                String displayName = locale.getDisplayName();
                String displayName2 = this.currentLocale.getDisplayName();
                if (!(displayName == null || displayName2 == null || displayName.equals(displayName2))) {
                    recreateFormatters();
                }
                this.currentLocale = locale;
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                if (localeInfo2 != null && !TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
            }
            String systemLocaleStringIso639 = getSystemLocaleStringIso639();
            String str = this.currentSystemLocale;
            if (str != null && !systemLocaleStringIso639.equals(str)) {
                this.currentSystemLocale = systemLocaleStringIso639;
                ConnectionsManager.setSystemLangCode(systemLocaleStringIso639);
            }
        }
    }

    public static String formatDateChat(long j) {
        return formatDateChat(j, false);
    }

    public static String formatDateChat(long j, boolean z) {
        try {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int i = instance.get(1);
            long j2 = j * 1000;
            instance.setTimeInMillis(j2);
            if ((!z || i != instance.get(1)) && (z || Math.abs(System.currentTimeMillis() - j2) >= 31536000000L)) {
                return getInstance().chatFullDate.format(j2);
            }
            return getInstance().chatDate.format(j2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j2));
            }
            if (i3 + 1 == i && i2 == i4) {
                return getString("Yesterday", NUM);
            }
            if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(j2));
            }
            return getInstance().formatterYear.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                if (z) {
                    return formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2)));
                }
                return formatString("TodayAtFormattedWithToday", NUM, getInstance().formatterDay.format(new Date(j2)));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2)));
            } else if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j2));
            }
            if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2)));
            } else if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().chatDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().chatFullDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateTime(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return formatString("TodayAtFormattedWithToday", NUM, getInstance().formatterDay.format(new Date(j2)));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2)));
            } else if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().chatDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().chatFullDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                int currentTime = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (j2 / 1000))) / 60;
                if (currentTime < 1) {
                    return getString("LocationUpdatedJustNow", NUM);
                }
                if (currentTime < 60) {
                    return formatPluralString("UpdatedMinutes", currentTime);
                }
                return formatString("LocationUpdatedFormatted", NUM, formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2))));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("LocationUpdatedFormatted", NUM, formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2))));
            } else if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("LocationUpdatedFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            } else {
                return formatString("LocationUpdatedFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int i) {
        int i2 = (i / 60) / 60;
        int i3 = i - ((i2 * 60) * 60);
        int i4 = i3 / 60;
        int i5 = i3 - (i4 * 60);
        int i6 = 1;
        if (i2 != 0) {
            Object[] objArr = new Object[1];
            if (i4 <= 30) {
                i6 = 0;
            }
            objArr[0] = Integer.valueOf(i2 + i6);
            return String.format("%dh", objArr);
        } else if (i4 != 0) {
            Object[] objArr2 = new Object[1];
            if (i5 <= 30) {
                i6 = 0;
            }
            objArr2[0] = Integer.valueOf(i4 + i6);
            return String.format("%d", objArr2);
        } else {
            return String.format("%d", new Object[]{Integer.valueOf(i5)});
        }
    }

    public static String formatDateOnline(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return formatString("LastSeenFormatted", NUM, formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2))));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("LastSeenFormatted", NUM, formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2))));
            } else if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("LastSeenDateFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            } else {
                return formatString("LastSeenDateFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String str, String str2) {
        if (str == null || str.length() == 0) {
            str = str2;
        }
        try {
            return FastDateFormat.getInstance(str, locale);
        } catch (Exception unused) {
            return FastDateFormat.getInstance(str2, locale);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005d, code lost:
        r2 = r9.currentLocaleInfo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void recreateFormatters() {
        /*
            r9 = this;
            java.util.Locale r0 = r9.currentLocale
            if (r0 != 0) goto L_0x0008
            java.util.Locale r0 = java.util.Locale.getDefault()
        L_0x0008:
            java.lang.String r1 = r0.getLanguage()
            if (r1 != 0) goto L_0x0010
            java.lang.String r1 = "en"
        L_0x0010:
            java.lang.String r1 = r1.toLowerCase()
            int r2 = r1.length()
            r3 = 0
            java.lang.String r4 = "ar"
            r5 = 1
            r6 = 2
            if (r2 != r6) goto L_0x003d
            boolean r2 = r1.equals(r4)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "fa"
            boolean r2 = r1.equals(r2)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "he"
            boolean r2 = r1.equals(r2)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "iw"
            boolean r2 = r1.equals(r2)
            if (r2 != 0) goto L_0x0068
        L_0x003d:
            java.lang.String r2 = "ar_"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "fa_"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "he_"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x0068
            java.lang.String r2 = "iw_"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x0068
            org.telegram.messenger.LocaleController$LocaleInfo r2 = r9.currentLocaleInfo
            if (r2 == 0) goto L_0x0066
            boolean r2 = r2.isRtl
            if (r2 == 0) goto L_0x0066
            goto L_0x0068
        L_0x0066:
            r2 = 0
            goto L_0x0069
        L_0x0068:
            r2 = 1
        L_0x0069:
            isRTL = r2
            java.lang.String r2 = "ko"
            boolean r7 = r1.equals(r2)
            if (r7 == 0) goto L_0x0075
            r7 = 2
            goto L_0x0076
        L_0x0075:
            r7 = 1
        L_0x0076:
            nameDisplayOrder = r7
            r7 = 2131628992(0x7f0e13c0, float:1.8885292E38)
            java.lang.String r8 = "formatterMonthYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterMonthYear = r7
            r7 = 2131628990(0x7f0e13be, float:1.8885288E38)
            java.lang.String r8 = "formatterMonth"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd MMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterDayMonth = r7
            r7 = 2131628998(0x7f0e13c6, float:1.8885304E38)
            java.lang.String r8 = "formatterYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYear = r7
            r7 = 2131628999(0x7f0e13c7, float:1.8885307E38)
            java.lang.String r8 = "formatterYearMax"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYearMax = r7
            r7 = 2131628958(0x7f0e139e, float:1.8885223E38)
            java.lang.String r8 = "chatDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatDate = r7
            r7 = 2131628959(0x7f0e139f, float:1.8885225E38)
            java.lang.String r8 = "chatFullDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatFullDate = r7
            r7 = 2131628996(0x7f0e13c4, float:1.88853E38)
            java.lang.String r8 = "formatterWeek"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "EEE"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterWeek = r7
            r7 = 2131628997(0x7f0e13c5, float:1.8885302E38)
            java.lang.String r8 = "formatterWeekLong"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "EEEE"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterWeekLong = r7
            r7 = 2131628982(0x7f0e13b6, float:1.8885272E38)
            java.lang.String r8 = "formatDateSchedule"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleDay = r7
            r7 = 2131628983(0x7f0e13b7, float:1.8885274E38)
            java.lang.String r8 = "formatDateScheduleYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleYear = r7
            java.lang.String r7 = r1.toLowerCase()
            boolean r4 = r7.equals(r4)
            if (r4 != 0) goto L_0x013a
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0137
            goto L_0x013a
        L_0x0137:
            java.util.Locale r1 = java.util.Locale.US
            goto L_0x013b
        L_0x013a:
            r1 = r0
        L_0x013b:
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0145
            r2 = 2131628989(0x7f0e13bd, float:1.8885286E38)
            java.lang.String r4 = "formatterDay24H"
            goto L_0x014a
        L_0x0145:
            r2 = 2131628988(0x7f0e13bc, float:1.8885284E38)
            java.lang.String r4 = "formatterDay12H"
        L_0x014a:
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            boolean r4 = is24HourFormat
            if (r4 == 0) goto L_0x0155
            java.lang.String r4 = "HH:mm"
            goto L_0x0157
        L_0x0155:
            java.lang.String r4 = "h:mm a"
        L_0x0157:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r1, r2, r4)
            r9.formatterDay = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x0167
            r1 = 2131628995(0x7f0e13c3, float:1.8885298E38)
            java.lang.String r2 = "formatterStats24H"
            goto L_0x016c
        L_0x0167:
            r1 = 2131628994(0x7f0e13c2, float:1.8885296E38)
            java.lang.String r2 = "formatterStats12H"
        L_0x016c:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            java.lang.String r4 = "MMM dd yyyy, HH:mm"
            java.lang.String r7 = "MMM dd yyyy, h:mm a"
            if (r2 == 0) goto L_0x017a
            r2 = r4
            goto L_0x017b
        L_0x017a:
            r2 = r7
        L_0x017b:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r2)
            r9.formatterStats = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x018b
            r1 = 2131628985(0x7f0e13b9, float:1.8885278E38)
            java.lang.String r2 = "formatterBannedUntil24H"
            goto L_0x0190
        L_0x018b:
            r1 = 2131628984(0x7f0e13b8, float:1.8885276E38)
            java.lang.String r2 = "formatterBannedUntil12H"
        L_0x0190:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0199
            goto L_0x019a
        L_0x0199:
            r4 = r7
        L_0x019a:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r4)
            r9.formatterBannedUntil = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x01aa
            r1 = 2131628987(0x7f0e13bb, float:1.8885282E38)
            java.lang.String r2 = "formatterBannedUntilThisYear24H"
            goto L_0x01af
        L_0x01aa:
            r1 = 2131628986(0x7f0e13ba, float:1.888528E38)
            java.lang.String r2 = "formatterBannedUntilThisYear12H"
        L_0x01af:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x01ba
            java.lang.String r2 = "MMM dd, HH:mm"
            goto L_0x01bc
        L_0x01ba:
            java.lang.String r2 = "MMM dd, h:mm a"
        L_0x01bc:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r2)
            r9.formatterBannedUntilThisYear = r1
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131627905(0x7f0e0var_, float:1.8883088E38)
            java.lang.String r4 = "SendTodayAt"
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            java.lang.String r4 = "'Send today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r4)
            r1[r3] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131627879(0x7f0e0var_, float:1.8883035E38)
            java.lang.String r3 = "SendDayAt"
            java.lang.String r2 = r9.getStringInternal(r3, r2)
            java.lang.String r3 = "'Send on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r3)
            r1[r5] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131627880(0x7f0e0var_, float:1.8883037E38)
            java.lang.String r3 = "SendDayYearAt"
            java.lang.String r2 = r9.getStringInternal(r3, r2)
            java.lang.String r3 = "'Send on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r3)
            r1[r6] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 3
            r3 = 2131627602(0x7f0e0e52, float:1.8882473E38)
            java.lang.String r4 = "RemindTodayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 4
            r3 = 2131627600(0x7f0e0e50, float:1.888247E38)
            java.lang.String r4 = "RemindDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 5
            r3 = 2131627601(0x7f0e0e51, float:1.8882471E38)
            java.lang.String r4 = "RemindDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 6
            r3 = 2131628109(0x7f0e104d, float:1.8883501E38)
            java.lang.String r4 = "StartTodayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Start today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 7
            r3 = 2131628101(0x7f0e1045, float:1.8883485E38)
            java.lang.String r4 = "StartDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Start on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 8
            r3 = 2131628102(0x7f0e1046, float:1.8883487E38)
            java.lang.String r4 = "StartDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Start on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 9
            r3 = 2131628107(0x7f0e104b, float:1.8883497E38)
            java.lang.String r4 = "StartShortTodayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Today,' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 10
            r3 = 2131628105(0x7f0e1049, float:1.8883493E38)
            java.lang.String r4 = "StartShortDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "MMM d',' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 11
            r3 = 2131628106(0x7f0e104a, float:1.8883495E38)
            java.lang.String r4 = "StartShortDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "MMM d yyyy, HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 12
            r3 = 2131628119(0x7f0e1057, float:1.8883522E38)
            java.lang.String r4 = "StartsTodayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Starts today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 13
            r3 = 2131628117(0x7f0e1055, float:1.8883518E38)
            java.lang.String r4 = "StartsDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Starts on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 14
            r3 = 2131628118(0x7f0e1056, float:1.888352E38)
            java.lang.String r4 = "StartsDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Starts on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r0 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.recreateFormatters():void");
    }

    public static boolean isRTLCharacter(char c) {
        return Character.getDirectionality(c) == 1 || Character.getDirectionality(c) == 2 || Character.getDirectionality(c) == 16 || Character.getDirectionality(c) == 17;
    }

    public static String formatStartsTime(long j, int i) {
        return formatStartsTime(j, i, true);
    }

    public static String formatStartsTime(long j, int i, boolean z) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i2 = instance.get(1);
        int i3 = instance.get(6);
        instance.setTimeInMillis(j * 1000);
        int i4 = i2 == instance.get(1) ? (!z || instance.get(6) != i3) ? 1 : 0 : 2;
        if (i == 1) {
            i4 += 3;
        } else if (i == 2) {
            i4 += 6;
        } else if (i == 3) {
            i4 += 9;
        } else if (i == 4) {
            i4 += 12;
        }
        return getInstance().formatterScheduleSend[i4].format(instance.getTimeInMillis());
    }

    public static String formatSectionDate(long j) {
        return formatYearMont(j, false);
    }

    public static String formatYearMont(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(1);
            instance.setTimeInMillis(j2);
            int i2 = instance.get(1);
            int i3 = instance.get(2);
            String[] strArr = {getString("January", NUM), getString("February", NUM), getString("March", NUM), getString("April", NUM), getString("May", NUM), getString("June", NUM), getString("July", NUM), getString("August", NUM), getString("September", NUM), getString("October", NUM), getString("November", NUM), getString("December", NUM)};
            if (i == i2 && !z) {
                return strArr[i3];
            }
            return strArr[i3] + " " + i2;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(1);
            instance.setTimeInMillis(j2);
            if (i == instance.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(j2));
            }
            return getInstance().formatterBannedUntil.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            instance.setTimeInMillis(j2);
            int i2 = instance.get(6);
            if (Math.abs(System.currentTimeMillis() - j2) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(j2));
            }
            int i3 = i2 - i;
            if (i3 != 0) {
                if (i3 != -1 || System.currentTimeMillis() - j2 >= 28800000) {
                    if (i3 <= -7 || i3 > -1) {
                        return getInstance().formatterDayMonth.format(new Date(j2));
                    }
                    return getInstance().formatterWeek.format(new Date(j2));
                }
            }
            return getInstance().formatterDay.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int i, int[] iArr) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (true) {
            int i3 = i / 1000;
            if (i3 <= 0) {
                break;
            }
            sb.append("K");
            i2 = (i % 1000) / 100;
            i = i3;
        }
        if (iArr != null) {
            double d = (double) i;
            double d2 = (double) i2;
            Double.isNaN(d2);
            Double.isNaN(d);
            double d3 = d + (d2 / 10.0d);
            for (int i4 = 0; i4 < sb.length(); i4++) {
                d3 *= 1000.0d;
            }
            iArr[0] = (int) d3;
        }
        if (i2 == 0 || sb.length() <= 0) {
            if (sb.length() == 2) {
                return String.format(Locale.US, "%dM", new Object[]{Integer.valueOf(i)});
            }
            return String.format(Locale.US, "%d%s", new Object[]{Integer.valueOf(i), sb.toString()});
        } else if (sb.length() == 2) {
            return String.format(Locale.US, "%d.%dM", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), sb.toString()});
        }
    }

    public static String formatUserStatus(int i, TLRPC$User tLRPC$User) {
        return formatUserStatus(i, tLRPC$User, (boolean[]) null);
    }

    public static String formatJoined(long j) {
        String str;
        long j2 = j * 1000;
        try {
            if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                str = formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            } else {
                str = formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
            }
            return formatString("ChannelOtherSubscriberJoined", NUM, str);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatImportedDate(long j) {
        try {
            Date date = new Date(j * 1000);
            return String.format("%1$s, %2$s", new Object[]{getInstance().formatterYear.format(date), getInstance().formatterDay.format(date)});
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatUserStatus(int i, TLRPC$User tLRPC$User, boolean[] zArr) {
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$UserStatus tLRPC$UserStatus3;
        if (!(tLRPC$User == null || (tLRPC$UserStatus3 = tLRPC$User.status) == null || tLRPC$UserStatus3.expires != 0)) {
            if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusRecently) {
                tLRPC$UserStatus3.expires = -100;
            } else if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusLastWeek) {
                tLRPC$UserStatus3.expires = -101;
            } else if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusLastMonth) {
                tLRPC$UserStatus3.expires = -102;
            }
        }
        if (tLRPC$User != null && (tLRPC$UserStatus2 = tLRPC$User.status) != null && tLRPC$UserStatus2.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Long.valueOf(tLRPC$User.id))) {
            if (zArr != null) {
                zArr[0] = true;
            }
            return getString("Online", NUM);
        } else if (tLRPC$User == null || (tLRPC$UserStatus = tLRPC$User.status) == null || tLRPC$UserStatus.expires == 0 || UserObject.isDeleted(tLRPC$User) || (tLRPC$User instanceof TLRPC$TL_userEmpty)) {
            return getString("ALongTimeAgo", NUM);
        } else {
            int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
            int i2 = tLRPC$User.status.expires;
            if (i2 > currentTime) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                return getString("Online", NUM);
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

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, int i) {
        if (this.currentLocaleInfo != null) {
            String lowerCase = tLRPC$TL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (lowerCase.equals(this.currentLocaleInfo.shortName) || lowerCase.equals(this.currentLocaleInfo.baseLangCode)) {
                lambda$applyRemoteLanguage$9(this.currentLocaleInfo, tLRPC$TL_langPackDifference, i);
            }
        }
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void lambda$applyRemoteLanguage$9(LocaleInfo localeInfo, TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, int i) {
        int i2;
        File file;
        HashMap<String, String> hashMap;
        if (tLRPC$TL_langPackDifference != null && !tLRPC$TL_langPackDifference.strings.isEmpty() && localeInfo != null && !localeInfo.isLocal()) {
            String lowerCase = tLRPC$TL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (lowerCase.equals(localeInfo.shortName)) {
                i2 = 0;
            } else {
                i2 = lowerCase.equals(localeInfo.baseLangCode) ? 1 : -1;
            }
            if (i2 != -1) {
                if (i2 == 0) {
                    file = localeInfo.getPathToFile();
                } else {
                    file = localeInfo.getPathToBaseFile();
                }
                try {
                    if (tLRPC$TL_langPackDifference.from_version == 0) {
                        hashMap = new HashMap<>();
                    } else {
                        hashMap = getLocaleFileStrings(file, true);
                    }
                    for (int i3 = 0; i3 < tLRPC$TL_langPackDifference.strings.size(); i3++) {
                        TLRPC$LangPackString tLRPC$LangPackString = tLRPC$TL_langPackDifference.strings.get(i3);
                        if (tLRPC$LangPackString instanceof TLRPC$TL_langPackString) {
                            hashMap.put(tLRPC$LangPackString.key, escapeString(tLRPC$LangPackString.value));
                        } else if (tLRPC$LangPackString instanceof TLRPC$TL_langPackStringPluralized) {
                            String str = tLRPC$LangPackString.key + "_zero";
                            String str2 = tLRPC$LangPackString.zero_value;
                            String str3 = "";
                            hashMap.put(str, str2 != null ? escapeString(str2) : str3);
                            String str4 = tLRPC$LangPackString.key + "_one";
                            String str5 = tLRPC$LangPackString.one_value;
                            hashMap.put(str4, str5 != null ? escapeString(str5) : str3);
                            String str6 = tLRPC$LangPackString.key + "_two";
                            String str7 = tLRPC$LangPackString.two_value;
                            hashMap.put(str6, str7 != null ? escapeString(str7) : str3);
                            String str8 = tLRPC$LangPackString.key + "_few";
                            String str9 = tLRPC$LangPackString.few_value;
                            hashMap.put(str8, str9 != null ? escapeString(str9) : str3);
                            String str10 = tLRPC$LangPackString.key + "_many";
                            String str11 = tLRPC$LangPackString.many_value;
                            hashMap.put(str10, str11 != null ? escapeString(str11) : str3);
                            String str12 = tLRPC$LangPackString.key + "_other";
                            String str13 = tLRPC$LangPackString.other_value;
                            if (str13 != null) {
                                str3 = escapeString(str13);
                            }
                            hashMap.put(str12, str3);
                        } else if (tLRPC$LangPackString instanceof TLRPC$TL_langPackStringDeleted) {
                            hashMap.remove(tLRPC$LangPackString.key);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("save locale file to " + file);
                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    bufferedWriter.write("<resources>\n");
                    for (Map.Entry next : hashMap.entrySet()) {
                        bufferedWriter.write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[]{next.getKey(), next.getValue()}));
                    }
                    bufferedWriter.write("</resources>");
                    bufferedWriter.close();
                    boolean hasBaseLang = localeInfo.hasBaseLang();
                    HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    if (hasBaseLang) {
                        localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                    AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda2(this, i2, localeInfo, tLRPC$TL_langPackDifference, localeFileStrings));
                } catch (Exception unused) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveRemoteLocaleStrings$4(int i, LocaleInfo localeInfo, TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, HashMap hashMap) {
        String[] strArr;
        Locale locale;
        if (i == 0) {
            localeInfo.version = tLRPC$TL_langPackDifference.version;
        } else {
            localeInfo.baseVersion = tLRPC$TL_langPackDifference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    strArr = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    strArr = localeInfo.baseLangCode.split("_");
                } else {
                    strArr = localeInfo.shortName.split("_");
                }
                if (strArr.length == 1) {
                    locale = new Locale(strArr[0]);
                } else {
                    locale = new Locale(strArr[0], strArr[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putString("language", localeInfo.getKey());
                edit.commit();
                this.localeValues = hashMap;
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
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
            FileLog.e((Throwable) e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int i) {
        if (!this.loadingRemoteLanguages) {
            this.loadingRemoteLanguages = true;
            ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_langpack_getLanguages(), new LocaleController$$ExternalSyntheticLambda10(this, i), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRemoteLanguages$6(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda9(this, tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRemoteLanguages$5(TLObject tLObject, int i) {
        this.loadingRemoteLanguages = false;
        TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.remoteLanguages.get(i2).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = tLRPC$Vector.objects.size();
        for (int i3 = 0; i3 < size2; i3++) {
            TLRPC$TL_langPackLanguage tLRPC$TL_langPackLanguage = (TLRPC$TL_langPackLanguage) tLRPC$Vector.objects.get(i3);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + tLRPC$TL_langPackLanguage.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = tLRPC$TL_langPackLanguage.name;
            localeInfo.name = tLRPC$TL_langPackLanguage.native_name;
            localeInfo.shortName = tLRPC$TL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
            String str = tLRPC$TL_langPackLanguage.base_lang_code;
            if (str != null) {
                localeInfo.baseLangCode = str.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = tLRPC$TL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = tLRPC$TL_langPackLanguage.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = i3;
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                languageFromDict.nameEnglish = localeInfo.nameEnglish;
                languageFromDict.name = localeInfo.name;
                languageFromDict.baseLangCode = localeInfo.baseLangCode;
                languageFromDict.pluralLangCode = localeInfo.pluralLangCode;
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                localeInfo = languageFromDict;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int i4 = 0;
        while (i4 < this.remoteLanguages.size()) {
            LocaleInfo localeInfo2 = this.remoteLanguages.get(i4);
            if (localeInfo2.serverIndex == Integer.MAX_VALUE && localeInfo2 != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + localeInfo2.getKey());
                }
                this.remoteLanguages.remove(i4);
                this.remoteLanguagesDict.remove(localeInfo2.getKey());
                this.languages.remove(localeInfo2);
                this.languagesDict.remove(localeInfo2.getKey());
                i4--;
            }
            i4++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        applyLanguage(this.currentLocaleInfo, true, false, i);
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, String str, boolean z, int i) {
        if (localeInfo == null) {
            return;
        }
        if (localeInfo.isRemote() || localeInfo.isUnofficial()) {
            if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion == 0 || z) {
                    TLRPC$TL_langpack_getLangPack tLRPC$TL_langpack_getLangPack = new TLRPC$TL_langpack_getLangPack();
                    tLRPC$TL_langpack_getLangPack.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getLangPack, new LocaleController$$ExternalSyntheticLambda14(this, localeInfo, i), 8);
                } else if (localeInfo.hasBaseLang()) {
                    TLRPC$TL_langpack_getDifference tLRPC$TL_langpack_getDifference = new TLRPC$TL_langpack_getDifference();
                    tLRPC$TL_langpack_getDifference.from_version = localeInfo.baseVersion;
                    tLRPC$TL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                    tLRPC$TL_langpack_getDifference.lang_pack = "";
                    ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getDifference, new LocaleController$$ExternalSyntheticLambda13(this, localeInfo, i), 8);
                }
            }
            if (str != null && !str.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version == 0 || z) {
                for (int i2 = 0; i2 < 3; i2++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TLRPC$TL_langpack_getLangPack tLRPC$TL_langpack_getLangPack2 = new TLRPC$TL_langpack_getLangPack();
                tLRPC$TL_langpack_getLangPack2.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getLangPack2, new LocaleController$$ExternalSyntheticLambda11(this, localeInfo, i), 8);
                return;
            }
            TLRPC$TL_langpack_getDifference tLRPC$TL_langpack_getDifference2 = new TLRPC$TL_langpack_getDifference();
            tLRPC$TL_langpack_getDifference2.from_version = localeInfo.version;
            tLRPC$TL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
            tLRPC$TL_langpack_getDifference2.lang_pack = "";
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getDifference2, new LocaleController$$ExternalSyntheticLambda12(this, localeInfo, i), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyRemoteLanguage$8(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda6(this, localeInfo, tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyRemoteLanguage$10(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda5(this, localeInfo, tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyRemoteLanguage$12(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda8(this, localeInfo, tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyRemoteLanguage$14(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda7(this, localeInfo, tLObject, i));
        }
    }

    public String getTranslitString(String str) {
        return getTranslitString(str, true, false);
    }

    public String getTranslitString(String str, boolean z) {
        return getTranslitString(str, true, z);
    }

    public String getTranslitString(String str, boolean z, boolean z2) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        Object obj7;
        Object obj8;
        Object obj9;
        if (str == null) {
            return null;
        }
        Object obj10 = "h";
        Object obj11 = "t";
        Object obj12 = "u";
        Object obj13 = "s";
        Object obj14 = "r";
        if (this.ruTranslitChars == null) {
            HashMap<String, String> hashMap = new HashMap<>(33);
            this.ruTranslitChars = hashMap;
            hashMap.put("а", "a");
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", "g");
            this.ruTranslitChars.put("д", "d");
            this.ruTranslitChars.put("е", "e");
            obj = "g";
            this.ruTranslitChars.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", "z");
            this.ruTranslitChars.put("и", "i");
            this.ruTranslitChars.put("й", "i");
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", "l");
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", "n");
            this.ruTranslitChars.put("о", "o");
            Object obj15 = "p";
            this.ruTranslitChars.put("п", obj15);
            obj2 = "m";
            obj9 = obj14;
            this.ruTranslitChars.put("р", obj9);
            obj3 = "z";
            Object obj16 = obj13;
            this.ruTranslitChars.put("с", obj16);
            obj4 = "v";
            this.ruTranslitChars.put("т", obj11);
            obj8 = obj12;
            this.ruTranslitChars.put("у", obj8);
            obj5 = obj16;
            this.ruTranslitChars.put("ф", "f");
            obj7 = obj10;
            this.ruTranslitChars.put("х", obj7);
            obj6 = obj15;
            this.ruTranslitChars.put("ц", "ts");
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", "i");
            this.ruTranslitChars.put("ь", "");
            this.ruTranslitChars.put("ъ", "");
            this.ruTranslitChars.put("э", "e");
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        } else {
            obj2 = "m";
            obj = "g";
            obj9 = obj14;
            obj3 = "z";
            obj7 = obj10;
            obj6 = "p";
            Object obj17 = obj13;
            obj4 = "v";
            obj8 = obj12;
            obj5 = obj17;
        }
        if (this.translitChars == null) {
            HashMap<String, String> hashMap2 = new HashMap<>(487);
            this.translitChars = hashMap2;
            hashMap2.put("ȼ", "c");
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", "d");
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            this.translitChars.put("ḁ", "a");
            this.translitChars.put("ʯ", obj7);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", obj8);
            Object obj18 = obj8;
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", obj9);
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", obj9);
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            Object obj19 = obj6;
            this.translitChars.put("ꝕ", obj19);
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", "a");
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", obj7);
            this.translitChars.put("ᵬ", "b");
            Object obj20 = obj7;
            Object obj21 = obj5;
            this.translitChars.put("ṣ", obj21);
            this.translitChars.put("đ", "d");
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", "a");
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", obj4);
            this.translitChars.put("ꝓ", obj19);
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", "d");
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", obj9);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            Object obj22 = obj;
            this.translitChars.put("ᶃ", obj22);
            Object obj23 = obj19;
            Object obj24 = "n";
            this.translitChars.put("ṋ", obj24);
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", obj22);
            this.translitChars.put("ǳ", "dz");
            Object obj25 = obj3;
            this.translitChars.put("ź", obj25);
            this.translitChars.put("ꜷ", "au");
            Object obj26 = obj18;
            this.translitChars.put("ǖ", obj26);
            this.translitChars.put("ᵹ", obj22);
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", "a");
            this.translitChars.put("ą", "a");
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", obj9);
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", "a");
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", obj21);
            this.translitChars.put("ﬂ", "fl");
            Object obj27 = "i";
            this.translitChars.put("ȉ", obj27);
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", obj24);
            this.translitChars.put("ï", obj27);
            this.translitChars.put("ñ", obj24);
            this.translitChars.put("ᴉ", obj27);
            Object obj28 = obj24;
            Object obj29 = obj11;
            this.translitChars.put("ʇ", obj29);
            this.translitChars.put("ẓ", obj25);
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", obj21);
            this.translitChars.put("ɽ", obj9);
            this.translitChars.put("ĝ", obj22);
            this.translitChars.put("ᴝ", obj26);
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", obj27);
            this.translitChars.put("ť", obj29);
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", obj26);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", "a");
            this.translitChars.put("ǘ", obj26);
            this.translitChars.put("ꞅ", obj21);
            this.translitChars.put("ᵣ", obj9);
            this.translitChars.put("ᴀ", "a");
            Object obj30 = "b";
            this.translitChars.put("ƃ", obj30);
            Object obj31 = "l";
            Object obj32 = obj20;
            this.translitChars.put("ḩ", obj32);
            this.translitChars.put("ṧ", obj21);
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", obj32);
            Object obj33 = obj21;
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            Object obj34 = "d";
            this.translitChars.put("ḋ", obj34);
            Object obj35 = obj29;
            this.translitChars.put("ƣ", "oi");
            Object obj36 = obj23;
            this.translitChars.put("ꝑ", obj36);
            this.translitChars.put("ħ", obj32);
            Object obj37 = obj32;
            Object obj38 = obj4;
            this.translitChars.put("ⱴ", obj38);
            Object obj39 = obj27;
            this.translitChars.put("ẇ", "w");
            Object obj40 = obj28;
            this.translitChars.put("ǹ", obj40);
            Object obj41 = obj2;
            this.translitChars.put("ɯ", obj41);
            this.translitChars.put("ɡ", obj22);
            this.translitChars.put("ɴ", obj40);
            this.translitChars.put("ᴘ", obj36);
            this.translitChars.put("ᵥ", obj38);
            this.translitChars.put("ū", obj26);
            this.translitChars.put("ḃ", obj30);
            this.translitChars.put("ṗ", obj36);
            this.translitChars.put("å", "a");
            this.translitChars.put("ɕ", "c");
            Object obj42 = obj36;
            Object obj43 = "o";
            this.translitChars.put("ọ", obj43);
            this.translitChars.put("ắ", "a");
            Object obj44 = obj22;
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", obj9);
            this.translitChars.put("ô", obj43);
            this.translitChars.put("ǿ", obj43);
            this.translitChars.put("ṳ", obj26);
            this.translitChars.put("ȥ", obj25);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", obj34);
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", obj26);
            this.translitChars.put("ȵ", obj40);
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", "a");
            Object obj45 = "k";
            this.translitChars.put("ǩ", obj45);
            Object obj46 = obj41;
            this.translitChars.put("ĩ", obj39);
            this.translitChars.put("ṵ", obj26);
            Object obj47 = obj35;
            this.translitChars.put("ŧ", obj47);
            this.translitChars.put("ɾ", obj9);
            this.translitChars.put("ƙ", obj45);
            this.translitChars.put("ṫ", obj47);
            Object obj48 = obj45;
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", "a");
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", obj31);
            this.translitChars.put("ᶂ", "f");
            Object obj49 = obj33;
            this.translitChars.put("ᵴ", obj49);
            this.translitChars.put("ꞃ", obj9);
            this.translitChars.put("ᶌ", obj38);
            this.translitChars.put("ɵ", obj43);
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", obj26);
            this.translitChars.put("ẑ", obj25);
            this.translitChars.put("ṹ", obj26);
            this.translitChars.put("ň", obj40);
            Object obj50 = "c";
            Object obj51 = "w";
            this.translitChars.put("ʍ", obj51);
            this.translitChars.put("ầ", "a");
            Object obj52 = obj38;
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", obj30);
            this.translitChars.put("ɼ", obj9);
            this.translitChars.put("ò", obj43);
            this.translitChars.put("ẘ", obj51);
            this.translitChars.put("ɗ", obj34);
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", obj26);
            this.translitChars.put("ᶀ", obj30);
            this.translitChars.put("ǜ", obj26);
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", "a");
            Object obj53 = obj37;
            this.translitChars.put("ɥ", obj53);
            this.translitChars.put("ṏ", obj43);
            this.translitChars.put("ǔ", obj26);
            Object obj54 = obj30;
            Object obj55 = "y";
            this.translitChars.put("ʎ", obj55);
            this.translitChars.put("ȱ", obj43);
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            Object obj56 = obj39;
            this.translitChars.put("ĭ", obj56);
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", obj47);
            this.translitChars.put("ᶑ", obj34);
            this.translitChars.put("ḧ", obj53);
            this.translitChars.put("ṥ", obj49);
            this.translitChars.put("ë", "e");
            Object obj57 = obj47;
            Object obj58 = obj46;
            this.translitChars.put("ᴍ", obj58);
            this.translitChars.put("ö", obj43);
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", obj56);
            this.translitChars.put("ď", obj34);
            this.translitChars.put("ᵯ", obj58);
            this.translitChars.put("ỵ", obj55);
            this.translitChars.put("ŵ", obj51);
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", obj26);
            this.translitChars.put("ƶ", obj25);
            Object obj59 = obj51;
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", obj34);
            this.translitChars.put("ŭ", obj26);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", obj26);
            this.translitChars.put("ġ", obj44);
            this.translitChars.put("ṙ", obj9);
            this.translitChars.put("ƞ", obj40);
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", obj49);
            this.translitChars.put("ᶁ", obj34);
            Object obj60 = obj48;
            this.translitChars.put("ķ", obj60);
            Object obj61 = obj56;
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", obj43);
            this.translitChars.put("ḿ", obj58);
            this.translitChars.put("ꜰ", "f");
            Object obj62 = "a";
            this.translitChars.put("ẵ", obj62);
            Object obj63 = obj43;
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", obj58);
            Object obj64 = obj42;
            this.translitChars.put("ᵽ", obj64);
            this.translitChars.put("ữ", obj26);
            this.translitChars.put("ⱪ", obj60);
            this.translitChars.put("ḥ", obj53);
            Object obj65 = obj60;
            Object obj66 = obj57;
            this.translitChars.put("ţ", obj66);
            this.translitChars.put("ᵱ", obj64);
            this.translitChars.put("ṁ", obj58);
            this.translitChars.put("á", obj62);
            this.translitChars.put("ᴎ", obj40);
            Object obj67 = obj49;
            Object obj68 = obj52;
            this.translitChars.put("ꝟ", obj68);
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", obj25);
            this.translitChars.put("ꝺ", obj34);
            this.translitChars.put("ᶈ", obj64);
            Object obj69 = obj64;
            Object obj70 = obj31;
            this.translitChars.put("ɫ", obj70);
            this.translitChars.put("ᴢ", obj25);
            this.translitChars.put("ɱ", obj58);
            this.translitChars.put("ṝ", obj9);
            this.translitChars.put("ṽ", obj68);
            this.translitChars.put("ũ", obj26);
            Object obj71 = obj58;
            this.translitChars.put("ß", "ss");
            this.translitChars.put("ĥ", obj53);
            this.translitChars.put("ᵵ", obj66);
            this.translitChars.put("ʐ", obj25);
            this.translitChars.put("ṟ", obj9);
            this.translitChars.put("ɲ", obj40);
            this.translitChars.put("à", obj62);
            this.translitChars.put("ẙ", obj55);
            this.translitChars.put("ỳ", obj55);
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", obj26);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", obj62);
            this.translitChars.put("ʑ", obj25);
            Object obj72 = obj67;
            this.translitChars.put("ẛ", obj72);
            Object obj73 = obj40;
            Object obj74 = obj61;
            this.translitChars.put("ḭ", obj74);
            Object obj75 = obj68;
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", obj25);
            this.translitChars.put("ÿ", obj55);
            this.translitChars.put("ǝ", "e");
            Object obj76 = obj63;
            this.translitChars.put("ǭ", obj76);
            this.translitChars.put("ᴅ", obj34);
            this.translitChars.put("ᶅ", obj70);
            this.translitChars.put("ù", obj26);
            this.translitChars.put("ạ", obj62);
            Object obj77 = obj34;
            this.translitChars.put("ḅ", obj54);
            this.translitChars.put("ụ", obj26);
            this.translitChars.put("ằ", obj62);
            this.translitChars.put("ᴛ", obj66);
            this.translitChars.put("ƴ", obj55);
            this.translitChars.put("ⱦ", obj66);
            this.translitChars.put("ⱡ", obj70);
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", obj25);
            this.translitChars.put("ḫ", obj53);
            Object obj78 = obj59;
            this.translitChars.put("ⱳ", obj78);
            Object obj79 = obj53;
            this.translitChars.put("ḵ", obj65);
            this.translitChars.put("ờ", obj76);
            this.translitChars.put("î", obj74);
            this.translitChars.put("ģ", obj44);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", obj62);
            this.translitChars.put("ẳ", obj62);
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", obj66);
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", obj50);
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", obj26);
            this.translitChars.put("ỉ", obj74);
            this.translitChars.put("ᴚ", obj9);
            this.translitChars.put("ś", obj72);
            this.translitChars.put("ꝋ", obj76);
            this.translitChars.put("ỹ", obj55);
            this.translitChars.put("ṡ", obj72);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", obj62);
            this.translitChars.put("ẗ", obj66);
            this.translitChars.put("ĺ", obj70);
            this.translitChars.put("ž", obj25);
            this.translitChars.put("ᵺ", "th");
            Object obj80 = obj77;
            this.translitChars.put("ƌ", obj80);
            this.translitChars.put("ș", obj72);
            this.translitChars.put("š", obj72);
            this.translitChars.put("ᶙ", obj26);
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", obj72);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", obj26);
            this.translitChars.put("ố", obj76);
            this.translitChars.put("ȿ", obj72);
            Object obj81 = obj55;
            Object obj82 = obj75;
            this.translitChars.put("ᴠ", obj82);
            Object obj83 = obj70;
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", obj76);
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", obj62);
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", obj76);
            this.translitChars.put("ȋ", obj74);
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", obj80);
            this.translitChars.put("ⱬ", obj25);
            this.translitChars.put("ẁ", obj78);
            this.translitChars.put("ᶏ", obj62);
            this.translitChars.put("ꞇ", obj66);
            Object obj84 = obj44;
            this.translitChars.put("ğ", obj84);
            Object obj85 = obj78;
            Object obj86 = obj73;
            this.translitChars.put("ɳ", obj86);
            this.translitChars.put("ʛ", obj84);
            this.translitChars.put("ᴜ", obj26);
            this.translitChars.put("ẩ", obj62);
            this.translitChars.put("ṅ", obj86);
            this.translitChars.put("ɨ", obj74);
            this.translitChars.put("ᴙ", obj9);
            this.translitChars.put("ǎ", obj62);
            this.translitChars.put("ſ", obj72);
            this.translitChars.put("ȫ", obj76);
            this.translitChars.put("ɿ", obj9);
            this.translitChars.put("ƭ", obj66);
            this.translitChars.put("ḯ", obj74);
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", obj82);
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", obj71);
            this.translitChars.put("ż", obj25);
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", obj76);
            this.translitChars.put("ễ", "e");
            Object obj87 = obj83;
            this.translitChars.put("ɬ", obj87);
            this.translitChars.put("ị", obj74);
            this.translitChars.put("ᵭ", obj80);
            Object obj88 = obj82;
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", obj87);
            this.translitChars.put("ŕ", obj9);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", obj66);
            this.translitChars.put("ā", obj62);
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", obj76);
            Object obj89 = obj50;
            this.translitChars.put("ç", obj89);
            this.translitChars.put("ᶊ", obj72);
            this.translitChars.put("ặ", obj62);
            this.translitChars.put("ų", obj26);
            this.translitChars.put("ả", obj62);
            this.translitChars.put("ǥ", obj84);
            Object obj90 = obj26;
            Object obj91 = obj65;
            this.translitChars.put("ꝁ", obj91);
            this.translitChars.put("ẕ", obj25);
            this.translitChars.put("ŝ", obj72);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", obj84);
            this.translitChars.put("ꝉ", obj87);
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", obj76);
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", obj76);
            this.translitChars.put("ƫ", obj66);
            this.translitChars.put("ǫ", obj76);
            this.translitChars.put("i̇", obj74);
            Object obj92 = obj73;
            this.translitChars.put("ṇ", obj92);
            this.translitChars.put("ć", obj89);
            this.translitChars.put("ᵷ", obj84);
            Object obj93 = obj89;
            Object obj94 = obj85;
            this.translitChars.put("ẅ", obj94);
            this.translitChars.put("ḑ", obj80);
            this.translitChars.put("ḹ", obj87);
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", obj9);
            this.translitChars.put("ļ", obj87);
            this.translitChars.put("ȑ", obj9);
            this.translitChars.put("ȭ", obj76);
            this.translitChars.put("ᵰ", obj92);
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", obj87);
            this.translitChars.put("ä", obj62);
            Object obj95 = obj69;
            this.translitChars.put("ƥ", obj95);
            this.translitChars.put("ỏ", obj76);
            this.translitChars.put("į", obj74);
            this.translitChars.put("ȓ", obj9);
            Object obj96 = obj72;
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", obj84);
            Object obj97 = obj90;
            this.translitChars.put("ṻ", obj97);
            this.translitChars.put("ō", obj76);
            this.translitChars.put("ľ", obj87);
            this.translitChars.put("ẃ", obj94);
            this.translitChars.put("ț", obj66);
            this.translitChars.put("ń", obj92);
            this.translitChars.put("ɍ", obj9);
            this.translitChars.put("ȃ", obj62);
            this.translitChars.put("ü", obj97);
            this.translitChars.put("ꞁ", obj87);
            this.translitChars.put("ᴐ", obj76);
            this.translitChars.put("ớ", obj76);
            Object obj98 = obj91;
            this.translitChars.put("ᴃ", obj54);
            this.translitChars.put("ɹ", obj9);
            this.translitChars.put("ᵲ", obj9);
            this.translitChars.put("ʏ", obj81);
            this.translitChars.put("ᵮ", "f");
            Object obj99 = obj79;
            this.translitChars.put("ⱨ", obj99);
            this.translitChars.put("ŏ", obj76);
            this.translitChars.put("ú", obj97);
            this.translitChars.put("ṛ", obj9);
            this.translitChars.put("ʮ", obj99);
            this.translitChars.put("ó", obj76);
            this.translitChars.put("ů", obj97);
            this.translitChars.put("ỡ", obj76);
            this.translitChars.put("ṕ", obj95);
            this.translitChars.put("ᶖ", obj74);
            this.translitChars.put("ự", obj97);
            this.translitChars.put("ã", obj62);
            this.translitChars.put("ᵢ", obj74);
            this.translitChars.put("ṱ", obj66);
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", obj97);
            this.translitChars.put("í", obj74);
            this.translitChars.put("ɔ", obj76);
            this.translitChars.put("ɺ", obj9);
            this.translitChars.put("ɢ", obj84);
            this.translitChars.put("ř", obj9);
            this.translitChars.put("ẖ", obj99);
            this.translitChars.put("ű", obj97);
            this.translitChars.put("ȍ", obj76);
            this.translitChars.put("ḻ", obj87);
            this.translitChars.put("ḣ", obj99);
            this.translitChars.put("ȶ", obj66);
            this.translitChars.put("ņ", obj92);
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", obj74);
            this.translitChars.put("ẉ", obj94);
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", obj87);
            this.translitChars.put("ộ", obj76);
            this.translitChars.put("ɭ", obj87);
            this.translitChars.put("ẏ", obj81);
            this.translitChars.put("ᴊ", "j");
            Object obj100 = obj98;
            this.translitChars.put("ḱ", obj100);
            Object obj101 = obj88;
            this.translitChars.put("ṿ", obj101);
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", obj62);
            Object obj102 = obj96;
            this.translitChars.put("ş", obj102);
            this.translitChars.put("ŗ", obj9);
            this.translitChars.put("ʋ", obj101);
            this.translitChars.put("ₐ", obj62);
            Object obj103 = obj93;
            this.translitChars.put("ↄ", obj103);
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", obj71);
            this.translitChars.put("ᴡ", obj94);
            this.translitChars.put("ȏ", obj76);
            this.translitChars.put("č", obj103);
            this.translitChars.put("ǵ", obj84);
            this.translitChars.put("ĉ", obj103);
            this.translitChars.put("ᶗ", obj76);
            this.translitChars.put("ꝃ", obj100);
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", obj76);
            this.translitChars.put("ꜱ", obj102);
            this.translitChars.put("ṓ", obj76);
            this.translitChars.put("ȟ", obj99);
            this.translitChars.put("ő", obj76);
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
        }
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        boolean z3 = false;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            if (z2) {
                String lowerCase = substring.toLowerCase();
                boolean z4 = !substring.equals(lowerCase);
                substring = lowerCase;
                z3 = z4;
            }
            String str2 = this.translitChars.get(substring);
            if (str2 == null && z) {
                str2 = this.ruTranslitChars.get(substring);
            }
            if (str2 != null) {
                if (z2 && z3) {
                    if (str2.length() > 1) {
                        str2 = str2.substring(0, 1).toUpperCase() + str2.substring(1);
                    } else {
                        str2 = str2.toUpperCase();
                    }
                }
                sb.append(str2);
            } else {
                if (z2) {
                    char charAt = substring.charAt(0);
                    if ((charAt < 'a' || charAt > 'z' || charAt < '0' || charAt > '9') && charAt != ' ' && charAt != '\'' && charAt != ',' && charAt != '.' && charAt != '&' && charAt != '-' && charAt != '/') {
                        return null;
                    }
                    if (z3) {
                        substring = substring.toUpperCase();
                    }
                }
                sb.append(substring);
            }
            i = i2;
        }
        return sb.toString();
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i2 == 1) {
                return 2;
            }
            if (i2 == 2) {
                return 4;
            }
            return (i2 < 3 || i2 > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i != 0) {
                return (i2 < 1 || i2 > 19) ? 0 : 8;
            }
            return 8;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i == 1) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 >= 0 && i3 <= 1) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 12 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0) {
                return 8;
            }
            if (i2 < 2 || i2 > 10) {
                return (i2 < 11 || i2 > 19) ? 0 : 16;
            }
            return 8;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int i) {
            return (i % 10 != 1 || i == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && (i2 < 11 || i2 > 19)) {
                return 2;
            }
            if (i3 < 2 || i3 > 9) {
                return 0;
            }
            return (i2 < 11 || i2 > 19) ? 8 : 0;
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

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 == 0) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 11 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Serbian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 < 2 || i3 > 4) {
                return 0;
            }
            return (i2 < 12 || i2 > 14) ? 8 : 0;
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
            }
            return 8;
        }
    }

    public static String addNbsp(String str) {
        return str.replace(' ', 160);
    }

    public static void resetImperialSystemType() {
        useImperialSystemType = null;
    }

    public static boolean getUseImperialSystemType() {
        ensureImperialSystemInit();
        return useImperialSystemType.booleanValue();
    }

    public static void ensureImperialSystemInit() {
        if (useImperialSystemType == null) {
            int i = SharedConfig.distanceSystemType;
            boolean z = true;
            if (i == 0) {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager != null) {
                        String upperCase = telephonyManager.getSimCountryIso().toUpperCase();
                        if (!"US".equals(upperCase) && !"GB".equals(upperCase) && !"MM".equals(upperCase)) {
                            if (!"LR".equals(upperCase)) {
                                z = false;
                            }
                        }
                        useImperialSystemType = Boolean.valueOf(z);
                    }
                } catch (Exception e) {
                    useImperialSystemType = Boolean.FALSE;
                    FileLog.e((Throwable) e);
                }
            } else {
                if (i != 2) {
                    z = false;
                }
                useImperialSystemType = Boolean.valueOf(z);
            }
        }
    }

    public static String formatDistance(float f, int i) {
        return formatDistance(f, i, (Boolean) null);
    }

    public static String formatDistance(float f, int i, Boolean bool) {
        String str;
        String str2;
        ensureImperialSystemInit();
        if ((bool != null && bool.booleanValue()) || (bool == null && useImperialSystemType.booleanValue())) {
            float f2 = f * 3.28084f;
            if (f2 >= 1000.0f) {
                if (f2 % 5280.0f == 0.0f) {
                    str2 = String.format("%d", new Object[]{Integer.valueOf((int) (f2 / 5280.0f))});
                } else {
                    str2 = String.format("%.2f", new Object[]{Float.valueOf(f2 / 5280.0f)});
                }
                if (i == 0) {
                    return formatString("MilesAway", NUM, str2);
                } else if (i != 1) {
                    return formatString("MilesShort", NUM, str2);
                } else {
                    return formatString("MilesFromYou", NUM, str2);
                }
            } else if (i == 0) {
                return formatString("FootsAway", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f2))}));
            } else if (i != 1) {
                return formatString("FootsShort", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f2))}));
            } else {
                return formatString("FootsFromYou", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f2))}));
            }
        } else if (f >= 1000.0f) {
            if (f % 1000.0f == 0.0f) {
                str = String.format("%d", new Object[]{Integer.valueOf((int) (f / 1000.0f))});
            } else {
                str = String.format("%.2f", new Object[]{Float.valueOf(f / 1000.0f)});
            }
            if (i == 0) {
                return formatString("KMetersAway2", NUM, str);
            } else if (i != 1) {
                return formatString("KMetersShort", NUM, str);
            } else {
                return formatString("KMetersFromYou2", NUM, str);
            }
        } else if (i == 0) {
            return formatString("MetersAway2", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f))}));
        } else if (i != 1) {
            return formatString("MetersShort", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f))}));
        } else {
            return formatString("MetersFromYou2", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f))}));
        }
    }
}
