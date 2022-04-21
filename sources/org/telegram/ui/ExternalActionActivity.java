package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ExternalActionActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    protected ActionBarLayout actionBarLayout;
    protected SizeNotifierFrameLayout backgroundTablet;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    protected ActionBarLayout layersActionBarLayout;
    /* access modifiers changed from: private */
    public Runnable lockRunnable;
    private Intent passcodeSaveIntent;
    private int passcodeSaveIntentAccount;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private int passcodeSaveIntentState;
    private PasscodeView passcodeView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        this.actionBarLayout = new ActionBarLayout(this);
        DrawerLayoutContainer drawerLayoutContainer2 = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer = drawerLayoutContainer2;
        drawerLayoutContainer2.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            RelativeLayout launchLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout);
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) launchLayout.getLayoutParams();
            layoutParams1.width = -1;
            layoutParams1.height = -1;
            launchLayout.setLayoutParams(layoutParams1);
            AnonymousClass1 r6 = new SizeNotifierFrameLayout(this) {
                /* access modifiers changed from: protected */
                public boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = r6;
            r6.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            FrameLayout shadowTablet = new FrameLayout(this);
            shadowTablet.setBackgroundColor(NUM);
            launchLayout.addView(shadowTablet, LayoutHelper.createRelative(-1, -1));
            shadowTablet.setOnTouchListener(new ExternalActionActivity$$ExternalSyntheticLambda5(this));
            shadowTablet.setOnClickListener(ExternalActionActivity$$ExternalSyntheticLambda4.INSTANCE);
            ActionBarLayout actionBarLayout2 = new ActionBarLayout(this);
            this.layersActionBarLayout = actionBarLayout2;
            actionBarLayout2.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(NUM);
            launchLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, AndroidUtilities.isSmallTablet() ? 528 : 700));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            RelativeLayout launchLayout2 = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout2, LayoutHelper.createFrame(-1, -1.0f));
            AnonymousClass2 r5 = new SizeNotifierFrameLayout(this) {
                /* access modifiers changed from: protected */
                public boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = r5;
            r5.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            launchLayout2.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout2.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        PasscodeView passcodeView2 = new PasscodeView(this);
        this.passcodeView = passcodeView2;
        this.drawerLayoutContainer.addView(passcodeView2, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        ActionBarLayout actionBarLayout3 = this.layersActionBarLayout;
        if (actionBarLayout3 != null) {
            actionBarLayout3.removeAllFragments();
        }
        handleIntent(getIntent(), false, savedInstanceState != null, false, UserConfig.selectedAccount, 0);
        needLayout();
    }

    /* renamed from: lambda$onCreate$0$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ boolean m2141lambda$onCreate$0$orgtelegramuiExternalActionActivity(View v, MotionEvent event) {
        if (this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int[] location = new int[2];
        this.layersActionBarLayout.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if (this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (this.layersActionBarLayout.getHeight() + viewY)))) {
            return false;
        }
        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                actionBarLayout2.removeFragmentFromStack(actionBarLayout2.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    static /* synthetic */ void lambda$onCreate$1(View v) {
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
            this.passcodeView.onShow(true, false);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new ExternalActionActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$showPasscodeActivity$2$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2142x2765CLASSNAME() {
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
        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.removeAllFragments();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.backgroundTablet;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkPasscode(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        if (fromPassword || (!AndroidUtilities.needShowPasscode(true) && !SharedConfig.isWaitingForPasscodeEnter)) {
            return true;
        }
        showPasscodeActivity();
        this.passcodeSaveIntent = intent;
        this.passcodeSaveIntentIsNew = isNew;
        this.passcodeSaveIntentIsRestore = restore;
        this.passcodeSaveIntentAccount = intentAccount;
        this.passcodeSaveIntentState = state;
        UserConfig.getInstance(intentAccount).saveConfig(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        boolean z;
        Intent intent2 = intent;
        int i = intentAccount;
        int i2 = state;
        if (!checkPasscode(intent, isNew, restore, fromPassword, intentAccount, state)) {
            return false;
        }
        if ("org.telegram.passport.AUTHORIZE".equals(intent.getAction())) {
            if (i2 == 0) {
                int activatedAccountsCount = UserConfig.getActivatedAccountsCount();
                if (activatedAccountsCount == 0) {
                    this.passcodeSaveIntent = intent2;
                    this.passcodeSaveIntentIsNew = isNew;
                    this.passcodeSaveIntentIsRestore = restore;
                    this.passcodeSaveIntentAccount = i;
                    this.passcodeSaveIntentState = i2;
                    LoginActivity fragment = new LoginActivity();
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.addFragmentToStack(fragment);
                    } else {
                        this.actionBarLayout.addFragmentToStack(fragment);
                    }
                    if (!AndroidUtilities.isTablet()) {
                        this.backgroundTablet.setVisibility(8);
                    }
                    this.actionBarLayout.showLastFragment();
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.showLastFragment();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("PleaseLoginPassport", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    builder.show();
                    return true;
                }
                boolean z2 = isNew;
                boolean z3 = restore;
                if (activatedAccountsCount >= 2) {
                    ExternalActionActivity$$ExternalSyntheticLambda10 externalActionActivity$$ExternalSyntheticLambda10 = r0;
                    ExternalActionActivity$$ExternalSyntheticLambda10 externalActionActivity$$ExternalSyntheticLambda102 = new ExternalActionActivity$$ExternalSyntheticLambda10(this, intentAccount, intent, isNew, restore, fromPassword);
                    AlertDialog alertDialog = AlertsCreator.createAccountSelectDialog(this, externalActionActivity$$ExternalSyntheticLambda10);
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setOnDismissListener(new ExternalActionActivity$$ExternalSyntheticLambda2(this));
                    return true;
                }
            } else {
                boolean z4 = isNew;
                boolean z5 = restore;
            }
            long bot_id = intent2.getLongExtra("bot_id", (long) intent2.getIntExtra("bot_id", 0));
            String nonce = intent2.getStringExtra("nonce");
            String payload = intent2.getStringExtra("payload");
            TLRPC.TL_account_getAuthorizationForm req = new TLRPC.TL_account_getAuthorizationForm();
            req.bot_id = bot_id;
            req.scope = intent2.getStringExtra("scope");
            req.public_key = intent2.getStringExtra("public_key");
            if (bot_id != 0) {
                if (!TextUtils.isEmpty(payload) || !TextUtils.isEmpty(nonce)) {
                    if (!TextUtils.isEmpty(req.scope)) {
                        if (!TextUtils.isEmpty(req.public_key)) {
                            int[] requestId = {0};
                            AlertDialog progressDialog = new AlertDialog(this, 3);
                            progressDialog.setOnCancelListener(new ExternalActionActivity$$ExternalSyntheticLambda0(i, requestId));
                            progressDialog.show();
                            ExternalActionActivity$$ExternalSyntheticLambda9 externalActionActivity$$ExternalSyntheticLambda9 = r0;
                            ConnectionsManager instance = ConnectionsManager.getInstance(intentAccount);
                            long j = bot_id;
                            ExternalActionActivity$$ExternalSyntheticLambda9 externalActionActivity$$ExternalSyntheticLambda92 = new ExternalActionActivity$$ExternalSyntheticLambda9(this, requestId, intentAccount, progressDialog, req, payload, nonce);
                            requestId[0] = instance.sendRequest(req, externalActionActivity$$ExternalSyntheticLambda9, 10);
                            return false;
                        }
                    }
                }
                TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm = req;
                long j2 = bot_id;
                z = false;
                finish();
                return z;
            }
            TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm2 = req;
            long j3 = bot_id;
            z = false;
            finish();
            return z;
        }
        boolean z6 = isNew;
        boolean z7 = restore;
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
        intent2.setAction((String) null);
        return false;
    }

    /* renamed from: lambda$handleIntent$3$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2135lambda$handleIntent$3$orgtelegramuiExternalActionActivity(int intentAccount, Intent intent, boolean isNew, boolean restore, boolean fromPassword, int account) {
        if (account != intentAccount) {
            switchToAccount(account);
        }
        handleIntent(intent, isNew, restore, fromPassword, account, 1);
    }

    /* renamed from: lambda$handleIntent$4$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2136lambda$handleIntent$4$orgtelegramuiExternalActionActivity(DialogInterface dialog) {
        setResult(0);
        finish();
    }

    /* renamed from: lambda$handleIntent$10$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2134lambda$handleIntent$10$orgtelegramuiExternalActionActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_account_authorizationForm authorizationForm = (TLRPC.TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new ExternalActionActivity$$ExternalSyntheticLambda8(this, progressDialog, intentAccount, authorizationForm, req, payload, nonce));
            TLRPC.TL_error tL_error = error;
            return;
        }
        AndroidUtilities.runOnUIThread(new ExternalActionActivity$$ExternalSyntheticLambda7(this, progressDialog, error));
    }

    /* renamed from: lambda$handleIntent$7$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2138lambda$handleIntent$7$orgtelegramuiExternalActionActivity(AlertDialog progressDialog, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new ExternalActionActivity$$ExternalSyntheticLambda6(this, progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce));
    }

    /* renamed from: lambda$handleIntent$6$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2137lambda$handleIntent$6$orgtelegramuiExternalActionActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce) {
        TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response1 != null) {
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            PassportActivity fragment = new PassportActivity(5, tL_account_getAuthorizationForm.bot_id, tL_account_getAuthorizationForm.scope, tL_account_getAuthorizationForm.public_key, payload, nonce, (String) null, authorizationForm, (TLRPC.TL_account_password) response1);
            fragment.setNeedActivityResult(true);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.addFragmentToStack(fragment);
            } else {
                this.actionBarLayout.addFragmentToStack(fragment);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
                return;
            }
            return;
        }
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = authorizationForm;
    }

    /* renamed from: lambda$handleIntent$9$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2140lambda$handleIntent$9$orgtelegramuiExternalActionActivity(AlertDialog progressDialog, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertDialog dialog = AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                if (dialog != null) {
                    dialog.setOnDismissListener(new ExternalActionActivity$$ExternalSyntheticLambda3(this, error));
                } else {
                    setResult(1, new Intent().putExtra("error", error.text));
                    finish();
                }
                return;
            }
            if (!"BOT_INVALID".equals(error.text) && !"PUBLIC_KEY_REQUIRED".equals(error.text) && !"PUBLIC_KEY_INVALID".equals(error.text) && !"SCOPE_EMPTY".equals(error.text)) {
                if (!"PAYLOAD_EMPTY".equals(error.text)) {
                    setResult(0);
                    finish();
                    return;
                }
            }
            setResult(1, new Intent().putExtra("error", error.text));
            finish();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$handleIntent$8$org-telegram-ui-ExternalActionActivity  reason: not valid java name */
    public /* synthetic */ void m2139lambda$handleIntent$8$orgtelegramuiExternalActionActivity(TLRPC.TL_error error, DialogInterface dialog1) {
        setResult(1, new Intent().putExtra("error", error.text));
        finish();
    }

    public void switchToAccount(int account) {
        if (account != UserConfig.selectedAccount) {
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = account;
            UserConfig.getInstance(0).saveConfig(false);
            if (!ApplicationLoader.mainInterfacePaused) {
                ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, false);
            }
        }
    }

    public boolean onPreIme() {
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

    public void presentFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public void needLayout() {
        if (AndroidUtilities.isTablet()) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) this.layersActionBarLayout.getLayoutParams();
            relativeLayoutParams.leftMargin = (AndroidUtilities.displaySize.x - relativeLayoutParams.width) / 2;
            int y = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            relativeLayoutParams.topMargin = (((AndroidUtilities.displaySize.y - relativeLayoutParams.height) - y) / 2) + y;
            this.layersActionBarLayout.setLayoutParams(relativeLayoutParams);
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int leftWidth = (AndroidUtilities.displaySize.x / 100) * 35;
                if (leftWidth < AndroidUtilities.dp(320.0f)) {
                    leftWidth = AndroidUtilities.dp(320.0f);
                }
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
                relativeLayoutParams2.width = leftWidth;
                relativeLayoutParams2.height = -1;
                this.actionBarLayout.setLayoutParams(relativeLayoutParams2);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                    this.actionBarLayout.fragmentsStack.get(1).onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.actionBarLayout.showLastFragment();
                    return;
                }
                return;
            }
            RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
            relativeLayoutParams3.width = -1;
            relativeLayoutParams3.height = -1;
            this.actionBarLayout.setLayoutParams(relativeLayoutParams3);
        }
    }

    public void fixLayout() {
        ActionBarLayout actionBarLayout2;
        if (AndroidUtilities.isTablet() && (actionBarLayout2 = this.actionBarLayout) != null) {
            actionBarLayout2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ExternalActionActivity.this.needLayout();
                    if (ExternalActionActivity.this.actionBarLayout != null) {
                        ExternalActionActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
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
        onFinish();
    }

    /* access modifiers changed from: protected */
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
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
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
                        Runnable unused = ExternalActionActivity.this.lockRunnable = null;
                    }
                }
            };
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
            } else if (SharedConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) SharedConfig.autoLockIn) * 1000) + 1000);
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

    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
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

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }
}
