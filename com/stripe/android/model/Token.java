package com.stripe.android.model;

import java.util.Date;

public class Token
{
  private final Card mCard;
  private final Date mCreated;
  private final String mId;
  private final boolean mLivemode;
  private final String mType;
  private final boolean mUsed;
  
  public Token(String paramString1, boolean paramBoolean, Date paramDate, Boolean paramBoolean1, Card paramCard, String paramString2)
  {
    this.mId = paramString1;
    this.mType = paramString2;
    this.mCreated = paramDate;
    this.mLivemode = paramBoolean;
    this.mCard = paramCard;
    this.mUsed = paramBoolean1.booleanValue();
  }
  
  public Card getCard()
  {
    return this.mCard;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public String getType()
  {
    return this.mType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/model/Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */