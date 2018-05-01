package com.stripe.android.net;

public class RequestOptions
{
  private final String mApiVersion;
  private final String mIdempotencyKey;
  private final String mPublishableApiKey;
  
  private RequestOptions(String paramString1, String paramString2, String paramString3)
  {
    this.mApiVersion = paramString1;
    this.mIdempotencyKey = paramString2;
    this.mPublishableApiKey = paramString3;
  }
  
  public static RequestOptionsBuilder builder(String paramString)
  {
    return new RequestOptionsBuilder(paramString);
  }
  
  public String getApiVersion()
  {
    return this.mApiVersion;
  }
  
  public String getIdempotencyKey()
  {
    return this.mIdempotencyKey;
  }
  
  public String getPublishableApiKey()
  {
    return this.mPublishableApiKey;
  }
  
  public static final class RequestOptionsBuilder
  {
    private String apiVersion;
    private String idempotencyKey;
    private String publishableApiKey;
    
    public RequestOptionsBuilder(String paramString)
    {
      this.publishableApiKey = paramString;
    }
    
    public RequestOptions build()
    {
      return new RequestOptions(this.apiVersion, this.idempotencyKey, this.publishableApiKey, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/RequestOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */