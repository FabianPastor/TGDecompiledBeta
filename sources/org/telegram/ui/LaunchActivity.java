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
import android.util.Base64;
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
import androidx.recyclerview.widget.ItemTouchHelper;
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
import org.telegram.messenger.voip.VoIPService;
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
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_authorization;
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
import org.telegram.tgnet.TLRPC$TL_messages_discussionMessage;
import org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
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
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.Components.voip.VoIPHelper;
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
    /* access modifiers changed from: private */
    public DrawerLayoutAdapter drawerLayoutAdapter;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    private boolean finished;
    private FrameLayout frameLayout;
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
    /* access modifiers changed from: private */
    public RecyclerListView sideMenu;
    private HashMap<String, String> systemLocaleStrings;
    /* access modifiers changed from: private */
    public boolean tabletFullSize;
    /* access modifiers changed from: private */
    public TermsOfServiceView termsOfServiceView;
    /* access modifiers changed from: private */
    public ImageView themeSwitchImageView;
    /* access modifiers changed from: private */
    public RLottieDrawable themeSwitchSunDrawable;
    /* access modifiers changed from: private */
    public View themeSwitchSunView;
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
            r4 = 0
            r5 = 21
            if (r1 < r5) goto L_0x0112
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r6 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0109 }
            java.lang.String r7 = "actionBarDefault"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x0109 }
            r7 = r7 | r1
            r6.<init>(r4, r4, r7)     // Catch:{ Exception -> 0x0109 }
            r12.setTaskDescription(r6)     // Catch:{ Exception -> 0x0109 }
        L_0x0109:
            android.view.Window r6 = r12.getWindow()     // Catch:{ Exception -> 0x0111 }
            r6.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x0111 }
            goto L_0x0112
        L_0x0111:
        L_0x0112:
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x012c
            boolean r1 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x012c
            android.view.Window r1 = r12.getWindow()     // Catch:{ Exception -> 0x0128 }
            r6 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r6, r6)     // Catch:{ Exception -> 0x0128 }
            goto L_0x012c
        L_0x0128:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x012c:
            super.onCreate(r13)
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r1 < r6) goto L_0x013b
            boolean r1 = r12.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r1
        L_0x013b:
            org.telegram.ui.ActionBar.Theme.createChatResources(r12, r3)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0154
            boolean r1 = org.telegram.messenger.SharedConfig.appLocked
            if (r1 == 0) goto L_0x0154
            long r7 = android.os.SystemClock.elapsedRealtime()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r1 = (int) r7
            org.telegram.messenger.SharedConfig.lastPauseTime = r1
        L_0x0154:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r12)
            org.telegram.ui.LaunchActivity$1 r1 = new org.telegram.ui.LaunchActivity$1
            r1.<init>(r12)
            r12.actionBarLayout = r1
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r12)
            r12.frameLayout = r1
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r8 = -1
            r7.<init>(r8, r8)
            r12.setContentView(r1, r7)
            int r1 = android.os.Build.VERSION.SDK_INT
            r7 = 8
            if (r1 < r5) goto L_0x017e
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r12)
            r12.themeSwitchImageView = r1
            r1.setVisibility(r7)
        L_0x017e:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = new org.telegram.ui.ActionBar.DrawerLayoutContainer
            r1.<init>(r12)
            r12.drawerLayoutContainer = r1
            java.lang.String r9 = "windowBackgroundWhite"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r1.setBehindKeyboardColor(r9)
            android.widget.FrameLayout r1 = r12.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r12.drawerLayoutContainer
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r1.addView(r9, r11)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r5) goto L_0x01b9
            org.telegram.ui.LaunchActivity$2 r1 = new org.telegram.ui.LaunchActivity$2
            r1.<init>(r12)
            r12.themeSwitchSunView = r1
            android.widget.FrameLayout r5 = r12.frameLayout
            r9 = 48
            r11 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r5.addView(r1, r9)
            android.view.View r1 = r12.themeSwitchSunView
            r1.setVisibility(r7)
        L_0x01b9:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x02a3
            android.view.Window r1 = r12.getWindow()
            r5 = 16
            r1.setSoftInputMode(r5)
            org.telegram.ui.LaunchActivity$3 r1 = new org.telegram.ui.LaunchActivity$3
            r1.<init>(r12)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r5.addView(r1, r9)
            android.view.View r5 = new android.view.View
            r5.<init>(r12)
            r12.backgroundTablet = r5
            android.content.res.Resources r5 = r12.getResources()
            r9 = 2131165332(0x7var_, float:1.7944878E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r9)
            android.graphics.drawable.BitmapDrawable r5 = (android.graphics.drawable.BitmapDrawable) r5
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.REPEAT
            r5.setTileModeXY(r9, r9)
            android.view.View r9 = r12.backgroundTablet
            r9.setBackgroundDrawable(r5)
            android.view.View r5 = r12.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createRelative(r8, r8)
            r1.addView(r5, r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.actionBarLayout
            r1.addView(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r12)
            r12.rightActionBarLayout = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = rightFragmentsStack
            r5.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.rightActionBarLayout
            r5.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.rightActionBarLayout
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r12)
            r12.shadowTabletSide = r5
            r9 = 1076449908(0x40295274, float:2.6456575)
            r5.setBackgroundColor(r9)
            android.widget.FrameLayout r5 = r12.shadowTabletSide
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r12)
            r12.shadowTablet = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x023c
            r9 = 8
            goto L_0x023d
        L_0x023c:
            r9 = 0
        L_0x023d:
            r5.setVisibility(r9)
            android.widget.FrameLayout r5 = r12.shadowTablet
            r9 = 2130706432(0x7var_, float:1.7014118E38)
            r5.setBackgroundColor(r9)
            android.widget.FrameLayout r5 = r12.shadowTablet
            r1.addView(r5)
            android.widget.FrameLayout r5 = r12.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4 r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4
            r9.<init>()
            r5.setOnTouchListener(r9)
            android.widget.FrameLayout r5 = r12.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ r9 = org.telegram.ui.$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ.INSTANCE
            r5.setOnClickListener(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r12)
            r12.layersActionBarLayout = r5
            r5.setRemoveActionBarExtraHeight(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            android.widget.FrameLayout r9 = r12.shadowTablet
            r5.setBackgroundView(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            r5.setUseAlphaAnimations(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            r9 = 2131165296(0x7var_, float:1.7944805E38)
            r5.setBackgroundResource(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            r5.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            r5.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r12.drawerLayoutContainer
            r5.setDrawerLayoutContainer(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x0299
            goto L_0x029a
        L_0x0299:
            r7 = 0
        L_0x029a:
            r5.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.layersActionBarLayout
            r1.addView(r5)
            goto L_0x02af
        L_0x02a3:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r8, r8)
            r1.addView(r5, r7)
        L_0x02af:
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r12)
            r12.sideMenu = r1
            org.telegram.ui.Components.SideMenultItemAnimator r5 = new org.telegram.ui.Components.SideMenultItemAnimator
            r5.<init>(r1)
            r12.itemAnimator = r5
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setItemAnimator(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            java.lang.String r5 = "chats_menuBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setBackgroundColor(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            r5.<init>(r12, r2, r3)
            r1.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setAllowItemsInteractionDuringAnimation(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r5 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r7 = r12.itemAnimator
            r5.<init>(r12, r7)
            r12.drawerLayoutAdapter = r5
            r1.setAdapter(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.Components.RecyclerListView r5 = r12.sideMenu
            r1.setDrawerLayout(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x030a
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0321
        L_0x030a:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r9, r5)
            r9 = 1113587712(0x42600000, float:56.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r5 - r9
            int r5 = java.lang.Math.min(r7, r5)
        L_0x0321:
            r1.width = r5
            r1.height = r8
            org.telegram.ui.Components.RecyclerListView r5 = r12.sideMenu
            r5.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$Op_z7pMlRt1m8iTw0iRpzILSb8s r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$Op_z7pMlRt1m8iTw0iRpzILSb8s
            r5.<init>()
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r5)
            androidx.recyclerview.widget.ItemTouchHelper r1 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$5 r5 = new org.telegram.ui.LaunchActivity$5
            r7 = 3
            r5.<init>(r7, r3)
            r1.<init>(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r12.sideMenu
            r1.attachToRecyclerView(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r12.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$lfRqAv_09u1FlHY0nY_GzJUY410 r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$lfRqAv_09u1FlHY0nY_GzJUY410
            r9.<init>(r1)
            r5.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r9)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r12.actionBarLayout
            r1.setParentActionBarLayout(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r12.drawerLayoutContainer
            r1.setDrawerLayoutContainer(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = mainFragmentsStack
            r1.init(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            r1.setDelegate(r12)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.Components.PasscodeView r1 = new org.telegram.ui.Components.PasscodeView
            r1.<init>(r12)
            r12.passcodeView = r1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r5.addView(r1, r9)
            r12.checkCurrentAccount()
            int r1 = r12.currentAccount
            r12.updateCurrentConnectionState(r1)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r9[r3] = r12
            r1.postNotificationName(r5, r9)
            int r1 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r12.currentConnectionState = r1
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needShowAlert
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.reloadInterface
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r1.addObserver(r12, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r1.addObserver(r12, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x051c
            int r1 = r12.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x0425
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.LoginActivity r4 = new org.telegram.ui.LoginActivity
            r4.<init>()
            r1.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x0439
        L_0x0425:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            r4.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r3)
        L_0x0439:
            if (r13 == 0) goto L_0x0583
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r13.getString(r1)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x0583
            java.lang.String r4 = "args"
            android.os.Bundle r4 = r13.getBundle(r4)     // Catch:{ Exception -> 0x0517 }
            int r5 = r1.hashCode()     // Catch:{ Exception -> 0x0517 }
            r9 = 5
            r10 = 4
            r11 = 2
            switch(r5) {
                case -1529105743: goto L_0x0486;
                case -1349522494: goto L_0x047c;
                case 3052376: goto L_0x0472;
                case 98629247: goto L_0x0468;
                case 738950403: goto L_0x045e;
                case 1434631203: goto L_0x0454;
                default: goto L_0x0453;
            }     // Catch:{ Exception -> 0x0517 }
        L_0x0453:
            goto L_0x048f
        L_0x0454:
            java.lang.String r5 = "settings"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 1
            goto L_0x048f
        L_0x045e:
            java.lang.String r5 = "channel"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 3
            goto L_0x048f
        L_0x0468:
            java.lang.String r5 = "group"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 2
            goto L_0x048f
        L_0x0472:
            java.lang.String r5 = "chat"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 0
            goto L_0x048f
        L_0x047c:
            java.lang.String r5 = "chat_profile"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 4
            goto L_0x048f
        L_0x0486:
            java.lang.String r5 = "wallpapers"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0517 }
            if (r1 == 0) goto L_0x048f
            r8 = 5
        L_0x048f:
            if (r8 == 0) goto L_0x0504
            if (r8 == r2) goto L_0x04e8
            if (r8 == r11) goto L_0x04d4
            if (r8 == r7) goto L_0x04c0
            if (r8 == r10) goto L_0x04ac
            if (r8 == r9) goto L_0x049d
            goto L_0x0583
        L_0x049d:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x04ac:
            if (r4 == 0) goto L_0x0583
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            if (r4 == 0) goto L_0x0583
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x04c0:
            if (r4 == 0) goto L_0x0583
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            if (r4 == 0) goto L_0x0583
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x04d4:
            if (r4 == 0) goto L_0x0583
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            if (r4 == 0) goto L_0x0583
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x04e8:
            java.lang.String r1 = "user_id"
            int r5 = r12.currentAccount     // Catch:{ Exception -> 0x0517 }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x0517 }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x0517 }
            r4.putInt(r1, r5)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x0504:
            if (r4 == 0) goto L_0x0583
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x0517 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0517 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x0517 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0517 }
            if (r4 == 0) goto L_0x0583
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x0517 }
            goto L_0x0583
        L_0x0517:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0583
        L_0x051c:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r4 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r4 == 0) goto L_0x0531
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
        L_0x0531:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0566
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x054d
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x054d
            r1 = 1
            goto L_0x054e
        L_0x054d:
            r1 = 0
        L_0x054e:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0567
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0567
            r1 = 0
            goto L_0x0567
        L_0x0566:
            r1 = 1
        L_0x0567:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x057e
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x057e
            r1 = 0
        L_0x057e:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            r4.setAllowOpenDrawer(r1, r3)
        L_0x0583:
            r12.checkLayout()
            r12.checkSystemBarColors()
            android.content.Intent r1 = r12.getIntent()
            if (r13 == 0) goto L_0x0591
            r13 = 1
            goto L_0x0592
        L_0x0591:
            r13 = 0
        L_0x0592:
            r12.handleIntent(r1, r3, r13, r3)
            java.lang.String r13 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x05a2
            java.lang.String r13 = r13.toLowerCase()     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05a3
        L_0x05a2:
            r13 = r3
        L_0x05a3:
            if (r1 == 0) goto L_0x05a9
            java.lang.String r3 = r13.toLowerCase()     // Catch:{ Exception -> 0x05f6 }
        L_0x05a9:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x05c9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r1.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r4 = "OS name "
            r1.append(r4)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r13)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r4 = " "
            r1.append(r4)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05f6 }
        L_0x05c9:
            boolean r13 = r13.contains(r0)     // Catch:{ Exception -> 0x05f6 }
            if (r13 != 0) goto L_0x05d5
            boolean r13 = r3.contains(r0)     // Catch:{ Exception -> 0x05f6 }
            if (r13 == 0) goto L_0x05fa
        L_0x05d5:
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05f6 }
            if (r13 > r6) goto L_0x05fa
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x05f6 }
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x05f6 }
            android.view.View r13 = r13.getDecorView()     // Catch:{ Exception -> 0x05f6 }
            android.view.View r13 = r13.getRootView()     // Catch:{ Exception -> 0x05f6 }
            android.view.ViewTreeObserver r0 = r13.getViewTreeObserver()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.ui.-$$Lambda$LaunchActivity$clA_1_2ZDPlec1UclLcdzLOtSA4 r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$clA_1_2ZDPlec1UclLcdzLOtSA4     // Catch:{ Exception -> 0x05f6 }
            r1.<init>(r13)     // Catch:{ Exception -> 0x05f6 }
            r12.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x05f6 }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05fa
        L_0x05f6:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x05fa:
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
                lambda$runLinkRequest$41$LaunchActivity(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$41$LaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$41$LaunchActivity(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$41$LaunchActivity(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$41$LaunchActivity(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$41$LaunchActivity(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$41$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$41$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$41$LaunchActivity(new ChatActivity(bundle3));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    public /* synthetic */ boolean lambda$onCreate$3$LaunchActivity(ItemTouchHelper itemTouchHelper, View view, int i) {
        if (!(view instanceof DrawerUserCell)) {
            return false;
        }
        final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
        if (accountNumber == this.currentAccount) {
            itemTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
            return false;
        }
        AnonymousClass6 r2 = new DialogsActivity((Bundle) null) {
            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                super.onTransitionAnimationEnd(z, z2);
                if (!z && z2) {
                    LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                }
            }

            /* access modifiers changed from: protected */
            public void onPreviewOpenAnimationEnd() {
                super.onPreviewOpenAnimationEnd();
                LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                LaunchActivity.this.switchToAccount(accountNumber, true);
            }
        };
        r2.setCurrentAccount(accountNumber);
        this.actionBarLayout.presentFragmentAsPreview(r2);
        this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(true);
        return true;
    }

    static /* synthetic */ void lambda$onCreate$4(View view) {
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
        lambda$runLinkRequest$41$LaunchActivity(new ProfileActivity(bundle));
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
            FrameLayout frameLayout2 = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout2.setVisibility(i);
        }
    }

    private void showUpdateActivity(int i, TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, boolean z) {
        if (this.blockingUpdateView == null) {
            AnonymousClass7 r0 = new BlockingUpdateView(this) {
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
                            LaunchActivity.AnonymousClass8.this.lambda$onAcceptTerms$0$LaunchActivity$8();
                        }
                    }).start();
                }

                public /* synthetic */ void lambda$onAcceptTerms$0$LaunchActivity$8() {
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
                    LaunchActivity.this.lambda$showPasscodeActivity$5$LaunchActivity();
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

    public /* synthetic */ void lambda$showPasscodeActivity$5$LaunchActivity() {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v226, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v231, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v156, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v158, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v273, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v276, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v277, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v278, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v280, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v281, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v282, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v167, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v296, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v280, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v281, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v306, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v282, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v284, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v285, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v338, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v295, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v344, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v297, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v301, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v306, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v371, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v380, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v382, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v384, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v310, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v385, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v386, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v407, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v316, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v317, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v411, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v321, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v324, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v325, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v326, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v428, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v327, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v442, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v479, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v339, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v137, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v142, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v342, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v250, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r13v5, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r4v4 */
    /* JADX WARNING: type inference failed for: r13v9 */
    /* JADX WARNING: type inference failed for: r15v19, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r20v1, types: [org.telegram.tgnet.TLRPC$TL_wallPaper] */
    /* JADX WARNING: type inference failed for: r1v44 */
    /* JADX WARNING: type inference failed for: r29v11 */
    /* JADX WARNING: type inference failed for: r4v108 */
    /* JADX WARNING: type inference failed for: r29v13 */
    /* JADX WARNING: type inference failed for: r1v73, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r30v24 */
    /* JADX WARNING: type inference failed for: r30v25 */
    /* JADX WARNING: type inference failed for: r1v107, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r40v15 */
    /* JADX WARNING: type inference failed for: r40v18 */
    /* JADX WARNING: type inference failed for: r30v32 */
    /* JADX WARNING: type inference failed for: r30v33 */
    /* JADX WARNING: type inference failed for: r30v34 */
    /* JADX WARNING: type inference failed for: r30v35 */
    /* JADX WARNING: type inference failed for: r30v36 */
    /* JADX WARNING: type inference failed for: r4v282 */
    /* JADX WARNING: type inference failed for: r13v136 */
    /* JADX WARNING: type inference failed for: r28v47 */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0292, code lost:
        if (r15.sendingText == null) goto L_0x018d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0113, code lost:
        r0 = r19.getIntent().getExtras();
        r7 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0125, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0128, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0129, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x118b, code lost:
        if (r1.intValue() == 0) goto L_0x118d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0146, code lost:
        if (r2.equals(r0) != false) goto L_0x014a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x13dc, code lost:
        r25[0] = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:?, code lost:
        switchToAccount(r25[0], true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x16a3, code lost:
        if (r3.checkCanOpenChat(r0, r4.get(r4.size() - r13)) != false) goto L_0x16a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x16b8, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x16ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x16bd, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1713, code lost:
        if (r3.checkCanOpenChat(r0, r4.get(r4.size() - r13)) != false) goto L_0x1715;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1728, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x16ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1a53, code lost:
        if ((r0.get(r0.size() - r13) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1a71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1a6f, code lost:
        if ((r0.get(r0.size() - r13) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1a71;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:766:0x143b */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x1ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:1007:0x1ae1  */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x1afa  */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1b09  */
    /* JADX WARNING: Removed duplicated region for block: B:1026:0x1b55  */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x1ba1  */
    /* JADX WARNING: Removed duplicated region for block: B:1039:0x1bad  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0299  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x03b4  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x093c A[SYNTHETIC, Splitter:B:388:0x093c] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x1324  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1366  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x1377  */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x137e  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0158 A[SYNTHETIC, Splitter:B:71:0x0158] */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x1444 A[SYNTHETIC, Splitter:B:771:0x1444] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0190  */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x163d  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x166d  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x16e0  */
    /* JADX WARNING: Removed duplicated region for block: B:927:0x1843  */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x1854  */
    /* JADX WARNING: Removed duplicated region for block: B:993:0x1a91  */
    /* JADX WARNING: Removed duplicated region for block: B:994:0x1a9a  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r60, boolean r61, boolean r62, boolean r63) {
        /*
            r59 = this;
            r15 = r59
            r14 = r60
            r0 = r62
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r59, r60)
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
            java.lang.String r1 = r60.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r60.getFlags()
            int[] r11 = new int[r13]
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r3 = "currentAccount"
            int r2 = r14.getIntExtra(r3, r2)
            r11[r12] = r2
            r2 = r11[r12]
            r15.switchToAccount(r2, r13)
            java.lang.String r2 = r60.getAction()
            if (r2 == 0) goto L_0x0073
            java.lang.String r2 = r60.getAction()
            java.lang.String r3 = "voip"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0073
            r23 = 1
            goto L_0x0075
        L_0x0073:
            r23 = 0
        L_0x0075:
            if (r63 != 0) goto L_0x0098
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r2 != 0) goto L_0x0081
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x0098
        L_0x0081:
            r59.showPasscodeActivity()
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            r2.saveConfig(r12)
            if (r23 != 0) goto L_0x0098
            r15.passcodeSaveIntent = r14
            r10 = r61
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x0098:
            r10 = r61
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
            if (r1 != 0) goto L_0x160f
            if (r14 == 0) goto L_0x160f
            java.lang.String r1 = r60.getAction()
            if (r1 == 0) goto L_0x160f
            if (r0 != 0) goto L_0x160f
            java.lang.String r0 = r60.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r2 = "hash"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x02b7
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0148
            if (r14 == 0) goto L_0x0148
            android.os.Bundle r0 = r60.getExtras()
            if (r0 == 0) goto L_0x0148
            android.os.Bundle r0 = r60.getExtras()
            java.lang.String r4 = "dialogId"
            long r16 = r0.getLong(r4, r5)
            int r0 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0136
            android.os.Bundle r0 = r60.getExtras()     // Catch:{ all -> 0x0130 }
            java.lang.String r4 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r4)     // Catch:{ all -> 0x0130 }
            if (r0 == 0) goto L_0x0134
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0130 }
            java.util.List r4 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r4)     // Catch:{ all -> 0x0130 }
            int r8 = r4.size()     // Catch:{ all -> 0x0130 }
            r7 = 0
        L_0x0101:
            if (r7 >= r8) goto L_0x0134
            java.lang.Object r19 = r4.get(r7)     // Catch:{ all -> 0x0130 }
            androidx.core.content.pm.ShortcutInfoCompat r19 = (androidx.core.content.pm.ShortcutInfoCompat) r19     // Catch:{ all -> 0x0130 }
            java.lang.String r13 = r19.getId()     // Catch:{ all -> 0x0130 }
            boolean r13 = r0.equals(r13)     // Catch:{ all -> 0x0130 }
            if (r13 == 0) goto L_0x012c
            android.content.Intent r0 = r19.getIntent()     // Catch:{ all -> 0x0130 }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x0130 }
            java.lang.String r4 = "dialogId"
            long r7 = r0.getLong(r4, r5)     // Catch:{ all -> 0x0130 }
            java.lang.String r0 = r0.getString(r2, r9)     // Catch:{ all -> 0x0128 }
            r16 = r7
            goto L_0x013e
        L_0x0128:
            r0 = move-exception
            r16 = r7
            goto L_0x0131
        L_0x012c:
            int r7 = r7 + 1
            r13 = 1
            goto L_0x0101
        L_0x0130:
            r0 = move-exception
        L_0x0131:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0134:
            r0 = r9
            goto L_0x013e
        L_0x0136:
            android.os.Bundle r0 = r60.getExtras()
            java.lang.String r0 = r0.getString(r2, r9)
        L_0x013e:
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash
            if (r2 == 0) goto L_0x0148
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x014a
        L_0x0148:
            r16 = r5
        L_0x014a:
            java.lang.String r0 = r60.getType()
            if (r0 == 0) goto L_0x0190
            java.lang.String r2 = "text/x-vcard"
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0190
            android.os.Bundle r1 = r60.getExtras()     // Catch:{ Exception -> 0x0189 }
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0189 }
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x0189 }
            if (r1 == 0) goto L_0x018d
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0189 }
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r9, r9)     // Catch:{ Exception -> 0x0189 }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0189 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0189 }
            r3 = 5
            if (r2 <= r3) goto L_0x0185
            r15.contactsToSend = r9     // Catch:{ Exception -> 0x0189 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0189 }
            r2.<init>()     // Catch:{ Exception -> 0x0189 }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x0189 }
            r2.add(r1)     // Catch:{ Exception -> 0x0189 }
            r15.documentsMimeType = r0     // Catch:{ Exception -> 0x0189 }
            goto L_0x0296
        L_0x0185:
            r15.contactsToSendUri = r1     // Catch:{ Exception -> 0x0189 }
            goto L_0x0296
        L_0x0189:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x018d:
            r0 = 1
            goto L_0x0297
        L_0x0190:
            java.lang.String r2 = "android.intent.extra.TEXT"
            java.lang.String r2 = r14.getStringExtra(r2)
            if (r2 != 0) goto L_0x01a4
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x01a4
            java.lang.String r2 = r4.toString()
        L_0x01a4:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x01db
            java.lang.String r7 = "http://"
            boolean r7 = r2.startsWith(r7)
            if (r7 != 0) goto L_0x01c0
            java.lang.String r7 = "https://"
            boolean r7 = r2.startsWith(r7)
            if (r7 == 0) goto L_0x01d8
        L_0x01c0:
            boolean r7 = android.text.TextUtils.isEmpty(r4)
            if (r7 != 0) goto L_0x01d8
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            r7.append(r1)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
        L_0x01d8:
            r15.sendingText = r2
            goto L_0x01e3
        L_0x01db:
            boolean r1 = android.text.TextUtils.isEmpty(r4)
            if (r1 != 0) goto L_0x01e3
            r15.sendingText = r4
        L_0x01e3:
            java.lang.String r1 = "android.intent.extra.STREAM"
            android.os.Parcelable r1 = r14.getParcelableExtra(r1)
            if (r1 == 0) goto L_0x0290
            boolean r2 = r1 instanceof android.net.Uri
            if (r2 != 0) goto L_0x01f7
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
        L_0x01f7:
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x0203
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)
            if (r2 == 0) goto L_0x0203
            r2 = 1
            goto L_0x0204
        L_0x0203:
            r2 = 0
        L_0x0204:
            if (r2 != 0) goto L_0x028e
            if (r1 == 0) goto L_0x023a
            if (r0 == 0) goto L_0x0212
            java.lang.String r4 = "image/"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0222
        L_0x0212:
            java.lang.String r4 = r1.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r4 = r4.endsWith(r7)
            if (r4 == 0) goto L_0x023a
        L_0x0222:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x022d
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x022d:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x028e
        L_0x023a:
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r4 == 0) goto L_0x027c
            java.lang.String r7 = "file:"
            boolean r7 = r4.startsWith(r7)
            if (r7 == 0) goto L_0x024e
            java.lang.String r7 = "file://"
            java.lang.String r4 = r4.replace(r7, r3)
        L_0x024e:
            if (r0 == 0) goto L_0x025b
            java.lang.String r3 = "video/"
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L_0x025b
            r15.videoPath = r4
            goto L_0x028e
        L_0x025b:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            if (r0 != 0) goto L_0x026d
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsPathsArray = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsOriginalPathsArray = r0
        L_0x026d:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            r0.add(r4)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x028e
        L_0x027c:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            if (r3 != 0) goto L_0x0287
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15.documentsUrisArray = r3
        L_0x0287:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            r3.add(r1)
            r15.documentsMimeType = r0
        L_0x028e:
            r0 = r2
            goto L_0x0297
        L_0x0290:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0296
            goto L_0x018d
        L_0x0296:
            r0 = 0
        L_0x0297:
            if (r0 == 0) goto L_0x02a2
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
        L_0x02a2:
            r49 = r5
            r0 = r9
            r4 = r0
            r24 = r4
            r6 = r11
            r1 = r14
            r2 = r15
            r5 = 0
            r7 = 0
            r8 = 0
            r10 = 0
            r11 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r18 = 0
            goto L_0x1627
        L_0x02b7:
            java.lang.String r0 = r60.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x03bf
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x03ad }
            java.lang.String r1 = r60.getType()     // Catch:{ Exception -> 0x03ad }
            if (r0 == 0) goto L_0x0301
            r2 = 0
        L_0x02d0:
            int r4 = r0.size()     // Catch:{ Exception -> 0x03ad }
            if (r2 >= r4) goto L_0x02fa
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x03ad }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x03ad }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x03ad }
            if (r7 != 0) goto L_0x02e8
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03ad }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x03ad }
        L_0x02e8:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x03ad }
            if (r4 == 0) goto L_0x02f7
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x03ad }
            if (r4 == 0) goto L_0x02f7
            r0.remove(r2)     // Catch:{ Exception -> 0x03ad }
            int r2 = r2 + -1
        L_0x02f7:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x02d0
        L_0x02fa:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x03ad }
            if (r2 == 0) goto L_0x0301
            r0 = r9
        L_0x0301:
            if (r0 == 0) goto L_0x03b1
            if (r1 == 0) goto L_0x0342
            java.lang.String r2 = "image/"
            boolean r2 = r1.startsWith(r2)     // Catch:{ Exception -> 0x03ad }
            if (r2 == 0) goto L_0x0342
            r1 = 0
        L_0x030e:
            int r2 = r0.size()     // Catch:{ Exception -> 0x03ad }
            if (r1 >= r2) goto L_0x03ab
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x03ad }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x03ad }
            boolean r3 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x03ad }
            if (r3 != 0) goto L_0x0326
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x03ad }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x03ad }
        L_0x0326:
            android.net.Uri r2 = (android.net.Uri) r2     // Catch:{ Exception -> 0x03ad }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x03ad }
            if (r3 != 0) goto L_0x0333
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x03ad }
            r3.<init>()     // Catch:{ Exception -> 0x03ad }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x03ad }
        L_0x0333:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x03ad }
            r3.<init>()     // Catch:{ Exception -> 0x03ad }
            r3.uri = r2     // Catch:{ Exception -> 0x03ad }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray     // Catch:{ Exception -> 0x03ad }
            r2.add(r3)     // Catch:{ Exception -> 0x03ad }
            int r1 = r1 + 1
            goto L_0x030e
        L_0x0342:
            r2 = 0
        L_0x0343:
            int r4 = r0.size()     // Catch:{ Exception -> 0x03ad }
            if (r2 >= r4) goto L_0x03ab
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x03ad }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x03ad }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x03ad }
            if (r7 != 0) goto L_0x035b
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03ad }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x03ad }
        L_0x035b:
            r7 = r4
            android.net.Uri r7 = (android.net.Uri) r7     // Catch:{ Exception -> 0x03ad }
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.getPath(r7)     // Catch:{ Exception -> 0x03ad }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03ad }
            if (r4 != 0) goto L_0x0369
            r4 = r8
        L_0x0369:
            if (r8 == 0) goto L_0x0396
            java.lang.String r7 = "file:"
            boolean r7 = r8.startsWith(r7)     // Catch:{ Exception -> 0x03ad }
            if (r7 == 0) goto L_0x0379
            java.lang.String r7 = "file://"
            java.lang.String r8 = r8.replace(r7, r3)     // Catch:{ Exception -> 0x03ad }
        L_0x0379:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x03ad }
            if (r7 != 0) goto L_0x038b
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x03ad }
            r7.<init>()     // Catch:{ Exception -> 0x03ad }
            r15.documentsPathsArray = r7     // Catch:{ Exception -> 0x03ad }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x03ad }
            r7.<init>()     // Catch:{ Exception -> 0x03ad }
            r15.documentsOriginalPathsArray = r7     // Catch:{ Exception -> 0x03ad }
        L_0x038b:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x03ad }
            r7.add(r8)     // Catch:{ Exception -> 0x03ad }
            java.util.ArrayList<java.lang.String> r7 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x03ad }
            r7.add(r4)     // Catch:{ Exception -> 0x03ad }
            goto L_0x03a8
        L_0x0396:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x03ad }
            if (r4 != 0) goto L_0x03a1
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x03ad }
            r4.<init>()     // Catch:{ Exception -> 0x03ad }
            r15.documentsUrisArray = r4     // Catch:{ Exception -> 0x03ad }
        L_0x03a1:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x03ad }
            r4.add(r7)     // Catch:{ Exception -> 0x03ad }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x03ad }
        L_0x03a8:
            int r2 = r2 + 1
            goto L_0x0343
        L_0x03ab:
            r0 = 0
            goto L_0x03b2
        L_0x03ad:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03b1:
            r0 = 1
        L_0x03b2:
            if (r0 == 0) goto L_0x160f
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
            goto L_0x160f
        L_0x03bf:
            java.lang.String r0 = r60.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x1514
            android.net.Uri r0 = r60.getData()
            if (r0 == 0) goto L_0x14d7
            java.lang.String r4 = r0.getScheme()
            java.lang.String r7 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r8 = "phone"
            if (r4 == 0) goto L_0x12dc
            r13 = -1
            int r5 = r4.hashCode()
            r6 = 3699(0xe73, float:5.183E-42)
            if (r5 == r6) goto L_0x0403
            r6 = 3213448(0x310888, float:4.503E-39)
            if (r5 == r6) goto L_0x03f9
            r6 = 99617003(0x5var_eb, float:2.2572767E-35)
            if (r5 == r6) goto L_0x03ef
            goto L_0x040c
        L_0x03ef:
            java.lang.String r5 = "https"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x040c
            r13 = 1
            goto L_0x040c
        L_0x03f9:
            java.lang.String r5 = "http"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x040c
            r13 = 0
            goto L_0x040c
        L_0x0403:
            java.lang.String r5 = "tg"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x040c
            r13 = 2
        L_0x040c:
            java.lang.String r5 = "thread"
            r19 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            if (r13 == 0) goto L_0x0e5b
            r6 = 1
            if (r13 == r6) goto L_0x0e5b
            r6 = 2
            if (r13 == r6) goto L_0x041f
            r26 = r4
            r25 = r11
        L_0x041c:
            r9 = 0
            goto L_0x12e0
        L_0x041f:
            java.lang.String r6 = r0.toString()
            java.lang.String r13 = "tg:resolve"
            boolean r13 = r6.startsWith(r13)
            java.lang.String r9 = "callback_url"
            java.lang.String r12 = "public_key"
            java.lang.String r10 = "bot_id"
            r25 = r11
            java.lang.String r11 = "payload"
            r26 = r4
            java.lang.String r4 = "scope"
            r27 = r5
            java.lang.String r5 = "tg://telegram.org"
            if (r13 != 0) goto L_0x0cfb
            java.lang.String r13 = "tg://resolve"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0447
            goto L_0x0cfb
        L_0x0447:
            java.lang.String r13 = "tg:privatepost"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x0c3f
            java.lang.String r13 = "tg://privatepost"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0459
            goto L_0x0c3f
        L_0x0459:
            java.lang.String r13 = "tg:bg"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x0ab2
            java.lang.String r13 = "tg://bg"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x046b
            goto L_0x0ab2
        L_0x046b:
            java.lang.String r13 = "tg:join"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x0a8b
            java.lang.String r13 = "tg://join"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x047d
            goto L_0x0a8b
        L_0x047d:
            java.lang.String r13 = "tg:addstickers"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x0a6f
            java.lang.String r13 = "tg://addstickers"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x048f
            goto L_0x0a6f
        L_0x048f:
            java.lang.String r13 = "tg:msg"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x09e5
            java.lang.String r13 = "tg://msg"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x09e5
            java.lang.String r13 = "tg://share"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x09e5
            java.lang.String r13 = "tg:share"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x04b1
            goto L_0x09e5
        L_0x04b1:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x09c8
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x04c3
            goto L_0x09c8
        L_0x04c3:
            java.lang.String r1 = "tg:login"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x096f
            java.lang.String r1 = "tg://login"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x04d5
            goto L_0x096f
        L_0x04d5:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0906
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x04e7
            goto L_0x0906
        L_0x04e7:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x08a2
            java.lang.String r1 = "tg://passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x08a2
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0501
            goto L_0x08a2
        L_0x0501:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0855
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0513
            goto L_0x0855
        L_0x0513:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x07fe
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0525
            goto L_0x07fe
        L_0x0525:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x074b
            java.lang.String r1 = "tg://settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0537
            goto L_0x074b
        L_0x0537:
            java.lang.String r1 = "tg:search"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0547
            java.lang.String r1 = "tg://search"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0580
        L_0x0547:
            boolean r1 = org.telegram.messenger.SharedConfig.assistantSupport
            if (r1 == 0) goto L_0x0580
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0567
            java.lang.String r3 = r0.trim()
        L_0x0567:
            r28 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            goto L_0x0cad
        L_0x0580:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0590
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x05b3
        L_0x0590:
            boolean r1 = org.telegram.messenger.SharedConfig.assistantSupport
            if (r1 == 0) goto L_0x05b3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 1
            goto L_0x0cb3
        L_0x05b3:
            java.lang.String r1 = "tg:call"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x05c3
            java.lang.String r1 = "tg://call"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x066b
        L_0x05c3:
            boolean r1 = org.telegram.messenger.SharedConfig.assistantSupport
            if (r1 == 0) goto L_0x066b
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x041c
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0604
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x05e6
            goto L_0x0604
        L_0x05e6:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r7)
            java.lang.String r1 = "extra_force_call"
            r3 = 1
            r0.putExtra(r1, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$0pGNt-F4NL_ebwO6GCF-txrXwLw r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$0pGNt-F4NL_ebwO6GCF-txrXwLw
            r1.<init>(r0)
            r3 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r3)
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            goto L_0x063c
        L_0x0604:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r4 = "name"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r5 = 0
            java.util.List r0 = r15.findContacts(r4, r0, r5)
            int r6 = r0.size()
            r9 = 1
            if (r6 != r9) goto L_0x0629
            java.lang.Object r0 = r0.get(r5)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            int r0 = r0.user_id
            goto L_0x062a
        L_0x0629:
            r0 = 0
        L_0x062a:
            if (r0 != 0) goto L_0x0630
            if (r4 == 0) goto L_0x0631
            r3 = r4
            goto L_0x0631
        L_0x0630:
            r3 = 0
        L_0x0631:
            java.lang.String r4 = "video"
            boolean r1 = r4.equalsIgnoreCase(r1)
            r4 = r1 ^ 1
            r5 = r1
            r1 = r0
            r0 = 1
        L_0x063c:
            r39 = r0
            r36 = r1
            r29 = r3
            r32 = r4
            r33 = r5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r30 = 0
            r31 = 0
            r34 = 0
            r35 = 0
            r37 = 0
            r38 = 0
            goto L_0x0cc3
        L_0x066b:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x067b
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x06a6
        L_0x067b:
            boolean r0 = org.telegram.messenger.SharedConfig.assistantSupport
            if (r0 == 0) goto L_0x06a6
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 1
            goto L_0x0cbb
        L_0x06a6:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x06b6
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x06f9
        L_0x06b6:
            boolean r0 = org.telegram.messenger.SharedConfig.assistantSupport
            if (r0 == 0) goto L_0x06f9
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r24 = r0
            r27 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 1
            goto L_0x0cb9
        L_0x06f9:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r6.replace(r0, r3)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r3)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0712
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r1)
        L_0x0712:
            r44 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            goto L_0x0d9e
        L_0x074b:
            java.lang.String r0 = "themes"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0770
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 2
            goto L_0x0cb1
        L_0x0770:
            java.lang.String r0 = "devices"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0795
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 3
            goto L_0x0cb1
        L_0x0795:
            java.lang.String r0 = "folders"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x07bb
            r0 = 4
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 4
            goto L_0x0cb1
        L_0x07bb:
            java.lang.String r0 = "change_number"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x07e1
            r0 = 5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 5
            goto L_0x0cb1
        L_0x07e1:
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1
            goto L_0x0cb1
        L_0x07fe:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r48 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r47 = 0
            goto L_0x131e
        L_0x0855:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r43 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            goto L_0x0d9c
        L_0x08a2:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x08e5
            java.lang.String r5 = "{"
            boolean r5 = r3.startsWith(r5)
            if (r5 == 0) goto L_0x08e5
            java.lang.String r5 = "}"
            boolean r5 = r3.endsWith(r5)
            if (r5 == 0) goto L_0x08e5
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "nonce"
            r1.put(r6, r5)
            goto L_0x08ec
        L_0x08e5:
            java.lang.String r5 = r0.getQueryParameter(r11)
            r1.put(r11, r5)
        L_0x08ec:
            java.lang.String r5 = r0.getQueryParameter(r10)
            r1.put(r10, r5)
            r1.put(r4, r3)
            java.lang.String r3 = r0.getQueryParameter(r12)
            r1.put(r12, r3)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r1.put(r9, r0)
            goto L_0x0d67
        L_0x0906:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "chat_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "message_id"
            java.lang.String r0 = r0.getQueryParameter(r4)
            if (r1 == 0) goto L_0x092f
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0938 }
            goto L_0x0939
        L_0x092f:
            if (r3 == 0) goto L_0x0938
            int r1 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x0938 }
            r3 = r1
            r1 = 0
            goto L_0x093a
        L_0x0938:
            r1 = 0
        L_0x0939:
            r3 = 0
        L_0x093a:
            if (r0 == 0) goto L_0x0941
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0941 }
            goto L_0x0942
        L_0x0941:
            r0 = 0
        L_0x0942:
            r38 = r0
            r36 = r1
            r37 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            goto L_0x0cc1
        L_0x096f:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "code"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r45 = r0
            r46 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            goto L_0x0da2
        L_0x09c8:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = r0.getQueryParameter(r8)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r3 = r1
            r1 = 0
            r4 = 0
            goto L_0x0aa5
        L_0x09e5:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r4 = "tg://msg"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg://share"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg:share"
            java.lang.String r0 = r0.replace(r4, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0a0a
            goto L_0x0a0b
        L_0x0a0a:
            r3 = r4
        L_0x0a0b:
            java.lang.String r4 = "text"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 == 0) goto L_0x0a41
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x0a2a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r1)
            java.lang.String r3 = r4.toString()
            r4 = 1
            goto L_0x0a2b
        L_0x0a2a:
            r4 = 0
        L_0x0a2b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            goto L_0x0a42
        L_0x0a41:
            r4 = 0
        L_0x0a42:
            int r0 = r3.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r5) goto L_0x0a52
            r0 = 16384(0x4000, float:2.2959E-41)
            r5 = 0
            java.lang.String r0 = r3.substring(r5, r0)
            goto L_0x0a54
        L_0x0a52:
            r5 = 0
            r0 = r3
        L_0x0a54:
            boolean r3 = r0.endsWith(r1)
            if (r3 == 0) goto L_0x0a65
            int r3 = r0.length()
            r6 = 1
            int r3 = r3 - r6
            java.lang.String r0 = r0.substring(r5, r3)
            goto L_0x0a54
        L_0x0a65:
            r1 = r0
            r11 = r4
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            goto L_0x0aaa
        L_0x0a6f:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r5 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            goto L_0x0aa6
        L_0x0a8b:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r4 = r0
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x0aa5:
            r5 = 0
        L_0x0aa6:
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
        L_0x0aaa:
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            goto L_0x0ca5
        L_0x0ab2:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r3 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r3.<init>()
            r1.settings = r3
            java.lang.String r3 = "slug"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
            if (r3 != 0) goto L_0x0ae0
            java.lang.String r3 = "color"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
        L_0x0ae0:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0afe
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x0afe
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0af9 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0af9 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0af9 }
            r3 = r3 | r19
            r0.background_color = r3     // Catch:{ Exception -> 0x0af9 }
        L_0x0af9:
            r3 = 0
            r1.slug = r3
            goto L_0x0CLASSNAME
        L_0x0afe:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0b5e
            int r3 = r3.length()
            r4 = 13
            if (r3 != r4) goto L_0x0b5e
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            r5 = 45
            if (r3 != r5) goto L_0x0b5e
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b41 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0b41 }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0b41 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0b41 }
            r4 = r4 | r19
            r3.background_color = r4     // Catch:{ Exception -> 0x0b41 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b41 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0b41 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0b41 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0b41 }
            r4 = r4 | r19
            r3.second_background_color = r4     // Catch:{ Exception -> 0x0b41 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b41 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0b41 }
        L_0x0b41:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0b59 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0b59 }
            if (r3 != 0) goto L_0x0b59
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0b59 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0b59 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0b59 }
            r3.rotation = r0     // Catch:{ Exception -> 0x0b59 }
        L_0x0b59:
            r3 = 0
            r1.slug = r3
            goto L_0x0CLASSNAME
        L_0x0b5e:
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x0b9b
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x0b9b
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0b9b
            r4 = 0
        L_0x0b76:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x0b9b
            r5 = r3[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0b89
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x0b98
        L_0x0b89:
            r6 = 1
            r5 = r3[r4]
            java.lang.String r9 = "motion"
            boolean r5 = r9.equals(r5)
            if (r5 == 0) goto L_0x0b98
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x0b98:
            int r4 = r4 + 1
            goto L_0x0b76
        L_0x0b9b:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r3.intensity = r4
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0be8 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0be8 }
            if (r4 != 0) goto L_0x0be8
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0be8 }
            r5 = 6
            r6 = 0
            java.lang.String r9 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x0be8 }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x0be8 }
            r6 = r9 | r19
            r4.background_color = r6     // Catch:{ Exception -> 0x0be8 }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0be8 }
            if (r4 <= r5) goto L_0x0be8
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0be8 }
            r5 = 7
            java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x0be8 }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0be8 }
            r3 = r3 | r19
            r4.second_background_color = r3     // Catch:{ Exception -> 0x0be8 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0be8 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0be8 }
        L_0x0be8:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0CLASSNAME }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0CLASSNAME }
            if (r3 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0CLASSNAME }
            r3.rotation = r0     // Catch:{ Exception -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            r47 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            goto L_0x0da4
        L_0x0c3f:
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r3 = "channel"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r4 = r1.intValue()
            if (r4 == 0) goto L_0x0CLASSNAME
            int r4 = r3.intValue()
            if (r4 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r13 = r27
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r13 = r27
            r1 = 0
            r3 = 0
        L_0x0CLASSNAME:
            java.lang.String r4 = r0.getQueryParameter(r13)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r5 = r4.intValue()
            if (r5 != 0) goto L_0x0CLASSNAME
            r4 = 0
        L_0x0CLASSNAME:
            java.lang.String r5 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r5)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r5 = r0.intValue()
            if (r5 != 0) goto L_0x0cc6
            r13 = r1
            r18 = r3
            r19 = r4
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
        L_0x0ca5:
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
        L_0x0cad:
            r29 = 0
            r30 = 0
        L_0x0cb1:
            r31 = 0
        L_0x0cb3:
            r32 = 0
            r33 = 0
            r34 = 0
        L_0x0cb9:
            r35 = 0
        L_0x0cbb:
            r36 = 0
            r37 = 0
            r38 = 0
        L_0x0cc1:
            r39 = 0
        L_0x0cc3:
            r40 = 0
            goto L_0x0cf5
        L_0x0cc6:
            r40 = r0
            r13 = r1
            r18 = r3
            r19 = r4
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x0cf5:
            r41 = 0
            r42 = 0
            goto L_0x0d9a
        L_0x0cfb:
            r13 = r27
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "telegrampassport"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L_0x0da8
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x0d48
            java.lang.String r5 = "{"
            boolean r5 = r3.startsWith(r5)
            if (r5 == 0) goto L_0x0d48
            java.lang.String r5 = "}"
            boolean r5 = r3.endsWith(r5)
            if (r5 == 0) goto L_0x0d48
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "nonce"
            r1.put(r6, r5)
            goto L_0x0d4f
        L_0x0d48:
            java.lang.String r5 = r0.getQueryParameter(r11)
            r1.put(r11, r5)
        L_0x0d4f:
            java.lang.String r5 = r0.getQueryParameter(r10)
            r1.put(r10, r5)
            r1.put(r4, r3)
            java.lang.String r3 = r0.getQueryParameter(r12)
            r1.put(r12, r3)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r1.put(r9, r0)
        L_0x0d67:
            r42 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
        L_0x0d9a:
            r43 = 0
        L_0x0d9c:
            r44 = 0
        L_0x0d9e:
            r45 = 0
            r46 = 0
        L_0x0da2:
            r47 = 0
        L_0x0da4:
            r48 = 0
            goto L_0x131e
        L_0x0da8:
            java.lang.String r3 = "start"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "startgroup"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "game"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "post"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
            int r9 = r6.intValue()
            if (r9 != 0) goto L_0x0dcb
            r6 = 0
        L_0x0dcb:
            java.lang.String r9 = r0.getQueryParameter(r13)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r10 = r9.intValue()
            if (r10 != 0) goto L_0x0dda
            r9 = 0
        L_0x0dda:
            java.lang.String r10 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r10)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r10 = r0.intValue()
            if (r10 != 0) goto L_0x0e1a
            r21 = r1
            r12 = r4
            r41 = r5
            r13 = r6
            r19 = r9
            r0 = 0
            r1 = 0
            r4 = 0
            r5 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r18 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            goto L_0x0e49
        L_0x0e1a:
            r40 = r0
            r21 = r1
            r12 = r4
            r41 = r5
            r13 = r6
            r19 = r9
            r0 = 0
            r1 = 0
            r4 = 0
            r5 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r18 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x0e49:
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r47 = 0
            r48 = 0
            r6 = r3
            r3 = 0
            goto L_0x131e
        L_0x0e5b:
            r26 = r4
            r13 = r5
            r25 = r11
            java.lang.String r4 = r0.getHost()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r5 = "telegram.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0e80
            java.lang.String r5 = "t.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0e80
            java.lang.String r5 = "telegram.dog"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x041c
        L_0x0e80:
            java.lang.String r4 = r0.getPath()
            if (r4 == 0) goto L_0x127e
            int r5 = r4.length()
            r6 = 1
            if (r5 <= r6) goto L_0x127e
            java.lang.String r4 = r4.substring(r6)
            java.lang.String r5 = "bg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0ff4
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "bg/"
            java.lang.String r3 = r4.replace(r5, r3)
            r1.slug = r3
            if (r3 == 0) goto L_0x0eca
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x0eca
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0ec4 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0ec4 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0ec4 }
            r3 = r3 | r19
            r0.background_color = r3     // Catch:{ Exception -> 0x0ec4 }
        L_0x0ec4:
            r3 = 0
            r1.slug = r3
            r9 = r3
            goto L_0x0fe0
        L_0x0eca:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0f2a
            int r3 = r3.length()
            r4 = 13
            if (r3 != r4) goto L_0x0f2a
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            r5 = 45
            if (r3 != r5) goto L_0x0f2a
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0f0d }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0f0d }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0f0d }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0f0d }
            r4 = r4 | r19
            r3.background_color = r4     // Catch:{ Exception -> 0x0f0d }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0f0d }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0f0d }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0f0d }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0f0d }
            r4 = r4 | r19
            r3.second_background_color = r4     // Catch:{ Exception -> 0x0f0d }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0f0d }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0f0d }
        L_0x0f0d:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0var_ }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0var_ }
            if (r3 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0var_ }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0var_ }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0var_ }
            r3.rotation = r0     // Catch:{ Exception -> 0x0var_ }
        L_0x0var_:
            r9 = 0
            r1.slug = r9
            goto L_0x0fe0
        L_0x0f2a:
            r9 = 0
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x0var_
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x0var_
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0var_
            r4 = 0
        L_0x0var_:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x0var_
            r5 = r3[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x0var_
        L_0x0var_:
            r6 = 1
            r5 = r3[r4]
            java.lang.String r10 = "motion"
            boolean r5 = r10.equals(r5)
            if (r5 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x0var_:
            int r4 = r4 + 1
            goto L_0x0var_
        L_0x0var_:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r3 = r3.intValue()
            r4.intensity = r3
            goto L_0x0var_
        L_0x0var_:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            r4 = 50
            r3.intensity = r4
        L_0x0var_:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0fc8 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0fc8 }
            if (r4 != 0) goto L_0x0fc3
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0fc8 }
            r5 = 6
            r6 = 0
            java.lang.String r10 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x0fc8 }
            r6 = 16
            int r10 = java.lang.Integer.parseInt(r10, r6)     // Catch:{ Exception -> 0x0fc8 }
            r6 = r10 | r19
            r4.background_color = r6     // Catch:{ Exception -> 0x0fc8 }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0fc8 }
            if (r4 <= r5) goto L_0x0fc8
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0fc8 }
            r5 = 7
            java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x0fc8 }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0fc8 }
            r3 = r3 | r19
            r4.second_background_color = r3     // Catch:{ Exception -> 0x0fc8 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fc8 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0fc8 }
            goto L_0x0fc8
        L_0x0fc3:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fc8 }
            r4 = -1
            r3.background_color = r4     // Catch:{ Exception -> 0x0fc8 }
        L_0x0fc8:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0fe0 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0fe0 }
            if (r3 != 0) goto L_0x0fe0
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fe0 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0fe0 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0fe0 }
            r3.rotation = r0     // Catch:{ Exception -> 0x0fe0 }
        L_0x0fe0:
            r22 = r1
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r24 = r21
            goto L_0x1016
        L_0x0ff4:
            r9 = 0
            java.lang.String r5 = "login/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x101f
            java.lang.String r0 = "login/"
            java.lang.String r3 = r4.replace(r0, r3)
            r21 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r22 = r19
        L_0x1014:
            r24 = r22
        L_0x1016:
            r27 = r24
            r28 = r27
            r29 = r28
            r10 = 2
            goto L_0x1299
        L_0x101f:
            java.lang.String r5 = "joinchat/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x103f
            java.lang.String r0 = "joinchat/"
            java.lang.String r3 = r4.replace(r0, r3)
            r5 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r6 = r4
            r11 = r6
        L_0x1034:
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
        L_0x103a:
            r21 = r19
        L_0x103c:
            r22 = r21
            goto L_0x1014
        L_0x103f:
            java.lang.String r5 = "addstickers/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x1055
            java.lang.String r0 = "addstickers/"
            java.lang.String r3 = r4.replace(r0, r3)
            r6 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r11 = r5
            goto L_0x1034
        L_0x1055:
            java.lang.String r5 = "msg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x11fe
            java.lang.String r5 = "share/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x1067
            goto L_0x11fe
        L_0x1067:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x1096
            java.lang.String r1 = r0.getQueryParameter(r8)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r3 = r1
            r4 = r9
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r22 = r21
            r24 = r22
            r27 = r24
            r28 = r27
            r29 = r28
            r10 = 2
            r30 = 0
            r1 = r0
            r0 = r29
            goto L_0x129b
        L_0x1096:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x10b2
            r0 = 12
            java.lang.String r3 = r4.substring(r0)
            r18 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r19 = r13
            goto L_0x103a
        L_0x10b2:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x10d1
            r0 = 9
            java.lang.String r3 = r4.substring(r0)
            r19 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r21 = r18
            goto L_0x103c
        L_0x10d1:
            java.lang.String r1 = "c/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x113e
            java.util.List r1 = r0.getPathSegments()
            int r3 = r1.size()
            r4 = 3
            if (r3 != r4) goto L_0x111f
            r3 = 1
            java.lang.Object r4 = r1.get(r3)
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r4)
            r10 = 2
            java.lang.Object r1 = r1.get(r10)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 == 0) goto L_0x110d
            int r4 = r3.intValue()
            if (r4 != 0) goto L_0x1107
            goto L_0x110d
        L_0x1107:
            r58 = r3
            r3 = r1
            r1 = r58
            goto L_0x110f
        L_0x110d:
            r1 = r9
            r3 = r1
        L_0x110f:
            java.lang.String r0 = r0.getQueryParameter(r13)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x1123
            r0 = r9
            goto L_0x1123
        L_0x111f:
            r10 = 2
            r0 = r9
            r1 = r0
            r3 = r1
        L_0x1123:
            r28 = r0
            r27 = r1
            r24 = r3
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r22 = r21
            r29 = r22
            goto L_0x1299
        L_0x113e:
            r10 = 2
            int r1 = r4.length()
            r3 = 1
            if (r1 < r3) goto L_0x1280
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r1.<init>(r3)
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x1168
            r3 = 0
            java.lang.Object r4 = r1.get(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r5 = "s"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x1169
            r1.remove(r3)
            goto L_0x1169
        L_0x1168:
            r3 = 0
        L_0x1169:
            int r4 = r1.size()
            if (r4 <= 0) goto L_0x118f
            java.lang.Object r4 = r1.get(r3)
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            int r4 = r1.size()
            r5 = 1
            if (r4 <= r5) goto L_0x118d
            java.lang.Object r1 = r1.get(r5)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x1191
        L_0x118d:
            r1 = r9
            goto L_0x1191
        L_0x118f:
            r1 = r9
            r3 = r1
        L_0x1191:
            java.lang.String r4 = "start"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "startgroup"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "game"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r11 = r0.getQueryParameter(r13)
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)
            int r12 = r11.intValue()
            if (r12 != 0) goto L_0x11b2
            r11 = r9
        L_0x11b2:
            java.lang.String r12 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r12)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r12 = r0.intValue()
            if (r12 != 0) goto L_0x11e0
            r24 = r1
            r12 = r5
            r13 = r6
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r18 = r6
            r19 = r18
            r21 = r19
            r22 = r21
            r27 = r22
            r29 = r27
            r28 = r11
            r30 = 0
            r11 = r4
            r4 = r3
            r3 = r29
            goto L_0x129b
        L_0x11e0:
            r29 = r0
            r24 = r1
            r12 = r5
            r13 = r6
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r18 = r6
            r19 = r18
            r21 = r19
            r22 = r21
            r27 = r22
            r28 = r11
            r30 = 0
            r11 = r4
            r4 = r3
            r3 = r27
            goto L_0x129b
        L_0x11fe:
            r10 = 2
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x1208
            goto L_0x1209
        L_0x1208:
            r3 = r4
        L_0x1209:
            java.lang.String r4 = "text"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 == 0) goto L_0x123f
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x1228
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r1)
            java.lang.String r3 = r4.toString()
            r4 = 1
            goto L_0x1229
        L_0x1228:
            r4 = 0
        L_0x1229:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            goto L_0x1240
        L_0x123f:
            r4 = 0
        L_0x1240:
            int r0 = r3.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r5) goto L_0x1250
            r0 = 16384(0x4000, float:2.2959E-41)
            r5 = 0
            java.lang.String r3 = r3.substring(r5, r0)
            goto L_0x1251
        L_0x1250:
            r5 = 0
        L_0x1251:
            boolean r0 = r3.endsWith(r1)
            if (r0 == 0) goto L_0x1262
            int r0 = r3.length()
            r6 = 1
            int r0 = r0 - r6
            java.lang.String r3 = r3.substring(r5, r0)
            goto L_0x1251
        L_0x1262:
            r0 = r3
            r30 = r4
            r1 = r9
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r22 = r21
            r24 = r22
            r27 = r24
            r28 = r27
            r29 = r28
            goto L_0x129b
        L_0x127e:
            r9 = 0
            r10 = 2
        L_0x1280:
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r11 = r6
            r12 = r11
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r22 = r21
            r24 = r22
            r27 = r24
            r28 = r27
            r29 = r28
        L_0x1299:
            r30 = 0
        L_0x129b:
            r42 = r9
            r44 = r42
            r46 = r44
            r41 = r13
            r43 = r18
            r48 = r19
            r45 = r21
            r47 = r22
            r13 = r24
            r18 = r27
            r19 = r28
            r40 = r29
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r21 = r4
            r4 = r5
            r5 = r6
            r24 = r46
            r27 = r24
            r28 = r27
            r29 = r28
            r6 = r11
            r11 = r30
            r30 = 0
            r58 = r1
            r1 = r0
            r0 = r58
            goto L_0x131e
        L_0x12dc:
            r26 = r4
            r25 = r11
        L_0x12e0:
            r10 = 2
            r0 = r9
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r12 = r6
            r13 = r12
            r18 = r13
            r19 = r18
            r21 = r19
            r24 = r21
            r27 = r24
            r28 = r27
            r29 = r28
            r40 = r29
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r47 = r46
            r48 = r47
            r11 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x131e:
            boolean r22 = r14.hasExtra(r7)
            if (r22 == 0) goto L_0x1366
            int r9 = r15.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            boolean r9 = r9.isClientActivated()
            if (r9 == 0) goto L_0x133e
            java.lang.String r9 = "tg"
            r10 = r26
            boolean r9 = r9.equals(r10)
            if (r9 == 0) goto L_0x133e
            if (r44 != 0) goto L_0x133e
            r9 = 1
            goto L_0x133f
        L_0x133e:
            r9 = 0
        L_0x133f:
            com.google.firebase.appindexing.builders.AssistActionBuilder r10 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r10.<init>()
            r22 = r2
            java.lang.String r2 = r14.getStringExtra(r7)
            r10.setActionToken(r2)
            if (r9 == 0) goto L_0x1352
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x1354
        L_0x1352:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x1354:
            r10.setActionStatus(r2)
            com.google.firebase.appindexing.Action r2 = r10.build()
            com.google.firebase.appindexing.FirebaseUserActions r9 = com.google.firebase.appindexing.FirebaseUserActions.getInstance()
            r9.end(r2)
            r14.removeExtra(r7)
            goto L_0x1368
        L_0x1366:
            r22 = r2
        L_0x1368:
            if (r45 != 0) goto L_0x137e
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x1377
            goto L_0x137e
        L_0x1377:
            r2 = r15
            r56 = r25
            r49 = 0
            goto L_0x14d0
        L_0x137e:
            if (r3 != 0) goto L_0x14b5
            if (r0 == 0) goto L_0x1384
            goto L_0x14b5
        L_0x1384:
            if (r21 != 0) goto L_0x1462
            if (r4 != 0) goto L_0x1462
            if (r5 != 0) goto L_0x1462
            if (r1 != 0) goto L_0x1462
            if (r41 != 0) goto L_0x1462
            if (r42 != 0) goto L_0x1462
            if (r44 != 0) goto L_0x1462
            if (r43 != 0) goto L_0x1462
            if (r45 != 0) goto L_0x1462
            if (r47 != 0) goto L_0x1462
            if (r18 != 0) goto L_0x1462
            if (r48 != 0) goto L_0x1462
            if (r46 == 0) goto L_0x13a0
            goto L_0x1462
        L_0x13a0:
            android.content.ContentResolver r49 = r59.getContentResolver()     // Catch:{ Exception -> 0x144f }
            android.net.Uri r50 = r60.getData()     // Catch:{ Exception -> 0x144f }
            r51 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            android.database.Cursor r1 = r49.query(r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x144f }
            if (r1 == 0) goto L_0x143e
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1430 }
            if (r0 == 0) goto L_0x143e
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1430 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1430 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x1430 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1430 }
            r2 = 0
            r8 = 3
        L_0x13d0:
            if (r2 >= r8) goto L_0x13ec
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x13ea }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x13ea }
            if (r3 != r0) goto L_0x13e6
            r3 = 0
            r25[r3] = r2     // Catch:{ all -> 0x13ea }
            r0 = r25[r3]     // Catch:{ all -> 0x13ea }
            r10 = 1
            r15.switchToAccount(r0, r10)     // Catch:{ all -> 0x142e }
            goto L_0x13ed
        L_0x13e6:
            r10 = 1
            int r2 = r2 + 1
            goto L_0x13d0
        L_0x13ea:
            r0 = move-exception
            goto L_0x1432
        L_0x13ec:
            r10 = 1
        L_0x13ed:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x142e }
            int r2 = r1.getInt(r0)     // Catch:{ all -> 0x142e }
            r3 = 0
            r0 = r25[r3]     // Catch:{ all -> 0x142e }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x142e }
            int r4 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x142e }
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x142e }
            r0.postNotificationName(r4, r5)     // Catch:{ all -> 0x142e }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x142a }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x142a }
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r3 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x142a }
            if (r3 == 0) goto L_0x141b
            r36 = r2
            r4 = 1
            goto L_0x1442
        L_0x141b:
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x142a }
            r36 = r2
            r4 = r32
            if (r0 == 0) goto L_0x1442
            r33 = 1
            goto L_0x1442
        L_0x142a:
            r0 = move-exception
            r36 = r2
            goto L_0x1433
        L_0x142e:
            r0 = move-exception
            goto L_0x1433
        L_0x1430:
            r0 = move-exception
            r8 = 3
        L_0x1432:
            r10 = 1
        L_0x1433:
            throw r0     // Catch:{ all -> 0x1434 }
        L_0x1434:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x143b
            r1.close()     // Catch:{ all -> 0x143b }
        L_0x143b:
            throw r2     // Catch:{ Exception -> 0x143c }
        L_0x143c:
            r0 = move-exception
            goto L_0x1452
        L_0x143e:
            r8 = 3
            r10 = 1
            r4 = r32
        L_0x1442:
            if (r1 == 0) goto L_0x144c
            r1.close()     // Catch:{ Exception -> 0x1448 }
            goto L_0x144c
        L_0x1448:
            r0 = move-exception
            r32 = r4
            goto L_0x1452
        L_0x144c:
            r32 = r4
            goto L_0x1455
        L_0x144f:
            r0 = move-exception
            r8 = 3
            r10 = 1
        L_0x1452:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1455:
            r2 = r15
            r56 = r25
            r9 = r28
            r13 = r30
            r12 = r36
            r49 = 0
            goto L_0x14f5
        L_0x1462:
            r8 = 3
            r10 = 1
            if (r1 == 0) goto L_0x1480
            java.lang.String r0 = "@"
            boolean r0 = r1.startsWith(r0)
            if (r0 == 0) goto L_0x1480
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = " "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x1481
        L_0x1480:
            r0 = r1
        L_0x1481:
            r20 = 0
            r2 = r25[r20]
            r22 = 0
            r1 = r59
            r3 = r21
            r49 = 0
            r9 = 2
            r7 = r12
            r12 = 3
            r8 = r0
            r9 = r11
            r16 = 1
            r10 = r13
            r13 = r25
            r11 = r18
            r12 = r19
            r56 = r13
            r13 = r40
            r14 = r41
            r15 = r42
            r16 = r43
            r17 = r44
            r18 = r45
            r19 = r46
            r20 = r47
            r21 = r48
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r2 = r59
            goto L_0x14d0
        L_0x14b5:
            r56 = r25
            r49 = 0
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r1.putString(r8, r3)
            r2 = r22
            r1.putString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$L8vBlDpxGOn0gaqYSzcUDCVQ3Pc r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$L8vBlDpxGOn0gaqYSzcUDCVQ3Pc
            r2 = r59
            r0.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x14d0:
            r9 = r28
            r13 = r30
            r12 = r36
            goto L_0x14f5
        L_0x14d7:
            r49 = r5
            r56 = r11
            r2 = r15
            r9 = 0
            r12 = 0
            r13 = 0
            r24 = 0
            r27 = 0
            r29 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x14f5:
            r1 = r60
            r7 = r12
            r57 = r13
            r4 = r27
            r0 = r29
            r12 = r32
            r5 = r33
            r8 = r37
            r11 = r38
            r55 = r39
            r16 = r49
            r6 = r56
            r10 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r18 = 0
            goto L_0x1631
        L_0x1514:
            r49 = r5
            r56 = r11
            r2 = r15
            java.lang.String r0 = r60.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1547
            r1 = r60
            r16 = r49
            r6 = r56
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r18 = 0
            r24 = 0
            r31 = 0
            r34 = 0
            r35 = 0
            r55 = 0
            r57 = 1
            goto L_0x1631
        L_0x1547:
            java.lang.String r0 = r60.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1569
            r1 = r60
            r16 = r49
            r6 = r56
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r18 = 1
            goto L_0x1625
        L_0x1569:
            java.lang.String r0 = r60.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x15d5
            java.lang.String r0 = "chatId"
            r1 = r60
            r3 = 0
            int r0 = r1.getIntExtra(r0, r3)
            java.lang.String r4 = "userId"
            int r4 = r1.getIntExtra(r4, r3)
            java.lang.String r5 = "encId"
            int r5 = r1.getIntExtra(r5, r3)
            if (r0 == 0) goto L_0x159f
            r6 = r56
            r4 = r6[r3]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r4.postNotificationName(r5, r7)
            r5 = 0
        L_0x159c:
            r12 = 0
        L_0x159d:
            r13 = 0
            goto L_0x15c9
        L_0x159f:
            r6 = r56
            if (r4 == 0) goto L_0x15b4
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r7)
            r12 = r4
            r0 = 0
            r5 = 0
            goto L_0x159d
        L_0x15b4:
            if (r5 == 0) goto L_0x15c5
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r7)
            r0 = 0
            goto L_0x159c
        L_0x15c5:
            r0 = 0
            r5 = 0
            r12 = 0
            r13 = 1
        L_0x15c9:
            r8 = r0
            r10 = r5
            r7 = r12
            r16 = r49
            r0 = 0
            r4 = 0
            r5 = 0
            r9 = 0
            r11 = 0
            r12 = 0
            goto L_0x1621
        L_0x15d5:
            r1 = r60
            r6 = r56
            r3 = 0
            java.lang.String r0 = r60.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x15f4
            r16 = r49
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 1
            goto L_0x1622
        L_0x15f4:
            java.lang.String r0 = r60.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1615
            r16 = r49
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 1
            goto L_0x1623
        L_0x160f:
            r49 = r5
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
        L_0x1615:
            r16 = r49
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x1621:
            r14 = 0
        L_0x1622:
            r15 = 0
        L_0x1623:
            r18 = 0
        L_0x1625:
            r24 = 0
        L_0x1627:
            r31 = 0
            r34 = 0
            r35 = 0
            r55 = 0
            r57 = 0
        L_0x1631:
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x1afa
            if (r9 == 0) goto L_0x1666
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.getLastFragment()
            r62 = r13
            boolean r13 = r3 instanceof org.telegram.ui.DialogsActivity
            if (r13 == 0) goto L_0x1663
            org.telegram.ui.DialogsActivity r3 = (org.telegram.ui.DialogsActivity) r3
            boolean r13 = r3.isMainDialogList()
            if (r13 == 0) goto L_0x1668
            android.view.View r13 = r3.getFragmentView()
            if (r13 == 0) goto L_0x165e
            r13 = 1
            r3.search(r9, r13)
            goto L_0x1669
        L_0x165e:
            r13 = 1
            r3.setInitialSearchString(r9)
            goto L_0x1669
        L_0x1663:
            r13 = 1
            r3 = 1
            goto L_0x166b
        L_0x1666:
            r62 = r13
        L_0x1668:
            r13 = 1
        L_0x1669:
            r3 = r62
        L_0x166b:
            if (r7 == 0) goto L_0x16e0
            if (r12 != 0) goto L_0x16c0
            if (r5 == 0) goto L_0x1672
            goto L_0x16c0
        L_0x1672:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "user_id"
            r0.putInt(r3, r7)
            if (r11 == 0) goto L_0x1683
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r11)
        L_0x1683:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x16a5
            r3 = 0
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r13
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r3 = r3.checkCanOpenChat(r0, r4)
            if (r3 == 0) goto L_0x16bd
        L_0x16a5:
            org.telegram.ui.ChatActivity r15 = new org.telegram.ui.ChatActivity
            r15.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            boolean r0 = r14.presentFragment(r15, r16, r17, r18, r19)
            if (r0 == 0) goto L_0x16bd
        L_0x16ba:
            r0 = 1
            goto L_0x174a
        L_0x16bd:
            r0 = 0
            goto L_0x174a
        L_0x16c0:
            if (r55 == 0) goto L_0x16da
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x16dd
            org.telegram.messenger.MessagesController r3 = r0.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r3, r5)
            goto L_0x16dd
        L_0x16da:
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r2, r7, r5)
        L_0x16dd:
            r4 = 0
            goto L_0x1afc
        L_0x16e0:
            if (r8 == 0) goto L_0x172b
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "chat_id"
            r0.putInt(r3, r8)
            if (r11 == 0) goto L_0x16f3
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r11)
        L_0x16f3:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x1715
            r3 = 0
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r13
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r3 = r3.checkCanOpenChat(r0, r4)
            if (r3 == 0) goto L_0x16bd
        L_0x1715:
            org.telegram.ui.ChatActivity r15 = new org.telegram.ui.ChatActivity
            r15.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            boolean r0 = r14.presentFragment(r15, r16, r17, r18, r19)
            if (r0 == 0) goto L_0x16bd
            goto L_0x16ba
        L_0x172b:
            if (r10 == 0) goto L_0x174f
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "enc_id"
            r0.putInt(r3, r10)
            org.telegram.ui.ChatActivity r15 = new org.telegram.ui.ChatActivity
            r15.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            boolean r0 = r14.presentFragment(r15, r16, r17, r18, r19)
        L_0x174a:
            r7 = r61
        L_0x174c:
            r4 = 0
            goto L_0x1aff
        L_0x174f:
            if (r3 == 0) goto L_0x178a
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x175d
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1788
        L_0x175d:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1788
        L_0x1767:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r13
            if (r0 <= 0) goto L_0x1781
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsStack
            r7 = 0
            java.lang.Object r3 = r3.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r3)
            goto L_0x1767
        L_0x1781:
            r7 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.closeLastFragment(r7)
            goto L_0x17ab
        L_0x1788:
            r7 = 0
            goto L_0x17ab
        L_0x178a:
            r7 = 0
            if (r14 == 0) goto L_0x17ad
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x17a9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r3 = new org.telegram.ui.Components.AudioPlayerAlert
            r3.<init>(r2)
            r0.showDialog(r3)
        L_0x17a9:
            r7 = r61
        L_0x17ab:
            r0 = 0
            goto L_0x174c
        L_0x17ad:
            if (r15 == 0) goto L_0x17d2
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x17a9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r3 = 0
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r3 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI
            r4.<init>(r6)
            r3.<init>(r2, r4)
            r0.showDialog(r3)
            goto L_0x17a9
        L_0x17d2:
            java.lang.String r3 = r2.videoPath
            if (r3 != 0) goto L_0x19bc
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r2.photoPathsArray
            if (r3 != 0) goto L_0x19bc
            java.lang.String r3 = r2.sendingText
            if (r3 != 0) goto L_0x19bc
            java.util.ArrayList<java.lang.String> r3 = r2.documentsPathsArray
            if (r3 != 0) goto L_0x19bc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r2.contactsToSend
            if (r3 != 0) goto L_0x19bc
            java.util.ArrayList<android.net.Uri> r3 = r2.documentsUrisArray
            if (r3 == 0) goto L_0x17ec
            goto L_0x19bc
        L_0x17ec:
            r3 = r57
            if (r3 == 0) goto L_0x185b
            if (r3 != r13) goto L_0x180c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            java.lang.String r4 = "user_id"
            r0.putInt(r4, r3)
            org.telegram.ui.ProfileActivity r3 = new org.telegram.ui.ProfileActivity
            r3.<init>(r0)
            r0 = r3
        L_0x180a:
            r4 = 0
            goto L_0x1835
        L_0x180c:
            r6 = 2
            if (r3 != r6) goto L_0x1816
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r4 = 0
            r0.<init>(r4)
            goto L_0x1835
        L_0x1816:
            r4 = 0
            r5 = 3
            if (r3 != r5) goto L_0x1820
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r4)
            goto L_0x180a
        L_0x1820:
            r0 = 4
            if (r3 != r0) goto L_0x1829
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x180a
        L_0x1829:
            r0 = 5
            if (r3 != r0) goto L_0x1833
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r0.<init>(r5)
            r4 = 1
            goto L_0x1835
        L_0x1833:
            r0 = 0
            goto L_0x180a
        L_0x1835:
            org.telegram.ui.-$$Lambda$LaunchActivity$A9yfTko94mxgfWqCwJe1zHaB104 r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$A9yfTko94mxgfWqCwJe1zHaB104
            r3.<init>(r0, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1854
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x1854:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x185b:
            r6 = 2
            if (r18 == 0) goto L_0x189c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "destroyAfterSelect"
            r0.putBoolean(r3, r13)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            org.telegram.ui.ContactsActivity r15 = new org.telegram.ui.ContactsActivity
            r15.<init>(r0)
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1891
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x1891:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
        L_0x1897:
            r7 = r61
            r0 = 1
            goto L_0x174c
        L_0x189c:
            if (r0 == 0) goto L_0x18f9
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.lang.String r4 = "destroyAfterSelect"
            r3.putBoolean(r4, r13)
            java.lang.String r4 = "returnAsResult"
            r3.putBoolean(r4, r13)
            java.lang.String r4 = "onlyUsers"
            r3.putBoolean(r4, r13)
            java.lang.String r4 = "allowSelf"
            r6 = 0
            r3.putBoolean(r4, r6)
            org.telegram.ui.ContactsActivity r15 = new org.telegram.ui.ContactsActivity
            r15.<init>(r3)
            r15.setInitialSearchString(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$dG45nLUP3_u3K1u3Kx2q1lw3kQQ r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$dG45nLUP3_u3K1u3Kx2q1lw3kQQ
            r0.<init>(r5)
            r15.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r14.getLastFragment()
            boolean r0 = r0 instanceof org.telegram.ui.ContactsActivity
            r17 = 1
            r18 = 1
            r19 = 0
            r16 = r0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x18f2
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x18f2:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x18f9:
            if (r35 == 0) goto L_0x1936
            org.telegram.ui.ActionIntroActivity r15 = new org.telegram.ui.ActionIntroActivity
            r0 = 5
            r15.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$iXtyxUsVAmKXdkikZfice1fmz-w r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$iXtyxUsVAmKXdkikZfice1fmz-w
            r0.<init>(r15)
            r15.setQrLoginDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x192e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x192e:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x1936:
            r3 = 0
            if (r34 == 0) goto L_0x1988
            org.telegram.ui.NewContactActivity r15 = new org.telegram.ui.NewContactActivity
            r15.<init>()
            if (r4 == 0) goto L_0x1952
            java.lang.String r0 = " "
            java.lang.String[] r0 = r4.split(r0, r6)
            r4 = r0[r3]
            int r3 = r0.length
            if (r3 <= r13) goto L_0x194e
            r0 = r0[r13]
            goto L_0x194f
        L_0x194e:
            r0 = 0
        L_0x194f:
            r15.setInitialName(r4, r0)
        L_0x1952:
            if (r24 == 0) goto L_0x195b
            java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r24)
            r15.setInitialPhoneNumber(r0)
        L_0x195b:
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1980
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x1980:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x1988:
            if (r31 == 0) goto L_0x16dd
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            org.telegram.ui.CallLogActivity r15 = new org.telegram.ui.CallLogActivity
            r15.<init>()
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x19b4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1897
        L_0x19b4:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x19bc:
            r3 = 0
            r5 = 3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x19d1
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r6)
        L_0x19d1:
            int r0 = (r16 > r49 ? 1 : (r16 == r49 ? 0 : -1))
            if (r0 != 0) goto L_0x1ae8
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "onlySelect"
            r0.putBoolean(r3, r13)
            java.lang.String r3 = "dialogsType"
            r0.putInt(r3, r5)
            java.lang.String r3 = "allowSwitchAccount"
            r0.putBoolean(r3, r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r2.contactsToSend
            if (r3 == 0) goto L_0x1a10
            int r3 = r3.size()
            if (r3 == r13) goto L_0x1a2c
            r3 = 2131626931(0x7f0e0bb3, float:1.8881112E38)
            java.lang.String r4 = "SendContactToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "selectAlertString"
            r0.putString(r4, r3)
            r3 = 2131626908(0x7f0e0b9c, float:1.8881065E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "selectAlertStringGroup"
            r0.putString(r4, r3)
            goto L_0x1a2c
        L_0x1a10:
            r3 = 2131626931(0x7f0e0bb3, float:1.8881112E38)
            java.lang.String r4 = "SendMessagesToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "selectAlertString"
            r0.putString(r4, r3)
            r3 = 2131626930(0x7f0e0bb2, float:1.888111E38)
            java.lang.String r4 = "SendMessagesToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "selectAlertStringGroup"
            r0.putString(r4, r3)
        L_0x1a2c:
            org.telegram.ui.DialogsActivity r15 = new org.telegram.ui.DialogsActivity
            r15.<init>(r0)
            r15.setDelegate(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1a56
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x1a73
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r13
            java.lang.Object r0 = r0.get(r3)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1a73
            goto L_0x1a71
        L_0x1a56:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r13) goto L_0x1a73
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r13
            java.lang.Object r0 = r0.get(r3)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1a73
        L_0x1a71:
            r0 = 1
            goto L_0x1a74
        L_0x1a73:
            r0 = 0
        L_0x1a74:
            r16 = r0
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r2.actionBarLayout
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x1a9a
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1a9a
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r3 = 0
            r0.closePhoto(r3, r3)
            goto L_0x1aca
        L_0x1a9a:
            r3 = 0
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x1ab3
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1ab3
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r3, r13)
            goto L_0x1aca
        L_0x1ab3:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x1aca
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1aca
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r3, r13)
        L_0x1aca:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1ae1
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x1897
        L_0x1ae1:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r13, r3)
            goto L_0x1897
        L_0x1ae8:
            r3 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r4 = java.lang.Long.valueOf(r16)
            r0.add(r4)
            r4 = 0
            r2.didSelectDialogs(r4, r0, r4, r3)
            goto L_0x1afc
        L_0x1afa:
            r4 = 0
            r13 = 1
        L_0x1afc:
            r7 = r61
            r0 = 0
        L_0x1aff:
            if (r0 != 0) goto L_0x1bab
            if (r7 != 0) goto L_0x1bab
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x1b55
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            boolean r3 = r3.isClientActivated()
            if (r3 != 0) goto L_0x1b30
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x1b96
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.layersActionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r3.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r2.drawerLayoutContainer
            r5 = 0
            r3.setAllowOpenDrawer(r5, r5)
            goto L_0x1b96
        L_0x1b30:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x1b96
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r3.setSideMenu(r5)
            if (r9 == 0) goto L_0x1b49
            r3.setInitialSearchString(r9)
        L_0x1b49:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r2.drawerLayoutContainer
            r5 = 0
            r3.setAllowOpenDrawer(r13, r5)
            goto L_0x1b96
        L_0x1b55:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x1b96
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            boolean r3 = r3.isClientActivated()
            if (r3 != 0) goto L_0x1b7c
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r3.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r2.drawerLayoutContainer
            r5 = 0
            r3.setAllowOpenDrawer(r5, r5)
            goto L_0x1b96
        L_0x1b7c:
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r3.setSideMenu(r5)
            if (r9 == 0) goto L_0x1b8b
            r3.setInitialSearchString(r9)
        L_0x1b8b:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r2.drawerLayoutContainer
            r5 = 0
            r3.setAllowOpenDrawer(r13, r5)
        L_0x1b96:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            r3.showLastFragment()
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x1bab
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.layersActionBarLayout
            r3.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.rightActionBarLayout
            r3.showLastFragment()
        L_0x1bab:
            if (r23 == 0) goto L_0x1bb0
            org.telegram.ui.VoIPFragment.show(r59)
        L_0x1bb0:
            r1.setAction(r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    public /* synthetic */ void lambda$handleIntent$7$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$41$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$9$LaunchActivity(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
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
                SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        lambda$runLinkRequest$41$LaunchActivity(locationActivity);
    }

    public /* synthetic */ void lambda$handleIntent$10$LaunchActivity(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    public /* synthetic */ void lambda$handleIntent$11$LaunchActivity(boolean z, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull);
    }

    public /* synthetic */ void lambda$handleIntent$15$LaunchActivity(ActionIntroActivity actionIntroActivity, String str) {
        AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new RequestDelegate(actionIntroActivity) {
            public final /* synthetic */ ActionIntroActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tLObject, this.f$1, tLRPC$TL_error) {
                    public final /* synthetic */ TLObject f$1;
                    public final /* synthetic */ ActionIntroActivity f$2;
                    public final /* synthetic */ TLRPC$TL_error f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        LaunchActivity.lambda$null$13(AlertDialog.this, this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$13(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!(tLObject instanceof TLRPC$TL_authorization)) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
                public final /* synthetic */ TLRPC$TL_error f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LaunchActivity.lambda$null$12(ActionIntroActivity.this, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$12(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(actionIntroActivity, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
    }

    private int runCommentRequest(int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            return 0;
        }
        TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage = new TLRPC$TL_messages_getDiscussionMessage();
        tLRPC$TL_messages_getDiscussionMessage.peer = MessagesController.getInputPeer(tLRPC$Chat);
        tLRPC$TL_messages_getDiscussionMessage.msg_id = (num2 != null ? num : num3).intValue();
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new RequestDelegate(i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, alertDialog) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ Integer f$2;
            public final /* synthetic */ TLRPC$Chat f$3;
            public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$4;
            public final /* synthetic */ Integer f$5;
            public final /* synthetic */ AlertDialog f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$runCommentRequest$17$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$runCommentRequest$17$LaunchActivity(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, alertDialog) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ Integer f$3;
            public final /* synthetic */ TLRPC$Chat f$4;
            public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$5;
            public final /* synthetic */ Integer f$6;
            public final /* synthetic */ AlertDialog f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$16$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$LaunchActivity(TLObject tLObject, int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, AlertDialog alertDialog) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_messages_discussionMessage) {
            TLRPC$TL_messages_discussionMessage tLRPC$TL_messages_discussionMessage = (TLRPC$TL_messages_discussionMessage) tLObject2;
            MessagesController.getInstance(i).putUsers(tLRPC$TL_messages_discussionMessage.users, false);
            MessagesController.getInstance(i).putChats(tLRPC$TL_messages_discussionMessage.chats, false);
            ArrayList arrayList = new ArrayList();
            int size = tLRPC$TL_messages_discussionMessage.messages.size();
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(new MessageObject(UserConfig.selectedAccount, tLRPC$TL_messages_discussionMessage.messages.get(i2), true, true));
            }
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", (int) (-((MessageObject) arrayList.get(0)).getDialogId()));
            bundle.putInt("message_id", Math.max(1, num.intValue()));
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setReplyMessages(arrayList, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage.msg_id, tLRPC$TL_messages_discussionMessage.read_inbox_max_id, tLRPC$TL_messages_discussionMessage.read_outbox_max_id);
            if (num2 != null) {
                chatActivity.setHighlightMessageId(num2.intValue());
            }
            lambda$runLinkRequest$41$LaunchActivity(chatActivity);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x02d1  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x03d3  */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, boolean r31, java.lang.Integer r32, java.lang.Integer r33, java.lang.Integer r34, java.lang.Integer r35, java.lang.String r36, java.util.HashMap<java.lang.String, java.lang.String> r37, java.lang.String r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, org.telegram.tgnet.TLRPC$TL_wallPaper r42, java.lang.String r43, int r44) {
        /*
            r23 = this;
            r15 = r23
            r12 = r24
            r0 = r25
            r5 = r26
            r6 = r27
            r9 = r30
            r13 = r33
            r14 = r37
            r11 = r38
            r10 = r39
            r8 = r42
            r7 = r43
            r1 = r44
            r2 = 2
            if (r1 != 0) goto L_0x0062
            int r3 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r3 < r2) goto L_0x0062
            if (r14 == 0) goto L_0x0062
            org.telegram.ui.-$$Lambda$LaunchActivity$K4usk08dZlGtjl_i1wLn-5uek5M r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$K4usk08dZlGtjl_i1wLn-5uek5M
            r1 = r4
            r2 = r23
            r3 = r24
            r12 = r4
            r4 = r25
            r5 = r26
            r6 = r27
            r0 = r7
            r7 = r28
            r8 = r29
            r9 = r30
            r10 = r31
            r11 = r32
            r0 = r12
            r12 = r33
            r13 = r34
            r14 = r35
            r15 = r36
            r16 = r37
            r17 = r38
            r18 = r39
            r19 = r40
            r20 = r41
            r21 = r42
            r22 = r43
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r14 = r23
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r14, r0)
            r0.show()
            return
        L_0x0062:
            r3 = r7
            r14 = r15
            r4 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r7 = "OK"
            r15 = 0
            r8 = 1
            r11 = 0
            if (r40 == 0) goto L_0x00b8
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r1)
            if (r0 == 0) goto L_0x0088
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r40
            r0.postNotificationName(r1, r2)
            goto L_0x00b7
        L_0x0088:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r40
            java.lang.String r3 = "OtherLoginCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r15)
            r14.showAlertDialog(r0)
        L_0x00b7:
            return
        L_0x00b8:
            if (r41 == 0) goto L_0x00e2
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624364(0x7f0e01ac, float:1.8875906E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r2 = "AuthAnotherClientUrl"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r15)
            r14.showAlertDialog(r0)
            return
        L_0x00e2:
            org.telegram.ui.ActionBar.AlertDialog r10 = new org.telegram.ui.ActionBar.AlertDialog
            r4 = 3
            r10.<init>(r14, r4)
            int[] r7 = new int[r8]
            r7[r11] = r11
            if (r0 == 0) goto L_0x0127
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r13 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r13.<init>()
            r13.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$5y8IdItD-EpMjRNAkdtOuLXbG7U r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$5y8IdItD-EpMjRNAkdtOuLXbG7U
            r1 = r9
            r2 = r23
            r3 = r36
            r4 = r24
            r5 = r32
            r6 = r35
            r8 = r7
            r7 = r34
            r16 = r8
            r15 = r9
            r9 = r10
            r12 = r10
            r10 = r29
            r25 = r12
            r12 = 0
            r11 = r28
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r0 = r0.sendRequest(r13, r15)
            r7 = r16
            r7[r12] = r0
            r10 = r24
            r11 = r25
        L_0x0124:
            r4 = 0
            goto L_0x03ce
        L_0x0127:
            r25 = r10
            r12 = 0
            if (r5 == 0) goto L_0x0163
            if (r1 != 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$ufFX9KV28t1jczdFtW8HFMBG1g0 r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$ufFX9KV28t1jczdFtW8HFMBG1g0
            r10 = r24
            r11 = r25
            r3.<init>(r10, r11, r5)
            int r0 = r1.sendRequest(r0, r3, r2)
            r7[r12] = r0
            goto L_0x0124
        L_0x0149:
            r10 = r24
            r11 = r25
            if (r1 != r8) goto L_0x0124
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$IASpwFX1eizagiGSWTf2WJg6NtQ r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$IASpwFX1eizagiGSWTf2WJg6NtQ
            r3.<init>(r10, r11)
            r1.sendRequest(r0, r3, r2)
            goto L_0x0124
        L_0x0163:
            r10 = r24
            r11 = r25
            if (r6 == 0) goto L_0x01c1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01c0
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x01aa
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r4 = 0
            org.telegram.ui.Components.ChatActivityEnterView r5 = r2.getChatActivityEnterView()
            r24 = r3
            r25 = r23
            r26 = r1
            r27 = r0
            r28 = r4
            r29 = r5
            r24.<init>(r25, r26, r27, r28, r29)
            boolean r0 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r0)
            goto L_0x01bd
        L_0x01aa:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r2 = 0
            r4 = 0
            r24 = r3
            r25 = r23
            r26 = r1
            r27 = r0
            r28 = r2
            r29 = r4
            r24.<init>(r25, r26, r27, r28, r29)
        L_0x01bd:
            r1.showDialog(r3)
        L_0x01c0:
            return
        L_0x01c1:
            if (r9 == 0) goto L_0x01e6
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r8)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r4)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$_92WeWM5qBULcd8vg_-sWE-exWk r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$_92WeWM5qBULcd8vg_-sWE-exWk
            r2 = r31
            r0.<init>(r2, r10, r9)
            r1.setDelegate(r0)
            r14.presentFragment(r1, r12, r8)
            goto L_0x0124
        L_0x01e6:
            r0 = r37
            if (r0 == 0) goto L_0x0253
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x01fd
            return
        L_0x01fd:
            java.lang.String r2 = "payload"
            java.lang.Object r2 = r0.get(r2)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r3 = "nonce"
            java.lang.Object r3 = r0.get(r3)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r4 = "callback_url"
            java.lang.Object r4 = r0.get(r4)
            java.lang.String r4 = (java.lang.String) r4
            org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm r5 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm
            r5.<init>()
            r5.bot_id = r1
            java.lang.String r1 = "scope"
            java.lang.Object r1 = r0.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            r5.scope = r1
            java.lang.String r1 = "public_key"
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            r5.public_key = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$0BAduz7008D0BsSlehF8Na4ckuQ r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$0BAduz7008D0BsSlehF8Na4ckuQ
            r25 = r1
            r26 = r23
            r27 = r7
            r28 = r24
            r29 = r11
            r30 = r5
            r31 = r2
            r32 = r3
            r33 = r4
            r25.<init>(r27, r28, r29, r30, r31, r32, r33)
            int r0 = r0.sendRequest(r5, r1)
            r7[r12] = r0
            goto L_0x0124
        L_0x0253:
            r0 = r39
            if (r0 == 0) goto L_0x0271
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$L280iTFOnLjHPpDzjzrzo3zIWt8 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$L280iTFOnLjHPpDzjzrzo3zIWt8
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r12] = r0
            goto L_0x0124
        L_0x0271:
            java.lang.String r0 = "android"
            r1 = r38
            if (r1 == 0) goto L_0x0293
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r2 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r2.<init>()
            r2.lang_code = r1
            r2.lang_pack = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$RO25ivkUxQifnIeOsrtyq2qoytY r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$RO25ivkUxQifnIeOsrtyq2qoytY
            r1.<init>(r11)
            int r0 = r0.sendRequest(r2, r1)
            r7[r12] = r0
            goto L_0x0124
        L_0x0293:
            r1 = r42
            if (r1 == 0) goto L_0x02f4
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x02cd
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x02c7 }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x02c7 }
            int r3 = r3.background_color     // Catch:{ Exception -> 0x02c7 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x02c7 }
            int r4 = r4.second_background_color     // Catch:{ Exception -> 0x02c7 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x02c7 }
            int r5 = r5.rotation     // Catch:{ Exception -> 0x02c7 }
            int r5 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r5, r12)     // Catch:{ Exception -> 0x02c7 }
            r0.<init>(r2, r3, r4, r5)     // Catch:{ Exception -> 0x02c7 }
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x02c7 }
            r4 = 0
            r2.<init>(r0, r4)     // Catch:{ Exception -> 0x02c5 }
            org.telegram.ui.-$$Lambda$LaunchActivity$d5dCWln30riABRcT2k05WFvBzaQ r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$d5dCWln30riABRcT2k05WFvBzaQ     // Catch:{ Exception -> 0x02c5 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x02c5 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x02c5 }
            goto L_0x02cf
        L_0x02c5:
            r0 = move-exception
            goto L_0x02c9
        L_0x02c7:
            r0 = move-exception
            r4 = 0
        L_0x02c9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02ce
        L_0x02cd:
            r4 = 0
        L_0x02ce:
            r8 = 0
        L_0x02cf:
            if (r8 != 0) goto L_0x03ce
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$NP3KRtzUCQ-Ei7EicnaiTOoT9Ck r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$NP3KRtzUCQ-Ei7EicnaiTOoT9Ck
            r3.<init>(r11, r1)
            int r0 = r2.sendRequest(r0, r3)
            r7[r12] = r0
            goto L_0x03ce
        L_0x02f4:
            r4 = 0
            if (r3 == 0) goto L_0x031f
            org.telegram.ui.-$$Lambda$LaunchActivity$zlMR0i2S0SNMK4VW32V0u_zMy4E r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$zlMR0i2S0SNMK4VW32V0u_zMy4E
            r15.<init>()
            org.telegram.tgnet.TLRPC$TL_account_getTheme r1 = new org.telegram.tgnet.TLRPC$TL_account_getTheme
            r1.<init>()
            r1.format = r0
            org.telegram.tgnet.TLRPC$TL_inputThemeSlug r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug
            r0.<init>()
            r0.slug = r3
            r1.theme = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$zRCF_YBECUWZ2M1nWS4EqX-jOm8 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$zRCF_YBECUWZ2M1nWS4EqX-jOm8
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r12] = r0
            goto L_0x03cf
        L_0x031f:
            if (r13 == 0) goto L_0x03ce
            if (r32 == 0) goto L_0x03ce
            if (r34 == 0) goto L_0x037c
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r24)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r13)
            if (r0 == 0) goto L_0x0345
            r25 = r23
            r26 = r24
            r27 = r11
            r28 = r32
            r29 = r35
            r30 = r34
            r31 = r0
            int r0 = r25.runCommentRequest(r26, r27, r28, r29, r30, r31)
            r7[r12] = r0
            goto L_0x03ce
        L_0x0345:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r0 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            int r2 = r33.intValue()
            r1.channel_id = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r2 = r0.id
            r2.add(r1)
            int r1 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$Itm93ccUzPWY4WZV_GS-v8hCUBo r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$Itm93ccUzPWY4WZV_GS-v8hCUBo
            r36 = r2
            r37 = r23
            r38 = r7
            r39 = r24
            r40 = r11
            r41 = r32
            r42 = r35
            r43 = r34
            r36.<init>(r38, r39, r40, r41, r42, r43)
            int r0 = r1.sendRequest(r0, r2)
            r7[r12] = r0
            goto L_0x03ce
        L_0x037c:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r33.intValue()
            java.lang.String r2 = "chat_id"
            r0.putInt(r2, r1)
            int r1 = r32.intValue()
            java.lang.String r2 = "message_id"
            r0.putInt(r2, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x03a9
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03aa
        L_0x03a9:
            r1 = r4
        L_0x03aa:
            if (r1 == 0) goto L_0x03b6
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r24)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x03ce
        L_0x03b6:
            org.telegram.ui.-$$Lambda$LaunchActivity$b1mRvfAsITmKQRblBBwB7EQRCNM r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$b1mRvfAsITmKQRblBBwB7EQRCNM
            r25 = r2
            r26 = r23
            r27 = r0
            r28 = r33
            r29 = r7
            r30 = r11
            r31 = r1
            r32 = r24
            r25.<init>(r27, r28, r29, r30, r31, r32)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x03ce:
            r15 = r4
        L_0x03cf:
            r0 = r7[r12]
            if (r0 == 0) goto L_0x03e0
            org.telegram.ui.-$$Lambda$LaunchActivity$U2ux5LFnYK5eHvOgzfKRPuB5514 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$U2ux5LFnYK5eHvOgzfKRPuB5514
            r0.<init>(r10, r7, r15)
            r11.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r11.showDelayed(r0)     // Catch:{ Exception -> 0x03e0 }
        L_0x03e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, Integer num3, Integer num4, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, num3, num4, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$24$LaunchActivity(String str, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, str, i, num, num2, num3, iArr, alertDialog, str2, str3) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ String f$10;
            public final /* synthetic */ String f$11;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ Integer f$5;
            public final /* synthetic */ Integer f$6;
            public final /* synthetic */ Integer f$7;
            public final /* synthetic */ int[] f$8;
            public final /* synthetic */ AlertDialog f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
                this.f$10 = r11;
                this.f$11 = r12;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$23$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r0v13, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r0v60 */
    /* JADX WARNING: type inference failed for: r0v61 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006f, code lost:
        if (r22[0] != 0) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00d2, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00f1, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00d4;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0165  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$23$LaunchActivity(org.telegram.tgnet.TLObject r15, org.telegram.tgnet.TLRPC$TL_error r16, java.lang.String r17, int r18, java.lang.Integer r19, java.lang.Integer r20, java.lang.Integer r21, int[] r22, org.telegram.ui.ActionBar.AlertDialog r23, java.lang.String r24, java.lang.String r25) {
        /*
            r14 = this;
            r8 = r14
            r0 = r17
            r2 = r18
            r9 = r23
            r1 = r24
            r3 = r25
            boolean r4 = r14.isFinishing()
            if (r4 != 0) goto L_0x02cc
            r4 = r15
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r4 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r4
            r10 = 1
            r11 = 0
            if (r16 != 0) goto L_0x02ac
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.actionBarLayout
            if (r5 == 0) goto L_0x02ac
            if (r0 == 0) goto L_0x0028
            if (r0 == 0) goto L_0x02ac
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x02ac
        L_0x0028:
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r4.users
            r5.putUsers(r6, r11)
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r4.chats
            r5.putChats(r6, r11)
            org.telegram.messenger.MessagesStorage r5 = org.telegram.messenger.MessagesStorage.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r4.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r4.chats
            r5.putUsersAndChats(r6, r7, r11, r10)
            if (r19 == 0) goto L_0x0074
            if (r20 != 0) goto L_0x004b
            if (r21 == 0) goto L_0x0074
        L_0x004b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0074
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r4.chats
            java.lang.Object r0 = r0.get(r11)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r14
            r2 = r18
            r3 = r23
            r4 = r19
            r5 = r20
            r6 = r21
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r22[r11] = r0
            r0 = r22[r11]
            if (r0 == 0) goto L_0x02c1
        L_0x0071:
            r10 = 0
            goto L_0x02c1
        L_0x0074:
            java.lang.String r5 = "dialogsType"
            java.lang.String r6 = "onlySelect"
            if (r0 == 0) goto L_0x016c
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r1.putBoolean(r6, r10)
            java.lang.String r3 = "cantSendToChannels"
            r1.putBoolean(r3, r10)
            r1.putInt(r5, r10)
            r3 = 2131626914(0x7f0e0ba2, float:1.8881078E38)
            java.lang.String r5 = "SendGameToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertString"
            r1.putString(r5, r3)
            r3 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            java.lang.String r5 = "SendGameToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertStringGroup"
            r1.putString(r5, r3)
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$c9VrJluMPJpxrV7ypCFe0xr5zSw r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$c9VrJluMPJpxrV7ypCFe0xr5zSw
            r1.<init>(r0, r2, r4)
            r3.setDelegate(r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x00d8
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00d6
        L_0x00d4:
            r0 = 1
            goto L_0x00f4
        L_0x00d6:
            r0 = 0
            goto L_0x00f4
        L_0x00d8:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r10) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00d6
            goto L_0x00d4
        L_0x00f4:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r2 = 1
            r4 = 1
            r5 = 0
            r15 = r1
            r16 = r3
            r17 = r0
            r18 = r2
            r19 = r4
            r20 = r5
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x011f
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x011f
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r11, r11)
            goto L_0x014e
        L_0x011f:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x0137
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0137
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r10)
            goto L_0x014e
        L_0x0137:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x014e
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x014e
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r11, r10)
        L_0x014e:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r11)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0165
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x02c1
        L_0x0165:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r11)
            goto L_0x02c1
        L_0x016c:
            r0 = 0
            if (r1 == 0) goto L_0x01d8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r4.users
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x017f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r4.users
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
        L_0x017f:
            if (r0 == 0) goto L_0x01c2
            boolean r3 = r0.bot
            if (r3 == 0) goto L_0x018a
            boolean r3 = r0.bot_nochats
            if (r3 == 0) goto L_0x018a
            goto L_0x01c2
        L_0x018a:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putBoolean(r6, r10)
            r4 = 2
            r3.putInt(r5, r4)
            r5 = 2131624193(0x7f0e0101, float:1.8875559E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r0)
            r4[r11] = r6
            java.lang.String r6 = "%1$s"
            r4[r10] = r6
            java.lang.String r6 = "AddToTheGroupAlertText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r5, r4)
            java.lang.String r5 = "addToGroupAlertString"
            r3.putString(r5, r4)
            org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
            r4.<init>(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$RUQKDqzV7CVsBbhSjZmyVoBuSVQ r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$RUQKDqzV7CVsBbhSjZmyVoBuSVQ
            r3.<init>(r2, r0, r1)
            r4.setDelegate(r3)
            r14.lambda$runLinkRequest$41$LaunchActivity(r4)
            goto L_0x02c1
        L_0x01c2:
            java.lang.String r0 = "BotCantJoinGroups"
            r1 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x01d3 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r14, r0, r11)     // Catch:{ Exception -> 0x01d3 }
            r0.show()     // Catch:{ Exception -> 0x01d3 }
            goto L_0x01d7
        L_0x01d3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01d7:
            return
        L_0x01d8:
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0209
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            int r5 = r5.id
            java.lang.String r6 = "chat_id"
            r1.putInt(r6, r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            int r5 = r5.id
            int r5 = -r5
            long r5 = (long) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r4.chats
            java.lang.Object r7 = r7.get(r11)
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            goto L_0x0224
        L_0x0209:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            int r5 = r5.id
            java.lang.String r6 = "user_id"
            r1.putInt(r6, r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            int r5 = r5.id
            long r5 = (long) r5
            r7 = r0
        L_0x0224:
            if (r3 == 0) goto L_0x0241
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r4.users
            int r12 = r12.size()
            if (r12 <= 0) goto L_0x0241
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            java.lang.Object r4 = r4.get(r11)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0241
            java.lang.String r4 = "botUser"
            r1.putString(r4, r3)
            r4 = 1
            goto L_0x0242
        L_0x0241:
            r4 = 0
        L_0x0242:
            if (r19 == 0) goto L_0x024d
            int r12 = r19.intValue()
            java.lang.String r13 = "message_id"
            r1.putInt(r13, r12)
        L_0x024d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r12 = mainFragmentsStack
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x0262
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r12 = r0.size()
            int r12 = r12 - r10
            java.lang.Object r0 = r0.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
        L_0x0262:
            if (r0 == 0) goto L_0x026e
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r12 = r12.checkCanOpenChat(r1, r0)
            if (r12 == 0) goto L_0x02c1
        L_0x026e:
            if (r4 == 0) goto L_0x0282
            boolean r4 = r0 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0282
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r12 = r0.getDialogId()
            int r4 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x0282
            r0.setBotUser(r3)
            goto L_0x02c1
        L_0x0282:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r19 != 0) goto L_0x028e
            r3 = 0
            goto L_0x0292
        L_0x028e:
            int r3 = r19.intValue()
        L_0x0292:
            org.telegram.ui.-$$Lambda$LaunchActivity$d6mbKrNP2JuwSOyn_OjttpRQlSs r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$d6mbKrNP2JuwSOyn_OjttpRQlSs
            r4.<init>(r9, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$qVD4XhtrPRVC3s-VxGhPrrJPxA8 r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$qVD4XhtrPRVC3s-VxGhPrrJPxA8
            r1.<init>(r9)
            r15 = r0
            r16 = r5
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r1
            r15.ensureMessagesLoaded(r16, r18, r19, r20, r21)
            goto L_0x0071
        L_0x02ac:
            java.lang.String r0 = "NoUsernameFound"
            r1 = 2131626016(0x7f0e0820, float:1.8879256E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x02bd }
            android.widget.Toast r0 = android.widget.Toast.makeText(r14, r0, r11)     // Catch:{ Exception -> 0x02bd }
            r0.show()     // Catch:{ Exception -> 0x02bd }
            goto L_0x02c1
        L_0x02bd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02c1:
            if (r10 == 0) goto L_0x02cc
            r23.dismiss()     // Catch:{ Exception -> 0x02c7 }
            goto L_0x02cc
        L_0x02c7:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x02cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$23$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$19$LaunchActivity(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    public /* synthetic */ void lambda$null$20$LaunchActivity(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog alertDialog, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!isFinishing()) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$null$22$LaunchActivity(AlertDialog alertDialog) {
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

    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$28$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        if (r6.chat.has_geo != false) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0080, code lost:
        if (r0.checkCanOpenChat(r5, r1.get(r1.size() - 1)) != false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$28$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.ui.ActionBar.AlertDialog r21, java.lang.String r22) {
        /*
            r17 = this;
            r7 = r17
            r0 = r18
            r8 = r21
            boolean r1 = r17.isFinishing()
            if (r1 != 0) goto L_0x0117
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x00c9
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            if (r3 == 0) goto L_0x00c9
            r6 = r19
            org.telegram.tgnet.TLRPC$ChatInvite r6 = (org.telegram.tgnet.TLRPC$ChatInvite) r6
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            if (r0 == 0) goto L_0x00b1
            boolean r0 = org.telegram.messenger.ChatObject.isLeftFromChat(r0)
            if (r0 == 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            boolean r3 = r0.kicked
            if (r3 != 0) goto L_0x00b1
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x003a
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek
            if (r0 != 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            boolean r0 = r0.has_geo
            if (r0 == 0) goto L_0x00b1
        L_0x003a:
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
            if (r0 != 0) goto L_0x0082
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r20)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r0 = r0.checkCanOpenChat(r5, r1)
            if (r0 == 0) goto L_0x010c
        L_0x0082:
            boolean[] r4 = new boolean[r2]
            org.telegram.ui.-$$Lambda$LaunchActivity$kp_k_vw3hOpTL9ywvxcEI6NU1_s r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$kp_k_vw3hOpTL9ywvxcEI6NU1_s
            r0.<init>(r4)
            r8.setOnCancelListener(r0)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            int r1 = r0.id
            int r1 = -r1
            long r11 = (long) r1
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r0)
            r14 = 0
            org.telegram.ui.-$$Lambda$LaunchActivity$HcuTONgseXx7FiD2b0RcQPdCBF0 r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$HcuTONgseXx7FiD2b0RcQPdCBF0
            r1 = r15
            r2 = r17
            r3 = r21
            r1.<init>(r3, r4, r5, r6)
            org.telegram.ui.-$$Lambda$LaunchActivity$kvI-UQ14w1o0H1IyzediAyILr7E r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$kvI-UQ14w1o0H1IyzediAyILr7E
            r0.<init>(r8)
            r16 = r0
            r10.ensureMessagesLoaded(r11, r13, r14, r15, r16)
            r2 = 0
            goto L_0x010c
        L_0x00b1:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r2
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.JoinGroupAlert r1 = new org.telegram.ui.Components.JoinGroupAlert
            r3 = r22
            r1.<init>(r7, r6, r3, r0)
            r0.showDialog(r1)
            goto L_0x010c
        L_0x00c9:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r7)
            r4 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r5 = "AppName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            java.lang.String r0 = r0.text
            java.lang.String r4 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r4)
            if (r0 == 0) goto L_0x00f1
            r0 = 2131625402(0x7f0e05ba, float:1.887801E38)
            java.lang.String r4 = "FloodWait"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
            goto L_0x00fd
        L_0x00f1:
            r0 = 2131625651(0x7f0e06b3, float:1.8878516E38)
            java.lang.String r4 = "JoinToGroupErrorNotExist"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
        L_0x00fd:
            r0 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r4 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setPositiveButton(r0, r1)
            r7.showAlertDialog(r3)
        L_0x010c:
            if (r2 == 0) goto L_0x0117
            r21.dismiss()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0117
        L_0x0112:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0117:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$28$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$25(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle, TLRPC$ChatInvite tLRPC$ChatInvite) {
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

    public /* synthetic */ void lambda$null$27$LaunchActivity(AlertDialog alertDialog) {
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

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$30$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$30$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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

    public /* synthetic */ void lambda$runLinkRequest$32$LaunchActivity(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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
            MediaDataController.getInstance(i).saveDraft(longValue, 0, str, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$Message) null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$36$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    LaunchActivity.this.lambda$null$34$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
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
                LaunchActivity.this.lambda$null$35$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$34$LaunchActivity(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$33$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$33$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$41$LaunchActivity(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    public /* synthetic */ void lambda$null$35$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$38$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$37$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$37$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
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

    public /* synthetic */ void lambda$runLinkRequest$40$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$39$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$39$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$43$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$42$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$42$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC$TL_wallPaper r11, org.telegram.tgnet.TLRPC$TL_error r12) {
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
            r8.lambda$runLinkRequest$41$LaunchActivity(r9)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625187(0x7f0e04e3, float:1.8877575E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$42$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$44$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$46$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$45$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$45$LaunchActivity(org.telegram.tgnet.TLObject r5, org.telegram.ui.ActionBar.AlertDialog r6, org.telegram.tgnet.TLRPC$TL_error r7) {
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
            r5 = 2131627210(0x7f0e0cca, float:1.8881678E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627230(0x7f0e0cde, float:1.8881719E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627229(0x7f0e0cdd, float:1.8881717E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$45$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$48$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, iArr, i, alertDialog, num, num2, num3) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ int[] f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ AlertDialog f$4;
            public final /* synthetic */ Integer f$5;
            public final /* synthetic */ Integer f$6;
            public final /* synthetic */ Integer f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$47$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$47$LaunchActivity(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
        /*
            r10 = this;
            r8 = r10
            r0 = r11
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_chats
            r9 = 0
            if (r1 == 0) goto L_0x0034
            org.telegram.tgnet.TLRPC$TL_messages_chats r0 = (org.telegram.tgnet.TLRPC$TL_messages_chats) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r0.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0034
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r0.chats
            r1.putChats(r2, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r0.chats
            java.lang.Object r0 = r0.get(r9)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r10
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = r16
            r6 = r17
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r12[r9] = r0
            goto L_0x0035
        L_0x0034:
            r9 = 1
        L_0x0035:
            if (r9 == 0) goto L_0x0050
            r14.dismiss()     // Catch:{ Exception -> 0x003b }
            goto L_0x0040
        L_0x003b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0040:
            r0 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$47$LaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$51$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
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
                    LaunchActivity.this.lambda$null$50$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$50$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$49$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$49$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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

    static /* synthetic */ void lambda$runLinkRequest$52(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x005b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<org.telegram.tgnet.TLRPC$TL_contact> findContacts(java.lang.String r18, java.lang.String r19, boolean r20) {
        /*
            r17 = this;
            r0 = r17
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.ContactsController r2 = org.telegram.messenger.ContactsController.getInstance(r2)
            java.util.ArrayList r3 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r4 = r2.contacts
            r3.<init>(r4)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r5 = 0
            if (r19 == 0) goto L_0x0043
            java.lang.String r6 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r19)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r2 = r2.contactsByPhone
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$TL_contact r2 = (org.telegram.tgnet.TLRPC$TL_contact) r2
            if (r2 == 0) goto L_0x0043
            int r6 = r2.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r1.getUser(r6)
            if (r6 == 0) goto L_0x0041
            boolean r6 = r6.self
            if (r6 == 0) goto L_0x003d
            if (r20 == 0) goto L_0x0041
        L_0x003d:
            r4.add(r2)
            goto L_0x0043
        L_0x0041:
            r2 = r5
            goto L_0x0045
        L_0x0043:
            r2 = r18
        L_0x0045:
            boolean r6 = r4.isEmpty()
            if (r6 == 0) goto L_0x014b
            if (r2 == 0) goto L_0x014b
            java.lang.String r2 = r2.trim()
            java.lang.String r2 = r2.toLowerCase()
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            if (r6 != 0) goto L_0x014b
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.String r6 = r6.getTranslitString(r2)
            boolean r7 = r2.equals(r6)
            if (r7 != 0) goto L_0x006f
            int r7 = r6.length()
            if (r7 != 0) goto L_0x0070
        L_0x006f:
            r6 = r5
        L_0x0070:
            r7 = 2
            java.lang.String[] r8 = new java.lang.String[r7]
            r9 = 0
            r8[r9] = r2
            r2 = 1
            r8[r2] = r6
            int r6 = r3.size()
            r10 = 0
        L_0x007e:
            if (r10 >= r6) goto L_0x014b
            java.lang.Object r11 = r3.get(r10)
            org.telegram.tgnet.TLRPC$TL_contact r11 = (org.telegram.tgnet.TLRPC$TL_contact) r11
            if (r11 == 0) goto L_0x0141
            int r12 = r11.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r12 = r1.getUser(r12)
            if (r12 == 0) goto L_0x0141
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x009c
            if (r20 != 0) goto L_0x009c
            goto L_0x0141
        L_0x009c:
            r13 = 3
            java.lang.String[] r14 = new java.lang.String[r13]
            java.lang.String r15 = r12.first_name
            java.lang.String r13 = r12.last_name
            java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r9] = r13
            org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
            r15 = r14[r9]
            java.lang.String r13 = r13.getTranslitString(r15)
            r14[r2] = r13
            r13 = r14[r9]
            r15 = r14[r2]
            boolean r13 = r13.equals(r15)
            if (r13 == 0) goto L_0x00c5
            r14[r2] = r5
        L_0x00c5:
            boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r12)
            if (r13 == 0) goto L_0x00db
            r13 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r7] = r13
            goto L_0x00ee
        L_0x00db:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x00ee
            r13 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            java.lang.String r15 = "SavedMessages"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r7] = r13
        L_0x00ee:
            r13 = 0
            r15 = 0
        L_0x00f0:
            if (r13 >= r7) goto L_0x0141
            r2 = r8[r13]
            if (r2 != 0) goto L_0x00f7
            goto L_0x0138
        L_0x00f7:
            r5 = 3
            r7 = 0
        L_0x00f9:
            if (r7 >= r5) goto L_0x0125
            r5 = r14[r7]
            if (r5 == 0) goto L_0x011e
            boolean r16 = r5.startsWith(r2)
            if (r16 != 0) goto L_0x011c
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = " "
            r9.append(r0)
            r9.append(r2)
            java.lang.String r0 = r9.toString()
            boolean r0 = r5.contains(r0)
            if (r0 == 0) goto L_0x011e
        L_0x011c:
            r15 = 1
            goto L_0x0125
        L_0x011e:
            int r7 = r7 + 1
            r0 = r17
            r5 = 3
            r9 = 0
            goto L_0x00f9
        L_0x0125:
            if (r15 != 0) goto L_0x0132
            java.lang.String r0 = r12.username
            if (r0 == 0) goto L_0x0132
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x0132
            r15 = 1
        L_0x0132:
            if (r15 == 0) goto L_0x0138
            r4.add(r11)
            goto L_0x0141
        L_0x0138:
            int r13 = r13 + 1
            r0 = r17
            r2 = 1
            r5 = 0
            r7 = 2
            r9 = 0
            goto L_0x00f0
        L_0x0141:
            int r10 = r10 + 1
            r0 = r17
            r2 = 1
            r5 = 0
            r7 = 2
            r9 = 0
            goto L_0x007e
        L_0x014b:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.findContacts(java.lang.String, java.lang.String, boolean):java.util.List");
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
                    LaunchActivity.this.lambda$checkAppUpdate$54$LaunchActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$54$LaunchActivity(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    LaunchActivity.this.lambda$null$53$LaunchActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$53$LaunchActivity(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
                    LaunchActivity.this.lambda$showAlertDialog$55$LaunchActivity(dialogInterface);
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$55$LaunchActivity(DialogInterface dialogInterface) {
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

    /* JADX WARNING: Removed duplicated region for block: B:108:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x01ec  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0233 A[LOOP:2: B:127:0x022b->B:129:0x0233, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0258  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0265 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x018f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didSelectDialogs(org.telegram.ui.DialogsActivity r28, java.util.ArrayList<java.lang.Long> r29, java.lang.CharSequence r30, boolean r31) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            r2 = r29
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
            int r6 = r29.size()
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
            int r3 = r28.getCurrentAccount()
            goto L_0x0074
        L_0x0072:
            int r3 = r0.currentAccount
        L_0x0074:
            int r5 = r29.size()
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
            org.telegram.ui.-$$Lambda$LaunchActivity$4sTCb8MveCeWTkwJ3Ypt_beK1zQ r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$4sTCb8MveCeWTkwJ3Ypt_beK1zQ
            r8.<init>(r5, r2, r3)
            r4.setDelegate(r8)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r7
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r2.showDialog(r4)
            goto L_0x026a
        L_0x0117:
            r8 = 0
        L_0x0118:
            int r9 = r29.size()
            if (r8 >= r9) goto L_0x026a
            java.lang.Object r9 = r2.get(r8)
            java.lang.Long r9 = (java.lang.Long) r9
            long r24 = r9.longValue()
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r26 = org.telegram.messenger.AccountInstance.getInstance(r9)
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
            if (r9 == 0) goto L_0x0189
            java.lang.String r10 = r0.sendingText
            r5.openVideoEditor(r9, r10)
            r0.sendingText = r6
            goto L_0x0189
        L_0x0150:
            java.lang.String r9 = r0.videoPath
            if (r9 == 0) goto L_0x0189
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
            r21 = 0
            r22 = 1
            r23 = 0
            r10 = r26
            r11 = r12
            r6 = 1024(0x400, float:1.435E-42)
            r15 = r9
            r16 = r24
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r10, r11, r12, r13, r14, r15, r16, r18, r19, r20, r21, r22, r23)
            goto L_0x018b
        L_0x0189:
            r6 = 1024(0x400, float:1.435E-42)
        L_0x018b:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            if (r9 == 0) goto L_0x01c7
            java.lang.String r9 = r0.sendingText
            if (r9 == 0) goto L_0x01b0
            int r9 = r9.length()
            if (r9 > r6) goto L_0x01b0
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            int r9 = r9.size()
            if (r9 != r7) goto L_0x01b0
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r9 = r0.photoPathsArray
            java.lang.Object r9 = r9.get(r4)
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r9 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r9
            java.lang.String r10 = r0.sendingText
            r9.caption = r10
            r9 = 0
            r0.sendingText = r9
        L_0x01b0:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r11 = r0.photoPathsArray
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1
            r21 = 0
            r10 = r26
            r12 = r24
            org.telegram.messenger.SendMessagesHelper.prepareSendingMedia(r10, r11, r12, r14, r15, r16, r17, r18, r19, r20, r21)
        L_0x01c7:
            java.util.ArrayList<java.lang.String> r9 = r0.documentsPathsArray
            if (r9 != 0) goto L_0x01cf
            java.util.ArrayList<android.net.Uri> r9 = r0.documentsUrisArray
            if (r9 == 0) goto L_0x0213
        L_0x01cf:
            java.lang.String r9 = r0.sendingText
            if (r9 == 0) goto L_0x01f7
            int r9 = r9.length()
            if (r9 > r6) goto L_0x01f7
            java.util.ArrayList<java.lang.String> r6 = r0.documentsPathsArray
            if (r6 == 0) goto L_0x01e2
            int r6 = r6.size()
            goto L_0x01e3
        L_0x01e2:
            r6 = 0
        L_0x01e3:
            java.util.ArrayList<android.net.Uri> r9 = r0.documentsUrisArray
            if (r9 == 0) goto L_0x01ec
            int r9 = r9.size()
            goto L_0x01ed
        L_0x01ec:
            r9 = 0
        L_0x01ed:
            int r6 = r6 + r9
            if (r6 != r7) goto L_0x01f7
            java.lang.String r6 = r0.sendingText
            r9 = 0
            r0.sendingText = r9
            r14 = r6
            goto L_0x01f8
        L_0x01f7:
            r14 = 0
        L_0x01f8:
            java.util.ArrayList<java.lang.String> r11 = r0.documentsPathsArray
            java.util.ArrayList<java.lang.String> r12 = r0.documentsOriginalPathsArray
            java.util.ArrayList<android.net.Uri> r13 = r0.documentsUrisArray
            java.lang.String r15 = r0.documentsMimeType
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1
            r23 = 0
            r10 = r26
            r16 = r24
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r10, r11, r12, r13, r14, r15, r16, r18, r19, r20, r21, r22, r23)
        L_0x0213:
            java.lang.String r11 = r0.sendingText
            if (r11 == 0) goto L_0x0220
            r14 = 1
            r15 = 0
            r10 = r26
            r12 = r24
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r10, r11, r12, r14, r15)
        L_0x0220:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r0.contactsToSend
            if (r6 == 0) goto L_0x0252
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0252
            r6 = 0
        L_0x022b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r0.contactsToSend
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x0252
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r0.contactsToSend
            java.lang.Object r9 = r9.get(r6)
            r11 = r9
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            org.telegram.messenger.SendMessagesHelper r10 = org.telegram.messenger.SendMessagesHelper.getInstance(r3)
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            r19 = 0
            r12 = r24
            r10.sendMessage((org.telegram.tgnet.TLRPC$User) r11, (long) r12, (org.telegram.messenger.MessageObject) r14, (org.telegram.messenger.MessageObject) r15, (org.telegram.tgnet.TLRPC$ReplyMarkup) r16, (java.util.HashMap<java.lang.String, java.lang.String>) r17, (boolean) r18, (int) r19)
            int r6 = r6 + 1
            goto L_0x022b
        L_0x0252:
            boolean r6 = android.text.TextUtils.isEmpty(r30)
            if (r6 != 0) goto L_0x0265
            java.lang.String r11 = r30.toString()
            r14 = 1
            r15 = 0
            r10 = r26
            r12 = r24
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r10, r11, r12, r14, r15)
        L_0x0265:
            int r8 = r8 + 1
            r6 = 0
            goto L_0x0118
        L_0x026a:
            if (r1 == 0) goto L_0x0271
            if (r5 != 0) goto L_0x0271
            r28.finishFragment()
        L_0x0271:
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

    public /* synthetic */ void lambda$didSelectDialogs$56$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$User, ((Long) arrayList.get(i3)).longValue(), (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
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
    public void lambda$runLinkRequest$41$LaunchActivity(BaseFragment baseFragment) {
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
                LaunchActivity.this.lambda$showPermissionErrorAlert$57$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$57$LaunchActivity(DialogInterface dialogInterface, int i) {
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
        Utilities.stageQueue.postRunnable(new Runnable(this.currentAccount) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                LaunchActivity.lambda$onPause$58(this.f$0);
            }
        });
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

    static /* synthetic */ void lambda$onPause$58(int i) {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
        if (VoIPService.getSharedInstance() == null) {
            MessagesController.getInstance(i).ignoreSetOnline = false;
        }
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
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$7A2sw0ZZq7Jd3XfFtXdkAxNCIw.INSTANCE);
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
            VoIPFragment.onResume();
        }
    }

    static /* synthetic */ void lambda$onResume$59() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v8, types: [int] */
    /* JADX WARNING: type inference failed for: r4v15 */
    /* JADX WARNING: type inference failed for: r4v26 */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x025d, code lost:
        if (((org.telegram.ui.ProfileActivity) r2.get(r2.size() - 1)).isSettings() == false) goto L_0x0261;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x04be  */
    /* JADX WARNING: Removed duplicated region for block: B:253:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x024c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            int r3 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r0 != r3) goto L_0x000f
            r16.switchToAvailableAccountOrLogout()
            goto L_0x0601
        L_0x000f:
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r4 = 0
            if (r0 != r3) goto L_0x0020
            r0 = r19[r4]
            if (r0 == r1) goto L_0x0601
            r16.onFinish()
            r16.finish()
            goto L_0x0601
        L_0x0020:
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r3) goto L_0x004f
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r3 = r1.currentConnectionState
            if (r3 == r0) goto L_0x0601
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x0048
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "switch to state "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x0048:
            r1.currentConnectionState = r0
            r1.updateCurrentConnectionState(r2)
            goto L_0x0601
        L_0x004f:
            int r3 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r3) goto L_0x005a
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r1.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x0601
        L_0x005a:
            int r3 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r6 = "Cancel"
            r7 = 5
            r8 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r9 = "AppName"
            r10 = 4
            r11 = 3
            r12 = 2
            r13 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r14 = "OK"
            r15 = 0
            r5 = 1
            if (r0 != r3) goto L_0x018d
            r0 = r19[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r3 = r0.intValue()
            if (r3 != r11) goto L_0x007f
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.proxyErrorDialog
            if (r3 == 0) goto L_0x007f
            return
        L_0x007f:
            int r3 = r0.intValue()
            if (r3 != r10) goto L_0x008d
            r0 = r19[r5]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r0
            r1.showTosActivity(r2, r0)
            return
        L_0x008d:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r1)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r3.setTitle(r4)
            int r4 = r0.intValue()
            if (r4 == r12) goto L_0x00b6
            int r4 = r0.intValue()
            if (r4 == r11) goto L_0x00b6
            r4 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r8 = "MoreInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$iVBaYqZUwscvFHxiGi0xKr5OCT8 r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$iVBaYqZUwscvFHxiGi0xKr5OCT8
            r8.<init>(r2)
            r3.setNegativeButton(r4, r8)
        L_0x00b6:
            int r2 = r0.intValue()
            if (r2 != r7) goto L_0x00d1
            r0 = 2131626021(0x7f0e0825, float:1.8879266E38)
            java.lang.String r2 = "NobodyLikesSpam3"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r15)
            goto L_0x016f
        L_0x00d1:
            int r2 = r0.intValue()
            if (r2 != 0) goto L_0x00ec
            r0 = 2131626019(0x7f0e0823, float:1.8879262E38)
            java.lang.String r2 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r15)
            goto L_0x016f
        L_0x00ec:
            int r2 = r0.intValue()
            if (r2 != r5) goto L_0x0106
            r0 = 2131626020(0x7f0e0824, float:1.8879264E38)
            java.lang.String r2 = "NobodyLikesSpam2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r15)
            goto L_0x016f
        L_0x0106:
            int r2 = r0.intValue()
            if (r2 != r12) goto L_0x0143
            r0 = r19[r5]
            java.lang.String r0 = (java.lang.String) r0
            r3.setMessage(r0)
            r0 = r19[r12]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "AUTH_KEY_DROP_"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x013b
            r0 = 2131624561(0x7f0e0271, float:1.8876305E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r3.setPositiveButton(r0, r15)
            r0 = 2131625747(0x7f0e0713, float:1.887871E38)
            java.lang.String r2 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$t8oDWC_xA2MPhWr4aY31mziIbuw r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$t8oDWC_xA2MPhWr4aY31mziIbuw
            r2.<init>()
            r3.setNegativeButton(r0, r2)
            goto L_0x016f
        L_0x013b:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r15)
            goto L_0x016f
        L_0x0143:
            int r0 = r0.intValue()
            if (r0 != r11) goto L_0x016f
            r0 = 2131626650(0x7f0e0a9a, float:1.8880542E38)
            java.lang.String r2 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setTitle(r0)
            r0 = 2131627372(0x7f0e0d6c, float:1.8882007E38)
            java.lang.String r2 = "UseProxyTelegramError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r15)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r3)
            r1.proxyErrorDialog = r0
            return
        L_0x016f:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0601
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x0601
        L_0x018d:
            int r3 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r0 != r3) goto L_0x01e3
            r0 = r19[r4]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r1)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r3.setTitle(r4)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r4, r15)
            r4 = 2131627004(0x7f0e0bfc, float:1.888126E38)
            java.lang.String r6 = "ShareYouLocationUnableManually"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$0GoBDWlRCyEah1XP9xIZ-lqrrDg r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$0GoBDWlRCyEah1XP9xIZ-lqrrDg
            r6.<init>(r0, r2)
            r3.setNegativeButton(r4, r6)
            r0 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
            java.lang.String r2 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0601
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x0601
        L_0x01e3:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r3) goto L_0x01f6
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0601
            android.view.View r0 = r0.getChildAt(r4)
            if (r0 == 0) goto L_0x0601
            r0.invalidate()
            goto L_0x0601
        L_0x01f6:
            int r3 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r0 != r3) goto L_0x022c
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r2 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x0217
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0217
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0211 }
            r0.setFlags(r2, r2)     // Catch:{ Exception -> 0x0211 }
            goto L_0x0601
        L_0x0211:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0601
        L_0x0217:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x0601
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0226 }
            r0.clearFlags(r2)     // Catch:{ Exception -> 0x0226 }
            goto L_0x0601
        L_0x0226:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0601
        L_0x022c:
            int r3 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r0 != r3) goto L_0x0266
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r5) goto L_0x0249
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0249
            r0 = 1
            goto L_0x024a
        L_0x0249:
            r0 = 0
        L_0x024a:
            if (r0 == 0) goto L_0x0260
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r5
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ProfileActivity r2 = (org.telegram.ui.ProfileActivity) r2
            boolean r2 = r2.isSettings()
            if (r2 != 0) goto L_0x0260
            goto L_0x0261
        L_0x0260:
            r4 = r0
        L_0x0261:
            r1.rebuildAllFragments(r4)
            goto L_0x0601
        L_0x0266:
            int r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r3) goto L_0x026f
            r1.showLanguageAlert(r4)
            goto L_0x0601
        L_0x026f:
            int r3 = org.telegram.messenger.NotificationCenter.openArticle
            if (r0 != r3) goto L_0x02a1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x027c
            return
        L_0x027c:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r5
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r0.setParentActivity(r1, r2)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r2 = r19[r4]
            org.telegram.tgnet.TLRPC$TL_webPage r2 = (org.telegram.tgnet.TLRPC$TL_webPage) r2
            r3 = r19[r5]
            java.lang.String r3 = (java.lang.String) r3
            r0.open(r2, r3)
            goto L_0x0601
        L_0x02a1:
            int r3 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r3) goto L_0x0329
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            if (r0 == 0) goto L_0x0328
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02b2
            goto L_0x0328
        L_0x02b2:
            r0 = r19[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            r0.intValue()
            r0 = r19[r5]
            java.util.HashMap r0 = (java.util.HashMap) r0
            r3 = r19[r12]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r7 = r19[r11]
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            boolean r7 = r7.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r8.fragmentsStack
            int r9 = r8.size()
            int r9 = r9 - r5
            java.lang.Object r5 = r8.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r8.<init>((android.content.Context) r1)
            r9 = 2131627340(0x7f0e0d4c, float:1.8881942E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setTitle(r9)
            r9 = 2131627339(0x7f0e0d4b, float:1.888194E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.-$$Lambda$LaunchActivity$I8vQrymO_fjGwN2L1gXMrlLYR5I r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$I8vQrymO_fjGwN2L1gXMrlLYR5I
            r10.<init>(r2, r0, r3, r7)
            r8.setPositiveButton(r9, r10)
            r9 = 2131624561(0x7f0e0271, float:1.8876305E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$LytnlPKcjW9oZMdyeyVpKKaLy7s r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$LytnlPKcjW9oZMdyeyVpKKaLy7s
            r9.<init>(r2, r0, r3, r7)
            r8.setNegativeButton(r6, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$xpu7cR2aBy7tEbuWfceDLKjDRLc r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$xpu7cR2aBy7tEbuWfceDLKjDRLc
            r6.<init>(r2, r0, r3, r7)
            r8.setOnBackButtonListener(r6)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r5.showDialog(r0)
            r0.setCanceledOnTouchOutside(r4)
            goto L_0x0601
        L_0x0328:
            return
        L_0x0329:
            int r2 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r3 = 21
            if (r0 != r2) goto L_0x0389
            r0 = r19[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x0378
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0363
            java.lang.String r2 = "chats_menuBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setGlowColor(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            java.lang.String r2 = "listSelectorSDK21"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setListSelectorColor(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x0363:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x0378
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0378 }
            java.lang.String r2 = "actionBarDefault"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)     // Catch:{ Exception -> 0x0378 }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r3
            r0.<init>(r15, r15, r2)     // Catch:{ Exception -> 0x0378 }
            r1.setTaskDescription(r0)     // Catch:{ Exception -> 0x0378 }
        L_0x0378:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBehindKeyboardColor(r2)
            r16.checkSystemBarColors()
            goto L_0x0601
        L_0x0389:
            int r2 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r2) goto L_0x04ca
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x049e
            r0 = r19[r12]
            if (r0 == 0) goto L_0x049e
            android.widget.ImageView r0 = r1.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x039e
            return
        L_0x039e:
            r0 = r19[r12]     // Catch:{ all -> 0x049e }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x049e }
            r2 = r19[r10]     // Catch:{ all -> 0x049e }
            java.lang.Boolean r2 = (java.lang.Boolean) r2     // Catch:{ all -> 0x049e }
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x049e }
            r3 = r19[r7]     // Catch:{ all -> 0x049e }
            org.telegram.ui.Components.RLottieImageView r3 = (org.telegram.ui.Components.RLottieImageView) r3     // Catch:{ all -> 0x049e }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x049e }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            int r7 = r7.getMeasuredHeight()     // Catch:{ all -> 0x049e }
            if (r2 != 0) goto L_0x03bf
            r3.setVisibility(r10)     // Catch:{ all -> 0x049e }
        L_0x03bf:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            int r8 = r8.getMeasuredWidth()     // Catch:{ all -> 0x049e }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            int r9 = r9.getMeasuredHeight()     // Catch:{ all -> 0x049e }
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x049e }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r9, r10)     // Catch:{ all -> 0x049e }
            android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x049e }
            r9.<init>(r8)     // Catch:{ all -> 0x049e }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            r10.draw(r9)     // Catch:{ all -> 0x049e }
            android.widget.FrameLayout r9 = r1.frameLayout     // Catch:{ all -> 0x049e }
            android.widget.ImageView r10 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
            r9.removeView(r10)     // Catch:{ all -> 0x049e }
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            if (r2 == 0) goto L_0x03fa
            android.widget.FrameLayout r12 = r1.frameLayout     // Catch:{ all -> 0x049e }
            android.widget.ImageView r13 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)     // Catch:{ all -> 0x049e }
            r12.addView(r13, r4, r9)     // Catch:{ all -> 0x049e }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x049e }
            r10 = 8
            r9.setVisibility(r10)     // Catch:{ all -> 0x049e }
            goto L_0x042b
        L_0x03fa:
            android.widget.FrameLayout r12 = r1.frameLayout     // Catch:{ all -> 0x049e }
            android.widget.ImageView r13 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)     // Catch:{ all -> 0x049e }
            r12.addView(r13, r5, r9)     // Catch:{ all -> 0x049e }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x049e }
            r10 = r0[r4]     // Catch:{ all -> 0x049e }
            r12 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x049e }
            int r10 = r10 - r13
            float r10 = (float) r10     // Catch:{ all -> 0x049e }
            r9.setTranslationX(r10)     // Catch:{ all -> 0x049e }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x049e }
            r10 = r0[r5]     // Catch:{ all -> 0x049e }
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x049e }
            int r10 = r10 - r12
            float r10 = (float) r10     // Catch:{ all -> 0x049e }
            r9.setTranslationY(r10)     // Catch:{ all -> 0x049e }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x049e }
            r9.setVisibility(r4)     // Catch:{ all -> 0x049e }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x049e }
            r9.invalidate()     // Catch:{ all -> 0x049e }
        L_0x042b:
            android.widget.ImageView r9 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
            r9.setImageBitmap(r8)     // Catch:{ all -> 0x049e }
            android.widget.ImageView r8 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
            r8.setVisibility(r4)     // Catch:{ all -> 0x049e }
            org.telegram.ui.Components.RLottieDrawable r8 = r3.getAnimatedDrawable()     // Catch:{ all -> 0x049e }
            r1.themeSwitchSunDrawable = r8     // Catch:{ all -> 0x049e }
            r8 = r0[r4]     // Catch:{ all -> 0x049e }
            int r8 = r6 - r8
            r9 = r0[r4]     // Catch:{ all -> 0x049e }
            int r6 = r6 - r9
            int r8 = r8 * r6
            r6 = r0[r5]     // Catch:{ all -> 0x049e }
            int r6 = r7 - r6
            r9 = r0[r5]     // Catch:{ all -> 0x049e }
            int r9 = r7 - r9
            int r6 = r6 * r9
            int r8 = r8 + r6
            double r8 = (double) r8     // Catch:{ all -> 0x049e }
            double r8 = java.lang.Math.sqrt(r8)     // Catch:{ all -> 0x049e }
            r6 = r0[r4]     // Catch:{ all -> 0x049e }
            r10 = r0[r4]     // Catch:{ all -> 0x049e }
            int r6 = r6 * r10
            r10 = r0[r5]     // Catch:{ all -> 0x049e }
            int r10 = r7 - r10
            r12 = r0[r5]     // Catch:{ all -> 0x049e }
            int r7 = r7 - r12
            int r10 = r10 * r7
            int r6 = r6 + r10
            double r6 = (double) r6     // Catch:{ all -> 0x049e }
            double r6 = java.lang.Math.sqrt(r6)     // Catch:{ all -> 0x049e }
            double r6 = java.lang.Math.max(r8, r6)     // Catch:{ all -> 0x049e }
            float r6 = (float) r6     // Catch:{ all -> 0x049e }
            if (r2 == 0) goto L_0x0473
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x049e }
            goto L_0x0475
        L_0x0473:
            android.widget.ImageView r7 = r1.themeSwitchImageView     // Catch:{ all -> 0x049e }
        L_0x0475:
            r8 = r0[r4]     // Catch:{ all -> 0x049e }
            r0 = r0[r5]     // Catch:{ all -> 0x049e }
            r9 = 0
            if (r2 == 0) goto L_0x047e
            r10 = 0
            goto L_0x047f
        L_0x047e:
            r10 = r6
        L_0x047f:
            if (r2 == 0) goto L_0x0482
            goto L_0x0483
        L_0x0482:
            r6 = 0
        L_0x0483:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r7, r8, r0, r10, r6)     // Catch:{ all -> 0x049e }
            r6 = 400(0x190, double:1.976E-321)
            r0.setDuration(r6)     // Catch:{ all -> 0x049e }
            android.view.animation.Interpolator r6 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x049e }
            r0.setInterpolator(r6)     // Catch:{ all -> 0x049e }
            org.telegram.ui.LaunchActivity$9 r6 = new org.telegram.ui.LaunchActivity$9     // Catch:{ all -> 0x049e }
            r6.<init>(r2, r3)     // Catch:{ all -> 0x049e }
            r0.addListener(r6)     // Catch:{ all -> 0x049e }
            r0.start()     // Catch:{ all -> 0x049e }
            r0 = 1
            goto L_0x049f
        L_0x049e:
            r0 = 0
        L_0x049f:
            r2 = r19[r4]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r2
            r3 = r19[r5]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r4 = r19[r11]
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.actionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0601
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.layersActionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.rightActionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            goto L_0x0601
        L_0x04ca:
            int r2 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r2) goto L_0x04fb
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0601
            r2 = r19[r4]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r0 = r0.getChildCount()
        L_0x04da:
            if (r4 >= r0) goto L_0x0601
            org.telegram.ui.Components.RecyclerListView r3 = r1.sideMenu
            android.view.View r3 = r3.getChildAt(r4)
            boolean r5 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r5 == 0) goto L_0x04f8
            r5 = r3
            org.telegram.ui.Cells.DrawerUserCell r5 = (org.telegram.ui.Cells.DrawerUserCell) r5
            int r5 = r5.getAccountNumber()
            int r6 = r2.intValue()
            if (r5 != r6) goto L_0x04f8
            r3.invalidate()
            goto L_0x0601
        L_0x04f8:
            int r4 = r4 + 1
            goto L_0x04da
        L_0x04fb:
            int r2 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r2) goto L_0x050a
            r0 = r19[r4]     // Catch:{ all -> 0x0601 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x0601 }
            r2 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r1, r2)     // Catch:{ all -> 0x0601 }
            goto L_0x0601
        L_0x050a:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r0 != r2) goto L_0x05c9
            java.lang.String r0 = r1.loadingThemeFileName
            if (r0 == 0) goto L_0x0597
            r2 = r19[r4]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0601
            r1.loadingThemeFileName = r15
            java.io.File r0 = new java.io.File
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "remote"
            r3.append(r4)
            org.telegram.tgnet.TLRPC$TL_theme r4 = r1.loadingTheme
            long r6 = r4.id
            r3.append(r6)
            java.lang.String r4 = ".attheme"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r2, r3)
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r0, r3, r2)
            if (r2 == 0) goto L_0x0592
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x057b
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x057b
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r2.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r2.account
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$LSAozqNZXaUyKT8-RrSGBAD7WBQ r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$LSAozqNZXaUyKT8-RrSGBAD7WBQ
            r4.<init>(r2)
            r3.sendRequest(r0, r4)
            return
        L_0x057b:
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r3, r2, r5)
            if (r7 == 0) goto L_0x0592
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r8 = 1
            r9 = 0
            r10 = 0
            r11 = 0
            r6 = r0
            r6.<init>(r7, r8, r9, r10, r11)
            r1.lambda$runLinkRequest$41$LaunchActivity(r0)
        L_0x0592:
            r16.onThemeLoadFinish()
            goto L_0x0601
        L_0x0597:
            java.lang.String r0 = r1.loadingThemeWallpaperName
            if (r0 == 0) goto L_0x0601
            r2 = r19[r4]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0601
            r1.loadingThemeWallpaperName = r15
            r0 = r19[r5]
            java.io.File r0 = (java.io.File) r0
            boolean r2 = r1.loadingThemeAccent
            if (r2 == 0) goto L_0x05bc
            org.telegram.tgnet.TLRPC$TL_theme r0 = r1.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r1.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.loadingThemeInfo
            r1.openThemeAccentPreview(r0, r2, r3)
            r16.onThemeLoadFinish()
            goto L_0x0601
        L_0x05bc:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$cKC-k88CkN8sAtkphH-uWbqVj8w r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$cKC-k88CkN8sAtkphH-uWbqVj8w
            r4.<init>(r2, r0)
            r3.postRunnable(r4)
            goto L_0x0601
        L_0x05c9:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r0 != r2) goto L_0x05e5
            r0 = r19[r4]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = r1.loadingThemeFileName
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x05e1
            java.lang.String r2 = r1.loadingThemeWallpaperName
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0601
        L_0x05e1:
            r16.onThemeLoadFinish()
            goto L_0x0601
        L_0x05e5:
            int r2 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r2) goto L_0x05fa
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x05ee
            return
        L_0x05ee:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x05f6
            r16.onPasscodeResume()
            goto L_0x0601
        L_0x05f6:
            r16.onPasscodePause()
            goto L_0x0601
        L_0x05fa:
            int r2 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r2) goto L_0x0601
            r16.checkSystemBarColors()
        L_0x0601:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$60(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$61$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$63$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
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
                        LaunchActivity.lambda$null$62(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$41$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$62(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$68$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$67$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$67$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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

    public /* synthetic */ void lambda$didReceivedNotification$70$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$69$LaunchActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$69$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$41$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$41$LaunchActivity(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
                    LaunchActivity.this.lambda$checkFreeDiscSpace$72$LaunchActivity();
                }
            }, 2000);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$72$LaunchActivity() {
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
                                LaunchActivity.this.lambda$null$71$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$null$71$LaunchActivity() {
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
            r9 = 2131624801(0x7f0e0361, float:1.8876792E38)
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
            r14 = 2131625175(0x7f0e04d7, float:1.887755E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$GM5gMnTSSLVuxcZlOgeg-k3oV_o r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$GM5gMnTSSLVuxcZlOgeg-k3oV_o     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624802(0x7f0e0362, float:1.8876794E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$CM0GcHhz4SNPPf3xCch6YeQ8q8E r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$CM0GcHhz4SNPPf3xCch6YeQ8q8E     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$5GWoq-k22C3VKZ7LQpIsTZO-Vuo r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$5GWoq-k22C3VKZ7LQpIsTZO-Vuo     // Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$73(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$74$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$41$LaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$75$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                    LaunchActivity.this.lambda$showLanguageAlert$77$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                                    LaunchActivity.this.lambda$showLanguageAlert$79$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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

    public /* synthetic */ void lambda$showLanguageAlert$77$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$76$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$76$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$79$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$78$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$78$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass10 r0 = new Runnable() {
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
            $$Lambda$LaunchActivity$y5OsXZ4cx9dRXqiDgUqhyVanE r4 = null;
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
                        LaunchActivity.this.lambda$updateCurrentConnectionState$80$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$80$LaunchActivity() {
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
            r2.lambda$runLinkRequest$41$LaunchActivity(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$80$LaunchActivity():void");
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
                if (actionBarLayout2 != actionBarLayout7) {
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
                }
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
        }
        return true;
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
        } else {
            if (!(baseFragment instanceof LoginActivity) ? !(baseFragment instanceof CountrySelectActivity) || mainFragmentsStack.size() != 1 : mainFragmentsStack.size() != 0) {
                z = true;
            } else {
                z = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(z, false);
        }
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
