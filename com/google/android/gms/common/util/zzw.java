package com.google.android.gms.common.util;

import com.google.android.gms.common.internal.zzg;
import java.util.regex.Pattern;

public class zzw
{
  private static final Pattern EZ = Pattern.compile("\\$\\{(.*?)\\}");
  
  public static boolean zzij(String paramString)
  {
    return (paramString == null) || (zzg.BB.zzb(paramString));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */