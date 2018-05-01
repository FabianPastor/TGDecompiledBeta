package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog.Calls;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.ListView;
import android.widget.ScrollView;
import com.android.internal.telephony.ITelephony;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.TypefaceSpan;

public class AndroidUtilities
{
  public static final int FLAG_TAG_ALL = 3;
  public static final int FLAG_TAG_BOLD = 2;
  public static final int FLAG_TAG_BR = 1;
  public static final int FLAG_TAG_COLOR = 4;
  public static Pattern WEB_URL = null;
  public static AccelerateInterpolator accelerateInterpolator;
  private static int adjustOwnerClassGuid;
  private static RectF bitmapRect;
  private static final Object callLock;
  private static ContentObserver callLogContentObserver;
  public static DecelerateInterpolator decelerateInterpolator;
  public static float density;
  public static DisplayMetrics displayMetrics;
  public static Point displaySize;
  private static boolean hasCallPermissions;
  public static boolean incorrectDisplaySizeFix;
  public static boolean isInMultiwindow;
  private static Boolean isTablet;
  public static int leftBaseline;
  private static Field mAttachInfoField;
  private static Field mStableInsetsField;
  public static OvershootInterpolator overshootInterpolator;
  public static Integer photoSize;
  private static int prevOrientation;
  public static int roundMessageSize;
  private static Paint roundPaint;
  private static final Object smsLock;
  public static int statusBarHeight;
  private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
  private static Runnable unregisterRunnable;
  public static boolean usingHardwareInput;
  private static boolean waitingForCall;
  private static boolean waitingForSms;
  
  static
  {
    prevOrientation = -10;
    waitingForSms = false;
    waitingForCall = false;
    smsLock = new Object();
    callLock = new Object();
    statusBarHeight = 0;
    density = 1.0F;
    displaySize = new Point();
    photoSize = null;
    displayMetrics = new DisplayMetrics();
    decelerateInterpolator = new DecelerateInterpolator();
    accelerateInterpolator = new AccelerateInterpolator();
    overshootInterpolator = new OvershootInterpolator();
    isTablet = null;
    adjustOwnerClassGuid = 0;
    try
    {
      Pattern localPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localPattern = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + localPattern + ")");
      localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + localPattern + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
      if (isTablet())
      {
        i = 80;
        leftBaseline = i;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        if (Build.VERSION.SDK_INT < 23) {
          break label244;
        }
        bool = true;
        hasCallPermissions = bool;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        continue;
        int i = 72;
        continue;
        label244:
        boolean bool = false;
      }
    }
  }
  
  public static void addMediaToGallery(Uri paramUri)
  {
    if (paramUri == null) {}
    for (;;)
    {
      return;
      try
      {
        Intent localIntent = new android/content/Intent;
        localIntent.<init>("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(paramUri);
        ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
      }
    }
  }
  
  public static void addMediaToGallery(String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
      addMediaToGallery(Uri.fromFile(new File(paramString)));
    }
  }
  
  public static void addToClipboard(CharSequence paramCharSequence)
  {
    try
    {
      ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", paramCharSequence));
      return;
    }
    catch (Exception paramCharSequence)
    {
      for (;;)
      {
        FileLog.e(paramCharSequence);
      }
    }
  }
  
  public static byte[] calcAuthKeyHash(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = Utilities.computeSHA1(paramArrayOfByte);
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 16);
    return arrayOfByte;
  }
  
  public static int[] calcDrawableColor(Drawable paramDrawable)
  {
    int i = -16777216;
    j = i;
    for (;;)
    {
      try
      {
        if (!(paramDrawable instanceof BitmapDrawable)) {
          continue;
        }
        j = i;
        paramDrawable = ((BitmapDrawable)paramDrawable).getBitmap();
        k = i;
        if (paramDrawable != null)
        {
          j = i;
          Bitmap localBitmap = Bitmaps.createScaledBitmap(paramDrawable, 1, 1, true);
          k = i;
          if (localBitmap != null)
          {
            j = i;
            i = localBitmap.getPixel(0, 0);
            k = i;
            if (paramDrawable != localBitmap)
            {
              j = i;
              localBitmap.recycle();
              k = i;
            }
          }
        }
      }
      catch (Exception paramDrawable)
      {
        FileLog.e(paramDrawable);
        int k = j;
        continue;
      }
      paramDrawable = rgbToHsv(k >> 16 & 0xFF, k >> 8 & 0xFF, k & 0xFF);
      paramDrawable[1] = Math.min(1.0D, paramDrawable[1] + 0.05D + 0.1D * (1.0D - paramDrawable[1]));
      paramDrawable[2] = Math.max(0.0D, paramDrawable[2] * 0.65D);
      paramDrawable = hsvToRgb(paramDrawable[0], paramDrawable[1], paramDrawable[2]);
      return new int[] { Color.argb(102, paramDrawable[0], paramDrawable[1], paramDrawable[2]), Color.argb(136, paramDrawable[0], paramDrawable[1], paramDrawable[2]) };
      k = i;
      j = i;
      if ((paramDrawable instanceof ColorDrawable))
      {
        j = i;
        k = ((ColorDrawable)paramDrawable).getColor();
      }
    }
  }
  
  public static void cancelRunOnUIThread(Runnable paramRunnable)
  {
    ApplicationLoader.applicationHandler.removeCallbacks(paramRunnable);
  }
  
  public static void checkDisplaySize(Context paramContext, Configuration paramConfiguration)
  {
    boolean bool = true;
    for (;;)
    {
      try
      {
        density = paramContext.getResources().getDisplayMetrics().density;
        Configuration localConfiguration = paramConfiguration;
        paramConfiguration = localConfiguration;
        if (localConfiguration == null) {
          paramConfiguration = paramContext.getResources().getConfiguration();
        }
        if ((paramConfiguration.keyboard != 1) && (paramConfiguration.hardKeyboardHidden == 1))
        {
          usingHardwareInput = bool;
          paramContext = (WindowManager)paramContext.getSystemService("window");
          if (paramContext != null)
          {
            paramContext = paramContext.getDefaultDisplay();
            if (paramContext != null)
            {
              paramContext.getMetrics(displayMetrics);
              paramContext.getSize(displaySize);
            }
          }
          int i;
          if (paramConfiguration.screenWidthDp != 0)
          {
            i = (int)Math.ceil(paramConfiguration.screenWidthDp * density);
            if (Math.abs(displaySize.x - i) > 3) {
              displaySize.x = i;
            }
          }
          if (paramConfiguration.screenHeightDp != 0)
          {
            i = (int)Math.ceil(paramConfiguration.screenHeightDp * density);
            if (Math.abs(displaySize.y - i) > 3) {
              displaySize.y = i;
            }
          }
          if (roundMessageSize == 0)
          {
            if (!isTablet()) {
              continue;
            }
            roundMessageSize = (int)(getMinTabletSide() * 0.6F);
          }
          if (BuildVars.LOGS_ENABLED)
          {
            paramContext = new java/lang/StringBuilder;
            paramContext.<init>();
            FileLog.e("display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
          }
          return;
        }
      }
      catch (Exception paramContext)
      {
        FileLog.e(paramContext);
        continue;
      }
      bool = false;
      continue;
      roundMessageSize = (int)(Math.min(displaySize.x, displaySize.y) * 0.6F);
    }
  }
  
  public static void checkForCrashes(Activity paramActivity)
  {
    if (BuildVars.DEBUG_VERSION) {}
    for (String str = BuildVars.HOCKEY_APP_HASH_DEBUG;; str = BuildVars.HOCKEY_APP_HASH)
    {
      CrashManager.register(paramActivity, str, new CrashManagerListener()
      {
        public boolean includeDeviceData()
        {
          return true;
        }
      });
      return;
    }
  }
  
  public static void checkForUpdates(Activity paramActivity)
  {
    if (BuildVars.DEBUG_VERSION) {
      if (!BuildVars.DEBUG_VERSION) {
        break label22;
      }
    }
    label22:
    for (String str = BuildVars.HOCKEY_APP_HASH_DEBUG;; str = BuildVars.HOCKEY_APP_HASH)
    {
      UpdateManager.register(paramActivity, str);
      return;
    }
  }
  
  public static boolean checkPhonePattern(String paramString1, String paramString2)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (!TextUtils.isEmpty(paramString1))
    {
      if (paramString1.equals("*")) {
        bool2 = bool1;
      }
    }
    else {
      return bool2;
    }
    paramString1 = paramString1.split("\\*");
    paramString2 = PhoneFormat.stripExceptNumbers(paramString2);
    int i = 0;
    int j = 0;
    for (;;)
    {
      bool2 = bool1;
      if (j >= paramString1.length) {
        break;
      }
      CharSequence localCharSequence = paramString1[j];
      int k = i;
      if (!TextUtils.isEmpty(localCharSequence))
      {
        k = paramString2.indexOf(localCharSequence, i);
        if (k == -1)
        {
          bool2 = false;
          break;
        }
        k += localCharSequence.length();
      }
      j++;
      i = k;
    }
  }
  
  @SuppressLint({"NewApi"})
  public static void clearDrawableAnimation(View paramView)
  {
    if ((Build.VERSION.SDK_INT < 21) || (paramView == null)) {}
    for (;;)
    {
      return;
      if ((paramView instanceof ListView))
      {
        paramView = ((ListView)paramView).getSelector();
        if (paramView != null) {
          paramView.setState(StateSet.NOTHING);
        }
      }
      else
      {
        paramView = paramView.getBackground();
        if (paramView != null)
        {
          paramView.setState(StateSet.NOTHING);
          paramView.jumpToCurrentState();
        }
      }
    }
  }
  
  public static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return paramInt1;
      if (paramInt1 > paramInt2) {
        paramInt1 = 1;
      } else {
        paramInt1 = -1;
      }
    }
  }
  
  /* Error */
  public static boolean copyFile(File paramFile1, File paramFile2)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 535	java/io/File:exists	()Z
    //   4: ifne +8 -> 12
    //   7: aload_1
    //   8: invokevirtual 538	java/io/File:createNewFile	()Z
    //   11: pop
    //   12: aconst_null
    //   13: astore_2
    //   14: aconst_null
    //   15: astore_3
    //   16: aconst_null
    //   17: astore 4
    //   19: aconst_null
    //   20: astore 5
    //   22: aconst_null
    //   23: astore 6
    //   25: aload 4
    //   27: astore 7
    //   29: aload_2
    //   30: astore 8
    //   32: new 540	java/io/FileInputStream
    //   35: astore 9
    //   37: aload 4
    //   39: astore 7
    //   41: aload_2
    //   42: astore 8
    //   44: aload 9
    //   46: aload_0
    //   47: invokespecial 543	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   50: new 545	java/io/FileOutputStream
    //   53: astore_0
    //   54: aload_0
    //   55: aload_1
    //   56: invokespecial 546	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   59: aload_0
    //   60: invokevirtual 550	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   63: aload 9
    //   65: invokevirtual 551	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   68: lconst_0
    //   69: aload 9
    //   71: invokevirtual 551	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   74: invokevirtual 557	java/nio/channels/FileChannel:size	()J
    //   77: invokevirtual 561	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   80: pop2
    //   81: aload 9
    //   83: ifnull +8 -> 91
    //   86: aload 9
    //   88: invokevirtual 564	java/io/FileInputStream:close	()V
    //   91: aload_0
    //   92: ifnull +7 -> 99
    //   95: aload_0
    //   96: invokevirtual 565	java/io/FileOutputStream:close	()V
    //   99: iconst_1
    //   100: istore 10
    //   102: iload 10
    //   104: ireturn
    //   105: astore 9
    //   107: aload_3
    //   108: astore_0
    //   109: aload 6
    //   111: astore_1
    //   112: aload_1
    //   113: astore 7
    //   115: aload_0
    //   116: astore 8
    //   118: aload 9
    //   120: invokestatic 195	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   123: iconst_0
    //   124: istore 11
    //   126: aload_0
    //   127: ifnull +7 -> 134
    //   130: aload_0
    //   131: invokevirtual 564	java/io/FileInputStream:close	()V
    //   134: iload 11
    //   136: istore 10
    //   138: aload_1
    //   139: ifnull -37 -> 102
    //   142: aload_1
    //   143: invokevirtual 565	java/io/FileOutputStream:close	()V
    //   146: iload 11
    //   148: istore 10
    //   150: goto -48 -> 102
    //   153: astore_0
    //   154: aload 8
    //   156: ifnull +8 -> 164
    //   159: aload 8
    //   161: invokevirtual 564	java/io/FileInputStream:close	()V
    //   164: aload 7
    //   166: ifnull +8 -> 174
    //   169: aload 7
    //   171: invokevirtual 565	java/io/FileOutputStream:close	()V
    //   174: aload_0
    //   175: athrow
    //   176: astore_0
    //   177: aload 5
    //   179: astore 7
    //   181: aload 9
    //   183: astore 8
    //   185: goto -31 -> 154
    //   188: astore_1
    //   189: aload_0
    //   190: astore 7
    //   192: aload_1
    //   193: astore_0
    //   194: aload 9
    //   196: astore 8
    //   198: goto -44 -> 154
    //   201: astore 7
    //   203: aload 9
    //   205: astore_0
    //   206: aload 6
    //   208: astore_1
    //   209: aload 7
    //   211: astore 9
    //   213: goto -101 -> 112
    //   216: astore 7
    //   218: aload_0
    //   219: astore_1
    //   220: aload 9
    //   222: astore_0
    //   223: aload 7
    //   225: astore 9
    //   227: goto -115 -> 112
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	230	0	paramFile1	File
    //   0	230	1	paramFile2	File
    //   13	29	2	localObject1	Object
    //   15	93	3	localObject2	Object
    //   17	21	4	localObject3	Object
    //   20	158	5	localObject4	Object
    //   23	184	6	localObject5	Object
    //   27	164	7	localObject6	Object
    //   201	9	7	localException1	Exception
    //   216	8	7	localException2	Exception
    //   30	167	8	localObject7	Object
    //   35	52	9	localFileInputStream	java.io.FileInputStream
    //   105	99	9	localException3	Exception
    //   211	15	9	localException4	Exception
    //   100	49	10	bool1	boolean
    //   124	23	11	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   32	37	105	java/lang/Exception
    //   44	50	105	java/lang/Exception
    //   32	37	153	finally
    //   44	50	153	finally
    //   118	123	153	finally
    //   50	59	176	finally
    //   59	81	188	finally
    //   50	59	201	java/lang/Exception
    //   59	81	216	java/lang/Exception
  }
  
  public static boolean copyFile(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    paramFile = new FileOutputStream(paramFile);
    byte[] arrayOfByte = new byte['က'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0) {
        break;
      }
      Thread.yield();
      paramFile.write(arrayOfByte, 0, i);
    }
    paramFile.close();
    return true;
  }
  
  public static byte[] decodeQuotedPrintable(byte[] paramArrayOfByte)
  {
    Object localObject = null;
    if (paramArrayOfByte == null) {
      paramArrayOfByte = (byte[])localObject;
    }
    for (;;)
    {
      return paramArrayOfByte;
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      int i = 0;
      if (i < paramArrayOfByte.length)
      {
        int j = paramArrayOfByte[i];
        if (j == 61) {
          i++;
        }
        for (;;)
        {
          try
          {
            j = Character.digit((char)paramArrayOfByte[i], 16);
            i++;
            localByteArrayOutputStream.write((char)((j << 4) + Character.digit((char)paramArrayOfByte[i], 16)));
            i++;
          }
          catch (Exception paramArrayOfByte)
          {
            FileLog.e(paramArrayOfByte);
            paramArrayOfByte = (byte[])localObject;
          }
          break;
          localByteArrayOutputStream.write(j);
        }
      }
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      try
      {
        localByteArrayOutputStream.close();
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static int dp(float paramFloat)
  {
    if (paramFloat == 0.0F) {}
    for (int i = 0;; i = (int)Math.ceil(density * paramFloat)) {
      return i;
    }
  }
  
  public static int dp2(float paramFloat)
  {
    if (paramFloat == 0.0F) {}
    for (int i = 0;; i = (int)Math.floor(density * paramFloat)) {
      return i;
    }
  }
  
  public static float dpf2(float paramFloat)
  {
    float f = 0.0F;
    if (paramFloat == 0.0F) {}
    for (paramFloat = f;; paramFloat = density * paramFloat) {
      return paramFloat;
    }
  }
  
  public static void endIncomingCall()
  {
    if (!hasCallPermissions) {}
    for (;;)
    {
      return;
      try
      {
        TelephonyManager localTelephonyManager = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        Method localMethod = Class.forName(localTelephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
        localMethod.setAccessible(true);
        ITelephony localITelephony = (ITelephony)localMethod.invoke(localTelephonyManager, new Object[0]);
        localITelephony = (ITelephony)localMethod.invoke(localTelephonyManager, new Object[0]);
        localITelephony.silenceRinger();
        localITelephony.endCall();
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static String formatFileSize(long paramLong)
  {
    String str;
    if (paramLong < 1024L) {
      str = String.format("%d B", new Object[] { Long.valueOf(paramLong) });
    }
    for (;;)
    {
      return str;
      if (paramLong < 1048576L) {
        str = String.format("%.1f KB", new Object[] { Float.valueOf((float)paramLong / 1024.0F) });
      } else if (paramLong < 1073741824L) {
        str = String.format("%.1f MB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F) });
      } else {
        str = String.format("%.1f GB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F / 1024.0F) });
      }
    }
  }
  
  public static File generatePicturePath()
  {
    try
    {
      File localFile = getAlbumDir();
      Object localObject1 = new java/util/Date;
      ((Date)localObject1).<init>();
      ((Date)localObject1).setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1L);
      Object localObject3 = new java/text/SimpleDateFormat;
      ((SimpleDateFormat)localObject3).<init>("yyyyMMdd_HHmmss_SSS", Locale.US);
      String str = ((SimpleDateFormat)localObject3).format((Date)localObject1);
      localObject1 = new java/io/File;
      localObject3 = new java/lang/StringBuilder;
      ((StringBuilder)localObject3).<init>();
      ((File)localObject1).<init>(localFile, "IMG_" + str + ".jpg");
      return (File)localObject1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        Object localObject2 = null;
      }
    }
  }
  
  public static CharSequence generateSearchName(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      paramString1 = "";
    }
    for (;;)
    {
      return paramString1;
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      String str = paramString1;
      int i;
      label72:
      int k;
      label114:
      int m;
      if ((str == null) || (str.length() == 0))
      {
        paramString1 = paramString2;
        paramString2 = paramString1.trim();
        paramString1 = " " + paramString2.toLowerCase();
        i = 0;
        int j = paramString1.indexOf(" " + paramString3, i);
        if (j == -1) {
          break label347;
        }
        if (j != 0) {
          break label310;
        }
        k = 0;
        m = j - k;
        int n = paramString3.length();
        if (j != 0) {
          break label316;
        }
        k = 0;
        label135:
        k = k + n + m;
        if ((i == 0) || (i == m + 1)) {
          break label322;
        }
        localSpannableStringBuilder.append(paramString2.substring(i, m));
      }
      for (;;)
      {
        str = paramString2.substring(m, Math.min(paramString2.length(), k));
        if (str.startsWith(" ")) {
          localSpannableStringBuilder.append(" ");
        }
        str = str.trim();
        i = localSpannableStringBuilder.length();
        localSpannableStringBuilder.append(str);
        localSpannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i, str.length() + i, 33);
        i = k;
        break label72;
        paramString1 = str;
        if (paramString2 == null) {
          break;
        }
        paramString1 = str;
        if (paramString2.length() == 0) {
          break;
        }
        paramString1 = str + " " + paramString2;
        break;
        label310:
        k = 1;
        break label114;
        label316:
        k = 1;
        break label135;
        label322:
        if ((i == 0) && (m != 0)) {
          localSpannableStringBuilder.append(paramString2.substring(0, m));
        }
      }
      label347:
      paramString1 = localSpannableStringBuilder;
      if (i != -1)
      {
        paramString1 = localSpannableStringBuilder;
        if (i < paramString2.length())
        {
          localSpannableStringBuilder.append(paramString2.substring(i, paramString2.length()));
          paramString1 = localSpannableStringBuilder;
        }
      }
    }
  }
  
  public static File generateVideoPath()
  {
    try
    {
      File localFile = getAlbumDir();
      Object localObject1 = new java/util/Date;
      ((Date)localObject1).<init>();
      ((Date)localObject1).setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1L);
      Object localObject3 = new java/text/SimpleDateFormat;
      ((SimpleDateFormat)localObject3).<init>("yyyyMMdd_HHmmss_SSS", Locale.US);
      String str = ((SimpleDateFormat)localObject3).format((Date)localObject1);
      localObject1 = new java/io/File;
      localObject3 = new java/lang/StringBuilder;
      ((StringBuilder)localObject3).<init>();
      ((File)localObject1).<init>(localFile, "VID_" + str + ".mp4");
      return (File)localObject1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        Object localObject2 = null;
      }
    }
  }
  
  private static File getAlbumDir()
  {
    Object localObject;
    if ((Build.VERSION.SDK_INT >= 23) && (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
      localObject = FileLoader.getDirectory(4);
    }
    for (;;)
    {
      return (File)localObject;
      File localFile = null;
      if ("mounted".equals(Environment.getExternalStorageState()))
      {
        localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
        localObject = localFile;
        if (!localFile.mkdirs())
        {
          localObject = localFile;
          if (!localFile.exists())
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("failed to create directory");
            }
            localObject = null;
          }
        }
      }
      else
      {
        localObject = localFile;
        if (BuildVars.LOGS_ENABLED)
        {
          FileLog.d("External storage is not mounted READ/WRITE.");
          localObject = localFile;
        }
      }
    }
  }
  
  public static File getCacheDir()
  {
    Object localObject1 = null;
    try
    {
      String str = Environment.getExternalStorageState();
      localObject1 = str;
    }
    catch (Exception localException3)
    {
      for (;;)
      {
        try
        {
          localObject1 = ApplicationLoader.applicationContext.getExternalCacheDir();
          if (localObject1 == null) {
            break;
          }
          return (File)localObject1;
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
        localException3 = localException3;
        FileLog.e(localException3);
      }
    }
    if ((localObject1 == null) || (((String)localObject1).startsWith("mounted"))) {}
    for (;;)
    {
      try
      {
        File localFile = ApplicationLoader.applicationContext.getCacheDir();
        localObject2 = localFile;
        if (localFile != null) {
          continue;
        }
      }
      catch (Exception localException2)
      {
        Object localObject2;
        FileLog.e(localException2);
        continue;
      }
      localObject2 = new File("");
    }
  }
  
  public static String getDataColumn(Context paramContext, Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    localUri1 = null;
    localUri2 = null;
    for (;;)
    {
      try
      {
        paramUri = paramContext.getContentResolver().query(paramUri, new String[] { "_data" }, paramString, paramArrayOfString, null);
        if (paramUri != null)
        {
          localUri2 = paramUri;
          localUri1 = paramUri;
          if (paramUri.moveToFirst())
          {
            localUri2 = paramUri;
            localUri1 = paramUri;
            paramString = paramUri.getString(paramUri.getColumnIndexOrThrow("_data"));
            localUri2 = paramUri;
            localUri1 = paramUri;
            if (!paramString.startsWith("content://"))
            {
              localUri2 = paramUri;
              localUri1 = paramUri;
              if (!paramString.startsWith("/"))
              {
                localUri2 = paramUri;
                localUri1 = paramUri;
                boolean bool = paramString.startsWith("file://");
                if (bool) {}
              }
            }
            else
            {
              if (paramUri != null) {
                paramUri.close();
              }
              paramContext = null;
              return paramContext;
            }
            paramContext = paramString;
            if (paramUri == null) {
              continue;
            }
            paramUri.close();
            paramContext = paramString;
            continue;
          }
        }
      }
      catch (Exception paramContext)
      {
        if (localUri2 == null) {
          continue;
        }
        localUri2.close();
        continue;
      }
      finally
      {
        if (localUri1 == null) {
          continue;
        }
        localUri1.close();
      }
      paramContext = null;
    }
  }
  
  public static int getMinTabletSide()
  {
    int i;
    int j;
    if (!isSmallTablet())
    {
      i = Math.min(displaySize.x, displaySize.y);
      j = i * 35 / 100;
      k = j;
      if (j < dp(320.0F)) {
        k = dp(320.0F);
      }
    }
    int m;
    for (int k = i - k;; k = Math.min(i, m - k))
    {
      return k;
      i = Math.min(displaySize.x, displaySize.y);
      m = Math.max(displaySize.x, displaySize.y);
      j = m * 35 / 100;
      k = j;
      if (j < dp(320.0F)) {
        k = dp(320.0F);
      }
    }
  }
  
  public static int getMyLayerVersion(int paramInt)
  {
    return 0xFFFF & paramInt;
  }
  
  @SuppressLint({"NewApi"})
  public static String getPath(Uri paramUri)
  {
    localObject1 = null;
    int i = 0;
    for (;;)
    {
      try
      {
        if (Build.VERSION.SDK_INT < 19) {
          continue;
        }
        j = 1;
        if ((j == 0) || (!DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, paramUri))) {
          continue;
        }
        if (!isExternalStorageDocument(paramUri)) {
          continue;
        }
        paramUri = DocumentsContract.getDocumentId(paramUri).split(":");
        localObject2 = localObject1;
        if ("primary".equalsIgnoreCase(paramUri[0]))
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject2 = Environment.getExternalStorageDirectory() + "/" + paramUri[1];
        }
      }
      catch (Exception paramUri)
      {
        int j;
        Object localObject3;
        FileLog.e(paramUri);
        Object localObject2 = localObject1;
        continue;
      }
      return (String)localObject2;
      j = 0;
      continue;
      if (isDownloadsDocument(paramUri))
      {
        paramUri = DocumentsContract.getDocumentId(paramUri);
        paramUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(paramUri).longValue());
        localObject2 = getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
      }
      else
      {
        localObject2 = localObject1;
        if (isMediaDocument(paramUri))
        {
          localObject2 = DocumentsContract.getDocumentId(paramUri).split(":");
          localObject3 = localObject2[0];
          paramUri = null;
          switch (((String)localObject3).hashCode())
          {
          default: 
            j = -1;
            switch (j)
            {
            default: 
              localObject2 = localObject2[1];
              localObject2 = getDataColumn(ApplicationLoader.applicationContext, paramUri, "_id=?", new String[] { localObject2 });
            }
            break;
          case 100313435: 
            if (((String)localObject3).equals("image")) {
              j = i;
            }
            break;
          case 112202875: 
            if (((String)localObject3).equals("video")) {
              j = 1;
            }
            break;
          case 93166550: 
            if (((String)localObject3).equals("audio"))
            {
              j = 2;
              continue;
              paramUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
              continue;
              paramUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
              continue;
              paramUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
              continue;
              if ("content".equalsIgnoreCase(paramUri.getScheme()))
              {
                localObject2 = getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
              }
              else
              {
                localObject2 = localObject1;
                if ("file".equalsIgnoreCase(paramUri.getScheme())) {
                  localObject2 = paramUri.getPath();
                }
              }
            }
            break;
          }
        }
      }
    }
  }
  
  public static int getPeerLayerVersion(int paramInt)
  {
    return paramInt >> 16 & 0xFFFF;
  }
  
  public static int getPhotoSize()
  {
    if (photoSize == null) {
      photoSize = Integer.valueOf(1280);
    }
    return photoSize.intValue();
  }
  
  public static float getPixelsInCM(float paramFloat, boolean paramBoolean)
  {
    float f = paramFloat / 2.54F;
    if (paramBoolean) {}
    for (paramFloat = displayMetrics.xdpi;; paramFloat = displayMetrics.ydpi) {
      return paramFloat * f;
    }
  }
  
  public static Point getRealScreenSize()
  {
    Point localPoint = new Point();
    try
    {
      WindowManager localWindowManager = (WindowManager)ApplicationLoader.applicationContext.getSystemService("window");
      if (Build.VERSION.SDK_INT >= 17) {
        localWindowManager.getDefaultDisplay().getRealSize(localPoint);
      }
      for (;;)
      {
        return localPoint;
        try
        {
          Method localMethod1 = Display.class.getMethod("getRawWidth", new Class[0]);
          Method localMethod2 = Display.class.getMethod("getRawHeight", new Class[0]);
          localPoint.set(((Integer)localMethod1.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer)localMethod2.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue());
        }
        catch (Exception localException2)
        {
          localPoint.set(localWindowManager.getDefaultDisplay().getWidth(), localWindowManager.getDefaultDisplay().getHeight());
          FileLog.e(localException2);
        }
      }
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        FileLog.e(localException1);
      }
    }
  }
  
  public static CharSequence getTrimmedString(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence = paramCharSequence;
    if (paramCharSequence != null)
    {
      localCharSequence = paramCharSequence;
      if (paramCharSequence.length() == 0) {
        localCharSequence = paramCharSequence;
      }
    }
    else
    {
      return localCharSequence;
    }
    for (;;)
    {
      paramCharSequence = localCharSequence;
      if (localCharSequence.length() <= 0) {
        break;
      }
      if (localCharSequence.charAt(0) != '\n')
      {
        paramCharSequence = localCharSequence;
        if (localCharSequence.charAt(0) != ' ') {
          break;
        }
      }
      localCharSequence = localCharSequence.subSequence(1, localCharSequence.length());
    }
    for (;;)
    {
      localCharSequence = paramCharSequence;
      if (paramCharSequence.length() <= 0) {
        break;
      }
      if (paramCharSequence.charAt(paramCharSequence.length() - 1) != '\n')
      {
        localCharSequence = paramCharSequence;
        if (paramCharSequence.charAt(paramCharSequence.length() - 1) != ' ') {
          break;
        }
      }
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length() - 1);
    }
  }
  
  public static Typeface getTypeface(String paramString)
  {
    synchronized (typefaceCache)
    {
      boolean bool = typefaceCache.containsKey(paramString);
      if (!bool) {}
      try
      {
        Typeface localTypeface = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), paramString);
        typefaceCache.put(paramString, localTypeface);
        paramString = (Typeface)typefaceCache.get(paramString);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          if (BuildVars.LOGS_ENABLED)
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            FileLog.e("Could not get typeface '" + paramString + "' because " + localException.getMessage());
          }
          paramString = null;
        }
      }
      return paramString;
    }
  }
  
  public static int getViewInset(View paramView)
  {
    int i = 0;
    int j = i;
    if (paramView != null)
    {
      j = i;
      if (Build.VERSION.SDK_INT >= 21)
      {
        j = i;
        if (paramView.getHeight() != displaySize.y)
        {
          if (paramView.getHeight() != displaySize.y - statusBarHeight) {
            break label54;
          }
          j = i;
        }
      }
    }
    for (;;)
    {
      return j;
      try
      {
        label54:
        if (mAttachInfoField == null)
        {
          mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
          mAttachInfoField.setAccessible(true);
        }
        paramView = mAttachInfoField.get(paramView);
        j = i;
        if (paramView != null)
        {
          if (mStableInsetsField == null)
          {
            mStableInsetsField = paramView.getClass().getDeclaredField("mStableInsets");
            mStableInsetsField.setAccessible(true);
          }
          j = ((Rect)mStableInsetsField.get(paramView)).bottom;
        }
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
        j = i;
      }
    }
  }
  
  public static boolean handleProxyIntent(Activity paramActivity, Intent paramIntent)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramIntent == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      try
      {
        if ((paramIntent.getFlags() & 0x100000) != 0) {
          continue;
        }
        Object localObject1 = paramIntent.getData();
        bool2 = bool1;
        if (localObject1 == null) {
          continue;
        }
        Object localObject2 = null;
        Object localObject3 = null;
        Object localObject4 = null;
        Object localObject5 = null;
        String str = ((Uri)localObject1).getScheme();
        Object localObject6 = localObject5;
        paramIntent = (Intent)localObject3;
        Object localObject7 = localObject4;
        Object localObject8 = localObject2;
        if (str != null)
        {
          if ((!str.equals("http")) && (!str.equals("https"))) {
            break label321;
          }
          str = ((Uri)localObject1).getHost().toLowerCase();
          if ((!str.equals("telegram.me")) && (!str.equals("t.me")) && (!str.equals("telegram.dog")))
          {
            localObject6 = localObject5;
            paramIntent = (Intent)localObject3;
            localObject7 = localObject4;
            localObject8 = localObject2;
            if (!str.equals("telesco.pe")) {}
          }
          else
          {
            str = ((Uri)localObject1).getPath();
            localObject6 = localObject5;
            paramIntent = (Intent)localObject3;
            localObject7 = localObject4;
            localObject8 = localObject2;
            if (str != null)
            {
              localObject6 = localObject5;
              paramIntent = (Intent)localObject3;
              localObject7 = localObject4;
              localObject8 = localObject2;
              if (str.startsWith("/socks"))
              {
                localObject6 = ((Uri)localObject1).getQueryParameter("server");
                localObject7 = ((Uri)localObject1).getQueryParameter("port");
                localObject8 = ((Uri)localObject1).getQueryParameter("user");
                paramIntent = ((Uri)localObject1).getQueryParameter("pass");
              }
            }
          }
        }
        for (;;)
        {
          bool2 = bool1;
          if (TextUtils.isEmpty((CharSequence)localObject6)) {
            break;
          }
          bool2 = bool1;
          if (TextUtils.isEmpty((CharSequence)localObject7)) {
            break;
          }
          localObject4 = localObject8;
          if (localObject8 == null) {
            localObject4 = "";
          }
          localObject8 = paramIntent;
          if (paramIntent == null) {
            localObject8 = "";
          }
          showProxyAlert(paramActivity, (String)localObject6, (String)localObject7, (String)localObject4, (String)localObject8);
          bool2 = true;
          break;
          label321:
          localObject6 = localObject5;
          paramIntent = (Intent)localObject3;
          localObject7 = localObject4;
          localObject8 = localObject2;
          if (str.equals("tg"))
          {
            localObject1 = ((Uri)localObject1).toString();
            if (!((String)localObject1).startsWith("tg:socks"))
            {
              localObject6 = localObject5;
              paramIntent = (Intent)localObject3;
              localObject7 = localObject4;
              localObject8 = localObject2;
              if (!((String)localObject1).startsWith("tg://socks")) {}
            }
            else
            {
              paramIntent = Uri.parse(((String)localObject1).replace("tg:proxy", "tg://telegram.org").replace("tg://proxy", "tg://telegram.org"));
              localObject6 = paramIntent.getQueryParameter("server");
              localObject7 = paramIntent.getQueryParameter("port");
              localObject8 = paramIntent.getQueryParameter("user");
              paramIntent = paramIntent.getQueryParameter("pass");
            }
          }
        }
      }
      catch (Exception paramActivity)
      {
        bool2 = bool1;
      }
    }
  }
  
  public static void hideKeyboard(View paramView)
  {
    if (paramView == null) {}
    for (;;)
    {
      return;
      try
      {
        InputMethodManager localInputMethodManager = (InputMethodManager)paramView.getContext().getSystemService("input_method");
        if (localInputMethodManager.isActive()) {
          localInputMethodManager.hideSoftInputFromWindow(paramView.getWindowToken(), 0);
        }
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }
  }
  
  private static int[] hsvToRgb(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = (int)Math.floor(6.0D * paramDouble1);
    double d5 = 6.0D * paramDouble1 - d4;
    paramDouble1 = paramDouble3 * (1.0D - paramDouble2);
    double d6 = paramDouble3 * (1.0D - d5 * paramDouble2);
    paramDouble2 = paramDouble3 * (1.0D - (1.0D - d5) * paramDouble2);
    switch ((int)d4 % 6)
    {
    default: 
      paramDouble3 = d1;
      paramDouble2 = d2;
      paramDouble1 = d3;
    }
    for (;;)
    {
      return new int[] { (int)(255.0D * paramDouble3), (int)(255.0D * paramDouble2), (int)(255.0D * paramDouble1) };
      continue;
      paramDouble2 = paramDouble3;
      paramDouble3 = d6;
      continue;
      d6 = paramDouble1;
      paramDouble1 = paramDouble2;
      paramDouble2 = paramDouble3;
      paramDouble3 = d6;
      continue;
      d3 = paramDouble1;
      paramDouble2 = d6;
      paramDouble1 = paramDouble3;
      paramDouble3 = d3;
      continue;
      d6 = paramDouble2;
      paramDouble2 = paramDouble1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d6;
      continue;
      paramDouble2 = paramDouble1;
      paramDouble1 = d6;
    }
  }
  
  public static boolean isBannedForever(int paramInt)
  {
    if (Math.abs(paramInt - System.currentTimeMillis() / 1000L) > 157680000L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isDownloadsDocument(Uri paramUri)
  {
    return "com.android.providers.downloads.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isExternalStorageDocument(Uri paramUri)
  {
    return "com.android.externalstorage.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isGoogleMapsInstalled(BaseFragment paramBaseFragment)
  {
    bool1 = false;
    try
    {
      ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
      bool2 = true;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        boolean bool2 = bool1;
        if (paramBaseFragment.getParentActivity() != null)
        {
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
          localBuilder.setMessage("Install Google Maps?");
          localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              try
              {
                paramAnonymousDialogInterface = new android/content/Intent;
                paramAnonymousDialogInterface.<init>("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps"));
                this.val$fragment.getParentActivity().startActivityForResult(paramAnonymousDialogInterface, 500);
                return;
              }
              catch (Exception paramAnonymousDialogInterface)
              {
                for (;;)
                {
                  FileLog.e(paramAnonymousDialogInterface);
                }
              }
            }
          });
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          paramBaseFragment.showDialog(localBuilder.create());
          bool2 = bool1;
        }
      }
    }
    return bool2;
  }
  
  public static boolean isInternalUri(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject = paramUri.getPath();
    paramUri = (Uri)localObject;
    boolean bool2;
    if (localObject == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      do
      {
        paramUri = (Uri)localObject;
        localObject = Utilities.readlink(paramUri);
      } while ((localObject != null) && (!((String)localObject).equals(paramUri)));
      localObject = paramUri;
      if (paramUri != null) {}
      try
      {
        localObject = new java/io/File;
        ((File)localObject).<init>(paramUri);
        String str = ((File)localObject).getCanonicalPath();
        localObject = paramUri;
        if (str != null) {
          localObject = str;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          paramUri.replace("/./", "/");
          Uri localUri = paramUri;
        }
      }
      bool2 = bool1;
      if (localObject != null)
      {
        bool2 = bool1;
        if (((String)localObject).toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName() + "/files")) {
          bool2 = true;
        }
      }
    }
  }
  
  public static boolean isKeyboardShowed(View paramView)
  {
    boolean bool1 = false;
    if (paramView == null) {}
    for (;;)
    {
      return bool1;
      try
      {
        boolean bool2 = ((InputMethodManager)paramView.getContext().getSystemService("input_method")).isActive(paramView);
        bool1 = bool2;
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }
  }
  
  public static boolean isMediaDocument(Uri paramUri)
  {
    return "com.android.providers.media.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isSmallTablet()
  {
    if (Math.min(displaySize.x, displaySize.y) / density <= 700.0F) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isTablet()
  {
    if (isTablet == null) {
      isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(NUM));
    }
    return isTablet.booleanValue();
  }
  
  public static boolean isWaitingForCall()
  {
    synchronized (callLock)
    {
      boolean bool = waitingForCall;
      return bool;
    }
  }
  
  public static boolean isWaitingForSms()
  {
    synchronized (smsLock)
    {
      boolean bool = waitingForSms;
      return bool;
    }
  }
  
  public static void lockOrientation(Activity paramActivity)
  {
    if ((paramActivity == null) || (prevOrientation != -10)) {}
    for (;;)
    {
      return;
      int i;
      int j;
      try
      {
        prevOrientation = paramActivity.getRequestedOrientation();
        WindowManager localWindowManager = (WindowManager)paramActivity.getSystemService("window");
        if ((localWindowManager == null) || (localWindowManager.getDefaultDisplay() == null)) {
          continue;
        }
        i = localWindowManager.getDefaultDisplay().getRotation();
        j = paramActivity.getResources().getConfiguration().orientation;
        if (i != 3) {
          break label100;
        }
        if (j != 1) {
          break label91;
        }
        paramActivity.setRequestedOrientation(1);
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
      }
      continue;
      label91:
      paramActivity.setRequestedOrientation(8);
      continue;
      label100:
      if (i == 1)
      {
        if (j == 1) {
          paramActivity.setRequestedOrientation(9);
        } else {
          paramActivity.setRequestedOrientation(0);
        }
      }
      else if (i == 0)
      {
        if (j == 2) {
          paramActivity.setRequestedOrientation(0);
        } else {
          paramActivity.setRequestedOrientation(1);
        }
      }
      else if (j == 2) {
        paramActivity.setRequestedOrientation(8);
      } else {
        paramActivity.setRequestedOrientation(9);
      }
    }
  }
  
  public static long makeBroadcastId(int paramInt)
  {
    return 0x100000000 | paramInt & 0xFFFFFFFF;
  }
  
  public static boolean needShowPasscode(boolean paramBoolean)
  {
    boolean bool = ForegroundDetector.getInstance().isWasInBackground(paramBoolean);
    if (paramBoolean) {
      ForegroundDetector.getInstance().resetBackgroundVar();
    }
    if ((SharedConfig.passcodeHash.length() > 0) && (bool) && ((SharedConfig.appLocked) || ((SharedConfig.autoLockIn != 0) && (SharedConfig.lastPauseTime != 0) && (!SharedConfig.appLocked) && (SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime())) || (ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() + 5 < SharedConfig.lastPauseTime))) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public static String obtainLoginPhoneCall(String paramString)
  {
    if (!hasCallPermissions) {}
    for (paramString = null;; paramString = null) {
      for (;;)
      {
        return paramString;
        Object localObject1 = null;
        Object localObject2 = null;
        try
        {
          Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { "number", "date" }, "type IN (3,1,5)", null, "date DESC LIMIT 5");
          String str;
          boolean bool;
          do
          {
            long l;
            do
            {
              localObject2 = localCursor;
              localObject1 = localCursor;
              if (!localCursor.moveToNext()) {
                break;
              }
              localObject2 = localCursor;
              localObject1 = localCursor;
              str = localCursor.getString(0);
              localObject2 = localCursor;
              localObject1 = localCursor;
              l = localCursor.getLong(1);
              localObject2 = localCursor;
              localObject1 = localCursor;
              if (BuildVars.LOGS_ENABLED)
              {
                localObject2 = localCursor;
                localObject1 = localCursor;
                StringBuilder localStringBuilder = new java/lang/StringBuilder;
                localObject2 = localCursor;
                localObject1 = localCursor;
                localStringBuilder.<init>();
                localObject2 = localCursor;
                localObject1 = localCursor;
                FileLog.e("number = " + str);
              }
              localObject2 = localCursor;
              localObject1 = localCursor;
            } while (Math.abs(System.currentTimeMillis() - l) >= 3600000L);
            localObject2 = localCursor;
            localObject1 = localCursor;
            bool = checkPhonePattern(paramString, str);
          } while (!bool);
          paramString = str;
          if (localCursor != null)
          {
            localCursor.close();
            paramString = str;
            continue;
            if (localCursor != null) {
              localCursor.close();
            }
          }
        }
        catch (Exception paramString)
        {
          for (;;)
          {
            localObject1 = localObject2;
            FileLog.e(paramString);
            if (localObject2 != null) {
              ((Cursor)localObject2).close();
            }
          }
        }
        finally
        {
          if (localObject1 == null) {
            break label242;
          }
          ((Cursor)localObject1).close();
        }
      }
    }
  }
  
  public static void openForView(MessageObject paramMessageObject, Activity paramActivity)
    throws Exception
  {
    Object localObject1 = null;
    String str = paramMessageObject.getFileName();
    Object localObject2 = localObject1;
    if (paramMessageObject.messageOwner.attachPath != null)
    {
      localObject2 = localObject1;
      if (paramMessageObject.messageOwner.attachPath.length() != 0) {
        localObject2 = new File(paramMessageObject.messageOwner.attachPath);
      }
    }
    Object localObject3;
    if (localObject2 != null)
    {
      localObject3 = localObject2;
      if (((File)localObject2).exists()) {}
    }
    else
    {
      localObject3 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
    }
    Intent localIntent;
    if ((localObject3 != null) && (((File)localObject3).exists()))
    {
      localObject2 = null;
      localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setFlags(1);
      localObject1 = MimeTypeMap.getSingleton();
      int i = str.lastIndexOf('.');
      if (i != -1)
      {
        localObject1 = ((MimeTypeMap)localObject1).getMimeTypeFromExtension(str.substring(i + 1).toLowerCase());
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          if ((paramMessageObject.type == 9) || (paramMessageObject.type == 0)) {
            localObject1 = paramMessageObject.getDocument().mime_type;
          }
          if (localObject1 != null)
          {
            localObject2 = localObject1;
            if (((String)localObject1).length() != 0) {}
          }
          else
          {
            localObject2 = null;
          }
        }
      }
      if ((Build.VERSION.SDK_INT < 26) || (localObject2 == null) || (!((String)localObject2).equals("application/vnd.android.package-archive")) || (ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls())) {
        break label311;
      }
      paramMessageObject = new AlertDialog.Builder(paramActivity);
      paramMessageObject.setTitle(LocaleController.getString("AppName", NUM));
      paramMessageObject.setMessage(LocaleController.getString("ApkRestricted", NUM));
      paramMessageObject.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
      {
        @TargetApi(26)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          try
          {
            Activity localActivity = this.val$activity;
            Intent localIntent = new android/content/Intent;
            paramAnonymousDialogInterface = new java/lang/StringBuilder;
            paramAnonymousDialogInterface.<init>();
            localIntent.<init>("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + this.val$activity.getPackageName()));
            localActivity.startActivity(localIntent);
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousDialogInterface);
            }
          }
        }
      });
      paramMessageObject.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
      paramMessageObject.show();
    }
    for (;;)
    {
      return;
      label311:
      if (Build.VERSION.SDK_INT >= 24)
      {
        localObject1 = FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", (File)localObject3);
        if (localObject2 != null)
        {
          paramMessageObject = (MessageObject)localObject2;
          label337:
          localIntent.setDataAndType((Uri)localObject1, paramMessageObject);
          if (localObject2 == null) {
            break label457;
          }
          try
          {
            paramActivity.startActivityForResult(localIntent, 500);
          }
          catch (Exception paramMessageObject)
          {
            if (Build.VERSION.SDK_INT < 24) {
              break label440;
            }
          }
          localIntent.setDataAndType(FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", (File)localObject3), "text/plain");
        }
      }
      for (;;)
      {
        paramActivity.startActivityForResult(localIntent, 500);
        break;
        paramMessageObject = "text/plain";
        break label337;
        localObject1 = Uri.fromFile((File)localObject3);
        if (localObject2 != null) {}
        for (paramMessageObject = (MessageObject)localObject2;; paramMessageObject = "text/plain")
        {
          localIntent.setDataAndType((Uri)localObject1, paramMessageObject);
          break;
        }
        label440:
        localIntent.setDataAndType(Uri.fromFile((File)localObject3), "text/plain");
      }
      label457:
      paramActivity.startActivityForResult(localIntent, 500);
    }
  }
  
  public static void openForView(TLObject paramTLObject, Activity paramActivity)
    throws Exception
  {
    if ((paramTLObject == null) || (paramActivity == null)) {}
    for (;;)
    {
      return;
      String str = FileLoader.getAttachFileName(paramTLObject);
      File localFile = FileLoader.getPathToAttach(paramTLObject, true);
      if ((localFile != null) && (localFile.exists()))
      {
        Object localObject1 = null;
        Intent localIntent = new Intent("android.intent.action.VIEW");
        localIntent.setFlags(1);
        Object localObject2 = MimeTypeMap.getSingleton();
        int i = str.lastIndexOf('.');
        if (i != -1)
        {
          localObject2 = ((MimeTypeMap)localObject2).getMimeTypeFromExtension(str.substring(i + 1).toLowerCase());
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            if ((paramTLObject instanceof TLRPC.TL_document)) {
              localObject2 = ((TLRPC.TL_document)paramTLObject).mime_type;
            }
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {}
            }
            else
            {
              localObject1 = null;
            }
          }
        }
        if (Build.VERSION.SDK_INT >= 24)
        {
          localObject2 = FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", localFile);
          if (localObject1 != null)
          {
            paramTLObject = (TLObject)localObject1;
            label161:
            localIntent.setDataAndType((Uri)localObject2, paramTLObject);
            if (localObject1 == null) {
              break label281;
            }
            try
            {
              paramActivity.startActivityForResult(localIntent, 500);
            }
            catch (Exception paramTLObject)
            {
              if (Build.VERSION.SDK_INT < 24) {
                break label265;
              }
            }
            localIntent.setDataAndType(FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", localFile), "text/plain");
          }
        }
        for (;;)
        {
          paramActivity.startActivityForResult(localIntent, 500);
          break;
          paramTLObject = "text/plain";
          break label161;
          localObject2 = Uri.fromFile(localFile);
          if (localObject1 != null) {}
          for (paramTLObject = (TLObject)localObject1;; paramTLObject = "text/plain")
          {
            localIntent.setDataAndType((Uri)localObject2, paramTLObject);
            break;
          }
          label265:
          localIntent.setDataAndType(Uri.fromFile(localFile), "text/plain");
        }
        label281:
        paramActivity.startActivityForResult(localIntent, 500);
      }
    }
  }
  
  /* Error */
  private static void registerLoginContentObserver(boolean paramBoolean, final String paramString)
  {
    // Byte code:
    //   0: iload_0
    //   1: ifeq +74 -> 75
    //   4: getstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   7: ifnull +4 -> 11
    //   10: return
    //   11: getstatic 178	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   14: invokevirtual 826	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   17: astore_2
    //   18: getstatic 1319	android/provider/CallLog$Calls:CONTENT_URI	Landroid/net/Uri;
    //   21: astore_3
    //   22: new 8	org/telegram/messenger/AndroidUtilities$2
    //   25: dup
    //   26: new 337	android/os/Handler
    //   29: dup
    //   30: invokespecial 1446	android/os/Handler:<init>	()V
    //   33: aload_1
    //   34: invokespecial 1449	org/telegram/messenger/AndroidUtilities$2:<init>	(Landroid/os/Handler;Ljava/lang/String;)V
    //   37: astore 4
    //   39: aload 4
    //   41: putstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   44: aload_2
    //   45: aload_3
    //   46: iconst_1
    //   47: aload 4
    //   49: invokevirtual 1453	android/content/ContentResolver:registerContentObserver	(Landroid/net/Uri;ZLandroid/database/ContentObserver;)V
    //   52: new 10	org/telegram/messenger/AndroidUtilities$3
    //   55: dup
    //   56: aload_1
    //   57: invokespecial 1454	org/telegram/messenger/AndroidUtilities$3:<init>	(Ljava/lang/String;)V
    //   60: astore_1
    //   61: aload_1
    //   62: putstatic 205	org/telegram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   65: aload_1
    //   66: ldc2_w 1455
    //   69: invokestatic 1460	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;J)V
    //   72: goto -62 -> 10
    //   75: getstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   78: ifnull -68 -> 10
    //   81: getstatic 205	org/telegram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   84: ifnull +13 -> 97
    //   87: getstatic 205	org/telegram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   90: invokestatic 1462	org/telegram/messenger/AndroidUtilities:cancelRunOnUIThread	(Ljava/lang/Runnable;)V
    //   93: aconst_null
    //   94: putstatic 205	org/telegram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   97: getstatic 178	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: invokevirtual 826	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   103: getstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   106: invokevirtual 1466	android/content/ContentResolver:unregisterContentObserver	(Landroid/database/ContentObserver;)V
    //   109: aconst_null
    //   110: putstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   113: goto -103 -> 10
    //   116: astore_1
    //   117: aconst_null
    //   118: putstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   121: goto -111 -> 10
    //   124: astore_1
    //   125: aconst_null
    //   126: putstatic 1445	org/telegram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   129: aload_1
    //   130: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	paramBoolean	boolean
    //   0	131	1	paramString	String
    //   17	28	2	localContentResolver	ContentResolver
    //   21	25	3	localUri	Uri
    //   37	11	4	local2	2
    // Exception table:
    //   from	to	target	type
    //   97	109	116	java/lang/Exception
    //   97	109	124	finally
  }
  
  public static void removeAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet())) {}
    for (;;)
    {
      return;
      if (adjustOwnerClassGuid == paramInt) {
        paramActivity.getWindow().setSoftInputMode(32);
      }
    }
  }
  
  public static void removeLoginPhoneCall(String paramString, boolean paramBoolean)
  {
    if (!hasCallPermissions) {}
    for (;;)
    {
      return;
      Object localObject1 = null;
      Object localObject2 = null;
      try
      {
        Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { "_id", "number" }, "type IN (3,1,5)", null, "date DESC LIMIT 5");
        int i = 0;
        String str;
        do
        {
          j = i;
          localObject2 = localCursor;
          localObject1 = localCursor;
          if (!localCursor.moveToNext()) {
            break label160;
          }
          localObject2 = localCursor;
          localObject1 = localCursor;
          str = localCursor.getString(1);
          localObject2 = localCursor;
          localObject1 = localCursor;
          if (str.contains(paramString)) {
            break;
          }
          localObject2 = localCursor;
          localObject1 = localCursor;
        } while (!paramString.contains(str));
        int j = 1;
        localObject2 = localCursor;
        localObject1 = localCursor;
        ApplicationLoader.applicationContext.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "_id = ? ", new String[] { String.valueOf(localCursor.getInt(0)) });
        label160:
        if ((j == 0) && (paramBoolean))
        {
          localObject2 = localCursor;
          localObject1 = localCursor;
          registerLoginContentObserver(true, paramString);
        }
        if (localCursor == null) {
          continue;
        }
        localCursor.close();
      }
      catch (Exception paramString)
      {
        localObject1 = localObject2;
        FileLog.e(paramString);
        if (localObject2 == null) {
          continue;
        }
        ((Cursor)localObject2).close();
      }
      finally
      {
        if (localObject1 != null) {
          ((Cursor)localObject1).close();
        }
      }
    }
  }
  
  public static SpannableStringBuilder replaceTags(String paramString)
  {
    return replaceTags(paramString, 3);
  }
  
  public static SpannableStringBuilder replaceTags(String paramString, int paramInt)
  {
    int i;
    Object localObject;
    try
    {
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>(paramString);
      if ((paramInt & 0x1) == 0) {
        break label87;
      }
      for (;;)
      {
        i = localStringBuilder.indexOf("<br>");
        if (i == -1) {
          break;
        }
        localStringBuilder.replace(i, i + 4, "\n");
      }
      return (SpannableStringBuilder)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      localObject = new SpannableStringBuilder(paramString);
    }
    for (;;)
    {
      i = ((StringBuilder)localObject).indexOf("<br/>");
      if (i == -1) {
        break;
      }
      ((StringBuilder)localObject).replace(i, i + 5, "\n");
    }
    label87:
    ArrayList localArrayList = new java/util/ArrayList;
    localArrayList.<init>();
    if ((paramInt & 0x2) != 0)
    {
      for (;;)
      {
        int j = ((StringBuilder)localObject).indexOf("<b>");
        if (j == -1) {
          break;
        }
        ((StringBuilder)localObject).replace(j, j + 3, "");
        i = ((StringBuilder)localObject).indexOf("</b>");
        paramInt = i;
        if (i == -1) {
          paramInt = ((StringBuilder)localObject).indexOf("<b>");
        }
        ((StringBuilder)localObject).replace(paramInt, paramInt + 4, "");
        localArrayList.add(Integer.valueOf(j));
        localArrayList.add(Integer.valueOf(paramInt));
      }
      for (;;)
      {
        i = ((StringBuilder)localObject).indexOf("**");
        if (i == -1) {
          break;
        }
        ((StringBuilder)localObject).replace(i, i + 2, "");
        paramInt = ((StringBuilder)localObject).indexOf("**");
        if (paramInt >= 0)
        {
          ((StringBuilder)localObject).replace(paramInt, paramInt + 2, "");
          localArrayList.add(Integer.valueOf(i));
          localArrayList.add(Integer.valueOf(paramInt));
        }
      }
    }
    SpannableStringBuilder localSpannableStringBuilder = new android/text/SpannableStringBuilder;
    localSpannableStringBuilder.<init>((CharSequence)localObject);
    for (paramInt = 0;; paramInt++)
    {
      localObject = localSpannableStringBuilder;
      if (paramInt >= localArrayList.size() / 2) {
        break;
      }
      localObject = new org/telegram/ui/Components/TypefaceSpan;
      ((TypefaceSpan)localObject).<init>(getTypeface("fonts/rmedium.ttf"));
      localSpannableStringBuilder.setSpan(localObject, ((Integer)localArrayList.get(paramInt * 2)).intValue(), ((Integer)localArrayList.get(paramInt * 2 + 1)).intValue(), 33);
    }
  }
  
  public static void requestAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet())) {}
    for (;;)
    {
      return;
      paramActivity.getWindow().setSoftInputMode(16);
      adjustOwnerClassGuid = paramInt;
    }
  }
  
  private static double[] rgbToHsv(int paramInt1, int paramInt2, int paramInt3)
  {
    double d1 = paramInt1 / 255.0D;
    double d2 = paramInt2 / 255.0D;
    double d3 = paramInt3 / 255.0D;
    double d4;
    double d5;
    label57:
    double d6;
    if ((d1 > d2) && (d1 > d3))
    {
      d4 = d1;
      if ((d1 >= d2) || (d1 >= d3)) {
        break label126;
      }
      d5 = d1;
      d6 = d4 - d5;
      if (d4 != 0.0D) {
        break label148;
      }
    }
    label126:
    label148:
    for (double d7 = 0.0D;; d7 = d6 / d4)
    {
      if (d4 != d5) {
        break label158;
      }
      d5 = 0.0D;
      return new double[] { d5, d7, d4 };
      if (d2 > d3)
      {
        d4 = d2;
        break;
      }
      d4 = d3;
      break;
      if (d2 < d3)
      {
        d5 = d2;
        break label57;
      }
      d5 = d3;
      break label57;
    }
    label158:
    if ((d1 > d2) && (d1 > d3))
    {
      d5 = (d2 - d3) / d6;
      if (d2 < d3)
      {
        paramInt1 = 6;
        label193:
        d5 += paramInt1;
      }
    }
    for (;;)
    {
      d5 /= 6.0D;
      break;
      paramInt1 = 0;
      break label193;
      if (d2 > d3) {
        d5 = (d3 - d1) / d6 + 2.0D;
      } else {
        d5 = (d1 - d2) / d6 + 4.0D;
      }
    }
  }
  
  public static void runOnUIThread(Runnable paramRunnable)
  {
    runOnUIThread(paramRunnable, 0L);
  }
  
  public static void runOnUIThread(Runnable paramRunnable, long paramLong)
  {
    if (paramLong == 0L) {
      ApplicationLoader.applicationHandler.post(paramRunnable);
    }
    for (;;)
    {
      return;
      ApplicationLoader.applicationHandler.postDelayed(paramRunnable, paramLong);
    }
  }
  
  public static void setEnabled(View paramView, boolean paramBoolean)
  {
    if (paramView == null) {}
    for (;;)
    {
      return;
      paramView.setEnabled(paramBoolean);
      if ((paramView instanceof ViewGroup))
      {
        paramView = (ViewGroup)paramView;
        for (int i = 0; i < paramView.getChildCount(); i++) {
          setEnabled(paramView.getChildAt(i), paramBoolean);
        }
      }
    }
  }
  
  public static int setMyLayerVersion(int paramInt1, int paramInt2)
  {
    return 0xFFFF0000 & paramInt1 | paramInt2;
  }
  
  public static int setPeerLayerVersion(int paramInt1, int paramInt2)
  {
    return 0xFFFF & paramInt1 | paramInt2 << 16;
  }
  
  public static void setRectToRect(Matrix paramMatrix, RectF paramRectF1, RectF paramRectF2, int paramInt, Matrix.ScaleToFit paramScaleToFit)
  {
    float f1;
    float f2;
    float f3;
    float f4;
    if ((paramInt == 90) || (paramInt == 270))
    {
      f1 = paramRectF2.height() / paramRectF1.width();
      f2 = paramRectF2.width() / paramRectF1.height();
      f3 = f1;
      f4 = f2;
      if (paramScaleToFit != Matrix.ScaleToFit.FILL)
      {
        if (f1 <= f2) {
          break label168;
        }
        f3 = f2;
        f4 = f2;
      }
      label67:
      f1 = -paramRectF1.left;
      f2 = -paramRectF1.top;
      paramMatrix.setTranslate(paramRectF2.left, paramRectF2.top);
      if (paramInt != 90) {
        break label179;
      }
      paramMatrix.preRotate(90.0F);
      paramMatrix.preTranslate(0.0F, -paramRectF2.width());
    }
    for (;;)
    {
      paramMatrix.preScale(f3, f4);
      paramMatrix.preTranslate(f1 * f3, f2 * f4);
      return;
      f1 = paramRectF2.width() / paramRectF1.width();
      f2 = paramRectF2.height() / paramRectF1.height();
      break;
      label168:
      f4 = f1;
      f3 = f1;
      break label67;
      label179:
      if (paramInt == 180)
      {
        paramMatrix.preRotate(180.0F);
        paramMatrix.preTranslate(-paramRectF2.width(), -paramRectF2.height());
      }
      else if (paramInt == 270)
      {
        paramMatrix.preRotate(270.0F);
        paramMatrix.preTranslate(-paramRectF2.height(), 0.0F);
      }
    }
  }
  
  public static void setScrollViewEdgeEffectColor(ScrollView paramScrollView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    try
    {
      Object localObject = ScrollView.class.getDeclaredField("mEdgeGlowTop");
      ((Field)localObject).setAccessible(true);
      localObject = (EdgeEffect)((Field)localObject).get(paramScrollView);
      if (localObject != null) {
        ((EdgeEffect)localObject).setColor(paramInt);
      }
      localObject = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
      ((Field)localObject).setAccessible(true);
      paramScrollView = (EdgeEffect)((Field)localObject).get(paramScrollView);
      if (paramScrollView != null) {
        paramScrollView.setColor(paramInt);
      }
      return;
    }
    catch (Exception paramScrollView)
    {
      for (;;)
      {
        FileLog.e(paramScrollView);
      }
    }
  }
  
  public static void setViewPagerEdgeEffectColor(ViewPager paramViewPager, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    try
    {
      Object localObject = ViewPager.class.getDeclaredField("mLeftEdge");
      ((Field)localObject).setAccessible(true);
      localObject = (EdgeEffectCompat)((Field)localObject).get(paramViewPager);
      if (localObject != null)
      {
        Field localField = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
        localField.setAccessible(true);
        localObject = (EdgeEffect)localField.get(localObject);
        if (localObject != null) {
          ((EdgeEffect)localObject).setColor(paramInt);
        }
      }
      localObject = ViewPager.class.getDeclaredField("mRightEdge");
      ((Field)localObject).setAccessible(true);
      localObject = (EdgeEffectCompat)((Field)localObject).get(paramViewPager);
      if (localObject != null)
      {
        paramViewPager = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
        paramViewPager.setAccessible(true);
        paramViewPager = (EdgeEffect)paramViewPager.get(localObject);
        if (paramViewPager != null) {
          paramViewPager.setColor(paramInt);
        }
      }
      return;
    }
    catch (Exception paramViewPager)
    {
      for (;;)
      {
        FileLog.e(paramViewPager);
      }
    }
  }
  
  public static void setWaitingForCall(boolean paramBoolean)
  {
    synchronized (callLock)
    {
      waitingForCall = paramBoolean;
      return;
    }
  }
  
  public static void setWaitingForSms(boolean paramBoolean)
  {
    synchronized (smsLock)
    {
      waitingForSms = paramBoolean;
      return;
    }
  }
  
  public static void shakeView(View paramView, final float paramFloat, final int paramInt)
  {
    if (paramInt == 6) {
      paramView.setTranslationX(0.0F);
    }
    for (;;)
    {
      return;
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, "translationX", new float[] { dp(paramFloat) }) });
      localAnimatorSet.setDuration(50L);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = this.val$view;
          if (paramInt == 5) {}
          for (float f = 0.0F;; f = -paramFloat)
          {
            AndroidUtilities.shakeView(paramAnonymousAnimator, f, paramInt + 1);
            return;
          }
        }
      });
      localAnimatorSet.start();
    }
  }
  
  public static void showKeyboard(View paramView)
  {
    if (paramView == null) {}
    for (;;)
    {
      return;
      try
      {
        ((InputMethodManager)paramView.getContext().getSystemService("input_method")).showSoftInput(paramView, 1);
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }
  }
  
  public static void showProxyAlert(Activity paramActivity, String paramString1, final String paramString2, final String paramString3, final String paramString4)
  {
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("Proxy", NUM));
    StringBuilder localStringBuilder = new StringBuilder(LocaleController.getString("EnableProxyAlert", NUM));
    localStringBuilder.append("\n\n");
    localStringBuilder.append(LocaleController.getString("UseProxyAddress", NUM)).append(": ").append(paramString1).append("\n");
    localStringBuilder.append(LocaleController.getString("UseProxyPort", NUM)).append(": ").append(paramString2).append("\n");
    if (!TextUtils.isEmpty(paramString3)) {
      localStringBuilder.append(LocaleController.getString("UseProxyUsername", NUM)).append(": ").append(paramString3).append("\n");
    }
    if (!TextUtils.isEmpty(paramString4)) {
      localStringBuilder.append(LocaleController.getString("UseProxyPassword", NUM)).append(": ").append(paramString4).append("\n");
    }
    localStringBuilder.append("\n").append(LocaleController.getString("EnableProxyAlert2", NUM));
    paramActivity.setMessage(localStringBuilder.toString());
    paramActivity.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = MessagesController.getGlobalMainSettings().edit();
        paramAnonymousDialogInterface.putBoolean("proxy_enabled", true);
        paramAnonymousDialogInterface.putString("proxy_ip", this.val$address);
        int i = Utilities.parseInt(paramString2).intValue();
        paramAnonymousDialogInterface.putInt("proxy_port", i);
        if (TextUtils.isEmpty(paramString4))
        {
          paramAnonymousDialogInterface.remove("proxy_pass");
          if (!TextUtils.isEmpty(paramString3)) {
            break label144;
          }
          paramAnonymousDialogInterface.remove("proxy_user");
        }
        for (;;)
        {
          paramAnonymousDialogInterface.commit();
          for (paramAnonymousInt = 0; paramAnonymousInt < 3; paramAnonymousInt++) {
            ConnectionsManager.native_setProxySettings(paramAnonymousInt, this.val$address, i, paramString3, paramString4);
          }
          paramAnonymousDialogInterface.putString("proxy_pass", paramString4);
          break;
          label144:
          paramAnonymousDialogInterface.putString("proxy_user", paramString3);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
      }
    });
    paramActivity.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
    paramActivity.show().setCanceledOnTouchOutside(true);
  }
  
  public static void unlockOrientation(Activity paramActivity)
  {
    if (paramActivity == null) {}
    for (;;)
    {
      return;
      try
      {
        if (prevOrientation != -10)
        {
          paramActivity.setRequestedOrientation(prevOrientation);
          prevOrientation = -10;
        }
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
      }
    }
  }
  
  public static void unregisterUpdates()
  {
    if (BuildVars.DEBUG_VERSION) {
      UpdateManager.unregister();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/AndroidUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */