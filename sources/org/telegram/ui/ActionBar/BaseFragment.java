package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public abstract class BaseFragment {
    /* access modifiers changed from: protected */
    public ActionBar actionBar;
    /* access modifiers changed from: protected */
    public Bundle arguments;
    /* access modifiers changed from: protected */
    public int classGuid;
    /* access modifiers changed from: protected */
    public int currentAccount;
    private boolean finishing;
    protected boolean fragmentBeginToShow;
    /* access modifiers changed from: protected */
    public View fragmentView;
    protected boolean hasOwnBackground;
    protected boolean inBubbleMode;
    protected boolean inMenuMode;
    /* access modifiers changed from: protected */
    public boolean inPreviewMode;
    private boolean isFinished;
    protected boolean isPaused;
    protected Dialog parentDialog;
    /* access modifiers changed from: protected */
    public ActionBarLayout parentLayout;
    protected Dialog visibleDialog;

    /* access modifiers changed from: protected */
    public boolean allowPresentFragment() {
        return true;
    }

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

    /* access modifiers changed from: protected */
    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        return null;
    }

    /* access modifiers changed from: protected */
    public int getPreviewHeight() {
        return -1;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean hideKeyboardOnShow() {
        return true;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return true;
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
    }

    public boolean onBackPressed() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onLowMemory() {
    }

    /* access modifiers changed from: protected */
    public void onPreviewOpenAnimationEnd() {
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
    }

    /* access modifiers changed from: protected */
    public void onSlideProgress(boolean z, float f) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean z, float f) {
    }

    /* access modifiers changed from: protected */
    public void prepareFragmentToSlide(boolean z, boolean z2) {
    }

    public void saveKeyboardPositionBeforeTransition() {
    }

    public void saveSelfArgs(Bundle bundle) {
    }

    public void setProgressToDrawerOpened(float f) {
    }

    public BaseFragment() {
        this.currentAccount = UserConfig.selectedAccount;
        this.hasOwnBackground = false;
        this.isPaused = true;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public BaseFragment(Bundle bundle) {
        this.currentAccount = UserConfig.selectedAccount;
        this.hasOwnBackground = false;
        this.isPaused = true;
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

    public int getClassGuid() {
        return this.classGuid;
    }

    public void setInBubbleMode(boolean z) {
        this.inBubbleMode = z;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    /* access modifiers changed from: protected */
    public void setInPreviewMode(boolean z) {
        this.inPreviewMode = z;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            boolean z2 = false;
            if (z) {
                actionBar2.setOccupyStatusBar(false);
                return;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                z2 = true;
            }
            actionBar2.setOccupyStatusBar(z2);
        }
    }

    /* access modifiers changed from: protected */
    public void setInMenuMode(boolean z) {
        this.inMenuMode = z;
    }

    /* access modifiers changed from: protected */
    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                try {
                    onRemoveFromParent();
                    viewGroup.removeViewInLayout(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            ViewGroup viewGroup2 = (ViewGroup) actionBar2.getParent();
            if (viewGroup2 != null) {
                try {
                    viewGroup2.removeViewInLayout(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    public void setParentFragment(BaseFragment baseFragment) {
        setParentLayout(baseFragment.parentLayout);
        this.fragmentView = createView(this.parentLayout.getContext());
    }

    /* access modifiers changed from: protected */
    public void setParentLayout(ActionBarLayout actionBarLayout) {
        ViewGroup viewGroup;
        if (this.parentLayout != actionBarLayout) {
            this.parentLayout = actionBarLayout;
            boolean z = true;
            this.inBubbleMode = actionBarLayout != null && actionBarLayout.isInBubbleMode();
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup viewGroup2 = (ViewGroup) view.getParent();
                if (viewGroup2 != null) {
                    try {
                        onRemoveFromParent();
                        viewGroup2.removeViewInLayout(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (!(actionBarLayout2 == null || actionBarLayout2.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                ActionBarLayout actionBarLayout3 = this.parentLayout;
                if (actionBarLayout3 == null || actionBarLayout3.getContext() == this.actionBar.getContext()) {
                    z = false;
                }
                if ((this.actionBar.shouldAddToContainer() || z) && (viewGroup = (ViewGroup) this.actionBar.getParent()) != null) {
                    try {
                        viewGroup.removeViewInLayout(this.actionBar);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                if (z) {
                    this.actionBar = null;
                }
            }
            ActionBarLayout actionBarLayout4 = this.parentLayout;
            if (actionBarLayout4 != null && this.actionBar == null) {
                ActionBar createActionBar = createActionBar(actionBarLayout4.getContext());
                this.actionBar = createActionBar;
                createActionBar.parentFragment = this;
            }
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar2 = new ActionBar(context);
        actionBar2.setBackgroundColor(getThemedColor("actionBarDefault"));
        actionBar2.setItemsBackgroundColor(getThemedColor("actionBarDefaultSelector"), false);
        actionBar2.setItemsBackgroundColor(getThemedColor("actionBarActionModeDefaultSelector"), true);
        actionBar2.setItemsColor(getThemedColor("actionBarDefaultIcon"), false);
        actionBar2.setItemsColor(getThemedColor("actionBarActionModeDefaultIcon"), true);
        if (this.inPreviewMode || this.inBubbleMode) {
            actionBar2.setOccupyStatusBar(false);
        }
        return actionBar2;
    }

    public void movePreviewFragment(float f) {
        this.parentLayout.movePreviewFragment(f);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public void finishFragment() {
        Dialog dialog = this.parentDialog;
        if (dialog != null) {
            dialog.dismiss();
        } else {
            finishFragment(true);
        }
    }

    public void finishFragment(boolean z) {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            this.finishing = true;
            actionBarLayout.closeLastFragment(z);
        }
    }

    public void removeSelfFromStack() {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            Dialog dialog = this.parentDialog;
            if (dialog != null) {
                dialog.dismiss();
            } else {
                actionBarLayout.removeFragmentFromStack(this);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isFinishing() {
        return this.finishing;
    }

    public void onFragmentDestroy() {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        getMessagesStorage().cancelTasksForGuid(this.classGuid);
        this.isFinished = true;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.setEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    public void resumeDelayedFragmentAnimation() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.resumeDelayedFragmentAnimation();
        }
    }

    public void onResume() {
        this.isPaused = false;
    }

    public void onPause() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.onPause();
        }
        this.isPaused = true;
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public BaseFragment getFragmentForAlert(int i) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout == null || actionBarLayout.fragmentsStack.size() <= i + 1) {
            return this;
        }
        ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
        return arrayList.get((arrayList.size() - 2) - i);
    }

    public boolean isLastFragment() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null && !actionBarLayout.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
            if (arrayList.get(arrayList.size() - 1) == this) {
                return true;
            }
        }
        return false;
    }

    public ActionBarLayout getParentLayout() {
        return this.parentLayout;
    }

    public FrameLayout getLayoutContainer() {
        View view = this.fragmentView;
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof FrameLayout) {
            return (FrameLayout) parent;
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragmentAsPreview(org.telegram.ui.ActionBar.BaseFragment r2) {
        /*
            r1 = this;
            boolean r0 = r1.allowPresentFragment()
            if (r0 == 0) goto L_0x0012
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.parentLayout
            if (r0 == 0) goto L_0x0012
            boolean r2 = r0.presentFragmentAsPreview(r2)
            if (r2 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0013
        L_0x0012:
            r2 = 0
        L_0x0013:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragmentAsPreview(org.telegram.ui.ActionBar.BaseFragment):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragmentAsPreviewWithMenu(org.telegram.ui.ActionBar.BaseFragment r2, android.view.View r3) {
        /*
            r1 = this;
            boolean r0 = r1.allowPresentFragment()
            if (r0 == 0) goto L_0x0012
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.parentLayout
            if (r0 == 0) goto L_0x0012
            boolean r2 = r0.presentFragmentAsPreviewWithMenu(r2, r3)
            if (r2 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0013
        L_0x0012:
            r2 = 0
        L_0x0013:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragmentAsPreviewWithMenu(org.telegram.ui.ActionBar.BaseFragment, android.view.View):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragment(org.telegram.ui.ActionBar.BaseFragment r2) {
        /*
            r1 = this;
            boolean r0 = r1.allowPresentFragment()
            if (r0 == 0) goto L_0x0012
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.parentLayout
            if (r0 == 0) goto L_0x0012
            boolean r2 = r0.presentFragment(r2)
            if (r2 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0013
        L_0x0012:
            r2 = 0
        L_0x0013:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragment(org.telegram.ui.ActionBar.BaseFragment):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragment(org.telegram.ui.ActionBar.BaseFragment r2, boolean r3) {
        /*
            r1 = this;
            boolean r0 = r1.allowPresentFragment()
            if (r0 == 0) goto L_0x0012
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.parentLayout
            if (r0 == 0) goto L_0x0012
            boolean r2 = r0.presentFragment(r2, r3)
            if (r2 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0013
        L_0x0012:
            r2 = 0
        L_0x0013:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragment(org.telegram.ui.ActionBar.BaseFragment, boolean):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = r8.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragment(org.telegram.ui.ActionBar.BaseFragment r9, boolean r10, boolean r11) {
        /*
            r8 = this;
            boolean r0 = r8.allowPresentFragment()
            if (r0 == 0) goto L_0x0018
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.parentLayout
            if (r1 == 0) goto L_0x0018
            r5 = 1
            r6 = 0
            r7 = 0
            r2 = r9
            r3 = r10
            r4 = r11
            boolean r9 = r1.presentFragment(r2, r3, r4, r5, r6, r7)
            if (r9 == 0) goto L_0x0018
            r9 = 1
            goto L_0x0019
        L_0x0018:
            r9 = 0
        L_0x0019:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragment(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean):boolean");
    }

    public Activity getParentActivity() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            return actionBarLayout.parentActivity;
        }
        return null;
    }

    /* access modifiers changed from: protected */
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

    public void dismissCurrentDialog() {
        Dialog dialog = this.visibleDialog;
        if (dialog != null) {
            try {
                dialog.dismiss();
                this.visibleDialog = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onBeginSlide() {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            this.fragmentBeginToShow = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        ActionBar actionBar2;
        if (((AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility")).isEnabled() && (actionBar2 = getActionBar()) != null) {
            String title = actionBar2.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setParentActivityTitle(title);
            }
        }
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, (DialogInterface.OnDismissListener) null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean z, DialogInterface.OnDismissListener onDismissListener) {
        ActionBarLayout actionBarLayout;
        if (dialog != null && (actionBarLayout = this.parentLayout) != null && !actionBarLayout.animationInProgress && !actionBarLayout.startedTracking && (z || !actionBarLayout.checkTransitionAnimation())) {
            try {
                Dialog dialog2 = this.visibleDialog;
                if (dialog2 != null) {
                    dialog2.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                this.visibleDialog = dialog;
                dialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new BaseFragment$$ExternalSyntheticLambda0(this, onDismissListener));
                this.visibleDialog.show();
                return this.visibleDialog;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$0(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialogInterface) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
        onDialogDismiss((Dialog) dialogInterface);
        if (dialogInterface == this.visibleDialog) {
            this.visibleDialog = null;
        }
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    public MessagesController getMessagesController() {
        return getAccountInstance().getMessagesController();
    }

    /* access modifiers changed from: protected */
    public ContactsController getContactsController() {
        return getAccountInstance().getContactsController();
    }

    public MediaDataController getMediaDataController() {
        return getAccountInstance().getMediaDataController();
    }

    public ConnectionsManager getConnectionsManager() {
        return getAccountInstance().getConnectionsManager();
    }

    public LocationController getLocationController() {
        return getAccountInstance().getLocationController();
    }

    /* access modifiers changed from: protected */
    public NotificationsController getNotificationsController() {
        return getAccountInstance().getNotificationsController();
    }

    public MessagesStorage getMessagesStorage() {
        return getAccountInstance().getMessagesStorage();
    }

    public SendMessagesHelper getSendMessagesHelper() {
        return getAccountInstance().getSendMessagesHelper();
    }

    public FileLoader getFileLoader() {
        return getAccountInstance().getFileLoader();
    }

    /* access modifiers changed from: protected */
    public SecretChatHelper getSecretChatHelper() {
        return getAccountInstance().getSecretChatHelper();
    }

    /* access modifiers changed from: protected */
    public DownloadController getDownloadController() {
        return getAccountInstance().getDownloadController();
    }

    /* access modifiers changed from: protected */
    public SharedPreferences getNotificationsSettings() {
        return getAccountInstance().getNotificationsSettings();
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    public MediaController getMediaController() {
        return MediaController.getInstance();
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }

    public void setFragmentPanTranslationOffset(int i) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setFragmentPanTranslationOffset(i);
        }
    }

    public ActionBarLayout[] showAsSheet(BaseFragment baseFragment) {
        if (getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayoutArr = {new ActionBarLayout(getParentActivity())};
        AnonymousClass1 r1 = new BottomSheet(this, getParentActivity(), true, actionBarLayoutArr, baseFragment) {
            final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
            final /* synthetic */ BaseFragment val$fragment;

            /* access modifiers changed from: protected */
            public boolean canDismissWithSwipe() {
                return false;
            }

            {
                this.val$actionBarLayout = r4;
                this.val$fragment = r5;
                r4[0].init(new ArrayList());
                r4[0].addFragmentToStack(r5);
                r4[0].showLastFragment();
                ActionBarLayout actionBarLayout = r4[0];
                int i = this.backgroundPaddingLeft;
                actionBarLayout.setPadding(i, 0, i, 0);
                this.containerView = r4[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new BaseFragment$1$$ExternalSyntheticLambda0(r5));
            }

            public void onBackPressed() {
                ActionBarLayout[] actionBarLayoutArr = this.val$actionBarLayout;
                if (actionBarLayoutArr[0] == null || actionBarLayoutArr[0].fragmentsStack.size() <= 1) {
                    super.onBackPressed();
                } else {
                    this.val$actionBarLayout[0].onBackPressed();
                }
            }

            public void dismiss() {
                super.dismiss();
                this.val$actionBarLayout[0] = null;
            }
        };
        baseFragment.setParentDialog(r1);
        r1.show();
        return actionBarLayoutArr;
    }

    public int getThemedColor(String str) {
        return Theme.getColor(str, getResourceProvider());
    }

    public Drawable getThemedDrawable(String str) {
        return Theme.getThemeDrawable(str);
    }

    public int getNavigationBarColor() {
        return Theme.getColor("windowBackgroundGray");
    }

    public void setNavigationBarColor(int i) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            Window window = parentActivity.getWindow();
            if (Build.VERSION.SDK_INT >= 26 && window != null && window.getNavigationBarColor() != i) {
                window.setNavigationBarColor(i);
                AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(i) >= 0.721f);
            }
        }
    }

    public boolean isBeginToShow() {
        return this.fragmentBeginToShow;
    }

    private void setParentDialog(Dialog dialog) {
        this.parentDialog = dialog;
    }
}
