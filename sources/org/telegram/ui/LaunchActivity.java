package org.telegram.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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
import org.telegram.messenger.voip.VideoCapturerDevice;
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
import org.telegram.tgnet.TLRPC$TL_document;
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
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
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
import org.telegram.ui.ActionBar.SimpleTextView;
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
import org.telegram.ui.Components.RadialProgress2;
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
    /* access modifiers changed from: private */
    public FrameLayout updateLayout;
    /* access modifiers changed from: private */
    public RadialProgress2 updateLayoutIcon;
    private TextView updateSizeTextView;
    private SimpleTextView updateTextView;
    private String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;
    private boolean wasMutedByAdminRaisedHand;

    static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:45|46|47|48|49) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x0114 */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r24) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            java.lang.String r3 = "flyme"
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r0 = r23.getResources()
            android.content.res.Configuration r0 = r0.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r1, r0)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r1.currentAccount = r0
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            r4 = 1
            r5 = 0
            if (r0 != 0) goto L_0x00f3
            android.content.Intent r0 = r23.getIntent()
            if (r0 == 0) goto L_0x008c
            java.lang.String r6 = r0.getAction()
            if (r6 == 0) goto L_0x008c
            java.lang.String r6 = r0.getAction()
            java.lang.String r7 = "android.intent.action.SEND"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0085
            java.lang.String r6 = r0.getAction()
            java.lang.String r7 = "android.intent.action.SEND_MULTIPLE"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0049
            goto L_0x0085
        L_0x0049:
            java.lang.String r6 = r0.getAction()
            java.lang.String r7 = "android.intent.action.VIEW"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x008c
            android.net.Uri r6 = r0.getData()
            if (r6 == 0) goto L_0x008c
            java.lang.String r6 = r6.toString()
            java.lang.String r6 = r6.toLowerCase()
            java.lang.String r7 = "tg:proxy"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x0083
            java.lang.String r7 = "tg://proxy"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x0083
            java.lang.String r7 = "tg:socks"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x0083
            java.lang.String r7 = "tg://socks"
            boolean r6 = r6.startsWith(r7)
            if (r6 == 0) goto L_0x008c
        L_0x0083:
            r6 = 1
            goto L_0x008d
        L_0x0085:
            super.onCreate(r24)
            r23.finish()
            return
        L_0x008c:
            r6 = 0
        L_0x008d:
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r8 = "intro_crashed_time"
            r9 = 0
            long r11 = r7.getLong(r8, r9)
            if (r0 == 0) goto L_0x00a5
            java.lang.String r13 = "fromIntro"
            boolean r13 = r0.getBooleanExtra(r13, r5)
            if (r13 == 0) goto L_0x00a5
            r13 = 1
            goto L_0x00a6
        L_0x00a5:
            r13 = 0
        L_0x00a6:
            if (r13 == 0) goto L_0x00b3
            android.content.SharedPreferences$Editor r7 = r7.edit()
            android.content.SharedPreferences$Editor r7 = r7.putLong(r8, r9)
            r7.commit()
        L_0x00b3:
            if (r6 != 0) goto L_0x00f3
            long r6 = java.lang.System.currentTimeMillis()
            long r11 = r11 - r6
            long r6 = java.lang.Math.abs(r11)
            r8 = 120000(0x1d4c0, double:5.9288E-319)
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 < 0) goto L_0x00f3
            if (r0 == 0) goto L_0x00f3
            if (r13 != 0) goto L_0x00f3
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "logininfo2"
            android.content.SharedPreferences r6 = r6.getSharedPreferences(r7, r5)
            java.util.Map r6 = r6.getAll()
            boolean r6 = r6.isEmpty()
            if (r6 == 0) goto L_0x00f3
            android.content.Intent r3 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r4 = org.telegram.ui.IntroActivity.class
            r3.<init>(r1, r4)
            android.net.Uri r0 = r0.getData()
            r3.setData(r0)
            r1.startActivity(r3)
            super.onCreate(r24)
            r23.finish()
            return
        L_0x00f3:
            r1.requestWindowFeature(r4)
            r0 = 2131689484(0x7f0var_c, float:1.9007985E38)
            r1.setTheme(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 0
            r7 = 21
            if (r0 < r7) goto L_0x011d
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r8 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0114 }
            java.lang.String r9 = "actionBarDefault"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)     // Catch:{ Exception -> 0x0114 }
            r9 = r9 | r0
            r8.<init>(r6, r6, r9)     // Catch:{ Exception -> 0x0114 }
            r1.setTaskDescription(r8)     // Catch:{ Exception -> 0x0114 }
        L_0x0114:
            android.view.Window r8 = r23.getWindow()     // Catch:{ Exception -> 0x011c }
            r8.setNavigationBarColor(r0)     // Catch:{ Exception -> 0x011c }
            goto L_0x011d
        L_0x011c:
        L_0x011d:
            android.view.Window r0 = r23.getWindow()
            r8 = 2131166107(0x7var_b, float:1.794645E38)
            r0.setBackgroundDrawableResource(r8)
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0141
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0141
            android.view.Window r0 = r23.getWindow()     // Catch:{ Exception -> 0x013d }
            r8 = 8192(0x2000, float:1.14794E-41)
            r0.setFlags(r8, r8)     // Catch:{ Exception -> 0x013d }
            goto L_0x0141
        L_0x013d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0141:
            super.onCreate(r24)
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 24
            if (r0 < r8) goto L_0x0150
            boolean r9 = r23.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r9
        L_0x0150:
            org.telegram.ui.ActionBar.Theme.createChatResources(r1, r5)
            java.lang.String r9 = org.telegram.messenger.SharedConfig.passcodeHash
            int r9 = r9.length()
            if (r9 == 0) goto L_0x0169
            boolean r9 = org.telegram.messenger.SharedConfig.appLocked
            if (r9 == 0) goto L_0x0169
            long r9 = android.os.SystemClock.elapsedRealtime()
            r11 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 / r11
            int r10 = (int) r9
            org.telegram.messenger.SharedConfig.lastPauseTime = r10
        L_0x0169:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r23)
            org.telegram.ui.LaunchActivity$1 r9 = new org.telegram.ui.LaunchActivity$1
            r9.<init>(r1)
            r1.actionBarLayout = r9
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            r1.frameLayout = r9
            android.view.ViewGroup$LayoutParams r10 = new android.view.ViewGroup$LayoutParams
            r11 = -1
            r10.<init>(r11, r11)
            r1.setContentView(r9, r10)
            r9 = 8
            if (r0 < r7) goto L_0x0191
            android.widget.ImageView r10 = new android.widget.ImageView
            r10.<init>(r1)
            r1.themeSwitchImageView = r10
            r10.setVisibility(r9)
        L_0x0191:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = new org.telegram.ui.ActionBar.DrawerLayoutContainer
            r10.<init>(r1)
            r1.drawerLayoutContainer = r10
            java.lang.String r12 = "windowBackgroundWhite"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r10.setBehindKeyboardColor(r12)
            android.widget.FrameLayout r10 = r1.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r1.drawerLayoutContainer
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r13)
            r10.addView(r12, r14)
            if (r0 < r7) goto L_0x01c9
            org.telegram.ui.LaunchActivity$2 r10 = new org.telegram.ui.LaunchActivity$2
            r10.<init>(r1)
            r1.themeSwitchSunView = r10
            android.widget.FrameLayout r12 = r1.frameLayout
            r14 = 48
            r15 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15)
            r12.addView(r10, r14)
            android.view.View r10 = r1.themeSwitchSunView
            r10.setVisibility(r9)
        L_0x01c9:
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x02b3
            android.view.Window r10 = r23.getWindow()
            r12 = 16
            r10.setSoftInputMode(r12)
            org.telegram.ui.LaunchActivity$3 r10 = new org.telegram.ui.LaunchActivity$3
            r10.<init>(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r1.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r13)
            r12.addView(r10, r14)
            android.view.View r12 = new android.view.View
            r12.<init>(r1)
            r1.backgroundTablet = r12
            android.content.res.Resources r12 = r23.getResources()
            r14 = 2131165338(0x7var_a, float:1.794489E38)
            android.graphics.drawable.Drawable r12 = r12.getDrawable(r14)
            android.graphics.drawable.BitmapDrawable r12 = (android.graphics.drawable.BitmapDrawable) r12
            android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.REPEAT
            r12.setTileModeXY(r14, r14)
            android.view.View r14 = r1.backgroundTablet
            r14.setBackgroundDrawable(r12)
            android.view.View r12 = r1.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createRelative(r11, r11)
            r10.addView(r12, r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.actionBarLayout
            r10.addView(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = new org.telegram.ui.ActionBar.ActionBarLayout
            r12.<init>(r1)
            r1.rightActionBarLayout = r12
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = rightFragmentsStack
            r12.init(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.rightActionBarLayout
            r12.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.rightActionBarLayout
            r10.addView(r12)
            android.widget.FrameLayout r12 = new android.widget.FrameLayout
            r12.<init>(r1)
            r1.shadowTabletSide = r12
            r14 = 1076449908(0x40295274, float:2.6456575)
            r12.setBackgroundColor(r14)
            android.widget.FrameLayout r12 = r1.shadowTabletSide
            r10.addView(r12)
            android.widget.FrameLayout r12 = new android.widget.FrameLayout
            r12.<init>(r1)
            r1.shadowTablet = r12
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = layerFragmentsStack
            boolean r14 = r14.isEmpty()
            if (r14 == 0) goto L_0x024c
            r14 = 8
            goto L_0x024d
        L_0x024c:
            r14 = 0
        L_0x024d:
            r12.setVisibility(r14)
            android.widget.FrameLayout r12 = r1.shadowTablet
            r14 = 2130706432(0x7var_, float:1.7014118E38)
            r12.setBackgroundColor(r14)
            android.widget.FrameLayout r12 = r1.shadowTablet
            r10.addView(r12)
            android.widget.FrameLayout r12 = r1.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw r14 = new org.telegram.ui.-$$Lambda$LaunchActivity$kQBSb900ZfovzV4C2-TBJIVramw
            r14.<init>()
            r12.setOnTouchListener(r14)
            android.widget.FrameLayout r12 = r1.shadowTablet
            org.telegram.ui.-$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7-S05A r14 = org.telegram.ui.$$Lambda$LaunchActivity$2viHQ7iQGsryXfoyH4fBq7S05A.INSTANCE
            r12.setOnClickListener(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = new org.telegram.ui.ActionBar.ActionBarLayout
            r12.<init>(r1)
            r1.layersActionBarLayout = r12
            r12.setRemoveActionBarExtraHeight(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            android.widget.FrameLayout r14 = r1.shadowTablet
            r12.setBackgroundView(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            r12.setUseAlphaAnimations(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            r14 = 2131165301(0x7var_, float:1.7944815E38)
            r12.setBackgroundResource(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = layerFragmentsStack
            r12.init(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            r12.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r14 = r1.drawerLayoutContainer
            r12.setDrawerLayoutContainer(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = layerFragmentsStack
            boolean r14 = r14.isEmpty()
            if (r14 == 0) goto L_0x02a9
            goto L_0x02aa
        L_0x02a9:
            r9 = 0
        L_0x02aa:
            r12.setVisibility(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r1.layersActionBarLayout
            r10.addView(r9)
            goto L_0x02bf
        L_0x02b3:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r1.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r1.actionBarLayout
            android.view.ViewGroup$LayoutParams r12 = new android.view.ViewGroup$LayoutParams
            r12.<init>(r11, r11)
            r9.addView(r10, r12)
        L_0x02bf:
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            org.telegram.ui.LaunchActivity$4 r10 = new org.telegram.ui.LaunchActivity$4
            r10.<init>(r1)
            r1.sideMenu = r10
            org.telegram.ui.Components.SideMenultItemAnimator r12 = new org.telegram.ui.Components.SideMenultItemAnimator
            r12.<init>(r10)
            r1.itemAnimator = r12
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            r10.setItemAnimator(r12)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            java.lang.String r12 = "chats_menuBackground"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r10.setBackgroundColor(r12)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r12 = new androidx.recyclerview.widget.LinearLayoutManager
            r12.<init>(r1, r4, r5)
            r10.setLayoutManager(r12)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            r10.setAllowItemsInteractionDuringAnimation(r5)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r12 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r14 = r1.itemAnimator
            r12.<init>(r1, r14)
            r1.drawerLayoutAdapter = r12
            r10.setAdapter(r12)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r13)
            r9.addView(r10, r12)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer
            r10.setDrawerLayout(r9)
            android.view.ViewGroup$LayoutParams r10 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r10 = (android.widget.FrameLayout.LayoutParams) r10
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r14 = org.telegram.messenger.AndroidUtilities.isTablet()
            r15 = 1134559232(0x43a00000, float:320.0)
            if (r14 == 0) goto L_0x0324
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x033b
        L_0x0324:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r12.x
            int r12 = r12.y
            int r12 = java.lang.Math.min(r15, r12)
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = r12 - r15
            int r12 = java.lang.Math.min(r14, r12)
        L_0x033b:
            r10.width = r12
            r10.height = r11
            r9.setLayoutParams(r10)
            org.telegram.ui.Components.RecyclerListView r10 = r1.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s r12 = new org.telegram.ui.-$$Lambda$LaunchActivity$c7BMHFAZYRAqqOiXCrFzi2Les1s
            r12.<init>()
            r10.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r12)
            androidx.recyclerview.widget.ItemTouchHelper r10 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$5 r12 = new org.telegram.ui.LaunchActivity$5
            r14 = 3
            r12.<init>(r14, r5)
            r10.<init>(r12)
            org.telegram.ui.Components.RecyclerListView r12 = r1.sideMenu
            r10.attachToRecyclerView(r12)
            org.telegram.ui.Components.RecyclerListView r12 = r1.sideMenu
            org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas r15 = new org.telegram.ui.-$$Lambda$LaunchActivity$YILGR3yadeycI2j_s8n6fLMaQas
            r15.<init>(r10)
            r12.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r15)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r1.actionBarLayout
            r10.setParentActionBarLayout(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r1.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r1.drawerLayoutContainer
            r10.setDrawerLayoutContainer(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r12 = mainFragmentsStack
            r10.init(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r1.actionBarLayout
            r10.setDelegate(r1)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            org.telegram.ui.LaunchActivity$7 r10 = new org.telegram.ui.LaunchActivity$7
            r10.<init>(r1)
            r1.updateLayout = r10
            r10.setWillNotDraw(r5)
            android.widget.FrameLayout r10 = r1.updateLayout
            r12 = 4
            r10.setVisibility(r12)
            android.widget.FrameLayout r10 = r1.updateLayout
            r15 = 1110441984(0x42300000, float:44.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r8 = (float) r8
            r10.setTranslationY(r8)
            if (r0 < r7) goto L_0x03b0
            android.widget.FrameLayout r0 = r1.updateLayout
            java.lang.String r7 = "listSelectorSDK21"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r7, (java.lang.String) r6)
            r0.setBackground(r7)
        L_0x03b0:
            android.widget.FrameLayout r0 = r1.updateLayout
            r7 = 44
            r8 = 83
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r7, r8)
            r9.addView(r0, r7)
            android.widget.FrameLayout r0 = r1.updateLayout
            org.telegram.ui.-$$Lambda$LaunchActivity$PjydhyJXuSTf6ct-uvdFxIOTudU r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$PjydhyJXuSTf6ct-uvdFxIOTudU
            r7.<init>()
            r0.setOnClickListener(r7)
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            android.widget.FrameLayout r7 = r1.updateLayout
            r0.<init>(r7)
            r1.updateLayoutIcon = r0
            r0.setColors((int) r11, (int) r11, (int) r11, (int) r11)
            org.telegram.ui.Components.RadialProgress2 r0 = r1.updateLayoutIcon
            r7 = 1102053376(0x41b00000, float:22.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r15 = 1107558400(0x42040000, float:33.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r0.setProgressRect(r7, r9, r10, r15)
            org.telegram.ui.Components.RadialProgress2 r0 = r1.updateLayoutIcon
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.setCircleRadius(r7)
            org.telegram.ui.Components.RadialProgress2 r0 = r1.updateLayoutIcon
            r0.setAsMini()
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r1)
            r1.updateTextView = r0
            r7 = 15
            r0.setTextSize(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.updateTextView
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r0.setTypeface(r8)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.updateTextView
            r8 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r9 = "AppUpdate"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setText(r8)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.updateTextView
            r0.setTextColor(r11)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.updateTextView
            r0.setGravity(r14)
            android.widget.FrameLayout r0 = r1.updateLayout
            org.telegram.ui.ActionBar.SimpleTextView r8 = r1.updateTextView
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 16
            r19 = 1116995584(0x42940000, float:74.0)
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r8, r9)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r1)
            r1.updateSizeTextView = r0
            r8 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r4, r8)
            android.widget.TextView r0 = r1.updateSizeTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r0.setTypeface(r7)
            android.widget.TextView r0 = r1.updateSizeTextView
            r7 = 5
            r0.setGravity(r7)
            android.widget.TextView r0 = r1.updateSizeTextView
            r0.setTextColor(r11)
            android.widget.FrameLayout r0 = r1.updateLayout
            android.widget.TextView r8 = r1.updateSizeTextView
            r18 = 21
            r19 = 0
            r21 = 1099431936(0x41880000, float:17.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r8, r9)
            org.telegram.ui.Components.PasscodeView r0 = new org.telegram.ui.Components.PasscodeView
            r0.<init>(r1)
            r1.passcodeView = r0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r13)
            r8.addView(r0, r9)
            r23.checkCurrentAccount()
            int r0 = r1.currentAccount
            r1.updateCurrentConnectionState(r0)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r9 = new java.lang.Object[r4]
            r9[r5] = r1
            r0.postNotificationName(r8, r9)
            int r0 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getConnectionState()
            r1.currentConnectionState = r0
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needShowAlert
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.reloadInterface
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r0.addObserver(r1, r9)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.showBulletin
            r0.addObserver(r1, r8)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r8 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r0.addObserver(r1, r8)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0634
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x053f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            org.telegram.ui.LoginActivity r6 = new org.telegram.ui.LoginActivity
            r6.<init>()
            r0.addFragmentToStack(r6)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r5)
            goto L_0x0553
        L_0x053f:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r1.sideMenu
            r0.setSideMenu(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout
            r6.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r1.drawerLayoutContainer
            r0.setAllowOpenDrawer(r4, r5)
        L_0x0553:
            if (r2 == 0) goto L_0x069b
            java.lang.String r0 = "fragment"
            java.lang.String r0 = r2.getString(r0)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x069b
            java.lang.String r6 = "args"
            android.os.Bundle r6 = r2.getBundle(r6)     // Catch:{ Exception -> 0x062f }
            int r8 = r0.hashCode()     // Catch:{ Exception -> 0x062f }
            r9 = 2
            switch(r8) {
                case -1529105743: goto L_0x059e;
                case -1349522494: goto L_0x0594;
                case 3052376: goto L_0x058a;
                case 98629247: goto L_0x0580;
                case 738950403: goto L_0x0576;
                case 1434631203: goto L_0x056c;
                default: goto L_0x056b;
            }     // Catch:{ Exception -> 0x062f }
        L_0x056b:
            goto L_0x05a7
        L_0x056c:
            java.lang.String r8 = "settings"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 1
            goto L_0x05a7
        L_0x0576:
            java.lang.String r8 = "channel"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 3
            goto L_0x05a7
        L_0x0580:
            java.lang.String r8 = "group"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 2
            goto L_0x05a7
        L_0x058a:
            java.lang.String r8 = "chat"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 0
            goto L_0x05a7
        L_0x0594:
            java.lang.String r8 = "chat_profile"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 4
            goto L_0x05a7
        L_0x059e:
            java.lang.String r8 = "wallpapers"
            boolean r0 = r0.equals(r8)     // Catch:{ Exception -> 0x062f }
            if (r0 == 0) goto L_0x05a7
            r11 = 5
        L_0x05a7:
            if (r11 == 0) goto L_0x061c
            if (r11 == r4) goto L_0x0600
            if (r11 == r9) goto L_0x05ec
            if (r11 == r14) goto L_0x05d8
            if (r11 == r12) goto L_0x05c4
            if (r11 == r7) goto L_0x05b5
            goto L_0x069b
        L_0x05b5:
            org.telegram.ui.WallpapersListActivity r0 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r5)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x05c4:
            if (r6 == 0) goto L_0x069b
            org.telegram.ui.ProfileActivity r0 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            boolean r6 = r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            if (r6 == 0) goto L_0x069b
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x05d8:
            if (r6 == 0) goto L_0x069b
            org.telegram.ui.ChannelCreateActivity r0 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            boolean r6 = r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            if (r6 == 0) goto L_0x069b
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x05ec:
            if (r6 == 0) goto L_0x069b
            org.telegram.ui.GroupCreateFinalActivity r0 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            boolean r6 = r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            if (r6 == 0) goto L_0x069b
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x0600:
            java.lang.String r0 = "user_id"
            int r7 = r1.currentAccount     // Catch:{ Exception -> 0x062f }
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ Exception -> 0x062f }
            int r7 = r7.clientUserId     // Catch:{ Exception -> 0x062f }
            r6.putInt(r0, r7)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ProfileActivity r0 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x061c:
            if (r6 == 0) goto L_0x069b
            org.telegram.ui.ChatActivity r0 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x062f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x062f }
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout     // Catch:{ Exception -> 0x062f }
            boolean r6 = r6.addFragmentToStack(r0)     // Catch:{ Exception -> 0x062f }
            if (r6 == 0) goto L_0x069b
            r0.restoreSelfArgs(r2)     // Catch:{ Exception -> 0x062f }
            goto L_0x069b
        L_0x062f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x069b
        L_0x0634:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r6 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r6 == 0) goto L_0x0649
            org.telegram.ui.DialogsActivity r0 = (org.telegram.ui.DialogsActivity) r0
            org.telegram.ui.Components.RecyclerListView r6 = r1.sideMenu
            r0.setSideMenu(r6)
        L_0x0649:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x067e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 > r4) goto L_0x0665
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0665
            r0 = 1
            goto L_0x0666
        L_0x0665:
            r0 = 0
        L_0x0666:
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            int r6 = r6.size()
            if (r6 != r4) goto L_0x067f
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            java.lang.Object r6 = r6.get(r5)
            boolean r6 = r6 instanceof org.telegram.ui.LoginActivity
            if (r6 == 0) goto L_0x067f
            r0 = 0
            goto L_0x067f
        L_0x067e:
            r0 = 1
        L_0x067f:
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            int r6 = r6.size()
            if (r6 != r4) goto L_0x0696
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r1.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            java.lang.Object r6 = r6.get(r5)
            boolean r6 = r6 instanceof org.telegram.ui.LoginActivity
            if (r6 == 0) goto L_0x0696
            r0 = 0
        L_0x0696:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r1.drawerLayoutContainer
            r6.setAllowOpenDrawer(r0, r5)
        L_0x069b:
            r23.checkLayout()
            r23.checkSystemBarColors()
            android.content.Intent r0 = r23.getIntent()
            if (r2 == 0) goto L_0x06a9
            r2 = 1
            goto L_0x06aa
        L_0x06a9:
            r2 = 0
        L_0x06aa:
            r1.handleIntent(r0, r5, r2, r5)
            java.lang.String r0 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x0710 }
            java.lang.String r2 = android.os.Build.USER     // Catch:{ Exception -> 0x0710 }
            java.lang.String r6 = ""
            if (r0 == 0) goto L_0x06ba
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0710 }
            goto L_0x06bb
        L_0x06ba:
            r0 = r6
        L_0x06bb:
            if (r2 == 0) goto L_0x06c1
            java.lang.String r6 = r0.toLowerCase()     // Catch:{ Exception -> 0x0710 }
        L_0x06c1:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0710 }
            if (r2 == 0) goto L_0x06e1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0710 }
            r2.<init>()     // Catch:{ Exception -> 0x0710 }
            java.lang.String r7 = "OS name "
            r2.append(r7)     // Catch:{ Exception -> 0x0710 }
            r2.append(r0)     // Catch:{ Exception -> 0x0710 }
            java.lang.String r7 = " "
            r2.append(r7)     // Catch:{ Exception -> 0x0710 }
            r2.append(r6)     // Catch:{ Exception -> 0x0710 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0710 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0710 }
        L_0x06e1:
            boolean r0 = r0.contains(r3)     // Catch:{ Exception -> 0x0710 }
            if (r0 != 0) goto L_0x06ed
            boolean r0 = r6.contains(r3)     // Catch:{ Exception -> 0x0710 }
            if (r0 == 0) goto L_0x0714
        L_0x06ed:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0710 }
            r2 = 24
            if (r0 > r2) goto L_0x0714
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r4     // Catch:{ Exception -> 0x0710 }
            android.view.Window r0 = r23.getWindow()     // Catch:{ Exception -> 0x0710 }
            android.view.View r0 = r0.getDecorView()     // Catch:{ Exception -> 0x0710 }
            android.view.View r0 = r0.getRootView()     // Catch:{ Exception -> 0x0710 }
            android.view.ViewTreeObserver r2 = r0.getViewTreeObserver()     // Catch:{ Exception -> 0x0710 }
            org.telegram.ui.-$$Lambda$LaunchActivity$ETa89RwmojhIDcQ-SrCxujdl48w r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$ETa89RwmojhIDcQ-SrCxujdl48w     // Catch:{ Exception -> 0x0710 }
            r3.<init>(r0)     // Catch:{ Exception -> 0x0710 }
            r1.onGlobalLayoutListener = r3     // Catch:{ Exception -> 0x0710 }
            r2.addOnGlobalLayoutListener(r3)     // Catch:{ Exception -> 0x0710 }
            goto L_0x0714
        L_0x0710:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0714:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            r0.setBaseActivity(r1, r4)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r23)
            r1.updateAppUpdateViews(r5)
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
                        lambda$runLinkRequest$42(new PeopleNearbyActivity());
                    } else {
                        lambda$runLinkRequest$42(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                lambda$runLinkRequest$42(new ActionIntroActivity(1));
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$4 */
    public /* synthetic */ void lambda$onCreate$4$LaunchActivity(View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            if (this.updateLayoutIcon.getIcon() == 2) {
                FileLoader.getInstance(this.currentAccount).loadFile(SharedConfig.pendingAppUpdate.document, "update", 1, 1);
                updateAppUpdateViews(true);
            } else if (this.updateLayoutIcon.getIcon() == 3) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(SharedConfig.pendingAppUpdate.document);
                updateAppUpdateViews(true);
            } else {
                AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, this);
            }
        }
    }

    static /* synthetic */ void lambda$onCreate$5(View view) {
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
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
            AnonymousClass8 r0 = new BlockingUpdateView(this) {
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
                            LaunchActivity.AnonymousClass9.this.lambda$onAcceptTerms$0$LaunchActivity$9();
                        }
                    }).start();
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAcceptTerms$0 */
                public /* synthetic */ void lambda$onAcceptTerms$0$LaunchActivity$9() {
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
                    LaunchActivity.this.lambda$showPasscodeActivity$6$LaunchActivity();
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
    /* renamed from: lambda$showPasscodeActivity$6 */
    public /* synthetic */ void lambda$showPasscodeActivity$6$LaunchActivity() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v281, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v302, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v310, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v28, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v329, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v29, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v335, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v30, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v341, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v31, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v32, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v33, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v34, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v355, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v35, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v36, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v362, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v37, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v38, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v39, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v379, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v255, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v40, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v389, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v41, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v42, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v44, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v399, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v270, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v405, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v407, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v274, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v411, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v413, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v420, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v421, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v195, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v424, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v301, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v431, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v434, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v436, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v439, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v441, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v443, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v449, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v452, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v316, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v28, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v30, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v31, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v199, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r3v6, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v2, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: type inference failed for: r2v15 */
    /* JADX WARNING: type inference failed for: r2v17 */
    /* JADX WARNING: type inference failed for: r2v20 */
    /* JADX WARNING: type inference failed for: r2v21 */
    /* JADX WARNING: type inference failed for: r47v0 */
    /* JADX WARNING: type inference failed for: r47v1 */
    /* JADX WARNING: type inference failed for: r47v2 */
    /* JADX WARNING: type inference failed for: r47v3 */
    /* JADX WARNING: type inference failed for: r47v4 */
    /* JADX WARNING: type inference failed for: r47v5 */
    /* JADX WARNING: type inference failed for: r47v6 */
    /* JADX WARNING: type inference failed for: r47v8 */
    /* JADX WARNING: type inference failed for: r52v4 */
    /* JADX WARNING: type inference failed for: r1v148, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
    /* JADX WARNING: type inference failed for: r47v13 */
    /* JADX WARNING: type inference failed for: r47v14 */
    /* JADX WARNING: type inference failed for: r47v15 */
    /* JADX WARNING: type inference failed for: r47v16 */
    /* JADX WARNING: type inference failed for: r47v17 */
    /* JADX WARNING: type inference failed for: r47v18 */
    /* JADX WARNING: type inference failed for: r3v99 */
    /* JADX WARNING: type inference failed for: r3v101 */
    /* JADX WARNING: type inference failed for: r3v103 */
    /* JADX WARNING: type inference failed for: r43v23 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x030e, code lost:
        if (r15.sendingText == null) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0458, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0901, code lost:
        if (r1.intValue() == 0) goto L_0x0903;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x010f, code lost:
        r0 = r22.getIntent().getExtras();
        r12 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0121, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0124, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0125, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0142, code lost:
        if (r2.equals(r0) != false) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x199c, code lost:
        if (r1.checkCanOpenChat(r0, r3.get(r3.size() - r2)) != false) goto L_0x199e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1a10, code lost:
        if (r1.checkCanOpenChat(r0, r3.get(r3.size() - r2)) != false) goto L_0x1a12;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:864:0x16a5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x1b5f  */
    /* JADX WARNING: Removed duplicated region for block: B:1038:0x1b6b  */
    /* JADX WARNING: Removed duplicated region for block: B:1041:0x1b79  */
    /* JADX WARNING: Removed duplicated region for block: B:1042:0x1b8a  */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x1dd2  */
    /* JADX WARNING: Removed duplicated region for block: B:1123:0x1e1e  */
    /* JADX WARNING: Removed duplicated region for block: B:1134:0x1e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:1136:0x1e76  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0397 A[Catch:{ Exception -> 0x04bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x073b A[Catch:{ Exception -> 0x0747 }] */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0f9f A[SYNTHETIC, Splitter:B:623:0x0f9f] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0154 A[SYNTHETIC, Splitter:B:69:0x0154] */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x12fb A[Catch:{ Exception -> 0x1307 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:869:0x16ae A[SYNTHETIC, Splitter:B:869:0x16ae] */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x1937  */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x1968  */
    /* JADX WARNING: Removed duplicated region for block: B:959:0x19df  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r65, boolean r66, boolean r67, boolean r68) {
        /*
            r64 = this;
            r15 = r64
            r14 = r65
            r0 = r67
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r64, r65)
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
            java.lang.String r1 = r65.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r1 = r65.getFlags()
            java.lang.String r2 = r65.getAction()
            int[] r11 = new int[r13]
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r4 = "currentAccount"
            int r3 = r14.getIntExtra(r4, r3)
            r11[r12] = r3
            r3 = r11[r12]
            r15.switchToAccount(r3, r13)
            if (r2 == 0) goto L_0x006f
            java.lang.String r3 = "voip"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x006f
            r24 = 1
            goto L_0x0071
        L_0x006f:
            r24 = 0
        L_0x0071:
            if (r68 != 0) goto L_0x0094
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r3 != 0) goto L_0x007d
            boolean r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r3 == 0) goto L_0x0094
        L_0x007d:
            r64.showPasscodeActivity()
            int r3 = r15.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            r3.saveConfig(r12)
            if (r24 != 0) goto L_0x0094
            r15.passcodeSaveIntent = r14
            r10 = r66
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x0094:
            r10 = r66
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
            if (r1 != 0) goto L_0x18ff
            java.lang.String r1 = r65.getAction()
            if (r1 == 0) goto L_0x18ff
            if (r0 != 0) goto L_0x18ff
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "\n"
            java.lang.String r7 = "hash"
            java.lang.String r5 = ""
            if (r0 == 0) goto L_0x0349
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0144
            android.os.Bundle r0 = r65.getExtras()
            if (r0 == 0) goto L_0x0144
            android.os.Bundle r0 = r65.getExtras()
            java.lang.String r2 = "dialogId"
            long r18 = r0.getLong(r2, r3)
            int r0 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0132
            android.os.Bundle r0 = r65.getExtras()     // Catch:{ all -> 0x012c }
            java.lang.String r2 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r2)     // Catch:{ all -> 0x012c }
            if (r0 == 0) goto L_0x0130
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x012c }
            java.util.List r2 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r2)     // Catch:{ all -> 0x012c }
            int r6 = r2.size()     // Catch:{ all -> 0x012c }
            r13 = 0
        L_0x00fd:
            if (r13 >= r6) goto L_0x0130
            java.lang.Object r22 = r2.get(r13)     // Catch:{ all -> 0x012c }
            androidx.core.content.pm.ShortcutInfoCompat r22 = (androidx.core.content.pm.ShortcutInfoCompat) r22     // Catch:{ all -> 0x012c }
            java.lang.String r12 = r22.getId()     // Catch:{ all -> 0x012c }
            boolean r12 = r0.equals(r12)     // Catch:{ all -> 0x012c }
            if (r12 == 0) goto L_0x0128
            android.content.Intent r0 = r22.getIntent()     // Catch:{ all -> 0x012c }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x012c }
            java.lang.String r2 = "dialogId"
            long r12 = r0.getLong(r2, r3)     // Catch:{ all -> 0x012c }
            java.lang.String r0 = r0.getString(r7, r9)     // Catch:{ all -> 0x0124 }
            r18 = r12
            goto L_0x013a
        L_0x0124:
            r0 = move-exception
            r18 = r12
            goto L_0x012d
        L_0x0128:
            int r13 = r13 + 1
            r12 = 0
            goto L_0x00fd
        L_0x012c:
            r0 = move-exception
        L_0x012d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0130:
            r0 = r9
            goto L_0x013a
        L_0x0132:
            android.os.Bundle r0 = r65.getExtras()
            java.lang.String r0 = r0.getString(r7, r9)
        L_0x013a:
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash
            if (r2 == 0) goto L_0x0144
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0146
        L_0x0144:
            r18 = r3
        L_0x0146:
            java.lang.String r2 = r65.getType()
            if (r2 == 0) goto L_0x018d
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x018d
            android.os.Bundle r0 = r65.getExtras()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r1 = "android.intent.extra.STREAM"
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x0186 }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x0186 }
            if (r0 == 0) goto L_0x018a
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0186 }
            r5 = 0
            java.util.ArrayList r1 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r1, r5, r9, r9)     // Catch:{ Exception -> 0x0186 }
            r15.contactsToSend = r1     // Catch:{ Exception -> 0x0186 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0186 }
            r5 = 5
            if (r1 <= r5) goto L_0x0182
            r15.contactsToSend = r9     // Catch:{ Exception -> 0x0186 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0186 }
            r1.<init>()     // Catch:{ Exception -> 0x0186 }
            r15.documentsUrisArray = r1     // Catch:{ Exception -> 0x0186 }
            r1.add(r0)     // Catch:{ Exception -> 0x0186 }
            r15.documentsMimeType = r2     // Catch:{ Exception -> 0x0186 }
            goto L_0x0312
        L_0x0182:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x0186 }
            goto L_0x0312
        L_0x0186:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x018a:
            r0 = 1
            goto L_0x0313
        L_0x018d:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x01a1
            java.lang.String r6 = "android.intent.extra.TEXT"
            java.lang.CharSequence r6 = r14.getCharSequenceExtra(r6)
            if (r6 == 0) goto L_0x01a1
            java.lang.String r0 = r6.toString()
        L_0x01a1:
            java.lang.String r6 = "android.intent.extra.SUBJECT"
            java.lang.String r6 = r14.getStringExtra(r6)
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 != 0) goto L_0x01d8
            java.lang.String r7 = "http://"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x01bd
            java.lang.String r7 = "https://"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x01d5
        L_0x01bd:
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 != 0) goto L_0x01d5
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r6)
            r7.append(r1)
            r7.append(r0)
            java.lang.String r0 = r7.toString()
        L_0x01d5:
            r15.sendingText = r0
            goto L_0x01e0
        L_0x01d8:
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            if (r0 != 0) goto L_0x01e0
            r15.sendingText = r6
        L_0x01e0:
            java.lang.String r0 = "android.intent.extra.STREAM"
            android.os.Parcelable r0 = r14.getParcelableExtra(r0)
            if (r0 == 0) goto L_0x030c
            boolean r1 = r0 instanceof android.net.Uri
            if (r1 != 0) goto L_0x01f4
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x01f4:
            r1 = r0
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x0201
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)
            if (r0 == 0) goto L_0x0201
            r6 = 1
            goto L_0x0202
        L_0x0201:
            r6 = 0
        L_0x0202:
            if (r6 != 0) goto L_0x030a
            if (r1 == 0) goto L_0x030a
            if (r2 == 0) goto L_0x0210
            java.lang.String r0 = "image/"
            boolean r0 = r2.startsWith(r0)
            if (r0 != 0) goto L_0x0220
        L_0x0210:
            java.lang.String r0 = r1.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r0 = r0.endsWith(r7)
            if (r0 == 0) goto L_0x0239
        L_0x0220:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x022b
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x022b:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x030a
        L_0x0239:
            java.lang.String r7 = r1.toString()
            int r0 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x02b2
            if (r7 == 0) goto L_0x02b2
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x025b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "export path = "
            r0.append(r12)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x025b:
            r12 = 0
            r0 = r11[r12]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r0 = r0.exportUri
            java.lang.String r12 = org.telegram.messenger.MediaController.getFileName(r1)
            java.lang.String r12 = org.telegram.messenger.FileLoader.fixFileName(r12)
            java.util.Iterator r13 = r0.iterator()
        L_0x0270:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x029c
            java.lang.Object r0 = r13.next()
            java.lang.String r0 = (java.lang.String) r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x0297 }
            java.util.regex.Matcher r22 = r0.matcher(r7)     // Catch:{ Exception -> 0x0297 }
            boolean r22 = r22.find()     // Catch:{ Exception -> 0x0297 }
            if (r22 != 0) goto L_0x0294
            java.util.regex.Matcher r0 = r0.matcher(r12)     // Catch:{ Exception -> 0x0297 }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x0297 }
            if (r0 == 0) goto L_0x0270
        L_0x0294:
            r15.exportingChatUri = r1     // Catch:{ Exception -> 0x0297 }
            goto L_0x029c
        L_0x0297:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0270
        L_0x029c:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x02b2
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x02b2
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r7.endsWith(r0)
            if (r0 == 0) goto L_0x02b2
            r15.exportingChatUri = r1
        L_0x02b2:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x030a
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            if (r0 == 0) goto L_0x02f8
            java.lang.String r7 = "file:"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x02ca
            java.lang.String r7 = "file://"
            java.lang.String r0 = r0.replace(r7, r5)
        L_0x02ca:
            if (r2 == 0) goto L_0x02d7
            java.lang.String r5 = "video/"
            boolean r2 = r2.startsWith(r5)
            if (r2 == 0) goto L_0x02d7
            r15.videoPath = r0
            goto L_0x030a
        L_0x02d7:
            java.util.ArrayList<java.lang.String> r2 = r15.documentsPathsArray
            if (r2 != 0) goto L_0x02e9
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsPathsArray = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r15.documentsOriginalPathsArray = r2
        L_0x02e9:
            java.util.ArrayList<java.lang.String> r2 = r15.documentsPathsArray
            r2.add(r0)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
            r0.add(r1)
            goto L_0x030a
        L_0x02f8:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            if (r0 != 0) goto L_0x0303
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsUrisArray = r0
        L_0x0303:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            r0.add(r1)
            r15.documentsMimeType = r2
        L_0x030a:
            r0 = r6
            goto L_0x0313
        L_0x030c:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0312
            goto L_0x018a
        L_0x0312:
            r0 = 0
        L_0x0313:
            if (r0 == 0) goto L_0x031f
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x031f:
            r56 = r3
            r3 = r8
            r67 = r9
            r1 = r67
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
            r25 = 0
            r32 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r11 = r2
            r9 = 0
            goto L_0x192b
        L_0x0349:
            java.lang.String r0 = r65.getAction()
            java.lang.String r6 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x04d9
            java.lang.String r0 = "android.intent.extra.STREAM"
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r0)     // Catch:{ Exception -> 0x04bd }
            java.lang.String r1 = r65.getType()     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x0394
            r2 = 0
        L_0x0362:
            int r6 = r0.size()     // Catch:{ Exception -> 0x04bd }
            if (r2 >= r6) goto L_0x038c
            java.lang.Object r6 = r0.get(r2)     // Catch:{ Exception -> 0x04bd }
            android.os.Parcelable r6 = (android.os.Parcelable) r6     // Catch:{ Exception -> 0x04bd }
            boolean r7 = r6 instanceof android.net.Uri     // Catch:{ Exception -> 0x04bd }
            if (r7 != 0) goto L_0x037a
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04bd }
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x04bd }
        L_0x037a:
            android.net.Uri r6 = (android.net.Uri) r6     // Catch:{ Exception -> 0x04bd }
            if (r6 == 0) goto L_0x0389
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r6)     // Catch:{ Exception -> 0x04bd }
            if (r6 == 0) goto L_0x0389
            r0.remove(r2)     // Catch:{ Exception -> 0x04bd }
            int r2 = r2 + -1
        L_0x0389:
            r6 = 1
            int r2 = r2 + r6
            goto L_0x0362
        L_0x038c:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x04bd }
            if (r2 == 0) goto L_0x0394
            r2 = r9
            goto L_0x0395
        L_0x0394:
            r2 = r0
        L_0x0395:
            if (r2 == 0) goto L_0x04c1
            if (r1 == 0) goto L_0x03d6
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x03d6
            r0 = 0
        L_0x03a2:
            int r1 = r2.size()     // Catch:{ Exception -> 0x04bd }
            if (r0 >= r1) goto L_0x04bb
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x04bd }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x04bd }
            boolean r5 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x04bd }
            if (r5 != 0) goto L_0x03ba
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04bd }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x04bd }
        L_0x03ba:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x04bd }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r5 = r15.photoPathsArray     // Catch:{ Exception -> 0x04bd }
            if (r5 != 0) goto L_0x03c7
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x04bd }
            r5.<init>()     // Catch:{ Exception -> 0x04bd }
            r15.photoPathsArray = r5     // Catch:{ Exception -> 0x04bd }
        L_0x03c7:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r5 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x04bd }
            r5.<init>()     // Catch:{ Exception -> 0x04bd }
            r5.uri = r1     // Catch:{ Exception -> 0x04bd }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x04bd }
            r1.add(r5)     // Catch:{ Exception -> 0x04bd }
            int r0 = r0 + 1
            goto L_0x03a2
        L_0x03d6:
            r6 = 0
            r0 = r11[r6]     // Catch:{ Exception -> 0x04bd }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x04bd }
            java.util.Set<java.lang.String> r6 = r0.exportUri     // Catch:{ Exception -> 0x04bd }
            r7 = 0
        L_0x03e0:
            int r0 = r2.size()     // Catch:{ Exception -> 0x04bd }
            if (r7 >= r0) goto L_0x04bb
            java.lang.Object r0 = r2.get(r7)     // Catch:{ Exception -> 0x04bd }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x04bd }
            boolean r12 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x04bd }
            if (r12 != 0) goto L_0x03f8
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04bd }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x04bd }
        L_0x03f8:
            r12 = r0
            android.net.Uri r12 = (android.net.Uri) r12     // Catch:{ Exception -> 0x04bd }
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.getPath(r12)     // Catch:{ Exception -> 0x04bd }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04bd }
            if (r0 != 0) goto L_0x0407
            r3 = r13
            goto L_0x0408
        L_0x0407:
            r3 = r0
        L_0x0408:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x0420
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04bd }
            r0.<init>()     // Catch:{ Exception -> 0x04bd }
            java.lang.String r4 = "export path = "
            r0.append(r4)     // Catch:{ Exception -> 0x04bd }
            r0.append(r3)     // Catch:{ Exception -> 0x04bd }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04bd }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x04bd }
        L_0x0420:
            if (r3 == 0) goto L_0x0476
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x04bd }
            if (r0 != 0) goto L_0x0476
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r12)     // Catch:{ Exception -> 0x04bd }
            java.lang.String r4 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x04bd }
            java.util.Iterator r22 = r6.iterator()     // Catch:{ Exception -> 0x04bd }
        L_0x0432:
            boolean r0 = r22.hasNext()     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x045f
            java.lang.Object r0 = r22.next()     // Catch:{ Exception -> 0x04bd }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x04bd }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x045a }
            java.util.regex.Matcher r23 = r0.matcher(r3)     // Catch:{ Exception -> 0x045a }
            boolean r23 = r23.find()     // Catch:{ Exception -> 0x045a }
            if (r23 != 0) goto L_0x0456
            java.util.regex.Matcher r0 = r0.matcher(r4)     // Catch:{ Exception -> 0x045a }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x045a }
            if (r0 == 0) goto L_0x0432
        L_0x0456:
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x045a }
            r0 = 1
            goto L_0x0460
        L_0x045a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04bd }
            goto L_0x0432
        L_0x045f:
            r0 = 0
        L_0x0460:
            if (r0 == 0) goto L_0x0463
            goto L_0x04b5
        L_0x0463:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r3.startsWith(r0)     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x0476
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r3.endsWith(r0)     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x0476
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x04bd }
            goto L_0x04b5
        L_0x0476:
            if (r13 == 0) goto L_0x04a3
            java.lang.String r0 = "file:"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x04bd }
            if (r0 == 0) goto L_0x0486
            java.lang.String r0 = "file://"
            java.lang.String r13 = r13.replace(r0, r5)     // Catch:{ Exception -> 0x04bd }
        L_0x0486:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04bd }
            if (r0 != 0) goto L_0x0498
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04bd }
            r0.<init>()     // Catch:{ Exception -> 0x04bd }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x04bd }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04bd }
            r0.<init>()     // Catch:{ Exception -> 0x04bd }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x04bd }
        L_0x0498:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04bd }
            r0.add(r13)     // Catch:{ Exception -> 0x04bd }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x04bd }
            r0.add(r3)     // Catch:{ Exception -> 0x04bd }
            goto L_0x04b5
        L_0x04a3:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04bd }
            if (r0 != 0) goto L_0x04ae
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04bd }
            r0.<init>()     // Catch:{ Exception -> 0x04bd }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x04bd }
        L_0x04ae:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04bd }
            r0.add(r12)     // Catch:{ Exception -> 0x04bd }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x04bd }
        L_0x04b5:
            int r7 = r7 + 1
            r3 = 0
            goto L_0x03e0
        L_0x04bb:
            r0 = 0
            goto L_0x04c2
        L_0x04bd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04c1:
            r0 = 1
        L_0x04c2:
            if (r0 == 0) goto L_0x04ce
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x04ce:
            r3 = r8
            r10 = r11
            r8 = r14
            r7 = r15
            r9 = 0
            r25 = -1
            r56 = 0
            goto L_0x1908
        L_0x04d9:
            java.lang.String r0 = r65.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x179d
            android.net.Uri r0 = r65.getData()
            if (r0 == 0) goto L_0x1753
            java.lang.String r2 = r0.getScheme()
            java.lang.String r3 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r4 = "phone"
            if (r2 == 0) goto L_0x153e
            int r6 = r2.hashCode()
            switch(r6) {
                case 3699: goto L_0x0514;
                case 3213448: goto L_0x0509;
                case 99617003: goto L_0x04fe;
                default: goto L_0x04fc;
            }
        L_0x04fc:
            r6 = -1
            goto L_0x051e
        L_0x04fe:
            java.lang.String r6 = "https"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0507
            goto L_0x04fc
        L_0x0507:
            r6 = 2
            goto L_0x051e
        L_0x0509:
            java.lang.String r6 = "http"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0512
            goto L_0x04fc
        L_0x0512:
            r6 = 1
            goto L_0x051e
        L_0x0514:
            java.lang.String r6 = "tg"
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x051d
            goto L_0x04fc
        L_0x051d:
            r6 = 0
        L_0x051e:
            java.lang.String r12 = "thread"
            r22 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r6) {
                case 0: goto L_0x0a4f;
                case 1: goto L_0x0527;
                case 2: goto L_0x0527;
                default: goto L_0x0525;
            }
        L_0x0525:
            goto L_0x153e
        L_0x0527:
            java.lang.String r6 = r0.getHost()
            java.lang.String r6 = r6.toLowerCase()
            java.lang.String r9 = "telegram.me"
            boolean r9 = r6.equals(r9)
            if (r9 != 0) goto L_0x0547
            java.lang.String r9 = "t.me"
            boolean r9 = r6.equals(r9)
            if (r9 != 0) goto L_0x0547
            java.lang.String r9 = "telegram.dog"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x153e
        L_0x0547:
            java.lang.String r6 = r0.getPath()
            if (r6 == 0) goto L_0x09ea
            int r9 = r6.length()
            r13 = 1
            if (r9 <= r13) goto L_0x09ea
            java.lang.String r6 = r6.substring(r13)
            java.lang.String r9 = "bg/"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0760
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r9 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r9.<init>()
            r1.settings = r9
            java.lang.String r9 = "bg/"
            java.lang.String r5 = r6.replace(r9, r5)
            r1.slug = r5
            if (r5 == 0) goto L_0x0591
            int r5 = r5.length()
            r6 = 6
            if (r5 != r6) goto L_0x0591
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x058b }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x058b }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x058b }
            r5 = r5 | r22
            r0.background_color = r5     // Catch:{ Exception -> 0x058b }
        L_0x058b:
            r5 = 0
            r1.slug = r5
        L_0x058e:
            r9 = -1
            goto L_0x0747
        L_0x0591:
            java.lang.String r5 = r1.slug
            if (r5 == 0) goto L_0x0649
            int r5 = r5.length()
            r6 = 13
            if (r5 < r6) goto L_0x0649
            java.lang.String r5 = r1.slug
            r6 = 6
            char r5 = r5.charAt(r6)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)
            if (r5 == 0) goto L_0x0649
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x062c }
            java.lang.String r9 = r1.slug     // Catch:{ Exception -> 0x062c }
            r12 = 0
            java.lang.String r9 = r9.substring(r12, r6)     // Catch:{ Exception -> 0x062c }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x062c }
            r6 = r9 | r22
            r5.background_color = r6     // Catch:{ Exception -> 0x062c }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x062c }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x062c }
            r9 = 7
            r12 = 13
            java.lang.String r6 = r6.substring(r9, r12)     // Catch:{ Exception -> 0x062c }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x062c }
            r6 = r6 | r22
            r5.second_background_color = r6     // Catch:{ Exception -> 0x062c }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x062c }
            int r5 = r5.length()     // Catch:{ Exception -> 0x062c }
            r6 = 20
            if (r5 < r6) goto L_0x0600
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x062c }
            r6 = 13
            char r5 = r5.charAt(r6)     // Catch:{ Exception -> 0x062c }
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x062c }
            if (r5 == 0) goto L_0x0600
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x062c }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x062c }
            r9 = 14
            r12 = 20
            java.lang.String r6 = r6.substring(r9, r12)     // Catch:{ Exception -> 0x062c }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x062c }
            r6 = r6 | r22
            r5.third_background_color = r6     // Catch:{ Exception -> 0x062c }
        L_0x0600:
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x062c }
            int r5 = r5.length()     // Catch:{ Exception -> 0x062c }
            r6 = 27
            if (r5 != r6) goto L_0x062c
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x062c }
            r6 = 20
            char r5 = r5.charAt(r6)     // Catch:{ Exception -> 0x062c }
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x062c }
            if (r5 == 0) goto L_0x062c
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x062c }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x062c }
            r9 = 21
            java.lang.String r6 = r6.substring(r9)     // Catch:{ Exception -> 0x062c }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x062c }
            r6 = r6 | r22
            r5.fourth_background_color = r6     // Catch:{ Exception -> 0x062c }
        L_0x062c:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x0644 }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0644 }
            if (r5 != 0) goto L_0x0644
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0644 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0644 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0644 }
            r5.rotation = r0     // Catch:{ Exception -> 0x0644 }
        L_0x0644:
            r5 = 0
            r1.slug = r5
            goto L_0x058e
        L_0x0649:
            java.lang.String r5 = "mode"
            java.lang.String r5 = r0.getQueryParameter(r5)
            if (r5 == 0) goto L_0x0686
            java.lang.String r5 = r5.toLowerCase()
            java.lang.String r6 = " "
            java.lang.String[] r5 = r5.split(r6)
            if (r5 == 0) goto L_0x0686
            int r6 = r5.length
            if (r6 <= 0) goto L_0x0686
            r6 = 0
        L_0x0661:
            int r9 = r5.length
            if (r6 >= r9) goto L_0x0686
            r9 = r5[r6]
            java.lang.String r12 = "blur"
            boolean r9 = r12.equals(r9)
            if (r9 == 0) goto L_0x0674
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r12 = 1
            r9.blur = r12
            goto L_0x0683
        L_0x0674:
            r12 = 1
            r9 = r5[r6]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x0683
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r9.motion = r12
        L_0x0683:
            int r6 = r6 + 1
            goto L_0x0661
        L_0x0686:
            java.lang.String r5 = "intensity"
            java.lang.String r5 = r0.getQueryParameter(r5)
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x069f
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r5 = r5.intValue()
            r6.intensity = r5
            goto L_0x06a5
        L_0x069f:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            r6 = 50
            r5.intensity = r6
        L_0x06a5:
            java.lang.String r5 = "bg_color"
            java.lang.String r5 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x0728 }
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x0728 }
            if (r6 != 0) goto L_0x072a
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0728 }
            r9 = 6
            r12 = 0
            java.lang.String r13 = r5.substring(r12, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r13, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = r12 | r22
            r6.background_color = r9     // Catch:{ Exception -> 0x0728 }
            int r6 = r5.length()     // Catch:{ Exception -> 0x0728 }
            r9 = 13
            if (r6 < r9) goto L_0x0728
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0728 }
            r12 = 7
            java.lang.String r12 = r5.substring(r12, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = r12 | r22
            r6.second_background_color = r9     // Catch:{ Exception -> 0x0728 }
            int r6 = r5.length()     // Catch:{ Exception -> 0x0728 }
            r9 = 20
            if (r6 < r9) goto L_0x0702
            r6 = 13
            char r6 = r5.charAt(r6)     // Catch:{ Exception -> 0x0728 }
            boolean r6 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r6)     // Catch:{ Exception -> 0x0728 }
            if (r6 == 0) goto L_0x0702
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0728 }
            r12 = 14
            java.lang.String r12 = r5.substring(r12, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x0728 }
            r9 = r12 | r22
            r6.third_background_color = r9     // Catch:{ Exception -> 0x0728 }
        L_0x0702:
            int r6 = r5.length()     // Catch:{ Exception -> 0x0728 }
            r9 = 27
            if (r6 != r9) goto L_0x0728
            r6 = 20
            char r6 = r5.charAt(r6)     // Catch:{ Exception -> 0x0728 }
            boolean r6 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r6)     // Catch:{ Exception -> 0x0728 }
            if (r6 == 0) goto L_0x0728
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x0728 }
            r9 = 21
            java.lang.String r5 = r5.substring(r9)     // Catch:{ Exception -> 0x0728 }
            r9 = 16
            int r5 = java.lang.Integer.parseInt(r5, r9)     // Catch:{ Exception -> 0x0728 }
            r5 = r5 | r22
            r6.fourth_background_color = r5     // Catch:{ Exception -> 0x0728 }
        L_0x0728:
            r9 = -1
            goto L_0x072f
        L_0x072a:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0728 }
            r9 = -1
            r5.background_color = r9     // Catch:{ Exception -> 0x072f }
        L_0x072f:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x0747 }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0747 }
            if (r5 != 0) goto L_0x0747
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x0747 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0747 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0747 }
            r5.rotation = r0     // Catch:{ Exception -> 0x0747 }
        L_0x0747:
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
            goto L_0x0a01
        L_0x0760:
            r9 = -1
            java.lang.String r13 = "login/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x07a1
            java.lang.String r0 = "login/"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0789
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x078a
        L_0x0789:
            r0 = 0
        L_0x078a:
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
            goto L_0x09ff
        L_0x07a1:
            java.lang.String r13 = "joinchat/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x07b1
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r6.replace(r0, r5)
            goto L_0x09eb
        L_0x07b1:
            java.lang.String r13 = "+"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x07c1
            java.lang.String r0 = "+"
            java.lang.String r0 = r6.replace(r0, r5)
            goto L_0x09eb
        L_0x07c1:
            java.lang.String r13 = "addstickers/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x07d3
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r6.replace(r0, r5)
            r1 = r0
            r0 = 0
            goto L_0x09ec
        L_0x07d3:
            java.lang.String r13 = "msg/"
            boolean r13 = r6.startsWith(r13)
            if (r13 != 0) goto L_0x097f
            java.lang.String r13 = "share/"
            boolean r13 = r6.startsWith(r13)
            if (r13 == 0) goto L_0x07e5
            goto L_0x097f
        L_0x07e5:
            java.lang.String r1 = "confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0806
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
            goto L_0x09f9
        L_0x0806:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0827
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
            goto L_0x09fb
        L_0x0827:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x084a
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
            goto L_0x09fd
        L_0x084a:
            java.lang.String r1 = "c/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x08b4
            java.util.List r1 = r0.getPathSegments()
            int r5 = r1.size()
            r6 = 3
            if (r5 != r6) goto L_0x0891
            r5 = 1
            java.lang.Object r6 = r1.get(r5)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r6)
            r13 = 2
            java.lang.Object r1 = r1.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 == 0) goto L_0x087f
            int r6 = r5.intValue()
            if (r6 != 0) goto L_0x0881
        L_0x087f:
            r1 = 0
            r5 = 0
        L_0x0881:
            java.lang.String r0 = r0.getQueryParameter(r12)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r6 = r0.intValue()
            if (r6 != 0) goto L_0x0895
            r0 = 0
            goto L_0x0895
        L_0x0891:
            r13 = 2
            r0 = 0
            r1 = 0
            r5 = 0
        L_0x0895:
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
            goto L_0x0a07
        L_0x08b4:
            r13 = 2
            int r1 = r6.length()
            r5 = 1
            if (r1 < r5) goto L_0x09ea
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r5 = r0.getPathSegments()
            r1.<init>(r5)
            int r5 = r1.size()
            if (r5 <= 0) goto L_0x08de
            r5 = 0
            java.lang.Object r6 = r1.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r9 = "s"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x08df
            r1.remove(r5)
            goto L_0x08df
        L_0x08de:
            r5 = 0
        L_0x08df:
            int r6 = r1.size()
            if (r6 <= 0) goto L_0x0905
            java.lang.Object r6 = r1.get(r5)
            r5 = r6
            java.lang.String r5 = (java.lang.String) r5
            int r6 = r1.size()
            r9 = 1
            if (r6 <= r9) goto L_0x0903
            java.lang.Object r1 = r1.get(r9)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r1.intValue()
            if (r6 != 0) goto L_0x0907
        L_0x0903:
            r1 = 0
            goto L_0x0907
        L_0x0905:
            r1 = 0
            r5 = 0
        L_0x0907:
            java.lang.String r6 = "start"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r9 = "startgroup"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r13 = "game"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r67 = r1
            java.lang.String r1 = "voicechat"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r22 = r12.intValue()
            if (r22 != 0) goto L_0x0933
            r22 = r1
            r12 = 0
            goto L_0x0935
        L_0x0933:
            r22 = r1
        L_0x0935:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            r33 = r67
            if (r1 != 0) goto L_0x0961
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
            goto L_0x097a
        L_0x0961:
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
        L_0x097a:
            r12 = r9
            r9 = r6
            r6 = 0
            goto L_0x0a09
        L_0x097f:
            java.lang.String r6 = "url"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 != 0) goto L_0x0988
            goto L_0x0989
        L_0x0988:
            r5 = r6
        L_0x0989:
            java.lang.String r6 = "text"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 == 0) goto L_0x09bf
            int r6 = r5.length()
            if (r6 <= 0) goto L_0x09a8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r1)
            java.lang.String r5 = r6.toString()
            r6 = 1
            goto L_0x09a9
        L_0x09a8:
            r6 = 0
        L_0x09a9:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r5 = "text"
            java.lang.String r0 = r0.getQueryParameter(r5)
            r9.append(r0)
            java.lang.String r5 = r9.toString()
            goto L_0x09c0
        L_0x09bf:
            r6 = 0
        L_0x09c0:
            int r0 = r5.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x09d0
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r5.substring(r9, r0)
            goto L_0x09d2
        L_0x09d0:
            r9 = 0
            r0 = r5
        L_0x09d2:
            boolean r5 = r0.endsWith(r1)
            if (r5 == 0) goto L_0x09e3
            int r5 = r0.length()
            r12 = 1
            int r5 = r5 - r12
            java.lang.String r0 = r0.substring(r9, r5)
            goto L_0x09d2
        L_0x09e3:
            r13 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            r9 = 0
            r12 = 0
            goto L_0x09f1
        L_0x09ea:
            r0 = 0
        L_0x09eb:
            r1 = 0
        L_0x09ec:
            r5 = 0
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x09f1:
            r22 = 0
            r23 = 0
            r27 = 0
            r28 = 0
        L_0x09f9:
            r29 = 0
        L_0x09fb:
            r30 = 0
        L_0x09fd:
            r31 = 0
        L_0x09ff:
            r32 = 0
        L_0x0a01:
            r33 = 0
            r34 = 0
            r35 = 0
        L_0x0a07:
            r36 = 0
        L_0x0a09:
            r10 = r9
            r46 = r23
            r54 = r27
            r48 = r29
            r53 = r30
            r50 = r31
            r52 = r32
            r43 = r34
            r44 = r35
            r45 = r36
            r20 = 6
            r27 = 0
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
            r47 = 0
            r49 = 0
            r51 = 0
            r9 = r5
            r5 = r28
            r28 = r11
            r11 = r6
            r6 = r1
            r1 = r22
            r22 = r33
            r33 = 0
            goto L_0x1583
        L_0x0a4f:
            java.lang.String r6 = r0.toString()
            java.lang.String r9 = "tg:resolve"
            boolean r9 = r6.startsWith(r9)
            java.lang.String r13 = "payload"
            java.lang.String r10 = "scope"
            r28 = r11
            java.lang.String r11 = "tg://telegram.org"
            if (r9 != 0) goto L_0x13cb
            java.lang.String r9 = "tg://resolve"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a6d
            goto L_0x13cb
        L_0x0a6d:
            java.lang.String r9 = "tg:privatepost"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x1330
            java.lang.String r9 = "tg://privatepost"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a7f
            goto L_0x1330
        L_0x0a7f:
            java.lang.String r9 = "tg:bg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x1113
            java.lang.String r9 = "tg://bg"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0a91
            goto L_0x1113
        L_0x0a91:
            java.lang.String r9 = "tg:join"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x10f1
            java.lang.String r9 = "tg://join"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0aa3
            goto L_0x10f1
        L_0x0aa3:
            java.lang.String r9 = "tg:addstickers"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x10d6
            java.lang.String r9 = "tg://addstickers"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0ab5
            goto L_0x10d6
        L_0x0ab5:
            java.lang.String r9 = "tg:msg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x104c
            java.lang.String r9 = "tg://msg"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x104c
            java.lang.String r9 = "tg://share"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x104c
            java.lang.String r9 = "tg:share"
            boolean r9 = r6.startsWith(r9)
            if (r9 == 0) goto L_0x0ad7
            goto L_0x104c
        L_0x0ad7:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x1030
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0ae9
            goto L_0x1030
        L_0x0ae9:
            java.lang.String r1 = "tg:login"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0fba
            java.lang.String r1 = "tg://login"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0afb
            goto L_0x0fba
        L_0x0afb:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0f6b
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b0d
            goto L_0x0f6b
        L_0x0b0d:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0ec8
            java.lang.String r1 = "tg://passport"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0ec8
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b27
            goto L_0x0ec8
        L_0x0b27:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0e79
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b39
            goto L_0x0e79
        L_0x0b39:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0e20
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b4b
            goto L_0x0e20
        L_0x0b4b:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0d8b
            java.lang.String r1 = "tg://settings"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b5d
            goto L_0x0d8b
        L_0x0b5d:
            java.lang.String r1 = "tg:search"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0d48
            java.lang.String r1 = "tg://search"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b6f
            goto L_0x0d48
        L_0x0b6f:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0d2f
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b81
            goto L_0x0d2f
        L_0x0b81:
            java.lang.String r1 = "tg:call"
            boolean r1 = r6.startsWith(r1)
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "tg://call"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x0b93
            goto L_0x0CLASSNAME
        L_0x0b93:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x0ba5
            goto L_0x0CLASSNAME
        L_0x0ba5:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 != 0) goto L_0x0c0a
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x0bb6
            goto L_0x0c0a
        L_0x0bb6:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r6.replace(r0, r5)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r5)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0bcf
            r5 = 0
            java.lang.String r0 = r0.substring(r5, r1)
        L_0x0bcf:
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
            r27 = 0
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
            goto L_0x1579
        L_0x0c0a:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r42 = r0
            r41 = r1
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            goto L_0x156b
        L_0x0CLASSNAME:
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 1
            goto L_0x1563
        L_0x0CLASSNAME:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x1540
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0cb6
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x0CLASSNAME
            goto L_0x0cb6
        L_0x0CLASSNAME:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r3)
            java.lang.String r1 = "extra_force_call"
            r5 = 1
            r0.putExtra(r1, r5)
            org.telegram.ui.-$$Lambda$LaunchActivity$4pM9rKMFmZsCmpbKNHgfGr41uKk r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$4pM9rKMFmZsCmpbKNHgfGr41uKk
            r1.<init>(r0)
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r5)
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            goto L_0x0d02
        L_0x0cb6:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r6 = "name"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r9 = 0
            java.util.List r10 = r15.findContacts(r6, r0, r9)
            boolean r11 = r10.isEmpty()
            if (r11 == 0) goto L_0x0cdb
            if (r0 == 0) goto L_0x0cdb
            r12 = r0
            r11 = r6
            r0 = 1
            r1 = 0
            r5 = 0
            r6 = 0
            r10 = 0
            goto L_0x0d02
        L_0x0cdb:
            int r0 = r10.size()
            r11 = 1
            if (r0 != r11) goto L_0x0ceb
            java.lang.Object r0 = r10.get(r9)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            int r0 = r0.user_id
            goto L_0x0cec
        L_0x0ceb:
            r0 = 0
        L_0x0cec:
            if (r0 != 0) goto L_0x0cf2
            if (r6 == 0) goto L_0x0cf3
            r5 = r6
            goto L_0x0cf3
        L_0x0cf2:
            r5 = 0
        L_0x0cf3:
            java.lang.String r6 = "video"
            boolean r1 = r6.equalsIgnoreCase(r1)
            r6 = r1 ^ 1
            r10 = r5
            r9 = 1
            r11 = 0
            r12 = 0
            r5 = r1
            r1 = r0
            r0 = 0
        L_0x0d02:
            r37 = r0
            r27 = r1
            r34 = r5
            r33 = r6
            r35 = r9
            r40 = r10
            r41 = r11
            r42 = r12
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
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r36 = 0
            r38 = 0
            r39 = 0
            goto L_0x156b
        L_0x0d2f:
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 1
            goto L_0x1557
        L_0x0d48:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0d64
            java.lang.String r5 = r0.trim()
        L_0x0d64:
            r39 = r5
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
            r27 = 0
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
            goto L_0x1565
        L_0x0d8b:
            java.lang.String r0 = "themes"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0daa
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 2
            goto L_0x1555
        L_0x0daa:
            java.lang.String r0 = "devices"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0dc9
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 3
            goto L_0x1555
        L_0x0dc9:
            java.lang.String r0 = "folders"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0de9
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 4
            goto L_0x1555
        L_0x0de9:
            java.lang.String r0 = "change_number"
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0e09
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 5
            goto L_0x1555
        L_0x0e09:
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
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 1
            goto L_0x1555
        L_0x0e20:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r53 = r0
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
            r27 = 0
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
            goto L_0x1581
        L_0x0e79:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r48 = r0
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
            r27 = 0
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
            goto L_0x1577
        L_0x0ec8:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r11)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r5 = r0.getQueryParameter(r10)
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x0var_
            java.lang.String r6 = "{"
            boolean r6 = r5.startsWith(r6)
            if (r6 == 0) goto L_0x0var_
            java.lang.String r6 = "}"
            boolean r6 = r5.endsWith(r6)
            if (r6 == 0) goto L_0x0var_
            java.lang.String r6 = "nonce"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r9 = "nonce"
            r1.put(r9, r6)
            goto L_0x0var_
        L_0x0var_:
            java.lang.String r6 = r0.getQueryParameter(r13)
            r1.put(r13, r6)
        L_0x0var_:
            java.lang.String r6 = "bot_id"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r9 = "bot_id"
            r1.put(r9, r6)
            r1.put(r10, r5)
            java.lang.String r5 = "public_key"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "public_key"
            r1.put(r6, r5)
            java.lang.String r5 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r5)
            java.lang.String r5 = "callback_url"
            r1.put(r5, r0)
            r47 = r1
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
            r27 = 0
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
            goto L_0x1575
        L_0x0f6b:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r5 = "chat_id"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r0 = r0.getQueryParameter(r8)
            if (r1 == 0) goto L_0x0var_
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0f9b }
            goto L_0x0f9c
        L_0x0var_:
            if (r5 == 0) goto L_0x0f9b
            int r1 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x0f9b }
            r5 = r1
            r1 = 0
            goto L_0x0f9d
        L_0x0f9b:
            r1 = 0
        L_0x0f9c:
            r5 = 0
        L_0x0f9d:
            if (r0 == 0) goto L_0x0fa4
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0fa4 }
            goto L_0x0fa5
        L_0x0fa4:
            r0 = 0
        L_0x0fa5:
            r30 = r0
            r27 = r1
            r29 = r5
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
            goto L_0x1553
        L_0x0fba:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r6 = "code"
            java.lang.String r0 = r0.getQueryParameter(r6)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0ff0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            goto L_0x0ff1
        L_0x0ff0:
            r0 = 0
        L_0x0ff1:
            r50 = r0
            r51 = r1
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
            r27 = 0
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
            goto L_0x157d
        L_0x1030:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r7)
            r5 = r0
            r0 = 0
            goto L_0x1109
        L_0x104c:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r6 = "tg://msg"
            java.lang.String r0 = r0.replace(r6, r11)
            java.lang.String r6 = "tg://share"
            java.lang.String r0 = r0.replace(r6, r11)
            java.lang.String r6 = "tg:share"
            java.lang.String r0 = r0.replace(r6, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r6 = "url"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 != 0) goto L_0x1071
            goto L_0x1072
        L_0x1071:
            r5 = r6
        L_0x1072:
            java.lang.String r6 = "text"
            java.lang.String r6 = r0.getQueryParameter(r6)
            if (r6 == 0) goto L_0x10a8
            int r6 = r5.length()
            if (r6 <= 0) goto L_0x1091
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r1)
            java.lang.String r5 = r6.toString()
            r6 = 1
            goto L_0x1092
        L_0x1091:
            r6 = 0
        L_0x1092:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r5 = "text"
            java.lang.String r0 = r0.getQueryParameter(r5)
            r9.append(r0)
            java.lang.String r5 = r9.toString()
            goto L_0x10a9
        L_0x10a8:
            r6 = 0
        L_0x10a9:
            int r0 = r5.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x10b9
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r5.substring(r9, r0)
            goto L_0x10bb
        L_0x10b9:
            r9 = 0
            r0 = r5
        L_0x10bb:
            boolean r5 = r0.endsWith(r1)
            if (r5 == 0) goto L_0x10cc
            int r5 = r0.length()
            r10 = 1
            int r5 = r5 - r10
            java.lang.String r0 = r0.substring(r9, r5)
            goto L_0x10bb
        L_0x10cc:
            r13 = r0
            r11 = r6
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            goto L_0x110f
        L_0x10d6:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r6 = r0
            r0 = 0
            r1 = 0
            r5 = 0
            goto L_0x110a
        L_0x10f1:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r1 = 0
            r5 = 0
        L_0x1109:
            r6 = 0
        L_0x110a:
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x110f:
            r20 = 6
            goto L_0x154b
        L_0x1113:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r5 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r5.<init>()
            r1.settings = r5
            java.lang.String r5 = "slug"
            java.lang.String r5 = r0.getQueryParameter(r5)
            r1.slug = r5
            if (r5 != 0) goto L_0x1141
            java.lang.String r5 = "color"
            java.lang.String r5 = r0.getQueryParameter(r5)
            r1.slug = r5
        L_0x1141:
            java.lang.String r5 = r1.slug
            if (r5 == 0) goto L_0x1161
            int r5 = r5.length()
            r6 = 6
            if (r5 != r6) goto L_0x1161
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x115a }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x115a }
            r6 = 16
            int r5 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ Exception -> 0x115a }
            r5 = r5 | r22
            r0.background_color = r5     // Catch:{ Exception -> 0x115a }
        L_0x115a:
            r5 = 0
            r1.slug = r5
            r9 = r5
        L_0x115e:
            r10 = 6
            goto L_0x1307
        L_0x1161:
            java.lang.String r5 = r1.slug
            if (r5 == 0) goto L_0x1219
            int r5 = r5.length()
            r6 = 13
            if (r5 < r6) goto L_0x1219
            java.lang.String r5 = r1.slug
            r6 = 6
            char r5 = r5.charAt(r6)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)
            if (r5 == 0) goto L_0x1219
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x11fc }
            java.lang.String r9 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r6)     // Catch:{ Exception -> 0x11fc }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x11fc }
            r6 = r9 | r22
            r5.background_color = r6     // Catch:{ Exception -> 0x11fc }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x11fc }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r9 = 7
            r10 = 13
            java.lang.String r6 = r6.substring(r9, r10)     // Catch:{ Exception -> 0x11fc }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x11fc }
            r6 = r6 | r22
            r5.second_background_color = r6     // Catch:{ Exception -> 0x11fc }
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x11fc }
            int r5 = r5.length()     // Catch:{ Exception -> 0x11fc }
            r6 = 20
            if (r5 < r6) goto L_0x11d0
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r6 = 13
            char r5 = r5.charAt(r6)     // Catch:{ Exception -> 0x11fc }
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x11fc }
            if (r5 == 0) goto L_0x11d0
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x11fc }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r9 = 14
            r10 = 20
            java.lang.String r6 = r6.substring(r9, r10)     // Catch:{ Exception -> 0x11fc }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x11fc }
            r6 = r6 | r22
            r5.third_background_color = r6     // Catch:{ Exception -> 0x11fc }
        L_0x11d0:
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x11fc }
            int r5 = r5.length()     // Catch:{ Exception -> 0x11fc }
            r6 = 27
            if (r5 != r6) goto L_0x11fc
            java.lang.String r5 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r6 = 20
            char r5 = r5.charAt(r6)     // Catch:{ Exception -> 0x11fc }
            boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x11fc }
            if (r5 == 0) goto L_0x11fc
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x11fc }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x11fc }
            r9 = 21
            java.lang.String r6 = r6.substring(r9)     // Catch:{ Exception -> 0x11fc }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x11fc }
            r6 = r6 | r22
            r5.fourth_background_color = r6     // Catch:{ Exception -> 0x11fc }
        L_0x11fc:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x1214 }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1214 }
            if (r5 != 0) goto L_0x1214
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x1214 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1214 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x1214 }
            r5.rotation = r0     // Catch:{ Exception -> 0x1214 }
        L_0x1214:
            r9 = 0
            r1.slug = r9
            goto L_0x115e
        L_0x1219:
            r9 = 0
            java.lang.String r5 = "mode"
            java.lang.String r5 = r0.getQueryParameter(r5)
            if (r5 == 0) goto L_0x1257
            java.lang.String r5 = r5.toLowerCase()
            java.lang.String r6 = " "
            java.lang.String[] r5 = r5.split(r6)
            if (r5 == 0) goto L_0x1257
            int r6 = r5.length
            if (r6 <= 0) goto L_0x1257
            r6 = 0
        L_0x1232:
            int r10 = r5.length
            if (r6 >= r10) goto L_0x1257
            r10 = r5[r6]
            java.lang.String r11 = "blur"
            boolean r10 = r11.equals(r10)
            if (r10 == 0) goto L_0x1245
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r11 = 1
            r10.blur = r11
            goto L_0x1254
        L_0x1245:
            r11 = 1
            r10 = r5[r6]
            java.lang.String r12 = "motion"
            boolean r10 = r12.equals(r10)
            if (r10 == 0) goto L_0x1254
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r10.motion = r11
        L_0x1254:
            int r6 = r6 + 1
            goto L_0x1232
        L_0x1257:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings
            java.lang.String r6 = "intensity"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
            int r6 = r6.intValue()
            r5.intensity = r6
            java.lang.String r5 = "bg_color"
            java.lang.String r5 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x12ee }
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x12ee }
            if (r6 != 0) goto L_0x12ee
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x12ee }
            r10 = 6
            r11 = 0
            java.lang.String r12 = r5.substring(r11, r10)     // Catch:{ Exception -> 0x12ef }
            r11 = 16
            int r12 = java.lang.Integer.parseInt(r12, r11)     // Catch:{ Exception -> 0x12ef }
            r11 = r12 | r22
            r6.background_color = r11     // Catch:{ Exception -> 0x12ef }
            int r6 = r5.length()     // Catch:{ Exception -> 0x12ef }
            r11 = 13
            if (r6 < r11) goto L_0x12ef
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x12ef }
            r12 = 8
            java.lang.String r12 = r5.substring(r12, r11)     // Catch:{ Exception -> 0x12ef }
            r11 = 16
            int r12 = java.lang.Integer.parseInt(r12, r11)     // Catch:{ Exception -> 0x12ef }
            r11 = r12 | r22
            r6.second_background_color = r11     // Catch:{ Exception -> 0x12ef }
            int r6 = r5.length()     // Catch:{ Exception -> 0x12ef }
            r11 = 20
            if (r6 < r11) goto L_0x12c7
            r6 = 13
            char r6 = r5.charAt(r6)     // Catch:{ Exception -> 0x12ef }
            boolean r6 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r6)     // Catch:{ Exception -> 0x12ef }
            if (r6 == 0) goto L_0x12c7
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x12ef }
            r12 = 14
            java.lang.String r12 = r5.substring(r12, r11)     // Catch:{ Exception -> 0x12ef }
            r11 = 16
            int r12 = java.lang.Integer.parseInt(r12, r11)     // Catch:{ Exception -> 0x12ef }
            r11 = r12 | r22
            r6.third_background_color = r11     // Catch:{ Exception -> 0x12ef }
        L_0x12c7:
            int r6 = r5.length()     // Catch:{ Exception -> 0x12ef }
            r11 = 27
            if (r6 != r11) goto L_0x12ef
            r6 = 20
            char r6 = r5.charAt(r6)     // Catch:{ Exception -> 0x12ef }
            boolean r6 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r6)     // Catch:{ Exception -> 0x12ef }
            if (r6 == 0) goto L_0x12ef
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x12ef }
            r11 = 21
            java.lang.String r5 = r5.substring(r11)     // Catch:{ Exception -> 0x12ef }
            r11 = 16
            int r5 = java.lang.Integer.parseInt(r5, r11)     // Catch:{ Exception -> 0x12ef }
            r5 = r5 | r22
            r6.fourth_background_color = r5     // Catch:{ Exception -> 0x12ef }
            goto L_0x12ef
        L_0x12ee:
            r10 = 6
        L_0x12ef:
            java.lang.String r5 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r5)     // Catch:{ Exception -> 0x1307 }
            boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1307 }
            if (r5 != 0) goto L_0x1307
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r1.settings     // Catch:{ Exception -> 0x1307 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1307 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x1307 }
            r5.rotation = r0     // Catch:{ Exception -> 0x1307 }
        L_0x1307:
            r52 = r1
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r22 = r13
            r39 = r22
            r40 = r39
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
            r53 = r51
            goto L_0x13c4
        L_0x1330:
            r9 = 0
            r10 = 6
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r1 = "channel"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r6 = r5.intValue()
            if (r6 == 0) goto L_0x1362
            int r6 = r1.intValue()
            if (r6 != 0) goto L_0x1364
        L_0x1362:
            r1 = r9
            r5 = r1
        L_0x1364:
            java.lang.String r6 = r0.getQueryParameter(r12)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
            int r11 = r6.intValue()
            if (r11 != 0) goto L_0x1373
            r6 = r9
        L_0x1373:
            java.lang.String r11 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r11)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r11 = r0.intValue()
            if (r11 != 0) goto L_0x139d
            r43 = r1
            r22 = r5
            r44 = r6
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r39 = r13
            r40 = r39
            r41 = r40
            r42 = r41
            r45 = r42
            r46 = r45
            goto L_0x13b6
        L_0x139d:
            r45 = r0
            r43 = r1
            r22 = r5
            r44 = r6
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r39 = r13
            r40 = r39
            r41 = r40
            r42 = r41
            r46 = r42
        L_0x13b6:
            r47 = r46
            r48 = r47
            r49 = r48
            r50 = r49
            r51 = r50
            r52 = r51
            r53 = r52
        L_0x13c4:
            r54 = r53
            r11 = 0
            r20 = 6
            goto L_0x146c
        L_0x13cb:
            r9 = 0
            r20 = 6
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r6.replace(r0, r11)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r11)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r5 = "telegrampassport"
            boolean r5 = r5.equals(r1)
            if (r5 == 0) goto L_0x1484
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r5 = r0.getQueryParameter(r10)
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x1417
            java.lang.String r6 = "{"
            boolean r6 = r5.startsWith(r6)
            if (r6 == 0) goto L_0x1417
            java.lang.String r6 = "}"
            boolean r6 = r5.endsWith(r6)
            if (r6 == 0) goto L_0x1417
            java.lang.String r6 = "nonce"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r11 = "nonce"
            r1.put(r11, r6)
            goto L_0x141e
        L_0x1417:
            java.lang.String r6 = r0.getQueryParameter(r13)
            r1.put(r13, r6)
        L_0x141e:
            java.lang.String r6 = "bot_id"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r11 = "bot_id"
            r1.put(r11, r6)
            r1.put(r10, r5)
            java.lang.String r5 = "public_key"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "public_key"
            r1.put(r6, r5)
            java.lang.String r5 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r5)
            java.lang.String r5 = "callback_url"
            r1.put(r5, r0)
            r47 = r1
            r0 = r9
            r1 = r0
            r5 = r1
            r6 = r5
            r10 = r6
            r12 = r10
            r13 = r12
            r22 = r13
            r39 = r22
            r40 = r39
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r45 = r44
            r46 = r45
            r48 = r46
            r49 = r48
            r50 = r49
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
            r11 = 0
        L_0x146c:
            r27 = 0
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
            goto L_0x1583
        L_0x1484:
            java.lang.String r5 = "start"
            java.lang.String r5 = r0.getQueryParameter(r5)
            java.lang.String r6 = "startgroup"
            java.lang.String r6 = r0.getQueryParameter(r6)
            java.lang.String r10 = "game"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r11 = "voicechat"
            java.lang.String r11 = r0.getQueryParameter(r11)
            java.lang.String r13 = "post"
            java.lang.String r13 = r0.getQueryParameter(r13)
            java.lang.Integer r13 = org.telegram.messenger.Utilities.parseInt(r13)
            int r22 = r13.intValue()
            if (r22 != 0) goto L_0x14ad
            r13 = r9
        L_0x14ad:
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r22 = r12.intValue()
            if (r22 != 0) goto L_0x14bc
            r12 = r9
        L_0x14bc:
            java.lang.String r9 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x14fc
            r9 = r1
            r46 = r10
            r54 = r11
            r44 = r12
            r22 = r13
            r0 = 0
            r1 = 0
            r11 = 0
            r13 = 0
            r27 = 0
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
            r45 = 0
            goto L_0x152b
        L_0x14fc:
            r45 = r0
            r9 = r1
            r46 = r10
            r54 = r11
            r44 = r12
            r22 = r13
            r0 = 0
            r1 = 0
            r11 = 0
            r13 = 0
            r27 = 0
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
        L_0x152b:
            r47 = 0
            r48 = 0
            r49 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            r10 = r5
            r12 = r6
            r5 = 0
            r6 = 0
            goto L_0x1583
        L_0x153e:
            r28 = r11
        L_0x1540:
            r20 = 6
            r0 = 0
            r1 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x154b:
            r22 = 0
            r27 = 0
            r29 = 0
            r30 = 0
        L_0x1553:
            r31 = 0
        L_0x1555:
            r32 = 0
        L_0x1557:
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
        L_0x1563:
            r39 = 0
        L_0x1565:
            r40 = 0
            r41 = 0
            r42 = 0
        L_0x156b:
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r47 = 0
        L_0x1575:
            r48 = 0
        L_0x1577:
            r49 = 0
        L_0x1579:
            r50 = 0
            r51 = 0
        L_0x157d:
            r52 = 0
            r53 = 0
        L_0x1581:
            r54 = 0
        L_0x1583:
            boolean r23 = r14.hasExtra(r3)
            if (r23 == 0) goto L_0x15cd
            r55 = r8
            int r8 = r15.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            boolean r8 = r8.isClientActivated()
            if (r8 == 0) goto L_0x15a3
            java.lang.String r8 = "tg"
            boolean r2 = r8.equals(r2)
            if (r2 == 0) goto L_0x15a3
            if (r49 != 0) goto L_0x15a3
            r2 = 1
            goto L_0x15a4
        L_0x15a3:
            r2 = 0
        L_0x15a4:
            com.google.firebase.appindexing.builders.AssistActionBuilder r8 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r8.<init>()
            r67 = r7
            java.lang.String r7 = r14.getStringExtra(r3)
            com.google.firebase.appindexing.builders.AssistActionBuilder r7 = r8.setActionToken(r7)
            if (r2 == 0) goto L_0x15b8
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x15ba
        L_0x15b8:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x15ba:
            com.google.firebase.appindexing.Action$Builder r2 = r7.setActionStatus(r2)
            com.google.firebase.appindexing.Action r2 = r2.build()
            com.google.firebase.appindexing.FirebaseUserActions r7 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r64)
            r7.end(r2)
            r14.removeExtra(r3)
            goto L_0x15d1
        L_0x15cd:
            r67 = r7
            r55 = r8
        L_0x15d1:
            if (r50 != 0) goto L_0x15eb
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x15e0
            goto L_0x15eb
        L_0x15e0:
            r7 = r15
            r62 = r28
            r63 = r55
            r25 = -1
            r56 = 0
            goto L_0x174c
        L_0x15eb:
            if (r1 != 0) goto L_0x172d
            if (r5 == 0) goto L_0x15f1
            goto L_0x172d
        L_0x15f1:
            if (r9 != 0) goto L_0x16d0
            if (r0 != 0) goto L_0x16d0
            if (r6 != 0) goto L_0x16d0
            if (r13 != 0) goto L_0x16d0
            if (r46 != 0) goto L_0x16d0
            if (r54 != 0) goto L_0x16d0
            if (r47 != 0) goto L_0x16d0
            if (r49 != 0) goto L_0x16d0
            if (r48 != 0) goto L_0x16d0
            if (r50 != 0) goto L_0x16d0
            if (r52 != 0) goto L_0x16d0
            if (r43 != 0) goto L_0x16d0
            if (r53 != 0) goto L_0x16d0
            if (r51 == 0) goto L_0x160f
            goto L_0x16d0
        L_0x160f:
            android.content.ContentResolver r56 = r64.getContentResolver()     // Catch:{ Exception -> 0x16b9 }
            android.net.Uri r57 = r65.getData()     // Catch:{ Exception -> 0x16b9 }
            r58 = 0
            r59 = 0
            r60 = 0
            r61 = 0
            android.database.Cursor r1 = r56.query(r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x16b9 }
            if (r1 == 0) goto L_0x16a8
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x169f }
            if (r0 == 0) goto L_0x16a8
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x169f }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x169f }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x169f }
            int r0 = r0.intValue()     // Catch:{ all -> 0x169f }
            r2 = 0
            r7 = 3
        L_0x163f:
            if (r2 >= r7) goto L_0x165b
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1659 }
            int r3 = r3.getClientUserId()     // Catch:{ all -> 0x1659 }
            if (r3 != r0) goto L_0x1655
            r3 = 0
            r28[r3] = r2     // Catch:{ all -> 0x1659 }
            r0 = r28[r3]     // Catch:{ all -> 0x1659 }
            r8 = 1
            r15.switchToAccount(r0, r8)     // Catch:{ all -> 0x169d }
            goto L_0x165c
        L_0x1655:
            r8 = 1
            int r2 = r2 + 1
            goto L_0x163f
        L_0x1659:
            r0 = move-exception
            goto L_0x16a1
        L_0x165b:
            r8 = 1
        L_0x165c:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x169d }
            int r2 = r1.getInt(r0)     // Catch:{ all -> 0x169d }
            r3 = 0
            r0 = r28[r3]     // Catch:{ all -> 0x169d }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x169d }
            int r4 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x169d }
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x169d }
            r0.postNotificationName(r4, r5)     // Catch:{ all -> 0x169d }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1699 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1699 }
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r3 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x1699 }
            if (r3 == 0) goto L_0x168a
            r27 = r2
            r6 = 1
            goto L_0x16ac
        L_0x168a:
            java.lang.String r3 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r3)     // Catch:{ all -> 0x1699 }
            r27 = r2
            r6 = r33
            if (r0 == 0) goto L_0x16ac
            r34 = 1
            goto L_0x16ac
        L_0x1699:
            r0 = move-exception
            r27 = r2
            goto L_0x16a2
        L_0x169d:
            r0 = move-exception
            goto L_0x16a2
        L_0x169f:
            r0 = move-exception
            r7 = 3
        L_0x16a1:
            r8 = 1
        L_0x16a2:
            r1.close()     // Catch:{ all -> 0x16a5 }
        L_0x16a5:
            throw r0     // Catch:{ Exception -> 0x16a6 }
        L_0x16a6:
            r0 = move-exception
            goto L_0x16bc
        L_0x16a8:
            r7 = 3
            r8 = 1
            r6 = r33
        L_0x16ac:
            if (r1 == 0) goto L_0x16b6
            r1.close()     // Catch:{ Exception -> 0x16b2 }
            goto L_0x16b6
        L_0x16b2:
            r0 = move-exception
            r33 = r6
            goto L_0x16bc
        L_0x16b6:
            r33 = r6
            goto L_0x16bf
        L_0x16b9:
            r0 = move-exception
            r7 = 3
            r8 = 1
        L_0x16bc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16bf:
            r7 = r15
            r12 = r27
            r62 = r28
            r13 = r31
            r9 = r39
            r63 = r55
            r25 = -1
            r56 = 0
            goto L_0x1777
        L_0x16d0:
            r7 = 3
            r8 = 1
            if (r13 == 0) goto L_0x16ee
            java.lang.String r1 = "@"
            boolean r1 = r13.startsWith(r1)
            if (r1 == 0) goto L_0x16ee
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = " "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            r13 = r1
        L_0x16ee:
            r16 = 0
            r2 = r28[r16]
            r23 = 0
            r1 = r64
            r56 = 0
            r3 = r9
            r4 = r0
            r25 = -1
            r5 = r6
            r9 = 2
            r6 = r10
            r10 = 3
            r7 = r12
            r12 = r55
            r17 = 1
            r8 = r13
            r13 = 0
            r9 = r11
            r11 = 3
            r10 = r22
            r62 = r28
            r11 = r43
            r63 = r12
            r12 = r44
            r13 = r45
            r14 = r46
            r15 = r47
            r16 = r48
            r17 = r49
            r18 = r50
            r19 = r51
            r20 = r52
            r21 = r53
            r22 = r54
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23)
            r7 = r64
            goto L_0x174c
        L_0x172d:
            r62 = r28
            r63 = r55
            r25 = -1
            r56 = 0
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r0.putString(r4, r1)
            r1 = r67
            r0.putString(r1, r5)
            org.telegram.ui.-$$Lambda$LaunchActivity$ugOxq87iV4YY8Ict2iiLsqxg7qY r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$ugOxq87iV4YY8Ict2iiLsqxg7qY
            r7 = r64
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x174c:
            r12 = r27
            r13 = r31
            r9 = r39
            goto L_0x1777
        L_0x1753:
            r63 = r8
            r62 = r11
            r7 = r15
            r25 = -1
            r56 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r29 = 0
            r30 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r40 = 0
            r41 = 0
            r42 = 0
        L_0x1777:
            r11 = r9
            r6 = r12
            r8 = r13
            r14 = r29
            r9 = r30
            r12 = r33
            r13 = r34
            r1 = r40
            r2 = r41
            r67 = r42
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
            r25 = 0
            goto L_0x192b
        L_0x179d:
            r63 = r8
            r62 = r11
            r7 = r15
            r25 = -1
            r56 = 0
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x17c3
            r18 = r56
            r10 = r62
            r3 = r63
            r67 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 1
            r9 = 0
            goto L_0x1912
        L_0x17c3:
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x17eb
            r18 = r56
            r10 = r62
            r3 = r63
            r67 = 0
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
            r20 = 1
            goto L_0x191d
        L_0x17eb:
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x189a
            java.lang.String r0 = "chatId"
            r8 = r65
            r9 = 0
            int r0 = r8.getIntExtra(r0, r9)
            java.lang.String r1 = "userId"
            int r1 = r8.getIntExtra(r1, r9)
            java.lang.String r2 = "encId"
            int r2 = r8.getIntExtra(r2, r9)
            java.lang.String r3 = "appWidgetId"
            int r5 = r8.getIntExtra(r3, r9)
            if (r5 == 0) goto L_0x1828
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
            goto L_0x1874
        L_0x1828:
            r3 = r63
            int r4 = r8.getIntExtra(r3, r9)
            if (r0 == 0) goto L_0x1845
            r10 = r62
            r1 = r10[r9]
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r1.postNotificationName(r2, r5)
            r1 = 0
            r2 = 0
        L_0x1841:
            r5 = -1
            r12 = 0
        L_0x1843:
            r13 = 0
            goto L_0x1874
        L_0x1845:
            r10 = r62
            if (r1 == 0) goto L_0x185c
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
            goto L_0x1843
        L_0x185c:
            if (r2 == 0) goto L_0x186e
            r0 = r10[r9]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r0.postNotificationName(r1, r5)
            r0 = 0
            r1 = 0
            goto L_0x1841
        L_0x186e:
            r0 = 0
            r1 = 0
            r2 = 0
            r5 = -1
            r12 = 0
            r13 = 1
        L_0x1874:
            r14 = r0
            r8 = r1
            r15 = r2
            r9 = r4
            r6 = r12
            r0 = r25
            r18 = r56
            r67 = 0
            r1 = 0
            r2 = 0
            r11 = 0
            r12 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r21 = 0
            r32 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r25 = r13
            r13 = 0
            goto L_0x192b
        L_0x189a:
            r8 = r65
            r10 = r62
            r3 = r63
            r9 = 0
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x18bf
            r18 = r56
            r67 = 0
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
            r16 = 1
            goto L_0x1919
        L_0x18bf:
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x18df
            r18 = r56
            r67 = 0
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
            goto L_0x191b
        L_0x18df:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x1908
            r18 = r56
            r67 = 0
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
            r21 = 1
            goto L_0x191f
        L_0x18ff:
            r56 = r3
            r3 = r8
            r10 = r11
            r8 = r14
            r7 = r15
            r9 = 0
            r25 = -1
        L_0x1908:
            r18 = r56
            r67 = 0
            r0 = -1
            r1 = 0
            r2 = 0
            r5 = -1
            r6 = 0
            r8 = 0
        L_0x1912:
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
        L_0x1919:
            r17 = 0
        L_0x191b:
            r20 = 0
        L_0x191d:
            r21 = 0
        L_0x191f:
            r25 = 0
            r32 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
        L_0x192b:
            int r4 = r7.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            boolean r4 = r4.isClientActivated()
            if (r4 == 0) goto L_0x1dc3
            if (r11 == 0) goto L_0x1963
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r4 = r4.getLastFragment()
            r68 = r2
            boolean r2 = r4 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x195f
            org.telegram.ui.DialogsActivity r4 = (org.telegram.ui.DialogsActivity) r4
            boolean r2 = r4.isMainDialogList()
            if (r2 == 0) goto L_0x195d
            android.view.View r2 = r4.getFragmentView()
            if (r2 == 0) goto L_0x1958
            r2 = 1
            r4.search(r11, r2)
            goto L_0x1966
        L_0x1958:
            r2 = 1
            r4.setInitialSearchString(r11)
            goto L_0x1966
        L_0x195d:
            r2 = 1
            goto L_0x1966
        L_0x195f:
            r2 = 1
            r25 = 1
            goto L_0x1966
        L_0x1963:
            r68 = r2
            goto L_0x195d
        L_0x1966:
            if (r6 == 0) goto L_0x19df
            if (r12 != 0) goto L_0x19b8
            if (r13 == 0) goto L_0x196d
            goto L_0x19b8
        L_0x196d:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putInt(r1, r6)
            if (r9 == 0) goto L_0x197c
            r0.putInt(r3, r9)
        L_0x197c:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x199e
            r1 = 0
            r3 = r10[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r1 = r1.checkCanOpenChat(r0, r3)
            if (r1 == 0) goto L_0x1a57
        L_0x199e:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1a57
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1a55
        L_0x19b8:
            if (r35 == 0) goto L_0x19d3
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x1dc3
            org.telegram.messenger.MessagesController r1 = r0.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r1, r13)
            goto L_0x1dc3
        L_0x19d3:
            r1 = 0
            r0 = r10[r1]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r6, r13, r0)
            goto L_0x1dc3
        L_0x19df:
            if (r14 == 0) goto L_0x1a2b
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putInt(r1, r14)
            if (r9 == 0) goto L_0x19f0
            r0.putInt(r3, r9)
        L_0x19f0:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1a12
            r1 = 0
            r3 = r10[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r1 = r1.checkCanOpenChat(r0, r3)
            if (r1 == 0) goto L_0x1a57
        L_0x1a12:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1a57
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1a55
        L_0x1a2b:
            if (r15 == 0) goto L_0x1a5e
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enc_id"
            r0.putInt(r1, r15)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r27 = 0
            r28 = 1
            r29 = 1
            r30 = 0
            r25 = r0
            r26 = r1
            boolean r0 = r25.presentFragment(r26, r27, r28, r29, r30)
            if (r0 == 0) goto L_0x1a57
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
        L_0x1a55:
            r13 = 1
            goto L_0x1a58
        L_0x1a57:
            r13 = 0
        L_0x1a58:
            r0 = r66
            r3 = 0
            r8 = 1
            goto L_0x1dc8
        L_0x1a5e:
            if (r25 == 0) goto L_0x1a9a
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1a6c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1a97
        L_0x1a6c:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1a97
        L_0x1a76:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r2
            if (r0 <= 0) goto L_0x1a90
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x1a76
        L_0x1a90:
            r3 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.closeLastFragment(r3)
            goto L_0x1a98
        L_0x1a97:
            r3 = 0
        L_0x1a98:
            r0 = 0
            goto L_0x1abb
        L_0x1a9a:
            r3 = 0
            if (r16 == 0) goto L_0x1abf
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1ab9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r1.<init>(r7)
            r0.showDialog(r1)
        L_0x1ab9:
            r0 = r66
        L_0x1abb:
            r3 = 0
            r8 = 1
            goto L_0x1dc7
        L_0x1abf:
            if (r17 == 0) goto L_0x1ae4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1ab9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.-$$Lambda$LaunchActivity$zK6CNluZzXSNlhd6eyW5AL-PZR8 r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$zK6CNluZzXSNlhd6eyW5AL-PZR8
            r3.<init>(r10)
            r1.<init>(r7, r3)
            r0.showDialog(r1)
            goto L_0x1ab9
        L_0x1ae4:
            android.net.Uri r3 = r7.exportingChatUri
            if (r3 == 0) goto L_0x1aef
            java.util.ArrayList<android.net.Uri> r0 = r7.documentsUrisArray
            r7.runImportRequest(r3, r0)
            goto L_0x1dc3
        L_0x1aef:
            java.lang.String r3 = r7.videoPath
            if (r3 != 0) goto L_0x1d92
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r7.photoPathsArray
            if (r3 != 0) goto L_0x1d92
            java.lang.String r3 = r7.sendingText
            if (r3 != 0) goto L_0x1d92
            java.util.ArrayList<java.lang.String> r3 = r7.documentsPathsArray
            if (r3 != 0) goto L_0x1d92
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r7.contactsToSend
            if (r3 != 0) goto L_0x1d92
            java.util.ArrayList<android.net.Uri> r3 = r7.documentsUrisArray
            if (r3 == 0) goto L_0x1b09
            goto L_0x1d92
        L_0x1b09:
            if (r8 == 0) goto L_0x1b91
            if (r8 != r2) goto L_0x1b28
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            int r1 = r1.clientUserId
            java.lang.String r3 = "user_id"
            r0.putInt(r3, r1)
            org.telegram.ui.ProfileActivity r9 = new org.telegram.ui.ProfileActivity
            r9.<init>(r0)
        L_0x1b24:
            r13 = r9
            r0 = 0
        L_0x1b26:
            r1 = 6
            goto L_0x1b5d
        L_0x1b28:
            r3 = 2
            if (r8 != r3) goto L_0x1b32
            org.telegram.ui.ThemeActivity r9 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r9.<init>(r1)
            goto L_0x1b24
        L_0x1b32:
            r1 = 0
            r3 = 3
            if (r8 != r3) goto L_0x1b3c
            org.telegram.ui.SessionsActivity r9 = new org.telegram.ui.SessionsActivity
            r9.<init>(r1)
            goto L_0x1b24
        L_0x1b3c:
            r1 = 4
            if (r8 != r1) goto L_0x1b45
            org.telegram.ui.FiltersSetupActivity r9 = new org.telegram.ui.FiltersSetupActivity
            r9.<init>()
            goto L_0x1b24
        L_0x1b45:
            r1 = 5
            if (r8 != r1) goto L_0x1b50
            org.telegram.ui.ActionIntroActivity r9 = new org.telegram.ui.ActionIntroActivity
            r9.<init>(r3)
            r13 = r9
            r0 = 1
            goto L_0x1b26
        L_0x1b50:
            r1 = 6
            if (r8 != r1) goto L_0x1b5b
            org.telegram.ui.EditWidgetActivity r9 = new org.telegram.ui.EditWidgetActivity
            r9.<init>(r5, r0, r2)
            r13 = r9
            r0 = 0
            goto L_0x1b5d
        L_0x1b5b:
            r0 = 0
            r13 = 0
        L_0x1b5d:
            if (r8 != r1) goto L_0x1b6b
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            goto L_0x1b73
        L_0x1b6b:
            org.telegram.ui.-$$Lambda$LaunchActivity$fxpwFjHemArViH2vQ0s5Y5pZu-Y r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$fxpwFjHemArViH2vQ0s5Y5pZu-Y
            r1.<init>(r13, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x1b73:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1b8a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1bcb
        L_0x1b8a:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1bcb
        L_0x1b91:
            r3 = 2
            if (r20 == 0) goto L_0x1bd1
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
            if (r0 == 0) goto L_0x1bc5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1bcb
        L_0x1bc5:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
        L_0x1bcb:
            r0 = r66
            r3 = 0
            r8 = 1
            goto L_0x1db1
        L_0x1bd1:
            if (r1 == 0) goto L_0x1c2e
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
            org.telegram.ui.-$$Lambda$LaunchActivity$ENDDsvzG4ZV3mNux_L7yRhccjYI r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$ENDDsvzG4ZV3mNux_L7yRhccjYI
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
            if (r0 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1bcb
        L_0x1CLASSNAME:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1bcb
        L_0x1c2e:
            if (r38 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.ActionIntroActivity r13 = new org.telegram.ui.ActionIntroActivity
            r0 = 5
            r13.<init>(r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$CySiNuRPhQo3Swslj0qNla-ESyQ r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$CySiNuRPhQo3Swslj0qNla-ESyQ
            r0.<init>(r13)
            r13.setQrLoginDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1bcb
        L_0x1CLASSNAME:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1bcb
        L_0x1CLASSNAME:
            r1 = 0
            if (r36 == 0) goto L_0x1cbd
            org.telegram.ui.NewContactActivity r13 = new org.telegram.ui.NewContactActivity
            r13.<init>()
            if (r68 == 0) goto L_0x1CLASSNAME
            java.lang.String r0 = " "
            r9 = r68
            java.lang.String[] r0 = r9.split(r0, r3)
            r3 = r0[r1]
            int r4 = r0.length
            if (r4 <= r2) goto L_0x1CLASSNAME
            r9 = r0[r2]
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r9 = 0
        L_0x1CLASSNAME:
            r13.setInitialName(r3, r9)
        L_0x1CLASSNAME:
            if (r67 == 0) goto L_0x1CLASSNAME
            r0 = r67
            java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0, r2)
            r13.setInitialPhoneNumber(r0, r1)
        L_0x1CLASSNAME:
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1cb5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1bcb
        L_0x1cb5:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r2, r1)
            goto L_0x1bcb
        L_0x1cbd:
            r0 = r67
            r9 = r68
            if (r21 == 0) goto L_0x1cdd
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r64
            r8 = 1
            r2 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1cda
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r8
        L_0x1cda:
            r3 = 0
            goto L_0x1dc5
        L_0x1cdd:
            r8 = 1
            if (r37 == 0) goto L_0x1d5f
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r1.getLastFragment()
            if (r1 == 0) goto L_0x1d5a
            android.app.Activity r2 = r1.getParentActivity()
            if (r2 == 0) goto L_0x1d5a
            int r2 = r7.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r3 = 0
            java.lang.String r0 = org.telegram.ui.NewContactActivity.getPhoneNumber(r7, r2, r0, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r1.getParentActivity()
            r2.<init>((android.content.Context) r3)
            r3 = 2131626251(0x7f0e090b, float:1.8879733E38)
            java.lang.String r4 = "NewContactAlertTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r2.setTitle(r3)
            r3 = 2131626250(0x7f0e090a, float:1.887973E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r5 = r5.format(r0)
            r6 = 0
            r4[r6] = r5
            java.lang.String r5 = "NewContactAlertMessage"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r2.setMessage(r3)
            r3 = 2131626249(0x7f0e0909, float:1.8879729E38)
            java.lang.String r4 = "NewContactAlertButton"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$KCwcw_qpAPhtZTLkQ4suwK4JnK8 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$KCwcw_qpAPhtZTLkQ4suwK4JnK8
            r4.<init>(r0, r9, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r2.setPositiveButton(r3, r4)
            r2 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 0
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r2, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1.showDialog(r0)
            r13 = 1
            goto L_0x1d5c
        L_0x1d5a:
            r3 = 0
            r13 = 0
        L_0x1d5c:
            r0 = r66
            goto L_0x1dc8
        L_0x1d5f:
            r3 = 0
            if (r32 == 0) goto L_0x1dc5
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r15 = new org.telegram.ui.CallLogActivity
            r15.<init>()
            r16 = 0
            r17 = 1
            r18 = 1
            r19 = 0
            r14.presentFragment(r15, r16, r17, r18, r19)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d8b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1daf
        L_0x1d8b:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1daf
        L_0x1d92:
            r1 = 0
            r3 = 0
            r8 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1da8
            r0 = r10[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r4)
        L_0x1da8:
            int r0 = (r18 > r56 ? 1 : (r18 == r56 ? 0 : -1))
            if (r0 != 0) goto L_0x1db3
            r7.openDialogsToSend(r1)
        L_0x1daf:
            r0 = r66
        L_0x1db1:
            r13 = 1
            goto L_0x1dc8
        L_0x1db3:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r18)
            r0.add(r2)
            r7.didSelectDialogs(r3, r0, r3, r1)
            goto L_0x1dc5
        L_0x1dc3:
            r3 = 0
            r8 = 1
        L_0x1dc5:
            r0 = r66
        L_0x1dc7:
            r13 = 0
        L_0x1dc8:
            if (r13 != 0) goto L_0x1e74
            if (r0 != 0) goto L_0x1e74
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1e1e
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1df9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1e5f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1e5f
        L_0x1df9:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1e5f
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r11 == 0) goto L_0x1e12
            r0.setInitialSearchString(r11)
        L_0x1e12:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1e5f
        L_0x1e1e:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1e5f
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1e45
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1e5f
        L_0x1e45:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r11 == 0) goto L_0x1e54
            r0.setInitialSearchString(r11)
        L_0x1e54:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
        L_0x1e5f:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1e74
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
        L_0x1e74:
            if (r24 == 0) goto L_0x1e7c
            r1 = 0
            r0 = r10[r1]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x1e7c:
            if (r21 != 0) goto L_0x1e93
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1e93
            java.lang.String r0 = r65.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x1e93
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r0.dismiss()
        L_0x1e93:
            r1 = r65
            r1.setAction(r3)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$7 */
    public /* synthetic */ void lambda$handleIntent$7$LaunchActivity(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$8 */
    public /* synthetic */ void lambda$handleIntent$8$LaunchActivity(Bundle bundle) {
        lambda$runLinkRequest$42(new CancelAccountDeletionActivity(bundle));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$10 */
    public /* synthetic */ void lambda$handleIntent$10$LaunchActivity(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
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
    /* renamed from: lambda$handleIntent$11 */
    public /* synthetic */ void lambda$handleIntent$11$LaunchActivity(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$12 */
    public /* synthetic */ void lambda$handleIntent$12$LaunchActivity(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleIntent$16 */
    public /* synthetic */ void lambda$handleIntent$16$LaunchActivity(ActionIntroActivity actionIntroActivity, String str) {
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
                        LaunchActivity.lambda$handleIntent$14(AlertDialog.this, this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$handleIntent$14(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
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
                    LaunchActivity.lambda$handleIntent$13(ActionIntroActivity.this, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$handleIntent$13(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(actionIntroActivity, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
    }

    static /* synthetic */ void lambda$handleIntent$17(String str, String str2, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
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
            r4 = 2131627394(0x7f0e0d82, float:1.8882051E38)
            java.lang.String r5 = "selectAlertString"
            if (r1 == 0) goto L_0x003d
            int r1 = r1.size()
            if (r1 == r2) goto L_0x0052
            java.lang.String r1 = "SendContactToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627371(0x7f0e0d6b, float:1.8882005E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
            goto L_0x0052
        L_0x003d:
            java.lang.String r1 = "SendMessagesToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627393(0x7f0e0d81, float:1.888205E38)
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
                LaunchActivity.this.lambda$runCommentRequest$19$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runCommentRequest$19 */
    public /* synthetic */ void lambda$runCommentRequest$19$LaunchActivity(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runCommentRequest$18$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0095 A[SYNTHETIC, Splitter:B:15:0x0095] */
    /* renamed from: lambda$runCommentRequest$18 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runCommentRequest$18$LaunchActivity(org.telegram.tgnet.TLObject r12, int r13, java.lang.Integer r14, org.telegram.tgnet.TLRPC$Chat r15, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r16, java.lang.Integer r17, java.lang.Integer r18, org.telegram.ui.ActionBar.AlertDialog r19) {
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
            r11.lambda$runLinkRequest$42(r2)
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
            r3 = 2131624764(0x7f0e033c, float:1.8876717E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$18$LaunchActivity(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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
            org.telegram.ui.-$$Lambda$LaunchActivity$_0Sc0EVal9NLxNFtohPz8SRKTWQ r6 = new org.telegram.ui.-$$Lambda$LaunchActivity$_0Sc0EVal9NLxNFtohPz8SRKTWQ
            r6.<init>(r11, r12, r0)
            int r11 = r5.sendRequest(r4, r6)
            r1[r2] = r11
            org.telegram.ui.-$$Lambda$LaunchActivity$0iwwK0kDcXHZn9ta-b0nCfxyd2A r11 = new org.telegram.ui.-$$Lambda$LaunchActivity$0iwwK0kDcXHZn9ta-b0nCfxyd2A
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
    /* renamed from: lambda$runImportRequest$21 */
    public /* synthetic */ void lambda$runImportRequest$21$LaunchActivity(Uri uri, int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runImportRequest$20$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
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
    /* renamed from: lambda$runImportRequest$20 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runImportRequest$20$LaunchActivity(org.telegram.tgnet.TLObject r10, android.net.Uri r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runImportRequest$20$LaunchActivity(org.telegram.tgnet.TLObject, android.net.Uri, int, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    static /* synthetic */ void lambda$runImportRequest$22(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x03f7  */
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
            org.telegram.ui.-$$Lambda$LaunchActivity$uXl0H2VGAiOStwVqQzxAMrND3UI r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$uXl0H2VGAiOStwVqQzxAMrND3UI
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
            r4 = 2131626529(0x7f0e0a21, float:1.8880297E38)
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
            r1 = 2131626569(0x7f0e0a49, float:1.8880378E38)
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
            r1 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624430(0x7f0e01ee, float:1.887604E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$cCoYxmM_Y8y9oCZW4rBZi1H7vOg r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$cCoYxmM_Y8y9oCZW4rBZi1H7vOg
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
            r2 = 0
            r13 = 0
            goto L_0x03f2
        L_0x012c:
            r17 = r10
            r10 = 0
            if (r5 == 0) goto L_0x016c
            if (r1 != 0) goto L_0x014f
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.-$$Lambda$LaunchActivity$LDtT7crwD6ADPsWLJuKYeMgGDCI r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$LDtT7crwD6ADPsWLJuKYeMgGDCI
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
            org.telegram.ui.-$$Lambda$LaunchActivity$GwUEI2a_5NpmVIzxztcNAoZcu5A r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$GwUEI2a_5NpmVIzxztcNAoZcu5A
            r3.<init>(r10, r11)
            r1.sendRequest(r0, r3, r2)
        L_0x0169:
            r2 = 0
            goto L_0x03f2
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
            org.telegram.ui.-$$Lambda$LaunchActivity$IgTAe6w8G-5OZ7bACkqzn1uPvlg r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$IgTAe6w8G-5OZ7bACkqzn1uPvlg
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
            org.telegram.ui.-$$Lambda$LaunchActivity$eVFmP_tlCyUeNOlJlkKgWC6mSLQ r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$eVFmP_tlCyUeNOlJlkKgWC6mSLQ
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
            org.telegram.ui.-$$Lambda$LaunchActivity$4tW2XicmLEhP2ugN3S3jnsIgZ4Q r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$4tW2XicmLEhP2ugN3S3jnsIgZ4Q
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
            org.telegram.ui.-$$Lambda$LaunchActivity$VSjE1eN9mZifLPctvdAUSi0JYSI r1 = new org.telegram.ui.-$$Lambda$LaunchActivity$VSjE1eN9mZifLPctvdAUSi0JYSI
            r1.<init>(r11)
            int r0 = r0.sendRequest(r2, r1)
            r7[r13] = r0
            goto L_0x0169
        L_0x029d:
            r1 = r43
            if (r1 == 0) goto L_0x0318
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x02f1
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x02eb }
            int r2 = r0.third_background_color     // Catch:{ Exception -> 0x02eb }
            if (r2 == 0) goto L_0x02c9
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x02eb }
            java.lang.String r4 = "c"
            int r5 = r0.background_color     // Catch:{ Exception -> 0x02eb }
            int r6 = r0.second_background_color     // Catch:{ Exception -> 0x02eb }
            int r0 = r0.fourth_background_color     // Catch:{ Exception -> 0x02eb }
            r26 = r3
            r27 = r4
            r28 = r5
            r29 = r6
            r30 = r2
            r31 = r0
            r26.<init>(r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x02eb }
            goto L_0x02da
        L_0x02c9:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x02eb }
            java.lang.String r2 = "c"
            int r4 = r0.background_color     // Catch:{ Exception -> 0x02eb }
            int r5 = r0.second_background_color     // Catch:{ Exception -> 0x02eb }
            int r0 = r0.rotation     // Catch:{ Exception -> 0x02eb }
            int r0 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r13)     // Catch:{ Exception -> 0x02eb }
            r3.<init>(r2, r4, r5, r0)     // Catch:{ Exception -> 0x02eb }
        L_0x02da:
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x02eb }
            r2 = 0
            r0.<init>(r3, r2, r8)     // Catch:{ Exception -> 0x02e9 }
            org.telegram.ui.-$$Lambda$LaunchActivity$IvZYWCJ5VP43AqVW5sLdG-n7LqU r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$IvZYWCJ5VP43AqVW5sLdG-n7LqU     // Catch:{ Exception -> 0x02e9 }
            r3.<init>(r0)     // Catch:{ Exception -> 0x02e9 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x02e9 }
            goto L_0x02f3
        L_0x02e9:
            r0 = move-exception
            goto L_0x02ed
        L_0x02eb:
            r0 = move-exception
            r2 = 0
        L_0x02ed:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02f2
        L_0x02f1:
            r2 = 0
        L_0x02f2:
            r8 = 0
        L_0x02f3:
            if (r8 != 0) goto L_0x03f2
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r1.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$1Wr0xeuHNbi5DBI4pwEviELoA8Q r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$1Wr0xeuHNbi5DBI4pwEviELoA8Q
            r4.<init>(r11, r1)
            int r0 = r3.sendRequest(r0, r4)
            r7[r13] = r0
            goto L_0x03f2
        L_0x0318:
            r2 = 0
            if (r3 == 0) goto L_0x0343
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
            r7[r13] = r0
            goto L_0x03f3
        L_0x0343:
            if (r12 == 0) goto L_0x03f2
            if (r33 == 0) goto L_0x03f2
            if (r35 == 0) goto L_0x03a0
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r25)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            if (r0 == 0) goto L_0x0369
            r26 = r24
            r27 = r25
            r28 = r11
            r29 = r33
            r30 = r36
            r31 = r35
            r32 = r0
            int r0 = r26.runCommentRequest(r27, r28, r29, r30, r31, r32)
            r7[r13] = r0
            goto L_0x03f2
        L_0x0369:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r0 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            int r3 = r34.intValue()
            r1.channel_id = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r3 = r0.id
            r3.add(r1)
            int r1 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.-$$Lambda$LaunchActivity$6ASbhnUTE94SjiiNtpHcA7AK7bM r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$6ASbhnUTE94SjiiNtpHcA7AK7bM
            r37 = r3
            r38 = r24
            r39 = r7
            r40 = r25
            r41 = r11
            r42 = r33
            r43 = r36
            r44 = r35
            r37.<init>(r39, r40, r41, r42, r43, r44)
            int r0 = r1.sendRequest(r0, r3)
            r7[r13] = r0
            goto L_0x03f2
        L_0x03a0:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r34.intValue()
            java.lang.String r3 = "chat_id"
            r0.putInt(r3, r1)
            int r1 = r33.intValue()
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x03cd
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r8
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03ce
        L_0x03cd:
            r1 = r2
        L_0x03ce:
            if (r1 == 0) goto L_0x03da
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r25)
            boolean r3 = r3.checkCanOpenChat(r0, r1)
            if (r3 == 0) goto L_0x03f2
        L_0x03da:
            org.telegram.ui.-$$Lambda$LaunchActivity$sZhK88tPbXp-nXk1MdN1mdZNj1U r3 = new org.telegram.ui.-$$Lambda$LaunchActivity$sZhK88tPbXp-nXk1MdN1mdZNj1U
            r26 = r3
            r27 = r24
            r28 = r0
            r29 = r34
            r30 = r7
            r31 = r11
            r32 = r1
            r33 = r25
            r26.<init>(r28, r29, r30, r31, r32, r33)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x03f2:
            r15 = r2
        L_0x03f3:
            r0 = r7[r13]
            if (r0 == 0) goto L_0x0404
            org.telegram.ui.-$$Lambda$LaunchActivity$q9Ney3udn2R5Utqvs4W2s6wwTns r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$q9Ney3udn2R5Utqvs4W2s6wwTns
            r0.<init>(r10, r7, r15)
            r11.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r11.showDelayed(r0)     // Catch:{ Exception -> 0x0404 }
        L_0x0404:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$23 */
    public /* synthetic */ void lambda$runLinkRequest$23$LaunchActivity(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, Integer num3, Integer num4, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, String str13, int i2) {
        int i3 = i2;
        if (i3 != i) {
            switchToAccount(i3, true);
        }
        runLinkRequest(i2, str, str2, str3, str4, str5, str6, z, num, num2, num3, num4, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, str13, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$27 */
    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(String str, String str2, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str3, String str4, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runLinkRequest$26$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
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
    /* renamed from: lambda$runLinkRequest$26 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$26$LaunchActivity(org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15, java.lang.String r16, java.lang.String r17, int r18, java.lang.Integer r19, java.lang.Integer r20, java.lang.Integer r21, int[] r22, org.telegram.ui.ActionBar.AlertDialog r23, java.lang.String r24, java.lang.String r25) {
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
            if (r6 != 0) goto L_0x0327
            r6 = r14
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r6 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r6
            r9 = 1
            r10 = 0
            if (r0 != 0) goto L_0x02c9
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r8.actionBarLayout
            if (r7 == 0) goto L_0x02c9
            if (r1 != 0) goto L_0x0021
            if (r2 == 0) goto L_0x0035
        L_0x0021:
            if (r1 == 0) goto L_0x002b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r6.users
            boolean r7 = r7.isEmpty()
            if (r7 == 0) goto L_0x0035
        L_0x002b:
            if (r2 == 0) goto L_0x02c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r6.chats
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x02c9
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
            if (r0 == 0) goto L_0x02ae
            r4 = r23
        L_0x0080:
            r9 = 0
            goto L_0x031c
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
            r0 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.String r4 = "SendGameToText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertString"
            r2.putString(r4, r0)
            r0 = 2131627376(0x7f0e0d70, float:1.8882015E38)
            java.lang.String r4 = "SendGameToGroupText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertStringGroup"
            r2.putString(r4, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.-$$Lambda$LaunchActivity$bmhEVoNV8n5SdaLhLO8aMT94QIQ r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$bmhEVoNV8n5SdaLhLO8aMT94QIQ
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
            goto L_0x02ae
        L_0x017a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r9, r10)
            goto L_0x02ae
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
            org.telegram.ui.-$$Lambda$LaunchActivity$U1mQS2TWtTEmqPgk9hbZVUJ4DZk r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$U1mQS2TWtTEmqPgk9hbZVUJ4DZk
            r2.<init>(r3, r1, r4)
            r0.setDelegate(r2)
            r13.lambda$runLinkRequest$42(r0)
            goto L_0x02ae
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
            r2 = 2131624584(0x7f0e0288, float:1.8876352E38)
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
            goto L_0x0247
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
        L_0x0247:
            long r11 = (long) r4
            if (r5 == 0) goto L_0x0265
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            int r4 = r4.size()
            if (r4 <= 0) goto L_0x0265
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r6.users
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0265
            java.lang.String r4 = "botUser"
            r0.putString(r4, r5)
            r4 = 1
            goto L_0x0266
        L_0x0265:
            r4 = 0
        L_0x0266:
            if (r19 == 0) goto L_0x0271
            int r6 = r19.intValue()
            java.lang.String r7 = "message_id"
            r0.putInt(r7, r6)
        L_0x0271:
            if (r2 == 0) goto L_0x0278
            java.lang.String r6 = "voicechat"
            r0.putString(r6, r2)
        L_0x0278:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x028f
            if (r2 != 0) goto L_0x028f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
        L_0x028f:
            if (r1 == 0) goto L_0x029b
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r18)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x02ae
        L_0x029b:
            if (r4 == 0) goto L_0x02b1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x02b1
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r6 = r1.getDialogId()
            int r2 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x02b1
            r1.setBotUser(r5)
        L_0x02ae:
            r4 = r23
            goto L_0x031c
        L_0x02b1:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r18)
            if (r19 != 0) goto L_0x02b9
            r2 = 0
            goto L_0x02bd
        L_0x02b9:
            int r2 = r19.intValue()
        L_0x02bd:
            org.telegram.ui.LaunchActivity$10 r3 = new org.telegram.ui.LaunchActivity$10
            r4 = r23
            r3.<init>(r4, r0)
            r1.ensureMessagesLoaded(r11, r2, r3)
            goto L_0x0080
        L_0x02c9:
            r4 = r23
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0318 }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x0318 }
            if (r1 != 0) goto L_0x031c
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0318 }
            int r2 = r1.size()     // Catch:{ Exception -> 0x0318 }
            int r2 = r2 - r9
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0318 }
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1     // Catch:{ Exception -> 0x0318 }
            if (r0 == 0) goto L_0x0303
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x0318 }
            if (r0 == 0) goto L_0x0303
            java.lang.String r2 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r2)     // Catch:{ Exception -> 0x0318 }
            if (r0 == 0) goto L_0x0303
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r1)     // Catch:{ Exception -> 0x0318 }
            java.lang.String r1 = "FloodWait"
            r2 = 2131625574(0x7f0e0666, float:1.887836E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0318 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0318 }
            r0.show()     // Catch:{ Exception -> 0x0318 }
            goto L_0x031c
        L_0x0303:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r1)     // Catch:{ Exception -> 0x0318 }
            java.lang.String r1 = "NoUsernameFound"
            r2 = 2131626340(0x7f0e0964, float:1.8879913E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0318 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0318 }
            r0.show()     // Catch:{ Exception -> 0x0318 }
            goto L_0x031c
        L_0x0318:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x031c:
            if (r9 == 0) goto L_0x0327
            r23.dismiss()     // Catch:{ Exception -> 0x0322 }
            goto L_0x0327
        L_0x0322:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0327:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$26$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$24 */
    public /* synthetic */ void lambda$runLinkRequest$24$LaunchActivity(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    /* renamed from: lambda$runLinkRequest$25 */
    public /* synthetic */ void lambda$runLinkRequest$25$LaunchActivity(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
                LaunchActivity.this.lambda$runLinkRequest$29$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
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
    /* renamed from: lambda$runLinkRequest$29 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
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
            org.telegram.ui.-$$Lambda$LaunchActivity$zXIwSXyew7gskscyf_lRZ-DRXJw r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$zXIwSXyew7gskscyf_lRZ-DRXJw
            r10.<init>(r6)
            r13.setOnCancelListener(r10)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r8.chat
            int r11 = r11.id
            int r11 = -r11
            long r11 = (long) r11
            org.telegram.ui.LaunchActivity$11 r0 = new org.telegram.ui.LaunchActivity$11
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
            r10 = 2131625574(0x7f0e0666, float:1.887836E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x010a
        L_0x00db:
            java.lang.String r10 = r10.text
            java.lang.String r12 = "INVITE_HASH_EXPIRED"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00fe
            r10 = 2131625461(0x7f0e05f5, float:1.887813E38)
            java.lang.String r12 = "ExpiredLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setTitle(r10)
            r10 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.String r12 = "InviteExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x010a
        L_0x00fe:
            r10 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x010a:
            r10 = 2131626529(0x7f0e0a21, float:1.8880297E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$29$LaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$runLinkRequest$28(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
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
                LaunchActivity.this.lambda$runLinkRequest$31$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$31 */
    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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
                    LaunchActivity.this.lambda$runLinkRequest$35$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
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
                LaunchActivity.this.lambda$runLinkRequest$36$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$35 */
    public /* synthetic */ void lambda$runLinkRequest$35$LaunchActivity(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runLinkRequest$34$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$34 */
    public /* synthetic */ void lambda$runLinkRequest$34$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
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
    /* renamed from: lambda$runLinkRequest$36 */
    public /* synthetic */ void lambda$runLinkRequest$36$LaunchActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runLinkRequest$38$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$38 */
    public /* synthetic */ void lambda$runLinkRequest$38$LaunchActivity(AlertDialog alertDialog, TLObject tLObject) {
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
                LaunchActivity.this.lambda$runLinkRequest$40$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$40 */
    public /* synthetic */ void lambda$runLinkRequest$40$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runLinkRequest$43$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$runLinkRequest$43 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$43$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC$TL_wallPaper r13, org.telegram.tgnet.TLRPC$TL_error r14) {
        /*
            r10 = this;
            r11.dismiss()     // Catch:{ Exception -> 0x0004 }
            goto L_0x0008
        L_0x0004:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x0008:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r11 == 0) goto L_0x004e
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r12
            boolean r11 = r12.pattern
            if (r11 == 0) goto L_0x003a
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r11 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r12.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r14 = r13.settings
            int r2 = r14.background_color
            int r3 = r14.second_background_color
            int r4 = r14.third_background_color
            int r5 = r14.fourth_background_color
            int r14 = r14.rotation
            r0 = 0
            int r6 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r14, r0)
            org.telegram.tgnet.TLRPC$WallPaperSettings r14 = r13.settings
            int r0 = r14.intensity
            float r0 = (float) r0
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            float r7 = r0 / r7
            boolean r8 = r14.motion
            r9 = 0
            r0 = r11
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r11.pattern = r12
            r12 = r11
        L_0x003a:
            org.telegram.ui.ThemePreviewActivity r11 = new org.telegram.ui.ThemePreviewActivity
            r14 = 0
            r0 = 1
            r11.<init>(r12, r14, r0)
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r13.settings
            boolean r13 = r12.blur
            boolean r12 = r12.motion
            r11.setInitialModes(r13, r12)
            r10.lambda$runLinkRequest$42(r11)
            goto L_0x0074
        L_0x004e:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 2131625338(0x7f0e057a, float:1.8877881E38)
            java.lang.String r13 = "ErrorOccurred"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.append(r12)
            java.lang.String r12 = "\n"
            r11.append(r12)
            java.lang.String r12 = r14.text
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r11)
            r10.showAlertDialog(r11)
        L_0x0074:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$43$LaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
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
                LaunchActivity.this.lambda$runLinkRequest$46$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086 A[SYNTHETIC, Splitter:B:27:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$runLinkRequest$46 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$46$LaunchActivity(org.telegram.tgnet.TLObject r5, org.telegram.ui.ActionBar.AlertDialog r6, org.telegram.tgnet.TLRPC$TL_error r7) {
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
            r5 = 2131627719(0x7f0e0ec7, float:1.888271E38)
            java.lang.String r6 = "Theme"
            if (r0 != r1) goto L_0x00aa
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627739(0x7f0e0edb, float:1.888275E38)
            java.lang.String r7 = "ThemeNotSupported"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
            goto L_0x00be
        L_0x00aa:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131627738(0x7f0e0eda, float:1.8882749E38)
            java.lang.String r7 = "ThemeNotFound"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r4, r5, r6)
            r4.showAlertDialog(r5)
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$46$LaunchActivity(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
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
                LaunchActivity.this.lambda$runLinkRequest$48$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* renamed from: lambda$runLinkRequest$48 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$48$LaunchActivity(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
            r0 = 2131625983(0x7f0e07ff, float:1.887919E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$48$LaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
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
                    LaunchActivity.this.lambda$runLinkRequest$51$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$51 */
    public /* synthetic */ void lambda$runLinkRequest$51$LaunchActivity(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$runLinkRequest$50$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runLinkRequest$50 */
    public /* synthetic */ void lambda$runLinkRequest$50$LaunchActivity(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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
            r13 = 2131627169(0x7f0e0ca1, float:1.8881595E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131627280(0x7f0e0d10, float:1.888182E38)
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

    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0124 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0125  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAppUpdateViews(boolean r13) {
        /*
            r12 = this;
            android.widget.FrameLayout r0 = r12.updateLayout
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            r1 = 1110441984(0x42300000, float:44.0)
            r2 = 0
            r3 = 180(0xb4, double:8.9E-322)
            r5 = 0
            if (r0 == 0) goto L_0x0160
            android.widget.TextView r0 = r12.updateSizeTextView
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            int r6 = r6.size
            long r6 = (long) r6
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.formatFileSize(r6)
            r0.setText(r6)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r0 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.lang.String r0 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            r7 = 1
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r7)
            boolean r6 = r6.exists()
            r8 = 0
            if (r6 == 0) goto L_0x0050
            org.telegram.ui.Components.RadialProgress2 r0 = r12.updateLayoutIcon
            r6 = 15
            r0.setIcon(r6, r7, r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.updateTextView
            r6 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r9 = "AppUpdateNow"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r0.setText(r6)
        L_0x004e:
            r0 = 0
            goto L_0x00a3
        L_0x0050:
            int r6 = r12.currentAccount
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)
            boolean r6 = r6.isLoadingFile(r0)
            if (r6 == 0) goto L_0x008e
            org.telegram.ui.Components.RadialProgress2 r6 = r12.updateLayoutIcon
            r9 = 3
            r6.setIcon(r9, r7, r13)
            org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r6.getFileProgress(r0)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r12.updateTextView
            r9 = 2131624287(0x7f0e015f, float:1.887575E38)
            java.lang.Object[] r10 = new java.lang.Object[r7]
            if (r0 == 0) goto L_0x0078
            float r0 = r0.floatValue()
            goto L_0x0079
        L_0x0078:
            r0 = 0
        L_0x0079:
            r11 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r11
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r10[r5] = r0
            java.lang.String r0 = "AppUpdateDownloading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r9, r10)
            r6.setText(r0)
            goto L_0x004e
        L_0x008e:
            org.telegram.ui.Components.RadialProgress2 r0 = r12.updateLayoutIcon
            r6 = 2
            r0.setIcon(r6, r7, r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.updateTextView
            r6 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r9 = "AppUpdate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r0.setText(r6)
            r0 = 1
        L_0x00a3:
            if (r0 == 0) goto L_0x00e0
            android.widget.TextView r0 = r12.updateSizeTextView
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x011c
            r0 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x00d0
            android.widget.TextView r6 = r12.updateSizeTextView
            r6.setTag(r2)
            android.widget.TextView r6 = r12.updateSizeTextView
            android.view.ViewPropertyAnimator r6 = r6.animate()
            android.view.ViewPropertyAnimator r6 = r6.alpha(r0)
            android.view.ViewPropertyAnimator r6 = r6.scaleX(r0)
            android.view.ViewPropertyAnimator r0 = r6.scaleY(r0)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r3)
            r0.start()
            goto L_0x011c
        L_0x00d0:
            android.widget.TextView r6 = r12.updateSizeTextView
            r6.setAlpha(r0)
            android.widget.TextView r6 = r12.updateSizeTextView
            r6.setScaleX(r0)
            android.widget.TextView r6 = r12.updateSizeTextView
            r6.setScaleY(r0)
            goto L_0x011c
        L_0x00e0:
            android.widget.TextView r0 = r12.updateSizeTextView
            java.lang.Object r0 = r0.getTag()
            if (r0 != 0) goto L_0x011c
            if (r13 == 0) goto L_0x010d
            android.widget.TextView r0 = r12.updateSizeTextView
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            r0.setTag(r6)
            android.widget.TextView r0 = r12.updateSizeTextView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.alpha(r8)
            android.view.ViewPropertyAnimator r0 = r0.scaleX(r8)
            android.view.ViewPropertyAnimator r0 = r0.scaleY(r8)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r3)
            r0.start()
            goto L_0x011c
        L_0x010d:
            android.widget.TextView r0 = r12.updateSizeTextView
            r0.setAlpha(r8)
            android.widget.TextView r0 = r12.updateSizeTextView
            r0.setScaleX(r8)
            android.widget.TextView r0 = r12.updateSizeTextView
            r0.setScaleY(r8)
        L_0x011c:
            android.widget.FrameLayout r0 = r12.updateLayout
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0125
            return
        L_0x0125:
            android.widget.FrameLayout r0 = r12.updateLayout
            r0.setVisibility(r5)
            android.widget.FrameLayout r0 = r12.updateLayout
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            r0.setTag(r6)
            if (r13 == 0) goto L_0x0151
            android.widget.FrameLayout r13 = r12.updateLayout
            android.view.ViewPropertyAnimator r13 = r13.animate()
            android.view.ViewPropertyAnimator r13 = r13.translationY(r8)
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r13 = r13.setInterpolator(r0)
            android.view.ViewPropertyAnimator r13 = r13.setListener(r2)
            android.view.ViewPropertyAnimator r13 = r13.setDuration(r3)
            r13.start()
            goto L_0x0156
        L_0x0151:
            android.widget.FrameLayout r13 = r12.updateLayout
            r13.setTranslationY(r8)
        L_0x0156:
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r13.setPadding(r5, r5, r5, r0)
            goto L_0x01ab
        L_0x0160:
            android.widget.FrameLayout r0 = r12.updateLayout
            java.lang.Object r0 = r0.getTag()
            if (r0 != 0) goto L_0x0169
            return
        L_0x0169:
            android.widget.FrameLayout r0 = r12.updateLayout
            r0.setTag(r2)
            if (r13 == 0) goto L_0x0196
            android.widget.FrameLayout r13 = r12.updateLayout
            android.view.ViewPropertyAnimator r13 = r13.animate()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            android.view.ViewPropertyAnimator r13 = r13.translationY(r0)
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r13 = r13.setInterpolator(r0)
            org.telegram.ui.LaunchActivity$12 r0 = new org.telegram.ui.LaunchActivity$12
            r0.<init>()
            android.view.ViewPropertyAnimator r13 = r13.setListener(r0)
            android.view.ViewPropertyAnimator r13 = r13.setDuration(r3)
            r13.start()
            goto L_0x01a6
        L_0x0196:
            android.widget.FrameLayout r13 = r12.updateLayout
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            r13.setTranslationY(r0)
            android.widget.FrameLayout r13 = r12.updateLayout
            r0 = 4
            r13.setVisibility(r0)
        L_0x01a6:
            org.telegram.ui.Components.RecyclerListView r13 = r12.sideMenu
            r13.setPadding(r5, r5, r5, r5)
        L_0x01ab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.updateAppUpdateViews(boolean):void");
    }

    public void checkAppUpdate(boolean z) {
        if (!z && BuildVars.DEBUG_VERSION) {
            return;
        }
        if (!z && !BuildVars.CHECK_UPDATES) {
            return;
        }
        if (z || Math.abs(System.currentTimeMillis() - SharedConfig.lastUpdateCheckTime) >= ((long) (MessagesController.getInstance(0).updateCheckDelay * 1000))) {
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
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = new TLRPC$TL_help_appUpdate();
        tLRPC$TL_help_appUpdate.id = 1;
        tLRPC$TL_help_appUpdate.version = "7.8.0";
        tLRPC$TL_help_appUpdate.text = "some text";
        TLRPC$TL_messageEntityBold tLRPC$TL_messageEntityBold = new TLRPC$TL_messageEntityBold();
        tLRPC$TL_messageEntityBold.offset = 0;
        tLRPC$TL_messageEntityBold.length = 4;
        tLRPC$TL_help_appUpdate.entities.add(tLRPC$TL_messageEntityBold);
        TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
        tLRPC$TL_help_appUpdate.document = tLRPC$TL_document;
        tLRPC$TL_document.size = 1048576;
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_help_appUpdate, i) {
            public final /* synthetic */ TLRPC$TL_help_appUpdate f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$checkAppUpdate$54$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkAppUpdate$54 */
    public /* synthetic */ void lambda$checkAppUpdate$54$LaunchActivity(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
        SharedConfig.setNewAppVersionAvailable(tLRPC$TL_help_appUpdate);
        if (tLRPC$TL_help_appUpdate.can_not_skip) {
            showUpdateActivity(i, tLRPC$TL_help_appUpdate, false);
        } else {
            this.drawerLayoutAdapter.notifyDataSetChanged();
            new UpdateAppAlertDialog(this, tLRPC$TL_help_appUpdate, i).show();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable, new Object[0]);
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
                    LaunchActivity.this.lambda$didSelectDialogs$57$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, i);
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
                        LaunchActivity.this.lambda$didSelectDialogs$58$LaunchActivity(this.f$1, this.f$2, this.f$3, tLRPC$User, z, i);
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
    /* renamed from: lambda$didSelectDialogs$57 */
    public /* synthetic */ void lambda$didSelectDialogs$57$LaunchActivity(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, int i2) {
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
    /* renamed from: lambda$didSelectDialogs$58 */
    public /* synthetic */ void lambda$didSelectDialogs$58$LaunchActivity(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
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
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
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
        VoIPService sharedInstance;
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
            if (i == 520) {
                if (i2 == -1 && (sharedInstance = VoIPService.getSharedInstance()) != null && sharedInstance.groupCall != null) {
                    VideoCapturerDevice.mediaProjectionPermissionResultData = intent;
                    sharedInstance.createCaptureDevice(true);
                }
            } else if (i == 140) {
                LocationController instance = LocationController.getInstance(this.currentAccount);
                if (i2 == -1) {
                    z = true;
                }
                instance.startFusedLocationRequest(z);
            } else {
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
                        LaunchActivity.this.lambda$onActivityResult$59$LaunchActivity();
                    }
                }, 200);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResult$59 */
    public /* synthetic */ void lambda$onActivityResult$59$LaunchActivity() {
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
        if (i == 104) {
            if (z) {
                GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                if (groupCallActivity != null) {
                    groupCallActivity.enableCamera();
                }
            } else {
                showPermissionErrorAlert(LocaleController.getString("VoipNeedCameraPermission", NUM));
            }
        } else if (i == 4) {
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
                LaunchActivity.this.lambda$showPermissionErrorAlert$60$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPermissionErrorAlert$60 */
    public /* synthetic */ void lambda$showPermissionErrorAlert$60$LaunchActivity(DialogInterface dialogInterface, int i) {
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
                LaunchActivity.lambda$onPause$61(this.f$0);
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

    static /* synthetic */ void lambda$onPause$61(int i) {
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
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$HXc4ZnqdklO73YToGu956puWpIc.INSTANCE);
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
        } else {
            TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = SharedConfig.pendingAppUpdate;
            if (tLRPC$TL_help_appUpdate != null && tLRPC$TL_help_appUpdate.can_not_skip) {
                showUpdateActivity(UserConfig.selectedAccount, SharedConfig.pendingAppUpdate, true);
            }
        }
        checkAppUpdate(false);
        if (Build.VERSION.SDK_INT >= 23) {
            ApplicationLoader.canDrawOverlays = Settings.canDrawOverlays(this);
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onResume();
        }
    }

    static /* synthetic */ void lambda$onResume$62() {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v31, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v39, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v53, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v56, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v59, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v66, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v73, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v80, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v85, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v99, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v101, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v126, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v135, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v149, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v151, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v168, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v173, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX WARNING: type inference failed for: r5v0 */
    /* JADX WARNING: type inference failed for: r5v2 */
    /* JADX WARNING: type inference failed for: r5v7, types: [int] */
    /* JADX WARNING: type inference failed for: r5v14 */
    /* JADX WARNING: type inference failed for: r5v23 */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0273, code lost:
        if (((org.telegram.ui.ProfileActivity) r2.get(r2.size() - 1)).isSettings() == false) goto L_0x0277;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x04f6  */
    /* JADX WARNING: Removed duplicated region for block: B:342:? A[RETURN, SYNTHETIC] */
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
            goto L_0x07e7
        L_0x0011:
            int r4 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r5 = 0
            if (r0 != r4) goto L_0x0022
            r0 = r3[r5]
            if (r0 == r1) goto L_0x07e7
            r16.onFinish()
            r16.finish()
            goto L_0x07e7
        L_0x0022:
            int r4 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r4) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r3 = r1.currentConnectionState
            if (r3 == r0) goto L_0x07e7
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
            goto L_0x07e7
        L_0x0051:
            int r4 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r4) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r1.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x07e7
        L_0x005c:
            int r4 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r7 = "Cancel"
            r8 = 5
            r9 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r10 = "AppName"
            r11 = 4
            r12 = 3
            r13 = 2131626529(0x7f0e0a21, float:1.8880297E38)
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
            r5 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            java.lang.String r9 = "MoreInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            org.telegram.ui.-$$Lambda$LaunchActivity$77YGQwUJcVUjkCrpLNEsuuIraao r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$77YGQwUJcVUjkCrpLNEsuuIraao
            r9.<init>(r2)
            r4.setNegativeButton(r5, r9)
        L_0x00c5:
            int r2 = r0.intValue()
            if (r2 != r8) goto L_0x00e1
            r0 = 2131626345(0x7f0e0969, float:1.8879924E38)
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
            r0 = 2131626343(0x7f0e0967, float:1.887992E38)
            java.lang.String r3 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r4.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r4.setPositiveButton(r0, r2)
            goto L_0x0183
        L_0x00fd:
            int r5 = r0.intValue()
            if (r5 != r6) goto L_0x0117
            r0 = 2131626344(0x7f0e0968, float:1.8879922E38)
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
            r0 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r2 = 0
            r4.setPositiveButton(r0, r2)
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r2 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.-$$Lambda$LaunchActivity$M5gIw-0ptAm9VJcoD94SqfEm_t4 r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$M5gIw-0ptAm9VJcoD94SqfEm_t4
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
            r0 = 2131627074(0x7f0e0CLASSNAME, float:1.8881402E38)
            java.lang.String r2 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r4.setTitle(r0)
            r0 = 2131627892(0x7f0e0var_, float:1.8883061E38)
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
            if (r0 != 0) goto L_0x07e7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r4.create()
            r0.showDialog(r2)
            goto L_0x07e7
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
            r4 = 2131627473(0x7f0e0dd1, float:1.8882211E38)
            java.lang.String r5 = "ShareYouLocationUnableManually"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.-$$Lambda$LaunchActivity$vaYL171Hkz3S8DehEoPID28LotY r5 = new org.telegram.ui.-$$Lambda$LaunchActivity$vaYL171Hkz3S8DehEoPID28LotY
            r5.<init>(r0, r2)
            r3.setNegativeButton(r4, r5)
            r0 = 2131627472(0x7f0e0dd0, float:1.888221E38)
            java.lang.String r2 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07e7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r6
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r2 = r3.create()
            r0.showDialog(r2)
            goto L_0x07e7
        L_0x01f9:
            int r4 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r4) goto L_0x020c
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x07e7
            android.view.View r0 = r0.getChildAt(r5)
            if (r0 == 0) goto L_0x07e7
            r0.invalidate()
            goto L_0x07e7
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
            goto L_0x07e7
        L_0x0227:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07e7
        L_0x022d:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x07e7
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x023c }
            r0.clearFlags(r2)     // Catch:{ Exception -> 0x023c }
            goto L_0x07e7
        L_0x023c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07e7
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
            goto L_0x07e7
        L_0x027c:
            int r4 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r4) goto L_0x0285
            r1.showLanguageAlert(r5)
            goto L_0x07e7
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
            goto L_0x07e7
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
            r9 = 2131627860(0x7f0e0var_, float:1.8882996E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setTitle(r9)
            r9 = 2131627859(0x7f0e0var_, float:1.8882994E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY r10 = new org.telegram.ui.-$$Lambda$LaunchActivity$nwcfu6i_mbCrr2wexylSTSY5gMY
            r10.<init>(r2, r0, r4, r3)
            r8.setPositiveButton(r9, r10)
            r9 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$u6y_ASGln28gOCl_6a9-Nj_3XHo r9 = new org.telegram.ui.-$$Lambda$LaunchActivity$u6y_ASGln28gOCl_6a9-Nj_3XHo
            r9.<init>(r2, r0, r4, r3)
            r8.setNegativeButton(r7, r9)
            org.telegram.ui.-$$Lambda$LaunchActivity$cMO6m30YRbxgAmxNEQk7KnvblYA r7 = new org.telegram.ui.-$$Lambda$LaunchActivity$cMO6m30YRbxgAmxNEQk7KnvblYA
            r7.<init>(r2, r0, r4, r3)
            r8.setOnBackButtonListener(r7)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r6.showDialog(r0)
            r0.setCanceledOnTouchOutside(r5)
            goto L_0x07e7
        L_0x033e:
            return
        L_0x033f:
            int r2 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r4 = 21
            if (r0 != r2) goto L_0x039f
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
            goto L_0x07e7
        L_0x039f:
            int r2 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r2) goto L_0x0502
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x04d6
            r0 = r3[r15]
            if (r0 == 0) goto L_0x04d6
            android.widget.ImageView r0 = r1.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03b4
            return
        L_0x03b4:
            r0 = r3[r15]     // Catch:{ all -> 0x04be }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x04be }
            r2 = r3[r11]     // Catch:{ all -> 0x04be }
            java.lang.Boolean r2 = (java.lang.Boolean) r2     // Catch:{ all -> 0x04be }
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x04be }
            r4 = r3[r8]     // Catch:{ all -> 0x04be }
            org.telegram.ui.Components.RLottieImageView r4 = (org.telegram.ui.Components.RLottieImageView) r4     // Catch:{ all -> 0x04be }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            int r7 = r7.getMeasuredWidth()     // Catch:{ all -> 0x04be }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            int r8 = r8.getMeasuredHeight()     // Catch:{ all -> 0x04be }
            if (r2 != 0) goto L_0x03d5
            r4.setVisibility(r11)     // Catch:{ all -> 0x04be }
        L_0x03d5:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            int r9 = r9.getMeasuredWidth()     // Catch:{ all -> 0x04be }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            int r10 = r10.getMeasuredHeight()     // Catch:{ all -> 0x04be }
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x04be }
            android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r9, r10, r11)     // Catch:{ all -> 0x04be }
            android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x04be }
            r10.<init>(r9)     // Catch:{ all -> 0x04be }
            java.util.HashMap r11 = new java.util.HashMap     // Catch:{ all -> 0x04be }
            r11.<init>()     // Catch:{ all -> 0x04be }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            r1.invalidateCachedViews(r11)     // Catch:{ all -> 0x04be }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            r11.draw(r10)     // Catch:{ all -> 0x04be }
            android.widget.FrameLayout r10 = r1.frameLayout     // Catch:{ all -> 0x04be }
            android.widget.ImageView r11 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
            r10.removeView(r11)     // Catch:{ all -> 0x04be }
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r2 == 0) goto L_0x041a
            android.widget.FrameLayout r13 = r1.frameLayout     // Catch:{ all -> 0x04be }
            android.widget.ImageView r14 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x04be }
            r13.addView(r14, r5, r10)     // Catch:{ all -> 0x04be }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04be }
            r11 = 8
            r10.setVisibility(r11)     // Catch:{ all -> 0x04be }
            goto L_0x044b
        L_0x041a:
            android.widget.FrameLayout r13 = r1.frameLayout     // Catch:{ all -> 0x04be }
            android.widget.ImageView r14 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x04be }
            r13.addView(r14, r6, r10)     // Catch:{ all -> 0x04be }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04be }
            r11 = r0[r5]     // Catch:{ all -> 0x04be }
            r13 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x04be }
            int r11 = r11 - r14
            float r11 = (float) r11     // Catch:{ all -> 0x04be }
            r10.setTranslationX(r11)     // Catch:{ all -> 0x04be }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04be }
            r11 = r0[r6]     // Catch:{ all -> 0x04be }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x04be }
            int r11 = r11 - r13
            float r11 = (float) r11     // Catch:{ all -> 0x04be }
            r10.setTranslationY(r11)     // Catch:{ all -> 0x04be }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04be }
            r10.setVisibility(r5)     // Catch:{ all -> 0x04be }
            android.view.View r10 = r1.themeSwitchSunView     // Catch:{ all -> 0x04be }
            r10.invalidate()     // Catch:{ all -> 0x04be }
        L_0x044b:
            android.widget.ImageView r10 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
            r10.setImageBitmap(r9)     // Catch:{ all -> 0x04be }
            android.widget.ImageView r9 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
            r9.setVisibility(r5)     // Catch:{ all -> 0x04be }
            org.telegram.ui.Components.RLottieDrawable r9 = r4.getAnimatedDrawable()     // Catch:{ all -> 0x04be }
            r1.themeSwitchSunDrawable = r9     // Catch:{ all -> 0x04be }
            r9 = r0[r5]     // Catch:{ all -> 0x04be }
            int r9 = r7 - r9
            r10 = r0[r5]     // Catch:{ all -> 0x04be }
            int r7 = r7 - r10
            int r9 = r9 * r7
            r7 = r0[r6]     // Catch:{ all -> 0x04be }
            int r7 = r8 - r7
            r10 = r0[r6]     // Catch:{ all -> 0x04be }
            int r10 = r8 - r10
            int r7 = r7 * r10
            int r9 = r9 + r7
            double r9 = (double) r9     // Catch:{ all -> 0x04be }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x04be }
            r7 = r0[r5]     // Catch:{ all -> 0x04be }
            r11 = r0[r5]     // Catch:{ all -> 0x04be }
            int r7 = r7 * r11
            r11 = r0[r6]     // Catch:{ all -> 0x04be }
            int r11 = r8 - r11
            r13 = r0[r6]     // Catch:{ all -> 0x04be }
            int r8 = r8 - r13
            int r11 = r11 * r8
            int r7 = r7 + r11
            double r7 = (double) r7     // Catch:{ all -> 0x04be }
            double r7 = java.lang.Math.sqrt(r7)     // Catch:{ all -> 0x04be }
            double r7 = java.lang.Math.max(r9, r7)     // Catch:{ all -> 0x04be }
            float r7 = (float) r7     // Catch:{ all -> 0x04be }
            if (r2 == 0) goto L_0x0493
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r1.drawerLayoutContainer     // Catch:{ all -> 0x04be }
            goto L_0x0495
        L_0x0493:
            android.widget.ImageView r8 = r1.themeSwitchImageView     // Catch:{ all -> 0x04be }
        L_0x0495:
            r9 = r0[r5]     // Catch:{ all -> 0x04be }
            r0 = r0[r6]     // Catch:{ all -> 0x04be }
            r10 = 0
            if (r2 == 0) goto L_0x049e
            r11 = 0
            goto L_0x049f
        L_0x049e:
            r11 = r7
        L_0x049f:
            if (r2 == 0) goto L_0x04a2
            goto L_0x04a3
        L_0x04a2:
            r7 = 0
        L_0x04a3:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r8, r9, r0, r11, r7)     // Catch:{ all -> 0x04be }
            r7 = 400(0x190, double:1.976E-321)
            r0.setDuration(r7)     // Catch:{ all -> 0x04be }
            android.view.animation.Interpolator r7 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x04be }
            r0.setInterpolator(r7)     // Catch:{ all -> 0x04be }
            org.telegram.ui.LaunchActivity$13 r7 = new org.telegram.ui.LaunchActivity$13     // Catch:{ all -> 0x04be }
            r7.<init>(r2, r4)     // Catch:{ all -> 0x04be }
            r0.addListener(r7)     // Catch:{ all -> 0x04be }
            r0.start()     // Catch:{ all -> 0x04be }
            r0 = 1
            goto L_0x04d7
        L_0x04be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04d2 }
            r2 = 0
            r0.setImageDrawable(r2)     // Catch:{ Exception -> 0x04d2 }
            android.widget.FrameLayout r0 = r1.frameLayout     // Catch:{ Exception -> 0x04d2 }
            android.widget.ImageView r2 = r1.themeSwitchImageView     // Catch:{ Exception -> 0x04d2 }
            r0.removeView(r2)     // Catch:{ Exception -> 0x04d2 }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r5     // Catch:{ Exception -> 0x04d2 }
            goto L_0x04d6
        L_0x04d2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04d6:
            r0 = 0
        L_0x04d7:
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
            if (r5 == 0) goto L_0x07e7
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.layersActionBarLayout
            r5.animateThemedValues(r2, r3, r4, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r1.rightActionBarLayout
            r5.animateThemedValues(r2, r3, r4, r0)
            goto L_0x07e7
        L_0x0502:
            int r2 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r2) goto L_0x0533
            org.telegram.ui.Components.RecyclerListView r0 = r1.sideMenu
            if (r0 == 0) goto L_0x07e7
            r2 = r3[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r0 = r0.getChildCount()
        L_0x0512:
            if (r5 >= r0) goto L_0x07e7
            org.telegram.ui.Components.RecyclerListView r3 = r1.sideMenu
            android.view.View r3 = r3.getChildAt(r5)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r4 == 0) goto L_0x0530
            r4 = r3
            org.telegram.ui.Cells.DrawerUserCell r4 = (org.telegram.ui.Cells.DrawerUserCell) r4
            int r4 = r4.getAccountNumber()
            int r6 = r2.intValue()
            if (r4 != r6) goto L_0x0530
            r3.invalidate()
            goto L_0x07e7
        L_0x0530:
            int r5 = r5 + 1
            goto L_0x0512
        L_0x0533:
            int r2 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r2) goto L_0x0542
            r0 = r3[r5]     // Catch:{ all -> 0x07e7 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x07e7 }
            r2 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r1, r2)     // Catch:{ all -> 0x07e7 }
            goto L_0x07e7
        L_0x0542:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r0 != r2) goto L_0x0618
            r0 = r3[r5]
            java.lang.String r0 = (java.lang.String) r0
            boolean r2 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r2 == 0) goto L_0x0561
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.lang.String r2 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0561
            r1.updateAppUpdateViews(r6)
        L_0x0561:
            java.lang.String r2 = r1.loadingThemeFileName
            if (r2 == 0) goto L_0x05e7
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x07e7
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
            if (r2 == 0) goto L_0x05e2
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x05cb
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x05cb
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r2.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r2.account
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$LaunchActivity$25bsFxkHHmY4JcEDvGYxq0SEzHg r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$25bsFxkHHmY4JcEDvGYxq0SEzHg
            r4.<init>(r2)
            r3.sendRequest(r0, r4)
            return
        L_0x05cb:
            org.telegram.tgnet.TLRPC$TL_theme r2 = r1.loadingTheme
            java.lang.String r3 = r2.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r3, r2, r6)
            if (r8 == 0) goto L_0x05e2
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r9 = 1
            r10 = 0
            r11 = 0
            r12 = 0
            r7 = r0
            r7.<init>(r8, r9, r10, r11, r12)
            r1.lambda$runLinkRequest$42(r0)
        L_0x05e2:
            r16.onThemeLoadFinish()
            goto L_0x07e7
        L_0x05e7:
            java.lang.String r2 = r1.loadingThemeWallpaperName
            if (r2 == 0) goto L_0x07e7
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x07e7
            r2 = 0
            r1.loadingThemeWallpaperName = r2
            r0 = r3[r6]
            java.io.File r0 = (java.io.File) r0
            boolean r2 = r1.loadingThemeAccent
            if (r2 == 0) goto L_0x060a
            org.telegram.tgnet.TLRPC$TL_theme r0 = r1.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r1.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.loadingThemeInfo
            r1.openThemeAccentPreview(r0, r2, r3)
            r16.onThemeLoadFinish()
            goto L_0x07e7
        L_0x060a:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.-$$Lambda$LaunchActivity$DS0nm4qGZ1ekc-QTr5Zw4e9mH68 r4 = new org.telegram.ui.-$$Lambda$LaunchActivity$DS0nm4qGZ1ekc-QTr5Zw4e9mH68
            r4.<init>(r2, r0)
            r3.postRunnable(r4)
            goto L_0x07e7
        L_0x0618:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            if (r0 != r2) goto L_0x064c
            r0 = r3[r5]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = r1.loadingThemeFileName
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x0630
            java.lang.String r2 = r1.loadingThemeWallpaperName
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0633
        L_0x0630:
            r16.onThemeLoadFinish()
        L_0x0633:
            boolean r2 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r2 == 0) goto L_0x07e7
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.lang.String r2 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x07e7
            r1.updateAppUpdateViews(r6)
            goto L_0x07e7
        L_0x064c:
            int r2 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r2) goto L_0x0663
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x0655
            return
        L_0x0655:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x065e
            r16.onPasscodeResume()
            goto L_0x07e7
        L_0x065e:
            r16.onPasscodePause()
            goto L_0x07e7
        L_0x0663:
            int r2 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r2) goto L_0x066c
            r16.checkSystemBarColors()
            goto L_0x07e7
        L_0x066c:
            int r2 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r0 != r2) goto L_0x0699
            int r0 = r3.length
            if (r0 <= r6) goto L_0x07e7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07e7
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
            goto L_0x07e7
        L_0x0699:
            int r2 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r0 != r2) goto L_0x0779
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07e7
            r0 = r3[r5]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            boolean r2 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r2 == 0) goto L_0x06ba
            org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r2 == 0) goto L_0x06ba
            android.widget.FrameLayout r2 = r2.getContainer()
            goto L_0x06bb
        L_0x06ba:
            r2 = 0
        L_0x06bb:
            if (r2 != 0) goto L_0x06cb
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r5 = r4.size()
            int r5 = r5 - r6
            java.lang.Object r4 = r4.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            goto L_0x06cc
        L_0x06cb:
            r4 = 0
        L_0x06cc:
            if (r0 != r12) goto L_0x06fb
            r0 = r3[r6]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            if (r0 <= 0) goto L_0x06de
            r0 = 2131628306(0x7f0e1112, float:1.88839E38)
            java.lang.String r3 = "YourNameChanged"
            goto L_0x06e3
        L_0x06de:
            r0 = 2131624783(0x7f0e034f, float:1.8876755E38)
            java.lang.String r3 = "CannelTitleChanged"
        L_0x06e3:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            if (r2 == 0) goto L_0x06ee
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            goto L_0x06f2
        L_0x06ee:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
        L_0x06f2:
            org.telegram.ui.Components.Bulletin r0 = r2.createErrorBulletin(r0)
            r0.show()
            goto L_0x07e7
        L_0x06fb:
            if (r0 != r15) goto L_0x072a
            r0 = r3[r6]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            if (r0 <= 0) goto L_0x070d
            r0 = 2131628289(0x7f0e1101, float:1.8883866E38)
            java.lang.String r3 = "YourBioChanged"
            goto L_0x0712
        L_0x070d:
            r0 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r3 = "CannelDescriptionChanged"
        L_0x0712:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            if (r2 == 0) goto L_0x071d
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            goto L_0x0721
        L_0x071d:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
        L_0x0721:
            org.telegram.ui.Components.Bulletin r0 = r2.createErrorBulletin(r0)
            r0.show()
            goto L_0x07e7
        L_0x072a:
            if (r0 != 0) goto L_0x0754
            r0 = r3[r6]
            org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
            org.telegram.ui.Components.StickerSetBulletinLayout r5 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = r3[r15]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r6 = 0
            r5.<init>(r1, r6, r3, r0)
            r0 = 1500(0x5dc, float:2.102E-42)
            if (r4 == 0) goto L_0x074b
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r4, (org.telegram.ui.Components.Bulletin.Layout) r5, (int) r0)
            r0.show()
            goto L_0x07e7
        L_0x074b:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r2, (org.telegram.ui.Components.Bulletin.Layout) r5, (int) r0)
            r0.show()
            goto L_0x07e7
        L_0x0754:
            if (r0 != r6) goto L_0x07e7
            if (r4 == 0) goto L_0x0769
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r4)
            r2 = r3[r6]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)
            r0.show()
            goto L_0x07e7
        L_0x0769:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((android.widget.FrameLayout) r2)
            r2 = r3[r6]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)
            r0.show()
            goto L_0x07e7
        L_0x0779:
            int r2 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r0 != r2) goto L_0x0781
            r1.checkWasMutedByAdmin(r5)
            goto L_0x07e7
        L_0x0781:
            int r2 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r0 != r2) goto L_0x07d7
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.updateTextView
            if (r0 == 0) goto L_0x07e7
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x07e7
            r0 = r3[r5]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.lang.String r2 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            if (r2 == 0) goto L_0x07e7
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x07e7
            r0 = r3[r6]
            java.lang.Long r0 = (java.lang.Long) r0
            r2 = r3[r15]
            java.lang.Long r2 = (java.lang.Long) r2
            long r3 = r0.longValue()
            float r0 = (float) r3
            long r2 = r2.longValue()
            float r2 = (float) r2
            float r0 = r0 / r2
            org.telegram.ui.Components.RadialProgress2 r2 = r1.updateLayoutIcon
            r2.setProgress(r0, r6)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r1.updateTextView
            r3 = 2131624287(0x7f0e015f, float:1.887575E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r6 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r6
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r4[r5] = r0
            java.lang.String r0 = "AppUpdateDownloading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            goto L_0x07e7
        L_0x07d7:
            int r2 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r0 != r2) goto L_0x07e7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 != r6) goto L_0x07e4
            r5 = 1
        L_0x07e4:
            r1.updateAppUpdateViews(r5)
        L_0x07e7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$63(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$64 */
    public /* synthetic */ void lambda$didReceivedNotification$64$LaunchActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$66 */
    public /* synthetic */ void lambda$didReceivedNotification$66$LaunchActivity(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
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
                        LaunchActivity.lambda$didReceivedNotification$65(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$42(locationActivity);
            }
        }
    }

    static /* synthetic */ void lambda$didReceivedNotification$65(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$71 */
    public /* synthetic */ void lambda$didReceivedNotification$71$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$70$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$70 */
    public /* synthetic */ void lambda$didReceivedNotification$70$LaunchActivity(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    /* renamed from: lambda$didReceivedNotification$73 */
    public /* synthetic */ void lambda$didReceivedNotification$73$LaunchActivity(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$72$LaunchActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$72 */
    public /* synthetic */ void lambda$didReceivedNotification$72$LaunchActivity() {
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
                    LaunchActivity.this.lambda$checkFreeDiscSpace$75$LaunchActivity();
                }
            }, 2000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkFreeDiscSpace$75 */
    public /* synthetic */ void lambda$checkFreeDiscSpace$75$LaunchActivity() {
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
                                LaunchActivity.this.lambda$checkFreeDiscSpace$74$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkFreeDiscSpace$74 */
    public /* synthetic */ void lambda$checkFreeDiscSpace$74$LaunchActivity() {
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
            r9 = 2131624902(0x7f0e03c6, float:1.8876997E38)
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
            r14 = 2131625326(0x7f0e056e, float:1.8877857E38)
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
            org.telegram.ui.-$$Lambda$LaunchActivity$y_iSn7oy_kdmraXlZrdnWKUFe-Q r13 = new org.telegram.ui.-$$Lambda$LaunchActivity$y_iSn7oy_kdmraXlZrdnWKUFe-Q     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624903(0x7f0e03c7, float:1.8876999E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$eWhfp1_QBfFL7JTePftXnF1w7vw r0 = new org.telegram.ui.-$$Lambda$LaunchActivity$eWhfp1_QBfFL7JTePftXnF1w7vw     // Catch:{ Exception -> 0x0115 }
            r0.<init>()     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.-$$Lambda$LaunchActivity$EVPz1JNGxBqrOazPrcHKuy0h5yU r2 = new org.telegram.ui.-$$Lambda$LaunchActivity$EVPz1JNGxBqrOazPrcHKuy0h5yU     // Catch:{ Exception -> 0x0115 }
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

    static /* synthetic */ void lambda$showLanguageAlertInternal$76(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlertInternal$77 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$77$LaunchActivity(View view) {
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
    /* renamed from: lambda$showLanguageAlertInternal$78 */
    public /* synthetic */ void lambda$showLanguageAlertInternal$78$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                    LaunchActivity.this.lambda$showLanguageAlert$80$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                                    LaunchActivity.this.lambda$showLanguageAlert$82$LaunchActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                LaunchActivity.this.lambda$showLanguageAlert$79$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$79 */
    public /* synthetic */ void lambda$showLanguageAlert$79$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$82 */
    public /* synthetic */ void lambda$showLanguageAlert$82$LaunchActivity(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                LaunchActivity.this.lambda$showLanguageAlert$81$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showLanguageAlert$81 */
    public /* synthetic */ void lambda$showLanguageAlert$81$LaunchActivity(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass14 r0 = new Runnable() {
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
            $$Lambda$LaunchActivity$jWcj3Et5exTg2VLZ7obAyZgIkDY r4 = null;
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
                        LaunchActivity.this.lambda$updateCurrentConnectionState$83$LaunchActivity();
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, r4);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$updateCurrentConnectionState$83 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$83$LaunchActivity() {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$83$LaunchActivity():void");
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
