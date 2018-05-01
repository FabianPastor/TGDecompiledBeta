package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.Api.zze;
import java.util.ArrayList;

final class zzbcj
  extends zzbcn
{
  private final ArrayList<Api.zze> zzaDv;
  
  public zzbcj(ArrayList<Api.zze> paramArrayList)
  {
    super(paramArrayList, null);
    ArrayList localArrayList;
    this.zzaDv = localArrayList;
  }
  
  @WorkerThread
  public final void zzpV()
  {
    zzbcd.zzd(this.zzaDp).zzaCl.zzaDG = zzbcd.zzg(this.zzaDp);
    ArrayList localArrayList = (ArrayList)this.zzaDv;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = localArrayList.get(i);
      i += 1;
      ((Api.zze)localObject).zza(zzbcd.zzh(this.zzaDp), zzbcd.zzd(this.zzaDp).zzaCl.zzaDG);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */