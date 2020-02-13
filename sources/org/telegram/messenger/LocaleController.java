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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

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
    public FastDateFormat formatterScheduleDay;
    public FastDateFormat[] formatterScheduleSend = new FastDateFormat[6];
    public FastDateFormat formatterScheduleYear;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
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
            ApplicationLoader.applicationHandler.post(new Runnable() {
                public final void run() {
                    LocaleController.TimeZoneChangedReceiver.this.lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver() {
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
            boolean isEmpty = TextUtils.isEmpty(this.pluralLangCode);
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    LocaleController.this.lambda$new$0$LocaleController();
                }
            });
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
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocaleController.this.lambda$new$1$LocaleController();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$LocaleController() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public /* synthetic */ void lambda$new$1$LocaleController() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public LocaleInfo getLanguageFromDict(String str) {
        if (str == null) {
            return null;
        }
        return this.languagesDict.get(str.toLowerCase().replace("-", "_"));
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

    public void reloadCurrentRemoteLocale(int i, String str) {
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
        applyRemoteLanguage(this.currentLocaleInfo, str, true, i);
    }

    public void checkUpdateForCurrentRemoteLocale(int i, int i2, int i3) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null) {
            return;
        }
        if (localeInfo == null || localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
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

    /* JADX WARNING: Removed duplicated region for block: B:65:0x00bf A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c0 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c1 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c2 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c3 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c4 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c5 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c6 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c7 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c8 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c9 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ca A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cb A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getLocaleAlias(java.lang.String r16) {
        /*
            r0 = r16
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            int r3 = r16.hashCode()
            r4 = 3325(0xcfd, float:4.66E-42)
            java.lang.String r5 = "fil"
            java.lang.String r6 = "yi"
            java.lang.String r7 = "tl"
            java.lang.String r8 = "no"
            java.lang.String r9 = "nb"
            java.lang.String r10 = "jw"
            java.lang.String r11 = "jv"
            java.lang.String r12 = "ji"
            java.lang.String r13 = "iw"
            java.lang.String r14 = "in"
            java.lang.String r15 = "id"
            java.lang.String r2 = "he"
            if (r3 == r4) goto L_0x00b3
            r4 = 3355(0xd1b, float:4.701E-42)
            if (r3 == r4) goto L_0x00ab
            r4 = 3365(0xd25, float:4.715E-42)
            if (r3 == r4) goto L_0x00a3
            r4 = 3374(0xd2e, float:4.728E-42)
            if (r3 == r4) goto L_0x009b
            r4 = 3391(0xd3f, float:4.752E-42)
            if (r3 == r4) goto L_0x0093
            r4 = 3508(0xdb4, float:4.916E-42)
            if (r3 == r4) goto L_0x008a
            r4 = 3521(0xdc1, float:4.934E-42)
            if (r3 == r4) goto L_0x0082
            r4 = 3704(0xe78, float:5.19E-42)
            if (r3 == r4) goto L_0x007a
            r4 = 3856(0xvar_, float:5.403E-42)
            if (r3 == r4) goto L_0x0071
            r4 = 101385(0x18CLASSNAME, float:1.4207E-40)
            if (r3 == r4) goto L_0x0068
            r4 = 3404(0xd4c, float:4.77E-42)
            if (r3 == r4) goto L_0x005f
            r4 = 3405(0xd4d, float:4.771E-42)
            if (r3 == r4) goto L_0x0056
            goto L_0x00bb
        L_0x0056:
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x00bb
            r0 = 2
            goto L_0x00bc
        L_0x005f:
            boolean r0 = r0.equals(r11)
            if (r0 == 0) goto L_0x00bb
            r0 = 8
            goto L_0x00bc
        L_0x0068:
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x00bb
            r0 = 10
            goto L_0x00bc
        L_0x0071:
            boolean r0 = r0.equals(r6)
            if (r0 == 0) goto L_0x00bb
            r0 = 11
            goto L_0x00bc
        L_0x007a:
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00bb
            r0 = 4
            goto L_0x00bc
        L_0x0082:
            boolean r0 = r0.equals(r8)
            if (r0 == 0) goto L_0x00bb
            r0 = 3
            goto L_0x00bc
        L_0x008a:
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x00bb
            r0 = 9
            goto L_0x00bc
        L_0x0093:
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00bb
            r0 = 5
            goto L_0x00bc
        L_0x009b:
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x00bb
            r0 = 1
            goto L_0x00bc
        L_0x00a3:
            boolean r0 = r0.equals(r14)
            if (r0 == 0) goto L_0x00bb
            r0 = 0
            goto L_0x00bc
        L_0x00ab:
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x00bb
            r0 = 6
            goto L_0x00bc
        L_0x00b3:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x00bb
            r0 = 7
            goto L_0x00bc
        L_0x00bb:
            r0 = -1
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
            }
        L_0x00bf:
            return r1
        L_0x00c0:
            return r12
        L_0x00c1:
            return r7
        L_0x00c2:
            return r8
        L_0x00c3:
            return r10
        L_0x00c4:
            return r13
        L_0x00c5:
            return r14
        L_0x00c6:
            return r6
        L_0x00c7:
            return r5
        L_0x00c8:
            return r9
        L_0x00c9:
            return r11
        L_0x00ca:
            return r2
        L_0x00cb:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getLocaleAlias(java.lang.String):java.lang.String");
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
                                        languageFromDict.shortName = str3.toLowerCase();
                                        languageFromDict.pluralLangCode = languageFromDict.shortName;
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
                if (!this.remoteLanguagesDict.containsKey(createWithString4.getKey()) && createWithString4 != null) {
                    this.remoteLanguages.add(createWithString4);
                    this.remoteLanguagesDict.put(createWithString4.getKey(), createWithString4);
                }
            }
        }
        String string3 = sharedPreferences.getString("unofficial", (String) null);
        if (!TextUtils.isEmpty(string3)) {
            for (String createWithString5 : string3.split("&")) {
                LocaleInfo createWithString6 = LocaleInfo.createWithString(createWithString5);
                createWithString6.shortName = createWithString6.shortName.replace("-", "_");
                if (createWithString6 != null) {
                    this.unofficialLanguages.add(createWithString6);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x00da A[SYNTHETIC, Splitter:B:61:0x00da] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00ea A[SYNTHETIC, Splitter:B:68:0x00ea] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, java.lang.String> getLocaleFileStrings(java.io.File r13, boolean r14) {
        /*
            r12 = this;
            r0 = 0
            r12.reloadLastFile = r0
            r1 = 1
            r2 = 0
            boolean r3 = r13.exists()     // Catch:{ Exception -> 0x00d2 }
            if (r3 != 0) goto L_0x0011
            java.util.HashMap r13 = new java.util.HashMap     // Catch:{ Exception -> 0x00d2 }
            r13.<init>()     // Catch:{ Exception -> 0x00d2 }
            return r13
        L_0x0011:
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x00d2 }
            r3.<init>()     // Catch:{ Exception -> 0x00d2 }
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ Exception -> 0x00d2 }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00d2 }
            r5.<init>(r13)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r13 = "UTF-8"
            r4.setInput(r5, r13)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            int r13 = r4.getEventType()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x002b:
            if (r13 == r1) goto L_0x00c1
            r9 = 2
            if (r13 != r9) goto L_0x0040
            java.lang.String r13 = r4.getName()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            int r7 = r4.getAttributeCount()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r7 <= 0) goto L_0x003e
            java.lang.String r6 = r4.getAttributeValue(r0)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
        L_0x003e:
            r7 = r13
            goto L_0x009b
        L_0x0040:
            r9 = 4
            if (r13 != r9) goto L_0x0095
            if (r6 == 0) goto L_0x009b
            java.lang.String r13 = r4.getText()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r13 == 0) goto L_0x0093
            java.lang.String r13 = r13.trim()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r8 = "&lt;"
            java.lang.String r9 = "<"
            if (r14 == 0) goto L_0x0072
            java.lang.String r13 = r13.replace(r9, r8)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r8 = ">"
            java.lang.String r9 = "&gt;"
            java.lang.String r13 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r8 = "'"
            java.lang.String r9 = "\\'"
            java.lang.String r13 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r8 = "& "
            java.lang.String r9 = "&amp; "
            java.lang.String r13 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            goto L_0x0093
        L_0x0072:
            java.lang.String r10 = "\\n"
            java.lang.String r11 = "\n"
            java.lang.String r13 = r13.replace(r10, r11)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r10 = "\\"
            java.lang.String r11 = ""
            java.lang.String r13 = r13.replace(r10, r11)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            java.lang.String r8 = r13.replace(r8, r9)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            boolean r9 = r12.reloadLastFile     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r9 != 0) goto L_0x009b
            boolean r13 = r8.equals(r13)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r13 != 0) goto L_0x009b
            r12.reloadLastFile = r1     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            goto L_0x009b
        L_0x0093:
            r8 = r13
            goto L_0x009b
        L_0x0095:
            r9 = 3
            if (r13 != r9) goto L_0x009b
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x009b:
            if (r7 == 0) goto L_0x00bb
            java.lang.String r13 = "string"
            boolean r13 = r7.equals(r13)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r13 == 0) goto L_0x00bb
            if (r8 == 0) goto L_0x00bb
            if (r6 == 0) goto L_0x00bb
            int r13 = r8.length()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r13 == 0) goto L_0x00bb
            int r13 = r6.length()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            if (r13 == 0) goto L_0x00bb
            r3.put(r6, r8)     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            r6 = r2
            r7 = r6
            r8 = r7
        L_0x00bb:
            int r13 = r4.next()     // Catch:{ Exception -> 0x00cc, all -> 0x00ca }
            goto L_0x002b
        L_0x00c1:
            r5.close()     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00c9
        L_0x00c5:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x00c9:
            return r3
        L_0x00ca:
            r13 = move-exception
            goto L_0x00e8
        L_0x00cc:
            r13 = move-exception
            r2 = r5
            goto L_0x00d3
        L_0x00cf:
            r13 = move-exception
            r5 = r2
            goto L_0x00e8
        L_0x00d2:
            r13 = move-exception
        L_0x00d3:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ all -> 0x00cf }
            r12.reloadLastFile = r1     // Catch:{ all -> 0x00cf }
            if (r2 == 0) goto L_0x00e2
            r2.close()     // Catch:{ Exception -> 0x00de }
            goto L_0x00e2
        L_0x00de:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x00e2:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            return r13
        L_0x00e8:
            if (r5 == 0) goto L_0x00f2
            r5.close()     // Catch:{ Exception -> 0x00ee }
            goto L_0x00f2
        L_0x00ee:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x00f2:
            goto L_0x00f4
        L_0x00f3:
            throw r13
        L_0x00f4:
            goto L_0x00f3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.getLocaleFileStrings(java.io.File, boolean):java.util.HashMap");
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        applyLanguage(localeInfo, z, z2, false, false, i);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, boolean z3, boolean z4, int i) {
        String[] strArr;
        Locale locale;
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
                    FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
                }
                if (z2) {
                    AndroidUtilities.runOnUIThread(new Runnable(localeInfo, i) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            LocaleController.this.lambda$applyLanguage$2$LocaleController(this.f$1, this.f$2);
                        }
                    });
                } else {
                    applyRemoteLanguage(localeInfo, (String) null, true, i);
                }
            }
            try {
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
                if (z) {
                    this.languageOverride = localeInfo.shortName;
                    SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
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
                if (this.currentLocaleInfo != null && !TextUtils.isEmpty(this.currentLocaleInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = this.allRules.get(strArr[0]);
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = this.allRules.get(this.currentLocale.getLanguage());
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
                        AndroidUtilities.runOnUIThread(new Runnable(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                LocaleController.this.lambda$applyLanguage$3$LocaleController(this.f$1);
                            }
                        });
                    } else {
                        reloadCurrentRemoteLocale(i, (String) null);
                    }
                    this.reloadLastFile = false;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    public /* synthetic */ void lambda$applyLanguage$2$LocaleController(LocaleInfo localeInfo, int i) {
        applyRemoteLanguage(localeInfo, (String) null, true, i);
    }

    public /* synthetic */ void lambda$applyLanguage$3$LocaleController(int i) {
        reloadCurrentRemoteLocale(i, (String) null);
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", NUM) : localeInfo.name;
    }

    private String getStringInternal(String str, int i) {
        String str2 = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(str) : null;
        if (str2 == null) {
            try {
                str2 = ApplicationLoader.applicationContext.getString(i);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (str2 != null) {
            return str2;
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

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    public static String getPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return getString(str2, ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return formatString(str2, ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(i));
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
                        String str3 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str2) : null;
                        if (str3 == null) {
                            str3 = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
                        }
                        String replace = str3.replace("%1$d", "%1$s");
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
        try {
            String str2 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str) : null;
            if (str2 == null) {
                str2 = ApplicationLoader.applicationContext.getString(i);
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str2, objArr);
            }
            return String.format(str2, objArr);
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
        int i2 = ((i / 60) / 60) / 24;
        if (i % 7 == 0) {
            return formatPluralString("Weeks", i2 / 7);
        }
        return String.format("%s %s", new Object[]{formatPluralString("Weeks", i2 / 7), formatPluralString("Days", i2 % 7)});
    }

    public String formatCurrencyString(long j, String str) {
        double d;
        String upperCase = str.toUpperCase();
        boolean z = j < 0;
        long abs = Math.abs(j);
        Currency instance = Currency.getInstance(upperCase);
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 2;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 9;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 10;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 0;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 11;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 12;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 13;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 14;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = 3;
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = 1;
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = 15;
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 4;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = 16;
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = 17;
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 18;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 5;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 6;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 19;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 29;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 7;
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
                    c = 8;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        String str2 = " %.0f";
        switch (c) {
            case 0:
                double d2 = (double) abs;
                Double.isNaN(d2);
                d = d2 / 10000.0d;
                str2 = " %.4f";
                break;
            case 1:
                double d3 = (double) (((float) abs) / 100.0f);
                if (abs % 100 != 0) {
                    str2 = " %.2f";
                }
                d = d3;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                double d4 = (double) abs;
                Double.isNaN(d4);
                d = d4 / 1000.0d;
                str2 = " %.3f";
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
                double d5 = (double) abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = (double) abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                str2 = " %.2f";
                break;
        }
        String str3 = "-";
        if (instance != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
            currencyInstance.setCurrency(instance);
            if (upperCase.equals("IRR")) {
                currencyInstance.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!z) {
                str3 = "";
            }
            sb.append(str3);
            sb.append(currencyInstance.format(d));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        if (!z) {
            str3 = "";
        }
        sb2.append(str3);
        Locale locale2 = Locale.US;
        sb2.append(String.format(locale2, upperCase + str2, new Object[]{Double.valueOf(d)}));
        return sb2.toString();
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
            if (r0 == 0) goto L_0x0169
            r0 = 28
            goto L_0x016a
        L_0x001f:
            java.lang.String r0 = "XOF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 27
            goto L_0x016a
        L_0x002b:
            java.lang.String r0 = "XAF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 26
            goto L_0x016a
        L_0x0037:
            java.lang.String r0 = "VUV"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 25
            goto L_0x016a
        L_0x0043:
            java.lang.String r0 = "VND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 24
            goto L_0x016a
        L_0x004f:
            java.lang.String r0 = "UYI"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 23
            goto L_0x016a
        L_0x005b:
            java.lang.String r0 = "UGX"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 22
            goto L_0x016a
        L_0x0067:
            java.lang.String r0 = "TND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 8
            goto L_0x016a
        L_0x0073:
            java.lang.String r0 = "RWF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 21
            goto L_0x016a
        L_0x007f:
            java.lang.String r0 = "PYG"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 20
            goto L_0x016a
        L_0x008b:
            java.lang.String r0 = "OMR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 7
            goto L_0x016a
        L_0x0096:
            java.lang.String r0 = "MRO"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 29
            goto L_0x016a
        L_0x00a2:
            java.lang.String r0 = "MGA"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 19
            goto L_0x016a
        L_0x00ae:
            java.lang.String r0 = "LYD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 6
            goto L_0x016a
        L_0x00b9:
            java.lang.String r0 = "KWD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 5
            goto L_0x016a
        L_0x00c4:
            java.lang.String r0 = "KRW"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 18
            goto L_0x016a
        L_0x00d0:
            java.lang.String r0 = "KMF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 17
            goto L_0x016a
        L_0x00dc:
            java.lang.String r0 = "JPY"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 16
            goto L_0x016a
        L_0x00e8:
            java.lang.String r0 = "JOD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 4
            goto L_0x016a
        L_0x00f3:
            java.lang.String r0 = "ISK"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 15
            goto L_0x016a
        L_0x00ff:
            java.lang.String r0 = "IRR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 1
            goto L_0x016a
        L_0x0109:
            java.lang.String r0 = "IQD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 3
            goto L_0x016a
        L_0x0113:
            java.lang.String r0 = "GNF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 14
            goto L_0x016a
        L_0x011e:
            java.lang.String r0 = "DJF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 13
            goto L_0x016a
        L_0x0129:
            java.lang.String r0 = "CVE"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 12
            goto L_0x016a
        L_0x0134:
            java.lang.String r0 = "CLP"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 11
            goto L_0x016a
        L_0x013f:
            java.lang.String r0 = "CLF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 0
            goto L_0x016a
        L_0x0149:
            java.lang.String r0 = "BYR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 10
            goto L_0x016a
        L_0x0154:
            java.lang.String r0 = "BIF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 9
            goto L_0x016a
        L_0x015f:
            java.lang.String r0 = "BHD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0169
            r0 = 2
            goto L_0x016a
        L_0x0169:
            r0 = -1
        L_0x016a:
            java.lang.String r3 = " %.0f"
            java.lang.String r4 = " %.2f"
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
            }
        L_0x0171:
            double r10 = (double) r10
            r5 = 4636737291354636288(0xNUM, double:100.0)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r5
            r3 = r4
            goto L_0x01b1
        L_0x017a:
            double r10 = (double) r10
            r3 = 4621819117588971520(0xNUM, double:10.0)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r3
            java.lang.String r3 = " %.1f"
            goto L_0x01b1
        L_0x0184:
            double r10 = (double) r10
            goto L_0x01b1
        L_0x0186:
            double r10 = (double) r10
            r3 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r3
            java.lang.String r3 = " %.3f"
            goto L_0x01b1
        L_0x0193:
            float r0 = (float) r10
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 / r5
            double r5 = (double) r0
            r7 = 100
            long r10 = r10 % r7
            r7 = 0
            int r0 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x01a2
            goto L_0x01a3
        L_0x01a2:
            r3 = r4
        L_0x01a3:
            r10 = r5
            goto L_0x01b1
        L_0x01a5:
            double r10 = (double) r10
            r3 = 4666723172467343360(0x40cNUM, double:10000.0)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r3
            java.lang.String r3 = " %.4f"
        L_0x01b1:
            java.util.Locale r0 = java.util.Locale.US
            if (r13 == 0) goto L_0x01b6
            goto L_0x01c7
        L_0x01b6:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = ""
            r12.append(r13)
            r12.append(r3)
            java.lang.String r12 = r12.toString()
        L_0x01c7:
            java.lang.Object[] r13 = new java.lang.Object[r1]
            java.lang.Double r10 = java.lang.Double.valueOf(r10)
            r13[r2] = r10
            java.lang.String r10 = java.lang.String.format(r0, r12, r13)
            java.lang.String r10 = r10.trim()
            return r10
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
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + str;
        }
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
                    this.currentPluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
            }
            String systemLocaleStringIso639 = getSystemLocaleStringIso639();
            String str = this.currentSystemLocale;
            if (str != null && !systemLocaleStringIso639.equals(str)) {
                this.currentSystemLocale = systemLocaleStringIso639;
                ConnectionsManager.setSystemLangCode(this.currentSystemLocale);
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

    public static String formatDateAudio(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(1);
            instance.setTimeInMillis(j2);
            int i3 = instance.get(6);
            int i4 = instance.get(1);
            if (i3 == i && i2 == i4) {
                return formatString("TodayAtFormatted", NUM, getInstance().formatterDay.format(new Date(j2)));
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
            r7 = 2131627340(0x7f0e0d4c, float:1.8881942E38)
            java.lang.String r8 = "formatterMonth"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd MMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterDayMonth = r7
            r7 = 2131627346(0x7f0e0d52, float:1.8881954E38)
            java.lang.String r8 = "formatterYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYear = r7
            r7 = 2131627347(0x7f0e0d53, float:1.8881956E38)
            java.lang.String r8 = "formatterYearMax"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "dd.MM.yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterYearMax = r7
            r7 = 2131627308(0x7f0e0d2c, float:1.8881877E38)
            java.lang.String r8 = "chatDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatDate = r7
            r7 = 2131627309(0x7f0e0d2d, float:1.8881879E38)
            java.lang.String r8 = "chatFullDate"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "d MMMM yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.chatFullDate = r7
            r7 = 2131627345(0x7f0e0d51, float:1.8881952E38)
            java.lang.String r8 = "formatterWeek"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "EEE"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterWeek = r7
            r7 = 2131627332(0x7f0e0d44, float:1.8881925E38)
            java.lang.String r8 = "formatDateSchedule"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleDay = r7
            r7 = 2131627333(0x7f0e0d45, float:1.8881927E38)
            java.lang.String r8 = "formatDateScheduleYear"
            java.lang.String r7 = r9.getStringInternal(r8, r7)
            java.lang.String r8 = "MMM d yyyy"
            org.telegram.messenger.time.FastDateFormat r7 = r9.createFormatter(r0, r7, r8)
            r9.formatterScheduleYear = r7
            java.lang.String r7 = r1.toLowerCase()
            boolean r4 = r7.equals(r4)
            if (r4 != 0) goto L_0x0118
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0115
            goto L_0x0118
        L_0x0115:
            java.util.Locale r1 = java.util.Locale.US
            goto L_0x0119
        L_0x0118:
            r1 = r0
        L_0x0119:
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0123
            r2 = 2131627339(0x7f0e0d4b, float:1.888194E38)
            java.lang.String r4 = "formatterDay24H"
            goto L_0x0128
        L_0x0123:
            r2 = 2131627338(0x7f0e0d4a, float:1.8881938E38)
            java.lang.String r4 = "formatterDay12H"
        L_0x0128:
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            boolean r4 = is24HourFormat
            if (r4 == 0) goto L_0x0133
            java.lang.String r4 = "HH:mm"
            goto L_0x0135
        L_0x0133:
            java.lang.String r4 = "h:mm a"
        L_0x0135:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r1, r2, r4)
            r9.formatterDay = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x0145
            r1 = 2131627344(0x7f0e0d50, float:1.888195E38)
            java.lang.String r2 = "formatterStats24H"
            goto L_0x014a
        L_0x0145:
            r1 = 2131627343(0x7f0e0d4f, float:1.8881948E38)
            java.lang.String r2 = "formatterStats12H"
        L_0x014a:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            java.lang.String r4 = "MMM dd yyyy, HH:mm"
            java.lang.String r7 = "MMM dd yyyy, h:mm a"
            if (r2 == 0) goto L_0x0158
            r2 = r4
            goto L_0x0159
        L_0x0158:
            r2 = r7
        L_0x0159:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r2)
            r9.formatterStats = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x0169
            r1 = 2131627335(0x7f0e0d47, float:1.8881932E38)
            java.lang.String r2 = "formatterBannedUntil24H"
            goto L_0x016e
        L_0x0169:
            r1 = 2131627334(0x7f0e0d46, float:1.888193E38)
            java.lang.String r2 = "formatterBannedUntil12H"
        L_0x016e:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0177
            goto L_0x0178
        L_0x0177:
            r4 = r7
        L_0x0178:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r4)
            r9.formatterBannedUntil = r1
            boolean r1 = is24HourFormat
            if (r1 == 0) goto L_0x0188
            r1 = 2131627337(0x7f0e0d49, float:1.8881936E38)
            java.lang.String r2 = "formatterBannedUntilThisYear24H"
            goto L_0x018d
        L_0x0188:
            r1 = 2131627336(0x7f0e0d48, float:1.8881934E38)
            java.lang.String r2 = "formatterBannedUntilThisYear12H"
        L_0x018d:
            java.lang.String r1 = r9.getStringInternal(r2, r1)
            boolean r2 = is24HourFormat
            if (r2 == 0) goto L_0x0198
            java.lang.String r2 = "MMM dd, HH:mm"
            goto L_0x019a
        L_0x0198:
            java.lang.String r2 = "MMM dd, h:mm a"
        L_0x019a:
            org.telegram.messenger.time.FastDateFormat r1 = r9.createFormatter(r0, r1, r2)
            r9.formatterBannedUntilThisYear = r1
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            java.lang.String r4 = "SendTodayAt"
            java.lang.String r2 = r9.getStringInternal(r4, r2)
            java.lang.String r4 = "'Send today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r4)
            r1[r3] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.String r3 = "SendDayAt"
            java.lang.String r2 = r9.getStringInternal(r3, r2)
            java.lang.String r3 = "'Send on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r3)
            r1[r5] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            java.lang.String r3 = "SendDayYearAt"
            java.lang.String r2 = r9.getStringInternal(r3, r2)
            java.lang.String r3 = "'Send on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r2 = r9.createFormatter(r0, r2, r3)
            r1[r6] = r2
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 3
            r3 = 2131626389(0x7f0e0995, float:1.8880013E38)
            java.lang.String r4 = "RemindTodayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind today at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 4
            r3 = 2131626387(0x7f0e0993, float:1.8880009E38)
            java.lang.String r4 = "RemindDayAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind on' MMM d 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r3 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r3
            org.telegram.messenger.time.FastDateFormat[] r1 = r9.formatterScheduleSend
            r2 = 5
            r3 = 2131626388(0x7f0e0994, float:1.888001E38)
            java.lang.String r4 = "RemindDayYearAt"
            java.lang.String r3 = r9.getStringInternal(r4, r3)
            java.lang.String r4 = "'Remind on' MMM d yyyy 'at' HH:mm"
            org.telegram.messenger.time.FastDateFormat r0 = r9.createFormatter(r0, r3, r4)
            r1[r2] = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.recreateFormatters():void");
    }

    public static boolean isRTLCharacter(char c) {
        return Character.getDirectionality(c) == 1 || Character.getDirectionality(c) == 2 || Character.getDirectionality(c) == 16 || Character.getDirectionality(c) == 17;
    }

    public static String formatSectionDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(1);
            instance.setTimeInMillis(j2);
            int i2 = instance.get(1);
            int i3 = instance.get(2);
            String[] strArr = {getString("January", NUM), getString("February", NUM), getString("March", NUM), getString("April", NUM), getString("May", NUM), getString("June", NUM), getString("July", NUM), getString("August", NUM), getString("September", NUM), getString("October", NUM), getString("November", NUM), getString("December", NUM)};
            if (i == i2) {
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

    public static String formatUserStatus(int i, TLRPC.User user) {
        return formatUserStatus(i, user, (boolean[]) null);
    }

    public static String formatUserStatus(int i, TLRPC.User user, boolean[] zArr) {
        TLRPC.UserStatus userStatus;
        TLRPC.UserStatus userStatus2;
        TLRPC.UserStatus userStatus3;
        if (!(user == null || (userStatus3 = user.status) == null || userStatus3.expires != 0)) {
            if (userStatus3 instanceof TLRPC.TL_userStatusRecently) {
                userStatus3.expires = -100;
            } else if (userStatus3 instanceof TLRPC.TL_userStatusLastWeek) {
                userStatus3.expires = -101;
            } else if (userStatus3 instanceof TLRPC.TL_userStatusLastMonth) {
                userStatus3.expires = -102;
            }
        }
        if (user != null && (userStatus2 = user.status) != null && userStatus2.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            if (zArr != null) {
                zArr[0] = true;
            }
            return getString("Online", NUM);
        } else if (user == null || (userStatus = user.status) == null || userStatus.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", NUM);
        } else {
            int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
            int i2 = user.status.expires;
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

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC.TL_langPackDifference tL_langPackDifference, int i) {
        if (this.currentLocaleInfo != null) {
            String lowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (lowerCase.equals(this.currentLocaleInfo.shortName) || lowerCase.equals(this.currentLocaleInfo.baseLangCode)) {
                lambda$null$9$LocaleController(this.currentLocaleInfo, tL_langPackDifference, i);
            }
        }
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void lambda$null$9$LocaleController(LocaleInfo localeInfo, TLRPC.TL_langPackDifference tL_langPackDifference, int i) {
        int i2;
        File file;
        HashMap<String, String> hashMap;
        if (tL_langPackDifference != null && !tL_langPackDifference.strings.isEmpty() && localeInfo != null && !localeInfo.isLocal()) {
            String lowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
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
                    if (tL_langPackDifference.from_version == 0) {
                        hashMap = new HashMap<>();
                    } else {
                        hashMap = getLocaleFileStrings(file, true);
                    }
                    for (int i3 = 0; i3 < tL_langPackDifference.strings.size(); i3++) {
                        TLRPC.LangPackString langPackString = tL_langPackDifference.strings.get(i3);
                        if (langPackString instanceof TLRPC.TL_langPackString) {
                            hashMap.put(langPackString.key, escapeString(langPackString.value));
                        } else if (langPackString instanceof TLRPC.TL_langPackStringPluralized) {
                            String str = "";
                            hashMap.put(langPackString.key + "_zero", langPackString.zero_value != null ? escapeString(langPackString.zero_value) : str);
                            hashMap.put(langPackString.key + "_one", langPackString.one_value != null ? escapeString(langPackString.one_value) : str);
                            hashMap.put(langPackString.key + "_two", langPackString.two_value != null ? escapeString(langPackString.two_value) : str);
                            hashMap.put(langPackString.key + "_few", langPackString.few_value != null ? escapeString(langPackString.few_value) : str);
                            hashMap.put(langPackString.key + "_many", langPackString.many_value != null ? escapeString(langPackString.many_value) : str);
                            String str2 = langPackString.key + "_other";
                            if (langPackString.other_value != null) {
                                str = escapeString(langPackString.other_value);
                            }
                            hashMap.put(str2, str);
                        } else if (langPackString instanceof TLRPC.TL_langPackStringDeleted) {
                            hashMap.remove(langPackString.key);
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
                    AndroidUtilities.runOnUIThread(new Runnable(localeInfo, i2, tL_langPackDifference, localeFileStrings) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ TLRPC.TL_langPackDifference f$3;
                        private final /* synthetic */ HashMap f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            LocaleController.this.lambda$saveRemoteLocaleStrings$4$LocaleController(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                } catch (Exception unused) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$saveRemoteLocaleStrings$4$LocaleController(LocaleInfo localeInfo, int i, TLRPC.TL_langPackDifference tL_langPackDifference, HashMap hashMap) {
        String[] strArr;
        Locale locale;
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
                if (this.currentLocaleInfo != null && !TextUtils.isEmpty(this.currentLocaleInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
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
            ConnectionsManager.getInstance(i).sendRequest(new TLRPC.TL_langpack_getLanguages(), new RequestDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocaleController.this.lambda$loadRemoteLanguages$6$LocaleController(this.f$1, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$loadRemoteLanguages$6$LocaleController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, i) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LocaleController.this.lambda$null$5$LocaleController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$LocaleController(TLObject tLObject, int i) {
        this.loadingRemoteLanguages = false;
        TLRPC.Vector vector = (TLRPC.Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.remoteLanguages.get(i2).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = vector.objects.size();
        for (int i3 = 0; i3 < size2; i3++) {
            TLRPC.TL_langPackLanguage tL_langPackLanguage = (TLRPC.TL_langPackLanguage) vector.objects.get(i3);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + tL_langPackLanguage.name);
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
            localeInfo.serverIndex = i3;
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
        if (localeInfo == null || localeInfo.isRemote() || localeInfo.isUnofficial()) {
            if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion == 0 || z) {
                    TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack = new TLRPC.TL_langpack_getLangPack();
                    tL_langpack_getLangPack.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack, new RequestDelegate(localeInfo, i) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.lambda$applyRemoteLanguage$10$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 8);
                } else if (localeInfo.hasBaseLang()) {
                    TLRPC.TL_langpack_getDifference tL_langpack_getDifference = new TLRPC.TL_langpack_getDifference();
                    tL_langpack_getDifference.from_version = localeInfo.baseVersion;
                    tL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                    tL_langpack_getDifference.lang_pack = "";
                    ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference, new RequestDelegate(localeInfo, i) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.lambda$applyRemoteLanguage$8$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 8);
                }
            }
            if (str != null && !str.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version == 0 || z) {
                for (int i2 = 0; i2 < 3; i2++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack2 = new TLRPC.TL_langpack_getLangPack();
                tL_langpack_getLangPack2.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack2, new RequestDelegate(localeInfo, i) {
                    private final /* synthetic */ LocaleController.LocaleInfo f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocaleController.this.lambda$applyRemoteLanguage$14$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                }, 8);
                return;
            }
            TLRPC.TL_langpack_getDifference tL_langpack_getDifference2 = new TLRPC.TL_langpack_getDifference();
            tL_langpack_getDifference2.from_version = localeInfo.version;
            tL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
            tL_langpack_getDifference2.lang_pack = "";
            ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference2, new RequestDelegate(localeInfo, i) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocaleController.this.lambda$applyRemoteLanguage$12$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$8$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, tLObject, i) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$7$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$10$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, tLObject, i) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$9$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$12$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, tLObject, i) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$11$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$14$LocaleController(LocaleInfo localeInfo, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, tLObject, i) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$13$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
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
        boolean z3;
        String str2;
        if (str == null) {
            return null;
        }
        if (this.ruTranslitChars == null) {
            this.ruTranslitChars = new HashMap<>(33);
            this.ruTranslitChars.put("а", "a");
            obj = "a";
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", "g");
            this.ruTranslitChars.put("д", "d");
            this.ruTranslitChars.put("е", "e");
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
            this.ruTranslitChars.put("п", "p");
            this.ruTranslitChars.put("р", "r");
            this.ruTranslitChars.put("с", "s");
            this.ruTranslitChars.put("т", "t");
            this.ruTranslitChars.put("у", "u");
            this.ruTranslitChars.put("ф", "f");
            obj3 = "h";
            this.ruTranslitChars.put("х", obj3);
            obj2 = "t";
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
            obj2 = "t";
            obj = "a";
            obj3 = "h";
        }
        if (this.translitChars == null) {
            this.translitChars = new HashMap<>(487);
            this.translitChars.put("ȼ", "c");
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", "d");
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            Object obj4 = obj;
            this.translitChars.put("ḁ", obj4);
            this.translitChars.put("ʯ", obj3);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", "u");
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", "r");
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", "r");
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            this.translitChars.put("ꝕ", "p");
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", obj4);
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", obj3);
            this.translitChars.put("ᵬ", "b");
            this.translitChars.put("ṣ", "s");
            this.translitChars.put("đ", "d");
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", obj4);
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", "v");
            this.translitChars.put("ꝓ", "p");
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", "d");
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", "r");
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            this.translitChars.put("ᶃ", "g");
            Object obj5 = "n";
            this.translitChars.put("ṋ", obj5);
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", "g");
            this.translitChars.put("ǳ", "dz");
            Object obj6 = "z";
            this.translitChars.put("ź", obj6);
            Object obj7 = obj3;
            this.translitChars.put("ꜷ", "au");
            this.translitChars.put("ǖ", "u");
            this.translitChars.put("ᵹ", "g");
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", obj4);
            this.translitChars.put("ą", obj4);
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", "r");
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", obj4);
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", "s");
            this.translitChars.put("ﬂ", "fl");
            this.translitChars.put("ȉ", "i");
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", obj5);
            this.translitChars.put("ï", "i");
            this.translitChars.put("ñ", obj5);
            this.translitChars.put("ᴉ", "i");
            Object obj8 = obj2;
            this.translitChars.put("ʇ", obj8);
            this.translitChars.put("ẓ", obj6);
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", "s");
            this.translitChars.put("ɽ", "r");
            this.translitChars.put("ĝ", "g");
            this.translitChars.put("ᴝ", "u");
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", "i");
            this.translitChars.put("ť", obj8);
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", "u");
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", obj4);
            this.translitChars.put("ǘ", "u");
            this.translitChars.put("ꞅ", "s");
            this.translitChars.put("ᵣ", "r");
            this.translitChars.put("ᴀ", obj4);
            this.translitChars.put("ƃ", "b");
            Object obj9 = obj7;
            this.translitChars.put("ḩ", obj9);
            this.translitChars.put("ṧ", "s");
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", obj9);
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            Object obj10 = "d";
            this.translitChars.put("ḋ", obj10);
            Object obj11 = "l";
            this.translitChars.put("ƣ", "oi");
            this.translitChars.put("ꝑ", "p");
            this.translitChars.put("ħ", obj9);
            this.translitChars.put("ⱴ", "v");
            this.translitChars.put("ẇ", "w");
            this.translitChars.put("ǹ", obj5);
            this.translitChars.put("ɯ", "m");
            this.translitChars.put("ɡ", "g");
            this.translitChars.put("ɴ", obj5);
            this.translitChars.put("ᴘ", "p");
            this.translitChars.put("ᵥ", "v");
            this.translitChars.put("ū", "u");
            this.translitChars.put("ḃ", "b");
            this.translitChars.put("ṗ", "p");
            this.translitChars.put("å", obj4);
            this.translitChars.put("ɕ", "c");
            this.translitChars.put("ọ", "o");
            this.translitChars.put("ắ", obj4);
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", "r");
            this.translitChars.put("ô", "o");
            this.translitChars.put("ǿ", "o");
            this.translitChars.put("ṳ", "u");
            this.translitChars.put("ȥ", obj6);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", obj10);
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", "u");
            this.translitChars.put("ȵ", obj5);
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", obj4);
            this.translitChars.put("ǩ", "k");
            this.translitChars.put("ĩ", "i");
            this.translitChars.put("ṵ", "u");
            this.translitChars.put("ŧ", obj8);
            this.translitChars.put("ɾ", "r");
            this.translitChars.put("ƙ", "k");
            this.translitChars.put("ṫ", obj8);
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", obj4);
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", obj11);
            this.translitChars.put("ᶂ", "f");
            Object obj12 = "s";
            this.translitChars.put("ᵴ", obj12);
            this.translitChars.put("ꞃ", "r");
            Object obj13 = "g";
            this.translitChars.put("ᶌ", "v");
            this.translitChars.put("ɵ", "o");
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", "u");
            this.translitChars.put("ẑ", obj6);
            this.translitChars.put("ṹ", "u");
            this.translitChars.put("ň", obj5);
            this.translitChars.put("ʍ", "w");
            this.translitChars.put("ầ", obj4);
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", "b");
            this.translitChars.put("ɼ", "r");
            this.translitChars.put("ò", "o");
            this.translitChars.put("ẘ", "w");
            this.translitChars.put("ɗ", obj10);
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", "u");
            this.translitChars.put("ᶀ", "b");
            this.translitChars.put("ǜ", "u");
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", obj4);
            this.translitChars.put("ɥ", obj9);
            this.translitChars.put("ṏ", "o");
            this.translitChars.put("ǔ", "u");
            Object obj14 = "y";
            this.translitChars.put("ʎ", obj14);
            this.translitChars.put("ȱ", "o");
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            this.translitChars.put("ĭ", "i");
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", obj8);
            this.translitChars.put("ᶑ", obj10);
            this.translitChars.put("ḧ", obj9);
            this.translitChars.put("ṥ", obj12);
            this.translitChars.put("ë", "e");
            this.translitChars.put("ᴍ", "m");
            this.translitChars.put("ö", "o");
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", "i");
            this.translitChars.put("ď", obj10);
            this.translitChars.put("ᵯ", "m");
            this.translitChars.put("ỵ", obj14);
            this.translitChars.put("ŵ", "w");
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", "u");
            this.translitChars.put("ƶ", obj6);
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", obj10);
            this.translitChars.put("ŭ", "u");
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", "u");
            this.translitChars.put("ġ", obj13);
            this.translitChars.put("ṙ", "r");
            this.translitChars.put("ƞ", obj5);
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", obj12);
            this.translitChars.put("ᶁ", obj10);
            this.translitChars.put("ķ", "k");
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", "o");
            this.translitChars.put("ḿ", "m");
            this.translitChars.put("ꜰ", "f");
            this.translitChars.put("ẵ", obj4);
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", "m");
            this.translitChars.put("ᵽ", "p");
            this.translitChars.put("ữ", "u");
            this.translitChars.put("ⱪ", "k");
            this.translitChars.put("ḥ", obj9);
            Object obj15 = obj8;
            this.translitChars.put("ţ", obj15);
            this.translitChars.put("ᵱ", "p");
            this.translitChars.put("ṁ", "m");
            this.translitChars.put("á", obj4);
            this.translitChars.put("ᴎ", obj5);
            this.translitChars.put("ꝟ", "v");
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", obj6);
            this.translitChars.put("ꝺ", obj10);
            this.translitChars.put("ᶈ", "p");
            this.translitChars.put("ɫ", obj11);
            this.translitChars.put("ᴢ", obj6);
            this.translitChars.put("ɱ", "m");
            this.translitChars.put("ṝ", "r");
            this.translitChars.put("ṽ", "v");
            this.translitChars.put("ũ", "u");
            this.translitChars.put("ß", "ss");
            this.translitChars.put("ĥ", obj9);
            this.translitChars.put("ᵵ", obj15);
            this.translitChars.put("ʐ", obj6);
            this.translitChars.put("ṟ", "r");
            this.translitChars.put("ɲ", obj5);
            this.translitChars.put("à", obj4);
            this.translitChars.put("ẙ", obj14);
            this.translitChars.put("ỳ", obj14);
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", "u");
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", obj4);
            this.translitChars.put("ʑ", obj6);
            this.translitChars.put("ẛ", obj12);
            this.translitChars.put("ḭ", "i");
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", obj6);
            this.translitChars.put("ÿ", obj14);
            this.translitChars.put("ǝ", "e");
            Object obj16 = "o";
            this.translitChars.put("ǭ", obj16);
            this.translitChars.put("ᴅ", obj10);
            Object obj17 = obj5;
            Object obj18 = obj11;
            this.translitChars.put("ᶅ", obj18);
            this.translitChars.put("ù", "u");
            this.translitChars.put("ạ", obj4);
            Object obj19 = obj10;
            this.translitChars.put("ḅ", "b");
            this.translitChars.put("ụ", "u");
            this.translitChars.put("ằ", obj4);
            this.translitChars.put("ᴛ", obj15);
            this.translitChars.put("ƴ", obj14);
            this.translitChars.put("ⱦ", obj15);
            this.translitChars.put("ⱡ", obj18);
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", obj6);
            this.translitChars.put("ḫ", obj9);
            this.translitChars.put("ⱳ", "w");
            this.translitChars.put("ḵ", "k");
            this.translitChars.put("ờ", obj16);
            this.translitChars.put("î", "i");
            Object obj20 = obj13;
            this.translitChars.put("ģ", obj20);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", obj4);
            this.translitChars.put("ẳ", obj4);
            Object obj21 = obj9;
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", obj15);
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", "c");
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", "u");
            this.translitChars.put("ỉ", "i");
            this.translitChars.put("ᴚ", "r");
            this.translitChars.put("ś", obj12);
            this.translitChars.put("ꝋ", obj16);
            this.translitChars.put("ỹ", obj14);
            this.translitChars.put("ṡ", obj12);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", obj4);
            this.translitChars.put("ẗ", obj15);
            this.translitChars.put("ĺ", obj18);
            this.translitChars.put("ž", obj6);
            this.translitChars.put("ᵺ", "th");
            Object obj22 = obj19;
            this.translitChars.put("ƌ", obj22);
            this.translitChars.put("ș", obj12);
            this.translitChars.put("š", obj12);
            this.translitChars.put("ᶙ", "u");
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", obj12);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", "u");
            this.translitChars.put("ố", obj16);
            this.translitChars.put("ȿ", obj12);
            Object obj23 = obj14;
            this.translitChars.put("ᴠ", "v");
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", obj16);
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", obj4);
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", obj16);
            this.translitChars.put("ȋ", "i");
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", obj22);
            this.translitChars.put("ⱬ", obj6);
            this.translitChars.put("ẁ", "w");
            this.translitChars.put("ᶏ", obj4);
            this.translitChars.put("ꞇ", obj15);
            this.translitChars.put("ğ", obj20);
            Object obj24 = obj17;
            this.translitChars.put("ɳ", obj24);
            this.translitChars.put("ʛ", obj20);
            this.translitChars.put("ᴜ", "u");
            this.translitChars.put("ẩ", obj4);
            this.translitChars.put("ṅ", obj24);
            this.translitChars.put("ɨ", "i");
            this.translitChars.put("ᴙ", "r");
            this.translitChars.put("ǎ", obj4);
            this.translitChars.put("ſ", obj12);
            this.translitChars.put("ȫ", obj16);
            this.translitChars.put("ɿ", "r");
            this.translitChars.put("ƭ", obj15);
            this.translitChars.put("ḯ", "i");
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", "v");
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", "m");
            this.translitChars.put("ż", obj6);
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", obj16);
            this.translitChars.put("ễ", "e");
            this.translitChars.put("ɬ", obj18);
            this.translitChars.put("ị", "i");
            this.translitChars.put("ᵭ", obj22);
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", obj18);
            this.translitChars.put("ŕ", "r");
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", obj15);
            this.translitChars.put("ā", obj4);
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", obj16);
            this.translitChars.put("ç", "c");
            this.translitChars.put("ᶊ", obj12);
            this.translitChars.put("ặ", obj4);
            this.translitChars.put("ų", "u");
            this.translitChars.put("ả", obj4);
            this.translitChars.put("ǥ", obj20);
            this.translitChars.put("ꝁ", "k");
            this.translitChars.put("ẕ", obj6);
            this.translitChars.put("ŝ", obj12);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", obj20);
            this.translitChars.put("ꝉ", obj18);
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", obj16);
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", obj16);
            this.translitChars.put("ƫ", obj15);
            this.translitChars.put("ǫ", obj16);
            this.translitChars.put("i̇", "i");
            Object obj25 = obj17;
            this.translitChars.put("ṇ", obj25);
            this.translitChars.put("ć", "c");
            this.translitChars.put("ᵷ", obj20);
            this.translitChars.put("ẅ", "w");
            this.translitChars.put("ḑ", obj22);
            this.translitChars.put("ḹ", obj18);
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", "r");
            this.translitChars.put("ļ", obj18);
            this.translitChars.put("ȑ", "r");
            this.translitChars.put("ȭ", obj16);
            this.translitChars.put("ᵰ", obj25);
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", obj18);
            this.translitChars.put("ä", obj4);
            this.translitChars.put("ƥ", "p");
            this.translitChars.put("ỏ", obj16);
            this.translitChars.put("į", "i");
            this.translitChars.put("ȓ", "r");
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", obj20);
            this.translitChars.put("ṻ", "u");
            this.translitChars.put("ō", obj16);
            this.translitChars.put("ľ", obj18);
            this.translitChars.put("ẃ", "w");
            this.translitChars.put("ț", obj15);
            this.translitChars.put("ń", obj25);
            this.translitChars.put("ɍ", "r");
            this.translitChars.put("ȃ", obj4);
            this.translitChars.put("ü", "u");
            this.translitChars.put("ꞁ", obj18);
            this.translitChars.put("ᴐ", obj16);
            this.translitChars.put("ớ", obj16);
            this.translitChars.put("ᴃ", "b");
            this.translitChars.put("ɹ", "r");
            this.translitChars.put("ᵲ", "r");
            Object obj26 = obj23;
            this.translitChars.put("ʏ", obj26);
            this.translitChars.put("ᵮ", "f");
            Object obj27 = obj21;
            this.translitChars.put("ⱨ", obj27);
            this.translitChars.put("ŏ", obj16);
            this.translitChars.put("ú", "u");
            this.translitChars.put("ṛ", "r");
            this.translitChars.put("ʮ", obj27);
            this.translitChars.put("ó", obj16);
            this.translitChars.put("ů", "u");
            this.translitChars.put("ỡ", obj16);
            this.translitChars.put("ṕ", "p");
            this.translitChars.put("ᶖ", "i");
            this.translitChars.put("ự", "u");
            this.translitChars.put("ã", obj4);
            this.translitChars.put("ᵢ", "i");
            this.translitChars.put("ṱ", obj15);
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", "u");
            this.translitChars.put("í", "i");
            this.translitChars.put("ɔ", obj16);
            this.translitChars.put("ɺ", "r");
            this.translitChars.put("ɢ", obj20);
            this.translitChars.put("ř", "r");
            this.translitChars.put("ẖ", obj27);
            this.translitChars.put("ű", "u");
            this.translitChars.put("ȍ", obj16);
            this.translitChars.put("ḻ", obj18);
            this.translitChars.put("ḣ", obj27);
            this.translitChars.put("ȶ", obj15);
            this.translitChars.put("ņ", obj25);
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", "i");
            this.translitChars.put("ẉ", "w");
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", obj18);
            this.translitChars.put("ộ", obj16);
            this.translitChars.put("ɭ", obj18);
            this.translitChars.put("ẏ", obj26);
            this.translitChars.put("ᴊ", "j");
            this.translitChars.put("ḱ", "k");
            this.translitChars.put("ṿ", "v");
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", obj4);
            Object obj28 = obj12;
            this.translitChars.put("ş", obj28);
            this.translitChars.put("ŗ", "r");
            this.translitChars.put("ʋ", "v");
            this.translitChars.put("ₐ", obj4);
            this.translitChars.put("ↄ", "c");
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", "m");
            this.translitChars.put("ᴡ", "w");
            this.translitChars.put("ȏ", obj16);
            this.translitChars.put("č", "c");
            this.translitChars.put("ǵ", obj20);
            this.translitChars.put("ĉ", "c");
            this.translitChars.put("ᶗ", obj16);
            this.translitChars.put("ꝃ", "k");
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", obj16);
            this.translitChars.put("ꜱ", obj28);
            this.translitChars.put("ṓ", obj16);
            this.translitChars.put("ȟ", obj27);
            this.translitChars.put("ő", obj16);
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
        }
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        boolean z4 = false;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            if (z2) {
                str2 = substring.toLowerCase();
                z3 = !substring.equals(str2);
            } else {
                String str3 = substring;
                z3 = z4;
                str2 = str3;
            }
            String str4 = this.translitChars.get(str2);
            if (str4 == null && z) {
                str4 = this.ruTranslitChars.get(str2);
            }
            if (str4 != null) {
                if (z2 && z3) {
                    if (str4.length() > 1) {
                        str4 = str4.substring(0, 1).toUpperCase() + str4.substring(1);
                    } else {
                        str4 = str4.toUpperCase();
                    }
                }
                sb.append(str4);
            } else {
                if (z2) {
                    char charAt = str2.charAt(0);
                    if ((charAt < 'a' || charAt > 'z' || charAt < '0' || charAt > '9') && charAt != ' ' && charAt != '\'' && charAt != ',' && charAt != '.' && charAt != '&' && charAt != '-' && charAt != '/') {
                        return null;
                    }
                    if (z3) {
                        str2 = str2.toUpperCase();
                    }
                }
                sb.append(str2);
            }
            z4 = z3;
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

    public static String formatDistance(float f) {
        String str;
        String str2;
        boolean z;
        if (useImperialSystemType == null) {
            int i = SharedConfig.distanceSystemType;
            if (i == 0) {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager != null) {
                        String upperCase = telephonyManager.getSimCountryIso().toUpperCase();
                        if (!"US".equals(upperCase) && !"GB".equals(upperCase) && !"MM".equals(upperCase)) {
                            if (!"LR".equals(upperCase)) {
                                z = false;
                                useImperialSystemType = Boolean.valueOf(z);
                            }
                        }
                        z = true;
                        useImperialSystemType = Boolean.valueOf(z);
                    }
                } catch (Exception e) {
                    useImperialSystemType = false;
                    FileLog.e((Throwable) e);
                }
            } else {
                useImperialSystemType = Boolean.valueOf(i == 2);
            }
        }
        if (useImperialSystemType.booleanValue()) {
            float f2 = f * 3.28084f;
            if (f2 < 1000.0f) {
                return formatString("FootsAway", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f2))}));
            }
            if (f2 % 5280.0f == 0.0f) {
                str2 = String.format("%d", new Object[]{Integer.valueOf((int) (f2 / 5280.0f))});
            } else {
                str2 = String.format("%.2f", new Object[]{Float.valueOf(f2 / 5280.0f)});
            }
            return formatString("MilesAway", NUM, str2);
        } else if (f < 1000.0f) {
            return formatString("MetersAway2", NUM, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, f))}));
        } else {
            if (f % 1000.0f == 0.0f) {
                str = String.format("%d", new Object[]{Integer.valueOf((int) (f / 1000.0f))});
            } else {
                str = String.format("%.2f", new Object[]{Float.valueOf(f / 1000.0f)});
            }
            return formatString("KMetersAway2", NUM, str);
        }
    }
}
