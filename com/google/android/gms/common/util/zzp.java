package com.google.android.gms.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public final class zzp
{
  public static void zza(StringBuilder paramStringBuilder, HashMap<String, String> paramHashMap)
  {
    paramStringBuilder.append("{");
    Iterator localIterator = paramHashMap.keySet().iterator();
    int i = 1;
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (i == 0) {
        paramStringBuilder.append(",");
      }
      String str2;
      for (;;)
      {
        str2 = (String)paramHashMap.get(str1);
        paramStringBuilder.append("\"").append(str1).append("\":");
        if (str2 != null) {
          break label98;
        }
        paramStringBuilder.append("null");
        break;
        i = 0;
      }
      label98:
      paramStringBuilder.append("\"").append(str2).append("\"");
    }
    paramStringBuilder.append("}");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */