package com.google.android.gms.common.util;

import android.text.TextUtils;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class zzo
{
  private static final Pattern zzaJU = Pattern.compile("\\\\.");
  private static final Pattern zzaJV = Pattern.compile("[\\\\\"/\b\f\n\r\t]");
  
  public static boolean zzc(Object paramObject1, Object paramObject2)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramObject1 == null) && (paramObject2 == null)) {
      bool1 = true;
    }
    for (;;)
    {
      return bool1;
      bool1 = bool2;
      Iterator localIterator;
      if (paramObject1 != null)
      {
        bool1 = bool2;
        if (paramObject2 != null) {
          if (((paramObject1 instanceof JSONObject)) && ((paramObject2 instanceof JSONObject)))
          {
            paramObject1 = (JSONObject)paramObject1;
            paramObject2 = (JSONObject)paramObject2;
            bool1 = bool2;
            if (((JSONObject)paramObject1).length() != ((JSONObject)paramObject2).length()) {
              continue;
            }
            localIterator = ((JSONObject)paramObject1).keys();
          }
        }
      }
      for (;;)
      {
        String str;
        if (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          bool1 = bool2;
          if (!((JSONObject)paramObject2).has(str)) {
            break;
          }
        }
        int i;
        boolean bool3;
        try
        {
          bool1 = zzc(((JSONObject)paramObject1).get(str), ((JSONObject)paramObject2).get(str));
          if (!bool1) {
            return false;
          }
        }
        catch (JSONException paramObject1) {}
      }
      return true;
      if ((!(paramObject1 instanceof JSONArray)) || (!(paramObject2 instanceof JSONArray))) {
        break label211;
      }
      paramObject1 = (JSONArray)paramObject1;
      paramObject2 = (JSONArray)paramObject2;
      bool1 = bool2;
      if (((JSONArray)paramObject1).length() == ((JSONArray)paramObject2).length())
      {
        i = 0;
        if (i >= ((JSONArray)paramObject1).length()) {}
      }
      try
      {
        bool3 = zzc(((JSONArray)paramObject1).get(i), ((JSONArray)paramObject2).get(i));
        bool1 = bool2;
        if (bool3) {
          i += 1;
        }
      }
      catch (JSONException paramObject1)
      {
        label211:
        return false;
      }
    }
    return true;
    return paramObject1.equals(paramObject2);
    return false;
  }
  
  public static String zzcK(String paramString)
  {
    Matcher localMatcher;
    Object localObject1;
    if (!TextUtils.isEmpty(paramString))
    {
      localMatcher = zzaJV.matcher(paramString);
      localObject1 = null;
      while (localMatcher.find())
      {
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new StringBuffer();
        }
        switch (localMatcher.group().charAt(0))
        {
        default: 
          localObject1 = localObject2;
          break;
        case '\b': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\b");
          localObject1 = localObject2;
          break;
        case '"': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\\\\"");
          localObject1 = localObject2;
          break;
        case '\\': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\\\\\");
          localObject1 = localObject2;
          break;
        case '/': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\/");
          localObject1 = localObject2;
          break;
        case '\f': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\f");
          localObject1 = localObject2;
          break;
        case '\n': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\n");
          localObject1 = localObject2;
          break;
        case '\r': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\r");
          localObject1 = localObject2;
          break;
        case '\t': 
          localMatcher.appendReplacement((StringBuffer)localObject2, "\\\\t");
          localObject1 = localObject2;
        }
      }
      if (localObject1 != null) {}
    }
    else
    {
      return paramString;
    }
    localMatcher.appendTail((StringBuffer)localObject1);
    return ((StringBuffer)localObject1).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */