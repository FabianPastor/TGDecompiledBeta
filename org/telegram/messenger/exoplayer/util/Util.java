package org.telegram.messenger.exoplayer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;

public final class Util
{
  public static final String DEVICE;
  private static final Pattern ESCAPED_CHARACTER_PATTERN;
  public static final String MANUFACTURER;
  private static final long MAX_BYTES_TO_DRAIN = 2048L;
  public static final String MODEL;
  public static final int SDK_INT;
  public static final int TYPE_DASH = 0;
  public static final int TYPE_HLS = 2;
  public static final int TYPE_OTHER = 3;
  public static final int TYPE_SS = 1;
  private static final Pattern XS_DATE_TIME_PATTERN;
  private static final Pattern XS_DURATION_PATTERN;
  
  static
  {
    if ((Build.VERSION.SDK_INT == 23) && (Build.VERSION.CODENAME.charAt(0) == 'N')) {}
    for (int i = 24;; i = Build.VERSION.SDK_INT)
    {
      SDK_INT = i;
      DEVICE = Build.DEVICE;
      MANUFACTURER = Build.MANUFACTURER;
      MODEL = Build.MODEL;
      XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)(\\.(\\d+))?([Zz]|((\\+|\\-)(\\d\\d):(\\d\\d)))?");
      XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");
      ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
      return;
    }
  }
  
  public static boolean areEqual(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      return paramObject2 == null;
    }
    return paramObject1.equals(paramObject2);
  }
  
  public static <T> int binarySearchCeil(List<? extends Comparable<? super T>> paramList, T paramT, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Collections.binarySearch(paramList, paramT);
    int i;
    if (j < 0) {
      i = j ^ 0xFFFFFFFF;
    }
    for (;;)
    {
      j = i;
      if (paramBoolean2) {
        j = Math.min(paramList.size() - 1, i);
      }
      return j;
      i = j;
      if (!paramBoolean1) {
        i = j + 1;
      }
    }
  }
  
  public static int binarySearchCeil(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int i;
    if (j < 0) {
      i = j ^ 0xFFFFFFFF;
    }
    for (;;)
    {
      j = i;
      if (paramBoolean2) {
        j = Math.min(paramArrayOfLong.length - 1, i);
      }
      return j;
      i = j;
      if (!paramBoolean1) {
        i = j + 1;
      }
    }
  }
  
  public static <T> int binarySearchFloor(List<? extends Comparable<? super T>> paramList, T paramT, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Collections.binarySearch(paramList, paramT);
    int i;
    if (j < 0) {
      i = -(j + 2);
    }
    for (;;)
    {
      j = i;
      if (paramBoolean2) {
        j = Math.max(0, i);
      }
      return j;
      i = j;
      if (!paramBoolean1) {
        i = j - 1;
      }
    }
  }
  
  public static int binarySearchFloor(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int i;
    if (j < 0) {
      i = -(j + 2);
    }
    for (;;)
    {
      j = i;
      if (paramBoolean2) {
        j = Math.max(0, i);
      }
      return j;
      i = j;
      if (!paramBoolean1) {
        i = j - 1;
      }
    }
  }
  
  public static int ceilDivide(int paramInt1, int paramInt2)
  {
    return (paramInt1 + paramInt2 - 1) / paramInt2;
  }
  
  public static long ceilDivide(long paramLong1, long paramLong2)
  {
    return (paramLong1 + paramLong2 - 1L) / paramLong2;
  }
  
  public static void closeQuietly(OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream.close();
      return;
    }
    catch (IOException paramOutputStream) {}
  }
  
  public static void closeQuietly(DataSource paramDataSource)
  {
    try
    {
      paramDataSource.close();
      return;
    }
    catch (IOException paramDataSource) {}
  }
  
  public static boolean contains(Object[] paramArrayOfObject, Object paramObject)
  {
    int i = 0;
    while (i < paramArrayOfObject.length)
    {
      if (areEqual(paramArrayOfObject[i], paramObject)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static String escapeFileName(String paramString)
  {
    int m = paramString.length();
    int i = 0;
    int j = 0;
    while (j < m)
    {
      int k = i;
      if (shouldEscapeCharacter(paramString.charAt(j))) {
        k = i + 1;
      }
      j += 1;
      i = k;
    }
    if (i == 0) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(i * 2 + m);
    j = 0;
    if (i > 0)
    {
      char c = paramString.charAt(j);
      if (shouldEscapeCharacter(c))
      {
        localStringBuilder.append('%').append(Integer.toHexString(c));
        i -= 1;
      }
      for (;;)
      {
        j += 1;
        break;
        localStringBuilder.append(c);
      }
    }
    if (j < m) {
      localStringBuilder.append(paramString, j, m);
    }
    return localStringBuilder.toString();
  }
  
  /* Error */
  public static byte[] executePost(String paramString, byte[] paramArrayOfByte, java.util.Map<String, String> paramMap)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 174	java/net/URL
    //   6: dup
    //   7: aload_0
    //   8: invokespecial 177	java/net/URL:<init>	(Ljava/lang/String;)V
    //   11: invokevirtual 181	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   14: checkcast 183	java/net/HttpURLConnection
    //   17: astore_0
    //   18: aload_0
    //   19: astore 4
    //   21: aload_0
    //   22: ldc -71
    //   24: invokevirtual 188	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
    //   27: aload_1
    //   28: ifnull +109 -> 137
    //   31: iconst_1
    //   32: istore_3
    //   33: aload_0
    //   34: astore 4
    //   36: aload_0
    //   37: iload_3
    //   38: invokevirtual 192	java/net/HttpURLConnection:setDoOutput	(Z)V
    //   41: aload_0
    //   42: astore 4
    //   44: aload_0
    //   45: iconst_1
    //   46: invokevirtual 195	java/net/HttpURLConnection:setDoInput	(Z)V
    //   49: aload_2
    //   50: ifnull +92 -> 142
    //   53: aload_0
    //   54: astore 4
    //   56: aload_2
    //   57: invokeinterface 201 1 0
    //   62: invokeinterface 207 1 0
    //   67: astore_2
    //   68: aload_0
    //   69: astore 4
    //   71: aload_2
    //   72: invokeinterface 213 1 0
    //   77: ifeq +65 -> 142
    //   80: aload_0
    //   81: astore 4
    //   83: aload_2
    //   84: invokeinterface 217 1 0
    //   89: checkcast 219	java/util/Map$Entry
    //   92: astore 5
    //   94: aload_0
    //   95: astore 4
    //   97: aload_0
    //   98: aload 5
    //   100: invokeinterface 222 1 0
    //   105: checkcast 41	java/lang/String
    //   108: aload 5
    //   110: invokeinterface 225 1 0
    //   115: checkcast 41	java/lang/String
    //   118: invokevirtual 229	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   121: goto -53 -> 68
    //   124: astore_0
    //   125: aload 4
    //   127: ifnull +8 -> 135
    //   130: aload 4
    //   132: invokevirtual 232	java/net/HttpURLConnection:disconnect	()V
    //   135: aload_0
    //   136: athrow
    //   137: iconst_0
    //   138: istore_3
    //   139: goto -106 -> 33
    //   142: aload_1
    //   143: ifnull +23 -> 166
    //   146: aload_0
    //   147: astore 4
    //   149: aload_0
    //   150: invokevirtual 236	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   153: astore_2
    //   154: aload_2
    //   155: aload_1
    //   156: invokevirtual 240	java/io/OutputStream:write	([B)V
    //   159: aload_0
    //   160: astore 4
    //   162: aload_2
    //   163: invokevirtual 128	java/io/OutputStream:close	()V
    //   166: aload_0
    //   167: astore 4
    //   169: aload_0
    //   170: invokevirtual 244	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   173: astore_1
    //   174: aload_1
    //   175: invokestatic 248	org/telegram/messenger/exoplayer/util/Util:toByteArray	(Ljava/io/InputStream;)[B
    //   178: astore_2
    //   179: aload_0
    //   180: astore 4
    //   182: aload_1
    //   183: invokevirtual 251	java/io/InputStream:close	()V
    //   186: aload_0
    //   187: ifnull +7 -> 194
    //   190: aload_0
    //   191: invokevirtual 232	java/net/HttpURLConnection:disconnect	()V
    //   194: aload_2
    //   195: areturn
    //   196: astore_1
    //   197: aload_0
    //   198: astore 4
    //   200: aload_2
    //   201: invokevirtual 128	java/io/OutputStream:close	()V
    //   204: aload_0
    //   205: astore 4
    //   207: aload_1
    //   208: athrow
    //   209: astore_2
    //   210: aload_0
    //   211: astore 4
    //   213: aload_1
    //   214: invokevirtual 251	java/io/InputStream:close	()V
    //   217: aload_0
    //   218: astore 4
    //   220: aload_2
    //   221: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	paramString	String
    //   0	222	1	paramArrayOfByte	byte[]
    //   0	222	2	paramMap	java.util.Map<String, String>
    //   32	107	3	bool	boolean
    //   1	218	4	str	String
    //   92	17	5	localEntry	java.util.Map.Entry
    // Exception table:
    //   from	to	target	type
    //   3	18	124	finally
    //   21	27	124	finally
    //   36	41	124	finally
    //   44	49	124	finally
    //   56	68	124	finally
    //   71	80	124	finally
    //   83	94	124	finally
    //   97	121	124	finally
    //   149	154	124	finally
    //   162	166	124	finally
    //   169	174	124	finally
    //   182	186	124	finally
    //   200	204	124	finally
    //   207	209	124	finally
    //   213	217	124	finally
    //   220	222	124	finally
    //   154	159	196	finally
    //   174	179	209	finally
  }
  
  public static int[] firstIntegersArray(int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      arrayOfInt[i] = i;
      i += 1;
    }
    return arrayOfInt;
  }
  
  public static int getBottomInt(long paramLong)
  {
    return (int)paramLong;
  }
  
  public static byte[] getBytesFromHexString(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    int i = 0;
    while (i < arrayOfByte.length)
    {
      int j = i * 2;
      arrayOfByte[i] = ((byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
      i += 1;
    }
    return arrayOfByte;
  }
  
  public static <T> String getCommaDelimitedSimpleClassNames(T[] paramArrayOfT)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfT.length)
    {
      localStringBuilder.append(paramArrayOfT[i].getClass().getSimpleName());
      if (i < paramArrayOfT.length - 1) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static String getHexStringFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramInt2 - paramInt1);
    while (paramInt1 < paramInt2)
    {
      localStringBuilder.append(String.format(Locale.US, "%02X", new Object[] { Byte.valueOf(paramArrayOfByte[paramInt1]) }));
      paramInt1 += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static int getIntegerCodeForString(String paramString)
  {
    int k = paramString.length();
    if (k <= 4) {}
    int j;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      j = 0;
      int i = 0;
      while (i < k)
      {
        j = j << 8 | paramString.charAt(i);
        i += 1;
      }
    }
    return j;
  }
  
  public static long getLong(int paramInt1, int paramInt2)
  {
    return paramInt1 << 32 | paramInt2 & 0xFFFFFFFF;
  }
  
  public static int getPcmEncoding(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 8: 
      return 3;
    case 16: 
      return 2;
    case 24: 
      return Integer.MIN_VALUE;
    }
    return NUM;
  }
  
  public static DataSpec getRemainderDataSpec(DataSpec paramDataSpec, int paramInt)
  {
    long l = -1L;
    if (paramInt == 0) {
      return paramDataSpec;
    }
    if (paramDataSpec.length == -1L) {}
    for (;;)
    {
      return new DataSpec(paramDataSpec.uri, paramDataSpec.position + paramInt, l, paramDataSpec.key, paramDataSpec.flags);
      l = paramDataSpec.length - paramInt;
    }
  }
  
  public static int getTopInt(long paramLong)
  {
    return (int)(paramLong >>> 32);
  }
  
  public static String getUserAgent(Context paramContext, String paramString)
  {
    try
    {
      String str = paramContext.getPackageName();
      paramContext = paramContext.getPackageManager().getPackageInfo(str, 0).versionName;
      return paramString + "/" + paramContext + " (Linux;Android " + Build.VERSION.RELEASE + ") ExoPlayerLib/" + "1.5.10";
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = "?";
      }
    }
  }
  
  public static int inferContentType(String paramString)
  {
    if (paramString == null) {}
    do
    {
      return 3;
      if (paramString.endsWith(".mpd")) {
        return 0;
      }
      if (paramString.endsWith(".ism")) {
        return 1;
      }
    } while (!paramString.endsWith(".m3u8"));
    return 2;
  }
  
  @SuppressLint({"InlinedApi"})
  public static boolean isAndroidTv(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("android.software.leanback");
  }
  
  public static boolean isLocalFileUri(Uri paramUri)
  {
    paramUri = paramUri.getScheme();
    return (TextUtils.isEmpty(paramUri)) || (paramUri.equals("file"));
  }
  
  public static void maybeTerminateInputStream(HttpURLConnection paramHttpURLConnection, long paramLong)
  {
    if ((SDK_INT != 19) && (SDK_INT != 20)) {}
    do
    {
      for (;;)
      {
        return;
        try
        {
          paramHttpURLConnection = paramHttpURLConnection.getInputStream();
          if (paramLong != -1L) {
            break label102;
          }
          if (paramHttpURLConnection.read() == -1) {}
        }
        catch (IOException paramHttpURLConnection)
        {
          do
          {
            Object localObject;
            return;
          } while (paramLong > 2048L);
          return;
        }
        catch (Exception paramHttpURLConnection) {}
      }
      localObject = paramHttpURLConnection.getClass().getName();
    } while ((!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream")) && (!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream")));
    localObject = paramHttpURLConnection.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
    ((Method)localObject).setAccessible(true);
    ((Method)localObject).invoke(paramHttpURLConnection, new Object[0]);
    return;
    label102:
  }
  
  public static ExecutorService newSingleThreadExecutor(String paramString)
  {
    Executors.newSingleThreadExecutor(new ThreadFactory()
    {
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        return new Thread(paramAnonymousRunnable, this.val$threadName);
      }
    });
  }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor(String paramString)
  {
    Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
    {
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        return new Thread(paramAnonymousRunnable, this.val$threadName);
      }
    });
  }
  
  public static long parseXsDateTime(String paramString)
    throws ParseException
  {
    Matcher localMatcher = XS_DATE_TIME_PATTERN.matcher(paramString);
    if (!localMatcher.matches()) {
      throw new ParseException("Invalid date/time format: " + paramString, 0);
    }
    int i;
    if (localMatcher.group(9) == null) {
      i = 0;
    }
    for (;;)
    {
      paramString = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
      paramString.clear();
      paramString.set(Integer.parseInt(localMatcher.group(1)), Integer.parseInt(localMatcher.group(2)) - 1, Integer.parseInt(localMatcher.group(3)), Integer.parseInt(localMatcher.group(4)), Integer.parseInt(localMatcher.group(5)), Integer.parseInt(localMatcher.group(6)));
      if (!TextUtils.isEmpty(localMatcher.group(8))) {
        paramString.set(14, new BigDecimal("0." + localMatcher.group(8)).movePointRight(3).intValue());
      }
      long l2 = paramString.getTimeInMillis();
      long l1 = l2;
      if (i != 0) {
        l1 = l2 - 60000 * i;
      }
      return l1;
      if (localMatcher.group(9).equalsIgnoreCase("Z"))
      {
        i = 0;
      }
      else
      {
        int j = Integer.parseInt(localMatcher.group(12)) * 60 + Integer.parseInt(localMatcher.group(13));
        i = j;
        if (localMatcher.group(11).equals("-")) {
          i = j * -1;
        }
      }
    }
  }
  
  public static long parseXsDuration(String paramString)
  {
    Matcher localMatcher = XS_DURATION_PATTERN.matcher(paramString);
    if (localMatcher.matches())
    {
      int i;
      double d1;
      label52:
      double d2;
      label72:
      double d3;
      label94:
      double d4;
      label116:
      double d5;
      if (!TextUtils.isEmpty(localMatcher.group(1)))
      {
        i = 1;
        paramString = localMatcher.group(3);
        if (paramString == null) {
          break label201;
        }
        d1 = Double.parseDouble(paramString) * 3.1556908E7D;
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label206;
        }
        d2 = Double.parseDouble(paramString) * 2629739.0D;
        paramString = localMatcher.group(7);
        if (paramString == null) {
          break label211;
        }
        d3 = Double.parseDouble(paramString) * 86400.0D;
        paramString = localMatcher.group(10);
        if (paramString == null) {
          break label217;
        }
        d4 = Double.parseDouble(paramString) * 3600.0D;
        paramString = localMatcher.group(12);
        if (paramString == null) {
          break label223;
        }
        d5 = Double.parseDouble(paramString) * 60.0D;
        label138:
        paramString = localMatcher.group(14);
        if (paramString == null) {
          break label229;
        }
      }
      label201:
      label206:
      label211:
      label217:
      label223:
      label229:
      for (double d6 = Double.parseDouble(paramString);; d6 = 0.0D)
      {
        long l2 = (1000.0D * (d1 + d2 + d3 + d4 + d5 + d6));
        long l1 = l2;
        if (i != 0) {
          l1 = -l2;
        }
        return l1;
        i = 0;
        break;
        d1 = 0.0D;
        break label52;
        d2 = 0.0D;
        break label72;
        d3 = 0.0D;
        break label94;
        d4 = 0.0D;
        break label116;
        d5 = 0.0D;
        break label138;
      }
    }
    return (Double.parseDouble(paramString) * 3600.0D * 1000.0D);
  }
  
  public static long scaleLargeTimestamp(long paramLong1, long paramLong2, long paramLong3)
  {
    if ((paramLong3 >= paramLong2) && (paramLong3 % paramLong2 == 0L)) {
      return paramLong1 / (paramLong3 / paramLong2);
    }
    if ((paramLong3 < paramLong2) && (paramLong2 % paramLong3 == 0L)) {
      return paramLong1 * (paramLong2 / paramLong3);
    }
    double d = paramLong2 / paramLong3;
    return (paramLong1 * d);
  }
  
  public static long[] scaleLargeTimestamps(List<Long> paramList, long paramLong1, long paramLong2)
  {
    long[] arrayOfLong = new long[paramList.size()];
    int i;
    if ((paramLong2 >= paramLong1) && (paramLong2 % paramLong1 == 0L))
    {
      paramLong1 = paramLong2 / paramLong1;
      i = 0;
    }
    while (i < arrayOfLong.length)
    {
      arrayOfLong[i] = (((Long)paramList.get(i)).longValue() / paramLong1);
      i += 1;
      continue;
      if ((paramLong2 < paramLong1) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = 0;
      }
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = (((Long)paramList.get(i)).longValue() * paramLong1);
        i += 1;
        continue;
        double d = paramLong1 / paramLong2;
        i = 0;
        while (i < arrayOfLong.length)
        {
          arrayOfLong[i] = ((((Long)paramList.get(i)).longValue() * d));
          i += 1;
        }
      }
    }
    return arrayOfLong;
  }
  
  public static void scaleLargeTimestampsInPlace(long[] paramArrayOfLong, long paramLong1, long paramLong2)
  {
    int i;
    if ((paramLong2 >= paramLong1) && (paramLong2 % paramLong1 == 0L))
    {
      paramLong1 = paramLong2 / paramLong1;
      i = 0;
    }
    while (i < paramArrayOfLong.length)
    {
      paramArrayOfLong[i] /= paramLong1;
      i += 1;
      continue;
      if ((paramLong2 < paramLong1) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = 0;
      }
      while (i < paramArrayOfLong.length)
      {
        paramArrayOfLong[i] *= paramLong1;
        i += 1;
        continue;
        double d = paramLong1 / paramLong2;
        i = 0;
        while (i < paramArrayOfLong.length)
        {
          paramArrayOfLong[i] = ((paramArrayOfLong[i] * d));
          i += 1;
        }
      }
    }
  }
  
  private static boolean shouldEscapeCharacter(char paramChar)
  {
    switch (paramChar)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static int[] toArray(List<Integer> paramList)
  {
    Object localObject;
    if (paramList == null)
    {
      localObject = null;
      return (int[])localObject;
    }
    int j = paramList.size();
    int[] arrayOfInt = new int[j];
    int i = 0;
    for (;;)
    {
      localObject = arrayOfInt;
      if (i >= j) {
        break;
      }
      arrayOfInt[i] = ((Integer)paramList.get(i)).intValue();
      i += 1;
    }
  }
  
  public static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static String toLowerInvariant(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.toLowerCase(Locale.US);
  }
  
  public static String unescapeFileName(String paramString)
  {
    int m = paramString.length();
    int i = 0;
    int j = 0;
    while (j < m)
    {
      k = i;
      if (paramString.charAt(j) == '%') {
        k = i + 1;
      }
      j += 1;
      i = k;
    }
    if (i == 0) {
      return paramString;
    }
    int k = m - i * 2;
    StringBuilder localStringBuilder = new StringBuilder(k);
    Matcher localMatcher = ESCAPED_CHARACTER_PATTERN.matcher(paramString);
    j = 0;
    while ((i > 0) && (localMatcher.find()))
    {
      char c = (char)Integer.parseInt(localMatcher.group(1), 16);
      localStringBuilder.append(paramString, j, localMatcher.start()).append(c);
      j = localMatcher.end();
      i -= 1;
    }
    if (j < m) {
      localStringBuilder.append(paramString, j, m);
    }
    if (localStringBuilder.length() != k) {
      return null;
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */