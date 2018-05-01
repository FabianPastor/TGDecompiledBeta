package com.google.firebase.iid;

import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

final class zzab
{
  private static final long zzbsa = TimeUnit.DAYS.toMillis(7L);
  private final long timestamp;
  final String zzbsb;
  private final String zztc;
  
  private zzab(String paramString1, String paramString2, long paramLong)
  {
    this.zzbsb = paramString1;
    this.zztc = paramString2;
    this.timestamp = paramLong;
  }
  
  static String zza(String paramString1, String paramString2, long paramLong)
  {
    try
    {
      JSONObject localJSONObject = new org/json/JSONObject;
      localJSONObject.<init>();
      localJSONObject.put("token", paramString1);
      localJSONObject.put("appVersion", paramString2);
      localJSONObject.put("timestamp", paramLong);
      paramString1 = localJSONObject.toString();
      return paramString1;
    }
    catch (JSONException paramString1)
    {
      for (;;)
      {
        paramString1 = String.valueOf(paramString1);
        Log.w("FirebaseInstanceId", String.valueOf(paramString1).length() + 24 + "Failed to encode token: " + paramString1);
        paramString1 = null;
      }
    }
  }
  
  static zzab zzfe(String paramString)
  {
    Object localObject = null;
    if (TextUtils.isEmpty(paramString)) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      if (paramString.startsWith("{")) {
        try
        {
          JSONObject localJSONObject = new org/json/JSONObject;
          localJSONObject.<init>(paramString);
          paramString = new com/google/firebase/iid/zzab;
          paramString.<init>(localJSONObject.getString("token"), localJSONObject.getString("appVersion"), localJSONObject.getLong("timestamp"));
        }
        catch (JSONException paramString)
        {
          paramString = String.valueOf(paramString);
          Log.w("FirebaseInstanceId", String.valueOf(paramString).length() + 23 + "Failed to parse token: " + paramString);
          paramString = (String)localObject;
        }
      } else {
        paramString = new zzab(paramString, null, 0L);
      }
    }
  }
  
  final boolean zzff(String paramString)
  {
    if ((System.currentTimeMillis() > this.timestamp + zzbsa) || (!paramString.equals(this.zztc))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */