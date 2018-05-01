package com.google.android.gms.internal.vision;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.LoadingException;
import javax.annotation.concurrent.GuardedBy;

public abstract class zzj<T>
{
  private final Context mContext;
  private final Object mLock = new Object();
  private final String mTag;
  private boolean zzci = false;
  @GuardedBy("mLock")
  private T zzcj;
  
  public zzj(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  public final boolean isOperational()
  {
    if (zzh() != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected abstract T zza(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.LoadingException;
  
  protected abstract void zze()
    throws RemoteException;
  
  public final void zzg()
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.zzcj == null) {
          return;
        }
      }
      try
      {
        zze();
        continue;
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
  }
  
  protected final T zzh()
  {
    for (;;)
    {
      Object localObject2;
      synchronized (this.mLock)
      {
        if (this.zzcj != null)
        {
          localObject2 = this.zzcj;
          return (T)localObject2;
        }
      }
      try
      {
        this.zzcj = zza(DynamiteModule.load(this.mContext, DynamiteModule.PREFER_HIGHEST_OR_REMOTE_VERSION, "com.google.android.gms.vision.dynamite"), this.mContext);
        if ((!this.zzci) && (this.zzcj == null))
        {
          Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
          this.zzci = true;
          localObject2 = this.zzcj;
          continue;
          localObject3 = finally;
          throw ((Throwable)localObject3);
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e(this.mTag, "Error creating remote native handle", localRemoteException);
          continue;
          if ((this.zzci) && (this.zzcj != null)) {
            Log.w(this.mTag, "Native handle is now available.");
          }
        }
      }
      catch (DynamiteModule.LoadingException localLoadingException)
      {
        for (;;) {}
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/vision/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */