package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
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
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.Vector;

public class LocaleController
{
  private static volatile LocaleController Instance = null;
  static final int QUANTITY_FEW = 8;
  static final int QUANTITY_MANY = 16;
  static final int QUANTITY_ONE = 2;
  static final int QUANTITY_OTHER = 0;
  static final int QUANTITY_TWO = 4;
  static final int QUANTITY_ZERO = 1;
  public static boolean is24HourFormat;
  public static boolean isRTL = false;
  public static int nameDisplayOrder = 1;
  private HashMap<String, PluralRules> allRules;
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
  private HashMap<String, String> localeValues;
  private ArrayList<LocaleInfo> otherLanguages;
  private boolean reloadLastFile;
  public ArrayList<LocaleInfo> remoteLanguages;
  private Locale systemDefaultLocale;
  private HashMap<String, String> translitChars;
  
  static
  {
    is24HourFormat = false;
  }
  
  /* Error */
  public LocaleController()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 165	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 167	java/util/HashMap
    //   8: dup
    //   9: invokespecial 168	java/util/HashMap:<init>	()V
    //   12: putfield 170	org/telegram/messenger/LocaleController:allRules	Ljava/util/HashMap;
    //   15: aload_0
    //   16: new 167	java/util/HashMap
    //   19: dup
    //   20: invokespecial 168	java/util/HashMap:<init>	()V
    //   23: putfield 172	org/telegram/messenger/LocaleController:localeValues	Ljava/util/HashMap;
    //   26: aload_0
    //   27: iconst_0
    //   28: putfield 174	org/telegram/messenger/LocaleController:changingConfiguration	Z
    //   31: aload_0
    //   32: new 176	java/util/ArrayList
    //   35: dup
    //   36: invokespecial 177	java/util/ArrayList:<init>	()V
    //   39: putfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   42: aload_0
    //   43: new 176	java/util/ArrayList
    //   46: dup
    //   47: invokespecial 177	java/util/ArrayList:<init>	()V
    //   50: putfield 181	org/telegram/messenger/LocaleController:remoteLanguages	Ljava/util/ArrayList;
    //   53: aload_0
    //   54: new 167	java/util/HashMap
    //   57: dup
    //   58: invokespecial 168	java/util/HashMap:<init>	()V
    //   61: putfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   64: aload_0
    //   65: new 176	java/util/ArrayList
    //   68: dup
    //   69: invokespecial 177	java/util/ArrayList:<init>	()V
    //   72: putfield 185	org/telegram/messenger/LocaleController:otherLanguages	Ljava/util/ArrayList;
    //   75: new 65	org/telegram/messenger/LocaleController$PluralRules_One
    //   78: dup
    //   79: invokespecial 186	org/telegram/messenger/LocaleController$PluralRules_One:<init>	()V
    //   82: astore_1
    //   83: aload_0
    //   84: bipush 56
    //   86: anewarray 188	java/lang/String
    //   89: dup
    //   90: iconst_0
    //   91: ldc -66
    //   93: aastore
    //   94: dup
    //   95: iconst_1
    //   96: ldc -64
    //   98: aastore
    //   99: dup
    //   100: iconst_2
    //   101: ldc -62
    //   103: aastore
    //   104: dup
    //   105: iconst_3
    //   106: ldc -60
    //   108: aastore
    //   109: dup
    //   110: iconst_4
    //   111: ldc -58
    //   113: aastore
    //   114: dup
    //   115: iconst_5
    //   116: ldc -56
    //   118: aastore
    //   119: dup
    //   120: bipush 6
    //   122: ldc -54
    //   124: aastore
    //   125: dup
    //   126: bipush 7
    //   128: ldc -52
    //   130: aastore
    //   131: dup
    //   132: bipush 8
    //   134: ldc -50
    //   136: aastore
    //   137: dup
    //   138: bipush 9
    //   140: ldc -48
    //   142: aastore
    //   143: dup
    //   144: bipush 10
    //   146: ldc -46
    //   148: aastore
    //   149: dup
    //   150: bipush 11
    //   152: ldc -44
    //   154: aastore
    //   155: dup
    //   156: bipush 12
    //   158: ldc -42
    //   160: aastore
    //   161: dup
    //   162: bipush 13
    //   164: ldc -40
    //   166: aastore
    //   167: dup
    //   168: bipush 14
    //   170: ldc -38
    //   172: aastore
    //   173: dup
    //   174: bipush 15
    //   176: ldc -36
    //   178: aastore
    //   179: dup
    //   180: bipush 16
    //   182: ldc -34
    //   184: aastore
    //   185: dup
    //   186: bipush 17
    //   188: ldc -32
    //   190: aastore
    //   191: dup
    //   192: bipush 18
    //   194: ldc -30
    //   196: aastore
    //   197: dup
    //   198: bipush 19
    //   200: ldc -28
    //   202: aastore
    //   203: dup
    //   204: bipush 20
    //   206: ldc -26
    //   208: aastore
    //   209: dup
    //   210: bipush 21
    //   212: ldc -24
    //   214: aastore
    //   215: dup
    //   216: bipush 22
    //   218: ldc -22
    //   220: aastore
    //   221: dup
    //   222: bipush 23
    //   224: ldc -20
    //   226: aastore
    //   227: dup
    //   228: bipush 24
    //   230: ldc -18
    //   232: aastore
    //   233: dup
    //   234: bipush 25
    //   236: ldc -16
    //   238: aastore
    //   239: dup
    //   240: bipush 26
    //   242: ldc -14
    //   244: aastore
    //   245: dup
    //   246: bipush 27
    //   248: ldc -12
    //   250: aastore
    //   251: dup
    //   252: bipush 28
    //   254: ldc -10
    //   256: aastore
    //   257: dup
    //   258: bipush 29
    //   260: ldc -8
    //   262: aastore
    //   263: dup
    //   264: bipush 30
    //   266: ldc -6
    //   268: aastore
    //   269: dup
    //   270: bipush 31
    //   272: ldc -4
    //   274: aastore
    //   275: dup
    //   276: bipush 32
    //   278: ldc -2
    //   280: aastore
    //   281: dup
    //   282: bipush 33
    //   284: ldc_w 256
    //   287: aastore
    //   288: dup
    //   289: bipush 34
    //   291: ldc_w 258
    //   294: aastore
    //   295: dup
    //   296: bipush 35
    //   298: ldc_w 260
    //   301: aastore
    //   302: dup
    //   303: bipush 36
    //   305: ldc_w 262
    //   308: aastore
    //   309: dup
    //   310: bipush 37
    //   312: ldc_w 264
    //   315: aastore
    //   316: dup
    //   317: bipush 38
    //   319: ldc_w 266
    //   322: aastore
    //   323: dup
    //   324: bipush 39
    //   326: ldc_w 268
    //   329: aastore
    //   330: dup
    //   331: bipush 40
    //   333: ldc_w 270
    //   336: aastore
    //   337: dup
    //   338: bipush 41
    //   340: ldc_w 272
    //   343: aastore
    //   344: dup
    //   345: bipush 42
    //   347: ldc_w 274
    //   350: aastore
    //   351: dup
    //   352: bipush 43
    //   354: ldc_w 276
    //   357: aastore
    //   358: dup
    //   359: bipush 44
    //   361: ldc_w 278
    //   364: aastore
    //   365: dup
    //   366: bipush 45
    //   368: ldc_w 280
    //   371: aastore
    //   372: dup
    //   373: bipush 46
    //   375: ldc_w 282
    //   378: aastore
    //   379: dup
    //   380: bipush 47
    //   382: ldc_w 284
    //   385: aastore
    //   386: dup
    //   387: bipush 48
    //   389: ldc_w 286
    //   392: aastore
    //   393: dup
    //   394: bipush 49
    //   396: ldc_w 288
    //   399: aastore
    //   400: dup
    //   401: bipush 50
    //   403: ldc_w 290
    //   406: aastore
    //   407: dup
    //   408: bipush 51
    //   410: ldc_w 292
    //   413: aastore
    //   414: dup
    //   415: bipush 52
    //   417: ldc_w 294
    //   420: aastore
    //   421: dup
    //   422: bipush 53
    //   424: ldc_w 296
    //   427: aastore
    //   428: dup
    //   429: bipush 54
    //   431: ldc_w 298
    //   434: aastore
    //   435: dup
    //   436: bipush 55
    //   438: ldc_w 300
    //   441: aastore
    //   442: aload_1
    //   443: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   446: new 41	org/telegram/messenger/LocaleController$PluralRules_Czech
    //   449: dup
    //   450: invokespecial 305	org/telegram/messenger/LocaleController$PluralRules_Czech:<init>	()V
    //   453: astore_1
    //   454: aload_0
    //   455: iconst_2
    //   456: anewarray 188	java/lang/String
    //   459: dup
    //   460: iconst_0
    //   461: ldc_w 307
    //   464: aastore
    //   465: dup
    //   466: iconst_1
    //   467: ldc_w 309
    //   470: aastore
    //   471: aload_1
    //   472: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   475: new 44	org/telegram/messenger/LocaleController$PluralRules_French
    //   478: dup
    //   479: invokespecial 310	org/telegram/messenger/LocaleController$PluralRules_French:<init>	()V
    //   482: astore_1
    //   483: aload_0
    //   484: iconst_3
    //   485: anewarray 188	java/lang/String
    //   488: dup
    //   489: iconst_0
    //   490: ldc_w 312
    //   493: aastore
    //   494: dup
    //   495: iconst_1
    //   496: ldc_w 314
    //   499: aastore
    //   500: dup
    //   501: iconst_2
    //   502: ldc_w 316
    //   505: aastore
    //   506: aload_1
    //   507: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   510: new 35	org/telegram/messenger/LocaleController$PluralRules_Balkan
    //   513: dup
    //   514: invokespecial 317	org/telegram/messenger/LocaleController$PluralRules_Balkan:<init>	()V
    //   517: astore_1
    //   518: aload_0
    //   519: bipush 7
    //   521: anewarray 188	java/lang/String
    //   524: dup
    //   525: iconst_0
    //   526: ldc_w 319
    //   529: aastore
    //   530: dup
    //   531: iconst_1
    //   532: ldc_w 321
    //   535: aastore
    //   536: dup
    //   537: iconst_2
    //   538: ldc_w 323
    //   541: aastore
    //   542: dup
    //   543: iconst_3
    //   544: ldc_w 325
    //   547: aastore
    //   548: dup
    //   549: iconst_4
    //   550: ldc_w 327
    //   553: aastore
    //   554: dup
    //   555: iconst_5
    //   556: ldc_w 329
    //   559: aastore
    //   560: dup
    //   561: bipush 6
    //   563: ldc_w 331
    //   566: aastore
    //   567: aload_1
    //   568: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   571: new 50	org/telegram/messenger/LocaleController$PluralRules_Latvian
    //   574: dup
    //   575: invokespecial 332	org/telegram/messenger/LocaleController$PluralRules_Latvian:<init>	()V
    //   578: astore_1
    //   579: aload_0
    //   580: iconst_1
    //   581: anewarray 188	java/lang/String
    //   584: dup
    //   585: iconst_0
    //   586: ldc_w 334
    //   589: aastore
    //   590: aload_1
    //   591: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   594: new 53	org/telegram/messenger/LocaleController$PluralRules_Lithuanian
    //   597: dup
    //   598: invokespecial 335	org/telegram/messenger/LocaleController$PluralRules_Lithuanian:<init>	()V
    //   601: astore_1
    //   602: aload_0
    //   603: iconst_1
    //   604: anewarray 188	java/lang/String
    //   607: dup
    //   608: iconst_0
    //   609: ldc_w 337
    //   612: aastore
    //   613: aload_1
    //   614: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   617: new 68	org/telegram/messenger/LocaleController$PluralRules_Polish
    //   620: dup
    //   621: invokespecial 338	org/telegram/messenger/LocaleController$PluralRules_Polish:<init>	()V
    //   624: astore_1
    //   625: aload_0
    //   626: iconst_1
    //   627: anewarray 188	java/lang/String
    //   630: dup
    //   631: iconst_0
    //   632: ldc_w 340
    //   635: aastore
    //   636: aload_1
    //   637: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   640: new 71	org/telegram/messenger/LocaleController$PluralRules_Romanian
    //   643: dup
    //   644: invokespecial 341	org/telegram/messenger/LocaleController$PluralRules_Romanian:<init>	()V
    //   647: astore_1
    //   648: aload_0
    //   649: iconst_2
    //   650: anewarray 188	java/lang/String
    //   653: dup
    //   654: iconst_0
    //   655: ldc_w 343
    //   658: aastore
    //   659: dup
    //   660: iconst_1
    //   661: ldc_w 345
    //   664: aastore
    //   665: aload_1
    //   666: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   669: new 74	org/telegram/messenger/LocaleController$PluralRules_Slovenian
    //   672: dup
    //   673: invokespecial 346	org/telegram/messenger/LocaleController$PluralRules_Slovenian:<init>	()V
    //   676: astore_1
    //   677: aload_0
    //   678: iconst_1
    //   679: anewarray 188	java/lang/String
    //   682: dup
    //   683: iconst_0
    //   684: ldc_w 348
    //   687: aastore
    //   688: aload_1
    //   689: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   692: new 32	org/telegram/messenger/LocaleController$PluralRules_Arabic
    //   695: dup
    //   696: invokespecial 349	org/telegram/messenger/LocaleController$PluralRules_Arabic:<init>	()V
    //   699: astore_1
    //   700: aload_0
    //   701: iconst_1
    //   702: anewarray 188	java/lang/String
    //   705: dup
    //   706: iconst_0
    //   707: ldc_w 351
    //   710: aastore
    //   711: aload_1
    //   712: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   715: new 56	org/telegram/messenger/LocaleController$PluralRules_Macedonian
    //   718: dup
    //   719: invokespecial 352	org/telegram/messenger/LocaleController$PluralRules_Macedonian:<init>	()V
    //   722: astore_1
    //   723: aload_0
    //   724: iconst_1
    //   725: anewarray 188	java/lang/String
    //   728: dup
    //   729: iconst_0
    //   730: ldc_w 354
    //   733: aastore
    //   734: aload_1
    //   735: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   738: new 83	org/telegram/messenger/LocaleController$PluralRules_Welsh
    //   741: dup
    //   742: invokespecial 355	org/telegram/messenger/LocaleController$PluralRules_Welsh:<init>	()V
    //   745: astore_1
    //   746: aload_0
    //   747: iconst_1
    //   748: anewarray 188	java/lang/String
    //   751: dup
    //   752: iconst_0
    //   753: ldc_w 357
    //   756: aastore
    //   757: aload_1
    //   758: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   761: new 38	org/telegram/messenger/LocaleController$PluralRules_Breton
    //   764: dup
    //   765: invokespecial 358	org/telegram/messenger/LocaleController$PluralRules_Breton:<init>	()V
    //   768: astore_1
    //   769: aload_0
    //   770: iconst_1
    //   771: anewarray 188	java/lang/String
    //   774: dup
    //   775: iconst_0
    //   776: ldc_w 360
    //   779: aastore
    //   780: aload_1
    //   781: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   784: new 47	org/telegram/messenger/LocaleController$PluralRules_Langi
    //   787: dup
    //   788: invokespecial 361	org/telegram/messenger/LocaleController$PluralRules_Langi:<init>	()V
    //   791: astore_1
    //   792: aload_0
    //   793: iconst_1
    //   794: anewarray 188	java/lang/String
    //   797: dup
    //   798: iconst_0
    //   799: ldc_w 363
    //   802: aastore
    //   803: aload_1
    //   804: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   807: new 77	org/telegram/messenger/LocaleController$PluralRules_Tachelhit
    //   810: dup
    //   811: invokespecial 364	org/telegram/messenger/LocaleController$PluralRules_Tachelhit:<init>	()V
    //   814: astore_1
    //   815: aload_0
    //   816: iconst_1
    //   817: anewarray 188	java/lang/String
    //   820: dup
    //   821: iconst_0
    //   822: ldc_w 366
    //   825: aastore
    //   826: aload_1
    //   827: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   830: new 59	org/telegram/messenger/LocaleController$PluralRules_Maltese
    //   833: dup
    //   834: invokespecial 367	org/telegram/messenger/LocaleController$PluralRules_Maltese:<init>	()V
    //   837: astore_1
    //   838: aload_0
    //   839: iconst_1
    //   840: anewarray 188	java/lang/String
    //   843: dup
    //   844: iconst_0
    //   845: ldc_w 369
    //   848: aastore
    //   849: aload_1
    //   850: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   853: new 80	org/telegram/messenger/LocaleController$PluralRules_Two
    //   856: dup
    //   857: invokespecial 370	org/telegram/messenger/LocaleController$PluralRules_Two:<init>	()V
    //   860: astore_1
    //   861: aload_0
    //   862: bipush 7
    //   864: anewarray 188	java/lang/String
    //   867: dup
    //   868: iconst_0
    //   869: ldc_w 372
    //   872: aastore
    //   873: dup
    //   874: iconst_1
    //   875: ldc_w 374
    //   878: aastore
    //   879: dup
    //   880: iconst_2
    //   881: ldc_w 376
    //   884: aastore
    //   885: dup
    //   886: iconst_3
    //   887: ldc_w 378
    //   890: aastore
    //   891: dup
    //   892: iconst_4
    //   893: ldc_w 380
    //   896: aastore
    //   897: dup
    //   898: iconst_5
    //   899: ldc_w 382
    //   902: aastore
    //   903: dup
    //   904: bipush 6
    //   906: ldc_w 384
    //   909: aastore
    //   910: aload_1
    //   911: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   914: new 86	org/telegram/messenger/LocaleController$PluralRules_Zero
    //   917: dup
    //   918: invokespecial 385	org/telegram/messenger/LocaleController$PluralRules_Zero:<init>	()V
    //   921: astore_1
    //   922: aload_0
    //   923: bipush 12
    //   925: anewarray 188	java/lang/String
    //   928: dup
    //   929: iconst_0
    //   930: ldc_w 387
    //   933: aastore
    //   934: dup
    //   935: iconst_1
    //   936: ldc_w 389
    //   939: aastore
    //   940: dup
    //   941: iconst_2
    //   942: ldc_w 391
    //   945: aastore
    //   946: dup
    //   947: iconst_3
    //   948: ldc_w 393
    //   951: aastore
    //   952: dup
    //   953: iconst_4
    //   954: ldc_w 395
    //   957: aastore
    //   958: dup
    //   959: iconst_5
    //   960: ldc_w 397
    //   963: aastore
    //   964: dup
    //   965: bipush 6
    //   967: ldc_w 399
    //   970: aastore
    //   971: dup
    //   972: bipush 7
    //   974: ldc_w 401
    //   977: aastore
    //   978: dup
    //   979: bipush 8
    //   981: ldc_w 403
    //   984: aastore
    //   985: dup
    //   986: bipush 9
    //   988: ldc_w 405
    //   991: aastore
    //   992: dup
    //   993: bipush 10
    //   995: ldc_w 407
    //   998: aastore
    //   999: dup
    //   1000: bipush 11
    //   1002: ldc_w 409
    //   1005: aastore
    //   1006: aload_1
    //   1007: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   1010: new 62	org/telegram/messenger/LocaleController$PluralRules_None
    //   1013: dup
    //   1014: invokespecial 410	org/telegram/messenger/LocaleController$PluralRules_None:<init>	()V
    //   1017: astore_1
    //   1018: aload_0
    //   1019: bipush 29
    //   1021: anewarray 188	java/lang/String
    //   1024: dup
    //   1025: iconst_0
    //   1026: ldc_w 412
    //   1029: aastore
    //   1030: dup
    //   1031: iconst_1
    //   1032: ldc_w 414
    //   1035: aastore
    //   1036: dup
    //   1037: iconst_2
    //   1038: ldc_w 416
    //   1041: aastore
    //   1042: dup
    //   1043: iconst_3
    //   1044: ldc_w 418
    //   1047: aastore
    //   1048: dup
    //   1049: iconst_4
    //   1050: ldc_w 420
    //   1053: aastore
    //   1054: dup
    //   1055: iconst_5
    //   1056: ldc_w 422
    //   1059: aastore
    //   1060: dup
    //   1061: bipush 6
    //   1063: ldc_w 424
    //   1066: aastore
    //   1067: dup
    //   1068: bipush 7
    //   1070: ldc_w 426
    //   1073: aastore
    //   1074: dup
    //   1075: bipush 8
    //   1077: ldc_w 428
    //   1080: aastore
    //   1081: dup
    //   1082: bipush 9
    //   1084: ldc_w 430
    //   1087: aastore
    //   1088: dup
    //   1089: bipush 10
    //   1091: ldc_w 432
    //   1094: aastore
    //   1095: dup
    //   1096: bipush 11
    //   1098: ldc_w 434
    //   1101: aastore
    //   1102: dup
    //   1103: bipush 12
    //   1105: ldc_w 436
    //   1108: aastore
    //   1109: dup
    //   1110: bipush 13
    //   1112: ldc_w 438
    //   1115: aastore
    //   1116: dup
    //   1117: bipush 14
    //   1119: ldc_w 440
    //   1122: aastore
    //   1123: dup
    //   1124: bipush 15
    //   1126: ldc_w 442
    //   1129: aastore
    //   1130: dup
    //   1131: bipush 16
    //   1133: ldc_w 444
    //   1136: aastore
    //   1137: dup
    //   1138: bipush 17
    //   1140: ldc_w 446
    //   1143: aastore
    //   1144: dup
    //   1145: bipush 18
    //   1147: ldc_w 448
    //   1150: aastore
    //   1151: dup
    //   1152: bipush 19
    //   1154: ldc_w 450
    //   1157: aastore
    //   1158: dup
    //   1159: bipush 20
    //   1161: ldc_w 452
    //   1164: aastore
    //   1165: dup
    //   1166: bipush 21
    //   1168: ldc_w 454
    //   1171: aastore
    //   1172: dup
    //   1173: bipush 22
    //   1175: ldc_w 456
    //   1178: aastore
    //   1179: dup
    //   1180: bipush 23
    //   1182: ldc_w 458
    //   1185: aastore
    //   1186: dup
    //   1187: bipush 24
    //   1189: ldc_w 460
    //   1192: aastore
    //   1193: dup
    //   1194: bipush 25
    //   1196: ldc_w 462
    //   1199: aastore
    //   1200: dup
    //   1201: bipush 26
    //   1203: ldc_w 464
    //   1206: aastore
    //   1207: dup
    //   1208: bipush 27
    //   1210: ldc_w 466
    //   1213: aastore
    //   1214: dup
    //   1215: bipush 28
    //   1217: ldc_w 468
    //   1220: aastore
    //   1221: aload_1
    //   1222: invokespecial 304	org/telegram/messenger/LocaleController:addRules	([Ljava/lang/String;Lorg/telegram/messenger/LocaleController$PluralRules;)V
    //   1225: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1228: dup
    //   1229: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1232: astore_1
    //   1233: aload_1
    //   1234: ldc_w 471
    //   1237: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1240: aload_1
    //   1241: ldc_w 471
    //   1244: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1247: aload_1
    //   1248: ldc -56
    //   1250: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1253: aload_1
    //   1254: aconst_null
    //   1255: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1258: aload_1
    //   1259: iconst_1
    //   1260: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1263: aload_0
    //   1264: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1267: aload_1
    //   1268: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1271: pop
    //   1272: aload_0
    //   1273: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1276: aload_1
    //   1277: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1280: aload_1
    //   1281: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1284: pop
    //   1285: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1288: dup
    //   1289: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1292: astore_1
    //   1293: aload_1
    //   1294: ldc_w 496
    //   1297: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1300: aload_1
    //   1301: ldc_w 498
    //   1304: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1307: aload_1
    //   1308: ldc -38
    //   1310: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1313: aload_1
    //   1314: aconst_null
    //   1315: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1318: aload_1
    //   1319: iconst_1
    //   1320: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1323: aload_0
    //   1324: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1327: aload_1
    //   1328: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1331: pop
    //   1332: aload_0
    //   1333: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1336: aload_1
    //   1337: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1340: aload_1
    //   1341: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1344: pop
    //   1345: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1348: dup
    //   1349: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1352: astore_1
    //   1353: aload_1
    //   1354: ldc_w 500
    //   1357: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1360: aload_1
    //   1361: ldc_w 502
    //   1364: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1367: aload_1
    //   1368: ldc -52
    //   1370: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1373: aload_1
    //   1374: iconst_1
    //   1375: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1378: aload_0
    //   1379: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1382: aload_1
    //   1383: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1386: pop
    //   1387: aload_0
    //   1388: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1391: aload_1
    //   1392: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1395: aload_1
    //   1396: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1399: pop
    //   1400: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1403: dup
    //   1404: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1407: astore_1
    //   1408: aload_1
    //   1409: ldc_w 504
    //   1412: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1415: aload_1
    //   1416: ldc_w 506
    //   1419: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1422: aload_1
    //   1423: ldc -60
    //   1425: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1428: aload_1
    //   1429: aconst_null
    //   1430: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1433: aload_1
    //   1434: iconst_1
    //   1435: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1438: aload_0
    //   1439: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1442: aload_1
    //   1443: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1446: pop
    //   1447: aload_0
    //   1448: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1451: aload_1
    //   1452: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1455: aload_1
    //   1456: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1459: pop
    //   1460: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1463: dup
    //   1464: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1467: astore_1
    //   1468: aload_1
    //   1469: ldc_w 508
    //   1472: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1475: aload_1
    //   1476: ldc_w 510
    //   1479: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1482: aload_1
    //   1483: ldc -34
    //   1485: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1488: aload_1
    //   1489: aconst_null
    //   1490: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1493: aload_1
    //   1494: iconst_1
    //   1495: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1498: aload_0
    //   1499: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1502: aload_1
    //   1503: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1506: pop
    //   1507: aload_0
    //   1508: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1511: aload_1
    //   1512: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1515: aload_1
    //   1516: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1519: pop
    //   1520: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1523: dup
    //   1524: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1527: astore_1
    //   1528: aload_1
    //   1529: ldc_w 512
    //   1532: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1535: aload_1
    //   1536: ldc_w 514
    //   1539: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1542: aload_1
    //   1543: ldc_w 351
    //   1546: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1549: aload_1
    //   1550: aconst_null
    //   1551: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1554: aload_1
    //   1555: iconst_1
    //   1556: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1559: aload_0
    //   1560: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1563: aload_1
    //   1564: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1567: pop
    //   1568: aload_0
    //   1569: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1572: aload_1
    //   1573: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1576: aload_1
    //   1577: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1580: pop
    //   1581: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1584: dup
    //   1585: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1588: astore_1
    //   1589: aload_1
    //   1590: ldc_w 516
    //   1593: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1596: aload_1
    //   1597: ldc_w 518
    //   1600: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1603: aload_1
    //   1604: ldc_w 520
    //   1607: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1610: aload_1
    //   1611: aconst_null
    //   1612: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1615: aload_1
    //   1616: iconst_1
    //   1617: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1620: aload_0
    //   1621: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1624: aload_1
    //   1625: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1628: pop
    //   1629: aload_0
    //   1630: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1633: aload_1
    //   1634: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1637: aload_1
    //   1638: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1641: pop
    //   1642: new 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1645: dup
    //   1646: invokespecial 469	org/telegram/messenger/LocaleController$LocaleInfo:<init>	()V
    //   1649: astore_1
    //   1650: aload_1
    //   1651: ldc_w 522
    //   1654: putfield 474	org/telegram/messenger/LocaleController$LocaleInfo:name	Ljava/lang/String;
    //   1657: aload_1
    //   1658: ldc_w 524
    //   1661: putfield 477	org/telegram/messenger/LocaleController$LocaleInfo:nameEnglish	Ljava/lang/String;
    //   1664: aload_1
    //   1665: ldc_w 428
    //   1668: putfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1671: aload_1
    //   1672: aconst_null
    //   1673: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1676: aload_1
    //   1677: iconst_1
    //   1678: putfield 486	org/telegram/messenger/LocaleController$LocaleInfo:builtIn	Z
    //   1681: aload_0
    //   1682: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1685: aload_1
    //   1686: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1689: pop
    //   1690: aload_0
    //   1691: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1694: aload_1
    //   1695: getfield 480	org/telegram/messenger/LocaleController$LocaleInfo:shortName	Ljava/lang/String;
    //   1698: aload_1
    //   1699: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1702: pop
    //   1703: aload_0
    //   1704: invokespecial 527	org/telegram/messenger/LocaleController:loadOtherLanguages	()V
    //   1707: aload_0
    //   1708: getfield 181	org/telegram/messenger/LocaleController:remoteLanguages	Ljava/util/ArrayList;
    //   1711: invokevirtual 531	java/util/ArrayList:isEmpty	()Z
    //   1714: ifeq +14 -> 1728
    //   1717: new 6	org/telegram/messenger/LocaleController$1
    //   1720: dup
    //   1721: aload_0
    //   1722: invokespecial 534	org/telegram/messenger/LocaleController$1:<init>	(Lorg/telegram/messenger/LocaleController;)V
    //   1725: invokestatic 540	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   1728: iconst_0
    //   1729: istore_2
    //   1730: iload_2
    //   1731: aload_0
    //   1732: getfield 185	org/telegram/messenger/LocaleController:otherLanguages	Ljava/util/ArrayList;
    //   1735: invokevirtual 544	java/util/ArrayList:size	()I
    //   1738: if_icmpge +43 -> 1781
    //   1741: aload_0
    //   1742: getfield 185	org/telegram/messenger/LocaleController:otherLanguages	Ljava/util/ArrayList;
    //   1745: iload_2
    //   1746: invokevirtual 548	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1749: checkcast 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1752: astore_1
    //   1753: aload_0
    //   1754: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1757: aload_1
    //   1758: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1761: pop
    //   1762: aload_0
    //   1763: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1766: aload_1
    //   1767: invokevirtual 552	org/telegram/messenger/LocaleController$LocaleInfo:getKey	()Ljava/lang/String;
    //   1770: aload_1
    //   1771: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1774: pop
    //   1775: iinc 2 1
    //   1778: goto -48 -> 1730
    //   1781: iconst_0
    //   1782: istore_2
    //   1783: iload_2
    //   1784: aload_0
    //   1785: getfield 181	org/telegram/messenger/LocaleController:remoteLanguages	Ljava/util/ArrayList;
    //   1788: invokevirtual 544	java/util/ArrayList:size	()I
    //   1791: if_icmpge +85 -> 1876
    //   1794: aload_0
    //   1795: getfield 181	org/telegram/messenger/LocaleController:remoteLanguages	Ljava/util/ArrayList;
    //   1798: iload_2
    //   1799: invokevirtual 548	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1802: checkcast 26	org/telegram/messenger/LocaleController$LocaleInfo
    //   1805: astore_1
    //   1806: aload_0
    //   1807: aload_1
    //   1808: invokevirtual 552	org/telegram/messenger/LocaleController$LocaleInfo:getKey	()Ljava/lang/String;
    //   1811: invokespecial 556	org/telegram/messenger/LocaleController:getLanguageFromDict	(Ljava/lang/String;)Lorg/telegram/messenger/LocaleController$LocaleInfo;
    //   1814: astore_3
    //   1815: aload_3
    //   1816: ifnull +35 -> 1851
    //   1819: aload_3
    //   1820: aload_1
    //   1821: getfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1824: putfield 483	org/telegram/messenger/LocaleController$LocaleInfo:pathToFile	Ljava/lang/String;
    //   1827: aload_3
    //   1828: aload_1
    //   1829: getfield 559	org/telegram/messenger/LocaleController$LocaleInfo:version	I
    //   1832: putfield 559	org/telegram/messenger/LocaleController$LocaleInfo:version	I
    //   1835: aload_0
    //   1836: getfield 181	org/telegram/messenger/LocaleController:remoteLanguages	Ljava/util/ArrayList;
    //   1839: iload_2
    //   1840: aload_3
    //   1841: invokevirtual 563	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   1844: pop
    //   1845: iinc 2 1
    //   1848: goto -65 -> 1783
    //   1851: aload_0
    //   1852: getfield 179	org/telegram/messenger/LocaleController:languages	Ljava/util/ArrayList;
    //   1855: aload_1
    //   1856: invokevirtual 490	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1859: pop
    //   1860: aload_0
    //   1861: getfield 183	org/telegram/messenger/LocaleController:languagesDict	Ljava/util/HashMap;
    //   1864: aload_1
    //   1865: invokevirtual 552	org/telegram/messenger/LocaleController$LocaleInfo:getKey	()Ljava/lang/String;
    //   1868: aload_1
    //   1869: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1872: pop
    //   1873: goto -28 -> 1845
    //   1876: aload_0
    //   1877: invokestatic 569	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   1880: putfield 571	org/telegram/messenger/LocaleController:systemDefaultLocale	Ljava/util/Locale;
    //   1883: getstatic 577	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1886: invokestatic 582	android/text/format/DateFormat:is24HourFormat	(Landroid/content/Context;)Z
    //   1889: putstatic 157	org/telegram/messenger/LocaleController:is24HourFormat	Z
    //   1892: aconst_null
    //   1893: astore_1
    //   1894: iconst_0
    //   1895: istore 4
    //   1897: invokestatic 588	org/telegram/messenger/MessagesController:getGlobalMainSettings	()Landroid/content/SharedPreferences;
    //   1900: ldc_w 590
    //   1903: aconst_null
    //   1904: invokeinterface 596 3 0
    //   1909: astore_3
    //   1910: iload 4
    //   1912: istore 5
    //   1914: aload_3
    //   1915: ifnull +24 -> 1939
    //   1918: aload_0
    //   1919: aload_3
    //   1920: invokespecial 556	org/telegram/messenger/LocaleController:getLanguageFromDict	(Ljava/lang/String;)Lorg/telegram/messenger/LocaleController$LocaleInfo;
    //   1923: astore_3
    //   1924: aload_3
    //   1925: astore_1
    //   1926: iload 4
    //   1928: istore 5
    //   1930: aload_3
    //   1931: ifnull +8 -> 1939
    //   1934: iconst_1
    //   1935: istore 5
    //   1937: aload_3
    //   1938: astore_1
    //   1939: aload_1
    //   1940: astore_3
    //   1941: aload_1
    //   1942: ifnonnull +27 -> 1969
    //   1945: aload_1
    //   1946: astore_3
    //   1947: aload_0
    //   1948: getfield 571	org/telegram/messenger/LocaleController:systemDefaultLocale	Ljava/util/Locale;
    //   1951: invokevirtual 599	java/util/Locale:getLanguage	()Ljava/lang/String;
    //   1954: ifnull +15 -> 1969
    //   1957: aload_0
    //   1958: aload_0
    //   1959: getfield 571	org/telegram/messenger/LocaleController:systemDefaultLocale	Ljava/util/Locale;
    //   1962: invokevirtual 599	java/util/Locale:getLanguage	()Ljava/lang/String;
    //   1965: invokespecial 556	org/telegram/messenger/LocaleController:getLanguageFromDict	(Ljava/lang/String;)Lorg/telegram/messenger/LocaleController$LocaleInfo;
    //   1968: astore_3
    //   1969: aload_3
    //   1970: astore_1
    //   1971: aload_3
    //   1972: ifnonnull +29 -> 2001
    //   1975: aload_0
    //   1976: aload_0
    //   1977: aload_0
    //   1978: getfield 571	org/telegram/messenger/LocaleController:systemDefaultLocale	Ljava/util/Locale;
    //   1981: invokespecial 603	org/telegram/messenger/LocaleController:getLocaleString	(Ljava/util/Locale;)Ljava/lang/String;
    //   1984: invokespecial 556	org/telegram/messenger/LocaleController:getLanguageFromDict	(Ljava/lang/String;)Lorg/telegram/messenger/LocaleController$LocaleInfo;
    //   1987: astore_3
    //   1988: aload_3
    //   1989: astore_1
    //   1990: aload_3
    //   1991: ifnonnull +10 -> 2001
    //   1994: aload_0
    //   1995: ldc -56
    //   1997: invokespecial 556	org/telegram/messenger/LocaleController:getLanguageFromDict	(Ljava/lang/String;)Lorg/telegram/messenger/LocaleController$LocaleInfo;
    //   2000: astore_1
    //   2001: aload_0
    //   2002: aload_1
    //   2003: iload 5
    //   2005: iconst_1
    //   2006: getstatic 608	org/telegram/messenger/UserConfig:selectedAccount	I
    //   2009: invokevirtual 612	org/telegram/messenger/LocaleController:applyLanguage	(Lorg/telegram/messenger/LocaleController$LocaleInfo;ZZI)V
    //   2012: new 614	android/content/IntentFilter
    //   2015: astore 6
    //   2017: aload 6
    //   2019: ldc_w 616
    //   2022: invokespecial 619	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   2025: getstatic 577	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   2028: astore_1
    //   2029: new 89	org/telegram/messenger/LocaleController$TimeZoneChangedReceiver
    //   2032: astore_3
    //   2033: aload_3
    //   2034: aload_0
    //   2035: aconst_null
    //   2036: invokespecial 622	org/telegram/messenger/LocaleController$TimeZoneChangedReceiver:<init>	(Lorg/telegram/messenger/LocaleController;Lorg/telegram/messenger/LocaleController$1;)V
    //   2039: aload_1
    //   2040: aload_3
    //   2041: aload 6
    //   2043: invokevirtual 628	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   2046: pop
    //   2047: return
    //   2048: astore_1
    //   2049: aload_1
    //   2050: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2053: goto -41 -> 2012
    //   2056: astore_1
    //   2057: aload_1
    //   2058: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2061: goto -14 -> 2047
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2064	0	this	LocaleController
    //   82	1958	1	localObject1	Object
    //   2048	2	1	localException1	Exception
    //   2056	2	1	localException2	Exception
    //   1729	117	2	i	int
    //   1814	227	3	localObject2	Object
    //   1895	32	4	bool1	boolean
    //   1912	92	5	bool2	boolean
    //   2015	27	6	localIntentFilter	android.content.IntentFilter
    // Exception table:
    //   from	to	target	type
    //   1897	1910	2048	java/lang/Exception
    //   1918	1924	2048	java/lang/Exception
    //   1947	1969	2048	java/lang/Exception
    //   1975	1988	2048	java/lang/Exception
    //   1994	2001	2048	java/lang/Exception
    //   2001	2012	2048	java/lang/Exception
    //   2012	2047	2056	java/lang/Exception
  }
  
  public static String addNbsp(String paramString)
  {
    return paramString.replace(' ', ' ');
  }
  
  private void addRules(String[] paramArrayOfString, PluralRules paramPluralRules)
  {
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
    {
      String str = paramArrayOfString[j];
      this.allRules.put(str, paramPluralRules);
    }
  }
  
  private void applyRemoteLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean, final int paramInt)
  {
    if ((paramLocaleInfo == null) || ((paramLocaleInfo != null) && (!paramLocaleInfo.isRemote()))) {}
    for (;;)
    {
      return;
      Object localObject;
      if ((paramLocaleInfo.version != 0) && (!paramBoolean))
      {
        localObject = new TLRPC.TL_langpack_getDifference();
        ((TLRPC.TL_langpack_getDifference)localObject).from_version = paramLocaleInfo.version;
        ConnectionsManager.getInstance(paramInt).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  LocaleController.this.saveRemoteLocaleStrings((TLRPC.TL_langPackDifference)paramAnonymousTLObject, LocaleController.6.this.val$currentAccount);
                }
              });
            }
          }
        }, 8);
      }
      else
      {
        for (int i = 0; i < 3; i++) {
          ConnectionsManager.setLangCode(paramLocaleInfo.shortName);
        }
        localObject = new TLRPC.TL_langpack_getLangPack();
        ((TLRPC.TL_langpack_getLangPack)localObject).lang_code = paramLocaleInfo.shortName.replace("_", "-");
        ConnectionsManager.getInstance(paramInt).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  LocaleController.this.saveRemoteLocaleStrings((TLRPC.TL_langPackDifference)paramAnonymousTLObject, LocaleController.7.this.val$currentAccount);
                }
              });
            }
          }
        }, 8);
      }
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
      paramLocale = paramString1;
    }
    catch (Exception paramString1)
    {
      for (;;)
      {
        paramLocale = FastDateFormat.getInstance(paramString2, paramLocale);
      }
    }
    return paramLocale;
  }
  
  private String escapeString(String paramString)
  {
    if (paramString.contains("[CDATA")) {}
    for (;;)
    {
      return paramString;
      paramString = paramString.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }
  }
  
  public static String formatCallDuration(int paramInt)
  {
    String str2;
    if (paramInt > 3600)
    {
      String str1 = formatPluralString("Hours", paramInt / 3600);
      paramInt = paramInt % 3600 / 60;
      str2 = str1;
      if (paramInt > 0) {
        str2 = str1 + ", " + formatPluralString("Minutes", paramInt);
      }
    }
    for (;;)
    {
      return str2;
      if (paramInt > 60) {
        str2 = formatPluralString("Minutes", paramInt / 60);
      } else {
        str2 = formatPluralString("Seconds", paramInt);
      }
    }
  }
  
  public static String formatDate(long paramLong)
  {
    paramLong *= 1000L;
    for (;;)
    {
      try
      {
        localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k != i) || (j != m)) {
          continue;
        }
        localObject2 = getInstance().formatterDay;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
      }
      catch (Exception localException)
      {
        Object localObject1;
        int i;
        int j;
        int k;
        int m;
        Object localObject2;
        FileLog.e(localException);
        String str = "LOC_ERR: formatDate";
        continue;
      }
      return (String)localObject1;
      if ((k + 1 == i) && (j == m))
      {
        localObject1 = getString("Yesterday", NUM);
      }
      else if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
      {
        localObject1 = getInstance().formatterMonth;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject1).format((Date)localObject2);
      }
      else
      {
        localObject1 = getInstance().formatterYear;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject1).format((Date)localObject2);
      }
    }
  }
  
  public static String formatDateAudio(long paramLong)
  {
    paramLong *= 1000L;
    for (;;)
    {
      try
      {
        localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k != i) || (j != m)) {
          continue;
        }
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("TodayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject1).format((Date)localObject2) });
      }
      catch (Exception localException)
      {
        Object localObject1;
        int i;
        int j;
        int k;
        int m;
        Object localObject2;
        Object localObject3;
        FileLog.e(localException);
        String str = "LOC_ERR";
        continue;
      }
      return (String)localObject1;
      if ((k + 1 == i) && (j == m))
      {
        localObject2 = getInstance().formatterDay;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = formatString("YesterdayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject2).format((Date)localObject1) });
      }
      else if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
      {
        localObject1 = getInstance().formatterMonth;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject2 = ((FastDateFormat)localObject1).format((Date)localObject2);
        localObject1 = getInstance().formatterDay;
        localObject3 = new java/util/Date;
        ((Date)localObject3).<init>(paramLong);
        localObject1 = formatString("formatDateAtTime", NUM, new Object[] { localObject2, ((FastDateFormat)localObject1).format((Date)localObject3) });
      }
      else
      {
        localObject2 = getInstance().formatterYear;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
        localObject3 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("formatDateAtTime", NUM, new Object[] { localObject1, ((FastDateFormat)localObject3).format((Date)localObject2) });
      }
    }
  }
  
  public static String formatDateCallLog(long paramLong)
  {
    paramLong *= 1000L;
    for (;;)
    {
      try
      {
        localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k != i) || (j != m)) {
          continue;
        }
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject1).format((Date)localObject2);
      }
      catch (Exception localException)
      {
        Object localObject1;
        int i;
        int j;
        int k;
        int m;
        Object localObject2;
        Object localObject3;
        FileLog.e(localException);
        String str = "LOC_ERR";
        continue;
      }
      return (String)localObject1;
      if ((k + 1 == i) && (j == m))
      {
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("YesterdayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject1).format((Date)localObject2) });
      }
      else if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
      {
        localObject1 = getInstance().chatDate;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject2 = ((FastDateFormat)localObject1).format((Date)localObject2);
        localObject3 = getInstance().formatterDay;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = formatString("formatDateAtTime", NUM, new Object[] { localObject2, ((FastDateFormat)localObject3).format((Date)localObject1) });
      }
      else
      {
        localObject2 = getInstance().chatFullDate;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject2 = ((FastDateFormat)localObject2).format((Date)localObject1);
        localObject1 = getInstance().formatterDay;
        localObject3 = new java/util/Date;
        ((Date)localObject3).<init>(paramLong);
        localObject1 = formatString("formatDateAtTime", NUM, new Object[] { localObject2, ((FastDateFormat)localObject1).format((Date)localObject3) });
      }
    }
  }
  
  public static String formatDateChat(long paramLong)
  {
    for (;;)
    {
      try
      {
        localObject = Calendar.getInstance();
        paramLong *= 1000L;
        ((Calendar)localObject).setTimeInMillis(paramLong);
        if (Math.abs(System.currentTimeMillis() - paramLong) >= 31536000000L) {
          continue;
        }
        localObject = getInstance().chatDate.format(paramLong);
      }
      catch (Exception localException)
      {
        Object localObject;
        FileLog.e(localException);
        String str = "LOC_ERR: formatDateChat";
        continue;
      }
      return (String)localObject;
      localObject = getInstance().chatFullDate.format(paramLong);
    }
  }
  
  public static String formatDateForBan(long paramLong)
  {
    paramLong *= 1000L;
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(paramLong);
      FastDateFormat localFastDateFormat;
      if (i == ((Calendar)localObject).get(1))
      {
        localFastDateFormat = getInstance().formatterBannedUntilThisYear;
        localObject = new java/util/Date;
        ((Date)localObject).<init>(paramLong);
      }
      for (localObject = localFastDateFormat.format((Date)localObject);; localObject = localFastDateFormat.format((Date)localObject))
      {
        return (String)localObject;
        localFastDateFormat = getInstance().formatterBannedUntil;
        localObject = new java/util/Date;
        ((Date)localObject).<init>(paramLong);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        String str = "LOC_ERR";
      }
    }
  }
  
  public static String formatDateOnline(long paramLong)
  {
    paramLong *= 1000L;
    for (;;)
    {
      try
      {
        localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k != i) || (j != m)) {
          continue;
        }
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("LastSeenFormatted", NUM, new Object[] { formatString("TodayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject1).format((Date)localObject2) }) });
      }
      catch (Exception localException)
      {
        Object localObject1;
        int i;
        int j;
        int k;
        int m;
        Object localObject2;
        Object localObject3;
        FileLog.e(localException);
        String str = "LOC_ERR";
        continue;
      }
      return (String)localObject1;
      if ((k + 1 == i) && (j == m))
      {
        localObject2 = getInstance().formatterDay;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = formatString("LastSeenFormatted", NUM, new Object[] { formatString("YesterdayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject2).format((Date)localObject1) }) });
      }
      else if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
      {
        localObject2 = getInstance().formatterMonth;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject3 = ((FastDateFormat)localObject2).format((Date)localObject1);
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("LastSeenDateFormatted", NUM, new Object[] { formatString("formatDateAtTime", NUM, new Object[] { localObject3, ((FastDateFormat)localObject1).format((Date)localObject2) }) });
      }
      else
      {
        localObject2 = getInstance().formatterYear;
        localObject1 = new java/util/Date;
        ((Date)localObject1).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
        localObject2 = getInstance().formatterDay;
        localObject3 = new java/util/Date;
        ((Date)localObject3).<init>(paramLong);
        localObject1 = formatString("LastSeenDateFormatted", NUM, new Object[] { formatString("formatDateAtTime", NUM, new Object[] { localObject1, ((FastDateFormat)localObject2).format((Date)localObject3) }) });
      }
    }
  }
  
  public static String formatLocationLeftTime(int paramInt)
  {
    int i = 1;
    int j = 1;
    int k = paramInt / 60 / 60;
    paramInt -= k * 60 * 60;
    int m = paramInt / 60;
    paramInt -= m * 60;
    String str;
    if (k != 0) {
      if (m > 30)
      {
        paramInt = j;
        str = String.format("%dh", new Object[] { Integer.valueOf(paramInt + k) });
      }
    }
    for (;;)
    {
      return str;
      paramInt = 0;
      break;
      if (m != 0)
      {
        if (paramInt > 30) {}
        for (paramInt = i;; paramInt = 0)
        {
          str = String.format("%d", new Object[] { Integer.valueOf(paramInt + m) });
          break;
        }
      }
      str = String.format("%d", new Object[] { Integer.valueOf(paramInt) });
    }
  }
  
  public static String formatLocationUpdateDate(long paramLong)
  {
    paramLong *= 1000L;
    for (;;)
    {
      try
      {
        localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k != i) || (j != m)) {
          continue;
        }
        m = (int)(ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() - paramLong / 1000L) / 60;
        if (m >= 1) {
          continue;
        }
        localObject1 = getString("LocationUpdatedJustNow", NUM);
      }
      catch (Exception localException)
      {
        Object localObject1;
        int i;
        int j;
        int k;
        int m;
        Object localObject2;
        Object localObject3;
        FileLog.e(localException);
        String str = "LOC_ERR";
        continue;
      }
      return (String)localObject1;
      if (m < 60)
      {
        localObject1 = formatPluralString("UpdatedMinutes", m);
      }
      else
      {
        localObject1 = getInstance().formatterDay;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = formatString("LocationUpdatedFormatted", NUM, new Object[] { formatString("TodayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject1).format((Date)localObject2) }) });
        continue;
        if ((k + 1 == i) && (j == m))
        {
          localObject1 = getInstance().formatterDay;
          localObject2 = new java/util/Date;
          ((Date)localObject2).<init>(paramLong);
          localObject1 = formatString("LocationUpdatedFormatted", NUM, new Object[] { formatString("YesterdayAtFormatted", NUM, new Object[] { ((FastDateFormat)localObject1).format((Date)localObject2) }) });
        }
        else if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        {
          localObject1 = getInstance().formatterMonth;
          localObject2 = new java/util/Date;
          ((Date)localObject2).<init>(paramLong);
          localObject3 = ((FastDateFormat)localObject1).format((Date)localObject2);
          localObject1 = getInstance().formatterDay;
          localObject2 = new java/util/Date;
          ((Date)localObject2).<init>(paramLong);
          localObject1 = formatString("LocationUpdatedFormatted", NUM, new Object[] { formatString("formatDateAtTime", NUM, new Object[] { localObject3, ((FastDateFormat)localObject1).format((Date)localObject2) }) });
        }
        else
        {
          localObject2 = getInstance().formatterYear;
          localObject1 = new java/util/Date;
          ((Date)localObject1).<init>(paramLong);
          localObject2 = ((FastDateFormat)localObject2).format((Date)localObject1);
          localObject1 = getInstance().formatterDay;
          localObject3 = new java/util/Date;
          ((Date)localObject3).<init>(paramLong);
          localObject1 = formatString("LocationUpdatedFormatted", NUM, new Object[] { formatString("formatDateAtTime", NUM, new Object[] { localObject2, ((FastDateFormat)localObject1).format((Date)localObject3) }) });
        }
      }
    }
  }
  
  public static String formatPluralString(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().currentPluralRules == null)) {}
    for (paramString = "LOC_ERR:" + paramString;; paramString = formatString(paramString, ApplicationLoader.applicationContext.getResources().getIdentifier(paramString, "string", ApplicationLoader.applicationContext.getPackageName()), new Object[] { Integer.valueOf(paramInt) }))
    {
      return paramString;
      String str = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(paramInt));
      paramString = paramString + "_" + str;
    }
  }
  
  public static String formatShortNumber(int paramInt, int[] paramArrayOfInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (paramInt / 1000 > 0)
    {
      localStringBuilder.append("K");
      i = paramInt % 1000 / 100;
      paramInt /= 1000;
    }
    if (paramArrayOfInt != null)
    {
      double d = paramInt + i / 10.0D;
      for (int j = 0; j < localStringBuilder.length(); j++) {
        d *= 1000.0D;
      }
      paramArrayOfInt[0] = ((int)d);
    }
    if ((i != 0) && (localStringBuilder.length() > 0)) {
      if (localStringBuilder.length() == 2) {
        paramArrayOfInt = String.format(Locale.US, "%d.%dM", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) });
      }
    }
    for (;;)
    {
      return paramArrayOfInt;
      paramArrayOfInt = String.format(Locale.US, "%d.%d%s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), localStringBuilder.toString() });
      continue;
      if (localStringBuilder.length() == 2) {
        paramArrayOfInt = String.format(Locale.US, "%dM", new Object[] { Integer.valueOf(paramInt) });
      } else {
        paramArrayOfInt = String.format(Locale.US, "%d%s", new Object[] { Integer.valueOf(paramInt), localStringBuilder.toString() });
      }
    }
  }
  
  public static String formatString(String paramString, int paramInt, Object... paramVarArgs)
  {
    for (;;)
    {
      try
      {
        String str1 = (String)getInstance().localeValues.get(paramString);
        str2 = str1;
        if (str1 == null) {
          str2 = ApplicationLoader.applicationContext.getString(paramInt);
        }
        if (getInstance().currentLocale == null) {
          continue;
        }
        paramVarArgs = String.format(getInstance().currentLocale, str2, paramVarArgs);
        paramString = paramVarArgs;
      }
      catch (Exception paramVarArgs)
      {
        String str2;
        FileLog.e(paramVarArgs);
        paramString = "LOC_ERR: " + paramString;
        continue;
      }
      return paramString;
      paramVarArgs = String.format(str2, paramVarArgs);
      paramString = paramVarArgs;
    }
  }
  
  public static String formatStringSimple(String paramString, Object... paramVarArgs)
  {
    for (;;)
    {
      try
      {
        if (getInstance().currentLocale == null) {
          continue;
        }
        paramVarArgs = String.format(getInstance().currentLocale, paramString, paramVarArgs);
        paramString = paramVarArgs;
      }
      catch (Exception paramVarArgs)
      {
        FileLog.e(paramVarArgs);
        paramString = "LOC_ERR: " + paramString;
        continue;
      }
      return paramString;
      paramVarArgs = String.format(paramString, paramVarArgs);
      paramString = paramVarArgs;
    }
  }
  
  public static String formatTTLString(int paramInt)
  {
    String str;
    if (paramInt < 60) {
      str = formatPluralString("Seconds", paramInt);
    }
    for (;;)
    {
      return str;
      if (paramInt < 3600)
      {
        str = formatPluralString("Minutes", paramInt / 60);
      }
      else if (paramInt < 86400)
      {
        str = formatPluralString("Hours", paramInt / 60 / 60);
      }
      else if (paramInt < 604800)
      {
        str = formatPluralString("Days", paramInt / 60 / 60 / 24);
      }
      else
      {
        int i = paramInt / 60 / 60 / 24;
        if (paramInt % 7 == 0) {
          str = formatPluralString("Weeks", i / 7);
        } else {
          str = String.format("%s %s", new Object[] { formatPluralString("Weeks", i / 7), formatPluralString("Days", i % 7) });
        }
      }
    }
  }
  
  public static String formatUserStatus(int paramInt, TLRPC.User paramUser)
  {
    if ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires == 0))
    {
      if ((paramUser.status instanceof TLRPC.TL_userStatusRecently)) {
        paramUser.status.expires = -100;
      }
    }
    else
    {
      if ((paramUser == null) || (paramUser.status == null) || (paramUser.status.expires > 0) || (!MessagesController.getInstance(paramInt).onlinePrivacy.containsKey(Integer.valueOf(paramUser.id)))) {
        break label137;
      }
      paramUser = getString("Online", NUM);
    }
    for (;;)
    {
      return paramUser;
      if ((paramUser.status instanceof TLRPC.TL_userStatusLastWeek))
      {
        paramUser.status.expires = -101;
        break;
      }
      if (!(paramUser.status instanceof TLRPC.TL_userStatusLastMonth)) {
        break;
      }
      paramUser.status.expires = -102;
      break;
      label137:
      if ((paramUser == null) || (paramUser.status == null) || (paramUser.status.expires == 0) || (UserObject.isDeleted(paramUser)) || ((paramUser instanceof TLRPC.TL_userEmpty)))
      {
        paramUser = getString("ALongTimeAgo", NUM);
      }
      else
      {
        paramInt = ConnectionsManager.getInstance(paramInt).getCurrentTime();
        if (paramUser.status.expires > paramInt) {
          paramUser = getString("Online", NUM);
        } else if (paramUser.status.expires == -1) {
          paramUser = getString("Invisible", NUM);
        } else if (paramUser.status.expires == -100) {
          paramUser = getString("Lately", NUM);
        } else if (paramUser.status.expires == -101) {
          paramUser = getString("WithinAWeek", NUM);
        } else if (paramUser.status.expires == -102) {
          paramUser = getString("WithinAMonth", NUM);
        } else {
          paramUser = formatDateOnline(paramUser.status.expires);
        }
      }
    }
  }
  
  public static String getCurrentLanguageName()
  {
    return getString("LanguageName", NUM);
  }
  
  /* Error */
  public static LocaleController getInstance()
  {
    // Byte code:
    //   0: getstatic 159	org/telegram/messenger/LocaleController:Instance	Lorg/telegram/messenger/LocaleController;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 159	org/telegram/messenger/LocaleController:Instance	Lorg/telegram/messenger/LocaleController;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/messenger/LocaleController
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 1025	org/telegram/messenger/LocaleController:<init>	()V
    //   31: aload_1
    //   32: putstatic 159	org/telegram/messenger/LocaleController:Instance	Lorg/telegram/messenger/LocaleController;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localLocaleController1	LocaleController
    //   5	34	1	localLocaleController2	LocaleController
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  private LocaleInfo getLanguageFromDict(String paramString)
  {
    if (paramString == null) {}
    for (paramString = null;; paramString = (LocaleInfo)this.languagesDict.get(paramString.toLowerCase().replace("-", "_"))) {
      return paramString;
    }
  }
  
  public static String getLocaleAlias(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      int i = -1;
      switch (paramString.hashCode())
      {
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          paramString = (String)localObject;
          break;
        case 0: 
          paramString = "id";
          break;
          if (paramString.equals("in"))
          {
            i = 0;
            continue;
            if (paramString.equals("iw"))
            {
              i = 1;
              continue;
              if (paramString.equals("jw"))
              {
                i = 2;
                continue;
                if (paramString.equals("no"))
                {
                  i = 3;
                  continue;
                  if (paramString.equals("tl"))
                  {
                    i = 4;
                    continue;
                    if (paramString.equals("ji"))
                    {
                      i = 5;
                      continue;
                      if (paramString.equals("id"))
                      {
                        i = 6;
                        continue;
                        if (paramString.equals("he"))
                        {
                          i = 7;
                          continue;
                          if (paramString.equals("jv"))
                          {
                            i = 8;
                            continue;
                            if (paramString.equals("nb"))
                            {
                              i = 9;
                              continue;
                              if (paramString.equals("fil"))
                              {
                                i = 10;
                                continue;
                                if (paramString.equals("yi")) {
                                  i = 11;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        }
      }
      paramString = "he";
      continue;
      paramString = "jv";
      continue;
      paramString = "nb";
      continue;
      paramString = "fil";
      continue;
      paramString = "yi";
      continue;
      paramString = "in";
      continue;
      paramString = "iw";
      continue;
      paramString = "jw";
      continue;
      paramString = "no";
      continue;
      paramString = "tl";
      continue;
      paramString = "ji";
    }
  }
  
  private HashMap<String, String> getLocaleFileStrings(File paramFile)
  {
    return getLocaleFileStrings(paramFile, false);
  }
  
  /* Error */
  private HashMap<String, String> getLocaleFileStrings(File paramFile, boolean paramBoolean)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: iconst_0
    //   7: putfield 1048	org/telegram/messenger/LocaleController:reloadLastFile	Z
    //   10: aload_3
    //   11: astore 5
    //   13: aload_1
    //   14: invokevirtual 1053	java/io/File:exists	()Z
    //   17: ifne +47 -> 64
    //   20: aload_3
    //   21: astore 5
    //   23: new 167	java/util/HashMap
    //   26: astore 6
    //   28: aload_3
    //   29: astore 5
    //   31: aload 6
    //   33: invokespecial 168	java/util/HashMap:<init>	()V
    //   36: aload 6
    //   38: astore_1
    //   39: iconst_0
    //   40: ifeq +11 -> 51
    //   43: new 1055	java/lang/NullPointerException
    //   46: dup
    //   47: invokespecial 1056	java/lang/NullPointerException:<init>	()V
    //   50: athrow
    //   51: aload_1
    //   52: areturn
    //   53: astore_1
    //   54: aload_1
    //   55: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   58: aload 6
    //   60: astore_1
    //   61: goto -10 -> 51
    //   64: aload_3
    //   65: astore 5
    //   67: new 167	java/util/HashMap
    //   70: astore 7
    //   72: aload_3
    //   73: astore 5
    //   75: aload 7
    //   77: invokespecial 168	java/util/HashMap:<init>	()V
    //   80: aload_3
    //   81: astore 5
    //   83: invokestatic 1062	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   86: astore 8
    //   88: aload_3
    //   89: astore 5
    //   91: new 1064	java/io/FileInputStream
    //   94: dup
    //   95: aload_1
    //   96: invokespecial 1067	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   99: astore 9
    //   101: aload 8
    //   103: aload 9
    //   105: ldc_w 1069
    //   108: invokeinterface 1075 3 0
    //   113: aload 8
    //   115: invokeinterface 1078 1 0
    //   120: istore 10
    //   122: aconst_null
    //   123: astore 6
    //   125: aconst_null
    //   126: astore 11
    //   128: aconst_null
    //   129: astore 4
    //   131: iload 10
    //   133: iconst_1
    //   134: if_icmpeq +437 -> 571
    //   137: iload 10
    //   139: iconst_2
    //   140: if_icmpne +175 -> 315
    //   143: aload 8
    //   145: invokeinterface 1081 1 0
    //   150: astore 6
    //   152: aload 4
    //   154: astore_3
    //   155: aload 6
    //   157: astore 5
    //   159: aload 11
    //   161: astore_1
    //   162: aload 8
    //   164: invokeinterface 1084 1 0
    //   169: ifle +19 -> 188
    //   172: aload 8
    //   174: iconst_0
    //   175: invokeinterface 1087 2 0
    //   180: astore_3
    //   181: aload 11
    //   183: astore_1
    //   184: aload 6
    //   186: astore 5
    //   188: aload_3
    //   189: astore 4
    //   191: aload 5
    //   193: astore 6
    //   195: aload_1
    //   196: astore 11
    //   198: aload 5
    //   200: ifnull +103 -> 303
    //   203: aload_3
    //   204: astore 4
    //   206: aload 5
    //   208: astore 6
    //   210: aload_1
    //   211: astore 11
    //   213: aload 5
    //   215: ldc_w 910
    //   218: invokevirtual 1035	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   221: ifeq +82 -> 303
    //   224: aload_3
    //   225: astore 4
    //   227: aload 5
    //   229: astore 6
    //   231: aload_1
    //   232: astore 11
    //   234: aload_1
    //   235: ifnull +68 -> 303
    //   238: aload_3
    //   239: astore 4
    //   241: aload 5
    //   243: astore 6
    //   245: aload_1
    //   246: astore 11
    //   248: aload_3
    //   249: ifnull +54 -> 303
    //   252: aload_3
    //   253: astore 4
    //   255: aload 5
    //   257: astore 6
    //   259: aload_1
    //   260: astore 11
    //   262: aload_1
    //   263: invokevirtual 730	java/lang/String:length	()I
    //   266: ifeq +37 -> 303
    //   269: aload_3
    //   270: astore 4
    //   272: aload 5
    //   274: astore 6
    //   276: aload_1
    //   277: astore 11
    //   279: aload_3
    //   280: invokevirtual 730	java/lang/String:length	()I
    //   283: ifeq +20 -> 303
    //   286: aload 7
    //   288: aload_3
    //   289: aload_1
    //   290: invokevirtual 494	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   293: pop
    //   294: aconst_null
    //   295: astore 6
    //   297: aconst_null
    //   298: astore 11
    //   300: aconst_null
    //   301: astore 4
    //   303: aload 8
    //   305: invokeinterface 1090 1 0
    //   310: istore 10
    //   312: goto -181 -> 131
    //   315: iload 10
    //   317: iconst_4
    //   318: if_icmpne +227 -> 545
    //   321: aload 4
    //   323: astore_3
    //   324: aload 6
    //   326: astore 5
    //   328: aload 11
    //   330: astore_1
    //   331: aload 4
    //   333: ifnull -145 -> 188
    //   336: aload 8
    //   338: invokeinterface 1093 1 0
    //   343: astore 11
    //   345: aload 4
    //   347: astore_3
    //   348: aload 6
    //   350: astore 5
    //   352: aload 11
    //   354: astore_1
    //   355: aload 11
    //   357: ifnull -169 -> 188
    //   360: aload 11
    //   362: invokevirtual 1096	java/lang/String:trim	()Ljava/lang/String;
    //   365: astore_1
    //   366: iload_2
    //   367: ifeq +51 -> 418
    //   370: aload_1
    //   371: ldc_w 744
    //   374: ldc_w 746
    //   377: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   380: ldc_w 748
    //   383: ldc_w 750
    //   386: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   389: ldc_w 1098
    //   392: ldc_w 1100
    //   395: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   398: ldc_w 752
    //   401: ldc_w 754
    //   404: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   407: astore_1
    //   408: aload 4
    //   410: astore_3
    //   411: aload 6
    //   413: astore 5
    //   415: goto -227 -> 188
    //   418: aload_1
    //   419: ldc_w 1102
    //   422: ldc_w 1104
    //   425: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   428: ldc_w 1106
    //   431: ldc_w 1108
    //   434: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   437: astore 12
    //   439: aload 12
    //   441: ldc_w 746
    //   444: ldc_w 744
    //   447: invokevirtual 721	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   450: astore 11
    //   452: aload 4
    //   454: astore_3
    //   455: aload 6
    //   457: astore 5
    //   459: aload 11
    //   461: astore_1
    //   462: aload_0
    //   463: getfield 1048	org/telegram/messenger/LocaleController:reloadLastFile	Z
    //   466: ifne -278 -> 188
    //   469: aload 4
    //   471: astore_3
    //   472: aload 6
    //   474: astore 5
    //   476: aload 11
    //   478: astore_1
    //   479: aload 11
    //   481: aload 12
    //   483: invokevirtual 1035	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   486: ifne -298 -> 188
    //   489: aload_0
    //   490: iconst_1
    //   491: putfield 1048	org/telegram/messenger/LocaleController:reloadLastFile	Z
    //   494: aload 4
    //   496: astore_3
    //   497: aload 6
    //   499: astore 5
    //   501: aload 11
    //   503: astore_1
    //   504: goto -316 -> 188
    //   507: astore_3
    //   508: aload 9
    //   510: astore_1
    //   511: aload_1
    //   512: astore 5
    //   514: aload_3
    //   515: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   518: aload_1
    //   519: astore 5
    //   521: aload_0
    //   522: iconst_1
    //   523: putfield 1048	org/telegram/messenger/LocaleController:reloadLastFile	Z
    //   526: aload_1
    //   527: ifnull +7 -> 534
    //   530: aload_1
    //   531: invokevirtual 1111	java/io/FileInputStream:close	()V
    //   534: new 167	java/util/HashMap
    //   537: dup
    //   538: invokespecial 168	java/util/HashMap:<init>	()V
    //   541: astore_1
    //   542: goto -491 -> 51
    //   545: aload 4
    //   547: astore_3
    //   548: aload 6
    //   550: astore 5
    //   552: aload 11
    //   554: astore_1
    //   555: iload 10
    //   557: iconst_3
    //   558: if_icmpne -370 -> 188
    //   561: aconst_null
    //   562: astore_1
    //   563: aconst_null
    //   564: astore_3
    //   565: aconst_null
    //   566: astore 5
    //   568: goto -380 -> 188
    //   571: aload 9
    //   573: ifnull +8 -> 581
    //   576: aload 9
    //   578: invokevirtual 1111	java/io/FileInputStream:close	()V
    //   581: aload 7
    //   583: astore_1
    //   584: goto -533 -> 51
    //   587: astore_1
    //   588: aload_1
    //   589: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   592: goto -11 -> 581
    //   595: astore_1
    //   596: aload_1
    //   597: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   600: goto -66 -> 534
    //   603: astore_1
    //   604: aload 5
    //   606: ifnull +8 -> 614
    //   609: aload 5
    //   611: invokevirtual 1111	java/io/FileInputStream:close	()V
    //   614: aload_1
    //   615: athrow
    //   616: astore 5
    //   618: aload 5
    //   620: invokestatic 634	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   623: goto -9 -> 614
    //   626: astore_1
    //   627: aload 9
    //   629: astore 5
    //   631: goto -27 -> 604
    //   634: astore_3
    //   635: aload 4
    //   637: astore_1
    //   638: goto -127 -> 511
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	641	0	this	LocaleController
    //   0	641	1	paramFile	File
    //   0	641	2	paramBoolean	boolean
    //   1	496	3	localObject1	Object
    //   507	8	3	localException1	Exception
    //   547	18	3	localObject2	Object
    //   634	1	3	localException2	Exception
    //   3	633	4	localObject3	Object
    //   11	599	5	localObject4	Object
    //   616	3	5	localException3	Exception
    //   629	1	5	localObject5	Object
    //   26	523	6	localObject6	Object
    //   70	512	7	localHashMap	HashMap
    //   86	251	8	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   99	529	9	localFileInputStream	java.io.FileInputStream
    //   120	439	10	i	int
    //   126	427	11	localObject7	Object
    //   437	45	12	str	String
    // Exception table:
    //   from	to	target	type
    //   43	51	53	java/lang/Exception
    //   101	122	507	java/lang/Exception
    //   143	152	507	java/lang/Exception
    //   162	181	507	java/lang/Exception
    //   213	224	507	java/lang/Exception
    //   262	269	507	java/lang/Exception
    //   279	294	507	java/lang/Exception
    //   303	312	507	java/lang/Exception
    //   336	345	507	java/lang/Exception
    //   360	366	507	java/lang/Exception
    //   370	408	507	java/lang/Exception
    //   418	452	507	java/lang/Exception
    //   462	469	507	java/lang/Exception
    //   479	494	507	java/lang/Exception
    //   576	581	587	java/lang/Exception
    //   530	534	595	java/lang/Exception
    //   13	20	603	finally
    //   23	28	603	finally
    //   31	36	603	finally
    //   67	72	603	finally
    //   75	80	603	finally
    //   83	88	603	finally
    //   91	101	603	finally
    //   514	518	603	finally
    //   521	526	603	finally
    //   609	614	616	java/lang/Exception
    //   101	122	626	finally
    //   143	152	626	finally
    //   162	181	626	finally
    //   213	224	626	finally
    //   262	269	626	finally
    //   279	294	626	finally
    //   303	312	626	finally
    //   336	345	626	finally
    //   360	366	626	finally
    //   370	408	626	finally
    //   418	452	626	finally
    //   462	469	626	finally
    //   479	494	626	finally
    //   13	20	634	java/lang/Exception
    //   23	28	634	java/lang/Exception
    //   31	36	634	java/lang/Exception
    //   67	72	634	java/lang/Exception
    //   75	80	634	java/lang/Exception
    //   83	88	634	java/lang/Exception
    //   91	101	634	java/lang/Exception
  }
  
  private String getLocaleString(Locale paramLocale)
  {
    if (paramLocale == null) {
      paramLocale = "en";
    }
    for (;;)
    {
      return paramLocale;
      String str1 = paramLocale.getLanguage();
      String str2 = paramLocale.getCountry();
      paramLocale = paramLocale.getVariant();
      if ((str1.length() == 0) && (str2.length() == 0))
      {
        paramLocale = "en";
      }
      else
      {
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
        paramLocale = localStringBuilder.toString();
      }
    }
  }
  
  public static String getLocaleStringIso639()
  {
    Object localObject = getInstance().currentLocale;
    String str1;
    if (localObject == null) {
      str1 = "en";
    }
    for (;;)
    {
      return str1;
      String str2 = ((Locale)localObject).getLanguage();
      str1 = ((Locale)localObject).getCountry();
      localObject = ((Locale)localObject).getVariant();
      if ((str2.length() == 0) && (str1.length() == 0))
      {
        str1 = "en";
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder(11);
        localStringBuilder.append(str2);
        if ((str1.length() > 0) || (((String)localObject).length() > 0)) {
          localStringBuilder.append('-');
        }
        localStringBuilder.append(str1);
        if (((String)localObject).length() > 0) {
          localStringBuilder.append('_');
        }
        localStringBuilder.append((String)localObject);
        str1 = localStringBuilder.toString();
      }
    }
  }
  
  public static String getPluralString(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().currentPluralRules == null)) {}
    for (paramString = "LOC_ERR:" + paramString;; paramString = getString(paramString, ApplicationLoader.applicationContext.getResources().getIdentifier(paramString, "string", ApplicationLoader.applicationContext.getPackageName())))
    {
      return paramString;
      String str = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(paramInt));
      paramString = paramString + "_" + str;
    }
  }
  
  public static String getString(String paramString, int paramInt)
  {
    return getInstance().getStringInternal(paramString, paramInt);
  }
  
  private String getStringInternal(String paramString, int paramInt)
  {
    Object localObject1 = (String)this.localeValues.get(paramString);
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject2 = ApplicationLoader.applicationContext.getString(paramInt);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = "LOC_ERR:" + paramString;
      }
      return (String)localObject1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        Object localObject3 = localObject1;
      }
    }
  }
  
  public static String getSystemLocaleStringIso639()
  {
    Object localObject = getInstance().getSystemDefaultLocale();
    String str1;
    if (localObject == null) {
      str1 = "en";
    }
    for (;;)
    {
      return str1;
      String str2 = ((Locale)localObject).getLanguage();
      str1 = ((Locale)localObject).getCountry();
      String str3 = ((Locale)localObject).getVariant();
      if ((str2.length() == 0) && (str1.length() == 0))
      {
        str1 = "en";
      }
      else
      {
        localObject = new StringBuilder(11);
        ((StringBuilder)localObject).append(str2);
        if ((str1.length() > 0) || (str3.length() > 0)) {
          ((StringBuilder)localObject).append('-');
        }
        ((StringBuilder)localObject).append(str1);
        if (str3.length() > 0) {
          ((StringBuilder)localObject).append('_');
        }
        ((StringBuilder)localObject).append(str3);
        str1 = ((StringBuilder)localObject).toString();
      }
    }
  }
  
  public static boolean isRTLCharacter(char paramChar)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (Character.getDirectionality(paramChar) != 1)
    {
      bool2 = bool1;
      if (Character.getDirectionality(paramChar) != 2)
      {
        bool2 = bool1;
        if (Character.getDirectionality(paramChar) != 16) {
          if (Character.getDirectionality(paramChar) != 17) {
            break label46;
          }
        }
      }
    }
    label46:
    for (bool2 = bool1;; bool2 = false) {
      return bool2;
    }
  }
  
  private void loadOtherLanguages()
  {
    int i = 0;
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
    Object localObject2 = ((SharedPreferences)localObject1).getString("locales", null);
    int j;
    int k;
    if (!TextUtils.isEmpty((CharSequence)localObject2))
    {
      localObject2 = ((String)localObject2).split("&");
      j = localObject2.length;
      for (k = 0; k < j; k++)
      {
        LocaleInfo localLocaleInfo = LocaleInfo.createWithString(localObject2[k]);
        if (localLocaleInfo != null) {
          this.otherLanguages.add(localLocaleInfo);
        }
      }
    }
    localObject1 = ((SharedPreferences)localObject1).getString("remote", null);
    if (!TextUtils.isEmpty((CharSequence)localObject1))
    {
      localObject1 = ((String)localObject1).split("&");
      j = localObject1.length;
      for (k = i; k < j; k++)
      {
        localObject2 = LocaleInfo.createWithString(localObject1[k]);
        ((LocaleInfo)localObject2).shortName = ((LocaleInfo)localObject2).shortName.replace("-", "_");
        if (localObject2 != null) {
          this.remoteLanguages.add(localObject2);
        }
      }
    }
  }
  
  private void saveOtherLanguages()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
    StringBuilder localStringBuilder = new StringBuilder();
    String str;
    for (int i = 0; i < this.otherLanguages.size(); i++)
    {
      str = ((LocaleInfo)this.otherLanguages.get(i)).getSaveString();
      if (str != null)
      {
        if (localStringBuilder.length() != 0) {
          localStringBuilder.append("&");
        }
        localStringBuilder.append(str);
      }
    }
    localEditor.putString("locales", localStringBuilder.toString());
    localStringBuilder.setLength(0);
    for (i = 0; i < this.remoteLanguages.size(); i++)
    {
      str = ((LocaleInfo)this.remoteLanguages.get(i)).getSaveString();
      if (str != null)
      {
        if (localStringBuilder.length() != 0) {
          localStringBuilder.append("&");
        }
        localStringBuilder.append(str);
      }
    }
    localEditor.putString("remote", localStringBuilder.toString());
    localEditor.commit();
  }
  
  public static String stringForMessageListDate(long paramLong)
  {
    paramLong *= 1000L;
    try
    {
      Object localObject1 = Calendar.getInstance();
      int i = ((Calendar)localObject1).get(6);
      ((Calendar)localObject1).setTimeInMillis(paramLong);
      int j = ((Calendar)localObject1).get(6);
      Object localObject2;
      if (Math.abs(System.currentTimeMillis() - paramLong) >= 31536000000L)
      {
        localObject1 = getInstance().formatterYear;
        localObject2 = new java/util/Date;
        ((Date)localObject2).<init>(paramLong);
        localObject1 = ((FastDateFormat)localObject1).format((Date)localObject2);
      }
      for (;;)
      {
        return (String)localObject1;
        j -= i;
        if ((j == 0) || ((j == -1) && (System.currentTimeMillis() - paramLong < 28800000L)))
        {
          localObject2 = getInstance().formatterDay;
          localObject1 = new java/util/Date;
          ((Date)localObject1).<init>(paramLong);
          localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
        }
        else if ((j > -7) && (j <= -1))
        {
          localObject2 = getInstance().formatterWeek;
          localObject1 = new java/util/Date;
          ((Date)localObject1).<init>(paramLong);
          localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
        }
        else
        {
          localObject2 = getInstance().formatterMonth;
          localObject1 = new java/util/Date;
          ((Date)localObject1).<init>(paramLong);
          localObject1 = ((FastDateFormat)localObject2).format((Date)localObject1);
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        String str = "LOC_ERR";
      }
    }
  }
  
  private String stringForQuantity(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "other";
    }
    for (;;)
    {
      return str;
      str = "zero";
      continue;
      str = "one";
      continue;
      str = "two";
      continue;
      str = "few";
      continue;
      str = "many";
    }
  }
  
  public void applyLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    applyLanguage(paramLocaleInfo, paramBoolean1, paramBoolean2, false, false, paramInt);
  }
  
  public void applyLanguage(final LocaleInfo paramLocaleInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, final int paramInt)
  {
    if (paramLocaleInfo == null) {
      return;
    }
    File localFile = paramLocaleInfo.getPathToFile();
    Object localObject = paramLocaleInfo.shortName;
    if (!paramBoolean2) {
      ConnectionsManager.setLangCode(((String)localObject).replace("_", "-"));
    }
    if ((paramLocaleInfo.isRemote()) && ((paramBoolean4) || (!localFile.exists())))
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("reload locale because file doesn't exist " + localFile);
      }
      if (!paramBoolean2) {
        break label360;
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          LocaleController.this.applyRemoteLanguage(paramLocaleInfo, true, paramInt);
        }
      });
    }
    for (;;)
    {
      try
      {
        arrayOfString = paramLocaleInfo.shortName.split("_");
        if (arrayOfString.length != 1) {
          continue;
        }
        localObject = new java/util/Locale;
        ((Locale)localObject).<init>(paramLocaleInfo.shortName);
        if (paramBoolean1)
        {
          this.languageOverride = paramLocaleInfo.shortName;
          SharedPreferences.Editor localEditor = MessagesController.getGlobalMainSettings().edit();
          localEditor.putString("language", paramLocaleInfo.getKey());
          localEditor.commit();
        }
        if (localFile != null) {
          continue;
        }
        this.localeValues.clear();
        this.currentLocale = ((Locale)localObject);
        this.currentLocaleInfo = paramLocaleInfo;
        this.currentPluralRules = ((PluralRules)this.allRules.get(arrayOfString[0]));
        if (this.currentPluralRules == null) {
          this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
        }
        if (this.currentPluralRules == null)
        {
          paramLocaleInfo = new org/telegram/messenger/LocaleController$PluralRules_None;
          paramLocaleInfo.<init>();
          this.currentPluralRules = paramLocaleInfo;
        }
        this.changingConfiguration = true;
        Locale.setDefault(this.currentLocale);
        paramLocaleInfo = new android/content/res/Configuration;
        paramLocaleInfo.<init>();
        paramLocaleInfo.locale = this.currentLocale;
        ApplicationLoader.applicationContext.getResources().updateConfiguration(paramLocaleInfo, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
        this.changingConfiguration = false;
        if (!this.reloadLastFile) {
          continue;
        }
        if (!paramBoolean2) {
          continue;
        }
        paramLocaleInfo = new org/telegram/messenger/LocaleController$3;
        paramLocaleInfo.<init>(this, paramInt);
        AndroidUtilities.runOnUIThread(paramLocaleInfo);
      }
      catch (Exception paramLocaleInfo)
      {
        String[] arrayOfString;
        label360:
        FileLog.e(paramLocaleInfo);
        this.changingConfiguration = false;
        continue;
        reloadCurrentRemoteLocale(paramInt);
        continue;
      }
      this.reloadLastFile = false;
      recreateFormatters();
      break;
      applyRemoteLanguage(paramLocaleInfo, true, paramInt);
      continue;
      localObject = new Locale(arrayOfString[0], arrayOfString[1]);
      continue;
      if (!paramBoolean3) {
        this.localeValues = getLocaleFileStrings(localFile);
      }
    }
  }
  
  public boolean applyLanguageFile(File paramFile, int paramInt)
  {
    for (;;)
    {
      try
      {
        localHashMap = getLocaleFileStrings(paramFile);
        str1 = (String)localHashMap.get("LanguageName");
        str2 = (String)localHashMap.get("LanguageNameInEnglish");
        str3 = (String)localHashMap.get("LanguageCode");
        if ((str1 == null) || (str1.length() <= 0) || (str2 == null) || (str2.length() <= 0) || (str3 == null) || (str3.length() <= 0)) {
          continue;
        }
        if ((!str1.contains("&")) && (!str1.contains("|"))) {
          continue;
        }
        bool = false;
      }
      catch (Exception paramFile)
      {
        HashMap localHashMap;
        String str1;
        String str2;
        String str3;
        File localFile;
        Object localObject;
        StringBuilder localStringBuilder;
        FileLog.e(paramFile);
        boolean bool = false;
        continue;
      }
      return bool;
      if ((str2.contains("&")) || (str2.contains("|")))
      {
        bool = false;
      }
      else if ((str3.contains("&")) || (str3.contains("|")) || (str3.contains("/")) || (str3.contains("\\")))
      {
        bool = false;
      }
      else
      {
        localFile = new java/io/File;
        localObject = ApplicationLoader.getFilesDirFixed();
        localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localFile.<init>((File)localObject, str3 + ".xml");
        if (!AndroidUtilities.copyFile(paramFile, localFile))
        {
          bool = false;
        }
        else
        {
          paramFile = new java/lang/StringBuilder;
          paramFile.<init>();
          localObject = getLanguageFromDict("local_" + str3.toLowerCase());
          paramFile = (File)localObject;
          if (localObject == null)
          {
            paramFile = new org/telegram/messenger/LocaleController$LocaleInfo;
            paramFile.<init>();
            paramFile.name = str1;
            paramFile.nameEnglish = str2;
            paramFile.shortName = str3.toLowerCase();
            paramFile.pathToFile = localFile.getAbsolutePath();
            this.languages.add(paramFile);
            this.languagesDict.put(paramFile.getKey(), paramFile);
            this.otherLanguages.add(paramFile);
            saveOtherLanguages();
          }
          this.localeValues = localHashMap;
          applyLanguage(paramFile, true, false, true, false, paramInt);
          bool = true;
        }
      }
    }
  }
  
  public void checkUpdateForCurrentRemoteLocale(int paramInt1, int paramInt2)
  {
    if ((this.currentLocaleInfo == null) || ((this.currentLocaleInfo != null) && (!this.currentLocaleInfo.isRemote()))) {}
    for (;;)
    {
      return;
      if (this.currentLocaleInfo.version < paramInt2) {
        applyRemoteLanguage(this.currentLocaleInfo, false, paramInt1);
      }
    }
  }
  
  public boolean deleteLanguage(LocaleInfo paramLocaleInfo, int paramInt)
  {
    boolean bool = true;
    if ((paramLocaleInfo.pathToFile == null) || (paramLocaleInfo.isRemote())) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if (this.currentLocaleInfo == paramLocaleInfo)
      {
        Object localObject1 = null;
        if (this.systemDefaultLocale.getLanguage() != null) {
          localObject1 = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
        }
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
        }
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = getLanguageFromDict("en");
        }
        applyLanguage((LocaleInfo)localObject1, true, false, paramInt);
      }
      this.otherLanguages.remove(paramLocaleInfo);
      this.languages.remove(paramLocaleInfo);
      this.languagesDict.remove(paramLocaleInfo.shortName);
      new File(paramLocaleInfo.pathToFile).delete();
      saveOtherLanguages();
    }
  }
  
  public String formatCurrencyDecimalString(long paramLong, String paramString, boolean paramBoolean)
  {
    String str = paramString.toUpperCase();
    paramLong = Math.abs(paramLong);
    int i = -1;
    double d;
    label416:
    Locale localLocale;
    switch (str.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        paramString = " %.2f";
        d = paramLong / 100.0D;
        localLocale = Locale.US;
        if (!paramBoolean) {}
        break;
      }
      break;
    }
    for (paramString = str;; paramString = "" + paramString)
    {
      return String.format(localLocale, paramString, new Object[] { Double.valueOf(d) }).trim();
      if (!str.equals("CLF")) {
        break;
      }
      i = 0;
      break;
      if (!str.equals("IRR")) {
        break;
      }
      i = 1;
      break;
      if (!str.equals("BHD")) {
        break;
      }
      i = 2;
      break;
      if (!str.equals("IQD")) {
        break;
      }
      i = 3;
      break;
      if (!str.equals("JOD")) {
        break;
      }
      i = 4;
      break;
      if (!str.equals("KWD")) {
        break;
      }
      i = 5;
      break;
      if (!str.equals("LYD")) {
        break;
      }
      i = 6;
      break;
      if (!str.equals("OMR")) {
        break;
      }
      i = 7;
      break;
      if (!str.equals("TND")) {
        break;
      }
      i = 8;
      break;
      if (!str.equals("BIF")) {
        break;
      }
      i = 9;
      break;
      if (!str.equals("BYR")) {
        break;
      }
      i = 10;
      break;
      if (!str.equals("CLP")) {
        break;
      }
      i = 11;
      break;
      if (!str.equals("CVE")) {
        break;
      }
      i = 12;
      break;
      if (!str.equals("DJF")) {
        break;
      }
      i = 13;
      break;
      if (!str.equals("GNF")) {
        break;
      }
      i = 14;
      break;
      if (!str.equals("ISK")) {
        break;
      }
      i = 15;
      break;
      if (!str.equals("JPY")) {
        break;
      }
      i = 16;
      break;
      if (!str.equals("KMF")) {
        break;
      }
      i = 17;
      break;
      if (!str.equals("KRW")) {
        break;
      }
      i = 18;
      break;
      if (!str.equals("MGA")) {
        break;
      }
      i = 19;
      break;
      if (!str.equals("PYG")) {
        break;
      }
      i = 20;
      break;
      if (!str.equals("RWF")) {
        break;
      }
      i = 21;
      break;
      if (!str.equals("UGX")) {
        break;
      }
      i = 22;
      break;
      if (!str.equals("UYI")) {
        break;
      }
      i = 23;
      break;
      if (!str.equals("VND")) {
        break;
      }
      i = 24;
      break;
      if (!str.equals("VUV")) {
        break;
      }
      i = 25;
      break;
      if (!str.equals("XAF")) {
        break;
      }
      i = 26;
      break;
      if (!str.equals("XOF")) {
        break;
      }
      i = 27;
      break;
      if (!str.equals("XPF")) {
        break;
      }
      i = 28;
      break;
      if (!str.equals("MRO")) {
        break;
      }
      i = 29;
      break;
      paramString = " %.4f";
      d = paramLong / 10000.0D;
      break label416;
      d = (float)paramLong / 100.0F;
      if (paramLong % 100L == 0L)
      {
        paramString = " %.0f";
        break label416;
      }
      paramString = " %.2f";
      break label416;
      paramString = " %.3f";
      d = paramLong / 1000.0D;
      break label416;
      paramString = " %.0f";
      d = paramLong;
      break label416;
      paramString = " %.1f";
      d = paramLong / 10.0D;
      break label416;
    }
  }
  
  public String formatCurrencyString(long paramLong, String paramString)
  {
    String str = paramString.toUpperCase();
    int i;
    int j;
    label284:
    double d;
    if (paramLong < 0L)
    {
      i = 1;
      paramLong = Math.abs(paramLong);
      localObject1 = Currency.getInstance(str);
      j = -1;
      switch (str.hashCode())
      {
      default: 
        switch (j)
        {
        default: 
          paramString = " %.2f";
          d = paramLong / 100.0D;
          label432:
          if (localObject1 == null) {
            break label1163;
          }
          if (this.currentLocale != null)
          {
            paramString = this.currentLocale;
            label449:
            localObject2 = NumberFormat.getCurrencyInstance(paramString);
            ((NumberFormat)localObject2).setCurrency((Currency)localObject1);
            if (str.equals("IRR")) {
              ((NumberFormat)localObject2).setMaximumFractionDigits(0);
            }
            localObject1 = new StringBuilder();
            if (i == 0) {
              break label1156;
            }
          }
          break;
        }
        break;
      }
    }
    label1156:
    for (paramString = "-";; paramString = "")
    {
      paramString = paramString + ((NumberFormat)localObject2).format(d);
      return paramString;
      i = 0;
      break;
      if (!str.equals("CLF")) {
        break label284;
      }
      j = 0;
      break label284;
      if (!str.equals("IRR")) {
        break label284;
      }
      j = 1;
      break label284;
      if (!str.equals("BHD")) {
        break label284;
      }
      j = 2;
      break label284;
      if (!str.equals("IQD")) {
        break label284;
      }
      j = 3;
      break label284;
      if (!str.equals("JOD")) {
        break label284;
      }
      j = 4;
      break label284;
      if (!str.equals("KWD")) {
        break label284;
      }
      j = 5;
      break label284;
      if (!str.equals("LYD")) {
        break label284;
      }
      j = 6;
      break label284;
      if (!str.equals("OMR")) {
        break label284;
      }
      j = 7;
      break label284;
      if (!str.equals("TND")) {
        break label284;
      }
      j = 8;
      break label284;
      if (!str.equals("BIF")) {
        break label284;
      }
      j = 9;
      break label284;
      if (!str.equals("BYR")) {
        break label284;
      }
      j = 10;
      break label284;
      if (!str.equals("CLP")) {
        break label284;
      }
      j = 11;
      break label284;
      if (!str.equals("CVE")) {
        break label284;
      }
      j = 12;
      break label284;
      if (!str.equals("DJF")) {
        break label284;
      }
      j = 13;
      break label284;
      if (!str.equals("GNF")) {
        break label284;
      }
      j = 14;
      break label284;
      if (!str.equals("ISK")) {
        break label284;
      }
      j = 15;
      break label284;
      if (!str.equals("JPY")) {
        break label284;
      }
      j = 16;
      break label284;
      if (!str.equals("KMF")) {
        break label284;
      }
      j = 17;
      break label284;
      if (!str.equals("KRW")) {
        break label284;
      }
      j = 18;
      break label284;
      if (!str.equals("MGA")) {
        break label284;
      }
      j = 19;
      break label284;
      if (!str.equals("PYG")) {
        break label284;
      }
      j = 20;
      break label284;
      if (!str.equals("RWF")) {
        break label284;
      }
      j = 21;
      break label284;
      if (!str.equals("UGX")) {
        break label284;
      }
      j = 22;
      break label284;
      if (!str.equals("UYI")) {
        break label284;
      }
      j = 23;
      break label284;
      if (!str.equals("VND")) {
        break label284;
      }
      j = 24;
      break label284;
      if (!str.equals("VUV")) {
        break label284;
      }
      j = 25;
      break label284;
      if (!str.equals("XAF")) {
        break label284;
      }
      j = 26;
      break label284;
      if (!str.equals("XOF")) {
        break label284;
      }
      j = 27;
      break label284;
      if (!str.equals("XPF")) {
        break label284;
      }
      j = 28;
      break label284;
      if (!str.equals("MRO")) {
        break label284;
      }
      j = 29;
      break label284;
      paramString = " %.4f";
      d = paramLong / 10000.0D;
      break label432;
      d = (float)paramLong / 100.0F;
      if (paramLong % 100L == 0L)
      {
        paramString = " %.0f";
        break label432;
      }
      paramString = " %.2f";
      break label432;
      paramString = " %.3f";
      d = paramLong / 1000.0D;
      break label432;
      paramString = " %.0f";
      d = paramLong;
      break label432;
      paramString = " %.1f";
      d = paramLong / 10.0D;
      break label432;
      paramString = this.systemDefaultLocale;
      break label449;
    }
    label1163:
    Object localObject2 = new StringBuilder();
    if (i != 0) {}
    for (Object localObject1 = "-";; localObject1 = "")
    {
      paramString = (String)localObject1 + String.format(Locale.US, new StringBuilder().append(str).append(paramString).toString(), new Object[] { Double.valueOf(d) });
      break;
    }
  }
  
  public LocaleInfo getCurrentLocaleInfo()
  {
    return this.currentLocaleInfo;
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
    int i = paramString.length();
    int j = 0;
    if (j < i)
    {
      String str1 = paramString.substring(j, j + 1);
      String str2 = (String)this.translitChars.get(str1);
      if (str2 != null) {
        localStringBuilder.append(str2);
      }
      for (;;)
      {
        j++;
        break;
        localStringBuilder.append(str1);
      }
    }
    return localStringBuilder.toString();
  }
  
  public boolean isCurrentLocalLocale()
  {
    return this.currentLocaleInfo.isLocal();
  }
  
  public void loadRemoteLanguages(final int paramInt)
  {
    if (this.loadingRemoteLanguages) {}
    for (;;)
    {
      return;
      this.loadingRemoteLanguages = true;
      TLRPC.TL_langpack_getLanguages localTL_langpack_getLanguages = new TLRPC.TL_langpack_getLanguages();
      ConnectionsManager.getInstance(paramInt).sendRequest(localTL_langpack_getLanguages, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                LocaleController.access$1102(LocaleController.this, false);
                TLRPC.Vector localVector = (TLRPC.Vector)paramAnonymousTLObject;
                HashMap localHashMap = new HashMap();
                LocaleController.this.remoteLanguages.clear();
                int i = 0;
                Object localObject1;
                Object localObject2;
                if (i < localVector.objects.size())
                {
                  localObject1 = (TLRPC.TL_langPackLanguage)localVector.objects.get(i);
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("loaded lang " + ((TLRPC.TL_langPackLanguage)localObject1).name);
                  }
                  localObject2 = new LocaleController.LocaleInfo();
                  ((LocaleController.LocaleInfo)localObject2).nameEnglish = ((TLRPC.TL_langPackLanguage)localObject1).name;
                  ((LocaleController.LocaleInfo)localObject2).name = ((TLRPC.TL_langPackLanguage)localObject1).native_name;
                  ((LocaleController.LocaleInfo)localObject2).shortName = ((TLRPC.TL_langPackLanguage)localObject1).lang_code.replace('-', '_').toLowerCase();
                  ((LocaleController.LocaleInfo)localObject2).pathToFile = "remote";
                  localObject1 = LocaleController.this.getLanguageFromDict(((LocaleController.LocaleInfo)localObject2).getKey());
                  if (localObject1 == null)
                  {
                    LocaleController.this.languages.add(localObject2);
                    LocaleController.this.languagesDict.put(((LocaleController.LocaleInfo)localObject2).getKey(), localObject2);
                    localObject1 = localObject2;
                  }
                  for (;;)
                  {
                    LocaleController.this.remoteLanguages.add(localObject2);
                    localHashMap.put(((LocaleController.LocaleInfo)localObject2).getKey(), localObject1);
                    i++;
                    break;
                    ((LocaleController.LocaleInfo)localObject1).nameEnglish = ((LocaleController.LocaleInfo)localObject2).nameEnglish;
                    ((LocaleController.LocaleInfo)localObject1).name = ((LocaleController.LocaleInfo)localObject2).name;
                    ((LocaleController.LocaleInfo)localObject1).pathToFile = ((LocaleController.LocaleInfo)localObject2).pathToFile;
                    localObject2 = localObject1;
                  }
                }
                i = 0;
                if (i < LocaleController.this.languages.size())
                {
                  localObject1 = (LocaleController.LocaleInfo)LocaleController.this.languages.get(i);
                  int j = i;
                  if (!((LocaleController.LocaleInfo)localObject1).isBuiltIn())
                  {
                    if (((LocaleController.LocaleInfo)localObject1).isRemote()) {
                      break label357;
                    }
                    j = i;
                  }
                  for (;;)
                  {
                    i = j + 1;
                    break;
                    label357:
                    j = i;
                    if ((LocaleController.LocaleInfo)localHashMap.get(((LocaleController.LocaleInfo)localObject1).getKey()) == null)
                    {
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("remove lang " + ((LocaleController.LocaleInfo)localObject1).getKey());
                      }
                      LocaleController.this.languages.remove(i);
                      LocaleController.this.languagesDict.remove(((LocaleController.LocaleInfo)localObject1).getKey());
                      i--;
                      j = i;
                      if (localObject1 == LocaleController.this.currentLocaleInfo)
                      {
                        if (LocaleController.this.systemDefaultLocale.getLanguage() != null) {
                          localObject1 = LocaleController.this.getLanguageFromDict(LocaleController.this.systemDefaultLocale.getLanguage());
                        }
                        localObject2 = localObject1;
                        if (localObject1 == null) {
                          localObject2 = LocaleController.this.getLanguageFromDict(LocaleController.access$1300(LocaleController.this, LocaleController.this.systemDefaultLocale));
                        }
                        localObject1 = localObject2;
                        if (localObject2 == null) {
                          localObject1 = LocaleController.this.getLanguageFromDict("en");
                        }
                        LocaleController.this.applyLanguage((LocaleController.LocaleInfo)localObject1, true, false, LocaleController.5.this.val$currentAccount);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                        j = i;
                      }
                    }
                  }
                }
                LocaleController.this.saveOtherLanguages();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
                LocaleController.this.applyLanguage(LocaleController.this.currentLocaleInfo, true, false, LocaleController.5.this.val$currentAccount);
              }
            });
          }
        }
      }, 8);
    }
  }
  
  public void onDeviceConfigurationChange(Configuration paramConfiguration)
  {
    if (this.changingConfiguration) {}
    for (;;)
    {
      return;
      is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
      this.systemDefaultLocale = paramConfiguration.locale;
      if (this.languageOverride != null)
      {
        paramConfiguration = this.currentLocaleInfo;
        this.currentLocaleInfo = null;
        applyLanguage(paramConfiguration, false, false, UserConfig.selectedAccount);
      }
      else
      {
        paramConfiguration = paramConfiguration.locale;
        if (paramConfiguration != null)
        {
          String str1 = paramConfiguration.getDisplayName();
          String str2 = this.currentLocale.getDisplayName();
          if ((str1 != null) && (str2 != null) && (!str1.equals(str2))) {
            recreateFormatters();
          }
          this.currentLocale = paramConfiguration;
          this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
          if (this.currentPluralRules == null) {
            this.currentPluralRules = ((PluralRules)this.allRules.get("en"));
          }
        }
      }
    }
  }
  
  public void recreateFormatters()
  {
    int i = 1;
    Object localObject1 = this.currentLocale;
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = Locale.getDefault();
    }
    String str1 = ((Locale)localObject2).getLanguage();
    localObject1 = str1;
    if (str1 == null) {
      localObject1 = "en";
    }
    localObject1 = ((String)localObject1).toLowerCase();
    boolean bool;
    label289:
    label307:
    String str2;
    if ((((String)localObject1).startsWith("ar")) || (((String)localObject1).startsWith("fa")) || ((BuildVars.DEBUG_VERSION) && ((((String)localObject1).startsWith("he")) || (((String)localObject1).startsWith("iw")))))
    {
      bool = true;
      isRTL = bool;
      if (((String)localObject1).equals("ko")) {
        i = 2;
      }
      nameDisplayOrder = i;
      this.formatterMonth = createFormatter((Locale)localObject2, getStringInternal("formatterMonth", NUM), "dd MMM");
      this.formatterYear = createFormatter((Locale)localObject2, getStringInternal("formatterYear", NUM), "dd.MM.yy");
      this.formatterYearMax = createFormatter((Locale)localObject2, getStringInternal("formatterYearMax", NUM), "dd.MM.yyyy");
      this.chatDate = createFormatter((Locale)localObject2, getStringInternal("chatDate", NUM), "d MMMM");
      this.chatFullDate = createFormatter((Locale)localObject2, getStringInternal("chatFullDate", NUM), "d MMMM yyyy");
      this.formatterWeek = createFormatter((Locale)localObject2, getStringInternal("formatterWeek", NUM), "EEE");
      this.formatterMonthYear = createFormatter((Locale)localObject2, getStringInternal("formatterMonthYear", NUM), "MMMM yyyy");
      if ((!((String)localObject1).toLowerCase().equals("ar")) && (!((String)localObject1).toLowerCase().equals("ko"))) {
        break label458;
      }
      localObject1 = localObject2;
      if (!is24HourFormat) {
        break label465;
      }
      str1 = getStringInternal("formatterDay24H", NUM);
      if (!is24HourFormat) {
        break label480;
      }
      str2 = "HH:mm";
      label318:
      this.formatterDay = createFormatter((Locale)localObject1, str1, str2);
      if (!is24HourFormat) {
        break label488;
      }
      localObject1 = getStringInternal("formatterStats24H", NUM);
      label348:
      if (!is24HourFormat) {
        break label502;
      }
      str1 = "MMM dd yyyy, HH:mm";
      label359:
      this.formatterStats = createFormatter((Locale)localObject2, (String)localObject1, str1);
      if (!is24HourFormat) {
        break label510;
      }
      localObject1 = getStringInternal("formatterBannedUntil24H", NUM);
      label388:
      if (!is24HourFormat) {
        break label524;
      }
      str1 = "MMM dd yyyy, HH:mm";
      label399:
      this.formatterBannedUntil = createFormatter((Locale)localObject2, (String)localObject1, str1);
      if (!is24HourFormat) {
        break label532;
      }
      localObject1 = getStringInternal("formatterBannedUntilThisYear24H", NUM);
      label428:
      if (!is24HourFormat) {
        break label546;
      }
    }
    label458:
    label465:
    label480:
    label488:
    label502:
    label510:
    label524:
    label532:
    label546:
    for (str1 = "MMM dd, HH:mm";; str1 = "MMM dd, h:mm a")
    {
      this.formatterBannedUntilThisYear = createFormatter((Locale)localObject2, (String)localObject1, str1);
      return;
      bool = false;
      break;
      localObject1 = Locale.US;
      break label289;
      str1 = getStringInternal("formatterDay12H", NUM);
      break label307;
      str2 = "h:mm a";
      break label318;
      localObject1 = getStringInternal("formatterStats12H", NUM);
      break label348;
      str1 = "MMM dd yyyy, h:mm a";
      break label359;
      localObject1 = getStringInternal("formatterBannedUntil12H", NUM);
      break label388;
      str1 = "MMM dd yyyy, h:mm a";
      break label399;
      localObject1 = getStringInternal("formatterBannedUntilThisYear12H", NUM);
      break label428;
    }
  }
  
  public void reloadCurrentRemoteLocale(int paramInt)
  {
    applyRemoteLanguage(this.currentLocaleInfo, true, paramInt);
  }
  
  public void saveRemoteLocaleStrings(TLRPC.TL_langPackDifference paramTL_langPackDifference, int paramInt)
  {
    if ((paramTL_langPackDifference == null) || (paramTL_langPackDifference.strings.isEmpty())) {}
    for (;;)
    {
      return;
      String str1 = paramTL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
      if (str1.equals(this.currentLocaleInfo.shortName))
      {
        File localFile = new File(ApplicationLoader.getFilesDirFixed(), "remote_" + str1 + ".xml");
        for (;;)
        {
          try
          {
            if (paramTL_langPackDifference.from_version == 0)
            {
              localObject1 = new java/util/HashMap;
              ((HashMap)localObject1).<init>();
              paramInt = 0;
              if (paramInt >= paramTL_langPackDifference.strings.size()) {
                break label616;
              }
              localObject2 = (TLRPC.LangPackString)paramTL_langPackDifference.strings.get(paramInt);
              if ((localObject2 instanceof TLRPC.TL_langPackString))
              {
                ((HashMap)localObject1).put(((TLRPC.LangPackString)localObject2).key, escapeString(((TLRPC.LangPackString)localObject2).value));
                paramInt++;
                continue;
              }
            }
            else
            {
              localObject1 = getLocaleFileStrings(localFile, true);
              continue;
            }
            if (!(localObject2 instanceof TLRPC.TL_langPackStringPluralized)) {
              break label594;
            }
            localObject3 = new java/lang/StringBuilder;
            ((StringBuilder)localObject3).<init>();
            String str2 = ((TLRPC.LangPackString)localObject2).key + "_zero";
            if (((TLRPC.LangPackString)localObject2).zero_value != null)
            {
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).zero_value);
              ((HashMap)localObject1).put(str2, localObject3);
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              str2 = ((TLRPC.LangPackString)localObject2).key + "_one";
              if (((TLRPC.LangPackString)localObject2).one_value == null) {
                break label554;
              }
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).one_value);
              ((HashMap)localObject1).put(str2, localObject3);
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              str2 = ((TLRPC.LangPackString)localObject2).key + "_two";
              if (((TLRPC.LangPackString)localObject2).two_value == null) {
                break label562;
              }
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).two_value);
              ((HashMap)localObject1).put(str2, localObject3);
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              str2 = ((TLRPC.LangPackString)localObject2).key + "_few";
              if (((TLRPC.LangPackString)localObject2).few_value == null) {
                break label570;
              }
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).few_value);
              ((HashMap)localObject1).put(str2, localObject3);
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              str2 = ((TLRPC.LangPackString)localObject2).key + "_many";
              if (((TLRPC.LangPackString)localObject2).many_value == null) {
                break label578;
              }
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).many_value);
              ((HashMap)localObject1).put(str2, localObject3);
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              str2 = ((TLRPC.LangPackString)localObject2).key + "_other";
              if (((TLRPC.LangPackString)localObject2).other_value == null) {
                break label586;
              }
              localObject3 = escapeString(((TLRPC.LangPackString)localObject2).other_value);
              ((HashMap)localObject1).put(str2, localObject3);
              continue;
            }
          }
          catch (Exception paramTL_langPackDifference) {}
          localObject3 = "";
          continue;
          label554:
          localObject3 = "";
          continue;
          label562:
          localObject3 = "";
          continue;
          label570:
          localObject3 = "";
          continue;
          label578:
          localObject3 = "";
          continue;
          label586:
          localObject3 = "";
          continue;
          label594:
          if ((localObject2 instanceof TLRPC.TL_langPackStringDeleted)) {
            ((HashMap)localObject1).remove(((TLRPC.LangPackString)localObject2).key);
          }
        }
        label616:
        if (BuildVars.LOGS_ENABLED)
        {
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          FileLog.d("save locale file to " + localFile);
        }
        Object localObject3 = new java/io/BufferedWriter;
        Object localObject2 = new java/io/FileWriter;
        ((FileWriter)localObject2).<init>(localFile);
        ((BufferedWriter)localObject3).<init>((Writer)localObject2);
        ((BufferedWriter)localObject3).write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        ((BufferedWriter)localObject3).write("<resources>\n");
        Object localObject1 = ((HashMap)localObject1).entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          ((BufferedWriter)localObject3).write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[] { ((Map.Entry)localObject2).getKey(), ((Map.Entry)localObject2).getValue() }));
        }
        ((BufferedWriter)localObject3).write("</resources>");
        ((BufferedWriter)localObject3).close();
        localObject1 = getLocaleFileStrings(localFile);
        localObject3 = new org/telegram/messenger/LocaleController$4;
        ((4)localObject3).<init>(this, str1, paramTL_langPackDifference, (HashMap)localObject1);
        AndroidUtilities.runOnUIThread((Runnable)localObject3);
      }
    }
  }
  
  public static class LocaleInfo
  {
    public boolean builtIn;
    public String name;
    public String nameEnglish;
    public String pathToFile;
    public String shortName;
    public int version;
    
    public static LocaleInfo createWithString(String paramString)
    {
      if ((paramString == null) || (paramString.length() == 0)) {
        paramString = null;
      }
      for (;;)
      {
        return paramString;
        String[] arrayOfString = paramString.split("\\|");
        paramString = null;
        if (arrayOfString.length >= 4)
        {
          LocaleInfo localLocaleInfo = new LocaleInfo();
          localLocaleInfo.name = arrayOfString[0];
          localLocaleInfo.nameEnglish = arrayOfString[1];
          localLocaleInfo.shortName = arrayOfString[2].toLowerCase();
          localLocaleInfo.pathToFile = arrayOfString[3];
          paramString = localLocaleInfo;
          if (arrayOfString.length >= 5)
          {
            localLocaleInfo.version = Utilities.parseInt(arrayOfString[4]).intValue();
            paramString = localLocaleInfo;
          }
        }
      }
    }
    
    public String getKey()
    {
      if ((this.pathToFile != null) && (!"remote".equals(this.pathToFile))) {}
      for (String str = "local_" + this.shortName;; str = this.shortName) {
        return str;
      }
    }
    
    public File getPathToFile()
    {
      File localFile;
      if (isRemote()) {
        localFile = new File(ApplicationLoader.getFilesDirFixed(), "remote_" + this.shortName + ".xml");
      }
      for (;;)
      {
        return localFile;
        if (!TextUtils.isEmpty(this.pathToFile)) {
          localFile = new File(this.pathToFile);
        } else {
          localFile = null;
        }
      }
    }
    
    public String getSaveString()
    {
      return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version;
    }
    
    public boolean isBuiltIn()
    {
      return this.builtIn;
    }
    
    public boolean isLocal()
    {
      if ((!TextUtils.isEmpty(this.pathToFile)) && (!isRemote())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean isRemote()
    {
      return "remote".equals(this.pathToFile);
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
      int i = 1;
      int j = paramInt % 100;
      if (paramInt == 0) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == 1) {
          paramInt = 2;
        } else if (paramInt == 2) {
          paramInt = 4;
        } else if ((j >= 3) && (j <= 10)) {
          paramInt = 8;
        } else if ((j >= 11) && (j <= 99)) {
          paramInt = 16;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Balkan
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      int j = paramInt % 100;
      paramInt %= 10;
      if ((paramInt == 1) && (j != 11)) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt >= 2) && (paramInt <= 4) && ((j < 12) || (j > 14))) {
          paramInt = 8;
        } else if ((paramInt == 0) || ((paramInt >= 5) && (paramInt <= 9)) || ((j >= 11) && (j <= 14))) {
          paramInt = 16;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Breton
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 1;
      if (paramInt == 0) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == 1) {
          paramInt = 2;
        } else if (paramInt == 2) {
          paramInt = 4;
        } else if (paramInt == 3) {
          paramInt = 8;
        } else if (paramInt == 6) {
          paramInt = 16;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Czech
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if (paramInt == 1) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt >= 2) && (paramInt <= 4)) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_French
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if ((paramInt >= 0) && (paramInt < 2)) {}
      for (paramInt = i;; paramInt = 0) {
        return paramInt;
      }
    }
  }
  
  public static class PluralRules_Langi
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if (paramInt == 0) {}
      for (i = 1;; i = 0) {
        do
        {
          return i;
        } while ((paramInt > 0) && (paramInt < 2));
      }
    }
  }
  
  public static class PluralRules_Latvian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 1;
      if (paramInt == 0) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt % 10 == 1) && (paramInt % 100 != 11)) {
          paramInt = 2;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Lithuanian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      int j = paramInt % 100;
      int k = paramInt % 10;
      if (k == 1)
      {
        paramInt = i;
        if (j >= 11)
        {
          if (j <= 19) {
            break label37;
          }
          paramInt = i;
        }
      }
      for (;;)
      {
        return paramInt;
        label37:
        if ((k >= 2) && (k <= 9) && ((j < 11) || (j > 19))) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Macedonian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt % 10 == 1) && (paramInt != 11)) {}
      for (paramInt = 2;; paramInt = 0) {
        return paramInt;
      }
    }
  }
  
  public static class PluralRules_Maltese
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      int j = paramInt % 100;
      if (paramInt == 1) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == 0) || ((j >= 2) && (j <= 10))) {
          paramInt = 8;
        } else if ((j >= 11) && (j <= 19)) {
          paramInt = 16;
        } else {
          paramInt = 0;
        }
      }
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
      if (paramInt == 1) {}
      for (paramInt = 2;; paramInt = 0) {
        return paramInt;
      }
    }
  }
  
  public static class PluralRules_Polish
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      int j = paramInt % 100;
      int k = paramInt % 10;
      if (paramInt == 1) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((k >= 2) && (k <= 4) && ((j < 12) || (j > 14)) && ((j < 22) || (j > 24))) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Romanian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 1) {
        paramInt = 2;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == 0) || ((i >= 1) && (i <= 19))) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Slovenian
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      paramInt %= 100;
      if (paramInt == 1) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == 2) {
          paramInt = 4;
        } else if ((paramInt >= 3) && (paramInt <= 4)) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Tachelhit
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if ((paramInt >= 0) && (paramInt <= 1)) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt >= 2) && (paramInt <= 10)) {
          paramInt = 8;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Two
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if (paramInt == 1) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == 2) {
          paramInt = 4;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Welsh
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 1;
      if (paramInt == 0) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == 1) {
          paramInt = 2;
        } else if (paramInt == 2) {
          paramInt = 4;
        } else if (paramInt == 3) {
          paramInt = 8;
        } else if (paramInt == 6) {
          paramInt = 16;
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public static class PluralRules_Zero
    extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt == 0) || (paramInt == 1)) {}
      for (paramInt = 2;; paramInt = 0) {
        return paramInt;
      }
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