package org.telegram.ui.ActionBar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;

public class BaseFragment
{
  protected ActionBar actionBar;
  protected Bundle arguments;
  protected int classGuid = 0;
  protected View fragmentView;
  protected boolean hasOwnBackground = false;
  private boolean isFinished = false;
  protected ActionBarLayout parentLayout;
  protected boolean swipeBackEnabled = true;
  protected Dialog visibleDialog = null;
  
  public BaseFragment()
  {
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
  }
  
  public BaseFragment(Bundle paramBundle)
  {
    this.arguments = paramBundle;
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
  }
  
  protected void clearViews()
  {
    ViewGroup localViewGroup;
    if (this.fragmentView != null)
    {
      localViewGroup = (ViewGroup)this.fragmentView.getParent();
      if (localViewGroup == null) {}
    }
    try
    {
      localViewGroup.removeView(this.fragmentView);
      this.fragmentView = null;
      if (this.actionBar != null)
      {
        localViewGroup = (ViewGroup)this.actionBar.getParent();
        if (localViewGroup == null) {}
      }
    }
    catch (Exception localException1)
    {
      try
      {
        localViewGroup.removeView(this.actionBar);
        this.actionBar = null;
        this.parentLayout = null;
        return;
        localException1 = localException1;
        FileLog.e("tmessages", localException1);
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext);
    paramContext.setBackgroundColor(-11371101);
    paramContext.setItemsBackgroundColor(-12554860);
    return paramContext;
  }
  
  public View createView(Context paramContext)
  {
    return null;
  }
  
  public void dismissCurrentDialig()
  {
    if (this.visibleDialog == null) {
      return;
    }
    try
    {
      this.visibleDialog.dismiss();
      this.visibleDialog = null;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public boolean dismissDialogOnPause(Dialog paramDialog)
  {
    return true;
  }
  
  public void finishFragment()
  {
    finishFragment(true);
  }
  
  public void finishFragment(boolean paramBoolean)
  {
    if ((this.isFinished) || (this.parentLayout == null)) {
      return;
    }
    this.parentLayout.closeLastFragment(paramBoolean);
  }
  
  public ActionBar getActionBar()
  {
    return this.actionBar;
  }
  
  public Bundle getArguments()
  {
    return this.arguments;
  }
  
  public View getFragmentView()
  {
    return this.fragmentView;
  }
  
  public Activity getParentActivity()
  {
    if (this.parentLayout != null) {
      return this.parentLayout.parentActivity;
    }
    return null;
  }
  
  public Dialog getVisibleDialog()
  {
    return this.visibleDialog;
  }
  
  public boolean needDelayOpenAnimation()
  {
    return false;
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public boolean onBackPressed()
  {
    return true;
  }
  
  protected void onBecomeFullyVisible() {}
  
  public void onBeginSlide()
  {
    try
    {
      if ((this.visibleDialog != null) && (this.visibleDialog.isShowing()))
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      if (this.actionBar != null) {
        this.actionBar.onPause();
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  protected AnimatorSet onCustomTransitionAnimation(boolean paramBoolean, Runnable paramRunnable)
  {
    return null;
  }
  
  protected void onDialogDismiss(Dialog paramDialog) {}
  
  public boolean onFragmentCreate()
  {
    return true;
  }
  
  public void onFragmentDestroy()
  {
    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
    this.isFinished = true;
    if (this.actionBar != null) {
      this.actionBar.setEnabled(false);
    }
  }
  
  public void onLowMemory() {}
  
  public void onPause()
  {
    if (this.actionBar != null) {
      this.actionBar.onPause();
    }
    try
    {
      if ((this.visibleDialog != null) && (this.visibleDialog.isShowing()) && (dismissDialogOnPause(this.visibleDialog)))
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {}
  
  public void onResume() {}
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public boolean presentFragment(BaseFragment paramBaseFragment)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment));
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean));
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true));
  }
  
  public void removeSelfFromStack()
  {
    if ((this.isFinished) || (this.parentLayout == null)) {
      return;
    }
    this.parentLayout.removeFragmentFromStack(this);
  }
  
  public void restoreSelfArgs(Bundle paramBundle) {}
  
  public void saveSelfArgs(Bundle paramBundle) {}
  
  protected void setParentLayout(ActionBarLayout paramActionBarLayout)
  {
    if (this.parentLayout != paramActionBarLayout)
    {
      this.parentLayout = paramActionBarLayout;
      if (this.fragmentView != null)
      {
        paramActionBarLayout = (ViewGroup)this.fragmentView.getParent();
        if (paramActionBarLayout == null) {}
      }
    }
    try
    {
      paramActionBarLayout.removeView(this.fragmentView);
      if ((this.parentLayout != null) && (this.parentLayout.getContext() != this.fragmentView.getContext())) {
        this.fragmentView = null;
      }
      if (this.actionBar != null)
      {
        if ((this.parentLayout == null) || (this.parentLayout.getContext() == this.actionBar.getContext())) {
          break label199;
        }
        i = 1;
        if ((this.actionBar.getAddToContainer()) || (i != 0))
        {
          paramActionBarLayout = (ViewGroup)this.actionBar.getParent();
          if (paramActionBarLayout == null) {}
        }
      }
    }
    catch (Exception paramActionBarLayout)
    {
      try
      {
        for (;;)
        {
          paramActionBarLayout.removeView(this.actionBar);
          if (i != 0) {
            this.actionBar = null;
          }
          if ((this.parentLayout != null) && (this.actionBar == null))
          {
            this.actionBar = createActionBar(this.parentLayout.getContext());
            this.actionBar.parentFragment = this;
          }
          return;
          paramActionBarLayout = paramActionBarLayout;
          FileLog.e("tmessages", paramActionBarLayout);
        }
        label199:
        int i = 0;
      }
      catch (Exception paramActionBarLayout)
      {
        for (;;)
        {
          FileLog.e("tmessages", paramActionBarLayout);
        }
      }
    }
  }
  
  public void setVisibleDialog(Dialog paramDialog)
  {
    this.visibleDialog = paramDialog;
  }
  
  public Dialog showDialog(Dialog paramDialog)
  {
    return showDialog(paramDialog, false);
  }
  
  public Dialog showDialog(Dialog paramDialog, boolean paramBoolean)
  {
    if ((paramDialog == null) || (this.parentLayout == null) || (this.parentLayout.animationInProgress) || (this.parentLayout.startedTracking) || ((!paramBoolean) && (this.parentLayout.checkTransitionAnimation()))) {
      return null;
    }
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        try
        {
          this.visibleDialog = paramDialog;
          this.visibleDialog.setCanceledOnTouchOutside(true);
          this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymousDialogInterface)
            {
              BaseFragment.this.onDialogDismiss(BaseFragment.this.visibleDialog);
              BaseFragment.this.visibleDialog = null;
            }
          });
          this.visibleDialog.show();
          paramDialog = this.visibleDialog;
          return paramDialog;
        }
        catch (Exception paramDialog)
        {
          FileLog.e("tmessages", paramDialog);
        }
        localException = localException;
        FileLog.e("tmessages", localException);
      }
    }
    return null;
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (this.parentLayout != null) {
      this.parentLayout.startActivityForResult(paramIntent, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/BaseFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */