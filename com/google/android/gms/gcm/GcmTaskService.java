package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.common.util.zzw;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public abstract class GcmTaskService
  extends Service
{
  public static final String SERVICE_ACTION_EXECUTE_TASK = "com.google.android.gms.gcm.ACTION_TASK_READY";
  public static final String SERVICE_ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
  public static final String SERVICE_PERMISSION = "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE";
  private ComponentName componentName;
  private final Object lock = new Object();
  private final Set<String> zzbfE = new HashSet();
  private int zzbfF;
  private Messenger zzbfG;
  private ExecutorService zzqF;
  
  private final void zza(zzb paramzzb)
  {
    try
    {
      this.zzqF.execute(paramzzb);
      return;
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      Log.e("GcmTaskService", "Executor is shutdown. onDestroy was called but main looper had an unprocessed start task message. The task will be retried with backoff delay.", localRejectedExecutionException);
      zzb.zza(paramzzb, 1);
    }
  }
  
  private final void zzbf(int paramInt)
  {
    synchronized (this.lock)
    {
      this.zzbfF = paramInt;
      if (this.zzbfE.isEmpty()) {
        stopSelf(this.zzbfF);
      }
      return;
    }
  }
  
  private final void zzdp(String paramString)
  {
    synchronized (this.lock)
    {
      this.zzbfE.remove(paramString);
      if (this.zzbfE.isEmpty()) {
        stopSelf(this.zzbfF);
      }
      return;
    }
  }
  
  @CallSuper
  public IBinder onBind(Intent paramIntent)
  {
    if ((paramIntent == null) || (!zzq.zzse()) || (!"com.google.android.gms.gcm.ACTION_TASK_READY".equals(paramIntent.getAction()))) {
      return null;
    }
    return this.zzbfG.getBinder();
  }
  
  @CallSuper
  public void onCreate()
  {
    super.onCreate();
    this.zzqF = Executors.newFixedThreadPool(2, new zzb(this));
    this.zzbfG = new Messenger(new zza(Looper.getMainLooper()));
    this.componentName = new ComponentName(this, getClass());
  }
  
  @CallSuper
  public void onDestroy()
  {
    super.onDestroy();
    List localList = this.zzqF.shutdownNow();
    if (!localList.isEmpty())
    {
      int i = localList.size();
      Log.e("GcmTaskService", 79 + "Shutting down, but not all tasks are finished executing. Remaining: " + i);
    }
  }
  
  public void onInitializeTasks() {}
  
  public abstract int onRunTask(TaskParams paramTaskParams);
  
  @CallSuper
  public int onStartCommand(Intent arg1, int paramInt1, int paramInt2)
  {
    if (??? == null)
    {
      zzbf(paramInt2);
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
          Object localObject2 = ???.getParcelableExtra("callback");
          Bundle localBundle = ???.getBundleExtra("extras");
          ArrayList localArrayList = ???.getParcelableArrayListExtra("triggered_uris");
          if (!(localObject2 instanceof PendingCallback))
          {
            ??? = String.valueOf(getPackageName());
            Log.e("GcmTaskService", String.valueOf(???).length() + 47 + String.valueOf(str).length() + ??? + " " + str + ": Could not process request, invalid callback.");
            return 2;
          }
          synchronized (this.lock)
          {
            if (!this.zzbfE.add(str))
            {
              localObject2 = String.valueOf(getPackageName());
              Log.w("GcmTaskService", String.valueOf(localObject2).length() + 44 + String.valueOf(str).length() + (String)localObject2 + " " + str + ": Task already running, won't start another");
              return 2;
            }
            zza(new zzb(str, ((PendingCallback)localObject2).zzaHj, localBundle, localArrayList));
            return 2;
          }
        }
        if (!"com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE".equals(localObject1)) {
          break label308;
        }
      }
      finally
      {
        zzbf(paramInt2);
      }
      onInitializeTasks();
      continue;
      label308:
      Log.e("GcmTaskService", String.valueOf(localObject1).length() + 37 + "Unknown action received " + (String)localObject1 + ", terminating");
    }
  }
  
  @TargetApi(21)
  final class zza
    extends Handler
  {
    zza(Looper paramLooper)
    {
      super();
    }
    
    public final void handleMessage(Message paramMessage)
    {
      if (!zzw.zzb(GcmTaskService.this, paramMessage.sendingUid, "com.google.android.gms")) {
        Log.e("GcmTaskService", "unable to verify presence of Google Play Services");
      }
      do
      {
        Bundle localBundle;
        do
        {
          do
          {
            return;
            switch (paramMessage.what)
            {
            case 3: 
            default: 
              paramMessage = String.valueOf(paramMessage);
              Log.e("GcmTaskService", String.valueOf(paramMessage).length() + 31 + "Unrecognized message received: " + paramMessage);
              return;
            case 1: 
              localBundle = paramMessage.getData();
            }
          } while (localBundle == null);
          paramMessage = paramMessage.replyTo;
        } while (paramMessage == null);
        String str = localBundle.getString("tag");
        ArrayList localArrayList = localBundle.getParcelableArrayList("triggered_uris");
        GcmTaskService.zza(GcmTaskService.this, new GcmTaskService.zzb(GcmTaskService.this, str, paramMessage, localBundle.getBundle("extras"), localArrayList));
        return;
      } while (!Log.isLoggable("GcmTaskService", 3));
      paramMessage = String.valueOf(paramMessage);
      Log.d("GcmTaskService", String.valueOf(paramMessage).length() + 45 + "ignoring unimplemented stop message for now: " + paramMessage);
      return;
      GcmTaskService.this.onInitializeTasks();
    }
  }
  
  final class zzb
    implements Runnable
  {
    private final Bundle mExtras;
    @Nullable
    private final Messenger mMessenger;
    private final String mTag;
    private final List<Uri> zzbfJ;
    @Nullable
    private final zzd zzbfK;
    
    zzb(IBinder paramIBinder, Bundle paramBundle, List<Uri> paramList)
    {
      this.mTag = paramIBinder;
      if (paramBundle == null) {
        this$1 = null;
      }
      for (;;)
      {
        this.zzbfK = GcmTaskService.this;
        this.mExtras = paramList;
        List localList;
        this.zzbfJ = localList;
        this.mMessenger = null;
        return;
        this$1 = paramBundle.queryLocalInterface("com.google.android.gms.gcm.INetworkTaskCallback");
        if ((GcmTaskService.this instanceof zzd)) {
          this$1 = (zzd)GcmTaskService.this;
        } else {
          this$1 = new zze(paramBundle);
        }
      }
    }
    
    zzb(Messenger paramMessenger, Bundle paramBundle, List<Uri> paramList)
    {
      this.mTag = paramMessenger;
      this.mMessenger = paramBundle;
      this.mExtras = paramList;
      List localList;
      this.zzbfJ = localList;
      this.zzbfK = null;
    }
    
    private final void zzbg(int paramInt)
    {
      localObject3 = GcmTaskService.zza(GcmTaskService.this);
      for (;;)
      {
        try
        {
          if (zzvC())
          {
            Messenger localMessenger = this.mMessenger;
            Message localMessage = Message.obtain();
            localMessage.what = 3;
            localMessage.arg1 = paramInt;
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("component", GcmTaskService.zzb(GcmTaskService.this));
            localBundle.putString("tag", this.mTag);
            localMessage.setData(localBundle);
            localMessenger.send(localMessage);
          }
        }
        catch (RemoteException localRemoteException)
        {
          String str1 = String.valueOf(this.mTag);
          if (str1.length() == 0) {
            continue;
          }
          str1 = "Error reporting result of operation to scheduler for ".concat(str1);
          Log.e("GcmTaskService", str1);
          if (zzvC()) {
            continue;
          }
          GcmTaskService.zza(GcmTaskService.this, this.mTag);
          continue;
          String str2 = new String("Error reporting result of operation to scheduler for ");
          continue;
        }
        finally
        {
          if (zzvC()) {
            continue;
          }
          GcmTaskService.zza(GcmTaskService.this, this.mTag);
        }
        try
        {
          if (!zzvC()) {
            GcmTaskService.zza(GcmTaskService.this, this.mTag);
          }
          return;
        }
        finally {}
        this.zzbfK.zzbh(paramInt);
      }
    }
    
    private final boolean zzvC()
    {
      return this.mMessenger != null;
    }
    
    public final void run()
    {
      TaskParams localTaskParams = new TaskParams(this.mTag, this.mExtras, this.zzbfJ);
      zzbg(GcmTaskService.this.onRunTask(localTaskParams));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmTaskService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */