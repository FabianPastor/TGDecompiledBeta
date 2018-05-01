package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class PatternItem
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<PatternItem> CREATOR = new zzi();
  private static final String TAG = PatternItem.class.getSimpleName();
  private final int type;
  private final Float zzdu;
  
  public PatternItem(int paramInt, Float paramFloat)
  {
    boolean bool2 = bool1;
    if (paramInt != 1) {
      if ((paramFloat == null) || (paramFloat.floatValue() < 0.0F)) {
        break label92;
      }
    }
    label92:
    for (bool2 = bool1;; bool2 = false)
    {
      String str = String.valueOf(paramFloat);
      Preconditions.checkArgument(bool2, String.valueOf(str).length() + 45 + "Invalid PatternItem: type=" + paramInt + " length=" + str);
      this.type = paramInt;
      this.zzdu = paramFloat;
      return;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof PatternItem))
      {
        bool = false;
      }
      else
      {
        paramObject = (PatternItem)paramObject;
        if ((this.type != ((PatternItem)paramObject).type) || (!Objects.equal(this.zzdu, ((PatternItem)paramObject).zzdu))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.type), this.zzdu });
  }
  
  public String toString()
  {
    int i = this.type;
    String str = String.valueOf(this.zzdu);
    return String.valueOf(str).length() + 39 + "[PatternItem: type=" + i + " length=" + str + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.type);
    SafeParcelWriter.writeFloatObject(paramParcel, 3, this.zzdu, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PatternItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */