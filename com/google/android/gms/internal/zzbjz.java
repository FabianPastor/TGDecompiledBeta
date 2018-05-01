package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public abstract class zzbjz<T>
{
  private final Context mContext;
  private final String mTag;
  private boolean zzbPm = false;
  private T zzbPn;
  private final Object zzrJ = new Object();
  
  public zzbjz(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  public boolean isOperational()
  {
    return zzTU() != null;
  }
  
  protected abstract void zzTR()
    throws RemoteException;
  
  public void zzTT()
  {
    synchronized (this.zzrJ)
    {
      if (this.zzbPn == null) {
        return;
      }
    }
    try
    {
      zzTR();
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
  
  protected T zzTU()
  {
    Object localObject1;
    synchronized (this.zzrJ)
    {
      if (this.zzbPn != null)
      {
        localObject1 = this.zzbPn;
        return (T)localObject1;
      }
    }
    try
    {
      this.zzbPn = zzb(DynamiteModule.zza(this.mContext, DynamiteModule.zzaRX, "com.google.android.gms.vision.dynamite"), this.mContext);
      if ((!this.zzbPm) && (this.zzbPn == null))
      {
        Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
        this.zzbPm = true;
        localObject1 = this.zzbPn;
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
        if ((this.zzbPm) && (this.zzbPn != null)) {
          Log.w(this.mTag, "Native handle is now available.");
        }
      }
    }
    catch (DynamiteModule.zza localzza)
    {
      for (;;) {}
    }
  }
  
  protected abstract T zzb(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zza;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */