package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import com.google.android.gms.common.stats.zza;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class zzaj
  implements ServiceConnection
{
  private ComponentName mComponentName;
  private int mState;
  private IBinder zzfzf;
  private final Set<ServiceConnection> zzgaq;
  private boolean zzgar;
  private final zzah zzgas;
  
  public zzaj(zzai paramzzai, zzah paramzzah)
  {
    this.zzgas = paramzzah;
    this.zzgaq = new HashSet();
    this.mState = 2;
  }
  
  public final IBinder getBinder()
  {
    return this.zzfzf;
  }
  
  public final ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public final int getState()
  {
    return this.mState;
  }
  
  public final boolean isBound()
  {
    return this.zzgar;
  }
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    synchronized (zzai.zza(this.zzgat))
    {
      zzai.zzb(this.zzgat).removeMessages(1, this.zzgas);
      this.zzfzf = paramIBinder;
      this.mComponentName = paramComponentName;
      Iterator localIterator = this.zzgaq.iterator();
      if (localIterator.hasNext()) {
        ((ServiceConnection)localIterator.next()).onServiceConnected(paramComponentName, paramIBinder);
      }
    }
    this.mState = 1;
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    synchronized (zzai.zza(this.zzgat))
    {
      zzai.zzb(this.zzgat).removeMessages(1, this.zzgas);
      this.zzfzf = null;
      this.mComponentName = paramComponentName;
      Iterator localIterator = this.zzgaq.iterator();
      if (localIterator.hasNext()) {
        ((ServiceConnection)localIterator.next()).onServiceDisconnected(paramComponentName);
      }
    }
    this.mState = 2;
  }
  
  public final void zza(ServiceConnection paramServiceConnection, String paramString)
  {
    zzai.zzd(this.zzgat);
    zzai.zzc(this.zzgat);
    this.zzgas.zzall();
    this.zzgaq.add(paramServiceConnection);
  }
  
  public final boolean zza(ServiceConnection paramServiceConnection)
  {
    return this.zzgaq.contains(paramServiceConnection);
  }
  
  public final boolean zzalm()
  {
    return this.zzgaq.isEmpty();
  }
  
  public final void zzb(ServiceConnection paramServiceConnection, String paramString)
  {
    zzai.zzd(this.zzgat);
    zzai.zzc(this.zzgat);
    this.zzgaq.remove(paramServiceConnection);
  }
  
  public final void zzgi(String paramString)
  {
    this.mState = 3;
    this.zzgar = zzai.zzd(this.zzgat).zza(zzai.zzc(this.zzgat), paramString, this.zzgas.zzall(), this, this.zzgas.zzalk());
    if (this.zzgar)
    {
      paramString = zzai.zzb(this.zzgat).obtainMessage(1, this.zzgas);
      zzai.zzb(this.zzgat).sendMessageDelayed(paramString, zzai.zze(this.zzgat));
      return;
    }
    this.mState = 2;
    try
    {
      zzai.zzd(this.zzgat);
      zzai.zzc(this.zzgat).unbindService(this);
      return;
    }
    catch (IllegalArgumentException paramString) {}
  }
  
  public final void zzgj(String paramString)
  {
    zzai.zzb(this.zzgat).removeMessages(1, this.zzgas);
    zzai.zzd(this.zzgat);
    zzai.zzc(this.zzgat).unbindService(this);
    this.zzgar = false;
    this.mState = 2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */