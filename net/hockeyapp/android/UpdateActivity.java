package net.hockeyapp.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class UpdateActivity
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      paramBundle = getIntent().getExtras();
      if (paramBundle == null) {
        finish();
      }
    }
    for (;;)
    {
      return;
      paramBundle = Fragment.instantiate(this, paramBundle.getString("fragmentClass", UpdateFragment.class.getName()), paramBundle);
      getFragmentManager().beginTransaction().add(16908290, paramBundle, "hockey_update_dialog").commit();
      setTitle(R.string.hockeyapp_update_title);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */