package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzatd
  extends zza
{
  public static final Parcelable.Creator<zzatd> CREATOR = new zzate();
  public final String packageName;
  public final String zzbhN;
  public final String zzbqK;
  public final String zzbqL;
  public final long zzbqM;
  public final long zzbqN;
  public final String zzbqO;
  public final boolean zzbqP;
  public final boolean zzbqQ;
  public final long zzbqR;
  public final String zzbqS;
  public final long zzbqT;
  public final long zzbqU;
  public final int zzbqV;
  
  zzatd(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6, long paramLong4, long paramLong5, int paramInt)
  {
    zzac.zzdr(paramString1);
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzbqK = paramString1;
    this.zzbhN = paramString3;
    this.zzbqR = paramLong1;
    this.zzbqL = paramString4;
    this.zzbqM = paramLong2;
    this.zzbqN = paramLong3;
    this.zzbqO = paramString5;
    this.zzbqP = paramBoolean1;
    this.zzbqQ = paramBoolean2;
    this.zzbqS = paramString6;
    this.zzbqT = paramLong4;
    this.zzbqU = paramLong5;
    this.zzbqV = paramInt;
  }
  
  zzatd(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6, long paramLong4, long paramLong5, int paramInt)
  {
    this.packageName = paramString1;
    this.zzbqK = paramString2;
    this.zzbhN = paramString3;
    this.zzbqR = paramLong3;
    this.zzbqL = paramString4;
    this.zzbqM = paramLong1;
    this.zzbqN = paramLong2;
    this.zzbqO = paramString5;
    this.zzbqP = paramBoolean1;
    this.zzbqQ = paramBoolean2;
    this.zzbqS = paramString6;
    this.zzbqT = paramLong4;
    this.zzbqU = paramLong5;
    this.zzbqV = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzate.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */