package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.internal.zzaj;

public final class zzbgb
{
  private final String mTag;
  private final zzaj zzaIA;
  private final String zzaIc;
  private final int zzagX;
  
  private zzbgb(String paramString1, String paramString2)
  {
    this.zzaIc = paramString2;
    this.mTag = paramString1;
    this.zzaIA = new zzaj(paramString1);
    this.zzagX = getLogLevel();
  }
  
  public zzbgb(String paramString, String... paramVarArgs)
  {
    this(paramString, zzb(paramVarArgs));
  }
  
  private final String format(String paramString, @Nullable Object... paramVarArgs)
  {
    String str = paramString;
    if (paramVarArgs != null)
    {
      str = paramString;
      if (paramVarArgs.length > 0) {
        str = String.format(paramString, paramVarArgs);
      }
    }
    return this.zzaIc.concat(str);
  }
  
  private final int getLogLevel()
  {
    int i = 2;
    while ((7 >= i) && (!Log.isLoggable(this.mTag, i))) {
      i += 1;
    }
    return i;
  }
  
  private static String zzb(String... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      String str = paramVarArgs[i];
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(str);
      i += 1;
    }
    localStringBuilder.append(']').append(' ');
    return localStringBuilder.toString();
  }
  
  private final boolean zzz(int paramInt)
  {
    return this.zzagX <= paramInt;
  }
  
  public final void zza(String paramString, Throwable paramThrowable, @Nullable Object... paramVarArgs)
  {
    Log.wtf(this.mTag, format(paramString, paramVarArgs), paramThrowable);
  }
  
  public final void zza(String paramString, @Nullable Object... paramVarArgs)
  {
    if (zzz(2)) {
      Log.v(this.mTag, format(paramString, paramVarArgs));
    }
  }
  
  public final void zzb(String paramString, @Nullable Object... paramVarArgs)
  {
    if (zzz(3)) {
      Log.d(this.mTag, format(paramString, paramVarArgs));
    }
  }
  
  public final void zzd(Throwable paramThrowable)
  {
    Log.wtf(this.mTag, paramThrowable);
  }
  
  public final void zze(String paramString, @Nullable Object... paramVarArgs)
  {
    Log.i(this.mTag, format(paramString, paramVarArgs));
  }
  
  public final void zzf(String paramString, @Nullable Object... paramVarArgs)
  {
    Log.w(this.mTag, format(paramString, paramVarArgs));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */