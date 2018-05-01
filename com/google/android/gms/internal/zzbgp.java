package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzbgp
  extends zza
{
  public static final Parcelable.Creator<zzbgp> CREATOR = new zzbgs();
  final String className;
  private int versionCode;
  private ArrayList<zzbgq> zzaIU;
  
  zzbgp(int paramInt, String paramString, ArrayList<zzbgq> paramArrayList)
  {
    this.versionCode = paramInt;
    this.className = paramString;
    this.zzaIU = paramArrayList;
  }
  
  zzbgp(String paramString, Map<String, zzbgj<?, ?>> paramMap)
  {
    this.versionCode = 1;
    this.className = paramString;
    if (paramMap == null) {
      paramString = null;
    }
    for (;;)
    {
      this.zzaIU = paramString;
      return;
      paramString = new ArrayList();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramString.add(new zzbgq(str, (zzbgj)paramMap.get(str)));
      }
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.versionCode);
    zzd.zza(paramParcel, 2, this.className, false);
    zzd.zzc(paramParcel, 3, this.zzaIU, false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  final HashMap<String, zzbgj<?, ?>> zzrS()
  {
    HashMap localHashMap = new HashMap();
    int j = this.zzaIU.size();
    int i = 0;
    while (i < j)
    {
      zzbgq localzzbgq = (zzbgq)this.zzaIU.get(i);
      localHashMap.put(localzzbgq.key, localzzbgq.zzaIV);
      i += 1;
    }
    return localHashMap;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */