package com.google.android.gms.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public abstract class GcmTaskService
  extends Service
{
  public static final String SERVICE_ACTION_EXECUTE_TASK = "com.google.android.gms.gcm.ACTION_TASK_READY";
  public static final String SERVICE_ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
  public static final String SERVICE_PERMISSION = "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE";
  private final Set<String> aeL = new HashSet();
  private int aeM;
  
  private void zzkn(String paramString)
  {
    synchronized (this.aeL)
    {
      this.aeL.remove(paramString);
      if (this.aeL.size() == 0) {
        stopSelf(this.aeM);
      }
      return;
    }
  }
  
  private void zztl(int paramInt)
  {
    synchronized (this.aeL)
    {
      this.aeM = paramInt;
      if (this.aeL.size() == 0) {
        stopSelf(this.aeM);
      }
      return;
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onInitializeTasks() {}
  
  public abstract int onRunTask(TaskParams paramTaskParams);
  
  @CallSuper
  public int onStartCommand(Intent arg1, int paramInt1, int paramInt2)
  {
    if (??? == null)
    {
      zztl(paramInt2);
      return 2;
    }
    for (;;)
    {
      try
      {
        ???.setExtrasClassLoader(PendingCallback.class.getClassLoader());
        String str = ???.getAction();
        if ("com.google.android.gms.gcm.ACTION_TASK_READY".equals(str))
        {
          str = ???.getStringExtra("tag");
          Parcelable localParcelable = ???.getParcelableExtra("callback");
          Bundle localBundle = (Bundle)???.getParcelableExtra("extras");
          if ((localParcelable == null) || (!(localParcelable instanceof PendingCallback)))
          {
            ??? = String.valueOf(getPackageName());
            Log.e("GcmTaskService", String.valueOf(???).length() + 47 + String.valueOf(str).length() + ??? + " " + str + ": Could not process request, invalid callback.");
            return 2;
          }
          synchronized (this.aeL)
          {
            this.aeL.add(str);
            new zza(str, ((PendingCallback)localParcelable).getIBinder(), localBundle).start();
            return 2;
          }
        }
        if (!"com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE".equals(localObject)) {
          break label228;
        }
      }
      finally
      {
        zztl(paramInt2);
      }
      onInitializeTasks();
      continue;
      label228:
      Log.e("GcmTaskService", String.valueOf(localObject).length() + 37 + "Unknown action received " + (String)localObject + ", terminating");
    }
  }
  
  private class zza
    extends Thread
  {
    private final zzb aeN;
    private final Bundle mExtras;
    private final String mTag;
    
    zza(String paramString, IBinder paramIBinder, Bundle paramBundle)
    {
      super();
      this.mTag = paramString;
      this.aeN = zzb.zza.zzgs(paramIBinder);
      this.mExtras = paramBundle;
    }
    
    public void run()
    {
      Process.setThreadPriority(10);
      int i = GcmTaskService.this.onRunTask(new TaskParams(this.mTag, this.mExtras));
      try
      {
        this.aeN.zztm(i);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        String str = String.valueOf(this.mTag);
        if (str.length() != 0) {}
        for (str = "Error reporting result of operation to scheduler for ".concat(str);; str = new String("Error reporting result of operation to scheduler for "))
        {
          Log.e("GcmTaskService", str);
          return;
        }
      }
      finally
      {
        GcmTaskService.zza(GcmTaskService.this, this.mTag);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmTaskService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */