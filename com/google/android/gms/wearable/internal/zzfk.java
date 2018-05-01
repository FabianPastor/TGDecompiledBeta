package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.ArrayList;
import java.util.List;

final class zzfk
  extends zzfc<NodeApi.GetConnectedNodesResult>
{
  public zzfk(zzbaz<NodeApi.GetConnectedNodesResult> paramzzbaz)
  {
    super(paramzzbaz);
  }
  
  public final void zza(zzcy paramzzcy)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(paramzzcy.zzbSO);
    zzR(new zzee(zzev.zzaY(paramzzcy.statusCode), localArrayList));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */