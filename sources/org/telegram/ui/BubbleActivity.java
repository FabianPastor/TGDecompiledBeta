package org.telegram.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.ThemeEditorView;
/* loaded from: classes3.dex */
public class BubbleActivity extends BasePermissionsActivity implements INavigationLayout.INavigationLayoutDelegate {
    private INavigationLayout actionBarLayout;
    private long dialogId;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private Runnable lockRunnable;
    private ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private Intent passcodeSaveIntent;
    private int passcodeSaveIntentAccount;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private int passcodeSaveIntentState;
    private PasscodeView passcodeView;

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ boolean needAddFragmentToStack(BaseFragment baseFragment, INavigationLayout iNavigationLayout) {
        return INavigationLayout.INavigationLayoutDelegate.CC.$default$needAddFragmentToStack(this, baseFragment, iNavigationLayout);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout) {
        return INavigationLayout.INavigationLayoutDelegate.CC.$default$needPresentFragment(this, baseFragment, z, z2, iNavigationLayout);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ boolean needPresentFragment(INavigationLayout iNavigationLayout, INavigationLayout.NavigationParams navigationParams) {
        boolean needPresentFragment;
        needPresentFragment = needPresentFragment(navigationParams.fragment, navigationParams.removeLast, navigationParams.noAnimation, iNavigationLayout);
        return needPresentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ void onMeasureOverride(int[] iArr) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onMeasureOverride(this, iArr);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ boolean onPreIme() {
        return INavigationLayout.INavigationLayoutDelegate.CC.$default$onPreIme(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onRebuildAllFragments(this, iNavigationLayout, z);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ void onThemeProgress(float f) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onThemeProgress(this, f);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        super.onCreate(bundle);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        INavigationLayout newLayout = INavigationLayout.CC.newLayout(this);
        this.actionBarLayout = newLayout;
        newLayout.setInBubbleMode(true);
        this.actionBarLayout.setRemoveActionBarExtraHeight(true);
        DrawerLayoutContainer drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer = drawerLayoutContainer;
        drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        RelativeLayout relativeLayout = new RelativeLayout(this);
        this.drawerLayoutContainer.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        relativeLayout.addView(this.actionBarLayout.getView(), LayoutHelper.createRelative(-1, -1));
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.setFragmentStack(this.mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        PasscodeView passcodeView = new PasscodeView(this);
        this.passcodeView = passcodeView;
        this.drawerLayoutContainer.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        handleIntent(getIntent(), false, bundle != null, false, UserConfig.selectedAccount, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPasscodeActivity() {
        if (this.passcodeView == null) {
            return;
        }
        SharedConfig.appLocked = true;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        this.passcodeView.onShow(true, false);
        SharedConfig.isWaitingForPasscodeEnter = true;
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate() { // from class: org.telegram.ui.BubbleActivity$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate
            public final void didAcceptedPassword() {
                BubbleActivity.this.lambda$showPasscodeActivity$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPasscodeActivity$0() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, this.passcodeSaveIntentAccount, this.passcodeSaveIntentState);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.showLastFragment();
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3, int i, int i2) {
        if (!z3 && (AndroidUtilities.needShowPasscode(true) || SharedConfig.isWaitingForPasscodeEnter)) {
            showPasscodeActivity();
            this.passcodeSaveIntent = intent;
            this.passcodeSaveIntentIsNew = z;
            this.passcodeSaveIntentIsRestore = z2;
            this.passcodeSaveIntentAccount = i;
            this.passcodeSaveIntentState = i2;
            UserConfig.getInstance(i).saveConfig(false);
            return false;
        }
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        this.currentAccount = intExtra;
        if (!UserConfig.isValidAccount(intExtra)) {
            finish();
            return false;
        }
        ChatActivity chatActivity = null;
        if (intent.getAction() != null && intent.getAction().startsWith("com.tmessages.openchat")) {
            long longExtra = intent.getLongExtra("chatId", 0L);
            long longExtra2 = intent.getLongExtra("userId", 0L);
            Bundle bundle = new Bundle();
            if (longExtra2 != 0) {
                this.dialogId = longExtra2;
                bundle.putLong("user_id", longExtra2);
            } else {
                this.dialogId = -longExtra;
                bundle.putLong("chat_id", longExtra);
            }
            chatActivity = new ChatActivity(bundle);
            chatActivity.setInBubbleMode(true);
            chatActivity.setCurrentAccount(this.currentAccount);
        }
        if (chatActivity == null) {
            finish();
            return false;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(this.dialogId));
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.addFragmentToStack(chatActivity);
        AccountInstance.getInstance(this.currentAccount).getNotificationsController().setOpenedInBubble(this.dialogId, true);
        AccountInstance.getInstance(this.currentAccount).getConnectionsManager().setAppPaused(false, false);
        this.actionBarLayout.showLastFragment();
        return true;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, UserConfig.selectedAccount, 0);
    }

    private void onFinish() {
        if (this.finished) {
            return;
        }
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        this.finished = true;
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        ApplicationLoader.externalInterfacePaused = true;
        onPasscodePause();
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onPause();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        int i = this.currentAccount;
        if (i != -1) {
            AccountInstance.getInstance(i).getNotificationsController().setOpenedInBubble(this.dialogId, false);
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().setAppPaused(false, false);
        }
        onFinish();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.getFragmentStack().size() != 0) {
            this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1).onActivityResultFragment(i, i2, intent);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (!checkPermissionsResult(i, strArr, iArr)) {
            return;
        }
        if (this.actionBarLayout.getFragmentStack().size() != 0) {
            this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        VoIPFragment.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        ApplicationLoader.externalInterfacePaused = false;
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            return;
        }
        this.actionBarLayout.dismissDialogs();
        this.passcodeView.onResume();
    }

    private void onPasscodePause() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.BubbleActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    if (BubbleActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            BubbleActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        BubbleActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = runnable2;
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(runnable2, 1000L);
            } else {
                int i = SharedConfig.autoLockIn;
                if (i != 0) {
                    AndroidUtilities.runOnUIThread(runnable2, (i * 1000) + 1000);
                }
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mainFragmentsStack.size() == 1) {
            super.onBackPressed();
        } else if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needCloseLastFragment(INavigationLayout iNavigationLayout) {
        if (iNavigationLayout.getFragmentStack().size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }
}
