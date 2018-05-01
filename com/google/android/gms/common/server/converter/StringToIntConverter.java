package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
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
  public static final Parcelable.Creator<StringToIntConverter> CREATOR = new zzb();
  private final HashMap<String, Integer> Fb;
  private final SparseArray<String> Fc;
  private final ArrayList<Entry> Fd;
  final int mVersionCode;
  
  public StringToIntConverter()
  {
    this.mVersionCode = 1;
    this.Fb = new HashMap();
    this.Fc = new SparseArray();
    this.Fd = null;
  }
  
  StringToIntConverter(int paramInt, ArrayList<Entry> paramArrayList)
  {
    this.mVersionCode = paramInt;
    this.Fb = new HashMap();
    this.Fc = new SparseArray();
    this.Fd = null;
    zzh(paramArrayList);
  }
  
  private void zzh(ArrayList<Entry> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      Entry localEntry = (Entry)paramArrayList.next();
      zzj(localEntry.Fe, localEntry.Ff);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
  
  ArrayList<Entry> zzawy()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.Fb.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new Entry(str, ((Integer)this.Fb.get(str)).intValue()));
    }
    return localArrayList;
  }
  
  public String zzd(Integer paramInteger)
  {
    String str = (String)this.Fc.get(paramInteger.intValue());
    paramInteger = str;
    if (str == null)
    {
      paramInteger = str;
      if (this.Fb.containsKey("gms_unknown")) {
        paramInteger = "gms_unknown";
      }
    }
    return paramInteger;
  }
  
  public StringToIntConverter zzj(String paramString, int paramInt)
  {
    this.Fb.put(paramString, Integer.valueOf(paramInt));
    this.Fc.put(paramInt, paramString);
    return this;
  }
  
  public static final class Entry
    extends AbstractSafeParcelable
  {
    public static final Parcelable.Creator<Entry> CREATOR = new zzc();
    final String Fe;
    final int Ff;
    final int versionCode;
    
    Entry(int paramInt1, String paramString, int paramInt2)
    {
      this.versionCode = paramInt1;
      this.Fe = paramString;
      this.Ff = paramInt2;
    }
    
    Entry(String paramString, int paramInt)
    {
      this.versionCode = 1;
      this.Fe = paramString;
      this.Ff = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzc.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/converter/StringToIntConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */