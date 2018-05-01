package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public final class zzaz
  implements zzbh
{
  private final zzbi zzfqv;
  
  public zzaz(zzbi paramzzbi)
  {
    this.zzfqv = paramzzbi;
  }
  
  public final void begin()
  {
    Iterator localIterator = this.zzfqv.zzfsb.values().iterator();
    while (localIterator.hasNext()) {
      ((Api.zze)localIterator.next()).disconnect();
    }
    this.zzfqv.zzfpi.zzfsc = Collections.emptySet();
  }
  
  public final void connect()
  {
    this.zzfqv.zzain();
  }
  
  public final boolean disconnect()
  {
    return true;
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt) {}
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean) {}
  
  public final <A extends Api.zzb, R extends Result, T extends zzm<R, A>> T zzd(T paramT)
  {
    this.zzfqv.zzfpi.zzfqg.add(paramT);
    return paramT;
  }
  
  public final <A extends Api.zzb, T extends zzm<? extends Result, A>> T zze(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzaz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */