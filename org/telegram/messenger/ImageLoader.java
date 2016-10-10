package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
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
  private LinkedList<HttpFileTask> httpFileLoadTasks = new LinkedList();
  private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys = new HashMap();
  private LinkedList<HttpImageTask> httpTasks = new LinkedList();
  private String ignoreRemoval = null;
  private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
  private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap();
  private HashMap<Integer, CacheImage> imageLoadingByTag = new HashMap();
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
  private HashMap<Integer, String> waitingForQualityThumbByTag = new HashMap();
  
  public ImageLoader()
  {
    this.cacheOutQueue.setPriority(1);
    this.cacheThumbOutQueue.setPriority(1);
    this.thumbGeneratingQueue.setPriority(1);
    this.imageLoadQueue.setPriority(1);
    this.memCache = new LruCache(Math.min(15, ((ActivityManager)ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * 1024 * 1024)
    {
      protected void entryRemoved(boolean paramAnonymousBoolean, String paramAnonymousString, BitmapDrawable paramAnonymousBitmapDrawable1, BitmapDrawable paramAnonymousBitmapDrawable2)
      {
        if ((ImageLoader.this.ignoreRemoval != null) && (paramAnonymousString != null) && (ImageLoader.this.ignoreRemoval.equals(paramAnonymousString))) {}
        do
        {
          do
          {
            return;
            paramAnonymousString = (Integer)ImageLoader.this.bitmapUseCounts.get(paramAnonymousString);
          } while ((paramAnonymousString != null) && (paramAnonymousString.intValue() != 0));
          paramAnonymousString = paramAnonymousBitmapDrawable1.getBitmap();
        } while (paramAnonymousString.isRecycled());
        paramAnonymousString.recycle();
      }
      
      protected int sizeOf(String paramAnonymousString, BitmapDrawable paramAnonymousBitmapDrawable)
      {
        return paramAnonymousBitmapDrawable.getBitmap().getByteCount();
      }
    };
    FileLoader.getInstance().setDelegate(new FileLoader.FileLoaderDelegate()
    {
      public void fileDidFailedLoad(final String paramAnonymousString, final int paramAnonymousInt)
      {
        ImageLoader.this.fileProgresses.remove(paramAnonymousString);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.fileDidFailedLoad(paramAnonymousString, paramAnonymousInt);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { paramAnonymousString, Integer.valueOf(paramAnonymousInt) });
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
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailUpload, new Object[] { ImageLoader.2.3.this.val$location, Boolean.valueOf(ImageLoader.2.3.this.val$isEncrypted) });
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
            if ((MediaController.getInstance().canSaveToGallery()) && (ImageLoader.this.telegramPath != null) && (paramAnonymousFile != null) && ((paramAnonymousString.endsWith(".mp4")) || (paramAnonymousString.endsWith(".jpg"))) && (paramAnonymousFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
              AndroidUtilities.addMediaToGallery(paramAnonymousFile.toString());
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { paramAnonymousString });
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
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidUpload, new Object[] { ImageLoader.2.2.this.val$location, ImageLoader.2.2.this.val$inputFile, ImageLoader.2.2.this.val$inputEncryptedFile, ImageLoader.2.2.this.val$key, ImageLoader.2.2.this.val$iv, Long.valueOf(ImageLoader.2.2.this.val$totalFileSize) });
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
          ImageLoader.access$2602(ImageLoader.this, l);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { paramAnonymousString, Float.valueOf(paramAnonymousFloat) });
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
          ImageLoader.access$2602(ImageLoader.this, l);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileUploadProgressChanged, new Object[] { paramAnonymousString, Float.valueOf(paramAnonymousFloat), Boolean.valueOf(paramAnonymousBoolean) });
            }
          });
        }
      }
    });
    Object localObject1 = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        FileLog.e("tmessages", "file system changed");
        paramAnonymousContext = new Runnable()
        {
          public void run()
          {
            ImageLoader.this.checkMediaPaths();
          }
        };
        if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramAnonymousIntent.getAction()))
        {
          AndroidUtilities.runOnUIThread(paramAnonymousContext, 1000L);
          return;
        }
        paramAnonymousContext.run();
      }
    };
    Object localObject2 = new IntentFilter();
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_CHECKING");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_EJECT");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_MOUNTED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_NOFS");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_REMOVED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_SHARED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_UNMOUNTED");
    ((IntentFilter)localObject2).addDataScheme("file");
    ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject1, (IntentFilter)localObject2);
    localObject1 = new HashMap();
    localObject2 = AndroidUtilities.getCacheDir();
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
          new File((File)localObject2, ".nomedia").createNewFile();
          ((HashMap)localObject1).put(Integer.valueOf(4), localObject2);
          FileLoader.getInstance().setMediaDirs((HashMap)localObject1);
          checkMediaPaths();
          return;
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  /* Error */
  private boolean canMoveFiles(File paramFile1, File paramFile2, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 6
    //   9: aconst_null
    //   10: astore 5
    //   12: iload_3
    //   13: ifne +143 -> 156
    //   16: aload 8
    //   18: astore 7
    //   20: new 318	java/io/File
    //   23: dup
    //   24: aload_1
    //   25: ldc_w 456
    //   28: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   31: astore 5
    //   33: aload 8
    //   35: astore 7
    //   37: aload 5
    //   39: astore_1
    //   40: new 318	java/io/File
    //   43: dup
    //   44: aload_2
    //   45: ldc_w 458
    //   48: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   51: astore_1
    //   52: aload 5
    //   54: astore 6
    //   56: aload_1
    //   57: astore 5
    //   59: aload 8
    //   61: astore 7
    //   63: sipush 1024
    //   66: newarray <illegal type>
    //   68: astore_2
    //   69: aload 8
    //   71: astore 7
    //   73: aload 6
    //   75: invokevirtual 333	java/io/File:createNewFile	()Z
    //   78: pop
    //   79: aload 8
    //   81: astore 7
    //   83: new 460	java/io/RandomAccessFile
    //   86: dup
    //   87: aload 6
    //   89: ldc_w 462
    //   92: invokespecial 463	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   95: astore_1
    //   96: aload_1
    //   97: aload_2
    //   98: invokevirtual 467	java/io/RandomAccessFile:write	([B)V
    //   101: aload_1
    //   102: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   105: aconst_null
    //   106: astore_1
    //   107: aload_1
    //   108: astore 7
    //   110: aload 6
    //   112: aload 5
    //   114: invokevirtual 474	java/io/File:renameTo	(Ljava/io/File;)Z
    //   117: istore 4
    //   119: aload_1
    //   120: astore 7
    //   122: aload 6
    //   124: invokevirtual 477	java/io/File:delete	()Z
    //   127: pop
    //   128: aload_1
    //   129: astore 7
    //   131: aload 5
    //   133: invokevirtual 477	java/io/File:delete	()Z
    //   136: pop
    //   137: iload 4
    //   139: ifeq +180 -> 319
    //   142: iconst_0
    //   143: ifeq +11 -> 154
    //   146: new 479	java/lang/NullPointerException
    //   149: dup
    //   150: invokespecial 480	java/lang/NullPointerException:<init>	()V
    //   153: athrow
    //   154: iconst_1
    //   155: ireturn
    //   156: iload_3
    //   157: iconst_3
    //   158: if_icmpne +49 -> 207
    //   161: aload 8
    //   163: astore 7
    //   165: new 318	java/io/File
    //   168: dup
    //   169: aload_1
    //   170: ldc_w 482
    //   173: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   176: astore 5
    //   178: aload 8
    //   180: astore 7
    //   182: aload 5
    //   184: astore_1
    //   185: new 318	java/io/File
    //   188: dup
    //   189: aload_2
    //   190: ldc_w 484
    //   193: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   196: astore_1
    //   197: aload 5
    //   199: astore 6
    //   201: aload_1
    //   202: astore 5
    //   204: goto -145 -> 59
    //   207: iload_3
    //   208: iconst_1
    //   209: if_icmpne +49 -> 258
    //   212: aload 8
    //   214: astore 7
    //   216: new 318	java/io/File
    //   219: dup
    //   220: aload_1
    //   221: ldc_w 486
    //   224: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   227: astore 5
    //   229: aload 8
    //   231: astore 7
    //   233: aload 5
    //   235: astore_1
    //   236: new 318	java/io/File
    //   239: dup
    //   240: aload_2
    //   241: ldc_w 488
    //   244: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   247: astore_1
    //   248: aload 5
    //   250: astore 6
    //   252: aload_1
    //   253: astore 5
    //   255: goto -196 -> 59
    //   258: iload_3
    //   259: iconst_2
    //   260: if_icmpne -201 -> 59
    //   263: aload 8
    //   265: astore 7
    //   267: new 318	java/io/File
    //   270: dup
    //   271: aload_1
    //   272: ldc_w 490
    //   275: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   278: astore 5
    //   280: aload 8
    //   282: astore 7
    //   284: aload 5
    //   286: astore_1
    //   287: new 318	java/io/File
    //   290: dup
    //   291: aload_2
    //   292: ldc_w 492
    //   295: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   298: astore_1
    //   299: aload 5
    //   301: astore 6
    //   303: aload_1
    //   304: astore 5
    //   306: goto -247 -> 59
    //   309: astore_1
    //   310: ldc_w 352
    //   313: aload_1
    //   314: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   317: iconst_1
    //   318: ireturn
    //   319: iconst_0
    //   320: ifeq +11 -> 331
    //   323: new 479	java/lang/NullPointerException
    //   326: dup
    //   327: invokespecial 480	java/lang/NullPointerException:<init>	()V
    //   330: athrow
    //   331: iconst_0
    //   332: ireturn
    //   333: astore_1
    //   334: ldc_w 352
    //   337: aload_1
    //   338: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   341: goto -10 -> 331
    //   344: aconst_null
    //   345: astore_1
    //   346: astore_2
    //   347: aload_1
    //   348: astore 7
    //   350: ldc_w 352
    //   353: aload_2
    //   354: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   357: aload_1
    //   358: ifnull -27 -> 331
    //   361: aload_1
    //   362: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   365: goto -34 -> 331
    //   368: astore_1
    //   369: ldc_w 352
    //   372: aload_1
    //   373: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   376: goto -45 -> 331
    //   379: astore_1
    //   380: aload 7
    //   382: ifnull +8 -> 390
    //   385: aload 7
    //   387: invokevirtual 470	java/io/RandomAccessFile:close	()V
    //   390: aload_1
    //   391: athrow
    //   392: astore_2
    //   393: ldc_w 352
    //   396: aload_2
    //   397: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   400: goto -10 -> 390
    //   403: astore_2
    //   404: aload_1
    //   405: astore 7
    //   407: aload_2
    //   408: astore_1
    //   409: goto -29 -> 380
    //   412: astore_2
    //   413: aload 9
    //   415: astore_1
    //   416: goto -69 -> 347
    //   419: astore_2
    //   420: goto -73 -> 347
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	423	0	this	ImageLoader
    //   0	423	1	paramFile1	File
    //   0	423	2	paramFile2	File
    //   0	423	3	paramInt	int
    //   117	21	4	bool	boolean
    //   10	295	5	localFile	File
    //   7	295	6	localObject1	Object
    //   18	388	7	localObject2	Object
    //   1	280	8	localObject3	Object
    //   4	410	9	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   146	154	309	java/lang/Exception
    //   323	331	333	java/lang/Exception
    //   20	33	344	java/lang/Exception
    //   63	69	344	java/lang/Exception
    //   73	79	344	java/lang/Exception
    //   83	96	344	java/lang/Exception
    //   110	119	344	java/lang/Exception
    //   122	128	344	java/lang/Exception
    //   131	137	344	java/lang/Exception
    //   165	178	344	java/lang/Exception
    //   216	229	344	java/lang/Exception
    //   267	280	344	java/lang/Exception
    //   361	365	368	java/lang/Exception
    //   20	33	379	finally
    //   40	52	379	finally
    //   63	69	379	finally
    //   73	79	379	finally
    //   83	96	379	finally
    //   110	119	379	finally
    //   122	128	379	finally
    //   131	137	379	finally
    //   165	178	379	finally
    //   185	197	379	finally
    //   216	229	379	finally
    //   236	248	379	finally
    //   267	280	379	finally
    //   287	299	379	finally
    //   350	357	379	finally
    //   385	390	392	java/lang/Exception
    //   96	105	403	finally
    //   40	52	412	java/lang/Exception
    //   185	197	412	java/lang/Exception
    //   236	248	412	java/lang/Exception
    //   287	299	412	java/lang/Exception
    //   96	105	419	java/lang/Exception
  }
  
  private void createLoadOperationForImageReceiver(final ImageReceiver paramImageReceiver, final String paramString1, final String paramString2, final String paramString3, final TLObject paramTLObject, final String paramString4, final String paramString5, final int paramInt1, final boolean paramBoolean, final int paramInt2)
  {
    if ((paramImageReceiver == null) || (paramString2 == null) || (paramString1 == null)) {
      return;
    }
    final Object localObject2;
    final Object localObject1;
    if (paramInt2 != 0)
    {
      bool1 = true;
      localObject2 = paramImageReceiver.getTag(bool1);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = Integer.valueOf(this.lastImageNum);
        if (paramInt2 == 0) {
          break label161;
        }
      }
    }
    label161:
    for (final boolean bool1 = true;; bool1 = false)
    {
      paramImageReceiver.setTag((Integer)localObject2, bool1);
      this.lastImageNum += 1;
      localObject1 = localObject2;
      if (this.lastImageNum == Integer.MAX_VALUE)
      {
        this.lastImageNum = 0;
        localObject1 = localObject2;
      }
      bool1 = paramImageReceiver.isNeedsQualityThumb();
      localObject2 = paramImageReceiver.getParentMessageObject();
      final boolean bool2 = paramImageReceiver.isShouldGenerateQualityThumb();
      this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          int j = 0;
          Object localObject2;
          Object localObject3;
          if (paramInt2 != 2)
          {
            localObject1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString2);
            localObject2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(paramString1);
            localObject3 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(localObject1);
            i = j;
            if (localObject3 != null)
            {
              if ((localObject3 != localObject1) && (localObject3 != localObject2)) {
                break label553;
              }
              i = 1;
            }
            j = i;
            if (i == 0)
            {
              j = i;
              if (localObject2 != null)
              {
                ((ImageLoader.CacheImage)localObject2).addImageReceiver(paramImageReceiver, paramString1, paramString5);
                j = 1;
              }
            }
            i = j;
            if (j == 0)
            {
              i = j;
              if (localObject1 != null)
              {
                ((ImageLoader.CacheImage)localObject1).addImageReceiver(paramImageReceiver, paramString1, paramString5);
                i = 1;
              }
            }
          }
          int k;
          label250:
          label406:
          boolean bool;
          if (i == 0)
          {
            j = 0;
            localObject3 = null;
            localObject2 = null;
            if (paramString4 == null) {
              break label643;
            }
            localObject1 = localObject2;
            i = j;
            if (!paramString4.startsWith("http"))
            {
              j = 1;
              if (!paramString4.startsWith("thumb://")) {
                break label567;
              }
              k = paramString4.indexOf(":", 8);
              localObject1 = localObject2;
              i = j;
              if (k >= 0)
              {
                localObject1 = new File(paramString4.substring(k + 1));
                i = j;
              }
            }
            if (paramInt2 != 2)
            {
              localObject3 = new ImageLoader.CacheImage(ImageLoader.this, null);
              if (((paramString4 != null) && (!paramString4.startsWith("vthumb")) && (!paramString4.startsWith("thumb")) && ((paramString4.endsWith("mp4")) || (paramString4.endsWith("gif")))) || (((paramTLObject instanceof TLRPC.Document)) && (MessageObject.isGifDocument((TLRPC.Document)paramTLObject)))) {
                ((ImageLoader.CacheImage)localObject3).animatedFile = true;
              }
              localObject2 = localObject1;
              if (localObject1 == null)
              {
                if ((!paramBoolean) && (paramInt1 != 0) && (paramString4 == null)) {
                  break label1013;
                }
                localObject2 = new File(FileLoader.getInstance().getDirectory(4), paramString2);
              }
              if (paramInt2 == 0) {
                break label1069;
              }
              bool = true;
              label416:
              ((ImageLoader.CacheImage)localObject3).thumb = bool;
              ((ImageLoader.CacheImage)localObject3).key = paramString1;
              ((ImageLoader.CacheImage)localObject3).filter = paramString5;
              ((ImageLoader.CacheImage)localObject3).httpUrl = paramString4;
              ((ImageLoader.CacheImage)localObject3).ext = paramString3;
              ((ImageLoader.CacheImage)localObject3).addImageReceiver(paramImageReceiver, paramString1, paramString5);
              if ((i == 0) && (!((File)localObject2).exists())) {
                break label1091;
              }
              ((ImageLoader.CacheImage)localObject3).finalFilePath = ((File)localObject2);
              ((ImageLoader.CacheImage)localObject3).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject3);
              ImageLoader.this.imageLoadingByKeys.put(paramString1, localObject3);
              if (paramInt2 == 0) {
                break label1075;
              }
              ImageLoader.this.cacheThumbOutQueue.postRunnable(((ImageLoader.CacheImage)localObject3).cacheTask);
            }
          }
          label553:
          label567:
          label643:
          label1013:
          label1069:
          label1075:
          label1091:
          do
          {
            return;
            ((ImageLoader.CacheImage)localObject3).removeImageReceiver(paramImageReceiver);
            i = j;
            break;
            if (paramString4.startsWith("vthumb://"))
            {
              k = paramString4.indexOf(":", 9);
              localObject1 = localObject2;
              i = j;
              if (k < 0) {
                break label250;
              }
              localObject1 = new File(paramString4.substring(k + 1));
              i = j;
              break label250;
            }
            localObject1 = new File(paramString4);
            i = j;
            break label250;
            localObject1 = localObject2;
            i = j;
            if (paramInt2 == 0) {
              break label250;
            }
            localObject2 = localObject3;
            if (bool1)
            {
              localObject1 = new File(FileLoader.getInstance().getDirectory(4), "q_" + paramString2);
              localObject2 = localObject1;
              if (!((File)localObject1).exists()) {
                localObject2 = null;
              }
            }
            localObject1 = localObject2;
            i = j;
            if (localObject2 == null) {
              break label250;
            }
            localObject3 = null;
            localObject1 = localObject3;
            if (localObject2.messageOwner.attachPath != null)
            {
              localObject1 = localObject3;
              if (localObject2.messageOwner.attachPath.length() > 0)
              {
                localObject3 = new File(localObject2.messageOwner.attachPath);
                localObject1 = localObject3;
                if (!((File)localObject3).exists()) {
                  localObject1 = null;
                }
              }
            }
            localObject3 = localObject1;
            if (localObject1 == null) {
              localObject3 = FileLoader.getPathToMessage(localObject2.messageOwner);
            }
            if ((bool1) && (localObject2 == null))
            {
              String str = localObject2.getFileName();
              ImageLoader.ThumbGenerateInfo localThumbGenerateInfo = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(str);
              localObject1 = localThumbGenerateInfo;
              if (localThumbGenerateInfo == null)
              {
                localObject1 = new ImageLoader.ThumbGenerateInfo(ImageLoader.this, null);
                ImageLoader.ThumbGenerateInfo.access$3402((ImageLoader.ThumbGenerateInfo)localObject1, (TLRPC.TL_fileLocation)paramTLObject);
                ImageLoader.ThumbGenerateInfo.access$3502((ImageLoader.ThumbGenerateInfo)localObject1, paramString5);
                ImageLoader.this.waitingForQualityThumb.put(str, localObject1);
              }
              ImageLoader.ThumbGenerateInfo.access$2908((ImageLoader.ThumbGenerateInfo)localObject1);
              ImageLoader.this.waitingForQualityThumbByTag.put(localObject1, str);
            }
            localObject1 = localObject2;
            i = j;
            if (!((File)localObject3).exists()) {
              break label250;
            }
            localObject1 = localObject2;
            i = j;
            if (!bool2) {
              break label250;
            }
            ImageLoader.this.generateThumb(localObject2.getFileType(), (File)localObject3, (TLRPC.TL_fileLocation)paramTLObject, paramString5);
            localObject1 = localObject2;
            i = j;
            break label250;
            if ((paramTLObject instanceof TLRPC.Document))
            {
              localObject2 = new File(FileLoader.getInstance().getDirectory(3), paramString2);
              break label406;
            }
            localObject2 = new File(FileLoader.getInstance().getDirectory(0), paramString2);
            break label406;
            bool = false;
            break label416;
            ImageLoader.this.cacheOutQueue.postRunnable(((ImageLoader.CacheImage)localObject3).cacheTask);
            return;
            ((ImageLoader.CacheImage)localObject3).url = paramString2;
            ((ImageLoader.CacheImage)localObject3).location = paramTLObject;
            ImageLoader.this.imageLoadingByUrl.put(paramString2, localObject3);
            if (paramString4 != null) {
              break label1241;
            }
            if ((paramTLObject instanceof TLRPC.FileLocation))
            {
              localObject1 = (TLRPC.FileLocation)paramTLObject;
              localObject2 = FileLoader.getInstance();
              localObject3 = paramString3;
              i = paramInt1;
              if ((paramInt1 == 0) || (((TLRPC.FileLocation)localObject1).key != null) || (paramBoolean)) {}
              for (bool = true;; bool = false)
              {
                ((FileLoader)localObject2).loadFile((TLRPC.FileLocation)localObject1, (String)localObject3, i, bool);
                return;
              }
            }
          } while (!(paramTLObject instanceof TLRPC.Document));
          FileLoader.getInstance().loadFile((TLRPC.Document)paramTLObject, true, paramBoolean);
          return;
          label1241:
          Object localObject1 = Utilities.MD5(paramString4);
          ((ImageLoader.CacheImage)localObject3).tempFilePath = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1 + "_temp.jpg");
          ((ImageLoader.CacheImage)localObject3).finalFilePath = ((File)localObject2);
          ((ImageLoader.CacheImage)localObject3).httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, (ImageLoader.CacheImage)localObject3, paramInt1);
          ImageLoader.this.httpTasks.add(((ImageLoader.CacheImage)localObject3).httpTask);
          ImageLoader.this.runHttpTasks(false);
        }
      });
      return;
      bool1 = false;
      break;
    }
  }
  
  private void fileDidFailedLoad(final String paramString, int paramInt)
  {
    if (paramInt == 1) {
      return;
    }
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
  
  private void fileDidLoaded(final String paramString, final File paramFile, final int paramInt)
  {
    this.imageLoadQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(paramString);
        if (localObject != null)
        {
          ImageLoader.this.generateThumb(paramInt, paramFile, ImageLoader.ThumbGenerateInfo.access$3400((ImageLoader.ThumbGenerateInfo)localObject), ImageLoader.ThumbGenerateInfo.access$3500((ImageLoader.ThumbGenerateInfo)localObject));
          ImageLoader.this.waitingForQualityThumb.remove(paramString);
        }
        ImageLoader.CacheImage localCacheImage2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString);
        if (localCacheImage2 == null) {
          return;
        }
        ImageLoader.this.imageLoadingByUrl.remove(paramString);
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        while (i < localCacheImage2.imageReceiverArray.size())
        {
          String str1 = (String)localCacheImage2.keys.get(i);
          String str2 = (String)localCacheImage2.filters.get(i);
          ImageReceiver localImageReceiver = (ImageReceiver)localCacheImage2.imageReceiverArray.get(i);
          ImageLoader.CacheImage localCacheImage1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(str1);
          localObject = localCacheImage1;
          if (localCacheImage1 == null)
          {
            localObject = new ImageLoader.CacheImage(ImageLoader.this, null);
            ((ImageLoader.CacheImage)localObject).finalFilePath = paramFile;
            ((ImageLoader.CacheImage)localObject).key = str1;
            ((ImageLoader.CacheImage)localObject).httpUrl = localCacheImage2.httpUrl;
            ((ImageLoader.CacheImage)localObject).thumb = localCacheImage2.thumb;
            ((ImageLoader.CacheImage)localObject).ext = localCacheImage2.ext;
            ((ImageLoader.CacheImage)localObject).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject);
            ((ImageLoader.CacheImage)localObject).filter = str2;
            ((ImageLoader.CacheImage)localObject).animatedFile = localCacheImage2.animatedFile;
            ImageLoader.this.imageLoadingByKeys.put(str1, localObject);
            localArrayList.add(((ImageLoader.CacheImage)localObject).cacheTask);
          }
          ((ImageLoader.CacheImage)localObject).addImageReceiver(localImageReceiver, str1, str2);
          i += 1;
        }
        i = 0;
        label315:
        if (i < localArrayList.size())
        {
          if (!localCacheImage2.thumb) {
            break label358;
          }
          ImageLoader.this.cacheThumbOutQueue.postRunnable((Runnable)localArrayList.get(i));
        }
        for (;;)
        {
          i += 1;
          break label315;
          break;
          label358:
          ImageLoader.this.cacheOutQueue.postRunnable((Runnable)localArrayList.get(i));
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
      Object localObject = FileLoader.getPathToAttach(paramPhotoSize, true);
      try
      {
        localObject = new RandomAccessFile((File)localObject, "r");
        if ((int)((RandomAccessFile)localObject).length() < 20000)
        {
          paramPhotoSize.bytes = new byte[(int)((RandomAccessFile)localObject).length()];
          ((RandomAccessFile)localObject).readFully(paramPhotoSize.bytes, 0, paramPhotoSize.bytes.length);
          return;
        }
      }
      catch (Throwable paramPhotoSize)
      {
        FileLog.e("tmessages", paramPhotoSize);
      }
    }
  }
  
  private void generateThumb(int paramInt, File paramFile, TLRPC.FileLocation paramFileLocation, String paramString)
  {
    if (((paramInt != 0) && (paramInt != 2) && (paramInt != 3)) || (paramFile == null) || (paramFileLocation == null)) {}
    String str;
    do
    {
      return;
      str = FileLoader.getAttachFileName(paramFileLocation);
    } while ((ThumbGenerateTask)this.thumbGenerateTasks.get(str) != null);
    paramFile = new ThumbGenerateTask(paramInt, paramFile, paramFileLocation, paramString);
    this.thumbGeneratingQueue.postRunnable(paramFile);
  }
  
  public static String getHttpUrlExtension(String paramString1, String paramString2)
  {
    String str = null;
    int i = paramString1.lastIndexOf('.');
    if (i != -1) {
      str = paramString1.substring(i + 1);
    }
    if ((str != null) && (str.length() != 0))
    {
      paramString1 = str;
      if (str.length() <= 4) {}
    }
    else
    {
      paramString1 = paramString2;
    }
    return paramString1;
  }
  
  public static ImageLoader getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          ImageLoader localImageLoader2 = Instance;
          localObject1 = localImageLoader2;
          if (localImageLoader2 == null) {
            localObject1 = new ImageLoader();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (ImageLoader)localObject1;
          return (ImageLoader)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localImageLoader1;
  }
  
  private void httpFileLoadError(final String paramString)
  {
    this.imageLoadQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ImageLoader.CacheImage localCacheImage = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(paramString);
        if (localCacheImage == null) {
          return;
        }
        ImageLoader.HttpImageTask localHttpImageTask = localCacheImage.httpTask;
        localCacheImage.httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, ImageLoader.HttpImageTask.access$300(localHttpImageTask), ImageLoader.HttpImageTask.access$3900(localHttpImageTask));
        ImageLoader.this.httpTasks.add(localCacheImage.httpTask);
        ImageLoader.this.runHttpTasks(false);
      }
    });
  }
  
  /* Error */
  public static Bitmap loadBitmap(String paramString, android.net.Uri paramUri, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    // Byte code:
    //   0: new 579	android/graphics/BitmapFactory$Options
    //   3: dup
    //   4: invokespecial 580	android/graphics/BitmapFactory$Options:<init>	()V
    //   7: astore 13
    //   9: aload 13
    //   11: iconst_1
    //   12: putfield 584	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   15: aconst_null
    //   16: astore 10
    //   18: aload_0
    //   19: astore 8
    //   21: aload_0
    //   22: ifnonnull +39 -> 61
    //   25: aload_0
    //   26: astore 8
    //   28: aload_1
    //   29: ifnull +32 -> 61
    //   32: aload_0
    //   33: astore 8
    //   35: aload_1
    //   36: invokevirtual 590	android/net/Uri:getScheme	()Ljava/lang/String;
    //   39: ifnull +22 -> 61
    //   42: aload_1
    //   43: invokevirtual 590	android/net/Uri:getScheme	()Ljava/lang/String;
    //   46: ldc_w 303
    //   49: invokevirtual 594	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   52: ifeq +270 -> 322
    //   55: aload_1
    //   56: invokevirtual 597	android/net/Uri:getPath	()Ljava/lang/String;
    //   59: astore 8
    //   61: aload 8
    //   63: ifnull +284 -> 347
    //   66: aload 8
    //   68: aload 13
    //   70: invokestatic 603	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   73: pop
    //   74: aload 13
    //   76: getfield 606	android/graphics/BitmapFactory$Options:outWidth	I
    //   79: i2f
    //   80: fstore 5
    //   82: aload 13
    //   84: getfield 609	android/graphics/BitmapFactory$Options:outHeight	I
    //   87: i2f
    //   88: fstore 6
    //   90: iload 4
    //   92: ifeq +307 -> 399
    //   95: fload 5
    //   97: fload_2
    //   98: fdiv
    //   99: fload 6
    //   101: fload_3
    //   102: fdiv
    //   103: invokestatic 613	java/lang/Math:max	(FF)F
    //   106: fstore_2
    //   107: fload_2
    //   108: fstore_3
    //   109: fload_2
    //   110: fconst_1
    //   111: fcmpg
    //   112: ifge +5 -> 117
    //   115: fconst_1
    //   116: fstore_3
    //   117: aload 13
    //   119: iconst_0
    //   120: putfield 584	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   123: aload 13
    //   125: fload_3
    //   126: f2i
    //   127: putfield 616	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   130: getstatic 621	android/os/Build$VERSION:SDK_INT	I
    //   133: bipush 21
    //   135: if_icmpge +279 -> 414
    //   138: iconst_1
    //   139: istore 4
    //   141: aload 13
    //   143: iload 4
    //   145: putfield 624	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   148: aconst_null
    //   149: astore_0
    //   150: aload 8
    //   152: ifnull +268 -> 420
    //   155: aload 8
    //   157: astore_0
    //   158: aconst_null
    //   159: astore 11
    //   161: aconst_null
    //   162: astore 9
    //   164: aload_0
    //   165: ifnull +70 -> 235
    //   168: new 626	android/media/ExifInterface
    //   171: dup
    //   172: aload_0
    //   173: invokespecial 627	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   176: ldc_w 629
    //   179: iconst_1
    //   180: invokevirtual 633	android/media/ExifInterface:getAttributeInt	(Ljava/lang/String;I)I
    //   183: istore 7
    //   185: new 635	android/graphics/Matrix
    //   188: dup
    //   189: invokespecial 636	android/graphics/Matrix:<init>	()V
    //   192: astore_0
    //   193: iload 7
    //   195: tableswitch	default:+37->232, 3:+264->459, 4:+37->232, 5:+37->232, 6:+237->432, 7:+37->232, 8:+275->470
    //   232: aload_0
    //   233: astore 9
    //   235: aconst_null
    //   236: astore 12
    //   238: aconst_null
    //   239: astore 11
    //   241: aconst_null
    //   242: astore_0
    //   243: aload 8
    //   245: ifnull +353 -> 598
    //   248: aload 12
    //   250: astore_0
    //   251: aload 8
    //   253: aload 13
    //   255: invokestatic 603	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   258: astore_1
    //   259: aload_1
    //   260: astore_0
    //   261: aload_1
    //   262: ifnull +58 -> 320
    //   265: aload_1
    //   266: astore_0
    //   267: aload 13
    //   269: getfield 624	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   272: ifeq +10 -> 282
    //   275: aload_1
    //   276: astore_0
    //   277: aload_1
    //   278: invokestatic 642	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   281: pop
    //   282: aload_1
    //   283: astore_0
    //   284: aload_1
    //   285: iconst_0
    //   286: iconst_0
    //   287: aload_1
    //   288: invokevirtual 647	android/graphics/Bitmap:getWidth	()I
    //   291: aload_1
    //   292: invokevirtual 650	android/graphics/Bitmap:getHeight	()I
    //   295: aload 9
    //   297: iconst_1
    //   298: invokestatic 656	org/telegram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   301: astore 10
    //   303: aload_1
    //   304: astore_0
    //   305: aload 10
    //   307: aload_1
    //   308: if_acmpeq +12 -> 320
    //   311: aload_1
    //   312: astore_0
    //   313: aload_1
    //   314: invokevirtual 659	android/graphics/Bitmap:recycle	()V
    //   317: aload 10
    //   319: astore_0
    //   320: aload_0
    //   321: areturn
    //   322: aload_1
    //   323: invokestatic 662	org/telegram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   326: astore 8
    //   328: goto -267 -> 61
    //   331: astore 8
    //   333: ldc_w 352
    //   336: aload 8
    //   338: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   341: aload_0
    //   342: astore 8
    //   344: goto -283 -> 61
    //   347: aload_1
    //   348: ifnull -274 -> 74
    //   351: getstatic 238	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   354: invokevirtual 666	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   357: aload_1
    //   358: invokevirtual 672	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   361: astore_0
    //   362: aload_0
    //   363: aconst_null
    //   364: aload 13
    //   366: invokestatic 676	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   369: pop
    //   370: aload_0
    //   371: invokevirtual 679	java/io/InputStream:close	()V
    //   374: getstatic 238	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   377: invokevirtual 666	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   380: aload_1
    //   381: invokevirtual 672	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   384: astore 10
    //   386: goto -312 -> 74
    //   389: astore_0
    //   390: ldc_w 352
    //   393: aload_0
    //   394: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   397: aconst_null
    //   398: areturn
    //   399: fload 5
    //   401: fload_2
    //   402: fdiv
    //   403: fload 6
    //   405: fload_3
    //   406: fdiv
    //   407: invokestatic 681	java/lang/Math:min	(FF)F
    //   410: fstore_2
    //   411: goto -304 -> 107
    //   414: iconst_0
    //   415: istore 4
    //   417: goto -276 -> 141
    //   420: aload_1
    //   421: ifnull -263 -> 158
    //   424: aload_1
    //   425: invokestatic 662	org/telegram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   428: astore_0
    //   429: goto -271 -> 158
    //   432: aload_0
    //   433: ldc_w 682
    //   436: invokevirtual 686	android/graphics/Matrix:postRotate	(F)Z
    //   439: pop
    //   440: goto -208 -> 232
    //   443: astore 11
    //   445: aload_0
    //   446: astore 9
    //   448: ldc_w 352
    //   451: aload 11
    //   453: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   456: goto -221 -> 235
    //   459: aload_0
    //   460: ldc_w 687
    //   463: invokevirtual 686	android/graphics/Matrix:postRotate	(F)Z
    //   466: pop
    //   467: goto -235 -> 232
    //   470: aload_0
    //   471: ldc_w 688
    //   474: invokevirtual 686	android/graphics/Matrix:postRotate	(F)Z
    //   477: pop
    //   478: goto -246 -> 232
    //   481: astore_1
    //   482: ldc_w 352
    //   485: aload_1
    //   486: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   489: invokestatic 690	org/telegram/messenger/ImageLoader:getInstance	()Lorg/telegram/messenger/ImageLoader;
    //   492: invokevirtual 693	org/telegram/messenger/ImageLoader:clearMemory	()V
    //   495: aload_0
    //   496: astore_1
    //   497: aload_0
    //   498: ifnonnull +46 -> 544
    //   501: aload 8
    //   503: aload 13
    //   505: invokestatic 603	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   508: astore 8
    //   510: aload 8
    //   512: astore_1
    //   513: aload 8
    //   515: ifnull +29 -> 544
    //   518: aload 8
    //   520: astore_1
    //   521: aload 8
    //   523: astore_0
    //   524: aload 13
    //   526: getfield 624	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   529: ifeq +15 -> 544
    //   532: aload 8
    //   534: astore_0
    //   535: aload 8
    //   537: invokestatic 642	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   540: pop
    //   541: aload 8
    //   543: astore_1
    //   544: aload_1
    //   545: astore_0
    //   546: aload_1
    //   547: ifnull -227 -> 320
    //   550: aload_1
    //   551: astore_0
    //   552: aload_1
    //   553: iconst_0
    //   554: iconst_0
    //   555: aload_1
    //   556: invokevirtual 647	android/graphics/Bitmap:getWidth	()I
    //   559: aload_1
    //   560: invokevirtual 650	android/graphics/Bitmap:getHeight	()I
    //   563: aload 9
    //   565: iconst_1
    //   566: invokestatic 656	org/telegram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   569: astore 8
    //   571: aload_1
    //   572: astore_0
    //   573: aload 8
    //   575: aload_1
    //   576: if_acmpeq -256 -> 320
    //   579: aload_1
    //   580: astore_0
    //   581: aload_1
    //   582: invokevirtual 659	android/graphics/Bitmap:recycle	()V
    //   585: aload 8
    //   587: areturn
    //   588: astore_1
    //   589: ldc_w 352
    //   592: aload_1
    //   593: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   596: aload_0
    //   597: areturn
    //   598: aload_1
    //   599: ifnull -279 -> 320
    //   602: aload 11
    //   604: astore_0
    //   605: aload 10
    //   607: aconst_null
    //   608: aload 13
    //   610: invokestatic 676	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   613: astore_1
    //   614: aload_1
    //   615: astore_0
    //   616: aload_1
    //   617: ifnull +58 -> 675
    //   620: aload_1
    //   621: astore_0
    //   622: aload 13
    //   624: getfield 624	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   627: ifeq +10 -> 637
    //   630: aload_1
    //   631: astore_0
    //   632: aload_1
    //   633: invokestatic 642	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   636: pop
    //   637: aload_1
    //   638: astore_0
    //   639: aload_1
    //   640: iconst_0
    //   641: iconst_0
    //   642: aload_1
    //   643: invokevirtual 647	android/graphics/Bitmap:getWidth	()I
    //   646: aload_1
    //   647: invokevirtual 650	android/graphics/Bitmap:getHeight	()I
    //   650: aload 9
    //   652: iconst_1
    //   653: invokestatic 656	org/telegram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   656: astore 8
    //   658: aload_1
    //   659: astore_0
    //   660: aload 8
    //   662: aload_1
    //   663: if_acmpeq +12 -> 675
    //   666: aload_1
    //   667: astore_0
    //   668: aload_1
    //   669: invokevirtual 659	android/graphics/Bitmap:recycle	()V
    //   672: aload 8
    //   674: astore_0
    //   675: aload 10
    //   677: invokevirtual 679	java/io/InputStream:close	()V
    //   680: aload_0
    //   681: areturn
    //   682: astore_1
    //   683: ldc_w 352
    //   686: aload_1
    //   687: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   690: aload_0
    //   691: areturn
    //   692: astore_1
    //   693: ldc_w 352
    //   696: aload_1
    //   697: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   700: aload 10
    //   702: invokevirtual 679	java/io/InputStream:close	()V
    //   705: aload_0
    //   706: areturn
    //   707: astore_1
    //   708: ldc_w 352
    //   711: aload_1
    //   712: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   715: aload_0
    //   716: areturn
    //   717: astore_0
    //   718: aload 10
    //   720: invokevirtual 679	java/io/InputStream:close	()V
    //   723: aload_0
    //   724: athrow
    //   725: astore_1
    //   726: ldc_w 352
    //   729: aload_1
    //   730: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   733: goto -10 -> 723
    //   736: astore_0
    //   737: aload 11
    //   739: astore 9
    //   741: aload_0
    //   742: astore 11
    //   744: goto -296 -> 448
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	747	0	paramString	String
    //   0	747	1	paramUri	android.net.Uri
    //   0	747	2	paramFloat1	float
    //   0	747	3	paramFloat2	float
    //   0	747	4	paramBoolean	boolean
    //   80	320	5	f1	float
    //   88	316	6	f2	float
    //   183	11	7	i	int
    //   19	308	8	str1	String
    //   331	6	8	localThrowable1	Throwable
    //   342	331	8	localObject1	Object
    //   162	578	9	localObject2	Object
    //   16	703	10	localObject3	Object
    //   159	81	11	localObject4	Object
    //   443	295	11	localThrowable2	Throwable
    //   742	1	11	str2	String
    //   236	13	12	localObject5	Object
    //   7	616	13	localOptions	android.graphics.BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   322	328	331	java/lang/Throwable
    //   351	386	389	java/lang/Throwable
    //   432	440	443	java/lang/Throwable
    //   459	467	443	java/lang/Throwable
    //   470	478	443	java/lang/Throwable
    //   251	259	481	java/lang/Throwable
    //   267	275	481	java/lang/Throwable
    //   277	282	481	java/lang/Throwable
    //   284	303	481	java/lang/Throwable
    //   313	317	481	java/lang/Throwable
    //   501	510	588	java/lang/Throwable
    //   524	532	588	java/lang/Throwable
    //   535	541	588	java/lang/Throwable
    //   552	571	588	java/lang/Throwable
    //   581	585	588	java/lang/Throwable
    //   675	680	682	java/lang/Throwable
    //   605	614	692	java/lang/Throwable
    //   622	630	692	java/lang/Throwable
    //   632	637	692	java/lang/Throwable
    //   639	658	692	java/lang/Throwable
    //   668	672	692	java/lang/Throwable
    //   700	705	707	java/lang/Throwable
    //   605	614	717	finally
    //   622	630	717	finally
    //   632	637	717	finally
    //   639	658	717	finally
    //   668	672	717	finally
    //   693	700	717	finally
    //   718	723	725	java/lang/Throwable
    //   168	193	736	java/lang/Throwable
  }
  
  private void performReplace(String paramString1, String paramString2)
  {
    Object localObject = this.memCache.get(paramString1);
    if (localObject != null)
    {
      this.ignoreRemoval = paramString1;
      this.memCache.remove(paramString1);
      this.memCache.put(paramString2, (BitmapDrawable)localObject);
      this.ignoreRemoval = null;
    }
    localObject = (Integer)this.bitmapUseCounts.get(paramString1);
    if (localObject != null)
    {
      this.bitmapUseCounts.put(paramString2, localObject);
      this.bitmapUseCounts.remove(paramString1);
    }
  }
  
  private void removeFromWaitingForThumb(Integer paramInteger)
  {
    String str = (String)this.waitingForQualityThumbByTag.get(paramInteger);
    if (str != null)
    {
      ThumbGenerateInfo localThumbGenerateInfo = (ThumbGenerateInfo)this.waitingForQualityThumb.get(str);
      if (localThumbGenerateInfo != null)
      {
        ThumbGenerateInfo.access$2910(localThumbGenerateInfo);
        if (localThumbGenerateInfo.count == 0) {
          this.waitingForQualityThumb.remove(str);
        }
      }
      this.waitingForQualityThumbByTag.remove(paramInteger);
    }
  }
  
  private void replaceImageInCacheInternal(String paramString1, String paramString2, TLRPC.FileLocation paramFileLocation)
  {
    ArrayList localArrayList = this.memCache.getFilterKeys(paramString1);
    if (localArrayList != null)
    {
      int i = 0;
      while (i < localArrayList.size())
      {
        String str2 = (String)localArrayList.get(i);
        String str1 = paramString1 + "@" + str2;
        str2 = paramString2 + "@" + str2;
        performReplace(str1, str2);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { str1, str2, paramFileLocation });
        i += 1;
      }
    }
    performReplace(paramString1, paramString2);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { paramString1, paramString2, paramFileLocation });
  }
  
  private void runHttpFileLoadTasks(final HttpFileTask paramHttpFileTask, final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramHttpFileTask != null) {
          ImageLoader.access$4010(ImageLoader.this);
        }
        Object localObject;
        if (paramHttpFileTask != null)
        {
          if (paramInt != 1) {
            break label229;
          }
          if (!ImageLoader.HttpFileTask.access$4100(paramHttpFileTask)) {
            break label178;
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
          ImageLoader.access$4008(ImageLoader.this);
          continue;
          label178:
          ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(paramHttpFileTask));
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidFailedLoad, new Object[] { ImageLoader.HttpFileTask.access$000(paramHttpFileTask), Integer.valueOf(0) });
          continue;
          label229:
          if (paramInt == 2)
          {
            ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(paramHttpFileTask));
            localObject = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(ImageLoader.HttpFileTask.access$000(paramHttpFileTask)) + "." + ImageLoader.HttpFileTask.access$4300(paramHttpFileTask));
            if (ImageLoader.HttpFileTask.access$4200(paramHttpFileTask).renameTo((File)localObject)) {}
            for (localObject = ((File)localObject).toString();; localObject = ImageLoader.HttpFileTask.access$4200(paramHttpFileTask).toString())
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidLoaded, new Object[] { ImageLoader.HttpFileTask.access$000(paramHttpFileTask), localObject });
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
    Object localObject2 = null;
    Iterator localIterator;
    Object localObject1;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      localIterator = paramMessage.media.photo.sizes.iterator();
      do
      {
        localObject1 = localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = (TLRPC.PhotoSize)localIterator.next();
      } while (!(localObject1 instanceof TLRPC.TL_photoCachedSize));
      if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).bytes != null) && (((TLRPC.PhotoSize)localObject1).bytes.length != 0))
      {
        if ((((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable))
        {
          ((TLRPC.PhotoSize)localObject1).location = new TLRPC.TL_fileLocation();
          ((TLRPC.PhotoSize)localObject1).location.volume_id = -2147483648L;
          ((TLRPC.PhotoSize)localObject1).location.dc_id = Integer.MIN_VALUE;
          ((TLRPC.PhotoSize)localObject1).location.local_id = UserConfig.lastLocalId;
          UserConfig.lastLocalId -= 1;
        }
        localObject2 = FileLoader.getPathToAttach((TLObject)localObject1, true);
        if (((File)localObject2).exists()) {}
      }
    }
    for (;;)
    {
      int i;
      try
      {
        localObject2 = new RandomAccessFile((File)localObject2, "rws");
        ((RandomAccessFile)localObject2).write(((TLRPC.PhotoSize)localObject1).bytes);
        ((RandomAccessFile)localObject2).close();
        localObject2 = new TLRPC.TL_photoSize();
        ((TLRPC.TL_photoSize)localObject2).w = ((TLRPC.PhotoSize)localObject1).w;
        ((TLRPC.TL_photoSize)localObject2).h = ((TLRPC.PhotoSize)localObject1).h;
        ((TLRPC.TL_photoSize)localObject2).location = ((TLRPC.PhotoSize)localObject1).location;
        ((TLRPC.TL_photoSize)localObject2).size = ((TLRPC.PhotoSize)localObject1).size;
        ((TLRPC.TL_photoSize)localObject2).type = ((TLRPC.PhotoSize)localObject1).type;
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          i = 0;
          if (i < paramMessage.media.photo.sizes.size())
          {
            if ((paramMessage.media.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize)) {
              paramMessage.media.photo.sizes.set(i, localObject2);
            }
          }
          else
          {
            return;
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
            {
              localObject1 = localObject2;
              if (!(paramMessage.media.document.thumb instanceof TLRPC.TL_photoCachedSize)) {
                break;
              }
              localObject1 = paramMessage.media.document.thumb;
              break;
            }
            localObject1 = localObject2;
            if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
              break;
            }
            localObject1 = localObject2;
            if (paramMessage.media.webpage.photo == null) {
              break;
            }
            localIterator = paramMessage.media.webpage.photo.sizes.iterator();
            localObject1 = localObject2;
            if (!localIterator.hasNext()) {
              break;
            }
            localObject1 = (TLRPC.PhotoSize)localIterator.next();
            if (!(localObject1 instanceof TLRPC.TL_photoCachedSize)) {
              continue;
            }
          }
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        continue;
        i += 1;
        continue;
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        {
          paramMessage.media.document.thumb = localException;
          return;
        }
        if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
          continue;
        }
        i = 0;
      }
      while (i < paramMessage.media.webpage.photo.sizes.size())
      {
        if ((paramMessage.media.webpage.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize))
        {
          paramMessage.media.webpage.photo.sizes.set(i, localException);
          return;
        }
        i += 1;
      }
    }
  }
  
  public static void saveMessagesThumbs(ArrayList<TLRPC.Message> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < paramArrayList.size())
      {
        saveMessageThumbs((TLRPC.Message)paramArrayList.get(i));
        i += 1;
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
      return null;
    }
    float f1 = paramBitmap.getWidth();
    float f2 = paramBitmap.getHeight();
    if ((f1 == 0.0F) || (f2 == 0.0F)) {
      return null;
    }
    boolean bool2 = false;
    paramFloat2 = Math.max(f1 / paramFloat1, f2 / paramFloat2);
    paramFloat1 = paramFloat2;
    boolean bool1 = bool2;
    if (paramInt2 != 0)
    {
      paramFloat1 = paramFloat2;
      bool1 = bool2;
      if (paramInt3 != 0) {
        if (f1 >= paramInt2)
        {
          paramFloat1 = paramFloat2;
          bool1 = bool2;
          if (f2 >= paramInt3) {}
        }
        else
        {
          if ((f1 >= paramInt2) || (f2 <= paramInt3)) {
            break label151;
          }
          paramFloat1 = f1 / paramInt2;
        }
      }
    }
    for (;;)
    {
      bool1 = true;
      paramInt2 = (int)(f1 / paramFloat1);
      paramInt3 = (int)(f2 / paramFloat1);
      if ((paramInt3 != 0) && (paramInt2 != 0)) {
        break;
      }
      return null;
      label151:
      if ((f1 > paramInt2) && (f2 < paramInt3)) {
        paramFloat1 = f2 / paramInt3;
      } else {
        paramFloat1 = Math.max(f1 / paramInt2, f2 / paramInt3);
      }
    }
    try
    {
      TLRPC.PhotoSize localPhotoSize = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool1);
      return localPhotoSize;
    }
    catch (Throwable localThrowable)
    {
      FileLog.e("tmessages", localThrowable);
      getInstance().clearMemory();
      System.gc();
      try
      {
        paramBitmap = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool1);
        return paramBitmap;
      }
      catch (Throwable paramBitmap)
      {
        FileLog.e("tmessages", paramBitmap);
      }
    }
    return null;
  }
  
  private static TLRPC.PhotoSize scaleAndSaveImageInternal(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
    throws Exception
  {
    Bitmap localBitmap;
    Object localObject;
    TLRPC.TL_photoSize localTL_photoSize;
    if ((paramFloat3 > 1.0F) || (paramBoolean2))
    {
      localBitmap = Bitmaps.createScaledBitmap(paramBitmap, paramInt1, paramInt2, true);
      localObject = new TLRPC.TL_fileLocation();
      ((TLRPC.TL_fileLocation)localObject).volume_id = -2147483648L;
      ((TLRPC.TL_fileLocation)localObject).dc_id = Integer.MIN_VALUE;
      ((TLRPC.TL_fileLocation)localObject).local_id = UserConfig.lastLocalId;
      UserConfig.lastLocalId -= 1;
      localTL_photoSize = new TLRPC.TL_photoSize();
      localTL_photoSize.location = ((TLRPC.FileLocation)localObject);
      localTL_photoSize.w = localBitmap.getWidth();
      localTL_photoSize.h = localBitmap.getHeight();
      if ((localTL_photoSize.w > 100) || (localTL_photoSize.h > 100)) {
        break label282;
      }
      localTL_photoSize.type = "s";
      label126:
      localObject = ((TLRPC.TL_fileLocation)localObject).volume_id + "_" + ((TLRPC.TL_fileLocation)localObject).local_id + ".jpg";
      localObject = new FileOutputStream(new File(FileLoader.getInstance().getDirectory(4), (String)localObject));
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, (OutputStream)localObject);
      if (!paramBoolean1) {
        break label392;
      }
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, localByteArrayOutputStream);
      localTL_photoSize.bytes = localByteArrayOutputStream.toByteArray();
      localTL_photoSize.size = localTL_photoSize.bytes.length;
      localByteArrayOutputStream.close();
    }
    for (;;)
    {
      ((FileOutputStream)localObject).close();
      if (localBitmap != paramBitmap) {
        localBitmap.recycle();
      }
      return localTL_photoSize;
      localBitmap = paramBitmap;
      break;
      label282:
      if ((localTL_photoSize.w <= 320) && (localTL_photoSize.h <= 320))
      {
        localTL_photoSize.type = "m";
        break label126;
      }
      if ((localTL_photoSize.w <= 800) && (localTL_photoSize.h <= 800))
      {
        localTL_photoSize.type = "x";
        break label126;
      }
      if ((localTL_photoSize.w <= 1280) && (localTL_photoSize.h <= 1280))
      {
        localTL_photoSize.type = "y";
        break label126;
      }
      localTL_photoSize.type = "w";
      break label126;
      label392:
      localTL_photoSize.size = ((int)((FileOutputStream)localObject).getChannel().size());
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
    if (paramImageReceiver == null) {
      return;
    }
    this.imageLoadQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int k = 2;
        int j;
        Object localObject;
        if (paramInt == 1)
        {
          j = 1;
          if (i >= j) {
            return;
          }
          localObject = paramImageReceiver;
          if (i != 0) {
            break label114;
          }
        }
        label114:
        for (boolean bool = true;; bool = false)
        {
          localObject = ((ImageReceiver)localObject).getTag(bool);
          if (i == 0) {
            ImageLoader.this.removeFromWaitingForThumb((Integer)localObject);
          }
          if (localObject != null)
          {
            localObject = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(localObject);
            if (localObject != null) {
              ((ImageLoader.CacheImage)localObject).removeImageReceiver(paramImageReceiver);
            }
          }
          i += 1;
          break;
          j = k;
          if (paramInt != 2) {
            break;
          }
          i = 1;
          j = k;
          break;
        }
      }
    });
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
            FileLoader.getInstance().setMediaDirs(this.val$paths);
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
  public HashMap<Integer, File> createMediaPaths()
  {
    // Byte code:
    //   0: new 166	java/util/HashMap
    //   3: dup
    //   4: invokespecial 167	java/util/HashMap:<init>	()V
    //   7: astore_2
    //   8: invokestatic 316	org/telegram/messenger/AndroidUtilities:getCacheDir	()Ljava/io/File;
    //   11: astore_3
    //   12: aload_3
    //   13: invokevirtual 322	java/io/File:isDirectory	()Z
    //   16: ifne +8 -> 24
    //   19: aload_3
    //   20: invokevirtual 325	java/io/File:mkdirs	()Z
    //   23: pop
    //   24: new 318	java/io/File
    //   27: dup
    //   28: aload_3
    //   29: ldc_w 327
    //   32: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   35: invokevirtual 333	java/io/File:createNewFile	()Z
    //   38: pop
    //   39: aload_2
    //   40: iconst_4
    //   41: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   44: aload_3
    //   45: invokevirtual 343	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   48: pop
    //   49: ldc_w 352
    //   52: new 729	java/lang/StringBuilder
    //   55: dup
    //   56: invokespecial 730	java/lang/StringBuilder:<init>	()V
    //   59: ldc_w 997
    //   62: invokevirtual 734	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: aload_3
    //   66: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   69: invokevirtual 739	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   72: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   75: ldc_w 1004
    //   78: invokestatic 1009	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   81: invokevirtual 1012	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   84: ifeq +482 -> 566
    //   87: aload_0
    //   88: new 318	java/io/File
    //   91: dup
    //   92: invokestatic 1015	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   95: ldc_w 1017
    //   98: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   101: putfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   104: aload_0
    //   105: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   108: invokevirtual 325	java/io/File:mkdirs	()Z
    //   111: pop
    //   112: aload_0
    //   113: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   116: invokevirtual 322	java/io/File:isDirectory	()Z
    //   119: istore_1
    //   120: iload_1
    //   121: ifeq +351 -> 472
    //   124: new 318	java/io/File
    //   127: dup
    //   128: aload_0
    //   129: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   132: ldc_w 1019
    //   135: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   138: astore 4
    //   140: aload 4
    //   142: invokevirtual 1022	java/io/File:mkdir	()Z
    //   145: pop
    //   146: aload 4
    //   148: invokevirtual 322	java/io/File:isDirectory	()Z
    //   151: ifeq +52 -> 203
    //   154: aload_0
    //   155: aload_3
    //   156: aload 4
    //   158: iconst_0
    //   159: invokespecial 1024	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   162: ifeq +41 -> 203
    //   165: aload_2
    //   166: iconst_0
    //   167: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   170: aload 4
    //   172: invokevirtual 343	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   175: pop
    //   176: ldc_w 352
    //   179: new 729	java/lang/StringBuilder
    //   182: dup
    //   183: invokespecial 730	java/lang/StringBuilder:<init>	()V
    //   186: ldc_w 1026
    //   189: invokevirtual 734	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: aload 4
    //   194: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   197: invokevirtual 739	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   200: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   203: new 318	java/io/File
    //   206: dup
    //   207: aload_0
    //   208: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   211: ldc_w 1028
    //   214: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   217: astore 4
    //   219: aload 4
    //   221: invokevirtual 1022	java/io/File:mkdir	()Z
    //   224: pop
    //   225: aload 4
    //   227: invokevirtual 322	java/io/File:isDirectory	()Z
    //   230: ifeq +52 -> 282
    //   233: aload_0
    //   234: aload_3
    //   235: aload 4
    //   237: iconst_2
    //   238: invokespecial 1024	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   241: ifeq +41 -> 282
    //   244: aload_2
    //   245: iconst_2
    //   246: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   249: aload 4
    //   251: invokevirtual 343	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   254: pop
    //   255: ldc_w 352
    //   258: new 729	java/lang/StringBuilder
    //   261: dup
    //   262: invokespecial 730	java/lang/StringBuilder:<init>	()V
    //   265: ldc_w 1030
    //   268: invokevirtual 734	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   271: aload 4
    //   273: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   276: invokevirtual 739	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   279: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   282: new 318	java/io/File
    //   285: dup
    //   286: aload_0
    //   287: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   290: ldc_w 1032
    //   293: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   296: astore 4
    //   298: aload 4
    //   300: invokevirtual 1022	java/io/File:mkdir	()Z
    //   303: pop
    //   304: aload 4
    //   306: invokevirtual 322	java/io/File:isDirectory	()Z
    //   309: ifeq +68 -> 377
    //   312: aload_0
    //   313: aload_3
    //   314: aload 4
    //   316: iconst_1
    //   317: invokespecial 1024	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   320: ifeq +57 -> 377
    //   323: new 318	java/io/File
    //   326: dup
    //   327: aload 4
    //   329: ldc_w 327
    //   332: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   335: invokevirtual 333	java/io/File:createNewFile	()Z
    //   338: pop
    //   339: aload_2
    //   340: iconst_1
    //   341: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   344: aload 4
    //   346: invokevirtual 343	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   349: pop
    //   350: ldc_w 352
    //   353: new 729	java/lang/StringBuilder
    //   356: dup
    //   357: invokespecial 730	java/lang/StringBuilder:<init>	()V
    //   360: ldc_w 1034
    //   363: invokevirtual 734	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: aload 4
    //   368: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   371: invokevirtual 739	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   374: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   377: new 318	java/io/File
    //   380: dup
    //   381: aload_0
    //   382: getfield 228	org/telegram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   385: ldc_w 1036
    //   388: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   391: astore 4
    //   393: aload 4
    //   395: invokevirtual 1022	java/io/File:mkdir	()Z
    //   398: pop
    //   399: aload 4
    //   401: invokevirtual 322	java/io/File:isDirectory	()Z
    //   404: ifeq +68 -> 472
    //   407: aload_0
    //   408: aload_3
    //   409: aload 4
    //   411: iconst_3
    //   412: invokespecial 1024	org/telegram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   415: ifeq +57 -> 472
    //   418: new 318	java/io/File
    //   421: dup
    //   422: aload 4
    //   424: ldc_w 327
    //   427: invokespecial 330	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   430: invokevirtual 333	java/io/File:createNewFile	()Z
    //   433: pop
    //   434: aload_2
    //   435: iconst_3
    //   436: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   439: aload 4
    //   441: invokevirtual 343	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   444: pop
    //   445: ldc_w 352
    //   448: new 729	java/lang/StringBuilder
    //   451: dup
    //   452: invokespecial 730	java/lang/StringBuilder:<init>	()V
    //   455: ldc_w 1038
    //   458: invokevirtual 734	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   461: aload 4
    //   463: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   466: invokevirtual 739	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   469: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   472: invokestatic 1043	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   475: invokevirtual 1046	org/telegram/messenger/MediaController:checkSaveToGalleryFiles	()V
    //   478: aload_2
    //   479: areturn
    //   480: astore 4
    //   482: ldc_w 352
    //   485: aload 4
    //   487: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   490: goto -466 -> 24
    //   493: astore 4
    //   495: ldc_w 352
    //   498: aload 4
    //   500: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   503: goto -464 -> 39
    //   506: astore 4
    //   508: ldc_w 352
    //   511: aload 4
    //   513: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   516: goto -313 -> 203
    //   519: astore_3
    //   520: ldc_w 352
    //   523: aload_3
    //   524: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   527: aload_2
    //   528: areturn
    //   529: astore 4
    //   531: ldc_w 352
    //   534: aload 4
    //   536: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   539: goto -257 -> 282
    //   542: astore 4
    //   544: ldc_w 352
    //   547: aload 4
    //   549: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   552: goto -175 -> 377
    //   555: astore_3
    //   556: ldc_w 352
    //   559: aload_3
    //   560: invokestatic 358	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   563: goto -91 -> 472
    //   566: ldc_w 352
    //   569: ldc_w 1048
    //   572: invokestatic 1002	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   575: goto -103 -> 472
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	ImageLoader
    //   119	2	1	bool	boolean
    //   7	521	2	localHashMap	HashMap
    //   11	398	3	localFile1	File
    //   519	5	3	localException1	Exception
    //   555	5	3	localException2	Exception
    //   138	324	4	localFile2	File
    //   480	6	4	localException3	Exception
    //   493	6	4	localException4	Exception
    //   506	6	4	localException5	Exception
    //   529	6	4	localException6	Exception
    //   542	6	4	localException7	Exception
    // Exception table:
    //   from	to	target	type
    //   19	24	480	java/lang/Exception
    //   24	39	493	java/lang/Exception
    //   124	203	506	java/lang/Exception
    //   75	120	519	java/lang/Exception
    //   472	478	519	java/lang/Exception
    //   508	516	519	java/lang/Exception
    //   531	539	519	java/lang/Exception
    //   544	552	519	java/lang/Exception
    //   556	563	519	java/lang/Exception
    //   566	575	519	java/lang/Exception
    //   203	282	529	java/lang/Exception
    //   282	377	542	java/lang/Exception
    //   377	472	555	java/lang/Exception
  }
  
  public boolean decrementUseCount(String paramString)
  {
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null) {
      return true;
    }
    if (localInteger.intValue() == 1)
    {
      this.bitmapUseCounts.remove(paramString);
      return true;
    }
    this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() - 1));
    return false;
  }
  
  public Float getFileProgress(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return (Float)this.fileProgresses.get(paramString);
  }
  
  public BitmapDrawable getImageFromMemory(String paramString)
  {
    return this.memCache.get(paramString);
  }
  
  public BitmapDrawable getImageFromMemory(TLObject paramTLObject, String paramString1, String paramString2)
  {
    if ((paramTLObject == null) && (paramString1 == null)) {
      return null;
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
      return this.memCache.get(paramTLObject);
      if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        paramTLObject = (TLRPC.FileLocation)paramTLObject;
        paramString1 = paramTLObject.volume_id + "_" + paramTLObject.local_id;
      }
      else
      {
        paramString1 = (String)localObject;
        if ((paramTLObject instanceof TLRPC.Document))
        {
          paramTLObject = (TLRPC.Document)paramTLObject;
          if (paramTLObject.version == 0) {
            paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id;
          } else {
            paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id + "_" + paramTLObject.version;
          }
        }
      }
    }
  }
  
  public void incrementUseCount(String paramString)
  {
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null)
    {
      this.bitmapUseCounts.put(paramString, Integer.valueOf(1));
      return;
    }
    this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
  }
  
  public boolean isInCache(String paramString)
  {
    return this.memCache.get(paramString) != null;
  }
  
  public boolean isLoadingHttpFile(String paramString)
  {
    return this.httpFileLoadTasksByKeys.containsKey(paramString);
  }
  
  public void loadHttpFile(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (this.httpFileLoadTasksByKeys.containsKey(paramString1))) {
      return;
    }
    paramString2 = getHttpUrlExtension(paramString1, paramString2);
    File localFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(paramString1) + "_temp." + paramString2);
    localFile.delete();
    paramString2 = new HttpFileTask(paramString1, localFile, paramString2);
    this.httpFileLoadTasks.add(paramString2);
    this.httpFileLoadTasksByKeys.put(paramString1, paramString2);
    runHttpFileLoadTasks(null, 0);
  }
  
  public void loadImageForImageReceiver(ImageReceiver paramImageReceiver)
  {
    if (paramImageReceiver == null) {
      return;
    }
    Object localObject1 = paramImageReceiver.getKey();
    if (localObject1 != null)
    {
      localObject2 = this.memCache.get((String)localObject1);
      if (localObject2 != null)
      {
        cancelLoadingForImageReceiver(paramImageReceiver, 0);
        if (!paramImageReceiver.isForcePreview())
        {
          paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject2, (String)localObject1, false, true);
          return;
        }
      }
    }
    int i = 0;
    localObject1 = paramImageReceiver.getThumbKey();
    int j = i;
    if (localObject1 != null)
    {
      localObject2 = this.memCache.get((String)localObject1);
      j = i;
      if (localObject2 != null)
      {
        paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject2, (String)localObject1, true, true);
        cancelLoadingForImageReceiver(paramImageReceiver, 1);
        j = 1;
      }
    }
    TLRPC.FileLocation localFileLocation = paramImageReceiver.getThumbLocation();
    Object localObject8 = paramImageReceiver.getImageLocation();
    String str = paramImageReceiver.getHttpImageLocation();
    int m = 0;
    int k = 0;
    Object localObject12 = null;
    Object localObject2 = null;
    Object localObject10 = null;
    Object localObject9 = null;
    Object localObject4 = null;
    Object localObject11 = null;
    localObject1 = null;
    Object localObject7 = null;
    Object localObject3 = paramImageReceiver.getExt();
    Object localObject5 = localObject3;
    if (localObject3 == null) {
      localObject5 = "jpg";
    }
    Object localObject6;
    if (str != null)
    {
      localObject1 = Utilities.MD5(str);
      localObject2 = (String)localObject1 + "." + getHttpUrlExtension(str, "jpg");
      localObject6 = localObject8;
      label227:
      localObject3 = localObject7;
      if (localFileLocation != null)
      {
        localObject3 = localFileLocation.volume_id + "_" + localFileLocation.local_id;
        localObject4 = (String)localObject3 + "." + (String)localObject5;
      }
      localObject8 = paramImageReceiver.getFilter();
      localObject9 = paramImageReceiver.getThumbFilter();
      localObject7 = localObject1;
      if (localObject1 != null)
      {
        localObject7 = localObject1;
        if (localObject8 != null) {
          localObject7 = (String)localObject1 + "@" + (String)localObject8;
        }
      }
      localObject1 = localObject3;
      if (localObject3 != null)
      {
        localObject1 = localObject3;
        if (localObject9 != null) {
          localObject1 = (String)localObject3 + "@" + (String)localObject9;
        }
      }
      if (str == null) {
        break label964;
      }
      if (j == 0) {
        break label959;
      }
    }
    label744:
    label774:
    label814:
    label935:
    label946:
    label954:
    label959:
    for (i = 2;; i = 1)
    {
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject1, (String)localObject4, (String)localObject5, localFileLocation, null, (String)localObject9, 0, true, i);
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject7, (String)localObject2, (String)localObject5, null, str, (String)localObject8, 0, true, 0);
      return;
      localObject6 = localObject8;
      if (localObject8 == null) {
        break label227;
      }
      if ((localObject8 instanceof TLRPC.FileLocation))
      {
        localObject9 = (TLRPC.FileLocation)localObject8;
        localObject4 = ((TLRPC.FileLocation)localObject9).volume_id + "_" + ((TLRPC.FileLocation)localObject9).local_id;
        localObject6 = (String)localObject4 + "." + (String)localObject5;
        if ((paramImageReceiver.getExt() == null) && (((TLRPC.FileLocation)localObject9).key == null))
        {
          localObject3 = localObject10;
          localObject2 = localObject6;
          localObject1 = localObject4;
          i = m;
          if (((TLRPC.FileLocation)localObject9).volume_id == -2147483648L)
          {
            localObject3 = localObject10;
            localObject2 = localObject6;
            localObject1 = localObject4;
            i = m;
            if (((TLRPC.FileLocation)localObject9).local_id >= 0) {}
          }
        }
        else
        {
          i = 1;
          localObject1 = localObject4;
          localObject2 = localObject6;
          localObject3 = localObject10;
        }
      }
      do
      {
        localObject4 = localObject3;
        localObject6 = localObject8;
        k = i;
        if (localObject8 != localFileLocation) {
          break;
        }
        localObject6 = null;
        localObject1 = null;
        localObject2 = null;
        localObject4 = localObject3;
        k = i;
        break;
        localObject3 = localObject10;
        localObject2 = localObject12;
        localObject1 = localObject11;
        i = m;
      } while (!(localObject8 instanceof TLRPC.Document));
      localObject4 = (TLRPC.Document)localObject8;
      if ((((TLRPC.Document)localObject4).id == 0L) || (((TLRPC.Document)localObject4).dc_id == 0)) {
        break;
      }
      if (((TLRPC.Document)localObject4).version == 0)
      {
        localObject1 = ((TLRPC.Document)localObject4).dc_id + "_" + ((TLRPC.Document)localObject4).id;
        localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject4);
        if (localObject2 != null)
        {
          i = ((String)localObject2).lastIndexOf('.');
          if (i != -1) {
            break label935;
          }
        }
        localObject2 = "";
        localObject3 = localObject2;
        if (((String)localObject2).length() <= 1)
        {
          if ((((TLRPC.Document)localObject4).mime_type == null) || (!((TLRPC.Document)localObject4).mime_type.equals("video/mp4"))) {
            break label946;
          }
          localObject3 = ".mp4";
        }
        localObject2 = (String)localObject1 + (String)localObject3;
        localObject3 = localObject9;
        if (0 != 0) {
          localObject3 = null + "." + (String)localObject5;
        }
        if (MessageObject.isGifDocument((TLRPC.Document)localObject4)) {
          break label954;
        }
      }
      for (i = 1;; i = 0)
      {
        break;
        localObject1 = ((TLRPC.Document)localObject4).dc_id + "_" + ((TLRPC.Document)localObject4).id + "_" + ((TLRPC.Document)localObject4).version;
        break label744;
        localObject2 = ((String)localObject2).substring(i);
        break label774;
        localObject3 = "";
        break label814;
      }
    }
    label964:
    if (j != 0)
    {
      i = 2;
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject1, (String)localObject4, (String)localObject5, localFileLocation, null, (String)localObject9, 0, true, i);
      i = paramImageReceiver.getSize();
      if ((k == 0) && (!paramImageReceiver.getCacheOnly())) {
        break label1035;
      }
    }
    label1035:
    for (boolean bool = true;; bool = false)
    {
      createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject7, (String)localObject2, (String)localObject5, (TLObject)localObject6, null, (String)localObject8, i, bool, 0);
      return;
      i = 1;
      break;
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
    if (paramBoolean)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.replaceImageInCacheInternal(paramString1, paramString2, paramFileLocation);
        }
      });
      return;
    }
    replaceImageInCacheInternal(paramString1, paramString2, paramFileLocation);
  }
  
  private class CacheImage
  {
    protected boolean animatedFile;
    protected ImageLoader.CacheOutTask cacheTask;
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
    protected File tempFilePath;
    protected boolean thumb;
    protected String url;
    
    private CacheImage() {}
    
    public void addImageReceiver(ImageReceiver paramImageReceiver, String paramString1, String paramString2)
    {
      if (this.imageReceiverArray.contains(paramImageReceiver)) {
        return;
      }
      this.imageReceiverArray.add(paramImageReceiver);
      this.keys.add(paramString1);
      this.filters.add(paramString2);
      ImageLoader.this.imageLoadingByTag.put(paramImageReceiver.getTag(this.thumb), this);
    }
    
    public void removeImageReceiver(ImageReceiver paramImageReceiver)
    {
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
          if (localImageReceiver != null) {
            ImageLoader.this.imageLoadingByTag.remove(localImageReceiver.getTag(this.thumb));
          }
          j = i - 1;
        }
      }
      if (this.imageReceiverArray.size() == 0)
      {
        i = 0;
        while (i < this.imageReceiverArray.size())
        {
          ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver)this.imageReceiverArray.get(i)).getTag(this.thumb));
          i += 1;
        }
        this.imageReceiverArray.clear();
        if (this.location != null)
        {
          if (!(this.location instanceof TLRPC.FileLocation)) {
            break label323;
          }
          FileLoader.getInstance().cancelLoadFile((TLRPC.FileLocation)this.location, this.ext);
        }
        if (this.cacheTask != null)
        {
          if (!this.thumb) {
            break label349;
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
        label323:
        if (!(this.location instanceof TLRPC.Document)) {
          break;
        }
        FileLoader.getInstance().cancelLoadFile((TLRPC.Document)this.location);
        break;
        label349:
        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
      }
    }
    
    public void setImageAndClear(final BitmapDrawable paramBitmapDrawable)
    {
      if (paramBitmapDrawable != null) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i;
            if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
            {
              int j = 0;
              AnimatedFileDrawable localAnimatedFileDrawable2 = (AnimatedFileDrawable)paramBitmapDrawable;
              i = 0;
              if (i < this.val$finalImageReceiverArray.size())
              {
                ImageReceiver localImageReceiver = (ImageReceiver)this.val$finalImageReceiverArray.get(i);
                if (i == 0) {}
                for (AnimatedFileDrawable localAnimatedFileDrawable1 = localAnimatedFileDrawable2;; localAnimatedFileDrawable1 = localAnimatedFileDrawable2.makeCopy())
                {
                  if (localImageReceiver.setImageBitmapByKey(localAnimatedFileDrawable1, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.thumb, false)) {
                    j = 1;
                  }
                  i += 1;
                  break;
                }
              }
              if (j == 0) {
                ((AnimatedFileDrawable)paramBitmapDrawable).recycle();
              }
            }
            for (;;)
            {
              return;
              i = 0;
              while (i < this.val$finalImageReceiverArray.size())
              {
                ((ImageReceiver)this.val$finalImageReceiverArray.get(i)).setImageBitmapByKey(paramBitmapDrawable, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.thumb, false);
                i += 1;
              }
            }
          }
        });
      }
      int i = 0;
      while (i < this.imageReceiverArray.size())
      {
        paramBitmapDrawable = (ImageReceiver)this.imageReceiverArray.get(i);
        ImageLoader.this.imageLoadingByTag.remove(paramBitmapDrawable.getTag(this.thumb));
        i += 1;
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
      //   4: astore 14
      //   6: aload 14
      //   8: monitorenter
      //   9: aload_0
      //   10: invokestatic 67	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   13: putfield 55	org/telegram/messenger/ImageLoader$CacheOutTask:runningThread	Ljava/lang/Thread;
      //   16: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   19: pop
      //   20: aload_0
      //   21: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   24: ifeq +7 -> 31
      //   27: aload 14
      //   29: monitorexit
      //   30: return
      //   31: aload 14
      //   33: monitorexit
      //   34: aload_0
      //   35: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   38: getfield 76	org/telegram/messenger/ImageLoader$CacheImage:animatedFile	Z
      //   41: ifeq +109 -> 150
      //   44: aload_0
      //   45: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   48: astore 14
      //   50: aload 14
      //   52: monitorenter
      //   53: aload_0
      //   54: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   57: ifeq +23 -> 80
      //   60: aload 14
      //   62: monitorexit
      //   63: return
      //   64: astore 15
      //   66: aload 14
      //   68: monitorexit
      //   69: aload 15
      //   71: athrow
      //   72: astore 15
      //   74: aload 14
      //   76: monitorexit
      //   77: aload 15
      //   79: athrow
      //   80: aload 14
      //   82: monitorexit
      //   83: aload_0
      //   84: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   87: getfield 80	org/telegram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   90: astore 14
      //   92: aload_0
      //   93: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   96: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   99: ifnull +45 -> 144
      //   102: aload_0
      //   103: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   106: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   109: ldc 86
      //   111: invokevirtual 92	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   114: ifeq +30 -> 144
      //   117: iconst_1
      //   118: istore 13
      //   120: new 94	org/telegram/ui/Components/AnimatedFileDrawable
      //   123: dup
      //   124: aload 14
      //   126: iload 13
      //   128: invokespecial 97	org/telegram/ui/Components/AnimatedFileDrawable:<init>	(Ljava/io/File;Z)V
      //   131: astore 14
      //   133: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   136: pop
      //   137: aload_0
      //   138: aload 14
      //   140: invokespecial 99	org/telegram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   143: return
      //   144: iconst_0
      //   145: istore 13
      //   147: goto -27 -> 120
      //   150: aconst_null
      //   151: astore 19
      //   153: aconst_null
      //   154: astore 20
      //   156: aconst_null
      //   157: astore 18
      //   159: iconst_0
      //   160: istore 10
      //   162: iconst_0
      //   163: istore 9
      //   165: aload_0
      //   166: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   169: getfield 80	org/telegram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   172: astore 21
      //   174: iconst_1
      //   175: istore 11
      //   177: iconst_0
      //   178: istore 7
      //   180: iconst_0
      //   181: istore 6
      //   183: iconst_0
      //   184: istore 12
      //   186: iconst_0
      //   187: istore 8
      //   189: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   192: bipush 19
      //   194: if_icmpge +147 -> 341
      //   197: aconst_null
      //   198: astore 14
      //   200: aconst_null
      //   201: astore 16
      //   203: new 107	java/io/RandomAccessFile
      //   206: dup
      //   207: aload 21
      //   209: ldc 109
      //   211: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   214: astore 15
      //   216: iload 12
      //   218: istore 6
      //   220: aload_0
      //   221: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   224: getfield 115	org/telegram/messenger/ImageLoader$CacheImage:thumb	Z
      //   227: ifeq +241 -> 468
      //   230: iload 12
      //   232: istore 6
      //   234: invokestatic 119	org/telegram/messenger/ImageLoader:access$1200	()[B
      //   237: astore 14
      //   239: iload 12
      //   241: istore 6
      //   243: aload 15
      //   245: aload 14
      //   247: iconst_0
      //   248: aload 14
      //   250: arraylength
      //   251: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   254: iload 12
      //   256: istore 6
      //   258: new 88	java/lang/String
      //   261: dup
      //   262: aload 14
      //   264: invokespecial 126	java/lang/String:<init>	([B)V
      //   267: invokevirtual 130	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   270: invokevirtual 130	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   273: astore 14
      //   275: iload 8
      //   277: istore 5
      //   279: iload 12
      //   281: istore 6
      //   283: aload 14
      //   285: ldc -124
      //   287: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   290: ifeq +24 -> 314
      //   293: iload 8
      //   295: istore 5
      //   297: iload 12
      //   299: istore 6
      //   301: aload 14
      //   303: ldc -118
      //   305: invokevirtual 141	java/lang/String:endsWith	(Ljava/lang/String;)Z
      //   308: ifeq +6 -> 314
      //   311: iconst_1
      //   312: istore 5
      //   314: iload 5
      //   316: istore 6
      //   318: aload 15
      //   320: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   323: iload 5
      //   325: istore 7
      //   327: aload 15
      //   329: ifnull +12 -> 341
      //   332: aload 15
      //   334: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   337: iload 5
      //   339: istore 7
      //   341: aload_0
      //   342: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   345: getfield 115	org/telegram/messenger/ImageLoader$CacheImage:thumb	Z
      //   348: ifeq +1000 -> 1348
      //   351: iconst_0
      //   352: istore 6
      //   354: iload 6
      //   356: istore 5
      //   358: aload_0
      //   359: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   362: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   365: ifnull +21 -> 386
      //   368: aload_0
      //   369: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   372: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   375: ldc -110
      //   377: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   380: ifeq +209 -> 589
      //   383: iconst_3
      //   384: istore 5
      //   386: aload_0
      //   387: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   390: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   393: invokestatic 160	org/telegram/messenger/ImageLoader:access$1402	(Lorg/telegram/messenger/ImageLoader;J)J
      //   396: pop2
      //   397: aload_0
      //   398: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   401: astore 14
      //   403: aload 14
      //   405: monitorenter
      //   406: aload_0
      //   407: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   410: ifeq +225 -> 635
      //   413: aload 14
      //   415: monitorexit
      //   416: return
      //   417: astore 15
      //   419: aload 14
      //   421: monitorexit
      //   422: aload 15
      //   424: athrow
      //   425: astore 14
      //   427: aconst_null
      //   428: astore 15
      //   430: ldc -94
      //   432: aload 14
      //   434: invokestatic 168	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   437: aload 15
      //   439: astore 16
      //   441: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   444: pop
      //   445: aload 16
      //   447: ifnull +2424 -> 2871
      //   450: new 170	android/graphics/drawable/BitmapDrawable
      //   453: dup
      //   454: aload 16
      //   456: invokespecial 173	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
      //   459: astore 14
      //   461: aload_0
      //   462: aload 14
      //   464: invokespecial 99	org/telegram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   467: return
      //   468: iload 12
      //   470: istore 6
      //   472: invokestatic 176	org/telegram/messenger/ImageLoader:access$1300	()[B
      //   475: astore 14
      //   477: goto -238 -> 239
      //   480: astore 14
      //   482: ldc -94
      //   484: aload 14
      //   486: invokestatic 168	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   489: iload 5
      //   491: istore 7
      //   493: goto -152 -> 341
      //   496: astore 14
      //   498: aload 16
      //   500: astore 15
      //   502: aload 14
      //   504: astore 16
      //   506: aload 15
      //   508: astore 14
      //   510: ldc -94
      //   512: aload 16
      //   514: invokestatic 168	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   517: iload 6
      //   519: istore 7
      //   521: aload 15
      //   523: ifnull -182 -> 341
      //   526: aload 15
      //   528: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   531: iload 6
      //   533: istore 7
      //   535: goto -194 -> 341
      //   538: astore 14
      //   540: ldc -94
      //   542: aload 14
      //   544: invokestatic 168	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   547: iload 6
      //   549: istore 7
      //   551: goto -210 -> 341
      //   554: astore 16
      //   556: aload 14
      //   558: astore 15
      //   560: aload 16
      //   562: astore 14
      //   564: aload 15
      //   566: ifnull +8 -> 574
      //   569: aload 15
      //   571: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   574: aload 14
      //   576: athrow
      //   577: astore 15
      //   579: ldc -94
      //   581: aload 15
      //   583: invokestatic 168	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   586: goto -12 -> 574
      //   589: aload_0
      //   590: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   593: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   596: ldc -78
      //   598: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   601: ifeq +9 -> 610
      //   604: iconst_2
      //   605: istore 5
      //   607: goto -221 -> 386
      //   610: iload 6
      //   612: istore 5
      //   614: aload_0
      //   615: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   618: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   621: ldc -76
      //   623: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   626: ifeq -240 -> 386
      //   629: iconst_1
      //   630: istore 5
      //   632: goto -246 -> 386
      //   635: aload 14
      //   637: monitorexit
      //   638: new 182	android/graphics/BitmapFactory$Options
      //   641: dup
      //   642: invokespecial 183	android/graphics/BitmapFactory$Options:<init>	()V
      //   645: astore 17
      //   647: aload 17
      //   649: iconst_1
      //   650: putfield 186	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   653: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   656: bipush 21
      //   658: if_icmpge +9 -> 667
      //   661: aload 17
      //   663: iconst_1
      //   664: putfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   667: iload 7
      //   669: ifeq +200 -> 869
      //   672: new 107	java/io/RandomAccessFile
      //   675: dup
      //   676: aload 21
      //   678: ldc 109
      //   680: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   683: astore 16
      //   685: aload 16
      //   687: invokevirtual 193	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   690: getstatic 199	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   693: lconst_0
      //   694: aload 21
      //   696: invokevirtual 204	java/io/File:length	()J
      //   699: invokevirtual 210	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   702: astore 18
      //   704: new 182	android/graphics/BitmapFactory$Options
      //   707: dup
      //   708: invokespecial 183	android/graphics/BitmapFactory$Options:<init>	()V
      //   711: astore 14
      //   713: aload 14
      //   715: iconst_1
      //   716: putfield 213	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   719: aconst_null
      //   720: aload 18
      //   722: aload 18
      //   724: invokevirtual 219	java/nio/ByteBuffer:limit	()I
      //   727: aload 14
      //   729: iconst_1
      //   730: invokestatic 225	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   733: pop
      //   734: aload 14
      //   736: getfield 228	android/graphics/BitmapFactory$Options:outWidth	I
      //   739: aload 14
      //   741: getfield 231	android/graphics/BitmapFactory$Options:outHeight	I
      //   744: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   747: invokestatic 243	org/telegram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   750: astore 14
      //   752: aload 14
      //   754: astore 15
      //   756: aload 18
      //   758: invokevirtual 219	java/nio/ByteBuffer:limit	()I
      //   761: istore 6
      //   763: aload 14
      //   765: astore 15
      //   767: aload 17
      //   769: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   772: ifne +91 -> 863
      //   775: iconst_1
      //   776: istore 13
      //   778: aload 14
      //   780: astore 15
      //   782: aload 14
      //   784: aload 18
      //   786: iload 6
      //   788: aconst_null
      //   789: iload 13
      //   791: invokestatic 225	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   794: pop
      //   795: aload 14
      //   797: astore 15
      //   799: aload 16
      //   801: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   804: aload 14
      //   806: ifnonnull +191 -> 997
      //   809: aload 14
      //   811: astore 15
      //   813: aload 21
      //   815: invokevirtual 204	java/io/File:length	()J
      //   818: lconst_0
      //   819: lcmp
      //   820: ifeq +21 -> 841
      //   823: aload 14
      //   825: astore 16
      //   827: aload 14
      //   829: astore 15
      //   831: aload_0
      //   832: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   835: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   838: ifnonnull -397 -> 441
      //   841: aload 14
      //   843: astore 15
      //   845: aload 21
      //   847: invokevirtual 246	java/io/File:delete	()Z
      //   850: pop
      //   851: aload 14
      //   853: astore 16
      //   855: goto -414 -> 441
      //   858: astore 14
      //   860: goto -430 -> 430
      //   863: iconst_0
      //   864: istore 13
      //   866: goto -88 -> 778
      //   869: aload 17
      //   871: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   874: ifeq +90 -> 964
      //   877: new 107	java/io/RandomAccessFile
      //   880: dup
      //   881: aload 21
      //   883: ldc 109
      //   885: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   888: astore 16
      //   890: aload 16
      //   892: invokevirtual 247	java/io/RandomAccessFile:length	()J
      //   895: l2i
      //   896: istore 6
      //   898: invokestatic 250	org/telegram/messenger/ImageLoader:access$1500	()[B
      //   901: ifnull +1998 -> 2899
      //   904: invokestatic 250	org/telegram/messenger/ImageLoader:access$1500	()[B
      //   907: arraylength
      //   908: iload 6
      //   910: if_icmplt +1989 -> 2899
      //   913: invokestatic 250	org/telegram/messenger/ImageLoader:access$1500	()[B
      //   916: astore 14
      //   918: aload 14
      //   920: astore 15
      //   922: aload 14
      //   924: ifnonnull +15 -> 939
      //   927: iload 6
      //   929: newarray <illegal type>
      //   931: astore 15
      //   933: aload 15
      //   935: invokestatic 254	org/telegram/messenger/ImageLoader:access$1502	([B)[B
      //   938: pop
      //   939: aload 16
      //   941: aload 15
      //   943: iconst_0
      //   944: iload 6
      //   946: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   949: aload 15
      //   951: iconst_0
      //   952: iload 6
      //   954: aload 17
      //   956: invokestatic 260	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   959: astore 14
      //   961: goto -157 -> 804
      //   964: new 262	java/io/FileInputStream
      //   967: dup
      //   968: aload 21
      //   970: invokespecial 265	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   973: astore 16
      //   975: aload 16
      //   977: aconst_null
      //   978: aload 17
      //   980: invokestatic 269	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   983: astore 14
      //   985: aload 14
      //   987: astore 15
      //   989: aload 16
      //   991: invokevirtual 270	java/io/FileInputStream:close	()V
      //   994: goto -190 -> 804
      //   997: iload 5
      //   999: iconst_1
      //   1000: if_icmpne +71 -> 1071
      //   1003: aload 14
      //   1005: astore 16
      //   1007: aload 14
      //   1009: astore 15
      //   1011: aload 14
      //   1013: invokevirtual 276	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1016: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1019: if_acmpne -578 -> 441
      //   1022: aload 14
      //   1024: astore 15
      //   1026: aload 17
      //   1028: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1031: ifeq +1874 -> 2905
      //   1034: iconst_0
      //   1035: istore 5
      //   1037: aload 14
      //   1039: astore 15
      //   1041: aload 14
      //   1043: iconst_3
      //   1044: iload 5
      //   1046: aload 14
      //   1048: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   1051: aload 14
      //   1053: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   1056: aload 14
      //   1058: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   1061: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1064: aload 14
      //   1066: astore 16
      //   1068: goto -627 -> 441
      //   1071: iload 5
      //   1073: iconst_2
      //   1074: if_icmpne +71 -> 1145
      //   1077: aload 14
      //   1079: astore 16
      //   1081: aload 14
      //   1083: astore 15
      //   1085: aload 14
      //   1087: invokevirtual 276	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1090: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1093: if_acmpne -652 -> 441
      //   1096: aload 14
      //   1098: astore 15
      //   1100: aload 17
      //   1102: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1105: ifeq +1806 -> 2911
      //   1108: iconst_0
      //   1109: istore 5
      //   1111: aload 14
      //   1113: astore 15
      //   1115: aload 14
      //   1117: iconst_1
      //   1118: iload 5
      //   1120: aload 14
      //   1122: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   1125: aload 14
      //   1127: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   1130: aload 14
      //   1132: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   1135: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1138: aload 14
      //   1140: astore 16
      //   1142: goto -701 -> 441
      //   1145: iload 5
      //   1147: iconst_3
      //   1148: if_icmpne +158 -> 1306
      //   1151: aload 14
      //   1153: astore 16
      //   1155: aload 14
      //   1157: astore 15
      //   1159: aload 14
      //   1161: invokevirtual 276	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1164: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1167: if_acmpne -726 -> 441
      //   1170: aload 14
      //   1172: astore 15
      //   1174: aload 17
      //   1176: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1179: ifeq +1738 -> 2917
      //   1182: iconst_0
      //   1183: istore 5
      //   1185: aload 14
      //   1187: astore 15
      //   1189: aload 14
      //   1191: bipush 7
      //   1193: iload 5
      //   1195: aload 14
      //   1197: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   1200: aload 14
      //   1202: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   1205: aload 14
      //   1207: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   1210: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1213: aload 14
      //   1215: astore 15
      //   1217: aload 17
      //   1219: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1222: ifeq +1701 -> 2923
      //   1225: iconst_0
      //   1226: istore 5
      //   1228: aload 14
      //   1230: astore 15
      //   1232: aload 14
      //   1234: bipush 7
      //   1236: iload 5
      //   1238: aload 14
      //   1240: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   1243: aload 14
      //   1245: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   1248: aload 14
      //   1250: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   1253: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1256: aload 14
      //   1258: astore 15
      //   1260: aload 17
      //   1262: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1265: ifeq +1664 -> 2929
      //   1268: iconst_0
      //   1269: istore 5
      //   1271: aload 14
      //   1273: astore 15
      //   1275: aload 14
      //   1277: bipush 7
      //   1279: iload 5
      //   1281: aload 14
      //   1283: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   1286: aload 14
      //   1288: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   1291: aload 14
      //   1293: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   1296: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1299: aload 14
      //   1301: astore 16
      //   1303: goto -862 -> 441
      //   1306: aload 14
      //   1308: astore 16
      //   1310: iload 5
      //   1312: ifne -871 -> 441
      //   1315: aload 14
      //   1317: astore 16
      //   1319: aload 14
      //   1321: astore 15
      //   1323: aload 17
      //   1325: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1328: ifeq -887 -> 441
      //   1331: aload 14
      //   1333: astore 15
      //   1335: aload 14
      //   1337: invokestatic 293	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   1340: pop
      //   1341: aload 14
      //   1343: astore 16
      //   1345: goto -904 -> 441
      //   1348: iload 11
      //   1350: istore 5
      //   1352: aload 19
      //   1354: astore 17
      //   1356: iload 9
      //   1358: istore 8
      //   1360: aload_0
      //   1361: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1364: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1367: ifnull +1574 -> 2941
      //   1370: aload_0
      //   1371: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1374: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1377: ldc_w 298
      //   1380: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1383: ifeq +142 -> 1525
      //   1386: aload_0
      //   1387: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1390: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1393: ldc_w 300
      //   1396: bipush 8
      //   1398: invokevirtual 304	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1401: istore 5
      //   1403: aload 18
      //   1405: astore 17
      //   1407: iload 5
      //   1409: iflt +1526 -> 2935
      //   1412: aload_0
      //   1413: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1416: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1419: bipush 8
      //   1421: iload 5
      //   1423: invokevirtual 308	java/lang/String:substring	(II)Ljava/lang/String;
      //   1426: invokestatic 314	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1429: invokestatic 318	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1432: astore 17
      //   1434: goto +1501 -> 2935
      //   1437: iload 6
      //   1439: ifeq +47 -> 1486
      //   1442: aload_0
      //   1443: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1446: invokestatic 322	org/telegram/messenger/ImageLoader:access$1400	(Lorg/telegram/messenger/ImageLoader;)J
      //   1449: lconst_0
      //   1450: lcmp
      //   1451: ifeq +35 -> 1486
      //   1454: aload_0
      //   1455: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1458: invokestatic 322	org/telegram/messenger/ImageLoader:access$1400	(Lorg/telegram/messenger/ImageLoader;)J
      //   1461: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   1464: iload 6
      //   1466: i2l
      //   1467: lsub
      //   1468: lcmp
      //   1469: ifle +17 -> 1486
      //   1472: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   1475: bipush 21
      //   1477: if_icmpge +9 -> 1486
      //   1480: iload 6
      //   1482: i2l
      //   1483: invokestatic 326	java/lang/Thread:sleep	(J)V
      //   1486: aload_0
      //   1487: getfield 27	org/telegram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/telegram/messenger/ImageLoader;
      //   1490: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   1493: invokestatic 160	org/telegram/messenger/ImageLoader:access$1402	(Lorg/telegram/messenger/ImageLoader;J)J
      //   1496: pop2
      //   1497: aload_0
      //   1498: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1501: astore 14
      //   1503: aload 14
      //   1505: monitorenter
      //   1506: aload_0
      //   1507: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1510: ifeq +135 -> 1645
      //   1513: aload 14
      //   1515: monitorexit
      //   1516: return
      //   1517: astore 15
      //   1519: aload 14
      //   1521: monitorexit
      //   1522: aload 15
      //   1524: athrow
      //   1525: aload_0
      //   1526: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1529: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1532: ldc_w 328
      //   1535: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1538: ifeq +61 -> 1599
      //   1541: aload_0
      //   1542: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1545: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1548: ldc_w 300
      //   1551: bipush 9
      //   1553: invokevirtual 304	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1556: istore 5
      //   1558: aload 20
      //   1560: astore 17
      //   1562: iload 10
      //   1564: istore 8
      //   1566: iload 5
      //   1568: iflt +1396 -> 2964
      //   1571: aload_0
      //   1572: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1575: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1578: bipush 9
      //   1580: iload 5
      //   1582: invokevirtual 308	java/lang/String:substring	(II)Ljava/lang/String;
      //   1585: invokestatic 314	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1588: invokestatic 318	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1591: astore 17
      //   1593: iconst_1
      //   1594: istore 8
      //   1596: goto +1368 -> 2964
      //   1599: aload_0
      //   1600: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1603: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1606: ldc_w 330
      //   1609: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1612: istore 13
      //   1614: iload 11
      //   1616: istore 5
      //   1618: aload 19
      //   1620: astore 17
      //   1622: iload 9
      //   1624: istore 8
      //   1626: iload 13
      //   1628: ifne +1313 -> 2941
      //   1631: iconst_0
      //   1632: istore 5
      //   1634: aload 19
      //   1636: astore 17
      //   1638: iload 9
      //   1640: istore 8
      //   1642: goto +1299 -> 2941
      //   1645: aload 14
      //   1647: monitorexit
      //   1648: new 182	android/graphics/BitmapFactory$Options
      //   1651: dup
      //   1652: invokespecial 183	android/graphics/BitmapFactory$Options:<init>	()V
      //   1655: astore 18
      //   1657: aload 18
      //   1659: iconst_1
      //   1660: putfield 186	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1663: fconst_0
      //   1664: fstore_2
      //   1665: fconst_0
      //   1666: fstore_1
      //   1667: fconst_0
      //   1668: fstore_3
      //   1669: iconst_0
      //   1670: istore 9
      //   1672: iconst_0
      //   1673: istore 6
      //   1675: aload_0
      //   1676: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1679: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1682: ifnull +1205 -> 2887
      //   1685: aload_0
      //   1686: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1689: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1692: ldc_w 332
      //   1695: invokevirtual 336	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   1698: astore 14
      //   1700: aload 14
      //   1702: arraylength
      //   1703: iconst_2
      //   1704: if_icmplt +27 -> 1731
      //   1707: aload 14
      //   1709: iconst_0
      //   1710: aaload
      //   1711: invokestatic 342	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1714: getstatic 346	org/telegram/messenger/AndroidUtilities:density	F
      //   1717: fmul
      //   1718: fstore_1
      //   1719: aload 14
      //   1721: iconst_1
      //   1722: aaload
      //   1723: invokestatic 342	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1726: getstatic 346	org/telegram/messenger/AndroidUtilities:density	F
      //   1729: fmul
      //   1730: fstore_3
      //   1731: aload_0
      //   1732: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1735: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1738: ldc -76
      //   1740: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   1743: ifeq +6 -> 1749
      //   1746: iconst_1
      //   1747: istore 6
      //   1749: iload 6
      //   1751: istore 9
      //   1753: fload_1
      //   1754: fstore_2
      //   1755: fload_1
      //   1756: fconst_0
      //   1757: fcmpl
      //   1758: ifeq +1129 -> 2887
      //   1761: iload 6
      //   1763: istore 9
      //   1765: fload_1
      //   1766: fstore_2
      //   1767: fload_3
      //   1768: fconst_0
      //   1769: fcmpl
      //   1770: ifeq +1117 -> 2887
      //   1773: aload 18
      //   1775: iconst_1
      //   1776: putfield 213	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1779: aload 17
      //   1781: ifnull +171 -> 1952
      //   1784: iload 8
      //   1786: ifeq +142 -> 1928
      //   1789: getstatic 352	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1792: invokevirtual 358	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1795: aload 17
      //   1797: invokevirtual 361	java/lang/Long:longValue	()J
      //   1800: iconst_1
      //   1801: aload 18
      //   1803: invokestatic 367	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1806: pop
      //   1807: aconst_null
      //   1808: astore 15
      //   1810: aload 15
      //   1812: astore 14
      //   1814: aload 18
      //   1816: getfield 228	android/graphics/BitmapFactory$Options:outWidth	I
      //   1819: i2f
      //   1820: fstore_2
      //   1821: aload 15
      //   1823: astore 14
      //   1825: aload 18
      //   1827: getfield 231	android/graphics/BitmapFactory$Options:outHeight	I
      //   1830: i2f
      //   1831: fstore 4
      //   1833: aload 15
      //   1835: astore 14
      //   1837: fload_2
      //   1838: fload_1
      //   1839: fdiv
      //   1840: fload 4
      //   1842: fload_3
      //   1843: fdiv
      //   1844: invokestatic 373	java/lang/Math:max	(FF)F
      //   1847: fstore_3
      //   1848: fload_3
      //   1849: fstore_2
      //   1850: fload_3
      //   1851: fconst_1
      //   1852: fcmpg
      //   1853: ifge +5 -> 1858
      //   1856: fconst_1
      //   1857: fstore_2
      //   1858: aload 15
      //   1860: astore 14
      //   1862: aload 18
      //   1864: iconst_0
      //   1865: putfield 213	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1868: aload 15
      //   1870: astore 14
      //   1872: aload 18
      //   1874: fload_2
      //   1875: f2i
      //   1876: putfield 186	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1879: aload 15
      //   1881: astore 14
      //   1883: aload_0
      //   1884: getfield 32	org/telegram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1887: astore 16
      //   1889: aload 15
      //   1891: astore 14
      //   1893: aload 16
      //   1895: monitorenter
      //   1896: aload_0
      //   1897: getfield 53	org/telegram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1900: ifeq +85 -> 1985
      //   1903: aload 16
      //   1905: monitorexit
      //   1906: return
      //   1907: astore 17
      //   1909: aload 16
      //   1911: monitorexit
      //   1912: aload 15
      //   1914: astore 14
      //   1916: aload 17
      //   1918: athrow
      //   1919: astore 15
      //   1921: aload 14
      //   1923: astore 16
      //   1925: goto -1484 -> 441
      //   1928: getstatic 352	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1931: invokevirtual 358	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1934: aload 17
      //   1936: invokevirtual 361	java/lang/Long:longValue	()J
      //   1939: iconst_1
      //   1940: aload 18
      //   1942: invokestatic 376	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1945: pop
      //   1946: aconst_null
      //   1947: astore 15
      //   1949: goto -139 -> 1810
      //   1952: new 262	java/io/FileInputStream
      //   1955: dup
      //   1956: aload 21
      //   1958: invokespecial 265	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   1961: astore 16
      //   1963: aload 16
      //   1965: aconst_null
      //   1966: aload 18
      //   1968: invokestatic 269	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1971: astore 15
      //   1973: aload 15
      //   1975: astore 14
      //   1977: aload 16
      //   1979: invokevirtual 270	java/io/FileInputStream:close	()V
      //   1982: goto -172 -> 1810
      //   1985: aload 16
      //   1987: monitorexit
      //   1988: aload 15
      //   1990: astore 14
      //   1992: aload_0
      //   1993: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   1996: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1999: ifnull +22 -> 2021
      //   2002: iload 6
      //   2004: ifne +17 -> 2021
      //   2007: aload 15
      //   2009: astore 14
      //   2011: aload_0
      //   2012: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2015: getfield 296	org/telegram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   2018: ifnull +317 -> 2335
      //   2021: aload 15
      //   2023: astore 14
      //   2025: aload 18
      //   2027: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2030: putfield 379	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2033: aload 15
      //   2035: astore 14
      //   2037: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   2040: bipush 21
      //   2042: if_icmpge +13 -> 2055
      //   2045: aload 15
      //   2047: astore 14
      //   2049: aload 18
      //   2051: iconst_1
      //   2052: putfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2055: aload 15
      //   2057: astore 14
      //   2059: aload 18
      //   2061: iconst_0
      //   2062: putfield 382	android/graphics/BitmapFactory$Options:inDither	Z
      //   2065: aload 15
      //   2067: astore 16
      //   2069: aload 17
      //   2071: ifnull +31 -> 2102
      //   2074: iload 8
      //   2076: ifeq +274 -> 2350
      //   2079: aload 15
      //   2081: astore 14
      //   2083: getstatic 352	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2086: invokevirtual 358	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2089: aload 17
      //   2091: invokevirtual 361	java/lang/Long:longValue	()J
      //   2094: iconst_1
      //   2095: aload 18
      //   2097: invokestatic 367	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2100: astore 16
      //   2102: aload 16
      //   2104: astore 15
      //   2106: aload 16
      //   2108: ifnonnull +164 -> 2272
      //   2111: iload 7
      //   2113: ifeq +263 -> 2376
      //   2116: aload 16
      //   2118: astore 14
      //   2120: new 107	java/io/RandomAccessFile
      //   2123: dup
      //   2124: aload 21
      //   2126: ldc 109
      //   2128: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2131: astore 17
      //   2133: aload 16
      //   2135: astore 14
      //   2137: aload 17
      //   2139: invokevirtual 193	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   2142: getstatic 199	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   2145: lconst_0
      //   2146: aload 21
      //   2148: invokevirtual 204	java/io/File:length	()J
      //   2151: invokevirtual 210	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   2154: astore 19
      //   2156: aload 16
      //   2158: astore 14
      //   2160: new 182	android/graphics/BitmapFactory$Options
      //   2163: dup
      //   2164: invokespecial 183	android/graphics/BitmapFactory$Options:<init>	()V
      //   2167: astore 15
      //   2169: aload 16
      //   2171: astore 14
      //   2173: aload 15
      //   2175: iconst_1
      //   2176: putfield 213	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   2179: aload 16
      //   2181: astore 14
      //   2183: aconst_null
      //   2184: aload 19
      //   2186: aload 19
      //   2188: invokevirtual 219	java/nio/ByteBuffer:limit	()I
      //   2191: aload 15
      //   2193: iconst_1
      //   2194: invokestatic 225	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2197: pop
      //   2198: aload 16
      //   2200: astore 14
      //   2202: aload 15
      //   2204: getfield 228	android/graphics/BitmapFactory$Options:outWidth	I
      //   2207: aload 15
      //   2209: getfield 231	android/graphics/BitmapFactory$Options:outHeight	I
      //   2212: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2215: invokestatic 243	org/telegram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   2218: astore 15
      //   2220: aload 15
      //   2222: astore 14
      //   2224: aload 19
      //   2226: invokevirtual 219	java/nio/ByteBuffer:limit	()I
      //   2229: istore 7
      //   2231: aload 15
      //   2233: astore 14
      //   2235: aload 18
      //   2237: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2240: ifne +730 -> 2970
      //   2243: iconst_1
      //   2244: istore 13
      //   2246: aload 15
      //   2248: astore 14
      //   2250: aload 15
      //   2252: aload 19
      //   2254: iload 7
      //   2256: aconst_null
      //   2257: iload 13
      //   2259: invokestatic 225	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2262: pop
      //   2263: aload 15
      //   2265: astore 14
      //   2267: aload 17
      //   2269: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   2272: aload 15
      //   2274: ifnonnull +282 -> 2556
      //   2277: aload 15
      //   2279: astore 16
      //   2281: iload 5
      //   2283: ifeq -1842 -> 441
      //   2286: aload 15
      //   2288: astore 14
      //   2290: aload 21
      //   2292: invokevirtual 204	java/io/File:length	()J
      //   2295: lconst_0
      //   2296: lcmp
      //   2297: ifeq +21 -> 2318
      //   2300: aload 15
      //   2302: astore 16
      //   2304: aload 15
      //   2306: astore 14
      //   2308: aload_0
      //   2309: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2312: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2315: ifnonnull -1874 -> 441
      //   2318: aload 15
      //   2320: astore 14
      //   2322: aload 21
      //   2324: invokevirtual 246	java/io/File:delete	()Z
      //   2327: pop
      //   2328: aload 15
      //   2330: astore 16
      //   2332: goto -1891 -> 441
      //   2335: aload 15
      //   2337: astore 14
      //   2339: aload 18
      //   2341: getstatic 385	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
      //   2344: putfield 379	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2347: goto -314 -> 2033
      //   2350: aload 15
      //   2352: astore 14
      //   2354: getstatic 352	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2357: invokevirtual 358	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2360: aload 17
      //   2362: invokevirtual 361	java/lang/Long:longValue	()J
      //   2365: iconst_1
      //   2366: aload 18
      //   2368: invokestatic 376	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2371: astore 16
      //   2373: goto -271 -> 2102
      //   2376: aload 16
      //   2378: astore 14
      //   2380: aload 18
      //   2382: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2385: ifeq +130 -> 2515
      //   2388: aload 16
      //   2390: astore 14
      //   2392: new 107	java/io/RandomAccessFile
      //   2395: dup
      //   2396: aload 21
      //   2398: ldc 109
      //   2400: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2403: astore 17
      //   2405: aload 16
      //   2407: astore 14
      //   2409: aload 17
      //   2411: invokevirtual 247	java/io/RandomAccessFile:length	()J
      //   2414: l2i
      //   2415: istore 7
      //   2417: aload 16
      //   2419: astore 14
      //   2421: invokestatic 388	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   2424: ifnull +552 -> 2976
      //   2427: aload 16
      //   2429: astore 14
      //   2431: invokestatic 388	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   2434: arraylength
      //   2435: iload 7
      //   2437: if_icmplt +539 -> 2976
      //   2440: aload 16
      //   2442: astore 14
      //   2444: invokestatic 388	org/telegram/messenger/ImageLoader:access$1600	()[B
      //   2447: astore 15
      //   2449: aload 15
      //   2451: astore 14
      //   2453: aload 14
      //   2455: astore 15
      //   2457: aload 14
      //   2459: ifnonnull +23 -> 2482
      //   2462: aload 16
      //   2464: astore 14
      //   2466: iload 7
      //   2468: newarray <illegal type>
      //   2470: astore 15
      //   2472: aload 16
      //   2474: astore 14
      //   2476: aload 15
      //   2478: invokestatic 391	org/telegram/messenger/ImageLoader:access$1602	([B)[B
      //   2481: pop
      //   2482: aload 16
      //   2484: astore 14
      //   2486: aload 17
      //   2488: aload 15
      //   2490: iconst_0
      //   2491: iload 7
      //   2493: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   2496: aload 16
      //   2498: astore 14
      //   2500: aload 15
      //   2502: iconst_0
      //   2503: iload 7
      //   2505: aload 18
      //   2507: invokestatic 260	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2510: astore 15
      //   2512: goto -240 -> 2272
      //   2515: aload 16
      //   2517: astore 14
      //   2519: new 262	java/io/FileInputStream
      //   2522: dup
      //   2523: aload 21
      //   2525: invokespecial 265	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   2528: astore 17
      //   2530: aload 16
      //   2532: astore 14
      //   2534: aload 17
      //   2536: aconst_null
      //   2537: aload 18
      //   2539: invokestatic 269	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2542: astore 15
      //   2544: aload 15
      //   2546: astore 14
      //   2548: aload 17
      //   2550: invokevirtual 270	java/io/FileInputStream:close	()V
      //   2553: goto -281 -> 2272
      //   2556: iconst_0
      //   2557: istore 7
      //   2559: aload 15
      //   2561: astore 14
      //   2563: aload 15
      //   2565: astore 17
      //   2567: iload 7
      //   2569: istore 5
      //   2571: aload_0
      //   2572: getfield 34	org/telegram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/telegram/messenger/ImageLoader$CacheImage;
      //   2575: getfield 84	org/telegram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2578: ifnull +245 -> 2823
      //   2581: aload 15
      //   2583: astore 14
      //   2585: aload 15
      //   2587: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   2590: i2f
      //   2591: fstore_2
      //   2592: aload 15
      //   2594: astore 14
      //   2596: aload 15
      //   2598: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   2601: i2f
      //   2602: fstore_3
      //   2603: aload 15
      //   2605: astore 14
      //   2607: aload 15
      //   2609: astore 16
      //   2611: aload 18
      //   2613: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2616: ifne +89 -> 2705
      //   2619: aload 15
      //   2621: astore 16
      //   2623: fload_1
      //   2624: fconst_0
      //   2625: fcmpl
      //   2626: ifeq +79 -> 2705
      //   2629: aload 15
      //   2631: astore 16
      //   2633: fload_2
      //   2634: fload_1
      //   2635: fcmpl
      //   2636: ifeq +69 -> 2705
      //   2639: aload 15
      //   2641: astore 16
      //   2643: fload_2
      //   2644: ldc_w 392
      //   2647: fload_1
      //   2648: fadd
      //   2649: fcmpl
      //   2650: ifle +55 -> 2705
      //   2653: aload 15
      //   2655: astore 14
      //   2657: fload_2
      //   2658: fload_1
      //   2659: fdiv
      //   2660: fstore 4
      //   2662: aload 15
      //   2664: astore 14
      //   2666: aload 15
      //   2668: fload_1
      //   2669: f2i
      //   2670: fload_3
      //   2671: fload 4
      //   2673: fdiv
      //   2674: f2i
      //   2675: iconst_1
      //   2676: invokestatic 396	org/telegram/messenger/Bitmaps:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
      //   2679: astore 17
      //   2681: aload 15
      //   2683: astore 16
      //   2685: aload 15
      //   2687: aload 17
      //   2689: if_acmpeq +16 -> 2705
      //   2692: aload 15
      //   2694: astore 14
      //   2696: aload 15
      //   2698: invokevirtual 399	android/graphics/Bitmap:recycle	()V
      //   2701: aload 17
      //   2703: astore 16
      //   2705: aload 16
      //   2707: astore 17
      //   2709: iload 7
      //   2711: istore 5
      //   2713: aload 16
      //   2715: ifnull +108 -> 2823
      //   2718: aload 16
      //   2720: astore 17
      //   2722: iload 7
      //   2724: istore 5
      //   2726: iload 6
      //   2728: ifeq +95 -> 2823
      //   2731: aload 16
      //   2733: astore 17
      //   2735: iload 7
      //   2737: istore 5
      //   2739: fload_3
      //   2740: ldc_w 400
      //   2743: fcmpg
      //   2744: ifge +79 -> 2823
      //   2747: aload 16
      //   2749: astore 17
      //   2751: iload 7
      //   2753: istore 5
      //   2755: fload_2
      //   2756: ldc_w 400
      //   2759: fcmpg
      //   2760: ifge +63 -> 2823
      //   2763: aload 16
      //   2765: astore 14
      //   2767: aload 16
      //   2769: invokevirtual 276	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   2772: getstatic 237	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2775: if_acmpne +207 -> 2982
      //   2778: aload 16
      //   2780: astore 14
      //   2782: aload 18
      //   2784: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2787: ifeq +78 -> 2865
      //   2790: iconst_0
      //   2791: istore 5
      //   2793: aload 16
      //   2795: astore 14
      //   2797: aload 16
      //   2799: iconst_3
      //   2800: iload 5
      //   2802: aload 16
      //   2804: invokevirtual 279	android/graphics/Bitmap:getWidth	()I
      //   2807: aload 16
      //   2809: invokevirtual 282	android/graphics/Bitmap:getHeight	()I
      //   2812: aload 16
      //   2814: invokevirtual 285	android/graphics/Bitmap:getRowBytes	()I
      //   2817: invokestatic 289	org/telegram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   2820: goto +162 -> 2982
      //   2823: aload 17
      //   2825: astore 16
      //   2827: iload 5
      //   2829: ifne -2388 -> 441
      //   2832: aload 17
      //   2834: astore 16
      //   2836: aload 17
      //   2838: astore 14
      //   2840: aload 18
      //   2842: getfield 189	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2845: ifeq -2404 -> 441
      //   2848: aload 17
      //   2850: astore 14
      //   2852: aload 17
      //   2854: invokestatic 293	org/telegram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   2857: pop
      //   2858: aload 17
      //   2860: astore 16
      //   2862: goto -2421 -> 441
      //   2865: iconst_1
      //   2866: istore 5
      //   2868: goto -75 -> 2793
      //   2871: aconst_null
      //   2872: astore 14
      //   2874: goto -2413 -> 461
      //   2877: astore 14
      //   2879: goto -2315 -> 564
      //   2882: astore 16
      //   2884: goto -2378 -> 506
      //   2887: aconst_null
      //   2888: astore 15
      //   2890: iload 9
      //   2892: istore 6
      //   2894: fload_2
      //   2895: fstore_1
      //   2896: goto -1017 -> 1879
      //   2899: aconst_null
      //   2900: astore 14
      //   2902: goto -1984 -> 918
      //   2905: iconst_1
      //   2906: istore 5
      //   2908: goto -1871 -> 1037
      //   2911: iconst_1
      //   2912: istore 5
      //   2914: goto -1803 -> 1111
      //   2917: iconst_1
      //   2918: istore 5
      //   2920: goto -1735 -> 1185
      //   2923: iconst_1
      //   2924: istore 5
      //   2926: goto -1698 -> 1228
      //   2929: iconst_1
      //   2930: istore 5
      //   2932: goto -1661 -> 1271
      //   2935: iconst_0
      //   2936: istore 8
      //   2938: iconst_0
      //   2939: istore 5
      //   2941: bipush 20
      //   2943: istore 6
      //   2945: aload 17
      //   2947: ifnull -1510 -> 1437
      //   2950: iconst_0
      //   2951: istore 6
      //   2953: goto -1516 -> 1437
      //   2956: astore 14
      //   2958: aconst_null
      //   2959: astore 16
      //   2961: goto -2520 -> 441
      //   2964: iconst_0
      //   2965: istore 5
      //   2967: goto -26 -> 2941
      //   2970: iconst_0
      //   2971: istore 13
      //   2973: goto -727 -> 2246
      //   2976: aconst_null
      //   2977: astore 14
      //   2979: goto -526 -> 2453
      //   2982: iconst_1
      //   2983: istore 5
      //   2985: aload 16
      //   2987: astore 17
      //   2989: goto -166 -> 2823
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	2992	0	this	CacheOutTask
      //   1666	1230	1	f1	float
      //   1664	1231	2	f2	float
      //   1668	1072	3	f3	float
      //   1831	841	4	f4	float
      //   277	2707	5	i	int
      //   181	2771	6	j	int
      //   178	2574	7	k	int
      //   187	2750	8	m	int
      //   163	2728	9	n	int
      //   160	1403	10	i1	int
      //   175	1440	11	i2	int
      //   184	285	12	i3	int
      //   118	2854	13	bool	boolean
      //   425	8	14	localThrowable1	Throwable
      //   459	17	14	localObject2	Object
      //   480	5	14	localException1	Exception
      //   496	7	14	localException2	Exception
      //   508	1	14	localObject3	Object
      //   538	19	14	localException3	Exception
      //   562	290	14	localObject4	Object
      //   858	1	14	localThrowable2	Throwable
      //   2877	1	14	localObject6	Object
      //   2900	1	14	localObject7	Object
      //   2956	1	14	localThrowable3	Throwable
      //   2977	1	14	localObject8	Object
      //   64	6	15	localObject9	Object
      //   72	6	15	localObject10	Object
      //   214	119	15	localRandomAccessFile	RandomAccessFile
      //   417	6	15	localObject11	Object
      //   428	142	15	localObject12	Object
      //   577	5	15	localException4	Exception
      //   754	580	15	localObject13	Object
      //   1517	6	15	localObject14	Object
      //   1808	105	15	localObject15	Object
      //   1919	1	15	localThrowable4	Throwable
      //   1947	942	15	localObject16	Object
      //   201	312	16	localObject17	Object
      //   554	7	16	localObject18	Object
      //   683	2178	16	localObject19	Object
      //   2882	1	16	localException5	Exception
      //   2959	27	16	localObject20	Object
      //   645	1151	17	localObject21	Object
      //   1907	183	17	localObject22	Object
      //   2131	857	17	localObject23	Object
      //   157	2684	18	localObject24	Object
      //   151	2102	19	localMappedByteBuffer	java.nio.MappedByteBuffer
      //   154	1405	20	localObject25	Object
      //   172	2352	21	localFile	File
      // Exception table:
      //   from	to	target	type
      //   53	63	64	finally
      //   66	69	64	finally
      //   80	83	64	finally
      //   9	30	72	finally
      //   31	34	72	finally
      //   74	77	72	finally
      //   406	416	417	finally
      //   419	422	417	finally
      //   635	638	417	finally
      //   386	406	425	java/lang/Throwable
      //   422	425	425	java/lang/Throwable
      //   638	667	425	java/lang/Throwable
      //   672	752	425	java/lang/Throwable
      //   869	918	425	java/lang/Throwable
      //   927	939	425	java/lang/Throwable
      //   939	961	425	java/lang/Throwable
      //   964	985	425	java/lang/Throwable
      //   332	337	480	java/lang/Exception
      //   203	216	496	java/lang/Exception
      //   526	531	538	java/lang/Exception
      //   203	216	554	finally
      //   510	517	554	finally
      //   569	574	577	java/lang/Exception
      //   756	763	858	java/lang/Throwable
      //   767	775	858	java/lang/Throwable
      //   782	795	858	java/lang/Throwable
      //   799	804	858	java/lang/Throwable
      //   813	823	858	java/lang/Throwable
      //   831	841	858	java/lang/Throwable
      //   845	851	858	java/lang/Throwable
      //   989	994	858	java/lang/Throwable
      //   1011	1022	858	java/lang/Throwable
      //   1026	1034	858	java/lang/Throwable
      //   1041	1064	858	java/lang/Throwable
      //   1085	1096	858	java/lang/Throwable
      //   1100	1108	858	java/lang/Throwable
      //   1115	1138	858	java/lang/Throwable
      //   1159	1170	858	java/lang/Throwable
      //   1174	1182	858	java/lang/Throwable
      //   1189	1213	858	java/lang/Throwable
      //   1217	1225	858	java/lang/Throwable
      //   1232	1256	858	java/lang/Throwable
      //   1260	1268	858	java/lang/Throwable
      //   1275	1299	858	java/lang/Throwable
      //   1323	1331	858	java/lang/Throwable
      //   1335	1341	858	java/lang/Throwable
      //   1506	1516	1517	finally
      //   1519	1522	1517	finally
      //   1645	1648	1517	finally
      //   1896	1906	1907	finally
      //   1909	1912	1907	finally
      //   1985	1988	1907	finally
      //   1814	1821	1919	java/lang/Throwable
      //   1825	1833	1919	java/lang/Throwable
      //   1837	1848	1919	java/lang/Throwable
      //   1862	1868	1919	java/lang/Throwable
      //   1872	1879	1919	java/lang/Throwable
      //   1883	1889	1919	java/lang/Throwable
      //   1893	1896	1919	java/lang/Throwable
      //   1916	1919	1919	java/lang/Throwable
      //   1977	1982	1919	java/lang/Throwable
      //   1992	2002	1919	java/lang/Throwable
      //   2011	2021	1919	java/lang/Throwable
      //   2025	2033	1919	java/lang/Throwable
      //   2037	2045	1919	java/lang/Throwable
      //   2049	2055	1919	java/lang/Throwable
      //   2059	2065	1919	java/lang/Throwable
      //   2083	2102	1919	java/lang/Throwable
      //   2120	2133	1919	java/lang/Throwable
      //   2137	2156	1919	java/lang/Throwable
      //   2160	2169	1919	java/lang/Throwable
      //   2173	2179	1919	java/lang/Throwable
      //   2183	2198	1919	java/lang/Throwable
      //   2202	2220	1919	java/lang/Throwable
      //   2224	2231	1919	java/lang/Throwable
      //   2235	2243	1919	java/lang/Throwable
      //   2250	2263	1919	java/lang/Throwable
      //   2267	2272	1919	java/lang/Throwable
      //   2290	2300	1919	java/lang/Throwable
      //   2308	2318	1919	java/lang/Throwable
      //   2322	2328	1919	java/lang/Throwable
      //   2339	2347	1919	java/lang/Throwable
      //   2354	2373	1919	java/lang/Throwable
      //   2380	2388	1919	java/lang/Throwable
      //   2392	2405	1919	java/lang/Throwable
      //   2409	2417	1919	java/lang/Throwable
      //   2421	2427	1919	java/lang/Throwable
      //   2431	2440	1919	java/lang/Throwable
      //   2444	2449	1919	java/lang/Throwable
      //   2466	2472	1919	java/lang/Throwable
      //   2476	2482	1919	java/lang/Throwable
      //   2486	2496	1919	java/lang/Throwable
      //   2500	2512	1919	java/lang/Throwable
      //   2519	2530	1919	java/lang/Throwable
      //   2534	2544	1919	java/lang/Throwable
      //   2548	2553	1919	java/lang/Throwable
      //   2571	2581	1919	java/lang/Throwable
      //   2585	2592	1919	java/lang/Throwable
      //   2596	2603	1919	java/lang/Throwable
      //   2611	2619	1919	java/lang/Throwable
      //   2657	2662	1919	java/lang/Throwable
      //   2666	2681	1919	java/lang/Throwable
      //   2696	2701	1919	java/lang/Throwable
      //   2767	2778	1919	java/lang/Throwable
      //   2782	2790	1919	java/lang/Throwable
      //   2797	2820	1919	java/lang/Throwable
      //   2840	2848	1919	java/lang/Throwable
      //   2852	2858	1919	java/lang/Throwable
      //   220	230	2877	finally
      //   234	239	2877	finally
      //   243	254	2877	finally
      //   258	275	2877	finally
      //   283	293	2877	finally
      //   301	311	2877	finally
      //   318	323	2877	finally
      //   472	477	2877	finally
      //   220	230	2882	java/lang/Exception
      //   234	239	2882	java/lang/Exception
      //   243	254	2882	java/lang/Exception
      //   258	275	2882	java/lang/Exception
      //   283	293	2882	java/lang/Exception
      //   301	311	2882	java/lang/Exception
      //   318	323	2882	java/lang/Exception
      //   472	477	2882	java/lang/Exception
      //   1360	1403	2956	java/lang/Throwable
      //   1412	1434	2956	java/lang/Throwable
      //   1442	1486	2956	java/lang/Throwable
      //   1486	1506	2956	java/lang/Throwable
      //   1522	1525	2956	java/lang/Throwable
      //   1525	1558	2956	java/lang/Throwable
      //   1571	1593	2956	java/lang/Throwable
      //   1599	1614	2956	java/lang/Throwable
      //   1648	1663	2956	java/lang/Throwable
      //   1675	1700	2956	java/lang/Throwable
      //   1700	1731	2956	java/lang/Throwable
      //   1731	1746	2956	java/lang/Throwable
      //   1773	1779	2956	java/lang/Throwable
      //   1789	1807	2956	java/lang/Throwable
      //   1928	1946	2956	java/lang/Throwable
      //   1952	1973	2956	java/lang/Throwable
    }
  }
  
  private class HttpFileTask
    extends AsyncTask<Void, Void, Boolean>
  {
    private boolean canRetry = true;
    private String ext;
    private RandomAccessFile fileOutputStream = null;
    private int fileSize;
    private long lastProgressTime;
    private File tempFile;
    private String url;
    
    public HttpFileTask(String paramString1, File paramFile, String paramString2)
    {
      this.url = paramString1;
      this.tempFile = paramFile;
      this.ext = paramString2;
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
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpFileTask.this.url, Float.valueOf(ImageLoader.HttpFileTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      Object localObject4 = null;
      bool2 = false;
      bool5 = false;
      bool6 = false;
      bool4 = false;
      paramVarArgs = null;
      localObject2 = localObject4;
      try
      {
        Object localObject3 = new URL(this.url).openConnection();
        paramVarArgs = (Void[])localObject3;
        localObject2 = localObject4;
        ((URLConnection)localObject3).addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        paramVarArgs = (Void[])localObject3;
        localObject2 = localObject4;
        ((URLConnection)localObject3).addRequestProperty("Referer", "google.com");
        paramVarArgs = (Void[])localObject3;
        localObject2 = localObject4;
        ((URLConnection)localObject3).setConnectTimeout(5000);
        paramVarArgs = (Void[])localObject3;
        localObject2 = localObject4;
        ((URLConnection)localObject3).setReadTimeout(5000);
        Object localObject1 = localObject3;
        paramVarArgs = (Void[])localObject3;
        localObject2 = localObject4;
        if ((localObject3 instanceof HttpURLConnection))
        {
          paramVarArgs = (Void[])localObject3;
          localObject2 = localObject4;
          Object localObject5 = (HttpURLConnection)localObject3;
          paramVarArgs = (Void[])localObject3;
          localObject2 = localObject4;
          ((HttpURLConnection)localObject5).setInstanceFollowRedirects(true);
          paramVarArgs = (Void[])localObject3;
          localObject2 = localObject4;
          i = ((HttpURLConnection)localObject5).getResponseCode();
          if ((i != 302) && (i != 301))
          {
            localObject1 = localObject3;
            if (i != 303) {}
          }
          else
          {
            paramVarArgs = (Void[])localObject3;
            localObject2 = localObject4;
            localObject1 = ((HttpURLConnection)localObject5).getHeaderField("Location");
            paramVarArgs = (Void[])localObject3;
            localObject2 = localObject4;
            localObject5 = ((HttpURLConnection)localObject5).getHeaderField("Set-Cookie");
            paramVarArgs = (Void[])localObject3;
            localObject2 = localObject4;
            localObject1 = new URL((String)localObject1).openConnection();
            paramVarArgs = (Void[])localObject1;
            localObject2 = localObject4;
            ((URLConnection)localObject1).setRequestProperty("Cookie", (String)localObject5);
            paramVarArgs = (Void[])localObject1;
            localObject2 = localObject4;
            ((URLConnection)localObject1).addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
            paramVarArgs = (Void[])localObject1;
            localObject2 = localObject4;
            ((URLConnection)localObject1).addRequestProperty("Referer", "google.com");
          }
        }
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject4;
        ((URLConnection)localObject1).connect();
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject4;
        localObject3 = ((URLConnection)localObject1).getInputStream();
        paramVarArgs = (Void[])localObject1;
        localObject2 = localObject3;
        this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
        localObject2 = localObject3;
        paramVarArgs = (Void[])localObject1;
      }
      catch (Throwable paramVarArgs)
      {
        try
        {
          if (!(paramVarArgs instanceof HttpURLConnection)) {
            break label393;
          }
          i = ((HttpURLConnection)paramVarArgs).getResponseCode();
          if ((i == 200) || (i == 202) || (i == 304)) {
            break label393;
          }
          this.canRetry = false;
          if (paramVarArgs == null) {
            break label457;
          }
        }
        catch (Exception paramVarArgs)
        {
          try
          {
            paramVarArgs = paramVarArgs.getHeaderFields();
            if (paramVarArgs == null) {
              break label457;
            }
            paramVarArgs = (List)paramVarArgs.get("content-Length");
            if ((paramVarArgs == null) || (paramVarArgs.isEmpty())) {
              break label457;
            }
            paramVarArgs = (String)paramVarArgs.get(0);
            if (paramVarArgs == null) {
              break label457;
            }
            this.fileSize = Utilities.parseInt(paramVarArgs).intValue();
            bool1 = bool4;
            if (localObject2 == null) {
              break label496;
            }
            bool2 = bool6;
          }
          catch (Exception paramVarArgs)
          {
            try
            {
              paramVarArgs = new byte[32768];
              i = 0;
              bool2 = bool6;
              bool1 = isCancelled();
              if (!bool1) {
                break label661;
              }
              bool1 = bool4;
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
                  bool2 = bool1;
                  if (localObject2 != null) {}
                  try
                  {
                    ((InputStream)localObject2).close();
                    bool2 = bool1;
                  }
                  catch (Throwable paramVarArgs)
                  {
                    for (;;)
                    {
                      boolean bool3;
                      int j;
                      FileLog.e("tmessages", paramVarArgs);
                      bool2 = bool1;
                    }
                  }
                  return Boolean.valueOf(bool2);
                  localThrowable = localThrowable;
                  if ((localThrowable instanceof SocketTimeoutException)) {
                    if (ConnectionsManager.isNetworkOnline()) {
                      this.canRetry = false;
                    }
                  }
                  for (;;)
                  {
                    FileLog.e("tmessages", localThrowable);
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
                  FileLog.e("tmessages", localException);
                  continue;
                  paramVarArgs = paramVarArgs;
                  FileLog.e("tmessages", paramVarArgs);
                  continue;
                  bool3 = bool5;
                  bool2 = bool6;
                  try
                  {
                    j = ((InputStream)localObject2).read(paramVarArgs);
                    if (j <= 0) {
                      break label776;
                    }
                    bool3 = bool5;
                    bool2 = bool6;
                    this.fileOutputStream.write(paramVarArgs, 0, j);
                    j = i + j;
                    i = j;
                    bool3 = bool5;
                    bool2 = bool6;
                    if (this.fileSize <= 0) {
                      continue;
                    }
                    bool3 = bool5;
                    bool2 = bool6;
                    reportProgress(j / this.fileSize);
                    i = j;
                  }
                  catch (Exception paramVarArgs)
                  {
                    bool2 = bool3;
                    FileLog.e("tmessages", paramVarArgs);
                    bool1 = bool3;
                  }
                  continue;
                  paramVarArgs = paramVarArgs;
                  FileLog.e("tmessages", paramVarArgs);
                  bool1 = bool2;
                  continue;
                  bool1 = bool4;
                  if (j == -1)
                  {
                    bool5 = true;
                    bool6 = true;
                    bool4 = true;
                    bool1 = bool4;
                    bool3 = bool5;
                    bool2 = bool6;
                    if (this.fileSize != 0)
                    {
                      bool3 = bool5;
                      bool2 = bool6;
                      reportProgress(1.0F);
                      bool1 = bool4;
                    }
                  }
                }
              }
              catch (Throwable paramVarArgs)
              {
                for (;;)
                {
                  FileLog.e("tmessages", paramVarArgs);
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
    private URLConnection httpConnection = null;
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
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Float.valueOf(ImageLoader.HttpImageTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      Object localObject3 = null;
      Object localObject2 = null;
      bool5 = false;
      bool6 = false;
      bool4 = false;
      paramVarArgs = (Void[])localObject2;
      if (!isCancelled()) {
        localObject1 = localObject3;
      }
      try
      {
        this.httpConnection = new URL(this.cacheImage.httpUrl).openConnection();
        localObject1 = localObject3;
        this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        localObject1 = localObject3;
        this.httpConnection.addRequestProperty("Referer", "google.com");
        localObject1 = localObject3;
        this.httpConnection.setConnectTimeout(5000);
        localObject1 = localObject3;
        this.httpConnection.setReadTimeout(5000);
        localObject1 = localObject3;
        if ((this.httpConnection instanceof HttpURLConnection))
        {
          localObject1 = localObject3;
          ((HttpURLConnection)this.httpConnection).setInstanceFollowRedirects(true);
        }
        paramVarArgs = (Void[])localObject2;
        localObject1 = localObject3;
        if (!isCancelled())
        {
          localObject1 = localObject3;
          this.httpConnection.connect();
          localObject1 = localObject3;
          paramVarArgs = this.httpConnection.getInputStream();
          localObject1 = paramVarArgs;
          this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
        }
      }
      catch (Throwable localThrowable2)
      {
        try
        {
          if ((this.httpConnection == null) || (!(this.httpConnection instanceof HttpURLConnection))) {
            break label262;
          }
          i = ((HttpURLConnection)this.httpConnection).getResponseCode();
          if ((i == 200) || (i == 202) || (i == 304)) {
            break label262;
          }
          this.canRetry = false;
          if ((this.imageSize != 0) || (this.httpConnection == null)) {
            break label349;
          }
        }
        catch (Exception localThrowable2)
        {
          try
          {
            localObject1 = this.httpConnection.getHeaderFields();
            if (localObject1 == null) {
              break label349;
            }
            localObject1 = (List)((Map)localObject1).get("content-Length");
            if ((localObject1 == null) || (((List)localObject1).isEmpty())) {
              break label349;
            }
            localObject1 = (String)((List)localObject1).get(0);
            if (localObject1 == null) {
              break label349;
            }
            this.imageSize = Utilities.parseInt((String)localObject1).intValue();
            bool2 = bool4;
            if (paramVarArgs == null) {
              break label389;
            }
            bool1 = bool6;
          }
          catch (Exception localThrowable2)
          {
            try
            {
              localObject1 = new byte[' '];
              i = 0;
              bool1 = bool6;
              bool2 = isCancelled();
              if (!bool2) {
                break label590;
              }
              bool2 = bool4;
            }
            catch (Throwable localThrowable2)
            {
              try
              {
                if (this.fileOutputStream == null) {
                  break label408;
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
                if (paramVarArgs == null) {
                  break label416;
                }
              }
              catch (Throwable localThrowable2)
              {
                try
                {
                  for (;;)
                  {
                    int i;
                    paramVarArgs.close();
                    if ((bool2) && (this.cacheImage.tempFilePath != null) && (!this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                      this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                    }
                    return Boolean.valueOf(bool2);
                    paramVarArgs = paramVarArgs;
                    if ((paramVarArgs instanceof SocketTimeoutException)) {
                      if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                      }
                    }
                    for (;;)
                    {
                      FileLog.e("tmessages", paramVarArgs);
                      paramVarArgs = (Void[])localObject1;
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
                    FileLog.e("tmessages", localException1);
                    continue;
                    localException2 = localException2;
                    FileLog.e("tmessages", localException2);
                    continue;
                    boolean bool3 = bool5;
                    boolean bool1 = bool6;
                    int k;
                    try
                    {
                      k = paramVarArgs.read(localException2);
                      if (k <= 0) {
                        break label714;
                      }
                      int j = i + k;
                      bool3 = bool5;
                      bool1 = bool6;
                      this.fileOutputStream.write(localException2, 0, k);
                      i = j;
                      bool3 = bool5;
                      bool1 = bool6;
                      if (this.imageSize == 0) {
                        continue;
                      }
                      bool3 = bool5;
                      bool1 = bool6;
                      reportProgress(j / this.imageSize);
                      i = j;
                    }
                    catch (Exception localException3)
                    {
                      bool1 = bool3;
                      FileLog.e("tmessages", localException3);
                      bool2 = bool3;
                    }
                    continue;
                    localThrowable1 = localThrowable1;
                    FileLog.e("tmessages", localThrowable1);
                    boolean bool2 = bool1;
                    continue;
                    bool2 = bool4;
                    if (k == -1)
                    {
                      bool5 = true;
                      bool6 = true;
                      bool4 = true;
                      bool2 = bool4;
                      bool3 = bool5;
                      bool1 = bool6;
                      if (this.imageSize != 0)
                      {
                        bool3 = bool5;
                        bool1 = bool6;
                        reportProgress(1.0F);
                        bool2 = bool4;
                      }
                    }
                  }
                  localThrowable2 = localThrowable2;
                  FileLog.e("tmessages", localThrowable2);
                }
                catch (Throwable paramVarArgs)
                {
                  for (;;)
                  {
                    FileLog.e("tmessages", paramVarArgs);
                  }
                }
              }
            }
          }
        }
      }
      bool2 = bool4;
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
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Integer.valueOf(1) });
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
                if (ImageLoader.HttpImageTask.2.this.val$result.booleanValue())
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url });
                  return;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.this.cacheImage.url, Integer.valueOf(2) });
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
      if (this.thumbLocation == null) {
        return;
      }
      final String str = FileLoader.getAttachFileName(this.thumbLocation);
      ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.thumbGenerateTasks.remove(str);
        }
      });
    }
    
    public void run()
    {
      final String str;
      File localFile;
      try
      {
        if (this.thumbLocation == null)
        {
          removeTask();
          return;
        }
        str = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
        localFile = new File(FileLoader.getInstance().getDirectory(4), "q_" + str + ".jpg");
        if ((localFile.exists()) || (!this.originalPath.exists()))
        {
          removeTask();
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e("tmessages", localThrowable);
        removeTask();
        return;
      }
      int i = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
      Object localObject = null;
      if (this.mediaType == 0) {
        localObject = ImageLoader.loadBitmap(this.originalPath.toString(), null, i, i, false);
      }
      while (localObject == null)
      {
        removeTask();
        return;
        if (this.mediaType == 2)
        {
          localObject = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
        }
        else if (this.mediaType == 3)
        {
          localObject = this.originalPath.toString().toLowerCase();
          if ((!((String)localObject).endsWith(".jpg")) && (!((String)localObject).endsWith(".jpeg")) && (!((String)localObject).endsWith(".png")) && (!((String)localObject).endsWith(".gif")))
          {
            removeTask();
            return;
          }
          localObject = ImageLoader.loadBitmap((String)localObject, null, i, i, false);
        }
      }
      int j = ((Bitmap)localObject).getWidth();
      int k = ((Bitmap)localObject).getHeight();
      if ((j == 0) || (k == 0))
      {
        removeTask();
        return;
      }
      float f = Math.min(j / i, k / i);
      Bitmap localBitmap = Bitmaps.createScaledBitmap((Bitmap)localObject, (int)(j / f), (int)(k / f), true);
      if (localBitmap != localObject) {
        ((Bitmap)localObject).recycle();
      }
      localObject = new FileOutputStream(localFile);
      localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, (OutputStream)localObject);
      try
      {
        ((FileOutputStream)localObject).close();
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            ImageLoader.ThumbGenerateTask.this.removeTask();
            String str2 = str;
            String str1 = str2;
            if (ImageLoader.ThumbGenerateTask.this.filter != null) {
              str1 = str2 + "@" + ImageLoader.ThumbGenerateTask.this.filter;
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageThumbGenerated, new Object[] { this.val$bitmapDrawable, str1 });
            ImageLoader.this.memCache.put(str1, this.val$bitmapDrawable);
          }
        });
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  public class VMRuntimeHack
  {
    private Object runtime = null;
    private Method trackAllocation = null;
    private Method trackFree = null;
    
    public VMRuntimeHack()
    {
      try
      {
        this$1 = Class.forName("dalvik.system.VMRuntime");
        this.runtime = ImageLoader.this.getMethod("getRuntime", new Class[0]).invoke(null, new Object[0]);
        this.trackAllocation = ImageLoader.this.getMethod("trackExternalAllocation", new Class[] { Long.TYPE });
        this.trackFree = ImageLoader.this.getMethod("trackExternalFree", new Class[] { Long.TYPE });
        return;
      }
      catch (Exception this$1)
      {
        FileLog.e("tmessages", ImageLoader.this);
        this.runtime = null;
        this.trackAllocation = null;
        this.trackFree = null;
      }
    }
    
    public boolean trackAlloc(long paramLong)
    {
      boolean bool = true;
      if (this.runtime == null) {
        bool = false;
      }
      for (;;)
      {
        return bool;
        try
        {
          Object localObject = this.trackAllocation.invoke(this.runtime, new Object[] { Long.valueOf(paramLong) });
          if ((localObject instanceof Boolean))
          {
            bool = ((Boolean)localObject).booleanValue();
            return bool;
          }
        }
        catch (Exception localException) {}
      }
      return false;
    }
    
    public boolean trackFree(long paramLong)
    {
      boolean bool = true;
      if (this.runtime == null) {
        bool = false;
      }
      for (;;)
      {
        return bool;
        try
        {
          Object localObject = this.trackFree.invoke(this.runtime, new Object[] { Long.valueOf(paramLong) });
          if ((localObject instanceof Boolean))
          {
            bool = ((Boolean)localObject).booleanValue();
            return bool;
          }
        }
        catch (Exception localException) {}
      }
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ImageLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */