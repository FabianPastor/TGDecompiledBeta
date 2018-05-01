package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.security.SecureRandom;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class WallpaperUpdater
{
  private String currentPicturePath;
  private File currentWallpaperPath;
  private WallpaperUpdaterDelegate delegate;
  private Activity parentActivity;
  private File picturePath = null;
  
  public WallpaperUpdater(Activity paramActivity, WallpaperUpdaterDelegate paramWallpaperUpdaterDelegate)
  {
    this.parentActivity = paramActivity;
    this.delegate = paramWallpaperUpdaterDelegate;
    this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
  }
  
  public void cleanup()
  {
    this.currentWallpaperPath.delete();
  }
  
  public String getCurrentPicturePath()
  {
    return this.currentPicturePath;
  }
  
  public File getCurrentWallpaperPath()
  {
    return this.currentWallpaperPath;
  }
  
  /* Error */
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    // Byte code:
    //   0: iload_2
    //   1: iconst_m1
    //   2: if_icmpne +119 -> 121
    //   5: iload_1
    //   6: bipush 10
    //   8: if_icmpne +183 -> 191
    //   11: aload_0
    //   12: getfield 77	org/telegram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   15: invokestatic 97	org/telegram/messenger/AndroidUtilities:addMediaToGallery	(Ljava/lang/String;)V
    //   18: aconst_null
    //   19: astore 4
    //   21: aconst_null
    //   22: astore 5
    //   24: aload 4
    //   26: astore_3
    //   27: invokestatic 101	org/telegram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   30: astore 6
    //   32: aload 4
    //   34: astore_3
    //   35: aload_0
    //   36: getfield 77	org/telegram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   39: aconst_null
    //   40: aload 6
    //   42: getfield 107	android/graphics/Point:x	I
    //   45: i2f
    //   46: aload 6
    //   48: getfield 110	android/graphics/Point:y	I
    //   51: i2f
    //   52: iconst_1
    //   53: invokestatic 116	org/telegram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   56: astore 7
    //   58: aload 4
    //   60: astore_3
    //   61: new 118	java/io/FileOutputStream
    //   64: astore 6
    //   66: aload 4
    //   68: astore_3
    //   69: aload 6
    //   71: aload_0
    //   72: getfield 70	org/telegram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   75: invokespecial 121	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   78: aload 7
    //   80: getstatic 127	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   83: bipush 87
    //   85: aload 6
    //   87: invokevirtual 133	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   90: pop
    //   91: aload_0
    //   92: getfield 29	org/telegram/ui/Components/WallpaperUpdater:delegate	Lorg/telegram/ui/Components/WallpaperUpdater$WallpaperUpdaterDelegate;
    //   95: aload_0
    //   96: getfield 70	org/telegram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   99: aload 7
    //   101: invokeinterface 137 3 0
    //   106: aload 6
    //   108: ifnull +8 -> 116
    //   111: aload 6
    //   113: invokevirtual 140	java/io/FileOutputStream:close	()V
    //   116: aload_0
    //   117: aconst_null
    //   118: putfield 77	org/telegram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   121: return
    //   122: astore_3
    //   123: aload_3
    //   124: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   127: goto -11 -> 116
    //   130: astore 4
    //   132: aload 5
    //   134: astore 6
    //   136: aload 6
    //   138: astore_3
    //   139: aload 4
    //   141: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   144: aload 6
    //   146: ifnull -30 -> 116
    //   149: aload 6
    //   151: invokevirtual 140	java/io/FileOutputStream:close	()V
    //   154: goto -38 -> 116
    //   157: astore_3
    //   158: aload_3
    //   159: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   162: goto -46 -> 116
    //   165: astore 6
    //   167: aload_3
    //   168: astore 4
    //   170: aload 4
    //   172: ifnull +8 -> 180
    //   175: aload 4
    //   177: invokevirtual 140	java/io/FileOutputStream:close	()V
    //   180: aload 6
    //   182: athrow
    //   183: astore_3
    //   184: aload_3
    //   185: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   188: goto -8 -> 180
    //   191: iload_1
    //   192: bipush 11
    //   194: if_icmpne -73 -> 121
    //   197: aload_3
    //   198: ifnull -77 -> 121
    //   201: aload_3
    //   202: invokevirtual 152	android/content/Intent:getData	()Landroid/net/Uri;
    //   205: ifnull -84 -> 121
    //   208: invokestatic 101	org/telegram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   211: astore 6
    //   213: aconst_null
    //   214: aload_3
    //   215: invokevirtual 152	android/content/Intent:getData	()Landroid/net/Uri;
    //   218: aload 6
    //   220: getfield 107	android/graphics/Point:x	I
    //   223: i2f
    //   224: aload 6
    //   226: getfield 110	android/graphics/Point:y	I
    //   229: i2f
    //   230: iconst_1
    //   231: invokestatic 116	org/telegram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   234: astore 6
    //   236: new 118	java/io/FileOutputStream
    //   239: astore_3
    //   240: aload_3
    //   241: aload_0
    //   242: getfield 70	org/telegram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   245: invokespecial 121	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   248: aload 6
    //   250: getstatic 127	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   253: bipush 87
    //   255: aload_3
    //   256: invokevirtual 133	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   259: pop
    //   260: aload_0
    //   261: getfield 29	org/telegram/ui/Components/WallpaperUpdater:delegate	Lorg/telegram/ui/Components/WallpaperUpdater$WallpaperUpdaterDelegate;
    //   264: aload_0
    //   265: getfield 70	org/telegram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   268: aload 6
    //   270: invokeinterface 137 3 0
    //   275: goto -154 -> 121
    //   278: astore_3
    //   279: aload_3
    //   280: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   283: goto -162 -> 121
    //   286: astore_3
    //   287: aload 6
    //   289: astore 4
    //   291: aload_3
    //   292: astore 6
    //   294: goto -124 -> 170
    //   297: astore 4
    //   299: goto -163 -> 136
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	302	0	this	WallpaperUpdater
    //   0	302	1	paramInt1	int
    //   0	302	2	paramInt2	int
    //   0	302	3	paramIntent	Intent
    //   19	48	4	localObject1	Object
    //   130	10	4	localException1	Exception
    //   168	122	4	localObject2	Object
    //   297	1	4	localException2	Exception
    //   22	111	5	localObject3	Object
    //   30	120	6	localObject4	Object
    //   165	16	6	localObject5	Object
    //   211	82	6	localObject6	Object
    //   56	44	7	localBitmap	Bitmap
    // Exception table:
    //   from	to	target	type
    //   111	116	122	java/lang/Exception
    //   27	32	130	java/lang/Exception
    //   35	58	130	java/lang/Exception
    //   61	66	130	java/lang/Exception
    //   69	78	130	java/lang/Exception
    //   149	154	157	java/lang/Exception
    //   27	32	165	finally
    //   35	58	165	finally
    //   61	66	165	finally
    //   69	78	165	finally
    //   139	144	165	finally
    //   175	180	183	java/lang/Exception
    //   208	275	278	java/lang/Exception
    //   78	106	286	finally
    //   78	106	297	java/lang/Exception
  }
  
  public void setCurrentPicturePath(String paramString)
  {
    this.currentPicturePath = paramString;
  }
  
  public void showAlert(final boolean paramBoolean)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.parentActivity);
    CharSequence[] arrayOfCharSequence;
    if (paramBoolean)
    {
      arrayOfCharSequence = new CharSequence[5];
      arrayOfCharSequence[0] = LocaleController.getString("FromCamera", NUM);
      arrayOfCharSequence[1] = LocaleController.getString("FromGalley", NUM);
      arrayOfCharSequence[2] = LocaleController.getString("SelectColor", NUM);
      arrayOfCharSequence[3] = LocaleController.getString("Default", NUM);
      arrayOfCharSequence[4] = LocaleController.getString("Cancel", NUM);
    }
    for (;;)
    {
      localBuilder.setItems(arrayOfCharSequence, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {}
          for (;;)
          {
            try
            {
              paramAnonymousDialogInterface = new android/content/Intent;
              paramAnonymousDialogInterface.<init>("android.media.action.IMAGE_CAPTURE");
              localFile = AndroidUtilities.generatePicturePath();
              if (localFile != null)
              {
                if (Build.VERSION.SDK_INT >= 24)
                {
                  paramAnonymousDialogInterface.putExtra("output", FileProvider.getUriForFile(WallpaperUpdater.this.parentActivity, "org.telegram.messenger.beta.provider", localFile));
                  paramAnonymousDialogInterface.addFlags(2);
                  paramAnonymousDialogInterface.addFlags(1);
                  WallpaperUpdater.access$102(WallpaperUpdater.this, localFile.getAbsolutePath());
                }
              }
              else
              {
                WallpaperUpdater.this.parentActivity.startActivityForResult(paramAnonymousDialogInterface, 10);
                return;
              }
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              try
              {
                File localFile;
                FileLog.e(paramAnonymousDialogInterface);
              }
              catch (Exception paramAnonymousDialogInterface)
              {
                FileLog.e(paramAnonymousDialogInterface);
              }
              continue;
            }
            paramAnonymousDialogInterface.putExtra("output", Uri.fromFile(localFile));
            continue;
            if (paramAnonymousInt == 1)
            {
              paramAnonymousDialogInterface = new android/content/Intent;
              paramAnonymousDialogInterface.<init>("android.intent.action.PICK");
              paramAnonymousDialogInterface.setType("image/*");
              WallpaperUpdater.this.parentActivity.startActivityForResult(paramAnonymousDialogInterface, 11);
            }
            else if (paramBoolean)
            {
              if (paramAnonymousInt == 2) {
                WallpaperUpdater.this.delegate.needOpenColorPicker();
              } else if (paramAnonymousInt == 3) {
                WallpaperUpdater.this.delegate.didSelectWallpaper(null, null);
              }
            }
          }
        }
      });
      localBuilder.show();
      return;
      arrayOfCharSequence = new CharSequence[3];
      arrayOfCharSequence[0] = LocaleController.getString("FromCamera", NUM);
      arrayOfCharSequence[1] = LocaleController.getString("FromGalley", NUM);
      arrayOfCharSequence[2] = LocaleController.getString("Cancel", NUM);
    }
  }
  
  public static abstract interface WallpaperUpdaterDelegate
  {
    public abstract void didSelectWallpaper(File paramFile, Bitmap paramBitmap);
    
    public abstract void needOpenColorPicker();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/WallpaperUpdater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */