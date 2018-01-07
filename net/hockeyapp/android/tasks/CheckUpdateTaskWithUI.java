package net.hockeyapp.android.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import net.hockeyapp.android.R;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import org.json.JSONArray;

public class CheckUpdateTaskWithUI extends CheckUpdateTask {
    private AlertDialog mDialog = null;
    protected boolean mIsDialogRequired = false;
    private WeakReference<Activity> mWeakActivity = null;

    public CheckUpdateTaskWithUI(WeakReference<Activity> weakActivity, String urlString, String appIdentifier, UpdateManagerListener listener, boolean isDialogRequired) {
        super(weakActivity, urlString, appIdentifier, listener);
        this.mWeakActivity = weakActivity;
        this.mIsDialogRequired = isDialogRequired;
    }

    public void detach() {
        super.detach();
        this.mWeakActivity = null;
        if (this.mDialog != null) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    protected void onPostExecute(JSONArray updateInfo) {
        super.onPostExecute(updateInfo);
        if (updateInfo != null && this.mIsDialogRequired) {
            showDialog((Activity) this.mWeakActivity.get(), updateInfo);
        }
    }

    private void showDialog(final Activity activity, final JSONArray updateInfo) {
        if (activity != null && !activity.isFinishing()) {
            Builder builder = new Builder(activity);
            builder.setTitle(R.string.hockeyapp_update_dialog_title);
            if (this.mandatory.booleanValue()) {
                String appName = Util.getAppName(activity);
                Toast.makeText(activity, activity.getString(R.string.hockeyapp_update_mandatory_toast, new Object[]{appName}), 1).show();
                startUpdateIntent(activity, updateInfo, Boolean.valueOf(true));
                return;
            }
            builder.setMessage(R.string.hockeyapp_update_dialog_message);
            builder.setNegativeButton(R.string.hockeyapp_update_dialog_negative_button, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CheckUpdateTaskWithUI.this.cleanUp();
                    if (CheckUpdateTaskWithUI.this.listener != null) {
                        CheckUpdateTaskWithUI.this.listener.onCancel();
                    }
                }
            });
            builder.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    CheckUpdateTaskWithUI.this.cleanUp();
                    if (CheckUpdateTaskWithUI.this.listener != null) {
                        CheckUpdateTaskWithUI.this.listener.onCancel();
                    }
                }
            });
            builder.setPositiveButton(R.string.hockeyapp_update_dialog_positive_button, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean useUpdateDialog;
                    if (CheckUpdateTaskWithUI.this.listener != null) {
                        useUpdateDialog = CheckUpdateTaskWithUI.this.listener.useUpdateDialog(activity);
                    } else {
                        useUpdateDialog = Util.runsOnTablet(activity).booleanValue();
                    }
                    if (useUpdateDialog) {
                        CheckUpdateTaskWithUI.this.showUpdateFragment(activity, updateInfo);
                    } else {
                        CheckUpdateTaskWithUI.this.startUpdateIntent(activity, updateInfo, Boolean.valueOf(false));
                    }
                }
            });
            this.mDialog = builder.create();
            this.mDialog.show();
        }
    }

    private void showUpdateFragment(Activity activity, JSONArray updateInfo) {
        if (activity != null) {
            FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(4097);
            Fragment existingFragment = activity.getFragmentManager().findFragmentByTag(UpdateFragment.FRAGMENT_TAG);
            if (existingFragment != null) {
                fragmentTransaction.remove(existingFragment);
            }
            fragmentTransaction.addToBackStack(null);
            Class<? extends UpdateFragment> fragmentClass = UpdateFragment.class;
            if (this.listener != null) {
                fragmentClass = this.listener.getUpdateFragmentClass();
            }
            try {
                ((DialogFragment) fragmentClass.getMethod("newInstance", new Class[]{String.class, String.class, Boolean.TYPE}).invoke(null, new Object[]{updateInfo.toString(), this.apkUrlString, Boolean.valueOf(true)})).show(fragmentTransaction, UpdateFragment.FRAGMENT_TAG);
            } catch (Throwable e) {
                HockeyLog.error("An exception happened while showing the update fragment", e);
            }
        }
    }

    private void startUpdateIntent(Activity activity, JSONArray updateInfo, Boolean finish) {
        if (activity != null) {
            Class<? extends UpdateFragment> fragmentClass = UpdateFragment.class;
            if (this.listener != null) {
                fragmentClass = this.listener.getUpdateFragmentClass();
            }
            Intent intent = new Intent();
            intent.setClass(activity, UpdateActivity.class);
            intent.putExtra("fragmentClass", fragmentClass.getName());
            intent.putExtra(UpdateFragment.FRAGMENT_VERSION_INFO, updateInfo.toString());
            intent.putExtra(UpdateFragment.FRAGMENT_URL, this.apkUrlString);
            intent.putExtra(UpdateFragment.FRAGMENT_DIALOG, false);
            activity.startActivity(intent);
            if (finish.booleanValue()) {
                activity.finish();
            }
        }
        cleanUp();
    }

    protected void cleanUp() {
        super.cleanUp();
        this.mWeakActivity = null;
        this.mDialog = null;
    }
}
