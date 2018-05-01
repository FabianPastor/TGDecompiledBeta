package com.google.android.gms.common.util;

import java.util.regex.Pattern;

public class Strings
{
  private static final Pattern zzaak = Pattern.compile("\\$\\{(.*?)\\}");
  
  public static boolean isEmptyOrWhitespace(String paramString)
  {
    if ((paramString == null) || (paramString.trim().isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/Strings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */