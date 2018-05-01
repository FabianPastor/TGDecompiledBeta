package com.google.android.gms.vision.internal.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zztl;
import com.google.android.gms.internal.zztl.zza;

public abstract class zza<T>
{
  private boolean aOC = false;
  private T aOD;
  private final Context mContext;
  private final String mTag;
  private final Object zzako = new Object();
  
  public zza(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  public boolean isOperational()
  {
    return zzcls() != null;
  }
  
  protected abstract T zzb(zztl paramzztl, Context paramContext)
    throws RemoteException, zztl.zza;
  
  protected abstract void zzclp()
    throws RemoteException;
  
  public void zzclr()
  {
    synchronized (this.zzako)
    {
      if (this.aOD == null) {
        return;
      }
    }
    try
    {
      zzclp();
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
  
  protected T zzcls()
  {
    Object localObject1;
    synchronized (this.zzako)
    {
      if (this.aOD != null)
      {
        localObject1 = this.aOD;
        return (T)localObject1;
      }
    }
    try
    {
      this.aOD = zzb(zztl.zza(this.mContext, zztl.Qp, "com.google.android.gms.vision.dynamite"), this.mContext);
      if ((!this.aOC) && (this.aOD == null))
      {
        Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
        this.aOC = true;
        localObject1 = this.aOD;
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
        if ((this.aOC) && (this.aOD != null)) {
          Log.w(this.mTag, "Native handle is now available.");
        }
      }
    }
    catch (zztl.zza localzza)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/internal/client/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */