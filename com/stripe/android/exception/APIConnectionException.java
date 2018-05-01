package com.stripe.android.exception;

public class APIConnectionException
  extends StripeException
{
  public APIConnectionException(String paramString)
  {
    super(paramString, null, Integer.valueOf(0));
  }
  
  public APIConnectionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, null, Integer.valueOf(0), paramThrowable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/exception/APIConnectionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */