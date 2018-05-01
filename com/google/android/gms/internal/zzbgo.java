package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzbgo
  extends zza
{
  public static final Parcelable.Creator<zzbgo> CREATOR = new zzbgr();
  private final HashMap<String, Map<String, zzbgj<?, ?>>> zzaIR;
  private final ArrayList<zzbgp> zzaIS;
  private final String zzaIT;
  private int zzaku;
  
  zzbgo(int paramInt, ArrayList<zzbgp> paramArrayList, String paramString)
  {
    this.zzaku = paramInt;
    this.zzaIS = null;
    HashMap localHashMap = new HashMap();
    int i = paramArrayList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      zzbgp localzzbgp = (zzbgp)paramArrayList.get(paramInt);
      localHashMap.put(localzzbgp.className, localzzbgp.zzrS());
      paramInt += 1;
    }
    this.zzaIR = localHashMap;
    this.zzaIT = ((String)zzbo.zzu(paramString));
    zzrQ();
  }
  
  private final void zzrQ()
  {
    Iterator localIterator1 = this.zzaIR.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localObject = (Map)this.zzaIR.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext()) {
        ((zzbgj)((Map)localObject).get((String)localIterator2.next())).zza(this);
      }
    }
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator1 = this.zzaIR.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localStringBuilder.append((String)localObject).append(":\n");
      localObject = (Map)this.zzaIR.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        localStringBuilder.append("  ").append(str).append(": ");
        localStringBuilder.append(((Map)localObject).get(str));
      }
    }
    return localStringBuilder.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.zzaIR.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new zzbgp(str, (Map)this.zzaIR.get(str)));
    }
    zzd.zzc(paramParcel, 2, localArrayList, false);
    zzd.zza(paramParcel, 3, this.zzaIT, false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final Map<String, zzbgj<?, ?>> zzcJ(String paramString)
  {
    return (Map)this.zzaIR.get(paramString);
  }
  
  public final String zzrR()
  {
    return this.zzaIT;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */