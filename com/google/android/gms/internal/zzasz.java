package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class zzasz
  extends zza
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzasz> CREATOR = new zzata();
  public final int versionCode;
  private final Bundle zzbqM;
  
  zzasz(int paramInt, Bundle paramBundle)
  {
    this.versionCode = paramInt;
    this.zzbqM = paramBundle;
  }
  
  zzasz(Bundle paramBundle)
  {
    zzac.zzw(paramBundle);
    this.zzbqM = paramBundle;
    this.versionCode = 1;
  }
  
  Object get(String paramString)
  {
    return this.zzbqM.get(paramString);
  }
  
  public Iterator<String> iterator()
  {
    new Iterator()
    {
      Iterator<String> zzbqN = zzasz.zza(zzasz.this).keySet().iterator();
      
      public boolean hasNext()
      {
        return this.zzbqN.hasNext();
      }
      
      public String next()
      {
        return (String)this.zzbqN.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Remove not supported");
      }
    };
  }
  
  public int size()
  {
    return this.zzbqM.size();
  }
  
  public String toString()
  {
    return this.zzbqM.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzata.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzKY()
  {
    return new Bundle(this.zzbqM);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */