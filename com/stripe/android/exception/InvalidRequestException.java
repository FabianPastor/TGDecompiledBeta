package com.stripe.android.exception;

public class InvalidRequestException
  extends StripeException
{
  private final String param;
  
  public InvalidRequestException(String paramString1, String paramString2, String paramString3, Integer paramInteger, Throwable paramThrowable)
  {
    super(paramString1, paramString3, paramInteger, paramThrowable);
    this.param = paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/exception/InvalidRequestException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */