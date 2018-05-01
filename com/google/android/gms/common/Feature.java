package com.google.android.gms.common;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class Feature
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<Feature> CREATOR = new FeatureCreator();
  private final String name;
  @Deprecated
  private final int zzaq;
  private final long zzar;
  
  public Feature(String paramString, int paramInt, long paramLong)
  {
    this.name = paramString;
    this.zzaq = paramInt;
    this.zzar = paramLong;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if ((paramObject instanceof Feature))
    {
      paramObject = (Feature)paramObject;
      if ((getName() == null) || (!getName().equals(((Feature)paramObject).getName())))
      {
        bool2 = bool1;
        if (getName() == null)
        {
          bool2 = bool1;
          if (((Feature)paramObject).getName() != null) {}
        }
      }
      else
      {
        bool2 = bool1;
        if (getVersion() == ((Feature)paramObject).getVersion()) {
          bool2 = true;
        }
      }
    }
    return bool2;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public long getVersion()
  {
    if (this.zzar == -1L) {}
    for (long l = this.zzaq;; l = this.zzar) {
      return l;
    }
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { getName(), Long.valueOf(getVersion()) });
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).add("name", getName()).add("version", Long.valueOf(getVersion())).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 1, getName(), false);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzaq);
    SafeParcelWriter.writeLong(paramParcel, 3, getVersion());
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/Feature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */