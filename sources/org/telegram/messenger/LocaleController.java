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
import android.util.Xml;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import org.telegram.tgnet.TLRPC;
import org.xmlpull.v1.XmlPullParser;

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
    private boolean changingConfiguration = false;
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
    public ArrayList<LocaleInfo> languages = new ArrayList<>();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap<>();
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList<>();
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList<>();
    public HashMap<String, LocaleInfo> remoteLanguagesDict = new HashMap<>();
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    public ArrayList<LocaleInfo> unofficialLanguages = new ArrayList<>();

    public static abstract class PluralRules {
        /* access modifiers changed from: package-private */
        public abstract int quantityForNumber(int i);
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new LocaleController$TimeZoneChangedReceiver$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$onReceive$0$org-telegram-messenger-LocaleController$TimeZoneChangedReceiver  reason: not valid java name */
        public /* synthetic */ void m1916xb3806a48() {
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
            String langCode = this.baseLangCode;
            if (langCode == null) {
                langCode = "";
            }
            if (TextUtils.isEmpty(this.pluralLangCode)) {
                String str = this.shortName;
            } else {
                String str2 = this.pluralLangCode;
            }
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version + "|" + langCode + "|" + this.pluralLangCode + "|" + (this.isRtl ? 1 : 0) + "|" + this.baseVersion + "|" + this.serverIndex;
        }

        public static LocaleInfo createWithString(String string) {
            if (string == null || string.length() == 0) {
                return null;
            }
            String[] args = string.split("\\|");
            LocaleInfo localeInfo = null;
            if (args.length >= 4) {
                localeInfo = new LocaleInfo();
                boolean z = false;
                localeInfo.name = args[0];
                localeInfo.nameEnglish = args[1];
                localeInfo.shortName = args[2].toLowerCase();
                localeInfo.pathToFile = args[3];
                if (args.length >= 5) {
                    localeInfo.version = Utilities.parseInt((CharSequence) args[4]).intValue();
                }
                localeInfo.baseLangCode = args.length >= 6 ? args[5] : "";
                localeInfo.pluralLangCode = args.length >= 7 ? args[6] : localeInfo.shortName;
                if (args.length >= 8) {
                    if (Utilities.parseInt((CharSequence) args[7]).intValue() == 1) {
                        z = true;
                    }
                    localeInfo.isRtl = z;
                }
                if (args.length >= 9) {
                    localeInfo.baseVersion = Utilities.parseInt((CharSequence) args[8]).intValue();
                }
                if (args.length >= 10) {
                    localeInfo.serverIndex = Utilities.parseInt((CharSequence) args[9]).intValue();
                } else {
                    localeInfo.serverIndex = Integer.MAX_VALUE;
                }
                if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
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
        LocaleController localInstance = Instance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    LocaleController localeController = new LocaleController();
                    localInstance = localeController;
                    Instance = localeController;
                }
            }
        }
        return localInstance;
    }

    public LocaleController() {
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
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.pluralLangCode = "en";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Italiano";
        localeInfo2.nameEnglish = "Italian";
        localeInfo2.pluralLangCode = "it";
        localeInfo2.shortName = "it";
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Español";
        localeInfo3.nameEnglish = "Spanish";
        localeInfo3.pluralLangCode = "es";
        localeInfo3.shortName = "es";
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Deutsch";
        localeInfo4.nameEnglish = "German";
        localeInfo4.pluralLangCode = "de";
        localeInfo4.shortName = "de";
        localeInfo4.pathToFile = null;
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Nederlands";
        localeInfo5.nameEnglish = "Dutch";
        localeInfo5.pluralLangCode = "nl";
        localeInfo5.shortName = "nl";
        localeInfo5.pathToFile = null;
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "العربية";
        localeInfo6.nameEnglish = "Arabic";
        localeInfo6.pluralLangCode = "ar";
        localeInfo6.shortName = "ar";
        localeInfo6.pathToFile = null;
        localeInfo6.builtIn = true;
        localeInfo6.isRtl = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "Português (Brasil)";
        localeInfo7.nameEnglish = "Portuguese (Brazil)";
        localeInfo7.pluralLangCode = "pt_br";
        localeInfo7.shortName = "pt_br";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "한국어";
        localeInfo8.nameEnglish = "Korean";
        localeInfo8.pluralLangCode = "ko";
        localeInfo8.shortName = "ko";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda0(this));
        }
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            LocaleInfo locale = this.otherLanguages.get(a);
            this.languages.add(locale);
            this.languagesDict.put(locale.getKey(), locale);
        }
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            LocaleInfo locale2 = this.remoteLanguages.get(a2);
            LocaleInfo existingLocale = getLanguageFromDict(locale2.getKey());
            if (existingLocale != null) {
                existingLocale.pathToFile = locale2.pathToFile;
                existingLocale.version = locale2.version;
                existingLocale.baseVersion = locale2.baseVersion;
                existingLocale.serverIndex = locale2.serverIndex;
                this.remoteLanguages.set(a2, existingLocale);
            } else {
                this.languages.add(locale2);
                this.languagesDict.put(locale2.getKey(), locale2);
            }
        }
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            LocaleInfo locale3 = this.unofficialLanguages.get(a3);
            LocaleInfo existingLocale2 = getLanguageFromDict(locale3.getKey());
            if (existingLocale2 != null) {
                existingLocale2.pathToFile = locale3.pathToFile;
                existingLocale2.version = locale3.version;
                existingLocale2.baseVersion = locale3.baseVersion;
                existingLocale2.serverIndex = locale3.serverIndex;
                this.unofficialLanguages.set(a3, existingLocale2);
            } else {
                this.languagesDict.put(locale3.getKey(), locale3);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        LocaleInfo currentInfo = null;
        boolean override = false;
        try {
            String lang = MessagesController.getGlobalMainSettings().getString("language", (String) null);
            if (!(lang == null || (currentInfo = getLanguageFromDict(lang)) == null)) {
                override = true;
            }
            if (currentInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                currentInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (currentInfo == null && (currentInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                currentInfo = getLanguageFromDict("en");
            }
            applyLanguage(currentInfo, override, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda7(this));
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m84lambda$new$0$orgtelegrammessengerLocaleController() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m85lambda$new$1$orgtelegrammessengerLocaleController() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public static String getLanguageFlag(String countryCode) {
        if (countryCode.length() != 2 || countryCode.equals("YL")) {
            return null;
        }
        if (countryCode.equals("XG")) {
            return "🛰";
        }
        if (countryCode.equals("XV")) {
            return "🌍";
        }
        char[] chars = countryCode.toCharArray();
        return new String(new char[]{CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(chars[0] + 61861), CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(chars[1] + 61861)});
    }

    public LocaleInfo getLanguageFromDict(String key) {
        if (key == null) {
            return null;
        }
        return this.languagesDict.get(key.toLowerCase().replace("-", "_"));
    }

    public LocaleInfo getBuiltinLanguageByPlural(String plural) {
        for (LocaleInfo l : this.languagesDict.values()) {
            if (l.pathToFile != null && l.pathToFile.equals("remote") && l.pluralLangCode != null && l.pluralLangCode.equals(plural)) {
                return l;
            }
        }
        return null;
    }

    private void addRules(String[] languages2, PluralRules rules) {
        for (String language : languages2) {
            this.allRules.put(language, rules);
        }
    }

    private String stringForQuantity(int quantity) {
        switch (quantity) {
            case 1:
                return "zero";
            case 2:
                return "one";
            case 4:
                return "two";
            case 8:
                return "few";
            case 16:
                return "many";
            default:
                return "other";
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void reloadCurrentRemoteLocale(int currentAccount, String langCode, boolean force) {
        if (langCode != null) {
            langCode = langCode.replace("-", "_");
        }
        if (langCode != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!langCode.equals(localeInfo.shortName) && !langCode.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, langCode, force, currentAccount);
    }

    public void checkUpdateForCurrentRemoteLocale(int currentAccount, int version, int baseVersion) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null) {
            return;
        }
        if (localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
            if (this.currentLocaleInfo.hasBaseLang() && this.currentLocaleInfo.baseVersion < baseVersion) {
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, currentAccount);
            }
            if (this.currentLocaleInfo.version < version) {
                LocaleInfo localeInfo3 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, currentAccount);
            }
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('_');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale locale = getInstance().getSystemDefaultLocale();
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo info = getInstance().currentLocaleInfo;
        if (info != null) {
            return info.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleAlias(String code) {
        if (code == null) {
            return null;
        }
        char c = 65535;
        switch (code.hashCode()) {
            case 3325:
                if (code.equals("he")) {
                    c = 7;
                    break;
                }
                break;
            case 3355:
                if (code.equals("id")) {
                    c = 6;
                    break;
                }
                break;
            case 3365:
                if (code.equals("in")) {
                    c = 0;
                    break;
                }
                break;
            case 3374:
                if (code.equals("iw")) {
                    c = 1;
                    break;
                }
                break;
            case 3391:
                if (code.equals("ji")) {
                    c = 5;
                    break;
                }
                break;
            case 3404:
                if (code.equals("jv")) {
                    c = 8;
                    break;
                }
                break;
            case 3405:
                if (code.equals("jw")) {
                    c = 2;
                    break;
                }
                break;
            case 3508:
                if (code.equals("nb")) {
                    c = 9;
                    break;
                }
                break;
            case 3521:
                if (code.equals("no")) {
                    c = 3;
                    break;
                }
                break;
            case 3704:
                if (code.equals("tl")) {
                    c = 4;
                    break;
                }
                break;
            case 3856:
                if (code.equals("yi")) {
                    c = 11;
                    break;
                }
                break;
            case 101385:
                if (code.equals("fil")) {
                    c = 10;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "id";
            case 1:
                return "he";
            case 2:
                return "jv";
            case 3:
                return "nb";
            case 4:
                return "fil";
            case 5:
                return "yi";
            case 6:
                return "in";
            case 7:
                return "iw";
            case 8:
                return "jw";
            case 9:
                return "no";
            case 10:
                return "tl";
            case 11:
                return "ji";
            default:
                return null;
        }
    }

    public boolean applyLanguageFile(File file, int currentAccount) {
        LocaleInfo localeInfo;
        try {
            HashMap<String, String> stringMap = getLocaleFileStrings(file);
            String languageName = stringMap.get("LanguageName");
            String languageNameInEnglish = stringMap.get("LanguageNameInEnglish");
            String languageCode = stringMap.get("LanguageCode");
            if (languageName == null || languageName.length() <= 0 || languageNameInEnglish == null) {
                File file2 = file;
                return false;
            } else if (languageNameInEnglish.length() <= 0 || languageCode == null) {
                File file3 = file;
                return false;
            } else if (languageCode.length() > 0) {
                if (languageName.contains("&")) {
                    File file4 = file;
                } else if (languageName.contains("|")) {
                    File file5 = file;
                } else {
                    if (languageNameInEnglish.contains("&")) {
                        File file6 = file;
                    } else if (languageNameInEnglish.contains("|")) {
                        File file7 = file;
                    } else {
                        if (languageCode.contains("&") || languageCode.contains("|") || languageCode.contains("/")) {
                            File file8 = file;
                        } else if (languageCode.contains("\\")) {
                            File file9 = file;
                        } else {
                            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                            File finalFile = new File(filesDirFixed, languageCode + ".xml");
                            try {
                                if (!AndroidUtilities.copyFile(file, finalFile)) {
                                    return false;
                                }
                                LocaleInfo localeInfo2 = getLanguageFromDict("local_" + languageCode.toLowerCase());
                                if (localeInfo2 == null) {
                                    LocaleInfo localeInfo3 = new LocaleInfo();
                                    localeInfo3.name = languageName;
                                    localeInfo3.nameEnglish = languageNameInEnglish;
                                    localeInfo3.shortName = languageCode.toLowerCase();
                                    localeInfo3.pluralLangCode = localeInfo3.shortName;
                                    localeInfo3.pathToFile = finalFile.getAbsolutePath();
                                    this.languages.add(localeInfo3);
                                    this.languagesDict.put(localeInfo3.getKey(), localeInfo3);
                                    this.otherLanguages.add(localeInfo3);
                                    saveOtherLanguages();
                                    localeInfo = localeInfo3;
                                } else {
                                    localeInfo = localeInfo2;
                                }
                                this.localeValues = stringMap;
                                applyLanguage(localeInfo, true, false, true, false, currentAccount);
                                return true;
                            } catch (Exception e) {
                                e = e;
                                FileLog.e((Throwable) e);
                                return false;
                            }
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            } else {
                File file10 = file;
                return false;
            }
        } catch (Exception e2) {
            e = e2;
            File file11 = file;
            FileLog.e((Throwable) e);
            return false;
        }
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            String loc = this.otherLanguages.get(a).getSaveString();
            if (loc != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc);
            }
        }
        editor.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            String loc2 = this.remoteLanguages.get(a2).getSaveString();
            if (loc2 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc2);
            }
        }
        editor.putString("remote", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            String loc3 = this.unofficialLanguages.get(a3).getSaveString();
            if (loc3 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc3);
            }
        }
        editor.putString("unofficial", stringBuilder.toString());
        editor.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int currentAccount) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo info = null;
            if (this.systemDefaultLocale.getLanguage() != null) {
                info = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (info == null) {
                info = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (info == null) {
                info = getLanguageFromDict("en");
            }
            applyLanguage(info, true, false, currentAccount);
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
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String locales = preferences.getString("locales", (String) null);
        if (!TextUtils.isEmpty(locales)) {
            for (String locale : locales.split("&")) {
                LocaleInfo localeInfo = LocaleInfo.createWithString(locale);
                if (localeInfo != null) {
                    this.otherLanguages.add(localeInfo);
                }
            }
        }
        String locales2 = preferences.getString("remote", (String) null);
        if (!TextUtils.isEmpty(locales2)) {
            for (String locale2 : locales2.split("&")) {
                LocaleInfo localeInfo2 = LocaleInfo.createWithString(locale2);
                localeInfo2.shortName = localeInfo2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(localeInfo2.getKey())) {
                    this.remoteLanguages.add(localeInfo2);
                    this.remoteLanguagesDict.put(localeInfo2.getKey(), localeInfo2);
                }
            }
        }
        String locales3 = preferences.getString("unofficial", (String) null);
        if (!TextUtils.isEmpty(locales3)) {
            for (String locale3 : locales3.split("&")) {
                LocaleInfo localeInfo3 = LocaleInfo.createWithString(locale3);
                if (localeInfo3 != null) {
                    localeInfo3.shortName = localeInfo3.shortName.replace("-", "_");
                    this.unofficialLanguages.add(localeInfo3);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean preserveEscapes) {
        FileInputStream stream = null;
        this.reloadLastFile = false;
        try {
            if (!file.exists()) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                return hashMap;
            }
            HashMap<String, String> stringMap = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            stream = new FileInputStream(file);
            parser.setInput(stream, "UTF-8");
            String name = null;
            String value = null;
            String attrName = null;
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2) {
                    name = parser.getName();
                    if (parser.getAttributeCount() > 0) {
                        attrName = parser.getAttributeValue(0);
                    }
                } else if (eventType == 4) {
                    if (!(attrName == null || (value = parser.getText()) == null)) {
                        String value2 = value.trim();
                        if (preserveEscapes) {
                            value = value2.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                        } else {
                            String old = value2.replace("\\n", "\n").replace("\\", "");
                            value = old.replace("&lt;", "<");
                            if (!this.reloadLastFile && !value.equals(old)) {
                                this.reloadLastFile = true;
                            }
                        }
                    }
                } else if (eventType == 3) {
                    value = null;
                    attrName = null;
                    name = null;
                }
                if (!(name == null || !name.equals("string") || value == null || attrName == null || value.length() == 0 || attrName.length() == 0)) {
                    stringMap.put(attrName, value);
                    name = null;
                    value = null;
                    attrName = null;
                }
            }
            try {
                stream.close();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            return stringMap;
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            this.reloadLastFile = true;
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            return new HashMap<>();
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
            }
            throw th;
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, int currentAccount) {
        applyLanguage(localeInfo, override, init, false, false, currentAccount);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, boolean fromFile, boolean force, int currentAccount) {
        boolean z;
        String[] args;
        Locale newLocale;
        LocaleInfo localeInfo2 = localeInfo;
        boolean z2 = force;
        int i = currentAccount;
        if (localeInfo2 != null) {
            boolean hasBase = localeInfo.hasBaseLang();
            File pathToFile = localeInfo.getPathToFile();
            File pathToBaseFile = localeInfo.getPathToBaseFile();
            String str = localeInfo2.shortName;
            if (!init) {
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
            boolean isLoadingRemote = false;
            if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (z2 || !pathToFile.exists() || (hasBase && !pathToBaseFile.exists()))) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
                }
                isLoadingRemote = true;
                if (init) {
                    AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda10(this, localeInfo2, i));
                } else {
                    applyRemoteLanguage(localeInfo2, (String) null, true, i);
                }
            }
            boolean isLoadingRemote2 = isLoadingRemote;
            try {
                if (!TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    args = localeInfo2.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo2.baseLangCode)) {
                    args = localeInfo2.baseLangCode.split("_");
                } else {
                    args = localeInfo2.shortName.split("_");
                }
                if (args.length == 1) {
                    newLocale = new Locale(args[0]);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                if (override) {
                    this.languageOverride = localeInfo2.shortName;
                    SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                    editor.putString("language", localeInfo.getKey());
                    editor.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!fromFile) {
                    HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    this.localeValues = localeFileStrings;
                    if (hasBase) {
                        localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                }
                this.currentLocale = newLocale;
                this.currentLocaleInfo = localeInfo2;
                if (!TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(args[0]);
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
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                if (this.reloadLastFile) {
                    if (init) {
                        AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda9(this, i, z2));
                    } else {
                        reloadCurrentRemoteLocale(i, (String) null, z2);
                    }
                    this.reloadLastFile = false;
                }
                if (isLoadingRemote2) {
                } else if (init) {
                    AndroidUtilities.runOnUIThread(LocaleController$$ExternalSyntheticLambda1.INSTANCE);
                } else {
                    String[] strArr = args;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                }
                z = false;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                z = false;
                this.changingConfiguration = false;
            }
            recreateFormatters();
            if (z2) {
                MediaDataController.getInstance(currentAccount).loadAttachMenuBots(z, true);
            }
        }
    }

    /* renamed from: lambda$applyLanguage$2$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m72lambda$applyLanguage$2$orgtelegrammessengerLocaleController(LocaleInfo localeInfo, int currentAccount) {
        applyRemoteLanguage(localeInfo, (String) null, true, currentAccount);
    }

    /* renamed from: lambda$applyLanguage$3$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m73lambda$applyLanguage$3$orgtelegrammessengerLocaleController(int currentAccount, boolean force) {
        reloadCurrentRemoteLocale(currentAccount, (String) null, force);
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

    private String getStringInternal(String key, int res) {
        return getStringInternal(key, (String) null, res);
    }

    private String getStringInternal(String key, String fallback, int res) {
        String value = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(key) : null;
        if (value == null) {
            if (BuildVars.USE_CLOUD_STRINGS && fallback != null) {
                value = this.localeValues.get(fallback);
            }
            if (value == null) {
                try {
                    value = ApplicationLoader.applicationContext.getString(res);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
        if (value != null) {
            return value;
        }
        return "LOC_ERR:" + key;
    }

    public static String getServerString(String key) {
        int resourceId;
        String value = getInstance().localeValues.get(key);
        if (value != null || (resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(key, "string", ApplicationLoader.applicationContext.getPackageName())) == 0) {
            return value;
        }
        return ApplicationLoader.applicationContext.getString(resourceId);
    }

    public static String getString(int res) {
        String key = resourcesCacheMap.get(Integer.valueOf(res));
        if (key == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(res);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(res);
            key = resourceEntryName;
            hashMap.put(valueOf, resourceEntryName);
        }
        return getString(key, res);
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    public static String getString(String key, String fallback, int res) {
        return getInstance().getStringInternal(key, fallback, res);
    }

    public static String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return "LOC_ERR:" + key;
        }
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(key, "string", ApplicationLoader.applicationContext.getPackageName());
        if (resourceId != 0) {
            return getString(key, resourceId);
        }
        return getServerString(key);
    }

    public static String getPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        return getString(param, key + "_other", ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String key, int plural, Object... args) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName());
        Object[] argsWithPlural = new Object[(args.length + 1)];
        argsWithPlural[0] = Integer.valueOf(plural);
        System.arraycopy(args, 0, argsWithPlural, 1, args.length);
        return formatString(param, key + "_other", resourceId, argsWithPlural);
    }

    public static String formatPluralStringComma(String key, int plural) {
        if (key != null) {
            try {
                if (key.length() != 0) {
                    if (getInstance().currentPluralRules != null) {
                        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
                        StringBuilder stringBuilder = new StringBuilder(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(plural)}));
                        for (int a = stringBuilder.length() - 3; a > 0; a -= 3) {
                            stringBuilder.insert(a, ',');
                        }
                        String str = null;
                        String value = BuildVars.USE_CLOUD_STRINGS != 0 ? getInstance().localeValues.get(param) : null;
                        if (value == null) {
                            if (BuildVars.USE_CLOUD_STRINGS) {
                                str = getInstance().localeValues.get(key + "_other");
                            }
                            value = str;
                        }
                        if (value == null) {
                            value = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()));
                        }
                        String value2 = value.replace("%1$d", "%1$s");
                        if (getInstance().currentLocale != null) {
                            return String.format(getInstance().currentLocale, value2, new Object[]{stringBuilder});
                        }
                        return String.format(value2, new Object[]{stringBuilder});
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return "LOC_ERR: " + key;
            }
        }
        return "LOC_ERR:" + key;
    }

    public static String formatString(int res, Object... args) {
        String key = resourcesCacheMap.get(Integer.valueOf(res));
        if (key == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(res);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(res);
            key = resourceEntryName;
            hashMap.put(valueOf, resourceEntryName);
        }
        return formatString(key, res, args);
    }

    public static String formatString(String key, int res, Object... args) {
        return formatString(key, (String) null, res, args);
    }

    public static String formatString(String key, String fallback, int res, Object... args) {
        try {
            String value = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(key) : null;
            if (value == null) {
                if (BuildVars.USE_CLOUD_STRINGS && fallback != null) {
                    value = getInstance().localeValues.get(fallback);
                }
                if (value == null) {
                    value = ApplicationLoader.applicationContext.getString(res);
                }
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, value, args);
            }
            return String.format(value, args);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + key;
        }
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return formatPluralString("Seconds", ttl, new Object[0]);
        }
        if (ttl < 3600) {
            return formatPluralString("Minutes", ttl / 60, new Object[0]);
        }
        if (ttl < 86400) {
            return formatPluralString("Hours", (ttl / 60) / 60, new Object[0]);
        }
        if (ttl < 604800) {
            return formatPluralString("Days", ((ttl / 60) / 60) / 24, new Object[0]);
        }
        if (ttl >= 2678400) {
            return formatPluralString("Months", (((ttl / 60) / 60) / 24) / 30, new Object[0]);
        }
        int days = ((ttl / 60) / 60) / 24;
        if (ttl % 7 == 0) {
            return formatPluralString("Weeks", days / 7, new Object[0]);
        }
        return String.format("%s %s", new Object[]{formatPluralString("Weeks", days / 7, new Object[0]), formatPluralString("Days", days % 7, new Object[0])});
    }

    public static String fixNumbers(CharSequence numbers) {
        StringBuilder builder = new StringBuilder(numbers);
        int N = builder.length();
        for (int c = 0; c < N; c++) {
            char ch = builder.charAt(c);
            if (!((ch >= '0' && ch <= '9') || ch == '.' || ch == ',')) {
                int a = 0;
                while (a < otherNumbers.length) {
                    int b = 0;
                    while (true) {
                        char[][] cArr = otherNumbers;
                        if (b >= cArr[a].length) {
                            break;
                        } else if (ch == cArr[a][b]) {
                            builder.setCharAt(c, defaultNumbers[b]);
                            a = otherNumbers.length;
                            break;
                        } else {
                            b++;
                        }
                    }
                    a++;
                }
            }
        }
        return builder.toString();
    }

    public String formatCurrencyString(long amount, String type) {
        return formatCurrencyString(amount, true, true, false, type);
    }

    public String formatCurrencyString(long amount, boolean fixAnything, boolean withExp, boolean editText, String type) {
        double doubleAmount;
        String customFormat;
        int idx;
        String type2 = type.toUpperCase();
        boolean discount = amount < 0;
        long amount2 = Math.abs(amount);
        Currency currency = Currency.getInstance(type2);
        char c = 65535;
        switch (type2.hashCode()) {
            case 65726:
                if (type2.equals("BHD")) {
                    c = 2;
                    break;
                }
                break;
            case 65759:
                if (type2.equals("BIF")) {
                    c = 9;
                    break;
                }
                break;
            case 66267:
                if (type2.equals("BYR")) {
                    c = 10;
                    break;
                }
                break;
            case 66813:
                if (type2.equals("CLF")) {
                    c = 0;
                    break;
                }
                break;
            case 66823:
                if (type2.equals("CLP")) {
                    c = 11;
                    break;
                }
                break;
            case 67122:
                if (type2.equals("CVE")) {
                    c = 12;
                    break;
                }
                break;
            case 67712:
                if (type2.equals("DJF")) {
                    c = 13;
                    break;
                }
                break;
            case 70719:
                if (type2.equals("GNF")) {
                    c = 14;
                    break;
                }
                break;
            case 72732:
                if (type2.equals("IQD")) {
                    c = 3;
                    break;
                }
                break;
            case 72777:
                if (type2.equals("IRR")) {
                    c = 1;
                    break;
                }
                break;
            case 72801:
                if (type2.equals("ISK")) {
                    c = 15;
                    break;
                }
                break;
            case 73631:
                if (type2.equals("JOD")) {
                    c = 4;
                    break;
                }
                break;
            case 73683:
                if (type2.equals("JPY")) {
                    c = 16;
                    break;
                }
                break;
            case 74532:
                if (type2.equals("KMF")) {
                    c = 17;
                    break;
                }
                break;
            case 74704:
                if (type2.equals("KRW")) {
                    c = 18;
                    break;
                }
                break;
            case 74840:
                if (type2.equals("KWD")) {
                    c = 5;
                    break;
                }
                break;
            case 75863:
                if (type2.equals("LYD")) {
                    c = 6;
                    break;
                }
                break;
            case 76263:
                if (type2.equals("MGA")) {
                    c = 19;
                    break;
                }
                break;
            case 76618:
                if (type2.equals("MRO")) {
                    c = 29;
                    break;
                }
                break;
            case 78388:
                if (type2.equals("OMR")) {
                    c = 7;
                    break;
                }
                break;
            case 79710:
                if (type2.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (type2.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (type2.equals("TND")) {
                    c = 8;
                    break;
                }
                break;
            case 83974:
                if (type2.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (type2.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (type2.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (type2.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (type2.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (type2.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (type2.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                customFormat = " %.4f";
                double d = (double) amount2;
                Double.isNaN(d);
                doubleAmount = d / 10000.0d;
                break;
            case 1:
                doubleAmount = (double) (((float) amount2) / 100.0f);
                if (fixAnything && amount2 % 100 == 0) {
                    customFormat = " %.0f";
                    break;
                } else {
                    customFormat = " %.2f";
                    break;
                }
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                customFormat = " %.3f";
                double d2 = (double) amount2;
                Double.isNaN(d2);
                doubleAmount = d2 / 1000.0d;
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
                customFormat = " %.0f";
                doubleAmount = (double) amount2;
                break;
            case 29:
                customFormat = " %.1f";
                double d3 = (double) amount2;
                Double.isNaN(d3);
                doubleAmount = d3 / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                double d4 = (double) amount2;
                Double.isNaN(d4);
                doubleAmount = d4 / 100.0d;
                break;
        }
        if (!withExp) {
            customFormat = " %.0f";
        }
        String str = "-";
        if (currency != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(currency);
            if (editText) {
                format.setGroupingUsed(false);
            }
            if (!withExp || (fixAnything && type2.equals("IRR"))) {
                format.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!discount) {
                str = "";
            }
            sb.append(str);
            sb.append(format.format(doubleAmount));
            String result = sb.toString();
            int idx2 = result.indexOf(type2);
            if (idx2 < 0 || (idx = idx2 + type2.length()) >= result.length() || result.charAt(idx) == ' ') {
                return result;
            }
            return result.substring(0, idx) + " " + result.substring(idx);
        }
        StringBuilder sb2 = new StringBuilder();
        if (!discount) {
            str = "";
        }
        sb2.append(str);
        Locale locale2 = Locale.US;
        sb2.append(String.format(locale2, type2 + customFormat, new Object[]{Double.valueOf(doubleAmount)}));
        return sb2.toString();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getCurrencyExpDivider(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = 10
            r2 = 1
            switch(r0) {
                case 65726: goto L_0x014c;
                case 65759: goto L_0x0141;
                case 66267: goto L_0x0136;
                case 66813: goto L_0x012c;
                case 66823: goto L_0x0121;
                case 67122: goto L_0x0116;
                case 67712: goto L_0x010b;
                case 70719: goto L_0x0100;
                case 72732: goto L_0x00f6;
                case 72801: goto L_0x00eb;
                case 73631: goto L_0x00e0;
                case 73683: goto L_0x00d4;
                case 74532: goto L_0x00c8;
                case 74704: goto L_0x00bc;
                case 74840: goto L_0x00b1;
                case 75863: goto L_0x00a6;
                case 76263: goto L_0x009a;
                case 76618: goto L_0x008e;
                case 78388: goto L_0x0083;
                case 79710: goto L_0x0077;
                case 81569: goto L_0x006b;
                case 83210: goto L_0x0060;
                case 83974: goto L_0x0054;
                case 84517: goto L_0x0048;
                case 85132: goto L_0x003c;
                case 85367: goto L_0x0030;
                case 86653: goto L_0x0024;
                case 87087: goto L_0x0018;
                case 87118: goto L_0x000c;
                default: goto L_0x000a;
            }
        L_0x000a:
            goto L_0x0156
        L_0x000c:
            java.lang.String r0 = "XPF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 27
            goto L_0x0157
        L_0x0018:
            java.lang.String r0 = "XOF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 26
            goto L_0x0157
        L_0x0024:
            java.lang.String r0 = "XAF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 25
            goto L_0x0157
        L_0x0030:
            java.lang.String r0 = "VUV"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 24
            goto L_0x0157
        L_0x003c:
            java.lang.String r0 = "VND"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 23
            goto L_0x0157
        L_0x0048:
            java.lang.String r0 = "UYI"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 22
            goto L_0x0157
        L_0x0054:
            java.lang.String r0 = "UGX"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 21
            goto L_0x0157
        L_0x0060:
            java.lang.String r0 = "TND"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 7
            goto L_0x0157
        L_0x006b:
            java.lang.String r0 = "RWF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 20
            goto L_0x0157
        L_0x0077:
            java.lang.String r0 = "PYG"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 19
            goto L_0x0157
        L_0x0083:
            java.lang.String r0 = "OMR"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 6
            goto L_0x0157
        L_0x008e:
            java.lang.String r0 = "MRO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 28
            goto L_0x0157
        L_0x009a:
            java.lang.String r0 = "MGA"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 18
            goto L_0x0157
        L_0x00a6:
            java.lang.String r0 = "LYD"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 5
            goto L_0x0157
        L_0x00b1:
            java.lang.String r0 = "KWD"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 4
            goto L_0x0157
        L_0x00bc:
            java.lang.String r0 = "KRW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 17
            goto L_0x0157
        L_0x00c8:
            java.lang.String r0 = "KMF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 16
            goto L_0x0157
        L_0x00d4:
            java.lang.String r0 = "JPY"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 15
            goto L_0x0157
        L_0x00e0:
            java.lang.String r0 = "JOD"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 3
            goto L_0x0157
        L_0x00eb:
            java.lang.String r0 = "ISK"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 14
            goto L_0x0157
        L_0x00f6:
            java.lang.String r0 = "IQD"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 2
            goto L_0x0157
        L_0x0100:
            java.lang.String r0 = "GNF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 13
            goto L_0x0157
        L_0x010b:
            java.lang.String r0 = "DJF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 12
            goto L_0x0157
        L_0x0116:
            java.lang.String r0 = "CVE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 11
            goto L_0x0157
        L_0x0121:
            java.lang.String r0 = "CLP"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 10
            goto L_0x0157
        L_0x012c:
            java.lang.String r0 = "CLF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 0
            goto L_0x0157
        L_0x0136:
            java.lang.String r0 = "BYR"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 9
            goto L_0x0157
        L_0x0141:
            java.lang.String r0 = "BIF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 8
            goto L_0x0157
        L_0x014c:
            java.lang.String r0 = "BHD"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 1
            goto L_0x0157
        L_0x0156:
            r0 = -1
        L_0x0157:
            switch(r0) {
                case 0: goto L_0x0162;
                case 1: goto L_0x015f;
                case 2: goto L_0x015f;
                case 3: goto L_0x015f;
                case 4: goto L_0x015f;
                case 5: goto L_0x015f;
                case 6: goto L_0x015f;
                case 7: goto L_0x015f;
                case 8: goto L_0x015e;
                case 9: goto L_0x015e;
                case 10: goto L_0x015e;
                case 11: goto L_0x015e;
                case 12: goto L_0x015e;
                case 13: goto L_0x015e;
                case 14: goto L_0x015e;
                case 15: goto L_0x015e;
                case 16: goto L_0x015e;
                case 17: goto L_0x015e;
                case 18: goto L_0x015e;
                case 19: goto L_0x015e;
                case 20: goto L_0x015e;
                case 21: goto L_0x015e;
                case 22: goto L_0x015e;
                case 23: goto L_0x015e;
                case 24: goto L_0x015e;
                case 25: goto L_0x015e;
                case 26: goto L_0x015e;
                case 27: goto L_0x015e;
                case 28: goto L_0x015d;
                default: goto L_0x015a;
            }
        L_0x015a:
            r0 = 100
            return r0
        L_0x015d:
            return r1
        L_0x015e:
            return r2
        L_0x015f:
            r0 = 1000(0x3e8, float:1.401E-42)
            return r0
        L_0x0162:
            r0 = 10000(0x2710, float:1.4013E-41)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getCurrencyExpDivider(java.lang.String):int");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String formatCurrencyDecimalString(long r10, java.lang.String r12, boolean r13) {
        /*
            r9 = this;
            java.lang.String r12 = r12.toUpperCase()
            long r10 = java.lang.Math.abs(r10)
            int r0 = r12.hashCode()
            r1 = 1
            r2 = 0
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
            }
        L_0x0011:
            goto L_0x0169
        L_0x0013:
            java.lang.String r0 = "XPF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 28
            goto L_0x016a
        L_0x001f:
            java.lang.String r0 = "XOF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 27
            goto L_0x016a
        L_0x002b:
            java.lang.String r0 = "XAF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 26
            goto L_0x016a
        L_0x0037:
            java.lang.String r0 = "VUV"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 25
            goto L_0x016a
        L_0x0043:
            java.lang.String r0 = "VND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 24
            goto L_0x016a
        L_0x004f:
            java.lang.String r0 = "UYI"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 23
            goto L_0x016a
        L_0x005b:
            java.lang.String r0 = "UGX"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 22
            goto L_0x016a
        L_0x0067:
            java.lang.String r0 = "TND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 8
            goto L_0x016a
        L_0x0073:
            java.lang.String r0 = "RWF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 21
            goto L_0x016a
        L_0x007f:
            java.lang.String r0 = "PYG"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 20
            goto L_0x016a
        L_0x008b:
            java.lang.String r0 = "OMR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 7
            goto L_0x016a
        L_0x0096:
            java.lang.String r0 = "MRO"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 29
            goto L_0x016a
        L_0x00a2:
            java.lang.String r0 = "MGA"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 19
            goto L_0x016a
        L_0x00ae:
            java.lang.String r0 = "LYD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 6
            goto L_0x016a
        L_0x00b9:
            java.lang.String r0 = "KWD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 5
            goto L_0x016a
        L_0x00c4:
            java.lang.String r0 = "KRW"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 18
            goto L_0x016a
        L_0x00d0:
            java.lang.String r0 = "KMF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 17
            goto L_0x016a
        L_0x00dc:
            java.lang.String r0 = "JPY"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 16
            goto L_0x016a
        L_0x00e8:
            java.lang.String r0 = "JOD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 4
            goto L_0x016a
        L_0x00f3:
            java.lang.String r0 = "ISK"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 15
            goto L_0x016a
        L_0x00ff:
            java.lang.String r0 = "IRR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 1
            goto L_0x016a
        L_0x0109:
            java.lang.String r0 = "IQD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 3
            goto L_0x016a
        L_0x0113:
            java.lang.String r0 = "GNF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 14
            goto L_0x016a
        L_0x011e:
            java.lang.String r0 = "DJF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 13
            goto L_0x016a
        L_0x0129:
            java.lang.String r0 = "CVE"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 12
            goto L_0x016a
        L_0x0134:
            java.lang.String r0 = "CLP"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 11
            goto L_0x016a
        L_0x013f:
            java.lang.String r0 = "CLF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 0
            goto L_0x016a
        L_0x0149:
            java.lang.String r0 = "BYR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 10
            goto L_0x016a
        L_0x0154:
            java.lang.String r0 = "BIF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 9
            goto L_0x016a
        L_0x015f:
            java.lang.String r0 = "BHD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 2
            goto L_0x016a
        L_0x0169:
            r0 = -1
        L_0x016a:
            switch(r0) {
                case 0: goto L_0x01a7;
                case 1: goto L_0x0192;
                case 2: goto L_0x0185;
                case 3: goto L_0x0185;
                case 4: goto L_0x0185;
                case 5: goto L_0x0185;
                case 6: goto L_0x0185;
                case 7: goto L_0x0185;
                case 8: goto L_0x0185;
                case 9: goto L_0x0181;
                case 10: goto L_0x0181;
                case 11: goto L_0x0181;
                case 12: goto L_0x0181;
                case 13: goto L_0x0181;
                case 14: goto L_0x0181;
                case 15: goto L_0x0181;
                case 16: goto L_0x0181;
                case 17: goto L_0x0181;
                case 18: goto L_0x0181;
                case 19: goto L_0x0181;
                case 20: goto L_0x0181;
                case 21: goto L_0x0181;
                case 22: goto L_0x0181;
                case 23: goto L_0x0181;
                case 24: goto L_0x0181;
                case 25: goto L_0x0181;
                case 26: goto L_0x0181;
                case 27: goto L_0x0181;
                case 28: goto L_0x0181;
                case 29: goto L_0x0177;
                default: goto L_0x016d;
            }
        L_0x016d:
            java.lang.String r0 = " %.2f"
            double r3 = (double) r10
            r5 = 4636737291354636288(0xNUM, double:100.0)
            java.lang.Double.isNaN(r3)
            double r3 = r3 / r5
            goto L_0x01b4
        L_0x0177:
            java.lang.String r0 = " %.1f"
            double r3 = (double) r10
            r5 = 4621819117588971520(0xNUM, double:10.0)
            java.lang.Double.isNaN(r3)
            double r3 = r3 / r5
            goto L_0x01b4
        L_0x0181:
            java.lang.String r0 = " %.0f"
            double r3 = (double) r10
            goto L_0x01b4
        L_0x0185:
            java.lang.String r0 = " %.3f"
            double r3 = (double) r10
            r5 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r3)
            double r3 = r3 / r5
            goto L_0x01b4
        L_0x0192:
            float r0 = (float) r10
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 / r3
            double r3 = (double) r0
            r5 = 100
            long r5 = r10 % r5
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x01a4
            java.lang.String r0 = " %.0f"
            goto L_0x01b4
        L_0x01a4:
            java.lang.String r0 = " %.2f"
            goto L_0x01b4
        L_0x01a7:
            java.lang.String r0 = " %.4f"
            double r3 = (double) r10
            r5 = 4666723172467343360(0x40cNUM, double:10000.0)
            java.lang.Double.isNaN(r3)
            double r3 = r3 / r5
        L_0x01b4:
            java.util.Locale r5 = java.util.Locale.US
            if (r13 == 0) goto L_0x01ba
            r6 = r12
            goto L_0x01cb
        L_0x01ba:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = ""
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
        L_0x01cb:
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Double r7 = java.lang.Double.valueOf(r3)
            r1[r2] = r7
            java.lang.String r1 = java.lang.String.format(r5, r6, r1)
            java.lang.String r1 = r1.trim()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.formatCurrencyDecimalString(long, java.lang.String, boolean):java.lang.String");
    }

    public static String formatStringSimple(String string, Object... args) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, string, args);
            }
            return String.format(string, args);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + string;
        }
    }

    public static String formatDuration(int duration) {
        if (duration <= 0) {
            return formatPluralString("Seconds", 0, new Object[0]);
        }
        int hours = duration / 3600;
        int minutes = (duration / 60) % 60;
        int seconds = duration % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(formatPluralString("Hours", hours, new Object[0]));
        }
        if (minutes > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(formatPluralString("Minutes", minutes, new Object[0]));
        }
        if (seconds > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(formatPluralString("Seconds", seconds, new Object[0]));
        }
        return stringBuilder.toString();
    }

    public static String formatCallDuration(int duration) {
        if (duration > 3600) {
            String result = formatPluralString("Hours", duration / 3600, new Object[0]);
            int minutes = (duration % 3600) / 60;
            if (minutes <= 0) {
                return result;
            }
            return result + ", " + formatPluralString("Minutes", minutes, new Object[0]);
        } else if (duration > 60) {
            return formatPluralString("Minutes", duration / 60, new Object[0]);
        } else {
            return formatPluralString("Seconds", duration, new Object[0]);
        }
    }

    public void onDeviceConfigurationChange(Configuration newConfig) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            this.systemDefaultLocale = newConfig.locale;
            if (this.languageOverride != null) {
                LocaleInfo toSet = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(toSet, false, false, UserConfig.selectedAccount);
            } else {
                Locale newLocale = newConfig.locale;
                if (newLocale != null) {
                    String d1 = newLocale.getDisplayName();
                    String d2 = this.currentLocale.getDisplayName();
                    if (!(d1 == null || d2 == null || d1.equals(d2))) {
                        recreateFormatters();
                    }
                    this.currentLocale = newLocale;
                    LocaleInfo localeInfo = this.currentLocaleInfo;
                    if (localeInfo != null && !TextUtils.isEmpty(localeInfo.pluralLangCode)) {
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
            }
            String newSystemLocale = getSystemLocaleStringIso639();
            String str = this.currentSystemLocale;
            if (str != null && !newSystemLocale.equals(str)) {
                this.currentSystemLocale = newSystemLocale;
                ConnectionsManager.setSystemLangCode(newSystemLocale);
            }
        }
    }

    public static String formatDateChat(long date) {
        return formatDateChat(date, false);
    }

    public static String formatDateChat(long date, boolean checkYear) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(1);
            long date2 = date * 1000;
            calendar.setTimeInMillis(date2);
            if ((!checkYear || currentYear != calendar.get(1)) && (checkYear || Math.abs(System.currentTimeMillis() - date2) >= 31536000000L)) {
                return getInstance().chatFullDate.format(date2);
            }
            return getInstance().chatDate.format(date2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return getString("Yesterday", NUM);
            }
            if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(date2));
            }
            return getInstance().formatterYear.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long date, boolean shortFormat) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                if (shortFormat) {
                    return formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
                }
                return formatString("TodayAtFormattedWithToday", NUM, getInstance().formatterDay.format(new Date(date2)));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().chatDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().chatFullDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateTime(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return formatString("TodayAtFormattedWithToday", NUM, getInstance().formatterDay.format(new Date(date2)));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("formatDateAtTime", NUM, getInstance().chatDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                return formatString("formatDateAtTime", NUM, getInstance().chatFullDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                int diff = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (date2 / 1000))) / 60;
                if (diff < 1) {
                    return getString("LocationUpdatedJustNow", NUM);
                }
                if (diff < 60) {
                    return formatPluralString("UpdatedMinutes", diff, new Object[0]);
                }
                return formatString("LocationUpdatedFormatted", NUM, formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2))));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("LocationUpdatedFormatted", NUM, formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2))));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("LocationUpdatedFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            } else {
                return formatString("LocationUpdatedFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int time) {
        int hours = (time / 60) / 60;
        int time2 = time - ((hours * 60) * 60);
        int minutes = time2 / 60;
        int time3 = time2 - (minutes * 60);
        int i = 1;
        if (hours != 0) {
            Object[] objArr = new Object[1];
            if (minutes <= 30) {
                i = 0;
            }
            objArr[0] = Integer.valueOf(i + hours);
            return String.format("%dh", objArr);
        } else if (minutes != 0) {
            Object[] objArr2 = new Object[1];
            if (time3 <= 30) {
                i = 0;
            }
            objArr2[0] = Integer.valueOf(i + minutes);
            return String.format("%d", objArr2);
        } else {
            return String.format("%d", new Object[]{Integer.valueOf(time3)});
        }
    }

    public static String formatDateOnline(long date, boolean[] madeShorter) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            int hour = rightNow.get(11);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            int dateHour = rightNow.get(11);
            if (dateDay == day && year == dateYear) {
                return formatString("LastSeenFormatted", NUM, formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2))));
            } else if (dateDay + 1 == day && year == dateYear) {
                if (madeShorter != null) {
                    madeShorter[0] = true;
                    if (hour > 6 || dateHour <= 18 || !is24HourFormat) {
                        return formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
                    }
                    return formatString("LastSeenFormatted", NUM, getInstance().formatterDay.format(new Date(date2)));
                }
                return formatString("LastSeenFormatted", NUM, formatString("YesterdayAtFormatted", NUM, getInstance().formatterDay.format(new Date(date2))));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("LastSeenDateFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            } else {
                return formatString("LastSeenDateFormatted", NUM, formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String format, String defaultFormat) {
        if (format == null || format.length() == 0) {
            format = defaultFormat;
        }
        try {
            return FastDateFormat.getInstance(format, locale);
        } catch (Exception e) {
            return FastDateFormat.getInstance(defaultFormat, locale);
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
            r7 = 2131629360(0x7f0e1530, float:1.8886039E38)
            java.lang.String r8 = "formatterMonthYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterMonthYear = r7
            r7 = 2131629358(0x7f0e152e, float:1.8886035E38)
            java.lang.String r8 = "formatterMonth"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd MMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterDayMonth = r7
            r7 = 2131629366(0x7f0e1536, float:1.888605E38)
            java.lang.String r8 = "formatterYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYear = r7
            r7 = 2131629367(0x7f0e1537, float:1.8886053E38)
            java.lang.String r8 = "formatterYearMax"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYearMax = r7
            r7 = 2131629326(0x7f0e150e, float:1.888597E38)
            java.lang.String r8 = "chatDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatDate = r7
            r7 = 2131629327(0x7f0e150f, float:1.8885972E38)
            java.lang.String r8 = "chatFullDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatFullDate = r7
            r7 = 2131629364(0x7f0e1534, float:1.8886047E38)
            java.lang.String r8 = "formatterWeek"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "EEE"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterWeek = r7
            r7 = 2131629365(0x7f0e1535, float:1.8886049E38)
            java.lang.String r8 = "formatterWeekLong"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "EEEE"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterWeekLong = r7
            r7 = 2131629350(0x7f0e1526, float:1.8886018E38)
            java.lang.String r8 = "formatDateSchedule"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleDay = r7
            r7 = 2131629351(0x7f0e1527, float:1.888602E38)
            java.lang.String r8 = "formatDateScheduleYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleYear = r7
            java.lang.String r7 = r1.toLowerCase()
            boolean r4 = r7.equals(r4)
            if (r4 != 0) goto L_0x013a
            java.lang.String r4 = r1.toLowerCase()
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0137
            goto L_0x013a
        L_0x0137:
            java.util.Locale r2 = java.util.Locale.US
            goto L_0x013b
        L_0x013a:
            r2 = r0
        L_0x013b:
            boolean r4 = is24HourFormat
            if (r4 == 0) goto L_0x0145
            r4 = 2131629357(0x7f0e152d, float:1.8886033E38)
            java.lang.String r7 = "formatterDay24H"
            goto L_0x014a
        L_0x0145:
            r4 = 2131629356(0x7f0e152c, float:1.888603E38)
            java.lang.String r7 = "formatterDay12H"
        L_0x014a:
            java.lang.String r4 = r9.getStringInternal(r7, r4)
            boolean r7 = is24HourFormat
            if (r7 == 0) goto L_0x0155
            java.lang.String r7 = "HH:mm"
            goto L_0x0157
        L_0x0155:
            java.lang.String r7 = "h:mm a"
        L_0x0157:
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r2, r4, r7)
            r9.formatterDay = r2
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0167
            r2 = 2131629363(0x7f0e1533, float:1.8886045E38)
            java.lang.String r4 = "formatterStats24H"
            goto L_0x016c
        L_0x0167:
            r2 = 2131629362(0x7f0e1532, float:1.8886043E38)
            java.lang.String r4 = "formatterStats12H"
        L_0x016c:
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            boolean r4 = is24HourFormat
            java.lang.String r7 = "MMM dd yyyy, HH:mm"
            java.lang.String r8 = "MMM dd yyyy, h:mm a"
            if (r4 == 0) goto L_0x017a
            r4 = r7
            goto L_0x017b
        L_0x017a:
            r4 = r8
        L_0x017b:
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r4)
            r9.formatterStats = r2
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x018b
            r2 = 2131629353(0x7f0e1529, float:1.8886025E38)
            java.lang.String r4 = "formatterBannedUntil24H"
            goto L_0x0190
        L_0x018b:
            r2 = 2131629352(0x7f0e1528, float:1.8886022E38)
            java.lang.String r4 = "formatterBannedUntil12H"
        L_0x0190:
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            boolean r4 = is24HourFormat
            if (r4 == 0) goto L_0x0199
            goto L_0x019a
        L_0x0199:
            r7 = r8
        L_0x019a:
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r7)
            r9.formatterBannedUntil = r2
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x01aa
            r2 = 2131629355(0x7f0e152b, float:1.8886029E38)
            java.lang.String r4 = "formatterBannedUntilThisYear24H"
            goto L_0x01af
        L_0x01aa:
            r2 = 2131629354(0x7f0e152a, float:1.8886027E38)
            java.lang.String r4 = "formatterBannedUntilThisYear12H"
        L_0x01af:
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            boolean r4 = is24HourFormat
            if (r4 == 0) goto L_0x01ba
            java.lang.String r4 = "MMM dd, HH:mm"
            goto L_0x01bc
        L_0x01ba:
            java.lang.String r4 = "MMM dd, h:mm a"
        L_0x01bc:
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r4)
            r9.formatterBannedUntilThisYear = r2
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r4 = 2131628209(0x7f0e10b1, float:1.8883704E38)
            java.lang.String r7 = "SendTodayAt"
            java.lang.String r4 = r9.getStringInternal(r7, r4)
            java.lang.String r7 = "'Send today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r7)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 2131628183(0x7f0e1097, float:1.8883651E38)
            java.lang.String r4 = "SendDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Send on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r2[r5] = r3
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 2131628184(0x7f0e1098, float:1.8883654E38)
            java.lang.String r4 = "SendDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Send on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r2[r6] = r3
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 3
            r4 = 2131627887(0x7f0e0f6f, float:1.8883051E38)
            java.lang.String r5 = "RemindTodayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Remind today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 4
            r4 = 2131627885(0x7f0e0f6d, float:1.8883047E38)
            java.lang.String r5 = "RemindDayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Remind on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 5
            r4 = 2131627886(0x7f0e0f6e, float:1.888305E38)
            java.lang.String r5 = "RemindDayYearAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Remind on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 6
            r4 = 2131628422(0x7f0e1186, float:1.8884136E38)
            java.lang.String r5 = "StartTodayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Start today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 7
            r4 = 2131628414(0x7f0e117e, float:1.888412E38)
            java.lang.String r5 = "StartDayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Start on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 8
            r4 = 2131628415(0x7f0e117f, float:1.8884122E38)
            java.lang.String r5 = "StartDayYearAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Start on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 9
            r4 = 2131628420(0x7f0e1184, float:1.8884132E38)
            java.lang.String r5 = "StartShortTodayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Today,' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 10
            r4 = 2131628418(0x7f0e1182, float:1.8884128E38)
            java.lang.String r5 = "StartShortDayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "MMM d',' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 11
            r4 = 2131628419(0x7f0e1183, float:1.888413E38)
            java.lang.String r5 = "StartShortDayYearAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "MMM d yyyy, HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 12
            r4 = 2131628432(0x7f0e1190, float:1.8884157E38)
            java.lang.String r5 = "StartsTodayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Starts today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 13
            r4 = 2131628430(0x7f0e118e, float:1.8884152E38)
            java.lang.String r5 = "StartsDayAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Starts on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            org.telegram.messenger.time.FastDateFormat[] r2 = r9.formatterScheduleSend
            r3 = 14
            r4 = 2131628431(0x7f0e118f, float:1.8884154E38)
            java.lang.String r5 = "StartsDayYearAt"
            java.lang.String r4 = r9.getStringInternal(r5, r4)
            java.lang.String r5 = "'Starts on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r4 = r9.createFormatter(r0, r4, r5)
            r2[r3] = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.recreateFormatters():void");
    }

    public static boolean isRTLCharacter(char ch) {
        return Character.getDirectionality(ch) == 1 || Character.getDirectionality(ch) == 2 || Character.getDirectionality(ch) == 16 || Character.getDirectionality(ch) == 17;
    }

    public static String formatStartsTime(long date, int type) {
        return formatStartsTime(date, type, true);
    }

    public static String formatStartsTime(long date, int type, boolean needToday) {
        int num;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        int currentDay = calendar.get(6);
        calendar.setTimeInMillis(1000 * date);
        int selectedYear = calendar.get(1);
        int selectedDay = calendar.get(6);
        if (currentYear != selectedYear) {
            num = 2;
        } else if (!needToday || selectedDay != currentDay) {
            num = 1;
        } else {
            num = 0;
        }
        if (type == 1) {
            num += 3;
        } else if (type == 2) {
            num += 6;
        } else if (type == 3) {
            num += 9;
        } else if (type == 4) {
            num += 12;
        }
        return getInstance().formatterScheduleSend[num].format(calendar.getTimeInMillis());
    }

    public static String formatSectionDate(long date) {
        return formatYearMont(date, false);
    }

    public static String formatYearMont(long date, boolean alwaysShowYear) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateYear = rightNow.get(1);
            int month = rightNow.get(2);
            String[] months = {getString("January", NUM), getString("February", NUM), getString("March", NUM), getString("April", NUM), getString("May", NUM), getString("June", NUM), getString("July", NUM), getString("August", NUM), getString("September", NUM), getString("October", NUM), getString("November", NUM), getString("December", NUM)};
            if (year == dateYear && !alwaysShowYear) {
                return months[month];
            }
            return months[month] + " " + dateYear;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            if (year == rightNow.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(date2));
            }
            return getInstance().formatterBannedUntil.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            if (Math.abs(System.currentTimeMillis() - date2) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(date2));
            }
            int dayDiff = dateDay - day;
            if (dayDiff != 0) {
                if (dayDiff != -1 || System.currentTimeMillis() - date2 >= 28800000) {
                    if (dayDiff <= -7 || dayDiff > -1) {
                        return getInstance().formatterDayMonth.format(new Date(date2));
                    }
                    return getInstance().formatterWeek.format(new Date(date2));
                }
            }
            return getInstance().formatterDay.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int number, int[] rounded) {
        StringBuilder K = new StringBuilder();
        int lastDec = 0;
        while (number / 1000 > 0) {
            K.append("K");
            lastDec = (number % 1000) / 100;
            number /= 1000;
        }
        if (rounded != null) {
            double d = (double) number;
            double d2 = (double) lastDec;
            Double.isNaN(d2);
            Double.isNaN(d);
            double value = d + (d2 / 10.0d);
            for (int a = 0; a < K.length(); a++) {
                value *= 1000.0d;
            }
            rounded[0] = (int) value;
        }
        if (lastDec == 0 || K.length() <= 0) {
            if (K.length() == 2) {
                return String.format(Locale.US, "%dM", new Object[]{Integer.valueOf(number)});
            }
            return String.format(Locale.US, "%d%s", new Object[]{Integer.valueOf(number), K.toString()});
        } else if (K.length() == 2) {
            return String.format(Locale.US, "%d.%dM", new Object[]{Integer.valueOf(number), Integer.valueOf(lastDec)});
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(number), Integer.valueOf(lastDec), K.toString()});
        }
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user) {
        return formatUserStatus(currentAccount, user, (boolean[]) null);
    }

    public static String formatJoined(long date) {
        String format;
        long date2 = date * 1000;
        try {
            if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                format = formatString("formatDateAtTime", NUM, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                format = formatString("formatDateAtTime", NUM, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
            return formatString("ChannelOtherSubscriberJoined", NUM, format);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatImportedDate(long date) {
        try {
            Date dt = new Date(date * 1000);
            return String.format("%1$s, %2$s", new Object[]{getInstance().formatterYear.format(dt), getInstance().formatterDay.format(dt)});
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user, boolean[] isOnline) {
        return formatUserStatus(currentAccount, user, isOnline, (boolean[]) null);
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user, boolean[] isOnline, boolean[] madeShorter) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TLRPC.TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TLRPC.TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TLRPC.TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(currentAccount).onlinePrivacy.containsKey(Long.valueOf(user.id))) {
            if (isOnline != null) {
                isOnline[0] = true;
            }
            return getString("Online", NUM);
        } else if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", NUM);
        } else {
            if (user.status.expires > ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                if (isOnline != null) {
                    isOnline[0] = true;
                }
                return getString("Online", NUM);
            } else if (user.status.expires == -1) {
                return getString("Invisible", NUM);
            } else {
                if (user.status.expires == -100) {
                    return getString("Lately", NUM);
                }
                if (user.status.expires == -101) {
                    return getString("WithinAWeek", NUM);
                }
                if (user.status.expires == -102) {
                    return getString("WithinAMonth", NUM);
                }
                return formatDateOnline((long) user.status.expires, madeShorter);
            }
        }
    }

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC.TL_langPackDifference difference, int currentAccount) {
        if (this.currentLocaleInfo != null) {
            String langCode = difference.lang_code.replace('-', '_').toLowerCase();
            if (langCode.equals(this.currentLocaleInfo.shortName) || langCode.equals(this.currentLocaleInfo.baseLangCode)) {
                m80x926058b2(this.currentLocaleInfo, difference, currentAccount);
            }
        }
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void m80x926058b2(LocaleInfo localeInfo, TLRPC.TL_langPackDifference difference, int currentAccount) {
        int type;
        File finalFile;
        HashMap<String, String> values;
        LocaleInfo localeInfo2 = localeInfo;
        TLRPC.TL_langPackDifference tL_langPackDifference = difference;
        if (tL_langPackDifference != null && !tL_langPackDifference.strings.isEmpty() && localeInfo2 != null && !localeInfo.isLocal()) {
            String langCode = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (langCode.equals(localeInfo2.shortName)) {
                type = 0;
            } else if (langCode.equals(localeInfo2.baseLangCode)) {
                type = 1;
            } else {
                type = -1;
            }
            if (type != -1) {
                if (type == 0) {
                    finalFile = localeInfo.getPathToFile();
                } else {
                    finalFile = localeInfo.getPathToBaseFile();
                }
                try {
                    if (tL_langPackDifference.from_version == 0) {
                        values = new HashMap<>();
                    } else {
                        values = getLocaleFileStrings(finalFile, true);
                    }
                    for (int a = 0; a < tL_langPackDifference.strings.size(); a++) {
                        TLRPC.LangPackString string = tL_langPackDifference.strings.get(a);
                        if (string instanceof TLRPC.TL_langPackString) {
                            values.put(string.key, escapeString(string.value));
                        } else if (string instanceof TLRPC.TL_langPackStringPluralized) {
                            String str = "";
                            values.put(string.key + "_zero", string.zero_value != null ? escapeString(string.zero_value) : str);
                            values.put(string.key + "_one", string.one_value != null ? escapeString(string.one_value) : str);
                            values.put(string.key + "_two", string.two_value != null ? escapeString(string.two_value) : str);
                            values.put(string.key + "_few", string.few_value != null ? escapeString(string.few_value) : str);
                            values.put(string.key + "_many", string.many_value != null ? escapeString(string.many_value) : str);
                            String str2 = string.key + "_other";
                            if (string.other_value != null) {
                                str = escapeString(string.other_value);
                            }
                            values.put(str2, str);
                        } else if (string instanceof TLRPC.TL_langPackStringDeleted) {
                            values.remove(string.key);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED != 0) {
                        FileLog.d("save locale file to " + finalFile);
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile));
                    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    writer.write("<resources>\n");
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        writer.write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[]{entry.getKey(), entry.getValue()}));
                    }
                    writer.write("</resources>");
                    writer.close();
                    boolean hasBase = localeInfo.hasBaseLang();
                    HashMap<String, String> valuesToSet = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    if (hasBase) {
                        valuesToSet.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                    AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda8(this, type, localeInfo, difference, valuesToSet));
                } catch (Exception e) {
                }
            }
        }
    }

    /* renamed from: lambda$saveRemoteLocaleStrings$5$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m86xa697d88(int type, LocaleInfo localeInfo, TLRPC.TL_langPackDifference difference, HashMap valuesToSet) {
        String[] args;
        Locale newLocale;
        if (type == 0) {
            localeInfo.version = difference.version;
        } else {
            localeInfo.baseVersion = difference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    args = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    args = localeInfo.baseLangCode.split("_");
                } else {
                    args = localeInfo.shortName.split("_");
                }
                if (args.length == 1) {
                    newLocale = new Locale(args[0]);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putString("language", localeInfo.getKey());
                editor.commit();
                this.localeValues = valuesToSet;
                this.currentLocale = newLocale;
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
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int currentAccount) {
        loadRemoteLanguages(currentAccount, true);
    }

    public void loadRemoteLanguages(int currentAccount, boolean applyCurrent) {
        if (!this.loadingRemoteLanguages) {
            this.loadingRemoteLanguages = true;
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_langpack_getLanguages(), new LocaleController$$ExternalSyntheticLambda6(this, applyCurrent, currentAccount), 8);
        }
    }

    /* renamed from: lambda$loadRemoteLanguages$7$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m83xCLASSNAMEd4(boolean applyCurrent, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda15(this, response, applyCurrent, currentAccount));
        }
    }

    /* renamed from: lambda$loadRemoteLanguages$6$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m82x35812ed3(TLObject response, boolean applyCurrent, int currentAccount) {
        this.loadingRemoteLanguages = false;
        TLRPC.Vector res = (TLRPC.Vector) response;
        int size = this.remoteLanguages.size();
        for (int a = 0; a < size; a++) {
            this.remoteLanguages.get(a).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = res.objects.size();
        for (int a2 = 0; a2 < size2; a2++) {
            TLRPC.TL_langPackLanguage language = (TLRPC.TL_langPackLanguage) res.objects.get(a2);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + language.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = language.name;
            localeInfo.name = language.native_name;
            localeInfo.shortName = language.lang_code.replace('-', '_').toLowerCase();
            if (language.base_lang_code != null) {
                localeInfo.baseLangCode = language.base_lang_code.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = language.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = language.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = a2;
            LocaleInfo existing = getLanguageFromDict(localeInfo.getKey());
            if (existing == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                existing.nameEnglish = localeInfo.nameEnglish;
                existing.name = localeInfo.name;
                existing.baseLangCode = localeInfo.baseLangCode;
                existing.pluralLangCode = localeInfo.pluralLangCode;
                existing.pathToFile = localeInfo.pathToFile;
                existing.serverIndex = localeInfo.serverIndex;
                localeInfo = existing;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int a3 = 0;
        while (a3 < this.remoteLanguages.size()) {
            LocaleInfo info = this.remoteLanguages.get(a3);
            if (info.serverIndex == Integer.MAX_VALUE && info != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + info.getKey());
                }
                this.remoteLanguages.remove(a3);
                this.remoteLanguagesDict.remove(info.getKey());
                this.languages.remove(info);
                this.languagesDict.remove(info.getKey());
                a3--;
            }
            a3++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        if (applyCurrent) {
            applyLanguage(this.currentLocaleInfo, true, false, currentAccount);
        }
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, String langCode, boolean force, int currentAccount) {
        if (localeInfo == null) {
            return;
        }
        if (localeInfo.isRemote() || localeInfo.isUnofficial()) {
            if (localeInfo.hasBaseLang() && (langCode == null || langCode.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion == 0 || force) {
                    TLRPC.TL_langpack_getLangPack req = new TLRPC.TL_langpack_getLangPack();
                    req.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req, new LocaleController$$ExternalSyntheticLambda2(this, localeInfo, currentAccount), 8);
                } else if (localeInfo.hasBaseLang()) {
                    TLRPC.TL_langpack_getDifference req2 = new TLRPC.TL_langpack_getDifference();
                    req2.from_version = localeInfo.baseVersion;
                    req2.lang_code = localeInfo.getBaseLangCode();
                    req2.lang_pack = "";
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req2, new LocaleController$$ExternalSyntheticLambda5(this, localeInfo, currentAccount), 8);
                }
            }
            if (langCode != null && !langCode.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version == 0 || force) {
                for (int a = 0; a < 4; a++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TLRPC.TL_langpack_getLangPack req3 = new TLRPC.TL_langpack_getLangPack();
                req3.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(currentAccount).sendRequest(req3, new LocaleController$$ExternalSyntheticLambda4(this, localeInfo, currentAccount), 8);
                return;
            }
            TLRPC.TL_langpack_getDifference req4 = new TLRPC.TL_langpack_getDifference();
            req4.from_version = localeInfo.version;
            req4.lang_code = localeInfo.getLangCode();
            req4.lang_pack = "";
            ConnectionsManager.getInstance(currentAccount).sendRequest(req4, new LocaleController$$ExternalSyntheticLambda3(this, localeInfo, currentAccount), 8);
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$9$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m81x1var_b3(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda14(this, localeInfo, response, currentAccount));
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$11$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m75x8b2f7be4(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda11(this, localeInfo, response, currentAccount));
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$13$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m77xa46fd1e6(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda12(this, localeInfo, response, currentAccount));
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$15$org-telegram-messenger-LocaleController  reason: not valid java name */
    public /* synthetic */ void m79xbdb027e8(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocaleController$$ExternalSyntheticLambda13(this, localeInfo, response, currentAccount));
        }
    }

    public String getTranslitString(String src) {
        return getTranslitString(src, true, false);
    }

    public String getTranslitString(String src, boolean onlyEnglish) {
        return getTranslitString(src, true, onlyEnglish);
    }

    public String getTranslitString(String src, boolean ru, boolean onlyEnglish) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        Object obj7;
        Object obj8;
        Object obj9;
        if (src == null) {
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
        StringBuilder dst = new StringBuilder(src.length());
        int len = src.length();
        boolean upperCase = false;
        for (int a = 0; a < len; a++) {
            String ch = src.substring(a, a + 1);
            if (onlyEnglish) {
                String lower = ch.toLowerCase();
                upperCase = !ch.equals(lower);
                ch = lower;
            }
            String tch = this.translitChars.get(ch);
            if (tch == null && ru) {
                tch = this.ruTranslitChars.get(ch);
            }
            if (tch != null) {
                if (onlyEnglish && upperCase) {
                    if (tch.length() > 1) {
                        tch = tch.substring(0, 1).toUpperCase() + tch.substring(1);
                    } else {
                        tch = tch.toUpperCase();
                    }
                }
                dst.append(tch);
            } else {
                if (onlyEnglish) {
                    char c = ch.charAt(0);
                    if ((c < 'a' || c > 'z' || c < '0' || c > '9') && c != ' ' && c != '\'' && c != ',' && c != '.' && c != '&' && c != '-' && c != '/') {
                        return null;
                    }
                    if (upperCase) {
                        ch = ch.toUpperCase();
                    }
                }
                dst.append(ch);
            }
        }
        String str = src;
        return dst.toString();
    }

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0 || count == 1) {
                return 2;
            }
            return 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            return 0;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int count) {
            if (count >= 0 && count <= 1) {
                return 2;
            }
            if (count < 2 || count > 10) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (rem100 == 1) {
                return 2;
            }
            if (rem100 == 2) {
                return 4;
            }
            if (rem100 < 3 || rem100 > 4) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count == 0) {
                return 8;
            }
            if (rem100 < 1 || rem100 > 19) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (count == 1) {
                return 2;
            }
            if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                return 8;
            }
            if (rem10 >= 0 && rem10 <= 1) {
                return 16;
            }
            if (rem10 >= 5 && rem10 <= 9) {
                return 16;
            }
            if (rem100 < 12 || rem100 > 14) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int count) {
            return count == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int count) {
            return 0;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count == 0) {
                return 8;
            }
            if (rem100 >= 2 && rem100 <= 10) {
                return 8;
            }
            if (rem100 < 11 || rem100 > 19) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count % 10 != 1 || count == 11) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && (rem100 < 11 || rem100 > 19)) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 9) {
                return 0;
            }
            if (rem100 < 11 || rem100 > 19) {
                return 8;
            }
            return 0;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count % 10 != 1 || count % 100 == 11) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            return 0;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int count) {
            if (count < 0 || count >= 2) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count < 2 || count > 4) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && rem100 != 11) {
                return 2;
            }
            if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                return 8;
            }
            if (rem10 == 0) {
                return 16;
            }
            if (rem10 >= 5 && rem10 <= 9) {
                return 16;
            }
            if (rem100 < 11 || rem100 > 14) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_Serbian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && rem100 != 11) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 4) {
                return 0;
            }
            if (rem100 < 12 || rem100 > 14) {
                return 8;
            }
            return 0;
        }
    }

    public static class PluralRules_Arabic extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (rem100 >= 3 && rem100 <= 10) {
                return 8;
            }
            if (rem100 < 11 || rem100 > 99) {
                return 0;
            }
            return 16;
        }
    }

    public static String addNbsp(String src) {
        return src.replace(' ', 160);
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
            boolean z = true;
            if (SharedConfig.distanceSystemType == 0) {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager != null) {
                        String country = telephonyManager.getSimCountryIso().toUpperCase();
                        if (!"US".equals(country) && !"GB".equals(country) && !"MM".equals(country)) {
                            if (!"LR".equals(country)) {
                                z = false;
                            }
                        }
                        useImperialSystemType = Boolean.valueOf(z);
                    }
                } catch (Exception e) {
                    useImperialSystemType = false;
                    FileLog.e((Throwable) e);
                }
            } else {
                if (SharedConfig.distanceSystemType != 2) {
                    z = false;
                }
                useImperialSystemType = Boolean.valueOf(z);
            }
        }
    }

    public static String formatDistance(float distance, int type) {
        return formatDistance(distance, type, (Boolean) null);
    }

    public static String formatDistance(float distance, int type, Boolean useImperial) {
        String arg;
        String arg2;
        ensureImperialSystemInit();
        if ((useImperial != null && useImperial.booleanValue()) || (useImperial == null && useImperialSystemType.booleanValue())) {
            float distance2 = distance * 3.28084f;
            if (distance2 < 1000.0f) {
                switch (type) {
                    case 0:
                        return formatString("FootsAway", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance2))}));
                    case 1:
                        return formatString("FootsFromYou", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance2))}));
                    default:
                        return formatString("FootsShort", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance2))}));
                }
            } else {
                if (distance2 % 5280.0f == 0.0f) {
                    arg2 = String.format("%d", new Object[]{Integer.valueOf((int) (distance2 / 5280.0f))});
                } else {
                    arg2 = String.format("%.2f", new Object[]{Float.valueOf(distance2 / 5280.0f)});
                }
                switch (type) {
                    case 0:
                        return formatString("MilesAway", NUM, arg2);
                    case 1:
                        return formatString("MilesFromYou", NUM, arg2);
                    default:
                        return formatString("MilesShort", NUM, arg2);
                }
            }
        } else if (distance < 1000.0f) {
            switch (type) {
                case 0:
                    return formatString("MetersAway2", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance))}));
                case 1:
                    return formatString("MetersFromYou2", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance))}));
                default:
                    return formatString("MetersShort", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance))}));
            }
        } else {
            if (distance % 1000.0f == 0.0f) {
                arg = String.format("%d", new Object[]{Integer.valueOf((int) (distance / 1000.0f))});
            } else {
                arg = String.format("%.2f", new Object[]{Float.valueOf(distance / 1000.0f)});
            }
            switch (type) {
                case 0:
                    return formatString("KMetersAway2", NUM, arg);
                case 1:
                    return formatString("KMetersFromYou2", NUM, arg);
                default:
                    return formatString("KMetersShort", NUM, arg);
            }
        }
    }
}
