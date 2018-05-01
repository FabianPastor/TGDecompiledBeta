package com.stripe.android.net;

import com.stripe.android.util.StripeJsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

class ErrorParser
{
  static StripeError parseError(String paramString)
  {
    StripeError localStripeError = new StripeError();
    try
    {
      JSONObject localJSONObject = new org/json/JSONObject;
      localJSONObject.<init>(paramString);
      paramString = localJSONObject.getJSONObject("error");
      localStripeError.charge = StripeJsonUtils.optString(paramString, "charge");
      localStripeError.code = StripeJsonUtils.optString(paramString, "code");
      localStripeError.decline_code = StripeJsonUtils.optString(paramString, "decline_code");
      localStripeError.message = StripeJsonUtils.optString(paramString, "message");
      localStripeError.param = StripeJsonUtils.optString(paramString, "param");
      localStripeError.type = StripeJsonUtils.optString(paramString, "type");
      return localStripeError;
    }
    catch (JSONException paramString)
    {
      for (;;)
      {
        localStripeError.message = "An improperly formatted error response was found.";
      }
    }
  }
  
  static class StripeError
  {
    public String charge;
    public String code;
    public String decline_code;
    public String message;
    public String param;
    public String type;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/ErrorParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */