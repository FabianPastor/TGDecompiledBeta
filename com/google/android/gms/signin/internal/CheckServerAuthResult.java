package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.List;

public class CheckServerAuthResult
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<CheckServerAuthResult> CREATOR = new CheckServerAuthResultCreator();
  private final boolean zzadp;
  private final List<Scope> zzadq;
  private final int zzal;
  
  CheckServerAuthResult(int paramInt, boolean paramBoolean, List<Scope> paramList)
  {
    this.zzal = paramInt;
    this.zzadp = paramBoolean;
    this.zzadq = paramList;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeBoolean(paramParcel, 2, this.zzadp);
    SafeParcelWriter.writeTypedList(paramParcel, 3, this.zzadq, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/CheckServerAuthResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */