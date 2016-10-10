package com.google.android.gms.common.internal;

import android.util.Log;

public final class zzq
{
  public static final int CO = 23 - " PII_LOG".length();
  private static final String CP = null;
  private final String CQ;
  private final String CR;
  
  public zzq(String paramString)
  {
    this(paramString, null);
  }
  
  public zzq(String paramString1, String paramString2)
  {
    zzac.zzb(paramString1, "log tag cannot be null");
    if (paramString1.length() <= 23) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "tag \"%s\" is longer than the %d character maximum", new Object[] { paramString1, Integer.valueOf(23) });
      this.CQ = paramString1;
      if ((paramString2 != null) && (paramString2.length() > 0)) {
        break;
      }
      this.CR = null;
      return;
    }
    this.CR = paramString2;
  }
  
  private String zzhx(String paramString)
  {
    if (this.CR == null) {
      return paramString;
    }
    return this.CR.concat(paramString);
  }
  
  public void zzae(String paramString1, String paramString2)
  {
    if (zzgp(3)) {
      Log.d(paramString1, zzhx(paramString2));
    }
  }
  
  public void zzaf(String paramString1, String paramString2)
  {
    if (zzgp(5)) {
      Log.w(paramString1, zzhx(paramString2));
    }
  }
  
  public void zzag(String paramString1, String paramString2)
  {
    if (zzgp(6)) {
      Log.e(paramString1, zzhx(paramString2));
    }
  }
  
  public void zzb(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (zzgp(4)) {
      Log.i(paramString1, zzhx(paramString2), paramThrowable);
    }
  }
  
  public void zzc(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (zzgp(5)) {
      Log.w(paramString1, zzhx(paramString2), paramThrowable);
    }
  }
  
  public void zzd(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (zzgp(6)) {
      Log.e(paramString1, zzhx(paramString2), paramThrowable);
    }
  }
  
  public void zze(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (zzgp(7))
    {
      Log.e(paramString1, zzhx(paramString2), paramThrowable);
      Log.wtf(paramString1, zzhx(paramString2), paramThrowable);
    }
  }
  
  public boolean zzgp(int paramInt)
  {
    return Log.isLoggable(this.CQ, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */