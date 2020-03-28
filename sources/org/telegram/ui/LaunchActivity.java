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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
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
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PhonebookSelectShareAlert;
import org.telegram.ui.Components.PhonebookShareAlert;
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
            r6 = 0
            java.lang.String r8 = "intro_crashed_time"
            long r9 = r5.getLong(r8, r6)
            java.lang.String r11 = "fromIntro"
            boolean r11 = r1.getBooleanExtra(r11, r3)
            if (r11 == 0) goto L_0x00a8
            android.content.SharedPreferences$Editor r5 = r5.edit()
            android.content.SharedPreferences$Editor r5 = r5.putLong(r8, r6)
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
            if (r1 == 0) goto L_0x0297
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
            r6 = 2131165317(0x7var_, float:1.7944848E38)
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
            if (r6 == 0) goto L_0x0230
            r6 = 8
            goto L_0x0231
        L_0x0230:
            r6 = 0
        L_0x0231:
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
            r6 = 2131165302(0x7var_, float:1.7944817E38)
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
            if (r6 == 0) goto L_0x028d
            goto L_0x028e
        L_0x028d:
            r8 = 0
        L_0x028e:
            r4.setVisibility(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            r1.addView(r4)
            goto L_0x02a3
        L_0x0297:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            android.view.ViewGroup$LayoutParams r6 = new android.view.ViewGroup$LayoutParams
            r6.<init>(r7, r7)
            r1.addView(r4, r6)
        L_0x02a3:
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
            if (r6 == 0) goto L_0x02fe
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0315
        L_0x02fe:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r4.x
            int r4 = r4.y
            int r4 = java.lang.Math.min(r8, r4)
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            int r4 = java.lang.Math.min(r6, r4)
        L_0x0315:
            r1.width = r4
            r1.height = r7
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r4.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0
            r4.<init>()
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
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
            if (r1 == 0) goto L_0x04eb
            int r1 = r12.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x03ff
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.LoginActivity r4 = new org.telegram.ui.LoginActivity
            r4.<init>()
            r1.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x0413
        L_0x03ff:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r5)
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            r4.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r3)
        L_0x0413:
            if (r13 == 0) goto L_0x0552
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r13.getString(r1)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x0552
            java.lang.String r4 = "args"
            android.os.Bundle r4 = r13.getBundle(r4)     // Catch:{ Exception -> 0x04e6 }
            int r5 = r1.hashCode()     // Catch:{ Exception -> 0x04e6 }
            r6 = 5
            r8 = 4
            r9 = 3
            r10 = 2
            switch(r5) {
                case -1529105743: goto L_0x0461;
                case -1349522494: goto L_0x0457;
                case 3052376: goto L_0x044d;
                case 98629247: goto L_0x0443;
                case 738950403: goto L_0x0439;
                case 1434631203: goto L_0x042f;
                default: goto L_0x042e;
            }     // Catch:{ Exception -> 0x04e6 }
        L_0x042e:
            goto L_0x046b
        L_0x042f:
            java.lang.String r5 = "settings"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 1
            goto L_0x046b
        L_0x0439:
            java.lang.String r5 = "channel"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 3
            goto L_0x046b
        L_0x0443:
            java.lang.String r5 = "group"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 2
            goto L_0x046b
        L_0x044d:
            java.lang.String r5 = "chat"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 0
            goto L_0x046b
        L_0x0457:
            java.lang.String r5 = "chat_profile"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 4
            goto L_0x046b
        L_0x0461:
            java.lang.String r5 = "wallpapers"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x04e6 }
            if (r1 == 0) goto L_0x046b
            r7 = 5
        L_0x046b:
            if (r7 == 0) goto L_0x04d3
            if (r7 == r2) goto L_0x04c4
            if (r7 == r10) goto L_0x04b0
            if (r7 == r9) goto L_0x049c
            if (r7 == r8) goto L_0x0488
            if (r7 == r6) goto L_0x0479
            goto L_0x0552
        L_0x0479:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x0488:
            if (r4 == 0) goto L_0x0552
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            if (r4 == 0) goto L_0x0552
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x049c:
            if (r4 == 0) goto L_0x0552
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            if (r4 == 0) goto L_0x0552
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x04b0:
            if (r4 == 0) goto L_0x0552
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            if (r4 == 0) goto L_0x0552
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x04c4:
            org.telegram.ui.SettingsActivity r1 = new org.telegram.ui.SettingsActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>()     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x04d3:
            if (r4 == 0) goto L_0x0552
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04e6 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04e6 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout     // Catch:{ Exception -> 0x04e6 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04e6 }
            if (r4 == 0) goto L_0x0552
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04e6 }
            goto L_0x0552
        L_0x04e6:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0552
        L_0x04eb:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r4 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r4 == 0) goto L_0x0500
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r4 = r12.sideMenu
            r1.setSideMenu(r4)
        L_0x0500:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0535
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x051c
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x051c
            r1 = 1
            goto L_0x051d
        L_0x051c:
            r1 = 0
        L_0x051d:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0536
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0536
            r1 = 0
            goto L_0x0536
        L_0x0535:
            r1 = 1
        L_0x0536:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x054d
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x054d
            r1 = 0
        L_0x054d:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer
            r4.setAllowOpenDrawer(r1, r3)
        L_0x0552:
            r12.checkLayout()
            r12.checkSystemBarColors()
            android.content.Intent r1 = r12.getIntent()
            if (r13 == 0) goto L_0x0560
            r13 = 1
            goto L_0x0561
        L_0x0560:
            r13 = 0
        L_0x0561:
            r12.handleIntent(r1, r3, r13, r3)
            java.lang.String r13 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05c1 }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x05c1 }
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x0571
            java.lang.String r13 = r13.toLowerCase()     // Catch:{ Exception -> 0x05c1 }
            goto L_0x0572
        L_0x0571:
            r13 = r3
        L_0x0572:
            if (r1 == 0) goto L_0x0578
            java.lang.String r3 = r13.toLowerCase()     // Catch:{ Exception -> 0x05c1 }
        L_0x0578:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05c1 }
            if (r1 == 0) goto L_0x0598
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05c1 }
            r1.<init>()     // Catch:{ Exception -> 0x05c1 }
            java.lang.String r4 = "OS name "
            r1.append(r4)     // Catch:{ Exception -> 0x05c1 }
            r1.append(r13)     // Catch:{ Exception -> 0x05c1 }
            java.lang.String r4 = " "
            r1.append(r4)     // Catch:{ Exception -> 0x05c1 }
            r1.append(r3)     // Catch:{ Exception -> 0x05c1 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05c1 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05c1 }
        L_0x0598:
            boolean r13 = r13.contains(r0)     // Catch:{ Exception -> 0x05c1 }
            if (r13 != 0) goto L_0x05a4
            boolean r13 = r3.contains(r0)     // Catch:{ Exception -> 0x05c1 }
            if (r13 == 0) goto L_0x05c5
        L_0x05a4:
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x05c1 }
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x05c1 }
            android.view.View r13 = r13.getDecorView()     // Catch:{ Exception -> 0x05c1 }
            android.view.View r13 = r13.getRootView()     // Catch:{ Exception -> 0x05c1 }
            android.view.ViewTreeObserver r0 = r13.getViewTreeObserver()     // Catch:{ Exception -> 0x05c1 }
            org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0 r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0     // Catch:{ Exception -> 0x05c1 }
            r1.<init>(r13)     // Catch:{ Exception -> 0x05c1 }
            r12.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x05c1 }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x05c1 }
            goto L_0x05c5
        L_0x05c1:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x05c5:
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

    public /* synthetic */ void lambda$onCreate$2$LaunchActivity(View view, int i) {
        if (i == 0) {
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
                lambda$runLinkRequest$30$LaunchActivity(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$30$LaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$30$LaunchActivity(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$30$LaunchActivity(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$30$LaunchActivity(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$30$LaunchActivity(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$30$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                lambda$runLinkRequest$30$LaunchActivity(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$30$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$30$LaunchActivity(new ChatActivity(bundle3));
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogFiltersUpdated);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogFiltersUpdated);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v95, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v211, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v213, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v102, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v103, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v105, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v108, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v109, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v12, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v251, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v256, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v103, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v276, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v300, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v301, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v302, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v303, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v310, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v321, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v333, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v338, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v341, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v342, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v343, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v346, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v347, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v260, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v261, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v15, resolved type: java.util.HashMap} */
    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r4v25 */
    /* JADX WARNING: type inference failed for: r3v12 */
    /* JADX WARNING: type inference failed for: r18v1, types: [org.telegram.tgnet.TLRPC$TL_wallPaper] */
    /* JADX WARNING: type inference failed for: r13v49 */
    /* JADX WARNING: type inference failed for: r13v50 */
    /* JADX WARNING: type inference failed for: r13v51 */
    /* JADX WARNING: type inference failed for: r13v52 */
    /* JADX WARNING: type inference failed for: r13v53 */
    /* JADX WARNING: type inference failed for: r28v8 */
    /* JADX WARNING: type inference failed for: r13v54 */
    /* JADX WARNING: type inference failed for: r13v55 */
    /* JADX WARNING: type inference failed for: r28v9 */
    /* JADX WARNING: type inference failed for: r13v56 */
    /* JADX WARNING: type inference failed for: r13v57 */
    /* JADX WARNING: type inference failed for: r13v58 */
    /* JADX WARNING: type inference failed for: r13v59 */
    /* JADX WARNING: type inference failed for: r13v60 */
    /* JADX WARNING: type inference failed for: r13v61 */
    /* JADX WARNING: type inference failed for: r13v62 */
    /* JADX WARNING: type inference failed for: r29v10 */
    /* JADX WARNING: type inference failed for: r13v63 */
    /* JADX WARNING: type inference failed for: r13v64 */
    /* JADX WARNING: type inference failed for: r13v65 */
    /* JADX WARNING: type inference failed for: r13v66 */
    /* JADX WARNING: type inference failed for: r29v13 */
    /* JADX WARNING: type inference failed for: r34v6 */
    /* JADX WARNING: type inference failed for: r13v67 */
    /* JADX WARNING: type inference failed for: r28v15 */
    /* JADX WARNING: type inference failed for: r13v68 */
    /* JADX WARNING: type inference failed for: r28v16 */
    /* JADX WARNING: type inference failed for: r13v69 */
    /* JADX WARNING: type inference failed for: r28v17 */
    /* JADX WARNING: type inference failed for: r13v70 */
    /* JADX WARNING: type inference failed for: r4v255 */
    /* JADX WARNING: type inference failed for: r3v56 */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x022a, code lost:
        if (r15.sendingText == null) goto L_0x0125;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0d41, code lost:
        if (r4 == 0) goto L_0x0d43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0ebd, code lost:
        r11[0] = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:?, code lost:
        switchToAccount(r11[0], true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x10b5, code lost:
        if (r4.checkCanOpenChat(r0, r5.get(r5.size() - 1)) != false) goto L_0x10b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x10c9, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x10cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x10cd, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x1102, code lost:
        if (r0.checkCanOpenChat(r5, r4.get(r4.size() - 1)) != false) goto L_0x1104;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x1114, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r5), false, true, true, false) != false) goto L_0x10cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x12f0, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x12f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x130f, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x12f2;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r3v2, types: [int, boolean] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:625:0x0ef7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0231  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0399 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x065b A[SYNTHETIC, Splitter:B:300:0x065b] */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a67  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0de5  */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x0e58  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0e5f  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0efc A[Catch:{ all -> 0x0ef0, all -> 0x0ef7, Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x1082  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x1333  */
    /* JADX WARNING: Removed duplicated region for block: B:805:0x136d  */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x1379  */
    /* JADX WARNING: Removed duplicated region for block: B:808:0x1391  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x13a0  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x13e5  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x142a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r46, boolean r47, boolean r48, boolean r49) {
        /*
            r45 = this;
            r15 = r45
            r14 = r46
            r0 = r48
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r45, r46)
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
            java.lang.String r1 = r46.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r46.getFlags()
            int[] r11 = new int[r13]
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r3 = "currentAccount"
            int r2 = r14.getIntExtra(r3, r2)
            r11[r12] = r2
            r2 = r11[r12]
            r15.switchToAccount(r2, r13)
            if (r49 != 0) goto L_0x007f
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r2 != 0) goto L_0x006a
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x007f
        L_0x006a:
            r45.showPasscodeActivity()
            r15.passcodeSaveIntent = r14
            r10 = r47
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r12)
            return r12
        L_0x007f:
            r10 = r47
            boolean r2 = org.telegram.messenger.SharedConfig.directShare
            java.lang.String r3 = "hash"
            r8 = 0
            if (r2 == 0) goto L_0x00ad
            if (r14 == 0) goto L_0x00ad
            android.os.Bundle r2 = r46.getExtras()
            if (r2 == 0) goto L_0x00ad
            android.os.Bundle r2 = r46.getExtras()
            java.lang.String r4 = "dialogId"
            long r4 = r2.getLong(r4, r8)
            android.os.Bundle r2 = r46.getExtras()
            long r6 = r2.getLong(r3, r8)
            long r16 = org.telegram.messenger.SharedConfig.directShareHash
            int r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x00aa
            goto L_0x00ad
        L_0x00aa:
            r21 = r4
            goto L_0x00af
        L_0x00ad:
            r21 = r8
        L_0x00af:
            r7 = 0
            r15.photoPathsArray = r7
            r15.videoPath = r7
            r15.sendingText = r7
            r15.documentsPathsArray = r7
            r15.documentsOriginalPathsArray = r7
            r15.documentsMimeType = r7
            r15.documentsUrisArray = r7
            r15.contactsToSend = r7
            r15.contactsToSendUri = r7
            r2 = 1048576(0x100000, float:1.469368E-39)
            r1 = r1 & r2
            r5 = 2
            if (r1 != 0) goto L_0x1066
            if (r14 == 0) goto L_0x1066
            java.lang.String r1 = r46.getAction()
            if (r1 == 0) goto L_0x1066
            if (r0 != 0) goto L_0x1066
            java.lang.String r0 = r46.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r2 = ""
            if (r0 == 0) goto L_0x023c
            java.lang.String r0 = r46.getType()
            if (r0 == 0) goto L_0x0128
            java.lang.String r3 = "text/x-vcard"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0128
            android.os.Bundle r1 = r46.getExtras()     // Catch:{ Exception -> 0x0121 }
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0121 }
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x0121 }
            if (r1 == 0) goto L_0x0125
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0121 }
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r7, r7)     // Catch:{ Exception -> 0x0121 }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0121 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0121 }
            r3 = 5
            if (r2 <= r3) goto L_0x011d
            r15.contactsToSend = r7     // Catch:{ Exception -> 0x0121 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0121 }
            r2.<init>()     // Catch:{ Exception -> 0x0121 }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x0121 }
            r2.add(r1)     // Catch:{ Exception -> 0x0121 }
            r15.documentsMimeType = r0     // Catch:{ Exception -> 0x0121 }
            goto L_0x022e
        L_0x011d:
            r15.contactsToSendUri = r1     // Catch:{ Exception -> 0x0121 }
            goto L_0x022e
        L_0x0121:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0125:
            r0 = 1
            goto L_0x022f
        L_0x0128:
            java.lang.String r3 = "android.intent.extra.TEXT"
            java.lang.String r3 = r14.getStringExtra(r3)
            if (r3 != 0) goto L_0x013c
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x013c
            java.lang.String r3 = r4.toString()
        L_0x013c:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r16 = android.text.TextUtils.isEmpty(r3)
            if (r16 != 0) goto L_0x0173
            java.lang.String r8 = "http://"
            boolean r8 = r3.startsWith(r8)
            if (r8 != 0) goto L_0x0158
            java.lang.String r8 = "https://"
            boolean r8 = r3.startsWith(r8)
            if (r8 == 0) goto L_0x0170
        L_0x0158:
            boolean r8 = android.text.TextUtils.isEmpty(r4)
            if (r8 != 0) goto L_0x0170
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            r8.append(r1)
            r8.append(r3)
            java.lang.String r3 = r8.toString()
        L_0x0170:
            r15.sendingText = r3
            goto L_0x017b
        L_0x0173:
            boolean r1 = android.text.TextUtils.isEmpty(r4)
            if (r1 != 0) goto L_0x017b
            r15.sendingText = r4
        L_0x017b:
            java.lang.String r1 = "android.intent.extra.STREAM"
            android.os.Parcelable r1 = r14.getParcelableExtra(r1)
            if (r1 == 0) goto L_0x0228
            boolean r3 = r1 instanceof android.net.Uri
            if (r3 != 0) goto L_0x018f
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
        L_0x018f:
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x019b
            boolean r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1)
            if (r3 == 0) goto L_0x019b
            r3 = 1
            goto L_0x019c
        L_0x019b:
            r3 = 0
        L_0x019c:
            if (r3 != 0) goto L_0x0226
            if (r1 == 0) goto L_0x01d2
            if (r0 == 0) goto L_0x01aa
            java.lang.String r4 = "image/"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x01ba
        L_0x01aa:
            java.lang.String r4 = r1.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r8 = ".jpg"
            boolean r4 = r4.endsWith(r8)
            if (r4 == 0) goto L_0x01d2
        L_0x01ba:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x01c5
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x01c5:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0226
        L_0x01d2:
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r4 == 0) goto L_0x0214
            java.lang.String r8 = "file:"
            boolean r8 = r4.startsWith(r8)
            if (r8 == 0) goto L_0x01e6
            java.lang.String r8 = "file://"
            java.lang.String r4 = r4.replace(r8, r2)
        L_0x01e6:
            if (r0 == 0) goto L_0x01f3
            java.lang.String r2 = "video/"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x01f3
            r15.videoPath = r4
            goto L_0x0226
        L_0x01f3:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            if (r0 != 0) goto L_0x0205
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsPathsArray = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsOriginalPathsArray = r0
        L_0x0205:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            r0.add(r4)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x0226
        L_0x0214:
            java.util.ArrayList<android.net.Uri> r2 = r15.documentsUrisArray
            if (r2 != 0) goto L_0x021f
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsUrisArray = r2
        L_0x021f:
            java.util.ArrayList<android.net.Uri> r2 = r15.documentsUrisArray
            r2.add(r1)
            r15.documentsMimeType = r0
        L_0x0226:
            r0 = r3
            goto L_0x022f
        L_0x0228:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x022e
            goto L_0x0125
        L_0x022e:
            r0 = 0
        L_0x022f:
            if (r0 == 0) goto L_0x0341
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
            goto L_0x0341
        L_0x023c:
            java.lang.String r0 = r46.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0349
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x0331 }
            java.lang.String r1 = r46.getType()     // Catch:{ Exception -> 0x0331 }
            if (r0 == 0) goto L_0x0285
            r3 = 0
        L_0x0255:
            int r4 = r0.size()     // Catch:{ Exception -> 0x0331 }
            if (r3 >= r4) goto L_0x027e
            java.lang.Object r4 = r0.get(r3)     // Catch:{ Exception -> 0x0331 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x0331 }
            boolean r8 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x0331 }
            if (r8 != 0) goto L_0x026d
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0331 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0331 }
        L_0x026d:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x0331 }
            if (r4 == 0) goto L_0x027c
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r4)     // Catch:{ Exception -> 0x0331 }
            if (r4 == 0) goto L_0x027c
            r0.remove(r3)     // Catch:{ Exception -> 0x0331 }
            int r3 = r3 + -1
        L_0x027c:
            int r3 = r3 + r13
            goto L_0x0255
        L_0x027e:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x0331 }
            if (r3 == 0) goto L_0x0285
            r0 = r7
        L_0x0285:
            if (r0 == 0) goto L_0x0335
            if (r1 == 0) goto L_0x02c6
            java.lang.String r3 = "image/"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x0331 }
            if (r3 == 0) goto L_0x02c6
            r1 = 0
        L_0x0292:
            int r2 = r0.size()     // Catch:{ Exception -> 0x0331 }
            if (r1 >= r2) goto L_0x032f
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x0331 }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x0331 }
            boolean r3 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x0331 }
            if (r3 != 0) goto L_0x02aa
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0331 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0331 }
        L_0x02aa:
            android.net.Uri r2 = (android.net.Uri) r2     // Catch:{ Exception -> 0x0331 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x0331 }
            if (r3 != 0) goto L_0x02b7
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0331 }
            r3.<init>()     // Catch:{ Exception -> 0x0331 }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x0331 }
        L_0x02b7:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x0331 }
            r3.<init>()     // Catch:{ Exception -> 0x0331 }
            r3.uri = r2     // Catch:{ Exception -> 0x0331 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray     // Catch:{ Exception -> 0x0331 }
            r2.add(r3)     // Catch:{ Exception -> 0x0331 }
            int r1 = r1 + 1
            goto L_0x0292
        L_0x02c6:
            r3 = 0
        L_0x02c7:
            int r4 = r0.size()     // Catch:{ Exception -> 0x0331 }
            if (r3 >= r4) goto L_0x032f
            java.lang.Object r4 = r0.get(r3)     // Catch:{ Exception -> 0x0331 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x0331 }
            boolean r8 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x0331 }
            if (r8 != 0) goto L_0x02df
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0331 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0331 }
        L_0x02df:
            r8 = r4
            android.net.Uri r8 = (android.net.Uri) r8     // Catch:{ Exception -> 0x0331 }
            java.lang.String r9 = org.telegram.messenger.AndroidUtilities.getPath(r8)     // Catch:{ Exception -> 0x0331 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0331 }
            if (r4 != 0) goto L_0x02ed
            r4 = r9
        L_0x02ed:
            if (r9 == 0) goto L_0x031a
            java.lang.String r8 = "file:"
            boolean r8 = r9.startsWith(r8)     // Catch:{ Exception -> 0x0331 }
            if (r8 == 0) goto L_0x02fd
            java.lang.String r8 = "file://"
            java.lang.String r9 = r9.replace(r8, r2)     // Catch:{ Exception -> 0x0331 }
        L_0x02fd:
            java.util.ArrayList<java.lang.String> r8 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0331 }
            if (r8 != 0) goto L_0x030f
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0331 }
            r8.<init>()     // Catch:{ Exception -> 0x0331 }
            r15.documentsPathsArray = r8     // Catch:{ Exception -> 0x0331 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0331 }
            r8.<init>()     // Catch:{ Exception -> 0x0331 }
            r15.documentsOriginalPathsArray = r8     // Catch:{ Exception -> 0x0331 }
        L_0x030f:
            java.util.ArrayList<java.lang.String> r8 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0331 }
            r8.add(r9)     // Catch:{ Exception -> 0x0331 }
            java.util.ArrayList<java.lang.String> r8 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x0331 }
            r8.add(r4)     // Catch:{ Exception -> 0x0331 }
            goto L_0x032c
        L_0x031a:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0331 }
            if (r4 != 0) goto L_0x0325
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x0331 }
            r4.<init>()     // Catch:{ Exception -> 0x0331 }
            r15.documentsUrisArray = r4     // Catch:{ Exception -> 0x0331 }
        L_0x0325:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0331 }
            r4.add(r8)     // Catch:{ Exception -> 0x0331 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x0331 }
        L_0x032c:
            int r3 = r3 + 1
            goto L_0x02c7
        L_0x032f:
            r0 = 0
            goto L_0x0336
        L_0x0331:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0335:
            r0 = 1
        L_0x0336:
            if (r0 == 0) goto L_0x0341
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
        L_0x0341:
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
            r36 = 0
            goto L_0x106c
        L_0x0349:
            java.lang.String r0 = r46.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0f9d
            android.net.Uri r0 = r46.getData()
            if (r0 == 0) goto L_0x0var_
            java.lang.String r4 = r0.getScheme()
            if (r4 == 0) goto L_0x0e25
            int r9 = r4.hashCode()
            r8 = 3699(0xe73, float:5.183E-42)
            if (r9 == r8) goto L_0x0388
            r8 = 3213448(0x310888, float:4.503E-39)
            if (r9 == r8) goto L_0x037e
            r8 = 99617003(0x5var_eb, float:2.2572767E-35)
            if (r9 == r8) goto L_0x0374
            goto L_0x0392
        L_0x0374:
            java.lang.String r8 = "https"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x0392
            r8 = 1
            goto L_0x0393
        L_0x037e:
            java.lang.String r8 = "http"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x0392
            r8 = 0
            goto L_0x0393
        L_0x0388:
            java.lang.String r8 = "tg"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x0392
            r8 = 2
            goto L_0x0393
        L_0x0392:
            r8 = -1
        L_0x0393:
            java.lang.String r9 = "text"
            r18 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            if (r8 == 0) goto L_0x0a36
            if (r8 == r13) goto L_0x0a36
            if (r8 == r5) goto L_0x03a0
        L_0x039d:
            r7 = 0
            goto L_0x0e25
        L_0x03a0:
            java.lang.String r0 = r0.toString()
            java.lang.String r8 = "tg:resolve"
            boolean r8 = r0.startsWith(r8)
            java.lang.String r5 = "nonce"
            java.lang.String r6 = "callback_url"
            java.lang.String r7 = "public_key"
            java.lang.String r13 = "bot_id"
            java.lang.String r4 = "payload"
            java.lang.String r12 = "scope"
            java.lang.String r10 = "tg://telegram.org"
            if (r8 != 0) goto L_0x095b
            java.lang.String r8 = "tg://resolve"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x03c4
            goto L_0x095b
        L_0x03c4:
            java.lang.String r8 = "tg:privatepost"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x090b
            java.lang.String r8 = "tg://privatepost"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x03d6
            goto L_0x090b
        L_0x03d6:
            java.lang.String r8 = "tg:bg"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x0797
            java.lang.String r8 = "tg://bg"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x03e8
            goto L_0x0797
        L_0x03e8:
            java.lang.String r8 = "tg:join"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x077b
            java.lang.String r8 = "tg://join"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x03fa
            goto L_0x077b
        L_0x03fa:
            java.lang.String r8 = "tg:addstickers"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x075b
            java.lang.String r8 = "tg://addstickers"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x040c
            goto L_0x075b
        L_0x040c:
            java.lang.String r8 = "tg:msg"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x06d7
            java.lang.String r8 = "tg://msg"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x06d7
            java.lang.String r8 = "tg://share"
            boolean r8 = r0.startsWith(r8)
            if (r8 != 0) goto L_0x06d7
            java.lang.String r8 = "tg:share"
            boolean r8 = r0.startsWith(r8)
            if (r8 == 0) goto L_0x042e
            goto L_0x06d7
        L_0x042e:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x06b9
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0440
            goto L_0x06b9
        L_0x0440:
            java.lang.String r1 = "tg:login"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0679
            java.lang.String r1 = "tg://login"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0452
            goto L_0x0679
        L_0x0452:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0625
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0464
            goto L_0x0625
        L_0x0464:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05c5
            java.lang.String r1 = "tg://passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05c5
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x047e
            goto L_0x05c5
        L_0x047e:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0591
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0490
            goto L_0x0591
        L_0x0490:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0553
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04a2
            goto L_0x0553
        L_0x04a2:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x04ec
            java.lang.String r1 = "tg://settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04b3
            goto L_0x04ec
        L_0x04b3:
            java.lang.String r1 = "tg://"
            java.lang.String r0 = r0.replace(r1, r2)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r2)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x04cc
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r1)
        L_0x04cc:
            r31 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0a2c
        L_0x04ec:
            java.lang.String r1 = "themes"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x050e
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 2
            goto L_0x0a26
        L_0x050e:
            java.lang.String r1 = "devices"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0530
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 3
            goto L_0x0a26
        L_0x0530:
            java.lang.String r1 = "folders"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x0a57
            r0 = 4
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 4
            goto L_0x0a26
        L_0x0553:
            java.lang.String r1 = "tg:addtheme"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r35 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            goto L_0x0e49
        L_0x0591:
            java.lang.String r1 = "tg:setlanguage"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r30 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            goto L_0x0a2a
        L_0x05c5:
            java.lang.String r1 = "tg:passport"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r12)
            boolean r8 = android.text.TextUtils.isEmpty(r2)
            if (r8 != 0) goto L_0x0604
            java.lang.String r8 = "{"
            boolean r8 = r2.startsWith(r8)
            if (r8 == 0) goto L_0x0604
            java.lang.String r8 = "}"
            boolean r8 = r2.endsWith(r8)
            if (r8 == 0) goto L_0x0604
            java.lang.String r4 = r0.getQueryParameter(r5)
            r1.put(r5, r4)
            goto L_0x060b
        L_0x0604:
            java.lang.String r5 = r0.getQueryParameter(r4)
            r1.put(r4, r5)
        L_0x060b:
            java.lang.String r4 = r0.getQueryParameter(r13)
            r1.put(r13, r4)
            r1.put(r12, r2)
            java.lang.String r2 = r0.getQueryParameter(r7)
            r1.put(r7, r2)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r1.put(r6, r0)
            goto L_0x09c1
        L_0x0625:
            java.lang.String r1 = "tg:openmessage"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "chat_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "message_id"
            java.lang.String r0 = r0.getQueryParameter(r4)
            if (r1 == 0) goto L_0x064e
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0657 }
            goto L_0x0658
        L_0x064e:
            if (r2 == 0) goto L_0x0657
            int r1 = java.lang.Integer.parseInt(r2)     // Catch:{ NumberFormatException -> 0x0657 }
            r2 = r1
            r1 = 0
            goto L_0x0659
        L_0x0657:
            r1 = 0
        L_0x0658:
            r2 = 0
        L_0x0659:
            if (r0 == 0) goto L_0x0660
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0660 }
            goto L_0x0661
        L_0x0660:
            r0 = 0
        L_0x0661:
            r26 = r0
            r23 = r1
            r24 = r2
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            goto L_0x0a24
        L_0x0679:
            java.lang.String r1 = "tg:login"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "code"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r32 = r0
            r33 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            goto L_0x0a30
        L_0x06b9:
            java.lang.String r1 = "tg:confirmphone"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "phone"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r3)
            r2 = r1
            r1 = 0
            goto L_0x094c
        L_0x06d7:
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
            if (r4 != 0) goto L_0x06fc
            goto L_0x06fd
        L_0x06fc:
            r2 = r4
        L_0x06fd:
            java.lang.String r4 = r0.getQueryParameter(r9)
            if (r4 == 0) goto L_0x072f
            int r4 = r2.length()
            if (r4 <= 0) goto L_0x071a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r1)
            java.lang.String r2 = r4.toString()
            r4 = 1
            goto L_0x071b
        L_0x071a:
            r4 = 0
        L_0x071b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r5.append(r0)
            java.lang.String r2 = r5.toString()
            goto L_0x0730
        L_0x072f:
            r4 = 0
        L_0x0730:
            int r0 = r2.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            if (r0 <= r5) goto L_0x073e
            java.lang.String r0 = r2.substring(r6, r5)
            goto L_0x073f
        L_0x073e:
            r0 = r2
        L_0x073f:
            boolean r2 = r0.endsWith(r1)
            if (r2 == 0) goto L_0x0750
            int r2 = r0.length()
            r5 = 1
            int r2 = r2 - r5
            java.lang.String r0 = r0.substring(r6, r2)
            goto L_0x073f
        L_0x0750:
            r1 = r0
            r9 = r4
            r0 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            goto L_0x0952
        L_0x075b:
            java.lang.String r1 = "tg:addstickers"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r8 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            goto L_0x0951
        L_0x077b:
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
            r2 = 0
            goto L_0x094d
        L_0x0797:
            java.lang.String r1 = "tg:bg"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "slug"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
            if (r2 != 0) goto L_0x07c5
            java.lang.String r2 = "color"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
        L_0x07c5:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x07e3
            int r2 = r2.length()
            r4 = 6
            if (r2 != r4) goto L_0x07e3
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x07de }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x07de }
            r4 = 16
            int r2 = java.lang.Integer.parseInt(r2, r4)     // Catch:{ Exception -> 0x07de }
            r2 = r2 | r18
            r0.background_color = r2     // Catch:{ Exception -> 0x07de }
        L_0x07de:
            r2 = 0
            r1.slug = r2
            goto L_0x08e5
        L_0x07e3:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x0843
            int r2 = r2.length()
            r4 = 13
            if (r2 != r4) goto L_0x0843
            java.lang.String r2 = r1.slug
            r4 = 6
            char r2 = r2.charAt(r4)
            r5 = 45
            if (r2 != r5) goto L_0x0843
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0826 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0826 }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0826 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0826 }
            r4 = r4 | r18
            r2.background_color = r4     // Catch:{ Exception -> 0x0826 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0826 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0826 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0826 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0826 }
            r4 = r4 | r18
            r2.second_background_color = r4     // Catch:{ Exception -> 0x0826 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0826 }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x0826 }
        L_0x0826:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x083e }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x083e }
            if (r2 != 0) goto L_0x083e
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x083e }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x083e }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x083e }
            r2.rotation = r0     // Catch:{ Exception -> 0x083e }
        L_0x083e:
            r2 = 0
            r1.slug = r2
            goto L_0x08e5
        L_0x0843:
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x0880
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r2 = r2.split(r4)
            if (r2 == 0) goto L_0x0880
            int r4 = r2.length
            if (r4 <= 0) goto L_0x0880
            r4 = 0
        L_0x085b:
            int r5 = r2.length
            if (r4 >= r5) goto L_0x0880
            r5 = r2[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x086e
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x087d
        L_0x086e:
            r6 = 1
            r5 = r2[r4]
            java.lang.String r7 = "motion"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x087d
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x087d:
            int r4 = r4 + 1
            goto L_0x085b
        L_0x0880:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r2.intensity = r4
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x08cd }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x08cd }
            if (r4 != 0) goto L_0x08cd
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x08cd }
            r5 = 6
            r6 = 0
            java.lang.String r7 = r2.substring(r6, r5)     // Catch:{ Exception -> 0x08cd }
            r6 = 16
            int r7 = java.lang.Integer.parseInt(r7, r6)     // Catch:{ Exception -> 0x08cd }
            r6 = r7 | r18
            r4.background_color = r6     // Catch:{ Exception -> 0x08cd }
            int r4 = r2.length()     // Catch:{ Exception -> 0x08cd }
            if (r4 <= r5) goto L_0x08cd
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x08cd }
            r5 = 7
            java.lang.String r2 = r2.substring(r5)     // Catch:{ Exception -> 0x08cd }
            r5 = 16
            int r2 = java.lang.Integer.parseInt(r2, r5)     // Catch:{ Exception -> 0x08cd }
            r2 = r2 | r18
            r4.second_background_color = r2     // Catch:{ Exception -> 0x08cd }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x08cd }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x08cd }
        L_0x08cd:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x08e5 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x08e5 }
            if (r2 != 0) goto L_0x08e5
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x08e5 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x08e5 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x08e5 }
            r2.rotation = r0     // Catch:{ Exception -> 0x08e5 }
        L_0x08e5:
            r34 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            goto L_0x0a32
        L_0x090b:
            java.lang.String r1 = "tg:privatepost"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r2 = "channel"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r2 = r1.intValue()
            if (r2 == 0) goto L_0x0949
            int r2 = r0.intValue()
            if (r2 != 0) goto L_0x093c
            goto L_0x0949
        L_0x093c:
            r13 = r0
            r12 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            goto L_0x0955
        L_0x0949:
            r0 = 0
            r1 = 0
            r2 = 0
        L_0x094c:
            r4 = 0
        L_0x094d:
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x0951:
            r9 = 0
        L_0x0952:
            r10 = 0
            r12 = 0
            r13 = 0
        L_0x0955:
            r18 = 0
            r19 = 0
            goto L_0x0a1e
        L_0x095b:
            java.lang.String r1 = "tg:resolve"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "telegrampassport"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x09dc
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r12)
            boolean r8 = android.text.TextUtils.isEmpty(r2)
            if (r8 != 0) goto L_0x09a2
            java.lang.String r8 = "{"
            boolean r8 = r2.startsWith(r8)
            if (r8 == 0) goto L_0x09a2
            java.lang.String r8 = "}"
            boolean r8 = r2.endsWith(r8)
            if (r8 == 0) goto L_0x09a2
            java.lang.String r4 = r0.getQueryParameter(r5)
            r1.put(r5, r4)
            goto L_0x09a9
        L_0x09a2:
            java.lang.String r5 = r0.getQueryParameter(r4)
            r1.put(r4, r5)
        L_0x09a9:
            java.lang.String r4 = r0.getQueryParameter(r13)
            r1.put(r13, r4)
            r1.put(r12, r2)
            java.lang.String r2 = r0.getQueryParameter(r7)
            r1.put(r7, r2)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r1.put(r6, r0)
        L_0x09c1:
            r29 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r18 = 0
            r19 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            goto L_0x0a28
        L_0x09dc:
            java.lang.String r2 = "start"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "startgroup"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "game"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "post"
            java.lang.String r0 = r0.getQueryParameter(r6)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r6 = r0.intValue()
            if (r6 != 0) goto L_0x0a0e
            r19 = r1
            r6 = r2
            r10 = r4
            r18 = r5
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r12 = 0
            goto L_0x0a1d
        L_0x0a0e:
            r12 = r0
            r19 = r1
            r6 = r2
            r10 = r4
            r18 = r5
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 2
            r7 = 0
            r8 = 0
            r9 = 0
        L_0x0a1d:
            r13 = 0
        L_0x0a1e:
            r23 = 0
            r24 = 0
            r26 = 0
        L_0x0a24:
            r28 = 0
        L_0x0a26:
            r29 = 0
        L_0x0a28:
            r30 = 0
        L_0x0a2a:
            r31 = 0
        L_0x0a2c:
            r32 = 0
            r33 = 0
        L_0x0a30:
            r34 = 0
        L_0x0a32:
            r35 = 0
            goto L_0x0e49
        L_0x0a36:
            java.lang.String r4 = r0.getHost()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r5 = "telegram.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0a5a
            java.lang.String r5 = "t.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0a5a
            java.lang.String r5 = "telegram.dog"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0a57
            goto L_0x0a5a
        L_0x0a57:
            r5 = 2
            goto L_0x039d
        L_0x0a5a:
            java.lang.String r4 = r0.getPath()
            if (r4 == 0) goto L_0x0de5
            int r5 = r4.length()
            r6 = 1
            if (r5 <= r6) goto L_0x0de5
            java.lang.String r4 = r4.substring(r6)
            java.lang.String r5 = "bg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0bcd
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "bg/"
            java.lang.String r2 = r4.replace(r5, r2)
            r1.slug = r2
            if (r2 == 0) goto L_0x0aa4
            int r2 = r2.length()
            r4 = 6
            if (r2 != r4) goto L_0x0aa4
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0a9e }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x0a9e }
            r4 = 16
            int r2 = java.lang.Integer.parseInt(r2, r4)     // Catch:{ Exception -> 0x0a9e }
            r2 = r2 | r18
            r0.background_color = r2     // Catch:{ Exception -> 0x0a9e }
        L_0x0a9e:
            r2 = 0
            r1.slug = r2
            r7 = r2
            goto L_0x0bba
        L_0x0aa4:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x0b04
            int r2 = r2.length()
            r4 = 13
            if (r2 != r4) goto L_0x0b04
            java.lang.String r2 = r1.slug
            r4 = 6
            char r2 = r2.charAt(r4)
            r5 = 45
            if (r2 != r5) goto L_0x0b04
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0ae7 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x0ae7 }
            r6 = 0
            java.lang.String r4 = r5.substring(r6, r4)     // Catch:{ Exception -> 0x0ae7 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0ae7 }
            r4 = r4 | r18
            r2.background_color = r4     // Catch:{ Exception -> 0x0ae7 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0ae7 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0ae7 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0ae7 }
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch:{ Exception -> 0x0ae7 }
            r4 = r4 | r18
            r2.second_background_color = r4     // Catch:{ Exception -> 0x0ae7 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0ae7 }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x0ae7 }
        L_0x0ae7:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0aff }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0aff }
            if (r2 != 0) goto L_0x0aff
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0aff }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0aff }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0aff }
            r2.rotation = r0     // Catch:{ Exception -> 0x0aff }
        L_0x0aff:
            r7 = 0
            r1.slug = r7
            goto L_0x0bba
        L_0x0b04:
            r7 = 0
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x0b42
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r2 = r2.split(r4)
            if (r2 == 0) goto L_0x0b42
            int r4 = r2.length
            if (r4 <= 0) goto L_0x0b42
            r4 = 0
        L_0x0b1d:
            int r5 = r2.length
            if (r4 >= r5) goto L_0x0b42
            r5 = r2[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0b30
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 1
            r5.blur = r6
            goto L_0x0b3f
        L_0x0b30:
            r6 = 1
            r5 = r2[r4]
            java.lang.String r8 = "motion"
            boolean r5 = r8.equals(r5)
            if (r5 == 0) goto L_0x0b3f
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r6
        L_0x0b3f:
            int r4 = r4 + 1
            goto L_0x0b1d
        L_0x0b42:
            java.lang.String r2 = "intensity"
            java.lang.String r2 = r0.getQueryParameter(r2)
            boolean r4 = android.text.TextUtils.isEmpty(r2)
            if (r4 != 0) goto L_0x0b5b
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r2 = r2.intValue()
            r4.intensity = r2
            goto L_0x0b61
        L_0x0b5b:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            r4 = 50
            r2.intensity = r4
        L_0x0b61:
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0ba2 }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0ba2 }
            if (r4 != 0) goto L_0x0b9d
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0ba2 }
            r5 = 6
            r6 = 0
            java.lang.String r8 = r2.substring(r6, r5)     // Catch:{ Exception -> 0x0ba2 }
            r6 = 16
            int r8 = java.lang.Integer.parseInt(r8, r6)     // Catch:{ Exception -> 0x0ba2 }
            r6 = r8 | r18
            r4.background_color = r6     // Catch:{ Exception -> 0x0ba2 }
            int r4 = r2.length()     // Catch:{ Exception -> 0x0ba2 }
            if (r4 <= r5) goto L_0x0ba2
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0ba2 }
            r5 = 7
            java.lang.String r2 = r2.substring(r5)     // Catch:{ Exception -> 0x0ba2 }
            r5 = 16
            int r2 = java.lang.Integer.parseInt(r2, r5)     // Catch:{ Exception -> 0x0ba2 }
            r2 = r2 | r18
            r4.second_background_color = r2     // Catch:{ Exception -> 0x0ba2 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0ba2 }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x0ba2 }
            goto L_0x0ba2
        L_0x0b9d:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0ba2 }
            r4 = -1
            r2.background_color = r4     // Catch:{ Exception -> 0x0ba2 }
        L_0x0ba2:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0bba }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0bba }
            if (r2 != 0) goto L_0x0bba
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0bba }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0bba }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0bba }
            r2.rotation = r0     // Catch:{ Exception -> 0x0bba }
        L_0x0bba:
            r20 = r1
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r23 = r19
            goto L_0x0bee
        L_0x0bcd:
            r7 = 0
            java.lang.String r5 = "login/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0bf3
            java.lang.String r0 = "login/"
            java.lang.String r2 = r4.replace(r0, r2)
            r19 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r20 = r18
        L_0x0bec:
            r23 = r20
        L_0x0bee:
            r24 = r23
            r5 = 2
            goto L_0x0dfb
        L_0x0bf3:
            java.lang.String r5 = "joinchat/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "joinchat/"
            java.lang.String r2 = r4.replace(r0, r2)
            r6 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r8 = r4
            r9 = r8
        L_0x0CLASSNAME:
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
        L_0x0c0d:
            r19 = r18
        L_0x0c0f:
            r20 = r19
            goto L_0x0bec
        L_0x0CLASSNAME:
            java.lang.String r5 = "addstickers/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "addstickers/"
            java.lang.String r2 = r4.replace(r0, r2)
            r8 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r9 = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r5 = "msg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x0d72
            java.lang.String r5 = "share/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0c3a
            goto L_0x0d72
        L_0x0c3a:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "phone"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r3)
            r2 = r1
            r4 = r7
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r20 = r19
            r23 = r20
            r24 = r23
            r5 = 2
            r26 = 0
            r1 = r0
            r0 = r24
            goto L_0x0dfd
        L_0x0CLASSNAME:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0CLASSNAME
            r0 = 12
            java.lang.String r2 = r4.substring(r0)
            r13 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r18 = r12
            goto L_0x0c0d
        L_0x0CLASSNAME:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0c9f
            r0 = 9
            java.lang.String r2 = r4.substring(r0)
            r18 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r19 = r13
            goto L_0x0c0f
        L_0x0c9f:
            java.lang.String r1 = "c/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0cf4
            java.util.List r0 = r0.getPathSegments()
            int r1 = r0.size()
            r2 = 3
            if (r1 != r2) goto L_0x0cdb
            r1 = 1
            java.lang.Object r2 = r0.get(r1)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            r5 = 2
            java.lang.Object r0 = r0.get(r5)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            if (r1 == 0) goto L_0x0cdc
            int r1 = r2.intValue()
            if (r1 != 0) goto L_0x0cd5
            goto L_0x0cdc
        L_0x0cd5:
            r44 = r2
            r2 = r0
            r0 = r44
            goto L_0x0cde
        L_0x0cdb:
            r5 = 2
        L_0x0cdc:
            r0 = r7
            r2 = r0
        L_0x0cde:
            r24 = r0
            r23 = r2
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r20 = r19
            goto L_0x0dfb
        L_0x0cf4:
            r5 = 2
            int r1 = r4.length()
            r2 = 1
            if (r1 < r2) goto L_0x0de7
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r2 = r0.getPathSegments()
            r1.<init>(r2)
            int r2 = r1.size()
            if (r2 <= 0) goto L_0x0d1e
            r2 = 0
            java.lang.Object r4 = r1.get(r2)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r6 = "s"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0d1f
            r1.remove(r2)
            goto L_0x0d1f
        L_0x0d1e:
            r2 = 0
        L_0x0d1f:
            int r4 = r1.size()
            if (r4 <= 0) goto L_0x0d45
            java.lang.Object r4 = r1.get(r2)
            r2 = r4
            java.lang.String r2 = (java.lang.String) r2
            int r4 = r1.size()
            r6 = 1
            if (r4 <= r6) goto L_0x0d43
            java.lang.Object r1 = r1.get(r6)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x0d47
        L_0x0d43:
            r1 = r7
            goto L_0x0d47
        L_0x0d45:
            r1 = r7
            r2 = r1
        L_0x0d47:
            java.lang.String r4 = "start"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r6 = "startgroup"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r8 = "game"
            java.lang.String r0 = r0.getQueryParameter(r8)
            r12 = r0
            r23 = r1
            r9 = r4
            r10 = r6
            r0 = r7
            r1 = r0
            r6 = r1
            r8 = r6
            r13 = r8
            r18 = r13
            r19 = r18
            r20 = r19
            r24 = r20
            r26 = 0
            r4 = r2
            r2 = r24
            goto L_0x0dfd
        L_0x0d72:
            r5 = 2
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0d7c
            goto L_0x0d7d
        L_0x0d7c:
            r2 = r4
        L_0x0d7d:
            java.lang.String r4 = r0.getQueryParameter(r9)
            if (r4 == 0) goto L_0x0daf
            int r4 = r2.length()
            if (r4 <= 0) goto L_0x0d9a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r1)
            java.lang.String r2 = r4.toString()
            r4 = 1
            goto L_0x0d9b
        L_0x0d9a:
            r4 = 0
        L_0x0d9b:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            java.lang.String r0 = r0.getQueryParameter(r9)
            r6.append(r0)
            java.lang.String r2 = r6.toString()
            goto L_0x0db0
        L_0x0daf:
            r4 = 0
        L_0x0db0:
            int r0 = r2.length()
            r6 = 16384(0x4000, float:2.2959E-41)
            r8 = 0
            if (r0 <= r6) goto L_0x0dbd
            java.lang.String r2 = r2.substring(r8, r6)
        L_0x0dbd:
            boolean r0 = r2.endsWith(r1)
            if (r0 == 0) goto L_0x0dce
            int r0 = r2.length()
            r6 = 1
            int r0 = r0 - r6
            java.lang.String r2 = r2.substring(r8, r0)
            goto L_0x0dbd
        L_0x0dce:
            r0 = r2
            r26 = r4
            r1 = r7
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r20 = r19
            r23 = r20
            r24 = r23
            goto L_0x0dfd
        L_0x0de5:
            r5 = 2
            r7 = 0
        L_0x0de7:
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r9 = r8
            r10 = r9
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r20 = r19
            r23 = r20
            r24 = r23
        L_0x0dfb:
            r26 = 0
        L_0x0dfd:
            r29 = r7
            r31 = r29
            r33 = r31
            r30 = r13
            r35 = r18
            r32 = r19
            r34 = r20
            r13 = r24
            r24 = 0
            r28 = 0
            r19 = r4
            r4 = r6
            r6 = r9
            r18 = r12
            r12 = r23
            r9 = r26
            r23 = 0
            r26 = 0
            r44 = r1
            r1 = r0
            r0 = r44
            goto L_0x0e49
        L_0x0e25:
            r0 = r7
            r1 = r0
            r2 = r1
            r4 = r2
            r6 = r4
            r8 = r6
            r10 = r8
            r12 = r10
            r13 = r12
            r18 = r13
            r19 = r18
            r29 = r19
            r30 = r29
            r31 = r30
            r32 = r31
            r33 = r32
            r34 = r33
            r35 = r34
            r9 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
        L_0x0e49:
            if (r32 != 0) goto L_0x0e5f
            int r5 = r15.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            boolean r5 = r5.isClientActivated()
            if (r5 == 0) goto L_0x0e58
            goto L_0x0e5f
        L_0x0e58:
            r42 = r11
            r2 = r15
            r36 = 0
            goto L_0x0f7e
        L_0x0e5f:
            if (r2 != 0) goto L_0x0var_
            if (r0 == 0) goto L_0x0e65
            goto L_0x0var_
        L_0x0e65:
            if (r19 != 0) goto L_0x0var_
            if (r4 != 0) goto L_0x0var_
            if (r8 != 0) goto L_0x0var_
            if (r1 != 0) goto L_0x0var_
            if (r18 != 0) goto L_0x0var_
            if (r29 != 0) goto L_0x0var_
            if (r31 != 0) goto L_0x0var_
            if (r30 != 0) goto L_0x0var_
            if (r32 != 0) goto L_0x0var_
            if (r34 != 0) goto L_0x0var_
            if (r13 != 0) goto L_0x0var_
            if (r35 != 0) goto L_0x0var_
            if (r33 == 0) goto L_0x0e81
            goto L_0x0var_
        L_0x0e81:
            android.content.ContentResolver r36 = r45.getContentResolver()     // Catch:{ Exception -> 0x0var_ }
            android.net.Uri r37 = r46.getData()     // Catch:{ Exception -> 0x0var_ }
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            android.database.Cursor r1 = r36.query(r37, r38, r39, r40, r41)     // Catch:{ Exception -> 0x0var_ }
            if (r1 == 0) goto L_0x0ef8
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x0eec }
            if (r0 == 0) goto L_0x0ef8
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0eec }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0eec }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x0eec }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0eec }
            r2 = 0
            r5 = 3
        L_0x0eb1:
            if (r2 >= r5) goto L_0x0ece
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x0ecb }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x0ecb }
            if (r3 != r0) goto L_0x0ec7
            r3 = 0
            r11[r3] = r2     // Catch:{ all -> 0x0ecb }
            r0 = r11[r3]     // Catch:{ all -> 0x0ecb }
            r3 = 1
            r15.switchToAccount(r0, r3)     // Catch:{ all -> 0x0eea }
            goto L_0x0ecf
        L_0x0ec7:
            r3 = 1
            int r2 = r2 + 1
            goto L_0x0eb1
        L_0x0ecb:
            r0 = move-exception
            r3 = 1
            goto L_0x0eef
        L_0x0ece:
            r3 = 1
        L_0x0ecf:
            java.lang.String r0 = "DATA4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0eea }
            int r0 = r1.getInt(r0)     // Catch:{ all -> 0x0eea }
            r2 = 0
            r4 = r11[r2]     // Catch:{ all -> 0x0eea }
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)     // Catch:{ all -> 0x0eea }
            int r6 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x0eea }
            java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ all -> 0x0eea }
            r4.postNotificationName(r6, r8)     // Catch:{ all -> 0x0eea }
            r23 = r0
            goto L_0x0efa
        L_0x0eea:
            r0 = move-exception
            goto L_0x0eef
        L_0x0eec:
            r0 = move-exception
            r3 = 1
            r5 = 3
        L_0x0eef:
            throw r0     // Catch:{ all -> 0x0ef0 }
        L_0x0ef0:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x0ef7
            r1.close()     // Catch:{ all -> 0x0ef7 }
        L_0x0ef7:
            throw r2     // Catch:{ Exception -> 0x0var_ }
        L_0x0ef8:
            r3 = 1
            r5 = 3
        L_0x0efa:
            if (r1 == 0) goto L_0x0var_
            r1.close()     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r3 = 1
            r5 = 3
        L_0x0var_:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0var_:
            r42 = r11
            r2 = r15
            r12 = r23
            r13 = r28
            r36 = 0
            goto L_0x0f8e
        L_0x0var_:
            r3 = 1
            r5 = 3
            if (r1 == 0) goto L_0x0var_
            java.lang.String r0 = "@"
            boolean r0 = r1.startsWith(r0)
            if (r0 == 0) goto L_0x0var_
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = " "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x0var_
        L_0x0var_:
            r0 = r1
        L_0x0var_:
            r25 = 0
            r2 = r11[r25]
            r20 = 0
            r1 = r45
            r27 = 1
            r3 = r19
            r19 = 3
            r5 = r8
            r8 = 3
            r7 = r10
            r10 = 3
            r36 = 0
            r8 = r0
            r10 = r12
            r12 = r11
            r11 = r13
            r42 = r12
            r13 = 0
            r12 = r18
            r13 = r29
            r14 = r30
            r15 = r31
            r16 = r32
            r17 = r33
            r18 = r34
            r19 = r35
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r2 = r45
            goto L_0x0f7e
        L_0x0var_:
            r42 = r11
            r36 = 0
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r4 = "phone"
            r1.putString(r4, r2)
            r1.putString(r3, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$N1thb-LLgMOn57u-_wgkv5RqrBk r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$N1thb-LLgMOn57u-_wgkv5RqrBk
            r2 = r45
            r0.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0f7e:
            r12 = r23
            r13 = r28
            goto L_0x0f8e
        L_0x0var_:
            r42 = r11
            r2 = r15
            r36 = 0
            r12 = 0
            r13 = 0
            r24 = 0
            r26 = 0
        L_0x0f8e:
            r1 = r46
            r8 = r13
            r0 = r24
            r4 = r26
            r6 = r42
            r3 = 0
            r5 = 0
            r7 = 0
            r9 = 0
            goto L_0x1073
        L_0x0f9d:
            r42 = r11
            r2 = r15
            r36 = 0
            java.lang.String r0 = r46.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0fba
            r1 = r46
            r6 = r42
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 1
            goto L_0x1071
        L_0x0fba:
            java.lang.String r0 = r46.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0fd3
            r1 = r46
            r6 = r42
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            goto L_0x1072
        L_0x0fd3:
            java.lang.String r0 = r46.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x103b
            java.lang.String r0 = "chatId"
            r1 = r46
            r3 = 0
            int r0 = r1.getIntExtra(r0, r3)
            java.lang.String r4 = "userId"
            int r4 = r1.getIntExtra(r4, r3)
            java.lang.String r5 = "encId"
            int r5 = r1.getIntExtra(r5, r3)
            if (r0 == 0) goto L_0x1009
            r6 = r42
            r4 = r6[r3]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r4.postNotificationName(r5, r7)
            r5 = 0
        L_0x1006:
            r12 = 0
        L_0x1007:
            r13 = 0
            goto L_0x1033
        L_0x1009:
            r6 = r42
            if (r4 == 0) goto L_0x101e
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r7)
            r12 = r4
            r0 = 0
            r5 = 0
            goto L_0x1007
        L_0x101e:
            if (r5 == 0) goto L_0x102f
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r7)
            r0 = 0
            goto L_0x1006
        L_0x102f:
            r0 = 0
            r5 = 0
            r12 = 0
            r13 = 1
        L_0x1033:
            r43 = r13
            r4 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r13 = 0
            goto L_0x1076
        L_0x103b:
            r1 = r46
            r6 = r42
            r3 = 0
            java.lang.String r0 = r46.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1055
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r12 = 0
            r13 = 1
            goto L_0x1074
        L_0x1055:
            java.lang.String r0 = r46.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x106c
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 1
            goto L_0x1070
        L_0x1066:
            r36 = r8
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
        L_0x106c:
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x1070:
            r8 = 0
        L_0x1071:
            r9 = 0
        L_0x1072:
            r12 = 0
        L_0x1073:
            r13 = 0
        L_0x1074:
            r43 = 0
        L_0x1076:
            int r10 = r2.currentAccount
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
            boolean r10 = r10.isClientActivated()
            if (r10 == 0) goto L_0x1391
            if (r12 == 0) goto L_0x10cf
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "user_id"
            r0.putInt(r5, r12)
            if (r4 == 0) goto L_0x1095
            java.lang.String r5 = "message_id"
            r0.putInt(r5, r4)
        L_0x1095:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x10b8
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = mainFragmentsStack
            int r6 = r5.size()
            r10 = 1
            int r6 = r6 - r10
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            boolean r4 = r4.checkCanOpenChat(r0, r5)
            if (r4 == 0) goto L_0x10cd
            goto L_0x10b9
        L_0x10b8:
            r10 = 1
        L_0x10b9:
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r2.actionBarLayout
            r13 = 0
            r14 = 1
            r15 = 1
            r16 = 0
            boolean r0 = r11.presentFragment(r12, r13, r14, r15, r16)
            if (r0 == 0) goto L_0x10cd
        L_0x10cb:
            r13 = 1
            goto L_0x1133
        L_0x10cd:
            r13 = 0
            goto L_0x1133
        L_0x10cf:
            r10 = 1
            if (r0 == 0) goto L_0x1117
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.String r7 = "chat_id"
            r5.putInt(r7, r0)
            if (r4 == 0) goto L_0x10e3
            java.lang.String r0 = "message_id"
            r5.putInt(r0, r4)
        L_0x10e3:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1104
            r0 = r6[r3]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r6 = r4.size()
            int r6 = r6 - r10
            java.lang.Object r4 = r4.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r0 = r0.checkCanOpenChat(r5, r4)
            if (r0 == 0) goto L_0x10cd
        L_0x1104:
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r2.actionBarLayout
            r13 = 0
            r14 = 1
            r15 = 1
            r16 = 0
            boolean r0 = r11.presentFragment(r12, r13, r14, r15, r16)
            if (r0 == 0) goto L_0x10cd
            goto L_0x10cb
        L_0x1117:
            if (r5 == 0) goto L_0x1138
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "enc_id"
            r0.putInt(r4, r5)
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r2.actionBarLayout
            r13 = 0
            r14 = 1
            r15 = 1
            r16 = 0
            boolean r13 = r11.presentFragment(r12, r13, r14, r15, r16)
        L_0x1133:
            r0 = r47
            r4 = 0
            goto L_0x1396
        L_0x1138:
            if (r43 == 0) goto L_0x1170
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1146
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.removeAllFragments()
            goto L_0x116e
        L_0x1146:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x116e
        L_0x1150:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r10
            if (r0 <= 0) goto L_0x1169
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r0.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r4)
            goto L_0x1150
        L_0x1169:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.closeLastFragment(r3)
        L_0x116e:
            r0 = 0
            goto L_0x1190
        L_0x1170:
            if (r13 == 0) goto L_0x1193
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x118e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r4 = new org.telegram.ui.Components.AudioPlayerAlert
            r4.<init>(r2)
            r0.showDialog(r4)
        L_0x118e:
            r0 = r47
        L_0x1190:
            r4 = 0
            goto L_0x1395
        L_0x1193:
            if (r7 == 0) goto L_0x11b7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x118e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r4 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4
            r5.<init>(r6)
            r4.<init>(r2, r5)
            r0.showDialog(r4)
            goto L_0x118e
        L_0x11b7:
            java.lang.String r0 = r2.videoPath
            if (r0 != 0) goto L_0x125a
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r2.photoPathsArray
            if (r0 != 0) goto L_0x125a
            java.lang.String r0 = r2.sendingText
            if (r0 != 0) goto L_0x125a
            java.util.ArrayList<java.lang.String> r0 = r2.documentsPathsArray
            if (r0 != 0) goto L_0x125a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r2.contactsToSend
            if (r0 != 0) goto L_0x125a
            java.util.ArrayList<android.net.Uri> r0 = r2.documentsUrisArray
            if (r0 == 0) goto L_0x11d1
            goto L_0x125a
        L_0x11d1:
            if (r8 == 0) goto L_0x121b
            if (r8 != r10) goto L_0x11db
            org.telegram.ui.SettingsActivity r7 = new org.telegram.ui.SettingsActivity
            r7.<init>()
            goto L_0x11f7
        L_0x11db:
            r4 = 2
            if (r8 != r4) goto L_0x11e4
            org.telegram.ui.ThemeActivity r7 = new org.telegram.ui.ThemeActivity
            r7.<init>(r3)
            goto L_0x11f7
        L_0x11e4:
            r4 = 3
            if (r8 != r4) goto L_0x11ed
            org.telegram.ui.SessionsActivity r7 = new org.telegram.ui.SessionsActivity
            r7.<init>(r3)
            goto L_0x11f7
        L_0x11ed:
            r0 = 4
            if (r8 != r0) goto L_0x11f6
            org.telegram.ui.FiltersSetupActivity r7 = new org.telegram.ui.FiltersSetupActivity
            r7.<init>()
            goto L_0x11f7
        L_0x11f6:
            r7 = 0
        L_0x11f7:
            org.telegram.ui.-$$Lambda$LaunchActivity$YC0TQSCzjX6yEZP4KwCgFQIY6u8 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$YC0TQSCzjX6yEZP4KwCgFQIY6u8
            r0.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1215
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1251
        L_0x1215:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r3)
            goto L_0x1251
        L_0x121b:
            if (r9 == 0) goto L_0x1257
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "destroyAfterSelect"
            r0.putBoolean(r4, r10)
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r2.actionBarLayout
            org.telegram.ui.ContactsActivity r12 = new org.telegram.ui.ContactsActivity
            r12.<init>(r0)
            r13 = 0
            r14 = 1
            r15 = 1
            r16 = 0
            r11.presentFragment(r12, r13, r14, r15, r16)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x124c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x1251
        L_0x124c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r3)
        L_0x1251:
            r0 = r47
            r4 = 0
            r13 = 1
            goto L_0x1396
        L_0x1257:
            r4 = 0
            goto L_0x1393
        L_0x125a:
            r4 = 3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x126e
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r6)
        L_0x126e:
            int r0 = (r21 > r36 ? 1 : (r21 == r36 ? 0 : -1))
            if (r0 != 0) goto L_0x1380
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "onlySelect"
            r0.putBoolean(r5, r10)
            java.lang.String r5 = "dialogsType"
            r0.putInt(r5, r4)
            java.lang.String r4 = "allowSwitchAccount"
            r0.putBoolean(r4, r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r2.contactsToSend
            if (r4 == 0) goto L_0x12ad
            int r4 = r4.size()
            if (r4 == r10) goto L_0x12c9
            r4 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            java.lang.String r5 = "SendContactToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            java.lang.String r5 = "SendContactToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
            goto L_0x12c9
        L_0x12ad:
            r4 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            java.lang.String r5 = "SendMessagesToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            java.lang.String r5 = "SendMessagesToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
        L_0x12c9:
            org.telegram.ui.DialogsActivity r12 = new org.telegram.ui.DialogsActivity
            r12.<init>(r0)
            r12.setDelegate(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x12f6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x12f4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r10
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x12f4
        L_0x12f2:
            r13 = 1
            goto L_0x1312
        L_0x12f4:
            r13 = 0
            goto L_0x1312
        L_0x12f6:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r10) goto L_0x12f4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r10
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x12f4
            goto L_0x12f2
        L_0x1312:
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r2.actionBarLayout
            r14 = 1
            r15 = 1
            r16 = 0
            r11.presentFragment(r12, r13, r14, r15, r16)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x1333
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1333
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r3, r3)
            goto L_0x1362
        L_0x1333:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x134b
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x134b
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r3, r10)
            goto L_0x1362
        L_0x134b:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x1362
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1362
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r3, r10)
        L_0x1362:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1379
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x1251
        L_0x1379:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r3)
            goto L_0x1251
        L_0x1380:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r4 = java.lang.Long.valueOf(r21)
            r0.add(r4)
            r4 = 0
            r2.didSelectDialogs(r4, r0, r4, r3)
            goto L_0x1393
        L_0x1391:
            r4 = 0
            r10 = 1
        L_0x1393:
            r0 = r47
        L_0x1395:
            r13 = 0
        L_0x1396:
            if (r13 != 0) goto L_0x1434
            if (r0 != 0) goto L_0x1434
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x13e5
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x13c6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x141f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x141f
        L_0x13c6:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x141f
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r3)
            goto L_0x141f
        L_0x13e5:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x141f
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x140b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x141f
        L_0x140b:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r3)
        L_0x141f:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1434
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
        L_0x1434:
            r1.setAction(r4)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$5$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$30$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$7$LaunchActivity(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(iArr, sharingLocationInfo.messageObject.getDialogId()) {
            private final /* synthetic */ int[] f$0;
            private final /* synthetic */ long f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        lambda$runLinkRequest$30$LaunchActivity(locationActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0284  */
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
            r7 = 2131625987(0x7f0e0803, float:1.8879197E38)
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
            r1 = 2131624195(0x7f0e0103, float:1.8875563E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626023(0x7f0e0827, float:1.887927E38)
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
            r1 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624317(0x7f0e017d, float:1.887581E38)
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
            int[] r8 = new int[r11]
            r8[r12] = r12
            if (r0 == 0) goto L_0x010f
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r1.<init>()
            r1.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$fRpscu2eN28IR3N0XKb9kTzXU00 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$fRpscu2eN28IR3N0XKb9kTzXU00
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
            r8[r12] = r0
            goto L_0x0328
        L_0x010f:
            if (r5 == 0) goto L_0x0142
            if (r1 != 0) goto L_0x012b
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$vUk_dybBMAIvbhU62z04SQXrPks r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$vUk_dybBMAIvbhU62z04SQXrPks
            r6.<init>(r3, r7, r5)
            int r0 = r1.sendRequest(r0, r6, r2)
            r8[r12] = r0
            goto L_0x0328
        L_0x012b:
            if (r1 != r11) goto L_0x0328
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.-$$Lambda$LaunchActivity$xereN_8MowaIDMCzEOq6V5KMYEA r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$xereN_8MowaIDMCzEOq6V5KMYEA
            r5.<init>(r3, r7)
            r1.sendRequest(r0, r5, r2)
            goto L_0x0328
        L_0x0142:
            if (r6 == 0) goto L_0x0183
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0182
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r11
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x016c
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.ChatActivityEnterView r2 = r2.getChatActivityEnterView()
            goto L_0x016d
        L_0x016c:
            r2 = r10
        L_0x016d:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r5 = 0
            r22 = r3
            r23 = r21
            r24 = r1
            r25 = r0
            r26 = r5
            r27 = r2
            r22.<init>(r23, r24, r25, r26, r27)
            r1.showDialog(r3)
        L_0x0182:
            return
        L_0x0183:
            if (r9 == 0) goto L_0x01a3
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r11)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$Rls3K3QtgdE8loKShvCQMzPUlQE r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Rls3K3QtgdE8loKShvCQMzPUlQE
            r2 = r29
            r0.<init>(r2, r3, r9)
            r1.setDelegate(r0)
            r4.presentFragment(r1, r12, r11)
            goto L_0x0328
        L_0x01a3:
            if (r14 == 0) goto L_0x020e
            java.lang.String r0 = "bot_id"
            java.lang.Object r0 = r14.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 != 0) goto L_0x01b8
            return
        L_0x01b8:
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
            org.telegram.ui.-$$Lambda$LaunchActivity$vCehZA31YUFYTaDVEnzegxRmqeI r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$vCehZA31YUFYTaDVEnzegxRmqeI
            r23 = r9
            r24 = r21
            r25 = r8
            r26 = r22
            r27 = r7
            r28 = r6
            r29 = r1
            r30 = r2
            r31 = r5
            r23.<init>(r25, r26, r27, r28, r29, r30, r31)
            int r0 = r0.sendRequest(r6, r9)
            r8[r12] = r0
            goto L_0x0328
        L_0x020e:
            r0 = r35
            if (r0 == 0) goto L_0x022c
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$zcSIPeMix4HG2uFGcss6lWwGsz4 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$zcSIPeMix4HG2uFGcss6lWwGsz4
            r2.<init>(r7)
            int r0 = r0.sendRequest(r1, r2)
            r8[r12] = r0
            goto L_0x0328
        L_0x022c:
            java.lang.String r0 = "android"
            if (r15 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r1 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r1.<init>()
            r1.lang_code = r15
            r1.lang_pack = r0
            int r0 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c
            r2.<init>(r7)
            int r0 = r0.sendRequest(r1, r2)
            r8[r12] = r0
            goto L_0x0328
        L_0x024c:
            r1 = r38
            if (r1 == 0) goto L_0x02a7
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0281
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x027d }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x027d }
            int r5 = r5.background_color     // Catch:{ Exception -> 0x027d }
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x027d }
            int r6 = r6.second_background_color     // Catch:{ Exception -> 0x027d }
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings     // Catch:{ Exception -> 0x027d }
            int r9 = r9.rotation     // Catch:{ Exception -> 0x027d }
            int r9 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r9, r12)     // Catch:{ Exception -> 0x027d }
            r0.<init>(r2, r5, r6, r9)     // Catch:{ Exception -> 0x027d }
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x027d }
            r2.<init>(r0, r10)     // Catch:{ Exception -> 0x027d }
            org.telegram.ui.-$$Lambda$LaunchActivity$TJYsDPVYvHLPKqN-YVLsvbJGyt8 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$TJYsDPVYvHLPKqN-YVLsvbJGyt8     // Catch:{ Exception -> 0x027d }
            r0.<init>(r2)     // Catch:{ Exception -> 0x027d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x027d }
            goto L_0x0282
        L_0x027d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0281:
            r11 = 0
        L_0x0282:
            if (r11 != 0) goto L_0x0328
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r5 = r1.slug
            r2.slug = r5
            r0.wallpaper = r2
            int r2 = r4.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$zT-hFFzd9Wgk92DQ_D554ANyLac r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$zT-hFFzd9Wgk92DQ_D554ANyLac
            r5.<init>(r7, r1)
            int r0 = r2.sendRequest(r0, r5)
            r8[r12] = r0
            goto L_0x0328
        L_0x02a7:
            r1 = r39
            if (r1 == 0) goto L_0x02d2
            org.telegram.ui.-$$Lambda$LaunchActivity$qNS697CdkmPEpfzziDis1-GW2q8 r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$qNS697CdkmPEpfzziDis1-GW2q8
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
            org.telegram.ui.-$$Lambda$LaunchActivity$GZkLzPKxX0SNuZ7tUENydm9iGIg r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$GZkLzPKxX0SNuZ7tUENydm9iGIg
            r1.<init>(r7)
            int r0 = r0.sendRequest(r2, r1)
            r8[r12] = r0
            goto L_0x0328
        L_0x02d2:
            if (r31 == 0) goto L_0x0328
            if (r30 == 0) goto L_0x0328
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
            if (r1 != 0) goto L_0x0303
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r11
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x0304
        L_0x0303:
            r1 = r10
        L_0x0304:
            if (r1 == 0) goto L_0x0310
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r22)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x0328
        L_0x0310:
            org.telegram.ui.-$$Lambda$LaunchActivity$Rr1Y4K0xNnBGZjl4sgvK2e6gJ8k r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$Rr1Y4K0xNnBGZjl4sgvK2e6gJ8k
            r23 = r2
            r24 = r21
            r25 = r0
            r26 = r31
            r27 = r8
            r28 = r7
            r29 = r1
            r30 = r22
            r23.<init>(r25, r26, r27, r28, r29, r30)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0328:
            r0 = r8[r12]
            if (r0 == 0) goto L_0x0339
            org.telegram.ui.-$$Lambda$LaunchActivity$fN6e8T6z5S_MqvWxoiyHNaInNHI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$fN6e8T6z5S_MqvWxoiyHNaInNHI
            r0.<init>(r3, r8, r10)
            r7.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r7.showDelayed(r0)     // Catch:{ Exception -> 0x0339 }
        L_0x0339:
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

    public /* synthetic */ void lambda$runLinkRequest$14$LaunchActivity(String str, int i, String str2, String str3, Integer num, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, str, i, str2, str3, num, alertDialog) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC$TL_error f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ String f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ Integer f$7;
            private final /* synthetic */ AlertDialog f$8;

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
                LaunchActivity.this.lambda$null$13$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r0v13, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r0v56 */
    /* JADX WARNING: type inference failed for: r0v57 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00a1, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00c0, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0133  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$13$LaunchActivity(org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15, java.lang.String r16, int r17, java.lang.String r18, java.lang.String r19, java.lang.Integer r20, org.telegram.ui.ActionBar.AlertDialog r21) {
        /*
            r13 = this;
            r1 = r13
            r0 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            boolean r5 = r13.isFinishing()
            if (r5 != 0) goto L_0x0298
            r5 = r14
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r5 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r5
            r6 = 1
            r7 = 0
            if (r15 != 0) goto L_0x0276
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r1.actionBarLayout
            if (r8 == 0) goto L_0x0276
            if (r0 == 0) goto L_0x0026
            if (r0 == 0) goto L_0x0276
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0276
        L_0x0026:
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r17)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r5.users
            r8.putUsers(r9, r7)
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r17)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r9 = r5.chats
            r8.putChats(r9, r7)
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r17)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r5.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r10 = r5.chats
            r8.putUsersAndChats(r9, r10, r7, r6)
            java.lang.String r8 = "dialogsType"
            java.lang.String r9 = "onlySelect"
            if (r0 == 0) goto L_0x013a
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putBoolean(r9, r6)
            java.lang.String r4 = "cantSendToChannels"
            r3.putBoolean(r4, r6)
            r3.putInt(r8, r6)
            r4 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            java.lang.String r8 = "SendGameToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            java.lang.String r8 = "selectAlertString"
            r3.putString(r8, r4)
            r4 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            java.lang.String r8 = "SendGameToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            java.lang.String r8 = "selectAlertStringGroup"
            r3.putString(r8, r4)
            org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
            r4.<init>(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$OYiAv66YOgtstnL0zNrjeqSNdJw r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$OYiAv66YOgtstnL0zNrjeqSNdJw
            r3.<init>(r0, r2, r5)
            r4.setDelegate(r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x00a7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x00a5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00a5
        L_0x00a3:
            r0 = 1
            goto L_0x00c3
        L_0x00a5:
            r0 = 0
            goto L_0x00c3
        L_0x00a7:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r6) goto L_0x00a5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x00a5
            goto L_0x00a3
        L_0x00c3:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r1.actionBarLayout
            r3 = 1
            r5 = 1
            r8 = 0
            r14 = r2
            r15 = r4
            r16 = r0
            r17 = r3
            r18 = r5
            r19 = r8
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x00ed
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x00ed
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r7, r7)
            goto L_0x011c
        L_0x00ed:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x0105
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0105
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r7, r6)
            goto L_0x011c
        L_0x0105:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x011c
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x011c
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r7, r6)
        L_0x011c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r7)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0133
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x024f
        L_0x0133:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r7)
            goto L_0x024f
        L_0x013a:
            r0 = 0
            if (r3 == 0) goto L_0x01a6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r5.users
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x014d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r5.users
            java.lang.Object r0 = r0.get(r7)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
        L_0x014d:
            if (r0 == 0) goto L_0x0190
            boolean r4 = r0.bot
            if (r4 == 0) goto L_0x0158
            boolean r4 = r0.bot_nochats
            if (r4 == 0) goto L_0x0158
            goto L_0x0190
        L_0x0158:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            r4.putBoolean(r9, r6)
            r5 = 2
            r4.putInt(r8, r5)
            r8 = 2131624144(0x7f0e00d0, float:1.887546E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r0)
            r5[r7] = r9
            java.lang.String r7 = "%1$s"
            r5[r6] = r7
            java.lang.String r7 = "AddToTheGroupAlertText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r8, r5)
            java.lang.String r7 = "addToGroupAlertString"
            r4.putString(r7, r5)
            org.telegram.ui.DialogsActivity r5 = new org.telegram.ui.DialogsActivity
            r5.<init>(r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$-h31I40SekLamy9dVWGtQhuyj8U r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$-h31I40SekLamy9dVWGtQhuyj8U
            r4.<init>(r2, r0, r3)
            r5.setDelegate(r4)
            r13.lambda$runLinkRequest$30$LaunchActivity(r5)
            goto L_0x024f
        L_0x0190:
            java.lang.String r0 = "BotCantJoinGroups"
            r2 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x01a1 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r13, r0, r7)     // Catch:{ Exception -> 0x01a1 }
            r0.show()     // Catch:{ Exception -> 0x01a1 }
            goto L_0x01a5
        L_0x01a1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a5:
            return
        L_0x01a6:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r5.chats
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x01d7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r5.chats
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC$Chat) r8
            int r8 = r8.id
            java.lang.String r9 = "chat_id"
            r3.putInt(r9, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r5.chats
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC$Chat) r8
            int r8 = r8.id
            int r8 = -r8
            long r8 = (long) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r10 = r5.chats
            java.lang.Object r10 = r10.get(r7)
            org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
            goto L_0x01f2
        L_0x01d7:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC$User) r8
            int r8 = r8.id
            java.lang.String r9 = "user_id"
            r3.putInt(r9, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC$User) r8
            int r8 = r8.id
            long r8 = (long) r8
            r10 = r0
        L_0x01f2:
            if (r4 == 0) goto L_0x020f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r5.users
            int r11 = r11.size()
            if (r11 <= 0) goto L_0x020f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r5.users
            java.lang.Object r5 = r5.get(r7)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            boolean r5 = r5.bot
            if (r5 == 0) goto L_0x020f
            java.lang.String r5 = "botUser"
            r3.putString(r5, r4)
            r5 = 1
            goto L_0x0210
        L_0x020f:
            r5 = 0
        L_0x0210:
            if (r20 == 0) goto L_0x021b
            int r11 = r20.intValue()
            java.lang.String r12 = "message_id"
            r3.putInt(r12, r11)
        L_0x021b:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x0230
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r11 = r0.size()
            int r11 = r11 - r6
            java.lang.Object r0 = r0.get(r11)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
        L_0x0230:
            if (r0 == 0) goto L_0x023c
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r17)
            boolean r11 = r11.checkCanOpenChat(r3, r0)
            if (r11 == 0) goto L_0x024f
        L_0x023c:
            if (r5 == 0) goto L_0x0252
            boolean r5 = r0 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x0252
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r11 = r0.getDialogId()
            int r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0252
            r0.setBotUser(r4)
        L_0x024f:
            r10 = r21
            goto L_0x028d
        L_0x0252:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r17)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r20 != 0) goto L_0x025e
            r4 = 0
            goto L_0x0262
        L_0x025e:
            int r4 = r20.intValue()
        L_0x0262:
            org.telegram.ui.-$$Lambda$LaunchActivity$ngS0I82WewctrSS8-A4FFh1cbb0 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$ngS0I82WewctrSS8-A4FFh1cbb0
            r10 = r21
            r5.<init>(r10, r3)
            r14 = r0
            r15 = r8
            r17 = r2
            r18 = r4
            r19 = r5
            r14.ensureMessagesLoaded(r15, r17, r18, r19)
            r6 = 0
            goto L_0x028d
        L_0x0276:
            r10 = r21
            java.lang.String r0 = "NoUsernameFound"
            r2 = 2131625830(0x7f0e0766, float:1.887888E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0289 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r13, r0, r7)     // Catch:{ Exception -> 0x0289 }
            r0.show()     // Catch:{ Exception -> 0x0289 }
            goto L_0x028d
        L_0x0289:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x028d:
            if (r6 == 0) goto L_0x0298
            r21.dismiss()     // Catch:{ Exception -> 0x0293 }
            goto L_0x0298
        L_0x0293:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0298:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$13$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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

    public /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, i, alertDialog, str) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ AlertDialog f$4;
            private final /* synthetic */ String f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$17$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (android.text.TextUtils.isEmpty(r10.username) == false) goto L_0x0028;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006e, code lost:
        if (r14.checkCanOpenChat(r10, r0.get(r0.size() - 1)) != false) goto L_0x0070;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$17$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x00f6
            r0 = 0
            r1 = 1
            if (r10 != 0) goto L_0x00a9
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$ChatInvite r11 = (org.telegram.tgnet.TLRPC$ChatInvite) r11
            org.telegram.tgnet.TLRPC$Chat r10 = r11.chat
            if (r10 == 0) goto L_0x0093
            boolean r10 = org.telegram.messenger.ChatObject.isLeftFromChat(r10)
            if (r10 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$Chat r10 = r11.chat
            boolean r2 = r10.kicked
            if (r2 != 0) goto L_0x0093
            java.lang.String r10 = r10.username
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x0093
        L_0x0028:
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r14 = r11.chat
            r2 = 0
            r10.putChat(r14, r2)
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            org.telegram.tgnet.TLRPC$Chat r14 = r11.chat
            r10.add(r14)
            org.telegram.messenger.MessagesStorage r14 = org.telegram.messenger.MessagesStorage.getInstance(r12)
            r14.putUsersAndChats(r0, r10, r2, r1)
            android.os.Bundle r10 = new android.os.Bundle
            r10.<init>()
            org.telegram.tgnet.TLRPC$Chat r14 = r11.chat
            int r14 = r14.id
            java.lang.String r0 = "chat_id"
            r10.putInt(r0, r14)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = mainFragmentsStack
            boolean r14 = r14.isEmpty()
            if (r14 != 0) goto L_0x0070
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r1
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r14 = r14.checkCanOpenChat(r10, r0)
            if (r14 == 0) goto L_0x00ec
        L_0x0070:
            boolean[] r14 = new boolean[r1]
            org.telegram.ui.-$$Lambda$LaunchActivity$HPHkNcKXSEWV5e4qfJJUxs_pWOs r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$HPHkNcKXSEWV5e4qfJJUxs_pWOs
            r0.<init>(r14)
            r13.setOnCancelListener(r0)
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.chat
            int r12 = r11.id
            int r12 = -r12
            long r4 = (long) r12
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r11)
            r7 = 0
            org.telegram.ui.-$$Lambda$LaunchActivity$PauTp0-Pd8-I84_iuzH99t2SaRo r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$PauTp0-Pd8-I84_iuzH99t2SaRo
            r8.<init>(r13, r14, r10)
            r3.ensureMessagesLoaded(r4, r6, r7, r8)
            r1 = 0
            goto L_0x00ec
        L_0x0093:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = mainFragmentsStack
            int r12 = r10.size()
            int r12 = r12 - r1
            java.lang.Object r10 = r10.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            org.telegram.ui.Components.JoinGroupAlert r12 = new org.telegram.ui.Components.JoinGroupAlert
            r12.<init>(r9, r11, r14, r10)
            r10.showDialog(r12)
            goto L_0x00ec
        L_0x00a9:
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r11.<init>((android.content.Context) r9)
            r12 = 2131624195(0x7f0e0103, float:1.8875563E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r10 = r10.text
            java.lang.String r12 = "FLOOD_WAIT"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00d1
            r10 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x00dd
        L_0x00d1:
            r10 = 2131625498(0x7f0e061a, float:1.8878206E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x00dd:
            r10 = 2131625987(0x7f0e0803, float:1.8879197E38)
            java.lang.String r12 = "OK"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setPositiveButton(r10, r0)
            r9.showAlertDialog(r11)
        L_0x00ec:
            if (r1 == 0) goto L_0x00f6
            r13.dismiss()     // Catch:{ Exception -> 0x00f2 }
            goto L_0x00f6
        L_0x00f2:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x00f6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$17$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$15(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public /* synthetic */ void lambda$null$16$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!zArr[0]) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$20$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error, tLObject, i) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC$TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$19$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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

    public /* synthetic */ void lambda$runLinkRequest$21$LaunchActivity(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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

    public /* synthetic */ void lambda$runLinkRequest$25$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate(alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC$TL_account_authorizationForm f$3;
                private final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ String f$7;

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
                    LaunchActivity.this.lambda$null$23$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC$TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$24$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$23$LaunchActivity(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC$TL_account_authorizationForm f$4;
            private final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ String f$8;

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
                LaunchActivity.this.lambda$null$22$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$22$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$30$LaunchActivity(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$26$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
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

    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$28$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$28$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$32$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC$TL_wallPaper f$3;
            private final /* synthetic */ TLRPC$TL_error f$4;

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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$31$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC$TL_wallPaper r11, org.telegram.tgnet.TLRPC$TL_error r12) {
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
            r8.lambda$runLinkRequest$30$LaunchActivity(r9)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625066(0x7f0e046a, float:1.887733E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$31$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$33$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$35$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, alertDialog, tLRPC$TL_error) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$34$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$34$LaunchActivity(org.telegram.tgnet.TLObject r5, org.telegram.ui.ActionBar.AlertDialog r6, org.telegram.tgnet.TLRPC$TL_error r7) {
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
            r5 = 2131626933(0x7f0e0bb5, float:1.8881116E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131626953(0x7f0e0bc9, float:1.8881157E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$34$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$38$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = num.intValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new RequestDelegate(alertDialog, baseFragment, i, bundle) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ Bundle f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LaunchActivity.this.lambda$null$37$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$37$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, baseFragment, i, bundle) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ BaseFragment f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ Bundle f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$36$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$36$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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

    static /* synthetic */ void lambda$runLinkRequest$39(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LaunchActivity.this.lambda$checkAppUpdate$41$LaunchActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$41$LaunchActivity(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_help_appUpdate) tLObject, i) {
                private final /* synthetic */ TLRPC$TL_help_appUpdate f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LaunchActivity.this.lambda$null$40$LaunchActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$40$LaunchActivity(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
                    LaunchActivity.this.lambda$showAlertDialog$42$LaunchActivity(dialogInterface);
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$42$LaunchActivity(DialogInterface dialogInterface) {
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
        String str;
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList<Long> arrayList2 = arrayList;
        ArrayList<TLRPC$User> arrayList3 = this.contactsToSend;
        int i2 = 0;
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
        int i3 = 0;
        while (true) {
            boolean z2 = true;
            if (i3 < arrayList.size()) {
                long longValue = arrayList2.get(i3).longValue();
                int i4 = this.currentAccount;
                if (size <= 1) {
                    z2 = false;
                }
                if (!AlertsCreator.checkSlowMode(this, i4, longValue, z2)) {
                    i3++;
                } else {
                    return;
                }
            } else {
                int currentAccount2 = dialogsActivity2 != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
                if (arrayList.size() <= 1) {
                    long longValue2 = arrayList2.get(0).longValue();
                    int i5 = (int) longValue2;
                    int i6 = (int) (longValue2 >> 32);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (!AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance(currentAccount2).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    }
                    if (i5 == 0) {
                        bundle.putInt("enc_id", i6);
                    } else if (i5 > 0) {
                        bundle.putInt("user_id", i5);
                    } else if (i5 < 0) {
                        bundle.putInt("chat_id", -i5);
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
                    int i7 = 0;
                    while (i7 < arrayList.size()) {
                        long longValue3 = arrayList2.get(i7).longValue();
                        AccountInstance instance = AccountInstance.getInstance(UserConfig.selectedAccount);
                        if (chatActivity != null) {
                            this.actionBarLayout.presentFragment(chatActivity, dialogsActivity2 != null, dialogsActivity2 == null, true, false);
                            String str2 = this.videoPath;
                            if (str2 != null) {
                                chatActivity.openVideoEditor(str2, this.sendingText);
                                this.sendingText = null;
                            }
                        }
                        if (this.photoPathsArray != null) {
                            String str3 = this.sendingText;
                            if (str3 != null && str3.length() <= 1024 && this.photoPathsArray.size() == 1) {
                                this.photoPathsArray.get(i2).caption = this.sendingText;
                                this.sendingText = null;
                            }
                            i = 1024;
                            SendMessagesHelper.prepareSendingMedia(instance, this.photoPathsArray, longValue3, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, true, 0);
                        } else {
                            i = 1024;
                        }
                        if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                            String str4 = this.sendingText;
                            if (str4 != null && str4.length() <= i) {
                                ArrayList<String> arrayList8 = this.documentsPathsArray;
                                int size2 = arrayList8 != null ? arrayList8.size() : 0;
                                ArrayList<Uri> arrayList9 = this.documentsUrisArray;
                                if (size2 + (arrayList9 != null ? arrayList9.size() : 0) == 1) {
                                    String str5 = this.sendingText;
                                    this.sendingText = null;
                                    str = str5;
                                    SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str, this.documentsMimeType, longValue3, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                                }
                            }
                            str = null;
                            SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str, this.documentsMimeType, longValue3, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                        }
                        String str6 = this.sendingText;
                        if (str6 != null) {
                            SendMessagesHelper.prepareSendingText(instance, str6, longValue3, true, 0);
                        }
                        ArrayList<TLRPC$User> arrayList10 = this.contactsToSend;
                        if (arrayList10 != null && !arrayList10.isEmpty()) {
                            for (int i8 = 0; i8 < this.contactsToSend.size(); i8++) {
                                SendMessagesHelper.getInstance(currentAccount2).sendMessage(this.contactsToSend.get(i8), longValue3, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                            }
                        }
                        if (!TextUtils.isEmpty(charSequence)) {
                            SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue3, true, 0);
                        }
                        i7++;
                        i2 = 0;
                    }
                } else {
                    ArrayList<BaseFragment> arrayList11 = mainFragmentsStack;
                    PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList11.get(arrayList11.size() - 1), (ContactsController.Contact) null, (TLRPC$User) null, this.contactsToSendUri, (File) null, (String) null);
                    phonebookShareAlert.setDelegate(new PhonebookSelectShareAlert.PhonebookShareAlertDelegate(chatActivity, arrayList2, currentAccount2) {
                        private final /* synthetic */ ChatActivity f$1;
                        private final /* synthetic */ ArrayList f$2;
                        private final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i) {
                            LaunchActivity.this.lambda$didSelectDialogs$43$LaunchActivity(this.f$1, this.f$2, this.f$3, tLRPC$User, z, i);
                        }
                    });
                    ArrayList<BaseFragment> arrayList12 = mainFragmentsStack;
                    arrayList12.get(arrayList12.size() - 1).showDialog(phonebookShareAlert);
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

    public /* synthetic */ void lambda$didSelectDialogs$43$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$User, ((Long) arrayList.get(i3)).longValue(), (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
        }
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            Runnable runnable = this.lockRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lockRunnable = null;
            }
            int i = this.currentAccount;
            if (i != -1) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needCheckSystemBarColors);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$30$LaunchActivity(BaseFragment baseFragment) {
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
    }

    private void showPermissionErrorAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$showPermissionErrorAlert$44$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$44$LaunchActivity(DialogInterface dialogInterface, int i) {
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
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$OzjFUSFSzYpiz8AsR2YugcbvrAU.INSTANCE);
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
    }

    static /* synthetic */ void lambda$onPause$45() {
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
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$Zvo7oInSWRLEdJOrA3zyZG27mkE.INSTANCE);
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
    }

    static /* synthetic */ void lambda$onResume$46() {
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
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:230:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r13, int r14, java.lang.Object... r15) {
        /*
            r12 = this;
            int r0 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r13 != r0) goto L_0x0009
            r12.switchToAvailableAccountOrLogout()
            goto L_0x056a
        L_0x0009:
            int r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1 = 0
            if (r13 != r0) goto L_0x001a
            r13 = r15[r1]
            if (r13 == r12) goto L_0x056a
            r12.onFinish()
            r12.finish()
            goto L_0x056a
        L_0x001a:
            int r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r13 != r0) goto L_0x0049
            org.telegram.tgnet.ConnectionsManager r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r14)
            int r13 = r13.getConnectionState()
            int r15 = r12.currentConnectionState
            if (r15 == r13) goto L_0x056a
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
            goto L_0x056a
        L_0x0049:
            int r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r13 != r0) goto L_0x0054
            org.telegram.ui.Adapters.DrawerLayoutAdapter r13 = r12.drawerLayoutAdapter
            r13.notifyDataSetChanged()
            goto L_0x056a
        L_0x0054:
            int r0 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2 = 2131624484(0x7f0e0224, float:1.887615E38)
            java.lang.String r3 = "Cancel"
            r4 = 2131624195(0x7f0e0103, float:1.8875563E38)
            java.lang.String r5 = "AppName"
            r6 = 3
            r7 = 2
            r8 = 2131625987(0x7f0e0803, float:1.8879197E38)
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
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.String r4 = "MoreInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$bF2RAMQkjS-XZtlAJEgLJHkP4ac r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$bF2RAMQkjS-XZtlAJEgLJHkP4ac
            r4.<init>(r14)
            r0.setNegativeButton(r1, r4)
        L_0x00b2:
            int r14 = r13.intValue()
            r1 = 5
            if (r14 != r1) goto L_0x00ce
            r13 = 2131625835(0x7f0e076b, float:1.887889E38)
            java.lang.String r14 = "NobodyLikesSpam3"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00ce:
            int r14 = r13.intValue()
            if (r14 != 0) goto L_0x00e9
            r13 = 2131625833(0x7f0e0769, float:1.8878885E38)
            java.lang.String r14 = "NobodyLikesSpam1"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00e9:
            int r14 = r13.intValue()
            if (r14 != r11) goto L_0x0103
            r13 = 2131625834(0x7f0e076a, float:1.8878887E38)
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
            r13 = 2131625591(0x7f0e0677, float:1.8878394E38)
            java.lang.String r14 = "LogOut"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.-$$Lambda$LaunchActivity$zZysRuGcsEomhTOcorktwP65b_E r14 = new org.telegram.ui.-$$Lambda$LaunchActivity$zZysRuGcsEomhTOcorktwP65b_E
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
            r13 = 2131626445(0x7f0e09cd, float:1.8880126E38)
            java.lang.String r14 = "Proxy"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setTitle(r13)
            r13 = 2131627083(0x7f0e0c4b, float:1.888142E38)
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
            if (r13 != 0) goto L_0x056a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r0.create()
            r13.showDialog(r14)
            goto L_0x056a
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
            r0 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.String r1 = "ShareYouLocationUnableManually"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$q-CXmSfWmpKLmI8IZ1k-863-aIA r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$q-CXmSfWmpKLmI8IZ1k-863-aIA
            r1.<init>(r13, r14)
            r15.setNegativeButton(r0, r1)
            r13 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.String r14 = "ShareYouLocationUnable"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r15.setMessage(r13)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x056a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r15.create()
            r13.showDialog(r14)
            goto L_0x056a
        L_0x01dd:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r13 != r0) goto L_0x01f0
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x056a
            android.view.View r13 = r13.getChildAt(r1)
            if (r13 == 0) goto L_0x056a
            r13.invalidate()
            goto L_0x056a
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
            goto L_0x056a
        L_0x020b:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x056a
        L_0x0211:
            boolean r13 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r13 != 0) goto L_0x056a
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x0220 }
            r13.clearFlags(r14)     // Catch:{ Exception -> 0x0220 }
            goto L_0x056a
        L_0x0220:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x056a
        L_0x0226:
            int r0 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r13 != r0) goto L_0x0247
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r13 = r13.size()
            if (r13 <= r11) goto L_0x0242
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            boolean r13 = r13 instanceof org.telegram.ui.SettingsActivity
            if (r13 == 0) goto L_0x0242
            r1 = 1
        L_0x0242:
            r12.rebuildAllFragments(r1)
            goto L_0x056a
        L_0x0247:
            int r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r13 != r0) goto L_0x0250
            r12.showLanguageAlert(r1)
            goto L_0x056a
        L_0x0250:
            int r0 = org.telegram.messenger.NotificationCenter.openArticle
            if (r13 != r0) goto L_0x0282
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x025d
            return
        L_0x025d:
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
            goto L_0x056a
        L_0x0282:
            int r0 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r13 != r0) goto L_0x0307
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r12.actionBarLayout
            if (r13 == 0) goto L_0x0306
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = r13.fragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0293
            goto L_0x0306
        L_0x0293:
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
            r6 = 2131627051(0x7f0e0c2b, float:1.8881356E38)
            java.lang.String r7 = "UpdateContactsTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setTitle(r6)
            r6 = 2131627050(0x7f0e0c2a, float:1.8881353E38)
            java.lang.String r7 = "UpdateContactsMessage"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setMessage(r6)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.-$$Lambda$LaunchActivity$rLe83pzZN3vb5bi9r9BLBOnoFkA r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$rLe83pzZN3vb5bi9r9BLBOnoFkA
            r7.<init>(r14, r13, r0, r15)
            r5.setPositiveButton(r6, r7)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$rhp2KcYB1In1KhvA_Qn_mBve8YY r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$rhp2KcYB1In1KhvA_Qn_mBve8YY
            r3.<init>(r14, r13, r0, r15)
            r5.setNegativeButton(r2, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$yWZ09sr4SZBZ89evaU7BeAPv2KY r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$yWZ09sr4SZBZ89evaU7BeAPv2KY
            r2.<init>(r14, r13, r0, r15)
            r5.setOnBackButtonListener(r2)
            org.telegram.ui.ActionBar.AlertDialog r13 = r5.create()
            r4.showDialog(r13)
            r13.setCanceledOnTouchOutside(r1)
            goto L_0x056a
        L_0x0306:
            return
        L_0x0307:
            int r14 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r0 = 21
            if (r13 != r14) goto L_0x0367
            r13 = r15[r1]
            java.lang.Boolean r13 = (java.lang.Boolean) r13
            boolean r13 = r13.booleanValue()
            if (r13 != 0) goto L_0x0356
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x0341
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
        L_0x0341:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r0) goto L_0x0356
            android.app.ActivityManager$TaskDescription r13 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0356 }
            java.lang.String r14 = "actionBarDefault"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0356 }
            r15 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r14 = r14 | r15
            r13.<init>(r10, r10, r14)     // Catch:{ Exception -> 0x0356 }
            r12.setTaskDescription(r13)     // Catch:{ Exception -> 0x0356 }
        L_0x0356:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r13 = r12.drawerLayoutContainer
            java.lang.String r14 = "windowBackgroundWhite"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setBehindKeyboardColor(r14)
            r12.checkSystemBarColors()
            goto L_0x056a
        L_0x0367:
            int r14 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r13 != r14) goto L_0x0433
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r0) goto L_0x0407
            r13 = r15[r7]
            if (r13 == 0) goto L_0x0407
            android.widget.ImageView r13 = r12.themeSwitchImageView
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x037c
            return
        L_0x037c:
            r13 = r15[r7]     // Catch:{ all -> 0x0407 }
            int[] r13 = (int[]) r13     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r14 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            int r14 = r14.getMeasuredWidth()     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            int r0 = r0.getMeasuredHeight()     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            int r2 = r2.getMeasuredWidth()     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            int r3 = r3.getMeasuredHeight()     // Catch:{ all -> 0x0407 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0407 }
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)     // Catch:{ all -> 0x0407 }
            android.graphics.Canvas r3 = new android.graphics.Canvas     // Catch:{ all -> 0x0407 }
            r3.<init>(r2)     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            r4.draw(r3)     // Catch:{ all -> 0x0407 }
            android.widget.ImageView r3 = r12.themeSwitchImageView     // Catch:{ all -> 0x0407 }
            r3.setImageBitmap(r2)     // Catch:{ all -> 0x0407 }
            android.widget.ImageView r2 = r12.themeSwitchImageView     // Catch:{ all -> 0x0407 }
            r2.setVisibility(r1)     // Catch:{ all -> 0x0407 }
            r2 = r13[r1]     // Catch:{ all -> 0x0407 }
            int r2 = r14 - r2
            r3 = r13[r1]     // Catch:{ all -> 0x0407 }
            int r14 = r14 - r3
            int r2 = r2 * r14
            r14 = r13[r11]     // Catch:{ all -> 0x0407 }
            int r14 = r0 - r14
            r3 = r13[r11]     // Catch:{ all -> 0x0407 }
            int r3 = r0 - r3
            int r14 = r14 * r3
            int r2 = r2 + r14
            double r2 = (double) r2     // Catch:{ all -> 0x0407 }
            double r2 = java.lang.Math.sqrt(r2)     // Catch:{ all -> 0x0407 }
            r14 = r13[r1]     // Catch:{ all -> 0x0407 }
            r4 = r13[r1]     // Catch:{ all -> 0x0407 }
            int r14 = r14 * r4
            r4 = r13[r11]     // Catch:{ all -> 0x0407 }
            int r4 = r0 - r4
            r5 = r13[r11]     // Catch:{ all -> 0x0407 }
            int r0 = r0 - r5
            int r4 = r4 * r0
            int r14 = r14 + r4
            double r4 = (double) r14     // Catch:{ all -> 0x0407 }
            double r4 = java.lang.Math.sqrt(r4)     // Catch:{ all -> 0x0407 }
            double r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x0407 }
            float r14 = (float) r2     // Catch:{ all -> 0x0407 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r12.drawerLayoutContainer     // Catch:{ all -> 0x0407 }
            r2 = r13[r1]     // Catch:{ all -> 0x0407 }
            r13 = r13[r11]     // Catch:{ all -> 0x0407 }
            r3 = 0
            android.animation.Animator r13 = android.view.ViewAnimationUtils.createCircularReveal(r0, r2, r13, r3, r14)     // Catch:{ all -> 0x0407 }
            r2 = 400(0x190, double:1.976E-321)
            r13.setDuration(r2)     // Catch:{ all -> 0x0407 }
            android.view.animation.Interpolator r14 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x0407 }
            r13.setInterpolator(r14)     // Catch:{ all -> 0x0407 }
            org.telegram.ui.LaunchActivity$7 r14 = new org.telegram.ui.LaunchActivity$7     // Catch:{ all -> 0x0407 }
            r14.<init>()     // Catch:{ all -> 0x0407 }
            r13.addListener(r14)     // Catch:{ all -> 0x0407 }
            r13.start()     // Catch:{ all -> 0x0407 }
            r13 = 1
            goto L_0x0408
        L_0x0407:
            r13 = 0
        L_0x0408:
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
            if (r1 == 0) goto L_0x056a
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.rightActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            goto L_0x056a
        L_0x0433:
            int r14 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r13 != r14) goto L_0x0464
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x056a
            r14 = r15[r1]
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r13 = r13.getChildCount()
        L_0x0443:
            if (r1 >= r13) goto L_0x056a
            org.telegram.ui.Components.RecyclerListView r15 = r12.sideMenu
            android.view.View r15 = r15.getChildAt(r1)
            boolean r0 = r15 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r0 == 0) goto L_0x0461
            r0 = r15
            org.telegram.ui.Cells.DrawerUserCell r0 = (org.telegram.ui.Cells.DrawerUserCell) r0
            int r0 = r0.getAccountNumber()
            int r2 = r14.intValue()
            if (r0 != r2) goto L_0x0461
            r15.invalidate()
            goto L_0x056a
        L_0x0461:
            int r1 = r1 + 1
            goto L_0x0443
        L_0x0464:
            int r14 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r13 != r14) goto L_0x0473
            r13 = r15[r1]     // Catch:{ all -> 0x056a }
            com.google.android.gms.common.api.Status r13 = (com.google.android.gms.common.api.Status) r13     // Catch:{ all -> 0x056a }
            r14 = 140(0x8c, float:1.96E-43)
            r13.startResolutionForResult(r12, r14)     // Catch:{ all -> 0x056a }
            goto L_0x056a
        L_0x0473:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r13 != r14) goto L_0x0532
            java.lang.String r13 = r12.loadingThemeFileName
            if (r13 == 0) goto L_0x0500
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x056a
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
            if (r14 == 0) goto L_0x04fb
            java.lang.String r15 = r14.pathToWallpaper
            if (r15 == 0) goto L_0x04e4
            java.io.File r15 = new java.io.File
            java.lang.String r0 = r14.pathToWallpaper
            r15.<init>(r0)
            boolean r15 = r15.exists()
            if (r15 != 0) goto L_0x04e4
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r13 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r13.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r15 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r15.<init>()
            java.lang.String r0 = r14.slug
            r15.slug = r0
            r13.wallpaper = r15
            int r15 = r14.account
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            org.telegram.ui.-$$Lambda$LaunchActivity$NB4rmWZO1XiQbYcphQHxJu0Oj8Q r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$NB4rmWZO1XiQbYcphQHxJu0Oj8Q
            r0.<init>(r14)
            r15.sendRequest(r13, r0)
            return
        L_0x04e4:
            org.telegram.tgnet.TLRPC$TL_theme r14 = r12.loadingTheme
            java.lang.String r15 = r14.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r13, r15, r14, r11)
            if (r1 == 0) goto L_0x04fb
            org.telegram.ui.ThemePreviewActivity r13 = new org.telegram.ui.ThemePreviewActivity
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r13
            r0.<init>(r1, r2, r3, r4, r5)
            r12.lambda$runLinkRequest$30$LaunchActivity(r13)
        L_0x04fb:
            r12.onThemeLoadFinish()
            goto L_0x056a
        L_0x0500:
            java.lang.String r13 = r12.loadingThemeWallpaperName
            if (r13 == 0) goto L_0x056a
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x056a
            r12.loadingThemeWallpaperName = r10
            r13 = r15[r11]
            java.io.File r13 = (java.io.File) r13
            boolean r14 = r12.loadingThemeAccent
            if (r14 == 0) goto L_0x0525
            org.telegram.tgnet.TLRPC$TL_theme r13 = r12.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = r12.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r15 = r12.loadingThemeInfo
            r12.openThemeAccentPreview(r13, r14, r15)
            r12.onThemeLoadFinish()
            goto L_0x056a
        L_0x0525:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r12.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r15 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$-B6V0WWj11RTU3AcYX0QmqhCwL0 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$-B6V0WWj11RTU3AcYX0QmqhCwL0
            r0.<init>(r14, r13)
            r15.postRunnable(r0)
            goto L_0x056a
        L_0x0532:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r13 != r14) goto L_0x054e
            r13 = r15[r1]
            java.lang.String r13 = (java.lang.String) r13
            java.lang.String r14 = r12.loadingThemeFileName
            boolean r14 = r13.equals(r14)
            if (r14 != 0) goto L_0x054a
            java.lang.String r14 = r12.loadingThemeWallpaperName
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x056a
        L_0x054a:
            r12.onThemeLoadFinish()
            goto L_0x056a
        L_0x054e:
            int r14 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r13 != r14) goto L_0x0563
            boolean r13 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r13 == 0) goto L_0x0557
            return
        L_0x0557:
            boolean r13 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r13 == 0) goto L_0x055f
            r12.onPasscodeResume()
            goto L_0x056a
        L_0x055f:
            r12.onPasscodePause()
            goto L_0x056a
        L_0x0563:
            int r14 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r13 != r14) goto L_0x056a
            r12.checkSystemBarColors()
        L_0x056a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$47(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$48$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$50$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(hashMap, i) {
                    private final /* synthetic */ HashMap f$0;
                    private final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                        LaunchActivity.lambda$null$49(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$30$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$49(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$55$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$54$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$54$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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

    public /* synthetic */ void lambda$didReceivedNotification$57$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$56$LaunchActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$56$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$30$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$30$LaunchActivity(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
                    LaunchActivity.this.lambda$checkFreeDiscSpace$59$LaunchActivity();
                }
            }, 2000);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$59$LaunchActivity() {
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
                                LaunchActivity.this.lambda$null$58$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$null$58$LaunchActivity() {
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
            r9 = 2131624706(0x7f0e0302, float:1.88766E38)
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
            r14 = 2131625055(0x7f0e045f, float:1.8877307E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$VkdLLWBTOKy430kyIPZI4qTin7k r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$VkdLLWBTOKy430kyIPZI4qTin7k     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624707(0x7f0e0303, float:1.8876601E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$OtvHp1Fy9gMJymxCwl35oBI_mIw r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$OtvHp1Fy9gMJymxCwl35oBI_mIw     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131625987(0x7f0e0803, float:1.8879197E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$0kAJMcwHL3BemO-ZzCrDhD7Syb0 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$0kAJMcwHL3BemO-ZzCrDhD7Syb0     // Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$60(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$61$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$30$LaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$62$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                private final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                private final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$64$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                                }
                            }, 8);
                            TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                            tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                            tLRPC$TL_langpack_getStrings2.keys.add("English");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new RequestDelegate(localeInfoArr, str2) {
                                private final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                private final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$66$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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

    public /* synthetic */ void lambda$showLanguageAlert$64$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap, localeInfoArr, str) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$63$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$63$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
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
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            private final /* synthetic */ String f$3;

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
            $$Lambda$LaunchActivity$eu9dkhQEA9OGDVDjTCgLwCHFeM0 r4 = null;
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
                        LaunchActivity.this.lambda$updateCurrentConnectionState$67$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$67$LaunchActivity() {
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
            r2.lambda$runLinkRequest$30$LaunchActivity(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$67$LaunchActivity():void");
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
                } else if (baseFragment instanceof SettingsActivity) {
                    bundle.putString("fragment", "settings");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "group");
                } else if (baseFragment instanceof WallpapersListActivity) {
                    bundle.putString("fragment", "wallpapers");
                } else if ((baseFragment instanceof ProfileActivity) && ((ProfileActivity) baseFragment).isChat() && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat_profile");
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
