package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.TypefaceSpan;

public class AndroidUtilities
{
  public static final int FLAG_TAG_ALL = 7;
  public static final int FLAG_TAG_BOLD = 2;
  public static final int FLAG_TAG_BR = 1;
  public static final int FLAG_TAG_COLOR = 4;
  public static Pattern WEB_URL = null;
  private static int adjustOwnerClassGuid;
  private static RectF bitmapRect;
  private static final Object callLock;
  public static float density;
  public static DisplayMetrics displayMetrics;
  public static Point displaySize;
  public static boolean isInMultiwindow;
  private static Boolean isTablet;
  public static int leftBaseline;
  private static Field mAttachInfoField;
  private static Field mStableInsetsField;
  public static Integer photoSize;
  private static int prevOrientation;
  private static Paint roundPaint;
  private static final Object smsLock;
  public static int statusBarHeight;
  private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
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
    isTablet = null;
    adjustOwnerClassGuid = 0;
    try
    {
      Pattern localPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
      localPattern = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + localPattern + ")");
      WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + localPattern + ")" + "(?:\\:\\d{1,5})?)" + "(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~" + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?" + "(?:\\b|$)");
      if (isTablet())
      {
        i = 80;
        leftBaseline = i;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
        continue;
        int i = 72;
      }
    }
  }
  
  public static void addMediaToGallery(Uri paramUri)
  {
    if (paramUri == null) {
      return;
    }
    try
    {
      Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
      localIntent.setData(paramUri);
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception paramUri)
    {
      FileLog.e("tmessages", paramUri);
    }
  }
  
  public static void addMediaToGallery(String paramString)
  {
    if (paramString == null) {
      return;
    }
    addMediaToGallery(Uri.fromFile(new File(paramString)));
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
      FileLog.e("tmessages", paramCharSequence);
    }
  }
  
  public static AlertDialog.Builder buildTTLAlert(final Context paramContext, TLRPC.EncryptedChat paramEncryptedChat)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(LocaleController.getString("MessageLifetime", 2131165872));
    paramContext = new NumberPicker(paramContext);
    paramContext.setMinValue(0);
    paramContext.setMaxValue(20);
    if ((paramEncryptedChat.ttl > 0) && (paramEncryptedChat.ttl < 16)) {
      paramContext.setValue(paramEncryptedChat.ttl);
    }
    for (;;)
    {
      paramContext.setFormatter(new NumberPicker.Formatter()
      {
        public String format(int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", 2131166289);
          }
          if ((paramAnonymousInt >= 1) && (paramAnonymousInt < 16)) {
            return AndroidUtilities.formatTTLString(paramAnonymousInt);
          }
          if (paramAnonymousInt == 16) {
            return AndroidUtilities.formatTTLString(30);
          }
          if (paramAnonymousInt == 17) {
            return AndroidUtilities.formatTTLString(60);
          }
          if (paramAnonymousInt == 18) {
            return AndroidUtilities.formatTTLString(3600);
          }
          if (paramAnonymousInt == 19) {
            return AndroidUtilities.formatTTLString(86400);
          }
          if (paramAnonymousInt == 20) {
            return AndroidUtilities.formatTTLString(604800);
          }
          return "";
        }
      });
      localBuilder.setView(paramContext);
      localBuilder.setNegativeButton(LocaleController.getString("Done", 2131165590), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousInt = this.val$encryptedChat.ttl;
          int i = paramContext.getValue();
          if ((i >= 0) && (i < 16)) {
            this.val$encryptedChat.ttl = i;
          }
          for (;;)
          {
            if (paramAnonymousInt != this.val$encryptedChat.ttl)
            {
              SecretChatHelper.getInstance().sendTTLMessage(this.val$encryptedChat, null);
              MessagesStorage.getInstance().updateEncryptedChatTTL(this.val$encryptedChat);
            }
            return;
            if (i == 16) {
              this.val$encryptedChat.ttl = 30;
            } else if (i == 17) {
              this.val$encryptedChat.ttl = 60;
            } else if (i == 18) {
              this.val$encryptedChat.ttl = 3600;
            } else if (i == 19) {
              this.val$encryptedChat.ttl = 86400;
            } else if (i == 20) {
              this.val$encryptedChat.ttl = 604800;
            }
          }
        }
      });
      return localBuilder;
      if (paramEncryptedChat.ttl == 30) {
        paramContext.setValue(16);
      } else if (paramEncryptedChat.ttl == 60) {
        paramContext.setValue(17);
      } else if (paramEncryptedChat.ttl == 3600) {
        paramContext.setValue(18);
      } else if (paramEncryptedChat.ttl == 86400) {
        paramContext.setValue(19);
      } else if (paramEncryptedChat.ttl == 604800) {
        paramContext.setValue(20);
      } else if (paramEncryptedChat.ttl == 0) {
        paramContext.setValue(0);
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
    int k = -16777216;
    j = k;
    for (;;)
    {
      try
      {
        if (!(paramDrawable instanceof BitmapDrawable)) {
          continue;
        }
        j = k;
        paramDrawable = ((BitmapDrawable)paramDrawable).getBitmap();
        i = k;
        if (paramDrawable != null)
        {
          j = k;
          paramDrawable = Bitmaps.createScaledBitmap(paramDrawable, 1, 1, true);
          i = k;
          if (paramDrawable != null)
          {
            j = k;
            i = paramDrawable.getPixel(0, 0);
            j = i;
            paramDrawable.recycle();
          }
        }
      }
      catch (Exception paramDrawable)
      {
        FileLog.e("tmessages", paramDrawable);
        int i = j;
        continue;
      }
      paramDrawable = rgbToHsv(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
      paramDrawable[1] = Math.min(1.0D, paramDrawable[1] + 0.05D + 0.1D * (1.0D - paramDrawable[1]));
      paramDrawable[2] = Math.max(0.0D, paramDrawable[2] * 0.65D);
      paramDrawable = hsvToRgb(paramDrawable[0], paramDrawable[1], paramDrawable[2]);
      return new int[] { Color.argb(102, paramDrawable[0], paramDrawable[1], paramDrawable[2]), Color.argb(136, paramDrawable[0], paramDrawable[1], paramDrawable[2]) };
      i = k;
      j = k;
      if ((paramDrawable instanceof ColorDrawable))
      {
        j = k;
        i = ((ColorDrawable)paramDrawable).getColor();
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
    try
    {
      density = paramContext.getResources().getDisplayMetrics().density;
      Configuration localConfiguration = paramConfiguration;
      paramConfiguration = localConfiguration;
      if (localConfiguration == null) {
        paramConfiguration = paramContext.getResources().getConfiguration();
      }
      if ((paramConfiguration.keyboard != 1) && (paramConfiguration.hardKeyboardHidden == 1)) {}
      for (;;)
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
        FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        return;
        bool = false;
      }
      return;
    }
    catch (Exception paramContext)
    {
      FileLog.e("tmessages", paramContext);
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
  
  public static void clearCursorDrawable(EditText paramEditText)
  {
    if (paramEditText == null) {
      return;
    }
    try
    {
      Field localField = TextView.class.getDeclaredField("mCursorDrawableRes");
      localField.setAccessible(true);
      localField.setInt(paramEditText, 0);
      return;
    }
    catch (Exception paramEditText)
    {
      FileLog.e("tmessages", paramEditText);
    }
  }
  
  @SuppressLint({"NewApi"})
  public static void clearDrawableAnimation(View paramView)
  {
    if ((Build.VERSION.SDK_INT < 21) || (paramView == null)) {}
    do
    {
      do
      {
        return;
        if (!(paramView instanceof ListView)) {
          break;
        }
        paramView = ((ListView)paramView).getSelector();
      } while (paramView == null);
      paramView.setState(StateSet.NOTHING);
      return;
      paramView = paramView.getBackground();
    } while (paramView == null);
    paramView.setState(StateSet.NOTHING);
    paramView.jumpToCurrentState();
  }
  
  public static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return 0;
    }
    if (paramInt1 > paramInt2) {
      return 1;
    }
    return -1;
  }
  
  /* Error */
  public static boolean copyFile(File paramFile1, File paramFile2)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 536	java/io/File:exists	()Z
    //   4: ifne +8 -> 12
    //   7: aload_1
    //   8: invokevirtual 539	java/io/File:createNewFile	()Z
    //   11: pop
    //   12: aconst_null
    //   13: astore 4
    //   15: aconst_null
    //   16: astore 7
    //   18: aconst_null
    //   19: astore_3
    //   20: aconst_null
    //   21: astore 5
    //   23: aconst_null
    //   24: astore 6
    //   26: new 541	java/io/FileInputStream
    //   29: dup
    //   30: aload_0
    //   31: invokespecial 544	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   34: astore_0
    //   35: new 546	java/io/FileOutputStream
    //   38: dup
    //   39: aload_1
    //   40: invokespecial 547	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   43: astore_1
    //   44: aload_1
    //   45: invokevirtual 551	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   48: aload_0
    //   49: invokevirtual 552	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   52: lconst_0
    //   53: aload_0
    //   54: invokevirtual 552	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   57: invokevirtual 558	java/nio/channels/FileChannel:size	()J
    //   60: invokevirtual 562	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   63: pop2
    //   64: aload_0
    //   65: ifnull +7 -> 72
    //   68: aload_0
    //   69: invokevirtual 565	java/io/FileInputStream:close	()V
    //   72: aload_1
    //   73: ifnull +7 -> 80
    //   76: aload_1
    //   77: invokevirtual 566	java/io/FileOutputStream:close	()V
    //   80: iconst_1
    //   81: istore_2
    //   82: iload_2
    //   83: ireturn
    //   84: astore 5
    //   86: aload 7
    //   88: astore_0
    //   89: aload 6
    //   91: astore_1
    //   92: aload_1
    //   93: astore_3
    //   94: aload_0
    //   95: astore 4
    //   97: ldc -98
    //   99: aload 5
    //   101: invokestatic 164	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   104: iconst_0
    //   105: istore_2
    //   106: aload_0
    //   107: ifnull +7 -> 114
    //   110: aload_0
    //   111: invokevirtual 565	java/io/FileInputStream:close	()V
    //   114: aload_1
    //   115: ifnull -33 -> 82
    //   118: aload_1
    //   119: invokevirtual 566	java/io/FileOutputStream:close	()V
    //   122: iconst_0
    //   123: ireturn
    //   124: astore_0
    //   125: aload 4
    //   127: ifnull +8 -> 135
    //   130: aload 4
    //   132: invokevirtual 565	java/io/FileInputStream:close	()V
    //   135: aload_3
    //   136: ifnull +7 -> 143
    //   139: aload_3
    //   140: invokevirtual 566	java/io/FileOutputStream:close	()V
    //   143: aload_0
    //   144: athrow
    //   145: astore_1
    //   146: aload_0
    //   147: astore 4
    //   149: aload_1
    //   150: astore_0
    //   151: aload 5
    //   153: astore_3
    //   154: goto -29 -> 125
    //   157: astore_3
    //   158: aload_0
    //   159: astore 4
    //   161: aload_3
    //   162: astore_0
    //   163: aload_1
    //   164: astore_3
    //   165: goto -40 -> 125
    //   168: astore 5
    //   170: aload 6
    //   172: astore_1
    //   173: goto -81 -> 92
    //   176: astore 5
    //   178: goto -86 -> 92
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	paramFile1	File
    //   0	181	1	paramFile2	File
    //   81	25	2	bool	boolean
    //   19	135	3	localObject1	Object
    //   157	5	3	localObject2	Object
    //   164	1	3	localFile1	File
    //   13	147	4	localFile2	File
    //   21	1	5	localObject3	Object
    //   84	68	5	localException1	Exception
    //   168	1	5	localException2	Exception
    //   176	1	5	localException3	Exception
    //   24	147	6	localObject4	Object
    //   16	71	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   26	35	84	java/lang/Exception
    //   26	35	124	finally
    //   97	104	124	finally
    //   35	44	145	finally
    //   44	64	157	finally
    //   35	44	168	java/lang/Exception
    //   44	64	176	java/lang/Exception
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
  
  /* Error */
  private static Intent createShortcutIntent(long paramLong, boolean paramBoolean)
  {
    // Byte code:
    //   0: new 169	android/content/Intent
    //   3: dup
    //   4: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   7: ldc_w 592
    //   10: invokespecial 595	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   13: astore 12
    //   15: lload_0
    //   16: l2i
    //   17: istore 4
    //   19: lload_0
    //   20: bipush 32
    //   22: lshr
    //   23: l2i
    //   24: istore 5
    //   26: aconst_null
    //   27: astore 8
    //   29: aconst_null
    //   30: astore 10
    //   32: iload 4
    //   34: ifne +66 -> 100
    //   37: aload 12
    //   39: ldc_w 597
    //   42: iload 5
    //   44: invokevirtual 601	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   47: pop
    //   48: invokestatic 607	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   51: iload 5
    //   53: invokestatic 613	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   56: invokevirtual 617	org/telegram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   59: astore 7
    //   61: aload 7
    //   63: ifnonnull +9 -> 72
    //   66: aconst_null
    //   67: astore 7
    //   69: aload 7
    //   71: areturn
    //   72: invokestatic 607	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   75: aload 7
    //   77: getfield 620	org/telegram/tgnet/TLRPC$EncryptedChat:user_id	I
    //   80: invokestatic 613	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   83: invokevirtual 624	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   86: astore 8
    //   88: aload 8
    //   90: ifnonnull +78 -> 168
    //   93: aload 10
    //   95: ifnonnull +73 -> 168
    //   98: aconst_null
    //   99: areturn
    //   100: iload 4
    //   102: ifle +30 -> 132
    //   105: aload 12
    //   107: ldc_w 626
    //   110: iload 4
    //   112: invokevirtual 601	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   115: pop
    //   116: invokestatic 607	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   119: iload 4
    //   121: invokestatic 613	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   124: invokevirtual 624	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   127: astore 8
    //   129: goto -41 -> 88
    //   132: iload 4
    //   134: ifge +32 -> 166
    //   137: invokestatic 607	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   140: iload 4
    //   142: ineg
    //   143: invokestatic 613	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   146: invokevirtual 630	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   149: astore 10
    //   151: aload 12
    //   153: ldc_w 632
    //   156: iload 4
    //   158: ineg
    //   159: invokevirtual 601	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   162: pop
    //   163: goto -75 -> 88
    //   166: aconst_null
    //   167: areturn
    //   168: aconst_null
    //   169: astore 9
    //   171: aload 8
    //   173: ifnull +517 -> 690
    //   176: aload 8
    //   178: getfield 637	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   181: aload 8
    //   183: getfield 640	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   186: invokestatic 646	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   189: astore 11
    //   191: aload 11
    //   193: astore 7
    //   195: aload 8
    //   197: getfield 650	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   200: ifnull +17 -> 217
    //   203: aload 8
    //   205: getfield 650	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   208: getfield 656	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   211: astore 9
    //   213: aload 11
    //   215: astore 7
    //   217: aload 12
    //   219: new 111	java/lang/StringBuilder
    //   222: dup
    //   223: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   226: ldc_w 658
    //   229: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: lload_0
    //   233: invokevirtual 661	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   236: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   239: invokevirtual 665	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   242: pop
    //   243: aload 12
    //   245: ldc_w 666
    //   248: invokevirtual 670	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   251: pop
    //   252: new 169	android/content/Intent
    //   255: dup
    //   256: invokespecial 671	android/content/Intent:<init>	()V
    //   259: astore 11
    //   261: aload 11
    //   263: ldc_w 673
    //   266: aload 12
    //   268: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   271: pop
    //   272: aload 11
    //   274: ldc_w 678
    //   277: aload 7
    //   279: invokevirtual 681	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   282: pop
    //   283: aload 11
    //   285: ldc_w 683
    //   288: iconst_0
    //   289: invokevirtual 686	android/content/Intent:putExtra	(Ljava/lang/String;Z)Landroid/content/Intent;
    //   292: pop
    //   293: aload 11
    //   295: astore 7
    //   297: iload_2
    //   298: ifne -229 -> 69
    //   301: aconst_null
    //   302: astore 12
    //   304: aconst_null
    //   305: astore 7
    //   307: aload 9
    //   309: ifnull +362 -> 671
    //   312: aload 12
    //   314: astore 7
    //   316: aload 9
    //   318: iconst_1
    //   319: invokestatic 692	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;Z)Ljava/io/File;
    //   322: invokevirtual 693	java/io/File:toString	()Ljava/lang/String;
    //   325: invokestatic 699	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;)Landroid/graphics/Bitmap;
    //   328: astore 9
    //   330: aload 9
    //   332: astore 7
    //   334: aload 9
    //   336: ifnull +335 -> 671
    //   339: aload 9
    //   341: astore 7
    //   343: ldc_w 700
    //   346: invokestatic 704	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   349: istore 6
    //   351: aload 9
    //   353: astore 7
    //   355: iload 6
    //   357: iload 6
    //   359: getstatic 710	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   362: invokestatic 714	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   365: astore 12
    //   367: aload 9
    //   369: astore 7
    //   371: aload 12
    //   373: iconst_0
    //   374: invokevirtual 717	android/graphics/Bitmap:eraseColor	(I)V
    //   377: aload 9
    //   379: astore 7
    //   381: new 719	android/graphics/Canvas
    //   384: dup
    //   385: aload 12
    //   387: invokespecial 722	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   390: astore 13
    //   392: aload 9
    //   394: astore 7
    //   396: new 724	android/graphics/BitmapShader
    //   399: dup
    //   400: aload 9
    //   402: getstatic 730	android/graphics/Shader$TileMode:CLAMP	Landroid/graphics/Shader$TileMode;
    //   405: getstatic 730	android/graphics/Shader$TileMode:CLAMP	Landroid/graphics/Shader$TileMode;
    //   408: invokespecial 733	android/graphics/BitmapShader:<init>	(Landroid/graphics/Bitmap;Landroid/graphics/Shader$TileMode;Landroid/graphics/Shader$TileMode;)V
    //   411: astore 14
    //   413: aload 9
    //   415: astore 7
    //   417: getstatic 735	org/telegram/messenger/AndroidUtilities:roundPaint	Landroid/graphics/Paint;
    //   420: ifnonnull +32 -> 452
    //   423: aload 9
    //   425: astore 7
    //   427: new 737	android/graphics/Paint
    //   430: dup
    //   431: iconst_1
    //   432: invokespecial 739	android/graphics/Paint:<init>	(I)V
    //   435: putstatic 735	org/telegram/messenger/AndroidUtilities:roundPaint	Landroid/graphics/Paint;
    //   438: aload 9
    //   440: astore 7
    //   442: new 741	android/graphics/RectF
    //   445: dup
    //   446: invokespecial 742	android/graphics/RectF:<init>	()V
    //   449: putstatic 744	org/telegram/messenger/AndroidUtilities:bitmapRect	Landroid/graphics/RectF;
    //   452: aload 9
    //   454: astore 7
    //   456: iload 6
    //   458: i2f
    //   459: aload 9
    //   461: invokevirtual 747	android/graphics/Bitmap:getWidth	()I
    //   464: i2f
    //   465: fdiv
    //   466: fstore_3
    //   467: aload 9
    //   469: astore 7
    //   471: aload 13
    //   473: invokevirtual 750	android/graphics/Canvas:save	()I
    //   476: pop
    //   477: aload 9
    //   479: astore 7
    //   481: aload 13
    //   483: fload_3
    //   484: fload_3
    //   485: invokevirtual 754	android/graphics/Canvas:scale	(FF)V
    //   488: aload 9
    //   490: astore 7
    //   492: getstatic 735	org/telegram/messenger/AndroidUtilities:roundPaint	Landroid/graphics/Paint;
    //   495: aload 14
    //   497: invokevirtual 758	android/graphics/Paint:setShader	(Landroid/graphics/Shader;)Landroid/graphics/Shader;
    //   500: pop
    //   501: aload 9
    //   503: astore 7
    //   505: getstatic 744	org/telegram/messenger/AndroidUtilities:bitmapRect	Landroid/graphics/RectF;
    //   508: fconst_0
    //   509: fconst_0
    //   510: aload 9
    //   512: invokevirtual 747	android/graphics/Bitmap:getWidth	()I
    //   515: i2f
    //   516: aload 9
    //   518: invokevirtual 761	android/graphics/Bitmap:getHeight	()I
    //   521: i2f
    //   522: invokevirtual 765	android/graphics/RectF:set	(FFFF)V
    //   525: aload 9
    //   527: astore 7
    //   529: aload 13
    //   531: getstatic 744	org/telegram/messenger/AndroidUtilities:bitmapRect	Landroid/graphics/RectF;
    //   534: aload 9
    //   536: invokevirtual 747	android/graphics/Bitmap:getWidth	()I
    //   539: i2f
    //   540: aload 9
    //   542: invokevirtual 761	android/graphics/Bitmap:getHeight	()I
    //   545: i2f
    //   546: getstatic 735	org/telegram/messenger/AndroidUtilities:roundPaint	Landroid/graphics/Paint;
    //   549: invokevirtual 769	android/graphics/Canvas:drawRoundRect	(Landroid/graphics/RectF;FFLandroid/graphics/Paint;)V
    //   552: aload 9
    //   554: astore 7
    //   556: aload 13
    //   558: invokevirtual 772	android/graphics/Canvas:restore	()V
    //   561: aload 9
    //   563: astore 7
    //   565: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   568: invokevirtual 363	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   571: ldc_w 773
    //   574: invokevirtual 777	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   577: astore 14
    //   579: aload 9
    //   581: astore 7
    //   583: ldc_w 778
    //   586: invokestatic 704	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   589: istore 4
    //   591: aload 9
    //   593: astore 7
    //   595: iload 6
    //   597: iload 4
    //   599: isub
    //   600: fconst_2
    //   601: invokestatic 704	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   604: isub
    //   605: istore 5
    //   607: aload 9
    //   609: astore 7
    //   611: iload 6
    //   613: iload 4
    //   615: isub
    //   616: fconst_2
    //   617: invokestatic 704	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   620: isub
    //   621: istore 6
    //   623: aload 9
    //   625: astore 7
    //   627: aload 14
    //   629: iload 5
    //   631: iload 6
    //   633: iload 5
    //   635: iload 4
    //   637: iadd
    //   638: iload 6
    //   640: iload 4
    //   642: iadd
    //   643: invokevirtual 782	android/graphics/drawable/Drawable:setBounds	(IIII)V
    //   646: aload 9
    //   648: astore 7
    //   650: aload 14
    //   652: aload 13
    //   654: invokevirtual 786	android/graphics/drawable/Drawable:draw	(Landroid/graphics/Canvas;)V
    //   657: aload 9
    //   659: astore 7
    //   661: aload 13
    //   663: aconst_null
    //   664: invokevirtual 789	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
    //   667: aload 12
    //   669: astore 7
    //   671: aload 7
    //   673: ifnull +65 -> 738
    //   676: aload 11
    //   678: ldc_w 791
    //   681: aload 7
    //   683: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   686: pop
    //   687: aload 11
    //   689: areturn
    //   690: aload 10
    //   692: getfield 796	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   695: astore 11
    //   697: aload 11
    //   699: astore 7
    //   701: aload 10
    //   703: getfield 799	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   706: ifnull -489 -> 217
    //   709: aload 10
    //   711: getfield 799	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   714: getfield 802	org/telegram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   717: astore 9
    //   719: aload 11
    //   721: astore 7
    //   723: goto -506 -> 217
    //   726: astore 9
    //   728: ldc -98
    //   730: aload 9
    //   732: invokestatic 164	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   735: goto -64 -> 671
    //   738: aload 8
    //   740: ifnull +53 -> 793
    //   743: aload 8
    //   745: getfield 805	org/telegram/tgnet/TLRPC$User:bot	Z
    //   748: ifeq +24 -> 772
    //   751: aload 11
    //   753: ldc_w 807
    //   756: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   759: ldc_w 808
    //   762: invokestatic 814	android/content/Intent$ShortcutIconResource:fromContext	(Landroid/content/Context;I)Landroid/content/Intent$ShortcutIconResource;
    //   765: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   768: pop
    //   769: aload 11
    //   771: areturn
    //   772: aload 11
    //   774: ldc_w 807
    //   777: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   780: ldc_w 815
    //   783: invokestatic 814	android/content/Intent$ShortcutIconResource:fromContext	(Landroid/content/Context;I)Landroid/content/Intent$ShortcutIconResource;
    //   786: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   789: pop
    //   790: aload 11
    //   792: areturn
    //   793: aload 11
    //   795: astore 7
    //   797: aload 10
    //   799: ifnull -730 -> 69
    //   802: aload 10
    //   804: invokestatic 821	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   807: ifeq +32 -> 839
    //   810: aload 10
    //   812: getfield 824	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   815: ifne +24 -> 839
    //   818: aload 11
    //   820: ldc_w 807
    //   823: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   826: ldc_w 825
    //   829: invokestatic 814	android/content/Intent$ShortcutIconResource:fromContext	(Landroid/content/Context;I)Landroid/content/Intent$ShortcutIconResource;
    //   832: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   835: pop
    //   836: aload 11
    //   838: areturn
    //   839: aload 11
    //   841: ldc_w 807
    //   844: getstatic 152	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   847: ldc_w 826
    //   850: invokestatic 814	android/content/Intent$ShortcutIconResource:fromContext	(Landroid/content/Context;I)Landroid/content/Intent$ShortcutIconResource;
    //   853: invokevirtual 676	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   856: pop
    //   857: aload 11
    //   859: areturn
    //   860: astore 7
    //   862: goto -195 -> 667
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	865	0	paramLong	long
    //   0	865	2	paramBoolean	boolean
    //   466	19	3	f	float
    //   17	626	4	i	int
    //   24	614	5	j	int
    //   349	294	6	k	int
    //   59	737	7	localObject1	Object
    //   860	1	7	localException	Exception
    //   27	717	8	localUser	org.telegram.tgnet.TLRPC.User
    //   169	549	9	localObject2	Object
    //   726	5	9	localThrowable	Throwable
    //   30	781	10	localChat	org.telegram.tgnet.TLRPC.Chat
    //   189	669	11	localObject3	Object
    //   13	655	12	localObject4	Object
    //   390	272	13	localCanvas	android.graphics.Canvas
    //   411	240	14	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   316	330	726	java/lang/Throwable
    //   343	351	726	java/lang/Throwable
    //   355	367	726	java/lang/Throwable
    //   371	377	726	java/lang/Throwable
    //   381	392	726	java/lang/Throwable
    //   396	413	726	java/lang/Throwable
    //   417	423	726	java/lang/Throwable
    //   427	438	726	java/lang/Throwable
    //   442	452	726	java/lang/Throwable
    //   456	467	726	java/lang/Throwable
    //   471	477	726	java/lang/Throwable
    //   481	488	726	java/lang/Throwable
    //   492	501	726	java/lang/Throwable
    //   505	525	726	java/lang/Throwable
    //   529	552	726	java/lang/Throwable
    //   556	561	726	java/lang/Throwable
    //   565	579	726	java/lang/Throwable
    //   583	591	726	java/lang/Throwable
    //   595	607	726	java/lang/Throwable
    //   611	623	726	java/lang/Throwable
    //   627	646	726	java/lang/Throwable
    //   650	657	726	java/lang/Throwable
    //   661	667	726	java/lang/Throwable
    //   661	667	860	java/lang/Exception
  }
  
  public static byte[] decodeQuotedPrintable(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i];
      if (j == 61) {
        i += 1;
      }
      for (;;)
      {
        try
        {
          j = Character.digit((char)paramArrayOfByte[i], 16);
          i += 1;
          localByteArrayOutputStream.write((char)((j << 4) + Character.digit((char)paramArrayOfByte[i], 16)));
          i += 1;
        }
        catch (Exception paramArrayOfByte)
        {
          FileLog.e("tmessages", paramArrayOfByte);
          return null;
        }
        localByteArrayOutputStream.write(j);
      }
    }
    paramArrayOfByte = localByteArrayOutputStream.toByteArray();
    try
    {
      localByteArrayOutputStream.close();
      return paramArrayOfByte;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return paramArrayOfByte;
  }
  
  public static int dp(float paramFloat)
  {
    if (paramFloat == 0.0F) {
      return 0;
    }
    return (int)Math.ceil(density * paramFloat);
  }
  
  public static float dpf2(float paramFloat)
  {
    if (paramFloat == 0.0F) {
      return 0.0F;
    }
    return density * paramFloat;
  }
  
  public static String formatFileSize(long paramLong)
  {
    if (paramLong < 1024L) {
      return String.format("%d B", new Object[] { Long.valueOf(paramLong) });
    }
    if (paramLong < 1048576L) {
      return String.format("%.1f KB", new Object[] { Float.valueOf((float)paramLong / 1024.0F) });
    }
    if (paramLong < 1073741824L) {
      return String.format("%.1f MB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F) });
    }
    return String.format("%.1f GB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F / 1024.0F) });
  }
  
  public static String formatTTLString(int paramInt)
  {
    if (paramInt < 60) {
      return LocaleController.formatPluralString("Seconds", paramInt);
    }
    if (paramInt < 3600) {
      return LocaleController.formatPluralString("Minutes", paramInt / 60);
    }
    if (paramInt < 86400) {
      return LocaleController.formatPluralString("Hours", paramInt / 60 / 60);
    }
    if (paramInt < 604800) {
      return LocaleController.formatPluralString("Days", paramInt / 60 / 60 / 24);
    }
    int i = paramInt / 60 / 60 / 24;
    if (paramInt % 7 == 0) {
      return LocaleController.formatPluralString("Weeks", i / 7);
    }
    return String.format("%s %s", new Object[] { LocaleController.formatPluralString("Weeks", i / 7), LocaleController.formatPluralString("Days", i % 7) });
  }
  
  public static File generatePicturePath()
  {
    try
    {
      File localFile = getAlbumDir();
      String str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
      localFile = new File(localFile, "IMG_" + str + ".jpg");
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return null;
  }
  
  public static CharSequence generateSearchName(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      paramString1 = "";
    }
    SpannableStringBuilder localSpannableStringBuilder;
    int i;
    label72:
    label113:
    label134:
    label294:
    label300:
    label306:
    label331:
    do
    {
      do
      {
        return paramString1;
        localSpannableStringBuilder = new SpannableStringBuilder();
        String str = paramString1;
        int j;
        int k;
        if ((str == null) || (str.length() == 0))
        {
          paramString1 = paramString2;
          paramString2 = paramString1.trim();
          paramString1 = " " + paramString2.toLowerCase();
          i = 0;
          int m = paramString1.indexOf(" " + paramString3, i);
          if (m == -1) {
            break label331;
          }
          if (m != 0) {
            break label294;
          }
          j = 0;
          k = m - j;
          int n = paramString3.length();
          if (m != 0) {
            break label300;
          }
          j = 0;
          j = j + n + k;
          if ((i == 0) || (i == k + 1)) {
            break label306;
          }
          localSpannableStringBuilder.append(paramString2.substring(i, k));
        }
        for (;;)
        {
          str = paramString2.substring(k, j);
          if (str.startsWith(" ")) {
            localSpannableStringBuilder.append(" ");
          }
          str = str.trim();
          localSpannableStringBuilder.append(replaceTags("<c#ff4d83b3>" + str + "</c>"));
          i = j;
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
          j = 1;
          break label113;
          j = 1;
          break label134;
          if ((i == 0) && (k != 0)) {
            localSpannableStringBuilder.append(paramString2.substring(0, k));
          }
        }
        paramString1 = localSpannableStringBuilder;
      } while (i == -1);
      paramString1 = localSpannableStringBuilder;
    } while (i == paramString2.length());
    localSpannableStringBuilder.append(paramString2.substring(i, paramString2.length()));
    return localSpannableStringBuilder;
  }
  
  public static File generateVideoPath()
  {
    try
    {
      File localFile = getAlbumDir();
      String str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
      localFile = new File(localFile, "VID_" + str + ".mp4");
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return null;
  }
  
  private static File getAlbumDir()
  {
    Object localObject;
    if ((Build.VERSION.SDK_INT >= 23) && (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
      localObject = FileLoader.getInstance().getDirectory(4);
    }
    File localFile;
    do
    {
      do
      {
        return (File)localObject;
        if (!"mounted".equals(Environment.getExternalStorageState())) {
          break;
        }
        localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
        localObject = localFile;
      } while (localFile.mkdirs());
      localObject = localFile;
    } while (localFile.exists());
    FileLog.d("tmessages", "failed to create directory");
    return null;
    FileLog.d("tmessages", "External storage is not mounted READ/WRITE.");
    return null;
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
          FileLog.e("tmessages", localException1);
        }
        localException3 = localException3;
        FileLog.e("tmessages", localException3);
      }
    }
    if ((localObject1 == null) || (((String)localObject1).startsWith("mounted"))) {}
    try
    {
      File localFile;
      do
      {
        localFile = ApplicationLoader.applicationContext.getCacheDir();
        Object localObject2 = localFile;
      } while (localFile != null);
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException2);
      }
    }
    return new File("");
  }
  
  public static String getDataColumn(Context paramContext, Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    localObject = null;
    localContext = null;
    try
    {
      paramContext = paramContext.getContentResolver().query(paramUri, new String[] { "_data" }, paramString, paramArrayOfString, null);
      if (paramContext != null)
      {
        localContext = paramContext;
        localObject = paramContext;
        if (paramContext.moveToFirst())
        {
          localContext = paramContext;
          localObject = paramContext;
          paramString = paramContext.getString(paramContext.getColumnIndexOrThrow("_data"));
          localContext = paramContext;
          localObject = paramContext;
          if (!paramString.startsWith("content://"))
          {
            localContext = paramContext;
            localObject = paramContext;
            if (!paramString.startsWith("/"))
            {
              localContext = paramContext;
              localObject = paramContext;
              boolean bool = paramString.startsWith("file://");
              if (bool) {}
            }
          }
          else
          {
            if (paramContext != null) {
              paramContext.close();
            }
            paramUri = null;
          }
          do
          {
            return paramUri;
            paramUri = paramString;
          } while (paramContext == null);
          paramContext.close();
          return paramString;
        }
      }
      if (paramContext != null) {
        paramContext.close();
      }
    }
    catch (Exception paramContext)
    {
      for (;;)
      {
        localObject = localContext;
        FileLog.e("tmessages", paramContext);
        if (localContext != null) {
          localContext.close();
        }
      }
    }
    finally
    {
      if (localObject == null) {
        break label200;
      }
      ((Cursor)localObject).close();
    }
    return null;
  }
  
  public static int getMinTabletSide()
  {
    if (!isSmallTablet())
    {
      k = Math.min(displaySize.x, displaySize.y);
      j = k * 35 / 100;
      i = j;
      if (j < dp(320.0F)) {
        i = dp(320.0F);
      }
      return k - i;
    }
    int k = Math.min(displaySize.x, displaySize.y);
    int m = Math.max(displaySize.x, displaySize.y);
    int j = m * 35 / 100;
    int i = j;
    if (j < dp(320.0F)) {
      i = dp(320.0F);
    }
    return Math.min(k, m - i);
  }
  
  public static int getMyLayerVersion(int paramInt)
  {
    return 0xFFFF & paramInt;
  }
  
  @SuppressLint({"NewApi"})
  public static String getPath(Uri paramUri)
  {
    int j = 0;
    for (;;)
    {
      try
      {
        if (Build.VERSION.SDK_INT < 19) {
          break label332;
        }
        i = 1;
        Object localObject1;
        Object localObject2;
        if ((i != 0) && (DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, paramUri)))
        {
          if (isExternalStorageDocument(paramUri))
          {
            paramUri = DocumentsContract.getDocumentId(paramUri).split(":");
            if ("primary".equalsIgnoreCase(paramUri[0])) {
              return Environment.getExternalStorageDirectory() + "/" + paramUri[1];
            }
          }
          else
          {
            if (isDownloadsDocument(paramUri))
            {
              paramUri = DocumentsContract.getDocumentId(paramUri);
              paramUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(paramUri).longValue());
              return getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
            }
            if (isMediaDocument(paramUri))
            {
              localObject1 = DocumentsContract.getDocumentId(paramUri).split(":");
              localObject2 = localObject1[0];
              paramUri = null;
            }
          }
        }
        else {
          switch (((String)localObject2).hashCode())
          {
          case 100313435: 
            localObject1 = localObject1[1];
            return getDataColumn(ApplicationLoader.applicationContext, paramUri, "_id=?", new String[] { localObject1 });
            if (!((String)localObject2).equals("image")) {
              break;
            }
            i = j;
            break;
          case 112202875: 
            if (!((String)localObject2).equals("video")) {
              break;
            }
            i = 1;
            break;
          case 93166550: 
            if (!((String)localObject2).equals("audio")) {
              break;
            }
            i = 2;
            break label339;
            paramUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            continue;
            paramUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            continue;
            paramUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            continue;
            if ("content".equalsIgnoreCase(paramUri.getScheme())) {
              return getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
            }
            if ("file".equalsIgnoreCase(paramUri.getScheme()))
            {
              paramUri = paramUri.getPath();
              return paramUri;
            }
            break;
          }
        }
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
      }
      return null;
      label332:
      int i = 0;
      continue;
      i = -1;
      label339:
      switch (i)
      {
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
      if (Build.VERSION.SDK_INT < 16) {
        break label30;
      }
    }
    label30:
    for (photoSize = Integer.valueOf(1280);; photoSize = Integer.valueOf(800)) {
      return photoSize.intValue();
    }
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
    localPoint = new Point();
    try
    {
      WindowManager localWindowManager = (WindowManager)ApplicationLoader.applicationContext.getSystemService("window");
      if (Build.VERSION.SDK_INT >= 17)
      {
        localWindowManager.getDefaultDisplay().getRealSize(localPoint);
        return localPoint;
      }
      try
      {
        Method localMethod1 = Display.class.getMethod("getRawWidth", new Class[0]);
        Method localMethod2 = Display.class.getMethod("getRawHeight", new Class[0]);
        localPoint.set(((Integer)localMethod1.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer)localMethod2.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue());
        return localPoint;
      }
      catch (Exception localException2)
      {
        localPoint.set(localWindowManager.getDefaultDisplay().getWidth(), localWindowManager.getDefaultDisplay().getHeight());
        FileLog.e("tmessages", localException2);
        return localPoint;
      }
      return localPoint;
    }
    catch (Exception localException1)
    {
      FileLog.e("tmessages", localException1);
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
        return paramString;
      }
      catch (Exception localException)
      {
        FileLog.e("Typefaces", "Could not get typeface '" + paramString + "' because " + localException.getMessage());
        return null;
      }
    }
  }
  
  public static int getViewInset(View paramView)
  {
    if ((paramView == null) || (Build.VERSION.SDK_INT < 21) || (paramView.getHeight() == displaySize.y) || (paramView.getHeight() == displaySize.y - statusBarHeight)) {}
    for (;;)
    {
      return 0;
      try
      {
        if (mAttachInfoField == null)
        {
          mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
          mAttachInfoField.setAccessible(true);
        }
        paramView = mAttachInfoField.get(paramView);
        if (paramView != null)
        {
          if (mStableInsetsField == null)
          {
            mStableInsetsField = paramView.getClass().getDeclaredField("mStableInsets");
            mStableInsetsField.setAccessible(true);
          }
          int i = ((Rect)mStableInsetsField.get(paramView)).bottom;
          return i;
        }
      }
      catch (Exception paramView)
      {
        FileLog.e("tmessages", paramView);
      }
    }
    return 0;
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
        if (localInputMethodManager.isActive())
        {
          localInputMethodManager.hideSoftInputFromWindow(paramView.getWindowToken(), 0);
          return;
        }
      }
      catch (Exception paramView)
      {
        FileLog.e("tmessages", paramView);
      }
    }
  }
  
  private static int[] hsvToRgb(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d4 = 0.0D;
    double d3 = 0.0D;
    double d2 = 0.0D;
    double d5 = (int)Math.floor(6.0D * paramDouble1);
    double d6 = 6.0D * paramDouble1 - d5;
    paramDouble1 = paramDouble3 * (1.0D - paramDouble2);
    double d1 = paramDouble3 * (1.0D - d6 * paramDouble2);
    paramDouble2 = paramDouble3 * (1.0D - (1.0D - d6) * paramDouble2);
    switch ((int)d5 % 6)
    {
    default: 
      paramDouble3 = d4;
      paramDouble2 = d3;
      paramDouble1 = d2;
    }
    for (;;)
    {
      return new int[] { (int)(255.0D * paramDouble3), (int)(255.0D * paramDouble2), (int)(255.0D * paramDouble1) };
      continue;
      paramDouble2 = paramDouble3;
      paramDouble3 = d1;
      continue;
      d1 = paramDouble1;
      paramDouble1 = paramDouble2;
      paramDouble2 = paramDouble3;
      paramDouble3 = d1;
      continue;
      d2 = paramDouble1;
      paramDouble2 = d1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d2;
      continue;
      d1 = paramDouble2;
      paramDouble2 = paramDouble1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d1;
      continue;
      paramDouble2 = paramDouble1;
      paramDouble1 = d1;
    }
  }
  
  public static void installShortcut(long paramLong)
  {
    try
    {
      Intent localIntent = createShortcutIntent(paramLong, false);
      localIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
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
    try
    {
      ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
      return true;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (paramBaseFragment.getParentActivity() == null) {
        return false;
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      localBuilder.setMessage("Install Google Maps?");
      localBuilder.setCancelable(true);
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          try
          {
            paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps"));
            this.val$fragment.getParentActivity().startActivityForResult(paramAnonymousDialogInterface, 500);
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      paramBaseFragment.showDialog(localBuilder.create());
    }
    return false;
  }
  
  public static boolean isInternalUri(Uri paramUri)
  {
    Object localObject = paramUri.getPath();
    paramUri = (Uri)localObject;
    if (localObject == null) {}
    do
    {
      return false;
      do
      {
        paramUri = (Uri)localObject;
        localObject = Utilities.readlink(paramUri);
      } while ((localObject != null) && (!((String)localObject).equals(paramUri)));
      localObject = paramUri;
      if (paramUri != null) {}
      try
      {
        String str = new File(paramUri).getCanonicalPath();
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
    } while ((localObject == null) || (!((String)localObject).toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName() + "/files")));
    return true;
  }
  
  public static boolean isKeyboardShowed(View paramView)
  {
    if (paramView == null) {
      return false;
    }
    try
    {
      boolean bool = ((InputMethodManager)paramView.getContext().getSystemService("input_method")).isActive(paramView);
      return bool;
    }
    catch (Exception paramView)
    {
      FileLog.e("tmessages", paramView);
    }
    return false;
  }
  
  public static boolean isMediaDocument(Uri paramUri)
  {
    return "com.android.providers.media.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isSmallTablet()
  {
    return Math.min(displaySize.x, displaySize.y) / density <= 700.0F;
  }
  
  public static boolean isTablet()
  {
    if (isTablet == null) {
      isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(2131230720));
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
    int i;
    int j;
    for (;;)
    {
      return;
      try
      {
        prevOrientation = paramActivity.getRequestedOrientation();
        WindowManager localWindowManager = (WindowManager)paramActivity.getSystemService("window");
        if ((localWindowManager != null) && (localWindowManager.getDefaultDisplay() != null))
        {
          i = localWindowManager.getDefaultDisplay().getRotation();
          j = paramActivity.getResources().getConfiguration().orientation;
          if (i != 3) {
            break label96;
          }
          if (j == 1)
          {
            paramActivity.setRequestedOrientation(1);
            return;
          }
        }
      }
      catch (Exception paramActivity)
      {
        FileLog.e("tmessages", paramActivity);
        return;
      }
    }
    paramActivity.setRequestedOrientation(8);
    return;
    label96:
    if (i == 1)
    {
      if (j == 1)
      {
        paramActivity.setRequestedOrientation(9);
        return;
      }
      paramActivity.setRequestedOrientation(0);
      return;
    }
    if (i == 0)
    {
      if (j == 2)
      {
        paramActivity.setRequestedOrientation(0);
        return;
      }
      paramActivity.setRequestedOrientation(1);
      return;
    }
    if (j == 2)
    {
      paramActivity.setRequestedOrientation(8);
      return;
    }
    paramActivity.setRequestedOrientation(9);
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
    return (UserConfig.passcodeHash.length() > 0) && (bool) && ((UserConfig.appLocked) || ((UserConfig.autoLockIn != 0) && (UserConfig.lastPauseTime != 0) && (!UserConfig.appLocked) && (UserConfig.lastPauseTime + UserConfig.autoLockIn <= ConnectionsManager.getInstance().getCurrentTime())));
  }
  
  public static void openForView(MessageObject paramMessageObject, Activity paramActivity)
    throws Exception
  {
    Object localObject2 = null;
    String str = paramMessageObject.getFileName();
    Object localObject1 = localObject2;
    if (paramMessageObject.messageOwner.attachPath != null)
    {
      localObject1 = localObject2;
      if (paramMessageObject.messageOwner.attachPath.length() != 0) {
        localObject1 = new File(paramMessageObject.messageOwner.attachPath);
      }
    }
    Object localObject3;
    if (localObject1 != null)
    {
      localObject3 = localObject1;
      if (((File)localObject1).exists()) {}
    }
    else
    {
      localObject3 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
    }
    Intent localIntent;
    if ((localObject3 != null) && (((File)localObject3).exists()))
    {
      localObject1 = null;
      localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setFlags(1);
      localObject2 = MimeTypeMap.getSingleton();
      int i = str.lastIndexOf('.');
      if (i != -1)
      {
        localObject2 = ((MimeTypeMap)localObject2).getMimeTypeFromExtension(str.substring(i + 1).toLowerCase());
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          if ((paramMessageObject.type == 9) || (paramMessageObject.type == 0)) {
            localObject2 = paramMessageObject.getDocument().mime_type;
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
      if (Build.VERSION.SDK_INT < 24) {
        break label252;
      }
      localObject2 = FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", (File)localObject3);
      if (localObject1 == null) {
        break label245;
      }
    }
    for (paramMessageObject = (MessageObject)localObject1;; paramMessageObject = "text/plain")
    {
      localIntent.setDataAndType((Uri)localObject2, paramMessageObject);
      if (localObject1 == null) {
        break label338;
      }
      try
      {
        paramActivity.startActivityForResult(localIntent, 500);
        return;
      }
      catch (Exception paramMessageObject)
      {
        label245:
        if (Build.VERSION.SDK_INT < 24) {
          break;
        }
      }
    }
    label252:
    localObject2 = Uri.fromFile((File)localObject3);
    if (localObject1 != null) {}
    for (paramMessageObject = (MessageObject)localObject1;; paramMessageObject = "text/plain")
    {
      localIntent.setDataAndType((Uri)localObject2, paramMessageObject);
      break;
    }
    localIntent.setDataAndType(FileProvider.getUriForFile(paramActivity, "org.telegram.messenger.beta.provider", (File)localObject3), "text/plain");
    for (;;)
    {
      paramActivity.startActivityForResult(localIntent, 500);
      return;
      localIntent.setDataAndType(Uri.fromFile((File)localObject3), "text/plain");
    }
    label338:
    paramActivity.startActivityForResult(localIntent, 500);
  }
  
  public static void removeAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet())) {}
    while (adjustOwnerClassGuid != paramInt) {
      return;
    }
    paramActivity.getWindow().setSoftInputMode(32);
  }
  
  public static SpannableStringBuilder replaceTags(String paramString)
  {
    return replaceTags(paramString, 7);
  }
  
  public static SpannableStringBuilder replaceTags(String paramString, int paramInt)
  {
    int i;
    try
    {
      localObject2 = new StringBuilder(paramString);
      if ((paramInt & 0x1) == 0) {
        break label98;
      }
      for (;;)
      {
        i = ((StringBuilder)localObject2).indexOf("<br>");
        if (i == -1) {
          break;
        }
        ((StringBuilder)localObject2).replace(i, i + 4, "\n");
      }
      return (SpannableStringBuilder)localObject1;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      localObject1 = new SpannableStringBuilder(paramString);
    }
    for (;;)
    {
      i = ((StringBuilder)localObject2).indexOf("<br/>");
      if (i == -1) {
        break;
      }
      ((StringBuilder)localObject2).replace(i, i + 5, "\n");
    }
    label98:
    Object localObject1 = new ArrayList();
    int j;
    if ((paramInt & 0x2) != 0) {
      for (;;)
      {
        int k = ((StringBuilder)localObject2).indexOf("<b>");
        if (k == -1) {
          break;
        }
        ((StringBuilder)localObject2).replace(k, k + 3, "");
        j = ((StringBuilder)localObject2).indexOf("</b>");
        i = j;
        if (j == -1) {
          i = ((StringBuilder)localObject2).indexOf("<b>");
        }
        ((StringBuilder)localObject2).replace(i, i + 4, "");
        ((ArrayList)localObject1).add(Integer.valueOf(k));
        ((ArrayList)localObject1).add(Integer.valueOf(i));
      }
    }
    ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x4) != 0) {
      for (;;)
      {
        paramInt = ((StringBuilder)localObject2).indexOf("<c#");
        if (paramInt == -1) {
          break;
        }
        ((StringBuilder)localObject2).replace(paramInt, paramInt + 2, "");
        j = ((StringBuilder)localObject2).indexOf(">", paramInt);
        i = Color.parseColor(((StringBuilder)localObject2).substring(paramInt, j));
        ((StringBuilder)localObject2).replace(paramInt, j + 1, "");
        j = ((StringBuilder)localObject2).indexOf("</c>");
        ((StringBuilder)localObject2).replace(j, j + 4, "");
        localArrayList.add(Integer.valueOf(paramInt));
        localArrayList.add(Integer.valueOf(j));
        localArrayList.add(Integer.valueOf(i));
      }
    }
    Object localObject2 = new SpannableStringBuilder((CharSequence)localObject2);
    paramInt = 0;
    while (paramInt < ((ArrayList)localObject1).size() / 2)
    {
      ((SpannableStringBuilder)localObject2).setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer)((ArrayList)localObject1).get(paramInt * 2)).intValue(), ((Integer)((ArrayList)localObject1).get(paramInt * 2 + 1)).intValue(), 33);
      paramInt += 1;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (paramInt >= localArrayList.size() / 3) {
        break;
      }
      ((SpannableStringBuilder)localObject2).setSpan(new ForegroundColorSpan(((Integer)localArrayList.get(paramInt * 3 + 2)).intValue()), ((Integer)localArrayList.get(paramInt * 3)).intValue(), ((Integer)localArrayList.get(paramInt * 3 + 1)).intValue(), 33);
      paramInt += 1;
      continue;
      paramInt = 0;
    }
  }
  
  public static void requestAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet())) {
      return;
    }
    paramActivity.getWindow().setSoftInputMode(16);
    adjustOwnerClassGuid = paramInt;
  }
  
  private static double[] rgbToHsv(int paramInt1, int paramInt2, int paramInt3)
  {
    double d5 = paramInt1 / 255.0D;
    double d3 = paramInt2 / 255.0D;
    double d4 = paramInt3 / 255.0D;
    double d1;
    double d2;
    label63:
    double d7;
    if ((d5 > d3) && (d5 > d4))
    {
      d1 = d5;
      if ((d5 >= d3) || (d5 >= d4)) {
        break label126;
      }
      d2 = d5;
      d7 = d1 - d2;
      if (d1 != 0.0D) {
        break label148;
      }
    }
    label126:
    label148:
    for (double d6 = 0.0D;; d6 = d7 / d1)
    {
      if (d1 != d2) {
        break label157;
      }
      d2 = 0.0D;
      return new double[] { d2, d6, d1 };
      if (d3 > d4)
      {
        d1 = d3;
        break;
      }
      d1 = d4;
      break;
      if (d3 < d4)
      {
        d2 = d3;
        break label63;
      }
      d2 = d4;
      break label63;
    }
    label157:
    if ((d5 > d3) && (d5 > d4))
    {
      d2 = (d3 - d4) / d7;
      if (d3 < d4)
      {
        paramInt1 = 6;
        label194:
        d2 += paramInt1;
      }
    }
    for (;;)
    {
      d2 /= 6.0D;
      break;
      paramInt1 = 0;
      break label194;
      if (d3 > d4) {
        d2 = (d4 - d5) / d7 + 2.0D;
      } else {
        d2 = (d5 - d3) / d7 + 4.0D;
      }
    }
  }
  
  public static void runOnUIThread(Runnable paramRunnable)
  {
    runOnUIThread(paramRunnable, 0L);
  }
  
  public static void runOnUIThread(Runnable paramRunnable, long paramLong)
  {
    if (paramLong == 0L)
    {
      ApplicationLoader.applicationHandler.post(paramRunnable);
      return;
    }
    ApplicationLoader.applicationHandler.postDelayed(paramRunnable, paramLong);
  }
  
  public static void setListViewEdgeEffectColor(AbsListView paramAbsListView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    try
    {
      Object localObject = AbsListView.class.getDeclaredField("mEdgeGlowTop");
      ((Field)localObject).setAccessible(true);
      localObject = (EdgeEffect)((Field)localObject).get(paramAbsListView);
      if (localObject != null) {
        ((EdgeEffect)localObject).setColor(paramInt);
      }
      localObject = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
      ((Field)localObject).setAccessible(true);
      paramAbsListView = (EdgeEffect)((Field)localObject).get(paramAbsListView);
      if (paramAbsListView != null) {
        paramAbsListView.setColor(paramInt);
      }
      return;
    }
    catch (Exception paramAbsListView)
    {
      FileLog.e("tmessages", paramAbsListView);
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
  
  public static void setProgressBarAnimationDuration(ProgressBar paramProgressBar, int paramInt)
  {
    if (paramProgressBar == null) {
      return;
    }
    try
    {
      Field localField = ProgressBar.class.getDeclaredField("mDuration");
      localField.setAccessible(true);
      localField.setInt(paramProgressBar, paramInt);
      return;
    }
    catch (Exception paramProgressBar)
    {
      FileLog.e("tmessages", paramProgressBar);
    }
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
    if (paramInt == 6)
    {
      paramView.setTranslationX(0.0F);
      return;
    }
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, "translationX", new float[] { dp(paramFloat) }) });
    localAnimatorSet.setDuration(50L);
    localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
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
  
  public static void showKeyboard(View paramView)
  {
    if (paramView == null) {
      return;
    }
    try
    {
      ((InputMethodManager)paramView.getContext().getSystemService("input_method")).showSoftInput(paramView, 1);
      return;
    }
    catch (Exception paramView)
    {
      FileLog.e("tmessages", paramView);
    }
  }
  
  public static void uninstallShortcut(long paramLong)
  {
    try
    {
      Intent localIntent = createShortcutIntent(paramLong, true);
      localIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
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
          return;
        }
      }
      catch (Exception paramActivity)
      {
        FileLog.e("tmessages", paramActivity);
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