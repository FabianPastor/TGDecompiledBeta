package org.telegram.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.core.graphics.ColorUtils;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot;
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
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
import org.telegram.tgnet.TLRPC$TL_messages_toggleBotInAttachMenu;
import org.telegram.tgnet.TLRPC$TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
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
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AttachBotIntroTopView;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PaymentFormActivity;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    public static boolean isResumed;
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    public static Runnable onResumeStaticCallback;
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$User> contactsToSend;
    private Uri contactsToSendUri;
    private int currentConnectionState;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    /* access modifiers changed from: private */
    public DrawerLayoutAdapter drawerLayoutAdapter;
    public DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    /* access modifiers changed from: private */
    public Uri exportingChatUri;
    private boolean finished;
    private FireworksOverlay fireworksOverlay;
    private FrameLayout frameLayout;
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<String> importingStickersEmoji;
    private String importingStickersSoftware;
    private boolean isNavigationBarColorFrozen = false;
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
    private boolean navigateToPremiumBot;
    private Runnable navigateToPremiumGiftCallback;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private List<Runnable> onUserLeaveHintListeners = new ArrayList();
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    /* access modifiers changed from: private */
    public ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
    private AlertDialog proxyErrorDialog;
    /* access modifiers changed from: private */
    public ActionBarLayout rightActionBarLayout;
    /* access modifiers changed from: private */
    public View rippleAbove;
    private String sendingText;
    /* access modifiers changed from: private */
    public FrameLayout shadowTablet;
    /* access modifiers changed from: private */
    public FrameLayout shadowTabletSide;
    /* access modifiers changed from: private */
    public RecyclerListView sideMenu;
    private FrameLayout sideMenuContainer;
    private HashMap<String, String> systemLocaleStrings;
    /* access modifiers changed from: private */
    public boolean tabletFullSize;
    private int[] tempLocation;
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
    /* access modifiers changed from: private */
    public String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;
    private boolean wasMutedByAdminRaisedHand;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$1(View view) {
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:28|29|30|31|32) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x00bd */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            java.lang.String r0 = "flyme"
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r1 == 0) goto L_0x001a
            android.os.StrictMode$VmPolicy$Builder r1 = new android.os.StrictMode$VmPolicy$Builder
            android.os.StrictMode$VmPolicy r2 = android.os.StrictMode.getVmPolicy()
            r1.<init>(r2)
            android.os.StrictMode$VmPolicy$Builder r1 = r1.detectLeakedClosableObjects()
            android.os.StrictMode$VmPolicy r1 = r1.build()
            android.os.StrictMode.setVmPolicy(r1)
        L_0x001a:
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r1 = r12.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r12, r1)
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r12.currentAccount = r1
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x009b
            android.content.Intent r1 = r12.getIntent()
            if (r1 == 0) goto L_0x009b
            java.lang.String r2 = r1.getAction()
            if (r2 == 0) goto L_0x009b
            java.lang.String r2 = r1.getAction()
            java.lang.String r3 = "android.intent.action.SEND"
            boolean r2 = r3.equals(r2)
            if (r2 != 0) goto L_0x0094
            java.lang.String r2 = r1.getAction()
            java.lang.String r3 = "android.intent.action.SEND_MULTIPLE"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x005b
            goto L_0x0094
        L_0x005b:
            java.lang.String r2 = r1.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x009b
            android.net.Uri r1 = r1.getData()
            if (r1 == 0) goto L_0x009b
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "tg:proxy"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x009b
            java.lang.String r2 = "tg://proxy"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x009b
            java.lang.String r2 = "tg:socks"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L_0x009b
            java.lang.String r2 = "tg://socks"
            boolean r1 = r1.startsWith(r2)
            goto L_0x009b
        L_0x0094:
            super.onCreate(r13)
            r12.finish()
            return
        L_0x009b:
            r1 = 1
            r12.requestWindowFeature(r1)
            r2 = 2131689490(0x7f0var_, float:1.9007997E38)
            r12.setTheme(r2)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 0
            r4 = 21
            if (r2 < r4) goto L_0x00c6
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r5 = new android.app.ActivityManager$TaskDescription     // Catch:{ all -> 0x00bd }
            java.lang.String r6 = "actionBarDefault"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ all -> 0x00bd }
            r6 = r6 | r2
            r5.<init>(r3, r3, r6)     // Catch:{ all -> 0x00bd }
            r12.setTaskDescription(r5)     // Catch:{ all -> 0x00bd }
        L_0x00bd:
            android.view.Window r5 = r12.getWindow()     // Catch:{ all -> 0x00c5 }
            r5.setNavigationBarColor(r2)     // Catch:{ all -> 0x00c5 }
            goto L_0x00c6
        L_0x00c5:
        L_0x00c6:
            android.view.Window r2 = r12.getWindow()
            r5 = 2131166192(0x7var_f0, float:1.7946622E38)
            r2.setBackgroundDrawableResource(r5)
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x00ea
            boolean r2 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r2 != 0) goto L_0x00ea
            android.view.Window r2 = r12.getWindow()     // Catch:{ Exception -> 0x00e6 }
            r5 = 8192(0x2000, float:1.14794E-41)
            r2.setFlags(r5, r5)     // Catch:{ Exception -> 0x00e6 }
            goto L_0x00ea
        L_0x00e6:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00ea:
            super.onCreate(r13)
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 24
            if (r2 < r5) goto L_0x00f9
            boolean r6 = r12.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r6
        L_0x00f9:
            org.telegram.ui.ActionBar.Theme.createCommonChatResources()
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r12)
            java.lang.String r6 = org.telegram.messenger.SharedConfig.passcodeHash
            int r6 = r6.length()
            if (r6 == 0) goto L_0x0115
            boolean r6 = org.telegram.messenger.SharedConfig.appLocked
            if (r6 == 0) goto L_0x0115
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            int r7 = (int) r6
            org.telegram.messenger.SharedConfig.lastPauseTime = r7
        L_0x0115:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r12)
            org.telegram.ui.LaunchActivity$1 r6 = new org.telegram.ui.LaunchActivity$1
            r6.<init>(r12)
            r12.actionBarLayout = r6
            org.telegram.ui.LaunchActivity$2 r6 = new org.telegram.ui.LaunchActivity$2
            r6.<init>(r12)
            r12.frameLayout = r6
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r8 = -1
            r7.<init>(r8, r8)
            r12.setContentView(r6, r7)
            r6 = 8
            if (r2 < r4) goto L_0x013d
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r12)
            r12.themeSwitchImageView = r7
            r7.setVisibility(r6)
        L_0x013d:
            org.telegram.ui.LaunchActivity$3 r7 = new org.telegram.ui.LaunchActivity$3
            r7.<init>(r12, r12)
            r12.drawerLayoutContainer = r7
            java.lang.String r9 = "windowBackgroundWhite"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setBehindKeyboardColor(r9)
            android.widget.FrameLayout r7 = r12.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r12.drawerLayoutContainer
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r7.addView(r9, r11)
            if (r2 < r4) goto L_0x0175
            org.telegram.ui.LaunchActivity$4 r2 = new org.telegram.ui.LaunchActivity$4
            r2.<init>(r12)
            r12.themeSwitchSunView = r2
            android.widget.FrameLayout r4 = r12.frameLayout
            r7 = 48
            r9 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r4.addView(r2, r7)
            android.view.View r2 = r12.themeSwitchSunView
            r2.setVisibility(r6)
        L_0x0175:
            android.widget.FrameLayout r2 = r12.frameLayout
            org.telegram.ui.Components.FireworksOverlay r4 = new org.telegram.ui.Components.FireworksOverlay
            r4.<init>(r12)
            r12.fireworksOverlay = r4
            r2.addView(r4)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r4 = 0
            if (r2 == 0) goto L_0x026a
            android.view.Window r2 = r12.getWindow()
            r7 = 16
            r2.setSoftInputMode(r7)
            org.telegram.ui.LaunchActivity$5 r2 = new org.telegram.ui.LaunchActivity$5
            r2.<init>(r12)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r7.addView(r2, r9)
            org.telegram.ui.LaunchActivity$6 r7 = new org.telegram.ui.LaunchActivity$6
            r7.<init>(r12, r12)
            r12.backgroundTablet = r7
            r7.setOccupyStatusBar(r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r7 = r12.backgroundTablet
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r11 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r7.setBackgroundImage(r9, r11)
            org.telegram.ui.Components.SizeNotifierFrameLayout r7 = r12.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createRelative(r8, r8)
            r2.addView(r7, r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.actionBarLayout
            r2.addView(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = new org.telegram.ui.ActionBar.ActionBarLayout
            r7.<init>(r12)
            r12.rightActionBarLayout = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = rightFragmentsStack
            r7.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.rightActionBarLayout
            r7.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.rightActionBarLayout
            r2.addView(r7)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r12)
            r12.shadowTabletSide = r7
            r9 = 1076449908(0x40295274, float:2.6456575)
            r7.setBackgroundColor(r9)
            android.widget.FrameLayout r7 = r12.shadowTabletSide
            r2.addView(r7)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r12)
            r12.shadowTablet = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x01fe
            r9 = 8
            goto L_0x01ff
        L_0x01fe:
            r9 = 0
        L_0x01ff:
            r7.setVisibility(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            r9 = 2130706432(0x7var_, float:1.7014118E38)
            r7.setBackgroundColor(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            r2.addView(r7)
            android.widget.FrameLayout r7 = r12.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda19 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda19
            r9.<init>(r12)
            r7.setOnTouchListener(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18 r9 = org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18.INSTANCE
            r7.setOnClickListener(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = new org.telegram.ui.ActionBar.ActionBarLayout
            r7.<init>(r12)
            r12.layersActionBarLayout = r7
            r7.setRemoveActionBarExtraHeight(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            android.widget.FrameLayout r9 = r12.shadowTablet
            r7.setBackgroundView(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            r7.setUseAlphaAnimations(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            r9 = 2131165291(0x7var_b, float:1.7944795E38)
            r7.setBackgroundResource(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            r7.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            r7.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r12.drawerLayoutContainer
            r7.setDrawerLayoutContainer(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x025b
            goto L_0x025c
        L_0x025b:
            r6 = 0
        L_0x025c:
            r7.setVisibility(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.layersActionBarLayout
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.layersActionBarLayout
            r2.addView(r6)
            goto L_0x0276
        L_0x026a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r8, r8)
            r2.addView(r6, r7)
        L_0x0276:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r12)
            r12.sideMenuContainer = r2
            org.telegram.ui.LaunchActivity$7 r2 = new org.telegram.ui.LaunchActivity$7
            r2.<init>(r12)
            r12.sideMenu = r2
            org.telegram.ui.Components.SideMenultItemAnimator r6 = new org.telegram.ui.Components.SideMenultItemAnimator
            r6.<init>(r2)
            r12.itemAnimator = r6
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            r2.setItemAnimator(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            java.lang.String r6 = "chats_menuBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r2.setBackgroundColor(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            r6.<init>(r12, r1, r4)
            r2.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            r2.setAllowItemsInteractionDuringAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r6 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r7 = r12.itemAnimator
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r12.drawerLayoutContainer
            r6.<init>(r12, r7, r9)
            r12.drawerLayoutAdapter = r6
            r2.setAdapter(r6)
            android.widget.FrameLayout r2 = r12.sideMenuContainer
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r2.addView(r6, r7)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer
            android.widget.FrameLayout r6 = r12.sideMenuContainer
            r2.setDrawerLayout(r6)
            android.widget.FrameLayout r2 = r12.sideMenuContainer
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x02e5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x02fc
        L_0x02e5:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r6.x
            int r6 = r6.y
            int r6 = java.lang.Math.min(r9, r6)
            r9 = 1113587712(0x42600000, float:56.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 - r9
            int r6 = java.lang.Math.min(r7, r6)
        L_0x02fc:
            r2.width = r6
            r2.height = r8
            android.widget.FrameLayout r6 = r12.sideMenuContainer
            r6.setLayoutParams(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r12.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda95 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda95
            r6.<init>(r12)
            r2.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r6)
            androidx.recyclerview.widget.ItemTouchHelper r2 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$8 r6 = new org.telegram.ui.LaunchActivity$8
            r7 = 3
            r6.<init>(r7, r4)
            r2.<init>(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            r2.attachToRecyclerView(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda96 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda96
            r9.<init>(r12, r2)
            r6.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r9)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.actionBarLayout
            r2.setParentActionBarLayout(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r12.drawerLayoutContainer
            r2.setDrawerLayoutContainer(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            r2.init(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda32 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda32
            r6.<init>(r12)
            r2.setFragmentStackChangedListener(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            r2.setDelegate(r12)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            r12.checkCurrentAccount()
            int r2 = r12.currentAccount
            r12.updateCurrentConnectionState(r2)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r9 = new java.lang.Object[r1]
            r9[r4] = r12
            r2.postNotificationName(r6, r9)
            int r2 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getConnectionState()
            r12.currentConnectionState = r2
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.reloadInterface
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r2.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.showBulletin
            r2.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r2.addObserver(r12, r6)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0502
            int r2 = r12.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 != 0) goto L_0x0409
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r12.getClientNotActivatedFragment()
            r2.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer
            r2.setAllowOpenDrawer(r4, r4)
            goto L_0x041d
        L_0x0409:
            org.telegram.ui.DialogsActivity r2 = new org.telegram.ui.DialogsActivity
            r2.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r12.sideMenu
            r2.setSideMenu(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            r3.addFragmentToStack(r2)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r12.drawerLayoutContainer
            r2.setAllowOpenDrawer(r1, r4)
        L_0x041d:
            if (r13 == 0) goto L_0x0581
            java.lang.String r2 = "fragment"
            java.lang.String r2 = r13.getString(r2)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0581
            java.lang.String r3 = "args"
            android.os.Bundle r3 = r13.getBundle(r3)     // Catch:{ Exception -> 0x04fc }
            int r6 = r2.hashCode()     // Catch:{ Exception -> 0x04fc }
            r9 = 5
            r10 = 4
            r11 = 2
            switch(r6) {
                case -1529105743: goto L_0x046a;
                case -1349522494: goto L_0x0460;
                case 3052376: goto L_0x0456;
                case 98629247: goto L_0x044c;
                case 738950403: goto L_0x0442;
                case 1434631203: goto L_0x0438;
                default: goto L_0x0437;
            }     // Catch:{ Exception -> 0x04fc }
        L_0x0437:
            goto L_0x0473
        L_0x0438:
            java.lang.String r6 = "settings"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 1
            goto L_0x0473
        L_0x0442:
            java.lang.String r6 = "channel"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 3
            goto L_0x0473
        L_0x044c:
            java.lang.String r6 = "group"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 2
            goto L_0x0473
        L_0x0456:
            java.lang.String r6 = "chat"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 0
            goto L_0x0473
        L_0x0460:
            java.lang.String r6 = "chat_profile"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 4
            goto L_0x0473
        L_0x046a:
            java.lang.String r6 = "wallpapers"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x04fc }
            if (r2 == 0) goto L_0x0473
            r8 = 5
        L_0x0473:
            if (r8 == 0) goto L_0x04e8
            if (r8 == r1) goto L_0x04cc
            if (r8 == r11) goto L_0x04b8
            if (r8 == r7) goto L_0x04a4
            if (r8 == r10) goto L_0x0490
            if (r8 == r9) goto L_0x0481
            goto L_0x0581
        L_0x0481:
            org.telegram.ui.WallpapersListActivity r2 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x0490:
            if (r3 == 0) goto L_0x0581
            org.telegram.ui.ProfileActivity r2 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            boolean r3 = r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            if (r3 == 0) goto L_0x0581
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x04a4:
            if (r3 == 0) goto L_0x0581
            org.telegram.ui.ChannelCreateActivity r2 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            boolean r3 = r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            if (r3 == 0) goto L_0x0581
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x04b8:
            if (r3 == 0) goto L_0x0581
            org.telegram.ui.GroupCreateFinalActivity r2 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            boolean r3 = r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            if (r3 == 0) goto L_0x0581
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x04cc:
            java.lang.String r2 = "user_id"
            int r6 = r12.currentAccount     // Catch:{ Exception -> 0x04fc }
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)     // Catch:{ Exception -> 0x04fc }
            long r6 = r6.clientUserId     // Catch:{ Exception -> 0x04fc }
            r3.putLong(r2, r6)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ProfileActivity r2 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x04e8:
            if (r3 == 0) goto L_0x0581
            org.telegram.ui.ChatActivity r2 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x04fc }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04fc }
            boolean r3 = r3.addFragmentToStack(r2)     // Catch:{ Exception -> 0x04fc }
            if (r3 == 0) goto L_0x0581
            r2.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0581
        L_0x04fc:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            goto L_0x0581
        L_0x0502:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r3 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x0517
            org.telegram.ui.DialogsActivity r2 = (org.telegram.ui.DialogsActivity) r2
            org.telegram.ui.Components.RecyclerListView r3 = r12.sideMenu
            r2.setSideMenu(r3)
        L_0x0517:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x0558
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 > r1) goto L_0x0533
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0533
            r2 = 1
            goto L_0x0534
        L_0x0533:
            r2 = 0
        L_0x0534:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r1) goto L_0x0559
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 != 0) goto L_0x0556
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.IntroActivity
            if (r3 == 0) goto L_0x0559
        L_0x0556:
            r2 = 0
            goto L_0x0559
        L_0x0558:
            r2 = 1
        L_0x0559:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r1) goto L_0x057c
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 != 0) goto L_0x057b
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.IntroActivity
            if (r3 == 0) goto L_0x057c
        L_0x057b:
            r2 = 0
        L_0x057c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r12.drawerLayoutContainer
            r3.setAllowOpenDrawer(r2, r4)
        L_0x0581:
            r12.checkLayout()
            r12.checkSystemBarColors()
            android.content.Intent r2 = r12.getIntent()
            if (r13 == 0) goto L_0x058f
            r13 = 1
            goto L_0x0590
        L_0x058f:
            r13 = 0
        L_0x0590:
            r12.handleIntent(r2, r4, r13, r4)
            java.lang.String r13 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05f4 }
            java.lang.String r2 = android.os.Build.USER     // Catch:{ Exception -> 0x05f4 }
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x05a0
            java.lang.String r13 = r13.toLowerCase()     // Catch:{ Exception -> 0x05f4 }
            goto L_0x05a1
        L_0x05a0:
            r13 = r3
        L_0x05a1:
            if (r2 == 0) goto L_0x05a7
            java.lang.String r3 = r13.toLowerCase()     // Catch:{ Exception -> 0x05f4 }
        L_0x05a7:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f4 }
            if (r2 == 0) goto L_0x05c7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f4 }
            r2.<init>()     // Catch:{ Exception -> 0x05f4 }
            java.lang.String r6 = "OS name "
            r2.append(r6)     // Catch:{ Exception -> 0x05f4 }
            r2.append(r13)     // Catch:{ Exception -> 0x05f4 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x05f4 }
            r2.append(r3)     // Catch:{ Exception -> 0x05f4 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x05f4 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x05f4 }
        L_0x05c7:
            boolean r13 = r13.contains(r0)     // Catch:{ Exception -> 0x05f4 }
            if (r13 != 0) goto L_0x05d3
            boolean r13 = r3.contains(r0)     // Catch:{ Exception -> 0x05f4 }
            if (r13 == 0) goto L_0x05f8
        L_0x05d3:
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05f4 }
            if (r13 > r5) goto L_0x05f8
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r1     // Catch:{ Exception -> 0x05f4 }
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x05f4 }
            android.view.View r13 = r13.getDecorView()     // Catch:{ Exception -> 0x05f4 }
            android.view.View r13 = r13.getRootView()     // Catch:{ Exception -> 0x05f4 }
            android.view.ViewTreeObserver r0 = r13.getViewTreeObserver()     // Catch:{ Exception -> 0x05f4 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda20 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda20     // Catch:{ Exception -> 0x05f4 }
            r2.<init>(r13)     // Catch:{ Exception -> 0x05f4 }
            r12.onGlobalLayoutListener = r2     // Catch:{ Exception -> 0x05f4 }
            r0.addOnGlobalLayoutListener(r2)     // Catch:{ Exception -> 0x05f4 }
            goto L_0x05f8
        L_0x05f4:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x05f8:
            org.telegram.messenger.MediaController r13 = org.telegram.messenger.MediaController.getInstance()
            r13.setBaseActivity(r12, r1)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r12)
            r12.updateAppUpdateViews(r4)
            int r13 = android.os.Build.VERSION.SDK_INT
            r0 = 23
            if (r13 < r0) goto L_0x060e
            org.telegram.messenger.FingerprintController.checkKeyReady()
        L_0x060e:
            r0 = 28
            if (r13 < r0) goto L_0x063e
            java.lang.String r13 = "activity"
            java.lang.Object r13 = r12.getSystemService(r13)
            android.app.ActivityManager r13 = (android.app.ActivityManager) r13
            boolean r13 = r13.isBackgroundRestricted()
            if (r13 == 0) goto L_0x063e
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.getLastCheckedBackgroundActivity()
            long r0 = r0 - r2
            r2 = 86400000(0x5265CLASSNAME, double:4.2687272E-316)
            int r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r13 < 0) goto L_0x063e
            android.app.Dialog r13 = org.telegram.ui.Components.AlertsCreator.createBackgroundActivityDialog(r12)
            r13.show()
            long r0 = java.lang.System.currentTimeMillis()
            org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.setLastCheckedBackgroundActivity(r0)
        L_0x063e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(View view, MotionEvent motionEvent) {
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
    public /* synthetic */ void lambda$onCreate$3(View view, int i, float f, float f2) {
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
        } else {
            Integer num = null;
            if (view instanceof DrawerAddCell) {
                int i2 = 0;
                for (int i3 = 3; i3 >= 0; i3--) {
                    if (!UserConfig.getInstance(i3).isClientActivated()) {
                        i2++;
                        if (num == null) {
                            num = Integer.valueOf(i3);
                        }
                    }
                }
                if (!UserConfig.hasPremiumOnAccounts()) {
                    i2--;
                }
                if (i2 > 0 && num != null) {
                    lambda$runLinkRequest$61(new LoginActivity(num.intValue()));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (!UserConfig.hasPremiumOnAccounts() && this.actionBarLayout.fragmentsStack.size() > 0) {
                    BaseFragment baseFragment = this.actionBarLayout.fragmentsStack.get(0);
                    LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, this, 7, this.currentAccount);
                    baseFragment.showDialog(limitReachedBottomSheet);
                    limitReachedBottomSheet.onShowPremiumScreenRunnable = new LaunchActivity$$ExternalSyntheticLambda33(this);
                }
            } else {
                int id = this.drawerLayoutAdapter.getId(i);
                if (id == 2) {
                    lambda$runLinkRequest$61(new GroupCreateActivity(new Bundle()));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("createSecretChat", true);
                    bundle.putBoolean("allowBots", false);
                    bundle.putBoolean("allowSelf", false);
                    lambda$runLinkRequest$61(new ContactsActivity(bundle));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 4) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                        lambda$runLinkRequest$61(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                    } else {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("step", 0);
                        lambda$runLinkRequest$61(new ChannelCreateActivity(bundle2));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 6) {
                    lambda$runLinkRequest$61(new ContactsActivity((Bundle) null));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 7) {
                    lambda$runLinkRequest$61(new InviteContactsActivity());
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 8) {
                    openSettings(false);
                } else if (id == 9) {
                    Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 10) {
                    lambda$runLinkRequest$61(new CallLogActivity());
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 11) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                    lambda$runLinkRequest$61(new ChatActivity(bundle3));
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
                            lambda$runLinkRequest$61(new PeopleNearbyActivity());
                        } else {
                            lambda$runLinkRequest$61(new ActionIntroActivity(4));
                        }
                        this.drawerLayoutContainer.closeDrawer(false);
                        return;
                    }
                    lambda$runLinkRequest$61(new ActionIntroActivity(1));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 13) {
                    Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
                    this.drawerLayoutContainer.closeDrawer(false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2() {
        this.drawerLayoutContainer.closeDrawer(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$4(ItemTouchHelper itemTouchHelper, View view, int i) {
        if (!(view instanceof DrawerUserCell)) {
            return false;
        }
        final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
        if (accountNumber == this.currentAccount || AndroidUtilities.isTablet()) {
            itemTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
            return false;
        }
        AnonymousClass9 r2 = new DialogsActivity((Bundle) null) {
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
    public /* synthetic */ void lambda$onCreate$5() {
        checkSystemBarColors(true, false);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$6(View view) {
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

    public void addOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.add(runnable);
    }

    public void removeOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.remove(runnable);
    }

    private BaseFragment getClientNotActivatedFragment() {
        if (LoginActivity.loadCurrentState(false).getInt("currentViewNum", 0) != 0) {
            return new LoginActivity();
        }
        return new IntroActivity();
    }

    public FireworksOverlay getFireworksOverlay() {
        return this.fireworksOverlay;
    }

    private void openSettings(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (z) {
            bundle.putBoolean("expandPhoto", true);
        }
        lambda$runLinkRequest$61(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(false, true, !this.isNavigationBarColorFrozen);
    }

    private void checkSystemBarColors(boolean z) {
        checkSystemBarColors(z, true, !this.isNavigationBarColorFrozen);
    }

    private void checkSystemBarColors(boolean z, boolean z2) {
        checkSystemBarColors(false, z, z2);
    }

    private void checkSystemBarColors(boolean z, boolean z2, boolean z3) {
        BaseFragment baseFragment;
        boolean z4;
        boolean z5 = true;
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            baseFragment = arrayList.get(arrayList.size() - 1);
        } else {
            baseFragment = null;
        }
        if (baseFragment != null && (baseFragment.isRemovingFromStack() || baseFragment.isInPreviewMode())) {
            if (mainFragmentsStack.size() > 1) {
                ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                baseFragment = arrayList2.get(arrayList2.size() - 2);
            } else {
                baseFragment = null;
            }
        }
        boolean z6 = baseFragment != null && baseFragment.hasForceLightStatusBar();
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            if (z2) {
                if (baseFragment != null) {
                    z4 = baseFragment.isLightStatusBar();
                } else {
                    z4 = ColorUtils.calculateLuminance(Theme.getColor("actionBarDefault", (boolean[]) null, true)) > 0.699999988079071d;
                }
                AndroidUtilities.setLightStatusBar(getWindow(), z4, z6);
            }
            if (i >= 26 && z3 && (!z || baseFragment == null || !baseFragment.isInPreviewMode())) {
                Window window = getWindow();
                int color = (baseFragment == null || !z) ? Theme.getColor("windowBackgroundGray", (boolean[]) null, true) : baseFragment.getNavigationBarColor();
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
                    Window window2 = getWindow();
                    if (computePerceivedBrightness < 0.721f) {
                        z5 = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window2, z5);
                }
            }
        }
        if ((SharedConfig.noStatusBar || z6) && i >= 21 && z2) {
            getWindow().setStatusBarColor(0);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ DialogsActivity lambda$switchToAccount$7(Void voidR) {
        return new DialogsActivity((Bundle) null);
    }

    public void switchToAccount(int i, boolean z) {
        switchToAccount(i, z, LaunchActivity$$ExternalSyntheticLambda66.INSTANCE);
    }

    public void switchToAccount(int i, boolean z, GenericProvider<Void, DialogsActivity> genericProvider) {
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
            DialogsActivity provide = genericProvider.provide(null);
            provide.setSideMenu(this.sideMenu);
            this.actionBarLayout.addFragmentToStack(provide, 0);
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
            if (i >= 4) {
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
        this.actionBarLayout.rebuildLogout();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildLogout();
            this.rightActionBarLayout.rebuildLogout();
        }
        lambda$runLinkRequest$61(new IntroActivity().setOnLogout());
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.historyImportProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersImportComplete);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
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
                    PasscodeView passcodeView2 = this.passcodeView;
                    if (passcodeView2 == null || passcodeView2.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                    }
                }
                this.shadowTabletSide.setVisibility(8);
                this.rightActionBarLayout.setVisibility(8);
                SizeNotifierFrameLayout sizeNotifierFrameLayout = this.backgroundTablet;
                if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i = 8;
                }
                sizeNotifierFrameLayout.setVisibility(i);
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
                PasscodeView passcodeView3 = this.passcodeView;
                if (passcodeView3 == null || passcodeView3.getVisibility() != 0) {
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
            AnonymousClass10 r0 = new BlockingUpdateView(this) {
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
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new LaunchActivity$11$$ExternalSyntheticLambda0(this)).start();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAcceptTerms$0() {
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

    public void showPasscodeActivity(boolean z, boolean z2, int i, int i2, Runnable runnable, Runnable runnable2) {
        if (this.drawerLayoutContainer != null) {
            if (this.passcodeView == null) {
                PasscodeView passcodeView2 = new PasscodeView(this);
                this.passcodeView = passcodeView2;
                this.drawerLayoutContainer.addView(passcodeView2, LayoutHelper.createFrame(-1, -1.0f));
            }
            SharedConfig.appLocked = true;
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(false, false);
            } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(false, true);
            }
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.passcodeView.onShow(z, z2, i, i2, new LaunchActivity$$ExternalSyntheticLambda37(this, runnable), runnable2);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda94(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPasscodeActivity$8(Runnable runnable) {
        this.actionBarLayout.setVisibility(4);
        if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.getVisibility() == 0) {
                this.layersActionBarLayout.setVisibility(4);
            }
            this.rightActionBarLayout.setVisibility(4);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPasscodeActivity$9() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r80v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v83, resolved type: org.telegram.ui.EditWidgetActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r80v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r80v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r80v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v1, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v2, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v5, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v7, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v9, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v10, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v11, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v12, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v4, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v13, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v14, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v158, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v15, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v16, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v13, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v17, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v18, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v7, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v19, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v20, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v21, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v8, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v23, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v24, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v25, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v26, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v27, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v28, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v28, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v28, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v278, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v284, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v29, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v29, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v29, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v322, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v30, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v30, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v30, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v240, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v331, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v31, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v31, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v31, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v337, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v32, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v32, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v32, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v343, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v33, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v33, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v33, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v34, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v34, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v35, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v36, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v36, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v37, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v358, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v38, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v364, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v39, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v278, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v40, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v41, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v382, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v303, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v42, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v43, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v34, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v399, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v44, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v44, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v10, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v46, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v46, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v37, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v405, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v407, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v409, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v411, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v413, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v415, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v418, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v426, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v427, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v428, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v437, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v440, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v442, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v445, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v448, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v450, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v451, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v453, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v459, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v462, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v470, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v471, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v33, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v50, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v53, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v54, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r8v3, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r8v5 */
    /* JADX WARNING: type inference failed for: r8v6 */
    /* JADX WARNING: type inference failed for: r8v7 */
    /* JADX WARNING: type inference failed for: r3v0, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r8v8 */
    /* JADX WARNING: type inference failed for: r8v10 */
    /* JADX WARNING: type inference failed for: r8v11 */
    /* JADX WARNING: type inference failed for: r8v12 */
    /* JADX WARNING: type inference failed for: r8v13 */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: type inference failed for: r3v11 */
    /* JADX WARNING: type inference failed for: r3v14 */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r18v13, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r8v36 */
    /* JADX WARNING: type inference failed for: r8v38 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:922|925|926|927|928) */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x1e0f, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r3)) != false) goto L_0x1e11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1032:0x1e8d, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r3)) != false) goto L_0x1e8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0330, code lost:
        if (r15.sendingText == null) goto L_0x01a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x04b8, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09f3, code lost:
        if (r1.intValue() == 0) goto L_0x09f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0121, code lost:
        r0 = r24.getIntent().getExtras();
        r4 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0136, code lost:
        r20 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0139, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x013a, code lost:
        r20 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x015b, code lost:
        if (r1.equals(r0) != false) goto L_0x015f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1628, code lost:
        if (r1.longValue() == 0) goto L_0x162d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1a5d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:927:0x1a5c */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1007:0x1ddb  */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x1e58  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x1ff6  */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x2008  */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x2016  */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x2027  */
    /* JADX WARNING: Removed duplicated region for block: B:1184:0x227d  */
    /* JADX WARNING: Removed duplicated region for block: B:1195:0x22c8  */
    /* JADX WARNING: Removed duplicated region for block: B:1206:0x2313  */
    /* JADX WARNING: Removed duplicated region for block: B:1208:0x231f  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0337  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f7 A[Catch:{ Exception -> 0x051b }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0522  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x06de  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x07ca A[Catch:{ Exception -> 0x07d8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x07d7  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x016d A[SYNTHETIC, Splitter:B:70:0x016d] */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x14ad  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x1590 A[Catch:{ Exception -> 0x159e }] */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x159d  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x1a64 A[SYNTHETIC, Splitter:B:932:0x1a64] */
    /* JADX WARNING: Removed duplicated region for block: B:994:0x1dac  */
    @android.annotation.SuppressLint({"Range"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r85, boolean r86, boolean r87, boolean r88) {
        /*
            r84 = this;
            r15 = r84
            r14 = r85
            r0 = r87
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r84, r85)
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
            java.lang.String r1 = r85.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r8 = r85.getFlags()
            java.lang.String r9 = r85.getAction()
            int[] r11 = new int[r13]
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r2 = "currentAccount"
            int r1 = r14.getIntExtra(r2, r1)
            r11[r12] = r1
            r1 = r11[r12]
            r15.switchToAccount(r1, r13)
            if (r9 == 0) goto L_0x006f
            java.lang.String r1 = "voip"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x006f
            r33 = 1
            goto L_0x0071
        L_0x006f:
            r33 = 0
        L_0x0071:
            if (r88 != 0) goto L_0x009c
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13)
            if (r1 != 0) goto L_0x007d
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x009c
        L_0x007d:
            r2 = 1
            r3 = 0
            r4 = -1
            r5 = -1
            r6 = 0
            r7 = 0
            r1 = r84
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            r1.saveConfig(r12)
            if (r33 != 0) goto L_0x009c
            r15.passcodeSaveIntent = r14
            r10 = r86
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x009c:
            r10 = r86
            r7 = 0
            r15.photoPathsArray = r7
            r15.videoPath = r7
            r15.sendingText = r7
            r15.documentsPathsArray = r7
            r15.documentsOriginalPathsArray = r7
            r15.documentsMimeType = r7
            r15.documentsUrisArray = r7
            r15.exportingChatUri = r7
            r15.contactsToSend = r7
            r15.contactsToSendUri = r7
            r15.importingStickers = r7
            r15.importingStickersEmoji = r7
            r15.importingStickersSoftware = r7
            r1 = 1048576(0x100000, float:1.469368E-39)
            r1 = r1 & r8
            java.lang.String r8 = " "
            java.lang.String r6 = "message_id"
            r4 = 0
            if (r1 != 0) goto L_0x1d6b
            java.lang.String r1 = r85.getAction()
            if (r1 == 0) goto L_0x1d6b
            if (r0 != 0) goto L_0x1d6b
            java.lang.String r0 = r85.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "hash"
            java.lang.String r3 = "android.intent.extra.STREAM"
            java.lang.String r2 = "\n"
            java.lang.String r13 = ""
            if (r0 == 0) goto L_0x0375
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x015d
            android.os.Bundle r0 = r85.getExtras()
            if (r0 == 0) goto L_0x015d
            android.os.Bundle r0 = r85.getExtras()
            java.lang.String r9 = "dialogId"
            long r20 = r0.getLong(r9, r4)
            int r0 = (r20 > r4 ? 1 : (r20 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x014a
            android.os.Bundle r0 = r85.getExtras()     // Catch:{ all -> 0x0144 }
            java.lang.String r9 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r9)     // Catch:{ all -> 0x0144 }
            if (r0 == 0) goto L_0x0148
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0144 }
            java.util.List r9 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r9)     // Catch:{ all -> 0x0144 }
            int r12 = r9.size()     // Catch:{ all -> 0x0144 }
            r7 = 0
        L_0x010f:
            if (r7 >= r12) goto L_0x0148
            java.lang.Object r24 = r9.get(r7)     // Catch:{ all -> 0x0144 }
            androidx.core.content.pm.ShortcutInfoCompat r24 = (androidx.core.content.pm.ShortcutInfoCompat) r24     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = r24.getId()     // Catch:{ all -> 0x0144 }
            boolean r4 = r0.equals(r4)     // Catch:{ all -> 0x0144 }
            if (r4 == 0) goto L_0x013d
            android.content.Intent r0 = r24.getIntent()     // Catch:{ all -> 0x0144 }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = "dialogId"
            r9 = 0
            long r4 = r0.getLong(r4, r9)     // Catch:{ all -> 0x0144 }
            r7 = 0
            java.lang.String r0 = r0.getString(r1, r7)     // Catch:{ all -> 0x0139 }
            r20 = r4
            goto L_0x0153
        L_0x0139:
            r0 = move-exception
            r20 = r4
            goto L_0x0145
        L_0x013d:
            int r7 = r7 + 1
            r10 = r86
            r4 = 0
            goto L_0x010f
        L_0x0144:
            r0 = move-exception
        L_0x0145:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0148:
            r0 = 0
            goto L_0x0153
        L_0x014a:
            android.os.Bundle r0 = r85.getExtras()
            r4 = 0
            java.lang.String r0 = r0.getString(r1, r4)
        L_0x0153:
            java.lang.String r1 = org.telegram.messenger.SharedConfig.directShareHash
            if (r1 == 0) goto L_0x015d
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x015f
        L_0x015d:
            r20 = 0
        L_0x015f:
            java.lang.String r1 = r85.getType()
            if (r1 == 0) goto L_0x01a5
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x01a5
            android.os.Bundle r0 = r85.getExtras()     // Catch:{ Exception -> 0x019e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x019e }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x019e }
            if (r0 == 0) goto L_0x01a2
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x019e }
            r3 = 0
            r4 = 0
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r2, r4, r3, r3)     // Catch:{ Exception -> 0x019e }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x019e }
            int r2 = r2.size()     // Catch:{ Exception -> 0x019e }
            r4 = 5
            if (r2 <= r4) goto L_0x019a
            r15.contactsToSend = r3     // Catch:{ Exception -> 0x019e }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x019e }
            r2.<init>()     // Catch:{ Exception -> 0x019e }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x019e }
            r2.add(r0)     // Catch:{ Exception -> 0x019e }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x019e }
            goto L_0x0334
        L_0x019a:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x019e }
            goto L_0x0334
        L_0x019e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a2:
            r0 = 1
            goto L_0x0335
        L_0x01a5:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x01b9
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x01b9
            java.lang.String r0 = r4.toString()
        L_0x01b9:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r0)
            if (r5 != 0) goto L_0x01f0
            java.lang.String r5 = "http://"
            boolean r5 = r0.startsWith(r5)
            if (r5 != 0) goto L_0x01d5
            java.lang.String r5 = "https://"
            boolean r5 = r0.startsWith(r5)
            if (r5 == 0) goto L_0x01ed
        L_0x01d5:
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 != 0) goto L_0x01ed
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r2)
            r5.append(r0)
            java.lang.String r0 = r5.toString()
        L_0x01ed:
            r15.sendingText = r0
            goto L_0x01f8
        L_0x01f0:
            boolean r0 = android.text.TextUtils.isEmpty(r4)
            if (r0 != 0) goto L_0x01f8
            r15.sendingText = r4
        L_0x01f8:
            android.os.Parcelable r0 = r14.getParcelableExtra(r3)
            if (r0 == 0) goto L_0x032e
            boolean r2 = r0 instanceof android.net.Uri
            if (r2 != 0) goto L_0x020a
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x020a:
            r2 = r0
            android.net.Uri r2 = (android.net.Uri) r2
            if (r2 == 0) goto L_0x0217
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r2)
            if (r0 == 0) goto L_0x0217
            r3 = 1
            goto L_0x0218
        L_0x0217:
            r3 = 0
        L_0x0218:
            if (r3 != 0) goto L_0x032c
            if (r2 == 0) goto L_0x032c
            if (r1 == 0) goto L_0x0226
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)
            if (r0 != 0) goto L_0x0236
        L_0x0226:
            java.lang.String r0 = r2.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r4 = ".jpg"
            boolean r0 = r0.endsWith(r4)
            if (r0 == 0) goto L_0x024f
        L_0x0236:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x0241
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x0241:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r2
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x032c
        L_0x024f:
            java.lang.String r4 = r2.toString()
            r9 = 0
            int r0 = (r20 > r9 ? 1 : (r20 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x02ca
            if (r4 == 0) goto L_0x02ca
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0273
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "export path = "
            r0.append(r5)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0273:
            r5 = 0
            r0 = r11[r5]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r0 = r0.exportUri
            java.lang.String r5 = org.telegram.messenger.MediaController.getFileName(r2)
            java.lang.String r5 = org.telegram.messenger.FileLoader.fixFileName(r5)
            java.util.Iterator r7 = r0.iterator()
        L_0x0288:
            boolean r0 = r7.hasNext()
            if (r0 == 0) goto L_0x02b4
            java.lang.Object r0 = r7.next()
            java.lang.String r0 = (java.lang.String) r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x02af }
            java.util.regex.Matcher r9 = r0.matcher(r4)     // Catch:{ Exception -> 0x02af }
            boolean r9 = r9.find()     // Catch:{ Exception -> 0x02af }
            if (r9 != 0) goto L_0x02ac
            java.util.regex.Matcher r0 = r0.matcher(r5)     // Catch:{ Exception -> 0x02af }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x02af }
            if (r0 == 0) goto L_0x0288
        L_0x02ac:
            r15.exportingChatUri = r2     // Catch:{ Exception -> 0x02af }
            goto L_0x02b4
        L_0x02af:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0288
        L_0x02b4:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x02ca
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r4.startsWith(r0)
            if (r0 == 0) goto L_0x02ca
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r4.endsWith(r0)
            if (r0 == 0) goto L_0x02ca
            r15.exportingChatUri = r2
        L_0x02ca:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x032c
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            boolean r4 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r4 != 0) goto L_0x02dc
            java.lang.String r0 = "file"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
        L_0x02dc:
            if (r0 == 0) goto L_0x031a
            java.lang.String r4 = "file:"
            boolean r4 = r0.startsWith(r4)
            if (r4 == 0) goto L_0x02ec
            java.lang.String r4 = "file://"
            java.lang.String r0 = r0.replace(r4, r13)
        L_0x02ec:
            if (r1 == 0) goto L_0x02f9
            java.lang.String r4 = "video/"
            boolean r1 = r1.startsWith(r4)
            if (r1 == 0) goto L_0x02f9
            r15.videoPath = r0
            goto L_0x032c
        L_0x02f9:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            if (r1 != 0) goto L_0x030b
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsPathsArray = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsOriginalPathsArray = r1
        L_0x030b:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            r1.add(r0)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r2.toString()
            r0.add(r1)
            goto L_0x032c
        L_0x031a:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            if (r0 != 0) goto L_0x0325
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsUrisArray = r0
        L_0x0325:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            r0.add(r2)
            r15.documentsMimeType = r1
        L_0x032c:
            r0 = r3
            goto L_0x0335
        L_0x032e:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x0334
            goto L_0x01a2
        L_0x0334:
            r0 = 0
        L_0x0335:
            if (r0 == 0) goto L_0x0341
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x0341:
            r76 = r8
            r7 = r15
            r74 = r20
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r8 = 0
            r10 = 0
            r12 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r79 = 0
            r80 = 0
            r81 = 0
            r82 = 0
            r83 = r14
            r14 = r11
            r11 = r83
            goto L_0x1da0
        L_0x0375:
            java.lang.String r0 = r85.getAction()
            java.lang.String r4 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x03ab
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r3)     // Catch:{ all -> 0x0398 }
            r15.importingStickers = r0     // Catch:{ all -> 0x0398 }
            java.lang.String r0 = "STICKER_EMOJIS"
            java.util.ArrayList r0 = r14.getStringArrayListExtra(r0)     // Catch:{ all -> 0x0398 }
            r15.importingStickersEmoji = r0     // Catch:{ all -> 0x0398 }
            java.lang.String r0 = "IMPORTER"
            java.lang.String r0 = r14.getStringExtra(r0)     // Catch:{ all -> 0x0398 }
            r15.importingStickersSoftware = r0     // Catch:{ all -> 0x0398 }
            goto L_0x03a3
        L_0x0398:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r15.importingStickers = r1
            r15.importingStickersEmoji = r1
            r15.importingStickersSoftware = r1
        L_0x03a3:
            r76 = r8
            r7 = r15
            r1 = 2
            r2 = 0
            goto L_0x1d70
        L_0x03ab:
            java.lang.String r0 = r85.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x052e
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r3)     // Catch:{ Exception -> 0x051b }
            java.lang.String r1 = r85.getType()     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x03f4
            r2 = 0
        L_0x03c2:
            int r3 = r0.size()     // Catch:{ Exception -> 0x051b }
            if (r2 >= r3) goto L_0x03ec
            java.lang.Object r3 = r0.get(r2)     // Catch:{ Exception -> 0x051b }
            android.os.Parcelable r3 = (android.os.Parcelable) r3     // Catch:{ Exception -> 0x051b }
            boolean r4 = r3 instanceof android.net.Uri     // Catch:{ Exception -> 0x051b }
            if (r4 != 0) goto L_0x03da
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x051b }
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x051b }
        L_0x03da:
            android.net.Uri r3 = (android.net.Uri) r3     // Catch:{ Exception -> 0x051b }
            if (r3 == 0) goto L_0x03e9
            boolean r3 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r3)     // Catch:{ Exception -> 0x051b }
            if (r3 == 0) goto L_0x03e9
            r0.remove(r2)     // Catch:{ Exception -> 0x051b }
            int r2 = r2 + -1
        L_0x03e9:
            r3 = 1
            int r2 = r2 + r3
            goto L_0x03c2
        L_0x03ec:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x051b }
            if (r2 == 0) goto L_0x03f4
            r2 = 0
            goto L_0x03f5
        L_0x03f4:
            r2 = r0
        L_0x03f5:
            if (r2 == 0) goto L_0x051f
            if (r1 == 0) goto L_0x0436
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x0436
            r0 = 0
        L_0x0402:
            int r1 = r2.size()     // Catch:{ Exception -> 0x051b }
            if (r0 >= r1) goto L_0x0519
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x051b }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x051b }
            boolean r3 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x051b }
            if (r3 != 0) goto L_0x041a
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x051b }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x051b }
        L_0x041a:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x051b }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x051b }
            if (r3 != 0) goto L_0x0427
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x051b }
            r3.<init>()     // Catch:{ Exception -> 0x051b }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x051b }
        L_0x0427:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x051b }
            r3.<init>()     // Catch:{ Exception -> 0x051b }
            r3.uri = r1     // Catch:{ Exception -> 0x051b }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x051b }
            r1.add(r3)     // Catch:{ Exception -> 0x051b }
            int r0 = r0 + 1
            goto L_0x0402
        L_0x0436:
            r3 = 0
            r0 = r11[r3]     // Catch:{ Exception -> 0x051b }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x051b }
            java.util.Set<java.lang.String> r3 = r0.exportUri     // Catch:{ Exception -> 0x051b }
            r4 = 0
        L_0x0440:
            int r0 = r2.size()     // Catch:{ Exception -> 0x051b }
            if (r4 >= r0) goto L_0x0519
            java.lang.Object r0 = r2.get(r4)     // Catch:{ Exception -> 0x051b }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x051b }
            boolean r5 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x051b }
            if (r5 != 0) goto L_0x0458
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x051b }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x051b }
        L_0x0458:
            r5 = r0
            android.net.Uri r5 = (android.net.Uri) r5     // Catch:{ Exception -> 0x051b }
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.getPath(r5)     // Catch:{ Exception -> 0x051b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x051b }
            if (r0 != 0) goto L_0x0467
            r9 = r7
            goto L_0x0468
        L_0x0467:
            r9 = r0
        L_0x0468:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x0480
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x051b }
            r0.<init>()     // Catch:{ Exception -> 0x051b }
            java.lang.String r10 = "export path = "
            r0.append(r10)     // Catch:{ Exception -> 0x051b }
            r0.append(r9)     // Catch:{ Exception -> 0x051b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x051b }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x051b }
        L_0x0480:
            if (r9 == 0) goto L_0x04d6
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x051b }
            if (r0 != 0) goto L_0x04d6
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r5)     // Catch:{ Exception -> 0x051b }
            java.lang.String r10 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x051b }
            java.util.Iterator r12 = r3.iterator()     // Catch:{ Exception -> 0x051b }
        L_0x0492:
            boolean r0 = r12.hasNext()     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x04bf
            java.lang.Object r0 = r12.next()     // Catch:{ Exception -> 0x051b }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x051b }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x04ba }
            java.util.regex.Matcher r20 = r0.matcher(r9)     // Catch:{ Exception -> 0x04ba }
            boolean r20 = r20.find()     // Catch:{ Exception -> 0x04ba }
            if (r20 != 0) goto L_0x04b6
            java.util.regex.Matcher r0 = r0.matcher(r10)     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0492
        L_0x04b6:
            r15.exportingChatUri = r5     // Catch:{ Exception -> 0x04ba }
            r0 = 1
            goto L_0x04c0
        L_0x04ba:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x051b }
            goto L_0x0492
        L_0x04bf:
            r0 = 0
        L_0x04c0:
            if (r0 == 0) goto L_0x04c3
            goto L_0x0515
        L_0x04c3:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r9.startsWith(r0)     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x04d6
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r9.endsWith(r0)     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x04d6
            r15.exportingChatUri = r5     // Catch:{ Exception -> 0x051b }
            goto L_0x0515
        L_0x04d6:
            if (r7 == 0) goto L_0x0503
            java.lang.String r0 = "file:"
            boolean r0 = r7.startsWith(r0)     // Catch:{ Exception -> 0x051b }
            if (r0 == 0) goto L_0x04e6
            java.lang.String r0 = "file://"
            java.lang.String r7 = r7.replace(r0, r13)     // Catch:{ Exception -> 0x051b }
        L_0x04e6:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x051b }
            if (r0 != 0) goto L_0x04f8
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x051b }
            r0.<init>()     // Catch:{ Exception -> 0x051b }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x051b }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x051b }
            r0.<init>()     // Catch:{ Exception -> 0x051b }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x051b }
        L_0x04f8:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x051b }
            r0.add(r7)     // Catch:{ Exception -> 0x051b }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x051b }
            r0.add(r9)     // Catch:{ Exception -> 0x051b }
            goto L_0x0515
        L_0x0503:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x051b }
            if (r0 != 0) goto L_0x050e
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x051b }
            r0.<init>()     // Catch:{ Exception -> 0x051b }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x051b }
        L_0x050e:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x051b }
            r0.add(r5)     // Catch:{ Exception -> 0x051b }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x051b }
        L_0x0515:
            int r4 = r4 + 1
            goto L_0x0440
        L_0x0519:
            r0 = 0
            goto L_0x0520
        L_0x051b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x051f:
            r0 = 1
        L_0x0520:
            if (r0 == 0) goto L_0x03a3
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
            goto L_0x03a3
        L_0x052e:
            java.lang.String r0 = r85.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x1bc5
            android.net.Uri r0 = r85.getData()
            if (r0 == 0) goto L_0x1b75
            java.lang.String r3 = r0.getScheme()
            java.lang.String r4 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r5 = "phone"
            if (r3 == 0) goto L_0x18e0
            int r7 = r3.hashCode()
            switch(r7) {
                case 3699: goto L_0x0569;
                case 3213448: goto L_0x055e;
                case 99617003: goto L_0x0553;
                default: goto L_0x0551;
            }
        L_0x0551:
            r7 = -1
            goto L_0x0573
        L_0x0553:
            java.lang.String r7 = "https"
            boolean r7 = r3.equals(r7)
            if (r7 != 0) goto L_0x055c
            goto L_0x0551
        L_0x055c:
            r7 = 2
            goto L_0x0573
        L_0x055e:
            java.lang.String r7 = "http"
            boolean r7 = r3.equals(r7)
            if (r7 != 0) goto L_0x0567
            goto L_0x0551
        L_0x0567:
            r7 = 1
            goto L_0x0573
        L_0x0569:
            java.lang.String r7 = "tg"
            boolean r7 = r3.equals(r7)
            if (r7 != 0) goto L_0x0572
            goto L_0x0551
        L_0x0572:
            r7 = 0
        L_0x0573:
            r9 = 16
            switch(r7) {
                case 0: goto L_0x0bba;
                case 1: goto L_0x057a;
                case 2: goto L_0x057a;
                default: goto L_0x0578;
            }
        L_0x0578:
            goto L_0x18e0
        L_0x057a:
            java.lang.String r7 = r0.getHost()
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r10 = "telegram.me"
            boolean r10 = r7.equals(r10)
            if (r10 != 0) goto L_0x059a
            java.lang.String r10 = "t.me"
            boolean r10 = r7.equals(r10)
            if (r10 != 0) goto L_0x059a
            java.lang.String r10 = "telegram.dog"
            boolean r7 = r7.equals(r10)
            if (r7 == 0) goto L_0x18e0
        L_0x059a:
            java.lang.String r7 = r0.getPath()
            if (r7 == 0) goto L_0x0b2e
            int r10 = r7.length()
            r12 = 1
            if (r10 <= r12) goto L_0x0b2e
            java.lang.String r7 = r7.substring(r12)
            java.lang.String r10 = "$"
            boolean r10 = r7.startsWith(r10)
            if (r10 == 0) goto L_0x05db
            java.lang.String r0 = r7.substring(r12)
        L_0x05b7:
            r37 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            goto L_0x0b50
        L_0x05db:
            java.lang.String r10 = "invoice/"
            boolean r10 = r7.startsWith(r10)
            if (r10 == 0) goto L_0x05ef
            r0 = 47
            int r0 = r7.indexOf(r0)
            int r0 = r0 + r12
            java.lang.String r0 = r7.substring(r0)
            goto L_0x05b7
        L_0x05ef:
            java.lang.String r10 = "bg/"
            boolean r10 = r7.startsWith(r10)
            if (r10 == 0) goto L_0x07fa
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "bg/"
            java.lang.String r2 = r7.replace(r2, r13)
            r1.slug = r2
            if (r2 == 0) goto L_0x0627
            int r2 = r2.length()
            r7 = 6
            if (r2 != r7) goto L_0x0627
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x06db }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x06db }
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x06db }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r10
            r2.background_color = r7     // Catch:{ Exception -> 0x06db }
            r2 = 0
            r1.slug = r2     // Catch:{ Exception -> 0x06db }
        L_0x0624:
            r2 = 1
            goto L_0x06dc
        L_0x0627:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x06db
            int r2 = r2.length()
            r7 = 13
            if (r2 < r7) goto L_0x06db
            java.lang.String r2 = r1.slug
            r7 = 6
            char r2 = r2.charAt(r7)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)
            if (r2 == 0) goto L_0x06db
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x06db }
            java.lang.String r10 = r1.slug     // Catch:{ Exception -> 0x06db }
            r12 = 0
            java.lang.String r10 = r10.substring(r12, r7)     // Catch:{ Exception -> 0x06db }
            int r7 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x06db }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r10
            r2.background_color = r7     // Catch:{ Exception -> 0x06db }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x06db }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x06db }
            r10 = 7
            r12 = 13
            java.lang.String r7 = r7.substring(r10, r12)     // Catch:{ Exception -> 0x06db }
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x06db }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r10
            r2.second_background_color = r7     // Catch:{ Exception -> 0x06db }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x06db }
            int r2 = r2.length()     // Catch:{ Exception -> 0x06db }
            r7 = 20
            if (r2 < r7) goto L_0x0693
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x06db }
            r7 = 13
            char r2 = r2.charAt(r7)     // Catch:{ Exception -> 0x06db }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x06db }
            if (r2 == 0) goto L_0x0693
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x06db }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x06db }
            r10 = 14
            r12 = 20
            java.lang.String r7 = r7.substring(r10, r12)     // Catch:{ Exception -> 0x06db }
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x06db }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r10
            r2.third_background_color = r7     // Catch:{ Exception -> 0x06db }
        L_0x0693:
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x06db }
            int r2 = r2.length()     // Catch:{ Exception -> 0x06db }
            r7 = 27
            if (r2 != r7) goto L_0x06be
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x06db }
            r7 = 20
            char r2 = r2.charAt(r7)     // Catch:{ Exception -> 0x06db }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x06db }
            if (r2 == 0) goto L_0x06be
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x06db }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x06db }
            r10 = 21
            java.lang.String r7 = r7.substring(r10)     // Catch:{ Exception -> 0x06db }
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x06db }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r10
            r2.fourth_background_color = r7     // Catch:{ Exception -> 0x06db }
        L_0x06be:
            java.lang.String r2 = "rotation"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x06d6 }
            boolean r7 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x06d6 }
            if (r7 != 0) goto L_0x06d6
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x06d6 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ Exception -> 0x06d6 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x06d6 }
            r7.rotation = r2     // Catch:{ Exception -> 0x06d6 }
        L_0x06d6:
            r2 = 0
            r1.slug = r2     // Catch:{ Exception -> 0x06db }
            goto L_0x0624
        L_0x06db:
            r2 = 0
        L_0x06dc:
            if (r2 != 0) goto L_0x07d7
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x0719
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String[] r2 = r2.split(r8)
            if (r2 == 0) goto L_0x0719
            int r7 = r2.length
            if (r7 <= 0) goto L_0x0719
            r7 = 0
        L_0x06f4:
            int r10 = r2.length
            if (r7 >= r10) goto L_0x0719
            r10 = r2[r7]
            java.lang.String r12 = "blur"
            boolean r10 = r12.equals(r10)
            if (r10 == 0) goto L_0x0707
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r12 = 1
            r10.blur = r12
            goto L_0x0716
        L_0x0707:
            r12 = 1
            r10 = r2[r7]
            java.lang.String r13 = "motion"
            boolean r10 = r13.equals(r10)
            if (r10 == 0) goto L_0x0716
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r10.motion = r12
        L_0x0716:
            int r7 = r7 + 1
            goto L_0x06f4
        L_0x0719:
            java.lang.String r2 = "intensity"
            java.lang.String r2 = r0.getQueryParameter(r2)
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x0732
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r2 = r2.intValue()
            r7.intensity = r2
            goto L_0x0738
        L_0x0732:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            r7 = 50
            r2.intensity = r7
        L_0x0738:
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x07b7 }
            boolean r7 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x07b7 }
            if (r7 != 0) goto L_0x07b9
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x07b7 }
            r10 = 6
            r12 = 0
            java.lang.String r13 = r2.substring(r12, r10)     // Catch:{ Exception -> 0x07b7 }
            int r10 = java.lang.Integer.parseInt(r13, r9)     // Catch:{ Exception -> 0x07b7 }
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r12
            r7.background_color = r10     // Catch:{ Exception -> 0x07b7 }
            int r7 = r2.length()     // Catch:{ Exception -> 0x07b7 }
            r10 = 13
            if (r7 < r10) goto L_0x07b7
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x07b7 }
            r12 = 7
            java.lang.String r12 = r2.substring(r12, r10)     // Catch:{ Exception -> 0x07b7 }
            int r10 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x07b7 }
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r12
            r7.second_background_color = r10     // Catch:{ Exception -> 0x07b7 }
            int r7 = r2.length()     // Catch:{ Exception -> 0x07b7 }
            r10 = 20
            if (r7 < r10) goto L_0x0792
            r7 = 13
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x07b7 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x07b7 }
            if (r7 == 0) goto L_0x0792
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x07b7 }
            r12 = 14
            java.lang.String r12 = r2.substring(r12, r10)     // Catch:{ Exception -> 0x07b7 }
            int r10 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x07b7 }
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r12
            r7.third_background_color = r10     // Catch:{ Exception -> 0x07b7 }
        L_0x0792:
            int r7 = r2.length()     // Catch:{ Exception -> 0x07b7 }
            r10 = 27
            if (r7 != r10) goto L_0x07b7
            r7 = 20
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x07b7 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x07b7 }
            if (r7 == 0) goto L_0x07b7
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x07b7 }
            r10 = 21
            java.lang.String r2 = r2.substring(r10)     // Catch:{ Exception -> 0x07b7 }
            int r2 = java.lang.Integer.parseInt(r2, r9)     // Catch:{ Exception -> 0x07b7 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r9
            r7.fourth_background_color = r2     // Catch:{ Exception -> 0x07b7 }
        L_0x07b7:
            r10 = -1
            goto L_0x07be
        L_0x07b9:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x07b7 }
            r10 = -1
            r2.background_color = r10     // Catch:{ Exception -> 0x07be }
        L_0x07be:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x07d8 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x07d8 }
            if (r2 != 0) goto L_0x07d8
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x07d8 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x07d8 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x07d8 }
            r2.rotation = r0     // Catch:{ Exception -> 0x07d8 }
            goto L_0x07d8
        L_0x07d7:
            r10 = -1
        L_0x07d8:
            r36 = r1
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            goto L_0x0b4e
        L_0x07fa:
            r10 = -1
            java.lang.String r9 = "login/"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0844
            java.lang.String r0 = "login/"
            java.lang.String r0 = r7.replace(r0, r13)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0823
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0824
        L_0x0823:
            r0 = 0
        L_0x0824:
            r35 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            goto L_0x0b4c
        L_0x0844:
            java.lang.String r9 = "joinchat/"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0854
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r7.replace(r0, r13)
            goto L_0x0b2f
        L_0x0854:
            java.lang.String r9 = "+"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x086d
            java.lang.String r0 = "+"
            java.lang.String r0 = r7.replace(r0, r13)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isNumeric(r0)
            if (r1 == 0) goto L_0x0b2f
            r2 = r0
            r0 = 0
            r1 = 0
            goto L_0x0b31
        L_0x086d:
            java.lang.String r9 = "addstickers/"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0882
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r7.replace(r0, r13)
            r9 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            goto L_0x0b33
        L_0x0882:
            java.lang.String r9 = "addemoji/"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0898
            java.lang.String r0 = "addemoji/"
            java.lang.String r0 = r7.replace(r0, r13)
            r10 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            goto L_0x0b34
        L_0x0898:
            java.lang.String r9 = "msg/"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x0abc
            java.lang.String r9 = "share/"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x08aa
            goto L_0x0abc
        L_0x08aa:
            java.lang.String r2 = "confirmphone"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x08d4
            java.lang.String r2 = r0.getQueryParameter(r5)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r31 = r0
            r27 = r2
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0b46
        L_0x08d4:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x08fe
            r0 = 12
            java.lang.String r0 = r7.substring(r0)
            r32 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            goto L_0x0b48
        L_0x08fe:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x092a
            r0 = 9
            java.lang.String r0 = r7.substring(r0)
            r34 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0b4a
        L_0x092a:
            java.lang.String r1 = "c/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x09a5
            java.util.List r1 = r0.getPathSegments()
            int r2 = r1.size()
            r12 = 3
            if (r2 != r12) goto L_0x0977
            r2 = 1
            java.lang.Object r7 = r1.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r7)
            r9 = 2
            java.lang.Object r1 = r1.get(r9)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r7 = r1.intValue()
            if (r7 == 0) goto L_0x0963
            long r16 = r2.longValue()
            r20 = 0
            int r7 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1))
            if (r7 != 0) goto L_0x0965
        L_0x0963:
            r1 = 0
            r2 = 0
        L_0x0965:
            java.lang.String r7 = "thread"
            java.lang.String r0 = r0.getQueryParameter(r7)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r7 = r0.intValue()
            if (r7 != 0) goto L_0x097b
            r0 = 0
            goto L_0x097b
        L_0x0977:
            r9 = 2
            r0 = 0
            r1 = 0
            r2 = 0
        L_0x097b:
            r40 = r0
            r38 = r1
            r39 = r2
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            goto L_0x0b56
        L_0x09a5:
            r9 = 2
            r12 = 3
            int r1 = r7.length()
            r2 = 1
            if (r1 < r2) goto L_0x0b2e
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r2 = r0.getPathSegments()
            r1.<init>(r2)
            int r2 = r1.size()
            if (r2 <= 0) goto L_0x09d0
            r2 = 0
            java.lang.Object r7 = r1.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.String r13 = "s"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x09d1
            r1.remove(r2)
            goto L_0x09d1
        L_0x09d0:
            r2 = 0
        L_0x09d1:
            int r7 = r1.size()
            if (r7 <= 0) goto L_0x09f7
            java.lang.Object r7 = r1.get(r2)
            r2 = r7
            java.lang.String r2 = (java.lang.String) r2
            int r7 = r1.size()
            r13 = 1
            if (r7 <= r13) goto L_0x09f5
            java.lang.Object r1 = r1.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r7 = r1.intValue()
            if (r7 != 0) goto L_0x09f9
        L_0x09f5:
            r1 = 0
            goto L_0x09f9
        L_0x09f7:
            r1 = 0
            r2 = 0
        L_0x09f9:
            if (r1 == 0) goto L_0x0a00
            int r7 = getTimestampFromLink(r0)
            goto L_0x0a01
        L_0x0a00:
            r7 = -1
        L_0x0a01:
            java.lang.String r13 = "start"
            java.lang.String r13 = r0.getQueryParameter(r13)
            java.lang.String r9 = "startgroup"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "startchannel"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r12 = "admin"
            java.lang.String r12 = r0.getQueryParameter(r12)
            r87 = r1
            java.lang.String r1 = "game"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r20 = r1
            java.lang.String r1 = "voicechat"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r21 = r1
            java.lang.String r1 = "livestream"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r24 = r1
            java.lang.String r1 = "startattach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r27 = r1
            java.lang.String r1 = "choose"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r28 = r1
            java.lang.String r1 = "attach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r29 = r1
            java.lang.String r1 = "thread"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r30 = r1.intValue()
            if (r30 != 0) goto L_0x0a5e
            r30 = 0
            goto L_0x0a60
        L_0x0a5e:
            r30 = r1
        L_0x0a60:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r1 = r0.intValue()
            r38 = r87
            if (r1 != 0) goto L_0x0a8f
            r42 = r27
            r44 = r28
            r43 = r29
            r40 = r30
            r0 = 0
            r1 = 0
            r27 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r41 = 0
            goto L_0x0aab
        L_0x0a8f:
            r41 = r0
            r42 = r27
            r44 = r28
            r43 = r29
            r40 = r30
            r0 = 0
            r1 = 0
            r27 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r39 = 0
        L_0x0aab:
            r28 = r20
            r29 = r21
            r30 = r24
            r24 = 0
            r20 = r10
            r21 = r12
            r10 = 0
            r12 = r9
            r9 = 0
            goto L_0x0b5e
        L_0x0abc:
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x0ac5
            goto L_0x0ac6
        L_0x0ac5:
            r13 = r1
        L_0x0ac6:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x0afc
            int r1 = r13.length()
            if (r1 <= 0) goto L_0x0ae5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            r1.append(r2)
            java.lang.String r13 = r1.toString()
            r1 = 1
            goto L_0x0ae6
        L_0x0ae5:
            r1 = 0
        L_0x0ae6:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r13)
            java.lang.String r9 = "text"
            java.lang.String r0 = r0.getQueryParameter(r9)
            r7.append(r0)
            java.lang.String r13 = r7.toString()
            goto L_0x0afd
        L_0x0afc:
            r1 = 0
        L_0x0afd:
            int r0 = r13.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r7) goto L_0x0b0e
            r0 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r0 = r13.substring(r7, r0)
            r13 = r0
            goto L_0x0b0f
        L_0x0b0e:
            r7 = 0
        L_0x0b0f:
            boolean r0 = r13.endsWith(r2)
            if (r0 == 0) goto L_0x0b20
            int r0 = r13.length()
            r9 = 1
            int r0 = r0 - r9
            java.lang.String r13 = r13.substring(r7, r0)
            goto L_0x0b0f
        L_0x0b20:
            r24 = r13
            r0 = 0
            r2 = 0
            r7 = -1
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            goto L_0x0b3c
        L_0x0b2e:
            r0 = 0
        L_0x0b2f:
            r1 = 0
            r2 = 0
        L_0x0b31:
            r7 = -1
            r9 = 0
        L_0x0b33:
            r10 = 0
        L_0x0b34:
            r12 = 0
            r13 = 0
            r20 = 0
            r21 = 0
            r24 = 0
        L_0x0b3c:
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
        L_0x0b46:
            r32 = 0
        L_0x0b48:
            r34 = 0
        L_0x0b4a:
            r35 = 0
        L_0x0b4c:
            r36 = 0
        L_0x0b4e:
            r37 = 0
        L_0x0b50:
            r38 = 0
            r39 = 0
            r40 = 0
        L_0x0b56:
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
        L_0x0b5e:
            r63 = r7
            r25 = r12
            r26 = r20
            r52 = r28
            r61 = r29
            r62 = r30
            r54 = r32
            r60 = r34
            r56 = r35
            r58 = r36
            r59 = r37
            r30 = r38
            r32 = r40
            r51 = r41
            r64 = r42
            r65 = r43
            r66 = r44
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r53 = 0
            r55 = 0
            r57 = 0
            r7 = r0
            r29 = r1
            r12 = r2
            r0 = r27
            r1 = r31
            r31 = r39
            r39 = 0
            r27 = r21
            r20 = 0
            r83 = r24
            r24 = r13
            r13 = r83
            goto L_0x1937
        L_0x0bba:
            r7 = 2
            java.lang.String r10 = r0.toString()
            java.lang.String r12 = "tg:premium_offer"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x18d5
            java.lang.String r12 = "tg://premium_offer"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0bd1
            goto L_0x18d5
        L_0x0bd1:
            java.lang.String r12 = "tg:resolve"
            boolean r12 = r10.startsWith(r12)
            java.lang.String r7 = "scope"
            java.lang.String r9 = "tg://telegram.org"
            if (r12 != 0) goto L_0x1703
            java.lang.String r12 = "tg://resolve"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0be7
            goto L_0x1703
        L_0x0be7:
            java.lang.String r12 = "tg:invoice"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1697
            java.lang.String r12 = "tg://invoice"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0bf9
            goto L_0x1697
        L_0x0bf9:
            java.lang.String r12 = "tg:privatepost"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x15f4
            java.lang.String r12 = "tg://privatepost"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0c0b
            goto L_0x15f4
        L_0x0c0b:
            java.lang.String r12 = "tg:bg"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x139e
            java.lang.String r12 = "tg://bg"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0c1d
            goto L_0x139e
        L_0x0c1d:
            java.lang.String r12 = "tg:join"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1351
            java.lang.String r12 = "tg://join"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0c2f
            goto L_0x1351
        L_0x0c2f:
            java.lang.String r12 = "tg:addstickers"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1336
            java.lang.String r12 = "tg://addstickers"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0CLASSNAME
            goto L_0x1336
        L_0x0CLASSNAME:
            java.lang.String r12 = "tg:addemoji"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x131a
            java.lang.String r12 = "tg://addemoji"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0CLASSNAME
            goto L_0x131a
        L_0x0CLASSNAME:
            java.lang.String r12 = "tg:msg"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1287
            java.lang.String r12 = "tg://msg"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1287
            java.lang.String r12 = "tg://share"
            boolean r12 = r10.startsWith(r12)
            if (r12 != 0) goto L_0x1287
            java.lang.String r12 = "tg:share"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0CLASSNAME
            goto L_0x1287
        L_0x0CLASSNAME:
            java.lang.String r2 = "tg:confirmphone"
            boolean r2 = r10.startsWith(r2)
            if (r2 != 0) goto L_0x126a
            java.lang.String r2 = "tg://confirmphone"
            boolean r2 = r10.startsWith(r2)
            if (r2 == 0) goto L_0x0CLASSNAME
            goto L_0x126a
        L_0x0CLASSNAME:
            java.lang.String r1 = "tg:login"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x11ec
            java.lang.String r1 = "tg://login"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0CLASSNAME
            goto L_0x11ec
        L_0x0CLASSNAME:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x118e
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0cab
            goto L_0x118e
        L_0x0cab:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x10df
            java.lang.String r1 = "tg://passport"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x10df
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0cc5
            goto L_0x10df
        L_0x0cc5:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x1088
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0cd7
            goto L_0x1088
        L_0x0cd7:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x1025
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0ce9
            goto L_0x1025
        L_0x0ce9:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://settings"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0cfb
            goto L_0x0var_
        L_0x0cfb:
            java.lang.String r1 = "tg:search"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://search"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0d0d
            goto L_0x0var_
        L_0x0d0d:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x0ef2
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0d1f
            goto L_0x0ef2
        L_0x0d1f:
            java.lang.String r1 = "tg:call"
            boolean r1 = r10.startsWith(r1)
            if (r1 != 0) goto L_0x0e2a
            java.lang.String r1 = "tg://call"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x0d31
            goto L_0x0e2a
        L_0x0d31:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r10.startsWith(r0)
            if (r0 != 0) goto L_0x0dfd
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r10.startsWith(r0)
            if (r0 == 0) goto L_0x0d43
            goto L_0x0dfd
        L_0x0d43:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r10.startsWith(r0)
            if (r0 != 0) goto L_0x0db0
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r10.startsWith(r0)
            if (r0 == 0) goto L_0x0d54
            goto L_0x0db0
        L_0x0d54:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r10.replace(r0, r13)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r13)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0d6d
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r1)
        L_0x0d6d:
            r55 = r0
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            goto L_0x1921
        L_0x0db0:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r5)
            r45 = r0
            r44 = r1
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r40 = 1
            r41 = 0
            r42 = 0
            r43 = 0
            goto L_0x1398
        L_0x0dfd:
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r42 = 1
            goto L_0x1392
        L_0x0e2a:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x18e0
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0e6c
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x0e49
            goto L_0x0e6c
        L_0x0e49:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r4)
            java.lang.String r1 = "extra_force_call"
            r2 = 1
            r0.putExtra(r1, r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65
            r1.<init>(r15, r0)
            r9 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r9)
            r0 = 0
            r1 = 0
            r2 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x0e69:
            r20 = 0
            goto L_0x0ebd
        L_0x0e6c:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "name"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r5)
            r7 = 0
            java.util.List r9 = r15.findContacts(r2, r0, r7)
            boolean r10 = r9.isEmpty()
            if (r10 == 0) goto L_0x0e93
            if (r0 == 0) goto L_0x0e93
            r20 = r0
            r12 = r2
            r0 = 1
            r1 = 0
            r2 = 0
            r9 = 0
            r13 = 0
            goto L_0x0ebd
        L_0x0e93:
            int r0 = r9.size()
            r10 = 1
            if (r0 != r10) goto L_0x0ea3
            java.lang.Object r0 = r9.get(r7)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            long r9 = r0.user_id
            goto L_0x0ea5
        L_0x0ea3:
            r9 = 0
        L_0x0ea5:
            r20 = 0
            int r0 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r0 != 0) goto L_0x0eaf
            if (r2 == 0) goto L_0x0eb0
            r13 = r2
            goto L_0x0eb0
        L_0x0eaf:
            r13 = 0
        L_0x0eb0:
            java.lang.String r0 = "video"
            boolean r0 = r0.equalsIgnoreCase(r1)
            r1 = r0 ^ 1
            r2 = r0
            r0 = 0
            r7 = 1
            r12 = 0
            goto L_0x0e69
        L_0x0ebd:
            r41 = r0
            r37 = r1
            r38 = r2
            r39 = r7
            r46 = r9
            r44 = r12
            r43 = r13
            r45 = r20
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r40 = 0
            r42 = 0
            goto L_0x139a
        L_0x0ef2:
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            goto L_0x1386
        L_0x0var_:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0f2f
            java.lang.String r13 = r0.trim()
        L_0x0f2f:
            r50 = r13
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            goto L_0x1917
        L_0x0var_:
            java.lang.String r0 = "themes"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0f8f
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 2
            goto L_0x1384
        L_0x0f8f:
            java.lang.String r0 = "devices"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0fb6
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 3
            goto L_0x1384
        L_0x0fb6:
            java.lang.String r0 = "folders"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0fde
            r0 = 4
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 4
            goto L_0x1384
        L_0x0fde:
            java.lang.String r0 = "change_number"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x1006
            r0 = 5
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 5
            goto L_0x1384
        L_0x1006:
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 1
            goto L_0x1384
        L_0x1025:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r60 = r0
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r58 = 0
            r59 = 0
            goto L_0x192b
        L_0x1088:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r54 = r0
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            goto L_0x191f
        L_0x10df:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r9)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r7)
            boolean r9 = android.text.TextUtils.isEmpty(r2)
            if (r9 != 0) goto L_0x1120
            java.lang.String r9 = "{"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x1120
            java.lang.String r9 = "}"
            boolean r9 = r2.endsWith(r9)
            if (r9 == 0) goto L_0x1120
            java.lang.String r9 = "nonce"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "nonce"
            r1.put(r10, r9)
            goto L_0x112b
        L_0x1120:
            java.lang.String r9 = "payload"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "payload"
            r1.put(r10, r9)
        L_0x112b:
            java.lang.String r9 = "bot_id"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "bot_id"
            r1.put(r10, r9)
            r1.put(r7, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "public_key"
            r1.put(r7, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            r53 = r1
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            goto L_0x191d
        L_0x118e:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "chat_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r6)
            if (r1 == 0) goto L_0x11b7
            long r9 = java.lang.Long.parseLong(r1)     // Catch:{ NumberFormatException -> 0x11bf }
            r1 = 0
            goto L_0x11c3
        L_0x11b7:
            if (r2 == 0) goto L_0x11bf
            long r9 = java.lang.Long.parseLong(r2)     // Catch:{ NumberFormatException -> 0x11bf }
            r1 = r9
            goto L_0x11c1
        L_0x11bf:
            r1 = 0
        L_0x11c1:
            r9 = 0
        L_0x11c3:
            if (r0 == 0) goto L_0x11ca
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x11ca }
            goto L_0x11cb
        L_0x11ca:
            r0 = 0
        L_0x11cb:
            r34 = r0
            r48 = r1
            r46 = r9
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x18ff
        L_0x11ec:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "code"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x1222
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x1223
        L_0x1222:
            r0 = 0
        L_0x1223:
            r56 = r0
            r57 = r1
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
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
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            goto L_0x1925
        L_0x126a:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r2 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r2, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = r0.getQueryParameter(r5)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r1 = r0
            r0 = r2
            r7 = 0
            goto L_0x136a
        L_0x1287:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://msg"
            java.lang.String r0 = r0.replace(r1, r9)
            java.lang.String r1 = "tg://share"
            java.lang.String r0 = r0.replace(r1, r9)
            java.lang.String r1 = "tg:share"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x12ac
            goto L_0x12ad
        L_0x12ac:
            r13 = r1
        L_0x12ad:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x12e3
            int r1 = r13.length()
            if (r1 <= 0) goto L_0x12cc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            r1.append(r2)
            java.lang.String r13 = r1.toString()
            r1 = 1
            goto L_0x12cd
        L_0x12cc:
            r1 = 0
        L_0x12cd:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r13)
            java.lang.String r9 = "text"
            java.lang.String r0 = r0.getQueryParameter(r9)
            r7.append(r0)
            java.lang.String r13 = r7.toString()
            goto L_0x12e4
        L_0x12e3:
            r1 = 0
        L_0x12e4:
            int r0 = r13.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r7) goto L_0x12f5
            r0 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r0 = r13.substring(r7, r0)
            r13 = r0
            goto L_0x12f6
        L_0x12f5:
            r7 = 0
        L_0x12f6:
            boolean r0 = r13.endsWith(r2)
            if (r0 == 0) goto L_0x1307
            int r0 = r13.length()
            r9 = 1
            int r0 = r0 - r9
            java.lang.String r13 = r13.substring(r7, r0)
            goto L_0x12f6
        L_0x1307:
            r29 = r1
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            goto L_0x137a
        L_0x131a:
            java.lang.String r0 = "tg:addemoji"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://addemoji"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r10 = r0
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            goto L_0x136c
        L_0x1336:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r9 = r0
            r0 = 0
            r1 = 0
            r7 = 0
            goto L_0x136b
        L_0x1351:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r7 = r0
            r0 = 0
            r1 = 0
        L_0x136a:
            r9 = 0
        L_0x136b:
            r10 = 0
        L_0x136c:
            r12 = 0
            r13 = 0
            r20 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
        L_0x137a:
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
        L_0x1384:
            r36 = 0
        L_0x1386:
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
        L_0x1392:
            r43 = 0
            r44 = 0
            r45 = 0
        L_0x1398:
            r46 = 0
        L_0x139a:
            r48 = 0
            goto L_0x1915
        L_0x139e:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "slug"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
            if (r2 != 0) goto L_0x13cc
            java.lang.String r2 = "color"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
        L_0x13cc:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x13ed
            int r2 = r2.length()
            r7 = 6
            if (r2 != r7) goto L_0x13ed
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x14a9 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r9
            r2.background_color = r7     // Catch:{ Exception -> 0x14a9 }
            r2 = 0
            r1.slug = r2     // Catch:{ Exception -> 0x14a9 }
            r2 = 1
            r12 = 0
            goto L_0x14ab
        L_0x13ed:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x14a9
            int r2 = r2.length()
            r7 = 13
            if (r2 < r7) goto L_0x14a9
            java.lang.String r2 = r1.slug
            r7 = 6
            char r2 = r2.charAt(r7)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)
            if (r2 == 0) goto L_0x14a9
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r9 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r7)     // Catch:{ Exception -> 0x14a9 }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x14a9 }
            r7 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r9 = r9 | r7
            r2.background_color = r9     // Catch:{ Exception -> 0x14a9 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r9 = 7
            r10 = 13
            java.lang.String r7 = r7.substring(r9, r10)     // Catch:{ Exception -> 0x14a9 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x14a9 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r9
            r2.second_background_color = r7     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x14a9 }
            r7 = 20
            if (r2 < r7) goto L_0x145f
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r7 = 13
            char r2 = r2.charAt(r7)     // Catch:{ Exception -> 0x14a9 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x14a9 }
            if (r2 == 0) goto L_0x145f
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r9 = 14
            r10 = 20
            java.lang.String r7 = r7.substring(r9, r10)     // Catch:{ Exception -> 0x14a9 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x14a9 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r9
            r2.third_background_color = r7     // Catch:{ Exception -> 0x14a9 }
        L_0x145f:
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x14a9 }
            r7 = 27
            if (r2 != r7) goto L_0x148c
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r7 = 20
            char r2 = r2.charAt(r7)     // Catch:{ Exception -> 0x14a9 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x14a9 }
            if (r2 == 0) goto L_0x148c
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x14a9 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x14a9 }
            r9 = 21
            java.lang.String r7 = r7.substring(r9)     // Catch:{ Exception -> 0x14a9 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x14a9 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = r7 | r9
            r2.fourth_background_color = r7     // Catch:{ Exception -> 0x14a9 }
        L_0x148c:
            java.lang.String r2 = "rotation"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x14a4 }
            boolean r7 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x14a4 }
            if (r7 != 0) goto L_0x14a4
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x14a4 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ Exception -> 0x14a4 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x14a4 }
            r7.rotation = r2     // Catch:{ Exception -> 0x14a4 }
        L_0x14a4:
            r12 = 0
            r1.slug = r12     // Catch:{ Exception -> 0x14aa }
            r2 = 1
            goto L_0x14ab
        L_0x14a9:
            r12 = 0
        L_0x14aa:
            r2 = 0
        L_0x14ab:
            if (r2 != 0) goto L_0x159d
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x14e8
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String[] r2 = r2.split(r8)
            if (r2 == 0) goto L_0x14e8
            int r7 = r2.length
            if (r7 <= 0) goto L_0x14e8
            r7 = 0
        L_0x14c3:
            int r9 = r2.length
            if (r7 >= r9) goto L_0x14e8
            r9 = r2[r7]
            java.lang.String r10 = "blur"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x14d6
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r10 = 1
            r9.blur = r10
            goto L_0x14e5
        L_0x14d6:
            r10 = 1
            r9 = r2[r7]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x14e5
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r1.settings
            r9.motion = r10
        L_0x14e5:
            int r7 = r7 + 1
            goto L_0x14c3
        L_0x14e8:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            java.lang.String r7 = "intensity"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)
            int r7 = r7.intValue()
            r2.intensity = r7
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x1583 }
            boolean r7 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x1583 }
            if (r7 != 0) goto L_0x1583
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x1583 }
            r9 = 0
            r13 = 6
            java.lang.String r10 = r2.substring(r9, r13)     // Catch:{ Exception -> 0x1584 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r9
            r7.background_color = r10     // Catch:{ Exception -> 0x1584 }
            int r7 = r2.length()     // Catch:{ Exception -> 0x1584 }
            r9 = 13
            if (r7 < r9) goto L_0x1584
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x1584 }
            r10 = 8
            java.lang.String r10 = r2.substring(r10, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r9
            r7.second_background_color = r10     // Catch:{ Exception -> 0x1584 }
            int r7 = r2.length()     // Catch:{ Exception -> 0x1584 }
            r9 = 20
            if (r7 < r9) goto L_0x155b
            r7 = 13
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x1584 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x1584 }
            if (r7 == 0) goto L_0x155b
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x1584 }
            r10 = 14
            java.lang.String r10 = r2.substring(r10, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r9
            r7.third_background_color = r10     // Catch:{ Exception -> 0x1584 }
        L_0x155b:
            int r7 = r2.length()     // Catch:{ Exception -> 0x1584 }
            r9 = 27
            if (r7 != r9) goto L_0x1584
            r7 = 20
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x1584 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x1584 }
            if (r7 == 0) goto L_0x1584
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings     // Catch:{ Exception -> 0x1584 }
            r9 = 21
            java.lang.String r2 = r2.substring(r9)     // Catch:{ Exception -> 0x1584 }
            r9 = 16
            int r2 = java.lang.Integer.parseInt(r2, r9)     // Catch:{ Exception -> 0x1584 }
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r9
            r7.fourth_background_color = r2     // Catch:{ Exception -> 0x1584 }
            goto L_0x1584
        L_0x1583:
            r13 = 6
        L_0x1584:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x159e }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x159e }
            if (r2 != 0) goto L_0x159e
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x159e }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x159e }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x159e }
            r2.rotation = r0     // Catch:{ Exception -> 0x159e }
            goto L_0x159e
        L_0x159d:
            r13 = 6
        L_0x159e:
            r58 = r1
            r0 = r12
            r1 = r0
            r7 = r1
            r9 = r7
            r10 = r9
            r13 = r10
            r24 = r13
            r25 = r24
            r26 = r25
            r27 = r26
            r30 = r27
            r31 = r30
            r32 = r31
            r43 = r32
            r44 = r43
            r45 = r44
            r50 = r45
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
            r55 = r54
            r56 = r55
            r57 = r56
            r59 = r57
            r60 = r59
            r61 = r60
            r62 = r61
            r64 = r62
            r65 = r64
            r66 = r65
            r20 = 0
            r29 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r46 = 0
            r48 = 0
            goto L_0x16ff
        L_0x15f4:
            r12 = 0
            r13 = 6
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            java.lang.String r1 = "channel"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Long r1 = org.telegram.messenger.Utilities.parseLong(r1)
            int r2 = r7.intValue()
            if (r2 == 0) goto L_0x162b
            long r9 = r1.longValue()
            r20 = 0
            int r2 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r2 != 0) goto L_0x162f
            goto L_0x162d
        L_0x162b:
            r20 = 0
        L_0x162d:
            r1 = r12
            r7 = r1
        L_0x162f:
            java.lang.String r2 = "thread"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r9 = r2.intValue()
            if (r9 != 0) goto L_0x1640
            r2 = r12
        L_0x1640:
            java.lang.String r9 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x1671
            r31 = r1
            r32 = r2
            r30 = r7
            r0 = r12
            r1 = r0
            r7 = r1
            r9 = r7
            r10 = r9
            r13 = r10
            r24 = r13
            r25 = r24
            r26 = r25
            r27 = r26
            r43 = r27
            r44 = r43
            r45 = r44
            r50 = r45
            r51 = r50
            r52 = r51
            goto L_0x1691
        L_0x1671:
            r51 = r0
            r31 = r1
            r32 = r2
            r30 = r7
            r0 = r12
            r1 = r0
            r7 = r1
            r9 = r7
            r10 = r9
            r13 = r10
            r24 = r13
            r25 = r24
            r26 = r25
            r27 = r26
            r43 = r27
            r44 = r43
            r45 = r44
            r50 = r45
            r52 = r50
        L_0x1691:
            r53 = r52
            r54 = r53
            goto L_0x17b8
        L_0x1697:
            r12 = 0
            r13 = 6
            r20 = 0
            java.lang.String r0 = "tg:invoice"
            java.lang.String r1 = "tg://invoice"
            java.lang.String r0 = r10.replace(r0, r1)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r59 = r0
            r0 = r12
            r1 = r0
            r7 = r1
            r9 = r7
            r10 = r9
            r13 = r10
            r24 = r13
            r25 = r24
            r26 = r25
            r27 = r26
            r30 = r27
            r31 = r30
            r32 = r31
            r43 = r32
            r44 = r43
            r45 = r44
            r50 = r45
            r51 = r50
            r52 = r51
            r53 = r52
            r54 = r53
            r55 = r54
            r56 = r55
            r57 = r56
            r58 = r57
            r60 = r58
        L_0x16dd:
            r61 = r60
            r62 = r61
            r64 = r62
            r65 = r64
            r66 = r65
            r46 = r20
            r48 = r46
            r29 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
        L_0x16ff:
            r63 = -1
            goto L_0x1937
        L_0x1703:
            r12 = 0
            r13 = 6
            r20 = 0
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r10.replace(r0, r9)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r9)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x1732
            java.lang.String r1 = r0.getQueryParameter(r5)
            if (r1 == 0) goto L_0x1732
            java.lang.String r2 = "+"
            boolean r2 = r1.startsWith(r2)
            if (r2 == 0) goto L_0x1732
            r2 = 1
            java.lang.String r1 = r1.substring(r2)
        L_0x1732:
            java.lang.String r2 = "telegrampassport"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x17c6
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r7)
            boolean r9 = android.text.TextUtils.isEmpty(r2)
            if (r9 != 0) goto L_0x1765
            java.lang.String r9 = "{"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x1765
            java.lang.String r9 = "}"
            boolean r9 = r2.endsWith(r9)
            if (r9 == 0) goto L_0x1765
            java.lang.String r9 = "nonce"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "nonce"
            r1.put(r10, r9)
            goto L_0x1770
        L_0x1765:
            java.lang.String r9 = "payload"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "payload"
            r1.put(r10, r9)
        L_0x1770:
            java.lang.String r9 = "bot_id"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "bot_id"
            r1.put(r10, r9)
            r1.put(r7, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "public_key"
            r1.put(r7, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            r53 = r1
            r0 = r12
            r1 = r0
            r7 = r1
            r9 = r7
            r10 = r9
            r13 = r10
            r24 = r13
            r25 = r24
            r26 = r25
            r27 = r26
            r30 = r27
            r31 = r30
            r32 = r31
            r43 = r32
            r44 = r43
            r45 = r44
            r50 = r45
            r51 = r50
            r52 = r51
            r54 = r52
        L_0x17b8:
            r55 = r54
            r56 = r55
            r57 = r56
            r58 = r57
            r59 = r58
            r60 = r59
            goto L_0x16dd
        L_0x17c6:
            java.lang.String r2 = "start"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "startgroup"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "startchannel"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "admin"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r12 = "game"
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.String r13 = "voicechat"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r24 = r1
            java.lang.String r1 = "livestream"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r87 = r1
            java.lang.String r1 = "startattach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r25 = r1
            java.lang.String r1 = "choose"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r26 = r1
            java.lang.String r1 = "attach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r27 = r1
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r28 = r1.intValue()
            if (r28 != 0) goto L_0x181f
            r28 = 0
            goto L_0x1821
        L_0x181f:
            r28 = r1
        L_0x1821:
            java.lang.String r1 = "thread"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r29 = r1.intValue()
            if (r29 != 0) goto L_0x1834
            r29 = 0
            goto L_0x1836
        L_0x1834:
            r29 = r1
        L_0x1836:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r1 = r0.intValue()
            r62 = r87
            if (r1 != 0) goto L_0x1880
            r52 = r12
            r61 = r13
            r46 = r20
            r48 = r46
            r12 = r24
            r64 = r25
            r66 = r26
            r65 = r27
            r30 = r28
            r32 = r29
            r0 = 0
            r1 = 0
            r13 = 0
            r29 = 0
            r31 = 0
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
            r50 = 0
            r51 = 0
            goto L_0x18b7
        L_0x1880:
            r51 = r0
            r52 = r12
            r61 = r13
            r46 = r20
            r48 = r46
            r12 = r24
            r64 = r25
            r66 = r26
            r65 = r27
            r30 = r28
            r32 = r29
            r0 = 0
            r1 = 0
            r13 = 0
            r29 = 0
            r31 = 0
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
            r50 = 0
        L_0x18b7:
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r58 = 0
            r59 = 0
            r60 = 0
            r63 = -1
            r24 = r2
            r25 = r7
            r26 = r9
            r27 = r10
            r7 = 0
            r9 = 0
            r10 = 0
            goto L_0x1937
        L_0x18d5:
            r20 = 0
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda38 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda38
            r0.<init>(r15, r10)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x18e2
        L_0x18e0:
            r20 = 0
        L_0x18e2:
            r46 = r20
            r48 = r46
            r0 = 0
            r1 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
        L_0x18ff:
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
        L_0x1915:
            r50 = 0
        L_0x1917:
            r51 = 0
            r52 = 0
            r53 = 0
        L_0x191d:
            r54 = 0
        L_0x191f:
            r55 = 0
        L_0x1921:
            r56 = 0
            r57 = 0
        L_0x1925:
            r58 = 0
            r59 = 0
            r60 = 0
        L_0x192b:
            r61 = 0
            r62 = 0
            r63 = -1
            r64 = 0
            r65 = 0
            r66 = 0
        L_0x1937:
            boolean r2 = r14.hasExtra(r4)
            if (r2 == 0) goto L_0x197f
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x1955
            java.lang.String r2 = "tg"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x1955
            if (r55 != 0) goto L_0x1955
            r2 = 1
            goto L_0x1956
        L_0x1955:
            r2 = 0
        L_0x1956:
            com.google.firebase.appindexing.builders.AssistActionBuilder r3 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r3.<init>()
            r67 = r6
            java.lang.String r6 = r14.getStringExtra(r4)
            com.google.firebase.appindexing.builders.AssistActionBuilder r3 = r3.setActionToken(r6)
            if (r2 == 0) goto L_0x196a
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x196c
        L_0x196a:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x196c:
            com.google.firebase.appindexing.Action$Builder r2 = r3.setActionStatus(r2)
            com.google.firebase.appindexing.Action r2 = r2.build()
            com.google.firebase.appindexing.FirebaseUserActions r3 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r84)
            r3.end(r2)
            r14.removeExtra(r4)
            goto L_0x1981
        L_0x197f:
            r67 = r6
        L_0x1981:
            if (r56 != 0) goto L_0x199e
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x1990
            goto L_0x199e
        L_0x1990:
            r76 = r8
            r78 = r11
            r7 = r15
            r77 = r67
            r1 = 2
            r8 = 3
            r10 = 0
            r68 = -1
            goto L_0x1b6e
        L_0x199e:
            if (r0 != 0) goto L_0x1afb
            if (r1 == 0) goto L_0x19a4
            goto L_0x1afb
        L_0x19a4:
            if (r12 != 0) goto L_0x1a88
            if (r7 != 0) goto L_0x1a88
            if (r9 != 0) goto L_0x1a88
            if (r10 != 0) goto L_0x1a88
            if (r13 != 0) goto L_0x1a88
            if (r52 != 0) goto L_0x1a88
            if (r61 != 0) goto L_0x1a88
            if (r53 != 0) goto L_0x1a88
            if (r55 != 0) goto L_0x1a88
            if (r54 != 0) goto L_0x1a88
            if (r56 != 0) goto L_0x1a88
            if (r58 != 0) goto L_0x1a88
            if (r59 != 0) goto L_0x1a88
            if (r31 != 0) goto L_0x1a88
            if (r60 != 0) goto L_0x1a88
            if (r57 == 0) goto L_0x19c6
            goto L_0x1a88
        L_0x19c6:
            android.content.ContentResolver r68 = r84.getContentResolver()     // Catch:{ Exception -> 0x1a6f }
            android.net.Uri r69 = r85.getData()     // Catch:{ Exception -> 0x1a6f }
            r70 = 0
            r71 = 0
            r72 = 0
            r73 = 0
            android.database.Cursor r1 = r68.query(r69, r70, r71, r72, r73)     // Catch:{ Exception -> 0x1a6f }
            if (r1 == 0) goto L_0x1a5f
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1a57 }
            if (r0 == 0) goto L_0x1a5f
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1a57 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1a57 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ all -> 0x1a57 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1a57 }
            r2 = 0
        L_0x19f5:
            r3 = 4
            if (r2 >= r3) goto L_0x1a13
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1a57 }
            long r3 = r3.getClientUserId()     // Catch:{ all -> 0x1a57 }
            long r5 = (long) r0     // Catch:{ all -> 0x1a57 }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x1a0f
            r3 = 0
            r11[r3] = r2     // Catch:{ all -> 0x1a57 }
            r0 = r11[r3]     // Catch:{ all -> 0x1a57 }
            r6 = 1
            r15.switchToAccount(r0, r6)     // Catch:{ all -> 0x1a55 }
            goto L_0x1a14
        L_0x1a0f:
            r6 = 1
            int r2 = r2 + 1
            goto L_0x19f5
        L_0x1a13:
            r6 = 1
        L_0x1a14:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1a55 }
            long r2 = r1.getLong(r0)     // Catch:{ all -> 0x1a55 }
            r4 = 0
            r0 = r11[r4]     // Catch:{ all -> 0x1a55 }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x1a55 }
            int r5 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x1a55 }
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ all -> 0x1a55 }
            r0.postNotificationName(r5, r7)     // Catch:{ all -> 0x1a55 }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1a51 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1a51 }
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r4 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x1a51 }
            if (r4 == 0) goto L_0x1a42
            r46 = r2
            r3 = 1
            goto L_0x1a62
        L_0x1a42:
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x1a51 }
            r46 = r2
            r3 = r37
            if (r0 == 0) goto L_0x1a62
            r38 = 1
            goto L_0x1a62
        L_0x1a51:
            r0 = move-exception
            r46 = r2
            goto L_0x1a59
        L_0x1a55:
            r0 = move-exception
            goto L_0x1a59
        L_0x1a57:
            r0 = move-exception
            r6 = 1
        L_0x1a59:
            r1.close()     // Catch:{ all -> 0x1a5c }
        L_0x1a5c:
            throw r0     // Catch:{ Exception -> 0x1a5d }
        L_0x1a5d:
            r0 = move-exception
            goto L_0x1a71
        L_0x1a5f:
            r6 = 1
            r3 = r37
        L_0x1a62:
            if (r1 == 0) goto L_0x1a6c
            r1.close()     // Catch:{ Exception -> 0x1a68 }
            goto L_0x1a6c
        L_0x1a68:
            r0 = move-exception
            r37 = r3
            goto L_0x1a71
        L_0x1a6c:
            r37 = r3
            goto L_0x1a74
        L_0x1a6f:
            r0 = move-exception
            r6 = 1
        L_0x1a71:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1a74:
            r76 = r8
            r78 = r11
            r7 = r15
            r12 = r34
            r13 = r35
            r4 = r46
            r77 = r67
            r1 = 2
            r8 = 3
            r10 = 0
            r68 = -1
            goto L_0x1b9d
        L_0x1a88:
            r6 = 1
            if (r13 == 0) goto L_0x1aa3
            java.lang.String r0 = "@"
            boolean r0 = r13.startsWith(r0)
            if (r0 == 0) goto L_0x1aa3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r8)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            r13 = r0
        L_0x1aa3:
            r19 = 0
            r2 = r11[r19]
            r5 = 6
            r28 = 0
            r1 = r84
            r68 = -1
            r3 = r12
            r12 = 2
            r4 = r7
            r7 = 3
            r5 = r9
            r9 = r67
            r16 = 1
            r6 = r10
            r10 = 0
            r7 = r24
            r76 = r8
            r8 = r25
            r77 = r9
            r9 = r26
            r10 = r27
            r78 = r11
            r11 = r13
            r13 = 0
            r12 = r29
            r13 = r30
            r14 = r31
            r15 = r32
            r16 = r51
            r17 = r52
            r18 = r53
            r19 = r54
            r20 = r55
            r21 = r56
            r22 = r57
            r23 = r58
            r24 = r59
            r25 = r60
            r26 = r61
            r27 = r62
            r29 = r63
            r30 = r64
            r31 = r65
            r32 = r66
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = 2
            r8 = 3
            r10 = 0
            r7 = r84
            goto L_0x1b6e
        L_0x1afb:
            r76 = r8
            r78 = r11
            r77 = r67
            r68 = -1
            org.telegram.ui.ActionBar.AlertDialog r3 = new org.telegram.ui.ActionBar.AlertDialog
            r8 = 3
            r7 = r84
            r3.<init>(r7, r8)
            r10 = 0
            r3.setCanCancel(r10)
            r3.show()
            org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode r9 = new org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode
            r9.<init>()
            r9.hash = r1
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = new org.telegram.tgnet.TLRPC$TL_codeSettings
            r1.<init>()
            r9.settings = r1
            r1.allow_flashcall = r10
            boolean r2 = org.telegram.messenger.ApplicationLoader.hasPlayServices
            r1.allow_app_hash = r2
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r2 = "mainconfig"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r10)
            org.telegram.tgnet.TLRPC$TL_codeSettings r2 = r9.settings
            boolean r2 = r2.allow_app_hash
            if (r2 == 0) goto L_0x1b44
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = org.telegram.messenger.BuildVars.SMS_HASH
            java.lang.String r4 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.putString(r4, r2)
            r1.apply()
            goto L_0x1b51
        L_0x1b44:
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            r1.apply()
        L_0x1b51:
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            r6.putString(r5, r0)
            int r1 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83
            r1 = r12
            r2 = r84
            r4 = r0
            r5 = r6
            r6 = r9
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 2
            r11.sendRequest(r9, r12, r1)
        L_0x1b6e:
            r12 = r34
            r13 = r35
            r4 = r46
            goto L_0x1b9d
        L_0x1b75:
            r77 = r6
            r76 = r8
            r78 = r11
            r7 = r15
            r1 = 2
            r8 = 3
            r10 = 0
            r68 = -1
            r4 = 0
            r12 = 0
            r13 = 0
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
            r48 = 0
            r50 = 0
        L_0x1b9d:
            r11 = r85
            r10 = r12
            r82 = r13
            r1 = r38
            r79 = r44
            r80 = r45
            r8 = r48
            r15 = r50
            r6 = r77
            r14 = r78
            r0 = -1
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r74 = 0
            r81 = 0
            r12 = r4
            r5 = r43
            r4 = -1
            goto L_0x1da0
        L_0x1bc5:
            r77 = r6
            r76 = r8
            r78 = r11
            r7 = r15
            r1 = 2
            r8 = 3
            r10 = 0
            r68 = -1
            java.lang.String r0 = r85.getAction()
            java.lang.String r2 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x1c0e
            r11 = r85
            r6 = r77
            r14 = r78
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r8 = 0
            r12 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r74 = 0
            r79 = 0
            r80 = 0
            r81 = 0
            r82 = 1
            goto L_0x1da0
        L_0x1c0e:
            java.lang.String r0 = r85.getAction()
            java.lang.String r2 = "new_dialog"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x1CLASSNAME
            r11 = r85
            r6 = r77
            r14 = r78
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r8 = 0
            r12 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1
            r21 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r74 = 0
            goto L_0x1d98
        L_0x1CLASSNAME:
            java.lang.String r0 = r85.getAction()
            java.lang.String r2 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x1d11
            java.lang.String r0 = "chatId"
            r11 = r85
            r2 = 0
            long r4 = r11.getLongExtra(r0, r2)
            java.lang.String r0 = "userId"
            long r12 = r11.getLongExtra(r0, r2)
            java.lang.String r0 = "encId"
            int r0 = r11.getIntExtra(r0, r10)
            java.lang.String r6 = "appWidgetId"
            int r6 = r11.getIntExtra(r6, r10)
            if (r6 == 0) goto L_0x1CLASSNAME
            java.lang.String r0 = "appWidgetType"
            int r0 = r11.getIntExtra(r0, r10)
            r68 = r0
            r4 = r2
            r12 = r4
            r0 = r6
            r6 = r77
            r14 = r78
            r9 = 0
            r15 = 0
            r16 = 0
            r17 = 6
            goto L_0x1ce6
        L_0x1CLASSNAME:
            r6 = r77
            int r9 = r11.getIntExtra(r6, r10)
            int r14 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r14 == 0) goto L_0x1ca7
            r14 = r78
            r0 = r14[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r12 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r13 = new java.lang.Object[r10]
            r0.postNotificationName(r12, r13)
            r12 = r4
            r15 = r9
            r0 = -1
            r9 = 0
            r16 = 0
            r17 = 0
            r4 = r2
            goto L_0x1ce6
        L_0x1ca7:
            r14 = r78
            int r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x1cc4
            r0 = r14[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r10]
            r0.postNotificationName(r4, r5)
            r15 = r9
            r4 = r12
            r0 = -1
            r9 = 0
            r16 = 0
            r17 = 0
            r12 = r2
            goto L_0x1ce6
        L_0x1cc4:
            if (r0 == 0) goto L_0x1cdd
            r4 = r14[r10]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r12 = new java.lang.Object[r10]
            r4.postNotificationName(r5, r12)
            r4 = r2
            r12 = r4
            r15 = r9
            r16 = 0
            r17 = 0
            r9 = r0
            r0 = -1
            goto L_0x1ce6
        L_0x1cdd:
            r4 = r2
            r12 = r4
            r15 = r9
            r0 = -1
            r9 = 0
            r16 = 1
            r17 = 0
        L_0x1ce6:
            r74 = r2
            r81 = r9
            r8 = r12
            r10 = r15
            r82 = r17
            r1 = 0
            r15 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r79 = 0
            r80 = 0
            r12 = r4
            r17 = r16
            r5 = 0
            r4 = r0
            r0 = r68
            goto L_0x1da0
        L_0x1d11:
            r11 = r85
            r6 = r77
            r14 = r78
            r2 = 0
            java.lang.String r0 = r85.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1d33
            r8 = r2
            r12 = r8
            r74 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 1
            goto L_0x1d86
        L_0x1d33:
            java.lang.String r0 = r85.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1d4f
            r8 = r2
            r12 = r8
            r74 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 1
            goto L_0x1d88
        L_0x1d4f:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x1d79
            r8 = r2
            r12 = r8
            r74 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1
            goto L_0x1d8c
        L_0x1d6b:
            r2 = r4
            r76 = r8
            r7 = r15
            r1 = 2
        L_0x1d70:
            r8 = 3
            r10 = 0
            r68 = -1
            r83 = r14
            r14 = r11
            r11 = r83
        L_0x1d79:
            r8 = r2
            r12 = r8
            r74 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 0
        L_0x1d86:
            r19 = 0
        L_0x1d88:
            r20 = 0
            r21 = 0
        L_0x1d8c:
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
        L_0x1d98:
            r79 = 0
            r80 = 0
            r81 = 0
            r82 = 0
        L_0x1da0:
            int r2 = r7.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x226e
            if (r15 == 0) goto L_0x1dd4
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.getLastFragment()
            boolean r3 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x1dd0
            org.telegram.ui.DialogsActivity r2 = (org.telegram.ui.DialogsActivity) r2
            boolean r3 = r2.isMainDialogList()
            if (r3 == 0) goto L_0x1dd4
            android.view.View r3 = r2.getFragmentView()
            if (r3 == 0) goto L_0x1dcb
            r3 = 1
            r2.search(r15, r3)
            goto L_0x1dd5
        L_0x1dcb:
            r3 = 1
            r2.setInitialSearchString(r15)
            goto L_0x1dd5
        L_0x1dd0:
            r3 = 1
            r17 = 1
            goto L_0x1dd5
        L_0x1dd4:
            r3 = 1
        L_0x1dd5:
            r23 = 0
            int r2 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1))
            if (r2 == 0) goto L_0x1e58
            if (r37 != 0) goto L_0x1e31
            if (r1 == 0) goto L_0x1de0
            goto L_0x1e31
        L_0x1de0:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putLong(r1, r12)
            if (r10 == 0) goto L_0x1def
            r0.putInt(r6, r10)
        L_0x1def:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1e11
            r1 = 0
            r2 = r14[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1edc
        L_0x1e11:
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r0
            r24 = r1
            boolean r0 = r23.presentFragment(r24, r25, r26, r27, r28)
            if (r0 == 0) goto L_0x1edc
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1eda
        L_0x1e31:
            if (r39 == 0) goto L_0x1e4c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x226e
            org.telegram.messenger.MessagesController r2 = r0.getMessagesController()
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r2, r1)
            goto L_0x226e
        L_0x1e4c:
            r2 = 0
            r0 = r14[r2]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r12, r1, r0)
            goto L_0x226e
        L_0x1e58:
            r12 = 0
            int r2 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x1eae
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r8)
            if (r10 == 0) goto L_0x1e6d
            r0.putInt(r6, r10)
        L_0x1e6d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1e8f
            r1 = 0
            r2 = r14[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1edc
        L_0x1e8f:
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r0
            r24 = r1
            boolean r0 = r23.presentFragment(r24, r25, r26, r27, r28)
            if (r0 == 0) goto L_0x1edc
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1eda
        L_0x1eae:
            r10 = r81
            if (r10 == 0) goto L_0x1ee3
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enc_id"
            r0.putInt(r1, r10)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r0
            r24 = r1
            boolean r0 = r23.presentFragment(r24, r25, r26, r27, r28)
            if (r0 == 0) goto L_0x1edc
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
        L_0x1eda:
            r13 = 1
            goto L_0x1edd
        L_0x1edc:
            r13 = 0
        L_0x1edd:
            r2 = r86
            r8 = 0
            r9 = 1
            goto L_0x2273
        L_0x1ee3:
            if (r17 == 0) goto L_0x1f1f
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1ef1
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1f1c
        L_0x1ef1:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1f1c
        L_0x1efb:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r3
            if (r0 <= 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x1efb
        L_0x1var_:
            r2 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.closeLastFragment(r2)
            goto L_0x1f1d
        L_0x1f1c:
            r2 = 0
        L_0x1f1d:
            r8 = 0
            goto L_0x1var_
        L_0x1f1f:
            r2 = 0
            if (r18 == 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r8 = 0
            r1.<init>(r7, r8)
            r0.showDialog(r1)
            goto L_0x1var_
        L_0x1var_:
            r8 = 0
        L_0x1var_:
            r2 = r86
        L_0x1var_:
            r9 = 1
            goto L_0x2272
        L_0x1var_:
            r8 = 0
            if (r19 == 0) goto L_0x1f6c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97
            r2.<init>(r7, r14)
            r1.<init>(r7, r2, r8)
            r0.showDialog(r1)
            goto L_0x1var_
        L_0x1f6c:
            android.net.Uri r2 = r7.exportingChatUri
            if (r2 == 0) goto L_0x1var_
            java.util.ArrayList<android.net.Uri> r0 = r7.documentsUrisArray
            r7.runImportRequest(r2, r0)
            goto L_0x226f
        L_0x1var_:
            java.util.ArrayList<android.os.Parcelable> r2 = r7.importingStickers
            if (r2 == 0) goto L_0x1var_
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25
            r0.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x1var_
        L_0x1var_:
            java.lang.String r2 = r7.videoPath
            if (r2 != 0) goto L_0x223c
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r7.photoPathsArray
            if (r2 != 0) goto L_0x223c
            java.lang.String r2 = r7.sendingText
            if (r2 != 0) goto L_0x223c
            java.util.ArrayList<java.lang.String> r2 = r7.documentsPathsArray
            if (r2 != 0) goto L_0x223c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.contactsToSend
            if (r2 != 0) goto L_0x223c
            java.util.ArrayList<android.net.Uri> r2 = r7.documentsUrisArray
            if (r2 == 0) goto L_0x1f9e
            goto L_0x223c
        L_0x1f9e:
            r10 = r82
            if (r10 == 0) goto L_0x202e
            if (r10 != r3) goto L_0x1fbf
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            java.lang.String r4 = "user_id"
            r0.putLong(r4, r1)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r0 = r1
        L_0x1fbc:
            r1 = 6
        L_0x1fbd:
            r13 = 0
            goto L_0x1ff4
        L_0x1fbf:
            r1 = 2
            if (r10 != r1) goto L_0x1fc9
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r0.<init>(r1)
            goto L_0x1fbc
        L_0x1fc9:
            r1 = 0
            r2 = 3
            if (r10 != r2) goto L_0x1fd3
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r1)
            goto L_0x1fbc
        L_0x1fd3:
            r1 = 4
            if (r10 != r1) goto L_0x1fdc
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x1fbc
        L_0x1fdc:
            r1 = 5
            if (r10 != r1) goto L_0x1fe8
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 3
            r0.<init>(r1)
            r1 = 6
            r13 = 1
            goto L_0x1ff4
        L_0x1fe8:
            r1 = 6
            if (r10 != r1) goto L_0x1ff2
            org.telegram.ui.EditWidgetActivity r2 = new org.telegram.ui.EditWidgetActivity
            r2.<init>(r0, r4)
            r0 = r2
            goto L_0x1fbd
        L_0x1ff2:
            r0 = r8
            goto L_0x1fbd
        L_0x1ff4:
            if (r10 != r1) goto L_0x2008
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r1
            r24 = r0
            r23.presentFragment(r24, r25, r26, r27, r28)
            goto L_0x2010
        L_0x2008:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61
            r1.<init>(r7, r0, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x2010:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2027
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x206d
        L_0x2027:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x206d
        L_0x202e:
            if (r20 == 0) goto L_0x2072
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r3)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ContactsActivity r2 = new org.telegram.ui.ContactsActivity
            r2.<init>(r0)
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r1
            r24 = r2
            r23.presentFragment(r24, r25, r26, r27, r28)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2067
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x206d
        L_0x2067:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
        L_0x206d:
            r2 = r86
            r9 = 1
            goto L_0x225c
        L_0x2072:
            if (r5 == 0) goto L_0x20d3
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r2 = "destroyAfterSelect"
            r0.putBoolean(r2, r3)
            java.lang.String r2 = "returnAsResult"
            r0.putBoolean(r2, r3)
            java.lang.String r2 = "onlyUsers"
            r0.putBoolean(r2, r3)
            java.lang.String r2 = "allowSelf"
            r4 = 0
            r0.putBoolean(r2, r4)
            org.telegram.ui.ContactsActivity r2 = new org.telegram.ui.ContactsActivity
            r2.<init>(r0)
            r2.setInitialSearchString(r5)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98
            r0.<init>(r7, r1, r14)
            r2.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.getLastFragment()
            boolean r1 = r1 instanceof org.telegram.ui.ContactsActivity
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r0
            r24 = r2
            r25 = r1
            r23.presentFragment(r24, r25, r26, r27, r28)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x20cc
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x206d
        L_0x20cc:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x206d
        L_0x20d3:
            if (r42 == 0) goto L_0x2114
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 5
            r0.<init>(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91
            r1.<init>(r7, r0)
            r0.setQrLoginDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r1
            r24 = r0
            r23.presentFragment(r24, r25, r26, r27, r28)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x210c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x206d
        L_0x210c:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x206d
        L_0x2114:
            r1 = 0
            if (r40 == 0) goto L_0x216f
            org.telegram.ui.NewContactActivity r0 = new org.telegram.ui.NewContactActivity
            r0.<init>()
            r2 = r79
            if (r2 == 0) goto L_0x2133
            r4 = r76
            r5 = 2
            java.lang.String[] r2 = r2.split(r4, r5)
            r4 = r2[r1]
            int r5 = r2.length
            if (r5 <= r3) goto L_0x212f
            r2 = r2[r3]
            goto L_0x2130
        L_0x212f:
            r2 = r8
        L_0x2130:
            r0.setInitialName(r4, r2)
        L_0x2133:
            r4 = r80
            if (r4 == 0) goto L_0x213e
            java.lang.String r2 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4, r3)
            r0.setInitialPhoneNumber(r2, r1)
        L_0x213e:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r25 = 0
            r26 = 1
            r27 = 1
            r28 = 0
            r23 = r1
            r24 = r0
            r23.presentFragment(r24, r25, r26, r27, r28)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2167
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x206d
        L_0x2167:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x206d
        L_0x216f:
            r2 = r79
            r4 = r80
            if (r21 == 0) goto L_0x218e
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r0 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r84
            r9 = 1
            r3 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x2270
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r9
            goto L_0x2270
        L_0x218e:
            r9 = 1
            if (r41 == 0) goto L_0x220e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x220a
            android.app.Activity r1 = r0.getParentActivity()
            if (r1 == 0) goto L_0x220a
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            r3 = 0
            java.lang.String r1 = org.telegram.ui.NewContactActivity.getPhoneNumber(r7, r1, r4, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r0.getParentActivity()
            r3.<init>((android.content.Context) r4)
            r4 = 2131626842(0x7f0e0b5a, float:1.8880932E38)
            java.lang.String r5 = "NewContactAlertTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setTitle(r4)
            r4 = 2131626841(0x7f0e0b59, float:1.888093E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r6 = r6.format(r1)
            r10 = 0
            r5[r10] = r6
            java.lang.String r6 = "NewContactAlertMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setMessage(r4)
            r4 = 2131626840(0x7f0e0b58, float:1.8880928E38)
            java.lang.String r5 = "NewContactAlertButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8
            r5.<init>(r1, r2, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r3.setPositiveButton(r4, r5)
            r2 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r1.setNegativeButton(r2, r8)
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            r0.showDialog(r1)
            r13 = 1
            goto L_0x220b
        L_0x220a:
            r13 = 0
        L_0x220b:
            r2 = r86
            goto L_0x2273
        L_0x220e:
            if (r36 == 0) goto L_0x2270
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r1 = new org.telegram.ui.CallLogActivity
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 0
            r0.presentFragment(r1, r2, r3, r4, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2235
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x225a
        L_0x2235:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r9, r1)
            goto L_0x225a
        L_0x223c:
            r1 = 0
            r9 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x2251
            r0 = r14[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r3)
        L_0x2251:
            r2 = 0
            int r0 = (r74 > r2 ? 1 : (r74 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x225e
            r7.openDialogsToSend(r1)
        L_0x225a:
            r2 = r86
        L_0x225c:
            r13 = 1
            goto L_0x2273
        L_0x225e:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r74)
            r0.add(r2)
            r7.didSelectDialogs(r8, r0, r8, r1)
            goto L_0x2270
        L_0x226e:
            r8 = 0
        L_0x226f:
            r9 = 1
        L_0x2270:
            r2 = r86
        L_0x2272:
            r13 = 0
        L_0x2273:
            if (r13 != 0) goto L_0x231d
            if (r2 != 0) goto L_0x231d
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x22c8
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x22a3
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2308
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r84.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2308
        L_0x22a3:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2308
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r8)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r15 == 0) goto L_0x22bc
            r0.setInitialSearchString(r15)
        L_0x22bc:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r9, r1)
            goto L_0x2308
        L_0x22c8:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2308
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x22ee
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r84.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2308
        L_0x22ee:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r8)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r15 == 0) goto L_0x22fd
            r0.setInitialSearchString(r15)
        L_0x22fd:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r9, r1)
        L_0x2308:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x231d
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
        L_0x231d:
            if (r33 == 0) goto L_0x2325
            r1 = 0
            r0 = r14[r1]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x2325:
            if (r21 != 0) goto L_0x233a
            java.lang.String r0 = r85.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x233a
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x233a
            r0.dismiss()
        L_0x233a:
            r11.setAction(r8)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$10(String str) {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).presentFragment(new PremiumPreviewFragment(Uri.parse(str).getQueryParameter("ref")));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$11(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$13(AlertDialog alertDialog, String str, Bundle bundle, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda59(this, alertDialog, tLRPC$TL_error, str, bundle, tLObject, tLRPC$TL_account_sendConfirmPhoneCode));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$12(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, String str, Bundle bundle, TLObject tLObject, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        alertDialog.dismiss();
        if (tLRPC$TL_error == null) {
            lambda$runLinkRequest$61(new LoginActivity().cancelAccountDeletion(str, bundle, (TLRPC$TL_auth_sentCode) tLObject));
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, getActionBarLayout().getLastFragment(), tLRPC$TL_account_sendConfirmPhoneCode, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$15(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda104(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$61(locationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$16() {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).showDialog(new StickersAlert((Context) this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$17(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$18(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$22(ActionIntroActivity actionIntroActivity, String str) {
        AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new LaunchActivity$$ExternalSyntheticLambda69(alertDialog, actionIntroActivity));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$20(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!(tLObject instanceof TLRPC$TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda24(actionIntroActivity, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$19(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(actionIntroActivity, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$23(String str, String str2, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        NewContactActivity newContactActivity = new NewContactActivity();
        newContactActivity.setInitialPhoneNumber(str, false);
        if (str2 != null) {
            String[] split = str2.split(" ", 2);
            newContactActivity.setInitialName(split[0], split.length > 1 ? split[1] : null);
        }
        baseFragment.presentFragment(newContactActivity);
    }

    public static int getTimestampFromLink(Uri uri) {
        String str;
        int i;
        if (uri.getPathSegments().contains("video")) {
            str = uri.getQuery();
        } else {
            str = uri.getQueryParameter("t") != null ? uri.getQueryParameter("t") : null;
        }
        if (str == null) {
            return -1;
        }
        try {
            i = Integer.parseInt(str);
        } catch (Throwable unused) {
            i = -1;
        }
        if (i == -1) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            try {
                return (int) ((simpleDateFormat.parse(str).getTime() - simpleDateFormat.parse("00:00").getTime()) / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return i;
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
            r4 = 2131628281(0x7f0e10f9, float:1.888385E38)
            java.lang.String r5 = "selectAlertString"
            if (r1 == 0) goto L_0x003d
            int r1 = r1.size()
            if (r1 == r2) goto L_0x0052
            java.lang.String r1 = "SendContactToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131628257(0x7f0e10e1, float:1.8883802E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
            goto L_0x0052
        L_0x003d:
            java.lang.String r1 = "SendMessagesToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131628280(0x7f0e10f8, float:1.8883848E38)
            java.lang.String r4 = "SendMessagesToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
        L_0x0052:
            org.telegram.ui.LaunchActivity$12 r5 = new org.telegram.ui.LaunchActivity$12
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
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new LaunchActivity$$ExternalSyntheticLambda71(this, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runCommentRequest$25(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0094 A[SYNTHETIC, Splitter:B:15:0x0094] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runCommentRequest$24(org.telegram.tgnet.TLObject r12, int r13, java.lang.Integer r14, org.telegram.tgnet.TLRPC$Chat r15, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r16, java.lang.Integer r17, java.lang.Integer r18, org.telegram.ui.ActionBar.AlertDialog r19) {
        /*
            r11 = this;
            r0 = r12
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_discussionMessage
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0091
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
            if (r1 != 0) goto L_0x0091
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.Object r2 = r6.get(r2)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r4 = r2.getDialogId()
            long r4 = -r4
            java.lang.String r2 = "chat_id"
            r1.putLong(r2, r4)
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
            if (r17 == 0) goto L_0x0082
            int r0 = r17.intValue()
            r2.setHighlightMessageId(r0)
            goto L_0x008b
        L_0x0082:
            if (r18 == 0) goto L_0x008b
            int r0 = r14.intValue()
            r2.setHighlightMessageId(r0)
        L_0x008b:
            r1 = r11
            r11.lambda$runLinkRequest$61(r2)
            r2 = 1
            goto L_0x0092
        L_0x0091:
            r1 = r11
        L_0x0092:
            if (r2 != 0) goto L_0x00c2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00be }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x00be }
            if (r0 != 0) goto L_0x00c2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00be }
            int r2 = r0.size()     // Catch:{ Exception -> 0x00be }
            int r2 = r2 - r3
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x00be }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x00be }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x00be }
            java.lang.String r2 = "ChannelPostDeleted"
            r3 = 2131624964(0x7f0e0404, float:1.8877123E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x00be }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r2)     // Catch:{ Exception -> 0x00be }
            r0.show()     // Catch:{ Exception -> 0x00be }
            goto L_0x00c2
        L_0x00be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c2:
            r19.dismiss()     // Catch:{ Exception -> 0x00c6 }
            goto L_0x00cb
        L_0x00c6:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$24(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77
            r6.<init>(r10, r11, r12, r0)
            int r11 = r5.sendRequest(r4, r6)
            r1[r2] = r11
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2
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
    public /* synthetic */ void lambda$runImportRequest$27(Uri uri, int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda45(this, tLObject, uri, i, alertDialog), 2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011b, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0137, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runImportRequest$26(org.telegram.tgnet.TLObject r10, android.net.Uri r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runImportRequest$26(org.telegram.tgnet.TLObject, android.net.Uri, int, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runImportRequest$28(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v54, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v59, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v67, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v68, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x04b8  */
    /* JADX WARNING: Removed duplicated region for block: B:119:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x03b5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r35, java.lang.String r36, java.lang.String r37, java.lang.String r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, java.lang.String r42, java.lang.String r43, java.lang.String r44, boolean r45, java.lang.Integer r46, java.lang.Long r47, java.lang.Integer r48, java.lang.Integer r49, java.lang.String r50, java.util.HashMap<java.lang.String, java.lang.String> r51, java.lang.String r52, java.lang.String r53, java.lang.String r54, java.lang.String r55, org.telegram.tgnet.TLRPC$TL_wallPaper r56, java.lang.String r57, java.lang.String r58, java.lang.String r59, java.lang.String r60, int r61, int r62, java.lang.String r63, java.lang.String r64, java.lang.String r65) {
        /*
            r34 = this;
            r15 = r34
            r14 = r35
            r0 = r36
            r5 = r37
            r6 = r38
            r12 = r44
            r13 = r47
            r11 = r51
            r10 = r52
            r9 = r53
            r8 = r56
            r7 = r57
            r4 = r58
            r1 = r61
            r2 = 2
            if (r1 != 0) goto L_0x0078
            int r3 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r3 < r2) goto L_0x0078
            if (r11 == 0) goto L_0x0078
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda92 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda92
            r1 = r3
            r2 = r34
            r0 = r3
            r3 = r35
            r14 = r4
            r4 = r36
            r5 = r37
            r6 = r38
            r7 = r39
            r8 = r40
            r9 = r41
            r10 = r42
            r11 = r43
            r12 = r44
            r15 = r13
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
            r23 = r55
            r24 = r56
            r25 = r57
            r26 = r58
            r27 = r59
            r28 = r60
            r29 = r62
            r30 = r63
            r31 = r64
            r32 = r65
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            r13 = r34
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r13, r0)
            r0.show()
            return
        L_0x0078:
            r33 = r15
            r15 = r13
            r13 = r33
            r0 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r3 = "OK"
            r11 = 0
            r4 = 1
            r10 = 0
            if (r54 == 0) goto L_0x00cf
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r1 = r1.hasObservers(r2)
            if (r1 == 0) goto L_0x009f
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r10] = r54
            r0.postNotificationName(r2, r1)
            goto L_0x00ce
        L_0x009f:
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r13)
            r2 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r5 = "AppName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setTitle(r2)
            r2 = 2131627185(0x7f0e0cb1, float:1.8881627E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r10] = r54
            java.lang.String r5 = "OtherLoginCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setMessage(r2)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r1.setPositiveButton(r0, r11)
            r13.showAlertDialog(r1)
        L_0x00ce:
            return
        L_0x00cf:
            if (r55 == 0) goto L_0x00f9
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r13)
            r2 = 2131624541(0x7f0e025d, float:1.8876265E38)
            java.lang.String r4 = "AuthAnotherClient"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setTitle(r2)
            r2 = 2131624552(0x7f0e0268, float:1.8876287E38)
            java.lang.String r4 = "AuthAnotherClientUrl"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setMessage(r2)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r1.setPositiveButton(r0, r11)
            r13.showAlertDialog(r1)
            return
        L_0x00f9:
            org.telegram.ui.ActionBar.AlertDialog r9 = new org.telegram.ui.ActionBar.AlertDialog
            r0 = 3
            r9.<init>(r13, r0)
            int[] r8 = new int[r4]
            r8[r10] = r10
            r3 = r57
            if (r3 == 0) goto L_0x012c
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug r1 = new org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug
            r1.<init>()
            r1.slug = r3
            r0.invoice = r1
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r35)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73
            r2.<init>(r13, r14, r3, r9)
            int r0 = r1.sendRequest(r0, r2)
            r8[r10] = r0
            r3 = r8
            r10 = r9
            r2 = r11
            r8 = r13
            r9 = r14
            r7 = 0
            goto L_0x04b3
        L_0x012c:
            r7 = r36
            if (r7 == 0) goto L_0x0193
            boolean r0 = org.telegram.messenger.AndroidUtilities.isNumeric(r36)
            if (r0 == 0) goto L_0x013e
            org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone r0 = new org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone
            r0.<init>()
            r0.phone = r7
            goto L_0x0145
        L_0x013e:
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r0 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r0.<init>()
            r0.username = r7
        L_0x0145:
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r35)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
            r1 = r12
            r2 = r34
            r3 = r50
            r4 = r59
            r5 = r60
            r6 = r35
            r7 = r63
            r50 = r8
            r8 = r64
            r54 = r9
            r9 = r65
            r10 = r46
            r11 = r49
            r21 = r0
            r0 = r12
            r12 = r48
            r13 = r50
            r14 = r54
            r22 = r0
            r0 = r15
            r15 = r41
            r16 = r42
            r17 = r43
            r18 = r40
            r19 = r62
            r20 = r36
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r1 = r21
            r2 = r22
            int r0 = r0.sendRequest(r1, r2)
            r3 = r50
            r7 = 0
            r3[r7] = r0
            r8 = r34
            r9 = r35
            r10 = r54
            goto L_0x01d3
        L_0x0193:
            r3 = r8
            r54 = r9
            r7 = 0
            if (r5 == 0) goto L_0x01d6
            if (r1 != 0) goto L_0x01b8
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r35)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75
            r8 = r34
            r9 = r35
            r10 = r54
            r4.<init>(r8, r9, r10, r5)
            int r0 = r1.sendRequest(r0, r4, r2)
            r3[r7] = r0
            goto L_0x01d3
        L_0x01b8:
            r8 = r34
            r9 = r35
            r10 = r54
            if (r1 != r4) goto L_0x01d3
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r35)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
            r4.<init>(r8, r9, r10)
            r1.sendRequest(r0, r4, r2)
        L_0x01d3:
            r2 = 0
            goto L_0x04b3
        L_0x01d6:
            r8 = r34
            r9 = r35
            r10 = r54
            if (r6 == 0) goto L_0x0242
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0241
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r4
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x0225
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r5 = 0
            org.telegram.ui.Components.ChatActivityEnterView r6 = r2.getChatActivityEnterViewForStickers()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r2.getResourceProvider()
            r40 = r3
            r41 = r34
            r42 = r1
            r43 = r0
            r44 = r5
            r45 = r6
            r46 = r9
            r40.<init>(r41, r42, r43, r44, r45, r46)
            boolean r0 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r0)
            goto L_0x0238
        L_0x0225:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r2 = 0
            r5 = 0
            r40 = r3
            r41 = r34
            r42 = r1
            r43 = r0
            r44 = r2
            r45 = r5
            r40.<init>((android.content.Context) r41, (org.telegram.ui.ActionBar.BaseFragment) r42, (org.telegram.tgnet.TLRPC$InputStickerSet) r43, (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r44, (org.telegram.ui.Components.StickersAlert.StickersAlertDelegate) r45)
        L_0x0238:
            if (r39 == 0) goto L_0x023b
            goto L_0x023c
        L_0x023b:
            r4 = 0
        L_0x023c:
            r3.probablyEmojis = r4
            r1.showDialog(r3)
        L_0x0241:
            return
        L_0x0242:
            if (r39 == 0) goto L_0x028f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x028e
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            if (r6 == 0) goto L_0x0254
            goto L_0x0256
        L_0x0254:
            r6 = r39
        L_0x0256:
            r0.short_name = r6
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r4)
            r1.add(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r4
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r2 = r0 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x0285
            r2 = r0
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.EmojiPacksAlert r3 = new org.telegram.ui.Components.EmojiPacksAlert
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r2.getResourceProvider()
            r3.<init>(r0, r8, r4, r1)
            boolean r1 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r1)
            goto L_0x028b
        L_0x0285:
            org.telegram.ui.Components.EmojiPacksAlert r3 = new org.telegram.ui.Components.EmojiPacksAlert
            r2 = 0
            r3.<init>(r0, r8, r2, r1)
        L_0x028b:
            r0.showDialog(r3)
        L_0x028e:
            return
        L_0x028f:
            r2 = 0
            if (r12 == 0) goto L_0x02b5
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r5 = "onlySelect"
            r1.putBoolean(r5, r4)
            java.lang.String r5 = "dialogsType"
            r1.putInt(r5, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda102 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda102
            r5 = r45
            r1.<init>(r8, r5, r9, r12)
            r0.setDelegate(r1)
            r8.presentFragment(r0, r7, r4)
            goto L_0x04b3
        L_0x02b5:
            r0 = r51
            if (r0 == 0) goto L_0x0323
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x02cc
            return
        L_0x02cc:
            java.lang.String r4 = "payload"
            java.lang.Object r4 = r0.get(r4)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r5 = "nonce"
            java.lang.Object r5 = r0.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            java.lang.String r6 = "callback_url"
            java.lang.Object r6 = r0.get(r6)
            java.lang.String r6 = (java.lang.String) r6
            org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm r11 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm
            r11.<init>()
            long r12 = (long) r1
            r11.bot_id = r12
            java.lang.String r1 = "scope"
            java.lang.Object r1 = r0.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            r11.scope = r1
            java.lang.String r1 = "public_key"
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            r11.public_key = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r35)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88
            r36 = r1
            r37 = r34
            r38 = r3
            r39 = r35
            r40 = r10
            r41 = r11
            r42 = r4
            r43 = r5
            r44 = r6
            r36.<init>(r37, r38, r39, r40, r41, r42, r43, r44)
            int r0 = r0.sendRequest(r11, r1)
            r3[r7] = r0
            goto L_0x04b3
        L_0x0323:
            r0 = r53
            if (r0 == 0) goto L_0x0341
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80
            r4.<init>(r8, r10)
            int r0 = r0.sendRequest(r1, r4)
            r3[r7] = r0
            goto L_0x04b3
        L_0x0341:
            java.lang.String r0 = "android"
            r1 = r52
            if (r1 == 0) goto L_0x0363
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r4 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r4.<init>()
            r4.lang_code = r1
            r4.lang_pack = r0
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81
            r1.<init>(r8, r10)
            int r0 = r0.sendRequest(r4, r1)
            r3[r7] = r0
            goto L_0x04b3
        L_0x0363:
            r1 = r56
            if (r1 == 0) goto L_0x03d8
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x03b2
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x03ae }
            int r5 = r0.third_background_color     // Catch:{ Exception -> 0x03ae }
            if (r5 == 0) goto L_0x038f
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r6 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x03ae }
            java.lang.String r11 = "c"
            int r12 = r0.background_color     // Catch:{ Exception -> 0x03ae }
            int r13 = r0.second_background_color     // Catch:{ Exception -> 0x03ae }
            int r0 = r0.fourth_background_color     // Catch:{ Exception -> 0x03ae }
            r36 = r6
            r37 = r11
            r38 = r12
            r39 = r13
            r40 = r5
            r41 = r0
            r36.<init>(r37, r38, r39, r40, r41)     // Catch:{ Exception -> 0x03ae }
            goto L_0x03a0
        L_0x038f:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r6 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x03ae }
            java.lang.String r5 = "c"
            int r11 = r0.background_color     // Catch:{ Exception -> 0x03ae }
            int r12 = r0.second_background_color     // Catch:{ Exception -> 0x03ae }
            int r0 = r0.rotation     // Catch:{ Exception -> 0x03ae }
            int r0 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r7)     // Catch:{ Exception -> 0x03ae }
            r6.<init>(r5, r11, r12, r0)     // Catch:{ Exception -> 0x03ae }
        L_0x03a0:
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x03ae }
            r0.<init>(r6, r2, r4, r7)     // Catch:{ Exception -> 0x03ae }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63     // Catch:{ Exception -> 0x03ae }
            r5.<init>(r8, r0)     // Catch:{ Exception -> 0x03ae }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5)     // Catch:{ Exception -> 0x03ae }
            goto L_0x03b3
        L_0x03ae:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03b2:
            r4 = 0
        L_0x03b3:
            if (r4 != 0) goto L_0x04b3
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r4 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r4.<init>()
            java.lang.String r5 = r1.slug
            r4.slug = r5
            r0.wallpaper = r4
            int r4 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84
            r5.<init>(r8, r10, r1)
            int r0 = r4.sendRequest(r0, r5)
            r3[r7] = r0
            goto L_0x04b3
        L_0x03d8:
            r1 = r58
            if (r1 == 0) goto L_0x0404
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30
            r11.<init>(r8)
            org.telegram.tgnet.TLRPC$TL_account_getTheme r2 = new org.telegram.tgnet.TLRPC$TL_account_getTheme
            r2.<init>()
            r2.format = r0
            org.telegram.tgnet.TLRPC$TL_inputThemeSlug r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug
            r0.<init>()
            r0.slug = r1
            r2.theme = r0
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
            r1.<init>(r8, r10)
            int r0 = r0.sendRequest(r2, r1)
            r3[r7] = r0
            goto L_0x04b4
        L_0x0404:
            if (r15 == 0) goto L_0x04b3
            if (r46 == 0) goto L_0x04b3
            if (r48 == 0) goto L_0x0461
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r35)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r15)
            if (r0 == 0) goto L_0x042a
            r36 = r34
            r37 = r35
            r38 = r10
            r39 = r46
            r40 = r49
            r41 = r48
            r42 = r0
            int r0 = r36.runCommentRequest(r37, r38, r39, r40, r41, r42)
            r3[r7] = r0
            goto L_0x04b3
        L_0x042a:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r0 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            long r4 = r47.longValue()
            r1.channel_id = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r4 = r0.id
            r4.add(r1)
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87
            r36 = r4
            r37 = r34
            r38 = r3
            r39 = r35
            r40 = r10
            r41 = r46
            r42 = r49
            r43 = r48
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)
            int r0 = r1.sendRequest(r0, r4)
            r3[r7] = r0
            goto L_0x04b3
        L_0x0461:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r5 = r47.longValue()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r5)
            int r1 = r46.intValue()
            java.lang.String r5 = "message_id"
            r0.putInt(r5, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x048e
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r5 = r1.size()
            int r5 = r5 - r4
            java.lang.Object r1 = r1.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x048f
        L_0x048e:
            r1 = r2
        L_0x048f:
            if (r1 == 0) goto L_0x049b
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r35)
            boolean r4 = r4.checkCanOpenChat(r0, r1)
            if (r4 == 0) goto L_0x04b3
        L_0x049b:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda36 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda36
            r36 = r4
            r37 = r34
            r38 = r0
            r39 = r47
            r40 = r3
            r41 = r10
            r42 = r1
            r43 = r35
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x04b3:
            r11 = r2
        L_0x04b4:
            r0 = r3[r7]
            if (r0 == 0) goto L_0x04c5
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1
            r0.<init>(r9, r3, r11)
            r10.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r10.showDelayed(r0)     // Catch:{ Exception -> 0x04c5 }
        L_0x04c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$29(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, Integer num, Long l, Integer num2, Integer num3, String str10, HashMap hashMap, String str11, String str12, String str13, String str14, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str15, String str16, String str17, String str18, int i2, String str19, String str20, String str21, int i3) {
        int i4 = i3;
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i3, str, str2, str3, str4, str5, str6, str7, str8, str9, z, num, l, num2, num3, str10, hashMap, str11, str12, str13, str14, tLRPC$TL_wallPaper, str15, str16, str17, str18, 1, i2, str19, str20, str21);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$32(int i, String str, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda50(this, tLRPC$TL_error, tLObject, i, str, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$31(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i, String str, AlertDialog alertDialog) {
        PaymentFormActivity paymentFormActivity;
        if (tLRPC$TL_error != null) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BulletinFactory.of(arrayList.get(arrayList.size() - 1)).createErrorBulletin(LocaleController.getString(NUM)).show();
        } else if (!isFinishing()) {
            if (tLObject instanceof TLRPC$TL_payments_paymentForm) {
                TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = (TLRPC$TL_payments_paymentForm) tLObject;
                MessagesController.getInstance(i).putUsers(tLRPC$TL_payments_paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm, str, getActionBarLayout().getLastFragment());
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$TL_payments_paymentReceipt ? new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                Runnable runnable = this.navigateToPremiumGiftCallback;
                if (runnable != null) {
                    this.navigateToPremiumGiftCallback = null;
                    paymentFormActivity.setPaymentFormCallback(new LaunchActivity$$ExternalSyntheticLambda105(runnable));
                }
                lambda$runLinkRequest$61(paymentFormActivity);
            }
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$30(Runnable runnable, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$46(String str, String str2, String str3, int i, String str4, String str5, String str6, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str7, String str8, String str9, String str10, int i2, String str11, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity$$ExternalSyntheticLambda46 launchActivity$$ExternalSyntheticLambda46 = r0;
        LaunchActivity$$ExternalSyntheticLambda46 launchActivity$$ExternalSyntheticLambda462 = new LaunchActivity$$ExternalSyntheticLambda46(this, tLObject, tLRPC$TL_error, str, str2, str3, i, str4, str5, str6, num, num2, num3, iArr, alertDialog, str7, str8, str9, str10, i2, str11);
        AndroidUtilities.runOnUIThread(launchActivity$$ExternalSyntheticLambda46, 2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0119, code lost:
        if (r29[0] != 0) goto L_0x011b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x017e, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x019d, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0180;
     */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0219  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$45(org.telegram.tgnet.TLObject r17, org.telegram.tgnet.TLRPC$TL_error r18, java.lang.String r19, java.lang.String r20, java.lang.String r21, int r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.Integer r26, java.lang.Integer r27, java.lang.Integer r28, int[] r29, org.telegram.ui.ActionBar.AlertDialog r30, java.lang.String r31, java.lang.String r32, java.lang.String r33, java.lang.String r34, int r35, java.lang.String r36) {
        /*
            r16 = this;
            r8 = r16
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            r6 = r23
            r4 = r24
            r5 = r34
            r7 = r35
            boolean r9 = r16.isFinishing()
            if (r9 != 0) goto L_0x043d
            r9 = r17
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r9 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r9
            r10 = 1
            r11 = 0
            if (r0 != 0) goto L_0x03c6
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r8.actionBarLayout
            if (r12 == 0) goto L_0x03c6
            if (r1 != 0) goto L_0x0028
            if (r2 == 0) goto L_0x0046
        L_0x0028:
            if (r1 == 0) goto L_0x0032
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            boolean r12 = r12.isEmpty()
            if (r12 == 0) goto L_0x0046
        L_0x0032:
            if (r2 == 0) goto L_0x003c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 == 0) goto L_0x0046
        L_0x003c:
            if (r3 == 0) goto L_0x03c6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x03c6
        L_0x0046:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            r0.putUsers(r12, r11)
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            r0.putChats(r12, r11)
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r22)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r13 = r9.chats
            r0.putUsersAndChats(r12, r13, r11, r10)
            if (r6 == 0) goto L_0x00ee
            if (r4 != 0) goto L_0x00ee
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            org.telegram.tgnet.TLRPC$Peer r1 = r9.peer
            long r1 = r1.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r5 = r0.getUser(r1)
            if (r5 == 0) goto L_0x00cd
            boolean r0 = r5.bot
            if (r0 == 0) goto L_0x00cd
            boolean r0 = r5.bot_attach_menu
            if (r0 == 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot r0 = new org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot
            r0.<init>()
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r22)
            org.telegram.tgnet.TLRPC$Peer r2 = r9.peer
            long r2 = r2.user_id
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((long) r2)
            r0.bot = r1
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72
            r1 = r12
            r2 = r16
            r3 = r22
            r4 = r25
            r6 = r23
            r7 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r11.sendRequest(r0, r12)
            goto L_0x0432
        L_0x00ac:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)
            r1 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0432
        L_0x00cd:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)
            r1 = 2131624749(0x7f0e032d, float:1.8876687E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0432
        L_0x00ee:
            if (r26 == 0) goto L_0x011e
            if (r27 != 0) goto L_0x00f4
            if (r28 == 0) goto L_0x011e
        L_0x00f4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x011e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            java.lang.Object r0 = r0.get(r11)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r16
            r2 = r22
            r3 = r30
            r4 = r26
            r5 = r27
            r6 = r28
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r29[r11] = r0
            r0 = r29[r11]
            if (r0 == 0) goto L_0x0432
        L_0x011b:
            r10 = 0
            goto L_0x0432
        L_0x011e:
            java.lang.String r0 = "dialogsType"
            java.lang.String r12 = "onlySelect"
            if (r1 == 0) goto L_0x0220
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r12, r10)
            java.lang.String r3 = "cantSendToChannels"
            r2.putBoolean(r3, r10)
            r2.putInt(r0, r10)
            r0 = 2131628263(0x7f0e10e7, float:1.8883814E38)
            java.lang.String r3 = "SendGameToText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            java.lang.String r3 = "selectAlertString"
            r2.putString(r3, r0)
            r0 = 2131628262(0x7f0e10e6, float:1.8883812E38)
            java.lang.String r3 = "SendGameToGroupText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            java.lang.String r3 = "selectAlertStringGroup"
            r2.putString(r3, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda100 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda100
            r13 = r22
            r2.<init>(r8, r1, r13, r9)
            r0.setDelegate(r2)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0184
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x0182
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x0182
        L_0x0180:
            r1 = 1
            goto L_0x01a0
        L_0x0182:
            r1 = 0
            goto L_0x01a0
        L_0x0184:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= r10) goto L_0x0182
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x0182
            goto L_0x0180
        L_0x01a0:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            r3 = 1
            r4 = 1
            r5 = 0
            r17 = r2
            r18 = r0
            r19 = r1
            r20 = r3
            r21 = r4
            r22 = r5
            r17.presentFragment(r18, r19, r20, r21, r22)
            boolean r0 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r0 == 0) goto L_0x01cc
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x01cc
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r11, r11)
            goto L_0x01fb
        L_0x01cc:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x01e4
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x01e4
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r10)
            goto L_0x01fb
        L_0x01e4:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x01fb
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x01fb
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r11, r10)
        L_0x01fb:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x0202
            r0.dismiss()
        L_0x0202:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r11)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0219
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x0432
        L_0x0219:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r11)
            goto L_0x0432
        L_0x0220:
            r13 = r22
            if (r31 != 0) goto L_0x031f
            if (r32 == 0) goto L_0x0228
            goto L_0x031f
        L_0x0228:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x0250
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC$Chat) r12
            long r14 = r12.id
            java.lang.String r12 = "chat_id"
            r0.putLong(r12, r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC$Chat) r12
            long r14 = r12.id
            long r14 = -r14
            goto L_0x0269
        L_0x0250:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC$User) r12
            long r14 = r12.id
            java.lang.String r12 = "user_id"
            r0.putLong(r12, r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC$User) r12
            long r14 = r12.id
        L_0x0269:
            if (r5 == 0) goto L_0x0286
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            int r12 = r12.size()
            if (r12 <= 0) goto L_0x0286
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r9.users
            java.lang.Object r9 = r9.get(r11)
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC$User) r9
            boolean r9 = r9.bot
            if (r9 == 0) goto L_0x0286
            java.lang.String r9 = "botUser"
            r0.putString(r9, r5)
            r9 = 1
            goto L_0x0287
        L_0x0286:
            r9 = 0
        L_0x0287:
            boolean r12 = r8.navigateToPremiumBot
            if (r12 == 0) goto L_0x0292
            r8.navigateToPremiumBot = r11
            java.lang.String r12 = "premium_bot"
            r0.putBoolean(r12, r10)
        L_0x0292:
            if (r26 == 0) goto L_0x029d
            int r12 = r26.intValue()
            java.lang.String r1 = "message_id"
            r0.putInt(r1, r12)
        L_0x029d:
            if (r2 == 0) goto L_0x02a4
            java.lang.String r1 = "voicechat"
            r0.putString(r1, r2)
        L_0x02a4:
            if (r3 == 0) goto L_0x02ab
            java.lang.String r1 = "livestream"
            r0.putString(r1, r3)
        L_0x02ab:
            if (r7 < 0) goto L_0x02b2
            java.lang.String r1 = "video_timestamp"
            r0.putInt(r1, r7)
        L_0x02b2:
            if (r4 == 0) goto L_0x02b9
            java.lang.String r1 = "attach_bot"
            r0.putString(r1, r4)
        L_0x02b9:
            if (r6 == 0) goto L_0x02c0
            java.lang.String r1 = "attach_bot_start_command"
            r0.putString(r1, r6)
        L_0x02c0:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x02d8
            if (r2 != 0) goto L_0x02d8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x02d9
        L_0x02d8:
            r1 = 0
        L_0x02d9:
            if (r1 == 0) goto L_0x02e5
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r22)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x0432
        L_0x02e5:
            if (r9 == 0) goto L_0x02fb
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x02fb
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            long r6 = r2.getDialogId()
            int r4 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r4 != 0) goto L_0x02fb
            r2.setBotUser(r5)
            goto L_0x0432
        L_0x02fb:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r22)
            if (r26 != 0) goto L_0x0303
            r4 = 0
            goto L_0x0307
        L_0x0303:
            int r4 = r26.intValue()
        L_0x0307:
            org.telegram.ui.LaunchActivity$14 r5 = new org.telegram.ui.LaunchActivity$14
            r22 = r5
            r23 = r16
            r24 = r30
            r25 = r21
            r26 = r1
            r27 = r14
            r29 = r0
            r22.<init>(r24, r25, r26, r27, r29)
            r2.ensureMessagesLoaded(r14, r4, r5)
            goto L_0x011b
        L_0x031f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r9.users
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0330
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r9.users
            java.lang.Object r1 = r1.get(r11)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            goto L_0x0331
        L_0x0330:
            r1 = 0
        L_0x0331:
            if (r1 == 0) goto L_0x0397
            boolean r2 = r1.bot
            if (r2 == 0) goto L_0x033c
            boolean r2 = r1.bot_nochats
            if (r2 == 0) goto L_0x033c
            goto L_0x0397
        L_0x033c:
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r12, r10)
            r3 = 2
            r2.putInt(r0, r3)
            java.lang.String r0 = "resetDelegate"
            r2.putBoolean(r0, r11)
            java.lang.String r0 = "closeFragment"
            r2.putBoolean(r0, r11)
            if (r31 == 0) goto L_0x0356
            r0 = 1
            goto L_0x0357
        L_0x0356:
            r0 = 0
        L_0x0357:
            java.lang.String r3 = "allowGroups"
            r2.putBoolean(r3, r0)
            if (r32 == 0) goto L_0x035f
            r11 = 1
        L_0x035f:
            java.lang.String r0 = "allowChannels"
            r2.putBoolean(r0, r11)
            boolean r0 = android.text.TextUtils.isEmpty(r31)
            if (r0 == 0) goto L_0x0375
            boolean r0 = android.text.TextUtils.isEmpty(r32)
            if (r0 == 0) goto L_0x0372
            r0 = 0
            goto L_0x0377
        L_0x0372:
            r0 = r32
            goto L_0x0377
        L_0x0375:
            r0 = r31
        L_0x0377:
            org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
            r3.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99
            r23 = r2
            r24 = r16
            r25 = r22
            r26 = r1
            r27 = r33
            r28 = r0
            r29 = r3
            r23.<init>(r24, r25, r26, r27, r28, r29)
            r3.setDelegate(r2)
            r8.lambda$runLinkRequest$61(r3)
            goto L_0x0432
        L_0x0397:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x03c1 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x03c1 }
            if (r0 != 0) goto L_0x03c5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x03c1 }
            int r1 = r0.size()     // Catch:{ Exception -> 0x03c1 }
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x03c1 }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x03c1 }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x03c1 }
            java.lang.String r1 = "BotCantJoinGroups"
            r2 = 2131624726(0x7f0e0316, float:1.887664E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x03c1 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x03c1 }
            r0.show()     // Catch:{ Exception -> 0x03c1 }
            goto L_0x03c5
        L_0x03c1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c5:
            return
        L_0x03c6:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x042e }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x042e }
            if (r1 != 0) goto L_0x0432
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x042e }
            int r2 = r1.size()     // Catch:{ Exception -> 0x042e }
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x042e }
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1     // Catch:{ Exception -> 0x042e }
            if (r0 == 0) goto L_0x03fe
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x042e }
            if (r0 == 0) goto L_0x03fe
            java.lang.String r2 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r2)     // Catch:{ Exception -> 0x042e }
            if (r0 == 0) goto L_0x03fe
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x042e }
            java.lang.String r1 = "FloodWait"
            r2 = 2131625962(0x7f0e07ea, float:1.8879147E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x042e }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x042e }
            r0.show()     // Catch:{ Exception -> 0x042e }
            goto L_0x0432
        L_0x03fe:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isNumeric(r36)     // Catch:{ Exception -> 0x042e }
            if (r0 == 0) goto L_0x0419
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x042e }
            java.lang.String r1 = "NoPhoneFound"
            r2 = 2131626911(0x7f0e0b9f, float:1.8881072E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x042e }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x042e }
            r0.show()     // Catch:{ Exception -> 0x042e }
            goto L_0x0432
        L_0x0419:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x042e }
            java.lang.String r1 = "NoUsernameFound"
            r2 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x042e }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x042e }
            r0.show()     // Catch:{ Exception -> 0x042e }
            goto L_0x0432
        L_0x042e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0432:
            if (r10 == 0) goto L_0x043d
            r30.dismiss()     // Catch:{ Exception -> 0x0438 }
            goto L_0x043d
        L_0x0438:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x043d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$45(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$38(int i, String str, TLRPC$User tLRPC$User, String str2, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda43(this, tLObject, i, str, tLRPC$User, str2, tLRPC$TL_contacts_resolvedPeer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$37(TLObject tLObject, int i, String str, TLRPC$User tLRPC$User, String str2, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer) {
        DialogsActivity dialogsActivity;
        TLObject tLObject2 = tLObject;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        String str3 = str2;
        if (tLObject2 instanceof TLRPC$TL_attachMenuBotsBot) {
            TLRPC$TL_attachMenuBotsBot tLRPC$TL_attachMenuBotsBot = (TLRPC$TL_attachMenuBotsBot) tLObject2;
            MessagesController.getInstance(i).putUsers(tLRPC$TL_attachMenuBotsBot.users, false);
            TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = tLRPC$TL_attachMenuBotsBot.bot;
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
            ArrayList arrayList2 = new ArrayList();
            if (!TextUtils.isEmpty(str)) {
                for (String str4 : str.split(" ")) {
                    if (MediaDataController.canShowAttachMenuBotForTarget(tLRPC$TL_attachMenuBot, str4)) {
                        arrayList2.add(str4);
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putInt("dialogsType", 14);
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("allowGroups", arrayList2.contains("groups"));
                bundle.putBoolean("allowUsers", arrayList2.contains("users"));
                bundle.putBoolean("allowChannels", arrayList2.contains("channels"));
                bundle.putBoolean("allowBots", arrayList2.contains("bots"));
                DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                dialogsActivity2.setDelegate(new LaunchActivity$$ExternalSyntheticLambda101(this, tLRPC$User2, str3, i));
                dialogsActivity = dialogsActivity2;
            } else {
                int i2 = i;
                dialogsActivity = null;
            }
            if (tLRPC$TL_attachMenuBot.inactive) {
                AttachBotIntroTopView attachBotIntroTopView = new AttachBotIntroTopView(this);
                attachBotIntroTopView.setColor(Theme.getColor("chat_attachContactIcon"));
                attachBotIntroTopView.setBackgroundColor(Theme.getColor("dialogTopBackground"));
                attachBotIntroTopView.setAttachBot(tLRPC$TL_attachMenuBot);
                new AlertDialog.Builder((Context) this).setTopView(attachBotIntroTopView).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotRequestAttachPermission", NUM, UserObject.getUserName(tLRPC$User)))).setPositiveButton(LocaleController.getString(NUM), new LaunchActivity$$ExternalSyntheticLambda10(this, i, tLRPC$TL_contacts_resolvedPeer, dialogsActivity, baseFragment, tLRPC$User, str2)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
            } else if (dialogsActivity != null) {
                lambda$runLinkRequest$61(dialogsActivity);
            } else if (baseFragment instanceof ChatActivity) {
                ((ChatActivity) baseFragment).openAttachBotLayout(tLRPC$User2.id, str3);
            } else {
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(NUM)).show();
            }
        } else {
            ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
            BulletinFactory.of(arrayList3.get(arrayList3.size() - 1)).createErrorBulletin(LocaleController.getString(NUM)).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$33(TLRPC$User tLRPC$User, String str, int i, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue));
        } else if (DialogObject.isUserDialog(longValue)) {
            bundle.putLong("user_id", longValue);
        } else {
            bundle.putLong("chat_id", -longValue);
        }
        bundle.putString("attach_bot", tLRPC$User.username);
        if (str != null) {
            bundle.putString("attach_bot_start_command", str);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$36(int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i2) {
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(tLRPC$TL_contacts_resolvedPeer.peer.user_id);
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = true;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new LaunchActivity$$ExternalSyntheticLambda76(this, i, dialogsActivity, baseFragment, tLRPC$User, str), 66);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$35(int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda44(this, tLObject, i, dialogsActivity, baseFragment, tLRPC$User, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$34(TLObject tLObject, int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
            if (dialogsActivity != null) {
                lambda$runLinkRequest$61(dialogsActivity);
            } else if (baseFragment instanceof ChatActivity) {
                ((ChatActivity) baseFragment).openAttachBotLayout(tLRPC$User.id, str);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$39(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        ArrayList arrayList2 = arrayList;
        long longValue = ((Long) arrayList.get(0)).longValue();
        TLRPC$TL_inputMediaGame tLRPC$TL_inputMediaGame = new TLRPC$TL_inputMediaGame();
        TLRPC$TL_inputGameShortName tLRPC$TL_inputGameShortName = new TLRPC$TL_inputGameShortName();
        tLRPC$TL_inputMediaGame.id = tLRPC$TL_inputGameShortName;
        tLRPC$TL_inputGameShortName.short_name = str;
        tLRPC$TL_inputGameShortName.bot_id = MessagesController.getInstance(i).getInputUser(tLRPC$TL_contacts_resolvedPeer.users.get(0));
        SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer(longValue), tLRPC$TL_inputMediaGame, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue));
        } else if (DialogObject.isUserDialog(longValue)) {
            bundle.putLong("user_id", longValue);
        } else {
            bundle.putLong("chat_id", -longValue);
        }
        DialogsActivity dialogsActivity2 = dialogsActivity;
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$44(int i, TLRPC$User tLRPC$User, String str, String str2, DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
        String str3;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        long longValue = ((Long) arrayList.get(0)).longValue();
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-longValue));
        if (chat == null || (!chat.creator && ((tLRPC$TL_chatAdminRights = chat.admin_rights) == null || !tLRPC$TL_chatAdminRights.add_admins))) {
            TLRPC$User tLRPC$User2 = tLRPC$User;
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
            builder.setTitle(LocaleController.getString("AddBot", NUM));
            if (chat == null) {
                str3 = "";
            } else {
                str3 = chat.title;
            }
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, UserObject.getUserName(tLRPC$User), str3)));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("AddBot", NUM), new LaunchActivity$$ExternalSyntheticLambda11(this, longValue, i, tLRPC$User, str2));
            builder.show();
            return;
        }
        MessagesController.getInstance(i).checkIsInChat(chat, tLRPC$User, new LaunchActivity$$ExternalSyntheticLambda67(this, str, str2, i, chat, dialogsActivity, tLRPC$User, longValue));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$42(String str, String str2, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, long j, boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str3) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, str, tLRPC$TL_chatAdminRights, z, str2, i, tLRPC$Chat, dialogsActivity, tLRPC$User, j, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$41(String str, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, boolean z, String str2, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, long j, String str3) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3;
        String str4 = str;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights4 = tLRPC$TL_chatAdminRights;
        final int i2 = i;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        final DialogsActivity dialogsActivity2 = dialogsActivity;
        if (str4 != null) {
            String[] split = str4.split("\\+| ");
            tLRPC$TL_chatAdminRights2 = new TLRPC$TL_chatAdminRights();
            for (String str5 : split) {
                str5.hashCode();
                char c = 65535;
                switch (str5.hashCode()) {
                    case -2110462504:
                        if (str5.equals("ban_users")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -2095811475:
                        if (str5.equals("anonymous")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1654794275:
                        if (str5.equals("change_info")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1593320096:
                        if (str5.equals("delete_messages")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -939200543:
                        if (str5.equals("edit_messages")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 22162680:
                        if (str5.equals("manage_call")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 22169074:
                        if (str5.equals("manage_chat")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 106069776:
                        if (str5.equals("other")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 449085338:
                        if (str5.equals("promote_members")) {
                            c = 8;
                            break;
                        }
                        break;
                    case 632157522:
                        if (str5.equals("invite_users")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 758599179:
                        if (str5.equals("post_messages")) {
                            c = 10;
                            break;
                        }
                        break;
                    case 1357805750:
                        if (str5.equals("pin_messages")) {
                            c = 11;
                            break;
                        }
                        break;
                    case 1529816162:
                        if (str5.equals("add_admins")) {
                            c = 12;
                            break;
                        }
                        break;
                    case 1542893206:
                        if (str5.equals("restrict_members")) {
                            c = 13;
                            break;
                        }
                        break;
                    case 1641337725:
                        if (str5.equals("manage_video_chats")) {
                            c = 14;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 13:
                        tLRPC$TL_chatAdminRights2.ban_users = true;
                        break;
                    case 1:
                        tLRPC$TL_chatAdminRights2.anonymous = true;
                        break;
                    case 2:
                        tLRPC$TL_chatAdminRights2.change_info = true;
                        break;
                    case 3:
                        tLRPC$TL_chatAdminRights2.delete_messages = true;
                        break;
                    case 4:
                        tLRPC$TL_chatAdminRights2.edit_messages = true;
                        break;
                    case 5:
                    case 14:
                        tLRPC$TL_chatAdminRights2.manage_call = true;
                        break;
                    case 6:
                    case 7:
                        tLRPC$TL_chatAdminRights2.other = true;
                        break;
                    case 8:
                    case 12:
                        tLRPC$TL_chatAdminRights2.add_admins = true;
                        break;
                    case 9:
                        tLRPC$TL_chatAdminRights2.invite_users = true;
                        break;
                    case 10:
                        tLRPC$TL_chatAdminRights2.post_messages = true;
                        break;
                    case 11:
                        tLRPC$TL_chatAdminRights2.pin_messages = true;
                        break;
                }
            }
        } else {
            tLRPC$TL_chatAdminRights2 = null;
        }
        if (tLRPC$TL_chatAdminRights2 == null && tLRPC$TL_chatAdminRights4 == null) {
            tLRPC$TL_chatAdminRights3 = null;
        } else {
            if (tLRPC$TL_chatAdminRights2 != null) {
                if (tLRPC$TL_chatAdminRights4 == null) {
                    tLRPC$TL_chatAdminRights3 = tLRPC$TL_chatAdminRights2;
                } else {
                    tLRPC$TL_chatAdminRights4.change_info = tLRPC$TL_chatAdminRights2.change_info || tLRPC$TL_chatAdminRights4.change_info;
                    tLRPC$TL_chatAdminRights4.post_messages = tLRPC$TL_chatAdminRights2.post_messages || tLRPC$TL_chatAdminRights4.post_messages;
                    tLRPC$TL_chatAdminRights4.edit_messages = tLRPC$TL_chatAdminRights2.edit_messages || tLRPC$TL_chatAdminRights4.edit_messages;
                    tLRPC$TL_chatAdminRights4.add_admins = tLRPC$TL_chatAdminRights2.add_admins || tLRPC$TL_chatAdminRights4.add_admins;
                    tLRPC$TL_chatAdminRights4.delete_messages = tLRPC$TL_chatAdminRights2.delete_messages || tLRPC$TL_chatAdminRights4.delete_messages;
                    tLRPC$TL_chatAdminRights4.ban_users = tLRPC$TL_chatAdminRights2.ban_users || tLRPC$TL_chatAdminRights4.ban_users;
                    tLRPC$TL_chatAdminRights4.invite_users = tLRPC$TL_chatAdminRights2.invite_users || tLRPC$TL_chatAdminRights4.invite_users;
                    tLRPC$TL_chatAdminRights4.pin_messages = tLRPC$TL_chatAdminRights2.pin_messages || tLRPC$TL_chatAdminRights4.pin_messages;
                    tLRPC$TL_chatAdminRights4.manage_call = tLRPC$TL_chatAdminRights2.manage_call || tLRPC$TL_chatAdminRights4.manage_call;
                    tLRPC$TL_chatAdminRights4.anonymous = tLRPC$TL_chatAdminRights2.anonymous || tLRPC$TL_chatAdminRights4.anonymous;
                    tLRPC$TL_chatAdminRights4.other = tLRPC$TL_chatAdminRights2.other || tLRPC$TL_chatAdminRights4.other;
                }
            }
            tLRPC$TL_chatAdminRights3 = tLRPC$TL_chatAdminRights4;
        }
        if (!z || tLRPC$TL_chatAdminRights2 != null || TextUtils.isEmpty(str2)) {
            ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(tLRPC$User.id, -j, tLRPC$TL_chatAdminRights3, (TLRPC$TL_chatBannedRights) null, (TLRPC$TL_chatBannedRights) null, str3, 2, true, !z, str2);
            chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate(this) {
                public void didChangeOwner(TLRPC$User tLRPC$User) {
                }

                public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                    dialogsActivity2.removeSelfFromStack();
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
            });
            this.actionBarLayout.presentFragment(chatRightsEditActivity, false);
            return;
        }
        MessagesController.getInstance(this.currentAccount).addUserToChat(tLRPC$Chat2.id, tLRPC$User, 0, str2, dialogsActivity, true, new LaunchActivity$$ExternalSyntheticLambda35(this, i2, tLRPC$Chat2, dialogsActivity2), (MessagesController.ErrorDelegate) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$40(int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity) {
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putLong("chat_id", tLRPC$Chat.id);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            presentFragment(new ChatActivity(bundle), true, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$43(long j, int i, TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        long j2 = -j;
        bundle.putLong("chat_id", j2);
        ChatActivity chatActivity = new ChatActivity(bundle);
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(j2, tLRPC$User, 0, str, chatActivity, (Runnable) null);
        this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$49(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda51(this, tLRPC$TL_error, tLObject, i, alertDialog, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r10.chat.has_geo != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0079, code lost:
        if (r11.checkCanOpenChat(r7, r14.get(r14.size() - 1)) != false) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$48(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x0132
            r0 = 0
            r1 = 1
            if (r10 != 0) goto L_0x00c2
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x00c2
            r10 = r11
            org.telegram.tgnet.TLRPC$ChatInvite r10 = (org.telegram.tgnet.TLRPC$ChatInvite) r10
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            if (r11 == 0) goto L_0x009d
            boolean r11 = org.telegram.messenger.ChatObject.isLeftFromChat(r11)
            if (r11 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            boolean r2 = r11.kicked
            if (r2 != 0) goto L_0x009d
            java.lang.String r11 = r11.username
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x0033
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek
            if (r11 != 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            boolean r11 = r11.has_geo
            if (r11 == 0) goto L_0x009d
        L_0x0033:
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r14 = r10.chat
            r2 = 0
            r11.putChat(r14, r2)
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            org.telegram.tgnet.TLRPC$Chat r14 = r10.chat
            r11.add(r14)
            org.telegram.messenger.MessagesStorage r14 = org.telegram.messenger.MessagesStorage.getInstance(r12)
            r14.putUsersAndChats(r0, r11, r2, r1)
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            long r3 = r11.id
            java.lang.String r11 = "chat_id"
            r7.putLong(r11, r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x007b
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r14 = mainFragmentsStack
            int r0 = r14.size()
            int r0 = r0 - r1
            java.lang.Object r14 = r14.get(r0)
            org.telegram.ui.ActionBar.BaseFragment r14 = (org.telegram.ui.ActionBar.BaseFragment) r14
            boolean r11 = r11.checkCanOpenChat(r7, r14)
            if (r11 == 0) goto L_0x0128
        L_0x007b:
            boolean[] r6 = new boolean[r1]
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3
            r11.<init>(r6)
            r13.setOnCancelListener(r11)
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r12 = r10.chat
            long r0 = r12.id
            long r0 = -r0
            org.telegram.ui.LaunchActivity$15 r12 = new org.telegram.ui.LaunchActivity$15
            r3 = r12
            r4 = r9
            r5 = r13
            r8 = r10
            r3.<init>(r5, r6, r7, r8)
            r11.ensureMessagesLoaded(r0, r2, r12)
            r1 = 0
            goto L_0x0128
        L_0x009d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            int r12 = r11.size()
            int r12 = r12 - r1
            java.lang.Object r11 = r11.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            org.telegram.ui.Components.JoinGroupAlert r12 = new org.telegram.ui.Components.JoinGroupAlert
            boolean r2 = r11 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x00b5
            r0 = r11
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.ui.ChatActivity$ThemeDelegate r0 = r0.themeDelegate
        L_0x00b5:
            r8 = r0
            r3 = r12
            r4 = r9
            r5 = r10
            r6 = r14
            r7 = r11
            r3.<init>(r4, r5, r6, r7, r8)
            r11.showDialog(r12)
            goto L_0x0128
        L_0x00c2:
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r11.<init>((android.content.Context) r9)
            r12 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r12 = r10.text
            java.lang.String r14 = "FLOOD_WAIT"
            boolean r12 = r12.startsWith(r14)
            if (r12 == 0) goto L_0x00ea
            r10 = 2131625962(0x7f0e07ea, float:1.8879147E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0119
        L_0x00ea:
            java.lang.String r10 = r10.text
            java.lang.String r12 = "INVITE_HASH_EXPIRED"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x010d
            r10 = 2131625842(0x7f0e0772, float:1.8878903E38)
            java.lang.String r12 = "ExpiredLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setTitle(r10)
            r10 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.String r12 = "InviteExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0119
        L_0x010d:
            r10 = 2131626385(0x7f0e0991, float:1.8880005E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x0119:
            r10 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r12 = "OK"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setPositiveButton(r10, r0)
            r9.showAlertDialog(r11)
        L_0x0128:
            if (r1 == 0) goto L_0x0132
            r13.dismiss()     // Catch:{ Exception -> 0x012e }
            goto L_0x0132
        L_0x012e:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0132:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$48(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$47(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$51(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda60(this, alertDialog, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$50(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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
                    bundle.putLong("chat_id", tLRPC$Chat.id);
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
    public /* synthetic */ void lambda$runLinkRequest$52(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
        ArrayList arrayList2 = arrayList;
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        boolean z3 = z;
        bundle.putBoolean("hasUrl", z);
        if (DialogObject.isEncryptedDialog(longValue)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue));
        } else if (DialogObject.isUserDialog(longValue)) {
            bundle.putLong("user_id", longValue);
        } else {
            bundle.putLong("chat_id", -longValue);
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
    public /* synthetic */ void lambda$runLinkRequest$56(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda82(this, alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda58(this, alertDialog, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$54(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda54(this, alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$53(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$61(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$55(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$58(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda53(this, alertDialog, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$57(AlertDialog alertDialog, TLObject tLObject) {
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
    public /* synthetic */ void lambda$runLinkRequest$60(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda55(this, alertDialog, tLObject, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$59(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$63(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda56(this, alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$62(org.telegram.ui.ActionBar.AlertDialog r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC$TL_wallPaper r13, org.telegram.tgnet.TLRPC$TL_error r14) {
        /*
            r10 = this;
            r11.dismiss()     // Catch:{ Exception -> 0x0004 }
            goto L_0x0008
        L_0x0004:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x0008:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r11 == 0) goto L_0x004d
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r12
            boolean r11 = r12.pattern
            r14 = 0
            if (r11 == 0) goto L_0x0039
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r11 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r12.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r13.settings
            int r2 = r0.background_color
            int r3 = r0.second_background_color
            int r4 = r0.third_background_color
            int r5 = r0.fourth_background_color
            int r0 = r0.rotation
            int r6 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r14)
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r13.settings
            int r7 = r0.intensity
            float r7 = (float) r7
            r8 = 1120403456(0x42CLASSNAME, float:100.0)
            float r7 = r7 / r8
            boolean r8 = r0.motion
            r9 = 0
            r0 = r11
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r11.pattern = r12
            r12 = r11
        L_0x0039:
            org.telegram.ui.ThemePreviewActivity r11 = new org.telegram.ui.ThemePreviewActivity
            r0 = 0
            r1 = 1
            r11.<init>(r12, r0, r1, r14)
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r13.settings
            boolean r13 = r12.blur
            boolean r12 = r12.motion
            r11.setInitialModes(r13, r12)
            r10.lambda$runLinkRequest$61(r11)
            goto L_0x0073
        L_0x004d:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 2131625708(0x7f0e06ec, float:1.8878632E38)
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
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$62(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$64() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$66(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda47(this, tLObject, alertDialog, tLRPC$TL_error));
    }

    /* JADX WARNING: type inference failed for: r8v11, types: [org.telegram.tgnet.TLRPC$WallPaper] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009b A[SYNTHETIC, Splitter:B:29:0x009b] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$65(org.telegram.tgnet.TLObject r6, org.telegram.ui.ActionBar.AlertDialog r7, org.telegram.tgnet.TLRPC$TL_error r8) {
        /*
            r5 = this;
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_theme
            r1 = 1
            if (r0 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$TL_theme r6 = (org.telegram.tgnet.TLRPC$TL_theme) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r8 = r6.settings
            int r8 = r8.size()
            r0 = 0
            r2 = 0
            if (r8 <= 0) goto L_0x001a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r8 = r6.settings
            java.lang.Object r8 = r8.get(r2)
            org.telegram.tgnet.TLRPC$ThemeSettings r8 = (org.telegram.tgnet.TLRPC$ThemeSettings) r8
            goto L_0x001b
        L_0x001a:
            r8 = r0
        L_0x001b:
            if (r8 == 0) goto L_0x006c
            java.lang.String r3 = org.telegram.ui.ActionBar.Theme.getBaseThemeKey(r8)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
            if (r3 == 0) goto L_0x0096
            org.telegram.tgnet.TLRPC$WallPaper r8 = r8.wallpaper
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r4 == 0) goto L_0x0060
            r0 = r8
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            int r8 = r5.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r8)
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            java.io.File r8 = r8.getPathToAttach(r4, r1)
            boolean r8 = r8.exists()
            if (r8 != 0) goto L_0x0060
            r5.loadingThemeProgressDialog = r7
            r5.loadingThemeAccent = r1
            r5.loadingThemeInfo = r3
            r5.loadingTheme = r6
            r5.loadingThemeWallpaper = r0
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            r5.loadingThemeWallpaperName = r6
            int r6 = r5.currentAccount
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            r6.loadFile(r7, r0, r1, r1)
            return
        L_0x0060:
            r7.dismiss()     // Catch:{ Exception -> 0x0064 }
            goto L_0x0068
        L_0x0064:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0068:
            r5.openThemeAccentPreview(r6, r0, r3)
            goto L_0x0099
        L_0x006c:
            org.telegram.tgnet.TLRPC$Document r8 = r6.document
            if (r8 == 0) goto L_0x0096
            r5.loadingThemeAccent = r2
            r5.loadingTheme = r6
            java.lang.String r8 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            r5.loadingThemeFileName = r8
            r5.loadingThemeProgressDialog = r7
            int r8 = r5.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r8)
            org.telegram.tgnet.TLRPC$TL_theme r0 = r5.loadingTheme
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            r8.loadFile(r0, r6, r1, r1)
            goto L_0x0099
        L_0x008a:
            if (r8 == 0) goto L_0x0098
            java.lang.String r6 = r8.text
            java.lang.String r8 = "THEME_FORMAT_INVALID"
            boolean r6 = r8.equals(r6)
            if (r6 == 0) goto L_0x0098
        L_0x0096:
            r2 = 1
            goto L_0x0099
        L_0x0098:
            r2 = 2
        L_0x0099:
            if (r2 == 0) goto L_0x00d3
            r7.dismiss()     // Catch:{ Exception -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00a3:
            r6 = 2131628679(0x7f0e1287, float:1.8884657E38)
            java.lang.String r7 = "Theme"
            if (r2 != r1) goto L_0x00bf
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628701(0x7f0e129d, float:1.8884702E38)
            java.lang.String r8 = "ThemeNotSupported"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
            goto L_0x00d3
        L_0x00bf:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628700(0x7f0e129c, float:1.88847E38)
            java.lang.String r8 = "ThemeNotFound"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
        L_0x00d3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$65(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$68(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda49(this, tLObject, iArr, i, alertDialog, num, num2, num3));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$67(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
            r0 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$67(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$71(Bundle bundle, Long l, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = l.longValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new LaunchActivity$$ExternalSyntheticLambda85(this, alertDialog, baseFragment, i, bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$70(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda57(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$69(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$72(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            long r9 = r8.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r9)
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
            long r12 = r11.user_id
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
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
            r13 = 2131627995(0x7f0e0fdb, float:1.888327E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131628153(0x7f0e1079, float:1.888359E38)
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

    private void createUpdateUI() {
        if (this.sideMenuContainer != null) {
            AnonymousClass16 r0 = new FrameLayout(this) {
                private int lastGradientWidth;
                private Matrix matrix = new Matrix();
                private Paint paint = new Paint();
                private LinearGradient updateGradient;

                public void draw(Canvas canvas) {
                    if (this.updateGradient != null) {
                        this.paint.setColor(-1);
                        this.paint.setShader(this.updateGradient);
                        this.updateGradient.setLocalMatrix(this.matrix);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
                        LaunchActivity.this.updateLayoutIcon.setBackgroundGradientDrawable(this.updateGradient);
                        LaunchActivity.this.updateLayoutIcon.draw(canvas);
                    }
                    super.draw(canvas);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    int size = View.MeasureSpec.getSize(i);
                    if (this.lastGradientWidth != size) {
                        this.updateGradient = new LinearGradient(0.0f, 0.0f, (float) size, 0.0f, new int[]{-9846926, -11291731}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                        this.lastGradientWidth = size;
                    }
                }
            };
            this.updateLayout = r0;
            r0.setWillNotDraw(false);
            this.updateLayout.setVisibility(4);
            this.updateLayout.setTranslationY((float) AndroidUtilities.dp(44.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.updateLayout.setBackground(Theme.getSelectorDrawable(NUM, false));
            }
            this.sideMenuContainer.addView(this.updateLayout, LayoutHelper.createFrame(-1, 44, 83));
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda16(this));
            RadialProgress2 radialProgress2 = new RadialProgress2(this.updateLayout);
            this.updateLayoutIcon = radialProgress2;
            radialProgress2.setColors(-1, -1, -1, -1);
            this.updateLayoutIcon.setProgressRect(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(44.0f), AndroidUtilities.dp(33.0f));
            this.updateLayoutIcon.setCircleRadius(AndroidUtilities.dp(11.0f));
            this.updateLayoutIcon.setAsMini();
            SimpleTextView simpleTextView = new SimpleTextView(this);
            this.updateTextView = simpleTextView;
            simpleTextView.setTextSize(15);
            this.updateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.updateTextView.setText(LocaleController.getString("AppUpdate", NUM));
            this.updateTextView.setTextColor(-1);
            this.updateTextView.setGravity(3);
            this.updateLayout.addView(this.updateTextView, LayoutHelper.createFrame(-2, -2.0f, 16, 74.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(this);
            this.updateSizeTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.updateSizeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.updateSizeTextView.setGravity(5);
            this.updateSizeTextView.setTextColor(-1);
            this.updateLayout.addView(this.updateSizeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 17.0f, 0.0f));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createUpdateUI$73(View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            if (this.updateLayoutIcon.getIcon() == 2) {
                FileLoader.getInstance(this.currentAccount).loadFile(SharedConfig.pendingAppUpdate.document, "update", 1, 1);
                updateAppUpdateViews(true);
            } else if (this.updateLayoutIcon.getIcon() == 3) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(SharedConfig.pendingAppUpdate.document);
                updateAppUpdateViews(true);
            } else {
                AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, (Activity) this);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0133 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0134  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAppUpdateViews(boolean r14) {
        /*
            r13 = this;
            android.widget.FrameLayout r0 = r13.sideMenuContainer
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            r1 = 1110441984(0x42300000, float:44.0)
            r2 = 0
            r3 = 180(0xb4, double:8.9E-322)
            r5 = 0
            if (r0 == 0) goto L_0x0183
            android.widget.FrameLayout r0 = r13.updateLayout
            r13.createUpdateUI()
            android.widget.TextView r6 = r13.updateSizeTextView
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r7 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            long r7 = r7.size
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r6.setText(r7)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            int r7 = r13.currentAccount
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r8 = r8.document
            r9 = 1
            java.io.File r7 = r7.getPathToAttach(r8, r9)
            boolean r7 = r7.exists()
            r8 = 0
            if (r7 == 0) goto L_0x005a
            org.telegram.ui.Components.RadialProgress2 r6 = r13.updateLayoutIcon
            r7 = 15
            r6.setIcon(r7, r9, r5)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r13.updateTextView
            r7 = 2131624396(0x7f0e01cc, float:1.887597E38)
            java.lang.String r10 = "AppUpdateNow"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6.setText(r7)
        L_0x0058:
            r6 = 0
            goto L_0x00b2
        L_0x005a:
            int r7 = r13.currentAccount
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)
            boolean r7 = r7.isLoadingFile(r6)
            if (r7 == 0) goto L_0x009d
            org.telegram.ui.Components.RadialProgress2 r7 = r13.updateLayoutIcon
            r10 = 3
            r7.setIcon(r10, r9, r5)
            org.telegram.ui.Components.RadialProgress2 r7 = r13.updateLayoutIcon
            r7.setProgress(r8, r5)
            org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r6 = r7.getFileProgress(r6)
            org.telegram.ui.ActionBar.SimpleTextView r7 = r13.updateTextView
            r10 = 2131624395(0x7f0e01cb, float:1.8875969E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            if (r6 == 0) goto L_0x0087
            float r6 = r6.floatValue()
            goto L_0x0088
        L_0x0087:
            r6 = 0
        L_0x0088:
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r6 = r6 * r12
            int r6 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r11[r5] = r6
            java.lang.String r6 = "AppUpdateDownloading"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r11)
            r7.setText(r6)
            goto L_0x0058
        L_0x009d:
            org.telegram.ui.Components.RadialProgress2 r6 = r13.updateLayoutIcon
            r7 = 2
            r6.setIcon(r7, r9, r5)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r13.updateTextView
            r7 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r10 = "AppUpdate"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6.setText(r7)
            r6 = 1
        L_0x00b2:
            if (r6 == 0) goto L_0x00ef
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Object r6 = r6.getTag()
            if (r6 == 0) goto L_0x012b
            r6 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x00df
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setTag(r2)
            android.widget.TextView r7 = r13.updateSizeTextView
            android.view.ViewPropertyAnimator r7 = r7.animate()
            android.view.ViewPropertyAnimator r7 = r7.alpha(r6)
            android.view.ViewPropertyAnimator r7 = r7.scaleX(r6)
            android.view.ViewPropertyAnimator r6 = r7.scaleY(r6)
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r3)
            r6.start()
            goto L_0x012b
        L_0x00df:
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setAlpha(r6)
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setScaleX(r6)
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setScaleY(r6)
            goto L_0x012b
        L_0x00ef:
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Object r6 = r6.getTag()
            if (r6 != 0) goto L_0x012b
            if (r14 == 0) goto L_0x011c
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Integer r7 = java.lang.Integer.valueOf(r9)
            r6.setTag(r7)
            android.widget.TextView r6 = r13.updateSizeTextView
            android.view.ViewPropertyAnimator r6 = r6.animate()
            android.view.ViewPropertyAnimator r6 = r6.alpha(r8)
            android.view.ViewPropertyAnimator r6 = r6.scaleX(r8)
            android.view.ViewPropertyAnimator r6 = r6.scaleY(r8)
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r3)
            r6.start()
            goto L_0x012b
        L_0x011c:
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setAlpha(r8)
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setScaleX(r8)
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setScaleY(r8)
        L_0x012b:
            android.widget.FrameLayout r6 = r13.updateLayout
            java.lang.Object r6 = r6.getTag()
            if (r6 == 0) goto L_0x0134
            return
        L_0x0134:
            android.widget.FrameLayout r6 = r13.updateLayout
            r6.setVisibility(r5)
            android.widget.FrameLayout r6 = r13.updateLayout
            java.lang.Integer r7 = java.lang.Integer.valueOf(r9)
            r6.setTag(r7)
            if (r14 == 0) goto L_0x0169
            android.widget.FrameLayout r14 = r13.updateLayout
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.translationY(r8)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r14 = r14.setInterpolator(r6)
            android.view.ViewPropertyAnimator r14 = r14.setListener(r2)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda22 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda22
            r2.<init>(r0)
            android.view.ViewPropertyAnimator r14 = r14.withEndAction(r2)
            r14.start()
            goto L_0x0179
        L_0x0169:
            android.widget.FrameLayout r14 = r13.updateLayout
            r14.setTranslationY(r8)
            if (r0 == 0) goto L_0x0179
            android.view.ViewParent r14 = r0.getParent()
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            r14.removeView(r0)
        L_0x0179:
            org.telegram.ui.Components.RecyclerListView r14 = r13.sideMenu
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r14.setPadding(r5, r5, r5, r0)
            goto L_0x01d0
        L_0x0183:
            android.widget.FrameLayout r0 = r13.updateLayout
            if (r0 == 0) goto L_0x01d0
            java.lang.Object r0 = r0.getTag()
            if (r0 != 0) goto L_0x018e
            goto L_0x01d0
        L_0x018e:
            android.widget.FrameLayout r0 = r13.updateLayout
            r0.setTag(r2)
            if (r14 == 0) goto L_0x01bb
            android.widget.FrameLayout r14 = r13.updateLayout
            android.view.ViewPropertyAnimator r14 = r14.animate()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            android.view.ViewPropertyAnimator r14 = r14.translationY(r0)
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r14 = r14.setInterpolator(r0)
            org.telegram.ui.LaunchActivity$17 r0 = new org.telegram.ui.LaunchActivity$17
            r0.<init>()
            android.view.ViewPropertyAnimator r14 = r14.setListener(r0)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r3)
            r14.start()
            goto L_0x01cb
        L_0x01bb:
            android.widget.FrameLayout r14 = r13.updateLayout
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            r14.setTranslationY(r0)
            android.widget.FrameLayout r14 = r13.updateLayout
            r0 = 4
            r14.setVisibility(r0)
        L_0x01cb:
            org.telegram.ui.Components.RecyclerListView r14 = r13.sideMenu
            r14.setPadding(r5, r5, r5, r5)
        L_0x01d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.updateAppUpdateViews(boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateAppUpdateViews$74(View view) {
        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new LaunchActivity$$ExternalSyntheticLambda70(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$76(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda52(this, (TLRPC$TL_help_appUpdate) tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$75(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
        TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate2 = SharedConfig.pendingAppUpdate;
        if ((tLRPC$TL_help_appUpdate2 == null || !tLRPC$TL_help_appUpdate2.version.equals(tLRPC$TL_help_appUpdate.version)) && SharedConfig.setNewAppVersionAvailable(tLRPC$TL_help_appUpdate)) {
            if (tLRPC$TL_help_appUpdate.can_not_skip) {
                showUpdateActivity(i, tLRPC$TL_help_appUpdate, false);
            } else {
                this.drawerLayoutAdapter.notifyDataSetChanged();
                try {
                    new UpdateAppAlertDialog(this, tLRPC$TL_help_appUpdate, i).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable, new Object[0]);
        }
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
            this.visibleDialog.setOnDismissListener(new LaunchActivity$$ExternalSyntheticLambda14(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAlertDialog$77(DialogInterface dialogInterface) {
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

    public void setNavigateToPremiumBot(boolean z) {
        this.navigateToPremiumBot = z;
    }

    public void setNavigateToPremiumGiftCallback(Runnable runnable) {
        this.navigateToPremiumGiftCallback = runnable;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        ChatActivity chatActivity;
        boolean z2;
        boolean z3;
        ChatActivity chatActivity2;
        int i;
        boolean z4;
        boolean z5;
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList2;
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList<Long> arrayList3 = arrayList;
        int currentAccount = dialogsActivity2 != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
        Uri uri = this.exportingChatUri;
        if (uri != null) {
            ArrayList arrayList4 = this.documentsUrisArray != null ? new ArrayList(this.documentsUrisArray) : null;
            AlertDialog alertDialog = new AlertDialog(this, 3);
            SendMessagesHelper.getInstance(currentAccount).prepareImportHistory(arrayList3.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new LaunchActivity$$ExternalSyntheticLambda68(this, currentAccount, dialogsActivity, z, arrayList4, uri, alertDialog));
            try {
                alertDialog.showDelayed(300);
            } catch (Exception unused) {
            }
        } else {
            boolean z6 = dialogsActivity2 == null || dialogsActivity2.notify;
            if (arrayList.size() <= 1) {
                long longValue = arrayList3.get(0).longValue();
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (!AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                if (DialogObject.isEncryptedDialog(longValue)) {
                    bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue));
                } else if (DialogObject.isUserDialog(longValue)) {
                    bundle.putLong("user_id", longValue);
                } else {
                    bundle.putLong("chat_id", -longValue);
                }
                if (MessagesController.getInstance(currentAccount).checkCanOpenChat(bundle, dialogsActivity2)) {
                    chatActivity = new ChatActivity(bundle);
                } else {
                    return;
                }
            } else {
                chatActivity = null;
            }
            ArrayList<TLRPC$User> arrayList5 = this.contactsToSend;
            int size = arrayList5 != null ? arrayList5.size() + 0 : 0;
            if (this.videoPath != null) {
                size++;
            }
            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList6 = this.photoPathsArray;
            if (arrayList6 != null) {
                size += arrayList6.size();
            }
            ArrayList<String> arrayList7 = this.documentsPathsArray;
            if (arrayList7 != null) {
                size += arrayList7.size();
            }
            ArrayList<Uri> arrayList8 = this.documentsUrisArray;
            if (arrayList8 != null) {
                size += arrayList8.size();
            }
            if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
                size++;
            }
            int i2 = 0;
            while (i2 < arrayList.size()) {
                if (!AlertsCreator.checkSlowMode(this, this.currentAccount, arrayList3.get(i2).longValue(), size > 1)) {
                    i2++;
                } else {
                    return;
                }
            }
            ArrayList<TLRPC$User> arrayList9 = this.contactsToSend;
            if (arrayList9 == null || arrayList9.size() != 1 || mainFragmentsStack.isEmpty()) {
                String str = null;
                int i3 = 0;
                while (i3 < arrayList.size()) {
                    long longValue2 = arrayList3.get(i3).longValue();
                    AccountInstance instance = AccountInstance.getInstance(UserConfig.selectedAccount);
                    if (chatActivity != null) {
                        i = 1024;
                        chatActivity2 = chatActivity;
                        this.actionBarLayout.presentFragment(chatActivity, dialogsActivity2 != null, dialogsActivity2 == null || this.videoPath != null || ((arrayList2 = this.photoPathsArray) != null && arrayList2.size() > 0), true, false);
                        String str2 = this.videoPath;
                        if (str2 != null) {
                            chatActivity2.openVideoEditor(str2, this.sendingText);
                            this.sendingText = null;
                            z5 = true;
                        } else {
                            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList10 = this.photoPathsArray;
                            if (arrayList10 == null || arrayList10.size() <= 0) {
                                z5 = false;
                            } else {
                                boolean openPhotosEditor = chatActivity2.openPhotosEditor(this.photoPathsArray, (charSequence == null || charSequence.length() == 0) ? this.sendingText : charSequence);
                                if (openPhotosEditor) {
                                    this.sendingText = null;
                                }
                                z4 = openPhotosEditor;
                                z5 = false;
                                z3 = z5;
                                z2 = z4;
                            }
                        }
                        z4 = false;
                        z3 = z5;
                        z2 = z4;
                    } else {
                        chatActivity2 = chatActivity;
                        i = 1024;
                        if (this.videoPath != null) {
                            String str3 = this.sendingText;
                            if (str3 != null && str3.length() <= 1024) {
                                str = this.sendingText;
                                this.sendingText = null;
                            }
                            ArrayList arrayList11 = new ArrayList();
                            arrayList11.add(this.videoPath);
                            SendMessagesHelper.prepareSendingDocuments(instance, arrayList11, arrayList11, (ArrayList<Uri>) null, str, (String) null, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, z6, 0);
                        }
                        z3 = false;
                        z2 = false;
                    }
                    if (this.photoPathsArray != null && !z2) {
                        String str4 = this.sendingText;
                        if (str4 != null && str4.length() <= i && this.photoPathsArray.size() == 1) {
                            this.photoPathsArray.get(0).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingMedia(instance, this.photoPathsArray, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, z6, 0);
                    }
                    if (this.documentsPathsArray != null || this.documentsUrisArray != null) {
                        String str5 = this.sendingText;
                        if (str5 != null && str5.length() <= i) {
                            ArrayList<String> arrayList12 = this.documentsPathsArray;
                            int size2 = arrayList12 != null ? arrayList12.size() : 0;
                            ArrayList<Uri> arrayList13 = this.documentsUrisArray;
                            if (size2 + (arrayList13 != null ? arrayList13.size() : 0) == 1) {
                                str = this.sendingText;
                                this.sendingText = null;
                            }
                        }
                        SendMessagesHelper.prepareSendingDocuments(instance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, str, this.documentsMimeType, longValue2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, z6, 0);
                    }
                    String str6 = this.sendingText;
                    if (str6 != null) {
                        SendMessagesHelper.prepareSendingText(instance, str6, longValue2, true, 0);
                    }
                    ArrayList<TLRPC$User> arrayList14 = this.contactsToSend;
                    if (arrayList14 != null && !arrayList14.isEmpty()) {
                        for (int i4 = 0; i4 < this.contactsToSend.size(); i4++) {
                            SendMessagesHelper.getInstance(currentAccount).sendMessage(this.contactsToSend.get(i4), longValue2, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z6, 0);
                        }
                    }
                    if (!TextUtils.isEmpty(charSequence) && !z3 && !z2) {
                        SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue2, z6, 0);
                    }
                    i3++;
                    chatActivity = chatActivity2;
                }
            } else {
                ArrayList<BaseFragment> arrayList15 = mainFragmentsStack;
                PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList15.get(arrayList15.size() - 1), (ContactsController.Contact) null, (TLRPC$User) null, this.contactsToSendUri, (File) null, (String) null, (String) null);
                phonebookShareAlert.setDelegate(new LaunchActivity$$ExternalSyntheticLambda93(this, chatActivity, arrayList, currentAccount, charSequence, z6));
                ArrayList<BaseFragment> arrayList16 = mainFragmentsStack;
                arrayList16.get(arrayList16.size() - 1).showDialog(phonebookShareAlert);
            }
            ChatActivity chatActivity3 = chatActivity;
            if (dialogsActivity2 != null && chatActivity3 == null) {
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
    public /* synthetic */ void lambda$didSelectDialogs$78(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
        ArrayList arrayList2 = arrayList;
        long j2 = j;
        if (j2 != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j2);
            } else {
                bundle.putLong("chat_id", -j2);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setOpenImport();
            this.actionBarLayout.presentFragment(chatActivity, dialogsActivity != null || z, dialogsActivity == null, true, false);
        } else {
            this.documentsUrisArray = arrayList2;
            if (arrayList2 == null) {
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
    public /* synthetic */ void lambda$didSelectDialogs$79(ChatActivity chatActivity, ArrayList arrayList, int i, CharSequence charSequence, boolean z, TLRPC$User tLRPC$User, boolean z2, int i2) {
        if (chatActivity != null) {
            this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
        }
        AccountInstance instance = AccountInstance.getInstance(UserConfig.selectedAccount);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            long longValue = ((Long) arrayList.get(i3)).longValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$User, longValue, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z2, i2);
            if (!TextUtils.isEmpty(charSequence)) {
                SendMessagesHelper.prepareSendingText(instance, charSequence.toString(), longValue, z, 0);
            }
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
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
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
    public void lambda$runLinkRequest$61(BaseFragment baseFragment) {
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
                if (i2 == -1 && (sharedInstance = VoIPService.getSharedInstance()) != null) {
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
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.onActivityResultReceived, Integer.valueOf(i), Integer.valueOf(i2), intent);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
            if (canDrawOverlays) {
                GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                if (groupCallActivity != null) {
                    groupCallActivity.dismissInternal();
                }
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda28(this), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$80() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (checkPermissionsResult(i, strArr, iArr)) {
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
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.onRequestPermissionResultReceived, Integer.valueOf(i), strArr, iArr);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        isResumed = false;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda21(this.currentAccount));
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onPause$81(int i) {
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
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = true;
        GroupCallPip.updateVisibility(this);
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onPause();
        }
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
    public void onUserLeaveHint() {
        for (Runnable run : this.onUserLeaveHintListeners) {
            run.run();
        }
        this.actionBarLayout.onUserLeaveHint();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MessageObject playingMessageObject;
        super.onResume();
        isResumed = true;
        Runnable runnable = onResumeStaticCallback;
        if (runnable != null) {
            runnable.run();
            onResumeStaticCallback = null;
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
        checkWasMutedByAdmin(true);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(LaunchActivity$$ExternalSyntheticLambda64.INSTANCE);
        checkFreeDiscSpace();
        MediaController.checkGallery();
        onPasscodeResume();
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 == null || passcodeView2.getVisibility() != 0) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onResume$82() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v29, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v38, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v46, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v58, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v63, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v74, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v79, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v82, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v85, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v92, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v84, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v99, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v106, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v115, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v96, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v125, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v138, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v140, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v110, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v166, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v175, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v190, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v192, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v210, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v215, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v3 */
    /* JADX WARNING: type inference failed for: r4v10 */
    /* JADX WARNING: type inference failed for: r4v11, types: [int] */
    /* JADX WARNING: type inference failed for: r4v18 */
    /* JADX WARNING: type inference failed for: r4v20 */
    /* JADX WARNING: type inference failed for: r4v31 */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0288, code lost:
        if (((org.telegram.ui.ProfileActivity) r1.get(r1.size() - 1)).isSettings() == false) goto L_0x028c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05d8  */
    /* JADX WARNING: Removed duplicated region for block: B:417:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0277  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r8 = r21
            r0 = r22
            r1 = r23
            r2 = r24
            int r3 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r0 != r3) goto L_0x0011
            r21.switchToAvailableAccountOrLogout()
            goto L_0x09c7
        L_0x0011:
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r4 = 0
            if (r0 != r3) goto L_0x0022
            r0 = r2[r4]
            if (r0 == r8) goto L_0x09c7
            r21.onFinish()
            r21.finish()
            goto L_0x09c7
        L_0x0022:
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r3) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r23)
            int r0 = r0.getConnectionState()
            int r2 = r8.currentConnectionState
            if (r2 == r0) goto L_0x09c7
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x004a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "switch to state "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x004a:
            r8.currentConnectionState = r0
            r8.updateCurrentConnectionState(r1)
            goto L_0x09c7
        L_0x0051:
            int r3 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r3) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r8.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x09c7
        L_0x005c:
            int r3 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r6 = "Cancel"
            r7 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r9 = "AppName"
            r10 = 5
            r11 = 4
            r12 = 6
            r13 = 3
            java.lang.String r15 = "OK"
            r5 = 2
            r14 = 1
            if (r0 != r3) goto L_0x01a5
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r3 = r0.intValue()
            if (r3 == r12) goto L_0x01a4
            int r3 = r0.intValue()
            if (r3 != r13) goto L_0x0085
            org.telegram.ui.ActionBar.AlertDialog r3 = r8.proxyErrorDialog
            if (r3 == 0) goto L_0x0085
            goto L_0x01a4
        L_0x0085:
            int r3 = r0.intValue()
            if (r3 != r11) goto L_0x0093
            r0 = r2[r14]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r0
            r8.showTosActivity(r1, r0)
            return
        L_0x0093:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r8)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r3.setTitle(r4)
            int r4 = r0.intValue()
            if (r4 == r5) goto L_0x00bc
            int r4 = r0.intValue()
            if (r4 == r13) goto L_0x00bc
            r4 = 2131626802(0x7f0e0b32, float:1.888085E38)
            java.lang.String r7 = "MoreInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4 r7 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4
            r7.<init>(r1)
            r3.setNegativeButton(r4, r7)
        L_0x00bc:
            int r1 = r0.intValue()
            if (r1 != r10) goto L_0x00db
            r0 = 2131626948(0x7f0e0bc4, float:1.8881147E38)
            java.lang.String r1 = "NobodyLikesSpam3"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setMessage(r0)
            r1 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r4 = 0
            r3.setPositiveButton(r0, r4)
            goto L_0x0186
        L_0x00db:
            r1 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            r4 = 0
            int r7 = r0.intValue()
            if (r7 != 0) goto L_0x00fa
            r0 = 2131626946(0x7f0e0bc2, float:1.8881143E38)
            java.lang.String r2 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r3.setPositiveButton(r0, r4)
            goto L_0x0186
        L_0x00fa:
            int r7 = r0.intValue()
            if (r7 != r14) goto L_0x0114
            r0 = 2131626947(0x7f0e0bc3, float:1.8881145E38)
            java.lang.String r2 = "NobodyLikesSpam2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r3.setPositiveButton(r0, r4)
            goto L_0x0186
        L_0x0114:
            int r1 = r0.intValue()
            if (r1 != r5) goto L_0x0156
            r0 = r2[r14]
            java.lang.String r0 = (java.lang.String) r0
            r3.setMessage(r0)
            r0 = r2[r5]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = "AUTH_KEY_DROP_"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x014a
            r0 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            r0 = 2131626558(0x7f0e0a3e, float:1.8880356E38)
            java.lang.String r1 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9
            r1.<init>(r8)
            r3.setNegativeButton(r0, r1)
            goto L_0x0186
        L_0x014a:
            r0 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            r1 = 0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            r3.setPositiveButton(r0, r1)
            goto L_0x0186
        L_0x0156:
            int r0 = r0.intValue()
            if (r0 != r13) goto L_0x0186
            r0 = 2131627824(0x7f0e0var_, float:1.8882923E38)
            java.lang.String r1 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setTitle(r0)
            r0 = 2131628881(0x7f0e1351, float:1.8885067E38)
            java.lang.String r1 = "UseProxyTelegramError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setMessage(r0)
            r0 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.showAlertDialog(r3)
            r8.proxyErrorDialog = r0
            return
        L_0x0186:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09c7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r14
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r3.create()
            r0.showDialog(r1)
            goto L_0x09c7
        L_0x01a4:
            return
        L_0x01a5:
            int r3 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r0 != r3) goto L_0x01ff
            r0 = r2[r4]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>((android.content.Context) r8)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.setTitle(r3)
            r3 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r4 = 0
            r2.setPositiveButton(r3, r4)
            r3 = 2131628368(0x7f0e1150, float:1.8884027E38)
            java.lang.String r4 = "ShareYouLocationUnableManually"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda12 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda12
            r4.<init>(r8, r0, r1)
            r2.setNegativeButton(r3, r4)
            r0 = 2131628367(0x7f0e114f, float:1.8884025E38)
            java.lang.String r1 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09c7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r14
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r2.create()
            r0.showDialog(r1)
            goto L_0x09c7
        L_0x01ff:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r3) goto L_0x0221
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0210
            android.view.View r0 = r0.getChildAt(r4)
            if (r0 == 0) goto L_0x0210
            r0.invalidate()
        L_0x0210:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r8.backgroundTablet
            if (r0 == 0) goto L_0x09c7
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            goto L_0x09c7
        L_0x0221:
            int r3 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r0 != r3) goto L_0x0257
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r1 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x0242
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0242
            android.view.Window r0 = r21.getWindow()     // Catch:{ Exception -> 0x023c }
            r0.setFlags(r1, r1)     // Catch:{ Exception -> 0x023c }
            goto L_0x09c7
        L_0x023c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x09c7
        L_0x0242:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x09c7
            android.view.Window r0 = r21.getWindow()     // Catch:{ Exception -> 0x0251 }
            r0.clearFlags(r1)     // Catch:{ Exception -> 0x0251 }
            goto L_0x09c7
        L_0x0251:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x09c7
        L_0x0257:
            int r3 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r0 != r3) goto L_0x0291
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r14) goto L_0x0274
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r14
            java.lang.Object r0 = r0.get(r1)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0274
            r0 = 1
            goto L_0x0275
        L_0x0274:
            r0 = 0
        L_0x0275:
            if (r0 == 0) goto L_0x028b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r14
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ProfileActivity r1 = (org.telegram.ui.ProfileActivity) r1
            boolean r1 = r1.isSettings()
            if (r1 != 0) goto L_0x028b
            goto L_0x028c
        L_0x028b:
            r4 = r0
        L_0x028c:
            r8.rebuildAllFragments(r4)
            goto L_0x09c7
        L_0x0291:
            int r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r3) goto L_0x029a
            r8.showLanguageAlert(r4)
            goto L_0x09c7
        L_0x029a:
            int r3 = org.telegram.messenger.NotificationCenter.openArticle
            if (r0 != r3) goto L_0x02cc
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02a7
            return
        L_0x02a7:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r14
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.setParentActivity(r8, r1)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r1 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_webPage r1 = (org.telegram.tgnet.TLRPC$TL_webPage) r1
            r2 = r2[r14]
            java.lang.String r2 = (java.lang.String) r2
            r0.open(r1, r2)
            goto L_0x09c7
        L_0x02cc:
            int r3 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r3) goto L_0x0366
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            if (r0 == 0) goto L_0x0365
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02de
            goto L_0x0365
        L_0x02de:
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            r0.intValue()
            r0 = r2[r14]
            java.util.HashMap r0 = (java.util.HashMap) r0
            r3 = r2[r5]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r2 = r2[r13]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r7 = r5.size()
            int r7 = r7 - r14
            java.lang.Object r5 = r5.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r7.<init>((android.content.Context) r8)
            r9 = 2131558507(0x7f0d006b, float:1.8742332E38)
            r10 = 72
            java.lang.String r11 = "dialogTopBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.setTopAnimation(r9, r10, r4, r11)
            r9 = 2131628847(0x7f0e132f, float:1.8884998E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setTitle(r9)
            r9 = 2131628846(0x7f0e132e, float:1.8884996E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setMessage(r9)
            r9 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6
            r10.<init>(r1, r0, r3, r2)
            r7.setPositiveButton(r9, r10)
            r9 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5
            r9.<init>(r1, r0, r3, r2)
            r7.setNegativeButton(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7
            r6.<init>(r1, r0, r3, r2)
            r7.setOnBackButtonListener(r6)
            org.telegram.ui.ActionBar.AlertDialog r0 = r7.create()
            r5.showDialog(r0)
            r0.setCanceledOnTouchOutside(r4)
            goto L_0x09c7
        L_0x0365:
            return
        L_0x0366:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r6 = 21
            if (r0 != r3) goto L_0x03f4
            r0 = r2[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x03b8
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x03a0
            java.lang.String r1 = "chats_menuBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setListSelectorColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x03a0:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x03b8
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x03b7 }
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)     // Catch:{ Exception -> 0x03b7 }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1 = r1 | r3
            r3 = 0
            r0.<init>(r3, r3, r1)     // Catch:{ Exception -> 0x03b7 }
            r8.setTaskDescription(r0)     // Catch:{ Exception -> 0x03b7 }
            goto L_0x03b8
        L_0x03b7:
        L_0x03b8:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBehindKeyboardColor(r1)
            int r0 = r2.length
            if (r0 <= r14) goto L_0x03cf
            r0 = r2[r14]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            goto L_0x03d0
        L_0x03cf:
            r0 = 1
        L_0x03d0:
            int r1 = r2.length
            if (r1 <= r5) goto L_0x03df
            r1 = r2[r5]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x03df
            r1 = 1
            goto L_0x03e0
        L_0x03df:
            r1 = 0
        L_0x03e0:
            if (r0 == 0) goto L_0x03ef
            boolean r0 = r8.isNavigationBarColorFrozen
            if (r0 != 0) goto L_0x03ef
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            boolean r0 = r0.isTransitionAnimationInProgress()
            if (r0 != 0) goto L_0x03ef
            r4 = 1
        L_0x03ef:
            r8.checkSystemBarColors(r1, r14, r4)
            goto L_0x09c7
        L_0x03f4:
            int r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r3) goto L_0x05e4
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x05b6
            r0 = r2[r5]
            if (r0 == 0) goto L_0x05b6
            android.widget.ImageView r0 = r8.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0409
            return
        L_0x0409:
            r0 = r2[r5]     // Catch:{ all -> 0x059d }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x059d }
            r1 = r2[r11]     // Catch:{ all -> 0x059d }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x059d }
            boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x059d }
            r3 = r2[r10]     // Catch:{ all -> 0x059d }
            org.telegram.ui.Components.RLottieImageView r3 = (org.telegram.ui.Components.RLottieImageView) r3     // Catch:{ all -> 0x059d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x059d }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x059d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x059d }
            int r7 = r7.getMeasuredHeight()     // Catch:{ all -> 0x059d }
            if (r1 != 0) goto L_0x042a
            r3.setVisibility(r11)     // Catch:{ all -> 0x059d }
        L_0x042a:
            r9 = 0
            r8.rippleAbove = r9     // Catch:{ all -> 0x059d }
            int r9 = r2.length     // Catch:{ all -> 0x059d }
            if (r9 <= r12) goto L_0x0436
            r9 = r2[r12]     // Catch:{ all -> 0x059d }
            android.view.View r9 = (android.view.View) r9     // Catch:{ all -> 0x059d }
            r8.rippleAbove = r9     // Catch:{ all -> 0x059d }
        L_0x0436:
            r8.isNavigationBarColorFrozen = r14     // Catch:{ all -> 0x059d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x059d }
            r8.invalidateCachedViews(r9)     // Catch:{ all -> 0x059d }
            android.view.View r9 = r8.rippleAbove     // Catch:{ all -> 0x059d }
            if (r9 == 0) goto L_0x0450
            android.graphics.drawable.Drawable r9 = r9.getBackground()     // Catch:{ all -> 0x059d }
            if (r9 == 0) goto L_0x0450
            android.view.View r9 = r8.rippleAbove     // Catch:{ all -> 0x059d }
            android.graphics.drawable.Drawable r9 = r9.getBackground()     // Catch:{ all -> 0x059d }
            r9.setAlpha(r4)     // Catch:{ all -> 0x059d }
        L_0x0450:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x059d }
            android.graphics.Bitmap r9 = org.telegram.messenger.AndroidUtilities.snapshotView(r9)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.rippleAbove     // Catch:{ all -> 0x059d }
            if (r10 == 0) goto L_0x046b
            android.graphics.drawable.Drawable r10 = r10.getBackground()     // Catch:{ all -> 0x059d }
            if (r10 == 0) goto L_0x046b
            android.view.View r10 = r8.rippleAbove     // Catch:{ all -> 0x059d }
            android.graphics.drawable.Drawable r10 = r10.getBackground()     // Catch:{ all -> 0x059d }
            r11 = 255(0xff, float:3.57E-43)
            r10.setAlpha(r11)     // Catch:{ all -> 0x059d }
        L_0x046b:
            android.widget.FrameLayout r10 = r8.frameLayout     // Catch:{ all -> 0x059d }
            android.widget.ImageView r11 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
            r10.removeView(r11)     // Catch:{ all -> 0x059d }
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r1 == 0) goto L_0x048a
            android.widget.FrameLayout r12 = r8.frameLayout     // Catch:{ all -> 0x059d }
            android.widget.ImageView r15 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x059d }
            r12.addView(r15, r4, r10)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x059d }
            r11 = 8
            r10.setVisibility(r11)     // Catch:{ all -> 0x059d }
            goto L_0x04bd
        L_0x048a:
            android.widget.FrameLayout r12 = r8.frameLayout     // Catch:{ all -> 0x059d }
            android.widget.ImageView r15 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x059d }
            r12.addView(r15, r14, r10)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x059d }
            r11 = r0[r4]     // Catch:{ all -> 0x059d }
            r12 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x059d }
            int r11 = r11 - r12
            float r11 = (float) r11     // Catch:{ all -> 0x059d }
            r10.setTranslationX(r11)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x059d }
            r11 = r0[r14]     // Catch:{ all -> 0x059d }
            r12 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x059d }
            int r11 = r11 - r12
            float r11 = (float) r11     // Catch:{ all -> 0x059d }
            r10.setTranslationY(r11)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x059d }
            r10.setVisibility(r4)     // Catch:{ all -> 0x059d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x059d }
            r10.invalidate()     // Catch:{ all -> 0x059d }
        L_0x04bd:
            android.widget.ImageView r10 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
            r10.setImageBitmap(r9)     // Catch:{ all -> 0x059d }
            android.widget.ImageView r9 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
            r9.setVisibility(r4)     // Catch:{ all -> 0x059d }
            org.telegram.ui.Components.RLottieDrawable r9 = r3.getAnimatedDrawable()     // Catch:{ all -> 0x059d }
            r8.themeSwitchSunDrawable = r9     // Catch:{ all -> 0x059d }
            r9 = r0[r4]     // Catch:{ all -> 0x059d }
            int r9 = r6 - r9
            r10 = r0[r4]     // Catch:{ all -> 0x059d }
            int r10 = r6 - r10
            int r9 = r9 * r10
            r10 = r0[r14]     // Catch:{ all -> 0x059d }
            int r10 = r7 - r10
            r11 = r0[r14]     // Catch:{ all -> 0x059d }
            int r11 = r7 - r11
            int r10 = r10 * r11
            int r9 = r9 + r10
            double r9 = (double) r9     // Catch:{ all -> 0x059d }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x059d }
            r11 = r0[r4]     // Catch:{ all -> 0x059d }
            r12 = r0[r4]     // Catch:{ all -> 0x059d }
            int r11 = r11 * r12
            r12 = r0[r14]     // Catch:{ all -> 0x059d }
            int r12 = r7 - r12
            r15 = r0[r14]     // Catch:{ all -> 0x059d }
            int r15 = r7 - r15
            int r12 = r12 * r15
            int r11 = r11 + r12
            double r11 = (double) r11     // Catch:{ all -> 0x059d }
            double r11 = java.lang.Math.sqrt(r11)     // Catch:{ all -> 0x059d }
            double r9 = java.lang.Math.max(r9, r11)     // Catch:{ all -> 0x059d }
            float r9 = (float) r9     // Catch:{ all -> 0x059d }
            r10 = r0[r4]     // Catch:{ all -> 0x059d }
            int r10 = r6 - r10
            r11 = r0[r4]     // Catch:{ all -> 0x059d }
            int r6 = r6 - r11
            int r10 = r10 * r6
            r6 = r0[r14]     // Catch:{ all -> 0x059d }
            r11 = r0[r14]     // Catch:{ all -> 0x059d }
            int r6 = r6 * r11
            int r10 = r10 + r6
            double r10 = (double) r10     // Catch:{ all -> 0x059d }
            double r10 = java.lang.Math.sqrt(r10)     // Catch:{ all -> 0x059d }
            r6 = r0[r4]     // Catch:{ all -> 0x059d }
            r12 = r0[r4]     // Catch:{ all -> 0x059d }
            int r6 = r6 * r12
            r12 = r0[r14]     // Catch:{ all -> 0x059d }
            r15 = r0[r14]     // Catch:{ all -> 0x059d }
            int r12 = r12 * r15
            int r6 = r6 + r12
            double r5 = (double) r6     // Catch:{ all -> 0x059d }
            double r5 = java.lang.Math.sqrt(r5)     // Catch:{ all -> 0x059d }
            double r5 = java.lang.Math.max(r10, r5)     // Catch:{ all -> 0x059d }
            float r5 = (float) r5     // Catch:{ all -> 0x059d }
            float r5 = java.lang.Math.max(r9, r5)     // Catch:{ all -> 0x059d }
            if (r1 == 0) goto L_0x0537
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x059d }
            goto L_0x0539
        L_0x0537:
            android.widget.ImageView r6 = r8.themeSwitchImageView     // Catch:{ all -> 0x059d }
        L_0x0539:
            r9 = r0[r4]     // Catch:{ all -> 0x059d }
            r10 = r0[r14]     // Catch:{ all -> 0x059d }
            r11 = 0
            if (r1 == 0) goto L_0x0542
            r12 = 0
            goto L_0x0543
        L_0x0542:
            r12 = r5
        L_0x0543:
            if (r1 == 0) goto L_0x0546
            goto L_0x0547
        L_0x0546:
            r5 = 0
        L_0x0547:
            android.animation.Animator r5 = android.view.ViewAnimationUtils.createCircularReveal(r6, r9, r10, r12, r5)     // Catch:{ all -> 0x059d }
            r9 = 400(0x190, double:1.976E-321)
            r5.setDuration(r9)     // Catch:{ all -> 0x059d }
            android.view.animation.Interpolator r6 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x059d }
            r5.setInterpolator(r6)     // Catch:{ all -> 0x059d }
            org.telegram.ui.LaunchActivity$18 r6 = new org.telegram.ui.LaunchActivity$18     // Catch:{ all -> 0x059d }
            r6.<init>(r1, r3)     // Catch:{ all -> 0x059d }
            r5.addListener(r6)     // Catch:{ all -> 0x059d }
            android.view.View r3 = r8.rippleAbove     // Catch:{ all -> 0x059d }
            if (r3 == 0) goto L_0x0580
            r3 = 2
            float[] r3 = new float[r3]     // Catch:{ all -> 0x059d }
            r3[r4] = r11     // Catch:{ all -> 0x059d }
            r6 = 1065353216(0x3var_, float:1.0)
            r3[r14] = r6     // Catch:{ all -> 0x059d }
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofFloat(r3)     // Catch:{ all -> 0x059d }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0     // Catch:{ all -> 0x059d }
            r6.<init>(r8)     // Catch:{ all -> 0x059d }
            r3.addUpdateListener(r6)     // Catch:{ all -> 0x059d }
            long r9 = r5.getDuration()     // Catch:{ all -> 0x059d }
            r3.setDuration(r9)     // Catch:{ all -> 0x059d }
            r3.start()     // Catch:{ all -> 0x059d }
        L_0x0580:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26     // Catch:{ all -> 0x059d }
            r3.<init>(r8)     // Catch:{ all -> 0x059d }
            if (r1 == 0) goto L_0x0593
            r0 = r0[r14]     // Catch:{ all -> 0x059d }
            int r7 = r7 - r0
            r0 = 1074790400(0x40100000, float:2.25)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x059d }
            int r7 = r7 / r0
            long r0 = (long) r7     // Catch:{ all -> 0x059d }
            goto L_0x0595
        L_0x0593:
            r0 = 50
        L_0x0595:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r0)     // Catch:{ all -> 0x059d }
            r5.start()     // Catch:{ all -> 0x059d }
            r0 = 1
            goto L_0x05b9
        L_0x059d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x05b1 }
            r1 = 0
            r0.setImageDrawable(r1)     // Catch:{ Exception -> 0x05b1 }
            android.widget.FrameLayout r0 = r8.frameLayout     // Catch:{ Exception -> 0x05b1 }
            android.widget.ImageView r1 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x05b1 }
            r0.removeView(r1)     // Catch:{ Exception -> 0x05b1 }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4     // Catch:{ Exception -> 0x05b1 }
            goto L_0x05b8
        L_0x05b1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x05b8
        L_0x05b6:
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4
        L_0x05b8:
            r0 = 0
        L_0x05b9:
            r1 = r2[r4]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r1
            r3 = r2[r14]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r2 = r2[r13]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.actionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x09c7
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.layersActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.rightActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            goto L_0x09c7
        L_0x05e4:
            int r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r3) goto L_0x0615
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x09c7
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r0 = r0.getChildCount()
        L_0x05f4:
            if (r4 >= r0) goto L_0x09c7
            org.telegram.ui.Components.RecyclerListView r2 = r8.sideMenu
            android.view.View r2 = r2.getChildAt(r4)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r3 == 0) goto L_0x0612
            r3 = r2
            org.telegram.ui.Cells.DrawerUserCell r3 = (org.telegram.ui.Cells.DrawerUserCell) r3
            int r3 = r3.getAccountNumber()
            int r5 = r1.intValue()
            if (r3 != r5) goto L_0x0612
            r2.invalidate()
            goto L_0x09c7
        L_0x0612:
            int r4 = r4 + 1
            goto L_0x05f4
        L_0x0615:
            int r3 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r3) goto L_0x0624
            r0 = r2[r4]     // Catch:{ all -> 0x09c7 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x09c7 }
            r1 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r8, r1)     // Catch:{ all -> 0x09c7 }
            goto L_0x09c7
        L_0x0624:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoaded
            if (r0 != r3) goto L_0x06fe
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x0643
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0643
            r8.updateAppUpdateViews(r14)
        L_0x0643:
            java.lang.String r1 = r8.loadingThemeFileName
            if (r1 == 0) goto L_0x06cd
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x09c7
            r1 = 0
            r8.loadingThemeFileName = r1
            java.io.File r0 = new java.io.File
            java.io.File r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "remote"
            r2.append(r3)
            org.telegram.tgnet.TLRPC$TL_theme r3 = r8.loadingTheme
            long r3 = r3.id
            r2.append(r3)
            java.lang.String r3 = ".attheme"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            org.telegram.tgnet.TLRPC$TL_theme r1 = r8.loadingTheme
            java.lang.String r2 = r1.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r0, r2, r1)
            if (r1 == 0) goto L_0x06c8
            java.lang.String r2 = r1.pathToWallpaper
            if (r2 == 0) goto L_0x06ad
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r1.pathToWallpaper
            r2.<init>(r3)
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x06ad
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r1.account
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86
            r3.<init>(r8, r1)
            r2.sendRequest(r0, r3)
            return
        L_0x06ad:
            org.telegram.tgnet.TLRPC$TL_theme r1 = r8.loadingTheme
            java.lang.String r2 = r1.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r16 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r2, r1, r14)
            if (r16 == 0) goto L_0x06c8
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r17 = 1
            r18 = 0
            r19 = 0
            r20 = 0
            r15 = r0
            r15.<init>(r16, r17, r18, r19, r20)
            r8.lambda$runLinkRequest$61(r0)
        L_0x06c8:
            r21.onThemeLoadFinish()
            goto L_0x09c7
        L_0x06cd:
            java.lang.String r1 = r8.loadingThemeWallpaperName
            if (r1 == 0) goto L_0x09c7
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x09c7
            r1 = 0
            r8.loadingThemeWallpaperName = r1
            r0 = r2[r14]
            java.io.File r0 = (java.io.File) r0
            boolean r1 = r8.loadingThemeAccent
            if (r1 == 0) goto L_0x06f0
            org.telegram.tgnet.TLRPC$TL_theme r0 = r8.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r8.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r8.loadingThemeInfo
            r8.openThemeAccentPreview(r0, r1, r2)
            r21.onThemeLoadFinish()
            goto L_0x09c7
        L_0x06f0:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r8.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62
            r3.<init>(r8, r1, r0)
            r2.postRunnable(r3)
            goto L_0x09c7
        L_0x06fe:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoadFailed
            if (r0 != r3) goto L_0x0732
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r8.loadingThemeFileName
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x0716
            java.lang.String r1 = r8.loadingThemeWallpaperName
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0719
        L_0x0716:
            r21.onThemeLoadFinish()
        L_0x0719:
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x09c7
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x09c7
            r8.updateAppUpdateViews(r14)
            goto L_0x09c7
        L_0x0732:
            int r3 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r3) goto L_0x0749
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x073b
            return
        L_0x073b:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x0744
            r21.onPasscodeResume()
            goto L_0x09c7
        L_0x0744:
            r21.onPasscodePause()
            goto L_0x09c7
        L_0x0749:
            int r3 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r3) goto L_0x0760
            int r0 = r2.length
            if (r0 <= 0) goto L_0x075b
            r0 = r2[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x075b
            r4 = 1
        L_0x075b:
            r8.checkSystemBarColors(r4)
            goto L_0x09c7
        L_0x0760:
            int r3 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r0 != r3) goto L_0x078e
            int r0 = r2.length
            if (r0 <= r14) goto L_0x09c7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09c7
            int r0 = r8.currentAccount
            r1 = 2
            r1 = r2[r1]
            org.telegram.tgnet.TLRPC$TL_error r1 = (org.telegram.tgnet.TLRPC$TL_error) r1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r5 = r3.size()
            int r5 = r5 - r14
            java.lang.Object r3 = r3.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r2 = r2[r14]
            org.telegram.tgnet.TLObject r2 = (org.telegram.tgnet.TLObject) r2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.ui.Components.AlertsCreator.processError(r0, r1, r3, r2, r4)
            goto L_0x09c7
        L_0x078e:
            int r3 = org.telegram.messenger.NotificationCenter.stickersImportComplete
            if (r0 != r3) goto L_0x07bd
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r23)
            r0 = r2[r4]
            r3 = r0
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r4 = 2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07b3
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r14
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r5 = r0
            goto L_0x07b4
        L_0x07b3:
            r5 = 0
        L_0x07b4:
            r6 = 0
            r7 = 1
            r2 = r21
            r1.toggleStickerSet(r2, r3, r4, r5, r6, r7)
            goto L_0x09c7
        L_0x07bd:
            int r1 = org.telegram.messenger.NotificationCenter.newSuggestionsAvailable
            if (r0 != r1) goto L_0x07c8
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            r0.invalidateViews()
            goto L_0x09c7
        L_0x07c8:
            int r1 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r0 != r1) goto L_0x0921
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09c7
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            boolean r1 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r1 == 0) goto L_0x07ea
            org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r1 == 0) goto L_0x07ea
            android.widget.FrameLayout r1 = r1.getContainer()
            r7 = r1
            goto L_0x07eb
        L_0x07ea:
            r7 = 0
        L_0x07eb:
            if (r7 != 0) goto L_0x07fc
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r14
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r9 = r1
            goto L_0x07fd
        L_0x07fc:
            r9 = 0
        L_0x07fd:
            r6 = 1500(0x5dc, float:2.102E-42)
            if (r0 == 0) goto L_0x08e6
            if (r0 == r14) goto L_0x08c1
            r3 = 0
            r1 = 2
            if (r0 == r1) goto L_0x0891
            if (r0 == r13) goto L_0x0861
            if (r0 == r11) goto L_0x082e
            if (r0 == r10) goto L_0x0810
            goto L_0x09c7
        L_0x0810:
            r0 = r2[r14]
            org.telegram.ui.LauncherIconController$LauncherIcon r0 = (org.telegram.ui.LauncherIconController.LauncherIcon) r0
            org.telegram.ui.Components.AppIconBulletinLayout r1 = new org.telegram.ui.Components.AppIconBulletinLayout
            r2 = 0
            r1.<init>(r8, r0, r2)
            if (r9 == 0) goto L_0x0825
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r6)
            r0.show()
            goto L_0x09c7
        L_0x0825:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r6)
            r0.show()
            goto L_0x09c7
        L_0x082e:
            if (r9 == 0) goto L_0x084a
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r9)
            r1 = r2[r14]
            java.lang.String r1 = (java.lang.String) r1
            r3 = 2
            r2 = r2[r3]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r9.getResourceProvider()
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletinSubtitle(r1, r2, r3)
            r0.show()
            goto L_0x09c7
        L_0x084a:
            r1 = 0
            r3 = 2
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r4 = r2[r14]
            java.lang.String r4 = (java.lang.String) r4
            r2 = r2[r3]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletinSubtitle(r4, r2, r1)
            r0.show()
            goto L_0x09c7
        L_0x0861:
            r0 = r2[r14]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0873
            r0 = 2131629380(0x7f0e1544, float:1.888608E38)
            java.lang.String r1 = "YourNameChanged"
            goto L_0x0878
        L_0x0873:
            r0 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r1 = "CannelTitleChanged"
        L_0x0878:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x0884
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x0888
        L_0x0884:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x0888:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x09c7
        L_0x0891:
            r0 = r2[r14]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x08a3
            r0 = 2131629363(0x7f0e1533, float:1.8886045E38)
            java.lang.String r1 = "YourBioChanged"
            goto L_0x08a8
        L_0x08a3:
            r0 = 2131624920(0x7f0e03d8, float:1.8877033E38)
            java.lang.String r1 = "CannelDescriptionChanged"
        L_0x08a8:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x08b4
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x08b8
        L_0x08b4:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x08b8:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x09c7
        L_0x08c1:
            if (r9 == 0) goto L_0x08d4
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r9)
            r1 = r2[r14]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x09c7
        L_0x08d4:
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r1 = r2[r14]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x09c7
        L_0x08e6:
            r0 = r2[r14]
            r5 = r0
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            r0 = 2
            r0 = r2[r0]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            org.telegram.ui.Components.StickerSetBulletinLayout r10 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = 0
            r11 = 0
            r1 = r10
            r2 = r21
            r4 = r0
            r13 = 1500(0x5dc, float:2.102E-42)
            r6 = r11
            r1.<init>(r2, r3, r4, r5, r6)
            if (r0 == r12) goto L_0x090b
            r1 = 7
            if (r0 != r1) goto L_0x0908
            goto L_0x090b
        L_0x0908:
            r6 = 1500(0x5dc, float:2.102E-42)
            goto L_0x090d
        L_0x090b:
            r6 = 3500(0xdac, float:4.905E-42)
        L_0x090d:
            if (r9 == 0) goto L_0x0918
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r10, (int) r6)
            r0.show()
            goto L_0x09c7
        L_0x0918:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r10, (int) r6)
            r0.show()
            goto L_0x09c7
        L_0x0921:
            int r1 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r0 != r1) goto L_0x092a
            r8.checkWasMutedByAdmin(r4)
            goto L_0x09c7
        L_0x092a:
            int r1 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r0 != r1) goto L_0x0981
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.updateTextView
            if (r0 == 0) goto L_0x09c7
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x09c7
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            if (r1 == 0) goto L_0x09c7
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x09c7
            r0 = r2[r14]
            java.lang.Long r0 = (java.lang.Long) r0
            r1 = 2
            r1 = r2[r1]
            java.lang.Long r1 = (java.lang.Long) r1
            long r2 = r0.longValue()
            float r0 = (float) r2
            long r1 = r1.longValue()
            float r1 = (float) r1
            float r0 = r0 / r1
            org.telegram.ui.Components.RadialProgress2 r1 = r8.updateLayoutIcon
            r1.setProgress(r0, r14)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r8.updateTextView
            r2 = 2131624395(0x7f0e01cb, float:1.8875969E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r5
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3[r4] = r0
            java.lang.String r0 = "AppUpdateDownloading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            r1.setText(r0)
            goto L_0x09c7
        L_0x0981:
            int r1 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r0 != r1) goto L_0x0992
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 != r14) goto L_0x098e
            r4 = 1
        L_0x098e:
            r8.updateAppUpdateViews(r4)
            goto L_0x09c7
        L_0x0992:
            int r1 = org.telegram.messenger.NotificationCenter.currentUserShowLimitReachedDialog
            if (r0 != r1) goto L_0x09c7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09c7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r14
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            android.app.Activity r1 = r0.getParentActivity()
            if (r1 == 0) goto L_0x09c7
            org.telegram.ui.Components.Premium.LimitReachedBottomSheet r1 = new org.telegram.ui.Components.Premium.LimitReachedBottomSheet
            android.app.Activity r3 = r0.getParentActivity()
            r2 = r2[r4]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r4 = r8.currentAccount
            r1.<init>(r0, r3, r2, r4)
            r0.showDialog(r1)
        L_0x09c7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$83(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$84(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$86(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda103(hashMap, i));
                lambda$runLinkRequest$61(locationActivity);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$85(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$90(ValueAnimator valueAnimator) {
        this.frameLayout.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$91() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$93(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda48(this, tLObject, themeInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$92(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    public /* synthetic */ void lambda$didReceivedNotification$95(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda31(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$94() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$61(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        long j;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance == null || (call = sharedInstance.groupCall) == null) {
            this.wasMutedByAdminRaisedHand = false;
            return;
        }
        boolean z3 = this.wasMutedByAdminRaisedHand;
        TLRPC$InputPeer groupCallPeer = sharedInstance.getGroupCallPeer();
        if (groupCallPeer != null) {
            j = groupCallPeer.user_id;
            if (j == 0) {
                long j2 = groupCallPeer.chat_id;
                if (j2 == 0) {
                    j2 = groupCallPeer.channel_id;
                }
                j = -j2;
            }
        } else {
            j = UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(j);
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
                if (chatActivity.getDialogId() == (-chat.id)) {
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
        lambda$runLinkRequest$61(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
        SharedConfig.checkLogsToDelete();
        if (Build.VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda29(this), 2000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$97() {
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
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda34(this));
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$96() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable unused) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0054 A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0056 A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x005c A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x005f A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0064 A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0065 A[Catch:{ Exception -> 0x0122 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x006e A[SYNTHETIC, Splitter:B:25:0x006e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r17, org.telegram.messenger.LocaleController.LocaleInfo r18, java.lang.String r19) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r0 = "ChooseYourLanguageOther"
            java.lang.String r2 = "ChooseYourLanguage"
            r3 = 0
            r1.loadingLocaleDialog = r3     // Catch:{ Exception -> 0x0122 }
            r4 = r17
            boolean r5 = r4.builtIn     // Catch:{ Exception -> 0x0122 }
            r6 = 1
            if (r5 != 0) goto L_0x001d
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0122 }
            boolean r5 = r5.isCurrentLocalLocale()     // Catch:{ Exception -> 0x0122 }
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r5 = 0
            goto L_0x001e
        L_0x001d:
            r5 = 1
        L_0x001e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x0122 }
            r7.<init>((android.content.Context) r1)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0122 }
            r9 = 2131625146(0x7f0e04ba, float:1.8877492E38)
            java.lang.String r8 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0122 }
            r7.setTitle(r8)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0122 }
            java.lang.String r2 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0122 }
            r7.setSubtitle(r2)     // Catch:{ Exception -> 0x0122 }
            android.widget.LinearLayout r2 = new android.widget.LinearLayout     // Catch:{ Exception -> 0x0122 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            r2.setOrientation(r6)     // Catch:{ Exception -> 0x0122 }
            r8 = 2
            org.telegram.ui.Cells.LanguageCell[] r9 = new org.telegram.ui.Cells.LanguageCell[r8]     // Catch:{ Exception -> 0x0122 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r10 = new org.telegram.messenger.LocaleController.LocaleInfo[r6]     // Catch:{ Exception -> 0x0122 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r11 = new org.telegram.messenger.LocaleController.LocaleInfo[r8]     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0122 }
            java.lang.String r13 = "English"
            r14 = 2131625692(0x7f0e06dc, float:1.88786E38)
            java.lang.String r12 = r1.getStringForLanguageAlert(r12, r13, r14)     // Catch:{ Exception -> 0x0122 }
            if (r5 == 0) goto L_0x0056
            r13 = r4
            goto L_0x0058
        L_0x0056:
            r13 = r18
        L_0x0058:
            r11[r3] = r13     // Catch:{ Exception -> 0x0122 }
            if (r5 == 0) goto L_0x005f
            r13 = r18
            goto L_0x0060
        L_0x005f:
            r13 = r4
        L_0x0060:
            r11[r6] = r13     // Catch:{ Exception -> 0x0122 }
            if (r5 == 0) goto L_0x0065
            goto L_0x0067
        L_0x0065:
            r4 = r18
        L_0x0067:
            r10[r3] = r4     // Catch:{ Exception -> 0x0122 }
            r4 = 0
        L_0x006a:
            java.lang.String r14 = "dialogButtonSelector"
            if (r4 >= r8) goto L_0x00c0
            org.telegram.ui.Cells.LanguageCell r15 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0122 }
            r15.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            r9[r4] = r15     // Catch:{ Exception -> 0x0122 }
            r15 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            r5 = r11[r4]     // Catch:{ Exception -> 0x0122 }
            r13 = r11[r4]     // Catch:{ Exception -> 0x0122 }
            r3 = r18
            if (r13 != r3) goto L_0x0081
            r13 = r12
            goto L_0x0082
        L_0x0081:
            r13 = 0
        L_0x0082:
            r15.setLanguage(r5, r13, r6)     // Catch:{ Exception -> 0x0122 }
            r5 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0122 }
            r5.setTag(r13)     // Catch:{ Exception -> 0x0122 }
            r5 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0122 }
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r8)     // Catch:{ Exception -> 0x0122 }
            r5.setBackground(r13)     // Catch:{ Exception -> 0x0122 }
            r5 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            if (r4 != 0) goto L_0x00a1
            r13 = 1
            goto L_0x00a2
        L_0x00a1:
            r13 = 0
        L_0x00a2:
            r14 = 0
            r5.setLanguageSelected(r13, r14)     // Catch:{ Exception -> 0x0122 }
            r5 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            r13 = 50
            r15 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r13)     // Catch:{ Exception -> 0x0122 }
            r2.addView(r5, r13)     // Catch:{ Exception -> 0x0122 }
            r5 = r9[r4]     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17     // Catch:{ Exception -> 0x0122 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0122 }
            r5.setOnClickListener(r13)     // Catch:{ Exception -> 0x0122 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00c0:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0122 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0122 }
            r5 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0122 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0122 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0122 }
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0122 }
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r0, r8)     // Catch:{ Exception -> 0x0122 }
            r3.setBackground(r0)     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15     // Catch:{ Exception -> 0x0122 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0122 }
            r0 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r0)     // Catch:{ Exception -> 0x0122 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0122 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0122 }
            java.lang.String r0 = "OK"
            r2 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13     // Catch:{ Exception -> 0x0122 }
            r2.<init>(r1, r10)     // Catch:{ Exception -> 0x0122 }
            r7.setNegativeButton(r0, r2)     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r7)     // Catch:{ Exception -> 0x0122 }
            r1.localeDialog = r0     // Catch:{ Exception -> 0x0122 }
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0122 }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x0122 }
            java.lang.String r2 = "language_showed2"
            r3 = r19
            android.content.SharedPreferences$Editor r0 = r0.putString(r2, r3)     // Catch:{ Exception -> 0x0122 }
            r0.commit()     // Catch:{ Exception -> 0x0122 }
            goto L_0x0126
        L_0x0122:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0126:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showLanguageAlertInternal$98(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$99(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$61(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$100(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    /* access modifiers changed from: private */
    public void drawRippleAbove(Canvas canvas, View view) {
        View view2;
        if (view != null && (view2 = this.rippleAbove) != null && view2.getBackground() != null) {
            if (this.tempLocation == null) {
                this.tempLocation = new int[2];
            }
            this.rippleAbove.getLocationInWindow(this.tempLocation);
            int[] iArr = this.tempLocation;
            int i = iArr[0];
            int i2 = iArr[1];
            view.getLocationInWindow(iArr);
            int[] iArr2 = this.tempLocation;
            int i3 = i2 - iArr2[1];
            canvas.save();
            canvas.translate((float) (i - iArr2[0]), (float) i3);
            this.rippleAbove.getBackground().draw(canvas);
            canvas.restore();
        }
    }

    private void showLanguageAlert(boolean z) {
        String str;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
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
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new LaunchActivity$$ExternalSyntheticLambda89(this, localeInfoArr, str2), 8);
                                TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                                tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                                tLRPC$TL_langpack_getStrings2.keys.add("English");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new LaunchActivity$$ExternalSyntheticLambda90(this, localeInfoArr, str2), 8);
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
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$102(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$101(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$104(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda40(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$103(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass19 r0 = new Runnable() {
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity(true, false, -1, -1, (Runnable) null, (Runnable) null);
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
            showPasscodeActivity(true, false, -1, -1, (Runnable) null, (Runnable) null);
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
            LaunchActivity$$ExternalSyntheticLambda27 launchActivity$$ExternalSyntheticLambda27 = null;
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
                launchActivity$$ExternalSyntheticLambda27 = new LaunchActivity$$ExternalSyntheticLambda27(this);
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, launchActivity$$ExternalSyntheticLambda27);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$105() {
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
            r2.lambda$runLinkRequest$61(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$105():void");
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
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 == null || passcodeView2.getVisibility() != 0) {
            if (ContentPreviewViewer.hasInstance() && ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().closeWithMenu();
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
        } else {
            finish();
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
                if (Build.VERSION.SDK_INT >= 32) {
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
        try {
            super.dispatchKeyEvent(keyEvent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return false;
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
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        ActionBarLayout actionBarLayout6;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            boolean z3 = baseFragment instanceof LoginActivity;
            this.drawerLayoutContainer.setAllowOpenDrawer(!z3 && !(baseFragment instanceof IntroActivity) && !(baseFragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
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
                    if (z3) {
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
                boolean z4 = this.tabletFullSize;
                if ((!z4 && actionBarLayout2 == this.rightActionBarLayout) || (z4 && actionBarLayout2 == this.actionBarLayout)) {
                    boolean z5 = (z4 && actionBarLayout2 == (actionBarLayout3 = this.actionBarLayout) && actionBarLayout3.fragmentsStack.size() == 1) ? false : true;
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
                } else if (!z4 && actionBarLayout2 != (actionBarLayout5 = this.rightActionBarLayout)) {
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
                } else if (!z4 || actionBarLayout2 == (actionBarLayout4 = this.actionBarLayout)) {
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
            this.drawerLayoutContainer.setAllowOpenDrawer((baseFragment instanceof LoginActivity) || (baseFragment instanceof IntroActivity) ? !(mainFragmentsStack.size() == 0 || (mainFragmentsStack.get(0) instanceof IntroActivity)) : !((baseFragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1), false);
        }
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout2) {
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        if (AndroidUtilities.isTablet()) {
            boolean z = baseFragment instanceof LoginActivity;
            this.drawerLayoutContainer.setAllowOpenDrawer(!z && !(baseFragment instanceof IntroActivity) && !(baseFragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
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
                boolean z2 = this.tabletFullSize;
                if (!z2 && actionBarLayout2 != (actionBarLayout4 = this.rightActionBarLayout)) {
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
                } else if (z2 && actionBarLayout2 != (actionBarLayout3 = this.actionBarLayout)) {
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
            this.drawerLayoutContainer.setAllowOpenDrawer((baseFragment instanceof LoginActivity) || (baseFragment instanceof IntroActivity) ? !(mainFragmentsStack.size() == 0 || (mainFragmentsStack.get(0) instanceof IntroActivity)) : !((baseFragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1), false);
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
