package com.google.android.gms.vision.internal.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.internal.zzsu.zza;

public abstract class zza<T>
{
  private boolean aLr = false;
  private T aLs;
  private final Context mContext;
  private final String mTag;
  private final Object zzakd = new Object();
  
  public zza(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  public boolean isOperational()
  {
    return zzclt() != null;
  }
  
  protected abstract T zzb(zzsu paramzzsu, Context paramContext)
    throws RemoteException, zzsu.zza;
  
  protected abstract void zzclq()
    throws RemoteException;
  
  public void zzcls()
  {
    synchronized (this.zzakd)
    {
      if (this.aLs == null) {
        return;
      }
    }
    try
    {
      zzclq();
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e(this.mTag, "Could not finalize native handle", localRemoteException);
      }
    }
  }
  
  protected T zzclt()
  {
    Object localObject1;
    synchronized (this.zzakd)
    {
      if (this.aLs != null)
      {
        localObject1 = this.aLs;
        return (T)localObject1;
      }
    }
    try
    {
      this.aLs = zzb(zzsu.zza(this.mContext, zzsu.OB, "com.google.android.gms.vision.dynamite"), this.mContext);
      if ((!this.aLr) && (this.aLs == null))
      {
        Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
        this.aLr = true;
        localObject1 = this.aLs;
        return (T)localObject1;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e(this.mTag, "Error creating remote native handle", localRemoteException);
        continue;
        if ((this.aLr) && (this.aLs != null)) {
          Log.w(this.mTag, "Native handle is now available.");
        }
      }
    }
    catch (zzsu.zza localzza)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/internal/client/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */