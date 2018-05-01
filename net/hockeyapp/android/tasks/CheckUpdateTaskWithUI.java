package net.hockeyapp.android.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import org.json.JSONArray;

public class CheckUpdateTaskWithUI
  extends CheckUpdateTask
{
  private AlertDialog mDialog = null;
  protected boolean mIsDialogRequired = false;
  private WeakReference<Activity> mWeakActivity = null;
  
  public CheckUpdateTaskWithUI(WeakReference<Activity> paramWeakReference, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    super(paramWeakReference, paramString1, paramString2, paramUpdateManagerListener);
    this.mWeakActivity = paramWeakReference;
    this.mIsDialogRequired = paramBoolean;
  }
  
  private void showDialog(final Activity paramActivity, final JSONArray paramJSONArray)
  {
    if ((paramActivity == null) || (paramActivity.isFinishing())) {}
    for (;;)
    {
      return;
      Object localObject = new AlertDialog.Builder(paramActivity);
      ((AlertDialog.Builder)localObject).setTitle(R.string.hockeyapp_update_dialog_title);
      if (!this.mandatory.booleanValue())
      {
        ((AlertDialog.Builder)localObject).setMessage(R.string.hockeyapp_update_dialog_message);
        ((AlertDialog.Builder)localObject).setNegativeButton(R.string.hockeyapp_update_dialog_negative_button, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            CheckUpdateTaskWithUI.this.cleanUp();
            if (CheckUpdateTaskWithUI.this.listener != null) {
              CheckUpdateTaskWithUI.this.listener.onCancel();
            }
          }
        });
        ((AlertDialog.Builder)localObject).setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          public void onCancel(DialogInterface paramAnonymousDialogInterface)
          {
            CheckUpdateTaskWithUI.this.cleanUp();
            if (CheckUpdateTaskWithUI.this.listener != null) {
              CheckUpdateTaskWithUI.this.listener.onCancel();
            }
          }
        });
        ((AlertDialog.Builder)localObject).setPositiveButton(R.string.hockeyapp_update_dialog_positive_button, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            boolean bool;
            if (CheckUpdateTaskWithUI.this.listener != null)
            {
              bool = CheckUpdateTaskWithUI.this.listener.useUpdateDialog(paramActivity);
              if (!bool) {
                break label59;
              }
              CheckUpdateTaskWithUI.this.showUpdateFragment(paramActivity, paramJSONArray);
            }
            for (;;)
            {
              return;
              bool = Util.runsOnTablet(paramActivity).booleanValue();
              break;
              label59:
              CheckUpdateTaskWithUI.this.startUpdateIntent(paramActivity, paramJSONArray, Boolean.valueOf(false));
            }
          }
        });
        this.mDialog = ((AlertDialog.Builder)localObject).create();
        this.mDialog.show();
      }
      else
      {
        localObject = Util.getAppName(paramActivity);
        Toast.makeText(paramActivity, paramActivity.getString(R.string.hockeyapp_update_mandatory_toast, new Object[] { localObject }), 1).show();
        startUpdateIntent(paramActivity, paramJSONArray, Boolean.valueOf(true));
      }
    }
  }
  
  private void showUpdateFragment(Activity paramActivity, JSONArray paramJSONArray)
  {
    FragmentTransaction localFragmentTransaction;
    if (paramActivity != null)
    {
      localFragmentTransaction = paramActivity.getFragmentManager().beginTransaction();
      localFragmentTransaction.setTransition(4097);
      paramActivity = paramActivity.getFragmentManager().findFragmentByTag("hockey_update_dialog");
      if (paramActivity != null) {
        localFragmentTransaction.remove(paramActivity);
      }
      localFragmentTransaction.addToBackStack(null);
      paramActivity = UpdateFragment.class;
      if (this.listener != null) {
        paramActivity = this.listener.getUpdateFragmentClass();
      }
    }
    try
    {
      ((DialogFragment)paramActivity.getMethod("newInstance", new Class[] { String.class, String.class, Boolean.TYPE }).invoke(null, new Object[] { paramJSONArray.toString(), this.apkUrlString, Boolean.valueOf(true) })).show(localFragmentTransaction, "hockey_update_dialog");
      return;
    }
    catch (Exception paramActivity)
    {
      for (;;)
      {
        HockeyLog.error("An exception happened while showing the update fragment", paramActivity);
      }
    }
  }
  
  private void startUpdateIntent(Activity paramActivity, JSONArray paramJSONArray, Boolean paramBoolean)
  {
    if (paramActivity != null)
    {
      Class localClass = UpdateFragment.class;
      if (this.listener != null) {
        localClass = this.listener.getUpdateFragmentClass();
      }
      Intent localIntent = new Intent();
      localIntent.setClass(paramActivity, UpdateActivity.class);
      localIntent.putExtra("fragmentClass", localClass.getName());
      localIntent.putExtra("versionInfo", paramJSONArray.toString());
      localIntent.putExtra("url", this.apkUrlString);
      localIntent.putExtra("dialog", false);
      paramActivity.startActivity(localIntent);
      if (paramBoolean.booleanValue()) {
        paramActivity.finish();
      }
    }
    cleanUp();
  }
  
  protected void cleanUp()
  {
    super.cleanUp();
    this.mWeakActivity = null;
    this.mDialog = null;
  }
  
  public void detach()
  {
    super.detach();
    this.mWeakActivity = null;
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
  }
  
  protected void onPostExecute(JSONArray paramJSONArray)
  {
    super.onPostExecute(paramJSONArray);
    if ((paramJSONArray != null) && (this.mIsDialogRequired)) {
      showDialog((Activity)this.mWeakActivity.get(), paramJSONArray);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/CheckUpdateTaskWithUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */