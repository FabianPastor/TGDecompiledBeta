package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.InputGame;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_channels_getChannels;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_deepLinkInfo;
import org.telegram.tgnet.TLRPC.TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_themeSettings;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WallPaperSettings;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.TermsOfServiceView.TermsOfServiceViewDelegate;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.Wallet.WalletActivity;
import org.telegram.ui.Wallet.WalletCreateActivity;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;

public class LaunchActivity extends Activity implements ActionBarLayoutDelegate, NotificationCenterDelegate, DialogsActivityDelegate {
    private static final int PLAY_SERVICES_REQUEST_CHECK_SETTINGS = 140;
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
    private TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TL_wallPaper loadingThemeWallpaper;
    private String loadingThemeWallpaperName;
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
    private ImageView themeSwitchImageView;
    private String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a1  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x010d */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:111:0x0475, code skipped:
            r0 = -1;
     */
    /* JADX WARNING: Missing block: B:112:0x0476, code skipped:
            if (r0 == 0) goto L_0x04e0;
     */
    /* JADX WARNING: Missing block: B:113:0x0478, code skipped:
            if (r0 == 1) goto L_0x04d1;
     */
    /* JADX WARNING: Missing block: B:114:0x047a, code skipped:
            if (r0 == 2) goto L_0x04bd;
     */
    /* JADX WARNING: Missing block: B:115:0x047c, code skipped:
            if (r0 == 3) goto L_0x04a9;
     */
    /* JADX WARNING: Missing block: B:117:0x047f, code skipped:
            if (r0 == 4) goto L_0x0495;
     */
    /* JADX WARNING: Missing block: B:119:0x0482, code skipped:
            if (r0 == 5) goto L_0x0486;
     */
    /* JADX WARNING: Missing block: B:121:0x0486, code skipped:
            r0 = new org.telegram.ui.WallpapersListActivity(0);
            r11.actionBarLayout.addFragmentToStack(r0);
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:122:0x0495, code skipped:
            if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:123:0x0497, code skipped:
            r0 = new org.telegram.ui.ProfileActivity(r3);
     */
    /* JADX WARNING: Missing block: B:124:0x04a2, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:125:0x04a4, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:126:0x04a9, code skipped:
            if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:127:0x04ab, code skipped:
            r0 = new org.telegram.ui.ChannelCreateActivity(r3);
     */
    /* JADX WARNING: Missing block: B:128:0x04b6, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:129:0x04b8, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:130:0x04bd, code skipped:
            if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:131:0x04bf, code skipped:
            r0 = new org.telegram.ui.GroupCreateFinalActivity(r3);
     */
    /* JADX WARNING: Missing block: B:132:0x04ca, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:133:0x04cc, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:134:0x04d1, code skipped:
            r0 = new org.telegram.ui.SettingsActivity();
            r11.actionBarLayout.addFragmentToStack(r0);
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:135:0x04e0, code skipped:
            if (r3 == null) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:136:0x04e2, code skipped:
            r0 = new org.telegram.ui.ChatActivity(r3);
     */
    /* JADX WARNING: Missing block: B:137:0x04ed, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x055f;
     */
    /* JADX WARNING: Missing block: B:138:0x04ef, code skipped:
            r0.restoreSelfArgs(r12);
     */
    public void onCreate(android.os.Bundle r12) {
        /*
        r11 = this;
        org.telegram.messenger.ApplicationLoader.postInitApplication();
        r0 = r11.getResources();
        r0 = r0.getConfiguration();
        org.telegram.messenger.AndroidUtilities.checkDisplaySize(r11, r0);
        r0 = org.telegram.messenger.UserConfig.selectedAccount;
        r11.currentAccount = r0;
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        r1 = 1;
        r2 = 0;
        if (r0 != 0) goto L_0x00ec;
    L_0x0020:
        r0 = r11.getIntent();
        if (r0 == 0) goto L_0x008c;
    L_0x0026:
        r3 = r0.getAction();
        if (r3 == 0) goto L_0x008c;
    L_0x002c:
        r3 = r0.getAction();
        r4 = "android.intent.action.SEND";
        r3 = r4.equals(r3);
        if (r3 != 0) goto L_0x0085;
    L_0x0038:
        r3 = r0.getAction();
        r4 = "android.intent.action.SEND_MULTIPLE";
        r3 = r4.equals(r3);
        if (r3 == 0) goto L_0x0045;
    L_0x0044:
        goto L_0x0085;
    L_0x0045:
        r3 = r0.getAction();
        r4 = "android.intent.action.VIEW";
        r3 = r4.equals(r3);
        if (r3 == 0) goto L_0x008c;
    L_0x0051:
        r3 = r0.getData();
        if (r3 == 0) goto L_0x008c;
    L_0x0057:
        r3 = r3.toString();
        r3 = r3.toLowerCase();
        r4 = "tg:proxy";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x0083;
    L_0x0068:
        r4 = "tg://proxy";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x0083;
    L_0x0071:
        r4 = "tg:socks";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x0083;
    L_0x007a:
        r4 = "tg://socks";
        r3 = r3.startsWith(r4);
        if (r3 == 0) goto L_0x008c;
    L_0x0083:
        r3 = 1;
        goto L_0x008d;
    L_0x0085:
        super.onCreate(r12);
        r11.finish();
        return;
    L_0x008c:
        r3 = 0;
    L_0x008d:
        r4 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r5 = 0;
        r7 = "intro_crashed_time";
        r8 = r4.getLong(r7, r5);
        r10 = "fromIntro";
        r10 = r0.getBooleanExtra(r10, r2);
        if (r10 == 0) goto L_0x00ac;
    L_0x00a1:
        r4 = r4.edit();
        r4 = r4.putLong(r7, r5);
        r4.commit();
    L_0x00ac:
        if (r3 != 0) goto L_0x00ec;
    L_0x00ae:
        r3 = java.lang.System.currentTimeMillis();
        r8 = r8 - r3;
        r3 = java.lang.Math.abs(r8);
        r5 = 120000; // 0x1d4c0 float:1.68156E-40 double:5.9288E-319;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 < 0) goto L_0x00ec;
    L_0x00be:
        if (r0 == 0) goto L_0x00ec;
    L_0x00c0:
        if (r10 != 0) goto L_0x00ec;
    L_0x00c2:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = "logininfo2";
        r3 = r3.getSharedPreferences(r4, r2);
        r3 = r3.getAll();
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x00ec;
    L_0x00d4:
        r1 = new android.content.Intent;
        r2 = org.telegram.ui.IntroActivity.class;
        r1.<init>(r11, r2);
        r0 = r0.getData();
        r1.setData(r0);
        r11.startActivity(r1);
        super.onCreate(r12);
        r11.finish();
        return;
    L_0x00ec:
        r11.requestWindowFeature(r1);
        r0 = NUM; // 0x7f0var_f float:1.900799E38 double:1.053194543E-314;
        r11.setTheme(r0);
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        r4 = 0;
        if (r0 < r3) goto L_0x0116;
    L_0x00fc:
        r0 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x010d }
        r6 = "actionBarDefault";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);	 Catch:{ Exception -> 0x010d }
        r6 = r6 | r0;
        r5.<init>(r4, r4, r6);	 Catch:{ Exception -> 0x010d }
        r11.setTaskDescription(r5);	 Catch:{ Exception -> 0x010d }
    L_0x010d:
        r5 = r11.getWindow();	 Catch:{ Exception -> 0x0115 }
        r5.setNavigationBarColor(r0);	 Catch:{ Exception -> 0x0115 }
        goto L_0x0116;
    L_0x0116:
        r0 = r11.getWindow();
        r5 = NUM; // 0x7var_dc float:1.7946063E38 double:1.0529358647E-314;
        r0.setBackgroundDrawableResource(r5);
        r0 = org.telegram.messenger.SharedConfig.passcodeHash;
        r0 = r0.length();
        if (r0 <= 0) goto L_0x013a;
    L_0x0128:
        r0 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r0 != 0) goto L_0x013a;
    L_0x012c:
        r0 = r11.getWindow();	 Catch:{ Exception -> 0x0136 }
        r5 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r0.setFlags(r5, r5);	 Catch:{ Exception -> 0x0136 }
        goto L_0x013a;
    L_0x0136:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x013a:
        super.onCreate(r12);
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 24;
        if (r0 < r5) goto L_0x0149;
    L_0x0143:
        r0 = r11.isInMultiWindowMode();
        org.telegram.messenger.AndroidUtilities.isInMultiwindow = r0;
    L_0x0149:
        org.telegram.ui.ActionBar.Theme.createChatResources(r11, r2);
        r0 = org.telegram.messenger.SharedConfig.passcodeHash;
        r0 = r0.length();
        if (r0 == 0) goto L_0x0162;
    L_0x0154:
        r0 = org.telegram.messenger.SharedConfig.appLocked;
        if (r0 == 0) goto L_0x0162;
    L_0x0158:
        r5 = android.os.SystemClock.uptimeMillis();
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r7;
        r0 = (int) r5;
        org.telegram.messenger.SharedConfig.lastPauseTime = r0;
    L_0x0162:
        org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r11);
        r0 = new org.telegram.ui.LaunchActivity$1;
        r0.<init>(r11);
        r11.actionBarLayout = r0;
        r0 = new android.widget.FrameLayout;
        r0.<init>(r11);
        r5 = new android.view.ViewGroup$LayoutParams;
        r6 = -1;
        r5.<init>(r6, r6);
        r11.setContentView(r0, r5);
        r5 = android.os.Build.VERSION.SDK_INT;
        r7 = 8;
        r8 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        if (r5 < r3) goto L_0x0197;
    L_0x0182:
        r3 = new android.widget.ImageView;
        r3.<init>(r11);
        r11.themeSwitchImageView = r3;
        r3 = r11.themeSwitchImageView;
        r3.setVisibility(r7);
        r3 = r11.themeSwitchImageView;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8);
        r0.addView(r3, r5);
    L_0x0197:
        r3 = new org.telegram.ui.ActionBar.DrawerLayoutContainer;
        r3.<init>(r11);
        r11.drawerLayoutContainer = r3;
        r3 = r11.drawerLayoutContainer;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setBehindKeyboardColor(r5);
        r3 = r11.drawerLayoutContainer;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8);
        r0.addView(r3, r5);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x02a5;
    L_0x01b9:
        r0 = r11.getWindow();
        r3 = 16;
        r0.setSoftInputMode(r3);
        r0 = new org.telegram.ui.LaunchActivity$2;
        r0.<init>(r11);
        r3 = r11.drawerLayoutContainer;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8);
        r3.addView(r0, r5);
        r3 = new android.view.View;
        r3.<init>(r11);
        r11.backgroundTablet = r3;
        r3 = r11.getResources();
        r5 = NUM; // 0x7var_ float:1.794484E38 double:1.052935567E-314;
        r3 = r3.getDrawable(r5);
        r3 = (android.graphics.drawable.BitmapDrawable) r3;
        r5 = android.graphics.Shader.TileMode.REPEAT;
        r3.setTileModeXY(r5, r5);
        r5 = r11.backgroundTablet;
        r5.setBackgroundDrawable(r3);
        r3 = r11.backgroundTablet;
        r5 = org.telegram.ui.Components.LayoutHelper.createRelative(r6, r6);
        r0.addView(r3, r5);
        r3 = r11.actionBarLayout;
        r0.addView(r3);
        r3 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r3.<init>(r11);
        r11.rightActionBarLayout = r3;
        r3 = r11.rightActionBarLayout;
        r5 = rightFragmentsStack;
        r3.init(r5);
        r3 = r11.rightActionBarLayout;
        r3.setDelegate(r11);
        r3 = r11.rightActionBarLayout;
        r0.addView(r3);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r11);
        r11.shadowTabletSide = r3;
        r3 = r11.shadowTabletSide;
        r5 = NUM; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r3.setBackgroundColor(r5);
        r3 = r11.shadowTabletSide;
        r0.addView(r3);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r11);
        r11.shadowTablet = r3;
        r3 = r11.shadowTablet;
        r5 = layerFragmentsStack;
        r5 = r5.isEmpty();
        if (r5 == 0) goto L_0x023c;
    L_0x0239:
        r5 = 8;
        goto L_0x023d;
    L_0x023c:
        r5 = 0;
    L_0x023d:
        r3.setVisibility(r5);
        r3 = r11.shadowTablet;
        r5 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r3.setBackgroundColor(r5);
        r3 = r11.shadowTablet;
        r0.addView(r3);
        r3 = r11.shadowTablet;
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4;
        r5.<init>(r11);
        r3.setOnTouchListener(r5);
        r3 = r11.shadowTablet;
        r5 = org.telegram.ui.-$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ.INSTANCE;
        r3.setOnClickListener(r5);
        r3 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r3.<init>(r11);
        r11.layersActionBarLayout = r3;
        r3 = r11.layersActionBarLayout;
        r3.setRemoveActionBarExtraHeight(r1);
        r3 = r11.layersActionBarLayout;
        r5 = r11.shadowTablet;
        r3.setBackgroundView(r5);
        r3 = r11.layersActionBarLayout;
        r3.setUseAlphaAnimations(r1);
        r3 = r11.layersActionBarLayout;
        r5 = NUM; // 0x7var_ float:1.794481E38 double:1.0529355594E-314;
        r3.setBackgroundResource(r5);
        r3 = r11.layersActionBarLayout;
        r5 = layerFragmentsStack;
        r3.init(r5);
        r3 = r11.layersActionBarLayout;
        r3.setDelegate(r11);
        r3 = r11.layersActionBarLayout;
        r5 = r11.drawerLayoutContainer;
        r3.setDrawerLayoutContainer(r5);
        r3 = r11.layersActionBarLayout;
        r5 = layerFragmentsStack;
        r5 = r5.isEmpty();
        if (r5 == 0) goto L_0x029b;
    L_0x029a:
        goto L_0x029c;
    L_0x029b:
        r7 = 0;
    L_0x029c:
        r3.setVisibility(r7);
        r3 = r11.layersActionBarLayout;
        r0.addView(r3);
        goto L_0x02b1;
    L_0x02a5:
        r0 = r11.drawerLayoutContainer;
        r3 = r11.actionBarLayout;
        r5 = new android.view.ViewGroup$LayoutParams;
        r5.<init>(r6, r6);
        r0.addView(r3, r5);
    L_0x02b1:
        r0 = new org.telegram.ui.Components.RecyclerListView;
        r0.<init>(r11);
        r11.sideMenu = r0;
        r0 = r11.sideMenu;
        r3 = new org.telegram.ui.Components.SideMenultItemAnimator;
        r3.<init>(r0);
        r0.setItemAnimator(r3);
        r0 = r11.sideMenu;
        r3 = "chats_menuBackground";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.setBackgroundColor(r3);
        r0 = r11.sideMenu;
        r3 = new androidx.recyclerview.widget.LinearLayoutManager;
        r3.<init>(r11, r1, r2);
        r0.setLayoutManager(r3);
        r0 = r11.sideMenu;
        r0.setAllowItemsInteractionDuringAnimation(r2);
        r0 = r11.sideMenu;
        r3 = new org.telegram.ui.Adapters.DrawerLayoutAdapter;
        r5 = r0.getItemAnimator();
        r3.<init>(r11, r5);
        r11.drawerLayoutAdapter = r3;
        r0.setAdapter(r3);
        r0 = r11.drawerLayoutContainer;
        r3 = r11.sideMenu;
        r0.setDrawerLayout(r3);
        r0 = r11.sideMenu;
        r0 = r0.getLayoutParams();
        r0 = (android.widget.FrameLayout.LayoutParams) r0;
        r3 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        r7 = NUM; // 0x43a00000 float:320.0 double:5.605467397E-315;
        if (r5 == 0) goto L_0x030c;
    L_0x0307:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r7);
        goto L_0x0323;
    L_0x030c:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r3.x;
        r3 = r3.y;
        r3 = java.lang.Math.min(r7, r3);
        r7 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r3 = r3 - r7;
        r3 = java.lang.Math.min(r5, r3);
    L_0x0323:
        r0.width = r3;
        r0.height = r6;
        r3 = r11.sideMenu;
        r3.setLayoutParams(r0);
        r0 = r11.sideMenu;
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0;
        r3.<init>(r11);
        r0.setOnItemClickListener(r3);
        r0 = r11.drawerLayoutContainer;
        r3 = r11.actionBarLayout;
        r0.setParentActionBarLayout(r3);
        r0 = r11.actionBarLayout;
        r3 = r11.drawerLayoutContainer;
        r0.setDrawerLayoutContainer(r3);
        r0 = r11.actionBarLayout;
        r3 = mainFragmentsStack;
        r0.init(r3);
        r0 = r11.actionBarLayout;
        r0.setDelegate(r11);
        org.telegram.ui.ActionBar.Theme.loadWallpaper();
        r0 = new org.telegram.ui.Components.PasscodeView;
        r0.<init>(r11);
        r11.passcodeView = r0;
        r0 = r11.drawerLayoutContainer;
        r3 = r11.passcodeView;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8);
        r0.addView(r3, r5);
        r11.checkCurrentAccount();
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r5 = new java.lang.Object[r1];
        r5[r2] = r11;
        r0.postNotificationName(r3, r5);
        r0 = r11.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r0 = r0.getConnectionState();
        r11.currentConnectionState = r0;
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.reloadInterface;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetPasscode;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated;
        r0.addObserver(r11, r3);
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.screenStateChanged;
        r0.addObserver(r11, r3);
        r0 = r11.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x04f8;
    L_0x03ee:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x040a;
    L_0x03fa:
        r0 = r11.actionBarLayout;
        r3 = new org.telegram.ui.LoginActivity;
        r3.<init>();
        r0.addFragmentToStack(r3);
        r0 = r11.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r2, r2);
        goto L_0x041e;
    L_0x040a:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r3 = r11.sideMenu;
        r0.setSideMenu(r3);
        r3 = r11.actionBarLayout;
        r3.addFragmentToStack(r0);
        r0 = r11.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r1, r2);
    L_0x041e:
        if (r12 == 0) goto L_0x055f;
    L_0x0420:
        r0 = "fragment";
        r0 = r12.getString(r0);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x055f;
    L_0x0428:
        r3 = "args";
        r3 = r12.getBundle(r3);	 Catch:{ Exception -> 0x04f3 }
        r4 = r0.hashCode();	 Catch:{ Exception -> 0x04f3 }
        r5 = 3;
        r7 = 2;
        switch(r4) {
            case -1529105743: goto L_0x046a;
            case -1349522494: goto L_0x0460;
            case 3052376: goto L_0x0456;
            case 98629247: goto L_0x044c;
            case 738950403: goto L_0x0442;
            case 1434631203: goto L_0x0438;
            default: goto L_0x0437;
        };	 Catch:{ Exception -> 0x04f3 }
    L_0x0437:
        goto L_0x0475;
    L_0x0438:
        r4 = "settings";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x0440:
        r0 = 1;
        goto L_0x0476;
    L_0x0442:
        r4 = "channel";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x044a:
        r0 = 3;
        goto L_0x0476;
    L_0x044c:
        r4 = "group";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x0454:
        r0 = 2;
        goto L_0x0476;
    L_0x0456:
        r4 = "chat";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x045e:
        r0 = 0;
        goto L_0x0476;
    L_0x0460:
        r4 = "chat_profile";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x0468:
        r0 = 4;
        goto L_0x0476;
    L_0x046a:
        r4 = "wallpapers";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04f3 }
        if (r0 == 0) goto L_0x0475;
    L_0x0473:
        r0 = 5;
        goto L_0x0476;
    L_0x0475:
        r0 = -1;
    L_0x0476:
        if (r0 == 0) goto L_0x04e0;
    L_0x0478:
        if (r0 == r1) goto L_0x04d1;
    L_0x047a:
        if (r0 == r7) goto L_0x04bd;
    L_0x047c:
        if (r0 == r5) goto L_0x04a9;
    L_0x047e:
        r4 = 4;
        if (r0 == r4) goto L_0x0495;
    L_0x0481:
        r3 = 5;
        if (r0 == r3) goto L_0x0486;
    L_0x0484:
        goto L_0x055f;
    L_0x0486:
        r0 = new org.telegram.ui.WallpapersListActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x0495:
        if (r3 == 0) goto L_0x055f;
    L_0x0497:
        r0 = new org.telegram.ui.ProfileActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        if (r3 == 0) goto L_0x055f;
    L_0x04a4:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x04a9:
        if (r3 == 0) goto L_0x055f;
    L_0x04ab:
        r0 = new org.telegram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        if (r3 == 0) goto L_0x055f;
    L_0x04b8:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x04bd:
        if (r3 == 0) goto L_0x055f;
    L_0x04bf:
        r0 = new org.telegram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        if (r3 == 0) goto L_0x055f;
    L_0x04cc:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x04d1:
        r0 = new org.telegram.ui.SettingsActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>();	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x04e0:
        if (r3 == 0) goto L_0x055f;
    L_0x04e2:
        r0 = new org.telegram.ui.ChatActivity;	 Catch:{ Exception -> 0x04f3 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04f3 }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04f3 }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04f3 }
        if (r3 == 0) goto L_0x055f;
    L_0x04ef:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04f3 }
        goto L_0x055f;
    L_0x04f3:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x055f;
    L_0x04f8:
        r0 = r11.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r2);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r3 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r3 == 0) goto L_0x050d;
    L_0x0506:
        r0 = (org.telegram.ui.DialogsActivity) r0;
        r3 = r11.sideMenu;
        r0.setSideMenu(r3);
    L_0x050d:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0542;
    L_0x0513:
        r0 = r11.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 > r1) goto L_0x0529;
    L_0x051d:
        r0 = r11.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0529;
    L_0x0527:
        r0 = 1;
        goto L_0x052a;
    L_0x0529:
        r0 = 0;
    L_0x052a:
        r3 = r11.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r1) goto L_0x0543;
    L_0x0534:
        r3 = r11.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r2);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x0543;
    L_0x0540:
        r0 = 0;
        goto L_0x0543;
    L_0x0542:
        r0 = 1;
    L_0x0543:
        r3 = r11.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r1) goto L_0x055a;
    L_0x054d:
        r3 = r11.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r2);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x055a;
    L_0x0559:
        r0 = 0;
    L_0x055a:
        r3 = r11.drawerLayoutContainer;
        r3.setAllowOpenDrawer(r0, r2);
    L_0x055f:
        r11.checkLayout();
        r11.checkSystemBarColors();
        r0 = r11.getIntent();
        if (r12 == 0) goto L_0x056d;
    L_0x056b:
        r12 = 1;
        goto L_0x056e;
    L_0x056d:
        r12 = 0;
    L_0x056e:
        r11.handleIntent(r0, r2, r12, r2);
        r12 = android.os.Build.DISPLAY;	 Catch:{ Exception -> 0x05d2 }
        r0 = android.os.Build.USER;	 Catch:{ Exception -> 0x05d2 }
        r2 = "";
        if (r12 == 0) goto L_0x057e;
    L_0x0579:
        r12 = r12.toLowerCase();	 Catch:{ Exception -> 0x05d2 }
        goto L_0x057f;
    L_0x057e:
        r12 = r2;
    L_0x057f:
        if (r0 == 0) goto L_0x0585;
    L_0x0581:
        r2 = r12.toLowerCase();	 Catch:{ Exception -> 0x05d2 }
    L_0x0585:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x05d2 }
        if (r0 == 0) goto L_0x05a5;
    L_0x0589:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x05d2 }
        r0.<init>();	 Catch:{ Exception -> 0x05d2 }
        r3 = "OS name ";
        r0.append(r3);	 Catch:{ Exception -> 0x05d2 }
        r0.append(r12);	 Catch:{ Exception -> 0x05d2 }
        r3 = " ";
        r0.append(r3);	 Catch:{ Exception -> 0x05d2 }
        r0.append(r2);	 Catch:{ Exception -> 0x05d2 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x05d2 }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x05d2 }
    L_0x05a5:
        r0 = "flyme";
        r12 = r12.contains(r0);	 Catch:{ Exception -> 0x05d2 }
        if (r12 != 0) goto L_0x05b5;
    L_0x05ad:
        r12 = "flyme";
        r12 = r2.contains(r12);	 Catch:{ Exception -> 0x05d2 }
        if (r12 == 0) goto L_0x05d6;
    L_0x05b5:
        org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r1;	 Catch:{ Exception -> 0x05d2 }
        r12 = r11.getWindow();	 Catch:{ Exception -> 0x05d2 }
        r12 = r12.getDecorView();	 Catch:{ Exception -> 0x05d2 }
        r12 = r12.getRootView();	 Catch:{ Exception -> 0x05d2 }
        r0 = r12.getViewTreeObserver();	 Catch:{ Exception -> 0x05d2 }
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0;	 Catch:{ Exception -> 0x05d2 }
        r2.<init>(r12);	 Catch:{ Exception -> 0x05d2 }
        r11.onGlobalLayoutListener = r2;	 Catch:{ Exception -> 0x05d2 }
        r0.addOnGlobalLayoutListener(r2);	 Catch:{ Exception -> 0x05d2 }
        goto L_0x05d6;
    L_0x05d2:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
    L_0x05d6:
        r12 = org.telegram.messenger.MediaController.getInstance();
        r12.setBaseActivity(r11, r1);
        return;
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

    public /* synthetic */ void lambda$onCreate$2$LaunchActivity(View view, int i) {
        int i2;
        if (i == 0) {
            DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
            drawerLayoutAdapter.setAccountsShowed(drawerLayoutAdapter.isAccountsShowed() ^ 1, true);
        } else if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (view instanceof DrawerAddCell) {
            i2 = -1;
            for (i = 0; i < 3; i++) {
                if (!UserConfig.getInstance(i).isClientActivated()) {
                    i2 = i;
                    break;
                }
            }
            if (i2 >= 0) {
                lambda$runLinkRequest$32$LaunchActivity(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            i2 = this.drawerLayoutAdapter.getId(i);
            Bundle bundle;
            if (i2 == 2) {
                lambda$runLinkRequest$32$LaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 3) {
                bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$32$LaunchActivity(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                String str = "channel_intro";
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean(str, false)) {
                    lambda$runLinkRequest$32$LaunchActivity(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean(str, true).commit();
                } else {
                    bundle = new Bundle();
                    bundle.putInt("step", 0);
                    lambda$runLinkRequest$32$LaunchActivity(new ChannelCreateActivity(bundle));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 6) {
                lambda$runLinkRequest$32$LaunchActivity(new ContactsActivity(null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 7) {
                lambda$runLinkRequest$32$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 8) {
                lambda$runLinkRequest$32$LaunchActivity(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 10) {
                lambda$runLinkRequest$32$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 11) {
                bundle = new Bundle();
                bundle.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$32$LaunchActivity(new ChatActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 12) {
                lambda$runLinkRequest$32$LaunchActivity(getCurrentWalletFragment(null));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    static /* synthetic */ void lambda$onCreate$3(View view) {
        int measuredHeight = view.getMeasuredHeight();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("height = ");
        stringBuilder.append(measuredHeight);
        stringBuilder.append(" displayHeight = ");
        stringBuilder.append(AndroidUtilities.displaySize.y);
        FileLog.d(stringBuilder.toString());
        if (VERSION.SDK_INT >= 21) {
            measuredHeight -= AndroidUtilities.statusBarHeight;
        }
        if (measuredHeight > AndroidUtilities.dp(100.0f) && measuredHeight < AndroidUtilities.displaySize.y) {
            int dp = AndroidUtilities.dp(100.0f) + measuredHeight;
            Point point = AndroidUtilities.displaySize;
            if (dp > point.y) {
                point.y = measuredHeight;
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("fix display size y to ");
                    stringBuilder2.append(AndroidUtilities.displaySize.y);
                    FileLog.d(stringBuilder2.toString());
                }
            }
        }
    }

    private void checkSystemBarColors() {
        if (VERSION.SDK_INT >= 23) {
            boolean z = true;
            AndroidUtilities.setLightStatusBar(getWindow(), Theme.getColor("actionBarDefault", null, true) == -1);
            if (VERSION.SDK_INT >= 26) {
                Window window = getWindow();
                int color = Theme.getColor("windowBackgroundGray", null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
                    window = getWindow();
                    if (computePerceivedBrightness < 0.721f) {
                        z = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window, z);
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
            if (UserConfig.getInstance(i).unacceptedTermsOfService != null) {
                showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
            }
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int i = 0;
        while (i < 3) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                break;
            }
            i++;
        }
        i = -1;
        TermsOfServiceView termsOfServiceView = this.termsOfServiceView;
        if (termsOfServiceView != null) {
            termsOfServiceView.setVisibility(8);
        }
        if (i != -1) {
            switchToAccount(i, true);
            return;
        }
        DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
        if (drawerLayoutAdapter != null) {
            drawerLayoutAdapter.notifyDataSetChanged();
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

    private BaseFragment getCurrentWalletFragment(String str) {
        UserConfig instance = UserConfig.getInstance(this.currentAccount);
        if (TextUtils.isEmpty(instance.tonEncryptedData)) {
            if (TextUtils.isEmpty(str)) {
                return new WalletCreateActivity(0);
            }
            Builder builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletTonLinkNoWalletText", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setPositiveButton(LocaleController.getString("WalletTonLinkNoWalletCreateWallet", NUM), new -$$Lambda$LaunchActivity$Uwf3yr5pdJtIdIgdDmTr8oCqFC0(this));
            builder.show();
            return null;
        } else if (!instance.tonCreationFinished) {
            BaseFragment walletCreateActivity = new WalletCreateActivity(1);
            walletCreateActivity.setResumeCreation();
            return walletCreateActivity;
        } else if (TextUtils.isEmpty(str)) {
            return new WalletActivity();
        } else {
            return new WalletActivity(str);
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
            BaseFragment baseFragment;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                this.tabletFullSize = true;
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                        baseFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(0);
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
                    baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(1);
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).setIgnoreAttachOnPause(true);
                    }
                    baseFragment.onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.rightActionBarLayout.fragmentsStack.add(baseFragment);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            ActionBarLayout actionBarLayout = this.rightActionBarLayout;
            actionBarLayout.setVisibility(actionBarLayout.fragmentsStack.isEmpty() ? 8 : 0);
            this.backgroundTablet.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 0 : 8);
            FrameLayout frameLayout = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    private void showUpdateActivity(int i, TL_help_appUpdate tL_help_appUpdate, boolean z) {
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

    private void showTosActivity(int i, TL_help_termsOfService tL_help_termsOfService) {
        if (this.termsOfServiceView == null) {
            this.termsOfServiceView = new TermsOfServiceView(this);
            this.termsOfServiceView.setAlpha(0.0f);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int i) {
                    UserConfig.getInstance(i).unacceptedTermsOfService = null;
                    UserConfig.getInstance(i).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (LaunchActivity.mainFragmentsStack.size() > 0) {
                        ((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)).onResume();
                    }
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new -$$Lambda$LaunchActivity$4$JpwiY2fSImRDlww0c6RBFGYYYgI(this)).start();
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
        TL_help_termsOfService tL_help_termsOfService2 = UserConfig.getInstance(i).unacceptedTermsOfService;
        if (tL_help_termsOfService2 != tL_help_termsOfService && (tL_help_termsOfService2 == null || !tL_help_termsOfService2.id.data.equals(tL_help_termsOfService.id.data))) {
            UserConfig.getInstance(i).unacceptedTermsOfService = tL_help_termsOfService;
            UserConfig.getInstance(i).saveConfig(false);
        }
        this.termsOfServiceView.show(i, tL_help_termsOfService);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.termsOfServiceView.animate().alpha(1.0f).setDuration(150).setInterpolator(AndroidUtilities.decelerateInterpolator).setListener(null).start();
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
            this.passcodeView.setDelegate(new -$$Lambda$LaunchActivity$pahSs2Gpgjl_VyrTkzxG38Y0dZQ(this));
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

    /* JADX WARNING: Removed duplicated region for block: B:115:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x033e  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0f6b A:{Catch:{ all -> 0x0var_, all -> 0x0var_, Exception -> 0x0f6f }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x067c A:{SYNTHETIC, Splitter:B:301:0x067c} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03b1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b07  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03b1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b07  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03b1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b07  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0eda A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x141e  */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:797:0x13c6  */
    /* JADX WARNING: Removed duplicated region for block: B:802:0x13de  */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x141e  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4c }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:482:0x0CLASSNAME */
    /* JADX WARNING: Missing exception handler attribute for start block: B:628:0x0var_ */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1436  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x148a  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x14cf  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0b8e A:{Catch:{ Exception -> 0x0b9a }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:449:0x0b82 */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x086a A:{Catch:{ Exception -> 0x0876 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:348:0x085e */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0907 A:{Catch:{ Exception -> 0x0913 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:377:0x08fb */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(10:455|(2:457|(3:461|(4:464|(2:466|858)(2:467|(2:469|857)(1:859))|470|462)|856))|471|(1:473)(1:474)|475|476|(2:478|(1:480))(1:481)|482|483|(1:485)) */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:447|448|449|450|(1:452)|453) */
    /* JADX WARNING: Can't wrap try/catch for region: R(9:354|(2:356|(3:360|(4:363|(2:365|854)(2:366|(2:368|853)(1:855))|369|361)|852))|370|371|372|(2:374|(1:376))|377|378|(1:380)) */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:346|347|348|349|(1:351)|352) */
    /* JADX WARNING: Missing block: B:12:0x0040, code skipped:
            if ("android.intent.action.MAIN".equals(r48.getAction()) == false) goto L_0x0042;
     */
    /* JADX WARNING: Missing block: B:112:0x0230, code skipped:
            if (r15.sendingText == null) goto L_0x012a;
     */
    /* JADX WARNING: Missing block: B:535:0x0dc0, code skipped:
            if (r1.intValue() == 0) goto L_0x0dc2;
     */
    /* JADX WARNING: Missing block: B:690:0x1132, code skipped:
            if (r4.checkCanOpenChat(r0, (org.telegram.ui.ActionBar.BaseFragment) r5.get(r5.size() - 1)) != false) goto L_0x1136;
     */
    /* JADX WARNING: Missing block: B:693:0x1145, code skipped:
            if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x1147;
     */
    /* JADX WARNING: Missing block: B:695:0x1149, code skipped:
            r13 = false;
     */
    /* JADX WARNING: Missing block: B:704:0x117e, code skipped:
            if (r0.checkCanOpenChat(r5, (org.telegram.ui.ActionBar.BaseFragment) r4.get(r4.size() - 1)) != false) goto L_0x1180;
     */
    /* JADX WARNING: Missing block: B:706:0x1191, code skipped:
            if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r5), false, true, true, false) != false) goto L_0x1147;
     */
    /* JADX WARNING: Missing block: B:788:0x1394, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x13b2;
     */
    /* JADX WARNING: Missing block: B:792:0x13b0, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x13b2;
     */
    private boolean handleIntent(android.content.Intent r48, boolean r49, boolean r50, boolean r51) {
        /*
        r47 = this;
        r15 = r47;
        r14 = r48;
        r0 = r50;
        r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r47, r48);
        r13 = 1;
        if (r1 == 0) goto L_0x0023;
    L_0x000d:
        r0 = r15.actionBarLayout;
        r0.showLastFragment();
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0022;
    L_0x0018:
        r0 = r15.layersActionBarLayout;
        r0.showLastFragment();
        r0 = r15.rightActionBarLayout;
        r0.showLastFragment();
    L_0x0022:
        return r13;
    L_0x0023:
        r1 = org.telegram.ui.PhotoViewer.hasInstance();
        r12 = 0;
        if (r1 == 0) goto L_0x0049;
    L_0x002a:
        r1 = org.telegram.ui.PhotoViewer.getInstance();
        r1 = r1.isVisible();
        if (r1 == 0) goto L_0x0049;
    L_0x0034:
        if (r14 == 0) goto L_0x0042;
    L_0x0036:
        r1 = r48.getAction();
        r2 = "android.intent.action.MAIN";
        r1 = r2.equals(r1);
        if (r1 != 0) goto L_0x0049;
    L_0x0042:
        r1 = org.telegram.ui.PhotoViewer.getInstance();
        r1.closePhoto(r12, r13);
    L_0x0049:
        r1 = r48.getFlags();
        r11 = new int[r13];
        r2 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = "currentAccount";
        r2 = r14.getIntExtra(r3, r2);
        r11[r12] = r2;
        r2 = r11[r12];
        r15.switchToAccount(r2, r13);
        if (r51 != 0) goto L_0x007f;
    L_0x0060:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13);
        if (r2 != 0) goto L_0x006a;
    L_0x0066:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x007f;
    L_0x006a:
        r47.showPasscodeActivity();
        r15.passcodeSaveIntent = r14;
        r10 = r49;
        r15.passcodeSaveIntentIsNew = r10;
        r15.passcodeSaveIntentIsRestore = r0;
        r0 = r15.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0.saveConfig(r12);
        return r12;
    L_0x007f:
        r10 = r49;
        r2 = org.telegram.messenger.SharedConfig.directShare;
        r3 = "hash";
        r8 = 0;
        if (r2 == 0) goto L_0x00ad;
    L_0x0089:
        if (r14 == 0) goto L_0x00ad;
    L_0x008b:
        r2 = r48.getExtras();
        if (r2 == 0) goto L_0x00ad;
    L_0x0091:
        r2 = r48.getExtras();
        r4 = "dialogId";
        r4 = r2.getLong(r4, r8);
        r2 = r48.getExtras();
        r6 = r2.getLong(r3, r8);
        r16 = org.telegram.messenger.SharedConfig.directShareHash;
        r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x00aa;
    L_0x00a9:
        goto L_0x00ad;
    L_0x00aa:
        r21 = r4;
        goto L_0x00af;
    L_0x00ad:
        r21 = r8;
    L_0x00af:
        r7 = 0;
        r15.photoPathsArray = r7;
        r15.videoPath = r7;
        r15.sendingText = r7;
        r15.documentsPathsArray = r7;
        r15.documentsOriginalPathsArray = r7;
        r15.documentsMimeType = r7;
        r15.documentsUrisArray = r7;
        r15.contactsToSend = r7;
        r15.contactsToSendUri = r7;
        r2 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r1 = r1 & r2;
        r6 = 2;
        r5 = 3;
        if (r1 != 0) goto L_0x10e1;
    L_0x00c9:
        if (r14 == 0) goto L_0x10e1;
    L_0x00cb:
        r1 = r48.getAction();
        if (r1 == 0) goto L_0x10e1;
    L_0x00d1:
        if (r0 != 0) goto L_0x10e1;
    L_0x00d3:
        r0 = r48.getAction();
        r1 = "android.intent.action.SEND";
        r0 = r1.equals(r0);
        r1 = "\n";
        r2 = "";
        if (r0 == 0) goto L_0x0242;
    L_0x00e3:
        r0 = r48.getType();
        if (r0 == 0) goto L_0x012d;
    L_0x00e9:
        r3 = "text/x-vcard";
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x012d;
    L_0x00f1:
        r1 = r48.getExtras();	 Catch:{ Exception -> 0x0126 }
        r2 = "android.intent.extra.STREAM";
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x0126 }
        r1 = (android.net.Uri) r1;	 Catch:{ Exception -> 0x0126 }
        if (r1 == 0) goto L_0x012a;
    L_0x00ff:
        r2 = r15.currentAccount;	 Catch:{ Exception -> 0x0126 }
        r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r1, r2, r12, r7, r7);	 Catch:{ Exception -> 0x0126 }
        r15.contactsToSend = r2;	 Catch:{ Exception -> 0x0126 }
        r2 = r15.contactsToSend;	 Catch:{ Exception -> 0x0126 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0126 }
        r3 = 5;
        if (r2 <= r3) goto L_0x0122;
    L_0x0110:
        r15.contactsToSend = r7;	 Catch:{ Exception -> 0x0126 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0126 }
        r2.<init>();	 Catch:{ Exception -> 0x0126 }
        r15.documentsUrisArray = r2;	 Catch:{ Exception -> 0x0126 }
        r2 = r15.documentsUrisArray;	 Catch:{ Exception -> 0x0126 }
        r2.add(r1);	 Catch:{ Exception -> 0x0126 }
        r15.documentsMimeType = r0;	 Catch:{ Exception -> 0x0126 }
        goto L_0x0234;
    L_0x0122:
        r15.contactsToSendUri = r1;	 Catch:{ Exception -> 0x0126 }
        goto L_0x0234;
    L_0x0126:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x012a:
        r0 = 1;
        goto L_0x0235;
    L_0x012d:
        r3 = "android.intent.extra.TEXT";
        r3 = r14.getStringExtra(r3);
        if (r3 != 0) goto L_0x0141;
    L_0x0135:
        r4 = "android.intent.extra.TEXT";
        r4 = r14.getCharSequenceExtra(r4);
        if (r4 == 0) goto L_0x0141;
    L_0x013d:
        r3 = r4.toString();
    L_0x0141:
        r4 = "android.intent.extra.SUBJECT";
        r4 = r14.getStringExtra(r4);
        r16 = android.text.TextUtils.isEmpty(r3);
        if (r16 != 0) goto L_0x0178;
    L_0x014d:
        r8 = "http://";
        r8 = r3.startsWith(r8);
        if (r8 != 0) goto L_0x015d;
    L_0x0155:
        r8 = "https://";
        r8 = r3.startsWith(r8);
        if (r8 == 0) goto L_0x0175;
    L_0x015d:
        r8 = android.text.TextUtils.isEmpty(r4);
        if (r8 != 0) goto L_0x0175;
    L_0x0163:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r4);
        r8.append(r1);
        r8.append(r3);
        r3 = r8.toString();
    L_0x0175:
        r15.sendingText = r3;
        goto L_0x0180;
    L_0x0178:
        r1 = android.text.TextUtils.isEmpty(r4);
        if (r1 != 0) goto L_0x0180;
    L_0x017e:
        r15.sendingText = r4;
    L_0x0180:
        r1 = "android.intent.extra.STREAM";
        r1 = r14.getParcelableExtra(r1);
        if (r1 == 0) goto L_0x022e;
    L_0x0188:
        r3 = r1 instanceof android.net.Uri;
        if (r3 != 0) goto L_0x0194;
    L_0x018c:
        r1 = r1.toString();
        r1 = android.net.Uri.parse(r1);
    L_0x0194:
        r1 = (android.net.Uri) r1;
        if (r1 == 0) goto L_0x01a0;
    L_0x0198:
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);
        if (r3 == 0) goto L_0x01a0;
    L_0x019e:
        r3 = 1;
        goto L_0x01a1;
    L_0x01a0:
        r3 = 0;
    L_0x01a1:
        if (r3 != 0) goto L_0x022c;
    L_0x01a3:
        if (r1 == 0) goto L_0x01d7;
    L_0x01a5:
        if (r0 == 0) goto L_0x01af;
    L_0x01a7:
        r4 = "image/";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x01bf;
    L_0x01af:
        r4 = r1.toString();
        r4 = r4.toLowerCase();
        r8 = ".jpg";
        r4 = r4.endsWith(r8);
        if (r4 == 0) goto L_0x01d7;
    L_0x01bf:
        r0 = r15.photoPathsArray;
        if (r0 != 0) goto L_0x01ca;
    L_0x01c3:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.photoPathsArray = r0;
    L_0x01ca:
        r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;
        r0.<init>();
        r0.uri = r1;
        r1 = r15.photoPathsArray;
        r1.add(r0);
        goto L_0x022c;
    L_0x01d7:
        r4 = org.telegram.messenger.AndroidUtilities.getPath(r1);
        if (r4 == 0) goto L_0x021a;
    L_0x01dd:
        r8 = "file:";
        r8 = r4.startsWith(r8);
        if (r8 == 0) goto L_0x01eb;
    L_0x01e5:
        r8 = "file://";
        r4 = r4.replace(r8, r2);
    L_0x01eb:
        if (r0 == 0) goto L_0x01f9;
    L_0x01ed:
        r2 = "video/";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x01f9;
    L_0x01f6:
        r15.videoPath = r4;
        goto L_0x022c;
    L_0x01f9:
        r0 = r15.documentsPathsArray;
        if (r0 != 0) goto L_0x020b;
    L_0x01fd:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.documentsPathsArray = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.documentsOriginalPathsArray = r0;
    L_0x020b:
        r0 = r15.documentsPathsArray;
        r0.add(r4);
        r0 = r15.documentsOriginalPathsArray;
        r1 = r1.toString();
        r0.add(r1);
        goto L_0x022c;
    L_0x021a:
        r2 = r15.documentsUrisArray;
        if (r2 != 0) goto L_0x0225;
    L_0x021e:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r15.documentsUrisArray = r2;
    L_0x0225:
        r2 = r15.documentsUrisArray;
        r2.add(r1);
        r15.documentsMimeType = r0;
    L_0x022c:
        r0 = r3;
        goto L_0x0235;
    L_0x022e:
        r0 = r15.sendingText;
        if (r0 != 0) goto L_0x0234;
    L_0x0232:
        goto L_0x012a;
    L_0x0234:
        r0 = 0;
    L_0x0235:
        if (r0 == 0) goto L_0x0347;
    L_0x0237:
        r0 = "Unsupported content";
        r0 = android.widget.Toast.makeText(r15, r0, r12);
        r0.show();
        goto L_0x0347;
    L_0x0242:
        r0 = r48.getAction();
        r4 = "android.intent.action.SEND_MULTIPLE";
        r0 = r4.equals(r0);
        if (r0 == 0) goto L_0x034f;
    L_0x024e:
        r0 = "android.intent.extra.STREAM";
        r0 = r14.getParcelableArrayListExtra(r0);	 Catch:{ Exception -> 0x0337 }
        r1 = r48.getType();	 Catch:{ Exception -> 0x0337 }
        if (r0 == 0) goto L_0x028b;
    L_0x025a:
        r3 = 0;
    L_0x025b:
        r4 = r0.size();	 Catch:{ Exception -> 0x0337 }
        if (r3 >= r4) goto L_0x0284;
    L_0x0261:
        r4 = r0.get(r3);	 Catch:{ Exception -> 0x0337 }
        r4 = (android.os.Parcelable) r4;	 Catch:{ Exception -> 0x0337 }
        r8 = r4 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0337 }
        if (r8 != 0) goto L_0x0273;
    L_0x026b:
        r4 = r4.toString();	 Catch:{ Exception -> 0x0337 }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0337 }
    L_0x0273:
        r4 = (android.net.Uri) r4;	 Catch:{ Exception -> 0x0337 }
        if (r4 == 0) goto L_0x0282;
    L_0x0277:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r4);	 Catch:{ Exception -> 0x0337 }
        if (r4 == 0) goto L_0x0282;
    L_0x027d:
        r0.remove(r3);	 Catch:{ Exception -> 0x0337 }
        r3 = r3 + -1;
    L_0x0282:
        r3 = r3 + r13;
        goto L_0x025b;
    L_0x0284:
        r3 = r0.isEmpty();	 Catch:{ Exception -> 0x0337 }
        if (r3 == 0) goto L_0x028b;
    L_0x028a:
        r0 = r7;
    L_0x028b:
        if (r0 == 0) goto L_0x033b;
    L_0x028d:
        if (r1 == 0) goto L_0x02cc;
    L_0x028f:
        r3 = "image/";
        r3 = r1.startsWith(r3);	 Catch:{ Exception -> 0x0337 }
        if (r3 == 0) goto L_0x02cc;
    L_0x0297:
        r1 = 0;
    L_0x0298:
        r2 = r0.size();	 Catch:{ Exception -> 0x0337 }
        if (r1 >= r2) goto L_0x0335;
    L_0x029e:
        r2 = r0.get(r1);	 Catch:{ Exception -> 0x0337 }
        r2 = (android.os.Parcelable) r2;	 Catch:{ Exception -> 0x0337 }
        r3 = r2 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0337 }
        if (r3 != 0) goto L_0x02b0;
    L_0x02a8:
        r2 = r2.toString();	 Catch:{ Exception -> 0x0337 }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0337 }
    L_0x02b0:
        r2 = (android.net.Uri) r2;	 Catch:{ Exception -> 0x0337 }
        r3 = r15.photoPathsArray;	 Catch:{ Exception -> 0x0337 }
        if (r3 != 0) goto L_0x02bd;
    L_0x02b6:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0337 }
        r3.<init>();	 Catch:{ Exception -> 0x0337 }
        r15.photoPathsArray = r3;	 Catch:{ Exception -> 0x0337 }
    L_0x02bd:
        r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;	 Catch:{ Exception -> 0x0337 }
        r3.<init>();	 Catch:{ Exception -> 0x0337 }
        r3.uri = r2;	 Catch:{ Exception -> 0x0337 }
        r2 = r15.photoPathsArray;	 Catch:{ Exception -> 0x0337 }
        r2.add(r3);	 Catch:{ Exception -> 0x0337 }
        r1 = r1 + 1;
        goto L_0x0298;
    L_0x02cc:
        r3 = 0;
    L_0x02cd:
        r4 = r0.size();	 Catch:{ Exception -> 0x0337 }
        if (r3 >= r4) goto L_0x0335;
    L_0x02d3:
        r4 = r0.get(r3);	 Catch:{ Exception -> 0x0337 }
        r4 = (android.os.Parcelable) r4;	 Catch:{ Exception -> 0x0337 }
        r8 = r4 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0337 }
        if (r8 != 0) goto L_0x02e5;
    L_0x02dd:
        r4 = r4.toString();	 Catch:{ Exception -> 0x0337 }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0337 }
    L_0x02e5:
        r8 = r4;
        r8 = (android.net.Uri) r8;	 Catch:{ Exception -> 0x0337 }
        r9 = org.telegram.messenger.AndroidUtilities.getPath(r8);	 Catch:{ Exception -> 0x0337 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0337 }
        if (r4 != 0) goto L_0x02f3;
    L_0x02f2:
        r4 = r9;
    L_0x02f3:
        if (r9 == 0) goto L_0x0320;
    L_0x02f5:
        r8 = "file:";
        r8 = r9.startsWith(r8);	 Catch:{ Exception -> 0x0337 }
        if (r8 == 0) goto L_0x0303;
    L_0x02fd:
        r8 = "file://";
        r9 = r9.replace(r8, r2);	 Catch:{ Exception -> 0x0337 }
    L_0x0303:
        r8 = r15.documentsPathsArray;	 Catch:{ Exception -> 0x0337 }
        if (r8 != 0) goto L_0x0315;
    L_0x0307:
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0337 }
        r8.<init>();	 Catch:{ Exception -> 0x0337 }
        r15.documentsPathsArray = r8;	 Catch:{ Exception -> 0x0337 }
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0337 }
        r8.<init>();	 Catch:{ Exception -> 0x0337 }
        r15.documentsOriginalPathsArray = r8;	 Catch:{ Exception -> 0x0337 }
    L_0x0315:
        r8 = r15.documentsPathsArray;	 Catch:{ Exception -> 0x0337 }
        r8.add(r9);	 Catch:{ Exception -> 0x0337 }
        r8 = r15.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x0337 }
        r8.add(r4);	 Catch:{ Exception -> 0x0337 }
        goto L_0x0332;
    L_0x0320:
        r4 = r15.documentsUrisArray;	 Catch:{ Exception -> 0x0337 }
        if (r4 != 0) goto L_0x032b;
    L_0x0324:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0337 }
        r4.<init>();	 Catch:{ Exception -> 0x0337 }
        r15.documentsUrisArray = r4;	 Catch:{ Exception -> 0x0337 }
    L_0x032b:
        r4 = r15.documentsUrisArray;	 Catch:{ Exception -> 0x0337 }
        r4.add(r8);	 Catch:{ Exception -> 0x0337 }
        r15.documentsMimeType = r1;	 Catch:{ Exception -> 0x0337 }
    L_0x0332:
        r3 = r3 + 1;
        goto L_0x02cd;
    L_0x0335:
        r0 = 0;
        goto L_0x033c;
    L_0x0337:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x033b:
        r0 = 1;
    L_0x033c:
        if (r0 == 0) goto L_0x0347;
    L_0x033e:
        r0 = "Unsupported content";
        r0 = android.widget.Toast.makeText(r15, r0, r12);
        r0.show();
    L_0x0347:
        r6 = r11;
        r1 = r14;
        r2 = r15;
        r3 = 0;
        r31 = 0;
        goto L_0x10e7;
    L_0x034f:
        r0 = r48.getAction();
        r4 = "android.intent.action.VIEW";
        r0 = r4.equals(r0);
        if (r0 == 0) goto L_0x1012;
    L_0x035b:
        r0 = r48.getData();
        if (r0 == 0) goto L_0x0ff7;
    L_0x0361:
        r4 = r0.getScheme();
        if (r4 == 0) goto L_0x0e9a;
    L_0x0367:
        r9 = r4.hashCode();
        r8 = 3699; // 0xe73 float:5.183E-42 double:1.8275E-320;
        if (r9 == r8) goto L_0x039e;
    L_0x036f:
        r8 = 115027; // 0x1CLASSNAME float:1.61187E-40 double:5.6831E-319;
        if (r9 == r8) goto L_0x0393;
    L_0x0374:
        r8 = 3213448; // 0x310888 float:4.503E-39 double:1.5876543E-317;
        if (r9 == r8) goto L_0x0389;
    L_0x0379:
        r8 = 99617003; // 0x5var_eb float:2.2572767E-35 double:4.9217339E-316;
        if (r9 == r8) goto L_0x037f;
    L_0x037e:
        goto L_0x03a9;
    L_0x037f:
        r8 = "https";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x03a9;
    L_0x0387:
        r4 = 1;
        goto L_0x03aa;
    L_0x0389:
        r8 = "http";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x03a9;
    L_0x0391:
        r4 = 0;
        goto L_0x03aa;
    L_0x0393:
        r8 = "ton";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x03a9;
    L_0x039c:
        r4 = 2;
        goto L_0x03aa;
    L_0x039e:
        r8 = "tg";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x03a9;
    L_0x03a7:
        r4 = 3;
        goto L_0x03aa;
    L_0x03a9:
        r4 = -1;
    L_0x03aa:
        r9 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r8 = 16;
        r7 = 6;
        if (r4 == 0) goto L_0x0ae1;
    L_0x03b1:
        if (r4 == r13) goto L_0x0ae1;
    L_0x03b3:
        if (r4 == r6) goto L_0x0a76;
    L_0x03b5:
        if (r4 == r5) goto L_0x03b9;
    L_0x03b7:
        goto L_0x0e9a;
    L_0x03b9:
        r0 = r0.toString();
        r4 = "tg:resolve";
        r4 = r0.startsWith(r4);
        r6 = "scope";
        r5 = "tg://telegram.org";
        if (r4 != 0) goto L_0x0980;
    L_0x03cb:
        r4 = "tg://resolve";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03d6;
    L_0x03d4:
        goto L_0x0980;
    L_0x03d6:
        r4 = "tg:privatepost";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0928;
    L_0x03df:
        r4 = "tg://privatepost";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03ea;
    L_0x03e8:
        goto L_0x0928;
    L_0x03ea:
        r4 = "tg:bg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x07d7;
    L_0x03f3:
        r4 = "tg://bg";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03fe;
    L_0x03fc:
        goto L_0x07d7;
    L_0x03fe:
        r4 = "tg:join";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x07b8;
    L_0x0407:
        r4 = "tg://join";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0412;
    L_0x0410:
        goto L_0x07b8;
    L_0x0412:
        r4 = "tg:addstickers";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0796;
    L_0x041b:
        r4 = "tg://addstickers";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0426;
    L_0x0424:
        goto L_0x0796;
    L_0x0426:
        r4 = "tg:msg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0708;
    L_0x042f:
        r4 = "tg://msg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0708;
    L_0x0438:
        r4 = "tg://share";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0708;
    L_0x0441:
        r4 = "tg:share";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x044c;
    L_0x044a:
        goto L_0x0708;
    L_0x044c:
        r1 = "tg:confirmphone";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x06e4;
    L_0x0455:
        r1 = "tg://confirmphone";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0460;
    L_0x045e:
        goto L_0x06e4;
    L_0x0460:
        r1 = "tg:login";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x06ab;
    L_0x0469:
        r1 = "tg://login";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0474;
    L_0x0472:
        goto L_0x06ab;
    L_0x0474:
        r1 = "tg:openmessage";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x0643;
    L_0x047d:
        r1 = "tg://openmessage";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0488;
    L_0x0486:
        goto L_0x0643;
    L_0x0488:
        r1 = "tg:passport";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x05cc;
    L_0x0491:
        r1 = "tg://passport";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x05cc;
    L_0x049a:
        r1 = "tg:secureid";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x04a5;
    L_0x04a3:
        goto L_0x05cc;
    L_0x04a5:
        r1 = "tg:setlanguage";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x05a1;
    L_0x04ae:
        r1 = "tg://setlanguage";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x04b9;
    L_0x04b7:
        goto L_0x05a1;
    L_0x04b9:
        r1 = "tg:addtheme";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x0572;
    L_0x04c2:
        r1 = "tg://addtheme";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x04cd;
    L_0x04cb:
        goto L_0x0572;
    L_0x04cd:
        r1 = "tg:settings";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x050b;
    L_0x04d6:
        r1 = "tg://settings";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x04e0;
    L_0x04df:
        goto L_0x050b;
    L_0x04e0:
        r1 = "tg://";
        r0 = r0.replace(r1, r2);
        r1 = "tg:";
        r0 = r0.replace(r1, r2);
        r1 = 63;
        r1 = r0.indexOf(r1);
        if (r1 < 0) goto L_0x04fa;
    L_0x04f6:
        r0 = r0.substring(r12, r1);
    L_0x04fa:
        r24 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        goto L_0x0ea8;
    L_0x050b:
        r1 = "themes";
        r1 = r0.contains(r1);
        if (r1 == 0) goto L_0x053f;
    L_0x0514:
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r36 = 0;
        r37 = 2;
        goto L_0x0ec2;
    L_0x053f:
        r1 = "devices";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x0ade;
    L_0x0547:
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r36 = 0;
        r37 = 3;
        goto L_0x0ec2;
    L_0x0572:
        r1 = "tg:addtheme";
        r0 = r0.replace(r1, r5);
        r1 = "tg://addtheme";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "slug";
        r0 = r0.getQueryParameter(r1);
        r27 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        goto L_0x0eae;
    L_0x05a1:
        r1 = "tg:setlanguage";
        r0 = r0.replace(r1, r5);
        r1 = "tg://setlanguage";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "lang";
        r0 = r0.getQueryParameter(r1);
        r25 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        goto L_0x0eaa;
    L_0x05cc:
        r1 = "tg:passport";
        r0 = r0.replace(r1, r5);
        r1 = "tg://passport";
        r0 = r0.replace(r1, r5);
        r1 = "tg:secureid";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = r0.getQueryParameter(r6);
        r4 = android.text.TextUtils.isEmpty(r2);
        if (r4 != 0) goto L_0x0612;
    L_0x05f4:
        r4 = "{";
        r4 = r2.startsWith(r4);
        if (r4 == 0) goto L_0x0612;
    L_0x05fd:
        r4 = "}";
        r4 = r2.endsWith(r4);
        if (r4 == 0) goto L_0x0612;
    L_0x0606:
        r4 = "nonce";
        r4 = r0.getQueryParameter(r4);
        r5 = "nonce";
        r1.put(r5, r4);
        goto L_0x061d;
    L_0x0612:
        r4 = "payload";
        r4 = r0.getQueryParameter(r4);
        r5 = "payload";
        r1.put(r5, r4);
    L_0x061d:
        r4 = "bot_id";
        r4 = r0.getQueryParameter(r4);
        r5 = "bot_id";
        r1.put(r5, r4);
        r1.put(r6, r2);
        r2 = "public_key";
        r2 = r0.getQueryParameter(r2);
        r4 = "public_key";
        r1.put(r4, r2);
        r2 = "callback_url";
        r0 = r0.getQueryParameter(r2);
        r2 = "callback_url";
        r1.put(r2, r0);
        goto L_0x09fc;
    L_0x0643:
        r1 = "tg:openmessage";
        r0 = r0.replace(r1, r5);
        r1 = "tg://openmessage";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "user_id";
        r1 = r0.getQueryParameter(r1);
        r2 = "chat_id";
        r2 = r0.getQueryParameter(r2);
        r4 = "message_id";
        r0 = r0.getQueryParameter(r4);
        if (r1 == 0) goto L_0x066f;
    L_0x066a:
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ NumberFormatException -> 0x0678 }
        goto L_0x0679;
    L_0x066f:
        if (r2 == 0) goto L_0x0678;
    L_0x0671:
        r1 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x0678 }
        r2 = r1;
        r1 = 0;
        goto L_0x067a;
    L_0x0678:
        r1 = 0;
    L_0x0679:
        r2 = 0;
    L_0x067a:
        if (r0 == 0) goto L_0x0681;
    L_0x067c:
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x0681 }
        goto L_0x0682;
    L_0x0681:
        r0 = 0;
    L_0x0682:
        r36 = r0;
        r34 = r1;
        r35 = r2;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        goto L_0x0ec0;
    L_0x06ab:
        r1 = "tg:login";
        r0 = r0.replace(r1, r5);
        r1 = "tg://login";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "token";
        r1 = r0.getQueryParameter(r1);
        r2 = "code";
        r0 = r0.getQueryParameter(r2);
        r29 = r1;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        goto L_0x0eb2;
    L_0x06e4:
        r1 = "tg:confirmphone";
        r0 = r0.replace(r1, r5);
        r1 = "tg://confirmphone";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "phone";
        r7 = r0.getQueryParameter(r1);
        r0 = r0.getQueryParameter(r3);
        r1 = r0;
        r0 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        goto L_0x0ea0;
    L_0x0708:
        r4 = "tg:msg";
        r0 = r0.replace(r4, r5);
        r4 = "tg://msg";
        r0 = r0.replace(r4, r5);
        r4 = "tg://share";
        r0 = r0.replace(r4, r5);
        r4 = "tg:share";
        r0 = r0.replace(r4, r5);
        r0 = android.net.Uri.parse(r0);
        r4 = "url";
        r4 = r0.getQueryParameter(r4);
        if (r4 != 0) goto L_0x0732;
    L_0x0731:
        goto L_0x0733;
    L_0x0732:
        r2 = r4;
    L_0x0733:
        r4 = "text";
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0769;
    L_0x073b:
        r4 = r2.length();
        if (r4 <= 0) goto L_0x0752;
    L_0x0741:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r1);
        r2 = r4.toString();
        r4 = 1;
        goto L_0x0753;
    L_0x0752:
        r4 = 0;
    L_0x0753:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = "text";
        r0 = r0.getQueryParameter(r2);
        r5.append(r0);
        r2 = r5.toString();
        goto L_0x076a;
    L_0x0769:
        r4 = 0;
    L_0x076a:
        r0 = r2.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r0 <= r5) goto L_0x077a;
    L_0x0772:
        r0 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r0 = r2.substring(r12, r0);
        r7 = r0;
        goto L_0x077b;
    L_0x077a:
        r7 = r2;
    L_0x077b:
        r0 = r7.endsWith(r1);
        if (r0 == 0) goto L_0x078b;
    L_0x0781:
        r0 = r7.length();
        r0 = r0 - r13;
        r7 = r7.substring(r12, r0);
        goto L_0x077b;
    L_0x078b:
        r9 = r4;
        r2 = r7;
        r0 = 0;
        r1 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        goto L_0x0ea2;
    L_0x0796:
        r1 = "tg:addstickers";
        r0 = r0.replace(r1, r5);
        r1 = "tg://addstickers";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "set";
        r0 = r0.getQueryParameter(r1);
        r8 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        goto L_0x0ea1;
    L_0x07b8:
        r1 = "tg:join";
        r0 = r0.replace(r1, r5);
        r1 = "tg://join";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "invite";
        r0 = r0.getQueryParameter(r1);
        r5 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        goto L_0x097d;
    L_0x07d7:
        r1 = "tg:bg";
        r0 = r0.replace(r1, r5);
        r1 = "tg://bg";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper;
        r1.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
        r2.<init>();
        r1.settings = r2;
        r2 = "slug";
        r2 = r0.getQueryParameter(r2);
        r1.slug = r2;
        r2 = r1.slug;
        if (r2 != 0) goto L_0x0809;
    L_0x0801:
        r2 = "color";
        r2 = r0.getQueryParameter(r2);
        r1.slug = r2;
    L_0x0809:
        r2 = r1.slug;
        if (r2 == 0) goto L_0x0823;
    L_0x080d:
        r2 = r2.length();
        if (r2 != r7) goto L_0x0823;
    L_0x0813:
        r0 = r1.settings;	 Catch:{ Exception -> 0x081e }
        r2 = r1.slug;	 Catch:{ Exception -> 0x081e }
        r2 = java.lang.Integer.parseInt(r2, r8);	 Catch:{ Exception -> 0x081e }
        r2 = r2 | r9;
        r0.background_color = r2;	 Catch:{ Exception -> 0x081e }
    L_0x081e:
        r2 = 0;
        r1.slug = r2;
        goto L_0x0913;
    L_0x0823:
        r2 = r1.slug;
        if (r2 == 0) goto L_0x087b;
    L_0x0827:
        r2 = r2.length();
        r4 = 13;
        if (r2 != r4) goto L_0x087b;
    L_0x082f:
        r2 = r1.slug;
        r2 = r2.charAt(r7);
        r4 = 45;
        if (r2 != r4) goto L_0x087b;
    L_0x0839:
        r2 = r1.settings;	 Catch:{ Exception -> 0x085e }
        r4 = r1.slug;	 Catch:{ Exception -> 0x085e }
        r4 = r4.substring(r12, r7);	 Catch:{ Exception -> 0x085e }
        r4 = java.lang.Integer.parseInt(r4, r8);	 Catch:{ Exception -> 0x085e }
        r4 = r4 | r9;
        r2.background_color = r4;	 Catch:{ Exception -> 0x085e }
        r2 = r1.settings;	 Catch:{ Exception -> 0x085e }
        r4 = r1.slug;	 Catch:{ Exception -> 0x085e }
        r5 = 7;
        r4 = r4.substring(r5);	 Catch:{ Exception -> 0x085e }
        r4 = java.lang.Integer.parseInt(r4, r8);	 Catch:{ Exception -> 0x085e }
        r4 = r4 | r9;
        r2.second_background_color = r4;	 Catch:{ Exception -> 0x085e }
        r2 = r1.settings;	 Catch:{ Exception -> 0x085e }
        r4 = 45;
        r2.rotation = r4;	 Catch:{ Exception -> 0x085e }
    L_0x085e:
        r2 = "rotation";
        r0 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x0876 }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0876 }
        if (r2 != 0) goto L_0x0876;
    L_0x086a:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0876 }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0876 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0876 }
        r2.rotation = r0;	 Catch:{ Exception -> 0x0876 }
    L_0x0876:
        r2 = 0;
        r1.slug = r2;
        goto L_0x0913;
    L_0x087b:
        r2 = "mode";
        r2 = r0.getQueryParameter(r2);
        if (r2 == 0) goto L_0x08b6;
    L_0x0883:
        r2 = r2.toLowerCase();
        r4 = " ";
        r2 = r2.split(r4);
        if (r2 == 0) goto L_0x08b6;
    L_0x088f:
        r4 = r2.length;
        if (r4 <= 0) goto L_0x08b6;
    L_0x0892:
        r4 = 0;
    L_0x0893:
        r5 = r2.length;
        if (r4 >= r5) goto L_0x08b6;
    L_0x0896:
        r5 = r2[r4];
        r6 = "blur";
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x08a5;
    L_0x08a0:
        r5 = r1.settings;
        r5.blur = r13;
        goto L_0x08b3;
    L_0x08a5:
        r5 = r2[r4];
        r6 = "motion";
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x08b3;
    L_0x08af:
        r5 = r1.settings;
        r5.motion = r13;
    L_0x08b3:
        r4 = r4 + 1;
        goto L_0x0893;
    L_0x08b6:
        r2 = r1.settings;
        r4 = "intensity";
        r4 = r0.getQueryParameter(r4);
        r4 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r4.intValue();
        r2.intensity = r4;
        r2 = "bg_color";
        r2 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x08fb }
        r4 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x08fb }
        if (r4 != 0) goto L_0x08fb;
    L_0x08d4:
        r4 = r1.settings;	 Catch:{ Exception -> 0x08fb }
        r5 = r2.substring(r12, r7);	 Catch:{ Exception -> 0x08fb }
        r5 = java.lang.Integer.parseInt(r5, r8);	 Catch:{ Exception -> 0x08fb }
        r5 = r5 | r9;
        r4.background_color = r5;	 Catch:{ Exception -> 0x08fb }
        r4 = r2.length();	 Catch:{ Exception -> 0x08fb }
        if (r4 <= r7) goto L_0x08fb;
    L_0x08e7:
        r4 = r1.settings;	 Catch:{ Exception -> 0x08fb }
        r5 = 7;
        r2 = r2.substring(r5);	 Catch:{ Exception -> 0x08fb }
        r2 = java.lang.Integer.parseInt(r2, r8);	 Catch:{ Exception -> 0x08fb }
        r2 = r2 | r9;
        r4.second_background_color = r2;	 Catch:{ Exception -> 0x08fb }
        r2 = r1.settings;	 Catch:{ Exception -> 0x08fb }
        r4 = 45;
        r2.rotation = r4;	 Catch:{ Exception -> 0x08fb }
    L_0x08fb:
        r2 = "rotation";
        r0 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x0913 }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0913 }
        if (r2 != 0) goto L_0x0913;
    L_0x0907:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0913 }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0913 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0913 }
        r2.rotation = r0;	 Catch:{ Exception -> 0x0913 }
    L_0x0913:
        r26 = r1;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        goto L_0x0eac;
    L_0x0928:
        r1 = "tg:privatepost";
        r0 = r0.replace(r1, r5);
        r1 = "tg://privatepost";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "post";
        r1 = r0.getQueryParameter(r1);
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r2 = "channel";
        r0 = r0.getQueryParameter(r2);
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r2 = r1.intValue();
        if (r2 == 0) goto L_0x0978;
    L_0x0954:
        r2 = r0.intValue();
        if (r2 != 0) goto L_0x095b;
    L_0x095a:
        goto L_0x0978;
    L_0x095b:
        r28 = r0;
        r30 = r1;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r29 = 0;
        goto L_0x0eb4;
    L_0x0978:
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
    L_0x097d:
        r6 = 2;
        goto L_0x0e9f;
    L_0x0980:
        r1 = "tg:resolve";
        r0 = r0.replace(r1, r5);
        r1 = "tg://resolve";
        r0 = r0.replace(r1, r5);
        r0 = android.net.Uri.parse(r0);
        r1 = "domain";
        r1 = r0.getQueryParameter(r1);
        r2 = "telegrampassport";
        r2 = r2.equals(r1);
        if (r2 == 0) goto L_0x0a0b;
    L_0x09a0:
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = r0.getQueryParameter(r6);
        r4 = android.text.TextUtils.isEmpty(r2);
        if (r4 != 0) goto L_0x09cd;
    L_0x09af:
        r4 = "{";
        r4 = r2.startsWith(r4);
        if (r4 == 0) goto L_0x09cd;
    L_0x09b8:
        r4 = "}";
        r4 = r2.endsWith(r4);
        if (r4 == 0) goto L_0x09cd;
    L_0x09c1:
        r4 = "nonce";
        r4 = r0.getQueryParameter(r4);
        r5 = "nonce";
        r1.put(r5, r4);
        goto L_0x09d8;
    L_0x09cd:
        r4 = "payload";
        r4 = r0.getQueryParameter(r4);
        r5 = "payload";
        r1.put(r5, r4);
    L_0x09d8:
        r4 = "bot_id";
        r4 = r0.getQueryParameter(r4);
        r5 = "bot_id";
        r1.put(r5, r4);
        r1.put(r6, r2);
        r2 = "public_key";
        r2 = r0.getQueryParameter(r2);
        r4 = "public_key";
        r1.put(r4, r2);
        r2 = "callback_url";
        r0 = r0.getQueryParameter(r2);
        r2 = "callback_url";
        r1.put(r2, r0);
    L_0x09fc:
        r23 = r1;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        goto L_0x0ea6;
    L_0x0a0b:
        r2 = "start";
        r2 = r0.getQueryParameter(r2);
        r4 = "startgroup";
        r4 = r0.getQueryParameter(r4);
        r5 = "game";
        r5 = r0.getQueryParameter(r5);
        r6 = "post";
        r0 = r0.getQueryParameter(r6);
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r6 = r0.intValue();
        if (r6 != 0) goto L_0x0a4b;
    L_0x0a2d:
        r31 = r2;
        r32 = r4;
        r18 = r5;
        r0 = 0;
        r2 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        goto L_0x0a68;
    L_0x0a4b:
        r30 = r0;
        r31 = r2;
        r32 = r4;
        r18 = r5;
        r0 = 0;
        r2 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
    L_0x0a68:
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r36 = 0;
        r37 = 0;
        r4 = r1;
        r1 = 0;
        goto L_0x0ec2;
    L_0x0a76:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x0ade;
    L_0x0a7c:
        r1 = org.telegram.messenger.UserConfig.selectedAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.walletConfig;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0ade;
    L_0x0a8a:
        r1 = org.telegram.messenger.UserConfig.selectedAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.walletBlockchainName;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0ade;
    L_0x0a98:
        r0 = r0.toString();
        r1 = "ton:transfer";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x0ab1;
    L_0x0aa5:
        r1 = "ton://transfer";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0aaf;
    L_0x0aae:
        goto L_0x0ab1;
    L_0x0aaf:
        r7 = 0;
        goto L_0x0abb;
    L_0x0ab1:
        r1 = "ton:transfer";
        r2 = "ton://transfer";
        r7 = r0.replace(r1, r2);
    L_0x0abb:
        r33 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
        goto L_0x0eba;
    L_0x0ade:
        r6 = 2;
        goto L_0x0e9a;
    L_0x0ae1:
        r4 = r0.getHost();
        r4 = r4.toLowerCase();
        r5 = "telegram.me";
        r5 = r4.equals(r5);
        if (r5 != 0) goto L_0x0b01;
    L_0x0af1:
        r5 = "t.me";
        r5 = r4.equals(r5);
        if (r5 != 0) goto L_0x0b01;
    L_0x0af9:
        r5 = "telegram.dog";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0ade;
    L_0x0b01:
        r4 = r0.getPath();
        if (r4 == 0) goto L_0x0e59;
    L_0x0b07:
        r5 = r4.length();
        if (r5 <= r13) goto L_0x0e59;
    L_0x0b0d:
        r4 = r4.substring(r13);
        r5 = "bg/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0b19:
        r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper;
        r1.<init>();
        r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
        r5.<init>();
        r1.settings = r5;
        r5 = "bg/";
        r2 = r4.replace(r5, r2);
        r1.slug = r2;
        r2 = r1.slug;
        if (r2 == 0) goto L_0x0b47;
    L_0x0b31:
        r2 = r2.length();
        if (r2 != r7) goto L_0x0b47;
    L_0x0b37:
        r0 = r1.settings;	 Catch:{ Exception -> 0x0b42 }
        r2 = r1.slug;	 Catch:{ Exception -> 0x0b42 }
        r2 = java.lang.Integer.parseInt(r2, r8);	 Catch:{ Exception -> 0x0b42 }
        r2 = r2 | r9;
        r0.background_color = r2;	 Catch:{ Exception -> 0x0b42 }
    L_0x0b42:
        r2 = 0;
        r1.slug = r2;
        goto L_0x0c4c;
    L_0x0b47:
        r2 = r1.slug;
        if (r2 == 0) goto L_0x0b9f;
    L_0x0b4b:
        r2 = r2.length();
        r4 = 13;
        if (r2 != r4) goto L_0x0b9f;
    L_0x0b53:
        r2 = r1.slug;
        r2 = r2.charAt(r7);
        r4 = 45;
        if (r2 != r4) goto L_0x0b9f;
    L_0x0b5d:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0b82 }
        r4 = r1.slug;	 Catch:{ Exception -> 0x0b82 }
        r4 = r4.substring(r12, r7);	 Catch:{ Exception -> 0x0b82 }
        r4 = java.lang.Integer.parseInt(r4, r8);	 Catch:{ Exception -> 0x0b82 }
        r4 = r4 | r9;
        r2.background_color = r4;	 Catch:{ Exception -> 0x0b82 }
        r2 = r1.settings;	 Catch:{ Exception -> 0x0b82 }
        r4 = r1.slug;	 Catch:{ Exception -> 0x0b82 }
        r5 = 7;
        r4 = r4.substring(r5);	 Catch:{ Exception -> 0x0b82 }
        r4 = java.lang.Integer.parseInt(r4, r8);	 Catch:{ Exception -> 0x0b82 }
        r4 = r4 | r9;
        r2.second_background_color = r4;	 Catch:{ Exception -> 0x0b82 }
        r2 = r1.settings;	 Catch:{ Exception -> 0x0b82 }
        r4 = 45;
        r2.rotation = r4;	 Catch:{ Exception -> 0x0b82 }
    L_0x0b82:
        r2 = "rotation";
        r0 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x0b9a }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0b9a }
        if (r2 != 0) goto L_0x0b9a;
    L_0x0b8e:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0b9a }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0b9a }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0b9a }
        r2.rotation = r0;	 Catch:{ Exception -> 0x0b9a }
    L_0x0b9a:
        r6 = 0;
        r1.slug = r6;
        goto L_0x0c4c;
    L_0x0b9f:
        r6 = 0;
        r2 = "mode";
        r2 = r0.getQueryParameter(r2);
        if (r2 == 0) goto L_0x0bdc;
    L_0x0ba8:
        r2 = r2.toLowerCase();
        r4 = " ";
        r2 = r2.split(r4);
        if (r2 == 0) goto L_0x0bdc;
    L_0x0bb4:
        r4 = r2.length;
        if (r4 <= 0) goto L_0x0bdc;
    L_0x0bb7:
        r4 = 0;
    L_0x0bb8:
        r5 = r2.length;
        if (r4 >= r5) goto L_0x0bdc;
    L_0x0bbb:
        r5 = r2[r4];
        r6 = "blur";
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x0bca;
    L_0x0bc5:
        r5 = r1.settings;
        r5.blur = r13;
        goto L_0x0bd8;
    L_0x0bca:
        r5 = r2[r4];
        r6 = "motion";
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x0bd8;
    L_0x0bd4:
        r5 = r1.settings;
        r5.motion = r13;
    L_0x0bd8:
        r4 = r4 + 1;
        r6 = 0;
        goto L_0x0bb8;
    L_0x0bdc:
        r2 = "intensity";
        r2 = r0.getQueryParameter(r2);
        r4 = android.text.TextUtils.isEmpty(r2);
        if (r4 != 0) goto L_0x0bf5;
    L_0x0be8:
        r4 = r1.settings;
        r2 = org.telegram.messenger.Utilities.parseInt(r2);
        r2 = r2.intValue();
        r4.intensity = r2;
        goto L_0x0bfb;
    L_0x0bf5:
        r2 = r1.settings;
        r4 = 50;
        r2.intensity = r4;
    L_0x0bfb:
        r2 = "bg_color";
        r2 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x0CLASSNAME }
        r4 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r4 != 0) goto L_0x0c2f;
    L_0x0CLASSNAME:
        r4 = r1.settings;	 Catch:{ Exception -> 0x0CLASSNAME }
        r5 = r2.substring(r12, r7);	 Catch:{ Exception -> 0x0CLASSNAME }
        r5 = java.lang.Integer.parseInt(r5, r8);	 Catch:{ Exception -> 0x0CLASSNAME }
        r5 = r5 | r9;
        r4.background_color = r5;	 Catch:{ Exception -> 0x0CLASSNAME }
        r4 = r2.length();	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r4 <= r7) goto L_0x0CLASSNAME;
    L_0x0c1a:
        r4 = r1.settings;	 Catch:{ Exception -> 0x0CLASSNAME }
        r5 = 7;
        r2 = r2.substring(r5);	 Catch:{ Exception -> 0x0CLASSNAME }
        r2 = java.lang.Integer.parseInt(r2, r8);	 Catch:{ Exception -> 0x0CLASSNAME }
        r2 = r2 | r9;
        r4.second_background_color = r2;	 Catch:{ Exception -> 0x0CLASSNAME }
        r2 = r1.settings;	 Catch:{ Exception -> 0x0CLASSNAME }
        r4 = 45;
        r2.rotation = r4;	 Catch:{ Exception -> 0x0CLASSNAME }
        goto L_0x0CLASSNAME;
    L_0x0c2f:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0CLASSNAME }
        r4 = -1;
        r2.background_color = r4;	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0CLASSNAME:
        r2 = "rotation";
        r0 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x0c4c }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0c4c }
        if (r2 != 0) goto L_0x0c4c;
    L_0x0CLASSNAME:
        r2 = r1.settings;	 Catch:{ Exception -> 0x0c4c }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0c4c }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0c4c }
        r2.rotation = r0;	 Catch:{ Exception -> 0x0c4c }
    L_0x0c4c:
        r26 = r1;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r20 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        goto L_0x0e6e;
    L_0x0CLASSNAME:
        r5 = "login/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c6b:
        r0 = "login/";
        r7 = r4.replace(r0, r2);
        r25 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r20 = 0;
        r23 = 0;
        r24 = 0;
        goto L_0x0e6c;
    L_0x0CLASSNAME:
        r5 = "joinchat/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0c9c;
    L_0x0c8e:
        r0 = "joinchat/";
        r7 = r4.replace(r0, r2);
        r0 = r7;
        r1 = 0;
    L_0x0CLASSNAME:
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        goto L_0x0e5f;
    L_0x0c9c:
        r5 = "addstickers/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0cad;
    L_0x0ca4:
        r0 = "addstickers/";
        r7 = r4.replace(r0, r2);
        r1 = r7;
        r0 = 0;
        goto L_0x0CLASSNAME;
    L_0x0cad:
        r5 = "msg/";
        r5 = r4.startsWith(r5);
        if (r5 != 0) goto L_0x0dee;
    L_0x0cb5:
        r5 = "share/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0cbf;
    L_0x0cbd:
        goto L_0x0dee;
    L_0x0cbf:
        r1 = "confirmphone";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0ce0;
    L_0x0cc7:
        r1 = "phone";
        r7 = r0.getQueryParameter(r1);
        r0 = r0.getQueryParameter(r3);
        r20 = r0;
        r9 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r18 = 0;
        goto L_0x0e66;
    L_0x0ce0:
        r1 = "setlanguage/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0cff;
    L_0x0ce8:
        r0 = 12;
        r7 = r4.substring(r0);
        r23 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r20 = 0;
        goto L_0x0e68;
    L_0x0cff:
        r1 = "addtheme/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0d20;
    L_0x0d07:
        r0 = 9;
        r7 = r4.substring(r0);
        r24 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 2;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r20 = 0;
        r23 = 0;
        goto L_0x0e6a;
    L_0x0d20:
        r1 = "c/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0d78;
    L_0x0d28:
        r0 = r0.getPathSegments();
        r1 = r0.size();
        r2 = 3;
        if (r1 != r2) goto L_0x0d5b;
    L_0x0d33:
        r1 = r0.get(r13);
        r1 = (java.lang.CharSequence) r1;
        r7 = org.telegram.messenger.Utilities.parseInt(r1);
        r6 = 2;
        r0 = r0.get(r6);
        r0 = (java.lang.CharSequence) r0;
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r1 = r0.intValue();
        if (r1 == 0) goto L_0x0d5c;
    L_0x0d4e:
        r1 = r7.intValue();
        if (r1 != 0) goto L_0x0d55;
    L_0x0d54:
        goto L_0x0d5c;
    L_0x0d55:
        r46 = r7;
        r7 = r0;
        r0 = r46;
        goto L_0x0d5e;
    L_0x0d5b:
        r6 = 2;
    L_0x0d5c:
        r0 = 0;
        r7 = 0;
    L_0x0d5e:
        r28 = r0;
        r27 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r18 = 0;
        r20 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        goto L_0x0e72;
    L_0x0d78:
        r6 = 2;
        r1 = r4.length();
        if (r1 < r13) goto L_0x0e5a;
    L_0x0d7f:
        r1 = new java.util.ArrayList;
        r2 = r0.getPathSegments();
        r1.<init>(r2);
        r2 = r1.size();
        if (r2 <= 0) goto L_0x0d9f;
    L_0x0d8e:
        r2 = r1.get(r12);
        r2 = (java.lang.String) r2;
        r4 = "s";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0d9f;
    L_0x0d9c:
        r1.remove(r12);
    L_0x0d9f:
        r2 = r1.size();
        if (r2 <= 0) goto L_0x0dc4;
    L_0x0da5:
        r2 = r1.get(r12);
        r7 = r2;
        r7 = (java.lang.String) r7;
        r2 = r1.size();
        if (r2 <= r13) goto L_0x0dc2;
    L_0x0db2:
        r1 = r1.get(r13);
        r1 = (java.lang.CharSequence) r1;
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r2 = r1.intValue();
        if (r2 != 0) goto L_0x0dc6;
    L_0x0dc2:
        r1 = 0;
        goto L_0x0dc6;
    L_0x0dc4:
        r1 = 0;
        r7 = 0;
    L_0x0dc6:
        r2 = "start";
        r2 = r0.getQueryParameter(r2);
        r4 = "startgroup";
        r4 = r0.getQueryParameter(r4);
        r5 = "game";
        r0 = r0.getQueryParameter(r5);
        r18 = r0;
        r27 = r1;
        r5 = r4;
        r0 = 0;
        r1 = 0;
        r4 = 0;
        r8 = 0;
        r9 = 0;
        r20 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        goto L_0x0e70;
    L_0x0dee:
        r6 = 2;
        r4 = "url";
        r4 = r0.getQueryParameter(r4);
        if (r4 != 0) goto L_0x0df9;
    L_0x0df8:
        goto L_0x0dfa;
    L_0x0df9:
        r2 = r4;
    L_0x0dfa:
        r4 = "text";
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0e30;
    L_0x0e02:
        r4 = r2.length();
        if (r4 <= 0) goto L_0x0e19;
    L_0x0e08:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r1);
        r2 = r4.toString();
        r4 = 1;
        goto L_0x0e1a;
    L_0x0e19:
        r4 = 0;
    L_0x0e1a:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = "text";
        r0 = r0.getQueryParameter(r2);
        r5.append(r0);
        r2 = r5.toString();
        goto L_0x0e31;
    L_0x0e30:
        r4 = 0;
    L_0x0e31:
        r0 = r2.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r0 <= r5) goto L_0x0e41;
    L_0x0e39:
        r0 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r0 = r2.substring(r12, r0);
        r7 = r0;
        goto L_0x0e42;
    L_0x0e41:
        r7 = r2;
    L_0x0e42:
        r0 = r7.endsWith(r1);
        if (r0 == 0) goto L_0x0e52;
    L_0x0e48:
        r0 = r7.length();
        r0 = r0 - r13;
        r7 = r7.substring(r12, r0);
        goto L_0x0e42;
    L_0x0e52:
        r8 = r7;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r5 = 0;
        r7 = 0;
        goto L_0x0e61;
    L_0x0e59:
        r6 = 2;
    L_0x0e5a:
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
    L_0x0e5f:
        r7 = 0;
        r8 = 0;
    L_0x0e61:
        r9 = 0;
        r18 = 0;
        r20 = 0;
    L_0x0e66:
        r23 = 0;
    L_0x0e68:
        r24 = 0;
    L_0x0e6a:
        r25 = 0;
    L_0x0e6c:
        r26 = 0;
    L_0x0e6e:
        r27 = 0;
    L_0x0e70:
        r28 = 0;
    L_0x0e72:
        r31 = r2;
        r32 = r5;
        r2 = r8;
        r30 = r27;
        r29 = 0;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r36 = 0;
        r37 = 0;
        r5 = r0;
        r8 = r1;
        r1 = r20;
        r27 = r24;
        r0 = r25;
        r24 = 0;
        r25 = r23;
        r23 = 0;
        r46 = r9;
        r9 = r4;
        r4 = r7;
        r7 = r46;
        goto L_0x0ec2;
    L_0x0e9a:
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r4 = 0;
        r5 = 0;
    L_0x0e9f:
        r7 = 0;
    L_0x0ea0:
        r8 = 0;
    L_0x0ea1:
        r9 = 0;
    L_0x0ea2:
        r18 = 0;
        r23 = 0;
    L_0x0ea6:
        r24 = 0;
    L_0x0ea8:
        r25 = 0;
    L_0x0eaa:
        r26 = 0;
    L_0x0eac:
        r27 = 0;
    L_0x0eae:
        r28 = 0;
        r29 = 0;
    L_0x0eb2:
        r30 = 0;
    L_0x0eb4:
        r31 = 0;
        r32 = 0;
        r33 = 0;
    L_0x0eba:
        r34 = 0;
        r35 = 0;
        r36 = 0;
    L_0x0ec0:
        r37 = 0;
    L_0x0ec2:
        if (r0 != 0) goto L_0x0ed8;
    L_0x0ec4:
        r6 = r15.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.isClientActivated();
        if (r6 == 0) goto L_0x0ed1;
    L_0x0ed0:
        goto L_0x0ed8;
    L_0x0ed1:
        r44 = r11;
        r2 = r15;
        r31 = 0;
        goto L_0x0ff0;
    L_0x0ed8:
        if (r7 != 0) goto L_0x0fd5;
    L_0x0eda:
        if (r1 == 0) goto L_0x0ede;
    L_0x0edc:
        goto L_0x0fd5;
    L_0x0ede:
        if (r4 != 0) goto L_0x0var_;
    L_0x0ee0:
        if (r5 != 0) goto L_0x0var_;
    L_0x0ee2:
        if (r8 != 0) goto L_0x0var_;
    L_0x0ee4:
        if (r2 != 0) goto L_0x0var_;
    L_0x0ee6:
        if (r18 != 0) goto L_0x0var_;
    L_0x0ee8:
        if (r23 != 0) goto L_0x0var_;
    L_0x0eea:
        if (r24 != 0) goto L_0x0var_;
    L_0x0eec:
        if (r25 != 0) goto L_0x0var_;
    L_0x0eee:
        if (r0 != 0) goto L_0x0var_;
    L_0x0ef0:
        if (r26 != 0) goto L_0x0var_;
    L_0x0ef2:
        if (r28 != 0) goto L_0x0var_;
    L_0x0ef4:
        if (r27 != 0) goto L_0x0var_;
    L_0x0ef6:
        if (r29 == 0) goto L_0x0efa;
    L_0x0ef8:
        goto L_0x0var_;
    L_0x0efa:
        r38 = r47.getContentResolver();	 Catch:{ Exception -> 0x0var_ }
        r39 = r48.getData();	 Catch:{ Exception -> 0x0var_ }
        r40 = 0;
        r41 = 0;
        r42 = 0;
        r43 = 0;
        r1 = r38.query(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0var_ }
        if (r1 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = r1.moveToFirst();	 Catch:{ all -> 0x0f5d }
        if (r0 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = "account_name";
        r0 = r1.getColumnIndex(r0);	 Catch:{ all -> 0x0f5d }
        r0 = r1.getString(r0);	 Catch:{ all -> 0x0f5d }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ all -> 0x0f5d }
        r0 = r0.intValue();	 Catch:{ all -> 0x0f5d }
        r2 = 0;
        r6 = 3;
    L_0x0f2a:
        if (r2 >= r6) goto L_0x0var_;
    L_0x0f2c:
        r3 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ all -> 0x0f5b }
        r3 = r3.getClientUserId();	 Catch:{ all -> 0x0f5b }
        if (r3 != r0) goto L_0x0f3e;
    L_0x0var_:
        r11[r12] = r2;	 Catch:{ all -> 0x0f5b }
        r0 = r11[r12];	 Catch:{ all -> 0x0f5b }
        r15.switchToAccount(r0, r13);	 Catch:{ all -> 0x0f5b }
        goto L_0x0var_;
    L_0x0f3e:
        r2 = r2 + 1;
        goto L_0x0f2a;
    L_0x0var_:
        r0 = "DATA4";
        r0 = r1.getColumnIndex(r0);	 Catch:{ all -> 0x0f5b }
        r0 = r1.getInt(r0);	 Catch:{ all -> 0x0f5b }
        r2 = r11[r12];	 Catch:{ all -> 0x0f5b }
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);	 Catch:{ all -> 0x0f5b }
        r3 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ all -> 0x0f5b }
        r4 = new java.lang.Object[r12];	 Catch:{ all -> 0x0f5b }
        r2.postNotificationName(r3, r4);	 Catch:{ all -> 0x0f5b }
        r34 = r0;
        goto L_0x0var_;
    L_0x0f5b:
        r0 = move-exception;
        goto L_0x0f5f;
    L_0x0f5d:
        r0 = move-exception;
        r6 = 3;
    L_0x0f5f:
        throw r0;	 Catch:{ all -> 0x0var_ }
    L_0x0var_:
        r0 = move-exception;
        r2 = r0;
        if (r1 == 0) goto L_0x0var_;
    L_0x0var_:
        r1.close();	 Catch:{ all -> 0x0var_ }
    L_0x0var_:
        throw r2;	 Catch:{ Exception -> 0x0f6f }
    L_0x0var_:
        r6 = 3;
    L_0x0var_:
        if (r1 == 0) goto L_0x0var_;
    L_0x0f6b:
        r1.close();	 Catch:{ Exception -> 0x0f6f }
        goto L_0x0var_;
    L_0x0f6f:
        r0 = move-exception;
        goto L_0x0var_;
    L_0x0var_:
        r0 = move-exception;
        r6 = 3;
    L_0x0var_:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0var_:
        r44 = r11;
        r2 = r15;
        r7 = r33;
        r12 = r34;
        r13 = r37;
        r31 = 0;
        goto L_0x1003;
    L_0x0var_:
        r6 = 3;
        if (r2 == 0) goto L_0x0fa2;
    L_0x0var_:
        r1 = "@";
        r1 = r2.startsWith(r1);
        if (r1 == 0) goto L_0x0fa2;
    L_0x0f8e:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = " ";
        r1.append(r3);
        r1.append(r2);
        r1 = r1.toString();
        r19 = r1;
        goto L_0x0fa4;
    L_0x0fa2:
        r19 = r2;
    L_0x0fa4:
        r2 = r11[r12];
        r20 = 0;
        r1 = r47;
        r3 = r4;
        r4 = r5;
        r7 = 3;
        r5 = r8;
        r8 = 2;
        r38 = 0;
        r6 = r31;
        r7 = r32;
        r31 = 0;
        r8 = r19;
        r10 = r30;
        r44 = r11;
        r11 = r28;
        r12 = r18;
        r13 = r23;
        r14 = r25;
        r15 = r24;
        r16 = r0;
        r17 = r29;
        r18 = r26;
        r19 = r27;
        r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r2 = r47;
        goto L_0x0ff0;
    L_0x0fd5:
        r44 = r11;
        r31 = 0;
        r0 = new android.os.Bundle;
        r0.<init>();
        r2 = "phone";
        r0.putString(r2, r7);
        r0.putString(r3, r1);
        r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$3rXtwzTW6WkmVHi2jvIuUk6ur6Y;
        r2 = r47;
        r1.<init>(r2, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0ff0:
        r7 = r33;
        r12 = r34;
        r13 = r37;
        goto L_0x1003;
    L_0x0ff7:
        r44 = r11;
        r2 = r15;
        r31 = 0;
        r7 = 0;
        r12 = 0;
        r13 = 0;
        r35 = 0;
        r36 = 0;
    L_0x1003:
        r1 = r48;
        r0 = r35;
        r4 = r36;
        r6 = r44;
        r3 = 0;
        r5 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        goto L_0x10f0;
    L_0x1012:
        r44 = r11;
        r2 = r15;
        r31 = 0;
        r0 = r48.getAction();
        r1 = "org.telegram.messenger.OPEN_ACCOUNT";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x1033;
    L_0x1023:
        r1 = r48;
        r6 = r44;
        r0 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r13 = 1;
        goto L_0x10f0;
    L_0x1033:
        r0 = r48.getAction();
        r1 = "new_dialog";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x104d;
    L_0x103f:
        r1 = r48;
        r6 = r44;
        r0 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        goto L_0x10ee;
    L_0x104d:
        r0 = r48.getAction();
        r1 = "com.tmessages.openchat";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x10b7;
    L_0x1059:
        r0 = "chatId";
        r1 = r48;
        r3 = 0;
        r0 = r1.getIntExtra(r0, r3);
        r4 = "userId";
        r4 = r1.getIntExtra(r4, r3);
        r5 = "encId";
        r5 = r1.getIntExtra(r5, r3);
        if (r0 == 0) goto L_0x1084;
    L_0x1071:
        r6 = r44;
        r4 = r6[r3];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r4.postNotificationName(r5, r7);
        r4 = 0;
    L_0x1081:
        r5 = 0;
    L_0x1082:
        r12 = 0;
        goto L_0x10ad;
    L_0x1084:
        r6 = r44;
        if (r4 == 0) goto L_0x1097;
    L_0x1088:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r0.postNotificationName(r5, r7);
        r0 = 0;
        goto L_0x1081;
    L_0x1097:
        if (r5 == 0) goto L_0x10a9;
    L_0x1099:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r0.postNotificationName(r4, r7);
        r0 = 0;
        r4 = 0;
        goto L_0x1082;
    L_0x10a9:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r12 = 1;
    L_0x10ad:
        r45 = r12;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r13 = 0;
        r12 = r4;
        r4 = 0;
        goto L_0x10f2;
    L_0x10b7:
        r1 = r48;
        r6 = r44;
        r3 = 0;
        r0 = r48.getAction();
        r4 = "com.tmessages.openplayer";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x10ce;
    L_0x10c8:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 1;
        goto L_0x10ec;
    L_0x10ce:
        r0 = r48.getAction();
        r4 = "org.tmessages.openlocations";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x10e7;
    L_0x10da:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 1;
        goto L_0x10ed;
    L_0x10e1:
        r31 = r8;
        r6 = r11;
        r1 = r14;
        r2 = r15;
        r3 = 0;
    L_0x10e7:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
    L_0x10ec:
        r9 = 0;
    L_0x10ed:
        r10 = 0;
    L_0x10ee:
        r12 = 0;
        r13 = 0;
    L_0x10f0:
        r45 = 0;
    L_0x10f2:
        r11 = r2.currentAccount;
        r11 = org.telegram.messenger.UserConfig.getInstance(r11);
        r11 = r11.isClientActivated();
        if (r11 == 0) goto L_0x1436;
    L_0x10fe:
        if (r12 == 0) goto L_0x114b;
    L_0x1100:
        r0 = new android.os.Bundle;
        r0.<init>();
        r5 = "user_id";
        r0.putInt(r5, r12);
        if (r4 == 0) goto L_0x1112;
    L_0x110d:
        r5 = "message_id";
        r0.putInt(r5, r4);
    L_0x1112:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x1135;
    L_0x111a:
        r4 = r6[r3];
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r5 = mainFragmentsStack;
        r6 = r5.size();
        r11 = 1;
        r6 = r6 - r11;
        r5 = r5.get(r6);
        r5 = (org.telegram.ui.ActionBar.BaseFragment) r5;
        r4 = r4.checkCanOpenChat(r0, r5);
        if (r4 == 0) goto L_0x1149;
    L_0x1134:
        goto L_0x1136;
    L_0x1135:
        r11 = 1;
    L_0x1136:
        r6 = new org.telegram.ui.ChatActivity;
        r6.<init>(r0);
        r5 = r2.actionBarLayout;
        r7 = 0;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r0 = r5.presentFragment(r6, r7, r8, r9, r10);
        if (r0 == 0) goto L_0x1149;
    L_0x1147:
        r13 = 1;
        goto L_0x11b1;
    L_0x1149:
        r13 = 0;
        goto L_0x11b1;
    L_0x114b:
        r11 = 1;
        if (r0 == 0) goto L_0x1194;
    L_0x114e:
        r5 = new android.os.Bundle;
        r5.<init>();
        r7 = "chat_id";
        r5.putInt(r7, r0);
        if (r4 == 0) goto L_0x115f;
    L_0x115a:
        r0 = "message_id";
        r5.putInt(r0, r4);
    L_0x115f:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x1180;
    L_0x1167:
        r0 = r6[r3];
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r4 = mainFragmentsStack;
        r6 = r4.size();
        r6 = r6 - r11;
        r4 = r4.get(r6);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r0.checkCanOpenChat(r5, r4);
        if (r0 == 0) goto L_0x1149;
    L_0x1180:
        r13 = new org.telegram.ui.ChatActivity;
        r13.<init>(r5);
        r12 = r2.actionBarLayout;
        r14 = 0;
        r15 = 1;
        r16 = 1;
        r17 = 0;
        r0 = r12.presentFragment(r13, r14, r15, r16, r17);
        if (r0 == 0) goto L_0x1149;
    L_0x1193:
        goto L_0x1147;
    L_0x1194:
        if (r5 == 0) goto L_0x11b6;
    L_0x1196:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "enc_id";
        r0.putInt(r4, r5);
        r13 = new org.telegram.ui.ChatActivity;
        r13.<init>(r0);
        r12 = r2.actionBarLayout;
        r14 = 0;
        r15 = 1;
        r16 = 1;
        r17 = 0;
        r13 = r12.presentFragment(r13, r14, r15, r16, r17);
    L_0x11b1:
        r0 = r49;
        r4 = 0;
        goto L_0x143b;
    L_0x11b6:
        if (r45 == 0) goto L_0x11f0;
    L_0x11b8:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 != 0) goto L_0x11c4;
    L_0x11be:
        r0 = r2.actionBarLayout;
        r0.removeAllFragments();
        goto L_0x11ec;
    L_0x11c4:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x11ec;
    L_0x11ce:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        r0 = r0 - r11;
        if (r0 <= 0) goto L_0x11e7;
    L_0x11d9:
        r0 = r2.layersActionBarLayout;
        r4 = r0.fragmentsStack;
        r4 = r4.get(r3);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0.removeFragmentFromStack(r4);
        goto L_0x11ce;
    L_0x11e7:
        r0 = r2.layersActionBarLayout;
        r0.closeLastFragment(r3);
    L_0x11ec:
        r0 = 0;
    L_0x11ed:
        r4 = 0;
        goto L_0x143a;
    L_0x11f0:
        if (r7 == 0) goto L_0x1221;
    L_0x11f2:
        r0 = r2.getCurrentWalletFragment(r7);
        if (r0 == 0) goto L_0x1200;
    L_0x11f8:
        r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$5GybZC1APjVUWmzZAupg-DSdEx8;
        r4.<init>(r2, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x1200:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1216;
    L_0x1206:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x121b;
    L_0x1216:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
    L_0x121b:
        r0 = r49;
        r4 = 0;
        r13 = 1;
        goto L_0x143b;
    L_0x1221:
        if (r8 == 0) goto L_0x1242;
    L_0x1223:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x123f;
    L_0x122d:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r3);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r4 = new org.telegram.ui.Components.AudioPlayerAlert;
        r4.<init>(r2);
        r0.showDialog(r4);
    L_0x123f:
        r0 = r49;
        goto L_0x11ed;
    L_0x1242:
        if (r9 == 0) goto L_0x1266;
    L_0x1244:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x123f;
    L_0x124e:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r3);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r4 = new org.telegram.ui.Components.SharingLocationsAlert;
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI;
        r5.<init>(r2, r6);
        r4.<init>(r2, r5);
        r0.showDialog(r4);
        goto L_0x123f;
    L_0x1266:
        r0 = r2.videoPath;
        if (r0 != 0) goto L_0x12fe;
    L_0x126a:
        r0 = r2.photoPathsArray;
        if (r0 != 0) goto L_0x12fe;
    L_0x126e:
        r0 = r2.sendingText;
        if (r0 != 0) goto L_0x12fe;
    L_0x1272:
        r0 = r2.documentsPathsArray;
        if (r0 != 0) goto L_0x12fe;
    L_0x1276:
        r0 = r2.contactsToSend;
        if (r0 != 0) goto L_0x12fe;
    L_0x127a:
        r0 = r2.documentsUrisArray;
        if (r0 == 0) goto L_0x1280;
    L_0x127e:
        goto L_0x12fe;
    L_0x1280:
        if (r13 == 0) goto L_0x12c3;
    L_0x1282:
        if (r13 != r11) goto L_0x128a;
    L_0x1284:
        r7 = new org.telegram.ui.SettingsActivity;
        r7.<init>();
        goto L_0x129d;
    L_0x128a:
        r4 = 2;
        if (r13 != r4) goto L_0x1293;
    L_0x128d:
        r7 = new org.telegram.ui.ThemeActivity;
        r7.<init>(r3);
        goto L_0x129d;
    L_0x1293:
        r4 = 3;
        if (r13 != r4) goto L_0x129c;
    L_0x1296:
        r7 = new org.telegram.ui.SessionsActivity;
        r7.<init>(r3);
        goto L_0x129d;
    L_0x129c:
        r7 = 0;
    L_0x129d:
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$8IG4rcI4GUiI0vd7KeiMHRKV008;
        r0.<init>(r2, r7);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x12bc;
    L_0x12ab:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x121b;
    L_0x12bc:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
        goto L_0x121b;
    L_0x12c3:
        if (r10 == 0) goto L_0x12fb;
    L_0x12c5:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "destroyAfterSelect";
        r0.putBoolean(r4, r11);
        r5 = r2.actionBarLayout;
        r6 = new org.telegram.ui.ContactsActivity;
        r6.<init>(r0);
        r7 = 0;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r5.presentFragment(r6, r7, r8, r9, r10);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x12f4;
    L_0x12e3:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x121b;
    L_0x12f4:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
        goto L_0x121b;
    L_0x12fb:
        r4 = 0;
        goto L_0x1438;
    L_0x12fe:
        r4 = 3;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 != 0) goto L_0x1312;
    L_0x1305:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r6 = new java.lang.Object[r3];
        r0.postNotificationName(r5, r6);
    L_0x1312:
        r0 = (r21 > r31 ? 1 : (r21 == r31 ? 0 : -1));
        if (r0 != 0) goto L_0x1425;
    L_0x1316:
        r0 = new android.os.Bundle;
        r0.<init>();
        r5 = "onlySelect";
        r0.putBoolean(r5, r11);
        r5 = "dialogsType";
        r0.putInt(r5, r4);
        r4 = "allowSwitchAccount";
        r0.putBoolean(r4, r11);
        r4 = r2.contactsToSend;
        if (r4 == 0) goto L_0x1351;
    L_0x132e:
        r4 = r4.size();
        if (r4 == r11) goto L_0x136d;
    L_0x1334:
        r4 = NUM; // 0x7f0e09f8 float:1.8880214E38 double:1.0531634175E-314;
        r5 = "SendContactToText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertString";
        r0.putString(r5, r4);
        r4 = NUM; // 0x7f0e09e4 float:1.8880173E38 double:1.0531634076E-314;
        r5 = "SendContactToGroupText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertStringGroup";
        r0.putString(r5, r4);
        goto L_0x136d;
    L_0x1351:
        r4 = NUM; // 0x7f0e09f8 float:1.8880214E38 double:1.0531634175E-314;
        r5 = "SendMessagesToText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertString";
        r0.putString(r5, r4);
        r4 = NUM; // 0x7f0e09f7 float:1.8880212E38 double:1.053163417E-314;
        r5 = "SendMessagesToGroupText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertStringGroup";
        r0.putString(r5, r4);
    L_0x136d:
        r13 = new org.telegram.ui.DialogsActivity;
        r13.<init>(r0);
        r13.setDelegate(r2);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1397;
    L_0x137b:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= 0) goto L_0x13b4;
    L_0x1385:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r4 = r0.size();
        r4 = r4 - r11;
        r0 = r0.get(r4);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x13b4;
    L_0x1396:
        goto L_0x13b2;
    L_0x1397:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= r11) goto L_0x13b4;
    L_0x13a1:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r4 = r0.size();
        r4 = r4 - r11;
        r0 = r0.get(r4);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x13b4;
    L_0x13b2:
        r0 = 1;
        goto L_0x13b5;
    L_0x13b4:
        r0 = 0;
    L_0x13b5:
        r14 = r0;
        r12 = r2.actionBarLayout;
        r15 = 1;
        r16 = 1;
        r17 = 0;
        r12.presentFragment(r13, r14, r15, r16, r17);
        r0 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r0 == 0) goto L_0x13d8;
    L_0x13c6:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x13d8;
    L_0x13d0:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0.closePhoto(r3, r3);
        goto L_0x1407;
    L_0x13d8:
        r0 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r0 == 0) goto L_0x13f0;
    L_0x13de:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x13f0;
    L_0x13e8:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0.closePhoto(r3, r11);
        goto L_0x1407;
    L_0x13f0:
        r0 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r0 == 0) goto L_0x1407;
    L_0x13f6:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x1407;
    L_0x1400:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0.close(r3, r11);
    L_0x1407:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x141e;
    L_0x1412:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        goto L_0x121b;
    L_0x141e:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
        goto L_0x121b;
    L_0x1425:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4 = java.lang.Long.valueOf(r21);
        r0.add(r4);
        r4 = 0;
        r2.didSelectDialogs(r4, r0, r4, r3);
        goto L_0x1438;
    L_0x1436:
        r4 = 0;
        r11 = 1;
    L_0x1438:
        r0 = r49;
    L_0x143a:
        r13 = 0;
    L_0x143b:
        if (r13 != 0) goto L_0x14d9;
    L_0x143d:
        if (r0 != 0) goto L_0x14d9;
    L_0x143f:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x148a;
    L_0x1445:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x146b;
    L_0x1451:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x14c4;
    L_0x145b:
        r0 = r2.layersActionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r0.addFragmentToStack(r5);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x14c4;
    L_0x146b:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x14c4;
    L_0x1475:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r5 = r2.sideMenu;
        r0.setSideMenu(r5);
        r5 = r2.actionBarLayout;
        r5.addFragmentToStack(r0);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
        goto L_0x14c4;
    L_0x148a:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x14c4;
    L_0x1494:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x14b0;
    L_0x14a0:
        r0 = r2.actionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r0.addFragmentToStack(r5);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x14c4;
    L_0x14b0:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r5 = r2.sideMenu;
        r0.setSideMenu(r5);
        r5 = r2.actionBarLayout;
        r5.addFragmentToStack(r0);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r11, r3);
    L_0x14c4:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x14d9;
    L_0x14cf:
        r0 = r2.layersActionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
    L_0x14d9:
        r1.setAction(r4);
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$32$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$9$LaunchActivity(int[] iArr, SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new -$$Lambda$LaunchActivity$XTKeVrtgIlPSrnJHpJxLVivhpCY(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$32$LaunchActivity(locationActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:57:0x0284  */
    private void runLinkRequest(int r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, boolean r29, java.lang.Integer r30, java.lang.Integer r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, org.telegram.tgnet.TLRPC.TL_wallPaper r38, java.lang.String r39, int r40) {
        /*
        r21 = this;
        r15 = r21;
        r3 = r22;
        r0 = r23;
        r5 = r24;
        r6 = r25;
        r9 = r28;
        r14 = r33;
        r13 = r34;
        r12 = r35;
        r11 = r38;
        r10 = r39;
        r1 = r40;
        r2 = 2;
        if (r1 != 0) goto L_0x005b;
    L_0x001b:
        r4 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();
        if (r4 < r2) goto L_0x005b;
    L_0x0021:
        if (r14 == 0) goto L_0x005b;
    L_0x0023:
        r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$uLaPByy0gdOc9w323HA3PoQ2cC8;
        r1 = r8;
        r2 = r21;
        r3 = r22;
        r4 = r23;
        r5 = r24;
        r6 = r25;
        r7 = r26;
        r0 = r8;
        r8 = r27;
        r9 = r28;
        r10 = r29;
        r11 = r30;
        r12 = r31;
        r15 = r13;
        r13 = r32;
        r14 = r33;
        r15 = r34;
        r16 = r35;
        r17 = r36;
        r18 = r37;
        r19 = r38;
        r20 = r39;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r4 = r21;
        r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r4, r0);
        r0.show();
        return;
    L_0x005b:
        r4 = r15;
        r15 = r13;
        r7 = NUM; // 0x7f0e0764 float:1.8878875E38 double:1.0531630914E-314;
        r8 = "OK";
        r10 = 0;
        r11 = 1;
        r12 = 0;
        if (r36 == 0) goto L_0x00b1;
    L_0x0067:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
        r0 = r0.hasObservers(r1);
        if (r0 == 0) goto L_0x0081;
    L_0x0073:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
        r2 = new java.lang.Object[r11];
        r2[r12] = r36;
        r0.postNotificationName(r1, r2);
        goto L_0x00b0;
    L_0x0081:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0.<init>(r4);
        r1 = NUM; // 0x7f0e00f8 float:1.887554E38 double:1.053162279E-314;
        r2 = "AppName";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0788 float:1.8878948E38 double:1.053163109E-314;
        r2 = new java.lang.Object[r11];
        r2[r12] = r36;
        r3 = "OtherLoginCode";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r0.setMessage(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r0.setPositiveButton(r1, r10);
        r4.showAlertDialog(r0);
    L_0x00b0:
        return;
    L_0x00b1:
        if (r37 == 0) goto L_0x00db;
    L_0x00b3:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0.<init>(r4);
        r1 = NUM; // 0x7f0e0167 float:1.8875766E38 double:1.053162334E-314;
        r2 = "AuthAnotherClient";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0170 float:1.8875784E38 double:1.0531623384E-314;
        r2 = "AuthAnotherClientUrl";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setMessage(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r0.setPositiveButton(r1, r10);
        r4.showAlertDialog(r0);
        return;
    L_0x00db:
        r7 = new org.telegram.ui.ActionBar.AlertDialog;
        r8 = 3;
        r7.<init>(r4, r8);
        r8 = new int[r11];
        r8[r12] = r12;
        if (r0 == 0) goto L_0x010f;
    L_0x00e7:
        r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
        r1.<init>();
        r1.username = r0;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r22);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$k6pGt3Qt4sxmr3lV_Rl0NIog2fE;
        r33 = r2;
        r34 = r21;
        r35 = r32;
        r36 = r22;
        r37 = r27;
        r38 = r26;
        r39 = r30;
        r40 = r7;
        r33.<init>(r34, r35, r36, r37, r38, r39, r40);
        r0 = r0.sendRequest(r1, r2);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x010f:
        if (r5 == 0) goto L_0x0142;
    L_0x0111:
        if (r1 != 0) goto L_0x012b;
    L_0x0113:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite;
        r0.<init>();
        r0.hash = r5;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22);
        r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$cP-BUXErnntbOCLASSNAMEWOXbRxYV31I;
        r6.<init>(r4, r3, r7, r5);
        r0 = r1.sendRequest(r0, r6, r2);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x012b:
        if (r1 != r11) goto L_0x0328;
    L_0x012d:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
        r0.<init>();
        r0.hash = r5;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22);
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$b0vcBpC7i4V2XHDdbrbQCKVGRv4;
        r5.<init>(r4, r3, r7);
        r1.sendRequest(r0, r5, r2);
        goto L_0x0328;
    L_0x0142:
        if (r6 == 0) goto L_0x0183;
    L_0x0144:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0182;
    L_0x014c:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
        r0.<init>();
        r0.short_name = r6;
        r1 = mainFragmentsStack;
        r2 = r1.size();
        r2 = r2 - r11;
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        r2 = r1 instanceof org.telegram.ui.ChatActivity;
        if (r2 == 0) goto L_0x016c;
    L_0x0164:
        r2 = r1;
        r2 = (org.telegram.ui.ChatActivity) r2;
        r2 = r2.getChatActivityEnterView();
        goto L_0x016d;
    L_0x016c:
        r2 = r10;
    L_0x016d:
        r3 = new org.telegram.ui.Components.StickersAlert;
        r5 = 0;
        r22 = r3;
        r23 = r21;
        r24 = r1;
        r25 = r0;
        r26 = r5;
        r27 = r2;
        r22.<init>(r23, r24, r25, r26, r27);
        r1.showDialog(r3);
    L_0x0182:
        return;
    L_0x0183:
        if (r9 == 0) goto L_0x01a3;
    L_0x0185:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "onlySelect";
        r0.putBoolean(r1, r11);
        r1 = new org.telegram.ui.DialogsActivity;
        r1.<init>(r0);
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$nstaeeHUtCq1UCIaPqfqMBnjOrI;
        r2 = r29;
        r0.<init>(r4, r2, r3, r9);
        r1.setDelegate(r0);
        r4.presentFragment(r1, r12, r11);
        goto L_0x0328;
    L_0x01a3:
        if (r14 == 0) goto L_0x020e;
    L_0x01a5:
        r0 = "bot_id";
        r0 = r14.get(r0);
        r0 = (java.lang.CharSequence) r0;
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r0 = r0.intValue();
        if (r0 != 0) goto L_0x01b8;
    L_0x01b7:
        return;
    L_0x01b8:
        r1 = "payload";
        r1 = r14.get(r1);
        r1 = (java.lang.String) r1;
        r2 = "nonce";
        r2 = r14.get(r2);
        r2 = (java.lang.String) r2;
        r5 = "callback_url";
        r5 = r14.get(r5);
        r5 = (java.lang.String) r5;
        r6 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
        r6.<init>();
        r6.bot_id = r0;
        r0 = "scope";
        r0 = r14.get(r0);
        r0 = (java.lang.String) r0;
        r6.scope = r0;
        r0 = "public_key";
        r0 = r14.get(r0);
        r0 = (java.lang.String) r0;
        r6.public_key = r0;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r22);
        r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$Y8iOWH5Pksz29uJJNv8u7elefb8;
        r23 = r9;
        r24 = r21;
        r25 = r8;
        r26 = r22;
        r27 = r7;
        r28 = r6;
        r29 = r1;
        r30 = r2;
        r31 = r5;
        r23.<init>(r24, r25, r26, r27, r28, r29, r30, r31);
        r0 = r0.sendRequest(r6, r9);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x020e:
        r0 = r35;
        if (r0 == 0) goto L_0x022c;
    L_0x0212:
        r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo;
        r1.<init>();
        r1.path = r0;
        r0 = r4.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$JFoMujQKcmXb-cZb0UBx2oj-r3c;
        r2.<init>(r4, r7);
        r0 = r0.sendRequest(r1, r2);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x022c:
        r0 = "android";
        if (r15 == 0) goto L_0x024c;
    L_0x0230:
        r1 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage;
        r1.<init>();
        r1.lang_code = r15;
        r1.lang_pack = r0;
        r0 = r4.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$7iJyRhApNsveW8Gt05Rio-vwsAg;
        r2.<init>(r4, r7);
        r0 = r0.sendRequest(r1, r2);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x024c:
        r1 = r38;
        if (r1 == 0) goto L_0x02a7;
    L_0x0250:
        r0 = r1.slug;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0281;
    L_0x0258:
        r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper;	 Catch:{ Exception -> 0x027d }
        r2 = "c";
        r5 = r1.settings;	 Catch:{ Exception -> 0x027d }
        r5 = r5.background_color;	 Catch:{ Exception -> 0x027d }
        r6 = r1.settings;	 Catch:{ Exception -> 0x027d }
        r6 = r6.second_background_color;	 Catch:{ Exception -> 0x027d }
        r9 = r1.settings;	 Catch:{ Exception -> 0x027d }
        r9 = r9.rotation;	 Catch:{ Exception -> 0x027d }
        r9 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r9, r12);	 Catch:{ Exception -> 0x027d }
        r0.<init>(r2, r5, r6, r9);	 Catch:{ Exception -> 0x027d }
        r2 = new org.telegram.ui.ThemePreviewActivity;	 Catch:{ Exception -> 0x027d }
        r2.<init>(r0, r10);	 Catch:{ Exception -> 0x027d }
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$vy243eJqlNTq76Ed9pGk-cU6I3g;	 Catch:{ Exception -> 0x027d }
        r0.<init>(r4, r2);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x027d }
        goto L_0x0282;
    L_0x027d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0281:
        r11 = 0;
    L_0x0282:
        if (r11 != 0) goto L_0x0328;
    L_0x0284:
        r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
        r2.<init>();
        r5 = r1.slug;
        r2.slug = r5;
        r0.wallpaper = r2;
        r2 = r4.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$tu9YrzAVkkuCJ6HR12ZmZoIW-70;
        r5.<init>(r4, r7, r1);
        r0 = r2.sendRequest(r0, r5);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x02a7:
        r1 = r39;
        if (r1 == 0) goto L_0x02d2;
    L_0x02ab:
        r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$91sVITdd8BmF_plAlcaK45Yo2t8;
        r10.<init>(r4);
        r2 = new org.telegram.tgnet.TLRPC$TL_account_getTheme;
        r2.<init>();
        r2.format = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug;
        r0.<init>();
        r0.slug = r1;
        r2.theme = r0;
        r0 = r4.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$JCHSxCLASSNAMEpVs5aM4o5NcLmc0aAhE;
        r1.<init>(r4, r7);
        r0 = r0.sendRequest(r2, r1);
        r8[r12] = r0;
        goto L_0x0328;
    L_0x02d2:
        if (r31 == 0) goto L_0x0328;
    L_0x02d4:
        if (r30 == 0) goto L_0x0328;
    L_0x02d6:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = r31.intValue();
        r2 = "chat_id";
        r0.putInt(r2, r1);
        r1 = r30.intValue();
        r2 = "message_id";
        r0.putInt(r2, r1);
        r1 = mainFragmentsStack;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x0303;
    L_0x02f5:
        r1 = mainFragmentsStack;
        r2 = r1.size();
        r2 = r2 - r11;
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        goto L_0x0304;
    L_0x0303:
        r1 = r10;
    L_0x0304:
        if (r1 == 0) goto L_0x0310;
    L_0x0306:
        r2 = org.telegram.messenger.MessagesController.getInstance(r22);
        r2 = r2.checkCanOpenChat(r0, r1);
        if (r2 == 0) goto L_0x0328;
    L_0x0310:
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$8SJjENa-BluOeubBalyuqiaa2Mk;
        r23 = r2;
        r24 = r21;
        r25 = r0;
        r26 = r31;
        r27 = r8;
        r28 = r7;
        r29 = r1;
        r30 = r22;
        r23.<init>(r24, r25, r26, r27, r28, r29, r30);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0328:
        r0 = r8[r12];
        if (r0 == 0) goto L_0x0339;
    L_0x032c:
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$BwVmPHsS9r3JtAebM5EKanHSsKE;
        r0.<init>(r3, r8, r10);
        r7.setOnCancelListener(r0);
        r0 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r7.showDelayed(r0);	 Catch:{ Exception -> 0x0339 }
    L_0x0339:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$11$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TL_wallPaper tL_wallPaper, String str12, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, str7, hashMap, str8, str9, str10, str11, tL_wallPaper, str12, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$16$LaunchActivity(String str, int i, String str2, String str3, Integer num, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$6fWQqOSgt4oDVk9Nqq4IlnlWnm4(this, tLObject, tL_error, str, i, str2, str3, num, alertDialog));
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0133  */
    /* JADX WARNING: Missing block: B:17:0x00a1, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a3;
     */
    /* JADX WARNING: Missing block: B:23:0x00c0, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00a3;
     */
    public /* synthetic */ void lambda$null$15$LaunchActivity(org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC.TL_error r15, java.lang.String r16, int r17, java.lang.String r18, java.lang.String r19, java.lang.Integer r20, org.telegram.ui.ActionBar.AlertDialog r21) {
        /*
        r13 = this;
        r1 = r13;
        r0 = r16;
        r2 = r17;
        r3 = r18;
        r4 = r19;
        r5 = r13.isFinishing();
        if (r5 != 0) goto L_0x0299;
    L_0x000f:
        r5 = r14;
        r5 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r5;
        r6 = 1;
        r7 = 0;
        if (r15 != 0) goto L_0x0277;
    L_0x0016:
        r8 = r1.actionBarLayout;
        if (r8 == 0) goto L_0x0277;
    L_0x001a:
        if (r0 == 0) goto L_0x0026;
    L_0x001c:
        if (r0 == 0) goto L_0x0277;
    L_0x001e:
        r8 = r5.users;
        r8 = r8.isEmpty();
        if (r8 != 0) goto L_0x0277;
    L_0x0026:
        r8 = org.telegram.messenger.MessagesController.getInstance(r17);
        r9 = r5.users;
        r8.putUsers(r9, r7);
        r8 = org.telegram.messenger.MessagesController.getInstance(r17);
        r9 = r5.chats;
        r8.putChats(r9, r7);
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r17);
        r9 = r5.users;
        r10 = r5.chats;
        r8.putUsersAndChats(r9, r10, r7, r6);
        r8 = "dialogsType";
        r9 = "onlySelect";
        if (r0 == 0) goto L_0x013a;
    L_0x0049:
        r3 = new android.os.Bundle;
        r3.<init>();
        r3.putBoolean(r9, r6);
        r4 = "cantSendToChannels";
        r3.putBoolean(r4, r6);
        r3.putInt(r8, r6);
        r4 = NUM; // 0x7f0e09e9 float:1.8880183E38 double:1.05316341E-314;
        r8 = "SendGameToText";
        r4 = org.telegram.messenger.LocaleController.getString(r8, r4);
        r8 = "selectAlertString";
        r3.putString(r8, r4);
        r4 = NUM; // 0x7f0e09e8 float:1.8880181E38 double:1.0531634096E-314;
        r8 = "SendGameToGroupText";
        r4 = org.telegram.messenger.LocaleController.getString(r8, r4);
        r8 = "selectAlertStringGroup";
        r3.putString(r8, r4);
        r4 = new org.telegram.ui.DialogsActivity;
        r4.<init>(r3);
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$SzCpHBmwn3FfXN9hHNqGNhaf7AA;
        r3.<init>(r13, r0, r2, r5);
        r4.setDelegate(r3);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x00a7;
    L_0x0088:
        r0 = r1.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= 0) goto L_0x00a5;
    L_0x0092:
        r0 = r1.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r2 = r0.size();
        r2 = r2 - r6;
        r0 = r0.get(r2);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x00a5;
    L_0x00a3:
        r0 = 1;
        goto L_0x00c3;
    L_0x00a5:
        r0 = 0;
        goto L_0x00c3;
    L_0x00a7:
        r0 = r1.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= r6) goto L_0x00a5;
    L_0x00b1:
        r0 = r1.actionBarLayout;
        r0 = r0.fragmentsStack;
        r2 = r0.size();
        r2 = r2 - r6;
        r0 = r0.get(r2);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x00a5;
    L_0x00c2:
        goto L_0x00a3;
    L_0x00c3:
        r2 = r1.actionBarLayout;
        r3 = 1;
        r5 = 1;
        r8 = 0;
        r14 = r2;
        r15 = r4;
        r16 = r0;
        r17 = r3;
        r18 = r5;
        r19 = r8;
        r14.presentFragment(r15, r16, r17, r18, r19);
        r0 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r0 == 0) goto L_0x00ed;
    L_0x00db:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x00ed;
    L_0x00e5:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0.closePhoto(r7, r7);
        goto L_0x011c;
    L_0x00ed:
        r0 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r0 == 0) goto L_0x0105;
    L_0x00f3:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x0105;
    L_0x00fd:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0.closePhoto(r7, r6);
        goto L_0x011c;
    L_0x0105:
        r0 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r0 == 0) goto L_0x011c;
    L_0x010b:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x011c;
    L_0x0115:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0.close(r7, r6);
    L_0x011c:
        r0 = r1.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r7, r7);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0133;
    L_0x0127:
        r0 = r1.actionBarLayout;
        r0.showLastFragment();
        r0 = r1.rightActionBarLayout;
        r0.showLastFragment();
        goto L_0x0250;
    L_0x0133:
        r0 = r1.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r6, r7);
        goto L_0x0250;
    L_0x013a:
        r0 = 0;
        if (r3 == 0) goto L_0x01a6;
    L_0x013d:
        r4 = r5.users;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x014d;
    L_0x0145:
        r0 = r5.users;
        r0 = r0.get(r7);
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
    L_0x014d:
        if (r0 == 0) goto L_0x0190;
    L_0x014f:
        r4 = r0.bot;
        if (r4 == 0) goto L_0x0158;
    L_0x0153:
        r4 = r0.bot_nochats;
        if (r4 == 0) goto L_0x0158;
    L_0x0157:
        goto L_0x0190;
    L_0x0158:
        r4 = new android.os.Bundle;
        r4.<init>();
        r4.putBoolean(r9, r6);
        r5 = 2;
        r4.putInt(r8, r5);
        r8 = NUM; // 0x7f0e00cd float:1.8875453E38 double:1.053162258E-314;
        r5 = new java.lang.Object[r5];
        r9 = org.telegram.messenger.UserObject.getUserName(r0);
        r5[r7] = r9;
        r7 = "%1$s";
        r5[r6] = r7;
        r7 = "AddToTheGroupAlertText";
        r5 = org.telegram.messenger.LocaleController.formatString(r7, r8, r5);
        r7 = "addToGroupAlertString";
        r4.putString(r7, r5);
        r5 = new org.telegram.ui.DialogsActivity;
        r5.<init>(r4);
        r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$rJYDHlW5yGDb4uth8VI7518EY4g;
        r4.<init>(r13, r2, r0, r3);
        r5.setDelegate(r4);
        r13.lambda$runLinkRequest$32$LaunchActivity(r5);
        goto L_0x0250;
    L_0x0190:
        r0 = "BotCantJoinGroups";
        r2 = NUM; // 0x7f0e01e8 float:1.8876027E38 double:1.0531623977E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x01a1 }
        r0 = android.widget.Toast.makeText(r13, r0, r7);	 Catch:{ Exception -> 0x01a1 }
        r0.show();	 Catch:{ Exception -> 0x01a1 }
        goto L_0x01a5;
    L_0x01a1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01a5:
        return;
    L_0x01a6:
        r3 = new android.os.Bundle;
        r3.<init>();
        r8 = r5.chats;
        r8 = r8.isEmpty();
        if (r8 != 0) goto L_0x01d7;
    L_0x01b3:
        r8 = r5.chats;
        r8 = r8.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        r8 = r8.id;
        r9 = "chat_id";
        r3.putInt(r9, r8);
        r8 = r5.chats;
        r8 = r8.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        r8 = r8.id;
        r8 = -r8;
        r8 = (long) r8;
        r10 = r5.chats;
        r10 = r10.get(r7);
        r10 = (org.telegram.tgnet.TLRPC.Chat) r10;
        goto L_0x01f3;
    L_0x01d7:
        r8 = r5.users;
        r8 = r8.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        r8 = r8.id;
        r9 = "user_id";
        r3.putInt(r9, r8);
        r8 = r5.users;
        r8 = r8.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        r8 = r8.id;
        r8 = (long) r8;
        r10 = r0;
    L_0x01f3:
        if (r4 == 0) goto L_0x0210;
    L_0x01f5:
        r11 = r5.users;
        r11 = r11.size();
        if (r11 <= 0) goto L_0x0210;
    L_0x01fd:
        r5 = r5.users;
        r5 = r5.get(r7);
        r5 = (org.telegram.tgnet.TLRPC.User) r5;
        r5 = r5.bot;
        if (r5 == 0) goto L_0x0210;
    L_0x0209:
        r5 = "botUser";
        r3.putString(r5, r4);
        r5 = 1;
        goto L_0x0211;
    L_0x0210:
        r5 = 0;
    L_0x0211:
        if (r20 == 0) goto L_0x021c;
    L_0x0213:
        r11 = r20.intValue();
        r12 = "message_id";
        r3.putInt(r12, r11);
    L_0x021c:
        r11 = mainFragmentsStack;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x0231;
    L_0x0224:
        r0 = mainFragmentsStack;
        r11 = r0.size();
        r11 = r11 - r6;
        r0 = r0.get(r11);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
    L_0x0231:
        if (r0 == 0) goto L_0x023d;
    L_0x0233:
        r11 = org.telegram.messenger.MessagesController.getInstance(r17);
        r11 = r11.checkCanOpenChat(r3, r0);
        if (r11 == 0) goto L_0x0250;
    L_0x023d:
        if (r5 == 0) goto L_0x0253;
    L_0x023f:
        r5 = r0 instanceof org.telegram.ui.ChatActivity;
        if (r5 == 0) goto L_0x0253;
    L_0x0243:
        r0 = (org.telegram.ui.ChatActivity) r0;
        r11 = r0.getDialogId();
        r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x0253;
    L_0x024d:
        r0.setBotUser(r4);
    L_0x0250:
        r10 = r21;
        goto L_0x028e;
    L_0x0253:
        r0 = org.telegram.messenger.MessagesController.getInstance(r17);
        r2 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r20 != 0) goto L_0x025f;
    L_0x025d:
        r4 = 0;
        goto L_0x0263;
    L_0x025f:
        r4 = r20.intValue();
    L_0x0263:
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$zf4T5X-LU_s-WkYwYEz7FgZAiR4;
        r10 = r21;
        r5.<init>(r13, r10, r3);
        r14 = r0;
        r15 = r8;
        r17 = r2;
        r18 = r4;
        r19 = r5;
        r14.ensureMessagesLoaded(r15, r17, r18, r19);
        r6 = 0;
        goto L_0x028e;
    L_0x0277:
        r10 = r21;
        r0 = "NoUsernameFound";
        r2 = NUM; // 0x7f0e06cc float:1.8878567E38 double:1.0531630163E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x028a }
        r0 = android.widget.Toast.makeText(r13, r0, r7);	 Catch:{ Exception -> 0x028a }
        r0.show();	 Catch:{ Exception -> 0x028a }
        goto L_0x028e;
    L_0x028a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x028e:
        if (r6 == 0) goto L_0x0299;
    L_0x0290:
        r21.dismiss();	 Catch:{ Exception -> 0x0294 }
        goto L_0x0299;
    L_0x0294:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0299:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$15$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$12$LaunchActivity(String str, int i, TL_contacts_resolvedPeer tL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        TL_inputMediaGame tL_inputMediaGame = new TL_inputMediaGame();
        tL_inputMediaGame.id = new TL_inputGameShortName();
        InputGame inputGame = tL_inputMediaGame.id;
        inputGame.short_name = str;
        inputGame.bot_id = MessagesController.getInstance(i).getInputUser((User) tL_contacts_resolvedPeer.users.get(0));
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

    public /* synthetic */ void lambda$null$13$LaunchActivity(int i, User user, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        int i2 = -((int) longValue);
        bundle.putInt("chat_id", i2);
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList arrayList2 = mainFragmentsStack;
            if (!instance.checkCanOpenChat(bundle, (BaseFragment) arrayList2.get(arrayList2.size() - 1))) {
                return;
            }
        }
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(i2, user, null, 0, str, null, null);
        this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
    }

    public /* synthetic */ void lambda$null$14$LaunchActivity(AlertDialog alertDialog, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!isFinishing()) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$20$LaunchActivity(int i, AlertDialog alertDialog, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$9-LWBAQPGpMTJez5aH5UmUrFNg8(this, tL_error, tLObject, i, alertDialog, str));
    }

    /* JADX WARNING: Missing block: B:13:0x0026, code skipped:
            if (android.text.TextUtils.isEmpty(r10.username) == false) goto L_0x0028;
     */
    /* JADX WARNING: Missing block: B:17:0x006e, code skipped:
            if (r14.checkCanOpenChat(r10, (org.telegram.ui.ActionBar.BaseFragment) r0.get(r0.size() - 1)) != false) goto L_0x0070;
     */
    public /* synthetic */ void lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC.TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
        r9 = this;
        r0 = r9.isFinishing();
        if (r0 != 0) goto L_0x00f6;
    L_0x0006:
        r0 = 0;
        r1 = 1;
        if (r10 != 0) goto L_0x00a9;
    L_0x000a:
        r2 = r9.actionBarLayout;
        if (r2 == 0) goto L_0x00a9;
    L_0x000e:
        r11 = (org.telegram.tgnet.TLRPC.ChatInvite) r11;
        r10 = r11.chat;
        if (r10 == 0) goto L_0x0093;
    L_0x0014:
        r10 = org.telegram.messenger.ChatObject.isLeftFromChat(r10);
        if (r10 == 0) goto L_0x0028;
    L_0x001a:
        r10 = r11.chat;
        r2 = r10.kicked;
        if (r2 != 0) goto L_0x0093;
    L_0x0020:
        r10 = r10.username;
        r10 = android.text.TextUtils.isEmpty(r10);
        if (r10 != 0) goto L_0x0093;
    L_0x0028:
        r10 = org.telegram.messenger.MessagesController.getInstance(r12);
        r14 = r11.chat;
        r2 = 0;
        r10.putChat(r14, r2);
        r10 = new java.util.ArrayList;
        r10.<init>();
        r14 = r11.chat;
        r10.add(r14);
        r14 = org.telegram.messenger.MessagesStorage.getInstance(r12);
        r14.putUsersAndChats(r0, r10, r2, r1);
        r10 = new android.os.Bundle;
        r10.<init>();
        r14 = r11.chat;
        r14 = r14.id;
        r0 = "chat_id";
        r10.putInt(r0, r14);
        r14 = mainFragmentsStack;
        r14 = r14.isEmpty();
        if (r14 != 0) goto L_0x0070;
    L_0x0059:
        r14 = org.telegram.messenger.MessagesController.getInstance(r12);
        r0 = mainFragmentsStack;
        r3 = r0.size();
        r3 = r3 - r1;
        r0 = r0.get(r3);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r14 = r14.checkCanOpenChat(r10, r0);
        if (r14 == 0) goto L_0x00ec;
    L_0x0070:
        r14 = new boolean[r1];
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$eFtoK2ipUbUMzowFNBWNIY4_2lw;
        r0.<init>(r14);
        r13.setOnCancelListener(r0);
        r3 = org.telegram.messenger.MessagesController.getInstance(r12);
        r11 = r11.chat;
        r12 = r11.id;
        r12 = -r12;
        r4 = (long) r12;
        r6 = org.telegram.messenger.ChatObject.isChannel(r11);
        r7 = 0;
        r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$EZ3DMx9oOLTpcp5ocRXeEi6Rg20;
        r8.<init>(r9, r13, r14, r10);
        r3.ensureMessagesLoaded(r4, r6, r7, r8);
        r1 = 0;
        goto L_0x00ec;
    L_0x0093:
        r10 = mainFragmentsStack;
        r12 = r10.size();
        r12 = r12 - r1;
        r10 = r10.get(r12);
        r10 = (org.telegram.ui.ActionBar.BaseFragment) r10;
        r12 = new org.telegram.ui.Components.JoinGroupAlert;
        r12.<init>(r9, r11, r14, r10);
        r10.showDialog(r12);
        goto L_0x00ec;
    L_0x00a9:
        r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r11.<init>(r9);
        r12 = NUM; // 0x7f0e00f8 float:1.887554E38 double:1.053162279E-314;
        r14 = "AppName";
        r12 = org.telegram.messenger.LocaleController.getString(r14, r12);
        r11.setTitle(r12);
        r10 = r10.text;
        r12 = "FLOOD_WAIT";
        r10 = r10.startsWith(r12);
        if (r10 == 0) goto L_0x00d1;
    L_0x00c4:
        r10 = NUM; // 0x7f0e04bf float:1.8877502E38 double:1.053162757E-314;
        r12 = "FloodWait";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setMessage(r10);
        goto L_0x00dd;
    L_0x00d1:
        r10 = NUM; // 0x7f0e05a0 float:1.8877958E38 double:1.053162868E-314;
        r12 = "JoinToGroupErrorNotExist";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setMessage(r10);
    L_0x00dd:
        r10 = NUM; // 0x7f0e0764 float:1.8878875E38 double:1.0531630914E-314;
        r12 = "OK";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setPositiveButton(r10, r0);
        r9.showAlertDialog(r11);
    L_0x00ec:
        if (r1 == 0) goto L_0x00f6;
    L_0x00ee:
        r13.dismiss();	 Catch:{ Exception -> 0x00f2 }
        goto L_0x00f6;
    L_0x00f2:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
    L_0x00f6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$19$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$18$LaunchActivity(AlertDialog alertDialog, boolean[] zArr, Bundle bundle) {
        alertDialog.hide();
        if (!zArr[0]) {
            this.actionBarLayout.presentFragment(new ChatActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(i).processUpdates((Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$qAcR4ARhsWqyzh4iuxGrFGyr3io(this, alertDialog, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog alertDialog, TL_error tL_error, TLObject tLObject, int i) {
        if (!isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (tL_error != null) {
                Builder builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                if (tL_error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", NUM));
                } else if (tL_error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                }
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                Updates updates = (Updates) tLObject;
                if (!updates.chats.isEmpty()) {
                    Chat chat = (Chat) updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(i).putUsers(updates.users, false);
                    MessagesController.getInstance(i).putChats(updates.chats, false);
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", chat.id);
                    if (!mainFragmentsStack.isEmpty()) {
                        MessagesController instance = MessagesController.getInstance(i);
                        ArrayList arrayList = mainFragmentsStack;
                        if (!instance.checkCanOpenChat(bundle, (BaseFragment) arrayList.get(arrayList.size() - 1))) {
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
            MediaDataController.getInstance(i).saveDraft(longValue, str, null, null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TL_error tL_error) {
        TL_account_authorizationForm tL_account_authorizationForm = (TL_account_authorizationForm) tLObject;
        if (tL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TL_account_getPassword(), new -$$Lambda$LaunchActivity$9fzXoK_lsi7XQ3uPc0deMdwJnlk(this, alertDialog, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$y93OCK2wG0n4xy6el6-GTNdUEf4(this, alertDialog, tL_error));
    }

    public /* synthetic */ void lambda$null$25$LaunchActivity(AlertDialog alertDialog, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$FgQzMvuTEPVgbmH6VTByLjyySgE(this, alertDialog, tLObject, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3));
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3) {
        TL_account_getAuthorizationForm tL_account_getAuthorizationForm2 = tL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            TL_account_password tL_account_password = (TL_account_password) tLObject;
            MessagesController.getInstance(i).putUsers(tL_account_authorizationForm.users, false);
            lambda$runLinkRequest$32$LaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm2.bot_id, tL_account_getAuthorizationForm2.scope, tL_account_getAuthorizationForm2.public_key, str, str2, str3, tL_account_authorizationForm, tL_account_password));
            return;
        }
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, TL_error tL_error) {
        try {
            alertDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
            stringBuilder.append("\n");
            stringBuilder.append(tL_error.text);
            showAlertDialog(AlertsCreator.createSimpleAlert(this, stringBuilder.toString()));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$tkrYatwYJhNzKTFh-fzro7nWOm4(this, alertDialog, tLObject));
    }

    public /* synthetic */ void lambda$null$28$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_help_deepLinkInfo) {
            TL_help_deepLinkInfo tL_help_deepLinkInfo = (TL_help_deepLinkInfo) tLObject;
            AlertsCreator.showUpdateAppAlert(this, tL_help_deepLinkInfo.message, tL_help_deepLinkInfo.update_app);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$UxfG2S8Qh0Aj3mcK8bDobeM6KiQ(this, alertDialog, tLObject, tL_error));
    }

    public /* synthetic */ void lambda$null$30$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TL_langPackLanguage) tLObject));
        } else if (tL_error != null) {
            if ("LANG_CODE_NOT_SUPPORTED".equals(tL_error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", NUM)));
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
            stringBuilder.append("\n");
            stringBuilder.append(tL_error.text);
            showAlertDialog(AlertsCreator.createSimpleAlert(this, stringBuilder.toString()));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$34$LaunchActivity(AlertDialog alertDialog, TL_wallPaper tL_wallPaper, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$LEBn30Fhml5ULBTC7bqvGtklrLY(this, alertDialog, tLObject, tL_wallPaper, tL_error));
    }

    public /* synthetic */ void lambda$null$33$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_wallPaper tL_wallPaper, TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_wallPaper) {
            Object obj;
            TL_wallPaper tL_wallPaper2 = (TL_wallPaper) tLObject;
            if (tL_wallPaper2.pattern) {
                String str = tL_wallPaper2.slug;
                WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                int i = wallPaperSettings.background_color;
                int i2 = wallPaperSettings.second_background_color;
                int wallpaperRotation = AndroidUtilities.getWallpaperRotation(wallPaperSettings.rotation, false);
                wallPaperSettings = tL_wallPaper.settings;
                ColorWallpaper colorWallpaper = new ColorWallpaper(str, i, i2, wallpaperRotation, ((float) wallPaperSettings.intensity) / 100.0f, wallPaperSettings.motion, null);
                colorWallpaper.pattern = tL_wallPaper2;
            } else {
                obj = tL_wallPaper2;
            }
            ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(obj, null);
            WallPaperSettings wallPaperSettings2 = tL_wallPaper.settings;
            themePreviewActivity.setInitialModes(wallPaperSettings2.blur, wallPaperSettings2.motion);
            lambda$runLinkRequest$32$LaunchActivity(themePreviewActivity);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        stringBuilder.append(tL_error.text);
        showAlertDialog(AlertsCreator.createSimpleAlert(this, stringBuilder.toString()));
    }

    public /* synthetic */ void lambda$runLinkRequest$35$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$37$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$IovdhgD2fVh1M6dzBwNsuS7OBS0(this, tLObject, alertDialog, tL_error));
    }

    public /* synthetic */ void lambda$null$36$LaunchActivity(TLObject tLObject, AlertDialog alertDialog, TL_error tL_error) {
        boolean z;
        if (tLObject instanceof TL_theme) {
            TL_theme tL_theme = (TL_theme) tLObject;
            TL_themeSettings tL_themeSettings = tL_theme.settings;
            boolean z2 = false;
            if (tL_themeSettings != null) {
                ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tL_themeSettings));
                if (theme != null) {
                    TL_wallPaper tL_wallPaper;
                    WallPaper wallPaper = tL_theme.settings.wallpaper;
                    if (wallPaper instanceof TL_wallPaper) {
                        tL_wallPaper = (TL_wallPaper) wallPaper;
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
                    }
                    tL_wallPaper = null;
                    try {
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    openThemeAccentPreview(tL_theme, tL_wallPaper, theme);
                    z = z2;
                }
            } else if (tL_theme.document != null) {
                this.loadingThemeAccent = false;
                this.loadingTheme = tL_theme;
                this.loadingThemeFileName = FileLoader.getAttachFileName(this.loadingTheme.document);
                this.loadingThemeProgressDialog = alertDialog;
                FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, tL_theme, 1, 1);
                z = z2;
            }
            z2 = true;
            z = z2;
        } else {
            if (tL_error != null) {
                if ("THEME_FORMAT_INVALID".equals(tL_error.text)) {
                    z = true;
                }
            }
            z = true;
        }
        if (z) {
            try {
                alertDialog.dismiss();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            String str = "Theme";
            if (z) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(str, NUM), LocaleController.getString("ThemeNotSupported", NUM)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(str, NUM), LocaleController.getString("ThemeNotFound", NUM)));
            }
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$40$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TL_channels_getChannels tL_channels_getChannels = new TL_channels_getChannels();
            TL_inputChannel tL_inputChannel = new TL_inputChannel();
            tL_inputChannel.channel_id = num.intValue();
            tL_channels_getChannels.id.add(tL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getChannels, new -$$Lambda$LaunchActivity$u7AoiQLKUaAK1H-Rp0gmQmjICv4(this, alertDialog, baseFragment, i, bundle));
        }
    }

    public /* synthetic */ void lambda$null$39$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$hd3iY2vGFtOQ6SZMSsvlSbb79yg(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    public /* synthetic */ void lambda$null$38$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Object obj = 1;
        if (tLObject instanceof TL_messages_chats) {
            TL_messages_chats tL_messages_chats = (TL_messages_chats) tLObject;
            if (!tL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chats.chats, false);
                Chat chat = (Chat) tL_messages_chats.chats.get(0);
                if (baseFragment == null || MessagesController.getInstance(i).checkCanOpenChat(bundle, baseFragment)) {
                    this.actionBarLayout.presentFragment(new ChatActivity(bundle));
                }
                obj = null;
            }
        }
        if (obj != null) {
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
        if ((!z && BuildVars.DEBUG_VERSION) || (!z && !BuildVars.CHECK_UPDATES)) {
            return;
        }
        if (z || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(0).lastUpdateCheckTime) >= 86400000) {
            TL_help_getAppUpdate tL_help_getAppUpdate = new TL_help_getAppUpdate();
            try {
                tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tL_help_getAppUpdate.source == null) {
                tL_help_getAppUpdate.source = "";
            }
            int i = this.currentAccount;
            ConnectionsManager.getInstance(i).sendRequest(tL_help_getAppUpdate, new -$$Lambda$LaunchActivity$WW5YFuc_0ROtIwfpToCN-t4BlFE(this, i));
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$43$LaunchActivity(int i, TLObject tLObject, TL_error tL_error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (tLObject instanceof TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$bC-ee5mpFxQ3lILJQUCgtRFqMiw(this, (TL_help_appUpdate) tLObject, i));
        }
    }

    public /* synthetic */ void lambda$null$42$LaunchActivity(TL_help_appUpdate tL_help_appUpdate, int i) {
        if (tL_help_appUpdate.can_not_skip) {
            UserConfig.getInstance(0).pendingAppUpdate = tL_help_appUpdate;
            UserConfig.getInstance(0).pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Exception e) {
                FileLog.e(e);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = 0;
            }
            UserConfig.getInstance(0).saveConfig(false);
            showUpdateActivity(i, tL_help_appUpdate, false);
            return;
        }
        new UpdateAppAlertDialog(this, tL_help_appUpdate, i).show();
    }

    public AlertDialog showAlertDialog(Builder builder) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new -$$Lambda$LaunchActivity$CZZJRxwhOU8hQoa4FFVu3lcGepE(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e(e2);
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
                    FileLog.e(e);
                }
                this.localeDialog = null;
            } else if (alertDialog == this.proxyErrorDialog) {
                MessagesController.getGlobalMainSettings();
                Editor edit = MessagesController.getGlobalMainSettings().edit();
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

    /* Access modifiers changed, original: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        BaseFragment baseFragment = dialogsActivity;
        ArrayList<Long> arrayList2 = arrayList;
        ArrayList arrayList3 = this.contactsToSend;
        int i = 0;
        int size = arrayList3 != null ? arrayList3.size() + 0 : 0;
        if (this.videoPath != null) {
            size++;
        }
        ArrayList arrayList4 = this.photoPathsArray;
        if (arrayList4 != null) {
            size += arrayList4.size();
        }
        arrayList4 = this.documentsPathsArray;
        if (arrayList4 != null) {
            size += arrayList4.size();
        }
        arrayList4 = this.documentsUrisArray;
        if (arrayList4 != null) {
            size += arrayList4.size();
        }
        if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
            size++;
        }
        int i2 = 0;
        while (true) {
            boolean z2 = true;
            long longValue;
            if (i2 < arrayList.size()) {
                longValue = ((Long) arrayList2.get(i2)).longValue();
                int i3 = this.currentAccount;
                if (size <= 1) {
                    z2 = false;
                }
                if (!AlertsCreator.checkSlowMode(this, i3, longValue, z2)) {
                    i2++;
                } else {
                    return;
                }
            }
            BaseFragment chatActivity;
            size = baseFragment != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
            if (arrayList.size() <= 1) {
                longValue = ((Long) arrayList2.get(0)).longValue();
                i2 = (int) longValue;
                int i4 = (int) (longValue >> 32);
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (!AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(size).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                if (i2 == 0) {
                    bundle.putInt("enc_id", i4);
                } else if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else if (i2 < 0) {
                    bundle.putInt("chat_id", -i2);
                }
                if (MessagesController.getInstance(size).checkCanOpenChat(bundle, baseFragment)) {
                    chatActivity = new ChatActivity(bundle);
                } else {
                    return;
                }
            }
            chatActivity = null;
            ArrayList arrayList5 = this.contactsToSend;
            if (arrayList5 == null || arrayList5.size() != 1 || mainFragmentsStack.isEmpty()) {
                int i5 = 0;
                while (i5 < arrayList.size()) {
                    String str;
                    ArrayList arrayList6;
                    long longValue2 = ((Long) arrayList2.get(i5)).longValue();
                    AccountInstance instance = AccountInstance.getInstance(UserConfig.selectedAccount);
                    if (chatActivity != null) {
                        this.actionBarLayout.presentFragment(chatActivity, baseFragment != null, baseFragment == null, true, false);
                        str = this.videoPath;
                        if (str != null) {
                            chatActivity.openVideoEditor(str, this.sendingText);
                            this.sendingText = null;
                        }
                    }
                    if (this.photoPathsArray != null) {
                        str = this.sendingText;
                        if (str != null && str.length() <= 1024 && this.photoPathsArray.size() == 1) {
                            ((SendingMediaInfo) this.photoPathsArray.get(i)).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        i = 1024;
                        SendMessagesHelper.prepareSendingMedia(instance, this.photoPathsArray, longValue2, null, null, false, false, null, true, 0);
                    } else {
                        i = 1024;
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        String str2;
                        str = this.sendingText;
                        if (str != null && str.length() <= r4) {
                            arrayList6 = this.documentsPathsArray;
                            i = arrayList6 != null ? arrayList6.size() : 0;
                            ArrayList arrayList7 = this.documentsUrisArray;
                            if (i + (arrayList7 != null ? arrayList7.size() : 0) == 1) {
                                String str3 = this.sendingText;
                                this.sendingText = null;
                                str2 = str3;
                                SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str2, this.documentsMimeType, longValue2, null, null, null, true, 0);
                            }
                        }
                        str2 = null;
                        SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str2, this.documentsMimeType, longValue2, null, null, null, true, 0);
                    }
                    String str4 = this.sendingText;
                    if (str4 != null) {
                        SendMessagesHelper.prepareSendingText(instance, str4, longValue2, true, 0);
                    }
                    arrayList6 = this.contactsToSend;
                    if (!(arrayList6 == null || arrayList6.isEmpty())) {
                        for (i = 0; i < this.contactsToSend.size(); i++) {
                            SendMessagesHelper.getInstance(size).sendMessage((User) this.contactsToSend.get(i), longValue2, null, null, null, true, 0);
                        }
                    }
                    if (!TextUtils.isEmpty(charSequence)) {
                        SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue2, true, 0);
                    }
                    i5++;
                    i = 0;
                }
            } else {
                arrayList5 = mainFragmentsStack;
                PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert((BaseFragment) arrayList5.get(arrayList5.size() - 1), null, null, this.contactsToSendUri, null, null);
                phonebookShareAlert.setDelegate(new -$$Lambda$LaunchActivity$On6dW61nPJlK6ZHSjQj2oAOHWYk(this, chatActivity, arrayList2, size));
                ArrayList arrayList8 = mainFragmentsStack;
                ((BaseFragment) arrayList8.get(arrayList8.size() - 1)).showDialog(phonebookShareAlert);
            }
            if (baseFragment != null && chatActivity == null) {
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

    public /* synthetic */ void lambda$didSelectDialogs$45$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, User user, boolean z, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SendMessagesHelper.getInstance(i).sendMessage(user, ((Long) arrayList.get(i3)).longValue(), null, null, null, z, i2);
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

    /* Access modifiers changed, original: protected */
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
        ArrayList arrayList;
        ThemeEditorView instance2 = ThemeEditorView.getInstance();
        if (instance2 != null) {
            instance2.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            arrayList = this.actionBarLayout.fragmentsStack;
            ((BaseFragment) arrayList.get(arrayList.size() - 1)).onActivityResultFragment(i, i2, intent);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                arrayList = this.rightActionBarLayout.fragmentsStack;
                ((BaseFragment) arrayList.get(arrayList.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                arrayList = this.layersActionBarLayout.fragmentsStack;
                ((BaseFragment) arrayList.get(arrayList.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        ArrayList arrayList;
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr == null) {
            iArr = new int[0];
        }
        if (strArr == null) {
            strArr = new String[0];
        }
        Object obj = (iArr.length <= 0 || iArr[0] != 0) ? null : 1;
        if (i == 4) {
            if (obj == null) {
                showPermissionErrorAlert(LocaleController.getString("PermissionStorage", NUM));
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (i != 5) {
            String str = "PermissionNoCamera";
            if (i == 3) {
                int min = Math.min(strArr.length, iArr.length);
                Object obj2 = 1;
                Object obj3 = 1;
                for (int i2 = 0; i2 < min; i2++) {
                    if ("android.permission.RECORD_AUDIO".equals(strArr[i2])) {
                        obj2 = iArr[i2] == 0 ? 1 : null;
                    } else {
                        if ("android.permission.CAMERA".equals(strArr[i2])) {
                            obj3 = iArr[i2] == 0 ? 1 : null;
                        }
                    }
                }
                if (obj2 == null) {
                    showPermissionErrorAlert(LocaleController.getString("PermissionNoAudio", NUM));
                } else if (obj3 == null) {
                    showPermissionErrorAlert(LocaleController.getString(str, NUM));
                } else {
                    if (SharedConfig.inappCamera) {
                        CameraController.getInstance().initCamera(null);
                    }
                    return;
                }
            } else if (i == 18 || i == 19 || i == 20 || i == 22) {
                if (obj == null) {
                    showPermissionErrorAlert(LocaleController.getString(str, NUM));
                }
            } else if (i == 2 && obj != null) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
            }
        } else if (obj == null) {
            showPermissionErrorAlert(LocaleController.getString("PermissionContacts", NUM));
            return;
        } else {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            arrayList = this.actionBarLayout.fragmentsStack;
            ((BaseFragment) arrayList.get(arrayList.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                arrayList = this.rightActionBarLayout.fragmentsStack;
                ((BaseFragment) arrayList.get(arrayList.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                arrayList = this.layersActionBarLayout.fragmentsStack;
                ((BaseFragment) arrayList.get(arrayList.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
        }
    }

    private void showPermissionErrorAlert(String str) {
        Builder builder = new Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LaunchActivity$qxpfdR3NCwupetvTW7FjpHCgK1o(this));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$46$LaunchActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(4096));
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(-$$Lambda$LaunchActivity$y5_tfrAMtFFLutJ-qWoFMvar_cgY.INSTANCE);
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onPause();
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

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    /* Access modifiers changed, original: protected */
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    /* Access modifiers changed, original: protected */
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
            FileLog.e(e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        super.onDestroy();
        onFinish();
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(4096));
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(-$$Lambda$LaunchActivity$Dhs5NjrIwUgtS8tj3dMdC4kmzMQ.INSTANCE);
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
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null) {
                MediaController.getInstance().seekToProgress(playingMessageObject, playingMessageObject.audioProgress);
            }
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

    /* JADX WARNING: Removed duplicated region for block: B:139:0x0427  */
    public void didReceivedNotification(int r13, int r14, java.lang.Object... r15) {
        /*
        r12 = this;
        r0 = org.telegram.messenger.NotificationCenter.appDidLogout;
        if (r13 != r0) goto L_0x0009;
    L_0x0004:
        r12.switchToAvailableAccountOrLogout();
        goto L_0x0568;
    L_0x0009:
        r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r1 = 0;
        if (r13 != r0) goto L_0x001a;
    L_0x000e:
        r13 = r15[r1];
        if (r13 == r12) goto L_0x0568;
    L_0x0012:
        r12.onFinish();
        r12.finish();
        goto L_0x0568;
    L_0x001a:
        r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState;
        if (r13 != r0) goto L_0x0049;
    L_0x001e:
        r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r14);
        r13 = r13.getConnectionState();
        r15 = r12.currentConnectionState;
        if (r15 == r13) goto L_0x0568;
    L_0x002a:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r15 == 0) goto L_0x0042;
    L_0x002e:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r0 = "switch to state ";
        r15.append(r0);
        r15.append(r13);
        r15 = r15.toString();
        org.telegram.messenger.FileLog.d(r15);
    L_0x0042:
        r12.currentConnectionState = r13;
        r12.updateCurrentConnectionState(r14);
        goto L_0x0568;
    L_0x0049:
        r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged;
        if (r13 != r0) goto L_0x0054;
    L_0x004d:
        r13 = r12.drawerLayoutAdapter;
        r13.notifyDataSetChanged();
        goto L_0x0568;
    L_0x0054:
        r0 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = NUM; // 0x7f0e0213 float:1.8876115E38 double:1.053162419E-314;
        r3 = "Cancel";
        r4 = NUM; // 0x7f0e00f8 float:1.887554E38 double:1.053162279E-314;
        r5 = "AppName";
        r6 = 3;
        r7 = 2;
        r8 = NUM; // 0x7f0e0764 float:1.8878875E38 double:1.0531630914E-314;
        r9 = "OK";
        r10 = 0;
        r11 = 1;
        if (r13 != r0) goto L_0x0187;
    L_0x006b:
        r13 = r15[r1];
        r13 = (java.lang.Integer) r13;
        r0 = r13.intValue();
        if (r0 != r6) goto L_0x007a;
    L_0x0075:
        r0 = r12.proxyErrorDialog;
        if (r0 == 0) goto L_0x007a;
    L_0x0079:
        return;
    L_0x007a:
        r0 = r13.intValue();
        r1 = 4;
        if (r0 != r1) goto L_0x0089;
    L_0x0081:
        r13 = r15[r11];
        r13 = (org.telegram.tgnet.TLRPC.TL_help_termsOfService) r13;
        r12.showTosActivity(r14, r13);
        return;
    L_0x0089:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0.<init>(r12);
        r1 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r0.setTitle(r1);
        r1 = r13.intValue();
        if (r1 == r7) goto L_0x00b2;
    L_0x009b:
        r1 = r13.intValue();
        if (r1 == r6) goto L_0x00b2;
    L_0x00a1:
        r1 = NUM; // 0x7f0e0670 float:1.887838E38 double:1.053162971E-314;
        r4 = "MoreInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r4, r1);
        r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$NSo2b96RBrvjx_PKPJS1uHaK_D0;
        r4.<init>(r14);
        r0.setNegativeButton(r1, r4);
    L_0x00b2:
        r14 = r13.intValue();
        r1 = 5;
        if (r14 != r1) goto L_0x00ce;
    L_0x00b9:
        r13 = NUM; // 0x7f0e06d0 float:1.8878575E38 double:1.0531630183E-314;
        r14 = "NobodyLikesSpam3";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0.setMessage(r13);
        r13 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r0.setPositiveButton(r13, r10);
        goto L_0x0169;
    L_0x00ce:
        r14 = r13.intValue();
        if (r14 != 0) goto L_0x00e9;
    L_0x00d4:
        r13 = NUM; // 0x7f0e06ce float:1.887857E38 double:1.0531630173E-314;
        r14 = "NobodyLikesSpam1";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0.setMessage(r13);
        r13 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r0.setPositiveButton(r13, r10);
        goto L_0x0169;
    L_0x00e9:
        r14 = r13.intValue();
        if (r14 != r11) goto L_0x0103;
    L_0x00ef:
        r13 = NUM; // 0x7f0e06cf float:1.8878573E38 double:1.053163018E-314;
        r14 = "NobodyLikesSpam2";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0.setMessage(r13);
        r13 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r0.setPositiveButton(r13, r10);
        goto L_0x0169;
    L_0x0103:
        r14 = r13.intValue();
        if (r14 != r7) goto L_0x013d;
    L_0x0109:
        r13 = r15[r11];
        r13 = (java.lang.String) r13;
        r0.setMessage(r13);
        r13 = r15[r7];
        r13 = (java.lang.String) r13;
        r14 = "AUTH_KEY_DROP_";
        r13 = r13.startsWith(r14);
        if (r13 == 0) goto L_0x0135;
    L_0x011c:
        r13 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setPositiveButton(r13, r10);
        r13 = NUM; // 0x7f0e05f4 float:1.8878129E38 double:1.0531629096E-314;
        r14 = "LogOut";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r14 = new org.telegram.ui.-$$Lambda$LaunchActivity$DcnfwOlEX5NnGZyS78k4skpxFsw;
        r14.<init>(r12);
        r0.setNegativeButton(r13, r14);
        goto L_0x0169;
    L_0x0135:
        r13 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r0.setPositiveButton(r13, r10);
        goto L_0x0169;
    L_0x013d:
        r13 = r13.intValue();
        if (r13 != r6) goto L_0x0169;
    L_0x0143:
        r13 = NUM; // 0x7f0e0922 float:1.887978E38 double:1.053163312E-314;
        r14 = "Proxy";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0.setTitle(r13);
        r13 = NUM; // 0x7f0e0b6f float:1.8880974E38 double:1.053163603E-314;
        r14 = "UseProxyTelegramError";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0.setMessage(r13);
        r13 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r0.setPositiveButton(r13, r10);
        r13 = r12.showAlertDialog(r0);
        r12.proxyErrorDialog = r13;
        return;
    L_0x0169:
        r13 = mainFragmentsStack;
        r13 = r13.isEmpty();
        if (r13 != 0) goto L_0x0568;
    L_0x0171:
        r13 = mainFragmentsStack;
        r14 = r13.size();
        r14 = r14 - r11;
        r13 = r13.get(r14);
        r13 = (org.telegram.ui.ActionBar.BaseFragment) r13;
        r14 = r0.create();
        r13.showDialog(r14);
        goto L_0x0568;
    L_0x0187:
        r0 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation;
        if (r13 != r0) goto L_0x01dd;
    L_0x018b:
        r13 = r15[r1];
        r13 = (java.util.HashMap) r13;
        r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r15.<init>(r12);
        r0 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r15.setTitle(r0);
        r0 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r15.setPositiveButton(r0, r10);
        r0 = NUM; // 0x7f0e0a3e float:1.8880356E38 double:1.053163452E-314;
        r1 = "ShareYouLocationUnableManually";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo;
        r1.<init>(r12, r13, r14);
        r15.setNegativeButton(r0, r1);
        r13 = NUM; // 0x7f0e0a3d float:1.8880354E38 double:1.0531634516E-314;
        r14 = "ShareYouLocationUnable";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r15.setMessage(r13);
        r13 = mainFragmentsStack;
        r13 = r13.isEmpty();
        if (r13 != 0) goto L_0x0568;
    L_0x01c7:
        r13 = mainFragmentsStack;
        r14 = r13.size();
        r14 = r14 - r11;
        r13 = r13.get(r14);
        r13 = (org.telegram.ui.ActionBar.BaseFragment) r13;
        r14 = r15.create();
        r13.showDialog(r14);
        goto L_0x0568;
    L_0x01dd:
        r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper;
        if (r13 != r0) goto L_0x01f0;
    L_0x01e1:
        r13 = r12.sideMenu;
        if (r13 == 0) goto L_0x0568;
    L_0x01e5:
        r13 = r13.getChildAt(r1);
        if (r13 == 0) goto L_0x0568;
    L_0x01eb:
        r13.invalidate();
        goto L_0x0568;
    L_0x01f0:
        r0 = org.telegram.messenger.NotificationCenter.didSetPasscode;
        if (r13 != r0) goto L_0x0226;
    L_0x01f4:
        r13 = org.telegram.messenger.SharedConfig.passcodeHash;
        r13 = r13.length();
        r14 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        if (r13 <= 0) goto L_0x0211;
    L_0x01fe:
        r13 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r13 != 0) goto L_0x0211;
    L_0x0202:
        r13 = r12.getWindow();	 Catch:{ Exception -> 0x020b }
        r13.setFlags(r14, r14);	 Catch:{ Exception -> 0x020b }
        goto L_0x0568;
    L_0x020b:
        r13 = move-exception;
        org.telegram.messenger.FileLog.e(r13);
        goto L_0x0568;
    L_0x0211:
        r13 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment();
        if (r13 != 0) goto L_0x0568;
    L_0x0217:
        r13 = r12.getWindow();	 Catch:{ Exception -> 0x0220 }
        r13.clearFlags(r14);	 Catch:{ Exception -> 0x0220 }
        goto L_0x0568;
    L_0x0220:
        r13 = move-exception;
        org.telegram.messenger.FileLog.e(r13);
        goto L_0x0568;
    L_0x0226:
        r0 = org.telegram.messenger.NotificationCenter.reloadInterface;
        if (r13 != r0) goto L_0x0247;
    L_0x022a:
        r13 = mainFragmentsStack;
        r13 = r13.size();
        if (r13 <= r11) goto L_0x0242;
    L_0x0232:
        r13 = mainFragmentsStack;
        r14 = r13.size();
        r14 = r14 - r11;
        r13 = r13.get(r14);
        r13 = r13 instanceof org.telegram.ui.SettingsActivity;
        if (r13 == 0) goto L_0x0242;
    L_0x0241:
        r1 = 1;
    L_0x0242:
        r12.rebuildAllFragments(r1);
        goto L_0x0568;
    L_0x0247:
        r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack;
        if (r13 != r0) goto L_0x0250;
    L_0x024b:
        r12.showLanguageAlert(r1);
        goto L_0x0568;
    L_0x0250:
        r0 = org.telegram.messenger.NotificationCenter.openArticle;
        if (r13 != r0) goto L_0x0282;
    L_0x0254:
        r13 = mainFragmentsStack;
        r13 = r13.isEmpty();
        if (r13 == 0) goto L_0x025d;
    L_0x025c:
        return;
    L_0x025d:
        r13 = org.telegram.ui.ArticleViewer.getInstance();
        r14 = mainFragmentsStack;
        r0 = r14.size();
        r0 = r0 - r11;
        r14 = r14.get(r0);
        r14 = (org.telegram.ui.ActionBar.BaseFragment) r14;
        r13.setParentActivity(r12, r14);
        r13 = org.telegram.ui.ArticleViewer.getInstance();
        r14 = r15[r1];
        r14 = (org.telegram.tgnet.TLRPC.TL_webPage) r14;
        r15 = r15[r11];
        r15 = (java.lang.String) r15;
        r13.open(r14, r15);
        goto L_0x0568;
    L_0x0282:
        r0 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport;
        if (r13 != r0) goto L_0x0307;
    L_0x0286:
        r13 = r12.actionBarLayout;
        if (r13 == 0) goto L_0x0306;
    L_0x028a:
        r13 = r13.fragmentsStack;
        r13 = r13.isEmpty();
        if (r13 == 0) goto L_0x0293;
    L_0x0292:
        goto L_0x0306;
    L_0x0293:
        r13 = r15[r1];
        r13 = (java.lang.Integer) r13;
        r13.intValue();
        r13 = r15[r11];
        r13 = (java.util.HashMap) r13;
        r0 = r15[r7];
        r0 = (java.lang.Boolean) r0;
        r0 = r0.booleanValue();
        r15 = r15[r6];
        r15 = (java.lang.Boolean) r15;
        r15 = r15.booleanValue();
        r4 = r12.actionBarLayout;
        r4 = r4.fragmentsStack;
        r5 = r4.size();
        r5 = r5 - r11;
        r4 = r4.get(r5);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5.<init>(r12);
        r6 = NUM; // 0x7f0e0b4f float:1.888091E38 double:1.053163587E-314;
        r7 = "UpdateContactsTitle";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5.setTitle(r6);
        r6 = NUM; // 0x7f0e0b4e float:1.8880907E38 double:1.0531635865E-314;
        r7 = "UpdateContactsMessage";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5.setMessage(r6);
        r6 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$yWZ09sr4SZBZ89evaU7BeAPv2KY;
        r7.<init>(r14, r13, r0, r15);
        r5.setPositiveButton(r6, r7);
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$_P5bEvhUvviSlsVoTEo8pfvXBqo;
        r3.<init>(r14, r13, r0, r15);
        r5.setNegativeButton(r2, r3);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$5KIejsOQETE7-muLZLFyiXcHku8;
        r2.<init>(r14, r13, r0, r15);
        r5.setOnBackButtonListener(r2);
        r13 = r5.create();
        r4.showDialog(r13);
        r13.setCanceledOnTouchOutside(r1);
        goto L_0x0568;
    L_0x0306:
        return;
    L_0x0307:
        r14 = org.telegram.messenger.NotificationCenter.didSetNewTheme;
        r0 = 21;
        if (r13 != r14) goto L_0x0367;
    L_0x030d:
        r13 = r15[r1];
        r13 = (java.lang.Boolean) r13;
        r13 = r13.booleanValue();
        if (r13 != 0) goto L_0x0356;
    L_0x0317:
        r13 = r12.sideMenu;
        if (r13 == 0) goto L_0x0341;
    L_0x031b:
        r14 = "chats_menuBackground";
        r15 = org.telegram.ui.ActionBar.Theme.getColor(r14);
        r13.setBackgroundColor(r15);
        r13 = r12.sideMenu;
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);
        r13.setGlowColor(r14);
        r13 = r12.sideMenu;
        r14 = "listSelectorSDK21";
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);
        r13.setListSelectorColor(r14);
        r13 = r12.sideMenu;
        r13 = r13.getAdapter();
        r13.notifyDataSetChanged();
    L_0x0341:
        r13 = android.os.Build.VERSION.SDK_INT;
        if (r13 < r0) goto L_0x0356;
    L_0x0345:
        r13 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x0356 }
        r14 = "actionBarDefault";
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);	 Catch:{ Exception -> 0x0356 }
        r15 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r14 = r14 | r15;
        r13.<init>(r10, r10, r14);	 Catch:{ Exception -> 0x0356 }
        r12.setTaskDescription(r13);	 Catch:{ Exception -> 0x0356 }
    L_0x0356:
        r13 = r12.drawerLayoutContainer;
        r14 = "windowBackgroundWhite";
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);
        r13.setBehindKeyboardColor(r14);
        r12.checkSystemBarColors();
        goto L_0x0568;
    L_0x0367:
        r14 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        if (r13 != r14) goto L_0x0433;
    L_0x036b:
        r13 = android.os.Build.VERSION.SDK_INT;
        if (r13 < r0) goto L_0x0407;
    L_0x036f:
        r13 = r15[r7];
        if (r13 == 0) goto L_0x0407;
    L_0x0373:
        r13 = r12.themeSwitchImageView;
        r13 = r13.getVisibility();
        if (r13 != 0) goto L_0x037c;
    L_0x037b:
        return;
    L_0x037c:
        r13 = r15[r7];	 Catch:{ all -> 0x0407 }
        r13 = (int[]) r13;	 Catch:{ all -> 0x0407 }
        r14 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r14 = r14.getMeasuredWidth();	 Catch:{ all -> 0x0407 }
        r0 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r0 = r0.getMeasuredHeight();	 Catch:{ all -> 0x0407 }
        r2 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r2 = r2.getMeasuredWidth();	 Catch:{ all -> 0x0407 }
        r3 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r3 = r3.getMeasuredHeight();	 Catch:{ all -> 0x0407 }
        r4 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0407 }
        r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4);	 Catch:{ all -> 0x0407 }
        r3 = new android.graphics.Canvas;	 Catch:{ all -> 0x0407 }
        r3.<init>(r2);	 Catch:{ all -> 0x0407 }
        r4 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r4.draw(r3);	 Catch:{ all -> 0x0407 }
        r3 = r12.themeSwitchImageView;	 Catch:{ all -> 0x0407 }
        r3.setImageBitmap(r2);	 Catch:{ all -> 0x0407 }
        r2 = r12.themeSwitchImageView;	 Catch:{ all -> 0x0407 }
        r2.setVisibility(r1);	 Catch:{ all -> 0x0407 }
        r2 = r13[r1];	 Catch:{ all -> 0x0407 }
        r2 = r14 - r2;
        r3 = r13[r1];	 Catch:{ all -> 0x0407 }
        r14 = r14 - r3;
        r2 = r2 * r14;
        r14 = r13[r11];	 Catch:{ all -> 0x0407 }
        r14 = r0 - r14;
        r3 = r13[r11];	 Catch:{ all -> 0x0407 }
        r3 = r0 - r3;
        r14 = r14 * r3;
        r2 = r2 + r14;
        r2 = (double) r2;	 Catch:{ all -> 0x0407 }
        r2 = java.lang.Math.sqrt(r2);	 Catch:{ all -> 0x0407 }
        r14 = r13[r1];	 Catch:{ all -> 0x0407 }
        r4 = r13[r1];	 Catch:{ all -> 0x0407 }
        r14 = r14 * r4;
        r4 = r13[r11];	 Catch:{ all -> 0x0407 }
        r4 = r0 - r4;
        r5 = r13[r11];	 Catch:{ all -> 0x0407 }
        r0 = r0 - r5;
        r4 = r4 * r0;
        r14 = r14 + r4;
        r4 = (double) r14;	 Catch:{ all -> 0x0407 }
        r4 = java.lang.Math.sqrt(r4);	 Catch:{ all -> 0x0407 }
        r2 = java.lang.Math.max(r2, r4);	 Catch:{ all -> 0x0407 }
        r14 = (float) r2;	 Catch:{ all -> 0x0407 }
        r0 = r12.drawerLayoutContainer;	 Catch:{ all -> 0x0407 }
        r2 = r13[r1];	 Catch:{ all -> 0x0407 }
        r13 = r13[r11];	 Catch:{ all -> 0x0407 }
        r3 = 0;
        r13 = android.view.ViewAnimationUtils.createCircularReveal(r0, r2, r13, r3, r14);	 Catch:{ all -> 0x0407 }
        r2 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        r13.setDuration(r2);	 Catch:{ all -> 0x0407 }
        r14 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN_OUT_QUAD;	 Catch:{ all -> 0x0407 }
        r13.setInterpolator(r14);	 Catch:{ all -> 0x0407 }
        r14 = new org.telegram.ui.LaunchActivity$5;	 Catch:{ all -> 0x0407 }
        r14.<init>();	 Catch:{ all -> 0x0407 }
        r13.addListener(r14);	 Catch:{ all -> 0x0407 }
        r13.start();	 Catch:{ all -> 0x0407 }
        r13 = 1;
        goto L_0x0408;
    L_0x0407:
        r13 = 0;
    L_0x0408:
        r14 = r15[r1];
        r14 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r14;
        r0 = r15[r11];
        r0 = (java.lang.Boolean) r0;
        r0 = r0.booleanValue();
        r15 = r15[r6];
        r15 = (java.lang.Integer) r15;
        r15 = r15.intValue();
        r1 = r12.actionBarLayout;
        r1.animateThemedValues(r14, r15, r0, r13);
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r1 == 0) goto L_0x0568;
    L_0x0427:
        r1 = r12.layersActionBarLayout;
        r1.animateThemedValues(r14, r15, r0, r13);
        r1 = r12.rightActionBarLayout;
        r1.animateThemedValues(r14, r15, r0, r13);
        goto L_0x0568;
    L_0x0433:
        r14 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated;
        if (r13 != r14) goto L_0x0464;
    L_0x0437:
        r13 = r12.sideMenu;
        if (r13 == 0) goto L_0x0568;
    L_0x043b:
        r14 = r15[r1];
        r14 = (java.lang.Integer) r14;
        r13 = r13.getChildCount();
    L_0x0443:
        if (r1 >= r13) goto L_0x0568;
    L_0x0445:
        r15 = r12.sideMenu;
        r15 = r15.getChildAt(r1);
        r0 = r15 instanceof org.telegram.ui.Cells.DrawerUserCell;
        if (r0 == 0) goto L_0x0461;
    L_0x044f:
        r0 = r15;
        r0 = (org.telegram.ui.Cells.DrawerUserCell) r0;
        r0 = r0.getAccountNumber();
        r2 = r14.intValue();
        if (r0 != r2) goto L_0x0461;
    L_0x045c:
        r15.invalidate();
        goto L_0x0568;
    L_0x0461:
        r1 = r1 + 1;
        goto L_0x0443;
    L_0x0464:
        r14 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert;
        if (r13 != r14) goto L_0x0473;
    L_0x0468:
        r13 = r15[r1];	 Catch:{ all -> 0x0568 }
        r13 = (com.google.android.gms.common.api.Status) r13;	 Catch:{ all -> 0x0568 }
        r14 = 140; // 0x8c float:1.96E-43 double:6.9E-322;
        r13.startResolutionForResult(r12, r14);	 Catch:{ all -> 0x0568 }
        goto L_0x0568;
    L_0x0473:
        r14 = org.telegram.messenger.NotificationCenter.fileDidLoad;
        if (r13 != r14) goto L_0x0530;
    L_0x0477:
        r13 = r12.loadingThemeFileName;
        if (r13 == 0) goto L_0x04fe;
    L_0x047b:
        r14 = r15[r1];
        r14 = (java.lang.String) r14;
        r13 = r13.equals(r14);
        if (r13 == 0) goto L_0x0568;
    L_0x0485:
        r12.loadingThemeFileName = r10;
        r13 = new java.io.File;
        r14 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r0 = "remote";
        r15.append(r0);
        r0 = r12.loadingTheme;
        r0 = r0.id;
        r15.append(r0);
        r0 = ".attheme";
        r15.append(r0);
        r15 = r15.toString();
        r13.<init>(r14, r15);
        r14 = r12.loadingTheme;
        r15 = r14.title;
        r14 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r13, r15, r14);
        if (r14 == 0) goto L_0x04f9;
    L_0x04b4:
        r15 = r14.pathToWallpaper;
        if (r15 == 0) goto L_0x04e2;
    L_0x04b8:
        r0 = new java.io.File;
        r0.<init>(r15);
        r15 = r0.exists();
        if (r15 != 0) goto L_0x04e2;
    L_0x04c3:
        r13 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
        r13.<init>();
        r15 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
        r15.<init>();
        r0 = r14.slug;
        r15.slug = r0;
        r13.wallpaper = r15;
        r15 = r14.account;
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Xb_UqNh9mLoUiPS9PtfFyDGzXWY;
        r0.<init>(r12, r14);
        r15.sendRequest(r13, r0);
        return;
    L_0x04e2:
        r14 = r12.loadingTheme;
        r15 = r14.title;
        r1 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r13, r15, r14, r11);
        if (r1 == 0) goto L_0x04f9;
    L_0x04ec:
        r13 = new org.telegram.ui.ThemePreviewActivity;
        r2 = 1;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r13;
        r0.<init>(r1, r2, r3, r4, r5);
        r12.lambda$runLinkRequest$32$LaunchActivity(r13);
    L_0x04f9:
        r12.onThemeLoadFinish();
        goto L_0x0568;
    L_0x04fe:
        r13 = r12.loadingThemeWallpaperName;
        if (r13 == 0) goto L_0x0568;
    L_0x0502:
        r14 = r15[r1];
        r14 = (java.lang.String) r14;
        r13 = r13.equals(r14);
        if (r13 == 0) goto L_0x0568;
    L_0x050c:
        r12.loadingThemeWallpaperName = r10;
        r13 = r15[r11];
        r13 = (java.io.File) r13;
        r14 = r12.loadingThemeAccent;
        if (r14 == 0) goto L_0x0523;
    L_0x0516:
        r13 = r12.loadingTheme;
        r14 = r12.loadingThemeWallpaper;
        r15 = r12.loadingThemeInfo;
        r12.openThemeAccentPreview(r13, r14, r15);
        r12.onThemeLoadFinish();
        goto L_0x0568;
    L_0x0523:
        r14 = r12.loadingThemeInfo;
        r15 = org.telegram.messenger.Utilities.globalQueue;
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Xd6X8oHbTQg4L6KRZ74pkknk70g;
        r0.<init>(r12, r14, r13);
        r15.postRunnable(r0);
        goto L_0x0568;
    L_0x0530:
        r14 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad;
        if (r13 != r14) goto L_0x054c;
    L_0x0534:
        r13 = r15[r1];
        r13 = (java.lang.String) r13;
        r14 = r12.loadingThemeFileName;
        r14 = r13.equals(r14);
        if (r14 != 0) goto L_0x0548;
    L_0x0540:
        r14 = r12.loadingThemeWallpaperName;
        r13 = r13.equals(r14);
        if (r13 == 0) goto L_0x0568;
    L_0x0548:
        r12.onThemeLoadFinish();
        goto L_0x0568;
    L_0x054c:
        r14 = org.telegram.messenger.NotificationCenter.screenStateChanged;
        if (r13 != r14) goto L_0x0561;
    L_0x0550:
        r13 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;
        if (r13 == 0) goto L_0x0555;
    L_0x0554:
        return;
    L_0x0555:
        r13 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r13 == 0) goto L_0x055d;
    L_0x0559:
        r12.onPasscodeResume();
        goto L_0x0568;
    L_0x055d:
        r12.onPasscodePause();
        goto L_0x0568;
    L_0x0561:
        r14 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors;
        if (r13 != r14) goto L_0x0568;
    L_0x0565:
        r12.checkSystemBarColors();
    L_0x0568:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$49(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", (BaseFragment) arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$50$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$52$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled((BaseFragment) arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new -$$Lambda$LaunchActivity$0qiVPxfoMMz32zSLwelNxxrNu3U(hashMap, i));
                lambda$runLinkRequest$32$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$51(HashMap hashMap, int i, MessageMedia messageMedia, int i2, boolean z, int i3) {
        for (Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(messageMedia, messageObject.getDialogId(), messageObject, null, null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$57$LaunchActivity(ThemeInfo themeInfo, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$O--LWJZxAGdrgkxV6rNZ2y3poHY(this, tLObject, themeInfo));
    }

    public /* synthetic */ void lambda$null$56$LaunchActivity(TLObject tLObject, ThemeInfo themeInfo) {
        if (tLObject instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
            this.loadingThemeWallpaper = tL_wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$59$LaunchActivity(ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$vpupveJhGHAPDPz0kMIWugUvsto(this));
    }

    public /* synthetic */ void lambda$null$58$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remote");
            stringBuilder.append(this.loadingTheme.id);
            stringBuilder.append(".attheme");
            File file = new File(filesDirFixed, stringBuilder.toString());
            TL_theme tL_theme = this.loadingTheme;
            ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$32$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
            }
            onThemeLoadFinish();
        }
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = (String) hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void openThemeAccentPreview(TL_theme tL_theme, TL_wallPaper tL_wallPaper, ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        ThemeAccent createNewAccent = themeInfo.createNewAccent(tL_theme, this.currentAccount);
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
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new -$$Lambda$LaunchActivity$Bas1US2NKgrAe5KdMLsQoX_7WRE(this), 2000);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$61$LaunchActivity() {
        String str = "last_space_check";
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (Math.abs(globalMainSettings.getLong(str, 0) - System.currentTimeMillis()) >= NUM) {
                    File directory = FileLoader.getDirectory(4);
                    if (directory != null) {
                        long abs;
                        StatFs statFs = new StatFs(directory.getAbsolutePath());
                        if (VERSION.SDK_INT < 18) {
                            abs = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                        } else {
                            abs = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
                        }
                        if (abs < NUM) {
                            globalMainSettings.edit().putLong(str, System.currentTimeMillis()).commit();
                            AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$_HkRsPfzB7I9IQoQIrfvWT-ZBK0(this));
                        }
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

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0056 A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0054 A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x005f A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x005c A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0065 A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0064 A:{Catch:{ Exception -> 0x0115 }} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d A:{Catch:{ Exception -> 0x0115 }} */
    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r17, org.telegram.messenger.LocaleController.LocaleInfo r18, java.lang.String r19) {
        /*
        r16 = this;
        r1 = r16;
        r0 = "ChooseYourLanguageOther";
        r2 = "ChooseYourLanguage";
        r3 = 0;
        r1.loadingLocaleDialog = r3;	 Catch:{ Exception -> 0x0115 }
        r4 = r17;
        r5 = r4.builtIn;	 Catch:{ Exception -> 0x0115 }
        r6 = 1;
        if (r5 != 0) goto L_0x001d;
    L_0x0010:
        r5 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0115 }
        r5 = r5.isCurrentLocalLocale();	 Catch:{ Exception -> 0x0115 }
        if (r5 == 0) goto L_0x001b;
    L_0x001a:
        goto L_0x001d;
    L_0x001b:
        r5 = 0;
        goto L_0x001e;
    L_0x001d:
        r5 = 1;
    L_0x001e:
        r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Exception -> 0x0115 }
        r7.<init>(r1);	 Catch:{ Exception -> 0x0115 }
        r8 = r1.systemLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r9 = NUM; // 0x7f0e02ee float:1.8876559E38 double:1.053162527E-314;
        r8 = r1.getStringForLanguageAlert(r8, r2, r9);	 Catch:{ Exception -> 0x0115 }
        r7.setTitle(r8);	 Catch:{ Exception -> 0x0115 }
        r8 = r1.englishLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r2 = r1.getStringForLanguageAlert(r8, r2, r9);	 Catch:{ Exception -> 0x0115 }
        r7.setSubtitle(r2);	 Catch:{ Exception -> 0x0115 }
        r2 = new android.widget.LinearLayout;	 Catch:{ Exception -> 0x0115 }
        r2.<init>(r1);	 Catch:{ Exception -> 0x0115 }
        r2.setOrientation(r6);	 Catch:{ Exception -> 0x0115 }
        r8 = 2;
        r9 = new org.telegram.ui.Cells.LanguageCell[r8];	 Catch:{ Exception -> 0x0115 }
        r10 = new org.telegram.messenger.LocaleController.LocaleInfo[r6];	 Catch:{ Exception -> 0x0115 }
        r11 = new org.telegram.messenger.LocaleController.LocaleInfo[r8];	 Catch:{ Exception -> 0x0115 }
        r12 = r1.systemLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r13 = "English";
        r14 = NUM; // 0x7f0e0433 float:1.8877218E38 double:1.0531626878E-314;
        r12 = r1.getStringForLanguageAlert(r12, r13, r14);	 Catch:{ Exception -> 0x0115 }
        if (r5 == 0) goto L_0x0056;
    L_0x0054:
        r13 = r4;
        goto L_0x0058;
    L_0x0056:
        r13 = r18;
    L_0x0058:
        r11[r3] = r13;	 Catch:{ Exception -> 0x0115 }
        if (r5 == 0) goto L_0x005f;
    L_0x005c:
        r13 = r18;
        goto L_0x0060;
    L_0x005f:
        r13 = r4;
    L_0x0060:
        r11[r6] = r13;	 Catch:{ Exception -> 0x0115 }
        if (r5 == 0) goto L_0x0065;
    L_0x0064:
        goto L_0x0067;
    L_0x0065:
        r4 = r18;
    L_0x0067:
        r10[r3] = r4;	 Catch:{ Exception -> 0x0115 }
        r4 = 0;
    L_0x006a:
        r13 = -1;
        if (r4 >= r8) goto L_0x00bf;
    L_0x006d:
        r14 = new org.telegram.ui.Cells.LanguageCell;	 Catch:{ Exception -> 0x0115 }
        r14.<init>(r1, r6);	 Catch:{ Exception -> 0x0115 }
        r9[r4] = r14;	 Catch:{ Exception -> 0x0115 }
        r14 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        r15 = r11[r4];	 Catch:{ Exception -> 0x0115 }
        r3 = r11[r4];	 Catch:{ Exception -> 0x0115 }
        r5 = r18;
        if (r3 != r5) goto L_0x0080;
    L_0x007e:
        r3 = r12;
        goto L_0x0081;
    L_0x0080:
        r3 = 0;
    L_0x0081:
        r14.setLanguage(r15, r3, r6);	 Catch:{ Exception -> 0x0115 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        r14 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0115 }
        r3.setTag(r14);	 Catch:{ Exception -> 0x0115 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        r14 = "dialogButtonSelector";
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);	 Catch:{ Exception -> 0x0115 }
        r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r8);	 Catch:{ Exception -> 0x0115 }
        r3.setBackgroundDrawable(r14);	 Catch:{ Exception -> 0x0115 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        if (r4 != 0) goto L_0x00a2;
    L_0x00a0:
        r14 = 1;
        goto L_0x00a3;
    L_0x00a2:
        r14 = 0;
    L_0x00a3:
        r3.setLanguageSelected(r14);	 Catch:{ Exception -> 0x0115 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        r14 = 50;
        r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r14);	 Catch:{ Exception -> 0x0115 }
        r2.addView(r3, r13);	 Catch:{ Exception -> 0x0115 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x0115 }
        r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$8tQj5cq4HGp8JiT0ZRfT2mD1dDw;	 Catch:{ Exception -> 0x0115 }
        r13.<init>(r10, r9);	 Catch:{ Exception -> 0x0115 }
        r3.setOnClickListener(r13);	 Catch:{ Exception -> 0x0115 }
        r4 = r4 + 1;
        r3 = 0;
        goto L_0x006a;
    L_0x00bf:
        r3 = new org.telegram.ui.Cells.LanguageCell;	 Catch:{ Exception -> 0x0115 }
        r3.<init>(r1, r6);	 Catch:{ Exception -> 0x0115 }
        r4 = r1.systemLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r5 = NUM; // 0x7f0e02ef float:1.887656E38 double:1.0531625277E-314;
        r4 = r1.getStringForLanguageAlert(r4, r0, r5);	 Catch:{ Exception -> 0x0115 }
        r6 = r1.englishLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r0 = r1.getStringForLanguageAlert(r6, r0, r5);	 Catch:{ Exception -> 0x0115 }
        r3.setValue(r4, r0);	 Catch:{ Exception -> 0x0115 }
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$Ro8pzBJk5O5wAEkcazwM88tPiOo;	 Catch:{ Exception -> 0x0115 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0115 }
        r3.setOnClickListener(r0);	 Catch:{ Exception -> 0x0115 }
        r0 = 50;
        r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0);	 Catch:{ Exception -> 0x0115 }
        r2.addView(r3, r0);	 Catch:{ Exception -> 0x0115 }
        r7.setView(r2);	 Catch:{ Exception -> 0x0115 }
        r0 = "OK";
        r2 = NUM; // 0x7f0e0764 float:1.8878875E38 double:1.0531630914E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x0115 }
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$9Ej_ZHZEFtsi6RMBIKBFESTAxoI;	 Catch:{ Exception -> 0x0115 }
        r2.<init>(r1, r10);	 Catch:{ Exception -> 0x0115 }
        r7.setNegativeButton(r0, r2);	 Catch:{ Exception -> 0x0115 }
        r0 = r1.showAlertDialog(r7);	 Catch:{ Exception -> 0x0115 }
        r1.localeDialog = r0;	 Catch:{ Exception -> 0x0115 }
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x0115 }
        r0 = r0.edit();	 Catch:{ Exception -> 0x0115 }
        r2 = "language_showed2";
        r3 = r19;
        r0 = r0.putString(r2, r3);	 Catch:{ Exception -> 0x0115 }
        r0.commit();	 Catch:{ Exception -> 0x0115 }
        goto L_0x0119;
    L_0x0115:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0119:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    static /* synthetic */ void lambda$showLanguageAlertInternal$62(LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
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

    public /* synthetic */ void lambda$showLanguageAlertInternal$64$LaunchActivity(LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    private void showLanguageAlert(boolean z) {
        String str = "ChangeLanguageLater";
        String str2 = "ChooseYourLanguageOther";
        String str3 = "ChooseYourLanguage";
        String str4 = "English";
        String str5 = "-";
        try {
            if (!this.loadingLocaleDialog) {
                if (!ApplicationLoader.mainInterfacePaused) {
                    String string = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
                    String str6 = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                    if (z || !string.equals(str6)) {
                        LocaleInfo[] localeInfoArr = new LocaleInfo[2];
                        Object obj = str6.contains(str5) ? str6.split(str5)[0] : str6;
                        Object obj2 = "in".equals(obj) ? "id" : "iw".equals(obj) ? "he" : "jw".equals(obj) ? "jv" : null;
                        for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
                            LocaleInfo localeInfo = (LocaleInfo) LocaleController.getInstance().languages.get(i);
                            if (localeInfo.shortName.equals("en")) {
                                localeInfoArr[0] = localeInfo;
                            }
                            if (localeInfo.shortName.replace("_", str5).equals(str6) || localeInfo.shortName.equals(obj) || localeInfo.shortName.equals(obj2)) {
                                localeInfoArr[1] = localeInfo;
                            }
                            if (localeInfoArr[0] != null && localeInfoArr[1] != null) {
                                break;
                            }
                        }
                        if (!(localeInfoArr[0] == null || localeInfoArr[1] == null)) {
                            if (localeInfoArr[0] != localeInfoArr[1]) {
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("show lang alert for ");
                                    stringBuilder.append(localeInfoArr[0].getKey());
                                    stringBuilder.append(" and ");
                                    stringBuilder.append(localeInfoArr[1].getKey());
                                    FileLog.d(stringBuilder.toString());
                                }
                                this.systemLocaleStrings = null;
                                this.englishLocaleStrings = null;
                                this.loadingLocaleDialog = true;
                                TL_langpack_getStrings tL_langpack_getStrings = new TL_langpack_getStrings();
                                tL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                                tL_langpack_getStrings.keys.add(str4);
                                tL_langpack_getStrings.keys.add(str3);
                                tL_langpack_getStrings.keys.add(str2);
                                tL_langpack_getStrings.keys.add(str);
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new -$$Lambda$LaunchActivity$ZcTu7dnCfEiYpxIUd95V9xDvlNU(this, localeInfoArr, str6), 8);
                                tL_langpack_getStrings = new TL_langpack_getStrings();
                                tL_langpack_getStrings.lang_code = localeInfoArr[0].getLangCode();
                                tL_langpack_getStrings.keys.add(str4);
                                tL_langpack_getStrings.keys.add(str3);
                                tL_langpack_getStrings.keys.add(str2);
                                tL_langpack_getStrings.keys.add(str);
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new -$$Lambda$LaunchActivity$-v8dDb0H9ZZO7qxZ_tG1x4C9QhM(this, localeInfoArr, str6), 8);
                            }
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("alert already showed for ");
                            stringBuilder2.append(string);
                            FileLog.d(stringBuilder2.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$66$LaunchActivity(LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                LangPackString langPackString = (LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$o0Cvzhv8f8YmqahyZmloCfIHyas(this, hashMap, localeInfoArr, str));
    }

    public /* synthetic */ void lambda$null$65$LaunchActivity(HashMap hashMap, LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$68$LaunchActivity(LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                LangPackString langPackString = (LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$cORlOoTiUrvBlxjXHVhyhW45cIE(this, hashMap, localeInfoArr, str));
    }

    public /* synthetic */ void lambda$null$67$LaunchActivity(HashMap hashMap, LocaleInfo[] localeInfoArr, String str) {
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
                        LaunchActivity.this.lockRunnable = null;
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
        if (this.actionBarLayout != null) {
            String str;
            i = 0;
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            int i2 = this.currentConnectionState;
            Runnable runnable = null;
            if (i2 == 2) {
                i = NUM;
                str = "WaitingForNetwork";
            } else if (i2 == 5) {
                i = NUM;
                str = "Updating";
            } else if (i2 == 4) {
                i = NUM;
                str = "ConnectingToProxy";
            } else if (i2 == 1) {
                i = NUM;
                str = "Connecting";
            } else {
                str = null;
            }
            int i3 = this.currentConnectionState;
            if (i3 == 1 || i3 == 4) {
                runnable = new -$$Lambda$LaunchActivity$-P2BvsvtXjb2l1rrge-69H2_Nlg(this);
            }
            this.actionBarLayout.setTitleOverlayText(str, i, runnable);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0039  */
    public /* synthetic */ void lambda$updateCurrentConnectionState$69$LaunchActivity() {
        /*
        r2 = this;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x001d;
    L_0x0006:
        r0 = layerFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0034;
    L_0x000e:
        r0 = layerFragmentsStack;
        r1 = r0.size();
        r1 = r1 + -1;
        r0 = r0.get(r1);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        goto L_0x0035;
    L_0x001d:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0034;
    L_0x0025:
        r0 = mainFragmentsStack;
        r1 = r0.size();
        r1 = r1 + -1;
        r0 = r0.get(r1);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        goto L_0x0035;
    L_0x0034:
        r0 = 0;
    L_0x0035:
        r1 = r0 instanceof org.telegram.ui.ProxyListActivity;
        if (r1 != 0) goto L_0x0046;
    L_0x0039:
        r0 = r0 instanceof org.telegram.ui.ProxySettingsActivity;
        if (r0 == 0) goto L_0x003e;
    L_0x003d:
        goto L_0x0046;
    L_0x003e:
        r0 = new org.telegram.ui.ProxyListActivity;
        r0.<init>();
        r2.lambda$runLinkRequest$32$LaunchActivity(r0);
    L_0x0046:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$69$LaunchActivity():void");
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle bundle) {
        try {
            super.onSaveInstanceState(bundle);
            BaseFragment baseFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (baseFragment != null) {
                Bundle arguments = baseFragment.getArguments();
                String str = "args";
                String str2 = "fragment";
                if ((baseFragment instanceof ChatActivity) && arguments != null) {
                    bundle.putBundle(str, arguments);
                    bundle.putString(str2, "chat");
                } else if (baseFragment instanceof SettingsActivity) {
                    bundle.putString(str2, "settings");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle(str, arguments);
                    bundle.putString(str2, "group");
                } else if (baseFragment instanceof WallpapersListActivity) {
                    bundle.putString(str2, "wallpapers");
                } else if ((baseFragment instanceof ProfileActivity) && ((ProfileActivity) baseFragment).isChat() && arguments != null) {
                    bundle.putBundle(str, arguments);
                    bundle.putString(str2, "chat_profile");
                } else if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                    bundle.putBundle(str, arguments);
                    bundle.putString(str2, "channel");
                }
                baseFragment.saveSelfArgs(bundle);
            }
        } catch (Exception e) {
            FileLog.e(e);
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
                ArrayList arrayList = this.rightActionBarLayout.fragmentsStack;
                z = ((BaseFragment) arrayList.get(arrayList.size() - 1)).onBackPressed() ^ 1;
            }
            if (!z) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
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
            if (!(menu == null || this.actionBarLayout.extendActionMode(menu) || !AndroidUtilities.isTablet() || this.rightActionBarLayout.extendActionMode(menu))) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
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
        if (VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
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
        if (!mainFragmentsStack.isEmpty() && (!(PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25))) {
            ArrayList arrayList = mainFragmentsStack;
            BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
            if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
                return true;
            }
            if (AndroidUtilities.isTablet() && !rightFragmentsStack.isEmpty()) {
                arrayList = rightFragmentsStack;
                baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
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
            } else if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
            } else {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00ba A:{LOOP_START, LOOP:0: B:49:0x00ba->B:51:0x00c5} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00dc  */
    public boolean needPresentFragment(org.telegram.ui.ActionBar.BaseFragment r12, boolean r13, boolean r14, org.telegram.ui.ActionBar.ActionBarLayout r15) {
        /*
        r11 = this;
        r0 = org.telegram.ui.ArticleViewer.hasInstance();
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x0019;
    L_0x0008:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x0019;
    L_0x0012:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0.close(r2, r1);
    L_0x0019:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x01e6;
    L_0x001f:
        r0 = r11.drawerLayoutContainer;
        r3 = r12 instanceof org.telegram.ui.LoginActivity;
        if (r3 != 0) goto L_0x0033;
    L_0x0025:
        r4 = r12 instanceof org.telegram.ui.CountrySelectActivity;
        if (r4 != 0) goto L_0x0033;
    L_0x0029:
        r4 = r11.layersActionBarLayout;
        r4 = r4.getVisibility();
        if (r4 == 0) goto L_0x0033;
    L_0x0031:
        r4 = 1;
        goto L_0x0034;
    L_0x0033:
        r4 = 0;
    L_0x0034:
        r0.setAllowOpenDrawer(r4, r1);
        r0 = r12 instanceof org.telegram.ui.DialogsActivity;
        r4 = 8;
        if (r0 == 0) goto L_0x007f;
    L_0x003d:
        r0 = r12;
        r0 = (org.telegram.ui.DialogsActivity) r0;
        r0 = r0.isMainDialogList();
        if (r0 == 0) goto L_0x007f;
    L_0x0046:
        r0 = r11.actionBarLayout;
        if (r15 == r0) goto L_0x007f;
    L_0x004a:
        r0.removeAllFragments();
        r5 = r11.actionBarLayout;
        r9 = 0;
        r10 = 0;
        r6 = r12;
        r7 = r13;
        r8 = r14;
        r5.presentFragment(r6, r7, r8, r9, r10);
        r12 = r11.layersActionBarLayout;
        r12.removeAllFragments();
        r12 = r11.layersActionBarLayout;
        r12.setVisibility(r4);
        r12 = r11.drawerLayoutContainer;
        r12.setAllowOpenDrawer(r1, r2);
        r12 = r11.tabletFullSize;
        if (r12 != 0) goto L_0x007e;
    L_0x006a:
        r12 = r11.shadowTabletSide;
        r12.setVisibility(r2);
        r12 = r11.rightActionBarLayout;
        r12 = r12.fragmentsStack;
        r12 = r12.isEmpty();
        if (r12 == 0) goto L_0x007e;
    L_0x0079:
        r12 = r11.backgroundTablet;
        r12.setVisibility(r2);
    L_0x007e:
        return r2;
    L_0x007f:
        r0 = r12 instanceof org.telegram.ui.ChatActivity;
        if (r0 == 0) goto L_0x01b5;
    L_0x0083:
        r0 = r12;
        r0 = (org.telegram.ui.ChatActivity) r0;
        r0 = r0.isInScheduleMode();
        if (r0 != 0) goto L_0x01b5;
    L_0x008c:
        r0 = r11.tabletFullSize;
        if (r0 != 0) goto L_0x0094;
    L_0x0090:
        r0 = r11.rightActionBarLayout;
        if (r15 == r0) goto L_0x009c;
    L_0x0094:
        r0 = r11.tabletFullSize;
        if (r0 == 0) goto L_0x00e7;
    L_0x0098:
        r0 = r11.actionBarLayout;
        if (r15 != r0) goto L_0x00e7;
    L_0x009c:
        r13 = r11.tabletFullSize;
        if (r13 == 0) goto L_0x00af;
    L_0x00a0:
        r13 = r11.actionBarLayout;
        if (r15 != r13) goto L_0x00af;
    L_0x00a4:
        r13 = r13.fragmentsStack;
        r13 = r13.size();
        if (r13 == r1) goto L_0x00ad;
    L_0x00ac:
        goto L_0x00af;
    L_0x00ad:
        r13 = 0;
        goto L_0x00b0;
    L_0x00af:
        r13 = 1;
    L_0x00b0:
        r15 = r11.layersActionBarLayout;
        r15 = r15.fragmentsStack;
        r15 = r15.isEmpty();
        if (r15 != 0) goto L_0x00da;
    L_0x00ba:
        r15 = r11.layersActionBarLayout;
        r15 = r15.fragmentsStack;
        r15 = r15.size();
        r15 = r15 - r1;
        if (r15 <= 0) goto L_0x00d3;
    L_0x00c5:
        r15 = r11.layersActionBarLayout;
        r0 = r15.fragmentsStack;
        r0 = r0.get(r2);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r15.removeFragmentFromStack(r0);
        goto L_0x00ba;
    L_0x00d3:
        r15 = r11.layersActionBarLayout;
        r0 = r14 ^ 1;
        r15.closeLastFragment(r0);
    L_0x00da:
        if (r13 != 0) goto L_0x00e6;
    L_0x00dc:
        r1 = r11.actionBarLayout;
        r3 = 0;
        r5 = 0;
        r6 = 0;
        r2 = r12;
        r4 = r14;
        r1.presentFragment(r2, r3, r4, r5, r6);
    L_0x00e6:
        return r13;
    L_0x00e7:
        r0 = r11.tabletFullSize;
        if (r0 != 0) goto L_0x0131;
    L_0x00eb:
        r0 = r11.rightActionBarLayout;
        if (r15 == r0) goto L_0x0131;
    L_0x00ef:
        r0.setVisibility(r2);
        r15 = r11.backgroundTablet;
        r15.setVisibility(r4);
        r15 = r11.rightActionBarLayout;
        r15.removeAllFragments();
        r3 = r11.rightActionBarLayout;
        r6 = 1;
        r7 = 0;
        r8 = 0;
        r4 = r12;
        r5 = r13;
        r3.presentFragment(r4, r5, r6, r7, r8);
        r12 = r11.layersActionBarLayout;
        r12 = r12.fragmentsStack;
        r12 = r12.isEmpty();
        if (r12 != 0) goto L_0x0130;
    L_0x0110:
        r12 = r11.layersActionBarLayout;
        r12 = r12.fragmentsStack;
        r12 = r12.size();
        r12 = r12 - r1;
        if (r12 <= 0) goto L_0x0129;
    L_0x011b:
        r12 = r11.layersActionBarLayout;
        r13 = r12.fragmentsStack;
        r13 = r13.get(r2);
        r13 = (org.telegram.ui.ActionBar.BaseFragment) r13;
        r12.removeFragmentFromStack(r13);
        goto L_0x0110;
    L_0x0129:
        r12 = r11.layersActionBarLayout;
        r13 = r14 ^ 1;
        r12.closeLastFragment(r13);
    L_0x0130:
        return r2;
    L_0x0131:
        r13 = r11.tabletFullSize;
        if (r13 == 0) goto L_0x0176;
    L_0x0135:
        r3 = r11.actionBarLayout;
        if (r15 == r3) goto L_0x0176;
    L_0x0139:
        r13 = r3.fragmentsStack;
        r13 = r13.size();
        if (r13 <= r1) goto L_0x0143;
    L_0x0141:
        r5 = 1;
        goto L_0x0144;
    L_0x0143:
        r5 = 0;
    L_0x0144:
        r7 = 0;
        r8 = 0;
        r4 = r12;
        r6 = r14;
        r3.presentFragment(r4, r5, r6, r7, r8);
        r12 = r11.layersActionBarLayout;
        r12 = r12.fragmentsStack;
        r12 = r12.isEmpty();
        if (r12 != 0) goto L_0x0175;
    L_0x0155:
        r12 = r11.layersActionBarLayout;
        r12 = r12.fragmentsStack;
        r12 = r12.size();
        r12 = r12 - r1;
        if (r12 <= 0) goto L_0x016e;
    L_0x0160:
        r12 = r11.layersActionBarLayout;
        r13 = r12.fragmentsStack;
        r13 = r13.get(r2);
        r13 = (org.telegram.ui.ActionBar.BaseFragment) r13;
        r12.removeFragmentFromStack(r13);
        goto L_0x0155;
    L_0x016e:
        r12 = r11.layersActionBarLayout;
        r13 = r14 ^ 1;
        r12.closeLastFragment(r13);
    L_0x0175:
        return r2;
    L_0x0176:
        r13 = r11.layersActionBarLayout;
        r13 = r13.fragmentsStack;
        r13 = r13.isEmpty();
        if (r13 != 0) goto L_0x01a0;
    L_0x0180:
        r13 = r11.layersActionBarLayout;
        r13 = r13.fragmentsStack;
        r13 = r13.size();
        r13 = r13 - r1;
        if (r13 <= 0) goto L_0x0199;
    L_0x018b:
        r13 = r11.layersActionBarLayout;
        r15 = r13.fragmentsStack;
        r15 = r15.get(r2);
        r15 = (org.telegram.ui.ActionBar.BaseFragment) r15;
        r13.removeFragmentFromStack(r15);
        goto L_0x0180;
    L_0x0199:
        r13 = r11.layersActionBarLayout;
        r15 = r14 ^ 1;
        r13.closeLastFragment(r15);
    L_0x01a0:
        r3 = r11.actionBarLayout;
        r13 = r3.fragmentsStack;
        r13 = r13.size();
        if (r13 <= r1) goto L_0x01ac;
    L_0x01aa:
        r5 = 1;
        goto L_0x01ad;
    L_0x01ac:
        r5 = 0;
    L_0x01ad:
        r7 = 0;
        r8 = 0;
        r4 = r12;
        r6 = r14;
        r3.presentFragment(r4, r5, r6, r7, r8);
        return r2;
    L_0x01b5:
        r0 = r11.layersActionBarLayout;
        if (r15 == r0) goto L_0x01e5;
    L_0x01b9:
        r0.setVisibility(r2);
        r15 = r11.drawerLayoutContainer;
        r15.setAllowOpenDrawer(r2, r1);
        if (r3 == 0) goto L_0x01d3;
    L_0x01c3:
        r15 = r11.backgroundTablet;
        r15.setVisibility(r2);
        r15 = r11.shadowTabletSide;
        r15.setVisibility(r4);
        r15 = r11.shadowTablet;
        r15.setBackgroundColor(r2);
        goto L_0x01da;
    L_0x01d3:
        r15 = r11.shadowTablet;
        r0 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r15.setBackgroundColor(r0);
    L_0x01da:
        r3 = r11.layersActionBarLayout;
        r7 = 0;
        r8 = 0;
        r4 = r12;
        r5 = r13;
        r6 = r14;
        r3.presentFragment(r4, r5, r6, r7, r8);
        return r2;
    L_0x01e5:
        return r1;
    L_0x01e6:
        r13 = r12 instanceof org.telegram.ui.LoginActivity;
        if (r13 == 0) goto L_0x01f4;
    L_0x01ea:
        r12 = mainFragmentsStack;
        r12 = r12.size();
        if (r12 != 0) goto L_0x0201;
    L_0x01f2:
        r12 = 0;
        goto L_0x0202;
    L_0x01f4:
        r12 = r12 instanceof org.telegram.ui.CountrySelectActivity;
        if (r12 == 0) goto L_0x0201;
    L_0x01f8:
        r12 = mainFragmentsStack;
        r12 = r12.size();
        if (r12 != r1) goto L_0x0201;
    L_0x0200:
        goto L_0x01f2;
    L_0x0201:
        r12 = 1;
    L_0x0202:
        r13 = r11.drawerLayoutContainer;
        r13.setAllowOpenDrawer(r12, r2);
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needPresentFragment(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, org.telegram.ui.ActionBar.ActionBarLayout):boolean");
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = baseFragment instanceof LoginActivity;
            boolean z2 = (z || (baseFragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z2, true);
            ActionBarLayout actionBarLayout2;
            if (baseFragment instanceof DialogsActivity) {
                if (((DialogsActivity) baseFragment).isMainDialogList()) {
                    actionBarLayout2 = this.actionBarLayout;
                    if (actionBarLayout != actionBarLayout2) {
                        actionBarLayout2.removeAllFragments();
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
                }
            } else if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).isInScheduleMode()) {
                actionBarLayout2 = this.layersActionBarLayout;
                if (actionBarLayout != actionBarLayout2) {
                    actionBarLayout2.setVisibility(0);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                    if (z) {
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
                ActionBarLayout actionBarLayout3;
                if (!this.tabletFullSize) {
                    actionBarLayout2 = this.rightActionBarLayout;
                    if (actionBarLayout != actionBarLayout2) {
                        actionBarLayout2.setVisibility(0);
                        this.backgroundTablet.setVisibility(8);
                        this.rightActionBarLayout.removeAllFragments();
                        this.rightActionBarLayout.addFragmentToStack(baseFragment);
                        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                            while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                                actionBarLayout3 = this.layersActionBarLayout;
                                actionBarLayout3.removeFragmentFromStack((BaseFragment) actionBarLayout3.fragmentsStack.get(0));
                            }
                            this.layersActionBarLayout.closeLastFragment(true);
                        }
                        return false;
                    }
                }
                if (this.tabletFullSize) {
                    actionBarLayout2 = this.actionBarLayout;
                    if (actionBarLayout != actionBarLayout2) {
                        actionBarLayout2.addFragmentToStack(baseFragment);
                        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                            while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                                actionBarLayout3 = this.layersActionBarLayout;
                                actionBarLayout3.removeFragmentFromStack((BaseFragment) actionBarLayout3.fragmentsStack.get(0));
                            }
                            this.layersActionBarLayout.closeLastFragment(true);
                        }
                        return false;
                    }
                }
            }
            return true;
        }
        boolean z3;
        if ((baseFragment instanceof LoginActivity) ? mainFragmentsStack.size() != 0 : !((baseFragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1)) {
            z3 = true;
        } else {
            z3 = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(z3, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (actionBarLayout.fragmentsStack.size() >= 2 && !(actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean z) {
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.rebuildAllFragmentViews(z, z);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z) {
        if (AndroidUtilities.isTablet() && actionBarLayout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(z, z);
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }
}
