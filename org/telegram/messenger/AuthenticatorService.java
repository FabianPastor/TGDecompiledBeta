package org.telegram.messenger;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class AuthenticatorService
  extends Service
{
  private static Authenticator authenticator = null;
  
  protected Authenticator getAuthenticator()
  {
    if (authenticator == null) {
      authenticator = new Authenticator(this);
    }
    return authenticator;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.accounts.AccountAuthenticator")) {}
    for (paramIntent = getAuthenticator().getIBinder();; paramIntent = null) {
      return paramIntent;
    }
  }
  
  private static class Authenticator
    extends AbstractAccountAuthenticator
  {
    private final Context context;
    
    public Authenticator(Context paramContext)
    {
      super();
      this.context = paramContext;
    }
    
    public Bundle addAccount(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
      throws NetworkErrorException
    {
      return null;
    }
    
    public Bundle confirmCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
      throws NetworkErrorException
    {
      return null;
    }
    
    public Bundle editProperties(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString)
    {
      return null;
    }
    
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount)
      throws NetworkErrorException
    {
      return super.getAccountRemovalAllowed(paramAccountAuthenticatorResponse, paramAccount);
    }
    
    public Bundle getAuthToken(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
      throws NetworkErrorException
    {
      return null;
    }
    
    public String getAuthTokenLabel(String paramString)
    {
      return null;
    }
    
    public Bundle hasFeatures(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
      throws NetworkErrorException
    {
      return null;
    }
    
    public Bundle updateCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
      throws NetworkErrorException
    {
      return null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/AuthenticatorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */