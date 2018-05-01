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
import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackDifference;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
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
    private boolean changingConfiguration;
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
    public ArrayList<LocaleInfo> languages;
    public HashMap<String, LocaleInfo> languagesDict;
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap();
    private ArrayList<LocaleInfo> otherLanguages;
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages;
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.name);
            stringBuilder.append("|");
            stringBuilder.append(this.nameEnglish);
            stringBuilder.append("|");
            stringBuilder.append(this.shortName);
            stringBuilder.append("|");
            stringBuilder.append(this.pathToFile);
            stringBuilder.append("|");
            stringBuilder.append(this.version);
            return stringBuilder.toString();
        }

        public static LocaleInfo createWithString(String str) {
            LocaleInfo localeInfo = null;
            if (str != null) {
                if (str.length() != 0) {
                    str = str.split("\\|");
                    if (str.length >= 4) {
                        localeInfo = new LocaleInfo();
                        localeInfo.name = str[0];
                        localeInfo.nameEnglish = str[1];
                        localeInfo.shortName = str[2].toLowerCase();
                        localeInfo.pathToFile = str[3];
                        if (str.length >= 5) {
                            localeInfo.version = Utilities.parseInt(str[4]).intValue();
                        }
                    }
                    return localeInfo;
                }
            }
            return null;
        }

        public File getPathToFile() {
            if (isRemote()) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("remote_");
                stringBuilder.append(this.shortName);
                stringBuilder.append(".xml");
                return new File(filesDirFixed, stringBuilder.toString());
            }
            return !TextUtils.isEmpty(this.pathToFile) ? new File(this.pathToFile) : null;
        }

        public String getKey() {
            if (this.pathToFile == null || "remote".equals(this.pathToFile)) {
                return this.shortName;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("local_");
            stringBuilder.append(this.shortName);
            return stringBuilder.toString();
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
            if (i >= 2 && i <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i != 0 && (i < 5 || i > 9)) {
                if (i2 < 11 || i2 > 14) {
                    return 0;
                }
            }
            return 16;
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
            if (i != 0) {
                if (i2 < 2 || i2 > 10) {
                    return (i2 < 11 || i2 > 19) ? 0 : 16;
                }
            }
            return 8;
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
            return (i3 < 2 || i3 > 4 || ((i2 >= 12 && i2 <= 14) || (i2 >= 22 && i2 <= 24))) ? 0 : 8;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i != 0) {
                if (i2 < 1 || i2 > 19) {
                    return 0;
                }
            }
            return 8;
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
            if (i != 0) {
                if (i != 1) {
                    return 0;
                }
            }
            return 2;
        }
    }

    private String stringForQuantity(int i) {
        if (i == 4) {
            return "two";
        }
        if (i == 8) {
            return "few";
        }
        if (i == 16) {
            return "many";
        }
        switch (i) {
            case 1:
                return "zero";
            case 2:
                return "one";
            default:
                return "other";
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
        int i;
        boolean z = false;
        this.changingConfiguration = false;
        this.languages = new ArrayList();
        this.remoteLanguages = new ArrayList();
        this.languagesDict = new HashMap();
        this.otherLanguages = new ArrayList();
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
        for (i = 0; i < r1.otherLanguages.size(); i++) {
            LocaleInfo localeInfo2 = (LocaleInfo) r1.otherLanguages.get(i);
            r1.languages.add(localeInfo2);
            r1.languagesDict.put(localeInfo2.getKey(), localeInfo2);
        }
        for (i = 0; i < r1.remoteLanguages.size(); i++) {
            localeInfo2 = (LocaleInfo) r1.remoteLanguages.get(i);
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo2.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo2.pathToFile;
                languageFromDict.version = localeInfo2.version;
                r1.remoteLanguages.set(i, languageFromDict);
            } else {
                r1.languages.add(localeInfo2);
                r1.languagesDict.put(localeInfo2.getKey(), localeInfo2);
            }
        }
        r1.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = MessagesController.getGlobalMainSettings().getString("language", null);
            if (string != null) {
                localeInfo = getLanguageFromDict(string);
                if (localeInfo != null) {
                    z = true;
                }
            } else {
                localeInfo = null;
            }
            if (localeInfo == null && r1.systemDefaultLocale.getLanguage() != null) {
                localeInfo = getLanguageFromDict(r1.systemDefaultLocale.getLanguage());
            }
            if (localeInfo == null) {
                localeInfo = getLanguageFromDict(getLocaleString(r1.systemDefaultLocale));
                if (localeInfo == null) {
                    localeInfo = getLanguageFromDict("en");
                }
            }
            applyLanguage(localeInfo, z, true, UserConfig.selectedAccount);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    private LocaleInfo getLanguageFromDict(String str) {
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

    public void reloadCurrentRemoteLocale(int i) {
        applyRemoteLanguage(this.currentLocaleInfo, true, i);
    }

    public void checkUpdateForCurrentRemoteLocale(int i, int i2) {
        if (this.currentLocaleInfo != null) {
            if (this.currentLocaleInfo == null || this.currentLocaleInfo.isRemote()) {
                if (this.currentLocaleInfo.version < i2) {
                    applyRemoteLanguage(this.currentLocaleInfo, false, i);
                }
            }
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        locale = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || locale.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(country);
        if (locale.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(locale);
        return stringBuilder.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale == null) {
            return "en";
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
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
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
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

    public static String getLocaleAlias(String str) {
        if (str == null) {
            return null;
        }
        Object obj = -1;
        switch (str.hashCode()) {
            case 3325:
                if (str.equals("he") != null) {
                    obj = 7;
                    break;
                }
                break;
            case 3355:
                if (str.equals(TtmlNode.ATTR_ID) != null) {
                    obj = 6;
                    break;
                }
                break;
            case 3365:
                if (str.equals("in") != null) {
                    obj = null;
                    break;
                }
                break;
            case 3374:
                if (str.equals("iw") != null) {
                    obj = 1;
                    break;
                }
                break;
            case 3391:
                if (str.equals("ji") != null) {
                    obj = 5;
                    break;
                }
                break;
            case 3404:
                if (str.equals("jv") != null) {
                    obj = 8;
                    break;
                }
                break;
            case 3405:
                if (str.equals("jw") != null) {
                    obj = 2;
                    break;
                }
                break;
            case 3508:
                if (str.equals("nb") != null) {
                    obj = 9;
                    break;
                }
                break;
            case 3521:
                if (str.equals("no") != null) {
                    obj = 3;
                    break;
                }
                break;
            case 3704:
                if (str.equals("tl") != null) {
                    obj = 4;
                    break;
                }
                break;
            case 3856:
                if (str.equals("yi") != null) {
                    obj = 11;
                    break;
                }
                break;
            case 101385:
                if (str.equals("fil") != null) {
                    obj = 10;
                    break;
                }
                break;
            default:
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

    public boolean applyLanguageFile(File file, int i) {
        try {
            HashMap localeFileStrings = getLocaleFileStrings(file);
            String str = (String) localeFileStrings.get("LanguageName");
            String str2 = (String) localeFileStrings.get("LanguageNameInEnglish");
            String str3 = (String) localeFileStrings.get("LanguageCode");
            if (!(str == null || str.length() <= 0 || str2 == null || str2.length() <= 0 || str3 == null || str3.length() <= 0 || str.contains("&"))) {
                if (!str.contains("|")) {
                    if (!str2.contains("&")) {
                        if (!str2.contains("|")) {
                            if (!(str3.contains("&") || str3.contains("|") || str3.contains("/"))) {
                                if (!str3.contains("\\")) {
                                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str3);
                                    stringBuilder.append(".xml");
                                    File file2 = new File(filesDirFixed, stringBuilder.toString());
                                    if (AndroidUtilities.copyFile(file, file2) == null) {
                                        return false;
                                    }
                                    file = new StringBuilder();
                                    file.append("local_");
                                    file.append(str3.toLowerCase());
                                    file = getLanguageFromDict(file.toString());
                                    if (file == null) {
                                        file = new LocaleInfo();
                                        file.name = str;
                                        file.nameEnglish = str2;
                                        file.shortName = str3.toLowerCase();
                                        file.pathToFile = file2.getAbsolutePath();
                                        this.languages.add(file);
                                        this.languagesDict.put(file.getKey(), file);
                                        this.otherLanguages.add(file);
                                        saveOtherLanguages();
                                    }
                                    File file3 = file;
                                    this.localeValues = localeFileStrings;
                                    applyLanguage(file3, true, false, true, false, i);
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                    return false;
                }
            }
            return false;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return false;
    }

    private void saveOtherLanguages() {
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < this.otherLanguages.size(); i2++) {
            String saveString = ((LocaleInfo) this.otherLanguages.get(i2)).getSaveString();
            if (saveString != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(saveString);
            }
        }
        edit.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        while (i < this.remoteLanguages.size()) {
            String saveString2 = ((LocaleInfo) this.remoteLanguages.get(i)).getSaveString();
            if (saveString2 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(saveString2);
            }
            i++;
        }
        edit.putString("remote", stringBuilder.toString());
        edit.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int i) {
        if (localeInfo.pathToFile != null) {
            if (!localeInfo.isRemote()) {
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
                this.otherLanguages.remove(localeInfo);
                this.languages.remove(localeInfo);
                this.languagesDict.remove(localeInfo.shortName);
                new File(localeInfo.pathToFile).delete();
                saveOtherLanguages();
                return true;
            }
        }
        return false;
    }

    private void loadOtherLanguages() {
        int i = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        Object string = sharedPreferences.getString("locales", null);
        if (!TextUtils.isEmpty(string)) {
            for (String createWithString : string.split("&")) {
                LocaleInfo createWithString2 = LocaleInfo.createWithString(createWithString);
                if (createWithString2 != null) {
                    this.otherLanguages.add(createWithString2);
                }
            }
        }
        Object string2 = sharedPreferences.getString("remote", null);
        if (!TextUtils.isEmpty(string2)) {
            String[] split = string2.split("&");
            int length = split.length;
            while (i < length) {
                LocaleInfo createWithString3 = LocaleInfo.createWithString(split[i]);
                createWithString3.shortName = createWithString3.shortName.replace("-", "_");
                if (createWithString3 != null) {
                    this.remoteLanguages.add(createWithString3);
                }
                i++;
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean z) {
        Throwable e;
        this.reloadLastFile = false;
        FileInputStream fileInputStream = null;
        try {
            if (!file.exists()) {
                return new HashMap();
            }
            HashMap<String, String> hashMap = new HashMap();
            XmlPullParser newPullParser = Xml.newPullParser();
            FileInputStream fileInputStream2 = new FileInputStream(file);
            try {
                newPullParser.setInput(fileInputStream2, C0542C.UTF8_NAME);
                String str = null;
                String str2 = str;
                String str3 = str2;
                for (file = newPullParser.getEventType(); file != 1; file = newPullParser.next()) {
                    if (file == 2) {
                        file = newPullParser.getName();
                        if (newPullParser.getAttributeCount() > 0) {
                            str = newPullParser.getAttributeValue(0);
                        }
                        str2 = file;
                    } else if (file == 4) {
                        if (str != null) {
                            file = newPullParser.getText();
                            if (file != null) {
                                file = file.trim();
                                if (z) {
                                    file = file.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                                } else {
                                    file = file.replace("\\n", "\n").replace("\\", TtmlNode.ANONYMOUS_REGION_ID);
                                    str3 = file.replace("&lt;", "<");
                                    if (!this.reloadLastFile && str3.equals(file) == null) {
                                        this.reloadLastFile = true;
                                    }
                                }
                            }
                            str3 = file;
                        }
                    } else if (file == 3) {
                        str = null;
                        str2 = str;
                        str3 = str2;
                    }
                    if (!(str2 == null || str2.equals("string") == null || str3 == null || str == null || str3.length() == null || str.length() == null)) {
                        hashMap.put(str, str3);
                        str = null;
                        str2 = str;
                        str3 = str2;
                    }
                }
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
                return hashMap;
            } catch (Exception e3) {
                e2 = e3;
                fileInputStream = fileInputStream2;
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
                } catch (Throwable th) {
                    file = th;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Throwable e4) {
                            FileLog.m3e(e4);
                        }
                    }
                    throw file;
                }
            } catch (Throwable th2) {
                file = th2;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw file;
            }
        } catch (Exception e5) {
            e22 = e5;
            FileLog.m3e(e22);
            this.reloadLastFile = true;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return new HashMap();
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        applyLanguage(localeInfo, z, z2, false, false, i);
    }

    public void applyLanguage(final LocaleInfo localeInfo, boolean z, boolean z2, boolean z3, boolean z4, final int i) {
        if (localeInfo != null) {
            File pathToFile = localeInfo.getPathToFile();
            String str = localeInfo.shortName;
            if (!z2) {
                ConnectionsManager.setLangCode(str.replace("_", "-"));
            }
            if (localeInfo.isRemote() && (z4 || !pathToFile.exists())) {
                if (BuildVars.LOGS_ENABLED) {
                    z4 = new StringBuilder();
                    z4.append("reload locale because file doesn't exist ");
                    z4.append(pathToFile);
                    FileLog.m0d(z4.toString());
                }
                if (z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocaleController.this.applyRemoteLanguage(localeInfo, true, i);
                        }
                    });
                } else {
                    applyRemoteLanguage(localeInfo, true, i);
                }
            }
            try {
                Locale locale;
                String[] split = localeInfo.shortName.split("_");
                if (split.length == 1) {
                    locale = new Locale(localeInfo.shortName);
                } else {
                    locale = new Locale(split[0], split[1]);
                }
                if (z) {
                    this.languageOverride = localeInfo.shortName;
                    z = MessagesController.getGlobalMainSettings().edit();
                    z.putString("language", localeInfo.getKey());
                    z.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!z3) {
                    this.localeValues = getLocaleFileStrings(pathToFile);
                }
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                this.currentPluralRules = (PluralRules) this.allRules.get(split[0]);
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                }
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = new PluralRules_None();
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                localeInfo = new Configuration();
                localeInfo.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(localeInfo, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                if (this.reloadLastFile != null) {
                    if (z2) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LocaleController.this.reloadCurrentRemoteLocale(i);
                            }
                        });
                    } else {
                        reloadCurrentRemoteLocale(i);
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
        return getString("LanguageName", C0446R.string.LanguageName);
    }

    private String getStringInternal(String str, int i) {
        String str2 = (String) this.localeValues.get(str);
        if (str2 == null) {
            try {
                i = ApplicationLoader.applicationContext.getString(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (i == 0) {
                return i;
            }
            i = new StringBuilder();
            i.append("LOC_ERR:");
            i.append(str);
            return i.toString();
        }
        i = str2;
        if (i == 0) {
            return i;
        }
        i = new StringBuilder();
        i.append("LOC_ERR:");
        i.append(str);
        return i.toString();
    }

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    public static String getPluralString(String str, int i) {
        if (!(str == null || str.length() == 0)) {
            if (getInstance().currentPluralRules != null) {
                i = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("_");
                stringBuilder.append(i);
                str = stringBuilder.toString();
                return getString(str, ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName()));
            }
        }
        i = new StringBuilder();
        i.append("LOC_ERR:");
        i.append(str);
        return i.toString();
    }

    public static String formatPluralString(String str, int i) {
        if (!(str == null || str.length() == 0)) {
            if (getInstance().currentPluralRules != null) {
                String stringForQuantity = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("_");
                stringBuilder.append(stringForQuantity);
                str = stringBuilder.toString();
                return formatString(str, ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(i));
            }
        }
        i = new StringBuilder();
        i.append("LOC_ERR:");
        i.append(str);
        return i.toString();
    }

    public static String formatString(String str, int i, Object... objArr) {
        try {
            String str2 = (String) getInstance().localeValues.get(str);
            if (str2 == null) {
                str2 = ApplicationLoader.applicationContext.getString(i);
            }
            if (getInstance().currentLocale != 0) {
                return String.format(getInstance().currentLocale, str2, objArr);
            }
            return String.format(str2, objArr);
        } catch (Throwable e) {
            FileLog.m3e(e);
            i = new StringBuilder();
            i.append("LOC_ERR: ");
            i.append(str);
            return i.toString();
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
        String str2;
        str = str.toUpperCase();
        int i = j < 0 ? 1 : 0;
        j = Math.abs(j);
        Currency instance = Currency.getInstance(str);
        int i2 = -1;
        switch (str.hashCode()) {
            case 65726:
                if (str.equals("BHD")) {
                    i2 = 2;
                    break;
                }
                break;
            case 65759:
                if (str.equals("BIF")) {
                    i2 = 9;
                    break;
                }
                break;
            case 66267:
                if (str.equals("BYR")) {
                    i2 = 10;
                    break;
                }
                break;
            case 66813:
                if (str.equals("CLF")) {
                    i2 = 0;
                    break;
                }
                break;
            case 66823:
                if (str.equals("CLP")) {
                    i2 = 11;
                    break;
                }
                break;
            case 67122:
                if (str.equals("CVE")) {
                    i2 = 12;
                    break;
                }
                break;
            case 67712:
                if (str.equals("DJF")) {
                    i2 = 13;
                    break;
                }
                break;
            case 70719:
                if (str.equals("GNF")) {
                    i2 = 14;
                    break;
                }
                break;
            case 72732:
                if (str.equals("IQD")) {
                    i2 = 3;
                    break;
                }
                break;
            case 72777:
                if (str.equals("IRR")) {
                    i2 = 1;
                    break;
                }
                break;
            case 72801:
                if (str.equals("ISK")) {
                    i2 = 15;
                    break;
                }
                break;
            case 73631:
                if (str.equals("JOD")) {
                    i2 = 4;
                    break;
                }
                break;
            case 73683:
                if (str.equals("JPY")) {
                    i2 = 16;
                    break;
                }
                break;
            case 74532:
                if (str.equals("KMF")) {
                    i2 = 17;
                    break;
                }
                break;
            case 74704:
                if (str.equals("KRW")) {
                    i2 = 18;
                    break;
                }
                break;
            case 74840:
                if (str.equals("KWD")) {
                    i2 = 5;
                    break;
                }
                break;
            case 75863:
                if (str.equals("LYD")) {
                    i2 = 6;
                    break;
                }
                break;
            case 76263:
                if (str.equals("MGA")) {
                    i2 = 19;
                    break;
                }
                break;
            case 76618:
                if (str.equals("MRO")) {
                    i2 = 29;
                    break;
                }
                break;
            case 78388:
                if (str.equals("OMR")) {
                    i2 = 7;
                    break;
                }
                break;
            case 79710:
                if (str.equals("PYG")) {
                    i2 = 20;
                    break;
                }
                break;
            case 81569:
                if (str.equals("RWF")) {
                    i2 = 21;
                    break;
                }
                break;
            case 83210:
                if (str.equals("TND")) {
                    i2 = 8;
                    break;
                }
                break;
            case 83974:
                if (str.equals("UGX")) {
                    i2 = 22;
                    break;
                }
                break;
            case 84517:
                if (str.equals("UYI")) {
                    i2 = 23;
                    break;
                }
                break;
            case 85132:
                if (str.equals("VND")) {
                    i2 = 24;
                    break;
                }
                break;
            case 85367:
                if (str.equals("VUV")) {
                    i2 = 25;
                    break;
                }
                break;
            case 86653:
                if (str.equals("XAF")) {
                    i2 = 26;
                    break;
                }
                break;
            case 87087:
                if (str.equals("XOF")) {
                    i2 = 27;
                    break;
                }
                break;
            case 87118:
                if (str.equals("XPF")) {
                    i2 = 28;
                    break;
                }
                break;
            default:
                break;
        }
        switch (i2) {
            case 0:
                str2 = " %.4f";
                j = ((double) j) / 4666723172467343360L;
                break;
            case 1:
                long j2 = (double) (((float) j) / 100.0f);
                str2 = j % 100 == 0 ? " %.0f" : " %.2f";
                j = j2;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                str2 = " %.3f";
                j = ((double) j) / 4652007308841189376L;
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
                str2 = " %.0f";
                j = (double) j;
                break;
            case 29:
                str2 = " %.1f";
                j = ((double) j) / 4621819117588971520L;
                break;
            default:
                str2 = " %.2f";
                j = ((double) j) / 4636737291354636288L;
                break;
        }
        if (instance != null) {
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(this.currentLocale != null ? this.currentLocale : this.systemDefaultLocale);
            currencyInstance.setCurrency(instance);
            if (str.equals("IRR") != null) {
                currencyInstance.setMaximumFractionDigits(0);
            }
            str = new StringBuilder();
            str.append(i != 0 ? "-" : TtmlNode.ANONYMOUS_REGION_ID);
            str.append(currencyInstance.format(j));
            return str.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i != 0 ? "-" : TtmlNode.ANONYMOUS_REGION_ID);
        Locale locale = Locale.US;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(str2);
        stringBuilder.append(String.format(locale, stringBuilder2.toString(), new Object[]{Double.valueOf(j)}));
        return stringBuilder.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public String formatCurrencyDecimalString(long j, String str, boolean z) {
        int i;
        String str2;
        str = str.toUpperCase();
        j = Math.abs(j);
        switch (str.hashCode()) {
            case 65726:
                if (str.equals("BHD")) {
                    i = 2;
                    break;
                }
            case 65759:
                if (str.equals("BIF")) {
                    i = 9;
                    break;
                }
            case 66267:
                if (str.equals("BYR")) {
                    i = 10;
                    break;
                }
            case 66813:
                if (str.equals("CLF")) {
                    i = 0;
                    break;
                }
            case 66823:
                if (str.equals("CLP")) {
                    i = 11;
                    break;
                }
            case 67122:
                if (str.equals("CVE")) {
                    i = 12;
                    break;
                }
            case 67712:
                if (str.equals("DJF")) {
                    i = 13;
                    break;
                }
            case 70719:
                if (str.equals("GNF")) {
                    i = 14;
                    break;
                }
            case 72732:
                if (str.equals("IQD")) {
                    i = 3;
                    break;
                }
            case 72777:
                if (str.equals("IRR")) {
                    i = 1;
                    break;
                }
            case 72801:
                if (str.equals("ISK")) {
                    i = 15;
                    break;
                }
            case 73631:
                if (str.equals("JOD")) {
                    i = 4;
                    break;
                }
            case 73683:
                if (str.equals("JPY")) {
                    i = 16;
                    break;
                }
            case 74532:
                if (str.equals("KMF")) {
                    i = 17;
                    break;
                }
            case 74704:
                if (str.equals("KRW")) {
                    i = 18;
                    break;
                }
            case 74840:
                if (str.equals("KWD")) {
                    i = 5;
                    break;
                }
            case 75863:
                if (str.equals("LYD")) {
                    i = 6;
                    break;
                }
            case 76263:
                if (str.equals("MGA")) {
                    i = 19;
                    break;
                }
            case 76618:
                if (str.equals("MRO")) {
                    i = 29;
                    break;
                }
            case 78388:
                if (str.equals("OMR")) {
                    i = 7;
                    break;
                }
            case 79710:
                if (str.equals("PYG")) {
                    i = 20;
                    break;
                }
            case 81569:
                if (str.equals("RWF")) {
                    i = 21;
                    break;
                }
            case 83210:
                if (str.equals("TND")) {
                    i = 8;
                    break;
                }
            case 83974:
                if (str.equals("UGX")) {
                    i = 22;
                    break;
                }
            case 84517:
                if (str.equals("UYI")) {
                    i = 23;
                    break;
                }
            case 85132:
                if (str.equals("VND")) {
                    i = 24;
                    break;
                }
            case 85367:
                if (str.equals("VUV")) {
                    i = 25;
                    break;
                }
            case 86653:
                if (str.equals("XAF")) {
                    i = 26;
                    break;
                }
            case 87087:
                if (str.equals("XOF")) {
                    i = 27;
                    break;
                }
            case 87118:
                if (str.equals("XPF")) {
                    i = 28;
                    break;
                }
            default:
        }
        i = -1;
        switch (i) {
            case 0:
                str2 = " %.4f";
                j = ((double) j) / 4666723172467343360L;
                break;
            case 1:
                long j2 = (double) (((float) j) / 100.0f);
                str2 = j % 100 == 0 ? " %.0f" : " %.2f";
                j = j2;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                str2 = " %.3f";
                j = ((double) j) / 4652007308841189376L;
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
                str2 = " %.0f";
                j = (double) j;
                break;
            case 29:
                str2 = " %.1f";
                j = ((double) j) / 4621819117588971520L;
                break;
            default:
                str2 = " %.2f";
                j = ((double) j) / 4636737291354636288L;
                break;
        }
        Locale locale = Locale.US;
        if (!z) {
            str = new StringBuilder();
            str.append(TtmlNode.ANONYMOUS_REGION_ID);
            str.append(str2);
            str = str.toString();
        }
        return String.format(locale, str, new Object[]{Double.valueOf(j)}).trim();
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str, objArr);
            }
            return String.format(str, objArr);
        } catch (Throwable e) {
            FileLog.m3e(e);
            objArr = new StringBuilder();
            objArr.append("LOC_ERR: ");
            objArr.append(str);
            return objArr.toString();
        }
    }

    public static String formatCallDuration(int i) {
        if (i > 3600) {
            String formatPluralString = formatPluralString("Hours", i / 3600);
            i = (i % 3600) / 60;
            if (i > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(formatPluralString);
                stringBuilder.append(", ");
                stringBuilder.append(formatPluralString("Minutes", i));
                formatPluralString = stringBuilder.toString();
            }
            return formatPluralString;
        } else if (i > 60) {
            return formatPluralString("Minutes", i / 60);
        } else {
            return formatPluralString("Seconds", i);
        }
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            this.systemDefaultLocale = configuration.locale;
            if (this.languageOverride != null) {
                configuration = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(configuration, false, false, UserConfig.selectedAccount);
            } else {
                configuration = configuration.locale;
                if (configuration != null) {
                    String displayName = configuration.getDisplayName();
                    String displayName2 = this.currentLocale.getDisplayName();
                    if (!(displayName == null || displayName2 == null || displayName.equals(displayName2))) {
                        recreateFormatters();
                    }
                    this.currentLocale = configuration;
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get("en");
                    }
                }
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
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                return getString("Yesterday", C0446R.string.Yesterday);
            }
            if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return getInstance().formatterMonth.format(new Date(j));
            }
            return getInstance().formatterYear.format(new Date(j));
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                return formatString("TodayAtFormatted", C0446R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j)));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("YesterdayAtFormatted", C0446R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j)));
            } else if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            } else {
                return formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                return formatString("YesterdayAtFormatted", C0446R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j)));
            } else if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                return formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().chatDate.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            } else {
                return formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            Object[] objArr;
            if (i3 == i && i2 == i4) {
                int currentTime = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (j / 1000))) / 60;
                if (currentTime < 1) {
                    return getString("LocationUpdatedJustNow", C0446R.string.LocationUpdatedJustNow);
                }
                if (currentTime < 60) {
                    return formatPluralString("UpdatedMinutes", currentTime);
                }
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", C0446R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j)));
                return formatString("LocationUpdatedFormatted", C0446R.string.LocationUpdatedFormatted, objArr);
            } else if (i3 + 1 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", C0446R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j)));
                return formatString("LocationUpdatedFormatted", C0446R.string.LocationUpdatedFormatted, objArr);
            } else if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                j = formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
                return formatString("LocationUpdatedFormatted", C0446R.string.LocationUpdatedFormatted, j);
            } else {
                j = formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
                return formatString("LocationUpdatedFormatted", C0446R.string.LocationUpdatedFormatted, j);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int i) {
        int i2 = (i / 60) / 60;
        i -= (i2 * 60) * 60;
        int i3 = i / 60;
        i -= i3 * 60;
        int i4 = 1;
        Object[] objArr;
        if (i2 != 0) {
            i = "%dh";
            objArr = new Object[1];
            if (i3 <= 30) {
                i4 = 0;
            }
            objArr[0] = Integer.valueOf(i2 + i4);
            return String.format(i, objArr);
        } else if (i3 != 0) {
            String str = "%d";
            objArr = new Object[1];
            if (i <= 30) {
                i4 = 0;
            }
            objArr[0] = Integer.valueOf(i3 + i4);
            return String.format(str, objArr);
        } else {
            return String.format("%d", new Object[]{Integer.valueOf(i)});
        }
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
            Object[] objArr;
            if (i3 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("TodayAtFormatted", C0446R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j)));
                return formatString("LastSeenFormatted", C0446R.string.LastSeenFormatted, objArr);
            } else if (i3 + 1 == i && i2 == i4) {
                objArr = new Object[1];
                objArr[0] = formatString("YesterdayAtFormatted", C0446R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j)));
                return formatString("LastSeenFormatted", C0446R.string.LastSeenFormatted, objArr);
            } else if (Math.abs(System.currentTimeMillis() - j) < 31536000000L) {
                j = formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterMonth.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
                return formatString("LastSeenDateFormatted", C0446R.string.LastSeenDateFormatted, j);
            } else {
                j = formatString("formatDateAtTime", C0446R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j)), getInstance().formatterDay.format(new Date(j)));
                return formatString("LastSeenDateFormatted", C0446R.string.LastSeenDateFormatted, j);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    private org.telegram.messenger.time.FastDateFormat createFormatter(java.util.Locale r2, java.lang.String r3, java.lang.String r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        if (r3 == 0) goto L_0x0008;
    L_0x0002:
        r0 = r3.length();
        if (r0 != 0) goto L_0x0009;
    L_0x0008:
        r3 = r4;
    L_0x0009:
        r3 = org.telegram.messenger.time.FastDateFormat.getInstance(r3, r2);	 Catch:{ Exception -> 0x000e }
        goto L_0x0012;
    L_0x000e:
        r3 = org.telegram.messenger.time.FastDateFormat.getInstance(r4, r2);
    L_0x0012:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.createFormatter(java.util.Locale, java.lang.String, java.lang.String):org.telegram.messenger.time.FastDateFormat");
    }

    public void recreateFormatters() {
        boolean z;
        Locale locale;
        String str;
        int i;
        Locale locale2 = this.currentLocale;
        if (locale2 == null) {
            locale2 = Locale.getDefault();
        }
        String language = locale2.getLanguage();
        if (language == null) {
            language = "en";
        }
        language = language.toLowerCase();
        int i2 = 1;
        if (!(language.startsWith("ar") || language.startsWith("fa"))) {
            if (BuildVars.DEBUG_VERSION) {
                if (!language.startsWith("he")) {
                    if (language.startsWith("iw")) {
                    }
                }
            }
            z = false;
            isRTL = z;
            if (language.equals("ko")) {
                i2 = 2;
            }
            nameDisplayOrder = i2;
            this.formatterMonth = createFormatter(locale2, getStringInternal("formatterMonth", C0446R.string.formatterMonth), "dd MMM");
            this.formatterYear = createFormatter(locale2, getStringInternal("formatterYear", C0446R.string.formatterYear), "dd.MM.yy");
            this.formatterYearMax = createFormatter(locale2, getStringInternal("formatterYearMax", C0446R.string.formatterYearMax), "dd.MM.yyyy");
            this.chatDate = createFormatter(locale2, getStringInternal("chatDate", C0446R.string.chatDate), "d MMMM");
            this.chatFullDate = createFormatter(locale2, getStringInternal("chatFullDate", C0446R.string.chatFullDate), "d MMMM yyyy");
            this.formatterWeek = createFormatter(locale2, getStringInternal("formatterWeek", C0446R.string.formatterWeek), "EEE");
            this.formatterMonthYear = createFormatter(locale2, getStringInternal("formatterMonthYear", C0446R.string.formatterMonthYear), "MMMM yyyy");
            if (!language.toLowerCase().equals("ar")) {
                if (language.toLowerCase().equals("ko")) {
                    locale = Locale.US;
                    if (is24HourFormat) {
                        str = "formatterDay12H";
                        i2 = C0446R.string.formatterDay12H;
                    } else {
                        str = "formatterDay24H";
                        i2 = C0446R.string.formatterDay24H;
                    }
                    this.formatterDay = createFormatter(locale, getStringInternal(str, i2), is24HourFormat ? "HH:mm" : "h:mm a");
                    if (is24HourFormat) {
                        language = "formatterStats12H";
                        i = C0446R.string.formatterStats12H;
                    } else {
                        language = "formatterStats24H";
                        i = C0446R.string.formatterStats24H;
                    }
                    this.formatterStats = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    if (is24HourFormat) {
                        language = "formatterBannedUntil12H";
                        i = C0446R.string.formatterBannedUntil12H;
                    } else {
                        language = "formatterBannedUntil24H";
                        i = C0446R.string.formatterBannedUntil24H;
                    }
                    this.formatterBannedUntil = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    if (is24HourFormat) {
                        language = "formatterBannedUntilThisYear12H";
                        i = C0446R.string.formatterBannedUntilThisYear12H;
                    } else {
                        language = "formatterBannedUntilThisYear24H";
                        i = C0446R.string.formatterBannedUntilThisYear24H;
                    }
                    this.formatterBannedUntilThisYear = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
                }
            }
            locale = locale2;
            if (is24HourFormat) {
                str = "formatterDay12H";
                i2 = C0446R.string.formatterDay12H;
            } else {
                str = "formatterDay24H";
                i2 = C0446R.string.formatterDay24H;
            }
            if (is24HourFormat) {
            }
            this.formatterDay = createFormatter(locale, getStringInternal(str, i2), is24HourFormat ? "HH:mm" : "h:mm a");
            if (is24HourFormat) {
                language = "formatterStats12H";
                i = C0446R.string.formatterStats12H;
            } else {
                language = "formatterStats24H";
                i = C0446R.string.formatterStats24H;
            }
            if (is24HourFormat) {
            }
            this.formatterStats = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
            if (is24HourFormat) {
                language = "formatterBannedUntil12H";
                i = C0446R.string.formatterBannedUntil12H;
            } else {
                language = "formatterBannedUntil24H";
                i = C0446R.string.formatterBannedUntil24H;
            }
            if (is24HourFormat) {
            }
            this.formatterBannedUntil = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
            if (is24HourFormat) {
                language = "formatterBannedUntilThisYear12H";
                i = C0446R.string.formatterBannedUntilThisYear12H;
            } else {
                language = "formatterBannedUntilThisYear24H";
                i = C0446R.string.formatterBannedUntilThisYear24H;
            }
            if (is24HourFormat) {
            }
            this.formatterBannedUntilThisYear = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
        }
        z = true;
        isRTL = z;
        if (language.equals("ko")) {
            i2 = 2;
        }
        nameDisplayOrder = i2;
        this.formatterMonth = createFormatter(locale2, getStringInternal("formatterMonth", C0446R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale2, getStringInternal("formatterYear", C0446R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale2, getStringInternal("formatterYearMax", C0446R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale2, getStringInternal("chatDate", C0446R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale2, getStringInternal("chatFullDate", C0446R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale2, getStringInternal("formatterWeek", C0446R.string.formatterWeek), "EEE");
        this.formatterMonthYear = createFormatter(locale2, getStringInternal("formatterMonthYear", C0446R.string.formatterMonthYear), "MMMM yyyy");
        if (language.toLowerCase().equals("ar")) {
            if (language.toLowerCase().equals("ko")) {
                locale = Locale.US;
                if (is24HourFormat) {
                    str = "formatterDay24H";
                    i2 = C0446R.string.formatterDay24H;
                } else {
                    str = "formatterDay12H";
                    i2 = C0446R.string.formatterDay12H;
                }
                if (is24HourFormat) {
                }
                this.formatterDay = createFormatter(locale, getStringInternal(str, i2), is24HourFormat ? "HH:mm" : "h:mm a");
                if (is24HourFormat) {
                    language = "formatterStats24H";
                    i = C0446R.string.formatterStats24H;
                } else {
                    language = "formatterStats12H";
                    i = C0446R.string.formatterStats12H;
                }
                if (is24HourFormat) {
                }
                this.formatterStats = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                if (is24HourFormat) {
                    language = "formatterBannedUntil24H";
                    i = C0446R.string.formatterBannedUntil24H;
                } else {
                    language = "formatterBannedUntil12H";
                    i = C0446R.string.formatterBannedUntil12H;
                }
                if (is24HourFormat) {
                }
                this.formatterBannedUntil = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                if (is24HourFormat) {
                    language = "formatterBannedUntilThisYear24H";
                    i = C0446R.string.formatterBannedUntilThisYear24H;
                } else {
                    language = "formatterBannedUntilThisYear12H";
                    i = C0446R.string.formatterBannedUntilThisYear12H;
                }
                if (is24HourFormat) {
                }
                this.formatterBannedUntilThisYear = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
            }
        }
        locale = locale2;
        if (is24HourFormat) {
            str = "formatterDay12H";
            i2 = C0446R.string.formatterDay12H;
        } else {
            str = "formatterDay24H";
            i2 = C0446R.string.formatterDay24H;
        }
        if (is24HourFormat) {
        }
        this.formatterDay = createFormatter(locale, getStringInternal(str, i2), is24HourFormat ? "HH:mm" : "h:mm a");
        if (is24HourFormat) {
            language = "formatterStats12H";
            i = C0446R.string.formatterStats12H;
        } else {
            language = "formatterStats24H";
            i = C0446R.string.formatterStats24H;
        }
        if (is24HourFormat) {
        }
        this.formatterStats = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
        if (is24HourFormat) {
            language = "formatterBannedUntil12H";
            i = C0446R.string.formatterBannedUntil12H;
        } else {
            language = "formatterBannedUntil24H";
            i = C0446R.string.formatterBannedUntil24H;
        }
        if (is24HourFormat) {
        }
        this.formatterBannedUntil = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
        if (is24HourFormat) {
            language = "formatterBannedUntilThisYear12H";
            i = C0446R.string.formatterBannedUntilThisYear12H;
        } else {
            language = "formatterBannedUntilThisYear24H";
            i = C0446R.string.formatterBannedUntilThisYear24H;
        }
        if (is24HourFormat) {
        }
        this.formatterBannedUntilThisYear = createFormatter(locale2, getStringInternal(language, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
    }

    public static boolean isRTLCharacter(char c) {
        if (Character.getDirectionality(c) == (byte) 1 || Character.getDirectionality(c) == (byte) 2 || Character.getDirectionality(c) == (byte) 16) {
            return true;
        }
        return Character.getDirectionality(c) == '\u0011';
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
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                        return getInstance().formatterMonth.format(new Date(j));
                    }
                    return getInstance().formatterWeek.format(new Date(j));
                }
            }
            return getInstance().formatterDay.format(new Date(j));
        } catch (Throwable e) {
            FileLog.m3e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int i, int[] iArr) {
        StringBuilder stringBuilder = new StringBuilder();
        int i2 = 0;
        while (true) {
            int i3 = i / 1000;
            if (i3 <= 0) {
                break;
            }
            stringBuilder.append("K");
            i2 = (i % 1000) / 100;
            i = i3;
        }
        if (iArr != null) {
            double d = ((double) i) + (((double) i2) / 10.0d);
            for (i3 = 0; i3 < stringBuilder.length(); i3++) {
                d *= 1000.0d;
            }
            iArr[0] = (int) d;
        }
        if (i2 == 0 || stringBuilder.length() <= 0) {
            if (stringBuilder.length() == 2) {
                return String.format(Locale.US, "%dM", new Object[]{Integer.valueOf(i)});
            }
            return String.format(Locale.US, "%d%s", new Object[]{Integer.valueOf(i), stringBuilder.toString()});
        } else if (stringBuilder.length() == 2) {
            return String.format(Locale.US, "%d.%dM", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), stringBuilder.toString()});
        }
    }

    public static String formatUserStatus(int i, User user) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            return getString("Online", C0446R.string.Online);
        }
        if (!(user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user))) {
            if (!(user instanceof TL_userEmpty)) {
                if (user.status.expires > ConnectionsManager.getInstance(i).getCurrentTime()) {
                    return getString("Online", C0446R.string.Online);
                }
                if (user.status.expires == -1) {
                    return getString("Invisible", C0446R.string.Invisible);
                }
                if (user.status.expires == -100) {
                    return getString("Lately", C0446R.string.Lately);
                }
                if (user.status.expires == -101) {
                    return getString("WithinAWeek", C0446R.string.WithinAWeek);
                }
                if (user.status.expires == -102) {
                    return getString("WithinAMonth", C0446R.string.WithinAMonth);
                }
                return formatDateOnline((long) user.status.expires);
            }
        }
        return getString("ALongTimeAgo", C0446R.string.ALongTimeAgo);
    }

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStrings(final org.telegram.tgnet.TLRPC.TL_langPackDifference r10, int r11) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        if (r10 == 0) goto L_0x01c8;
    L_0x0002:
        r11 = r10.strings;
        r11 = r11.isEmpty();
        if (r11 == 0) goto L_0x000c;
    L_0x000a:
        goto L_0x01c8;
    L_0x000c:
        r11 = r10.lang_code;
        r0 = 45;
        r1 = 95;
        r11 = r11.replace(r0, r1);
        r11 = r11.toLowerCase();
        r0 = r9.currentLocaleInfo;
        r0 = r0.shortName;
        r0 = r11.equals(r0);
        if (r0 != 0) goto L_0x0025;
    L_0x0024:
        return;
    L_0x0025:
        r0 = new java.io.File;
        r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "remote_";
        r2.append(r3);
        r2.append(r11);
        r3 = ".xml";
        r2.append(r3);
        r2 = r2.toString();
        r0.<init>(r1, r2);
        r1 = r10.from_version;	 Catch:{ Exception -> 0x01c7 }
        r2 = 1;	 Catch:{ Exception -> 0x01c7 }
        if (r1 != 0) goto L_0x004f;	 Catch:{ Exception -> 0x01c7 }
    L_0x0049:
        r1 = new java.util.HashMap;	 Catch:{ Exception -> 0x01c7 }
        r1.<init>();	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0053;	 Catch:{ Exception -> 0x01c7 }
    L_0x004f:
        r1 = r9.getLocaleFileStrings(r0, r2);	 Catch:{ Exception -> 0x01c7 }
    L_0x0053:
        r3 = 0;	 Catch:{ Exception -> 0x01c7 }
        r4 = r3;	 Catch:{ Exception -> 0x01c7 }
    L_0x0055:
        r5 = r10.strings;	 Catch:{ Exception -> 0x01c7 }
        r5 = r5.size();	 Catch:{ Exception -> 0x01c7 }
        if (r4 >= r5) goto L_0x015a;	 Catch:{ Exception -> 0x01c7 }
    L_0x005d:
        r5 = r10.strings;	 Catch:{ Exception -> 0x01c7 }
        r5 = r5.get(r4);	 Catch:{ Exception -> 0x01c7 }
        r5 = (org.telegram.tgnet.TLRPC.LangPackString) r5;	 Catch:{ Exception -> 0x01c7 }
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_langPackString;	 Catch:{ Exception -> 0x01c7 }
        if (r6 == 0) goto L_0x0076;	 Catch:{ Exception -> 0x01c7 }
    L_0x0069:
        r6 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r5 = r5.value;	 Catch:{ Exception -> 0x01c7 }
        r5 = r9.escapeString(r5);	 Catch:{ Exception -> 0x01c7 }
        r1.put(r6, r5);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0156;	 Catch:{ Exception -> 0x01c7 }
    L_0x0076:
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_langPackStringPluralized;	 Catch:{ Exception -> 0x01c7 }
        if (r6 == 0) goto L_0x014d;	 Catch:{ Exception -> 0x01c7 }
    L_0x007a:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_zero";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.zero_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x0098;	 Catch:{ Exception -> 0x01c7 }
    L_0x0091:
        r7 = r5.zero_value;	 Catch:{ Exception -> 0x01c7 }
        r7 = r9.escapeString(r7);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x009a;	 Catch:{ Exception -> 0x01c7 }
    L_0x0098:
        r7 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x009a:
        r1.put(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_one";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.one_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x00bb;	 Catch:{ Exception -> 0x01c7 }
    L_0x00b4:
        r7 = r5.one_value;	 Catch:{ Exception -> 0x01c7 }
        r7 = r9.escapeString(r7);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x00bd;	 Catch:{ Exception -> 0x01c7 }
    L_0x00bb:
        r7 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x00bd:
        r1.put(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_two";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.two_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x00de;	 Catch:{ Exception -> 0x01c7 }
    L_0x00d7:
        r7 = r5.two_value;	 Catch:{ Exception -> 0x01c7 }
        r7 = r9.escapeString(r7);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x00e0;	 Catch:{ Exception -> 0x01c7 }
    L_0x00de:
        r7 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x00e0:
        r1.put(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_few";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.few_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x0101;	 Catch:{ Exception -> 0x01c7 }
    L_0x00fa:
        r7 = r5.few_value;	 Catch:{ Exception -> 0x01c7 }
        r7 = r9.escapeString(r7);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0103;	 Catch:{ Exception -> 0x01c7 }
    L_0x0101:
        r7 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x0103:
        r1.put(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_many";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.many_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x0124;	 Catch:{ Exception -> 0x01c7 }
    L_0x011d:
        r7 = r5.many_value;	 Catch:{ Exception -> 0x01c7 }
        r7 = r9.escapeString(r7);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0126;	 Catch:{ Exception -> 0x01c7 }
    L_0x0124:
        r7 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x0126:
        r1.put(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r6.<init>();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r7 = "_other";	 Catch:{ Exception -> 0x01c7 }
        r6.append(r7);	 Catch:{ Exception -> 0x01c7 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01c7 }
        r7 = r5.other_value;	 Catch:{ Exception -> 0x01c7 }
        if (r7 == 0) goto L_0x0147;	 Catch:{ Exception -> 0x01c7 }
    L_0x0140:
        r5 = r5.other_value;	 Catch:{ Exception -> 0x01c7 }
        r5 = r9.escapeString(r5);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0149;	 Catch:{ Exception -> 0x01c7 }
    L_0x0147:
        r5 = "";	 Catch:{ Exception -> 0x01c7 }
    L_0x0149:
        r1.put(r6, r5);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0156;	 Catch:{ Exception -> 0x01c7 }
    L_0x014d:
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_langPackStringDeleted;	 Catch:{ Exception -> 0x01c7 }
        if (r6 == 0) goto L_0x0156;	 Catch:{ Exception -> 0x01c7 }
    L_0x0151:
        r5 = r5.key;	 Catch:{ Exception -> 0x01c7 }
        r1.remove(r5);	 Catch:{ Exception -> 0x01c7 }
    L_0x0156:
        r4 = r4 + 1;	 Catch:{ Exception -> 0x01c7 }
        goto L_0x0055;	 Catch:{ Exception -> 0x01c7 }
    L_0x015a:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01c7 }
        if (r4 == 0) goto L_0x0172;	 Catch:{ Exception -> 0x01c7 }
    L_0x015e:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7 }
        r4.<init>();	 Catch:{ Exception -> 0x01c7 }
        r5 = "save locale file to ";	 Catch:{ Exception -> 0x01c7 }
        r4.append(r5);	 Catch:{ Exception -> 0x01c7 }
        r4.append(r0);	 Catch:{ Exception -> 0x01c7 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01c7 }
        org.telegram.messenger.FileLog.m0d(r4);	 Catch:{ Exception -> 0x01c7 }
    L_0x0172:
        r4 = new java.io.BufferedWriter;	 Catch:{ Exception -> 0x01c7 }
        r5 = new java.io.FileWriter;	 Catch:{ Exception -> 0x01c7 }
        r5.<init>(r0);	 Catch:{ Exception -> 0x01c7 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01c7 }
        r5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";	 Catch:{ Exception -> 0x01c7 }
        r4.write(r5);	 Catch:{ Exception -> 0x01c7 }
        r5 = "<resources>\n";	 Catch:{ Exception -> 0x01c7 }
        r4.write(r5);	 Catch:{ Exception -> 0x01c7 }
        r1 = r1.entrySet();	 Catch:{ Exception -> 0x01c7 }
        r1 = r1.iterator();	 Catch:{ Exception -> 0x01c7 }
    L_0x018e:
        r5 = r1.hasNext();	 Catch:{ Exception -> 0x01c7 }
        if (r5 == 0) goto L_0x01b3;	 Catch:{ Exception -> 0x01c7 }
    L_0x0194:
        r5 = r1.next();	 Catch:{ Exception -> 0x01c7 }
        r5 = (java.util.Map.Entry) r5;	 Catch:{ Exception -> 0x01c7 }
        r6 = "<string name=\"%1$s\">%2$s</string>\n";	 Catch:{ Exception -> 0x01c7 }
        r7 = 2;	 Catch:{ Exception -> 0x01c7 }
        r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x01c7 }
        r8 = r5.getKey();	 Catch:{ Exception -> 0x01c7 }
        r7[r3] = r8;	 Catch:{ Exception -> 0x01c7 }
        r5 = r5.getValue();	 Catch:{ Exception -> 0x01c7 }
        r7[r2] = r5;	 Catch:{ Exception -> 0x01c7 }
        r5 = java.lang.String.format(r6, r7);	 Catch:{ Exception -> 0x01c7 }
        r4.write(r5);	 Catch:{ Exception -> 0x01c7 }
        goto L_0x018e;	 Catch:{ Exception -> 0x01c7 }
    L_0x01b3:
        r1 = "</resources>";	 Catch:{ Exception -> 0x01c7 }
        r4.write(r1);	 Catch:{ Exception -> 0x01c7 }
        r4.close();	 Catch:{ Exception -> 0x01c7 }
        r0 = r9.getLocaleFileStrings(r0);	 Catch:{ Exception -> 0x01c7 }
        r1 = new org.telegram.messenger.LocaleController$4;	 Catch:{ Exception -> 0x01c7 }
        r1.<init>(r11, r10, r0);	 Catch:{ Exception -> 0x01c7 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Exception -> 0x01c7 }
    L_0x01c7:
        return;
    L_0x01c8:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.saveRemoteLocaleStrings(org.telegram.tgnet.TLRPC$TL_langPackDifference, int):void");
    }

    public void loadRemoteLanguages(final int i) {
        if (!this.loadingRemoteLanguages) {
            this.loadingRemoteLanguages = true;
            ConnectionsManager.getInstance(i).sendRequest(new TL_langpack_getLanguages(), new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    if (tLObject != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LocaleController.this.loadingRemoteLanguages = false;
                                Vector vector = (Vector) tLObject;
                                HashMap hashMap = new HashMap();
                                LocaleController.this.remoteLanguages.clear();
                                for (int i = 0; i < vector.objects.size(); i++) {
                                    TL_langPackLanguage tL_langPackLanguage = (TL_langPackLanguage) vector.objects.get(i);
                                    if (BuildVars.LOGS_ENABLED) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("loaded lang ");
                                        stringBuilder.append(tL_langPackLanguage.name);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                    LocaleInfo localeInfo = new LocaleInfo();
                                    localeInfo.nameEnglish = tL_langPackLanguage.name;
                                    localeInfo.name = tL_langPackLanguage.native_name;
                                    localeInfo.shortName = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
                                    localeInfo.pathToFile = "remote";
                                    LocaleInfo access$200 = LocaleController.this.getLanguageFromDict(localeInfo.getKey());
                                    if (access$200 == null) {
                                        LocaleController.this.languages.add(localeInfo);
                                        LocaleController.this.languagesDict.put(localeInfo.getKey(), localeInfo);
                                        access$200 = localeInfo;
                                    } else {
                                        access$200.nameEnglish = localeInfo.nameEnglish;
                                        access$200.name = localeInfo.name;
                                        access$200.pathToFile = localeInfo.pathToFile;
                                    }
                                    LocaleController.this.remoteLanguages.add(access$200);
                                    hashMap.put(access$200.getKey(), access$200);
                                }
                                int i2 = 0;
                                while (i2 < LocaleController.this.languages.size()) {
                                    LocaleInfo localeInfo2 = (LocaleInfo) LocaleController.this.languages.get(i2);
                                    if (!localeInfo2.isBuiltIn()) {
                                        if (localeInfo2.isRemote()) {
                                            if (((LocaleInfo) hashMap.get(localeInfo2.getKey())) == null) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("remove lang ");
                                                    stringBuilder.append(localeInfo2.getKey());
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                                LocaleController.this.languages.remove(i2);
                                                LocaleController.this.languagesDict.remove(localeInfo2.getKey());
                                                i2--;
                                                if (localeInfo2 == LocaleController.this.currentLocaleInfo) {
                                                    if (LocaleController.this.systemDefaultLocale.getLanguage() != null) {
                                                        localeInfo2 = LocaleController.this.getLanguageFromDict(LocaleController.this.systemDefaultLocale.getLanguage());
                                                    }
                                                    if (localeInfo2 == null) {
                                                        localeInfo2 = LocaleController.this.getLanguageFromDict(LocaleController.this.getLocaleString(LocaleController.this.systemDefaultLocale));
                                                    }
                                                    if (localeInfo2 == null) {
                                                        localeInfo2 = LocaleController.this.getLanguageFromDict("en");
                                                    }
                                                    LocaleController.this.applyLanguage(localeInfo2, true, false, i);
                                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                                                }
                                            }
                                        }
                                    }
                                    i2++;
                                }
                                LocaleController.this.saveOtherLanguages();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
                                LocaleController.this.applyLanguage(LocaleController.this.currentLocaleInfo, true, false, i);
                            }
                        });
                    }
                }
            }, 8);
        }
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, boolean z, final int i) {
        if (localeInfo != null) {
            if (localeInfo == null || localeInfo.isRemote()) {
                if (localeInfo.version == 0 || z) {
                    for (z = false; z < true; z++) {
                        ConnectionsManager.setLangCode(localeInfo.shortName);
                    }
                    z = new TL_langpack_getLangPack();
                    z.lang_code = localeInfo.shortName.replace("_", "-");
                    ConnectionsManager.getInstance(i).sendRequest(z, new RequestDelegate() {
                        public void run(final TLObject tLObject, TL_error tL_error) {
                            if (tLObject != null) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        LocaleController.this.saveRemoteLocaleStrings((TL_langPackDifference) tLObject, i);
                                    }
                                });
                            }
                        }
                    }, 8);
                } else {
                    z = new TL_langpack_getDifference();
                    z.from_version = localeInfo.version;
                    ConnectionsManager.getInstance(i).sendRequest(z, new RequestDelegate() {
                        public void run(final TLObject tLObject, TL_error tL_error) {
                            if (tLObject != null) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        LocaleController.this.saveRemoteLocaleStrings((TL_langPackDifference) tLObject, i);
                                    }
                                });
                            }
                        }
                    }, 8);
                }
            }
        }
    }

    public String getTranslitString(String str) {
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
        StringBuilder stringBuilder = new StringBuilder(str.length());
        int length = str.length();
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            String str2 = (String) this.translitChars.get(substring);
            if (str2 != null) {
                stringBuilder.append(str2);
            } else {
                stringBuilder.append(substring);
            }
            i = i2;
        }
        return stringBuilder.toString();
    }

    public static String addNbsp(String str) {
        return str.replace(' ', '\u00a0');
    }
}
