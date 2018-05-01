package net.hockeyapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class LoginActivity
  extends Activity
{
  private Handler mLoginHandler;
  private LoginTask mLoginTask;
  private int mMode;
  private String mSecret;
  private String mUrl;
  
  private void configureView()
  {
    if (this.mMode == 1) {
      ((EditText)findViewById(R.id.input_password)).setVisibility(4);
    }
    TextView localTextView = (TextView)findViewById(R.id.text_headline);
    if (this.mMode == 1) {}
    for (int i = R.string.hockeyapp_login_headline_text_email_only;; i = R.string.hockeyapp_login_headline_text)
    {
      localTextView.setText(i);
      ((Button)findViewById(R.id.button_login)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          LoginActivity.this.performAuthentication();
        }
      });
      return;
    }
  }
  
  private void initLoginHandler()
  {
    this.mLoginHandler = new LoginHandler(this);
  }
  
  private void performAuthentication()
  {
    if (!Util.isConnectedToNetwork(this)) {
      Toast.makeText(this, R.string.hockeyapp_error_no_network_message, 1).show();
    }
    for (;;)
    {
      return;
      String str1 = ((EditText)findViewById(R.id.input_email)).getText().toString();
      String str2 = ((EditText)findViewById(R.id.input_password)).getText().toString();
      int i = 0;
      HashMap localHashMap = new HashMap();
      if (this.mMode == 1) {
        if (!TextUtils.isEmpty(str1))
        {
          i = 1;
          label81:
          localHashMap.put("email", str1);
          localHashMap.put("authcode", md5(this.mSecret + str1));
        }
      }
      for (;;)
      {
        if (i != 0)
        {
          this.mLoginTask = new LoginTask(this, this.mLoginHandler, this.mUrl, this.mMode, localHashMap);
          AsyncTaskUtils.execute(this.mLoginTask);
          break;
          i = 0;
          break label81;
          if (this.mMode == 2)
          {
            if ((!TextUtils.isEmpty(str1)) && (!TextUtils.isEmpty(str2))) {}
            for (i = 1;; i = 0)
            {
              localHashMap.put("email", str1);
              localHashMap.put("password", str2);
              break;
            }
          }
        }
      }
      Toast.makeText(this, getString(R.string.hockeyapp_login_missing_credentials_toast), 1).show();
    }
  }
  
  public String md5(String paramString)
  {
    try
    {
      paramString = Util.bytesToHex(Util.hash(paramString.getBytes(), "MD5"));
      return paramString;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      for (;;)
      {
        HockeyLog.error("Failed to create MD5 hash", paramString);
        paramString = "";
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.hockeyapp_activity_login);
    paramBundle = getIntent().getExtras();
    if (paramBundle != null)
    {
      this.mUrl = paramBundle.getString("url");
      this.mSecret = paramBundle.getString("secret");
      this.mMode = paramBundle.getInt("mode");
    }
    configureView();
    initLoginHandler();
    paramBundle = getLastNonConfigurationInstance();
    if (paramBundle != null)
    {
      this.mLoginTask = ((LoginTask)paramBundle);
      this.mLoginTask.attach(this, this.mLoginHandler);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4) {
      if (LoginManager.listener != null) {
        LoginManager.listener.onBack();
      }
    }
    for (boolean bool = true;; bool = super.onKeyDown(paramInt, paramKeyEvent)) {
      return bool;
    }
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    if (this.mLoginTask != null) {
      this.mLoginTask.detach();
    }
    return this.mLoginTask;
  }
  
  private static class LoginHandler
    extends Handler
  {
    private final WeakReference<Activity> mWeakActivity;
    
    LoginHandler(Activity paramActivity)
    {
      this.mWeakActivity = new WeakReference(paramActivity);
    }
    
    public void handleMessage(Message paramMessage)
    {
      Activity localActivity = (Activity)this.mWeakActivity.get();
      if (localActivity == null) {}
      for (;;)
      {
        return;
        if (paramMessage.getData().getBoolean("success"))
        {
          localActivity.finish();
          if (LoginManager.listener != null) {
            LoginManager.listener.onSuccess();
          }
        }
        else
        {
          Toast.makeText(localActivity, "Login failed. Check your credentials.", 1).show();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/LoginActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */