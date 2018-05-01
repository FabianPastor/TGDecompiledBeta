package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzacw
  extends zza
{
  public static final Parcelable.Creator<zzacw> CREATOR = new zzacx();
  private final HashMap<String, Map<String, zzacs.zza<?, ?>>> zzaHh;
  private final ArrayList<zza> zzaHi;
  private final String zzaHj;
  final int zzaiI;
  
  zzacw(int paramInt, ArrayList<zza> paramArrayList, String paramString)
  {
    this.zzaiI = paramInt;
    this.zzaHi = null;
    this.zzaHh = zzi(paramArrayList);
    this.zzaHj = ((String)zzac.zzw(paramString));
    zzyD();
  }
  
  private static HashMap<String, Map<String, zzacs.zza<?, ?>>> zzi(ArrayList<zza> paramArrayList)
  {
    HashMap localHashMap = new HashMap();
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      zza localzza = (zza)paramArrayList.get(i);
      localHashMap.put(localzza.className, localzza.zzyG());
      i += 1;
    }
    return localHashMap;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator1 = this.zzaHh.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localStringBuilder.append((String)localObject).append(":\n");
      localObject = (Map)this.zzaHh.get(localObject);
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
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzacx.zza(this, paramParcel, paramInt);
  }
  
  public Map<String, zzacs.zza<?, ?>> zzdw(String paramString)
  {
    return (Map)this.zzaHh.get(paramString);
  }
  
  public void zzyD()
  {
    Iterator localIterator1 = this.zzaHh.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localObject = (Map)this.zzaHh.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext()) {
        ((zzacs.zza)((Map)localObject).get((String)localIterator2.next())).zza(this);
      }
    }
  }
  
  ArrayList<zza> zzyE()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.zzaHh.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new zza(str, (Map)this.zzaHh.get(str)));
    }
    return localArrayList;
  }
  
  public String zzyF()
  {
    return this.zzaHj;
  }
  
  public static class zza
    extends zza
  {
    public static final Parcelable.Creator<zza> CREATOR = new zzacy();
    final String className;
    final int versionCode;
    final ArrayList<zzacw.zzb> zzaHk;
    
    zza(int paramInt, String paramString, ArrayList<zzacw.zzb> paramArrayList)
    {
      this.versionCode = paramInt;
      this.className = paramString;
      this.zzaHk = paramArrayList;
    }
    
    zza(String paramString, Map<String, zzacs.zza<?, ?>> paramMap)
    {
      this.versionCode = 1;
      this.className = paramString;
      this.zzaHk = zzX(paramMap);
    }
    
    private static ArrayList<zzacw.zzb> zzX(Map<String, zzacs.zza<?, ?>> paramMap)
    {
      if (paramMap == null) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localArrayList.add(new zzacw.zzb(str, (zzacs.zza)paramMap.get(str)));
      }
      return localArrayList;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzacy.zza(this, paramParcel, paramInt);
    }
    
    HashMap<String, zzacs.zza<?, ?>> zzyG()
    {
      HashMap localHashMap = new HashMap();
      int j = this.zzaHk.size();
      int i = 0;
      while (i < j)
      {
        zzacw.zzb localzzb = (zzacw.zzb)this.zzaHk.get(i);
        localHashMap.put(localzzb.zzaB, localzzb.zzaHl);
        i += 1;
      }
      return localHashMap;
    }
  }
  
  public static class zzb
    extends zza
  {
    public static final Parcelable.Creator<zzb> CREATOR = new zzacv();
    final int versionCode;
    final String zzaB;
    final zzacs.zza<?, ?> zzaHl;
    
    zzb(int paramInt, String paramString, zzacs.zza<?, ?> paramzza)
    {
      this.versionCode = paramInt;
      this.zzaB = paramString;
      this.zzaHl = paramzza;
    }
    
    zzb(String paramString, zzacs.zza<?, ?> paramzza)
    {
      this.versionCode = 1;
      this.zzaB = paramString;
      this.zzaHl = paramzza;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzacv.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */