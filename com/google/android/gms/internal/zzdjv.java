package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

public abstract class zzdjv<T>
{
  private final Context mContext;
  private final Object mLock = new Object();
  private final String mTag;
  private boolean zzkxs = false;
  private T zzkxt;
  
  public zzdjv(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  public final boolean isOperational()
  {
    return zzbjv() != null;
  }
  
  protected abstract T zza(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zzc;
  
  protected abstract void zzbjs()
    throws RemoteException;
  
  public final void zzbju()
  {
    synchronized (this.mLock)
    {
      if (this.zzkxt == null) {
        return;
      }
    }
    try
    {
      zzbjs();
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
  
  protected final T zzbjv()
  {
    Object localObject1;
    synchronized (this.mLock)
    {
      if (this.zzkxt != null)
      {
        localObject1 = this.zzkxt;
        return (T)localObject1;
      }
    }
    try
    {
      this.zzkxt = zza(DynamiteModule.zza(this.mContext, DynamiteModule.zzgxa, "com.google.android.gms.vision.dynamite"), this.mContext);
      if ((!this.zzkxs) && (this.zzkxt == null))
      {
        Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
        this.zzkxs = true;
        localObject1 = this.zzkxt;
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
        if ((this.zzkxs) && (this.zzkxt != null)) {
          Log.w(this.mTag, "Native handle is now available.");
        }
      }
    }
    catch (DynamiteModule.zzc localzzc)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdjv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */