package com.stripe.android.net;

import java.util.List;
import java.util.Map;

public class StripeResponse
{
  private String mResponseBody;
  private int mResponseCode;
  private Map<String, List<String>> mResponseHeaders;
  
  public StripeResponse(int paramInt, String paramString, Map<String, List<String>> paramMap)
  {
    this.mResponseCode = paramInt;
    this.mResponseBody = paramString;
    this.mResponseHeaders = paramMap;
  }
  
  public String getResponseBody()
  {
    return this.mResponseBody;
  }
  
  public int getResponseCode()
  {
    return this.mResponseCode;
  }
  
  public Map<String, List<String>> getResponseHeaders()
  {
    return this.mResponseHeaders;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/StripeResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */