package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.media.ExifInterface;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.Components.AnimatedFileDrawable;

public class ImageLoader
{
  private static volatile ImageLoader Instance = null;
  private static byte[] bytes;
  private static byte[] bytesThumb;
  private static byte[] header = new byte[12];
  private static byte[] headerThumb = new byte[12];
  private HashMap<String, Integer> bitmapUseCounts = new HashMap();
  private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
  private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
  private int currentHttpFileLoadTasksCount = 0;
  private int currentHttpTasksCount = 0;
  private ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap();
  private HashMap<String, Integer> forceLoadingImages = new HashMap();
  private LinkedList<HttpFileTask> httpFileLoadTasks = new LinkedList();
  private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys = new HashMap();
  private LinkedList<HttpImageTask> httpTasks = new LinkedList();
  private String ignoreRemoval = null;
  private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
  private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap();
  private SparseArray<CacheImage> imageLoadingByTag = new SparseArray();
  private HashMap<String, CacheImage> imageLoadingByUrl = new HashMap();
  private volatile long lastCacheOutTime = 0L;
  private int lastImageNum = 0;
  private long lastProgressUpdateTime = 0L;
  private LruCache memCache;
  private HashMap<String, Runnable> retryHttpsTasks = new HashMap();
  private File telegramPath = null;
  private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap();
  private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
  private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap();
  private SparseArray<String> waitingForQualityThumbByTag = new SparseArray();
  
  public ImageLoader()
  {
    this.thumbGeneratingQueue.setPriority(1);
    this.memCache = new LruCache(Math.min(15, ((ActivityManager)ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * 1024 * 1024)
    {
      protected void entryRemoved(boolean paramAnonymousBoolean, String paramAnonymousString, BitmapDrawable paramAnonymousBitmapDrawable1, BitmapDrawable paramAnonymousBitmapDrawable2)
      {
        if ((ImageLoader.this.ignoreRemoval != null) && (paramAnonymousString != null) && (ImageLoader.this.ignoreRemoval.equals(paramAnonymousString))) {}
        for (;;)
        {
          return;
          paramAnonymousString = (Integer)ImageLoader.this.bitmapUseCounts.get(paramAnonymousString);
          if ((paramAnonymousString == null) || (paramAnonymousString.intValue() == 0))
          {
            paramAnonymousString = paramAnonymousBitmapDrawable1.getBitmap();
            if (!paramAnonymousString.isRecycled()) {
              paramAnonymousString.recycle();
            }
          }
        }
      }
      
      protected int sizeOf(String paramAnonymousString, BitmapDrawable paramAnonymousBitmapDrawable)
      {
        return paramAnonymousBitmapDrawable.getBitmap().getByteCount();
      }
    };
    Object localObject1 = new SparseArray();
    Object localObject2 = AndroidUtilities.getCacheDir();
    if (!((File)localObject2).isDirectory()) {}
    try
    {
      ((File)localObject2).mkdirs();
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          File localFile = new java/io/File;
          localFile.<init>((File)localObject2, ".nomedia");
          localFile.createNewFile();
          ((SparseArray)localObject1).put(4, localObject2);
          for (final int i = 0; i < 3; i++) {
            FileLoader.getInstance(i).setDelegate(new FileLoader.FileLoaderDelegate()
            {
              public void fileDidFailedLoad(final String paramAnonymousString, final int paramAnonymousInt)
              {
                ImageLoader.this.fileProgresses.remove(paramAnonymousString);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    ImageLoader.this.fileDidFailedLoad(paramAnonymousString, paramAnonymousInt);
                    NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { paramAnonymousString, Integer.valueOf(paramAnonymousInt) });
                  }
                });
              }
              
              public void fileDidFailedUpload(final String paramAnonymousString, final boolean paramAnonymousBoolean)
              {
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileDidFailUpload, new Object[] { ImageLoader.2.3.this.val$location, Boolean.valueOf(ImageLoader.2.3.this.val$isEncrypted) });
                      }
                    });
                    ImageLoader.this.fileProgresses.remove(paramAnonymousString);
                  }
                });
              }
              
              public void fileDidLoaded(final String paramAnonymousString, final File paramAnonymousFile, final int paramAnonymousInt)
              {
                ImageLoader.this.fileProgresses.remove(paramAnonymousString);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    if ((SharedConfig.saveToGallery) && (ImageLoader.this.telegramPath != null) && (paramAnonymousFile != null) && ((paramAnonymousString.endsWith(".mp4")) || (paramAnonymousString.endsWith(".jpg"))) && (paramAnonymousFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                      AndroidUtilities.addMediaToGallery(paramAnonymousFile.toString());
                    }
                    NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { paramAnonymousString });
                    ImageLoader.this.fileDidLoaded(paramAnonymousString, paramAnonymousFile, paramAnonymousInt);
                  }
                });
              }
              
              public void fileDidUploaded(final String paramAnonymousString, final TLRPC.InputFile paramAnonymousInputFile, final TLRPC.InputEncryptedFile paramAnonymousInputEncryptedFile, final byte[] paramAnonymousArrayOfByte1, final byte[] paramAnonymousArrayOfByte2, final long paramAnonymousLong)
              {
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileDidUpload, new Object[] { ImageLoader.2.2.this.val$location, ImageLoader.2.2.this.val$inputFile, ImageLoader.2.2.this.val$inputEncryptedFile, ImageLoader.2.2.this.val$key, ImageLoader.2.2.this.val$iv, Long.valueOf(ImageLoader.2.2.this.val$totalFileSize) });
                      }
                    });
                    ImageLoader.this.fileProgresses.remove(paramAnonymousString);
                  }
                });
              }
              
              public void fileLoadProgressChanged(final String paramAnonymousString, final float paramAnonymousFloat)
              {
                ImageLoader.this.fileProgresses.put(paramAnonymousString, Float.valueOf(paramAnonymousFloat));
                long l = System.currentTimeMillis();
                if ((ImageLoader.this.lastProgressUpdateTime == 0L) || (ImageLoader.this.lastProgressUpdateTime < l - 500L))
                {
                  ImageLoader.access$2802(ImageLoader.this, l);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { paramAnonymousString, Float.valueOf(paramAnonymousFloat) });
                    }
                  });
                }
              }
              
              public void fileUploadProgressChanged(final String paramAnonymousString, final float paramAnonymousFloat, final boolean paramAnonymousBoolean)
              {
                ImageLoader.this.fileProgresses.put(paramAnonymousString, Float.valueOf(paramAnonymousFloat));
                long l = System.currentTimeMillis();
                if ((ImageLoader.this.lastProgressUpdateTime == 0L) || (ImageLoader.this.lastProgressUpdateTime < l - 500L))
                {
                  ImageLoader.access$2802(ImageLoader.this, l);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(ImageLoader.2.this.val$currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, new Object[] { paramAnonymousString, Float.valueOf(paramAnonymousFloat), Boolean.valueOf(paramAnonymousBoolean) });
                    }
                  });
                }
              }
            });
          }
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
        FileLoader.setMediaDirs((SparseArray)localObject1);
        localObject2 = new BroadcastReceiver()
        {
          public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("file system changed");
            }
            paramAnonymousContext = new Runnable()
            {
              public void run()
              {
                ImageLoader.this.checkMediaPaths();
              }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramAnonymousIntent.getAction())) {
              AndroidUtilities.runOnUIThread(paramAnonymousContext, 1000L);
            }
            for (;;)
            {
              return;
              paramAnonymousContext.run();
            }
          }
        };
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_CHECKING");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_EJECT");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_MOUNTED");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_NOFS");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_REMOVED");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_SHARED");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_UNMOUNTED");
        ((IntentFilter)localObject1).addDataScheme("file");
      }
    }
    try
    {
      ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject2, (IntentFilter)localObject1);
      checkMediaPaths();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  private boolean canMoveFiles(File paramFile1, File paramFile2, int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 6
    //   9: aconst_null
    //   10: astore 7
    //   12: aconst_null
    //   13: astore 8
    //   15: iload_3
    //   16: ifne +169 -> 185
    //   19: aload 5
    //   21: astore 9
    //   23: new 279	java/io/File
    //   26: astore 8
    //   28: aload 5
    //   30: astore 9
    //   32: aload 8
    //   34: aload_1
    //   35: ldc_w 456
    //   38: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   41: aload 5
    //   43: astore 9
    //   45: aload 8
    //   47: astore_1
    //   48: new 279	java/io/File
    //   51: astore 7
    //   53: aload 5
    //   55: astore 9
    //   57: aload 8
    //   59: astore_1
    //   60: aload 7
    //   62: aload_2
    //   63: ldc_w 458
    //   66: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   69: aload 7
    //   71: astore_1
    //   72: aload 8
    //   74: astore 7
    //   76: aload_1
    //   77: astore 8
    //   79: aload 5
    //   81: astore 9
    //   83: sipush 1024
    //   86: newarray <illegal type>
    //   88: astore_2
    //   89: aload 5
    //   91: astore 9
    //   93: aload 7
    //   95: invokevirtual 294	java/io/File:createNewFile	()Z
    //   98: pop
    //   99: aload 5
    //   101: astore 9
    //   103: new 460	java/io/RandomAccessFile
    //   106: astore_1
    //   107: aload 5
    //   109: astore 9
    //   111: aload_1
    //   112: aload 7
    //   114: ldc_w 462
    //   117: invokespecial 463	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   120: aload_1
    //   121: aload_2
    //   122: invokevirtual 467	java/io/RandomAccessFile:write	([B)V
    //   125: aload_1
    //   126: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   129: aconst_null
    //   130: astore_1
    //   131: aload_1
    //   132: astore 9
    //   134: aload 7
    //   136: aload 8
    //   138: invokevirtual 474	java/io/File:renameTo	(Ljava/io/File;)Z
    //   141: istore 10
    //   143: aload_1
    //   144: astore 9
    //   146: aload 7
    //   148: invokevirtual 477	java/io/File:delete	()Z
    //   151: pop
    //   152: aload_1
    //   153: astore 9
    //   155: aload 8
    //   157: invokevirtual 477	java/io/File:delete	()Z
    //   160: pop
    //   161: iload 10
    //   163: ifeq +202 -> 365
    //   166: iload 4
    //   168: istore 10
    //   170: iconst_0
    //   171: ifeq +11 -> 182
    //   174: new 479	java/lang/NullPointerException
    //   177: dup
    //   178: invokespecial 480	java/lang/NullPointerException:<init>	()V
    //   181: athrow
    //   182: iload 10
    //   184: ireturn
    //   185: iload_3
    //   186: iconst_3
    //   187: if_icmpne +54 -> 241
    //   190: aload 5
    //   192: astore 9
    //   194: new 279	java/io/File
    //   197: astore 8
    //   199: aload 5
    //   201: astore 9
    //   203: aload 8
    //   205: aload_1
    //   206: ldc_w 482
    //   209: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   212: aload 5
    //   214: astore 9
    //   216: aload 8
    //   218: astore_1
    //   219: new 279	java/io/File
    //   222: dup
    //   223: aload_2
    //   224: ldc_w 484
    //   227: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   230: astore_1
    //   231: aload 8
    //   233: astore 7
    //   235: aload_1
    //   236: astore 8
    //   238: goto -159 -> 79
    //   241: iload_3
    //   242: iconst_1
    //   243: if_icmpne +54 -> 297
    //   246: aload 5
    //   248: astore 9
    //   250: new 279	java/io/File
    //   253: astore 8
    //   255: aload 5
    //   257: astore 9
    //   259: aload 8
    //   261: aload_1
    //   262: ldc_w 486
    //   265: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   268: aload 5
    //   270: astore 9
    //   272: aload 8
    //   274: astore_1
    //   275: new 279	java/io/File
    //   278: dup
    //   279: aload_2
    //   280: ldc_w 488
    //   283: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   286: astore_1
    //   287: aload 8
    //   289: astore 7
    //   291: aload_1
    //   292: astore 8
    //   294: goto -215 -> 79
    //   297: iload_3
    //   298: iconst_2
    //   299: if_icmpne -220 -> 79
    //   302: aload 5
    //   304: astore 9
    //   306: new 279	java/io/File
    //   309: astore 8
    //   311: aload 5
    //   313: astore 9
    //   315: aload 8
    //   317: aload_1
    //   318: ldc_w 490
    //   321: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   324: aload 5
    //   326: astore 9
    //   328: aload 8
    //   330: astore_1
    //   331: new 279	java/io/File
    //   334: dup
    //   335: aload_2
    //   336: ldc_w 492
    //   339: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   342: astore_1
    //   343: aload 8
    //   345: astore 7
    //   347: aload_1
    //   348: astore 8
    //   350: goto -271 -> 79
    //   353: astore_1
    //   354: aload_1
    //   355: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   358: iload 4
    //   360: istore 10
    //   362: goto -180 -> 182
    //   365: iconst_0
    //   366: ifeq +11 -> 377
    //   369: new 479	java/lang/NullPointerException
    //   372: dup
    //   373: invokespecial 480	java/lang/NullPointerException:<init>	()V
    //   376: athrow
    //   377: iconst_0
    //   378: istore 10
    //   380: goto -198 -> 182
    //   383: astore_1
    //   384: aload_1
    //   385: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   388: goto -11 -> 377
    //   391: aconst_null
    //   392: astore_1
    //   393: astore_2
    //   394: aload_1
    //   395: astore 9
    //   397: aload_2
    //   398: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   401: aload_1
    //   402: ifnull -25 -> 377
    //   405: aload_1
    //   406: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   409: goto -32 -> 377
    //   412: astore_1
    //   413: aload_1
    //   414: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   417: goto -40 -> 377
    //   420: astore_1
    //   421: aload 9
    //   423: ifnull +8 -> 431
    //   426: aload 9
    //   428: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   431: aload_1
    //   432: athrow
    //   433: astore_2
    //   434: aload_2
    //   435: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   438: goto -7 -> 431
    //   441: astore_2
    //   442: aload_1
    //   443: astore 9
    //   445: aload_2
    //   446: astore_1
    //   447: goto -26 -> 421
    //   450: astore_2
    //   451: aload 6
    //   453: astore_1
    //   454: goto -60 -> 394
    //   457: astore_2
    //   458: goto -64 -> 394
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	461	0	this	ImageLoader
    //   0	461	1	paramFile1	File
    //   0	461	2	paramFile2	File
    //   0	461	3	paramInt	int
    //   1	358	4	bool1	boolean
    //   4	321	5	localObject1	Object
    //   7	445	6	localObject2	Object
    //   10	336	7	localObject3	Object
    //   13	336	8	localFile	File
    //   21	423	9	localObject4	Object
    //   141	238	10	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   174	182	353	java/lang/Exception
    //   369	377	383	java/lang/Exception
    //   23	28	391	java/lang/Exception
    //   32	41	391	java/lang/Exception
    //   83	89	391	java/lang/Exception
    //   93	99	391	java/lang/Exception
    //   103	107	391	java/lang/Exception
    //   111	120	391	java/lang/Exception
    //   134	143	391	java/lang/Exception
    //   146	152	391	java/lang/Exception
    //   155	161	391	java/lang/Exception
    //   194	199	391	java/lang/Exception
    //   203	212	391	java/lang/Exception
    //   250	255	391	java/lang/Exception
    //   259	268	391	java/lang/Exception
    //   306	311	391	java/lang/Exception
    //   315	324	391	java/lang/Exception
    //   405	409	412	java/lang/Exception
    //   23	28	420	finally
    //   32	41	420	finally
    //   48	53	420	finally
    //   60	69	420	finally
    //   83	89	420	finally
    //   93	99	420	finally
    //   103	107	420	finally
    //   111	120	420	finally
    //   134	143	420	finally
    //   146	152	420	finally
    //   155	161	420	finally
    //   194	199	420	finally
    //   203	212	420	finally
    //   219	231	420	finally
    //   250	255	420	finally
    //   259	268	420	finally
    //   275	287	420	finally
    //   306	311	420	finally
    //   315	324	420	finally
    //   331	343	420	finally
    //   397	401	420	finally
    //   426	431	433	java/lang/Exception
    //   120	129	441	finally
    //   48	53	450	java/lang/Exception
    //   60	69	450	java/lang/Exception
    //   219	231	450	java/lang/Exception
    //   275	287	450	java/lang/Exception
    //   331	343	450	java/lang/Exception
    //   120	129	457	java/lang/Exception
  }
  
  private void createLoadOperationForImageReceiver(final ImageReceiver paramImageReceiver, final String paramString1, final String paramString2, final String paramString3, final TLObject paramTLObject, final String paramString4, final String paramString5, final int paramInt1, final int paramInt2, final int paramInt3)
  {
    if ((paramImageReceiver == null) || (paramString2 == null) || (paramString1 == null)) {
      return;
    }
    label21:
    final int i;
    final int j;
    if (paramInt3 != 0)
    {
      bool1 = true;
      i = paramImageReceiver.getTag(bool1);
      j = i;
      if (i == 0)
      {
        i = this.lastImageNum;
        if (paramInt3 == 0) {
          break label168;
        }
      }
    }
    label168:
    for (final boolean bool1 = true;; bool1 = false)
    {
      paramImageReceiver.setTag(i, bool1);
      this.lastImageNum += 1;
      j = i;
      if (this.lastImageNum == Integer.MAX_VALUE)
      {
        this.lastImageNum = 0;
        j = i;
      }
      final boolean bool2 = paramImageReceiver.isNeedsQualityThumb();
      final MessageObject localMessageObject = paramImageReceiver.getParentMessageObject();
      bool1 = paramImageReceiver.isShouldGenerateQualityThumb();
      i = paramImageReceiver.getcurrentAccount();
      this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          int j = 0;
          Object localObject1;
          Object localObject2;
          Object localObject3;
          Object localObject4;
          Object localObject5;
          boolean bool;
          if (paramInt3 != 2)
          {
            localObject1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString2);
            localObject2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(paramString1);
            localObject3 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(j);
            i = j;
            if (localObject3 != null)
            {
              if (localObject3 != localObject2) {
                break label707;
              }
              i = 1;
            }
            j = i;
            if (i == 0)
            {
              j = i;
              if (localObject2 != null)
              {
                localObject4 = paramImageReceiver;
                localObject3 = paramString1;
                localObject5 = paramString5;
                if (paramInt3 == 0) {
                  break label784;
                }
                bool = true;
                label125:
                ((ImageLoader.CacheImage)localObject2).addImageReceiver((ImageReceiver)localObject4, (String)localObject3, (String)localObject5, bool);
                j = 1;
              }
            }
            i = j;
            if (j == 0)
            {
              i = j;
              if (localObject1 != null)
              {
                localObject3 = paramImageReceiver;
                localObject2 = paramString1;
                localObject5 = paramString5;
                if (paramInt3 == 0) {
                  break label790;
                }
                bool = true;
                label180:
                ((ImageLoader.CacheImage)localObject1).addImageReceiver((ImageReceiver)localObject3, (String)localObject2, (String)localObject5, bool);
                i = 1;
              }
            }
          }
          int k;
          int m;
          int n;
          if (i == 0)
          {
            k = 0;
            localObject5 = null;
            localObject1 = null;
            m = 0;
            j = 0;
            if (paramString4 == null) {
              break label886;
            }
            localObject2 = localObject1;
            i = j;
            n = k;
            if (!paramString4.startsWith("http"))
            {
              k = 1;
              if (!paramString4.startsWith("thumb://")) {
                break label796;
              }
              m = paramString4.indexOf(":", 8);
              localObject2 = localObject1;
              i = j;
              n = k;
              if (m >= 0)
              {
                localObject2 = new File(paramString4.substring(m + 1));
                n = k;
                i = j;
              }
            }
            label307:
            if (paramInt3 != 2)
            {
              if ((!(paramTLObject instanceof TLRPC.TL_documentEncrypted)) && (!(paramTLObject instanceof TLRPC.TL_fileEncryptedLocation))) {
                break label1269;
              }
              j = 1;
              label337:
              localObject5 = new ImageLoader.CacheImage(ImageLoader.this, null);
              if ((paramString4 == null) || (paramString4.startsWith("vthumb")) || (paramString4.startsWith("thumb"))) {
                break label1274;
              }
              localObject1 = ImageLoader.getHttpUrlExtension(paramString4, "jpg");
              if ((((String)localObject1).equals("mp4")) || (((String)localObject1).equals("gif"))) {
                ((ImageLoader.CacheImage)localObject5).animatedFile = true;
              }
              label416:
              localObject1 = localObject2;
              k = i;
              if (localObject2 == null)
              {
                if ((paramInt2 == 0) && (paramInt1 > 0) && (paramString4 == null) && (j == 0)) {
                  break label1394;
                }
                localObject1 = new File(FileLoader.getDirectory(4), paramString2);
                if (!((File)localObject1).exists()) {
                  break label1342;
                }
                k = 1;
              }
              label478:
              if (paramInt3 == 0) {
                break label1515;
              }
              bool = true;
              label488:
              ((ImageLoader.CacheImage)localObject5).selfThumb = bool;
              ((ImageLoader.CacheImage)localObject5).key = paramString1;
              ((ImageLoader.CacheImage)localObject5).filter = paramString5;
              ((ImageLoader.CacheImage)localObject5).httpUrl = paramString4;
              ((ImageLoader.CacheImage)localObject5).ext = paramString3;
              ((ImageLoader.CacheImage)localObject5).currentAccount = i;
              if (paramInt2 == 2) {
                ((ImageLoader.CacheImage)localObject5).encryptionKeyPath = new File(FileLoader.getInternalCacheDir(), paramString2 + ".enc.key");
              }
              localObject4 = paramImageReceiver;
              localObject3 = paramString1;
              localObject2 = paramString5;
              if (paramInt3 == 0) {
                break label1521;
              }
              bool = true;
              label613:
              ((ImageLoader.CacheImage)localObject5).addImageReceiver((ImageReceiver)localObject4, (String)localObject3, (String)localObject2, bool);
              if ((n == 0) && (k == 0) && (!((File)localObject1).exists())) {
                break label1545;
              }
              ((ImageLoader.CacheImage)localObject5).finalFilePath = ((File)localObject1);
              ((ImageLoader.CacheImage)localObject5).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject5);
              ImageLoader.this.imageLoadingByKeys.put(paramString1, localObject5);
              if (paramInt3 == 0) {
                break label1527;
              }
              ImageLoader.this.cacheThumbOutQueue.postRunnable(((ImageLoader.CacheImage)localObject5).cacheTask);
            }
          }
          for (;;)
          {
            return;
            label707:
            if (localObject3 == localObject1)
            {
              String str;
              if (localObject2 == null)
              {
                localObject5 = paramImageReceiver;
                str = paramString1;
                localObject4 = paramString5;
                if (paramInt3 == 0) {
                  break label764;
                }
              }
              label764:
              for (bool = true;; bool = false)
              {
                ((ImageLoader.CacheImage)localObject3).replaceImageReceiver((ImageReceiver)localObject5, str, (String)localObject4, bool);
                i = 1;
                break;
              }
            }
            ((ImageLoader.CacheImage)localObject3).removeImageReceiver(paramImageReceiver);
            i = j;
            break;
            label784:
            bool = false;
            break label125;
            label790:
            bool = false;
            break label180;
            label796:
            if (paramString4.startsWith("vthumb://"))
            {
              m = paramString4.indexOf(":", 9);
              localObject2 = localObject1;
              i = j;
              n = k;
              if (m < 0) {
                break label307;
              }
              localObject2 = new File(paramString4.substring(m + 1));
              i = j;
              n = k;
              break label307;
            }
            localObject2 = new File(paramString4);
            i = j;
            n = k;
            break label307;
            label886:
            localObject2 = localObject1;
            i = j;
            n = k;
            if (paramInt3 == 0) {
              break label307;
            }
            localObject1 = localObject5;
            j = m;
            if (bool2)
            {
              localObject1 = new File(FileLoader.getDirectory(4), "q_" + paramString2);
              if (((File)localObject1).exists()) {
                break label1264;
              }
              localObject1 = null;
            }
            label1264:
            for (j = m;; j = 1)
            {
              localObject2 = localObject1;
              i = j;
              n = k;
              if (localMessageObject == null) {
                break;
              }
              localObject5 = null;
              localObject2 = localObject5;
              if (localMessageObject.messageOwner.attachPath != null)
              {
                localObject2 = localObject5;
                if (localMessageObject.messageOwner.attachPath.length() > 0)
                {
                  localObject5 = new File(localMessageObject.messageOwner.attachPath);
                  localObject2 = localObject5;
                  if (!((File)localObject5).exists()) {
                    localObject2 = null;
                  }
                }
              }
              localObject5 = localObject2;
              if (localObject2 == null) {
                localObject5 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
              }
              if ((bool2) && (localObject1 == null))
              {
                localObject4 = localMessageObject.getFileName();
                localObject3 = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(localObject4);
                localObject2 = localObject3;
                if (localObject3 == null)
                {
                  localObject2 = new ImageLoader.ThumbGenerateInfo(ImageLoader.this, null);
                  ImageLoader.ThumbGenerateInfo.access$3602((ImageLoader.ThumbGenerateInfo)localObject2, (TLRPC.FileLocation)paramTLObject);
                  ImageLoader.ThumbGenerateInfo.access$3702((ImageLoader.ThumbGenerateInfo)localObject2, paramString5);
                  ImageLoader.this.waitingForQualityThumb.put(localObject4, localObject2);
                }
                ImageLoader.ThumbGenerateInfo.access$3108((ImageLoader.ThumbGenerateInfo)localObject2);
                ImageLoader.this.waitingForQualityThumbByTag.put(j, localObject4);
              }
              localObject2 = localObject1;
              i = j;
              n = k;
              if (!((File)localObject5).exists()) {
                break;
              }
              localObject2 = localObject1;
              i = j;
              n = k;
              if (!bool1) {
                break;
              }
              ImageLoader.this.generateThumb(localMessageObject.getFileType(), (File)localObject5, (TLRPC.FileLocation)paramTLObject, paramString5);
              localObject2 = localObject1;
              i = j;
              n = k;
              break;
            }
            label1269:
            j = 0;
            break label337;
            label1274:
            if (((!(paramTLObject instanceof TLRPC.TL_webDocument)) || (!MessageObject.isGifDocument((TLRPC.TL_webDocument)paramTLObject))) && ((!(paramTLObject instanceof TLRPC.Document)) || ((!MessageObject.isGifDocument((TLRPC.Document)paramTLObject)) && (!MessageObject.isRoundVideoDocument((TLRPC.Document)paramTLObject))))) {
              break label416;
            }
            ((ImageLoader.CacheImage)localObject5).animatedFile = true;
            break label416;
            label1342:
            k = i;
            if (paramInt2 != 2) {
              break label478;
            }
            localObject1 = new File(FileLoader.getDirectory(4), paramString2 + ".enc");
            k = i;
            break label478;
            label1394:
            if ((paramTLObject instanceof TLRPC.Document))
            {
              if (MessageObject.isVideoDocument((TLRPC.Document)paramTLObject))
              {
                localObject1 = new File(FileLoader.getDirectory(2), paramString2);
                k = i;
                break label478;
              }
              localObject1 = new File(FileLoader.getDirectory(3), paramString2);
              k = i;
              break label478;
            }
            if ((paramTLObject instanceof TLRPC.TL_webDocument))
            {
              localObject1 = new File(FileLoader.getDirectory(3), paramString2);
              k = i;
              break label478;
            }
            localObject1 = new File(FileLoader.getDirectory(0), paramString2);
            k = i;
            break label478;
            label1515:
            bool = false;
            break label488;
            label1521:
            bool = false;
            break label613;
            label1527:
            ImageLoader.this.cacheOutQueue.postRunnable(((ImageLoader.CacheImage)localObject5).cacheTask);
            continue;
            label1545:
            ((ImageLoader.CacheImage)localObject5).url = paramString2;
            ((ImageLoader.CacheImage)localObject5).location = paramTLObject;
            ImageLoader.this.imageLoadingByUrl.put(paramString2, localObject5);
            if (paramString4 == null)
            {
              if ((paramTLObject instanceof TLRPC.FileLocation))
              {
                localObject1 = (TLRPC.FileLocation)paramTLObject;
                j = paramInt2;
                i = j;
                if (j == 0) {
                  if (paramInt1 > 0)
                  {
                    i = j;
                    if (((TLRPC.FileLocation)localObject1).key == null) {}
                  }
                  else
                  {
                    i = 1;
                  }
                }
                FileLoader.getInstance(i).loadFile((TLRPC.FileLocation)localObject1, paramString3, paramInt1, i);
              }
              for (;;)
              {
                if (!paramImageReceiver.isForceLoding()) {
                  break label1755;
                }
                ImageLoader.this.forceLoadingImages.put(((ImageLoader.CacheImage)localObject5).key, Integer.valueOf(0));
                break;
                if ((paramTLObject instanceof TLRPC.Document)) {
                  FileLoader.getInstance(i).loadFile((TLRPC.Document)paramTLObject, true, paramInt2);
                } else if ((paramTLObject instanceof TLRPC.TL_webDocument)) {
                  FileLoader.getInstance(i).loadFile((TLRPC.TL_webDocument)paramTLObject, true, paramInt2);
                }
              }
            }
            else
            {
              label1755:
              localObject2 = Utilities.MD5(paramString4);
              ((ImageLoader.CacheImage)localObject5).tempFilePath = new File(FileLoader.getDirectory(4), (String)localObject2 + "_temp.jpg");
              ((ImageLoader.CacheImage)localObject5).finalFilePath = ((File)localObject1);
              ((ImageLoader.CacheImage)localObject5).httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, (ImageLoader.CacheImage)localObject5, paramInt1);
              ImageLoader.this.httpTasks.add(((ImageLoader.CacheImage)localObject5).httpTask);
              ImageLoader.this.runHttpTasks(false);
            }
          }
        }
      });
      break;
      bool1 = false;
      break label21;
    }
  }
  
  private void fileDidFailedLoad(final String paramString, int paramInt)
  {
    if (paramInt == 1) {}
    for (;;)
    {
      return;
      this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.CacheImage localCacheImage = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString);
          if (localCacheImage != null) {
            localCacheImage.setImageAndClear(null);
          }
        }
      });
    }
  }
  
  private void fileDidLoaded(final String paramString, final File paramFile, final int paramInt)
  {
    this.imageLoadQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(paramString);
        if (localObject != null)
        {
          ImageLoader.this.generateThumb(paramInt, paramFile, ImageLoader.ThumbGenerateInfo.access$3600((ImageLoader.ThumbGenerateInfo)localObject), ImageLoader.ThumbGenerateInfo.access$3700((ImageLoader.ThumbGenerateInfo)localObject));
          ImageLoader.this.waitingForQualityThumb.remove(paramString);
        }
        ImageLoader.CacheImage localCacheImage1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString);
        if (localCacheImage1 == null) {
          return;
        }
        ImageLoader.this.imageLoadingByUrl.remove(paramString);
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < localCacheImage1.imageReceiverArray.size(); i++)
        {
          String str1 = (String)localCacheImage1.keys.get(i);
          String str2 = (String)localCacheImage1.filters.get(i);
          Boolean localBoolean = (Boolean)localCacheImage1.thumbs.get(i);
          ImageReceiver localImageReceiver = (ImageReceiver)localCacheImage1.imageReceiverArray.get(i);
          ImageLoader.CacheImage localCacheImage2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(str1);
          localObject = localCacheImage2;
          if (localCacheImage2 == null)
          {
            localObject = new ImageLoader.CacheImage(ImageLoader.this, null);
            ((ImageLoader.CacheImage)localObject).currentAccount = localCacheImage1.currentAccount;
            ((ImageLoader.CacheImage)localObject).finalFilePath = paramFile;
            ((ImageLoader.CacheImage)localObject).key = str1;
            ((ImageLoader.CacheImage)localObject).httpUrl = localCacheImage1.httpUrl;
            ((ImageLoader.CacheImage)localObject).selfThumb = localBoolean.booleanValue();
            ((ImageLoader.CacheImage)localObject).ext = localCacheImage1.ext;
            ((ImageLoader.CacheImage)localObject).encryptionKeyPath = localCacheImage1.encryptionKeyPath;
            ((ImageLoader.CacheImage)localObject).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject);
            ((ImageLoader.CacheImage)localObject).filter = str2;
            ((ImageLoader.CacheImage)localObject).animatedFile = localCacheImage1.animatedFile;
            ImageLoader.this.imageLoadingByKeys.put(str1, localObject);
            localArrayList.add(((ImageLoader.CacheImage)localObject).cacheTask);
          }
          ((ImageLoader.CacheImage)localObject).addImageReceiver(localImageReceiver, str1, str2, localBoolean.booleanValue());
        }
        i = 0;
        label347:
        if (i < localArrayList.size())
        {
          localObject = (ImageLoader.CacheOutTask)localArrayList.get(i);
          if (!ImageLoader.CacheOutTask.access$1800((ImageLoader.CacheOutTask)localObject).selfThumb) {
            break label393;
          }
          ImageLoader.this.cacheThumbOutQueue.postRunnable((Runnable)localObject);
        }
        for (;;)
        {
          i++;
          break label347;
          break;
          label393:
          ImageLoader.this.cacheOutQueue.postRunnable((Runnable)localObject);
        }
      }
    });
  }
  
  public static void fillPhotoSizeWithBytes(TLRPC.PhotoSize paramPhotoSize)
  {
    if ((paramPhotoSize == null) || (paramPhotoSize.bytes != null)) {}
    for (;;)
    {
      return;
      File localFile = FileLoader.getPathToAttach(paramPhotoSize, true);
      try
      {
        RandomAccessFile localRandomAccessFile = new java/io/RandomAccessFile;
        localRandomAccessFile.<init>(localFile, "r");
        if ((int)localRandomAccessFile.length() < 20000)
        {
          paramPhotoSize.bytes = new byte[(int)localRandomAccessFile.length()];
          localRandomAccessFile.readFully(paramPhotoSize.bytes, 0, paramPhotoSize.bytes.length);
        }
      }
      catch (Throwable paramPhotoSize)
      {
        FileLog.e(paramPhotoSize);
      }
    }
  }
  
  private void generateThumb(int paramInt, File paramFile, TLRPC.FileLocation paramFileLocation, String paramString)
  {
    if (((paramInt != 0) && (paramInt != 2) && (paramInt != 3)) || (paramFile == null) || (paramFileLocation == null)) {}
    for (;;)
    {
      return;
      String str = FileLoader.getAttachFileName(paramFileLocation);
      if ((ThumbGenerateTask)this.thumbGenerateTasks.get(str) == null)
      {
        paramFile = new ThumbGenerateTask(paramInt, paramFile, paramFileLocation, paramString);
        this.thumbGeneratingQueue.postRunnable(paramFile);
      }
    }
  }
  
  public static String getHttpUrlExtension(String paramString1, String paramString2)
  {
    Object localObject = null;
    String str1 = Uri.parse(paramString1).getLastPathSegment();
    String str2 = paramString1;
    if (!TextUtils.isEmpty(str1))
    {
      str2 = paramString1;
      if (str1.length() > 1) {
        str2 = str1;
      }
    }
    int i = str2.lastIndexOf('.');
    paramString1 = (String)localObject;
    if (i != -1) {
      paramString1 = str2.substring(i + 1);
    }
    if ((paramString1 != null) && (paramString1.length() != 0))
    {
      str2 = paramString1;
      if (paramString1.length() <= 4) {}
    }
    else
    {
      str2 = paramString2;
    }
    return str2;
  }
  
  /* Error */
  public static ImageLoader getInstance()
  {
    // Byte code:
    //   0: getstatic 159	org/telegram/messenger/ImageLoader:Instance	Lorg/telegram/messenger/ImageLoader;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 159	org/telegram/messenger/ImageLoader:Instance	Lorg/telegram/messenger/ImageLoader;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/messenger/ImageLoader
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 591	org/telegram/messenger/ImageLoader:<init>	()V
    //   31: aload_1
    //   32: putstatic 159	org/telegram/messenger/ImageLoader:Instance	Lorg/telegram/messenger/ImageLoader;
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
    //   3	17	0	localImageLoader1	ImageLoader
    //   5	34	1	localImageLoader2	ImageLoader
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
  
  private void httpFileLoadError(final String paramString)
  {
    this.imageLoadQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ImageLoader.CacheImage localCacheImage = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString);
        if (localCacheImage == null) {}
        for (;;)
        {
          return;
          ImageLoader.HttpImageTask localHttpImageTask = localCacheImage.httpTask;
          localCacheImage.httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, ImageLoader.HttpImageTask.access$400(localHttpImageTask), ImageLoader.HttpImageTask.access$4100(localHttpImageTask));
          ImageLoader.this.httpTasks.add(localCacheImage.httpTask);
          ImageLoader.this.runHttpTasks(false);
        }
      }
    });
  }
  
  public static Bitmap loadBitmap(String paramString, Uri paramUri, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    Object localObject1 = null;
    String str = paramString;
    if (paramString == null)
    {
      str = paramString;
      if (paramUri != null)
      {
        str = paramString;
        if (paramUri.getScheme() != null)
        {
          if (!paramUri.getScheme().contains("file")) {
            break label164;
          }
          str = paramUri.getPath();
        }
      }
    }
    if (str != null) {
      BitmapFactory.decodeFile(str, localOptions);
    }
    label74:
    float f1;
    float f2;
    int i;
    label164:
    Object localObject2;
    for (;;)
    {
      f1 = localOptions.outWidth;
      f2 = localOptions.outHeight;
      if (paramBoolean)
      {
        paramFloat1 = Math.max(f1 / paramFloat1, f2 / paramFloat2);
        paramFloat2 = paramFloat1;
        if (paramFloat1 < 1.0F) {
          paramFloat2 = 1.0F;
        }
        localOptions.inJustDecodeBounds = false;
        localOptions.inSampleSize = ((int)paramFloat2);
        if (localOptions.inSampleSize % 2 == 0) {
          break label259;
        }
        i = 1;
        for (;;)
        {
          if (i * 2 < localOptions.inSampleSize)
          {
            i *= 2;
            continue;
            try
            {
              str = AndroidUtilities.getPath(paramUri);
            }
            catch (Throwable localThrowable1)
            {
              FileLog.e(localThrowable1);
              localObject2 = paramString;
            }
            break;
            if (paramUri == null) {
              break label74;
            }
            try
            {
              paramString = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
              BitmapFactory.decodeStream(paramString, null, localOptions);
              paramString.close();
              localObject1 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
            }
            catch (Throwable paramString)
            {
              FileLog.e(paramString);
              paramString = null;
            }
          }
        }
      }
    }
    for (;;)
    {
      return paramString;
      paramFloat1 = Math.min(f1 / paramFloat1, f2 / paramFloat2);
      break;
      localOptions.inSampleSize = i;
      label259:
      label270:
      label287:
      Object localObject4;
      if (Build.VERSION.SDK_INT < 21)
      {
        paramBoolean = true;
        localOptions.inPurgeable = paramBoolean;
        paramString = null;
        if (localObject2 == null) {
          break label465;
        }
        paramString = (String)localObject2;
        localObject3 = null;
        localObject4 = localObject3;
        if (paramString == null) {}
      }
      try
      {
        localObject4 = new android/support/media/ExifInterface;
        ((ExifInterface)localObject4).<init>(paramString);
        i = ((ExifInterface)localObject4).getAttributeInt("Orientation", 1);
        paramString = new android/graphics/Matrix;
        paramString.<init>();
        switch (i)
        {
        case 4: 
        case 5: 
        case 7: 
        default: 
          label368:
          localObject4 = paramString;
        }
      }
      catch (Throwable paramString)
      {
        for (;;)
        {
          label371:
          Object localObject6;
          label465:
          Object localObject5 = localObject3;
        }
      }
      localObject6 = null;
      localObject3 = null;
      paramString = null;
      if (localObject2 != null)
      {
        paramString = (String)localObject6;
        try
        {
          paramUri = BitmapFactory.decodeFile((String)localObject2, localOptions);
          paramString = paramUri;
          if (paramUri == null) {
            continue;
          }
          paramString = paramUri;
          if (localOptions.inPurgeable)
          {
            paramString = paramUri;
            Utilities.pinBitmap(paramUri);
          }
          paramString = paramUri;
          localObject1 = Bitmaps.createBitmap(paramUri, 0, 0, paramUri.getWidth(), paramUri.getHeight(), (Matrix)localObject4, true);
          paramString = paramUri;
          if (localObject1 == paramUri) {
            continue;
          }
          paramString = paramUri;
          paramUri.recycle();
          paramString = (String)localObject1;
          continue;
          paramBoolean = false;
          break label270;
          if (paramUri == null) {
            break label287;
          }
          paramString = AndroidUtilities.getPath(paramUri);
          break label287;
          try
          {
            paramString.postRotate(90.0F);
          }
          catch (Throwable localThrowable2)
          {
            localObject5 = paramString;
          }
          break label371;
          paramString.postRotate(180.0F);
          break label368;
          paramString.postRotate(270.0F);
        }
        catch (Throwable paramUri)
        {
          FileLog.e(paramUri);
          getInstance().clearMemory();
          paramUri = paramString;
          if (paramString == null) {}
          try
          {
            localObject2 = BitmapFactory.decodeFile((String)localObject2, localOptions);
            paramUri = (Uri)localObject2;
            if (localObject2 != null)
            {
              paramUri = (Uri)localObject2;
              paramString = (String)localObject2;
              if (localOptions.inPurgeable)
              {
                paramString = (String)localObject2;
                Utilities.pinBitmap((Bitmap)localObject2);
                paramUri = (Uri)localObject2;
              }
            }
            paramString = paramUri;
            if (paramUri == null) {
              continue;
            }
            paramString = paramUri;
            localObject2 = Bitmaps.createBitmap(paramUri, 0, 0, paramUri.getWidth(), paramUri.getHeight(), (Matrix)localObject5, true);
            paramString = paramUri;
            if (localObject2 == paramUri) {
              continue;
            }
            paramString = paramUri;
            paramUri.recycle();
            paramString = (String)localObject2;
          }
          catch (Throwable paramUri)
          {
            FileLog.e(paramUri);
          }
        }
        continue;
      }
      if (paramUri == null) {
        continue;
      }
      paramString = (String)localObject3;
      try
      {
        paramUri = BitmapFactory.decodeStream((InputStream)localObject1, null, localOptions);
        paramString = paramUri;
        if (paramUri != null)
        {
          paramString = paramUri;
          if (localOptions.inPurgeable)
          {
            paramString = paramUri;
            Utilities.pinBitmap(paramUri);
          }
          paramString = paramUri;
          localObject2 = Bitmaps.createBitmap(paramUri, 0, 0, paramUri.getWidth(), paramUri.getHeight(), (Matrix)localObject5, true);
          paramString = paramUri;
          if (localObject2 != paramUri)
          {
            paramString = paramUri;
            paramUri.recycle();
            paramString = (String)localObject2;
          }
        }
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (Throwable paramUri)
        {
          FileLog.e(paramUri);
        }
      }
      catch (Throwable paramUri)
      {
        FileLog.e(paramUri);
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (Throwable paramUri)
        {
          FileLog.e(paramUri);
        }
      }
      finally
      {
        try
        {
          ((InputStream)localObject1).close();
          throw paramString;
        }
        catch (Throwable paramUri)
        {
          for (;;)
          {
            FileLog.e(paramUri);
          }
        }
      }
    }
  }
  
  private void performReplace(String paramString1, String paramString2)
  {
    Object localObject1 = this.memCache.get(paramString1);
    if (localObject1 != null)
    {
      Object localObject2 = this.memCache.get(paramString2);
      int i = 0;
      int j = i;
      if (localObject2 != null)
      {
        j = i;
        if (((BitmapDrawable)localObject2).getBitmap() != null)
        {
          j = i;
          if (((BitmapDrawable)localObject1).getBitmap() != null)
          {
            Bitmap localBitmap = ((BitmapDrawable)localObject2).getBitmap();
            localObject2 = ((BitmapDrawable)localObject1).getBitmap();
            if (localBitmap.getWidth() <= ((Bitmap)localObject2).getWidth())
            {
              j = i;
              if (localBitmap.getHeight() <= ((Bitmap)localObject2).getHeight()) {}
            }
            else
            {
              j = 1;
            }
          }
        }
      }
      if (j != 0) {
        break label174;
      }
      this.ignoreRemoval = paramString1;
      this.memCache.remove(paramString1);
      this.memCache.put(paramString2, (BitmapDrawable)localObject1);
      this.ignoreRemoval = null;
    }
    for (;;)
    {
      localObject1 = (Integer)this.bitmapUseCounts.get(paramString1);
      if (localObject1 != null)
      {
        this.bitmapUseCounts.put(paramString2, localObject1);
        this.bitmapUseCounts.remove(paramString1);
      }
      return;
      label174:
      this.memCache.remove(paramString1);
    }
  }
  
  private void removeFromWaitingForThumb(int paramInt)
  {
    String str = (String)this.waitingForQualityThumbByTag.get(paramInt);
    if (str != null)
    {
      ThumbGenerateInfo localThumbGenerateInfo = (ThumbGenerateInfo)this.waitingForQualityThumb.get(str);
      if (localThumbGenerateInfo != null)
      {
        ThumbGenerateInfo.access$3110(localThumbGenerateInfo);
        if (localThumbGenerateInfo.count == 0) {
          this.waitingForQualityThumb.remove(str);
        }
      }
      this.waitingForQualityThumbByTag.remove(paramInt);
    }
  }
  
  private void replaceImageInCacheInternal(String paramString1, String paramString2, TLRPC.FileLocation paramFileLocation)
  {
    ArrayList localArrayList = this.memCache.getFilterKeys(paramString1);
    if (localArrayList != null) {
      for (int i = 0; i < localArrayList.size(); i++)
      {
        String str1 = (String)localArrayList.get(i);
        String str2 = paramString1 + "@" + str1;
        str1 = paramString2 + "@" + str1;
        performReplace(str2, str1);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { str2, str1, paramFileLocation });
      }
    }
    performReplace(paramString1, paramString2);
    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { paramString1, paramString2, paramFileLocation });
  }
  
  private void runHttpFileLoadTasks(final HttpFileTask paramHttpFileTask, final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramHttpFileTask != null) {
          ImageLoader.access$4210(ImageLoader.this);
        }
        Object localObject;
        if (paramHttpFileTask != null)
        {
          if (paramInt != 1) {
            break label243;
          }
          if (!ImageLoader.HttpFileTask.access$4300(paramHttpFileTask)) {
            break label185;
          }
          localObject = new Runnable()
          {
            public void run()
            {
              ImageLoader.this.httpFileLoadTasks.add(this.val$newTask);
              ImageLoader.this.runHttpFileLoadTasks(null, 0);
            }
          };
          ImageLoader.this.retryHttpsTasks.put(ImageLoader.HttpFileTask.access$000(paramHttpFileTask), localObject);
          AndroidUtilities.runOnUIThread((Runnable)localObject, 1000L);
        }
        while ((ImageLoader.this.currentHttpFileLoadTasksCount < 2) && (!ImageLoader.this.httpFileLoadTasks.isEmpty()))
        {
          ((ImageLoader.HttpFileTask)ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          ImageLoader.access$4208(ImageLoader.this);
          continue;
          label185:
          ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(paramHttpFileTask));
          NotificationCenter.getInstance(ImageLoader.HttpFileTask.access$200(paramHttpFileTask)).postNotificationName(NotificationCenter.httpFileDidFailedLoad, new Object[] { ImageLoader.HttpFileTask.access$000(paramHttpFileTask), Integer.valueOf(0) });
          continue;
          label243:
          if (paramInt == 2)
          {
            ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(paramHttpFileTask));
            localObject = new File(FileLoader.getDirectory(4), Utilities.MD5(ImageLoader.HttpFileTask.access$000(paramHttpFileTask)) + "." + ImageLoader.HttpFileTask.access$4500(paramHttpFileTask));
            if (ImageLoader.HttpFileTask.access$4400(paramHttpFileTask).renameTo((File)localObject)) {}
            for (localObject = ((File)localObject).toString();; localObject = ImageLoader.HttpFileTask.access$4400(paramHttpFileTask).toString())
            {
              NotificationCenter.getInstance(ImageLoader.HttpFileTask.access$200(paramHttpFileTask)).postNotificationName(NotificationCenter.httpFileDidLoaded, new Object[] { ImageLoader.HttpFileTask.access$000(paramHttpFileTask), localObject });
              break;
            }
          }
        }
      }
    });
  }
  
  private void runHttpTasks(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.currentHttpTasksCount -= 1; (this.currentHttpTasksCount < 4) && (!this.httpTasks.isEmpty()); this.currentHttpTasksCount += 1) {
      ((HttpImageTask)this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
    }
  }
  
  public static void saveMessageThumbs(TLRPC.Message paramMessage)
  {
    Object localObject1 = null;
    int i;
    int j;
    Object localObject2;
    label63:
    Object localObject3;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      i = 0;
      j = paramMessage.media.photo.sizes.size();
      localObject2 = localObject1;
      if (i < j)
      {
        localObject2 = (TLRPC.PhotoSize)paramMessage.media.photo.sizes.get(i);
        if (!(localObject2 instanceof TLRPC.TL_photoCachedSize)) {}
      }
      else if ((localObject2 != null) && (((TLRPC.PhotoSize)localObject2).bytes != null) && (((TLRPC.PhotoSize)localObject2).bytes.length != 0))
      {
        if ((((TLRPC.PhotoSize)localObject2).location instanceof TLRPC.TL_fileLocationUnavailable))
        {
          ((TLRPC.PhotoSize)localObject2).location = new TLRPC.TL_fileLocation();
          ((TLRPC.PhotoSize)localObject2).location.volume_id = -2147483648L;
          ((TLRPC.PhotoSize)localObject2).location.dc_id = Integer.MIN_VALUE;
          ((TLRPC.PhotoSize)localObject2).location.local_id = SharedConfig.getLastLocalId();
        }
        localObject3 = FileLoader.getPathToAttach((TLObject)localObject2, true);
        i = 0;
        localObject1 = localObject3;
        if (MessageObject.shouldEncryptPhotoOrVideo(paramMessage))
        {
          localObject1 = new File(((File)localObject3).getAbsolutePath() + ".enc");
          i = 1;
        }
        if ((!((File)localObject1).exists()) && (i == 0)) {}
      }
    }
    try
    {
      localObject3 = new java/io/File;
      localObject4 = FileLoader.getInternalCacheDir();
      localObject5 = new java/lang/StringBuilder;
      ((StringBuilder)localObject5).<init>();
      ((File)localObject3).<init>((File)localObject4, ((File)localObject1).getName() + ".key");
      localObject5 = new java/io/RandomAccessFile;
      ((RandomAccessFile)localObject5).<init>((File)localObject3, "rws");
      long l = ((RandomAccessFile)localObject5).length();
      localObject4 = new byte[32];
      localObject3 = new byte[16];
      if ((l <= 0L) || (l % 48L != 0L)) {
        break label645;
      }
      ((RandomAccessFile)localObject5).read((byte[])localObject4, 0, 32);
      ((RandomAccessFile)localObject5).read((byte[])localObject3, 0, 16);
      label325:
      ((RandomAccessFile)localObject5).close();
      Utilities.aesCtrDecryptionByteArray(((TLRPC.PhotoSize)localObject2).bytes, (byte[])localObject4, (byte[])localObject3, 0, ((TLRPC.PhotoSize)localObject2).bytes.length, 0);
      localObject3 = new java/io/RandomAccessFile;
      ((RandomAccessFile)localObject3).<init>((File)localObject1, "rws");
      ((RandomAccessFile)localObject3).write(((TLRPC.PhotoSize)localObject2).bytes);
      ((RandomAccessFile)localObject3).close();
    }
    catch (Exception localException)
    {
      do
      {
        for (;;)
        {
          Object localObject4;
          Object localObject5;
          FileLog.e(localException);
          continue;
          i++;
          continue;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
            break;
          }
          paramMessage.media.document.thumb = localException;
        }
      } while (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage));
      i = 0;
      j = paramMessage.media.webpage.photo.sizes.size();
    }
    localObject1 = new TLRPC.TL_photoSize();
    ((TLRPC.TL_photoSize)localObject1).w = ((TLRPC.PhotoSize)localObject2).w;
    ((TLRPC.TL_photoSize)localObject1).h = ((TLRPC.PhotoSize)localObject2).h;
    ((TLRPC.TL_photoSize)localObject1).location = ((TLRPC.PhotoSize)localObject2).location;
    ((TLRPC.TL_photoSize)localObject1).size = ((TLRPC.PhotoSize)localObject2).size;
    ((TLRPC.TL_photoSize)localObject1).type = ((TLRPC.PhotoSize)localObject2).type;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      i = 0;
      j = paramMessage.media.photo.sizes.size();
      if (i < j)
      {
        if (!(paramMessage.media.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize)) {
          break label686;
        }
        paramMessage.media.photo.sizes.set(i, localObject1);
      }
    }
    label645:
    label686:
    label799:
    for (;;)
    {
      return;
      i++;
      break;
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
      {
        localObject2 = localObject1;
        if (!(paramMessage.media.document.thumb instanceof TLRPC.TL_photoCachedSize)) {
          break label63;
        }
        localObject2 = paramMessage.media.document.thumb;
        break label63;
      }
      localObject2 = localObject1;
      if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
        break label63;
      }
      localObject2 = localObject1;
      if (paramMessage.media.webpage.photo == null) {
        break label63;
      }
      i = 0;
      j = paramMessage.media.webpage.photo.sizes.size();
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= j) {
          break;
        }
        localObject2 = (TLRPC.PhotoSize)paramMessage.media.webpage.photo.sizes.get(i);
        if ((localObject2 instanceof TLRPC.TL_photoCachedSize)) {
          break;
        }
        i++;
      }
      Utilities.random.nextBytes((byte[])localObject4);
      Utilities.random.nextBytes((byte[])localObject3);
      ((RandomAccessFile)localObject5).write((byte[])localObject4);
      ((RandomAccessFile)localObject5).write((byte[])localObject3);
      break label325;
      for (;;)
      {
        if (i >= j) {
          break label799;
        }
        if ((paramMessage.media.webpage.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize))
        {
          paramMessage.media.webpage.photo.sizes.set(i, localException);
          break;
        }
        i++;
      }
    }
  }
  
  public static void saveMessagesThumbs(ArrayList<TLRPC.Message> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      for (int i = 0; i < paramArrayList.size(); i++) {
        saveMessageThumbs((TLRPC.Message)paramArrayList.get(i));
      }
    }
  }
  
  public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap paramBitmap, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
  {
    return scaleAndSaveImage(paramBitmap, paramFloat1, paramFloat2, paramInt, paramBoolean, 0, 0);
  }
  
  public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap paramBitmap, float paramFloat1, float paramFloat2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    if (paramBitmap == null) {
      paramBitmap = null;
    }
    for (;;)
    {
      return paramBitmap;
      float f1 = paramBitmap.getWidth();
      float f2 = paramBitmap.getHeight();
      if ((f1 == 0.0F) || (f2 == 0.0F))
      {
        paramBitmap = null;
      }
      else
      {
        boolean bool1 = false;
        paramFloat2 = Math.max(f1 / paramFloat1, f2 / paramFloat2);
        paramFloat1 = paramFloat2;
        boolean bool2 = bool1;
        if (paramInt2 != 0)
        {
          paramFloat1 = paramFloat2;
          bool2 = bool1;
          if (paramInt3 != 0) {
            if (f1 >= paramInt2)
            {
              paramFloat1 = paramFloat2;
              bool2 = bool1;
              if (f2 >= paramInt3) {}
            }
            else
            {
              if ((f1 >= paramInt2) || (f2 <= paramInt3)) {
                break label159;
              }
              paramFloat1 = f1 / paramInt2;
            }
          }
        }
        for (;;)
        {
          bool2 = true;
          paramInt2 = (int)(f1 / paramFloat1);
          paramInt3 = (int)(f2 / paramFloat1);
          if ((paramInt3 != 0) && (paramInt2 != 0)) {
            break label206;
          }
          paramBitmap = null;
          break;
          label159:
          if ((f1 > paramInt2) && (f2 < paramInt3)) {
            paramFloat1 = f2 / paramInt3;
          } else {
            paramFloat1 = Math.max(f1 / paramInt2, f2 / paramInt3);
          }
        }
        try
        {
          label206:
          TLRPC.PhotoSize localPhotoSize = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool2);
          paramBitmap = localPhotoSize;
        }
        catch (Throwable localThrowable)
        {
          FileLog.e(localThrowable);
          getInstance().clearMemory();
          System.gc();
          try
          {
            paramBitmap = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool2);
          }
          catch (Throwable paramBitmap)
          {
            FileLog.e(paramBitmap);
            paramBitmap = null;
          }
        }
      }
    }
  }
  
  private static TLRPC.PhotoSize scaleAndSaveImageInternal(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
    throws Exception
  {
    Bitmap localBitmap;
    TLRPC.TL_photoSize localTL_photoSize;
    label118:
    FileOutputStream localFileOutputStream;
    if ((paramFloat3 > 1.0F) || (paramBoolean2))
    {
      localBitmap = Bitmaps.createScaledBitmap(paramBitmap, paramInt1, paramInt2, true);
      Object localObject = new TLRPC.TL_fileLocation();
      ((TLRPC.TL_fileLocation)localObject).volume_id = -2147483648L;
      ((TLRPC.TL_fileLocation)localObject).dc_id = Integer.MIN_VALUE;
      ((TLRPC.TL_fileLocation)localObject).local_id = SharedConfig.getLastLocalId();
      localTL_photoSize = new TLRPC.TL_photoSize();
      localTL_photoSize.location = ((TLRPC.FileLocation)localObject);
      localTL_photoSize.w = localBitmap.getWidth();
      localTL_photoSize.h = localBitmap.getHeight();
      if ((localTL_photoSize.w > 100) || (localTL_photoSize.h > 100)) {
        break label271;
      }
      localTL_photoSize.type = "s";
      localObject = ((TLRPC.TL_fileLocation)localObject).volume_id + "_" + ((TLRPC.TL_fileLocation)localObject).local_id + ".jpg";
      localFileOutputStream = new FileOutputStream(new File(FileLoader.getDirectory(4), (String)localObject));
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, localFileOutputStream);
      if (!paramBoolean1) {
        break label381;
      }
      localObject = new ByteArrayOutputStream();
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, (OutputStream)localObject);
      localTL_photoSize.bytes = ((ByteArrayOutputStream)localObject).toByteArray();
      localTL_photoSize.size = localTL_photoSize.bytes.length;
      ((ByteArrayOutputStream)localObject).close();
    }
    for (;;)
    {
      localFileOutputStream.close();
      if (localBitmap != paramBitmap) {
        localBitmap.recycle();
      }
      return localTL_photoSize;
      localBitmap = paramBitmap;
      break;
      label271:
      if ((localTL_photoSize.w <= 320) && (localTL_photoSize.h <= 320))
      {
        localTL_photoSize.type = "m";
        break label118;
      }
      if ((localTL_photoSize.w <= 800) && (localTL_photoSize.h <= 800))
      {
        localTL_photoSize.type = "x";
        break label118;
      }
      if ((localTL_photoSize.w <= 1280) && (localTL_photoSize.h <= 1280))
      {
        localTL_photoSize.type = "y";
        break label118;
      }
      localTL_photoSize.type = "w";
      break label118;
      label381:
      localTL_photoSize.size = ((int)localFileOutputStream.getChannel().size());
    }
  }
  
  public void cancelForceLoadingForImageReceiver(final ImageReceiver paramImageReceiver)
  {
    if (paramImageReceiver == null) {}
    for (;;)
    {
      return;
      paramImageReceiver = paramImageReceiver.getKey();
      if (paramImageReceiver != null) {
        this.imageLoadQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.forceLoadingImages.remove(paramImageReceiver);
          }
        });
      }
    }
  }
  
  public void cancelLoadHttpFile(String paramString)
  {
    HttpFileTask localHttpFileTask = (HttpFileTask)this.httpFileLoadTasksByKeys.get(paramString);
    if (localHttpFileTask != null)
    {
      localHttpFileTask.cancel(true);
      this.httpFileLoadTasksByKeys.remove(paramString);
      this.httpFileLoadTasks.remove(localHttpFileTask);
    }
    paramString = (Runnable)this.retryHttpsTasks.get(paramString);
    if (paramString != null) {
      AndroidUtilities.cancelRunOnUIThread(paramString);
    }
    runHttpFileLoadTasks(null, 0);
  }
  
  public void cancelLoadingForImageReceiver(final ImageReceiver paramImageReceiver, final int paramInt)
  {
    if (paramImageReceiver == null) {}
    for (;;)
    {
      return;
      this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          int j = 2;
          int k;
          Object localObject;
          if (paramInt == 1)
          {
            k = 1;
            if (i >= k) {
              return;
            }
            localObject = paramImageReceiver;
            if (i != 0) {
              break label109;
            }
          }
          label109:
          for (boolean bool = true;; bool = false)
          {
            j = ((ImageReceiver)localObject).getTag(bool);
            if (i == 0) {
              ImageLoader.this.removeFromWaitingForThumb(j);
            }
            if (j != 0)
            {
              localObject = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(j);
              if (localObject != null) {
                ((ImageLoader.CacheImage)localObject).removeImageReceiver(paramImageReceiver);
              }
            }
            i++;
            break;
            k = j;
            if (paramInt != 2) {
              break;
            }
            i = 1;
            k = j;
            break;
          }
        }
      });
    }
  }
  
  public void checkMediaPaths()
  {
    this.cacheOutQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            FileLoader.setMediaDirs(this.val$paths);
          }
        });
      }
    });
  }
  
  public void clearMemory()
  {
    this.memCache.evictAll();
  }
  
  /* Error */
  public SparseArray<File> createMediaPaths()
  {
    // Byte code:
    //   0: new 178	android/util/SparseArray
    //   3: dup
    //   4: invokespecial 179	android/util/SparseArray:<init>	()V
    //   7: astore_1
    //   8: invokestatic 277	org/telegram/messenger/AndroidUtilities:getCacheDir	()Ljava/io/File;
    //   11: astore_2
    //   12: aload_2
    //   13: invokevirtual 283	java/io/File:isDirectory	()Z
    //   16: ifne +8 -> 24
    //   19: aload_2
    //   20: invokevirtual 286	java/io/File:mkdirs	()Z
    //   23: pop
    //   24: new 279	java/io/File
    //   27: astore_3
    //   28: aload_3
    //   29: aload_2
    //   30: ldc_w 288
    //   33: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   36: aload_3
    //   37: invokevirtual 294	java/io/File:createNewFile	()Z
    //   40: pop
    //   41: aload_1
    //   42: iconst_4
    //   43: aload_2
    //   44: invokevirtual 298	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   47: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   50: ifeq +26 -> 76
    //   53: new 756	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 757	java/lang/StringBuilder:<init>	()V
    //   60: ldc_w 1062
    //   63: invokevirtual 761	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload_2
    //   67: invokevirtual 1065	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   70: invokevirtual 766	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   73: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   76: ldc_w 1070
    //   79: invokestatic 1075	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   82: invokevirtual 1078	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   85: ifeq +455 -> 540
    //   88: new 279	java/io/File
    //   91: astore_3
    //   92: aload_3
    //   93: invokestatic 1081	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   96: ldc_w 1083
    //   99: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   102: aload_0
    //   103: aload_3
    //   104: putfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   107: aload_0
    //   108: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   111: invokevirtual 286	java/io/File:mkdirs	()Z
    //   114: pop
    //   115: aload_0
    //   116: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   119: invokevirtual 283	java/io/File:isDirectory	()Z
    //   122: istore 4
    //   124: iload 4
    //   126: ifeq +353 -> 479
    //   129: new 279	java/io/File
    //   132: astore 5
    //   134: aload 5
    //   136: aload_0
    //   137: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   140: ldc_w 1085
    //   143: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   146: aload 5
    //   148: invokevirtual 1088	java/io/File:mkdir	()Z
    //   151: pop
    //   152: aload 5
    //   154: invokevirtual 283	java/io/File:isDirectory	()Z
    //   157: ifeq +53 -> 210
    //   160: aload_0
    //   161: aload_2
    //   162: aload 5
    //   164: iconst_0
    //   165: invokespecial 1090	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   168: ifeq +42 -> 210
    //   171: aload_1
    //   172: iconst_0
    //   173: aload 5
    //   175: invokevirtual 298	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   178: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   181: ifeq +29 -> 210
    //   184: new 756	java/lang/StringBuilder
    //   187: astore_3
    //   188: aload_3
    //   189: invokespecial 757	java/lang/StringBuilder:<init>	()V
    //   192: aload_3
    //   193: ldc_w 1092
    //   196: invokevirtual 761	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   199: aload 5
    //   201: invokevirtual 1065	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 766	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   210: new 279	java/io/File
    //   213: astore 5
    //   215: aload 5
    //   217: aload_0
    //   218: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   221: ldc_w 1094
    //   224: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   227: aload 5
    //   229: invokevirtual 1088	java/io/File:mkdir	()Z
    //   232: pop
    //   233: aload 5
    //   235: invokevirtual 283	java/io/File:isDirectory	()Z
    //   238: ifeq +53 -> 291
    //   241: aload_0
    //   242: aload_2
    //   243: aload 5
    //   245: iconst_2
    //   246: invokespecial 1090	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   249: ifeq +42 -> 291
    //   252: aload_1
    //   253: iconst_2
    //   254: aload 5
    //   256: invokevirtual 298	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   259: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   262: ifeq +29 -> 291
    //   265: new 756	java/lang/StringBuilder
    //   268: astore_3
    //   269: aload_3
    //   270: invokespecial 757	java/lang/StringBuilder:<init>	()V
    //   273: aload_3
    //   274: ldc_w 1096
    //   277: invokevirtual 761	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: aload 5
    //   282: invokevirtual 1065	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   285: invokevirtual 766	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   288: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   291: new 279	java/io/File
    //   294: astore_3
    //   295: aload_3
    //   296: aload_0
    //   297: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   300: ldc_w 1098
    //   303: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   306: aload_3
    //   307: invokevirtual 1088	java/io/File:mkdir	()Z
    //   310: pop
    //   311: aload_3
    //   312: invokevirtual 283	java/io/File:isDirectory	()Z
    //   315: ifeq +73 -> 388
    //   318: aload_0
    //   319: aload_2
    //   320: aload_3
    //   321: iconst_1
    //   322: invokespecial 1090	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   325: ifeq +63 -> 388
    //   328: new 279	java/io/File
    //   331: astore 5
    //   333: aload 5
    //   335: aload_3
    //   336: ldc_w 288
    //   339: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   342: aload 5
    //   344: invokevirtual 294	java/io/File:createNewFile	()Z
    //   347: pop
    //   348: aload_1
    //   349: iconst_1
    //   350: aload_3
    //   351: invokevirtual 298	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   354: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   357: ifeq +31 -> 388
    //   360: new 756	java/lang/StringBuilder
    //   363: astore 5
    //   365: aload 5
    //   367: invokespecial 757	java/lang/StringBuilder:<init>	()V
    //   370: aload 5
    //   372: ldc_w 1100
    //   375: invokevirtual 761	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   378: aload_3
    //   379: invokevirtual 1065	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   382: invokevirtual 766	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   385: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   388: new 279	java/io/File
    //   391: astore_3
    //   392: aload_3
    //   393: aload_0
    //   394: getfield 236	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   397: ldc_w 1102
    //   400: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   403: aload_3
    //   404: invokevirtual 1088	java/io/File:mkdir	()Z
    //   407: pop
    //   408: aload_3
    //   409: invokevirtual 283	java/io/File:isDirectory	()Z
    //   412: ifeq +67 -> 479
    //   415: aload_0
    //   416: aload_2
    //   417: aload_3
    //   418: iconst_3
    //   419: invokespecial 1090	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   422: ifeq +57 -> 479
    //   425: new 279	java/io/File
    //   428: astore_2
    //   429: aload_2
    //   430: aload_3
    //   431: ldc_w 288
    //   434: invokespecial 291	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   437: aload_2
    //   438: invokevirtual 294	java/io/File:createNewFile	()Z
    //   441: pop
    //   442: aload_1
    //   443: iconst_3
    //   444: aload_3
    //   445: invokevirtual 298	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   448: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   451: ifeq +28 -> 479
    //   454: new 756	java/lang/StringBuilder
    //   457: astore_2
    //   458: aload_2
    //   459: invokespecial 757	java/lang/StringBuilder:<init>	()V
    //   462: aload_2
    //   463: ldc_w 1104
    //   466: invokevirtual 761	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   469: aload_3
    //   470: invokevirtual 1065	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   473: invokevirtual 766	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   476: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   479: invokestatic 1107	org/telegram/messenger/SharedConfig:checkSaveToGalleryFiles	()V
    //   482: aload_1
    //   483: areturn
    //   484: astore_3
    //   485: aload_3
    //   486: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   489: goto -465 -> 24
    //   492: astore_3
    //   493: aload_3
    //   494: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   497: goto -456 -> 41
    //   500: astore_3
    //   501: aload_3
    //   502: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   505: goto -295 -> 210
    //   508: astore_2
    //   509: aload_2
    //   510: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   513: goto -31 -> 482
    //   516: astore_3
    //   517: aload_3
    //   518: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   521: goto -230 -> 291
    //   524: astore_3
    //   525: aload_3
    //   526: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   529: goto -141 -> 388
    //   532: astore_2
    //   533: aload_2
    //   534: invokestatic 315	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   537: goto -58 -> 479
    //   540: getstatic 1060	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   543: ifeq -64 -> 479
    //   546: ldc_w 1109
    //   549: invokestatic 1068	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   552: goto -73 -> 479
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	555	0	this	ImageLoader
    //   7	476	1	localSparseArray	SparseArray
    //   11	452	2	localObject1	Object
    //   508	2	2	localException1	Exception
    //   532	2	2	localException2	Exception
    //   27	443	3	localObject2	Object
    //   484	2	3	localException3	Exception
    //   492	2	3	localException4	Exception
    //   500	2	3	localException5	Exception
    //   516	2	3	localException6	Exception
    //   524	2	3	localException7	Exception
    //   122	3	4	bool	boolean
    //   132	239	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   19	24	484	java/lang/Exception
    //   24	41	492	java/lang/Exception
    //   129	210	500	java/lang/Exception
    //   76	124	508	java/lang/Exception
    //   479	482	508	java/lang/Exception
    //   501	505	508	java/lang/Exception
    //   517	521	508	java/lang/Exception
    //   525	529	508	java/lang/Exception
    //   533	537	508	java/lang/Exception
    //   540	552	508	java/lang/Exception
    //   210	291	516	java/lang/Exception
    //   291	388	524	java/lang/Exception
    //   388	479	532	java/lang/Exception
  }
  
  public boolean decrementUseCount(String paramString)
  {
    boolean bool = true;
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null) {}
    for (;;)
    {
      return bool;
      if (localInteger.intValue() == 1)
      {
        this.bitmapUseCounts.remove(paramString);
      }
      else
      {
        this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() - 1));
        bool = false;
      }
    }
  }
  
  public Float getFileProgress(String paramString)
  {
    if (paramString == null) {}
    for (paramString = null;; paramString = (Float)this.fileProgresses.get(paramString)) {
      return paramString;
    }
  }
  
  public BitmapDrawable getImageFromMemory(String paramString)
  {
    return this.memCache.get(paramString);
  }
  
  public BitmapDrawable getImageFromMemory(TLObject paramTLObject, String paramString1, String paramString2)
  {
    if ((paramTLObject == null) && (paramString1 == null))
    {
      paramTLObject = null;
      return paramTLObject;
    }
    Object localObject = null;
    if (paramString1 != null) {
      paramString1 = Utilities.MD5(paramString1);
    }
    for (;;)
    {
      paramTLObject = paramString1;
      if (paramString2 != null) {
        paramTLObject = paramString1 + "@" + paramString2;
      }
      paramTLObject = this.memCache.get(paramTLObject);
      break;
      if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        paramTLObject = (TLRPC.FileLocation)paramTLObject;
        paramString1 = paramTLObject.volume_id + "_" + paramTLObject.local_id;
      }
      else if ((paramTLObject instanceof TLRPC.Document))
      {
        paramTLObject = (TLRPC.Document)paramTLObject;
        if (paramTLObject.version == 0) {
          paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id;
        } else {
          paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id + "_" + paramTLObject.version;
        }
      }
      else
      {
        paramString1 = (String)localObject;
        if ((paramTLObject instanceof TLRPC.TL_webDocument)) {
          paramString1 = Utilities.MD5(((TLRPC.TL_webDocument)paramTLObject).url);
        }
      }
    }
  }
  
  public void incrementUseCount(String paramString)
  {
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null) {
      this.bitmapUseCounts.put(paramString, Integer.valueOf(1));
    }
    for (;;)
    {
      return;
      this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
    }
  }
  
  public boolean isInCache(String paramString)
  {
    if (this.memCache.get(paramString) != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLoadingHttpFile(String paramString)
  {
    return this.httpFileLoadTasksByKeys.containsKey(paramString);
  }
  
  public void loadHttpFile(String paramString1, String paramString2, int paramInt)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (this.httpFileLoadTasksByKeys.containsKey(paramString1))) {}
    for (;;)
    {
      return;
      paramString2 = getHttpUrlExtension(paramString1, paramString2);
      File localFile = new File(FileLoader.getDirectory(4), Utilities.MD5(paramString1) + "_temp." + paramString2);
      localFile.delete();
      paramString2 = new HttpFileTask(paramString1, localFile, paramString2, paramInt);
      this.httpFileLoadTasks.add(paramString2);
      this.httpFileLoadTasksByKeys.put(paramString1, paramString2);
      runHttpFileLoadTasks(null, 0);
    }
  }
  
  public void loadImageForImageReceiver(ImageReceiver paramImageReceiver)
  {
    if (paramImageReceiver == null) {}
    do
    {
      do
      {
        return;
        i = 0;
        localObject1 = paramImageReceiver.getKey();
        j = i;
        if (localObject1 == null) {
          break;
        }
        localObject2 = this.memCache.get((String)localObject1);
        j = i;
        if (localObject2 == null) {
          break;
        }
        cancelLoadingForImageReceiver(paramImageReceiver, 0);
        paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject2, (String)localObject1, false, true);
        j = 1;
      } while (!paramImageReceiver.isForcePreview());
      k = 0;
      localObject2 = paramImageReceiver.getThumbKey();
      i = k;
      if (localObject2 == null) {
        break;
      }
      localObject1 = this.memCache.get((String)localObject2);
      i = k;
      if (localObject1 == null) {
        break;
      }
      paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject1, (String)localObject2, true, true);
      cancelLoadingForImageReceiver(paramImageReceiver, 1);
    } while ((j != 0) && (paramImageReceiver.isForcePreview()));
    int i = 1;
    TLRPC.FileLocation localFileLocation = paramImageReceiver.getThumbLocation();
    Object localObject3 = paramImageReceiver.getImageLocation();
    String str1 = paramImageReceiver.getHttpImageLocation();
    int m = 0;
    int k = 0;
    Object localObject4 = null;
    Object localObject5 = null;
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject8 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    String str2 = null;
    Object localObject9 = paramImageReceiver.getExt();
    Object localObject10 = localObject9;
    if (localObject9 == null) {
      localObject10 = "jpg";
    }
    Object localObject11;
    if (str1 != null)
    {
      localObject2 = Utilities.MD5(str1);
      localObject5 = (String)localObject2 + "." + getHttpUrlExtension(str1, "jpg");
      localObject11 = localObject3;
      label244:
      localObject1 = str2;
      if (localFileLocation != null)
      {
        localObject1 = localFileLocation.volume_id + "_" + localFileLocation.local_id;
        localObject8 = (String)localObject1 + "." + (String)localObject10;
      }
      str2 = paramImageReceiver.getFilter();
      localObject3 = paramImageReceiver.getThumbFilter();
      localObject9 = localObject2;
      if (localObject2 != null)
      {
        localObject9 = localObject2;
        if (str2 != null) {
          localObject9 = (String)localObject2 + "@" + str2;
        }
      }
      localObject2 = localObject1;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (localObject3 != null) {
          localObject2 = (String)localObject1 + "@" + (String)localObject3;
        }
      }
      if (str1 == null) {
        break label1078;
      }
      if (i == 0) {
        break label1072;
      }
    }
    label757:
    label843:
    label875:
    label915:
    label1046:
    label1058:
    label1066:
    label1072:
    for (int j = 2;; j = 1)
    {
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject2, (String)localObject8, (String)localObject10, localFileLocation, null, (String)localObject3, 0, 1, j);
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject9, (String)localObject5, (String)localObject10, null, str1, str2, 0, 1, 0);
      break;
      localObject11 = localObject3;
      if (localObject3 == null) {
        break label244;
      }
      if ((localObject3 instanceof TLRPC.FileLocation))
      {
        localObject11 = (TLRPC.FileLocation)localObject3;
        localObject8 = ((TLRPC.FileLocation)localObject11).volume_id + "_" + ((TLRPC.FileLocation)localObject11).local_id;
        localObject5 = (String)localObject8 + "." + (String)localObject10;
        if ((paramImageReceiver.getExt() == null) && (((TLRPC.FileLocation)localObject11).key == null))
        {
          localObject9 = localObject6;
          localObject2 = localObject5;
          localObject1 = localObject8;
          j = m;
          if (((TLRPC.FileLocation)localObject11).volume_id == -2147483648L)
          {
            localObject9 = localObject6;
            localObject2 = localObject5;
            localObject1 = localObject8;
            j = m;
            if (((TLRPC.FileLocation)localObject11).local_id >= 0) {}
          }
        }
        else
        {
          j = 1;
          localObject1 = localObject8;
          localObject2 = localObject5;
          localObject9 = localObject6;
        }
      }
      do
      {
        for (;;)
        {
          localObject8 = localObject9;
          localObject5 = localObject2;
          localObject11 = localObject3;
          localObject2 = localObject1;
          k = j;
          if (localObject3 != localFileLocation) {
            break;
          }
          localObject11 = null;
          localObject2 = null;
          localObject5 = null;
          localObject8 = localObject9;
          k = j;
          break;
          if (!(localObject3 instanceof TLRPC.TL_webDocument)) {
            break label757;
          }
          localObject2 = (TLRPC.TL_webDocument)localObject3;
          localObject5 = FileLoader.getExtensionByMime(((TLRPC.TL_webDocument)localObject2).mime_type);
          localObject1 = Utilities.MD5(((TLRPC.TL_webDocument)localObject2).url);
          localObject2 = (String)localObject1 + "." + getHttpUrlExtension(((TLRPC.TL_webDocument)localObject2).url, (String)localObject5);
          localObject9 = localObject6;
          j = m;
        }
        localObject9 = localObject6;
        localObject2 = localObject4;
        j = m;
      } while (!(localObject3 instanceof TLRPC.Document));
      localObject8 = (TLRPC.Document)localObject3;
      if ((((TLRPC.Document)localObject8).id == 0L) || (((TLRPC.Document)localObject8).dc_id == 0)) {
        break;
      }
      if (((TLRPC.Document)localObject8).version == 0)
      {
        localObject1 = ((TLRPC.Document)localObject8).dc_id + "_" + ((TLRPC.Document)localObject8).id;
        localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject8);
        if (localObject2 != null)
        {
          j = ((String)localObject2).lastIndexOf('.');
          if (j != -1) {
            break label1046;
          }
        }
        localObject5 = "";
        localObject2 = localObject5;
        if (((String)localObject5).length() <= 1)
        {
          if ((((TLRPC.Document)localObject8).mime_type == null) || (!((TLRPC.Document)localObject8).mime_type.equals("video/mp4"))) {
            break label1058;
          }
          localObject2 = ".mp4";
        }
        localObject2 = (String)localObject1 + (String)localObject2;
        localObject9 = localObject7;
        if (0 != 0) {
          localObject9 = null + "." + (String)localObject10;
        }
        if ((MessageObject.isGifDocument((TLRPC.Document)localObject8)) || (MessageObject.isRoundVideoDocument((TLRPC.Document)localObject3))) {
          break label1066;
        }
      }
      for (j = 1;; j = 0)
      {
        break;
        localObject1 = ((TLRPC.Document)localObject8).dc_id + "_" + ((TLRPC.Document)localObject8).id + "_" + ((TLRPC.Document)localObject8).version;
        break label843;
        localObject5 = ((String)localObject2).substring(j);
        break label875;
        localObject2 = "";
        break label915;
      }
    }
    label1078:
    m = paramImageReceiver.getCacheType();
    j = m;
    if (m == 0)
    {
      j = m;
      if (k != 0) {
        j = 1;
      }
    }
    if (j == 0)
    {
      k = 1;
      label1113:
      if (i == 0) {
        break label1172;
      }
    }
    label1172:
    for (i = 2;; i = 1)
    {
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject2, (String)localObject8, (String)localObject10, localFileLocation, null, (String)localObject3, 0, k, i);
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject9, (String)localObject5, (String)localObject10, (TLObject)localObject11, null, str2, paramImageReceiver.getSize(), j, 0);
      break;
      k = j;
      break label1113;
    }
  }
  
  public void putImageToCache(BitmapDrawable paramBitmapDrawable, String paramString)
  {
    this.memCache.put(paramString, paramBitmapDrawable);
  }
  
  public void removeImage(String paramString)
  {
    this.bitmapUseCounts.remove(paramString);
    this.memCache.remove(paramString);
  }
  
  public void replaceImageInCache(final String paramString1, final String paramString2, final TLRPC.FileLocation paramFileLocation, boolean paramBoolean)
  {
    if (paramBoolean) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.replaceImageInCacheInternal(paramString1, paramString2, paramFileLocation);
        }
      });
    }
    for (;;)
    {
      return;
      replaceImageInCacheInternal(paramString1, paramString2, paramFileLocation);
    }
  }
  
  private class CacheImage
  {
    protected boolean animatedFile;
    protected ImageLoader.CacheOutTask cacheTask;
    protected int currentAccount;
    protected File encryptionKeyPath;
    protected String ext;
    protected String filter;
    protected ArrayList<String> filters = new ArrayList();
    protected File finalFilePath;
    protected ImageLoader.HttpImageTask httpTask;
    protected String httpUrl;
    protected ArrayList<ImageReceiver> imageReceiverArray = new ArrayList();
    protected String key;
    protected ArrayList<String> keys = new ArrayList();
    protected TLObject location;
    protected boolean selfThumb;
    protected File tempFilePath;
    protected ArrayList<Boolean> thumbs = new ArrayList();
    protected String url;
    
    private CacheImage() {}
    
    public void addImageReceiver(ImageReceiver paramImageReceiver, String paramString1, String paramString2, boolean paramBoolean)
    {
      if (this.imageReceiverArray.contains(paramImageReceiver)) {}
      for (;;)
      {
        return;
        this.imageReceiverArray.add(paramImageReceiver);
        this.keys.add(paramString1);
        this.filters.add(paramString2);
        this.thumbs.add(Boolean.valueOf(paramBoolean));
        ImageLoader.this.imageLoadingByTag.put(paramImageReceiver.getTag(paramBoolean), this);
      }
    }
    
    public void removeImageReceiver(ImageReceiver paramImageReceiver)
    {
      Boolean localBoolean = Boolean.valueOf(this.selfThumb);
      int j;
      for (int i = 0; i < this.imageReceiverArray.size(); i = j + 1)
      {
        ImageReceiver localImageReceiver = (ImageReceiver)this.imageReceiverArray.get(i);
        if (localImageReceiver != null)
        {
          j = i;
          if (localImageReceiver != paramImageReceiver) {}
        }
        else
        {
          this.imageReceiverArray.remove(i);
          this.keys.remove(i);
          this.filters.remove(i);
          localBoolean = (Boolean)this.thumbs.remove(i);
          if (localImageReceiver != null) {
            ImageLoader.this.imageLoadingByTag.remove(localImageReceiver.getTag(localBoolean.booleanValue()));
          }
          j = i - 1;
        }
      }
      if (this.imageReceiverArray.size() == 0)
      {
        for (i = 0; i < this.imageReceiverArray.size(); i++) {
          ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver)this.imageReceiverArray.get(i)).getTag(localBoolean.booleanValue()));
        }
        this.imageReceiverArray.clear();
        if ((this.location != null) && (!ImageLoader.this.forceLoadingImages.containsKey(this.key)))
        {
          if (!(this.location instanceof TLRPC.FileLocation)) {
            break label364;
          }
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.FileLocation)this.location, this.ext);
        }
        if (this.cacheTask != null)
        {
          if (!this.selfThumb) {
            break label424;
          }
          ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
        }
      }
      for (;;)
      {
        this.cacheTask.cancel();
        this.cacheTask = null;
        if (this.httpTask != null)
        {
          ImageLoader.this.httpTasks.remove(this.httpTask);
          this.httpTask.cancel(true);
          this.httpTask = null;
        }
        if (this.url != null) {
          ImageLoader.this.imageLoadingByUrl.remove(this.url);
        }
        if (this.key != null) {
          ImageLoader.this.imageLoadingByKeys.remove(this.key);
        }
        return;
        label364:
        if ((this.location instanceof TLRPC.Document))
        {
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)this.location);
          break;
        }
        if (!(this.location instanceof TLRPC.TL_webDocument)) {
          break;
        }
        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.TL_webDocument)this.location);
        break;
        label424:
        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
      }
    }
    
    public void replaceImageReceiver(ImageReceiver paramImageReceiver, String paramString1, String paramString2, boolean paramBoolean)
    {
      int i = this.imageReceiverArray.indexOf(paramImageReceiver);
      if (i == -1) {}
      for (;;)
      {
        return;
        int j = i;
        if (((Boolean)this.thumbs.get(i)).booleanValue() != paramBoolean)
        {
          j = this.imageReceiverArray.subList(i + 1, this.imageReceiverArray.size()).indexOf(paramImageReceiver);
          if (j == -1) {}
        }
        else
        {
          this.keys.set(j, paramString1);
          this.filters.set(j, paramString2);
        }
      }
    }
    
    public void setImageAndClear(final BitmapDrawable paramBitmapDrawable)
    {
      if (paramBitmapDrawable != null) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int j;
            if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
            {
              int i = 0;
              AnimatedFileDrawable localAnimatedFileDrawable1 = (AnimatedFileDrawable)paramBitmapDrawable;
              j = 0;
              if (j < this.val$finalImageReceiverArray.size())
              {
                ImageReceiver localImageReceiver = (ImageReceiver)this.val$finalImageReceiverArray.get(j);
                if (j == 0) {}
                for (AnimatedFileDrawable localAnimatedFileDrawable2 = localAnimatedFileDrawable1;; localAnimatedFileDrawable2 = localAnimatedFileDrawable1.makeCopy())
                {
                  if (localImageReceiver.setImageBitmapByKey(localAnimatedFileDrawable2, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.selfThumb, false)) {
                    i = 1;
                  }
                  j++;
                  break;
                }
              }
              if (i == 0) {
                ((AnimatedFileDrawable)paramBitmapDrawable).recycle();
              }
            }
            for (;;)
            {
              return;
              for (j = 0; j < this.val$finalImageReceiverArray.size(); j++) {
                ((ImageReceiver)this.val$finalImageReceiverArray.get(j)).setImageBitmapByKey(paramBitmapDrawable, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.selfThumb, false);
              }
            }
          }
        });
      }
      for (int i = 0; i < this.imageReceiverArray.size(); i++)
      {
        paramBitmapDrawable = (ImageReceiver)this.imageReceiverArray.get(i);
        ImageLoader.this.imageLoadingByTag.remove(paramBitmapDrawable.getTag(this.selfThumb));
      }
      this.imageReceiverArray.clear();
      if (this.url != null) {
        ImageLoader.this.imageLoadingByUrl.remove(this.url);
      }
      if (this.key != null) {
        ImageLoader.this.imageLoadingByKeys.remove(this.key);
      }
    }
  }
  
  private class CacheOutTask
    implements Runnable
  {
    private ImageLoader.CacheImage cacheImage;
    private boolean isCancelled;
    private Thread runningThread;
    private final Object sync = new Object();
    
    public CacheOutTask(ImageLoader.CacheImage paramCacheImage)
    {
      this.cacheImage = paramCacheImage;
    }
    
    private void onPostExecute(final BitmapDrawable paramBitmapDrawable)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          final BitmapDrawable localBitmapDrawable = null;
          if ((paramBitmapDrawable instanceof AnimatedFileDrawable)) {
            localBitmapDrawable = paramBitmapDrawable;
          }
          for (;;)
          {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                ImageLoader.CacheOutTask.this.cacheImage.setImageAndClear(localBitmapDrawable);
              }
            });
            return;
            if (paramBitmapDrawable != null)
            {
              localBitmapDrawable = ImageLoader.this.memCache.get(ImageLoader.CacheOutTask.this.cacheImage.key);
              if (localBitmapDrawable == null)
              {
                ImageLoader.this.memCache.put(ImageLoader.CacheOutTask.this.cacheImage.key, paramBitmapDrawable);
                localBitmapDrawable = paramBitmapDrawable;
              }
              else
              {
                paramBitmapDrawable.getBitmap().recycle();
              }
            }
          }
        }
      });
    }
    
    public void cancel()
    {
      try
      {
        synchronized (this.sync)
        {
          this.isCancelled = true;
          if (this.runningThread != null) {
            this.runningThread.interrupt();
          }
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   4: astore_1
      //   5: aload_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokestatic 67	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   11: putfield 55	org/telegram/messenger/ImageLoader$CacheOutTask:runningThread	Ljava/lang/Thread;
      //   14: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   17: pop
      //   18: aload_0
      //   19: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   22: ifeq +6 -> 28
      //   25: aload_1
      //   26: monitorexit
      //   27: return
      //   28: aload_1
      //   29: monitorexit
      //   30: aload_0
      //   31: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   34: getfield 76	org/telegram/messenger/ImageLoader$CacheImage:animatedFile	Z
      //   37: ifeq +96 -> 133
      //   40: aload_0
      //   41: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   44: astore_1
      //   45: aload_1
      //   46: monitorenter
      //   47: aload_0
      //   48: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   51: ifeq +18 -> 69
      //   54: aload_1
      //   55: monitorexit
      //   56: goto -29 -> 27
      //   59: astore_2
      //   60: aload_1
      //   61: monitorexit
      //   62: aload_2
      //   63: athrow
      //   64: astore_2
      //   65: aload_1
      //   66: monitorexit
      //   67: aload_2
      //   68: athrow
      //   69: aload_1
      //   70: monitorexit
      //   71: aload_0
      //   72: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   75: getfield 80	org/telegram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   78: astore_1
      //   79: aload_0
      //   80: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   83: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   86: ifnull +42 -> 128
      //   89: aload_0
      //   90: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   93: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   96: ldc 86
      //   98: invokevirtual 92	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   101: ifeq +27 -> 128
      //   104: iconst_1
      //   105: istore_3
      //   106: new 94	org/telegram/ui/Components/AnimatedFileDrawable
      //   109: dup
      //   110: aload_1
      //   111: iload_3
      //   112: invokespecial 97	org/telegram/ui/Components/AnimatedFileDrawable:<init>	(Ljava/io/File;Z)V
      //   115: astore_1
      //   116: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   119: pop
      //   120: aload_0
      //   121: aload_1
      //   122: invokespecial 99	org/telegram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   125: goto -98 -> 27
      //   128: iconst_0
      //   129: istore_3
      //   130: goto -24 -> 106
      //   133: aconst_null
      //   134: astore 4
      //   136: aconst_null
      //   137: astore 5
      //   139: aconst_null
      //   140: astore 6
      //   142: iconst_0
      //   143: istore 7
      //   145: iconst_0
      //   146: istore 8
      //   148: aload_0
      //   149: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   152: getfield 80	org/telegram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   155: astore 9
      //   157: aload_0
      //   158: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   161: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   164: ifnull +315 -> 479
      //   167: aload 9
      //   169: ifnull +310 -> 479
      //   172: aload 9
      //   174: invokevirtual 108	java/io/File:getAbsolutePath	()Ljava/lang/String;
      //   177: ldc 110
      //   179: invokevirtual 114	java/lang/String:endsWith	(Ljava/lang/String;)Z
      //   182: ifeq +297 -> 479
      //   185: iconst_1
      //   186: istore 10
      //   188: iconst_1
      //   189: istore 11
      //   191: iconst_0
      //   192: istore 12
      //   194: iconst_0
      //   195: istore 13
      //   197: iconst_0
      //   198: istore 14
      //   200: iconst_0
      //   201: istore 15
      //   203: getstatic 120	android/os/Build$VERSION:SDK_INT	I
      //   206: bipush 19
      //   208: if_icmpge +154 -> 362
      //   211: aconst_null
      //   212: astore 16
      //   214: aconst_null
      //   215: astore 17
      //   217: aload 16
      //   219: astore_1
      //   220: new 122	java/io/RandomAccessFile
      //   223: astore_2
      //   224: aload 16
      //   226: astore_1
      //   227: aload_2
      //   228: aload 9
      //   230: ldc 124
      //   232: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   235: iload 14
      //   237: istore 13
      //   239: aload_0
      //   240: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   243: getfield 130	org/telegram/messenger/ImageLoader$CacheImage:selfThumb	Z
      //   246: ifeq +239 -> 485
      //   249: iload 14
      //   251: istore 13
      //   253: invokestatic 134	org/telegram/messenger/ImageLoader:access$1300	()[B
      //   256: astore_1
      //   257: iload 14
      //   259: istore 13
      //   261: aload_2
      //   262: aload_1
      //   263: iconst_0
      //   264: aload_1
      //   265: arraylength
      //   266: invokevirtual 138	java/io/RandomAccessFile:readFully	([BII)V
      //   269: iload 14
      //   271: istore 13
      //   273: new 88	java/lang/String
      //   276: astore 16
      //   278: iload 14
      //   280: istore 13
      //   282: aload 16
      //   284: aload_1
      //   285: invokespecial 141	java/lang/String:<init>	([B)V
      //   288: iload 14
      //   290: istore 13
      //   292: aload 16
      //   294: invokevirtual 144	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   297: invokevirtual 144	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   300: astore_1
      //   301: iload 15
      //   303: istore 18
      //   305: iload 14
      //   307: istore 13
      //   309: aload_1
      //   310: ldc -110
      //   312: invokevirtual 149	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   315: ifeq +23 -> 338
      //   318: iload 15
      //   320: istore 18
      //   322: iload 14
      //   324: istore 13
      //   326: aload_1
      //   327: ldc -105
      //   329: invokevirtual 114	java/lang/String:endsWith	(Ljava/lang/String;)Z
      //   332: ifeq +6 -> 338
      //   335: iconst_1
      //   336: istore 18
      //   338: iload 18
      //   340: istore 13
      //   342: aload_2
      //   343: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   346: iload 18
      //   348: istore 12
      //   350: aload_2
      //   351: ifnull +11 -> 362
      //   354: aload_2
      //   355: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   358: iload 18
      //   360: istore 12
      //   362: aload_0
      //   363: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   366: getfield 130	org/telegram/messenger/ImageLoader$CacheImage:selfThumb	Z
      //   369: ifeq +951 -> 1320
      //   372: iconst_0
      //   373: istore 13
      //   375: iload 13
      //   377: istore 18
      //   379: aload_0
      //   380: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   383: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   386: ifnull +21 -> 407
      //   389: aload_0
      //   390: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   393: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   396: ldc -100
      //   398: invokevirtual 160	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   401: ifeq +175 -> 576
      //   404: iconst_3
      //   405: istore 18
      //   407: aload_0
      //   408: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   411: invokestatic 166	java/lang/System:currentTimeMillis	()J
      //   414: invokestatic 170	org/telegram/messenger/ImageLoader:access$1502	(Lorg/telegram/messenger/ImageLoader;J)J
      //   417: pop2
      //   418: aload_0
      //   419: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   422: astore_1
      //   423: aload_1
      //   424: monitorenter
      //   425: aload_0
      //   426: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   429: ifeq +193 -> 622
      //   432: aload_1
      //   433: monitorexit
      //   434: goto -407 -> 27
      //   437: astore_2
      //   438: aload_1
      //   439: monitorexit
      //   440: aload_2
      //   441: athrow
      //   442: astore_1
      //   443: aconst_null
      //   444: astore_2
      //   445: aload_1
      //   446: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   449: aload_2
      //   450: astore 16
      //   452: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   455: pop
      //   456: aload 16
      //   458: ifnull +2631 -> 3089
      //   461: new 178	android/graphics/drawable/BitmapDrawable
      //   464: dup
      //   465: aload 16
      //   467: invokespecial 181	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
      //   470: astore_1
      //   471: aload_0
      //   472: aload_1
      //   473: invokespecial 99	org/telegram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   476: goto -449 -> 27
      //   479: iconst_0
      //   480: istore 10
      //   482: goto -294 -> 188
      //   485: iload 14
      //   487: istore 13
      //   489: invokestatic 184	org/telegram/messenger/ImageLoader:access$1400	()[B
      //   492: astore_1
      //   493: goto -236 -> 257
      //   496: astore_1
      //   497: aload_1
      //   498: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   501: iload 18
      //   503: istore 12
      //   505: goto -143 -> 362
      //   508: astore 16
      //   510: aload 17
      //   512: astore_2
      //   513: aload_2
      //   514: astore_1
      //   515: aload 16
      //   517: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   520: iload 13
      //   522: istore 12
      //   524: aload_2
      //   525: ifnull -163 -> 362
      //   528: aload_2
      //   529: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   532: iload 13
      //   534: istore 12
      //   536: goto -174 -> 362
      //   539: astore_1
      //   540: aload_1
      //   541: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   544: iload 13
      //   546: istore 12
      //   548: goto -186 -> 362
      //   551: astore 16
      //   553: aload_1
      //   554: astore_2
      //   555: aload 16
      //   557: astore_1
      //   558: aload_2
      //   559: ifnull +7 -> 566
      //   562: aload_2
      //   563: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   566: aload_1
      //   567: athrow
      //   568: astore_2
      //   569: aload_2
      //   570: invokestatic 176	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   573: goto -7 -> 566
      //   576: aload_0
      //   577: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   580: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   583: ldc -70
      //   585: invokevirtual 160	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   588: ifeq +9 -> 597
      //   591: iconst_2
      //   592: istore 18
      //   594: goto -187 -> 407
      //   597: iload 13
      //   599: istore 18
      //   601: aload_0
      //   602: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   605: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   608: ldc -68
      //   610: invokevirtual 160	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   613: ifeq -206 -> 407
      //   616: iconst_1
      //   617: istore 18
      //   619: goto -212 -> 407
      //   622: aload_1
      //   623: monitorexit
      //   624: new 190	android/graphics/BitmapFactory$Options
      //   627: astore 17
      //   629: aload 17
      //   631: invokespecial 191	android/graphics/BitmapFactory$Options:<init>	()V
      //   634: aload 17
      //   636: iconst_1
      //   637: putfield 194	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   640: getstatic 120	android/os/Build$VERSION:SDK_INT	I
      //   643: bipush 21
      //   645: if_icmpge +9 -> 654
      //   648: aload 17
      //   650: iconst_1
      //   651: putfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   654: iload 12
      //   656: ifeq +173 -> 829
      //   659: new 122	java/io/RandomAccessFile
      //   662: astore 19
      //   664: aload 19
      //   666: aload 9
      //   668: ldc 124
      //   670: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   673: aload 19
      //   675: invokevirtual 201	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   678: getstatic 207	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   681: lconst_0
      //   682: aload 9
      //   684: invokevirtual 210	java/io/File:length	()J
      //   687: invokevirtual 216	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   690: astore 16
      //   692: new 190	android/graphics/BitmapFactory$Options
      //   695: astore_1
      //   696: aload_1
      //   697: invokespecial 191	android/graphics/BitmapFactory$Options:<init>	()V
      //   700: aload_1
      //   701: iconst_1
      //   702: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   705: aconst_null
      //   706: aload 16
      //   708: aload 16
      //   710: invokevirtual 225	java/nio/ByteBuffer:limit	()I
      //   713: aload_1
      //   714: iconst_1
      //   715: invokestatic 231	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   718: pop
      //   719: aload_1
      //   720: getfield 234	android/graphics/BitmapFactory$Options:outWidth	I
      //   723: aload_1
      //   724: getfield 237	android/graphics/BitmapFactory$Options:outHeight	I
      //   727: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   730: invokestatic 249	org/telegram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   733: astore_1
      //   734: aload_1
      //   735: astore_2
      //   736: aload 16
      //   738: invokevirtual 225	java/nio/ByteBuffer:limit	()I
      //   741: istore 13
      //   743: aload_1
      //   744: astore_2
      //   745: aload 17
      //   747: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   750: ifne +74 -> 824
      //   753: iconst_1
      //   754: istore_3
      //   755: aload_1
      //   756: astore_2
      //   757: aload_1
      //   758: aload 16
      //   760: iload 13
      //   762: aconst_null
      //   763: iload_3
      //   764: invokestatic 231	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   767: pop
      //   768: aload_1
      //   769: astore_2
      //   770: aload 19
      //   772: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   775: aload_1
      //   776: ifnonnull +226 -> 1002
      //   779: aload_1
      //   780: astore_2
      //   781: aload 9
      //   783: invokevirtual 210	java/io/File:length	()J
      //   786: lconst_0
      //   787: lcmp
      //   788: ifeq +18 -> 806
      //   791: aload_1
      //   792: astore 16
      //   794: aload_1
      //   795: astore_2
      //   796: aload_0
      //   797: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   800: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   803: ifnonnull -351 -> 452
      //   806: aload_1
      //   807: astore_2
      //   808: aload 9
      //   810: invokevirtual 252	java/io/File:delete	()Z
      //   813: pop
      //   814: aload_1
      //   815: astore 16
      //   817: goto -365 -> 452
      //   820: astore_1
      //   821: goto -376 -> 445
      //   824: iconst_0
      //   825: istore_3
      //   826: goto -71 -> 755
      //   829: aload 17
      //   831: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   834: ifeq +111 -> 945
      //   837: new 122	java/io/RandomAccessFile
      //   840: astore 16
      //   842: aload 16
      //   844: aload 9
      //   846: ldc 124
      //   848: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   851: aload 16
      //   853: invokevirtual 253	java/io/RandomAccessFile:length	()J
      //   856: l2i
      //   857: istore 13
      //   859: invokestatic 256	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   862: ifnull +78 -> 940
      //   865: invokestatic 256	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   868: arraylength
      //   869: iload 13
      //   871: if_icmplt +69 -> 940
      //   874: invokestatic 256	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   877: astore_1
      //   878: aload_1
      //   879: astore_2
      //   880: aload_1
      //   881: ifnonnull +13 -> 894
      //   884: iload 13
      //   886: newarray <illegal type>
      //   888: astore_2
      //   889: aload_2
      //   890: invokestatic 260	org/telegram/messenger/ImageLoader:access$1602	([B)[B
      //   893: pop
      //   894: aload 16
      //   896: aload_2
      //   897: iconst_0
      //   898: iload 13
      //   900: invokevirtual 138	java/io/RandomAccessFile:readFully	([BII)V
      //   903: aload 16
      //   905: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   908: iload 10
      //   910: ifeq +17 -> 927
      //   913: aload_2
      //   914: iconst_0
      //   915: iload 13
      //   917: aload_0
      //   918: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   921: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   924: invokestatic 266	org/telegram/messenger/secretmedia/EncryptedFileInputStream:decryptBytesWithKeyFile	([BIILjava/io/File;)V
      //   927: aload_2
      //   928: iconst_0
      //   929: iload 13
      //   931: aload 17
      //   933: invokestatic 272	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   936: astore_1
      //   937: goto -162 -> 775
      //   940: aconst_null
      //   941: astore_1
      //   942: goto -64 -> 878
      //   945: iload 10
      //   947: ifeq +41 -> 988
      //   950: new 262	org/telegram/messenger/secretmedia/EncryptedFileInputStream
      //   953: astore 16
      //   955: aload 16
      //   957: aload 9
      //   959: aload_0
      //   960: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   963: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   966: invokespecial 275	org/telegram/messenger/secretmedia/EncryptedFileInputStream:<init>	(Ljava/io/File;Ljava/io/File;)V
      //   969: aload 16
      //   971: aconst_null
      //   972: aload 17
      //   974: invokestatic 279	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   977: astore_1
      //   978: aload_1
      //   979: astore_2
      //   980: aload 16
      //   982: invokevirtual 282	java/io/FileInputStream:close	()V
      //   985: goto -210 -> 775
      //   988: new 281	java/io/FileInputStream
      //   991: dup
      //   992: aload 9
      //   994: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   997: astore 16
      //   999: goto -30 -> 969
      //   1002: iload 18
      //   1004: iconst_1
      //   1005: if_icmpne +64 -> 1069
      //   1008: aload_1
      //   1009: astore 16
      //   1011: aload_1
      //   1012: astore_2
      //   1013: aload_1
      //   1014: invokevirtual 291	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1017: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1020: if_acmpne -568 -> 452
      //   1023: aload_1
      //   1024: astore_2
      //   1025: aload 17
      //   1027: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1030: ifeq +33 -> 1063
      //   1033: iconst_0
      //   1034: istore 18
      //   1036: aload_1
      //   1037: astore_2
      //   1038: aload_1
      //   1039: iconst_3
      //   1040: iload 18
      //   1042: aload_1
      //   1043: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   1046: aload_1
      //   1047: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   1050: aload_1
      //   1051: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   1054: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1057: aload_1
      //   1058: astore 16
      //   1060: goto -608 -> 452
      //   1063: iconst_1
      //   1064: istore 18
      //   1066: goto -30 -> 1036
      //   1069: iload 18
      //   1071: iconst_2
      //   1072: if_icmpne +64 -> 1136
      //   1075: aload_1
      //   1076: astore 16
      //   1078: aload_1
      //   1079: astore_2
      //   1080: aload_1
      //   1081: invokevirtual 291	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1084: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1087: if_acmpne -635 -> 452
      //   1090: aload_1
      //   1091: astore_2
      //   1092: aload 17
      //   1094: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1097: ifeq +33 -> 1130
      //   1100: iconst_0
      //   1101: istore 18
      //   1103: aload_1
      //   1104: astore_2
      //   1105: aload_1
      //   1106: iconst_1
      //   1107: iload 18
      //   1109: aload_1
      //   1110: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   1113: aload_1
      //   1114: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   1117: aload_1
      //   1118: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   1121: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1124: aload_1
      //   1125: astore 16
      //   1127: goto -675 -> 452
      //   1130: iconst_1
      //   1131: istore 18
      //   1133: goto -30 -> 1103
      //   1136: iload 18
      //   1138: iconst_3
      //   1139: if_icmpne +147 -> 1286
      //   1142: aload_1
      //   1143: astore 16
      //   1145: aload_1
      //   1146: astore_2
      //   1147: aload_1
      //   1148: invokevirtual 291	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1151: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1154: if_acmpne -702 -> 452
      //   1157: aload_1
      //   1158: astore_2
      //   1159: aload 17
      //   1161: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1164: ifeq +104 -> 1268
      //   1167: iconst_0
      //   1168: istore 18
      //   1170: aload_1
      //   1171: astore_2
      //   1172: aload_1
      //   1173: bipush 7
      //   1175: iload 18
      //   1177: aload_1
      //   1178: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   1181: aload_1
      //   1182: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   1185: aload_1
      //   1186: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   1189: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1192: aload_1
      //   1193: astore_2
      //   1194: aload 17
      //   1196: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1199: ifeq +75 -> 1274
      //   1202: iconst_0
      //   1203: istore 18
      //   1205: aload_1
      //   1206: astore_2
      //   1207: aload_1
      //   1208: bipush 7
      //   1210: iload 18
      //   1212: aload_1
      //   1213: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   1216: aload_1
      //   1217: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   1220: aload_1
      //   1221: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   1224: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1227: aload_1
      //   1228: astore_2
      //   1229: aload 17
      //   1231: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1234: ifeq +46 -> 1280
      //   1237: iconst_0
      //   1238: istore 18
      //   1240: aload_1
      //   1241: astore_2
      //   1242: aload_1
      //   1243: bipush 7
      //   1245: iload 18
      //   1247: aload_1
      //   1248: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   1251: aload_1
      //   1252: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   1255: aload_1
      //   1256: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   1259: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1262: aload_1
      //   1263: astore 16
      //   1265: goto -813 -> 452
      //   1268: iconst_1
      //   1269: istore 18
      //   1271: goto -101 -> 1170
      //   1274: iconst_1
      //   1275: istore 18
      //   1277: goto -72 -> 1205
      //   1280: iconst_1
      //   1281: istore 18
      //   1283: goto -43 -> 1240
      //   1286: aload_1
      //   1287: astore 16
      //   1289: iload 18
      //   1291: ifne -839 -> 452
      //   1294: aload_1
      //   1295: astore 16
      //   1297: aload_1
      //   1298: astore_2
      //   1299: aload 17
      //   1301: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1304: ifeq -852 -> 452
      //   1307: aload_1
      //   1308: astore_2
      //   1309: aload_1
      //   1310: invokestatic 308	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   1313: pop
      //   1314: aload_1
      //   1315: astore 16
      //   1317: goto -865 -> 452
      //   1320: aconst_null
      //   1321: astore_1
      //   1322: aconst_null
      //   1323: astore_2
      //   1324: iload 11
      //   1326: istore 18
      //   1328: aload 4
      //   1330: astore 17
      //   1332: iload 8
      //   1334: istore 15
      //   1336: aload_1
      //   1337: astore 19
      //   1339: aload_0
      //   1340: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1343: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1346: ifnull +93 -> 1439
      //   1349: aload_0
      //   1350: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1353: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1356: ldc_w 313
      //   1359: invokevirtual 149	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1362: ifeq +180 -> 1542
      //   1365: aload_0
      //   1366: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1369: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1372: ldc_w 315
      //   1375: bipush 8
      //   1377: invokevirtual 319	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1380: istore 18
      //   1382: aload 6
      //   1384: astore 17
      //   1386: aload_2
      //   1387: astore_1
      //   1388: iload 18
      //   1390: iflt +40 -> 1430
      //   1393: aload_0
      //   1394: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1397: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1400: bipush 8
      //   1402: iload 18
      //   1404: invokevirtual 323	java/lang/String:substring	(II)Ljava/lang/String;
      //   1407: invokestatic 329	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1410: invokestatic 333	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1413: astore 17
      //   1415: aload_0
      //   1416: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1419: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1422: iload 18
      //   1424: iconst_1
      //   1425: iadd
      //   1426: invokevirtual 336	java/lang/String:substring	(I)Ljava/lang/String;
      //   1429: astore_1
      //   1430: iconst_0
      //   1431: istore 15
      //   1433: iconst_0
      //   1434: istore 18
      //   1436: aload_1
      //   1437: astore 19
      //   1439: bipush 20
      //   1441: istore 13
      //   1443: aload 17
      //   1445: ifnull +6 -> 1451
      //   1448: iconst_0
      //   1449: istore 13
      //   1451: iload 13
      //   1453: ifeq +47 -> 1500
      //   1456: aload_0
      //   1457: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1460: invokestatic 340	org/telegram/messenger/ImageLoader:access$1500	(Lorg/telegram/messenger/ImageLoader;)J
      //   1463: lconst_0
      //   1464: lcmp
      //   1465: ifeq +35 -> 1500
      //   1468: aload_0
      //   1469: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1472: invokestatic 340	org/telegram/messenger/ImageLoader:access$1500	(Lorg/telegram/messenger/ImageLoader;)J
      //   1475: invokestatic 166	java/lang/System:currentTimeMillis	()J
      //   1478: iload 13
      //   1480: i2l
      //   1481: lsub
      //   1482: lcmp
      //   1483: ifle +17 -> 1500
      //   1486: getstatic 120	android/os/Build$VERSION:SDK_INT	I
      //   1489: bipush 21
      //   1491: if_icmpge +9 -> 1500
      //   1494: iload 13
      //   1496: i2l
      //   1497: invokestatic 344	java/lang/Thread:sleep	(J)V
      //   1500: aload_0
      //   1501: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1504: invokestatic 166	java/lang/System:currentTimeMillis	()J
      //   1507: invokestatic 170	org/telegram/messenger/ImageLoader:access$1502	(Lorg/telegram/messenger/ImageLoader;J)J
      //   1510: pop2
      //   1511: aload_0
      //   1512: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1515: astore_1
      //   1516: aload_1
      //   1517: monitorenter
      //   1518: aload_0
      //   1519: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1522: ifeq +150 -> 1672
      //   1525: aload_1
      //   1526: monitorexit
      //   1527: goto -1500 -> 27
      //   1530: astore_2
      //   1531: aload_1
      //   1532: monitorexit
      //   1533: aload_2
      //   1534: athrow
      //   1535: astore_1
      //   1536: aconst_null
      //   1537: astore 16
      //   1539: goto -1087 -> 452
      //   1542: aload_0
      //   1543: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1546: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1549: ldc_w 346
      //   1552: invokevirtual 149	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1555: ifeq +67 -> 1622
      //   1558: aload_0
      //   1559: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1562: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1565: ldc_w 315
      //   1568: bipush 9
      //   1570: invokevirtual 319	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1573: istore 18
      //   1575: aload 5
      //   1577: astore 17
      //   1579: iload 7
      //   1581: istore 15
      //   1583: iload 18
      //   1585: iflt +28 -> 1613
      //   1588: aload_0
      //   1589: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1592: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1595: bipush 9
      //   1597: iload 18
      //   1599: invokevirtual 323	java/lang/String:substring	(II)Ljava/lang/String;
      //   1602: invokestatic 329	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1605: invokestatic 333	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1608: astore 17
      //   1610: iconst_1
      //   1611: istore 15
      //   1613: iconst_0
      //   1614: istore 18
      //   1616: aload_1
      //   1617: astore 19
      //   1619: goto -180 -> 1439
      //   1622: aload_0
      //   1623: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1626: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1629: ldc_w 348
      //   1632: invokevirtual 149	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1635: istore_3
      //   1636: iload 11
      //   1638: istore 18
      //   1640: aload 4
      //   1642: astore 17
      //   1644: iload 8
      //   1646: istore 15
      //   1648: aload_1
      //   1649: astore 19
      //   1651: iload_3
      //   1652: ifne -213 -> 1439
      //   1655: iconst_0
      //   1656: istore 18
      //   1658: aload 4
      //   1660: astore 17
      //   1662: iload 8
      //   1664: istore 15
      //   1666: aload_1
      //   1667: astore 19
      //   1669: goto -230 -> 1439
      //   1672: aload_1
      //   1673: monitorexit
      //   1674: new 190	android/graphics/BitmapFactory$Options
      //   1677: astore 6
      //   1679: aload 6
      //   1681: invokespecial 191	android/graphics/BitmapFactory$Options:<init>	()V
      //   1684: aload 6
      //   1686: iconst_1
      //   1687: putfield 194	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1690: fconst_0
      //   1691: fstore 20
      //   1693: fconst_0
      //   1694: fstore 21
      //   1696: fconst_0
      //   1697: fstore 22
      //   1699: fconst_0
      //   1700: fstore 23
      //   1702: iconst_0
      //   1703: istore 7
      //   1705: iconst_0
      //   1706: istore 8
      //   1708: iconst_0
      //   1709: istore 13
      //   1711: aload_0
      //   1712: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1715: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1718: ifnull +332 -> 2050
      //   1721: aload_0
      //   1722: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1725: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1728: ldc_w 350
      //   1731: invokevirtual 354	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   1734: astore_1
      //   1735: fload 23
      //   1737: fstore 20
      //   1739: aload_1
      //   1740: arraylength
      //   1741: iconst_2
      //   1742: if_icmplt +27 -> 1769
      //   1745: aload_1
      //   1746: iconst_0
      //   1747: aaload
      //   1748: invokestatic 360	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1751: getstatic 364	org/telegram/messenger/AndroidUtilities:density	F
      //   1754: fmul
      //   1755: fstore 22
      //   1757: aload_1
      //   1758: iconst_1
      //   1759: aaload
      //   1760: invokestatic 360	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1763: getstatic 364	org/telegram/messenger/AndroidUtilities:density	F
      //   1766: fmul
      //   1767: fstore 20
      //   1769: aload_0
      //   1770: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1773: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1776: ldc -68
      //   1778: invokevirtual 160	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   1781: ifeq +6 -> 1787
      //   1784: iconst_1
      //   1785: istore 13
      //   1787: iload 13
      //   1789: istore 8
      //   1791: fload 22
      //   1793: fstore 23
      //   1795: fload 22
      //   1797: fconst_0
      //   1798: fcmpl
      //   1799: ifeq +1304 -> 3103
      //   1802: iload 13
      //   1804: istore 8
      //   1806: fload 22
      //   1808: fstore 23
      //   1810: fload 20
      //   1812: fconst_0
      //   1813: fcmpl
      //   1814: ifeq +1289 -> 3103
      //   1817: aload 6
      //   1819: iconst_1
      //   1820: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1823: aload 17
      //   1825: ifnull +168 -> 1993
      //   1828: aload 19
      //   1830: ifnonnull +163 -> 1993
      //   1833: iload 15
      //   1835: ifeq +135 -> 1970
      //   1838: getstatic 370	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1841: invokevirtual 376	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1844: aload 17
      //   1846: invokevirtual 379	java/lang/Long:longValue	()J
      //   1849: iconst_1
      //   1850: aload 6
      //   1852: invokestatic 385	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1855: pop
      //   1856: aconst_null
      //   1857: astore_2
      //   1858: aload_2
      //   1859: astore_1
      //   1860: aload 6
      //   1862: getfield 234	android/graphics/BitmapFactory$Options:outWidth	I
      //   1865: i2f
      //   1866: fstore 21
      //   1868: aload_2
      //   1869: astore_1
      //   1870: aload 6
      //   1872: getfield 237	android/graphics/BitmapFactory$Options:outHeight	I
      //   1875: i2f
      //   1876: fstore 23
      //   1878: aload_2
      //   1879: astore_1
      //   1880: fload 21
      //   1882: fload 22
      //   1884: fdiv
      //   1885: fload 23
      //   1887: fload 20
      //   1889: fdiv
      //   1890: invokestatic 391	java/lang/Math:max	(FF)F
      //   1893: fstore 20
      //   1895: fload 20
      //   1897: fstore 23
      //   1899: fload 20
      //   1901: fconst_1
      //   1902: fcmpg
      //   1903: ifge +6 -> 1909
      //   1906: fconst_1
      //   1907: fstore 23
      //   1909: aload_2
      //   1910: astore_1
      //   1911: aload 6
      //   1913: iconst_0
      //   1914: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1917: aload_2
      //   1918: astore_1
      //   1919: aload 6
      //   1921: fload 23
      //   1923: f2i
      //   1924: putfield 194	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1927: aload_2
      //   1928: astore_1
      //   1929: aload_0
      //   1930: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1933: astore 16
      //   1935: aload_2
      //   1936: astore_1
      //   1937: aload 16
      //   1939: monitorenter
      //   1940: aload_0
      //   1941: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1944: ifeq +261 -> 2205
      //   1947: aload 16
      //   1949: monitorexit
      //   1950: goto -1923 -> 27
      //   1953: astore 17
      //   1955: aload 16
      //   1957: monitorexit
      //   1958: aload_2
      //   1959: astore_1
      //   1960: aload 17
      //   1962: athrow
      //   1963: astore_2
      //   1964: aload_1
      //   1965: astore 16
      //   1967: goto -1515 -> 452
      //   1970: getstatic 370	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1973: invokevirtual 376	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1976: aload 17
      //   1978: invokevirtual 379	java/lang/Long:longValue	()J
      //   1981: iconst_1
      //   1982: aload 6
      //   1984: invokestatic 394	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1987: pop
      //   1988: aconst_null
      //   1989: astore_2
      //   1990: goto -132 -> 1858
      //   1993: iload 10
      //   1995: ifeq +41 -> 2036
      //   1998: new 262	org/telegram/messenger/secretmedia/EncryptedFileInputStream
      //   2001: astore 16
      //   2003: aload 16
      //   2005: aload 9
      //   2007: aload_0
      //   2008: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2011: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   2014: invokespecial 275	org/telegram/messenger/secretmedia/EncryptedFileInputStream:<init>	(Ljava/io/File;Ljava/io/File;)V
      //   2017: aload 16
      //   2019: aconst_null
      //   2020: aload 6
      //   2022: invokestatic 279	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2025: astore_2
      //   2026: aload_2
      //   2027: astore_1
      //   2028: aload 16
      //   2030: invokevirtual 282	java/io/FileInputStream:close	()V
      //   2033: goto -175 -> 1858
      //   2036: new 281	java/io/FileInputStream
      //   2039: dup
      //   2040: aload 9
      //   2042: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   2045: astore 16
      //   2047: goto -30 -> 2017
      //   2050: fload 21
      //   2052: fstore 23
      //   2054: aload 19
      //   2056: ifnull +1047 -> 3103
      //   2059: aload 6
      //   2061: iconst_1
      //   2062: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   2065: aload 6
      //   2067: getstatic 397	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
      //   2070: putfield 400	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2073: new 281	java/io/FileInputStream
      //   2076: astore 16
      //   2078: aload 16
      //   2080: aload 9
      //   2082: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   2085: aload 16
      //   2087: aconst_null
      //   2088: aload 6
      //   2090: invokestatic 279	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2093: astore_2
      //   2094: aload_2
      //   2095: astore_1
      //   2096: aload 16
      //   2098: invokevirtual 282	java/io/FileInputStream:close	()V
      //   2101: aload_2
      //   2102: astore_1
      //   2103: aload 6
      //   2105: getfield 234	android/graphics/BitmapFactory$Options:outWidth	I
      //   2108: istore 8
      //   2110: aload_2
      //   2111: astore_1
      //   2112: aload 6
      //   2114: getfield 237	android/graphics/BitmapFactory$Options:outHeight	I
      //   2117: istore 13
      //   2119: aload_2
      //   2120: astore_1
      //   2121: aload 6
      //   2123: iconst_0
      //   2124: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   2127: aload_2
      //   2128: astore_1
      //   2129: iload 8
      //   2131: sipush 200
      //   2134: idiv
      //   2135: iload 13
      //   2137: sipush 200
      //   2140: idiv
      //   2141: invokestatic 403	java/lang/Math:max	(II)I
      //   2144: i2f
      //   2145: fstore 23
      //   2147: fload 23
      //   2149: fstore 22
      //   2151: fload 23
      //   2153: fconst_1
      //   2154: fcmpg
      //   2155: ifge +6 -> 2161
      //   2158: fconst_1
      //   2159: fstore 22
      //   2161: iconst_1
      //   2162: istore 13
      //   2164: iload 13
      //   2166: iconst_2
      //   2167: imul
      //   2168: istore 8
      //   2170: iload 8
      //   2172: istore 13
      //   2174: iload 8
      //   2176: iconst_2
      //   2177: imul
      //   2178: i2f
      //   2179: fload 22
      //   2181: fcmpg
      //   2182: iflt -18 -> 2164
      //   2185: aload_2
      //   2186: astore_1
      //   2187: aload 6
      //   2189: iload 8
      //   2191: putfield 194	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   2194: iload 7
      //   2196: istore 13
      //   2198: fload 20
      //   2200: fstore 22
      //   2202: goto -275 -> 1927
      //   2205: aload 16
      //   2207: monitorexit
      //   2208: aload_2
      //   2209: astore_1
      //   2210: aload_0
      //   2211: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2214: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2217: ifnull +20 -> 2237
      //   2220: iload 13
      //   2222: ifne +15 -> 2237
      //   2225: aload_2
      //   2226: astore_1
      //   2227: aload_0
      //   2228: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2231: getfield 311	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   2234: ifnull +287 -> 2521
      //   2237: aload_2
      //   2238: astore_1
      //   2239: aload 6
      //   2241: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2244: putfield 400	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2247: aload_2
      //   2248: astore_1
      //   2249: getstatic 120	android/os/Build$VERSION:SDK_INT	I
      //   2252: bipush 21
      //   2254: if_icmpge +11 -> 2265
      //   2257: aload_2
      //   2258: astore_1
      //   2259: aload 6
      //   2261: iconst_1
      //   2262: putfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2265: aload_2
      //   2266: astore_1
      //   2267: aload 6
      //   2269: iconst_0
      //   2270: putfield 406	android/graphics/BitmapFactory$Options:inDither	Z
      //   2273: aload_2
      //   2274: astore 16
      //   2276: aload 17
      //   2278: ifnull +37 -> 2315
      //   2281: aload_2
      //   2282: astore 16
      //   2284: aload 19
      //   2286: ifnonnull +29 -> 2315
      //   2289: iload 15
      //   2291: ifeq +243 -> 2534
      //   2294: aload_2
      //   2295: astore_1
      //   2296: getstatic 370	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2299: invokevirtual 376	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2302: aload 17
      //   2304: invokevirtual 379	java/lang/Long:longValue	()J
      //   2307: iconst_1
      //   2308: aload 6
      //   2310: invokestatic 385	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2313: astore 16
      //   2315: aload 16
      //   2317: astore_2
      //   2318: aload 16
      //   2320: ifnonnull +148 -> 2468
      //   2323: iload 12
      //   2325: ifeq +238 -> 2563
      //   2328: aload 16
      //   2330: astore_1
      //   2331: new 122	java/io/RandomAccessFile
      //   2334: astore 19
      //   2336: aload 16
      //   2338: astore_1
      //   2339: aload 19
      //   2341: aload 9
      //   2343: ldc 124
      //   2345: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2348: aload 16
      //   2350: astore_1
      //   2351: aload 19
      //   2353: invokevirtual 201	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   2356: getstatic 207	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   2359: lconst_0
      //   2360: aload 9
      //   2362: invokevirtual 210	java/io/File:length	()J
      //   2365: invokevirtual 216	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   2368: astore 17
      //   2370: aload 16
      //   2372: astore_1
      //   2373: new 190	android/graphics/BitmapFactory$Options
      //   2376: astore_2
      //   2377: aload 16
      //   2379: astore_1
      //   2380: aload_2
      //   2381: invokespecial 191	android/graphics/BitmapFactory$Options:<init>	()V
      //   2384: aload 16
      //   2386: astore_1
      //   2387: aload_2
      //   2388: iconst_1
      //   2389: putfield 219	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   2392: aload 16
      //   2394: astore_1
      //   2395: aconst_null
      //   2396: aload 17
      //   2398: aload 17
      //   2400: invokevirtual 225	java/nio/ByteBuffer:limit	()I
      //   2403: aload_2
      //   2404: iconst_1
      //   2405: invokestatic 231	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2408: pop
      //   2409: aload 16
      //   2411: astore_1
      //   2412: aload_2
      //   2413: getfield 234	android/graphics/BitmapFactory$Options:outWidth	I
      //   2416: aload_2
      //   2417: getfield 237	android/graphics/BitmapFactory$Options:outHeight	I
      //   2420: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2423: invokestatic 249	org/telegram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   2426: astore_2
      //   2427: aload_2
      //   2428: astore_1
      //   2429: aload 17
      //   2431: invokevirtual 225	java/nio/ByteBuffer:limit	()I
      //   2434: istore 12
      //   2436: aload_2
      //   2437: astore_1
      //   2438: aload 6
      //   2440: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2443: ifne +115 -> 2558
      //   2446: iconst_1
      //   2447: istore_3
      //   2448: aload_2
      //   2449: astore_1
      //   2450: aload_2
      //   2451: aload 17
      //   2453: iload 12
      //   2455: aconst_null
      //   2456: iload_3
      //   2457: invokestatic 231	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2460: pop
      //   2461: aload_2
      //   2462: astore_1
      //   2463: aload 19
      //   2465: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   2468: aload_2
      //   2469: ifnonnull +320 -> 2789
      //   2472: aload_2
      //   2473: astore 16
      //   2475: iload 18
      //   2477: ifeq -2025 -> 452
      //   2480: aload_2
      //   2481: astore_1
      //   2482: aload 9
      //   2484: invokevirtual 210	java/io/File:length	()J
      //   2487: lconst_0
      //   2488: lcmp
      //   2489: ifeq +18 -> 2507
      //   2492: aload_2
      //   2493: astore 16
      //   2495: aload_2
      //   2496: astore_1
      //   2497: aload_0
      //   2498: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2501: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2504: ifnonnull -2052 -> 452
      //   2507: aload_2
      //   2508: astore_1
      //   2509: aload 9
      //   2511: invokevirtual 252	java/io/File:delete	()Z
      //   2514: pop
      //   2515: aload_2
      //   2516: astore 16
      //   2518: goto -2066 -> 452
      //   2521: aload_2
      //   2522: astore_1
      //   2523: aload 6
      //   2525: getstatic 397	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
      //   2528: putfield 400	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2531: goto -284 -> 2247
      //   2534: aload_2
      //   2535: astore_1
      //   2536: getstatic 370	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2539: invokevirtual 376	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2542: aload 17
      //   2544: invokevirtual 379	java/lang/Long:longValue	()J
      //   2547: iconst_1
      //   2548: aload 6
      //   2550: invokestatic 394	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2553: astore 16
      //   2555: goto -240 -> 2315
      //   2558: iconst_0
      //   2559: istore_3
      //   2560: goto -112 -> 2448
      //   2563: aload 16
      //   2565: astore_1
      //   2566: aload 6
      //   2568: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2571: ifeq +149 -> 2720
      //   2574: aload 16
      //   2576: astore_1
      //   2577: new 122	java/io/RandomAccessFile
      //   2580: astore 17
      //   2582: aload 16
      //   2584: astore_1
      //   2585: aload 17
      //   2587: aload 9
      //   2589: ldc 124
      //   2591: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2594: aload 16
      //   2596: astore_1
      //   2597: aload 17
      //   2599: invokevirtual 253	java/io/RandomAccessFile:length	()J
      //   2602: l2i
      //   2603: istore 12
      //   2605: aload 16
      //   2607: astore_1
      //   2608: invokestatic 409	org/telegram/messenger/ImageLoader:access$1700	()[B
      //   2611: ifnull +104 -> 2715
      //   2614: aload 16
      //   2616: astore_1
      //   2617: invokestatic 409	org/telegram/messenger/ImageLoader:access$1700	()[B
      //   2620: arraylength
      //   2621: iload 12
      //   2623: if_icmplt +92 -> 2715
      //   2626: aload 16
      //   2628: astore_1
      //   2629: invokestatic 409	org/telegram/messenger/ImageLoader:access$1700	()[B
      //   2632: astore_2
      //   2633: aload_2
      //   2634: astore_1
      //   2635: aload_1
      //   2636: astore_2
      //   2637: aload_1
      //   2638: ifnonnull +19 -> 2657
      //   2641: aload 16
      //   2643: astore_1
      //   2644: iload 12
      //   2646: newarray <illegal type>
      //   2648: astore_2
      //   2649: aload 16
      //   2651: astore_1
      //   2652: aload_2
      //   2653: invokestatic 412	org/telegram/messenger/ImageLoader:access$1702	([B)[B
      //   2656: pop
      //   2657: aload 16
      //   2659: astore_1
      //   2660: aload 17
      //   2662: aload_2
      //   2663: iconst_0
      //   2664: iload 12
      //   2666: invokevirtual 138	java/io/RandomAccessFile:readFully	([BII)V
      //   2669: aload 16
      //   2671: astore_1
      //   2672: aload 17
      //   2674: invokevirtual 154	java/io/RandomAccessFile:close	()V
      //   2677: iload 10
      //   2679: ifeq +20 -> 2699
      //   2682: aload 16
      //   2684: astore_1
      //   2685: aload_2
      //   2686: iconst_0
      //   2687: iload 12
      //   2689: aload_0
      //   2690: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2693: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   2696: invokestatic 266	org/telegram/messenger/secretmedia/EncryptedFileInputStream:decryptBytesWithKeyFile	([BIILjava/io/File;)V
      //   2699: aload 16
      //   2701: astore_1
      //   2702: aload_2
      //   2703: iconst_0
      //   2704: iload 12
      //   2706: aload 6
      //   2708: invokestatic 272	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2711: astore_2
      //   2712: goto -244 -> 2468
      //   2715: aconst_null
      //   2716: astore_1
      //   2717: goto -82 -> 2635
      //   2720: iload 10
      //   2722: ifeq +51 -> 2773
      //   2725: aload 16
      //   2727: astore_1
      //   2728: new 262	org/telegram/messenger/secretmedia/EncryptedFileInputStream
      //   2731: astore_2
      //   2732: aload 16
      //   2734: astore_1
      //   2735: aload_2
      //   2736: aload 9
      //   2738: aload_0
      //   2739: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2742: getfield 102	org/telegram/messenger/ImageLoader$CacheImage:encryptionKeyPath	Ljava/io/File;
      //   2745: invokespecial 275	org/telegram/messenger/secretmedia/EncryptedFileInputStream:<init>	(Ljava/io/File;Ljava/io/File;)V
      //   2748: aload 16
      //   2750: astore_1
      //   2751: aload_2
      //   2752: aconst_null
      //   2753: aload 6
      //   2755: invokestatic 279	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2758: astore 16
      //   2760: aload 16
      //   2762: astore_1
      //   2763: aload_2
      //   2764: invokevirtual 282	java/io/FileInputStream:close	()V
      //   2767: aload 16
      //   2769: astore_2
      //   2770: goto -302 -> 2468
      //   2773: aload 16
      //   2775: astore_1
      //   2776: new 281	java/io/FileInputStream
      //   2779: dup
      //   2780: aload 9
      //   2782: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   2785: astore_2
      //   2786: goto -38 -> 2748
      //   2789: iconst_0
      //   2790: istore 12
      //   2792: aload_2
      //   2793: astore_1
      //   2794: aload_2
      //   2795: astore 17
      //   2797: iload 12
      //   2799: istore 18
      //   2801: aload_0
      //   2802: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2805: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2808: ifnull +235 -> 3043
      //   2811: aload_2
      //   2812: astore_1
      //   2813: aload_2
      //   2814: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   2817: i2f
      //   2818: fstore 21
      //   2820: aload_2
      //   2821: astore_1
      //   2822: aload_2
      //   2823: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   2826: i2f
      //   2827: fstore 20
      //   2829: aload_2
      //   2830: astore_1
      //   2831: aload_2
      //   2832: astore 16
      //   2834: aload 6
      //   2836: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2839: ifne +83 -> 2922
      //   2842: aload_2
      //   2843: astore 16
      //   2845: fload 22
      //   2847: fconst_0
      //   2848: fcmpl
      //   2849: ifeq +73 -> 2922
      //   2852: aload_2
      //   2853: astore 16
      //   2855: fload 21
      //   2857: fload 22
      //   2859: fcmpl
      //   2860: ifeq +62 -> 2922
      //   2863: aload_2
      //   2864: astore 16
      //   2866: fload 21
      //   2868: ldc_w 413
      //   2871: fload 22
      //   2873: fadd
      //   2874: fcmpl
      //   2875: ifle +47 -> 2922
      //   2878: fload 21
      //   2880: fload 22
      //   2882: fdiv
      //   2883: fstore 23
      //   2885: aload_2
      //   2886: astore_1
      //   2887: aload_2
      //   2888: fload 22
      //   2890: f2i
      //   2891: fload 20
      //   2893: fload 23
      //   2895: fdiv
      //   2896: f2i
      //   2897: iconst_1
      //   2898: invokestatic 417	org/telegram/messenger/Bitmaps:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
      //   2901: astore 17
      //   2903: aload_2
      //   2904: astore 16
      //   2906: aload_2
      //   2907: aload 17
      //   2909: if_acmpeq +13 -> 2922
      //   2912: aload_2
      //   2913: astore_1
      //   2914: aload_2
      //   2915: invokevirtual 420	android/graphics/Bitmap:recycle	()V
      //   2918: aload 17
      //   2920: astore 16
      //   2922: aload 16
      //   2924: astore 17
      //   2926: iload 12
      //   2928: istore 18
      //   2930: aload 16
      //   2932: ifnull +111 -> 3043
      //   2935: aload 16
      //   2937: astore 17
      //   2939: iload 12
      //   2941: istore 18
      //   2943: iload 13
      //   2945: ifeq +98 -> 3043
      //   2948: aload 16
      //   2950: astore 17
      //   2952: iload 12
      //   2954: istore 18
      //   2956: fload 20
      //   2958: ldc_w 421
      //   2961: fcmpg
      //   2962: ifge +81 -> 3043
      //   2965: aload 16
      //   2967: astore 17
      //   2969: iload 12
      //   2971: istore 18
      //   2973: fload 21
      //   2975: ldc_w 421
      //   2978: fcmpg
      //   2979: ifge +64 -> 3043
      //   2982: aload 16
      //   2984: astore_1
      //   2985: aload 16
      //   2987: invokevirtual 291	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   2990: getstatic 243	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2993: if_acmpne +43 -> 3036
      //   2996: aload 16
      //   2998: astore_1
      //   2999: aload 6
      //   3001: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   3004: ifeq +79 -> 3083
      //   3007: iconst_0
      //   3008: istore 18
      //   3010: aload 16
      //   3012: astore_1
      //   3013: aload 16
      //   3015: iconst_3
      //   3016: iload 18
      //   3018: aload 16
      //   3020: invokevirtual 294	android/graphics/Bitmap:getWidth	()I
      //   3023: aload 16
      //   3025: invokevirtual 297	android/graphics/Bitmap:getHeight	()I
      //   3028: aload 16
      //   3030: invokevirtual 300	android/graphics/Bitmap:getRowBytes	()I
      //   3033: invokestatic 304	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   3036: iconst_1
      //   3037: istore 18
      //   3039: aload 16
      //   3041: astore 17
      //   3043: aload 17
      //   3045: astore 16
      //   3047: iload 18
      //   3049: ifne -2597 -> 452
      //   3052: aload 17
      //   3054: astore 16
      //   3056: aload 17
      //   3058: astore_1
      //   3059: aload 6
      //   3061: getfield 197	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   3064: ifeq -2612 -> 452
      //   3067: aload 17
      //   3069: astore_1
      //   3070: aload 17
      //   3072: invokestatic 308	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   3075: pop
      //   3076: aload 17
      //   3078: astore 16
      //   3080: goto -2628 -> 452
      //   3083: iconst_1
      //   3084: istore 18
      //   3086: goto -76 -> 3010
      //   3089: aconst_null
      //   3090: astore_1
      //   3091: goto -2620 -> 471
      //   3094: astore_1
      //   3095: goto -2537 -> 558
      //   3098: astore 16
      //   3100: goto -2587 -> 513
      //   3103: aconst_null
      //   3104: astore_2
      //   3105: iload 8
      //   3107: istore 13
      //   3109: fload 23
      //   3111: fstore 22
      //   3113: goto -1186 -> 1927
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	3116	0	this	CacheOutTask
      //   442	4	1	localThrowable1	Throwable
      //   470	23	1	localObject2	Object
      //   496	2	1	localException1	Exception
      //   514	1	1	localObject3	Object
      //   539	15	1	localException2	Exception
      //   557	258	1	localObject4	Object
      //   820	1	1	localThrowable2	Throwable
      //   1535	138	1	localThrowable3	Throwable
      //   1734	1357	1	localObject6	Object
      //   3094	1	1	localObject7	Object
      //   59	4	2	localObject8	Object
      //   64	4	2	localObject9	Object
      //   223	132	2	localRandomAccessFile	RandomAccessFile
      //   437	4	2	localObject10	Object
      //   444	119	2	localObject11	Object
      //   568	2	2	localException3	Exception
      //   735	652	2	localObject12	Object
      //   1530	4	2	localObject13	Object
      //   1857	102	2	localObject14	Object
      //   1963	1	2	localThrowable4	Throwable
      //   1989	1116	2	localObject15	Object
      //   105	2455	3	bool	boolean
      //   134	1525	4	localObject16	Object
      //   137	1439	5	localObject17	Object
      //   140	2920	6	localOptions	BitmapFactory.Options
      //   143	2052	7	i	int
      //   146	2960	8	j	int
      //   155	2626	9	localFile	File
      //   186	2535	10	k	int
      //   189	1448	11	m	int
      //   192	2778	12	n	int
      //   195	2913	13	i1	int
      //   198	288	14	i2	int
      //   201	2089	15	i3	int
      //   212	254	16	localObject18	Object
      //   508	8	16	localException4	Exception
      //   551	5	16	localObject19	Object
      //   690	2389	16	localObject20	Object
      //   3098	1	16	localException5	Exception
      //   215	1630	17	localObject21	Object
      //   1953	350	17	localObject22	Object
      //   2368	709	17	localObject23	Object
      //   303	2782	18	i4	int
      //   662	1802	19	localObject24	Object
      //   1691	1266	20	f1	float
      //   1694	1280	21	f2	float
      //   1697	1415	22	f3	float
      //   1700	1410	23	f4	float
      // Exception table:
      //   from	to	target	type
      //   47	56	59	finally
      //   60	62	59	finally
      //   69	71	59	finally
      //   7	27	64	finally
      //   28	30	64	finally
      //   65	67	64	finally
      //   425	434	437	finally
      //   438	440	437	finally
      //   622	624	437	finally
      //   407	425	442	java/lang/Throwable
      //   440	442	442	java/lang/Throwable
      //   624	654	442	java/lang/Throwable
      //   659	734	442	java/lang/Throwable
      //   829	878	442	java/lang/Throwable
      //   884	894	442	java/lang/Throwable
      //   894	908	442	java/lang/Throwable
      //   913	927	442	java/lang/Throwable
      //   927	937	442	java/lang/Throwable
      //   950	969	442	java/lang/Throwable
      //   969	978	442	java/lang/Throwable
      //   988	999	442	java/lang/Throwable
      //   354	358	496	java/lang/Exception
      //   220	224	508	java/lang/Exception
      //   227	235	508	java/lang/Exception
      //   528	532	539	java/lang/Exception
      //   220	224	551	finally
      //   227	235	551	finally
      //   515	520	551	finally
      //   562	566	568	java/lang/Exception
      //   736	743	820	java/lang/Throwable
      //   745	753	820	java/lang/Throwable
      //   757	768	820	java/lang/Throwable
      //   770	775	820	java/lang/Throwable
      //   781	791	820	java/lang/Throwable
      //   796	806	820	java/lang/Throwable
      //   808	814	820	java/lang/Throwable
      //   980	985	820	java/lang/Throwable
      //   1013	1023	820	java/lang/Throwable
      //   1025	1033	820	java/lang/Throwable
      //   1038	1057	820	java/lang/Throwable
      //   1080	1090	820	java/lang/Throwable
      //   1092	1100	820	java/lang/Throwable
      //   1105	1124	820	java/lang/Throwable
      //   1147	1157	820	java/lang/Throwable
      //   1159	1167	820	java/lang/Throwable
      //   1172	1192	820	java/lang/Throwable
      //   1194	1202	820	java/lang/Throwable
      //   1207	1227	820	java/lang/Throwable
      //   1229	1237	820	java/lang/Throwable
      //   1242	1262	820	java/lang/Throwable
      //   1299	1307	820	java/lang/Throwable
      //   1309	1314	820	java/lang/Throwable
      //   1518	1527	1530	finally
      //   1531	1533	1530	finally
      //   1672	1674	1530	finally
      //   1339	1382	1535	java/lang/Throwable
      //   1393	1430	1535	java/lang/Throwable
      //   1456	1500	1535	java/lang/Throwable
      //   1500	1518	1535	java/lang/Throwable
      //   1533	1535	1535	java/lang/Throwable
      //   1542	1575	1535	java/lang/Throwable
      //   1588	1610	1535	java/lang/Throwable
      //   1622	1636	1535	java/lang/Throwable
      //   1674	1690	1535	java/lang/Throwable
      //   1711	1735	1535	java/lang/Throwable
      //   1739	1769	1535	java/lang/Throwable
      //   1769	1784	1535	java/lang/Throwable
      //   1817	1823	1535	java/lang/Throwable
      //   1838	1856	1535	java/lang/Throwable
      //   1970	1988	1535	java/lang/Throwable
      //   1998	2017	1535	java/lang/Throwable
      //   2017	2026	1535	java/lang/Throwable
      //   2036	2047	1535	java/lang/Throwable
      //   2059	2094	1535	java/lang/Throwable
      //   1940	1950	1953	finally
      //   1955	1958	1953	finally
      //   2205	2208	1953	finally
      //   1860	1868	1963	java/lang/Throwable
      //   1870	1878	1963	java/lang/Throwable
      //   1880	1895	1963	java/lang/Throwable
      //   1911	1917	1963	java/lang/Throwable
      //   1919	1927	1963	java/lang/Throwable
      //   1929	1935	1963	java/lang/Throwable
      //   1937	1940	1963	java/lang/Throwable
      //   1960	1963	1963	java/lang/Throwable
      //   2028	2033	1963	java/lang/Throwable
      //   2096	2101	1963	java/lang/Throwable
      //   2103	2110	1963	java/lang/Throwable
      //   2112	2119	1963	java/lang/Throwable
      //   2121	2127	1963	java/lang/Throwable
      //   2129	2147	1963	java/lang/Throwable
      //   2187	2194	1963	java/lang/Throwable
      //   2210	2220	1963	java/lang/Throwable
      //   2227	2237	1963	java/lang/Throwable
      //   2239	2247	1963	java/lang/Throwable
      //   2249	2257	1963	java/lang/Throwable
      //   2259	2265	1963	java/lang/Throwable
      //   2267	2273	1963	java/lang/Throwable
      //   2296	2315	1963	java/lang/Throwable
      //   2331	2336	1963	java/lang/Throwable
      //   2339	2348	1963	java/lang/Throwable
      //   2351	2370	1963	java/lang/Throwable
      //   2373	2377	1963	java/lang/Throwable
      //   2380	2384	1963	java/lang/Throwable
      //   2387	2392	1963	java/lang/Throwable
      //   2395	2409	1963	java/lang/Throwable
      //   2412	2427	1963	java/lang/Throwable
      //   2429	2436	1963	java/lang/Throwable
      //   2438	2446	1963	java/lang/Throwable
      //   2450	2461	1963	java/lang/Throwable
      //   2463	2468	1963	java/lang/Throwable
      //   2482	2492	1963	java/lang/Throwable
      //   2497	2507	1963	java/lang/Throwable
      //   2509	2515	1963	java/lang/Throwable
      //   2523	2531	1963	java/lang/Throwable
      //   2536	2555	1963	java/lang/Throwable
      //   2566	2574	1963	java/lang/Throwable
      //   2577	2582	1963	java/lang/Throwable
      //   2585	2594	1963	java/lang/Throwable
      //   2597	2605	1963	java/lang/Throwable
      //   2608	2614	1963	java/lang/Throwable
      //   2617	2626	1963	java/lang/Throwable
      //   2629	2633	1963	java/lang/Throwable
      //   2644	2649	1963	java/lang/Throwable
      //   2652	2657	1963	java/lang/Throwable
      //   2660	2669	1963	java/lang/Throwable
      //   2672	2677	1963	java/lang/Throwable
      //   2685	2699	1963	java/lang/Throwable
      //   2702	2712	1963	java/lang/Throwable
      //   2728	2732	1963	java/lang/Throwable
      //   2735	2748	1963	java/lang/Throwable
      //   2751	2760	1963	java/lang/Throwable
      //   2763	2767	1963	java/lang/Throwable
      //   2776	2786	1963	java/lang/Throwable
      //   2801	2811	1963	java/lang/Throwable
      //   2813	2820	1963	java/lang/Throwable
      //   2822	2829	1963	java/lang/Throwable
      //   2834	2842	1963	java/lang/Throwable
      //   2887	2903	1963	java/lang/Throwable
      //   2914	2918	1963	java/lang/Throwable
      //   2985	2996	1963	java/lang/Throwable
      //   2999	3007	1963	java/lang/Throwable
      //   3013	3036	1963	java/lang/Throwable
      //   3059	3067	1963	java/lang/Throwable
      //   3070	3076	1963	java/lang/Throwable
      //   239	249	3094	finally
      //   253	257	3094	finally
      //   261	269	3094	finally
      //   273	278	3094	finally
      //   282	288	3094	finally
      //   292	301	3094	finally
      //   309	318	3094	finally
      //   326	335	3094	finally
      //   342	346	3094	finally
      //   489	493	3094	finally
      //   239	249	3098	java/lang/Exception
      //   253	257	3098	java/lang/Exception
      //   261	269	3098	java/lang/Exception
      //   273	278	3098	java/lang/Exception
      //   282	288	3098	java/lang/Exception
      //   292	301	3098	java/lang/Exception
      //   309	318	3098	java/lang/Exception
      //   326	335	3098	java/lang/Exception
      //   342	346	3098	java/lang/Exception
      //   489	493	3098	java/lang/Exception
    }
  }
  
  private class HttpFileTask
    extends AsyncTask<Void, Void, Boolean>
  {
    private boolean canRetry = true;
    private int currentAccount;
    private String ext;
    private RandomAccessFile fileOutputStream = null;
    private int fileSize;
    private long lastProgressTime;
    private File tempFile;
    private String url;
    
    public HttpFileTask(String paramString1, File paramFile, String paramString2, int paramInt)
    {
      this.url = paramString1;
      this.tempFile = paramFile;
      this.ext = paramString2;
      this.currentAccount = paramInt;
    }
    
    private void reportProgress(final float paramFloat)
    {
      long l = System.currentTimeMillis();
      if ((paramFloat == 1.0F) || (this.lastProgressTime == 0L) || (this.lastProgressTime < l - 500L))
      {
        this.lastProgressTime = l;
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.put(ImageLoader.HttpFileTask.this.url, Float.valueOf(paramFloat));
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance(ImageLoader.HttpFileTask.this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpFileTask.this.url, Float.valueOf(ImageLoader.HttpFileTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      RandomAccessFile localRandomAccessFile = null;
      bool1 = false;
      bool2 = false;
      bool3 = false;
      bool4 = false;
      Object localObject1 = null;
      paramVarArgs = (Void[])localObject1;
      localObject2 = localRandomAccessFile;
      try
      {
        Object localObject3 = new java/net/URL;
        paramVarArgs = (Void[])localObject1;
        localObject2 = localRandomAccessFile;
        ((URL)localObject3).<init>(this.url);
        paramVarArgs = (Void[])localObject1;
        localObject2 = localRandomAccessFile;
        localObject3 = ((URL)localObject3).openConnection();
        paramVarArgs = (Void[])localObject3;
        localObject2 = localRandomAccessFile;
        ((URLConnection)localObject3).addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
        paramVarArgs = (Void[])localObject3;
        localObject2 = localRandomAccessFile;
        ((URLConnection)localObject3).setConnectTimeout(5000);
        paramVarArgs = (Void[])localObject3;
        localObject2 = localRandomAccessFile;
        ((URLConnection)localObject3).setReadTimeout(5000);
        localObject1 = localObject3;
        paramVarArgs = (Void[])localObject3;
        localObject2 = localRandomAccessFile;
        if ((localObject3 instanceof HttpURLConnection))
        {
          paramVarArgs = (Void[])localObject3;
          localObject2 = localRandomAccessFile;
          Object localObject4 = (HttpURLConnection)localObject3;
          paramVarArgs = (Void[])localObject3;
          localObject2 = localRandomAccessFile;
          ((HttpURLConnection)localObject4).setInstanceFollowRedirects(true);
          paramVarArgs = (Void[])localObject3;
          localObject2 = localRandomAccessFile;
          i = ((HttpURLConnection)localObject4).getResponseCode();
          if ((i != 302) && (i != 301))
          {
            localObject1 = localObject3;
            if (i != 303) {}
          }
          else
          {
            paramVarArgs = (Void[])localObject3;
            localObject2 = localRandomAccessFile;
            localObject1 = ((HttpURLConnection)localObject4).getHeaderField("Location");
            paramVarArgs = (Void[])localObject3;
            localObject2 = localRandomAccessFile;
            localObject4 = ((HttpURLConnection)localObject4).getHeaderField("Set-Cookie");
            paramVarArgs = (Void[])localObject3;
            localObject2 = localRandomAccessFile;
            URL localURL = new java/net/URL;
            paramVarArgs = (Void[])localObject3;
            localObject2 = localRandomAccessFile;
            localURL.<init>((String)localObject1);
            paramVarArgs = (Void[])localObject3;
            localObject2 = localRandomAccessFile;
            localObject1 = localURL.openConnection();
            paramVarArgs = (Void[])localObject1;
            localObject2 = localRandomAccessFile;
            ((URLConnection)localObject1).setRequestProperty("Cookie", (String)localObject4);
            paramVarArgs = (Void[])localObject1;
            localObject2 = localRandomAccessFile;
            ((URLConnection)localObject1).addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
          }
        }
        paramVarArgs = (Void[])localObject1;
        localObject2 = localRandomAccessFile;
        ((URLConnection)localObject1).connect();
        paramVarArgs = (Void[])localObject1;
        localObject2 = localRandomAccessFile;
        localObject3 = ((URLConnection)localObject1).getInputStream();
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject3;
        localRandomAccessFile = new java/io/RandomAccessFile;
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject3;
        localRandomAccessFile.<init>(this.tempFile, "rws");
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject3;
        this.fileOutputStream = localRandomAccessFile;
        localObject2 = localObject3;
        paramVarArgs = (Void[])localObject1;
      }
      catch (Throwable paramVarArgs)
      {
        try
        {
          if (!(paramVarArgs instanceof HttpURLConnection)) {
            break label406;
          }
          i = ((HttpURLConnection)paramVarArgs).getResponseCode();
          if ((i == 200) || (i == 202) || (i == 304)) {
            break label406;
          }
          this.canRetry = false;
          if (paramVarArgs == null) {
            break label470;
          }
        }
        catch (Exception paramVarArgs)
        {
          try
          {
            paramVarArgs = paramVarArgs.getHeaderFields();
            if (paramVarArgs == null) {
              break label470;
            }
            paramVarArgs = (List)paramVarArgs.get("content-Length");
            if ((paramVarArgs == null) || (paramVarArgs.isEmpty())) {
              break label470;
            }
            paramVarArgs = (String)paramVarArgs.get(0);
            if (paramVarArgs == null) {
              break label470;
            }
            this.fileSize = Utilities.parseInt(paramVarArgs).intValue();
            bool5 = bool4;
            if (localObject2 == null) {
              break label508;
            }
            bool1 = bool3;
          }
          catch (Exception paramVarArgs)
          {
            try
            {
              paramVarArgs = new byte[32768];
              i = 0;
              bool1 = bool3;
              bool5 = isCancelled();
              if (!bool5) {
                break label664;
              }
              bool5 = bool4;
            }
            catch (Throwable paramVarArgs)
            {
              try
              {
                for (;;)
                {
                  int i;
                  if (this.fileOutputStream != null)
                  {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                  }
                  bool1 = bool5;
                  if (localObject2 != null) {}
                  try
                  {
                    ((InputStream)localObject2).close();
                    bool1 = bool5;
                  }
                  catch (Throwable paramVarArgs)
                  {
                    for (;;)
                    {
                      boolean bool6;
                      int j;
                      FileLog.e(paramVarArgs);
                      bool1 = bool5;
                    }
                  }
                  return Boolean.valueOf(bool1);
                  localThrowable = localThrowable;
                  if ((localThrowable instanceof SocketTimeoutException)) {
                    if (ConnectionsManager.isNetworkOnline()) {
                      this.canRetry = false;
                    }
                  }
                  for (;;)
                  {
                    FileLog.e(localThrowable);
                    break;
                    if ((localThrowable instanceof UnknownHostException)) {
                      this.canRetry = false;
                    } else if ((localThrowable instanceof SocketException))
                    {
                      if ((localThrowable.getMessage() != null) && (localThrowable.getMessage().contains("ECONNRESET"))) {
                        this.canRetry = false;
                      }
                    }
                    else if ((localThrowable instanceof FileNotFoundException)) {
                      this.canRetry = false;
                    }
                  }
                  localException = localException;
                  FileLog.e(localException);
                  continue;
                  paramVarArgs = paramVarArgs;
                  FileLog.e(paramVarArgs);
                  continue;
                  bool6 = bool2;
                  bool1 = bool3;
                  try
                  {
                    j = ((InputStream)localObject2).read(paramVarArgs);
                    if (j <= 0) {
                      break label780;
                    }
                    bool6 = bool2;
                    bool1 = bool3;
                    this.fileOutputStream.write(paramVarArgs, 0, j);
                    j = i + j;
                    i = j;
                    bool6 = bool2;
                    bool1 = bool3;
                    if (this.fileSize <= 0) {
                      continue;
                    }
                    bool6 = bool2;
                    bool1 = bool3;
                    reportProgress(j / this.fileSize);
                    i = j;
                  }
                  catch (Exception paramVarArgs)
                  {
                    bool1 = bool6;
                    FileLog.e(paramVarArgs);
                    bool5 = bool6;
                  }
                  continue;
                  paramVarArgs = paramVarArgs;
                  FileLog.e(paramVarArgs);
                  bool5 = bool1;
                  continue;
                  bool5 = bool4;
                  if (j == -1)
                  {
                    bool2 = true;
                    bool3 = true;
                    bool4 = true;
                    bool5 = bool4;
                    bool6 = bool2;
                    bool1 = bool3;
                    if (this.fileSize != 0)
                    {
                      bool6 = bool2;
                      bool1 = bool3;
                      reportProgress(1.0F);
                      bool5 = bool4;
                    }
                  }
                }
              }
              catch (Throwable paramVarArgs)
              {
                for (;;)
                {
                  FileLog.e(paramVarArgs);
                }
              }
            }
          }
        }
      }
      if (this.canRetry) {
        if (paramVarArgs == null) {}
      }
    }
    
    protected void onCancelled()
    {
      ImageLoader.this.runHttpFileLoadTasks(this, 2);
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      ImageLoader localImageLoader = ImageLoader.this;
      if (paramBoolean.booleanValue()) {}
      for (int i = 2;; i = 1)
      {
        localImageLoader.runHttpFileLoadTasks(this, i);
        return;
      }
    }
  }
  
  private class HttpImageTask
    extends AsyncTask<Void, Void, Boolean>
  {
    private ImageLoader.CacheImage cacheImage = null;
    private boolean canRetry = true;
    private RandomAccessFile fileOutputStream = null;
    private HttpURLConnection httpConnection = null;
    private int imageSize;
    private long lastProgressTime;
    
    public HttpImageTask(ImageLoader.CacheImage paramCacheImage, int paramInt)
    {
      this.cacheImage = paramCacheImage;
      this.imageSize = paramInt;
    }
    
    private void reportProgress(final float paramFloat)
    {
      long l = System.currentTimeMillis();
      if ((paramFloat == 1.0F) || (this.lastProgressTime == 0L) || (this.lastProgressTime < l - 500L))
      {
        this.lastProgressTime = l;
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.put(ImageLoader.HttpImageTask.this.cacheImage.url, Float.valueOf(paramFloat));
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance(ImageLoader.HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Float.valueOf(ImageLoader.HttpImageTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      Object localObject1 = null;
      RandomAccessFile localRandomAccessFile = null;
      bool1 = false;
      bool2 = false;
      bool3 = false;
      paramVarArgs = localRandomAccessFile;
      if (!isCancelled()) {
        localObject2 = localObject1;
      }
      try
      {
        paramVarArgs = new java/net/URL;
        localObject2 = localObject1;
        paramVarArgs.<init>(this.cacheImage.httpUrl);
        localObject2 = localObject1;
        this.httpConnection = ((HttpURLConnection)paramVarArgs.openConnection());
        localObject2 = localObject1;
        this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
        localObject2 = localObject1;
        this.httpConnection.setConnectTimeout(5000);
        localObject2 = localObject1;
        this.httpConnection.setReadTimeout(5000);
        localObject2 = localObject1;
        this.httpConnection.setInstanceFollowRedirects(true);
        paramVarArgs = localRandomAccessFile;
        localObject2 = localObject1;
        if (!isCancelled())
        {
          localObject2 = localObject1;
          this.httpConnection.connect();
          localObject2 = localObject1;
          paramVarArgs = this.httpConnection.getInputStream();
          localObject2 = paramVarArgs;
          localRandomAccessFile = new java/io/RandomAccessFile;
          localObject2 = paramVarArgs;
          localRandomAccessFile.<init>(this.cacheImage.tempFilePath, "rws");
          localObject2 = paramVarArgs;
          this.fileOutputStream = localRandomAccessFile;
        }
      }
      catch (Throwable localThrowable2)
      {
        try
        {
          if ((this.httpConnection == null) || (!(this.httpConnection instanceof HttpURLConnection))) {
            break label238;
          }
          i = this.httpConnection.getResponseCode();
          if ((i == 200) || (i == 202) || (i == 304)) {
            break label238;
          }
          this.canRetry = false;
          if ((this.imageSize != 0) || (this.httpConnection == null)) {
            break label325;
          }
        }
        catch (Exception localThrowable2)
        {
          try
          {
            localObject2 = this.httpConnection.getHeaderFields();
            if (localObject2 == null) {
              break label325;
            }
            localObject2 = (List)((Map)localObject2).get("content-Length");
            if ((localObject2 == null) || (((List)localObject2).isEmpty())) {
              break label325;
            }
            localObject2 = (String)((List)localObject2).get(0);
            if (localObject2 == null) {
              break label325;
            }
            this.imageSize = Utilities.parseInt((String)localObject2).intValue();
            bool4 = bool3;
            if (paramVarArgs == null) {
              break label366;
            }
            bool5 = bool2;
          }
          catch (Exception localThrowable2)
          {
            try
            {
              localObject2 = new byte[' '];
              i = 0;
              bool5 = bool2;
              bool6 = isCancelled();
              if (!bool6) {
                break label575;
              }
              bool4 = bool3;
            }
            catch (Throwable localThrowable2)
            {
              try
              {
                if (this.fileOutputStream == null) {
                  break label385;
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
              }
              catch (Throwable localThrowable2)
              {
                try
                {
                  for (;;)
                  {
                    boolean bool4;
                    int i;
                    boolean bool5;
                    boolean bool6;
                    if (this.httpConnection == null) {
                      break label399;
                    }
                    this.httpConnection.disconnect();
                    if (paramVarArgs == null) {
                      break label407;
                    }
                    try
                    {
                      paramVarArgs.close();
                      if ((!bool4) || (this.cacheImage.tempFilePath == null) || (this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                        break label456;
                      }
                      this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                      return Boolean.valueOf(bool4);
                      paramVarArgs = paramVarArgs;
                      if (!(paramVarArgs instanceof SocketTimeoutException)) {
                        break label491;
                      }
                      if (!ConnectionsManager.isNetworkOnline()) {
                        break label481;
                      }
                      this.canRetry = false;
                      for (;;)
                      {
                        FileLog.e(paramVarArgs);
                        paramVarArgs = (Void[])localObject2;
                        break;
                        if ((paramVarArgs instanceof UnknownHostException)) {
                          this.canRetry = false;
                        } else if ((paramVarArgs instanceof SocketException))
                        {
                          if ((paramVarArgs.getMessage() != null) && (paramVarArgs.getMessage().contains("ECONNRESET"))) {
                            this.canRetry = false;
                          }
                        }
                        else if ((paramVarArgs instanceof FileNotFoundException)) {
                          this.canRetry = false;
                        }
                      }
                      localException1 = localException1;
                      FileLog.e(localException1);
                      continue;
                      localException2 = localException2;
                      FileLog.e(localException2);
                      continue;
                      bool6 = bool1;
                      bool5 = bool2;
                      int j;
                      try
                      {
                        j = paramVarArgs.read(localException2);
                        if (j <= 0) {
                          break label702;
                        }
                        int k = i + j;
                        bool6 = bool1;
                        bool5 = bool2;
                        this.fileOutputStream.write(localException2, 0, j);
                        i = k;
                        bool6 = bool1;
                        bool5 = bool2;
                        if (this.imageSize == 0) {
                          continue;
                        }
                        bool6 = bool1;
                        bool5 = bool2;
                        reportProgress(k / this.imageSize);
                        i = k;
                      }
                      catch (Exception localException3)
                      {
                        bool5 = bool6;
                        FileLog.e(localException3);
                        bool4 = bool6;
                      }
                      continue;
                      localThrowable1 = localThrowable1;
                      FileLog.e(localThrowable1);
                      bool4 = bool5;
                      continue;
                      bool4 = bool3;
                      if (j != -1) {
                        continue;
                      }
                      bool1 = true;
                      bool2 = true;
                      bool3 = true;
                      bool4 = bool3;
                      bool6 = bool1;
                      bool5 = bool2;
                      if (this.imageSize == 0) {
                        continue;
                      }
                      bool6 = bool1;
                      bool5 = bool2;
                      reportProgress(1.0F);
                      bool4 = bool3;
                      continue;
                      localThrowable2 = localThrowable2;
                      FileLog.e(localThrowable2);
                    }
                    catch (Throwable paramVarArgs)
                    {
                      for (;;)
                      {
                        FileLog.e(paramVarArgs);
                      }
                    }
                  }
                }
                catch (Throwable localThrowable3)
                {
                  for (;;) {}
                }
              }
            }
          }
        }
      }
      bool4 = bool3;
      if (isCancelled()) {}
    }
    
    protected void onCancelled()
    {
      ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.runHttpTasks(true);
        }
      });
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.fileProgresses.remove(ImageLoader.HttpImageTask.this.cacheImage.url);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance(ImageLoader.HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Integer.valueOf(1) });
            }
          });
        }
      });
    }
    
    protected void onPostExecute(final Boolean paramBoolean)
    {
      if ((paramBoolean.booleanValue()) || (!this.canRetry)) {
        ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
      }
      for (;;)
      {
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.remove(ImageLoader.HttpImageTask.this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (ImageLoader.HttpImageTask.2.this.val$result.booleanValue()) {
                  NotificationCenter.getInstance(ImageLoader.HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url });
                }
                for (;;)
                {
                  return;
                  NotificationCenter.getInstance(ImageLoader.HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Integer.valueOf(2) });
                }
              }
            });
          }
        });
        ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.runHttpTasks(true);
          }
        });
        return;
        ImageLoader.this.httpFileLoadError(this.cacheImage.url);
      }
    }
  }
  
  private class ThumbGenerateInfo
  {
    private int count;
    private TLRPC.FileLocation fileLocation;
    private String filter;
    
    private ThumbGenerateInfo() {}
  }
  
  private class ThumbGenerateTask
    implements Runnable
  {
    private String filter;
    private int mediaType;
    private File originalPath;
    private TLRPC.FileLocation thumbLocation;
    
    public ThumbGenerateTask(int paramInt, File paramFile, TLRPC.FileLocation paramFileLocation, String paramString)
    {
      this.mediaType = paramInt;
      this.originalPath = paramFile;
      this.thumbLocation = paramFileLocation;
      this.filter = paramString;
    }
    
    private void removeTask()
    {
      if (this.thumbLocation == null) {}
      for (;;)
      {
        return;
        final String str = FileLoader.getAttachFileName(this.thumbLocation);
        ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.thumbGenerateTasks.remove(str);
          }
        });
      }
    }
    
    public void run()
    {
      for (;;)
      {
        String str;
        Object localObject2;
        Object localObject1;
        try
        {
          if (this.thumbLocation == null)
          {
            removeTask();
            return;
          }
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          str = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
          localFile = new java/io/File;
          localObject2 = FileLoader.getDirectory(4);
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localFile.<init>((File)localObject2, "q_" + str + ".jpg");
          if ((localFile.exists()) || (!this.originalPath.exists()))
          {
            removeTask();
            continue;
          }
        }
        catch (Throwable localThrowable)
        {
          File localFile;
          FileLog.e(localThrowable);
          removeTask();
          continue;
          int i = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
          localObject1 = null;
          if (this.mediaType == 0)
          {
            localObject1 = ImageLoader.loadBitmap(this.originalPath.toString(), null, i, i, false);
            if (localObject1 == null) {
              removeTask();
            }
          }
          else
          {
            if (this.mediaType == 2)
            {
              localObject1 = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
              continue;
            }
            if (this.mediaType != 3) {
              continue;
            }
            localObject1 = this.originalPath.toString().toLowerCase();
            if ((!((String)localObject1).endsWith(".jpg")) && (!((String)localObject1).endsWith(".jpeg")) && (!((String)localObject1).endsWith(".png")) && (!((String)localObject1).endsWith(".gif")))
            {
              removeTask();
              continue;
            }
            localObject1 = ImageLoader.loadBitmap((String)localObject1, null, i, i, false);
            continue;
          }
          int j = ((Bitmap)localObject1).getWidth();
          int k = ((Bitmap)localObject1).getHeight();
          if ((j == 0) || (k == 0))
          {
            removeTask();
            continue;
          }
          float f = Math.min(j / i, k / i);
          Bitmap localBitmap = Bitmaps.createScaledBitmap((Bitmap)localObject1, (int)(j / f), (int)(k / f), true);
          localObject2 = localObject1;
          if (localBitmap != localObject1)
          {
            ((Bitmap)localObject1).recycle();
            localObject2 = localBitmap;
          }
          localObject1 = new java/io/FileOutputStream;
          ((FileOutputStream)localObject1).<init>(localFile);
          ((Bitmap)localObject2).compress(Bitmap.CompressFormat.JPEG, 60, (OutputStream)localObject1);
        }
        try
        {
          ((FileOutputStream)localObject1).close();
          localObject1 = new android/graphics/drawable/BitmapDrawable;
          ((BitmapDrawable)localObject1).<init>((Bitmap)localObject2);
          localObject2 = new org/telegram/messenger/ImageLoader$ThumbGenerateTask$2;
          ((2)localObject2).<init>(this, str, (BitmapDrawable)localObject1);
          AndroidUtilities.runOnUIThread((Runnable)localObject2);
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ImageLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */