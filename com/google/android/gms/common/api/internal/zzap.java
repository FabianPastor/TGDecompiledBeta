package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api.Client;
import java.util.ArrayList;

final class zzap
  extends zzat
{
  private final ArrayList<Api.Client> zzib;
  
  public zzap(ArrayList<Api.Client> paramArrayList)
  {
    super(paramArrayList, null);
    ArrayList localArrayList;
    this.zzib = localArrayList;
  }
  
  public final void zzaq()
  {
    zzaj.zzd(this.zzhv).zzfq.zzim = zzaj.zzg(this.zzhv);
    ArrayList localArrayList = (ArrayList)this.zzib;
    int i = localArrayList.size();
    int j = 0;
    while (j < i)
    {
      Object localObject = localArrayList.get(j);
      j++;
      ((Api.Client)localObject).getRemoteService(zzaj.zzh(this.zzhv), zzaj.zzd(this.zzhv).zzfq.zzim);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */