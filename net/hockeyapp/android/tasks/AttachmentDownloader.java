package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.views.AttachmentView;

public class AttachmentDownloader
{
  private final Handler downloadHandler = new DownloadHandler(this);
  private boolean downloadRunning = false;
  private Queue<DownloadJob> queue = new LinkedList();
  
  private void downloadNext()
  {
    if (this.downloadRunning) {}
    for (;;)
    {
      return;
      DownloadJob localDownloadJob = (DownloadJob)this.queue.peek();
      if (localDownloadJob != null)
      {
        this.downloadRunning = true;
        AsyncTaskUtils.execute(new DownloadTask(localDownloadJob, this.downloadHandler));
      }
    }
  }
  
  public static AttachmentDownloader getInstance()
  {
    return AttachmentDownloaderHolder.INSTANCE;
  }
  
  public void download(FeedbackAttachment paramFeedbackAttachment, AttachmentView paramAttachmentView)
  {
    this.queue.add(new DownloadJob(paramFeedbackAttachment, paramAttachmentView, null));
    downloadNext();
  }
  
  private static class AttachmentDownloaderHolder
  {
    static final AttachmentDownloader INSTANCE = new AttachmentDownloader(null);
  }
  
  private static class DownloadHandler
    extends Handler
  {
    private final AttachmentDownloader downloader;
    
    DownloadHandler(AttachmentDownloader paramAttachmentDownloader)
    {
      this.downloader = paramAttachmentDownloader;
    }
    
    public void handleMessage(final Message paramMessage)
    {
      paramMessage = (AttachmentDownloader.DownloadJob)this.downloader.queue.poll();
      if ((!paramMessage.isSuccess()) && (paramMessage.consumeRetry())) {
        postDelayed(new Runnable()
        {
          public void run()
          {
            AttachmentDownloader.this.queue.add(paramMessage);
            AttachmentDownloader.this.downloadNext();
          }
        }, 3000L);
      }
      AttachmentDownloader.access$502(this.downloader, false);
      this.downloader.downloadNext();
    }
  }
  
  private static class DownloadJob
  {
    private final AttachmentView attachmentView;
    private final FeedbackAttachment feedbackAttachment;
    private int remainingRetries;
    private boolean success;
    
    private DownloadJob(FeedbackAttachment paramFeedbackAttachment, AttachmentView paramAttachmentView)
    {
      this.feedbackAttachment = paramFeedbackAttachment;
      this.attachmentView = paramAttachmentView;
      this.success = false;
      this.remainingRetries = 2;
    }
    
    boolean consumeRetry()
    {
      int i = this.remainingRetries - 1;
      this.remainingRetries = i;
      if (i >= 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    AttachmentView getAttachmentView()
    {
      return this.attachmentView;
    }
    
    FeedbackAttachment getFeedbackAttachment()
    {
      return this.feedbackAttachment;
    }
    
    boolean hasRetry()
    {
      if (this.remainingRetries > 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isSuccess()
    {
      return this.success;
    }
    
    void setSuccess(boolean paramBoolean)
    {
      this.success = paramBoolean;
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private static class DownloadTask
    extends AsyncTask<Void, Integer, Boolean>
  {
    private Bitmap bitmap;
    private int bitmapOrientation;
    private final Context context;
    private final AttachmentDownloader.DownloadJob downloadJob;
    private final Handler handler;
    
    DownloadTask(AttachmentDownloader.DownloadJob paramDownloadJob, Handler paramHandler)
    {
      this.downloadJob = paramDownloadJob;
      this.handler = paramHandler;
      this.context = paramDownloadJob.getAttachmentView().getContext();
      this.bitmap = null;
      this.bitmapOrientation = 1;
    }
    
    private URLConnection createConnection(URL paramURL)
      throws IOException
    {
      paramURL = (HttpURLConnection)paramURL.openConnection();
      paramURL.addRequestProperty("User-Agent", "HockeySDK/Android 5.0.4");
      paramURL.setInstanceFollowRedirects(true);
      return paramURL;
    }
    
    /* Error */
    private boolean downloadAttachment(String paramString, File paramFile)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: aconst_null
      //   3: astore 4
      //   5: aconst_null
      //   6: astore 5
      //   8: aconst_null
      //   9: astore 6
      //   11: aconst_null
      //   12: astore 7
      //   14: aconst_null
      //   15: astore 8
      //   17: aconst_null
      //   18: astore 9
      //   20: aload 9
      //   22: astore 10
      //   24: aload_3
      //   25: astore 11
      //   27: aload 5
      //   29: astore 12
      //   31: aload 8
      //   33: astore 13
      //   35: new 55	java/net/URL
      //   38: astore 14
      //   40: aload 9
      //   42: astore 10
      //   44: aload_3
      //   45: astore 11
      //   47: aload 5
      //   49: astore 12
      //   51: aload 8
      //   53: astore 13
      //   55: aload 14
      //   57: aload_1
      //   58: invokespecial 79	java/net/URL:<init>	(Ljava/lang/String;)V
      //   61: aload 9
      //   63: astore 10
      //   65: aload_3
      //   66: astore 11
      //   68: aload 5
      //   70: astore 12
      //   72: aload 8
      //   74: astore 13
      //   76: aload_0
      //   77: aload 14
      //   79: invokespecial 81	net/hockeyapp/android/tasks/AttachmentDownloader$DownloadTask:createConnection	(Ljava/net/URL;)Ljava/net/URLConnection;
      //   82: checkcast 61	java/net/HttpURLConnection
      //   85: astore_1
      //   86: aload_1
      //   87: astore 10
      //   89: aload_3
      //   90: astore 11
      //   92: aload 5
      //   94: astore 12
      //   96: aload_1
      //   97: astore 13
      //   99: aload_1
      //   100: invokevirtual 84	java/net/HttpURLConnection:connect	()V
      //   103: aload_1
      //   104: astore 10
      //   106: aload_3
      //   107: astore 11
      //   109: aload 5
      //   111: astore 12
      //   113: aload_1
      //   114: astore 13
      //   116: aload_1
      //   117: invokevirtual 88	java/net/HttpURLConnection:getContentLength	()I
      //   120: istore 15
      //   122: aload_1
      //   123: astore 10
      //   125: aload_3
      //   126: astore 11
      //   128: aload 5
      //   130: astore 12
      //   132: aload_1
      //   133: astore 13
      //   135: aload_1
      //   136: ldc 90
      //   138: invokevirtual 94	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
      //   141: astore 9
      //   143: aload 9
      //   145: ifnull +76 -> 221
      //   148: aload_1
      //   149: astore 10
      //   151: aload_3
      //   152: astore 11
      //   154: aload 5
      //   156: astore 12
      //   158: aload_1
      //   159: astore 13
      //   161: aload 9
      //   163: ldc 96
      //   165: invokevirtual 102	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   168: istore 16
      //   170: iload 16
      //   172: ifne +49 -> 221
      //   175: iconst_0
      //   176: istore 17
      //   178: iconst_0
      //   179: ifeq +11 -> 190
      //   182: new 104	java/lang/NullPointerException
      //   185: dup
      //   186: invokespecial 105	java/lang/NullPointerException:<init>	()V
      //   189: athrow
      //   190: iconst_0
      //   191: ifeq +11 -> 202
      //   194: new 104	java/lang/NullPointerException
      //   197: dup
      //   198: invokespecial 105	java/lang/NullPointerException:<init>	()V
      //   201: athrow
      //   202: iload 17
      //   204: istore 16
      //   206: aload_1
      //   207: ifnull +11 -> 218
      //   210: aload_1
      //   211: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   214: iload 17
      //   216: istore 16
      //   218: iload 16
      //   220: ireturn
      //   221: aload_1
      //   222: astore 10
      //   224: aload_3
      //   225: astore 11
      //   227: aload 5
      //   229: astore 12
      //   231: aload_1
      //   232: astore 13
      //   234: new 110	java/io/BufferedInputStream
      //   237: astore 9
      //   239: aload_1
      //   240: astore 10
      //   242: aload_3
      //   243: astore 11
      //   245: aload 5
      //   247: astore 12
      //   249: aload_1
      //   250: astore 13
      //   252: aload 9
      //   254: aload_1
      //   255: invokevirtual 114	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
      //   258: invokespecial 117	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
      //   261: new 119	java/io/FileOutputStream
      //   264: astore 10
      //   266: aload 10
      //   268: aload_2
      //   269: invokespecial 122	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   272: sipush 1024
      //   275: newarray <illegal type>
      //   277: astore 11
      //   279: lconst_0
      //   280: lstore 18
      //   282: aload 9
      //   284: aload 11
      //   286: invokevirtual 128	java/io/InputStream:read	([B)I
      //   289: istore 20
      //   291: iload 20
      //   293: iconst_m1
      //   294: if_icmpeq +159 -> 453
      //   297: lload 18
      //   299: iload 20
      //   301: i2l
      //   302: ladd
      //   303: lstore 18
      //   305: aload_0
      //   306: iconst_1
      //   307: anewarray 130	java/lang/Integer
      //   310: dup
      //   311: iconst_0
      //   312: ldc2_w 131
      //   315: lload 18
      //   317: lmul
      //   318: iload 15
      //   320: i2l
      //   321: ldiv
      //   322: l2i
      //   323: invokestatic 136	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   326: aastore
      //   327: invokevirtual 140	net/hockeyapp/android/tasks/AttachmentDownloader$DownloadTask:publishProgress	([Ljava/lang/Object;)V
      //   330: aload 10
      //   332: aload 11
      //   334: iconst_0
      //   335: iload 20
      //   337: invokevirtual 146	java/io/OutputStream:write	([BII)V
      //   340: goto -58 -> 282
      //   343: astore 13
      //   345: aload 10
      //   347: astore 7
      //   349: aload_1
      //   350: astore 10
      //   352: aload 9
      //   354: astore 11
      //   356: aload 7
      //   358: astore 12
      //   360: new 148	java/lang/StringBuilder
      //   363: astore 6
      //   365: aload_1
      //   366: astore 10
      //   368: aload 9
      //   370: astore 11
      //   372: aload 7
      //   374: astore 12
      //   376: aload 6
      //   378: invokespecial 149	java/lang/StringBuilder:<init>	()V
      //   381: aload_1
      //   382: astore 10
      //   384: aload 9
      //   386: astore 11
      //   388: aload 7
      //   390: astore 12
      //   392: aload 6
      //   394: ldc -105
      //   396: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   399: aload_2
      //   400: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   403: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   406: aload 13
      //   408: invokestatic 168	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   411: iconst_0
      //   412: istore 17
      //   414: aload 7
      //   416: ifnull +8 -> 424
      //   419: aload 7
      //   421: invokevirtual 171	java/io/OutputStream:close	()V
      //   424: aload 9
      //   426: ifnull +8 -> 434
      //   429: aload 9
      //   431: invokevirtual 172	java/io/InputStream:close	()V
      //   434: iload 17
      //   436: istore 16
      //   438: aload_1
      //   439: ifnull -221 -> 218
      //   442: aload_1
      //   443: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   446: iload 17
      //   448: istore 16
      //   450: goto -232 -> 218
      //   453: aload 10
      //   455: invokevirtual 175	java/io/OutputStream:flush	()V
      //   458: lload 18
      //   460: lconst_0
      //   461: lcmp
      //   462: ifle +37 -> 499
      //   465: iconst_1
      //   466: istore 16
      //   468: aload 10
      //   470: ifnull +8 -> 478
      //   473: aload 10
      //   475: invokevirtual 171	java/io/OutputStream:close	()V
      //   478: aload 9
      //   480: ifnull +8 -> 488
      //   483: aload 9
      //   485: invokevirtual 172	java/io/InputStream:close	()V
      //   488: aload_1
      //   489: ifnull +7 -> 496
      //   492: aload_1
      //   493: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   496: goto -278 -> 218
      //   499: iconst_0
      //   500: istore 16
      //   502: goto -34 -> 468
      //   505: astore_1
      //   506: aload 12
      //   508: ifnull +8 -> 516
      //   511: aload 12
      //   513: invokevirtual 171	java/io/OutputStream:close	()V
      //   516: aload 11
      //   518: ifnull +8 -> 526
      //   521: aload 11
      //   523: invokevirtual 172	java/io/InputStream:close	()V
      //   526: aload 10
      //   528: ifnull +8 -> 536
      //   531: aload 10
      //   533: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   536: aload_1
      //   537: athrow
      //   538: astore_2
      //   539: goto -13 -> 526
      //   542: astore_2
      //   543: aload_1
      //   544: astore 10
      //   546: aload 9
      //   548: astore 11
      //   550: aload 6
      //   552: astore 12
      //   554: aload_2
      //   555: astore_1
      //   556: goto -50 -> 506
      //   559: astore_2
      //   560: aload 10
      //   562: astore 12
      //   564: aload_1
      //   565: astore 10
      //   567: aload 9
      //   569: astore 11
      //   571: aload_2
      //   572: astore_1
      //   573: goto -67 -> 506
      //   576: astore_2
      //   577: goto -143 -> 434
      //   580: astore 10
      //   582: aload 13
      //   584: astore_1
      //   585: aload 10
      //   587: astore 13
      //   589: aload 4
      //   591: astore 9
      //   593: goto -244 -> 349
      //   596: astore 13
      //   598: goto -249 -> 349
      //   601: astore_2
      //   602: goto -114 -> 488
      //   605: astore_2
      //   606: goto -404 -> 202
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	609	0	this	DownloadTask
      //   0	609	1	paramString	String
      //   0	609	2	paramFile	File
      //   1	242	3	localObject1	Object
      //   3	587	4	localObject2	Object
      //   6	240	5	localObject3	Object
      //   9	542	6	localStringBuilder	StringBuilder
      //   12	408	7	localObject4	Object
      //   15	58	8	localObject5	Object
      //   18	574	9	localObject6	Object
      //   22	544	10	localObject7	Object
      //   580	6	10	localIOException1	IOException
      //   25	545	11	localObject8	Object
      //   29	534	12	localObject9	Object
      //   33	218	13	localObject10	Object
      //   343	240	13	localIOException2	IOException
      //   587	1	13	localIOException3	IOException
      //   596	1	13	localIOException4	IOException
      //   38	40	14	localURL	URL
      //   120	199	15	i	int
      //   168	333	16	bool1	boolean
      //   176	271	17	bool2	boolean
      //   280	179	18	l	long
      //   289	47	20	j	int
      // Exception table:
      //   from	to	target	type
      //   272	279	343	java/io/IOException
      //   282	291	343	java/io/IOException
      //   305	340	343	java/io/IOException
      //   453	458	343	java/io/IOException
      //   35	40	505	finally
      //   55	61	505	finally
      //   76	86	505	finally
      //   99	103	505	finally
      //   116	122	505	finally
      //   135	143	505	finally
      //   161	170	505	finally
      //   234	239	505	finally
      //   252	261	505	finally
      //   360	365	505	finally
      //   376	381	505	finally
      //   392	411	505	finally
      //   511	516	538	java/io/IOException
      //   521	526	538	java/io/IOException
      //   261	272	542	finally
      //   272	279	559	finally
      //   282	291	559	finally
      //   305	340	559	finally
      //   453	458	559	finally
      //   419	424	576	java/io/IOException
      //   429	434	576	java/io/IOException
      //   35	40	580	java/io/IOException
      //   55	61	580	java/io/IOException
      //   76	86	580	java/io/IOException
      //   99	103	580	java/io/IOException
      //   116	122	580	java/io/IOException
      //   135	143	580	java/io/IOException
      //   161	170	580	java/io/IOException
      //   234	239	580	java/io/IOException
      //   252	261	580	java/io/IOException
      //   261	272	596	java/io/IOException
      //   473	478	601	java/io/IOException
      //   483	488	601	java/io/IOException
      //   182	190	605	java/io/IOException
      //   194	202	605	java/io/IOException
    }
    
    private void loadImageThumbnail(File paramFile)
    {
      for (;;)
      {
        try
        {
          localAttachmentView = this.downloadJob.getAttachmentView();
          this.bitmapOrientation = ImageUtils.determineOrientation(paramFile);
          if (this.bitmapOrientation == 0)
          {
            i = localAttachmentView.getWidthLandscape();
            if (this.bitmapOrientation != 0) {
              continue;
            }
            j = localAttachmentView.getMaxHeightLandscape();
            this.bitmap = ImageUtils.decodeSampledBitmap(paramFile, i, j);
            return;
          }
        }
        catch (IOException paramFile)
        {
          AttachmentView localAttachmentView;
          int i;
          int j;
          HockeyLog.error("Failed to load image thumbnail", paramFile);
          this.bitmap = null;
          continue;
        }
        i = localAttachmentView.getWidthPortrait();
        continue;
        j = localAttachmentView.getMaxHeightPortrait();
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      FeedbackAttachment localFeedbackAttachment = this.downloadJob.getFeedbackAttachment();
      paramVarArgs = new File(Constants.getHockeyAppStorageDir(this.context), localFeedbackAttachment.getCacheId());
      if (paramVarArgs.exists())
      {
        HockeyLog.error("Cached...");
        loadImageThumbnail(paramVarArgs);
      }
      boolean bool;
      for (paramVarArgs = Boolean.valueOf(true);; paramVarArgs = Boolean.valueOf(bool))
      {
        return paramVarArgs;
        HockeyLog.error("Downloading...");
        bool = downloadAttachment(localFeedbackAttachment.getUrl(), paramVarArgs);
        if (bool) {
          loadImageThumbnail(paramVarArgs);
        }
      }
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      AttachmentView localAttachmentView = this.downloadJob.getAttachmentView();
      this.downloadJob.setSuccess(paramBoolean.booleanValue());
      if (paramBoolean.booleanValue()) {
        localAttachmentView.setImage(this.bitmap, this.bitmapOrientation);
      }
      for (;;)
      {
        this.handler.sendEmptyMessage(0);
        return;
        if (!this.downloadJob.hasRetry()) {
          localAttachmentView.signalImageLoadingError();
        }
      }
    }
    
    protected void onPreExecute() {}
    
    protected void onProgressUpdate(Integer... paramVarArgs) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/AttachmentDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */