package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public class AppMetadata
  extends AbstractSafeParcelable
{
  public static final zzb CREATOR = new zzb();
  public final String afY;
  public final String anQ;
  public final String anR;
  public final long anS;
  public final long anT;
  public final String anU;
  public final boolean anV;
  public final boolean anW;
  public final long anX;
  public final String anY;
  public final String packageName;
  public final int versionCode;
  
  AppMetadata(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.anQ = paramString2;
    this.afY = paramString3;
    if (paramInt >= 5)
    {
      this.anX = paramLong3;
      this.anR = paramString4;
      this.anS = paramLong1;
      this.anT = paramLong2;
      this.anU = paramString5;
      if (paramInt < 3) {
        break label92;
      }
    }
    label92:
    for (this.anV = paramBoolean1;; this.anV = true)
    {
      this.anW = paramBoolean2;
      this.anY = paramString6;
      return;
      paramLong3 = -2147483648L;
      break;
    }
  }
  
  AppMetadata(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6)
  {
    zzac.zzhz(paramString1);
    this.versionCode = 6;
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.anQ = paramString1;
    this.afY = paramString3;
    this.anX = paramLong1;
    this.anR = paramString4;
    this.anS = paramLong2;
    this.anT = paramLong3;
    this.anU = paramString5;
    this.anV = paramBoolean1;
    this.anW = paramBoolean2;
    this.anY = paramString6;
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