package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.Node;

public final class zzfo
  extends AbstractSafeParcelable
  implements Node
{
  public static final Parcelable.Creator<zzfo> CREATOR = new zzfp();
  private final String zzbk;
  private final String zzdm;
  private final int zzen;
  private final boolean zzeo;
  
  public zzfo(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    this.zzdm = paramString1;
    this.zzbk = paramString2;
    this.zzen = paramInt;
    this.zzeo = paramBoolean;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzfo)) {}
    for (boolean bool = false;; bool = ((zzfo)paramObject).zzdm.equals(this.zzdm)) {
      return bool;
    }
  }
  
  public final String getDisplayName()
  {
    return this.zzbk;
  }
  
  public final String getId()
  {
    return this.zzdm;
  }
  
  public final int hashCode()
  {
    return this.zzdm.hashCode();
  }
  
  public final boolean isNearby()
  {
    return this.zzeo;
  }
  
  public final String toString()
  {
    String str1 = this.zzbk;
    String str2 = this.zzdm;
    int i = this.zzen;
    boolean bool = this.zzeo;
    return String.valueOf(str1).length() + 45 + String.valueOf(str2).length() + "Node{" + str1 + ", id=" + str2 + ", hops=" + i + ", isNearby=" + bool + "}";
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, getId(), false);
    SafeParcelWriter.writeString(paramParcel, 3, getDisplayName(), false);
    SafeParcelWriter.writeInt(paramParcel, 4, this.zzen);
    SafeParcelWriter.writeBoolean(paramParcel, 5, isNearby());
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */