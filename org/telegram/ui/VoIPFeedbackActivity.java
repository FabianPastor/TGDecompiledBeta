package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPFeedbackActivity
  extends Activity
{
  public void finish()
  {
    super.finish();
    overridePendingTransition(0, 0);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    getWindow().addFlags(524288);
    super.onCreate(paramBundle);
    overridePendingTransition(0, 0);
    setContentView(new View(this));
    VoIPHelper.showRateAlert(this, new Runnable()
    {
      public void run()
      {
        VoIPFeedbackActivity.this.finish();
      }
    }, getIntent().getLongExtra("call_id", 0L), getIntent().getLongExtra("call_access_hash", 0L), getIntent().getIntExtra("account", 0));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/VoIPFeedbackActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */