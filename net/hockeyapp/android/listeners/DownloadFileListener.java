package net.hockeyapp.android.listeners;

import net.hockeyapp.android.tasks.DownloadFileTask;

public abstract class DownloadFileListener
{
  public void downloadFailed(DownloadFileTask paramDownloadFileTask, Boolean paramBoolean) {}
  
  public void downloadSuccessful(DownloadFileTask paramDownloadFileTask) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/listeners/DownloadFileListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */