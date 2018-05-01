package com.google.android.gms.internal;

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

public final class zzbco
  implements zzbcw
{
  private final zzbcx zzaCZ;
  
  public zzbco(zzbcx paramzzbcx)
  {
    this.zzaCZ = paramzzbcx;
  }
  
  public final void begin()
  {
    Iterator localIterator = this.zzaCZ.zzaDF.values().iterator();
    while (localIterator.hasNext()) {
      ((Api.zze)localIterator.next()).disconnect();
    }
    this.zzaCZ.zzaCl.zzaDG = Collections.emptySet();
  }
  
  public final void connect()
  {
    this.zzaCZ.zzqh();
  }
  
  public final boolean disconnect()
  {
    return true;
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt) {}
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean) {}
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(T paramT)
  {
    this.zzaCZ.zzaCl.zzaCJ.add(paramT);
    return paramT;
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbco.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */