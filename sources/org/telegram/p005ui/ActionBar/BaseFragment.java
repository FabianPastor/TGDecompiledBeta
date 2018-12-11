package org.telegram.p005ui.ActionBar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.ui.ActionBar.BaseFragment */
public class BaseFragment {
    protected CLASSNAMEActionBar actionBar;
    protected Bundle arguments;
    protected int classGuid;
    protected int currentAccount;
    private boolean finishing;
    protected View fragmentView;
    protected boolean hasOwnBackground;
    protected boolean inPreviewMode;
    private boolean isFinished;
    protected ActionBarLayout parentLayout;
    protected boolean swipeBackEnabled;
    protected Dialog visibleDialog;

    public BaseFragment() {
        this.currentAccount = UserConfig.selectedAccount;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public BaseFragment(Bundle args) {
        this.currentAccount = UserConfig.selectedAccount;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.arguments = args;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public void setCurrentAccount(int account) {
        if (this.fragmentView != null) {
            throw new IllegalStateException("trying to set current account when fragment UI already created");
        }
        this.currentAccount = account;
    }

    public CLASSNAMEActionBar getActionBar() {
        return this.actionBar;
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public View createView(Context context) {
        return null;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    protected void setInPreviewMode(boolean value) {
        boolean z = false;
        this.inPreviewMode = value;
        if (this.actionBar == null) {
            return;
        }
        if (this.inPreviewMode) {
            this.actionBar.setOccupyStatusBar(false);
            return;
        }
        CLASSNAMEActionBar CLASSNAMEActionBar = this.actionBar;
        if (VERSION.SDK_INT >= 21) {
            z = true;
        }
        CLASSNAMEActionBar.setOccupyStatusBar(z);
    }

    protected void clearViews() {
        ViewGroup parent;
        if (this.fragmentView != null) {
            parent = (ViewGroup) this.fragmentView.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(this.fragmentView);
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
            }
            this.fragmentView = null;
        }
        if (this.actionBar != null) {
            parent = (ViewGroup) this.actionBar.getParent();
            if (parent != null) {
                try {
                    parent.removeView(this.actionBar);
                } catch (Throwable e2) {
                    FileLog.m14e(e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    protected void onRemoveFromParent() {
    }

    protected void setParentLayout(ActionBarLayout layout) {
        if (this.parentLayout != layout) {
            ViewGroup parent;
            this.parentLayout = layout;
            if (this.fragmentView != null) {
                parent = (ViewGroup) this.fragmentView.getParent();
                if (parent != null) {
                    try {
                        onRemoveFromParent();
                        parent.removeView(this.fragmentView);
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                }
                if (!(this.parentLayout == null || this.parentLayout.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                boolean differentParent = (this.parentLayout == null || this.parentLayout.getContext() == this.actionBar.getContext()) ? false : true;
                if (this.actionBar.getAddToContainer() || differentParent) {
                    parent = (ViewGroup) this.actionBar.getParent();
                    if (parent != null) {
                        try {
                            parent.removeView(this.actionBar);
                        } catch (Throwable e2) {
                            FileLog.m14e(e2);
                        }
                    }
                }
                if (differentParent) {
                    this.actionBar = null;
                }
            }
            if (this.parentLayout != null && this.actionBar == null) {
                this.actionBar = createActionBar(this.parentLayout.getContext());
                this.actionBar.parentFragment = this;
            }
        }
    }

    protected CLASSNAMEActionBar createActionBar(Context context) {
        CLASSNAMEActionBar actionBar = new CLASSNAMEActionBar(context);
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public void movePreviewFragment(float dy) {
        this.parentLayout.movePreviewFragment(dy);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public void finishFragment() {
        finishFragment(true);
    }

    public void finishFragment(boolean animated) {
        if (!this.isFinished && this.parentLayout != null) {
            this.finishing = true;
            this.parentLayout.closeLastFragment(animated);
        }
    }

    public void removeSelfFromStack() {
        if (!this.isFinished && this.parentLayout != null) {
            this.parentLayout.removeFragmentFromStack(this);
        }
    }

    protected boolean isFinishing() {
        return this.finishing;
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
        this.isFinished = true;
        if (this.actionBar != null) {
            this.actionBar.setEnabled(false);
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onResume() {
    }

    public void onPause() {
        if (this.actionBar != null) {
            this.actionBar.onPause();
        }
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
    }

    public BaseFragment getFragmentForAlert(int offset) {
        if (this.parentLayout == null || this.parentLayout.fragmentsStack.size() <= offset + 1) {
            return this;
        }
        return (BaseFragment) this.parentLayout.fragmentsStack.get((this.parentLayout.fragmentsStack.size() - 2) - offset);
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public boolean onBackPressed() {
        return true;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
    }

    public void saveSelfArgs(Bundle args) {
    }

    public void restoreSelfArgs(Bundle args) {
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        return this.parentLayout != null && this.parentLayout.presentFragmentAsPreview(fragment);
    }

    public boolean presentFragment(BaseFragment fragment) {
        return this.parentLayout != null && this.parentLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return this.parentLayout != null && this.parentLayout.presentFragment(fragment, removeLast);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.parentLayout != null && this.parentLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public Activity getParentActivity() {
        if (this.parentLayout != null) {
            return this.parentLayout.parentActivity;
        }
        return null;
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.parentLayout != null) {
            this.parentLayout.startActivityForResult(intent, requestCode);
        }
    }

    public void dismissCurrentDialig() {
        if (this.visibleDialog != null) {
            try {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public void onBeginSlide() {
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
        if (this.actionBar != null) {
            this.actionBar.onPause();
        }
    }

    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
    }

    protected void onBecomeFullyVisible() {
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        return null;
    }

    public void onLowMemory() {
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, null);
    }

    public Dialog showDialog(Dialog dialog, OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, OnDismissListener onDismissListener) {
        Dialog dialog2 = null;
        if (dialog == null || this.parentLayout == null || this.parentLayout.animationInProgress || this.parentLayout.startedTracking) {
            return dialog2;
        }
        if (!allowInTransition && this.parentLayout.checkTransitionAnimation()) {
            return dialog2;
        }
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
        try {
            this.visibleDialog = dialog;
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new BaseFragment$$Lambda$0(this, onDismissListener));
            this.visibleDialog.show();
            return this.visibleDialog;
        } catch (Throwable e2) {
            FileLog.m14e(e2);
            return dialog2;
        }
    }

    final /* synthetic */ void lambda$showDialog$0$BaseFragment(OnDismissListener onDismissListener, DialogInterface dialog1) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog1);
        }
        onDialogDismiss(this.visibleDialog);
        this.visibleDialog = null;
    }

    protected void onDialogDismiss(Dialog dialog) {
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[0];
    }
}
