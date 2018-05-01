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
    Intent localIntent = getIntent();
    if (localIntent == null) {
      finish();
    }
    if ((localIntent.getAction() == null) || (!localIntent.getAction().startsWith("com.tmessages.openchat"))) {
      finish();
    }
    for (;;)
    {
      return;
      paramBundle = new Intent(this, LaunchActivity.class);
      paramBundle.setAction(localIntent.getAction());
      paramBundle.putExtras(localIntent);
      startActivity(paramBundle);
      finish();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/OpenChatReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */