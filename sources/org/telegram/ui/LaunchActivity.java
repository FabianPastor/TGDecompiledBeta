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
import android.text.TextUtils;
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
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
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
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PhonebookShareAlert;
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
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r14) {
        /*
            r13 = this;
            java.lang.String r0 = "flyme"
            int r1 = android.os.Build.VERSION.SDK_INT
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r2 = r13.getResources()
            android.content.res.Configuration r2 = r2.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r13, r2)
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            r13.currentAccount = r2
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            r3 = 1
            r4 = 0
            if (r2 != 0) goto L_0x00ea
            android.content.Intent r2 = r13.getIntent()
            if (r2 == 0) goto L_0x008a
            java.lang.String r5 = r2.getAction()
            if (r5 == 0) goto L_0x008a
            java.lang.String r5 = r2.getAction()
            java.lang.String r6 = "android.intent.action.SEND"
            boolean r5 = r6.equals(r5)
            if (r5 != 0) goto L_0x0083
            java.lang.String r5 = r2.getAction()
            java.lang.String r6 = "android.intent.action.SEND_MULTIPLE"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0047
            goto L_0x0083
        L_0x0047:
            java.lang.String r5 = r2.getAction()
            java.lang.String r6 = "android.intent.action.VIEW"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x008a
            android.net.Uri r5 = r2.getData()
            if (r5 == 0) goto L_0x008a
            java.lang.String r5 = r5.toString()
            java.lang.String r5 = r5.toLowerCase()
            java.lang.String r6 = "tg:proxy"
            boolean r6 = r5.startsWith(r6)
            if (r6 != 0) goto L_0x0081
            java.lang.String r6 = "tg://proxy"
            boolean r6 = r5.startsWith(r6)
            if (r6 != 0) goto L_0x0081
            java.lang.String r6 = "tg:socks"
            boolean r6 = r5.startsWith(r6)
            if (r6 != 0) goto L_0x0081
            java.lang.String r6 = "tg://socks"
            boolean r5 = r5.startsWith(r6)
            if (r5 == 0) goto L_0x008a
        L_0x0081:
            r5 = 1
            goto L_0x008b
        L_0x0083:
            super.onCreate(r14)
            r13.finish()
            return
        L_0x008a:
            r5 = 0
        L_0x008b:
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r7 = "intro_crashed_time"
            r8 = 0
            long r10 = r6.getLong(r7, r8)
            java.lang.String r12 = "fromIntro"
            boolean r12 = r2.getBooleanExtra(r12, r4)
            if (r12 == 0) goto L_0x00aa
            android.content.SharedPreferences$Editor r6 = r6.edit()
            android.content.SharedPreferences$Editor r6 = r6.putLong(r7, r8)
            r6.commit()
        L_0x00aa:
            if (r5 != 0) goto L_0x00ea
            long r5 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r5
            long r5 = java.lang.Math.abs(r10)
            r7 = 120000(0x1d4c0, double:5.9288E-319)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x00ea
            if (r2 == 0) goto L_0x00ea
            if (r12 != 0) goto L_0x00ea
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "logininfo2"
            android.content.SharedPreferences r5 = r5.getSharedPreferences(r6, r4)
            java.util.Map r5 = r5.getAll()
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x00ea
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r1 = org.telegram.ui.IntroActivity.class
            r0.<init>(r13, r1)
            android.net.Uri r1 = r2.getData()
            r0.setData(r1)
            r13.startActivity(r0)
            super.onCreate(r14)
            r13.finish()
            return
        L_0x00ea:
            r13.requestWindowFeature(r3)
            r2 = 2131689484(0x7f0var_c, float:1.9007985E38)
            r13.setTheme(r2)
            r2 = 0
            r5 = 21
            if (r1 < r5) goto L_0x0112
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r7 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0109 }
            java.lang.String r8 = "actionBarDefault"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x0109 }
            r8 = r8 | r6
            r7.<init>(r2, r2, r8)     // Catch:{ Exception -> 0x0109 }
            r13.setTaskDescription(r7)     // Catch:{ Exception -> 0x0109 }
        L_0x0109:
            android.view.Window r7 = r13.getWindow()     // Catch:{ Exception -> 0x0111 }
            r7.setNavigationBarColor(r6)     // Catch:{ Exception -> 0x0111 }
            goto L_0x0112
        L_0x0111:
        L_0x0112:
            android.view.Window r6 = r13.getWindow()
            r7 = 2131166044(0x7var_c, float:1.7946322E38)
            r6.setBackgroundDrawableResource(r7)
            java.lang.String r6 = org.telegram.messenger.SharedConfig.passcodeHash
            int r6 = r6.length()
            if (r6 <= 0) goto L_0x0136
            boolean r6 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r6 != 0) goto L_0x0136
            android.view.Window r6 = r13.getWindow()     // Catch:{ Exception -> 0x0132 }
            r7 = 8192(0x2000, float:1.14794E-41)
            r6.setFlags(r7, r7)     // Catch:{ Exception -> 0x0132 }
            goto L_0x0136
        L_0x0132:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0136:
            super.onCreate(r14)
            r6 = 24
            if (r1 < r6) goto L_0x0143
            boolean r7 = r13.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r7
        L_0x0143:
            org.telegram.ui.ActionBar.Theme.createChatResources(r13, r4)
            java.lang.String r7 = org.telegram.messenger.SharedConfig.passcodeHash
            int r7 = r7.length()
            if (r7 == 0) goto L_0x015c
            boolean r7 = org.telegram.messenger.SharedConfig.appLocked
            if (r7 == 0) goto L_0x015c
            long r7 = android.os.SystemClock.elapsedRealtime()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r8 = (int) r7
            org.telegram.messenger.SharedConfig.lastPauseTime = r8
        L_0x015c:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r13)
            org.telegram.ui.LaunchActivity$1 r7 = new org.telegram.ui.LaunchActivity$1
            r7.<init>(r13)
            r13.actionBarLayout = r7
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r13)
            r13.frameLayout = r7
            android.view.ViewGroup$LayoutParams r8 = new android.view.ViewGroup$LayoutParams
            r9 = -1
            r8.<init>(r9, r9)
            r13.setContentView(r7, r8)
            r7 = 8
            if (r1 < r5) goto L_0x0184
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r13)
            r13.themeSwitchImageView = r8
            r8.setVisibility(r7)
        L_0x0184:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = new org.telegram.ui.ActionBar.DrawerLayoutContainer
            r8.<init>(r13)
            r13.drawerLayoutContainer = r8
            java.lang.String r10 = "windowBackgroundWhite"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setBehindKeyboardColor(r10)
            android.widget.FrameLayout r8 = r13.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r13.drawerLayoutContainer
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r8.addView(r10, r12)
            if (r1 < r5) goto L_0x01bd
            org.telegram.ui.LaunchActivity$2 r5 = new org.telegram.ui.LaunchActivity$2
            r5.<init>(r13)
            r13.themeSwitchSunView = r5
            android.widget.FrameLayout r8 = r13.frameLayout
            r10 = 48
            r12 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r12)
            r8.addView(r5, r10)
            android.view.View r5 = r13.themeSwitchSunView
            r5.setVisibility(r7)
        L_0x01bd:
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x02a7
            android.view.Window r5 = r13.getWindow()
            r8 = 16
            r5.setSoftInputMode(r8)
            org.telegram.ui.LaunchActivity$3 r5 = new org.telegram.ui.LaunchActivity$3
            r5.<init>(r13)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r8.addView(r5, r10)
            android.view.View r8 = new android.view.View
            r8.<init>(r13)
            r13.backgroundTablet = r8
            android.content.res.Resources r8 = r13.getResources()
            r10 = 2131165334(0x7var_, float:1.7944882E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r10)
            android.graphics.drawable.BitmapDrawable r8 = (android.graphics.drawable.BitmapDrawable) r8
            android.graphics.Shader$TileMode r10 = android.graphics.Shader.TileMode.REPEAT
            r8.setTileModeXY(r10, r10)
            android.view.View r10 = r13.backgroundTablet
            r10.setBackgroundDrawable(r8)
            android.view.View r8 = r13.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createRelative(r9, r9)
            r5.addView(r8, r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.actionBarLayout
            r5.addView(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = new org.telegram.ui.ActionBar.ActionBarLayout
            r8.<init>(r13)
            r13.rightActionBarLayout = r8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = rightFragmentsStack
            r8.init(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.rightActionBarLayout
            r8.setDelegate(r13)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.rightActionBarLayout
            r5.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r13)
            r13.shadowTabletSide = r8
            r10 = 1076449908(0x40295274, float:2.6456575)
            r8.setBackgroundColor(r10)
            android.widget.FrameLayout r8 = r13.shadowTabletSide
            r5.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r13)
            r13.shadowTablet = r8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = layerFragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x0240
            r10 = 8
            goto L_0x0241
        L_0x0240:
            r10 = 0
        L_0x0241:
            r8.setVisibility(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            r10 = 2130706432(0x7var_, float:1.7014118E38)
            r8.setBackgroundColor(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            r5.addView(r8)
            android.widget.FrameLayout r8 = r13.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw
            r10.<init>()
            r8.setOnTouchListener(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7-S05A r10 = org.telegram.ui.$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7S05A.INSTANCE
            r8.setOnClickListener(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = new org.telegram.ui.ActionBar.ActionBarLayout
            r8.<init>(r13)
            r13.layersActionBarLayout = r8
            r8.setRemoveActionBarExtraHeight(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            android.widget.FrameLayout r10 = r13.shadowTablet
            r8.setBackgroundView(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            r8.setUseAlphaAnimations(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            r10 = 2131165297(0x7var_, float:1.7944807E38)
            r8.setBackgroundResource(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = layerFragmentsStack
            r8.init(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            r8.setDelegate(r13)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r13.drawerLayoutContainer
            r8.setDrawerLayoutContainer(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = layerFragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x029d
            goto L_0x029e
        L_0x029d:
            r7 = 0
        L_0x029e:
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r13.layersActionBarLayout
            r5.addView(r7)
            goto L_0x02b3
        L_0x02a7:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r13.actionBarLayout
            android.view.ViewGroup$LayoutParams r8 = new android.view.ViewGroup$LayoutParams
            r8.<init>(r9, r9)
            r5.addView(r7, r8)
        L_0x02b3:
            org.telegram.ui.LaunchActivity$4 r5 = new org.telegram.ui.LaunchActivity$4
            r5.<init>(r13)
            r13.sideMenu = r5
            org.telegram.ui.Components.SideMenultItemAnimator r7 = new org.telegram.ui.Components.SideMenultItemAnimator
            r7.<init>(r5)
            r13.itemAnimator = r7
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r5.setItemAnimator(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            java.lang.String r7 = "chats_menuBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r7 = new androidx.recyclerview.widget.LinearLayoutManager
            r7.<init>(r13, r3, r4)
            r5.setLayoutManager(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r5.setAllowItemsInteractionDuringAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r7 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r8 = r13.itemAnimator
            r7.<init>(r13, r8)
            r13.drawerLayoutAdapter = r7
            r5.setAdapter(r7)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            org.telegram.ui.Components.RecyclerListView r7 = r13.sideMenu
            r5.setDrawerLayout(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            r10 = 1134559232(0x43a00000, float:320.0)
            if (r8 == 0) goto L_0x030e
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            goto L_0x0325
        L_0x030e:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r7.x
            int r7 = r7.y
            int r7 = java.lang.Math.min(r10, r7)
            r10 = 1113587712(0x42600000, float:56.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 - r10
            int r7 = java.lang.Math.min(r8, r7)
        L_0x0325:
            r5.width = r7
            r5.height = r9
            org.telegram.ui.Components.RecyclerListView r7 = r13.sideMenu
            r7.setLayoutParams(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s
            r7.<init>()
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r7)
            androidx.recyclerview.widget.ItemTouchHelper r5 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$5 r7 = new org.telegram.ui.LaunchActivity$5
            r8 = 3
            r7.<init>(r8, r4)
            r5.<init>(r7)
            org.telegram.ui.Components.RecyclerListView r7 = r13.sideMenu
            r5.attachToRecyclerView(r7)
            org.telegram.ui.Components.RecyclerListView r7 = r13.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas
            r10.<init>(r5)
            r7.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r10)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r13.actionBarLayout
            r5.setParentActionBarLayout(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r13.drawerLayoutContainer
            r5.setDrawerLayoutContainer(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = mainFragmentsStack
            r5.init(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            r5.setDelegate(r13)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.Components.PasscodeView r5 = new org.telegram.ui.Components.PasscodeView
            r5.<init>(r13)
            r13.passcodeView = r5
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r7.addView(r5, r10)
            r13.checkCurrentAccount()
            int r5 = r13.currentAccount
            r13.updateCurrentConnectionState(r5)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r10[r4] = r13
            r5.postNotificationName(r7, r10)
            int r5 = r13.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r5 = r5.getConnectionState()
            r13.currentConnectionState = r5
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.needShowAlert
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.reloadInterface
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r5.addObserver(r13, r10)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            r5.addObserver(r13, r7)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r5.addObserver(r13, r7)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r5.addObserver(r13, r7)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r5.addObserver(r13, r7)
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r5.addObserver(r13, r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x0520
            int r5 = r13.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            boolean r5 = r5.isClientActivated()
            if (r5 != 0) goto L_0x0427
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r13.actionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r2.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r13.drawerLayoutContainer
            r2.setAllowOpenDrawer(r4, r4)
            goto L_0x043b
        L_0x0427:
            org.telegram.ui.DialogsActivity r5 = new org.telegram.ui.DialogsActivity
            r5.<init>(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r13.sideMenu
            r5.setSideMenu(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r13.actionBarLayout
            r2.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r13.drawerLayoutContainer
            r2.setAllowOpenDrawer(r3, r4)
        L_0x043b:
            if (r14 == 0) goto L_0x0587
            java.lang.String r2 = "fragment"
            java.lang.String r2 = r14.getString(r2)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0587
            java.lang.String r5 = "args"
            android.os.Bundle r5 = r14.getBundle(r5)     // Catch:{ Exception -> 0x051b }
            int r7 = r2.hashCode()     // Catch:{ Exception -> 0x051b }
            r10 = 5
            r11 = 4
            r12 = 2
            switch(r7) {
                case -1529105743: goto L_0x0488;
                case -1349522494: goto L_0x047e;
                case 3052376: goto L_0x0474;
                case 98629247: goto L_0x046a;
                case 738950403: goto L_0x0460;
                case 1434631203: goto L_0x0456;
                default: goto L_0x0455;
            }     // Catch:{ Exception -> 0x051b }
        L_0x0455:
            goto L_0x0492
        L_0x0456:
            java.lang.String r7 = "settings"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 1
            goto L_0x0492
        L_0x0460:
            java.lang.String r7 = "channel"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 3
            goto L_0x0492
        L_0x046a:
            java.lang.String r7 = "group"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 2
            goto L_0x0492
        L_0x0474:
            java.lang.String r7 = "chat"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 0
            goto L_0x0492
        L_0x047e:
            java.lang.String r7 = "chat_profile"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 4
            goto L_0x0492
        L_0x0488:
            java.lang.String r7 = "wallpapers"
            boolean r2 = r2.equals(r7)     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x0492
            r9 = 5
        L_0x0492:
            if (r9 == 0) goto L_0x0508
            if (r9 == r3) goto L_0x04eb
            if (r9 == r12) goto L_0x04d7
            if (r9 == r8) goto L_0x04c3
            if (r9 == r11) goto L_0x04af
            if (r9 == r10) goto L_0x04a0
            goto L_0x0587
        L_0x04a0:
            org.telegram.ui.WallpapersListActivity r2 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r4)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x04af:
            if (r5 == 0) goto L_0x0587
            org.telegram.ui.ProfileActivity r2 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            boolean r5 = r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            if (r5 == 0) goto L_0x0587
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x04c3:
            if (r5 == 0) goto L_0x0587
            org.telegram.ui.ChannelCreateActivity r2 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            boolean r5 = r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            if (r5 == 0) goto L_0x0587
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x04d7:
            if (r5 == 0) goto L_0x0587
            org.telegram.ui.GroupCreateFinalActivity r2 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            boolean r5 = r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            if (r5 == 0) goto L_0x0587
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x04eb:
            java.lang.String r2 = "user_id"
            int r7 = r13.currentAccount     // Catch:{ Exception -> 0x051b }
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ Exception -> 0x051b }
            int r7 = r7.clientUserId     // Catch:{ Exception -> 0x051b }
            r5.putInt(r2, r7)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ProfileActivity r2 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x0508:
            if (r5 == 0) goto L_0x0587
            org.telegram.ui.ChatActivity r2 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x051b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout     // Catch:{ Exception -> 0x051b }
            boolean r5 = r5.addFragmentToStack(r2)     // Catch:{ Exception -> 0x051b }
            if (r5 == 0) goto L_0x0587
            r2.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x051b }
            goto L_0x0587
        L_0x051b:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            goto L_0x0587
        L_0x0520:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r5 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r5 == 0) goto L_0x0535
            org.telegram.ui.DialogsActivity r2 = (org.telegram.ui.DialogsActivity) r2
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r2.setSideMenu(r5)
        L_0x0535:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x056a
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 > r3) goto L_0x0551
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0551
            r2 = 1
            goto L_0x0552
        L_0x0551:
            r2 = 0
        L_0x0552:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 != r3) goto L_0x056b
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            java.lang.Object r5 = r5.get(r4)
            boolean r5 = r5 instanceof org.telegram.ui.LoginActivity
            if (r5 == 0) goto L_0x056b
            r2 = 0
            goto L_0x056b
        L_0x056a:
            r2 = 1
        L_0x056b:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 != r3) goto L_0x0582
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            java.lang.Object r5 = r5.get(r4)
            boolean r5 = r5 instanceof org.telegram.ui.LoginActivity
            if (r5 == 0) goto L_0x0582
            r2 = 0
        L_0x0582:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            r5.setAllowOpenDrawer(r2, r4)
        L_0x0587:
            r13.checkLayout()
            r13.checkSystemBarColors()
            android.content.Intent r2 = r13.getIntent()
            if (r14 == 0) goto L_0x0595
            r14 = 1
            goto L_0x0596
        L_0x0595:
            r14 = 0
        L_0x0596:
            r13.handleIntent(r2, r4, r14, r4)
            java.lang.String r14 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r2 = android.os.Build.USER     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x05a6
            java.lang.String r14 = r14.toLowerCase()     // Catch:{ Exception -> 0x05f8 }
            goto L_0x05a7
        L_0x05a6:
            r14 = r4
        L_0x05a7:
            if (r2 == 0) goto L_0x05ad
            java.lang.String r4 = r14.toLowerCase()     // Catch:{ Exception -> 0x05f8 }
        L_0x05ad:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f8 }
            if (r2 == 0) goto L_0x05cd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f8 }
            r2.<init>()     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r5 = "OS name "
            r2.append(r5)     // Catch:{ Exception -> 0x05f8 }
            r2.append(r14)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r5 = " "
            r2.append(r5)     // Catch:{ Exception -> 0x05f8 }
            r2.append(r4)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x05f8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x05f8 }
        L_0x05cd:
            boolean r14 = r14.contains(r0)     // Catch:{ Exception -> 0x05f8 }
            if (r14 != 0) goto L_0x05d9
            boolean r14 = r4.contains(r0)     // Catch:{ Exception -> 0x05f8 }
            if (r14 == 0) goto L_0x05fc
        L_0x05d9:
            if (r1 > r6) goto L_0x05fc
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r3     // Catch:{ Exception -> 0x05f8 }
            android.view.Window r14 = r13.getWindow()     // Catch:{ Exception -> 0x05f8 }
            android.view.View r14 = r14.getDecorView()     // Catch:{ Exception -> 0x05f8 }
            android.view.View r14 = r14.getRootView()     // Catch:{ Exception -> 0x05f8 }
            android.view.ViewTreeObserver r0 = r14.getViewTreeObserver()     // Catch:{ Exception -> 0x05f8 }
            org.telegram.ui.-$$Lambda$LaunchActivity$ZFrSHrKcJLvy27CVdXXiDrROQmE r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$ZFrSHrKcJLvy27CVdXXiDrROQmE     // Catch:{ Exception -> 0x05f8 }
            r1.<init>(r14)     // Catch:{ Exception -> 0x05f8 }
            r13.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x05f8 }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x05f8 }
            goto L_0x05fc
        L_0x05f8:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x05fc:
            org.telegram.messenger.MediaController r14 = org.telegram.messenger.MediaController.getInstance()
            r14.setBaseActivity(r13, r3)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$2 */
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
                lambda$runLinkRequest$42(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$42(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$42(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$42(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$42(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$42(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$42(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$42(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$42(new ChatActivity(bundle3));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$3 */
    public /* synthetic */ boolean lambda$onCreate$3$LaunchActivity(ItemTouchHelper itemTouchHelper, View view, int i) {
        if (!(view instanceof DrawerUserCell)) {
            return false;
        }
        final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
        if (accountNumber == this.currentAccount || AndroidUtilities.isTablet()) {
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
        lambda$runLinkRequest$42(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            boolean z = true;
            AndroidUtilities.setLightStatusBar(getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
            if (i >= 26) {
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAcceptTerms$0 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPasscodeActivity$5 */
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v127, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v276, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v282, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v302, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v324, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v330, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v336, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v46, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v345, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v347, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v34, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v358, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v36, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v360, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v37, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v374, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v38, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v384, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v39, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v391, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v40, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v393, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v59, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v59, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v397, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v399, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v417, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v118, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v419, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v426, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v429, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v431, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v434, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v436, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v439, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v440, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v274, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v34, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v22, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v78, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v61, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v23, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v79, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v62, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v80, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v81, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v42, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v82, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v83, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v45, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v46, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v47, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v48, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v49, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v50, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v53, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r14v10 */
    /* JADX WARNING: type inference failed for: r50v0 */
    /* JADX WARNING: type inference failed for: r15v20, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r20v9, types: [org.telegram.tgnet.TLRPC$TL_wallPaper] */
    /* JADX WARNING: type inference failed for: r50v1 */
    /* JADX WARNING: type inference failed for: r50v5 */
    /* JADX WARNING: type inference failed for: r30v12 */
    /* JADX WARNING: type inference failed for: r30v13 */
    /* JADX WARNING: type inference failed for: r50v6 */
    /* JADX WARNING: type inference failed for: r42v21 */
    /* JADX WARNING: type inference failed for: r30v19 */
    /* JADX WARNING: type inference failed for: r30v20 */
    /* JADX WARNING: type inference failed for: r30v21 */
    /* JADX WARNING: type inference failed for: r30v22 */
    /* JADX WARNING: type inference failed for: r30v23 */
    /* JADX WARNING: type inference failed for: r0v425, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v210 */
    /* JADX WARNING: type inference failed for: r4v213 */
    /* JADX WARNING: type inference failed for: r4v214 */
    /* JADX WARNING: type inference failed for: r14v36 */
    /* JADX WARNING: type inference failed for: r42v24 */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0294, code lost:
        if (r15.sendingText == null) goto L_0x018e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0431, code lost:
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x070b, code lost:
        if (r1.intValue() == 0) goto L_0x070d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0114, code lost:
        r0 = r19.getIntent().getExtras();
        r7 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0126, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0129, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x012a, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0147, code lost:
        if (r2.equals(r0) != false) goto L_0x014b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x161d, code lost:
        if (r3.checkCanOpenChat(r0, r4.get(r4.size() - r14)) != false) goto L_0x161f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x1634, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x1636;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1639, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x168f, code lost:
        if (r3.checkCanOpenChat(r0, r4.get(r4.size() - r14)) != false) goto L_0x1691;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x16a6, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x1636;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1a5e, code lost:
        if ((r0.get(r0.size() - r14) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1a7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1a7a, code lost:
        if ((r0.get(r0.size() - r14) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x1a7c;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r14v6, types: [int, boolean] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:753:0x13a9 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1002:0x1ae2  */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1aed  */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x1b07  */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1b16  */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x1b62  */
    /* JADX WARNING: Removed duplicated region for block: B:1035:0x1bae  */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x1bba  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x03af  */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0d75 A[SYNTHETIC, Splitter:B:535:0x0d75] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x12b9  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x12bc  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0159 A[SYNTHETIC, Splitter:B:71:0x0159] */
    /* JADX WARNING: Removed duplicated region for block: B:758:0x13b2 A[SYNTHETIC, Splitter:B:758:0x13b2] */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x15b6  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:827:0x15e6  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x165c  */
    /* JADX WARNING: Removed duplicated region for block: B:913:0x17c5  */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x17d6  */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x1a9e  */
    /* JADX WARNING: Removed duplicated region for block: B:990:0x1aa7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r61, boolean r62, boolean r63, boolean r64) {
        /*
            r60 = this;
            r15 = r60
            r14 = r61
            r0 = r63
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r60, r61)
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
            java.lang.String r1 = r61.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r61.getFlags()
            int[] r11 = new int[r13]
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r3 = "currentAccount"
            int r2 = r14.getIntExtra(r3, r2)
            r11[r12] = r2
            r2 = r11[r12]
            r15.switchToAccount(r2, r13)
            java.lang.String r2 = r61.getAction()
            if (r2 == 0) goto L_0x0074
            java.lang.String r2 = r61.getAction()
            java.lang.String r3 = "voip"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0074
            r23 = 1
            goto L_0x0076
        L_0x0074:
            r23 = 0
        L_0x0076:
            if (r64 != 0) goto L_0x0099
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r2 != 0) goto L_0x0082
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x0099
        L_0x0082:
            r60.showPasscodeActivity()
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            r2.saveConfig(r12)
            if (r23 != 0) goto L_0x0099
            r15.passcodeSaveIntent = r14
            r10 = r62
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x0099:
            r10 = r62
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
            if (r1 != 0) goto L_0x1586
            if (r14 == 0) goto L_0x1586
            java.lang.String r1 = r61.getAction()
            if (r1 == 0) goto L_0x1586
            if (r0 != 0) goto L_0x1586
            java.lang.String r0 = r61.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r2 = "hash"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x02b2
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0149
            if (r14 == 0) goto L_0x0149
            android.os.Bundle r0 = r61.getExtras()
            if (r0 == 0) goto L_0x0149
            android.os.Bundle r0 = r61.getExtras()
            java.lang.String r4 = "dialogId"
            long r16 = r0.getLong(r4, r5)
            int r0 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0137
            android.os.Bundle r0 = r61.getExtras()     // Catch:{ all -> 0x0131 }
            java.lang.String r4 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r4)     // Catch:{ all -> 0x0131 }
            if (r0 == 0) goto L_0x0135
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0131 }
            java.util.List r4 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r4)     // Catch:{ all -> 0x0131 }
            int r8 = r4.size()     // Catch:{ all -> 0x0131 }
            r7 = 0
        L_0x0102:
            if (r7 >= r8) goto L_0x0135
            java.lang.Object r19 = r4.get(r7)     // Catch:{ all -> 0x0131 }
            androidx.core.content.pm.ShortcutInfoCompat r19 = (androidx.core.content.pm.ShortcutInfoCompat) r19     // Catch:{ all -> 0x0131 }
            java.lang.String r13 = r19.getId()     // Catch:{ all -> 0x0131 }
            boolean r13 = r0.equals(r13)     // Catch:{ all -> 0x0131 }
            if (r13 == 0) goto L_0x012d
            android.content.Intent r0 = r19.getIntent()     // Catch:{ all -> 0x0131 }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x0131 }
            java.lang.String r4 = "dialogId"
            long r7 = r0.getLong(r4, r5)     // Catch:{ all -> 0x0131 }
            java.lang.String r0 = r0.getString(r2, r9)     // Catch:{ all -> 0x0129 }
            r16 = r7
            goto L_0x013f
        L_0x0129:
            r0 = move-exception
            r16 = r7
            goto L_0x0132
        L_0x012d:
            int r7 = r7 + 1
            r13 = 1
            goto L_0x0102
        L_0x0131:
            r0 = move-exception
        L_0x0132:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0135:
            r0 = r9
            goto L_0x013f
        L_0x0137:
            android.os.Bundle r0 = r61.getExtras()
            java.lang.String r0 = r0.getString(r2, r9)
        L_0x013f:
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash
            if (r2 == 0) goto L_0x0149
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x014b
        L_0x0149:
            r16 = r5
        L_0x014b:
            java.lang.String r0 = r61.getType()
            if (r0 == 0) goto L_0x0191
            java.lang.String r2 = "text/x-vcard"
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0191
            android.os.Bundle r1 = r61.getExtras()     // Catch:{ Exception -> 0x018a }
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x018a }
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x018a }
            if (r1 == 0) goto L_0x018e
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x018a }
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r9, r9)     // Catch:{ Exception -> 0x018a }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x018a }
            int r2 = r2.size()     // Catch:{ Exception -> 0x018a }
            r3 = 5
            if (r2 <= r3) goto L_0x0186
            r15.contactsToSend = r9     // Catch:{ Exception -> 0x018a }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x018a }
            r2.<init>()     // Catch:{ Exception -> 0x018a }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x018a }
            r2.add(r1)     // Catch:{ Exception -> 0x018a }
            r15.documentsMimeType = r0     // Catch:{ Exception -> 0x018a }
            goto L_0x0298
        L_0x0186:
            r15.contactsToSendUri = r1     // Catch:{ Exception -> 0x018a }
            goto L_0x0298
        L_0x018a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x018e:
            r0 = 1
            goto L_0x0299
        L_0x0191:
            java.lang.String r2 = "android.intent.extra.TEXT"
            java.lang.String r2 = r14.getStringExtra(r2)
            if (r2 != 0) goto L_0x01a5
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x01a5
            java.lang.String r2 = r4.toString()
        L_0x01a5:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x01dc
            java.lang.String r7 = "http://"
            boolean r7 = r2.startsWith(r7)
            if (r7 != 0) goto L_0x01c1
            java.lang.String r7 = "https://"
            boolean r7 = r2.startsWith(r7)
            if (r7 == 0) goto L_0x01d9
        L_0x01c1:
            boolean r7 = android.text.TextUtils.isEmpty(r4)
            if (r7 != 0) goto L_0x01d9
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            r7.append(r1)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
        L_0x01d9:
            r15.sendingText = r2
            goto L_0x01e4
        L_0x01dc:
            boolean r1 = android.text.TextUtils.isEmpty(r4)
            if (r1 != 0) goto L_0x01e4
            r15.sendingText = r4
        L_0x01e4:
            java.lang.String r1 = "android.intent.extra.STREAM"
            android.os.Parcelable r1 = r14.getParcelableExtra(r1)
            if (r1 == 0) goto L_0x0292
            boolean r2 = r1 instanceof android.net.Uri
            if (r2 != 0) goto L_0x01f8
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
        L_0x01f8:
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x0204
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)
            if (r2 == 0) goto L_0x0204
            r2 = 1
            goto L_0x0205
        L_0x0204:
            r2 = 0
        L_0x0205:
            if (r2 != 0) goto L_0x0290
            if (r1 == 0) goto L_0x023b
            if (r0 == 0) goto L_0x0213
            java.lang.String r4 = "image/"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0223
        L_0x0213:
            java.lang.String r4 = r1.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r4 = r4.endsWith(r7)
            if (r4 == 0) goto L_0x023b
        L_0x0223:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x022e
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x022e:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0290
        L_0x023b:
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r4 == 0) goto L_0x027e
            java.lang.String r7 = "file:"
            boolean r7 = r4.startsWith(r7)
            if (r7 == 0) goto L_0x024f
            java.lang.String r7 = "file://"
            java.lang.String r4 = r4.replace(r7, r3)
        L_0x024f:
            if (r0 == 0) goto L_0x025d
            java.lang.String r3 = "video/"
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L_0x025d
            r15.videoPath = r4
            goto L_0x0290
        L_0x025d:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            if (r0 != 0) goto L_0x026f
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsPathsArray = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsOriginalPathsArray = r0
        L_0x026f:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            r0.add(r4)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x0290
        L_0x027e:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            if (r3 != 0) goto L_0x0289
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15.documentsUrisArray = r3
        L_0x0289:
            java.util.ArrayList<android.net.Uri> r3 = r15.documentsUrisArray
            r3.add(r1)
            r15.documentsMimeType = r0
        L_0x0290:
            r0 = r2
            goto L_0x0299
        L_0x0292:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0298
            goto L_0x018e
        L_0x0298:
            r0 = 0
        L_0x0299:
            if (r0 == 0) goto L_0x02a4
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
        L_0x02a4:
            r51 = r5
            r0 = r9
            r4 = r0
            r5 = r4
            r6 = r11
            r1 = r14
            r2 = r15
            r7 = 0
            r8 = 0
            r10 = 0
            r11 = 0
            goto L_0x1597
        L_0x02b2:
            java.lang.String r0 = r61.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x03ba
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x03a8 }
            java.lang.String r1 = r61.getType()     // Catch:{ Exception -> 0x03a8 }
            if (r0 == 0) goto L_0x02fc
            r2 = 0
        L_0x02cb:
            int r4 = r0.size()     // Catch:{ Exception -> 0x03a8 }
            if (r2 >= r4) goto L_0x02f5
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x03a8 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x03a8 }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x03a8 }
            if (r7 != 0) goto L_0x02e3
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03a8 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x03a8 }
        L_0x02e3:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x03a8 }
            if (r4 == 0) goto L_0x02f2
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x03a8 }
            if (r4 == 0) goto L_0x02f2
            r0.remove(r2)     // Catch:{ Exception -> 0x03a8 }
            int r2 = r2 + -1
        L_0x02f2:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x02cb
        L_0x02f5:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x03a8 }
            if (r2 == 0) goto L_0x02fc
            r0 = r9
        L_0x02fc:
            if (r0 == 0) goto L_0x03ac
            if (r1 == 0) goto L_0x033d
            java.lang.String r2 = "image/"
            boolean r2 = r1.startsWith(r2)     // Catch:{ Exception -> 0x03a8 }
            if (r2 == 0) goto L_0x033d
            r1 = 0
        L_0x0309:
            int r2 = r0.size()     // Catch:{ Exception -> 0x03a8 }
            if (r1 >= r2) goto L_0x03a6
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x03a8 }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x03a8 }
            boolean r3 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x03a8 }
            if (r3 != 0) goto L_0x0321
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x03a8 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x03a8 }
        L_0x0321:
            android.net.Uri r2 = (android.net.Uri) r2     // Catch:{ Exception -> 0x03a8 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x03a8 }
            if (r3 != 0) goto L_0x032e
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x03a8 }
            r3.<init>()     // Catch:{ Exception -> 0x03a8 }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x03a8 }
        L_0x032e:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x03a8 }
            r3.<init>()     // Catch:{ Exception -> 0x03a8 }
            r3.uri = r2     // Catch:{ Exception -> 0x03a8 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray     // Catch:{ Exception -> 0x03a8 }
            r2.add(r3)     // Catch:{ Exception -> 0x03a8 }
            int r1 = r1 + 1
            goto L_0x0309
        L_0x033d:
            r2 = 0
        L_0x033e:
            int r4 = r0.size()     // Catch:{ Exception -> 0x03a8 }
            if (r2 >= r4) goto L_0x03a6
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x03a8 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x03a8 }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x03a8 }
            if (r7 != 0) goto L_0x0356
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03a8 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x03a8 }
        L_0x0356:
            r7 = r4
            android.net.Uri r7 = (android.net.Uri) r7     // Catch:{ Exception -> 0x03a8 }
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.getPath(r7)     // Catch:{ Exception -> 0x03a8 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03a8 }
            if (r4 != 0) goto L_0x0364
            r4 = r8
        L_0x0364:
            if (r8 == 0) goto L_0x0391
            java.lang.String r7 = "file:"
            boolean r7 = r8.startsWith(r7)     // Catch:{ Exception -> 0x03a8 }
            if (r7 == 0) goto L_0x0374
            java.lang.String r7 = "file://"
            java.lang.String r8 = r8.replace(r7, r3)     // Catch:{ Exception -> 0x03a8 }
        L_0x0374:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x03a8 }
            if (r7 != 0) goto L_0x0386
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x03a8 }
            r7.<init>()     // Catch:{ Exception -> 0x03a8 }
            r15.documentsPathsArray = r7     // Catch:{ Exception -> 0x03a8 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x03a8 }
            r7.<init>()     // Catch:{ Exception -> 0x03a8 }
            r15.documentsOriginalPathsArray = r7     // Catch:{ Exception -> 0x03a8 }
        L_0x0386:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray     // Catch:{ Exception -> 0x03a8 }
            r7.add(r8)     // Catch:{ Exception -> 0x03a8 }
            java.util.ArrayList<java.lang.String> r7 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x03a8 }
            r7.add(r4)     // Catch:{ Exception -> 0x03a8 }
            goto L_0x03a3
        L_0x0391:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x03a8 }
            if (r4 != 0) goto L_0x039c
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x03a8 }
            r4.<init>()     // Catch:{ Exception -> 0x03a8 }
            r15.documentsUrisArray = r4     // Catch:{ Exception -> 0x03a8 }
        L_0x039c:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x03a8 }
            r4.add(r7)     // Catch:{ Exception -> 0x03a8 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x03a8 }
        L_0x03a3:
            int r2 = r2 + 1
            goto L_0x033e
        L_0x03a6:
            r0 = 0
            goto L_0x03ad
        L_0x03a8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03ac:
            r0 = 1
        L_0x03ad:
            if (r0 == 0) goto L_0x1586
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
            goto L_0x1586
        L_0x03ba:
            java.lang.String r0 = r61.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x1481
            android.net.Uri r0 = r61.getData()
            if (r0 == 0) goto L_0x143f
            java.lang.String r4 = r0.getScheme()
            java.lang.String r7 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r8 = "phone"
            if (r4 == 0) goto L_0x1241
            r4.hashCode()
            r13 = -1
            int r16 = r4.hashCode()
            switch(r16) {
                case 3699: goto L_0x03f8;
                case 3213448: goto L_0x03ed;
                case 99617003: goto L_0x03e2;
                default: goto L_0x03e1;
            }
        L_0x03e1:
            goto L_0x0402
        L_0x03e2:
            java.lang.String r5 = "https"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x03eb
            goto L_0x0402
        L_0x03eb:
            r13 = 2
            goto L_0x0402
        L_0x03ed:
            java.lang.String r5 = "http"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x03f6
            goto L_0x0402
        L_0x03f6:
            r13 = 1
            goto L_0x0402
        L_0x03f8:
            java.lang.String r5 = "tg"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0401
            goto L_0x0402
        L_0x0401:
            r13 = 0
        L_0x0402:
            java.lang.String r5 = "thread"
            r19 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6 = 6
            switch(r13) {
                case 0: goto L_0x0841;
                case 1: goto L_0x040c;
                case 2: goto L_0x040c;
                default: goto L_0x040a;
            }
        L_0x040a:
            goto L_0x1241
        L_0x040c:
            java.lang.String r13 = r0.getHost()
            java.lang.String r13 = r13.toLowerCase()
            java.lang.String r9 = "telegram.me"
            boolean r9 = r13.equals(r9)
            if (r9 != 0) goto L_0x0434
            java.lang.String r9 = "t.me"
            boolean r9 = r13.equals(r9)
            if (r9 != 0) goto L_0x0434
            java.lang.String r9 = "telegram.dog"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x042d
            goto L_0x0434
        L_0x042d:
            r27 = r4
            r26 = r11
        L_0x0431:
            r12 = 0
            goto L_0x1246
        L_0x0434:
            java.lang.String r9 = r0.getPath()
            if (r9 == 0) goto L_0x07e2
            int r13 = r9.length()
            r12 = 1
            if (r13 <= r12) goto L_0x07e2
            java.lang.String r9 = r9.substring(r12)
            java.lang.String r12 = "bg/"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x05a5
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "bg/"
            java.lang.String r3 = r9.replace(r5, r3)
            r1.slug = r3
            if (r3 == 0) goto L_0x047c
            int r3 = r3.length()
            if (r3 != r6) goto L_0x047c
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0477 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0477 }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0477 }
            r3 = r3 | r19
            r0.background_color = r3     // Catch:{ Exception -> 0x0477 }
        L_0x0477:
            r3 = 0
            r1.slug = r3
            goto L_0x058f
        L_0x047c:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x04db
            int r3 = r3.length()
            r5 = 13
            if (r3 != r5) goto L_0x04db
            java.lang.String r3 = r1.slug
            char r3 = r3.charAt(r6)
            r5 = 45
            if (r3 != r5) goto L_0x04db
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x04be }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x04be }
            r9 = 0
            java.lang.String r5 = r5.substring(r9, r6)     // Catch:{ Exception -> 0x04be }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x04be }
            r5 = r5 | r19
            r3.background_color = r5     // Catch:{ Exception -> 0x04be }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x04be }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x04be }
            r6 = 7
            java.lang.String r5 = r5.substring(r6)     // Catch:{ Exception -> 0x04be }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x04be }
            r5 = r5 | r19
            r3.second_background_color = r5     // Catch:{ Exception -> 0x04be }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x04be }
            r5 = 45
            r3.rotation = r5     // Catch:{ Exception -> 0x04be }
        L_0x04be:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x04d6 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x04d6 }
            if (r3 != 0) goto L_0x04d6
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x04d6 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x04d6 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x04d6 }
            r3.rotation = r0     // Catch:{ Exception -> 0x04d6 }
        L_0x04d6:
            r3 = 0
            r1.slug = r3
            goto L_0x058f
        L_0x04db:
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x0518
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r5 = " "
            java.lang.String[] r3 = r3.split(r5)
            if (r3 == 0) goto L_0x0518
            int r5 = r3.length
            if (r5 <= 0) goto L_0x0518
            r5 = 0
        L_0x04f3:
            int r9 = r3.length
            if (r5 >= r9) goto L_0x0518
            r9 = r3[r5]
            java.lang.String r12 = "blur"
            boolean r9 = r12.equals(r9)
            if (r9 == 0) goto L_0x0506
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r12 = 1
            r9.blur = r12
            goto L_0x0515
        L_0x0506:
            r12 = 1
            r9 = r3[r5]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x0515
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r9.motion = r12
        L_0x0515:
            int r5 = r5 + 1
            goto L_0x04f3
        L_0x0518:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x0531
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r3 = r3.intValue()
            r5.intensity = r3
            goto L_0x0537
        L_0x0531:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            r5 = 50
            r3.intensity = r5
        L_0x0537:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0577 }
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x0572
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0577 }
            r9 = 0
            java.lang.String r12 = r3.substring(r9, r6)     // Catch:{ Exception -> 0x0577 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x0577 }
            r9 = r12 | r19
            r5.background_color = r9     // Catch:{ Exception -> 0x0577 }
            int r5 = r3.length()     // Catch:{ Exception -> 0x0577 }
            if (r5 <= r6) goto L_0x0577
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0577 }
            r6 = 7
            java.lang.String r3 = r3.substring(r6)     // Catch:{ Exception -> 0x0577 }
            r6 = 16
            int r3 = java.lang.Integer.parseInt(r3, r6)     // Catch:{ Exception -> 0x0577 }
            r3 = r3 | r19
            r5.second_background_color = r3     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0577 }
            r5 = 45
            r3.rotation = r5     // Catch:{ Exception -> 0x0577 }
            goto L_0x0577
        L_0x0572:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0577 }
            r5 = -1
            r3.background_color = r5     // Catch:{ Exception -> 0x0577 }
        L_0x0577:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x058f }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x058f }
            if (r3 != 0) goto L_0x058f
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x058f }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x058f }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x058f }
            r3.rotation = r0     // Catch:{ Exception -> 0x058f }
        L_0x058f:
            r28 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r22 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            goto L_0x07f6
        L_0x05a5:
            java.lang.String r6 = "login/"
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x05c7
            java.lang.String r0 = "login/"
            java.lang.String r0 = r9.replace(r0, r3)
            r27 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r22 = 0
            r25 = 0
            r26 = 0
            goto L_0x07f4
        L_0x05c7:
            java.lang.String r6 = "joinchat/"
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x05d7
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r9.replace(r0, r3)
            goto L_0x07e3
        L_0x05d7:
            java.lang.String r6 = "addstickers/"
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x05e9
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r9.replace(r0, r3)
            r1 = r0
            r0 = 0
            goto L_0x07e4
        L_0x05e9:
            java.lang.String r6 = "msg/"
            boolean r6 = r9.startsWith(r6)
            if (r6 != 0) goto L_0x0776
            java.lang.String r6 = "share/"
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x05fb
            goto L_0x0776
        L_0x05fb:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0619
            java.lang.String r1 = r0.getQueryParameter(r8)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r22 = r0
            r13 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r19 = 0
            goto L_0x07ee
        L_0x0619:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0637
            r0 = 12
            java.lang.String r0 = r9.substring(r0)
            r25 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r22 = 0
            goto L_0x07f0
        L_0x0637:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0657
            r0 = 9
            java.lang.String r0 = r9.substring(r0)
            r26 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r22 = 0
            r25 = 0
            goto L_0x07f2
        L_0x0657:
            java.lang.String r1 = "c/"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x06be
            java.util.List r1 = r0.getPathSegments()
            int r3 = r1.size()
            r6 = 3
            if (r3 != r6) goto L_0x069e
            r3 = 1
            java.lang.Object r6 = r1.get(r3)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r6)
            r12 = 2
            java.lang.Object r1 = r1.get(r12)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 == 0) goto L_0x068c
            int r6 = r3.intValue()
            if (r6 != 0) goto L_0x068e
        L_0x068c:
            r1 = 0
            r3 = 0
        L_0x068e:
            java.lang.String r0 = r0.getQueryParameter(r5)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r5 = r0.intValue()
            if (r5 != 0) goto L_0x06a2
            r0 = 0
            goto L_0x06a2
        L_0x069e:
            r12 = 2
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x06a2:
            r31 = r0
            r29 = r1
            r30 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r22 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            goto L_0x07fc
        L_0x06be:
            r12 = 2
            int r1 = r9.length()
            r3 = 1
            if (r1 < r3) goto L_0x07e2
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r1.<init>(r3)
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x06e8
            r3 = 0
            java.lang.Object r6 = r1.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r9 = "s"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x06e9
            r1.remove(r3)
            goto L_0x06e9
        L_0x06e8:
            r3 = 0
        L_0x06e9:
            int r6 = r1.size()
            if (r6 <= 0) goto L_0x070f
            java.lang.Object r6 = r1.get(r3)
            r3 = r6
            java.lang.String r3 = (java.lang.String) r3
            int r6 = r1.size()
            r9 = 1
            if (r6 <= r9) goto L_0x070d
            java.lang.Object r1 = r1.get(r9)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 != 0) goto L_0x0711
        L_0x070d:
            r1 = 0
            goto L_0x0711
        L_0x070f:
            r1 = 0
            r3 = 0
        L_0x0711:
            java.lang.String r6 = "start"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r9 = "startgroup"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r13 = "game"
            java.lang.String r13 = r0.getQueryParameter(r13)
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r19 = r5.intValue()
            if (r19 != 0) goto L_0x0732
            r5 = 0
        L_0x0732:
            java.lang.String r12 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r12)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r12 = r0.intValue()
            if (r12 != 0) goto L_0x075b
            r29 = r1
            r31 = r5
            r19 = r13
            r0 = 0
            r1 = 0
            r5 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r30 = 0
            goto L_0x07fc
        L_0x075b:
            r32 = r0
            r29 = r1
            r31 = r5
            r19 = r13
            r0 = 0
            r1 = 0
            r5 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r30 = 0
            goto L_0x07fe
        L_0x0776:
            java.lang.String r5 = "url"
            java.lang.String r5 = r0.getQueryParameter(r5)
            if (r5 != 0) goto L_0x0780
            goto L_0x0781
        L_0x0780:
            r3 = r5
        L_0x0781:
            java.lang.String r5 = "text"
            java.lang.String r5 = r0.getQueryParameter(r5)
            if (r5 == 0) goto L_0x07b7
            int r5 = r3.length()
            if (r5 <= 0) goto L_0x07a0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r5.append(r1)
            java.lang.String r3 = r5.toString()
            r5 = 1
            goto L_0x07a1
        L_0x07a0:
            r5 = 0
        L_0x07a1:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r6.append(r0)
            java.lang.String r3 = r6.toString()
            goto L_0x07b8
        L_0x07b7:
            r5 = 0
        L_0x07b8:
            int r0 = r3.length()
            r6 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r6) goto L_0x07c8
            r0 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            java.lang.String r0 = r3.substring(r6, r0)
            goto L_0x07ca
        L_0x07c8:
            r6 = 0
            r0 = r3
        L_0x07ca:
            boolean r3 = r0.endsWith(r1)
            if (r3 == 0) goto L_0x07db
            int r3 = r0.length()
            r9 = 1
            int r3 = r3 - r9
            java.lang.String r0 = r0.substring(r6, r3)
            goto L_0x07ca
        L_0x07db:
            r12 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r6 = 0
            r9 = 0
            goto L_0x07e9
        L_0x07e2:
            r0 = 0
        L_0x07e3:
            r1 = 0
        L_0x07e4:
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
        L_0x07e9:
            r13 = 0
            r19 = 0
            r22 = 0
        L_0x07ee:
            r25 = 0
        L_0x07f0:
            r26 = 0
        L_0x07f2:
            r27 = 0
        L_0x07f4:
            r28 = 0
        L_0x07f6:
            r29 = 0
            r30 = 0
            r31 = 0
        L_0x07fc:
            r32 = 0
        L_0x07fe:
            r10 = r5
            r43 = r19
            r45 = r25
            r50 = r26
            r47 = r27
            r49 = r28
            r19 = r30
            r41 = r31
            r42 = r32
            r24 = 0
            r25 = 0
            r28 = 0
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
            r44 = 0
            r46 = 0
            r48 = 0
            r5 = r1
            r27 = r4
            r26 = r11
            r1 = r12
            r11 = r29
            r12 = 0
            r29 = 0
            r4 = r0
            r0 = r22
            goto L_0x1284
        L_0x0841:
            java.lang.String r9 = r0.toString()
            java.lang.String r12 = "tg:resolve"
            boolean r12 = r9.startsWith(r12)
            java.lang.String r13 = "callback_url"
            java.lang.String r6 = "public_key"
            java.lang.String r10 = "bot_id"
            r26 = r11
            java.lang.String r11 = "payload"
            r27 = r4
            java.lang.String r4 = "scope"
            r28 = r5
            java.lang.String r5 = "tg://telegram.org"
            if (r12 != 0) goto L_0x111f
            java.lang.String r12 = "tg://resolve"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x0869
            goto L_0x111f
        L_0x0869:
            java.lang.String r12 = "tg:privatepost"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x1097
            java.lang.String r12 = "tg://privatepost"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x087b
            goto L_0x1097
        L_0x087b:
            java.lang.String r12 = "tg:bg"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0f1e
            java.lang.String r12 = "tg://bg"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x088d
            goto L_0x0f1e
        L_0x088d:
            java.lang.String r12 = "tg:join"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0ec7
            java.lang.String r12 = "tg://join"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x089f
            goto L_0x0ec7
        L_0x089f:
            java.lang.String r12 = "tg:addstickers"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0eab
            java.lang.String r12 = "tg://addstickers"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x08b1
            goto L_0x0eab
        L_0x08b1:
            java.lang.String r12 = "tg:msg"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0e21
            java.lang.String r12 = "tg://msg"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0e21
            java.lang.String r12 = "tg://share"
            boolean r12 = r9.startsWith(r12)
            if (r12 != 0) goto L_0x0e21
            java.lang.String r12 = "tg:share"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x08d3
            goto L_0x0e21
        L_0x08d3:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0dfd
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x08e5
            goto L_0x0dfd
        L_0x08e5:
            java.lang.String r1 = "tg:login"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0da4
            java.lang.String r1 = "tg://login"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x08f7
            goto L_0x0da4
        L_0x08f7:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0d3e
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0909
            goto L_0x0d3e
        L_0x0909:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0ca7
            java.lang.String r1 = "tg://passport"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0ca7
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0923
            goto L_0x0ca7
        L_0x0923:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0c5a
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0935
            goto L_0x0c5a
        L_0x0935:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0947
            goto L_0x0CLASSNAME
        L_0x0947:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0b64
            java.lang.String r1 = "tg://settings"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x0959
            goto L_0x0b64
        L_0x0959:
            java.lang.String r1 = "tg:search"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0b37
            java.lang.String r1 = "tg://search"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x096b
            goto L_0x0b37
        L_0x096b:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0b1c
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x097d
            goto L_0x0b1c
        L_0x097d:
            java.lang.String r1 = "tg:call"
            boolean r1 = r9.startsWith(r1)
            if (r1 != 0) goto L_0x0a62
            java.lang.String r1 = "tg://call"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x098f
            goto L_0x0a62
        L_0x098f:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0a3f
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x09a1
            goto L_0x0a3f
        L_0x09a1:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0a04
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x09b2
            goto L_0x0a04
        L_0x09b2:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r9.replace(r0, r3)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r3)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x09cb
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r1)
        L_0x09cb:
            r46 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            goto L_0x0var_
        L_0x0a04:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r29 = r0
            r28 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 1
            goto L_0x0efc
        L_0x0a3f:
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 1
            goto L_0x0efe
        L_0x0a62:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x0431
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0aa2
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x0a81
            goto L_0x0aa2
        L_0x0a81:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r7)
            java.lang.String r1 = "extra_force_call"
            r3 = 1
            r0.putExtra(r1, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$X0aF1Xm_mIISEfWg-pwwTqXnE3w r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$X0aF1Xm_mIISEfWg-pwwTqXnE3w
            r1.<init>(r0)
            r3 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r3)
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            goto L_0x0aef
        L_0x0aa2:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r4 = "name"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r8)
            r5 = 0
            java.util.List r6 = r15.findContacts(r4, r0, r5)
            boolean r9 = r6.isEmpty()
            if (r9 == 0) goto L_0x0ac7
            if (r0 == 0) goto L_0x0ac7
            r10 = r0
            r9 = r4
            r0 = 1
            r1 = 0
            r3 = 0
            r4 = 0
            r6 = 0
            goto L_0x0aef
        L_0x0ac7:
            int r0 = r6.size()
            r9 = 1
            if (r0 != r9) goto L_0x0ad7
            java.lang.Object r0 = r6.get(r5)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            int r0 = r0.user_id
            goto L_0x0ad8
        L_0x0ad7:
            r0 = 0
        L_0x0ad8:
            if (r0 != 0) goto L_0x0ade
            if (r4 == 0) goto L_0x0adf
            r3 = r4
            goto L_0x0adf
        L_0x0ade:
            r3 = 0
        L_0x0adf:
            java.lang.String r4 = "video"
            boolean r1 = r4.equalsIgnoreCase(r1)
            r4 = r1 ^ 1
            r6 = r3
            r5 = 1
            r9 = 0
            r10 = 0
            r3 = r1
            r1 = r0
            r0 = 0
        L_0x0aef:
            r40 = r0
            r36 = r1
            r33 = r3
            r32 = r4
            r39 = r5
            r25 = r6
            r28 = r9
            r29 = r10
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r30 = 0
            r31 = 0
            r34 = 0
            r35 = 0
            r37 = 0
            r38 = 0
            goto L_0x0var_
        L_0x0b1c:
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 1
            goto L_0x0ef6
        L_0x0b37:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0b53
            java.lang.String r3 = r0.trim()
        L_0x0b53:
            r24 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            goto L_0x0eec
        L_0x0b64:
            java.lang.String r0 = "themes"
            boolean r0 = r9.contains(r0)
            if (r0 == 0) goto L_0x0b85
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 2
            goto L_0x0ef4
        L_0x0b85:
            java.lang.String r0 = "devices"
            boolean r0 = r9.contains(r0)
            if (r0 == 0) goto L_0x0ba6
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 3
            goto L_0x0ef4
        L_0x0ba6:
            java.lang.String r0 = "folders"
            boolean r0 = r9.contains(r0)
            if (r0 == 0) goto L_0x0bc8
            r0 = 4
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 4
            goto L_0x0ef4
        L_0x0bc8:
            java.lang.String r0 = "change_number"
            boolean r0 = r9.contains(r0)
            if (r0 == 0) goto L_0x0bea
            r0 = 5
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 5
            goto L_0x0ef4
        L_0x0bea:
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 1
            goto L_0x0ef4
        L_0x0CLASSNAME:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r50 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r48 = 0
            r49 = 0
            goto L_0x1284
        L_0x0c5a:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r45 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            goto L_0x0var_
        L_0x0ca7:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x0cea
            java.lang.String r5 = "{"
            boolean r5 = r3.startsWith(r5)
            if (r5 == 0) goto L_0x0cea
            java.lang.String r5 = "}"
            boolean r5 = r3.endsWith(r5)
            if (r5 == 0) goto L_0x0cea
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r9 = "nonce"
            r1.put(r9, r5)
            goto L_0x0cf1
        L_0x0cea:
            java.lang.String r5 = r0.getQueryParameter(r11)
            r1.put(r11, r5)
        L_0x0cf1:
            java.lang.String r5 = r0.getQueryParameter(r10)
            r1.put(r10, r5)
            r1.put(r4, r3)
            java.lang.String r3 = r0.getQueryParameter(r6)
            r1.put(r6, r3)
            java.lang.String r0 = r0.getQueryParameter(r13)
            r1.put(r13, r0)
            r44 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            goto L_0x0var_
        L_0x0d3e:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "chat_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "message_id"
            java.lang.String r0 = r0.getQueryParameter(r4)
            if (r1 == 0) goto L_0x0d68
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0d71 }
            goto L_0x0d72
        L_0x0d68:
            if (r3 == 0) goto L_0x0d71
            int r1 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x0d71 }
            r3 = r1
            r1 = 0
            goto L_0x0d73
        L_0x0d71:
            r1 = 0
        L_0x0d72:
            r3 = 0
        L_0x0d73:
            if (r0 == 0) goto L_0x0d7a
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0d7a }
            goto L_0x0d7b
        L_0x0d7a:
            r0 = 0
        L_0x0d7b:
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
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            goto L_0x0var_
        L_0x0da4:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "code"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r47 = r0
            r48 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            goto L_0x0var_
        L_0x0dfd:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = r0.getQueryParameter(r8)
            java.lang.String r0 = r0.getQueryParameter(r2)
            r13 = r1
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            goto L_0x0ee8
        L_0x0e21:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r4 = "tg://msg"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg://share"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg:share"
            java.lang.String r0 = r0.replace(r4, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0e47
            goto L_0x0e48
        L_0x0e47:
            r3 = r4
        L_0x0e48:
            java.lang.String r4 = "text"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 == 0) goto L_0x0e7e
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x0e67
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r1)
            java.lang.String r3 = r4.toString()
            r4 = 1
            goto L_0x0e68
        L_0x0e67:
            r4 = 0
        L_0x0e68:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            goto L_0x0e7f
        L_0x0e7e:
            r4 = 0
        L_0x0e7f:
            int r0 = r3.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r5) goto L_0x0e8f
            r0 = 16384(0x4000, float:2.2959E-41)
            r5 = 0
            java.lang.String r0 = r3.substring(r5, r0)
            goto L_0x0e91
        L_0x0e8f:
            r5 = 0
            r0 = r3
        L_0x0e91:
            boolean r3 = r0.endsWith(r1)
            if (r3 == 0) goto L_0x0ea2
            int r3 = r0.length()
            r6 = 1
            int r3 = r3 - r6
            java.lang.String r0 = r0.substring(r5, r3)
            goto L_0x0e91
        L_0x0ea2:
            r1 = r0
            r10 = r4
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            goto L_0x0ee5
        L_0x0eab:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r9.replace(r0, r5)
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
            goto L_0x0ee2
        L_0x0ec7:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r4 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r5 = 0
        L_0x0ee2:
            r6 = 0
            r9 = 0
            r10 = 0
        L_0x0ee5:
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x0ee8:
            r19 = 0
            r24 = 0
        L_0x0eec:
            r25 = 0
            r28 = 0
            r29 = 0
            r30 = 0
        L_0x0ef4:
            r31 = 0
        L_0x0ef6:
            r32 = 0
            r33 = 0
            r34 = 0
        L_0x0efc:
            r35 = 0
        L_0x0efe:
            r36 = 0
            r37 = 0
            r38 = 0
        L_0x0var_:
            r39 = 0
            r40 = 0
        L_0x0var_:
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
        L_0x0var_:
            r45 = 0
        L_0x0var_:
            r46 = 0
        L_0x0var_:
            r47 = 0
            r48 = 0
        L_0x0var_:
            r49 = 0
            r50 = 0
            goto L_0x1284
        L_0x0f1e:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r9.replace(r0, r5)
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
            if (r3 != 0) goto L_0x0f4c
            java.lang.String r3 = "color"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
        L_0x0f4c:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0f6b
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x0f6b
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x0var_ }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0var_ }
            r3 = r3 | r19
            r0.background_color = r3     // Catch:{ Exception -> 0x0var_ }
        L_0x0var_:
            r3 = 0
            r1.slug = r3
            r12 = r3
            goto L_0x106e
        L_0x0f6b:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x0fcb
            int r3 = r3.length()
            r4 = 13
            if (r3 != r4) goto L_0x0fcb
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            r5 = 45
            if (r3 != r5) goto L_0x0fcb
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fae }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0fae }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0fae }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0fae }
            r4 = r4 | r19
            r3.background_color = r4     // Catch:{ Exception -> 0x0fae }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fae }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0fae }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0fae }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0fae }
            r4 = r4 | r19
            r3.second_background_color = r4     // Catch:{ Exception -> 0x0fae }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fae }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x0fae }
        L_0x0fae:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0fc6 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0fc6 }
            if (r3 != 0) goto L_0x0fc6
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x0fc6 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0fc6 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0fc6 }
            r3.rotation = r0     // Catch:{ Exception -> 0x0fc6 }
        L_0x0fc6:
            r12 = 0
            r1.slug = r12
            goto L_0x106e
        L_0x0fcb:
            r12 = 0
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x1009
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x1009
            int r4 = r3.length
            if (r4 <= 0) goto L_0x1009
            r4 = 0
        L_0x0fe4:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x1009
            r5 = r3[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0ff7
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x1006
        L_0x0ff7:
            r6 = 1
            r5 = r3[r4]
            java.lang.String r9 = "motion"
            boolean r5 = r9.equals(r5)
            if (r5 == 0) goto L_0x1006
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x1006:
            int r4 = r4 + 1
            goto L_0x0fe4
        L_0x1009:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r3.intensity = r4
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x1056 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x1056 }
            if (r4 != 0) goto L_0x1056
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x1056 }
            r5 = 6
            r6 = 0
            java.lang.String r9 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x1056 }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x1056 }
            r6 = r9 | r19
            r4.background_color = r6     // Catch:{ Exception -> 0x1056 }
            int r4 = r3.length()     // Catch:{ Exception -> 0x1056 }
            if (r4 <= r5) goto L_0x1056
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x1056 }
            r5 = 7
            java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x1056 }
            r5 = 16
            int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x1056 }
            r3 = r3 | r19
            r4.second_background_color = r3     // Catch:{ Exception -> 0x1056 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x1056 }
            r4 = 45
            r3.rotation = r4     // Catch:{ Exception -> 0x1056 }
        L_0x1056:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x106e }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x106e }
            if (r3 != 0) goto L_0x106e
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x106e }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x106e }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x106e }
            r3.rotation = r0     // Catch:{ Exception -> 0x106e }
        L_0x106e:
            r49 = r1
            r0 = r12
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r9 = r6
            r11 = r9
            r13 = r11
            r19 = r13
            r24 = r19
            r25 = r24
            r28 = r25
            r29 = r28
            r41 = r29
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r47 = r46
            r48 = r47
            r50 = r48
            goto L_0x126d
        L_0x1097:
            r12 = 0
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r9.replace(r0, r5)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r1 = "channel"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r3.intValue()
            if (r4 == 0) goto L_0x10c8
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x10ca
        L_0x10c8:
            r1 = r12
            r3 = r1
        L_0x10ca:
            r4 = r28
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r5 = r4.intValue()
            if (r5 != 0) goto L_0x10db
            r4 = r12
        L_0x10db:
            java.lang.String r5 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r5)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r5 = r0.intValue()
            if (r5 != 0) goto L_0x1104
            r19 = r1
            r11 = r3
            r41 = r4
            r0 = r12
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r9 = r6
            r13 = r9
            r24 = r13
            r25 = r24
            r28 = r25
            r29 = r28
            r42 = r29
            goto L_0x125d
        L_0x1104:
            r42 = r0
            r19 = r1
            r11 = r3
            r41 = r4
            r0 = r12
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r9 = r6
            r13 = r9
            r24 = r13
            r25 = r24
            r28 = r25
            r29 = r28
            r43 = r29
            goto L_0x125f
        L_0x111f:
            r0 = r28
            r12 = 0
            java.lang.String r1 = "tg:resolve"
            java.lang.String r1 = r9.replace(r1, r5)
            java.lang.String r3 = "tg://resolve"
            java.lang.String r1 = r1.replace(r3, r5)
            android.net.Uri r1 = android.net.Uri.parse(r1)
            java.lang.String r3 = "domain"
            java.lang.String r3 = r1.getQueryParameter(r3)
            java.lang.String r5 = "telegrampassport"
            boolean r5 = r5.equals(r3)
            if (r5 == 0) goto L_0x11ab
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.lang.String r3 = r1.getQueryParameter(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x116d
            java.lang.String r5 = "{"
            boolean r5 = r3.startsWith(r5)
            if (r5 == 0) goto L_0x116d
            java.lang.String r5 = "}"
            boolean r5 = r3.endsWith(r5)
            if (r5 == 0) goto L_0x116d
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r1.getQueryParameter(r5)
            java.lang.String r9 = "nonce"
            r0.put(r9, r5)
            goto L_0x1174
        L_0x116d:
            java.lang.String r5 = r1.getQueryParameter(r11)
            r0.put(r11, r5)
        L_0x1174:
            java.lang.String r5 = r1.getQueryParameter(r10)
            r0.put(r10, r5)
            r0.put(r4, r3)
            java.lang.String r3 = r1.getQueryParameter(r6)
            r0.put(r6, r3)
            java.lang.String r1 = r1.getQueryParameter(r13)
            r0.put(r13, r1)
            r44 = r0
            r0 = r12
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r9 = r6
            r11 = r9
            r13 = r11
            r19 = r13
            r24 = r19
            r25 = r24
            r28 = r25
            r29 = r28
            r41 = r29
            r42 = r41
            r43 = r42
            r45 = r43
            goto L_0x1263
        L_0x11ab:
            java.lang.String r4 = "start"
            java.lang.String r4 = r1.getQueryParameter(r4)
            java.lang.String r5 = "startgroup"
            java.lang.String r5 = r1.getQueryParameter(r5)
            java.lang.String r6 = "game"
            java.lang.String r6 = r1.getQueryParameter(r6)
            java.lang.String r9 = "post"
            java.lang.String r9 = r1.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r10 = r9.intValue()
            if (r10 != 0) goto L_0x11ce
            r9 = r12
        L_0x11ce:
            java.lang.String r0 = r1.getQueryParameter(r0)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r10 = r0.intValue()
            if (r10 != 0) goto L_0x11dd
            r0 = r12
        L_0x11dd:
            java.lang.String r10 = "comment"
            java.lang.String r1 = r1.getQueryParameter(r10)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r10 = r1.intValue()
            r41 = r0
            if (r10 != 0) goto L_0x1204
            r43 = r6
            r11 = r9
            r0 = r12
            r1 = r0
            r13 = r1
            r19 = r13
            r24 = r19
            r25 = r24
            r28 = r25
            r29 = r28
            r42 = r29
            r44 = r42
            goto L_0x1218
        L_0x1204:
            r42 = r1
            r43 = r6
            r11 = r9
            r0 = r12
            r1 = r0
            r13 = r1
            r19 = r13
            r24 = r19
            r25 = r24
            r28 = r25
            r29 = r28
            r44 = r29
        L_0x1218:
            r45 = r44
            r46 = r45
            r47 = r46
            r48 = r47
            r49 = r48
            r50 = r49
            r10 = 0
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
            r6 = r4
            r9 = r5
            r4 = r50
            r5 = r4
            goto L_0x1284
        L_0x1241:
            r27 = r4
            r12 = r9
            r26 = r11
        L_0x1246:
            r0 = r12
            r1 = r0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r9 = r6
            r11 = r9
            r13 = r11
            r19 = r13
            r24 = r19
            r25 = r24
            r28 = r25
            r29 = r28
            r41 = r29
            r42 = r41
        L_0x125d:
            r43 = r42
        L_0x125f:
            r44 = r43
            r45 = r44
        L_0x1263:
            r46 = r45
            r47 = r46
            r48 = r47
            r49 = r48
            r50 = r49
        L_0x126d:
            r10 = 0
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
        L_0x1284:
            boolean r22 = r14.hasExtra(r7)
            if (r22 == 0) goto L_0x12d0
            int r12 = r15.currentAccount
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
            boolean r12 = r12.isClientActivated()
            if (r12 == 0) goto L_0x12a6
            java.lang.String r12 = "tg"
            r22 = r2
            r2 = r27
            boolean r2 = r12.equals(r2)
            if (r2 == 0) goto L_0x12a8
            if (r46 != 0) goto L_0x12a8
            r2 = 1
            goto L_0x12a9
        L_0x12a6:
            r22 = r2
        L_0x12a8:
            r2 = 0
        L_0x12a9:
            com.google.firebase.appindexing.builders.AssistActionBuilder r12 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r12.<init>()
            r27 = r8
            java.lang.String r8 = r14.getStringExtra(r7)
            r12.setActionToken(r8)
            if (r2 == 0) goto L_0x12bc
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x12be
        L_0x12bc:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x12be:
            r12.setActionStatus(r2)
            com.google.firebase.appindexing.Action r2 = r12.build()
            com.google.firebase.appindexing.FirebaseUserActions r8 = com.google.firebase.appindexing.FirebaseUserActions.getInstance()
            r8.end(r2)
            r14.removeExtra(r7)
            goto L_0x12d4
        L_0x12d0:
            r22 = r2
            r27 = r8
        L_0x12d4:
            if (r47 != 0) goto L_0x12ea
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x12e3
            goto L_0x12ea
        L_0x12e3:
            r2 = r15
            r57 = r26
            r51 = 0
            goto L_0x1438
        L_0x12ea:
            if (r13 != 0) goto L_0x141b
            if (r0 == 0) goto L_0x12f0
            goto L_0x141b
        L_0x12f0:
            if (r3 != 0) goto L_0x13d0
            if (r4 != 0) goto L_0x13d0
            if (r5 != 0) goto L_0x13d0
            if (r1 != 0) goto L_0x13d0
            if (r43 != 0) goto L_0x13d0
            if (r44 != 0) goto L_0x13d0
            if (r46 != 0) goto L_0x13d0
            if (r45 != 0) goto L_0x13d0
            if (r47 != 0) goto L_0x13d0
            if (r49 != 0) goto L_0x13d0
            if (r19 != 0) goto L_0x13d0
            if (r50 != 0) goto L_0x13d0
            if (r48 == 0) goto L_0x130c
            goto L_0x13d0
        L_0x130c:
            android.content.ContentResolver r51 = r60.getContentResolver()     // Catch:{ Exception -> 0x13bd }
            android.net.Uri r52 = r61.getData()     // Catch:{ Exception -> 0x13bd }
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            android.database.Cursor r1 = r51.query(r52, r53, r54, r55, r56)     // Catch:{ Exception -> 0x13bd }
            if (r1 == 0) goto L_0x13ac
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x139e }
            if (r0 == 0) goto L_0x13ac
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x139e }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x139e }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x139e }
            int r0 = r0.intValue()     // Catch:{ all -> 0x139e }
            r2 = 0
            r7 = 3
        L_0x133c:
            if (r2 >= r7) goto L_0x1358
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1356 }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x1356 }
            if (r3 != r0) goto L_0x1352
            r3 = 0
            r26[r3] = r2     // Catch:{ all -> 0x1356 }
            r0 = r26[r3]     // Catch:{ all -> 0x1356 }
            r13 = 1
            r15.switchToAccount(r0, r13)     // Catch:{ all -> 0x139c }
            goto L_0x1359
        L_0x1352:
            r13 = 1
            int r2 = r2 + 1
            goto L_0x133c
        L_0x1356:
            r0 = move-exception
            goto L_0x13a0
        L_0x1358:
            r13 = 1
        L_0x1359:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x139c }
            int r2 = r1.getInt(r0)     // Catch:{ all -> 0x139c }
            r3 = 0
            r0 = r26[r3]     // Catch:{ all -> 0x139c }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x139c }
            int r4 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x139c }
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x139c }
            r0.postNotificationName(r4, r5)     // Catch:{ all -> 0x139c }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1398 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1398 }
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r3 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x1398 }
            if (r3 == 0) goto L_0x1388
            r36 = r2
            r4 = 1
            goto L_0x13b0
        L_0x1388:
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x1398 }
            r36 = r2
            r4 = r32
            if (r0 == 0) goto L_0x13b0
            r33 = 1
            goto L_0x13b0
        L_0x1398:
            r0 = move-exception
            r36 = r2
            goto L_0x13a1
        L_0x139c:
            r0 = move-exception
            goto L_0x13a1
        L_0x139e:
            r0 = move-exception
            r7 = 3
        L_0x13a0:
            r13 = 1
        L_0x13a1:
            throw r0     // Catch:{ all -> 0x13a2 }
        L_0x13a2:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x13a9
            r1.close()     // Catch:{ all -> 0x13a9 }
        L_0x13a9:
            throw r2     // Catch:{ Exception -> 0x13aa }
        L_0x13aa:
            r0 = move-exception
            goto L_0x13c0
        L_0x13ac:
            r7 = 3
            r13 = 1
            r4 = r32
        L_0x13b0:
            if (r1 == 0) goto L_0x13ba
            r1.close()     // Catch:{ Exception -> 0x13b6 }
            goto L_0x13ba
        L_0x13b6:
            r0 = move-exception
            r32 = r4
            goto L_0x13c0
        L_0x13ba:
            r32 = r4
            goto L_0x13c3
        L_0x13bd:
            r0 = move-exception
            r7 = 3
            r13 = 1
        L_0x13c0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x13c3:
            r2 = r15
            r9 = r24
            r57 = r26
            r13 = r30
            r12 = r36
            r51 = 0
            goto L_0x145f
        L_0x13d0:
            r7 = 3
            r13 = 1
            if (r1 == 0) goto L_0x13ef
            java.lang.String r0 = "@"
            boolean r0 = r1.startsWith(r0)
            if (r0 == 0) goto L_0x13ef
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = " "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r8 = r0
            goto L_0x13f0
        L_0x13ef:
            r8 = r1
        L_0x13f0:
            r12 = 0
            r2 = r26[r12]
            r22 = 0
            r1 = r60
            r51 = 0
            r7 = r9
            r9 = 2
            r9 = r10
            r10 = r11
            r57 = r26
            r11 = r19
            r12 = r41
            r13 = r42
            r14 = r43
            r15 = r44
            r16 = r45
            r17 = r46
            r18 = r47
            r19 = r48
            r20 = r49
            r21 = r50
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r2 = r60
            goto L_0x1438
        L_0x141b:
            r57 = r26
            r51 = 0
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r2 = r27
            r1.putString(r2, r13)
            r2 = r22
            r1.putString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$VK-kZ7jRryo5GCLASSNAMEoFxf--u3Egg r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$VK-kZ7jRryo5GCLASSNAMEoFxf--u3Egg
            r2 = r60
            r0.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x1438:
            r9 = r24
            r13 = r30
            r12 = r36
            goto L_0x145f
        L_0x143f:
            r51 = r5
            r57 = r11
            r2 = r15
            r9 = 0
            r12 = 0
            r13 = 0
            r25 = 0
            r28 = 0
            r29 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
        L_0x145f:
            r1 = r61
            r8 = r12
            r59 = r13
            r0 = r25
            r4 = r28
            r5 = r29
            r12 = r32
            r7 = r33
            r10 = r37
            r13 = r38
            r58 = r39
            r16 = r51
            r6 = r57
            r11 = 0
            r14 = 0
            r15 = 0
            r18 = 0
            r19 = 0
            goto L_0x15aa
        L_0x1481:
            r51 = r5
            r57 = r11
            r2 = r15
            java.lang.String r0 = r61.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x14b6
            r1 = r61
            r16 = r51
            r6 = r57
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
            r19 = 0
            r31 = 0
            r34 = 0
            r35 = 0
            r40 = 0
            r58 = 0
            r59 = 1
            goto L_0x15aa
        L_0x14b6:
            java.lang.String r0 = r61.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x14da
            r1 = r61
            r16 = r51
            r6 = r57
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
            r19 = 1
            goto L_0x159e
        L_0x14da:
            java.lang.String r0 = r61.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x1549
            java.lang.String r0 = "chatId"
            r1 = r61
            r3 = 0
            int r0 = r1.getIntExtra(r0, r3)
            java.lang.String r4 = "userId"
            int r4 = r1.getIntExtra(r4, r3)
            java.lang.String r5 = "encId"
            int r5 = r1.getIntExtra(r5, r3)
            if (r0 == 0) goto L_0x1511
            r6 = r57
            r4 = r6[r3]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r4.postNotificationName(r5, r7)
            r5 = 0
        L_0x150e:
            r12 = 0
        L_0x150f:
            r13 = 0
            goto L_0x153b
        L_0x1511:
            r6 = r57
            if (r4 == 0) goto L_0x1526
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r7)
            r12 = r4
            r0 = 0
            r5 = 0
            goto L_0x150f
        L_0x1526:
            if (r5 == 0) goto L_0x1537
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r7)
            r0 = 0
            goto L_0x150e
        L_0x1537:
            r0 = 0
            r5 = 0
            r12 = 0
            r13 = 1
        L_0x153b:
            r10 = r0
            r11 = r5
            r8 = r12
            r14 = r13
            r16 = r51
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            goto L_0x1599
        L_0x1549:
            r1 = r61
            r6 = r57
            r3 = 0
            java.lang.String r0 = r61.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1569
            r16 = r51
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
            goto L_0x159a
        L_0x1569:
            java.lang.String r0 = r61.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x158c
            r16 = r51
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
            goto L_0x159c
        L_0x1586:
            r51 = r5
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
        L_0x158c:
            r16 = r51
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x1597:
            r13 = 0
            r14 = 0
        L_0x1599:
            r15 = 0
        L_0x159a:
            r18 = 0
        L_0x159c:
            r19 = 0
        L_0x159e:
            r31 = 0
            r34 = 0
            r35 = 0
            r40 = 0
            r58 = 0
            r59 = 0
        L_0x15aa:
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x1b07
            if (r9 == 0) goto L_0x15df
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.getLastFragment()
            r63 = r14
            boolean r14 = r3 instanceof org.telegram.ui.DialogsActivity
            if (r14 == 0) goto L_0x15dc
            org.telegram.ui.DialogsActivity r3 = (org.telegram.ui.DialogsActivity) r3
            boolean r14 = r3.isMainDialogList()
            if (r14 == 0) goto L_0x15e1
            android.view.View r14 = r3.getFragmentView()
            if (r14 == 0) goto L_0x15d7
            r14 = 1
            r3.search(r9, r14)
            goto L_0x15e2
        L_0x15d7:
            r14 = 1
            r3.setInitialSearchString(r9)
            goto L_0x15e2
        L_0x15dc:
            r14 = 1
            r3 = 1
            goto L_0x15e4
        L_0x15df:
            r63 = r14
        L_0x15e1:
            r14 = 1
        L_0x15e2:
            r3 = r63
        L_0x15e4:
            if (r8 == 0) goto L_0x165c
            if (r12 != 0) goto L_0x163c
            if (r7 == 0) goto L_0x15eb
            goto L_0x163c
        L_0x15eb:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "user_id"
            r0.putInt(r3, r8)
            if (r13 == 0) goto L_0x15fd
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r13)
        L_0x15fd:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x161f
            r3 = 0
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r14
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r3 = r3.checkCanOpenChat(r0, r4)
            if (r3 == 0) goto L_0x1639
        L_0x161f:
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r3
            boolean r0 = r15.presentFragment(r16, r17, r18, r19, r20)
            if (r0 == 0) goto L_0x1639
        L_0x1636:
            r13 = 1
            goto L_0x16ca
        L_0x1639:
            r13 = 0
            goto L_0x16ca
        L_0x163c:
            if (r58 == 0) goto L_0x1656
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x1659
            org.telegram.messenger.MessagesController r3 = r0.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r3, r7)
            goto L_0x1659
        L_0x1656:
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r2, r8, r7)
        L_0x1659:
            r4 = 0
            goto L_0x1b09
        L_0x165c:
            if (r10 == 0) goto L_0x16a9
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "chat_id"
            r0.putInt(r3, r10)
            if (r13 == 0) goto L_0x166f
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r13)
        L_0x166f:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x1691
            r3 = 0
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r14
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r3 = r3.checkCanOpenChat(r0, r4)
            if (r3 == 0) goto L_0x1639
        L_0x1691:
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r3
            boolean r0 = r15.presentFragment(r16, r17, r18, r19, r20)
            if (r0 == 0) goto L_0x1639
            goto L_0x1636
        L_0x16a9:
            if (r11 == 0) goto L_0x16cf
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "enc_id"
            r0.putInt(r3, r11)
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r3
            boolean r13 = r15.presentFragment(r16, r17, r18, r19, r20)
        L_0x16ca:
            r8 = r62
            r4 = 0
            goto L_0x1b0c
        L_0x16cf:
            if (r3 == 0) goto L_0x170a
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x16dd
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1708
        L_0x16dd:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1708
        L_0x16e7:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r14
            if (r0 <= 0) goto L_0x1701
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsStack
            r8 = 0
            java.lang.Object r3 = r3.get(r8)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r3)
            goto L_0x16e7
        L_0x1701:
            r8 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.closeLastFragment(r8)
            goto L_0x172b
        L_0x1708:
            r8 = 0
            goto L_0x172b
        L_0x170a:
            r8 = 0
            if (r15 == 0) goto L_0x172e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1729
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r8)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r3 = new org.telegram.ui.Components.AudioPlayerAlert
            r3.<init>(r2)
            r0.showDialog(r3)
        L_0x1729:
            r8 = r62
        L_0x172b:
            r4 = 0
            goto L_0x1b0b
        L_0x172e:
            if (r18 == 0) goto L_0x1753
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1729
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r3 = 0
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r3 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$YHxLpwvfAJ-XuUMLXdxr2hO5tqk r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$YHxLpwvfAJ-XuUMLXdxr2hO5tqk
            r4.<init>(r6)
            r3.<init>(r2, r4)
            r0.showDialog(r3)
            goto L_0x1729
        L_0x1753:
            java.lang.String r3 = r2.videoPath
            if (r3 != 0) goto L_0x19c6
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r2.photoPathsArray
            if (r3 != 0) goto L_0x19c6
            java.lang.String r3 = r2.sendingText
            if (r3 != 0) goto L_0x19c6
            java.util.ArrayList<java.lang.String> r3 = r2.documentsPathsArray
            if (r3 != 0) goto L_0x19c6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r2.contactsToSend
            if (r3 != 0) goto L_0x19c6
            java.util.ArrayList<android.net.Uri> r3 = r2.documentsUrisArray
            if (r3 == 0) goto L_0x176d
            goto L_0x19c6
        L_0x176d:
            r3 = r59
            if (r3 == 0) goto L_0x17dd
            if (r3 != r14) goto L_0x178e
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
        L_0x178c:
            r13 = 0
            goto L_0x17b7
        L_0x178e:
            r6 = 2
            if (r3 != r6) goto L_0x1798
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r4 = 0
            r0.<init>(r4)
            goto L_0x178c
        L_0x1798:
            r4 = 0
            r5 = 3
            if (r3 != r5) goto L_0x17a2
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r4)
            goto L_0x178c
        L_0x17a2:
            r0 = 4
            if (r3 != r0) goto L_0x17ab
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x178c
        L_0x17ab:
            r0 = 5
            if (r3 != r0) goto L_0x17b5
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r0.<init>(r5)
            r13 = 1
            goto L_0x17b7
        L_0x17b5:
            r0 = 0
            goto L_0x178c
        L_0x17b7:
            org.telegram.ui.-$$Lambda$LaunchActivity$khhShGuCQfuJII48gK_zJ7yqvar_ r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$khhShGuCQfuJII48gK_zJ7yqvar_
            r3.<init>(r0, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x17d6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x181b
        L_0x17d6:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x181b
        L_0x17dd:
            r6 = 2
            if (r19 == 0) goto L_0x1821
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "destroyAfterSelect"
            r0.putBoolean(r3, r14)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            org.telegram.ui.ContactsActivity r3 = new org.telegram.ui.ContactsActivity
            r3.<init>(r0)
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r3
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1815
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x181b
        L_0x1815:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
        L_0x181b:
            r8 = r62
            r4 = 0
        L_0x181e:
            r13 = 1
            goto L_0x1b0c
        L_0x1821:
            if (r0 == 0) goto L_0x1880
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.lang.String r4 = "destroyAfterSelect"
            r3.putBoolean(r4, r14)
            java.lang.String r4 = "returnAsResult"
            r3.putBoolean(r4, r14)
            java.lang.String r4 = "onlyUsers"
            r3.putBoolean(r4, r14)
            java.lang.String r4 = "allowSelf"
            r5 = 0
            r3.putBoolean(r4, r5)
            org.telegram.ui.ContactsActivity r4 = new org.telegram.ui.ContactsActivity
            r4.<init>(r3)
            r4.setInitialSearchString(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$_L_5IaBoty343YZQPMn860Vrvd8 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$_L_5IaBoty343YZQPMn860Vrvd8
            r0.<init>(r7)
            r4.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r15.getLastFragment()
            boolean r0 = r0 instanceof org.telegram.ui.ContactsActivity
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r4
            r17 = r0
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1879
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x181b
        L_0x1879:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x181b
        L_0x1880:
            if (r35 == 0) goto L_0x18bf
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r3 = 5
            r0.<init>(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$8KPGrhe6Jj04g840aKU1DGBb1qE r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$8KPGrhe6Jj04g840aKU1DGBb1qE
            r3.<init>(r0)
            r0.setQrLoginDelegate(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r0
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x18b7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x181b
        L_0x18b7:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x181b
        L_0x18bf:
            r3 = 0
            if (r34 == 0) goto L_0x1913
            org.telegram.ui.NewContactActivity r0 = new org.telegram.ui.NewContactActivity
            r0.<init>()
            if (r4 == 0) goto L_0x18db
            java.lang.String r7 = " "
            java.lang.String[] r4 = r4.split(r7, r6)
            r6 = r4[r3]
            int r7 = r4.length
            if (r7 <= r14) goto L_0x18d7
            r4 = r4[r14]
            goto L_0x18d8
        L_0x18d7:
            r4 = 0
        L_0x18d8:
            r0.setInitialName(r6, r4)
        L_0x18db:
            if (r5 == 0) goto L_0x18e4
            java.lang.String r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r5, r14)
            r0.setInitialPhoneNumber(r4, r3)
        L_0x18e4:
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r0
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x190b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x181b
        L_0x190b:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x181b
        L_0x1913:
            if (r40 == 0) goto L_0x1991
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x198b
            android.app.Activity r3 = r0.getParentActivity()
            if (r3 == 0) goto L_0x198b
            int r3 = r2.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
            r6 = 0
            java.lang.String r3 = org.telegram.ui.NewContactActivity.getPhoneNumber(r2, r3, r5, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r0.getParentActivity()
            r5.<init>((android.content.Context) r6)
            r6 = 2131625993(0x7f0e0809, float:1.887921E38)
            java.lang.String r7 = "NewContactAlertTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setTitle(r6)
            r6 = 2131625992(0x7f0e0808, float:1.8879208E38)
            java.lang.Object[] r7 = new java.lang.Object[r14]
            org.telegram.PhoneFormat.PhoneFormat r8 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r8 = r8.format(r3)
            r10 = 0
            r7[r10] = r8
            java.lang.String r8 = "NewContactAlertMessage"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r7)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            r5.setMessage(r6)
            r6 = 2131625991(0x7f0e0807, float:1.8879206E38)
            java.lang.String r7 = "NewContactAlertButton"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.-$$Lambda$LaunchActivity$sE5-pyjtIyn8IQYwQNwWAj1sFSE r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$sE5-pyjtIyn8IQYwQNwWAj1sFSE
            r7.<init>(r3, r4, r0)
            r5.setPositiveButton(r6, r7)
            r3 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r4 = "Cancel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 0
            r5.setNegativeButton(r3, r4)
            org.telegram.ui.ActionBar.AlertDialog r3 = r5.create()
            r0.showDialog(r3)
            r13 = 1
            goto L_0x198d
        L_0x198b:
            r4 = 0
            r13 = 0
        L_0x198d:
            r8 = r62
            goto L_0x1b0c
        L_0x1991:
            r4 = 0
            if (r31 == 0) goto L_0x1b09
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            org.telegram.ui.CallLogActivity r16 = new org.telegram.ui.CallLogActivity
            r16.<init>()
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 0
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x19be
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1af2
        L_0x19be:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x1af2
        L_0x19c6:
            r3 = 0
            r4 = 0
            r5 = 3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x19dc
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r6 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r6, r7)
        L_0x19dc:
            int r0 = (r16 > r51 ? 1 : (r16 == r51 ? 0 : -1))
            if (r0 != 0) goto L_0x1af6
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "onlySelect"
            r0.putBoolean(r3, r14)
            java.lang.String r3 = "dialogsType"
            r0.putInt(r3, r5)
            java.lang.String r3 = "allowSwitchAccount"
            r0.putBoolean(r3, r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r2.contactsToSend
            if (r3 == 0) goto L_0x1a1b
            int r3 = r3.size()
            if (r3 == r14) goto L_0x1a37
            r3 = 2131627042(0x7f0e0CLASSNAME, float:1.8881337E38)
            java.lang.String r5 = "SendContactToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r3)
            r3 = 2131627019(0x7f0e0c0b, float:1.888129E38)
            java.lang.String r5 = "SendContactToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r3)
            goto L_0x1a37
        L_0x1a1b:
            r3 = 2131627042(0x7f0e0CLASSNAME, float:1.8881337E38)
            java.lang.String r5 = "SendMessagesToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r3)
            r3 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            java.lang.String r5 = "SendMessagesToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r3)
        L_0x1a37:
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r0)
            r3.setDelegate(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1a61
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x1a7e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r5 = r0.size()
            int r5 = r5 - r14
            java.lang.Object r0 = r0.get(r5)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1a7e
            goto L_0x1a7c
        L_0x1a61:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r14) goto L_0x1a7e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r5 = r0.size()
            int r5 = r5 - r14
            java.lang.Object r0 = r0.get(r5)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x1a7e
        L_0x1a7c:
            r13 = 1
            goto L_0x1a7f
        L_0x1a7e:
            r13 = 0
        L_0x1a7f:
            r17 = r13
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r2.actionBarLayout
            r18 = 1
            r19 = 1
            r20 = 0
            r16 = r3
            r15.presentFragment(r16, r17, r18, r19, r20)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x1aa7
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1aa7
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r3 = 0
            r0.closePhoto(r3, r3)
            goto L_0x1ad7
        L_0x1aa7:
            r3 = 0
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x1ac0
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1ac0
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r3, r14)
            goto L_0x1ad7
        L_0x1ac0:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x1ad7
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1ad7
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r3, r14)
        L_0x1ad7:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1aed
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x1af2
        L_0x1aed:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r14, r3)
        L_0x1af2:
            r8 = r62
            goto L_0x181e
        L_0x1af6:
            r3 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r5 = java.lang.Long.valueOf(r16)
            r0.add(r5)
            r2.didSelectDialogs(r4, r0, r4, r3)
            goto L_0x1b09
        L_0x1b07:
            r4 = 0
            r14 = 1
        L_0x1b09:
            r8 = r62
        L_0x1b0b:
            r13 = 0
        L_0x1b0c:
            if (r13 != 0) goto L_0x1bb8
            if (r8 != 0) goto L_0x1bb8
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1b62
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1b3d
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1ba3
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            org.telegram.ui.LoginActivity r3 = new org.telegram.ui.LoginActivity
            r3.<init>()
            r0.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1ba3
        L_0x1b3d:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1ba3
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r2.sideMenu
            r0.setSideMenu(r3)
            if (r9 == 0) goto L_0x1b56
            r0.setInitialSearchString(r9)
        L_0x1b56:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            r3.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r14, r3)
            goto L_0x1ba3
        L_0x1b62:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1ba3
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1b89
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.LoginActivity r3 = new org.telegram.ui.LoginActivity
            r3.<init>()
            r0.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1ba3
        L_0x1b89:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r2.sideMenu
            r0.setSideMenu(r3)
            if (r9 == 0) goto L_0x1b98
            r0.setInitialSearchString(r9)
        L_0x1b98:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r2.actionBarLayout
            r3.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r3 = 0
            r0.setAllowOpenDrawer(r14, r3)
        L_0x1ba3:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1bb8
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
        L_0x1bb8:
            if (r23 == 0) goto L_0x1bbd
            org.telegram.ui.VoIPFragment.show(r60)
        L_0x1bbd:
            r1.setAction(r4)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$6 */
    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$7 */
    public /* synthetic */ void lambda$handleIntent$7$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$42(new CancelAccountDeletionActivity(bundle));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$9 */
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
        lambda$runLinkRequest$42(locationActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$10 */
    public /* synthetic */ void lambda$handleIntent$10$LaunchActivity(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$11 */
    public /* synthetic */ void lambda$handleIntent$11$LaunchActivity(boolean z, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$15 */
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

    static /* synthetic */ void lambda$handleIntent$16(String str, String str2, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        NewContactActivity newContactActivity = new NewContactActivity();
        newContactActivity.setInitialPhoneNumber(str, false);
        if (str2 != null) {
            String[] split = str2.split(" ", 2);
            newContactActivity.setInitialName(split[0], split.length > 1 ? split[1] : null);
        }
        baseFragment.presentFragment(newContactActivity);
    }

    private int runCommentRequest(int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            return 0;
        }
        TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage = new TLRPC$TL_messages_getDiscussionMessage();
        tLRPC$TL_messages_getDiscussionMessage.peer = MessagesController.getInputPeer(tLRPC$Chat);
        tLRPC$TL_messages_getDiscussionMessage.msg_id = (num2 != null ? num : num3).intValue();
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new RequestDelegate(i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ Integer f$2;
            public final /* synthetic */ TLRPC$Chat f$3;
            public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$4;
            public final /* synthetic */ Integer f$5;
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

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$runCommentRequest$18$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runCommentRequest$18 */
    public /* synthetic */ void lambda$runCommentRequest$18$LaunchActivity(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ Integer f$3;
            public final /* synthetic */ TLRPC$Chat f$4;
            public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$5;
            public final /* synthetic */ Integer f$6;
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
                LaunchActivity.this.lambda$null$17$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0095  */
    /* renamed from: lambda$null$17 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$17$LaunchActivity(org.telegram.tgnet.TLObject r13, int r14, java.lang.Integer r15, org.telegram.tgnet.TLRPC$Chat r16, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r17, java.lang.Integer r18, java.lang.Integer r19, org.telegram.ui.ActionBar.AlertDialog r20) {
        /*
            r12 = this;
            r1 = r12
            r0 = r13
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_discussionMessage
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x0092
            org.telegram.tgnet.TLRPC$TL_messages_discussionMessage r0 = (org.telegram.tgnet.TLRPC$TL_messages_discussionMessage) r0
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r0.users
            r2.putUsers(r5, r4)
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r0.chats
            r2.putChats(r5, r4)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r0.messages
            int r2 = r2.size()
            r5 = 0
        L_0x0028:
            if (r5 >= r2) goto L_0x003f
            org.telegram.messenger.MessageObject r6 = new org.telegram.messenger.MessageObject
            int r8 = org.telegram.messenger.UserConfig.selectedAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r0.messages
            java.lang.Object r9 = r9.get(r5)
            org.telegram.tgnet.TLRPC$Message r9 = (org.telegram.tgnet.TLRPC$Message) r9
            r6.<init>(r8, r9, r3, r3)
            r7.add(r6)
            int r5 = r5 + 1
            goto L_0x0028
        L_0x003f:
            boolean r2 = r7.isEmpty()
            if (r2 != 0) goto L_0x0092
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.Object r5 = r7.get(r4)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r5 = r5.getDialogId()
            long r5 = -r5
            int r6 = (int) r5
            java.lang.String r5 = "chat_id"
            r2.putInt(r5, r6)
            int r5 = r15.intValue()
            int r5 = java.lang.Math.max(r3, r5)
            java.lang.String r6 = "message_id"
            r2.putInt(r6, r5)
            org.telegram.ui.ChatActivity r5 = new org.telegram.ui.ChatActivity
            r5.<init>(r2)
            r2 = r17
            int r9 = r2.msg_id
            int r10 = r0.read_inbox_max_id
            int r11 = r0.read_outbox_max_id
            r6 = r5
            r8 = r16
            r6.setThreadMessages(r7, r8, r9, r10, r11)
            if (r18 == 0) goto L_0x0085
            int r0 = r18.intValue()
            r5.setHighlightMessageId(r0)
            goto L_0x008e
        L_0x0085:
            if (r19 == 0) goto L_0x008e
            int r0 = r15.intValue()
            r5.setHighlightMessageId(r0)
        L_0x008e:
            r12.lambda$runLinkRequest$42(r5)
            goto L_0x0093
        L_0x0092:
            r3 = 0
        L_0x0093:
            if (r3 != 0) goto L_0x00aa
            java.lang.String r0 = "ChannelPostDeleted"
            r2 = 2131624680(0x7f0e02e8, float:1.8876547E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00a6 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r12, r0, r4)     // Catch:{ Exception -> 0x00a6 }
            r0.show()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00aa:
            r20.dismiss()     // Catch:{ Exception -> 0x00ae }
            goto L_0x00b3
        L_0x00ae:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00b3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$17$LaunchActivity(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x02cb  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x03cd  */
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
            org.telegram.ui.-$$Lambda$LaunchActivity$E7_509cX-6qDCQ2WtDzBzdaDtpI r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$E7_509cX-6qDCQ2WtDzBzdaDtpI
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
            r4 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.String r7 = "OK"
            r15 = 0
            r8 = 1
            r11 = 0
            if (r40 == 0) goto L_0x00b6
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r1)
            if (r0 == 0) goto L_0x0086
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r40
            r0.postNotificationName(r1, r2)
            goto L_0x00b5
        L_0x0086:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626297(0x7f0e0939, float:1.8879826E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r40
            java.lang.String r3 = "OtherLoginCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r15)
            r14.showAlertDialog(r0)
        L_0x00b5:
            return
        L_0x00b6:
            if (r41 == 0) goto L_0x00e0
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624378(0x7f0e01ba, float:1.8875934E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r2 = "AuthAnotherClientUrl"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r15)
            r14.showAlertDialog(r0)
            return
        L_0x00e0:
            org.telegram.ui.ActionBar.AlertDialog r10 = new org.telegram.ui.ActionBar.AlertDialog
            r4 = 3
            r10.<init>(r14, r4)
            int[] r7 = new int[r8]
            r7[r11] = r11
            if (r0 == 0) goto L_0x0125
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r13 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r13.<init>()
            r13.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$7CmMkTYibSieeJ5hluIpJo1KZ8s r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$7CmMkTYibSieeJ5hluIpJo1KZ8s
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
        L_0x0122:
            r4 = 0
            goto L_0x03c8
        L_0x0125:
            r25 = r10
            r12 = 0
            if (r5 == 0) goto L_0x0161
            if (r1 != 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$LDtT7crwD6ADPsWLJuKYeMgGDCI r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$LDtT7crwD6ADPsWLJuKYeMgGDCI
            r10 = r24
            r11 = r25
            r3.<init>(r10, r11, r5)
            int r0 = r1.sendRequest(r0, r3, r2)
            r7[r12] = r0
            goto L_0x0122
        L_0x0147:
            r10 = r24
            r11 = r25
            if (r1 != r8) goto L_0x0122
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r24)
            org.telegram.ui.-$$Lambda$LaunchActivity$GwUEI2a_5NpmVIzxztcNAoZcu5A r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$GwUEI2a_5NpmVIzxztcNAoZcu5A
            r3.<init>(r10, r11)
            r1.sendRequest(r0, r3, r2)
            goto L_0x0122
        L_0x0161:
            r10 = r24
            r11 = r25
            if (r6 == 0) goto L_0x01bf
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01be
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x01a8
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
            goto L_0x01bb
        L_0x01a8:
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
        L_0x01bb:
            r1.showDialog(r3)
        L_0x01be:
            return
        L_0x01bf:
            if (r9 == 0) goto L_0x01e4
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r8)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r4)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$IgTAe6w8G-5OZ7bACkqzn1uPvlg r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$IgTAe6w8G-5OZ7bACkqzn1uPvlg
            r2 = r31
            r0.<init>(r2, r10, r9)
            r1.setDelegate(r0)
            r14.presentFragment(r1, r12, r8)
            goto L_0x0122
        L_0x01e4:
            r0 = r37
            if (r0 == 0) goto L_0x0251
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x01fb
            return
        L_0x01fb:
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
            org.telegram.ui.-$$Lambda$LaunchActivity$eVFmP_tlCyUeNOlJlkKgWC6mSLQ r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$eVFmP_tlCyUeNOlJlkKgWC6mSLQ
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
            goto L_0x0122
        L_0x0251:
            r0 = r39
            if (r0 == 0) goto L_0x026f
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$4tW2XicmLEhP2ugN3S3jnsIgZ4Q r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$4tW2XicmLEhP2ugN3S3jnsIgZ4Q
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r12] = r0
            goto L_0x0122
        L_0x026f:
            java.lang.String r0 = "android"
            r1 = r38
            if (r1 == 0) goto L_0x0291
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r2 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r2.<init>()
            r2.lang_code = r1
            r2.lang_pack = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$VSjE1eN9mZifLPctvdAUSi0JYSI r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$VSjE1eN9mZifLPctvdAUSi0JYSI
            r1.<init>(r11)
            int r0 = r0.sendRequest(r2, r1)
            r7[r12] = r0
            goto L_0x0122
        L_0x0291:
            r1 = r42
            if (r1 == 0) goto L_0x02ee
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x02c7
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x02c1 }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x02c1 }
            int r4 = r3.background_color     // Catch:{ Exception -> 0x02c1 }
            int r5 = r3.second_background_color     // Catch:{ Exception -> 0x02c1 }
            int r3 = r3.rotation     // Catch:{ Exception -> 0x02c1 }
            int r3 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r3, r12)     // Catch:{ Exception -> 0x02c1 }
            r0.<init>(r2, r4, r5, r3)     // Catch:{ Exception -> 0x02c1 }
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x02c1 }
            r4 = 0
            r2.<init>(r0, r4)     // Catch:{ Exception -> 0x02bf }
            org.telegram.ui.-$$Lambda$LaunchActivity$IvZYWCJ5VP43AqVW5sLdG-n7LqU r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$IvZYWCJ5VP43AqVW5sLdG-n7LqU     // Catch:{ Exception -> 0x02bf }
            r0.<init>(r2)     // Catch:{ Exception -> 0x02bf }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x02bf }
            goto L_0x02c9
        L_0x02bf:
            r0 = move-exception
            goto L_0x02c3
        L_0x02c1:
            r0 = move-exception
            r4 = 0
        L_0x02c3:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02c8
        L_0x02c7:
            r4 = 0
        L_0x02c8:
            r8 = 0
        L_0x02c9:
            if (r8 != 0) goto L_0x03c8
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$1Wr0xeuHNbi5DBI4pwEviELoA8Q r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$1Wr0xeuHNbi5DBI4pwEviELoA8Q
            r3.<init>(r11, r1)
            int r0 = r2.sendRequest(r0, r3)
            r7[r12] = r0
            goto L_0x03c8
        L_0x02ee:
            r4 = 0
            if (r3 == 0) goto L_0x0319
            org.telegram.ui.-$$Lambda$LaunchActivity$PHEolRzREhC-tD2cRw66tgTYB78 r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$PHEolRzREhC-tD2cRw66tgTYB78
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
            org.telegram.ui.-$$Lambda$LaunchActivity$T-2wr2fQokNPsWdcuIPycWlr82Q r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$T-2wr2fQokNPsWdcuIPycWlr82Q
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r12] = r0
            goto L_0x03c9
        L_0x0319:
            if (r13 == 0) goto L_0x03c8
            if (r32 == 0) goto L_0x03c8
            if (r34 == 0) goto L_0x0376
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r24)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r13)
            if (r0 == 0) goto L_0x033f
            r25 = r23
            r26 = r24
            r27 = r11
            r28 = r32
            r29 = r35
            r30 = r34
            r31 = r0
            int r0 = r25.runCommentRequest(r26, r27, r28, r29, r30, r31)
            r7[r12] = r0
            goto L_0x03c8
        L_0x033f:
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
            org.telegram.ui.-$$Lambda$LaunchActivity$6ASbhnUTE94SjiiNtpHcA7AK7bM r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$6ASbhnUTE94SjiiNtpHcA7AK7bM
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
            goto L_0x03c8
        L_0x0376:
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
            if (r1 != 0) goto L_0x03a3
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03a4
        L_0x03a3:
            r1 = r4
        L_0x03a4:
            if (r1 == 0) goto L_0x03b0
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r24)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x03c8
        L_0x03b0:
            org.telegram.ui.-$$Lambda$LaunchActivity$sZhK88tPbXp-nXk1MdN1mdZNj1U r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$sZhK88tPbXp-nXk1MdN1mdZNj1U
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
        L_0x03c8:
            r15 = r4
        L_0x03c9:
            r0 = r7[r12]
            if (r0 == 0) goto L_0x03da
            org.telegram.ui.-$$Lambda$LaunchActivity$q9Ney3udn2R5Utqvs4W2s6wwTns r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$q9Ney3udn2R5Utqvs4W2s6wwTns
            r0.<init>(r10, r7, r15)
            r11.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r11.showDelayed(r0)     // Catch:{ Exception -> 0x03da }
        L_0x03da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$19 */
    public /* synthetic */ void lambda$runLinkRequest$19$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, Integer num3, Integer num4, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, num3, num4, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$25 */
    public /* synthetic */ void lambda$runLinkRequest$25$LaunchActivity(String str, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$24$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r0v13, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r0v60 */
    /* JADX WARNING: type inference failed for: r0v61 */
    /* access modifiers changed from: private */
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
    /* renamed from: lambda$null$24 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$24$LaunchActivity(org.telegram.tgnet.TLObject r15, org.telegram.tgnet.TLRPC$TL_error r16, java.lang.String r17, int r18, java.lang.Integer r19, java.lang.Integer r20, java.lang.Integer r21, int[] r22, org.telegram.ui.ActionBar.AlertDialog r23, java.lang.String r24, java.lang.String r25) {
        /*
            r14 = this;
            r8 = r14
            r0 = r17
            r2 = r18
            r9 = r23
            r1 = r24
            r3 = r25
            boolean r4 = r14.isFinishing()
            if (r4 != 0) goto L_0x02cd
            r4 = r15
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r4 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r4
            r10 = 1
            r11 = 0
            if (r16 != 0) goto L_0x02ad
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.actionBarLayout
            if (r5 == 0) goto L_0x02ad
            if (r0 == 0) goto L_0x0028
            if (r0 == 0) goto L_0x02ad
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x02ad
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
            if (r0 == 0) goto L_0x02c2
        L_0x0071:
            r10 = 0
            goto L_0x02c2
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
            r3 = 2131627025(0x7f0e0CLASSNAME, float:1.8881303E38)
            java.lang.String r5 = "SendGameToText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertString"
            r1.putString(r5, r3)
            r3 = 2131627024(0x7f0e0CLASSNAME, float:1.88813E38)
            java.lang.String r5 = "SendGameToGroupText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.String r5 = "selectAlertStringGroup"
            r1.putString(r5, r3)
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$h7eZsL0MdvagDvHFSBmmrwK5T9o r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$h7eZsL0MdvagDvHFSBmmrwK5T9o
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
            goto L_0x02c2
        L_0x0165:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r11)
            goto L_0x02c2
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
            r5 = 2131624198(0x7f0e0106, float:1.8875569E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$MerAbXncKyogjvar_aqSmadzWfgk r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$MerAbXncKyogjvar_aqSmadzWfgk
            r3.<init>(r2, r0, r1)
            r4.setDelegate(r3)
            r14.lambda$runLinkRequest$42(r4)
            goto L_0x02c2
        L_0x01c2:
            java.lang.String r0 = "BotCantJoinGroups"
            r1 = 2131624519(0x7f0e0247, float:1.887622E38)
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
            goto L_0x0225
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
        L_0x0225:
            if (r3 == 0) goto L_0x0242
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r4.users
            int r12 = r12.size()
            if (r12 <= 0) goto L_0x0242
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            java.lang.Object r4 = r4.get(r11)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0242
            java.lang.String r4 = "botUser"
            r1.putString(r4, r3)
            r4 = 1
            goto L_0x0243
        L_0x0242:
            r4 = 0
        L_0x0243:
            if (r19 == 0) goto L_0x024e
            int r12 = r19.intValue()
            java.lang.String r13 = "message_id"
            r1.putInt(r13, r12)
        L_0x024e:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r12 = mainFragmentsStack
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x0263
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r12 = r0.size()
            int r12 = r12 - r10
            java.lang.Object r0 = r0.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
        L_0x0263:
            if (r0 == 0) goto L_0x026f
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r12 = r12.checkCanOpenChat(r1, r0)
            if (r12 == 0) goto L_0x02c2
        L_0x026f:
            if (r4 == 0) goto L_0x0283
            boolean r4 = r0 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0283
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r12 = r0.getDialogId()
            int r4 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x0283
            r0.setBotUser(r3)
            goto L_0x02c2
        L_0x0283:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r19 != 0) goto L_0x028f
            r3 = 0
            goto L_0x0293
        L_0x028f:
            int r3 = r19.intValue()
        L_0x0293:
            org.telegram.ui.-$$Lambda$LaunchActivity$YXi_GwbOcmIgwVlAx7PUSqE09RY r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$YXi_GwbOcmIgwVlAx7PUSqE09RY
            r4.<init>(r9, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$F3eyh9F0BYvJlBs0PPumWBJNqbk r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$F3eyh9F0BYvJlBs0PPumWBJNqbk
            r1.<init>(r9)
            r15 = r0
            r16 = r5
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r1
            r15.ensureMessagesLoaded(r16, r18, r19, r20, r21)
            goto L_0x0071
        L_0x02ad:
            java.lang.String r0 = "NoUsernameFound"
            r1 = 2131626077(0x7f0e085d, float:1.887938E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x02be }
            android.widget.Toast r0 = android.widget.Toast.makeText(r14, r0, r11)     // Catch:{ Exception -> 0x02be }
            r0.show()     // Catch:{ Exception -> 0x02be }
            goto L_0x02c2
        L_0x02be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02c2:
            if (r10 == 0) goto L_0x02cd
            r23.dismiss()     // Catch:{ Exception -> 0x02c8 }
            goto L_0x02cd
        L_0x02c8:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x02cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$24$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$20 */
    public /* synthetic */ void lambda$null$20$LaunchActivity(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$21 */
    public /* synthetic */ void lambda$null$21$LaunchActivity(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$22 */
    public /* synthetic */ void lambda$null$22$LaunchActivity(AlertDialog alertDialog, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!isFinishing()) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$LaunchActivity(AlertDialog alertDialog) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$30 */
    public /* synthetic */ void lambda$runLinkRequest$30$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$29$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        if (r6.chat.has_geo != false) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0080, code lost:
        if (r0.checkCanOpenChat(r5, r1.get(r1.size() - 1)) != false) goto L_0x0082;
     */
    /* renamed from: lambda$null$29 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$29$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.ui.ActionBar.AlertDialog r21, java.lang.String r22) {
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
            org.telegram.ui.-$$Lambda$LaunchActivity$2ENiw1BEvBV5AsQzjhd3XoIuQZo r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$2ENiw1BEvBV5AsQzjhd3XoIuQZo
            r0.<init>(r4)
            r8.setOnCancelListener(r0)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.chat
            int r1 = r0.id
            int r1 = -r1
            long r11 = (long) r1
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r0)
            r14 = 0
            org.telegram.ui.-$$Lambda$LaunchActivity$3jCINMV4Atu3T7fa4NOFj-nSwg8 r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$3jCINMV4Atu3T7fa4NOFj-nSwg8
            r1 = r15
            r2 = r17
            r3 = r21
            r1.<init>(r3, r4, r5, r6)
            org.telegram.ui.-$$Lambda$LaunchActivity$kwJoEoLSBcIcM9zRQMLdRmViDmQ r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$kwJoEoLSBcIcM9zRQMLdRmViDmQ
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
            r4 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r5 = "AppName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            java.lang.String r0 = r0.text
            java.lang.String r4 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r4)
            if (r0 == 0) goto L_0x00f1
            r0 = 2131625434(0x7f0e05da, float:1.8878076E38)
            java.lang.String r4 = "FloodWait"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
            goto L_0x00fd
        L_0x00f1:
            r0 = 2131625686(0x7f0e06d6, float:1.8878587E38)
            java.lang.String r4 = "JoinToGroupErrorNotExist"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
        L_0x00fd:
            r0 = 2131626258(0x7f0e0912, float:1.8879747E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$29$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$26(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$27 */
    public /* synthetic */ void lambda$null$27$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle, TLRPC$ChatInvite tLRPC$ChatInvite) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$28 */
    public /* synthetic */ void lambda$null$28$LaunchActivity(AlertDialog alertDialog) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$32 */
    public /* synthetic */ void lambda$runLinkRequest$32$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$31$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$33 */
    public /* synthetic */ void lambda$runLinkRequest$33$LaunchActivity(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$37 */
    public /* synthetic */ void lambda$runLinkRequest$37$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    LaunchActivity.this.lambda$null$35$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
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
                LaunchActivity.this.lambda$null$36$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$35 */
    public /* synthetic */ void lambda$null$35$LaunchActivity(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$34$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$42(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$36 */
    public /* synthetic */ void lambda$null$36$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$39 */
    public /* synthetic */ void lambda$runLinkRequest$39$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$38$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$41 */
    public /* synthetic */ void lambda$runLinkRequest$41$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$40$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$40 */
    public /* synthetic */ void lambda$null$40$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$44 */
    public /* synthetic */ void lambda$runLinkRequest$44$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$43$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$null$43 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$43$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC$TL_wallPaper r11, org.telegram.tgnet.TLRPC$TL_error r12) {
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
            r8.lambda$runLinkRequest$42(r9)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625212(0x7f0e04fc, float:1.8877626E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$43$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$45 */
    public /* synthetic */ void lambda$runLinkRequest$45$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$47 */
    public /* synthetic */ void lambda$runLinkRequest$47$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$46$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$null$46 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$46$LaunchActivity(org.telegram.tgnet.TLObject r5, org.telegram.ui.ActionBar.AlertDialog r6, org.telegram.tgnet.TLRPC$TL_error r7) {
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
            r5 = 2131627324(0x7f0e0d3c, float:1.888191E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627344(0x7f0e0d50, float:1.888195E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627343(0x7f0e0d4f, float:1.8881948E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$46$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$49 */
    public /* synthetic */ void lambda$runLinkRequest$49$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$48$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* renamed from: lambda$null$48 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$48$LaunchActivity(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
            r0 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$48$LaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$52 */
    public /* synthetic */ void lambda$runLinkRequest$52$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
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
                    LaunchActivity.this.lambda$null$51$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$51 */
    public /* synthetic */ void lambda$null$51$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$50$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$50 */
    public /* synthetic */ void lambda$null$50$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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

    static /* synthetic */ void lambda$runLinkRequest$53(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0075  */
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
            r6 = 0
            if (r19 == 0) goto L_0x005d
            java.lang.String r7 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r19)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r8 = r2.contactsByPhone
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$TL_contact r8 = (org.telegram.tgnet.TLRPC$TL_contact) r8
            if (r8 != 0) goto L_0x0043
            int r8 = r7.length()
            int r8 = r8 + -7
            int r8 = java.lang.Math.max(r6, r8)
            java.lang.String r7 = r7.substring(r8)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r2 = r2.contactsByShortPhone
            java.lang.Object r2 = r2.get(r7)
            r8 = r2
            org.telegram.tgnet.TLRPC$TL_contact r8 = (org.telegram.tgnet.TLRPC$TL_contact) r8
        L_0x0043:
            if (r8 == 0) goto L_0x005d
            int r2 = r8.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r1.getUser(r2)
            if (r2 == 0) goto L_0x005b
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x0057
            if (r20 == 0) goto L_0x005b
        L_0x0057:
            r4.add(r8)
            goto L_0x005d
        L_0x005b:
            r2 = r5
            goto L_0x005f
        L_0x005d:
            r2 = r18
        L_0x005f:
            boolean r7 = r4.isEmpty()
            if (r7 == 0) goto L_0x0163
            if (r2 == 0) goto L_0x0163
            java.lang.String r2 = r2.trim()
            java.lang.String r2 = r2.toLowerCase()
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x0163
            org.telegram.messenger.LocaleController r7 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.String r7 = r7.getTranslitString(r2)
            boolean r8 = r2.equals(r7)
            if (r8 != 0) goto L_0x0089
            int r8 = r7.length()
            if (r8 != 0) goto L_0x008a
        L_0x0089:
            r7 = r5
        L_0x008a:
            r8 = 2
            java.lang.String[] r9 = new java.lang.String[r8]
            r9[r6] = r2
            r2 = 1
            r9[r2] = r7
            int r7 = r3.size()
            r10 = 0
        L_0x0097:
            if (r10 >= r7) goto L_0x0163
            java.lang.Object r11 = r3.get(r10)
            org.telegram.tgnet.TLRPC$TL_contact r11 = (org.telegram.tgnet.TLRPC$TL_contact) r11
            if (r11 == 0) goto L_0x0159
            int r12 = r11.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r12 = r1.getUser(r12)
            if (r12 == 0) goto L_0x0159
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x00b5
            if (r20 != 0) goto L_0x00b5
            goto L_0x0159
        L_0x00b5:
            r13 = 3
            java.lang.String[] r14 = new java.lang.String[r13]
            java.lang.String r15 = r12.first_name
            java.lang.String r13 = r12.last_name
            java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r6] = r13
            org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
            r15 = r14[r6]
            java.lang.String r13 = r13.getTranslitString(r15)
            r14[r2] = r13
            r13 = r14[r6]
            r15 = r14[r2]
            boolean r13 = r13.equals(r15)
            if (r13 == 0) goto L_0x00de
            r14[r2] = r5
        L_0x00de:
            boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r12)
            if (r13 == 0) goto L_0x00f4
            r13 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r15 = "SavedMessages"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
        L_0x0107:
            r13 = 0
            r15 = 0
        L_0x0109:
            if (r13 >= r8) goto L_0x0159
            r2 = r9[r13]
            if (r2 != 0) goto L_0x0110
            goto L_0x0150
        L_0x0110:
            r5 = 3
        L_0x0111:
            if (r6 >= r5) goto L_0x013d
            r5 = r14[r6]
            if (r5 == 0) goto L_0x0136
            boolean r16 = r5.startsWith(r2)
            if (r16 != 0) goto L_0x0134
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = " "
            r8.append(r0)
            r8.append(r2)
            java.lang.String r0 = r8.toString()
            boolean r0 = r5.contains(r0)
            if (r0 == 0) goto L_0x0136
        L_0x0134:
            r15 = 1
            goto L_0x013d
        L_0x0136:
            int r6 = r6 + 1
            r0 = r17
            r5 = 3
            r8 = 2
            goto L_0x0111
        L_0x013d:
            if (r15 != 0) goto L_0x014a
            java.lang.String r0 = r12.username
            if (r0 == 0) goto L_0x014a
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x014a
            r15 = 1
        L_0x014a:
            if (r15 == 0) goto L_0x0150
            r4.add(r11)
            goto L_0x0159
        L_0x0150:
            int r13 = r13 + 1
            r0 = r17
            r2 = 1
            r5 = 0
            r6 = 0
            r8 = 2
            goto L_0x0109
        L_0x0159:
            int r10 = r10 + 1
            r0 = r17
            r2 = 1
            r5 = 0
            r6 = 0
            r8 = 2
            goto L_0x0097
        L_0x0163:
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
                    LaunchActivity.this.lambda$checkAppUpdate$55$LaunchActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkAppUpdate$55 */
    public /* synthetic */ void lambda$checkAppUpdate$55$LaunchActivity(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    LaunchActivity.this.lambda$null$54$LaunchActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$54 */
    public /* synthetic */ void lambda$null$54$LaunchActivity(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
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
                    LaunchActivity.this.lambda$showAlertDialog$56$LaunchActivity(dialogInterface);
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showAlertDialog$56 */
    public /* synthetic */ void lambda$showAlertDialog$56$LaunchActivity(DialogInterface dialogInterface) {
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

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        ChatActivity chatActivity;
        int i;
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList<Long> arrayList2 = arrayList;
        ArrayList<TLRPC$User> arrayList3 = this.contactsToSend;
        int size = arrayList3 != null ? arrayList3.size() + 0 : 0;
        if (this.videoPath != null) {
            size++;
        }
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList4 = this.photoPathsArray;
        if (arrayList4 != null) {
            size += arrayList4.size();
        }
        ArrayList<String> arrayList5 = this.documentsPathsArray;
        if (arrayList5 != null) {
            size += arrayList5.size();
        }
        ArrayList<Uri> arrayList6 = this.documentsUrisArray;
        if (arrayList6 != null) {
            size += arrayList6.size();
        }
        if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
            size++;
        }
        int i2 = 0;
        while (true) {
            boolean z2 = true;
            if (i2 < arrayList.size()) {
                long longValue = arrayList2.get(i2).longValue();
                int i3 = this.currentAccount;
                if (size <= 1) {
                    z2 = false;
                }
                if (!AlertsCreator.checkSlowMode(this, i3, longValue, z2)) {
                    i2++;
                } else {
                    return;
                }
            } else {
                int currentAccount2 = dialogsActivity2 != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
                if (arrayList.size() <= 1) {
                    long longValue2 = arrayList2.get(0).longValue();
                    int i4 = (int) longValue2;
                    int i5 = (int) (longValue2 >> 32);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (!AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance(currentAccount2).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    }
                    if (i4 == 0) {
                        bundle.putInt("enc_id", i5);
                    } else if (i4 > 0) {
                        bundle.putInt("user_id", i4);
                    } else if (i4 < 0) {
                        bundle.putInt("chat_id", -i4);
                    }
                    if (MessagesController.getInstance(currentAccount2).checkCanOpenChat(bundle, dialogsActivity2)) {
                        chatActivity = new ChatActivity(bundle);
                    } else {
                        return;
                    }
                } else {
                    chatActivity = null;
                }
                ArrayList<TLRPC$User> arrayList7 = this.contactsToSend;
                if (arrayList7 == null || arrayList7.size() != 1 || mainFragmentsStack.isEmpty()) {
                    String str = null;
                    for (int i6 = 0; i6 < arrayList.size(); i6++) {
                        long longValue3 = arrayList2.get(i6).longValue();
                        AccountInstance instance = AccountInstance.getInstance(UserConfig.selectedAccount);
                        if (chatActivity != null) {
                            i = 1024;
                            this.actionBarLayout.presentFragment(chatActivity, dialogsActivity2 != null, dialogsActivity2 == null, true, false);
                            String str2 = this.videoPath;
                            if (str2 != null) {
                                chatActivity.openVideoEditor(str2, this.sendingText);
                                this.sendingText = null;
                            }
                        } else {
                            i = 1024;
                            if (this.videoPath != null) {
                                String str3 = this.sendingText;
                                if (str3 != null && str3.length() <= 1024) {
                                    str = this.sendingText;
                                    this.sendingText = null;
                                }
                                ArrayList arrayList8 = new ArrayList();
                                arrayList8.add(this.videoPath);
                                SendMessagesHelper.prepareSendingDocuments(instance, arrayList8, arrayList8, (ArrayList<Uri>) null, str, (String) null, longValue3, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                            }
                        }
                        if (this.photoPathsArray != null) {
                            String str4 = this.sendingText;
                            if (str4 != null && str4.length() <= i && this.photoPathsArray.size() == 1) {
                                this.photoPathsArray.get(0).caption = this.sendingText;
                                this.sendingText = null;
                            }
                            SendMessagesHelper.prepareSendingMedia(instance, this.photoPathsArray, longValue3, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, true, 0);
                        }
                        if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                            String str5 = this.sendingText;
                            if (str5 != null && str5.length() <= i) {
                                ArrayList<String> arrayList9 = this.documentsPathsArray;
                                int size2 = arrayList9 != null ? arrayList9.size() : 0;
                                ArrayList<Uri> arrayList10 = this.documentsUrisArray;
                                if (size2 + (arrayList10 != null ? arrayList10.size() : 0) == 1) {
                                    str = this.sendingText;
                                    this.sendingText = null;
                                }
                            }
                            SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str, this.documentsMimeType, longValue3, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                        }
                        String str6 = this.sendingText;
                        if (str6 != null) {
                            SendMessagesHelper.prepareSendingText(instance, str6, longValue3, true, 0);
                        }
                        ArrayList<TLRPC$User> arrayList11 = this.contactsToSend;
                        if (arrayList11 != null && !arrayList11.isEmpty()) {
                            for (int i7 = 0; i7 < this.contactsToSend.size(); i7++) {
                                SendMessagesHelper.getInstance(currentAccount2).sendMessage(this.contactsToSend.get(i7), longValue3, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                            }
                        }
                        if (!TextUtils.isEmpty(charSequence)) {
                            SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue3, true, 0);
                        }
                    }
                } else {
                    ArrayList<BaseFragment> arrayList12 = mainFragmentsStack;
                    PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList12.get(arrayList12.size() - 1), (ContactsController.Contact) null, (TLRPC$User) null, this.contactsToSendUri, (File) null, (String) null, (String) null);
                    phonebookShareAlert.setDelegate(new ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate(chatActivity, arrayList2, currentAccount2) {
                        public final /* synthetic */ ChatActivity f$1;
                        public final /* synthetic */ ArrayList f$2;
                        public final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i) {
                            LaunchActivity.this.lambda$didSelectDialogs$57$LaunchActivity(this.f$1, this.f$2, this.f$3, tLRPC$User, z, i);
                        }
                    });
                    ArrayList<BaseFragment> arrayList13 = mainFragmentsStack;
                    arrayList13.get(arrayList13.size() - 1).showDialog(phonebookShareAlert);
                }
                if (dialogsActivity2 != null && chatActivity == null) {
                    dialogsActivity.finishFragment();
                }
                this.photoPathsArray = null;
                this.videoPath = null;
                this.sendingText = null;
                this.documentsPathsArray = null;
                this.documentsOriginalPathsArray = null;
                this.contactsToSend = null;
                this.contactsToSendUri = null;
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didSelectDialogs$57 */
    public /* synthetic */ void lambda$didSelectDialogs$57$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
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
    public void lambda$runLinkRequest$42(BaseFragment baseFragment) {
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
                LaunchActivity.this.lambda$showPermissionErrorAlert$58$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPermissionErrorAlert$58 */
    public /* synthetic */ void lambda$showPermissionErrorAlert$58$LaunchActivity(DialogInterface dialogInterface, int i) {
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
                LaunchActivity.lambda$onPause$59(this.f$0);
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

    static /* synthetic */ void lambda$onPause$59(int i) {
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
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
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
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$QJq_PUkNfdrGl2BVipcIAyjxe0.INSTANCE);
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

    static /* synthetic */ void lambda$onResume$60() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r5v0 */
    /* JADX WARNING: type inference failed for: r5v1, types: [int] */
    /* JADX WARNING: type inference failed for: r5v9 */
    /* JADX WARNING: type inference failed for: r5v15 */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0264, code lost:
        if (((org.telegram.ui.ProfileActivity) r2.get(r2.size() - 1)).isSettings() == false) goto L_0x0268;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x04da  */
    /* JADX WARNING: Removed duplicated region for block: B:256:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0253  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            int r3 = android.os.Build.VERSION.SDK_INT
            int r4 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r0 != r4) goto L_0x0011
            r16.switchToAvailableAccountOrLogout()
            goto L_0x061f
        L_0x0011:
            int r4 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r5 = 0
            if (r0 != r4) goto L_0x0022
            r0 = r19[r5]
            if (r0 == r1) goto L_0x061f
            r16.onFinish()
            r16.finish()
            goto L_0x061f
        L_0x0022:
            int r4 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r4) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r3 = r1.currentConnectionState
            if (r3 == r0) goto L_0x061f
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x004a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "switch to state "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x004a:
            r1.currentConnectionState = r0
            r1.updateCurrentConnectionState(r2)
            goto L_0x061f
        L_0x0051:
            int r4 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r4) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r1.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x061f
        L_0x005c:
            int r4 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r7 = "Cancel"
            r8 = 5
            r9 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r10 = "AppName"
            r11 = 4
            r12 = 3
            r13 = 2
            r14 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.String r15 = "OK"
            r6 = 1
            if (r0 != r4) goto L_0x0193
            r0 = r19[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r3 = r0.intValue()
            if (r3 != r12) goto L_0x0080
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.proxyErrorDialog
            if (r3 == 0) goto L_0x0080
            return
        L_0x0080:
            int r3 = r0.intValue()
            if (r3 != r11) goto L_0x008e
            r0 = r19[r6]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r0
            r1.showTosActivity(r2, r0)
            return
        L_0x008e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r1)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r3.setTitle(r4)
            int r4 = r0.intValue()
            if (r4 == r13) goto L_0x00b7
            int r4 = r0.intValue()
            if (r4 == r12) goto L_0x00b7
            r4 = 2131625961(0x7f0e07e9, float:1.8879145E38)
            java.lang.String r5 = "MoreInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$uaYa4aK2H5FozyV_jgcfj_-rE9Y r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$uaYa4aK2H5FozyV_jgcfj_-rE9Y
            r5.<init>(r2)
            r3.setNegativeButton(r4, r5)
        L_0x00b7:
            int r2 = r0.intValue()
            if (r2 != r8) goto L_0x00d3
            r0 = 2131626082(0x7f0e0862, float:1.887939E38)
            java.lang.String r2 = "NobodyLikesSpam3"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2 = 0
            r3.setPositiveButton(r0, r2)
            goto L_0x0175
        L_0x00d3:
            r2 = 0
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x00ef
            r0 = 2131626080(0x7f0e0860, float:1.8879386E38)
            java.lang.String r4 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r3.setPositiveButton(r0, r2)
            goto L_0x0175
        L_0x00ef:
            int r4 = r0.intValue()
            if (r4 != r6) goto L_0x0109
            r0 = 2131626081(0x7f0e0861, float:1.8879388E38)
            java.lang.String r4 = "NobodyLikesSpam2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r3.setPositiveButton(r0, r2)
            goto L_0x0175
        L_0x0109:
            int r2 = r0.intValue()
            if (r2 != r13) goto L_0x0148
            r0 = r19[r6]
            java.lang.String r0 = (java.lang.String) r0
            r3.setMessage(r0)
            r0 = r19[r13]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "AUTH_KEY_DROP_"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x013f
            r0 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r2 = 0
            r3.setPositiveButton(r0, r2)
            r0 = 2131625788(0x7f0e073c, float:1.8878794E38)
            java.lang.String r2 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$tMviUugr1SfKq1m5jYinnxNyUHQ r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$tMviUugr1SfKq1m5jYinnxNyUHQ
            r2.<init>()
            r3.setNegativeButton(r0, r2)
            goto L_0x0175
        L_0x013f:
            r2 = 0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r3.setPositiveButton(r0, r2)
            goto L_0x0175
        L_0x0148:
            int r0 = r0.intValue()
            if (r0 != r12) goto L_0x0175
            r0 = 2131626753(0x7f0e0b01, float:1.8880751E38)
            java.lang.String r2 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setTitle(r0)
            r0 = 2131627487(0x7f0e0ddf, float:1.888224E38)
            java.lang.String r2 = "UseProxyTelegramError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2 = 0
            r3.setPositiveButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r3)
            r1.proxyErrorDialog = r0
            return
        L_0x0175:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x061f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x061f
        L_0x0193:
            int r4 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r0 != r4) goto L_0x01ea
            r0 = r19[r5]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r1)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r3.setTitle(r4)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r5 = 0
            r3.setPositiveButton(r4, r5)
            r4 = 2131627118(0x7f0e0c6e, float:1.8881491E38)
            java.lang.String r5 = "ShareYouLocationUnableManually"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$aEkvtITNmn2BFLE0oC8G_M2N05E r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$aEkvtITNmn2BFLE0oC8G_M2N05E
            r5.<init>(r0, r2)
            r3.setNegativeButton(r4, r5)
            r0 = 2131627117(0x7f0e0c6d, float:1.888149E38)
            java.lang.String r2 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x061f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x061f
        L_0x01ea:
            int r4 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r4) goto L_0x01fd
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x061f
            android.view.View r0 = r0.getChildAt(r5)
            if (r0 == 0) goto L_0x061f
            r0.invalidate()
            goto L_0x061f
        L_0x01fd:
            int r4 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r0 != r4) goto L_0x0233
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r2 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x021e
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x021e
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0218 }
            r0.setFlags(r2, r2)     // Catch:{ Exception -> 0x0218 }
            goto L_0x061f
        L_0x0218:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x061f
        L_0x021e:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x061f
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x022d }
            r0.clearFlags(r2)     // Catch:{ Exception -> 0x022d }
            goto L_0x061f
        L_0x022d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x061f
        L_0x0233:
            int r4 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r0 != r4) goto L_0x026d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r6) goto L_0x0250
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0250
            r0 = 1
            goto L_0x0251
        L_0x0250:
            r0 = 0
        L_0x0251:
            if (r0 == 0) goto L_0x0267
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r6
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ProfileActivity r2 = (org.telegram.ui.ProfileActivity) r2
            boolean r2 = r2.isSettings()
            if (r2 != 0) goto L_0x0267
            goto L_0x0268
        L_0x0267:
            r5 = r0
        L_0x0268:
            r1.rebuildAllFragments(r5)
            goto L_0x061f
        L_0x026d:
            int r4 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r4) goto L_0x0276
            r1.showLanguageAlert(r5)
            goto L_0x061f
        L_0x0276:
            int r4 = org.telegram.messenger.NotificationCenter.openArticle
            if (r0 != r4) goto L_0x02a8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0283
            return
        L_0x0283:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r6
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r0.setParentActivity(r1, r2)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r2 = r19[r5]
            org.telegram.tgnet.TLRPC$TL_webPage r2 = (org.telegram.tgnet.TLRPC$TL_webPage) r2
            r3 = r19[r6]
            java.lang.String r3 = (java.lang.String) r3
            r0.open(r2, r3)
            goto L_0x061f
        L_0x02a8:
            int r4 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r4) goto L_0x0330
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            if (r0 == 0) goto L_0x032f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02b9
            goto L_0x032f
        L_0x02b9:
            r0 = r19[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            r0.intValue()
            r0 = r19[r6]
            java.util.HashMap r0 = (java.util.HashMap) r0
            r3 = r19[r13]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r4 = r19[r12]
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r8.fragmentsStack
            int r9 = r8.size()
            int r9 = r9 - r6
            java.lang.Object r6 = r8.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r6 = (org.telegram.ui.ActionBar.BaseFragment) r6
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r8.<init>((android.content.Context) r1)
            r9 = 2131627455(0x7f0e0dbf, float:1.8882175E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setTitle(r9)
            r9 = 2131627454(0x7f0e0dbe, float:1.8882173E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.-$$Lambda$LaunchActivity$898XCrS9ANyUXSoygip_0DDXlNU r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$898XCrS9ANyUXSoygip_0DDXlNU
            r10.<init>(r2, r0, r3, r4)
            r8.setPositiveButton(r9, r10)
            r9 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$21s7RGBMu1rdDPmDpjfD3x9rS-E r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$21s7RGBMu1rdDPmDpjfD3x9rS-E
            r9.<init>(r2, r0, r3, r4)
            r8.setNegativeButton(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY
            r7.<init>(r2, r0, r3, r4)
            r8.setOnBackButtonListener(r7)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r6.showDialog(r0)
            r0.setCanceledOnTouchOutside(r5)
            goto L_0x061f
        L_0x032f:
            return
        L_0x0330:
            int r2 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r4 = 21
            if (r0 != r2) goto L_0x038f
            r0 = r19[r5]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x037e
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x036a
            java.lang.String r2 = "chats_menuBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r5)
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
        L_0x036a:
            if (r3 < r4) goto L_0x037e
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x037e }
            java.lang.String r2 = "actionBarDefault"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)     // Catch:{ Exception -> 0x037e }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r3
            r3 = 0
            r0.<init>(r3, r3, r2)     // Catch:{ Exception -> 0x037e }
            r1.setTaskDescription(r0)     // Catch:{ Exception -> 0x037e }
        L_0x037e:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBehindKeyboardColor(r2)
            r16.checkSystemBarColors()
            goto L_0x061f
        L_0x038f:
            int r2 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r2) goto L_0x04e6
            if (r3 < r4) goto L_0x04ba
            r0 = r19[r13]
            if (r0 == 0) goto L_0x04ba
            android.widget.ImageView r0 = r1.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03a2
            return
        L_0x03a2:
            r0 = r19[r13]     // Catch:{ all -> 0x04a2 }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x04a2 }
            r2 = r19[r11]     // Catch:{ all -> 0x04a2 }
            java.lang.Boolean r2 = (java.lang.Boolean) r2     // Catch:{ all -> 0x04a2 }
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x04a2 }
            r3 = r19[r8]     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.Components.RLottieImageView r3 = (org.telegram.ui.Components.RLottieImageView) r3     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            int r4 = r4.getMeasuredWidth()     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            int r7 = r7.getMeasuredHeight()     // Catch:{ all -> 0x04a2 }
            if (r2 != 0) goto L_0x03c3
            r3.setVisibility(r11)     // Catch:{ all -> 0x04a2 }
        L_0x03c3:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            int r8 = r8.getMeasuredWidth()     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            int r9 = r9.getMeasuredHeight()     // Catch:{ all -> 0x04a2 }
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x04a2 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r9, r10)     // Catch:{ all -> 0x04a2 }
            android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x04a2 }
            r9.<init>(r8)     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            r10.draw(r9)     // Catch:{ all -> 0x04a2 }
            android.widget.FrameLayout r9 = r1.frameLayout     // Catch:{ all -> 0x04a2 }
            android.widget.ImageView r10 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
            r9.removeView(r10)     // Catch:{ all -> 0x04a2 }
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            if (r2 == 0) goto L_0x03fe
            android.widget.FrameLayout r11 = r1.frameLayout     // Catch:{ all -> 0x04a2 }
            android.widget.ImageView r13 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)     // Catch:{ all -> 0x04a2 }
            r11.addView(r13, r5, r9)     // Catch:{ all -> 0x04a2 }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x04a2 }
            r10 = 8
            r9.setVisibility(r10)     // Catch:{ all -> 0x04a2 }
            goto L_0x042f
        L_0x03fe:
            android.widget.FrameLayout r11 = r1.frameLayout     // Catch:{ all -> 0x04a2 }
            android.widget.ImageView r13 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)     // Catch:{ all -> 0x04a2 }
            r11.addView(r13, r6, r9)     // Catch:{ all -> 0x04a2 }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x04a2 }
            r10 = r0[r5]     // Catch:{ all -> 0x04a2 }
            r11 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x04a2 }
            int r10 = r10 - r13
            float r10 = (float) r10     // Catch:{ all -> 0x04a2 }
            r9.setTranslationX(r10)     // Catch:{ all -> 0x04a2 }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x04a2 }
            r10 = r0[r6]     // Catch:{ all -> 0x04a2 }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x04a2 }
            int r10 = r10 - r11
            float r10 = (float) r10     // Catch:{ all -> 0x04a2 }
            r9.setTranslationY(r10)     // Catch:{ all -> 0x04a2 }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x04a2 }
            r9.setVisibility(r5)     // Catch:{ all -> 0x04a2 }
            android.view.View r9 = r1.themeSwitchSunView     // Catch:{ all -> 0x04a2 }
            r9.invalidate()     // Catch:{ all -> 0x04a2 }
        L_0x042f:
            android.widget.ImageView r9 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
            r9.setImageBitmap(r8)     // Catch:{ all -> 0x04a2 }
            android.widget.ImageView r8 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
            r8.setVisibility(r5)     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.Components.RLottieDrawable r8 = r3.getAnimatedDrawable()     // Catch:{ all -> 0x04a2 }
            r1.themeSwitchSunDrawable = r8     // Catch:{ all -> 0x04a2 }
            r8 = r0[r5]     // Catch:{ all -> 0x04a2 }
            int r8 = r4 - r8
            r9 = r0[r5]     // Catch:{ all -> 0x04a2 }
            int r4 = r4 - r9
            int r8 = r8 * r4
            r4 = r0[r6]     // Catch:{ all -> 0x04a2 }
            int r4 = r7 - r4
            r9 = r0[r6]     // Catch:{ all -> 0x04a2 }
            int r9 = r7 - r9
            int r4 = r4 * r9
            int r8 = r8 + r4
            double r8 = (double) r8     // Catch:{ all -> 0x04a2 }
            double r8 = java.lang.Math.sqrt(r8)     // Catch:{ all -> 0x04a2 }
            r4 = r0[r5]     // Catch:{ all -> 0x04a2 }
            r10 = r0[r5]     // Catch:{ all -> 0x04a2 }
            int r4 = r4 * r10
            r10 = r0[r6]     // Catch:{ all -> 0x04a2 }
            int r10 = r7 - r10
            r11 = r0[r6]     // Catch:{ all -> 0x04a2 }
            int r7 = r7 - r11
            int r10 = r10 * r7
            int r4 = r4 + r10
            double r10 = (double) r4     // Catch:{ all -> 0x04a2 }
            double r10 = java.lang.Math.sqrt(r10)     // Catch:{ all -> 0x04a2 }
            double r7 = java.lang.Math.max(r8, r10)     // Catch:{ all -> 0x04a2 }
            float r4 = (float) r7     // Catch:{ all -> 0x04a2 }
            if (r2 == 0) goto L_0x0477
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04a2 }
            goto L_0x0479
        L_0x0477:
            android.widget.ImageView r7 = r1.themeSwitchImageView     // Catch:{ all -> 0x04a2 }
        L_0x0479:
            r8 = r0[r5]     // Catch:{ all -> 0x04a2 }
            r0 = r0[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            if (r2 == 0) goto L_0x0482
            r10 = 0
            goto L_0x0483
        L_0x0482:
            r10 = r4
        L_0x0483:
            if (r2 == 0) goto L_0x0486
            goto L_0x0487
        L_0x0486:
            r4 = 0
        L_0x0487:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r7, r8, r0, r10, r4)     // Catch:{ all -> 0x04a2 }
            r7 = 400(0x190, double:1.976E-321)
            r0.setDuration(r7)     // Catch:{ all -> 0x04a2 }
            android.view.animation.Interpolator r4 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x04a2 }
            r0.setInterpolator(r4)     // Catch:{ all -> 0x04a2 }
            org.telegram.ui.LaunchActivity$9 r4 = new org.telegram.ui.LaunchActivity$9     // Catch:{ all -> 0x04a2 }
            r4.<init>(r2, r3)     // Catch:{ all -> 0x04a2 }
            r0.addListener(r4)     // Catch:{ all -> 0x04a2 }
            r0.start()     // Catch:{ all -> 0x04a2 }
            r0 = 1
            goto L_0x04bb
        L_0x04a2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04b6 }
            r2 = 0
            r0.setImageDrawable(r2)     // Catch:{ Exception -> 0x04b6 }
            android.widget.FrameLayout r0 = r1.frameLayout     // Catch:{ Exception -> 0x04b6 }
            android.widget.ImageView r2 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04b6 }
            r0.removeView(r2)     // Catch:{ Exception -> 0x04b6 }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r5     // Catch:{ Exception -> 0x04b6 }
            goto L_0x04ba
        L_0x04b6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04ba:
            r0 = 0
        L_0x04bb:
            r2 = r19[r5]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r2
            r3 = r19[r6]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r4 = r19[r12]
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.actionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x061f
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.layersActionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.rightActionBarLayout
            r5.animateThemedValues(r2, r4, r3, r0)
            goto L_0x061f
        L_0x04e6:
            int r2 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r2) goto L_0x0517
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x061f
            r2 = r19[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r0 = r0.getChildCount()
        L_0x04f6:
            if (r5 >= r0) goto L_0x061f
            org.telegram.ui.Components.RecyclerListView r3 = r1.sideMenu
            android.view.View r3 = r3.getChildAt(r5)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r4 == 0) goto L_0x0514
            r4 = r3
            org.telegram.ui.Cells.DrawerUserCell r4 = (org.telegram.ui.Cells.DrawerUserCell) r4
            int r4 = r4.getAccountNumber()
            int r6 = r2.intValue()
            if (r4 != r6) goto L_0x0514
            r3.invalidate()
            goto L_0x061f
        L_0x0514:
            int r5 = r5 + 1
            goto L_0x04f6
        L_0x0517:
            int r2 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r2) goto L_0x0526
            r0 = r19[r5]     // Catch:{ all -> 0x061f }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x061f }
            r2 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r1, r2)     // Catch:{ all -> 0x061f }
            goto L_0x061f
        L_0x0526:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r0 != r2) goto L_0x05e7
            java.lang.String r0 = r1.loadingThemeFileName
            if (r0 == 0) goto L_0x05b4
            r2 = r19[r5]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x061f
            r2 = 0
            r1.loadingThemeFileName = r2
            java.io.File r0 = new java.io.File
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "remote"
            r3.append(r4)
            org.telegram.tgnet.TLRPC$TL_theme r4 = r1.loadingTheme
            long r4 = r4.id
            r3.append(r4)
            java.lang.String r4 = ".attheme"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r2, r3)
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r0, r3, r2)
            if (r2 == 0) goto L_0x05af
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x0598
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x0598
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r2.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r2.account
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$C2DXMrlNLXXUIix2oOuW_mFp8BQ r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$C2DXMrlNLXXUIix2oOuW_mFp8BQ
            r4.<init>(r2)
            r3.sendRequest(r0, r4)
            return
        L_0x0598:
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r3, r2, r6)
            if (r8 == 0) goto L_0x05af
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r9 = 1
            r10 = 0
            r11 = 0
            r12 = 0
            r7 = r0
            r7.<init>(r8, r9, r10, r11, r12)
            r1.lambda$runLinkRequest$42(r0)
        L_0x05af:
            r16.onThemeLoadFinish()
            goto L_0x061f
        L_0x05b4:
            java.lang.String r0 = r1.loadingThemeWallpaperName
            if (r0 == 0) goto L_0x061f
            r2 = r19[r5]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x061f
            r2 = 0
            r1.loadingThemeWallpaperName = r2
            r0 = r19[r6]
            java.io.File r0 = (java.io.File) r0
            boolean r2 = r1.loadingThemeAccent
            if (r2 == 0) goto L_0x05da
            org.telegram.tgnet.TLRPC$TL_theme r0 = r1.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r1.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.loadingThemeInfo
            r1.openThemeAccentPreview(r0, r2, r3)
            r16.onThemeLoadFinish()
            goto L_0x061f
        L_0x05da:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$bimMCIfPid_duj1QWLc7OytSkuQ r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$bimMCIfPid_duj1QWLc7OytSkuQ
            r4.<init>(r2, r0)
            r3.postRunnable(r4)
            goto L_0x061f
        L_0x05e7:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r0 != r2) goto L_0x0603
            r0 = r19[r5]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = r1.loadingThemeFileName
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x05ff
            java.lang.String r2 = r1.loadingThemeWallpaperName
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x061f
        L_0x05ff:
            r16.onThemeLoadFinish()
            goto L_0x061f
        L_0x0603:
            int r2 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r2) goto L_0x0618
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x060c
            return
        L_0x060c:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x0614
            r16.onPasscodeResume()
            goto L_0x061f
        L_0x0614:
            r16.onPasscodePause()
            goto L_0x061f
        L_0x0618:
            int r2 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r2) goto L_0x061f
            r16.checkSystemBarColors()
        L_0x061f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$61(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$62 */
    public /* synthetic */ void lambda$didReceivedNotification$62$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$64 */
    public /* synthetic */ void lambda$didReceivedNotification$64$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
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
                        LaunchActivity.lambda$null$63(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$42(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$63(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$69 */
    public /* synthetic */ void lambda$didReceivedNotification$69$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$68$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$68 */
    public /* synthetic */ void lambda$null$68$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$71 */
    public /* synthetic */ void lambda$didReceivedNotification$71$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$70$LaunchActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$70 */
    public /* synthetic */ void lambda$null$70$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$42(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$42(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
                    LaunchActivity.this.lambda$checkFreeDiscSpace$73$LaunchActivity();
                }
            }, 2000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkFreeDiscSpace$73 */
    public /* synthetic */ void lambda$checkFreeDiscSpace$73$LaunchActivity() {
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
                                LaunchActivity.this.lambda$null$72$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$72 */
    public /* synthetic */ void lambda$null$72$LaunchActivity() {
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
            r9 = 2131624816(0x7f0e0370, float:1.8876822E38)
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
            r14 = 2131625200(0x7f0e04f0, float:1.8877601E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$ec5LVwGhRH0PBJn3cBsqXtQ-H8Y r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$ec5LVwGhRH0PBJn3cBsqXtQ-H8Y     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624817(0x7f0e0371, float:1.8876824E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$cEE7w90UBSKIdauginT81U4ZH9A r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$cEE7w90UBSKIdauginT81U4ZH9A     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$FX0QO3yZPkB9KH6V1pGgA8mHBmU r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$FX0QO3yZPkB9KH6V1pGgA8mHBmU     // Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$74(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlertInternal$75 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$75$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$42(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlertInternal$76 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$76$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                    LaunchActivity.this.lambda$showLanguageAlert$78$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                                    LaunchActivity.this.lambda$showLanguageAlert$80$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$78 */
    public /* synthetic */ void lambda$showLanguageAlert$78$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$77$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$77 */
    public /* synthetic */ void lambda$null$77$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$80 */
    public /* synthetic */ void lambda$showLanguageAlert$80$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$79$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$79 */
    public /* synthetic */ void lambda$null$79$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            $$Lambda$LaunchActivity$lHheWqRj6t5G4U9ID0aF5pNuxlM r4 = null;
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
            if (connectionState == 1 || connectionState == 4) {
                r4 = new Runnable() {
                    public final void run() {
                        LaunchActivity.this.lambda$updateCurrentConnectionState$81$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$updateCurrentConnectionState$81 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$81$LaunchActivity() {
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
            r2.lambda$runLinkRequest$42(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$81$LaunchActivity():void");
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
                    ArrayList<BaseFragment> arrayList = this.layersActionBarLayout.fragmentsStack;
                    baseFragment = arrayList.get(arrayList.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    ArrayList<BaseFragment> arrayList2 = this.rightActionBarLayout.fragmentsStack;
                    baseFragment = arrayList2.get(arrayList2.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    ArrayList<BaseFragment> arrayList3 = this.actionBarLayout.fragmentsStack;
                    baseFragment = arrayList3.get(arrayList3.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList4 = this.actionBarLayout.fragmentsStack;
                baseFragment = arrayList4.get(arrayList4.size() - 1);
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
            } else {
                boolean z5 = this.tabletFullSize;
                if ((!z5 && actionBarLayout2 == this.rightActionBarLayout) || (z5 && actionBarLayout2 == this.actionBarLayout)) {
                    boolean z6 = (z5 && actionBarLayout2 == (actionBarLayout3 = this.actionBarLayout) && actionBarLayout3.fragmentsStack.size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            ActionBarLayout actionBarLayout8 = this.layersActionBarLayout;
                            actionBarLayout8.removeFragmentFromStack(actionBarLayout8.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!z2);
                    }
                    if (!z6) {
                        this.actionBarLayout.presentFragment(baseFragment, false, z2, false, false);
                    }
                    return z6;
                } else if (!z5 && actionBarLayout2 != (actionBarLayout5 = this.rightActionBarLayout)) {
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
                } else if (!z5 || actionBarLayout2 == (actionBarLayout4 = this.actionBarLayout)) {
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
            } else {
                boolean z3 = this.tabletFullSize;
                if (!z3 && actionBarLayout2 != (actionBarLayout4 = this.rightActionBarLayout)) {
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
                } else if (z3 && actionBarLayout2 != (actionBarLayout3 = this.actionBarLayout)) {
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
