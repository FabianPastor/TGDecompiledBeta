package net.hockeyapp.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;

@TargetApi(11)
public class UpdateFragment
  extends DialogFragment
  implements View.OnClickListener, UpdateInfoListener
{
  public static final String FRAGMENT_URL = "url";
  public static final String FRAGMENT_VERSION_INFO = "versionInfo";
  private DownloadFileTask mDownloadTask;
  private String mUrlString;
  private VersionHelper mVersionHelper;
  private JSONArray mVersionInfo;
  
  public static UpdateFragment newInstance(JSONArray paramJSONArray, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("url", paramString);
    localBundle.putString("versionInfo", paramJSONArray.toString());
    paramJSONArray = new UpdateFragment();
    paramJSONArray.setArguments(localBundle);
    return paramJSONArray;
  }
  
  private void startDownloadTask(final Activity paramActivity)
  {
    this.mDownloadTask = new DownloadFileTask(paramActivity, this.mUrlString, new DownloadFileListener()
    {
      public void downloadFailed(DownloadFileTask paramAnonymousDownloadFileTask, Boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean.booleanValue()) {
          UpdateFragment.this.startDownloadTask(paramActivity);
        }
      }
      
      public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask) {}
    });
    AsyncTaskUtils.execute(this.mDownloadTask);
  }
  
  public String getAppName()
  {
    Object localObject = getActivity();
    try
    {
      PackageManager localPackageManager = ((Activity)localObject).getPackageManager();
      localObject = localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(((Activity)localObject).getPackageName(), 0)).toString();
      return (String)localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return "";
  }
  
  public int getCurrentVersionCode()
  {
    try
    {
      int i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 128).versionCode;
      return i;
    }
    catch (NullPointerException localNullPointerException)
    {
      return -1;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return -1;
  }
  
  public View getLayoutView()
  {
    LinearLayout localLinearLayout = new LinearLayout(getActivity());
    LayoutInflater.from(getActivity()).inflate(R.layout.hockeyapp_fragment_update, localLinearLayout);
    return localLinearLayout;
  }
  
  public void onClick(View paramView)
  {
    prepareDownload();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    try
    {
      this.mUrlString = getArguments().getString("url");
      this.mVersionInfo = new JSONArray(getArguments().getString("versionInfo"));
      setStyle(1, 16973939);
      return;
    }
    catch (JSONException paramBundle)
    {
      dismiss();
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, final Bundle paramBundle)
  {
    paramViewGroup = getLayoutView();
    this.mVersionHelper = new VersionHelper(getActivity(), this.mVersionInfo.toString(), this);
    ((TextView)paramViewGroup.findViewById(R.id.label_title)).setText(getAppName());
    paramBundle = (TextView)paramViewGroup.findViewById(R.id.label_version);
    final String str1 = "Version " + this.mVersionHelper.getVersionString();
    final String str2 = this.mVersionHelper.getFileDateString();
    paramLayoutInflater = "Unknown size";
    long l = this.mVersionHelper.getFileSizeBytes();
    if (l >= 0L) {
      paramLayoutInflater = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
    }
    for (;;)
    {
      paramBundle.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, paramLayoutInflater }));
      ((Button)paramViewGroup.findViewById(R.id.button_update)).setOnClickListener(this);
      paramLayoutInflater = (WebView)paramViewGroup.findViewById(R.id.web_update_details);
      paramLayoutInflater.clearCache(true);
      paramLayoutInflater.destroyDrawingCache();
      paramLayoutInflater.loadDataWithBaseURL("https://sdk.hockeyapp.net/", this.mVersionHelper.getReleaseNotes(false), "text/html", "utf-8", null);
      return paramViewGroup;
      AsyncTaskUtils.execute(new GetFileSizeTask(getActivity(), this.mUrlString, new DownloadFileListener()
      {
        public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask)
        {
          if ((paramAnonymousDownloadFileTask instanceof GetFileSizeTask))
          {
            long l = ((GetFileSizeTask)paramAnonymousDownloadFileTask).getSize();
            paramAnonymousDownloadFileTask = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
            paramBundle.setText(UpdateFragment.this.getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, paramAnonymousDownloadFileTask }));
          }
        }
      }));
    }
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramArrayOfString.length == 0) || (paramArrayOfInt.length == 0)) {}
    while (paramInt != 1) {
      return;
    }
    if (paramArrayOfInt[0] == 0)
    {
      startDownloadTask(getActivity());
      return;
    }
    HockeyLog.warn("User denied write permission, can't continue with updater task.");
    paramArrayOfString = UpdateManager.getLastListener();
    if (paramArrayOfString != null)
    {
      paramArrayOfString.onUpdatePermissionsNotGranted();
      return;
    }
    new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        jdField_this.prepareDownload();
      }
    }).create().show();
  }
  
  public void prepareDownload()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (getActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
    {
      requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 1);
      return;
    }
    startDownloadTask(getActivity());
    dismiss();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */