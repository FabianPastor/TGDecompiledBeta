package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public final class zzrd<L>
{
  private volatile L mListener;
  private final zza ze;
  private final zzb<L> zf;
  
  zzrd(@NonNull Looper paramLooper, @NonNull L paramL, @NonNull String paramString)
  {
    this.ze = new zza(paramLooper);
    this.mListener = zzac.zzb(paramL, "Listener must not be null");
    this.zf = new zzb(paramL, zzac.zzhz(paramString), null);
  }
  
  public void clear()
  {
    this.mListener = null;
  }
  
  public void zza(zzc<? super L> paramzzc)
  {
    zzac.zzb(paramzzc, "Notifier must not be null");
    paramzzc = this.ze.obtainMessage(1, paramzzc);
    this.ze.sendMessage(paramzzc);
  }
  
  @NonNull
  public zzb<L> zzasr()
  {
    return this.zf;
  }
  
  void zzb(zzc<? super L> paramzzc)
  {
    Object localObject = this.mListener;
    if (localObject == null)
    {
      paramzzc.zzarg();
      return;
    }
    try
    {
      paramzzc.zzt(localObject);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      paramzzc.zzarg();
      throw localRuntimeException;
    }
  }
  
  private final class zza
    extends Handler
  {
    public zza(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      if (paramMessage.what == 1) {}
      for (;;)
      {
        zzac.zzbs(bool);
        zzrd.this.zzb((zzrd.zzc)paramMessage.obj);
        return;
        bool = false;
      }
    }
  }
  
  public static final class zzb<L>
  {
    private final L mListener;
    private final String zh;
    
    private zzb(L paramL, String paramString)
    {
      this.mListener = paramL;
      this.zh = paramString;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if (!(paramObject instanceof zzb)) {
          return false;
        }
        paramObject = (zzb)paramObject;
      } while ((this.mListener == ((zzb)paramObject).mListener) && (this.zh.equals(((zzb)paramObject).zh)));
      return false;
    }
    
    public int hashCode()
    {
      return System.identityHashCode(this.mListener) * 31 + this.zh.hashCode();
    }
  }
  
  public static abstract interface zzc<L>
  {
    public abstract void zzarg();
    
    public abstract void zzt(L paramL);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */