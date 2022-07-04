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
import androidx.core.graphics.ColorUtils;
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
    protected boolean finishing;
    /* access modifiers changed from: protected */
    public boolean fragmentBeginToShow;
    /* access modifiers changed from: protected */
    public View fragmentView;
    protected boolean hasOwnBackground;
    /* access modifiers changed from: protected */
    public boolean inBubbleMode;
    protected boolean inMenuMode;
    /* access modifiers changed from: protected */
    public boolean inPreviewMode;
    protected boolean inTransitionAnimation;
    private boolean isFinished;
    /* access modifiers changed from: protected */
    public boolean isPaused;
    protected Dialog parentDialog;
    /* access modifiers changed from: protected */
    public ActionBarLayout parentLayout;
    private boolean removingFromStack;
    protected Dialog visibleDialog;

    public BaseFragment() {
        this.currentAccount = UserConfig.selectedAccount;
        this.hasOwnBackground = false;
        this.isPaused = true;
        this.inTransitionAnimation = false;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public BaseFragment(Bundle args) {
        this.currentAccount = UserConfig.selectedAccount;
        this.hasOwnBackground = false;
        this.isPaused = true;
        this.inTransitionAnimation = false;
        this.arguments = args;
        this.classGuid = ConnectionsManager.generateClassGuid();
    }

    public void setCurrentAccount(int account) {
        if (this.fragmentView == null) {
            this.currentAccount = account;
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

    public View createView(Context context) {
        return null;
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

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return true;
    }

    public void setInBubbleMode(boolean value) {
        this.inBubbleMode = value;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode;
    }

    public boolean getInPassivePreviewMode() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.isInPassivePreviewMode();
    }

    /* access modifiers changed from: protected */
    public void setInPreviewMode(boolean value) {
        this.inPreviewMode = value;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            boolean z = false;
            if (value) {
                actionBar2.setOccupyStatusBar(false);
                return;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                z = true;
            }
            actionBar2.setOccupyStatusBar(z);
        }
    }

    /* access modifiers changed from: protected */
    public void setInMenuMode(boolean value) {
        this.inMenuMode = value;
    }

    /* access modifiers changed from: protected */
    public void onPreviewOpenAnimationEnd() {
    }

    /* access modifiers changed from: protected */
    public boolean hideKeyboardOnShow() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeViewInLayout(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            ViewGroup parent2 = (ViewGroup) actionBar2.getParent();
            if (parent2 != null) {
                try {
                    parent2.removeViewInLayout(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
    }

    public void setParentFragment(BaseFragment fragment) {
        setParentLayout(fragment.parentLayout);
        this.fragmentView = createView(this.parentLayout.getContext());
    }

    /* access modifiers changed from: protected */
    public void setParentLayout(ActionBarLayout layout) {
        ViewGroup parent;
        if (this.parentLayout != layout) {
            this.parentLayout = layout;
            boolean differentParent = true;
            this.inBubbleMode = layout != null && layout.isInBubbleMode();
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup parent2 = (ViewGroup) view.getParent();
                if (parent2 != null) {
                    try {
                        onRemoveFromParent();
                        parent2.removeViewInLayout(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                ActionBarLayout actionBarLayout = this.parentLayout;
                if (!(actionBarLayout == null || actionBarLayout.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (actionBarLayout2 == null || actionBarLayout2.getContext() == this.actionBar.getContext()) {
                    differentParent = false;
                }
                if ((this.actionBar.shouldAddToContainer() || differentParent) && (parent = (ViewGroup) this.actionBar.getParent()) != null) {
                    try {
                        parent.removeViewInLayout(this.actionBar);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                if (differentParent) {
                    this.actionBar = null;
                }
            }
            ActionBarLayout actionBarLayout3 = this.parentLayout;
            if (actionBarLayout3 != null && this.actionBar == null) {
                ActionBar createActionBar = createActionBar(actionBarLayout3.getContext());
                this.actionBar = createActionBar;
                if (createActionBar != null) {
                    createActionBar.parentFragment = this;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar2 = new ActionBar(context, getResourceProvider());
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

    public void movePreviewFragment(float dy) {
        this.parentLayout.movePreviewFragment(dy);
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

    public void finishFragment(boolean animated) {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            this.finishing = true;
            actionBarLayout.closeLastFragment(animated);
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

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        getMessagesStorage().cancelTasksForGuid(this.classGuid);
        boolean z = true;
        this.isFinished = true;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.setEnabled(false);
        }
        if (hasForceLightStatusBar() && !AndroidUtilities.isTablet() && getParentLayout().getLastFragment() == this && getParentActivity() != null && !this.finishing) {
            Window window = getParentActivity().getWindow();
            if (Theme.getColor("actionBarDefault") != -1) {
                z = false;
            }
            AndroidUtilities.setLightStatusBar(window, z);
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void resumeDelayedFragmentAnimation() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.resumeDelayedFragmentAnimation();
        }
    }

    public void onUserLeaveHint() {
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

    public BaseFragment getFragmentForAlert(int offset) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout == null || actionBarLayout.fragmentsStack.size() <= offset + 1) {
            return this;
        }
        return this.parentLayout.fragmentsStack.get((this.parentLayout.fragmentsStack.size() - 2) - offset);
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

    public boolean isLastFragment() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && !actionBarLayout.fragmentsStack.isEmpty() && this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this;
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
            boolean r0 = r0.presentFragmentAsPreview(r2)
            if (r0 == 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragmentAsPreview(org.telegram.ui.ActionBar.BaseFragment):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.parentLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean presentFragmentAsPreviewWithMenu(org.telegram.ui.ActionBar.BaseFragment r2, org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout r3) {
        /*
            r1 = this;
            boolean r0 = r1.allowPresentFragment()
            if (r0 == 0) goto L_0x0012
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.parentLayout
            if (r0 == 0) goto L_0x0012
            boolean r0 = r0.presentFragmentAsPreviewWithMenu(r2, r3)
            if (r0 == 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.presentFragmentAsPreviewWithMenu(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout):boolean");
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
            boolean r0 = r0.presentFragment(r2)
            if (r0 == 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            return r0
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
            boolean r0 = r0.presentFragment(r2, r3)
            if (r0 == 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            return r0
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
            boolean r0 = r1.presentFragment(r2, r3, r4, r5, r6, r7)
            if (r0 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
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
    public void setParentActivityTitle(CharSequence title) {
        Activity activity = getParentActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.startActivityForResult(intent, requestCode);
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

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public boolean canBeginSlide() {
        return true;
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
    public void onSlideProgress(boolean isOpen, float progress) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        this.inTransitionAnimation = true;
        if (isOpen) {
            this.fragmentBeginToShow = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        this.inTransitionAnimation = false;
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

    /* access modifiers changed from: protected */
    public int getPreviewHeight() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        return null;
    }

    public void onLowMemory() {
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, (DialogInterface.OnDismissListener) null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, DialogInterface.OnDismissListener onDismissListener) {
        ActionBarLayout actionBarLayout;
        if (dialog == null || (actionBarLayout = this.parentLayout) == null || actionBarLayout.animationInProgress || this.parentLayout.startedTracking || (!allowInTransition && this.parentLayout.checkTransitionAnimation())) {
            return null;
        }
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
            return null;
        }
    }

    /* renamed from: lambda$showDialog$0$org-telegram-ui-ActionBar-BaseFragment  reason: not valid java name */
    public /* synthetic */ void m2561lambda$showDialog$0$orgtelegramuiActionBarBaseFragment(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialog1) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog1);
        }
        onDialogDismiss((Dialog) dialog1);
        if (dialog1 == this.visibleDialog) {
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(float y) {
    }

    /* access modifiers changed from: protected */
    public void onPanTransitionStart() {
    }

    /* access modifiers changed from: protected */
    public void onPanTransitionEnd() {
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

    public void setFragmentPanTranslationOffset(int offset) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setFragmentPanTranslationOffset(offset);
        }
    }

    public void saveKeyboardPositionBeforeTransition() {
    }

    /* access modifiers changed from: protected */
    public Animator getCustomSlideTransition(boolean topFragment, boolean backAnimation, float distanceToMove) {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean shouldOverrideSlideTransition(boolean topFragment, boolean backAnimation) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void prepareFragmentToSlide(boolean topFragment, boolean beginSlide) {
    }

    public void setProgressToDrawerOpened(float v) {
    }

    public ActionBarLayout[] showAsSheet(BaseFragment fragment) {
        if (getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayout = {new ActionBarLayout(getParentActivity())};
        AnonymousClass1 r4 = new BottomSheet(getParentActivity(), true, actionBarLayout, fragment) {
            final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
            final /* synthetic */ BaseFragment val$fragment;

            {
                this.val$actionBarLayout = r8;
                this.val$fragment = r9;
                r8[0].init(new ArrayList());
                r8[0].addFragmentToStack(r9);
                r8[0].showLastFragment();
                r8[0].setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
                this.containerView = r8[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new BaseFragment$1$$ExternalSyntheticLambda0(r9));
            }

            /* access modifiers changed from: protected */
            public boolean canDismissWithSwipe() {
                return false;
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
        fragment.setParentDialog(r4);
        r4.show();
        return actionBarLayout;
    }

    public int getThemedColor(String key) {
        return Theme.getColor(key, getResourceProvider());
    }

    public Drawable getThemedDrawable(String key) {
        return Theme.getThemeDrawable(key);
    }

    public boolean hasForceLightStatusBar() {
        return false;
    }

    public int getNavigationBarColor() {
        return Theme.getColor("windowBackgroundGray");
    }

    public void setNavigationBarColor(int color) {
        Activity activity = getParentActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= 26 && window != null && window.getNavigationBarColor() != color) {
                window.setNavigationBarColor(color);
                AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(color) >= 0.721f);
            }
        }
    }

    public boolean isBeginToShow() {
        return this.fragmentBeginToShow;
    }

    private void setParentDialog(Dialog dialog) {
        this.parentDialog = dialog;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean allowPresentFragment() {
        return true;
    }

    public boolean isRemovingFromStack() {
        return this.removingFromStack;
    }

    public void setRemovingFromStack(boolean b) {
        this.removingFromStack = b;
    }

    public boolean isLightStatusBar() {
        int color;
        if (hasForceLightStatusBar() && !Theme.getCurrentTheme().isDark()) {
            return true;
        }
        Theme.ResourcesProvider resourcesProvider = getResourceProvider();
        String key = "actionBarDefault";
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null && actionBar2.isActionModeShowed()) {
            key = "actionBarActionModeDefault";
        }
        if (resourcesProvider != null) {
            color = resourcesProvider.getColorOrDefault(key);
        } else {
            color = Theme.getColor(key, (boolean[]) null, true);
        }
        if (ColorUtils.calculateLuminance(color) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
