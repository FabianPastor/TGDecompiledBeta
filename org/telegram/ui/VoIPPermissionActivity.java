package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity
  extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      if ((paramArrayOfInt.length <= 0) || (paramArrayOfInt[0] != 0)) {
        break label48;
      }
      if (VoIPService.getSharedInstance() != null) {
        VoIPService.getSharedInstance().acceptIncomingCall();
      }
      finish();
      startActivity(new Intent(this, VoIPActivity.class));
    }
    for (;;)
    {
      return;
      label48:
      if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO"))
      {
        if (VoIPService.getSharedInstance() != null) {
          VoIPService.getSharedInstance().declineIncomingCall();
        }
        VoIPHelper.permissionDenied(this, new Runnable()
        {
          public void run()
          {
            VoIPPermissionActivity.this.finish();
          }
        });
      }
      else
      {
        finish();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/VoIPPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */