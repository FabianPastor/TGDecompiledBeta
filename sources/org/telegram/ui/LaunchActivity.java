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
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.Wallet.WalletActivity;
import org.telegram.ui.Wallet.WalletCreateActivity;

public class LaunchActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final int PLAY_SERVICES_REQUEST_CHECK_SETTINGS = 140;
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public View backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private ArrayList<TLRPC.User> contactsToSend;
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
    public ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC.TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TLRPC.TL_wallPaper loadingThemeWallpaper;
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
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0475, code lost:
        r0 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x0476, code lost:
        if (r0 == 0) goto L_0x04e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0478, code lost:
        if (r0 == 1) goto L_0x04d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x047a, code lost:
        if (r0 == 2) goto L_0x04bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x047c, code lost:
        if (r0 == 3) goto L_0x04a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x047f, code lost:
        if (r0 == 4) goto L_0x0495;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0482, code lost:
        if (r0 == 5) goto L_0x0486;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0486, code lost:
        r0 = new org.telegram.ui.WallpapersListActivity(0);
        r11.actionBarLayout.addFragmentToStack(r0);
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0495, code lost:
        if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0497, code lost:
        r0 = new org.telegram.ui.ProfileActivity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x04a2, code lost:
        if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x04a4, code lost:
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x04a9, code lost:
        if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x04ab, code lost:
        r0 = new org.telegram.ui.ChannelCreateActivity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x04b6, code lost:
        if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x04b8, code lost:
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x04bd, code lost:
        if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x04bf, code lost:
        r0 = new org.telegram.ui.GroupCreateFinalActivity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x04ca, code lost:
        if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x04cc, code lost:
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x04d1, code lost:
        r0 = new org.telegram.ui.SettingsActivity();
        r11.actionBarLayout.addFragmentToStack(r0);
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x04e0, code lost:
        if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x04e2, code lost:
        r0 = new org.telegram.ui.ChatActivity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x04ed, code lost:
        if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x04ef, code lost:
        r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x010d */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r12) {
        /*
            r11 = this;
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r0 = r11.getResources()
            android.content.res.Configuration r0 = r0.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r11, r0)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r11.currentAccount = r0
            int r0 = r11.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x00ec
            android.content.Intent r0 = r11.getIntent()
            if (r0 == 0) goto L_0x008c
            java.lang.String r3 = r0.getAction()
            if (r3 == 0) goto L_0x008c
            java.lang.String r3 = r0.getAction()
            java.lang.String r4 = "android.intent.action.SEND"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x0085
            java.lang.String r3 = r0.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0045
            goto L_0x0085
        L_0x0045:
            java.lang.String r3 = r0.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x008c
            android.net.Uri r3 = r0.getData()
            if (r3 == 0) goto L_0x008c
            java.lang.String r3 = r3.toString()
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = "tg:proxy"
            boolean r4 = r3.startsWith(r4)
            if (r4 != 0) goto L_0x0083
            java.lang.String r4 = "tg://proxy"
            boolean r4 = r3.startsWith(r4)
            if (r4 != 0) goto L_0x0083
            java.lang.String r4 = "tg:socks"
            boolean r4 = r3.startsWith(r4)
            if (r4 != 0) goto L_0x0083
            java.lang.String r4 = "tg://socks"
            boolean r3 = r3.startsWith(r4)
            if (r3 == 0) goto L_0x008c
        L_0x0083:
            r3 = 1
            goto L_0x008d
        L_0x0085:
            super.onCreate(r12)
            r11.finish()
            return
        L_0x008c:
            r3 = 0
        L_0x008d:
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            r5 = 0
            java.lang.String r7 = "intro_crashed_time"
            long r8 = r4.getLong(r7, r5)
            java.lang.String r10 = "fromIntro"
            boolean r10 = r0.getBooleanExtra(r10, r2)
            if (r10 == 0) goto L_0x00ac
            android.content.SharedPreferences$Editor r4 = r4.edit()
            android.content.SharedPreferences$Editor r4 = r4.putLong(r7, r5)
            r4.commit()
        L_0x00ac:
            if (r3 != 0) goto L_0x00ec
            long r3 = java.lang.System.currentTimeMillis()
            long r8 = r8 - r3
            long r3 = java.lang.Math.abs(r8)
            r5 = 120000(0x1d4c0, double:5.9288E-319)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x00ec
            if (r0 == 0) goto L_0x00ec
            if (r10 != 0) goto L_0x00ec
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r4 = "logininfo2"
            android.content.SharedPreferences r3 = r3.getSharedPreferences(r4, r2)
            java.util.Map r3 = r3.getAll()
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x00ec
            android.content.Intent r1 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r2 = org.telegram.ui.IntroActivity.class
            r1.<init>(r11, r2)
            android.net.Uri r0 = r0.getData()
            r1.setData(r0)
            r11.startActivity(r1)
            super.onCreate(r12)
            r11.finish()
            return
        L_0x00ec:
            r11.requestWindowFeature(r1)
            r0 = 2131689487(0x7f0var_f, float:1.900799E38)
            r11.setTheme(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            r4 = 0
            if (r0 < r3) goto L_0x0116
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r5 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x010d }
            java.lang.String r6 = "actionBarDefault"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ Exception -> 0x010d }
            r6 = r6 | r0
            r5.<init>(r4, r4, r6)     // Catch:{ Exception -> 0x010d }
            r11.setTaskDescription(r5)     // Catch:{ Exception -> 0x010d }
        L_0x010d:
            android.view.Window r5 = r11.getWindow()     // Catch:{ Exception -> 0x0115 }
            r5.setNavigationBarColor(r0)     // Catch:{ Exception -> 0x0115 }
            goto L_0x0116
        L_0x0115:
        L_0x0116:
            android.view.Window r0 = r11.getWindow()
            r5 = 2131165916(0x7var_dc, float:1.7946063E38)
            r0.setBackgroundDrawableResource(r5)
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x013a
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x013a
            android.view.Window r0 = r11.getWindow()     // Catch:{ Exception -> 0x0136 }
            r5 = 8192(0x2000, float:1.14794E-41)
            r0.setFlags(r5, r5)     // Catch:{ Exception -> 0x0136 }
            goto L_0x013a
        L_0x0136:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x013a:
            super.onCreate(r12)
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 24
            if (r0 < r5) goto L_0x0149
            boolean r0 = r11.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r0
        L_0x0149:
            org.telegram.ui.ActionBar.Theme.createChatResources(r11, r2)
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            if (r0 == 0) goto L_0x0162
            boolean r0 = org.telegram.messenger.SharedConfig.appLocked
            if (r0 == 0) goto L_0x0162
            long r5 = android.os.SystemClock.uptimeMillis()
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r7
            int r0 = (int) r5
            org.telegram.messenger.SharedConfig.lastPauseTime = r0
        L_0x0162:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r11)
            org.telegram.ui.LaunchActivity$1 r0 = new org.telegram.ui.LaunchActivity$1
            r0.<init>(r11)
            r11.actionBarLayout = r0
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r11)
            android.view.ViewGroup$LayoutParams r5 = new android.view.ViewGroup$LayoutParams
            r6 = -1
            r5.<init>(r6, r6)
            r11.setContentView(r0, r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 8
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r5 < r3) goto L_0x0197
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r11)
            r11.themeSwitchImageView = r3
            android.widget.ImageView r3 = r11.themeSwitchImageView
            r3.setVisibility(r7)
            android.widget.ImageView r3 = r11.themeSwitchImageView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8)
            r0.addView(r3, r5)
        L_0x0197:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = new org.telegram.ui.ActionBar.DrawerLayoutContainer
            r3.<init>(r11)
            r11.drawerLayoutContainer = r3
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r11.drawerLayoutContainer
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setBehindKeyboardColor(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r11.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8)
            r0.addView(r3, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x02a5
            android.view.Window r0 = r11.getWindow()
            r3 = 16
            r0.setSoftInputMode(r3)
            org.telegram.ui.LaunchActivity$2 r0 = new org.telegram.ui.LaunchActivity$2
            r0.<init>(r11)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r11.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8)
            r3.addView(r0, r5)
            android.view.View r3 = new android.view.View
            r3.<init>(r11)
            r11.backgroundTablet = r3
            android.content.res.Resources r3 = r11.getResources()
            r5 = 2131165313(0x7var_, float:1.794484E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r5)
            android.graphics.drawable.BitmapDrawable r3 = (android.graphics.drawable.BitmapDrawable) r3
            android.graphics.Shader$TileMode r5 = android.graphics.Shader.TileMode.REPEAT
            r3.setTileModeXY(r5, r5)
            android.view.View r5 = r11.backgroundTablet
            r5.setBackgroundDrawable(r3)
            android.view.View r3 = r11.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createRelative(r6, r6)
            r0.addView(r3, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            r0.addView(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = new org.telegram.ui.ActionBar.ActionBarLayout
            r3.<init>(r11)
            r11.rightActionBarLayout = r3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.rightActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = rightFragmentsStack
            r3.init(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.rightActionBarLayout
            r3.setDelegate(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.rightActionBarLayout
            r0.addView(r3)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r11)
            r11.shadowTabletSide = r3
            android.widget.FrameLayout r3 = r11.shadowTabletSide
            r5 = 1076449908(0x40295274, float:2.6456575)
            r3.setBackgroundColor(r5)
            android.widget.FrameLayout r3 = r11.shadowTabletSide
            r0.addView(r3)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r11)
            r11.shadowTablet = r3
            android.widget.FrameLayout r3 = r11.shadowTablet
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = layerFragmentsStack
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x023c
            r5 = 8
            goto L_0x023d
        L_0x023c:
            r5 = 0
        L_0x023d:
            r3.setVisibility(r5)
            android.widget.FrameLayout r3 = r11.shadowTablet
            r5 = 2130706432(0x7var_, float:1.7014118E38)
            r3.setBackgroundColor(r5)
            android.widget.FrameLayout r3 = r11.shadowTablet
            r0.addView(r3)
            android.widget.FrameLayout r3 = r11.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4
            r5.<init>()
            r3.setOnTouchListener(r5)
            android.widget.FrameLayout r3 = r11.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ r5 = org.telegram.ui.$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ.INSTANCE
            r3.setOnClickListener(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = new org.telegram.ui.ActionBar.ActionBarLayout
            r3.<init>(r11)
            r11.layersActionBarLayout = r3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            r3.setRemoveActionBarExtraHeight(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            android.widget.FrameLayout r5 = r11.shadowTablet
            r3.setBackgroundView(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            r3.setUseAlphaAnimations(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            r5 = 2131165298(0x7var_, float:1.794481E38)
            r3.setBackgroundResource(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = layerFragmentsStack
            r3.init(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            r3.setDelegate(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r11.drawerLayoutContainer
            r3.setDrawerLayoutContainer(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = layerFragmentsStack
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x029b
            goto L_0x029c
        L_0x029b:
            r7 = 0
        L_0x029c:
            r3.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            r0.addView(r3)
            goto L_0x02b1
        L_0x02a5:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            android.view.ViewGroup$LayoutParams r5 = new android.view.ViewGroup$LayoutParams
            r5.<init>(r6, r6)
            r0.addView(r3, r5)
        L_0x02b1:
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r11)
            r11.sideMenu = r0
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            org.telegram.ui.Components.SideMenultItemAnimator r3 = new org.telegram.ui.Components.SideMenultItemAnimator
            r3.<init>(r0)
            r0.setItemAnimator(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            java.lang.String r3 = "chats_menuBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r3 = new androidx.recyclerview.widget.LinearLayoutManager
            r3.<init>(r11, r1, r2)
            r0.setLayoutManager(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            r0.setAllowItemsInteractionDuringAnimation(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r3 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r5 = r0.getItemAnimator()
            r3.<init>(r11, r5)
            r11.drawerLayoutAdapter = r3
            r0.setAdapter(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            org.telegram.ui.Components.RecyclerListView r3 = r11.sideMenu
            r0.setDrawerLayout(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            r7 = 1134559232(0x43a00000, float:320.0)
            if (r5 == 0) goto L_0x030c
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x0323
        L_0x030c:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r3.x
            int r3 = r3.y
            int r3 = java.lang.Math.min(r7, r3)
            r7 = 1113587712(0x42600000, float:56.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r3 = r3 - r7
            int r3 = java.lang.Math.min(r5, r3)
        L_0x0323:
            r0.width = r3
            r0.height = r6
            org.telegram.ui.Components.RecyclerListView r3 = r11.sideMenu
            r3.setLayoutParams(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r11.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0 r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0
            r3.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            r0.setParentActionBarLayout(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r11.drawerLayoutContainer
            r0.setDrawerLayoutContainer(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            r0.init(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            r0.setDelegate(r11)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.Components.PasscodeView r0 = new org.telegram.ui.Components.PasscodeView
            r0.<init>(r11)
            r11.passcodeView = r0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            org.telegram.ui.Components.PasscodeView r3 = r11.passcodeView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8)
            r0.addView(r3, r5)
            r11.checkCurrentAccount()
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r5 = new java.lang.Object[r1]
            r5[r2] = r11
            r0.postNotificationName(r3, r5)
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getConnectionState()
            r11.currentConnectionState = r0
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.needShowAlert
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.reloadInterface
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r0.addObserver(r11, r3)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r0.addObserver(r11, r3)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x04f8
            int r0 = r11.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x040a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            org.telegram.ui.LoginActivity r3 = new org.telegram.ui.LoginActivity
            r3.<init>()
            r0.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r2)
            goto L_0x041e
        L_0x040a:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r11.sideMenu
            r0.setSideMenu(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            r3.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r11.drawerLayoutContainer
            r0.setAllowOpenDrawer(r1, r2)
        L_0x041e:
            if (r12 == 0) goto L_0x055f
            java.lang.String r0 = "fragment"
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x055f
            java.lang.String r3 = "args"
            android.os.Bundle r3 = r12.getBundle(r3)     // Catch:{ Exception -> 0x04f3 }
            int r4 = r0.hashCode()     // Catch:{ Exception -> 0x04f3 }
            r5 = 3
            r7 = 2
            switch(r4) {
                case -1529105743: goto L_0x046a;
                case -1349522494: goto L_0x0460;
                case 3052376: goto L_0x0456;
                case 98629247: goto L_0x044c;
                case 738950403: goto L_0x0442;
                case 1434631203: goto L_0x0438;
                default: goto L_0x0437;
            }     // Catch:{ Exception -> 0x04f3 }
        L_0x0437:
            goto L_0x0475
        L_0x0438:
            java.lang.String r4 = "settings"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 1
            goto L_0x0476
        L_0x0442:
            java.lang.String r4 = "channel"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 3
            goto L_0x0476
        L_0x044c:
            java.lang.String r4 = "group"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 2
            goto L_0x0476
        L_0x0456:
            java.lang.String r4 = "chat"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 0
            goto L_0x0476
        L_0x0460:
            java.lang.String r4 = "chat_profile"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 4
            goto L_0x0476
        L_0x046a:
            java.lang.String r4 = "wallpapers"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x04f3 }
            if (r0 == 0) goto L_0x0475
            r0 = 5
            goto L_0x0476
        L_0x0475:
            r0 = -1
        L_0x0476:
            if (r0 == 0) goto L_0x04e0
            if (r0 == r1) goto L_0x04d1
            if (r0 == r7) goto L_0x04bd
            if (r0 == r5) goto L_0x04a9
            r4 = 4
            if (r0 == r4) goto L_0x0495
            r3 = 5
            if (r0 == r3) goto L_0x0486
            goto L_0x055f
        L_0x0486:
            org.telegram.ui.WallpapersListActivity r0 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x0495:
            if (r3 == 0) goto L_0x055f
            org.telegram.ui.ProfileActivity r0 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            boolean r3 = r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            if (r3 == 0) goto L_0x055f
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x04a9:
            if (r3 == 0) goto L_0x055f
            org.telegram.ui.ChannelCreateActivity r0 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            boolean r3 = r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            if (r3 == 0) goto L_0x055f
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x04bd:
            if (r3 == 0) goto L_0x055f
            org.telegram.ui.GroupCreateFinalActivity r0 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            boolean r3 = r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            if (r3 == 0) goto L_0x055f
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x04d1:
            org.telegram.ui.SettingsActivity r0 = new org.telegram.ui.SettingsActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>()     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x04e0:
            if (r3 == 0) goto L_0x055f
            org.telegram.ui.ChatActivity r0 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04f3 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x04f3 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout     // Catch:{ Exception -> 0x04f3 }
            boolean r3 = r3.addFragmentToStack(r0)     // Catch:{ Exception -> 0x04f3 }
            if (r3 == 0) goto L_0x055f
            r0.restoreSelfArgs(r12)     // Catch:{ Exception -> 0x04f3 }
            goto L_0x055f
        L_0x04f3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x055f
        L_0x04f8:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r3 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x050d
            org.telegram.ui.DialogsActivity r0 = (org.telegram.ui.DialogsActivity) r0
            org.telegram.ui.Components.RecyclerListView r3 = r11.sideMenu
            r0.setSideMenu(r3)
        L_0x050d:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0542
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 > r1) goto L_0x0529
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0529
            r0 = 1
            goto L_0x052a
        L_0x0529:
            r0 = 0
        L_0x052a:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r1) goto L_0x0543
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r2)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 == 0) goto L_0x0543
            r0 = 0
            goto L_0x0543
        L_0x0542:
            r0 = 1
        L_0x0543:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r1) goto L_0x055a
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r11.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r2)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 == 0) goto L_0x055a
            r0 = 0
        L_0x055a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r11.drawerLayoutContainer
            r3.setAllowOpenDrawer(r0, r2)
        L_0x055f:
            r11.checkLayout()
            r11.checkSystemBarColors()
            android.content.Intent r0 = r11.getIntent()
            if (r12 == 0) goto L_0x056d
            r12 = 1
            goto L_0x056e
        L_0x056d:
            r12 = 0
        L_0x056e:
            r11.handleIntent(r0, r2, r12, r2)
            java.lang.String r12 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05d2 }
            java.lang.String r0 = android.os.Build.USER     // Catch:{ Exception -> 0x05d2 }
            java.lang.String r2 = ""
            if (r12 == 0) goto L_0x057e
            java.lang.String r12 = r12.toLowerCase()     // Catch:{ Exception -> 0x05d2 }
            goto L_0x057f
        L_0x057e:
            r12 = r2
        L_0x057f:
            if (r0 == 0) goto L_0x0585
            java.lang.String r2 = r12.toLowerCase()     // Catch:{ Exception -> 0x05d2 }
        L_0x0585:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05d2 }
            if (r0 == 0) goto L_0x05a5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05d2 }
            r0.<init>()     // Catch:{ Exception -> 0x05d2 }
            java.lang.String r3 = "OS name "
            r0.append(r3)     // Catch:{ Exception -> 0x05d2 }
            r0.append(r12)     // Catch:{ Exception -> 0x05d2 }
            java.lang.String r3 = " "
            r0.append(r3)     // Catch:{ Exception -> 0x05d2 }
            r0.append(r2)     // Catch:{ Exception -> 0x05d2 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05d2 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x05d2 }
        L_0x05a5:
            java.lang.String r0 = "flyme"
            boolean r12 = r12.contains(r0)     // Catch:{ Exception -> 0x05d2 }
            if (r12 != 0) goto L_0x05b5
            java.lang.String r12 = "flyme"
            boolean r12 = r2.contains(r12)     // Catch:{ Exception -> 0x05d2 }
            if (r12 == 0) goto L_0x05d6
        L_0x05b5:
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r1     // Catch:{ Exception -> 0x05d2 }
            android.view.Window r12 = r11.getWindow()     // Catch:{ Exception -> 0x05d2 }
            android.view.View r12 = r12.getDecorView()     // Catch:{ Exception -> 0x05d2 }
            android.view.View r12 = r12.getRootView()     // Catch:{ Exception -> 0x05d2 }
            android.view.ViewTreeObserver r0 = r12.getViewTreeObserver()     // Catch:{ Exception -> 0x05d2 }
            org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0     // Catch:{ Exception -> 0x05d2 }
            r2.<init>(r12)     // Catch:{ Exception -> 0x05d2 }
            r11.onGlobalLayoutListener = r2     // Catch:{ Exception -> 0x05d2 }
            r0.addOnGlobalLayoutListener(r2)     // Catch:{ Exception -> 0x05d2 }
            goto L_0x05d6
        L_0x05d2:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x05d6:
            org.telegram.messenger.MediaController r12 = org.telegram.messenger.MediaController.getInstance()
            r12.setBaseActivity(r11, r1)
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
            drawerLayoutAdapter2.setAccountsShowed(!drawerLayoutAdapter2.isAccountsShowed(), true);
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
                lambda$runLinkRequest$32$LaunchActivity(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
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
            } else if (id == 12) {
                lambda$runLinkRequest$32$LaunchActivity(getCurrentWalletFragment((String) null));
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

    private BaseFragment getCurrentWalletFragment(String str) {
        UserConfig instance = UserConfig.getInstance(this.currentAccount);
        if (TextUtils.isEmpty(instance.tonEncryptedData)) {
            if (TextUtils.isEmpty(str)) {
                return new WalletCreateActivity(0);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletTonLinkNoWalletText", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("WalletTonLinkNoWalletCreateWallet", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.lambda$getCurrentWalletFragment$4$LaunchActivity(dialogInterface, i);
                }
            });
            builder.show();
            return null;
        } else if (!instance.tonCreationFinished) {
            WalletCreateActivity walletCreateActivity = new WalletCreateActivity(1);
            walletCreateActivity.setResumeCreation();
            return walletCreateActivity;
        } else if (!TextUtils.isEmpty(str)) {
            return new WalletActivity(str);
        } else {
            return new WalletActivity();
        }
    }

    public /* synthetic */ void lambda$getCurrentWalletFragment$4$LaunchActivity(DialogInterface dialogInterface, int i) {
        lambda$runLinkRequest$32$LaunchActivity(new WalletCreateActivity(0));
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
        this.currentAccount = UserConfig.selectedAccount;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowPlayServicesAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
        updateCurrentConnectionState(this.currentAccount);
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

    private void showUpdateActivity(int i, TLRPC.TL_help_appUpdate tL_help_appUpdate, boolean z) {
        if (this.blockingUpdateView == null) {
            this.blockingUpdateView = new BlockingUpdateView(this) {
                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (i == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.drawerLayoutContainer.addView(this.blockingUpdateView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(i, tL_help_appUpdate, z);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int i, TLRPC.TL_help_termsOfService tL_help_termsOfService) {
        if (this.termsOfServiceView == null) {
            this.termsOfServiceView = new TermsOfServiceView(this);
            this.termsOfServiceView.setAlpha(0.0f);
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
                            LaunchActivity.AnonymousClass4.this.lambda$onAcceptTerms$0$LaunchActivity$4();
                        }
                    }).start();
                }

                public /* synthetic */ void lambda$onAcceptTerms$0$LaunchActivity$4() {
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }

                public void onDeclineTerms(int i) {
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }
            });
        }
        TLRPC.TL_help_termsOfService tL_help_termsOfService2 = UserConfig.getInstance(i).unacceptedTermsOfService;
        if (tL_help_termsOfService2 != tL_help_termsOfService && (tL_help_termsOfService2 == null || !tL_help_termsOfService2.id.data.equals(tL_help_termsOfService.id.data))) {
            UserConfig.getInstance(i).unacceptedTermsOfService = tL_help_termsOfService;
            UserConfig.getInstance(i).saveConfig(false);
        }
        this.termsOfServiceView.show(i, tL_help_termsOfService);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r4v30 */
    /* JADX WARNING: type inference failed for: r3v11 */
    /* JADX WARNING: type inference failed for: r37v0 */
    /* JADX WARNING: type inference failed for: r37v1 */
    /* JADX WARNING: type inference failed for: r37v2 */
    /* JADX WARNING: type inference failed for: r37v3 */
    /* JADX WARNING: type inference failed for: r37v4 */
    /* JADX WARNING: type inference failed for: r37v5 */
    /* JADX WARNING: type inference failed for: r4v309 */
    /* JADX WARNING: type inference failed for: r3v48 */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0230, code lost:
        if (r15.sendingText == null) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0dc0, code lost:
        if (r1.intValue() == 0) goto L_0x0dc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x1132, code lost:
        if (r4.checkCanOpenChat(r0, r5.get(r5.size() - 1)) != false) goto L_0x1136;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x1145, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x1147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x1149, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x117e, code lost:
        if (r0.checkCanOpenChat(r5, r4.get(r4.size() - 1)) != false) goto L_0x1180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x1191, code lost:
        if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r5), false, true, true, false) != false) goto L_0x1147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1394, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x13b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x13b0, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x13b2;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r3v2, types: [int, boolean] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:638:0x0var_ */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x033e  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03b1 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x067c A[SYNTHETIC, Splitter:B:302:0x067c] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0b0d  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0e59  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x0ed1  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0ed8  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0f6b A[Catch:{ all -> 0x0var_, all -> 0x0var_, Exception -> 0x0f6f }] */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x13d0  */
    /* JADX WARNING: Removed duplicated region for block: B:810:0x13d8  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x141e  */
    /* JADX WARNING: Removed duplicated region for block: B:825:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x14cf  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r48, boolean r49, boolean r50, boolean r51) {
        /*
            r47 = this;
            r15 = r47
            r14 = r48
            r0 = r50
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r47, r48)
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
            java.lang.String r1 = r48.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r48.getFlags()
            int[] r11 = new int[r13]
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r3 = "currentAccount"
            int r2 = r14.getIntExtra(r3, r2)
            r11[r12] = r2
            r2 = r11[r12]
            r15.switchToAccount(r2, r13)
            if (r51 != 0) goto L_0x007f
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r2 != 0) goto L_0x006a
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x007f
        L_0x006a:
            r47.showPasscodeActivity()
            r15.passcodeSaveIntent = r14
            r10 = r49
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r12)
            return r12
        L_0x007f:
            r10 = r49
            boolean r2 = org.telegram.messenger.SharedConfig.directShare
            java.lang.String r3 = "hash"
            r8 = 0
            if (r2 == 0) goto L_0x00ad
            if (r14 == 0) goto L_0x00ad
            android.os.Bundle r2 = r48.getExtras()
            if (r2 == 0) goto L_0x00ad
            android.os.Bundle r2 = r48.getExtras()
            java.lang.String r4 = "dialogId"
            long r4 = r2.getLong(r4, r8)
            android.os.Bundle r2 = r48.getExtras()
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
            r6 = 2
            r5 = 3
            if (r1 != 0) goto L_0x10e1
            if (r14 == 0) goto L_0x10e1
            java.lang.String r1 = r48.getAction()
            if (r1 == 0) goto L_0x10e1
            if (r0 != 0) goto L_0x10e1
            java.lang.String r0 = r48.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r2 = ""
            if (r0 == 0) goto L_0x0242
            java.lang.String r0 = r48.getType()
            if (r0 == 0) goto L_0x012d
            java.lang.String r3 = "text/x-vcard"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x012d
            android.os.Bundle r1 = r48.getExtras()     // Catch:{ Exception -> 0x0126 }
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0126 }
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x0126 }
            if (r1 == 0) goto L_0x012a
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0126 }
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r7, r7)     // Catch:{ Exception -> 0x0126 }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0126 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r15.contactsToSend     // Catch:{ Exception -> 0x0126 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0126 }
            r3 = 5
            if (r2 <= r3) goto L_0x0122
            r15.contactsToSend = r7     // Catch:{ Exception -> 0x0126 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0126 }
            r2.<init>()     // Catch:{ Exception -> 0x0126 }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x0126 }
            java.util.ArrayList<android.net.Uri> r2 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0126 }
            r2.add(r1)     // Catch:{ Exception -> 0x0126 }
            r15.documentsMimeType = r0     // Catch:{ Exception -> 0x0126 }
            goto L_0x0234
        L_0x0122:
            r15.contactsToSendUri = r1     // Catch:{ Exception -> 0x0126 }
            goto L_0x0234
        L_0x0126:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x012a:
            r0 = 1
            goto L_0x0235
        L_0x012d:
            java.lang.String r3 = "android.intent.extra.TEXT"
            java.lang.String r3 = r14.getStringExtra(r3)
            if (r3 != 0) goto L_0x0141
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x0141
            java.lang.String r3 = r4.toString()
        L_0x0141:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r16 = android.text.TextUtils.isEmpty(r3)
            if (r16 != 0) goto L_0x0178
            java.lang.String r8 = "http://"
            boolean r8 = r3.startsWith(r8)
            if (r8 != 0) goto L_0x015d
            java.lang.String r8 = "https://"
            boolean r8 = r3.startsWith(r8)
            if (r8 == 0) goto L_0x0175
        L_0x015d:
            boolean r8 = android.text.TextUtils.isEmpty(r4)
            if (r8 != 0) goto L_0x0175
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            r8.append(r1)
            r8.append(r3)
            java.lang.String r3 = r8.toString()
        L_0x0175:
            r15.sendingText = r3
            goto L_0x0180
        L_0x0178:
            boolean r1 = android.text.TextUtils.isEmpty(r4)
            if (r1 != 0) goto L_0x0180
            r15.sendingText = r4
        L_0x0180:
            java.lang.String r1 = "android.intent.extra.STREAM"
            android.os.Parcelable r1 = r14.getParcelableExtra(r1)
            if (r1 == 0) goto L_0x022e
            boolean r3 = r1 instanceof android.net.Uri
            if (r3 != 0) goto L_0x0194
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
        L_0x0194:
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x01a0
            boolean r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1)
            if (r3 == 0) goto L_0x01a0
            r3 = 1
            goto L_0x01a1
        L_0x01a0:
            r3 = 0
        L_0x01a1:
            if (r3 != 0) goto L_0x022c
            if (r1 == 0) goto L_0x01d7
            if (r0 == 0) goto L_0x01af
            java.lang.String r4 = "image/"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x01bf
        L_0x01af:
            java.lang.String r4 = r1.toString()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r8 = ".jpg"
            boolean r4 = r4.endsWith(r8)
            if (r4 == 0) goto L_0x01d7
        L_0x01bf:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x01ca
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x01ca:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x022c
        L_0x01d7:
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r4 == 0) goto L_0x021a
            java.lang.String r8 = "file:"
            boolean r8 = r4.startsWith(r8)
            if (r8 == 0) goto L_0x01eb
            java.lang.String r8 = "file://"
            java.lang.String r4 = r4.replace(r8, r2)
        L_0x01eb:
            if (r0 == 0) goto L_0x01f9
            java.lang.String r2 = "video/"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x01f9
            r15.videoPath = r4
            goto L_0x022c
        L_0x01f9:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            if (r0 != 0) goto L_0x020b
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsPathsArray = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsOriginalPathsArray = r0
        L_0x020b:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray
            r0.add(r4)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x022c
        L_0x021a:
            java.util.ArrayList<android.net.Uri> r2 = r15.documentsUrisArray
            if (r2 != 0) goto L_0x0225
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsUrisArray = r2
        L_0x0225:
            java.util.ArrayList<android.net.Uri> r2 = r15.documentsUrisArray
            r2.add(r1)
            r15.documentsMimeType = r0
        L_0x022c:
            r0 = r3
            goto L_0x0235
        L_0x022e:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0234
            goto L_0x012a
        L_0x0234:
            r0 = 0
        L_0x0235:
            if (r0 == 0) goto L_0x0347
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
            goto L_0x0347
        L_0x0242:
            java.lang.String r0 = r48.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x034f
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x0337 }
            java.lang.String r1 = r48.getType()     // Catch:{ Exception -> 0x0337 }
            if (r0 == 0) goto L_0x028b
            r3 = 0
        L_0x025b:
            int r4 = r0.size()     // Catch:{ Exception -> 0x0337 }
            if (r3 >= r4) goto L_0x0284
            java.lang.Object r4 = r0.get(r3)     // Catch:{ Exception -> 0x0337 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x0337 }
            boolean r8 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x0337 }
            if (r8 != 0) goto L_0x0273
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0337 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0337 }
        L_0x0273:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x0337 }
            if (r4 == 0) goto L_0x0282
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r4)     // Catch:{ Exception -> 0x0337 }
            if (r4 == 0) goto L_0x0282
            r0.remove(r3)     // Catch:{ Exception -> 0x0337 }
            int r3 = r3 + -1
        L_0x0282:
            int r3 = r3 + r13
            goto L_0x025b
        L_0x0284:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x0337 }
            if (r3 == 0) goto L_0x028b
            r0 = r7
        L_0x028b:
            if (r0 == 0) goto L_0x033b
            if (r1 == 0) goto L_0x02cc
            java.lang.String r3 = "image/"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x0337 }
            if (r3 == 0) goto L_0x02cc
            r1 = 0
        L_0x0298:
            int r2 = r0.size()     // Catch:{ Exception -> 0x0337 }
            if (r1 >= r2) goto L_0x0335
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x0337 }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x0337 }
            boolean r3 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x0337 }
            if (r3 != 0) goto L_0x02b0
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0337 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0337 }
        L_0x02b0:
            android.net.Uri r2 = (android.net.Uri) r2     // Catch:{ Exception -> 0x0337 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x0337 }
            if (r3 != 0) goto L_0x02bd
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0337 }
            r3.<init>()     // Catch:{ Exception -> 0x0337 }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x0337 }
        L_0x02bd:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x0337 }
            r3.<init>()     // Catch:{ Exception -> 0x0337 }
            r3.uri = r2     // Catch:{ Exception -> 0x0337 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray     // Catch:{ Exception -> 0x0337 }
            r2.add(r3)     // Catch:{ Exception -> 0x0337 }
            int r1 = r1 + 1
            goto L_0x0298
        L_0x02cc:
            r3 = 0
        L_0x02cd:
            int r4 = r0.size()     // Catch:{ Exception -> 0x0337 }
            if (r3 >= r4) goto L_0x0335
            java.lang.Object r4 = r0.get(r3)     // Catch:{ Exception -> 0x0337 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x0337 }
            boolean r8 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x0337 }
            if (r8 != 0) goto L_0x02e5
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0337 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0337 }
        L_0x02e5:
            r8 = r4
            android.net.Uri r8 = (android.net.Uri) r8     // Catch:{ Exception -> 0x0337 }
            java.lang.String r9 = org.telegram.messenger.AndroidUtilities.getPath(r8)     // Catch:{ Exception -> 0x0337 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0337 }
            if (r4 != 0) goto L_0x02f3
            r4 = r9
        L_0x02f3:
            if (r9 == 0) goto L_0x0320
            java.lang.String r8 = "file:"
            boolean r8 = r9.startsWith(r8)     // Catch:{ Exception -> 0x0337 }
            if (r8 == 0) goto L_0x0303
            java.lang.String r8 = "file://"
            java.lang.String r9 = r9.replace(r8, r2)     // Catch:{ Exception -> 0x0337 }
        L_0x0303:
            java.util.ArrayList<java.lang.String> r8 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0337 }
            if (r8 != 0) goto L_0x0315
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0337 }
            r8.<init>()     // Catch:{ Exception -> 0x0337 }
            r15.documentsPathsArray = r8     // Catch:{ Exception -> 0x0337 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0337 }
            r8.<init>()     // Catch:{ Exception -> 0x0337 }
            r15.documentsOriginalPathsArray = r8     // Catch:{ Exception -> 0x0337 }
        L_0x0315:
            java.util.ArrayList<java.lang.String> r8 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0337 }
            r8.add(r9)     // Catch:{ Exception -> 0x0337 }
            java.util.ArrayList<java.lang.String> r8 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x0337 }
            r8.add(r4)     // Catch:{ Exception -> 0x0337 }
            goto L_0x0332
        L_0x0320:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0337 }
            if (r4 != 0) goto L_0x032b
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x0337 }
            r4.<init>()     // Catch:{ Exception -> 0x0337 }
            r15.documentsUrisArray = r4     // Catch:{ Exception -> 0x0337 }
        L_0x032b:
            java.util.ArrayList<android.net.Uri> r4 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0337 }
            r4.add(r8)     // Catch:{ Exception -> 0x0337 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x0337 }
        L_0x0332:
            int r3 = r3 + 1
            goto L_0x02cd
        L_0x0335:
            r0 = 0
            goto L_0x033c
        L_0x0337:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x033b:
            r0 = 1
        L_0x033c:
            if (r0 == 0) goto L_0x0347
            java.lang.String r0 = "Unsupported content"
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r12)
            r0.show()
        L_0x0347:
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
            r31 = 0
            goto L_0x10e7
        L_0x034f:
            java.lang.String r0 = r48.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x1012
            android.net.Uri r0 = r48.getData()
            if (r0 == 0) goto L_0x0ff7
            java.lang.String r4 = r0.getScheme()
            if (r4 == 0) goto L_0x0e9a
            int r9 = r4.hashCode()
            r8 = 3699(0xe73, float:5.183E-42)
            if (r9 == r8) goto L_0x039e
            r8 = 115027(0x1CLASSNAME, float:1.61187E-40)
            if (r9 == r8) goto L_0x0393
            r8 = 3213448(0x310888, float:4.503E-39)
            if (r9 == r8) goto L_0x0389
            r8 = 99617003(0x5var_eb, float:2.2572767E-35)
            if (r9 == r8) goto L_0x037f
            goto L_0x03a9
        L_0x037f:
            java.lang.String r8 = "https"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03a9
            r4 = 1
            goto L_0x03aa
        L_0x0389:
            java.lang.String r8 = "http"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03a9
            r4 = 0
            goto L_0x03aa
        L_0x0393:
            java.lang.String r8 = "ton"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03a9
            r4 = 2
            goto L_0x03aa
        L_0x039e:
            java.lang.String r8 = "tg"
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x03a9
            r4 = 3
            goto L_0x03aa
        L_0x03a9:
            r4 = -1
        L_0x03aa:
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r8 = 16
            r7 = 6
            if (r4 == 0) goto L_0x0ae1
            if (r4 == r13) goto L_0x0ae1
            if (r4 == r6) goto L_0x0a76
            if (r4 == r5) goto L_0x03b9
            goto L_0x0e9a
        L_0x03b9:
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "tg:resolve"
            boolean r4 = r0.startsWith(r4)
            java.lang.String r6 = "scope"
            java.lang.String r5 = "tg://telegram.org"
            if (r4 != 0) goto L_0x0980
            java.lang.String r4 = "tg://resolve"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x03d6
            goto L_0x0980
        L_0x03d6:
            java.lang.String r4 = "tg:privatepost"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0928
            java.lang.String r4 = "tg://privatepost"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x03ea
            goto L_0x0928
        L_0x03ea:
            java.lang.String r4 = "tg:bg"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x07d7
            java.lang.String r4 = "tg://bg"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x03fe
            goto L_0x07d7
        L_0x03fe:
            java.lang.String r4 = "tg:join"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x07b8
            java.lang.String r4 = "tg://join"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x0412
            goto L_0x07b8
        L_0x0412:
            java.lang.String r4 = "tg:addstickers"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0796
            java.lang.String r4 = "tg://addstickers"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x0426
            goto L_0x0796
        L_0x0426:
            java.lang.String r4 = "tg:msg"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0708
            java.lang.String r4 = "tg://msg"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0708
            java.lang.String r4 = "tg://share"
            boolean r4 = r0.startsWith(r4)
            if (r4 != 0) goto L_0x0708
            java.lang.String r4 = "tg:share"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x044c
            goto L_0x0708
        L_0x044c:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x06e4
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0460
            goto L_0x06e4
        L_0x0460:
            java.lang.String r1 = "tg:login"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x06ab
            java.lang.String r1 = "tg://login"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0474
            goto L_0x06ab
        L_0x0474:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0643
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0488
            goto L_0x0643
        L_0x0488:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05cc
            java.lang.String r1 = "tg://passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05cc
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04a5
            goto L_0x05cc
        L_0x04a5:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x05a1
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04b9
            goto L_0x05a1
        L_0x04b9:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0572
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04cd
            goto L_0x0572
        L_0x04cd:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x050b
            java.lang.String r1 = "tg://settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x04e0
            goto L_0x050b
        L_0x04e0:
            java.lang.String r1 = "tg://"
            java.lang.String r0 = r0.replace(r1, r2)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r2)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x04fa
            java.lang.String r0 = r0.substring(r12, r1)
        L_0x04fa:
            r24 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            goto L_0x0ea8
        L_0x050b:
            java.lang.String r1 = "themes"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x053f
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r37 = 2
            goto L_0x0ec2
        L_0x053f:
            java.lang.String r1 = "devices"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x0ade
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r37 = 3
            goto L_0x0ec2
        L_0x0572:
            java.lang.String r1 = "tg:addtheme"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r27 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            goto L_0x0eae
        L_0x05a1:
            java.lang.String r1 = "tg:setlanguage"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r25 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            goto L_0x0eaa
        L_0x05cc:
            java.lang.String r1 = "tg:passport"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r6)
            boolean r4 = android.text.TextUtils.isEmpty(r2)
            if (r4 != 0) goto L_0x0612
            java.lang.String r4 = "{"
            boolean r4 = r2.startsWith(r4)
            if (r4 == 0) goto L_0x0612
            java.lang.String r4 = "}"
            boolean r4 = r2.endsWith(r4)
            if (r4 == 0) goto L_0x0612
            java.lang.String r4 = "nonce"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "nonce"
            r1.put(r5, r4)
            goto L_0x061d
        L_0x0612:
            java.lang.String r4 = "payload"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "payload"
            r1.put(r5, r4)
        L_0x061d:
            java.lang.String r4 = "bot_id"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "bot_id"
            r1.put(r5, r4)
            r1.put(r6, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "public_key"
            r1.put(r4, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            goto L_0x09fc
        L_0x0643:
            java.lang.String r1 = "tg:openmessage"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "chat_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "message_id"
            java.lang.String r0 = r0.getQueryParameter(r4)
            if (r1 == 0) goto L_0x066f
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0678 }
            goto L_0x0679
        L_0x066f:
            if (r2 == 0) goto L_0x0678
            int r1 = java.lang.Integer.parseInt(r2)     // Catch:{ NumberFormatException -> 0x0678 }
            r2 = r1
            r1 = 0
            goto L_0x067a
        L_0x0678:
            r1 = 0
        L_0x0679:
            r2 = 0
        L_0x067a:
            if (r0 == 0) goto L_0x0681
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0681 }
            goto L_0x0682
        L_0x0681:
            r0 = 0
        L_0x0682:
            r36 = r0
            r34 = r1
            r35 = r2
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            goto L_0x0ec0
        L_0x06ab:
            java.lang.String r1 = "tg:login"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "code"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r29 = r1
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            goto L_0x0eb2
        L_0x06e4:
            java.lang.String r1 = "tg:confirmphone"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "phone"
            java.lang.String r7 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r3)
            r1 = r0
            r0 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            goto L_0x0ea0
        L_0x0708:
            java.lang.String r4 = "tg:msg"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg://msg"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg://share"
            java.lang.String r0 = r0.replace(r4, r5)
            java.lang.String r4 = "tg:share"
            java.lang.String r0 = r0.replace(r4, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0732
            goto L_0x0733
        L_0x0732:
            r2 = r4
        L_0x0733:
            java.lang.String r4 = "text"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 == 0) goto L_0x0769
            int r4 = r2.length()
            if (r4 <= 0) goto L_0x0752
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r1)
            java.lang.String r2 = r4.toString()
            r4 = 1
            goto L_0x0753
        L_0x0752:
            r4 = 0
        L_0x0753:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r2 = "text"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r5.append(r0)
            java.lang.String r2 = r5.toString()
            goto L_0x076a
        L_0x0769:
            r4 = 0
        L_0x076a:
            int r0 = r2.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r5) goto L_0x077a
            r0 = 16384(0x4000, float:2.2959E-41)
            java.lang.String r0 = r2.substring(r12, r0)
            r7 = r0
            goto L_0x077b
        L_0x077a:
            r7 = r2
        L_0x077b:
            boolean r0 = r7.endsWith(r1)
            if (r0 == 0) goto L_0x078b
            int r0 = r7.length()
            int r0 = r0 - r13
            java.lang.String r7 = r7.substring(r12, r0)
            goto L_0x077b
        L_0x078b:
            r9 = r4
            r2 = r7
            r0 = 0
            r1 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            goto L_0x0ea2
        L_0x0796:
            java.lang.String r1 = "tg:addstickers"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r8 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            goto L_0x0ea1
        L_0x07b8:
            java.lang.String r1 = "tg:join"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r5 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            goto L_0x097d
        L_0x07d7:
            java.lang.String r1 = "tg:bg"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "slug"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
            java.lang.String r2 = r1.slug
            if (r2 != 0) goto L_0x0809
            java.lang.String r2 = "color"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
        L_0x0809:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x0823
            int r2 = r2.length()
            if (r2 != r7) goto L_0x0823
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x081e }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x081e }
            int r2 = java.lang.Integer.parseInt(r2, r8)     // Catch:{ Exception -> 0x081e }
            r2 = r2 | r9
            r0.background_color = r2     // Catch:{ Exception -> 0x081e }
        L_0x081e:
            r2 = 0
            r1.slug = r2
            goto L_0x0913
        L_0x0823:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x087b
            int r2 = r2.length()
            r4 = 13
            if (r2 != r4) goto L_0x087b
            java.lang.String r2 = r1.slug
            char r2 = r2.charAt(r7)
            r4 = 45
            if (r2 != r4) goto L_0x087b
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x085e }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x085e }
            java.lang.String r4 = r4.substring(r12, r7)     // Catch:{ Exception -> 0x085e }
            int r4 = java.lang.Integer.parseInt(r4, r8)     // Catch:{ Exception -> 0x085e }
            r4 = r4 | r9
            r2.background_color = r4     // Catch:{ Exception -> 0x085e }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x085e }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x085e }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x085e }
            int r4 = java.lang.Integer.parseInt(r4, r8)     // Catch:{ Exception -> 0x085e }
            r4 = r4 | r9
            r2.second_background_color = r4     // Catch:{ Exception -> 0x085e }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x085e }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x085e }
        L_0x085e:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0876 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0876 }
            if (r2 != 0) goto L_0x0876
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0876 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0876 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0876 }
            r2.rotation = r0     // Catch:{ Exception -> 0x0876 }
        L_0x0876:
            r2 = 0
            r1.slug = r2
            goto L_0x0913
        L_0x087b:
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x08b6
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r2 = r2.split(r4)
            if (r2 == 0) goto L_0x08b6
            int r4 = r2.length
            if (r4 <= 0) goto L_0x08b6
            r4 = 0
        L_0x0893:
            int r5 = r2.length
            if (r4 >= r5) goto L_0x08b6
            r5 = r2[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x08a5
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.blur = r13
            goto L_0x08b3
        L_0x08a5:
            r5 = r2[r4]
            java.lang.String r6 = "motion"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x08b3
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r13
        L_0x08b3:
            int r4 = r4 + 1
            goto L_0x0893
        L_0x08b6:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r2.intensity = r4
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x08fb }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x08fb }
            if (r4 != 0) goto L_0x08fb
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x08fb }
            java.lang.String r5 = r2.substring(r12, r7)     // Catch:{ Exception -> 0x08fb }
            int r5 = java.lang.Integer.parseInt(r5, r8)     // Catch:{ Exception -> 0x08fb }
            r5 = r5 | r9
            r4.background_color = r5     // Catch:{ Exception -> 0x08fb }
            int r4 = r2.length()     // Catch:{ Exception -> 0x08fb }
            if (r4 <= r7) goto L_0x08fb
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x08fb }
            r5 = 7
            java.lang.String r2 = r2.substring(r5)     // Catch:{ Exception -> 0x08fb }
            int r2 = java.lang.Integer.parseInt(r2, r8)     // Catch:{ Exception -> 0x08fb }
            r2 = r2 | r9
            r4.second_background_color = r2     // Catch:{ Exception -> 0x08fb }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x08fb }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x08fb }
        L_0x08fb:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0913 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0913 }
            if (r2 != 0) goto L_0x0913
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0913 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0913 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0913 }
            r2.rotation = r0     // Catch:{ Exception -> 0x0913 }
        L_0x0913:
            r26 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            goto L_0x0eac
        L_0x0928:
            java.lang.String r1 = "tg:privatepost"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r2 = "channel"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r2 = r1.intValue()
            if (r2 == 0) goto L_0x0978
            int r2 = r0.intValue()
            if (r2 != 0) goto L_0x095b
            goto L_0x0978
        L_0x095b:
            r28 = r0
            r30 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            goto L_0x0eb4
        L_0x0978:
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x097d:
            r6 = 2
            goto L_0x0e9f
        L_0x0980:
            java.lang.String r1 = "tg:resolve"
            java.lang.String r0 = r0.replace(r1, r5)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r5)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "telegrampassport"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0a0b
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r6)
            boolean r4 = android.text.TextUtils.isEmpty(r2)
            if (r4 != 0) goto L_0x09cd
            java.lang.String r4 = "{"
            boolean r4 = r2.startsWith(r4)
            if (r4 == 0) goto L_0x09cd
            java.lang.String r4 = "}"
            boolean r4 = r2.endsWith(r4)
            if (r4 == 0) goto L_0x09cd
            java.lang.String r4 = "nonce"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "nonce"
            r1.put(r5, r4)
            goto L_0x09d8
        L_0x09cd:
            java.lang.String r4 = "payload"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "payload"
            r1.put(r5, r4)
        L_0x09d8:
            java.lang.String r4 = "bot_id"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "bot_id"
            r1.put(r5, r4)
            r1.put(r6, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "public_key"
            r1.put(r4, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
        L_0x09fc:
            r23 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            goto L_0x0ea6
        L_0x0a0b:
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
            if (r6 != 0) goto L_0x0a4b
            r31 = r2
            r32 = r4
            r18 = r5
            r0 = 0
            r2 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0a68
        L_0x0a4b:
            r30 = r0
            r31 = r2
            r32 = r4
            r18 = r5
            r0 = 0
            r2 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
        L_0x0a68:
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r4 = r1
            r1 = 0
            goto L_0x0ec2
        L_0x0a76:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 18
            if (r1 < r2) goto L_0x0ade
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            java.lang.String r1 = r1.walletConfig
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ade
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            java.lang.String r1 = r1.walletBlockchainName
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ade
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "ton:transfer"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0ab1
            java.lang.String r1 = "ton://transfer"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0aaf
            goto L_0x0ab1
        L_0x0aaf:
            r7 = 0
            goto L_0x0abb
        L_0x0ab1:
            java.lang.String r1 = "ton:transfer"
            java.lang.String r2 = "ton://transfer"
            java.lang.String r7 = r0.replace(r1, r2)
        L_0x0abb:
            r33 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0eba
        L_0x0ade:
            r6 = 2
            goto L_0x0e9a
        L_0x0ae1:
            java.lang.String r4 = r0.getHost()
            java.lang.String r4 = r4.toLowerCase()
            java.lang.String r5 = "telegram.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0b01
            java.lang.String r5 = "t.me"
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x0b01
            java.lang.String r5 = "telegram.dog"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0ade
        L_0x0b01:
            java.lang.String r4 = r0.getPath()
            if (r4 == 0) goto L_0x0e59
            int r5 = r4.length()
            if (r5 <= r13) goto L_0x0e59
            java.lang.String r4 = r4.substring(r13)
            java.lang.String r5 = "bg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "bg/"
            java.lang.String r2 = r4.replace(r5, r2)
            r1.slug = r2
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x0b47
            int r2 = r2.length()
            if (r2 != r7) goto L_0x0b47
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0b42 }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x0b42 }
            int r2 = java.lang.Integer.parseInt(r2, r8)     // Catch:{ Exception -> 0x0b42 }
            r2 = r2 | r9
            r0.background_color = r2     // Catch:{ Exception -> 0x0b42 }
        L_0x0b42:
            r2 = 0
            r1.slug = r2
            goto L_0x0c4c
        L_0x0b47:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x0b9f
            int r2 = r2.length()
            r4 = 13
            if (r2 != r4) goto L_0x0b9f
            java.lang.String r2 = r1.slug
            char r2 = r2.charAt(r7)
            r4 = 45
            if (r2 != r4) goto L_0x0b9f
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0b82 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0b82 }
            java.lang.String r4 = r4.substring(r12, r7)     // Catch:{ Exception -> 0x0b82 }
            int r4 = java.lang.Integer.parseInt(r4, r8)     // Catch:{ Exception -> 0x0b82 }
            r4 = r4 | r9
            r2.background_color = r4     // Catch:{ Exception -> 0x0b82 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0b82 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x0b82 }
            r5 = 7
            java.lang.String r4 = r4.substring(r5)     // Catch:{ Exception -> 0x0b82 }
            int r4 = java.lang.Integer.parseInt(r4, r8)     // Catch:{ Exception -> 0x0b82 }
            r4 = r4 | r9
            r2.second_background_color = r4     // Catch:{ Exception -> 0x0b82 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0b82 }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x0b82 }
        L_0x0b82:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0b9a }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0b9a }
            if (r2 != 0) goto L_0x0b9a
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0b9a }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0b9a }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0b9a }
            r2.rotation = r0     // Catch:{ Exception -> 0x0b9a }
        L_0x0b9a:
            r6 = 0
            r1.slug = r6
            goto L_0x0c4c
        L_0x0b9f:
            r6 = 0
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x0bdc
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r2 = r2.split(r4)
            if (r2 == 0) goto L_0x0bdc
            int r4 = r2.length
            if (r4 <= 0) goto L_0x0bdc
            r4 = 0
        L_0x0bb8:
            int r5 = r2.length
            if (r4 >= r5) goto L_0x0bdc
            r5 = r2[r4]
            java.lang.String r6 = "blur"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0bca
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.blur = r13
            goto L_0x0bd8
        L_0x0bca:
            r5 = r2[r4]
            java.lang.String r6 = "motion"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0bd8
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r5.motion = r13
        L_0x0bd8:
            int r4 = r4 + 1
            r6 = 0
            goto L_0x0bb8
        L_0x0bdc:
            java.lang.String r2 = "intensity"
            java.lang.String r2 = r0.getQueryParameter(r2)
            boolean r4 = android.text.TextUtils.isEmpty(r2)
            if (r4 != 0) goto L_0x0bf5
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r2 = r2.intValue()
            r4.intensity = r2
            goto L_0x0bfb
        L_0x0bf5:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            r4 = 50
            r2.intensity = r4
        L_0x0bfb:
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0CLASSNAME }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0CLASSNAME }
            if (r4 != 0) goto L_0x0c2f
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r5 = r2.substring(r12, r7)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r5 = java.lang.Integer.parseInt(r5, r8)     // Catch:{ Exception -> 0x0CLASSNAME }
            r5 = r5 | r9
            r4.background_color = r5     // Catch:{ Exception -> 0x0CLASSNAME }
            int r4 = r2.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            if (r4 <= r7) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            r5 = 7
            java.lang.String r2 = r2.substring(r5)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r2 = java.lang.Integer.parseInt(r2, r8)     // Catch:{ Exception -> 0x0CLASSNAME }
            r2 = r2 | r9
            r4.second_background_color = r2     // Catch:{ Exception -> 0x0CLASSNAME }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            r4 = 45
            r2.rotation = r4     // Catch:{ Exception -> 0x0CLASSNAME }
            goto L_0x0CLASSNAME
        L_0x0c2f:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0CLASSNAME }
            r4 = -1
            r2.background_color = r4     // Catch:{ Exception -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0c4c }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0c4c }
            if (r2 != 0) goto L_0x0c4c
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x0c4c }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0c4c }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0c4c }
            r2.rotation = r0     // Catch:{ Exception -> 0x0c4c }
        L_0x0c4c:
            r26 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r20 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            goto L_0x0e6e
        L_0x0CLASSNAME:
            java.lang.String r5 = "login/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "login/"
            java.lang.String r7 = r4.replace(r0, r2)
            r25 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r20 = 0
            r23 = 0
            r24 = 0
            goto L_0x0e6c
        L_0x0CLASSNAME:
            java.lang.String r5 = "joinchat/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0c9c
            java.lang.String r0 = "joinchat/"
            java.lang.String r7 = r4.replace(r0, r2)
            r0 = r7
            r1 = 0
        L_0x0CLASSNAME:
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            goto L_0x0e5f
        L_0x0c9c:
            java.lang.String r5 = "addstickers/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0cad
            java.lang.String r0 = "addstickers/"
            java.lang.String r7 = r4.replace(r0, r2)
            r1 = r7
            r0 = 0
            goto L_0x0CLASSNAME
        L_0x0cad:
            java.lang.String r5 = "msg/"
            boolean r5 = r4.startsWith(r5)
            if (r5 != 0) goto L_0x0dee
            java.lang.String r5 = "share/"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x0cbf
            goto L_0x0dee
        L_0x0cbf:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0ce0
            java.lang.String r1 = "phone"
            java.lang.String r7 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r3)
            r20 = r0
            r9 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r18 = 0
            goto L_0x0e66
        L_0x0ce0:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0cff
            r0 = 12
            java.lang.String r7 = r4.substring(r0)
            r23 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r20 = 0
            goto L_0x0e68
        L_0x0cff:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0d20
            r0 = 9
            java.lang.String r7 = r4.substring(r0)
            r24 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 2
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r20 = 0
            r23 = 0
            goto L_0x0e6a
        L_0x0d20:
            java.lang.String r1 = "c/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0d78
            java.util.List r0 = r0.getPathSegments()
            int r1 = r0.size()
            r2 = 3
            if (r1 != r2) goto L_0x0d5b
            java.lang.Object r1 = r0.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r1)
            r6 = 2
            java.lang.Object r0 = r0.get(r6)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            if (r1 == 0) goto L_0x0d5c
            int r1 = r7.intValue()
            if (r1 != 0) goto L_0x0d55
            goto L_0x0d5c
        L_0x0d55:
            r46 = r7
            r7 = r0
            r0 = r46
            goto L_0x0d5e
        L_0x0d5b:
            r6 = 2
        L_0x0d5c:
            r0 = 0
            r7 = 0
        L_0x0d5e:
            r28 = r0
            r27 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r18 = 0
            r20 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            goto L_0x0e72
        L_0x0d78:
            r6 = 2
            int r1 = r4.length()
            if (r1 < r13) goto L_0x0e5a
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r2 = r0.getPathSegments()
            r1.<init>(r2)
            int r2 = r1.size()
            if (r2 <= 0) goto L_0x0d9f
            java.lang.Object r2 = r1.get(r12)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r4 = "s"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0d9f
            r1.remove(r12)
        L_0x0d9f:
            int r2 = r1.size()
            if (r2 <= 0) goto L_0x0dc4
            java.lang.Object r2 = r1.get(r12)
            r7 = r2
            java.lang.String r7 = (java.lang.String) r7
            int r2 = r1.size()
            if (r2 <= r13) goto L_0x0dc2
            java.lang.Object r1 = r1.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r2 = r1.intValue()
            if (r2 != 0) goto L_0x0dc6
        L_0x0dc2:
            r1 = 0
            goto L_0x0dc6
        L_0x0dc4:
            r1 = 0
            r7 = 0
        L_0x0dc6:
            java.lang.String r2 = "start"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r4 = "startgroup"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r5 = "game"
            java.lang.String r0 = r0.getQueryParameter(r5)
            r18 = r0
            r27 = r1
            r5 = r4
            r0 = 0
            r1 = 0
            r4 = 0
            r8 = 0
            r9 = 0
            r20 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            goto L_0x0e70
        L_0x0dee:
            r6 = 2
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 != 0) goto L_0x0df9
            goto L_0x0dfa
        L_0x0df9:
            r2 = r4
        L_0x0dfa:
            java.lang.String r4 = "text"
            java.lang.String r4 = r0.getQueryParameter(r4)
            if (r4 == 0) goto L_0x0e30
            int r4 = r2.length()
            if (r4 <= 0) goto L_0x0e19
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r1)
            java.lang.String r2 = r4.toString()
            r4 = 1
            goto L_0x0e1a
        L_0x0e19:
            r4 = 0
        L_0x0e1a:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r2 = "text"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r5.append(r0)
            java.lang.String r2 = r5.toString()
            goto L_0x0e31
        L_0x0e30:
            r4 = 0
        L_0x0e31:
            int r0 = r2.length()
            r5 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r5) goto L_0x0e41
            r0 = 16384(0x4000, float:2.2959E-41)
            java.lang.String r0 = r2.substring(r12, r0)
            r7 = r0
            goto L_0x0e42
        L_0x0e41:
            r7 = r2
        L_0x0e42:
            boolean r0 = r7.endsWith(r1)
            if (r0 == 0) goto L_0x0e52
            int r0 = r7.length()
            int r0 = r0 - r13
            java.lang.String r7 = r7.substring(r12, r0)
            goto L_0x0e42
        L_0x0e52:
            r8 = r7
            r0 = 0
            r1 = 0
            r2 = 0
            r5 = 0
            r7 = 0
            goto L_0x0e61
        L_0x0e59:
            r6 = 2
        L_0x0e5a:
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x0e5f:
            r7 = 0
            r8 = 0
        L_0x0e61:
            r9 = 0
            r18 = 0
            r20 = 0
        L_0x0e66:
            r23 = 0
        L_0x0e68:
            r24 = 0
        L_0x0e6a:
            r25 = 0
        L_0x0e6c:
            r26 = 0
        L_0x0e6e:
            r27 = 0
        L_0x0e70:
            r28 = 0
        L_0x0e72:
            r31 = r2
            r32 = r5
            r2 = r8
            r30 = r27
            r29 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r5 = r0
            r8 = r1
            r1 = r20
            r27 = r24
            r0 = r25
            r24 = 0
            r25 = r23
            r23 = 0
            r46 = r9
            r9 = r4
            r4 = r7
            r7 = r46
            goto L_0x0ec2
        L_0x0e9a:
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x0e9f:
            r7 = 0
        L_0x0ea0:
            r8 = 0
        L_0x0ea1:
            r9 = 0
        L_0x0ea2:
            r18 = 0
            r23 = 0
        L_0x0ea6:
            r24 = 0
        L_0x0ea8:
            r25 = 0
        L_0x0eaa:
            r26 = 0
        L_0x0eac:
            r27 = 0
        L_0x0eae:
            r28 = 0
            r29 = 0
        L_0x0eb2:
            r30 = 0
        L_0x0eb4:
            r31 = 0
            r32 = 0
            r33 = 0
        L_0x0eba:
            r34 = 0
            r35 = 0
            r36 = 0
        L_0x0ec0:
            r37 = 0
        L_0x0ec2:
            if (r0 != 0) goto L_0x0ed8
            int r6 = r15.currentAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            boolean r6 = r6.isClientActivated()
            if (r6 == 0) goto L_0x0ed1
            goto L_0x0ed8
        L_0x0ed1:
            r44 = r11
            r2 = r15
            r31 = 0
            goto L_0x0ff0
        L_0x0ed8:
            if (r7 != 0) goto L_0x0fd5
            if (r1 == 0) goto L_0x0ede
            goto L_0x0fd5
        L_0x0ede:
            if (r4 != 0) goto L_0x0var_
            if (r5 != 0) goto L_0x0var_
            if (r8 != 0) goto L_0x0var_
            if (r2 != 0) goto L_0x0var_
            if (r18 != 0) goto L_0x0var_
            if (r23 != 0) goto L_0x0var_
            if (r24 != 0) goto L_0x0var_
            if (r25 != 0) goto L_0x0var_
            if (r0 != 0) goto L_0x0var_
            if (r26 != 0) goto L_0x0var_
            if (r28 != 0) goto L_0x0var_
            if (r27 != 0) goto L_0x0var_
            if (r29 == 0) goto L_0x0efa
            goto L_0x0var_
        L_0x0efa:
            android.content.ContentResolver r38 = r47.getContentResolver()     // Catch:{ Exception -> 0x0var_ }
            android.net.Uri r39 = r48.getData()     // Catch:{ Exception -> 0x0var_ }
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            android.database.Cursor r1 = r38.query(r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x0var_ }
            if (r1 == 0) goto L_0x0var_
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x0f5d }
            if (r0 == 0) goto L_0x0var_
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0f5d }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0f5d }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x0f5d }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0f5d }
            r2 = 0
            r6 = 3
        L_0x0f2a:
            if (r2 >= r6) goto L_0x0var_
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x0f5b }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x0f5b }
            if (r3 != r0) goto L_0x0f3e
            r11[r12] = r2     // Catch:{ all -> 0x0f5b }
            r0 = r11[r12]     // Catch:{ all -> 0x0f5b }
            r15.switchToAccount(r0, r13)     // Catch:{ all -> 0x0f5b }
            goto L_0x0var_
        L_0x0f3e:
            int r2 = r2 + 1
            goto L_0x0f2a
        L_0x0var_:
            java.lang.String r0 = "DATA4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0f5b }
            int r0 = r1.getInt(r0)     // Catch:{ all -> 0x0f5b }
            r2 = r11[r12]     // Catch:{ all -> 0x0f5b }
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)     // Catch:{ all -> 0x0f5b }
            int r3 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x0f5b }
            java.lang.Object[] r4 = new java.lang.Object[r12]     // Catch:{ all -> 0x0f5b }
            r2.postNotificationName(r3, r4)     // Catch:{ all -> 0x0f5b }
            r34 = r0
            goto L_0x0var_
        L_0x0f5b:
            r0 = move-exception
            goto L_0x0f5f
        L_0x0f5d:
            r0 = move-exception
            r6 = 3
        L_0x0f5f:
            throw r0     // Catch:{ all -> 0x0var_ }
        L_0x0var_:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x0var_
            r1.close()     // Catch:{ all -> 0x0var_ }
        L_0x0var_:
            throw r2     // Catch:{ Exception -> 0x0f6f }
        L_0x0var_:
            r6 = 3
        L_0x0var_:
            if (r1 == 0) goto L_0x0var_
            r1.close()     // Catch:{ Exception -> 0x0f6f }
            goto L_0x0var_
        L_0x0f6f:
            r0 = move-exception
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r6 = 3
        L_0x0var_:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0var_:
            r44 = r11
            r2 = r15
            r7 = r33
            r12 = r34
            r13 = r37
            r31 = 0
            goto L_0x1003
        L_0x0var_:
            r6 = 3
            if (r2 == 0) goto L_0x0fa2
            java.lang.String r1 = "@"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0fa2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = " "
            r1.append(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r19 = r1
            goto L_0x0fa4
        L_0x0fa2:
            r19 = r2
        L_0x0fa4:
            r2 = r11[r12]
            r20 = 0
            r1 = r47
            r3 = r4
            r4 = r5
            r7 = 3
            r5 = r8
            r8 = 2
            r38 = 0
            r6 = r31
            r7 = r32
            r31 = 0
            r8 = r19
            r10 = r30
            r44 = r11
            r11 = r28
            r12 = r18
            r13 = r23
            r14 = r25
            r15 = r24
            r16 = r0
            r17 = r29
            r18 = r26
            r19 = r27
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r2 = r47
            goto L_0x0ff0
        L_0x0fd5:
            r44 = r11
            r31 = 0
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r2 = "phone"
            r0.putString(r2, r7)
            r0.putString(r3, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$3rXtwzTW6WkmVHi2jvIuUk6ur6Y r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$3rXtwzTW6WkmVHi2jvIuUk6ur6Y
            r2 = r47
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0ff0:
            r7 = r33
            r12 = r34
            r13 = r37
            goto L_0x1003
        L_0x0ff7:
            r44 = r11
            r2 = r15
            r31 = 0
            r7 = 0
            r12 = 0
            r13 = 0
            r35 = 0
            r36 = 0
        L_0x1003:
            r1 = r48
            r0 = r35
            r4 = r36
            r6 = r44
            r3 = 0
            r5 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            goto L_0x10f0
        L_0x1012:
            r44 = r11
            r2 = r15
            r31 = 0
            java.lang.String r0 = r48.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1033
            r1 = r48
            r6 = r44
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 1
            goto L_0x10f0
        L_0x1033:
            java.lang.String r0 = r48.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x104d
            r1 = r48
            r6 = r44
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 1
            goto L_0x10ee
        L_0x104d:
            java.lang.String r0 = r48.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x10b7
            java.lang.String r0 = "chatId"
            r1 = r48
            r3 = 0
            int r0 = r1.getIntExtra(r0, r3)
            java.lang.String r4 = "userId"
            int r4 = r1.getIntExtra(r4, r3)
            java.lang.String r5 = "encId"
            int r5 = r1.getIntExtra(r5, r3)
            if (r0 == 0) goto L_0x1084
            r6 = r44
            r4 = r6[r3]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r4.postNotificationName(r5, r7)
            r4 = 0
        L_0x1081:
            r5 = 0
        L_0x1082:
            r12 = 0
            goto L_0x10ad
        L_0x1084:
            r6 = r44
            if (r4 == 0) goto L_0x1097
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r7)
            r0 = 0
            goto L_0x1081
        L_0x1097:
            if (r5 == 0) goto L_0x10a9
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r7 = new java.lang.Object[r3]
            r0.postNotificationName(r4, r7)
            r0 = 0
            r4 = 0
            goto L_0x1082
        L_0x10a9:
            r0 = 0
            r4 = 0
            r5 = 0
            r12 = 1
        L_0x10ad:
            r45 = r12
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r13 = 0
            r12 = r4
            r4 = 0
            goto L_0x10f2
        L_0x10b7:
            r1 = r48
            r6 = r44
            r3 = 0
            java.lang.String r0 = r48.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x10ce
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 1
            goto L_0x10ec
        L_0x10ce:
            java.lang.String r0 = r48.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x10e7
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            goto L_0x10ed
        L_0x10e1:
            r31 = r8
            r6 = r11
            r1 = r14
            r2 = r15
            r3 = 0
        L_0x10e7:
            r0 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
        L_0x10ec:
            r9 = 0
        L_0x10ed:
            r10 = 0
        L_0x10ee:
            r12 = 0
            r13 = 0
        L_0x10f0:
            r45 = 0
        L_0x10f2:
            int r11 = r2.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            boolean r11 = r11.isClientActivated()
            if (r11 == 0) goto L_0x1436
            if (r12 == 0) goto L_0x114b
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "user_id"
            r0.putInt(r5, r12)
            if (r4 == 0) goto L_0x1112
            java.lang.String r5 = "message_id"
            r0.putInt(r5, r4)
        L_0x1112:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x1135
            r4 = r6[r3]
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = mainFragmentsStack
            int r6 = r5.size()
            r11 = 1
            int r6 = r6 - r11
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            boolean r4 = r4.checkCanOpenChat(r0, r5)
            if (r4 == 0) goto L_0x1149
            goto L_0x1136
        L_0x1135:
            r11 = 1
        L_0x1136:
            org.telegram.ui.ChatActivity r6 = new org.telegram.ui.ChatActivity
            r6.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r7 = 0
            r8 = 1
            r9 = 1
            r10 = 0
            boolean r0 = r5.presentFragment(r6, r7, r8, r9, r10)
            if (r0 == 0) goto L_0x1149
        L_0x1147:
            r13 = 1
            goto L_0x11b1
        L_0x1149:
            r13 = 0
            goto L_0x11b1
        L_0x114b:
            r11 = 1
            if (r0 == 0) goto L_0x1194
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.String r7 = "chat_id"
            r5.putInt(r7, r0)
            if (r4 == 0) goto L_0x115f
            java.lang.String r0 = "message_id"
            r5.putInt(r0, r4)
        L_0x115f:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1180
            r0 = r6[r3]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r6 = r4.size()
            int r6 = r6 - r11
            java.lang.Object r4 = r4.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r0 = r0.checkCanOpenChat(r5, r4)
            if (r0 == 0) goto L_0x1149
        L_0x1180:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r2.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1149
            goto L_0x1147
        L_0x1194:
            if (r5 == 0) goto L_0x11b6
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "enc_id"
            r0.putInt(r4, r5)
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r2.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r13 = r12.presentFragment(r13, r14, r15, r16, r17)
        L_0x11b1:
            r0 = r49
            r4 = 0
            goto L_0x143b
        L_0x11b6:
            if (r45 == 0) goto L_0x11f0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x11c4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.removeAllFragments()
            goto L_0x11ec
        L_0x11c4:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x11ec
        L_0x11ce:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r11
            if (r0 <= 0) goto L_0x11e7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r0.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r4)
            goto L_0x11ce
        L_0x11e7:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.closeLastFragment(r3)
        L_0x11ec:
            r0 = 0
        L_0x11ed:
            r4 = 0
            goto L_0x143a
        L_0x11f0:
            if (r7 == 0) goto L_0x1221
            org.telegram.ui.ActionBar.BaseFragment r0 = r2.getCurrentWalletFragment(r7)
            if (r0 == 0) goto L_0x1200
            org.telegram.ui.-$$Lambda$LaunchActivity$5GybZC1APjVUWmzZAupg-DSdEx8 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$5GybZC1APjVUWmzZAupg-DSdEx8
            r4.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x1200:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1216
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x121b
        L_0x1216:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
        L_0x121b:
            r0 = r49
            r4 = 0
            r13 = 1
            goto L_0x143b
        L_0x1221:
            if (r8 == 0) goto L_0x1242
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x123f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r4 = new org.telegram.ui.Components.AudioPlayerAlert
            r4.<init>(r2)
            r0.showDialog(r4)
        L_0x123f:
            r0 = r49
            goto L_0x11ed
        L_0x1242:
            if (r9 == 0) goto L_0x1266
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x123f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r4 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI
            r5.<init>(r6)
            r4.<init>(r2, r5)
            r0.showDialog(r4)
            goto L_0x123f
        L_0x1266:
            java.lang.String r0 = r2.videoPath
            if (r0 != 0) goto L_0x12fe
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r2.photoPathsArray
            if (r0 != 0) goto L_0x12fe
            java.lang.String r0 = r2.sendingText
            if (r0 != 0) goto L_0x12fe
            java.util.ArrayList<java.lang.String> r0 = r2.documentsPathsArray
            if (r0 != 0) goto L_0x12fe
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r2.contactsToSend
            if (r0 != 0) goto L_0x12fe
            java.util.ArrayList<android.net.Uri> r0 = r2.documentsUrisArray
            if (r0 == 0) goto L_0x1280
            goto L_0x12fe
        L_0x1280:
            if (r13 == 0) goto L_0x12c3
            if (r13 != r11) goto L_0x128a
            org.telegram.ui.SettingsActivity r7 = new org.telegram.ui.SettingsActivity
            r7.<init>()
            goto L_0x129d
        L_0x128a:
            r4 = 2
            if (r13 != r4) goto L_0x1293
            org.telegram.ui.ThemeActivity r7 = new org.telegram.ui.ThemeActivity
            r7.<init>(r3)
            goto L_0x129d
        L_0x1293:
            r4 = 3
            if (r13 != r4) goto L_0x129c
            org.telegram.ui.SessionsActivity r7 = new org.telegram.ui.SessionsActivity
            r7.<init>(r3)
            goto L_0x129d
        L_0x129c:
            r7 = 0
        L_0x129d:
            org.telegram.ui.-$$Lambda$LaunchActivity$8IG4rcI4GUiI0vd7KeiMHRKV008 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$8IG4rcI4GUiI0vd7KeiMHRKV008
            r0.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x12bc
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x121b
        L_0x12bc:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
            goto L_0x121b
        L_0x12c3:
            if (r10 == 0) goto L_0x12fb
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "destroyAfterSelect"
            r0.putBoolean(r4, r11)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            org.telegram.ui.ContactsActivity r6 = new org.telegram.ui.ContactsActivity
            r6.<init>(r0)
            r7 = 0
            r8 = 1
            r9 = 1
            r10 = 0
            r5.presentFragment(r6, r7, r8, r9, r10)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x12f4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x121b
        L_0x12f4:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
            goto L_0x121b
        L_0x12fb:
            r4 = 0
            goto L_0x1438
        L_0x12fe:
            r4 = 3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1312
            r0 = r6[r3]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r0.postNotificationName(r5, r6)
        L_0x1312:
            int r0 = (r21 > r31 ? 1 : (r21 == r31 ? 0 : -1))
            if (r0 != 0) goto L_0x1425
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r5 = "onlySelect"
            r0.putBoolean(r5, r11)
            java.lang.String r5 = "dialogsType"
            r0.putInt(r5, r4)
            java.lang.String r4 = "allowSwitchAccount"
            r0.putBoolean(r4, r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r2.contactsToSend
            if (r4 == 0) goto L_0x1351
            int r4 = r4.size()
            if (r4 == r11) goto L_0x136d
            r4 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r5 = "SendContactToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            java.lang.String r5 = "SendContactToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
            goto L_0x136d
        L_0x1351:
            r4 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r5 = "SendMessagesToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertString"
            r0.putString(r5, r4)
            r4 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            java.lang.String r5 = "SendMessagesToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = "selectAlertStringGroup"
            r0.putString(r5, r4)
        L_0x136d:
            org.telegram.ui.DialogsActivity r13 = new org.telegram.ui.DialogsActivity
            r13.<init>(r0)
            r13.setDelegate(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1397
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x13b4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r11
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x13b4
            goto L_0x13b2
        L_0x1397:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r11) goto L_0x13b4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r4 = r0.size()
            int r4 = r4 - r11
            java.lang.Object r0 = r0.get(r4)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x13b4
        L_0x13b2:
            r0 = 1
            goto L_0x13b5
        L_0x13b4:
            r0 = 0
        L_0x13b5:
            r14 = r0
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r2.actionBarLayout
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x13d8
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x13d8
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r3, r3)
            goto L_0x1407
        L_0x13d8:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x13f0
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x13f0
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r3, r11)
            goto L_0x1407
        L_0x13f0:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x1407
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x1407
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r3, r11)
        L_0x1407:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x141e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x121b
        L_0x141e:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
            goto L_0x121b
        L_0x1425:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r4 = java.lang.Long.valueOf(r21)
            r0.add(r4)
            r4 = 0
            r2.didSelectDialogs(r4, r0, r4, r3)
            goto L_0x1438
        L_0x1436:
            r4 = 0
            r11 = 1
        L_0x1438:
            r0 = r49
        L_0x143a:
            r13 = 0
        L_0x143b:
            if (r13 != 0) goto L_0x14d9
            if (r0 != 0) goto L_0x14d9
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x148a
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x146b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x14c4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x14c4
        L_0x146b:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x14c4
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
            goto L_0x14c4
        L_0x148a:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x14c4
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x14b0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            org.telegram.ui.LoginActivity r5 = new org.telegram.ui.LoginActivity
            r5.<init>()
            r0.addFragmentToStack(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r3)
            goto L_0x14c4
        L_0x14b0:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r2.sideMenu
            r0.setSideMenu(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r2.actionBarLayout
            r5.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r2.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r3)
        L_0x14c4:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x14d9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r2.rightActionBarLayout
            r0.showLastFragment()
        L_0x14d9:
            r1.setAction(r4)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$32$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$9$LaunchActivity(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
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

            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(messageMedia, this.f$1, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        lambda$runLinkRequest$32$LaunchActivity(locationActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0284  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, boolean r29, java.lang.Integer r30, java.lang.Integer r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, org.telegram.tgnet.TLRPC.TL_wallPaper r38, java.lang.String r39, int r40) {
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
            org.telegram.ui.-$$Lambda$LaunchActivity$uLaPByy0gdOc9w323HA3PoQ2cC8 r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$uLaPByy0gdOc9w323HA3PoQ2cC8
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
            r7 = 2131625828(0x7f0e0764, float:1.8878875E38)
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
            r1 = 2131624184(0x7f0e00f8, float:1.887554E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131625864(0x7f0e0788, float:1.8878948E38)
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
            r1 = 2131624295(0x7f0e0167, float:1.8875766E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624304(0x7f0e0170, float:1.8875784E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$k6pGt3Qt4sxmr3lV_Rl0NIog2fE r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$k6pGt3Qt4sxmr3lV_Rl0NIog2fE
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
            org.telegram.ui.-$$Lambda$LaunchActivity$cP-BUXErnntbOCLASSNAMEWOXbRxYV31I r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$cP-BUXErnntbOCLASSNAMEWOXbRxYV31I
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
            org.telegram.ui.-$$Lambda$LaunchActivity$b0vcBpC7i4V2XHDdbrbQCKVGRv4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$b0vcBpC7i4V2XHDdbrbQCKVGRv4
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
            org.telegram.ui.-$$Lambda$LaunchActivity$nstaeeHUtCq1UCIaPqfqMBnjOrI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$nstaeeHUtCq1UCIaPqfqMBnjOrI
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
            org.telegram.ui.-$$Lambda$LaunchActivity$Y8iOWH5Pksz29uJJNv8u7elefb8 r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$Y8iOWH5Pksz29uJJNv8u7elefb8
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
            org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c
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
            org.telegram.ui.-$$Lambda$LaunchActivity$7iJyRhApNsveW8Gt05Rio-vwsAg r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$7iJyRhApNsveW8Gt05Rio-vwsAg
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
            org.telegram.ui.-$$Lambda$LaunchActivity$vy243eJqlNTq76Ed9pGk-cU6I3g r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$vy243eJqlNTq76Ed9pGk-cU6I3g     // Catch:{ Exception -> 0x027d }
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
            org.telegram.ui.-$$Lambda$LaunchActivity$tu9YrzAVkkuCJ6HR12ZmZoIW-70 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$tu9YrzAVkkuCJ6HR12ZmZoIW-70
            r5.<init>(r7, r1)
            int r0 = r2.sendRequest(r0, r5)
            r8[r12] = r0
            goto L_0x0328
        L_0x02a7:
            r1 = r39
            if (r1 == 0) goto L_0x02d2
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
            org.telegram.ui.-$$Lambda$LaunchActivity$8SJjENa-BluOeubBalyuqiaa2Mk r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$8SJjENa-BluOeubBalyuqiaa2Mk
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
            org.telegram.ui.-$$Lambda$LaunchActivity$BwVmPHsS9r3JtAebM5EKanHSsKE r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$BwVmPHsS9r3JtAebM5EKanHSsKE
            r0.<init>(r3, r8, r10)
            r7.setOnCancelListener(r0)
            r0 = 3
            r7.showDelayed(r0)     // Catch:{ Exception -> 0x0339 }
        L_0x0339:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$11$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC.TL_wallPaper tL_wallPaper, String str12, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, str7, hashMap, str8, str9, str10, str11, tL_wallPaper, str12, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$16$LaunchActivity(String str, int i, String str2, String str3, Integer num, AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tL_error, str, i, str2, str3, num, alertDialog) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
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
                LaunchActivity.this.lambda$null$15$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
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
    public /* synthetic */ void lambda$null$15$LaunchActivity(org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC.TL_error r15, java.lang.String r16, int r17, java.lang.String r18, java.lang.String r19, java.lang.Integer r20, org.telegram.ui.ActionBar.AlertDialog r21) {
        /*
            r13 = this;
            r1 = r13
            r0 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            boolean r5 = r13.isFinishing()
            if (r5 != 0) goto L_0x0299
            r5 = r14
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r5 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r5
            r6 = 1
            r7 = 0
            if (r15 != 0) goto L_0x0277
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r1.actionBarLayout
            if (r8 == 0) goto L_0x0277
            if (r0 == 0) goto L_0x0026
            if (r0 == 0) goto L_0x0277
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0277
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
            r4 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.String r8 = "SendGameToText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            java.lang.String r8 = "selectAlertString"
            r3.putString(r8, r4)
            r4 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.String r8 = "SendGameToGroupText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            java.lang.String r8 = "selectAlertStringGroup"
            r3.putString(r8, r4)
            org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
            r4.<init>(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$SzCpHBmwn3FfXN9hHNqGNhaf7AA r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$SzCpHBmwn3FfXN9hHNqGNhaf7AA
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
            goto L_0x0250
        L_0x0133:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r7)
            goto L_0x0250
        L_0x013a:
            r0 = 0
            if (r3 == 0) goto L_0x01a6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r5.users
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x014d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r5.users
            java.lang.Object r0 = r0.get(r7)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
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
            r8 = 2131624141(0x7f0e00cd, float:1.8875453E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$rJYDHlW5yGDb4uth8VI7518EY4g r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$rJYDHlW5yGDb4uth8VI7518EY4g
            r4.<init>(r2, r0, r3)
            r5.setDelegate(r4)
            r13.lambda$runLinkRequest$32$LaunchActivity(r5)
            goto L_0x0250
        L_0x0190:
            java.lang.String r0 = "BotCantJoinGroups"
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
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
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            int r8 = r8.id
            java.lang.String r9 = "chat_id"
            r3.putInt(r9, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r5.chats
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            int r8 = r8.id
            int r8 = -r8
            long r8 = (long) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r10 = r5.chats
            java.lang.Object r10 = r10.get(r7)
            org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
            goto L_0x01f3
        L_0x01d7:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            int r8 = r8.id
            java.lang.String r9 = "user_id"
            r3.putInt(r9, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r5.users
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            int r8 = r8.id
            long r8 = (long) r8
            r10 = r0
        L_0x01f3:
            if (r4 == 0) goto L_0x0210
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r5.users
            int r11 = r11.size()
            if (r11 <= 0) goto L_0x0210
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r5.users
            java.lang.Object r5 = r5.get(r7)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC.User) r5
            boolean r5 = r5.bot
            if (r5 == 0) goto L_0x0210
            java.lang.String r5 = "botUser"
            r3.putString(r5, r4)
            r5 = 1
            goto L_0x0211
        L_0x0210:
            r5 = 0
        L_0x0211:
            if (r20 == 0) goto L_0x021c
            int r11 = r20.intValue()
            java.lang.String r12 = "message_id"
            r3.putInt(r12, r11)
        L_0x021c:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x0231
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r11 = r0.size()
            int r11 = r11 - r6
            java.lang.Object r0 = r0.get(r11)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
        L_0x0231:
            if (r0 == 0) goto L_0x023d
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r17)
            boolean r11 = r11.checkCanOpenChat(r3, r0)
            if (r11 == 0) goto L_0x0250
        L_0x023d:
            if (r5 == 0) goto L_0x0253
            boolean r5 = r0 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x0253
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r11 = r0.getDialogId()
            int r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0253
            r0.setBotUser(r4)
        L_0x0250:
            r10 = r21
            goto L_0x028e
        L_0x0253:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r17)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r20 != 0) goto L_0x025f
            r4 = 0
            goto L_0x0263
        L_0x025f:
            int r4 = r20.intValue()
        L_0x0263:
            org.telegram.ui.-$$Lambda$LaunchActivity$zf4T5X-LU_s-WkYwYEz7FgZAiR4 r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$zf4T5X-LU_s-WkYwYEz7FgZAiR4
            r10 = r21
            r5.<init>(r10, r3)
            r14 = r0
            r15 = r8
            r17 = r2
            r18 = r4
            r19 = r5
            r14.ensureMessagesLoaded(r15, r17, r18, r19)
            r6 = 0
            goto L_0x028e
        L_0x0277:
            r10 = r21
            java.lang.String r0 = "NoUsernameFound"
            r2 = 2131625676(0x7f0e06cc, float:1.8878567E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x028a }
            android.widget.Toast r0 = android.widget.Toast.makeText(r13, r0, r7)     // Catch:{ Exception -> 0x028a }
            r0.show()     // Catch:{ Exception -> 0x028a }
            goto L_0x028e
        L_0x028a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x028e:
            if (r6 == 0) goto L_0x0299
            r21.dismiss()     // Catch:{ Exception -> 0x0294 }
            goto L_0x0299
        L_0x0294:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0299:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$15$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$12$LaunchActivity(String str, int i, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        TLRPC.TL_inputMediaGame tL_inputMediaGame = new TLRPC.TL_inputMediaGame();
        tL_inputMediaGame.id = new TLRPC.TL_inputGameShortName();
        TLRPC.InputGame inputGame = tL_inputMediaGame.id;
        inputGame.short_name = str;
        inputGame.bot_id = MessagesController.getInstance(i).getInputUser(tL_contacts_resolvedPeer.users.get(0));
        int i2 = (int) longValue;
        SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer(i2), tL_inputMediaGame, 0, 0);
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

    public /* synthetic */ void lambda$null$13$LaunchActivity(int i, TLRPC.User user, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
        MessagesController.getInstance(i).addUserToChat(i2, user, (TLRPC.ChatFull) null, 0, str, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
    }

    public /* synthetic */ void lambda$null$14$LaunchActivity(AlertDialog alertDialog, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!isFinishing()) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$20$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, i, alertDialog, str) {
            private final /* synthetic */ TLRPC.TL_error f$1;
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
                LaunchActivity.this.lambda$null$19$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
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
    public /* synthetic */ void lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC.TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x00f6
            r0 = 0
            r1 = 1
            if (r10 != 0) goto L_0x00a9
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$ChatInvite r11 = (org.telegram.tgnet.TLRPC.ChatInvite) r11
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
            org.telegram.ui.-$$Lambda$LaunchActivity$eFtoK2ipUbUMzowFNBWNIY4_2lw r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$eFtoK2ipUbUMzowFNBWNIY4_2lw
            r0.<init>(r14)
            r13.setOnCancelListener(r0)
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.chat
            int r12 = r11.id
            int r12 = -r12
            long r4 = (long) r12
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r11)
            r7 = 0
            org.telegram.ui.-$$Lambda$LaunchActivity$EZ3DMx9oOLTpcp5ocRXeEi6Rg20 r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$EZ3DMx9oOLTpcp5ocRXeEi6Rg20
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
            r12 = 2131624184(0x7f0e00f8, float:1.887554E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r10 = r10.text
            java.lang.String r12 = "FLOOD_WAIT"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00d1
            r10 = 2131625151(0x7f0e04bf, float:1.8877502E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x00dd
        L_0x00d1:
            r10 = 2131625376(0x7f0e05a0, float:1.8877958E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x00dd:
            r10 = 2131625828(0x7f0e0764, float:1.8878875E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$17(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public /* synthetic */ void lambda$null$18$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle) {
        alertDialog.hide();
        if (!zArr[0]) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC.Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tL_error, tLObject, i) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;

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

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        if (!isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (tL_error != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                if (tL_error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", NUM));
                } else if (tL_error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                }
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                TLRPC.Updates updates = (TLRPC.Updates) tLObject;
                if (!updates.chats.isEmpty()) {
                    TLRPC.Chat chat = updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(i).putUsers(updates.users, false);
                    MessagesController.getInstance(i).putChats(updates.chats, false);
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", chat.id);
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
            MediaDataController.getInstance(i).saveDraft(longValue, str, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = (TLRPC.TL_account_authorizationForm) tLObject;
        if (tL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(alertDialog, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC.TL_account_authorizationForm f$3;
                private final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$4;
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

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$null$25$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$26$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$LaunchActivity(AlertDialog alertDialog, int i, TLRPC.TL_account_authorizationForm tL_account_authorizationForm, TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC.TL_account_authorizationForm f$4;
            private final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$5;
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
                LaunchActivity.this.lambda$null$24$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC.TL_account_authorizationForm tL_account_authorizationForm, TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm2 = tL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tL_account_authorizationForm.users, false);
            lambda$runLinkRequest$32$LaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm2.bot_id, tL_account_getAuthorizationForm2.scope, tL_account_getAuthorizationForm2.public_key, str, str2, str3, tL_account_authorizationForm, (TLRPC.TL_account_password) tLObject));
            return;
        }
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, TLRPC.TL_error tL_error) {
        try {
            alertDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tL_error.text));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;

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
        if (tLObject instanceof TLRPC.TL_help_deepLinkInfo) {
            TLRPC.TL_help_deepLinkInfo tL_help_deepLinkInfo = (TLRPC.TL_help_deepLinkInfo) tLObject;
            AlertsCreator.showUpdateAppAlert(this, tL_help_deepLinkInfo.message, tL_help_deepLinkInfo.update_app);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;

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

    public /* synthetic */ void lambda$null$30$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject instanceof TLRPC.TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TLRPC.TL_langPackLanguage) tLObject));
        } else if (tL_error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(tL_error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", NUM)));
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tL_error.text));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$34$LaunchActivity(AlertDialog alertDialog, TLRPC.TL_wallPaper tL_wallPaper, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tL_wallPaper, tL_error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_wallPaper f$3;
            private final /* synthetic */ TLRPC.TL_error f$4;

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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$33$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_wallPaper r11, org.telegram.tgnet.TLRPC.TL_error r12) {
        /*
            r8 = this;
            r9.dismiss()     // Catch:{ Exception -> 0x0004 }
            goto L_0x0008
        L_0x0004:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0008:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r9 == 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r10
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
            goto L_0x0037
        L_0x0036:
            r9 = r10
        L_0x0037:
            org.telegram.ui.ThemePreviewActivity r10 = new org.telegram.ui.ThemePreviewActivity
            r12 = 0
            r10.<init>(r9, r12)
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r11.settings
            boolean r11 = r9.blur
            boolean r9 = r9.motion
            r10.setInitialModes(r11, r9)
            r8.lambda$runLinkRequest$32$LaunchActivity(r10)
            goto L_0x0070
        L_0x004a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625022(0x7f0e043e, float:1.887724E38)
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
        L_0x0070:
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

    public /* synthetic */ void lambda$runLinkRequest$37$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, alertDialog, tL_error) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;

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

    public /* synthetic */ void lambda$null$36$LaunchActivity(TLObject tLObject, AlertDialog alertDialog, TLRPC.TL_error tL_error) {
        char c;
        TLRPC.TL_wallPaper tL_wallPaper;
        if (tLObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme tL_theme = (TLRPC.TL_theme) tLObject;
            TLRPC.TL_themeSettings tL_themeSettings = tL_theme.settings;
            char c2 = 0;
            if (tL_themeSettings != null) {
                Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tL_themeSettings));
                if (theme != null) {
                    TLRPC.WallPaper wallPaper = tL_theme.settings.wallpaper;
                    if (wallPaper instanceof TLRPC.TL_wallPaper) {
                        tL_wallPaper = (TLRPC.TL_wallPaper) wallPaper;
                        if (!FileLoader.getPathToAttach(tL_wallPaper.document, true).exists()) {
                            this.loadingThemeProgressDialog = alertDialog;
                            this.loadingThemeAccent = true;
                            this.loadingThemeInfo = theme;
                            this.loadingTheme = tL_theme;
                            this.loadingThemeWallpaper = tL_wallPaper;
                            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
                            FileLoader.getInstance(this.currentAccount).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
                            return;
                        }
                    } else {
                        tL_wallPaper = null;
                    }
                    try {
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    openThemeAccentPreview(tL_theme, tL_wallPaper, theme);
                    c = c2;
                }
            } else if (tL_theme.document != null) {
                this.loadingThemeAccent = false;
                this.loadingTheme = tL_theme;
                this.loadingThemeFileName = FileLoader.getAttachFileName(this.loadingTheme.document);
                this.loadingThemeProgressDialog = alertDialog;
                FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, tL_theme, 1, 1);
                c = c2;
            }
            c2 = 1;
            c = c2;
        } else {
            c = (tL_error == null || !"THEME_FORMAT_INVALID".equals(tL_error.text)) ? (char) 2 : 1;
        }
        if (c != 0) {
            try {
                alertDialog.dismiss();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (c == 1) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNotSupported", NUM)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNotFound", NUM)));
            }
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$40$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC.TL_channels_getChannels tL_channels_getChannels = new TLRPC.TL_channels_getChannels();
            TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
            tL_inputChannel.channel_id = num.intValue();
            tL_channels_getChannels.id.add(tL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getChannels, new RequestDelegate(alertDialog, baseFragment, i, bundle) {
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

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$null$39$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$39$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC.TL_error tL_error) {
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
        if (tLObject instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
            if (!tL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chats.chats, false);
                TLRPC.Chat chat = tL_messages_chats.chats.get(0);
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
            TLRPC.TL_help_getAppUpdate tL_help_getAppUpdate = new TLRPC.TL_help_getAppUpdate();
            try {
                tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tL_help_getAppUpdate.source == null) {
                tL_help_getAppUpdate.source = "";
            }
            int i = this.currentAccount;
            ConnectionsManager.getInstance(i).sendRequest(tL_help_getAppUpdate, new RequestDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$checkAppUpdate$43$LaunchActivity(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$43$LaunchActivity(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (tLObject instanceof TLRPC.TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_help_appUpdate) tLObject, i) {
                private final /* synthetic */ TLRPC.TL_help_appUpdate f$1;
                private final /* synthetic */ int f$2;

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

    public /* synthetic */ void lambda$null$42$LaunchActivity(TLRPC.TL_help_appUpdate tL_help_appUpdate, int i) {
        if (tL_help_appUpdate.can_not_skip) {
            UserConfig.getInstance(0).pendingAppUpdate = tL_help_appUpdate;
            UserConfig.getInstance(0).pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = 0;
            }
            UserConfig.getInstance(0).saveConfig(false);
            showUpdateActivity(i, tL_help_appUpdate, false);
            return;
        }
        new UpdateAppAlertDialog(this, tL_help_appUpdate, i).show();
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
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
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

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        ChatActivity chatActivity;
        int i;
        String str;
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList<Long> arrayList2 = arrayList;
        ArrayList<TLRPC.User> arrayList3 = this.contactsToSend;
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
                ArrayList<TLRPC.User> arrayList7 = this.contactsToSend;
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
                        ArrayList<TLRPC.User> arrayList10 = this.contactsToSend;
                        if (arrayList10 != null && !arrayList10.isEmpty()) {
                            for (int i8 = 0; i8 < this.contactsToSend.size(); i8++) {
                                SendMessagesHelper.getInstance(currentAccount2).sendMessage(this.contactsToSend.get(i8), longValue3, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
                    PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList11.get(arrayList11.size() - 1), (ContactsController.Contact) null, (TLRPC.User) null, this.contactsToSendUri, (File) null, (String) null);
                    phonebookShareAlert.setDelegate(new PhonebookSelectShareAlert.PhonebookShareAlertDelegate(chatActivity, arrayList2, currentAccount2) {
                        private final /* synthetic */ ChatActivity f$1;
                        private final /* synthetic */ ArrayList f$2;
                        private final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void didSelectContact(TLRPC.User user, boolean z, int i) {
                            LaunchActivity.this.lambda$didSelectDialogs$45$LaunchActivity(this.f$1, this.f$2, this.f$3, user, z, i);
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

    public /* synthetic */ void lambda$didSelectDialogs$45$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC.User user, boolean z, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SendMessagesHelper.getInstance(i).sendMessage(user, ((Long) arrayList.get(i3)).longValue(), (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
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
        AndroidUtilities.unregisterUpdates();
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
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
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
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
            goto L_0x0565
        L_0x0009:
            int r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1 = 0
            if (r13 != r0) goto L_0x001a
            r13 = r15[r1]
            if (r13 == r12) goto L_0x0565
            r12.onFinish()
            r12.finish()
            goto L_0x0565
        L_0x001a:
            int r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r13 != r0) goto L_0x0049
            org.telegram.tgnet.ConnectionsManager r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r14)
            int r13 = r13.getConnectionState()
            int r15 = r12.currentConnectionState
            if (r15 == r13) goto L_0x0565
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
            goto L_0x0565
        L_0x0049:
            int r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r13 != r0) goto L_0x0054
            org.telegram.ui.Adapters.DrawerLayoutAdapter r13 = r12.drawerLayoutAdapter
            r13.notifyDataSetChanged()
            goto L_0x0565
        L_0x0054:
            int r0 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2 = 2131624467(0x7f0e0213, float:1.8876115E38)
            java.lang.String r3 = "Cancel"
            r4 = 2131624184(0x7f0e00f8, float:1.887554E38)
            java.lang.String r5 = "AppName"
            r6 = 3
            r7 = 2
            r8 = 2131625828(0x7f0e0764, float:1.8878875E38)
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
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r13 = (org.telegram.tgnet.TLRPC.TL_help_termsOfService) r13
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
            r1 = 2131625584(0x7f0e0670, float:1.887838E38)
            java.lang.String r4 = "MoreInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$NSo2b96RBrvjx_PKPJS1uHaK_D0 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$NSo2b96RBrvjx_PKPJS1uHaK_D0
            r4.<init>(r14)
            r0.setNegativeButton(r1, r4)
        L_0x00b2:
            int r14 = r13.intValue()
            r1 = 5
            if (r14 != r1) goto L_0x00ce
            r13 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r14 = "NobodyLikesSpam3"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00ce:
            int r14 = r13.intValue()
            if (r14 != 0) goto L_0x00e9
            r13 = 2131625678(0x7f0e06ce, float:1.887857E38)
            java.lang.String r14 = "NobodyLikesSpam1"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setMessage(r13)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r13, r10)
            goto L_0x0169
        L_0x00e9:
            int r14 = r13.intValue()
            if (r14 != r11) goto L_0x0103
            r13 = 2131625679(0x7f0e06cf, float:1.8878573E38)
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
            r13 = 2131625460(0x7f0e05f4, float:1.8878129E38)
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
            r13 = 2131626274(0x7f0e0922, float:1.887978E38)
            java.lang.String r14 = "Proxy"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.setTitle(r13)
            r13 = 2131626863(0x7f0e0b6f, float:1.8880974E38)
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
            if (r13 != 0) goto L_0x0565
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r0.create()
            r13.showDialog(r14)
            goto L_0x0565
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
            r0 = 2131626558(0x7f0e0a3e, float:1.8880356E38)
            java.lang.String r1 = "ShareYouLocationUnableManually"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo
            r1.<init>(r13, r14)
            r15.setNegativeButton(r0, r1)
            r13 = 2131626557(0x7f0e0a3d, float:1.8880354E38)
            java.lang.String r14 = "ShareYouLocationUnable"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r15.setMessage(r13)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x0565
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r13 = mainFragmentsStack
            int r14 = r13.size()
            int r14 = r14 - r11
            java.lang.Object r13 = r13.get(r14)
            org.telegram.ui.ActionBar.BaseFragment r13 = (org.telegram.ui.ActionBar.BaseFragment) r13
            org.telegram.ui.ActionBar.AlertDialog r14 = r15.create()
            r13.showDialog(r14)
            goto L_0x0565
        L_0x01dd:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r13 != r0) goto L_0x01f0
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x0565
            android.view.View r13 = r13.getChildAt(r1)
            if (r13 == 0) goto L_0x0565
            r13.invalidate()
            goto L_0x0565
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
            goto L_0x0565
        L_0x020b:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x0565
        L_0x0211:
            boolean r13 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r13 != 0) goto L_0x0565
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x0220 }
            r13.clearFlags(r14)     // Catch:{ Exception -> 0x0220 }
            goto L_0x0565
        L_0x0220:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            goto L_0x0565
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
            goto L_0x0565
        L_0x0247:
            int r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r13 != r0) goto L_0x0250
            r12.showLanguageAlert(r1)
            goto L_0x0565
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
            org.telegram.tgnet.TLRPC$TL_webPage r14 = (org.telegram.tgnet.TLRPC.TL_webPage) r14
            r15 = r15[r11]
            java.lang.String r15 = (java.lang.String) r15
            r13.open(r14, r15)
            goto L_0x0565
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
            r6 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.String r7 = "UpdateContactsTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setTitle(r6)
            r6 = 2131626830(0x7f0e0b4e, float:1.8880907E38)
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
            goto L_0x0565
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
            goto L_0x0565
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
            org.telegram.ui.Components.CubicBezierInterpolator r14 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN_OUT_QUAD     // Catch:{ all -> 0x0407 }
            r13.setInterpolator(r14)     // Catch:{ all -> 0x0407 }
            org.telegram.ui.LaunchActivity$5 r14 = new org.telegram.ui.LaunchActivity$5     // Catch:{ all -> 0x0407 }
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
            if (r1 == 0) goto L_0x0565
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.rightActionBarLayout
            r1.animateThemedValues(r14, r15, r0, r13)
            goto L_0x0565
        L_0x0433:
            int r14 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r13 != r14) goto L_0x0464
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            if (r13 == 0) goto L_0x0565
            r14 = r15[r1]
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r13 = r13.getChildCount()
        L_0x0443:
            if (r1 >= r13) goto L_0x0565
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
            goto L_0x0565
        L_0x0461:
            int r1 = r1 + 1
            goto L_0x0443
        L_0x0464:
            int r14 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r13 != r14) goto L_0x0473
            r13 = r15[r1]     // Catch:{ all -> 0x0565 }
            com.google.android.gms.common.api.Status r13 = (com.google.android.gms.common.api.Status) r13     // Catch:{ all -> 0x0565 }
            r14 = 140(0x8c, float:1.96E-43)
            r13.startResolutionForResult(r12, r14)     // Catch:{ all -> 0x0565 }
            goto L_0x0565
        L_0x0473:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r13 != r14) goto L_0x052d
            java.lang.String r13 = r12.loadingThemeFileName
            if (r13 == 0) goto L_0x04fd
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0565
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
            if (r14 == 0) goto L_0x04f9
            java.lang.String r15 = r14.pathToWallpaper
            if (r15 == 0) goto L_0x04e2
            java.io.File r0 = new java.io.File
            r0.<init>(r15)
            boolean r15 = r0.exists()
            if (r15 != 0) goto L_0x04e2
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
        L_0x04e2:
            org.telegram.tgnet.TLRPC$TL_theme r14 = r12.loadingTheme
            java.lang.String r15 = r14.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r13, r15, r14, r11)
            if (r1 == 0) goto L_0x04f9
            org.telegram.ui.ThemePreviewActivity r13 = new org.telegram.ui.ThemePreviewActivity
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r13
            r0.<init>(r1, r2, r3, r4, r5)
            r12.lambda$runLinkRequest$32$LaunchActivity(r13)
        L_0x04f9:
            r12.onThemeLoadFinish()
            goto L_0x0565
        L_0x04fd:
            java.lang.String r13 = r12.loadingThemeWallpaperName
            if (r13 == 0) goto L_0x0565
            r14 = r15[r1]
            java.lang.String r14 = (java.lang.String) r14
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0565
            r12.loadingThemeWallpaperName = r10
            r13 = r15[r11]
            java.io.File r13 = (java.io.File) r13
            boolean r14 = r12.loadingThemeAccent
            if (r14 == 0) goto L_0x0522
            org.telegram.tgnet.TLRPC$TL_theme r13 = r12.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = r12.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r15 = r12.loadingThemeInfo
            r12.openThemeAccentPreview(r13, r14, r15)
            r12.onThemeLoadFinish()
            goto L_0x0565
        L_0x0522:
            org.telegram.messenger.DispatchQueue r14 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$XCOh9XiH_nEBmntlMKEFLyPJasM r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$XCOh9XiH_nEBmntlMKEFLyPJasM
            r15.<init>(r13)
            r14.postRunnable(r15)
            goto L_0x0565
        L_0x052d:
            int r14 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r13 != r14) goto L_0x0549
            r13 = r15[r1]
            java.lang.String r13 = (java.lang.String) r13
            java.lang.String r14 = r12.loadingThemeFileName
            boolean r14 = r13.equals(r14)
            if (r14 != 0) goto L_0x0545
            java.lang.String r14 = r12.loadingThemeWallpaperName
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0565
        L_0x0545:
            r12.onThemeLoadFinish()
            goto L_0x0565
        L_0x0549:
            int r14 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r13 != r14) goto L_0x055e
            boolean r13 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r13 == 0) goto L_0x0552
            return
        L_0x0552:
            boolean r13 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r13 == 0) goto L_0x055a
            r12.onPasscodeResume()
            goto L_0x0565
        L_0x055a:
            r12.onPasscodePause()
            goto L_0x0565
        L_0x055e:
            int r14 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r13 != r14) goto L_0x0565
            r12.checkSystemBarColors()
        L_0x0565:
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
                    private final /* synthetic */ HashMap f$0;
                    private final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                        LaunchActivity.lambda$null$51(this.f$0, this.f$1, messageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$32$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$51(HashMap hashMap, int i, TLRPC.MessageMedia messageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(messageMedia, messageObject.getDialogId(), messageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$57$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Theme.ThemeInfo f$2;

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
        if (tLObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
            this.loadingThemeWallpaper = tL_wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$59$LaunchActivity(File file) {
        Theme.ThemeInfo themeInfo = this.loadingThemeInfo;
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$58$LaunchActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$58$LaunchActivity() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
        TLRPC.TL_theme tL_theme = this.loadingTheme;
        Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
        if (applyThemeFile != null) {
            lambda$runLinkRequest$32$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
        }
        onThemeLoadFinish();
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void openThemeAccentPreview(TLRPC.TL_theme tL_theme, TLRPC.TL_wallPaper tL_wallPaper, Theme.ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        Theme.ThemeAccent createNewAccent = themeInfo.createNewAccent(tL_theme, this.currentAccount);
        themeInfo.prevAccentId = themeInfo.currentAccentId;
        themeInfo.setCurrentAccentId(createNewAccent.id);
        createNewAccent.pattern = tL_wallPaper;
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
            r9 = 2131624686(0x7f0e02ee, float:1.8876559E38)
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
            r14 = 2131625011(0x7f0e0433, float:1.8877218E38)
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
            r5 = 2131624687(0x7f0e02ef, float:1.887656E38)
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
            r2 = 2131625828(0x7f0e0764, float:1.8878875E38)
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
                            TLRPC.TL_langpack_getStrings tL_langpack_getStrings = new TLRPC.TL_langpack_getStrings();
                            tL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                            tL_langpack_getStrings.keys.add("English");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguage");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                            tL_langpack_getStrings.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate(localeInfoArr, str2) {
                                private final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                private final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$66$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
                                }
                            }, 8);
                            TLRPC.TL_langpack_getStrings tL_langpack_getStrings2 = new TLRPC.TL_langpack_getStrings();
                            tL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                            tL_langpack_getStrings2.keys.add("English");
                            tL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                            tL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                            tL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings2, new RequestDelegate(localeInfoArr, str2) {
                                private final /* synthetic */ LocaleController.LocaleInfo[] f$1;
                                private final /* synthetic */ String f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    LaunchActivity.this.lambda$showLanguageAlert$68$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
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

    public /* synthetic */ void lambda$showLanguageAlert$66$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
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
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$68$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
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
                LaunchActivity.this.lambda$null$67$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$67$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.englishLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
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

    private void updateCurrentConnectionState(int i) {
        String str;
        if (this.actionBarLayout != null) {
            int i2 = 0;
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            int i3 = this.currentConnectionState;
            $$Lambda$LaunchActivity$P2BvsvtXjb2l1rrge69H2_Nlg r4 = null;
            if (i3 == 2) {
                i2 = NUM;
                str = "WaitingForNetwork";
            } else if (i3 == 5) {
                i2 = NUM;
                str = "Updating";
            } else if (i3 == 4) {
                i2 = NUM;
                str = "ConnectingToProxy";
            } else if (i3 == 1) {
                i2 = NUM;
                str = "Connecting";
            } else {
                str = null;
            }
            int i4 = this.currentConnectionState;
            if (i4 == 1 || i4 == 4) {
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
