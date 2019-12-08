package org.telegram.ui;

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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
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
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import java.io.File;
import java.io.FileOutputStream;
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
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
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
import org.telegram.tgnet.TLRPC.TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
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
    private String loadingThemeFileName;
    private ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
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
    private String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009d  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x0109 */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:40|41|42|43|44) */
    /* JADX WARNING: Missing block: B:108:0x0442, code skipped:
            r0 = -1;
     */
    /* JADX WARNING: Missing block: B:109:0x0443, code skipped:
            if (r0 == 0) goto L_0x04ac;
     */
    /* JADX WARNING: Missing block: B:110:0x0445, code skipped:
            if (r0 == 1) goto L_0x049d;
     */
    /* JADX WARNING: Missing block: B:111:0x0447, code skipped:
            if (r0 == 2) goto L_0x0489;
     */
    /* JADX WARNING: Missing block: B:112:0x0449, code skipped:
            if (r0 == 3) goto L_0x0475;
     */
    /* JADX WARNING: Missing block: B:113:0x044b, code skipped:
            if (r0 == 4) goto L_0x0461;
     */
    /* JADX WARNING: Missing block: B:115:0x044e, code skipped:
            if (r0 == 5) goto L_0x0452;
     */
    /* JADX WARNING: Missing block: B:117:0x0452, code skipped:
            r0 = new org.telegram.ui.WallpapersListActivity(0);
            r11.actionBarLayout.addFragmentToStack(r0);
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:118:0x0461, code skipped:
            if (r3 == null) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:119:0x0463, code skipped:
            r0 = new org.telegram.ui.ProfileActivity(r3);
     */
    /* JADX WARNING: Missing block: B:120:0x046e, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:121:0x0470, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:122:0x0475, code skipped:
            if (r3 == null) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:123:0x0477, code skipped:
            r0 = new org.telegram.ui.ChannelCreateActivity(r3);
     */
    /* JADX WARNING: Missing block: B:124:0x0482, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:125:0x0484, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:126:0x0489, code skipped:
            if (r3 == null) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:127:0x048b, code skipped:
            r0 = new org.telegram.ui.GroupCreateFinalActivity(r3);
     */
    /* JADX WARNING: Missing block: B:128:0x0496, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:129:0x0498, code skipped:
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:130:0x049d, code skipped:
            r0 = new org.telegram.ui.SettingsActivity();
            r11.actionBarLayout.addFragmentToStack(r0);
            r0.restoreSelfArgs(r12);
     */
    /* JADX WARNING: Missing block: B:131:0x04ac, code skipped:
            if (r3 == null) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:132:0x04ae, code skipped:
            r0 = new org.telegram.ui.ChatActivity(r3);
     */
    /* JADX WARNING: Missing block: B:133:0x04b9, code skipped:
            if (r11.actionBarLayout.addFragmentToStack(r0) == false) goto L_0x052b;
     */
    /* JADX WARNING: Missing block: B:134:0x04bb, code skipped:
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
        if (r0 != 0) goto L_0x00e8;
    L_0x0020:
        r0 = r11.getIntent();
        if (r0 == 0) goto L_0x0088;
    L_0x0026:
        r3 = r0.getAction();
        if (r3 == 0) goto L_0x0088;
    L_0x002c:
        r3 = r0.getAction();
        r4 = "android.intent.action.SEND";
        r3 = r4.equals(r3);
        if (r3 != 0) goto L_0x0081;
    L_0x0038:
        r3 = r0.getAction();
        r4 = "android.intent.action.SEND_MULTIPLE";
        r3 = r4.equals(r3);
        if (r3 == 0) goto L_0x0045;
    L_0x0044:
        goto L_0x0081;
    L_0x0045:
        r3 = r0.getAction();
        r4 = "android.intent.action.VIEW";
        r3 = r4.equals(r3);
        if (r3 == 0) goto L_0x0088;
    L_0x0051:
        r3 = r0.getData();
        if (r3 == 0) goto L_0x0088;
    L_0x0057:
        r3 = r3.toString();
        r3 = r3.toLowerCase();
        r4 = "tg:proxy";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x007f;
    L_0x0067:
        r4 = "tg://proxy";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x007f;
    L_0x006f:
        r4 = "tg:socks";
        r4 = r3.startsWith(r4);
        if (r4 != 0) goto L_0x007f;
    L_0x0077:
        r4 = "tg://socks";
        r3 = r3.startsWith(r4);
        if (r3 == 0) goto L_0x0088;
    L_0x007f:
        r3 = 1;
        goto L_0x0089;
    L_0x0081:
        super.onCreate(r12);
        r11.finish();
        return;
    L_0x0088:
        r3 = 0;
    L_0x0089:
        r4 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r5 = 0;
        r7 = "intro_crashed_time";
        r8 = r4.getLong(r7, r5);
        r10 = "fromIntro";
        r10 = r0.getBooleanExtra(r10, r2);
        if (r10 == 0) goto L_0x00a8;
    L_0x009d:
        r4 = r4.edit();
        r4 = r4.putLong(r7, r5);
        r4.commit();
    L_0x00a8:
        if (r3 != 0) goto L_0x00e8;
    L_0x00aa:
        r3 = java.lang.System.currentTimeMillis();
        r8 = r8 - r3;
        r3 = java.lang.Math.abs(r8);
        r5 = 120000; // 0x1d4c0 float:1.68156E-40 double:5.9288E-319;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 < 0) goto L_0x00e8;
    L_0x00ba:
        if (r0 == 0) goto L_0x00e8;
    L_0x00bc:
        if (r10 != 0) goto L_0x00e8;
    L_0x00be:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = "logininfo2";
        r3 = r3.getSharedPreferences(r4, r2);
        r3 = r3.getAll();
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x00e8;
    L_0x00d0:
        r1 = new android.content.Intent;
        r2 = org.telegram.ui.IntroActivity.class;
        r1.<init>(r11, r2);
        r0 = r0.getData();
        r1.setData(r0);
        r11.startActivity(r1);
        super.onCreate(r12);
        r11.finish();
        return;
    L_0x00e8:
        r11.requestWindowFeature(r1);
        r0 = NUM; // 0x7f0var_f float:1.900799E38 double:1.053194543E-314;
        r11.setTheme(r0);
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        r4 = 0;
        if (r0 < r3) goto L_0x0112;
    L_0x00f8:
        r0 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r3 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x0109 }
        r5 = "actionBarDefault";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);	 Catch:{ Exception -> 0x0109 }
        r5 = r5 | r0;
        r3.<init>(r4, r4, r5);	 Catch:{ Exception -> 0x0109 }
        r11.setTaskDescription(r3);	 Catch:{ Exception -> 0x0109 }
    L_0x0109:
        r3 = r11.getWindow();	 Catch:{ Exception -> 0x0111 }
        r3.setNavigationBarColor(r0);	 Catch:{ Exception -> 0x0111 }
        goto L_0x0112;
    L_0x0112:
        r0 = r11.getWindow();
        r3 = NUM; // 0x7var_ce float:1.7946034E38 double:1.052935858E-314;
        r0.setBackgroundDrawableResource(r3);
        r0 = org.telegram.messenger.SharedConfig.passcodeHash;
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0136;
    L_0x0124:
        r0 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r0 != 0) goto L_0x0136;
    L_0x0128:
        r0 = r11.getWindow();	 Catch:{ Exception -> 0x0132 }
        r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r0.setFlags(r3, r3);	 Catch:{ Exception -> 0x0132 }
        goto L_0x0136;
    L_0x0132:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0136:
        super.onCreate(r12);
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 24;
        if (r0 < r3) goto L_0x0145;
    L_0x013f:
        r0 = r11.isInMultiWindowMode();
        org.telegram.messenger.AndroidUtilities.isInMultiwindow = r0;
    L_0x0145:
        org.telegram.ui.ActionBar.Theme.createChatResources(r11, r2);
        r0 = org.telegram.messenger.SharedConfig.passcodeHash;
        r0 = r0.length();
        if (r0 == 0) goto L_0x015e;
    L_0x0150:
        r0 = org.telegram.messenger.SharedConfig.appLocked;
        if (r0 == 0) goto L_0x015e;
    L_0x0154:
        r5 = android.os.SystemClock.uptimeMillis();
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r7;
        r0 = (int) r5;
        org.telegram.messenger.SharedConfig.lastPauseTime = r0;
    L_0x015e:
        org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r11);
        r0 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0.<init>(r11);
        r11.actionBarLayout = r0;
        r0 = new org.telegram.ui.ActionBar.DrawerLayoutContainer;
        r0.<init>(r11);
        r11.drawerLayoutContainer = r0;
        r0 = r11.drawerLayoutContainer;
        r3 = "windowBackgroundWhite";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.setBehindKeyboardColor(r3);
        r0 = r11.drawerLayoutContainer;
        r3 = new android.view.ViewGroup$LayoutParams;
        r5 = -1;
        r3.<init>(r5, r5);
        r11.setContentView(r0, r3);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        if (r0 == 0) goto L_0x027b;
    L_0x018d:
        r0 = r11.getWindow();
        r6 = 16;
        r0.setSoftInputMode(r6);
        r0 = new org.telegram.ui.LaunchActivity$1;
        r0.<init>(r11);
        r6 = r11.drawerLayoutContainer;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3);
        r6.addView(r0, r7);
        r6 = new android.view.View;
        r6.<init>(r11);
        r11.backgroundTablet = r6;
        r6 = r11.getResources();
        r7 = NUM; // 0x7var_ float:1.7944838E38 double:1.0529355663E-314;
        r6 = r6.getDrawable(r7);
        r6 = (android.graphics.drawable.BitmapDrawable) r6;
        r7 = android.graphics.Shader.TileMode.REPEAT;
        r6.setTileModeXY(r7, r7);
        r7 = r11.backgroundTablet;
        r7.setBackgroundDrawable(r6);
        r6 = r11.backgroundTablet;
        r7 = org.telegram.ui.Components.LayoutHelper.createRelative(r5, r5);
        r0.addView(r6, r7);
        r6 = r11.actionBarLayout;
        r0.addView(r6);
        r6 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r6.<init>(r11);
        r11.rightActionBarLayout = r6;
        r6 = r11.rightActionBarLayout;
        r7 = rightFragmentsStack;
        r6.init(r7);
        r6 = r11.rightActionBarLayout;
        r6.setDelegate(r11);
        r6 = r11.rightActionBarLayout;
        r0.addView(r6);
        r6 = new android.widget.FrameLayout;
        r6.<init>(r11);
        r11.shadowTabletSide = r6;
        r6 = r11.shadowTabletSide;
        r7 = NUM; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r6.setBackgroundColor(r7);
        r6 = r11.shadowTabletSide;
        r0.addView(r6);
        r6 = new android.widget.FrameLayout;
        r6.<init>(r11);
        r11.shadowTablet = r6;
        r6 = r11.shadowTablet;
        r7 = layerFragmentsStack;
        r7 = r7.isEmpty();
        r8 = 8;
        if (r7 == 0) goto L_0x0212;
    L_0x020f:
        r7 = 8;
        goto L_0x0213;
    L_0x0212:
        r7 = 0;
    L_0x0213:
        r6.setVisibility(r7);
        r6 = r11.shadowTablet;
        r7 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r6.setBackgroundColor(r7);
        r6 = r11.shadowTablet;
        r0.addView(r6);
        r6 = r11.shadowTablet;
        r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$KFZR9bOIUYM1vrC9qoPrRupqDO4;
        r7.<init>(r11);
        r6.setOnTouchListener(r7);
        r6 = r11.shadowTablet;
        r7 = org.telegram.ui.-$$Lambda$LaunchActivity$OJponKw8R53ezoQT8H7udVOmkKQ.INSTANCE;
        r6.setOnClickListener(r7);
        r6 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r6.<init>(r11);
        r11.layersActionBarLayout = r6;
        r6 = r11.layersActionBarLayout;
        r6.setRemoveActionBarExtraHeight(r1);
        r6 = r11.layersActionBarLayout;
        r7 = r11.shadowTablet;
        r6.setBackgroundView(r7);
        r6 = r11.layersActionBarLayout;
        r6.setUseAlphaAnimations(r1);
        r6 = r11.layersActionBarLayout;
        r7 = NUM; // 0x7var_ float:1.7944807E38 double:1.052935559E-314;
        r6.setBackgroundResource(r7);
        r6 = r11.layersActionBarLayout;
        r7 = layerFragmentsStack;
        r6.init(r7);
        r6 = r11.layersActionBarLayout;
        r6.setDelegate(r11);
        r6 = r11.layersActionBarLayout;
        r7 = r11.drawerLayoutContainer;
        r6.setDrawerLayoutContainer(r7);
        r6 = r11.layersActionBarLayout;
        r7 = layerFragmentsStack;
        r7 = r7.isEmpty();
        if (r7 == 0) goto L_0x0271;
    L_0x0270:
        goto L_0x0272;
    L_0x0271:
        r8 = 0;
    L_0x0272:
        r6.setVisibility(r8);
        r6 = r11.layersActionBarLayout;
        r0.addView(r6);
        goto L_0x0287;
    L_0x027b:
        r0 = r11.drawerLayoutContainer;
        r6 = r11.actionBarLayout;
        r7 = new android.view.ViewGroup$LayoutParams;
        r7.<init>(r5, r5);
        r0.addView(r6, r7);
    L_0x0287:
        r0 = new org.telegram.ui.Components.RecyclerListView;
        r0.<init>(r11);
        r11.sideMenu = r0;
        r0 = r11.sideMenu;
        r6 = new org.telegram.ui.Components.SideMenultItemAnimator;
        r6.<init>(r0);
        r0.setItemAnimator(r6);
        r0 = r11.sideMenu;
        r6 = "chats_menuBackground";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setBackgroundColor(r6);
        r0 = r11.sideMenu;
        r6 = new androidx.recyclerview.widget.LinearLayoutManager;
        r6.<init>(r11, r1, r2);
        r0.setLayoutManager(r6);
        r0 = r11.sideMenu;
        r0.setAllowItemsInteractionDuringAnimation(r2);
        r0 = r11.sideMenu;
        r6 = new org.telegram.ui.Adapters.DrawerLayoutAdapter;
        r7 = r0.getItemAnimator();
        r6.<init>(r11, r7);
        r11.drawerLayoutAdapter = r6;
        r0.setAdapter(r6);
        r0 = r11.drawerLayoutContainer;
        r6 = r11.sideMenu;
        r0.setDrawerLayout(r6);
        r0 = r11.sideMenu;
        r0 = r0.getLayoutParams();
        r0 = (android.widget.FrameLayout.LayoutParams) r0;
        r6 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();
        r7 = org.telegram.messenger.AndroidUtilities.isTablet();
        r8 = NUM; // 0x43a00000 float:320.0 double:5.605467397E-315;
        if (r7 == 0) goto L_0x02e2;
    L_0x02dd:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x02f9;
    L_0x02e2:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r6.x;
        r6 = r6.y;
        r6 = java.lang.Math.min(r8, r6);
        r8 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 - r8;
        r6 = java.lang.Math.min(r7, r6);
    L_0x02f9:
        r0.width = r6;
        r0.height = r5;
        r6 = r11.sideMenu;
        r6.setLayoutParams(r0);
        r0 = r11.sideMenu;
        r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$dOtJNBBcNQv2FwIcA_NKr5dzUI0;
        r6.<init>(r11);
        r0.setOnItemClickListener(r6);
        r0 = r11.drawerLayoutContainer;
        r6 = r11.actionBarLayout;
        r0.setParentActionBarLayout(r6);
        r0 = r11.actionBarLayout;
        r6 = r11.drawerLayoutContainer;
        r0.setDrawerLayoutContainer(r6);
        r0 = r11.actionBarLayout;
        r6 = mainFragmentsStack;
        r0.init(r6);
        r0 = r11.actionBarLayout;
        r0.setDelegate(r11);
        org.telegram.ui.ActionBar.Theme.loadWallpaper();
        r0 = new org.telegram.ui.Components.PasscodeView;
        r0.<init>(r11);
        r11.passcodeView = r0;
        r0 = r11.drawerLayoutContainer;
        r6 = r11.passcodeView;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3);
        r0.addView(r6, r3);
        r11.checkCurrentAccount();
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r6 = new java.lang.Object[r1];
        r6[r2] = r11;
        r0.postNotificationName(r3, r6);
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
        if (r0 == 0) goto L_0x04c4;
    L_0x03bb:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x03d7;
    L_0x03c7:
        r0 = r11.actionBarLayout;
        r3 = new org.telegram.ui.LoginActivity;
        r3.<init>();
        r0.addFragmentToStack(r3);
        r0 = r11.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r2, r2);
        goto L_0x03eb;
    L_0x03d7:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r3 = r11.sideMenu;
        r0.setSideMenu(r3);
        r3 = r11.actionBarLayout;
        r3.addFragmentToStack(r0);
        r0 = r11.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r1, r2);
    L_0x03eb:
        if (r12 == 0) goto L_0x052b;
    L_0x03ed:
        r0 = "fragment";
        r0 = r12.getString(r0);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x052b;
    L_0x03f5:
        r3 = "args";
        r3 = r12.getBundle(r3);	 Catch:{ Exception -> 0x04bf }
        r4 = r0.hashCode();	 Catch:{ Exception -> 0x04bf }
        r6 = 4;
        r7 = 3;
        r8 = 2;
        switch(r4) {
            case -1529105743: goto L_0x0438;
            case -1349522494: goto L_0x042e;
            case 3052376: goto L_0x0424;
            case 98629247: goto L_0x041a;
            case 738950403: goto L_0x0410;
            case 1434631203: goto L_0x0406;
            default: goto L_0x0405;
        };	 Catch:{ Exception -> 0x04bf }
    L_0x0405:
        goto L_0x0442;
    L_0x0406:
        r4 = "settings";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x040e:
        r0 = 1;
        goto L_0x0443;
    L_0x0410:
        r4 = "channel";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x0418:
        r0 = 3;
        goto L_0x0443;
    L_0x041a:
        r4 = "group";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x0422:
        r0 = 2;
        goto L_0x0443;
    L_0x0424:
        r4 = "chat";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x042c:
        r0 = 0;
        goto L_0x0443;
    L_0x042e:
        r4 = "chat_profile";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x0436:
        r0 = 4;
        goto L_0x0443;
    L_0x0438:
        r4 = "wallpapers";
        r0 = r0.equals(r4);	 Catch:{ Exception -> 0x04bf }
        if (r0 == 0) goto L_0x0442;
    L_0x0440:
        r0 = 5;
        goto L_0x0443;
    L_0x0442:
        r0 = -1;
    L_0x0443:
        if (r0 == 0) goto L_0x04ac;
    L_0x0445:
        if (r0 == r1) goto L_0x049d;
    L_0x0447:
        if (r0 == r8) goto L_0x0489;
    L_0x0449:
        if (r0 == r7) goto L_0x0475;
    L_0x044b:
        if (r0 == r6) goto L_0x0461;
    L_0x044d:
        r3 = 5;
        if (r0 == r3) goto L_0x0452;
    L_0x0450:
        goto L_0x052b;
    L_0x0452:
        r0 = new org.telegram.ui.WallpapersListActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>(r2);	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x0461:
        if (r3 == 0) goto L_0x052b;
    L_0x0463:
        r0 = new org.telegram.ui.ProfileActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        if (r3 == 0) goto L_0x052b;
    L_0x0470:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x0475:
        if (r3 == 0) goto L_0x052b;
    L_0x0477:
        r0 = new org.telegram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        if (r3 == 0) goto L_0x052b;
    L_0x0484:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x0489:
        if (r3 == 0) goto L_0x052b;
    L_0x048b:
        r0 = new org.telegram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        if (r3 == 0) goto L_0x052b;
    L_0x0498:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x049d:
        r0 = new org.telegram.ui.SettingsActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>();	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x04ac:
        if (r3 == 0) goto L_0x052b;
    L_0x04ae:
        r0 = new org.telegram.ui.ChatActivity;	 Catch:{ Exception -> 0x04bf }
        r0.<init>(r3);	 Catch:{ Exception -> 0x04bf }
        r3 = r11.actionBarLayout;	 Catch:{ Exception -> 0x04bf }
        r3 = r3.addFragmentToStack(r0);	 Catch:{ Exception -> 0x04bf }
        if (r3 == 0) goto L_0x052b;
    L_0x04bb:
        r0.restoreSelfArgs(r12);	 Catch:{ Exception -> 0x04bf }
        goto L_0x052b;
    L_0x04bf:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x052b;
    L_0x04c4:
        r0 = r11.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r2);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r3 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r3 == 0) goto L_0x04d9;
    L_0x04d2:
        r0 = (org.telegram.ui.DialogsActivity) r0;
        r3 = r11.sideMenu;
        r0.setSideMenu(r3);
    L_0x04d9:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x050e;
    L_0x04df:
        r0 = r11.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 > r1) goto L_0x04f5;
    L_0x04e9:
        r0 = r11.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x04f5;
    L_0x04f3:
        r0 = 1;
        goto L_0x04f6;
    L_0x04f5:
        r0 = 0;
    L_0x04f6:
        r3 = r11.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r1) goto L_0x050f;
    L_0x0500:
        r3 = r11.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r2);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x050f;
    L_0x050c:
        r0 = 0;
        goto L_0x050f;
    L_0x050e:
        r0 = 1;
    L_0x050f:
        r3 = r11.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r1) goto L_0x0526;
    L_0x0519:
        r3 = r11.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r2);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x0526;
    L_0x0525:
        r0 = 0;
    L_0x0526:
        r3 = r11.drawerLayoutContainer;
        r3.setAllowOpenDrawer(r0, r2);
    L_0x052b:
        r11.checkLayout();
        r0 = r11.getIntent();
        if (r12 == 0) goto L_0x0536;
    L_0x0534:
        r12 = 1;
        goto L_0x0537;
    L_0x0536:
        r12 = 0;
    L_0x0537:
        r11.handleIntent(r0, r2, r12, r2);
        r12 = android.os.Build.DISPLAY;	 Catch:{ Exception -> 0x059b }
        r0 = android.os.Build.USER;	 Catch:{ Exception -> 0x059b }
        r2 = "";
        if (r12 == 0) goto L_0x0547;
    L_0x0542:
        r12 = r12.toLowerCase();	 Catch:{ Exception -> 0x059b }
        goto L_0x0548;
    L_0x0547:
        r12 = r2;
    L_0x0548:
        if (r0 == 0) goto L_0x054e;
    L_0x054a:
        r2 = r12.toLowerCase();	 Catch:{ Exception -> 0x059b }
    L_0x054e:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x059b }
        if (r0 == 0) goto L_0x056e;
    L_0x0552:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x059b }
        r0.<init>();	 Catch:{ Exception -> 0x059b }
        r3 = "OS name ";
        r0.append(r3);	 Catch:{ Exception -> 0x059b }
        r0.append(r12);	 Catch:{ Exception -> 0x059b }
        r3 = " ";
        r0.append(r3);	 Catch:{ Exception -> 0x059b }
        r0.append(r2);	 Catch:{ Exception -> 0x059b }
        r0 = r0.toString();	 Catch:{ Exception -> 0x059b }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x059b }
    L_0x056e:
        r0 = "flyme";
        r12 = r12.contains(r0);	 Catch:{ Exception -> 0x059b }
        if (r12 != 0) goto L_0x057e;
    L_0x0576:
        r12 = "flyme";
        r12 = r2.contains(r12);	 Catch:{ Exception -> 0x059b }
        if (r12 == 0) goto L_0x059f;
    L_0x057e:
        org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r1;	 Catch:{ Exception -> 0x059b }
        r12 = r11.getWindow();	 Catch:{ Exception -> 0x059b }
        r12 = r12.getDecorView();	 Catch:{ Exception -> 0x059b }
        r12 = r12.getRootView();	 Catch:{ Exception -> 0x059b }
        r0 = r12.getViewTreeObserver();	 Catch:{ Exception -> 0x059b }
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$KOGX7F1UQC0GXDka6tTbIrU0Wk0;	 Catch:{ Exception -> 0x059b }
        r2.<init>(r12);	 Catch:{ Exception -> 0x059b }
        r11.onGlobalLayoutListener = r2;	 Catch:{ Exception -> 0x059b }
        r0.addOnGlobalLayoutListener(r2);	 Catch:{ Exception -> 0x059b }
        goto L_0x059f;
    L_0x059b:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
    L_0x059f:
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
                lambda$runLinkRequest$28$LaunchActivity(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            i2 = this.drawerLayoutAdapter.getId(i);
            Bundle bundle;
            if (i2 == 2) {
                lambda$runLinkRequest$28$LaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 3) {
                bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                lambda$runLinkRequest$28$LaunchActivity(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                String str = "channel_intro";
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean(str, false)) {
                    lambda$runLinkRequest$28$LaunchActivity(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean(str, true).commit();
                } else {
                    bundle = new Bundle();
                    bundle.putInt("step", 0);
                    lambda$runLinkRequest$28$LaunchActivity(new ChannelCreateActivity(bundle));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 6) {
                lambda$runLinkRequest$28$LaunchActivity(new ContactsActivity(null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 7) {
                lambda$runLinkRequest$28$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 8) {
                lambda$runLinkRequest$28$LaunchActivity(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 10) {
                lambda$runLinkRequest$28$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 11) {
                bundle = new Bundle();
                bundle.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$28$LaunchActivity(new ChatActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (i2 == 12) {
                lambda$runLinkRequest$28$LaunchActivity(getCurrentWalletFragment(null));
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
        lambda$runLinkRequest$28$LaunchActivity(new WalletCreateActivity(0));
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
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int i) {
                    UserConfig.getInstance(i).unacceptedTermsOfService = null;
                    UserConfig.getInstance(i).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
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

    /* JADX WARNING: Removed duplicated region for block: B:112:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0d5e A:{Catch:{ all -> 0x0d52, all -> 0x0d59, Exception -> 0x0d62 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0390 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x097a  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0390 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x097a  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0390 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x097a  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0cc6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x11e7  */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x11db  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x118f  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x11db  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x11e7  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e2  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:579:0x0d59 */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11ff  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1253  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x120e  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1298  */
    /* JADX WARNING: Missing block: B:12:0x0040, code skipped:
            if ("android.intent.action.MAIN".equals(r46.getAction()) == false) goto L_0x0042;
     */
    /* JADX WARNING: Missing block: B:109:0x0213, code skipped:
            if (r15.sendingText == null) goto L_0x010e;
     */
    /* JADX WARNING: Missing block: B:491:0x0ba6, code skipped:
            if (r1.intValue() == 0) goto L_0x0ba8;
     */
    /* JADX WARNING: Missing block: B:641:0x0var_, code skipped:
            if (r4.checkCanOpenChat(r0, (org.telegram.ui.ActionBar.BaseFragment) r5.get(r5.size() - 1)) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Missing block: B:644:0x0var_, code skipped:
            if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r0), false, true, true, false) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Missing block: B:646:0x0f2b, code skipped:
            r13 = false;
     */
    /* JADX WARNING: Missing block: B:655:0x0var_, code skipped:
            if (r0.checkCanOpenChat(r5, (org.telegram.ui.ActionBar.BaseFragment) r4.get(r4.size() - 1)) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Missing block: B:657:0x0var_, code skipped:
            if (r2.actionBarLayout.presentFragment(new org.telegram.ui.ChatActivity(r5), false, true, true, false) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Missing block: B:730:0x115f, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x117d;
     */
    /* JADX WARNING: Missing block: B:734:0x117b, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x117d;
     */
    private boolean handleIntent(android.content.Intent r46, boolean r47, boolean r48, boolean r49) {
        /*
        r45 = this;
        r15 = r45;
        r14 = r46;
        r0 = r48;
        r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r45, r46);
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
        r1 = r46.getAction();
        r2 = "android.intent.action.MAIN";
        r1 = r2.equals(r1);
        if (r1 != 0) goto L_0x0049;
    L_0x0042:
        r1 = org.telegram.ui.PhotoViewer.getInstance();
        r1.closePhoto(r12, r13);
    L_0x0049:
        r1 = r46.getFlags();
        r11 = new int[r13];
        r2 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = "currentAccount";
        r2 = r14.getIntExtra(r3, r2);
        r11[r12] = r2;
        r2 = r11[r12];
        r15.switchToAccount(r2, r13);
        if (r49 != 0) goto L_0x007f;
    L_0x0060:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13);
        if (r2 != 0) goto L_0x006a;
    L_0x0066:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x007f;
    L_0x006a:
        r45.showPasscodeActivity();
        r15.passcodeSaveIntent = r14;
        r10 = r47;
        r15.passcodeSaveIntentIsNew = r10;
        r15.passcodeSaveIntentIsRestore = r0;
        r0 = r15.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0.saveConfig(r12);
        return r12;
    L_0x007f:
        r10 = r47;
        r2 = org.telegram.messenger.SharedConfig.directShare;
        r3 = "hash";
        r8 = 0;
        if (r2 == 0) goto L_0x00ad;
    L_0x0089:
        if (r14 == 0) goto L_0x00ad;
    L_0x008b:
        r2 = r46.getExtras();
        if (r2 == 0) goto L_0x00ad;
    L_0x0091:
        r2 = r46.getExtras();
        r4 = "dialogId";
        r4 = r2.getLong(r4, r8);
        r2 = r46.getExtras();
        r6 = r2.getLong(r3, r8);
        r16 = org.telegram.messenger.SharedConfig.directShareHash;
        r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x00aa;
    L_0x00a9:
        goto L_0x00ad;
    L_0x00aa:
        r20 = r4;
        goto L_0x00af;
    L_0x00ad:
        r20 = r8;
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
        r6 = 3;
        if (r1 != 0) goto L_0x0ec4;
    L_0x00c8:
        if (r14 == 0) goto L_0x0ec4;
    L_0x00ca:
        r1 = r46.getAction();
        if (r1 == 0) goto L_0x0ec4;
    L_0x00d0:
        if (r0 != 0) goto L_0x0ec4;
    L_0x00d2:
        r0 = r46.getAction();
        r1 = "android.intent.action.SEND";
        r0 = r1.equals(r0);
        r1 = "\n";
        r2 = "";
        if (r0 == 0) goto L_0x0225;
    L_0x00e2:
        r0 = r46.getType();
        if (r0 == 0) goto L_0x0111;
    L_0x00e8:
        r3 = "text/x-vcard";
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0111;
    L_0x00f0:
        r0 = r46.getExtras();	 Catch:{ Exception -> 0x010a }
        r1 = "android.intent.extra.STREAM";
        r0 = r0.get(r1);	 Catch:{ Exception -> 0x010a }
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x010a }
        if (r0 == 0) goto L_0x010e;
    L_0x00fe:
        r1 = r15.currentAccount;	 Catch:{ Exception -> 0x010a }
        r1 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r1, r12, r7, r7);	 Catch:{ Exception -> 0x010a }
        r15.contactsToSend = r1;	 Catch:{ Exception -> 0x010a }
        r15.contactsToSendUri = r0;	 Catch:{ Exception -> 0x010a }
        goto L_0x0217;
    L_0x010a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x010e:
        r0 = 1;
        goto L_0x0218;
    L_0x0111:
        r3 = "android.intent.extra.TEXT";
        r3 = r14.getStringExtra(r3);
        if (r3 != 0) goto L_0x0125;
    L_0x0119:
        r4 = "android.intent.extra.TEXT";
        r4 = r14.getCharSequenceExtra(r4);
        if (r4 == 0) goto L_0x0125;
    L_0x0121:
        r3 = r4.toString();
    L_0x0125:
        r4 = "android.intent.extra.SUBJECT";
        r4 = r14.getStringExtra(r4);
        r5 = android.text.TextUtils.isEmpty(r3);
        if (r5 != 0) goto L_0x015c;
    L_0x0131:
        r5 = "http://";
        r5 = r3.startsWith(r5);
        if (r5 != 0) goto L_0x0141;
    L_0x0139:
        r5 = "https://";
        r5 = r3.startsWith(r5);
        if (r5 == 0) goto L_0x0159;
    L_0x0141:
        r5 = android.text.TextUtils.isEmpty(r4);
        if (r5 != 0) goto L_0x0159;
    L_0x0147:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r5.append(r1);
        r5.append(r3);
        r3 = r5.toString();
    L_0x0159:
        r15.sendingText = r3;
        goto L_0x0164;
    L_0x015c:
        r1 = android.text.TextUtils.isEmpty(r4);
        if (r1 != 0) goto L_0x0164;
    L_0x0162:
        r15.sendingText = r4;
    L_0x0164:
        r1 = "android.intent.extra.STREAM";
        r1 = r14.getParcelableExtra(r1);
        if (r1 == 0) goto L_0x0211;
    L_0x016c:
        r3 = r1 instanceof android.net.Uri;
        if (r3 != 0) goto L_0x0178;
    L_0x0170:
        r1 = r1.toString();
        r1 = android.net.Uri.parse(r1);
    L_0x0178:
        r1 = (android.net.Uri) r1;
        if (r1 == 0) goto L_0x0184;
    L_0x017c:
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);
        if (r3 == 0) goto L_0x0184;
    L_0x0182:
        r3 = 1;
        goto L_0x0185;
    L_0x0184:
        r3 = 0;
    L_0x0185:
        if (r3 != 0) goto L_0x020f;
    L_0x0187:
        if (r1 == 0) goto L_0x01bb;
    L_0x0189:
        if (r0 == 0) goto L_0x0193;
    L_0x018b:
        r4 = "image/";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x01a3;
    L_0x0193:
        r4 = r1.toString();
        r4 = r4.toLowerCase();
        r5 = ".jpg";
        r4 = r4.endsWith(r5);
        if (r4 == 0) goto L_0x01bb;
    L_0x01a3:
        r0 = r15.photoPathsArray;
        if (r0 != 0) goto L_0x01ae;
    L_0x01a7:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.photoPathsArray = r0;
    L_0x01ae:
        r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;
        r0.<init>();
        r0.uri = r1;
        r1 = r15.photoPathsArray;
        r1.add(r0);
        goto L_0x020f;
    L_0x01bb:
        r4 = org.telegram.messenger.AndroidUtilities.getPath(r1);
        if (r4 == 0) goto L_0x01fd;
    L_0x01c1:
        r5 = "file:";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x01cf;
    L_0x01c9:
        r5 = "file://";
        r4 = r4.replace(r5, r2);
    L_0x01cf:
        if (r0 == 0) goto L_0x01dc;
    L_0x01d1:
        r2 = "video/";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x01dc;
    L_0x01d9:
        r15.videoPath = r4;
        goto L_0x020f;
    L_0x01dc:
        r0 = r15.documentsPathsArray;
        if (r0 != 0) goto L_0x01ee;
    L_0x01e0:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.documentsPathsArray = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r15.documentsOriginalPathsArray = r0;
    L_0x01ee:
        r0 = r15.documentsPathsArray;
        r0.add(r4);
        r0 = r15.documentsOriginalPathsArray;
        r1 = r1.toString();
        r0.add(r1);
        goto L_0x020f;
    L_0x01fd:
        r2 = r15.documentsUrisArray;
        if (r2 != 0) goto L_0x0208;
    L_0x0201:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r15.documentsUrisArray = r2;
    L_0x0208:
        r2 = r15.documentsUrisArray;
        r2.add(r1);
        r15.documentsMimeType = r0;
    L_0x020f:
        r0 = r3;
        goto L_0x0218;
    L_0x0211:
        r0 = r15.sendingText;
        if (r0 != 0) goto L_0x0217;
    L_0x0215:
        goto L_0x010e;
    L_0x0217:
        r0 = 0;
    L_0x0218:
        if (r0 == 0) goto L_0x0ec4;
    L_0x021a:
        r0 = "Unsupported content";
        r0 = android.widget.Toast.makeText(r15, r0, r12);
        r0.show();
        goto L_0x0ec4;
    L_0x0225:
        r0 = r46.getAction();
        r4 = "android.intent.action.SEND_MULTIPLE";
        r0 = r4.equals(r0);
        if (r0 == 0) goto L_0x0334;
    L_0x0231:
        r0 = "android.intent.extra.STREAM";
        r0 = r14.getParcelableArrayListExtra(r0);	 Catch:{ Exception -> 0x031c }
        r1 = r46.getType();	 Catch:{ Exception -> 0x031c }
        if (r0 == 0) goto L_0x026e;
    L_0x023d:
        r3 = 0;
    L_0x023e:
        r4 = r0.size();	 Catch:{ Exception -> 0x031c }
        if (r3 >= r4) goto L_0x0267;
    L_0x0244:
        r4 = r0.get(r3);	 Catch:{ Exception -> 0x031c }
        r4 = (android.os.Parcelable) r4;	 Catch:{ Exception -> 0x031c }
        r5 = r4 instanceof android.net.Uri;	 Catch:{ Exception -> 0x031c }
        if (r5 != 0) goto L_0x0256;
    L_0x024e:
        r4 = r4.toString();	 Catch:{ Exception -> 0x031c }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x031c }
    L_0x0256:
        r4 = (android.net.Uri) r4;	 Catch:{ Exception -> 0x031c }
        if (r4 == 0) goto L_0x0265;
    L_0x025a:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r4);	 Catch:{ Exception -> 0x031c }
        if (r4 == 0) goto L_0x0265;
    L_0x0260:
        r0.remove(r3);	 Catch:{ Exception -> 0x031c }
        r3 = r3 + -1;
    L_0x0265:
        r3 = r3 + r13;
        goto L_0x023e;
    L_0x0267:
        r3 = r0.isEmpty();	 Catch:{ Exception -> 0x031c }
        if (r3 == 0) goto L_0x026e;
    L_0x026d:
        r0 = r7;
    L_0x026e:
        if (r0 == 0) goto L_0x0320;
    L_0x0270:
        if (r1 == 0) goto L_0x02af;
    L_0x0272:
        r3 = "image/";
        r3 = r1.startsWith(r3);	 Catch:{ Exception -> 0x031c }
        if (r3 == 0) goto L_0x02af;
    L_0x027a:
        r1 = 0;
    L_0x027b:
        r2 = r0.size();	 Catch:{ Exception -> 0x031c }
        if (r1 >= r2) goto L_0x031a;
    L_0x0281:
        r2 = r0.get(r1);	 Catch:{ Exception -> 0x031c }
        r2 = (android.os.Parcelable) r2;	 Catch:{ Exception -> 0x031c }
        r3 = r2 instanceof android.net.Uri;	 Catch:{ Exception -> 0x031c }
        if (r3 != 0) goto L_0x0293;
    L_0x028b:
        r2 = r2.toString();	 Catch:{ Exception -> 0x031c }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x031c }
    L_0x0293:
        r2 = (android.net.Uri) r2;	 Catch:{ Exception -> 0x031c }
        r3 = r15.photoPathsArray;	 Catch:{ Exception -> 0x031c }
        if (r3 != 0) goto L_0x02a0;
    L_0x0299:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x031c }
        r3.<init>();	 Catch:{ Exception -> 0x031c }
        r15.photoPathsArray = r3;	 Catch:{ Exception -> 0x031c }
    L_0x02a0:
        r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;	 Catch:{ Exception -> 0x031c }
        r3.<init>();	 Catch:{ Exception -> 0x031c }
        r3.uri = r2;	 Catch:{ Exception -> 0x031c }
        r2 = r15.photoPathsArray;	 Catch:{ Exception -> 0x031c }
        r2.add(r3);	 Catch:{ Exception -> 0x031c }
        r1 = r1 + 1;
        goto L_0x027b;
    L_0x02af:
        r3 = 0;
    L_0x02b0:
        r4 = r0.size();	 Catch:{ Exception -> 0x031c }
        if (r3 >= r4) goto L_0x031a;
    L_0x02b6:
        r4 = r0.get(r3);	 Catch:{ Exception -> 0x031c }
        r4 = (android.os.Parcelable) r4;	 Catch:{ Exception -> 0x031c }
        r5 = r4 instanceof android.net.Uri;	 Catch:{ Exception -> 0x031c }
        if (r5 != 0) goto L_0x02c8;
    L_0x02c0:
        r4 = r4.toString();	 Catch:{ Exception -> 0x031c }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x031c }
    L_0x02c8:
        r5 = r4;
        r5 = (android.net.Uri) r5;	 Catch:{ Exception -> 0x031c }
        r8 = org.telegram.messenger.AndroidUtilities.getPath(r5);	 Catch:{ Exception -> 0x031c }
        r4 = r4.toString();	 Catch:{ Exception -> 0x031c }
        if (r4 != 0) goto L_0x02d6;
    L_0x02d5:
        r4 = r8;
    L_0x02d6:
        if (r8 == 0) goto L_0x0303;
    L_0x02d8:
        r5 = "file:";
        r5 = r8.startsWith(r5);	 Catch:{ Exception -> 0x031c }
        if (r5 == 0) goto L_0x02e6;
    L_0x02e0:
        r5 = "file://";
        r8 = r8.replace(r5, r2);	 Catch:{ Exception -> 0x031c }
    L_0x02e6:
        r5 = r15.documentsPathsArray;	 Catch:{ Exception -> 0x031c }
        if (r5 != 0) goto L_0x02f8;
    L_0x02ea:
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x031c }
        r5.<init>();	 Catch:{ Exception -> 0x031c }
        r15.documentsPathsArray = r5;	 Catch:{ Exception -> 0x031c }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x031c }
        r5.<init>();	 Catch:{ Exception -> 0x031c }
        r15.documentsOriginalPathsArray = r5;	 Catch:{ Exception -> 0x031c }
    L_0x02f8:
        r5 = r15.documentsPathsArray;	 Catch:{ Exception -> 0x031c }
        r5.add(r8);	 Catch:{ Exception -> 0x031c }
        r5 = r15.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x031c }
        r5.add(r4);	 Catch:{ Exception -> 0x031c }
        goto L_0x0315;
    L_0x0303:
        r4 = r15.documentsUrisArray;	 Catch:{ Exception -> 0x031c }
        if (r4 != 0) goto L_0x030e;
    L_0x0307:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x031c }
        r4.<init>();	 Catch:{ Exception -> 0x031c }
        r15.documentsUrisArray = r4;	 Catch:{ Exception -> 0x031c }
    L_0x030e:
        r4 = r15.documentsUrisArray;	 Catch:{ Exception -> 0x031c }
        r4.add(r5);	 Catch:{ Exception -> 0x031c }
        r15.documentsMimeType = r1;	 Catch:{ Exception -> 0x031c }
    L_0x0315:
        r3 = r3 + 1;
        r8 = 0;
        goto L_0x02b0;
    L_0x031a:
        r0 = 0;
        goto L_0x0321;
    L_0x031c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0320:
        r0 = 1;
    L_0x0321:
        if (r0 == 0) goto L_0x032c;
    L_0x0323:
        r0 = "Unsupported content";
        r0 = android.widget.Toast.makeText(r15, r0, r12);
        r0.show();
    L_0x032c:
        r6 = r11;
        r1 = r14;
        r2 = r15;
        r3 = 0;
        r30 = 0;
        goto L_0x0eca;
    L_0x0334:
        r0 = r46.getAction();
        r4 = "android.intent.action.VIEW";
        r0 = r4.equals(r0);
        if (r0 == 0) goto L_0x0df7;
    L_0x0340:
        r0 = r46.getData();
        if (r0 == 0) goto L_0x0ddc;
    L_0x0346:
        r4 = r0.getScheme();
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x034c:
        r5 = -1;
        r8 = r4.hashCode();
        r9 = 3699; // 0xe73 float:5.183E-42 double:1.8275E-320;
        if (r8 == r9) goto L_0x0383;
    L_0x0355:
        r9 = 115027; // 0x1CLASSNAME float:1.61187E-40 double:5.6831E-319;
        if (r8 == r9) goto L_0x0379;
    L_0x035a:
        r9 = 3213448; // 0x310888 float:4.503E-39 double:1.5876543E-317;
        if (r8 == r9) goto L_0x036f;
    L_0x035f:
        r9 = 99617003; // 0x5var_eb float:2.2572767E-35 double:4.9217339E-316;
        if (r8 == r9) goto L_0x0365;
    L_0x0364:
        goto L_0x038d;
    L_0x0365:
        r8 = "https";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x038d;
    L_0x036d:
        r4 = 1;
        goto L_0x038e;
    L_0x036f:
        r8 = "http";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x038d;
    L_0x0377:
        r4 = 0;
        goto L_0x038e;
    L_0x0379:
        r8 = "ton";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x038d;
    L_0x0381:
        r4 = 2;
        goto L_0x038e;
    L_0x0383:
        r8 = "tg";
        r4 = r4.equals(r8);
        if (r4 == 0) goto L_0x038d;
    L_0x038b:
        r4 = 3;
        goto L_0x038e;
    L_0x038d:
        r4 = -1;
    L_0x038e:
        if (r4 == 0) goto L_0x0954;
    L_0x0390:
        if (r4 == r13) goto L_0x0954;
    L_0x0392:
        r5 = 2;
        if (r4 == r5) goto L_0x08ef;
    L_0x0395:
        if (r4 == r6) goto L_0x0399;
    L_0x0397:
        goto L_0x0CLASSNAME;
    L_0x0399:
        r0 = r0.toString();
        r4 = "tg:resolve";
        r4 = r0.startsWith(r4);
        r5 = "nonce";
        r8 = "callback_url";
        r9 = "public_key";
        r6 = "bot_id";
        r7 = "payload";
        r13 = "scope";
        r12 = "tg://telegram.org";
        if (r4 != 0) goto L_0x080a;
    L_0x03b3:
        r4 = "tg://resolve";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03bd;
    L_0x03bb:
        goto L_0x080a;
    L_0x03bd:
        r4 = "tg:privatepost";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x07b0;
    L_0x03c5:
        r4 = "tg://privatepost";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03cf;
    L_0x03cd:
        goto L_0x07b0;
    L_0x03cf:
        r4 = "tg:bg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x06e5;
    L_0x03d7:
        r4 = "tg://bg";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03e1;
    L_0x03df:
        goto L_0x06e5;
    L_0x03e1:
        r4 = "tg:join";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x06cb;
    L_0x03e9:
        r4 = "tg://join";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x03f3;
    L_0x03f1:
        goto L_0x06cb;
    L_0x03f3:
        r4 = "tg:addstickers";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x06b0;
    L_0x03fb:
        r4 = "tg://addstickers";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0405;
    L_0x0403:
        goto L_0x06b0;
    L_0x0405:
        r4 = "tg:msg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0626;
    L_0x040d:
        r4 = "tg://msg";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0626;
    L_0x0415:
        r4 = "tg://share";
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0626;
    L_0x041d:
        r4 = "tg:share";
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0427;
    L_0x0425:
        goto L_0x0626;
    L_0x0427:
        r1 = "tg:confirmphone";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x0601;
    L_0x042f:
        r1 = "tg://confirmphone";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0439;
    L_0x0437:
        goto L_0x0601;
    L_0x0439:
        r1 = "tg:login";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x05de;
    L_0x0441:
        r1 = "tg://login";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x044b;
    L_0x0449:
        goto L_0x05de;
    L_0x044b:
        r1 = "tg:openmessage";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x057b;
    L_0x0453:
        r1 = "tg://openmessage";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x045d;
    L_0x045b:
        goto L_0x057b;
    L_0x045d:
        r1 = "tg:passport";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x051b;
    L_0x0465:
        r1 = "tg://passport";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x051b;
    L_0x046d:
        r1 = "tg:secureid";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0477;
    L_0x0475:
        goto L_0x051b;
    L_0x0477:
        r1 = "tg:setlanguage";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x04f2;
    L_0x047f:
        r1 = "tg://setlanguage";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0488;
    L_0x0487:
        goto L_0x04f2;
    L_0x0488:
        r1 = "tg:addtheme";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x04c3;
    L_0x0490:
        r1 = "tg://addtheme";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0499;
    L_0x0498:
        goto L_0x04c3;
    L_0x0499:
        r1 = "tg://";
        r0 = r0.replace(r1, r2);
        r1 = "tg:";
        r0 = r0.replace(r1, r2);
        r1 = 63;
        r1 = r0.indexOf(r1);
        if (r1 < 0) goto L_0x04b2;
    L_0x04ad:
        r2 = 0;
        r0 = r0.substring(r2, r1);
    L_0x04b2:
        r24 = r0;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        goto L_0x087f;
    L_0x04c3:
        r1 = "tg:addtheme";
        r0 = r0.replace(r1, r12);
        r1 = "tg://addtheme";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "slug";
        r0 = r0.getQueryParameter(r1);
        r28 = r0;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        goto L_0x0887;
    L_0x04f2:
        r1 = "tg:setlanguage";
        r0 = r0.replace(r1, r12);
        r1 = "tg://setlanguage";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "lang";
        r0 = r0.getQueryParameter(r1);
        r25 = r0;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        goto L_0x0881;
    L_0x051b:
        r1 = "tg:passport";
        r0 = r0.replace(r1, r12);
        r1 = "tg://passport";
        r0 = r0.replace(r1, r12);
        r1 = "tg:secureid";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = r0.getQueryParameter(r13);
        r4 = android.text.TextUtils.isEmpty(r2);
        if (r4 != 0) goto L_0x055a;
    L_0x0540:
        r4 = "{";
        r4 = r2.startsWith(r4);
        if (r4 == 0) goto L_0x055a;
    L_0x0549:
        r4 = "}";
        r4 = r2.endsWith(r4);
        if (r4 == 0) goto L_0x055a;
    L_0x0552:
        r4 = r0.getQueryParameter(r5);
        r1.put(r5, r4);
        goto L_0x0561;
    L_0x055a:
        r4 = r0.getQueryParameter(r7);
        r1.put(r7, r4);
    L_0x0561:
        r4 = r0.getQueryParameter(r6);
        r1.put(r6, r4);
        r1.put(r13, r2);
        r2 = r0.getQueryParameter(r9);
        r1.put(r9, r2);
        r0 = r0.getQueryParameter(r8);
        r1.put(r8, r0);
        goto L_0x0870;
    L_0x057b:
        r1 = "tg:openmessage";
        r0 = r0.replace(r1, r12);
        r1 = "tg://openmessage";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "user_id";
        r1 = r0.getQueryParameter(r1);
        r2 = "chat_id";
        r2 = r0.getQueryParameter(r2);
        r4 = "message_id";
        r0 = r0.getQueryParameter(r4);
        if (r1 == 0) goto L_0x05a5;
    L_0x059f:
        r12 = java.lang.Integer.parseInt(r1);	 Catch:{ NumberFormatException -> 0x05ad }
        r1 = 0;
        goto L_0x05af;
    L_0x05a5:
        if (r2 == 0) goto L_0x05ad;
    L_0x05a7:
        r12 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x05ad }
        r1 = r12;
        goto L_0x05ae;
    L_0x05ad:
        r1 = 0;
    L_0x05ae:
        r12 = 0;
    L_0x05af:
        if (r0 == 0) goto L_0x05b6;
    L_0x05b1:
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x05b6 }
        goto L_0x05b7;
    L_0x05b6:
        r0 = 0;
    L_0x05b7:
        r35 = r0;
        r34 = r1;
        r33 = r12;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
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
        goto L_0x0cae;
    L_0x05de:
        r1 = "tg:login";
        r0 = r0.replace(r1, r12);
        r1 = "tg://login";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "code";
        r0 = r0.getQueryParameter(r1);
        r18 = r0;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        goto L_0x0807;
    L_0x0601:
        r1 = "tg:confirmphone";
        r0 = r0.replace(r1, r12);
        r1 = "tg://confirmphone";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "phone";
        r7 = r0.getQueryParameter(r1);
        r0 = r0.getQueryParameter(r3);
        r13 = r0;
        r6 = r7;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        goto L_0x0805;
    L_0x0626:
        r4 = "tg:msg";
        r0 = r0.replace(r4, r12);
        r4 = "tg://msg";
        r0 = r0.replace(r4, r12);
        r4 = "tg://share";
        r0 = r0.replace(r4, r12);
        r4 = "tg:share";
        r0 = r0.replace(r4, r12);
        r0 = android.net.Uri.parse(r0);
        r4 = "url";
        r4 = r0.getQueryParameter(r4);
        if (r4 != 0) goto L_0x064b;
    L_0x064a:
        goto L_0x064c;
    L_0x064b:
        r2 = r4;
    L_0x064c:
        r4 = "text";
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0682;
    L_0x0654:
        r4 = r2.length();
        if (r4 <= 0) goto L_0x066b;
    L_0x065a:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r1);
        r2 = r4.toString();
        r12 = 1;
        goto L_0x066c;
    L_0x066b:
        r12 = 0;
    L_0x066c:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r2 = "text";
        r0 = r0.getQueryParameter(r2);
        r4.append(r0);
        r2 = r4.toString();
        goto L_0x0683;
    L_0x0682:
        r12 = 0;
    L_0x0683:
        r0 = r2.length();
        r4 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r0 <= r4) goto L_0x0694;
    L_0x068b:
        r0 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r4 = 0;
        r0 = r2.substring(r4, r0);
        r7 = r0;
        goto L_0x0696;
    L_0x0694:
        r4 = 0;
        r7 = r2;
    L_0x0696:
        r0 = r7.endsWith(r1);
        if (r0 == 0) goto L_0x06a7;
    L_0x069c:
        r0 = r7.length();
        r2 = 1;
        r0 = r0 - r2;
        r7 = r7.substring(r4, r0);
        goto L_0x0696;
    L_0x06a7:
        r0 = r7;
        r9 = r12;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        goto L_0x0803;
    L_0x06b0:
        r1 = "tg:addstickers";
        r0 = r0.replace(r1, r12);
        r1 = "tg://addstickers";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "set";
        r0 = r0.getQueryParameter(r1);
        r5 = r0;
        r0 = 0;
        r4 = 0;
        goto L_0x07ff;
    L_0x06cb:
        r1 = "tg:join";
        r0 = r0.replace(r1, r12);
        r1 = "tg://join";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "invite";
        r0 = r0.getQueryParameter(r1);
        r4 = r0;
        r0 = 0;
        goto L_0x07fe;
    L_0x06e5:
        r1 = "tg:bg";
        r0 = r0.replace(r1, r12);
        r1 = "tg://bg";
        r0 = r0.replace(r1, r12);
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
        if (r2 != 0) goto L_0x0715;
    L_0x070d:
        r2 = "color";
        r2 = r0.getQueryParameter(r2);
        r1.slug = r2;
    L_0x0715:
        r2 = r1.slug;
        if (r2 == 0) goto L_0x0733;
    L_0x0719:
        r2 = r2.length();
        r4 = 6;
        if (r2 != r4) goto L_0x0733;
    L_0x0720:
        r0 = r1.settings;	 Catch:{ Exception -> 0x072f }
        r2 = r1.slug;	 Catch:{ Exception -> 0x072f }
        r4 = 16;
        r2 = java.lang.Integer.parseInt(r2, r4);	 Catch:{ Exception -> 0x072f }
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r2 = r2 | r4;
        r0.background_color = r2;	 Catch:{ Exception -> 0x072f }
    L_0x072f:
        r2 = 0;
        r1.slug = r2;
        goto L_0x079b;
    L_0x0733:
        r2 = "mode";
        r2 = r0.getQueryParameter(r2);
        if (r2 == 0) goto L_0x0770;
    L_0x073b:
        r2 = r2.toLowerCase();
        r4 = " ";
        r2 = r2.split(r4);
        if (r2 == 0) goto L_0x0770;
    L_0x0747:
        r4 = r2.length;
        if (r4 <= 0) goto L_0x0770;
    L_0x074a:
        r4 = 0;
    L_0x074b:
        r5 = r2.length;
        if (r4 >= r5) goto L_0x0770;
    L_0x074e:
        r5 = r2[r4];
        r6 = "blur";
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x075e;
    L_0x0758:
        r5 = r1.settings;
        r6 = 1;
        r5.blur = r6;
        goto L_0x076d;
    L_0x075e:
        r6 = 1;
        r5 = r2[r4];
        r7 = "motion";
        r5 = r7.equals(r5);
        if (r5 == 0) goto L_0x076d;
    L_0x0769:
        r5 = r1.settings;
        r5.motion = r6;
    L_0x076d:
        r4 = r4 + 1;
        goto L_0x074b;
    L_0x0770:
        r2 = r1.settings;
        r4 = "intensity";
        r4 = r0.getQueryParameter(r4);
        r4 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r4.intValue();
        r2.intensity = r4;
        r2 = "bg_color";
        r0 = r0.getQueryParameter(r2);	 Catch:{ Exception -> 0x079b }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x079b }
        if (r2 != 0) goto L_0x079b;
    L_0x078e:
        r2 = r1.settings;	 Catch:{ Exception -> 0x079b }
        r4 = 16;
        r0 = java.lang.Integer.parseInt(r0, r4);	 Catch:{ Exception -> 0x079b }
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0 = r0 | r4;
        r2.background_color = r0;	 Catch:{ Exception -> 0x079b }
    L_0x079b:
        r26 = r1;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        goto L_0x0883;
    L_0x07b0:
        r1 = "tg:privatepost";
        r0 = r0.replace(r1, r12);
        r1 = "tg://privatepost";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "post";
        r1 = r0.getQueryParameter(r1);
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r2 = "channel";
        r0 = r0.getQueryParameter(r2);
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r2 = r1.intValue();
        if (r2 == 0) goto L_0x07fc;
    L_0x07da:
        r2 = r0.intValue();
        if (r2 != 0) goto L_0x07e1;
    L_0x07e0:
        goto L_0x07fc;
    L_0x07e1:
        r27 = r0;
        r29 = r1;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r28 = 0;
        goto L_0x0889;
    L_0x07fc:
        r0 = 0;
        r4 = 0;
    L_0x07fe:
        r5 = 0;
    L_0x07ff:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
    L_0x0803:
        r12 = 0;
        r13 = 0;
    L_0x0805:
        r18 = 0;
    L_0x0807:
        r23 = 0;
        goto L_0x087d;
    L_0x080a:
        r1 = "tg:resolve";
        r0 = r0.replace(r1, r12);
        r1 = "tg://resolve";
        r0 = r0.replace(r1, r12);
        r0 = android.net.Uri.parse(r0);
        r1 = "domain";
        r1 = r0.getQueryParameter(r1);
        r2 = "telegrampassport";
        r2 = r2.equals(r1);
        if (r2 == 0) goto L_0x088e;
    L_0x0828:
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = r0.getQueryParameter(r13);
        r4 = android.text.TextUtils.isEmpty(r2);
        if (r4 != 0) goto L_0x0851;
    L_0x0837:
        r4 = "{";
        r4 = r2.startsWith(r4);
        if (r4 == 0) goto L_0x0851;
    L_0x0840:
        r4 = "}";
        r4 = r2.endsWith(r4);
        if (r4 == 0) goto L_0x0851;
    L_0x0849:
        r4 = r0.getQueryParameter(r5);
        r1.put(r5, r4);
        goto L_0x0858;
    L_0x0851:
        r4 = r0.getQueryParameter(r7);
        r1.put(r7, r4);
    L_0x0858:
        r4 = r0.getQueryParameter(r6);
        r1.put(r6, r4);
        r1.put(r13, r2);
        r2 = r0.getQueryParameter(r9);
        r1.put(r9, r2);
        r0 = r0.getQueryParameter(r8);
        r1.put(r8, r0);
    L_0x0870:
        r23 = r1;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r18 = 0;
    L_0x087d:
        r24 = 0;
    L_0x087f:
        r25 = 0;
    L_0x0881:
        r26 = 0;
    L_0x0883:
        r27 = 0;
        r28 = 0;
    L_0x0887:
        r29 = 0;
    L_0x0889:
        r30 = 0;
        r31 = 0;
        goto L_0x08eb;
    L_0x088e:
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
        if (r6 != 0) goto L_0x08ce;
    L_0x08b0:
        r7 = r1;
        r30 = r2;
        r31 = r4;
        r12 = r5;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r8 = 0;
        r9 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        goto L_0x08eb;
    L_0x08ce:
        r29 = r0;
        r7 = r1;
        r30 = r2;
        r31 = r4;
        r12 = r5;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r8 = 0;
        r9 = 0;
        r13 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
    L_0x08eb:
        r32 = 0;
        goto L_0x0ca8;
    L_0x08ef:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x0951;
    L_0x08f5:
        r1 = org.telegram.messenger.UserConfig.selectedAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.walletConfig;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0951;
    L_0x0903:
        r1 = org.telegram.messenger.UserConfig.selectedAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.walletBlockchainName;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0951;
    L_0x0911:
        r0 = r0.toString();
        r1 = "ton:transfer";
        r1 = r0.startsWith(r1);
        if (r1 != 0) goto L_0x0928;
    L_0x091d:
        r1 = "ton://transfer";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0926;
    L_0x0925:
        goto L_0x0928;
    L_0x0926:
        r7 = 0;
        goto L_0x0930;
    L_0x0928:
        r1 = "ton:transfer";
        r2 = "ton://transfer";
        r7 = r0.replace(r1, r2);
    L_0x0930:
        r32 = r7;
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
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
        goto L_0x0ca8;
    L_0x0951:
        r8 = 0;
        goto L_0x0c8a;
    L_0x0954:
        r4 = r0.getHost();
        r4 = r4.toLowerCase();
        r5 = "telegram.me";
        r5 = r4.equals(r5);
        if (r5 != 0) goto L_0x0974;
    L_0x0964:
        r5 = "t.me";
        r5 = r4.equals(r5);
        if (r5 != 0) goto L_0x0974;
    L_0x096c:
        r5 = "telegram.dog";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0951;
    L_0x0974:
        r4 = r0.getPath();
        if (r4 == 0) goto L_0x0c4d;
    L_0x097a:
        r5 = r4.length();
        r6 = 1;
        if (r5 <= r6) goto L_0x0c4d;
    L_0x0981:
        r4 = r4.substring(r6);
        r5 = "bg/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0a51;
    L_0x098d:
        r7 = new org.telegram.tgnet.TLRPC$TL_wallPaper;
        r7.<init>();
        r1 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
        r1.<init>();
        r7.settings = r1;
        r1 = "bg/";
        r1 = r4.replace(r1, r2);
        r7.slug = r1;
        r1 = r7.slug;
        if (r1 == 0) goto L_0x09c0;
    L_0x09a5:
        r1 = r1.length();
        r2 = 6;
        if (r1 != r2) goto L_0x09c0;
    L_0x09ac:
        r0 = r7.settings;	 Catch:{ Exception -> 0x09bb }
        r1 = r7.slug;	 Catch:{ Exception -> 0x09bb }
        r2 = 16;
        r1 = java.lang.Integer.parseInt(r1, r2);	 Catch:{ Exception -> 0x09bb }
        r2 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r1 = r1 | r2;
        r0.background_color = r1;	 Catch:{ Exception -> 0x09bb }
    L_0x09bb:
        r8 = 0;
        r7.slug = r8;
        goto L_0x0a3c;
    L_0x09c0:
        r8 = 0;
        r1 = "mode";
        r1 = r0.getQueryParameter(r1);
        if (r1 == 0) goto L_0x09fe;
    L_0x09c9:
        r1 = r1.toLowerCase();
        r2 = " ";
        r1 = r1.split(r2);
        if (r1 == 0) goto L_0x09fe;
    L_0x09d5:
        r2 = r1.length;
        if (r2 <= 0) goto L_0x09fe;
    L_0x09d8:
        r2 = 0;
    L_0x09d9:
        r4 = r1.length;
        if (r2 >= r4) goto L_0x09fe;
    L_0x09dc:
        r4 = r1[r2];
        r5 = "blur";
        r4 = r5.equals(r4);
        if (r4 == 0) goto L_0x09ec;
    L_0x09e6:
        r4 = r7.settings;
        r5 = 1;
        r4.blur = r5;
        goto L_0x09fb;
    L_0x09ec:
        r5 = 1;
        r4 = r1[r2];
        r6 = "motion";
        r4 = r6.equals(r4);
        if (r4 == 0) goto L_0x09fb;
    L_0x09f7:
        r4 = r7.settings;
        r4.motion = r5;
    L_0x09fb:
        r2 = r2 + 1;
        goto L_0x09d9;
    L_0x09fe:
        r1 = "intensity";
        r1 = r0.getQueryParameter(r1);
        r2 = android.text.TextUtils.isEmpty(r1);
        if (r2 != 0) goto L_0x0a17;
    L_0x0a0a:
        r2 = r7.settings;
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r1 = r1.intValue();
        r2.intensity = r1;
        goto L_0x0a1d;
    L_0x0a17:
        r1 = r7.settings;
        r2 = 50;
        r1.intensity = r2;
    L_0x0a1d:
        r1 = "bg_color";
        r0 = r0.getQueryParameter(r1);	 Catch:{ Exception -> 0x0a3c }
        r1 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0a3c }
        if (r1 != 0) goto L_0x0a37;
    L_0x0a29:
        r1 = r7.settings;	 Catch:{ Exception -> 0x0a3c }
        r2 = 16;
        r0 = java.lang.Integer.parseInt(r0, r2);	 Catch:{ Exception -> 0x0a3c }
        r2 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0 = r0 | r2;
        r1.background_color = r0;	 Catch:{ Exception -> 0x0a3c }
        goto L_0x0a3c;
    L_0x0a37:
        r0 = r7.settings;	 Catch:{ Exception -> 0x0a3c }
        r1 = -1;
        r0.background_color = r1;	 Catch:{ Exception -> 0x0a3c }
    L_0x0a3c:
        r24 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
        r19 = r18;
        r23 = r19;
        r25 = r23;
        goto L_0x0CLASSNAME;
    L_0x0a51:
        r8 = 0;
        r5 = "login/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0a73;
    L_0x0a5a:
        r0 = "login/";
        r7 = r4.replace(r0, r2);
        r23 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
        r19 = r18;
        r24 = r19;
        goto L_0x0c5f;
    L_0x0a73:
        r5 = "joinchat/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0a85;
    L_0x0a7b:
        r0 = "joinchat/";
        r7 = r4.replace(r0, r2);
        r0 = r7;
        r1 = r8;
        goto L_0x0CLASSNAME;
    L_0x0a85:
        r5 = "addstickers/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0a98;
    L_0x0a8d:
        r0 = "addstickers/";
        r7 = r4.replace(r0, r2);
        r1 = r7;
        r0 = r8;
        r2 = r0;
        goto L_0x0CLASSNAME;
    L_0x0a98:
        r5 = "msg/";
        r5 = r4.startsWith(r5);
        if (r5 != 0) goto L_0x0bd2;
    L_0x0aa0:
        r5 = "share/";
        r5 = r4.startsWith(r5);
        if (r5 == 0) goto L_0x0aaa;
    L_0x0aa8:
        goto L_0x0bd2;
    L_0x0aaa:
        r1 = "confirmphone";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0ac9;
    L_0x0ab2:
        r1 = "phone";
        r7 = r0.getQueryParameter(r1);
        r0 = r0.getQueryParameter(r3);
        r13 = r0;
        r6 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r7 = r5;
        r9 = r7;
        r18 = r9;
        goto L_0x0CLASSNAME;
    L_0x0ac9:
        r1 = "setlanguage/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0ae6;
    L_0x0ad1:
        r0 = 12;
        r7 = r4.substring(r0);
        r18 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r19 = r13;
        goto L_0x0c5b;
    L_0x0ae6:
        r1 = "addtheme/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0b05;
    L_0x0aee:
        r0 = 9;
        r7 = r4.substring(r0);
        r19 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
        r23 = r18;
        goto L_0x0c5d;
    L_0x0b05:
        r1 = "c/";
        r1 = r4.startsWith(r1);
        if (r1 == 0) goto L_0x0b5a;
    L_0x0b0d:
        r0 = r0.getPathSegments();
        r1 = r0.size();
        r2 = 3;
        if (r1 != r2) goto L_0x0b41;
    L_0x0b18:
        r1 = 1;
        r2 = r0.get(r1);
        r2 = (java.lang.CharSequence) r2;
        r7 = org.telegram.messenger.Utilities.parseInt(r2);
        r1 = 2;
        r0 = r0.get(r1);
        r0 = (java.lang.CharSequence) r0;
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r1 = r0.intValue();
        if (r1 == 0) goto L_0x0b41;
    L_0x0b34:
        r1 = r7.intValue();
        if (r1 != 0) goto L_0x0b3b;
    L_0x0b3a:
        goto L_0x0b41;
    L_0x0b3b:
        r44 = r7;
        r7 = r0;
        r0 = r44;
        goto L_0x0b43;
    L_0x0b41:
        r0 = r8;
        r7 = r0;
    L_0x0b43:
        r26 = r0;
        r25 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
        r19 = r18;
        r23 = r19;
        r24 = r23;
        goto L_0x0CLASSNAME;
    L_0x0b5a:
        r1 = r4.length();
        r2 = 1;
        if (r1 < r2) goto L_0x0c4e;
    L_0x0b61:
        r1 = new java.util.ArrayList;
        r2 = r0.getPathSegments();
        r1.<init>(r2);
        r2 = r1.size();
        if (r2 <= 0) goto L_0x0b83;
    L_0x0b70:
        r2 = 0;
        r4 = r1.get(r2);
        r4 = (java.lang.String) r4;
        r5 = "s";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0b84;
    L_0x0b7f:
        r1.remove(r2);
        goto L_0x0b84;
    L_0x0b83:
        r2 = 0;
    L_0x0b84:
        r4 = r1.size();
        if (r4 <= 0) goto L_0x0baa;
    L_0x0b8a:
        r4 = r1.get(r2);
        r7 = r4;
        r7 = (java.lang.String) r7;
        r2 = r1.size();
        r4 = 1;
        if (r2 <= r4) goto L_0x0ba8;
    L_0x0b98:
        r1 = r1.get(r4);
        r1 = (java.lang.CharSequence) r1;
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r2 = r1.intValue();
        if (r2 != 0) goto L_0x0bac;
    L_0x0ba8:
        r1 = r8;
        goto L_0x0bac;
    L_0x0baa:
        r1 = r8;
        r7 = r1;
    L_0x0bac:
        r2 = "start";
        r2 = r0.getQueryParameter(r2);
        r4 = "startgroup";
        r4 = r0.getQueryParameter(r4);
        r5 = "game";
        r0 = r0.getQueryParameter(r5);
        r9 = r0;
        r25 = r1;
        r0 = r8;
        r1 = r0;
        r5 = r1;
        r6 = r5;
        r13 = r6;
        r18 = r13;
        r19 = r18;
        r23 = r19;
        r24 = r23;
        r26 = r24;
        goto L_0x0CLASSNAME;
    L_0x0bd2:
        r4 = "url";
        r4 = r0.getQueryParameter(r4);
        if (r4 != 0) goto L_0x0bdb;
    L_0x0bda:
        goto L_0x0bdc;
    L_0x0bdb:
        r2 = r4;
    L_0x0bdc:
        r4 = "text";
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0be4:
        r4 = r2.length();
        if (r4 <= 0) goto L_0x0bfb;
    L_0x0bea:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r1);
        r2 = r4.toString();
        r12 = 1;
        goto L_0x0bfc;
    L_0x0bfb:
        r12 = 0;
    L_0x0bfc:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r2 = "text";
        r0 = r0.getQueryParameter(r2);
        r4.append(r0);
        r2 = r4.toString();
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r12 = 0;
    L_0x0CLASSNAME:
        r0 = r2.length();
        r4 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r0 <= r4) goto L_0x0CLASSNAME;
    L_0x0c1b:
        r0 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r4 = 0;
        r0 = r2.substring(r4, r0);
        r7 = r0;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = 0;
        r7 = r2;
    L_0x0CLASSNAME:
        r0 = r7.endsWith(r1);
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0c2c:
        r0 = r7.length();
        r2 = 1;
        r0 = r0 - r2;
        r7 = r7.substring(r4, r0);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = r7;
        r0 = r8;
        r1 = r0;
        r2 = r1;
        r4 = r2;
        r6 = r4;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
        r19 = r18;
        r23 = r19;
        r24 = r23;
        r25 = r24;
        r26 = r25;
        goto L_0x0CLASSNAME;
    L_0x0c4d:
        r8 = 0;
    L_0x0c4e:
        r0 = r8;
        r1 = r0;
    L_0x0CLASSNAME:
        r2 = r1;
    L_0x0CLASSNAME:
        r4 = r2;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r9 = r7;
        r13 = r9;
        r18 = r13;
    L_0x0CLASSNAME:
        r19 = r18;
    L_0x0c5b:
        r23 = r19;
    L_0x0c5d:
        r24 = r23;
    L_0x0c5f:
        r25 = r24;
    L_0x0CLASSNAME:
        r26 = r25;
    L_0x0CLASSNAME:
        r12 = 0;
    L_0x0CLASSNAME:
        r30 = r2;
        r31 = r4;
        r32 = r8;
        r28 = r19;
        r29 = r25;
        r27 = r26;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r4 = r0;
        r0 = r5;
        r25 = r18;
        r18 = r23;
        r26 = r24;
        r5 = r1;
        r23 = r32;
        r24 = r23;
        r44 = r12;
        r12 = r9;
        r9 = r44;
        goto L_0x0cae;
    L_0x0CLASSNAME:
        r8 = r7;
    L_0x0c8a:
        r0 = r8;
        r4 = r0;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r12 = r7;
        r13 = r12;
        r18 = r13;
        r23 = r18;
        r24 = r23;
        r25 = r24;
        r26 = r25;
        r27 = r26;
        r28 = r27;
        r29 = r28;
        r30 = r29;
        r31 = r30;
        r32 = r31;
        r9 = 0;
    L_0x0ca8:
        r33 = 0;
        r34 = 0;
        r35 = 0;
    L_0x0cae:
        if (r18 != 0) goto L_0x0cc4;
    L_0x0cb0:
        r1 = r15.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.isClientActivated();
        if (r1 == 0) goto L_0x0cbd;
    L_0x0cbc:
        goto L_0x0cc4;
    L_0x0cbd:
        r42 = r11;
        r2 = r15;
        r30 = 0;
        goto L_0x0dd7;
    L_0x0cc4:
        if (r6 != 0) goto L_0x0dbc;
    L_0x0cc6:
        if (r13 == 0) goto L_0x0cca;
    L_0x0cc8:
        goto L_0x0dbc;
    L_0x0cca:
        if (r7 != 0) goto L_0x0d75;
    L_0x0ccc:
        if (r4 != 0) goto L_0x0d75;
    L_0x0cce:
        if (r5 != 0) goto L_0x0d75;
    L_0x0cd0:
        if (r0 != 0) goto L_0x0d75;
    L_0x0cd2:
        if (r12 != 0) goto L_0x0d75;
    L_0x0cd4:
        if (r23 != 0) goto L_0x0d75;
    L_0x0cd6:
        if (r24 != 0) goto L_0x0d75;
    L_0x0cd8:
        if (r25 != 0) goto L_0x0d75;
    L_0x0cda:
        if (r18 != 0) goto L_0x0d75;
    L_0x0cdc:
        if (r26 != 0) goto L_0x0d75;
    L_0x0cde:
        if (r27 != 0) goto L_0x0d75;
    L_0x0ce0:
        if (r28 == 0) goto L_0x0ce4;
    L_0x0ce2:
        goto L_0x0d75;
    L_0x0ce4:
        r36 = r45.getContentResolver();	 Catch:{ Exception -> 0x0d64 }
        r37 = r46.getData();	 Catch:{ Exception -> 0x0d64 }
        r38 = 0;
        r39 = 0;
        r40 = 0;
        r41 = 0;
        r1 = r36.query(r37, r38, r39, r40, r41);	 Catch:{ Exception -> 0x0d64 }
        if (r1 == 0) goto L_0x0d5a;
    L_0x0cfa:
        r0 = r1.moveToFirst();	 Catch:{ all -> 0x0d4e }
        if (r0 == 0) goto L_0x0d5a;
    L_0x0d00:
        r0 = "account_name";
        r0 = r1.getColumnIndex(r0);	 Catch:{ all -> 0x0d4e }
        r0 = r1.getString(r0);	 Catch:{ all -> 0x0d4e }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ all -> 0x0d4e }
        r0 = r0.intValue();	 Catch:{ all -> 0x0d4e }
        r2 = 0;
        r6 = 3;
    L_0x0d14:
        if (r2 >= r6) goto L_0x0d30;
    L_0x0d16:
        r3 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ all -> 0x0d2e }
        r3 = r3.getClientUserId();	 Catch:{ all -> 0x0d2e }
        if (r3 != r0) goto L_0x0d2a;
    L_0x0d20:
        r3 = 0;
        r11[r3] = r2;	 Catch:{ all -> 0x0d2e }
        r0 = r11[r3];	 Catch:{ all -> 0x0d2e }
        r13 = 1;
        r15.switchToAccount(r0, r13);	 Catch:{ all -> 0x0d4c }
        goto L_0x0d31;
    L_0x0d2a:
        r13 = 1;
        r2 = r2 + 1;
        goto L_0x0d14;
    L_0x0d2e:
        r0 = move-exception;
        goto L_0x0d50;
    L_0x0d30:
        r13 = 1;
    L_0x0d31:
        r0 = "DATA4";
        r0 = r1.getColumnIndex(r0);	 Catch:{ all -> 0x0d4c }
        r0 = r1.getInt(r0);	 Catch:{ all -> 0x0d4c }
        r2 = 0;
        r3 = r11[r2];	 Catch:{ all -> 0x0d4c }
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);	 Catch:{ all -> 0x0d4c }
        r4 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ all -> 0x0d4c }
        r5 = new java.lang.Object[r2];	 Catch:{ all -> 0x0d4c }
        r3.postNotificationName(r4, r5);	 Catch:{ all -> 0x0d4c }
        r33 = r0;
        goto L_0x0d5c;
    L_0x0d4c:
        r0 = move-exception;
        goto L_0x0d51;
    L_0x0d4e:
        r0 = move-exception;
        r6 = 3;
    L_0x0d50:
        r13 = 1;
    L_0x0d51:
        throw r0;	 Catch:{ all -> 0x0d52 }
    L_0x0d52:
        r0 = move-exception;
        r2 = r0;
        if (r1 == 0) goto L_0x0d59;
    L_0x0d56:
        r1.close();	 Catch:{ all -> 0x0d59 }
    L_0x0d59:
        throw r2;	 Catch:{ Exception -> 0x0d62 }
    L_0x0d5a:
        r6 = 3;
        r13 = 1;
    L_0x0d5c:
        if (r1 == 0) goto L_0x0d6a;
    L_0x0d5e:
        r1.close();	 Catch:{ Exception -> 0x0d62 }
        goto L_0x0d6a;
    L_0x0d62:
        r0 = move-exception;
        goto L_0x0d67;
    L_0x0d64:
        r0 = move-exception;
        r6 = 3;
        r13 = 1;
    L_0x0d67:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0d6a:
        r42 = r11;
        r2 = r15;
        r7 = r32;
        r12 = r33;
        r30 = 0;
        goto L_0x0de7;
    L_0x0d75:
        r6 = 3;
        r13 = 1;
        if (r0 == 0) goto L_0x0d92;
    L_0x0d79:
        r1 = "@";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0d92;
    L_0x0d81:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = " ";
        r1.append(r2);
        r1.append(r0);
        r0 = r1.toString();
    L_0x0d92:
        r22 = 0;
        r2 = r11[r22];
        r19 = 0;
        r1 = r45;
        r3 = r7;
        r7 = 3;
        r6 = r30;
        r8 = 3;
        r7 = r31;
        r30 = 0;
        r8 = r0;
        r10 = r29;
        r42 = r11;
        r11 = r27;
        r13 = r23;
        r14 = r25;
        r15 = r24;
        r16 = r18;
        r17 = r26;
        r18 = r28;
        r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r2 = r45;
        goto L_0x0dd7;
    L_0x0dbc:
        r42 = r11;
        r30 = 0;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "phone";
        r0.putString(r1, r6);
        r0.putString(r3, r13);
        r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$3rXtwzTW6WkmVHi2jvIuUk6ur6Y;
        r2 = r45;
        r1.<init>(r2, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0dd7:
        r7 = r32;
        r12 = r33;
        goto L_0x0de7;
    L_0x0ddc:
        r42 = r11;
        r2 = r15;
        r30 = 0;
        r7 = 0;
        r12 = 0;
        r34 = 0;
        r35 = 0;
    L_0x0de7:
        r1 = r46;
        r0 = r34;
        r4 = r35;
        r6 = r42;
        r3 = 0;
        r5 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        goto L_0x0ed3;
    L_0x0df7:
        r42 = r11;
        r2 = r15;
        r30 = 0;
        r0 = r46.getAction();
        r1 = "org.telegram.messenger.OPEN_ACCOUNT";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0e16;
    L_0x0e08:
        r1 = r46;
        r6 = r42;
        r0 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        goto L_0x0ed1;
    L_0x0e16:
        r0 = r46.getAction();
        r1 = "new_dialog";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0e31;
    L_0x0e22:
        r1 = r46;
        r6 = r42;
        r0 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 1;
        goto L_0x0ed2;
    L_0x0e31:
        r0 = r46.getAction();
        r1 = "com.tmessages.openchat";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x0e9a;
    L_0x0e3d:
        r0 = "chatId";
        r1 = r46;
        r3 = 0;
        r0 = r1.getIntExtra(r0, r3);
        r4 = "userId";
        r4 = r1.getIntExtra(r4, r3);
        r5 = "encId";
        r5 = r1.getIntExtra(r5, r3);
        if (r0 == 0) goto L_0x0e67;
    L_0x0e54:
        r6 = r42;
        r4 = r6[r3];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r4.postNotificationName(r5, r7);
        r4 = 0;
    L_0x0e64:
        r5 = 0;
    L_0x0e65:
        r12 = 0;
        goto L_0x0e90;
    L_0x0e67:
        r6 = r42;
        if (r4 == 0) goto L_0x0e7a;
    L_0x0e6b:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r0.postNotificationName(r5, r7);
        r0 = 0;
        goto L_0x0e64;
    L_0x0e7a:
        if (r5 == 0) goto L_0x0e8c;
    L_0x0e7c:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r0.postNotificationName(r4, r7);
        r0 = 0;
        r4 = 0;
        goto L_0x0e65;
    L_0x0e8c:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r12 = 1;
    L_0x0e90:
        r43 = r12;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r12 = r4;
        r4 = 0;
        goto L_0x0ed5;
    L_0x0e9a:
        r1 = r46;
        r6 = r42;
        r3 = 0;
        r0 = r46.getAction();
        r4 = "com.tmessages.openplayer";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x0eb1;
    L_0x0eab:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 1;
        goto L_0x0ecf;
    L_0x0eb1:
        r0 = r46.getAction();
        r4 = "org.tmessages.openlocations";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x0eca;
    L_0x0ebd:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 1;
        goto L_0x0ed0;
    L_0x0ec4:
        r30 = r8;
        r6 = r11;
        r1 = r14;
        r2 = r15;
        r3 = 0;
    L_0x0eca:
        r0 = 0;
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
    L_0x0ecf:
        r9 = 0;
    L_0x0ed0:
        r10 = 0;
    L_0x0ed1:
        r11 = 0;
    L_0x0ed2:
        r12 = 0;
    L_0x0ed3:
        r43 = 0;
    L_0x0ed5:
        r13 = r2.currentAccount;
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);
        r13 = r13.isClientActivated();
        if (r13 == 0) goto L_0x11ff;
    L_0x0ee1:
        if (r12 == 0) goto L_0x0f2d;
    L_0x0ee3:
        r0 = new android.os.Bundle;
        r0.<init>();
        r5 = "user_id";
        r0.putInt(r5, r12);
        if (r4 == 0) goto L_0x0ef4;
    L_0x0eef:
        r5 = "message_id";
        r0.putInt(r5, r4);
    L_0x0ef4:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0var_;
    L_0x0efc:
        r4 = r6[r3];
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r5 = mainFragmentsStack;
        r6 = r5.size();
        r12 = 1;
        r6 = r6 - r12;
        r5 = r5.get(r6);
        r5 = (org.telegram.ui.ActionBar.BaseFragment) r5;
        r4 = r4.checkCanOpenChat(r0, r5);
        if (r4 == 0) goto L_0x0f2b;
    L_0x0var_:
        goto L_0x0var_;
    L_0x0var_:
        r12 = 1;
    L_0x0var_:
        r6 = new org.telegram.ui.ChatActivity;
        r6.<init>(r0);
        r5 = r2.actionBarLayout;
        r7 = 0;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r0 = r5.presentFragment(r6, r7, r8, r9, r10);
        if (r0 == 0) goto L_0x0f2b;
    L_0x0var_:
        r13 = 1;
        goto L_0x0f8f;
    L_0x0f2b:
        r13 = 0;
        goto L_0x0f8f;
    L_0x0f2d:
        r12 = 1;
        if (r0 == 0) goto L_0x0var_;
    L_0x0var_:
        r5 = new android.os.Bundle;
        r5.<init>();
        r7 = "chat_id";
        r5.putInt(r7, r0);
        if (r4 == 0) goto L_0x0var_;
    L_0x0f3c:
        r0 = "message_id";
        r5.putInt(r0, r4);
    L_0x0var_:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0var_;
    L_0x0var_:
        r0 = r6[r3];
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r4 = mainFragmentsStack;
        r6 = r4.size();
        r6 = r6 - r12;
        r4 = r4.get(r6);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r0.checkCanOpenChat(r5, r4);
        if (r0 == 0) goto L_0x0f2b;
    L_0x0var_:
        r7 = new org.telegram.ui.ChatActivity;
        r7.<init>(r5);
        r6 = r2.actionBarLayout;
        r8 = 0;
        r9 = 1;
        r10 = 1;
        r11 = 0;
        r0 = r6.presentFragment(r7, r8, r9, r10, r11);
        if (r0 == 0) goto L_0x0f2b;
    L_0x0var_:
        goto L_0x0var_;
    L_0x0var_:
        if (r5 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "enc_id";
        r0.putInt(r4, r5);
        r7 = new org.telegram.ui.ChatActivity;
        r7.<init>(r0);
        r6 = r2.actionBarLayout;
        r8 = 0;
        r9 = 1;
        r10 = 1;
        r11 = 0;
        r13 = r6.presentFragment(r7, r8, r9, r10, r11);
    L_0x0f8f:
        r0 = r47;
        r4 = 0;
        goto L_0x1204;
    L_0x0var_:
        if (r43 == 0) goto L_0x0fce;
    L_0x0var_:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 != 0) goto L_0x0fa2;
    L_0x0f9c:
        r0 = r2.actionBarLayout;
        r0.removeAllFragments();
        goto L_0x0fca;
    L_0x0fa2:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0fca;
    L_0x0fac:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        r0 = r0 - r12;
        if (r0 <= 0) goto L_0x0fc5;
    L_0x0fb7:
        r0 = r2.layersActionBarLayout;
        r4 = r0.fragmentsStack;
        r4 = r4.get(r3);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0.removeFragmentFromStack(r4);
        goto L_0x0fac;
    L_0x0fc5:
        r0 = r2.layersActionBarLayout;
        r0.closeLastFragment(r3);
    L_0x0fca:
        r0 = 0;
    L_0x0fcb:
        r4 = 0;
        goto L_0x1203;
    L_0x0fce:
        if (r7 == 0) goto L_0x0fff;
    L_0x0fd0:
        r0 = r2.getCurrentWalletFragment(r7);
        if (r0 == 0) goto L_0x0fde;
    L_0x0fd6:
        r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$5GybZC1APjVUWmzZAupg-DSdEx8;
        r4.<init>(r2, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x0fde:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0ff4;
    L_0x0fe4:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x0ff9;
    L_0x0ff4:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
    L_0x0ff9:
        r0 = r47;
        r4 = 0;
        r13 = 1;
        goto L_0x1204;
    L_0x0fff:
        if (r8 == 0) goto L_0x1020;
    L_0x1001:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x101d;
    L_0x100b:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r3);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r4 = new org.telegram.ui.Components.AudioPlayerAlert;
        r4.<init>(r2);
        r0.showDialog(r4);
    L_0x101d:
        r0 = r47;
        goto L_0x0fcb;
    L_0x1020:
        if (r9 == 0) goto L_0x1044;
    L_0x1022:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x101d;
    L_0x102c:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.get(r3);
        r0 = (org.telegram.ui.ActionBar.BaseFragment) r0;
        r4 = new org.telegram.ui.Components.SharingLocationsAlert;
        r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI;
        r5.<init>(r2, r6);
        r4.<init>(r2, r5);
        r0.showDialog(r4);
        goto L_0x101d;
    L_0x1044:
        r0 = r2.videoPath;
        if (r0 != 0) goto L_0x10c9;
    L_0x1048:
        r0 = r2.photoPathsArray;
        if (r0 != 0) goto L_0x10c9;
    L_0x104c:
        r0 = r2.sendingText;
        if (r0 != 0) goto L_0x10c9;
    L_0x1050:
        r0 = r2.documentsPathsArray;
        if (r0 != 0) goto L_0x10c9;
    L_0x1054:
        r0 = r2.contactsToSend;
        if (r0 != 0) goto L_0x10c9;
    L_0x1058:
        r0 = r2.documentsUrisArray;
        if (r0 == 0) goto L_0x105d;
    L_0x105c:
        goto L_0x10c9;
    L_0x105d:
        if (r10 == 0) goto L_0x108e;
    L_0x105f:
        r13 = r2.actionBarLayout;
        r14 = new org.telegram.ui.SettingsActivity;
        r14.<init>();
        r15 = 0;
        r16 = 1;
        r17 = 1;
        r18 = 0;
        r13.presentFragment(r14, r15, r16, r17, r18);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1087;
    L_0x1076:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x0ff9;
    L_0x1087:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
        goto L_0x0ff9;
    L_0x108e:
        if (r11 == 0) goto L_0x10c6;
    L_0x1090:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "destroyAfterSelect";
        r0.putBoolean(r4, r12);
        r5 = r2.actionBarLayout;
        r6 = new org.telegram.ui.ContactsActivity;
        r6.<init>(r0);
        r7 = 0;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r5.presentFragment(r6, r7, r8, r9, r10);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x10bf;
    L_0x10ae:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x0ff9;
    L_0x10bf:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
        goto L_0x0ff9;
    L_0x10c6:
        r4 = 0;
        goto L_0x1201;
    L_0x10c9:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 != 0) goto L_0x10dc;
    L_0x10cf:
        r0 = r6[r3];
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r5 = new java.lang.Object[r3];
        r0.postNotificationName(r4, r5);
    L_0x10dc:
        r0 = (r20 > r30 ? 1 : (r20 == r30 ? 0 : -1));
        if (r0 != 0) goto L_0x11ee;
    L_0x10e0:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "onlySelect";
        r0.putBoolean(r4, r12);
        r4 = "dialogsType";
        r5 = 3;
        r0.putInt(r4, r5);
        r4 = "allowSwitchAccount";
        r0.putBoolean(r4, r12);
        r4 = r2.contactsToSend;
        if (r4 == 0) goto L_0x111c;
    L_0x10f9:
        r4 = r4.size();
        if (r4 == r12) goto L_0x1138;
    L_0x10ff:
        r4 = NUM; // 0x7f0e09c1 float:1.8880102E38 double:1.0531633903E-314;
        r5 = "SendContactToText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertString";
        r0.putString(r5, r4);
        r4 = NUM; // 0x7f0e09aa float:1.8880055E38 double:1.053163379E-314;
        r5 = "SendContactToGroupText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertStringGroup";
        r0.putString(r5, r4);
        goto L_0x1138;
    L_0x111c:
        r4 = NUM; // 0x7f0e09c1 float:1.8880102E38 double:1.0531633903E-314;
        r5 = "SendMessagesToText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertString";
        r0.putString(r5, r4);
        r4 = NUM; // 0x7f0e09c0 float:1.88801E38 double:1.05316339E-314;
        r5 = "SendMessagesToGroupText";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "selectAlertStringGroup";
        r0.putString(r5, r4);
    L_0x1138:
        r7 = new org.telegram.ui.DialogsActivity;
        r7.<init>(r0);
        r7.setDelegate(r2);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1162;
    L_0x1146:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= 0) goto L_0x117f;
    L_0x1150:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r4 = r0.size();
        r4 = r4 - r12;
        r0 = r0.get(r4);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x117f;
    L_0x1161:
        goto L_0x117d;
    L_0x1162:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= r12) goto L_0x117f;
    L_0x116c:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r4 = r0.size();
        r4 = r4 - r12;
        r0 = r0.get(r4);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x117f;
    L_0x117d:
        r0 = 1;
        goto L_0x1180;
    L_0x117f:
        r0 = 0;
    L_0x1180:
        r8 = r0;
        r6 = r2.actionBarLayout;
        r9 = 1;
        r10 = 1;
        r11 = 0;
        r6.presentFragment(r7, r8, r9, r10, r11);
        r0 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r0 == 0) goto L_0x11a1;
    L_0x118f:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x11a1;
    L_0x1199:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0.closePhoto(r3, r3);
        goto L_0x11d0;
    L_0x11a1:
        r0 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r0 == 0) goto L_0x11b9;
    L_0x11a7:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x11b9;
    L_0x11b1:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0.closePhoto(r3, r12);
        goto L_0x11d0;
    L_0x11b9:
        r0 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r0 == 0) goto L_0x11d0;
    L_0x11bf:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x11d0;
    L_0x11c9:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0.close(r3, r12);
    L_0x11d0:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x11e7;
    L_0x11db:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
        goto L_0x0ff9;
    L_0x11e7:
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
        goto L_0x0ff9;
    L_0x11ee:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4 = java.lang.Long.valueOf(r20);
        r0.add(r4);
        r4 = 0;
        r2.didSelectDialogs(r4, r0, r4, r3);
        goto L_0x1201;
    L_0x11ff:
        r4 = 0;
        r12 = 1;
    L_0x1201:
        r0 = r47;
    L_0x1203:
        r13 = 0;
    L_0x1204:
        if (r13 != 0) goto L_0x12a2;
    L_0x1206:
        if (r0 != 0) goto L_0x12a2;
    L_0x1208:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1253;
    L_0x120e:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x1234;
    L_0x121a:
        r0 = r2.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x128d;
    L_0x1224:
        r0 = r2.layersActionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r0.addFragmentToStack(r5);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x128d;
    L_0x1234:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x128d;
    L_0x123e:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r5 = r2.sideMenu;
        r0.setSideMenu(r5);
        r5 = r2.actionBarLayout;
        r5.addFragmentToStack(r0);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
        goto L_0x128d;
    L_0x1253:
        r0 = r2.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x128d;
    L_0x125d:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        if (r0 != 0) goto L_0x1279;
    L_0x1269:
        r0 = r2.actionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r0.addFragmentToStack(r5);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r3);
        goto L_0x128d;
    L_0x1279:
        r0 = new org.telegram.ui.DialogsActivity;
        r0.<init>(r4);
        r5 = r2.sideMenu;
        r0.setSideMenu(r5);
        r5 = r2.actionBarLayout;
        r5.addFragmentToStack(r0);
        r0 = r2.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r12, r3);
    L_0x128d:
        r0 = r2.actionBarLayout;
        r0.showLastFragment();
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x12a2;
    L_0x1298:
        r0 = r2.layersActionBarLayout;
        r0.showLastFragment();
        r0 = r2.rightActionBarLayout;
        r0.showLastFragment();
    L_0x12a2:
        r1.setAction(r4);
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$28$LaunchActivity(new CancelAccountDeletionActivity(bundle));
    }

    public /* synthetic */ void lambda$handleIntent$9$LaunchActivity(int[] iArr, SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new -$$Lambda$LaunchActivity$XTKeVrtgIlPSrnJHpJxLVivhpCY(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$28$LaunchActivity(locationActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x02f3  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x02f3  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0249  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x02f3  */
    private void runLinkRequest(int r21, java.lang.String r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, boolean r28, java.lang.Integer r29, java.lang.Integer r30, java.lang.String r31, java.util.HashMap<java.lang.String, java.lang.String> r32, java.lang.String r33, java.lang.String r34, java.lang.String r35, org.telegram.tgnet.TLRPC.TL_wallPaper r36, java.lang.String r37, int r38) {
        /*
        r20 = this;
        r15 = r20;
        r9 = r21;
        r0 = r22;
        r5 = r23;
        r6 = r24;
        r10 = r27;
        r14 = r32;
        r13 = r33;
        r12 = r34;
        r11 = r36;
        r8 = r37;
        r1 = r38;
        r2 = 2;
        if (r1 != 0) goto L_0x005a;
    L_0x001b:
        r3 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();
        if (r3 < r2) goto L_0x005a;
    L_0x0021:
        if (r14 == 0) goto L_0x005a;
    L_0x0023:
        r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$fmRNQ3kPgQUWCBh5uJJH0wIuPtI;
        r1 = r7;
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r0 = r7;
        r7 = r25;
        r9 = r8;
        r8 = r26;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r15 = r13;
        r13 = r31;
        r14 = r32;
        r15 = r33;
        r16 = r34;
        r17 = r35;
        r18 = r36;
        r19 = r37;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r11 = r20;
        r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r11, r0);
        r0.show();
        return;
    L_0x005a:
        r11 = r15;
        r15 = r13;
        r12 = 0;
        r3 = 1;
        r13 = 0;
        if (r35 == 0) goto L_0x00b0;
    L_0x0061:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
        r0 = r0.hasObservers(r1);
        if (r0 == 0) goto L_0x007b;
    L_0x006d:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
        r2 = new java.lang.Object[r3];
        r2[r13] = r35;
        r0.postNotificationName(r1, r2);
        goto L_0x00af;
    L_0x007b:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0.<init>(r11);
        r1 = NUM; // 0x7f0e00f4 float:1.8875532E38 double:1.053162277E-314;
        r2 = "AppName";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0753 float:1.887884E38 double:1.053163083E-314;
        r2 = new java.lang.Object[r3];
        r2[r13] = r35;
        r3 = "OtherLoginCode";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r2 = "OK";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setPositiveButton(r1, r12);
        r11.showAlertDialog(r0);
    L_0x00af:
        return;
    L_0x00b0:
        r8 = new org.telegram.ui.ActionBar.AlertDialog;
        r4 = 3;
        r8.<init>(r11, r4);
        r7 = new int[r3];
        r7[r13] = r13;
        if (r0 == 0) goto L_0x00e6;
    L_0x00bc:
        r10 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
        r10.<init>();
        r10.username = r0;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r21);
        r14 = new org.telegram.ui.-$$Lambda$LaunchActivity$kUJEZrSfN2QNY91cez_kcbLVw5M;
        r1 = r14;
        r2 = r20;
        r3 = r8;
        r4 = r31;
        r5 = r21;
        r6 = r26;
        r15 = r7;
        r7 = r25;
        r12 = r8;
        r8 = r29;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
        r0 = r0.sendRequest(r10, r14);
        r15[r13] = r0;
        r4 = r15;
    L_0x00e3:
        r5 = 0;
        goto L_0x02ee;
    L_0x00e6:
        r4 = r7;
        r12 = r8;
        if (r5 == 0) goto L_0x0119;
    L_0x00ea:
        if (r1 != 0) goto L_0x0103;
    L_0x00ec:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite;
        r0.<init>();
        r0.hash = r5;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r21);
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$m0WdBFNUrZDToj2GiVi4czfOzdI;
        r3.<init>(r11, r12, r9, r5);
        r0 = r1.sendRequest(r0, r3, r2);
        r4[r13] = r0;
        goto L_0x00e3;
    L_0x0103:
        if (r1 != r3) goto L_0x00e3;
    L_0x0105:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
        r0.<init>();
        r0.hash = r5;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r21);
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$Old86aqSncIYPaLNIiGmfv6gGu4;
        r3.<init>(r11, r9, r12);
        r1.sendRequest(r0, r3, r2);
        goto L_0x00e3;
    L_0x0119:
        if (r6 == 0) goto L_0x014e;
    L_0x011b:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x014d;
    L_0x0123:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
        r0.<init>();
        r0.short_name = r6;
        r1 = mainFragmentsStack;
        r2 = r1.size();
        r2 = r2 - r3;
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        r2 = new org.telegram.ui.Components.StickersAlert;
        r3 = 0;
        r4 = 0;
        r21 = r2;
        r22 = r20;
        r23 = r1;
        r24 = r0;
        r25 = r3;
        r26 = r4;
        r21.<init>(r22, r23, r24, r25, r26);
        r1.showDialog(r2);
    L_0x014d:
        return;
    L_0x014e:
        if (r10 == 0) goto L_0x016e;
    L_0x0150:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "onlySelect";
        r0.putBoolean(r1, r3);
        r1 = new org.telegram.ui.DialogsActivity;
        r1.<init>(r0);
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$a1twPvKSuJ3MEhIZrCHxkC1QlZ0;
        r2 = r28;
        r0.<init>(r11, r2, r9, r10);
        r1.setDelegate(r0);
        r11.presentFragment(r1, r13, r3);
        goto L_0x00e3;
    L_0x016e:
        if (r14 == 0) goto L_0x01d9;
    L_0x0170:
        r0 = "bot_id";
        r0 = r14.get(r0);
        r0 = (java.lang.CharSequence) r0;
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r0 = r0.intValue();
        if (r0 != 0) goto L_0x0183;
    L_0x0182:
        return;
    L_0x0183:
        r1 = "payload";
        r1 = r14.get(r1);
        r1 = (java.lang.String) r1;
        r2 = "nonce";
        r2 = r14.get(r2);
        r2 = (java.lang.String) r2;
        r3 = "callback_url";
        r3 = r14.get(r3);
        r3 = (java.lang.String) r3;
        r5 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
        r5.<init>();
        r5.bot_id = r0;
        r0 = "scope";
        r0 = r14.get(r0);
        r0 = (java.lang.String) r0;
        r5.scope = r0;
        r0 = "public_key";
        r0 = r14.get(r0);
        r0 = (java.lang.String) r0;
        r5.public_key = r0;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r21);
        r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$PIbOoHPAyrdyJmc5_Z8re04-IWk;
        r22 = r6;
        r23 = r20;
        r24 = r4;
        r25 = r21;
        r26 = r12;
        r27 = r5;
        r28 = r1;
        r29 = r2;
        r30 = r3;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29, r30);
        r0 = r0.sendRequest(r5, r6);
        r4[r13] = r0;
        goto L_0x00e3;
    L_0x01d9:
        r0 = r34;
        if (r0 == 0) goto L_0x01f7;
    L_0x01dd:
        r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo;
        r1.<init>();
        r1.path = r0;
        r0 = r11.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$TqVbW4kW2AUPqQw3HQquT_BmH-0;
        r2.<init>(r11, r12);
        r0 = r0.sendRequest(r1, r2);
        r4[r13] = r0;
        goto L_0x00e3;
    L_0x01f7:
        r0 = "android";
        if (r15 == 0) goto L_0x0217;
    L_0x01fb:
        r1 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage;
        r1.<init>();
        r1.lang_code = r15;
        r1.lang_pack = r0;
        r0 = r11.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$zcSIPeMix4HG2uFGcss6lWwGsz4;
        r2.<init>(r11, r12);
        r0 = r0.sendRequest(r1, r2);
        r4[r13] = r0;
        goto L_0x00e3;
    L_0x0217:
        r1 = r36;
        if (r1 == 0) goto L_0x026c;
    L_0x021b:
        r0 = r1.slug;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0245;
    L_0x0223:
        r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper;	 Catch:{ Exception -> 0x023f }
        r5 = -100;
        r2 = r1.settings;	 Catch:{ Exception -> 0x023f }
        r2 = r2.background_color;	 Catch:{ Exception -> 0x023f }
        r0.<init>(r5, r2);	 Catch:{ Exception -> 0x023f }
        r2 = new org.telegram.ui.WallpaperActivity;	 Catch:{ Exception -> 0x023f }
        r5 = 0;
        r2.<init>(r0, r5);	 Catch:{ Exception -> 0x023d }
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$no7_WPwP24B0S8EG00FNHRcu78Y;	 Catch:{ Exception -> 0x023d }
        r0.<init>(r11, r2);	 Catch:{ Exception -> 0x023d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x023d }
        goto L_0x0247;
    L_0x023d:
        r0 = move-exception;
        goto L_0x0241;
    L_0x023f:
        r0 = move-exception;
        r5 = 0;
    L_0x0241:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0246;
    L_0x0245:
        r5 = 0;
    L_0x0246:
        r3 = 0;
    L_0x0247:
        if (r3 != 0) goto L_0x02ee;
    L_0x0249:
        r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
        r2.<init>();
        r3 = r1.slug;
        r2.slug = r3;
        r0.wallpaper = r2;
        r2 = r11.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$0LjwkVrhcZ--K7SwMYwX9zvr4IE;
        r3.<init>(r11, r12, r1);
        r0 = r2.sendRequest(r0, r3);
        r4[r13] = r0;
        goto L_0x02ee;
    L_0x026c:
        r1 = r37;
        r5 = 0;
        if (r1 == 0) goto L_0x0298;
    L_0x0271:
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$kNOzJe-LzOO42Nkfi3zQViNkkMw;
        r2.<init>(r11);
        r3 = new org.telegram.tgnet.TLRPC$TL_account_getTheme;
        r3.<init>();
        r3.format = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug;
        r0.<init>();
        r0.slug = r1;
        r3.theme = r0;
        r0 = r11.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$ITuDb69UKW2Et5rLo0fFGnOwCWY;
        r1.<init>(r11, r12);
        r0 = r0.sendRequest(r3, r1);
        r4[r13] = r0;
        goto L_0x02ef;
    L_0x0298:
        if (r30 == 0) goto L_0x02ee;
    L_0x029a:
        if (r29 == 0) goto L_0x02ee;
    L_0x029c:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = r30.intValue();
        r2 = "chat_id";
        r0.putInt(r2, r1);
        r1 = r29.intValue();
        r2 = "message_id";
        r0.putInt(r2, r1);
        r1 = mainFragmentsStack;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x02c9;
    L_0x02bb:
        r1 = mainFragmentsStack;
        r2 = r1.size();
        r2 = r2 - r3;
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        goto L_0x02ca;
    L_0x02c9:
        r1 = r5;
    L_0x02ca:
        if (r1 == 0) goto L_0x02d6;
    L_0x02cc:
        r2 = org.telegram.messenger.MessagesController.getInstance(r21);
        r2 = r2.checkCanOpenChat(r0, r1);
        if (r2 == 0) goto L_0x02ee;
    L_0x02d6:
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$7cBKljKbDPuoX1h57OHeP_AO_K8;
        r22 = r2;
        r23 = r20;
        r24 = r0;
        r25 = r30;
        r26 = r4;
        r27 = r12;
        r28 = r1;
        r29 = r21;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x02ee:
        r2 = r5;
    L_0x02ef:
        r0 = r4[r13];
        if (r0 == 0) goto L_0x02fe;
    L_0x02f3:
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$zbp14CFOmL9gqRKJbYQvFvmHCzY;
        r0.<init>(r9, r4, r2);
        r12.setOnCancelListener(r0);
        r12.show();	 Catch:{ Exception -> 0x02fe }
    L_0x02fe:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$10$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, TL_wallPaper tL_wallPaper, String str11, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, str7, hashMap, str8, str9, str10, tL_wallPaper, str11, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$14$LaunchActivity(AlertDialog alertDialog, String str, int i, String str2, String str3, Integer num, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$XiLlrf3r558r6QvMDLPEm5IJWYQ(this, alertDialog, tLObject, tL_error, str, i, str2, str3, num));
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0139  */
    /* JADX WARNING: Missing block: B:21:0x00a9, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00ab;
     */
    /* JADX WARNING: Missing block: B:27:0x00c8, code skipped:
            if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00ab;
     */
    public /* synthetic */ void lambda$null$13$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r12, org.telegram.tgnet.TLObject r13, org.telegram.tgnet.TLRPC.TL_error r14, java.lang.String r15, int r16, java.lang.String r17, java.lang.String r18, java.lang.Integer r19) {
        /*
        r11 = this;
        r1 = r11;
        r2 = r15;
        r3 = r16;
        r4 = r17;
        r5 = r18;
        r0 = r11.isFinishing();
        if (r0 != 0) goto L_0x026c;
    L_0x000e:
        r12.dismiss();	 Catch:{ Exception -> 0x0012 }
        goto L_0x0017;
    L_0x0012:
        r0 = move-exception;
        r6 = r0;
        org.telegram.messenger.FileLog.e(r6);
    L_0x0017:
        r0 = r13;
        r0 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r0;
        r6 = 0;
        if (r14 != 0) goto L_0x0257;
    L_0x001d:
        r7 = r1.actionBarLayout;
        if (r7 == 0) goto L_0x0257;
    L_0x0021:
        if (r2 == 0) goto L_0x002d;
    L_0x0023:
        if (r2 == 0) goto L_0x0257;
    L_0x0025:
        r7 = r0.users;
        r7 = r7.isEmpty();
        if (r7 != 0) goto L_0x0257;
    L_0x002d:
        r7 = org.telegram.messenger.MessagesController.getInstance(r16);
        r8 = r0.users;
        r7.putUsers(r8, r6);
        r7 = org.telegram.messenger.MessagesController.getInstance(r16);
        r8 = r0.chats;
        r7.putChats(r8, r6);
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r16);
        r8 = r0.users;
        r9 = r0.chats;
        r10 = 1;
        r7.putUsersAndChats(r8, r9, r6, r10);
        r7 = "dialogsType";
        r8 = "onlySelect";
        if (r2 == 0) goto L_0x0140;
    L_0x0051:
        r4 = new android.os.Bundle;
        r4.<init>();
        r4.putBoolean(r8, r10);
        r5 = "cantSendToChannels";
        r4.putBoolean(r5, r10);
        r4.putInt(r7, r10);
        r5 = NUM; // 0x7f0e09b1 float:1.888007E38 double:1.0531633824E-314;
        r7 = "SendGameToText";
        r5 = org.telegram.messenger.LocaleController.getString(r7, r5);
        r7 = "selectAlertString";
        r4.putString(r7, r5);
        r5 = NUM; // 0x7f0e09b0 float:1.8880068E38 double:1.053163382E-314;
        r7 = "SendGameToGroupText";
        r5 = org.telegram.messenger.LocaleController.getString(r7, r5);
        r7 = "selectAlertStringGroup";
        r4.putString(r7, r5);
        r5 = new org.telegram.ui.DialogsActivity;
        r5.<init>(r4);
        r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$hX_eqKiqp4HUUAl2RiZrtdLyCNM;
        r4.<init>(r11, r15, r3, r0);
        r5.setDelegate(r4);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x00af;
    L_0x0090:
        r0 = r1.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= 0) goto L_0x00ad;
    L_0x009a:
        r0 = r1.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r2 = r0.size();
        r2 = r2 - r10;
        r0 = r0.get(r2);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x00ad;
    L_0x00ab:
        r0 = 1;
        goto L_0x00cb;
    L_0x00ad:
        r0 = 0;
        goto L_0x00cb;
    L_0x00af:
        r0 = r1.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 <= r10) goto L_0x00ad;
    L_0x00b9:
        r0 = r1.actionBarLayout;
        r0 = r0.fragmentsStack;
        r2 = r0.size();
        r2 = r2 - r10;
        r0 = r0.get(r2);
        r0 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r0 == 0) goto L_0x00ad;
    L_0x00ca:
        goto L_0x00ab;
    L_0x00cb:
        r2 = r1.actionBarLayout;
        r3 = 1;
        r4 = 1;
        r7 = 0;
        r12 = r2;
        r13 = r5;
        r14 = r0;
        r15 = r3;
        r16 = r4;
        r17 = r7;
        r12.presentFragment(r13, r14, r15, r16, r17);
        r0 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r0 == 0) goto L_0x00f3;
    L_0x00e1:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x00f3;
    L_0x00eb:
        r0 = org.telegram.ui.SecretMediaViewer.getInstance();
        r0.closePhoto(r6, r6);
        goto L_0x0122;
    L_0x00f3:
        r0 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r0 == 0) goto L_0x010b;
    L_0x00f9:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x010b;
    L_0x0103:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r0.closePhoto(r6, r10);
        goto L_0x0122;
    L_0x010b:
        r0 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r0 == 0) goto L_0x0122;
    L_0x0111:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0 = r0.isVisible();
        if (r0 == 0) goto L_0x0122;
    L_0x011b:
        r0 = org.telegram.ui.ArticleViewer.getInstance();
        r0.close(r6, r10);
    L_0x0122:
        r0 = r1.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r6, r6);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0139;
    L_0x012d:
        r0 = r1.actionBarLayout;
        r0.showLastFragment();
        r0 = r1.rightActionBarLayout;
        r0.showLastFragment();
        goto L_0x026c;
    L_0x0139:
        r0 = r1.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r10, r6);
        goto L_0x026c;
    L_0x0140:
        r2 = 0;
        if (r4 == 0) goto L_0x01ad;
    L_0x0143:
        r5 = r0.users;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x0154;
    L_0x014b:
        r0 = r0.users;
        r0 = r0.get(r6);
        r2 = r0;
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
    L_0x0154:
        if (r2 == 0) goto L_0x0197;
    L_0x0156:
        r0 = r2.bot;
        if (r0 == 0) goto L_0x015f;
    L_0x015a:
        r0 = r2.bot_nochats;
        if (r0 == 0) goto L_0x015f;
    L_0x015e:
        goto L_0x0197;
    L_0x015f:
        r0 = new android.os.Bundle;
        r0.<init>();
        r0.putBoolean(r8, r10);
        r5 = 2;
        r0.putInt(r7, r5);
        r7 = NUM; // 0x7f0e00ca float:1.8875447E38 double:1.0531622564E-314;
        r5 = new java.lang.Object[r5];
        r8 = org.telegram.messenger.UserObject.getUserName(r2);
        r5[r6] = r8;
        r6 = "%1$s";
        r5[r10] = r6;
        r6 = "AddToTheGroupAlertText";
        r5 = org.telegram.messenger.LocaleController.formatString(r6, r7, r5);
        r6 = "addToGroupAlertString";
        r0.putString(r6, r5);
        r5 = new org.telegram.ui.DialogsActivity;
        r5.<init>(r0);
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$wXZS3_Qr-g0caarKbRisJT_pmr8;
        r0.<init>(r11, r3, r2, r4);
        r5.setDelegate(r0);
        r11.lambda$runLinkRequest$28$LaunchActivity(r5);
        goto L_0x026c;
    L_0x0197:
        r0 = "BotCantJoinGroups";
        r2 = NUM; // 0x7f0e01d8 float:1.8875995E38 double:1.05316239E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x01a8 }
        r0 = android.widget.Toast.makeText(r11, r0, r6);	 Catch:{ Exception -> 0x01a8 }
        r0.show();	 Catch:{ Exception -> 0x01a8 }
        goto L_0x01ac;
    L_0x01a8:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01ac:
        return;
    L_0x01ad:
        r4 = new android.os.Bundle;
        r4.<init>();
        r7 = r0.chats;
        r7 = r7.isEmpty();
        if (r7 != 0) goto L_0x01d5;
    L_0x01ba:
        r7 = r0.chats;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.Chat) r7;
        r7 = r7.id;
        r8 = "chat_id";
        r4.putInt(r8, r7);
        r7 = r0.chats;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.Chat) r7;
        r7 = r7.id;
        r7 = -r7;
        goto L_0x01ee;
    L_0x01d5:
        r7 = r0.users;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r7 = r7.id;
        r8 = "user_id";
        r4.putInt(r8, r7);
        r7 = r0.users;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r7 = r7.id;
    L_0x01ee:
        r7 = (long) r7;
        if (r5 == 0) goto L_0x020b;
    L_0x01f1:
        r9 = r0.users;
        r9 = r9.size();
        if (r9 <= 0) goto L_0x020b;
    L_0x01f9:
        r0 = r0.users;
        r0 = r0.get(r6);
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r0 = r0.bot;
        if (r0 == 0) goto L_0x020b;
    L_0x0205:
        r0 = "botUser";
        r4.putString(r0, r5);
        r6 = 1;
    L_0x020b:
        if (r19 == 0) goto L_0x0216;
    L_0x020d:
        r0 = r19.intValue();
        r9 = "message_id";
        r4.putInt(r9, r0);
    L_0x0216:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x022c;
    L_0x021e:
        r0 = mainFragmentsStack;
        r2 = r0.size();
        r2 = r2 - r10;
        r0 = r0.get(r2);
        r2 = r0;
        r2 = (org.telegram.ui.ActionBar.BaseFragment) r2;
    L_0x022c:
        if (r2 == 0) goto L_0x0238;
    L_0x022e:
        r0 = org.telegram.messenger.MessagesController.getInstance(r16);
        r0 = r0.checkCanOpenChat(r4, r2);
        if (r0 == 0) goto L_0x026c;
    L_0x0238:
        if (r6 == 0) goto L_0x024c;
    L_0x023a:
        r0 = r2 instanceof org.telegram.ui.ChatActivity;
        if (r0 == 0) goto L_0x024c;
    L_0x023e:
        r2 = (org.telegram.ui.ChatActivity) r2;
        r9 = r2.getDialogId();
        r0 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
        if (r0 != 0) goto L_0x024c;
    L_0x0248:
        r2.setBotUser(r5);
        goto L_0x026c;
    L_0x024c:
        r0 = new org.telegram.ui.ChatActivity;
        r0.<init>(r4);
        r2 = r1.actionBarLayout;
        r2.presentFragment(r0);
        goto L_0x026c;
    L_0x0257:
        r0 = "NoUsernameFound";
        r2 = NUM; // 0x7f0e069d float:1.8878471E38 double:1.053162993E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x0268 }
        r0 = android.widget.Toast.makeText(r11, r0, r6);	 Catch:{ Exception -> 0x0268 }
        r0.show();	 Catch:{ Exception -> 0x0268 }
        goto L_0x026c;
    L_0x0268:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x026c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$13$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer):void");
    }

    public /* synthetic */ void lambda$null$11$LaunchActivity(String str, int i, TL_contacts_resolvedPeer tL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    public /* synthetic */ void lambda$null$12$LaunchActivity(int i, User user, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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

    public /* synthetic */ void lambda$runLinkRequest$16$LaunchActivity(AlertDialog alertDialog, int i, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$dyfx93R9rXog0WRQBoD6BTnIkNU(this, alertDialog, tL_error, tLObject, i, str));
    }

    /* JADX WARNING: Missing block: B:17:0x002e, code skipped:
            if (android.text.TextUtils.isEmpty(r10.username) == false) goto L_0x0030;
     */
    public /* synthetic */ void lambda$null$15$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r9, org.telegram.tgnet.TLRPC.TL_error r10, org.telegram.tgnet.TLObject r11, int r12, java.lang.String r13) {
        /*
        r8 = this;
        r0 = r8.isFinishing();
        if (r0 != 0) goto L_0x00eb;
    L_0x0006:
        r9.dismiss();	 Catch:{ Exception -> 0x000a }
        goto L_0x000e;
    L_0x000a:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x000e:
        r9 = 0;
        if (r10 != 0) goto L_0x00a8;
    L_0x0011:
        r0 = r8.actionBarLayout;
        if (r0 == 0) goto L_0x00a8;
    L_0x0015:
        r11 = (org.telegram.tgnet.TLRPC.ChatInvite) r11;
        r10 = r11.chat;
        r0 = 1;
        if (r10 == 0) goto L_0x0092;
    L_0x001c:
        r10 = org.telegram.messenger.ChatObject.isLeftFromChat(r10);
        if (r10 == 0) goto L_0x0030;
    L_0x0022:
        r10 = r11.chat;
        r1 = r10.kicked;
        if (r1 != 0) goto L_0x0092;
    L_0x0028:
        r10 = r10.username;
        r10 = android.text.TextUtils.isEmpty(r10);
        if (r10 != 0) goto L_0x0092;
    L_0x0030:
        r10 = org.telegram.messenger.MessagesController.getInstance(r12);
        r13 = r11.chat;
        r1 = 0;
        r10.putChat(r13, r1);
        r10 = new java.util.ArrayList;
        r10.<init>();
        r13 = r11.chat;
        r10.add(r13);
        r13 = org.telegram.messenger.MessagesStorage.getInstance(r12);
        r13.putUsersAndChats(r9, r10, r1, r0);
        r9 = new android.os.Bundle;
        r9.<init>();
        r10 = r11.chat;
        r10 = r10.id;
        r11 = "chat_id";
        r9.putInt(r11, r10);
        r10 = mainFragmentsStack;
        r10 = r10.isEmpty();
        if (r10 != 0) goto L_0x0078;
    L_0x0061:
        r10 = org.telegram.messenger.MessagesController.getInstance(r12);
        r11 = mainFragmentsStack;
        r13 = r11.size();
        r13 = r13 - r0;
        r11 = r11.get(r13);
        r11 = (org.telegram.ui.ActionBar.BaseFragment) r11;
        r10 = r10.checkCanOpenChat(r9, r11);
        if (r10 == 0) goto L_0x00eb;
    L_0x0078:
        r3 = new org.telegram.ui.ChatActivity;
        r3.<init>(r9);
        r9 = org.telegram.messenger.NotificationCenter.getInstance(r12);
        r10 = org.telegram.messenger.NotificationCenter.closeChats;
        r11 = new java.lang.Object[r1];
        r9.postNotificationName(r10, r11);
        r2 = r8.actionBarLayout;
        r4 = 0;
        r5 = 1;
        r6 = 1;
        r7 = 0;
        r2.presentFragment(r3, r4, r5, r6, r7);
        goto L_0x00eb;
    L_0x0092:
        r9 = mainFragmentsStack;
        r10 = r9.size();
        r10 = r10 - r0;
        r9 = r9.get(r10);
        r9 = (org.telegram.ui.ActionBar.BaseFragment) r9;
        r10 = new org.telegram.ui.Components.JoinGroupAlert;
        r10.<init>(r8, r11, r13, r9);
        r9.showDialog(r10);
        goto L_0x00eb;
    L_0x00a8:
        r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r11.<init>(r8);
        r12 = NUM; // 0x7f0e00f4 float:1.8875532E38 double:1.053162277E-314;
        r13 = "AppName";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r11.setTitle(r12);
        r10 = r10.text;
        r12 = "FLOOD_WAIT";
        r10 = r10.startsWith(r12);
        if (r10 == 0) goto L_0x00d0;
    L_0x00c3:
        r10 = NUM; // 0x7f0e049c float:1.887743E38 double:1.0531627396E-314;
        r12 = "FloodWait";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setMessage(r10);
        goto L_0x00dc;
    L_0x00d0:
        r10 = NUM; // 0x7f0e0579 float:1.887788E38 double:1.053162849E-314;
        r12 = "JoinToGroupErrorNotExist";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setMessage(r10);
    L_0x00dc:
        r10 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r12 = "OK";
        r10 = org.telegram.messenger.LocaleController.getString(r12, r10);
        r11.setPositiveButton(r10, r9);
        r8.showAlertDialog(r11);
    L_0x00eb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$15$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, java.lang.String):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(int i, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(i).processUpdates((Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$vCEa6p3T1ZN6hNGAhiLWej0rAN8(this, alertDialog, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$17$LaunchActivity(AlertDialog alertDialog, TL_error tL_error, TLObject tLObject, int i) {
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

    public /* synthetic */ void lambda$runLinkRequest$19$LaunchActivity(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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

    public /* synthetic */ void lambda$runLinkRequest$23$LaunchActivity(int[] iArr, int i, AlertDialog alertDialog, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TL_error tL_error) {
        TL_account_authorizationForm tL_account_authorizationForm = (TL_account_authorizationForm) tLObject;
        if (tL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TL_account_getPassword(), new -$$Lambda$LaunchActivity$6fMxpl9we_mycb7TXbZnPk7q0wU(this, alertDialog, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$UBVX6iJYfHWnqNM7CfCLASSNAMEpTnhSw(this, alertDialog, tL_error));
    }

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog alertDialog, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$6EO4tCbv3up53E1g6P81435Y9c0(this, alertDialog, tLObject, i, tL_account_authorizationForm, tL_account_getAuthorizationForm, str, str2, str3));
    }

    public /* synthetic */ void lambda$null$20$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3) {
        TL_account_getAuthorizationForm tL_account_getAuthorizationForm2 = tL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            TL_account_password tL_account_password = (TL_account_password) tLObject;
            MessagesController.getInstance(i).putUsers(tL_account_authorizationForm.users, false);
            lambda$runLinkRequest$28$LaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm2.bot_id, tL_account_getAuthorizationForm2.scope, tL_account_getAuthorizationForm2.public_key, str, str2, str3, tL_account_authorizationForm, tL_account_password));
            return;
        }
    }

    public /* synthetic */ void lambda$null$22$LaunchActivity(AlertDialog alertDialog, TL_error tL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$25$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$xd2zqcHcOs4B0hxZRGiMLfUIVHE(this, alertDialog, tLObject));
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
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

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$xSsHngnLohOpbZxSxmREnZNgM30(this, alertDialog, tLObject, tL_error));
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
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

    public /* synthetic */ void lambda$runLinkRequest$30$LaunchActivity(AlertDialog alertDialog, TL_wallPaper tL_wallPaper, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$npgEXsQgkctcRCxjB_nbijdpEPo(this, alertDialog, tLObject, tL_wallPaper, tL_error));
    }

    public /* synthetic */ void lambda$null$29$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_wallPaper tL_wallPaper, TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_wallPaper) {
            Object obj;
            TL_wallPaper tL_wallPaper2 = (TL_wallPaper) tLObject;
            if (tL_wallPaper2.pattern) {
                TL_wallPaperSettings tL_wallPaperSettings = tL_wallPaper.settings;
                ColorWallpaper colorWallpaper = new ColorWallpaper(-1, tL_wallPaperSettings.background_color, tL_wallPaper2.id, ((float) tL_wallPaperSettings.intensity) / 100.0f, tL_wallPaperSettings.motion, null);
                colorWallpaper.pattern = tL_wallPaper2;
            } else {
                obj = tL_wallPaper2;
            }
            WallpaperActivity wallpaperActivity = new WallpaperActivity(obj, null);
            TL_wallPaperSettings tL_wallPaperSettings2 = tL_wallPaper.settings;
            wallpaperActivity.setInitialModes(tL_wallPaperSettings2.blur, tL_wallPaperSettings2.motion);
            lambda$runLinkRequest$28$LaunchActivity(wallpaperActivity);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        stringBuilder.append(tL_error.text);
        showAlertDialog(AlertsCreator.createSimpleAlert(this, stringBuilder.toString()));
    }

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$33$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$zL4X6ZzTMWuP9ienZowFN1efS5s(this, tLObject, alertDialog, tL_error));
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0039 A:{SYNTHETIC, Splitter:B:11:0x0039} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0039 A:{SYNTHETIC, Splitter:B:11:0x0039} */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing block: B:7:0x0032, code skipped:
            if ("THEME_FORMAT_INVALID".equals(r5.text) != false) goto L_0x0034;
     */
    public /* synthetic */ void lambda$null$32$LaunchActivity(org.telegram.tgnet.TLObject r3, org.telegram.ui.ActionBar.AlertDialog r4, org.telegram.tgnet.TLRPC.TL_error r5) {
        /*
        r2 = this;
        r0 = r3 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        r1 = 1;
        if (r0 == 0) goto L_0x0028;
    L_0x0005:
        r3 = (org.telegram.tgnet.TLRPC.TL_theme) r3;
        r5 = r3.document;
        if (r5 == 0) goto L_0x0034;
    L_0x000b:
        r2.loadingTheme = r3;
        r5 = r2.loadingTheme;
        r5 = r5.document;
        r5 = org.telegram.messenger.FileLoader.getAttachFileName(r5);
        r2.loadingThemeFileName = r5;
        r2.loadingThemeProgressDialog = r4;
        r5 = r2.currentAccount;
        r5 = org.telegram.messenger.FileLoader.getInstance(r5);
        r0 = r2.loadingTheme;
        r0 = r0.document;
        r5.loadFile(r0, r3, r1, r1);
        r3 = 0;
        goto L_0x0037;
    L_0x0028:
        if (r5 == 0) goto L_0x0036;
    L_0x002a:
        r3 = r5.text;
        r5 = "THEME_FORMAT_INVALID";
        r3 = r5.equals(r3);
        if (r3 == 0) goto L_0x0036;
    L_0x0034:
        r3 = 1;
        goto L_0x0037;
    L_0x0036:
        r3 = 2;
    L_0x0037:
        if (r3 == 0) goto L_0x0071;
    L_0x0039:
        r4.dismiss();	 Catch:{ Exception -> 0x003d }
        goto L_0x0041;
    L_0x003d:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
    L_0x0041:
        r4 = NUM; // 0x7f0e0aa2 float:1.8880558E38 double:1.0531635015E-314;
        r5 = "Theme";
        if (r3 != r1) goto L_0x005d;
    L_0x0048:
        r3 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r4 = NUM; // 0x7f0e0ab5 float:1.8880597E38 double:1.053163511E-314;
        r5 = "ThemeNotSupported";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r3 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r2, r3, r4);
        r2.showAlertDialog(r3);
        goto L_0x0071;
    L_0x005d:
        r3 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r4 = NUM; // 0x7f0e0ab4 float:1.8880595E38 double:1.0531635104E-314;
        r5 = "ThemeNotFound";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r3 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r2, r3, r4);
        r2.showAlertDialog(r3);
    L_0x0071:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$32$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$36$LaunchActivity(Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TL_channels_getChannels tL_channels_getChannels = new TL_channels_getChannels();
            TL_inputChannel tL_inputChannel = new TL_inputChannel();
            tL_inputChannel.channel_id = num.intValue();
            tL_channels_getChannels.id.add(tL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getChannels, new -$$Lambda$LaunchActivity$dRnOL8ny0LCcAoDQKZ-xJuC1bEc(this, alertDialog, baseFragment, i, bundle));
        }
    }

    public /* synthetic */ void lambda$null$35$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$w4v600u5TO1-pL3QBRVdYvE0A3s(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    public /* synthetic */ void lambda$null$34$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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

    static /* synthetic */ void lambda$runLinkRequest$37(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            ConnectionsManager.getInstance(i).sendRequest(tL_help_getAppUpdate, new -$$Lambda$LaunchActivity$puamlNtAsUPmY46PGuZTMuLAy6U(this, i));
        }
    }

    public /* synthetic */ void lambda$checkAppUpdate$39$LaunchActivity(int i, TLObject tLObject, TL_error tL_error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (tLObject instanceof TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$gV3_Gxrz0V8VPlp5bc6jLaZHkuw(this, (TL_help_appUpdate) tLObject, i));
        }
    }

    public /* synthetic */ void lambda$null$38$LaunchActivity(TL_help_appUpdate tL_help_appUpdate, int i) {
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
            this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    if (LaunchActivity.this.visibleDialog != null) {
                        if (LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                            try {
                                Toast.makeText(LaunchActivity.this, LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", NUM), 1).show();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            LaunchActivity.this.localeDialog = null;
                        } else if (LaunchActivity.this.visibleDialog == LaunchActivity.this.proxyErrorDialog) {
                            MessagesController.getGlobalMainSettings();
                            Editor edit = MessagesController.getGlobalMainSettings().edit();
                            edit.putBoolean("proxy_enabled", false);
                            edit.putBoolean("proxy_enabled_calls", false);
                            edit.commit();
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                            ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                            LaunchActivity.this.proxyErrorDialog = null;
                        }
                    }
                    LaunchActivity.this.visibleDialog = null;
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
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
            if (arrayList5 == null || arrayList5.size() != 1) {
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
                PhonebookShareActivity phonebookShareActivity = new PhonebookShareActivity(null, this.contactsToSendUri, null, null);
                phonebookShareActivity.setDelegate(new -$$Lambda$LaunchActivity$A1V1jKdUjCR72Pzt5rp6DOhh7GI(this, chatActivity, arrayList2, size));
                this.actionBarLayout.presentFragment(phonebookShareActivity, baseFragment != null, baseFragment == null, true, false);
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

    public /* synthetic */ void lambda$didSelectDialogs$40$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, User user, boolean z, int i2) {
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$28$LaunchActivity(BaseFragment baseFragment) {
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
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
        } else {
            showPermissionErrorAlert(LocaleController.getString("PermissionContacts", NUM));
            return;
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
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LaunchActivity$0ZaZpyEb2BAGJU2FCxRGTDab2yM(this));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$41$LaunchActivity(DialogInterface dialogInterface, int i) {
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
        Utilities.stageQueue.postRunnable(-$$Lambda$LaunchActivity$FosST-41dau99zs7twbH8VdqwTc.INSTANCE);
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

    static /* synthetic */ void lambda$onPause$42() {
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
        Utilities.stageQueue.postRunnable(-$$Lambda$LaunchActivity$93u5ZUKTUKGO_5ztQoGcsaZVAgA.INSTANCE);
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

    static /* synthetic */ void lambda$onResume$43() {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
        } else {
            int i3 = 0;
            StringBuilder stringBuilder;
            if (i == NotificationCenter.closeOtherAppActivities) {
                if (objArr[0] != this) {
                    onFinish();
                    finish();
                }
            } else if (i == NotificationCenter.didUpdateConnectionState) {
                i = ConnectionsManager.getInstance(i2).getConnectionState();
                if (this.currentConnectionState != i) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("switch to state ");
                        stringBuilder.append(i);
                        FileLog.d(stringBuilder.toString());
                    }
                    this.currentConnectionState = i;
                    updateCurrentConnectionState(i2);
                }
            } else if (i == NotificationCenter.mainUserInfoChanged) {
                this.drawerLayoutAdapter.notifyDataSetChanged();
            } else {
                String str = "Cancel";
                String str2 = "AppName";
                String str3 = "OK";
                ArrayList arrayList;
                HashMap hashMap;
                RecyclerListView recyclerListView;
                ThemeInfo themeInfo;
                String str4;
                if (i == NotificationCenter.needShowAlert) {
                    Integer num = (Integer) objArr[0];
                    if (num.intValue() == 3 && this.proxyErrorDialog != null) {
                        return;
                    }
                    if (num.intValue() == 4) {
                        showTosActivity(i2, (TL_help_termsOfService) objArr[1]);
                        return;
                    }
                    Builder builder = new Builder((Context) this);
                    builder.setTitle(LocaleController.getString(str2, NUM));
                    if (!(num.intValue() == 2 || num.intValue() == 3)) {
                        builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new -$$Lambda$LaunchActivity$YavO8soSppyScOgJAGY3e6zF9Vc(i2));
                    }
                    if (num.intValue() == 5) {
                        builder.setMessage(LocaleController.getString("NobodyLikesSpam3", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    } else if (num.intValue() == 0) {
                        builder.setMessage(LocaleController.getString("NobodyLikesSpam1", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    } else if (num.intValue() == 1) {
                        builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    } else if (num.intValue() == 2) {
                        builder.setMessage((String) objArr[1]);
                        if (((String) objArr[2]).startsWith("AUTH_KEY_DROP_")) {
                            builder.setPositiveButton(LocaleController.getString(str, NUM), null);
                            builder.setNegativeButton(LocaleController.getString("LogOut", NUM), new -$$Lambda$LaunchActivity$ohiCjTY45oyltDIuw3_XpdrJYKw(this));
                        } else {
                            builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                        }
                    } else if (num.intValue() == 3) {
                        builder.setMessage(LocaleController.getString("UseProxyTelegramError", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                        this.proxyErrorDialog = showAlertDialog(builder);
                        return;
                    }
                    if (!mainFragmentsStack.isEmpty()) {
                        arrayList = mainFragmentsStack;
                        ((BaseFragment) arrayList.get(arrayList.size() - 1)).showDialog(builder.create());
                    }
                } else if (i == NotificationCenter.wasUnableToFindCurrentLocation) {
                    hashMap = (HashMap) objArr[0];
                    Builder builder2 = new Builder((Context) this);
                    builder2.setTitle(LocaleController.getString(str2, NUM));
                    builder2.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    builder2.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", NUM), new -$$Lambda$LaunchActivity$PW5wPiEvar_bfbIupoIqjuvBm0(this, hashMap, i2));
                    builder2.setMessage(LocaleController.getString("ShareYouLocationUnable", NUM));
                    if (!mainFragmentsStack.isEmpty()) {
                        arrayList = mainFragmentsStack;
                        ((BaseFragment) arrayList.get(arrayList.size() - 1)).showDialog(builder2.create());
                    }
                } else if (i == NotificationCenter.didSetNewWallpapper) {
                    recyclerListView = this.sideMenu;
                    if (recyclerListView != null) {
                        View childAt = recyclerListView.getChildAt(0);
                        if (childAt != null) {
                            childAt.invalidate();
                        }
                    }
                } else if (i == NotificationCenter.didSetPasscode) {
                    if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
                        try {
                            getWindow().setFlags(8192, 8192);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    } else if (!AndroidUtilities.hasFlagSecureFragment()) {
                        try {
                            getWindow().clearFlags(8192);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                } else if (i == NotificationCenter.reloadInterface) {
                    boolean z;
                    if (mainFragmentsStack.size() > 1) {
                        arrayList = mainFragmentsStack;
                        if (arrayList.get(arrayList.size() - 1) instanceof SettingsActivity) {
                            z = true;
                        }
                    }
                    rebuildAllFragments(z);
                } else if (i == NotificationCenter.suggestedLangpack) {
                    showLanguageAlert(false);
                } else if (i == NotificationCenter.openArticle) {
                    if (!mainFragmentsStack.isEmpty()) {
                        ArticleViewer instance = ArticleViewer.getInstance();
                        ArrayList arrayList2 = mainFragmentsStack;
                        instance.setParentActivity(this, (BaseFragment) arrayList2.get(arrayList2.size() - 1));
                        ArticleViewer.getInstance().open((TL_webPage) objArr[0], (String) objArr[1]);
                    }
                } else if (i == NotificationCenter.hasNewContactsToImport) {
                    ActionBarLayout actionBarLayout = this.actionBarLayout;
                    if (actionBarLayout != null && !actionBarLayout.fragmentsStack.isEmpty()) {
                        ((Integer) objArr[0]).intValue();
                        hashMap = (HashMap) objArr[1];
                        boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                        boolean booleanValue2 = ((Boolean) objArr[3]).booleanValue();
                        ArrayList arrayList3 = this.actionBarLayout.fragmentsStack;
                        BaseFragment baseFragment = (BaseFragment) arrayList3.get(arrayList3.size() - 1);
                        Builder builder3 = new Builder((Context) this);
                        builder3.setTitle(LocaleController.getString("UpdateContactsTitle", NUM));
                        builder3.setMessage(LocaleController.getString("UpdateContactsMessage", NUM));
                        builder3.setPositiveButton(LocaleController.getString(str3, NUM), new -$$Lambda$LaunchActivity$Qr9Ud-NdOimXHu1XJQbnkx_ht3U(i2, hashMap, booleanValue, booleanValue2));
                        builder3.setNegativeButton(LocaleController.getString(str, NUM), new -$$Lambda$LaunchActivity$UE1gLD7_e0jUEwM6LsuMoTRYapU(i2, hashMap, booleanValue, booleanValue2));
                        builder3.setOnBackButtonListener(new -$$Lambda$LaunchActivity$ranLGZC9-H2Uv1-75NOehiIJcFU(i2, hashMap, booleanValue, booleanValue2));
                        AlertDialog create = builder3.create();
                        baseFragment.showDialog(create);
                        create.setCanceledOnTouchOutside(false);
                    }
                } else if (i == NotificationCenter.didSetNewTheme) {
                    if (!((Boolean) objArr[0]).booleanValue()) {
                        recyclerListView = this.sideMenu;
                        if (recyclerListView != null) {
                            String str5 = "chats_menuBackground";
                            recyclerListView.setBackgroundColor(Theme.getColor(str5));
                            this.sideMenu.setGlowColor(Theme.getColor(str5));
                            this.sideMenu.setListSelectorColor(Theme.getColor("listSelectorSDK21"));
                            this.sideMenu.getAdapter().notifyDataSetChanged();
                        }
                        if (VERSION.SDK_INT >= 21) {
                            try {
                                setTaskDescription(new TaskDescription(null, null, Theme.getColor("actionBarDefault") | -16777216));
                            } catch (Exception unused) {
                            }
                        }
                    }
                    this.drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor("windowBackgroundWhite"));
                } else if (i == NotificationCenter.needSetDayNightTheme) {
                    themeInfo = (ThemeInfo) objArr[0];
                    boolean booleanValue3 = ((Boolean) objArr[1]).booleanValue();
                    this.actionBarLayout.animateThemedValues(themeInfo, booleanValue3);
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.animateThemedValues(themeInfo, booleanValue3);
                        this.rightActionBarLayout.animateThemedValues(themeInfo, booleanValue3);
                    }
                } else if (i == NotificationCenter.notificationsCountUpdated) {
                    recyclerListView = this.sideMenu;
                    if (recyclerListView != null) {
                        Integer num2 = (Integer) objArr[0];
                        i = recyclerListView.getChildCount();
                        while (i3 < i) {
                            View childAt2 = this.sideMenu.getChildAt(i3);
                            if ((childAt2 instanceof DrawerUserCell) && ((DrawerUserCell) childAt2).getAccountNumber() == num2.intValue()) {
                                childAt2.invalidate();
                                break;
                            }
                            i3++;
                        }
                    }
                } else if (i == NotificationCenter.needShowPlayServicesAlert) {
                    try {
                        ((Status) objArr[0]).startResolutionForResult(this, 140);
                    } catch (Throwable unused2) {
                    }
                } else if (i == NotificationCenter.fileDidLoad) {
                    str4 = this.loadingThemeFileName;
                    if (str4 == null) {
                        str4 = this.loadingThemeWallpaperName;
                        if (str4 != null && str4.equals((String) objArr[0])) {
                            this.loadingThemeWallpaperName = null;
                            Utilities.globalQueue.postRunnable(new -$$Lambda$LaunchActivity$ooxBJ3YZyy_8xDIs-7CpwcP_78g(this, (File) objArr[1]));
                        }
                    } else if (str4.equals((String) objArr[0])) {
                        this.loadingThemeFileName = null;
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("remote");
                        stringBuilder.append(this.loadingTheme.id);
                        stringBuilder.append(".attheme");
                        File file = new File(filesDirFixed, stringBuilder.toString());
                        TL_theme tL_theme = this.loadingTheme;
                        ThemeInfo fillThemeValues = Theme.fillThemeValues(file, tL_theme.title, tL_theme);
                        if (fillThemeValues != null) {
                            String str6 = fillThemeValues.pathToWallpaper;
                            if (str6 == null || new File(str6).exists()) {
                                tL_theme = this.loadingTheme;
                                themeInfo = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
                                if (themeInfo != null) {
                                    lambda$runLinkRequest$28$LaunchActivity(new ThemePreviewActivity(themeInfo, true, 0, false));
                                }
                            } else {
                                TL_account_getWallPaper tL_account_getWallPaper = new TL_account_getWallPaper();
                                TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
                                tL_inputWallPaperSlug.slug = fillThemeValues.slug;
                                tL_account_getWallPaper.wallpaper = tL_inputWallPaperSlug;
                                ConnectionsManager.getInstance(fillThemeValues.account).sendRequest(tL_account_getWallPaper, new -$$Lambda$LaunchActivity$lJcvZEJyO8Mz9YX2KH774RfKjcc(this, fillThemeValues));
                                return;
                            }
                        }
                        onThemeLoadFinish();
                    }
                } else if (i == NotificationCenter.fileDidFailToLoad) {
                    str4 = (String) objArr[0];
                    if (str4.equals(this.loadingThemeFileName) || str4.equals(this.loadingThemeWallpaperName)) {
                        onThemeLoadFinish();
                    }
                } else if (i == NotificationCenter.screenStateChanged && !ApplicationLoader.mainInterfacePaused) {
                    if (ApplicationLoader.isScreenOn) {
                        onPasscodeResume();
                    } else {
                        onPasscodePause();
                    }
                }
            }
        }
    }

    static /* synthetic */ void lambda$didReceivedNotification$44(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", (BaseFragment) arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$45$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$47$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled((BaseFragment) arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new -$$Lambda$LaunchActivity$zMbUU4Sr9q8n_1Kz7064ju32gao(hashMap, i));
                lambda$runLinkRequest$28$LaunchActivity(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$46(HashMap hashMap, int i, MessageMedia messageMedia, int i2, boolean z, int i3) {
        for (Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(messageMedia, messageObject.getDialogId(), messageObject, null, null, z, i3);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$52$LaunchActivity(ThemeInfo themeInfo, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$qxRFygcKzjvIhvkrBl4HiJVv_ck(this, tLObject, themeInfo));
    }

    public /* synthetic */ void lambda$null$51$LaunchActivity(TLObject tLObject, ThemeInfo themeInfo) {
        if (tLObject instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
            FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$54$LaunchActivity(File file) {
        try {
            Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), null, 0);
            if (!(scaledBitmap == null || this.loadingThemeInfo.patternBgColor == 0)) {
                Bitmap createBitmap = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap.getConfig());
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawColor(this.loadingThemeInfo.patternBgColor);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(this.loadingThemeInfo.patternBgColor), Mode.SRC_IN));
                paint.setAlpha((int) ((((float) this.loadingThemeInfo.patternIntensity) / 100.0f) * 255.0f));
                canvas.drawBitmap(scaledBitmap, 0.0f, 0.0f, paint);
                canvas.setBitmap(null);
                scaledBitmap = createBitmap;
            }
            if (this.loadingThemeInfo.isBlured) {
                scaledBitmap = Utilities.blurWallpaper(scaledBitmap);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(this.loadingThemeInfo.pathToWallpaper);
            scaledBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
            fileOutputStream.close();
        } catch (Throwable th) {
            FileLog.e(th);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$B81FGoUWKNHnG131s5JNaumNWjE(this));
    }

    public /* synthetic */ void lambda$null$53$LaunchActivity() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("remote");
        stringBuilder.append(this.loadingTheme.id);
        stringBuilder.append(".attheme");
        File file = new File(filesDirFixed, stringBuilder.toString());
        TL_theme tL_theme = this.loadingTheme;
        ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
        if (applyThemeFile != null) {
            lambda$runLinkRequest$28$LaunchActivity(new ThemePreviewActivity(applyThemeFile, true, 0, false));
        }
        onThemeLoadFinish();
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = (String) hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
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
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void checkFreeDiscSpace() {
        SharedConfig.checkKeepMedia();
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new -$$Lambda$LaunchActivity$L1eoJr6Ukh2DIsDS6GLA7Tzwydg(this), 2000);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$56$LaunchActivity() {
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
                            AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$c-2CyZ2pQx0v22446c0BmD-JC_k(this));
                        }
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$null$55$LaunchActivity() {
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
        r9 = NUM; // 0x7f0e02de float:1.8876526E38 double:1.0531625193E-314;
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
        r14 = NUM; // 0x7f0e0412 float:1.887715E38 double:1.0531626714E-314;
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
        r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$4u68s-gPo04xoNFteibptrKEOrk;	 Catch:{ Exception -> 0x0115 }
        r13.<init>(r10, r9);	 Catch:{ Exception -> 0x0115 }
        r3.setOnClickListener(r13);	 Catch:{ Exception -> 0x0115 }
        r4 = r4 + 1;
        r3 = 0;
        goto L_0x006a;
    L_0x00bf:
        r3 = new org.telegram.ui.Cells.LanguageCell;	 Catch:{ Exception -> 0x0115 }
        r3.<init>(r1, r6);	 Catch:{ Exception -> 0x0115 }
        r4 = r1.systemLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r5 = NUM; // 0x7f0e02df float:1.8876528E38 double:1.05316252E-314;
        r4 = r1.getStringForLanguageAlert(r4, r0, r5);	 Catch:{ Exception -> 0x0115 }
        r6 = r1.englishLocaleStrings;	 Catch:{ Exception -> 0x0115 }
        r0 = r1.getStringForLanguageAlert(r6, r0, r5);	 Catch:{ Exception -> 0x0115 }
        r3.setValue(r4, r0);	 Catch:{ Exception -> 0x0115 }
        r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$wVDAp4KP76ENmF-zF7YYlzt_5Lo;	 Catch:{ Exception -> 0x0115 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0115 }
        r3.setOnClickListener(r0);	 Catch:{ Exception -> 0x0115 }
        r0 = 50;
        r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0);	 Catch:{ Exception -> 0x0115 }
        r2.addView(r3, r0);	 Catch:{ Exception -> 0x0115 }
        r7.setView(r2);	 Catch:{ Exception -> 0x0115 }
        r0 = "OK";
        r2 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x0115 }
        r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$hPnomj29M7tV8r3dsaooSomOoSE;	 Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$57(LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$58$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$28$LaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$59$LaunchActivity(LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new -$$Lambda$LaunchActivity$TDwDYFNQ6WqEl4Dka8hV0rY0Tew(this, localeInfoArr, str6), 8);
                                tL_langpack_getStrings = new TL_langpack_getStrings();
                                tL_langpack_getStrings.lang_code = localeInfoArr[0].getLangCode();
                                tL_langpack_getStrings.keys.add(str4);
                                tL_langpack_getStrings.keys.add(str3);
                                tL_langpack_getStrings.keys.add(str2);
                                tL_langpack_getStrings.keys.add(str);
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new -$$Lambda$LaunchActivity$KaM0UNfbj33XSzG29var_GAj2LH0(this, localeInfoArr, str6), 8);
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

    public /* synthetic */ void lambda$showLanguageAlert$61$LaunchActivity(LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                LangPackString langPackString = (LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$efuVwScQLokRW2f8CFWNKe9_mpw(this, hashMap, localeInfoArr, str));
    }

    public /* synthetic */ void lambda$null$60$LaunchActivity(HashMap hashMap, LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$63$LaunchActivity(LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TL_error tL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                LangPackString langPackString = (LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LaunchActivity$uVbd0ew9Gl7HjErIAY3ofIZOrRg(this, hashMap, localeInfoArr, str));
    }

    public /* synthetic */ void lambda$null$62$LaunchActivity(HashMap hashMap, LocaleInfo[] localeInfoArr, String str) {
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
                runnable = new -$$Lambda$LaunchActivity$0UAtL0GiD-J7tGPc_zFNwNoEZgE(this);
            }
            this.actionBarLayout.setTitleOverlayText(str, i, runnable);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0039  */
    public /* synthetic */ void lambda$updateCurrentConnectionState$64$LaunchActivity() {
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
        r2.lambda$runLinkRequest$28$LaunchActivity(r0);
    L_0x0046:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$64$LaunchActivity():void");
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
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onLowMemory();
            this.layersActionBarLayout.onLowMemory();
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
