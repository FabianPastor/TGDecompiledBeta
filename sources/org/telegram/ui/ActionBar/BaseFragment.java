package org.telegram.ui.ActionBar;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

public class BaseFragment {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected int classGuid;
    protected int currentAccount;
    private boolean finishing;
    protected View fragmentView;
    protected boolean hasOwnBackground;
    protected boolean inPreviewMode;
    private boolean isFinished;
    protected ActionBarLayout parentLayout;
    protected boolean swipeBackEnabled = false;
    protected Dialog visibleDialog;

    public boolean canBeginSlide() {
        return true;
    }

    public View createView(Context context) {
        return null;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
    }

    public boolean onBackPressed() {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyHidden() {
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    /* Access modifiers changed, original: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        return null;
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onLowMemory() {
    }

    /* Access modifiers changed, original: protected */
    public void onRemoveFromParent() {
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
    }

    public void onResume() {
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
    }

    public void restoreSelfArgs(Bundle bundle) {
    }

    public void saveSelfArgs(Bundle bundle) {
    }

    public BaseFragment() {
        this.currentAccount = UserConfig.selectedAccount;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public BaseFragment(Bundle bundle) {
        this.currentAccount = UserConfig.selectedAccount;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.arguments = bundle;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public void setCurrentAccount(int i) {
        if (this.fragmentView == null) {
            this.currentAccount = i;
            return;
        }
        throw new IllegalStateException("trying to set current account when fragment UI already created");
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    /* Access modifiers changed, original: protected */
    public void setInPreviewMode(boolean z) {
        this.inPreviewMode = z;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            boolean z2 = false;
            if (this.inPreviewMode) {
                actionBar.setOccupyStatusBar(false);
                return;
            }
            if (VERSION.SDK_INT >= 21) {
                z2 = true;
            }
            actionBar.setOccupyStatusBar(z2);
        }
    }

    /* Access modifiers changed, original: protected */
    public void clearViews() {
        ViewGroup viewGroup;
        View view = this.fragmentView;
        if (view != null) {
            viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                try {
                    onRemoveFromParent();
                    viewGroup.removeView(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            viewGroup = (ViewGroup) actionBar.getParent();
            if (viewGroup != null) {
                try {
                    viewGroup.removeView(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    /* Access modifiers changed, original: protected */
    public void setParentLayout(ActionBarLayout actionBarLayout) {
        if (this.parentLayout != actionBarLayout) {
            this.parentLayout = actionBarLayout;
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    try {
                        onRemoveFromParent();
                        viewGroup.removeView(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                actionBarLayout = this.parentLayout;
                if (!(actionBarLayout == null || actionBarLayout.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                actionBarLayout = this.parentLayout;
                Object obj = (actionBarLayout == null || actionBarLayout.getContext() == this.actionBar.getContext()) ? null : 1;
                if (this.actionBar.getAddToContainer() || obj != null) {
                    ViewGroup viewGroup2 = (ViewGroup) this.actionBar.getParent();
                    if (viewGroup2 != null) {
                        try {
                            viewGroup2.removeView(this.actionBar);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                }
                if (obj != null) {
                    this.actionBar = null;
                }
            }
            actionBarLayout = this.parentLayout;
            if (actionBarLayout != null && this.actionBar == null) {
                this.actionBar = createActionBar(actionBarLayout.getContext());
                this.actionBar.parentFragment = this;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), true);
        actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        actionBar.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
        if (this.inPreviewMode) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public void movePreviewFragment(float f) {
        this.parentLayout.movePreviewFragment(f);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public void finishFragment() {
        finishFragment(true);
    }

    public void finishFragment(boolean z) {
        if (!this.isFinished) {
            ActionBarLayout actionBarLayout = this.parentLayout;
            if (actionBarLayout != null) {
                this.finishing = true;
                actionBarLayout.closeLastFragment(z);
            }
        }
    }

    public void removeSelfFromStack() {
        if (!this.isFinished) {
            ActionBarLayout actionBarLayout = this.parentLayout;
            if (actionBarLayout != null) {
                actionBarLayout.removeFragmentFromStack(this);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean isFinishing() {
        return this.finishing;
    }

    public void onFragmentDestroy() {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
        this.isFinished = true;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setEnabled(false);
        }
    }

    public void onPause() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public BaseFragment getFragmentForAlert(int i) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout == null || actionBarLayout.fragmentsStack.size() <= i + 1) {
            return this;
        }
        ArrayList arrayList = this.parentLayout.fragmentsStack;
        return (BaseFragment) arrayList.get((arrayList.size() - 2) - i);
    }

    public boolean presentFragmentAsPreview(BaseFragment baseFragment) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragmentAsPreview(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(baseFragment, z);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(baseFragment, z, z2, true, false);
    }

    public Activity getParentActivity() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null ? actionBarLayout.parentActivity : null;
    }

    /* Access modifiers changed, original: protected */
    public void setParentActivityTitle(CharSequence charSequence) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            parentActivity.setTitle(charSequence);
        }
    }

    public void startActivityForResult(Intent intent, int i) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.startActivityForResult(intent, i);
        }
    }

    public void dismissCurrentDialig() {
        Dialog dialog = this.visibleDialog;
        if (dialog != null) {
            try {
                dialog.dismiss();
                this.visibleDialog = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void onBeginSlide() {
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyVisible() {
        if (((AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility")).isEnabled()) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                String title = actionBar.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    setParentActivityTitle(title);
                }
            }
        }
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, null);
    }

    public Dialog showDialog(Dialog dialog, OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean z, OnDismissListener onDismissListener) {
        if (dialog != null) {
            ActionBarLayout actionBarLayout = this.parentLayout;
            if (!(actionBarLayout == null || actionBarLayout.animationInProgress || actionBarLayout.startedTracking || (!z && actionBarLayout.checkTransitionAnimation()))) {
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    this.visibleDialog = dialog;
                    this.visibleDialog.setCanceledOnTouchOutside(true);
                    this.visibleDialog.setOnDismissListener(new -$$Lambda$BaseFragment$vXTvtAK8XZpLjv4Env96FSJndOM(this, onDismissListener));
                    this.visibleDialog.show();
                    return this.visibleDialog;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
        return null;
    }

    public /* synthetic */ void lambda$showDialog$0$BaseFragment(OnDismissListener onDismissListener, DialogInterface dialogInterface) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
        onDialogDismiss(this.visibleDialog);
        this.visibleDialog = null;
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[0];
    }

    /* Access modifiers changed, original: protected */
    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    /* Access modifiers changed, original: protected */
    public MessagesController getMessagesController() {
        return getAccountInstance().getMessagesController();
    }

    /* Access modifiers changed, original: protected */
    public ContactsController getContactsController() {
        return getAccountInstance().getContactsController();
    }

    /* Access modifiers changed, original: protected */
    public DataQuery getDataQuery() {
        return getAccountInstance().getDataQuery();
    }

    /* Access modifiers changed, original: protected */
    public ConnectionsManager getConnectionsManager() {
        return getAccountInstance().getConnectionsManager();
    }

    /* Access modifiers changed, original: protected */
    public NotificationsController getNotificationsController() {
        return getAccountInstance().getNotificationsController();
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }
}
