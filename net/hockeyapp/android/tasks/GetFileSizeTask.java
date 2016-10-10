package net.hockeyapp.android.tasks;

import android.content.Context;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import net.hockeyapp.android.listeners.DownloadFileListener;

public class GetFileSizeTask
  extends DownloadFileTask
{
  private long mSize;
  
  public GetFileSizeTask(Context paramContext, String paramString, DownloadFileListener paramDownloadFileListener)
  {
    super(paramContext, paramString, paramDownloadFileListener);
  }
  
  protected Long doInBackground(Void... paramVarArgs)
  {
    try
    {
      long l = createConnection(new URL(getURLString()), 6).getContentLength();
      return Long.valueOf(l);
    }
    catch (IOException paramVarArgs)
    {
      paramVarArgs.printStackTrace();
    }
    return Long.valueOf(0L);
  }
  
  public long getSize()
  {
    return this.mSize;
  }
  
  protected void onPostExecute(Long paramLong)
  {
    this.mSize = paramLong.longValue();
    if (this.mSize > 0L)
    {
      this.mNotifier.downloadSuccessful(this);
      return;
    }
    this.mNotifier.downloadFailed(this, Boolean.valueOf(false));
  }
  
  protected void onProgressUpdate(Integer... paramVarArgs) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/GetFileSizeTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */