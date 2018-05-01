package org.telegram.messenger.exoplayer2.drm;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.util.Util;

final class ClearKeyUtil
{
  private static final Pattern REQUEST_KIDS_PATTERN = Pattern.compile("\"kids\":\\[\"(.*?)\"]");
  private static final String TAG = "ClearKeyUtil";
  
  public static byte[] adjustRequestData(byte[] paramArrayOfByte)
  {
    if (Util.SDK_INT >= 27) {}
    for (;;)
    {
      return paramArrayOfByte;
      String str = Util.fromUtf8Bytes(paramArrayOfByte);
      Matcher localMatcher = REQUEST_KIDS_PATTERN.matcher(str);
      if (!localMatcher.find())
      {
        Log.e("ClearKeyUtil", "Failed to adjust request data: " + str);
      }
      else
      {
        int i = localMatcher.start(1);
        int j = localMatcher.end(1);
        paramArrayOfByte = new StringBuilder(str);
        base64ToBase64Url(paramArrayOfByte, i, j);
        paramArrayOfByte = Util.getUtf8Bytes(paramArrayOfByte.toString());
      }
    }
  }
  
  public static byte[] adjustResponseData(byte[] paramArrayOfByte)
  {
    if (Util.SDK_INT >= 27) {}
    for (;;)
    {
      return paramArrayOfByte;
      try
      {
        JSONObject localJSONObject1 = new org/json/JSONObject;
        localJSONObject1.<init>(Util.fromUtf8Bytes(paramArrayOfByte));
        Object localObject = localJSONObject1.getJSONArray("keys");
        for (int i = 0; i < ((JSONArray)localObject).length(); i++)
        {
          JSONObject localJSONObject2 = ((JSONArray)localObject).getJSONObject(i);
          localJSONObject2.put("k", base64UrlToBase64(localJSONObject2.getString("k")));
          localJSONObject2.put("kid", base64UrlToBase64(localJSONObject2.getString("kid")));
        }
        localObject = Util.getUtf8Bytes(localJSONObject1.toString());
        paramArrayOfByte = (byte[])localObject;
      }
      catch (JSONException localJSONException)
      {
        Log.e("ClearKeyUtil", "Failed to adjust response data: " + Util.fromUtf8Bytes(paramArrayOfByte), localJSONException);
      }
    }
  }
  
  private static void base64ToBase64Url(StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2)
    {
      switch (paramStringBuilder.charAt(paramInt1))
      {
      }
      for (;;)
      {
        paramInt1++;
        break;
        paramStringBuilder.setCharAt(paramInt1, '-');
        continue;
        paramStringBuilder.setCharAt(paramInt1, '_');
      }
    }
  }
  
  private static String base64UrlToBase64(String paramString)
  {
    return paramString.replace('-', '+').replace('_', '/');
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/ClearKeyUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */