package com.google.android.gms.signin;

import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.signin.internal.ISignInCallbacks;

public abstract interface SignInClient
  extends Api.Client
{
  public abstract void clearAccountFromSessionStore();
  
  public abstract void connect();
  
  public abstract void saveDefaultAccount(IAccountAccessor paramIAccountAccessor, boolean paramBoolean);
  
  public abstract void signIn(ISignInCallbacks paramISignInCallbacks);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/SignInClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */