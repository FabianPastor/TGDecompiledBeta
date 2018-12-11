package org.telegram.p005ui;

import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.p005ui.ActionBar.ActionBarLayout;
import org.telegram.p005ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.DrawerLayoutContainer;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.Theme.ThemeInfo;
import org.telegram.p005ui.Adapters.DrawerLayoutAdapter;
import org.telegram.p005ui.Cells.DrawerAddCell;
import org.telegram.p005ui.Cells.DrawerUserCell;
import org.telegram.p005ui.Cells.LanguageCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.AudioPlayerAlert;
import org.telegram.p005ui.Components.BlockingUpdateView;
import org.telegram.p005ui.Components.EmbedBottomSheet;
import org.telegram.p005ui.Components.JoinGroupAlert;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.PasscodeView;
import org.telegram.p005ui.Components.PipRoundVideoView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.SharingLocationsAlert;
import org.telegram.p005ui.Components.StickersAlert;
import org.telegram.p005ui.Components.TermsOfServiceView;
import org.telegram.p005ui.Components.TermsOfServiceView.TermsOfServiceViewDelegate;
import org.telegram.p005ui.Components.ThemeEditorView;
import org.telegram.p005ui.Components.UpdateAppAlertDialog;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_deepLinkInfo;
import org.telegram.tgnet.TLRPC.TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC.TL_help_getDeepLinkInfo;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;

/* renamed from: org.telegram.ui.LaunchActivity */
public class LaunchActivity extends Activity implements NotificationCenterDelegate, ActionBarLayoutDelegate, DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList();
    private ActionBarLayout actionBarLayout;
    private View backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private ArrayList<User> contactsToSend;
    private Uri contactsToSendUri;
    private int currentAccount;
    private int currentConnectionState;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private AlertDialog localeDialog;
    private Runnable lockRunnable;
    private OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    private ArrayList<SendingMediaInfo> photoPathsArray;
    private AlertDialog proxyErrorDialog;
    private ActionBarLayout rightActionBarLayout;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    private HashMap<String, String> systemLocaleStrings;
    private boolean tabletFullSize;
    private TermsOfServiceView termsOfServiceView;
    private String videoPath;
    private AlertDialog visibleDialog;

    /* renamed from: org.telegram.ui.LaunchActivity$4 */
    class CLASSNAME implements OnDismissListener {
        CLASSNAME() {
        }

        public void onDismiss(DialogInterface dialog) {
            if (LaunchActivity.this.visibleDialog != null) {
                if (LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                    try {
                        Toast.makeText(LaunchActivity.this, LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater), 1).show();
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                    LaunchActivity.this.localeDialog = null;
                } else if (LaunchActivity.this.visibleDialog == LaunchActivity.this.proxyErrorDialog) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    Editor editor = MessagesController.getGlobalMainSettings().edit();
                    editor.putBoolean("proxy_enabled", false);
                    editor.putBoolean("proxy_enabled_calls", false);
                    editor.commit();
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                    ConnectionsManager.setProxySettings(false, TtmlNode.ANONYMOUS_REGION_ID, 1080, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    LaunchActivity.this.proxyErrorDialog = null;
                }
            }
            LaunchActivity.this.visibleDialog = null;
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$5 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            if (LaunchActivity.this.lockRunnable == this) {
                if (AndroidUtilities.needShowPasscode(true)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d("lock app");
                    }
                    LaunchActivity.this.showPasscodeActivity();
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("didn't pass lock check");
                }
                LaunchActivity.this.lockRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$3 */
    class CLASSNAME implements TermsOfServiceViewDelegate {
        CLASSNAME() {
        }

        public void onAcceptTerms(int account) {
            UserConfig.getInstance(account).unacceptedTermsOfService = null;
            UserConfig.getInstance(account).saveConfig(false);
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            LaunchActivity.this.termsOfServiceView.setVisibility(8);
        }

        public void onDeclineTerms(int account) {
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            LaunchActivity.this.termsOfServiceView.setVisibility(8);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:123:0x0778 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x079a A:{SYNTHETIC, Splitter: B:129:0x079a} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07b5 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x07d7 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x07f3 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0815 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0778 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x079a A:{SYNTHETIC, Splitter: B:129:0x079a} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07b5 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x07d7 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x07f3 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0815 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0778 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x079a A:{SYNTHETIC, Splitter: B:129:0x079a} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07b5 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x07d7 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x07f3 A:{Catch:{ Exception -> 0x0794 }} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0815 A:{Catch:{ Exception -> 0x0794 }} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(Bundle savedInstanceState) {
        int dp;
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        this.currentAccount = UserConfig.selectedAccount;
        if (!UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            Intent intent = getIntent();
            boolean isProxy = false;
            if (!(intent == null || intent.getAction() == null)) {
                if ("android.intent.action.SEND".equals(intent.getAction()) || "android.intent.action.SEND_MULTIPLE".equals(intent.getAction())) {
                    super.onCreate(savedInstanceState);
                    finish();
                    return;
                } else if ("android.intent.action.VIEW".equals(intent.getAction())) {
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String url = uri.toString().toLowerCase();
                        isProxy = url.startsWith("tg:proxy") || url.startsWith("tg://proxy") || url.startsWith("tg:socks") || url.startsWith("tg://socks");
                    }
                }
            }
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            long crashed_time = preferences.getLong("intro_crashed_time", 0);
            boolean fromIntro = intent.getBooleanExtra("fromIntro", false);
            if (fromIntro) {
                preferences.edit().putLong("intro_crashed_time", 0).commit();
            }
            if (!(isProxy || Math.abs(crashed_time - System.currentTimeMillis()) < 120000 || intent == null || fromIntro || !ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty())) {
                Intent intent2 = new Intent(this, IntroActivity.class);
                intent2.setData(intent.getData());
                startActivity(intent2);
                super.onCreate(savedInstanceState);
                finish();
                return;
            }
        }
        requestWindowFeature(1);
        setTheme(R.style.Theme.TMessages);
        if (VERSION.SDK_INT >= 21) {
            try {
                setTaskDescription(new TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
            } catch (Exception e) {
            }
        }
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
        }
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= 24) {
            AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
        }
        Theme.createChatResources(this, false);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            View CLASSNAME = new RelativeLayout(this) {
                private boolean inLayout;

                public void requestLayout() {
                    if (!this.inLayout) {
                        super.requestLayout();
                    }
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    this.inLayout = true;
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(width, height);
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.tabletFullSize = true;
                        LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    } else {
                        LaunchActivity.this.tabletFullSize = false;
                        int leftWidth = (width / 100) * 35;
                        if (leftWidth < AndroidUtilities.m9dp(320.0f)) {
                            leftWidth = AndroidUtilities.m9dp(320.0f);
                        }
                        LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(leftWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                        LaunchActivity.this.shadowTabletSide.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(1.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                        LaunchActivity.this.rightActionBarLayout.measure(MeasureSpec.makeMeasureSpec(width - leftWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    }
                    LaunchActivity.this.backgroundTablet.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    LaunchActivity.this.shadowTablet.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    LaunchActivity.this.layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m9dp(530.0f), width), NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m9dp(528.0f), height), NUM));
                    this.inLayout = false;
                }

                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                    } else {
                        int leftWidth = (width / 100) * 35;
                        if (leftWidth < AndroidUtilities.m9dp(320.0f)) {
                            leftWidth = AndroidUtilities.m9dp(320.0f);
                        }
                        LaunchActivity.this.shadowTabletSide.layout(leftWidth, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + leftWidth, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                        LaunchActivity.this.rightActionBarLayout.layout(leftWidth, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + leftWidth, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
                    }
                    int x = (width - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
                    int y = (height - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
                    LaunchActivity.this.layersActionBarLayout.layout(x, y, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + x, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + y);
                    LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                    LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                }
            };
            this.drawerLayoutContainer.addView(CLASSNAME, LayoutHelper.createFrame(-1, -1.0f));
            this.backgroundTablet = new View(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable(drawable);
            CLASSNAME.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            CLASSNAME.addView(this.actionBarLayout);
            this.rightActionBarLayout = new ActionBarLayout(this);
            this.rightActionBarLayout.init(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            CLASSNAME.addView(this.rightActionBarLayout);
            this.shadowTabletSide = new FrameLayout(this);
            this.shadowTabletSide.setBackgroundColor(NUM);
            CLASSNAME.addView(this.shadowTabletSide);
            this.shadowTablet = new FrameLayout(this);
            this.shadowTablet.setVisibility(layerFragmentsStack.isEmpty() ? 8 : 0);
            this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            CLASSNAME.addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new LaunchActivity$$Lambda$0(this));
            this.shadowTablet.setOnClickListener(LaunchActivity$$Lambda$1.$instance);
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            this.layersActionBarLayout.setVisibility(layerFragmentsStack.isEmpty() ? 8 : 0);
            CLASSNAME.addView(this.layersActionBarLayout);
        } else {
            this.drawerLayoutContainer.addView(this.actionBarLayout, new LayoutParams(-1, -1));
        }
        this.sideMenu = new RecyclerListView(this);
        ((DefaultItemAnimator) this.sideMenu.getItemAnimator()).setDelayAnimations(false);
        this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setLayoutManager(new LinearLayoutManager(this, 1, false));
        RecyclerListView recyclerListView = this.sideMenu;
        Adapter drawerLayoutAdapter = new DrawerLayoutAdapter(this);
        this.drawerLayoutAdapter = drawerLayoutAdapter;
        recyclerListView.setAdapter(drawerLayoutAdapter);
        this.drawerLayoutContainer.setDrawerLayout(this.sideMenu);
        LayoutParams layoutParams = (FrameLayout.LayoutParams) this.sideMenu.getLayoutParams();
        Point screenSize = AndroidUtilities.getRealScreenSize();
        if (AndroidUtilities.isTablet()) {
            dp = AndroidUtilities.m9dp(320.0f);
        } else {
            dp = Math.min(AndroidUtilities.m9dp(320.0f), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.m9dp(56.0f));
        }
        layoutParams.width = dp;
        layoutParams.height = -1;
        this.sideMenu.setLayoutParams(layoutParams);
        this.sideMenu.setOnItemClickListener(new LaunchActivity$$Lambda$2(this));
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        Theme.loadWallpaper();
        this.passcodeView = new PasscodeView(this);
        this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        checkCurrentAccount();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsCountUpdated);
        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                DialogsActivity dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSideMenu(this.sideMenu);
                this.actionBarLayout.addFragmentToStack(dialogsActivity);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            } else {
                this.actionBarLayout.addFragmentToStack(new LoginActivity());
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            }
            if (savedInstanceState != null) {
                try {
                    String fragmentName = savedInstanceState.getString("fragment");
                    if (fragmentName != null) {
                        Bundle args = savedInstanceState.getBundle("args");
                        Object obj = -1;
                        switch (fragmentName.hashCode()) {
                            case -1529105743:
                                if (fragmentName.equals("wallpapers")) {
                                    obj = 5;
                                }
                            case -1349522494:
                                if (fragmentName.equals("chat_profile")) {
                                    obj = 4;
                                }
                            case 3052376:
                                if (fragmentName.equals("chat")) {
                                    obj = null;
                                }
                            case 98629247:
                                if (fragmentName.equals("group")) {
                                    obj = 2;
                                }
                                BaseFragment settings;
                                BaseFragment groupCreateFinalActivity;
                                switch (obj) {
                                    case null:
                                        if (args != null) {
                                            ChatActivity chat = new ChatActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(chat)) {
                                                chat.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 1:
                                        settings = new SettingsActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                    case 2:
                                        if (args != null) {
                                            groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (args != null) {
                                            ChannelCreateActivity channel = new ChannelCreateActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel)) {
                                                channel.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (args != null) {
                                            groupCreateFinalActivity = new ProfileActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        settings = new WallpapersActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                }
                                break;
                            case 738950403:
                                if (fragmentName.equals("channel")) {
                                    obj = 3;
                                }
                                switch (obj) {
                                    case null:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                }
                                break;
                            case 1434631203:
                                if (fragmentName.equals("settings")) {
                                    obj = 1;
                                }
                                switch (obj) {
                                    case null:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                }
                                break;
                        }
                        switch (obj) {
                            case null:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                        }
                    }
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
            }
        } else {
            BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(0);
            if (fragment instanceof DialogsActivity) {
                ((DialogsActivity) fragment).setSideMenu(this.sideMenu);
            }
            boolean allowOpen = true;
            if (AndroidUtilities.isTablet()) {
                allowOpen = this.actionBarLayout.fragmentsStack.size() <= 1 && this.layersActionBarLayout.fragmentsStack.isEmpty();
                if (this.layersActionBarLayout.fragmentsStack.size() == 1 && (this.layersActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                    allowOpen = false;
                }
            }
            if (this.actionBarLayout.fragmentsStack.size() == 1 && (this.actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                allowOpen = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(allowOpen, false);
        }
        checkLayout();
        handleIntent(getIntent(), false, savedInstanceState != null, false);
        try {
            String os1 = Build.DISPLAY;
            String os2 = Build.USER;
            if (os1 != null) {
                os1 = os1.toLowerCase();
            } else {
                os1 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (os2 != null) {
                os2 = os1.toLowerCase();
            } else {
                os2 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (os1.contains("flyme") || os2.contains("flyme")) {
                AndroidUtilities.incorrectDisplaySizeFix = true;
                View view = getWindow().getDecorView().getRootView();
                ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                OnGlobalLayoutListener launchActivity$$Lambda$3 = new LaunchActivity$$Lambda$3(view);
                this.onGlobalLayoutListener = launchActivity$$Lambda$3;
                viewTreeObserver.addOnGlobalLayoutListener(launchActivity$$Lambda$3);
            }
        } catch (Throwable e222) {
            FileLog.m13e(e222);
        }
        MediaController.getInstance().setBaseActivity(this, true);
    }

    final /* synthetic */ boolean lambda$onCreate$0$LaunchActivity(View v, MotionEvent event) {
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
            int a = 0;
            while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                a = (a - 1) + 1;
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    static final /* synthetic */ void lambda$onCreate$1$LaunchActivity(View v) {
    }

    final /* synthetic */ void lambda$onCreate$2$LaunchActivity(View view, int position) {
        boolean z = false;
        if (position == 0) {
            DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
            if (!this.drawerLayoutAdapter.isAccountsShowed()) {
                z = true;
            }
            drawerLayoutAdapter.setAccountsShowed(z, true);
        } else if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (view instanceof DrawerAddCell) {
            int freeAccount = -1;
            for (int a = 0; a < 3; a++) {
                if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginActivity(freeAccount));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(position);
            Bundle args;
            if (id == 2) {
                args = new Bundle();
                args.putBoolean("showFabButton", true);
                presentFragment(new GroupCreateActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                presentFragment(new ContactsActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                    presentFragment(new ChannelIntroActivity());
                    preferences.edit().putBoolean("channel_intro", true).commit();
                } else {
                    args = new Bundle();
                    args.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(args));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                presentFragment(new ContactsActivity(null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                presentFragment(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                presentFragment(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                presentFragment(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                args = new Bundle();
                args.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                presentFragment(new ChatActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    static final /* synthetic */ void lambda$onCreate$3$LaunchActivity(View view) {
        int height = view.getMeasuredHeight();
        if (VERSION.SDK_INT >= 21) {
            height -= AndroidUtilities.statusBarHeight;
        }
        if (height > AndroidUtilities.m9dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.m9dp(100.0f) + height > AndroidUtilities.displaySize.y) {
            AndroidUtilities.displaySize.y = height;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    public void switchToAccount(int account, boolean removeAll) {
        if (account != UserConfig.selectedAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = account;
            UserConfig.getInstance(0).saveConfig(false);
            checkCurrentAccount();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.removeAllFragments();
                if (!this.tabletFullSize) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        this.backgroundTablet.setVisibility(0);
                    }
                    this.rightActionBarLayout.setVisibility(8);
                }
                this.layersActionBarLayout.setVisibility(8);
            }
            if (removeAll) {
                this.actionBarLayout.removeAllFragments();
            } else {
                this.actionBarLayout.removeFragmentFromStack(0);
            }
            DialogsActivity dialogsActivity = new DialogsActivity(null);
            dialogsActivity.setSideMenu(this.sideMenu);
            this.actionBarLayout.addFragmentToStack(dialogsActivity, 0);
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
            if (!ApplicationLoader.mainInterfacePaused) {
                ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
            }
            if (UserConfig.getInstance(account).unacceptedTermsOfService != null) {
                showTosActivity(account, UserConfig.getInstance(account).unacceptedTermsOfService);
            }
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int account = -1;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                account = a;
                break;
            }
        }
        if (this.termsOfServiceView != null) {
            this.termsOfServiceView.setVisibility(8);
        }
        if (account != -1) {
            switchToAccount(account, true);
            return;
        }
        if (this.drawerLayoutAdapter != null) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        }
        Iterator it = this.actionBarLayout.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).onFragmentDestroy();
        }
        this.actionBarLayout.fragmentsStack.clear();
        if (AndroidUtilities.isTablet()) {
            it = this.layersActionBarLayout.fragmentsStack.iterator();
            while (it.hasNext()) {
                ((BaseFragment) it.next()).onFragmentDestroy();
            }
            this.layersActionBarLayout.fragmentsStack.clear();
            it = this.rightActionBarLayout.fragmentsStack.iterator();
            while (it.hasNext()) {
                ((BaseFragment) it.next()).onFragmentDestroy();
            }
            this.rightActionBarLayout.fragmentsStack.clear();
        }
        startActivity(new Intent(this, IntroActivity.class));
        onFinish();
        finish();
    }

    public int getMainFragmentsCount() {
        return mainFragmentsStack.size();
    }

    private void checkCurrentAccount() {
        if (this.currentAccount != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
        }
        this.currentAccount = UserConfig.selectedAccount;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        updateCurrentConnectionState(this.currentAccount);
    }

    private void checkLayout() {
        int i = 0;
        int i2 = 8;
        if (AndroidUtilities.isTablet() && this.rightActionBarLayout != null) {
            int a;
            BaseFragment chatFragment;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                this.tabletFullSize = true;
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    a = 0;
                    while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                        chatFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(a);
                        if (chatFragment instanceof ChatActivity) {
                            ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                        }
                        chatFragment.onPause();
                        this.rightActionBarLayout.fragmentsStack.remove(a);
                        this.actionBarLayout.fragmentsStack.add(chatFragment);
                        a = (a - 1) + 1;
                    }
                    if (this.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                    }
                }
                this.shadowTabletSide.setVisibility(8);
                this.rightActionBarLayout.setVisibility(8);
                View view = this.backgroundTablet;
                if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i2 = 0;
                }
                view.setVisibility(i2);
                return;
            }
            int i3;
            this.tabletFullSize = false;
            if (this.actionBarLayout.fragmentsStack.size() >= 2) {
                for (a = 1; a < this.actionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                    chatFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(a);
                    if (chatFragment instanceof ChatActivity) {
                        ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                    }
                    chatFragment.onPause();
                    this.actionBarLayout.fragmentsStack.remove(a);
                    this.rightActionBarLayout.fragmentsStack.add(chatFragment);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            ActionBarLayout actionBarLayout = this.rightActionBarLayout;
            if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            actionBarLayout.setVisibility(i3);
            View view2 = this.backgroundTablet;
            if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                i3 = 0;
            } else {
                i3 = 8;
            }
            view2.setVisibility(i3);
            FrameLayout frameLayout = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    private void showUpdateActivity(int account, TL_help_appUpdate update) {
        if (this.blockingUpdateView == null) {
            this.blockingUpdateView = new BlockingUpdateView(this) {
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (visibility == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.drawerLayoutContainer.addView(this.blockingUpdateView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(account, update);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int account, TL_help_termsOfService tos) {
        if (this.termsOfServiceView == null) {
            this.termsOfServiceView = new TermsOfServiceView(this);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new CLASSNAME());
        }
        TL_help_termsOfService currentTos = UserConfig.getInstance(account).unacceptedTermsOfService;
        if (currentTos != tos && (currentTos == null || !currentTos.var_id.data.equals(tos.var_id.data))) {
            UserConfig.getInstance(account).unacceptedTermsOfService = tos;
            UserConfig.getInstance(account).saveConfig(false);
        }
        this.termsOfServiceView.show(account, tos);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
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
            this.passcodeView.setDelegate(new LaunchActivity$$Lambda$4(this));
        }
    }

    final /* synthetic */ void lambda$showPasscodeActivity$4$LaunchActivity() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        if (this.passcodeSaveIntent != null) {
            handleIntent(this.passcodeSaveIntent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x0170  */
    /* JADX WARNING: Missing block: B:55:0x01db, code:
            if (org.telegram.messenger.MessagesController.getInstance(r47[0]).checkCanOpenChat(r31, (org.telegram.p005ui.ActionBar.BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)) != false) goto L_0x01dd;
     */
    /* JADX WARNING: Missing block: B:86:0x02a8, code:
            if (r68.startsWith("https://") != false) goto L_0x02aa;
     */
    /* JADX WARNING: Missing block: B:105:0x0308, code:
            if (r70.startsWith("image/") == false) goto L_0x030a;
     */
    /* JADX WARNING: Missing block: B:107:0x0319, code:
            if (r71.toString().toLowerCase().endsWith(".jpg") != false) goto L_0x031b;
     */
    /* JADX WARNING: Missing block: B:109:0x031f, code:
            if (r76.photoPathsArray != null) goto L_0x032a;
     */
    /* JADX WARNING: Missing block: B:110:0x0321, code:
            r76.photoPathsArray = new java.util.ArrayList();
     */
    /* JADX WARNING: Missing block: B:111:0x032a, code:
            r46 = new org.telegram.messenger.SendMessagesHelper.SendingMediaInfo();
            r46.uri = r71;
            r76.photoPathsArray.add(r46);
     */
    /* JADX WARNING: Missing block: B:214:0x05c0, code:
            if (r44.equals("telesco.pe") != false) goto L_0x05c2;
     */
    /* JADX WARNING: Missing block: B:441:0x0de9, code:
            if (org.telegram.messenger.MessagesController.getInstance(r47[0]).checkCanOpenChat(r31, (org.telegram.p005ui.ActionBar.BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)) != false) goto L_0x0deb;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword) {
        if (AndroidUtilities.handleProxyIntent(this, intent)) {
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
            return true;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible() && (intent == null || !"android.intent.action.MAIN".equals(intent.getAction()))) {
            PhotoViewer.getInstance().closePhoto(false, true);
        }
        int flags = intent.getFlags();
        int[] intentAccount = new int[1];
        intentAccount[0] = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        switchToAccount(intentAccount[0], true);
        if (fromPassword || !(AndroidUtilities.needShowPasscode(true) || SharedConfig.isWaitingForPasscodeEnter)) {
            int a;
            Bundle args;
            boolean pushOpened = false;
            Integer push_user_id = Integer.valueOf(0);
            Integer push_chat_id = Integer.valueOf(0);
            Integer push_enc_id = Integer.valueOf(0);
            Integer push_msg_id = Integer.valueOf(0);
            Integer open_settings = Integer.valueOf(0);
            Integer open_new_dialog = Integer.valueOf(0);
            long dialogId = 0;
            if (SharedConfig.directShare) {
                dialogId = (intent == null || intent.getExtras() == null) ? 0 : intent.getExtras().getLong("dialogId", 0);
            }
            boolean showDialogsList = false;
            boolean showPlayer = false;
            boolean showLocations = false;
            this.photoPathsArray = null;
            this.videoPath = null;
            this.sendingText = null;
            this.documentsPathsArray = null;
            this.documentsOriginalPathsArray = null;
            this.documentsMimeType = null;
            this.documentsUrisArray = null;
            this.contactsToSend = null;
            this.contactsToSendUri = null;
            if (!((ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES & flags) != 0 || intent == null || intent.getAction() == null || restore)) {
                boolean error;
                String type;
                Uri uri;
                Parcelable parcelable;
                String path;
                int userId;
                if ("android.intent.action.SEND".equals(intent.getAction())) {
                    error = false;
                    type = intent.getType();
                    if (type != null) {
                        if (type.equals("text/x-vcard")) {
                            try {
                                uri = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
                                if (uri != null) {
                                    this.contactsToSend = AndroidUtilities.loadVCardFromStream(uri, this.currentAccount, false, null, null);
                                    this.contactsToSendUri = uri;
                                } else {
                                    error = true;
                                }
                            } catch (Throwable e) {
                                FileLog.m13e(e);
                                error = true;
                            }
                            if (error) {
                                Toast.makeText(this, "Unsupported content", 0).show();
                            }
                        }
                    }
                    String text = intent.getStringExtra("android.intent.extra.TEXT");
                    if (text == null) {
                        CharSequence textSequence = intent.getCharSequenceExtra("android.intent.extra.TEXT");
                        if (textSequence != null) {
                            text = textSequence.toString();
                        }
                    }
                    String subject = intent.getStringExtra("android.intent.extra.SUBJECT");
                    if (!TextUtils.isEmpty(text)) {
                        if (!text.startsWith("http://")) {
                        }
                        if (!TextUtils.isEmpty(subject)) {
                            text = subject + "\n" + text;
                        }
                        this.sendingText = text;
                    } else if (!TextUtils.isEmpty(subject)) {
                        this.sendingText = subject;
                    }
                    parcelable = intent.getParcelableExtra("android.intent.extra.STREAM");
                    if (parcelable != null) {
                        if (!(parcelable instanceof Uri)) {
                            parcelable = Uri.parse(parcelable.toString());
                        }
                        uri = (Uri) parcelable;
                        if (uri != null && AndroidUtilities.isInternalUri(uri)) {
                            error = true;
                        }
                        if (!error) {
                            if (uri != null) {
                                if (type != null) {
                                }
                            }
                            path = AndroidUtilities.getPath(uri);
                            if (path != null) {
                                if (path.startsWith("file:")) {
                                    path = path.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                }
                                if (type != null) {
                                    if (type.startsWith("video/")) {
                                        this.videoPath = path;
                                    }
                                }
                                if (this.documentsPathsArray == null) {
                                    this.documentsPathsArray = new ArrayList();
                                    this.documentsOriginalPathsArray = new ArrayList();
                                }
                                this.documentsPathsArray.add(path);
                                this.documentsOriginalPathsArray.add(uri.toString());
                            } else {
                                if (this.documentsUrisArray == null) {
                                    this.documentsUrisArray = new ArrayList();
                                }
                                this.documentsUrisArray.add(uri);
                                this.documentsMimeType = type;
                            }
                        }
                    } else if (this.sendingText == null) {
                        error = true;
                    }
                    if (error) {
                    }
                } else if ("android.intent.action.SEND_MULTIPLE".equals(intent.getAction())) {
                    error = false;
                    try {
                        ArrayList<Parcelable> uris = intent.getParcelableArrayListExtra("android.intent.extra.STREAM");
                        type = intent.getType();
                        if (uris != null) {
                            a = 0;
                            while (a < uris.size()) {
                                parcelable = (Parcelable) uris.get(a);
                                if (!(parcelable instanceof Uri)) {
                                    parcelable = Uri.parse(parcelable.toString());
                                }
                                uri = (Uri) parcelable;
                                if (uri != null && AndroidUtilities.isInternalUri(uri)) {
                                    uris.remove(a);
                                    a--;
                                }
                                a++;
                            }
                            if (uris.isEmpty()) {
                                uris = null;
                            }
                        }
                        if (uris != null) {
                            if (type != null) {
                                if (type.startsWith("image/")) {
                                    for (a = 0; a < uris.size(); a++) {
                                        parcelable = (Parcelable) uris.get(a);
                                        if (!(parcelable instanceof Uri)) {
                                            parcelable = Uri.parse(parcelable.toString());
                                        }
                                        uri = (Uri) parcelable;
                                        if (this.photoPathsArray == null) {
                                            this.photoPathsArray = new ArrayList();
                                        }
                                        SendingMediaInfo info = new SendingMediaInfo();
                                        info.uri = uri;
                                        this.photoPathsArray.add(info);
                                    }
                                }
                            }
                            for (a = 0; a < uris.size(); a++) {
                                parcelable = (Parcelable) uris.get(a);
                                if (!(parcelable instanceof Uri)) {
                                    parcelable = Uri.parse(parcelable.toString());
                                }
                                uri = (Uri) parcelable;
                                path = AndroidUtilities.getPath(uri);
                                String originalPath = parcelable.toString();
                                if (originalPath == null) {
                                    originalPath = path;
                                }
                                if (path != null) {
                                    if (path.startsWith("file:")) {
                                        path = path.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                    }
                                    if (this.documentsPathsArray == null) {
                                        this.documentsPathsArray = new ArrayList();
                                        this.documentsOriginalPathsArray = new ArrayList();
                                    }
                                    this.documentsPathsArray.add(path);
                                    this.documentsOriginalPathsArray.add(originalPath);
                                } else {
                                    if (this.documentsUrisArray == null) {
                                        this.documentsUrisArray = new ArrayList();
                                    }
                                    this.documentsUrisArray.add(uri);
                                    this.documentsMimeType = type;
                                }
                            }
                        } else {
                            error = true;
                        }
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                        error = true;
                    }
                    if (error) {
                        Toast.makeText(this, "Unsupported content", 0).show();
                    }
                } else if ("android.intent.action.VIEW".equals(intent.getAction())) {
                    Uri data = intent.getData();
                    if (data != null) {
                        String username = null;
                        String group = null;
                        String sticker = null;
                        String[] instantView = null;
                        HashMap<String, String> auth = null;
                        String unsupportedUrl = null;
                        String botUser = null;
                        String botChat = null;
                        String message = null;
                        String phone = null;
                        String game = null;
                        String phoneHash = null;
                        String lang = null;
                        String code = null;
                        Integer messageId = null;
                        boolean hasUrl = false;
                        String scheme = data.getScheme();
                        if (scheme != null) {
                            if (!scheme.equals("http")) {
                                if (!scheme.equals("https")) {
                                    if (scheme.equals("tg")) {
                                        String scope;
                                        String url = data.toString();
                                        if (!url.startsWith("tg:resolve")) {
                                            if (!url.startsWith("tg://resolve")) {
                                                if (!url.startsWith("tg:join")) {
                                                    if (!url.startsWith("tg://join")) {
                                                        if (!url.startsWith("tg:addstickers")) {
                                                            if (!url.startsWith("tg://addstickers")) {
                                                                if (!url.startsWith("tg:msg")) {
                                                                    if (!url.startsWith("tg://msg")) {
                                                                        if (!url.startsWith("tg://share")) {
                                                                            if (!url.startsWith("tg:share")) {
                                                                                if (!url.startsWith("tg:confirmphone")) {
                                                                                    if (!url.startsWith("tg://confirmphone")) {
                                                                                        if (!url.startsWith("tg:openmessage")) {
                                                                                            if (!url.startsWith("tg://openmessage")) {
                                                                                                if (!url.startsWith("tg:passport")) {
                                                                                                    if (!url.startsWith("tg://passport")) {
                                                                                                        if (!url.startsWith("tg:secureid")) {
                                                                                                            if (!url.startsWith("tg:setlanguage")) {
                                                                                                                if (!url.startsWith("tg://setlanguage")) {
                                                                                                                    unsupportedUrl = url.replace("tg://", TtmlNode.ANONYMOUS_REGION_ID).replace("tg:", TtmlNode.ANONYMOUS_REGION_ID);
                                                                                                                    int index = unsupportedUrl.indexOf(63);
                                                                                                                    if (index >= 0) {
                                                                                                                        unsupportedUrl = unsupportedUrl.substring(0, index);
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                            lang = Uri.parse(url.replace("tg:setlanguage", "tg://telegram.org").replace("tg://setlanguage", "tg://telegram.org")).getQueryParameter("lang");
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                data = Uri.parse(url.replace("tg:passport", "tg://telegram.org").replace("tg://passport", "tg://telegram.org").replace("tg:secureid", "tg://telegram.org"));
                                                                                                auth = new HashMap();
                                                                                                scope = data.getQueryParameter("scope");
                                                                                                if (!TextUtils.isEmpty(scope)) {
                                                                                                    if (scope.startsWith("{")) {
                                                                                                        if (scope.endsWith("}")) {
                                                                                                            auth.put("nonce", data.getQueryParameter("nonce"));
                                                                                                            auth.put("bot_id", data.getQueryParameter("bot_id"));
                                                                                                            auth.put("scope", scope);
                                                                                                            auth.put("public_key", data.getQueryParameter("public_key"));
                                                                                                            auth.put("callback_url", data.getQueryParameter("callback_url"));
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                auth.put("payload", data.getQueryParameter("payload"));
                                                                                                auth.put("bot_id", data.getQueryParameter("bot_id"));
                                                                                                auth.put("scope", scope);
                                                                                                auth.put("public_key", data.getQueryParameter("public_key"));
                                                                                                auth.put("callback_url", data.getQueryParameter("callback_url"));
                                                                                            }
                                                                                        }
                                                                                        data = Uri.parse(url.replace("tg:openmessage", "tg://telegram.org").replace("tg://openmessage", "tg://telegram.org"));
                                                                                        String userID = data.getQueryParameter("user_id");
                                                                                        String chatID = data.getQueryParameter("chat_id");
                                                                                        String msgID = data.getQueryParameter("message_id");
                                                                                        if (userID != null) {
                                                                                            try {
                                                                                                push_user_id = Integer.valueOf(Integer.parseInt(userID));
                                                                                            } catch (NumberFormatException e3) {
                                                                                            }
                                                                                        } else if (chatID != null) {
                                                                                            try {
                                                                                                push_chat_id = Integer.valueOf(Integer.parseInt(chatID));
                                                                                            } catch (NumberFormatException e4) {
                                                                                            }
                                                                                        }
                                                                                        if (msgID != null) {
                                                                                            try {
                                                                                                push_msg_id = Integer.valueOf(Integer.parseInt(msgID));
                                                                                            } catch (NumberFormatException e5) {
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                                data = Uri.parse(url.replace("tg:confirmphone", "tg://telegram.org").replace("tg://confirmphone", "tg://telegram.org"));
                                                                                phone = data.getQueryParameter("phone");
                                                                                phoneHash = data.getQueryParameter("hash");
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                data = Uri.parse(url.replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                                                                message = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                                if (message == null) {
                                                                    message = TtmlNode.ANONYMOUS_REGION_ID;
                                                                }
                                                                if (data.getQueryParameter("text") != null) {
                                                                    if (message.length() > 0) {
                                                                        hasUrl = true;
                                                                        message = message + "\n";
                                                                    }
                                                                    message = message + data.getQueryParameter("text");
                                                                }
                                                                if (message.length() > MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                                                                    message = message.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                                                }
                                                                while (message.endsWith("\n")) {
                                                                    message = message.substring(0, message.length() - 1);
                                                                }
                                                            }
                                                        }
                                                        sticker = Uri.parse(url.replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org")).getQueryParameter("set");
                                                    }
                                                }
                                                group = Uri.parse(url.replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org")).getQueryParameter("invite");
                                            }
                                        }
                                        data = Uri.parse(url.replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                                        username = data.getQueryParameter("domain");
                                        if ("telegrampassport".equals(username)) {
                                            username = null;
                                            auth = new HashMap();
                                            scope = data.getQueryParameter("scope");
                                            if (!TextUtils.isEmpty(scope)) {
                                                if (scope.startsWith("{")) {
                                                    if (scope.endsWith("}")) {
                                                        auth.put("nonce", data.getQueryParameter("nonce"));
                                                        auth.put("bot_id", data.getQueryParameter("bot_id"));
                                                        auth.put("scope", scope);
                                                        auth.put("public_key", data.getQueryParameter("public_key"));
                                                        auth.put("callback_url", data.getQueryParameter("callback_url"));
                                                    }
                                                }
                                            }
                                            auth.put("payload", data.getQueryParameter("payload"));
                                            auth.put("bot_id", data.getQueryParameter("bot_id"));
                                            auth.put("scope", scope);
                                            auth.put("public_key", data.getQueryParameter("public_key"));
                                            auth.put("callback_url", data.getQueryParameter("callback_url"));
                                        } else {
                                            botUser = data.getQueryParameter(TtmlNode.START);
                                            botChat = data.getQueryParameter("startgroup");
                                            game = data.getQueryParameter("game");
                                            messageId = Utilities.parseInt(data.getQueryParameter("post"));
                                            if (messageId.intValue() == 0) {
                                                messageId = null;
                                            }
                                        }
                                    }
                                }
                            }
                            String host = data.getHost().toLowerCase();
                            if (!host.equals("telegram.me")) {
                                if (!host.equals("t.me")) {
                                    if (!host.equals("telegram.dog")) {
                                    }
                                }
                            }
                            path = data.getPath();
                            if (path != null && path.length() > 1) {
                                path = path.substring(1);
                                if (path.startsWith("login/")) {
                                    code = path.replace("login/", TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    if (path.startsWith("joinchat/")) {
                                        group = path.replace("joinchat/", TtmlNode.ANONYMOUS_REGION_ID);
                                    } else {
                                        if (path.startsWith("addstickers/")) {
                                            sticker = path.replace("addstickers/", TtmlNode.ANONYMOUS_REGION_ID);
                                        } else {
                                            if (path.startsWith("iv/")) {
                                                null[0] = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                null[1] = data.getQueryParameter("rhash");
                                                if (TextUtils.isEmpty(null[0]) || TextUtils.isEmpty(null[1])) {
                                                    instantView = null;
                                                }
                                            } else {
                                                if (!path.startsWith("msg/")) {
                                                    if (!path.startsWith("share/")) {
                                                        if (path.startsWith("confirmphone")) {
                                                            phone = data.getQueryParameter("phone");
                                                            phoneHash = data.getQueryParameter("hash");
                                                        } else {
                                                            if (path.startsWith("setlanguage/")) {
                                                                lang = path.substring(12);
                                                            } else if (path.length() >= 1) {
                                                                List<String> segments = data.getPathSegments();
                                                                if (segments.size() > 0) {
                                                                    username = (String) segments.get(0);
                                                                    if (segments.size() > 1) {
                                                                        messageId = Utilities.parseInt((String) segments.get(1));
                                                                        if (messageId.intValue() == 0) {
                                                                            messageId = null;
                                                                        }
                                                                    }
                                                                }
                                                                botUser = data.getQueryParameter(TtmlNode.START);
                                                                botChat = data.getQueryParameter("startgroup");
                                                                game = data.getQueryParameter("game");
                                                            }
                                                        }
                                                    }
                                                }
                                                message = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                if (message == null) {
                                                    message = TtmlNode.ANONYMOUS_REGION_ID;
                                                }
                                                if (data.getQueryParameter("text") != null) {
                                                    if (message.length() > 0) {
                                                        hasUrl = true;
                                                        message = message + "\n";
                                                    }
                                                    message = message + data.getQueryParameter("text");
                                                }
                                                if (message.length() > MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                                                    message = message.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                                }
                                                while (message.endsWith("\n")) {
                                                    message = message.substring(0, message.length() - 1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (code != null || UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                            if (phone != null || phoneHash != null) {
                                args = new Bundle();
                                args.putString("phone", phone);
                                args.putString("hash", phoneHash);
                                AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$5(this, args));
                            } else if (username == null && group == null && sticker == null && message == null && game == null && instantView == null && auth == null && unsupportedUrl == null && lang == null && code == null) {
                                Cursor cursor = null;
                                try {
                                    cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        int accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                        for (a = 0; a < 3; a++) {
                                            if (UserConfig.getInstance(a).getClientUserId() == accountId) {
                                                intentAccount[0] = a;
                                                switchToAccount(intentAccount[0], true);
                                                break;
                                            }
                                        }
                                        userId = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                        push_user_id = Integer.valueOf(userId);
                                    }
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                } catch (Throwable e22) {
                                    FileLog.m13e(e22);
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                } catch (Throwable th) {
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                }
                            } else {
                                if (message != null && message.startsWith("@")) {
                                    message = " " + message;
                                }
                                runLinkRequest(intentAccount[0], username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code, 0);
                            }
                        }
                    }
                } else if (intent.getAction().equals("org.telegram.messenger.OPEN_ACCOUNT")) {
                    open_settings = Integer.valueOf(1);
                } else if (intent.getAction().equals("new_dialog")) {
                    open_new_dialog = Integer.valueOf(1);
                } else if (intent.getAction().startsWith("com.tmessages.openchat")) {
                    int chatId = intent.getIntExtra("chatId", 0);
                    userId = intent.getIntExtra("userId", 0);
                    int encId = intent.getIntExtra("encId", 0);
                    if (chatId != 0) {
                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        push_chat_id = Integer.valueOf(chatId);
                    } else if (userId != 0) {
                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        push_user_id = Integer.valueOf(userId);
                    } else if (encId != 0) {
                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        push_enc_id = Integer.valueOf(encId);
                    } else {
                        showDialogsList = true;
                    }
                } else if (intent.getAction().equals("com.tmessages.openplayer")) {
                    showPlayer = true;
                } else if (intent.getAction().equals("org.tmessages.openlocations")) {
                    showLocations = true;
                }
            }
            if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                if (push_user_id.intValue() != 0) {
                    args = new Bundle();
                    args.putInt("user_id", push_user_id.intValue());
                    if (push_msg_id.intValue() != 0) {
                        args.putInt("message_id", push_msg_id.intValue());
                    }
                    if (!mainFragmentsStack.isEmpty()) {
                    }
                    if (this.actionBarLayout.presentFragment(new ChatActivity(args), false, true, true, false)) {
                        pushOpened = true;
                    }
                } else if (push_chat_id.intValue() != 0) {
                    args = new Bundle();
                    args.putInt("chat_id", push_chat_id.intValue());
                    if (push_msg_id.intValue() != 0) {
                        args.putInt("message_id", push_msg_id.intValue());
                    }
                    if (!mainFragmentsStack.isEmpty()) {
                    }
                    if (this.actionBarLayout.presentFragment(new ChatActivity(args), false, true, true, false)) {
                        pushOpened = true;
                    }
                } else if (push_enc_id.intValue() != 0) {
                    args = new Bundle();
                    args.putInt("enc_id", push_enc_id.intValue());
                    if (this.actionBarLayout.presentFragment(new ChatActivity(args), false, true, true, false)) {
                        pushOpened = true;
                    }
                } else if (showDialogsList) {
                    if (!AndroidUtilities.isTablet()) {
                        this.actionBarLayout.removeAllFragments();
                    } else if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(false);
                    }
                    pushOpened = false;
                    isNew = false;
                } else if (showPlayer) {
                    if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                        ((BaseFragment) this.actionBarLayout.fragmentsStack.get(0)).showDialog(new AudioPlayerAlert(this));
                    }
                    pushOpened = false;
                } else if (showLocations) {
                    if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                        ((BaseFragment) this.actionBarLayout.fragmentsStack.get(0)).showDialog(new SharingLocationsAlert(this, new LaunchActivity$$Lambda$6(this, intentAccount)));
                    }
                    pushOpened = false;
                } else if (this.videoPath != null || this.photoPathsArray != null || this.sendingText != null || this.documentsPathsArray != null || this.contactsToSend != null || this.documentsUrisArray != null) {
                    if (!AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    }
                    if (dialogId == 0) {
                        args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", 3);
                        args.putBoolean("allowSwitchAccount", true);
                        if (this.contactsToSend == null) {
                            args.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                            args.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                        } else if (this.contactsToSend.size() != 1) {
                            args.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                            args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                        }
                        DialogsActivity dialogsActivity = new DialogsActivity(args);
                        dialogsActivity.setDelegate(this);
                        boolean removeLast = AndroidUtilities.isTablet() ? this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                        this.actionBarLayout.presentFragment(dialogsActivity, removeLast, true, true, false);
                        pushOpened = true;
                        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                            SecretMediaViewer.getInstance().closePhoto(false, false);
                        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                            PhotoViewer.getInstance().closePhoto(false, true);
                        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                            ArticleViewer.getInstance().close(false, true);
                        }
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (AndroidUtilities.isTablet()) {
                            this.actionBarLayout.showLastFragment();
                            this.rightActionBarLayout.showLastFragment();
                        } else {
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else {
                        ArrayList<Long> dids = new ArrayList();
                        dids.add(Long.valueOf(dialogId));
                        didSelectDialogs(null, dids, null, false);
                    }
                } else if (open_settings.intValue() != 0) {
                    this.actionBarLayout.presentFragment(new SettingsActivity(), false, true, true, false);
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                    pushOpened = true;
                } else if (open_new_dialog.intValue() != 0) {
                    args = new Bundle();
                    args.putBoolean("destroyAfterSelect", true);
                    this.actionBarLayout.presentFragment(new ContactsActivity(args), false, true, true, false);
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                    pushOpened = true;
                }
            }
            if (!(pushOpened || isNew)) {
                BaseFragment dialogsActivity2;
                if (AndroidUtilities.isTablet()) {
                    if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                            dialogsActivity2 = new DialogsActivity(null);
                            dialogsActivity2.setSideMenu(this.sideMenu);
                            this.actionBarLayout.addFragmentToStack(dialogsActivity2);
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                        dialogsActivity2 = new DialogsActivity(null);
                        dialogsActivity2.setSideMenu(this.sideMenu);
                        this.actionBarLayout.addFragmentToStack(dialogsActivity2);
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    } else {
                        this.actionBarLayout.addFragmentToStack(new LoginActivity());
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                }
                this.actionBarLayout.showLastFragment();
                if (AndroidUtilities.isTablet()) {
                    this.layersActionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            intent.setAction(null);
            return pushOpened;
        }
        showPasscodeActivity();
        this.passcodeSaveIntent = intent;
        this.passcodeSaveIntentIsNew = isNew;
        this.passcodeSaveIntentIsRestore = restore;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        return false;
    }

    final /* synthetic */ void lambda$handleIntent$5$LaunchActivity(Bundle args) {
        presentFragment(new CancelAccountDeletionActivity(args));
    }

    final /* synthetic */ void lambda$handleIntent$7$LaunchActivity(int[] intentAccount, SharingLocationInfo info) {
        intentAccount[0] = info.messageObject.currentAccount;
        switchToAccount(intentAccount[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(info.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$Lambda$50(intentAccount, info.messageObject.getDialogId()));
        presentFragment(locationActivity);
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, HashMap<String, String> auth, String lang, String unsupportedUrl, String code, int state) {
        if (state == 0 && UserConfig.getActivatedAccountsCount() >= 2 && auth != null) {
            AlertsCreator.createAccountSelectDialog(this, new LaunchActivity$$Lambda$7(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code)).show();
        } else if (code == null) {
            AlertDialog progressDialog = new AlertDialog(this, 3);
            int[] requestId = new int[]{0};
            TLObject req;
            if (username != null) {
                req = new TL_contacts_resolveUsername();
                req.username = username;
                requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$8(this, progressDialog, game, intentAccount, botChat, botUser, messageId));
            } else if (group != null) {
                if (state == 0) {
                    TLObject req2 = new TL_messages_checkChatInvite();
                    req2.hash = group;
                    requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req2, new LaunchActivity$$Lambda$9(this, progressDialog, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code), 2);
                } else if (state == 1) {
                    req = new TL_messages_importChatInvite();
                    req.hash = group;
                    ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$10(this, intentAccount, progressDialog), 2);
                }
            } else if (sticker != null) {
                if (!mainFragmentsStack.isEmpty()) {
                    TL_inputStickerSetShortName stickerset = new TL_inputStickerSetShortName();
                    stickerset.short_name = sticker;
                    BaseFragment fragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                    fragment.showDialog(new StickersAlert(this, fragment, stickerset, null, null));
                    return;
                }
                return;
            } else if (message != null) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                DialogsActivity fragment2 = new DialogsActivity(args);
                fragment2.setDelegate(new LaunchActivity$$Lambda$11(this, hasUrl, intentAccount, message));
                presentFragment(fragment2, false, true);
            } else if (instantView == null) {
                if (auth != null) {
                    int bot_id = Utilities.parseInt((String) auth.get("bot_id")).intValue();
                    if (bot_id != 0) {
                        String payload = (String) auth.get("payload");
                        String nonce = (String) auth.get("nonce");
                        String callbackUrl = (String) auth.get("callback_url");
                        req = new TL_account_getAuthorizationForm();
                        req.bot_id = bot_id;
                        req.scope = (String) auth.get("scope");
                        req.public_key = (String) auth.get("public_key");
                        requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$12(this, requestId, intentAccount, progressDialog, req, payload, nonce, callbackUrl));
                    } else {
                        return;
                    }
                } else if (unsupportedUrl != null) {
                    req = new TL_help_getDeepLinkInfo();
                    req.path = unsupportedUrl;
                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$13(this, progressDialog));
                } else if (lang != null) {
                    req = new TL_langpack_getLanguage();
                    req.lang_code = lang;
                    req.lang_pack = "android";
                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$14(this, progressDialog));
                }
            }
            if (requestId[0] != 0) {
                progressDialog.setOnCancelListener(new LaunchActivity$$Lambda$15(intentAccount, requestId));
                try {
                    progressDialog.show();
                } catch (Exception e) {
                }
            }
        } else if (NotificationCenter.getGlobalInstance().hasObservers(NotificationCenter.didReceiveSmsCode)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, code);
        } else {
            Builder builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String codeToInsert = code;
            if (codeToInsert.length() > 3) {
                codeToInsert = codeToInsert.substring(0, 3) + "-" + codeToInsert.substring(3);
            }
            builder = builder;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("OtherLoginCode", R.string.OtherLoginCode, codeToInsert)));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showAlertDialog(builder);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$8$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, HashMap auth, String lang, String unsupportedUrl, String code, int account) {
        if (account != intentAccount) {
            switchToAccount(account, true);
        }
        runLinkRequest(account, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code, 1);
    }

    final /* synthetic */ void lambda$runLinkRequest$12$LaunchActivity(AlertDialog progressDialog, String game, int intentAccount, String botChat, String botUser, Integer messageId, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$47(this, progressDialog, response, error, game, intentAccount, botChat, botUser, messageId));
    }

    final /* synthetic */ void lambda$null$11$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error, String game, int intentAccount, String botChat, String botUser, Integer messageId) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
            if (error != null || this.actionBarLayout == null || (game != null && (game == null || res.users.isEmpty()))) {
                try {
                    Toast.makeText(this, LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                    return;
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                    return;
                }
            }
            MessagesController.getInstance(intentAccount).putUsers(res.users, false);
            MessagesController.getInstance(intentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(intentAccount).putUsersAndChats(res.users, res.chats, false, true);
            Bundle args;
            DialogsActivity fragment;
            if (game != null) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putBoolean("cantSendToChannels", true);
                args.putInt("dialogsType", 1);
                args.putString("selectAlertString", LocaleController.getString("SendGameTo", R.string.SendGameTo));
                args.putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", R.string.SendGameToGroup));
                fragment = new DialogsActivity(args);
                fragment.setDelegate(new LaunchActivity$$Lambda$48(this, game, intentAccount, res));
                boolean removeLast = AndroidUtilities.isTablet() ? this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                this.actionBarLayout.presentFragment(fragment, removeLast, true, true, false);
                if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                    SecretMediaViewer.getInstance().closePhoto(false, false);
                } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                    PhotoViewer.getInstance().closePhoto(false, true);
                } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                    ArticleViewer.getInstance().close(false, true);
                }
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                if (AndroidUtilities.isTablet()) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                    return;
                }
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            } else if (botChat != null) {
                User user = !res.users.isEmpty() ? (User) res.users.get(0) : null;
                if (user == null || (user.bot && user.bot_nochats)) {
                    try {
                        Toast.makeText(this, LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                        return;
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                        return;
                    }
                }
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 2);
                args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                fragment = new DialogsActivity(args);
                fragment.setDelegate(new LaunchActivity$$Lambda$49(this, intentAccount, user, botChat));
                presentFragment(fragment);
            } else {
                boolean isBot = false;
                args = new Bundle();
                long dialog_id;
                if (res.chats.isEmpty()) {
                    args.putInt("user_id", ((User) res.users.get(0)).var_id);
                    dialog_id = (long) ((User) res.users.get(0)).var_id;
                } else {
                    args.putInt("chat_id", ((Chat) res.chats.get(0)).var_id);
                    dialog_id = (long) (-((Chat) res.chats.get(0)).var_id);
                }
                if (botUser != null && res.users.size() > 0 && ((User) res.users.get(0)).bot) {
                    args.putString("botUser", botUser);
                    isBot = true;
                }
                if (messageId != null) {
                    args.putInt("message_id", messageId.intValue());
                }
                BaseFragment lastFragment = !mainFragmentsStack.isEmpty() ? (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1) : null;
                if (lastFragment != null && !MessagesController.getInstance(intentAccount).checkCanOpenChat(args, lastFragment)) {
                    return;
                }
                if (isBot && lastFragment != null && (lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == dialog_id) {
                    ((ChatActivity) lastFragment).setBotUser(botUser);
                } else {
                    this.actionBarLayout.presentFragment(new ChatActivity(args));
                }
            }
        }
    }

    final /* synthetic */ void lambda$null$9$LaunchActivity(String game, int intentAccount, TL_contacts_resolvedPeer res, DialogsActivity fragment1, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        TL_inputMediaGame inputMediaGame = new TL_inputMediaGame();
        inputMediaGame.var_id = new TL_inputGameShortName();
        inputMediaGame.var_id.short_name = game;
        inputMediaGame.var_id.bot_id = MessagesController.getInstance(intentAccount).getInputUser((User) res.users.get(0));
        SendMessagesHelper.getInstance(intentAccount).sendGame(MessagesController.getInstance(intentAccount).getInputPeer((int) did), inputMediaGame, 0, 0);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args1.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args1.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args1.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args1.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args1, fragment1)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(args1), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$null$10$LaunchActivity(int intentAccount, User user, String botChat, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putInt("chat_id", -((int) did));
        if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount).checkCanOpenChat(args12, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            MessagesController.getInstance(intentAccount).addUserToChat(-((int) did), user, null, 0, botChat, null);
            this.actionBarLayout.presentFragment(new ChatActivity(args12), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$15$LaunchActivity(AlertDialog progressDialog, int intentAccount, String group, String username, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, HashMap auth, String lang, String unsupportedUrl, String code, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$45(this, progressDialog, error, response, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code));
    }

    final /* synthetic */ void lambda$null$14$LaunchActivity(AlertDialog progressDialog, TL_error error, TLObject response, int intentAccount, String group, String username, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, HashMap auth, String lang, String unsupportedUrl, String code) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            Builder builder;
            if (error != null || this.actionBarLayout == null) {
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showAlertDialog(builder);
                return;
            }
            ChatInvite invite = (ChatInvite) response;
            if (invite.chat != null && !ChatObject.isLeftFromChat(invite.chat)) {
                MessagesController.getInstance(intentAccount).putChat(invite.chat, false);
                ArrayList<Chat> chats = new ArrayList();
                chats.add(invite.chat);
                MessagesStorage.getInstance(intentAccount).putUsersAndChats(null, chats, false, true);
                Bundle args = new Bundle();
                args.putInt("chat_id", invite.chat.var_id);
                if (!mainFragmentsStack.isEmpty()) {
                    if (!MessagesController.getInstance(intentAccount).checkCanOpenChat(args, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        return;
                    }
                }
                ChatActivity fragment = new ChatActivity(args);
                NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.actionBarLayout.presentFragment(fragment, false, true, true, false);
            } else if (((invite.chat != null || (invite.channel && !invite.megagroup)) && (invite.chat == null || (ChatObject.isChannel(invite.chat) && !invite.chat.megagroup))) || mainFragmentsStack.isEmpty()) {
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                String str = "ChannelJoinTo";
                Object[] objArr = new Object[1];
                objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                builder.setMessage(LocaleController.formatString(str, R.string.ChannelJoinTo, objArr));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$46(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showAlertDialog(builder);
            } else {
                BaseFragment fragment2 = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                fragment2.showDialog(new JoinGroupAlert(this, invite, group, fragment2));
            }
        }
    }

    final /* synthetic */ void lambda$null$13$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, HashMap auth, String lang, String unsupportedUrl, String code, DialogInterface dialogInterface, int i) {
        runLinkRequest(intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, instantView, auth, lang, unsupportedUrl, code, 1);
    }

    final /* synthetic */ void lambda$runLinkRequest$17$LaunchActivity(int intentAccount, AlertDialog progressDialog, TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(intentAccount).processUpdates((Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$44(this, progressDialog, error, response, intentAccount));
    }

    final /* synthetic */ void lambda$null$16$LaunchActivity(AlertDialog progressDialog, TL_error error, TLObject response, int intentAccount) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            if (error != null) {
                Builder builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                Updates updates = (Updates) response;
                if (!updates.chats.isEmpty()) {
                    Chat chat = (Chat) updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(intentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(intentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.var_id);
                    if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount).checkCanOpenChat(args, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        ChatActivity fragment = new ChatActivity(args);
                        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        this.actionBarLayout.presentFragment(fragment, false, true, true, false);
                    }
                }
            }
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(boolean hasUrl, int intentAccount, String message, DialogsActivity fragment13, ArrayList dids, CharSequence m, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args13 = new Bundle();
        args13.putBoolean("scrollToTopOnResume", true);
        args13.putBoolean("hasUrl", hasUrl);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args13.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args13.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args13.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args13.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args13, fragment13)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            DataQuery.getInstance(intentAccount).saveDraft(did, message, null, null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(args13), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response, TL_error error) {
        TL_account_authorizationForm authorizationForm = (TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TL_account_getPassword(), new LaunchActivity$$Lambda$41(this, progressDialog, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$42(this, progressDialog, error));
    }

    final /* synthetic */ void lambda$null$20$LaunchActivity(AlertDialog progressDialog, int intentAccount, TL_account_authorizationForm authorizationForm, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response1, TL_error error1) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$43(this, progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
    }

    final /* synthetic */ void lambda$null$19$LaunchActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TL_account_authorizationForm authorizationForm, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (response1 != null) {
            TL_account_password accountPassword = (TL_account_password) response1;
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            presentFragment(new PassportActivity(5, req.bot_id, req.scope, req.public_key, payload, nonce, callbackUrl, authorizationForm, accountPassword));
        }
    }

    final /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog progressDialog, TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$24$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$40(this, progressDialog, response));
    }

    final /* synthetic */ void lambda$null$23$LaunchActivity(AlertDialog progressDialog, TLObject response) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (response instanceof TL_help_deepLinkInfo) {
            TL_help_deepLinkInfo res = (TL_help_deepLinkInfo) response;
            AlertsCreator.showUpdateAppAlert(this, res.message, res.update_app);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$26$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$39(this, progressDialog, response, error));
    }

    final /* synthetic */ void lambda$null$25$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (response instanceof TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TL_langPackLanguage) response));
        } else if (error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", R.string.LanguageUnsupportedError)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
            }
        }
    }

    public void checkAppUpdate(boolean force) {
        if (!force && BuildVars.DEBUG_VERSION) {
            return;
        }
        if (!force && !BuildVars.CHECK_UPDATES) {
            return;
        }
        if (force || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(0).lastUpdateCheckTime) >= 86400000) {
            TL_help_getAppUpdate req = new TL_help_getAppUpdate();
            try {
                req.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception e) {
            }
            if (req.source == null) {
                req.source = TtmlNode.ANONYMOUS_REGION_ID;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$16(this, this.currentAccount));
        }
    }

    final /* synthetic */ void lambda$checkAppUpdate$29$LaunchActivity(int accountNum, TLObject response, TL_error error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (response instanceof TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$38(this, (TL_help_appUpdate) response, accountNum));
        }
    }

    final /* synthetic */ void lambda$null$28$LaunchActivity(TL_help_appUpdate res, int accountNum) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            res.popup = Utilities.random.nextBoolean();
        }
        if (res.popup) {
            UserConfig.getInstance(0).pendingAppUpdate = res;
            UserConfig.getInstance(0).pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Throwable e) {
                FileLog.m13e(e);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = 0;
            }
            UserConfig.getInstance(0).saveConfig(false);
            showUpdateActivity(accountNum, res);
            return;
        }
        new UpdateAppAlertDialog(this, res, accountNum).show();
    }

    public AlertDialog showAlertDialog(Builder builder) {
        AlertDialog alertDialog = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new CLASSNAME());
            return this.visibleDialog;
        } catch (Throwable e2) {
            FileLog.m13e(e2);
            return alertDialog;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        Bundle args = new Bundle();
        int account = dialogsFragment != null ? dialogsFragment.getCurrentAccount() : this.currentAccount;
        args.putBoolean("scrollToTopOnResume", true);
        if (!AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        if (lower_part == 0) {
            args.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(account).checkCanOpenChat(args, dialogsFragment)) {
            BaseFragment fragment = new ChatActivity(args);
            if (this.contactsToSend == null || this.contactsToSend.size() != 1) {
                boolean z;
                ActionBarLayout actionBarLayout = this.actionBarLayout;
                boolean z2 = dialogsFragment != null;
                if (dialogsFragment == null) {
                    z = true;
                } else {
                    z = false;
                }
                actionBarLayout.presentFragment(fragment, z2, z, true, false);
                if (this.videoPath != null) {
                    fragment.openVideoEditor(this.videoPath, this.sendingText);
                    this.sendingText = null;
                }
                if (this.photoPathsArray != null) {
                    if (this.sendingText != null && this.sendingText.length() <= 1024 && this.photoPathsArray.size() == 1) {
                        ((SendingMediaInfo) this.photoPathsArray.get(0)).caption = this.sendingText;
                        this.sendingText = null;
                    }
                    SendMessagesHelper.prepareSendingMedia(this.photoPathsArray, did, null, null, false, false, null);
                }
                if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                    String caption = null;
                    if (this.sendingText != null && this.sendingText.length() <= 1024) {
                        if ((this.documentsPathsArray != null ? this.documentsPathsArray.size() : 0) + (this.documentsUrisArray != null ? this.documentsUrisArray.size() : 0) == 1) {
                            caption = this.sendingText;
                            this.sendingText = null;
                        }
                    }
                    SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, caption, this.documentsMimeType, did, null, null, null);
                }
                if (this.sendingText != null) {
                    SendMessagesHelper.prepareSendingText(this.sendingText, did);
                }
                if (!(this.contactsToSend == null || this.contactsToSend.isEmpty())) {
                    for (int a = 0; a < this.contactsToSend.size(); a++) {
                        SendMessagesHelper.getInstance(account).sendMessage((User) this.contactsToSend.get(a), did, null, null, null);
                    }
                }
            } else if (this.contactsToSend.size() == 1) {
                boolean z3;
                boolean z4;
                PhonebookShareActivity contactFragment = new PhonebookShareActivity(null, this.contactsToSendUri, null, null);
                contactFragment.setDelegate(new LaunchActivity$$Lambda$17(this, fragment, account, did));
                ActionBarLayout actionBarLayout2 = this.actionBarLayout;
                if (dialogsFragment != null) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (dialogsFragment == null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                actionBarLayout2.presentFragment(contactFragment, z3, z4, true, false);
            }
            this.photoPathsArray = null;
            this.videoPath = null;
            this.sendingText = null;
            this.documentsPathsArray = null;
            this.documentsOriginalPathsArray = null;
            this.contactsToSend = null;
            this.contactsToSendUri = null;
        }
    }

    final /* synthetic */ void lambda$didSelectDialogs$30$LaunchActivity(ChatActivity fragment, int account, long did, User user) {
        this.actionBarLayout.presentFragment(fragment, true, false, true, false);
        SendMessagesHelper.getInstance(account).sendMessage(user, did, null, null, null);
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            if (this.lockRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
                this.lockRunnable = null;
            }
            if (this.currentAccount != -1) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
        }
    }

    public void presentFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public ActionBarLayout getActionBarLayout() {
        return this.actionBarLayout;
    }

    public ActionBarLayout getLayersActionBarLayout() {
        return this.layersActionBarLayout;
    }

    public ActionBarLayout getRightActionBarLayout() {
        return this.rightActionBarLayout;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onActivityResult(requestCode, resultCode, data);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 || requestCode == 4 || requestCode == 5 || requestCode == 19 || requestCode == 20 || requestCode == 22) {
            boolean showAlert = true;
            if (grantResults.length > 0 && grantResults[0] == 0) {
                if (requestCode == 4) {
                    ImageLoader.getInstance().checkMediaPaths();
                    return;
                } else if (requestCode == 5) {
                    ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    return;
                } else if (requestCode == 3) {
                    if (SharedConfig.inappCamera) {
                        CameraController.getInstance().initCamera(null);
                        return;
                    }
                    return;
                } else if (requestCode == 19 || requestCode == 20 || requestCode == 22) {
                    showAlert = false;
                }
            }
            if (showAlert) {
                Builder builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (requestCode == 3) {
                    builder.setMessage(LocaleController.getString("PermissionNoAudio", R.string.PermissionNoAudio));
                } else if (requestCode == 4) {
                    builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                } else if (requestCode == 5) {
                    builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                } else if (requestCode == 19 || requestCode == 20 || requestCode == 22) {
                    builder.setMessage(LocaleController.getString("PermissionNoCamera", R.string.PermissionNoCamera));
                }
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new LaunchActivity$$Lambda$18(this));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.show();
                return;
            }
        } else if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == 0) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
        }
    }

    final /* synthetic */ void lambda$onRequestPermissionsResult$31$LaunchActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    protected void onPause() {
        super.onPause();
        SharedConfig.lastAppPauseTime = System.currentTimeMillis();
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(LaunchActivity$$Lambda$19.$instance);
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        if (this.passcodeView != null) {
            this.passcodeView.onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        AndroidUtilities.unregisterUpdates();
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
    }

    static final /* synthetic */ void lambda$onPause$32$LaunchActivity() {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
    }

    protected void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    protected void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    protected void onDestroy() {
        if (PhotoViewer.getPipInstance() != null) {
            PhotoViewer.getPipInstance().destroyPhotoViewer();
        }
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().destroyPhotoViewer();
        }
        if (SecretMediaViewer.hasInstance()) {
            SecretMediaViewer.getInstance().destroyPhotoViewer();
        }
        if (ArticleViewer.hasInstance()) {
            ArticleViewer.getInstance().destroyArticleViewer();
        }
        if (StickerPreviewViewer.hasInstance()) {
            StickerPreviewViewer.getInstance().destroy();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, false);
        if (pipRoundVideoView != null) {
            pipRoundVideoView.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.destroy();
        }
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(LaunchActivity$$Lambda$20.$instance);
        checkFreeDiscSpace();
        MediaController.checkGallery();
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onResume();
                this.layersActionBarLayout.onResume();
            }
        } else {
            this.actionBarLayout.dismissDialogs();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.dismissDialogs();
                this.layersActionBarLayout.dismissDialogs();
            }
            this.passcodeView.onResume();
        }
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        updateCurrentConnectionState(this.currentAccount);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
        if (PipRoundVideoView.getInstance() != null && MediaController.getInstance().isMessagePaused()) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                MediaController.getInstance().seekToProgress(messageObject, messageObject.audioProgress);
            }
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            showTosActivity(UserConfig.selectedAccount, UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService);
        } else if (UserConfig.getInstance(0).pendingAppUpdate != null) {
            showUpdateActivity(UserConfig.selectedAccount, UserConfig.getInstance(0).pendingAppUpdate);
        }
        checkAppUpdate(false);
    }

    static final /* synthetic */ void lambda$onResume$33$LaunchActivity() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        checkLayout();
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.onConfigurationChanged();
        }
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.onConfigurationChanged(newConfig);
        }
        PhotoViewer photoViewer = PhotoViewer.getPipInstance();
        if (photoViewer != null) {
            photoViewer.onConfigurationChanged(newConfig);
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onConfigurationChanged();
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        checkLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        Builder builder;
        View child;
        if (id == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
        } else if (id == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != this) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("switch to state " + state);
                }
                this.currentConnectionState = state;
                updateCurrentConnectionState(account);
            }
        } else if (id == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            if (reason.intValue() == 3 && this.proxyErrorDialog != null) {
                return;
            }
            if (reason.intValue() == 4) {
                showTosActivity(account, (TL_help_termsOfService) args[1]);
                return;
            }
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (!(reason.intValue() == 2 || reason.intValue() == 3)) {
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new LaunchActivity$$Lambda$21(account));
            }
            if (reason.intValue() == 5) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam3", R.string.NobodyLikesSpam3));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 0) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam1", R.string.NobodyLikesSpam1));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 1) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 2) {
                builder.setMessage((String) args[1]);
                if (args[2].startsWith("AUTH_KEY_DROP_")) {
                    builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setNegativeButton(LocaleController.getString("LogOut", R.string.LogOut), new LaunchActivity$$Lambda$22(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                }
            } else if (reason.intValue() == 3) {
                builder.setMessage(LocaleController.getString("UseProxyTelegramError", R.string.UseProxyTelegramError));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                this.proxyErrorDialog = showAlertDialog(builder);
                return;
            }
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.wasUnableToFindCurrentLocation) {
            HashMap<String, MessageObject> waitingForLocation = args[0];
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", R.string.ShareYouLocationUnableManually), new LaunchActivity$$Lambda$23(this, waitingForLocation, account));
            builder.setMessage(LocaleController.getString("ShareYouLocationUnable", R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.sideMenu != null) {
                child = this.sideMenu.getChildAt(0);
                if (child != null) {
                    child.invalidate();
                }
            }
        } else if (id == NotificationCenter.didSetPasscode) {
            if (SharedConfig.passcodeHash.length() <= 0 || SharedConfig.allowScreenCapture) {
                try {
                    getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                    return;
                } catch (Throwable e) {
                    FileLog.m13e(e);
                    return;
                }
            }
            try {
                getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
        } else if (id == NotificationCenter.reloadInterface) {
            boolean last = mainFragmentsStack.size() > 1 && (mainFragmentsStack.get(mainFragmentsStack.size() - 1) instanceof SettingsActivity);
            rebuildAllFragments(last);
        } else if (id == NotificationCenter.suggestedLangpack) {
            showLanguageAlert(false);
        } else if (id == NotificationCenter.openArticle) {
            if (!mainFragmentsStack.isEmpty()) {
                ArticleViewer.getInstance().setParentActivity(this, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1));
                ArticleViewer.getInstance().open((TL_webPage) args[0], (String) args[1]);
            }
        } else if (id == NotificationCenter.hasNewContactsToImport) {
            if (this.actionBarLayout != null && !this.actionBarLayout.fragmentsStack.isEmpty()) {
                int type = ((Integer) args[0]).intValue();
                HashMap<String, Contact> contactHashMap = args[1];
                boolean first = ((Boolean) args[2]).booleanValue();
                boolean schedule = ((Boolean) args[3]).booleanValue();
                BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("UpdateContactsTitle", R.string.UpdateContactsTitle));
                builder.setMessage(LocaleController.getString("UpdateContactsMessage", R.string.UpdateContactsMessage));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$24(account, contactHashMap, first, schedule));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new LaunchActivity$$Lambda$25(account, contactHashMap, first, schedule));
                builder.setOnBackButtonListener(new LaunchActivity$$Lambda$26(account, contactHashMap, first, schedule));
                AlertDialog dialog = builder.create();
                fragment.showDialog(dialog);
                dialog.setCanceledOnTouchOutside(false);
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            if (!args[0].booleanValue()) {
                if (this.sideMenu != null) {
                    this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.getAdapter().notifyDataSetChanged();
                }
                if (VERSION.SDK_INT >= 21) {
                    try {
                        setTaskDescription(new TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
                    } catch (Exception e3) {
                    }
                }
            }
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            ThemeInfo theme = args[0];
            this.actionBarLayout.animateThemedValues(theme);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.animateThemedValues(theme);
                this.rightActionBarLayout.animateThemedValues(theme);
            }
        } else if (id == NotificationCenter.notificationsCountUpdated && this.sideMenu != null) {
            Integer accountNum = args[0];
            int count = this.sideMenu.getChildCount();
            for (int a = 0; a < count; a++) {
                child = this.sideMenu.getChildAt(a);
                if ((child instanceof DrawerUserCell) && ((DrawerUserCell) child).getAccountNumber() == accountNum.intValue()) {
                    child.invalidate();
                    return;
                }
            }
        }
    }

    static final /* synthetic */ void lambda$didReceivedNotification$34$LaunchActivity(int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController.getInstance(account).openByUserName("spambot", (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1), 1);
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$35$LaunchActivity(DialogInterface dialog, int which) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    final /* synthetic */ void lambda$didReceivedNotification$37$LaunchActivity(HashMap waitingForLocation, int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            LocationActivity fragment = new LocationActivity(0);
            fragment.setDelegate(new LaunchActivity$$Lambda$37(waitingForLocation, account));
            presentFragment(fragment);
        }
    }

    static final /* synthetic */ void lambda$null$36$LaunchActivity(HashMap waitingForLocation, int account, MessageMedia location, int live) {
        for (Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            SendMessagesHelper.getInstance(account).sendMessage(location, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    private String getStringForLanguageAlert(HashMap<String, String> map, String key, int intKey) {
        String value = (String) map.get(key);
        if (value == null) {
            return LocaleController.getString(key, intKey);
        }
        return value;
    }

    private void checkFreeDiscSpace() {
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new LaunchActivity$$Lambda$27(this), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    final /* synthetic */ void lambda$checkFreeDiscSpace$42$LaunchActivity() {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs(preferences.getLong("last_space_check", 0) - System.currentTimeMillis()) >= NUM) {
                    File path = FileLoader.getDirectory(4);
                    if (path != null) {
                        long freeSpace;
                        StatFs statFs = new StatFs(path.getAbsolutePath());
                        if (VERSION.SDK_INT < 18) {
                            freeSpace = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                        } else {
                            freeSpace = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                        }
                        preferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        if (freeSpace < NUM) {
                            AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$36(this));
                        }
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    final /* synthetic */ void lambda$null$41$LaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable th) {
        }
    }

    private void showLanguageAlertInternal(LocaleInfo systemInfo, LocaleInfo englishInfo, String systemLang) {
        try {
            LocaleInfo localeInfo;
            this.loadingLocaleDialog = false;
            boolean firstSystem = systemInfo.builtIn || LocaleController.getInstance().isCurrentLocalLocale();
            Builder builder = new Builder((Context) this);
            builder.setTitle(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            builder.setSubtitle(getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            LanguageCell[] cells = new LanguageCell[2];
            LocaleInfo[] selectedLanguage = new LocaleInfo[1];
            LocaleInfo[] locales = new LocaleInfo[2];
            String englishName = getStringForLanguageAlert(this.systemLocaleStrings, "English", R.string.English);
            if (firstSystem) {
                localeInfo = systemInfo;
            } else {
                localeInfo = englishInfo;
            }
            locales[0] = localeInfo;
            if (firstSystem) {
                localeInfo = englishInfo;
            } else {
                localeInfo = systemInfo;
            }
            locales[1] = localeInfo;
            if (!firstSystem) {
                systemInfo = englishInfo;
            }
            selectedLanguage[0] = systemInfo;
            int a = 0;
            while (a < 2) {
                cells[a] = new LanguageCell(this, true);
                cells[a].setLanguage(locales[a], locales[a] == englishInfo ? englishName : null, true);
                cells[a].setTag(Integer.valueOf(a));
                cells[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                cells[a].setLanguageSelected(a == 0);
                linearLayout.addView(cells[a], LayoutHelper.createLinear(-1, 50));
                cells[a].setOnClickListener(new LaunchActivity$$Lambda$28(selectedLanguage, cells));
                a++;
            }
            LanguageCell cell = new LanguageCell(this, true);
            cell.setValue(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
            cell.setOnClickListener(new LaunchActivity$$Lambda$29(this));
            linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
            builder.setView(linearLayout);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$30(this, selectedLanguage));
            this.localeDialog = showAlertDialog(builder);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang).commit();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ void lambda$showLanguageAlertInternal$43$LaunchActivity(LocaleInfo[] selectedLanguage, LanguageCell[] cells, View v) {
        Integer tag = (Integer) v.getTag();
        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
        for (int a1 = 0; a1 < cells.length; a1++) {
            boolean z;
            LanguageCell languageCell = cells[a1];
            if (a1 == tag.intValue()) {
                z = true;
            } else {
                z = false;
            }
            languageCell.setLanguageSelected(z);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlertInternal$44$LaunchActivity(View v) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        presentFragment(new LanguageSelectActivity());
        if (this.visibleDialog != null) {
            this.visibleDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    final /* synthetic */ void lambda$showLanguageAlertInternal$45$LaunchActivity(LocaleInfo[] selectedLanguage, DialogInterface dialog, int which) {
        LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    private void showLanguageAlert(boolean force) {
        try {
            if (!this.loadingLocaleDialog && !ApplicationLoader.mainInterfacePaused) {
                String showedLang = MessagesController.getGlobalMainSettings().getString("language_showed2", TtmlNode.ANONYMOUS_REGION_ID);
                String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                if (force || !showedLang.equals(systemLang)) {
                    String arg;
                    LocaleInfo[] infos = new LocaleInfo[2];
                    if (systemLang.contains("-")) {
                        arg = systemLang.split("-")[0];
                    } else {
                        arg = systemLang;
                    }
                    String alias;
                    if ("in".equals(arg)) {
                        alias = TtmlNode.ATTR_ID;
                    } else if ("iw".equals(arg)) {
                        alias = "he";
                    } else if ("jw".equals(arg)) {
                        alias = "jv";
                    } else {
                        alias = null;
                    }
                    for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
                        LocaleInfo info = (LocaleInfo) LocaleController.getInstance().languages.get(a);
                        if (info.shortName.equals("en")) {
                            infos[0] = info;
                        }
                        if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || (alias != null && info.shortName.equals(alias))) {
                            infos[1] = info;
                        }
                        if (infos[0] != null && infos[1] != null) {
                            break;
                        }
                    }
                    if (infos[0] != null && infos[1] != null && infos[0] != infos[1]) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m10d("show lang alert for " + infos[0].getKey() + " and " + infos[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TL_langpack_getStrings req = new TL_langpack_getStrings();
                        req.lang_code = infos[1].getLangCode();
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$31(this, infos, systemLang), 8);
                        req = new TL_langpack_getStrings();
                        req.lang_code = infos[0].getLangCode();
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$32(this, infos, systemLang), 8);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("alert already showed for " + showedLang);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlert$47$LaunchActivity(LocaleInfo[] infos, String systemLang, TLObject response, TL_error error) {
        HashMap<String, String> keys = new HashMap();
        if (response != null) {
            Vector vector = (Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                LangPackString string = (LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$35(this, keys, infos, systemLang));
    }

    final /* synthetic */ void lambda$null$46$LaunchActivity(HashMap keys, LocaleInfo[] infos, String systemLang) {
        this.systemLocaleStrings = keys;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlert$49$LaunchActivity(LocaleInfo[] infos, String systemLang, TLObject response, TL_error error) {
        HashMap<String, String> keys = new HashMap();
        if (response != null) {
            Vector vector = (Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                LangPackString string = (LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$34(this, keys, infos, systemLang));
    }

    final /* synthetic */ void lambda$null$48$LaunchActivity(HashMap keys, LocaleInfo[] infos, String systemLang) {
        this.englishLocaleStrings = keys;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            this.lockRunnable = new CLASSNAME();
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

    private void updateCurrentConnectionState(int account) {
        if (this.actionBarLayout != null) {
            String title = null;
            int titleId = 0;
            Runnable action = null;
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (this.currentConnectionState == 2) {
                title = "WaitingForNetwork";
                titleId = R.string.WaitingForNetwork;
            } else if (this.currentConnectionState == 5) {
                title = "Updating";
                titleId = R.string.Updating;
            } else if (this.currentConnectionState == 4) {
                title = "ConnectingToProxy";
                titleId = R.string.ConnectingToProxy;
            } else if (this.currentConnectionState == 1) {
                title = "Connecting";
                titleId = R.string.Connecting;
            }
            if (this.currentConnectionState == 1 || this.currentConnectionState == 4) {
                action = new LaunchActivity$$Lambda$33(this);
            }
            this.actionBarLayout.setTitleOverlayText(title, titleId, action);
        }
    }

    final /* synthetic */ void lambda$updateCurrentConnectionState$50$LaunchActivity() {
        BaseFragment lastFragment = null;
        if (AndroidUtilities.isTablet()) {
            if (!layerFragmentsStack.isEmpty()) {
                lastFragment = (BaseFragment) layerFragmentsStack.get(layerFragmentsStack.size() - 1);
            }
        } else if (!mainFragmentsStack.isEmpty()) {
            lastFragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
        }
        if (!(lastFragment instanceof ProxyListActivity) && !(lastFragment instanceof ProxySettingsActivity)) {
            presentFragment(new ProxyListActivity());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BaseFragment lastFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if ((lastFragment instanceof ChatActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat");
                } else if (lastFragment instanceof SettingsActivity) {
                    outState.putString("fragment", "settings");
                } else if ((lastFragment instanceof GroupCreateFinalActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                } else if (lastFragment instanceof WallpapersActivity) {
                    outState.putString("fragment", "wallpapers");
                } else if ((lastFragment instanceof ProfileActivity) && ((ProfileActivity) lastFragment).isChat() && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat_profile");
                } else if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "channel");
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            boolean cancel = false;
            if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                cancel = !((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() + -1)).onBackPressed();
            }
            if (!cancel) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onLowMemory();
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        try {
            Menu menu = mode.getMenu();
            if (!(menu == null || this.actionBarLayout.extendActionMode(menu) || !AndroidUtilities.isTablet() || this.rightActionBarLayout.extendActionMode(menu))) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(mode);
                this.layersActionBarLayout.onActionModeStarted(mode);
            }
        }
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(mode);
                this.layersActionBarLayout.onActionModeFinished(mode);
            }
        }
    }

    public boolean onPreIme() {
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
            return true;
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        } else if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return false;
        } else {
            ArticleViewer.getInstance().close(true, false);
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(keyCode, event);
                } else {
                    this.rightActionBarLayout.onKeyUp(keyCode, event);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(keyCode, event);
            } else if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
            } else {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if ((fragment instanceof DialogsActivity) && ((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
                this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                this.layersActionBarLayout.removeAllFragments();
                this.layersActionBarLayout.setVisibility(8);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                if (!this.tabletFullSize) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        this.backgroundTablet.setVisibility(0);
                    }
                }
                return false;
            } else if (fragment instanceof ChatActivity) {
                int a;
                if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                    boolean result = (this.tabletFullSize && layout == this.actionBarLayout && this.actionBarLayout.fragmentsStack.size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    if (result) {
                        return result;
                    }
                    this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false, false);
                    return result;
                } else if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                } else if (!this.tabletFullSize || layout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    this.actionBarLayout.presentFragment(fragment, this.actionBarLayout.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    return false;
                } else {
                    this.actionBarLayout.presentFragment(fragment, this.actionBarLayout.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                }
            } else if (layout == this.layersActionBarLayout) {
                return true;
            } else {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                return false;
            }
        }
        boolean allow = true;
        if (fragment instanceof LoginActivity) {
            if (mainFragmentsStack.size() == 0) {
                allow = false;
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            allow = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if (fragment instanceof DialogsActivity) {
                if (((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(fragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (this.tabletFullSize) {
                        return false;
                    }
                    this.shadowTabletSide.setVisibility(0);
                    if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    this.backgroundTablet.setVisibility(0);
                    return false;
                }
            } else if (fragment instanceof ChatActivity) {
                int a;
                if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                } else if (this.tabletFullSize && layout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                }
            } else if (layout != this.layersActionBarLayout) {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.addFragmentToStack(fragment);
                return false;
            }
            return true;
        }
        boolean allow = true;
        if (fragment instanceof LoginActivity) {
            if (mainFragmentsStack.size() == 0) {
                allow = false;
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            allow = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (layout.fragmentsStack.size() >= 2 && !(layout.fragmentsStack.get(0) instanceof LoginActivity)) {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean last) {
        if (this.layersActionBarLayout != null) {
            this.layersActionBarLayout.rebuildAllFragmentViews(last, last);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }

    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(last, last);
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }
}
