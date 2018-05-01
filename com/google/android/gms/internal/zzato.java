package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.Iterator;
import java.util.Set;

public class zzato
  extends zza
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzato> CREATOR = new zzatp();
  private final Bundle zzbrE;
  
  zzato(Bundle paramBundle)
  {
    this.zzbrE = paramBundle;
  }
  
  Object get(String paramString)
  {
    return this.zzbrE.get(paramString);
  }
  
  public Iterator<String> iterator()
  {
    new Iterator()
    {
      Iterator<String> zzbrF = zzato.zza(zzato.this).keySet().iterator();
      
      public boolean hasNext()
      {
        return this.zzbrF.hasNext();
      }
      
      public String next()
      {
        return (String)this.zzbrF.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Remove not supported");
      }
    };
  }
  
  public int size()
  {
    return this.zzbrE.size();
  }
  
  public String toString()
  {
    return this.zzbrE.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzatp.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzLW()
  {
    return new Bundle(this.zzbrE);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzato.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */