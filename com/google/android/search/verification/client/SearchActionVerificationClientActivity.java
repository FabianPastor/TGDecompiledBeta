package com.google.android.search.verification.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class SearchActionVerificationClientActivity
  extends Activity
{
  public abstract Class<? extends SearchActionVerificationClientService> getServiceClass();
  
  protected final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = new Intent(this, getServiceClass());
    paramBundle.putExtra("SearchActionVerificationClientExtraIntent", getIntent());
    startService(paramBundle);
    finish();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/search/verification/client/SearchActionVerificationClientActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */