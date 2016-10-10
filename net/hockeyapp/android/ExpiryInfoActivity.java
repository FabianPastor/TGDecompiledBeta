package net.hockeyapp.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import net.hockeyapp.android.utils.Util;

public class ExpiryInfoActivity
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle(getString(R.string.hockeyapp_expiry_info_title));
    setContentView(R.layout.hockeyapp_activity_expiry_info);
    paramBundle = Util.getAppName(this);
    paramBundle = String.format(getString(R.string.hockeyapp_expiry_info_text), new Object[] { paramBundle });
    ((TextView)findViewById(R.id.label_message)).setText(paramBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/ExpiryInfoActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */