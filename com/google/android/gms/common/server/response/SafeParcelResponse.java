package com.google.android.gms.common.server.response;

import android.os.Bundle;
import android.os.Parcel;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SafeParcelResponse
  extends FastSafeParcelableJsonResponse
{
  public static final zze CREATOR = new zze();
  private final FieldMappingDictionary DB;
  private final Parcel DI;
  private final int DJ;
  private int DK;
  private int DL;
  private final String mClassName;
  private final int mVersionCode;
  
  SafeParcelResponse(int paramInt, Parcel paramParcel, FieldMappingDictionary paramFieldMappingDictionary)
  {
    this.mVersionCode = paramInt;
    this.DI = ((Parcel)zzac.zzy(paramParcel));
    this.DJ = 2;
    this.DB = paramFieldMappingDictionary;
    if (this.DB == null) {}
    for (this.mClassName = null;; this.mClassName = this.DB.zzawg())
    {
      this.DK = 2;
      return;
    }
  }
  
  private void zza(StringBuilder paramStringBuilder, int paramInt, Object paramObject)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException(26 + "Unknown type = " + paramInt);
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
      paramStringBuilder.append(paramObject);
      return;
    case 7: 
      paramStringBuilder.append("\"").append(zzp.zzii(paramObject.toString())).append("\"");
      return;
    case 8: 
      paramStringBuilder.append("\"").append(zzc.zzp((byte[])paramObject)).append("\"");
      return;
    case 9: 
      paramStringBuilder.append("\"").append(zzc.zzq((byte[])paramObject));
      paramStringBuilder.append("\"");
      return;
    case 10: 
      zzq.zza(paramStringBuilder, (HashMap)paramObject);
      return;
    }
    throw new IllegalArgumentException("Method does not accept concrete type.");
  }
  
  private void zza(StringBuilder paramStringBuilder, FastJsonResponse.Field<?, ?> paramField, Parcel paramParcel, int paramInt)
  {
    switch (paramField.zzavr())
    {
    default: 
      paramInt = paramField.zzavr();
      throw new IllegalArgumentException(36 + "Unknown field out type = " + paramInt);
    case 0: 
      zzb(paramStringBuilder, paramField, zza(paramField, Integer.valueOf(zza.zzg(paramParcel, paramInt))));
      return;
    case 1: 
      zzb(paramStringBuilder, paramField, zza(paramField, zza.zzk(paramParcel, paramInt)));
      return;
    case 2: 
      zzb(paramStringBuilder, paramField, zza(paramField, Long.valueOf(zza.zzi(paramParcel, paramInt))));
      return;
    case 3: 
      zzb(paramStringBuilder, paramField, zza(paramField, Float.valueOf(zza.zzl(paramParcel, paramInt))));
      return;
    case 4: 
      zzb(paramStringBuilder, paramField, zza(paramField, Double.valueOf(zza.zzn(paramParcel, paramInt))));
      return;
    case 5: 
      zzb(paramStringBuilder, paramField, zza(paramField, zza.zzp(paramParcel, paramInt)));
      return;
    case 6: 
      zzb(paramStringBuilder, paramField, zza(paramField, Boolean.valueOf(zza.zzc(paramParcel, paramInt))));
      return;
    case 7: 
      zzb(paramStringBuilder, paramField, zza(paramField, zza.zzq(paramParcel, paramInt)));
      return;
    case 8: 
    case 9: 
      zzb(paramStringBuilder, paramField, zza(paramField, zza.zzt(paramParcel, paramInt)));
      return;
    case 10: 
      zzb(paramStringBuilder, paramField, zza(paramField, zzq(zza.zzs(paramParcel, paramInt))));
      return;
    }
    throw new IllegalArgumentException("Method does not accept concrete type.");
  }
  
  private void zza(StringBuilder paramStringBuilder, String paramString, FastJsonResponse.Field<?, ?> paramField, Parcel paramParcel, int paramInt)
  {
    paramStringBuilder.append("\"").append(paramString).append("\":");
    if (paramField.zzawb())
    {
      zza(paramStringBuilder, paramField, paramParcel, paramInt);
      return;
    }
    zzb(paramStringBuilder, paramField, paramParcel, paramInt);
  }
  
  private void zza(StringBuilder paramStringBuilder, Map<String, FastJsonResponse.Field<?, ?>> paramMap, Parcel paramParcel)
  {
    paramMap = zzav(paramMap);
    paramStringBuilder.append('{');
    int j = zza.zzcq(paramParcel);
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      Map.Entry localEntry = (Map.Entry)paramMap.get(zza.zzgv(k));
      if (localEntry != null)
      {
        if (i != 0) {
          paramStringBuilder.append(",");
        }
        zza(paramStringBuilder, (String)localEntry.getKey(), (FastJsonResponse.Field)localEntry.getValue(), paramParcel, k);
        i = 1;
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    paramStringBuilder.append('}');
  }
  
  private static SparseArray<Map.Entry<String, FastJsonResponse.Field<?, ?>>> zzav(Map<String, FastJsonResponse.Field<?, ?>> paramMap)
  {
    SparseArray localSparseArray = new SparseArray();
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      localSparseArray.put(((FastJsonResponse.Field)localEntry.getValue()).zzavy(), localEntry);
    }
    return localSparseArray;
  }
  
  private void zzb(StringBuilder paramStringBuilder, FastJsonResponse.Field<?, ?> paramField, Parcel paramParcel, int paramInt)
  {
    if (paramField.zzavw())
    {
      paramStringBuilder.append("[");
      switch (paramField.zzavr())
      {
      default: 
        throw new IllegalStateException("Unknown field type out.");
      case 0: 
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzw(paramParcel, paramInt));
      }
      for (;;)
      {
        paramStringBuilder.append("]");
        return;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzy(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzx(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzz(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzaa(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzab(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzv(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, zza.zzac(paramParcel, paramInt));
        continue;
        throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
        paramParcel = zza.zzag(paramParcel, paramInt);
        int i = paramParcel.length;
        paramInt = 0;
        while (paramInt < i)
        {
          if (paramInt > 0) {
            paramStringBuilder.append(",");
          }
          paramParcel[paramInt].setDataPosition(0);
          zza(paramStringBuilder, paramField.zzawd(), paramParcel[paramInt]);
          paramInt += 1;
        }
      }
    }
    switch (paramField.zzavr())
    {
    default: 
      throw new IllegalStateException("Unknown field type out");
    case 0: 
      paramStringBuilder.append(zza.zzg(paramParcel, paramInt));
      return;
    case 1: 
      paramStringBuilder.append(zza.zzk(paramParcel, paramInt));
      return;
    case 2: 
      paramStringBuilder.append(zza.zzi(paramParcel, paramInt));
      return;
    case 3: 
      paramStringBuilder.append(zza.zzl(paramParcel, paramInt));
      return;
    case 4: 
      paramStringBuilder.append(zza.zzn(paramParcel, paramInt));
      return;
    case 5: 
      paramStringBuilder.append(zza.zzp(paramParcel, paramInt));
      return;
    case 6: 
      paramStringBuilder.append(zza.zzc(paramParcel, paramInt));
      return;
    case 7: 
      paramField = zza.zzq(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(zzp.zzii(paramField)).append("\"");
      return;
    case 8: 
      paramField = zza.zzt(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(zzc.zzp(paramField)).append("\"");
      return;
    case 9: 
      paramField = zza.zzt(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(zzc.zzq(paramField));
      paramStringBuilder.append("\"");
      return;
    case 10: 
      paramField = zza.zzs(paramParcel, paramInt);
      paramParcel = paramField.keySet();
      paramParcel.size();
      paramStringBuilder.append("{");
      paramParcel = paramParcel.iterator();
      for (paramInt = 1; paramParcel.hasNext(); paramInt = 0)
      {
        String str = (String)paramParcel.next();
        if (paramInt == 0) {
          paramStringBuilder.append(",");
        }
        paramStringBuilder.append("\"").append(str).append("\"");
        paramStringBuilder.append(":");
        paramStringBuilder.append("\"").append(zzp.zzii(paramField.getString(str))).append("\"");
      }
      paramStringBuilder.append("}");
      return;
    }
    paramParcel = zza.zzaf(paramParcel, paramInt);
    paramParcel.setDataPosition(0);
    zza(paramStringBuilder, paramField.zzawd(), paramParcel);
  }
  
  private void zzb(StringBuilder paramStringBuilder, FastJsonResponse.Field<?, ?> paramField, Object paramObject)
  {
    if (paramField.zzavv())
    {
      zzb(paramStringBuilder, paramField, (ArrayList)paramObject);
      return;
    }
    zza(paramStringBuilder, paramField.zzavq(), paramObject);
  }
  
  private void zzb(StringBuilder paramStringBuilder, FastJsonResponse.Field<?, ?> paramField, ArrayList<?> paramArrayList)
  {
    paramStringBuilder.append("[");
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      if (i != 0) {
        paramStringBuilder.append(",");
      }
      zza(paramStringBuilder, paramField.zzavq(), paramArrayList.get(i));
      i += 1;
    }
    paramStringBuilder.append("]");
  }
  
  public static HashMap<String, String> zzq(Bundle paramBundle)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localHashMap.put(str, paramBundle.getString(str));
    }
    return localHashMap;
  }
  
  public int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public String toString()
  {
    zzac.zzb(this.DB, "Cannot convert to JSON on client side.");
    Parcel localParcel = zzawi();
    localParcel.setDataPosition(0);
    StringBuilder localStringBuilder = new StringBuilder(100);
    zza(localStringBuilder, this.DB.zzie(this.mClassName), localParcel);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zze localzze = CREATOR;
    zze.zza(this, paramParcel, paramInt);
  }
  
  public Map<String, FastJsonResponse.Field<?, ?>> zzavs()
  {
    if (this.DB == null) {
      return null;
    }
    return this.DB.zzie(this.mClassName);
  }
  
  public Parcel zzawi()
  {
    switch (this.DK)
    {
    }
    for (;;)
    {
      return this.DI;
      this.DL = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(this.DI);
      com.google.android.gms.common.internal.safeparcel.zzb.zzaj(this.DI, this.DL);
      this.DK = 2;
      continue;
      com.google.android.gms.common.internal.safeparcel.zzb.zzaj(this.DI, this.DL);
      this.DK = 2;
    }
  }
  
  FieldMappingDictionary zzawj()
  {
    switch (this.DJ)
    {
    default: 
      int i = this.DJ;
      throw new IllegalStateException(34 + "Invalid creation type: " + i);
    case 0: 
      return null;
    case 1: 
      return this.DB;
    }
    return this.DB;
  }
  
  public Object zzia(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
  
  public boolean zzib(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/SafeParcelResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */