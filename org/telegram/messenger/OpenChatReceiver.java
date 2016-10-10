package org.telegram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.ui.LaunchActivity;

public class OpenChatReceiver
  extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    if (paramBundle == null) {
      finish();
    }
    if ((paramBundle.getAction() == null) || (!paramBundle.getAction().startsWith("com.tmessages.openchat")))
    {
      finish();
      return;
    }
    Intent localIntent = new Intent(this, LaunchActivity.class);
    localIntent.setAction(paramBundle.getAction());
    localIntent.putExtras(paramBundle);
    startActivity(localIntent);
    finish();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/OpenChatReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */