package org.telegram.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.os.SystemClock;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$LangPackString;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_chatInvitePeek;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;
import org.telegram.tgnet.TLRPC$TL_help_deepLinkInfo;
import org.telegram.tgnet.TLRPC$TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$TL_inputChannel;
import org.telegram.tgnet.TLRPC$TL_inputGameShortName;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class LaunchActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public View backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private ArrayList<TLRPC$User> contactsToSend;
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
    /* access modifiers changed from: private */
    public SideMenultItemAnimator itemAnimator;
    /* access modifiers changed from: private */
    public ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC$TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TLRPC$TL_wallPaper loadingThemeWallpaper;
    private String loadingThemeWallpaperName;
    private AlertDialog localeDialog;
    /* access modifiers changed from: private */
    public Runnable lockRunnable;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    private ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
    private AlertDialog proxyErrorDialog;
    /* access modifiers changed from: private */
    public ActionBarLayout rightActionBarLayout;
    private String sendingText;
    /* access modifiers changed from: private */
    public FrameLayout shadowTablet;
    /* access modifiers changed from: private */
    public FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    private HashMap<String, String> systemLocaleStrings;
    /* access modifiers changed from: private */
    public boolean tabletFullSize;
    /* access modifiers changed from: private */
    public TermsOfServiceView termsOfServiceView;
    /* access modifiers changed from: private */
    public ImageView themeSwitchImageView;
    private String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:40|41|42|43|44) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x0109 */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            java.lang.String r0 = "flyme"
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r1 = r12.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r12, r1)
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r12.currentAccount = r1
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x00e8
            android.content.Intent r1 = r12.getIntent()
            if (r1 == 0) goto L_0x0088
            java.lang.String r4 = r1.getAction()
            if (r4 == 0) goto L_0x0088
            java.lang.String r4 = r1.getAction()
            java.lang.String r5 = "android.intent.action.SEND"
            boolean r4 = r5.equals(r4)
            if (r4 != 0) goto L_0x0081
            java.lang.String r4 = r1.getAction()
            java.lang.String r5 = "android.intent.action.SEND_MULTIPLE"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0045
            goto L_0x0081
        L_0x0045:
            java.lang.String r4 = r1.getAction()
            java.lang.String r5 = "android.intent.action.VIEW"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0088
            android.net.Uri r4 = r1.getData()
            if (r4 == 0) goto L_0x0088
            java.lang.String r4 = r4.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r5 = "tg:proxy"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x007f
            java.lang.String r5 = "tg://proxy"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x007f
            java.lang.String r5 = "tg:socks"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x007f
            java.lang.String r5 = "tg://socks"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x0088
        L_0x007f:
            r4 = 1
            goto L_0x0089
        L_0x0081:
            super.onCreate(r13)
            r12.finish()
            return
        L_0x0088:
            r4 = 0
        L_0x0089:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r6 = "intro_crashed_time"
            r7 = 0
            long r9 = r5.getLong(r6, r7)
            java.lang.String r11 = "fromIntro"
            boolean r11 = r1.getBooleanExtra(r11, r3)
            if (r11 == 0) goto L_0x00a8
            android.content.SharedPreferences$Editor r5 = r5.edit()
            android.content.SharedPreferences$Editor r5 = r5.putLong(r6, r7)
            r5.commit()
        L_0x00a8:
            if (r4 != 0) goto L_0x00e8
            long r4 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r4
            long r4 = java.lang.Math.abs(r9)
            r6 = 120000(0x1d4c0, double:5.9288E-319)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 < 0) goto L_0x00e8
            if (r1 == 0) goto L_0x00e8
            if (r11 != 0) goto L_0x00e8
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r5 = "logininfo2"
            android.content.SharedPreferences r4 = r4.getSharedPreferences(r5, r3)
            java.util.Map r4 = r4.getAll()
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x00e8
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r2 = org.telegram.ui.IntroActivity.class
            r0.<init>(r12, r2)
            android.net.Uri r1 = r1.getData()
            r0.setData(r1)
            r12.startActivity(r0)
            super.onCreate(r13)
            r12.finish()
            return
        L_0x00e8:
            r12.requestWindowFeature(r2)
            r1 = 2131689484(0x7f0var_c, float:1.9007985E38)
            r12.setTheme(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            r5 = 0
            if (r1 < r4) goto L_0x0112
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r6 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0109 }
            java.lang.String r7 = "actionBarDefault"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x0109 }
            r7 = r7 | r1
            r6.<init>(r5, r5, r7)     // Catch:{ Exception -> 0x0109 }
            r12.setTaskDescription(r6)     // Catch:{ Exception -> 0x0109 }
        L_0x0109:
            android.view.Window r6 = r12.getWindow()     // Catch:{ Exception -> 0x0111 }
            r6.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x0111 }
            goto L_0x0112
        L_0x0111:
        L_0x0112:
            android.view.Window r1 = r12.getWindow()
            org.telegram.ui.LaunchActivity$1 r6 = new org.telegram.ui.LaunchActivity$1
            r7 = -1
            r6.<init>(r12, r7)
            r1.setBackgroundDrawable(r6)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0139
            boolean r1 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x0139
            android.view.Window r1 = r12.getWindow()     // Catch:{ Exception -> 0x0135 }
            r6 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r6, r6)     // Catch:{ Exception -> 0x0135 }
            goto L_0x0139
        L_0x0135:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0139:
            super.onCreate(r13)
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r1 < r6) goto L_0x0148
            boolean r1 = r12.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r1
        L_0x0148:
            org.telegram.ui.ActionBar.Theme.createChatResources(r12, r3)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0161
            boolean r1 = org.telegram.messenger.SharedConfig.appLocked
            if (r1 == 0) goto L_0x0161
            long r8 = android.os.SystemClock.elapsedRealtime()
            r10 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 / r10
            int r1 = (int) r8
            org.telegram.messenger.SharedConfig.lastPauseTime = r1
        L_0x0161:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r12)
            org.telegram.ui.LaunchActivity$2 r1 = new org.telegram.ui.LaunchActivity$2
            r1.<init>(r12)
            r12.actionBarLayout = r1
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r12)
            android.view.ViewGroup$LayoutParams r6 = new android.view.ViewGroup$LayoutParams
            r6.<init>(r7, r7)
            r12.setContentView(r1, r6)
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 8
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r6 < r4) goto L_0x0193
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r12)
            r12.themeSwitchImageView = r4
            r4.setVisibility(r8)
            android.widget.ImageView r4 = r12.themeSwitchImageView
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r1.addView(r4, r6)
        L_0x0193:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = new org.telegram.ui.ActionBar.DrawerLayoutContainer
            r4.<init>(r12)
            r12.drawerLayoutContainer = r4
            java.lang.String r6 = "windowBackgroundWhite"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setBehindKeyboardColor(r6)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r1.addView(r4, r6)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0296
            android.view.Window r1 = r12.getWindow()
            r4 = 16
            r1.setSoftInputMode(r4)
            org.telegram.ui.LaunchActivity$3 r1 = new org.telegram.ui.LaunchActivity$3
            r1.<init>(r12)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r4.addView(r1, r6)
            android.view.View r4 = new android.view.View
            r4.<init>(r12)
            r12.backgroundTablet = r4
            android.content.res.Resources r4 = r12.getResources()
            r6 = 2131165331(0x7var_, float:1.7944876E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r6)
            android.graphics.drawable.BitmapDrawable r4 = (android.graphics.drawable.BitmapDrawable) r4
            android.graphics.Shader$TileMode r6 = android.graphics.Shader.TileMode.REPEAT
            r4.setTileModeXY(r6, r6)
            android.view.View r6 = r12.backgroundTablet
            r6.setBackgroundDrawable(r4)
            android.view.View r4 = r12.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createRelative(r7, r7)
            r1.addView(r4, r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            r1.addView(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = new org.telegram.ui.ActionBar.ActionBarLayout
            r4.<init>(r12)
            r12.rightActionBarLayout = r4
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = rightFragmentsStack
            r4.init(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.rightActionBarLayout
            r4.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.rightActionBarLayout
            r1.addView(r4)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r12)
            r12.shadowTabletSide = r4
            r6 = 1076449908(0x40295274, float:2.6456575)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r12.shadowTabletSide
            r1.addView(r4)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r12)
            r12.shadowTablet = r4
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = layerFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 == 0) goto L_0x022f
            r6 = 8
            goto L_0x0230
        L_0x022f:
            r6 = 0
        L_0x0230:
            r4.setVisibility(r6)
            android.widget.FrameLayout r4 = r12.shadowTablet
            r6 = 2130706432(0x7var_, float:1.7014118E38)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r12.shadowTablet
            r1.addView(r4)
            android.widget.FrameLayout r4 = r12.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4 r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4
            r6.<init>()
            r4.setOnTouchListener(r6)
            android.widget.FrameLayout r4 = r12.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ r6 = org.telegram.ui.$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ.INSTANCE
            r4.setOnClickListener(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = new org.telegram.ui.ActionBar.ActionBarLayout
            r4.<init>(r12)
            r12.layersActionBarLayout = r4
            r4.setRemoveActionBarExtraHeight(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            android.widget.FrameLayout r6 = r12.shadowTablet
            r4.setBackgroundView(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            r4.setUseAlphaAnimations(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            r6 = 2131165296(0x7var_, float:1.7944805E38)
            r4.setBackgroundResource(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = layerFragmentsStack
            r4.init(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            r4.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r12.drawerLayoutContainer
            r4.setDrawerLayoutContainer(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = layerFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 == 0) goto L_0x028c
            goto L_0x028d
        L_0x028c:
            r8 = 0
        L_0x028d:
            r4.setVisibility(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            r1.addView(r4)
            goto L_0x02a2
        L_0x0296:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            android.view.ViewGroup$LayoutParams r6 = new android.view.ViewGroup$LayoutParams
            r6.<init>(r7, r7)
            r1.addView(r4, r6)
        L_0x02a2:
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r12)
            r12.sideMenu = r1
            org.telegram.ui.Components.SideMenultItemAnimator r4 = new org.telegram.ui.Components.SideMenultItemAnimator
            r4.<init>(r1)
            r12.itemAnimator = r4
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            java.lang.String r4 = "chats_menuBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setBackgroundColor(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r12, r2, r3)
            r1.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setAllowItemsInteractionDuringAnimation(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r4 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r6 = r12.itemAnimator
            r4.<init>(r12, r6)
            r12.drawerLayoutAdapter = r4
            r1.setAdapter(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setDrawerLayout(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            r8 = 1134559232(0x43a00000, float:320.0)
            if (r6 == 0) goto L_0x02fd
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0314
        L_0x02fd:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r4.x
            int r4 = r4.y
            int r4 = java.lang.Math.min(r8, r4)
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            int r4 = java.lang.Math.min(r6, r4)
        L_0x0314:
            r1.width = r4
            r1.height = r7
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r4.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$Op_z7pMlRt1m8iTw0iRpzILSb8s r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$Op_z7pMlRt1m8iTw0iRpzILSb8s
            r4.<init>()
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            r1.setParentActionBarLayout(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            r1.setDrawerLayoutContainer(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            r1.init(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            r1.setDelegate(r12)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.Components.PasscodeView r1 = new org.telegram.ui.Components.PasscodeView
            r1.<init>(r12)
            r12.passcodeView = r1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r4.addView(r1, r6)
            r12.checkCurrentAccount()
            int r1 = r12.currentAccount
            r12.updateCurrentConnectionState(r1)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r6 = new java.lang.Object[r2]
            r6[r3] = r12
            r1.postNotificationName(r4, r6)
            int r1 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r12.currentConnectionState = r1
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.needShowAlert
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.reloadInterface
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r1.addObserver(r12, r4)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r4 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r1.addObserver(r12, r4)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x04f6
            int r1 = r12.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x03fe
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.LoginActivity r4 = new org.telegram.ui.LoginActivity
            r4.<init>()
            r1.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x0412
        L_0x03fe:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r5)
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            r4.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r3)
        L_0x0412:
            if (r13 == 0) goto L_0x055d
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r13.getString(r1)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x055d
            java.lang.String r4 = "args"
            android.os.Bundle r4 = r13.getBundle(r4)     // Catch:{ Exception -> 0x04f1 }
            int r5 = r1.hashCode()     // Catch:{ Exception -> 0x04f1 }
            r6 = 5
            r8 = 4
            r9 = 3
            r10 = 2
            switch(r5) {
                case -1529105743: goto L_0x0460;
                case -1349522494: goto L_0x0456;
                case 3052376: goto L_0x044c;
                case 98629247: goto L_0x0442;
                case 738950403: goto L_0x0438;
                case 1434631203: goto L_0x042e;
                default: goto L_0x042d;
            }     // Catch:{ Exception -> 0x04f1 }
        L_0x042d:
            goto L_0x0469
        L_0x042e:
            java.lang.String r5 = "settings"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 1
            goto L_0x0469
        L_0x0438:
            java.lang.String r5 = "channel"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 3
            goto L_0x0469
        L_0x0442:
            java.lang.String r5 = "group"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 2
            goto L_0x0469
        L_0x044c:
            java.lang.String r5 = "chat"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 0
            goto L_0x0469
        L_0x0456:
            java.lang.String r5 = "chat_profile"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 4
            goto L_0x0469
        L_0x0460:
            java.lang.String r5 = "wallpapers"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04f1 }
            if (r1 == 0) goto L_0x0469
            r7 = 5
        L_0x0469:
            if (r7 == 0) goto L_0x04de
            if (r7 == r2) goto L_0x04c2
            if (r7 == r10) goto L_0x04ae
            if (r7 == r9) goto L_0x049a
            if (r7 == r8) goto L_0x0486
            if (r7 == r6) goto L_0x0477
            goto L_0x055d
        L_0x0477:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x0486:
            if (r4 == 0) goto L_0x055d
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            if (r4 == 0) goto L_0x055d
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x049a:
            if (r4 == 0) goto L_0x055d
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            if (r4 == 0) goto L_0x055d
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x04ae:
            if (r4 == 0) goto L_0x055d
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            if (r4 == 0) goto L_0x055d
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x04c2:
            java.lang.String r1 = "user_id"
            int r5 = r12.currentAccount     // Catch:{ Exception -> 0x04f1 }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x04f1 }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x04f1 }
            r4.putInt(r1, r5)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x04de:
            if (r4 == 0) goto L_0x055d
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04f1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f1 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f1 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f1 }
            if (r4 == 0) goto L_0x055d
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x055d
        L_0x04f1:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x055d
        L_0x04f6:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r4 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r4 == 0) goto L_0x050b
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
        L_0x050b:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0540
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x0527
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0527
            r1 = 1
            goto L_0x0528
        L_0x0527:
            r1 = 0
        L_0x0528:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0541
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0541
            r1 = 0
            goto L_0x0541
        L_0x0540:
            r1 = 1
        L_0x0541:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0558
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0558
            r1 = 0
        L_0x0558:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            r4.setAllowOpenDrawer(r1, r3)
        L_0x055d:
            r12.checkLayout()
            r12.checkSystemBarColors()
            android.content.Intent r1 = r12.getIntent()
            if (r13 == 0) goto L_0x056b
            r13 = 1
            goto L_0x056c
        L_0x056b:
            r13 = 0
        L_0x056c:
            r12.handleIntent(r1, r3, r13, r3)
            java.lang.String r13 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05cc }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x05cc }
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x057c
            java.lang.String r13 = r13.toLowerCase()     // Catch:{ Exception -> 0x05cc }
            goto L_0x057d
        L_0x057c:
            r13 = r3
        L_0x057d:
            if (r1 == 0) goto L_0x0583
            java.lang.String r3 = r13.toLowerCase()     // Catch:{ Exception -> 0x05cc }
        L_0x0583:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05cc }
            if (r1 == 0) goto L_0x05a3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05cc }
            r1.<init>()     // Catch:{ Exception -> 0x05cc }
            java.lang.String r4 = "OS name "
            r1.append(r4)     // Catch:{ Exception -> 0x05cc }
            r1.append(r13)     // Catch:{ Exception -> 0x05cc }
            java.lang.String r4 = " "
            r1.append(r4)     // Catch:{ Exception -> 0x05cc }
            r1.append(r3)     // Catch:{ Exception -> 0x05cc }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05cc }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05cc }
        L_0x05a3:
            boolean r13 = r13.contains(r0)     // Catch:{ Exception -> 0x05cc }
            if (r13 != 0) goto L_0x05af
            boolean r13 = r3.contains(r0)     // Catch:{ Exception -> 0x05cc }
            if (r13 == 0) goto L_0x05d0
        L_0x05af:
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x05cc }
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x05cc }
            android.view.View r13 = r13.getDecorView()     // Catch:{ Exception -> 0x05cc }
            android.view.View r13 = r13.getRootView()     // Catch:{ Exception -> 0x05cc }
            android.view.ViewTreeObserver r0 = r13.getViewTreeObserver()     // Catch:{ Exception -> 0x05cc }
            org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0 r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0     // Catch:{ Exception -> 0x05cc }
            r1.<init>(r13)     // Catch:{ Exception -> 0x05cc }
            r12.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x05cc }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x05cc }
            goto L_0x05d0
        L_0x05cc:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x05d0:
            org.telegram.messenger.MediaController r13 = org.telegram.messenger.MediaController.getInstance()
            r13.setBaseActivity(r12, r2)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    public /* synthetic */ boolean lambda$onCreate$0$LaunchActivity(View view, MotionEvent motionEvent) {
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
                        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                        actionBarLayout2.removeFragmentFromStack(actionBarLayout2.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$onCreate$2$LaunchActivity(View view, int i, float f, float f2) {
        if (i == 0) {
            DrawerProfileCell drawerProfileCell = (DrawerProfileCell) view;
            if (drawerProfileCell.isInAvatar(f, f2)) {
                openSettings(drawerProfileCell.hasAvatar());
                return;
            }
            DrawerLayoutAdapter drawerLayoutAdapter2 = this.drawerLayoutAdapter;
            drawerLayoutAdapter2.setAccountsShown(!drawerLayoutAdapter2.isAccountsShown(), true);
        } else if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (view instanceof DrawerAddCell) {
            int i2 = -1;
            int i3 = 0;
            while (true) {
                if (i3 >= 3) {
                    break;
                } else if (!UserConfig.getInstance(i3).isClientActivated()) {
                    i2 = i3;
                    break;
                } else {
                    i3++;
                }
            }
            if (i2 >= 0) {
                lambda$runLinkRequest$32$LaunchActivity(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$32$LaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$32$LaunchActivity(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$32$LaunchActivity(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$32$LaunchActivity(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$32$LaunchActivity(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$32$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$32$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$32$LaunchActivity(new ChatActivity(bundle3));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    static /* synthetic */ void lambda$onCreate$3(View view) {
        int measuredHeight = view.getMeasuredHeight();
        FileLog.d("height = " + measuredHeight + " displayHeight = " + AndroidUtilities.displaySize.y);
        if (Build.VERSION.SDK_INT >= 21) {
            measuredHeight -= AndroidUtilities.statusBarHeight;
        }
        if (measuredHeight > AndroidUtilities.dp(100.0f) && measuredHeight < AndroidUtilities.displaySize.y) {
            int dp = AndroidUtilities.dp(100.0f) + measuredHeight;
            Point point = AndroidUtilities.displaySize;
            if (dp > point.y) {
                point.y = measuredHeight;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
                }
            }
        }
    }

    private void openSettings(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (z) {
            bundle.putBoolean("expandPhoto", true);
        }
        lambda$runLinkRequest$32$LaunchActivity(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean z = true;
            AndroidUtilities.setLightStatusBar(getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
            if (Build.VERSION.SDK_INT >= 26) {
                Window window = getWindow();
                int color = Theme.getColor("windowBackgroundGray", (boolean[]) null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
                    Window window2 = getWindow();
                    if (computePerceivedBrightness < 0.721f) {
                        z = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window2, z);
                }
            }
        }
    }

    public void switchToAccount(int i, boolean z) {
        if (i != UserConfig.selectedAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = i;
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
            if (z) {
                this.actionBarLayout.removeAllFragments();
            } else {
                this.actionBarLayout.removeFragmentFromStack(0);
            }
            DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
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
            if (UserConfig.getInstance(i).unacceptedTermsOfService != null) {
                showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
            }
            updateCurrentConnectionState(this.currentAccount);
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int i = 0;
        while (true) {
            if (i >= 3) {
                i = -1;
                break;
            } else if (UserConfig.getInstance(i).isClientActivated()) {
                break;
            } else {
                i++;
            }
        }
        TermsOfServiceView termsOfServiceView2 = this.termsOfServiceView;
        if (termsOfServiceView2 != null) {
            termsOfServiceView2.setVisibility(8);
        }
        if (i != -1) {
            switchToAccount(i, true);
            return;
        }
        DrawerLayoutAdapter drawerLayoutAdapter2 = this.drawerLayoutAdapter;
        if (drawerLayoutAdapter2 != null) {
            drawerLayoutAdapter2.notifyDataSetChanged();
        }
        Iterator<BaseFragment> it = this.actionBarLayout.fragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().onFragmentDestroy();
        }
        this.actionBarLayout.fragmentsStack.clear();
        if (AndroidUtilities.isTablet()) {
            Iterator<BaseFragment> it2 = this.layersActionBarLayout.fragmentsStack.iterator();
            while (it2.hasNext()) {
                it2.next().onFragmentDestroy();
            }
            this.layersActionBarLayout.fragmentsStack.clear();
            Iterator<BaseFragment> it3 = this.rightActionBarLayout.fragmentsStack.iterator();
            while (it3.hasNext()) {
                it3.next().onFragmentDestroy();
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
        int i = this.currentAccount;
        if (i != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowPlayServicesAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    private void checkLayout() {
        if (AndroidUtilities.isTablet() && this.rightActionBarLayout != null) {
            int i = 0;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                this.tabletFullSize = true;
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                        BaseFragment baseFragment = this.rightActionBarLayout.fragmentsStack.get(0);
                        if (baseFragment instanceof ChatActivity) {
                            ((ChatActivity) baseFragment).setIgnoreAttachOnPause(true);
                        }
                        baseFragment.onPause();
                        this.rightActionBarLayout.fragmentsStack.remove(0);
                        this.actionBarLayout.fragmentsStack.add(baseFragment);
                    }
                    if (this.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                    }
                }
                this.shadowTabletSide.setVisibility(8);
                this.rightActionBarLayout.setVisibility(8);
                View view = this.backgroundTablet;
                if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i = 8;
                }
                view.setVisibility(i);
                return;
            }
            this.tabletFullSize = false;
            if (this.actionBarLayout.fragmentsStack.size() >= 2) {
                while (1 < this.actionBarLayout.fragmentsStack.size()) {
                    BaseFragment baseFragment2 = this.actionBarLayout.fragmentsStack.get(1);
                    if (baseFragment2 instanceof ChatActivity) {
                        ((ChatActivity) baseFragment2).setIgnoreAttachOnPause(true);
                    }
                    baseFragment2.onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.rightActionBarLayout.fragmentsStack.add(baseFragment2);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
            actionBarLayout2.setVisibility(actionBarLayout2.fragmentsStack.isEmpty() ? 8 : 0);
            this.backgroundTablet.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 0 : 8);
            FrameLayout frameLayout = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    private void showUpdateActivity(int i, TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, boolean z) {
        if (this.blockingUpdateView == null) {
            AnonymousClass5 r0 = new BlockingUpdateView(this) {
                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (i == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.blockingUpdateView = r0;
            this.drawerLayoutContainer.addView(r0, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(i, tLRPC$TL_help_appUpdate, z);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int i, TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService) {
        if (this.termsOfServiceView == null) {
            TermsOfServiceView termsOfServiceView2 = new TermsOfServiceView(this);
            this.termsOfServiceView = termsOfServiceView2;
            termsOfServiceView2.setAlpha(0.0f);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceView.TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int i) {
                    UserConfig.getInstance(i).unacceptedTermsOfService = null;
                    UserConfig.getInstance(i).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (LaunchActivity.mainFragmentsStack.size() > 0) {
                        ((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)).onResume();
                    }
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable() {
                        public final void run() {
                            LaunchActivity.AnonymousClass6.this.lambda$onAcceptTerms$0$LaunchActivity$6();
                        }
                    }).start();
                }

                public /* synthetic */ void lambda$onAcceptTerms$0$LaunchActivity$6() {
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }
            });
        }
        TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService2 = UserConfig.getInstance(i).unacceptedTermsOfService;
        if (tLRPC$TL_help_termsOfService2 != tLRPC$TL_help_termsOfService && (tLRPC$TL_help_termsOfService2 == null || !tLRPC$TL_help_termsOfService2.id.data.equals(tLRPC$TL_help_termsOfService.id.data))) {
            UserConfig.getInstance(i).unacceptedTermsOfService = tLRPC$TL_help_termsOfService;
            UserConfig.getInstance(i).saveConfig(false);
        }
        this.termsOfServiceView.show(i, tLRPC$TL_help_termsOfService);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.termsOfServiceView.animate().alpha(1.0f).setDuration(150).setInterpolator(AndroidUtilities.decelerateInterpolator).setListener((Animator.AnimatorListener) null).start();
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
                    LaunchActivity.this.lambda$showPasscodeActivity$4$LaunchActivity();
                }
            });
            this.actionBarLayout.setVisibility(4);
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0) {
                    this.layersActionBarLayout.setVisibility(4);
                }
                this.rightActionBarLayout.setVisibility(4);
            }
        }
    }

    public /* synthetic */ void lambda$showPasscodeActivity$4$LaunchActivity() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.setVisibility(0);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
            if (this.layersActionBarLayout.getVisibility() == 4) {
                this.layersActionBarLayout.setVisibility(0);
            }
            this.rightActionBarLayout.setVisibility(0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v64, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v251, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v252, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v276, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v277, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v278, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v280, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v281, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v282, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v288, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v306, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v321, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v333, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v339, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v361, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v362, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v276, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v421, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: java.util.HashMap} */
    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r4v25 */
    /* JADX WARNING: type inference failed for: r3v16 */
    /* JADX WARNING: type inference failed for: r13v31, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r18v1, types: [org.telegram.tgnet.TLRPC$TL_wallPaper] */
    /* JADX WARNING: type inference failed for: r9v28 */
    /* JADX WARNING: type inference failed for: r3v58 */
    /* JADX WARNING: type inference failed for: r1v69, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r28v7 */
    /* JADX WARNING: type inference failed for: r28v8 */
    /* JADX WARNING: type inference failed for: r33v6 */
    /* JADX WARNING: type inference failed for: r28v13 */
    /* JADX WARNING: type inference failed for: r28v14 */
    /* JADX WARNING: type inference failed for: r28v15 */
    /* JADX WARNING: type inference failed for: r28v16 */
    /* JADX WARNING: type inference failed for: r4v273 */
    /* JADX WARNING: type inference failed for: r3v221 */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0279, code lost:
        if (r15.sendingText == null) goto L_0x0174;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00fa, code lost:
        r0 = r19.getIntent().getExtras();
        r7 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x010c, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x010f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0110, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0dcb, code lost:
        if (r4 == 0) goto L_0x0dcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x012d, code lost:
        if (r2.equals(r0) != false) goto L_0x0131;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0var_, code lost:
        r11[0] = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:?, code lost:
        switchToAccount(r11[0], true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x1193, code lost:
        if (r4.checkCanOpenChat(r0, r5.get(r5.size() - 1)) != false) goto L_0x1195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x11a4, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x11a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x11a6, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x11a8, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x11dd, code lost:
        if (r0.checkCanOpenChat(r5, r4.get(r4.size() - 1)) != false) goto L_0x11df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x11ee, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r5), false, true, true, false) != false) goto L_0x11a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x13e3, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1401;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x13ff, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1401;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r3v1, types: [int, boolean] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:659:0x0fa7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0280  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x06e3 A[SYNTHETIC, Splitter:B:324:0x06e3] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0ee4  */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0eeb  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x013f A[SYNTHETIC, Splitter:B:64:0x013f] */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0fb0 A[SYNTHETIC, Splitter:B:666:0x0fb0] */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1150  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x12fc  */
    /* JADX WARNING: Removed duplicated region for block: B:805:0x130c  */
    /* JADX WARNING: Removed duplicated region for block: B:839:0x141d  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x1425  */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x145f  */
    /* JADX WARNING: Removed duplicated region for block: B:853:0x146b  */
    /* JADX WARNING: Removed duplicated region for block: B:862:0x1492  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x14d7  */
    /* JADX WARNING: Removed duplicated region for block: B:878:0x151c  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r45, boolean r46, boolean r47, boolean r48) {
        /*
            r44 = this;
            r15 = r44
            r14 = r45
            r0 = r47
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r44, r45)
            r13 = 1
            if (r1 == 0) goto L_0x0023
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0022
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.rightActionBarLayout
            r0.showLastFragment()
        L_0x0022:
            return r13
        L_0x0023:
            boolean r1 = org.telegram.ui.PhotoViewer.hasInstance()
            r12 = 0
            if (r1 == 0) goto L_0x0049
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r1 = r1.isVisible()
            if (r1 == 0) goto L_0x0049
            if (r14 == 0) goto L_0x0042
            java.lang.String r1 = r45.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r45.getFlags()
            int[] r11 = new int[r13]
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r3 = "currentAccount"
            int r2 = r14.getIntExtra(r3, r2)
            r11[r12] = r2
            r2 = r11[r12]
            r15.switchToAccount(r2, r13)
            if (r48 != 0) goto L_0x007f
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r2 != 0) goto L_0x006a
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x007f
        L_0x006a:
            r44.showPasscodeActivity()
            r15.passcodeSaveIntent = r14
            r10 = r46
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r12)
            return r12
        L_0x007f:
            r10 = r46
            r9 = 0
            r15.photoPathsArray = r9
            r15.videoPath = r9
            r15.sendingText = r9
            r15.documentsPathsArray = r9
            r15.documentsOriginalPathsArray = r9
            r15.documentsMimeType = r9
            r15.documentsUrisArray = r9
            r15.contactsToSend = r9
            r15.contactsToSendUri = r9
            r2 = 1048576(0x100000, float:1.469368E-39)
            r1 = r1 & r2
            r5 = 0
            if (r1 != 0) goto L_0x1130
            if (r14 == 0) goto L_0x1130
            java.lang.String r1 = r45.getAction()
            if (r1 == 0) goto L_0x1130
            if (r0 != 0) goto L_0x1130
            java.lang.String r0 = r45.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r2 = "hash"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x0299
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x012f
            if (r14 == 0) goto L_0x012f
            android.os.Bundle r0 = r45.getExtras()
            if (r0 == 0) goto L_0x012f
            android.os.Bundle r0 = r45.getExtras()
            java.lang.String r4 = "dialogId"
            long r16 = r0.getLong(r4, r5)
            int r0 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x011d
            android.os.Bundle r0 = r45.getExtras()     // Catch:{ all -> 0x0117 }
            java.lang.String r4 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r4)     // Catch:{ all -> 0x0117 }
            if (r0 == 0) goto L_0x011b
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0117 }
            java.util.List r4 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r4)     // Catch:{ all -> 0x0117 }
            int r7 = r4.size()     // Catch:{ all -> 0x0117 }
            r8 = 0
        L_0x00e8:
            if (r8 >= r7) goto L_0x011b
            java.lang.Object r19 = r4.get(r8)     // Catch:{ all -> 0x0117 }
            androidx.core.content.pm.ShortcutInfoCompat r19 = (androidx.core.content.pm.ShortcutInfoCompat) r19     // Catch:{ all -> 0x0117 }
            java.lang.String r13 = r19.getId()     // Catch:{ all -> 0x0117 }
            boolean r13 = r0.equals(r13)     // Catch:{ all -> 0x0117 }
            if (r13 == 0) goto L_0x0113
            android.content.Intent r0 = r19.getIntent()     // Catch:{ all -> 0x0117 }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x0117 }
            java.lang.String r4 = "dialogId"
            long r7 = r0.getLong(r4, r5)     // Catch:{ all -> 0x0117 }
            java.lang.String r0 = r0.getString(r2, r9)     // Catch:{ all -> 0x010f }
            r16 = r7
            goto L_0x0125
        L_0x010f:
            r0 = move-exception
            r16 = r7
            goto L_0x0118
        L_0x0113:
            int r8 = r8 + 1
            r13 = 1
            goto L_0x00e8
        L_0x0117:
            r0 = move-exception
        L_0x0118:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x011b:
            r0 = r9
            goto L_0x0125
        L_0x011d:
            android.os.Bundle r0 = r45.getExtras()
            java.lang.String r0 = r0.getString(r2, r9)
        L_0x0125:
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash
            if (r2 == 0) goto L_0x012f
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0131
        L_0x012f:
            r16 = r5
        L_0x0131:
            java.lang.String r0 = r45.getType()
            if (r0 == 0) goto L_0x0177
            java.lang.String r2 = "text/x-vcard"
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0177
            android.os.Bundle r1 = r45.getExtras()     // Catch:{ Exception -> 0x0170 }
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0170 }
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x0170 }
            if (r1 == 0) goto L_0x0174
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0170 }
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r9, r9)     // Catch:{ Exception -> 0x0170 }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0170 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0170 }
            r3 = 5
            if (r2 <= r3) goto L_0x016c
            r15.contactsToSend = r9     // Catch:{ Exception -> 0x0170 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0170 }
            r2.<init>()     // Catch:{ Exception -> 0x0170 }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x0170 }
            r2.add(r1)     // Catch:{ Exception -> 0x0170 }
            r15.documentsMimeType = r0     // Catch:{ Exception -> 0x0170 }
            goto L_0x027d
        L_0x016c:
            r15.contactsToSendUri = r1     // Catch:{ Exception -> 0x0170 }
            goto L_0x027d
        L_0x0170:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0174:
            r0 = 1
            goto L_0x027e
        L_0x0177:
            java.lang.String r2 = "android.intent.extra.TEXT"
            java.lang.String r2 = r14.getStringExtra(r2)
            if (r2 != 0) goto L_0x018b
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x018b
            java.lang.String r2 = r4.toString()
        L_0x018b:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x01c2
            java.lang.String r7 = "http://"
            boolean r7 = r2.startsWith(r7)
            if (r7 != 0) goto L_0x01a7
            java.lang.String r7 = "https://"
            boolean r7 = r2.startsWith(r7)
            if (r7 == 0) goto L_0x01bf
        L_0x01a7:
            boolean r7 = android.text.TextUtils.isEmpty(r4)
            if (r7 != 0) goto L_0x01bf
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            r7.append(r1)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
        L_0x01bf:
            r15.sendingText = r2
            goto L_0x01ca
        L_0x01c2:
            boolean r1 = android.text.TextUtils.isEmpty(r4)
            if (r1 != 0) goto L_0x01ca
            r15.sendingText = r4
        L_0x01ca:
            java.lang.String r1 = "android.intent.extra.STREAM"
            android.os.Parcelable r1 = r14.getParcelableExtra(r1)
            if (r1 == 0) goto L_0x0277
            boolean r2 = r1 instanceof android.net.Uri
            if (r2 != 0) goto L_0x01de
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
        L_0x01de:
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x01ea
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1)
            if (r2 == 0) goto L_0x01ea
            r2 = 1
            goto L_0x01eb
        L_0x01ea:
            r2 = 0
        L_0x01eb:
            if (r2 != 0) goto L_0x0275
            if (r1 == 0) goto L_0x0221
            if (r0 == 0) goto L_0x01f9
            java.lang.String r4 = "image/"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0209
        L_0x01f9:
            java.lang.String r4 = r1.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r4 = r4.endsWith(r7)
            if (r4 == 0) goto L_0x0221
        L_0x0209:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x0214
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x0214:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0275
        L_0x0221:
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r4 == 0) goto L_0x0263
            java.lang.String r7 = "file:"
            boolean r7 = r4.startsWith(r7)
            if (r7 == 0) goto L_0x0235
            java.lang.String r7 = "file://"
            java.lang.String r4 = r4.replace(r7, r3)
        L_0x0235:
            if (r0 == 0) goto L_0x0242
            java.lang.String r3 = "video/"
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L_0x0242
            r15.videoPath = r4
            goto L_0x0275
        L_0x0242:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            if (r0 != 0) goto L_0x0254
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsPathsArray = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsOriginalPathsArray = r0
        L_0x0254:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            r0.add(r4)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x0275
        L_0x0263:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            if (r3 != 0) goto L_0x026e
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15.documentsUrisArray = r3
        L_0x026e:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            r3.add(r1)
            r15.documentsMimeType = r0
        L_0x0275:
            r0 = r2
            goto L_0x027e
        L_0x0277:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x027d
            goto L_0x0174
        L_0x027d:
            r0 = 0
        L_0x027e:
            if (r0 == 0) goto L_0x0289
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
        L_0x0289:
            r22 = r5
            r6 = r11
            r1 = r14
            r2 = r15
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            goto L_0x1141
        L_0x0299:
            java.lang.String r0 = r45.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x03a1
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x038f }
            java.lang.String r1 = r45.getType()     // Catch:{ Exception -> 0x038f }
            if (r0 == 0) goto L_0x02e3
            r2 = 0
        L_0x02b2:
            int r4 = r0.size()     // Catch:{ Exception -> 0x038f }
            if (r2 >= r4) goto L_0x02dc
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x038f }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x038f }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x038f }
            if (r7 != 0) goto L_0x02ca
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x038f }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x038f }
        L_0x02ca:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x038f }
            if (r4 == 0) goto L_0x02d9
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r4)     // Catch:{ Exception -> 0x038f }
            if (r4 == 0) goto L_0x02d9
            r0.remove(r2)     // Catch:{ Exception -> 0x038f }
            int r2 = r2 + -1
        L_0x02d9:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x02b2
        L_0x02dc:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x038f }
            if (r2 == 0) goto L_0x02e3
            r0 = r9
        L_0x02e3:
            if (r0 == 0) goto L_0x0393
            if (r1 == 0) goto L_0x0324
            java.lang.String r2 = "image/"
            boolean r2 = r1.startsWith(r2)     // Catch:{ Exception -> 0x038f }
            if (r2 == 0) goto L_0x0324
            r1 = 0
        L_0x02f0:
            int r2 = r0.size()     // Catch:{ Exception -> 0x038f }
            if (r1 >= r2) goto L_0x038d
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x038f }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x038f }
            boolean r3 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x038f }
            if (r3 != 0) goto L_0x0308
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x038f }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x038f }
        L_0x0308:
            android.net.Uri r2 = (android.net.Uri) r2     // Catch:{ Exception -> 0x038f }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x038f }
            if (r3 != 0) goto L_0x0315
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x038f }
            r3.<init>()     // Catch:{ Exception -> 0x038f }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x038f }
        L_0x0315:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x038f }
            r3.<init>()     // Catch:{ Exception -> 0x038f }
            r3.uri = r2     // Catch:{ Exception -> 0x038f }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray     // Catch:{ Exception -> 0x038f }
            r2.add(r3)     // Catch:{ Exception -> 0x038f }
            int r1 = r1 + 1
            goto L_0x02f0
        L_0x0324:
            r2 = 0
        L_0x0325:
            int r4 = r0.size()     // Catch:{ Exception -> 0x038f }
            if (r2 >= r4) goto L_0x038d
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x038f }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x038f }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x038f }
            if (r7 != 0) goto L_0x033d
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x038f }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x038f }
        L_0x033d:
            r7 = r4
            android.net.Uri r7 = (android.net.Uri) r7     // Catch:{ Exception -> 0x038f }
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.getPath(r7)     // Catch:{ Exception -> 0x038f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x038f }
            if (r4 != 0) goto L_0x034b
            r4 = r8
        L_0x034b:
            if (r8 == 0) goto L_0x0378
            java.lang.String r7 = "file:"
            boolean r7 = r8.startsWith(r7)     // Catch:{ Exception -> 0x038f }
            if (r7 == 0) goto L_0x035b
            java.lang.String r7 = "file://"
            java.lang.String r8 = r8.replace(r7, r3)     // Catch:{ Exception -> 0x038f }
        L_0x035b:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x038f }
            if (r7 != 0) goto L_0x036d
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x038f }
            r7.<init>()     // Catch:{ Exception -> 0x038f }
            r15.documentsPathsArray = r7     // Catch:{ Exception -> 0x038f }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x038f }
            r7.<init>()     // Catch:{ Exception -> 0x038f }
            r15.documentsOriginalPathsArray = r7     // Catch:{ Exception -> 0x038f }
        L_0x036d:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x038f }
            r7.add(r8)     // Catch:{ Exception -> 0x038f }
            java.util.ArrayList<java.lang.String> r7 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x038f }
            r7.add(r4)     // Catch:{ Exception -> 0x038f }
            goto L_0x038a
        L_0x0378:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x038f }
            if (r4 != 0) goto L_0x0383
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x038f }
            r4.<init>()     // Catch:{ Exception -> 0x038f }
            r15.documentsUrisArray = r4     // Catch:{ Exception -> 0x038f }
        L_0x0383:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x038f }
            r4.add(r7)     // Catch:{ Exception -> 0x038f }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x038f }
        L_0x038a:
            int r2 = r2 + 1
            goto L_0x0325
        L_0x038d:
            r0 = 0
            goto L_0x0394
        L_0x038f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0393:
            r0 = 1
        L_0x0394:
            if (r0 == 0) goto L_0x1130
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
            goto L_0x1130
        L_0x03a1:
            java.lang.String r0 = r45.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x105b
            android.net.Uri r0 = r45.getData()
            if (r0 == 0) goto L_0x1035
            java.lang.String r4 = r0.getScheme()
            if (r4 == 0) goto L_0x0eaf
            r7 = -1
            int r8 = r4.hashCode()
            r13 = 3699(0xe73, float:5.183E-42)
            if (r8 == r13) goto L_0x03e1
            r13 = 3213448(0x310888, float:4.503E-39)
            if (r8 == r13) goto L_0x03d7
            r13 = 99617003(0x5var_eb, float:2.2572767E-35)
            if (r8 == r13) goto L_0x03cd
            goto L_0x03ea
        L_0x03cd:
            java.lang.String r8 = "https"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03ea
            r7 = 1
            goto L_0x03ea
        L_0x03d7:
            java.lang.String r8 = "http"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03ea
            r7 = 0
            goto L_0x03ea
        L_0x03e1:
            java.lang.String r8 = "tg"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03ea
            r7 = 2
        L_0x03ea:
            java.lang.String r8 = "text"
            r16 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            if (r7 == 0) goto L_0x0ac0
            r13 = 1
            if (r7 == r13) goto L_0x0ac0
            r13 = 2
            if (r7 == r13) goto L_0x03f8
            goto L_0x0eaf
        L_0x03f8:
            java.lang.String r0 = r0.toString()
            java.lang.String r7 = "tg:resolve"
            boolean r7 = r0.startsWith(r7)
            java.lang.String r13 = "nonce"
            java.lang.String r9 = "callback_url"
            java.lang.String r5 = "public_key"
            java.lang.String r6 = "bot_id"
            java.lang.String r4 = "payload"
            java.lang.String r12 = "scope"
            java.lang.String r10 = "tg://telegram.org"
            if (r7 != 0) goto L_0x09eb
            java.lang.String r7 = "tg://resolve"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x041c
            goto L_0x09eb
        L_0x041c:
            java.lang.String r7 = "tg:privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0996
            java.lang.String r7 = "tg://privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x042e
            goto L_0x0996
        L_0x042e:
            java.lang.String r7 = "tg:bg"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0821
            java.lang.String r7 = "tg://bg"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x0440
            goto L_0x0821
        L_0x0440:
            java.lang.String r7 = "tg:join"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0805
            java.lang.String r7 = "tg://join"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x0452
            goto L_0x0805
        L_0x0452:
            java.lang.String r7 = "tg:addstickers"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x07e8
            java.lang.String r7 = "tg://addstickers"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x0464
            goto L_0x07e8
        L_0x0464:
            java.lang.String r7 = "tg:msg"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0763
            java.lang.String r7 = "tg://msg"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0763
            java.lang.String r7 = "tg://share"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0763
            java.lang.String r7 = "tg:share"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x0486
            goto L_0x0763
        L_0x0486:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0745
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0498
            goto L_0x0745
        L_0x0498:
            java.lang.String r1 = "tg:login"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0704
            java.lang.String r1 = "tg://login"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04aa
            goto L_0x0704
        L_0x04aa:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x06ad
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04bc
            goto L_0x06ad
        L_0x04bc:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x064f
            java.lang.String r1 = "tg://passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x064f
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04d6
            goto L_0x064f
        L_0x04d6:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x061a
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04e8
            goto L_0x061a
        L_0x04e8:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05db
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04fa
            goto L_0x05db
        L_0x04fa:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0545
            java.lang.String r1 = "tg://settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x050b
            goto L_0x0545
        L_0x050b:
            java.lang.String r1 = "tg://"
            java.lang.String r0 = r0.replace(r1, r3)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r3)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0524
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r1)
        L_0x0524:
            r30 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            goto L_0x0ab6
        L_0x0545:
            java.lang.String r1 = "themes"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x056a
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 2
            goto L_0x0ab2
        L_0x056a:
            java.lang.String r1 = "devices"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x058f
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 3
            goto L_0x0ab2
        L_0x058f:
            java.lang.String r1 = "folders"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x05b5
            r0 = 4
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 4
            goto L_0x0ab2
        L_0x05b5:
            java.lang.String r1 = "change_number"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x0ae1
            r0 = 5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 5
            goto L_0x0ab2
        L_0x05db:
            java.lang.String r1 = "tg:addtheme"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r34 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            goto L_0x0ed5
        L_0x061a:
            java.lang.String r1 = "tg:setlanguage"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r29 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            goto L_0x0ab4
        L_0x064f:
            java.lang.String r1 = "tg:passport"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r12)
            boolean r7 = android.text.TextUtils.isEmpty(r3)
            if (r7 != 0) goto L_0x068c
            java.lang.String r7 = "{"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x068c
            java.lang.String r7 = "}"
            boolean r7 = r3.endsWith(r7)
            if (r7 == 0) goto L_0x068c
            java.lang.String r4 = r0.getQueryParameter(r13)
            r1.put(r13, r4)
            goto L_0x0693
        L_0x068c:
            java.lang.String r7 = r0.getQueryParameter(r4)
            r1.put(r4, r7)
        L_0x0693:
            java.lang.String r4 = r0.getQueryParameter(r6)
            r1.put(r6, r4)
            r1.put(r12, r3)
            java.lang.String r3 = r0.getQueryParameter(r5)
            r1.put(r5, r3)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r1.put(r9, r0)
            goto L_0x0a4f
        L_0x06ad:
            java.lang.String r1 = "tg:openmessage"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "chat_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "message_id"
            java.lang.String r0 = r0.getQueryParameter(r4)
            if (r1 == 0) goto L_0x06d6
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x06df }
            goto L_0x06e0
        L_0x06d6:
            if (r3 == 0) goto L_0x06df
            int r1 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x06df }
            r3 = r1
            r1 = 0
            goto L_0x06e1
        L_0x06df:
            r1 = 0
        L_0x06e0:
            r3 = 0
        L_0x06e1:
            if (r0 == 0) goto L_0x06e8
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x06e8 }
            goto L_0x06e9
        L_0x06e8:
            r0 = 0
        L_0x06e9:
            r27 = r0
            r24 = r1
            r26 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            goto L_0x0ab0
        L_0x0704:
            java.lang.String r1 = "tg:login"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "code"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r31 = r0
            r32 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0aba
        L_0x0745:
            java.lang.String r1 = "tg:confirmphone"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "phone"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r3 = r1
            r1 = 0
            goto L_0x09dd
        L_0x0763:
            java.lang.String r4 = "tg:msg"
            java.lang.String r0 = r0.replace(r4, r10)
            java.lang.String r4 = "tg://msg"
            java.lang.String r0 = r0.replace(r4, r10)
            java.lang.String r4 = "tg://share"
            java.lang.String r0 = r0.replace(r4, r10)
            java.lang.String r4 = "tg:share"
            java.lang.String r0 = r0.replace(r4, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0788
            goto L_0x0789
        L_0x0788:
            r3 = r4
        L_0x0789:
            java.lang.String r4 = r0.getQueryParameter(r8)
            if (r4 == 0) goto L_0x07bb
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x07a6
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r1)
            java.lang.String r3 = r4.toString()
            r4 = 1
            goto L_0x07a7
        L_0x07a6:
            r4 = 0
        L_0x07a7:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            goto L_0x07bc
        L_0x07bb:
            r4 = 0
        L_0x07bc:
            int r0 = r3.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            if (r0 <= r5) goto L_0x07ca
            java.lang.String r0 = r3.substring(r6, r5)
            goto L_0x07cb
        L_0x07ca:
            r0 = r3
        L_0x07cb:
            boolean r3 = r0.endsWith(r1)
            if (r3 == 0) goto L_0x07dc
            int r3 = r0.length()
            r5 = 1
            int r3 = r3 - r5
            java.lang.String r0 = r0.substring(r6, r3)
            goto L_0x07cb
        L_0x07dc:
            r1 = r0
            r12 = r4
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            goto L_0x09e4
        L_0x07e8:
            java.lang.String r1 = "tg:addstickers"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r5 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            goto L_0x09df
        L_0x0805:
            java.lang.String r1 = "tg:join"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r4 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            goto L_0x09de
        L_0x0821:
            java.lang.String r1 = "tg:bg"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r3 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r3.<init>()
            r1.settings = r3
            java.lang.String r3 = "slug"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
            if (r3 != 0) goto L_0x084f
            java.lang.String r3 = "color"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
        L_0x084f:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x086d
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x086d
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0868 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0868 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0868 }
            r3 = r3 | r16
            r0.background_color = r3     // Catch:{ Exception -> 0x0868 }
        L_0x0868:
            r3 = 0
            r1.slug = r3
            goto L_0x096f
        L_0x086d:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x08cd
            int r3 = r3.length()
            r4 = 13
            if (r3 != r4) goto L_0x08cd
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            r5 = 45
            if (r3 != r5) goto L_0x08cd
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x08b0 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x08b0 }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x08b0 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x08b0 }
            r4 = r4 | r16
            r3.background_color = r4     // Catch:{ Exception -> 0x08b0 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x08b0 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x08b0 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x08b0 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x08b0 }
            r4 = r4 | r16
            r3.second_background_color = r4     // Catch:{ Exception -> 0x08b0 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x08b0 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x08b0 }
        L_0x08b0:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x08c8 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x08c8 }
            if (r3 != 0) goto L_0x08c8
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x08c8 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x08c8 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x08c8 }
            r3.rotation = r0     // Catch:{ Exception -> 0x08c8 }
        L_0x08c8:
            r3 = 0
            r1.slug = r3
            goto L_0x096f
        L_0x08cd:
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x090a
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x090a
            int r4 = r3.length
            if (r4 <= 0) goto L_0x090a
            r4 = 0
        L_0x08e5:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x090a
            r5 = r3[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x08f8
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x0907
        L_0x08f8:
            r6 = 1
            r5 = r3[r4]
            java.lang.String r7 = "motion"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x0907
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x0907:
            int r4 = r4 + 1
            goto L_0x08e5
        L_0x090a:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r3.intensity = r4
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0957 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0957 }
            if (r4 != 0) goto L_0x0957
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0957 }
            r5 = 6
            r6 = 0
            java.lang.String r7 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x0957 }
            r6 = 16
            int r7 = java.lang.Integer.parseInt(r7, r6)     // Catch:{ Exception -> 0x0957 }
            r6 = r7 | r16
            r4.background_color = r6     // Catch:{ Exception -> 0x0957 }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0957 }
            if (r4 <= r5) goto L_0x0957
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0957 }
            r5 = 7
            java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x0957 }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0957 }
            r3 = r3 | r16
            r4.second_background_color = r3     // Catch:{ Exception -> 0x0957 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0957 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0957 }
        L_0x0957:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x096f }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x096f }
            if (r3 != 0) goto L_0x096f
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x096f }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x096f }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x096f }
            r3.rotation = r0     // Catch:{ Exception -> 0x096f }
        L_0x096f:
            r33 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0abc
        L_0x0996:
            java.lang.String r1 = "tg:privatepost"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r3 = "channel"
            java.lang.String r0 = r0.getQueryParameter(r3)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r3 = r1.intValue()
            if (r3 == 0) goto L_0x09da
            int r3 = r0.intValue()
            if (r3 != 0) goto L_0x09c7
            goto L_0x09da
        L_0x09c7:
            r13 = r0
            r19 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r16 = 0
            r17 = 0
            goto L_0x0aa8
        L_0x09da:
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x09dd:
            r4 = 0
        L_0x09de:
            r5 = 0
        L_0x09df:
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
        L_0x09e4:
            r13 = 0
            r16 = 0
            r17 = 0
            goto L_0x0a92
        L_0x09eb:
            java.lang.String r1 = "tg:resolve"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "telegrampassport"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L_0x0a61
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r12)
            boolean r7 = android.text.TextUtils.isEmpty(r3)
            if (r7 != 0) goto L_0x0a30
            java.lang.String r7 = "{"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x0a30
            java.lang.String r7 = "}"
            boolean r7 = r3.endsWith(r7)
            if (r7 == 0) goto L_0x0a30
            java.lang.String r4 = r0.getQueryParameter(r13)
            r1.put(r13, r4)
            goto L_0x0a37
        L_0x0a30:
            java.lang.String r7 = r0.getQueryParameter(r4)
            r1.put(r4, r7)
        L_0x0a37:
            java.lang.String r4 = r0.getQueryParameter(r6)
            r1.put(r6, r4)
            r1.put(r12, r3)
            java.lang.String r3 = r0.getQueryParameter(r5)
            r1.put(r5, r3)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r1.put(r9, r0)
        L_0x0a4f:
            r18 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
            goto L_0x0a94
        L_0x0a61:
            java.lang.String r3 = "start"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "startgroup"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "game"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "post"
            java.lang.String r0 = r0.getQueryParameter(r6)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r6 = r0.intValue()
            if (r6 != 0) goto L_0x0a97
            r17 = r1
            r6 = r3
            r7 = r4
            r16 = r5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
        L_0x0a92:
            r18 = 0
        L_0x0a94:
            r19 = 0
            goto L_0x0aaa
        L_0x0a97:
            r19 = r0
            r17 = r1
            r6 = r3
            r7 = r4
            r16 = r5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r9 = 0
            r10 = 2
            r12 = 0
            r13 = 0
        L_0x0aa8:
            r18 = 0
        L_0x0aaa:
            r24 = 0
            r26 = 0
            r27 = 0
        L_0x0ab0:
            r28 = 0
        L_0x0ab2:
            r29 = 0
        L_0x0ab4:
            r30 = 0
        L_0x0ab6:
            r31 = 0
            r32 = 0
        L_0x0aba:
            r33 = 0
        L_0x0abc:
            r34 = 0
            goto L_0x0ed5
        L_0x0ac0:
            java.lang.String r4 = r0.getHost()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r5 = "telegram.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0ae4
            java.lang.String r5 = "t.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0ae4
            java.lang.String r5 = "telegram.dog"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0ae1
            goto L_0x0ae4
        L_0x0ae1:
            r9 = 0
            goto L_0x0eaf
        L_0x0ae4:
            java.lang.String r4 = r0.getPath()
            if (r4 == 0) goto L_0x0e6f
            int r5 = r4.length()
            r6 = 1
            if (r5 <= r6) goto L_0x0e6f
            java.lang.String r4 = r4.substring(r6)
            java.lang.String r5 = "bg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "bg/"
            java.lang.String r3 = r4.replace(r5, r3)
            r1.slug = r3
            if (r3 == 0) goto L_0x0b2e
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x0b2e
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0b28 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0b28 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0b28 }
            r3 = r3 | r16
            r0.background_color = r3     // Catch:{ Exception -> 0x0b28 }
        L_0x0b28:
            r3 = 0
            r1.slug = r3
            r9 = r3
            goto L_0x0CLASSNAME
        L_0x0b2e:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0b8e
            int r3 = r3.length()
            r4 = 13
            if (r3 != r4) goto L_0x0b8e
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            r5 = 45
            if (r3 != r5) goto L_0x0b8e
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b71 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0b71 }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0b71 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0b71 }
            r4 = r4 | r16
            r3.background_color = r4     // Catch:{ Exception -> 0x0b71 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b71 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0b71 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0b71 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0b71 }
            r4 = r4 | r16
            r3.second_background_color = r4     // Catch:{ Exception -> 0x0b71 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b71 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0b71 }
        L_0x0b71:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0b89 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0b89 }
            if (r3 != 0) goto L_0x0b89
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b89 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0b89 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0b89 }
            r3.rotation = r0     // Catch:{ Exception -> 0x0b89 }
        L_0x0b89:
            r9 = 0
            r1.slug = r9
            goto L_0x0CLASSNAME
        L_0x0b8e:
            r9 = 0
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x0bcc
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x0bcc
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0bcc
            r4 = 0
        L_0x0ba7:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x0bcc
            r5 = r3[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0bba
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x0bc9
        L_0x0bba:
            r6 = 1
            r5 = r3[r4]
            java.lang.String r7 = "motion"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x0bc9
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x0bc9:
            int r4 = r4 + 1
            goto L_0x0ba7
        L_0x0bcc:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0be5
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r3 = r3.intValue()
            r4.intensity = r3
            goto L_0x0beb
        L_0x0be5:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            r4 = 50
            r3.intensity = r4
        L_0x0beb:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0c2c }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0c2c }
            if (r4 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0c2c }
            r5 = 6
            r6 = 0
            java.lang.String r7 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x0c2c }
            r6 = 16
            int r7 = java.lang.Integer.parseInt(r7, r6)     // Catch:{ Exception -> 0x0c2c }
            r6 = r7 | r16
            r4.background_color = r6     // Catch:{ Exception -> 0x0c2c }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0c2c }
            if (r4 <= r5) goto L_0x0c2c
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0c2c }
            r5 = 7
            java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x0c2c }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0c2c }
            r3 = r3 | r16
            r4.second_background_color = r3     // Catch:{ Exception -> 0x0c2c }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0c2c }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0c2c }
            goto L_0x0c2c
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0c2c }
            r4 = -1
            r3.background_color = r4     // Catch:{ Exception -> 0x0c2c }
        L_0x0c2c:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0CLASSNAME }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0CLASSNAME }
            if (r3 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0CLASSNAME }
            r3.rotation = r0     // Catch:{ Exception -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            r18 = r1
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r17 = r16
            r19 = r17
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r9 = 0
            java.lang.String r5 = "login/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0c7d
            java.lang.String r0 = "login/"
            java.lang.String r3 = r4.replace(r0, r3)
            r17 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r18 = r16
        L_0x0CLASSNAME:
            r19 = r18
        L_0x0CLASSNAME:
            r20 = r19
            r10 = 2
            goto L_0x0e85
        L_0x0c7d:
            java.lang.String r5 = "joinchat/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0c9c
            java.lang.String r0 = "joinchat/"
            java.lang.String r3 = r4.replace(r0, r3)
            r5 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r6 = r4
            r7 = r6
        L_0x0CLASSNAME:
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
        L_0x0CLASSNAME:
            r17 = r16
        L_0x0CLASSNAME:
            r18 = r17
            goto L_0x0CLASSNAME
        L_0x0c9c:
            java.lang.String r5 = "addstickers/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0cb2
            java.lang.String r0 = "addstickers/"
            java.lang.String r3 = r4.replace(r0, r3)
            r6 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r7 = r5
            goto L_0x0CLASSNAME
        L_0x0cb2:
            java.lang.String r5 = "msg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x0dfc
            java.lang.String r5 = "share/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0cc4
            goto L_0x0dfc
        L_0x0cc4:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0cf0
            java.lang.String r1 = "phone"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r3 = r1
            r4 = r9
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r17 = r16
            r18 = r17
            r19 = r18
            r20 = r19
            r10 = 2
            r24 = 0
            r1 = r0
            r0 = r20
            goto L_0x0e87
        L_0x0cf0:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0d0b
            r0 = 12
            java.lang.String r3 = r4.substring(r0)
            r13 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r16 = r12
            goto L_0x0CLASSNAME
        L_0x0d0b:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0d29
            r0 = 9
            java.lang.String r3 = r4.substring(r0)
            r16 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r17 = r13
            goto L_0x0CLASSNAME
        L_0x0d29:
            java.lang.String r1 = "c/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0d7e
            java.util.List r0 = r0.getPathSegments()
            int r1 = r0.size()
            r3 = 3
            if (r1 != r3) goto L_0x0d65
            r1 = 1
            java.lang.Object r3 = r0.get(r1)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            r10 = 2
            java.lang.Object r0 = r0.get(r10)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            if (r1 == 0) goto L_0x0d66
            int r1 = r3.intValue()
            if (r1 != 0) goto L_0x0d5f
            goto L_0x0d66
        L_0x0d5f:
            r43 = r3
            r3 = r0
            r0 = r43
            goto L_0x0d68
        L_0x0d65:
            r10 = 2
        L_0x0d66:
            r0 = r9
            r3 = r0
        L_0x0d68:
            r20 = r0
            r19 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r17 = r16
            r18 = r17
            goto L_0x0e85
        L_0x0d7e:
            r10 = 2
            int r1 = r4.length()
            r3 = 1
            if (r1 < r3) goto L_0x0e71
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r1.<init>(r3)
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x0da8
            r3 = 0
            java.lang.Object r4 = r1.get(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r5 = "s"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0da9
            r1.remove(r3)
            goto L_0x0da9
        L_0x0da8:
            r3 = 0
        L_0x0da9:
            int r4 = r1.size()
            if (r4 <= 0) goto L_0x0dcf
            java.lang.Object r4 = r1.get(r3)
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            int r4 = r1.size()
            r5 = 1
            if (r4 <= r5) goto L_0x0dcd
            java.lang.Object r1 = r1.get(r5)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x0dd1
        L_0x0dcd:
            r1 = r9
            goto L_0x0dd1
        L_0x0dcf:
            r1 = r9
            r3 = r1
        L_0x0dd1:
            java.lang.String r4 = "start"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "startgroup"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "game"
            java.lang.String r0 = r0.getQueryParameter(r6)
            r12 = r0
            r19 = r1
            r7 = r4
            r8 = r5
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r13 = r6
            r16 = r13
            r17 = r16
            r18 = r17
            r20 = r18
            r24 = 0
            r4 = r3
            r3 = r20
            goto L_0x0e87
        L_0x0dfc:
            r10 = 2
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0e06
            goto L_0x0e07
        L_0x0e06:
            r3 = r4
        L_0x0e07:
            java.lang.String r4 = r0.getQueryParameter(r8)
            if (r4 == 0) goto L_0x0e39
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x0e24
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r1)
            java.lang.String r3 = r4.toString()
            r4 = 1
            goto L_0x0e25
        L_0x0e24:
            r4 = 0
        L_0x0e25:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            goto L_0x0e3a
        L_0x0e39:
            r4 = 0
        L_0x0e3a:
            int r0 = r3.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            if (r0 <= r5) goto L_0x0e47
            java.lang.String r3 = r3.substring(r6, r5)
        L_0x0e47:
            boolean r0 = r3.endsWith(r1)
            if (r0 == 0) goto L_0x0e58
            int r0 = r3.length()
            r5 = 1
            int r0 = r0 - r5
            java.lang.String r3 = r3.substring(r6, r0)
            goto L_0x0e47
        L_0x0e58:
            r0 = r3
            r24 = r4
            r1 = r9
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r17 = r16
            r18 = r17
            r19 = r18
            r20 = r19
            goto L_0x0e87
        L_0x0e6f:
            r9 = 0
            r10 = 2
        L_0x0e71:
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r12 = r8
            r13 = r12
            r16 = r13
            r17 = r16
            r18 = r17
            r19 = r18
            r20 = r19
        L_0x0e85:
            r24 = 0
        L_0x0e87:
            r30 = r9
            r32 = r30
            r29 = r13
            r34 = r16
            r31 = r17
            r33 = r18
            r13 = r20
            r26 = 0
            r27 = 0
            r28 = 0
            r17 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            r18 = r32
            r16 = r12
            r12 = r24
            r24 = 0
            r43 = r1
            r1 = r0
            r0 = r43
            goto L_0x0ed5
        L_0x0eaf:
            r10 = 2
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r13 = r7
            r16 = r13
            r17 = r16
            r18 = r17
            r19 = r18
            r29 = r19
            r30 = r29
            r31 = r30
            r32 = r31
            r33 = r32
            r34 = r33
            r12 = 0
            r24 = 0
            r26 = 0
            r27 = 0
            r28 = 0
        L_0x0ed5:
            if (r31 != 0) goto L_0x0eeb
            int r8 = r15.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            boolean r8 = r8.isClientActivated()
            if (r8 == 0) goto L_0x0ee4
            goto L_0x0eeb
        L_0x0ee4:
            r41 = r11
            r2 = r15
            r22 = 0
            goto L_0x1030
        L_0x0eeb:
            if (r3 != 0) goto L_0x1015
            if (r0 == 0) goto L_0x0ef1
            goto L_0x1015
        L_0x0ef1:
            if (r17 != 0) goto L_0x0fc9
            if (r4 != 0) goto L_0x0fc9
            if (r5 != 0) goto L_0x0fc9
            if (r1 != 0) goto L_0x0fc9
            if (r16 != 0) goto L_0x0fc9
            if (r18 != 0) goto L_0x0fc9
            if (r30 != 0) goto L_0x0fc9
            if (r29 != 0) goto L_0x0fc9
            if (r31 != 0) goto L_0x0fc9
            if (r33 != 0) goto L_0x0fc9
            if (r13 != 0) goto L_0x0fc9
            if (r34 != 0) goto L_0x0fc9
            if (r32 == 0) goto L_0x0f0d
            goto L_0x0fc9
        L_0x0f0d:
            android.content.ContentResolver r35 = r44.getContentResolver()     // Catch:{ Exception -> 0x0fb6 }
            android.net.Uri r36 = r45.getData()     // Catch:{ Exception -> 0x0fb6 }
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            android.database.Cursor r1 = r35.query(r36, r37, r38, r39, r40)     // Catch:{ Exception -> 0x0fb6 }
            if (r1 == 0) goto L_0x0faa
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x0f9c }
            if (r0 == 0) goto L_0x0faa
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0f9c }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0f9c }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x0f9c }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0f9c }
            r2 = 0
            r8 = 3
        L_0x0f3d:
            if (r2 >= r8) goto L_0x0f5a
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x0var_ }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x0var_ }
            if (r3 != r0) goto L_0x0var_
            r3 = 0
            r11[r3] = r2     // Catch:{ all -> 0x0var_ }
            r0 = r11[r3]     // Catch:{ all -> 0x0var_ }
            r3 = 1
            r15.switchToAccount(r0, r3)     // Catch:{ all -> 0x0f9a }
            goto L_0x0f5b
        L_0x0var_:
            r3 = 1
            int r2 = r2 + 1
            goto L_0x0f3d
        L_0x0var_:
            r0 = move-exception
            r3 = 1
            goto L_0x0f9f
        L_0x0f5a:
            r3 = 1
        L_0x0f5b:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0f9a }
            int r2 = r1.getInt(r0)     // Catch:{ all -> 0x0f9a }
            r4 = 0
            r0 = r11[r4]     // Catch:{ all -> 0x0f9a }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x0f9a }
            int r5 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x0f9a }
            java.lang.Object[] r6 = new java.lang.Object[r4]     // Catch:{ all -> 0x0f9a }
            r0.postNotificationName(r5, r6)     // Catch:{ all -> 0x0f9a }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0var_ }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0var_ }
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r4 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x0var_ }
            if (r4 == 0) goto L_0x0f8a
            r24 = r2
            r2 = 0
            r4 = 1
            goto L_0x0fae
        L_0x0f8a:
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x0var_ }
            r24 = r2
            if (r0 == 0) goto L_0x0fac
            r2 = 1
            goto L_0x0fad
        L_0x0var_:
            r0 = move-exception
            r24 = r2
            goto L_0x0f9f
        L_0x0f9a:
            r0 = move-exception
            goto L_0x0f9f
        L_0x0f9c:
            r0 = move-exception
            r3 = 1
            r8 = 3
        L_0x0f9f:
            throw r0     // Catch:{ all -> 0x0fa0 }
        L_0x0fa0:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x0fa7
            r1.close()     // Catch:{ all -> 0x0fa7 }
        L_0x0fa7:
            throw r2     // Catch:{ Exception -> 0x0fa8 }
        L_0x0fa8:
            r0 = move-exception
            goto L_0x0fb9
        L_0x0faa:
            r3 = 1
            r8 = 3
        L_0x0fac:
            r2 = 0
        L_0x0fad:
            r4 = 0
        L_0x0fae:
            if (r1 == 0) goto L_0x0fbe
            r1.close()     // Catch:{ Exception -> 0x0fb4 }
            goto L_0x0fbe
        L_0x0fb4:
            r0 = move-exception
            goto L_0x0fbb
        L_0x0fb6:
            r0 = move-exception
            r3 = 1
            r8 = 3
        L_0x0fb9:
            r2 = 0
            r4 = 0
        L_0x0fbb:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0fbe:
            r0 = r2
            r12 = r4
            r41 = r11
            r2 = r15
            r13 = r28
            r22 = 0
            goto L_0x1043
        L_0x0fc9:
            r3 = 1
            r8 = 3
            if (r1 == 0) goto L_0x0fe7
            java.lang.String r0 = "@"
            boolean r0 = r1.startsWith(r0)
            if (r0 == 0) goto L_0x0fe7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = " "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x0fe8
        L_0x0fe7:
            r0 = r1
        L_0x0fe8:
            r21 = 0
            r2 = r11[r21]
            r20 = 0
            r1 = r44
            r25 = 1
            r3 = r17
            r22 = 0
            r8 = r0
            r9 = r12
            r12 = 2
            r10 = r19
            r41 = r11
            r11 = r13
            r13 = 0
            r12 = r16
            r13 = r18
            r14 = r29
            r15 = r30
            r16 = r31
            r17 = r32
            r18 = r33
            r19 = r34
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r2 = r44
            goto L_0x1030
        L_0x1015:
            r41 = r11
            r22 = 0
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r4 = "phone"
            r1.putString(r4, r3)
            r1.putString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$N1thb-LLgMOn57u-_wgkv5RqrBk r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$N1thb-LLgMOn57u-_wgkv5RqrBk
            r2 = r44
            r0.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x1030:
            r13 = r28
            r0 = 0
            r12 = 0
            goto L_0x1043
        L_0x1035:
            r22 = r5
            r41 = r11
            r2 = r15
            r0 = 0
            r12 = 0
            r13 = 0
            r24 = 0
            r26 = 0
            r27 = 0
        L_0x1043:
            r1 = r45
            r7 = r0
            r42 = r12
            r10 = r13
            r16 = r22
            r12 = r24
            r0 = r26
            r4 = r27
            r6 = r41
            r3 = 0
            r5 = 0
            r8 = 0
            r9 = 0
            r11 = 0
            r13 = 0
            goto L_0x1144
        L_0x105b:
            r22 = r5
            r41 = r11
            r2 = r15
            java.lang.String r0 = r45.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x107c
            r1 = r45
            r16 = r22
            r6 = r41
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 1
            goto L_0x113f
        L_0x107c:
            java.lang.String r0 = r45.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1099
            r1 = r45
            r16 = r22
            r6 = r41
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 1
            goto L_0x1140
        L_0x1099:
            java.lang.String r0 = r45.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x1102
            java.lang.String r0 = "chatId"
            r1 = r45
            r3 = 0
            int r0 = r1.getIntExtra(r0, r3)
            java.lang.String r4 = "userId"
            int r4 = r1.getIntExtra(r4, r3)
            java.lang.String r5 = "encId"
            int r5 = r1.getIntExtra(r5, r3)
            if (r0 == 0) goto L_0x10cf
            r6 = r41
            r4 = r6[r3]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r4.postNotificationName(r5, r7)
            r5 = 0
        L_0x10cc:
            r12 = 0
        L_0x10cd:
            r13 = 0
            goto L_0x10f9
        L_0x10cf:
            r6 = r41
            if (r4 == 0) goto L_0x10e4
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r7)
            r12 = r4
            r0 = 0
            r5 = 0
            goto L_0x10cd
        L_0x10e4:
            if (r5 == 0) goto L_0x10f5
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r7)
            r0 = 0
            goto L_0x10cc
        L_0x10f5:
            r0 = 0
            r5 = 0
            r12 = 0
            r13 = 1
        L_0x10f9:
            r16 = r22
            r4 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            goto L_0x1142
        L_0x1102:
            r1 = r45
            r6 = r41
            r3 = 0
            java.lang.String r0 = r45.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x111b
            r16 = r22
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 1
            goto L_0x113d
        L_0x111b:
            java.lang.String r0 = r45.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1136
            r16 = r22
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            goto L_0x113e
        L_0x1130:
            r22 = r5
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
        L_0x1136:
            r16 = r22
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
        L_0x113d:
            r9 = 0
        L_0x113e:
            r10 = 0
        L_0x113f:
            r11 = 0
        L_0x1140:
            r12 = 0
        L_0x1141:
            r13 = 0
        L_0x1142:
            r42 = 0
        L_0x1144:
            int r14 = r2.currentAccount
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
            boolean r14 = r14.isClientActivated()
            if (r14 == 0) goto L_0x1483
            if (r12 == 0) goto L_0x11aa
            if (r42 == 0) goto L_0x1159
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r2, r12, r3)
            goto L_0x1483
        L_0x1159:
            if (r7 == 0) goto L_0x1162
            r7 = 1
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r2, r12, r7)
        L_0x115f:
            r4 = 0
            goto L_0x1485
        L_0x1162:
            r7 = 1
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "user_id"
            r0.putInt(r5, r12)
            if (r4 == 0) goto L_0x1174
            java.lang.String r5 = "message_id"
            r0.putInt(r5, r4)
        L_0x1174:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x1195
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = mainFragmentsStack
            int r6 = r5.size()
            int r6 = r6 - r7
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            boolean r4 = r4.checkCanOpenChat(r0, r5)
            if (r4 == 0) goto L_0x11a8
        L_0x1195:
            org.telegram.ui.ChatActivity r9 = new org.telegram.ui.ChatActivity
            r9.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r2.actionBarLayout
            r10 = 0
            r11 = 1
            r12 = 1
            r13 = 0
            boolean r0 = r8.presentFragment(r9, r10, r11, r12, r13)
            if (r0 == 0) goto L_0x11a8
        L_0x11a6:
            r13 = 1
            goto L_0x120c
        L_0x11a8:
            r13 = 0
            goto L_0x120c
        L_0x11aa:
            r7 = 1
            if (r0 == 0) goto L_0x11f1
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.String r8 = "chat_id"
            r5.putInt(r8, r0)
            if (r4 == 0) goto L_0x11be
            java.lang.String r0 = "message_id"
            r5.putInt(r0, r4)
        L_0x11be:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x11df
            r0 = r6[r3]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r6 = r4.size()
            int r6 = r6 - r7
            java.lang.Object r4 = r4.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r0 = r0.checkCanOpenChat(r5, r4)
            if (r0 == 0) goto L_0x11a8
        L_0x11df:
            org.telegram.ui.ChatActivity r9 = new org.telegram.ui.ChatActivity
            r9.<init>(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r2.actionBarLayout
            r10 = 0
            r11 = 1
            r12 = 1
            r13 = 0
            boolean r0 = r8.presentFragment(r9, r10, r11, r12, r13)
            if (r0 == 0) goto L_0x11a8
            goto L_0x11a6
        L_0x11f1:
            if (r5 == 0) goto L_0x1211
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "enc_id"
            r0.putInt(r4, r5)
            org.telegram.ui.ChatActivity r9 = new org.telegram.ui.ChatActivity
            r9.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r2.actionBarLayout
            r10 = 0
            r11 = 1
            r12 = 1
            r13 = 0
            boolean r13 = r8.presentFragment(r9, r10, r11, r12, r13)
        L_0x120c:
            r0 = r46
            r4 = 0
            goto L_0x1488
        L_0x1211:
            if (r13 == 0) goto L_0x1249
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x121f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1247
        L_0x121f:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1247
        L_0x1229:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r7
            if (r0 <= 0) goto L_0x1242
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r0.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r4)
            goto L_0x1229
        L_0x1242:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.closeLastFragment(r3)
        L_0x1247:
            r0 = 0
            goto L_0x1269
        L_0x1249:
            if (r8 == 0) goto L_0x126c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1267
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r4 = new org.telegram.ui.Components.AudioPlayerAlert
            r4.<init>(r2)
            r0.showDialog(r4)
        L_0x1267:
            r0 = r46
        L_0x1269:
            r4 = 0
            goto L_0x1487
        L_0x126c:
            if (r9 == 0) goto L_0x1290
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1267
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r4 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4
            r5.<init>(r6)
            r4.<init>(r2, r5)
            r0.showDialog(r4)
            goto L_0x1267
        L_0x1290:
            java.lang.String r0 = r2.videoPath
            if (r0 != 0) goto L_0x134d
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r2.photoPathsArray
            if (r0 != 0) goto L_0x134d
            java.lang.String r0 = r2.sendingText
            if (r0 != 0) goto L_0x134d
            java.util.ArrayList<java.lang.String> r0 = r2.documentsPathsArray
            if (r0 != 0) goto L_0x134d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r2.contactsToSend
            if (r0 != 0) goto L_0x134d
            java.util.ArrayList<android.net.Uri> r0 = r2.documentsUrisArray
            if (r0 == 0) goto L_0x12aa
            goto L_0x134d
        L_0x12aa:
            if (r10 == 0) goto L_0x1312
            if (r10 != r7) goto L_0x12c7
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r4 = r2.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            java.lang.String r5 = "user_id"
            r0.putInt(r5, r4)
            org.telegram.ui.ProfileActivity r9 = new org.telegram.ui.ProfileActivity
            r9.<init>(r0)
        L_0x12c5:
            r13 = 0
            goto L_0x12ee
        L_0x12c7:
            r4 = 2
            if (r10 != r4) goto L_0x12d0
            org.telegram.ui.ThemeActivity r9 = new org.telegram.ui.ThemeActivity
            r9.<init>(r3)
            goto L_0x12c5
        L_0x12d0:
            r4 = 3
            if (r10 != r4) goto L_0x12d9
            org.telegram.ui.SessionsActivity r9 = new org.telegram.ui.SessionsActivity
            r9.<init>(r3)
            goto L_0x12c5
        L_0x12d9:
            r0 = 4
            if (r10 != r0) goto L_0x12e2
            org.telegram.ui.FiltersSetupActivity r9 = new org.telegram.ui.FiltersSetupActivity
            r9.<init>()
            goto L_0x12c5
        L_0x12e2:
            r0 = 5
            if (r10 != r0) goto L_0x12ec
            org.telegram.ui.ActionIntroActivity r9 = new org.telegram.ui.ActionIntroActivity
            r9.<init>(r4)
            r13 = 1
            goto L_0x12ee
        L_0x12ec:
            r9 = 0
            goto L_0x12c5
        L_0x12ee:
            org.telegram.ui.-$$Lambda$LaunchActivity$71xJIBEBFDq1MWef5WYBFqJqscg r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$71xJIBEBFDq1MWef5WYBFqJqscg
            r0.<init>(r9, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x130c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1347
        L_0x130c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r3)
            goto L_0x1347
        L_0x1312:
            if (r11 == 0) goto L_0x115f
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "destroyAfterSelect"
            r0.putBoolean(r4, r7)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r2.actionBarLayout
            org.telegram.ui.ContactsActivity r9 = new org.telegram.ui.ContactsActivity
            r9.<init>(r0)
            r10 = 0
            r11 = 1
            r12 = 1
            r13 = 0
            r8.presentFragment(r9, r10, r11, r12, r13)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1342
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1347
        L_0x1342:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r3)
        L_0x1347:
            r0 = r46
            r4 = 0
            r13 = 1
            goto L_0x1488
        L_0x134d:
            r4 = 3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1361
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r6)
        L_0x1361:
            int r0 = (r16 > r22 ? 1 : (r16 == r22 ? 0 : -1))
            if (r0 != 0) goto L_0x1472
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "onlySelect"
            r0.putBoolean(r5, r7)
            java.lang.String r5 = "dialogsType"
            r0.putInt(r5, r4)
            java.lang.String r4 = "allowSwitchAccount"
            r0.putBoolean(r4, r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r2.contactsToSend
            if (r4 == 0) goto L_0x13a0
            int r4 = r4.size()
            if (r4 == r7) goto L_0x13bc
            r4 = 2131626865(0x7f0e0b71, float:1.8880978E38)
            java.lang.String r5 = "SendContactToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626843(0x7f0e0b5b, float:1.8880934E38)
            java.lang.String r5 = "SendContactToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
            goto L_0x13bc
        L_0x13a0:
            r4 = 2131626865(0x7f0e0b71, float:1.8880978E38)
            java.lang.String r5 = "SendMessagesToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626864(0x7f0e0b70, float:1.8880976E38)
            java.lang.String r5 = "SendMessagesToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
        L_0x13bc:
            org.telegram.ui.DialogsActivity r9 = new org.telegram.ui.DialogsActivity
            r9.<init>(r0)
            r9.setDelegate(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x13e6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x1403
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r7
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1403
            goto L_0x1401
        L_0x13e6:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r7) goto L_0x1403
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r7
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1403
        L_0x1401:
            r13 = 1
            goto L_0x1404
        L_0x1403:
            r13 = 0
        L_0x1404:
            r10 = r13
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r2.actionBarLayout
            r11 = 1
            r12 = 1
            r13 = 0
            r8.presentFragment(r9, r10, r11, r12, r13)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x1425
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1425
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r3, r3)
            goto L_0x1454
        L_0x1425:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x143d
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x143d
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r3, r7)
            goto L_0x1454
        L_0x143d:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x1454
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1454
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r3, r7)
        L_0x1454:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x146b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x1347
        L_0x146b:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r3)
            goto L_0x1347
        L_0x1472:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r4 = java.lang.Long.valueOf(r16)
            r0.add(r4)
            r4 = 0
            r2.didSelectDialogs(r4, r0, r4, r3)
            goto L_0x1485
        L_0x1483:
            r4 = 0
            r7 = 1
        L_0x1485:
            r0 = r46
        L_0x1487:
            r13 = 0
        L_0x1488:
            if (r13 != 0) goto L_0x1526
            if (r0 != 0) goto L_0x1526
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x14d7
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x14b8
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1511
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1511
        L_0x14b8:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1511
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r3)
            goto L_0x1511
        L_0x14d7:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1511
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x14fd
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1511
        L_0x14fd:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r3)
        L_0x1511:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1526
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
        L_0x1526:
            java.lang.String r0 = r45.getAction()
            if (r0 == 0) goto L_0x153b
            java.lang.String r0 = r45.getAction()
            java.lang.String r3 = "voip"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x153b
            org.telegram.ui.VoIPFragment.show(r44)
        L_0x153b:
            r1.setAction(r4)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$5$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$32$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$7$LaunchActivity(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(iArr, sharingLocationInfo.messageObject.getDialogId()) {
            public final /* synthetic */ int[] f$0;
            public final /* synthetic */ long f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        lambda$runLinkRequest$32$LaunchActivity(locationActivity);
    }

    public /* synthetic */ void lambda$handleIntent$8$LaunchActivity(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x02a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, boolean r29, java.lang.Integer r30, java.lang.Integer r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, org.telegram.tgnet.TLRPC$TL_wallPaper r38, java.lang.String r39, int r40) {
        /*
            r21 = this;
            r15 = r21
            r3 = r22
            r0 = r23
            r5 = r24
            r6 = r25
            r9 = r28
            r14 = r33
            r13 = r34
            r12 = r35
            r11 = r38
            r10 = r39
            r1 = r40
            r2 = 2
            if (r1 != 0) goto L_0x005b
            int r4 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r4 < r2) goto L_0x005b
            if (r14 == 0) goto L_0x005b
            org.telegram.ui.-$$Lambda$LaunchActivity$HINhz6oLqCSumcTr57qtnrRtxe0 r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$HINhz6oLqCSumcTr57qtnrRtxe0
            r1 = r8
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r7 = r26
            r0 = r8
            r8 = r27
            r9 = r28
            r10 = r29
            r11 = r30
            r12 = r31
            r15 = r13
            r13 = r32
            r14 = r33
            r15 = r34
            r16 = r35
            r17 = r36
            r18 = r37
            r19 = r38
            r20 = r39
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r4 = r21
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r4, r0)
            r0.show()
            return
        L_0x005b:
            r4 = r15
            r15 = r13
            r7 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.String r8 = "OK"
            r10 = 0
            r11 = 1
            r12 = 0
            if (r36 == 0) goto L_0x00b1
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r1)
            if (r0 == 0) goto L_0x0081
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            java.lang.Object[] r2 = new java.lang.Object[r11]
            r2[r12] = r36
            r0.postNotificationName(r1, r2)
            goto L_0x00b0
        L_0x0081:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r4)
            r1 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626173(0x7f0e08bd, float:1.8879575E38)
            java.lang.Object[] r2 = new java.lang.Object[r11]
            r2[r12] = r36
            java.lang.String r3 = "OtherLoginCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setPositiveButton(r1, r10)
            r4.showAlertDialog(r0)
        L_0x00b0:
            return
        L_0x00b1:
            if (r37 == 0) goto L_0x00db
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r4)
            r1 = 2131624364(0x7f0e01ac, float:1.8875906E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r2 = "AuthAnotherClientUrl"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setPositiveButton(r1, r10)
            r4.showAlertDialog(r0)
            return
        L_0x00db:
            org.telegram.ui.ActionBar.AlertDialog r7 = new org.telegram.ui.ActionBar.AlertDialog
            r8 = 3
            r7.<init>(r4, r8)
            int[] r13 = new int[r11]
            r13[r12] = r12
            if (r0 == 0) goto L_0x010f
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r1.<init>()
            r1.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$_Uf8qTEv3gp_QMVDvZtvctrzvY0 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$_Uf8qTEv3gp_QMVDvZtvctrzvY0
            r33 = r2
            r34 = r21
            r35 = r32
            r36 = r22
            r37 = r27
            r38 = r26
            r39 = r30
            r40 = r7
            r33.<init>(r35, r36, r37, r38, r39, r40)
            int r0 = r0.sendRequest(r1, r2)
            r13[r12] = r0
            goto L_0x0346
        L_0x010f:
            if (r5 == 0) goto L_0x0142
            if (r1 != 0) goto L_0x012b
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$cP-BUXErnntbOCLASSNAMEWOXbRxYV31I r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$cP-BUXErnntbOCLASSNAMEWOXbRxYV31I
            r6.<init>(r3, r7, r5)
            int r0 = r1.sendRequest(r0, r6, r2)
            r13[r12] = r0
            goto L_0x0346
        L_0x012b:
            if (r1 != r11) goto L_0x0346
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$b0vcBpC7i4V2XHDdbrbQCKVGRv4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$b0vcBpC7i4V2XHDdbrbQCKVGRv4
            r5.<init>(r3, r7)
            r1.sendRequest(r0, r5, r2)
            goto L_0x0346
        L_0x0142:
            if (r6 == 0) goto L_0x019c
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x019b
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r11
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x0185
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r5 = 0
            org.telegram.ui.Components.ChatActivityEnterView r6 = r2.getChatActivityEnterView()
            r22 = r3
            r23 = r21
            r24 = r1
            r25 = r0
            r26 = r5
            r27 = r6
            r22.<init>(r23, r24, r25, r26, r27)
            boolean r0 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r0)
            goto L_0x0198
        L_0x0185:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r2 = 0
            r5 = 0
            r22 = r3
            r23 = r21
            r24 = r1
            r25 = r0
            r26 = r2
            r27 = r5
            r22.<init>(r23, r24, r25, r26, r27)
        L_0x0198:
            r1.showDialog(r3)
        L_0x019b:
            return
        L_0x019c:
            if (r9 == 0) goto L_0x01c1
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r11)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r8)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$nstaeeHUtCq1UCIaPqfqMBnjOrI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$nstaeeHUtCq1UCIaPqfqMBnjOrI
            r2 = r29
            r0.<init>(r2, r3, r9)
            r1.setDelegate(r0)
            r4.presentFragment(r1, r12, r11)
            goto L_0x0346
        L_0x01c1:
            if (r14 == 0) goto L_0x022c
            java.lang.String r0 = "bot_id"
            java.lang.Object r0 = r14.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 != 0) goto L_0x01d6
            return
        L_0x01d6:
            java.lang.String r1 = "payload"
            java.lang.Object r1 = r14.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = "nonce"
            java.lang.Object r2 = r14.get(r2)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r5 = "callback_url"
            java.lang.Object r5 = r14.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm r6 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm
            r6.<init>()
            r6.bot_id = r0
            java.lang.String r0 = "scope"
            java.lang.Object r0 = r14.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r6.scope = r0
            java.lang.String r0 = "public_key"
            java.lang.Object r0 = r14.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r6.public_key = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$Y8iOWH5Pksz29uJJNv8u7elefb8 r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$Y8iOWH5Pksz29uJJNv8u7elefb8
            r23 = r8
            r24 = r21
            r25 = r13
            r26 = r22
            r27 = r7
            r28 = r6
            r29 = r1
            r30 = r2
            r31 = r5
            r23.<init>(r25, r26, r27, r28, r29, r30, r31)
            int r0 = r0.sendRequest(r6, r8)
            r13[r12] = r0
            goto L_0x0346
        L_0x022c:
            r0 = r35
            if (r0 == 0) goto L_0x024a
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c
            r2.<init>(r7)
            int r0 = r0.sendRequest(r1, r2)
            r13[r12] = r0
            goto L_0x0346
        L_0x024a:
            java.lang.String r0 = "android"
            if (r15 == 0) goto L_0x026a
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r1 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r1.<init>()
            r1.lang_code = r15
            r1.lang_pack = r0
            int r0 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$7iJyRhApNsveW8Gt05Rio-vwsAg r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$7iJyRhApNsveW8Gt05Rio-vwsAg
            r2.<init>(r7)
            int r0 = r0.sendRequest(r1, r2)
            r13[r12] = r0
            goto L_0x0346
        L_0x026a:
            r1 = r38
            if (r1 == 0) goto L_0x02c5
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x029f
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x029b }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x029b }
            int r5 = r5.background_color     // Catch:{ Exception -> 0x029b }
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x029b }
            int r6 = r6.second_background_color     // Catch:{ Exception -> 0x029b }
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r1.settings     // Catch:{ Exception -> 0x029b }
            int r8 = r8.rotation     // Catch:{ Exception -> 0x029b }
            int r8 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r8, r12)     // Catch:{ Exception -> 0x029b }
            r0.<init>(r2, r5, r6, r8)     // Catch:{ Exception -> 0x029b }
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x029b }
            r2.<init>(r0, r10)     // Catch:{ Exception -> 0x029b }
            org.telegram.ui.-$$Lambda$LaunchActivity$vy243eJqlNTq76Ed9pGk-cU6I3g r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$vy243eJqlNTq76Ed9pGk-cU6I3g     // Catch:{ Exception -> 0x029b }
            r0.<init>(r2)     // Catch:{ Exception -> 0x029b }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x029b }
            goto L_0x02a0
        L_0x029b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x029f:
            r11 = 0
        L_0x02a0:
            if (r11 != 0) goto L_0x0346
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r5 = r1.slug
            r2.slug = r5
            r0.wallpaper = r2
            int r2 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$tu9YrzAVkkuCJ6HR12ZmZoIW-70 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$tu9YrzAVkkuCJ6HR12ZmZoIW-70
            r5.<init>(r7, r1)
            int r0 = r2.sendRequest(r0, r5)
            r13[r12] = r0
            goto L_0x0346
        L_0x02c5:
            r1 = r39
            if (r1 == 0) goto L_0x02f0
            org.telegram.ui.-$$Lambda$LaunchActivity$91sVITdd8BmF_plAlcaK45Yo2t8 r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$91sVITdd8BmF_plAlcaK45Yo2t8
            r10.<init>()
            org.telegram.tgnet.TLRPC$TL_account_getTheme r2 = new org.telegram.tgnet.TLRPC$TL_account_getTheme
            r2.<init>()
            r2.format = r0
            org.telegram.tgnet.TLRPC$TL_inputThemeSlug r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug
            r0.<init>()
            r0.slug = r1
            r2.theme = r0
            int r0 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$JCHSxCLASSNAMEpVs5aM4o5NcLmc0aAhE r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$JCHSxCLASSNAMEpVs5aM4o5NcLmc0aAhE
            r1.<init>(r7)
            int r0 = r0.sendRequest(r2, r1)
            r13[r12] = r0
            goto L_0x0346
        L_0x02f0:
            if (r31 == 0) goto L_0x0346
            if (r30 == 0) goto L_0x0346
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r31.intValue()
            java.lang.String r2 = "chat_id"
            r0.putInt(r2, r1)
            int r1 = r30.intValue()
            java.lang.String r2 = "message_id"
            r0.putInt(r2, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0321
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r11
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x0322
        L_0x0321:
            r1 = r10
        L_0x0322:
            if (r1 == 0) goto L_0x032e
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r22)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x0346
        L_0x032e:
            org.telegram.ui.-$$Lambda$LaunchActivity$8SJjENa-BluOeubBalyuqiaa2Mk r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$8SJjENa-BluOeubBalyuqiaa2Mk
            r23 = r2
            r24 = r21
            r25 = r0
            r26 = r31
            r27 = r13
            r28 = r7
            r29 = r1
            r30 = r22
            r23.<init>(r25, r26, r27, r28, r29, r30)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0346:
            r0 = r13[r12]
            if (r0 == 0) goto L_0x0357
            org.telegram.ui.-$$Lambda$LaunchActivity$BwVmPHsS9r3JtAebM5EKanHSsKE r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$BwVmPHsS9r3JtAebM5EKanHSsKE
            r0.<init>(r3, r13, r10)
            r7.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r7.showDelayed(r0)     // Catch:{ Exception -> 0x0357 }
        L_0x0357:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$9$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$15$LaunchActivity(String str, int i, String str2, String str3, Integer num, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, str, i, str2, str3, num, alertDialog) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ String f$6;
            public final /* synthetic */ Integer f$7;
            public final /* synthetic */ AlertDialog f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$14$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r0v13, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r0v56 */
    /* JADX WARNING: type inference failed for: r0v57 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00a3, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00c2, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a5;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0136  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$14$LaunchActivity(org.telegram.tgnet.TLObject r15, org.telegram.tgnet.TLRPC$TL_error r16, java.lang.String r17, int r18, java.lang.String r19, java.lang.String r20, java.lang.Integer r21, org.telegram.ui.ActionBar.AlertDialog r22) {
        /*
            r14 = this;
            r1 = r14
            r0 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r22
            boolean r6 = r14.isFinishing()
            if (r6 != 0) goto L_0x029d
            r6 = r15
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r6 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r6
            r7 = 1
            r8 = 0
            if (r16 != 0) goto L_0x027d
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r1.actionBarLayout
            if (r9 == 0) goto L_0x027d
            if (r0 == 0) goto L_0x0028
            if (r0 == 0) goto L_0x027d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r6.users
            boolean r9 = r9.isEmpty()
            if (r9 != 0) goto L_0x027d
        L_0x0028:
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r10 = r6.users
            r9.putUsers(r10, r8)
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r10 = r6.chats
            r9.putChats(r10, r8)
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r10 = r6.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r6.chats
            r9.putUsersAndChats(r10, r11, r8, r7)
            java.lang.String r9 = "dialogsType"
            java.lang.String r10 = "onlySelect"
            if (r0 == 0) goto L_0x013d
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putBoolean(r10, r7)
            java.lang.String r4 = "cantSendToChannels"
            r3.putBoolean(r4, r7)
            r3.putInt(r9, r7)
            r4 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            java.lang.String r9 = "SendGameToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            java.lang.String r9 = "selectAlertString"
            r3.putString(r9, r4)
            r4 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            java.lang.String r9 = "SendGameToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            java.lang.String r9 = "selectAlertStringGroup"
            r3.putString(r9, r4)
            org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
            r4.<init>(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$OYiAv66YOgtstnL0zNrjeqSNdJw r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$OYiAv66YOgtstnL0zNrjeqSNdJw
            r3.<init>(r0, r2, r6)
            r4.setDelegate(r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x00a9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x00a7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r7
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00a7
        L_0x00a5:
            r0 = 1
            goto L_0x00c5
        L_0x00a7:
            r0 = 0
            goto L_0x00c5
        L_0x00a9:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r7) goto L_0x00a7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r7
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00a7
            goto L_0x00a5
        L_0x00c5:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r1.actionBarLayout
            r3 = 1
            r6 = 1
            r9 = 0
            r15 = r2
            r16 = r4
            r17 = r0
            r18 = r3
            r19 = r6
            r20 = r9
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x00f0
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x00f0
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r8, r8)
            goto L_0x011f
        L_0x00f0:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x0108
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0108
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r8, r7)
            goto L_0x011f
        L_0x0108:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x011f
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x011f
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r8, r7)
        L_0x011f:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r8, r8)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0136
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x0292
        L_0x0136:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r8)
            goto L_0x0292
        L_0x013d:
            r0 = 0
            if (r3 == 0) goto L_0x01a9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0150
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r6.users
            java.lang.Object r0 = r0.get(r8)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
        L_0x0150:
            if (r0 == 0) goto L_0x0193
            boolean r4 = r0.bot
            if (r4 == 0) goto L_0x015b
            boolean r4 = r0.bot_nochats
            if (r4 == 0) goto L_0x015b
            goto L_0x0193
        L_0x015b:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            r4.putBoolean(r10, r7)
            r6 = 2
            r4.putInt(r9, r6)
            r9 = 2131624193(0x7f0e0101, float:1.8875559E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r0)
            r6[r8] = r10
            java.lang.String r8 = "%1$s"
            r6[r7] = r8
            java.lang.String r8 = "AddToTheGroupAlertText"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r9, r6)
            java.lang.String r8 = "addToGroupAlertString"
            r4.putString(r8, r6)
            org.telegram.ui.DialogsActivity r6 = new org.telegram.ui.DialogsActivity
            r6.<init>(r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$-h31I40SekLamy9dVWGtQhuyj8U r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$-h31I40SekLamy9dVWGtQhuyj8U
            r4.<init>(r2, r0, r3)
            r6.setDelegate(r4)
            r14.lambda$runLinkRequest$32$LaunchActivity(r6)
            goto L_0x0292
        L_0x0193:
            java.lang.String r0 = "BotCantJoinGroups"
            r2 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x01a4 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r14, r0, r8)     // Catch:{ Exception -> 0x01a4 }
            r0.show()     // Catch:{ Exception -> 0x01a4 }
            goto L_0x01a8
        L_0x01a4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a8:
            return
        L_0x01a9:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r9 = r6.chats
            boolean r9 = r9.isEmpty()
            if (r9 != 0) goto L_0x01da
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r9 = r6.chats
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC$Chat) r9
            int r9 = r9.id
            java.lang.String r10 = "chat_id"
            r3.putInt(r10, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r9 = r6.chats
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC$Chat) r9
            int r9 = r9.id
            int r9 = -r9
            long r9 = (long) r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r6.chats
            java.lang.Object r11 = r11.get(r8)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC$Chat) r11
            goto L_0x01f5
        L_0x01da:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r6.users
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC$User) r9
            int r9 = r9.id
            java.lang.String r10 = "user_id"
            r3.putInt(r10, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r6.users
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC$User) r9
            int r9 = r9.id
            long r9 = (long) r9
            r11 = r0
        L_0x01f5:
            if (r4 == 0) goto L_0x0212
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r6.users
            int r12 = r12.size()
            if (r12 <= 0) goto L_0x0212
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r6.users
            java.lang.Object r6 = r6.get(r8)
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC$User) r6
            boolean r6 = r6.bot
            if (r6 == 0) goto L_0x0212
            java.lang.String r6 = "botUser"
            r3.putString(r6, r4)
            r6 = 1
            goto L_0x0213
        L_0x0212:
            r6 = 0
        L_0x0213:
            if (r21 == 0) goto L_0x021e
            int r12 = r21.intValue()
            java.lang.String r13 = "message_id"
            r3.putInt(r13, r12)
        L_0x021e:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r12 = mainFragmentsStack
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x0233
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r12 = r0.size()
            int r12 = r12 - r7
            java.lang.Object r0 = r0.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
        L_0x0233:
            if (r0 == 0) goto L_0x023f
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r12 = r12.checkCanOpenChat(r3, r0)
            if (r12 == 0) goto L_0x0292
        L_0x023f:
            if (r6 == 0) goto L_0x0253
            boolean r6 = r0 instanceof org.telegram.ui.ChatActivity
            if (r6 == 0) goto L_0x0253
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r12 = r0.getDialogId()
            int r6 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x0253
            r0.setBotUser(r4)
            goto L_0x0292
        L_0x0253:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r21 != 0) goto L_0x025f
            r4 = 0
            goto L_0x0263
        L_0x025f:
            int r4 = r21.intValue()
        L_0x0263:
            org.telegram.ui.-$$Lambda$LaunchActivity$ngS0I82WewctrSS8-A4FFh1cbb0 r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$ngS0I82WewctrSS8-A4FFh1cbb0
            r6.<init>(r5, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$GuLWg4p-FVRngnW5Wva8kxfJx5I r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$GuLWg4p-FVRngnW5Wva8kxfJx5I
            r3.<init>(r5)
            r15 = r0
            r16 = r9
            r18 = r2
            r19 = r4
            r20 = r6
            r21 = r3
            r15.ensureMessagesLoaded(r16, r18, r19, r20, r21)
            r7 = 0
            goto L_0x0292
        L_0x027d:
            java.lang.String r0 = "NoUsernameFound"
            r2 = 2131625978(0x7f0e07fa, float:1.887918E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x028e }
            android.widget.Toast r0 = android.widget.Toast.makeText(r14, r0, r8)     // Catch:{ Exception -> 0x028e }
            r0.show()     // Catch:{ Exception -> 0x028e }
            goto L_0x0292
        L_0x028e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0292:
            if (r7 == 0) goto L_0x029d
            r22.dismiss()     // Catch:{ Exception -> 0x0298 }
            goto L_0x029d
        L_0x0298:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x029d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$14$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$10$LaunchActivity(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        TLRPC$TL_inputMediaGame tLRPC$TL_inputMediaGame = new TLRPC$TL_inputMediaGame();
        TLRPC$TL_inputGameShortName tLRPC$TL_inputGameShortName = new TLRPC$TL_inputGameShortName();
        tLRPC$TL_inputMediaGame.id = tLRPC$TL_inputGameShortName;
        tLRPC$TL_inputGameShortName.short_name = str;
        tLRPC$TL_inputGameShortName.bot_id = MessagesController.getInstance(i).getInputUser(tLRPC$TL_contacts_resolvedPeer.users.get(0));
        int i2 = (int) longValue;
        SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer(i2), tLRPC$TL_inputMediaGame, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        int i3 = (int) (longValue >> 32);
        if (i2 == 0) {
            bundle.putInt("enc_id", i3);
        } else if (i2 > 0) {
            bundle.putInt("user_id", i2);
        } else if (i2 < 0) {
            bundle.putInt("chat_id", -i2);
        }
        DialogsActivity dialogsActivity2 = dialogsActivity;
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$null$11$LaunchActivity(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        int i2 = -((int) longValue);
        bundle.putInt("chat_id", i2);
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
            if (!instance.checkCanOpenChat(bundle, arrayList2.get(arrayList2.size() - 1))) {
                return;
            }
        }
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(i2, tLRPC$User, (TLRPC$ChatFull) null, 0, str, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
    }

    public /* synthetic */ void lambda$null$12$LaunchActivity(AlertDialog alertDialog, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!isFinishing()) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$null$13$LaunchActivity(AlertDialog alertDialog) {
        if (!isFinishing()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            AlertsCreator.showSimpleAlert(arrayList.get(arrayList.size() - 1), LocaleController.getString("JoinToGroupErrorNotExist", NUM));
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$20$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, i, alertDialog, str) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ AlertDialog f$4;
            public final /* synthetic */ String f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$19$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek) != false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x007a, code lost:
        if (r0.checkCanOpenChat(r5, r1.get(r1.size() - 1)) != false) goto L_0x007c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.ui.ActionBar.AlertDialog r21, java.lang.String r22) {
        /*
            r17 = this;
            r7 = r17
            r0 = r18
            r8 = r21
            boolean r1 = r17.isFinishing()
            if (r1 != 0) goto L_0x0111
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x00c3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            if (r3 == 0) goto L_0x00c3
            r6 = r19
            org.telegram.tgnet.TLRPC$ChatInvite r6 = (org.telegram.tgnet.TLRPC$ChatInvite) r6
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            if (r0 == 0) goto L_0x00ab
            boolean r0 = org.telegram.messenger.ChatObject.isLeftFromChat(r0)
            if (r0 == 0) goto L_0x0034
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            boolean r3 = r0.kicked
            if (r3 != 0) goto L_0x00ab
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0034
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek
            if (r0 == 0) goto L_0x00ab
        L_0x0034:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r3 = r6.chat
            r9 = 0
            r0.putChat(r3, r9)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            org.telegram.tgnet.TLRPC$Chat r3 = r6.chat
            r0.add(r3)
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r20)
            r3.putUsersAndChats(r1, r0, r9, r2)
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            int r0 = r0.id
            java.lang.String r1 = "chat_id"
            r5.putInt(r1, r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x007c
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r20)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r0 = r0.checkCanOpenChat(r5, r1)
            if (r0 == 0) goto L_0x0106
        L_0x007c:
            boolean[] r4 = new boolean[r2]
            org.telegram.ui.-$$Lambda$LaunchActivity$_HOGKHkfKnPkfgwvch-IiBcFdxs r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$_HOGKHkfKnPkfgwvch-IiBcFdxs
            r0.<init>(r4)
            r8.setOnCancelListener(r0)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            int r1 = r0.id
            int r1 = -r1
            long r11 = (long) r1
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r0)
            r14 = 0
            org.telegram.ui.-$$Lambda$LaunchActivity$sVRxg04gFjkEWcOgqBRLmz5I0Bo r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$sVRxg04gFjkEWcOgqBRLmz5I0Bo
            r1 = r15
            r2 = r17
            r3 = r21
            r1.<init>(r3, r4, r5, r6)
            org.telegram.ui.-$$Lambda$LaunchActivity$v6xoCAnTC-x8DFigK4rhIfwSDtI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$v6xoCAnTC-x8DFigK4rhIfwSDtI
            r0.<init>(r8)
            r16 = r0
            r10.ensureMessagesLoaded(r11, r13, r14, r15, r16)
            r2 = 0
            goto L_0x0106
        L_0x00ab:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r2
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.JoinGroupAlert r1 = new org.telegram.ui.Components.JoinGroupAlert
            r3 = r22
            r1.<init>(r7, r6, r3, r0)
            r0.showDialog(r1)
            goto L_0x0106
        L_0x00c3:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r7)
            r4 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r5 = "AppName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            java.lang.String r0 = r0.text
            java.lang.String r4 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r4)
            if (r0 == 0) goto L_0x00eb
            r0 = 2131625378(0x7f0e05a2, float:1.8877962E38)
            java.lang.String r4 = "FloodWait"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
            goto L_0x00f7
        L_0x00eb:
            r0 = 2131625627(0x7f0e069b, float:1.8878467E38)
            java.lang.String r4 = "JoinToGroupErrorNotExist"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
        L_0x00f7:
            r0 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.String r4 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setPositiveButton(r0, r1)
            r7.showAlertDialog(r3)
        L_0x0106:
            if (r2 == 0) goto L_0x0111
            r21.dismiss()     // Catch:{ Exception -> 0x010c }
            goto L_0x0111
        L_0x010c:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0111:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$16(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public /* synthetic */ void lambda$null$17$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle, TLRPC$ChatInvite tLRPC$ChatInvite) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!zArr[0]) {
            ChatActivity chatActivity = new ChatActivity(bundle);
            if (tLRPC$ChatInvite instanceof TLRPC$TL_chatInvitePeek) {
                chatActivity.setChatInvite(tLRPC$ChatInvite);
            }
            this.actionBarLayout.presentFragment(chatActivity);
        }
    }

    public /* synthetic */ void lambda$null$18$LaunchActivity(AlertDialog alertDialog) {
        if (!isFinishing()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            AlertsCreator.showSimpleAlert(arrayList.get(arrayList.size() - 1), LocaleController.getString("JoinToGroupErrorNotExist", NUM));
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error, tLObject, i) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$21$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (!isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (tLRPC$TL_error != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", NUM));
                } else if (tLRPC$TL_error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                }
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
                if (!tLRPC$Updates.chats.isEmpty()) {
                    TLRPC$Chat tLRPC$Chat = tLRPC$Updates.chats.get(0);
                    tLRPC$Chat.left = false;
                    tLRPC$Chat.kicked = false;
                    MessagesController.getInstance(i).putUsers(tLRPC$Updates.users, false);
                    MessagesController.getInstance(i).putChats(tLRPC$Updates.chats, false);
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", tLRPC$Chat.id);
                    if (!mainFragmentsStack.isEmpty()) {
                        MessagesController instance = MessagesController.getInstance(i);
                        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                        if (!instance.checkCanOpenChat(bundle, arrayList.get(arrayList.size() - 1))) {
                            return;
                        }
                    }
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    this.actionBarLayout.presentFragment(chatActivity, false, true, true, false);
                }
            }
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$23$LaunchActivity(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
        ArrayList arrayList2 = arrayList;
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        boolean z3 = z;
        bundle.putBoolean("hasUrl", z);
        int i2 = (int) longValue;
        int i3 = (int) (longValue >> 32);
        if (i2 == 0) {
            bundle.putInt("enc_id", i3);
        } else if (i2 > 0) {
            bundle.putInt("user_id", i2);
        } else if (i2 < 0) {
            bundle.putInt("chat_id", -i2);
        }
        DialogsActivity dialogsActivity2 = dialogsActivity;
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            MediaDataController.getInstance(i).saveDraft(longValue, str, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$Message) null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate(alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3) {
                public final /* synthetic */ AlertDialog f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ TLRPC$TL_account_authorizationForm f$3;
                public final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$4;
                public final /* synthetic */ String f$5;
                public final /* synthetic */ String f$6;
                public final /* synthetic */ String f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LaunchActivity.this.lambda$null$25$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$26$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$LaunchActivity(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ TLRPC$TL_account_authorizationForm f$4;
            public final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$5;
            public final /* synthetic */ String f$6;
            public final /* synthetic */ String f$7;
            public final /* synthetic */ String f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$24$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$32$LaunchActivity(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$28$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$28$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject instanceof TLRPC$TL_help_deepLinkInfo) {
            TLRPC$TL_help_deepLinkInfo tLRPC$TL_help_deepLinkInfo = (TLRPC$TL_help_deepLinkInfo) tLObject;
            AlertsCreator.showUpdateAppAlert(this, tLRPC$TL_help_deepLinkInfo.message, tLRPC$TL_help_deepLinkInfo.update_app);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_error) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$30$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$30$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject instanceof TLRPC$TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TLRPC$TL_langPackLanguage) tLObject));
        } else if (tLRPC$TL_error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(tLRPC$TL_error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", NUM)));
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$34$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_wallPaper f$3;
            public final /* synthetic */ TLRPC$TL_error f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$33$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$33$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC$TL_wallPaper r11, org.telegram.tgnet.TLRPC$TL_error r12) {
        /*
            r8 = this;
            r9.dismiss()     // Catch:{ Exception -> 0x0004 }
            goto L_0x0008
        L_0x0004:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0008:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r9 == 0) goto L_0x0049
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r10
            boolean r9 = r10.pattern
            if (r9 == 0) goto L_0x0036
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r9 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r10.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r11.settings
            int r2 = r12.background_color
            int r3 = r12.second_background_color
            int r12 = r12.rotation
            r0 = 0
            int r4 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r12, r0)
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r11.settings
            int r0 = r12.intensity
            float r0 = (float) r0
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r5 = r0 / r5
            boolean r6 = r12.motion
            r7 = 0
            r0 = r9
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r9.pattern = r10
            r10 = r9
        L_0x0036:
            org.telegram.ui.ThemePreviewActivity r9 = new org.telegram.ui.ThemePreviewActivity
            r12 = 0
            r9.<init>(r10, r12)
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r11.settings
            boolean r11 = r10.blur
            boolean r10 = r10.motion
            r9.setInitialModes(r11, r10)
            r8.lambda$runLinkRequest$32$LaunchActivity(r9)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625164(0x7f0e04cc, float:1.8877528E38)
            java.lang.String r11 = "ErrorOccurred"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.append(r10)
            java.lang.String r10 = "\n"
            r9.append(r10)
            java.lang.String r10 = r12.text
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            org.telegram.ui.ActionBar.AlertDialog$Builder r9 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r8, r9)
            r8.showAlertDialog(r9)
        L_0x006f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$33$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$35$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$37$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, alertDialog, tLRPC$TL_error) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ AlertDialog f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$36$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$36$LaunchActivity(org.telegram.tgnet.TLObject r5, org.telegram.ui.ActionBar.AlertDialog r6, org.telegram.tgnet.TLRPC$TL_error r7) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_theme
            r1 = 1
            if (r0 == 0) goto L_0x0075
            org.telegram.tgnet.TLRPC$TL_theme r5 = (org.telegram.tgnet.TLRPC$TL_theme) r5
            org.telegram.tgnet.TLRPC$TL_themeSettings r7 = r5.settings
            r0 = 0
            if (r7 == 0) goto L_0x0057
            java.lang.String r7 = org.telegram.ui.ActionBar.Theme.getBaseThemeKey(r7)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            if (r7 == 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$TL_themeSettings r2 = r5.settings
            org.telegram.tgnet.TLRPC$WallPaper r2 = r2.wallpaper
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r3 == 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r2
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r1)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x004b
            r4.loadingThemeProgressDialog = r6
            r4.loadingThemeAccent = r1
            r4.loadingThemeInfo = r7
            r4.loadingTheme = r5
            r4.loadingThemeWallpaper = r2
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getAttachFileName(r5)
            r4.loadingThemeWallpaperName = r5
            int r5 = r4.currentAccount
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r5)
            org.telegram.tgnet.TLRPC$Document r6 = r2.document
            r5.loadFile(r6, r2, r1, r1)
            return
        L_0x004a:
            r2 = 0
        L_0x004b:
            r6.dismiss()     // Catch:{ Exception -> 0x004f }
            goto L_0x0053
        L_0x004f:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0053:
            r4.openThemeAccentPreview(r5, r2, r7)
            goto L_0x0084
        L_0x0057:
            org.telegram.tgnet.TLRPC$Document r7 = r5.document
            if (r7 == 0) goto L_0x0081
            r4.loadingThemeAccent = r0
            r4.loadingTheme = r5
            java.lang.String r7 = org.telegram.messenger.FileLoader.getAttachFileName(r7)
            r4.loadingThemeFileName = r7
            r4.loadingThemeProgressDialog = r6
            int r7 = r4.currentAccount
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)
            org.telegram.tgnet.TLRPC$TL_theme r2 = r4.loadingTheme
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r7.loadFile(r2, r5, r1, r1)
            goto L_0x0084
        L_0x0075:
            if (r7 == 0) goto L_0x0083
            java.lang.String r5 = r7.text
            java.lang.String r7 = "THEME_FORMAT_INVALID"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x0083
        L_0x0081:
            r0 = 1
            goto L_0x0084
        L_0x0083:
            r0 = 2
        L_0x0084:
            if (r0 == 0) goto L_0x00be
            r6.dismiss()     // Catch:{ Exception -> 0x008a }
            goto L_0x008e
        L_0x008a:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x008e:
            r5 = 2131627136(0x7f0e0CLASSNAME, float:1.8881528E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627156(0x7f0e0CLASSNAME, float:1.8881568E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627155(0x7f0e0CLASSNAME, float:1.8881566E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$36$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$40$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = num.intValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new RequestDelegate(alertDialog, baseFragment, i, bundle) {
                public final /* synthetic */ AlertDialog f$1;
                public final /* synthetic */ BaseFragment f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ Bundle f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LaunchActivity.this.lambda$null$39$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$39$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, baseFragment, i, bundle) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ BaseFragment f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ Bundle f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$38$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$38$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        boolean z = true;
        if (tLObject instanceof TLRPC$TL_messages_chats) {
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            if (!tLRPC$TL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_messages_chats.chats, false);
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_chats.chats.get(0);
                if (baseFragment == null || MessagesController.getInstance(i).checkCanOpenChat(bundle, baseFragment)) {
                    this.actionBarLayout.presentFragment(new ChatActivity(bundle));
                }
                z = false;
            }
        }
        if (z) {
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LinkNotFound", NUM)));
        }
    }

    static /* synthetic */ void lambda$runLinkRequest$41(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void checkAppUpdate(boolean z) {
        if (!z && BuildVars.DEBUG_VERSION) {
            return;
        }
        if (!z && !BuildVars.CHECK_UPDATES) {
            return;
        }
        if (z || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(0).lastUpdateCheckTime) >= 86400000) {
            TLRPC$TL_help_getAppUpdate tLRPC$TL_help_getAppUpdate = new TLRPC$TL_help_getAppUpdate();
            try {
                tLRPC$TL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tLRPC$TL_help_getAppUpdate.source == null) {
                tLRPC$TL_help_getAppUpdate.source = "";
            }
            int i = this.currentAccount;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new RequestDelegate(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LaunchActivity.this.lambda$checkAppUpdate$43$LaunchActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$43$LaunchActivity(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_help_appUpdate) tLObject, i) {
                public final /* synthetic */ TLRPC$TL_help_appUpdate f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LaunchActivity.this.lambda$null$42$LaunchActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$42$LaunchActivity(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
        if (tLRPC$TL_help_appUpdate.can_not_skip) {
            UserConfig.getInstance(0).pendingAppUpdate = tLRPC$TL_help_appUpdate;
            UserConfig.getInstance(0).pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = 0;
            }
            UserConfig.getInstance(0).saveConfig(false);
            showUpdateActivity(i, tLRPC$TL_help_appUpdate, false);
            return;
        }
        new UpdateAppAlertDialog(this, tLRPC$TL_help_appUpdate, i).show();
    }

    public AlertDialog showAlertDialog(AlertDialog.Builder builder) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            AlertDialog show = builder.show();
            this.visibleDialog = show;
            show.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.lambda$showAlertDialog$44$LaunchActivity(dialogInterface);
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$44$LaunchActivity(DialogInterface dialogInterface) {
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            if (alertDialog == this.localeDialog) {
                try {
                    Toast.makeText(this, getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? this.englishLocaleStrings : this.systemLocaleStrings, "ChangeLanguageLater", NUM), 1).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.localeDialog = null;
            } else if (alertDialog == this.proxyErrorDialog) {
                MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putBoolean("proxy_enabled", false);
                edit.putBoolean("proxy_enabled_calls", false);
                edit.commit();
                ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                this.proxyErrorDialog = null;
            }
        }
        this.visibleDialog = null;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x01d9  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01e3  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01ec  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x022d A[LOOP:2: B:127:0x0225->B:129:0x022d, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0250  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x025d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x018d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didSelectDialogs(org.telegram.ui.DialogsActivity r27, java.util.ArrayList<java.lang.Long> r28, java.lang.CharSequence r29, boolean r30) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r0.contactsToSend
            r4 = 0
            if (r3 == 0) goto L_0x0011
            int r3 = r3.size()
            int r3 = r3 + r4
            goto L_0x0012
        L_0x0011:
            r3 = 0
        L_0x0012:
            java.lang.String r5 = r0.videoPath
            if (r5 == 0) goto L_0x0018
            int r3 = r3 + 1
        L_0x0018:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r5 = r0.photoPathsArray
            if (r5 == 0) goto L_0x0021
            int r5 = r5.size()
            int r3 = r3 + r5
        L_0x0021:
            java.util.ArrayList<java.lang.String> r5 = r0.documentsPathsArray
            if (r5 == 0) goto L_0x002a
            int r5 = r5.size()
            int r3 = r3 + r5
        L_0x002a:
            java.util.ArrayList<android.net.Uri> r5 = r0.documentsUrisArray
            if (r5 == 0) goto L_0x0033
            int r5 = r5.size()
            int r3 = r3 + r5
        L_0x0033:
            java.lang.String r5 = r0.videoPath
            if (r5 != 0) goto L_0x0049
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r5 = r0.photoPathsArray
            if (r5 != 0) goto L_0x0049
            java.util.ArrayList<java.lang.String> r5 = r0.documentsPathsArray
            if (r5 != 0) goto L_0x0049
            java.util.ArrayList<android.net.Uri> r5 = r0.documentsUrisArray
            if (r5 != 0) goto L_0x0049
            java.lang.String r5 = r0.sendingText
            if (r5 == 0) goto L_0x0049
            int r3 = r3 + 1
        L_0x0049:
            r5 = 0
        L_0x004a:
            int r6 = r28.size()
            r7 = 1
            if (r5 >= r6) goto L_0x006b
            java.lang.Object r6 = r2.get(r5)
            java.lang.Long r6 = (java.lang.Long) r6
            long r8 = r6.longValue()
            int r6 = r0.currentAccount
            if (r3 <= r7) goto L_0x0060
            goto L_0x0061
        L_0x0060:
            r7 = 0
        L_0x0061:
            boolean r6 = org.telegram.ui.Components.AlertsCreator.checkSlowMode(r0, r6, r8, r7)
            if (r6 == 0) goto L_0x0068
            return
        L_0x0068:
            int r5 = r5 + 1
            goto L_0x004a
        L_0x006b:
            if (r1 == 0) goto L_0x0072
            int r3 = r27.getCurrentAccount()
            goto L_0x0074
        L_0x0072:
            int r3 = r0.currentAccount
        L_0x0074:
            int r5 = r28.size()
            r6 = 0
            if (r5 > r7) goto L_0x00ce
            java.lang.Object r5 = r2.get(r4)
            java.lang.Long r5 = (java.lang.Long) r5
            long r8 = r5.longValue()
            int r5 = (int) r8
            r10 = 32
            long r8 = r8 >> r10
            int r9 = (int) r8
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r10 = "scrollToTopOnResume"
            r8.putBoolean(r10, r7)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 != 0) goto L_0x00a5
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getInstance(r3)
            int r11 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r12 = new java.lang.Object[r4]
            r10.postNotificationName(r11, r12)
        L_0x00a5:
            if (r5 == 0) goto L_0x00b8
            if (r5 <= 0) goto L_0x00af
            java.lang.String r9 = "user_id"
            r8.putInt(r9, r5)
            goto L_0x00bd
        L_0x00af:
            if (r5 >= 0) goto L_0x00bd
            int r5 = -r5
            java.lang.String r9 = "chat_id"
            r8.putInt(r9, r5)
            goto L_0x00bd
        L_0x00b8:
            java.lang.String r5 = "enc_id"
            r8.putInt(r5, r9)
        L_0x00bd:
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r3)
            boolean r5 = r5.checkCanOpenChat(r8, r1)
            if (r5 != 0) goto L_0x00c8
            return
        L_0x00c8:
            org.telegram.ui.ChatActivity r5 = new org.telegram.ui.ChatActivity
            r5.<init>(r8)
            goto L_0x00cf
        L_0x00ce:
            r5 = r6
        L_0x00cf:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r0.contactsToSend
            if (r8 == 0) goto L_0x0117
            int r8 = r8.size()
            if (r8 != r7) goto L_0x0117
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = mainFragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0117
            org.telegram.ui.Components.PhonebookShareAlert r4 = new org.telegram.ui.Components.PhonebookShareAlert
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = mainFragmentsStack
            int r9 = r8.size()
            int r9 = r9 - r7
            java.lang.Object r8 = r8.get(r9)
            r10 = r8
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            r11 = 0
            r12 = 0
            android.net.Uri r13 = r0.contactsToSendUri
            r14 = 0
            r15 = 0
            r16 = 0
            r9 = r4
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)
            org.telegram.ui.-$$Lambda$LaunchActivity$OJIrGRrOjZm8xRgb_b4Exf2Iyro r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$OJIrGRrOjZm8xRgb_b4Exf2Iyro
            r8.<init>(r5, r2, r3)
            r4.setDelegate(r8)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r7
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r2.showDialog(r4)
            goto L_0x0262
        L_0x0117:
            r8 = 0
        L_0x0118:
            int r9 = r28.size()
            if (r8 >= r9) goto L_0x0262
            java.lang.Object r9 = r2.get(r8)
            java.lang.Long r9 = (java.lang.Long) r9
            long r23 = r9.longValue()
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r25 = org.telegram.messenger.AccountInstance.getInstance(r9)
            r15 = 1024(0x400, float:1.435E-42)
            if (r5 == 0) goto L_0x0150
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r0.actionBarLayout
            if (r1 == 0) goto L_0x0138
            r11 = 1
            goto L_0x0139
        L_0x0138:
            r11 = 0
        L_0x0139:
            if (r1 != 0) goto L_0x013d
            r12 = 1
            goto L_0x013e
        L_0x013d:
            r12 = 0
        L_0x013e:
            r13 = 1
            r14 = 0
            r10 = r5
            r9.presentFragment(r10, r11, r12, r13, r14)
            java.lang.String r9 = r0.videoPath
            if (r9 == 0) goto L_0x0187
            java.lang.String r10 = r0.sendingText
            r5.openVideoEditor(r9, r10)
            r0.sendingText = r6
            goto L_0x0187
        L_0x0150:
            java.lang.String r9 = r0.videoPath
            if (r9 == 0) goto L_0x0187
            java.lang.String r9 = r0.sendingText
            if (r9 == 0) goto L_0x0164
            int r9 = r9.length()
            if (r9 > r15) goto L_0x0164
            java.lang.String r9 = r0.sendingText
            r0.sendingText = r6
            r14 = r9
            goto L_0x0165
        L_0x0164:
            r14 = r6
        L_0x0165:
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            java.lang.String r9 = r0.videoPath
            r12.add(r9)
            r13 = 0
            r9 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 0
            r10 = r25
            r11 = r12
            r6 = 1024(0x400, float:1.435E-42)
            r15 = r9
            r16 = r23
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r10, r11, r12, r13, r14, r15, r16, r18, r19, r20, r21, r22)
            goto L_0x0189
        L_0x0187:
            r6 = 1024(0x400, float:1.435E-42)
        L_0x0189:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            if (r9 == 0) goto L_0x01c3
            java.lang.String r9 = r0.sendingText
            if (r9 == 0) goto L_0x01ae
            int r9 = r9.length()
            if (r9 > r6) goto L_0x01ae
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            int r9 = r9.size()
            if (r9 != r7) goto L_0x01ae
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            java.lang.Object r9 = r9.get(r4)
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r9 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r9
            java.lang.String r10 = r0.sendingText
            r9.caption = r10
            r9 = 0
            r0.sendingText = r9
        L_0x01ae:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r11 = r0.photoPathsArray
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 1
            r20 = 0
            r10 = r25
            r12 = r23
            org.telegram.messenger.SendMessagesHelper.prepareSendingMedia(r10, r11, r12, r14, r15, r16, r17, r18, r19, r20)
        L_0x01c3:
            java.util.ArrayList<java.lang.String> r9 = r0.documentsPathsArray
            if (r9 != 0) goto L_0x01cb
            java.util.ArrayList<android.net.Uri> r9 = r0.documentsUrisArray
            if (r9 == 0) goto L_0x020d
        L_0x01cb:
            java.lang.String r9 = r0.sendingText
            if (r9 == 0) goto L_0x01f3
            int r9 = r9.length()
            if (r9 > r6) goto L_0x01f3
            java.util.ArrayList<java.lang.String> r6 = r0.documentsPathsArray
            if (r6 == 0) goto L_0x01de
            int r6 = r6.size()
            goto L_0x01df
        L_0x01de:
            r6 = 0
        L_0x01df:
            java.util.ArrayList<android.net.Uri> r9 = r0.documentsUrisArray
            if (r9 == 0) goto L_0x01e8
            int r9 = r9.size()
            goto L_0x01e9
        L_0x01e8:
            r9 = 0
        L_0x01e9:
            int r6 = r6 + r9
            if (r6 != r7) goto L_0x01f3
            java.lang.String r6 = r0.sendingText
            r9 = 0
            r0.sendingText = r9
            r14 = r6
            goto L_0x01f4
        L_0x01f3:
            r14 = 0
        L_0x01f4:
            java.util.ArrayList<java.lang.String> r11 = r0.documentsPathsArray
            java.util.ArrayList<java.lang.String> r12 = r0.documentsOriginalPathsArray
            java.util.ArrayList<android.net.Uri> r13 = r0.documentsUrisArray
            java.lang.String r15 = r0.documentsMimeType
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 0
            r10 = r25
            r16 = r23
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r10, r11, r12, r13, r14, r15, r16, r18, r19, r20, r21, r22)
        L_0x020d:
            java.lang.String r11 = r0.sendingText
            if (r11 == 0) goto L_0x021a
            r14 = 1
            r15 = 0
            r10 = r25
            r12 = r23
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r10, r11, r12, r14, r15)
        L_0x021a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r0.contactsToSend
            if (r6 == 0) goto L_0x024a
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x024a
            r6 = 0
        L_0x0225:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r0.contactsToSend
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x024a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r0.contactsToSend
            java.lang.Object r9 = r9.get(r6)
            r11 = r9
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            org.telegram.messenger.SendMessagesHelper r10 = org.telegram.messenger.SendMessagesHelper.getInstance(r3)
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1
            r18 = 0
            r12 = r23
            r10.sendMessage((org.telegram.tgnet.TLRPC$User) r11, (long) r12, (org.telegram.messenger.MessageObject) r14, (org.telegram.tgnet.TLRPC$ReplyMarkup) r15, (java.util.HashMap<java.lang.String, java.lang.String>) r16, (boolean) r17, (int) r18)
            int r6 = r6 + 1
            goto L_0x0225
        L_0x024a:
            boolean r6 = android.text.TextUtils.isEmpty(r29)
            if (r6 != 0) goto L_0x025d
            java.lang.String r11 = r29.toString()
            r14 = 1
            r15 = 0
            r10 = r25
            r12 = r23
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r10, r11, r12, r14, r15)
        L_0x025d:
            int r8 = r8 + 1
            r6 = 0
            goto L_0x0118
        L_0x0262:
            if (r1 == 0) goto L_0x0269
            if (r5 != 0) goto L_0x0269
            r27.finishFragment()
        L_0x0269:
            r1 = 0
            r0.photoPathsArray = r1
            r0.videoPath = r1
            r0.sendingText = r1
            r0.documentsPathsArray = r1
            r0.documentsOriginalPathsArray = r1
            r0.contactsToSend = r1
            r0.contactsToSendUri = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didSelectDialogs(org.telegram.ui.DialogsActivity, java.util.ArrayList, java.lang.CharSequence, boolean):void");
    }

    public /* synthetic */ void lambda$didSelectDialogs$45$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$User, ((Long) arrayList.get(i3)).longValue(), (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
        }
    }

    private void onFinish() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (!this.finished) {
            this.finished = true;
            int i = this.currentAccount;
            if (i != -1) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needCheckSystemBarColors);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$32$LaunchActivity(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true, false);
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

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onActivityResult");
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        super.onActivityResult(i, i2, intent);
        if (i == 140) {
            LocationController instance = LocationController.getInstance(this.currentAccount);
            if (i2 == -1) {
                z = true;
            }
            instance.startFusedLocationRequest(z);
            return;
        }
        ThemeEditorView instance2 = ThemeEditorView.getInstance();
        if (instance2 != null) {
            instance2.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ArrayList<BaseFragment> arrayList = this.actionBarLayout.fragmentsStack;
            arrayList.get(arrayList.size() - 1).onActivityResultFragment(i, i2, intent);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ArrayList<BaseFragment> arrayList2 = this.rightActionBarLayout.fragmentsStack;
                arrayList2.get(arrayList2.size() - 1).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ArrayList<BaseFragment> arrayList3 = this.layersActionBarLayout.fragmentsStack;
                arrayList3.get(arrayList3.size() - 1).onActivityResultFragment(i, i2, intent);
            }
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr == null) {
            iArr = new int[0];
        }
        if (strArr == null) {
            strArr = new String[0];
        }
        boolean z = iArr.length > 0 && iArr[0] == 0;
        if (i == 4) {
            if (!z) {
                showPermissionErrorAlert(LocaleController.getString("PermissionStorage", NUM));
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (i == 5) {
            if (!z) {
                showPermissionErrorAlert(LocaleController.getString("PermissionContacts", NUM));
                return;
            }
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
        } else if (i == 3) {
            int min = Math.min(strArr.length, iArr.length);
            boolean z2 = true;
            boolean z3 = true;
            for (int i2 = 0; i2 < min; i2++) {
                if ("android.permission.RECORD_AUDIO".equals(strArr[i2])) {
                    z2 = iArr[i2] == 0;
                } else if ("android.permission.CAMERA".equals(strArr[i2])) {
                    z3 = iArr[i2] == 0;
                }
            }
            if (!z2) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoAudio", NUM));
            } else if (!z3) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", NUM));
            } else if (SharedConfig.inappCamera) {
                CameraController.getInstance().initCamera((Runnable) null);
                return;
            } else {
                return;
            }
        } else if (i == 18 || i == 19 || i == 20 || i == 22) {
            if (!z) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", NUM));
            }
        } else if (i == 2 && z) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ArrayList<BaseFragment> arrayList = this.actionBarLayout.fragmentsStack;
            arrayList.get(arrayList.size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ArrayList<BaseFragment> arrayList2 = this.rightActionBarLayout.fragmentsStack;
                arrayList2.get(arrayList2.size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ArrayList<BaseFragment> arrayList3 = this.layersActionBarLayout.fragmentsStack;
                arrayList3.get(arrayList3.size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
        }
        VoIPFragment.onRequestPermissionsResult(i, strArr, iArr);
    }

    private void showPermissionErrorAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$showPermissionErrorAlert$46$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$46$LaunchActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$y5_tfrAMtFFLutJqWoFMvar_cgY.INSTANCE);
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 != null) {
            passcodeView2.onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onPause();
        }
    }

    static /* synthetic */ void lambda$onPause$47() {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
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
        if (ContentPreviewViewer.hasInstance()) {
            ContentPreviewViewer.getInstance().destroy();
        }
        PipRoundVideoView instance = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, false);
        if (instance != null) {
            instance.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet instance2 = EmbedBottomSheet.getInstance();
        if (instance2 != null) {
            instance2.destroy();
        }
        ThemeEditorView instance3 = ThemeEditorView.getInstance();
        if (instance3 != null) {
            instance3.destroy();
        }
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        super.onDestroy();
        onFinish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MessageObject playingMessageObject;
        super.onResume();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$Dhs5NjrIwUgtS8tj3dMdC4kmzMQ.INSTANCE);
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
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        updateCurrentConnectionState(this.currentAccount);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
        if (!(PipRoundVideoView.getInstance() == null || !MediaController.getInstance().isMessagePaused() || (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) == null)) {
            MediaController.getInstance().seekToProgress(playingMessageObject, playingMessageObject.audioProgress);
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            int i = UserConfig.selectedAccount;
            showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
        } else if (UserConfig.getInstance(0).pendingAppUpdate != null) {
            showUpdateActivity(UserConfig.selectedAccount, UserConfig.getInstance(0).pendingAppUpdate, true);
        }
        checkAppUpdate(false);
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.getInstance();
            VoIPFragment.onResume();
        }
    }

    static /* synthetic */ void lambda$onResume$48() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
        checkLayout();
        PipRoundVideoView instance = PipRoundVideoView.getInstance();
        if (instance != null) {
            instance.onConfigurationChanged();
        }
        EmbedBottomSheet instance2 = EmbedBottomSheet.getInstance();
        if (instance2 != null) {
            instance2.onConfigurationChanged(configuration);
        }
        PhotoViewer pipInstance = PhotoViewer.getPipInstance();
        if (pipInstance != null) {
            pipInstance.onConfigurationChanged(configuration);
        }
        ThemeEditorView instance3 = ThemeEditorView.getInstance();
        if (instance3 != null) {
            instance3.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    public void onMultiWindowModeChanged(boolean z) {
        AndroidUtilities.isInMultiwindow = z;
        checkLayout();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v2, types: [int] */
    /* JADX WARNING: type inference failed for: r1v9 */
    /* JADX WARNING: type inference failed for: r1v19 */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0257, code lost:
        if (((org.telegram.ui.ProfileActivity) r14.get(r14.size() - 1)).isSettings() == false) goto L_0x025b;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:235:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0246  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r13, int r14, java.lang.Object... r15) {
        /*
            r12 = this;
            int r0 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r13 != r0) goto L_0x0009
            r12.switchToAvailableAccountOrLogout()
            goto L_0x0582
        L_0x0009:
            int r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1 = 0
            if (r13 != r0) goto L_0x001a
            r13 = r15[r1]
            if (r13 == r12) goto L_0x0582
            r12.onFinish()
            r12.finish()
            goto L_0x0582
        L_0x001a:
            int r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r13 != r0) goto L_0x0049
            org.telegram.tgnet.ConnectionsManager r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r14)
            int r13 = r13.getConnectionState()
            int r15 = r12.currentConnectionState
            if (r15 == r13) goto L_0x0582
            boolean r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r15 == 0) goto L_0x0042
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r0 = "switch to state "
            r15.append(r0)
            r15.append(r13)
            java.lang.String r15 = r15.toString()
            org.telegram.messenger.FileLog.d(r15)
        L_0x0042:
            r12.currentConnectionState = r13
            r12.updateCurrentConnectionState(r14)
            goto L_0x0582
        L_0x0049:
            int r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r13 != r0) goto L_0x0054
            org.telegram.ui.Adapters.DrawerLayoutAdapter r13 = r12.drawerLayoutAdapter
            r13.notifyDataSetChanged()
            goto L_0x0582
        L_0x0054:
            int r0 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2 = 2131624552(0x7f0e0268, float:1.8876287E38)
            java.lang.String r3 = "Cancel"
            r4 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r5 = "AppName"
            r6 = 3
            r7 = 2
            r8 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.String r9 = "OK"
            r10 = 0
            r11 = 1
            if (r13 != r0) goto L_0x0187
            r13 = r15[r1]
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r0 = r13.intValue()
            if (r0 != r6) goto L_0x007a
            org.telegram.ui.ActionBar.AlertDialog r0 = r12.proxyErrorDialog
            if (r0 == 0) goto L_0x007a
            return
        L_0x007a:
            int r0 = r13.intValue()
            r1 = 4
            if (r0 != r1) goto L_0x0089
            r13 = r15[r11]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r13 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r13
            r12.showTosActivity(r14, r13)
            return
        L_0x0089:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r12)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            int r1 = r13.intValue()
            if (r1 == r7) goto L_0x00b2
            int r1 = r13.intValue()
            if (r1 == r6) goto L_0x00b2
            r1 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.String r4 = "MoreInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$NSo2b96RBrvjx_PKPJS1uHaK_D0 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$NSo2b96RBrvjx_PKPJS1uHaK_D0
            r4.<init>(r14)
            r0.setNegativeButton(r1, r4)
        L_0x00b2:
            int r14 = r13.intValue()
            r1 = 5
            if (r14 != r1) goto L_0x00ce
            r13 = 2131625983(0x7f0e07ff, float:1.887919E38)
            java.lang.String r14 = "NobodyLikesSpam3"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00ce:
            int r14 = r13.intValue()
            if (r14 != 0) goto L_0x00e9
            r13 = 2131625981(0x7f0e07fd, float:1.8879185E38)
            java.lang.String r14 = "NobodyLikesSpam1"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00e9:
            int r14 = r13.intValue()
            if (r14 != r11) goto L_0x0103
            r13 = 2131625982(0x7f0e07fe, float:1.8879187E38)
            java.lang.String r14 = "NobodyLikesSpam2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x0103:
            int r14 = r13.intValue()
            if (r14 != r7) goto L_0x013d
            r13 = r15[r11]
            java.lang.String r13 = (java.lang.String) r13
            r0.setMessage(r13)
            r13 = r15[r7]
            java.lang.String r13 = (java.lang.String) r13
            java.lang.String r14 = "AUTH_KEY_DROP_"
            boolean r13 = r13.startsWith(r14)
            if (r13 == 0) goto L_0x0135
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setPositiveButton(r13, r10)
            r13 = 2131625722(0x7f0e06fa, float:1.887866E38)
            java.lang.String r14 = "LogOut"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.-$$Lambda$LaunchActivity$DcnfwOlEX5NnGZyS78k4skpxFsw r14 = new org.telegram.ui.-$$Lambda$LaunchActivity$DcnfwOlEX5NnGZyS78k4skpxFsw
            r14.<init>()
            r0.setNegativeButton(r13, r14)
            goto L_0x0169
        L_0x0135:
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x013d:
            int r13 = r13.intValue()
            if (r13 != r6) goto L_0x0169
            r13 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            java.lang.String r14 = "Proxy"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setTitle(r13)
            r13 = 2131627298(0x7f0e0d22, float:1.8881856E38)
            java.lang.String r14 = "UseProxyTelegramError"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            org.telegram.ui.ActionBar.AlertDialog r13 = r12.showAlertDialog(r0)
            r12.proxyErrorDialog = r13
            return
        L_0x0169:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x0582
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r0.create()
            r13.showDialog(r14)
            goto L_0x0582
        L_0x0187:
            int r0 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r13 != r0) goto L_0x01dd
            r13 = r15[r1]
            java.util.HashMap r13 = (java.util.HashMap) r13
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r15.<init>((android.content.Context) r12)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r15.setTitle(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r15.setPositiveButton(r0, r10)
            r0 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r1 = "ShareYouLocationUnableManually"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo
            r1.<init>(r13, r14)
            r15.setNegativeButton(r0, r1)
            r13 = 2131626937(0x7f0e0bb9, float:1.8881124E38)
            java.lang.String r14 = "ShareYouLocationUnable"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r15.setMessage(r13)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x0582
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r15.create()
            r13.showDialog(r14)
            goto L_0x0582
        L_0x01dd:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r13 != r0) goto L_0x01f0
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x0582
            android.view.View r13 = r13.getChildAt(r1)
            if (r13 == 0) goto L_0x0582
            r13.invalidate()
            goto L_0x0582
        L_0x01f0:
            int r0 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r13 != r0) goto L_0x0226
            java.lang.String r13 = org.telegram.messenger.SharedConfig.passcodeHash
            int r13 = r13.length()
            r14 = 8192(0x2000, float:1.14794E-41)
            if (r13 <= 0) goto L_0x0211
            boolean r13 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r13 != 0) goto L_0x0211
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x020b }
            r13.setFlags(r14, r14)     // Catch:{ Exception -> 0x020b }
            goto L_0x0582
        L_0x020b:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x0582
        L_0x0211:
            boolean r13 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r13 != 0) goto L_0x0582
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x0220 }
            r13.clearFlags(r14)     // Catch:{ Exception -> 0x0220 }
            goto L_0x0582
        L_0x0220:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x0582
        L_0x0226:
            int r0 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r13 != r0) goto L_0x0260
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r13 = r13.size()
            if (r13 <= r11) goto L_0x0243
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            boolean r13 = r13 instanceof org.telegram.ui.ProfileActivity
            if (r13 == 0) goto L_0x0243
            r13 = 1
            goto L_0x0244
        L_0x0243:
            r13 = 0
        L_0x0244:
            if (r13 == 0) goto L_0x025a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = mainFragmentsStack
            int r15 = r14.size()
            int r15 = r15 - r11
            java.lang.Object r14 = r14.get(r15)
            org.telegram.ui.ProfileActivity r14 = (org.telegram.ui.ProfileActivity) r14
            boolean r14 = r14.isSettings()
            if (r14 != 0) goto L_0x025a
            goto L_0x025b
        L_0x025a:
            r1 = r13
        L_0x025b:
            r12.rebuildAllFragments(r1)
            goto L_0x0582
        L_0x0260:
            int r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r13 != r0) goto L_0x0269
            r12.showLanguageAlert(r1)
            goto L_0x0582
        L_0x0269:
            int r0 = org.telegram.messenger.NotificationCenter.openArticle
            if (r13 != r0) goto L_0x029b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0276
            return
        L_0x0276:
            org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = mainFragmentsStack
            int r0 = r14.size()
            int r0 = r0 - r11
            java.lang.Object r14 = r14.get(r0)
            org.telegram.ui.ActionBar.BaseFragment r14 = (org.telegram.ui.ActionBar.BaseFragment) r14
            r13.setParentActivity(r12, r14)
            org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.getInstance()
            r14 = r15[r1]
            org.telegram.tgnet.TLRPC$TL_webPage r14 = (org.telegram.tgnet.TLRPC$TL_webPage) r14
            r15 = r15[r11]
            java.lang.String r15 = (java.lang.String) r15
            r13.open(r14, r15)
            goto L_0x0582
        L_0x029b:
            int r0 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r13 != r0) goto L_0x0320
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r12.actionBarLayout
            if (r13 == 0) goto L_0x031f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = r13.fragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x02ac
            goto L_0x031f
        L_0x02ac:
            r13 = r15[r1]
            java.lang.Integer r13 = (java.lang.Integer) r13
            r13.intValue()
            r13 = r15[r11]
            java.util.HashMap r13 = (java.util.HashMap) r13
            r0 = r15[r7]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            r15 = r15[r6]
            java.lang.Boolean r15 = (java.lang.Boolean) r15
            boolean r15 = r15.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r11
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r5.<init>((android.content.Context) r12)
            r6 = 2131627266(0x7f0e0d02, float:1.8881792E38)
            java.lang.String r7 = "UpdateContactsTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setTitle(r6)
            r6 = 2131627265(0x7f0e0d01, float:1.888179E38)
            java.lang.String r7 = "UpdateContactsMessage"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setMessage(r6)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.-$$Lambda$LaunchActivity$yWZ09sr4SZBZ89evaU7BeAPv2KY r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$yWZ09sr4SZBZ89evaU7BeAPv2KY
            r7.<init>(r14, r13, r0, r15)
            r5.setPositiveButton(r6, r7)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$_P5bEvhUvviSlsVoTEo8pfvXBqo r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$_P5bEvhUvviSlsVoTEo8pfvXBqo
            r3.<init>(r14, r13, r0, r15)
            r5.setNegativeButton(r2, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$5KIejsOQETE7-muLZLFyiXcHku8 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$5KIejsOQETE7-muLZLFyiXcHku8
            r2.<init>(r14, r13, r0, r15)
            r5.setOnBackButtonListener(r2)
            org.telegram.ui.ActionBar.AlertDialog r13 = r5.create()
            r4.showDialog(r13)
            r13.setCanceledOnTouchOutside(r1)
            goto L_0x0582
        L_0x031f:
            return
        L_0x0320:
            int r14 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r0 = 21
            if (r13 != r14) goto L_0x037f
            r13 = r15[r1]
            java.lang.Boolean r13 = (java.lang.Boolean) r13
            boolean r13 = r13.booleanValue()
            if (r13 != 0) goto L_0x036f
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x035a
            java.lang.String r14 = "chats_menuBackground"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setBackgroundColor(r15)
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setGlowColor(r14)
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            java.lang.String r14 = "listSelectorSDK21"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setListSelectorColor(r14)
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r13 = r13.getAdapter()
            r13.notifyDataSetChanged()
        L_0x035a:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r0) goto L_0x036f
            android.app.ActivityManager$TaskDescription r13 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x036f }
            java.lang.String r14 = "actionBarDefault"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x036f }
            r15 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r14 = r14 | r15
            r13.<init>(r10, r10, r14)     // Catch:{ Exception -> 0x036f }
            r12.setTaskDescription(r13)     // Catch:{ Exception -> 0x036f }
        L_0x036f:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r13 = r12.drawerLayoutContainer
            java.lang.String r14 = "windowBackgroundWhite"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setBehindKeyboardColor(r14)
            r12.checkSystemBarColors()
            goto L_0x0582
        L_0x037f:
            int r14 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r13 != r14) goto L_0x044b
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r0) goto L_0x041f
            r13 = r15[r7]
            if (r13 == 0) goto L_0x041f
            android.widget.ImageView r13 = r12.themeSwitchImageView
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x0394
            return
        L_0x0394:
            r13 = r15[r7]     // Catch:{ all -> 0x041f }
            int[] r13 = (int[]) r13     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r14 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            int r14 = r14.getMeasuredWidth()     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            int r0 = r0.getMeasuredHeight()     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            int r2 = r2.getMeasuredWidth()     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            int r3 = r3.getMeasuredHeight()     // Catch:{ all -> 0x041f }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x041f }
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)     // Catch:{ all -> 0x041f }
            android.graphics.Canvas r3 = new android.graphics.Canvas     // Catch:{ all -> 0x041f }
            r3.<init>(r2)     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            r4.draw(r3)     // Catch:{ all -> 0x041f }
            android.widget.ImageView r3 = r12.themeSwitchImageView     // Catch:{ all -> 0x041f }
            r3.setImageBitmap(r2)     // Catch:{ all -> 0x041f }
            android.widget.ImageView r2 = r12.themeSwitchImageView     // Catch:{ all -> 0x041f }
            r2.setVisibility(r1)     // Catch:{ all -> 0x041f }
            r2 = r13[r1]     // Catch:{ all -> 0x041f }
            int r2 = r14 - r2
            r3 = r13[r1]     // Catch:{ all -> 0x041f }
            int r14 = r14 - r3
            int r2 = r2 * r14
            r14 = r13[r11]     // Catch:{ all -> 0x041f }
            int r14 = r0 - r14
            r3 = r13[r11]     // Catch:{ all -> 0x041f }
            int r3 = r0 - r3
            int r14 = r14 * r3
            int r2 = r2 + r14
            double r2 = (double) r2     // Catch:{ all -> 0x041f }
            double r2 = java.lang.Math.sqrt(r2)     // Catch:{ all -> 0x041f }
            r14 = r13[r1]     // Catch:{ all -> 0x041f }
            r4 = r13[r1]     // Catch:{ all -> 0x041f }
            int r14 = r14 * r4
            r4 = r13[r11]     // Catch:{ all -> 0x041f }
            int r4 = r0 - r4
            r5 = r13[r11]     // Catch:{ all -> 0x041f }
            int r0 = r0 - r5
            int r4 = r4 * r0
            int r14 = r14 + r4
            double r4 = (double) r14     // Catch:{ all -> 0x041f }
            double r4 = java.lang.Math.sqrt(r4)     // Catch:{ all -> 0x041f }
            double r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x041f }
            float r14 = (float) r2     // Catch:{ all -> 0x041f }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r12.drawerLayoutContainer     // Catch:{ all -> 0x041f }
            r2 = r13[r1]     // Catch:{ all -> 0x041f }
            r13 = r13[r11]     // Catch:{ all -> 0x041f }
            r3 = 0
            android.animation.Animator r13 = android.view.ViewAnimationUtils.createCircularReveal(r0, r2, r13, r3, r14)     // Catch:{ all -> 0x041f }
            r2 = 400(0x190, double:1.976E-321)
            r13.setDuration(r2)     // Catch:{ all -> 0x041f }
            android.view.animation.Interpolator r14 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x041f }
            r13.setInterpolator(r14)     // Catch:{ all -> 0x041f }
            org.telegram.ui.LaunchActivity$7 r14 = new org.telegram.ui.LaunchActivity$7     // Catch:{ all -> 0x041f }
            r14.<init>()     // Catch:{ all -> 0x041f }
            r13.addListener(r14)     // Catch:{ all -> 0x041f }
            r13.start()     // Catch:{ all -> 0x041f }
            r13 = 1
            goto L_0x0420
        L_0x041f:
            r13 = 0
        L_0x0420:
            r14 = r15[r1]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r14
            r0 = r15[r11]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            r15 = r15[r6]
            java.lang.Integer r15 = (java.lang.Integer) r15
            int r15 = r15.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0582
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.rightActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            goto L_0x0582
        L_0x044b:
            int r14 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r13 != r14) goto L_0x047c
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x0582
            r14 = r15[r1]
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r13 = r13.getChildCount()
        L_0x045b:
            if (r1 >= r13) goto L_0x0582
            org.telegram.ui.Components.RecyclerListView r15 = r12.sideMenu
            android.view.View r15 = r15.getChildAt(r1)
            boolean r0 = r15 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r0 == 0) goto L_0x0479
            r0 = r15
            org.telegram.ui.Cells.DrawerUserCell r0 = (org.telegram.ui.Cells.DrawerUserCell) r0
            int r0 = r0.getAccountNumber()
            int r2 = r14.intValue()
            if (r0 != r2) goto L_0x0479
            r15.invalidate()
            goto L_0x0582
        L_0x0479:
            int r1 = r1 + 1
            goto L_0x045b
        L_0x047c:
            int r14 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r13 != r14) goto L_0x048b
            r13 = r15[r1]     // Catch:{ all -> 0x0582 }
            com.google.android.gms.common.api.Status r13 = (com.google.android.gms.common.api.Status) r13     // Catch:{ all -> 0x0582 }
            r14 = 140(0x8c, float:1.96E-43)
            r13.startResolutionForResult(r12, r14)     // Catch:{ all -> 0x0582 }
            goto L_0x0582
        L_0x048b:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r13 != r14) goto L_0x054a
            java.lang.String r13 = r12.loadingThemeFileName
            if (r13 == 0) goto L_0x0518
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0582
            r12.loadingThemeFileName = r10
            java.io.File r13 = new java.io.File
            java.io.File r14 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r0 = "remote"
            r15.append(r0)
            org.telegram.tgnet.TLRPC$TL_theme r0 = r12.loadingTheme
            long r0 = r0.id
            r15.append(r0)
            java.lang.String r0 = ".attheme"
            r15.append(r0)
            java.lang.String r15 = r15.toString()
            r13.<init>(r14, r15)
            org.telegram.tgnet.TLRPC$TL_theme r14 = r12.loadingTheme
            java.lang.String r15 = r14.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r13, r15, r14)
            if (r14 == 0) goto L_0x0513
            java.lang.String r15 = r14.pathToWallpaper
            if (r15 == 0) goto L_0x04fc
            java.io.File r15 = new java.io.File
            java.lang.String r0 = r14.pathToWallpaper
            r15.<init>(r0)
            boolean r15 = r15.exists()
            if (r15 != 0) goto L_0x04fc
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r13 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r13.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r15 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r15.<init>()
            java.lang.String r0 = r14.slug
            r15.slug = r0
            r13.wallpaper = r15
            int r15 = r14.account
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            org.telegram.ui.-$$Lambda$LaunchActivity$Xb_UqNh9mLoUiPS9PtfFyDGzXWY r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Xb_UqNh9mLoUiPS9PtfFyDGzXWY
            r0.<init>(r14)
            r15.sendRequest(r13, r0)
            return
        L_0x04fc:
            org.telegram.tgnet.TLRPC$TL_theme r14 = r12.loadingTheme
            java.lang.String r15 = r14.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r13, r15, r14, r11)
            if (r1 == 0) goto L_0x0513
            org.telegram.ui.ThemePreviewActivity r13 = new org.telegram.ui.ThemePreviewActivity
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r13
            r0.<init>(r1, r2, r3, r4, r5)
            r12.lambda$runLinkRequest$32$LaunchActivity(r13)
        L_0x0513:
            r12.onThemeLoadFinish()
            goto L_0x0582
        L_0x0518:
            java.lang.String r13 = r12.loadingThemeWallpaperName
            if (r13 == 0) goto L_0x0582
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0582
            r12.loadingThemeWallpaperName = r10
            r13 = r15[r11]
            java.io.File r13 = (java.io.File) r13
            boolean r14 = r12.loadingThemeAccent
            if (r14 == 0) goto L_0x053d
            org.telegram.tgnet.TLRPC$TL_theme r13 = r12.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = r12.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r15 = r12.loadingThemeInfo
            r12.openThemeAccentPreview(r13, r14, r15)
            r12.onThemeLoadFinish()
            goto L_0x0582
        L_0x053d:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r12.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r15 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$Xd6X8oHbTQg4L6KRZ74pkknk70g r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Xd6X8oHbTQg4L6KRZ74pkknk70g
            r0.<init>(r14, r13)
            r15.postRunnable(r0)
            goto L_0x0582
        L_0x054a:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r13 != r14) goto L_0x0566
            r13 = r15[r1]
            java.lang.String r13 = (java.lang.String) r13
            java.lang.String r14 = r12.loadingThemeFileName
            boolean r14 = r13.equals(r14)
            if (r14 != 0) goto L_0x0562
            java.lang.String r14 = r12.loadingThemeWallpaperName
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0582
        L_0x0562:
            r12.onThemeLoadFinish()
            goto L_0x0582
        L_0x0566:
            int r14 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r13 != r14) goto L_0x057b
            boolean r13 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r13 == 0) goto L_0x056f
            return
        L_0x056f:
            boolean r13 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r13 == 0) goto L_0x0577
            r12.onPasscodeResume()
            goto L_0x0582
        L_0x0577:
            r12.onPasscodePause()
            goto L_0x0582
        L_0x057b:
            int r14 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r13 != r14) goto L_0x0582
            r12.checkSystemBarColors()
        L_0x0582:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$49(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$50$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$52$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(hashMap, i) {
                    public final /* synthetic */ HashMap f$0;
                    public final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                        LaunchActivity.lambda$null$51(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$32$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$51(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$57$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$56$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$56$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
            this.loadingThemeWallpaper = tLRPC$TL_wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(tLRPC$TL_wallPaper.document, tLRPC$TL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$59$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$58$LaunchActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$58$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$32$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
            }
            onThemeLoadFinish();
        }
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void openThemeAccentPreview(TLRPC$TL_theme tLRPC$TL_theme, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, Theme.ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        Theme.ThemeAccent createNewAccent = themeInfo.createNewAccent(tLRPC$TL_theme, this.currentAccount);
        themeInfo.prevAccentId = themeInfo.currentAccentId;
        themeInfo.setCurrentAccentId(createNewAccent.id);
        createNewAccent.pattern = tLRPC$TL_wallPaper;
        lambda$runLinkRequest$32$LaunchActivity(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
    }

    private void onThemeLoadFinish() {
        AlertDialog alertDialog = this.loadingThemeProgressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } finally {
                this.loadingThemeProgressDialog = null;
            }
        }
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void checkFreeDiscSpace() {
        SharedConfig.checkKeepMedia();
        if (Build.VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$61$LaunchActivity();
                }
            }, 2000);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$61$LaunchActivity() {
        File directory;
        long j;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (Math.abs(globalMainSettings.getLong("last_space_check", 0) - System.currentTimeMillis()) >= NUM && (directory = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(directory.getAbsolutePath());
                    if (Build.VERSION.SDK_INT < 18) {
                        j = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                    } else {
                        j = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
                    }
                    if (j < NUM) {
                        globalMainSettings.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                LaunchActivity.this.lambda$null$60$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$null$60$LaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable unused) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0054 A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0056 A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x005c A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x005f A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0064 A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0065 A[Catch:{ Exception -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d A[Catch:{ Exception -> 0x0115 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r17, org.telegram.messenger.LocaleController.LocaleInfo r18, java.lang.String r19) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r0 = "ChooseYourLanguageOther"
            java.lang.String r2 = "ChooseYourLanguage"
            r3 = 0
            r1.loadingLocaleDialog = r3     // Catch:{ Exception -> 0x0115 }
            r4 = r17
            boolean r5 = r4.builtIn     // Catch:{ Exception -> 0x0115 }
            r6 = 1
            if (r5 != 0) goto L_0x001d
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0115 }
            boolean r5 = r5.isCurrentLocalLocale()     // Catch:{ Exception -> 0x0115 }
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r5 = 0
            goto L_0x001e
        L_0x001d:
            r5 = 1
        L_0x001e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x0115 }
            r7.<init>((android.content.Context) r1)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r9 = 2131624790(0x7f0e0356, float:1.887677E38)
            java.lang.String r8 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0115 }
            r7.setTitle(r8)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r2 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0115 }
            r7.setSubtitle(r2)     // Catch:{ Exception -> 0x0115 }
            android.widget.LinearLayout r2 = new android.widget.LinearLayout     // Catch:{ Exception -> 0x0115 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0115 }
            r2.setOrientation(r6)     // Catch:{ Exception -> 0x0115 }
            r8 = 2
            org.telegram.ui.Cells.LanguageCell[] r9 = new org.telegram.ui.Cells.LanguageCell[r8]     // Catch:{ Exception -> 0x0115 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r10 = new org.telegram.messenger.LocaleController.LocaleInfo[r6]     // Catch:{ Exception -> 0x0115 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r11 = new org.telegram.messenger.LocaleController.LocaleInfo[r8]     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r13 = "English"
            r14 = 2131625152(0x7f0e04c0, float:1.8877504E38)
            java.lang.String r12 = r1.getStringForLanguageAlert(r12, r13, r14)     // Catch:{ Exception -> 0x0115 }
            if (r5 == 0) goto L_0x0056
            r13 = r4
            goto L_0x0058
        L_0x0056:
            r13 = r18
        L_0x0058:
            r11[r3] = r13     // Catch:{ Exception -> 0x0115 }
            if (r5 == 0) goto L_0x005f
            r13 = r18
            goto L_0x0060
        L_0x005f:
            r13 = r4
        L_0x0060:
            r11[r6] = r13     // Catch:{ Exception -> 0x0115 }
            if (r5 == 0) goto L_0x0065
            goto L_0x0067
        L_0x0065:
            r4 = r18
        L_0x0067:
            r10[r3] = r4     // Catch:{ Exception -> 0x0115 }
            r4 = 0
        L_0x006a:
            r13 = -1
            if (r4 >= r8) goto L_0x00bf
            org.telegram.ui.Cells.LanguageCell r14 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r14.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            r9[r4] = r14     // Catch:{ Exception -> 0x0115 }
            r14 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            r15 = r11[r4]     // Catch:{ Exception -> 0x0115 }
            r3 = r11[r4]     // Catch:{ Exception -> 0x0115 }
            r5 = r18
            if (r3 != r5) goto L_0x0080
            r3 = r12
            goto L_0x0081
        L_0x0080:
            r3 = 0
        L_0x0081:
            r14.setLanguage(r15, r3, r6)     // Catch:{ Exception -> 0x0115 }
            r3 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0115 }
            r3.setTag(r14)     // Catch:{ Exception -> 0x0115 }
            r3 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            java.lang.String r14 = "dialogButtonSelector"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0115 }
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r8)     // Catch:{ Exception -> 0x0115 }
            r3.setBackgroundDrawable(r14)     // Catch:{ Exception -> 0x0115 }
            r3 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            if (r4 != 0) goto L_0x00a2
            r14 = 1
            goto L_0x00a3
        L_0x00a2:
            r14 = 0
        L_0x00a3:
            r3.setLanguageSelected(r14)     // Catch:{ Exception -> 0x0115 }
            r3 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            r14 = 50
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r14)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r13)     // Catch:{ Exception -> 0x0115 }
            r3 = r9[r4]     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$8tQj5cq4HGp8JiT0ZRfT2mD1dDw r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$8tQj5cq4HGp8JiT0ZRfT2mD1dDw     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$Ro8pzBJk5O5wAEkcazwM88tPiOo r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Ro8pzBJk5O5wAEkcazwM88tPiOo     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$9Ej_ZHZEFtsi6RMBIKBFESTAxoI r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$9Ej_ZHZEFtsi6RMBIKBFESTAxoI     // Catch:{ Exception -> 0x0115 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0115 }
            r7.setNegativeButton(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r7)     // Catch:{ Exception -> 0x0115 }
            r1.localeDialog = r0     // Catch:{ Exception -> 0x0115 }
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0115 }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x0115 }
            java.lang.String r2 = "language_showed2"
            r3 = r19
            android.content.SharedPreferences$Editor r0 = r0.putString(r2, r3)     // Catch:{ Exception -> 0x0115 }
            r0.commit()     // Catch:{ Exception -> 0x0115 }
            goto L_0x0119
        L_0x0115:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0119:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    static /* synthetic */ void lambda$showLanguageAlertInternal$62(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$63$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$32$LaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$64$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    private void showLanguageAlert(boolean z) {
        String str;
        try {
            if (this.loadingLocaleDialog) {
                return;
            }
            if (!ApplicationLoader.mainInterfacePaused) {
                String string = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
                String str2 = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                if (z || !string.equals(str2)) {
                    LocaleController.LocaleInfo[] localeInfoArr = new LocaleController.LocaleInfo[2];
                    String str3 = str2.contains("-") ? str2.split("-")[0] : str2;
                    if ("in".equals(str3)) {
                        str = "id";
                    } else if ("iw".equals(str3)) {
                        str = "he";
                    } else {
                        str = "jw".equals(str3) ? "jv" : null;
                    }
                    int i = 0;
                    while (true) {
                        if (i >= LocaleController.getInstance().languages.size()) {
                            break;
                        }
                        LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().languages.get(i);
                        if (localeInfo.shortName.equals("en")) {
                            localeInfoArr[0] = localeInfo;
                        }
                        if (localeInfo.shortName.replace("_", "-").equals(str2) || localeInfo.shortName.equals(str3) || localeInfo.shortName.equals(str)) {
                            localeInfoArr[1] = localeInfo;
                        }
                        if (localeInfoArr[0] != null && localeInfoArr[1] != null) {
                            break;
                        }
                        i++;
                    }
                    if (localeInfoArr[0] != null && localeInfoArr[1] != null) {
                        if (localeInfoArr[0] != localeInfoArr[1]) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("show lang alert for " + localeInfoArr[0].getKey() + " and " + localeInfoArr[1].getKey());
                            }
                            this.systemLocaleStrings = null;
                            this.englishLocaleStrings = null;
                            this.loadingLocaleDialog = true;
                            TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings = new TLRPC$TL_langpack_getStrings();
                            tLRPC$TL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                            tLRPC$TL_langpack_getStrings.keys.add("English");
                            tLRPC$TL_langpack_getStrings.keys.add("ChooseYourLanguage");
                            tLRPC$TL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                            tLRPC$TL_langpack_getStrings.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new RequestDelegate(localeInfoArr, str2) {
                                public final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                public final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$66$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                                }
                            }, 8);
                            TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                            tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                            tLRPC$TL_langpack_getStrings2.keys.add("English");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new RequestDelegate(localeInfoArr, str2) {
                                public final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                public final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$68$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                                }
                            }, 8);
                        }
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("alert already showed for " + string);
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$66$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap, localeInfoArr, str) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$65$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$65$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$68$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap, localeInfoArr, str) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$67$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$67$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.englishLocaleStrings = hashMap;
        if (hashMap != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodePause");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            AnonymousClass8 r0 = new Runnable() {
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        Runnable unused = LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = r0;
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(r0, 1000);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in 1000");
                }
            } else if (SharedConfig.autoLockIn != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in " + ((((long) SharedConfig.autoLockIn) * 1000) + 1000));
                }
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) SharedConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodeResume");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onPasscodeResume");
            }
        }
    }

    private void updateCurrentConnectionState(int i) {
        String str;
        if (this.actionBarLayout != null) {
            int i2 = 0;
            int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            this.currentConnectionState = connectionState;
            $$Lambda$LaunchActivity$P2BvsvtXjb2l1rrge69H2_Nlg r4 = null;
            if (connectionState == 2) {
                i2 = NUM;
                str = "WaitingForNetwork";
            } else if (connectionState == 5) {
                i2 = NUM;
                str = "Updating";
            } else if (connectionState == 4) {
                i2 = NUM;
                str = "ConnectingToProxy";
            } else if (connectionState == 1) {
                i2 = NUM;
                str = "Connecting";
            } else {
                str = null;
            }
            int i3 = this.currentConnectionState;
            if (i3 == 1 || i3 == 4) {
                r4 = new Runnable() {
                    public final void run() {
                        LaunchActivity.this.lambda$updateCurrentConnectionState$69$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$69$LaunchActivity() {
        /*
            r2 = this;
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x001d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = layerFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0034
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = layerFragmentsStack
            int r1 = r0.size()
            int r1 = r1 + -1
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            goto L_0x0035
        L_0x001d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0034
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 + -1
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            goto L_0x0035
        L_0x0034:
            r0 = 0
        L_0x0035:
            boolean r1 = r0 instanceof org.telegram.ui.ProxyListActivity
            if (r1 != 0) goto L_0x0046
            boolean r0 = r0 instanceof org.telegram.ui.ProxySettingsActivity
            if (r0 == 0) goto L_0x003e
            goto L_0x0046
        L_0x003e:
            org.telegram.ui.ProxyListActivity r0 = new org.telegram.ui.ProxyListActivity
            r0.<init>()
            r2.lambda$runLinkRequest$32$LaunchActivity(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$69$LaunchActivity():void");
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        try {
            super.onSaveInstanceState(bundle);
            BaseFragment baseFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                baseFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (baseFragment != null) {
                Bundle arguments = baseFragment.getArguments();
                if ((baseFragment instanceof ChatActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "group");
                } else if (baseFragment instanceof WallpapersListActivity) {
                    bundle.putString("fragment", "wallpapers");
                } else if (baseFragment instanceof ProfileActivity) {
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                    if (profileActivity.isSettings()) {
                        bundle.putString("fragment", "settings");
                    } else if (profileActivity.isChat() && arguments != null) {
                        bundle.putBundle("args", arguments);
                        bundle.putString("fragment", "chat_profile");
                    }
                } else if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "channel");
                }
                baseFragment.saveSelfArgs(bundle);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
            return;
        }
        boolean z = false;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
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
            if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = this.rightActionBarLayout.fragmentsStack;
                z = !arrayList.get(arrayList.size() - 1).onBackPressed();
            }
            if (!z) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        ActionBarLayout actionBarLayout2 = this.actionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.onLowMemory();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onLowMemory();
                this.layersActionBarLayout.onLowMemory();
            }
        }
    }

    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        this.visibleActionMode = actionMode;
        try {
            Menu menu = actionMode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (Build.VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(actionMode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(actionMode);
                this.layersActionBarLayout.onActionModeStarted(actionMode);
            }
        }
    }

    public void onActionModeFinished(ActionMode actionMode) {
        super.onActionModeFinished(actionMode);
        if (this.visibleActionMode == actionMode) {
            this.visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(actionMode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(actionMode);
                this.layersActionBarLayout.onActionModeFinished(actionMode);
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

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        keyEvent.getKeyCode();
        if (!mainFragmentsStack.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25))) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
            if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
                return true;
            }
            if (AndroidUtilities.isTablet() && !rightFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                BaseFragment baseFragment2 = arrayList2.get(arrayList2.size() - 1);
                if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).maybePlayVisibleVideo()) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(i, keyEvent);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(i, keyEvent);
                } else {
                    this.rightActionBarLayout.onKeyUp(i, keyEvent);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(i, keyEvent);
            } else if (!this.drawerLayoutContainer.isDrawerOpened()) {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            } else {
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout2) {
        boolean z3;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        ActionBarLayout actionBarLayout6;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            boolean z4 = baseFragment instanceof LoginActivity;
            this.drawerLayoutContainer.setAllowOpenDrawer(!z4 && !(baseFragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
            if ((baseFragment instanceof DialogsActivity) && ((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout2 != (actionBarLayout6 = this.actionBarLayout)) {
                actionBarLayout6.removeAllFragments();
                this.actionBarLayout.presentFragment(baseFragment, z, z2, false, false);
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
            } else if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                if (actionBarLayout2 == actionBarLayout7) {
                    return true;
                }
                actionBarLayout7.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (z4) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(NUM);
                }
                this.layersActionBarLayout.presentFragment(baseFragment, z, z2, false, false);
                return false;
            } else if ((!this.tabletFullSize && actionBarLayout2 == this.rightActionBarLayout) || (this.tabletFullSize && actionBarLayout2 == this.actionBarLayout)) {
                boolean z5 = (this.tabletFullSize && actionBarLayout2 == (actionBarLayout3 = this.actionBarLayout) && actionBarLayout3.fragmentsStack.size() == 1) ? false : true;
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout8 = this.layersActionBarLayout;
                        actionBarLayout8.removeFragmentFromStack(actionBarLayout8.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!z2);
                }
                if (!z5) {
                    this.actionBarLayout.presentFragment(baseFragment, false, z2, false, false);
                }
                return z5;
            } else if (!this.tabletFullSize && actionBarLayout2 != (actionBarLayout5 = this.rightActionBarLayout)) {
                actionBarLayout5.setVisibility(0);
                this.backgroundTablet.setVisibility(8);
                this.rightActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.presentFragment(baseFragment, z, true, false, false);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout9 = this.layersActionBarLayout;
                        actionBarLayout9.removeFragmentFromStack(actionBarLayout9.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!z2);
                }
                return false;
            } else if (!this.tabletFullSize || actionBarLayout2 == (actionBarLayout4 = this.actionBarLayout)) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout10 = this.layersActionBarLayout;
                        actionBarLayout10.removeFragmentFromStack(actionBarLayout10.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!z2);
                }
                ActionBarLayout actionBarLayout11 = this.actionBarLayout;
                actionBarLayout11.presentFragment(baseFragment, actionBarLayout11.fragmentsStack.size() > 1, z2, false, false);
                return false;
            } else {
                actionBarLayout4.presentFragment(baseFragment, actionBarLayout4.fragmentsStack.size() > 1, z2, false, false);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout12 = this.layersActionBarLayout;
                        actionBarLayout12.removeFragmentFromStack(actionBarLayout12.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!z2);
                }
                return false;
            }
        } else {
            if (!(baseFragment instanceof LoginActivity) ? !(baseFragment instanceof CountrySelectActivity) || mainFragmentsStack.size() != 1 : mainFragmentsStack.size() != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(z3, false);
            return true;
        }
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout2) {
        boolean z;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        if (AndroidUtilities.isTablet()) {
            boolean z2 = baseFragment instanceof LoginActivity;
            this.drawerLayoutContainer.setAllowOpenDrawer(!z2 && !(baseFragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
            if (baseFragment instanceof DialogsActivity) {
                if (((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout2 != (actionBarLayout5 = this.actionBarLayout)) {
                    actionBarLayout5.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(baseFragment);
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
                }
            } else if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout6 = this.layersActionBarLayout;
                if (actionBarLayout2 != actionBarLayout6) {
                    actionBarLayout6.setVisibility(0);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                    if (z2) {
                        this.backgroundTablet.setVisibility(0);
                        this.shadowTabletSide.setVisibility(8);
                        this.shadowTablet.setBackgroundColor(0);
                    } else {
                        this.shadowTablet.setBackgroundColor(NUM);
                    }
                    this.layersActionBarLayout.addFragmentToStack(baseFragment);
                    return false;
                }
            } else if (!this.tabletFullSize && actionBarLayout2 != (actionBarLayout4 = this.rightActionBarLayout)) {
                actionBarLayout4.setVisibility(0);
                this.backgroundTablet.setVisibility(8);
                this.rightActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.addFragmentToStack(baseFragment);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                        actionBarLayout7.removeFragmentFromStack(actionBarLayout7.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return false;
            } else if (this.tabletFullSize && actionBarLayout2 != (actionBarLayout3 = this.actionBarLayout)) {
                actionBarLayout3.addFragmentToStack(baseFragment);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        ActionBarLayout actionBarLayout8 = this.layersActionBarLayout;
                        actionBarLayout8.removeFragmentFromStack(actionBarLayout8.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return false;
            }
            return true;
        }
        if (!(baseFragment instanceof LoginActivity) ? !(baseFragment instanceof CountrySelectActivity) || mainFragmentsStack.size() != 1 : mainFragmentsStack.size() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(z, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout2) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout2 == this.actionBarLayout && actionBarLayout2.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout2 == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (actionBarLayout2 == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout2.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (actionBarLayout2.fragmentsStack.size() >= 2 && !(actionBarLayout2.fragmentsStack.get(0) instanceof LoginActivity)) {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean z) {
        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.rebuildAllFragmentViews(z, z);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout2, boolean z) {
        if (AndroidUtilities.isTablet() && actionBarLayout2 == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(z, z);
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }
}
