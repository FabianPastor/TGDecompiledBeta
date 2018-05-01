package com.google.firebase.iid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

final class zzm
  implements ServiceConnection
{
  @GuardedBy("this")
  int state = 0;
  final Messenger zzbqz = new Messenger(new Handler(Looper.getMainLooper(), new zzn(this)));
  zzr zzbra;
  @GuardedBy("this")
  final Queue<zzt<?>> zzbrb = new ArrayDeque();
  @GuardedBy("this")
  final SparseArray<zzt<?>> zzbrc = new SparseArray();
  
  private zzm(zzk paramzzk) {}
  
  private final void zzsq()
  {
    zzk.zzb(this.zzbrd).execute(new zzp(this));
  }
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    for (;;)
    {
      try
      {
        if (Log.isLoggable("MessengerIpcClient", 2)) {
          Log.v("MessengerIpcClient", "Service connected");
        }
        if (paramIBinder != null) {
          continue;
        }
        zzb(0, "Null service connection");
      }
      finally
      {
        try
        {
          paramComponentName = new com/google/firebase/iid/zzr;
          paramComponentName.<init>(paramIBinder);
          this.zzbra = paramComponentName;
          this.state = 2;
          zzsq();
        }
        catch (RemoteException paramComponentName)
        {
          zzb(0, paramComponentName.getMessage());
        }
        paramComponentName = finally;
      }
      return;
    }
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    try
    {
      if (Log.isLoggable("MessengerIpcClient", 2)) {
        Log.v("MessengerIpcClient", "Service disconnected");
      }
      zzb(2, "Service disconnected");
      return;
    }
    finally {}
  }
  
  final boolean zza(Message paramMessage)
  {
    int i = paramMessage.arg1;
    if (Log.isLoggable("MessengerIpcClient", 3)) {
      Log.d("MessengerIpcClient", 41 + "Received response to request: " + i);
    }
    for (;;)
    {
      try
      {
        zzt localzzt = (zzt)this.zzbrc.get(i);
        if (localzzt == null)
        {
          paramMessage = new java/lang/StringBuilder;
          paramMessage.<init>(50);
          Log.w("MessengerIpcClient", "Received response for unknown request: " + i);
          return true;
        }
        this.zzbrc.remove(i);
        zzsr();
        paramMessage = paramMessage.getData();
        if (paramMessage.getBoolean("unsupported", false)) {
          localzzt.zza(new zzu(4, "Not supported by GmsCore"));
        } else {
          localzzt.zzh(paramMessage);
        }
      }
      finally {}
    }
  }
  
  final void zzae(int paramInt)
  {
    try
    {
      zzt localzzt = (zzt)this.zzbrc.get(paramInt);
      if (localzzt != null)
      {
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>(31);
        Log.w("MessengerIpcClient", "Timing out request: " + paramInt);
        this.zzbrc.remove(paramInt);
        localObject2 = new com/google/firebase/iid/zzu;
        ((zzu)localObject2).<init>(3, "Timed out waiting for response");
        localzzt.zza((zzu)localObject2);
        zzsr();
      }
      return;
    }
    finally {}
  }
  
  final void zzb(int paramInt, String paramString)
  {
    for (;;)
    {
      try
      {
        if (Log.isLoggable("MessengerIpcClient", 3))
        {
          localObject = String.valueOf(paramString);
          if (((String)localObject).length() != 0)
          {
            localObject = "Disconnected: ".concat((String)localObject);
            Log.d("MessengerIpcClient", (String)localObject);
          }
        }
        else
        {
          switch (this.state)
          {
          default: 
            localObject = new java/lang/IllegalStateException;
            paramInt = this.state;
            paramString = new java/lang/StringBuilder;
            paramString.<init>(26);
            ((IllegalStateException)localObject).<init>("Unknown state: " + paramInt);
            throw ((Throwable)localObject);
          }
        }
      }
      finally {}
      localObject = new String("Disconnected: ");
    }
    paramString = new java/lang/IllegalStateException;
    paramString.<init>();
    throw paramString;
    if (Log.isLoggable("MessengerIpcClient", 2)) {
      Log.v("MessengerIpcClient", "Unbinding service");
    }
    this.state = 4;
    ConnectionTracker.getInstance().unbindService(zzk.zza(this.zzbrd), this);
    Object localObject = new com/google/firebase/iid/zzu;
    ((zzu)localObject).<init>(paramInt, paramString);
    paramString = this.zzbrb.iterator();
    while (paramString.hasNext()) {
      ((zzt)paramString.next()).zza((zzu)localObject);
    }
    this.zzbrb.clear();
    for (paramInt = 0; paramInt < this.zzbrc.size(); paramInt++) {
      ((zzt)this.zzbrc.valueAt(paramInt)).zza((zzu)localObject);
    }
    this.zzbrc.clear();
    for (;;)
    {
      return;
      this.state = 4;
    }
  }
  
  final boolean zzb(zzt paramzzt)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    Object localObject;
    try
    {
      switch (this.state)
      {
      default: 
        localObject = new java/lang/IllegalStateException;
        int i = this.state;
        paramzzt = new java/lang/StringBuilder;
        paramzzt.<init>(26);
        ((IllegalStateException)localObject).<init>("Unknown state: " + i);
        throw ((Throwable)localObject);
      }
    }
    finally {}
    this.zzbrb.add(paramzzt);
    if (this.state == 0) {
      bool1 = true;
    }
    Preconditions.checkState(bool1);
    if (Log.isLoggable("MessengerIpcClient", 2)) {
      Log.v("MessengerIpcClient", "Starting bind to GmsCore");
    }
    this.state = 1;
    paramzzt = new android/content/Intent;
    paramzzt.<init>("com.google.android.c2dm.intent.REGISTER");
    paramzzt.setPackage("com.google.android.gms");
    if (!ConnectionTracker.getInstance().bindService(zzk.zza(this.zzbrd), paramzzt, this, 1))
    {
      zzb(0, "Unable to bind to service");
      bool1 = bool2;
    }
    for (;;)
    {
      return bool1;
      paramzzt = zzk.zzb(this.zzbrd);
      localObject = new com/google/firebase/iid/zzo;
      ((zzo)localObject).<init>(this);
      paramzzt.schedule((Runnable)localObject, 30L, TimeUnit.SECONDS);
      bool1 = bool2;
      continue;
      this.zzbrb.add(paramzzt);
      bool1 = bool2;
      continue;
      this.zzbrb.add(paramzzt);
      zzsq();
      bool1 = bool2;
      continue;
      bool1 = false;
    }
  }
  
  final void zzsr()
  {
    try
    {
      if ((this.state == 2) && (this.zzbrb.isEmpty()) && (this.zzbrc.size() == 0))
      {
        if (Log.isLoggable("MessengerIpcClient", 2)) {
          Log.v("MessengerIpcClient", "Finished handling requests, unbinding");
        }
        this.state = 3;
        ConnectionTracker.getInstance().unbindService(zzk.zza(this.zzbrd), this);
      }
      return;
    }
    finally {}
  }
  
  final void zzss()
  {
    try
    {
      if (this.state == 1) {
        zzb(1, "Timed out while binding");
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */