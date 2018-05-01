package com.stripe.android;

import com.stripe.android.model.Token;

public abstract interface TokenCallback
{
  public abstract void onError(Exception paramException);
  
  public abstract void onSuccess(Token paramToken);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/TokenCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */