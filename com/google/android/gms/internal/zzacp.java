package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public final class zzacp
  extends zza
  implements zzacs.zzb<String, Integer>
{
  public static final Parcelable.Creator<zzacp> CREATOR = new zzacq();
  private final HashMap<String, Integer> zzaGS;
  private final SparseArray<String> zzaGT;
  private final ArrayList<zza> zzaGU;
  final int zzaiI;
  
  public zzacp()
  {
    this.zzaiI = 1;
    this.zzaGS = new HashMap();
    this.zzaGT = new SparseArray();
    this.zzaGU = null;
  }
  
  zzacp(int paramInt, ArrayList<zza> paramArrayList)
  {
    this.zzaiI = paramInt;
    this.zzaGS = new HashMap();
    this.zzaGT = new SparseArray();
    this.zzaGU = null;
    zzh(paramArrayList);
  }
  
  private void zzh(ArrayList<zza> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      zza localzza = (zza)paramArrayList.next();
      zzj(localzza.zzaGV, localzza.zzaGW);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzacq.zza(this, paramParcel, paramInt);
  }
  
  public String zzd(Integer paramInteger)
  {
    String str = (String)this.zzaGT.get(paramInteger.intValue());
    paramInteger = str;
    if (str == null)
    {
      paramInteger = str;
      if (this.zzaGS.containsKey("gms_unknown")) {
        paramInteger = "gms_unknown";
      }
    }
    return paramInteger;
  }
  
  public zzacp zzj(String paramString, int paramInt)
  {
    this.zzaGS.put(paramString, Integer.valueOf(paramInt));
    this.zzaGT.put(paramInt, paramString);
    return this;
  }
  
  ArrayList<zza> zzyq()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.zzaGS.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new zza(str, ((Integer)this.zzaGS.get(str)).intValue()));
    }
    return localArrayList;
  }
  
  public static final class zza
    extends zza
  {
    public static final Parcelable.Creator<zza> CREATOR = new zzacr();
    final int versionCode;
    final String zzaGV;
    final int zzaGW;
    
    zza(int paramInt1, String paramString, int paramInt2)
    {
      this.versionCode = paramInt1;
      this.zzaGV = paramString;
      this.zzaGW = paramInt2;
    }
    
    zza(String paramString, int paramInt)
    {
      this.versionCode = 1;
      this.zzaGV = paramString;
      this.zzaGW = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzacr.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */