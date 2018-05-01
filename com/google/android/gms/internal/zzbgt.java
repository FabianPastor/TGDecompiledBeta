package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzo;
import com.google.android.gms.common.util.zzp;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class zzbgt
  extends zzbgl
{
  public static final Parcelable.Creator<zzbgt> CREATOR = new zzbgu();
  private final String mClassName;
  private final zzbgo zzaIP;
  private final Parcel zzaIW;
  private final int zzaIX;
  private int zzaIY;
  private int zzaIZ;
  private final int zzaku;
  
  zzbgt(int paramInt, Parcel paramParcel, zzbgo paramzzbgo)
  {
    this.zzaku = paramInt;
    this.zzaIW = ((Parcel)zzbo.zzu(paramParcel));
    this.zzaIX = 2;
    this.zzaIP = paramzzbgo;
    if (this.zzaIP == null) {}
    for (this.mClassName = null;; this.mClassName = this.zzaIP.zzrR())
    {
      this.zzaIY = 2;
      return;
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, Object paramObject)
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
      paramStringBuilder.append("\"").append(zzo.zzcK(paramObject.toString())).append("\"");
      return;
    case 8: 
      paramStringBuilder.append("\"").append(com.google.android.gms.common.util.zzc.encode((byte[])paramObject)).append("\"");
      return;
    case 9: 
      paramStringBuilder.append("\"").append(com.google.android.gms.common.util.zzc.zzg((byte[])paramObject));
      paramStringBuilder.append("\"");
      return;
    case 10: 
      zzp.zza(paramStringBuilder, (HashMap)paramObject);
      return;
    }
    throw new IllegalArgumentException("Method does not accept concrete type.");
  }
  
  private final void zza(StringBuilder paramStringBuilder, zzbgj<?, ?> paramzzbgj, Parcel paramParcel, int paramInt)
  {
    Object localObject = null;
    String str = null;
    int i = 0;
    int j = 0;
    if (paramzzbgj.zzaIK)
    {
      paramStringBuilder.append("[");
      int k;
      switch (paramzzbgj.zzaIJ)
      {
      default: 
        throw new IllegalStateException("Unknown field type out.");
      case 0: 
        paramzzbgj = com.google.android.gms.common.internal.safeparcel.zzb.zzw(paramParcel, paramInt);
        i = paramzzbgj.length;
        paramInt = j;
      case 1: 
        while (paramInt < i)
        {
          if (paramInt != 0) {
            paramStringBuilder.append(",");
          }
          paramStringBuilder.append(Integer.toString(paramzzbgj[paramInt]));
          paramInt += 1;
          continue;
          j = com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, paramInt);
          k = paramParcel.dataPosition();
          if (j != 0) {
            break label192;
          }
          paramzzbgj = str;
          com.google.android.gms.common.util.zzb.zza(paramStringBuilder, paramzzbgj);
        }
      }
      for (;;)
      {
        paramStringBuilder.append("]");
        return;
        label192:
        int m = paramParcel.readInt();
        paramzzbgj = new BigInteger[m];
        paramInt = i;
        while (paramInt < m)
        {
          paramzzbgj[paramInt] = new BigInteger(paramParcel.createByteArray());
          paramInt += 1;
        }
        paramParcel.setDataPosition(j + k);
        break;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, com.google.android.gms.common.internal.safeparcel.zzb.zzx(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, com.google.android.gms.common.internal.safeparcel.zzb.zzy(paramParcel, paramInt));
        continue;
        paramInt = com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, paramInt);
        i = paramParcel.dataPosition();
        if (paramInt == 0) {
          paramzzbgj = (zzbgj<?, ?>)localObject;
        }
        for (;;)
        {
          com.google.android.gms.common.util.zzb.zza(paramStringBuilder, paramzzbgj);
          break;
          paramzzbgj = paramParcel.createDoubleArray();
          paramParcel.setDataPosition(paramInt + i);
        }
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, com.google.android.gms.common.internal.safeparcel.zzb.zzz(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, com.google.android.gms.common.internal.safeparcel.zzb.zzv(paramParcel, paramInt));
        continue;
        com.google.android.gms.common.util.zzb.zza(paramStringBuilder, com.google.android.gms.common.internal.safeparcel.zzb.zzA(paramParcel, paramInt));
        continue;
        throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
        paramParcel = com.google.android.gms.common.internal.safeparcel.zzb.zzE(paramParcel, paramInt);
        i = paramParcel.length;
        paramInt = 0;
        while (paramInt < i)
        {
          if (paramInt > 0) {
            paramStringBuilder.append(",");
          }
          paramParcel[paramInt].setDataPosition(0);
          zza(paramStringBuilder, paramzzbgj.zzrP(), paramParcel[paramInt]);
          paramInt += 1;
        }
      }
    }
    switch (paramzzbgj.zzaIJ)
    {
    default: 
      throw new IllegalStateException("Unknown field type out");
    case 0: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzg(paramParcel, paramInt));
      return;
    case 1: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzk(paramParcel, paramInt));
      return;
    case 2: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzi(paramParcel, paramInt));
      return;
    case 3: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzl(paramParcel, paramInt));
      return;
    case 4: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzn(paramParcel, paramInt));
      return;
    case 5: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzp(paramParcel, paramInt));
      return;
    case 6: 
      paramStringBuilder.append(com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, paramInt));
      return;
    case 7: 
      paramzzbgj = com.google.android.gms.common.internal.safeparcel.zzb.zzq(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(zzo.zzcK(paramzzbgj)).append("\"");
      return;
    case 8: 
      paramzzbgj = com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(com.google.android.gms.common.util.zzc.encode(paramzzbgj)).append("\"");
      return;
    case 9: 
      paramzzbgj = com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, paramInt);
      paramStringBuilder.append("\"").append(com.google.android.gms.common.util.zzc.zzg(paramzzbgj));
      paramStringBuilder.append("\"");
      return;
    case 10: 
      paramzzbgj = com.google.android.gms.common.internal.safeparcel.zzb.zzs(paramParcel, paramInt);
      paramParcel = paramzzbgj.keySet();
      paramParcel.size();
      paramStringBuilder.append("{");
      paramParcel = paramParcel.iterator();
      for (paramInt = 1; paramParcel.hasNext(); paramInt = 0)
      {
        str = (String)paramParcel.next();
        if (paramInt == 0) {
          paramStringBuilder.append(",");
        }
        paramStringBuilder.append("\"").append(str).append("\"");
        paramStringBuilder.append(":");
        paramStringBuilder.append("\"").append(zzo.zzcK(paramzzbgj.getString(str))).append("\"");
      }
      paramStringBuilder.append("}");
      return;
    }
    paramParcel = com.google.android.gms.common.internal.safeparcel.zzb.zzD(paramParcel, paramInt);
    paramParcel.setDataPosition(0);
    zza(paramStringBuilder, paramzzbgj.zzrP(), paramParcel);
  }
  
  private final void zza(StringBuilder paramStringBuilder, Map<String, zzbgj<?, ?>> paramMap, Parcel paramParcel)
  {
    SparseArray localSparseArray = new SparseArray();
    paramMap = paramMap.entrySet().iterator();
    Object localObject;
    while (paramMap.hasNext())
    {
      localObject = (Map.Entry)paramMap.next();
      localSparseArray.put(((zzbgj)((Map.Entry)localObject).getValue()).zzaIM, localObject);
    }
    paramStringBuilder.append('{');
    int j = com.google.android.gms.common.internal.safeparcel.zzb.zzd(paramParcel);
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = paramParcel.readInt();
      localObject = (Map.Entry)localSparseArray.get(0xFFFF & k);
      if (localObject != null)
      {
        if (i != 0) {
          paramStringBuilder.append(",");
        }
        paramMap = (String)((Map.Entry)localObject).getKey();
        localObject = (zzbgj)((Map.Entry)localObject).getValue();
        paramStringBuilder.append("\"").append(paramMap).append("\":");
        if (((zzbgj)localObject).zzrO()) {
          switch (((zzbgj)localObject).zzaIJ)
          {
          default: 
            i = ((zzbgj)localObject).zzaIJ;
            throw new IllegalArgumentException(36 + "Unknown field out type = " + i);
          case 0: 
            zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, Integer.valueOf(com.google.android.gms.common.internal.safeparcel.zzb.zzg(paramParcel, k))));
          }
        }
        for (;;)
        {
          i = 1;
          break;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, com.google.android.gms.common.internal.safeparcel.zzb.zzk(paramParcel, k)));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, Long.valueOf(com.google.android.gms.common.internal.safeparcel.zzb.zzi(paramParcel, k))));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, Float.valueOf(com.google.android.gms.common.internal.safeparcel.zzb.zzl(paramParcel, k))));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, Double.valueOf(com.google.android.gms.common.internal.safeparcel.zzb.zzn(paramParcel, k))));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, com.google.android.gms.common.internal.safeparcel.zzb.zzp(paramParcel, k)));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, Boolean.valueOf(com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, k))));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, com.google.android.gms.common.internal.safeparcel.zzb.zzq(paramParcel, k)));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, k)));
          continue;
          zzb(paramStringBuilder, (zzbgj)localObject, zza((zzbgj)localObject, zzo(com.google.android.gms.common.internal.safeparcel.zzb.zzs(paramParcel, k))));
          continue;
          throw new IllegalArgumentException("Method does not accept concrete type.");
          zza(paramStringBuilder, (zzbgj)localObject, paramParcel, k);
        }
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new com.google.android.gms.common.internal.safeparcel.zzc(37 + "Overread allowed size end=" + j, paramParcel);
    }
    paramStringBuilder.append('}');
  }
  
  private final void zzb(StringBuilder paramStringBuilder, zzbgj<?, ?> paramzzbgj, Object paramObject)
  {
    if (paramzzbgj.zzaII)
    {
      paramObject = (ArrayList)paramObject;
      paramStringBuilder.append("[");
      int j = ((ArrayList)paramObject).size();
      int i = 0;
      while (i < j)
      {
        if (i != 0) {
          paramStringBuilder.append(",");
        }
        zza(paramStringBuilder, paramzzbgj.zzaIH, ((ArrayList)paramObject).get(i));
        i += 1;
      }
      paramStringBuilder.append("]");
      return;
    }
    zza(paramStringBuilder, paramzzbgj.zzaIH, paramObject);
  }
  
  private static HashMap<String, String> zzo(Bundle paramBundle)
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
  
  private Parcel zzrT()
  {
    switch (this.zzaIY)
    {
    }
    for (;;)
    {
      return this.zzaIW;
      this.zzaIZ = zzd.zze(this.zzaIW);
      zzd.zzI(this.zzaIW, this.zzaIZ);
      this.zzaIY = 2;
    }
  }
  
  public String toString()
  {
    zzbo.zzb(this.zzaIP, "Cannot convert to JSON on client side.");
    Parcel localParcel = zzrT();
    localParcel.setDataPosition(0);
    StringBuilder localStringBuilder = new StringBuilder(100);
    zza(localStringBuilder, this.zzaIP.zzcJ(this.mClassName), localParcel);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, zzrT(), false);
    Object localObject;
    switch (this.zzaIX)
    {
    default: 
      paramInt = this.zzaIX;
      throw new IllegalStateException(34 + "Invalid creation type: " + paramInt);
    case 0: 
      localObject = null;
    }
    for (;;)
    {
      zzd.zza(paramParcel, 3, (Parcelable)localObject, paramInt, false);
      zzd.zzI(paramParcel, i);
      return;
      localObject = this.zzaIP;
      continue;
      localObject = this.zzaIP;
    }
  }
  
  public final Object zzcH(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
  
  public final boolean zzcI(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
  
  public final Map<String, zzbgj<?, ?>> zzrL()
  {
    if (this.zzaIP == null) {
      return null;
    }
    return this.zzaIP.zzcJ(this.mClassName);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */