package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Message;
import com.google.android.gms.common.internal.Preconditions;

public final class ListenerHolder<L>
{
  private final zza zzlh;
  private volatile L zzli;
  
  public final void clear()
  {
    this.zzli = null;
  }
  
  public final void notifyListener(Notifier<? super L> paramNotifier)
  {
    Preconditions.checkNotNull(paramNotifier, "Notifier must not be null");
    paramNotifier = this.zzlh.obtainMessage(1, paramNotifier);
    this.zzlh.sendMessage(paramNotifier);
  }
  
  final void notifyListenerInternal(Notifier<? super L> paramNotifier)
  {
    Object localObject = this.zzli;
    if (localObject == null) {
      paramNotifier.onNotifyListenerFailed();
    }
    for (;;)
    {
      return;
      try
      {
        paramNotifier.notifyListener(localObject);
      }
      catch (RuntimeException localRuntimeException)
      {
        paramNotifier.onNotifyListenerFailed();
        throw localRuntimeException;
      }
    }
  }
  
  public static final class ListenerKey<L>
  {
    private final L zzli;
    private final String zzll;
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if (!(paramObject instanceof ListenerKey))
        {
          bool = false;
        }
        else
        {
          paramObject = (ListenerKey)paramObject;
          if ((this.zzli != ((ListenerKey)paramObject).zzli) || (!this.zzll.equals(((ListenerKey)paramObject).zzll))) {
            bool = false;
          }
        }
      }
    }
    
    public final int hashCode()
    {
      return System.identityHashCode(this.zzli) * 31 + this.zzll.hashCode();
    }
  }
  
  public static abstract interface Notifier<L>
  {
    public abstract void notifyListener(L paramL);
    
    public abstract void onNotifyListenerFailed();
  }
  
  private final class zza
    extends Handler
  {
    public final void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      if (paramMessage.what == 1) {}
      for (;;)
      {
        Preconditions.checkArgument(bool);
        this.zzlk.notifyListenerInternal((ListenerHolder.Notifier)paramMessage.obj);
        return;
        bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/ListenerHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */