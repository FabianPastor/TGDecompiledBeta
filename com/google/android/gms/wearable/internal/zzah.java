package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;

public final class zzah
  extends AbstractSafeParcelable
  implements CapabilityInfo
{
  public static final Parcelable.Creator<zzah> CREATOR = new zzai();
  private final Object lock = new Object();
  private final String name;
  @GuardedBy("lock")
  private Set<Node> zzbt;
  private final List<zzfo> zzca;
  
  public zzah(String paramString, List<zzfo> paramList)
  {
    this.name = paramString;
    this.zzca = paramList;
    this.zzbt = null;
    Preconditions.checkNotNull(this.name);
    Preconditions.checkNotNull(this.zzca);
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    do
    {
      for (;;)
      {
        return bool;
        if ((paramObject != null) && (getClass() == paramObject.getClass())) {
          break;
        }
        bool = false;
      }
      paramObject = (zzah)paramObject;
      if (this.name != null)
      {
        if (this.name.equals(((zzah)paramObject).name)) {}
      }
      else {
        while (((zzah)paramObject).name != null)
        {
          bool = false;
          break;
        }
      }
      if (this.zzca == null) {
        break;
      }
    } while (this.zzca.equals(((zzah)paramObject).zzca));
    for (;;)
    {
      bool = false;
      break;
      if (((zzah)paramObject).zzca == null) {
        break;
      }
    }
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  public final Set<Node> getNodes()
  {
    synchronized (this.lock)
    {
      if (this.zzbt == null)
      {
        localObject2 = new java/util/HashSet;
        ((HashSet)localObject2).<init>(this.zzca);
        this.zzbt = ((Set)localObject2);
      }
      Object localObject2 = this.zzbt;
      return (Set<Node>)localObject2;
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    if (this.name != null) {}
    for (int j = this.name.hashCode();; j = 0)
    {
      if (this.zzca != null) {
        i = this.zzca.hashCode();
      }
      return (j + 31) * 31 + i;
    }
  }
  
  public final String toString()
  {
    String str1 = this.name;
    String str2 = String.valueOf(this.zzca);
    return String.valueOf(str1).length() + 18 + String.valueOf(str2).length() + "CapabilityInfo{" + str1 + ", " + str2 + "}";
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, getName(), false);
    SafeParcelWriter.writeTypedList(paramParcel, 3, this.zzca, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */