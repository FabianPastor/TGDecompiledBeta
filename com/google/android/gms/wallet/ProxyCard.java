package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

@Deprecated
public final class ProxyCard
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ProxyCard> CREATOR = new zzak();
  private String zzeh;
  private String zzei;
  private int zzej;
  private int zzek;
  
  public ProxyCard(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    this.zzeh = paramString1;
    this.zzei = paramString2;
    this.zzej = paramInt1;
    this.zzek = paramInt2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzeh, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzei, false);
    SafeParcelWriter.writeInt(paramParcel, 4, this.zzej);
    SafeParcelWriter.writeInt(paramParcel, 5, this.zzek);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/ProxyCard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */