package com.stripe.android.exception;

public class CardException
  extends StripeException
{
  private String charge;
  private String code;
  private String declineCode;
  private String param;
  
  public CardException(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, Integer paramInteger, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInteger, paramThrowable);
    this.code = paramString3;
    this.param = paramString4;
    this.declineCode = paramString5;
    this.charge = paramString6;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/exception/CardException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */