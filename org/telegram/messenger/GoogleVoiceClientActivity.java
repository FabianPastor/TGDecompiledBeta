package org.telegram.messenger;

import com.google.android.search.verification.client.SearchActionVerificationClientActivity;
import com.google.android.search.verification.client.SearchActionVerificationClientService;

public class GoogleVoiceClientActivity
  extends SearchActionVerificationClientActivity
{
  public Class<? extends SearchActionVerificationClientService> getServiceClass()
  {
    return GoogleVoiceClientService.class;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GoogleVoiceClientActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */