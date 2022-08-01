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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda94 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda94
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda95 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda95
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
                    lambda$runLinkRequest$60(new LoginActivity(num.intValue()));
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
                    lambda$runLinkRequest$60(new GroupCreateActivity(new Bundle()));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("createSecretChat", true);
                    bundle.putBoolean("allowBots", false);
                    bundle.putBoolean("allowSelf", false);
                    lambda$runLinkRequest$60(new ContactsActivity(bundle));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 4) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                        lambda$runLinkRequest$60(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                    } else {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("step", 0);
                        lambda$runLinkRequest$60(new ChannelCreateActivity(bundle2));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 6) {
                    lambda$runLinkRequest$60(new ContactsActivity((Bundle) null));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 7) {
                    lambda$runLinkRequest$60(new InviteContactsActivity());
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 8) {
                    openSettings(false);
                } else if (id == 9) {
                    Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 10) {
                    lambda$runLinkRequest$60(new CallLogActivity());
                    this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 11) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                    lambda$runLinkRequest$60(new ChatActivity(bundle3));
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
                            lambda$runLinkRequest$60(new PeopleNearbyActivity());
                        } else {
                            lambda$runLinkRequest$60(new ActionIntroActivity(4));
                        }
                        this.drawerLayoutContainer.closeDrawer(false);
                        return;
                    }
                    lambda$runLinkRequest$60(new ActionIntroActivity(1));
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
        lambda$runLinkRequest$60(new ProfileActivity(bundle));
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
        switchToAccount(i, z, LaunchActivity$$ExternalSyntheticLambda65.INSTANCE);
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
        lambda$runLinkRequest$60(new IntroActivity().setOnLogout());
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
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda93(this));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r76v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v83, resolved type: org.telegram.ui.EditWidgetActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r76v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r76v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r76v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v8, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v1, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v10, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v11, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v2, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v12, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v4, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v13, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v14, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v15, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v16, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v17, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v145, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v7, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v19, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v8, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v20, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v21, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v22, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v23, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v24, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v25, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v26, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v254, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v273, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v280, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v29, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v296, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v32, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v300, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v33, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v13, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v34, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v35, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v36, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v37, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v38, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v40, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v324, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v329, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v340, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v42, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v344, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v43, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v348, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v44, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v16, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v46, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v38, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v269, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v296, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v353, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v355, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v358, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v360, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v361, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v362, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v364, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v366, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v368, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v369, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v370, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v372, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v376, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v378, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v380, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v387, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v390, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v405, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v411, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v423, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v49, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v50, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v53, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v55, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v17, resolved type: java.lang.String} */
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
    /* JADX WARNING: type inference failed for: r25v31 */
    /* JADX WARNING: type inference failed for: r55v12 */
    /* JADX WARNING: type inference failed for: r25v39 */
    /* JADX WARNING: type inference failed for: r55v14 */
    /* JADX WARNING: type inference failed for: r25v41 */
    /* JADX WARNING: type inference failed for: r55v15 */
    /* JADX WARNING: type inference failed for: r8v44 */
    /* JADX WARNING: type inference failed for: r8v46 */
    /* JADX WARNING: type inference failed for: r55v17 */
    /* JADX WARNING: type inference failed for: r25v66 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x1var_, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r3)) != false) goto L_0x1f3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1030:0x1fb6, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r3)) != false) goto L_0x1fb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0326, code lost:
        if (r15.sendingText == null) goto L_0x019a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0a06, code lost:
        if (r1.intValue() == 0) goto L_0x0a08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x011e, code lost:
        r0 = r23.getIntent().getExtras();
        r12 = r0.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        r0 = r0.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0131, code lost:
        r19 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0134, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0135, code lost:
        r19 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0153, code lost:
        if (r1.equals(r0) != false) goto L_0x0157;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x1958, code lost:
        if (r2.longValue() == 0) goto L_0x195d;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:921:0x1bab */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1005:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1108:0x211f  */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x2131  */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x213f  */
    /* JADX WARNING: Removed duplicated region for block: B:1113:0x2150  */
    /* JADX WARNING: Removed duplicated region for block: B:1182:0x23a6  */
    /* JADX WARNING: Removed duplicated region for block: B:1193:0x23f1  */
    /* JADX WARNING: Removed duplicated region for block: B:1204:0x243c  */
    /* JADX WARNING: Removed duplicated region for block: B:1206:0x2448  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x03e5 A[Catch:{ Exception -> 0x050b }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0512  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x06dc  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x07ce A[Catch:{ Exception -> 0x07dc }] */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x07db  */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0d7b  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:698:0x1421 A[SYNTHETIC, Splitter:B:698:0x1421] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0165 A[SYNTHETIC, Splitter:B:70:0x0165] */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x17e8  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:827:0x18c9 A[Catch:{ Exception -> 0x18d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x18d6  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x19c4  */
    /* JADX WARNING: Removed duplicated region for block: B:926:0x1bb3 A[SYNTHETIC, Splitter:B:926:0x1bb3] */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1ed5  */
    @android.annotation.SuppressLint({"Range"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r82, boolean r83, boolean r84, boolean r85) {
        /*
            r81 = this;
            r15 = r81
            r14 = r82
            r0 = r84
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r81, r82)
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
            java.lang.String r1 = r82.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r8 = r82.getFlags()
            java.lang.String r9 = r82.getAction()
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
            if (r85 != 0) goto L_0x009c
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
            r1 = r81
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            r1.saveConfig(r12)
            if (r33 != 0) goto L_0x009c
            r15.passcodeSaveIntent = r14
            r10 = r83
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x009c:
            r10 = r83
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
            java.lang.String r8 = "message_id"
            r5 = 0
            if (r1 != 0) goto L_0x1e95
            java.lang.String r1 = r82.getAction()
            if (r1 == 0) goto L_0x1e95
            if (r0 != 0) goto L_0x1e95
            java.lang.String r0 = r82.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "hash"
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.String r4 = "\n"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x0368
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r82.getExtras()
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r82.getExtras()
            java.lang.String r9 = "dialogId"
            long r19 = r0.getLong(r9, r5)
            int r0 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0142
            android.os.Bundle r0 = r82.getExtras()     // Catch:{ all -> 0x013c }
            java.lang.String r9 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r9)     // Catch:{ all -> 0x013c }
            if (r0 == 0) goto L_0x0140
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x013c }
            java.util.List r9 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r9)     // Catch:{ all -> 0x013c }
            int r13 = r9.size()     // Catch:{ all -> 0x013c }
        L_0x010c:
            if (r12 >= r13) goto L_0x0140
            java.lang.Object r23 = r9.get(r12)     // Catch:{ all -> 0x013c }
            androidx.core.content.pm.ShortcutInfoCompat r23 = (androidx.core.content.pm.ShortcutInfoCompat) r23     // Catch:{ all -> 0x013c }
            java.lang.String r7 = r23.getId()     // Catch:{ all -> 0x013c }
            boolean r7 = r0.equals(r7)     // Catch:{ all -> 0x013c }
            if (r7 == 0) goto L_0x0138
            android.content.Intent r0 = r23.getIntent()     // Catch:{ all -> 0x013c }
            android.os.Bundle r0 = r0.getExtras()     // Catch:{ all -> 0x013c }
            java.lang.String r7 = "dialogId"
            long r12 = r0.getLong(r7, r5)     // Catch:{ all -> 0x013c }
            r7 = 0
            java.lang.String r0 = r0.getString(r1, r7)     // Catch:{ all -> 0x0134 }
            r19 = r12
            goto L_0x014b
        L_0x0134:
            r0 = move-exception
            r19 = r12
            goto L_0x013d
        L_0x0138:
            int r12 = r12 + 1
            r7 = 0
            goto L_0x010c
        L_0x013c:
            r0 = move-exception
        L_0x013d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0140:
            r0 = 0
            goto L_0x014b
        L_0x0142:
            android.os.Bundle r0 = r82.getExtras()
            r7 = 0
            java.lang.String r0 = r0.getString(r1, r7)
        L_0x014b:
            java.lang.String r1 = org.telegram.messenger.SharedConfig.directShareHash
            if (r1 == 0) goto L_0x0155
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0157
        L_0x0155:
            r19 = r5
        L_0x0157:
            java.lang.String r1 = r82.getType()
            if (r1 == 0) goto L_0x019d
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x019d
            android.os.Bundle r0 = r82.getExtras()     // Catch:{ Exception -> 0x0196 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0196 }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x0196 }
            if (r0 == 0) goto L_0x019a
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0196 }
            r3 = 0
            r4 = 0
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r2, r4, r3, r3)     // Catch:{ Exception -> 0x0196 }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0196 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0196 }
            r4 = 5
            if (r2 <= r4) goto L_0x0192
            r15.contactsToSend = r3     // Catch:{ Exception -> 0x0196 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0196 }
            r2.<init>()     // Catch:{ Exception -> 0x0196 }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x0196 }
            r2.add(r0)     // Catch:{ Exception -> 0x0196 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x0196 }
            goto L_0x032a
        L_0x0192:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x0196 }
            goto L_0x032a
        L_0x0196:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x019a:
            r0 = 1
            goto L_0x032b
        L_0x019d:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x01b1
            java.lang.String r7 = "android.intent.extra.TEXT"
            java.lang.CharSequence r7 = r14.getCharSequenceExtra(r7)
            if (r7 == 0) goto L_0x01b1
            java.lang.String r0 = r7.toString()
        L_0x01b1:
            java.lang.String r7 = "android.intent.extra.SUBJECT"
            java.lang.String r7 = r14.getStringExtra(r7)
            boolean r9 = android.text.TextUtils.isEmpty(r0)
            if (r9 != 0) goto L_0x01e8
            java.lang.String r9 = "http://"
            boolean r9 = r0.startsWith(r9)
            if (r9 != 0) goto L_0x01cd
            java.lang.String r9 = "https://"
            boolean r9 = r0.startsWith(r9)
            if (r9 == 0) goto L_0x01e5
        L_0x01cd:
            boolean r9 = android.text.TextUtils.isEmpty(r7)
            if (r9 != 0) goto L_0x01e5
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r7)
            r9.append(r4)
            r9.append(r0)
            java.lang.String r0 = r9.toString()
        L_0x01e5:
            r15.sendingText = r0
            goto L_0x01f0
        L_0x01e8:
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x01f0
            r15.sendingText = r7
        L_0x01f0:
            android.os.Parcelable r0 = r14.getParcelableExtra(r2)
            if (r0 == 0) goto L_0x0324
            boolean r2 = r0 instanceof android.net.Uri
            if (r2 != 0) goto L_0x0202
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x0202:
            r2 = r0
            android.net.Uri r2 = (android.net.Uri) r2
            if (r2 == 0) goto L_0x020f
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r2)
            if (r0 == 0) goto L_0x020f
            r4 = 1
            goto L_0x0210
        L_0x020f:
            r4 = 0
        L_0x0210:
            if (r4 != 0) goto L_0x0322
            if (r2 == 0) goto L_0x0322
            if (r1 == 0) goto L_0x021e
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)
            if (r0 != 0) goto L_0x022e
        L_0x021e:
            java.lang.String r0 = r2.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r7 = ".jpg"
            boolean r0 = r0.endsWith(r7)
            if (r0 == 0) goto L_0x0247
        L_0x022e:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x0239
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x0239:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r2
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0322
        L_0x0247:
            java.lang.String r7 = r2.toString()
            int r0 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x02c0
            if (r7 == 0) goto L_0x02c0
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0269
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r9 = "export path = "
            r0.append(r9)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0269:
            r9 = 0
            r0 = r11[r9]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r0 = r0.exportUri
            java.lang.String r9 = org.telegram.messenger.MediaController.getFileName(r2)
            java.lang.String r9 = org.telegram.messenger.FileLoader.fixFileName(r9)
            java.util.Iterator r12 = r0.iterator()
        L_0x027e:
            boolean r0 = r12.hasNext()
            if (r0 == 0) goto L_0x02aa
            java.lang.Object r0 = r12.next()
            java.lang.String r0 = (java.lang.String) r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x02a5 }
            java.util.regex.Matcher r13 = r0.matcher(r7)     // Catch:{ Exception -> 0x02a5 }
            boolean r13 = r13.find()     // Catch:{ Exception -> 0x02a5 }
            if (r13 != 0) goto L_0x02a2
            java.util.regex.Matcher r0 = r0.matcher(r9)     // Catch:{ Exception -> 0x02a5 }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x02a5 }
            if (r0 == 0) goto L_0x027e
        L_0x02a2:
            r15.exportingChatUri = r2     // Catch:{ Exception -> 0x02a5 }
            goto L_0x02aa
        L_0x02a5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x027e
        L_0x02aa:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x02c0
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x02c0
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r7.endsWith(r0)
            if (r0 == 0) goto L_0x02c0
            r15.exportingChatUri = r2
        L_0x02c0:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x0322
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            boolean r7 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r7 != 0) goto L_0x02d2
            java.lang.String r0 = "file"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
        L_0x02d2:
            if (r0 == 0) goto L_0x0310
            java.lang.String r7 = "file:"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x02e2
            java.lang.String r7 = "file://"
            java.lang.String r0 = r0.replace(r7, r3)
        L_0x02e2:
            if (r1 == 0) goto L_0x02ef
            java.lang.String r3 = "video/"
            boolean r1 = r1.startsWith(r3)
            if (r1 == 0) goto L_0x02ef
            r15.videoPath = r0
            goto L_0x0322
        L_0x02ef:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            if (r1 != 0) goto L_0x0301
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsPathsArray = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsOriginalPathsArray = r1
        L_0x0301:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            r1.add(r0)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r2.toString()
            r0.add(r1)
            goto L_0x0322
        L_0x0310:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            if (r0 != 0) goto L_0x031b
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.documentsUrisArray = r0
        L_0x031b:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray
            r0.add(r2)
            r15.documentsMimeType = r1
        L_0x0322:
            r0 = r4
            goto L_0x032b
        L_0x0324:
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x032a
            goto L_0x019a
        L_0x032a:
            r0 = 0
        L_0x032b:
            if (r0 == 0) goto L_0x0337
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x0337:
            r12 = r5
            r7 = r15
            r72 = r19
            r0 = -1
            r1 = 0
            r4 = -1
            r10 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r37 = 0
            r38 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r76 = 0
            r77 = 0
            r78 = 0
            r79 = 0
            r6 = r8
            r5 = 0
            r8 = r12
            r80 = r14
            r14 = r11
            r11 = r80
            goto L_0x1ec9
        L_0x0368:
            java.lang.String r0 = r82.getAction()
            java.lang.String r7 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0399
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r2)     // Catch:{ all -> 0x038c }
            r15.importingStickers = r0     // Catch:{ all -> 0x038c }
            java.lang.String r0 = "STICKER_EMOJIS"
            java.util.ArrayList r0 = r14.getStringArrayListExtra(r0)     // Catch:{ all -> 0x038c }
            r15.importingStickersEmoji = r0     // Catch:{ all -> 0x038c }
            java.lang.String r0 = "IMPORTER"
            java.lang.String r0 = r14.getStringExtra(r0)     // Catch:{ all -> 0x038c }
            r15.importingStickersSoftware = r0     // Catch:{ all -> 0x038c }
            goto L_0x1e95
        L_0x038c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r15.importingStickers = r1
            r15.importingStickersEmoji = r1
            r15.importingStickersSoftware = r1
            goto L_0x1e95
        L_0x0399:
            java.lang.String r0 = r82.getAction()
            java.lang.String r7 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0523
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r2)     // Catch:{ Exception -> 0x050b }
            java.lang.String r1 = r82.getType()     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x03e2
            r2 = 0
        L_0x03b0:
            int r4 = r0.size()     // Catch:{ Exception -> 0x050b }
            if (r2 >= r4) goto L_0x03da
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x050b }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x050b }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x050b }
            if (r7 != 0) goto L_0x03c8
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x050b }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x050b }
        L_0x03c8:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x050b }
            if (r4 == 0) goto L_0x03d7
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x050b }
            if (r4 == 0) goto L_0x03d7
            r0.remove(r2)     // Catch:{ Exception -> 0x050b }
            int r2 = r2 + -1
        L_0x03d7:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x03b0
        L_0x03da:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x050b }
            if (r2 == 0) goto L_0x03e2
            r2 = 0
            goto L_0x03e3
        L_0x03e2:
            r2 = r0
        L_0x03e3:
            if (r2 == 0) goto L_0x050f
            if (r1 == 0) goto L_0x0424
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x0424
            r0 = 0
        L_0x03f0:
            int r1 = r2.size()     // Catch:{ Exception -> 0x050b }
            if (r0 >= r1) goto L_0x0509
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x050b }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x050b }
            boolean r3 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x050b }
            if (r3 != 0) goto L_0x0408
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x050b }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x050b }
        L_0x0408:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x050b }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x050b }
            if (r3 != 0) goto L_0x0415
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x050b }
            r3.<init>()     // Catch:{ Exception -> 0x050b }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x050b }
        L_0x0415:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x050b }
            r3.<init>()     // Catch:{ Exception -> 0x050b }
            r3.uri = r1     // Catch:{ Exception -> 0x050b }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x050b }
            r1.add(r3)     // Catch:{ Exception -> 0x050b }
            int r0 = r0 + 1
            goto L_0x03f0
        L_0x0424:
            r4 = 0
            r0 = r11[r4]     // Catch:{ Exception -> 0x050b }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x050b }
            java.util.Set<java.lang.String> r4 = r0.exportUri     // Catch:{ Exception -> 0x050b }
            r7 = 0
        L_0x042e:
            int r0 = r2.size()     // Catch:{ Exception -> 0x050b }
            if (r7 >= r0) goto L_0x0509
            java.lang.Object r0 = r2.get(r7)     // Catch:{ Exception -> 0x050b }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x050b }
            boolean r9 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x050b }
            if (r9 != 0) goto L_0x0446
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x050b }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x050b }
        L_0x0446:
            r9 = r0
            android.net.Uri r9 = (android.net.Uri) r9     // Catch:{ Exception -> 0x050b }
            java.lang.String r12 = org.telegram.messenger.AndroidUtilities.getPath(r9)     // Catch:{ Exception -> 0x050b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x050b }
            if (r0 != 0) goto L_0x0455
            r13 = r12
            goto L_0x0456
        L_0x0455:
            r13 = r0
        L_0x0456:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x046e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050b }
            r0.<init>()     // Catch:{ Exception -> 0x050b }
            java.lang.String r5 = "export path = "
            r0.append(r5)     // Catch:{ Exception -> 0x050b }
            r0.append(r13)     // Catch:{ Exception -> 0x050b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x050b }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x050b }
        L_0x046e:
            if (r13 == 0) goto L_0x04c4
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x050b }
            if (r0 != 0) goto L_0x04c4
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r9)     // Catch:{ Exception -> 0x050b }
            java.lang.String r5 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x050b }
            java.util.Iterator r6 = r4.iterator()     // Catch:{ Exception -> 0x050b }
        L_0x0480:
            boolean r0 = r6.hasNext()     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x04ad
            java.lang.Object r0 = r6.next()     // Catch:{ Exception -> 0x050b }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x050b }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x04a8 }
            java.util.regex.Matcher r23 = r0.matcher(r13)     // Catch:{ Exception -> 0x04a8 }
            boolean r23 = r23.find()     // Catch:{ Exception -> 0x04a8 }
            if (r23 != 0) goto L_0x04a4
            java.util.regex.Matcher r0 = r0.matcher(r5)     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0480
        L_0x04a4:
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x04a8 }
            r0 = 1
            goto L_0x04ae
        L_0x04a8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x050b }
            goto L_0x0480
        L_0x04ad:
            r0 = 0
        L_0x04ae:
            if (r0 == 0) goto L_0x04b1
            goto L_0x0503
        L_0x04b1:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x04c4
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r13.endsWith(r0)     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x04c4
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x050b }
            goto L_0x0503
        L_0x04c4:
            if (r12 == 0) goto L_0x04f1
            java.lang.String r0 = "file:"
            boolean r0 = r12.startsWith(r0)     // Catch:{ Exception -> 0x050b }
            if (r0 == 0) goto L_0x04d4
            java.lang.String r0 = "file://"
            java.lang.String r12 = r12.replace(r0, r3)     // Catch:{ Exception -> 0x050b }
        L_0x04d4:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x050b }
            if (r0 != 0) goto L_0x04e6
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x050b }
            r0.<init>()     // Catch:{ Exception -> 0x050b }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x050b }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x050b }
            r0.<init>()     // Catch:{ Exception -> 0x050b }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x050b }
        L_0x04e6:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x050b }
            r0.add(r12)     // Catch:{ Exception -> 0x050b }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x050b }
            r0.add(r13)     // Catch:{ Exception -> 0x050b }
            goto L_0x0503
        L_0x04f1:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x050b }
            if (r0 != 0) goto L_0x04fc
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x050b }
            r0.<init>()     // Catch:{ Exception -> 0x050b }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x050b }
        L_0x04fc:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x050b }
            r0.add(r9)     // Catch:{ Exception -> 0x050b }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x050b }
        L_0x0503:
            int r7 = r7 + 1
            r5 = 0
            goto L_0x042e
        L_0x0509:
            r0 = 0
            goto L_0x0510
        L_0x050b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x050f:
            r0 = 1
        L_0x0510:
            if (r0 == 0) goto L_0x051c
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x051c:
            r6 = r8
            r7 = r15
            r1 = 2
            r2 = 0
            goto L_0x1e99
        L_0x0523:
            java.lang.String r0 = r82.getAction()
            java.lang.String r2 = "android.intent.action.VIEW"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x1d0a
            android.net.Uri r0 = r82.getData()
            if (r0 == 0) goto L_0x1cba
            java.lang.String r2 = r0.getScheme()
            java.lang.String r5 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r6 = "phone"
            if (r2 == 0) goto L_0x1a2b
            int r7 = r2.hashCode()
            switch(r7) {
                case 3699: goto L_0x055e;
                case 3213448: goto L_0x0553;
                case 99617003: goto L_0x0548;
                default: goto L_0x0546;
            }
        L_0x0546:
            r7 = -1
            goto L_0x0568
        L_0x0548:
            java.lang.String r7 = "https"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x0551
            goto L_0x0546
        L_0x0551:
            r7 = 2
            goto L_0x0568
        L_0x0553:
            java.lang.String r7 = "http"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x055c
            goto L_0x0546
        L_0x055c:
            r7 = 1
            goto L_0x0568
        L_0x055e:
            java.lang.String r7 = "tg"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x0567
            goto L_0x0546
        L_0x0567:
            r7 = 0
        L_0x0568:
            java.lang.String r9 = "thread"
            r23 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r7) {
                case 0: goto L_0x0bd6;
                case 1: goto L_0x0571;
                case 2: goto L_0x0571;
                default: goto L_0x056f;
            }
        L_0x056f:
            goto L_0x1a2b
        L_0x0571:
            java.lang.String r7 = r0.getHost()
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r13 = "telegram.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0591
            java.lang.String r13 = "t.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0591
            java.lang.String r13 = "telegram.dog"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x1a2b
        L_0x0591:
            java.lang.String r7 = r0.getPath()
            if (r7 == 0) goto L_0x0b47
            int r13 = r7.length()
            r12 = 1
            if (r13 <= r12) goto L_0x0b47
            java.lang.String r7 = r7.substring(r12)
            java.lang.String r13 = "$"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x05d4
            java.lang.String r0 = r7.substring(r12)
        L_0x05ae:
            r37 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            goto L_0x0b6b
        L_0x05d4:
            java.lang.String r13 = "invoice/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x05e8
            r0 = 47
            int r0 = r7.indexOf(r0)
            int r0 = r0 + r12
            java.lang.String r0 = r7.substring(r0)
            goto L_0x05ae
        L_0x05e8:
            java.lang.String r12 = "bg/"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0800
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r4 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r4.<init>()
            r1.settings = r4
            java.lang.String r4 = "bg/"
            java.lang.String r3 = r7.replace(r4, r3)
            r1.slug = r3
            if (r3 == 0) goto L_0x0621
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x0621
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x06d9 }
            r4 = r4 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x06d9 }
            r3 = 0
            r1.slug = r3     // Catch:{ Exception -> 0x06d9 }
        L_0x061e:
            r3 = 1
            goto L_0x06da
        L_0x0621:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x06d9
            int r3 = r3.length()
            r4 = 13
            if (r3 < r4) goto L_0x06d9
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)
            if (r3 == 0) goto L_0x06d9
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r9 = 0
            java.lang.String r7 = r7.substring(r9, r4)     // Catch:{ Exception -> 0x06d9 }
            r4 = 16
            int r7 = java.lang.Integer.parseInt(r7, r4)     // Catch:{ Exception -> 0x06d9 }
            r4 = r7 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x06d9 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r7 = 7
            r9 = 13
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x06d9 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x06d9 }
            r4 = r4 | r23
            r3.second_background_color = r4     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x06d9 }
            r4 = 20
            if (r3 < r4) goto L_0x0690
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r4 = 13
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x06d9 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x06d9 }
            if (r3 == 0) goto L_0x0690
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r7 = 14
            r9 = 20
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x06d9 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x06d9 }
            r4 = r4 | r23
            r3.third_background_color = r4     // Catch:{ Exception -> 0x06d9 }
        L_0x0690:
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x06d9 }
            r4 = 27
            if (r3 != r4) goto L_0x06bc
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r4 = 20
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x06d9 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x06d9 }
            if (r3 == 0) goto L_0x06bc
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x06d9 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x06d9 }
            r7 = 21
            java.lang.String r4 = r4.substring(r7)     // Catch:{ Exception -> 0x06d9 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x06d9 }
            r4 = r4 | r23
            r3.fourth_background_color = r4     // Catch:{ Exception -> 0x06d9 }
        L_0x06bc:
            java.lang.String r3 = "rotation"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x06d4 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d4 }
            if (r4 != 0) goto L_0x06d4
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x06d4 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3)     // Catch:{ Exception -> 0x06d4 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x06d4 }
            r4.rotation = r3     // Catch:{ Exception -> 0x06d4 }
        L_0x06d4:
            r3 = 0
            r1.slug = r3     // Catch:{ Exception -> 0x06d9 }
            goto L_0x061e
        L_0x06d9:
            r3 = 0
        L_0x06da:
            if (r3 != 0) goto L_0x07db
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x0719
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x0719
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0719
            r4 = 0
        L_0x06f4:
            int r7 = r3.length
            if (r4 >= r7) goto L_0x0719
            r7 = r3[r4]
            java.lang.String r9 = "blur"
            boolean r7 = r9.equals(r7)
            if (r7 == 0) goto L_0x0707
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r9 = 1
            r7.blur = r9
            goto L_0x0716
        L_0x0707:
            r9 = 1
            r7 = r3[r4]
            java.lang.String r12 = "motion"
            boolean r7 = r12.equals(r7)
            if (r7 == 0) goto L_0x0716
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r7.motion = r9
        L_0x0716:
            int r4 = r4 + 1
            goto L_0x06f4
        L_0x0719:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0732
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3)
            int r3 = r3.intValue()
            r4.intensity = r3
            goto L_0x0738
        L_0x0732:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            r4 = 50
            r3.intensity = r4
        L_0x0738:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x07bb }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x07bb }
            if (r4 != 0) goto L_0x07bd
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x07bb }
            r7 = 6
            r9 = 0
            java.lang.String r12 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r12, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = r9 | r23
            r4.background_color = r7     // Catch:{ Exception -> 0x07bb }
            int r4 = r3.length()     // Catch:{ Exception -> 0x07bb }
            r7 = 13
            if (r4 < r7) goto L_0x07bb
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x07bb }
            r9 = 7
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = r9 | r23
            r4.second_background_color = r7     // Catch:{ Exception -> 0x07bb }
            int r4 = r3.length()     // Catch:{ Exception -> 0x07bb }
            r7 = 20
            if (r4 < r7) goto L_0x0795
            r4 = 13
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x07bb }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x07bb }
            if (r4 == 0) goto L_0x0795
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x07bb }
            r9 = 14
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x07bb }
            r7 = r9 | r23
            r4.third_background_color = r7     // Catch:{ Exception -> 0x07bb }
        L_0x0795:
            int r4 = r3.length()     // Catch:{ Exception -> 0x07bb }
            r7 = 27
            if (r4 != r7) goto L_0x07bb
            r4 = 20
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x07bb }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x07bb }
            if (r4 == 0) goto L_0x07bb
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x07bb }
            r7 = 21
            java.lang.String r3 = r3.substring(r7)     // Catch:{ Exception -> 0x07bb }
            r7 = 16
            int r3 = java.lang.Integer.parseInt(r3, r7)     // Catch:{ Exception -> 0x07bb }
            r3 = r3 | r23
            r4.fourth_background_color = r3     // Catch:{ Exception -> 0x07bb }
        L_0x07bb:
            r12 = -1
            goto L_0x07c2
        L_0x07bd:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x07bb }
            r12 = -1
            r3.background_color = r12     // Catch:{ Exception -> 0x07c2 }
        L_0x07c2:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x07dc }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x07dc }
            if (r3 != 0) goto L_0x07dc
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x07dc }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x07dc }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x07dc }
            r3.rotation = r0     // Catch:{ Exception -> 0x07dc }
            goto L_0x07dc
        L_0x07db:
            r12 = -1
        L_0x07dc:
            r36 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            goto L_0x0b69
        L_0x0800:
            r12 = -1
            java.lang.String r13 = "login/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x084c
            java.lang.String r0 = "login/"
            java.lang.String r0 = r7.replace(r0, r3)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0829
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x082a
        L_0x0829:
            r0 = 0
        L_0x082a:
            r35 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            goto L_0x0b67
        L_0x084c:
            java.lang.String r13 = "joinchat/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x0860
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r7.replace(r0, r3)
        L_0x085a:
            r1 = 0
            r3 = 0
        L_0x085c:
            r4 = -1
            r7 = 2
            goto L_0x0b4c
        L_0x0860:
            java.lang.String r13 = "+"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x0878
            java.lang.String r0 = "+"
            java.lang.String r0 = r7.replace(r0, r3)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isNumeric(r0)
            if (r1 == 0) goto L_0x085a
            r3 = r0
            r0 = 0
            r1 = 0
            goto L_0x085c
        L_0x0878:
            java.lang.String r13 = "addstickers/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x088e
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r7.replace(r0, r3)
            r9 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            goto L_0x0b4d
        L_0x088e:
            java.lang.String r13 = "addemoji/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x08a5
            java.lang.String r0 = "addemoji/"
            java.lang.String r0 = r7.replace(r0, r3)
            r12 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            goto L_0x0b4e
        L_0x08a5:
            java.lang.String r13 = "msg/"
            boolean r13 = r7.startsWith(r13)
            if (r13 != 0) goto L_0x0ad3
            java.lang.String r13 = "share/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x08b7
            goto L_0x0ad3
        L_0x08b7:
            java.lang.String r3 = "confirmphone"
            boolean r3 = r7.startsWith(r3)
            if (r3 == 0) goto L_0x08e3
            java.lang.String r3 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r31 = r0
            r27 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0b61
        L_0x08e3:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x090f
            r0 = 12
            java.lang.String r0 = r7.substring(r0)
            r32 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            goto L_0x0b63
        L_0x090f:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x093d
            r0 = 9
            java.lang.String r0 = r7.substring(r0)
            r34 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0b65
        L_0x093d:
            java.lang.String r1 = "c/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x09b8
            java.util.List r1 = r0.getPathSegments()
            int r3 = r1.size()
            r13 = 3
            if (r3 != r13) goto L_0x0988
            r3 = 1
            java.lang.Object r4 = r1.get(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r4)
            r4 = 2
            java.lang.Object r1 = r1.get(r4)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r7 = r1.intValue()
            if (r7 == 0) goto L_0x0976
            long r16 = r3.longValue()
            r19 = 0
            int r7 = (r16 > r19 ? 1 : (r16 == r19 ? 0 : -1))
            if (r7 != 0) goto L_0x0978
        L_0x0976:
            r1 = 0
            r3 = 0
        L_0x0978:
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r7 = r0.intValue()
            if (r7 != 0) goto L_0x098c
            r0 = 0
            goto L_0x098c
        L_0x0988:
            r4 = 2
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x098c:
            r40 = r0
            r38 = r1
            r39 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 2
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
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
            goto L_0x0b71
        L_0x09b8:
            r4 = 2
            r13 = 3
            int r1 = r7.length()
            r3 = 1
            if (r1 < r3) goto L_0x0b47
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r1.<init>(r3)
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x09e3
            r3 = 0
            java.lang.Object r7 = r1.get(r3)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.String r4 = "s"
            boolean r4 = r7.equals(r4)
            if (r4 == 0) goto L_0x09e4
            r1.remove(r3)
            goto L_0x09e4
        L_0x09e3:
            r3 = 0
        L_0x09e4:
            int r4 = r1.size()
            if (r4 <= 0) goto L_0x0a0a
            java.lang.Object r4 = r1.get(r3)
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            int r4 = r1.size()
            r7 = 1
            if (r4 <= r7) goto L_0x0a08
            java.lang.Object r1 = r1.get(r7)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x0a0c
        L_0x0a08:
            r1 = 0
            goto L_0x0a0c
        L_0x0a0a:
            r1 = 0
            r3 = 0
        L_0x0a0c:
            if (r1 == 0) goto L_0x0a13
            int r4 = getTimestampFromLink(r0)
            goto L_0x0a14
        L_0x0a13:
            r4 = -1
        L_0x0a14:
            java.lang.String r7 = "start"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r12 = "startgroup"
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.String r13 = "startchannel"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r84 = r1
            java.lang.String r1 = "admin"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r23 = r1
            java.lang.String r1 = "game"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r25 = r1
            java.lang.String r1 = "voicechat"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r26 = r1
            java.lang.String r1 = "livestream"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r27 = r1
            java.lang.String r1 = "startattach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r28 = r1
            java.lang.String r1 = "choose"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r29 = r1
            java.lang.String r1 = "attach"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)
            int r30 = r9.intValue()
            if (r30 != 0) goto L_0x0a70
            r30 = r1
            r9 = 0
            goto L_0x0a72
        L_0x0a70:
            r30 = r1
        L_0x0a72:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r1 = r0.intValue()
            r38 = r84
            if (r1 != 0) goto L_0x0aa3
            r40 = r9
            r16 = r12
            r42 = r28
            r44 = r29
            r43 = r30
            r0 = 0
            r1 = 0
            r9 = 0
            r12 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r39 = 0
            r41 = 0
            goto L_0x0ac1
        L_0x0aa3:
            r41 = r0
            r40 = r9
            r16 = r12
            r42 = r28
            r44 = r29
            r43 = r30
            r0 = 0
            r1 = 0
            r9 = 0
            r12 = 0
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r39 = 0
        L_0x0ac1:
            r28 = r25
            r29 = r26
            r30 = r27
            r26 = 0
            r27 = 0
            r25 = r23
            r23 = r13
            r13 = r7
            r7 = 2
            goto L_0x0b79
        L_0x0ad3:
            r7 = 2
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x0add
            goto L_0x0ade
        L_0x0add:
            r3 = r1
        L_0x0ade:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x0b14
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x0afd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r4)
            java.lang.String r3 = r1.toString()
            r1 = 1
            goto L_0x0afe
        L_0x0afd:
            r1 = 0
        L_0x0afe:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r9.append(r0)
            java.lang.String r3 = r9.toString()
            goto L_0x0b15
        L_0x0b14:
            r1 = 0
        L_0x0b15:
            int r0 = r3.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x0b25
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r3.substring(r9, r0)
            goto L_0x0b27
        L_0x0b25:
            r9 = 0
            r0 = r3
        L_0x0b27:
            boolean r3 = r0.endsWith(r4)
            if (r3 == 0) goto L_0x0b38
            int r3 = r0.length()
            r12 = 1
            int r3 = r3 - r12
            java.lang.String r0 = r0.substring(r9, r3)
            goto L_0x0b27
        L_0x0b38:
            r26 = r0
            r0 = 0
            r3 = 0
            r4 = -1
            r9 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            goto L_0x0b57
        L_0x0b47:
            r7 = 2
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
        L_0x0b4c:
            r9 = 0
        L_0x0b4d:
            r12 = 0
        L_0x0b4e:
            r13 = 0
            r16 = 0
            r23 = 0
            r25 = 0
            r26 = 0
        L_0x0b57:
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
        L_0x0b61:
            r32 = 0
        L_0x0b63:
            r34 = 0
        L_0x0b65:
            r35 = 0
        L_0x0b67:
            r36 = 0
        L_0x0b69:
            r37 = 0
        L_0x0b6b:
            r38 = 0
            r39 = 0
            r40 = 0
        L_0x0b71:
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
        L_0x0b79:
            r24 = r0
            r60 = r4
            r10 = r25
            r0 = r27
            r58 = r29
            r59 = r30
            r47 = r32
            r57 = r34
            r53 = r35
            r55 = r36
            r56 = r37
            r18 = r38
            r25 = r39
            r27 = r41
            r61 = r42
            r62 = r43
            r63 = r44
            r4 = 6
            r7 = 0
            r19 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 0
            r50 = 0
            r52 = 0
            r54 = 0
            r29 = r11
            r30 = r13
            r11 = r16
            r32 = r28
            r13 = r1
            r28 = r2
            r2 = r26
            r1 = r31
            r26 = r40
            r31 = 0
            r40 = 0
            goto L_0x1a86
        L_0x0bd6:
            r7 = 2
            java.lang.String r12 = r0.toString()
            java.lang.String r13 = "tg:resolve"
            boolean r13 = r12.startsWith(r13)
            java.lang.String r7 = "scope"
            r27 = r0
            java.lang.String r0 = "tg://telegram.org"
            if (r13 != 0) goto L_0x0CLASSNAME
            java.lang.String r13 = "tg://resolve"
            boolean r13 = r12.startsWith(r13)
            if (r13 == 0) goto L_0x0bf2
            goto L_0x0CLASSNAME
        L_0x0bf2:
            r28 = r2
            r29 = r11
            r2 = r12
            r13 = r27
            r11 = 0
            r12 = 0
            r27 = 0
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
        L_0x0CLASSNAME:
            r42 = 0
            goto L_0x0d69
        L_0x0CLASSNAME:
            java.lang.String r13 = "tg:resolve"
            java.lang.String r12 = r12.replace(r13, r0)
            java.lang.String r13 = "tg://resolve"
            java.lang.String r12 = r12.replace(r13, r0)
            android.net.Uri r13 = android.net.Uri.parse(r12)
            java.lang.String r10 = "domain"
            java.lang.String r10 = r13.getQueryParameter(r10)
            if (r10 != 0) goto L_0x0CLASSNAME
            java.lang.String r10 = r13.getQueryParameter(r6)
            if (r10 == 0) goto L_0x0CLASSNAME
            r27 = r12
            java.lang.String r12 = "+"
            boolean r12 = r10.startsWith(r12)
            if (r12 == 0) goto L_0x0CLASSNAME
            r12 = 1
            java.lang.String r10 = r10.substring(r12)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r27 = r12
        L_0x0CLASSNAME:
            java.lang.String r12 = "telegrampassport"
            boolean r12 = r12.equals(r10)
            if (r12 == 0) goto L_0x0cd2
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            java.lang.String r12 = r13.getQueryParameter(r7)
            boolean r28 = android.text.TextUtils.isEmpty(r12)
            if (r28 != 0) goto L_0x0CLASSNAME
            r29 = r11
            java.lang.String r11 = "{"
            boolean r11 = r12.startsWith(r11)
            if (r11 == 0) goto L_0x0c7e
            java.lang.String r11 = "}"
            boolean r11 = r12.endsWith(r11)
            if (r11 == 0) goto L_0x0c7e
            java.lang.String r11 = "nonce"
            java.lang.String r11 = r13.getQueryParameter(r11)
            r28 = r2
            java.lang.String r2 = "nonce"
            r10.put(r2, r11)
            goto L_0x0CLASSNAME
        L_0x0c7e:
            r28 = r2
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r28 = r2
            r29 = r11
        L_0x0CLASSNAME:
            java.lang.String r2 = "payload"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.String r11 = "payload"
            r10.put(r11, r2)
        L_0x0CLASSNAME:
            java.lang.String r2 = "bot_id"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.String r11 = "bot_id"
            r10.put(r11, r2)
            r10.put(r7, r12)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.String r11 = "public_key"
            r10.put(r11, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.String r11 = "callback_url"
            r10.put(r11, r2)
            r41 = r10
            r2 = r27
            r11 = 0
            r12 = 0
            r27 = 0
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
            goto L_0x0CLASSNAME
        L_0x0cd2:
            r28 = r2
            r29 = r11
            java.lang.String r2 = "start"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.String r11 = "startgroup"
            java.lang.String r11 = r13.getQueryParameter(r11)
            java.lang.String r12 = "startchannel"
            java.lang.String r12 = r13.getQueryParameter(r12)
            r30 = r2
            java.lang.String r2 = "admin"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r31 = r2
            java.lang.String r2 = "game"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r32 = r2
            java.lang.String r2 = "voicechat"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r34 = r2
            java.lang.String r2 = "livestream"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r35 = r2
            java.lang.String r2 = "startattach"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r36 = r2
            java.lang.String r2 = "choose"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r37 = r2
            java.lang.String r2 = "attach"
            java.lang.String r2 = r13.getQueryParameter(r2)
            r38 = r2
            java.lang.String r2 = "post"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r39 = r2.intValue()
            if (r39 != 0) goto L_0x0d33
            r2 = 0
        L_0x0d33:
            java.lang.String r39 = r13.getQueryParameter(r9)
            java.lang.Integer r39 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r39)
            int r40 = r39.intValue()
            if (r40 != 0) goto L_0x0d46
            r40 = r2
            r39 = 0
            goto L_0x0d48
        L_0x0d46:
            r40 = r2
        L_0x0d48:
            java.lang.String r2 = "comment"
            java.lang.String r2 = r13.getQueryParameter(r2)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r41 = r2.intValue()
            r42 = r10
            if (r41 != 0) goto L_0x0d61
            r2 = r27
            r27 = 0
            r41 = 0
            goto L_0x0d69
        L_0x0d61:
            r41 = 0
            r80 = r27
            r27 = r2
            r2 = r80
        L_0x0d69:
            java.lang.String r10 = "tg:invoice"
            boolean r10 = r2.startsWith(r10)
            if (r10 != 0) goto L_0x19c4
            java.lang.String r10 = "tg://invoice"
            boolean r10 = r2.startsWith(r10)
            if (r10 == 0) goto L_0x0d7b
            goto L_0x19c4
        L_0x0d7b:
            java.lang.String r10 = "tg:privatepost"
            boolean r10 = r2.startsWith(r10)
            if (r10 != 0) goto L_0x1924
            java.lang.String r10 = "tg://privatepost"
            boolean r10 = r2.startsWith(r10)
            if (r10 == 0) goto L_0x0d8d
            goto L_0x1924
        L_0x0d8d:
            java.lang.String r9 = "tg:bg"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x16de
            java.lang.String r9 = "tg://bg"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x0d9f
            goto L_0x16de
        L_0x0d9f:
            java.lang.String r9 = "tg:join"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x1676
            java.lang.String r9 = "tg://join"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x0db1
            goto L_0x1676
        L_0x0db1:
            java.lang.String r9 = "tg:addstickers"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x163d
            java.lang.String r9 = "tg://addstickers"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x0dc3
            goto L_0x163d
        L_0x0dc3:
            java.lang.String r9 = "tg:addemoji"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x15d3
            java.lang.String r9 = "tg://addemoji"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x0dd5
            goto L_0x15d3
        L_0x0dd5:
            java.lang.String r9 = "tg:msg"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x1533
            java.lang.String r9 = "tg://msg"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x1533
            java.lang.String r9 = "tg://share"
            boolean r9 = r2.startsWith(r9)
            if (r9 != 0) goto L_0x1533
            java.lang.String r9 = "tg:share"
            boolean r9 = r2.startsWith(r9)
            if (r9 == 0) goto L_0x0df7
            goto L_0x1533
        L_0x0df7:
            java.lang.String r4 = "tg:confirmphone"
            boolean r4 = r2.startsWith(r4)
            if (r4 != 0) goto L_0x14fd
            java.lang.String r4 = "tg://confirmphone"
            boolean r4 = r2.startsWith(r4)
            if (r4 == 0) goto L_0x0e09
            goto L_0x14fd
        L_0x0e09:
            java.lang.String r1 = "tg:login"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x147a
            java.lang.String r1 = "tg://login"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e1b
            goto L_0x147a
        L_0x0e1b:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x13ea
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e2d
            goto L_0x13ea
        L_0x0e2d:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x1326
            java.lang.String r1 = "tg://passport"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x1326
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e47
            goto L_0x1326
        L_0x0e47:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x12ce
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e59
            goto L_0x12ce
        L_0x0e59:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x1266
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e6b
            goto L_0x1266
        L_0x0e6b:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x1168
            java.lang.String r1 = "tg://settings"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e7d
            goto L_0x1168
        L_0x0e7d:
            java.lang.String r1 = "tg:search"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x10f8
            java.lang.String r1 = "tg://search"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0e8f
            goto L_0x10f8
        L_0x0e8f:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x10ca
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0ea1
            goto L_0x10ca
        L_0x0ea1:
            java.lang.String r1 = "tg:call"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x0fcb
            java.lang.String r1 = "tg://call"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0eb3
            goto L_0x0fcb
        L_0x0eb3:
            java.lang.String r1 = "tg:scanqr"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://scanqr"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0ec5
            goto L_0x0var_
        L_0x0ec5:
            java.lang.String r1 = "tg:addcontact"
            boolean r1 = r2.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://addcontact"
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x0ed6
            goto L_0x0var_
        L_0x0ed6:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r2.replace(r0, r3)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r3)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0eef
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r1)
        L_0x0eef:
            r52 = r0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r50 = 0
            goto L_0x16d2
        L_0x0var_:
            java.lang.String r1 = "tg:addcontact"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://addcontact"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r46 = r0
            r45 = r1
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 1
            r42 = 0
            r43 = 0
            r44 = 0
            goto L_0x16ca
        L_0x0var_:
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 1
            goto L_0x16c4
        L_0x0fcb:
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x10b0
            int r0 = r15.currentAccount
            org.telegram.messenger.ContactsController r0 = org.telegram.messenger.ContactsController.getInstance(r0)
            boolean r0 = r0.contactsLoaded
            if (r0 != 0) goto L_0x100c
            java.lang.String r0 = "extra_force_call"
            boolean r0 = r14.hasExtra(r0)
            if (r0 == 0) goto L_0x0fea
            goto L_0x100c
        L_0x0fea:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r5)
            java.lang.String r1 = "extra_force_call"
            r2 = 1
            r0.putExtra(r1, r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64
            r1.<init>(r15, r0)
            r2 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r2)
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
        L_0x100a:
            r13 = 0
            goto L_0x105c
        L_0x100c:
            java.lang.String r0 = "format"
            java.lang.String r0 = r13.getQueryParameter(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r13.getQueryParameter(r1)
            java.lang.String r2 = r13.getQueryParameter(r6)
            r4 = 0
            java.util.List r7 = r15.findContacts(r1, r2, r4)
            boolean r9 = r7.isEmpty()
            if (r9 == 0) goto L_0x1032
            if (r2 == 0) goto L_0x1032
            r7 = r1
            r13 = r2
            r0 = 1
            r1 = 0
            r2 = 0
            r3 = 0
            r9 = 0
            goto L_0x105c
        L_0x1032:
            int r2 = r7.size()
            r9 = 1
            if (r2 != r9) goto L_0x1042
            java.lang.Object r2 = r7.get(r4)
            org.telegram.tgnet.TLRPC$TL_contact r2 = (org.telegram.tgnet.TLRPC$TL_contact) r2
            long r9 = r2.user_id
            goto L_0x1044
        L_0x1042:
            r9 = 0
        L_0x1044:
            r19 = 0
            int r2 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r2 != 0) goto L_0x104e
            if (r1 == 0) goto L_0x104f
            r3 = r1
            goto L_0x104f
        L_0x104e:
            r3 = 0
        L_0x104f:
            java.lang.String r1 = "video"
            boolean r0 = r1.equalsIgnoreCase(r0)
            r1 = r0 ^ 1
            r2 = r0
            r0 = 0
            r4 = 1
            r7 = 0
            goto L_0x100a
        L_0x105c:
            r44 = r3
            r45 = r7
            r48 = r9
            r23 = r12
            r46 = r13
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r41 = 0
            r43 = 0
            r47 = 0
            r50 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r60 = -1
            r42 = r0
            r38 = r1
            r39 = r2
            r40 = r4
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            goto L_0x1a86
        L_0x10b0:
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            goto L_0x152d
        L_0x10ca:
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 1
            goto L_0x16b8
        L_0x10f8:
            java.lang.String r1 = "tg:search"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://search"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x1114
            java.lang.String r3 = r0.trim()
        L_0x1114:
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            r47 = 0
            r48 = 0
            r50 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r60 = -1
            r34 = r3
            r3 = r42
            r42 = 0
            goto L_0x1a86
        L_0x1168:
            java.lang.String r0 = "themes"
            boolean r0 = r2.contains(r0)
            if (r0 == 0) goto L_0x119c
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 2
            goto L_0x16b6
        L_0x119c:
            java.lang.String r0 = "devices"
            boolean r0 = r2.contains(r0)
            if (r0 == 0) goto L_0x11d0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 3
            goto L_0x16b6
        L_0x11d0:
            java.lang.String r0 = "folders"
            boolean r0 = r2.contains(r0)
            if (r0 == 0) goto L_0x1205
            r0 = 4
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 4
            goto L_0x16b6
        L_0x1205:
            java.lang.String r0 = "change_number"
            boolean r0 = r2.contains(r0)
            if (r0 == 0) goto L_0x123a
            r0 = 5
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 5
            goto L_0x16b6
        L_0x123a:
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            goto L_0x16b6
        L_0x1266:
            java.lang.String r1 = "tg:addtheme"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://addtheme"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r57 = r0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r50 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            goto L_0x1a84
        L_0x12ce:
            java.lang.String r1 = "tg:setlanguage"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://setlanguage"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r47 = r0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            goto L_0x16cc
        L_0x1326:
            java.lang.String r1 = "tg:passport"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://passport"
            java.lang.String r1 = r1.replace(r2, r0)
            java.lang.String r2 = "tg:secureid"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = r0.getQueryParameter(r7)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x1367
            java.lang.String r3 = "{"
            boolean r3 = r2.startsWith(r3)
            if (r3 == 0) goto L_0x1367
            java.lang.String r3 = "}"
            boolean r3 = r2.endsWith(r3)
            if (r3 == 0) goto L_0x1367
            java.lang.String r3 = "nonce"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "nonce"
            r1.put(r4, r3)
            goto L_0x1372
        L_0x1367:
            java.lang.String r3 = "payload"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "payload"
            r1.put(r4, r3)
        L_0x1372:
            java.lang.String r3 = "bot_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "bot_id"
            r1.put(r4, r3)
            r1.put(r7, r2)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r3 = "public_key"
            r1.put(r3, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.String r2 = "callback_url"
            r1.put(r2, r0)
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r3 = r42
            r0 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r50 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r60 = -1
            r31 = r1
            r1 = 0
            goto L_0x1a86
        L_0x13ea:
            java.lang.String r1 = "tg:openmessage"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://openmessage"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "chat_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r8)
            if (r1 == 0) goto L_0x1411
            long r1 = java.lang.Long.parseLong(r1)     // Catch:{ NumberFormatException -> 0x141b }
            goto L_0x141d
        L_0x1411:
            if (r2 == 0) goto L_0x141b
            long r1 = java.lang.Long.parseLong(r2)     // Catch:{ NumberFormatException -> 0x141b }
            r3 = r1
            r1 = 0
            goto L_0x141f
        L_0x141b:
            r1 = 0
        L_0x141d:
            r3 = 0
        L_0x141f:
            if (r0 == 0) goto L_0x1426
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x1426 }
            goto L_0x1427
        L_0x1426:
            r0 = 0
        L_0x1427:
            r48 = r1
            r50 = r3
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
            r34 = 0
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
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r60 = -1
            r35 = r0
            goto L_0x163a
        L_0x147a:
            java.lang.String r1 = "tg:login"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://login"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r2 = "code"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x14b0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x14b1
        L_0x14b0:
            r0 = 0
        L_0x14b1:
            r53 = r0
            r54 = r1
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r50 = 0
            r52 = 0
            goto L_0x16d6
        L_0x14fd:
            java.lang.String r3 = "tg:confirmphone"
            java.lang.String r2 = r2.replace(r3, r0)
            java.lang.String r3 = "tg://confirmphone"
            java.lang.String r0 = r2.replace(r3, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r1 = r0
            r0 = r2
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
        L_0x152d:
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            goto L_0x166f
        L_0x1533:
            java.lang.String r1 = "tg:msg"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://msg"
            java.lang.String r1 = r1.replace(r2, r0)
            java.lang.String r2 = "tg://share"
            java.lang.String r1 = r1.replace(r2, r0)
            java.lang.String r2 = "tg:share"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x1558
            goto L_0x1559
        L_0x1558:
            r3 = r1
        L_0x1559:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x158f
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x1578
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r4)
            java.lang.String r3 = r1.toString()
            r1 = 1
            goto L_0x1579
        L_0x1578:
            r1 = 0
        L_0x1579:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r2.append(r0)
            java.lang.String r3 = r2.toString()
            goto L_0x1590
        L_0x158f:
            r1 = 0
        L_0x1590:
            int r0 = r3.length()
            r2 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r2) goto L_0x15a0
            r0 = 16384(0x4000, float:2.2959E-41)
            r2 = 0
            java.lang.String r0 = r3.substring(r2, r0)
            goto L_0x15a2
        L_0x15a0:
            r2 = 0
            r0 = r3
        L_0x15a2:
            boolean r3 = r0.endsWith(r4)
            if (r3 == 0) goto L_0x15b3
            int r3 = r0.length()
            r7 = 1
            int r3 = r3 - r7
            java.lang.String r0 = r0.substring(r2, r3)
            goto L_0x15a2
        L_0x15b3:
            r2 = r0
            r13 = r1
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            goto L_0x1671
        L_0x15d3:
            java.lang.String r1 = "tg:addemoji"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://addemoji"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r13 = 0
            r19 = 0
            r24 = 0
            r25 = 0
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
            r50 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 0
            r60 = -1
            r12 = r0
        L_0x163a:
            r0 = 0
            goto L_0x1a86
        L_0x163d:
            java.lang.String r1 = "tg:addstickers"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://addstickers"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r9 = r0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
        L_0x166f:
            r12 = 0
            r13 = 0
        L_0x1671:
            r19 = 0
            r24 = 0
            goto L_0x16ae
        L_0x1676:
            java.lang.String r1 = "tg:join"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://join"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r24 = r0
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 6
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
        L_0x16ae:
            r25 = 0
            r34 = 0
            r35 = 0
            r36 = 0
        L_0x16b6:
            r37 = 0
        L_0x16b8:
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x16c4:
            r44 = 0
            r45 = 0
            r46 = 0
        L_0x16ca:
            r47 = 0
        L_0x16cc:
            r48 = 0
            r50 = 0
            r52 = 0
        L_0x16d2:
            r53 = 0
            r54 = 0
        L_0x16d6:
            r55 = 0
            r56 = 0
            r57 = 0
            goto L_0x1a84
        L_0x16de:
            java.lang.String r1 = "tg:bg"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://bg"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r2 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r2.<init>()
            r1.settings = r2
            java.lang.String r2 = "slug"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
            if (r2 != 0) goto L_0x170c
            java.lang.String r2 = "color"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r1.slug = r2
        L_0x170c:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x172c
            int r2 = r2.length()
            r3 = 6
            if (r2 != r3) goto L_0x172c
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x17e4 }
            r3 = r3 | r23
            r2.background_color = r3     // Catch:{ Exception -> 0x17e4 }
            r2 = 0
            r1.slug = r2     // Catch:{ Exception -> 0x17e4 }
            r2 = 1
            r7 = 0
            goto L_0x17e6
        L_0x172c:
            java.lang.String r2 = r1.slug
            if (r2 == 0) goto L_0x17e4
            int r2 = r2.length()
            r3 = 13
            if (r2 < r3) goto L_0x17e4
            java.lang.String r2 = r1.slug
            r3 = 6
            char r2 = r2.charAt(r3)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)
            if (r2 == 0) goto L_0x17e4
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r7 = 0
            java.lang.String r4 = r4.substring(r7, r3)     // Catch:{ Exception -> 0x17e4 }
            r3 = 16
            int r4 = java.lang.Integer.parseInt(r4, r3)     // Catch:{ Exception -> 0x17e4 }
            r3 = r4 | r23
            r2.background_color = r3     // Catch:{ Exception -> 0x17e4 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r4 = 7
            r7 = 13
            java.lang.String r3 = r3.substring(r4, r7)     // Catch:{ Exception -> 0x17e4 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x17e4 }
            r3 = r3 | r23
            r2.second_background_color = r3     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x17e4 }
            r3 = 20
            if (r2 < r3) goto L_0x179b
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r3 = 13
            char r2 = r2.charAt(r3)     // Catch:{ Exception -> 0x17e4 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x17e4 }
            if (r2 == 0) goto L_0x179b
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r4 = 14
            r7 = 20
            java.lang.String r3 = r3.substring(r4, r7)     // Catch:{ Exception -> 0x17e4 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x17e4 }
            r3 = r3 | r23
            r2.third_background_color = r3     // Catch:{ Exception -> 0x17e4 }
        L_0x179b:
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x17e4 }
            r3 = 27
            if (r2 != r3) goto L_0x17c7
            java.lang.String r2 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r3 = 20
            char r2 = r2.charAt(r3)     // Catch:{ Exception -> 0x17e4 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x17e4 }
            if (r2 == 0) goto L_0x17c7
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x17e4 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x17e4 }
            r4 = 21
            java.lang.String r3 = r3.substring(r4)     // Catch:{ Exception -> 0x17e4 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x17e4 }
            r3 = r3 | r23
            r2.fourth_background_color = r3     // Catch:{ Exception -> 0x17e4 }
        L_0x17c7:
            java.lang.String r2 = "rotation"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x17df }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x17df }
            if (r3 != 0) goto L_0x17df
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x17df }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ Exception -> 0x17df }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x17df }
            r3.rotation = r2     // Catch:{ Exception -> 0x17df }
        L_0x17df:
            r7 = 0
            r1.slug = r7     // Catch:{ Exception -> 0x17e5 }
            r2 = 1
            goto L_0x17e6
        L_0x17e4:
            r7 = 0
        L_0x17e5:
            r2 = 0
        L_0x17e6:
            if (r2 != 0) goto L_0x18d6
            java.lang.String r2 = "mode"
            java.lang.String r2 = r0.getQueryParameter(r2)
            if (r2 == 0) goto L_0x1825
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)
            if (r2 == 0) goto L_0x1825
            int r3 = r2.length
            if (r3 <= 0) goto L_0x1825
            r3 = 0
        L_0x1800:
            int r4 = r2.length
            if (r3 >= r4) goto L_0x1825
            r4 = r2[r3]
            java.lang.String r9 = "blur"
            boolean r4 = r9.equals(r4)
            if (r4 == 0) goto L_0x1813
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            r9 = 1
            r4.blur = r9
            goto L_0x1822
        L_0x1813:
            r9 = 1
            r4 = r2[r3]
            java.lang.String r10 = "motion"
            boolean r4 = r10.equals(r4)
            if (r4 == 0) goto L_0x1822
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            r4.motion = r9
        L_0x1822:
            int r3 = r3 + 1
            goto L_0x1800
        L_0x1825:
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3)
            int r3 = r3.intValue()
            r2.intensity = r3
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x18bc }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x18bc }
            if (r3 != 0) goto L_0x18bc
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x18bc }
            r4 = 6
            r9 = 0
            java.lang.String r10 = r2.substring(r9, r4)     // Catch:{ Exception -> 0x18bd }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x18bd }
            r9 = r10 | r23
            r3.background_color = r9     // Catch:{ Exception -> 0x18bd }
            int r3 = r2.length()     // Catch:{ Exception -> 0x18bd }
            r9 = 13
            if (r3 < r9) goto L_0x18bd
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x18bd }
            r10 = 8
            java.lang.String r10 = r2.substring(r10, r9)     // Catch:{ Exception -> 0x18bd }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x18bd }
            r9 = r10 | r23
            r3.second_background_color = r9     // Catch:{ Exception -> 0x18bd }
            int r3 = r2.length()     // Catch:{ Exception -> 0x18bd }
            r9 = 20
            if (r3 < r9) goto L_0x1895
            r3 = 13
            char r3 = r2.charAt(r3)     // Catch:{ Exception -> 0x18bd }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x18bd }
            if (r3 == 0) goto L_0x1895
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x18bd }
            r10 = 14
            java.lang.String r10 = r2.substring(r10, r9)     // Catch:{ Exception -> 0x18bd }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x18bd }
            r9 = r10 | r23
            r3.third_background_color = r9     // Catch:{ Exception -> 0x18bd }
        L_0x1895:
            int r3 = r2.length()     // Catch:{ Exception -> 0x18bd }
            r9 = 27
            if (r3 != r9) goto L_0x18bd
            r3 = 20
            char r3 = r2.charAt(r3)     // Catch:{ Exception -> 0x18bd }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x18bd }
            if (r3 == 0) goto L_0x18bd
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x18bd }
            r9 = 21
            java.lang.String r2 = r2.substring(r9)     // Catch:{ Exception -> 0x18bd }
            r9 = 16
            int r2 = java.lang.Integer.parseInt(r2, r9)     // Catch:{ Exception -> 0x18bd }
            r2 = r2 | r23
            r3.fourth_background_color = r2     // Catch:{ Exception -> 0x18bd }
            goto L_0x18bd
        L_0x18bc:
            r4 = 6
        L_0x18bd:
            java.lang.String r2 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x18d7 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x18d7 }
            if (r2 != 0) goto L_0x18d7
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x18d7 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x18d7 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x18d7 }
            r2.rotation = r0     // Catch:{ Exception -> 0x18d7 }
            goto L_0x18d7
        L_0x18d6:
            r4 = 6
        L_0x18d7:
            r55 = r1
            r0 = r7
            r1 = r0
            r2 = r1
            r9 = r2
            r24 = r9
            r25 = r24
            r44 = r25
            r45 = r44
            r46 = r45
            r47 = r46
            r52 = r47
            r53 = r52
            r54 = r53
            r56 = r54
            r57 = r56
            r23 = r12
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
            r31 = r41
            r3 = r42
            r13 = 0
            r19 = 0
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
            goto L_0x1a23
        L_0x1924:
            r4 = 6
            r7 = 0
            java.lang.String r1 = "tg:privatepost"
            java.lang.String r1 = r2.replace(r1, r0)
            java.lang.String r2 = "tg://privatepost"
            java.lang.String r0 = r1.replace(r2, r0)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            java.lang.String r2 = "channel"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)
            int r3 = r1.intValue()
            if (r3 == 0) goto L_0x195b
            long r23 = r2.longValue()
            r19 = 0
            int r3 = (r23 > r19 ? 1 : (r23 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x195f
            goto L_0x195d
        L_0x195b:
            r19 = 0
        L_0x195d:
            r1 = r7
            r2 = r1
        L_0x195f:
            java.lang.String r3 = r0.getQueryParameter(r9)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3)
            int r9 = r3.intValue()
            if (r9 != 0) goto L_0x196e
            r3 = r7
        L_0x196e:
            java.lang.String r9 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x198f
            r18 = r1
            r25 = r2
            r26 = r3
            r0 = r7
            r1 = r0
            r2 = r1
            r9 = r2
            r24 = r9
            r27 = r24
            r44 = r27
            goto L_0x199f
        L_0x198f:
            r27 = r0
            r18 = r1
            r25 = r2
            r26 = r3
            r0 = r7
            r1 = r0
            r2 = r1
            r9 = r2
            r24 = r9
            r44 = r24
        L_0x199f:
            r45 = r44
            r46 = r45
            r47 = r46
            r52 = r47
            r53 = r52
            r54 = r53
            r55 = r54
            r56 = r55
            r57 = r56
            r23 = r12
            r48 = r19
            r50 = r48
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            goto L_0x1a0c
        L_0x19c4:
            r4 = 6
            r7 = 0
            r19 = 0
            java.lang.String r0 = "tg:invoice"
            java.lang.String r1 = "tg://invoice"
            java.lang.String r0 = r2.replace(r0, r1)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r56 = r0
            r0 = r7
            r1 = r0
            r2 = r1
            r9 = r2
            r24 = r9
            r25 = r24
            r44 = r25
            r45 = r44
            r46 = r45
            r47 = r46
            r52 = r47
            r53 = r52
            r54 = r53
            r55 = r54
            r57 = r55
            r23 = r12
            r48 = r19
            r50 = r48
            r10 = r31
            r58 = r34
            r59 = r35
            r61 = r36
            r63 = r37
            r62 = r38
            r26 = r39
            r18 = r40
        L_0x1a0c:
            r31 = r41
            r3 = r42
            r13 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x1a23:
            r60 = -1
            r12 = r57
            r34 = r12
            goto L_0x1a86
        L_0x1a2b:
            r28 = r2
            r29 = r11
            r4 = 6
            r7 = 0
            r19 = 0
            r0 = r7
            r1 = r0
            r2 = r1
            r3 = r2
            r9 = r3
            r10 = r9
            r11 = r10
            r12 = r11
            r18 = r12
            r23 = r18
            r24 = r23
            r25 = r24
            r26 = r25
            r27 = r26
            r30 = r27
            r31 = r30
            r32 = r31
            r34 = r32
            r44 = r34
            r45 = r44
            r46 = r45
            r47 = r46
            r52 = r47
            r53 = r52
            r54 = r53
            r55 = r54
            r56 = r55
            r57 = r56
            r58 = r57
            r59 = r58
            r61 = r59
            r62 = r61
            r63 = r62
            r48 = r19
            r50 = r48
            r13 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x1a84:
            r60 = -1
        L_0x1a86:
            boolean r64 = r14.hasExtra(r5)
            if (r64 == 0) goto L_0x1ad0
            int r4 = r15.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            boolean r4 = r4.isClientActivated()
            if (r4 == 0) goto L_0x1aa6
            java.lang.String r4 = "tg"
            r7 = r28
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x1aa6
            if (r52 != 0) goto L_0x1aa6
            r4 = 1
            goto L_0x1aa7
        L_0x1aa6:
            r4 = 0
        L_0x1aa7:
            com.google.firebase.appindexing.builders.AssistActionBuilder r7 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r7.<init>()
            r65 = r8
            java.lang.String r8 = r14.getStringExtra(r5)
            com.google.firebase.appindexing.builders.AssistActionBuilder r7 = r7.setActionToken(r8)
            if (r4 == 0) goto L_0x1abb
            java.lang.String r4 = "http://schema.org/CompletedActionStatus"
            goto L_0x1abd
        L_0x1abb:
            java.lang.String r4 = "http://schema.org/FailedActionStatus"
        L_0x1abd:
            com.google.firebase.appindexing.Action$Builder r4 = r7.setActionStatus(r4)
            com.google.firebase.appindexing.Action r4 = r4.build()
            com.google.firebase.appindexing.FirebaseUserActions r7 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r81)
            r7.end(r4)
            r14.removeExtra(r5)
            goto L_0x1ad2
        L_0x1ad0:
            r65 = r8
        L_0x1ad2:
            if (r53 != 0) goto L_0x1aed
            int r4 = r15.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            boolean r4 = r4.isClientActivated()
            if (r4 == 0) goto L_0x1ae1
            goto L_0x1aed
        L_0x1ae1:
            r7 = r15
            r75 = r29
            r74 = r65
            r1 = 2
            r8 = 3
            r10 = 0
            r64 = -1
            goto L_0x1cb3
        L_0x1aed:
            if (r0 != 0) goto L_0x1CLASSNAME
            if (r1 == 0) goto L_0x1af3
            goto L_0x1CLASSNAME
        L_0x1af3:
            if (r3 != 0) goto L_0x1bd5
            if (r24 != 0) goto L_0x1bd5
            if (r9 != 0) goto L_0x1bd5
            if (r12 != 0) goto L_0x1bd5
            if (r2 != 0) goto L_0x1bd5
            if (r32 != 0) goto L_0x1bd5
            if (r58 != 0) goto L_0x1bd5
            if (r31 != 0) goto L_0x1bd5
            if (r52 != 0) goto L_0x1bd5
            if (r47 != 0) goto L_0x1bd5
            if (r53 != 0) goto L_0x1bd5
            if (r55 != 0) goto L_0x1bd5
            if (r56 != 0) goto L_0x1bd5
            if (r25 != 0) goto L_0x1bd5
            if (r57 != 0) goto L_0x1bd5
            if (r54 == 0) goto L_0x1b15
            goto L_0x1bd5
        L_0x1b15:
            android.content.ContentResolver r66 = r81.getContentResolver()     // Catch:{ Exception -> 0x1bbe }
            android.net.Uri r67 = r82.getData()     // Catch:{ Exception -> 0x1bbe }
            r68 = 0
            r69 = 0
            r70 = 0
            r71 = 0
            android.database.Cursor r1 = r66.query(r67, r68, r69, r70, r71)     // Catch:{ Exception -> 0x1bbe }
            if (r1 == 0) goto L_0x1bae
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1ba6 }
            if (r0 == 0) goto L_0x1bae
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1ba6 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1ba6 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ all -> 0x1ba6 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1ba6 }
            r2 = 0
        L_0x1b44:
            r3 = 4
            if (r2 >= r3) goto L_0x1b62
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1ba6 }
            long r3 = r3.getClientUserId()     // Catch:{ all -> 0x1ba6 }
            long r5 = (long) r0     // Catch:{ all -> 0x1ba6 }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x1b5e
            r3 = 0
            r29[r3] = r2     // Catch:{ all -> 0x1ba6 }
            r0 = r29[r3]     // Catch:{ all -> 0x1ba6 }
            r8 = 1
            r15.switchToAccount(r0, r8)     // Catch:{ all -> 0x1ba4 }
            goto L_0x1b63
        L_0x1b5e:
            r8 = 1
            int r2 = r2 + 1
            goto L_0x1b44
        L_0x1b62:
            r8 = 1
        L_0x1b63:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1ba4 }
            long r2 = r1.getLong(r0)     // Catch:{ all -> 0x1ba4 }
            r4 = 0
            r0 = r29[r4]     // Catch:{ all -> 0x1ba4 }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x1ba4 }
            int r5 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x1ba4 }
            java.lang.Object[] r6 = new java.lang.Object[r4]     // Catch:{ all -> 0x1ba4 }
            r0.postNotificationName(r5, r6)     // Catch:{ all -> 0x1ba4 }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1ba0 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1ba0 }
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r4 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x1ba0 }
            if (r4 == 0) goto L_0x1b91
            r48 = r2
            r4 = 1
            goto L_0x1bb1
        L_0x1b91:
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x1ba0 }
            r48 = r2
            r4 = r38
            if (r0 == 0) goto L_0x1bb1
            r39 = 1
            goto L_0x1bb1
        L_0x1ba0:
            r0 = move-exception
            r48 = r2
            goto L_0x1ba8
        L_0x1ba4:
            r0 = move-exception
            goto L_0x1ba8
        L_0x1ba6:
            r0 = move-exception
            r8 = 1
        L_0x1ba8:
            r1.close()     // Catch:{ all -> 0x1bab }
        L_0x1bab:
            throw r0     // Catch:{ Exception -> 0x1bac }
        L_0x1bac:
            r0 = move-exception
            goto L_0x1bc0
        L_0x1bae:
            r8 = 1
            r4 = r38
        L_0x1bb1:
            if (r1 == 0) goto L_0x1bbb
            r1.close()     // Catch:{ Exception -> 0x1bb7 }
            goto L_0x1bbb
        L_0x1bb7:
            r0 = move-exception
            r38 = r4
            goto L_0x1bc0
        L_0x1bbb:
            r38 = r4
            goto L_0x1bc3
        L_0x1bbe:
            r0 = move-exception
            r8 = 1
        L_0x1bc0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1bc3:
            r7 = r15
            r75 = r29
            r12 = r35
            r13 = r36
            r5 = r48
            r74 = r65
            r1 = 2
            r8 = 3
            r10 = 0
            r64 = -1
            goto L_0x1ce0
        L_0x1bd5:
            r8 = 1
            if (r2 == 0) goto L_0x1bf2
            java.lang.String r0 = "@"
            boolean r0 = r2.startsWith(r0)
            if (r0 == 0) goto L_0x1bf2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = " "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            goto L_0x1bf3
        L_0x1bf2:
            r0 = r2
        L_0x1bf3:
            r21 = 0
            r2 = r29[r21]
            r28 = 0
            r1 = r81
            r7 = 6
            r64 = -1
            r4 = r24
            r6 = 2
            r5 = r9
            r9 = 3
            r6 = r12
            r12 = 0
            r7 = r30
            r74 = r65
            r16 = 1
            r8 = r11
            r11 = 3
            r9 = r23
            r75 = r29
            r11 = r0
            r12 = r13
            r13 = r18
            r14 = r25
            r15 = r26
            r16 = r27
            r17 = r32
            r18 = r31
            r19 = r47
            r20 = r52
            r21 = r53
            r22 = r54
            r23 = r55
            r24 = r56
            r25 = r57
            r26 = r58
            r27 = r59
            r29 = r60
            r30 = r61
            r31 = r62
            r32 = r63
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = 2
            r8 = 3
            r10 = 0
            r7 = r81
            goto L_0x1cb3
        L_0x1CLASSNAME:
            r75 = r29
            r74 = r65
            r64 = -1
            org.telegram.ui.ActionBar.AlertDialog r3 = new org.telegram.ui.ActionBar.AlertDialog
            r8 = 3
            r7 = r81
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
            if (r2 == 0) goto L_0x1c8a
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = org.telegram.messenger.BuildVars.SMS_HASH
            java.lang.String r4 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.putString(r4, r2)
            r1.apply()
            goto L_0x1CLASSNAME
        L_0x1c8a:
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            r1.apply()
        L_0x1CLASSNAME:
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            r5.putString(r6, r0)
            int r1 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82
            r1 = r12
            r2 = r81
            r4 = r0
            r6 = r9
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 2
            r11.sendRequest(r9, r12, r1)
        L_0x1cb3:
            r12 = r35
            r13 = r36
            r5 = r48
            goto L_0x1ce0
        L_0x1cba:
            r74 = r8
            r75 = r11
            r7 = r15
            r1 = 2
            r8 = 3
            r10 = 0
            r64 = -1
            r5 = 0
            r12 = 0
            r13 = 0
            r34 = 0
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
            r50 = 0
        L_0x1ce0:
            r11 = r82
            r10 = r12
            r79 = r13
            r15 = r34
            r1 = r39
            r22 = r40
            r76 = r45
            r77 = r46
            r8 = r50
            r14 = r75
            r0 = -1
            r4 = -1
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r72 = 0
            r78 = 0
            r12 = r5
            r5 = r44
            r6 = r74
            goto L_0x1ec9
        L_0x1d0a:
            r74 = r8
            r75 = r11
            r7 = r15
            r1 = 2
            r8 = 3
            r10 = 0
            r64 = -1
            java.lang.String r0 = r82.getAction()
            java.lang.String r2 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x1d51
            r11 = r82
            r6 = r74
            r14 = r75
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
            r22 = 0
            r37 = 0
            r38 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r72 = 0
            r76 = 0
            r77 = 0
            r78 = 0
            r79 = 1
            goto L_0x1ec9
        L_0x1d51:
            java.lang.String r0 = r82.getAction()
            java.lang.String r2 = "new_dialog"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x1d86
            r11 = r82
            r6 = r74
            r14 = r75
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
            r22 = 0
            r37 = 0
            r38 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r72 = 0
            goto L_0x1ec1
        L_0x1d86:
            java.lang.String r0 = r82.getAction()
            java.lang.String r2 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x1e3b
            java.lang.String r0 = "chatId"
            r11 = r82
            r2 = 0
            long r4 = r11.getLongExtra(r0, r2)
            java.lang.String r0 = "userId"
            long r12 = r11.getLongExtra(r0, r2)
            java.lang.String r0 = "encId"
            int r0 = r11.getIntExtra(r0, r10)
            java.lang.String r6 = "appWidgetId"
            int r6 = r11.getIntExtra(r6, r10)
            if (r6 == 0) goto L_0x1dc4
            java.lang.String r0 = "appWidgetType"
            int r4 = r11.getIntExtra(r0, r10)
            r12 = r2
            r15 = r12
            r64 = r6
            r6 = r74
            r14 = r75
            r0 = 0
            r5 = 0
            r9 = 0
            r17 = 6
            goto L_0x1e12
        L_0x1dc4:
            r6 = r74
            int r9 = r11.getIntExtra(r6, r10)
            int r14 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r14 == 0) goto L_0x1de5
            r14 = r75
            r0 = r14[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r12 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r13 = new java.lang.Object[r10]
            r0.postNotificationName(r12, r13)
            r12 = r2
            r15 = r4
        L_0x1ddf:
            r0 = 0
        L_0x1de0:
            r4 = -1
            r5 = 0
        L_0x1de2:
            r17 = 0
            goto L_0x1e12
        L_0x1de5:
            r14 = r75
            int r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x1dfa
            r0 = r14[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r10]
            r0.postNotificationName(r4, r5)
            r15 = r2
            goto L_0x1ddf
        L_0x1dfa:
            if (r0 == 0) goto L_0x1e0c
            r4 = r14[r10]
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r12 = new java.lang.Object[r10]
            r4.postNotificationName(r5, r12)
            r12 = r2
            r15 = r12
            goto L_0x1de0
        L_0x1e0c:
            r12 = r2
            r15 = r12
            r0 = 0
            r4 = -1
            r5 = 1
            goto L_0x1de2
        L_0x1e12:
            r78 = r0
            r72 = r2
            r10 = r9
            r8 = r15
            r79 = r17
            r0 = r64
            r1 = 0
            r15 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r37 = 0
            r38 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r76 = 0
            r77 = 0
            r17 = r5
            r5 = 0
            goto L_0x1ec9
        L_0x1e3b:
            r11 = r82
            r6 = r74
            r14 = r75
            r2 = 0
            java.lang.String r0 = r82.getAction()
            java.lang.String r4 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1e5d
            r8 = r2
            r12 = r8
            r72 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 1
            goto L_0x1eaf
        L_0x1e5d:
            java.lang.String r0 = r82.getAction()
            java.lang.String r4 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x1e79
            r8 = r2
            r12 = r8
            r72 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 1
            goto L_0x1eb1
        L_0x1e79:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x1ea2
            r8 = r2
            r12 = r8
            r72 = r12
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
            goto L_0x1eb5
        L_0x1e95:
            r2 = r5
            r6 = r8
            r7 = r15
            r1 = 2
        L_0x1e99:
            r8 = 3
            r10 = 0
            r64 = -1
            r80 = r14
            r14 = r11
            r11 = r80
        L_0x1ea2:
            r8 = r2
            r12 = r8
            r72 = r12
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r15 = 0
            r17 = 0
            r18 = 0
        L_0x1eaf:
            r19 = 0
        L_0x1eb1:
            r20 = 0
            r21 = 0
        L_0x1eb5:
            r22 = 0
            r37 = 0
            r38 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x1ec1:
            r76 = 0
            r77 = 0
            r78 = 0
            r79 = 0
        L_0x1ec9:
            int r2 = r7.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x2397
            if (r15 == 0) goto L_0x1efd
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.getLastFragment()
            boolean r3 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x1ef9
            org.telegram.ui.DialogsActivity r2 = (org.telegram.ui.DialogsActivity) r2
            boolean r3 = r2.isMainDialogList()
            if (r3 == 0) goto L_0x1efd
            android.view.View r3 = r2.getFragmentView()
            if (r3 == 0) goto L_0x1ef4
            r3 = 1
            r2.search(r15, r3)
            goto L_0x1efe
        L_0x1ef4:
            r3 = 1
            r2.setInitialSearchString(r15)
            goto L_0x1efe
        L_0x1ef9:
            r3 = 1
            r17 = 1
            goto L_0x1efe
        L_0x1efd:
            r3 = 1
        L_0x1efe:
            r23 = 0
            int r2 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1))
            if (r2 == 0) goto L_0x1var_
            if (r38 != 0) goto L_0x1f5a
            if (r1 == 0) goto L_0x1var_
            goto L_0x1f5a
        L_0x1var_:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putLong(r1, r12)
            if (r10 == 0) goto L_0x1var_
            r0.putInt(r6, r10)
        L_0x1var_:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1f3a
            r1 = 0
            r2 = r14[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x2005
        L_0x1f3a:
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r0
            r23 = r1
            boolean r0 = r22.presentFragment(r23, r24, r25, r26, r27)
            if (r0 == 0) goto L_0x2005
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x2003
        L_0x1f5a:
            if (r22 == 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x2397
            org.telegram.messenger.MessagesController r2 = r0.getMessagesController()
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r2, r1)
            goto L_0x2397
        L_0x1var_:
            r2 = 0
            r0 = r14[r2]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r12, r1, r0)
            goto L_0x2397
        L_0x1var_:
            r12 = 0
            int r2 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x1fd7
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r8)
            if (r10 == 0) goto L_0x1var_
            r0.putInt(r6, r10)
        L_0x1var_:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1fb8
            r1 = 0
            r2 = r14[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x2005
        L_0x1fb8:
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r0
            r23 = r1
            boolean r0 = r22.presentFragment(r23, r24, r25, r26, r27)
            if (r0 == 0) goto L_0x2005
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x2003
        L_0x1fd7:
            r10 = r78
            if (r10 == 0) goto L_0x200c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enc_id"
            r0.putInt(r1, r10)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r0
            r23 = r1
            boolean r0 = r22.presentFragment(r23, r24, r25, r26, r27)
            if (r0 == 0) goto L_0x2005
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
        L_0x2003:
            r13 = 1
            goto L_0x2006
        L_0x2005:
            r13 = 0
        L_0x2006:
            r2 = r83
            r8 = 0
            r9 = 1
            goto L_0x239c
        L_0x200c:
            if (r17 == 0) goto L_0x2048
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x201a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.removeAllFragments()
            goto L_0x2045
        L_0x201a:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x2045
        L_0x2024:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r3
            if (r0 <= 0) goto L_0x203e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x2024
        L_0x203e:
            r2 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.closeLastFragment(r2)
            goto L_0x2046
        L_0x2045:
            r2 = 0
        L_0x2046:
            r8 = 0
            goto L_0x206c
        L_0x2048:
            r2 = 0
            if (r18 == 0) goto L_0x206f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x2069
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r8 = 0
            r1.<init>(r7, r8)
            r0.showDialog(r1)
            goto L_0x206a
        L_0x2069:
            r8 = 0
        L_0x206a:
            r2 = r83
        L_0x206c:
            r9 = 1
            goto L_0x239b
        L_0x206f:
            r8 = 0
            if (r19 == 0) goto L_0x2095
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x206a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda96 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda96
            r2.<init>(r7, r14)
            r1.<init>(r7, r2, r8)
            r0.showDialog(r1)
            goto L_0x206a
        L_0x2095:
            android.net.Uri r2 = r7.exportingChatUri
            if (r2 == 0) goto L_0x20a0
            java.util.ArrayList<android.net.Uri> r0 = r7.documentsUrisArray
            r7.runImportRequest(r2, r0)
            goto L_0x2398
        L_0x20a0:
            java.util.ArrayList<android.os.Parcelable> r2 = r7.importingStickers
            if (r2 == 0) goto L_0x20ad
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26
            r0.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x206a
        L_0x20ad:
            java.lang.String r2 = r7.videoPath
            if (r2 != 0) goto L_0x2365
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r7.photoPathsArray
            if (r2 != 0) goto L_0x2365
            java.lang.String r2 = r7.sendingText
            if (r2 != 0) goto L_0x2365
            java.util.ArrayList<java.lang.String> r2 = r7.documentsPathsArray
            if (r2 != 0) goto L_0x2365
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.contactsToSend
            if (r2 != 0) goto L_0x2365
            java.util.ArrayList<android.net.Uri> r2 = r7.documentsUrisArray
            if (r2 == 0) goto L_0x20c7
            goto L_0x2365
        L_0x20c7:
            r10 = r79
            if (r10 == 0) goto L_0x2157
            if (r10 != r3) goto L_0x20e8
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
        L_0x20e5:
            r1 = 6
        L_0x20e6:
            r13 = 0
            goto L_0x211d
        L_0x20e8:
            r1 = 2
            if (r10 != r1) goto L_0x20f2
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r0.<init>(r1)
            goto L_0x20e5
        L_0x20f2:
            r1 = 0
            r2 = 3
            if (r10 != r2) goto L_0x20fc
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r1)
            goto L_0x20e5
        L_0x20fc:
            r1 = 4
            if (r10 != r1) goto L_0x2105
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x20e5
        L_0x2105:
            r1 = 5
            if (r10 != r1) goto L_0x2111
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 3
            r0.<init>(r1)
            r1 = 6
            r13 = 1
            goto L_0x211d
        L_0x2111:
            r1 = 6
            if (r10 != r1) goto L_0x211b
            org.telegram.ui.EditWidgetActivity r2 = new org.telegram.ui.EditWidgetActivity
            r2.<init>(r4, r0)
            r0 = r2
            goto L_0x20e6
        L_0x211b:
            r0 = r8
            goto L_0x20e6
        L_0x211d:
            if (r10 != r1) goto L_0x2131
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r1
            r23 = r0
            r22.presentFragment(r23, r24, r25, r26, r27)
            goto L_0x2139
        L_0x2131:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60
            r1.<init>(r7, r0, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x2139:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2150
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2196
        L_0x2150:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x2196
        L_0x2157:
            if (r20 == 0) goto L_0x219b
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r3)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ContactsActivity r2 = new org.telegram.ui.ContactsActivity
            r2.<init>(r0)
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r1
            r23 = r2
            r22.presentFragment(r23, r24, r25, r26, r27)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2190
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2196
        L_0x2190:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
        L_0x2196:
            r2 = r83
            r9 = 1
            goto L_0x2385
        L_0x219b:
            if (r5 == 0) goto L_0x21fc
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97
            r0.<init>(r7, r1, r14)
            r2.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.getLastFragment()
            boolean r1 = r1 instanceof org.telegram.ui.ContactsActivity
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r0
            r23 = r2
            r24 = r1
            r22.presentFragment(r23, r24, r25, r26, r27)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x21f5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2196
        L_0x21f5:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x2196
        L_0x21fc:
            if (r43 == 0) goto L_0x223d
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 5
            r0.<init>(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda90 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda90
            r1.<init>(r7, r0)
            r0.setQrLoginDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r1
            r23 = r0
            r22.presentFragment(r23, r24, r25, r26, r27)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2235
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2196
        L_0x2235:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x2196
        L_0x223d:
            r1 = 0
            if (r41 == 0) goto L_0x2298
            org.telegram.ui.NewContactActivity r0 = new org.telegram.ui.NewContactActivity
            r0.<init>()
            r2 = r76
            if (r2 == 0) goto L_0x225c
            java.lang.String r4 = " "
            r5 = 2
            java.lang.String[] r2 = r2.split(r4, r5)
            r4 = r2[r1]
            int r5 = r2.length
            if (r5 <= r3) goto L_0x2258
            r2 = r2[r3]
            goto L_0x2259
        L_0x2258:
            r2 = r8
        L_0x2259:
            r0.setInitialName(r4, r2)
        L_0x225c:
            r4 = r77
            if (r4 == 0) goto L_0x2267
            java.lang.String r2 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4, r3)
            r0.setInitialPhoneNumber(r2, r1)
        L_0x2267:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r22 = r1
            r23 = r0
            r22.presentFragment(r23, r24, r25, r26, r27)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2290
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2196
        L_0x2290:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r3, r1)
            goto L_0x2196
        L_0x2298:
            r2 = r76
            r4 = r77
            if (r21 == 0) goto L_0x22b7
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r0 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r81
            r9 = 1
            r3 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x2399
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r9
            goto L_0x2399
        L_0x22b7:
            r9 = 1
            if (r42 == 0) goto L_0x2337
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x2333
            android.app.Activity r1 = r0.getParentActivity()
            if (r1 == 0) goto L_0x2333
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
            goto L_0x2334
        L_0x2333:
            r13 = 0
        L_0x2334:
            r2 = r83
            goto L_0x239c
        L_0x2337:
            if (r37 == 0) goto L_0x2399
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r1 = new org.telegram.ui.CallLogActivity
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 0
            r0.presentFragment(r1, r2, r3, r4, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x235e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2383
        L_0x235e:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r9, r1)
            goto L_0x2383
        L_0x2365:
            r1 = 0
            r9 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x237a
            r0 = r14[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r3)
        L_0x237a:
            r2 = 0
            int r0 = (r72 > r2 ? 1 : (r72 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x2387
            r7.openDialogsToSend(r1)
        L_0x2383:
            r2 = r83
        L_0x2385:
            r13 = 1
            goto L_0x239c
        L_0x2387:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r72)
            r0.add(r2)
            r7.didSelectDialogs(r8, r0, r8, r1)
            goto L_0x2399
        L_0x2397:
            r8 = 0
        L_0x2398:
            r9 = 1
        L_0x2399:
            r2 = r83
        L_0x239b:
            r13 = 0
        L_0x239c:
            if (r13 != 0) goto L_0x2446
            if (r2 != 0) goto L_0x2446
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x23f1
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x23cc
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2431
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r81.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2431
        L_0x23cc:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2431
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r8)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r15 == 0) goto L_0x23e5
            r0.setInitialSearchString(r15)
        L_0x23e5:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r9, r1)
            goto L_0x2431
        L_0x23f1:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2431
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x2417
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r81.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x2431
        L_0x2417:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r8)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r15 == 0) goto L_0x2426
            r0.setInitialSearchString(r15)
        L_0x2426:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r9, r1)
        L_0x2431:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2446
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
        L_0x2446:
            if (r33 == 0) goto L_0x244e
            r1 = 0
            r0 = r14[r1]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x244e:
            if (r21 != 0) goto L_0x2463
            java.lang.String r0 = r82.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x2463
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x2463
            r0.dismiss()
        L_0x2463:
            r11.setAction(r8)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$10(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$12(AlertDialog alertDialog, String str, Bundle bundle, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda58(this, alertDialog, tLRPC$TL_error, str, bundle, tLObject, tLRPC$TL_account_sendConfirmPhoneCode));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$11(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, String str, Bundle bundle, TLObject tLObject, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        alertDialog.dismiss();
        if (tLRPC$TL_error == null) {
            lambda$runLinkRequest$60(new LoginActivity().cancelAccountDeletion(str, bundle, (TLRPC$TL_auth_sentCode) tLObject));
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, getActionBarLayout().getLastFragment(), tLRPC$TL_account_sendConfirmPhoneCode, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$14(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda103(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$60(locationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$15() {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).showDialog(new StickersAlert((Context) this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$16(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$17(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$21(ActionIntroActivity actionIntroActivity, String str) {
        AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new LaunchActivity$$ExternalSyntheticLambda68(alertDialog, actionIntroActivity));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$19(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!(tLObject instanceof TLRPC$TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda24(actionIntroActivity, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$18(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(actionIntroActivity, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$22(String str, String str2, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
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
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new LaunchActivity$$ExternalSyntheticLambda70(this, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runCommentRequest$24(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0094 A[SYNTHETIC, Splitter:B:15:0x0094] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runCommentRequest$23(org.telegram.tgnet.TLObject r12, int r13, java.lang.Integer r14, org.telegram.tgnet.TLRPC$Chat r15, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r16, java.lang.Integer r17, java.lang.Integer r18, org.telegram.ui.ActionBar.AlertDialog r19) {
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
            r11.lambda$runLinkRequest$60(r2)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$23(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76
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
    public /* synthetic */ void lambda$runImportRequest$26(Uri uri, int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda44(this, tLObject, uri, i, alertDialog), 2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011b, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0137, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runImportRequest$25(org.telegram.tgnet.TLObject r10, android.net.Uri r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runImportRequest$25(org.telegram.tgnet.TLObject, android.net.Uri, int, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runImportRequest$27(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda101 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda101
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62     // Catch:{ Exception -> 0x03ae }
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83
            r5.<init>(r8, r10, r1)
            int r0 = r4.sendRequest(r0, r5)
            r3[r7] = r0
            goto L_0x04b3
        L_0x03d8:
            r1 = r58
            if (r1 == 0) goto L_0x0404
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda27 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda27
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86
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
    public /* synthetic */ void lambda$runLinkRequest$28(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, Integer num, Long l, Integer num2, Integer num3, String str10, HashMap hashMap, String str11, String str12, String str13, String str14, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str15, String str16, String str17, String str18, int i2, String str19, String str20, String str21, int i3) {
        int i4 = i3;
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i3, str, str2, str3, str4, str5, str6, str7, str8, str9, z, num, l, num2, num3, str10, hashMap, str11, str12, str13, str14, tLRPC$TL_wallPaper, str15, str16, str17, str18, 1, i2, str19, str20, str21);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$31(int i, String str, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda49(this, tLRPC$TL_error, tLObject, i, str, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$30(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i, String str, AlertDialog alertDialog) {
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
                    paymentFormActivity.setPaymentFormCallback(new LaunchActivity$$ExternalSyntheticLambda104(runnable));
                }
                lambda$runLinkRequest$60(paymentFormActivity);
            }
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$29(Runnable runnable, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$45(String str, String str2, String str3, int i, String str4, String str5, String str6, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str7, String str8, String str9, String str10, int i2, String str11, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity$$ExternalSyntheticLambda45 launchActivity$$ExternalSyntheticLambda45 = r0;
        LaunchActivity$$ExternalSyntheticLambda45 launchActivity$$ExternalSyntheticLambda452 = new LaunchActivity$$ExternalSyntheticLambda45(this, tLObject, tLRPC$TL_error, str, str2, str3, i, str4, str5, str6, num, num2, num3, iArr, alertDialog, str7, str8, str9, str10, i2, str11);
        AndroidUtilities.runOnUIThread(launchActivity$$ExternalSyntheticLambda45, 2);
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
    public /* synthetic */ void lambda$runLinkRequest$44(org.telegram.tgnet.TLObject r17, org.telegram.tgnet.TLRPC$TL_error r18, java.lang.String r19, java.lang.String r20, java.lang.String r21, int r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.Integer r26, java.lang.Integer r27, java.lang.Integer r28, int[] r29, org.telegram.ui.ActionBar.AlertDialog r30, java.lang.String r31, java.lang.String r32, java.lang.String r33, java.lang.String r34, int r35, java.lang.String r36) {
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98
            r23 = r2
            r24 = r16
            r25 = r22
            r26 = r1
            r27 = r33
            r28 = r0
            r29 = r3
            r23.<init>(r24, r25, r26, r27, r28, r29)
            r3.setDelegate(r2)
            r8.lambda$runLinkRequest$60(r3)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$44(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$37(int i, String str, TLRPC$User tLRPC$User, String str2, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, tLObject, i, str, tLRPC$User, str2, tLRPC$TL_contacts_resolvedPeer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$36(TLObject tLObject, int i, String str, TLRPC$User tLRPC$User, String str2, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer) {
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
                dialogsActivity2.setDelegate(new LaunchActivity$$ExternalSyntheticLambda100(this, tLRPC$User2, str3, i));
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
                lambda$runLinkRequest$60(dialogsActivity);
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
    public /* synthetic */ void lambda$runLinkRequest$32(TLRPC$User tLRPC$User, String str, int i, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    public /* synthetic */ void lambda$runLinkRequest$35(int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i2) {
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(tLRPC$TL_contacts_resolvedPeer.peer.user_id);
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = true;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new LaunchActivity$$ExternalSyntheticLambda75(this, i, dialogsActivity, baseFragment, tLRPC$User, str), 66);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$34(int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda43(this, tLObject, i, dialogsActivity, baseFragment, tLRPC$User, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$33(TLObject tLObject, int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
            if (dialogsActivity != null) {
                lambda$runLinkRequest$60(dialogsActivity);
            } else if (baseFragment instanceof ChatActivity) {
                ((ChatActivity) baseFragment).openAttachBotLayout(tLRPC$User.id, str);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$38(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    public /* synthetic */ void lambda$runLinkRequest$43(int i, TLRPC$User tLRPC$User, String str, String str2, DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
        MessagesController.getInstance(i).checkIsInChat(chat, tLRPC$User, new LaunchActivity$$ExternalSyntheticLambda66(this, str, str2, i, chat, dialogsActivity, tLRPC$User, longValue));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$41(String str, String str2, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, long j, boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str3) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda38(this, str, tLRPC$TL_chatAdminRights, z, str2, i, tLRPC$Chat, dialogsActivity, tLRPC$User, j, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$40(String str, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, boolean z, String str2, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, long j, String str3) {
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
    public /* synthetic */ void lambda$runLinkRequest$39(int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity) {
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putLong("chat_id", tLRPC$Chat.id);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            presentFragment(new ChatActivity(bundle), true, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$42(long j, int i, TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i2) {
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
    public /* synthetic */ void lambda$runLinkRequest$48(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda50(this, tLRPC$TL_error, tLObject, i, alertDialog, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r10.chat.has_geo != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0079, code lost:
        if (r11.checkCanOpenChat(r7, r14.get(r14.size() - 1)) != false) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$47(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$47(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$46(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$50(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda59(this, alertDialog, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$49(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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
    public /* synthetic */ void lambda$runLinkRequest$51(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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
    public /* synthetic */ void lambda$runLinkRequest$55(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda81(this, alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda57(this, alertDialog, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$53(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda53(this, alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$52(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$60(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$54(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$57(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda52(this, alertDialog, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$56(AlertDialog alertDialog, TLObject tLObject) {
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
    public /* synthetic */ void lambda$runLinkRequest$59(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda54(this, alertDialog, tLObject, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$58(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$62(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda55(this, alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$61(org.telegram.ui.ActionBar.AlertDialog r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC$TL_wallPaper r13, org.telegram.tgnet.TLRPC$TL_error r14) {
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
            r10.lambda$runLinkRequest$60(r11)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$61(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$63() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$65(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda46(this, tLObject, alertDialog, tLRPC$TL_error));
    }

    /* JADX WARNING: type inference failed for: r8v11, types: [org.telegram.tgnet.TLRPC$WallPaper] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009b A[SYNTHETIC, Splitter:B:29:0x009b] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$64(org.telegram.tgnet.TLObject r6, org.telegram.ui.ActionBar.AlertDialog r7, org.telegram.tgnet.TLRPC$TL_error r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$64(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$67(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda48(this, tLObject, iArr, i, alertDialog, num, num2, num3));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$66(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$66(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$70(Bundle bundle, Long l, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = l.longValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new LaunchActivity$$ExternalSyntheticLambda84(this, alertDialog, baseFragment, i, bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$69(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda56(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$68(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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
    public static /* synthetic */ void lambda$runLinkRequest$71(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda15(this));
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
    public /* synthetic */ void lambda$createUpdateUI$72(View view) {
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
    public static /* synthetic */ void lambda$updateAppUpdateViews$73(View view) {
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new LaunchActivity$$ExternalSyntheticLambda69(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$75(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda51(this, (TLRPC$TL_help_appUpdate) tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$74(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
    public /* synthetic */ void lambda$showAlertDialog$76(DialogInterface dialogInterface) {
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
            SendMessagesHelper.getInstance(currentAccount).prepareImportHistory(arrayList3.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new LaunchActivity$$ExternalSyntheticLambda67(this, currentAccount, dialogsActivity, z, arrayList4, uri, alertDialog));
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
                phonebookShareAlert.setDelegate(new LaunchActivity$$ExternalSyntheticLambda92(this, chatActivity, arrayList, currentAccount, charSequence, z6));
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
    public /* synthetic */ void lambda$didSelectDialogs$77(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
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
    public /* synthetic */ void lambda$didSelectDialogs$78(ChatActivity chatActivity, ArrayList arrayList, int i, CharSequence charSequence, boolean z, TLRPC$User tLRPC$User, boolean z2, int i2) {
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
    public void lambda$runLinkRequest$60(BaseFragment baseFragment) {
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
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda31(this), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$79() {
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
    public static /* synthetic */ void lambda$onPause$80(int i) {
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
        Utilities.stageQueue.postRunnable(LaunchActivity$$ExternalSyntheticLambda63.INSTANCE);
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
    public static /* synthetic */ void lambda$onResume$81() {
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
            r0 = 2131628880(0x7f0e1350, float:1.8885065E38)
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
            r9 = 2131628846(0x7f0e132e, float:1.8884996E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setTitle(r9)
            r9 = 2131628845(0x7f0e132d, float:1.8884994E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setMessage(r9)
            r9 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7
            r10.<init>(r1, r0, r3, r2)
            r7.setPositiveButton(r9, r10)
            r9 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6
            r9.<init>(r1, r0, r3, r2)
            r7.setNegativeButton(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda29 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda29     // Catch:{ all -> 0x059d }
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85
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
            r8.lambda$runLinkRequest$60(r0)
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61
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
            r0 = 2131629379(0x7f0e1543, float:1.8886077E38)
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
            r0 = 2131629362(0x7f0e1532, float:1.8886043E38)
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
    public static /* synthetic */ void lambda$didReceivedNotification$82(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$83(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$85(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda102(hashMap, i));
                lambda$runLinkRequest$60(locationActivity);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$84(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$89(ValueAnimator valueAnimator) {
        this.frameLayout.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$90() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$92(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda47(this, tLObject, themeInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$91(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    public /* synthetic */ void lambda$didReceivedNotification$94(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda28(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$93() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$60(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$60(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
            Utilities.globalQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda34(this), 2000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$96() {
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
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda30(this));
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$95() {
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16     // Catch:{ Exception -> 0x0122 }
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
    public static /* synthetic */ void lambda$showLanguageAlertInternal$97(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$98(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$60(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$99(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new LaunchActivity$$ExternalSyntheticLambda88(this, localeInfoArr, str2), 8);
                                TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                                tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                                tLRPC$TL_langpack_getStrings2.keys.add("English");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new LaunchActivity$$ExternalSyntheticLambda89(this, localeInfoArr, str2), 8);
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
    public /* synthetic */ void lambda$showLanguageAlert$101(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$showLanguageAlert$100(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$103(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$102(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            LaunchActivity$$ExternalSyntheticLambda25 launchActivity$$ExternalSyntheticLambda25 = null;
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
                launchActivity$$ExternalSyntheticLambda25 = new LaunchActivity$$ExternalSyntheticLambda25(this);
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, launchActivity$$ExternalSyntheticLambda25);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$104() {
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
            r2.lambda$runLinkRequest$60(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$104():void");
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
