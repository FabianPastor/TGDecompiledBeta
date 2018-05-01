package org.telegram.messenger.support.customtabsclient.shared;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class KeepAliveService
  extends Service
{
  private static final Binder sBinder = new Binder();
  
  public IBinder onBind(Intent paramIntent)
  {
    return sBinder;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabsclient/shared/KeepAliveService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */