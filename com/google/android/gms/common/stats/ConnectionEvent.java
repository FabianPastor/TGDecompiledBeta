package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class ConnectionEvent
  extends StatsEvent
{
  public static final Parcelable.Creator<ConnectionEvent> CREATOR = new zza();
  private final long DM;
  private int DN;
  private final String DO;
  private final String DP;
  private final String DQ;
  private final String DR;
  private final String DS;
  private final String DT;
  private final long DU;
  private final long DV;
  private long DW;
  final int mVersionCode;
  
  ConnectionEvent(int paramInt1, long paramLong1, int paramInt2, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, long paramLong2, long paramLong3)
  {
    this.mVersionCode = paramInt1;
    this.DM = paramLong1;
    this.DN = paramInt2;
    this.DO = paramString1;
    this.DP = paramString2;
    this.DQ = paramString3;
    this.DR = paramString4;
    this.DW = -1L;
    this.DS = paramString5;
    this.DT = paramString6;
    this.DU = paramLong2;
    this.DV = paramLong3;
  }
  
  public ConnectionEvent(long paramLong1, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, long paramLong2, long paramLong3)
  {
    this(1, paramLong1, paramInt, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramLong2, paramLong3);
  }
  
  public int getEventType()
  {
    return this.DN;
  }
  
  public long getTimeMillis()
  {
    return this.DM;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  public String zzawk()
  {
    return this.DO;
  }
  
  public String zzawl()
  {
    return this.DP;
  }
  
  public String zzawm()
  {
    return this.DQ;
  }
  
  public String zzawn()
  {
    return this.DR;
  }
  
  public String zzawo()
  {
    return this.DS;
  }
  
  public String zzawp()
  {
    return this.DT;
  }
  
  public long zzawq()
  {
    return this.DW;
  }
  
  public long zzawr()
  {
    return this.DV;
  }
  
  public long zzaws()
  {
    return this.DU;
  }
  
  public String zzawt()
  {
    String str2 = String.valueOf("\t");
    String str3 = String.valueOf(zzawk());
    String str4 = String.valueOf(zzawl());
    String str5 = String.valueOf("\t");
    String str6 = String.valueOf(zzawm());
    String str7 = String.valueOf(zzawn());
    String str8 = String.valueOf("\t");
    if (this.DS == null) {}
    for (String str1 = "";; str1 = this.DS)
    {
      String str9 = String.valueOf("\t");
      long l = zzawr();
      return String.valueOf(str2).length() + 22 + String.valueOf(str3).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + String.valueOf(str6).length() + String.valueOf(str7).length() + String.valueOf(str8).length() + String.valueOf(str1).length() + String.valueOf(str9).length() + str2 + str3 + "/" + str4 + str5 + str6 + "/" + str7 + str8 + str1 + str9 + l;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/ConnectionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */