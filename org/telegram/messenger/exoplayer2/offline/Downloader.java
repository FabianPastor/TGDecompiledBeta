package org.telegram.messenger.exoplayer2.offline;

import java.io.IOException;

public abstract interface Downloader
{
  public abstract void download(ProgressListener paramProgressListener)
    throws InterruptedException, IOException;
  
  public abstract float getDownloadPercentage();
  
  public abstract long getDownloadedBytes();
  
  public abstract void init()
    throws InterruptedException, IOException;
  
  public abstract void remove()
    throws InterruptedException;
  
  public static abstract interface ProgressListener
  {
    public abstract void onDownloadProgress(Downloader paramDownloader, float paramFloat, long paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/offline/Downloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */