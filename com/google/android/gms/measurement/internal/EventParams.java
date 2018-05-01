package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import java.util.Iterator;
import java.util.Set;

public class EventParams
  extends AbstractSafeParcelable
  implements Iterable<String>
{
  public static final Parcelable.Creator<EventParams> CREATOR = new zzj();
  private final Bundle arG;
  public final int versionCode;
  
  EventParams(int paramInt, Bundle paramBundle)
  {
    this.versionCode = paramInt;
    this.arG = paramBundle;
  }
  
  EventParams(Bundle paramBundle)
  {
    zzaa.zzy(paramBundle);
    this.arG = paramBundle;
    this.versionCode = 1;
  }
  
  Object get(String paramString)
  {
    return this.arG.get(paramString);
  }
  
  public Iterator<String> iterator()
  {
    new Iterator()
    {
      Iterator<String> arH = EventParams.zza(EventParams.this).keySet().iterator();
      
      public boolean hasNext()
      {
        return this.arH.hasNext();
      }
      
      public String next()
      {
        return (String)this.arH.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Remove not supported");
      }
    };
  }
  
  public int size()
  {
    return this.arG.size();
  }
  
  public String toString()
  {
    return this.arG.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzj.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzbww()
  {
    return new Bundle(this.arG);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/EventParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */