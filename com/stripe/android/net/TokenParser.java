package com.stripe.android.net;

import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.StripeJsonUtils;
import com.stripe.android.util.StripeTextUtils;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class TokenParser
{
  public static Token parseToken(String paramString)
    throws JSONException
  {
    Object localObject = new JSONObject(paramString);
    paramString = StripeJsonUtils.getString((JSONObject)localObject, "id");
    long l = ((JSONObject)localObject).getLong("created");
    boolean bool1 = ((JSONObject)localObject).getBoolean("livemode");
    String str = StripeTextUtils.asTokenType(StripeJsonUtils.getString((JSONObject)localObject, "type"));
    boolean bool2 = ((JSONObject)localObject).getBoolean("used");
    Card localCard = CardParser.parseCard(((JSONObject)localObject).getJSONObject("card"));
    localObject = new Date(Long.valueOf(l).longValue() * 1000L);
    return new Token(paramString, Boolean.valueOf(bool1).booleanValue(), (Date)localObject, Boolean.valueOf(bool2), localCard, str);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/TokenParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */