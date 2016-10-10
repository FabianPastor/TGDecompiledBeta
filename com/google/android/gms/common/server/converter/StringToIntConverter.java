package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse.zza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public final class StringToIntConverter
  extends AbstractSafeParcelable
  implements FastJsonResponse.zza<String, Integer>
{
  public static final zzb CREATOR = new zzb();
  private final HashMap<String, Integer> Do;
  private final SparseArray<String> Dp;
  private final ArrayList<Entry> Dq;
  private final int mVersionCode;
  
  public StringToIntConverter()
  {
    this.mVersionCode = 1;
    this.Do = new HashMap();
    this.Dp = new SparseArray();
    this.Dq = null;
  }
  
  StringToIntConverter(int paramInt, ArrayList<Entry> paramArrayList)
  {
    this.mVersionCode = paramInt;
    this.Do = new HashMap();
    this.Dp = new SparseArray();
    this.Dq = null;
    zzh(paramArrayList);
  }
  
  private void zzh(ArrayList<Entry> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      Entry localEntry = (Entry)paramArrayList.next();
      zzj(localEntry.Dr, localEntry.Ds);
    }
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb localzzb = CREATOR;
    zzb.zza(this, paramParcel, paramInt);
  }
  
  ArrayList<Entry> zzavp()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.Do.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new Entry(str, ((Integer)this.Do.get(str)).intValue()));
    }
    return localArrayList;
  }
  
  public int zzavq()
  {
    return 7;
  }
  
  public int zzavr()
  {
    return 0;
  }
  
  public String zzd(Integer paramInteger)
  {
    String str = (String)this.Dp.get(paramInteger.intValue());
    paramInteger = str;
    if (str == null)
    {
      paramInteger = str;
      if (this.Do.containsKey("gms_unknown")) {
        paramInteger = "gms_unknown";
      }
    }
    return paramInteger;
  }
  
  public StringToIntConverter zzj(String paramString, int paramInt)
  {
    this.Do.put(paramString, Integer.valueOf(paramInt));
    this.Dp.put(paramInt, paramString);
    return this;
  }
  
  public static final class Entry
    extends AbstractSafeParcelable
  {
    public static final zzc CREATOR = new zzc();
    final String Dr;
    final int Ds;
    final int versionCode;
    
    Entry(int paramInt1, String paramString, int paramInt2)
    {
      this.versionCode = paramInt1;
      this.Dr = paramString;
      this.Ds = paramInt2;
    }
    
    Entry(String paramString, int paramInt)
    {
      this.versionCode = 1;
      this.Dr = paramString;
      this.Ds = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzc localzzc = CREATOR;
      zzc.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/converter/StringToIntConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */