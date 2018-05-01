package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public final class zzabh<L>
{
  private volatile L mListener;
  private final zza zzaCX;
  private final zzb<L> zzaCY;
  
  zzabh(@NonNull Looper paramLooper, @NonNull L paramL, @NonNull String paramString)
  {
    this.zzaCX = new zza(paramLooper);
    this.mListener = zzac.zzb(paramL, "Listener must not be null");
    this.zzaCY = new zzb(paramL, zzac.zzdr(paramString));
  }
  
  public void clear()
  {
    this.mListener = null;
  }
  
  public void zza(zzc<? super L> paramzzc)
  {
    zzac.zzb(paramzzc, "Notifier must not be null");
    paramzzc = this.zzaCX.obtainMessage(1, paramzzc);
    this.zzaCX.sendMessage(paramzzc);
  }
  
  void zzb(zzc<? super L> paramzzc)
  {
    Object localObject = this.mListener;
    if (localObject == null)
    {
      paramzzc.zzwc();
      return;
    }
    try
    {
      paramzzc.zzs(localObject);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      paramzzc.zzwc();
      throw localRuntimeException;
    }
  }
  
  public boolean zztJ()
  {
    return this.mListener != null;
  }
  
  @NonNull
  public zzb<L> zzwW()
  {
    return this.zzaCY;
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
        zzac.zzaw(bool);
        zzabh.this.zzb((zzabh.zzc)paramMessage.obj);
        return;
        bool = false;
      }
    }
  }
  
  public static final class zzb<L>
  {
    private final L mListener;
    private final String zzaDa;
    
    zzb(L paramL, String paramString)
    {
      this.mListener = paramL;
      this.zzaDa = paramString;
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
      } while ((this.mListener == ((zzb)paramObject).mListener) && (this.zzaDa.equals(((zzb)paramObject).zzaDa)));
      return false;
    }
    
    public int hashCode()
    {
      return System.identityHashCode(this.mListener) * 31 + this.zzaDa.hashCode();
    }
  }
  
  public static abstract interface zzc<L>
  {
    public abstract void zzs(L paramL);
    
    public abstract void zzwc();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */