package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
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

public class UpdateActivity extends Activity implements OnClickListener, UpdateInfoListener {
    private Context mContext;
    protected DownloadFileTask mDownloadTask;
    private ErrorObject mError;
    protected VersionHelper mVersionHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("App Update");
        setContentView(getLayoutView());
        this.mContext = this;
        this.mVersionHelper = new VersionHelper(this, getIntent().getStringExtra("json"), this);
        configureView();
        this.mDownloadTask = (DownloadFileTask) getLastNonConfigurationInstance();
        if (this.mDownloadTask != null) {
            this.mDownloadTask.attach(this);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mDownloadTask != null) {
            this.mDownloadTask.detach();
        }
        return this.mDownloadTask;
    }

    protected Dialog onCreateDialog(int id) {
        return onCreateDialog(id, null);
    }

    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case 0:
                return new Builder(this).setMessage("An error has occured").setCancelable(false).setTitle("Error").setIcon(17301543).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UpdateActivity.this.mError = null;
                        dialog.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case 0:
                AlertDialog messageDialogError = (AlertDialog) dialog;
                if (this.mError != null) {
                    messageDialogError.setMessage(this.mError.getMessage());
                    return;
                } else {
                    messageDialogError.setMessage("An unknown error has occured.");
                    return;
                }
            default:
                return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        enableUpdateButton();
        if (permissions.length != 0 && grantResults.length != 0 && requestCode == 1) {
            if (grantResults[0] == 0) {
                prepareDownload();
                return;
            }
            HockeyLog.warn("User denied write permission, can't continue with updater task.");
            UpdateManagerListener listener = UpdateManager.getLastListener();
            if (listener != null) {
                listener.onUpdatePermissionsNotGranted();
                return;
            }
            final UpdateActivity updateActivity = this;
            new Builder(this.mContext).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    updateActivity.prepareDownload();
                }
            }).create().show();
        }
    }

    public int getCurrentVersionCode() {
        int currentVersionCode = -1;
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 128).versionCode;
        } catch (NameNotFoundException e) {
            return currentVersionCode;
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(R.layout.hockeyapp_activity_update, null);
    }

    public void onClick(View v) {
        prepareDownload();
        v.setEnabled(false);
    }

    protected void configureView() {
        ((TextView) findViewById(R.id.label_title)).setText(getAppName());
        final TextView versionLabel = (TextView) findViewById(R.id.label_version);
        String versionString = "Version " + this.mVersionHelper.getVersionString();
        final String fileDate = this.mVersionHelper.getFileDateString();
        String appSizeString = "Unknown size";
        if (this.mVersionHelper.getFileSizeBytes() >= 0) {
            appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
        } else {
            final String str = versionString;
            AsyncTaskUtils.execute(new GetFileSizeTask(this, getIntent().getStringExtra(UpdateFragment.FRAGMENT_URL), new DownloadFileListener() {
                public void downloadSuccessful(DownloadFileTask task) {
                    if (task instanceof GetFileSizeTask) {
                        long appSize = ((GetFileSizeTask) task).getSize();
                        String appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
                        versionLabel.setText(UpdateActivity.this.getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDate, appSizeString}));
                    }
                }
            }));
        }
        versionLabel.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[]{versionString, fileDate, appSizeString}));
        ((Button) findViewById(R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) findViewById(R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL("https://sdk.hockeyapp.net/", getReleaseNotes(), "text/html", "utf-8", null);
    }

    protected String getReleaseNotes() {
        return this.mVersionHelper.getReleaseNotes(false);
    }

    protected void startDownloadTask() {
        startDownloadTask(getIntent().getStringExtra(UpdateFragment.FRAGMENT_URL));
    }

    protected void startDownloadTask(String url) {
        createDownloadTask(url, new DownloadFileListener() {
            public void downloadFailed(DownloadFileTask task, Boolean userWantsRetry) {
                if (userWantsRetry.booleanValue()) {
                    UpdateActivity.this.startDownloadTask();
                } else {
                    UpdateActivity.this.enableUpdateButton();
                }
            }

            public void downloadSuccessful(DownloadFileTask task) {
                UpdateActivity.this.enableUpdateButton();
            }
        });
        AsyncTaskUtils.execute(this.mDownloadTask);
    }

    protected void createDownloadTask(String url, DownloadFileListener listener) {
        this.mDownloadTask = new DownloadFileTask(this, url, listener);
    }

    public void enableUpdateButton() {
        findViewById(R.id.button_update).setEnabled(true);
    }

    public String getAppName() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), 0)).toString();
        } catch (NameNotFoundException e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    private boolean isWriteExternalStorageSet(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    @SuppressLint({"InlinedApi"})
    private boolean isUnknownSourcesChecked() {
        try {
            if (VERSION.SDK_INT < 17 || VERSION.SDK_INT >= 21) {
                if (Secure.getInt(getContentResolver(), "install_non_market_apps") != 1) {
                    return false;
                }
                return true;
            } else if (Global.getInt(getContentResolver(), "install_non_market_apps") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SettingNotFoundException e) {
            return true;
        }
    }

    protected void prepareDownload() {
        if (!Util.isConnectedToNetwork(this.mContext)) {
            this.mError = new ErrorObject();
            this.mError.setMessage(getString(R.string.hockeyapp_error_no_network_message));
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        } else if (isWriteExternalStorageSet(this.mContext)) {
            if (isUnknownSourcesChecked()) {
                startDownloadTask();
                return;
            }
            this.mError = new ErrorObject();
            this.mError.setMessage("The installation from unknown sources is not enabled. Please check the device settings.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        } else if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        } else {
            this.mError = new ErrorObject();
            this.mError.setMessage("The permission to access the external storage permission is not set. Please contact the developer.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        }
    }
}
