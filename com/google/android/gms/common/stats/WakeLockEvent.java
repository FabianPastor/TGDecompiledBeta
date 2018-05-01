package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;

public final class WakeLockEvent
  extends StatsEvent
{
  public static final Parcelable.Creator<WakeLockEvent> CREATOR = new zzd();
  private final long mTimeout;
  private final long zzaJn;
  private int zzaJo;
  private final String zzaJp;
  private final String zzaJq;
  private final String zzaJr;
  private final int zzaJs;
  private final List<String> zzaJt;
  private final String zzaJu;
  private final long zzaJv;
  private int zzaJw;
  private final String zzaJx;
  private final float zzaJy;
  private long zzaJz;
  private int zzaku;
  
  WakeLockEvent(int paramInt1, long paramLong1, int paramInt2, String paramString1, int paramInt3, List<String> paramList, String paramString2, long paramLong2, int paramInt4, String paramString3, String paramString4, float paramFloat, long paramLong3, String paramString5)
  {
    this.zzaku = paramInt1;
    this.zzaJn = paramLong1;
    this.zzaJo = paramInt2;
    this.zzaJp = paramString1;
    this.zzaJq = paramString3;
    this.zzaJr = paramString5;
    this.zzaJs = paramInt3;
    this.zzaJz = -1L;
    this.zzaJt = paramList;
    this.zzaJu = paramString2;
    this.zzaJv = paramLong2;
    this.zzaJw = paramInt4;
    this.zzaJx = paramString4;
    this.zzaJy = paramFloat;
    this.mTimeout = paramLong3;
  }
  
  public WakeLockEvent(long paramLong1, int paramInt1, String paramString1, int paramInt2, List<String> paramList, String paramString2, long paramLong2, int paramInt3, String paramString3, String paramString4, float paramFloat, long paramLong3, String paramString5)
  {
    this(2, paramLong1, paramInt1, paramString1, paramInt2, paramList, paramString2, paramLong2, paramInt3, paramString3, paramString4, paramFloat, paramLong3, paramString5);
  }
  
  public final int getEventType()
  {
    return this.zzaJo;
  }
  
  public final long getTimeMillis()
  {
    return this.zzaJn;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 1, this.zzaku);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.zzaJn);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.zzaJp, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 5, this.zzaJs);
    com.google.android.gms.common.internal.safeparcel.zzd.zzb(paramParcel, 6, this.zzaJt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.zzaJv);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 10, this.zzaJq, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 11, this.zzaJo);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 12, this.zzaJu, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 13, this.zzaJx, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 14, this.zzaJw);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 15, this.zzaJy);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 16, this.mTimeout);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 17, this.zzaJr, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
  }
  
  public final long zzrV()
  {
    return this.zzaJz;
  }
  
  public final String zzrW()
  {
    String str5 = String.valueOf("\t");
    String str6 = String.valueOf(this.zzaJp);
    String str7 = String.valueOf("\t");
    int i = this.zzaJs;
    String str8 = String.valueOf("\t");
    String str1;
    String str9;
    int j;
    String str10;
    String str2;
    label76:
    String str11;
    String str3;
    label94:
    String str12;
    float f;
    String str13;
    if (this.zzaJt == null)
    {
      str1 = "";
      str9 = String.valueOf("\t");
      j = this.zzaJw;
      str10 = String.valueOf("\t");
      if (this.zzaJq != null) {
        break label345;
      }
      str2 = "";
      str11 = String.valueOf("\t");
      if (this.zzaJx != null) {
        break label354;
      }
      str3 = "";
      str12 = String.valueOf("\t");
      f = this.zzaJy;
      str13 = String.valueOf("\t");
      if (this.zzaJr != null) {
        break label363;
      }
    }
    label345:
    label354:
    label363:
    for (String str4 = "";; str4 = this.zzaJr)
    {
      return String.valueOf(str5).length() + 37 + String.valueOf(str6).length() + String.valueOf(str7).length() + String.valueOf(str8).length() + String.valueOf(str1).length() + String.valueOf(str9).length() + String.valueOf(str10).length() + String.valueOf(str2).length() + String.valueOf(str11).length() + String.valueOf(str3).length() + String.valueOf(str12).length() + String.valueOf(str13).length() + String.valueOf(str4).length() + str5 + str6 + str7 + i + str8 + str1 + str9 + j + str10 + str2 + str11 + str3 + str12 + f + str13 + str4;
      str1 = TextUtils.join(",", this.zzaJt);
      break;
      str2 = this.zzaJq;
      break label76;
      str3 = this.zzaJx;
      break label94;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/WakeLockEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */