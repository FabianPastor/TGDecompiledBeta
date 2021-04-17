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
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.arch.core.util.Function;
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
import org.telegram.messenger.ChatObject;
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
import org.telegram.messenger.MessagesStorage;
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
import org.telegram.tgnet.TLRPC$InputPeer;
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
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
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
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.GroupCallPip;
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
import org.webrtc.voiceengine.WebRtcAudioTrack;

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
    public DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    private Uri exportingChatUri;
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
    private boolean wasMutedByAdminRaisedHand;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x0110 */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r14) {
        /*
            r13 = this;
            java.lang.String r0 = "flyme"
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r1 = r13.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r13, r1)
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r13.currentAccount = r1
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x00ef
            android.content.Intent r1 = r13.getIntent()
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
            super.onCreate(r14)
            r13.finish()
            return
        L_0x0088:
            r4 = 0
        L_0x0089:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r6 = "intro_crashed_time"
            r7 = 0
            long r9 = r5.getLong(r6, r7)
            if (r1 == 0) goto L_0x00a1
            java.lang.String r11 = "fromIntro"
            boolean r11 = r1.getBooleanExtra(r11, r3)
            if (r11 == 0) goto L_0x00a1
            r11 = 1
            goto L_0x00a2
        L_0x00a1:
            r11 = 0
        L_0x00a2:
            if (r11 == 0) goto L_0x00af
            android.content.SharedPreferences$Editor r5 = r5.edit()
            android.content.SharedPreferences$Editor r5 = r5.putLong(r6, r7)
            r5.commit()
        L_0x00af:
            if (r4 != 0) goto L_0x00ef
            long r4 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r4
            long r4 = java.lang.Math.abs(r9)
            r6 = 120000(0x1d4c0, double:5.9288E-319)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 < 0) goto L_0x00ef
            if (r1 == 0) goto L_0x00ef
            if (r11 != 0) goto L_0x00ef
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r5 = "logininfo2"
            android.content.SharedPreferences r4 = r4.getSharedPreferences(r5, r3)
            java.util.Map r4 = r4.getAll()
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x00ef
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r2 = org.telegram.ui.IntroActivity.class
            r0.<init>(r13, r2)
            android.net.Uri r1 = r1.getData()
            r0.setData(r1)
            r13.startActivity(r0)
            super.onCreate(r14)
            r13.finish()
            return
        L_0x00ef:
            r13.requestWindowFeature(r2)
            r1 = 2131689484(0x7f0var_c, float:1.9007985E38)
            r13.setTheme(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 0
            r5 = 21
            if (r1 < r5) goto L_0x0119
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r6 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0110 }
            java.lang.String r7 = "actionBarDefault"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x0110 }
            r7 = r7 | r1
            r6.<init>(r4, r4, r7)     // Catch:{ Exception -> 0x0110 }
            r13.setTaskDescription(r6)     // Catch:{ Exception -> 0x0110 }
        L_0x0110:
            android.view.Window r6 = r13.getWindow()     // Catch:{ Exception -> 0x0118 }
            r6.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x0118 }
            goto L_0x0119
        L_0x0118:
        L_0x0119:
            android.view.Window r1 = r13.getWindow()
            r6 = 2131166093(0x7var_d, float:1.7946422E38)
            r1.setBackgroundDrawableResource(r6)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x013d
            boolean r1 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x013d
            android.view.Window r1 = r13.getWindow()     // Catch:{ Exception -> 0x0139 }
            r6 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r6, r6)     // Catch:{ Exception -> 0x0139 }
            goto L_0x013d
        L_0x0139:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x013d:
            super.onCreate(r14)
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r1 < r6) goto L_0x014c
            boolean r7 = r13.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r7
        L_0x014c:
            org.telegram.ui.ActionBar.Theme.createChatResources(r13, r3)
            java.lang.String r7 = org.telegram.messenger.SharedConfig.passcodeHash
            int r7 = r7.length()
            if (r7 == 0) goto L_0x0165
            boolean r7 = org.telegram.messenger.SharedConfig.appLocked
            if (r7 == 0) goto L_0x0165
            long r7 = android.os.SystemClock.elapsedRealtime()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r8 = (int) r7
            org.telegram.messenger.SharedConfig.lastPauseTime = r8
        L_0x0165:
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
            if (r1 < r5) goto L_0x018d
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r13)
            r13.themeSwitchImageView = r8
            r8.setVisibility(r7)
        L_0x018d:
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
            if (r1 < r5) goto L_0x01c6
            org.telegram.ui.LaunchActivity$2 r1 = new org.telegram.ui.LaunchActivity$2
            r1.<init>(r13)
            r13.themeSwitchSunView = r1
            android.widget.FrameLayout r5 = r13.frameLayout
            r8 = 48
            r10 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r5.addView(r1, r8)
            android.view.View r1 = r13.themeSwitchSunView
            r1.setVisibility(r7)
        L_0x01c6:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x02b0
            android.view.Window r1 = r13.getWindow()
            r5 = 16
            r1.setSoftInputMode(r5)
            org.telegram.ui.LaunchActivity$3 r1 = new org.telegram.ui.LaunchActivity$3
            r1.<init>(r13)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r5.addView(r1, r8)
            android.view.View r5 = new android.view.View
            r5.<init>(r13)
            r13.backgroundTablet = r5
            android.content.res.Resources r5 = r13.getResources()
            r8 = 2131165337(0x7var_, float:1.7944888E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r8)
            android.graphics.drawable.BitmapDrawable r5 = (android.graphics.drawable.BitmapDrawable) r5
            android.graphics.Shader$TileMode r8 = android.graphics.Shader.TileMode.REPEAT
            r5.setTileModeXY(r8, r8)
            android.view.View r8 = r13.backgroundTablet
            r8.setBackgroundDrawable(r5)
            android.view.View r5 = r13.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createRelative(r9, r9)
            r1.addView(r5, r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            r1.addView(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r13)
            r13.rightActionBarLayout = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = rightFragmentsStack
            r5.init(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.rightActionBarLayout
            r5.setDelegate(r13)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.rightActionBarLayout
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r13)
            r13.shadowTabletSide = r5
            r8 = 1076449908(0x40295274, float:2.6456575)
            r5.setBackgroundColor(r8)
            android.widget.FrameLayout r5 = r13.shadowTabletSide
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r13)
            r13.shadowTablet = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = layerFragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 == 0) goto L_0x0249
            r8 = 8
            goto L_0x024a
        L_0x0249:
            r8 = 0
        L_0x024a:
            r5.setVisibility(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            r8 = 2130706432(0x7var_, float:1.7014118E38)
            r5.setBackgroundColor(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            r1.addView(r5)
            android.widget.FrameLayout r5 = r13.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw
            r8.<init>()
            r5.setOnTouchListener(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7-S05A r8 = org.telegram.ui.$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7S05A.INSTANCE
            r5.setOnClickListener(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r13)
            r13.layersActionBarLayout = r5
            r5.setRemoveActionBarExtraHeight(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            android.widget.FrameLayout r8 = r13.shadowTablet
            r5.setBackgroundView(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            r5.setUseAlphaAnimations(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            r8 = 2131165300(0x7var_, float:1.7944813E38)
            r5.setBackgroundResource(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = layerFragmentsStack
            r5.init(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            r5.setDelegate(r13)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r13.drawerLayoutContainer
            r5.setDrawerLayoutContainer(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = layerFragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 == 0) goto L_0x02a6
            goto L_0x02a7
        L_0x02a6:
            r7 = 0
        L_0x02a7:
            r5.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            r1.addView(r5)
            goto L_0x02bc
        L_0x02b0:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r9, r9)
            r1.addView(r5, r7)
        L_0x02bc:
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r13)
            r13.sideMenu = r1
            org.telegram.ui.Components.SideMenultItemAnimator r5 = new org.telegram.ui.Components.SideMenultItemAnimator
            r5.<init>(r1)
            r13.itemAnimator = r5
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            r1.setItemAnimator(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            java.lang.String r5 = "chats_menuBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setBackgroundColor(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            r5.<init>(r13, r2, r3)
            r1.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            r1.setAllowItemsInteractionDuringAnimation(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r5 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r7 = r13.itemAnimator
            r5.<init>(r13, r7)
            r13.drawerLayoutAdapter = r5
            r1.setAdapter(r5)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r1.setDrawerLayout(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r8 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x0317
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x032e
        L_0x0317:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r8, r5)
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            int r5 = java.lang.Math.min(r7, r5)
        L_0x032e:
            r1.width = r5
            r1.height = r9
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r5.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s
            r5.<init>()
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r5)
            androidx.recyclerview.widget.ItemTouchHelper r1 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$5 r5 = new org.telegram.ui.LaunchActivity$5
            r7 = 3
            r5.<init>(r7, r3)
            r1.<init>(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r1.attachToRecyclerView(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas r8 = new org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas
            r8.<init>(r1)
            r5.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r8)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            r1.setParentActionBarLayout(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            r1.setDrawerLayoutContainer(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = mainFragmentsStack
            r1.init(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            r1.setDelegate(r13)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.Components.PasscodeView r1 = new org.telegram.ui.Components.PasscodeView
            r1.<init>(r13)
            r13.passcodeView = r1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r5.addView(r1, r8)
            r13.checkCurrentAccount()
            int r1 = r13.currentAccount
            r13.updateCurrentConnectionState(r1)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r8 = new java.lang.Object[r2]
            r8[r3] = r13
            r1.postNotificationName(r5, r8)
            int r1 = r13.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r13.currentConnectionState = r1
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.needShowAlert
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.reloadInterface
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r1.addObserver(r13, r8)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            r1.addObserver(r13, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r1.addObserver(r13, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r1.addObserver(r13, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r1.addObserver(r13, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r1.addObserver(r13, r5)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.showBulletin
            r1.addObserver(r13, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0532
            int r1 = r13.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x0439
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            org.telegram.ui.LoginActivity r4 = new org.telegram.ui.LoginActivity
            r4.<init>()
            r1.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x044d
        L_0x0439:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r4 = r13.sideMenu
            r1.setSideMenu(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            r4.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r3)
        L_0x044d:
            if (r14 == 0) goto L_0x0599
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r14.getString(r1)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x0599
            java.lang.String r4 = "args"
            android.os.Bundle r4 = r14.getBundle(r4)     // Catch:{ Exception -> 0x052d }
            int r5 = r1.hashCode()     // Catch:{ Exception -> 0x052d }
            r8 = 5
            r10 = 4
            r11 = 2
            switch(r5) {
                case -1529105743: goto L_0x049a;
                case -1349522494: goto L_0x0490;
                case 3052376: goto L_0x0486;
                case 98629247: goto L_0x047c;
                case 738950403: goto L_0x0472;
                case 1434631203: goto L_0x0468;
                default: goto L_0x0467;
            }     // Catch:{ Exception -> 0x052d }
        L_0x0467:
            goto L_0x04a4
        L_0x0468:
            java.lang.String r5 = "settings"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 1
            goto L_0x04a4
        L_0x0472:
            java.lang.String r5 = "channel"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 3
            goto L_0x04a4
        L_0x047c:
            java.lang.String r5 = "group"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 2
            goto L_0x04a4
        L_0x0486:
            java.lang.String r5 = "chat"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 0
            goto L_0x04a4
        L_0x0490:
            java.lang.String r5 = "chat_profile"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 4
            goto L_0x04a4
        L_0x049a:
            java.lang.String r5 = "wallpapers"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x052d }
            if (r1 == 0) goto L_0x04a4
            r9 = 5
        L_0x04a4:
            if (r9 == 0) goto L_0x051a
            if (r9 == r2) goto L_0x04fd
            if (r9 == r11) goto L_0x04e9
            if (r9 == r7) goto L_0x04d5
            if (r9 == r10) goto L_0x04c1
            if (r9 == r8) goto L_0x04b2
            goto L_0x0599
        L_0x04b2:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r3)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x04c1:
            if (r4 == 0) goto L_0x0599
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r4)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            if (r4 == 0) goto L_0x0599
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x04d5:
            if (r4 == 0) goto L_0x0599
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r4)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            if (r4 == 0) goto L_0x0599
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x04e9:
            if (r4 == 0) goto L_0x0599
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r4)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            if (r4 == 0) goto L_0x0599
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x04fd:
            java.lang.String r1 = "user_id"
            int r5 = r13.currentAccount     // Catch:{ Exception -> 0x052d }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x052d }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x052d }
            r4.putInt(r1, r5)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r4)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x051a:
            if (r4 == 0) goto L_0x0599
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x052d }
            r1.<init>(r4)     // Catch:{ Exception -> 0x052d }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x052d }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x052d }
            if (r4 == 0) goto L_0x0599
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x052d }
            goto L_0x0599
        L_0x052d:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0599
        L_0x0532:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r4 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r4 == 0) goto L_0x0547
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r4 = r13.sideMenu
            r1.setSideMenu(r4)
        L_0x0547:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x057c
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x0563
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0563
            r1 = 1
            goto L_0x0564
        L_0x0563:
            r1 = 0
        L_0x0564:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x057d
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x057d
            r1 = 0
            goto L_0x057d
        L_0x057c:
            r1 = 1
        L_0x057d:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0594
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0594
            r1 = 0
        L_0x0594:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r13.drawerLayoutContainer
            r4.setAllowOpenDrawer(r1, r3)
        L_0x0599:
            r13.checkLayout()
            r13.checkSystemBarColors()
            android.content.Intent r1 = r13.getIntent()
            if (r14 == 0) goto L_0x05a7
            r14 = 1
            goto L_0x05a8
        L_0x05a7:
            r14 = 0
        L_0x05a8:
            r13.handleIntent(r1, r3, r14, r3)
            java.lang.String r14 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x060c }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x060c }
            java.lang.String r3 = ""
            if (r14 == 0) goto L_0x05b8
            java.lang.String r14 = r14.toLowerCase()     // Catch:{ Exception -> 0x060c }
            goto L_0x05b9
        L_0x05b8:
            r14 = r3
        L_0x05b9:
            if (r1 == 0) goto L_0x05bf
            java.lang.String r3 = r14.toLowerCase()     // Catch:{ Exception -> 0x060c }
        L_0x05bf:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x060c }
            if (r1 == 0) goto L_0x05df
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x060c }
            r1.<init>()     // Catch:{ Exception -> 0x060c }
            java.lang.String r4 = "OS name "
            r1.append(r4)     // Catch:{ Exception -> 0x060c }
            r1.append(r14)     // Catch:{ Exception -> 0x060c }
            java.lang.String r4 = " "
            r1.append(r4)     // Catch:{ Exception -> 0x060c }
            r1.append(r3)     // Catch:{ Exception -> 0x060c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x060c }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x060c }
        L_0x05df:
            boolean r14 = r14.contains(r0)     // Catch:{ Exception -> 0x060c }
            if (r14 != 0) goto L_0x05eb
            boolean r14 = r3.contains(r0)     // Catch:{ Exception -> 0x060c }
            if (r14 == 0) goto L_0x0610
        L_0x05eb:
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x060c }
            if (r14 > r6) goto L_0x0610
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x060c }
            android.view.Window r14 = r13.getWindow()     // Catch:{ Exception -> 0x060c }
            android.view.View r14 = r14.getDecorView()     // Catch:{ Exception -> 0x060c }
            android.view.View r14 = r14.getRootView()     // Catch:{ Exception -> 0x060c }
            android.view.ViewTreeObserver r0 = r14.getViewTreeObserver()     // Catch:{ Exception -> 0x060c }
            org.telegram.ui.-$$Lambda$LaunchActivity$ZFrSHrKcJLvy27CVdXXiDrROQmE r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$ZFrSHrKcJLvy27CVdXXiDrROQmE     // Catch:{ Exception -> 0x060c }
            r1.<init>(r14)     // Catch:{ Exception -> 0x060c }
            r13.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x060c }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x060c }
            goto L_0x0610
        L_0x060c:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x0610:
            org.telegram.messenger.MediaController r14 = org.telegram.messenger.MediaController.getInstance()
            r14.setBaseActivity(r13, r2)
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
        boolean z = true;
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
                lambda$runLinkRequest$41(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$41(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$41(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$41(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$41(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$41(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$41(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$41(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$41(new ChatActivity(bundle3));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 12) {
                int i4 = Build.VERSION.SDK_INT;
                if (i4 < 23 || checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    if (i4 >= 28) {
                        z = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                    } else if (i4 >= 19) {
                        try {
                            if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                                z = false;
                            }
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }
                    if (z) {
                        lambda$runLinkRequest$41(new PeopleNearbyActivity());
                    } else {
                        lambda$runLinkRequest$41(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                lambda$runLinkRequest$41(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 13) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
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
        lambda$runLinkRequest$41(new ProfileActivity(bundle));
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
        if (SharedConfig.noStatusBar) {
            getWindow().setStatusBarColor(0);
        }
    }

    public void switchToAccount(int i, boolean z) {
        if (i != UserConfig.selectedAccount && UserConfig.isValidAccount(i)) {
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
        clearFragments();
        startActivity(new Intent(this, IntroActivity.class));
        onFinish();
        finish();
    }

    public static void clearFragments() {
        Iterator<BaseFragment> it = mainFragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().onFragmentDestroy();
        }
        mainFragmentsStack.clear();
        if (AndroidUtilities.isTablet()) {
            Iterator<BaseFragment> it2 = layerFragmentsStack.iterator();
            while (it2.hasNext()) {
                it2.next().onFragmentDestroy();
            }
            layerFragmentsStack.clear();
            Iterator<BaseFragment> it3 = rightFragmentsStack.iterator();
            while (it3.hasNext()) {
                it3.next().onFragmentDestroy();
            }
            rightFragmentsStack.clear();
        }
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.historyImportProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v231, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v277, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v322, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v331, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v337, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v343, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v358, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v364, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v381, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v391, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v397, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v399, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v47, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v405, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v407, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v274, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v409, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v276, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v421, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v423, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v424, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v425, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v426, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v302, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v427, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v429, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v433, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v436, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v310, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v438, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v441, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v443, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v445, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v450, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v451, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v454, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v317, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v74, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v75, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v76, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v347, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v77, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v163, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v78, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v45, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v46, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v47, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v48, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v597, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r3v6, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v3, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: type inference failed for: r2v12 */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: type inference failed for: r2v17 */
    /* JADX WARNING: type inference failed for: r2v18 */
    /* JADX WARNING: type inference failed for: r15v22, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r20v10, types: [org.telegram.tgnet.TLRPC$TL_wallPaper] */
    /* JADX WARNING: type inference failed for: r1v138, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r1v316, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r3v100 */
    /* JADX WARNING: type inference failed for: r3v102 */
    /* JADX WARNING: type inference failed for: r3v104 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0314, code lost:
        if (r15.sendingText == null) goto L_0x018f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x045e, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0555, code lost:
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x07ed, code lost:
        if (r6 == 0) goto L_0x07ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0871, code lost:
        if (r1.intValue() == 0) goto L_0x0873;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0114, code lost:
        r0 = r22.getIntent().getExtras();
        r12 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0126, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0129, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x012a, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0147, code lost:
        if (r2.equals(r0) != false) goto L_0x014b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x1295, code lost:
        if (r2 == 0) goto L_0x1297;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x189a, code lost:
        if (r1.checkCanOpenChat(r0, r3.get(r3.size() - r2)) != false) goto L_0x189c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x190e, code lost:
        if (r1.checkCanOpenChat(r0, r3.get(r3.size() - r2)) != false) goto L_0x1910;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:837:0x15ab */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1a5e  */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1a6a  */
    /* JADX WARNING: Removed duplicated region for block: B:1016:0x1a78  */
    /* JADX WARNING: Removed duplicated region for block: B:1017:0x1a89  */
    /* JADX WARNING: Removed duplicated region for block: B:1087:0x1ccd  */
    /* JADX WARNING: Removed duplicated region for block: B:1098:0x1d19  */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x1d65  */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x1d71  */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x1d7d  */
    /* JADX WARNING: Removed duplicated region for block: B:1120:0x1d93  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x039d A[Catch:{ Exception -> 0x04c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x06ab A[Catch:{ Exception -> 0x06b7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0var_ A[SYNTHETIC, Splitter:B:609:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x122e A[Catch:{ Exception -> 0x123a }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0159 A[SYNTHETIC, Splitter:B:71:0x0159] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0192  */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x15b4 A[SYNTHETIC, Splitter:B:842:0x15b4] */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1834  */
    /* JADX WARNING: Removed duplicated region for block: B:917:0x1865  */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x18dd  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r66, boolean r67, boolean r68, boolean r69) {
        /*
            r65 = this;
            r15 = r65
            r14 = r66
            r0 = r68
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r65, r66)
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
            java.lang.String r1 = r66.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r66.getFlags()
            java.lang.String r2 = r66.getAction()
            int[] r11 = new int[r13]
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r4 = "currentAccount"
            int r3 = r14.getIntExtra(r4, r3)
            r11[r12] = r3
            r3 = r11[r12]
            r15.switchToAccount(r3, r13)
            if (r2 == 0) goto L_0x0070
            java.lang.String r3 = "voip"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0070
            r24 = 1
            goto L_0x0072
        L_0x0070:
            r24 = 0
        L_0x0072:
            if (r69 != 0) goto L_0x0095
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r3 != 0) goto L_0x007e
            boolean r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r3 == 0) goto L_0x0095
        L_0x007e:
            r65.showPasscodeActivity()
            int r3 = r15.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            r3.saveConfig(r12)
            if (r24 != 0) goto L_0x0095
            r15.passcodeSaveIntent = r14
            r10 = r67
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x0095:
            r10 = r67
            r9 = 0
            r15.photoPathsArray = r9
            r15.videoPath = r9
            r15.sendingText = r9
            r15.documentsPathsArray = r9
            r15.documentsOriginalPathsArray = r9
            r15.documentsMimeType = r9
            r15.documentsUrisArray = r9
            r15.exportingChatUri = r9
            r15.contactsToSend = r9
            r15.contactsToSendUri = r9
            r3 = 1048576(0x100000, float:1.469368E-39)
            r1 = r1 & r3
            java.lang.String r8 = "message_id"
            r3 = 0
            if (r1 != 0) goto L_0x17fc
            if (r14 == 0) goto L_0x17fc
            java.lang.String r1 = r66.getAction()
            if (r1 == 0) goto L_0x17fc
            if (r0 != 0) goto L_0x17fc
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r7 = "hash"
            java.lang.String r5 = ""
            if (r0 == 0) goto L_0x034f
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0149
            if (r14 == 0) goto L_0x0149
            android.os.Bundle r0 = r66.getExtras()
            if (r0 == 0) goto L_0x0149
            android.os.Bundle r0 = r66.getExtras()
            java.lang.String r2 = "dialogId"
            long r18 = r0.getLong(r2, r3)
            int r0 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0137
            android.os.Bundle r0 = r66.getExtras()     // Catch:{ all -> 0x0131 }
            java.lang.String r2 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r2)     // Catch:{ all -> 0x0131 }
            if (r0 == 0) goto L_0x0135
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0131 }
            java.util.List r2 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r2)     // Catch:{ all -> 0x0131 }
            int r6 = r2.size()     // Catch:{ all -> 0x0131 }
            r13 = 0
        L_0x0102:
            if (r13 >= r6) goto L_0x0135
            java.lang.Object r22 = r2.get(r13)     // Catch:{ all -> 0x0131 }
            androidx.core.content.pm.ShortcutInfoCompat r22 = (androidx.core.content.pm.ShortcutInfoCompat) r22     // Catch:{ all -> 0x0131 }
            java.lang.String r12 = r22.getId()     // Catch:{ all -> 0x0131 }
            boolean r12 = r0.equals(r12)     // Catch:{ all -> 0x0131 }
            if (r12 == 0) goto L_0x012d
            android.content.Intent r0 = r22.getIntent()     // Catch:{ all -> 0x0131 }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x0131 }
            java.lang.String r2 = "dialogId"
            long r12 = r0.getLong(r2, r3)     // Catch:{ all -> 0x0131 }
            java.lang.String r0 = r0.getString(r7, r9)     // Catch:{ all -> 0x0129 }
            r18 = r12
            goto L_0x013f
        L_0x0129:
            r0 = move-exception
            r18 = r12
            goto L_0x0132
        L_0x012d:
            int r13 = r13 + 1
            r12 = 0
            goto L_0x0102
        L_0x0131:
            r0 = move-exception
        L_0x0132:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0135:
            r0 = r9
            goto L_0x013f
        L_0x0137:
            android.os.Bundle r0 = r66.getExtras()
            java.lang.String r0 = r0.getString(r7, r9)
        L_0x013f:
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash
            if (r2 == 0) goto L_0x0149
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x014b
        L_0x0149:
            r18 = r3
        L_0x014b:
            java.lang.String r2 = r66.getType()
            if (r2 == 0) goto L_0x0192
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0192
            android.os.Bundle r0 = r66.getExtras()     // Catch:{ Exception -> 0x018b }
            java.lang.String r1 = "android.intent.extra.STREAM"
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x018b }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x018b }
            if (r0 == 0) goto L_0x018f
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x018b }
            r5 = 0
            java.util.ArrayList r1 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r1, r5, r9, r9)     // Catch:{ Exception -> 0x018b }
            r15.contactsToSend = r1     // Catch:{ Exception -> 0x018b }
            int r1 = r1.size()     // Catch:{ Exception -> 0x018b }
            r5 = 5
            if (r1 <= r5) goto L_0x0187
            r15.contactsToSend = r9     // Catch:{ Exception -> 0x018b }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x018b }
            r1.<init>()     // Catch:{ Exception -> 0x018b }
            r15.documentsUrisArray = r1     // Catch:{ Exception -> 0x018b }
            r1.add(r0)     // Catch:{ Exception -> 0x018b }
            r15.documentsMimeType = r2     // Catch:{ Exception -> 0x018b }
            goto L_0x0318
        L_0x0187:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x018b }
            goto L_0x0318
        L_0x018b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x018f:
            r0 = 1
            goto L_0x0319
        L_0x0192:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x01a6
            java.lang.String r6 = "android.intent.extra.TEXT"
            java.lang.CharSequence r6 = r14.getCharSequenceExtra(r6)
            if (r6 == 0) goto L_0x01a6
            java.lang.String r0 = r6.toString()
        L_0x01a6:
            java.lang.String r6 = "android.intent.extra.SUBJECT"
            java.lang.String r6 = r14.getStringExtra(r6)
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 != 0) goto L_0x01dd
            java.lang.String r7 = "http://"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x01c2
            java.lang.String r7 = "https://"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x01da
        L_0x01c2:
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 != 0) goto L_0x01da
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r6)
            r7.append(r1)
            r7.append(r0)
            java.lang.String r0 = r7.toString()
        L_0x01da:
            r15.sendingText = r0
            goto L_0x01e5
        L_0x01dd:
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            if (r0 != 0) goto L_0x01e5
            r15.sendingText = r6
        L_0x01e5:
            java.lang.String r0 = "android.intent.extra.STREAM"
            android.os.Parcelable r0 = r14.getParcelableExtra(r0)
            if (r0 == 0) goto L_0x0312
            boolean r1 = r0 instanceof android.net.Uri
            if (r1 != 0) goto L_0x01f9
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x01f9:
            r1 = r0
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x0206
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)
            if (r0 == 0) goto L_0x0206
            r6 = 1
            goto L_0x0207
        L_0x0206:
            r6 = 0
        L_0x0207:
            if (r6 != 0) goto L_0x0310
            if (r1 == 0) goto L_0x0310
            if (r2 == 0) goto L_0x0215
            java.lang.String r0 = "image/"
            boolean r0 = r2.startsWith(r0)
            if (r0 != 0) goto L_0x0225
        L_0x0215:
            java.lang.String r0 = r1.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r0 = r0.endsWith(r7)
            if (r0 == 0) goto L_0x023e
        L_0x0225:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x0230
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x0230:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0310
        L_0x023e:
            java.lang.String r7 = r1.toString()
            int r0 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x02b7
            if (r7 == 0) goto L_0x02b7
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0260
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "export path = "
            r0.append(r12)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0260:
            r12 = 0
            r0 = r11[r12]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r0 = r0.exportUri
            java.lang.String r12 = org.telegram.messenger.MediaController.getFileName(r1)
            java.lang.String r12 = org.telegram.messenger.FileLoader.fixFileName(r12)
            java.util.Iterator r13 = r0.iterator()
        L_0x0275:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x02a1
            java.lang.Object r0 = r13.next()
            java.lang.String r0 = (java.lang.String) r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x029c }
            java.util.regex.Matcher r22 = r0.matcher(r7)     // Catch:{ Exception -> 0x029c }
            boolean r22 = r22.find()     // Catch:{ Exception -> 0x029c }
            if (r22 != 0) goto L_0x0299
            java.util.regex.Matcher r0 = r0.matcher(r12)     // Catch:{ Exception -> 0x029c }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x029c }
            if (r0 == 0) goto L_0x0275
        L_0x0299:
            r15.exportingChatUri = r1     // Catch:{ Exception -> 0x029c }
            goto L_0x02a1
        L_0x029c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0275
        L_0x02a1:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x02b7
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x02b7
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r7.endsWith(r0)
            if (r0 == 0) goto L_0x02b7
            r15.exportingChatUri = r1
        L_0x02b7:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x0310
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r0 == 0) goto L_0x02fe
            java.lang.String r7 = "file:"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x02cf
            java.lang.String r7 = "file://"
            java.lang.String r0 = r0.replace(r7, r5)
        L_0x02cf:
            if (r2 == 0) goto L_0x02dd
            java.lang.String r5 = "video/"
            boolean r2 = r2.startsWith(r5)
            if (r2 == 0) goto L_0x02dd
            r15.videoPath = r0
            goto L_0x0310
        L_0x02dd:
            java.util.ArrayList<java.lang.String> r2 = r15.documentsPathsArray
            if (r2 != 0) goto L_0x02ef
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsPathsArray = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsOriginalPathsArray = r2
        L_0x02ef:
            java.util.ArrayList<java.lang.String> r2 = r15.documentsPathsArray
            r2.add(r0)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x0310
        L_0x02fe:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            if (r0 != 0) goto L_0x0309
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsUrisArray = r0
        L_0x0309:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            r0.add(r1)
            r15.documentsMimeType = r2
        L_0x0310:
            r0 = r6
            goto L_0x0319
        L_0x0312:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0318
            goto L_0x018f
        L_0x0318:
            r0 = 0
        L_0x0319:
            if (r0 == 0) goto L_0x0325
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x0325:
            r56 = r3
            r3 = r8
            r68 = r9
            r1 = r68
            r2 = r1
            r10 = r11
            r7 = r15
            r0 = -1
            r5 = -1
            r6 = 0
            r8 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r33 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r64 = 0
            r11 = r2
            r9 = 0
            goto L_0x1828
        L_0x034f:
            java.lang.String r0 = r66.getAction()
            java.lang.String r6 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x04df
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r1 = r66.getType()     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x039a
            r2 = 0
        L_0x0368:
            int r6 = r0.size()     // Catch:{ Exception -> 0x04c3 }
            if (r2 >= r6) goto L_0x0392
            java.lang.Object r6 = r0.get(r2)     // Catch:{ Exception -> 0x04c3 }
            android.os.Parcelable r6 = (android.os.Parcelable) r6     // Catch:{ Exception -> 0x04c3 }
            boolean r7 = r6 instanceof android.net.Uri     // Catch:{ Exception -> 0x04c3 }
            if (r7 != 0) goto L_0x0380
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04c3 }
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x04c3 }
        L_0x0380:
            android.net.Uri r6 = (android.net.Uri) r6     // Catch:{ Exception -> 0x04c3 }
            if (r6 == 0) goto L_0x038f
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r6)     // Catch:{ Exception -> 0x04c3 }
            if (r6 == 0) goto L_0x038f
            r0.remove(r2)     // Catch:{ Exception -> 0x04c3 }
            int r2 = r2 + -1
        L_0x038f:
            r6 = 1
            int r2 = r2 + r6
            goto L_0x0368
        L_0x0392:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x04c3 }
            if (r2 == 0) goto L_0x039a
            r2 = r9
            goto L_0x039b
        L_0x039a:
            r2 = r0
        L_0x039b:
            if (r2 == 0) goto L_0x04c7
            if (r1 == 0) goto L_0x03dc
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x03dc
            r0 = 0
        L_0x03a8:
            int r1 = r2.size()     // Catch:{ Exception -> 0x04c3 }
            if (r0 >= r1) goto L_0x04c1
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x04c3 }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x04c3 }
            boolean r5 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x04c3 }
            if (r5 != 0) goto L_0x03c0
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04c3 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x04c3 }
        L_0x03c0:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x04c3 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r5 = r15.photoPathsArray     // Catch:{ Exception -> 0x04c3 }
            if (r5 != 0) goto L_0x03cd
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x04c3 }
            r5.<init>()     // Catch:{ Exception -> 0x04c3 }
            r15.photoPathsArray = r5     // Catch:{ Exception -> 0x04c3 }
        L_0x03cd:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r5 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x04c3 }
            r5.<init>()     // Catch:{ Exception -> 0x04c3 }
            r5.uri = r1     // Catch:{ Exception -> 0x04c3 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x04c3 }
            r1.add(r5)     // Catch:{ Exception -> 0x04c3 }
            int r0 = r0 + 1
            goto L_0x03a8
        L_0x03dc:
            r6 = 0
            r0 = r11[r6]     // Catch:{ Exception -> 0x04c3 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x04c3 }
            java.util.Set<java.lang.String> r6 = r0.exportUri     // Catch:{ Exception -> 0x04c3 }
            r7 = 0
        L_0x03e6:
            int r0 = r2.size()     // Catch:{ Exception -> 0x04c3 }
            if (r7 >= r0) goto L_0x04c1
            java.lang.Object r0 = r2.get(r7)     // Catch:{ Exception -> 0x04c3 }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x04c3 }
            boolean r12 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x04c3 }
            if (r12 != 0) goto L_0x03fe
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04c3 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x04c3 }
        L_0x03fe:
            r12 = r0
            android.net.Uri r12 = (android.net.Uri) r12     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.getPath(r12)     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04c3 }
            if (r0 != 0) goto L_0x040d
            r3 = r13
            goto L_0x040e
        L_0x040d:
            r3 = r0
        L_0x040e:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x0426
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c3 }
            r0.<init>()     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r4 = "export path = "
            r0.append(r4)     // Catch:{ Exception -> 0x04c3 }
            r0.append(r3)     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04c3 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x04c3 }
        L_0x0426:
            if (r3 == 0) goto L_0x047c
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x04c3 }
            if (r0 != 0) goto L_0x047c
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r12)     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r4 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x04c3 }
            java.util.Iterator r22 = r6.iterator()     // Catch:{ Exception -> 0x04c3 }
        L_0x0438:
            boolean r0 = r22.hasNext()     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x0465
            java.lang.Object r0 = r22.next()     // Catch:{ Exception -> 0x04c3 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x04c3 }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x0460 }
            java.util.regex.Matcher r23 = r0.matcher(r3)     // Catch:{ Exception -> 0x0460 }
            boolean r23 = r23.find()     // Catch:{ Exception -> 0x0460 }
            if (r23 != 0) goto L_0x045c
            java.util.regex.Matcher r0 = r0.matcher(r4)     // Catch:{ Exception -> 0x0460 }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x0460 }
            if (r0 == 0) goto L_0x0438
        L_0x045c:
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x0460 }
            r0 = 1
            goto L_0x0466
        L_0x0460:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04c3 }
            goto L_0x0438
        L_0x0465:
            r0 = 0
        L_0x0466:
            if (r0 == 0) goto L_0x0469
            goto L_0x04bb
        L_0x0469:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r3.startsWith(r0)     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x047c
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r3.endsWith(r0)     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x047c
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x04c3 }
            goto L_0x04bb
        L_0x047c:
            if (r13 == 0) goto L_0x04a9
            java.lang.String r0 = "file:"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x04c3 }
            if (r0 == 0) goto L_0x048c
            java.lang.String r0 = "file://"
            java.lang.String r13 = r13.replace(r0, r5)     // Catch:{ Exception -> 0x04c3 }
        L_0x048c:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04c3 }
            if (r0 != 0) goto L_0x049e
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04c3 }
            r0.<init>()     // Catch:{ Exception -> 0x04c3 }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x04c3 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04c3 }
            r0.<init>()     // Catch:{ Exception -> 0x04c3 }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x04c3 }
        L_0x049e:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04c3 }
            r0.add(r13)     // Catch:{ Exception -> 0x04c3 }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x04c3 }
            r0.add(r3)     // Catch:{ Exception -> 0x04c3 }
            goto L_0x04bb
        L_0x04a9:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04c3 }
            if (r0 != 0) goto L_0x04b4
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04c3 }
            r0.<init>()     // Catch:{ Exception -> 0x04c3 }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x04c3 }
        L_0x04b4:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04c3 }
            r0.add(r12)     // Catch:{ Exception -> 0x04c3 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x04c3 }
        L_0x04bb:
            int r7 = r7 + 1
            r3 = 0
            goto L_0x03e6
        L_0x04c1:
            r0 = 0
            goto L_0x04c8
        L_0x04c3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04c7:
            r0 = 1
        L_0x04c8:
            if (r0 == 0) goto L_0x04d4
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x04d4:
            r3 = r8
            r10 = r11
            r8 = r14
            r7 = r15
            r9 = 0
            r25 = -1
            r56 = 0
            goto L_0x1805
        L_0x04df:
            java.lang.String r0 = r66.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x16a2
            android.net.Uri r0 = r66.getData()
            if (r0 == 0) goto L_0x1656
            java.lang.String r2 = r0.getScheme()
            java.lang.String r3 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r4 = "phone"
            if (r2 == 0) goto L_0x143e
            r2.hashCode()
            int r6 = r2.hashCode()
            switch(r6) {
                case 3699: goto L_0x051d;
                case 3213448: goto L_0x0512;
                case 99617003: goto L_0x0507;
                default: goto L_0x0505;
            }
        L_0x0505:
            r6 = -1
            goto L_0x0527
        L_0x0507:
            java.lang.String r6 = "https"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0510
            goto L_0x0505
        L_0x0510:
            r6 = 2
            goto L_0x0527
        L_0x0512:
            java.lang.String r6 = "http"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x051b
            goto L_0x0505
        L_0x051b:
            r6 = 1
            goto L_0x0527
        L_0x051d:
            java.lang.String r6 = "tg"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0526
            goto L_0x0505
        L_0x0526:
            r6 = 0
        L_0x0527:
            java.lang.String r12 = "thread"
            r22 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r6) {
                case 0: goto L_0x09c5;
                case 1: goto L_0x0530;
                case 2: goto L_0x0530;
                default: goto L_0x052e;
            }
        L_0x052e:
            goto L_0x143e
        L_0x0530:
            java.lang.String r6 = r0.getHost()
            java.lang.String r6 = r6.toLowerCase()
            java.lang.String r9 = "telegram.me"
            boolean r9 = r6.equals(r9)
            if (r9 != 0) goto L_0x0558
            java.lang.String r9 = "t.me"
            boolean r9 = r6.equals(r9)
            if (r9 != 0) goto L_0x0558
            java.lang.String r9 = "telegram.dog"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x0551
            goto L_0x0558
        L_0x0551:
            r28 = r2
            r27 = r11
        L_0x0555:
            r9 = 0
            goto L_0x1442
        L_0x0558:
            java.lang.String r6 = r0.getPath()
            if (r6 == 0) goto L_0x095c
            int r9 = r6.length()
            r13 = 1
            if (r9 <= r13) goto L_0x095c
            java.lang.String r6 = r6.substring(r13)
            java.lang.String r9 = "bg/"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x06d0
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r9 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r9.<init>()
            r1.settings = r9
            java.lang.String r9 = "bg/"
            java.lang.String r5 = r6.replace(r9, r5)
            r1.slug = r5
            if (r5 == 0) goto L_0x05a2
            int r5 = r5.length()
            r6 = 6
            if (r5 != r6) goto L_0x05a2
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x059c }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x059c }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x059c }
            r5 = r5 | r22
            r0.background_color = r5     // Catch:{ Exception -> 0x059c }
        L_0x059c:
            r5 = 0
            r1.slug = r5
        L_0x059f:
            r9 = -1
            goto L_0x06b7
        L_0x05a2:
            java.lang.String r5 = r1.slug
            if (r5 == 0) goto L_0x0601
            int r5 = r5.length()
            r6 = 13
            if (r5 != r6) goto L_0x0601
            java.lang.String r5 = r1.slug
            r6 = 6
            char r5 = r5.charAt(r6)
            r9 = 45
            if (r5 != r9) goto L_0x0601
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x05e5 }
            java.lang.String r9 = r1.slug     // Catch:{ Exception -> 0x05e5 }
            r12 = 0
            java.lang.String r9 = r9.substring(r12, r6)     // Catch:{ Exception -> 0x05e5 }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x05e5 }
            r6 = r9 | r22
            r5.background_color = r6     // Catch:{ Exception -> 0x05e5 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x05e5 }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x05e5 }
            r9 = 7
            java.lang.String r6 = r6.substring(r9)     // Catch:{ Exception -> 0x05e5 }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x05e5 }
            r6 = r6 | r22
            r5.second_background_color = r6     // Catch:{ Exception -> 0x05e5 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x05e5 }
            r6 = 45
            r5.rotation = r6     // Catch:{ Exception -> 0x05e5 }
        L_0x05e5:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x05fd }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x05fd }
            if (r5 != 0) goto L_0x05fd
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x05fd }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x05fd }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x05fd }
            r5.rotation = r0     // Catch:{ Exception -> 0x05fd }
        L_0x05fd:
            r5 = 0
            r1.slug = r5
            goto L_0x059f
        L_0x0601:
            java.lang.String r5 = "mode"
            java.lang.String r5 = r0.getQueryParameter(r5)
            if (r5 == 0) goto L_0x063e
            java.lang.String r5 = r5.toLowerCase()
            java.lang.String r6 = " "
            java.lang.String[] r5 = r5.split(r6)
            if (r5 == 0) goto L_0x063e
            int r6 = r5.length
            if (r6 <= 0) goto L_0x063e
            r6 = 0
        L_0x0619:
            int r9 = r5.length
            if (r6 >= r9) goto L_0x063e
            r9 = r5[r6]
            java.lang.String r12 = "blur"
            boolean r9 = r12.equals(r9)
            if (r9 == 0) goto L_0x062c
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r12 = 1
            r9.blur = r12
            goto L_0x063b
        L_0x062c:
            r12 = 1
            r9 = r5[r6]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x063b
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r9.motion = r12
        L_0x063b:
            int r6 = r6 + 1
            goto L_0x0619
        L_0x063e:
            java.lang.String r5 = "intensity"
            java.lang.String r5 = r0.getQueryParameter(r5)
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x0657
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r5 = r5.intValue()
            r6.intensity = r5
            goto L_0x065d
        L_0x0657:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 50
            r5.intensity = r6
        L_0x065d:
            java.lang.String r5 = "bg_color"
            java.lang.String r5 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x0698 }
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x0698 }
            if (r6 != 0) goto L_0x069a
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0698 }
            r9 = 6
            r12 = 0
            java.lang.String r13 = r5.substring(r12, r9)     // Catch:{ Exception -> 0x0698 }
            r12 = 16
            int r13 = java.lang.Integer.parseInt(r13, r12)     // Catch:{ Exception -> 0x0698 }
            r12 = r13 | r22
            r6.background_color = r12     // Catch:{ Exception -> 0x0698 }
            int r6 = r5.length()     // Catch:{ Exception -> 0x0698 }
            if (r6 <= r9) goto L_0x0698
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0698 }
            r9 = 7
            java.lang.String r5 = r5.substring(r9)     // Catch:{ Exception -> 0x0698 }
            r9 = 16
            int r5 = java.lang.Integer.parseInt(r5, r9)     // Catch:{ Exception -> 0x0698 }
            r5 = r5 | r22
            r6.second_background_color = r5     // Catch:{ Exception -> 0x0698 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0698 }
            r6 = 45
            r5.rotation = r6     // Catch:{ Exception -> 0x0698 }
        L_0x0698:
            r9 = -1
            goto L_0x069f
        L_0x069a:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0698 }
            r9 = -1
            r5.background_color = r9     // Catch:{ Exception -> 0x069f }
        L_0x069f:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x06b7 }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x06b7 }
            if (r5 != 0) goto L_0x06b7
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x06b7 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x06b7 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x06b7 }
            r5.rotation = r0     // Catch:{ Exception -> 0x06b7 }
        L_0x06b7:
            r32 = r1
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            goto L_0x0973
        L_0x06d0:
            r9 = -1
            java.lang.String r13 = "login/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0711
            java.lang.String r0 = "login/"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x06f9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x06fa
        L_0x06f9:
            r0 = 0
        L_0x06fa:
            r31 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0971
        L_0x0711:
            java.lang.String r13 = "joinchat/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0721
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r6.replace(r0, r5)
            goto L_0x095d
        L_0x0721:
            java.lang.String r13 = "+"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0731
            java.lang.String r0 = "+"
            java.lang.String r0 = r6.replace(r0, r5)
            goto L_0x095d
        L_0x0731:
            java.lang.String r13 = "addstickers/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0743
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r6.replace(r0, r5)
            r1 = r0
            r0 = 0
            goto L_0x095e
        L_0x0743:
            java.lang.String r13 = "msg/"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x08f0
            java.lang.String r13 = "share/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x0755
            goto L_0x08f0
        L_0x0755:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0776
            java.lang.String r1 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r7)
            r28 = r0
            r22 = r1
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r27 = 0
            goto L_0x096b
        L_0x0776:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0797
            r0 = 12
            java.lang.String r0 = r6.substring(r0)
            r29 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            goto L_0x096d
        L_0x0797:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x07ba
            r0 = 9
            java.lang.String r0 = r6.substring(r0)
            r30 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            goto L_0x096f
        L_0x07ba:
            java.lang.String r1 = "c/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0824
            java.util.List r1 = r0.getPathSegments()
            int r5 = r1.size()
            r6 = 3
            if (r5 != r6) goto L_0x0801
            r5 = 1
            java.lang.Object r6 = r1.get(r5)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r6)
            r13 = 2
            java.lang.Object r1 = r1.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 == 0) goto L_0x07ef
            int r6 = r5.intValue()
            if (r6 != 0) goto L_0x07f1
        L_0x07ef:
            r1 = 0
            r5 = 0
        L_0x07f1:
            java.lang.String r0 = r0.getQueryParameter(r12)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r6 = r0.intValue()
            if (r6 != 0) goto L_0x0805
            r0 = 0
            goto L_0x0805
        L_0x0801:
            r13 = 2
            r0 = 0
            r1 = 0
            r5 = 0
        L_0x0805:
            r35 = r0
            r33 = r1
            r34 = r5
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0979
        L_0x0824:
            r13 = 2
            int r1 = r6.length()
            r5 = 1
            if (r1 < r5) goto L_0x095c
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r5 = r0.getPathSegments()
            r1.<init>(r5)
            int r5 = r1.size()
            if (r5 <= 0) goto L_0x084e
            r5 = 0
            java.lang.Object r6 = r1.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r9 = "s"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x084f
            r1.remove(r5)
            goto L_0x084f
        L_0x084e:
            r5 = 0
        L_0x084f:
            int r6 = r1.size()
            if (r6 <= 0) goto L_0x0875
            java.lang.Object r6 = r1.get(r5)
            r5 = r6
            java.lang.String r5 = (java.lang.String) r5
            int r6 = r1.size()
            r9 = 1
            if (r6 <= r9) goto L_0x0873
            java.lang.Object r1 = r1.get(r9)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 != 0) goto L_0x0877
        L_0x0873:
            r1 = 0
            goto L_0x0877
        L_0x0875:
            r1 = 0
            r5 = 0
        L_0x0877:
            java.lang.String r6 = "start"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r9 = "startgroup"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r13 = "game"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r68 = r1
            java.lang.String r1 = "voicechat"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r22 = r12.intValue()
            if (r22 != 0) goto L_0x08a4
            r22 = r1
            r12 = 0
            goto L_0x08a6
        L_0x08a4:
            r22 = r1
        L_0x08a6:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            r33 = r68
            if (r1 != 0) goto L_0x08d2
            r35 = r12
            r23 = r13
            r27 = r22
            r0 = 0
            r1 = 0
            r13 = 0
            r22 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r36 = 0
            goto L_0x08eb
        L_0x08d2:
            r36 = r0
            r35 = r12
            r23 = r13
            r27 = r22
            r0 = 0
            r1 = 0
            r13 = 0
            r22 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
        L_0x08eb:
            r12 = r9
            r9 = r6
            r6 = 0
            goto L_0x097b
        L_0x08f0:
            java.lang.String r6 = "url"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 != 0) goto L_0x08fa
            goto L_0x08fb
        L_0x08fa:
            r5 = r6
        L_0x08fb:
            java.lang.String r6 = "text"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 == 0) goto L_0x0931
            int r6 = r5.length()
            if (r6 <= 0) goto L_0x091a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r1)
            java.lang.String r5 = r6.toString()
            r6 = 1
            goto L_0x091b
        L_0x091a:
            r6 = 0
        L_0x091b:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r5 = "text"
            java.lang.String r0 = r0.getQueryParameter(r5)
            r9.append(r0)
            java.lang.String r5 = r9.toString()
            goto L_0x0932
        L_0x0931:
            r6 = 0
        L_0x0932:
            int r0 = r5.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x0942
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r5.substring(r9, r0)
            goto L_0x0944
        L_0x0942:
            r9 = 0
            r0 = r5
        L_0x0944:
            boolean r5 = r0.endsWith(r1)
            if (r5 == 0) goto L_0x0955
            int r5 = r0.length()
            r12 = 1
            int r5 = r5 - r12
            java.lang.String r0 = r0.substring(r9, r5)
            goto L_0x0944
        L_0x0955:
            r13 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r9 = 0
            r12 = 0
            goto L_0x0963
        L_0x095c:
            r0 = 0
        L_0x095d:
            r1 = 0
        L_0x095e:
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x0963:
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
        L_0x096b:
            r29 = 0
        L_0x096d:
            r30 = 0
        L_0x096f:
            r31 = 0
        L_0x0971:
            r32 = 0
        L_0x0973:
            r33 = 0
            r34 = 0
            r35 = 0
        L_0x0979:
            r36 = 0
        L_0x097b:
            r26 = r5
            r10 = r9
            r47 = r23
            r55 = r27
            r5 = r28
            r49 = r29
            r54 = r30
            r51 = r31
            r53 = r32
            r44 = r34
            r45 = r35
            r46 = r36
            r9 = 0
            r20 = 6
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
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
            r48 = 0
            r50 = 0
            r52 = 0
            r28 = r2
            r27 = r11
            r11 = r6
            r6 = r1
            r1 = r22
            r22 = r33
            r33 = 0
            goto L_0x1486
        L_0x09c5:
            java.lang.String r6 = r0.toString()
            java.lang.String r9 = "tg:resolve"
            boolean r9 = r6.startsWith(r9)
            java.lang.String r13 = "bot_id"
            java.lang.String r10 = "payload"
            r27 = r11
            java.lang.String r11 = "scope"
            r28 = r2
            java.lang.String r2 = "tg://telegram.org"
            if (r9 != 0) goto L_0x1304
            java.lang.String r9 = "tg://resolve"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x09e7
            goto L_0x1304
        L_0x09e7:
            java.lang.String r9 = "tg:privatepost"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x1265
            java.lang.String r9 = "tg://privatepost"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x09f9
            goto L_0x1265
        L_0x09f9:
            java.lang.String r9 = "tg:bg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x10e8
            java.lang.String r9 = "tg://bg"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a0b
            goto L_0x10e8
        L_0x0a0b:
            java.lang.String r9 = "tg:join"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x108c
            java.lang.String r9 = "tg://join"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a1d
            goto L_0x108c
        L_0x0a1d:
            java.lang.String r9 = "tg:addstickers"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x1071
            java.lang.String r9 = "tg://addstickers"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a2f
            goto L_0x1071
        L_0x0a2f:
            java.lang.String r9 = "tg:msg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x0fe6
            java.lang.String r9 = "tg://msg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x0fe6
            java.lang.String r9 = "tg://share"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x0fe6
            java.lang.String r9 = "tg:share"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a51
            goto L_0x0fe6
        L_0x0a51:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0fca
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0a63
            goto L_0x0fca
        L_0x0a63:
            java.lang.String r1 = "tg:login"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://login"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0a75
            goto L_0x0var_
        L_0x0a75:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0a87
            goto L_0x0var_
        L_0x0a87:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0e5d
            java.lang.String r1 = "tg://passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0e5d
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0aa1
            goto L_0x0e5d
        L_0x0aa1:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0e0c
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0ab3
            goto L_0x0e0c
        L_0x0ab3:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0db1
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0ac5
            goto L_0x0db1
        L_0x0ac5:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0d12
            java.lang.String r1 = "tg://settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0ad7
            goto L_0x0d12
        L_0x0ad7:
            java.lang.String r1 = "tg:search"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0ccd
            java.lang.String r1 = "tg://search"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0ae9
            goto L_0x0ccd
        L_0x0ae9:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0cb2
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0afb
            goto L_0x0cb2
        L_0x0afb:
            java.lang.String r1 = "tg:call"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0bf6
            java.lang.String r1 = "tg://call"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b0d
            goto L_0x0bf6
        L_0x0b0d:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x0bcf
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x0b1f
            goto L_0x0bcf
        L_0x0b1f:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x0b86
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x0b30
            goto L_0x0b86
        L_0x0b30:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r5)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0b49
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r1)
        L_0x0b49:
            r50 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            goto L_0x10dc
        L_0x0b86:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r43 = r0
            r42 = r1
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 1
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            goto L_0x10ce
        L_0x0bcf:
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            r39 = 1
            goto L_0x10c6
        L_0x0bf6:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x0555
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r3)
            java.lang.String r1 = "extra_force_call"
            r2 = 1
            r0.putExtra(r1, r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$X0aF1Xm_mIISEfWg-pwwTqXnE3w r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$X0aF1Xm_mIISEfWg-pwwTqXnE3w
            r1.<init>(r0)
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r5)
            r0 = 0
            r1 = 0
            r2 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "name"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r6 = 0
            java.util.List r9 = r15.findContacts(r2, r0, r6)
            boolean r10 = r9.isEmpty()
            if (r10 == 0) goto L_0x0c5b
            if (r0 == 0) goto L_0x0c5b
            r11 = r0
            r10 = r2
            r0 = 1
            r1 = 0
            r2 = 0
            r5 = 0
            r9 = 0
            goto L_0x0CLASSNAME
        L_0x0c5b:
            int r0 = r9.size()
            r10 = 1
            if (r0 != r10) goto L_0x0c6b
            java.lang.Object r0 = r9.get(r6)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            int r0 = r0.user_id
            goto L_0x0c6c
        L_0x0c6b:
            r0 = 0
        L_0x0c6c:
            if (r0 != 0) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0CLASSNAME
            r5 = r2
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = 0
        L_0x0CLASSNAME:
            java.lang.String r2 = "video"
            boolean r1 = r2.equalsIgnoreCase(r1)
            r2 = r1 ^ 1
            r9 = r5
            r6 = 1
            r10 = 0
            r11 = 0
            r5 = r1
            r1 = r0
            r0 = 0
        L_0x0CLASSNAME:
            r38 = r0
            r29 = r1
            r34 = r2
            r35 = r5
            r36 = r6
            r41 = r9
            r42 = r10
            r43 = r11
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            goto L_0x10ce
        L_0x0cb2:
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 1
            goto L_0x10ba
        L_0x0ccd:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0ce9
            java.lang.String r5 = r0.trim()
        L_0x0ce9:
            r40 = r5
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            goto L_0x10c8
        L_0x0d12:
            java.lang.String r0 = "themes"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0d33
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 2
            goto L_0x10b8
        L_0x0d33:
            java.lang.String r0 = "devices"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0d54
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 3
            goto L_0x10b8
        L_0x0d54:
            java.lang.String r0 = "folders"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0d76
            r0 = 4
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 4
            goto L_0x10b8
        L_0x0d76:
            java.lang.String r0 = "change_number"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0d98
            r0 = 5
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 5
            goto L_0x10b8
        L_0x0d98:
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 1
            goto L_0x10b8
        L_0x0db1:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r54 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            goto L_0x10e4
        L_0x0e0c:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r49 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            goto L_0x10da
        L_0x0e5d:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r2)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r11)
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 != 0) goto L_0x0ea0
            java.lang.String r5 = "{"
            boolean r5 = r2.startsWith(r5)
            if (r5 == 0) goto L_0x0ea0
            java.lang.String r5 = "}"
            boolean r5 = r2.endsWith(r5)
            if (r5 == 0) goto L_0x0ea0
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "nonce"
            r1.put(r6, r5)
            goto L_0x0ea7
        L_0x0ea0:
            java.lang.String r5 = r0.getQueryParameter(r10)
            r1.put(r10, r5)
        L_0x0ea7:
            java.lang.String r5 = r0.getQueryParameter(r13)
            r1.put(r13, r5)
            r1.put(r11, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r5 = "public_key"
            r1.put(r5, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            r48 = r1
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            goto L_0x10d8
        L_0x0var_:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "chat_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r8)
            if (r1 == 0) goto L_0x0var_
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0var_ }
            goto L_0x0var_
        L_0x0var_:
            if (r2 == 0) goto L_0x0var_
            int r1 = java.lang.Integer.parseInt(r2)     // Catch:{ NumberFormatException -> 0x0var_ }
            r2 = r1
            r1 = 0
            goto L_0x0var_
        L_0x0var_:
            r1 = 0
        L_0x0var_:
            r2 = 0
        L_0x0var_:
            if (r0 == 0) goto L_0x0f3a
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0f3a }
            goto L_0x0f3b
        L_0x0f3a:
            r0 = 0
        L_0x0f3b:
            r31 = r0
            r29 = r1
            r30 = r2
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
            goto L_0x10b6
        L_0x0var_:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "code"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0var_
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0var_
        L_0x0var_:
            r0 = 0
        L_0x0var_:
            r51 = r0
            r52 = r1
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r20 = 6
            r22 = 0
            r26 = 0
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
            r50 = 0
            goto L_0x10e0
        L_0x0fca:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r7)
            r5 = r0
            r0 = 0
            goto L_0x10a4
        L_0x0fe6:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r6 = "tg://msg"
            java.lang.String r0 = r0.replace(r6, r2)
            java.lang.String r6 = "tg://share"
            java.lang.String r0 = r0.replace(r6, r2)
            java.lang.String r6 = "tg:share"
            java.lang.String r0 = r0.replace(r6, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "url"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 != 0) goto L_0x100c
            goto L_0x100d
        L_0x100c:
            r5 = r2
        L_0x100d:
            java.lang.String r2 = "text"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x1043
            int r2 = r5.length()
            if (r2 <= 0) goto L_0x102c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            r2.append(r1)
            java.lang.String r5 = r2.toString()
            r2 = 1
            goto L_0x102d
        L_0x102c:
            r2 = 0
        L_0x102d:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            java.lang.String r5 = "text"
            java.lang.String r0 = r0.getQueryParameter(r5)
            r6.append(r0)
            java.lang.String r5 = r6.toString()
            goto L_0x1044
        L_0x1043:
            r2 = 0
        L_0x1044:
            int r0 = r5.length()
            r6 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r6) goto L_0x1054
            r0 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            java.lang.String r0 = r5.substring(r6, r0)
            goto L_0x1056
        L_0x1054:
            r6 = 0
            r0 = r5
        L_0x1056:
            boolean r5 = r0.endsWith(r1)
            if (r5 == 0) goto L_0x1067
            int r5 = r0.length()
            r9 = 1
            int r5 = r5 - r9
            java.lang.String r0 = r0.substring(r6, r5)
            goto L_0x1056
        L_0x1067:
            r13 = r0
            r11 = r2
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            goto L_0x10aa
        L_0x1071:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r6 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            goto L_0x10a5
        L_0x108c:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r1 = 0
            r5 = 0
        L_0x10a4:
            r6 = 0
        L_0x10a5:
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x10aa:
            r20 = 6
            r22 = 0
            r26 = 0
            r29 = 0
            r30 = 0
            r31 = 0
        L_0x10b6:
            r32 = 0
        L_0x10b8:
            r33 = 0
        L_0x10ba:
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x10c6:
            r40 = 0
        L_0x10c8:
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x10ce:
            r44 = 0
            r45 = 0
            r46 = 0
            r47 = 0
            r48 = 0
        L_0x10d8:
            r49 = 0
        L_0x10da:
            r50 = 0
        L_0x10dc:
            r51 = 0
            r52 = 0
        L_0x10e0:
            r53 = 0
            r54 = 0
        L_0x10e4:
            r55 = 0
            goto L_0x1486
        L_0x10e8:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "slug"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
            if (r2 != 0) goto L_0x1116
            java.lang.String r2 = "color"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
        L_0x1116:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x1136
            int r2 = r2.length()
            r5 = 6
            if (r2 != r5) goto L_0x1136
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x112f }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x112f }
            r5 = 16
            int r2 = java.lang.Integer.parseInt(r2, r5)     // Catch:{ Exception -> 0x112f }
            r2 = r2 | r22
            r0.background_color = r2     // Catch:{ Exception -> 0x112f }
        L_0x112f:
            r2 = 0
            r1.slug = r2
            r9 = r2
        L_0x1133:
            r10 = 6
            goto L_0x123a
        L_0x1136:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x1195
            int r2 = r2.length()
            r5 = 13
            if (r2 != r5) goto L_0x1195
            java.lang.String r2 = r1.slug
            r5 = 6
            char r2 = r2.charAt(r5)
            r6 = 45
            if (r2 != r6) goto L_0x1195
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x1179 }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x1179 }
            r9 = 0
            java.lang.String r6 = r6.substring(r9, r5)     // Catch:{ Exception -> 0x1179 }
            r5 = 16
            int r6 = java.lang.Integer.parseInt(r6, r5)     // Catch:{ Exception -> 0x1179 }
            r5 = r6 | r22
            r2.background_color = r5     // Catch:{ Exception -> 0x1179 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x1179 }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x1179 }
            r6 = 7
            java.lang.String r5 = r5.substring(r6)     // Catch:{ Exception -> 0x1179 }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x1179 }
            r5 = r5 | r22
            r2.second_background_color = r5     // Catch:{ Exception -> 0x1179 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x1179 }
            r5 = 45
            r2.rotation = r5     // Catch:{ Exception -> 0x1179 }
        L_0x1179:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x1191 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1191 }
            if (r2 != 0) goto L_0x1191
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x1191 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1191 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x1191 }
            r2.rotation = r0     // Catch:{ Exception -> 0x1191 }
        L_0x1191:
            r9 = 0
            r1.slug = r9
            goto L_0x1133
        L_0x1195:
            r9 = 0
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x11d3
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r5 = " "
            java.lang.String[] r2 = r2.split(r5)
            if (r2 == 0) goto L_0x11d3
            int r5 = r2.length
            if (r5 <= 0) goto L_0x11d3
            r5 = 0
        L_0x11ae:
            int r6 = r2.length
            if (r5 >= r6) goto L_0x11d3
            r6 = r2[r5]
            java.lang.String r10 = "blur"
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x11c1
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings
            r10 = 1
            r6.blur = r10
            goto L_0x11d0
        L_0x11c1:
            r10 = 1
            r6 = r2[r5]
            java.lang.String r11 = "motion"
            boolean r6 = r11.equals(r6)
            if (r6 == 0) goto L_0x11d0
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings
            r6.motion = r10
        L_0x11d0:
            int r5 = r5 + 1
            goto L_0x11ae
        L_0x11d3:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            java.lang.String r5 = "intensity"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r5 = r5.intValue()
            r2.intensity = r5
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x1221 }
            boolean r5 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x1221 }
            if (r5 != 0) goto L_0x1221
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x1221 }
            r6 = 0
            r10 = 6
            java.lang.String r11 = r2.substring(r6, r10)     // Catch:{ Exception -> 0x1222 }
            r6 = 16
            int r11 = java.lang.Integer.parseInt(r11, r6)     // Catch:{ Exception -> 0x1222 }
            r6 = r11 | r22
            r5.background_color = r6     // Catch:{ Exception -> 0x1222 }
            int r5 = r2.length()     // Catch:{ Exception -> 0x1222 }
            if (r5 <= r10) goto L_0x1222
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x1222 }
            r6 = 7
            java.lang.String r2 = r2.substring(r6)     // Catch:{ Exception -> 0x1222 }
            r6 = 16
            int r2 = java.lang.Integer.parseInt(r2, r6)     // Catch:{ Exception -> 0x1222 }
            r2 = r2 | r22
            r5.second_background_color = r2     // Catch:{ Exception -> 0x1222 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x1222 }
            r5 = 45
            r2.rotation = r5     // Catch:{ Exception -> 0x1222 }
            goto L_0x1222
        L_0x1221:
            r10 = 6
        L_0x1222:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x123a }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x123a }
            if (r2 != 0) goto L_0x123a
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x123a }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x123a }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x123a }
            r2.rotation = r0     // Catch:{ Exception -> 0x123a }
        L_0x123a:
            r53 = r1
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r22 = r13
            r26 = r22
            r40 = r26
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r47 = r46
            r48 = r47
            r49 = r48
            r50 = r49
            r51 = r50
            r52 = r51
            r54 = r52
            goto L_0x12fd
        L_0x1265:
            r9 = 0
            r10 = 6
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r1 = "channel"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r2 = r5.intValue()
            if (r2 == 0) goto L_0x1297
            int r2 = r1.intValue()
            if (r2 != 0) goto L_0x1299
        L_0x1297:
            r1 = r9
            r5 = r1
        L_0x1299:
            java.lang.String r2 = r0.getQueryParameter(r12)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r6 = r2.intValue()
            if (r6 != 0) goto L_0x12a8
            r2 = r9
        L_0x12a8:
            java.lang.String r6 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r6)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r6 = r0.intValue()
            if (r6 != 0) goto L_0x12d4
            r44 = r1
            r45 = r2
            r22 = r5
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r26 = r13
            r40 = r26
            r41 = r40
            r42 = r41
            r43 = r42
            r46 = r43
            r47 = r46
            goto L_0x12ef
        L_0x12d4:
            r46 = r0
            r44 = r1
            r45 = r2
            r22 = r5
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r26 = r13
            r40 = r26
            r41 = r40
            r42 = r41
            r43 = r42
            r47 = r43
        L_0x12ef:
            r48 = r47
            r49 = r48
            r50 = r49
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
        L_0x12fd:
            r55 = r54
            r11 = 0
            r20 = 6
            goto L_0x1470
        L_0x1304:
            r9 = 0
            r20 = 6
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r6.replace(r0, r2)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "telegrampassport"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x139a
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r11)
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 != 0) goto L_0x1352
            java.lang.String r5 = "{"
            boolean r5 = r2.startsWith(r5)
            if (r5 == 0) goto L_0x1352
            java.lang.String r5 = "}"
            boolean r5 = r2.endsWith(r5)
            if (r5 == 0) goto L_0x1352
            java.lang.String r5 = "nonce"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "nonce"
            r1.put(r6, r5)
            goto L_0x1359
        L_0x1352:
            java.lang.String r5 = r0.getQueryParameter(r10)
            r1.put(r10, r5)
        L_0x1359:
            java.lang.String r5 = r0.getQueryParameter(r13)
            r1.put(r13, r5)
            r1.put(r11, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r5 = "public_key"
            r1.put(r5, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            r48 = r1
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r22 = r13
            r26 = r22
            r40 = r26
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r47 = r46
            r49 = r47
            goto L_0x1463
        L_0x139a:
            java.lang.String r2 = "start"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r5 = "startgroup"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "game"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r10 = "voicechat"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r11 = "post"
            java.lang.String r11 = r0.getQueryParameter(r11)
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)
            int r13 = r11.intValue()
            if (r13 != 0) goto L_0x13c4
            r11 = r9
        L_0x13c4:
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r13 = r12.intValue()
            if (r13 != 0) goto L_0x13d3
            r12 = r9
        L_0x13d3:
            java.lang.String r13 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r13)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r13 = r0.intValue()
            if (r13 != 0) goto L_0x13fa
            r26 = r1
            r47 = r6
            r0 = r9
            r1 = r0
            r6 = r1
            r13 = r6
            r40 = r13
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r46 = r44
            r48 = r46
            goto L_0x1410
        L_0x13fa:
            r46 = r0
            r26 = r1
            r47 = r6
            r0 = r9
            r1 = r0
            r6 = r1
            r13 = r6
            r40 = r13
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r48 = r44
        L_0x1410:
            r49 = r48
            r50 = r49
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
            r55 = r10
            r22 = r11
            r45 = r12
            r11 = 0
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
            r10 = r2
            r12 = r5
            r5 = r54
            goto L_0x1486
        L_0x143e:
            r28 = r2
            r27 = r11
        L_0x1442:
            r20 = 6
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r22 = r13
            r26 = r22
            r40 = r26
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r47 = r46
            r48 = r47
            r49 = r48
        L_0x1463:
            r50 = r49
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
            r55 = r54
            r11 = 0
        L_0x1470:
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
        L_0x1486:
            boolean r2 = r14.hasExtra(r3)
            if (r2 == 0) goto L_0x14ce
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x14a6
            java.lang.String r2 = "tg"
            r9 = r28
            boolean r2 = r2.equals(r9)
            if (r2 == 0) goto L_0x14a6
            if (r50 != 0) goto L_0x14a6
            r2 = 1
            goto L_0x14a7
        L_0x14a6:
            r2 = 0
        L_0x14a7:
            com.google.firebase.appindexing.builders.AssistActionBuilder r9 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r9.<init>()
            r28 = r8
            java.lang.String r8 = r14.getStringExtra(r3)
            r9.setActionToken(r8)
            if (r2 == 0) goto L_0x14ba
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x14bc
        L_0x14ba:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x14bc:
            r9.setActionStatus(r2)
            com.google.firebase.appindexing.Action r2 = r9.build()
            com.google.firebase.appindexing.FirebaseUserActions r8 = com.google.firebase.appindexing.FirebaseUserActions.getInstance()
            r8.end(r2)
            r14.removeExtra(r3)
            goto L_0x14d0
        L_0x14ce:
            r28 = r8
        L_0x14d0:
            if (r51 != 0) goto L_0x14ea
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x14df
            goto L_0x14ea
        L_0x14df:
            r7 = r15
            r62 = r27
            r63 = r28
            r25 = -1
            r56 = 0
            goto L_0x164f
        L_0x14ea:
            if (r1 != 0) goto L_0x1632
            if (r5 == 0) goto L_0x14f0
            goto L_0x1632
        L_0x14f0:
            if (r26 != 0) goto L_0x15d6
            if (r0 != 0) goto L_0x15d6
            if (r6 != 0) goto L_0x15d6
            if (r13 != 0) goto L_0x15d6
            if (r47 != 0) goto L_0x15d6
            if (r55 != 0) goto L_0x15d6
            if (r48 != 0) goto L_0x15d6
            if (r50 != 0) goto L_0x15d6
            if (r49 != 0) goto L_0x15d6
            if (r51 != 0) goto L_0x15d6
            if (r53 != 0) goto L_0x15d6
            if (r44 != 0) goto L_0x15d6
            if (r54 != 0) goto L_0x15d6
            if (r52 == 0) goto L_0x150e
            goto L_0x15d6
        L_0x150e:
            android.content.ContentResolver r56 = r65.getContentResolver()     // Catch:{ Exception -> 0x15bf }
            android.net.Uri r57 = r66.getData()     // Catch:{ Exception -> 0x15bf }
            r58 = 0
            r59 = 0
            r60 = 0
            r61 = 0
            android.database.Cursor r1 = r56.query(r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x15bf }
            if (r1 == 0) goto L_0x15ae
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x15a0 }
            if (r0 == 0) goto L_0x15ae
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x15a0 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x15a0 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x15a0 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x15a0 }
            r2 = 0
            r7 = 3
        L_0x153e:
            if (r2 >= r7) goto L_0x155a
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1558 }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x1558 }
            if (r3 != r0) goto L_0x1554
            r3 = 0
            r27[r3] = r2     // Catch:{ all -> 0x1558 }
            r0 = r27[r3]     // Catch:{ all -> 0x1558 }
            r9 = 1
            r15.switchToAccount(r0, r9)     // Catch:{ all -> 0x159e }
            goto L_0x155b
        L_0x1554:
            r9 = 1
            int r2 = r2 + 1
            goto L_0x153e
        L_0x1558:
            r0 = move-exception
            goto L_0x15a2
        L_0x155a:
            r9 = 1
        L_0x155b:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x159e }
            int r2 = r1.getInt(r0)     // Catch:{ all -> 0x159e }
            r3 = 0
            r0 = r27[r3]     // Catch:{ all -> 0x159e }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x159e }
            int r4 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x159e }
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x159e }
            r0.postNotificationName(r4, r5)     // Catch:{ all -> 0x159e }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x159a }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x159a }
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r3 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x159a }
            if (r3 == 0) goto L_0x158a
            r29 = r2
            r6 = 1
            goto L_0x15b2
        L_0x158a:
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x159a }
            r29 = r2
            r6 = r34
            if (r0 == 0) goto L_0x15b2
            r35 = 1
            goto L_0x15b2
        L_0x159a:
            r0 = move-exception
            r29 = r2
            goto L_0x15a3
        L_0x159e:
            r0 = move-exception
            goto L_0x15a3
        L_0x15a0:
            r0 = move-exception
            r7 = 3
        L_0x15a2:
            r9 = 1
        L_0x15a3:
            throw r0     // Catch:{ all -> 0x15a4 }
        L_0x15a4:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x15ab
            r1.close()     // Catch:{ all -> 0x15ab }
        L_0x15ab:
            throw r2     // Catch:{ Exception -> 0x15ac }
        L_0x15ac:
            r0 = move-exception
            goto L_0x15c2
        L_0x15ae:
            r7 = 3
            r9 = 1
            r6 = r34
        L_0x15b2:
            if (r1 == 0) goto L_0x15bc
            r1.close()     // Catch:{ Exception -> 0x15b8 }
            goto L_0x15bc
        L_0x15b8:
            r0 = move-exception
            r34 = r6
            goto L_0x15c2
        L_0x15bc:
            r34 = r6
            goto L_0x15c5
        L_0x15bf:
            r0 = move-exception
            r7 = 3
            r9 = 1
        L_0x15c2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x15c5:
            r7 = r15
            r62 = r27
            r63 = r28
            r12 = r29
            r13 = r32
            r9 = r40
            r25 = -1
            r56 = 0
            goto L_0x167a
        L_0x15d6:
            r7 = 3
            r9 = 1
            if (r13 == 0) goto L_0x15f5
            java.lang.String r1 = "@"
            boolean r1 = r13.startsWith(r1)
            if (r1 == 0) goto L_0x15f5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = " "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            r8 = r1
            goto L_0x15f6
        L_0x15f5:
            r8 = r13
        L_0x15f6:
            r13 = 0
            r2 = r27[r13]
            r23 = 0
            r1 = r65
            r56 = 0
            r3 = r26
            r4 = r0
            r25 = -1
            r5 = r6
            r6 = r10
            r10 = 3
            r7 = r12
            r12 = r28
            r16 = 1
            r9 = r11
            r11 = 3
            r10 = r22
            r62 = r27
            r11 = r44
            r63 = r12
            r12 = r45
            r13 = r46
            r14 = r47
            r15 = r48
            r16 = r49
            r17 = r50
            r18 = r51
            r19 = r52
            r20 = r53
            r21 = r54
            r22 = r55
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23)
            r7 = r65
            goto L_0x164f
        L_0x1632:
            r62 = r27
            r63 = r28
            r25 = -1
            r56 = 0
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r0.putString(r4, r1)
            r0.putString(r7, r5)
            org.telegram.ui.-$$Lambda$LaunchActivity$VK-kZ7jRryo5GCLASSNAMEoFxf--u3Egg r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$VK-kZ7jRryo5GCLASSNAMEoFxf--u3Egg
            r7 = r65
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x164f:
            r12 = r29
            r13 = r32
            r9 = r40
            goto L_0x167a
        L_0x1656:
            r63 = r8
            r62 = r11
            r7 = r15
            r25 = -1
            r56 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r30 = 0
            r31 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x167a:
            r11 = r9
            r6 = r12
            r8 = r13
            r14 = r30
            r9 = r31
            r12 = r34
            r13 = r35
            r64 = r36
            r1 = r41
            r2 = r42
            r68 = r43
            r18 = r56
            r10 = r62
            r3 = r63
            r0 = -1
            r5 = -1
            r15 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            goto L_0x1828
        L_0x16a2:
            r63 = r8
            r62 = r11
            r7 = r15
            r25 = -1
            r56 = 0
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x16c8
            r18 = r56
            r10 = r62
            r3 = r63
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 1
            r9 = 0
            goto L_0x180f
        L_0x16c8:
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x16f2
            r18 = r56
            r10 = r62
            r3 = r63
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
            r9 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r21 = 1
            goto L_0x181c
        L_0x16f2:
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x1790
            java.lang.String r0 = "chatId"
            r8 = r66
            r9 = 0
            int r0 = r8.getIntExtra(r0, r9)
            java.lang.String r1 = "userId"
            int r1 = r8.getIntExtra(r1, r9)
            java.lang.String r2 = "encId"
            int r2 = r8.getIntExtra(r2, r9)
            java.lang.String r3 = "appWidgetId"
            int r5 = r8.getIntExtra(r3, r9)
            if (r5 == 0) goto L_0x1730
            java.lang.String r0 = "appWidgetType"
            int r0 = r8.getIntExtra(r0, r9)
            r25 = r5
            r10 = r62
            r3 = r63
            r1 = 6
            r2 = 0
            r4 = 0
            r12 = 0
            r13 = 0
            r5 = r0
            r0 = 0
            goto L_0x177c
        L_0x1730:
            r3 = r63
            int r4 = r8.getIntExtra(r3, r9)
            if (r0 == 0) goto L_0x174d
            r10 = r62
            r1 = r10[r9]
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r1.postNotificationName(r2, r5)
            r1 = 0
            r2 = 0
        L_0x1749:
            r5 = -1
            r12 = 0
        L_0x174b:
            r13 = 0
            goto L_0x177c
        L_0x174d:
            r10 = r62
            if (r1 == 0) goto L_0x1764
            r0 = r10[r9]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r0.postNotificationName(r2, r5)
            r12 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r5 = -1
            goto L_0x174b
        L_0x1764:
            if (r2 == 0) goto L_0x1776
            r0 = r10[r9]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r0.postNotificationName(r1, r5)
            r0 = 0
            r1 = 0
            goto L_0x1749
        L_0x1776:
            r0 = 0
            r1 = 0
            r2 = 0
            r5 = -1
            r12 = 0
            r13 = 1
        L_0x177c:
            r14 = r0
            r8 = r1
            r15 = r2
            r9 = r4
            r6 = r12
            r16 = r13
            r0 = r25
            r18 = r56
            r68 = 0
            r1 = 0
            r2 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            goto L_0x1816
        L_0x1790:
            r8 = r66
            r10 = r62
            r3 = r63
            r9 = 0
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x17b7
            r18 = r56
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1
            goto L_0x1818
        L_0x17b7:
            java.lang.String r0 = r66.getAction()
            java.lang.String r1 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x17d9
            r18 = r56
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r20 = 1
            goto L_0x181a
        L_0x17d9:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x1805
            r18 = r56
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r21 = 0
            r22 = 1
            goto L_0x181e
        L_0x17fc:
            r56 = r3
            r3 = r8
            r10 = r11
            r8 = r14
            r7 = r15
            r9 = 0
            r25 = -1
        L_0x1805:
            r18 = r56
            r68 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
        L_0x180f:
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
        L_0x1816:
            r17 = 0
        L_0x1818:
            r20 = 0
        L_0x181a:
            r21 = 0
        L_0x181c:
            r22 = 0
        L_0x181e:
            r33 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r64 = 0
        L_0x1828:
            int r4 = r7.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            boolean r4 = r4.isClientActivated()
            if (r4 == 0) goto L_0x1cbe
            if (r11 == 0) goto L_0x1860
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r4 = r4.getLastFragment()
            r69 = r2
            boolean r2 = r4 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x185c
            org.telegram.ui.DialogsActivity r4 = (org.telegram.ui.DialogsActivity) r4
            boolean r2 = r4.isMainDialogList()
            if (r2 == 0) goto L_0x185a
            android.view.View r2 = r4.getFragmentView()
            if (r2 == 0) goto L_0x1855
            r2 = 1
            r4.search(r11, r2)
            goto L_0x1863
        L_0x1855:
            r2 = 1
            r4.setInitialSearchString(r11)
            goto L_0x1863
        L_0x185a:
            r2 = 1
            goto L_0x1863
        L_0x185c:
            r2 = 1
            r16 = 1
            goto L_0x1863
        L_0x1860:
            r69 = r2
            goto L_0x185a
        L_0x1863:
            if (r6 == 0) goto L_0x18dd
            if (r12 != 0) goto L_0x18b6
            if (r13 == 0) goto L_0x186a
            goto L_0x18b6
        L_0x186a:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putInt(r1, r6)
            if (r9 == 0) goto L_0x187a
            r0.putInt(r3, r9)
        L_0x187a:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x189c
            r1 = 0
            r3 = r10[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r1 = r1.checkCanOpenChat(r0, r3)
            if (r1 == 0) goto L_0x1955
        L_0x189c:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1955
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1953
        L_0x18b6:
            if (r64 == 0) goto L_0x18d1
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x1cbe
            org.telegram.messenger.MessagesController r1 = r0.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r1, r13)
            goto L_0x1cbe
        L_0x18d1:
            r1 = 0
            r0 = r10[r1]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r6, r13, r0)
            goto L_0x1cbe
        L_0x18dd:
            if (r14 == 0) goto L_0x1929
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putInt(r1, r14)
            if (r9 == 0) goto L_0x18ee
            r0.putInt(r3, r9)
        L_0x18ee:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1910
            r1 = 0
            r3 = r10[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r1 = r1.checkCanOpenChat(r0, r3)
            if (r1 == 0) goto L_0x1955
        L_0x1910:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1955
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1953
        L_0x1929:
            if (r15 == 0) goto L_0x195c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enc_id"
            r0.putInt(r1, r15)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r18 = 0
            r19 = 1
            r20 = 1
            r21 = 0
            r16 = r0
            r17 = r1
            boolean r0 = r16.presentFragment(r17, r18, r19, r20, r21)
            if (r0 == 0) goto L_0x1955
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
        L_0x1953:
            r13 = 1
            goto L_0x1956
        L_0x1955:
            r13 = 0
        L_0x1956:
            r0 = r67
            r3 = 0
            r8 = 1
            goto L_0x1cc3
        L_0x195c:
            if (r16 == 0) goto L_0x1998
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x196a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1995
        L_0x196a:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1995
        L_0x1974:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r2
            if (r0 <= 0) goto L_0x198e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x1974
        L_0x198e:
            r3 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.closeLastFragment(r3)
            goto L_0x1996
        L_0x1995:
            r3 = 0
        L_0x1996:
            r0 = 0
            goto L_0x19b9
        L_0x1998:
            r3 = 0
            if (r17 == 0) goto L_0x19bd
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x19b7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r1.<init>(r7)
            r0.showDialog(r1)
        L_0x19b7:
            r0 = r67
        L_0x19b9:
            r3 = 0
            r8 = 1
            goto L_0x1cc2
        L_0x19bd:
            if (r20 == 0) goto L_0x19e2
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x19b7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$YHxLpwvfAJ-XuUMLXdxr2hO5tqk r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$YHxLpwvfAJ-XuUMLXdxr2hO5tqk
            r3.<init>(r10)
            r1.<init>(r7, r3)
            r0.showDialog(r1)
            goto L_0x19b7
        L_0x19e2:
            android.net.Uri r3 = r7.exportingChatUri
            if (r3 == 0) goto L_0x19ed
            java.util.ArrayList<android.net.Uri> r0 = r7.documentsUrisArray
            r7.runImportRequest(r3, r0)
            goto L_0x1cbe
        L_0x19ed:
            java.lang.String r3 = r7.videoPath
            if (r3 != 0) goto L_0x1c8d
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r7.photoPathsArray
            if (r3 != 0) goto L_0x1c8d
            java.lang.String r3 = r7.sendingText
            if (r3 != 0) goto L_0x1c8d
            java.util.ArrayList<java.lang.String> r3 = r7.documentsPathsArray
            if (r3 != 0) goto L_0x1c8d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r7.contactsToSend
            if (r3 != 0) goto L_0x1c8d
            java.util.ArrayList<android.net.Uri> r3 = r7.documentsUrisArray
            if (r3 == 0) goto L_0x1a07
            goto L_0x1c8d
        L_0x1a07:
            if (r8 == 0) goto L_0x1a90
            if (r8 != r2) goto L_0x1a27
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            int r1 = r1.clientUserId
            java.lang.String r3 = "user_id"
            r0.putInt(r3, r1)
            org.telegram.ui.ProfileActivity r9 = new org.telegram.ui.ProfileActivity
            r9.<init>(r0)
        L_0x1a23:
            r13 = r9
            r0 = 0
        L_0x1a25:
            r1 = 6
            goto L_0x1a5c
        L_0x1a27:
            r3 = 2
            if (r8 != r3) goto L_0x1a31
            org.telegram.ui.ThemeActivity r9 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r9.<init>(r1)
            goto L_0x1a23
        L_0x1a31:
            r1 = 0
            r3 = 3
            if (r8 != r3) goto L_0x1a3b
            org.telegram.ui.SessionsActivity r9 = new org.telegram.ui.SessionsActivity
            r9.<init>(r1)
            goto L_0x1a23
        L_0x1a3b:
            r1 = 4
            if (r8 != r1) goto L_0x1a44
            org.telegram.ui.FiltersSetupActivity r9 = new org.telegram.ui.FiltersSetupActivity
            r9.<init>()
            goto L_0x1a23
        L_0x1a44:
            r1 = 5
            if (r8 != r1) goto L_0x1a4f
            org.telegram.ui.ActionIntroActivity r9 = new org.telegram.ui.ActionIntroActivity
            r9.<init>(r3)
            r13 = r9
            r0 = 1
            goto L_0x1a25
        L_0x1a4f:
            r1 = 6
            if (r8 != r1) goto L_0x1a5a
            org.telegram.ui.EditWidgetActivity r9 = new org.telegram.ui.EditWidgetActivity
            r9.<init>(r5, r0, r2)
            r13 = r9
            r0 = 0
            goto L_0x1a5c
        L_0x1a5a:
            r0 = 0
            r13 = 0
        L_0x1a5c:
            if (r8 != r1) goto L_0x1a6a
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            goto L_0x1a72
        L_0x1a6a:
            org.telegram.ui.-$$Lambda$LaunchActivity$khhShGuCQfuJII48gK_zJ7yqvar_ r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$khhShGuCQfuJII48gK_zJ7yqvar_
            r1.<init>(r13, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x1a72:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1a89
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1aca
        L_0x1a89:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1aca
        L_0x1a90:
            r3 = 2
            if (r21 == 0) goto L_0x1ad0
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r2)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            org.telegram.ui.ContactsActivity r13 = new org.telegram.ui.ContactsActivity
            r13.<init>(r0)
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1ac4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1aca
        L_0x1ac4:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
        L_0x1aca:
            r0 = r67
            r3 = 0
            r8 = 1
            goto L_0x1cac
        L_0x1ad0:
            if (r1 == 0) goto L_0x1b2d
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "destroyAfterSelect"
            r0.putBoolean(r3, r2)
            java.lang.String r3 = "returnAsResult"
            r0.putBoolean(r3, r2)
            java.lang.String r3 = "onlyUsers"
            r0.putBoolean(r3, r2)
            java.lang.String r3 = "allowSelf"
            r4 = 0
            r0.putBoolean(r3, r4)
            org.telegram.ui.ContactsActivity r15 = new org.telegram.ui.ContactsActivity
            r15.<init>(r0)
            r15.setInitialSearchString(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$IYhLCLASSNAMEKdQLqstenfGA5Hl8JOK8 r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$IYhLCLASSNAMEKdQLqstenfGA5Hl8JOK8
            r0.<init>(r13, r10)
            r15.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r14.getLastFragment()
            boolean r0 = r0 instanceof org.telegram.ui.ContactsActivity
            r17 = 1
            r18 = 1
            r19 = 0
            r16 = r0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1b26
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1aca
        L_0x1b26:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1aca
        L_0x1b2d:
            if (r39 == 0) goto L_0x1b68
            org.telegram.ui.ActionIntroActivity r13 = new org.telegram.ui.ActionIntroActivity
            r0 = 5
            r13.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$8KPGrhe6Jj04g840aKU1DGBb1qE r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$8KPGrhe6Jj04g840aKU1DGBb1qE
            r0.<init>(r13)
            r13.setQrLoginDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1b60
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1aca
        L_0x1b60:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1aca
        L_0x1b68:
            r1 = 0
            if (r37 == 0) goto L_0x1bbc
            org.telegram.ui.NewContactActivity r13 = new org.telegram.ui.NewContactActivity
            r13.<init>()
            if (r69 == 0) goto L_0x1b86
            java.lang.String r0 = " "
            r9 = r69
            java.lang.String[] r0 = r9.split(r0, r3)
            r3 = r0[r1]
            int r4 = r0.length
            if (r4 <= r2) goto L_0x1b82
            r9 = r0[r2]
            goto L_0x1b83
        L_0x1b82:
            r9 = 0
        L_0x1b83:
            r13.setInitialName(r3, r9)
        L_0x1b86:
            if (r68 == 0) goto L_0x1b91
            r0 = r68
            java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0, r2)
            r13.setInitialPhoneNumber(r0, r1)
        L_0x1b91:
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1bb4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1aca
        L_0x1bb4:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1aca
        L_0x1bbc:
            r0 = r68
            r9 = r69
            if (r22 == 0) goto L_0x1bdc
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r65
            r8 = 1
            r2 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1bd9
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r8
        L_0x1bd9:
            r3 = 0
            goto L_0x1cc0
        L_0x1bdc:
            r8 = 1
            if (r38 == 0) goto L_0x1c5a
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r1.getLastFragment()
            if (r1 == 0) goto L_0x1CLASSNAME
            android.app.Activity r2 = r1.getParentActivity()
            if (r2 == 0) goto L_0x1CLASSNAME
            int r2 = r7.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r3 = 0
            java.lang.String r0 = org.telegram.ui.NewContactActivity.getPhoneNumber(r7, r2, r0, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r1.getParentActivity()
            r2.<init>((android.content.Context) r3)
            r3 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.String r4 = "NewContactAlertTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setTitle(r3)
            r3 = 2131626228(0x7f0e08f4, float:1.8879686E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r5 = r5.format(r0)
            r6 = 0
            r4[r6] = r5
            java.lang.String r5 = "NewContactAlertMessage"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setMessage(r3)
            r3 = 2131626227(0x7f0e08f3, float:1.8879684E38)
            java.lang.String r4 = "NewContactAlertButton"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$sE5-pyjtIyn8IQYwQNwWAj1sFSE r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$sE5-pyjtIyn8IQYwQNwWAj1sFSE
            r4.<init>(r0, r9, r1)
            r2.setPositiveButton(r3, r4)
            r0 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 0
            r2.setNegativeButton(r0, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r2.create()
            r1.showDialog(r0)
            r13 = 1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = 0
            r13 = 0
        L_0x1CLASSNAME:
            r0 = r67
            goto L_0x1cc3
        L_0x1c5a:
            r3 = 0
            if (r33 == 0) goto L_0x1cc0
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r15 = new org.telegram.ui.CallLogActivity
            r15.<init>()
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1caa
        L_0x1CLASSNAME:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1caa
        L_0x1c8d:
            r1 = 0
            r3 = 0
            r8 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1ca3
            r0 = r10[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r4)
        L_0x1ca3:
            int r0 = (r18 > r56 ? 1 : (r18 == r56 ? 0 : -1))
            if (r0 != 0) goto L_0x1cae
            r7.openDialogsToSend(r1)
        L_0x1caa:
            r0 = r67
        L_0x1cac:
            r13 = 1
            goto L_0x1cc3
        L_0x1cae:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r18)
            r0.add(r2)
            r7.didSelectDialogs(r3, r0, r3, r1)
            goto L_0x1cc0
        L_0x1cbe:
            r3 = 0
            r8 = 1
        L_0x1cc0:
            r0 = r67
        L_0x1cc2:
            r13 = 0
        L_0x1cc3:
            if (r13 != 0) goto L_0x1d6f
            if (r0 != 0) goto L_0x1d6f
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d19
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1cf4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1d5a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d5a
        L_0x1cf4:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1d5a
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r11 == 0) goto L_0x1d0d
            r0.setInitialSearchString(r11)
        L_0x1d0d:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1d5a
        L_0x1d19:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1d5a
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1d40
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d5a
        L_0x1d40:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r11 == 0) goto L_0x1d4f
            r0.setInitialSearchString(r11)
        L_0x1d4f:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
        L_0x1d5a:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d6f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
        L_0x1d6f:
            if (r24 == 0) goto L_0x1d77
            r1 = 0
            r0 = r10[r1]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x1d77:
            if (r22 != 0) goto L_0x1d93
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1d93
            r1 = r66
            if (r1 == 0) goto L_0x1d8d
            java.lang.String r0 = r66.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x1d95
        L_0x1d8d:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r0.dismiss()
            goto L_0x1d95
        L_0x1d93:
            r1 = r66
        L_0x1d95:
            r1.setAction(r3)
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
        lambda$runLinkRequest$41(new CancelAccountDeletionActivity(bundle));
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
        lambda$runLinkRequest$41(locationActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$10 */
    public /* synthetic */ void lambda$handleIntent$10$LaunchActivity(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$11 */
    public /* synthetic */ void lambda$handleIntent$11$LaunchActivity(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
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

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x007a, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0098;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0096, code lost:
        if ((r0.get(r0.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0098;
     */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openDialogsToSend(boolean r11) {
        /*
            r10 = this;
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r2 = 1
            r0.putBoolean(r1, r2)
            java.lang.String r1 = "dialogsType"
            r3 = 3
            r0.putInt(r1, r3)
            java.lang.String r1 = "allowSwitchAccount"
            r0.putBoolean(r1, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r10.contactsToSend
            java.lang.String r3 = "selectAlertStringGroup"
            r4 = 2131627366(0x7f0e0d66, float:1.8881994E38)
            java.lang.String r5 = "selectAlertString"
            if (r1 == 0) goto L_0x003d
            int r1 = r1.size()
            if (r1 == r2) goto L_0x0052
            java.lang.String r1 = "SendContactToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627343(0x7f0e0d4f, float:1.8881948E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
            goto L_0x0052
        L_0x003d:
            java.lang.String r1 = "SendMessagesToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627365(0x7f0e0d65, float:1.8881992E38)
            java.lang.String r4 = "SendMessagesToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
        L_0x0052:
            org.telegram.ui.DialogsActivity r5 = new org.telegram.ui.DialogsActivity
            r5.<init>(r0)
            r5.setDelegate(r10)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            r1 = 0
            if (r0 == 0) goto L_0x007d
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x009a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r2
            java.lang.Object r0 = r0.get(r3)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x009a
            goto L_0x0098
        L_0x007d:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 <= r2) goto L_0x009a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r2
            java.lang.Object r0 = r0.get(r3)
            boolean r0 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r0 == 0) goto L_0x009a
        L_0x0098:
            r0 = 1
            goto L_0x009b
        L_0x009a:
            r0 = 0
        L_0x009b:
            r6 = r0
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r10.actionBarLayout
            r7 = r11 ^ 1
            r8 = 1
            r9 = 0
            r4.presentFragment(r5, r6, r7, r8, r9)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x00bd
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x00bd
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r1, r1)
            goto L_0x00ec
        L_0x00bd:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x00d5
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x00d5
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r1, r2)
            goto L_0x00ec
        L_0x00d5:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x00ec
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x00ec
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r1, r2)
        L_0x00ec:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x00f3
            r0.dismiss()
        L_0x00f3:
            if (r11 != 0) goto L_0x0110
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r10.drawerLayoutContainer
            r11.setAllowOpenDrawer(r1, r1)
            boolean r11 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r11 == 0) goto L_0x010b
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r10.actionBarLayout
            r11.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r10.rightActionBarLayout
            r11.showLastFragment()
            goto L_0x0110
        L_0x010b:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r10.drawerLayoutContainer
            r11.setAllowOpenDrawer(r2, r1)
        L_0x0110:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openDialogsToSend(boolean):void");
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
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0095 A[SYNTHETIC, Splitter:B:15:0x0095] */
    /* renamed from: lambda$null$17 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$17$LaunchActivity(org.telegram.tgnet.TLObject r12, int r13, java.lang.Integer r14, org.telegram.tgnet.TLRPC$Chat r15, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r16, java.lang.Integer r17, java.lang.Integer r18, org.telegram.ui.ActionBar.AlertDialog r19) {
        /*
            r11 = this;
            r0 = r12
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_discussionMessage
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0092
            org.telegram.tgnet.TLRPC$TL_messages_discussionMessage r0 = (org.telegram.tgnet.TLRPC$TL_messages_discussionMessage) r0
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r0.users
            r1.putUsers(r4, r2)
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r0.chats
            r1.putChats(r4, r2)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r0.messages
            int r1 = r1.size()
            r4 = 0
        L_0x0027:
            if (r4 >= r1) goto L_0x003e
            org.telegram.messenger.MessageObject r5 = new org.telegram.messenger.MessageObject
            int r7 = org.telegram.messenger.UserConfig.selectedAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r0.messages
            java.lang.Object r8 = r8.get(r4)
            org.telegram.tgnet.TLRPC$Message r8 = (org.telegram.tgnet.TLRPC$Message) r8
            r5.<init>(r7, r8, r3, r3)
            r6.add(r5)
            int r4 = r4 + 1
            goto L_0x0027
        L_0x003e:
            boolean r1 = r6.isEmpty()
            if (r1 != 0) goto L_0x0092
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.Object r2 = r6.get(r2)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r4 = r2.getDialogId()
            long r4 = -r4
            int r2 = (int) r4
            java.lang.String r4 = "chat_id"
            r1.putInt(r4, r2)
            int r2 = r14.intValue()
            int r2 = java.lang.Math.max(r3, r2)
            java.lang.String r4 = "message_id"
            r1.putInt(r4, r2)
            org.telegram.ui.ChatActivity r2 = new org.telegram.ui.ChatActivity
            r2.<init>(r1)
            r1 = r16
            int r8 = r1.msg_id
            int r9 = r0.read_inbox_max_id
            int r10 = r0.read_outbox_max_id
            r5 = r2
            r7 = r15
            r5.setThreadMessages(r6, r7, r8, r9, r10)
            if (r17 == 0) goto L_0x0083
            int r0 = r17.intValue()
            r2.setHighlightMessageId(r0)
            goto L_0x008c
        L_0x0083:
            if (r18 == 0) goto L_0x008c
            int r0 = r14.intValue()
            r2.setHighlightMessageId(r0)
        L_0x008c:
            r1 = r11
            r11.lambda$runLinkRequest$41(r2)
            r2 = 1
            goto L_0x0093
        L_0x0092:
            r1 = r11
        L_0x0093:
            if (r2 != 0) goto L_0x00c3
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00bf }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x00bf }
            if (r0 != 0) goto L_0x00c3
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00bf }
            int r2 = r0.size()     // Catch:{ Exception -> 0x00bf }
            int r2 = r2 - r3
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x00bf }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x00bf }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r0)     // Catch:{ Exception -> 0x00bf }
            java.lang.String r2 = "ChannelPostDeleted"
            r3 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x00bf }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)     // Catch:{ Exception -> 0x00bf }
            r0.show()     // Catch:{ Exception -> 0x00bf }
            goto L_0x00c3
        L_0x00bf:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c3:
            r19.dismiss()     // Catch:{ Exception -> 0x00c7 }
            goto L_0x00cc
        L_0x00c7:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$17$LaunchActivity(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x007c A[SYNTHETIC, Splitter:B:29:0x007c] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0087 A[SYNTHETIC, Splitter:B:34:0x0087] */
    /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runImportRequest(android.net.Uri r11, java.util.ArrayList<android.net.Uri> r12) {
        /*
            r10 = this;
            int r12 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r1 = 3
            r0.<init>(r10, r1)
            r1 = 1
            int[] r1 = new int[r1]
            r2 = 0
            r1[r2] = r2
            r3 = 0
            android.content.ContentResolver r4 = r10.getContentResolver()     // Catch:{ Exception -> 0x0076 }
            java.io.InputStream r4 = r4.openInputStream(r11)     // Catch:{ Exception -> 0x0076 }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            r6.<init>(r4)     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            r5.<init>(r6)     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            r6.<init>()     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            r7 = 0
        L_0x0027:
            java.lang.String r8 = r5.readLine()     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            if (r8 == 0) goto L_0x003c
            r9 = 100
            if (r7 >= r9) goto L_0x003c
            r6.append(r8)     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            r8 = 10
            r6.append(r8)     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            int r7 = r7 + 1
            goto L_0x0027
        L_0x003c:
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0071, all -> 0x006e }
            if (r4 == 0) goto L_0x004a
            r4.close()     // Catch:{ Exception -> 0x0046 }
            goto L_0x004a
        L_0x0046:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x004a:
            org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImport r4 = new org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImport
            r4.<init>()
            r4.import_head = r5
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            org.telegram.ui.-$$Lambda$LaunchActivity$_oizVSS35ihStU6XX8OJB0k3vmg r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$_oizVSS35ihStU6XX8OJB0k3vmg
            r6.<init>(r11, r12, r0)
            int r11 = r5.sendRequest(r4, r6)
            r1[r2] = r11
            org.telegram.ui.-$$Lambda$LaunchActivity$ohKO_W48WGqsecb5AjK-LXFCG3o r11 = new org.telegram.ui.-$$Lambda$LaunchActivity$ohKO_W48WGqsecb5AjK-LXFCG3o
            r11.<init>(r12, r1, r3)
            r0.setOnCancelListener(r11)
            r11 = 300(0x12c, double:1.48E-321)
            r0.showDelayed(r11)     // Catch:{ Exception -> 0x006d }
        L_0x006d:
            return
        L_0x006e:
            r11 = move-exception
            r3 = r4
            goto L_0x0085
        L_0x0071:
            r11 = move-exception
            r3 = r4
            goto L_0x0077
        L_0x0074:
            r11 = move-exception
            goto L_0x0085
        L_0x0076:
            r11 = move-exception
        L_0x0077:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ all -> 0x0074 }
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0084
        L_0x0080:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x0084:
            return
        L_0x0085:
            if (r3 == 0) goto L_0x008f
            r3.close()     // Catch:{ Exception -> 0x008b }
            goto L_0x008f
        L_0x008b:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x008f:
            goto L_0x0091
        L_0x0090:
            throw r11
        L_0x0091:
            goto L_0x0090
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runImportRequest(android.net.Uri, java.util.ArrayList):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runImportRequest$20 */
    public /* synthetic */ void lambda$runImportRequest$20$LaunchActivity(Uri uri, int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, uri, i, alertDialog) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Uri f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ AlertDialog f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$19$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011b, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0137, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* renamed from: lambda$null$19 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$19$LaunchActivity(org.telegram.tgnet.TLObject r10, android.net.Uri r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x0164
            r0 = 0
            r1 = 1
            if (r10 == 0) goto L_0x0144
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x0144
            org.telegram.tgnet.TLRPC$TL_messages_historyImportParsed r10 = (org.telegram.tgnet.TLRPC$TL_messages_historyImportParsed) r10
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.String r3 = "onlySelect"
            r2.putBoolean(r3, r1)
            java.lang.String r3 = r10.title
            java.lang.String r4 = "importTitle"
            r2.putString(r4, r3)
            java.lang.String r3 = "allowSwitchAccount"
            r2.putBoolean(r3, r1)
            boolean r3 = r10.pm
            r4 = 12
            java.lang.String r5 = "dialogsType"
            if (r3 == 0) goto L_0x0032
            r2.putInt(r5, r4)
            goto L_0x008b
        L_0x0032:
            boolean r10 = r10.group
            r3 = 11
            if (r10 == 0) goto L_0x003c
            r2.putInt(r5, r3)
            goto L_0x008b
        L_0x003c:
            java.lang.String r10 = r11.toString()
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.util.Set<java.lang.String> r11 = r11.exportPrivateUri
            java.util.Iterator r11 = r11.iterator()
        L_0x004a:
            boolean r6 = r11.hasNext()
            if (r6 == 0) goto L_0x0061
            java.lang.Object r6 = r11.next()
            java.lang.String r6 = (java.lang.String) r6
            boolean r6 = r10.contains(r6)
            if (r6 == 0) goto L_0x004a
            r2.putInt(r5, r4)
            r11 = 1
            goto L_0x0062
        L_0x0061:
            r11 = 0
        L_0x0062:
            if (r11 != 0) goto L_0x008b
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.util.Set<java.lang.String> r12 = r12.exportGroupUri
            java.util.Iterator r12 = r12.iterator()
        L_0x006e:
            boolean r4 = r12.hasNext()
            if (r4 == 0) goto L_0x0084
            java.lang.Object r4 = r12.next()
            java.lang.String r4 = (java.lang.String) r4
            boolean r4 = r10.contains(r4)
            if (r4 == 0) goto L_0x006e
            r2.putInt(r5, r3)
            r11 = 1
        L_0x0084:
            if (r11 != 0) goto L_0x008b
            r10 = 13
            r2.putInt(r5, r10)
        L_0x008b:
            boolean r10 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r10 == 0) goto L_0x00a3
            org.telegram.ui.SecretMediaViewer r10 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x00a3
            org.telegram.ui.SecretMediaViewer r10 = org.telegram.ui.SecretMediaViewer.getInstance()
            r10.closePhoto(r0, r0)
            goto L_0x00d2
        L_0x00a3:
            boolean r10 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r10 == 0) goto L_0x00bb
            org.telegram.ui.PhotoViewer r10 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x00bb
            org.telegram.ui.PhotoViewer r10 = org.telegram.ui.PhotoViewer.getInstance()
            r10.closePhoto(r0, r1)
            goto L_0x00d2
        L_0x00bb:
            boolean r10 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r10 == 0) goto L_0x00d2
            org.telegram.ui.ArticleViewer r10 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x00d2
            org.telegram.ui.ArticleViewer r10 = org.telegram.ui.ArticleViewer.getInstance()
            r10.close(r0, r1)
        L_0x00d2:
            org.telegram.ui.GroupCallActivity r10 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r10 == 0) goto L_0x00d9
            r10.dismiss()
        L_0x00d9:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r9.drawerLayoutContainer
            r10.setAllowOpenDrawer(r0, r0)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x00ef
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.actionBarLayout
            r10.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.rightActionBarLayout
            r10.showLastFragment()
            goto L_0x00f4
        L_0x00ef:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r9.drawerLayoutContainer
            r10.setAllowOpenDrawer(r1, r0)
        L_0x00f4:
            org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
            r4.<init>(r2)
            r4.setDelegate(r9)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x011e
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r10.fragmentsStack
            int r10 = r10.size()
            if (r10 <= 0) goto L_0x013a
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r10.fragmentsStack
            int r11 = r10.size()
            int r11 = r11 - r1
            java.lang.Object r10 = r10.get(r11)
            boolean r10 = r10 instanceof org.telegram.ui.DialogsActivity
            if (r10 == 0) goto L_0x013a
            goto L_0x0139
        L_0x011e:
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r10.fragmentsStack
            int r10 = r10.size()
            if (r10 <= r1) goto L_0x013a
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r9.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r10.fragmentsStack
            int r11 = r10.size()
            int r11 = r11 - r1
            java.lang.Object r10 = r10.get(r11)
            boolean r10 = r10 instanceof org.telegram.ui.DialogsActivity
            if (r10 == 0) goto L_0x013a
        L_0x0139:
            r0 = 1
        L_0x013a:
            r5 = r0
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r9.actionBarLayout
            r6 = 0
            r7 = 1
            r8 = 0
            r3.presentFragment(r4, r5, r6, r7, r8)
            goto L_0x015c
        L_0x0144:
            java.util.ArrayList<android.net.Uri> r10 = r9.documentsUrisArray
            if (r10 != 0) goto L_0x014f
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r9.documentsUrisArray = r10
        L_0x014f:
            java.util.ArrayList<android.net.Uri> r10 = r9.documentsUrisArray
            android.net.Uri r11 = r9.exportingChatUri
            r10.add(r0, r11)
            r10 = 0
            r9.exportingChatUri = r10
            r9.openDialogsToSend(r1)
        L_0x015c:
            r13.dismiss()     // Catch:{ Exception -> 0x0160 }
            goto L_0x0164
        L_0x0160:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0164:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$19$LaunchActivity(org.telegram.tgnet.TLObject, android.net.Uri, int, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    static /* synthetic */ void lambda$runImportRequest$21(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x02d7  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x03d9  */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, java.lang.String r31, boolean r32, java.lang.Integer r33, java.lang.Integer r34, java.lang.Integer r35, java.lang.Integer r36, java.lang.String r37, java.util.HashMap<java.lang.String, java.lang.String> r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, java.lang.String r42, org.telegram.tgnet.TLRPC$TL_wallPaper r43, java.lang.String r44, java.lang.String r45, int r46) {
        /*
            r24 = this;
            r15 = r24
            r13 = r25
            r0 = r26
            r5 = r27
            r6 = r28
            r9 = r31
            r12 = r34
            r14 = r38
            r11 = r39
            r10 = r40
            r8 = r43
            r7 = r44
            r1 = r46
            r2 = 2
            if (r1 != 0) goto L_0x0064
            int r3 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r3 < r2) goto L_0x0064
            if (r14 == 0) goto L_0x0064
            org.telegram.ui.-$$Lambda$LaunchActivity$aLW_uVZMYhFhq9rSFxIugCqWn7Y r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$aLW_uVZMYhFhq9rSFxIugCqWn7Y
            r1 = r4
            r2 = r24
            r3 = r25
            r13 = r4
            r4 = r26
            r5 = r27
            r6 = r28
            r0 = r7
            r7 = r29
            r8 = r30
            r9 = r31
            r10 = r32
            r11 = r33
            r12 = r34
            r0 = r13
            r13 = r35
            r14 = r36
            r15 = r37
            r16 = r38
            r17 = r39
            r18 = r40
            r19 = r41
            r20 = r42
            r21 = r43
            r22 = r44
            r23 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23)
            r14 = r24
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r14, r0)
            r0.show()
            return
        L_0x0064:
            r3 = r7
            r14 = r15
            r4 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r7 = "OK"
            r15 = 0
            r8 = 1
            r11 = 0
            if (r41 == 0) goto L_0x00b8
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r1)
            if (r0 == 0) goto L_0x0088
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r41
            r0.postNotificationName(r1, r2)
            goto L_0x00b7
        L_0x0088:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r41
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
            if (r42 == 0) goto L_0x00e2
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r14)
            r1 = 2131624414(0x7f0e01de, float:1.8876007E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624423(0x7f0e01e7, float:1.8876025E38)
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
            if (r0 == 0) goto L_0x012c
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r12 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r12.<init>()
            r12.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.-$$Lambda$LaunchActivity$W9owPhUj9Hg4A7K3rn5oPVf6jFs r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$W9owPhUj9Hg4A7K3rn5oPVf6jFs
            r1 = r9
            r2 = r24
            r3 = r37
            r4 = r45
            r5 = r25
            r6 = r33
            r8 = r7
            r7 = r36
            r45 = r8
            r8 = r35
            r15 = r9
            r9 = r45
            r17 = r10
            r13 = 0
            r11 = r30
            r13 = r12
            r12 = r29
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            int r0 = r0.sendRequest(r13, r15)
            r7 = r45
            r10 = 0
            r7[r10] = r0
            r10 = r25
            r11 = r17
            r4 = 0
            r13 = 0
            goto L_0x03d4
        L_0x012c:
            r17 = r10
            r10 = 0
            if (r5 == 0) goto L_0x016c
            if (r1 != 0) goto L_0x014f
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.-$$Lambda$LaunchActivity$e-ClUywGxaQ-PSWsW_7mI6USITc r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$e-ClUywGxaQ-PSWsW_7mI6USITc
            r10 = r25
            r11 = r17
            r13 = 0
            r3.<init>(r10, r11, r5)
            int r0 = r1.sendRequest(r0, r3, r2)
            r7[r13] = r0
            goto L_0x0169
        L_0x014f:
            r10 = r25
            r11 = r17
            r13 = 0
            if (r1 != r8) goto L_0x0169
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.-$$Lambda$LaunchActivity$tx-zYfRNJpo9OSQMqkrWTTVcmeE r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$tx-zYfRNJpo9OSQMqkrWTTVcmeE
            r3.<init>(r10, r11)
            r1.sendRequest(r0, r3, r2)
        L_0x0169:
            r4 = 0
            goto L_0x03d4
        L_0x016c:
            r10 = r25
            r11 = r17
            r13 = 0
            if (r6 == 0) goto L_0x01cb
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01ca
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x01b4
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r4 = 0
            org.telegram.ui.Components.ChatActivityEnterView r5 = r2.getChatActivityEnterView()
            r25 = r3
            r26 = r24
            r27 = r1
            r28 = r0
            r29 = r4
            r30 = r5
            r25.<init>(r26, r27, r28, r29, r30)
            boolean r0 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r0)
            goto L_0x01c7
        L_0x01b4:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r2 = 0
            r4 = 0
            r25 = r3
            r26 = r24
            r27 = r1
            r28 = r0
            r29 = r2
            r30 = r4
            r25.<init>(r26, r27, r28, r29, r30)
        L_0x01c7:
            r1.showDialog(r3)
        L_0x01ca:
            return
        L_0x01cb:
            if (r9 == 0) goto L_0x01f0
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r8)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r4)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$xiZ0HxBCdiHST-WvYLTYBrvtpxI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$xiZ0HxBCdiHST-WvYLTYBrvtpxI
            r2 = r32
            r0.<init>(r2, r10, r9)
            r1.setDelegate(r0)
            r14.presentFragment(r1, r13, r8)
            goto L_0x0169
        L_0x01f0:
            r0 = r38
            if (r0 == 0) goto L_0x025d
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x0207
            return
        L_0x0207:
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
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.-$$Lambda$LaunchActivity$6XLU44Aqp3YzcUhosz_IhoLjXEU r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$6XLU44Aqp3YzcUhosz_IhoLjXEU
            r26 = r1
            r27 = r24
            r28 = r7
            r29 = r25
            r30 = r11
            r31 = r5
            r32 = r2
            r33 = r3
            r34 = r4
            r26.<init>(r28, r29, r30, r31, r32, r33, r34)
            int r0 = r0.sendRequest(r5, r1)
            r7[r13] = r0
            goto L_0x0169
        L_0x025d:
            r0 = r40
            if (r0 == 0) goto L_0x027b
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$BV_AIdZJ2q_uyN40u8TT-n_w3VI r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$BV_AIdZJ2q_uyN40u8TT-n_w3VI
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r13] = r0
            goto L_0x0169
        L_0x027b:
            java.lang.String r0 = "android"
            r1 = r39
            if (r1 == 0) goto L_0x029d
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r2 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r2.<init>()
            r2.lang_code = r1
            r2.lang_pack = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$XpSS05F2CIV2lO1vS07kCnBFcqI r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$XpSS05F2CIV2lO1vS07kCnBFcqI
            r1.<init>(r11)
            int r0 = r0.sendRequest(r2, r1)
            r7[r13] = r0
            goto L_0x0169
        L_0x029d:
            r1 = r43
            if (r1 == 0) goto L_0x02fa
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x02d3
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x02cd }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x02cd }
            int r4 = r3.background_color     // Catch:{ Exception -> 0x02cd }
            int r5 = r3.second_background_color     // Catch:{ Exception -> 0x02cd }
            int r3 = r3.rotation     // Catch:{ Exception -> 0x02cd }
            int r3 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r3, r13)     // Catch:{ Exception -> 0x02cd }
            r0.<init>(r2, r4, r5, r3)     // Catch:{ Exception -> 0x02cd }
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x02cd }
            r4 = 0
            r2.<init>(r0, r4)     // Catch:{ Exception -> 0x02cb }
            org.telegram.ui.-$$Lambda$LaunchActivity$1ewIDSX6v3iqNHxvLs57zXfzUOk r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$1ewIDSX6v3iqNHxvLs57zXfzUOk     // Catch:{ Exception -> 0x02cb }
            r0.<init>(r2)     // Catch:{ Exception -> 0x02cb }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x02cb }
            goto L_0x02d5
        L_0x02cb:
            r0 = move-exception
            goto L_0x02cf
        L_0x02cd:
            r0 = move-exception
            r4 = 0
        L_0x02cf:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02d4
        L_0x02d3:
            r4 = 0
        L_0x02d4:
            r8 = 0
        L_0x02d5:
            if (r8 != 0) goto L_0x03d4
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$hgWUm3vZOS6YWZiTjCASeedskVs r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$hgWUm3vZOS6YWZiTjCASeedskVs
            r3.<init>(r11, r1)
            int r0 = r2.sendRequest(r0, r3)
            r7[r13] = r0
            goto L_0x03d4
        L_0x02fa:
            r4 = 0
            if (r3 == 0) goto L_0x0325
            org.telegram.ui.-$$Lambda$LaunchActivity$datiwutOOOqtyVnN6hEr0rYDv9A r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$datiwutOOOqtyVnN6hEr0rYDv9A
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
            org.telegram.ui.-$$Lambda$LaunchActivity$teSqgmp29W09k55dP2J5mMX2tVQ r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$teSqgmp29W09k55dP2J5mMX2tVQ
            r2.<init>(r11)
            int r0 = r0.sendRequest(r1, r2)
            r7[r13] = r0
            goto L_0x03d5
        L_0x0325:
            if (r12 == 0) goto L_0x03d4
            if (r33 == 0) goto L_0x03d4
            if (r35 == 0) goto L_0x0382
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r25)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            if (r0 == 0) goto L_0x034b
            r26 = r24
            r27 = r25
            r28 = r11
            r29 = r33
            r30 = r36
            r31 = r35
            r32 = r0
            int r0 = r26.runCommentRequest(r27, r28, r29, r30, r31, r32)
            r7[r13] = r0
            goto L_0x03d4
        L_0x034b:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r0 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            int r2 = r34.intValue()
            r1.channel_id = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r2 = r0.id
            r2.add(r1)
            int r1 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$KX-nzHOEQzUNbsu33IC2w_PgHdU r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$KX-nzHOEQzUNbsu33IC2w_PgHdU
            r37 = r2
            r38 = r24
            r39 = r7
            r40 = r25
            r41 = r11
            r42 = r33
            r43 = r36
            r44 = r35
            r37.<init>(r39, r40, r41, r42, r43, r44)
            int r0 = r1.sendRequest(r0, r2)
            r7[r13] = r0
            goto L_0x03d4
        L_0x0382:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r34.intValue()
            java.lang.String r2 = "chat_id"
            r0.putInt(r2, r1)
            int r1 = r33.intValue()
            java.lang.String r2 = "message_id"
            r0.putInt(r2, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x03af
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03b0
        L_0x03af:
            r1 = r4
        L_0x03b0:
            if (r1 == 0) goto L_0x03bc
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r25)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x03d4
        L_0x03bc:
            org.telegram.ui.-$$Lambda$LaunchActivity$-jayKIWnQR28AcfSLJAMeeUlMYM r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$-jayKIWnQR28AcfSLJAMeeUlMYM
            r26 = r2
            r27 = r24
            r28 = r0
            r29 = r34
            r30 = r7
            r31 = r11
            r32 = r1
            r33 = r25
            r26.<init>(r28, r29, r30, r31, r32, r33)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x03d4:
            r15 = r4
        L_0x03d5:
            r0 = r7[r13]
            if (r0 == 0) goto L_0x03e6
            org.telegram.ui.-$$Lambda$LaunchActivity$bixgLd95ntcdBdsNOZaC8gm-wzw r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$bixgLd95ntcdBdsNOZaC8gm-wzw
            r0.<init>(r10, r7, r15)
            r11.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r11.showDelayed(r0)     // Catch:{ Exception -> 0x03e6 }
        L_0x03e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$22 */
    public /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, Integer num3, Integer num4, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, String str13, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, num3, num4, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, str13, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$26 */
    public /* synthetic */ void lambda$runLinkRequest$26$LaunchActivity(String str, String str2, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str3, String str4, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, str, str2, i, num, num2, num3, iArr, alertDialog, str3, str4) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ AlertDialog f$10;
            public final /* synthetic */ String f$11;
            public final /* synthetic */ String f$12;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ String f$4;
            public final /* synthetic */ int f$5;
            public final /* synthetic */ Integer f$6;
            public final /* synthetic */ Integer f$7;
            public final /* synthetic */ Integer f$8;
            public final /* synthetic */ int[] f$9;

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
                this.f$12 = r13;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$25$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
            }
        }, 2);
    }

    /* JADX WARNING: type inference failed for: r1v11 */
    /* JADX WARNING: type inference failed for: r1v12, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r1v18, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r1v45 */
    /* JADX WARNING: type inference failed for: r1v46 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00e1, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0100, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00e3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x016e  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x017a  */
    /* renamed from: lambda$null$25 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$25$LaunchActivity(org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15, java.lang.String r16, java.lang.String r17, int r18, java.lang.Integer r19, java.lang.Integer r20, java.lang.Integer r21, int[] r22, org.telegram.ui.ActionBar.AlertDialog r23, java.lang.String r24, java.lang.String r25) {
        /*
            r13 = this;
            r8 = r13
            r0 = r15
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r24
            r5 = r25
            boolean r6 = r13.isFinishing()
            if (r6 != 0) goto L_0x0329
            r6 = r14
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r6 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r6
            r9 = 1
            r10 = 0
            if (r0 != 0) goto L_0x02cb
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r8.actionBarLayout
            if (r7 == 0) goto L_0x02cb
            if (r1 != 0) goto L_0x0021
            if (r2 == 0) goto L_0x0035
        L_0x0021:
            if (r1 == 0) goto L_0x002b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r6.users
            boolean r7 = r7.isEmpty()
            if (r7 == 0) goto L_0x0035
        L_0x002b:
            if (r2 == 0) goto L_0x02cb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r6.chats
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x02cb
        L_0x0035:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r6.users
            r0.putUsers(r7, r10)
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r6.chats
            r0.putChats(r7, r10)
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r6.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r6.chats
            r0.putUsersAndChats(r7, r11, r10, r9)
            if (r19 == 0) goto L_0x0083
            if (r20 != 0) goto L_0x0058
            if (r21 == 0) goto L_0x0083
        L_0x0058:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r6.chats
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0083
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r6.chats
            java.lang.Object r0 = r0.get(r10)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r13
            r2 = r18
            r3 = r23
            r4 = r19
            r5 = r20
            r6 = r21
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r22[r10] = r0
            r0 = r22[r10]
            if (r0 == 0) goto L_0x02b0
            r4 = r23
        L_0x0080:
            r9 = 0
            goto L_0x031e
        L_0x0083:
            java.lang.String r0 = "dialogsType"
            java.lang.String r7 = "onlySelect"
            if (r1 == 0) goto L_0x0181
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r7, r9)
            java.lang.String r4 = "cantSendToChannels"
            r2.putBoolean(r4, r9)
            r2.putInt(r0, r9)
            r0 = 2131627349(0x7f0e0d55, float:1.888196E38)
            java.lang.String r4 = "SendGameToText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertString"
            r2.putString(r4, r0)
            r0 = 2131627348(0x7f0e0d54, float:1.8881958E38)
            java.lang.String r4 = "SendGameToGroupText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertStringGroup"
            r2.putString(r4, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$uEXWQU9sal1FAKLIQuDENvstjxE r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$uEXWQU9sal1FAKLIQuDENvstjxE
            r2.<init>(r1, r3, r6)
            r0.setDelegate(r2)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x00e7
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x00e5
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x00e5
        L_0x00e3:
            r1 = 1
            goto L_0x0103
        L_0x00e5:
            r1 = 0
            goto L_0x0103
        L_0x00e7:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= r9) goto L_0x00e5
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x00e5
            goto L_0x00e3
        L_0x0103:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            r3 = 1
            r4 = 1
            r5 = 0
            r14 = r2
            r15 = r0
            r16 = r1
            r17 = r3
            r18 = r4
            r19 = r5
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x012d
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x012d
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r10, r10)
            goto L_0x015c
        L_0x012d:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x0145
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0145
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r10, r9)
            goto L_0x015c
        L_0x0145:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x015c
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x015c
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r10, r9)
        L_0x015c:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x0163
            r0.dismiss()
        L_0x0163:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r10)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x017a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x02b0
        L_0x017a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r9, r10)
            goto L_0x02b0
        L_0x0181:
            r1 = 0
            if (r4 == 0) goto L_0x0206
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r6.users
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0194
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r6.users
            java.lang.Object r1 = r1.get(r10)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
        L_0x0194:
            if (r1 == 0) goto L_0x01d7
            boolean r2 = r1.bot
            if (r2 == 0) goto L_0x019f
            boolean r2 = r1.bot_nochats
            if (r2 == 0) goto L_0x019f
            goto L_0x01d7
        L_0x019f:
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r7, r9)
            r5 = 2
            r2.putInt(r0, r5)
            r0 = 2131624229(0x7f0e0125, float:1.8875632E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r1)
            r5[r10] = r6
            java.lang.String r6 = "%1$s"
            r5[r9] = r6
            java.lang.String r6 = "AddToTheGroupAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r0, r5)
            java.lang.String r5 = "addToGroupAlertString"
            r2.putString(r5, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$XZ49yEvInq0WX7xUd5zGpCpU3bI r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$XZ49yEvInq0WX7xUd5zGpCpU3bI
            r2.<init>(r3, r1, r4)
            r0.setDelegate(r2)
            r13.lambda$runLinkRequest$41(r0)
            goto L_0x02b0
        L_0x01d7:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x0201 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0201 }
            if (r0 != 0) goto L_0x0205
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x0201 }
            int r1 = r0.size()     // Catch:{ Exception -> 0x0201 }
            int r1 = r1 - r9
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x0201 }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x0201 }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r0)     // Catch:{ Exception -> 0x0201 }
            java.lang.String r1 = "BotCantJoinGroups"
            r2 = 2131624572(0x7f0e027c, float:1.8876328E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0201 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0201 }
            r0.show()     // Catch:{ Exception -> 0x0201 }
            goto L_0x0205
        L_0x0201:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0205:
            return
        L_0x0206:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r6.chats
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x022e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r6.chats
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC$Chat) r4
            int r4 = r4.id
            java.lang.String r7 = "chat_id"
            r0.putInt(r7, r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r6.chats
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC$Chat) r4
            int r4 = r4.id
            int r4 = -r4
            goto L_0x0248
        L_0x022e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            int r4 = r4.id
            java.lang.String r7 = "user_id"
            r0.putInt(r7, r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            int r4 = r4.id
        L_0x0248:
            long r11 = (long) r4
            if (r5 == 0) goto L_0x0266
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            int r4 = r4.size()
            if (r4 <= 0) goto L_0x0266
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0266
            java.lang.String r4 = "botUser"
            r0.putString(r4, r5)
            r4 = 1
            goto L_0x0267
        L_0x0266:
            r4 = 0
        L_0x0267:
            if (r19 == 0) goto L_0x0272
            int r6 = r19.intValue()
            java.lang.String r7 = "message_id"
            r0.putInt(r7, r6)
        L_0x0272:
            if (r2 == 0) goto L_0x027a
            java.lang.String r6 = "voicechat"
            r0.putString(r6, r2)
        L_0x027a:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0291
            if (r2 != 0) goto L_0x0291
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
        L_0x0291:
            if (r1 == 0) goto L_0x029d
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x02b0
        L_0x029d:
            if (r4 == 0) goto L_0x02b3
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x02b3
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r6 = r1.getDialogId()
            int r2 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x02b3
            r1.setBotUser(r5)
        L_0x02b0:
            r4 = r23
            goto L_0x031e
        L_0x02b3:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r18)
            if (r19 != 0) goto L_0x02bb
            r2 = 0
            goto L_0x02bf
        L_0x02bb:
            int r2 = r19.intValue()
        L_0x02bf:
            org.telegram.ui.LaunchActivity$9 r3 = new org.telegram.ui.LaunchActivity$9
            r4 = r23
            r3.<init>(r4, r0)
            r1.ensureMessagesLoaded(r11, r2, r3)
            goto L_0x0080
        L_0x02cb:
            r4 = r23
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x031a }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x031a }
            if (r1 != 0) goto L_0x031e
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x031a }
            int r2 = r1.size()     // Catch:{ Exception -> 0x031a }
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x031a }
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1     // Catch:{ Exception -> 0x031a }
            if (r0 == 0) goto L_0x0305
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x031a }
            if (r0 == 0) goto L_0x0305
            java.lang.String r2 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r2)     // Catch:{ Exception -> 0x031a }
            if (r0 == 0) goto L_0x0305
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r1)     // Catch:{ Exception -> 0x031a }
            java.lang.String r1 = "FloodWait"
            r2 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x031a }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x031a }
            r0.show()     // Catch:{ Exception -> 0x031a }
            goto L_0x031e
        L_0x0305:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r1)     // Catch:{ Exception -> 0x031a }
            java.lang.String r1 = "NoUsernameFound"
            r2 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x031a }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x031a }
            r0.show()     // Catch:{ Exception -> 0x031a }
            goto L_0x031e
        L_0x031a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x031e:
            if (r9 == 0) goto L_0x0329
            r23.dismiss()     // Catch:{ Exception -> 0x0324 }
            goto L_0x0329
        L_0x0324:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0329:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$25$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$LaunchActivity(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$LaunchActivity(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
        MessagesController.getInstance(i).addUserToChat(i2, tLRPC$User, 0, str, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$29 */
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r8.chat.has_geo != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0079, code lost:
        if (r10.checkCanOpenChat(r7, r11.get(r11.size() - 1)) != false) goto L_0x007b;
     */
    /* renamed from: lambda$null$28 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$28$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x0123
            r0 = 0
            r1 = 1
            if (r10 != 0) goto L_0x00b3
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x00b3
            r8 = r11
            org.telegram.tgnet.TLRPC$ChatInvite r8 = (org.telegram.tgnet.TLRPC$ChatInvite) r8
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            if (r10 == 0) goto L_0x009d
            boolean r10 = org.telegram.messenger.ChatObject.isLeftFromChat(r10)
            if (r10 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            boolean r11 = r10.kicked
            if (r11 != 0) goto L_0x009d
            java.lang.String r10 = r10.username
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 == 0) goto L_0x0033
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek
            if (r10 != 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            boolean r10 = r10.has_geo
            if (r10 == 0) goto L_0x009d
        L_0x0033:
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r8.chat
            r14 = 0
            r10.putChat(r11, r14)
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            org.telegram.tgnet.TLRPC$Chat r11 = r8.chat
            r10.add(r11)
            org.telegram.messenger.MessagesStorage r11 = org.telegram.messenger.MessagesStorage.getInstance(r12)
            r11.putUsersAndChats(r0, r10, r14, r1)
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            int r10 = r10.id
            java.lang.String r11 = "chat_id"
            r7.putInt(r11, r10)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = mainFragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x007b
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            int r0 = r11.size()
            int r0 = r0 - r1
            java.lang.Object r11 = r11.get(r0)
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            boolean r10 = r10.checkCanOpenChat(r7, r11)
            if (r10 == 0) goto L_0x0119
        L_0x007b:
            boolean[] r6 = new boolean[r1]
            org.telegram.ui.-$$Lambda$LaunchActivity$_hA3_aEzWRFWZyU2V9yxBtC0oqk r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$_hA3_aEzWRFWZyU2V9yxBtC0oqk
            r10.<init>(r6)
            r13.setOnCancelListener(r10)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r8.chat
            int r11 = r11.id
            int r11 = -r11
            long r11 = (long) r11
            org.telegram.ui.LaunchActivity$10 r0 = new org.telegram.ui.LaunchActivity$10
            r3 = r0
            r4 = r9
            r5 = r13
            r3.<init>(r5, r6, r7, r8)
            r10.ensureMessagesLoaded(r11, r14, r0)
            r1 = 0
            goto L_0x0119
        L_0x009d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = mainFragmentsStack
            int r11 = r10.size()
            int r11 = r11 - r1
            java.lang.Object r10 = r10.get(r11)
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            org.telegram.ui.Components.JoinGroupAlert r11 = new org.telegram.ui.Components.JoinGroupAlert
            r11.<init>(r9, r8, r14, r10)
            r10.showDialog(r11)
            goto L_0x0119
        L_0x00b3:
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r11.<init>((android.content.Context) r9)
            r12 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r12 = r10.text
            java.lang.String r14 = "FLOOD_WAIT"
            boolean r12 = r12.startsWith(r14)
            if (r12 == 0) goto L_0x00db
            r10 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x010a
        L_0x00db:
            java.lang.String r10 = r10.text
            java.lang.String r12 = "INVITE_HASH_EXPIRED"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00fe
            r10 = 2131625445(0x7f0e05e5, float:1.8878098E38)
            java.lang.String r12 = "ExpiredLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setTitle(r10)
            r10 = 2131625844(0x7f0e0774, float:1.8878907E38)
            java.lang.String r12 = "InviteExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x010a
        L_0x00fe:
            r10 = 2131625889(0x7f0e07a1, float:1.8878999E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x010a:
            r10 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r12 = "OK"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setPositiveButton(r10, r0)
            r9.showAlertDialog(r11)
        L_0x0119:
            if (r1 == 0) goto L_0x0123
            r13.dismiss()     // Catch:{ Exception -> 0x011f }
            goto L_0x0123
        L_0x011f:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0123:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$28$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$27(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$31 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$30 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$32 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$36 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$33 */
    public /* synthetic */ void lambda$null$33$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$41(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$35 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$38 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$37 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$40 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$39 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$43 */
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
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$null$42 */
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
            r8.lambda$runLinkRequest$41(r9)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 2131625322(0x7f0e056a, float:1.8877849E38)
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$44 */
    public /* synthetic */ void lambda$runLinkRequest$44$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$46 */
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$null$45 */
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
            r5 = 2131627690(0x7f0e0eaa, float:1.8882652E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627710(0x7f0e0ebe, float:1.8882692E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627709(0x7f0e0ebd, float:1.888269E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$45$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$48 */
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* renamed from: lambda$null$47 */
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
            r0 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$null$47$LaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$51 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$50 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$49 */
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
            r13 = 2131627141(0x7f0e0CLASSNAME, float:1.8881538E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131627252(0x7f0e0cf4, float:1.8881763E38)
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
        if (z || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(0).lastUpdateCheckTime) >= ((long) (MessagesController.getInstance(0).updateCheckDelay * 1000))) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkAppUpdate$54 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$53 */
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
                    LaunchActivity.this.lambda$showAlertDialog$55$LaunchActivity(dialogInterface);
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showAlertDialog$55 */
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

    public void showBulletin(Function<BulletinFactory, Bulletin> function) {
        BaseFragment baseFragment;
        if (!layerFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = layerFragmentsStack;
            baseFragment = arrayList.get(arrayList.size() - 1);
        } else if (!rightFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
            baseFragment = arrayList2.get(arrayList2.size() - 1);
        } else if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
            baseFragment = arrayList3.get(arrayList3.size() - 1);
        } else {
            baseFragment = null;
        }
        if (BulletinFactory.canShowBulletin(baseFragment)) {
            function.apply(BulletinFactory.of(baseFragment)).show();
        }
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
        int currentAccount2 = dialogsActivity2 != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
        Uri uri = this.exportingChatUri;
        if (uri != null) {
            ArrayList arrayList3 = this.documentsUrisArray != null ? new ArrayList(this.documentsUrisArray) : null;
            AlertDialog alertDialog = new AlertDialog(this, 3);
            SendMessagesHelper.getInstance(currentAccount2).prepareImportHistory(arrayList2.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new MessagesStorage.IntCallback(currentAccount2, dialogsActivity, z, arrayList3, uri, alertDialog) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ DialogsActivity f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ ArrayList f$4;
                public final /* synthetic */ Uri f$5;
                public final /* synthetic */ AlertDialog f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run(int i) {
                    LaunchActivity.this.lambda$didSelectDialogs$56$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, i);
                }
            });
            try {
                alertDialog.showDelayed(300);
            } catch (Exception unused) {
            }
        } else {
            if (arrayList.size() <= 1) {
                long longValue = arrayList2.get(0).longValue();
                int i2 = (int) longValue;
                int i3 = (int) (longValue >> 32);
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (!AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(currentAccount2).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                if (i2 == 0) {
                    bundle.putInt("enc_id", i3);
                } else if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else if (i2 < 0) {
                    bundle.putInt("chat_id", -i2);
                }
                if (MessagesController.getInstance(currentAccount2).checkCanOpenChat(bundle, dialogsActivity2)) {
                    chatActivity = new ChatActivity(bundle);
                } else {
                    return;
                }
            } else {
                chatActivity = null;
            }
            ArrayList<TLRPC$User> arrayList4 = this.contactsToSend;
            int size = arrayList4 != null ? arrayList4.size() + 0 : 0;
            if (this.videoPath != null) {
                size++;
            }
            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList5 = this.photoPathsArray;
            if (arrayList5 != null) {
                size += arrayList5.size();
            }
            ArrayList<String> arrayList6 = this.documentsPathsArray;
            if (arrayList6 != null) {
                size += arrayList6.size();
            }
            ArrayList<Uri> arrayList7 = this.documentsUrisArray;
            if (arrayList7 != null) {
                size += arrayList7.size();
            }
            if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
                size++;
            }
            int i4 = 0;
            while (i4 < arrayList.size()) {
                if (!AlertsCreator.checkSlowMode(this, this.currentAccount, arrayList2.get(i4).longValue(), size > 1)) {
                    i4++;
                } else {
                    return;
                }
            }
            ArrayList<TLRPC$User> arrayList8 = this.contactsToSend;
            if (arrayList8 == null || arrayList8.size() != 1 || mainFragmentsStack.isEmpty()) {
                String str = null;
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    long longValue2 = arrayList2.get(i5).longValue();
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
                            ArrayList arrayList9 = new ArrayList();
                            arrayList9.add(this.videoPath);
                            SendMessagesHelper.prepareSendingDocuments(instance, arrayList9, arrayList9, (ArrayList<Uri>) null, str, (String) null, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                        }
                    }
                    if (this.photoPathsArray != null) {
                        String str4 = this.sendingText;
                        if (str4 != null && str4.length() <= i && this.photoPathsArray.size() == 1) {
                            this.photoPathsArray.get(0).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingMedia(instance, this.photoPathsArray, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, true, 0);
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        String str5 = this.sendingText;
                        if (str5 != null && str5.length() <= i) {
                            ArrayList<String> arrayList10 = this.documentsPathsArray;
                            int size2 = arrayList10 != null ? arrayList10.size() : 0;
                            ArrayList<Uri> arrayList11 = this.documentsUrisArray;
                            if (size2 + (arrayList11 != null ? arrayList11.size() : 0) == 1) {
                                str = this.sendingText;
                                this.sendingText = null;
                            }
                        }
                        SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str, this.documentsMimeType, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                    }
                    String str6 = this.sendingText;
                    if (str6 != null) {
                        SendMessagesHelper.prepareSendingText(instance, str6, longValue2, true, 0);
                    }
                    ArrayList<TLRPC$User> arrayList12 = this.contactsToSend;
                    if (arrayList12 != null && !arrayList12.isEmpty()) {
                        for (int i6 = 0; i6 < this.contactsToSend.size(); i6++) {
                            SendMessagesHelper.getInstance(currentAccount2).sendMessage(this.contactsToSend.get(i6), longValue2, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                    }
                    if (!TextUtils.isEmpty(charSequence)) {
                        SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue2, true, 0);
                    }
                }
            } else {
                ArrayList<BaseFragment> arrayList13 = mainFragmentsStack;
                PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList13.get(arrayList13.size() - 1), (ContactsController.Contact) null, (TLRPC$User) null, this.contactsToSendUri, (File) null, (String) null, (String) null);
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
                ArrayList<BaseFragment> arrayList14 = mainFragmentsStack;
                arrayList14.get(arrayList14.size() - 1).showDialog(phonebookShareAlert);
            }
            if (dialogsActivity2 != null && chatActivity == null) {
                dialogsActivity.finishFragment();
            }
        }
        this.photoPathsArray = null;
        this.videoPath = null;
        this.sendingText = null;
        this.documentsPathsArray = null;
        this.documentsOriginalPathsArray = null;
        this.contactsToSend = null;
        this.contactsToSendUri = null;
        this.exportingChatUri = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didSelectDialogs$56 */
    public /* synthetic */ void lambda$didSelectDialogs$56$LaunchActivity(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, int i2) {
        if (i2 != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (i2 > 0) {
                bundle.putInt("user_id", i2);
            } else {
                bundle.putInt("chat_id", -i2);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setOpenImport();
            this.actionBarLayout.presentFragment(chatActivity, dialogsActivity != null || z, dialogsActivity == null, true, false);
        } else {
            this.documentsUrisArray = arrayList;
            if (arrayList == null) {
                this.documentsUrisArray = new ArrayList<>();
            }
            this.documentsUrisArray.add(0, uri);
            openDialogsToSend(true);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.showBulletin);
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$41(BaseFragment baseFragment) {
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
        if (i != 105) {
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
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
            if (canDrawOverlays) {
                GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                if (groupCallActivity != null) {
                    groupCallActivity.dismissInternal();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        LaunchActivity.this.lambda$onActivityResult$58$LaunchActivity();
                    }
                }, 200);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResult$58 */
    public /* synthetic */ void lambda$onActivityResult$58$LaunchActivity() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
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
                LaunchActivity.this.lambda$showPermissionErrorAlert$59$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPermissionErrorAlert$59 */
    public /* synthetic */ void lambda$showPermissionErrorAlert$59$LaunchActivity(DialogInterface dialogInterface, int i) {
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
                LaunchActivity.lambda$onPause$60(this.f$0);
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

    static /* synthetic */ void lambda$onPause$60(int i) {
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
        ApplicationLoader.mainInterfaceStopped = false;
        GroupCallPip.updateVisibility(this);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = true;
        GroupCallPip.updateVisibility(this);
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
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.dismissInternal();
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
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
        checkWasMutedByAdmin(true);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$nZYI2PWYyxHcYI8dHurda3KvFnQ.INSTANCE);
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
        if (Build.VERSION.SDK_INT >= 23) {
            ApplicationLoader.canDrawOverlays = Settings.canDrawOverlays(this);
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onResume();
        }
    }

    static /* synthetic */ void lambda$onResume$61() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v23, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v37, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v32, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v43, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v50, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v48, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v57, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v64, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v52, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v69, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v83, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v85, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v38, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v110, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v119, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v133, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v135, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v152, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v157, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX WARNING: type inference failed for: r5v0 */
    /* JADX WARNING: type inference failed for: r5v5, types: [int] */
    /* JADX WARNING: type inference failed for: r5v12 */
    /* JADX WARNING: type inference failed for: r5v21 */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0273, code lost:
        if (((org.telegram.ui.ProfileActivity) r2.get(r2.size() - 1)).isSettings() == false) goto L_0x0277;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:315:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0262  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            r3 = r19
            int r4 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r0 != r4) goto L_0x0011
            r16.switchToAvailableAccountOrLogout()
            goto L_0x0753
        L_0x0011:
            int r4 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r5 = 0
            if (r0 != r4) goto L_0x0022
            r0 = r3[r5]
            if (r0 == r1) goto L_0x0753
            r16.onFinish()
            r16.finish()
            goto L_0x0753
        L_0x0022:
            int r4 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r4) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r3 = r1.currentConnectionState
            if (r3 == r0) goto L_0x0753
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
            goto L_0x0753
        L_0x0051:
            int r4 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r4) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r1.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x0753
        L_0x005c:
            int r4 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r7 = "Cancel"
            r8 = 5
            r9 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r10 = "AppName"
            r11 = 4
            r12 = 3
            r13 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r14 = "OK"
            r15 = 2
            r6 = 1
            if (r0 != r4) goto L_0x01a2
            r0 = r3[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r4 = r0.intValue()
            r5 = 6
            if (r4 == r5) goto L_0x01a1
            int r4 = r0.intValue()
            if (r4 != r12) goto L_0x0088
            org.telegram.ui.ActionBar.AlertDialog r4 = r1.proxyErrorDialog
            if (r4 == 0) goto L_0x0088
            goto L_0x01a1
        L_0x0088:
            int r4 = r0.intValue()
            if (r4 != r11) goto L_0x0096
            r0 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r0
            r1.showTosActivity(r2, r0)
            return
        L_0x0096:
            org.telegram.ui.ActionBar.AlertDialog$Builder r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r4.<init>((android.content.Context) r1)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.setTitle(r9)
            int r9 = r0.intValue()
            if (r9 == r15) goto L_0x00c5
            int r9 = r0.intValue()
            if (r9 == r12) goto L_0x00c5
            int r9 = r0.intValue()
            if (r9 == r5) goto L_0x00c5
            r5 = 2131626197(0x7f0e08d5, float:1.8879623E38)
            java.lang.String r9 = "MoreInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            org.telegram.ui.-$$Lambda$LaunchActivity$SCcf-_q-u1JVKeTVoRH2zokW458 r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$SCcf-_q-u1JVKeTVoRH2zokW458
            r9.<init>(r2)
            r4.setNegativeButton(r5, r9)
        L_0x00c5:
            int r2 = r0.intValue()
            if (r2 != r8) goto L_0x00e1
            r0 = 2131626323(0x7f0e0953, float:1.8879879E38)
            java.lang.String r2 = "NobodyLikesSpam3"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r4.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r2 = 0
            r4.setPositiveButton(r0, r2)
            goto L_0x0183
        L_0x00e1:
            r2 = 0
            int r5 = r0.intValue()
            if (r5 != 0) goto L_0x00fd
            r0 = 2131626321(0x7f0e0951, float:1.8879875E38)
            java.lang.String r3 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r4.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r4.setPositiveButton(r0, r2)
            goto L_0x0183
        L_0x00fd:
            int r5 = r0.intValue()
            if (r5 != r6) goto L_0x0117
            r0 = 2131626322(0x7f0e0952, float:1.8879877E38)
            java.lang.String r3 = "NobodyLikesSpam2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r4.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r4.setPositiveButton(r0, r2)
            goto L_0x0183
        L_0x0117:
            int r2 = r0.intValue()
            if (r2 != r15) goto L_0x0156
            r0 = r3[r6]
            java.lang.String r0 = (java.lang.String) r0
            r4.setMessage(r0)
            r0 = r3[r15]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "AUTH_KEY_DROP_"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x014d
            r0 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r2 = 0
            r4.setPositiveButton(r0, r2)
            r0 = 2131626008(0x7f0e0818, float:1.887924E38)
            java.lang.String r2 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$V2eQa3_rAwGNDg0nIbDTI1QF8qk r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$V2eQa3_rAwGNDg0nIbDTI1QF8qk
            r2.<init>()
            r4.setNegativeButton(r0, r2)
            goto L_0x0183
        L_0x014d:
            r2 = 0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r4.setPositiveButton(r0, r2)
            goto L_0x0183
        L_0x0156:
            int r0 = r0.intValue()
            if (r0 != r12) goto L_0x0183
            r0 = 2131627046(0x7f0e0CLASSNAME, float:1.8881345E38)
            java.lang.String r2 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r4.setTitle(r0)
            r0 = 2131627857(0x7f0e0var_, float:1.888299E38)
            java.lang.String r2 = "UseProxyTelegramError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r4.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r2 = 0
            r4.setPositiveButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r4)
            r1.proxyErrorDialog = r0
            return
        L_0x0183:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0753
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r4.create()
            r0.showDialog(r2)
            goto L_0x0753
        L_0x01a1:
            return
        L_0x01a2:
            int r4 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r0 != r4) goto L_0x01f9
            r0 = r3[r5]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r1)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r3.setTitle(r4)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r5 = 0
            r3.setPositiveButton(r4, r5)
            r4 = 2131627444(0x7f0e0db4, float:1.8882153E38)
            java.lang.String r5 = "ShareYouLocationUnableManually"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$clw_9BxtWBWPpwYAnFcwbhkXUaU r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$clw_9BxtWBWPpwYAnFcwbhkXUaU
            r5.<init>(r0, r2)
            r3.setNegativeButton(r4, r5)
            r0 = 2131627443(0x7f0e0db3, float:1.888215E38)
            java.lang.String r2 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0753
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x0753
        L_0x01f9:
            int r4 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r4) goto L_0x020c
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0753
            android.view.View r0 = r0.getChildAt(r5)
            if (r0 == 0) goto L_0x0753
            r0.invalidate()
            goto L_0x0753
        L_0x020c:
            int r4 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r0 != r4) goto L_0x0242
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r2 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x022d
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x022d
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0227 }
            r0.setFlags(r2, r2)     // Catch:{ Exception -> 0x0227 }
            goto L_0x0753
        L_0x0227:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0753
        L_0x022d:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x0753
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x023c }
            r0.clearFlags(r2)     // Catch:{ Exception -> 0x023c }
            goto L_0x0753
        L_0x023c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0753
        L_0x0242:
            int r4 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r0 != r4) goto L_0x027c
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r6) goto L_0x025f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x025f
            r0 = 1
            goto L_0x0260
        L_0x025f:
            r0 = 0
        L_0x0260:
            if (r0 == 0) goto L_0x0276
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r6
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ProfileActivity r2 = (org.telegram.ui.ProfileActivity) r2
            boolean r2 = r2.isSettings()
            if (r2 != 0) goto L_0x0276
            goto L_0x0277
        L_0x0276:
            r5 = r0
        L_0x0277:
            r1.rebuildAllFragments(r5)
            goto L_0x0753
        L_0x027c:
            int r4 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r4) goto L_0x0285
            r1.showLanguageAlert(r5)
            goto L_0x0753
        L_0x0285:
            int r4 = org.telegram.messenger.NotificationCenter.openArticle
            if (r0 != r4) goto L_0x02b7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0292
            return
        L_0x0292:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r6
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r0.setParentActivity(r1, r2)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r2 = r3[r5]
            org.telegram.tgnet.TLRPC$TL_webPage r2 = (org.telegram.tgnet.TLRPC$TL_webPage) r2
            r3 = r3[r6]
            java.lang.String r3 = (java.lang.String) r3
            r0.open(r2, r3)
            goto L_0x0753
        L_0x02b7:
            int r4 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r4) goto L_0x033f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            if (r0 == 0) goto L_0x033e
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02c8
            goto L_0x033e
        L_0x02c8:
            r0 = r3[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            r0.intValue()
            r0 = r3[r6]
            java.util.HashMap r0 = (java.util.HashMap) r0
            r4 = r3[r15]
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            r3 = r3[r12]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r8.fragmentsStack
            int r9 = r8.size()
            int r9 = r9 - r6
            java.lang.Object r6 = r8.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r6 = (org.telegram.ui.ActionBar.BaseFragment) r6
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r8.<init>((android.content.Context) r1)
            r9 = 2131627825(0x7f0e0var_, float:1.8882925E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setTitle(r9)
            r9 = 2131627824(0x7f0e0var_, float:1.8882923E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.-$$Lambda$LaunchActivity$21s7RGBMu1rdDPmDpjfD3x9rS-E r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$21s7RGBMu1rdDPmDpjfD3x9rS-E
            r10.<init>(r2, r0, r4, r3)
            r8.setPositiveButton(r9, r10)
            r9 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY
            r9.<init>(r2, r0, r4, r3)
            r8.setNegativeButton(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$u6y_ASGln28gOCl_6a9-Nj_3XHo r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$u6y_ASGln28gOCl_6a9-Nj_3XHo
            r7.<init>(r2, r0, r4, r3)
            r8.setOnBackButtonListener(r7)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r6.showDialog(r0)
            r0.setCanceledOnTouchOutside(r5)
            goto L_0x0753
        L_0x033e:
            return
        L_0x033f:
            int r2 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r4 = 21
            if (r0 != r2) goto L_0x03a0
            r0 = r3[r5]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x038f
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0379
            java.lang.String r2 = "chats_menuBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r3)
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
        L_0x0379:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x038f
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x038f }
            java.lang.String r2 = "actionBarDefault"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)     // Catch:{ Exception -> 0x038f }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r3
            r3 = 0
            r0.<init>(r3, r3, r2)     // Catch:{ Exception -> 0x038f }
            r1.setTaskDescription(r0)     // Catch:{ Exception -> 0x038f }
        L_0x038f:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBehindKeyboardColor(r2)
            r16.checkSystemBarColors()
            goto L_0x0753
        L_0x03a0:
            int r2 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r2) goto L_0x0503
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x04d7
            r0 = r3[r15]
            if (r0 == 0) goto L_0x04d7
            android.widget.ImageView r0 = r1.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03b5
            return
        L_0x03b5:
            r0 = r3[r15]     // Catch:{ all -> 0x04bf }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x04bf }
            r2 = r3[r11]     // Catch:{ all -> 0x04bf }
            java.lang.Boolean r2 = (java.lang.Boolean) r2     // Catch:{ all -> 0x04bf }
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x04bf }
            r4 = r3[r8]     // Catch:{ all -> 0x04bf }
            org.telegram.ui.Components.RLottieImageView r4 = (org.telegram.ui.Components.RLottieImageView) r4     // Catch:{ all -> 0x04bf }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            int r7 = r7.getMeasuredWidth()     // Catch:{ all -> 0x04bf }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            int r8 = r8.getMeasuredHeight()     // Catch:{ all -> 0x04bf }
            if (r2 != 0) goto L_0x03d6
            r4.setVisibility(r11)     // Catch:{ all -> 0x04bf }
        L_0x03d6:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            int r9 = r9.getMeasuredWidth()     // Catch:{ all -> 0x04bf }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            int r10 = r10.getMeasuredHeight()     // Catch:{ all -> 0x04bf }
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x04bf }
            android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r9, r10, r11)     // Catch:{ all -> 0x04bf }
            android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x04bf }
            r10.<init>(r9)     // Catch:{ all -> 0x04bf }
            java.util.HashMap r11 = new java.util.HashMap     // Catch:{ all -> 0x04bf }
            r11.<init>()     // Catch:{ all -> 0x04bf }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            r1.invalidateCachedViews(r11)     // Catch:{ all -> 0x04bf }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            r11.draw(r10)     // Catch:{ all -> 0x04bf }
            android.widget.FrameLayout r10 = r1.frameLayout     // Catch:{ all -> 0x04bf }
            android.widget.ImageView r11 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
            r10.removeView(r11)     // Catch:{ all -> 0x04bf }
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r2 == 0) goto L_0x041b
            android.widget.FrameLayout r13 = r1.frameLayout     // Catch:{ all -> 0x04bf }
            android.widget.ImageView r14 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x04bf }
            r13.addView(r14, r5, r10)     // Catch:{ all -> 0x04bf }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04bf }
            r11 = 8
            r10.setVisibility(r11)     // Catch:{ all -> 0x04bf }
            goto L_0x044c
        L_0x041b:
            android.widget.FrameLayout r13 = r1.frameLayout     // Catch:{ all -> 0x04bf }
            android.widget.ImageView r14 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x04bf }
            r13.addView(r14, r6, r10)     // Catch:{ all -> 0x04bf }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04bf }
            r11 = r0[r5]     // Catch:{ all -> 0x04bf }
            r13 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x04bf }
            int r11 = r11 - r14
            float r11 = (float) r11     // Catch:{ all -> 0x04bf }
            r10.setTranslationX(r11)     // Catch:{ all -> 0x04bf }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04bf }
            r11 = r0[r6]     // Catch:{ all -> 0x04bf }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x04bf }
            int r11 = r11 - r13
            float r11 = (float) r11     // Catch:{ all -> 0x04bf }
            r10.setTranslationY(r11)     // Catch:{ all -> 0x04bf }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04bf }
            r10.setVisibility(r5)     // Catch:{ all -> 0x04bf }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04bf }
            r10.invalidate()     // Catch:{ all -> 0x04bf }
        L_0x044c:
            android.widget.ImageView r10 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
            r10.setImageBitmap(r9)     // Catch:{ all -> 0x04bf }
            android.widget.ImageView r9 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
            r9.setVisibility(r5)     // Catch:{ all -> 0x04bf }
            org.telegram.ui.Components.RLottieDrawable r9 = r4.getAnimatedDrawable()     // Catch:{ all -> 0x04bf }
            r1.themeSwitchSunDrawable = r9     // Catch:{ all -> 0x04bf }
            r9 = r0[r5]     // Catch:{ all -> 0x04bf }
            int r9 = r7 - r9
            r10 = r0[r5]     // Catch:{ all -> 0x04bf }
            int r7 = r7 - r10
            int r9 = r9 * r7
            r7 = r0[r6]     // Catch:{ all -> 0x04bf }
            int r7 = r8 - r7
            r10 = r0[r6]     // Catch:{ all -> 0x04bf }
            int r10 = r8 - r10
            int r7 = r7 * r10
            int r9 = r9 + r7
            double r9 = (double) r9     // Catch:{ all -> 0x04bf }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x04bf }
            r7 = r0[r5]     // Catch:{ all -> 0x04bf }
            r11 = r0[r5]     // Catch:{ all -> 0x04bf }
            int r7 = r7 * r11
            r11 = r0[r6]     // Catch:{ all -> 0x04bf }
            int r11 = r8 - r11
            r13 = r0[r6]     // Catch:{ all -> 0x04bf }
            int r8 = r8 - r13
            int r11 = r11 * r8
            int r7 = r7 + r11
            double r7 = (double) r7     // Catch:{ all -> 0x04bf }
            double r7 = java.lang.Math.sqrt(r7)     // Catch:{ all -> 0x04bf }
            double r7 = java.lang.Math.max(r9, r7)     // Catch:{ all -> 0x04bf }
            float r7 = (float) r7     // Catch:{ all -> 0x04bf }
            if (r2 == 0) goto L_0x0494
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04bf }
            goto L_0x0496
        L_0x0494:
            android.widget.ImageView r8 = r1.themeSwitchImageView     // Catch:{ all -> 0x04bf }
        L_0x0496:
            r9 = r0[r5]     // Catch:{ all -> 0x04bf }
            r0 = r0[r6]     // Catch:{ all -> 0x04bf }
            r10 = 0
            if (r2 == 0) goto L_0x049f
            r11 = 0
            goto L_0x04a0
        L_0x049f:
            r11 = r7
        L_0x04a0:
            if (r2 == 0) goto L_0x04a3
            goto L_0x04a4
        L_0x04a3:
            r7 = 0
        L_0x04a4:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r8, r9, r0, r11, r7)     // Catch:{ all -> 0x04bf }
            r7 = 400(0x190, double:1.976E-321)
            r0.setDuration(r7)     // Catch:{ all -> 0x04bf }
            android.view.animation.Interpolator r7 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x04bf }
            r0.setInterpolator(r7)     // Catch:{ all -> 0x04bf }
            org.telegram.ui.LaunchActivity$11 r7 = new org.telegram.ui.LaunchActivity$11     // Catch:{ all -> 0x04bf }
            r7.<init>(r2, r4)     // Catch:{ all -> 0x04bf }
            r0.addListener(r7)     // Catch:{ all -> 0x04bf }
            r0.start()     // Catch:{ all -> 0x04bf }
            r0 = 1
            goto L_0x04d8
        L_0x04bf:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04d3 }
            r2 = 0
            r0.setImageDrawable(r2)     // Catch:{ Exception -> 0x04d3 }
            android.widget.FrameLayout r0 = r1.frameLayout     // Catch:{ Exception -> 0x04d3 }
            android.widget.ImageView r2 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04d3 }
            r0.removeView(r2)     // Catch:{ Exception -> 0x04d3 }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r5     // Catch:{ Exception -> 0x04d3 }
            goto L_0x04d7
        L_0x04d3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04d7:
            r0 = 0
        L_0x04d8:
            r2 = r3[r5]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r2
            r4 = r3[r6]
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            r3 = r3[r12]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.actionBarLayout
            r5.animateThemedValues(r2, r3, r4, r0)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0753
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.layersActionBarLayout
            r5.animateThemedValues(r2, r3, r4, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.rightActionBarLayout
            r5.animateThemedValues(r2, r3, r4, r0)
            goto L_0x0753
        L_0x0503:
            int r2 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r2) goto L_0x0534
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x0753
            r2 = r3[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r0 = r0.getChildCount()
        L_0x0513:
            if (r5 >= r0) goto L_0x0753
            org.telegram.ui.Components.RecyclerListView r3 = r1.sideMenu
            android.view.View r3 = r3.getChildAt(r5)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r4 == 0) goto L_0x0531
            r4 = r3
            org.telegram.ui.Cells.DrawerUserCell r4 = (org.telegram.ui.Cells.DrawerUserCell) r4
            int r4 = r4.getAccountNumber()
            int r6 = r2.intValue()
            if (r4 != r6) goto L_0x0531
            r3.invalidate()
            goto L_0x0753
        L_0x0531:
            int r5 = r5 + 1
            goto L_0x0513
        L_0x0534:
            int r2 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r2) goto L_0x0543
            r0 = r3[r5]     // Catch:{ all -> 0x0753 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x0753 }
            r2 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r1, r2)     // Catch:{ all -> 0x0753 }
            goto L_0x0753
        L_0x0543:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r0 != r2) goto L_0x0606
            java.lang.String r0 = r1.loadingThemeFileName
            if (r0 == 0) goto L_0x05d1
            r2 = r3[r5]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0753
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
            if (r2 == 0) goto L_0x05cc
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x05b5
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x05b5
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r2.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r2.account
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$DjRqksnHFWXNPJJVV5f_Ep2g8-I r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$DjRqksnHFWXNPJJVV5f_Ep2g8-I
            r4.<init>(r2)
            r3.sendRequest(r0, r4)
            return
        L_0x05b5:
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r3, r2, r6)
            if (r8 == 0) goto L_0x05cc
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r9 = 1
            r10 = 0
            r11 = 0
            r12 = 0
            r7 = r0
            r7.<init>(r8, r9, r10, r11, r12)
            r1.lambda$runLinkRequest$41(r0)
        L_0x05cc:
            r16.onThemeLoadFinish()
            goto L_0x0753
        L_0x05d1:
            java.lang.String r0 = r1.loadingThemeWallpaperName
            if (r0 == 0) goto L_0x0753
            r2 = r3[r5]
            java.lang.String r2 = (java.lang.String) r2
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0753
            r2 = 0
            r1.loadingThemeWallpaperName = r2
            r0 = r3[r6]
            java.io.File r0 = (java.io.File) r0
            boolean r2 = r1.loadingThemeAccent
            if (r2 == 0) goto L_0x05f8
            org.telegram.tgnet.TLRPC$TL_theme r0 = r1.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r1.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.loadingThemeInfo
            r1.openThemeAccentPreview(r0, r2, r3)
            r16.onThemeLoadFinish()
            goto L_0x0753
        L_0x05f8:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$xVGtG59-qSVwAX72hqav3yEKPE8 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$xVGtG59-qSVwAX72hqav3yEKPE8
            r4.<init>(r2, r0)
            r3.postRunnable(r4)
            goto L_0x0753
        L_0x0606:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r0 != r2) goto L_0x0623
            r0 = r3[r5]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = r1.loadingThemeFileName
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x061e
            java.lang.String r2 = r1.loadingThemeWallpaperName
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0753
        L_0x061e:
            r16.onThemeLoadFinish()
            goto L_0x0753
        L_0x0623:
            int r2 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r2) goto L_0x063a
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x062c
            return
        L_0x062c:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x0635
            r16.onPasscodeResume()
            goto L_0x0753
        L_0x0635:
            r16.onPasscodePause()
            goto L_0x0753
        L_0x063a:
            int r2 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r2) goto L_0x0643
            r16.checkSystemBarColors()
            goto L_0x0753
        L_0x0643:
            int r2 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r0 != r2) goto L_0x0670
            int r0 = r3.length
            if (r0 <= r6) goto L_0x0753
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0753
            int r0 = r1.currentAccount
            r2 = r3[r15]
            org.telegram.tgnet.TLRPC$TL_error r2 = (org.telegram.tgnet.TLRPC$TL_error) r2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r7 = r4.size()
            int r7 = r7 - r6
            java.lang.Object r4 = r4.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r3 = r3[r6]
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.ui.Components.AlertsCreator.processError(r0, r2, r4, r3, r5)
            goto L_0x0753
        L_0x0670:
            int r2 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r0 != r2) goto L_0x074c
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0753
            r0 = r3[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            boolean r2 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r2 == 0) goto L_0x0691
            org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r2 == 0) goto L_0x0691
            android.widget.FrameLayout r2 = r2.getContainer()
            goto L_0x0692
        L_0x0691:
            r2 = 0
        L_0x0692:
            if (r2 != 0) goto L_0x06a2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r6
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            goto L_0x06a3
        L_0x06a2:
            r4 = 0
        L_0x06a3:
            if (r0 != r12) goto L_0x06d2
            r0 = r3[r6]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            if (r0 <= 0) goto L_0x06b5
            r0 = 2131628257(0x7f0e10e1, float:1.8883802E38)
            java.lang.String r3 = "YourNameChanged"
            goto L_0x06ba
        L_0x06b5:
            r0 = 2131624767(0x7f0e033f, float:1.8876723E38)
            java.lang.String r3 = "CannelTitleChanged"
        L_0x06ba:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            if (r2 == 0) goto L_0x06c5
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            goto L_0x06c9
        L_0x06c5:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
        L_0x06c9:
            org.telegram.ui.Components.Bulletin r0 = r2.createErrorBulletin(r0)
            r0.show()
            goto L_0x0753
        L_0x06d2:
            if (r0 != r15) goto L_0x0700
            r0 = r3[r6]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            if (r0 <= 0) goto L_0x06e4
            r0 = 2131628240(0x7f0e10d0, float:1.8883767E38)
            java.lang.String r3 = "YourBioChanged"
            goto L_0x06e9
        L_0x06e4:
            r0 = 2131624706(0x7f0e0302, float:1.88766E38)
            java.lang.String r3 = "CannelDescriptionChanged"
        L_0x06e9:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            if (r2 == 0) goto L_0x06f4
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            goto L_0x06f8
        L_0x06f4:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
        L_0x06f8:
            org.telegram.ui.Components.Bulletin r0 = r2.createErrorBulletin(r0)
            r0.show()
            goto L_0x0753
        L_0x0700:
            if (r0 != 0) goto L_0x0728
            r0 = r3[r6]
            org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
            org.telegram.ui.Components.StickerSetBulletinLayout r5 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = r3[r15]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r6 = 0
            r5.<init>(r1, r6, r3, r0)
            r0 = 1500(0x5dc, float:2.102E-42)
            if (r4 == 0) goto L_0x0720
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r4, (org.telegram.ui.Components.Bulletin.Layout) r5, (int) r0)
            r0.show()
            goto L_0x0753
        L_0x0720:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r2, (org.telegram.ui.Components.Bulletin.Layout) r5, (int) r0)
            r0.show()
            goto L_0x0753
        L_0x0728:
            if (r0 != r6) goto L_0x0753
            if (r4 == 0) goto L_0x073c
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
            r2 = r3[r6]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)
            r0.show()
            goto L_0x0753
        L_0x073c:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            r2 = r3[r6]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)
            r0.show()
            goto L_0x0753
        L_0x074c:
            int r2 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r0 != r2) goto L_0x0753
            r1.checkWasMutedByAdmin(r5)
        L_0x0753:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$62(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$63 */
    public /* synthetic */ void lambda$didReceivedNotification$63$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$65 */
    public /* synthetic */ void lambda$didReceivedNotification$65$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
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
                        LaunchActivity.lambda$null$64(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$41(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$null$64(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$70 */
    public /* synthetic */ void lambda$didReceivedNotification$70$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$69$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$69 */
    public /* synthetic */ void lambda$null$69$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    /* renamed from: lambda$didReceivedNotification$72 */
    public /* synthetic */ void lambda$didReceivedNotification$72$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$71$LaunchActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$71 */
    public /* synthetic */ void lambda$null$71$LaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$41(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
            }
            onThemeLoadFinish();
        }
    }

    private void invalidateCachedViews(View view) {
        if (view.getLayerType() != 0) {
            view.invalidate();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateCachedViews(viewGroup.getChildAt(i));
            }
        }
    }

    private void checkWasMutedByAdmin(boolean z) {
        ChatObject.Call call;
        int i;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance == null || (call = sharedInstance.groupCall) == null) {
            this.wasMutedByAdminRaisedHand = false;
            return;
        }
        boolean z3 = this.wasMutedByAdminRaisedHand;
        TLRPC$InputPeer groupCallPeer = sharedInstance.getGroupCallPeer();
        if (groupCallPeer != null) {
            i = groupCallPeer.user_id;
            if (i == 0) {
                int i2 = groupCallPeer.chat_id;
                if (i2 != 0) {
                    i = -i2;
                } else {
                    i = -groupCallPeer.channel_id;
                }
            }
        } else {
            i = UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(i);
        boolean z4 = tLRPC$TL_groupCallParticipant != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted;
        if (z4 && tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
            z2 = true;
        }
        this.wasMutedByAdminRaisedHand = z2;
        if (!z && z3 && !z2 && !z4 && GroupCallActivity.groupCallInstance == null) {
            showVoiceChatTooltip(38);
        }
    }

    private void showVoiceChatTooltip(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && !mainFragmentsStack.isEmpty() && sharedInstance.groupCall != null && !mainFragmentsStack.isEmpty()) {
            TLRPC$Chat chat = sharedInstance.getChat();
            ArrayList<BaseFragment> arrayList = this.actionBarLayout.fragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
            if (baseFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                if (chatActivity.getDialogId() == ((long) (-chat.id))) {
                    chat = null;
                }
                chatActivity.getUndoView().showWithAction(0, i, (Object) chat);
            } else if (baseFragment instanceof DialogsActivity) {
                ((DialogsActivity) baseFragment).getUndoView().showWithAction(0, i, (Object) chat);
            } else if (baseFragment instanceof ProfileActivity) {
                ((ProfileActivity) baseFragment).getUndoView().showWithAction(0, i, (Object) chat);
            }
            if (i == 38 && VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().playAllowTalkSound();
            }
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
        lambda$runLinkRequest$41(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
                    LaunchActivity.this.lambda$checkFreeDiscSpace$74$LaunchActivity();
                }
            }, 2000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkFreeDiscSpace$74 */
    public /* synthetic */ void lambda$checkFreeDiscSpace$74$LaunchActivity() {
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
                                LaunchActivity.this.lambda$null$73$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$73 */
    public /* synthetic */ void lambda$null$73$LaunchActivity() {
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
            r9 = 2131624886(0x7f0e03b6, float:1.8876964E38)
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
            r14 = 2131625310(0x7f0e055e, float:1.8877824E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$GzZv3SpdQcwOop2ZD24j4UDs0b8 r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$GzZv3SpdQcwOop2ZD24j4UDs0b8     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624887(0x7f0e03b7, float:1.8876966E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$ArO1NT8ZqCZlhD2rO0IVbTJrd2o r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$ArO1NT8ZqCZlhD2rO0IVbTJrd2o     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$RDJjmjvX9b3dNBMFeBmYVriSiFk r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$RDJjmjvX9b3dNBMFeBmYVriSiFk     // Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$75(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlertInternal$76 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$76$LaunchActivity(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$41(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlertInternal$77 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$77$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                    LaunchActivity.this.lambda$showLanguageAlert$79$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                                    LaunchActivity.this.lambda$showLanguageAlert$81$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$showLanguageAlert$79 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$78 */
    public /* synthetic */ void lambda$null$78$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$81 */
    public /* synthetic */ void lambda$showLanguageAlert$81$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$null$80$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$80 */
    public /* synthetic */ void lambda$null$80$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass12 r0 = new Runnable() {
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
            $$Lambda$LaunchActivity$XHl69xs9YAkfuP4uNzotSBE4NOM r4 = null;
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
                        LaunchActivity.this.lambda$updateCurrentConnectionState$82$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$updateCurrentConnectionState$82 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$82$LaunchActivity() {
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
            r2.lambda$runLinkRequest$41(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$82$LaunchActivity():void");
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
        if (keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
            boolean z = true;
            if (VoIPService.getSharedInstance() != null) {
                if (Build.VERSION.SDK_INT >= 31 && !SharedConfig.useMediaStream) {
                    boolean isSpeakerMuted = WebRtcAudioTrack.isSpeakerMuted();
                    AudioManager audioManager = (AudioManager) getSystemService("audio");
                    if (!(audioManager.getStreamVolume(0) == audioManager.getStreamMinVolume(0) && keyEvent.getKeyCode() == 25)) {
                        z = false;
                    }
                    WebRtcAudioTrack.setSpeakerMute(z);
                    if (isSpeakerMuted != WebRtcAudioTrack.isSpeakerMuted()) {
                        showVoiceChatTooltip(z ? 42 : 43);
                    }
                }
            } else if (!mainFragmentsStack.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0)) {
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
