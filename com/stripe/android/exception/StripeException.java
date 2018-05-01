package com.stripe.android.exception;

public abstract class StripeException
  extends Exception
{
  private String requestId;
  private Integer statusCode;
  
  public StripeException(String paramString1, String paramString2, Integer paramInteger)
  {
    super(paramString1, null);
    this.requestId = paramString2;
    this.statusCode = paramInteger;
  }
  
  public StripeException(String paramString1, String paramString2, Integer paramInteger, Throwable paramThrowable)
  {
    super(paramString1, paramThrowable);
    this.statusCode = paramInteger;
    this.requestId = paramString2;
  }
  
  public String toString()
  {
    String str = "";
    if (this.requestId != null) {
      str = "; request-id: " + this.requestId;
    }
    return super.toString() + str;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/exception/StripeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */