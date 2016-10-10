package net.hockeyapp.android.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class UiThreadUtil
{
  public static UiThreadUtil getInstance()
  {
    return WbUtilHolder.INSTANCE;
  }
  
  public void dismissLoading(WeakReference<Activity> paramWeakReference, final ProgressDialog paramProgressDialog)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      if (paramWeakReference != null) {
        paramWeakReference.runOnUiThread(new Runnable()
        {
          public void run()
          {
            if ((paramProgressDialog != null) && (paramProgressDialog.isShowing())) {
              paramProgressDialog.dismiss();
            }
          }
        });
      }
    }
  }
  
  public void dismissLoadingDialogAndDisplayError(final WeakReference<Activity> paramWeakReference, final ProgressDialog paramProgressDialog, final int paramInt)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      if (paramWeakReference != null) {
        paramWeakReference.runOnUiThread(new Runnable()
        {
          public void run()
          {
            if ((paramProgressDialog != null) && (paramProgressDialog.isShowing())) {
              paramProgressDialog.dismiss();
            }
            paramWeakReference.showDialog(paramInt);
          }
        });
      }
    }
  }
  
  public void displayToastMessage(final WeakReference<Activity> paramWeakReference, final String paramString, final int paramInt)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      if (paramWeakReference != null) {
        paramWeakReference.runOnUiThread(new Runnable()
        {
          public void run()
          {
            Toast.makeText(paramWeakReference, paramString, paramInt).show();
          }
        });
      }
    }
  }
  
  private static class WbUtilHolder
  {
    public static final UiThreadUtil INSTANCE = new UiThreadUtil(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/UiThreadUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */