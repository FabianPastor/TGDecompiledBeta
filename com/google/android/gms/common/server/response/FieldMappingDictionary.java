package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FieldMappingDictionary
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<FieldMappingDictionary> CREATOR = new zzc();
  private final HashMap<String, Map<String, FastJsonResponse.Field<?, ?>>> Fq;
  private final ArrayList<Entry> Fr;
  private final String Fs;
  final int mVersionCode;
  
  FieldMappingDictionary(int paramInt, ArrayList<Entry> paramArrayList, String paramString)
  {
    this.mVersionCode = paramInt;
    this.Fr = null;
    this.Fq = zzi(paramArrayList);
    this.Fs = ((String)zzaa.zzy(paramString));
    zzaxl();
  }
  
  private static HashMap<String, Map<String, FastJsonResponse.Field<?, ?>>> zzi(ArrayList<Entry> paramArrayList)
  {
    HashMap localHashMap = new HashMap();
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Entry localEntry = (Entry)paramArrayList.get(i);
      localHashMap.put(localEntry.className, localEntry.zzaxo());
      i += 1;
    }
    return localHashMap;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator1 = this.Fq.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localStringBuilder.append((String)localObject).append(":\n");
      localObject = (Map)this.Fq.get(localObject);
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
    zzc.zza(this, paramParcel, paramInt);
  }
  
  public void zzaxl()
  {
    Iterator localIterator1 = this.Fq.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localObject = (Map)this.Fq.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext()) {
        ((FastJsonResponse.Field)((Map)localObject).get((String)localIterator2.next())).zza(this);
      }
    }
  }
  
  ArrayList<Entry> zzaxm()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.Fq.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new Entry(str, (Map)this.Fq.get(str)));
    }
    return localArrayList;
  }
  
  public String zzaxn()
  {
    return this.Fs;
  }
  
  public Map<String, FastJsonResponse.Field<?, ?>> zzig(String paramString)
  {
    return (Map)this.Fq.get(paramString);
  }
  
  public static class Entry
    extends AbstractSafeParcelable
  {
    public static final Parcelable.Creator<Entry> CREATOR = new zzd();
    final ArrayList<FieldMappingDictionary.FieldMapPair> Ft;
    final String className;
    final int versionCode;
    
    Entry(int paramInt, String paramString, ArrayList<FieldMappingDictionary.FieldMapPair> paramArrayList)
    {
      this.versionCode = paramInt;
      this.className = paramString;
      this.Ft = paramArrayList;
    }
    
    Entry(String paramString, Map<String, FastJsonResponse.Field<?, ?>> paramMap)
    {
      this.versionCode = 1;
      this.className = paramString;
      this.Ft = zzaw(paramMap);
    }
    
    private static ArrayList<FieldMappingDictionary.FieldMapPair> zzaw(Map<String, FastJsonResponse.Field<?, ?>> paramMap)
    {
      if (paramMap == null) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localArrayList.add(new FieldMappingDictionary.FieldMapPair(str, (FastJsonResponse.Field)paramMap.get(str)));
      }
      return localArrayList;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzd.zza(this, paramParcel, paramInt);
    }
    
    HashMap<String, FastJsonResponse.Field<?, ?>> zzaxo()
    {
      HashMap localHashMap = new HashMap();
      int j = this.Ft.size();
      int i = 0;
      while (i < j)
      {
        FieldMappingDictionary.FieldMapPair localFieldMapPair = (FieldMappingDictionary.FieldMapPair)this.Ft.get(i);
        localHashMap.put(localFieldMapPair.zzcb, localFieldMapPair.Fu);
        i += 1;
      }
      return localHashMap;
    }
  }
  
  public static class FieldMapPair
    extends AbstractSafeParcelable
  {
    public static final Parcelable.Creator<FieldMapPair> CREATOR = new zzb();
    final FastJsonResponse.Field<?, ?> Fu;
    final int versionCode;
    final String zzcb;
    
    FieldMapPair(int paramInt, String paramString, FastJsonResponse.Field<?, ?> paramField)
    {
      this.versionCode = paramInt;
      this.zzcb = paramString;
      this.Fu = paramField;
    }
    
    FieldMapPair(String paramString, FastJsonResponse.Field<?, ?> paramField)
    {
      this.versionCode = 1;
      this.zzcb = paramString;
      this.Fu = paramField;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzb.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/FieldMappingDictionary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */