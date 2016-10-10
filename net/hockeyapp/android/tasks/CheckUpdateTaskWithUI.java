package net.hockeyapp.android.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import net.hockeyapp.android.utils.VersionCache;
import org.json.JSONArray;

public class CheckUpdateTaskWithUI
  extends CheckUpdateTask
{
  private Activity mActivity = null;
  private AlertDialog mDialog = null;
  protected boolean mIsDialogRequired = false;
  
  public CheckUpdateTaskWithUI(WeakReference<Activity> paramWeakReference, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    super(paramWeakReference, paramString1, paramString2, paramUpdateManagerListener);
    if (paramWeakReference != null) {
      this.mActivity = ((Activity)paramWeakReference.get());
    }
    this.mIsDialogRequired = paramBoolean;
  }
  
  @TargetApi(11)
  private void showDialog(final JSONArray paramJSONArray)
  {
    if (getCachingEnabled())
    {
      HockeyLog.verbose("HockeyUpdate", "Caching is enabled. Setting version to cached one.");
      VersionCache.setVersionInfo(this.mActivity, paramJSONArray.toString());
    }
    if ((this.mActivity == null) || (this.mActivity.isFinishing())) {
      return;
    }
    Object localObject = new AlertDialog.Builder(this.mActivity);
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
      ((AlertDialog.Builder)localObject).setPositiveButton(R.string.hockeyapp_update_dialog_positive_button, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (CheckUpdateTaskWithUI.this.getCachingEnabled()) {
            VersionCache.setVersionInfo(CheckUpdateTaskWithUI.this.mActivity, "[]");
          }
          paramAnonymousDialogInterface = new WeakReference(CheckUpdateTaskWithUI.this.mActivity);
          if ((Util.fragmentsSupported().booleanValue()) && (Util.runsOnTablet(paramAnonymousDialogInterface).booleanValue()))
          {
            CheckUpdateTaskWithUI.this.showUpdateFragment(paramJSONArray);
            return;
          }
          CheckUpdateTaskWithUI.this.startUpdateIntent(paramJSONArray, Boolean.valueOf(false));
        }
      });
      this.mDialog = ((AlertDialog.Builder)localObject).create();
      this.mDialog.show();
      return;
    }
    localObject = Util.getAppName(this.mActivity);
    localObject = String.format(this.mActivity.getString(R.string.hockeyapp_update_mandatory_toast), new Object[] { localObject });
    Toast.makeText(this.mActivity, (CharSequence)localObject, 1).show();
    startUpdateIntent(paramJSONArray, Boolean.valueOf(true));
  }
  
  @TargetApi(11)
  private void showUpdateFragment(JSONArray paramJSONArray)
  {
    FragmentTransaction localFragmentTransaction;
    Object localObject;
    if (this.mActivity != null)
    {
      localFragmentTransaction = this.mActivity.getFragmentManager().beginTransaction();
      localFragmentTransaction.setTransition(4097);
      localObject = this.mActivity.getFragmentManager().findFragmentByTag("hockey_update_dialog");
      if (localObject != null) {
        localFragmentTransaction.remove((Fragment)localObject);
      }
      localFragmentTransaction.addToBackStack(null);
      localObject = UpdateFragment.class;
      if (this.listener != null) {
        localObject = this.listener.getUpdateFragmentClass();
      }
    }
    try
    {
      ((DialogFragment)((Class)localObject).getMethod("newInstance", new Class[] { JSONArray.class, String.class }).invoke(null, new Object[] { paramJSONArray, getURLString("apk") })).show(localFragmentTransaction, "hockey_update_dialog");
      return;
    }
    catch (Exception localException)
    {
      HockeyLog.error("An exception happened while showing the update fragment:");
      localException.printStackTrace();
      HockeyLog.error("Showing update activity instead.");
      startUpdateIntent(paramJSONArray, Boolean.valueOf(false));
    }
  }
  
  private void startUpdateIntent(JSONArray paramJSONArray, Boolean paramBoolean)
  {
    Object localObject1 = null;
    if (this.listener != null) {
      localObject1 = this.listener.getUpdateActivityClass();
    }
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = UpdateActivity.class;
    }
    if (this.mActivity != null)
    {
      localObject1 = new Intent();
      ((Intent)localObject1).setClass(this.mActivity, (Class)localObject2);
      ((Intent)localObject1).putExtra("json", paramJSONArray.toString());
      ((Intent)localObject1).putExtra("url", getURLString("apk"));
      this.mActivity.startActivity((Intent)localObject1);
      if (paramBoolean.booleanValue()) {
        this.mActivity.finish();
      }
    }
    cleanUp();
  }
  
  protected void cleanUp()
  {
    super.cleanUp();
    this.mActivity = null;
    this.mDialog = null;
  }
  
  public void detach()
  {
    super.detach();
    this.mActivity = null;
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
      showDialog(paramJSONArray);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/CheckUpdateTaskWithUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */