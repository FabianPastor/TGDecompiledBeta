package com.stripe.android;

import android.os.AsyncTask;
import android.os.Build.VERSION;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.StripeException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.RequestOptions;
import com.stripe.android.net.RequestOptions.RequestOptionsBuilder;
import com.stripe.android.net.StripeApiHandler;
import com.stripe.android.util.StripeNetworkUtils;
import java.util.concurrent.Executor;

public class Stripe
{
  private String defaultPublishableKey;
  TokenCreator tokenCreator = new TokenCreator()
  {
    public void create(final Card paramAnonymousCard, final String paramAnonymousString, Executor paramAnonymousExecutor, final TokenCallback paramAnonymousTokenCallback)
    {
      paramAnonymousCard = new AsyncTask()
      {
        protected Stripe.ResponseWrapper doInBackground(Void... paramAnonymous2VarArgs)
        {
          try
          {
            paramAnonymous2VarArgs = RequestOptions.builder(paramAnonymousString).build();
            Token localToken = StripeApiHandler.createToken(StripeNetworkUtils.hashMapFromCard(paramAnonymousCard), paramAnonymous2VarArgs);
            paramAnonymous2VarArgs = new com/stripe/android/Stripe$ResponseWrapper;
            paramAnonymous2VarArgs.<init>(Stripe.this, localToken, null, null);
            return paramAnonymous2VarArgs;
          }
          catch (StripeException paramAnonymous2VarArgs)
          {
            for (;;)
            {
              paramAnonymous2VarArgs = new Stripe.ResponseWrapper(Stripe.this, null, paramAnonymous2VarArgs, null);
            }
          }
        }
        
        protected void onPostExecute(Stripe.ResponseWrapper paramAnonymous2ResponseWrapper)
        {
          Stripe.this.tokenTaskPostExecution(paramAnonymous2ResponseWrapper, paramAnonymousTokenCallback);
        }
      };
      Stripe.this.executeTokenTask(paramAnonymousExecutor, paramAnonymousCard);
    }
  };
  
  public Stripe() {}
  
  public Stripe(String paramString)
    throws AuthenticationException
  {
    setDefaultPublishableKey(paramString);
  }
  
  private void executeTokenTask(Executor paramExecutor, AsyncTask<Void, Void, ResponseWrapper> paramAsyncTask)
  {
    if ((paramExecutor != null) && (Build.VERSION.SDK_INT > 11)) {
      paramAsyncTask.executeOnExecutor(paramExecutor, new Void[0]);
    }
    for (;;)
    {
      return;
      paramAsyncTask.execute(new Void[0]);
    }
  }
  
  private void tokenTaskPostExecution(ResponseWrapper paramResponseWrapper, TokenCallback paramTokenCallback)
  {
    if (paramResponseWrapper.token != null) {
      paramTokenCallback.onSuccess(paramResponseWrapper.token);
    }
    for (;;)
    {
      return;
      if (paramResponseWrapper.error != null) {
        paramTokenCallback.onError(paramResponseWrapper.error);
      } else {
        paramTokenCallback.onError(new RuntimeException("Somehow got neither a token response or an error response"));
      }
    }
  }
  
  private void validateKey(String paramString)
    throws AuthenticationException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new AuthenticationException("Invalid Publishable Key: You must use a valid publishable key to create a token.  For more info, see https://stripe.com/docs/stripe.js.", null, Integer.valueOf(0));
    }
    if (paramString.startsWith("sk_")) {
      throw new AuthenticationException("Invalid Publishable Key: You are using a secret key to create a token, instead of the publishable one. For more info, see https://stripe.com/docs/stripe.js", null, Integer.valueOf(0));
    }
  }
  
  public void createToken(Card paramCard, TokenCallback paramTokenCallback)
  {
    createToken(paramCard, this.defaultPublishableKey, paramTokenCallback);
  }
  
  public void createToken(Card paramCard, String paramString, TokenCallback paramTokenCallback)
  {
    createToken(paramCard, paramString, null, paramTokenCallback);
  }
  
  public void createToken(Card paramCard, String paramString, Executor paramExecutor, TokenCallback paramTokenCallback)
  {
    if (paramCard == null) {
      try
      {
        paramCard = new java/lang/RuntimeException;
        paramCard.<init>("Required Parameter: 'card' is required to create a token");
        throw paramCard;
      }
      catch (AuthenticationException paramCard)
      {
        paramTokenCallback.onError(paramCard);
      }
    }
    for (;;)
    {
      return;
      if (paramTokenCallback == null)
      {
        paramCard = new java/lang/RuntimeException;
        paramCard.<init>("Required Parameter: 'callback' is required to use the created token and handle errors");
        throw paramCard;
      }
      validateKey(paramString);
      this.tokenCreator.create(paramCard, paramString, paramExecutor, paramTokenCallback);
    }
  }
  
  public void setDefaultPublishableKey(String paramString)
    throws AuthenticationException
  {
    validateKey(paramString);
    this.defaultPublishableKey = paramString;
  }
  
  private class ResponseWrapper
  {
    final Exception error;
    final Token token;
    
    private ResponseWrapper(Token paramToken, Exception paramException)
    {
      this.error = paramException;
      this.token = paramToken;
    }
  }
  
  static abstract interface TokenCreator
  {
    public abstract void create(Card paramCard, String paramString, Executor paramExecutor, TokenCallback paramTokenCallback);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/Stripe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */