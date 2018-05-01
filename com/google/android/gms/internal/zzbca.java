package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbx;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzbca
  implements zzbcw
{
  private final zzbcx zzaCZ;
  private boolean zzaDa = false;
  
  public zzbca(zzbcx paramzzbcx)
  {
    this.zzaCZ = paramzzbcx;
  }
  
  public final void begin() {}
  
  public final void connect()
  {
    if (this.zzaDa)
    {
      this.zzaDa = false;
      this.zzaCZ.zza(new zzbcc(this, this));
    }
  }
  
  public final boolean disconnect()
  {
    if (this.zzaDa) {
      return false;
    }
    if (this.zzaCZ.zzaCl.zzqf())
    {
      this.zzaDa = true;
      Iterator localIterator = this.zzaCZ.zzaCl.zzaDK.iterator();
      while (localIterator.hasNext()) {
        ((zzbes)localIterator.next()).zzqK();
      }
      return false;
    }
    this.zzaCZ.zzg(null);
    return true;
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzaCZ.zzg(null);
    this.zzaCZ.zzaDY.zze(paramInt, this.zzaDa);
  }
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean) {}
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(T paramT)
  {
    return zze(paramT);
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(T paramT)
  {
    try
    {
      this.zzaCZ.zzaCl.zzaDL.zzb(paramT);
      Object localObject1 = this.zzaCZ.zzaCl;
      Object localObject2 = paramT.zzpd();
      localObject2 = (Api.zze)((zzbcp)localObject1).zzaDF.get(localObject2);
      zzbo.zzb(localObject2, "Appropriate Api was not requested.");
      if ((!((Api.zze)localObject2).isConnected()) && (this.zzaCZ.zzaDU.containsKey(paramT.zzpd())))
      {
        paramT.zzr(new Status(17));
        return paramT;
      }
      localObject1 = localObject2;
      if ((localObject2 instanceof zzbx))
      {
        localObject1 = (zzbx)localObject2;
        localObject1 = null;
      }
      paramT.zzb((Api.zzb)localObject1);
      return paramT;
    }
    catch (DeadObjectException localDeadObjectException)
    {
      this.zzaCZ.zza(new zzbcb(this, this));
    }
    return paramT;
  }
  
  final void zzpU()
  {
    if (this.zzaDa)
    {
      this.zzaDa = false;
      this.zzaCZ.zzaCl.zzaDL.release();
      disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbca.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */