package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import java.util.List;
import javax.annotation.Nullable;

public final class zzhk<T>
  extends zzen
{
  private final IntentFilter[] zzba;
  @Nullable
  private final String zzbb;
  private ListenerHolder<DataApi.DataListener> zzfl;
  private ListenerHolder<MessageApi.MessageListener> zzfm;
  private ListenerHolder<ChannelApi.ChannelListener> zzfp;
  private ListenerHolder<CapabilityApi.CapabilityListener> zzfq;
  
  public final void onConnectedNodes(List<zzfo> paramList) {}
  
  public final void zza(DataHolder paramDataHolder)
  {
    if (this.zzfl != null) {
      this.zzfl.notifyListener(new zzhl(paramDataHolder));
    }
    for (;;)
    {
      return;
      paramDataHolder.close();
    }
  }
  
  public final void zza(zzah paramzzah)
  {
    if (this.zzfq != null) {
      this.zzfq.notifyListener(new zzho(paramzzah));
    }
  }
  
  public final void zza(zzaw paramzzaw)
  {
    if (this.zzfp != null) {
      this.zzfp.notifyListener(new zzhn(paramzzaw));
    }
  }
  
  public final void zza(zzfe paramzzfe)
  {
    if (this.zzfm != null) {
      this.zzfm.notifyListener(new zzhm(paramzzfe));
    }
  }
  
  public final void zza(zzfo paramzzfo) {}
  
  public final void zza(zzi paramzzi) {}
  
  public final void zza(zzl paramzzl) {}
  
  public final void zzb(zzfo paramzzfo) {}
  
  public final IntentFilter[] zze()
  {
    return this.zzba;
  }
  
  @Nullable
  public final String zzf()
  {
    return this.zzbb;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzhk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */