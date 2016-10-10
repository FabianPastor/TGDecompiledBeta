package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class EventParams
  extends AbstractSafeParcelable
  implements Iterable<String>
{
  public static final zzj CREATOR = new zzj();
  private final Bundle aow;
  public final int versionCode;
  
  EventParams(int paramInt, Bundle paramBundle)
  {
    this.versionCode = paramInt;
    this.aow = paramBundle;
  }
  
  EventParams(Bundle paramBundle)
  {
    zzac.zzy(paramBundle);
    this.aow = paramBundle;
    this.versionCode = 1;
  }
  
  Object get(String paramString)
  {
    return this.aow.get(paramString);
  }
  
  public Iterator<String> iterator()
  {
    new Iterator()
    {
      Iterator<String> aox = EventParams.zza(EventParams.this).keySet().iterator();
      
      public boolean hasNext()
      {
        return this.aox.hasNext();
      }
      
      public String next()
      {
        return (String)this.aox.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Remove not supported");
      }
    };
  }
  
  public int size()
  {
    return this.aow.size();
  }
  
  public String toString()
  {
    return this.aow.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzj.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzbvz()
  {
    return new Bundle(this.aow);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/EventParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */