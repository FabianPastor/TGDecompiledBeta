package org.telegram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;

public class ExternalActionActivity extends Activity implements ActionBarLayoutDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
    private ActionBarLayout actionBarLayout;
    private View backgroundTablet;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;
    private Runnable lockRunnable;
    private Intent passcodeSaveIntent;
    private int passcodeSaveIntentAccount;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private int passcodeSaveIntentState;
    private PasscodeView passcodeView;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        return true;
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout) {
        return true;
    }

    public boolean onPreIme() {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(NUM);
        getWindow().setBackgroundDrawableResource(NUM);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        super.onCreate(bundle);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.uptimeMillis() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        RelativeLayout relativeLayout;
        BitmapDrawable bitmapDrawable;
        TileMode tileMode;
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            relativeLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(relativeLayout);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            relativeLayout.setLayoutParams(layoutParams);
            this.backgroundTablet = new View(this);
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(NUM);
            tileMode = TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            this.backgroundTablet.setBackgroundDrawable(bitmapDrawable);
            relativeLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            relativeLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setBackgroundColor(NUM);
            relativeLayout.addView(frameLayout, LayoutHelper.createRelative(-1, -1));
            frameLayout.setOnTouchListener(new -$$Lambda$ExternalActionActivity$uCjV_BmhLs5mtl2FVWuHvttAbEQ(this));
            frameLayout.setOnClickListener(-$$Lambda$ExternalActionActivity$u8HNxR8AzNk3ipgQhpw5QlDqvFw.INSTANCE);
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(frameLayout);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(NUM);
            relativeLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, AndroidUtilities.isSmallTablet() ? 528 : 700));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            relativeLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
            this.backgroundTablet = new View(this);
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(NUM);
            tileMode = TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            this.backgroundTablet.setBackgroundDrawable(bitmapDrawable);
            relativeLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            relativeLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        this.passcodeView = new PasscodeView(this);
        this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.removeAllFragments();
        }
        handleIntent(getIntent(), false, bundle != null, false, UserConfig.selectedAccount, 0);
        needLayout();
    }

    public /* synthetic */ boolean lambda$onCreate$0$ExternalActionActivity(View view, MotionEvent motionEvent) {
        if (!this.actionBarLayout.fragmentsStack.isEmpty() && motionEvent.getAction() == 1) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int[] iArr = new int[2];
            this.layersActionBarLayout.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (!this.layersActionBarLayout.checkTransitionAnimation() && (x <= ((float) i) || x >= ((float) (i + this.layersActionBarLayout.getWidth())) || y <= ((float) i2) || y >= ((float) (i2 + this.layersActionBarLayout.getHeight())))) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                        actionBarLayout.removeFragmentFromStack((BaseFragment) actionBarLayout.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return true;
            }
        }
        return false;
    }

    private void showPasscodeActivity() {
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
            this.passcodeView.setDelegate(new -$$Lambda$ExternalActionActivity$gW5CjfzBu63uWJgg9FkpT1P0Byc(this));
        }
    }

    public /* synthetic */ void lambda$showPasscodeActivity$2$ExternalActionActivity() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, this.passcodeSaveIntentAccount, this.passcodeSaveIntentState);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
        }
    }

    public void onFinishLogin() {
        handleIntent(this.passcodeSaveIntent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, this.passcodeSaveIntentAccount, this.passcodeSaveIntentState);
        this.actionBarLayout.removeAllFragments();
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.removeAllFragments();
        }
        View view = this.backgroundTablet;
        if (view != null) {
            view.setVisibility(0);
        }
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3, int i, int i2) {
        Intent intent2 = intent;
        boolean z4 = z;
        boolean z5 = z2;
        int i3 = i;
        int i4 = i2;
        if (z3 || !(AndroidUtilities.needShowPasscode(true) || SharedConfig.isWaitingForPasscodeEnter)) {
            if ("org.telegram.passport.AUTHORIZE".equals(intent.getAction())) {
                if (i4 == 0) {
                    int activatedAccountsCount = UserConfig.getActivatedAccountsCount();
                    if (activatedAccountsCount == 0) {
                        this.passcodeSaveIntent = intent2;
                        this.passcodeSaveIntentIsNew = z4;
                        this.passcodeSaveIntentIsRestore = z5;
                        this.passcodeSaveIntentAccount = i3;
                        this.passcodeSaveIntentState = i4;
                        LoginActivity loginActivity = new LoginActivity();
                        if (AndroidUtilities.isTablet()) {
                            this.layersActionBarLayout.addFragmentToStack(loginActivity);
                        } else {
                            this.actionBarLayout.addFragmentToStack(loginActivity);
                        }
                        if (!AndroidUtilities.isTablet()) {
                            this.backgroundTablet.setVisibility(8);
                        }
                        this.actionBarLayout.showLastFragment();
                        if (AndroidUtilities.isTablet()) {
                            this.layersActionBarLayout.showLastFragment();
                        }
                        Builder builder = new Builder(this);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("PleaseLoginPassport", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        builder.show();
                        return true;
                    } else if (activatedAccountsCount >= 2) {
                        AlertDialog createAccountSelectDialog = AlertsCreator.createAccountSelectDialog(this, new -$$Lambda$ExternalActionActivity$7MenW-e7ZVDhaPYTW6k7H5-jjBA(this, i, intent, z, z2, z3));
                        createAccountSelectDialog.show();
                        createAccountSelectDialog.setCanceledOnTouchOutside(false);
                        createAccountSelectDialog.setOnDismissListener(new -$$Lambda$ExternalActionActivity$HPinU7ZiWQ3pid8nvHDiLL5fIOA(this));
                        return true;
                    }
                }
                i4 = intent2.getIntExtra("bot_id", 0);
                String stringExtra = intent2.getStringExtra("nonce");
                String stringExtra2 = intent2.getStringExtra("payload");
                TL_account_getAuthorizationForm tL_account_getAuthorizationForm = new TL_account_getAuthorizationForm();
                tL_account_getAuthorizationForm.bot_id = i4;
                tL_account_getAuthorizationForm.scope = intent2.getStringExtra("scope");
                tL_account_getAuthorizationForm.public_key = intent2.getStringExtra("public_key");
                if (i4 == 0 || ((TextUtils.isEmpty(stringExtra2) && TextUtils.isEmpty(stringExtra)) || TextUtils.isEmpty(tL_account_getAuthorizationForm.scope) || TextUtils.isEmpty(tL_account_getAuthorizationForm.public_key))) {
                    finish();
                    return false;
                }
                int[] iArr = new int[]{0};
                AlertDialog alertDialog = new AlertDialog(this, 3);
                alertDialog.setOnCancelListener(new -$$Lambda$ExternalActionActivity$JSGDZffGA-C8G3znC_C8fmn13dg(i3, iArr));
                alertDialog.show();
                iArr[0] = ConnectionsManager.getInstance(i).sendRequest(tL_account_getAuthorizationForm, new -$$Lambda$ExternalActionActivity$7IhkCSknxlX6e3UV6VR8IaZyD4g(this, iArr, i, alertDialog, tL_account_getAuthorizationForm, stringExtra2, stringExtra), 10);
            } else {
                if (AndroidUtilities.isTablet()) {
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        this.layersActionBarLayout.addFragmentToStack(new CacheControlActivity());
                    }
                } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.addFragmentToStack(new CacheControlActivity());
                }
                if (!AndroidUtilities.isTablet()) {
                    this.backgroundTablet.setVisibility(8);
                }
                this.actionBarLayout.showLastFragment();
                if (AndroidUtilities.isTablet()) {
                    this.layersActionBarLayout.showLastFragment();
                }
                intent2.setAction(null);
            }
            return false;
        }
        showPasscodeActivity();
        this.passcodeSaveIntent = intent2;
        this.passcodeSaveIntentIsNew = z4;
        this.passcodeSaveIntentIsRestore = z5;
        this.passcodeSaveIntentAccount = i3;
        this.passcodeSaveIntentState = i4;
        UserConfig.getInstance(i).saveConfig(false);
        return false;
    }

    public /* synthetic */ void lambda$handleIntent$3$ExternalActionActivity(int i, Intent intent, boolean z, boolean z2, boolean z3, int i2) {
        if (i2 != i) {
            switchToAccount(i2);
        }
        handleIntent(intent, z, z2, z3, i2, 1);
    }

    public /* synthetic */ void lambda$handleIntent$4$ExternalActionActivity(DialogInterface dialogInterface) {
        setResult(0);
        finish();
    }

    public /* synthetic */ void lambda$handleIntent$10$ExternalActionActivity(int[] iArr, int i, AlertDialog alertDialog, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, TLObject tLObject, TL_error tL_error) {
        TL_account_authorizationForm tL_account_authorizationForm = (TL_account_authorizationForm) tLObject;
        if (tL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TL_account_getPassword(), new -$$Lambda$ExternalActionActivity$JR-1IB3pX75b9s0gpd--Ar_xSw8(this, alertDialog, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2));
            return;
        }
        AlertDialog alertDialog2 = alertDialog;
        AndroidUtilities.runOnUIThread(new -$$Lambda$ExternalActionActivity$-9fe3mEaNJ9F6qOli_eauMz3QBw(this, alertDialog, tL_error));
    }

    public /* synthetic */ void lambda$null$7$ExternalActionActivity(AlertDialog alertDialog, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ExternalActionActivity$Okpye-XNIzLiHpMbfSsCYU0NOVg(this, alertDialog, tLObject, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2));
    }

    public /* synthetic */ void lambda$null$6$ExternalActionActivity(AlertDialog alertDialog, TLObject tLObject, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
        TL_account_getAuthorizationForm tL_account_getAuthorizationForm2 = tL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            TL_account_password tL_account_password = (TL_account_password) tLObject;
            MessagesController.getInstance(i).putUsers(tL_account_authorizationForm.users, false);
            PassportActivity passportActivity = new PassportActivity(5, tL_account_getAuthorizationForm2.bot_id, tL_account_getAuthorizationForm2.scope, tL_account_getAuthorizationForm2.public_key, str, str2, null, tL_account_authorizationForm, tL_account_password);
            passportActivity.setNeedActivityResult(true);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.addFragmentToStack(passportActivity);
            } else {
                this.actionBarLayout.addFragmentToStack(passportActivity);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
            }
        }
    }

    public /* synthetic */ void lambda$null$9$ExternalActionActivity(AlertDialog alertDialog, TL_error tL_error) {
        try {
            alertDialog.dismiss();
            String str = "error";
            if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                alertDialog = AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                if (alertDialog != null) {
                    alertDialog.setOnDismissListener(new -$$Lambda$ExternalActionActivity$tLYtJXpiU7Sr8cawZGmHGmD9I2k(this, tL_error));
                    return;
                }
                setResult(1, new Intent().putExtra(str, tL_error.text));
                finish();
                return;
            }
            if (!("BOT_INVALID".equals(tL_error.text) || "PUBLIC_KEY_REQUIRED".equals(tL_error.text) || "PUBLIC_KEY_INVALID".equals(tL_error.text) || "SCOPE_EMPTY".equals(tL_error.text))) {
                if (!"PAYLOAD_EMPTY".equals(tL_error.text)) {
                    setResult(0);
                    finish();
                    return;
                }
            }
            setResult(1, new Intent().putExtra(str, tL_error.text));
            finish();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$8$ExternalActionActivity(TL_error tL_error, DialogInterface dialogInterface) {
        setResult(1, new Intent().putExtra("error", tL_error.text));
        finish();
    }

    public void switchToAccount(int i) {
        int i2 = UserConfig.selectedAccount;
        if (i != i2) {
            ConnectionsManager.getInstance(i2).setAppPaused(true, false);
            UserConfig.selectedAccount = i;
            UserConfig.getInstance(0).saveConfig(false);
            if (!ApplicationLoader.mainInterfacePaused) {
                ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, false);
            }
        }
    }

    /* Access modifiers changed, original: protected */
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

    public void presentFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true, false);
    }

    public void needLayout() {
        if (AndroidUtilities.isTablet()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.layersActionBarLayout.getLayoutParams();
            layoutParams.leftMargin = (AndroidUtilities.displaySize.x - layoutParams.width) / 2;
            int i = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            layoutParams.topMargin = i + (((AndroidUtilities.displaySize.y - layoutParams.height) - i) / 2);
            this.layersActionBarLayout.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int i2 = (AndroidUtilities.displaySize.x / 100) * 35;
                if (i2 < AndroidUtilities.dp(320.0f)) {
                    i2 = AndroidUtilities.dp(320.0f);
                }
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
                layoutParams2.width = i2;
                layoutParams2.height = -1;
                this.actionBarLayout.setLayoutParams(layoutParams2);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                    ((BaseFragment) this.actionBarLayout.fragmentsStack.get(1)).onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.actionBarLayout.showLastFragment();
                    return;
                }
                return;
            }
            layoutParams = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            this.actionBarLayout.setLayoutParams(layoutParams);
        }
    }

    public void fixLayout() {
        if (AndroidUtilities.isTablet()) {
            ActionBarLayout actionBarLayout = this.actionBarLayout;
            if (actionBarLayout != null) {
                actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        ExternalActionActivity.this.needLayout();
                        if (ExternalActionActivity.this.actionBarLayout != null) {
                            ExternalActionActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
        ApplicationLoader.externalInterfacePaused = true;
        onPasscodePause();
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onPause();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
        super.onDestroy();
        onFinish();
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onResume();
        }
        ApplicationLoader.externalInterfacePaused = false;
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.onResume();
                return;
            }
            return;
        }
        this.actionBarLayout.dismissDialogs();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.dismissDialogs();
        }
        this.passcodeView.onResume();
    }

    private void onPasscodePause() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.uptimeMillis() / 1000);
            this.lockRunnable = new Runnable() {
                public void run() {
                    if (ExternalActionActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            ExternalActionActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        ExternalActionActivity.this.lockRunnable = null;
                    }
                }
            };
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
            } else {
                int i = SharedConfig.autoLockIn;
                if (i != 0) {
                    AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) i) * 1000) + 1000);
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
        fixLayout();
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
            return;
        }
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z) {
        if (AndroidUtilities.isTablet() && actionBarLayout == this.layersActionBarLayout) {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }
}
