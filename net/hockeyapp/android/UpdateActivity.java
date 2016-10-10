package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;

public class UpdateActivity
  extends Activity
  implements UpdateActivityInterface, UpdateInfoListener, View.OnClickListener
{
  private static final int DIALOG_ERROR_ID = 0;
  public static final String EXTRA_JSON = "json";
  public static final String EXTRA_URL = "url";
  private Context mContext;
  protected DownloadFileTask mDownloadTask;
  private ErrorObject mError;
  protected VersionHelper mVersionHelper;
  
  @SuppressLint({"InlinedApi"})
  private boolean isUnknownSourcesChecked()
  {
    try
    {
      if ((Build.VERSION.SDK_INT >= 17) && (Build.VERSION.SDK_INT < 21))
      {
        if (Settings.Global.getInt(getContentResolver(), "install_non_market_apps") != 1) {
          break label51;
        }
        return true;
      }
      int i = Settings.Secure.getInt(getContentResolver(), "install_non_market_apps");
      if (i != 1) {
        return false;
      }
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return true;
    label51:
    return false;
  }
  
  private boolean isWriteExternalStorageSet(Context paramContext)
  {
    return paramContext.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
  }
  
  protected void configureView()
  {
    ((TextView)findViewById(R.id.label_title)).setText(getAppName());
    final TextView localTextView = (TextView)findViewById(R.id.label_version);
    final String str1 = "Version " + this.mVersionHelper.getVersionString();
    final String str2 = this.mVersionHelper.getFileDateString();
    Object localObject = "Unknown size";
    long l = this.mVersionHelper.getFileSizeBytes();
    if (l >= 0L) {
      localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
    }
    for (;;)
    {
      localTextView.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, localObject }));
      ((Button)findViewById(R.id.button_update)).setOnClickListener(this);
      localObject = (WebView)findViewById(R.id.web_update_details);
      ((WebView)localObject).clearCache(true);
      ((WebView)localObject).destroyDrawingCache();
      ((WebView)localObject).loadDataWithBaseURL("https://sdk.hockeyapp.net/", getReleaseNotes(), "text/html", "utf-8", null);
      return;
      AsyncTaskUtils.execute(new GetFileSizeTask(this, getIntent().getStringExtra("url"), new DownloadFileListener()
      {
        public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask)
        {
          if ((paramAnonymousDownloadFileTask instanceof GetFileSizeTask))
          {
            long l = ((GetFileSizeTask)paramAnonymousDownloadFileTask).getSize();
            paramAnonymousDownloadFileTask = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
            localTextView.setText(UpdateActivity.this.getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, paramAnonymousDownloadFileTask }));
          }
        }
      }));
    }
  }
  
  protected void createDownloadTask(String paramString, DownloadFileListener paramDownloadFileListener)
  {
    this.mDownloadTask = new DownloadFileTask(this, paramString, paramDownloadFileListener);
  }
  
  public void enableUpdateButton()
  {
    findViewById(R.id.button_update).setEnabled(true);
  }
  
  public String getAppName()
  {
    try
    {
      Object localObject = getPackageManager();
      localObject = ((PackageManager)localObject).getApplicationLabel(((PackageManager)localObject).getApplicationInfo(getPackageName(), 0)).toString();
      return (String)localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return "";
  }
  
  public int getCurrentVersionCode()
  {
    try
    {
      int i = getPackageManager().getPackageInfo(getPackageName(), 128).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return -1;
  }
  
  @SuppressLint({"InflateParams"})
  public View getLayoutView()
  {
    return getLayoutInflater().inflate(R.layout.hockeyapp_activity_update, null);
  }
  
  protected String getReleaseNotes()
  {
    return this.mVersionHelper.getReleaseNotes(false);
  }
  
  public void onClick(View paramView)
  {
    prepareDownload();
    paramView.setEnabled(false);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle("App Update");
    setContentView(getLayoutView());
    this.mContext = this;
    this.mVersionHelper = new VersionHelper(this, getIntent().getStringExtra("json"), this);
    configureView();
    this.mDownloadTask = ((DownloadFileTask)getLastNonConfigurationInstance());
    if (this.mDownloadTask != null) {
      this.mDownloadTask.attach(this);
    }
  }
  
  protected Dialog onCreateDialog(int paramInt)
  {
    return onCreateDialog(paramInt, null);
  }
  
  protected Dialog onCreateDialog(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    default: 
      return null;
    }
    new AlertDialog.Builder(this).setMessage("An error has occured").setCancelable(false).setTitle("Error").setIcon(17301543).setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        UpdateActivity.access$002(UpdateActivity.this, null);
        paramAnonymousDialogInterface.cancel();
      }
    }).create();
  }
  
  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    switch (paramInt)
    {
    default: 
      return;
    }
    paramDialog = (AlertDialog)paramDialog;
    if (this.mError != null)
    {
      paramDialog.setMessage(this.mError.getMessage());
      return;
    }
    paramDialog.setMessage("An unknown error has occured.");
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    enableUpdateButton();
    if ((paramArrayOfString.length == 0) || (paramArrayOfInt.length == 0)) {}
    while (paramInt != 1) {
      return;
    }
    if (paramArrayOfInt[0] == 0)
    {
      prepareDownload();
      return;
    }
    HockeyLog.warn("User denied write permission, can't continue with updater task.");
    paramArrayOfString = UpdateManager.getLastListener();
    if (paramArrayOfString != null)
    {
      paramArrayOfString.onUpdatePermissionsNotGranted();
      return;
    }
    new AlertDialog.Builder(this.mContext).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        jdField_this.prepareDownload();
      }
    }).create().show();
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    if (this.mDownloadTask != null) {
      this.mDownloadTask.detach();
    }
    return this.mDownloadTask;
  }
  
  protected void prepareDownload()
  {
    if (!Util.isConnectedToNetwork(this.mContext))
    {
      this.mError = new ErrorObject();
      this.mError.setMessage(getString(R.string.hockeyapp_error_no_network_message));
      runOnUiThread(new Runnable()
      {
        public void run()
        {
          UpdateActivity.this.showDialog(0);
        }
      });
      return;
    }
    if (!isWriteExternalStorageSet(this.mContext))
    {
      if (Build.VERSION.SDK_INT >= 23)
      {
        requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 1);
        return;
      }
      this.mError = new ErrorObject();
      this.mError.setMessage("The permission to access the external storage permission is not set. Please contact the developer.");
      runOnUiThread(new Runnable()
      {
        public void run()
        {
          UpdateActivity.this.showDialog(0);
        }
      });
      return;
    }
    if (!isUnknownSourcesChecked())
    {
      this.mError = new ErrorObject();
      this.mError.setMessage("The installation from unknown sources is not enabled. Please check the device settings.");
      runOnUiThread(new Runnable()
      {
        public void run()
        {
          UpdateActivity.this.showDialog(0);
        }
      });
      return;
    }
    startDownloadTask();
  }
  
  protected void startDownloadTask()
  {
    startDownloadTask(getIntent().getStringExtra("url"));
  }
  
  protected void startDownloadTask(String paramString)
  {
    createDownloadTask(paramString, new DownloadFileListener()
    {
      public void downloadFailed(DownloadFileTask paramAnonymousDownloadFileTask, Boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean.booleanValue())
        {
          UpdateActivity.this.startDownloadTask();
          return;
        }
        UpdateActivity.this.enableUpdateButton();
      }
      
      public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask)
      {
        UpdateActivity.this.enableUpdateButton();
      }
    });
    AsyncTaskUtils.execute(this.mDownloadTask);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */