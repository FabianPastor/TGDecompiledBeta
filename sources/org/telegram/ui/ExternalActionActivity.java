package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.account_Password;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;

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

    /* renamed from: org.telegram.ui.ExternalActionActivity$1 */
    class C14471 implements OnTouchListener {
        C14471() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (ExternalActionActivity.this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
                return false;
            }
            float x = event.getX();
            float y = event.getY();
            int[] location = new int[2];
            ExternalActionActivity.this.layersActionBarLayout.getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];
            if (ExternalActionActivity.this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (ExternalActionActivity.this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (ExternalActionActivity.this.layersActionBarLayout.getHeight() + viewY)))) {
                return false;
            }
            if (!ExternalActionActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                int a = 0;
                while (ExternalActionActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                    ExternalActionActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) ExternalActionActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                    a = (a - 1) + 1;
                }
                ExternalActionActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ExternalActionActivity$2 */
    class C14482 implements OnClickListener {
        C14482() {
        }

        public void onClick(View v) {
        }
    }

    /* renamed from: org.telegram.ui.ExternalActionActivity$6 */
    class C14516 implements OnGlobalLayoutListener {
        C14516() {
        }

        public void onGlobalLayout() {
            ExternalActionActivity.this.needLayout();
            if (ExternalActionActivity.this.actionBarLayout != null) {
                ExternalActionActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    /* renamed from: org.telegram.ui.ExternalActionActivity$7 */
    class C14527 implements Runnable {
        C14527() {
        }

        public void run() {
            if (ExternalActionActivity.this.lockRunnable == this) {
                if (AndroidUtilities.needShowPasscode(true)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("lock app");
                    }
                    ExternalActionActivity.this.showPasscodeActivity();
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("didn't pass lock check");
                }
                ExternalActionActivity.this.lockRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ExternalActionActivity$3 */
    class C22823 implements PasscodeViewDelegate {
        C22823() {
        }

        public void didAcceptedPassword() {
            SharedConfig.isWaitingForPasscodeEnter = false;
            if (ExternalActionActivity.this.passcodeSaveIntent != null) {
                ExternalActionActivity.this.handleIntent(ExternalActionActivity.this.passcodeSaveIntent, ExternalActionActivity.this.passcodeSaveIntentIsNew, ExternalActionActivity.this.passcodeSaveIntentIsRestore, true, ExternalActionActivity.this.passcodeSaveIntentAccount, ExternalActionActivity.this.passcodeSaveIntentState);
                ExternalActionActivity.this.passcodeSaveIntent = null;
            }
            ExternalActionActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            ExternalActionActivity.this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                ExternalActionActivity.this.layersActionBarLayout.showLastFragment();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        boolean z = true;
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(R.style.Theme.TMessages);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        super.onCreate(savedInstanceState);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        RelativeLayout launchLayout;
        BitmapDrawable drawable;
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            launchLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout);
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) launchLayout.getLayoutParams();
            layoutParams1.width = -1;
            layoutParams1.height = -1;
            launchLayout.setLayoutParams(layoutParams1);
            this.backgroundTablet = new View(this);
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable(drawable);
            launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            FrameLayout shadowTablet = new FrameLayout(this);
            shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            launchLayout.addView(shadowTablet, LayoutHelper.createRelative(-1, -1));
            shadowTablet.setOnTouchListener(new C14471());
            shadowTablet.setOnClickListener(new C14482());
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            launchLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, 700));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            launchLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout, LayoutHelper.createFrame(-1, -1.0f));
            this.backgroundTablet = new View(this);
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable(drawable);
            launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        this.passcodeView = new PasscodeView(this);
        this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        if (this.layersActionBarLayout != null) {
            this.layersActionBarLayout.removeAllFragments();
        }
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            z = false;
        }
        handleIntent(intent, false, z, false, UserConfig.selectedAccount, 0);
        needLayout();
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
            this.passcodeView.setDelegate(new C22823());
        }
    }

    private boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        if (fromPassword || !(AndroidUtilities.needShowPasscode(true) || SharedConfig.isWaitingForPasscodeEnter)) {
            if (!"org.telegram.passport.AUTHORIZE".equals(intent.getAction())) {
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
                intent.setAction(null);
            } else if (state != 0 || UserConfig.getActivatedAccountsCount() < 2) {
                final AlertDialog progressDialog = new AlertDialog(this, 1);
                progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                int bot_id = intent.getIntExtra("bot_id", 0);
                final String payload = intent.getStringExtra("payload");
                final TL_account_getAuthorizationForm req = new TL_account_getAuthorizationForm();
                req.bot_id = bot_id;
                req.scope = intent.getStringExtra("scope");
                req.public_key = intent.getStringExtra("public_key");
                final int[] requestId = new int[]{0};
                if (bot_id == 0 || TextUtils.isEmpty(payload) || TextUtils.isEmpty(req.scope) || TextUtils.isEmpty(req.public_key)) {
                    finish();
                    return false;
                }
                progressDialog.show();
                final int i = intentAccount;
                requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        final TL_account_authorizationForm authorizationForm = (TL_account_authorizationForm) response;
                        if (authorizationForm != null) {
                            requestId[0] = ConnectionsManager.getInstance(i).sendRequest(new TL_account_getPassword(), new RequestDelegate() {
                                public void run(final TLObject response, TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            try {
                                                progressDialog.dismiss();
                                            } catch (Throwable e) {
                                                FileLog.m3e(e);
                                            }
                                            if (response != null) {
                                                account_Password accountPassword = response;
                                                MessagesController.getInstance(i).putUsers(authorizationForm.users, false);
                                                PassportActivity fragment = new PassportActivity(5, req.bot_id, req.scope, req.public_key, payload, null, authorizationForm, accountPassword);
                                                fragment.setNeedActivityResult(true);
                                                if (AndroidUtilities.isTablet()) {
                                                    ExternalActionActivity.this.layersActionBarLayout.addFragmentToStack(fragment);
                                                } else {
                                                    ExternalActionActivity.this.actionBarLayout.addFragmentToStack(fragment);
                                                }
                                                if (!AndroidUtilities.isTablet()) {
                                                    ExternalActionActivity.this.backgroundTablet.setVisibility(8);
                                                }
                                                ExternalActionActivity.this.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    ExternalActionActivity.this.layersActionBarLayout.showLastFragment();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                    if ("APP_VERSION_OUTDATED".equals(error.text)) {
                                        AlertsCreator.showUpdateAppAlert(ExternalActionActivity.this, LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                                    } else if ("BOT_INVALID".equals(error.text) || "PUBLIC_KEY_REQUIRED".equals(error.text) || "PUBLIC_KEY_INVALID".equals(error.text) || "SCOPE_EMPTY".equals(error.text) || "PAYLOAD_EMPTY".equals(error.text)) {
                                        ExternalActionActivity.this.setResult(1, new Intent().putExtra("error", error.text));
                                    }
                                    ExternalActionActivity.this.finish();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        });
                    }
                });
            } else {
                final int i2 = intentAccount;
                final Intent intent2 = intent;
                final boolean z = isNew;
                final boolean z2 = restore;
                final boolean z3 = fromPassword;
                AlertsCreator.createAccountSelectDialog(this, new AccountSelectDelegate() {
                    public void didSelectAccount(int account) {
                        if (account != i2) {
                            ExternalActionActivity.this.switchToAccount(account);
                        }
                        ExternalActionActivity.this.handleIntent(intent2, z, z2, z3, account, 1);
                    }
                }).show();
                return true;
            }
            return false;
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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, UserConfig.selectedAccount, 0);
    }

    private void onFinish() {
        if (!this.finished) {
            if (this.lockRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
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
            int y = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            relativeLayoutParams.topMargin = (((AndroidUtilities.displaySize.y - relativeLayoutParams.height) - y) / 2) + y;
            this.layersActionBarLayout.setLayoutParams(relativeLayoutParams);
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int leftWidth = (AndroidUtilities.displaySize.x / 100) * 35;
                if (leftWidth < AndroidUtilities.dp(320.0f)) {
                    leftWidth = AndroidUtilities.dp(320.0f);
                }
                relativeLayoutParams = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
                relativeLayoutParams.width = leftWidth;
                relativeLayoutParams.height = -1;
                this.actionBarLayout.setLayoutParams(relativeLayoutParams);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                    ((BaseFragment) this.actionBarLayout.fragmentsStack.get(1)).onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.actionBarLayout.showLastFragment();
                    return;
                }
                return;
            }
            relativeLayoutParams = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
            relativeLayoutParams.width = -1;
            relativeLayoutParams.height = -1;
            this.actionBarLayout.setLayoutParams(relativeLayoutParams);
        }
    }

    public void fixLayout() {
        if (AndroidUtilities.isTablet() && this.actionBarLayout != null) {
            this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new C14516());
        }
    }

    protected void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
        onPasscodePause();
        if (this.passcodeView != null) {
            this.passcodeView.onPause();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onResume();
        }
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
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            this.lockRunnable = new C14527();
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
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
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
