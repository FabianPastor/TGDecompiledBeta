package com.google.firebase.iid;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

final class zzr
{
  private final Messenger zzbqg;
  private final zzi zzbrg;
  
  zzr(IBinder paramIBinder)
    throws RemoteException
  {
    String str = paramIBinder.getInterfaceDescriptor();
    if ("android.os.IMessenger".equals(str))
    {
      this.zzbqg = new Messenger(paramIBinder);
      this.zzbrg = null;
    }
    for (;;)
    {
      return;
      if (!"com.google.android.gms.iid.IMessengerCompat".equals(str)) {
        break;
      }
      this.zzbrg = new zzi(paramIBinder);
      this.zzbqg = null;
    }
    paramIBinder = String.valueOf(str);
    if (paramIBinder.length() != 0) {}
    for (paramIBinder = "Invalid interface descriptor: ".concat(paramIBinder);; paramIBinder = new String("Invalid interface descriptor: "))
    {
      Log.w("MessengerIpcClient", paramIBinder);
      throw new RemoteException();
    }
  }
  
  final void send(Message paramMessage)
    throws RemoteException
  {
    if (this.zzbqg != null) {
      this.zzbqg.send(paramMessage);
    }
    for (;;)
    {
      return;
      if (this.zzbrg == null) {
        break;
      }
      this.zzbrg.send(paramMessage);
    }
    throw new IllegalStateException("Both messengers are null");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */