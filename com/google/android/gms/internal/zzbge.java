package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public final class zzbge
  extends zza
  implements zzbgk<String, Integer>
{
  public static final Parcelable.Creator<zzbge> CREATOR = new zzbgg();
  private final HashMap<String, Integer> zzaIC;
  private final SparseArray<String> zzaID;
  private final ArrayList<zzbgf> zzaIE;
  private int zzaku;
  
  public zzbge()
  {
    this.zzaku = 1;
    this.zzaIC = new HashMap();
    this.zzaID = new SparseArray();
    this.zzaIE = null;
  }
  
  zzbge(int paramInt, ArrayList<zzbgf> paramArrayList)
  {
    this.zzaku = paramInt;
    this.zzaIC = new HashMap();
    this.zzaID = new SparseArray();
    this.zzaIE = null;
    zzd(paramArrayList);
  }
  
  private final void zzd(ArrayList<zzbgf> paramArrayList)
  {
    paramArrayList = (ArrayList)paramArrayList;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = paramArrayList.get(i);
      i += 1;
      localObject = (zzbgf)localObject;
      zzi(((zzbgf)localObject).zzaIF, ((zzbgf)localObject).zzaIG);
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.zzaIC.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new zzbgf(str, ((Integer)this.zzaIC.get(str)).intValue()));
    }
    zzd.zzc(paramParcel, 2, localArrayList, false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final zzbge zzi(String paramString, int paramInt)
  {
    this.zzaIC.put(paramString, Integer.valueOf(paramInt));
    this.zzaID.put(paramInt, paramString);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */