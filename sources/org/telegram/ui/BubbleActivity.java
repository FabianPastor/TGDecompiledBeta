package org.telegram.ui;

import android.app.Activity;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;

public class BubbleActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate {
    private ActionBarLayout actionBarLayout;
    private int currentAccount = -1;
    private long dialogId;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    /* access modifiers changed from: private */
    public Runnable lockRunnable;
    private ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private Intent passcodeSaveIntent;
    private int passcodeSaveIntentAccount;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private int passcodeSaveIntentState;
    private PasscodeView passcodeView;

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout2) {
        return true;
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout2) {
        return true;
    }

    public boolean onPreIme() {
        return false;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout2, boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(NUM);
        getWindow().setBackgroundDrawableResource(NUM);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        super.onCreate(bundle);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        ActionBarLayout actionBarLayout2 = new ActionBarLayout(this);
        this.actionBarLayout = actionBarLayout2;
        actionBarLayout2.setInBubbleMode(true);
        this.actionBarLayout.setRemoveActionBarExtraHeight(true);
        DrawerLayoutContainer drawerLayoutContainer2 = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer = drawerLayoutContainer2;
        drawerLayoutContainer2.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        RelativeLayout relativeLayout = new RelativeLayout(this);
        this.drawerLayoutContainer.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        relativeLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(this.mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        PasscodeView passcodeView2 = new PasscodeView(this);
        this.passcodeView = passcodeView2;
        this.drawerLayoutContainer.addView(passcodeView2, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        handleIntent(getIntent(), false, bundle != null, false, UserConfig.selectedAccount, 0);
    }

    /* access modifiers changed from: private */
    public void showPasscodeActivity() {
        if (this.passcodeView != null) {
            SharedConfig.appLocked = true;
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(false, false);
            } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(false, true);
            }
            this.passcodeView.onShow();
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate() {
                public final void didAcceptedPassword() {
                    BubbleActivity.this.lambda$showPasscodeActivity$0$BubbleActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$showPasscodeActivity$0$BubbleActivity() {
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
        if (z3 || (!AndroidUtilities.needShowPasscode(true) && !SharedConfig.isWaitingForPasscodeEnter)) {
            this.currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            ChatActivity chatActivity = null;
            if (intent.getAction().startsWith("com.tmessages.openchat")) {
                int intExtra = intent.getIntExtra("chatId", 0);
                int intExtra2 = intent.getIntExtra("userId", 0);
                Bundle bundle = new Bundle();
                if (intExtra2 != 0) {
                    this.dialogId = (long) intExtra2;
                    bundle.putInt("user_id", intExtra2);
                } else {
                    this.dialogId = (long) (-intExtra);
                    bundle.putInt("chat_id", intExtra);
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
        showPasscodeActivity();
        this.passcodeSaveIntent = intent;
        this.passcodeSaveIntentIsNew = z;
        this.passcodeSaveIntentIsRestore = z2;
        this.passcodeSaveIntentAccount = i;
        this.passcodeSaveIntentState = i2;
        UserConfig.getInstance(i).saveConfig(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, UserConfig.selectedAccount, 0);
    }

    private void onFinish() {
        if (!this.finished) {
            Runnable runnable = this.lockRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lockRunnable = null;
            }
            this.finished = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        ApplicationLoader.externalInterfacePaused = true;
        onPasscodePause();
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 != null) {
            passcodeView2.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        int i = this.currentAccount;
        if (i != -1) {
            AccountInstance.getInstance(i).getNotificationsController().setOpenedInBubble(this.dialogId, false);
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().setAppPaused(false, false);
        }
        onFinish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
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
            AnonymousClass1 r0 = new Runnable() {
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
                        Runnable unused = BubbleActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = r0;
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(r0, 1000);
            } else {
                int i = SharedConfig.autoLockIn;
                if (i != 0) {
                    AndroidUtilities.runOnUIThread(r0, (((long) i) * 1000) + 1000);
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

    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout2) {
        if (actionBarLayout2.fragmentsStack.size() > 1) {
            return true;
        }
        onFinish();
        finish();
        return false;
    }
}
