package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder;
import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;

public class ConnectionsManager
{
  public static final int ConnectionStateConnected = 3;
  public static final int ConnectionStateConnecting = 1;
  public static final int ConnectionStateConnectingToProxy = 4;
  public static final int ConnectionStateUpdating = 5;
  public static final int ConnectionStateWaitingForNetwork = 2;
  public static final int ConnectionTypeDownload = 2;
  public static final int ConnectionTypeDownload2 = 65538;
  public static final int ConnectionTypeGeneric = 1;
  public static final int ConnectionTypePush = 8;
  public static final int ConnectionTypeUpload = 4;
  public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
  public static final int FileTypeAudio = 50331648;
  public static final int FileTypeFile = 67108864;
  public static final int FileTypePhoto = 16777216;
  public static final int FileTypeVideo = 33554432;
  private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
  public static final int RequestFlagCanCompress = 4;
  public static final int RequestFlagEnableUnauthorized = 1;
  public static final int RequestFlagFailOnServerErrors = 2;
  public static final int RequestFlagForceDownload = 32;
  public static final int RequestFlagInvokeAfter = 64;
  public static final int RequestFlagNeedQuickAck = 128;
  public static final int RequestFlagTryDifferentDc = 16;
  public static final int RequestFlagWithoutLogin = 8;
  private static AsyncTask currentTask;
  private static ThreadLocal<HashMap<String, ResolvedDomain>> dnsCache = new ThreadLocal()
  {
    protected HashMap<String, ConnectionsManager.ResolvedDomain> initialValue()
    {
      return new HashMap();
    }
  };
  private static final int dnsConfigVersion = 0;
  private static int lastClassGuid = 1;
  private static long lastDnsRequestTime;
  private boolean appPaused = true;
  private int appResumeCount;
  private int connectionState;
  private int currentAccount;
  private boolean isUpdating;
  private long lastPauseTime = System.currentTimeMillis();
  private AtomicInteger lastRequestToken = new AtomicInteger(1);
  
  public ConnectionsManager(int paramInt)
  {
    this.currentAccount = paramInt;
    this.connectionState = native_getConnectionState(this.currentAccount);
    Object localObject1 = ApplicationLoader.getFilesDirFixed();
    Object localObject2 = localObject1;
    if (paramInt != 0)
    {
      localObject2 = new File((File)localObject1, "account" + paramInt);
      ((File)localObject2).mkdirs();
    }
    String str2 = ((File)localObject2).toString();
    boolean bool = MessagesController.getGlobalNotificationsSettings().getBoolean("pushConnection", true);
    try
    {
      localObject3 = LocaleController.getSystemLocaleStringIso639().toLowerCase();
      str3 = LocaleController.getLocaleStringIso639().toLowerCase();
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      localObject4 = Build.MANUFACTURER + Build.MODEL;
      localObject2 = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
      localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      localObject1 = ((PackageInfo)localObject2).versionName + " (" + ((PackageInfo)localObject2).versionCode + ")";
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      localObject2 = "SDK " + Build.VERSION.SDK_INT;
      Object localObject5 = localObject3;
      if (((String)localObject3).trim().length() == 0) {
        localObject5 = "en";
      }
      localObject3 = localObject4;
      if (((String)localObject4).trim().length() == 0) {
        localObject3 = "Android unknown";
      }
      localObject4 = localObject1;
      if (((String)localObject1).trim().length() == 0) {
        localObject4 = "App version unknown";
      }
      localObject1 = localObject2;
      if (((String)localObject2).trim().length() == 0) {
        localObject1 = "SDK Unknown";
      }
      UserConfig.getInstance(this.currentAccount).loadConfig();
      init(BuildVars.BUILD_VERSION, 76, BuildVars.APP_ID, (String)localObject3, (String)localObject1, (String)localObject4, str3, (String)localObject5, str2, FileLog.getNetworkLogPath(), UserConfig.getInstance(this.currentAccount).getClientUserId(), bool);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject3 = "en";
        String str3 = "";
        Object localObject4 = "Android unknown";
        localObject1 = "App version unknown";
        String str1 = "SDK " + Build.VERSION.SDK_INT;
      }
    }
  }
  
  private void checkConnection()
  {
    native_setUseIpv6(this.currentAccount, useIpv6Address());
    native_setNetworkAvailable(this.currentAccount, isNetworkOnline(), getCurrentNetworkType());
  }
  
  public static int generateClassGuid()
  {
    int i = lastClassGuid;
    lastClassGuid = i + 1;
    return i;
  }
  
  public static int getCurrentNetworkType()
  {
    int i;
    if (isConnectedOrConnectingToWiFi()) {
      i = 1;
    }
    for (;;)
    {
      return i;
      if (isRoaming()) {
        i = 2;
      } else {
        i = 0;
      }
    }
  }
  
  /* Error */
  public static String getHostByName(String paramString, int paramInt)
  {
    // Byte code:
    //   0: getstatic 122	org/telegram/tgnet/ConnectionsManager:dnsCache	Ljava/lang/ThreadLocal;
    //   3: invokevirtual 368	java/lang/ThreadLocal:get	()Ljava/lang/Object;
    //   6: checkcast 370	java/util/HashMap
    //   9: astore_2
    //   10: aload_2
    //   11: aload_0
    //   12: invokevirtual 373	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: checkcast 53	org/telegram/tgnet/ConnectionsManager$ResolvedDomain
    //   18: astore_3
    //   19: aload_3
    //   20: ifnull +25 -> 45
    //   23: invokestatic 378	android/os/SystemClock:uptimeMillis	()J
    //   26: aload_3
    //   27: getfield 381	org/telegram/tgnet/ConnectionsManager$ResolvedDomain:ttl	J
    //   30: lsub
    //   31: ldc2_w 382
    //   34: lcmp
    //   35: ifge +10 -> 45
    //   38: aload_3
    //   39: getfield 386	org/telegram/tgnet/ConnectionsManager$ResolvedDomain:address	Ljava/lang/String;
    //   42: astore_0
    //   43: aload_0
    //   44: areturn
    //   45: aconst_null
    //   46: astore 4
    //   48: aconst_null
    //   49: astore 5
    //   51: aconst_null
    //   52: astore 6
    //   54: aconst_null
    //   55: astore_3
    //   56: aload_3
    //   57: astore 7
    //   59: aload 4
    //   61: astore 8
    //   63: aload 6
    //   65: astore 9
    //   67: new 388	java/net/URL
    //   70: astore 10
    //   72: aload_3
    //   73: astore 7
    //   75: aload 4
    //   77: astore 8
    //   79: aload 6
    //   81: astore 9
    //   83: new 165	java/lang/StringBuilder
    //   86: astore 11
    //   88: aload_3
    //   89: astore 7
    //   91: aload 4
    //   93: astore 8
    //   95: aload 6
    //   97: astore 9
    //   99: aload 11
    //   101: invokespecial 166	java/lang/StringBuilder:<init>	()V
    //   104: aload_3
    //   105: astore 7
    //   107: aload 4
    //   109: astore 8
    //   111: aload 6
    //   113: astore 9
    //   115: aload 10
    //   117: aload 11
    //   119: ldc_w 390
    //   122: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: aload_0
    //   126: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: ldc_w 392
    //   132: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: invokespecial 395	java/net/URL:<init>	(Ljava/lang/String;)V
    //   141: aload_3
    //   142: astore 7
    //   144: aload 4
    //   146: astore 8
    //   148: aload 6
    //   150: astore 9
    //   152: aload 10
    //   154: invokevirtual 399	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   157: astore 10
    //   159: aload_3
    //   160: astore 7
    //   162: aload 4
    //   164: astore 8
    //   166: aload 6
    //   168: astore 9
    //   170: aload 10
    //   172: ldc_w 401
    //   175: ldc_w 403
    //   178: invokevirtual 409	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   181: aload_3
    //   182: astore 7
    //   184: aload 4
    //   186: astore 8
    //   188: aload 6
    //   190: astore 9
    //   192: aload 10
    //   194: ldc_w 411
    //   197: ldc_w 413
    //   200: invokevirtual 409	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   203: aload_3
    //   204: astore 7
    //   206: aload 4
    //   208: astore 8
    //   210: aload 6
    //   212: astore 9
    //   214: aload 10
    //   216: sipush 1000
    //   219: invokevirtual 416	java/net/URLConnection:setConnectTimeout	(I)V
    //   222: aload_3
    //   223: astore 7
    //   225: aload 4
    //   227: astore 8
    //   229: aload 6
    //   231: astore 9
    //   233: aload 10
    //   235: sipush 2000
    //   238: invokevirtual 419	java/net/URLConnection:setReadTimeout	(I)V
    //   241: aload_3
    //   242: astore 7
    //   244: aload 4
    //   246: astore 8
    //   248: aload 6
    //   250: astore 9
    //   252: aload 10
    //   254: invokevirtual 422	java/net/URLConnection:connect	()V
    //   257: aload_3
    //   258: astore 7
    //   260: aload 4
    //   262: astore 8
    //   264: aload 6
    //   266: astore 9
    //   268: aload 10
    //   270: invokevirtual 426	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   273: astore_3
    //   274: aload_3
    //   275: astore 7
    //   277: aload 4
    //   279: astore 8
    //   281: aload_3
    //   282: astore 9
    //   284: new 428	java/io/ByteArrayOutputStream
    //   287: astore 6
    //   289: aload_3
    //   290: astore 7
    //   292: aload 4
    //   294: astore 8
    //   296: aload_3
    //   297: astore 9
    //   299: aload 6
    //   301: invokespecial 429	java/io/ByteArrayOutputStream:<init>	()V
    //   304: ldc_w 430
    //   307: newarray <illegal type>
    //   309: astore 7
    //   311: aload_3
    //   312: aload 7
    //   314: invokevirtual 436	java/io/InputStream:read	([B)I
    //   317: istore_1
    //   318: iload_1
    //   319: ifle +58 -> 377
    //   322: aload 6
    //   324: aload 7
    //   326: iconst_0
    //   327: iload_1
    //   328: invokevirtual 440	java/io/ByteArrayOutputStream:write	([BII)V
    //   331: goto -20 -> 311
    //   334: astore 7
    //   336: aload 6
    //   338: astore_0
    //   339: aload 7
    //   341: astore 6
    //   343: aload_3
    //   344: astore 7
    //   346: aload_0
    //   347: astore 8
    //   349: aload 6
    //   351: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   354: aload_3
    //   355: ifnull +7 -> 362
    //   358: aload_3
    //   359: invokevirtual 447	java/io/InputStream:close	()V
    //   362: aload_0
    //   363: ifnull +7 -> 370
    //   366: aload_0
    //   367: invokevirtual 448	java/io/ByteArrayOutputStream:close	()V
    //   370: ldc_w 307
    //   373: astore_0
    //   374: goto -331 -> 43
    //   377: iload_1
    //   378: iconst_m1
    //   379: if_icmpne +3 -> 382
    //   382: new 450	org/json/JSONObject
    //   385: astore 8
    //   387: new 208	java/lang/String
    //   390: astore 7
    //   392: aload 7
    //   394: aload 6
    //   396: invokevirtual 454	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   399: invokespecial 457	java/lang/String:<init>	([B)V
    //   402: aload 8
    //   404: aload 7
    //   406: invokespecial 458	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   409: aload 8
    //   411: ldc_w 460
    //   414: invokevirtual 464	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   417: astore 7
    //   419: aload 7
    //   421: invokevirtual 467	org/json/JSONArray:length	()I
    //   424: ifle +92 -> 516
    //   427: aload 7
    //   429: getstatic 473	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   432: aload 7
    //   434: invokevirtual 467	org/json/JSONArray:length	()I
    //   437: invokevirtual 478	java/security/SecureRandom:nextInt	(I)I
    //   440: invokevirtual 482	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   443: ldc_w 484
    //   446: invokevirtual 488	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   449: astore 7
    //   451: new 53	org/telegram/tgnet/ConnectionsManager$ResolvedDomain
    //   454: astore 8
    //   456: aload 8
    //   458: aload 7
    //   460: invokestatic 378	android/os/SystemClock:uptimeMillis	()J
    //   463: invokespecial 491	org/telegram/tgnet/ConnectionsManager$ResolvedDomain:<init>	(Ljava/lang/String;J)V
    //   466: aload_2
    //   467: aload_0
    //   468: aload 8
    //   470: invokevirtual 495	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   473: pop
    //   474: aload_3
    //   475: ifnull +7 -> 482
    //   478: aload_3
    //   479: invokevirtual 447	java/io/InputStream:close	()V
    //   482: aload 7
    //   484: astore_0
    //   485: aload 6
    //   487: ifnull -444 -> 43
    //   490: aload 6
    //   492: invokevirtual 448	java/io/ByteArrayOutputStream:close	()V
    //   495: aload 7
    //   497: astore_0
    //   498: goto -455 -> 43
    //   501: astore_0
    //   502: aload 7
    //   504: astore_0
    //   505: goto -462 -> 43
    //   508: astore_0
    //   509: aload_0
    //   510: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   513: goto -31 -> 482
    //   516: aload_3
    //   517: ifnull +7 -> 524
    //   520: aload_3
    //   521: invokevirtual 447	java/io/InputStream:close	()V
    //   524: aload 6
    //   526: ifnull +8 -> 534
    //   529: aload 6
    //   531: invokevirtual 448	java/io/ByteArrayOutputStream:close	()V
    //   534: goto -164 -> 370
    //   537: astore_0
    //   538: aload_0
    //   539: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   542: goto -18 -> 524
    //   545: astore_0
    //   546: goto -176 -> 370
    //   549: astore_3
    //   550: aload_3
    //   551: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   554: goto -192 -> 362
    //   557: astore_0
    //   558: aload 7
    //   560: astore_3
    //   561: aload_3
    //   562: ifnull +7 -> 569
    //   565: aload_3
    //   566: invokevirtual 447	java/io/InputStream:close	()V
    //   569: aload 8
    //   571: ifnull +8 -> 579
    //   574: aload 8
    //   576: invokevirtual 448	java/io/ByteArrayOutputStream:close	()V
    //   579: aload_0
    //   580: athrow
    //   581: astore_3
    //   582: aload_3
    //   583: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   586: goto -17 -> 569
    //   589: astore_0
    //   590: goto -220 -> 370
    //   593: astore_3
    //   594: goto -15 -> 579
    //   597: astore_0
    //   598: aload 6
    //   600: astore 8
    //   602: goto -41 -> 561
    //   605: astore 6
    //   607: aload 9
    //   609: astore_3
    //   610: aload 5
    //   612: astore_0
    //   613: goto -270 -> 343
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	616	0	paramString	String
    //   0	616	1	paramInt	int
    //   9	458	2	localHashMap	HashMap
    //   18	503	3	localObject1	Object
    //   549	2	3	localThrowable1	Throwable
    //   560	6	3	localObject2	Object
    //   581	2	3	localThrowable2	Throwable
    //   593	1	3	localException	Exception
    //   609	1	3	localObject3	Object
    //   46	247	4	localObject4	Object
    //   49	562	5	localObject5	Object
    //   52	547	6	localObject6	Object
    //   605	1	6	localThrowable3	Throwable
    //   57	268	7	localObject7	Object
    //   334	6	7	localThrowable4	Throwable
    //   344	215	7	localObject8	Object
    //   61	540	8	localObject9	Object
    //   65	543	9	localObject10	Object
    //   70	199	10	localObject11	Object
    //   86	32	11	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   304	311	334	java/lang/Throwable
    //   311	318	334	java/lang/Throwable
    //   322	331	334	java/lang/Throwable
    //   382	474	334	java/lang/Throwable
    //   490	495	501	java/lang/Exception
    //   478	482	508	java/lang/Throwable
    //   520	524	537	java/lang/Throwable
    //   529	534	545	java/lang/Exception
    //   358	362	549	java/lang/Throwable
    //   67	72	557	finally
    //   83	88	557	finally
    //   99	104	557	finally
    //   115	141	557	finally
    //   152	159	557	finally
    //   170	181	557	finally
    //   192	203	557	finally
    //   214	222	557	finally
    //   233	241	557	finally
    //   252	257	557	finally
    //   268	274	557	finally
    //   284	289	557	finally
    //   299	304	557	finally
    //   349	354	557	finally
    //   565	569	581	java/lang/Throwable
    //   366	370	589	java/lang/Exception
    //   574	579	593	java/lang/Exception
    //   304	311	597	finally
    //   311	318	597	finally
    //   322	331	597	finally
    //   382	474	597	finally
    //   67	72	605	java/lang/Throwable
    //   83	88	605	java/lang/Throwable
    //   99	104	605	java/lang/Throwable
    //   115	141	605	java/lang/Throwable
    //   152	159	605	java/lang/Throwable
    //   170	181	605	java/lang/Throwable
    //   192	203	605	java/lang/Throwable
    //   214	222	605	java/lang/Throwable
    //   233	241	605	java/lang/Throwable
    //   252	257	605	java/lang/Throwable
    //   268	274	605	java/lang/Throwable
    //   284	289	605	java/lang/Throwable
    //   299	304	605	java/lang/Throwable
  }
  
  public static ConnectionsManager getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/tgnet/ConnectionsManager;
        ((ConnectionsManager)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (ConnectionsManager)localObject2;
    }
    finally {}
  }
  
  public static boolean isConnectedOrConnectingToWiFi()
  {
    boolean bool1 = true;
    try
    {
      Object localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
      NetworkInfo.State localState = ((NetworkInfo)localObject).getState();
      if (localObject == null) {
        break label64;
      }
      bool2 = bool1;
      if (localState != NetworkInfo.State.CONNECTED)
      {
        bool2 = bool1;
        if (localState != NetworkInfo.State.CONNECTING)
        {
          localObject = NetworkInfo.State.SUSPENDED;
          if (localState != localObject) {
            break label64;
          }
          bool2 = bool1;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        boolean bool2 = false;
      }
    }
    return bool2;
  }
  
  public static boolean isConnectedToWiFi()
  {
    bool = true;
    try
    {
      Object localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
      if (localObject == null) {
        break label44;
      }
      localObject = ((NetworkInfo)localObject).getState();
      NetworkInfo.State localState = NetworkInfo.State.CONNECTED;
      if (localObject != localState) {
        break label44;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        bool = false;
      }
    }
    return bool;
  }
  
  public static boolean isNetworkOnline()
  {
    bool1 = true;
    for (;;)
    {
      try
      {
        localObject = (ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity");
        localNetworkInfo = ((ConnectivityManager)localObject).getActiveNetworkInfo();
        if (localNetworkInfo == null) {
          continue;
        }
        bool2 = bool1;
        if (!localNetworkInfo.isConnectedOrConnecting())
        {
          if (!localNetworkInfo.isAvailable()) {
            continue;
          }
          bool2 = bool1;
        }
      }
      catch (Exception localException)
      {
        Object localObject;
        NetworkInfo localNetworkInfo;
        boolean bool3;
        FileLog.e(localException);
        boolean bool2 = bool1;
        continue;
      }
      return bool2;
      localNetworkInfo = ((ConnectivityManager)localObject).getNetworkInfo(0);
      if (localNetworkInfo != null)
      {
        bool2 = bool1;
        if (localNetworkInfo.isConnectedOrConnecting()) {}
      }
      else
      {
        localObject = ((ConnectivityManager)localObject).getNetworkInfo(1);
        if (localObject != null)
        {
          bool3 = ((NetworkInfo)localObject).isConnectedOrConnecting();
          bool2 = bool1;
          if (bool3) {}
        }
        else
        {
          bool2 = false;
        }
      }
    }
  }
  
  public static boolean isRoaming()
  {
    try
    {
      NetworkInfo localNetworkInfo = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
      if (localNetworkInfo == null) {
        break label32;
      }
      bool = localNetworkInfo.isRoaming();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        label32:
        boolean bool = false;
      }
    }
    return bool;
  }
  
  public static native void native_applyDatacenterAddress(int paramInt1, int paramInt2, String paramString, int paramInt3);
  
  public static native void native_applyDnsConfig(int paramInt, long paramLong);
  
  public static native void native_bindRequestToGuid(int paramInt1, int paramInt2, int paramInt3);
  
  public static native void native_cancelRequest(int paramInt1, int paramInt2, boolean paramBoolean);
  
  public static native void native_cancelRequestsForGuid(int paramInt1, int paramInt2);
  
  public static native void native_cleanUp(int paramInt);
  
  public static native int native_getConnectionState(int paramInt);
  
  public static native int native_getCurrentTime(int paramInt);
  
  public static native long native_getCurrentTimeMillis(int paramInt);
  
  public static native int native_getTimeDifference(int paramInt);
  
  public static native void native_init(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt5, boolean paramBoolean1, boolean paramBoolean2, int paramInt6);
  
  public static native int native_isTestBackend(int paramInt);
  
  public static native void native_pauseNetwork(int paramInt);
  
  public static native void native_resumeNetwork(int paramInt, boolean paramBoolean);
  
  public static native void native_sendRequest(int paramInt1, long paramLong, RequestDelegateInternal paramRequestDelegateInternal, QuickAckDelegate paramQuickAckDelegate, WriteToSocketDelegate paramWriteToSocketDelegate, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5);
  
  public static native void native_setJava(boolean paramBoolean);
  
  public static native void native_setLangCode(int paramInt, String paramString);
  
  public static native void native_setNetworkAvailable(int paramInt1, boolean paramBoolean, int paramInt2);
  
  public static native void native_setProxySettings(int paramInt1, String paramString1, int paramInt2, String paramString2, String paramString3);
  
  public static native void native_setPushConnectionEnabled(int paramInt, boolean paramBoolean);
  
  public static native void native_setUseIpv6(int paramInt, boolean paramBoolean);
  
  public static native void native_setUserId(int paramInt1, int paramInt2);
  
  public static native void native_switchBackend(int paramInt);
  
  public static native void native_updateDcSettings(int paramInt);
  
  public static void onBytesReceived(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      StatsController.getInstance(paramInt3).incrementReceivedBytesCount(paramInt2, 6, paramInt1);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static void onBytesSent(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      StatsController.getInstance(paramInt3).incrementSentBytesCount(paramInt2, 6, paramInt1);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static void onConnectionStateChanged(final int paramInt1, int paramInt2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ConnectionsManager.access$202(ConnectionsManager.getInstance(this.val$currentAccount), paramInt1);
        NotificationCenter.getInstance(this.val$currentAccount).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
      }
    });
  }
  
  public static void onInternalPushReceived(int paramInt) {}
  
  public static void onLogout(int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (UserConfig.getInstance(this.val$currentAccount).getClientUserId() != 0)
        {
          UserConfig.getInstance(this.val$currentAccount).clearConfig();
          MessagesController.getInstance(this.val$currentAccount).performLogout(false);
        }
      }
    });
  }
  
  public static void onRequestNewServerIpAndPort(int paramInt1, final int paramInt2)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((ConnectionsManager.currentTask != null) || ((this.val$second == 0) && (Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()) < 10000L)) || (!ConnectionsManager.isNetworkOnline())) {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("don't start task, current task = " + ConnectionsManager.currentTask + " next task = " + this.val$second + " time diff = " + Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ConnectionsManager.isNetworkOnline());
          }
        }
        for (;;)
        {
          return;
          ConnectionsManager.access$402(System.currentTimeMillis());
          Object localObject;
          if (this.val$second == 2)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("start dns txt task");
            }
            localObject = new ConnectionsManager.DnsTxtLoadTask(paramInt2);
            ((ConnectionsManager.DnsTxtLoadTask)localObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            ConnectionsManager.access$302((AsyncTask)localObject);
          }
          else if (this.val$second == 1)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("start azure dns task");
            }
            localObject = new ConnectionsManager.AzureLoadTask(paramInt2);
            ((ConnectionsManager.AzureLoadTask)localObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            ConnectionsManager.access$302((AsyncTask)localObject);
          }
          else
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("start firebase task");
            }
            localObject = new ConnectionsManager.FirebaseTask(paramInt2);
            ((ConnectionsManager.FirebaseTask)localObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            ConnectionsManager.access$302((AsyncTask)localObject);
          }
        }
      }
    });
  }
  
  public static void onSessionCreated(int paramInt)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance(this.val$currentAccount).getDifference();
      }
    });
  }
  
  public static void onUnparsedMessageReceived(long paramLong, int paramInt)
  {
    try
    {
      Object localObject1 = NativeByteBuffer.wrap(paramLong);
      ((NativeByteBuffer)localObject1).reused = true;
      localObject1 = TLClassStore.Instance().TLdeserialize((NativeByteBuffer)localObject1, ((NativeByteBuffer)localObject1).readInt32(true), true);
      if ((localObject1 instanceof TLRPC.Updates))
      {
        if (BuildVars.LOGS_ENABLED)
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          FileLog.d("java received " + localObject1);
        }
        KeepAliveJob.finishJob();
        DispatchQueue localDispatchQueue = Utilities.stageQueue;
        Object localObject2 = new org/telegram/tgnet/ConnectionsManager$4;
        ((4)localObject2).<init>(paramInt, (TLObject)localObject1);
        localDispatchQueue.postRunnable((Runnable)localObject2);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static void onUpdate(int paramInt)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance(this.val$currentAccount).updateTimerProc();
      }
    });
  }
  
  public static void onUpdateConfig(long paramLong, int paramInt)
  {
    try
    {
      Object localObject = NativeByteBuffer.wrap(paramLong);
      ((NativeByteBuffer)localObject).reused = true;
      TLRPC.TL_config localTL_config = TLRPC.TL_config.TLdeserialize((AbstractSerializedData)localObject, ((NativeByteBuffer)localObject).readInt32(true), true);
      if (localTL_config != null)
      {
        localObject = Utilities.stageQueue;
        Runnable local10 = new org/telegram/tgnet/ConnectionsManager$10;
        local10.<init>(paramInt, localTL_config);
        ((DispatchQueue)localObject).postRunnable(local10);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static void setLangCode(String paramString)
  {
    paramString = paramString.replace('_', '-').toLowerCase();
    for (int i = 0; i < 3; i++) {
      native_setLangCode(i, paramString);
    }
  }
  
  @SuppressLint({"NewApi"})
  protected static boolean useIpv6Address()
  {
    boolean bool1 = false;
    boolean bool2;
    if (Build.VERSION.SDK_INT < 19) {
      bool2 = bool1;
    }
    label112:
    label204:
    label384:
    for (;;)
    {
      return bool2;
      if (BuildVars.LOGS_ENABLED) {
        try
        {
          Enumeration localEnumeration1 = NetworkInterface.getNetworkInterfaces();
          Object localObject1;
          Object localObject2;
          int i;
          while (localEnumeration1.hasMoreElements())
          {
            localObject1 = (NetworkInterface)localEnumeration1.nextElement();
            if ((((NetworkInterface)localObject1).isUp()) && (!((NetworkInterface)localObject1).isLoopback()) && (!((NetworkInterface)localObject1).getInterfaceAddresses().isEmpty()))
            {
              if (BuildVars.LOGS_ENABLED)
              {
                localObject2 = new java/lang/StringBuilder;
                ((StringBuilder)localObject2).<init>();
                FileLog.d("valid interface: " + localObject1);
              }
              List localList = ((NetworkInterface)localObject1).getInterfaceAddresses();
              i = 0;
              if (i < localList.size())
              {
                localObject1 = ((InterfaceAddress)localList.get(i)).getAddress();
                if (BuildVars.LOGS_ENABLED)
                {
                  localObject2 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject2).<init>();
                  FileLog.d("address: " + ((InetAddress)localObject1).getHostAddress());
                }
                if ((!((InetAddress)localObject1).isLinkLocalAddress()) && (!((InetAddress)localObject1).isLoopbackAddress()) && (!((InetAddress)localObject1).isMulticastAddress())) {
                  break label204;
                }
              }
              for (;;)
              {
                i++;
                break label112;
                break;
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("address is good");
                }
              }
            }
          }
          try
          {
            Enumeration localEnumeration2 = NetworkInterface.getNetworkInterfaces();
            int j = 0;
            int k = 0;
            while (localEnumeration2.hasMoreElements())
            {
              localObject2 = (NetworkInterface)localEnumeration2.nextElement();
              if ((((NetworkInterface)localObject2).isUp()) && (!((NetworkInterface)localObject2).isLoopback()))
              {
                localObject1 = ((NetworkInterface)localObject2).getInterfaceAddresses();
                int m = 0;
                i = k;
                int n = j;
                j = n;
                k = i;
                if (m < ((List)localObject1).size())
                {
                  localObject2 = ((InterfaceAddress)((List)localObject1).get(m)).getAddress();
                  k = n;
                  j = i;
                  if (!((InetAddress)localObject2).isLinkLocalAddress())
                  {
                    k = n;
                    j = i;
                    if (!((InetAddress)localObject2).isLoopbackAddress())
                    {
                      if (!((InetAddress)localObject2).isMulticastAddress()) {
                        break label384;
                      }
                      j = i;
                      k = n;
                    }
                  }
                  for (;;)
                  {
                    m++;
                    n = k;
                    i = j;
                    break;
                    if ((localObject2 instanceof Inet6Address))
                    {
                      j = 1;
                      k = n;
                    }
                    else
                    {
                      k = n;
                      j = i;
                      if ((localObject2 instanceof Inet4Address))
                      {
                        bool2 = ((InetAddress)localObject2).getHostAddress().startsWith("192.0.0.");
                        k = n;
                        j = i;
                        if (!bool2)
                        {
                          k = 1;
                          j = i;
                        }
                      }
                    }
                  }
                }
              }
            }
            bool2 = bool1;
            if (j == 0)
            {
              bool2 = bool1;
              if (k != 0) {
                bool2 = true;
              }
            }
          }
          catch (Throwable localThrowable2)
          {
            FileLog.e(localThrowable2);
            bool2 = bool1;
          }
        }
        catch (Throwable localThrowable1)
        {
          FileLog.e(localThrowable1);
        }
      }
    }
  }
  
  public void applyDatacenterAddress(int paramInt1, String paramString, int paramInt2)
  {
    native_applyDatacenterAddress(this.currentAccount, paramInt1, paramString, paramInt2);
  }
  
  public void bindRequestToGuid(int paramInt1, int paramInt2)
  {
    native_bindRequestToGuid(this.currentAccount, paramInt1, paramInt2);
  }
  
  public void cancelRequest(int paramInt, boolean paramBoolean)
  {
    native_cancelRequest(this.currentAccount, paramInt, paramBoolean);
  }
  
  public void cancelRequestsForGuid(int paramInt)
  {
    native_cancelRequestsForGuid(this.currentAccount, paramInt);
  }
  
  public void cleanup()
  {
    native_cleanUp(this.currentAccount);
  }
  
  public int getConnectionState()
  {
    if ((this.connectionState == 3) && (this.isUpdating)) {}
    for (int i = 5;; i = this.connectionState) {
      return i;
    }
  }
  
  public int getCurrentTime()
  {
    return native_getCurrentTime(this.currentAccount);
  }
  
  public long getCurrentTimeMillis()
  {
    return native_getCurrentTimeMillis(this.currentAccount);
  }
  
  public long getPauseTime()
  {
    return this.lastPauseTime;
  }
  
  public int getTimeDifference()
  {
    return native_getTimeDifference(this.currentAccount);
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt4, boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    String str1 = localSharedPreferences.getString("proxy_ip", "");
    String str2 = localSharedPreferences.getString("proxy_user", "");
    String str3 = localSharedPreferences.getString("proxy_pass", "");
    int i = localSharedPreferences.getInt("proxy_port", 1080);
    if ((localSharedPreferences.getBoolean("proxy_enabled", false)) && (!TextUtils.isEmpty(str1))) {
      native_setProxySettings(this.currentAccount, str1, i, str2, str3);
    }
    native_init(this.currentAccount, paramInt1, paramInt2, paramInt3, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramInt4, paramBoolean, isNetworkOnline(), getCurrentNetworkType());
    checkConnection();
    paramString2 = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        ConnectionsManager.this.checkConnection();
      }
    };
    paramString1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    ApplicationLoader.applicationContext.registerReceiver(paramString2, paramString1);
  }
  
  public void resumeNetworkMaybe()
  {
    native_resumeNetwork(this.currentAccount, true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, 0);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, null, paramInt, Integer.MAX_VALUE, 1, true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt1, int paramInt2)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, null, paramInt1, Integer.MAX_VALUE, paramInt2, true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, QuickAckDelegate paramQuickAckDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, paramQuickAckDelegate, null, paramInt, Integer.MAX_VALUE, 1, true);
  }
  
  public int sendRequest(final TLObject paramTLObject, final RequestDelegate paramRequestDelegate, final QuickAckDelegate paramQuickAckDelegate, final WriteToSocketDelegate paramWriteToSocketDelegate, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean)
  {
    final int i = this.lastRequestToken.getAndIncrement();
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("send request " + paramTLObject + " with token = " + i);
        }
        try
        {
          Object localObject = new org/telegram/tgnet/NativeByteBuffer;
          ((NativeByteBuffer)localObject).<init>(paramTLObject.getObjectSize());
          paramTLObject.serializeToStream((AbstractSerializedData)localObject);
          paramTLObject.freeResources();
          int i = ConnectionsManager.this.currentAccount;
          long l = ((NativeByteBuffer)localObject).address;
          localObject = new org/telegram/tgnet/ConnectionsManager$2$1;
          ((1)localObject).<init>(this);
          ConnectionsManager.native_sendRequest(i, l, (RequestDelegateInternal)localObject, paramQuickAckDelegate, paramWriteToSocketDelegate, paramInt1, paramInt2, paramInt3, paramBoolean, i);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
    return i;
  }
  
  public void setAppPaused(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean2)
    {
      this.appPaused = paramBoolean1;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("app paused = " + paramBoolean1);
      }
      if (!paramBoolean1) {
        break label127;
      }
      this.appResumeCount -= 1;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("app resume count " + this.appResumeCount);
      }
      if (this.appResumeCount < 0) {
        this.appResumeCount = 0;
      }
    }
    if (this.appResumeCount == 0)
    {
      if (this.lastPauseTime == 0L) {
        this.lastPauseTime = System.currentTimeMillis();
      }
      native_pauseNetwork(this.currentAccount);
    }
    for (;;)
    {
      return;
      label127:
      this.appResumeCount += 1;
      break;
      if (!this.appPaused)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("reset app pause time");
        }
        if ((this.lastPauseTime != 0L) && (System.currentTimeMillis() - this.lastPauseTime > 5000L)) {
          ContactsController.getInstance(this.currentAccount).checkContacts();
        }
        this.lastPauseTime = 0L;
        native_resumeNetwork(this.currentAccount, false);
      }
    }
  }
  
  public void setIsUpdating(final boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ConnectionsManager.this.isUpdating == paramBoolean) {}
        for (;;)
        {
          return;
          ConnectionsManager.access$502(ConnectionsManager.this, paramBoolean);
          if (ConnectionsManager.this.connectionState == 3) {
            NotificationCenter.getInstance(ConnectionsManager.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
          }
        }
      }
    });
  }
  
  public void setPushConnectionEnabled(boolean paramBoolean)
  {
    native_setPushConnectionEnabled(this.currentAccount, paramBoolean);
  }
  
  public void setUserId(int paramInt)
  {
    native_setUserId(this.currentAccount, paramInt);
  }
  
  public void switchBackend()
  {
    MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
    native_switchBackend(this.currentAccount);
  }
  
  public void updateDcSettings()
  {
    native_updateDcSettings(this.currentAccount);
  }
  
  private static class AzureLoadTask
    extends AsyncTask<Void, Void, NativeByteBuffer>
  {
    private int currentAccount;
    
    public AzureLoadTask(int paramInt)
    {
      this.currentAccount = paramInt;
    }
    
    /* Error */
    protected NativeByteBuffer doInBackground(Void... paramVarArgs)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_2
      //   2: aconst_null
      //   3: astore_3
      //   4: aconst_null
      //   5: astore 4
      //   7: aconst_null
      //   8: astore 5
      //   10: aload 5
      //   12: astore_1
      //   13: aload_2
      //   14: astore 6
      //   16: aload 4
      //   18: astore 7
      //   20: aload_0
      //   21: getfield 19	org/telegram/tgnet/ConnectionsManager$AzureLoadTask:currentAccount	I
      //   24: invokestatic 37	org/telegram/tgnet/ConnectionsManager:native_isTestBackend	(I)I
      //   27: ifeq +250 -> 277
      //   30: aload 5
      //   32: astore_1
      //   33: aload_2
      //   34: astore 6
      //   36: aload 4
      //   38: astore 7
      //   40: new 39	java/net/URL
      //   43: astore 8
      //   45: aload 5
      //   47: astore_1
      //   48: aload_2
      //   49: astore 6
      //   51: aload 4
      //   53: astore 7
      //   55: aload 8
      //   57: ldc 41
      //   59: invokespecial 44	java/net/URL:<init>	(Ljava/lang/String;)V
      //   62: aload 5
      //   64: astore_1
      //   65: aload_2
      //   66: astore 6
      //   68: aload 4
      //   70: astore 7
      //   72: aload 8
      //   74: invokevirtual 48	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   77: astore 8
      //   79: aload 5
      //   81: astore_1
      //   82: aload_2
      //   83: astore 6
      //   85: aload 4
      //   87: astore 7
      //   89: aload 8
      //   91: ldc 50
      //   93: ldc 52
      //   95: invokevirtual 58	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   98: aload 5
      //   100: astore_1
      //   101: aload_2
      //   102: astore 6
      //   104: aload 4
      //   106: astore 7
      //   108: aload 8
      //   110: ldc 60
      //   112: ldc 62
      //   114: invokevirtual 58	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   117: aload 5
      //   119: astore_1
      //   120: aload_2
      //   121: astore 6
      //   123: aload 4
      //   125: astore 7
      //   127: aload 8
      //   129: sipush 5000
      //   132: invokevirtual 65	java/net/URLConnection:setConnectTimeout	(I)V
      //   135: aload 5
      //   137: astore_1
      //   138: aload_2
      //   139: astore 6
      //   141: aload 4
      //   143: astore 7
      //   145: aload 8
      //   147: sipush 5000
      //   150: invokevirtual 68	java/net/URLConnection:setReadTimeout	(I)V
      //   153: aload 5
      //   155: astore_1
      //   156: aload_2
      //   157: astore 6
      //   159: aload 4
      //   161: astore 7
      //   163: aload 8
      //   165: invokevirtual 71	java/net/URLConnection:connect	()V
      //   168: aload 5
      //   170: astore_1
      //   171: aload_2
      //   172: astore 6
      //   174: aload 4
      //   176: astore 7
      //   178: aload 8
      //   180: invokevirtual 75	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
      //   183: astore 8
      //   185: aload 8
      //   187: astore_1
      //   188: aload_2
      //   189: astore 6
      //   191: aload 8
      //   193: astore 7
      //   195: new 77	java/io/ByteArrayOutputStream
      //   198: astore 5
      //   200: aload 8
      //   202: astore_1
      //   203: aload_2
      //   204: astore 6
      //   206: aload 8
      //   208: astore 7
      //   210: aload 5
      //   212: invokespecial 78	java/io/ByteArrayOutputStream:<init>	()V
      //   215: ldc 79
      //   217: newarray <illegal type>
      //   219: astore_1
      //   220: aload_0
      //   221: invokevirtual 83	org/telegram/tgnet/ConnectionsManager$AzureLoadTask:isCancelled	()Z
      //   224: ifeq +77 -> 301
      //   227: aload 5
      //   229: invokevirtual 87	java/io/ByteArrayOutputStream:toByteArray	()[B
      //   232: iconst_0
      //   233: invokestatic 93	android/util/Base64:decode	([BI)[B
      //   236: astore 6
      //   238: new 95	org/telegram/tgnet/NativeByteBuffer
      //   241: astore_1
      //   242: aload_1
      //   243: aload 6
      //   245: arraylength
      //   246: invokespecial 97	org/telegram/tgnet/NativeByteBuffer:<init>	(I)V
      //   249: aload_1
      //   250: aload 6
      //   252: invokevirtual 101	org/telegram/tgnet/NativeByteBuffer:writeBytes	([B)V
      //   255: aload 8
      //   257: ifnull +8 -> 265
      //   260: aload 8
      //   262: invokevirtual 106	java/io/InputStream:close	()V
      //   265: aload 5
      //   267: ifnull +8 -> 275
      //   270: aload 5
      //   272: invokevirtual 107	java/io/ByteArrayOutputStream:close	()V
      //   275: aload_1
      //   276: areturn
      //   277: aload 5
      //   279: astore_1
      //   280: aload_2
      //   281: astore 6
      //   283: aload 4
      //   285: astore 7
      //   287: new 39	java/net/URL
      //   290: dup
      //   291: ldc 109
      //   293: invokespecial 44	java/net/URL:<init>	(Ljava/lang/String;)V
      //   296: astore 8
      //   298: goto -236 -> 62
      //   301: aload 8
      //   303: aload_1
      //   304: invokevirtual 113	java/io/InputStream:read	([B)I
      //   307: istore 9
      //   309: iload 9
      //   311: ifle +60 -> 371
      //   314: aload 5
      //   316: aload_1
      //   317: iconst_0
      //   318: iload 9
      //   320: invokevirtual 117	java/io/ByteArrayOutputStream:write	([BII)V
      //   323: goto -103 -> 220
      //   326: astore_1
      //   327: aload 5
      //   329: astore 7
      //   331: aload_1
      //   332: astore 5
      //   334: aload 8
      //   336: astore_1
      //   337: aload 7
      //   339: astore 6
      //   341: aload 5
      //   343: invokestatic 123	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   346: aload 8
      //   348: ifnull +8 -> 356
      //   351: aload 8
      //   353: invokevirtual 106	java/io/InputStream:close	()V
      //   356: aload 7
      //   358: ifnull +8 -> 366
      //   361: aload 7
      //   363: invokevirtual 107	java/io/ByteArrayOutputStream:close	()V
      //   366: aconst_null
      //   367: astore_1
      //   368: goto -93 -> 275
      //   371: iload 9
      //   373: iconst_m1
      //   374: if_icmpne -147 -> 227
      //   377: goto -150 -> 227
      //   380: astore 8
      //   382: aload 8
      //   384: invokestatic 123	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   387: goto -122 -> 265
      //   390: astore_1
      //   391: aload_1
      //   392: invokestatic 123	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   395: goto -39 -> 356
      //   398: astore 8
      //   400: aload_1
      //   401: astore 7
      //   403: aload 7
      //   405: ifnull +8 -> 413
      //   408: aload 7
      //   410: invokevirtual 106	java/io/InputStream:close	()V
      //   413: aload 6
      //   415: ifnull +8 -> 423
      //   418: aload 6
      //   420: invokevirtual 107	java/io/ByteArrayOutputStream:close	()V
      //   423: aload 8
      //   425: athrow
      //   426: astore_1
      //   427: aload_1
      //   428: invokestatic 123	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   431: goto -18 -> 413
      //   434: astore 8
      //   436: goto -161 -> 275
      //   439: astore_1
      //   440: goto -74 -> 366
      //   443: astore_1
      //   444: goto -21 -> 423
      //   447: astore_1
      //   448: aload 5
      //   450: astore 6
      //   452: aload 8
      //   454: astore 7
      //   456: aload_1
      //   457: astore 8
      //   459: goto -56 -> 403
      //   462: astore 5
      //   464: aload 7
      //   466: astore 8
      //   468: aload_3
      //   469: astore 7
      //   471: goto -137 -> 334
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	474	0	this	AzureLoadTask
      //   0	474	1	paramVarArgs	Void[]
      //   1	280	2	localObject1	Object
      //   3	466	3	localObject2	Object
      //   5	279	4	localObject3	Object
      //   8	441	5	localObject4	Object
      //   462	1	5	localThrowable1	Throwable
      //   14	437	6	localObject5	Object
      //   18	452	7	localObject6	Object
      //   43	309	8	localObject7	Object
      //   380	3	8	localThrowable2	Throwable
      //   398	26	8	localObject8	Object
      //   434	19	8	localException	Exception
      //   457	10	8	localObject9	Object
      //   307	68	9	i	int
      // Exception table:
      //   from	to	target	type
      //   215	220	326	java/lang/Throwable
      //   220	227	326	java/lang/Throwable
      //   227	255	326	java/lang/Throwable
      //   301	309	326	java/lang/Throwable
      //   314	323	326	java/lang/Throwable
      //   260	265	380	java/lang/Throwable
      //   351	356	390	java/lang/Throwable
      //   20	30	398	finally
      //   40	45	398	finally
      //   55	62	398	finally
      //   72	79	398	finally
      //   89	98	398	finally
      //   108	117	398	finally
      //   127	135	398	finally
      //   145	153	398	finally
      //   163	168	398	finally
      //   178	185	398	finally
      //   195	200	398	finally
      //   210	215	398	finally
      //   287	298	398	finally
      //   341	346	398	finally
      //   408	413	426	java/lang/Throwable
      //   270	275	434	java/lang/Exception
      //   361	366	439	java/lang/Exception
      //   418	423	443	java/lang/Exception
      //   215	220	447	finally
      //   220	227	447	finally
      //   227	255	447	finally
      //   301	309	447	finally
      //   314	323	447	finally
      //   20	30	462	java/lang/Throwable
      //   40	45	462	java/lang/Throwable
      //   55	62	462	java/lang/Throwable
      //   72	79	462	java/lang/Throwable
      //   89	98	462	java/lang/Throwable
      //   108	117	462	java/lang/Throwable
      //   127	135	462	java/lang/Throwable
      //   145	153	462	java/lang/Throwable
      //   163	168	462	java/lang/Throwable
      //   178	185	462	java/lang/Throwable
      //   195	200	462	java/lang/Throwable
      //   210	215	462	java/lang/Throwable
      //   287	298	462	java/lang/Throwable
    }
    
    protected void onPostExecute(final NativeByteBuffer paramNativeByteBuffer)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (paramNativeByteBuffer != null)
          {
            ConnectionsManager.access$302(null);
            ConnectionsManager.native_applyDnsConfig(ConnectionsManager.AzureLoadTask.this.currentAccount, paramNativeByteBuffer.address);
          }
          for (;;)
          {
            return;
            if (BuildVars.LOGS_ENABLED)
            {
              FileLog.d("failed to get azure result");
              FileLog.d("start dns txt task");
            }
            ConnectionsManager.DnsTxtLoadTask localDnsTxtLoadTask = new ConnectionsManager.DnsTxtLoadTask(ConnectionsManager.AzureLoadTask.this.currentAccount);
            localDnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            ConnectionsManager.access$302(localDnsTxtLoadTask);
          }
        }
      });
    }
  }
  
  private static class DnsTxtLoadTask
    extends AsyncTask<Void, Void, NativeByteBuffer>
  {
    private int currentAccount;
    
    public DnsTxtLoadTask(int paramInt)
    {
      this.currentAccount = paramInt;
    }
    
    /* Error */
    protected NativeByteBuffer doInBackground(Void... paramVarArgs)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_2
      //   2: aconst_null
      //   3: astore_3
      //   4: aconst_null
      //   5: astore 4
      //   7: aconst_null
      //   8: astore 5
      //   10: aload 5
      //   12: astore 6
      //   14: aload_2
      //   15: astore 7
      //   17: aload 4
      //   19: astore 8
      //   21: getstatic 41	java/util/Locale:US	Ljava/util/Locale;
      //   24: astore 9
      //   26: aload 5
      //   28: astore 6
      //   30: aload_2
      //   31: astore 7
      //   33: aload 4
      //   35: astore 8
      //   37: aload_0
      //   38: getfield 21	org/telegram/tgnet/ConnectionsManager$DnsTxtLoadTask:currentAccount	I
      //   41: invokestatic 45	org/telegram/tgnet/ConnectionsManager:native_isTestBackend	(I)I
      //   44: ifeq +369 -> 413
      //   47: ldc 47
      //   49: astore_1
      //   50: aload 5
      //   52: astore 6
      //   54: aload_2
      //   55: astore 7
      //   57: aload 4
      //   59: astore 8
      //   61: aload 9
      //   63: aload_1
      //   64: iconst_1
      //   65: anewarray 49	java/lang/Object
      //   68: dup
      //   69: iconst_0
      //   70: ldc 51
      //   72: aastore
      //   73: invokestatic 57	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   76: astore 9
      //   78: aload 5
      //   80: astore 6
      //   82: aload_2
      //   83: astore 7
      //   85: aload 4
      //   87: astore 8
      //   89: new 59	java/net/URL
      //   92: astore_1
      //   93: aload 5
      //   95: astore 6
      //   97: aload_2
      //   98: astore 7
      //   100: aload 4
      //   102: astore 8
      //   104: new 61	java/lang/StringBuilder
      //   107: astore 10
      //   109: aload 5
      //   111: astore 6
      //   113: aload_2
      //   114: astore 7
      //   116: aload 4
      //   118: astore 8
      //   120: aload 10
      //   122: invokespecial 62	java/lang/StringBuilder:<init>	()V
      //   125: aload 5
      //   127: astore 6
      //   129: aload_2
      //   130: astore 7
      //   132: aload 4
      //   134: astore 8
      //   136: aload_1
      //   137: aload 10
      //   139: ldc 64
      //   141: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   144: aload 9
      //   146: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   149: ldc 70
      //   151: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   154: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   157: invokespecial 77	java/net/URL:<init>	(Ljava/lang/String;)V
      //   160: aload 5
      //   162: astore 6
      //   164: aload_2
      //   165: astore 7
      //   167: aload 4
      //   169: astore 8
      //   171: aload_1
      //   172: invokevirtual 81	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   175: astore_1
      //   176: aload 5
      //   178: astore 6
      //   180: aload_2
      //   181: astore 7
      //   183: aload 4
      //   185: astore 8
      //   187: aload_1
      //   188: ldc 83
      //   190: ldc 85
      //   192: invokevirtual 91	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   195: aload 5
      //   197: astore 6
      //   199: aload_2
      //   200: astore 7
      //   202: aload 4
      //   204: astore 8
      //   206: aload_1
      //   207: ldc 93
      //   209: ldc 95
      //   211: invokevirtual 91	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   214: aload 5
      //   216: astore 6
      //   218: aload_2
      //   219: astore 7
      //   221: aload 4
      //   223: astore 8
      //   225: aload_1
      //   226: sipush 5000
      //   229: invokevirtual 98	java/net/URLConnection:setConnectTimeout	(I)V
      //   232: aload 5
      //   234: astore 6
      //   236: aload_2
      //   237: astore 7
      //   239: aload 4
      //   241: astore 8
      //   243: aload_1
      //   244: sipush 5000
      //   247: invokevirtual 101	java/net/URLConnection:setReadTimeout	(I)V
      //   250: aload 5
      //   252: astore 6
      //   254: aload_2
      //   255: astore 7
      //   257: aload 4
      //   259: astore 8
      //   261: aload_1
      //   262: invokevirtual 104	java/net/URLConnection:connect	()V
      //   265: aload 5
      //   267: astore 6
      //   269: aload_2
      //   270: astore 7
      //   272: aload 4
      //   274: astore 8
      //   276: aload_1
      //   277: invokevirtual 108	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
      //   280: astore_1
      //   281: aload_1
      //   282: astore 6
      //   284: aload_2
      //   285: astore 7
      //   287: aload_1
      //   288: astore 8
      //   290: new 110	java/io/ByteArrayOutputStream
      //   293: astore 5
      //   295: aload_1
      //   296: astore 6
      //   298: aload_2
      //   299: astore 7
      //   301: aload_1
      //   302: astore 8
      //   304: aload 5
      //   306: invokespecial 111	java/io/ByteArrayOutputStream:<init>	()V
      //   309: ldc 112
      //   311: newarray <illegal type>
      //   313: astore 6
      //   315: aload_0
      //   316: invokevirtual 116	org/telegram/tgnet/ConnectionsManager$DnsTxtLoadTask:isCancelled	()Z
      //   319: ifeq +100 -> 419
      //   322: new 118	org/json/JSONObject
      //   325: astore 7
      //   327: new 53	java/lang/String
      //   330: astore 6
      //   332: aload 6
      //   334: aload 5
      //   336: invokevirtual 122	java/io/ByteArrayOutputStream:toByteArray	()[B
      //   339: ldc 124
      //   341: invokespecial 127	java/lang/String:<init>	([BLjava/lang/String;)V
      //   344: aload 7
      //   346: aload 6
      //   348: invokespecial 128	org/json/JSONObject:<init>	(Ljava/lang/String;)V
      //   351: aload 7
      //   353: ldc -126
      //   355: invokevirtual 134	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
      //   358: astore 7
      //   360: aload 7
      //   362: invokevirtual 140	org/json/JSONArray:length	()I
      //   365: istore 11
      //   367: new 142	java/util/ArrayList
      //   370: astore 6
      //   372: aload 6
      //   374: iload 11
      //   376: invokespecial 144	java/util/ArrayList:<init>	(I)V
      //   379: iconst_0
      //   380: istore 12
      //   382: iload 12
      //   384: iload 11
      //   386: if_icmpge +112 -> 498
      //   389: aload 6
      //   391: aload 7
      //   393: iload 12
      //   395: invokevirtual 148	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
      //   398: ldc -106
      //   400: invokevirtual 154	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
      //   403: invokevirtual 158	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   406: pop
      //   407: iinc 12 1
      //   410: goto -28 -> 382
      //   413: ldc -96
      //   415: astore_1
      //   416: goto -366 -> 50
      //   419: aload_1
      //   420: aload 6
      //   422: invokevirtual 166	java/io/InputStream:read	([B)I
      //   425: istore 12
      //   427: iload 12
      //   429: ifle +60 -> 489
      //   432: aload 5
      //   434: aload 6
      //   436: iconst_0
      //   437: iload 12
      //   439: invokevirtual 170	java/io/ByteArrayOutputStream:write	([BII)V
      //   442: goto -127 -> 315
      //   445: astore 6
      //   447: aload 5
      //   449: astore 8
      //   451: aload 6
      //   453: astore 5
      //   455: aload_1
      //   456: astore 6
      //   458: aload 8
      //   460: astore 7
      //   462: aload 5
      //   464: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   467: aload_1
      //   468: ifnull +7 -> 475
      //   471: aload_1
      //   472: invokevirtual 179	java/io/InputStream:close	()V
      //   475: aload 8
      //   477: ifnull +8 -> 485
      //   480: aload 8
      //   482: invokevirtual 180	java/io/ByteArrayOutputStream:close	()V
      //   485: aconst_null
      //   486: astore_1
      //   487: aload_1
      //   488: areturn
      //   489: iload 12
      //   491: iconst_m1
      //   492: if_icmpne -170 -> 322
      //   495: goto -173 -> 322
      //   498: new 10	org/telegram/tgnet/ConnectionsManager$DnsTxtLoadTask$1
      //   501: astore 7
      //   503: aload 7
      //   505: aload_0
      //   506: invokespecial 183	org/telegram/tgnet/ConnectionsManager$DnsTxtLoadTask$1:<init>	(Lorg/telegram/tgnet/ConnectionsManager$DnsTxtLoadTask;)V
      //   509: aload 6
      //   511: aload 7
      //   513: invokestatic 189	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
      //   516: new 61	java/lang/StringBuilder
      //   519: astore 7
      //   521: aload 7
      //   523: invokespecial 62	java/lang/StringBuilder:<init>	()V
      //   526: iconst_0
      //   527: istore 12
      //   529: iload 12
      //   531: aload 6
      //   533: invokevirtual 192	java/util/ArrayList:size	()I
      //   536: if_icmpge +32 -> 568
      //   539: aload 7
      //   541: aload 6
      //   543: iload 12
      //   545: invokevirtual 196	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   548: checkcast 53	java/lang/String
      //   551: ldc -58
      //   553: ldc 51
      //   555: invokevirtual 202	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   558: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   561: pop
      //   562: iinc 12 1
      //   565: goto -36 -> 529
      //   568: aload 7
      //   570: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   573: iconst_0
      //   574: invokestatic 208	android/util/Base64:decode	(Ljava/lang/String;I)[B
      //   577: astore 7
      //   579: new 210	org/telegram/tgnet/NativeByteBuffer
      //   582: astore 6
      //   584: aload 6
      //   586: aload 7
      //   588: arraylength
      //   589: invokespecial 211	org/telegram/tgnet/NativeByteBuffer:<init>	(I)V
      //   592: aload 6
      //   594: aload 7
      //   596: invokevirtual 215	org/telegram/tgnet/NativeByteBuffer:writeBytes	([B)V
      //   599: aload_1
      //   600: ifnull +7 -> 607
      //   603: aload_1
      //   604: invokevirtual 179	java/io/InputStream:close	()V
      //   607: aload 5
      //   609: ifnull +8 -> 617
      //   612: aload 5
      //   614: invokevirtual 180	java/io/ByteArrayOutputStream:close	()V
      //   617: aload 6
      //   619: astore_1
      //   620: goto -133 -> 487
      //   623: astore_1
      //   624: aload_1
      //   625: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   628: goto -21 -> 607
      //   631: astore_1
      //   632: aload_1
      //   633: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   636: goto -161 -> 475
      //   639: astore_1
      //   640: aload 6
      //   642: astore 8
      //   644: aload 8
      //   646: ifnull +8 -> 654
      //   649: aload 8
      //   651: invokevirtual 179	java/io/InputStream:close	()V
      //   654: aload 7
      //   656: ifnull +8 -> 664
      //   659: aload 7
      //   661: invokevirtual 180	java/io/ByteArrayOutputStream:close	()V
      //   664: aload_1
      //   665: athrow
      //   666: astore 6
      //   668: aload 6
      //   670: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   673: goto -19 -> 654
      //   676: astore_1
      //   677: goto -60 -> 617
      //   680: astore_1
      //   681: goto -196 -> 485
      //   684: astore 6
      //   686: goto -22 -> 664
      //   689: astore 6
      //   691: aload 5
      //   693: astore 7
      //   695: aload_1
      //   696: astore 8
      //   698: aload 6
      //   700: astore_1
      //   701: goto -57 -> 644
      //   704: astore 5
      //   706: aload 8
      //   708: astore_1
      //   709: aload_3
      //   710: astore 8
      //   712: goto -257 -> 455
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	715	0	this	DnsTxtLoadTask
      //   0	715	1	paramVarArgs	Void[]
      //   1	298	2	localObject1	Object
      //   3	707	3	localObject2	Object
      //   5	268	4	localObject3	Object
      //   8	684	5	localObject4	Object
      //   704	1	5	localThrowable1	Throwable
      //   12	423	6	localObject5	Object
      //   445	7	6	localThrowable2	Throwable
      //   456	185	6	localObject6	Object
      //   666	3	6	localThrowable3	Throwable
      //   684	1	6	localException	Exception
      //   689	10	6	localObject7	Object
      //   15	679	7	localObject8	Object
      //   19	692	8	localObject9	Object
      //   24	121	9	localObject10	Object
      //   107	31	10	localStringBuilder	StringBuilder
      //   365	22	11	i	int
      //   380	183	12	j	int
      // Exception table:
      //   from	to	target	type
      //   309	315	445	java/lang/Throwable
      //   315	322	445	java/lang/Throwable
      //   322	379	445	java/lang/Throwable
      //   389	407	445	java/lang/Throwable
      //   419	427	445	java/lang/Throwable
      //   432	442	445	java/lang/Throwable
      //   498	526	445	java/lang/Throwable
      //   529	562	445	java/lang/Throwable
      //   568	599	445	java/lang/Throwable
      //   603	607	623	java/lang/Throwable
      //   471	475	631	java/lang/Throwable
      //   21	26	639	finally
      //   37	47	639	finally
      //   61	78	639	finally
      //   89	93	639	finally
      //   104	109	639	finally
      //   120	125	639	finally
      //   136	160	639	finally
      //   171	176	639	finally
      //   187	195	639	finally
      //   206	214	639	finally
      //   225	232	639	finally
      //   243	250	639	finally
      //   261	265	639	finally
      //   276	281	639	finally
      //   290	295	639	finally
      //   304	309	639	finally
      //   462	467	639	finally
      //   649	654	666	java/lang/Throwable
      //   612	617	676	java/lang/Exception
      //   480	485	680	java/lang/Exception
      //   659	664	684	java/lang/Exception
      //   309	315	689	finally
      //   315	322	689	finally
      //   322	379	689	finally
      //   389	407	689	finally
      //   419	427	689	finally
      //   432	442	689	finally
      //   498	526	689	finally
      //   529	562	689	finally
      //   568	599	689	finally
      //   21	26	704	java/lang/Throwable
      //   37	47	704	java/lang/Throwable
      //   61	78	704	java/lang/Throwable
      //   89	93	704	java/lang/Throwable
      //   104	109	704	java/lang/Throwable
      //   120	125	704	java/lang/Throwable
      //   136	160	704	java/lang/Throwable
      //   171	176	704	java/lang/Throwable
      //   187	195	704	java/lang/Throwable
      //   206	214	704	java/lang/Throwable
      //   225	232	704	java/lang/Throwable
      //   243	250	704	java/lang/Throwable
      //   261	265	704	java/lang/Throwable
      //   276	281	704	java/lang/Throwable
      //   290	295	704	java/lang/Throwable
      //   304	309	704	java/lang/Throwable
    }
    
    protected void onPostExecute(final NativeByteBuffer paramNativeByteBuffer)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (paramNativeByteBuffer != null) {
            ConnectionsManager.native_applyDnsConfig(ConnectionsManager.DnsTxtLoadTask.this.currentAccount, paramNativeByteBuffer.address);
          }
          for (;;)
          {
            ConnectionsManager.access$302(null);
            return;
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("failed to get dns txt result");
            }
          }
        }
      });
    }
  }
  
  private static class FirebaseTask
    extends AsyncTask<Void, Void, NativeByteBuffer>
  {
    private int currentAccount;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    
    public FirebaseTask(int paramInt)
    {
      this.currentAccount = paramInt;
    }
    
    protected NativeByteBuffer doInBackground(Void... paramVarArgs)
    {
      try
      {
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        paramVarArgs = new com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder;
        paramVarArgs.<init>();
        paramVarArgs = paramVarArgs.setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        this.firebaseRemoteConfig.setConfigSettings(paramVarArgs);
        Object localObject = this.firebaseRemoteConfig.getString("ipconfig");
        if (BuildVars.LOGS_ENABLED)
        {
          paramVarArgs = new java/lang/StringBuilder;
          paramVarArgs.<init>();
          FileLog.d("current firebase value = " + (String)localObject);
        }
        localObject = this.firebaseRemoteConfig.fetch(0L);
        paramVarArgs = new org/telegram/tgnet/ConnectionsManager$FirebaseTask$1;
        paramVarArgs.<init>(this);
        ((Task)localObject).addOnCompleteListener(paramVarArgs);
      }
      catch (Throwable paramVarArgs)
      {
        for (;;)
        {
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (BuildVars.LOGS_ENABLED)
              {
                FileLog.d("failed to get firebase result");
                FileLog.d("start azure task");
              }
              ConnectionsManager.AzureLoadTask localAzureLoadTask = new ConnectionsManager.AzureLoadTask(ConnectionsManager.FirebaseTask.this.currentAccount);
              localAzureLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
              ConnectionsManager.access$302(localAzureLoadTask);
            }
          });
          FileLog.e(paramVarArgs);
        }
      }
      return null;
    }
    
    protected void onPostExecute(NativeByteBuffer paramNativeByteBuffer) {}
  }
  
  private static class ResolvedDomain
  {
    public String address;
    long ttl;
    
    public ResolvedDomain(String paramString, long paramLong)
    {
      this.address = paramString;
      this.ttl = paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/ConnectionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */