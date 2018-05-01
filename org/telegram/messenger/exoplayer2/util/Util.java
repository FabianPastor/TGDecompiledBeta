package org.telegram.messenger.exoplayer2.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.upstream.DataSource;

public final class Util
{
  private static final int[] CRC32_BYTES_MSBF;
  public static final String DEVICE;
  public static final String DEVICE_DEBUG_INFO;
  private static final Pattern ESCAPED_CHARACTER_PATTERN;
  public static final String MANUFACTURER;
  public static final String MODEL;
  public static final int SDK_INT;
  private static final String TAG = "Util";
  private static final Pattern XS_DATE_TIME_PATTERN;
  private static final Pattern XS_DURATION_PATTERN;
  
  static
  {
    if ((Build.VERSION.SDK_INT == 25) && (Build.VERSION.CODENAME.charAt(0) == 'O')) {}
    for (int i = 26;; i = Build.VERSION.SDK_INT)
    {
      SDK_INT = i;
      DEVICE = Build.DEVICE;
      MANUFACTURER = Build.MANUFACTURER;
      MODEL = Build.MODEL;
      DEVICE_DEBUG_INFO = DEVICE + ", " + MODEL + ", " + MANUFACTURER + ", " + SDK_INT;
      XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?([Zz]|((\\+|\\-)(\\d?\\d):?(\\d\\d)))?");
      XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");
      ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
      CRC32_BYTES_MSBF = new int[] { 0, 79764919, 159529838, 222504665, 319059676, 398814059, 445009330, 507990021, 638119352, 583659535, 797628118, 726387553, 890018660, 835552979, NUM, 944750013, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -734892656, -789352409, -575645954, -646886583, -952755380, -NUM, -827056094, -898286187, -231047128, -151282273, -71779514, -8804623, -515967244, -436212925, -390279782, -327299027, 881225847, 809987520, NUM, 969234094, 662832811, 591600412, 771767749, 717299826, 311336399, 374308984, 453813921, 533576470, 25881363, 88864420, 134795389, 214552010, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -525066777, -462094256, -382327159, -302564546, -206542021, -143559028, -97365931, -17609246, -960696225, -NUM, -817968335, -872425850, -709327229, -780559564, -600130067, -654598054, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, 622672798, 568075817, 748617968, 677256519, 907627842, 853037301, NUM, 995781531, 51762726, 131386257, 177728840, 240578815, 269590778, 349224269, 429104020, 491947555, -248556018, -168932423, -122852000, -60002089, -500490030, -420856475, -341238852, -278395381, -685261898, -739858943, -559578920, -630940305, -NUM, -NUM, -845023740, -916395085, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, 295390185, 358241886, 404320391, 483945776, 43990325, 106832002, 186451547, 266083308, 932423249, 861060070, NUM, 986742920, 613929101, 542559546, 756411363, 701822548, -978770311, -NUM, -869589737, -924188512, -693284699, -764654318, -550540341, -605129092, -475935807, -413084042, -366743377, -287118056, -257573603, -194731862, -114850189, -35218492, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM };
      return;
    }
  }
  
  public static long addWithOverflowDefault(long paramLong1, long paramLong2, long paramLong3)
  {
    long l = paramLong1 + paramLong2;
    if (((paramLong1 ^ l) & (paramLong2 ^ l)) < 0L) {}
    for (paramLong1 = paramLong3;; paramLong1 = l) {
      return paramLong1;
    }
  }
  
  public static boolean areEqual(Object paramObject1, Object paramObject2)
  {
    boolean bool;
    if (paramObject1 == null) {
      if (paramObject2 == null) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      bool = paramObject1.equals(paramObject2);
    }
  }
  
  public static <T> int binarySearchCeil(List<? extends Comparable<? super T>> paramList, T paramT, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Collections.binarySearch(paramList, paramT);
    if (i < 0) {
      i ^= 0xFFFFFFFF;
    }
    for (;;)
    {
      int j = i;
      if (paramBoolean2) {
        j = Math.min(paramList.size() - 1, i);
      }
      return j;
      int k = paramList.size();
      do
      {
        j = i + 1;
        if (j >= k) {
          break;
        }
        i = j;
      } while (((Comparable)paramList.get(j)).compareTo(paramT) == 0);
      i = j;
      if (paramBoolean1) {
        i = j - 1;
      }
    }
  }
  
  public static int binarySearchCeil(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int j = i;
    if (i < 0) {
      j = i ^ 0xFFFFFFFF;
    }
    for (;;)
    {
      i = j;
      if (paramBoolean2) {
        i = Math.min(paramArrayOfLong.length - 1, j);
      }
      return i;
      do
      {
        i = j + 1;
        if (i >= paramArrayOfLong.length) {
          break;
        }
        j = i;
      } while (paramArrayOfLong[i] == paramLong);
      j = i;
      if (paramBoolean1) {
        j = i - 1;
      }
    }
  }
  
  public static <T> int binarySearchFloor(List<? extends Comparable<? super T>> paramList, T paramT, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Collections.binarySearch(paramList, paramT);
    int j = i;
    if (i < 0) {
      j = -(i + 2);
    }
    for (;;)
    {
      i = j;
      if (paramBoolean2) {
        i = Math.max(0, j);
      }
      return i;
      do
      {
        i = j - 1;
        if (i < 0) {
          break;
        }
        j = i;
      } while (((Comparable)paramList.get(i)).compareTo(paramT) == 0);
      j = i;
      if (paramBoolean1) {
        j = i + 1;
      }
    }
  }
  
  public static int binarySearchFloor(int[] paramArrayOfInt, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Arrays.binarySearch(paramArrayOfInt, paramInt);
    int j = i;
    if (i < 0) {
      paramInt = -(i + 2);
    }
    for (;;)
    {
      i = paramInt;
      if (paramBoolean2) {
        i = Math.max(0, paramInt);
      }
      return i;
      do
      {
        i = j - 1;
        if (i < 0) {
          break;
        }
        j = i;
      } while (paramArrayOfInt[i] == paramInt);
      paramInt = i;
      if (paramBoolean1) {
        paramInt = i + 1;
      }
    }
  }
  
  public static int binarySearchFloor(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int j = i;
    if (i < 0) {
      j = -(i + 2);
    }
    for (;;)
    {
      i = j;
      if (paramBoolean2) {
        i = Math.max(0, j);
      }
      return i;
      do
      {
        i = j - 1;
        if (i < 0) {
          break;
        }
        j = i;
      } while (paramArrayOfLong[i] == paramLong);
      j = i;
      if (paramBoolean1) {
        j = i + 1;
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
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable)
    {
      for (;;) {}
    }
  }
  
  public static void closeQuietly(DataSource paramDataSource)
  {
    if (paramDataSource != null) {}
    try
    {
      paramDataSource.close();
      return;
    }
    catch (IOException paramDataSource)
    {
      for (;;) {}
    }
  }
  
  public static int compareLong(long paramLong1, long paramLong2)
  {
    int i;
    if (paramLong1 < paramLong2) {
      i = -1;
    }
    for (;;)
    {
      return i;
      if (paramLong1 == paramLong2) {
        i = 0;
      } else {
        i = 1;
      }
    }
  }
  
  public static float constrainValue(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.max(paramFloat2, Math.min(paramFloat1, paramFloat3));
  }
  
  public static int constrainValue(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.max(paramInt2, Math.min(paramInt1, paramInt3));
  }
  
  public static long constrainValue(long paramLong1, long paramLong2, long paramLong3)
  {
    return Math.max(paramLong2, Math.min(paramLong1, paramLong3));
  }
  
  public static boolean contains(Object[] paramArrayOfObject, Object paramObject)
  {
    boolean bool1 = false;
    int i = paramArrayOfObject.length;
    for (int j = 0;; j++)
    {
      boolean bool2 = bool1;
      if (j < i)
      {
        if (areEqual(paramArrayOfObject[j], paramObject)) {
          bool2 = true;
        }
      }
      else {
        return bool2;
      }
    }
  }
  
  public static int crc(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    while (paramInt1 < paramInt2)
    {
      paramInt3 = paramInt3 << 8 ^ CRC32_BYTES_MSBF[((paramInt3 >>> 24 ^ paramArrayOfByte[paramInt1] & 0xFF) & 0xFF)];
      paramInt1++;
    }
    return paramInt3;
  }
  
  public static File createTempDirectory(Context paramContext, String paramString)
    throws IOException
  {
    paramContext = createTempFile(paramContext, paramString);
    paramContext.delete();
    paramContext.mkdir();
    return paramContext;
  }
  
  public static File createTempFile(Context paramContext, String paramString)
    throws IOException
  {
    return File.createTempFile(paramString, null, paramContext.getCacheDir());
  }
  
  public static String escapeFileName(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    int k = 0;
    while (k < i)
    {
      int m = j;
      if (shouldEscapeCharacter(paramString.charAt(k))) {
        m = j + 1;
      }
      k++;
      j = m;
    }
    if (j == 0) {}
    for (;;)
    {
      return paramString;
      StringBuilder localStringBuilder = new StringBuilder(j * 2 + i);
      k = 0;
      if (j > 0)
      {
        char c = paramString.charAt(k);
        if (shouldEscapeCharacter(c))
        {
          localStringBuilder.append('%').append(Integer.toHexString(c));
          j--;
        }
        for (;;)
        {
          k++;
          break;
          localStringBuilder.append(c);
        }
      }
      if (k < i) {
        localStringBuilder.append(paramString, k, i);
      }
      paramString = localStringBuilder.toString();
    }
  }
  
  public static String fromUtf8Bytes(byte[] paramArrayOfByte)
  {
    return new String(paramArrayOfByte, Charset.forName("UTF-8"));
  }
  
  public static int getAudioContentTypeForStreamType(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    case 6: 
    case 7: 
    default: 
      paramInt = 2;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 4;
      continue;
      paramInt = 1;
    }
  }
  
  public static int getAudioUsageForStreamType(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    case 6: 
    case 7: 
    default: 
      paramInt = 1;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 4;
      continue;
      paramInt = 3;
      continue;
      paramInt = 5;
      continue;
      paramInt = 6;
      continue;
      paramInt = 13;
      continue;
      paramInt = 2;
    }
  }
  
  public static byte[] getBytesFromHexString(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    for (int i = 0; i < arrayOfByte.length; i++)
    {
      int j = i * 2;
      arrayOfByte[i] = ((byte)(byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
    }
    return arrayOfByte;
  }
  
  public static String getCodecsOfType(String paramString, int paramInt)
  {
    Object localObject = null;
    if (TextUtils.isEmpty(paramString)) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      String[] arrayOfString = paramString.trim().split("(\\s*,\\s*)");
      StringBuilder localStringBuilder = new StringBuilder();
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        paramString = arrayOfString[j];
        if (paramInt == MimeTypes.getTrackTypeOfCodec(paramString))
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(",");
          }
          localStringBuilder.append(paramString);
        }
      }
      paramString = (String)localObject;
      if (localStringBuilder.length() > 0) {
        paramString = localStringBuilder.toString();
      }
    }
  }
  
  public static String getCommaDelimitedSimpleClassNames(Object[] paramArrayOfObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      localStringBuilder.append(paramArrayOfObject[i].getClass().getSimpleName());
      if (i < paramArrayOfObject.length - 1) {
        localStringBuilder.append(", ");
      }
    }
    return localStringBuilder.toString();
  }
  
  public static int getDefaultBufferSize(int paramInt)
  {
    int i = 131072;
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      i = 16777216;
    }
    for (;;)
    {
      return i;
      i = 3538944;
      continue;
      i = 13107200;
    }
  }
  
  @TargetApi(16)
  private static void getDisplaySizeV16(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getSize(paramPoint);
  }
  
  @TargetApi(17)
  private static void getDisplaySizeV17(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getRealSize(paramPoint);
  }
  
  @TargetApi(23)
  private static void getDisplaySizeV23(Display paramDisplay, Point paramPoint)
  {
    paramDisplay = paramDisplay.getMode();
    paramPoint.x = paramDisplay.getPhysicalWidth();
    paramPoint.y = paramDisplay.getPhysicalHeight();
  }
  
  private static void getDisplaySizeV9(Display paramDisplay, Point paramPoint)
  {
    paramPoint.x = paramDisplay.getWidth();
    paramPoint.y = paramDisplay.getHeight();
  }
  
  public static UUID getDrmUuid(String paramString)
  {
    String str = toLowerInvariant(paramString);
    int i = -1;
    switch (str.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      }
      try
      {
        paramString = UUID.fromString(paramString);
        for (;;)
        {
          return paramString;
          if (!str.equals("widevine")) {
            break;
          }
          i = 0;
          break;
          if (!str.equals("playready")) {
            break;
          }
          i = 1;
          break;
          if (!str.equals("clearkey")) {
            break;
          }
          i = 2;
          break;
          paramString = C.WIDEVINE_UUID;
          continue;
          paramString = C.PLAYREADY_UUID;
          continue;
          paramString = C.CLEARKEY_UUID;
        }
      }
      catch (RuntimeException paramString)
      {
        for (;;)
        {
          paramString = null;
        }
      }
    }
  }
  
  public static int getIntegerCodeForString(String paramString)
  {
    int i = paramString.length();
    if (i <= 4) {}
    int j;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      j = 0;
      for (int k = 0; k < i; k++) {
        j = j << 8 | paramString.charAt(k);
      }
    }
    return j;
  }
  
  public static long getMediaDurationForPlayoutDuration(long paramLong, float paramFloat)
  {
    if (paramFloat == 1.0F) {}
    for (;;)
    {
      return paramLong;
      paramLong = Math.round(paramLong * paramFloat);
    }
  }
  
  public static int getPcmEncoding(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramInt = 0;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 3;
      continue;
      paramInt = 2;
      continue;
      paramInt = Integer.MIN_VALUE;
      continue;
      paramInt = NUM;
    }
  }
  
  public static int getPcmFrameSize(int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException();
    case 2: 
      i = paramInt2 * 2;
    }
    for (;;)
    {
      return i;
      i = paramInt2 * 3;
      continue;
      i = paramInt2 * 4;
    }
  }
  
  public static Point getPhysicalDisplaySize(Context paramContext)
  {
    return getPhysicalDisplaySize(paramContext, ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay());
  }
  
  public static Point getPhysicalDisplaySize(Context paramContext, Display paramDisplay)
  {
    if ((SDK_INT < 25) && (paramDisplay.getDisplayId() == 0)) {
      if (("Sony".equals(MANUFACTURER)) && (MODEL.startsWith("BRAVIA")) && (paramContext.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd"))) {
        paramContext = new Point(3840, 2160);
      }
    }
    for (;;)
    {
      return paramContext;
      if (("NVIDIA".equals(MANUFACTURER)) && (MODEL.contains("SHIELD"))) {
        paramContext = null;
      }
      try
      {
        Object localObject = Class.forName("android.os.SystemProperties");
        localObject = (String)((Class)localObject).getMethod("get", new Class[] { String.class }).invoke(localObject, new Object[] { "sys.display-size" });
        paramContext = (Context)localObject;
        if (!TextUtils.isEmpty(paramContext))
        {
          try
          {
            localObject = paramContext.trim().split("x");
            if (localObject.length == 2)
            {
              int i = Integer.parseInt(localObject[0]);
              int j = Integer.parseInt(localObject[1]);
              if ((i > 0) && (j > 0))
              {
                localObject = new android/graphics/Point;
                ((Point)localObject).<init>(i, j);
                paramContext = (Context)localObject;
              }
            }
          }
          catch (NumberFormatException localNumberFormatException)
          {
            Log.e("Util", "Invalid sys.display-size: " + paramContext);
          }
        }
        else
        {
          paramContext = new Point();
          if (SDK_INT >= 23) {
            getDisplaySizeV23(paramDisplay, paramContext);
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("Util", "Failed to read sys.display-size", localException);
        }
        if (SDK_INT >= 17) {
          getDisplaySizeV17(paramDisplay, paramContext);
        } else if (SDK_INT >= 16) {
          getDisplaySizeV16(paramDisplay, paramContext);
        } else {
          getDisplaySizeV9(paramDisplay, paramContext);
        }
      }
    }
  }
  
  public static long getPlayoutDurationForMediaDuration(long paramLong, float paramFloat)
  {
    if (paramFloat == 1.0F) {}
    for (;;)
    {
      return paramLong;
      paramLong = Math.round(paramLong / paramFloat);
    }
  }
  
  public static int getStreamTypeForAudioUsage(int paramInt)
  {
    int i = 3;
    int j = i;
    switch (paramInt)
    {
    default: 
      j = i;
    }
    for (;;)
    {
      return j;
      j = 1;
      continue;
      j = 0;
      continue;
      j = 8;
      continue;
      j = 4;
      continue;
      j = 2;
      continue;
      j = 5;
    }
  }
  
  public static String getStringForTime(StringBuilder paramStringBuilder, Formatter paramFormatter, long paramLong)
  {
    long l1 = paramLong;
    if (paramLong == -9223372036854775807L) {
      l1 = 0L;
    }
    long l2 = (500L + l1) / 1000L;
    paramLong = l2 % 60L;
    l1 = l2 / 60L % 60L;
    l2 /= 3600L;
    paramStringBuilder.setLength(0);
    if (l2 > 0L) {}
    for (paramStringBuilder = paramFormatter.format("%d:%02d:%02d", new Object[] { Long.valueOf(l2), Long.valueOf(l1), Long.valueOf(paramLong) }).toString();; paramStringBuilder = paramFormatter.format("%02d:%02d", new Object[] { Long.valueOf(l1), Long.valueOf(paramLong) }).toString()) {
      return paramStringBuilder;
    }
  }
  
  public static String getUserAgent(Context paramContext, String paramString)
  {
    try
    {
      String str = paramContext.getPackageName();
      paramContext = paramContext.getPackageManager().getPackageInfo(str, 0).versionName;
      return paramString + "/" + paramContext + " (Linux;Android " + Build.VERSION.RELEASE + ") " + "ExoPlayerLib/2.6.1";
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = "?";
      }
    }
  }
  
  public static byte[] getUtf8Bytes(String paramString)
  {
    return paramString.getBytes(Charset.forName("UTF-8"));
  }
  
  public static int inferContentType(Uri paramUri)
  {
    paramUri = paramUri.getPath();
    if (paramUri == null) {}
    for (int i = 3;; i = inferContentType(paramUri)) {
      return i;
    }
  }
  
  public static int inferContentType(String paramString)
  {
    paramString = toLowerInvariant(paramString);
    int i;
    if (paramString.endsWith(".mpd")) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (paramString.endsWith(".m3u8")) {
        i = 2;
      } else if (paramString.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
        i = 1;
      } else {
        i = 3;
      }
    }
  }
  
  public static boolean isEncodingHighResolutionIntegerPcm(int paramInt)
  {
    if ((paramInt == Integer.MIN_VALUE) || (paramInt == NUM)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isLinebreak(int paramInt)
  {
    if ((paramInt == 10) || (paramInt == 13)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isLocalFileUri(Uri paramUri)
  {
    paramUri = paramUri.getScheme();
    if ((TextUtils.isEmpty(paramUri)) || (paramUri.equals("file"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(23)
  public static boolean maybeRequestReadExternalStoragePermission(Activity paramActivity, Uri... paramVarArgs)
  {
    boolean bool1 = false;
    boolean bool2;
    if (SDK_INT < 23)
    {
      bool2 = bool1;
      return bool2;
    }
    int i = paramVarArgs.length;
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j >= i) {
        break;
      }
      if (isLocalFileUri(paramVarArgs[j]))
      {
        bool2 = bool1;
        if (paramActivity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
          break;
        }
        paramActivity.requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 0);
        bool2 = true;
        break;
      }
    }
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
  
  public static String normalizeLanguageCode(String paramString)
  {
    if (paramString == null) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      try
      {
        Object localObject = new java/util/Locale;
        ((Locale)localObject).<init>(paramString);
        localObject = ((Locale)localObject).getISO3Language();
        paramString = (String)localObject;
      }
      catch (MissingResourceException localMissingResourceException)
      {
        paramString = paramString.toLowerCase();
      }
    }
  }
  
  public static long parseXsDateTime(String paramString)
    throws ParserException
  {
    Matcher localMatcher = XS_DATE_TIME_PATTERN.matcher(paramString);
    if (!localMatcher.matches()) {
      throw new ParserException("Invalid date/time format: " + paramString);
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
      long l1 = paramString.getTimeInMillis();
      long l2 = l1;
      if (i != 0) {
        l2 = l1 - 60000 * i;
      }
      return l2;
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
    int i;
    double d1;
    label47:
    double d2;
    label67:
    double d3;
    label88:
    double d4;
    label109:
    double d5;
    label130:
    double d6;
    label147:
    long l1;
    if (localMatcher.matches()) {
      if (!TextUtils.isEmpty(localMatcher.group(1)))
      {
        i = 1;
        paramString = localMatcher.group(3);
        if (paramString == null) {
          break label191;
        }
        d1 = Double.parseDouble(paramString) * 3.1556908E7D;
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label196;
        }
        d2 = Double.parseDouble(paramString) * 2629739.0D;
        paramString = localMatcher.group(7);
        if (paramString == null) {
          break label202;
        }
        d3 = Double.parseDouble(paramString) * 86400.0D;
        paramString = localMatcher.group(10);
        if (paramString == null) {
          break label208;
        }
        d4 = Double.parseDouble(paramString) * 3600.0D;
        paramString = localMatcher.group(12);
        if (paramString == null) {
          break label214;
        }
        d5 = Double.parseDouble(paramString) * 60.0D;
        paramString = localMatcher.group(14);
        if (paramString == null) {
          break label220;
        }
        d6 = Double.parseDouble(paramString);
        l1 = (1000.0D * (d1 + d2 + d3 + d4 + d5 + d6));
        l2 = l1;
        if (i == 0) {}
      }
    }
    for (long l2 = -l1;; l2 = (Double.parseDouble(paramString) * 3600.0D * 1000.0D))
    {
      return l2;
      i = 0;
      break;
      label191:
      d1 = 0.0D;
      break label47;
      label196:
      d2 = 0.0D;
      break label67;
      label202:
      d3 = 0.0D;
      break label88;
      label208:
      d4 = 0.0D;
      break label109;
      label214:
      d5 = 0.0D;
      break label130;
      label220:
      d6 = 0.0D;
      break label147;
    }
  }
  
  public static void recursiveDelete(File paramFile)
  {
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        recursiveDelete(arrayOfFile[j]);
      }
    }
    paramFile.delete();
  }
  
  public static <T> void removeRange(List<T> paramList, int paramInt1, int paramInt2)
  {
    paramList.subList(paramInt1, paramInt2).clear();
  }
  
  public static long resolveSeekPositionUs(long paramLong1, SeekParameters paramSeekParameters, long paramLong2, long paramLong3)
  {
    long l1;
    if (SeekParameters.EXACT.equals(paramSeekParameters)) {
      l1 = paramLong1;
    }
    for (;;)
    {
      return l1;
      long l2 = subtractWithOverflowDefault(paramLong1, paramSeekParameters.toleranceBeforeUs, Long.MIN_VALUE);
      l1 = addWithOverflowDefault(paramLong1, paramSeekParameters.toleranceAfterUs, Long.MAX_VALUE);
      int i;
      if ((l2 <= paramLong2) && (paramLong2 <= l1))
      {
        i = 1;
        label59:
        if ((l2 > paramLong3) || (paramLong3 > l1)) {
          break label121;
        }
      }
      label121:
      for (int j = 1;; j = 0)
      {
        if ((i == 0) || (j == 0)) {
          break label127;
        }
        l1 = paramLong2;
        if (Math.abs(paramLong2 - paramLong1) <= Math.abs(paramLong3 - paramLong1)) {
          break;
        }
        l1 = paramLong3;
        break;
        i = 0;
        break label59;
      }
      label127:
      l1 = paramLong2;
      if (i == 0) {
        if (j != 0) {
          l1 = paramLong3;
        } else {
          l1 = l2;
        }
      }
    }
  }
  
  public static long scaleLargeTimestamp(long paramLong1, long paramLong2, long paramLong3)
  {
    if ((paramLong3 >= paramLong2) && (paramLong3 % paramLong2 == 0L)) {
      paramLong1 /= paramLong3 / paramLong2;
    }
    for (;;)
    {
      return paramLong1;
      if ((paramLong3 < paramLong2) && (paramLong2 % paramLong3 == 0L))
      {
        paramLong1 *= paramLong2 / paramLong3;
      }
      else
      {
        double d = paramLong2 / paramLong3;
        paramLong1 = (paramLong1 * d);
      }
    }
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
      i++;
      continue;
      if ((paramLong2 < paramLong1) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = 0;
      }
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = (((Long)paramList.get(i)).longValue() * paramLong1);
        i++;
        continue;
        double d = paramLong1 / paramLong2;
        for (i = 0; i < arrayOfLong.length; i++) {
          arrayOfLong[i] = ((((Long)paramList.get(i)).longValue() * d));
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
      i++;
      continue;
      if ((paramLong2 < paramLong1) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = 0;
      }
      while (i < paramArrayOfLong.length)
      {
        paramArrayOfLong[i] *= paramLong1;
        i++;
        continue;
        double d = paramLong1 / paramLong2;
        for (i = 0; i < paramArrayOfLong.length; i++) {
          paramArrayOfLong[i] = ((paramArrayOfLong[i] * d));
        }
      }
    }
  }
  
  private static boolean shouldEscapeCharacter(char paramChar)
  {
    switch (paramChar)
    {
    }
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  public static void sneakyThrow(Throwable paramThrowable)
  {
    sneakyThrowInternal(paramThrowable);
  }
  
  private static <T extends Throwable> void sneakyThrowInternal(Throwable paramThrowable)
    throws Throwable
  {
    throw paramThrowable;
  }
  
  public static long subtractWithOverflowDefault(long paramLong1, long paramLong2, long paramLong3)
  {
    long l = paramLong1 - paramLong2;
    if (((paramLong1 ^ paramLong2) & (paramLong1 ^ l)) < 0L) {}
    for (;;)
    {
      return paramLong3;
      paramLong3 = l;
    }
  }
  
  public static int[] toArray(List<Integer> paramList)
  {
    Object localObject;
    if (paramList == null)
    {
      localObject = null;
      return (int[])localObject;
    }
    int i = paramList.size();
    int[] arrayOfInt = new int[i];
    for (int j = 0;; j++)
    {
      localObject = arrayOfInt;
      if (j >= i) {
        break;
      }
      arrayOfInt[j] = ((Integer)paramList.get(j)).intValue();
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
    if (paramString == null) {}
    for (paramString = null;; paramString = paramString.toLowerCase(Locale.US)) {
      return paramString;
    }
  }
  
  public static String unescapeFileName(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    int k = 0;
    int m;
    while (k < i)
    {
      m = j;
      if (paramString.charAt(k) == '%') {
        m = j + 1;
      }
      k++;
      j = m;
    }
    if (j == 0) {}
    for (;;)
    {
      return paramString;
      m = i - j * 2;
      StringBuilder localStringBuilder = new StringBuilder(m);
      Matcher localMatcher = ESCAPED_CHARACTER_PATTERN.matcher(paramString);
      k = 0;
      while ((j > 0) && (localMatcher.find()))
      {
        char c = (char)Integer.parseInt(localMatcher.group(1), 16);
        localStringBuilder.append(paramString, k, localMatcher.start()).append(c);
        k = localMatcher.end();
        j--;
      }
      if (k < i) {
        localStringBuilder.append(paramString, k, i);
      }
      if (localStringBuilder.length() != m) {
        paramString = null;
      } else {
        paramString = localStringBuilder.toString();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */