package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.ArrayUtils;
import java.util.ArrayList;

public final class LabelValueRow
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LabelValueRow> CREATOR = new zze();
  String zzgn;
  String zzgo;
  ArrayList<LabelValue> zzgp;
  
  LabelValueRow()
  {
    this.zzgp = ArrayUtils.newArrayList();
  }
  
  LabelValueRow(String paramString1, String paramString2, ArrayList<LabelValue> paramArrayList)
  {
    this.zzgn = paramString1;
    this.zzgo = paramString2;
    this.zzgp = paramArrayList;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzgn, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzgo, false);
    SafeParcelWriter.writeTypedList(paramParcel, 4, this.zzgp, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/LabelValueRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */