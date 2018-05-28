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
import java.util.Map.Entry;
import java.util.TimeZone;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0546C;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.tgnet.TLRPC.Vector;
import org.xmlpull.v1.XmlPullParser;

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
    private boolean changingConfiguration = false;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    public FastDateFormat formatterBannedUntil;
    public FastDateFormat formatterBannedUntilThisYear;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterMonth;
    public FastDateFormat formatterMonthYear;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public ArrayList<LocaleInfo> languages = new ArrayList();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap();
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList();
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList();
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;

    /* renamed from: org.telegram.messenger.LocaleController$1 */
    class C02221 implements Runnable {
        C02221() {
        }

        public void run() {
            LocaleController.this.loadRemoteLanguages(UserConfig.selectedAccount);
        }
    }

    public static class LocaleInfo {
        public boolean builtIn;
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String shortName;
        public int version;

        public String getSaveString() {
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version;
        }

        public static LocaleInfo createWithString(String string) {
            if (string == null || string.length() == 0) {
                return null;
            }
            String[] args = string.split("\\|");
            if (args.length < 4) {
                return null;
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.name = args[0];
            localeInfo.nameEnglish = args[1];
            localeInfo.shortName = args[2].toLowerCase();
            localeInfo.pathToFile = args[3];
            if (args.length < 5) {
                return localeInfo;
            }
            localeInfo.version = Utilities.parseInt(args[4]).intValue();
            return localeInfo;
        }

        public File getPathToFile() {
            if (isRemote()) {
                return new File(ApplicationLoader.getFilesDirFixed(), "remote_" + this.shortName + ".xml");
            }
            return !TextUtils.isEmpty(this.pathToFile) ? new File(this.pathToFile) : null;
        }

        public String getKey() {
            if (this.pathToFile == null || "remote".equals(this.pathToFile)) {
                return this.shortName;
            }
            return "local_" + this.shortName;
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isLocal() {
            return (TextUtils.isEmpty(this.pathToFile) || isRemote()) ? false : true;
        }

        public boolean isBuiltIn() {
            return this.builtIn;
        }
    }

    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {

        /* renamed from: org.telegram.messenger.LocaleController$TimeZoneChangedReceiver$1 */
        class C02291 implements Runnable {
            C02291() {
            }

            public void run() {
                if (!LocaleController.this.formatterMonth.getTimeZone().equals(TimeZone.getDefault())) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        }

        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new C02291());
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
            if (rem10 == 0 || ((rem10 >= 5 && rem10 <= 9) || (rem100 >= 11 && rem100 <= 14))) {
                return 16;
            }
            return 0;
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

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int count) {
            if (count < 0 || count >= 2) {
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
            if (count <= 0 || count >= 2) {
                return 0;
            }
            return 2;
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

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && (rem100 < 11 || rem100 > 19)) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 9 || (rem100 >= 11 && rem100 <= 19)) {
                return 0;
            }
            return 8;
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

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count == 0 || (rem100 >= 2 && rem100 <= 10)) {
                return 8;
            }
            if (rem100 < 11 || rem100 > 19) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int count) {
            return 0;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int count) {
            return count == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (count == 1) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 4 || ((rem100 >= 12 && rem100 <= 14) || (rem100 >= 22 && rem100 <= 24))) {
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
            if (count == 0 || (rem100 >= 1 && rem100 <= 19)) {
                return 8;
            }
            return 0;
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

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0 || count == 1) {
                return 2;
            }
            return 0;
        }
    }

    public static LocaleController getInstance() {
        LocaleController localInstance = Instance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        LocaleController localInstance2 = new LocaleController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public LocaleController() {
        int a;
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
        addRules(new String[]{TtmlNode.TAG_BR}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", TtmlNode.ATTR_ID, "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Italiano";
        localeInfo.nameEnglish = "Italian";
        localeInfo.shortName = "it";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Espa\u00f1ol";
        localeInfo.nameEnglish = "Spanish";
        localeInfo.shortName = "es";
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Deutsch";
        localeInfo.nameEnglish = "German";
        localeInfo.shortName = "de";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Nederlands";
        localeInfo.nameEnglish = "Dutch";
        localeInfo.shortName = "nl";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0627\u0644\u0639\u0631\u0628\u064a\u0629";
        localeInfo.nameEnglish = "Arabic";
        localeInfo.shortName = "ar";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Portugu\u00eas (Brasil)";
        localeInfo.nameEnglish = "Portuguese (Brazil)";
        localeInfo.shortName = "pt_br";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\ud55c\uad6d\uc5b4";
        localeInfo.nameEnglish = "Korean";
        localeInfo.shortName = "ko";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new C02221());
        }
        for (a = 0; a < this.otherLanguages.size(); a++) {
            LocaleInfo locale = (LocaleInfo) this.otherLanguages.get(a);
            this.languages.add(locale);
            this.languagesDict.put(locale.getKey(), locale);
        }
        for (a = 0; a < this.remoteLanguages.size(); a++) {
            locale = (LocaleInfo) this.remoteLanguages.get(a);
            LocaleInfo existingLocale = getLanguageFromDict(locale.getKey());
            if (existingLocale != null) {
                existingLocale.pathToFile = locale.pathToFile;
                existingLocale.version = locale.version;
                this.remoteLanguages.set(a, existingLocale);
            } else {
                this.languages.add(locale);
                this.languagesDict.put(locale.getKey(), locale);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        LocaleInfo currentInfo = null;
        boolean override = false;
        try {
            String lang = MessagesController.getGlobalMainSettings().getString("language", null);
            if (lang != null) {
                currentInfo = getLanguageFromDict(lang);
                if (currentInfo != null) {
                    override = true;
                }
            }
            if (currentInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                currentInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (currentInfo == null) {
                currentInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
                if (currentInfo == null) {
                    currentInfo = getLanguageFromDict("en");
                }
            }
            applyLanguage(currentInfo, override, true, UserConfig.selectedAccount);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    private LocaleInfo getLanguageFromDict(String key) {
        if (key == null) {
            return null;
        }
        return (LocaleInfo) this.languagesDict.get(key.toLowerCase().replace("-", "_"));
    }

    private void addRules(String[] languages, PluralRules rules) {
        for (String language : languages) {
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

    public void reloadCurrentRemoteLocale(int currentAccount) {
        applyRemoteLanguage(this.currentLocaleInfo, true, currentAccount);
    }

    public void checkUpdateForCurrentRemoteLocale(int currentAccount, int version) {
        if (this.currentLocaleInfo == null) {
            return;
        }
        if ((this.currentLocaleInfo == null || this.currentLocaleInfo.isRemote()) && this.currentLocaleInfo.version < version) {
            applyRemoteLanguage(this.currentLocaleInfo, false, currentAccount);
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
        Object obj = -1;
        switch (code.hashCode()) {
            case 3325:
                if (code.equals("he")) {
                    obj = 7;
                    break;
                }
                break;
            case 3355:
                if (code.equals(TtmlNode.ATTR_ID)) {
                    obj = 6;
                    break;
                }
                break;
            case 3365:
                if (code.equals("in")) {
                    obj = null;
                    break;
                }
                break;
            case 3374:
                if (code.equals("iw")) {
                    obj = 1;
                    break;
                }
                break;
            case 3391:
                if (code.equals("ji")) {
                    obj = 5;
                    break;
                }
                break;
            case 3404:
                if (code.equals("jv")) {
                    obj = 8;
                    break;
                }
                break;
            case 3405:
                if (code.equals("jw")) {
                    obj = 2;
                    break;
                }
                break;
            case 3508:
                if (code.equals("nb")) {
                    obj = 9;
                    break;
                }
                break;
            case 3521:
                if (code.equals("no")) {
                    obj = 3;
                    break;
                }
                break;
            case 3704:
                if (code.equals("tl")) {
                    obj = 4;
                    break;
                }
                break;
            case 3856:
                if (code.equals("yi")) {
                    obj = 11;
                    break;
                }
                break;
            case 101385:
                if (code.equals("fil")) {
                    obj = 10;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                return TtmlNode.ATTR_ID;
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
        try {
            HashMap<String, String> stringMap = getLocaleFileStrings(file);
            String languageName = (String) stringMap.get("LanguageName");
            String languageNameInEnglish = (String) stringMap.get("LanguageNameInEnglish");
            String languageCode = (String) stringMap.get("LanguageCode");
            if (languageName != null && languageName.length() > 0 && languageNameInEnglish != null && languageNameInEnglish.length() > 0 && languageCode != null && languageCode.length() > 0) {
                if (languageName.contains("&") || languageName.contains("|")) {
                    return false;
                }
                if (languageNameInEnglish.contains("&") || languageNameInEnglish.contains("|")) {
                    return false;
                }
                if (languageCode.contains("&") || languageCode.contains("|") || languageCode.contains("/") || languageCode.contains("\\")) {
                    return false;
                }
                File finalFile = new File(ApplicationLoader.getFilesDirFixed(), languageCode + ".xml");
                if (!AndroidUtilities.copyFile(file, finalFile)) {
                    return false;
                }
                LocaleInfo localeInfo = getLanguageFromDict("local_" + languageCode.toLowerCase());
                if (localeInfo == null) {
                    localeInfo = new LocaleInfo();
                    localeInfo.name = languageName;
                    localeInfo.nameEnglish = languageNameInEnglish;
                    localeInfo.shortName = languageCode.toLowerCase();
                    localeInfo.pathToFile = finalFile.getAbsolutePath();
                    this.languages.add(localeInfo);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo);
                    this.otherLanguages.add(localeInfo);
                    saveOtherLanguages();
                }
                this.localeValues = stringMap;
                applyLanguage(localeInfo, true, false, true, false, currentAccount);
                return true;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return false;
    }

    private void saveOtherLanguages() {
        int a;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (a = 0; a < this.otherLanguages.size(); a++) {
            String loc = ((LocaleInfo) this.otherLanguages.get(a)).getSaveString();
            if (loc != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc);
            }
        }
        editor.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (a = 0; a < this.remoteLanguages.size(); a++) {
            loc = ((LocaleInfo) this.remoteLanguages.get(a)).getSaveString();
            if (loc != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc);
            }
        }
        editor.putString("remote", stringBuilder.toString());
        editor.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int currentAccount) {
        if (localeInfo.pathToFile == null || localeInfo.isRemote()) {
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
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.shortName);
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        int length;
        LocaleInfo localeInfo;
        int i = 0;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String locales = preferences.getString("locales", null);
        if (!TextUtils.isEmpty(locales)) {
            for (String locale : locales.split("&")) {
                localeInfo = LocaleInfo.createWithString(locale);
                if (localeInfo != null) {
                    this.otherLanguages.add(localeInfo);
                }
            }
        }
        locales = preferences.getString("remote", null);
        if (!TextUtils.isEmpty(locales)) {
            String[] localesArr = locales.split("&");
            length = localesArr.length;
            while (i < length) {
                localeInfo = LocaleInfo.createWithString(localesArr[i]);
                localeInfo.shortName = localeInfo.shortName.replace("-", "_");
                if (localeInfo != null) {
                    this.remoteLanguages.add(localeInfo);
                }
                i++;
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean preserveEscapes) {
        Throwable e;
        Throwable th;
        FileInputStream fileInputStream = null;
        this.reloadLastFile = false;
        try {
            HashMap<String, String> stringMap;
            if (file.exists()) {
                stringMap = new HashMap();
                XmlPullParser parser = Xml.newPullParser();
                FileInputStream stream = new FileInputStream(file);
                try {
                    parser.setInput(stream, C0546C.UTF8_NAME);
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
                            if (attrName != null) {
                                value = parser.getText();
                                if (value != null) {
                                    value = value.trim();
                                    if (preserveEscapes) {
                                        value = value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                                    } else {
                                        value = value.replace("\\n", "\n").replace("\\", TtmlNode.ANONYMOUS_REGION_ID);
                                        String old = value;
                                        value = value.replace("&lt;", "<");
                                        if (!(this.reloadLastFile || value.equals(old))) {
                                            this.reloadLastFile = true;
                                        }
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
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    fileInputStream = stream;
                    return stringMap;
                } catch (Exception e3) {
                    e2 = e3;
                    fileInputStream = stream;
                    try {
                        FileLog.m3e(e2);
                        this.reloadLastFile = true;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable e22) {
                                FileLog.m3e(e22);
                            }
                        }
                        return new HashMap();
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable e222) {
                                FileLog.m3e(e222);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fileInputStream = stream;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            }
            stringMap = new HashMap();
            if (fileInputStream == null) {
                return stringMap;
            }
            try {
                fileInputStream.close();
                return stringMap;
            } catch (Throwable e2222) {
                FileLog.m3e(e2222);
                return stringMap;
            }
        } catch (Exception e4) {
            e2222 = e4;
            FileLog.m3e(e2222);
            this.reloadLastFile = true;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return new HashMap();
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, int currentAccount) {
        applyLanguage(localeInfo, override, init, false, false, currentAccount);
    }

    public void applyLanguage(final LocaleInfo localeInfo, boolean override, boolean init, boolean fromFile, boolean force, int currentAccount) {
        if (localeInfo != null) {
            final int i;
            File pathToFile = localeInfo.getPathToFile();
            String shortName = localeInfo.shortName;
            if (!init) {
                ConnectionsManager.setLangCode(shortName.replace("_", "-"));
            }
            if (localeInfo.isRemote() && (force || !pathToFile.exists())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("reload locale because file doesn't exist " + pathToFile);
                }
                if (init) {
                    i = currentAccount;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocaleController.this.applyRemoteLanguage(localeInfo, true, i);
                        }
                    });
                } else {
                    applyRemoteLanguage(localeInfo, true, currentAccount);
                }
            }
            try {
                Locale newLocale;
                String[] args = localeInfo.shortName.split("_");
                if (args.length == 1) {
                    newLocale = new Locale(localeInfo.shortName);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                if (override) {
                    this.languageOverride = localeInfo.shortName;
                    Editor editor = MessagesController.getGlobalMainSettings().edit();
                    editor.putString("language", localeInfo.getKey());
                    editor.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!fromFile) {
                    this.localeValues = getLocaleFileStrings(pathToFile);
                }
                this.currentLocale = newLocale;
                this.currentLocaleInfo = localeInfo;
                this.currentPluralRules = (PluralRules) this.allRules.get(args[0]);
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = new PluralRules_None();
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                if (this.reloadLastFile) {
                    if (init) {
                        i = currentAccount;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LocaleController.this.reloadCurrentRemoteLocale(i);
                            }
                        });
                    } else {
                        reloadCurrentRemoteLocale(currentAccount);
                    }
                    this.reloadLastFile = false;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public static String getCurrentLanguageName() {
        return getString("LanguageName", R.string.LanguageName);
    }

    private String getStringInternal(String key, int res) {
        String value = (String) this.localeValues.get(key);
        if (value == null) {
            try {
                value = ApplicationLoader.applicationContext.getString(res);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        if (value == null) {
            return "LOC_ERR:" + key;
        }
        return value;
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    public static String getPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        return getString(param, ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        return formatString(param, ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(plural));
    }

    public static String formatString(String key, int res, Object... args) {
        try {
            String value = (String) getInstance().localeValues.get(key);
            if (value == null) {
                value = ApplicationLoader.applicationContext.getString(res);
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, value, args);
            }
            return String.format(value, args);
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR: " + key;
        }
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return formatPluralString("Seconds", ttl);
        }
        if (ttl < 3600) {
            return formatPluralString("Minutes", ttl / 60);
        }
        if (ttl < 86400) {
            return formatPluralString("Hours", (ttl / 60) / 60);
        }
        if (ttl < 604800) {
            return formatPluralString("Days", ((ttl / 60) / 60) / 24);
        }
        int days = ((ttl / 60) / 60) / 24;
        if (ttl % 7 == 0) {
            return formatPluralString("Weeks", days / 7);
        }
        return String.format("%s %s", new Object[]{formatPluralString("Weeks", days / 7), formatPluralString("Days", days % 7)});
    }

    public String formatCurrencyString(long amount, String type) {
        boolean discount;
        String customFormat;
        double doubleAmount;
        type = type.toUpperCase();
        if (amount < 0) {
            discount = true;
        } else {
            discount = false;
        }
        amount = Math.abs(amount);
        Currency сurrency = Currency.getInstance(type);
        int i = -1;
        switch (type.hashCode()) {
            case 65726:
                if (type.equals("BHD")) {
                    i = 2;
                    break;
                }
                break;
            case 65759:
                if (type.equals("BIF")) {
                    i = 9;
                    break;
                }
                break;
            case 66267:
                if (type.equals("BYR")) {
                    i = 10;
                    break;
                }
                break;
            case 66813:
                if (type.equals("CLF")) {
                    i = 0;
                    break;
                }
                break;
            case 66823:
                if (type.equals("CLP")) {
                    i = 11;
                    break;
                }
                break;
            case 67122:
                if (type.equals("CVE")) {
                    i = 12;
                    break;
                }
                break;
            case 67712:
                if (type.equals("DJF")) {
                    i = 13;
                    break;
                }
                break;
            case 70719:
                if (type.equals("GNF")) {
                    i = 14;
                    break;
                }
                break;
            case 72732:
                if (type.equals("IQD")) {
                    i = 3;
                    break;
                }
                break;
            case 72777:
                if (type.equals("IRR")) {
                    i = 1;
                    break;
                }
                break;
            case 72801:
                if (type.equals("ISK")) {
                    i = 15;
                    break;
                }
                break;
            case 73631:
                if (type.equals("JOD")) {
                    i = 4;
                    break;
                }
                break;
            case 73683:
                if (type.equals("JPY")) {
                    i = 16;
                    break;
                }
                break;
            case 74532:
                if (type.equals("KMF")) {
                    i = 17;
                    break;
                }
                break;
            case 74704:
                if (type.equals("KRW")) {
                    i = 18;
                    break;
                }
                break;
            case 74840:
                if (type.equals("KWD")) {
                    i = 5;
                    break;
                }
                break;
            case 75863:
                if (type.equals("LYD")) {
                    i = 6;
                    break;
                }
                break;
            case 76263:
                if (type.equals("MGA")) {
                    i = 19;
                    break;
                }
                break;
            case 76618:
                if (type.equals("MRO")) {
                    i = 29;
                    break;
                }
                break;
            case 78388:
                if (type.equals("OMR")) {
                    i = 7;
                    break;
                }
                break;
            case 79710:
                if (type.equals("PYG")) {
                    i = 20;
                    break;
                }
                break;
            case 81569:
                if (type.equals("RWF")) {
                    i = 21;
                    break;
                }
                break;
            case 83210:
                if (type.equals("TND")) {
                    i = 8;
                    break;
                }
                break;
            case 83974:
                if (type.equals("UGX")) {
                    i = 22;
                    break;
                }
                break;
            case 84517:
                if (type.equals("UYI")) {
                    i = 23;
                    break;
                }
                break;
            case 85132:
                if (type.equals("VND")) {
                    i = 24;
                    break;
                }
                break;
            case 85367:
                if (type.equals("VUV")) {
                    i = 25;
                    break;
                }
                break;
            case 86653:
                if (type.equals("XAF")) {
                    i = 26;
                    break;
                }
                break;
            case 87087:
                if (type.equals("XOF")) {
                    i = 27;
                    break;
                }
                break;
            case 87118:
                if (type.equals("XPF")) {
                    i = 28;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                customFormat = " %.4f";
                doubleAmount = ((double) amount) / 10000.0d;
                break;
            case 1:
                doubleAmount = (double) (((float) amount) / 100.0f);
                if (amount % 100 != 0) {
                    customFormat = " %.2f";
                    break;
                }
                customFormat = " %.0f";
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                customFormat = " %.3f";
                doubleAmount = ((double) amount) / 1000.0d;
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
            case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
            case 25:
            case 26:
            case 27:
            case 28:
                customFormat = " %.0f";
                doubleAmount = (double) amount;
                break;
            case 29:
                customFormat = " %.1f";
                doubleAmount = ((double) amount) / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                doubleAmount = ((double) amount) / 100.0d;
                break;
        }
        if (сurrency != null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(this.currentLocale != null ? this.currentLocale : this.systemDefaultLocale);
            format.setCurrency(сurrency);
            if (type.equals("IRR")) {
                format.setMaximumFractionDigits(0);
            }
            return (discount ? "-" : TtmlNode.ANONYMOUS_REGION_ID) + format.format(doubleAmount);
        }
        return (discount ? "-" : TtmlNode.ANONYMOUS_REGION_ID) + String.format(Locale.US, type + customFormat, new Object[]{Double.valueOf(doubleAmount)});
    }

    public String formatCurrencyDecimalString(long amount, String type, boolean inludeType) {
        String customFormat;
        double doubleAmount;
        type = type.toUpperCase();
        amount = Math.abs(amount);
        int i = -1;
        switch (type.hashCode()) {
            case 65726:
                if (type.equals("BHD")) {
                    i = 2;
                    break;
                }
                break;
            case 65759:
                if (type.equals("BIF")) {
                    i = 9;
                    break;
                }
                break;
            case 66267:
                if (type.equals("BYR")) {
                    i = 10;
                    break;
                }
                break;
            case 66813:
                if (type.equals("CLF")) {
                    i = 0;
                    break;
                }
                break;
            case 66823:
                if (type.equals("CLP")) {
                    i = 11;
                    break;
                }
                break;
            case 67122:
                if (type.equals("CVE")) {
                    i = 12;
                    break;
                }
                break;
            case 67712:
                if (type.equals("DJF")) {
                    i = 13;
                    break;
                }
                break;
            case 70719:
                if (type.equals("GNF")) {
                    i = 14;
                    break;
                }
                break;
            case 72732:
                if (type.equals("IQD")) {
                    i = 3;
                    break;
                }
                break;
            case 72777:
                if (type.equals("IRR")) {
                    i = 1;
                    break;
                }
                break;
            case 72801:
                if (type.equals("ISK")) {
                    i = 15;
                    break;
                }
                break;
            case 73631:
                if (type.equals("JOD")) {
                    i = 4;
                    break;
                }
                break;
            case 73683:
                if (type.equals("JPY")) {
                    i = 16;
                    break;
                }
                break;
            case 74532:
                if (type.equals("KMF")) {
                    i = 17;
                    break;
                }
                break;
            case 74704:
                if (type.equals("KRW")) {
                    i = 18;
                    break;
                }
                break;
            case 74840:
                if (type.equals("KWD")) {
                    i = 5;
                    break;
                }
                break;
            case 75863:
                if (type.equals("LYD")) {
                    i = 6;
                    break;
                }
                break;
            case 76263:
                if (type.equals("MGA")) {
                    i = 19;
                    break;
                }
                break;
            case 76618:
                if (type.equals("MRO")) {
                    i = 29;
                    break;
                }
                break;
            case 78388:
                if (type.equals("OMR")) {
                    i = 7;
                    break;
                }
                break;
            case 79710:
                if (type.equals("PYG")) {
                    i = 20;
                    break;
                }
                break;
            case 81569:
                if (type.equals("RWF")) {
                    i = 21;
                    break;
                }
                break;
            case 83210:
                if (type.equals("TND")) {
                    i = 8;
                    break;
                }
                break;
            case 83974:
                if (type.equals("UGX")) {
                    i = 22;
                    break;
                }
                break;
            case 84517:
                if (type.equals("UYI")) {
                    i = 23;
                    break;
                }
                break;
            case 85132:
                if (type.equals("VND")) {
                    i = 24;
                    break;
                }
                break;
            case 85367:
                if (type.equals("VUV")) {
                    i = 25;
                    break;
                }
                break;
            case 86653:
                if (type.equals("XAF")) {
                    i = 26;
                    break;
                }
                break;
            case 87087:
                if (type.equals("XOF")) {
                    i = 27;
                    break;
                }
                break;
            case 87118:
                if (type.equals("XPF")) {
                    i = 28;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                customFormat = " %.4f";
                doubleAmount = ((double) amount) / 10000.0d;
                break;
            case 1:
                doubleAmount = (double) (((float) amount) / 100.0f);
                if (amount % 100 != 0) {
                    customFormat = " %.2f";
                    break;
                }
                customFormat = " %.0f";
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                customFormat = " %.3f";
                doubleAmount = ((double) amount) / 1000.0d;
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
            case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
            case 25:
            case 26:
            case 27:
            case 28:
                customFormat = " %.0f";
                doubleAmount = (double) amount;
                break;
            case 29:
                customFormat = " %.1f";
                doubleAmount = ((double) amount) / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                doubleAmount = ((double) amount) / 100.0d;
                break;
        }
        Locale locale = Locale.US;
        if (!inludeType) {
            type = TtmlNode.ANONYMOUS_REGION_ID + customFormat;
        }
        return String.format(locale, type, new Object[]{Double.valueOf(doubleAmount)}).trim();
    }

    public static String formatStringSimple(String string, Object... args) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, string, args);
            }
            return String.format(string, args);
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR: " + string;
        }
    }

    public static String formatCallDuration(int duration) {
        if (duration > 3600) {
            String result = formatPluralString("Hours", duration / 3600);
            int minutes = (duration % 3600) / 60;
            if (minutes > 0) {
                return result + ", " + formatPluralString("Minutes", minutes);
            }
            return result;
        } else if (duration > 60) {
            return formatPluralString("Minutes", duration / 60);
        } else {
            return formatPluralString("Seconds", duration);
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
                return;
            }
            Locale newLocale = newConfig.locale;
            if (newLocale != null) {
                String d1 = newLocale.getDisplayName();
                String d2 = this.currentLocale.getDisplayName();
                if (!(d1 == null || d2 == null || d1.equals(d2))) {
                    recreateFormatters();
                }
                this.currentLocale = newLocale;
                this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get("en");
                }
            }
        }
    }

    public static String formatDateChat(long date) {
        try {
            date *= 1000;
            Calendar.getInstance().setTimeInMillis(date);
            if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                return getInstance().chatDate.format(date);
            }
            return getInstance().chatFullDate.format(date);
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return getString("Yesterday", R.string.Yesterday);
            }
            if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                return getInstance().formatterMonth.format(new Date(date));
            }
            return getInstance().formatterYear.format(new Date(date));
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date)));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date)));
            } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
            } else {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date)));
            } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().chatDate.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
            } else {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            Object[] objArr;
            if (dateDay == day && year == dateYear) {
                int diff = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (date / 1000))) / 60;
                if (diff < 1) {
                    return getString("LocationUpdatedJustNow", R.string.LocationUpdatedJustNow);
                }
                if (diff < 60) {
                    return formatPluralString("UpdatedMinutes", diff);
                }
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date)));
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, objArr);
            } else if (dateDay + 1 == day && year == dateYear) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date)));
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, objArr);
            } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                format = formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, format);
            } else {
                format = formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, format);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int time) {
        int i = 1;
        int hours = (time / 60) / 60;
        time -= (hours * 60) * 60;
        int minutes = time / 60;
        time -= minutes * 60;
        String str;
        Object[] objArr;
        if (hours != 0) {
            str = "%dh";
            objArr = new Object[1];
            if (minutes <= 30) {
                i = 0;
            }
            objArr[0] = Integer.valueOf(i + hours);
            return String.format(str, objArr);
        } else if (minutes != 0) {
            str = "%d";
            objArr = new Object[1];
            if (time <= 30) {
                i = 0;
            }
            objArr[0] = Integer.valueOf(i + minutes);
            return String.format(str, objArr);
        } else {
            return String.format("%d", new Object[]{Integer.valueOf(time)});
        }
    }

    public static String formatDateOnline(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            Object[] objArr;
            if (dateDay == day && year == dateYear) {
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date)));
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, objArr);
            } else if (dateDay + 1 == day && year == dateYear) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date)));
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, objArr);
            } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                format = formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, format);
            } else {
                format = formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date)), getInstance().formatterDay.format(new Date(date)));
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, format);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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

    public void recreateFormatters() {
        int i = 1;
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String lang = locale.getLanguage();
        if (lang == null) {
            lang = "en";
        }
        lang = lang.toLowerCase();
        boolean z = lang.startsWith("ar") || lang.startsWith("fa") || (BuildVars.DEBUG_VERSION && (lang.startsWith("he") || lang.startsWith("iw")));
        isRTL = z;
        if (lang.equals("ko")) {
            i = 2;
        }
        nameDisplayOrder = i;
        this.formatterMonth = createFormatter(locale, getStringInternal("formatterMonth", R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale, getStringInternal("chatDate", R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", R.string.formatterWeek), "EEE");
        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", R.string.formatterMonthYear), "MMMM yyyy");
        Locale locale2 = (lang.toLowerCase().equals("ar") || lang.toLowerCase().equals("ko")) ? locale : Locale.US;
        this.formatterDay = createFormatter(locale2, is24HourFormat ? getStringInternal("formatterDay24H", R.string.formatterDay24H) : getStringInternal("formatterDay12H", R.string.formatterDay12H), is24HourFormat ? "HH:mm" : "h:mm a");
        this.formatterStats = createFormatter(locale, is24HourFormat ? getStringInternal("formatterStats24H", R.string.formatterStats24H) : getStringInternal("formatterStats12H", R.string.formatterStats12H), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
        this.formatterBannedUntil = createFormatter(locale, is24HourFormat ? getStringInternal("formatterBannedUntil24H", R.string.formatterBannedUntil24H) : getStringInternal("formatterBannedUntil12H", R.string.formatterBannedUntil12H), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
        this.formatterBannedUntilThisYear = createFormatter(locale, is24HourFormat ? getStringInternal("formatterBannedUntilThisYear24H", R.string.formatterBannedUntilThisYear24H) : getStringInternal("formatterBannedUntilThisYear12H", R.string.formatterBannedUntilThisYear12H), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
    }

    public static boolean isRTLCharacter(char ch) {
        return Character.getDirectionality(ch) == (byte) 1 || Character.getDirectionality(ch) == (byte) 2 || Character.getDirectionality(ch) == (byte) 16 || Character.getDirectionality(ch) == (byte) 17;
    }

    public static String formatDateForBan(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date);
            if (year == rightNow.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(date));
            }
            return getInstance().formatterBannedUntil.format(new Date(date));
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long date) {
        date *= 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(6);
            if (Math.abs(System.currentTimeMillis() - date) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(date));
            }
            int dayDiff = dateDay - day;
            if (dayDiff == 0 || (dayDiff == -1 && System.currentTimeMillis() - date < 28800000)) {
                return getInstance().formatterDay.format(new Date(date));
            }
            if (dayDiff <= -7 || dayDiff > -1) {
                return getInstance().formatterMonth.format(new Date(date));
            }
            return getInstance().formatterWeek.format(new Date(date));
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            double value = ((double) number) + (((double) lastDec) / 10.0d);
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

    public static String formatUserStatus(int currentAccount, User user) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            return getString("Online", R.string.Online);
        }
        if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TL_userEmpty)) {
            return getString("ALongTimeAgo", R.string.ALongTimeAgo);
        }
        if (user.status.expires > ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
            return getString("Online", R.string.Online);
        }
        if (user.status.expires == -1) {
            return getString("Invisible", R.string.Invisible);
        }
        if (user.status.expires == -100) {
            return getString("Lately", R.string.Lately);
        }
        if (user.status.expires == -101) {
            return getString("WithinAWeek", R.string.WithinAWeek);
        }
        if (user.status.expires == -102) {
            return getString("WithinAMonth", R.string.WithinAMonth);
        }
        return formatDateOnline((long) user.status.expires);
    }

    private String escapeString(String str) {
        return str.contains("[CDATA") ? str : str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStrings(final TL_langPackDifference difference, int currentAccount) {
        if (difference != null && !difference.strings.isEmpty() && this.currentLocaleInfo != null) {
            final String langCode = difference.lang_code.replace('-', '_').toLowerCase();
            if (langCode.equals(this.currentLocaleInfo.shortName)) {
                File finalFile = new File(ApplicationLoader.getFilesDirFixed(), "remote_" + langCode + ".xml");
                try {
                    HashMap<String, String> values;
                    if (difference.from_version == 0) {
                        values = new HashMap();
                    } else {
                        values = getLocaleFileStrings(finalFile, true);
                    }
                    for (int a = 0; a < difference.strings.size(); a++) {
                        LangPackString string = (LangPackString) difference.strings.get(a);
                        if (string instanceof TL_langPackString) {
                            values.put(string.key, escapeString(string.value));
                        } else if (string instanceof TL_langPackStringPluralized) {
                            Object escapeString;
                            values.put(string.key + "_zero", string.zero_value != null ? escapeString(string.zero_value) : TtmlNode.ANONYMOUS_REGION_ID);
                            values.put(string.key + "_one", string.one_value != null ? escapeString(string.one_value) : TtmlNode.ANONYMOUS_REGION_ID);
                            values.put(string.key + "_two", string.two_value != null ? escapeString(string.two_value) : TtmlNode.ANONYMOUS_REGION_ID);
                            values.put(string.key + "_few", string.few_value != null ? escapeString(string.few_value) : TtmlNode.ANONYMOUS_REGION_ID);
                            values.put(string.key + "_many", string.many_value != null ? escapeString(string.many_value) : TtmlNode.ANONYMOUS_REGION_ID);
                            String str = string.key + "_other";
                            if (string.other_value != null) {
                                escapeString = escapeString(string.other_value);
                            } else {
                                escapeString = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            values.put(str, escapeString);
                        } else if (string instanceof TL_langPackStringDeleted) {
                            values.remove(string.key);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("save locale file to " + finalFile);
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile));
                    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    writer.write("<resources>\n");
                    for (Entry<String, String> entry : values.entrySet()) {
                        writer.write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[]{entry.getKey(), entry.getValue()}));
                    }
                    writer.write("</resources>");
                    writer.close();
                    final HashMap<String, String> valuesToSet = getLocaleFileStrings(finalFile);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocaleInfo localeInfo = LocaleController.this.getLanguageFromDict(langCode);
                            if (localeInfo != null) {
                                localeInfo.version = difference.version;
                            }
                            LocaleController.this.saveOtherLanguages();
                            if (LocaleController.this.currentLocaleInfo == null || !LocaleController.this.currentLocaleInfo.isLocal()) {
                                try {
                                    Locale newLocale;
                                    String[] args = localeInfo.shortName.split("_");
                                    if (args.length == 1) {
                                        newLocale = new Locale(localeInfo.shortName);
                                    } else {
                                        newLocale = new Locale(args[0], args[1]);
                                    }
                                    if (newLocale != null) {
                                        LocaleController.this.languageOverride = localeInfo.shortName;
                                        Editor editor = MessagesController.getGlobalMainSettings().edit();
                                        editor.putString("language", localeInfo.getKey());
                                        editor.commit();
                                    }
                                    if (newLocale != null) {
                                        LocaleController.this.localeValues = valuesToSet;
                                        LocaleController.this.currentLocale = newLocale;
                                        LocaleController.this.currentLocaleInfo = localeInfo;
                                        LocaleController.this.currentPluralRules = (PluralRules) LocaleController.this.allRules.get(LocaleController.this.currentLocale.getLanguage());
                                        if (LocaleController.this.currentPluralRules == null) {
                                            LocaleController.this.currentPluralRules = (PluralRules) LocaleController.this.allRules.get("en");
                                        }
                                        LocaleController.this.changingConfiguration = true;
                                        Locale.setDefault(LocaleController.this.currentLocale);
                                        Configuration config = new Configuration();
                                        config.locale = LocaleController.this.currentLocale;
                                        ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                                        LocaleController.this.changingConfiguration = false;
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                    LocaleController.this.changingConfiguration = false;
                                }
                                LocaleController.this.recreateFormatters();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    public void loadRemoteLanguages(final int currentAccount) {
        if (!this.loadingRemoteLanguages) {
            this.loadingRemoteLanguages = true;
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TL_langpack_getLanguages(), new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    if (response != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                int a;
                                LocaleController.this.loadingRemoteLanguages = false;
                                Vector res = response;
                                HashMap<String, LocaleInfo> remoteLoaded = new HashMap();
                                LocaleController.this.remoteLanguages.clear();
                                for (a = 0; a < res.objects.size(); a++) {
                                    TL_langPackLanguage language = (TL_langPackLanguage) res.objects.get(a);
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("loaded lang " + language.name);
                                    }
                                    LocaleInfo localeInfo = new LocaleInfo();
                                    localeInfo.nameEnglish = language.name;
                                    localeInfo.name = language.native_name;
                                    localeInfo.shortName = language.lang_code.replace('-', '_').toLowerCase();
                                    localeInfo.pathToFile = "remote";
                                    LocaleInfo existing = LocaleController.this.getLanguageFromDict(localeInfo.getKey());
                                    if (existing == null) {
                                        LocaleController.this.languages.add(localeInfo);
                                        LocaleController.this.languagesDict.put(localeInfo.getKey(), localeInfo);
                                        existing = localeInfo;
                                    } else {
                                        existing.nameEnglish = localeInfo.nameEnglish;
                                        existing.name = localeInfo.name;
                                        existing.pathToFile = localeInfo.pathToFile;
                                        localeInfo = existing;
                                    }
                                    LocaleController.this.remoteLanguages.add(localeInfo);
                                    remoteLoaded.put(localeInfo.getKey(), existing);
                                }
                                a = 0;
                                while (a < LocaleController.this.languages.size()) {
                                    LocaleInfo info = (LocaleInfo) LocaleController.this.languages.get(a);
                                    if (!info.isBuiltIn() && info.isRemote() && ((LocaleInfo) remoteLoaded.get(info.getKey())) == null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("remove lang " + info.getKey());
                                        }
                                        LocaleController.this.languages.remove(a);
                                        LocaleController.this.languagesDict.remove(info.getKey());
                                        a--;
                                        if (info == LocaleController.this.currentLocaleInfo) {
                                            if (LocaleController.this.systemDefaultLocale.getLanguage() != null) {
                                                info = LocaleController.this.getLanguageFromDict(LocaleController.this.systemDefaultLocale.getLanguage());
                                            }
                                            if (info == null) {
                                                info = LocaleController.this.getLanguageFromDict(LocaleController.this.getLocaleString(LocaleController.this.systemDefaultLocale));
                                            }
                                            if (info == null) {
                                                info = LocaleController.this.getLanguageFromDict("en");
                                            }
                                            LocaleController.this.applyLanguage(info, true, false, currentAccount);
                                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                                        }
                                    }
                                    a++;
                                }
                                LocaleController.this.saveOtherLanguages();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
                                LocaleController.this.applyLanguage(LocaleController.this.currentLocaleInfo, true, false, currentAccount);
                            }
                        });
                    }
                }
            }, 8);
        }
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, boolean force, final int currentAccount) {
        if (localeInfo == null) {
            return;
        }
        if (localeInfo != null && !localeInfo.isRemote()) {
            return;
        }
        if (localeInfo.version == 0 || force) {
            for (int a = 0; a < 3; a++) {
                ConnectionsManager.setLangCode(localeInfo.shortName);
            }
            TL_langpack_getLangPack req = new TL_langpack_getLangPack();
            req.lang_code = localeInfo.shortName.replace("_", "-");
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    if (response != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LocaleController.this.saveRemoteLocaleStrings((TL_langPackDifference) response, currentAccount);
                            }
                        });
                    }
                }
            }, 8);
            return;
        }
        TL_langpack_getDifference req2 = new TL_langpack_getDifference();
        req2.from_version = localeInfo.version;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                if (response != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocaleController.this.saveRemoteLocaleStrings((TL_langPackDifference) response, currentAccount);
                        }
                    });
                }
            }
        }, 8);
    }

    public String getTranslitString(String src) {
        if (this.translitChars == null) {
            this.translitChars = new HashMap(520);
            this.translitChars.put("\u023c", "c");
            this.translitChars.put("\u1d87", "n");
            this.translitChars.put("\u0256", "d");
            this.translitChars.put("\u1eff", "y");
            this.translitChars.put("\u1d13", "o");
            this.translitChars.put("\u00f8", "o");
            this.translitChars.put("\u1e01", "a");
            this.translitChars.put("\u02af", "h");
            this.translitChars.put("\u0177", "y");
            this.translitChars.put("\u029e", "k");
            this.translitChars.put("\u1eeb", "u");
            this.translitChars.put("\ua733", "aa");
            this.translitChars.put("\u0133", "ij");
            this.translitChars.put("\u1e3d", "l");
            this.translitChars.put("\u026a", "i");
            this.translitChars.put("\u1e07", "b");
            this.translitChars.put("\u0280", "r");
            this.translitChars.put("\u011b", "e");
            this.translitChars.put("\ufb03", "ffi");
            this.translitChars.put("\u01a1", "o");
            this.translitChars.put("\u2c79", "r");
            this.translitChars.put("\u1ed3", "o");
            this.translitChars.put("\u01d0", "i");
            this.translitChars.put("\ua755", TtmlNode.TAG_P);
            this.translitChars.put("\u00fd", "y");
            this.translitChars.put("\u1e1d", "e");
            this.translitChars.put("\u2092", "o");
            this.translitChars.put("\u2c65", "a");
            this.translitChars.put("\u0299", "b");
            this.translitChars.put("\u1e1b", "e");
            this.translitChars.put("\u0188", "c");
            this.translitChars.put("\u0266", "h");
            this.translitChars.put("\u1d6c", "b");
            this.translitChars.put("\u1e63", "s");
            this.translitChars.put("\u0111", "d");
            this.translitChars.put("\u1ed7", "o");
            this.translitChars.put("\u025f", "j");
            this.translitChars.put("\u1e9a", "a");
            this.translitChars.put("\u024f", "y");
            this.translitChars.put("\u043b", "l");
            this.translitChars.put("\u028c", "v");
            this.translitChars.put("\ua753", TtmlNode.TAG_P);
            this.translitChars.put("\ufb01", "fi");
            this.translitChars.put("\u1d84", "k");
            this.translitChars.put("\u1e0f", "d");
            this.translitChars.put("\u1d0c", "l");
            this.translitChars.put("\u0117", "e");
            this.translitChars.put("\u0451", "yo");
            this.translitChars.put("\u1d0b", "k");
            this.translitChars.put("\u010b", "c");
            this.translitChars.put("\u0281", "r");
            this.translitChars.put("\u0195", "hv");
            this.translitChars.put("\u0180", "b");
            this.translitChars.put("\u1e4d", "o");
            this.translitChars.put("\u0223", "ou");
            this.translitChars.put("\u01f0", "j");
            this.translitChars.put("\u1d83", "g");
            this.translitChars.put("\u1e4b", "n");
            this.translitChars.put("\u0249", "j");
            this.translitChars.put("\u01e7", "g");
            this.translitChars.put("\u01f3", "dz");
            this.translitChars.put("\u017a", "z");
            this.translitChars.put("\ua737", "au");
            this.translitChars.put("\u01d6", "u");
            this.translitChars.put("\u1d79", "g");
            this.translitChars.put("\u022f", "o");
            this.translitChars.put("\u0250", "a");
            this.translitChars.put("\u0105", "a");
            this.translitChars.put("\u00f5", "o");
            this.translitChars.put("\u027b", "r");
            this.translitChars.put("\ua74d", "o");
            this.translitChars.put("\u01df", "a");
            this.translitChars.put("\u0234", "l");
            this.translitChars.put("\u0282", "s");
            this.translitChars.put("\ufb02", "fl");
            this.translitChars.put("\u0209", "i");
            this.translitChars.put("\u2c7b", "e");
            this.translitChars.put("\u1e49", "n");
            this.translitChars.put("\u00ef", "i");
            this.translitChars.put("\u00f1", "n");
            this.translitChars.put("\u1d09", "i");
            this.translitChars.put("\u0287", "t");
            this.translitChars.put("\u1e93", "z");
            this.translitChars.put("\u1ef7", "y");
            this.translitChars.put("\u0233", "y");
            this.translitChars.put("\u1e69", "s");
            this.translitChars.put("\u027d", "r");
            this.translitChars.put("\u011d", "g");
            this.translitChars.put("\u0432", "v");
            this.translitChars.put("\u1d1d", "u");
            this.translitChars.put("\u1e33", "k");
            this.translitChars.put("\ua76b", "et");
            this.translitChars.put("\u012b", "i");
            this.translitChars.put("\u0165", "t");
            this.translitChars.put("\ua73f", "c");
            this.translitChars.put("\u029f", "l");
            this.translitChars.put("\ua739", "av");
            this.translitChars.put("\u00fb", "u");
            this.translitChars.put("\u00e6", "ae");
            this.translitChars.put("\u0438", "i");
            this.translitChars.put("\u0103", "a");
            this.translitChars.put("\u01d8", "u");
            this.translitChars.put("\ua785", "s");
            this.translitChars.put("\u1d63", "r");
            this.translitChars.put("\u1d00", "a");
            this.translitChars.put("\u0183", "b");
            this.translitChars.put("\u1e29", "h");
            this.translitChars.put("\u1e67", "s");
            this.translitChars.put("\u2091", "e");
            this.translitChars.put("\u029c", "h");
            this.translitChars.put("\u1e8b", "x");
            this.translitChars.put("\ua745", "k");
            this.translitChars.put("\u1e0b", "d");
            this.translitChars.put("\u01a3", "oi");
            this.translitChars.put("\ua751", TtmlNode.TAG_P);
            this.translitChars.put("\u0127", "h");
            this.translitChars.put("\u2c74", "v");
            this.translitChars.put("\u1e87", "w");
            this.translitChars.put("\u01f9", "n");
            this.translitChars.put("\u026f", "m");
            this.translitChars.put("\u0261", "g");
            this.translitChars.put("\u0274", "n");
            this.translitChars.put("\u1d18", TtmlNode.TAG_P);
            this.translitChars.put("\u1d65", "v");
            this.translitChars.put("\u016b", "u");
            this.translitChars.put("\u1e03", "b");
            this.translitChars.put("\u1e57", TtmlNode.TAG_P);
            this.translitChars.put("\u044c", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u00e5", "a");
            this.translitChars.put("\u0255", "c");
            this.translitChars.put("\u1ecd", "o");
            this.translitChars.put("\u1eaf", "a");
            this.translitChars.put("\u0192", "f");
            this.translitChars.put("\u01e3", "ae");
            this.translitChars.put("\ua761", "vy");
            this.translitChars.put("\ufb00", "ff");
            this.translitChars.put("\u1d89", "r");
            this.translitChars.put("\u00f4", "o");
            this.translitChars.put("\u01ff", "o");
            this.translitChars.put("\u1e73", "u");
            this.translitChars.put("\u0225", "z");
            this.translitChars.put("\u1e1f", "f");
            this.translitChars.put("\u1e13", "d");
            this.translitChars.put("\u0207", "e");
            this.translitChars.put("\u0215", "u");
            this.translitChars.put("\u043f", TtmlNode.TAG_P);
            this.translitChars.put("\u0235", "n");
            this.translitChars.put("\u02a0", "q");
            this.translitChars.put("\u1ea5", "a");
            this.translitChars.put("\u01e9", "k");
            this.translitChars.put("\u0129", "i");
            this.translitChars.put("\u1e75", "u");
            this.translitChars.put("\u0167", "t");
            this.translitChars.put("\u027e", "r");
            this.translitChars.put("\u0199", "k");
            this.translitChars.put("\u1e6b", "t");
            this.translitChars.put("\ua757", "q");
            this.translitChars.put("\u1ead", "a");
            this.translitChars.put("\u043d", "n");
            this.translitChars.put("\u0284", "j");
            this.translitChars.put("\u019a", "l");
            this.translitChars.put("\u1d82", "f");
            this.translitChars.put("\u0434", "d");
            this.translitChars.put("\u1d74", "s");
            this.translitChars.put("\ua783", "r");
            this.translitChars.put("\u1d8c", "v");
            this.translitChars.put("\u0275", "o");
            this.translitChars.put("\u1e09", "c");
            this.translitChars.put("\u1d64", "u");
            this.translitChars.put("\u1e91", "z");
            this.translitChars.put("\u1e79", "u");
            this.translitChars.put("\u0148", "n");
            this.translitChars.put("\u028d", "w");
            this.translitChars.put("\u1ea7", "a");
            this.translitChars.put("\u01c9", "lj");
            this.translitChars.put("\u0253", "b");
            this.translitChars.put("\u027c", "r");
            this.translitChars.put("\u00f2", "o");
            this.translitChars.put("\u1e98", "w");
            this.translitChars.put("\u0257", "d");
            this.translitChars.put("\ua73d", "ay");
            this.translitChars.put("\u01b0", "u");
            this.translitChars.put("\u1d80", "b");
            this.translitChars.put("\u01dc", "u");
            this.translitChars.put("\u1eb9", "e");
            this.translitChars.put("\u01e1", "a");
            this.translitChars.put("\u0265", "h");
            this.translitChars.put("\u1e4f", "o");
            this.translitChars.put("\u01d4", "u");
            this.translitChars.put("\u028e", "y");
            this.translitChars.put("\u0231", "o");
            this.translitChars.put("\u1ec7", "e");
            this.translitChars.put("\u1ebf", "e");
            this.translitChars.put("\u012d", "i");
            this.translitChars.put("\u2c78", "e");
            this.translitChars.put("\u1e6f", "t");
            this.translitChars.put("\u1d91", "d");
            this.translitChars.put("\u1e27", "h");
            this.translitChars.put("\u1e65", "s");
            this.translitChars.put("\u00eb", "e");
            this.translitChars.put("\u1d0d", "m");
            this.translitChars.put("\u00f6", "o");
            this.translitChars.put("\u00e9", "e");
            this.translitChars.put("\u0131", "i");
            this.translitChars.put("\u010f", "d");
            this.translitChars.put("\u1d6f", "m");
            this.translitChars.put("\u1ef5", "y");
            this.translitChars.put("\u044f", "ya");
            this.translitChars.put("\u0175", "w");
            this.translitChars.put("\u1ec1", "e");
            this.translitChars.put("\u1ee9", "u");
            this.translitChars.put("\u01b6", "z");
            this.translitChars.put("\u0135", "j");
            this.translitChars.put("\u1e0d", "d");
            this.translitChars.put("\u016d", "u");
            this.translitChars.put("\u029d", "j");
            this.translitChars.put("\u0436", "zh");
            this.translitChars.put("\u00ea", "e");
            this.translitChars.put("\u01da", "u");
            this.translitChars.put("\u0121", "g");
            this.translitChars.put("\u1e59", "r");
            this.translitChars.put("\u019e", "n");
            this.translitChars.put("\u044a", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u1e17", "e");
            this.translitChars.put("\u1e9d", "s");
            this.translitChars.put("\u1d81", "d");
            this.translitChars.put("\u0137", "k");
            this.translitChars.put("\u1d02", "ae");
            this.translitChars.put("\u0258", "e");
            this.translitChars.put("\u1ee3", "o");
            this.translitChars.put("\u1e3f", "m");
            this.translitChars.put("\ua730", "f");
            this.translitChars.put("\u0430", "a");
            this.translitChars.put("\u1eb5", "a");
            this.translitChars.put("\ua74f", "oo");
            this.translitChars.put("\u1d86", "m");
            this.translitChars.put("\u1d7d", TtmlNode.TAG_P);
            this.translitChars.put("\u0446", "ts");
            this.translitChars.put("\u1eef", "u");
            this.translitChars.put("\u2c6a", "k");
            this.translitChars.put("\u1e25", "h");
            this.translitChars.put("\u0163", "t");
            this.translitChars.put("\u1d71", TtmlNode.TAG_P);
            this.translitChars.put("\u1e41", "m");
            this.translitChars.put("\u00e1", "a");
            this.translitChars.put("\u1d0e", "n");
            this.translitChars.put("\ua75f", "v");
            this.translitChars.put("\u00e8", "e");
            this.translitChars.put("\u1d8e", "z");
            this.translitChars.put("\ua77a", "d");
            this.translitChars.put("\u1d88", TtmlNode.TAG_P);
            this.translitChars.put("\u043c", "m");
            this.translitChars.put("\u026b", "l");
            this.translitChars.put("\u1d22", "z");
            this.translitChars.put("\u0271", "m");
            this.translitChars.put("\u1e5d", "r");
            this.translitChars.put("\u1e7d", "v");
            this.translitChars.put("\u0169", "u");
            this.translitChars.put("\u00df", "ss");
            this.translitChars.put("\u0442", "t");
            this.translitChars.put("\u0125", "h");
            this.translitChars.put("\u1d75", "t");
            this.translitChars.put("\u0290", "z");
            this.translitChars.put("\u1e5f", "r");
            this.translitChars.put("\u0272", "n");
            this.translitChars.put("\u00e0", "a");
            this.translitChars.put("\u1e99", "y");
            this.translitChars.put("\u1ef3", "y");
            this.translitChars.put("\u1d14", "oe");
            this.translitChars.put("\u044b", "i");
            this.translitChars.put("\u2093", "x");
            this.translitChars.put("\u0217", "u");
            this.translitChars.put("\u2c7c", "j");
            this.translitChars.put("\u1eab", "a");
            this.translitChars.put("\u0291", "z");
            this.translitChars.put("\u1e9b", "s");
            this.translitChars.put("\u1e2d", "i");
            this.translitChars.put("\ua735", "ao");
            this.translitChars.put("\u0240", "z");
            this.translitChars.put("\u00ff", "y");
            this.translitChars.put("\u01dd", "e");
            this.translitChars.put("\u01ed", "o");
            this.translitChars.put("\u1d05", "d");
            this.translitChars.put("\u1d85", "l");
            this.translitChars.put("\u00f9", "u");
            this.translitChars.put("\u1ea1", "a");
            this.translitChars.put("\u1e05", "b");
            this.translitChars.put("\u1ee5", "u");
            this.translitChars.put("\u043a", "k");
            this.translitChars.put("\u1eb1", "a");
            this.translitChars.put("\u1d1b", "t");
            this.translitChars.put("\u01b4", "y");
            this.translitChars.put("\u2c66", "t");
            this.translitChars.put("\u0437", "z");
            this.translitChars.put("\u2c61", "l");
            this.translitChars.put("\u0237", "j");
            this.translitChars.put("\u1d76", "z");
            this.translitChars.put("\u1e2b", "h");
            this.translitChars.put("\u2c73", "w");
            this.translitChars.put("\u1e35", "k");
            this.translitChars.put("\u1edd", "o");
            this.translitChars.put("\u00ee", "i");
            this.translitChars.put("\u0123", "g");
            this.translitChars.put("\u0205", "e");
            this.translitChars.put("\u0227", "a");
            this.translitChars.put("\u1eb3", "a");
            this.translitChars.put("\u0449", "sch");
            this.translitChars.put("\u024b", "q");
            this.translitChars.put("\u1e6d", "t");
            this.translitChars.put("\ua778", "um");
            this.translitChars.put("\u1d04", "c");
            this.translitChars.put("\u1e8d", "x");
            this.translitChars.put("\u1ee7", "u");
            this.translitChars.put("\u1ec9", "i");
            this.translitChars.put("\u1d1a", "r");
            this.translitChars.put("\u015b", "s");
            this.translitChars.put("\ua74b", "o");
            this.translitChars.put("\u1ef9", "y");
            this.translitChars.put("\u1e61", "s");
            this.translitChars.put("\u01cc", "nj");
            this.translitChars.put("\u0201", "a");
            this.translitChars.put("\u1e97", "t");
            this.translitChars.put("\u013a", "l");
            this.translitChars.put("\u017e", "z");
            this.translitChars.put("\u1d7a", "th");
            this.translitChars.put("\u018c", "d");
            this.translitChars.put("\u0219", "s");
            this.translitChars.put("\u0161", "s");
            this.translitChars.put("\u1d99", "u");
            this.translitChars.put("\u1ebd", "e");
            this.translitChars.put("\u1e9c", "s");
            this.translitChars.put("\u0247", "e");
            this.translitChars.put("\u1e77", "u");
            this.translitChars.put("\u1ed1", "o");
            this.translitChars.put("\u023f", "s");
            this.translitChars.put("\u1d20", "v");
            this.translitChars.put("\ua76d", "is");
            this.translitChars.put("\u1d0f", "o");
            this.translitChars.put("\u025b", "e");
            this.translitChars.put("\u01fb", "a");
            this.translitChars.put("\ufb04", "ffl");
            this.translitChars.put("\u2c7a", "o");
            this.translitChars.put("\u020b", "i");
            this.translitChars.put("\u1d6b", "ue");
            this.translitChars.put("\u0221", "d");
            this.translitChars.put("\u2c6c", "z");
            this.translitChars.put("\u1e81", "w");
            this.translitChars.put("\u1d8f", "a");
            this.translitChars.put("\ua787", "t");
            this.translitChars.put("\u011f", "g");
            this.translitChars.put("\u0273", "n");
            this.translitChars.put("\u029b", "g");
            this.translitChars.put("\u1d1c", "u");
            this.translitChars.put("\u0444", "f");
            this.translitChars.put("\u1ea9", "a");
            this.translitChars.put("\u1e45", "n");
            this.translitChars.put("\u0268", "i");
            this.translitChars.put("\u1d19", "r");
            this.translitChars.put("\u01ce", "a");
            this.translitChars.put("\u017f", "s");
            this.translitChars.put("\u0443", "u");
            this.translitChars.put("\u022b", "o");
            this.translitChars.put("\u027f", "r");
            this.translitChars.put("\u01ad", "t");
            this.translitChars.put("\u1e2f", "i");
            this.translitChars.put("\u01fd", "ae");
            this.translitChars.put("\u2c71", "v");
            this.translitChars.put("\u0276", "oe");
            this.translitChars.put("\u1e43", "m");
            this.translitChars.put("\u017c", "z");
            this.translitChars.put("\u0115", "e");
            this.translitChars.put("\ua73b", "av");
            this.translitChars.put("\u1edf", "o");
            this.translitChars.put("\u1ec5", "e");
            this.translitChars.put("\u026c", "l");
            this.translitChars.put("\u1ecb", "i");
            this.translitChars.put("\u1d6d", "d");
            this.translitChars.put("\ufb06", "st");
            this.translitChars.put("\u1e37", "l");
            this.translitChars.put("\u0155", "r");
            this.translitChars.put("\u1d15", "ou");
            this.translitChars.put("\u0288", "t");
            this.translitChars.put("\u0101", "a");
            this.translitChars.put("\u044d", "e");
            this.translitChars.put("\u1e19", "e");
            this.translitChars.put("\u1d11", "o");
            this.translitChars.put("\u00e7", "c");
            this.translitChars.put("\u1d8a", "s");
            this.translitChars.put("\u1eb7", "a");
            this.translitChars.put("\u0173", "u");
            this.translitChars.put("\u1ea3", "a");
            this.translitChars.put("\u01e5", "g");
            this.translitChars.put("\u0440", "r");
            this.translitChars.put("\ua741", "k");
            this.translitChars.put("\u1e95", "z");
            this.translitChars.put("\u015d", "s");
            this.translitChars.put("\u1e15", "e");
            this.translitChars.put("\u0260", "g");
            this.translitChars.put("\ua749", "l");
            this.translitChars.put("\ua77c", "f");
            this.translitChars.put("\u1d8d", "x");
            this.translitChars.put("\u0445", "h");
            this.translitChars.put("\u01d2", "o");
            this.translitChars.put("\u0119", "e");
            this.translitChars.put("\u1ed5", "o");
            this.translitChars.put("\u01ab", "t");
            this.translitChars.put("\u01eb", "o");
            this.translitChars.put("i\u0307", "i");
            this.translitChars.put("\u1e47", "n");
            this.translitChars.put("\u0107", "c");
            this.translitChars.put("\u1d77", "g");
            this.translitChars.put("\u1e85", "w");
            this.translitChars.put("\u1e11", "d");
            this.translitChars.put("\u1e39", "l");
            this.translitChars.put("\u0447", "ch");
            this.translitChars.put("\u0153", "oe");
            this.translitChars.put("\u1d73", "r");
            this.translitChars.put("\u013c", "l");
            this.translitChars.put("\u0211", "r");
            this.translitChars.put("\u022d", "o");
            this.translitChars.put("\u1d70", "n");
            this.translitChars.put("\u1d01", "ae");
            this.translitChars.put("\u0140", "l");
            this.translitChars.put("\u00e4", "a");
            this.translitChars.put("\u01a5", TtmlNode.TAG_P);
            this.translitChars.put("\u1ecf", "o");
            this.translitChars.put("\u012f", "i");
            this.translitChars.put("\u0213", "r");
            this.translitChars.put("\u01c6", "dz");
            this.translitChars.put("\u1e21", "g");
            this.translitChars.put("\u1e7b", "u");
            this.translitChars.put("\u014d", "o");
            this.translitChars.put("\u013e", "l");
            this.translitChars.put("\u1e83", "w");
            this.translitChars.put("\u021b", "t");
            this.translitChars.put("\u0144", "n");
            this.translitChars.put("\u024d", "r");
            this.translitChars.put("\u0203", "a");
            this.translitChars.put("\u00fc", "u");
            this.translitChars.put("\ua781", "l");
            this.translitChars.put("\u1d10", "o");
            this.translitChars.put("\u1edb", "o");
            this.translitChars.put("\u1d03", "b");
            this.translitChars.put("\u0279", "r");
            this.translitChars.put("\u1d72", "r");
            this.translitChars.put("\u028f", "y");
            this.translitChars.put("\u1d6e", "f");
            this.translitChars.put("\u2c68", "h");
            this.translitChars.put("\u014f", "o");
            this.translitChars.put("\u00fa", "u");
            this.translitChars.put("\u1e5b", "r");
            this.translitChars.put("\u02ae", "h");
            this.translitChars.put("\u00f3", "o");
            this.translitChars.put("\u016f", "u");
            this.translitChars.put("\u1ee1", "o");
            this.translitChars.put("\u1e55", TtmlNode.TAG_P);
            this.translitChars.put("\u1d96", "i");
            this.translitChars.put("\u1ef1", "u");
            this.translitChars.put("\u00e3", "a");
            this.translitChars.put("\u1d62", "i");
            this.translitChars.put("\u1e71", "t");
            this.translitChars.put("\u1ec3", "e");
            this.translitChars.put("\u1eed", "u");
            this.translitChars.put("\u00ed", "i");
            this.translitChars.put("\u0254", "o");
            this.translitChars.put("\u0441", "s");
            this.translitChars.put("\u0439", "i");
            this.translitChars.put("\u027a", "r");
            this.translitChars.put("\u0262", "g");
            this.translitChars.put("\u0159", "r");
            this.translitChars.put("\u1e96", "h");
            this.translitChars.put("\u0171", "u");
            this.translitChars.put("\u020d", "o");
            this.translitChars.put("\u0448", "sh");
            this.translitChars.put("\u1e3b", "l");
            this.translitChars.put("\u1e23", "h");
            this.translitChars.put("\u0236", "t");
            this.translitChars.put("\u0146", "n");
            this.translitChars.put("\u1d92", "e");
            this.translitChars.put("\u00ec", "i");
            this.translitChars.put("\u1e89", "w");
            this.translitChars.put("\u0431", "b");
            this.translitChars.put("\u0113", "e");
            this.translitChars.put("\u1d07", "e");
            this.translitChars.put("\u0142", "l");
            this.translitChars.put("\u1ed9", "o");
            this.translitChars.put("\u026d", "l");
            this.translitChars.put("\u1e8f", "y");
            this.translitChars.put("\u1d0a", "j");
            this.translitChars.put("\u1e31", "k");
            this.translitChars.put("\u1e7f", "v");
            this.translitChars.put("\u0229", "e");
            this.translitChars.put("\u00e2", "a");
            this.translitChars.put("\u015f", "s");
            this.translitChars.put("\u0157", "r");
            this.translitChars.put("\u028b", "v");
            this.translitChars.put("\u2090", "a");
            this.translitChars.put("\u2184", "c");
            this.translitChars.put("\u1d93", "e");
            this.translitChars.put("\u0270", "m");
            this.translitChars.put("\u0435", "e");
            this.translitChars.put("\u1d21", "w");
            this.translitChars.put("\u020f", "o");
            this.translitChars.put("\u010d", "c");
            this.translitChars.put("\u01f5", "g");
            this.translitChars.put("\u0109", "c");
            this.translitChars.put("\u044e", "yu");
            this.translitChars.put("\u1d97", "o");
            this.translitChars.put("\ua743", "k");
            this.translitChars.put("\ua759", "q");
            this.translitChars.put("\u0433", "g");
            this.translitChars.put("\u1e51", "o");
            this.translitChars.put("\ua731", "s");
            this.translitChars.put("\u1e53", "o");
            this.translitChars.put("\u021f", "h");
            this.translitChars.put("\u0151", "o");
            this.translitChars.put("\ua729", "tz");
            this.translitChars.put("\u1ebb", "e");
            this.translitChars.put("\u043e", "o");
        }
        StringBuilder dst = new StringBuilder(src.length());
        int len = src.length();
        for (int a = 0; a < len; a++) {
            String ch = src.substring(a, a + 1);
            String tch = (String) this.translitChars.get(ch);
            if (tch != null) {
                dst.append(tch);
            } else {
                dst.append(ch);
            }
        }
        return dst.toString();
    }

    public static String addNbsp(String src) {
        return src.replace(' ', '\u00a0');
    }
}
