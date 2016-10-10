package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.text.format.DateFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;

public class LocaleController
{
  private static volatile LocaleController Instance = null;
  static final int QUANTITY_FEW = 8;
  static final int QUANTITY_MANY = 16;
  static final int QUANTITY_ONE = 2;
  static final int QUANTITY_OTHER = 0;
  static final int QUANTITY_TWO = 4;
  static final int QUANTITY_ZERO = 1;
  private static boolean is24HourFormat;
  public static boolean isRTL = false;
  public static int nameDisplayOrder = 1;
  private HashMap<String, PluralRules> allRules = new HashMap();
  private boolean changingConfiguration = false;
  public FastDateFormat chatDate;
  public FastDateFormat chatFullDate;
  private Locale currentLocale;
  private LocaleInfo currentLocaleInfo;
  private PluralRules currentPluralRules;
  private LocaleInfo defaultLocalInfo;
  public FastDateFormat formatterDay;
  public FastDateFormat formatterMonth;
  public FastDateFormat formatterMonthYear;
  public FastDateFormat formatterWeek;
  public FastDateFormat formatterYear;
  public FastDateFormat formatterYearMax;
  private String languageOverride;
  public HashMap<String, LocaleInfo> languagesDict = new HashMap();
  private HashMap<String, String> localeValues = new HashMap();
  private ArrayList<LocaleInfo> otherLanguages = new ArrayList();
  public ArrayList<LocaleInfo> sortedLanguages = new ArrayList();
  private Locale systemDefaultLocale;
  private HashMap<String, String> translitChars;
  
  static
  {
    is24HourFormat = false;
  }
  
  public LocaleController()
  {
    Object localObject1 = new PluralRules_One();
    addRules(new String[] { "bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Czech();
    addRules(new String[] { "cs", "sk" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_French();
    addRules(new String[] { "ff", "fr", "kab" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Balkan();
    addRules(new String[] { "hr", "ru", "sr", "uk", "be", "bs", "sh" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Latvian();
    addRules(new String[] { "lv" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Lithuanian();
    addRules(new String[] { "lt" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Polish();
    addRules(new String[] { "pl" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Romanian();
    addRules(new String[] { "ro", "mo" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Slovenian();
    addRules(new String[] { "sl" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Arabic();
    addRules(new String[] { "ar" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Macedonian();
    addRules(new String[] { "mk" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Welsh();
    addRules(new String[] { "cy" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Breton();
    addRules(new String[] { "br" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Langi();
    addRules(new String[] { "lag" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Tachelhit();
    addRules(new String[] { "shi" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Maltese();
    addRules(new String[] { "mt" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Two();
    addRules(new String[] { "ga", "se", "sma", "smi", "smj", "smn", "sms" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Zero();
    addRules(new String[] { "ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_None();
    addRules(new String[] { "az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "ka", "km", "kn", "ms", "th" }, (PluralRules)localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "English";
    ((LocaleInfo)localObject1).nameEnglish = "English";
    ((LocaleInfo)localObject1).shortName = "en";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Italiano";
    ((LocaleInfo)localObject1).nameEnglish = "Italian";
    ((LocaleInfo)localObject1).shortName = "it";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Español";
    ((LocaleInfo)localObject1).nameEnglish = "Spanish";
    ((LocaleInfo)localObject1).shortName = "es";
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Deutsch";
    ((LocaleInfo)localObject1).nameEnglish = "German";
    ((LocaleInfo)localObject1).shortName = "de";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Nederlands";
    ((LocaleInfo)localObject1).nameEnglish = "Dutch";
    ((LocaleInfo)localObject1).shortName = "nl";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "العربية";
    ((LocaleInfo)localObject1).nameEnglish = "Arabic";
    ((LocaleInfo)localObject1).shortName = "ar";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Português (Brasil)";
    ((LocaleInfo)localObject1).nameEnglish = "Portuguese (Brazil)";
    ((LocaleInfo)localObject1).shortName = "pt_BR";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Português (Portugal)";
    ((LocaleInfo)localObject1).nameEnglish = "Portuguese (Portugal)";
    ((LocaleInfo)localObject1).shortName = "pt_PT";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "한국어";
    ((LocaleInfo)localObject1).nameEnglish = "Korean";
    ((LocaleInfo)localObject1).shortName = "ko";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    loadOtherLanguages();
    localObject1 = this.otherLanguages.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (LocaleInfo)((Iterator)localObject1).next();
      this.sortedLanguages.add(localObject2);
      this.languagesDict.put(((LocaleInfo)localObject2).shortName, localObject2);
    }
    Collections.sort(this.sortedLanguages, new Comparator()
    {
      public int compare(LocaleController.LocaleInfo paramAnonymousLocaleInfo1, LocaleController.LocaleInfo paramAnonymousLocaleInfo2)
      {
        return paramAnonymousLocaleInfo1.name.compareTo(paramAnonymousLocaleInfo2.name);
      }
    });
    localObject1 = new LocaleInfo();
    this.defaultLocalInfo = ((LocaleInfo)localObject1);
    ((LocaleInfo)localObject1).name = "System default";
    ((LocaleInfo)localObject1).nameEnglish = "System default";
    ((LocaleInfo)localObject1).shortName = null;
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(0, localObject1);
    this.systemDefaultLocale = Locale.getDefault();
    is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
    localObject1 = null;
    boolean bool2 = false;
    try
    {
      localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getString("language", null);
      boolean bool1 = bool2;
      if (localObject2 != null)
      {
        localObject2 = (LocaleInfo)this.languagesDict.get(localObject2);
        localObject1 = localObject2;
        bool1 = bool2;
        if (localObject2 != null)
        {
          bool1 = true;
          localObject1 = localObject2;
        }
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = localObject1;
        if (this.systemDefaultLocale.getLanguage() != null) {
          localObject2 = (LocaleInfo)this.languagesDict.get(this.systemDefaultLocale.getLanguage());
        }
      }
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = (LocaleInfo)this.languagesDict.get(getLocaleString(this.systemDefaultLocale));
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = (LocaleInfo)this.languagesDict.get("en");
      }
      applyLanguage((LocaleInfo)localObject2, bool1);
      return;
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          localObject1 = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
          ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(null), (IntentFilter)localObject1);
          return;
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
        }
        localException1 = localException1;
        FileLog.e("tmessages", localException1);
      }
    }
  }
  
  private void addRules(String[] paramArrayOfString, PluralRules paramPluralRules)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      this.allRules.put(str, paramPluralRules);
      i += 1;
    }
  }
  
  private FastDateFormat createFormatter(Locale paramLocale, String paramString1, String paramString2)
  {
    String str;
    if (paramString1 != null)
    {
      str = paramString1;
      if (paramString1.length() != 0) {}
    }
    else
    {
      str = paramString2;
    }
    try
    {
      paramString1 = FastDateFormat.getInstance(str, paramLocale);
      return paramString1;
    }
    catch (Exception paramString1) {}
    return FastDateFormat.getInstance(paramString2, paramLocale);
  }
  
  public static String formatDate(long paramLong)
  {
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(6);
      int j = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(paramLong * 1000L);
      int k = ((Calendar)localObject).get(6);
      int m = ((Calendar)localObject).get(1);
      if ((k == i) && (j == m)) {
        return getInstance().formatterDay.format(new Date(1000L * paramLong));
      }
      if ((k + 1 == i) && (j == m)) {
        return getString("Yesterday", 2131166414);
      }
      if (j == m) {
        return getInstance().formatterMonth.format(new Date(1000L * paramLong));
      }
      localObject = getInstance().formatterYear.format(new Date(1000L * paramLong));
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return "LOC_ERR: formatDate";
  }
  
  public static String formatDateAudio(long paramLong)
  {
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(6);
      int j = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(1000L * paramLong);
      int k = ((Calendar)localObject).get(6);
      int m = ((Calendar)localObject).get(1);
      if ((k == i) && (j == m)) {
        return String.format("%s %s", new Object[] { getString("TodayAt", 2131166343), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      }
      if ((k + 1 == i) && (j == m)) {
        return String.format("%s %s", new Object[] { getString("YesterdayAt", 2131166415), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      }
      if (j == m) {
        return formatString("formatDateAtTime", 2131166435, new Object[] { getInstance().formatterMonth.format(new Date(1000L * paramLong)), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      }
      localObject = formatString("formatDateAtTime", 2131166435, new Object[] { getInstance().formatterYear.format(new Date(1000L * paramLong)), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return "LOC_ERR";
  }
  
  public static String formatDateChat(long paramLong)
  {
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(paramLong * 1000L);
      if (i == ((Calendar)localObject).get(1)) {
        return getInstance().chatDate.format(1000L * paramLong);
      }
      localObject = getInstance().chatFullDate.format(1000L * paramLong);
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return "LOC_ERR: formatDateChat";
  }
  
  public static String formatDateOnline(long paramLong)
  {
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(6);
      int j = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(1000L * paramLong);
      int k = ((Calendar)localObject).get(6);
      int m = ((Calendar)localObject).get(1);
      if ((k == i) && (j == m)) {
        return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165789), getString("TodayAt", 2131166343), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      }
      if ((k + 1 == i) && (j == m)) {
        return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165789), getString("YesterdayAt", 2131166415), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      }
      if (j == m)
      {
        localObject = formatString("formatDateAtTime", 2131166435, new Object[] { getInstance().formatterMonth.format(new Date(1000L * paramLong)), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
        return String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165794), localObject });
      }
      localObject = formatString("formatDateAtTime", 2131166435, new Object[] { getInstance().formatterYear.format(new Date(1000L * paramLong)), getInstance().formatterDay.format(new Date(1000L * paramLong)) });
      localObject = String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165794), localObject });
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return "LOC_ERR";
  }
  
  public static String formatPluralString(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().currentPluralRules == null)) {
      return "LOC_ERR:" + paramString;
    }
    String str = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(paramInt));
    paramString = paramString + "_" + str;
    return formatString(paramString, ApplicationLoader.applicationContext.getResources().getIdentifier(paramString, "string", ApplicationLoader.applicationContext.getPackageName()), new Object[] { Integer.valueOf(paramInt) });
  }
  
  public static String formatShortNumber(int paramInt, int[] paramArrayOfInt)
  {
    String str = "";
    int i = 0;
    while (paramInt / 1000 > 0)
    {
      str = str + "K";
      i = paramInt % 1000 / 100;
      paramInt /= 1000;
    }
    if (paramArrayOfInt != null)
    {
      double d = paramInt + i / 10.0D;
      int j = 0;
      while (j < str.length())
      {
        d *= 1000.0D;
        j += 1;
      }
      paramArrayOfInt[0] = ((int)d);
    }
    if ((i != 0) && (str.length() > 0))
    {
      if (str.length() == 2) {
        return String.format(Locale.US, "%d.%dM", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) });
      }
      return String.format(Locale.US, "%d.%d%s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), str });
    }
    if (str.length() == 2) {
      return String.format(Locale.US, "%dM", new Object[] { Integer.valueOf(paramInt) });
    }
    return String.format(Locale.US, "%d%s", new Object[] { Integer.valueOf(paramInt), str });
  }
  
  public static String formatString(String paramString, int paramInt, Object... paramVarArgs)
  {
    try
    {
      String str2 = (String)getInstance().localeValues.get(paramString);
      String str1 = str2;
      if (str2 == null) {
        str1 = ApplicationLoader.applicationContext.getString(paramInt);
      }
      if (getInstance().currentLocale != null) {
        return String.format(getInstance().currentLocale, str1, paramVarArgs);
      }
      paramVarArgs = String.format(str1, paramVarArgs);
      return paramVarArgs;
    }
    catch (Exception paramVarArgs)
    {
      FileLog.e("tmessages", paramVarArgs);
    }
    return "LOC_ERR: " + paramString;
  }
  
  public static String formatStringSimple(String paramString, Object... paramVarArgs)
  {
    try
    {
      if (getInstance().currentLocale != null) {
        return String.format(getInstance().currentLocale, paramString, paramVarArgs);
      }
      paramVarArgs = String.format(paramString, paramVarArgs);
      return paramVarArgs;
    }
    catch (Exception paramVarArgs)
    {
      FileLog.e("tmessages", paramVarArgs);
    }
    return "LOC_ERR: " + paramString;
  }
  
  public static String formatUserStatus(TLRPC.User paramUser)
  {
    if ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires == 0))
    {
      if (!(paramUser.status instanceof TLRPC.TL_userStatusRecently)) {
        break label90;
      }
      paramUser.status.expires = -100;
    }
    while ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires <= 0) && (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(paramUser.id))))
    {
      return getString("Online", 2131166046);
      label90:
      if ((paramUser.status instanceof TLRPC.TL_userStatusLastWeek)) {
        paramUser.status.expires = -101;
      } else if ((paramUser.status instanceof TLRPC.TL_userStatusLastMonth)) {
        paramUser.status.expires = -102;
      }
    }
    if ((paramUser == null) || (paramUser.status == null) || (paramUser.status.expires == 0) || (UserObject.isDeleted(paramUser)) || ((paramUser instanceof TLRPC.TL_userEmpty))) {
      return getString("ALongTimeAgo", 2131165204);
    }
    int i = ConnectionsManager.getInstance().getCurrentTime();
    if (paramUser.status.expires > i) {
      return getString("Online", 2131166046);
    }
    if (paramUser.status.expires == -1) {
      return getString("Invisible", 2131165759);
    }
    if (paramUser.status.expires == -100) {
      return getString("Lately", 2131165814);
    }
    if (paramUser.status.expires == -101) {
      return getString("WithinAWeek", 2131166405);
    }
    if (paramUser.status.expires == -102) {
      return getString("WithinAMonth", 2131166404);
    }
    return formatDateOnline(paramUser.status.expires);
  }
  
  public static String getCurrentLanguageName()
  {
    return getString("LanguageName", 2131165786);
  }
  
  public static LocaleController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          LocaleController localLocaleController2 = Instance;
          localObject1 = localLocaleController2;
          if (localLocaleController2 == null) {
            localObject1 = new LocaleController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (LocaleController)localObject1;
          return (LocaleController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localLocaleController1;
  }
  
  /* Error */
  private HashMap<String, String> getLocaleFileStrings(File paramFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 4
    //   6: aload 5
    //   8: astore_3
    //   9: new 145	java/util/HashMap
    //   12: dup
    //   13: invokespecial 146	java/util/HashMap:<init>	()V
    //   16: astore 9
    //   18: aload 5
    //   20: astore_3
    //   21: invokestatic 857	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   24: astore 10
    //   26: aload 5
    //   28: astore_3
    //   29: new 859	java/io/FileInputStream
    //   32: dup
    //   33: aload_1
    //   34: invokespecial 862	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   37: astore 8
    //   39: aload 10
    //   41: aload 8
    //   43: ldc_w 864
    //   46: invokeinterface 870 3 0
    //   51: aload 10
    //   53: invokeinterface 873 1 0
    //   58: istore_2
    //   59: aconst_null
    //   60: astore 6
    //   62: aconst_null
    //   63: astore 7
    //   65: aconst_null
    //   66: astore 5
    //   68: iload_2
    //   69: iconst_1
    //   70: if_icmpeq +282 -> 352
    //   73: iload_2
    //   74: iconst_2
    //   75: if_icmpne +174 -> 249
    //   78: aload 10
    //   80: invokeinterface 876 1 0
    //   85: astore 6
    //   87: aload 5
    //   89: astore_3
    //   90: aload 6
    //   92: astore 4
    //   94: aload 7
    //   96: astore_1
    //   97: aload 10
    //   99: invokeinterface 879 1 0
    //   104: ifle +19 -> 123
    //   107: aload 10
    //   109: iconst_0
    //   110: invokeinterface 882 2 0
    //   115: astore_3
    //   116: aload 7
    //   118: astore_1
    //   119: aload 6
    //   121: astore 4
    //   123: aload_3
    //   124: astore 5
    //   126: aload 4
    //   128: astore 6
    //   130: aload_1
    //   131: astore 7
    //   133: aload 4
    //   135: ifnull +103 -> 238
    //   138: aload_3
    //   139: astore 5
    //   141: aload 4
    //   143: astore 6
    //   145: aload_1
    //   146: astore 7
    //   148: aload 4
    //   150: ldc_w 727
    //   153: invokevirtual 885	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   156: ifeq +82 -> 238
    //   159: aload_3
    //   160: astore 5
    //   162: aload 4
    //   164: astore 6
    //   166: aload_1
    //   167: astore 7
    //   169: aload_1
    //   170: ifnull +68 -> 238
    //   173: aload_3
    //   174: astore 5
    //   176: aload 4
    //   178: astore 6
    //   180: aload_1
    //   181: astore 7
    //   183: aload_3
    //   184: ifnull +54 -> 238
    //   187: aload_3
    //   188: astore 5
    //   190: aload 4
    //   192: astore 6
    //   194: aload_1
    //   195: astore 7
    //   197: aload_1
    //   198: invokevirtual 611	java/lang/String:length	()I
    //   201: ifeq +37 -> 238
    //   204: aload_3
    //   205: astore 5
    //   207: aload 4
    //   209: astore 6
    //   211: aload_1
    //   212: astore 7
    //   214: aload_3
    //   215: invokevirtual 611	java/lang/String:length	()I
    //   218: ifeq +20 -> 238
    //   221: aload 9
    //   223: aload_3
    //   224: aload_1
    //   225: invokevirtual 463	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   228: pop
    //   229: aconst_null
    //   230: astore 6
    //   232: aconst_null
    //   233: astore 7
    //   235: aconst_null
    //   236: astore 5
    //   238: aload 10
    //   240: invokeinterface 887 1 0
    //   245: istore_2
    //   246: goto -178 -> 68
    //   249: iload_2
    //   250: iconst_4
    //   251: if_icmpne +76 -> 327
    //   254: aload 5
    //   256: astore_3
    //   257: aload 6
    //   259: astore 4
    //   261: aload 7
    //   263: astore_1
    //   264: aload 5
    //   266: ifnull -143 -> 123
    //   269: aload 10
    //   271: invokeinterface 890 1 0
    //   276: astore 7
    //   278: aload 5
    //   280: astore_3
    //   281: aload 6
    //   283: astore 4
    //   285: aload 7
    //   287: astore_1
    //   288: aload 7
    //   290: ifnull -167 -> 123
    //   293: aload 7
    //   295: invokevirtual 893	java/lang/String:trim	()Ljava/lang/String;
    //   298: ldc_w 895
    //   301: ldc_w 897
    //   304: invokevirtual 901	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   307: ldc_w 903
    //   310: ldc_w 746
    //   313: invokevirtual 901	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   316: astore_1
    //   317: aload 5
    //   319: astore_3
    //   320: aload 6
    //   322: astore 4
    //   324: goto -201 -> 123
    //   327: aload 5
    //   329: astore_3
    //   330: aload 6
    //   332: astore 4
    //   334: aload 7
    //   336: astore_1
    //   337: iload_2
    //   338: iconst_3
    //   339: if_icmpne -216 -> 123
    //   342: aconst_null
    //   343: astore_1
    //   344: aconst_null
    //   345: astore_3
    //   346: aconst_null
    //   347: astore 4
    //   349: goto -226 -> 123
    //   352: aload 8
    //   354: ifnull +8 -> 362
    //   357: aload 8
    //   359: invokevirtual 906	java/io/FileInputStream:close	()V
    //   362: aload 9
    //   364: areturn
    //   365: astore_1
    //   366: ldc_w 599
    //   369: aload_1
    //   370: invokestatic 605	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   373: goto -11 -> 362
    //   376: astore_3
    //   377: aload 4
    //   379: astore_1
    //   380: aload_3
    //   381: astore 4
    //   383: aload_1
    //   384: astore_3
    //   385: ldc_w 599
    //   388: aload 4
    //   390: invokestatic 605	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   393: aload_1
    //   394: ifnull +7 -> 401
    //   397: aload_1
    //   398: invokevirtual 906	java/io/FileInputStream:close	()V
    //   401: new 145	java/util/HashMap
    //   404: dup
    //   405: invokespecial 146	java/util/HashMap:<init>	()V
    //   408: areturn
    //   409: astore_1
    //   410: ldc_w 599
    //   413: aload_1
    //   414: invokestatic 605	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   417: goto -16 -> 401
    //   420: astore_1
    //   421: aload_3
    //   422: ifnull +7 -> 429
    //   425: aload_3
    //   426: invokevirtual 906	java/io/FileInputStream:close	()V
    //   429: aload_1
    //   430: athrow
    //   431: astore_3
    //   432: ldc_w 599
    //   435: aload_3
    //   436: invokestatic 605	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   439: goto -10 -> 429
    //   442: astore_1
    //   443: aload 8
    //   445: astore_3
    //   446: goto -25 -> 421
    //   449: astore 4
    //   451: aload 8
    //   453: astore_1
    //   454: goto -71 -> 383
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	457	0	this	LocaleController
    //   0	457	1	paramFile	File
    //   58	282	2	i	int
    //   8	338	3	localObject1	Object
    //   376	5	3	localException1	Exception
    //   384	42	3	localFile	File
    //   431	5	3	localException2	Exception
    //   445	1	3	localObject2	Object
    //   4	385	4	localObject3	Object
    //   449	1	4	localException3	Exception
    //   1	327	5	localObject4	Object
    //   60	271	6	localObject5	Object
    //   63	272	7	localObject6	Object
    //   37	415	8	localFileInputStream	java.io.FileInputStream
    //   16	347	9	localHashMap	HashMap
    //   24	246	10	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    // Exception table:
    //   from	to	target	type
    //   357	362	365	java/lang/Exception
    //   9	18	376	java/lang/Exception
    //   21	26	376	java/lang/Exception
    //   29	39	376	java/lang/Exception
    //   397	401	409	java/lang/Exception
    //   9	18	420	finally
    //   21	26	420	finally
    //   29	39	420	finally
    //   385	393	420	finally
    //   425	429	431	java/lang/Exception
    //   39	59	442	finally
    //   78	87	442	finally
    //   97	116	442	finally
    //   148	159	442	finally
    //   197	204	442	finally
    //   214	229	442	finally
    //   238	246	442	finally
    //   269	278	442	finally
    //   293	317	442	finally
    //   39	59	449	java/lang/Exception
    //   78	87	449	java/lang/Exception
    //   97	116	449	java/lang/Exception
    //   148	159	449	java/lang/Exception
    //   197	204	449	java/lang/Exception
    //   214	229	449	java/lang/Exception
    //   238	246	449	java/lang/Exception
    //   269	278	449	java/lang/Exception
    //   293	317	449	java/lang/Exception
  }
  
  private String getLocaleString(Locale paramLocale)
  {
    if (paramLocale == null) {
      return "en";
    }
    String str1 = paramLocale.getLanguage();
    String str2 = paramLocale.getCountry();
    paramLocale = paramLocale.getVariant();
    if ((str1.length() == 0) && (str2.length() == 0)) {
      return "en";
    }
    StringBuilder localStringBuilder = new StringBuilder(11);
    localStringBuilder.append(str1);
    if ((str2.length() > 0) || (paramLocale.length() > 0)) {
      localStringBuilder.append('_');
    }
    localStringBuilder.append(str2);
    if (paramLocale.length() > 0) {
      localStringBuilder.append('_');
    }
    localStringBuilder.append(paramLocale);
    return localStringBuilder.toString();
  }
  
  public static String getLocaleStringIso639()
  {
    Object localObject = getInstance().getSystemDefaultLocale();
    if (localObject == null) {
      return "en";
    }
    String str1 = ((Locale)localObject).getLanguage();
    String str2 = ((Locale)localObject).getCountry();
    localObject = ((Locale)localObject).getVariant();
    if ((str1.length() == 0) && (str2.length() == 0)) {
      return "en";
    }
    StringBuilder localStringBuilder = new StringBuilder(11);
    localStringBuilder.append(str1);
    if ((str2.length() > 0) || (((String)localObject).length() > 0)) {
      localStringBuilder.append('-');
    }
    localStringBuilder.append(str2);
    if (((String)localObject).length() > 0) {
      localStringBuilder.append('_');
    }
    localStringBuilder.append((String)localObject);
    return localStringBuilder.toString();
  }
  
  public static String getString(String paramString, int paramInt)
  {
    return getInstance().getStringInternal(paramString, paramInt);
  }
  
  private String getStringInternal(String paramString, int paramInt)
  {
    Object localObject3 = (String)this.localeValues.get(paramString);
    Object localObject1 = localObject3;
    if (localObject3 == null) {}
    try
    {
      localObject1 = ApplicationLoader.applicationContext.getString(paramInt);
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = "LOC_ERR:" + paramString;
      }
      return (String)localObject3;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
        Object localObject2 = localObject3;
      }
    }
  }
  
  private void loadCurrentLocale()
  {
    this.localeValues.clear();
  }
  
  private void loadOtherLanguages()
  {
    int i = 0;
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).getString("locales", null);
    if ((localObject == null) || (((String)localObject).length() == 0)) {}
    for (;;)
    {
      return;
      localObject = ((String)localObject).split("&");
      int j = localObject.length;
      while (i < j)
      {
        LocaleInfo localLocaleInfo = LocaleInfo.createWithString(localObject[i]);
        if (localLocaleInfo != null) {
          this.otherLanguages.add(localLocaleInfo);
        }
        i += 1;
      }
    }
  }
  
  private void saveOtherLanguages()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
    String str1 = "";
    Iterator localIterator = this.otherLanguages.iterator();
    while (localIterator.hasNext())
    {
      String str3 = ((LocaleInfo)localIterator.next()).getSaveString();
      if (str3 != null)
      {
        String str2 = str1;
        if (str1.length() != 0) {
          str2 = str1 + "&";
        }
        str1 = str2 + str3;
      }
    }
    localEditor.putString("locales", str1);
    localEditor.commit();
  }
  
  public static String stringForMessageListDate(long paramLong)
  {
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(6);
      int j = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(1000L * paramLong);
      int k = ((Calendar)localObject).get(6);
      if (j != ((Calendar)localObject).get(1)) {
        return getInstance().formatterYear.format(new Date(1000L * paramLong));
      }
      i = k - i;
      if ((i == 0) || ((i == -1) && ((int)(System.currentTimeMillis() / 1000L) - paramLong < 28800L))) {
        return getInstance().formatterDay.format(new Date(1000L * paramLong));
      }
      if ((i > -7) && (i <= -1)) {
        return getInstance().formatterWeek.format(new Date(1000L * paramLong));
      }
      localObject = getInstance().formatterMonth.format(new Date(1000L * paramLong));
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return "LOC_ERR";
  }
  
  private String stringForQuantity(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "other";
    case 1: 
      return "zero";
    case 2: 
      return "one";
    case 4: 
      return "two";
    case 8: 
      return "few";
    }
    return "many";
  }
  
  public void applyLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean)
  {
    applyLanguage(paramLocaleInfo, paramBoolean, false);
  }
  
  public void applyLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramLocaleInfo == null) {
      return;
    }
    for (;;)
    {
      try
      {
        if (paramLocaleInfo.shortName == null) {
          continue;
        }
        localObject1 = paramLocaleInfo.shortName.split("_");
        if (localObject1.length != 1) {
          continue;
        }
        localObject2 = new Locale(paramLocaleInfo.shortName);
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (paramBoolean1)
          {
            this.languageOverride = paramLocaleInfo.shortName;
            localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            ((SharedPreferences.Editor)localObject1).putString("language", paramLocaleInfo.shortName);
            ((SharedPreferences.Editor)localObject1).commit();
            localObject1 = localObject2;
          }
        }
        if (localObject1 != null)
        {
          if (paramLocaleInfo.pathToFile != null) {
            continue;
          }
          this.localeValues.clear();
          this.currentLocale = ((Locale)localObject1);
          this.currentLocaleInfo = paramLocaleInfo;
          this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
          if (this.currentPluralRules == null) {
            this.currentPluralRules = ((PluralRules)this.allRules.get("en"));
          }
          this.changingConfiguration = true;
          Locale.setDefault(this.currentLocale);
          paramLocaleInfo = new Configuration();
          paramLocaleInfo.locale = this.currentLocale;
          ApplicationLoader.applicationContext.getResources().updateConfiguration(paramLocaleInfo, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
          this.changingConfiguration = false;
        }
      }
      catch (Exception paramLocaleInfo)
      {
        Object localObject1;
        Object localObject2;
        Locale localLocale;
        FileLog.e("tmessages", paramLocaleInfo);
        this.changingConfiguration = false;
        continue;
      }
      recreateFormatters();
      return;
      localObject2 = new Locale(localObject1[0], localObject1[1]);
      continue;
      localLocale = this.systemDefaultLocale;
      this.languageOverride = null;
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      ((SharedPreferences.Editor)localObject1).remove("language");
      ((SharedPreferences.Editor)localObject1).commit();
      localObject1 = localLocale;
      if (localLocale != null)
      {
        localObject1 = null;
        if (localLocale.getLanguage() != null) {
          localObject1 = (LocaleInfo)this.languagesDict.get(localLocale.getLanguage());
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = (LocaleInfo)this.languagesDict.get(getLocaleString(localLocale));
        }
        localObject1 = localLocale;
        if (localObject2 == null)
        {
          localObject1 = Locale.US;
          continue;
          if (!paramBoolean2) {
            this.localeValues = getLocaleFileStrings(new File(paramLocaleInfo.pathToFile));
          }
        }
      }
    }
  }
  
  public boolean applyLanguageFile(File paramFile)
  {
    try
    {
      HashMap localHashMap = getLocaleFileStrings(paramFile);
      String str1 = (String)localHashMap.get("LanguageName");
      String str2 = (String)localHashMap.get("LanguageNameInEnglish");
      String str3 = (String)localHashMap.get("LanguageCode");
      if ((str1 != null) && (str1.length() > 0) && (str2 != null) && (str2.length() > 0) && (str3 != null) && (str3.length() > 0) && (!str1.contains("&")))
      {
        if (str1.contains("|")) {
          return false;
        }
        if ((!str2.contains("&")) && (!str2.contains("|")) && (!str3.contains("&")) && (!str3.contains("|")) && (!str3.contains("/")) && (!str3.contains("\\")))
        {
          File localFile = new File(ApplicationLoader.getFilesDirFixed(), str3 + ".xml");
          if (AndroidUtilities.copyFile(paramFile, localFile))
          {
            LocaleInfo localLocaleInfo = (LocaleInfo)this.languagesDict.get(str3);
            paramFile = localLocaleInfo;
            if (localLocaleInfo == null)
            {
              paramFile = new LocaleInfo();
              paramFile.name = str1;
              paramFile.nameEnglish = str2;
              paramFile.shortName = str3;
              paramFile.pathToFile = localFile.getAbsolutePath();
              this.sortedLanguages.add(paramFile);
              this.languagesDict.put(paramFile.shortName, paramFile);
              this.otherLanguages.add(paramFile);
              Collections.sort(this.sortedLanguages, new Comparator()
              {
                public int compare(LocaleController.LocaleInfo paramAnonymousLocaleInfo1, LocaleController.LocaleInfo paramAnonymousLocaleInfo2)
                {
                  if (paramAnonymousLocaleInfo1.shortName == null) {
                    return -1;
                  }
                  if (paramAnonymousLocaleInfo2.shortName == null) {
                    return 1;
                  }
                  return paramAnonymousLocaleInfo1.name.compareTo(paramAnonymousLocaleInfo2.name);
                }
              });
              saveOtherLanguages();
            }
            this.localeValues = localHashMap;
            applyLanguage(paramFile, true, true);
            return true;
          }
        }
      }
    }
    catch (Exception paramFile)
    {
      FileLog.e("tmessages", paramFile);
    }
    return false;
  }
  
  public boolean deleteLanguage(LocaleInfo paramLocaleInfo)
  {
    if (paramLocaleInfo.pathToFile == null) {
      return false;
    }
    if (this.currentLocaleInfo == paramLocaleInfo) {
      applyLanguage(this.defaultLocalInfo, true);
    }
    this.otherLanguages.remove(paramLocaleInfo);
    this.sortedLanguages.remove(paramLocaleInfo);
    this.languagesDict.remove(paramLocaleInfo.shortName);
    new File(paramLocaleInfo.pathToFile).delete();
    saveOtherLanguages();
    return true;
  }
  
  public Locale getSystemDefaultLocale()
  {
    return this.systemDefaultLocale;
  }
  
  public String getTranslitString(String paramString)
  {
    if (this.translitChars == null)
    {
      this.translitChars = new HashMap(520);
      this.translitChars.put("ȼ", "c");
      this.translitChars.put("ᶇ", "n");
      this.translitChars.put("ɖ", "d");
      this.translitChars.put("ỿ", "y");
      this.translitChars.put("ᴓ", "o");
      this.translitChars.put("ø", "o");
      this.translitChars.put("ḁ", "a");
      this.translitChars.put("ʯ", "h");
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
      this.translitChars.put("ⱥ", "a");
      this.translitChars.put("ʙ", "b");
      this.translitChars.put("ḛ", "e");
      this.translitChars.put("ƈ", "c");
      this.translitChars.put("ɦ", "h");
      this.translitChars.put("ᵬ", "b");
      this.translitChars.put("ṣ", "s");
      this.translitChars.put("đ", "d");
      this.translitChars.put("ỗ", "o");
      this.translitChars.put("ɟ", "j");
      this.translitChars.put("ẚ", "a");
      this.translitChars.put("ɏ", "y");
      this.translitChars.put("л", "l");
      this.translitChars.put("ʌ", "v");
      this.translitChars.put("ꝓ", "p");
      this.translitChars.put("ﬁ", "fi");
      this.translitChars.put("ᶄ", "k");
      this.translitChars.put("ḏ", "d");
      this.translitChars.put("ᴌ", "l");
      this.translitChars.put("ė", "e");
      this.translitChars.put("ё", "yo");
      this.translitChars.put("ᴋ", "k");
      this.translitChars.put("ċ", "c");
      this.translitChars.put("ʁ", "r");
      this.translitChars.put("ƕ", "hv");
      this.translitChars.put("ƀ", "b");
      this.translitChars.put("ṍ", "o");
      this.translitChars.put("ȣ", "ou");
      this.translitChars.put("ǰ", "j");
      this.translitChars.put("ᶃ", "g");
      this.translitChars.put("ṋ", "n");
      this.translitChars.put("ɉ", "j");
      this.translitChars.put("ǧ", "g");
      this.translitChars.put("ǳ", "dz");
      this.translitChars.put("ź", "z");
      this.translitChars.put("ꜷ", "au");
      this.translitChars.put("ǖ", "u");
      this.translitChars.put("ᵹ", "g");
      this.translitChars.put("ȯ", "o");
      this.translitChars.put("ɐ", "a");
      this.translitChars.put("ą", "a");
      this.translitChars.put("õ", "o");
      this.translitChars.put("ɻ", "r");
      this.translitChars.put("ꝍ", "o");
      this.translitChars.put("ǟ", "a");
      this.translitChars.put("ȴ", "l");
      this.translitChars.put("ʂ", "s");
      this.translitChars.put("ﬂ", "fl");
      this.translitChars.put("ȉ", "i");
      this.translitChars.put("ⱻ", "e");
      this.translitChars.put("ṉ", "n");
      this.translitChars.put("ï", "i");
      this.translitChars.put("ñ", "n");
      this.translitChars.put("ᴉ", "i");
      this.translitChars.put("ʇ", "t");
      this.translitChars.put("ẓ", "z");
      this.translitChars.put("ỷ", "y");
      this.translitChars.put("ȳ", "y");
      this.translitChars.put("ṩ", "s");
      this.translitChars.put("ɽ", "r");
      this.translitChars.put("ĝ", "g");
      this.translitChars.put("в", "v");
      this.translitChars.put("ᴝ", "u");
      this.translitChars.put("ḳ", "k");
      this.translitChars.put("ꝫ", "et");
      this.translitChars.put("ī", "i");
      this.translitChars.put("ť", "t");
      this.translitChars.put("ꜿ", "c");
      this.translitChars.put("ʟ", "l");
      this.translitChars.put("ꜹ", "av");
      this.translitChars.put("û", "u");
      this.translitChars.put("æ", "ae");
      this.translitChars.put("и", "i");
      this.translitChars.put("ă", "a");
      this.translitChars.put("ǘ", "u");
      this.translitChars.put("ꞅ", "s");
      this.translitChars.put("ᵣ", "r");
      this.translitChars.put("ᴀ", "a");
      this.translitChars.put("ƃ", "b");
      this.translitChars.put("ḩ", "h");
      this.translitChars.put("ṧ", "s");
      this.translitChars.put("ₑ", "e");
      this.translitChars.put("ʜ", "h");
      this.translitChars.put("ẋ", "x");
      this.translitChars.put("ꝅ", "k");
      this.translitChars.put("ḋ", "d");
      this.translitChars.put("ƣ", "oi");
      this.translitChars.put("ꝑ", "p");
      this.translitChars.put("ħ", "h");
      this.translitChars.put("ⱴ", "v");
      this.translitChars.put("ẇ", "w");
      this.translitChars.put("ǹ", "n");
      this.translitChars.put("ɯ", "m");
      this.translitChars.put("ɡ", "g");
      this.translitChars.put("ɴ", "n");
      this.translitChars.put("ᴘ", "p");
      this.translitChars.put("ᵥ", "v");
      this.translitChars.put("ū", "u");
      this.translitChars.put("ḃ", "b");
      this.translitChars.put("ṗ", "p");
      this.translitChars.put("ь", "");
      this.translitChars.put("å", "a");
      this.translitChars.put("ɕ", "c");
      this.translitChars.put("ọ", "o");
      this.translitChars.put("ắ", "a");
      this.translitChars.put("ƒ", "f");
      this.translitChars.put("ǣ", "ae");
      this.translitChars.put("ꝡ", "vy");
      this.translitChars.put("ﬀ", "ff");
      this.translitChars.put("ᶉ", "r");
      this.translitChars.put("ô", "o");
      this.translitChars.put("ǿ", "o");
      this.translitChars.put("ṳ", "u");
      this.translitChars.put("ȥ", "z");
      this.translitChars.put("ḟ", "f");
      this.translitChars.put("ḓ", "d");
      this.translitChars.put("ȇ", "e");
      this.translitChars.put("ȕ", "u");
      this.translitChars.put("п", "p");
      this.translitChars.put("ȵ", "n");
      this.translitChars.put("ʠ", "q");
      this.translitChars.put("ấ", "a");
      this.translitChars.put("ǩ", "k");
      this.translitChars.put("ĩ", "i");
      this.translitChars.put("ṵ", "u");
      this.translitChars.put("ŧ", "t");
      this.translitChars.put("ɾ", "r");
      this.translitChars.put("ƙ", "k");
      this.translitChars.put("ṫ", "t");
      this.translitChars.put("ꝗ", "q");
      this.translitChars.put("ậ", "a");
      this.translitChars.put("н", "n");
      this.translitChars.put("ʄ", "j");
      this.translitChars.put("ƚ", "l");
      this.translitChars.put("ᶂ", "f");
      this.translitChars.put("д", "d");
      this.translitChars.put("ᵴ", "s");
      this.translitChars.put("ꞃ", "r");
      this.translitChars.put("ᶌ", "v");
      this.translitChars.put("ɵ", "o");
      this.translitChars.put("ḉ", "c");
      this.translitChars.put("ᵤ", "u");
      this.translitChars.put("ẑ", "z");
      this.translitChars.put("ṹ", "u");
      this.translitChars.put("ň", "n");
      this.translitChars.put("ʍ", "w");
      this.translitChars.put("ầ", "a");
      this.translitChars.put("ǉ", "lj");
      this.translitChars.put("ɓ", "b");
      this.translitChars.put("ɼ", "r");
      this.translitChars.put("ò", "o");
      this.translitChars.put("ẘ", "w");
      this.translitChars.put("ɗ", "d");
      this.translitChars.put("ꜽ", "ay");
      this.translitChars.put("ư", "u");
      this.translitChars.put("ᶀ", "b");
      this.translitChars.put("ǜ", "u");
      this.translitChars.put("ẹ", "e");
      this.translitChars.put("ǡ", "a");
      this.translitChars.put("ɥ", "h");
      this.translitChars.put("ṏ", "o");
      this.translitChars.put("ǔ", "u");
      this.translitChars.put("ʎ", "y");
      this.translitChars.put("ȱ", "o");
      this.translitChars.put("ệ", "e");
      this.translitChars.put("ế", "e");
      this.translitChars.put("ĭ", "i");
      this.translitChars.put("ⱸ", "e");
      this.translitChars.put("ṯ", "t");
      this.translitChars.put("ᶑ", "d");
      this.translitChars.put("ḧ", "h");
      this.translitChars.put("ṥ", "s");
      this.translitChars.put("ë", "e");
      this.translitChars.put("ᴍ", "m");
      this.translitChars.put("ö", "o");
      this.translitChars.put("é", "e");
      this.translitChars.put("ı", "i");
      this.translitChars.put("ď", "d");
      this.translitChars.put("ᵯ", "m");
      this.translitChars.put("ỵ", "y");
      this.translitChars.put("я", "ya");
      this.translitChars.put("ŵ", "w");
      this.translitChars.put("ề", "e");
      this.translitChars.put("ứ", "u");
      this.translitChars.put("ƶ", "z");
      this.translitChars.put("ĵ", "j");
      this.translitChars.put("ḍ", "d");
      this.translitChars.put("ŭ", "u");
      this.translitChars.put("ʝ", "j");
      this.translitChars.put("ж", "zh");
      this.translitChars.put("ê", "e");
      this.translitChars.put("ǚ", "u");
      this.translitChars.put("ġ", "g");
      this.translitChars.put("ṙ", "r");
      this.translitChars.put("ƞ", "n");
      this.translitChars.put("ъ", "");
      this.translitChars.put("ḗ", "e");
      this.translitChars.put("ẝ", "s");
      this.translitChars.put("ᶁ", "d");
      this.translitChars.put("ķ", "k");
      this.translitChars.put("ᴂ", "ae");
      this.translitChars.put("ɘ", "e");
      this.translitChars.put("ợ", "o");
      this.translitChars.put("ḿ", "m");
      this.translitChars.put("ꜰ", "f");
      this.translitChars.put("а", "a");
      this.translitChars.put("ẵ", "a");
      this.translitChars.put("ꝏ", "oo");
      this.translitChars.put("ᶆ", "m");
      this.translitChars.put("ᵽ", "p");
      this.translitChars.put("ц", "ts");
      this.translitChars.put("ữ", "u");
      this.translitChars.put("ⱪ", "k");
      this.translitChars.put("ḥ", "h");
      this.translitChars.put("ţ", "t");
      this.translitChars.put("ᵱ", "p");
      this.translitChars.put("ṁ", "m");
      this.translitChars.put("á", "a");
      this.translitChars.put("ᴎ", "n");
      this.translitChars.put("ꝟ", "v");
      this.translitChars.put("è", "e");
      this.translitChars.put("ᶎ", "z");
      this.translitChars.put("ꝺ", "d");
      this.translitChars.put("ᶈ", "p");
      this.translitChars.put("м", "m");
      this.translitChars.put("ɫ", "l");
      this.translitChars.put("ᴢ", "z");
      this.translitChars.put("ɱ", "m");
      this.translitChars.put("ṝ", "r");
      this.translitChars.put("ṽ", "v");
      this.translitChars.put("ũ", "u");
      this.translitChars.put("ß", "ss");
      this.translitChars.put("т", "t");
      this.translitChars.put("ĥ", "h");
      this.translitChars.put("ᵵ", "t");
      this.translitChars.put("ʐ", "z");
      this.translitChars.put("ṟ", "r");
      this.translitChars.put("ɲ", "n");
      this.translitChars.put("à", "a");
      this.translitChars.put("ẙ", "y");
      this.translitChars.put("ỳ", "y");
      this.translitChars.put("ᴔ", "oe");
      this.translitChars.put("ы", "i");
      this.translitChars.put("ₓ", "x");
      this.translitChars.put("ȗ", "u");
      this.translitChars.put("ⱼ", "j");
      this.translitChars.put("ẫ", "a");
      this.translitChars.put("ʑ", "z");
      this.translitChars.put("ẛ", "s");
      this.translitChars.put("ḭ", "i");
      this.translitChars.put("ꜵ", "ao");
      this.translitChars.put("ɀ", "z");
      this.translitChars.put("ÿ", "y");
      this.translitChars.put("ǝ", "e");
      this.translitChars.put("ǭ", "o");
      this.translitChars.put("ᴅ", "d");
      this.translitChars.put("ᶅ", "l");
      this.translitChars.put("ù", "u");
      this.translitChars.put("ạ", "a");
      this.translitChars.put("ḅ", "b");
      this.translitChars.put("ụ", "u");
      this.translitChars.put("к", "k");
      this.translitChars.put("ằ", "a");
      this.translitChars.put("ᴛ", "t");
      this.translitChars.put("ƴ", "y");
      this.translitChars.put("ⱦ", "t");
      this.translitChars.put("з", "z");
      this.translitChars.put("ⱡ", "l");
      this.translitChars.put("ȷ", "j");
      this.translitChars.put("ᵶ", "z");
      this.translitChars.put("ḫ", "h");
      this.translitChars.put("ⱳ", "w");
      this.translitChars.put("ḵ", "k");
      this.translitChars.put("ờ", "o");
      this.translitChars.put("î", "i");
      this.translitChars.put("ģ", "g");
      this.translitChars.put("ȅ", "e");
      this.translitChars.put("ȧ", "a");
      this.translitChars.put("ẳ", "a");
      this.translitChars.put("щ", "sch");
      this.translitChars.put("ɋ", "q");
      this.translitChars.put("ṭ", "t");
      this.translitChars.put("ꝸ", "um");
      this.translitChars.put("ᴄ", "c");
      this.translitChars.put("ẍ", "x");
      this.translitChars.put("ủ", "u");
      this.translitChars.put("ỉ", "i");
      this.translitChars.put("ᴚ", "r");
      this.translitChars.put("ś", "s");
      this.translitChars.put("ꝋ", "o");
      this.translitChars.put("ỹ", "y");
      this.translitChars.put("ṡ", "s");
      this.translitChars.put("ǌ", "nj");
      this.translitChars.put("ȁ", "a");
      this.translitChars.put("ẗ", "t");
      this.translitChars.put("ĺ", "l");
      this.translitChars.put("ž", "z");
      this.translitChars.put("ᵺ", "th");
      this.translitChars.put("ƌ", "d");
      this.translitChars.put("ș", "s");
      this.translitChars.put("š", "s");
      this.translitChars.put("ᶙ", "u");
      this.translitChars.put("ẽ", "e");
      this.translitChars.put("ẜ", "s");
      this.translitChars.put("ɇ", "e");
      this.translitChars.put("ṷ", "u");
      this.translitChars.put("ố", "o");
      this.translitChars.put("ȿ", "s");
      this.translitChars.put("ᴠ", "v");
      this.translitChars.put("ꝭ", "is");
      this.translitChars.put("ᴏ", "o");
      this.translitChars.put("ɛ", "e");
      this.translitChars.put("ǻ", "a");
      this.translitChars.put("ﬄ", "ffl");
      this.translitChars.put("ⱺ", "o");
      this.translitChars.put("ȋ", "i");
      this.translitChars.put("ᵫ", "ue");
      this.translitChars.put("ȡ", "d");
      this.translitChars.put("ⱬ", "z");
      this.translitChars.put("ẁ", "w");
      this.translitChars.put("ᶏ", "a");
      this.translitChars.put("ꞇ", "t");
      this.translitChars.put("ğ", "g");
      this.translitChars.put("ɳ", "n");
      this.translitChars.put("ʛ", "g");
      this.translitChars.put("ᴜ", "u");
      this.translitChars.put("ф", "f");
      this.translitChars.put("ẩ", "a");
      this.translitChars.put("ṅ", "n");
      this.translitChars.put("ɨ", "i");
      this.translitChars.put("ᴙ", "r");
      this.translitChars.put("ǎ", "a");
      this.translitChars.put("ſ", "s");
      this.translitChars.put("у", "u");
      this.translitChars.put("ȫ", "o");
      this.translitChars.put("ɿ", "r");
      this.translitChars.put("ƭ", "t");
      this.translitChars.put("ḯ", "i");
      this.translitChars.put("ǽ", "ae");
      this.translitChars.put("ⱱ", "v");
      this.translitChars.put("ɶ", "oe");
      this.translitChars.put("ṃ", "m");
      this.translitChars.put("ż", "z");
      this.translitChars.put("ĕ", "e");
      this.translitChars.put("ꜻ", "av");
      this.translitChars.put("ở", "o");
      this.translitChars.put("ễ", "e");
      this.translitChars.put("ɬ", "l");
      this.translitChars.put("ị", "i");
      this.translitChars.put("ᵭ", "d");
      this.translitChars.put("ﬆ", "st");
      this.translitChars.put("ḷ", "l");
      this.translitChars.put("ŕ", "r");
      this.translitChars.put("ᴕ", "ou");
      this.translitChars.put("ʈ", "t");
      this.translitChars.put("ā", "a");
      this.translitChars.put("э", "e");
      this.translitChars.put("ḙ", "e");
      this.translitChars.put("ᴑ", "o");
      this.translitChars.put("ç", "c");
      this.translitChars.put("ᶊ", "s");
      this.translitChars.put("ặ", "a");
      this.translitChars.put("ų", "u");
      this.translitChars.put("ả", "a");
      this.translitChars.put("ǥ", "g");
      this.translitChars.put("р", "r");
      this.translitChars.put("ꝁ", "k");
      this.translitChars.put("ẕ", "z");
      this.translitChars.put("ŝ", "s");
      this.translitChars.put("ḕ", "e");
      this.translitChars.put("ɠ", "g");
      this.translitChars.put("ꝉ", "l");
      this.translitChars.put("ꝼ", "f");
      this.translitChars.put("ᶍ", "x");
      this.translitChars.put("х", "h");
      this.translitChars.put("ǒ", "o");
      this.translitChars.put("ę", "e");
      this.translitChars.put("ổ", "o");
      this.translitChars.put("ƫ", "t");
      this.translitChars.put("ǫ", "o");
      this.translitChars.put("i̇", "i");
      this.translitChars.put("ṇ", "n");
      this.translitChars.put("ć", "c");
      this.translitChars.put("ᵷ", "g");
      this.translitChars.put("ẅ", "w");
      this.translitChars.put("ḑ", "d");
      this.translitChars.put("ḹ", "l");
      this.translitChars.put("ч", "ch");
      this.translitChars.put("œ", "oe");
      this.translitChars.put("ᵳ", "r");
      this.translitChars.put("ļ", "l");
      this.translitChars.put("ȑ", "r");
      this.translitChars.put("ȭ", "o");
      this.translitChars.put("ᵰ", "n");
      this.translitChars.put("ᴁ", "ae");
      this.translitChars.put("ŀ", "l");
      this.translitChars.put("ä", "a");
      this.translitChars.put("ƥ", "p");
      this.translitChars.put("ỏ", "o");
      this.translitChars.put("į", "i");
      this.translitChars.put("ȓ", "r");
      this.translitChars.put("ǆ", "dz");
      this.translitChars.put("ḡ", "g");
      this.translitChars.put("ṻ", "u");
      this.translitChars.put("ō", "o");
      this.translitChars.put("ľ", "l");
      this.translitChars.put("ẃ", "w");
      this.translitChars.put("ț", "t");
      this.translitChars.put("ń", "n");
      this.translitChars.put("ɍ", "r");
      this.translitChars.put("ȃ", "a");
      this.translitChars.put("ü", "u");
      this.translitChars.put("ꞁ", "l");
      this.translitChars.put("ᴐ", "o");
      this.translitChars.put("ớ", "o");
      this.translitChars.put("ᴃ", "b");
      this.translitChars.put("ɹ", "r");
      this.translitChars.put("ᵲ", "r");
      this.translitChars.put("ʏ", "y");
      this.translitChars.put("ᵮ", "f");
      this.translitChars.put("ⱨ", "h");
      this.translitChars.put("ŏ", "o");
      this.translitChars.put("ú", "u");
      this.translitChars.put("ṛ", "r");
      this.translitChars.put("ʮ", "h");
      this.translitChars.put("ó", "o");
      this.translitChars.put("ů", "u");
      this.translitChars.put("ỡ", "o");
      this.translitChars.put("ṕ", "p");
      this.translitChars.put("ᶖ", "i");
      this.translitChars.put("ự", "u");
      this.translitChars.put("ã", "a");
      this.translitChars.put("ᵢ", "i");
      this.translitChars.put("ṱ", "t");
      this.translitChars.put("ể", "e");
      this.translitChars.put("ử", "u");
      this.translitChars.put("í", "i");
      this.translitChars.put("ɔ", "o");
      this.translitChars.put("с", "s");
      this.translitChars.put("й", "i");
      this.translitChars.put("ɺ", "r");
      this.translitChars.put("ɢ", "g");
      this.translitChars.put("ř", "r");
      this.translitChars.put("ẖ", "h");
      this.translitChars.put("ű", "u");
      this.translitChars.put("ȍ", "o");
      this.translitChars.put("ш", "sh");
      this.translitChars.put("ḻ", "l");
      this.translitChars.put("ḣ", "h");
      this.translitChars.put("ȶ", "t");
      this.translitChars.put("ņ", "n");
      this.translitChars.put("ᶒ", "e");
      this.translitChars.put("ì", "i");
      this.translitChars.put("ẉ", "w");
      this.translitChars.put("б", "b");
      this.translitChars.put("ē", "e");
      this.translitChars.put("ᴇ", "e");
      this.translitChars.put("ł", "l");
      this.translitChars.put("ộ", "o");
      this.translitChars.put("ɭ", "l");
      this.translitChars.put("ẏ", "y");
      this.translitChars.put("ᴊ", "j");
      this.translitChars.put("ḱ", "k");
      this.translitChars.put("ṿ", "v");
      this.translitChars.put("ȩ", "e");
      this.translitChars.put("â", "a");
      this.translitChars.put("ş", "s");
      this.translitChars.put("ŗ", "r");
      this.translitChars.put("ʋ", "v");
      this.translitChars.put("ₐ", "a");
      this.translitChars.put("ↄ", "c");
      this.translitChars.put("ᶓ", "e");
      this.translitChars.put("ɰ", "m");
      this.translitChars.put("е", "e");
      this.translitChars.put("ᴡ", "w");
      this.translitChars.put("ȏ", "o");
      this.translitChars.put("č", "c");
      this.translitChars.put("ǵ", "g");
      this.translitChars.put("ĉ", "c");
      this.translitChars.put("ю", "yu");
      this.translitChars.put("ᶗ", "o");
      this.translitChars.put("ꝃ", "k");
      this.translitChars.put("ꝙ", "q");
      this.translitChars.put("г", "g");
      this.translitChars.put("ṑ", "o");
      this.translitChars.put("ꜱ", "s");
      this.translitChars.put("ṓ", "o");
      this.translitChars.put("ȟ", "h");
      this.translitChars.put("ő", "o");
      this.translitChars.put("ꜩ", "tz");
      this.translitChars.put("ẻ", "e");
      this.translitChars.put("о", "o");
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    int j = paramString.length();
    int i = 0;
    if (i < j)
    {
      String str1 = paramString.substring(i, i + 1);
      String str2 = (String)this.translitChars.get(str1);
      if (str2 != null) {
        localStringBuilder.append(str2);
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append(str1);
      }
    }
    return localStringBuilder.toString();
  }
  
  public void onDeviceConfigurationChange(Configuration paramConfiguration)
  {
    if (this.changingConfiguration) {}
    do
    {
      do
      {
        return;
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        this.systemDefaultLocale = paramConfiguration.locale;
        if (this.languageOverride != null)
        {
          paramConfiguration = this.currentLocaleInfo;
          this.currentLocaleInfo = null;
          applyLanguage(paramConfiguration, false);
          return;
        }
        paramConfiguration = paramConfiguration.locale;
      } while (paramConfiguration == null);
      String str1 = paramConfiguration.getDisplayName();
      String str2 = this.currentLocale.getDisplayName();
      if ((str1 != null) && (str2 != null) && (!str1.equals(str2))) {
        recreateFormatters();
      }
      this.currentLocale = paramConfiguration;
      this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
    } while (this.currentPluralRules != null);
    this.currentPluralRules = ((PluralRules)this.allRules.get("en"));
  }
  
  public void recreateFormatters()
  {
    Object localObject2 = this.currentLocale;
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Locale.getDefault();
    }
    localObject2 = ((Locale)localObject1).getLanguage();
    Object localObject3 = localObject2;
    if (localObject2 == null) {
      localObject3 = "en";
    }
    isRTL = ((String)localObject3).toLowerCase().equals("ar");
    int i;
    if (((String)localObject3).toLowerCase().equals("ko"))
    {
      i = 2;
      nameDisplayOrder = i;
      this.formatterMonth = createFormatter((Locale)localObject1, getStringInternal("formatterMonth", 2131166438), "dd MMM");
      this.formatterYear = createFormatter((Locale)localObject1, getStringInternal("formatterYear", 2131166441), "dd.MM.yy");
      this.formatterYearMax = createFormatter((Locale)localObject1, getStringInternal("formatterYearMax", 2131166442), "dd.MM.yyyy");
      this.chatDate = createFormatter((Locale)localObject1, getStringInternal("chatDate", 2131166433), "d MMMM");
      this.chatFullDate = createFormatter((Locale)localObject1, getStringInternal("chatFullDate", 2131166434), "d MMMM yyyy");
      this.formatterWeek = createFormatter((Locale)localObject1, getStringInternal("formatterWeek", 2131166440), "EEE");
      this.formatterMonthYear = createFormatter((Locale)localObject1, getStringInternal("formatterMonthYear", 2131166439), "MMMM yyyy");
      localObject2 = localObject1;
      if (!((String)localObject3).toLowerCase().equals("ar"))
      {
        if (!((String)localObject3).toLowerCase().equals("ko")) {
          break label297;
        }
        localObject2 = localObject1;
      }
      label251:
      if (!is24HourFormat) {
        break label304;
      }
      localObject1 = getStringInternal("formatterDay24H", 2131166437);
      label268:
      if (!is24HourFormat) {
        break label318;
      }
    }
    label297:
    label304:
    label318:
    for (localObject3 = "HH:mm";; localObject3 = "h:mm a")
    {
      this.formatterDay = createFormatter((Locale)localObject2, (String)localObject1, (String)localObject3);
      return;
      i = 1;
      break;
      localObject2 = Locale.US;
      break label251;
      localObject1 = getStringInternal("formatterDay12H", 2131166436);
      break label268;
    }
  }
  
  public static class LocaleInfo
  {
    public String name;
    public String nameEnglish;
    public String pathToFile;
    public String shortName;
    
    public static LocaleInfo createWithString(String paramString)
    {
      if ((paramString == null) || (paramString.length() == 0)) {}
      do
      {
        return null;
        paramString = paramString.split("\\|");
      } while (paramString.length != 4);
      LocaleInfo localLocaleInfo = new LocaleInfo();
      localLocaleInfo.name = paramString[0];
      localLocaleInfo.nameEnglish = paramString[1];
      localLocaleInfo.shortName = paramString[2];
      localLocaleInfo.pathToFile = paramString[3];
      return localLocaleInfo;
    }
    
    public String getSaveString()
    {
      return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile;
    }
  }
  
  public static abstract class PluralRules
  {
    abstract int quantityForNumber(int paramInt);
  }
  
  public static class PluralRules_Arabic
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 0) {
        return 1;
      }
      if (paramInt == 1) {
        return 2;
      }
      if (paramInt == 2) {
        return 4;
      }
      if ((i >= 3) && (i <= 10)) {
        return 8;
      }
      if ((i >= 11) && (i <= 99)) {
        return 16;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Balkan
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      paramInt %= 10;
      if ((paramInt == 1) && (i != 11)) {
        return 2;
      }
      if ((paramInt >= 2) && (paramInt <= 4) && ((i < 12) || (i > 14))) {
        return 8;
      }
      if ((paramInt == 0) || ((paramInt >= 5) && (paramInt <= 9)) || ((i >= 11) && (i <= 14))) {
        return 16;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Breton
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0) {
        return 1;
      }
      if (paramInt == 1) {
        return 2;
      }
      if (paramInt == 2) {
        return 4;
      }
      if (paramInt == 3) {
        return 8;
      }
      if (paramInt == 6) {
        return 16;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Czech
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1) {
        return 2;
      }
      if ((paramInt >= 2) && (paramInt <= 4)) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_French
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 2)) {
        return 2;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Langi
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if (paramInt == 0) {
        i = 1;
      }
      while ((paramInt > 0) && (paramInt < 2)) {
        return i;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Latvian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0) {
        return 1;
      }
      if ((paramInt % 10 == 1) && (paramInt % 100 != 11)) {
        return 2;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Lithuanian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      paramInt %= 10;
      if ((paramInt == 1) && ((i < 11) || (i > 19))) {
        return 2;
      }
      if ((paramInt >= 2) && (paramInt <= 9) && ((i < 11) || (i > 19))) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Macedonian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt % 10 == 1) && (paramInt != 11)) {
        return 2;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Maltese
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 1) {
        return 2;
      }
      if ((paramInt == 0) || ((i >= 2) && (i <= 10))) {
        return 8;
      }
      if ((i >= 11) && (i <= 19)) {
        return 16;
      }
      return 0;
    }
  }
  
  public static class PluralRules_None
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      return 0;
    }
  }
  
  public static class PluralRules_One
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1) {
        return 2;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Polish
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      int j = paramInt % 10;
      if (paramInt == 1) {
        return 2;
      }
      if ((j >= 2) && (j <= 4) && ((i < 12) || (i > 14)) && ((i < 22) || (i > 24))) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Romanian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 1) {
        return 2;
      }
      if ((paramInt == 0) || ((i >= 1) && (i <= 19))) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Slovenian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      paramInt %= 100;
      if (paramInt == 1) {
        return 2;
      }
      if (paramInt == 2) {
        return 4;
      }
      if ((paramInt >= 3) && (paramInt <= 4)) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Tachelhit
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt <= 1)) {
        return 2;
      }
      if ((paramInt >= 2) && (paramInt <= 10)) {
        return 8;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Two
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1) {
        return 2;
      }
      if (paramInt == 2) {
        return 4;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Welsh
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0) {
        return 1;
      }
      if (paramInt == 1) {
        return 2;
      }
      if (paramInt == 2) {
        return 4;
      }
      if (paramInt == 3) {
        return 8;
      }
      if (paramInt == 6) {
        return 16;
      }
      return 0;
    }
  }
  
  public static class PluralRules_Zero
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt == 0) || (paramInt == 1)) {
        return 2;
      }
      return 0;
    }
  }
  
  private class TimeZoneChangedReceiver
    extends BroadcastReceiver
  {
    private TimeZoneChangedReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ApplicationLoader.applicationHandler.post(new Runnable()
      {
        public void run()
        {
          if (!LocaleController.this.formatterMonth.getTimeZone().equals(TimeZone.getDefault())) {
            LocaleController.getInstance().recreateFormatters();
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/LocaleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */