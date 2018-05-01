package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;

public class AppMetadata
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<AppMetadata> CREATOR = new zzb();
  public final String aii;
  public final String aqZ;
  public final String ara;
  public final long arb;
  public final long arc;
  public final String ard;
  public final boolean are;
  public final boolean arf;
  public final long arg;
  public final String arh;
  public final String packageName;
  public final int versionCode;
  
  AppMetadata(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.aqZ = paramString2;
    this.aii = paramString3;
    if (paramInt >= 5)
    {
      this.arg = paramLong3;
      this.ara = paramString4;
      this.arb = paramLong1;
      this.arc = paramLong2;
      this.ard = paramString5;
      if (paramInt < 3) {
        break label92;
      }
    }
    label92:
    for (this.are = paramBoolean1;; this.are = true)
    {
      this.arf = paramBoolean2;
      this.arh = paramString6;
      return;
      paramLong3 = -2147483648L;
      break;
    }
  }
  
  AppMetadata(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6)
  {
    zzaa.zzib(paramString1);
    this.versionCode = 6;
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.aqZ = paramString1;
    this.aii = paramString3;
    this.arg = paramLong1;
    this.ara = paramString4;
    this.arb = paramLong2;
    this.arc = paramLong3;
    this.ard = paramString5;
    this.are = paramBoolean1;
    this.arf = paramBoolean2;
    this.arh = paramString6;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/AppMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */