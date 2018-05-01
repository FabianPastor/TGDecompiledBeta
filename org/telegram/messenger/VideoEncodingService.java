package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;

public class VideoEncodingService
  extends Service
  implements NotificationCenter.NotificationCenterDelegate
{
  private NotificationCompat.Builder builder;
  private int currentAccount;
  private int currentProgress;
  private String path;
  
  public VideoEncodingService()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopEncodingService);
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    boolean bool = true;
    Object localObject;
    if (paramInt1 == NotificationCenter.FileUploadProgressChanged)
    {
      localObject = (String)paramVarArgs[0];
      if ((paramInt2 == this.currentAccount) && (this.path != null) && (this.path.equals(localObject)))
      {
        localObject = (Float)paramVarArgs[1];
        paramVarArgs = (Boolean)paramVarArgs[2];
        this.currentProgress = ((int)(((Float)localObject).floatValue() * 100.0F));
        paramVarArgs = this.builder;
        paramInt1 = this.currentProgress;
        if (this.currentProgress != 0) {
          break label118;
        }
        paramVarArgs.setProgress(100, paramInt1, bool);
      }
    }
    for (;;)
    {
      try
      {
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return;
        label118:
        bool = false;
      }
      catch (Throwable paramVarArgs)
      {
        FileLog.e(paramVarArgs);
        continue;
      }
      if (paramInt1 == NotificationCenter.stopEncodingService)
      {
        localObject = (String)paramVarArgs[0];
        if ((((Integer)paramVarArgs[1]).intValue() == this.currentAccount) && ((localObject == null) || (((String)localObject).equals(this.path)))) {
          stopSelf();
        }
      }
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onDestroy()
  {
    stopForeground(true);
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("destroy video service");
    }
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    this.path = paramIntent.getStringExtra("path");
    paramInt1 = this.currentAccount;
    this.currentAccount = paramIntent.getIntExtra("currentAccount", UserConfig.selectedAccount);
    if (paramInt1 != this.currentAccount)
    {
      NotificationCenter.getInstance(paramInt1).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileUploadProgressChanged);
    }
    boolean bool2 = paramIntent.getBooleanExtra("gif", false);
    if (this.path == null)
    {
      stopSelf();
      return 2;
    }
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("start video service");
    }
    if (this.builder == null)
    {
      this.builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
      this.builder.setSmallIcon(17301640);
      this.builder.setWhen(System.currentTimeMillis());
      this.builder.setChannelId("Other3");
      this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
      if (!bool2) {
        break label266;
      }
      this.builder.setTicker(LocaleController.getString("SendingGif", NUM));
      this.builder.setContentText(LocaleController.getString("SendingGif", NUM));
    }
    for (;;)
    {
      this.currentProgress = 0;
      paramIntent = this.builder;
      paramInt1 = this.currentProgress;
      if (this.currentProgress == 0) {
        bool1 = true;
      }
      paramIntent.setProgress(100, paramInt1, bool1);
      startForeground(4, this.builder.build());
      NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
      break;
      label266:
      this.builder.setTicker(LocaleController.getString("SendingVideo", NUM));
      this.builder.setContentText(LocaleController.getString("SendingVideo", NUM));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/VideoEncodingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */