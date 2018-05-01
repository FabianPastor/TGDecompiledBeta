package com.stripe.android.util;

import org.json.JSONException;
import org.json.JSONObject;

public class StripeJsonUtils
{
  public static String getString(JSONObject paramJSONObject, String paramString)
    throws JSONException
  {
    return nullIfNullOrEmpty(paramJSONObject.getString(paramString));
  }
  
  static String nullIfNullOrEmpty(String paramString)
  {
    String str;
    if (!"null".equals(paramString))
    {
      str = paramString;
      if (!"".equals(paramString)) {}
    }
    else
    {
      str = null;
    }
    return str;
  }
  
  public static String optString(JSONObject paramJSONObject, String paramString)
  {
    return nullIfNullOrEmpty(paramJSONObject.optString(paramString));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/util/StripeJsonUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */