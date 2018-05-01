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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

public class BaseFragment
{
  protected ActionBar actionBar;
  protected Bundle arguments;
  protected int classGuid = 0;
  protected int currentAccount = UserConfig.selectedAccount;
  protected View fragmentView;
  protected boolean hasOwnBackground = false;
  private boolean isFinished = false;
  protected ActionBarLayout parentLayout;
  protected boolean swipeBackEnabled = true;
  protected Dialog visibleDialog = null;
  
  public BaseFragment()
  {
    this.classGuid = ConnectionsManager.generateClassGuid();
  }
  
  public BaseFragment(Bundle paramBundle)
  {
    this.arguments = paramBundle;
    this.classGuid = ConnectionsManager.generateClassGuid();
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
      onRemoveFromParent();
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
        FileLog.e(localException1);
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext);
    paramContext.setBackgroundColor(Theme.getColor("actionBarDefault"));
    paramContext.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
    paramContext.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), true);
    paramContext.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
    paramContext.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
    return paramContext;
  }
  
  public View createView(Context paramContext)
  {
    return null;
  }
  
  public void dismissCurrentDialig()
  {
    if (this.visibleDialog == null) {}
    for (;;)
    {
      return;
      try
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public boolean dismissDialogOnPause(Dialog paramDialog)
  {
    return true;
  }
  
  public boolean extendActionMode(Menu paramMenu)
  {
    return false;
  }
  
  public void finishFragment()
  {
    finishFragment(true);
  }
  
  public void finishFragment(boolean paramBoolean)
  {
    if ((this.isFinished) || (this.parentLayout == null)) {}
    for (;;)
    {
      return;
      this.parentLayout.closeLastFragment(paramBoolean);
    }
  }
  
  public ActionBar getActionBar()
  {
    return this.actionBar;
  }
  
  public Bundle getArguments()
  {
    return this.arguments;
  }
  
  public int getCurrentAccount()
  {
    return this.currentAccount;
  }
  
  public BaseFragment getFragmentForAlert(int paramInt)
  {
    if ((this.parentLayout == null) || (this.parentLayout.fragmentsStack.size() <= paramInt + 1)) {}
    for (BaseFragment localBaseFragment = this;; localBaseFragment = (BaseFragment)this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2 - paramInt)) {
      return localBaseFragment;
    }
  }
  
  public View getFragmentView()
  {
    return this.fragmentView;
  }
  
  public Activity getParentActivity()
  {
    if (this.parentLayout != null) {}
    for (Activity localActivity = this.parentLayout.parentActivity;; localActivity = null) {
      return localActivity;
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[0];
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
        FileLog.e(localException);
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
    ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
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
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  protected void onRemoveFromParent() {}
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {}
  
  public void onResume() {}
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public boolean presentFragment(BaseFragment paramBaseFragment)
  {
    if ((this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    if ((this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean))) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = true;
    if ((this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true))) {}
    for (paramBoolean1 = bool;; paramBoolean1 = false) {
      return paramBoolean1;
    }
  }
  
  public void removeSelfFromStack()
  {
    if ((this.isFinished) || (this.parentLayout == null)) {}
    for (;;)
    {
      return;
      this.parentLayout.removeFragmentFromStack(this);
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle) {}
  
  public void saveSelfArgs(Bundle paramBundle) {}
  
  public void setCurrentAccount(int paramInt)
  {
    if (this.fragmentView != null) {
      throw new IllegalStateException("trying to set current account when fragment UI already created");
    }
    this.currentAccount = paramInt;
  }
  
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
      onRemoveFromParent();
      paramActionBarLayout.removeView(this.fragmentView);
      if ((this.parentLayout != null) && (this.parentLayout.getContext() != this.fragmentView.getContext())) {
        this.fragmentView = null;
      }
      if (this.actionBar != null)
      {
        if ((this.parentLayout == null) || (this.parentLayout.getContext() == this.actionBar.getContext())) {
          break label201;
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
          FileLog.e(paramActionBarLayout);
        }
        label201:
        int i = 0;
      }
      catch (Exception paramActionBarLayout)
      {
        for (;;)
        {
          FileLog.e(paramActionBarLayout);
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
    return showDialog(paramDialog, false, null);
  }
  
  public Dialog showDialog(Dialog paramDialog, DialogInterface.OnDismissListener paramOnDismissListener)
  {
    return showDialog(paramDialog, false, paramOnDismissListener);
  }
  
  public Dialog showDialog(Dialog paramDialog, boolean paramBoolean, DialogInterface.OnDismissListener paramOnDismissListener)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramDialog != null)
    {
      localObject2 = localObject1;
      if (this.parentLayout != null)
      {
        localObject2 = localObject1;
        if (!this.parentLayout.animationInProgress)
        {
          localObject2 = localObject1;
          if (!this.parentLayout.startedTracking)
          {
            if ((paramBoolean) || (!this.parentLayout.checkTransitionAnimation())) {
              break label71;
            }
            localObject2 = localObject1;
          }
        }
      }
    }
    for (;;)
    {
      return (Dialog)localObject2;
      try
      {
        label71:
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
            paramDialog = this.visibleDialog;
            localObject2 = new org/telegram/ui/ActionBar/BaseFragment$1;
            ((1)localObject2).<init>(this, paramOnDismissListener);
            paramDialog.setOnDismissListener((DialogInterface.OnDismissListener)localObject2);
            this.visibleDialog.show();
            localObject2 = this.visibleDialog;
          }
          catch (Exception paramDialog)
          {
            FileLog.e(paramDialog);
            Object localObject3 = localObject1;
          }
          localException = localException;
          FileLog.e(localException);
        }
      }
    }
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