package net.hockeyapp.android;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.PermissionsUtil;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;

public class UpdateFragment extends DialogFragment implements OnClickListener, UpdateInfoListener {
    public static final String FRAGMENT_DIALOG = "dialog";
    public static final String FRAGMENT_TAG = "hockey_update_dialog";
    public static final String FRAGMENT_URL = "url";
    public static final String FRAGMENT_VERSION_INFO = "versionInfo";
    private String mUrlString;
    private String mVersionInfo;

    public static UpdateFragment newInstance(String versionInfo, String urlString, boolean dialog) {
        Bundle arguments = new Bundle();
        arguments.putString(FRAGMENT_URL, urlString);
        arguments.putString(FRAGMENT_VERSION_INFO, versionInfo);
        arguments.putBoolean(FRAGMENT_DIALOG, dialog);
        UpdateFragment fragment = new UpdateFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -1);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments = getArguments();
        this.mUrlString = arguments.getString(FRAGMENT_URL);
        this.mVersionInfo = arguments.getString(FRAGMENT_VERSION_INFO);
        setShowsDialog(arguments.getBoolean(FRAGMENT_DIALOG));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutView();
        VersionHelper versionHelper = new VersionHelper(getActivity(), this.mVersionInfo, this);
        TextView nameLabel = (TextView) view.findViewById(R.id.label_title);
        nameLabel.setText(Util.getAppName(getActivity()));
        nameLabel.setContentDescription(nameLabel.getText());
        TextView versionLabel = (TextView) view.findViewById(R.id.label_version);
        String versionString = String.format(getString(R.string.hockeyapp_update_version), new Object[]{versionHelper.getVersionString()});
        final String fileDate = versionHelper.getFileDateString();
        String appSizeString = getString(R.string.hockeyapp_update_unknown_size);
        if (versionHelper.getFileSizeBytes() >= 0) {
            appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
        } else {
            final TextView textView = versionLabel;
            final String str = versionString;
            AsyncTaskUtils.execute(new GetFileSizeTask(getActivity(), this.mUrlString, new DownloadFileListener() {
                public void downloadSuccessful(DownloadFileTask task) {
                    if (task instanceof GetFileSizeTask) {
                        long appSize = ((GetFileSizeTask) task).getSize();
                        String appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
                        textView.setText(UpdateFragment.this.getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDate, appSizeString}));
                    }
                }
            }));
        }
        versionLabel.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[]{versionString, fileDate, appSizeString}));
        ((Button) view.findViewById(R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) view.findViewById(R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL("https://sdk.hockeyapp.net/", versionHelper.getReleaseNotes(false), "text/html", "utf-8", null);
        return view;
    }

    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public void onClick(View view) {
        prepareDownload();
    }

    public int getCurrentVersionCode() {
        int currentVersionCode = -1;
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 128).versionCode;
        } catch (NameNotFoundException e) {
            return currentVersionCode;
        } catch (NullPointerException e2) {
            return currentVersionCode;
        }
    }

    private void showError(int message) {
        new Builder(getActivity()).setTitle(R.string.hockeyapp_dialog_error_title).setMessage(message).setCancelable(false).setPositiveButton(R.string.hockeyapp_dialog_positive_button, null).create().show();
    }

    private static String[] requiredPermissions() {
        ArrayList<String> permissions = new ArrayList();
        if (VERSION.SDK_INT < 19) {
            permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        return (String[]) permissions.toArray(new String[0]);
    }

    protected void prepareDownload() {
        Context context = getActivity();
        if (!Util.isConnectedToNetwork(context)) {
            showError(R.string.hockeyapp_error_no_network_message);
        } else if (!PermissionsUtil.permissionsAreGranted(PermissionsUtil.permissionsState(context, requiredPermissions()))) {
            showError(R.string.hockeyapp_error_no_external_storage_permission);
        } else if (PermissionsUtil.isUnknownSourcesEnabled(context)) {
            startDownloadTask();
            if (getShowsDialog()) {
                dismiss();
            }
        } else if (VERSION.SDK_INT >= 26) {
            Intent intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES");
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } else {
            showError(R.string.hockeyapp_error_install_form_unknown_sources_disabled);
        }
    }

    protected void startDownloadTask() {
        AsyncTaskUtils.execute(new DownloadFileTask(getActivity(), this.mUrlString, new DownloadFileListener() {
            public void downloadFailed(DownloadFileTask task, Boolean userWantsRetry) {
                if (userWantsRetry.booleanValue()) {
                    UpdateFragment.this.startDownloadTask();
                }
            }

            public void downloadSuccessful(DownloadFileTask task) {
            }
        }));
    }

    public View getLayoutView() {
        LinearLayout layout = new LinearLayout(getActivity());
        LayoutInflater.from(getActivity()).inflate(R.layout.hockeyapp_fragment_update, layout);
        return layout;
    }
}
