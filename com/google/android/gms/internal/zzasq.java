package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzasq
  extends zza
{
  public static final Parcelable.Creator<zzasq> CREATOR = new zzasr();
  public final String packageName;
  public final int versionCode;
  public final String zzbhg;
  public final String zzbqf;
  public final String zzbqg;
  public final long zzbqh;
  public final long zzbqi;
  public final String zzbqj;
  public final boolean zzbqk;
  public final boolean zzbql;
  public final long zzbqm;
  public final String zzbqn;
  
  zzasq(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.zzbqf = paramString2;
    this.zzbhg = paramString3;
    if (paramInt >= 5)
    {
      this.zzbqm = paramLong3;
      this.zzbqg = paramString4;
      this.zzbqh = paramLong1;
      this.zzbqi = paramLong2;
      this.zzbqj = paramString5;
      if (paramInt < 3) {
        break label92;
      }
    }
    label92:
    for (this.zzbqk = paramBoolean1;; this.zzbqk = true)
    {
      this.zzbql = paramBoolean2;
      this.zzbqn = paramString6;
      return;
      paramLong3 = -2147483648L;
      break;
    }
  }
  
  zzasq(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6)
  {
    zzac.zzdv(paramString1);
    this.versionCode = 6;
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzbqf = paramString1;
    this.zzbhg = paramString3;
    this.zzbqm = paramLong1;
    this.zzbqg = paramString4;
    this.zzbqh = paramLong2;
    this.zzbqi = paramLong3;
    this.zzbqj = paramString5;
    this.zzbqk = paramBoolean1;
    this.zzbql = paramBoolean2;
    this.zzbqn = paramString6;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzasr.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */