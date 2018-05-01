package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.PermissionsUtil;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;

public class UpdateFragment
  extends DialogFragment
  implements View.OnClickListener, UpdateInfoListener
{
  public static final String FRAGMENT_DIALOG = "dialog";
  public static final String FRAGMENT_TAG = "hockey_update_dialog";
  public static final String FRAGMENT_URL = "url";
  public static final String FRAGMENT_VERSION_INFO = "versionInfo";
  private String mUrlString;
  private String mVersionInfo;
  
  public static UpdateFragment newInstance(String paramString1, String paramString2, boolean paramBoolean)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("url", paramString2);
    localBundle.putString("versionInfo", paramString1);
    localBundle.putBoolean("dialog", paramBoolean);
    paramString1 = new UpdateFragment();
    paramString1.setArguments(localBundle);
    return paramString1;
  }
  
  private static String[] requiredPermissions()
  {
    ArrayList localArrayList = new ArrayList();
    if (Build.VERSION.SDK_INT < 19) {
      localArrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
    }
    return (String[])localArrayList.toArray(new String[0]);
  }
  
  private void showError(int paramInt)
  {
    new AlertDialog.Builder(getActivity()).setTitle(R.string.hockeyapp_dialog_error_title).setMessage(paramInt).setCancelable(false).setPositiveButton(R.string.hockeyapp_dialog_positive_button, null).create().show();
  }
  
  public int getCurrentVersionCode()
  {
    int i = -1;
    try
    {
      j = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 128).versionCode;
      return j;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        j = i;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        int j = i;
      }
    }
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
    setRetainInstance(true);
    paramBundle = getArguments();
    this.mUrlString = paramBundle.getString("url");
    this.mVersionInfo = paramBundle.getString("versionInfo");
    setShowsDialog(paramBundle.getBoolean("dialog"));
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = getLayoutView();
    paramBundle = new VersionHelper(getActivity(), this.mVersionInfo, this);
    paramLayoutInflater = (TextView)paramViewGroup.findViewById(R.id.label_title);
    paramLayoutInflater.setText(Util.getAppName(getActivity()));
    paramLayoutInflater.setContentDescription(paramLayoutInflater.getText());
    final TextView localTextView = (TextView)paramViewGroup.findViewById(R.id.label_version);
    final String str1 = String.format(getString(R.string.hockeyapp_update_version), new Object[] { paramBundle.getVersionString() });
    final String str2 = paramBundle.getFileDateString();
    paramLayoutInflater = getString(R.string.hockeyapp_update_unknown_size);
    long l = paramBundle.getFileSizeBytes();
    if (l >= 0L) {
      paramLayoutInflater = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
    }
    for (;;)
    {
      localTextView.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, paramLayoutInflater }));
      ((Button)paramViewGroup.findViewById(R.id.button_update)).setOnClickListener(this);
      paramLayoutInflater = (WebView)paramViewGroup.findViewById(R.id.web_update_details);
      paramLayoutInflater.clearCache(true);
      paramLayoutInflater.destroyDrawingCache();
      paramLayoutInflater.loadDataWithBaseURL("https://sdk.hockeyapp.net/", paramBundle.getReleaseNotes(false), "text/html", "utf-8", null);
      return paramViewGroup;
      AsyncTaskUtils.execute(new GetFileSizeTask(getActivity(), this.mUrlString, new DownloadFileListener()
      {
        public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask)
        {
          if ((paramAnonymousDownloadFileTask instanceof GetFileSizeTask))
          {
            long l = ((GetFileSizeTask)paramAnonymousDownloadFileTask).getSize();
            paramAnonymousDownloadFileTask = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf((float)l / 1048576.0F) }) + " MB";
            localTextView.setText(UpdateFragment.this.getString(R.string.hockeyapp_update_version_details_label, new Object[] { str1, str2, paramAnonymousDownloadFileTask }));
          }
        }
      }));
    }
  }
  
  public void onDestroyView()
  {
    Dialog localDialog = getDialog();
    if ((localDialog != null) && (getRetainInstance())) {
      localDialog.setDismissMessage(null);
    }
    super.onDestroyView();
  }
  
  public void onStart()
  {
    super.onStart();
    Dialog localDialog = getDialog();
    if ((localDialog != null) && (localDialog.getWindow() != null)) {
      localDialog.getWindow().setLayout(-1, -1);
    }
  }
  
  protected void prepareDownload()
  {
    Activity localActivity = getActivity();
    if (!Util.isConnectedToNetwork(localActivity)) {
      showError(R.string.hockeyapp_error_no_network_message);
    }
    for (;;)
    {
      return;
      if (!PermissionsUtil.permissionsAreGranted(PermissionsUtil.permissionsState(localActivity, requiredPermissions())))
      {
        showError(R.string.hockeyapp_error_no_external_storage_permission);
      }
      else if (!PermissionsUtil.isUnknownSourcesEnabled(localActivity))
      {
        if (Build.VERSION.SDK_INT >= 26)
        {
          Intent localIntent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES");
          localIntent.setData(Uri.parse("package:" + localActivity.getPackageName()));
          localActivity.startActivity(localIntent);
        }
        else
        {
          showError(R.string.hockeyapp_error_install_form_unknown_sources_disabled);
        }
      }
      else
      {
        startDownloadTask();
        if (getShowsDialog()) {
          dismiss();
        }
      }
    }
  }
  
  protected void startDownloadTask()
  {
    AsyncTaskUtils.execute(new DownloadFileTask(getActivity(), this.mUrlString, new DownloadFileListener()
    {
      public void downloadFailed(DownloadFileTask paramAnonymousDownloadFileTask, Boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean.booleanValue()) {
          UpdateFragment.this.startDownloadTask();
        }
      }
      
      public void downloadSuccessful(DownloadFileTask paramAnonymousDownloadFileTask) {}
    }));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */