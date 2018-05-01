package com.google.android.gms.playlog.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzz;

public class PlayLoggerContext
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<PlayLoggerContext> CREATOR = new zza();
  public final int aAB;
  public final int aAC;
  public final String aAD;
  public final String aAE;
  public final boolean aAF;
  public final String aAG;
  public final boolean aAH;
  public final int aAI;
  public final String packageName;
  public final int versionCode;
  
  public PlayLoggerContext(int paramInt1, String paramString1, int paramInt2, int paramInt3, String paramString2, String paramString3, boolean paramBoolean1, String paramString4, boolean paramBoolean2, int paramInt4)
  {
    this.versionCode = paramInt1;
    this.packageName = paramString1;
    this.aAB = paramInt2;
    this.aAC = paramInt3;
    this.aAD = paramString2;
    this.aAE = paramString3;
    this.aAF = paramBoolean1;
    this.aAG = paramString4;
    this.aAH = paramBoolean2;
    this.aAI = paramInt4;
  }
  
  public PlayLoggerContext(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, String paramString4, boolean paramBoolean, int paramInt3)
  {
    this.versionCode = 1;
    this.packageName = ((String)zzaa.zzy(paramString1));
    this.aAB = paramInt1;
    this.aAC = paramInt2;
    this.aAG = paramString2;
    this.aAD = paramString3;
    this.aAE = paramString4;
    if (!paramBoolean) {}
    for (boolean bool = true;; bool = false)
    {
      this.aAF = bool;
      this.aAH = paramBoolean;
      this.aAI = paramInt3;
      return;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof PlayLoggerContext)) {
        break;
      }
      paramObject = (PlayLoggerContext)paramObject;
    } while ((this.versionCode == ((PlayLoggerContext)paramObject).versionCode) && (this.packageName.equals(((PlayLoggerContext)paramObject).packageName)) && (this.aAB == ((PlayLoggerContext)paramObject).aAB) && (this.aAC == ((PlayLoggerContext)paramObject).aAC) && (zzz.equal(this.aAG, ((PlayLoggerContext)paramObject).aAG)) && (zzz.equal(this.aAD, ((PlayLoggerContext)paramObject).aAD)) && (zzz.equal(this.aAE, ((PlayLoggerContext)paramObject).aAE)) && (this.aAF == ((PlayLoggerContext)paramObject).aAF) && (this.aAH == ((PlayLoggerContext)paramObject).aAH) && (this.aAI == ((PlayLoggerContext)paramObject).aAI));
    return false;
    return false;
  }
  
  public int hashCode()
  {
    return zzz.hashCode(new Object[] { Integer.valueOf(this.versionCode), this.packageName, Integer.valueOf(this.aAB), Integer.valueOf(this.aAC), this.aAG, this.aAD, this.aAE, Boolean.valueOf(this.aAF), Boolean.valueOf(this.aAH), Integer.valueOf(this.aAI) });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PlayLoggerContext[");
    localStringBuilder.append("versionCode=").append(this.versionCode).append(',');
    localStringBuilder.append("package=").append(this.packageName).append(',');
    localStringBuilder.append("packageVersionCode=").append(this.aAB).append(',');
    localStringBuilder.append("logSource=").append(this.aAC).append(',');
    localStringBuilder.append("logSourceName=").append(this.aAG).append(',');
    localStringBuilder.append("uploadAccount=").append(this.aAD).append(',');
    localStringBuilder.append("loggingId=").append(this.aAE).append(',');
    localStringBuilder.append("logAndroidId=").append(this.aAF).append(',');
    localStringBuilder.append("isAnonymous=").append(this.aAH).append(',');
    localStringBuilder.append("qosTier=").append(this.aAI);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/playlog/internal/PlayLoggerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */