package net.hockeyapp.android.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
  private boolean downloadRunning = false;
  private Queue<DownloadJob> queue = new LinkedList();
  
  private void downloadNext()
  {
    if (this.downloadRunning) {}
    do
    {
      return;
      localObject = (DownloadJob)this.queue.peek();
    } while (localObject == null);
    Object localObject = new DownloadTask((DownloadJob)localObject, new Handler()
    {
      public void handleMessage(final Message paramAnonymousMessage)
      {
        paramAnonymousMessage = (AttachmentDownloader.DownloadJob)AttachmentDownloader.this.queue.poll();
        if ((!paramAnonymousMessage.isSuccess()) && (paramAnonymousMessage.consumeRetry())) {
          postDelayed(new Runnable()
          {
            public void run()
            {
              AttachmentDownloader.this.queue.add(paramAnonymousMessage);
              AttachmentDownloader.this.downloadNext();
            }
          }, 3000L);
        }
        AttachmentDownloader.access$402(AttachmentDownloader.this, false);
        AttachmentDownloader.this.downloadNext();
      }
    });
    this.downloadRunning = true;
    AsyncTaskUtils.execute((AsyncTask)localObject);
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
    public static final AttachmentDownloader INSTANCE = new AttachmentDownloader(null);
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
    
    public boolean consumeRetry()
    {
      int i = this.remainingRetries - 1;
      this.remainingRetries = i;
      return i >= 0;
    }
    
    public AttachmentView getAttachmentView()
    {
      return this.attachmentView;
    }
    
    public FeedbackAttachment getFeedbackAttachment()
    {
      return this.feedbackAttachment;
    }
    
    public boolean hasRetry()
    {
      return this.remainingRetries > 0;
    }
    
    public boolean isSuccess()
    {
      return this.success;
    }
    
    public void setSuccess(boolean paramBoolean)
    {
      this.success = paramBoolean;
    }
  }
  
  private static class DownloadTask
    extends AsyncTask<Void, Integer, Boolean>
  {
    private Bitmap bitmap;
    private int bitmapOrientation;
    private final AttachmentDownloader.DownloadJob downloadJob;
    private File dropFolder;
    private final Handler handler;
    
    public DownloadTask(AttachmentDownloader.DownloadJob paramDownloadJob, Handler paramHandler)
    {
      this.downloadJob = paramDownloadJob;
      this.handler = paramHandler;
      this.dropFolder = Constants.getHockeyAppStorageDir();
      this.bitmap = null;
      this.bitmapOrientation = 0;
    }
    
    private URLConnection createConnection(URL paramURL)
      throws IOException
    {
      paramURL = (HttpURLConnection)paramURL.openConnection();
      paramURL.addRequestProperty("User-Agent", "HockeySDK/Android");
      paramURL.setInstanceFollowRedirects(true);
      if (Build.VERSION.SDK_INT <= 9) {
        paramURL.setRequestProperty("connection", "close");
      }
      return paramURL;
    }
    
    private boolean downloadAttachment(String paramString1, String paramString2)
    {
      long l;
      try
      {
        paramString1 = createConnection(new URL(paramString1));
        paramString1.connect();
        int i = paramString1.getContentLength();
        Object localObject = paramString1.getHeaderField("Status");
        if ((localObject != null) && (!((String)localObject).startsWith("200"))) {
          return false;
        }
        paramString2 = new File(this.dropFolder, paramString2);
        paramString1 = new BufferedInputStream(paramString1.getInputStream());
        paramString2 = new FileOutputStream(paramString2);
        localObject = new byte['Ѐ'];
        l = 0L;
        for (;;)
        {
          int j = paramString1.read((byte[])localObject);
          if (j == -1) {
            break;
          }
          l += j;
          publishProgress(new Integer[] { Integer.valueOf((int)(100L * l / i)) });
          paramString2.write((byte[])localObject, 0, j);
        }
        paramString2.flush();
      }
      catch (IOException paramString1)
      {
        paramString1.printStackTrace();
        return false;
      }
      paramString2.close();
      paramString1.close();
      return l > 0L;
    }
    
    private void loadImageThumbnail()
    {
      try
      {
        String str = this.downloadJob.getFeedbackAttachment().getCacheId();
        AttachmentView localAttachmentView = this.downloadJob.getAttachmentView();
        this.bitmapOrientation = ImageUtils.determineOrientation(new File(this.dropFolder, str));
        int i;
        if (this.bitmapOrientation == 1)
        {
          i = localAttachmentView.getWidthLandscape();
          if (this.bitmapOrientation != 1) {
            break label98;
          }
        }
        label98:
        for (int j = localAttachmentView.getMaxHeightLandscape();; j = localAttachmentView.getMaxHeightPortrait())
        {
          this.bitmap = ImageUtils.decodeSampledBitmap(new File(this.dropFolder, str), i, j);
          return;
          i = localAttachmentView.getWidthPortrait();
          break;
        }
        return;
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        this.bitmap = null;
      }
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = this.downloadJob.getFeedbackAttachment();
      if (paramVarArgs.isAvailableInCache())
      {
        HockeyLog.error("Cached...");
        loadImageThumbnail();
        return Boolean.valueOf(true);
      }
      HockeyLog.error("Downloading...");
      boolean bool = downloadAttachment(paramVarArgs.getUrl(), paramVarArgs.getCacheId());
      if (bool) {
        loadImageThumbnail();
      }
      return Boolean.valueOf(bool);
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