package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import android.window.SplashScreenView;
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
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
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
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
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
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    /* access modifiers changed from: private */
    public ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
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
    private FrameLayout sideMenuContainer;
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
    /* access modifiers changed from: private */
    public String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;
    private boolean wasMutedByAdminRaisedHand;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$2(View view) {
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:28|29|30|31|32) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x00b5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            java.lang.String r0 = "flyme"
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 31
            if (r1 < r2) goto L_0x0014
            android.window.SplashScreen r2 = r12.getSplashScreen()
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18
            r3.<init>(r12)
            r2.setOnExitAnimationListener(r3)
        L_0x0014:
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r2 = r12.getResources()
            android.content.res.Configuration r2 = r2.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r12, r2)
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            r12.currentAccount = r2
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 != 0) goto L_0x0095
            android.content.Intent r2 = r12.getIntent()
            if (r2 == 0) goto L_0x0095
            java.lang.String r3 = r2.getAction()
            if (r3 == 0) goto L_0x0095
            java.lang.String r3 = r2.getAction()
            java.lang.String r4 = "android.intent.action.SEND"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x008e
            java.lang.String r3 = r2.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0055
            goto L_0x008e
        L_0x0055:
            java.lang.String r3 = r2.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0095
            android.net.Uri r2 = r2.getData()
            if (r2 == 0) goto L_0x0095
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r3 = "tg:proxy"
            boolean r3 = r2.startsWith(r3)
            if (r3 != 0) goto L_0x0095
            java.lang.String r3 = "tg://proxy"
            boolean r3 = r2.startsWith(r3)
            if (r3 != 0) goto L_0x0095
            java.lang.String r3 = "tg:socks"
            boolean r3 = r2.startsWith(r3)
            if (r3 != 0) goto L_0x0095
            java.lang.String r3 = "tg://socks"
            boolean r2 = r2.startsWith(r3)
            goto L_0x0095
        L_0x008e:
            super.onCreate(r13)
            r12.finish()
            return
        L_0x0095:
            r2 = 1
            r12.requestWindowFeature(r2)
            r3 = 2131689489(0x7f0var_, float:1.9007995E38)
            r12.setTheme(r3)
            r3 = 0
            r4 = 21
            if (r1 < r4) goto L_0x00be
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r5 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r6 = "actionBarDefault"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ Exception -> 0x00b5 }
            r6 = r6 | r1
            r5.<init>(r3, r3, r6)     // Catch:{ Exception -> 0x00b5 }
            r12.setTaskDescription(r5)     // Catch:{ Exception -> 0x00b5 }
        L_0x00b5:
            android.view.Window r5 = r12.getWindow()     // Catch:{ Exception -> 0x00bd }
            r5.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x00bd }
            goto L_0x00be
        L_0x00bd:
        L_0x00be:
            android.view.Window r1 = r12.getWindow()
            r5 = 2131166173(0x7var_dd, float:1.7946584E38)
            r1.setBackgroundDrawableResource(r5)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x00e2
            boolean r1 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x00e2
            android.view.Window r1 = r12.getWindow()     // Catch:{ Exception -> 0x00de }
            r5 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r5, r5)     // Catch:{ Exception -> 0x00de }
            goto L_0x00e2
        L_0x00de:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x00e2:
            super.onCreate(r13)
            int r1 = android.os.Build.VERSION.SDK_INT
            r5 = 24
            if (r1 < r5) goto L_0x00f1
            boolean r6 = r12.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r6
        L_0x00f1:
            org.telegram.ui.ActionBar.Theme.createCommonChatResources()
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r12)
            java.lang.String r6 = org.telegram.messenger.SharedConfig.passcodeHash
            int r6 = r6.length()
            if (r6 == 0) goto L_0x010d
            boolean r6 = org.telegram.messenger.SharedConfig.appLocked
            if (r6 == 0) goto L_0x010d
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            int r7 = (int) r6
            org.telegram.messenger.SharedConfig.lastPauseTime = r7
        L_0x010d:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r12)
            org.telegram.ui.LaunchActivity$2 r6 = new org.telegram.ui.LaunchActivity$2
            r6.<init>(r12)
            r12.actionBarLayout = r6
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r12)
            r12.frameLayout = r6
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r8 = -1
            r7.<init>(r8, r8)
            r12.setContentView(r6, r7)
            r6 = 8
            if (r1 < r4) goto L_0x0135
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r12)
            r12.themeSwitchImageView = r7
            r7.setVisibility(r6)
        L_0x0135:
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
            if (r1 < r4) goto L_0x016d
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r12)
            r12.themeSwitchSunView = r1
            android.widget.FrameLayout r4 = r12.frameLayout
            r7 = 48
            r9 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r4.addView(r1, r7)
            android.view.View r1 = r12.themeSwitchSunView
            r1.setVisibility(r6)
        L_0x016d:
            android.widget.FrameLayout r1 = r12.frameLayout
            org.telegram.ui.Components.FireworksOverlay r4 = new org.telegram.ui.Components.FireworksOverlay
            r4.<init>(r12)
            r12.fireworksOverlay = r4
            r1.addView(r4)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            r4 = 0
            if (r1 == 0) goto L_0x0262
            android.view.Window r1 = r12.getWindow()
            r7 = 16
            r1.setSoftInputMode(r7)
            org.telegram.ui.LaunchActivity$5 r1 = new org.telegram.ui.LaunchActivity$5
            r1.<init>(r12)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r12.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r7.addView(r1, r9)
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
            r1.addView(r7, r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.actionBarLayout
            r1.addView(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = new org.telegram.ui.ActionBar.ActionBarLayout
            r7.<init>(r12)
            r12.rightActionBarLayout = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = rightFragmentsStack
            r7.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.rightActionBarLayout
            r7.setDelegate(r12)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.rightActionBarLayout
            r1.addView(r7)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r12)
            r12.shadowTabletSide = r7
            r9 = 1076449908(0x40295274, float:2.6456575)
            r7.setBackgroundColor(r9)
            android.widget.FrameLayout r7 = r12.shadowTabletSide
            r1.addView(r7)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r12)
            r12.shadowTablet = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x01f6
            r9 = 8
            goto L_0x01f7
        L_0x01f6:
            r9 = 0
        L_0x01f7:
            r7.setVisibility(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            r9 = 2130706432(0x7var_, float:1.7014118E38)
            r7.setBackgroundColor(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            r1.addView(r7)
            android.widget.FrameLayout r7 = r12.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16
            r9.<init>(r12)
            r7.setOnTouchListener(r9)
            android.widget.FrameLayout r7 = r12.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15 r9 = org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15.INSTANCE
            r7.setOnClickListener(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = new org.telegram.ui.ActionBar.ActionBarLayout
            r7.<init>(r12)
            r12.layersActionBarLayout = r7
            r7.setRemoveActionBarExtraHeight(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            android.widget.FrameLayout r9 = r12.shadowTablet
            r7.setBackgroundView(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            r7.setUseAlphaAnimations(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r12.layersActionBarLayout
            r9 = 2131165304(0x7var_, float:1.7944821E38)
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
            if (r9 == 0) goto L_0x0253
            goto L_0x0254
        L_0x0253:
            r6 = 0
        L_0x0254:
            r7.setVisibility(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.layersActionBarLayout
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.layersActionBarLayout
            r1.addView(r6)
            goto L_0x026e
        L_0x0262:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r8, r8)
            r1.addView(r6, r7)
        L_0x026e:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r12)
            r12.sideMenuContainer = r1
            org.telegram.ui.LaunchActivity$7 r1 = new org.telegram.ui.LaunchActivity$7
            r1.<init>(r12)
            r12.sideMenu = r1
            org.telegram.ui.Components.SideMenultItemAnimator r6 = new org.telegram.ui.Components.SideMenultItemAnimator
            r6.<init>(r1)
            r12.itemAnimator = r6
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setItemAnimator(r6)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            java.lang.String r6 = "chats_menuBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setBackgroundColor(r6)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            r6.<init>(r12, r2, r4)
            r1.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            r1.setAllowItemsInteractionDuringAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r6 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r7 = r12.itemAnimator
            r6.<init>(r12, r7)
            r12.drawerLayoutAdapter = r6
            r1.setAdapter(r6)
            android.widget.FrameLayout r1 = r12.sideMenuContainer
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r1.addView(r6, r7)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            android.widget.FrameLayout r6 = r12.sideMenuContainer
            r1.setDrawerLayout(r6)
            android.widget.FrameLayout r1 = r12.sideMenuContainer
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x02db
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x02f2
        L_0x02db:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r6.x
            int r6 = r6.y
            int r6 = java.lang.Math.min(r9, r6)
            r9 = 1113587712(0x42600000, float:56.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 - r9
            int r6 = java.lang.Math.min(r7, r6)
        L_0x02f2:
            r1.width = r6
            r1.height = r8
            android.widget.FrameLayout r6 = r12.sideMenuContainer
            r6.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r12.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82
            r6.<init>(r12)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r6)
            androidx.recyclerview.widget.ItemTouchHelper r1 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$8 r6 = new org.telegram.ui.LaunchActivity$8
            r7 = 3
            r6.<init>(r7, r4)
            r1.<init>(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            r1.attachToRecyclerView(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r12.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83
            r9.<init>(r12, r1)
            r6.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r9)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r12.actionBarLayout
            r1.setParentActionBarLayout(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r12.drawerLayoutContainer
            r1.setDrawerLayoutContainer(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            r1.init(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25
            r6.<init>(r12)
            r1.setFragmentStackChangedListener(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            r1.setDelegate(r12)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            r12.checkCurrentAccount()
            int r1 = r12.currentAccount
            r12.updateCurrentConnectionState(r1)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r9[r4] = r12
            r1.postNotificationName(r6, r9)
            int r1 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r12.currentConnectionState = r1
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needShowAlert
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.reloadInterface
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r1.addObserver(r12, r9)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.showBulletin
            r1.addObserver(r12, r6)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r1.addObserver(r12, r6)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x04f8
            int r1 = r12.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x03ff
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r12.getClientNotActivatedFragment()
            r1.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r4, r4)
            goto L_0x0413
        L_0x03ff:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r12.sideMenu
            r1.setSideMenu(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            r3.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r12.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r4)
        L_0x0413:
            if (r13 == 0) goto L_0x0577
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r13.getString(r1)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0577
            java.lang.String r3 = "args"
            android.os.Bundle r3 = r13.getBundle(r3)     // Catch:{ Exception -> 0x04f2 }
            int r6 = r1.hashCode()     // Catch:{ Exception -> 0x04f2 }
            r9 = 5
            r10 = 4
            r11 = 2
            switch(r6) {
                case -1529105743: goto L_0x0460;
                case -1349522494: goto L_0x0456;
                case 3052376: goto L_0x044c;
                case 98629247: goto L_0x0442;
                case 738950403: goto L_0x0438;
                case 1434631203: goto L_0x042e;
                default: goto L_0x042d;
            }     // Catch:{ Exception -> 0x04f2 }
        L_0x042d:
            goto L_0x0469
        L_0x042e:
            java.lang.String r6 = "settings"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 1
            goto L_0x0469
        L_0x0438:
            java.lang.String r6 = "channel"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 3
            goto L_0x0469
        L_0x0442:
            java.lang.String r6 = "group"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 2
            goto L_0x0469
        L_0x044c:
            java.lang.String r6 = "chat"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 0
            goto L_0x0469
        L_0x0456:
            java.lang.String r6 = "chat_profile"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 4
            goto L_0x0469
        L_0x0460:
            java.lang.String r6 = "wallpapers"
            boolean r1 = r1.equals(r6)     // Catch:{ Exception -> 0x04f2 }
            if (r1 == 0) goto L_0x0469
            r8 = 5
        L_0x0469:
            if (r8 == 0) goto L_0x04de
            if (r8 == r2) goto L_0x04c2
            if (r8 == r11) goto L_0x04ae
            if (r8 == r7) goto L_0x049a
            if (r8 == r10) goto L_0x0486
            if (r8 == r9) goto L_0x0477
            goto L_0x0577
        L_0x0477:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x0486:
            if (r3 == 0) goto L_0x0577
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            boolean r3 = r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            if (r3 == 0) goto L_0x0577
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x049a:
            if (r3 == 0) goto L_0x0577
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            boolean r3 = r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            if (r3 == 0) goto L_0x0577
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x04ae:
            if (r3 == 0) goto L_0x0577
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            boolean r3 = r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            if (r3 == 0) goto L_0x0577
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x04c2:
            java.lang.String r1 = "user_id"
            int r6 = r12.currentAccount     // Catch:{ Exception -> 0x04f2 }
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)     // Catch:{ Exception -> 0x04f2 }
            long r6 = r6.clientUserId     // Catch:{ Exception -> 0x04f2 }
            r3.putLong(r1, r6)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x04de:
            if (r3 == 0) goto L_0x0577
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04f2 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x04f2 }
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout     // Catch:{ Exception -> 0x04f2 }
            boolean r3 = r3.addFragmentToStack(r1)     // Catch:{ Exception -> 0x04f2 }
            if (r3 == 0) goto L_0x0577
            r1.restoreSelfArgs(r13)     // Catch:{ Exception -> 0x04f2 }
            goto L_0x0577
        L_0x04f2:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0577
        L_0x04f8:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r3 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x050d
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r3 = r12.sideMenu
            r1.setSideMenu(r3)
        L_0x050d:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x054e
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x0529
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0529
            r1 = 1
            goto L_0x052a
        L_0x0529:
            r1 = 0
        L_0x052a:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r2) goto L_0x054f
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 != 0) goto L_0x054c
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.IntroActivity
            if (r3 == 0) goto L_0x054f
        L_0x054c:
            r1 = 0
            goto L_0x054f
        L_0x054e:
            r1 = 1
        L_0x054f:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            if (r3 != r2) goto L_0x0572
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.LoginActivity
            if (r3 != 0) goto L_0x0571
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r12.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            boolean r3 = r3 instanceof org.telegram.ui.IntroActivity
            if (r3 == 0) goto L_0x0572
        L_0x0571:
            r1 = 0
        L_0x0572:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r12.drawerLayoutContainer
            r3.setAllowOpenDrawer(r1, r4)
        L_0x0577:
            r12.checkLayout()
            r12.lambda$onCreate$5()
            android.content.Intent r1 = r12.getIntent()
            if (r13 == 0) goto L_0x0585
            r13 = 1
            goto L_0x0586
        L_0x0585:
            r13 = 0
        L_0x0586:
            r12.handleIntent(r1, r4, r13, r4)
            java.lang.String r13 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05ea }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x05ea }
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x0596
            java.lang.String r13 = r13.toLowerCase()     // Catch:{ Exception -> 0x05ea }
            goto L_0x0597
        L_0x0596:
            r13 = r3
        L_0x0597:
            if (r1 == 0) goto L_0x059d
            java.lang.String r3 = r13.toLowerCase()     // Catch:{ Exception -> 0x05ea }
        L_0x059d:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05ea }
            if (r1 == 0) goto L_0x05bd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05ea }
            r1.<init>()     // Catch:{ Exception -> 0x05ea }
            java.lang.String r6 = "OS name "
            r1.append(r6)     // Catch:{ Exception -> 0x05ea }
            r1.append(r13)     // Catch:{ Exception -> 0x05ea }
            java.lang.String r6 = " "
            r1.append(r6)     // Catch:{ Exception -> 0x05ea }
            r1.append(r3)     // Catch:{ Exception -> 0x05ea }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05ea }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05ea }
        L_0x05bd:
            boolean r13 = r13.contains(r0)     // Catch:{ Exception -> 0x05ea }
            if (r13 != 0) goto L_0x05c9
            boolean r13 = r3.contains(r0)     // Catch:{ Exception -> 0x05ea }
            if (r13 == 0) goto L_0x05ee
        L_0x05c9:
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05ea }
            if (r13 > r5) goto L_0x05ee
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x05ea }
            android.view.Window r13 = r12.getWindow()     // Catch:{ Exception -> 0x05ea }
            android.view.View r13 = r13.getDecorView()     // Catch:{ Exception -> 0x05ea }
            android.view.View r13 = r13.getRootView()     // Catch:{ Exception -> 0x05ea }
            android.view.ViewTreeObserver r0 = r13.getViewTreeObserver()     // Catch:{ Exception -> 0x05ea }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17     // Catch:{ Exception -> 0x05ea }
            r1.<init>(r13)     // Catch:{ Exception -> 0x05ea }
            r12.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x05ea }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x05ea }
            goto L_0x05ee
        L_0x05ea:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x05ee:
            org.telegram.messenger.MediaController r13 = org.telegram.messenger.MediaController.getInstance()
            r13.setBaseActivity(r12, r2)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r12)
            r12.updateAppUpdateViews(r4)
            int r13 = android.os.Build.VERSION.SDK_INT
            r0 = 23
            if (r13 < r0) goto L_0x0604
            org.telegram.messenger.FingerprintController.checkKeyReady()
        L_0x0604:
            r0 = 28
            if (r13 < r0) goto L_0x0634
            java.lang.String r13 = "activity"
            java.lang.Object r13 = r12.getSystemService(r13)
            android.app.ActivityManager r13 = (android.app.ActivityManager) r13
            boolean r13 = r13.isBackgroundRestricted()
            if (r13 == 0) goto L_0x0634
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.getLastCheckedBackgroundActivity()
            long r0 = r0 - r2
            r2 = 86400000(0x5265CLASSNAME, double:4.2687272E-316)
            int r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r13 < 0) goto L_0x0634
            android.app.Dialog r13 = org.telegram.ui.Components.AlertsCreator.createBackgroundActivityDialog(r12)
            r13.show()
            long r0 = java.lang.System.currentTimeMillis()
            org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.setLastCheckedBackgroundActivity(r0)
        L_0x0634:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(final SplashScreenView splashScreenView) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat.setDuration(150);
        ofFloat.addListener(new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(Animator animator) {
                splashScreenView.remove();
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$1(View view, MotionEvent motionEvent) {
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
                lambda$runLinkRequest$47(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$47(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$47(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$47(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$47(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$47(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$47(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$47(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$47(new ChatActivity(bundle3));
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
                        lambda$runLinkRequest$47(new PeopleNearbyActivity());
                    } else {
                        lambda$runLinkRequest$47(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                lambda$runLinkRequest$47(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 13) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
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
        lambda$runLinkRequest$47(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: checkSystemBarColors */
    public void lambda$onCreate$5() {
        checkSystemBarColors(true, !this.isNavigationBarColorFrozen);
    }

    private void checkSystemBarColors(boolean z, boolean z2) {
        BaseFragment baseFragment;
        boolean z3;
        boolean z4 = true;
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
        boolean z5 = baseFragment != null && baseFragment.hasForceLightStatusBar();
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            if (z) {
                if (baseFragment != null) {
                    z3 = baseFragment.isLightStatusBar();
                } else {
                    z3 = ColorUtils.calculateLuminance(Theme.getColor("actionBarDefault", (boolean[]) null, true)) > 0.699999988079071d;
                }
                AndroidUtilities.setLightStatusBar(getWindow(), z3, z5);
            }
            if (i >= 26 && z2) {
                Window window = getWindow();
                int color = Theme.getColor("windowBackgroundGray", (boolean[]) null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
                    Window window2 = getWindow();
                    if (computePerceivedBrightness < 0.721f) {
                        z4 = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window2, z4);
                }
            }
        }
        if ((SharedConfig.noStatusBar || z5) && i >= 21 && z) {
            getWindow().setStatusBarColor(0);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ DialogsActivity lambda$switchToAccount$7(Void voidR) {
        return new DialogsActivity((Bundle) null);
    }

    public void switchToAccount(int i, boolean z) {
        switchToAccount(i, z, LaunchActivity$$ExternalSyntheticLambda57.INSTANCE);
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
        this.actionBarLayout.rebuildLogout();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildLogout();
            this.rightActionBarLayout.rebuildLogout();
        }
        lambda$runLinkRequest$47(new IntroActivity().setOnLogout());
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
            this.passcodeView.onShow(z, z2, i, i2, new LaunchActivity$$ExternalSyntheticLambda33(this, runnable), runnable2);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda81(this));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: org.telegram.ui.ActionIntroActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: org.telegram.ui.ActionIntroActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: org.telegram.ui.EditWidgetActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: org.telegram.ui.ActionIntroActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: org.telegram.ui.ActionIntroActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: org.telegram.ui.ProfileActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v1, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v12, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v14, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v252, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v15, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v16, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v254, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v255, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v18, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v19, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v285, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v20, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v20, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v323, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v21, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v329, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v22, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v335, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v23, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v7, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v344, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v346, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v348, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v24, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v373, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v383, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v390, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v397, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v399, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v407, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v409, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v417, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v418, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v193, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v420, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v427, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v432, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v435, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v437, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v438, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v440, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v446, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v449, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v149, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v231, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v60, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v62, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v53, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v55, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v56, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v57, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v58, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v59, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v60, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r11v2, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r5v2, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r11v6 */
    /* JADX WARNING: type inference failed for: r11v7 */
    /* JADX WARNING: type inference failed for: r11v8 */
    /* JADX WARNING: type inference failed for: r11v9 */
    /* JADX WARNING: type inference failed for: r11v10 */
    /* JADX WARNING: type inference failed for: r5v9 */
    /* JADX WARNING: type inference failed for: r5v11 */
    /* JADX WARNING: type inference failed for: r5v14 */
    /* JADX WARNING: type inference failed for: r5v15 */
    /* JADX WARNING: type inference failed for: r57v0 */
    /* JADX WARNING: type inference failed for: r56v0 */
    /* JADX WARNING: type inference failed for: r47v0 */
    /* JADX WARNING: type inference failed for: r44v0 */
    /* JADX WARNING: type inference failed for: r42v0 */
    /* JADX WARNING: type inference failed for: r15v19, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r57v1 */
    /* JADX WARNING: type inference failed for: r56v1 */
    /* JADX WARNING: type inference failed for: r47v2 */
    /* JADX WARNING: type inference failed for: r44v2 */
    /* JADX WARNING: type inference failed for: r44v3 */
    /* JADX WARNING: type inference failed for: r42v3 */
    /* JADX WARNING: type inference failed for: r42v4 */
    /* JADX WARNING: type inference failed for: r44v4 */
    /* JADX WARNING: type inference failed for: r44v5 */
    /* JADX WARNING: type inference failed for: r42v5 */
    /* JADX WARNING: type inference failed for: r57v4 */
    /* JADX WARNING: type inference failed for: r56v4 */
    /* JADX WARNING: type inference failed for: r44v6 */
    /* JADX WARNING: type inference failed for: r42v6 */
    /* JADX WARNING: type inference failed for: r47v6 */
    /* JADX WARNING: type inference failed for: r42v10 */
    /* JADX WARNING: type inference failed for: r44v12 */
    /* JADX WARNING: type inference failed for: r44v14 */
    /* JADX WARNING: type inference failed for: r42v14 */
    /* JADX WARNING: type inference failed for: r44v15 */
    /* JADX WARNING: type inference failed for: r42v15 */
    /* JADX WARNING: type inference failed for: r44v16 */
    /* JADX WARNING: type inference failed for: r42v16 */
    /* JADX WARNING: type inference failed for: r44v17 */
    /* JADX WARNING: type inference failed for: r42v17 */
    /* JADX WARNING: type inference failed for: r44v18 */
    /* JADX WARNING: type inference failed for: r42v18 */
    /* JADX WARNING: type inference failed for: r42v19 */
    /* JADX WARNING: type inference failed for: r44v19 */
    /* JADX WARNING: type inference failed for: r42v20 */
    /* JADX WARNING: type inference failed for: r44v20 */
    /* JADX WARNING: type inference failed for: r42v21 */
    /* JADX WARNING: type inference failed for: r44v21 */
    /* JADX WARNING: type inference failed for: r42v22 */
    /* JADX WARNING: type inference failed for: r44v22 */
    /* JADX WARNING: type inference failed for: r42v23 */
    /* JADX WARNING: type inference failed for: r44v23 */
    /* JADX WARNING: type inference failed for: r42v24 */
    /* JADX WARNING: type inference failed for: r44v24 */
    /* JADX WARNING: type inference failed for: r42v25 */
    /* JADX WARNING: type inference failed for: r44v25 */
    /* JADX WARNING: type inference failed for: r47v16 */
    /* JADX WARNING: type inference failed for: r11v32 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1b3d, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r5)) != false) goto L_0x1b3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0326, code lost:
        if (r15.sendingText == null) goto L_0x019a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x095a, code lost:
        if (r1.intValue() == 0) goto L_0x095c;
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
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1410, code lost:
        if (r1.longValue() == 0) goto L_0x1415;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x1ac6, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r5)) != false) goto L_0x1ac8;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:889:0x1766 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1077:0x1c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:1078:0x1caa  */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x1cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:1082:0x1cc9  */
    /* JADX WARNING: Removed duplicated region for block: B:1150:0x1f0a  */
    /* JADX WARNING: Removed duplicated region for block: B:1161:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1172:0x1fa0  */
    /* JADX WARNING: Removed duplicated region for block: B:1174:0x1fac  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x03c7 A[Catch:{ Exception -> 0x04ed }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x04f4  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x067e  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0770 A[Catch:{ Exception -> 0x077e }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x077d  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0ff0 A[SYNTHETIC, Splitter:B:641:0x0ff0] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0165 A[SYNTHETIC, Splitter:B:70:0x0165] */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x12a6  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x1387 A[Catch:{ Exception -> 0x1395 }] */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x1394  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x176f A[SYNTHETIC, Splitter:B:894:0x176f] */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x1a5f  */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1a92  */
    /* JADX WARNING: Removed duplicated region for block: B:993:0x1b0a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r70, boolean r71, boolean r72, boolean r73) {
        /*
            r69 = this;
            r15 = r69
            r14 = r70
            r0 = r72
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r69, r70)
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
            java.lang.String r1 = r70.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r8 = r70.getFlags()
            java.lang.String r9 = r70.getAction()
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
            r26 = 1
            goto L_0x0071
        L_0x006f:
            r26 = 0
        L_0x0071:
            if (r73 != 0) goto L_0x009c
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
            r1 = r69
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            r1.saveConfig(r12)
            if (r26 != 0) goto L_0x009c
            r15.passcodeSaveIntent = r14
            r10 = r71
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x009c:
            r10 = r71
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
            if (r1 != 0) goto L_0x1a22
            java.lang.String r1 = r70.getAction()
            if (r1 == 0) goto L_0x1a22
            if (r0 != 0) goto L_0x1a22
            java.lang.String r0 = r70.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "hash"
            java.lang.String r2 = "android.intent.extra.STREAM"
            java.lang.String r4 = "\n"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x034a
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r70.getExtras()
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r70.getExtras()
            java.lang.String r9 = "dialogId"
            long r19 = r0.getLong(r9, r5)
            int r0 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0142
            android.os.Bundle r0 = r70.getExtras()     // Catch:{ all -> 0x013c }
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
            android.os.Bundle r0 = r70.getExtras()
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
            java.lang.String r1 = r70.getType()
            if (r1 == 0) goto L_0x019d
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x019d
            android.os.Bundle r0 = r70.getExtras()     // Catch:{ Exception -> 0x0196 }
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
            r2 = r5
            r13 = r2
            r61 = r13
            r6 = r8
            r12 = r11
            r8 = r15
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            goto L_0x1a3b
        L_0x034a:
            java.lang.String r0 = r70.getAction()
            java.lang.String r7 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x037b
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r2)     // Catch:{ all -> 0x036e }
            r15.importingStickers = r0     // Catch:{ all -> 0x036e }
            java.lang.String r0 = "STICKER_EMOJIS"
            java.util.ArrayList r0 = r14.getStringArrayListExtra(r0)     // Catch:{ all -> 0x036e }
            r15.importingStickersEmoji = r0     // Catch:{ all -> 0x036e }
            java.lang.String r0 = "IMPORTER"
            java.lang.String r0 = r14.getStringExtra(r0)     // Catch:{ all -> 0x036e }
            r15.importingStickersSoftware = r0     // Catch:{ all -> 0x036e }
            goto L_0x1a22
        L_0x036e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r15.importingStickers = r1
            r15.importingStickersEmoji = r1
            r15.importingStickersSoftware = r1
            goto L_0x1a22
        L_0x037b:
            java.lang.String r0 = r70.getAction()
            java.lang.String r7 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x050b
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r2)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r1 = r70.getType()     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x03c4
            r2 = 0
        L_0x0392:
            int r4 = r0.size()     // Catch:{ Exception -> 0x04ed }
            if (r2 >= r4) goto L_0x03bc
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x04ed }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x04ed }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x04ed }
            if (r7 != 0) goto L_0x03aa
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04ed }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x04ed }
        L_0x03aa:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x04ed }
            if (r4 == 0) goto L_0x03b9
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x04ed }
            if (r4 == 0) goto L_0x03b9
            r0.remove(r2)     // Catch:{ Exception -> 0x04ed }
            int r2 = r2 + -1
        L_0x03b9:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x0392
        L_0x03bc:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x04ed }
            if (r2 == 0) goto L_0x03c4
            r2 = 0
            goto L_0x03c5
        L_0x03c4:
            r2 = r0
        L_0x03c5:
            if (r2 == 0) goto L_0x04f1
            if (r1 == 0) goto L_0x0406
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x0406
            r0 = 0
        L_0x03d2:
            int r1 = r2.size()     // Catch:{ Exception -> 0x04ed }
            if (r0 >= r1) goto L_0x04eb
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x04ed }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x04ed }
            boolean r3 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x04ed }
            if (r3 != 0) goto L_0x03ea
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04ed }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x04ed }
        L_0x03ea:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x04ed }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x04ed }
            if (r3 != 0) goto L_0x03f7
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ed }
            r3.<init>()     // Catch:{ Exception -> 0x04ed }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x04ed }
        L_0x03f7:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x04ed }
            r3.<init>()     // Catch:{ Exception -> 0x04ed }
            r3.uri = r1     // Catch:{ Exception -> 0x04ed }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x04ed }
            r1.add(r3)     // Catch:{ Exception -> 0x04ed }
            int r0 = r0 + 1
            goto L_0x03d2
        L_0x0406:
            r4 = 0
            r0 = r11[r4]     // Catch:{ Exception -> 0x04ed }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x04ed }
            java.util.Set<java.lang.String> r4 = r0.exportUri     // Catch:{ Exception -> 0x04ed }
            r7 = 0
        L_0x0410:
            int r0 = r2.size()     // Catch:{ Exception -> 0x04ed }
            if (r7 >= r0) goto L_0x04eb
            java.lang.Object r0 = r2.get(r7)     // Catch:{ Exception -> 0x04ed }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x04ed }
            boolean r9 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x04ed }
            if (r9 != 0) goto L_0x0428
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ed }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x04ed }
        L_0x0428:
            r9 = r0
            android.net.Uri r9 = (android.net.Uri) r9     // Catch:{ Exception -> 0x04ed }
            java.lang.String r12 = org.telegram.messenger.AndroidUtilities.getPath(r9)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ed }
            if (r0 != 0) goto L_0x0437
            r13 = r12
            goto L_0x0438
        L_0x0437:
            r13 = r0
        L_0x0438:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x0450
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ed }
            r0.<init>()     // Catch:{ Exception -> 0x04ed }
            java.lang.String r5 = "export path = "
            r0.append(r5)     // Catch:{ Exception -> 0x04ed }
            r0.append(r13)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ed }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x04ed }
        L_0x0450:
            if (r13 == 0) goto L_0x04a6
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x04ed }
            if (r0 != 0) goto L_0x04a6
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r9)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r5 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x04ed }
            java.util.Iterator r6 = r4.iterator()     // Catch:{ Exception -> 0x04ed }
        L_0x0462:
            boolean r0 = r6.hasNext()     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x048f
            java.lang.Object r0 = r6.next()     // Catch:{ Exception -> 0x04ed }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x04ed }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x048a }
            java.util.regex.Matcher r23 = r0.matcher(r13)     // Catch:{ Exception -> 0x048a }
            boolean r23 = r23.find()     // Catch:{ Exception -> 0x048a }
            if (r23 != 0) goto L_0x0486
            java.util.regex.Matcher r0 = r0.matcher(r5)     // Catch:{ Exception -> 0x048a }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x048a }
            if (r0 == 0) goto L_0x0462
        L_0x0486:
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x048a }
            r0 = 1
            goto L_0x0490
        L_0x048a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04ed }
            goto L_0x0462
        L_0x048f:
            r0 = 0
        L_0x0490:
            if (r0 == 0) goto L_0x0493
            goto L_0x04e5
        L_0x0493:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x04a6
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r13.endsWith(r0)     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x04a6
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x04ed }
            goto L_0x04e5
        L_0x04a6:
            if (r12 == 0) goto L_0x04d3
            java.lang.String r0 = "file:"
            boolean r0 = r12.startsWith(r0)     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x04b6
            java.lang.String r0 = "file://"
            java.lang.String r12 = r12.replace(r0, r3)     // Catch:{ Exception -> 0x04ed }
        L_0x04b6:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04ed }
            if (r0 != 0) goto L_0x04c8
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ed }
            r0.<init>()     // Catch:{ Exception -> 0x04ed }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x04ed }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ed }
            r0.<init>()     // Catch:{ Exception -> 0x04ed }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x04ed }
        L_0x04c8:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04ed }
            r0.add(r12)     // Catch:{ Exception -> 0x04ed }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x04ed }
            r0.add(r13)     // Catch:{ Exception -> 0x04ed }
            goto L_0x04e5
        L_0x04d3:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04ed }
            if (r0 != 0) goto L_0x04de
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ed }
            r0.<init>()     // Catch:{ Exception -> 0x04ed }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x04ed }
        L_0x04de:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04ed }
            r0.add(r9)     // Catch:{ Exception -> 0x04ed }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x04ed }
        L_0x04e5:
            int r7 = r7 + 1
            r5 = 0
            goto L_0x0410
        L_0x04eb:
            r0 = 0
            goto L_0x04f2
        L_0x04ed:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04f1:
            r0 = 1
        L_0x04f2:
            if (r0 == 0) goto L_0x04fe
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x04fe:
            r6 = r8
            r12 = r11
            r7 = r14
            r8 = r15
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
            r61 = 0
            goto L_0x1a2d
        L_0x050b:
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "android.intent.action.VIEW"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x18bd
            android.net.Uri r0 = r70.getData()
            if (r0 == 0) goto L_0x1873
            java.lang.String r2 = r0.getScheme()
            java.lang.String r5 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r6 = "phone"
            if (r2 == 0) goto L_0x15fa
            int r7 = r2.hashCode()
            switch(r7) {
                case 3699: goto L_0x0546;
                case 3213448: goto L_0x053b;
                case 99617003: goto L_0x0530;
                default: goto L_0x052e;
            }
        L_0x052e:
            r7 = -1
            goto L_0x0550
        L_0x0530:
            java.lang.String r7 = "https"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x0539
            goto L_0x052e
        L_0x0539:
            r7 = 2
            goto L_0x0550
        L_0x053b:
            java.lang.String r7 = "http"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x0544
            goto L_0x052e
        L_0x0544:
            r7 = 1
            goto L_0x0550
        L_0x0546:
            java.lang.String r7 = "tg"
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x054f
            goto L_0x052e
        L_0x054f:
            r7 = 0
        L_0x0550:
            java.lang.String r9 = "thread"
            r23 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r7) {
                case 0: goto L_0x0abf;
                case 1: goto L_0x0559;
                case 2: goto L_0x0559;
                default: goto L_0x0557;
            }
        L_0x0557:
            goto L_0x15fa
        L_0x0559:
            java.lang.String r7 = r0.getHost()
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r13 = "telegram.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0579
            java.lang.String r13 = "t.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0579
            java.lang.String r13 = "telegram.dog"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x15fa
        L_0x0579:
            java.lang.String r7 = r0.getPath()
            if (r7 == 0) goto L_0x0a54
            int r13 = r7.length()
            r12 = 1
            if (r13 <= r12) goto L_0x0a54
            java.lang.String r7 = r7.substring(r12)
            java.lang.String r12 = "bg/"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x079a
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r4 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r4.<init>()
            r1.settings = r4
            java.lang.String r4 = "bg/"
            java.lang.String r3 = r7.replace(r4, r3)
            r1.slug = r3
            if (r3 == 0) goto L_0x05c3
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x05c3
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x067b }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x067b }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x067b }
            r4 = r4 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x067b }
            r3 = 0
            r1.slug = r3     // Catch:{ Exception -> 0x067b }
        L_0x05c0:
            r3 = 1
            goto L_0x067c
        L_0x05c3:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x067b
            int r3 = r3.length()
            r4 = 13
            if (r3 < r4) goto L_0x067b
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)
            if (r3 == 0) goto L_0x067b
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x067b }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x067b }
            r9 = 0
            java.lang.String r7 = r7.substring(r9, r4)     // Catch:{ Exception -> 0x067b }
            r4 = 16
            int r7 = java.lang.Integer.parseInt(r7, r4)     // Catch:{ Exception -> 0x067b }
            r4 = r7 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x067b }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x067b }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x067b }
            r7 = 7
            r9 = 13
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x067b }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x067b }
            r4 = r4 | r23
            r3.second_background_color = r4     // Catch:{ Exception -> 0x067b }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x067b }
            int r3 = r3.length()     // Catch:{ Exception -> 0x067b }
            r4 = 20
            if (r3 < r4) goto L_0x0632
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x067b }
            r4 = 13
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x067b }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x067b }
            if (r3 == 0) goto L_0x0632
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x067b }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x067b }
            r7 = 14
            r9 = 20
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x067b }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x067b }
            r4 = r4 | r23
            r3.third_background_color = r4     // Catch:{ Exception -> 0x067b }
        L_0x0632:
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x067b }
            int r3 = r3.length()     // Catch:{ Exception -> 0x067b }
            r4 = 27
            if (r3 != r4) goto L_0x065e
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x067b }
            r4 = 20
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x067b }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x067b }
            if (r3 == 0) goto L_0x065e
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x067b }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x067b }
            r7 = 21
            java.lang.String r4 = r4.substring(r7)     // Catch:{ Exception -> 0x067b }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x067b }
            r4 = r4 | r23
            r3.fourth_background_color = r4     // Catch:{ Exception -> 0x067b }
        L_0x065e:
            java.lang.String r3 = "rotation"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0676 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0676 }
            if (r4 != 0) goto L_0x0676
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x0676 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0676 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x0676 }
            r4.rotation = r3     // Catch:{ Exception -> 0x0676 }
        L_0x0676:
            r3 = 0
            r1.slug = r3     // Catch:{ Exception -> 0x067b }
            goto L_0x05c0
        L_0x067b:
            r3 = 0
        L_0x067c:
            if (r3 != 0) goto L_0x077d
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x06bb
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x06bb
            int r4 = r3.length
            if (r4 <= 0) goto L_0x06bb
            r4 = 0
        L_0x0696:
            int r7 = r3.length
            if (r4 >= r7) goto L_0x06bb
            r7 = r3[r4]
            java.lang.String r9 = "blur"
            boolean r7 = r9.equals(r7)
            if (r7 == 0) goto L_0x06a9
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r9 = 1
            r7.blur = r9
            goto L_0x06b8
        L_0x06a9:
            r9 = 1
            r7 = r3[r4]
            java.lang.String r12 = "motion"
            boolean r7 = r12.equals(r7)
            if (r7 == 0) goto L_0x06b8
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r7.motion = r9
        L_0x06b8:
            int r4 = r4 + 1
            goto L_0x0696
        L_0x06bb:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x06d4
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r3 = r3.intValue()
            r4.intensity = r3
            goto L_0x06da
        L_0x06d4:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            r4 = 50
            r3.intensity = r4
        L_0x06da:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x075d }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x075d }
            if (r4 != 0) goto L_0x075f
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x075d }
            r7 = 6
            r9 = 0
            java.lang.String r12 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x075d }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r12, r7)     // Catch:{ Exception -> 0x075d }
            r7 = r9 | r23
            r4.background_color = r7     // Catch:{ Exception -> 0x075d }
            int r4 = r3.length()     // Catch:{ Exception -> 0x075d }
            r7 = 13
            if (r4 < r7) goto L_0x075d
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x075d }
            r9 = 7
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x075d }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x075d }
            r7 = r9 | r23
            r4.second_background_color = r7     // Catch:{ Exception -> 0x075d }
            int r4 = r3.length()     // Catch:{ Exception -> 0x075d }
            r7 = 20
            if (r4 < r7) goto L_0x0737
            r4 = 13
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x075d }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x075d }
            if (r4 == 0) goto L_0x0737
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x075d }
            r9 = 14
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x075d }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x075d }
            r7 = r9 | r23
            r4.third_background_color = r7     // Catch:{ Exception -> 0x075d }
        L_0x0737:
            int r4 = r3.length()     // Catch:{ Exception -> 0x075d }
            r7 = 27
            if (r4 != r7) goto L_0x075d
            r4 = 20
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x075d }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x075d }
            if (r4 == 0) goto L_0x075d
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x075d }
            r7 = 21
            java.lang.String r3 = r3.substring(r7)     // Catch:{ Exception -> 0x075d }
            r7 = 16
            int r3 = java.lang.Integer.parseInt(r3, r7)     // Catch:{ Exception -> 0x075d }
            r3 = r3 | r23
            r4.fourth_background_color = r3     // Catch:{ Exception -> 0x075d }
        L_0x075d:
            r12 = -1
            goto L_0x0764
        L_0x075f:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x075d }
            r12 = -1
            r3.background_color = r12     // Catch:{ Exception -> 0x0764 }
        L_0x0764:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x077e }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x077e }
            if (r3 != 0) goto L_0x077e
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x077e }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x077e }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x077e }
            r3.rotation = r0     // Catch:{ Exception -> 0x077e }
            goto L_0x077e
        L_0x077d:
            r12 = -1
        L_0x077e:
            r33 = r1
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            goto L_0x0a6e
        L_0x079a:
            r12 = -1
            java.lang.String r13 = "login/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x07de
            java.lang.String r0 = "login/"
            java.lang.String r0 = r7.replace(r0, r3)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x07c3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x07c4
        L_0x07c3:
            r0 = 0
        L_0x07c4:
            r32 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            goto L_0x0a6c
        L_0x07de:
            java.lang.String r13 = "joinchat/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x07ee
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r7.replace(r0, r3)
            goto L_0x0a55
        L_0x07ee:
            java.lang.String r13 = "+"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x0807
            java.lang.String r0 = "+"
            java.lang.String r0 = r7.replace(r0, r3)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isNumeric(r0)
            if (r1 == 0) goto L_0x0a55
            r3 = r0
            r0 = 0
            r1 = 0
            goto L_0x0a57
        L_0x0807:
            java.lang.String r13 = "addstickers/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x081c
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r7.replace(r0, r3)
            r7 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            goto L_0x0a59
        L_0x081c:
            java.lang.String r13 = "msg/"
            boolean r13 = r7.startsWith(r13)
            if (r13 != 0) goto L_0x09e8
            java.lang.String r13 = "share/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x082e
            goto L_0x09e8
        L_0x082e:
            java.lang.String r3 = "confirmphone"
            boolean r3 = r7.startsWith(r3)
            if (r3 == 0) goto L_0x0852
            java.lang.String r3 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r29 = r0
            r23 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            goto L_0x0a66
        L_0x0852:
            java.lang.String r1 = "setlanguage/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0876
            r0 = 12
            java.lang.String r0 = r7.substring(r0)
            r30 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            goto L_0x0a68
        L_0x0876:
            java.lang.String r1 = "addtheme/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x089c
            r0 = 9
            java.lang.String r0 = r7.substring(r0)
            r31 = r0
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0a6a
        L_0x089c:
            java.lang.String r1 = "c/"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x090d
            java.util.List r1 = r0.getPathSegments()
            int r3 = r1.size()
            r4 = 3
            if (r3 != r4) goto L_0x08e7
            r3 = 1
            java.lang.Object r4 = r1.get(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r4)
            r13 = 2
            java.lang.Object r1 = r1.get(r13)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 == 0) goto L_0x08d5
            long r16 = r3.longValue()
            r19 = 0
            int r4 = (r16 > r19 ? 1 : (r16 == r19 ? 0 : -1))
            if (r4 != 0) goto L_0x08d7
        L_0x08d5:
            r1 = 0
            r3 = 0
        L_0x08d7:
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x08eb
            r0 = 0
            goto L_0x08eb
        L_0x08e7:
            r13 = 2
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x08eb:
            r36 = r0
            r34 = r1
            r35 = r3
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            goto L_0x0a74
        L_0x090d:
            r13 = 2
            int r1 = r7.length()
            r3 = 1
            if (r1 < r3) goto L_0x0a54
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r1.<init>(r3)
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x0937
            r3 = 0
            java.lang.Object r4 = r1.get(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r7 = "s"
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x0938
            r1.remove(r3)
            goto L_0x0938
        L_0x0937:
            r3 = 0
        L_0x0938:
            int r4 = r1.size()
            if (r4 <= 0) goto L_0x095e
            java.lang.Object r4 = r1.get(r3)
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            int r4 = r1.size()
            r7 = 1
            if (r4 <= r7) goto L_0x095c
            java.lang.Object r1 = r1.get(r7)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r4 = r1.intValue()
            if (r4 != 0) goto L_0x0960
        L_0x095c:
            r1 = 0
            goto L_0x0960
        L_0x095e:
            r1 = 0
            r3 = 0
        L_0x0960:
            if (r1 == 0) goto L_0x0967
            int r4 = getTimestampFromLink(r0)
            goto L_0x0968
        L_0x0967:
            r4 = -1
        L_0x0968:
            java.lang.String r7 = "start"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r12 = "startgroup"
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.String r13 = "game"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r72 = r1
            java.lang.String r1 = "voicechat"
            java.lang.String r1 = r0.getQueryParameter(r1)
            r23 = r1
            java.lang.String r1 = "livestream"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r24 = r9.intValue()
            if (r24 != 0) goto L_0x099c
            r24 = r1
            r9 = 0
            goto L_0x099e
        L_0x099c:
            r24 = r1
        L_0x099e:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            r34 = r72
            if (r1 != 0) goto L_0x09c9
            r36 = r9
            r27 = r23
            r28 = r24
            r0 = 0
            r1 = 0
            r23 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r35 = 0
            r37 = 0
            goto L_0x09e1
        L_0x09c9:
            r37 = r0
            r36 = r9
            r27 = r23
            r28 = r24
            r0 = 0
            r1 = 0
            r23 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r35 = 0
        L_0x09e1:
            r9 = r7
            r24 = r13
            r7 = 0
            r13 = 0
            goto L_0x0a76
        L_0x09e8:
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x09f1
            goto L_0x09f2
        L_0x09f1:
            r3 = r1
        L_0x09f2:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x0a28
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x0a11
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r4)
            java.lang.String r3 = r1.toString()
            r1 = 1
            goto L_0x0a12
        L_0x0a11:
            r1 = 0
        L_0x0a12:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r7.append(r0)
            java.lang.String r3 = r7.toString()
            goto L_0x0a29
        L_0x0a28:
            r1 = 0
        L_0x0a29:
            int r0 = r3.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r7) goto L_0x0a39
            r0 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r0 = r3.substring(r7, r0)
            goto L_0x0a3b
        L_0x0a39:
            r7 = 0
            r0 = r3
        L_0x0a3b:
            boolean r3 = r0.endsWith(r4)
            if (r3 == 0) goto L_0x0a4c
            int r3 = r0.length()
            r9 = 1
            int r3 = r3 - r9
            java.lang.String r0 = r0.substring(r7, r3)
            goto L_0x0a3b
        L_0x0a4c:
            r13 = r0
            r0 = 0
            r3 = 0
            r4 = -1
            r7 = 0
            r9 = 0
            r12 = 0
            goto L_0x0a5c
        L_0x0a54:
            r0 = 0
        L_0x0a55:
            r1 = 0
            r3 = 0
        L_0x0a57:
            r4 = -1
            r7 = 0
        L_0x0a59:
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x0a5c:
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
        L_0x0a66:
            r30 = 0
        L_0x0a68:
            r31 = 0
        L_0x0a6a:
            r32 = 0
        L_0x0a6c:
            r33 = 0
        L_0x0a6e:
            r34 = 0
            r35 = 0
            r36 = 0
        L_0x0a74:
            r37 = 0
        L_0x0a76:
            r58 = r4
            r10 = r12
            r12 = r23
            r44 = r24
            r56 = r27
            r57 = r28
            r46 = r30
            r55 = r31
            r48 = r32
            r54 = r33
            r40 = r34
            r41 = r35
            r42 = r36
            r43 = r37
            r19 = 0
            r27 = 0
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
            r45 = 0
            r47 = 0
            r49 = 0
            r50 = 0
            r52 = 0
            r4 = r0
            r23 = r1
            r0 = r7
            r7 = r29
            r29 = 0
            goto L_0x1642
        L_0x0abf:
            java.lang.String r7 = r0.toString()
            java.lang.String r12 = "tg:resolve"
            boolean r12 = r7.startsWith(r12)
            java.lang.String r13 = "scope"
            java.lang.String r10 = "tg://telegram.org"
            if (r12 != 0) goto L_0x146f
            java.lang.String r12 = "tg://resolve"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0ad9
            goto L_0x146f
        L_0x0ad9:
            java.lang.String r12 = "tg:privatepost"
            boolean r12 = r7.startsWith(r12)
            if (r12 != 0) goto L_0x13dc
            java.lang.String r12 = "tg://privatepost"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0aeb
            goto L_0x13dc
        L_0x0aeb:
            java.lang.String r9 = "tg:bg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x119c
            java.lang.String r9 = "tg://bg"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0afd
            goto L_0x119c
        L_0x0afd:
            java.lang.String r9 = "tg:join"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x1146
            java.lang.String r9 = "tg://join"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b0f
            goto L_0x1146
        L_0x0b0f:
            java.lang.String r9 = "tg:addstickers"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x112d
            java.lang.String r9 = "tg://addstickers"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b21
            goto L_0x112d
        L_0x0b21:
            java.lang.String r9 = "tg:msg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10a0
            java.lang.String r9 = "tg://msg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10a0
            java.lang.String r9 = "tg://share"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10a0
            java.lang.String r9 = "tg:share"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b43
            goto L_0x10a0
        L_0x0b43:
            java.lang.String r4 = "tg:confirmphone"
            boolean r4 = r7.startsWith(r4)
            if (r4 != 0) goto L_0x107f
            java.lang.String r4 = "tg://confirmphone"
            boolean r4 = r7.startsWith(r4)
            if (r4 == 0) goto L_0x0b55
            goto L_0x107f
        L_0x0b55:
            java.lang.String r1 = "tg:login"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x100c
            java.lang.String r1 = "tg://login"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0b67
            goto L_0x100c
        L_0x0b67:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0fb9
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0b79
            goto L_0x0fb9
        L_0x0b79:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://passport"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0b93
            goto L_0x0var_
        L_0x0b93:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0ec9
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0ba5
            goto L_0x0ec9
        L_0x0ba5:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0e6f
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0bb7
            goto L_0x0e6f
        L_0x0bb7:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0de9
            java.lang.String r1 = "tg://settings"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0bc9
            goto L_0x0de9
        L_0x0bc9:
            java.lang.String r1 = "tg:search"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0dbd
            java.lang.String r1 = "tg://search"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0bdb
            goto L_0x0dbd
        L_0x0bdb:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0da7
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0bed
            goto L_0x0da7
        L_0x0bed:
            java.lang.String r1 = "tg:call"
            boolean r1 = r7.startsWith(r1)
            if (r1 != 0) goto L_0x0cd7
            java.lang.String r1 = "tg://call"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0bff
            goto L_0x0cd7
        L_0x0bff:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r7.startsWith(r0)
            if (r0 != 0) goto L_0x0cb5
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x0CLASSNAME
            goto L_0x0cb5
        L_0x0CLASSNAME:
            java.lang.String r0 = "tg:addcontact"
            boolean r0 = r7.startsWith(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "tg://addcontact"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r0 = "tg://"
            java.lang.String r0 = r7.replace(r0, r3)
            java.lang.String r1 = "tg:"
            java.lang.String r0 = r0.replace(r1, r3)
            r1 = 63
            int r1 = r0.indexOf(r1)
            if (r1 < 0) goto L_0x0c3b
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r1)
        L_0x0c3b:
            r47 = r0
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
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
            goto L_0x1192
        L_0x0CLASSNAME:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r39 = r0
            r38 = r1
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 1
            r35 = 0
            r36 = 0
            r37 = 0
            goto L_0x1182
        L_0x0cb5:
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            goto L_0x117c
        L_0x0cd7:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x15fa
            int r1 = r15.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            boolean r1 = r1.contactsLoaded
            if (r1 != 0) goto L_0x0d18
            java.lang.String r1 = "extra_force_call"
            boolean r1 = r14.hasExtra(r1)
            if (r1 == 0) goto L_0x0cf6
            goto L_0x0d18
        L_0x0cf6:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r5)
            java.lang.String r1 = "extra_force_call"
            r3 = 1
            r0.putExtra(r1, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda56 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda56
            r1.<init>(r15, r0)
            r3 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r1, r3)
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            goto L_0x0d69
        L_0x0d18:
            java.lang.String r1 = "format"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r4 = "name"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r7 = 0
            java.util.List r9 = r15.findContacts(r4, r0, r7)
            boolean r10 = r9.isEmpty()
            if (r10 == 0) goto L_0x0d3e
            if (r0 == 0) goto L_0x0d3e
            r13 = r0
            r12 = r4
            r0 = 1
            r1 = 0
            r3 = 0
            r4 = 0
            r9 = 0
            goto L_0x0d69
        L_0x0d3e:
            int r0 = r9.size()
            r10 = 1
            if (r0 != r10) goto L_0x0d4e
            java.lang.Object r0 = r9.get(r7)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            long r9 = r0.user_id
            goto L_0x0d50
        L_0x0d4e:
            r9 = 0
        L_0x0d50:
            r12 = 0
            int r0 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x0d5a
            if (r4 == 0) goto L_0x0d5b
            r3 = r4
            goto L_0x0d5b
        L_0x0d5a:
            r3 = 0
        L_0x0d5b:
            java.lang.String r0 = "video"
            boolean r0 = r0.equalsIgnoreCase(r1)
            r1 = r0 ^ 1
            r4 = r3
            r7 = 1
            r12 = 0
            r13 = 0
            r3 = r0
            r0 = 0
        L_0x0d69:
            r35 = r0
            r31 = r1
            r32 = r3
            r37 = r4
            r33 = r7
            r50 = r9
            r38 = r12
            r39 = r13
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r34 = 0
            r36 = 0
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
            goto L_0x1198
        L_0x0da7:
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1
            goto L_0x1170
        L_0x0dbd:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://search"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r0 = r0.getQueryParameter(r1)
            if (r0 == 0) goto L_0x0dd9
            java.lang.String r3 = r0.trim()
        L_0x0dd9:
            r27 = r3
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            goto L_0x116a
        L_0x0de9:
            java.lang.String r0 = "themes"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e05
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 2
            goto L_0x116e
        L_0x0e05:
            java.lang.String r0 = "devices"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e21
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 3
            goto L_0x116e
        L_0x0e21:
            java.lang.String r0 = "folders"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e3e
            r0 = 4
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 4
            goto L_0x116e
        L_0x0e3e:
            java.lang.String r0 = "change_number"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e5b
            r0 = 5
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 5
            goto L_0x116e
        L_0x0e5b:
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 1
            goto L_0x116e
        L_0x0e6f:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r55 = r0
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
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
            r48 = 0
            r49 = 0
            r50 = 0
            r52 = 0
            r54 = 0
            goto L_0x163c
        L_0x0ec9:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r46 = r0
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
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
            goto L_0x1190
        L_0x0var_:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://passport"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg:secureid"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r3 = r0.getQueryParameter(r13)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0var_
            java.lang.String r4 = "{"
            boolean r4 = r3.startsWith(r4)
            if (r4 == 0) goto L_0x0var_
            java.lang.String r4 = "}"
            boolean r4 = r3.endsWith(r4)
            if (r4 == 0) goto L_0x0var_
            java.lang.String r4 = "nonce"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r7 = "nonce"
            r1.put(r7, r4)
            goto L_0x0var_
        L_0x0var_:
            java.lang.String r4 = "payload"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r7 = "payload"
            r1.put(r7, r4)
        L_0x0var_:
            java.lang.String r4 = "bot_id"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r7 = "bot_id"
            r1.put(r7, r4)
            r1.put(r13, r3)
            java.lang.String r3 = "public_key"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r4 = "public_key"
            r1.put(r4, r3)
            java.lang.String r3 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r3)
            java.lang.String r3 = "callback_url"
            r1.put(r3, r0)
            r45 = r1
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
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
            goto L_0x118e
        L_0x0fb9:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "user_id"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r3 = "chat_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r0 = r0.getQueryParameter(r8)
            if (r1 == 0) goto L_0x0fe0
            long r3 = java.lang.Long.parseLong(r1)     // Catch:{ NumberFormatException -> 0x0fea }
            goto L_0x0fec
        L_0x0fe0:
            if (r3 == 0) goto L_0x0fea
            long r3 = java.lang.Long.parseLong(r3)     // Catch:{ NumberFormatException -> 0x0fea }
            r9 = r3
            r3 = 0
            goto L_0x0fee
        L_0x0fea:
            r3 = 0
        L_0x0fec:
            r9 = 0
        L_0x0fee:
            if (r0 == 0) goto L_0x0ff5
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0ff5 }
            goto L_0x0ff6
        L_0x0ff5:
            r0 = 0
        L_0x0ff6:
            r28 = r0
            r50 = r3
            r52 = r9
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            goto L_0x160e
        L_0x100c:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://login"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r4 = "code"
            java.lang.String r0 = r0.getQueryParameter(r4)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x1042
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            goto L_0x1043
        L_0x1042:
            r0 = 0
        L_0x1043:
            r48 = r0
            r49 = r1
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
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
            goto L_0x1196
        L_0x107f:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r3 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r3, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r3 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r1)
            r7 = r0
            r12 = r3
            r0 = 0
            r3 = 0
            r4 = 0
            r9 = 0
            r10 = 0
            goto L_0x1163
        L_0x10a0:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://msg"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg://share"
            java.lang.String r0 = r0.replace(r1, r10)
            java.lang.String r1 = "tg:share"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "url"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 != 0) goto L_0x10c5
            goto L_0x10c6
        L_0x10c5:
            r3 = r1
        L_0x10c6:
            java.lang.String r1 = "text"
            java.lang.String r1 = r0.getQueryParameter(r1)
            if (r1 == 0) goto L_0x10fc
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x10e5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r4)
            java.lang.String r3 = r1.toString()
            r1 = 1
            goto L_0x10e6
        L_0x10e5:
            r1 = 0
        L_0x10e6:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r7.append(r0)
            java.lang.String r3 = r7.toString()
            goto L_0x10fd
        L_0x10fc:
            r1 = 0
        L_0x10fd:
            int r0 = r3.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r7) goto L_0x110d
            r0 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r0 = r3.substring(r7, r0)
            goto L_0x110f
        L_0x110d:
            r7 = 0
            r0 = r3
        L_0x110f:
            boolean r3 = r0.endsWith(r4)
            if (r3 == 0) goto L_0x1120
            int r3 = r0.length()
            r9 = 1
            int r3 = r3 - r9
            java.lang.String r0 = r0.substring(r7, r3)
            goto L_0x110f
        L_0x1120:
            r13 = r0
            r23 = r1
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r19 = 0
            goto L_0x1168
        L_0x112d:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r3 = 0
            r4 = 0
            goto L_0x115f
        L_0x1146:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://join"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r1)
            r4 = r0
            r0 = 0
            r3 = 0
        L_0x115f:
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
        L_0x1163:
            r13 = 0
            r19 = 0
            r23 = 0
        L_0x1168:
            r27 = 0
        L_0x116a:
            r28 = 0
            r29 = 0
        L_0x116e:
            r30 = 0
        L_0x1170:
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
        L_0x117c:
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x1182:
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
        L_0x118e:
            r46 = 0
        L_0x1190:
            r47 = 0
        L_0x1192:
            r48 = 0
            r49 = 0
        L_0x1196:
            r50 = 0
        L_0x1198:
            r52 = 0
            goto L_0x1638
        L_0x119c:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://bg"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r3 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r3.<init>()
            r1.settings = r3
            java.lang.String r3 = "slug"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
            if (r3 != 0) goto L_0x11ca
            java.lang.String r3 = "color"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r1.slug = r3
        L_0x11ca:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x11ea
            int r3 = r3.length()
            r4 = 6
            if (r3 != r4) goto L_0x11ea
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x12a2 }
            r4 = r4 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x12a2 }
            r3 = 0
            r1.slug = r3     // Catch:{ Exception -> 0x12a2 }
            r3 = 1
            r12 = 0
            goto L_0x12a4
        L_0x11ea:
            java.lang.String r3 = r1.slug
            if (r3 == 0) goto L_0x12a2
            int r3 = r3.length()
            r4 = 13
            if (r3 < r4) goto L_0x12a2
            java.lang.String r3 = r1.slug
            r4 = 6
            char r3 = r3.charAt(r4)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)
            if (r3 == 0) goto L_0x12a2
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r7 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r9 = 0
            java.lang.String r7 = r7.substring(r9, r4)     // Catch:{ Exception -> 0x12a2 }
            r4 = 16
            int r7 = java.lang.Integer.parseInt(r7, r4)     // Catch:{ Exception -> 0x12a2 }
            r4 = r7 | r23
            r3.background_color = r4     // Catch:{ Exception -> 0x12a2 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r7 = 7
            r9 = 13
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x12a2 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x12a2 }
            r4 = r4 | r23
            r3.second_background_color = r4     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x12a2 }
            r4 = 20
            if (r3 < r4) goto L_0x1259
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r4 = 13
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x12a2 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x12a2 }
            if (r3 == 0) goto L_0x1259
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r7 = 14
            r9 = 20
            java.lang.String r4 = r4.substring(r7, r9)     // Catch:{ Exception -> 0x12a2 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x12a2 }
            r4 = r4 | r23
            r3.third_background_color = r4     // Catch:{ Exception -> 0x12a2 }
        L_0x1259:
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x12a2 }
            r4 = 27
            if (r3 != r4) goto L_0x1285
            java.lang.String r3 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r4 = 20
            char r3 = r3.charAt(r4)     // Catch:{ Exception -> 0x12a2 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x12a2 }
            if (r3 == 0) goto L_0x1285
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x12a2 }
            java.lang.String r4 = r1.slug     // Catch:{ Exception -> 0x12a2 }
            r7 = 21
            java.lang.String r4 = r4.substring(r7)     // Catch:{ Exception -> 0x12a2 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x12a2 }
            r4 = r4 | r23
            r3.fourth_background_color = r4     // Catch:{ Exception -> 0x12a2 }
        L_0x1285:
            java.lang.String r3 = "rotation"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x129d }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x129d }
            if (r4 != 0) goto L_0x129d
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x129d }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x129d }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x129d }
            r4.rotation = r3     // Catch:{ Exception -> 0x129d }
        L_0x129d:
            r12 = 0
            r1.slug = r12     // Catch:{ Exception -> 0x12a3 }
            r3 = 1
            goto L_0x12a4
        L_0x12a2:
            r12 = 0
        L_0x12a3:
            r3 = 0
        L_0x12a4:
            if (r3 != 0) goto L_0x1394
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x12e3
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r3 = r3.split(r4)
            if (r3 == 0) goto L_0x12e3
            int r4 = r3.length
            if (r4 <= 0) goto L_0x12e3
            r4 = 0
        L_0x12be:
            int r7 = r3.length
            if (r4 >= r7) goto L_0x12e3
            r7 = r3[r4]
            java.lang.String r9 = "blur"
            boolean r7 = r9.equals(r7)
            if (r7 == 0) goto L_0x12d1
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r9 = 1
            r7.blur = r9
            goto L_0x12e0
        L_0x12d1:
            r9 = 1
            r7 = r3[r4]
            java.lang.String r10 = "motion"
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x12e0
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r1.settings
            r7.motion = r9
        L_0x12e0:
            int r4 = r4 + 1
            goto L_0x12be
        L_0x12e3:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings
            java.lang.String r4 = "intensity"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)
            int r4 = r4.intValue()
            r3.intensity = r4
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x137a }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x137a }
            if (r4 != 0) goto L_0x137a
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x137a }
            r7 = 0
            r13 = 6
            java.lang.String r9 = r3.substring(r7, r13)     // Catch:{ Exception -> 0x137b }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x137b }
            r7 = r9 | r23
            r4.background_color = r7     // Catch:{ Exception -> 0x137b }
            int r4 = r3.length()     // Catch:{ Exception -> 0x137b }
            r7 = 13
            if (r4 < r7) goto L_0x137b
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x137b }
            r9 = 8
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x137b }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x137b }
            r7 = r9 | r23
            r4.second_background_color = r7     // Catch:{ Exception -> 0x137b }
            int r4 = r3.length()     // Catch:{ Exception -> 0x137b }
            r7 = 20
            if (r4 < r7) goto L_0x1353
            r4 = 13
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x137b }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x137b }
            if (r4 == 0) goto L_0x1353
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x137b }
            r9 = 14
            java.lang.String r9 = r3.substring(r9, r7)     // Catch:{ Exception -> 0x137b }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x137b }
            r7 = r9 | r23
            r4.third_background_color = r7     // Catch:{ Exception -> 0x137b }
        L_0x1353:
            int r4 = r3.length()     // Catch:{ Exception -> 0x137b }
            r7 = 27
            if (r4 != r7) goto L_0x137b
            r4 = 20
            char r4 = r3.charAt(r4)     // Catch:{ Exception -> 0x137b }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x137b }
            if (r4 == 0) goto L_0x137b
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r1.settings     // Catch:{ Exception -> 0x137b }
            r7 = 21
            java.lang.String r3 = r3.substring(r7)     // Catch:{ Exception -> 0x137b }
            r7 = 16
            int r3 = java.lang.Integer.parseInt(r3, r7)     // Catch:{ Exception -> 0x137b }
            r3 = r3 | r23
            r4.fourth_background_color = r3     // Catch:{ Exception -> 0x137b }
            goto L_0x137b
        L_0x137a:
            r13 = 6
        L_0x137b:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x1395 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1395 }
            if (r3 != 0) goto L_0x1395
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r1.settings     // Catch:{ Exception -> 0x1395 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1395 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x1395 }
            r3.rotation = r0     // Catch:{ Exception -> 0x1395 }
            goto L_0x1395
        L_0x1394:
            r13 = 6
        L_0x1395:
            r54 = r1
            r0 = r12
            r3 = r0
            r4 = r3
            r7 = r4
            r9 = r7
            r10 = r9
            r13 = r10
            r27 = r13
            r37 = r27
            r38 = r37
            r39 = r38
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
            r55 = r49
            r56 = r55
            r57 = r56
            r19 = 0
            r23 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r50 = 0
            r52 = 0
            goto L_0x1640
        L_0x13dc:
            r12 = 0
            r13 = 6
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r1)
            java.lang.String r1 = "channel"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.Long r1 = org.telegram.messenger.Utilities.parseLong(r1)
            int r3 = r7.intValue()
            if (r3 == 0) goto L_0x1413
            long r3 = r1.longValue()
            r19 = 0
            int r10 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r10 != 0) goto L_0x1417
            goto L_0x1415
        L_0x1413:
            r19 = 0
        L_0x1415:
            r1 = r12
            r7 = r1
        L_0x1417:
            java.lang.String r3 = r0.getQueryParameter(r9)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r4 = r3.intValue()
            if (r4 != 0) goto L_0x1426
            r3 = r12
        L_0x1426:
            java.lang.String r4 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r4)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x1450
            r41 = r1
            r42 = r3
            r40 = r7
            r0 = r12
            r3 = r0
            r4 = r3
            r7 = r4
            r9 = r7
            r10 = r9
            r13 = r10
            r27 = r13
            r37 = r27
            r38 = r37
            r39 = r38
            r43 = r39
            r44 = r43
            goto L_0x1469
        L_0x1450:
            r43 = r0
            r41 = r1
            r42 = r3
            r40 = r7
            r0 = r12
            r3 = r0
            r4 = r3
            r7 = r4
            r9 = r7
            r10 = r9
            r13 = r10
            r27 = r13
            r37 = r27
            r38 = r37
            r39 = r38
            r44 = r39
        L_0x1469:
            r45 = r44
            r46 = r45
            goto L_0x1508
        L_0x146f:
            r3 = 6
            r12 = 0
            r19 = 0
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r1 = "tg://resolve"
            java.lang.String r0 = r0.replace(r1, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r1 = "domain"
            java.lang.String r1 = r0.getQueryParameter(r1)
            java.lang.String r4 = "telegrampassport"
            boolean r4 = r4.equals(r1)
            if (r4 == 0) goto L_0x1530
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r4 = r0.getQueryParameter(r13)
            boolean r7 = android.text.TextUtils.isEmpty(r4)
            if (r7 != 0) goto L_0x14bc
            java.lang.String r7 = "{"
            boolean r7 = r4.startsWith(r7)
            if (r7 == 0) goto L_0x14bc
            java.lang.String r7 = "}"
            boolean r7 = r4.endsWith(r7)
            if (r7 == 0) goto L_0x14bc
            java.lang.String r7 = "nonce"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "nonce"
            r1.put(r9, r7)
            goto L_0x14c7
        L_0x14bc:
            java.lang.String r7 = "payload"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "payload"
            r1.put(r9, r7)
        L_0x14c7:
            java.lang.String r7 = "bot_id"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "bot_id"
            r1.put(r9, r7)
            r1.put(r13, r4)
            java.lang.String r4 = "public_key"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r7 = "public_key"
            r1.put(r7, r4)
            java.lang.String r4 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r4)
            java.lang.String r4 = "callback_url"
            r1.put(r4, r0)
            r45 = r1
            r0 = r12
            r3 = r0
            r4 = r3
            r7 = r4
            r9 = r7
            r10 = r9
            r13 = r10
            r27 = r13
            r37 = r27
            r38 = r37
            r39 = r38
            r40 = r39
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r46 = r44
        L_0x1508:
            r47 = r46
            r48 = r47
            r49 = r48
            r54 = r49
            r55 = r54
            r56 = r55
            r57 = r56
            r50 = r19
            r52 = r50
            r23 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            goto L_0x1640
        L_0x1530:
            java.lang.String r4 = "start"
            java.lang.String r4 = r0.getQueryParameter(r4)
            java.lang.String r7 = "startgroup"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r10 = "game"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r13 = "voicechat"
            java.lang.String r13 = r0.getQueryParameter(r13)
            java.lang.String r3 = "livestream"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r12 = "post"
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r23 = r12.intValue()
            if (r23 != 0) goto L_0x155f
            r12 = 0
        L_0x155f:
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r23 = r9.intValue()
            r72 = r1
            if (r23 != 0) goto L_0x1570
            r9 = 0
        L_0x1570:
            java.lang.String r1 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r1)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r1 = r0.intValue()
            if (r1 != 0) goto L_0x15b2
            r57 = r3
            r42 = r9
            r44 = r10
            r40 = r12
            r56 = r13
            r50 = r19
            r52 = r50
            r0 = 0
            r12 = 0
            r13 = 0
            r23 = 0
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
            r41 = 0
            r43 = 0
            goto L_0x15e3
        L_0x15b2:
            r43 = r0
            r57 = r3
            r42 = r9
            r44 = r10
            r40 = r12
            r56 = r13
            r50 = r19
            r52 = r50
            r0 = 0
            r12 = 0
            r13 = 0
            r23 = 0
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
            r41 = 0
        L_0x15e3:
            r45 = 0
            r46 = 0
            r47 = 0
            r48 = 0
            r49 = 0
            r54 = 0
            r55 = 0
            r58 = -1
            r3 = r72
            r9 = r4
            r10 = r7
            r4 = 0
            r7 = 0
            goto L_0x1642
        L_0x15fa:
            r19 = 0
            r50 = r19
            r52 = r50
            r0 = 0
            r3 = 0
            r4 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r27 = 0
            r28 = 0
        L_0x160e:
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
        L_0x1638:
            r54 = 0
            r55 = 0
        L_0x163c:
            r56 = 0
            r57 = 0
        L_0x1640:
            r58 = -1
        L_0x1642:
            boolean r1 = r14.hasExtra(r5)
            if (r1 == 0) goto L_0x168a
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x1660
            java.lang.String r1 = "tg"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x1660
            if (r47 != 0) goto L_0x1660
            r1 = 1
            goto L_0x1661
        L_0x1660:
            r1 = 0
        L_0x1661:
            com.google.firebase.appindexing.builders.AssistActionBuilder r2 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r2.<init>()
            r59 = r8
            java.lang.String r8 = r14.getStringExtra(r5)
            com.google.firebase.appindexing.builders.AssistActionBuilder r2 = r2.setActionToken(r8)
            if (r1 == 0) goto L_0x1675
            java.lang.String r1 = "http://schema.org/CompletedActionStatus"
            goto L_0x1677
        L_0x1675:
            java.lang.String r1 = "http://schema.org/FailedActionStatus"
        L_0x1677:
            com.google.firebase.appindexing.Action$Builder r1 = r2.setActionStatus(r1)
            com.google.firebase.appindexing.Action r1 = r1.build()
            com.google.firebase.appindexing.FirebaseUserActions r2 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r69)
            r2.end(r1)
            r14.removeExtra(r5)
            goto L_0x168c
        L_0x168a:
            r59 = r8
        L_0x168c:
            if (r48 != 0) goto L_0x16a9
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x169b
            goto L_0x16a9
        L_0x169b:
            r67 = r11
            r8 = r15
            r61 = r19
            r66 = r59
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
            goto L_0x186a
        L_0x16a9:
            if (r12 != 0) goto L_0x17f8
            if (r7 == 0) goto L_0x16af
            goto L_0x17f8
        L_0x16af:
            if (r3 != 0) goto L_0x1796
            if (r4 != 0) goto L_0x1796
            if (r0 != 0) goto L_0x1796
            if (r13 != 0) goto L_0x1796
            if (r44 != 0) goto L_0x1796
            if (r56 != 0) goto L_0x1796
            if (r45 != 0) goto L_0x1796
            if (r47 != 0) goto L_0x1796
            if (r46 != 0) goto L_0x1796
            if (r48 != 0) goto L_0x1796
            if (r54 != 0) goto L_0x1796
            if (r41 != 0) goto L_0x1796
            if (r55 != 0) goto L_0x1796
            if (r49 == 0) goto L_0x16cd
            goto L_0x1796
        L_0x16cd:
            android.content.ContentResolver r60 = r69.getContentResolver()     // Catch:{ Exception -> 0x177a }
            android.net.Uri r61 = r70.getData()     // Catch:{ Exception -> 0x177a }
            r62 = 0
            r63 = 0
            r64 = 0
            r65 = 0
            android.database.Cursor r1 = r60.query(r61, r62, r63, r64, r65)     // Catch:{ Exception -> 0x177a }
            if (r1 == 0) goto L_0x1769
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1760 }
            if (r0 == 0) goto L_0x1769
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1760 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1760 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x1760 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1760 }
            r2 = 0
            r6 = 3
        L_0x16fd:
            if (r2 >= r6) goto L_0x171c
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x171a }
            long r3 = r3.getClientUserId()     // Catch:{ all -> 0x171a }
            long r7 = (long) r0     // Catch:{ all -> 0x171a }
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x1716
            r3 = 0
            r11[r3] = r2     // Catch:{ all -> 0x171a }
            r0 = r11[r3]     // Catch:{ all -> 0x171a }
            r12 = 1
            r15.switchToAccount(r0, r12)     // Catch:{ all -> 0x175e }
            goto L_0x171d
        L_0x1716:
            r12 = 1
            int r2 = r2 + 1
            goto L_0x16fd
        L_0x171a:
            r0 = move-exception
            goto L_0x1762
        L_0x171c:
            r12 = 1
        L_0x171d:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x175e }
            long r2 = r1.getLong(r0)     // Catch:{ all -> 0x175e }
            r4 = 0
            r0 = r11[r4]     // Catch:{ all -> 0x175e }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x175e }
            int r5 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x175e }
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ all -> 0x175e }
            r0.postNotificationName(r5, r7)     // Catch:{ all -> 0x175e }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x175a }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x175a }
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r4 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x175a }
            if (r4 == 0) goto L_0x174b
            r50 = r2
            r4 = 1
            goto L_0x176d
        L_0x174b:
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x175a }
            r50 = r2
            r4 = r31
            if (r0 == 0) goto L_0x176d
            r32 = 1
            goto L_0x176d
        L_0x175a:
            r0 = move-exception
            r50 = r2
            goto L_0x1763
        L_0x175e:
            r0 = move-exception
            goto L_0x1763
        L_0x1760:
            r0 = move-exception
            r6 = 3
        L_0x1762:
            r12 = 1
        L_0x1763:
            r1.close()     // Catch:{ all -> 0x1766 }
        L_0x1766:
            throw r0     // Catch:{ Exception -> 0x1767 }
        L_0x1767:
            r0 = move-exception
            goto L_0x177d
        L_0x1769:
            r6 = 3
            r12 = 1
            r4 = r31
        L_0x176d:
            if (r1 == 0) goto L_0x1777
            r1.close()     // Catch:{ Exception -> 0x1773 }
            goto L_0x1777
        L_0x1773:
            r0 = move-exception
            r31 = r4
            goto L_0x177d
        L_0x1777:
            r31 = r4
            goto L_0x1780
        L_0x177a:
            r0 = move-exception
            r6 = 3
            r12 = 1
        L_0x177d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1780:
            r67 = r11
            r8 = r15
            r61 = r19
            r7 = r27
            r12 = r28
            r13 = r29
            r5 = r50
            r66 = r59
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
            goto L_0x189a
        L_0x1796:
            r6 = 3
            r12 = 1
            if (r13 == 0) goto L_0x17b5
            java.lang.String r1 = "@"
            boolean r1 = r13.startsWith(r1)
            if (r1 == 0) goto L_0x17b5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = " "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            r8 = r1
            goto L_0x17b6
        L_0x17b5:
            r8 = r13
        L_0x17b6:
            r13 = 0
            r2 = r11[r13]
            r24 = 0
            r1 = r69
            r7 = 6
            r60 = -1
            r61 = r19
            r5 = r0
            r6 = r9
            r9 = 0
            r7 = r10
            r10 = r59
            r9 = r23
            r66 = r10
            r10 = r40
            r67 = r11
            r11 = r41
            r16 = 1
            r12 = r42
            r13 = r43
            r14 = r44
            r15 = r45
            r16 = r46
            r17 = r47
            r18 = r48
            r19 = r49
            r20 = r54
            r21 = r55
            r22 = r56
            r23 = r57
            r25 = r58
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)
            r1 = 2
            r10 = 3
            r11 = 0
            r8 = r69
            goto L_0x186a
        L_0x17f8:
            r67 = r11
            r61 = r19
            r66 = r59
            r60 = -1
            org.telegram.ui.ActionBar.AlertDialog r3 = new org.telegram.ui.ActionBar.AlertDialog
            r10 = 3
            r8 = r69
            r3.<init>(r8, r10)
            r11 = 0
            r3.setCanCancel(r11)
            r3.show()
            org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode r0 = new org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode
            r0.<init>()
            r0.hash = r7
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = new org.telegram.tgnet.TLRPC$TL_codeSettings
            r1.<init>()
            r0.settings = r1
            r1.allow_flashcall = r11
            boolean r2 = org.telegram.messenger.ApplicationLoader.hasPlayServices
            r1.allow_app_hash = r2
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r2 = "mainconfig"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r11)
            org.telegram.tgnet.TLRPC$TL_codeSettings r2 = r0.settings
            boolean r2 = r2.allow_app_hash
            if (r2 == 0) goto L_0x1841
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = org.telegram.messenger.BuildVars.SMS_HASH
            java.lang.String r4 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.putString(r4, r2)
            r1.apply()
            goto L_0x184e
        L_0x1841:
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            r1.apply()
        L_0x184e:
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            r5.putString(r6, r12)
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70
            r1 = r9
            r2 = r69
            r4 = r12
            r6 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 2
            r7.sendRequest(r0, r9, r1)
        L_0x186a:
            r7 = r27
            r12 = r28
            r13 = r29
            r5 = r50
            goto L_0x189a
        L_0x1873:
            r66 = r8
            r67 = r11
            r8 = r15
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
            r61 = 0
            r5 = r61
            r52 = r5
            r7 = 0
            r12 = 0
            r13 = 0
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
        L_0x189a:
            r2 = r5
            r9 = r7
            r11 = r12
            r7 = r13
            r10 = r32
            r5 = r37
            r72 = r38
            r68 = r39
            r13 = r52
            r19 = r61
            r6 = r66
            r12 = r67
            r0 = -1
            r1 = 0
            r4 = -1
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 0
            r23 = 0
            goto L_0x1a53
        L_0x18bd:
            r66 = r8
            r67 = r11
            r8 = r15
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
            r61 = 0
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x18e7
            r2 = r61
            r13 = r2
            r19 = r13
            r6 = r66
            r12 = r67
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 1
            goto L_0x1a39
        L_0x18e7:
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "new_dialog"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x190f
            r2 = r61
            r13 = r2
            r19 = r13
            r6 = r66
            r12 = r67
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 1
            goto L_0x1a43
        L_0x190f:
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x19bb
            java.lang.String r0 = "chatId"
            r7 = r70
            int r0 = r7.getIntExtra(r0, r11)
            long r2 = (long) r0
            java.lang.String r0 = "chatId"
            long r2 = r7.getLongExtra(r0, r2)
            java.lang.String r0 = "userId"
            int r0 = r7.getIntExtra(r0, r11)
            long r4 = (long) r0
            java.lang.String r0 = "userId"
            long r4 = r7.getLongExtra(r0, r4)
            java.lang.String r0 = "encId"
            int r0 = r7.getIntExtra(r0, r11)
            java.lang.String r6 = "appWidgetId"
            int r6 = r7.getIntExtra(r6, r11)
            if (r6 == 0) goto L_0x1959
            java.lang.String r0 = "appWidgetType"
            int r4 = r7.getIntExtra(r0, r11)
            r60 = r6
            r2 = r61
            r13 = r2
            r6 = r66
            r12 = r67
            r0 = 0
            r5 = 0
            r9 = 0
            r15 = 6
            goto L_0x19ab
        L_0x1959:
            r6 = r66
            int r9 = r7.getIntExtra(r6, r11)
            int r12 = (r2 > r61 ? 1 : (r2 == r61 ? 0 : -1))
            if (r12 == 0) goto L_0x197a
            r12 = r67
            r0 = r12[r11]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r4 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r0.postNotificationName(r4, r5)
            r13 = r2
            r2 = r61
        L_0x1975:
            r0 = 0
        L_0x1976:
            r4 = -1
            r5 = 0
        L_0x1978:
            r15 = 0
            goto L_0x19ab
        L_0x197a:
            r12 = r67
            int r2 = (r4 > r61 ? 1 : (r4 == r61 ? 0 : -1))
            if (r2 == 0) goto L_0x1991
            r0 = r12[r11]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r0.postNotificationName(r2, r3)
            r2 = r4
            r13 = r61
            goto L_0x1975
        L_0x1991:
            if (r0 == 0) goto L_0x19a4
            r2 = r12[r11]
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r2.postNotificationName(r3, r4)
            r2 = r61
            r13 = r2
            goto L_0x1976
        L_0x19a4:
            r2 = r61
            r13 = r2
            r0 = 0
            r4 = -1
            r5 = 1
            goto L_0x1978
        L_0x19ab:
            r1 = r0
            r16 = r5
            r11 = r9
            r7 = r15
            r0 = r60
            r19 = r61
            r72 = 0
            r5 = 0
            r9 = 0
            r10 = 0
            goto L_0x1a3d
        L_0x19bb:
            r7 = r70
            r6 = r66
            r12 = r67
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x19e0
            r2 = r61
            r13 = r2
            r19 = r13
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r16 = 0
            r17 = 1
            goto L_0x1a3f
        L_0x19e0:
            java.lang.String r0 = r70.getAction()
            java.lang.String r2 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x1a01
            r2 = r61
            r13 = r2
            r19 = r13
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            goto L_0x1a41
        L_0x1a01:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x1a2d
            r2 = r61
            r13 = r2
            r19 = r13
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 0
            r23 = 1
            goto L_0x1a45
        L_0x1a22:
            r61 = r5
            r6 = r8
            r12 = r11
            r7 = r14
            r8 = r15
            r1 = 2
            r10 = 3
            r11 = 0
            r60 = -1
        L_0x1a2d:
            r2 = r61
            r13 = r2
            r19 = r13
            r72 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r5 = 0
            r7 = 0
        L_0x1a39:
            r9 = 0
            r10 = 0
        L_0x1a3b:
            r16 = 0
        L_0x1a3d:
            r17 = 0
        L_0x1a3f:
            r18 = 0
        L_0x1a41:
            r21 = 0
        L_0x1a43:
            r23 = 0
        L_0x1a45:
            r30 = 0
            r31 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r68 = 0
        L_0x1a53:
            int r15 = r8.currentAccount
            org.telegram.messenger.UserConfig r15 = org.telegram.messenger.UserConfig.getInstance(r15)
            boolean r15 = r15.isClientActivated()
            if (r15 == 0) goto L_0x1efb
            if (r9 == 0) goto L_0x1a8b
            org.telegram.ui.ActionBar.ActionBarLayout r15 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r15 = r15.getLastFragment()
            r73 = r5
            boolean r5 = r15 instanceof org.telegram.ui.DialogsActivity
            if (r5 == 0) goto L_0x1a87
            org.telegram.ui.DialogsActivity r15 = (org.telegram.ui.DialogsActivity) r15
            boolean r5 = r15.isMainDialogList()
            if (r5 == 0) goto L_0x1a85
            android.view.View r5 = r15.getFragmentView()
            if (r5 == 0) goto L_0x1a80
            r5 = 1
            r15.search(r9, r5)
            goto L_0x1a8e
        L_0x1a80:
            r5 = 1
            r15.setInitialSearchString(r9)
            goto L_0x1a8e
        L_0x1a85:
            r5 = 1
            goto L_0x1a8e
        L_0x1a87:
            r5 = 1
            r16 = 1
            goto L_0x1a8e
        L_0x1a8b:
            r73 = r5
            goto L_0x1a85
        L_0x1a8e:
            int r15 = (r2 > r61 ? 1 : (r2 == r61 ? 0 : -1))
            if (r15 == 0) goto L_0x1b0a
            if (r31 != 0) goto L_0x1ae3
            if (r10 == 0) goto L_0x1a97
            goto L_0x1ae3
        L_0x1a97:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putLong(r1, r2)
            if (r11 == 0) goto L_0x1aa6
            r0.putInt(r6, r11)
        L_0x1aa6:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1ac8
            r1 = 0
            r2 = r12[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r5
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1b80
        L_0x1ac8:
            org.telegram.ui.ChatActivity r14 = new org.telegram.ui.ChatActivity
            r14.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            boolean r0 = r13.presentFragment(r14, r15, r16, r17, r18)
            if (r0 == 0) goto L_0x1b80
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1b7e
        L_0x1ae3:
            if (r33 == 0) goto L_0x1afe
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x1efb
            org.telegram.messenger.MessagesController r1 = r0.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r1, r10)
            goto L_0x1efb
        L_0x1afe:
            r1 = 0
            r0 = r12[r1]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r8, r2, r10, r0)
            goto L_0x1efb
        L_0x1b0a:
            int r2 = (r13 > r61 ? 1 : (r13 == r61 ? 0 : -1))
            if (r2 == 0) goto L_0x1b59
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r13)
            if (r11 == 0) goto L_0x1b1d
            r0.putInt(r6, r11)
        L_0x1b1d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1b3f
            r1 = 0
            r2 = r12[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r5
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1b80
        L_0x1b3f:
            org.telegram.ui.ChatActivity r14 = new org.telegram.ui.ChatActivity
            r14.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            boolean r0 = r13.presentFragment(r14, r15, r16, r17, r18)
            if (r0 == 0) goto L_0x1b80
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1b7e
        L_0x1b59:
            if (r1 == 0) goto L_0x1b87
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r2 = "enc_id"
            r0.putInt(r2, r1)
            org.telegram.ui.ChatActivity r14 = new org.telegram.ui.ChatActivity
            r14.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            boolean r0 = r13.presentFragment(r14, r15, r16, r17, r18)
            if (r0 == 0) goto L_0x1b80
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.closeDrawer()
        L_0x1b7e:
            r13 = 1
            goto L_0x1b81
        L_0x1b80:
            r13 = 0
        L_0x1b81:
            r2 = r71
            r7 = 1
            r11 = 0
            goto L_0x1var_
        L_0x1b87:
            if (r16 == 0) goto L_0x1bc5
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1b95
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1bc0
        L_0x1b95:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1bc0
        L_0x1b9f:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r5
            if (r0 <= 0) goto L_0x1bb9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x1b9f
        L_0x1bb9:
            r2 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            r0.closeLastFragment(r2)
            goto L_0x1bc1
        L_0x1bc0:
            r2 = 0
        L_0x1bc1:
            r7 = 1
            r11 = 0
            goto L_0x1eff
        L_0x1bc5:
            r2 = 0
            if (r17 == 0) goto L_0x1bec
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1be6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r11 = 0
            r1.<init>(r8, r11)
            r0.showDialog(r1)
            goto L_0x1be7
        L_0x1be6:
            r11 = 0
        L_0x1be7:
            r2 = r71
            r7 = 1
            goto L_0x1eff
        L_0x1bec:
            r11 = 0
            if (r18 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1be7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84
            r2.<init>(r8, r12)
            r1.<init>(r8, r2, r11)
            r0.showDialog(r1)
            goto L_0x1be7
        L_0x1CLASSNAME:
            android.net.Uri r1 = r8.exportingChatUri
            if (r1 == 0) goto L_0x1c1e
            java.util.ArrayList<android.net.Uri> r0 = r8.documentsUrisArray
            r8.runImportRequest(r1, r0)
            r7 = 1
            goto L_0x1efd
        L_0x1c1e:
            java.util.ArrayList<android.os.Parcelable> r1 = r8.importingStickers
            if (r1 == 0) goto L_0x1c2b
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda24 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda24
            r0.<init>(r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x1be7
        L_0x1c2b:
            java.lang.String r1 = r8.videoPath
            if (r1 != 0) goto L_0x1ecb
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r8.photoPathsArray
            if (r1 != 0) goto L_0x1ecb
            java.lang.String r1 = r8.sendingText
            if (r1 != 0) goto L_0x1ecb
            java.util.ArrayList<java.lang.String> r1 = r8.documentsPathsArray
            if (r1 != 0) goto L_0x1ecb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r8.contactsToSend
            if (r1 != 0) goto L_0x1ecb
            java.util.ArrayList<android.net.Uri> r1 = r8.documentsUrisArray
            if (r1 == 0) goto L_0x1CLASSNAME
            goto L_0x1ecb
        L_0x1CLASSNAME:
            if (r7 == 0) goto L_0x1cd0
            if (r7 != r5) goto L_0x1CLASSNAME
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r8.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            java.lang.String r3 = "user_id"
            r0.putLong(r3, r1)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r14 = r1
        L_0x1CLASSNAME:
            r1 = 6
        L_0x1CLASSNAME:
            r13 = 0
            goto L_0x1c9b
        L_0x1CLASSNAME:
            r1 = 2
            if (r7 != r1) goto L_0x1c6f
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r0.<init>(r1)
        L_0x1c6d:
            r14 = r0
            goto L_0x1CLASSNAME
        L_0x1c6f:
            r1 = 0
            r2 = 3
            if (r7 != r2) goto L_0x1CLASSNAME
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r1)
            goto L_0x1c6d
        L_0x1CLASSNAME:
            r1 = 4
            if (r7 != r1) goto L_0x1CLASSNAME
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x1c6d
        L_0x1CLASSNAME:
            r1 = 5
            if (r7 != r1) goto L_0x1c8f
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 3
            r0.<init>(r1)
            r14 = r0
            r1 = 6
            r13 = 1
            goto L_0x1c9b
        L_0x1c8f:
            r1 = 6
            if (r7 != r1) goto L_0x1CLASSNAME
            org.telegram.ui.EditWidgetActivity r2 = new org.telegram.ui.EditWidgetActivity
            r2.<init>(r4, r0)
            r14 = r2
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r14 = r11
            goto L_0x1CLASSNAME
        L_0x1c9b:
            if (r7 != r1) goto L_0x1caa
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            goto L_0x1cb2
        L_0x1caa:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda52 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda52
            r0.<init>(r8, r14, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x1cb2:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1cc9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d0a
        L_0x1cc9:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r1)
            goto L_0x1d0a
        L_0x1cd0:
            if (r21 == 0) goto L_0x1d0f
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            org.telegram.ui.ContactsActivity r14 = new org.telegram.ui.ContactsActivity
            r14.<init>(r0)
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d04
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d0a
        L_0x1d04:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r1)
        L_0x1d0a:
            r2 = r71
            r7 = 1
            goto L_0x1ee9
        L_0x1d0f:
            if (r73 == 0) goto L_0x1d6c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r5)
            java.lang.String r1 = "returnAsResult"
            r0.putBoolean(r1, r5)
            java.lang.String r1 = "onlyUsers"
            r0.putBoolean(r1, r5)
            java.lang.String r1 = "allowSelf"
            r2 = 0
            r0.putBoolean(r1, r2)
            org.telegram.ui.ContactsActivity r14 = new org.telegram.ui.ContactsActivity
            r14.<init>(r0)
            r0 = r73
            r14.setInitialSearchString(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85
            r0.<init>(r8, r10, r12)
            r14.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r13.getLastFragment()
            boolean r15 = r0 instanceof org.telegram.ui.ContactsActivity
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d65
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d0a
        L_0x1d65:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r1)
            goto L_0x1d0a
        L_0x1d6c:
            if (r36 == 0) goto L_0x1da8
            org.telegram.ui.ActionIntroActivity r14 = new org.telegram.ui.ActionIntroActivity
            r0 = 5
            r14.<init>(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
            r0.<init>(r8, r14)
            r14.setQrLoginDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1da0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d0a
        L_0x1da0:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r1)
            goto L_0x1d0a
        L_0x1da8:
            r1 = 0
            if (r34 == 0) goto L_0x1dfe
            org.telegram.ui.NewContactActivity r14 = new org.telegram.ui.NewContactActivity
            r14.<init>()
            if (r72 == 0) goto L_0x1dc7
            java.lang.String r0 = " "
            r2 = r72
            r3 = 2
            java.lang.String[] r0 = r2.split(r0, r3)
            r2 = r0[r1]
            int r3 = r0.length
            if (r3 <= r5) goto L_0x1dc3
            r7 = r0[r5]
            goto L_0x1dc4
        L_0x1dc3:
            r7 = r11
        L_0x1dc4:
            r14.setInitialName(r2, r7)
        L_0x1dc7:
            r0 = r68
            if (r0 == 0) goto L_0x1dd2
            java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0, r5)
            r14.setInitialPhoneNumber(r0, r1)
        L_0x1dd2:
            org.telegram.ui.ActionBar.ActionBarLayout r13 = r8.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1df6
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d0a
        L_0x1df6:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r5, r1)
            goto L_0x1d0a
        L_0x1dfe:
            r2 = r72
            r0 = r68
            if (r23 == 0) goto L_0x1e1d
            int r0 = r8.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r4 = 0
            r0 = 0
            r6 = 0
            r1 = r69
            r7 = 1
            r5 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1efd
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r7
            goto L_0x1efd
        L_0x1e1d:
            r7 = 1
            if (r35 == 0) goto L_0x1e9d
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r1.getLastFragment()
            if (r1 == 0) goto L_0x1e99
            android.app.Activity r3 = r1.getParentActivity()
            if (r3 == 0) goto L_0x1e99
            int r3 = r8.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
            r4 = 0
            java.lang.String r0 = org.telegram.ui.NewContactActivity.getPhoneNumber(r8, r3, r0, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r1.getParentActivity()
            r3.<init>((android.content.Context) r4)
            r4 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.String r5 = "NewContactAlertTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setTitle(r4)
            r4 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r6 = r6.format(r0)
            r10 = 0
            r5[r10] = r6
            java.lang.String r6 = "NewContactAlertMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setMessage(r4)
            r4 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r5 = "NewContactAlertButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7
            r5.<init>(r0, r2, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r3.setPositiveButton(r4, r5)
            r2 = 2131624707(0x7f0e0303, float:1.8876601E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r2, r11)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1.showDialog(r0)
            r13 = 1
            goto L_0x1e9a
        L_0x1e99:
            r13 = 0
        L_0x1e9a:
            r2 = r71
            goto L_0x1var_
        L_0x1e9d:
            if (r30 == 0) goto L_0x1efd
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            org.telegram.ui.CallLogActivity r1 = new org.telegram.ui.CallLogActivity
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 0
            r0.presentFragment(r1, r2, r3, r4, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1ec4
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1ee7
        L_0x1ec4:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r7, r1)
            goto L_0x1ee7
        L_0x1ecb:
            r1 = 0
            r7 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1ee0
            r0 = r12[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r3)
        L_0x1ee0:
            int r0 = (r19 > r61 ? 1 : (r19 == r61 ? 0 : -1))
            if (r0 != 0) goto L_0x1eeb
            r8.openDialogsToSend(r1)
        L_0x1ee7:
            r2 = r71
        L_0x1ee9:
            r13 = 1
            goto L_0x1var_
        L_0x1eeb:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r19)
            r0.add(r2)
            r8.didSelectDialogs(r11, r0, r11, r1)
            goto L_0x1efd
        L_0x1efb:
            r7 = 1
            r11 = 0
        L_0x1efd:
            r2 = r71
        L_0x1eff:
            r13 = 0
        L_0x1var_:
            if (r13 != 0) goto L_0x1faa
            if (r2 != 0) goto L_0x1faa
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1var_
            int r0 = r8.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r69.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1var_
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1var_
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r11)
            org.telegram.ui.Components.RecyclerListView r1 = r8.sideMenu
            r0.setSideMenu(r1)
            if (r9 == 0) goto L_0x1var_
            r0.setInitialSearchString(r9)
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r7, r1)
            goto L_0x1var_
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1var_
            int r0 = r8.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1f7b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r69.getClientNotActivatedFragment()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1var_
        L_0x1f7b:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r11)
            org.telegram.ui.Components.RecyclerListView r1 = r8.sideMenu
            r0.setSideMenu(r1)
            if (r9 == 0) goto L_0x1f8a
            r0.setInitialSearchString(r9)
        L_0x1f8a:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r7, r1)
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1faa
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
        L_0x1faa:
            if (r26 == 0) goto L_0x1fb2
            r1 = 0
            r0 = r12[r1]
            org.telegram.ui.VoIPFragment.show(r8, r0)
        L_0x1fb2:
            if (r23 != 0) goto L_0x1fc7
            java.lang.String r0 = r70.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x1fc7
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1fc7
            r0.dismiss()
        L_0x1fc7:
            r1 = r70
            r1.setAction(r11)
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
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda50(this, alertDialog, tLRPC$TL_error, str, bundle, tLObject, tLRPC$TL_account_sendConfirmPhoneCode));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$11(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, String str, Bundle bundle, TLObject tLObject, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        alertDialog.dismiss();
        if (tLRPC$TL_error == null) {
            lambda$runLinkRequest$47(new LoginActivity().cancelAccountDeletion(str, bundle, (TLRPC$TL_auth_sentCode) tLObject));
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
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda90(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$47(locationActivity);
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new LaunchActivity$$ExternalSyntheticLambda59(alertDialog, actionIntroActivity));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$19(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!(tLObject instanceof TLRPC$TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda22(actionIntroActivity, tLRPC$TL_error));
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
            r4 = 2131627905(0x7f0e0var_, float:1.8883088E38)
            java.lang.String r5 = "selectAlertString"
            if (r1 == 0) goto L_0x003d
            int r1 = r1.size()
            if (r1 == r2) goto L_0x0052
            java.lang.String r1 = "SendContactToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627881(0x7f0e0var_, float:1.8883039E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
            goto L_0x0052
        L_0x003d:
            java.lang.String r1 = "SendMessagesToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627904(0x7f0e0var_, float:1.8883086E38)
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
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new LaunchActivity$$ExternalSyntheticLambda61(this, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runCommentRequest$24(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda36(this, tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
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
            r11.lambda$runLinkRequest$47(r2)
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
            r3 = 2131624827(0x7f0e037b, float:1.8876845E38)
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64
            r6.<init>(r10, r11, r12, r0)
            int r11 = r5.sendRequest(r4, r6)
            r1[r2] = r11
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1
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
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda37(this, tLObject, uri, i, alertDialog), 2);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v39, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v43, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:106:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0319  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x041b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, java.lang.String r31, java.lang.String r32, java.lang.String r33, boolean r34, java.lang.Integer r35, java.lang.Long r36, java.lang.Integer r37, java.lang.Integer r38, java.lang.String r39, java.util.HashMap<java.lang.String, java.lang.String> r40, java.lang.String r41, java.lang.String r42, java.lang.String r43, java.lang.String r44, org.telegram.tgnet.TLRPC$TL_wallPaper r45, java.lang.String r46, java.lang.String r47, java.lang.String r48, int r49, int r50) {
        /*
            r26 = this;
            r15 = r26
            r14 = r27
            r0 = r28
            r5 = r29
            r6 = r30
            r9 = r33
            r12 = r36
            r13 = r40
            r11 = r41
            r10 = r42
            r8 = r45
            r7 = r46
            r1 = r49
            r2 = 2
            if (r1 != 0) goto L_0x0068
            int r3 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r3 < r2) goto L_0x0068
            if (r13 == 0) goto L_0x0068
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
            r1 = r4
            r2 = r26
            r3 = r27
            r14 = r4
            r4 = r28
            r5 = r29
            r6 = r30
            r0 = r7
            r7 = r31
            r8 = r32
            r9 = r33
            r10 = r34
            r11 = r35
            r12 = r36
            r13 = r37
            r0 = r14
            r14 = r38
            r15 = r39
            r16 = r40
            r17 = r41
            r18 = r42
            r19 = r43
            r20 = r44
            r21 = r45
            r22 = r46
            r23 = r47
            r24 = r48
            r25 = r50
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)
            r15 = r26
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r15, r0)
            r0.show()
            return
        L_0x0068:
            r3 = r7
            r4 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.String r7 = "OK"
            r13 = 0
            r8 = 1
            r11 = 0
            if (r43 == 0) goto L_0x00bb
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r1)
            if (r0 == 0) goto L_0x008b
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r43
            r0.postNotificationName(r1, r2)
            goto L_0x00ba
        L_0x008b:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r15)
            r1 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626877(0x7f0e0b7d, float:1.8881003E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r11] = r43
            java.lang.String r3 = "OtherLoginCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r13)
            r15.showAlertDialog(r0)
        L_0x00ba:
            return
        L_0x00bb:
            if (r44 == 0) goto L_0x00e5
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r15)
            r1 = 2131624454(0x7f0e0206, float:1.8876088E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624465(0x7f0e0211, float:1.887611E38)
            java.lang.String r2 = "AuthAnotherClientUrl"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setPositiveButton(r1, r13)
            r15.showAlertDialog(r0)
            return
        L_0x00e5:
            org.telegram.ui.ActionBar.AlertDialog r10 = new org.telegram.ui.ActionBar.AlertDialog
            r4 = 3
            r10.<init>(r15, r4)
            int[] r7 = new int[r8]
            r7[r11] = r11
            if (r0 == 0) goto L_0x0146
            boolean r1 = org.telegram.messenger.AndroidUtilities.isNumeric(r28)
            if (r1 == 0) goto L_0x00ff
            org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone
            r1.<init>()
            r1.phone = r0
            goto L_0x0106
        L_0x00ff:
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r1.<init>()
            r1.username = r0
        L_0x0106:
            r0 = r1
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65
            r1 = r9
            r2 = r26
            r3 = r39
            r4 = r47
            r5 = r48
            r6 = r27
            r8 = r7
            r7 = r35
            r47 = r8
            r8 = r38
            r15 = r9
            r9 = r37
            r48 = r10
            r10 = r47
            r11 = r48
            r16 = r0
            r0 = r12
            r12 = r32
            r13 = r31
            r14 = r50
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            r1 = r16
            int r0 = r0.sendRequest(r1, r15)
            r7 = r47
            r10 = 0
            r7[r10] = r0
            r11 = r26
            r13 = r27
            r14 = r48
            goto L_0x0185
        L_0x0146:
            r48 = r10
            r10 = 0
            if (r5 == 0) goto L_0x0188
            if (r1 != 0) goto L_0x016a
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63
            r11 = r26
            r13 = r27
            r14 = r48
            r3.<init>(r11, r13, r14, r5)
            int r0 = r1.sendRequest(r0, r3, r2)
            r7[r10] = r0
            goto L_0x0185
        L_0x016a:
            r11 = r26
            r13 = r27
            r14 = r48
            if (r1 != r8) goto L_0x0185
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62
            r3.<init>(r11, r13, r14)
            r1.sendRequest(r0, r3, r2)
        L_0x0185:
            r2 = 0
            goto L_0x0416
        L_0x0188:
            r11 = r26
            r13 = r27
            r14 = r48
            if (r6 == 0) goto L_0x01ee
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x01d7
            r2 = r1
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r4 = 0
            org.telegram.ui.Components.ChatActivityEnterView r5 = r2.getChatActivityEnterViewForStickers()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r2.getResourceProvider()
            r27 = r3
            r28 = r26
            r29 = r1
            r30 = r0
            r31 = r4
            r32 = r5
            r33 = r6
            r27.<init>(r28, r29, r30, r31, r32, r33)
            boolean r0 = r2.isKeyboardVisible()
            r3.setCalcMandatoryInsets(r0)
            goto L_0x01ea
        L_0x01d7:
            org.telegram.ui.Components.StickersAlert r3 = new org.telegram.ui.Components.StickersAlert
            r2 = 0
            r4 = 0
            r27 = r3
            r28 = r26
            r29 = r1
            r30 = r0
            r31 = r2
            r32 = r4
            r27.<init>((android.content.Context) r28, (org.telegram.ui.ActionBar.BaseFragment) r29, (org.telegram.tgnet.TLRPC$InputStickerSet) r30, (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r31, (org.telegram.ui.Components.StickersAlert.StickersAlertDelegate) r32)
        L_0x01ea:
            r1.showDialog(r3)
        L_0x01ed:
            return
        L_0x01ee:
            if (r9 == 0) goto L_0x0213
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r8)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r4)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88
            r2 = r34
            r0.<init>(r11, r2, r13, r9)
            r1.setDelegate(r0)
            r11.presentFragment(r1, r10, r8)
            goto L_0x0185
        L_0x0213:
            r0 = r40
            if (r0 == 0) goto L_0x0281
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x022a
            return
        L_0x022a:
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
            long r8 = (long) r1
            r5.bot_id = r8
            java.lang.String r1 = "scope"
            java.lang.Object r1 = r0.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            r5.scope = r1
            java.lang.String r1 = "public_key"
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            r5.public_key = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75
            r28 = r1
            r29 = r26
            r30 = r7
            r31 = r27
            r32 = r14
            r33 = r5
            r34 = r2
            r35 = r3
            r36 = r4
            r28.<init>(r29, r30, r31, r32, r33, r34, r35, r36)
            int r0 = r0.sendRequest(r5, r1)
            r7[r10] = r0
            goto L_0x0185
        L_0x0281:
            r0 = r42
            if (r0 == 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda66 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda66
            r2.<init>(r11, r14)
            int r0 = r0.sendRequest(r1, r2)
            r7[r10] = r0
            goto L_0x0185
        L_0x029f:
            java.lang.String r0 = "android"
            r1 = r41
            if (r1 == 0) goto L_0x02c1
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r2 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r2.<init>()
            r2.lang_code = r1
            r2.lang_pack = r0
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda68 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda68
            r1.<init>(r11, r14)
            int r0 = r0.sendRequest(r2, r1)
            r7[r10] = r0
            goto L_0x0185
        L_0x02c1:
            r1 = r45
            if (r1 == 0) goto L_0x033c
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0315
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x030f }
            int r2 = r0.third_background_color     // Catch:{ Exception -> 0x030f }
            if (r2 == 0) goto L_0x02ed
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x030f }
            java.lang.String r4 = "c"
            int r5 = r0.background_color     // Catch:{ Exception -> 0x030f }
            int r6 = r0.second_background_color     // Catch:{ Exception -> 0x030f }
            int r0 = r0.fourth_background_color     // Catch:{ Exception -> 0x030f }
            r28 = r3
            r29 = r4
            r30 = r5
            r31 = r6
            r32 = r2
            r33 = r0
            r28.<init>(r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x030f }
            goto L_0x02fe
        L_0x02ed:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x030f }
            java.lang.String r2 = "c"
            int r4 = r0.background_color     // Catch:{ Exception -> 0x030f }
            int r5 = r0.second_background_color     // Catch:{ Exception -> 0x030f }
            int r0 = r0.rotation     // Catch:{ Exception -> 0x030f }
            int r0 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r10)     // Catch:{ Exception -> 0x030f }
            r3.<init>(r2, r4, r5, r0)     // Catch:{ Exception -> 0x030f }
        L_0x02fe:
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x030f }
            r2 = 0
            r0.<init>(r3, r2, r8, r10)     // Catch:{ Exception -> 0x030d }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda54 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda54     // Catch:{ Exception -> 0x030d }
            r3.<init>(r11, r0)     // Catch:{ Exception -> 0x030d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x030d }
            goto L_0x0317
        L_0x030d:
            r0 = move-exception
            goto L_0x0311
        L_0x030f:
            r0 = move-exception
            r2 = 0
        L_0x0311:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0316
        L_0x0315:
            r2 = 0
        L_0x0316:
            r8 = 0
        L_0x0317:
            if (r8 != 0) goto L_0x0416
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r1.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71
            r4.<init>(r11, r14, r1)
            int r0 = r3.sendRequest(r0, r4)
            r7[r10] = r0
            goto L_0x0416
        L_0x033c:
            r2 = 0
            if (r3 == 0) goto L_0x0367
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30
            r1.<init>(r11)
            org.telegram.tgnet.TLRPC$TL_account_getTheme r2 = new org.telegram.tgnet.TLRPC$TL_account_getTheme
            r2.<init>()
            r2.format = r0
            org.telegram.tgnet.TLRPC$TL_inputThemeSlug r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug
            r0.<init>()
            r0.slug = r3
            r2.theme = r0
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda67 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda67
            r3.<init>(r11, r14)
            int r0 = r0.sendRequest(r2, r3)
            r7[r10] = r0
            goto L_0x0417
        L_0x0367:
            if (r12 == 0) goto L_0x0416
            if (r35 == 0) goto L_0x0416
            if (r37 == 0) goto L_0x03c4
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r27)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            if (r0 == 0) goto L_0x038d
            r28 = r26
            r29 = r27
            r30 = r14
            r31 = r35
            r32 = r38
            r33 = r37
            r34 = r0
            int r0 = r28.runCommentRequest(r29, r30, r31, r32, r33, r34)
            r7[r10] = r0
            goto L_0x0416
        L_0x038d:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r0 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            long r3 = r36.longValue()
            r1.channel_id = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r3 = r0.id
            r3.add(r1)
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
            r39 = r3
            r40 = r26
            r41 = r7
            r42 = r27
            r43 = r14
            r44 = r35
            r45 = r38
            r46 = r37
            r39.<init>(r40, r41, r42, r43, r44, r45, r46)
            int r0 = r1.sendRequest(r0, r3)
            r7[r10] = r0
            goto L_0x0416
        L_0x03c4:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r3 = r36.longValue()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r3)
            int r1 = r35.intValue()
            java.lang.String r3 = "message_id"
            r0.putInt(r3, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x03f1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r8
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03f2
        L_0x03f1:
            r1 = r2
        L_0x03f2:
            if (r1 == 0) goto L_0x03fe
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r27)
            boolean r3 = r3.checkCanOpenChat(r0, r1)
            if (r3 == 0) goto L_0x0416
        L_0x03fe:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda32 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda32
            r28 = r3
            r29 = r26
            r30 = r0
            r31 = r36
            r32 = r7
            r33 = r14
            r34 = r1
            r35 = r27
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x0416:
            r1 = r2
        L_0x0417:
            r0 = r7[r10]
            if (r0 == 0) goto L_0x0428
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0
            r0.<init>(r13, r7, r1)
            r14.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r14.showDelayed(r0)     // Catch:{ Exception -> 0x0428 }
        L_0x0428:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, int, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$28(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Long l, Integer num2, Integer num3, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, String str13, String str14, int i2, int i3) {
        int i4 = i3;
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i3, str, str2, str3, str4, str5, str6, z, num, l, num2, num3, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, str13, str14, 1, i2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$32(String str, String str2, String str3, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str4, String str5, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity$$ExternalSyntheticLambda38 launchActivity$$ExternalSyntheticLambda38 = r0;
        LaunchActivity$$ExternalSyntheticLambda38 launchActivity$$ExternalSyntheticLambda382 = new LaunchActivity$$ExternalSyntheticLambda38(this, tLObject, tLRPC$TL_error, str, str2, str3, i, num, num2, num3, iArr, alertDialog, str4, str5, i2);
        AndroidUtilities.runOnUIThread(launchActivity$$ExternalSyntheticLambda38, 2);
    }

    /* JADX WARNING: type inference failed for: r1v11 */
    /* JADX WARNING: type inference failed for: r1v12, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r1v19, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r1v46 */
    /* JADX WARNING: type inference failed for: r1v47 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x008e, code lost:
        if (r26[0] != 0) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f1, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0110, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00f3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x018c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$31(org.telegram.tgnet.TLObject r17, org.telegram.tgnet.TLRPC$TL_error r18, java.lang.String r19, java.lang.String r20, java.lang.String r21, int r22, java.lang.Integer r23, java.lang.Integer r24, java.lang.Integer r25, int[] r26, org.telegram.ui.ActionBar.AlertDialog r27, java.lang.String r28, java.lang.String r29, int r30) {
        /*
            r16 = this;
            r8 = r16
            r0 = r18
            r1 = r19
            r2 = r20
            r5 = r21
            r3 = r22
            r4 = r28
            r6 = r29
            r7 = r30
            boolean r9 = r16.isFinishing()
            if (r9 != 0) goto L_0x034a
            r9 = r17
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r9 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r9
            r10 = 1
            r11 = 0
            if (r0 != 0) goto L_0x02ee
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r8.actionBarLayout
            if (r12 == 0) goto L_0x02ee
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
            if (r5 == 0) goto L_0x02ee
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x02ee
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
            if (r23 == 0) goto L_0x0093
            if (r24 != 0) goto L_0x0069
            if (r25 == 0) goto L_0x0093
        L_0x0069:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0093
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            java.lang.Object r0 = r0.get(r11)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r16
            r2 = r22
            r3 = r27
            r4 = r23
            r5 = r24
            r6 = r25
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r26[r11] = r0
            r0 = r26[r11]
            if (r0 == 0) goto L_0x033f
        L_0x0090:
            r10 = 0
            goto L_0x033f
        L_0x0093:
            java.lang.String r0 = "dialogsType"
            java.lang.String r12 = "onlySelect"
            if (r1 == 0) goto L_0x0193
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r12, r10)
            java.lang.String r4 = "cantSendToChannels"
            r2.putBoolean(r4, r10)
            r2.putInt(r0, r10)
            r0 = 2131627887(0x7f0e0f6f, float:1.8883051E38)
            java.lang.String r4 = "SendGameToText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertString"
            r2.putString(r4, r0)
            r0 = 2131627886(0x7f0e0f6e, float:1.888305E38)
            java.lang.String r4 = "SendGameToGroupText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.String r4 = "selectAlertStringGroup"
            r2.putString(r4, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87
            r2.<init>(r8, r1, r3, r9)
            r0.setDelegate(r2)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x00f7
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x00f5
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x00f5
        L_0x00f3:
            r1 = 1
            goto L_0x0113
        L_0x00f5:
            r1 = 0
            goto L_0x0113
        L_0x00f7:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 <= r10) goto L_0x00f5
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            boolean r1 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r1 == 0) goto L_0x00f5
            goto L_0x00f3
        L_0x0113:
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
            if (r0 == 0) goto L_0x013f
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x013f
            org.telegram.ui.SecretMediaViewer r0 = org.telegram.ui.SecretMediaViewer.getInstance()
            r0.closePhoto(r11, r11)
            goto L_0x016e
        L_0x013f:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r0 == 0) goto L_0x0157
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0157
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r10)
            goto L_0x016e
        L_0x0157:
            boolean r0 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r0 == 0) goto L_0x016e
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x016e
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r0.close(r11, r10)
        L_0x016e:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x0175
            r0.dismiss()
        L_0x0175:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r11, r11)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x018c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x033f
        L_0x018c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r11)
            goto L_0x033f
        L_0x0193:
            r1 = 0
            if (r4 == 0) goto L_0x0218
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r9.users
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x01a6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r9.users
            java.lang.Object r1 = r1.get(r11)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
        L_0x01a6:
            if (r1 == 0) goto L_0x01e9
            boolean r2 = r1.bot
            if (r2 == 0) goto L_0x01b1
            boolean r2 = r1.bot_nochats
            if (r2 == 0) goto L_0x01b1
            goto L_0x01e9
        L_0x01b1:
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r12, r10)
            r5 = 2
            r2.putInt(r0, r5)
            r0 = 2131624247(0x7f0e0137, float:1.8875668E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r1)
            r5[r11] = r6
            java.lang.String r6 = "%1$s"
            r5[r10] = r6
            java.lang.String r6 = "AddToTheGroupAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r0, r5)
            java.lang.String r5 = "addToGroupAlertString"
            r2.putString(r5, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda86
            r2.<init>(r8, r3, r1, r4)
            r0.setDelegate(r2)
            r8.lambda$runLinkRequest$47(r0)
            goto L_0x033f
        L_0x01e9:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x0213 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x0217
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x0213 }
            int r1 = r0.size()     // Catch:{ Exception -> 0x0213 }
            int r1 = r1 - r10
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x0213 }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x0213 }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r1 = "BotCantJoinGroups"
            r2 = 2131624625(0x7f0e02b1, float:1.8876435E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0213 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0213 }
            r0.show()     // Catch:{ Exception -> 0x0213 }
            goto L_0x0217
        L_0x0213:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0217:
            return
        L_0x0218:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0240
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            long r12 = r0.id
            java.lang.String r0 = "chat_id"
            r4.putLong(r0, r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            long r12 = r0.id
            long r12 = -r12
            goto L_0x0259
        L_0x0240:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r9.users
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            long r12 = r0.id
            java.lang.String r0 = "user_id"
            r4.putLong(r0, r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r9.users
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            long r12 = r0.id
        L_0x0259:
            if (r6 == 0) goto L_0x0276
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r9.users
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0276
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r9.users
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            boolean r0 = r0.bot
            if (r0 == 0) goto L_0x0276
            java.lang.String r0 = "botUser"
            r4.putString(r0, r6)
            r0 = 1
            goto L_0x0277
        L_0x0276:
            r0 = 0
        L_0x0277:
            if (r23 == 0) goto L_0x0282
            int r9 = r23.intValue()
            java.lang.String r14 = "message_id"
            r4.putInt(r14, r9)
        L_0x0282:
            if (r2 == 0) goto L_0x0289
            java.lang.String r9 = "voicechat"
            r4.putString(r9, r2)
        L_0x0289:
            if (r5 == 0) goto L_0x0290
            java.lang.String r9 = "livestream"
            r4.putString(r9, r5)
        L_0x0290:
            if (r7 < 0) goto L_0x0297
            java.lang.String r9 = "video_timestamp"
            r4.putInt(r9, r7)
        L_0x0297:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = mainFragmentsStack
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x02ae
            if (r2 != 0) goto L_0x02ae
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
        L_0x02ae:
            if (r1 == 0) goto L_0x02ba
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r22)
            boolean r2 = r2.checkCanOpenChat(r4, r1)
            if (r2 == 0) goto L_0x033f
        L_0x02ba:
            if (r0 == 0) goto L_0x02cf
            boolean r0 = r1 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x02cf
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r14 = r1.getDialogId()
            int r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x02cf
            r1.setBotUser(r6)
            goto L_0x033f
        L_0x02cf:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            if (r23 != 0) goto L_0x02d7
            r9 = 0
            goto L_0x02dc
        L_0x02d7:
            int r1 = r23.intValue()
            r9 = r1
        L_0x02dc:
            org.telegram.ui.LaunchActivity$13 r10 = new org.telegram.ui.LaunchActivity$13
            r1 = r10
            r2 = r16
            r3 = r27
            r5 = r21
            r6 = r12
            r1.<init>(r3, r4, r5, r6)
            r0.ensureMessagesLoaded(r12, r9, r10)
            goto L_0x0090
        L_0x02ee:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x033b }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x033b }
            if (r1 != 0) goto L_0x033f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x033b }
            int r2 = r1.size()     // Catch:{ Exception -> 0x033b }
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x033b }
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1     // Catch:{ Exception -> 0x033b }
            if (r0 == 0) goto L_0x0326
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x033b }
            if (r0 == 0) goto L_0x0326
            java.lang.String r2 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r2)     // Catch:{ Exception -> 0x033b }
            if (r0 == 0) goto L_0x0326
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x033b }
            java.lang.String r1 = "FloodWait"
            r2 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x033b }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x033b }
            r0.show()     // Catch:{ Exception -> 0x033b }
            goto L_0x033f
        L_0x0326:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x033b }
            java.lang.String r1 = "NoUsernameFound"
            r2 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x033b }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x033b }
            r0.show()     // Catch:{ Exception -> 0x033b }
            goto L_0x033f
        L_0x033b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x033f:
            if (r10 == 0) goto L_0x034a
            r27.dismiss()     // Catch:{ Exception -> 0x0345 }
            goto L_0x034a
        L_0x0345:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x034a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$31(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$29(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    public /* synthetic */ void lambda$runLinkRequest$30(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        long j = -longValue;
        bundle.putLong("chat_id", j);
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
            if (!instance.checkCanOpenChat(bundle, arrayList2.get(arrayList2.size() - 1))) {
                return;
            }
        }
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(j, tLRPC$User, 0, str, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$35(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, tLRPC$TL_error, tLObject, i, alertDialog, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r8.chat.has_geo != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0079, code lost:
        if (r10.checkCanOpenChat(r7, r11.get(r11.size() - 1)) != false) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$34(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
        /*
            r9 = this;
            boolean r0 = r9.isFinishing()
            if (r0 != 0) goto L_0x0122
            r0 = 0
            r1 = 1
            if (r10 != 0) goto L_0x00b2
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            if (r2 == 0) goto L_0x00b2
            r8 = r11
            org.telegram.tgnet.TLRPC$ChatInvite r8 = (org.telegram.tgnet.TLRPC$ChatInvite) r8
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            if (r10 == 0) goto L_0x009c
            boolean r10 = org.telegram.messenger.ChatObject.isLeftFromChat(r10)
            if (r10 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            boolean r11 = r10.kicked
            if (r11 != 0) goto L_0x009c
            java.lang.String r10 = r10.username
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 == 0) goto L_0x0033
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePeek
            if (r10 != 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Chat r10 = r8.chat
            boolean r10 = r10.has_geo
            if (r10 == 0) goto L_0x009c
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
            long r10 = r10.id
            java.lang.String r0 = "chat_id"
            r7.putLong(r0, r10)
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
            if (r10 == 0) goto L_0x0118
        L_0x007b:
            boolean[] r6 = new boolean[r1]
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2
            r10.<init>(r6)
            r13.setOnCancelListener(r10)
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r8.chat
            long r11 = r11.id
            long r11 = -r11
            org.telegram.ui.LaunchActivity$14 r0 = new org.telegram.ui.LaunchActivity$14
            r3 = r0
            r4 = r9
            r5 = r13
            r3.<init>(r5, r6, r7, r8)
            r10.ensureMessagesLoaded(r11, r14, r0)
            r1 = 0
            goto L_0x0118
        L_0x009c:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = mainFragmentsStack
            int r11 = r10.size()
            int r11 = r11 - r1
            java.lang.Object r10 = r10.get(r11)
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            org.telegram.ui.Components.JoinGroupAlert r11 = new org.telegram.ui.Components.JoinGroupAlert
            r11.<init>(r9, r8, r14, r10)
            r10.showDialog(r11)
            goto L_0x0118
        L_0x00b2:
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r11.<init>((android.content.Context) r9)
            r12 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r12 = r10.text
            java.lang.String r14 = "FLOOD_WAIT"
            boolean r12 = r12.startsWith(r14)
            if (r12 == 0) goto L_0x00da
            r10 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0109
        L_0x00da:
            java.lang.String r10 = r10.text
            java.lang.String r12 = "INVITE_HASH_EXPIRED"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00fd
            r10 = 2131625622(0x7f0e0696, float:1.8878457E38)
            java.lang.String r12 = "ExpiredLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setTitle(r10)
            r10 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.String r12 = "InviteExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0109
        L_0x00fd:
            r10 = 2131626136(0x7f0e0898, float:1.88795E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x0109:
            r10 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.String r12 = "OK"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setPositiveButton(r10, r0)
            r9.showAlertDialog(r11)
        L_0x0118:
            if (r1 == 0) goto L_0x0122
            r13.dismiss()     // Catch:{ Exception -> 0x011e }
            goto L_0x0122
        L_0x011e:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0122:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$34(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$33(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$37(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda51(this, alertDialog, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$36(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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
    public /* synthetic */ void lambda$runLinkRequest$38(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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
    public /* synthetic */ void lambda$runLinkRequest$42(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda69(this, alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda49(this, alertDialog, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$40(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda45(this, alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$39(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$47(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$41(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$44(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda44(this, alertDialog, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$43(AlertDialog alertDialog, TLObject tLObject) {
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
    public /* synthetic */ void lambda$runLinkRequest$46(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda46(this, alertDialog, tLObject, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$45(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$49(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda47(this, alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$48(org.telegram.ui.ActionBar.AlertDialog r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC$TL_wallPaper r13, org.telegram.tgnet.TLRPC$TL_error r14) {
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
            r10.lambda$runLinkRequest$47(r11)
            goto L_0x0073
        L_0x004d:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 2131625493(0x7f0e0615, float:1.8878196E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$48(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$50() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$52(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, tLObject, alertDialog, tLRPC$TL_error));
    }

    /* JADX WARNING: type inference failed for: r8v11, types: [org.telegram.tgnet.TLRPC$WallPaper] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0095 A[SYNTHETIC, Splitter:B:29:0x0095] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$51(org.telegram.tgnet.TLObject r6, org.telegram.ui.ActionBar.AlertDialog r7, org.telegram.tgnet.TLRPC$TL_error r8) {
        /*
            r5 = this;
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_theme
            r1 = 1
            if (r0 == 0) goto L_0x0084
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
            if (r8 == 0) goto L_0x0066
            java.lang.String r3 = org.telegram.ui.ActionBar.Theme.getBaseThemeKey(r8)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
            if (r3 == 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$WallPaper r8 = r8.wallpaper
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r4 == 0) goto L_0x005a
            r0 = r8
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r8 = r0.document
            java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r1)
            boolean r8 = r8.exists()
            if (r8 != 0) goto L_0x005a
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
        L_0x005a:
            r7.dismiss()     // Catch:{ Exception -> 0x005e }
            goto L_0x0062
        L_0x005e:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0062:
            r5.openThemeAccentPreview(r6, r0, r3)
            goto L_0x0093
        L_0x0066:
            org.telegram.tgnet.TLRPC$Document r8 = r6.document
            if (r8 == 0) goto L_0x0090
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
            goto L_0x0093
        L_0x0084:
            if (r8 == 0) goto L_0x0092
            java.lang.String r6 = r8.text
            java.lang.String r8 = "THEME_FORMAT_INVALID"
            boolean r6 = r8.equals(r6)
            if (r6 == 0) goto L_0x0092
        L_0x0090:
            r2 = 1
            goto L_0x0093
        L_0x0092:
            r2 = 2
        L_0x0093:
            if (r2 == 0) goto L_0x00cd
            r7.dismiss()     // Catch:{ Exception -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x009d:
            r6 = 2131628276(0x7f0e10f4, float:1.888384E38)
            java.lang.String r7 = "Theme"
            if (r2 != r1) goto L_0x00b9
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628298(0x7f0e110a, float:1.8883885E38)
            java.lang.String r8 = "ThemeNotSupported"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
            goto L_0x00cd
        L_0x00b9:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628297(0x7f0e1109, float:1.8883883E38)
            java.lang.String r8 = "ThemeNotFound"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
        L_0x00cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$51(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$54(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, tLObject, iArr, i, alertDialog, num, num2, num3));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$53(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
            r0 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$53(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$57(Bundle bundle, Long l, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = l.longValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new LaunchActivity$$ExternalSyntheticLambda72(this, alertDialog, baseFragment, i, bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$56(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda48(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$55(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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
    public static /* synthetic */ void lambda$runLinkRequest$58(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            r13 = 2131627638(0x7f0e0e76, float:1.8882546E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131627778(0x7f0e0var_, float:1.888283E38)
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
            AnonymousClass15 r0 = new FrameLayout(this) {
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
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda12(this));
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
    public /* synthetic */ void lambda$createUpdateUI$59(View view) {
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

    /* JADX WARNING: Removed duplicated region for block: B:18:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x012e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x012f  */
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
            if (r0 == 0) goto L_0x017e
            android.widget.FrameLayout r0 = r13.updateLayout
            r13.createUpdateUI()
            android.widget.TextView r6 = r13.updateSizeTextView
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r7 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            int r7 = r7.size
            long r7 = (long) r7
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r6.setText(r7)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r7 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            r8 = 1
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r8)
            boolean r7 = r7.exists()
            r9 = 0
            if (r7 == 0) goto L_0x0055
            org.telegram.ui.Components.RadialProgress2 r6 = r13.updateLayoutIcon
            r7 = 15
            r6.setIcon(r7, r8, r5)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r13.updateTextView
            r7 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.String r10 = "AppUpdateNow"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6.setText(r7)
        L_0x0053:
            r6 = 0
            goto L_0x00ad
        L_0x0055:
            int r7 = r13.currentAccount
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)
            boolean r7 = r7.isLoadingFile(r6)
            if (r7 == 0) goto L_0x0098
            org.telegram.ui.Components.RadialProgress2 r7 = r13.updateLayoutIcon
            r10 = 3
            r7.setIcon(r10, r8, r5)
            org.telegram.ui.Components.RadialProgress2 r7 = r13.updateLayoutIcon
            r7.setProgress(r9, r5)
            org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r6 = r7.getFileProgress(r6)
            org.telegram.ui.ActionBar.SimpleTextView r7 = r13.updateTextView
            r10 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.Object[] r11 = new java.lang.Object[r8]
            if (r6 == 0) goto L_0x0082
            float r6 = r6.floatValue()
            goto L_0x0083
        L_0x0082:
            r6 = 0
        L_0x0083:
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r6 = r6 * r12
            int r6 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r11[r5] = r6
            java.lang.String r6 = "AppUpdateDownloading"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r11)
            r7.setText(r6)
            goto L_0x0053
        L_0x0098:
            org.telegram.ui.Components.RadialProgress2 r6 = r13.updateLayoutIcon
            r7 = 2
            r6.setIcon(r7, r8, r5)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r13.updateTextView
            r7 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r10 = "AppUpdate"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6.setText(r7)
            r6 = 1
        L_0x00ad:
            if (r6 == 0) goto L_0x00ea
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Object r6 = r6.getTag()
            if (r6 == 0) goto L_0x0126
            r6 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x00da
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setTag(r2)
            android.widget.TextView r7 = r13.updateSizeTextView
            android.view.ViewPropertyAnimator r7 = r7.animate()
            android.view.ViewPropertyAnimator r7 = r7.alpha(r6)
            android.view.ViewPropertyAnimator r7 = r7.scaleX(r6)
            android.view.ViewPropertyAnimator r6 = r7.scaleY(r6)
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r3)
            r6.start()
            goto L_0x0126
        L_0x00da:
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setAlpha(r6)
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setScaleX(r6)
            android.widget.TextView r7 = r13.updateSizeTextView
            r7.setScaleY(r6)
            goto L_0x0126
        L_0x00ea:
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Object r6 = r6.getTag()
            if (r6 != 0) goto L_0x0126
            if (r14 == 0) goto L_0x0117
            android.widget.TextView r6 = r13.updateSizeTextView
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r6.setTag(r7)
            android.widget.TextView r6 = r13.updateSizeTextView
            android.view.ViewPropertyAnimator r6 = r6.animate()
            android.view.ViewPropertyAnimator r6 = r6.alpha(r9)
            android.view.ViewPropertyAnimator r6 = r6.scaleX(r9)
            android.view.ViewPropertyAnimator r6 = r6.scaleY(r9)
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r3)
            r6.start()
            goto L_0x0126
        L_0x0117:
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setAlpha(r9)
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setScaleX(r9)
            android.widget.TextView r6 = r13.updateSizeTextView
            r6.setScaleY(r9)
        L_0x0126:
            android.widget.FrameLayout r6 = r13.updateLayout
            java.lang.Object r6 = r6.getTag()
            if (r6 == 0) goto L_0x012f
            return
        L_0x012f:
            android.widget.FrameLayout r6 = r13.updateLayout
            r6.setVisibility(r5)
            android.widget.FrameLayout r6 = r13.updateLayout
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r6.setTag(r7)
            if (r14 == 0) goto L_0x0164
            android.widget.FrameLayout r14 = r13.updateLayout
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.translationY(r9)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r14 = r14.setInterpolator(r6)
            android.view.ViewPropertyAnimator r14 = r14.setListener(r2)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda20 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda20
            r2.<init>(r0)
            android.view.ViewPropertyAnimator r14 = r14.withEndAction(r2)
            r14.start()
            goto L_0x0174
        L_0x0164:
            android.widget.FrameLayout r14 = r13.updateLayout
            r14.setTranslationY(r9)
            if (r0 == 0) goto L_0x0174
            android.view.ViewParent r14 = r0.getParent()
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            r14.removeView(r0)
        L_0x0174:
            org.telegram.ui.Components.RecyclerListView r14 = r13.sideMenu
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r14.setPadding(r5, r5, r5, r0)
            goto L_0x01cb
        L_0x017e:
            android.widget.FrameLayout r0 = r13.updateLayout
            if (r0 == 0) goto L_0x01cb
            java.lang.Object r0 = r0.getTag()
            if (r0 != 0) goto L_0x0189
            goto L_0x01cb
        L_0x0189:
            android.widget.FrameLayout r0 = r13.updateLayout
            r0.setTag(r2)
            if (r14 == 0) goto L_0x01b6
            android.widget.FrameLayout r14 = r13.updateLayout
            android.view.ViewPropertyAnimator r14 = r14.animate()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            android.view.ViewPropertyAnimator r14 = r14.translationY(r0)
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            android.view.ViewPropertyAnimator r14 = r14.setInterpolator(r0)
            org.telegram.ui.LaunchActivity$16 r0 = new org.telegram.ui.LaunchActivity$16
            r0.<init>()
            android.view.ViewPropertyAnimator r14 = r14.setListener(r0)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r3)
            r14.start()
            goto L_0x01c6
        L_0x01b6:
            android.widget.FrameLayout r14 = r13.updateLayout
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            r14.setTranslationY(r0)
            android.widget.FrameLayout r14 = r13.updateLayout
            r0 = 4
            r14.setVisibility(r0)
        L_0x01c6:
            org.telegram.ui.Components.RecyclerListView r14 = r13.sideMenu
            r14.setPadding(r5, r5, r5, r5)
        L_0x01cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.updateAppUpdateViews(boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateAppUpdateViews$60(View view) {
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new LaunchActivity$$ExternalSyntheticLambda60(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$62(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda43(this, (TLRPC$TL_help_appUpdate) tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$61(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
            this.visibleDialog.setOnDismissListener(new LaunchActivity$$ExternalSyntheticLambda11(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAlertDialog$63(DialogInterface dialogInterface) {
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
            SendMessagesHelper.getInstance(currentAccount).prepareImportHistory(arrayList3.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new LaunchActivity$$ExternalSyntheticLambda58(this, currentAccount, dialogsActivity, z, arrayList4, uri, alertDialog));
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
                phonebookShareAlert.setDelegate(new LaunchActivity$$ExternalSyntheticLambda80(this, chatActivity, arrayList, currentAccount, charSequence, z6));
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
    public /* synthetic */ void lambda$didSelectDialogs$64(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
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
    public /* synthetic */ void lambda$didSelectDialogs$65(ChatActivity chatActivity, ArrayList arrayList, int i, CharSequence charSequence, boolean z, TLRPC$User tLRPC$User, boolean z2, int i2) {
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
    public void lambda$runLinkRequest$47(BaseFragment baseFragment) {
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
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
            if (canDrawOverlays) {
                GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                if (groupCallActivity != null) {
                    groupCallActivity.dismissInternal();
                }
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda26(this), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$66() {
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
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda19(this.currentAccount));
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
    public static /* synthetic */ void lambda$onPause$67(int i) {
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
        Utilities.stageQueue.postRunnable(LaunchActivity$$ExternalSyntheticLambda55.INSTANCE);
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
    public static /* synthetic */ void lambda$onResume$68() {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v38, resolved type: org.telegram.tgnet.TLRPC$TL_error} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v47, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v59, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v62, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v65, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v72, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v44, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v79, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v86, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v95, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v103, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v116, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v118, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v31, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v144, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v153, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v167, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v169, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v186, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v191, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v2 */
    /* JADX WARNING: type inference failed for: r4v6, types: [int] */
    /* JADX WARNING: type inference failed for: r4v13 */
    /* JADX WARNING: type inference failed for: r4v15 */
    /* JADX WARNING: type inference failed for: r4v27 */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x027c, code lost:
        if (((org.telegram.ui.ProfileActivity) r1.get(r1.size() - 1)).isSettings() == false) goto L_0x0280;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0558  */
    /* JADX WARNING: Removed duplicated region for block: B:366:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x026b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r8 = r16
            r0 = r17
            r1 = r18
            r2 = r19
            int r3 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r0 != r3) goto L_0x0011
            r16.switchToAvailableAccountOrLogout()
            goto L_0x0893
        L_0x0011:
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r4 = 0
            if (r0 != r3) goto L_0x0022
            r0 = r2[r4]
            if (r0 == r8) goto L_0x0893
            r16.onFinish()
            r16.finish()
            goto L_0x0893
        L_0x0022:
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r3) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r2 = r8.currentConnectionState
            if (r2 == r0) goto L_0x0893
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
            goto L_0x0893
        L_0x0051:
            int r3 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r3) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r8.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x0893
        L_0x005c:
            int r3 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r6 = "Cancel"
            r7 = 5
            r9 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r10 = "AppName"
            r11 = 4
            r12 = 3
            r13 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.String r14 = "OK"
            r15 = 2
            r5 = 1
            if (r0 != r3) goto L_0x019c
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r3 = r0.intValue()
            r4 = 6
            if (r3 == r4) goto L_0x019b
            int r3 = r0.intValue()
            if (r3 != r12) goto L_0x0088
            org.telegram.ui.ActionBar.AlertDialog r3 = r8.proxyErrorDialog
            if (r3 == 0) goto L_0x0088
            goto L_0x019b
        L_0x0088:
            int r3 = r0.intValue()
            if (r3 != r11) goto L_0x0096
            r0 = r2[r5]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = (org.telegram.tgnet.TLRPC$TL_help_termsOfService) r0
            r8.showTosActivity(r1, r0)
            return
        L_0x0096:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r3.<init>((android.content.Context) r8)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r3.setTitle(r4)
            int r4 = r0.intValue()
            if (r4 == r15) goto L_0x00bf
            int r4 = r0.intValue()
            if (r4 == r12) goto L_0x00bf
            r4 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            java.lang.String r9 = "MoreInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3
            r9.<init>(r1)
            r3.setNegativeButton(r4, r9)
        L_0x00bf:
            int r1 = r0.intValue()
            if (r1 != r7) goto L_0x00db
            r0 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            java.lang.String r1 = "NobodyLikesSpam3"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            goto L_0x017d
        L_0x00db:
            r1 = 0
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x00f7
            r0 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            java.lang.String r2 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r1)
            goto L_0x017d
        L_0x00f7:
            int r4 = r0.intValue()
            if (r4 != r5) goto L_0x0111
            r0 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r2 = "NobodyLikesSpam2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r1)
            goto L_0x017d
        L_0x0111:
            int r1 = r0.intValue()
            if (r1 != r15) goto L_0x0150
            r0 = r2[r5]
            java.lang.String r0 = (java.lang.String) r0
            r3.setMessage(r0)
            r0 = r2[r15]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = "AUTH_KEY_DROP_"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0147
            r0 = 2131624707(0x7f0e0303, float:1.8876601E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            r0 = 2131626273(0x7f0e0921, float:1.8879778E38)
            java.lang.String r1 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8
            r1.<init>(r8)
            r3.setNegativeButton(r0, r1)
            goto L_0x017d
        L_0x0147:
            r1 = 0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r1)
            goto L_0x017d
        L_0x0150:
            int r0 = r0.intValue()
            if (r0 != r12) goto L_0x017d
            r0 = 2131627471(0x7f0e0dcf, float:1.8882207E38)
            java.lang.String r1 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setTitle(r0)
            r0 = 2131628460(0x7f0e11ac, float:1.8884213E38)
            java.lang.String r1 = "UseProxyTelegramError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.showAlertDialog(r3)
            r8.proxyErrorDialog = r0
            return
        L_0x017d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0893
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r3.create()
            r0.showDialog(r1)
            goto L_0x0893
        L_0x019b:
            return
        L_0x019c:
            int r3 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r0 != r3) goto L_0x01f3
            r0 = r2[r4]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>((android.content.Context) r8)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r2.setTitle(r3)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r4 = 0
            r2.setPositiveButton(r3, r4)
            r3 = 2131627991(0x7f0e0fd7, float:1.8883262E38)
            java.lang.String r4 = "ShareYouLocationUnableManually"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9
            r4.<init>(r8, r0, r1)
            r2.setNegativeButton(r3, r4)
            r0 = 2131627990(0x7f0e0fd6, float:1.888326E38)
            java.lang.String r1 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0893
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r2.create()
            r0.showDialog(r1)
            goto L_0x0893
        L_0x01f3:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r0 != r3) goto L_0x0215
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0204
            android.view.View r0 = r0.getChildAt(r4)
            if (r0 == 0) goto L_0x0204
            r0.invalidate()
        L_0x0204:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r8.backgroundTablet
            if (r0 == 0) goto L_0x0893
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            goto L_0x0893
        L_0x0215:
            int r3 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r0 != r3) goto L_0x024b
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r1 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x0236
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0236
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0230 }
            r0.setFlags(r1, r1)     // Catch:{ Exception -> 0x0230 }
            goto L_0x0893
        L_0x0230:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0893
        L_0x0236:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x0893
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0245 }
            r0.clearFlags(r1)     // Catch:{ Exception -> 0x0245 }
            goto L_0x0893
        L_0x0245:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0893
        L_0x024b:
            int r3 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r0 != r3) goto L_0x0285
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r5) goto L_0x0268
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0268
            r0 = 1
            goto L_0x0269
        L_0x0268:
            r0 = 0
        L_0x0269:
            if (r0 == 0) goto L_0x027f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r5
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ProfileActivity r1 = (org.telegram.ui.ProfileActivity) r1
            boolean r1 = r1.isSettings()
            if (r1 != 0) goto L_0x027f
            goto L_0x0280
        L_0x027f:
            r4 = r0
        L_0x0280:
            r8.rebuildAllFragments(r4)
            goto L_0x0893
        L_0x0285:
            int r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r3) goto L_0x028e
            r8.showLanguageAlert(r4)
            goto L_0x0893
        L_0x028e:
            int r3 = org.telegram.messenger.NotificationCenter.openArticle
            if (r0 != r3) goto L_0x02c0
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x029b
            return
        L_0x029b:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r5
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.setParentActivity(r8, r1)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r1 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_webPage r1 = (org.telegram.tgnet.TLRPC$TL_webPage) r1
            r2 = r2[r5]
            java.lang.String r2 = (java.lang.String) r2
            r0.open(r1, r2)
            goto L_0x0893
        L_0x02c0:
            int r3 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r3) goto L_0x0357
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            if (r0 == 0) goto L_0x0356
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02d2
            goto L_0x0356
        L_0x02d2:
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            r0.intValue()
            r0 = r2[r5]
            java.util.HashMap r0 = (java.util.HashMap) r0
            r3 = r2[r15]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r2 = r2[r12]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = r7.fragmentsStack
            int r9 = r7.size()
            int r9 = r9 - r5
            java.lang.Object r5 = r7.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r7.<init>((android.content.Context) r8)
            r9 = 2131558487(0x7f0d0057, float:1.8742291E38)
            r10 = 72
            java.lang.String r11 = "dialogTopBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.setTopAnimation(r9, r10, r4, r11)
            r9 = 2131628427(0x7f0e118b, float:1.8884146E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setTitle(r9)
            r9 = 2131628426(0x7f0e118a, float:1.8884144E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6
            r10.<init>(r1, r0, r3, r2)
            r7.setPositiveButton(r9, r10)
            r9 = 2131624707(0x7f0e0303, float:1.8876601E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4
            r9.<init>(r1, r0, r3, r2)
            r7.setNegativeButton(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5
            r6.<init>(r1, r0, r3, r2)
            r7.setOnBackButtonListener(r6)
            org.telegram.ui.ActionBar.AlertDialog r0 = r7.create()
            r5.showDialog(r0)
            r0.setCanceledOnTouchOutside(r4)
            goto L_0x0893
        L_0x0356:
            return
        L_0x0357:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r6 = 21
            if (r0 != r3) goto L_0x03cd
            r0 = r2[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x03a9
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0391
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
        L_0x0391:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x03a9
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x03a8 }
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)     // Catch:{ Exception -> 0x03a8 }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1 = r1 | r3
            r3 = 0
            r0.<init>(r3, r3, r1)     // Catch:{ Exception -> 0x03a8 }
            r8.setTaskDescription(r0)     // Catch:{ Exception -> 0x03a8 }
            goto L_0x03a9
        L_0x03a8:
        L_0x03a9:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBehindKeyboardColor(r1)
            int r0 = r2.length
            if (r0 <= r5) goto L_0x03c0
            r0 = r2[r5]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            goto L_0x03c1
        L_0x03c0:
            r0 = 1
        L_0x03c1:
            if (r0 == 0) goto L_0x03c8
            boolean r0 = r8.isNavigationBarColorFrozen
            if (r0 != 0) goto L_0x03c8
            r4 = 1
        L_0x03c8:
            r8.checkSystemBarColors(r5, r4)
            goto L_0x0893
        L_0x03cd:
            int r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r3) goto L_0x0564
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x0536
            r0 = r2[r15]
            if (r0 == 0) goto L_0x0536
            android.widget.ImageView r0 = r8.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03e2
            return
        L_0x03e2:
            r0 = r2[r15]     // Catch:{ all -> 0x051d }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x051d }
            r1 = r2[r11]     // Catch:{ all -> 0x051d }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x051d }
            boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x051d }
            r3 = r2[r7]     // Catch:{ all -> 0x051d }
            org.telegram.ui.Components.RLottieImageView r3 = (org.telegram.ui.Components.RLottieImageView) r3     // Catch:{ all -> 0x051d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x051d }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x051d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x051d }
            int r7 = r7.getMeasuredHeight()     // Catch:{ all -> 0x051d }
            if (r1 != 0) goto L_0x0403
            r3.setVisibility(r11)     // Catch:{ all -> 0x051d }
        L_0x0403:
            r8.isNavigationBarColorFrozen = r5     // Catch:{ all -> 0x051d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x051d }
            r8.invalidateCachedViews(r9)     // Catch:{ all -> 0x051d }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x051d }
            android.graphics.Bitmap r9 = org.telegram.messenger.AndroidUtilities.snapshotView(r9)     // Catch:{ all -> 0x051d }
            android.widget.FrameLayout r10 = r8.frameLayout     // Catch:{ all -> 0x051d }
            android.widget.ImageView r11 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
            r10.removeView(r11)     // Catch:{ all -> 0x051d }
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r1 == 0) goto L_0x042f
            android.widget.FrameLayout r13 = r8.frameLayout     // Catch:{ all -> 0x051d }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x051d }
            r13.addView(r14, r4, r10)     // Catch:{ all -> 0x051d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x051d }
            r11 = 8
            r10.setVisibility(r11)     // Catch:{ all -> 0x051d }
            goto L_0x0460
        L_0x042f:
            android.widget.FrameLayout r13 = r8.frameLayout     // Catch:{ all -> 0x051d }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x051d }
            r13.addView(r14, r5, r10)     // Catch:{ all -> 0x051d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x051d }
            r11 = r0[r4]     // Catch:{ all -> 0x051d }
            r13 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x051d }
            int r11 = r11 - r14
            float r11 = (float) r11     // Catch:{ all -> 0x051d }
            r10.setTranslationX(r11)     // Catch:{ all -> 0x051d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x051d }
            r11 = r0[r5]     // Catch:{ all -> 0x051d }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x051d }
            int r11 = r11 - r13
            float r11 = (float) r11     // Catch:{ all -> 0x051d }
            r10.setTranslationY(r11)     // Catch:{ all -> 0x051d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x051d }
            r10.setVisibility(r4)     // Catch:{ all -> 0x051d }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x051d }
            r10.invalidate()     // Catch:{ all -> 0x051d }
        L_0x0460:
            android.widget.ImageView r10 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
            r10.setImageBitmap(r9)     // Catch:{ all -> 0x051d }
            android.widget.ImageView r9 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
            r9.setVisibility(r4)     // Catch:{ all -> 0x051d }
            org.telegram.ui.Components.RLottieDrawable r9 = r3.getAnimatedDrawable()     // Catch:{ all -> 0x051d }
            r8.themeSwitchSunDrawable = r9     // Catch:{ all -> 0x051d }
            r9 = r0[r4]     // Catch:{ all -> 0x051d }
            int r9 = r6 - r9
            r10 = r0[r4]     // Catch:{ all -> 0x051d }
            int r10 = r6 - r10
            int r9 = r9 * r10
            r10 = r0[r5]     // Catch:{ all -> 0x051d }
            int r10 = r7 - r10
            r11 = r0[r5]     // Catch:{ all -> 0x051d }
            int r11 = r7 - r11
            int r10 = r10 * r11
            int r9 = r9 + r10
            double r9 = (double) r9     // Catch:{ all -> 0x051d }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x051d }
            r11 = r0[r4]     // Catch:{ all -> 0x051d }
            r13 = r0[r4]     // Catch:{ all -> 0x051d }
            int r11 = r11 * r13
            r13 = r0[r5]     // Catch:{ all -> 0x051d }
            int r13 = r7 - r13
            r14 = r0[r5]     // Catch:{ all -> 0x051d }
            int r14 = r7 - r14
            int r13 = r13 * r14
            int r11 = r11 + r13
            double r13 = (double) r11     // Catch:{ all -> 0x051d }
            double r13 = java.lang.Math.sqrt(r13)     // Catch:{ all -> 0x051d }
            double r9 = java.lang.Math.max(r9, r13)     // Catch:{ all -> 0x051d }
            float r9 = (float) r9     // Catch:{ all -> 0x051d }
            r10 = r0[r4]     // Catch:{ all -> 0x051d }
            int r10 = r6 - r10
            r11 = r0[r4]     // Catch:{ all -> 0x051d }
            int r6 = r6 - r11
            int r10 = r10 * r6
            r6 = r0[r5]     // Catch:{ all -> 0x051d }
            r11 = r0[r5]     // Catch:{ all -> 0x051d }
            int r6 = r6 * r11
            int r10 = r10 + r6
            double r10 = (double) r10     // Catch:{ all -> 0x051d }
            double r10 = java.lang.Math.sqrt(r10)     // Catch:{ all -> 0x051d }
            r6 = r0[r4]     // Catch:{ all -> 0x051d }
            r13 = r0[r4]     // Catch:{ all -> 0x051d }
            int r6 = r6 * r13
            r13 = r0[r5]     // Catch:{ all -> 0x051d }
            r14 = r0[r5]     // Catch:{ all -> 0x051d }
            int r13 = r13 * r14
            int r6 = r6 + r13
            double r13 = (double) r6     // Catch:{ all -> 0x051d }
            double r13 = java.lang.Math.sqrt(r13)     // Catch:{ all -> 0x051d }
            double r10 = java.lang.Math.max(r10, r13)     // Catch:{ all -> 0x051d }
            float r6 = (float) r10     // Catch:{ all -> 0x051d }
            float r6 = java.lang.Math.max(r9, r6)     // Catch:{ all -> 0x051d }
            if (r1 == 0) goto L_0x04da
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x051d }
            goto L_0x04dc
        L_0x04da:
            android.widget.ImageView r9 = r8.themeSwitchImageView     // Catch:{ all -> 0x051d }
        L_0x04dc:
            r10 = r0[r4]     // Catch:{ all -> 0x051d }
            r11 = r0[r5]     // Catch:{ all -> 0x051d }
            r13 = 0
            if (r1 == 0) goto L_0x04e5
            r14 = 0
            goto L_0x04e6
        L_0x04e5:
            r14 = r6
        L_0x04e6:
            if (r1 == 0) goto L_0x04e9
            goto L_0x04ea
        L_0x04e9:
            r6 = 0
        L_0x04ea:
            android.animation.Animator r6 = android.view.ViewAnimationUtils.createCircularReveal(r9, r10, r11, r14, r6)     // Catch:{ all -> 0x051d }
            r9 = 400(0x190, double:1.976E-321)
            r6.setDuration(r9)     // Catch:{ all -> 0x051d }
            android.view.animation.Interpolator r9 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x051d }
            r6.setInterpolator(r9)     // Catch:{ all -> 0x051d }
            org.telegram.ui.LaunchActivity$17 r9 = new org.telegram.ui.LaunchActivity$17     // Catch:{ all -> 0x051d }
            r9.<init>(r1, r3)     // Catch:{ all -> 0x051d }
            r6.addListener(r9)     // Catch:{ all -> 0x051d }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda23 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda23     // Catch:{ all -> 0x051d }
            r3.<init>(r8)     // Catch:{ all -> 0x051d }
            if (r1 == 0) goto L_0x0513
            r0 = r0[r5]     // Catch:{ all -> 0x051d }
            int r7 = r7 - r0
            r0 = 1074790400(0x40100000, float:2.25)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x051d }
            int r7 = r7 / r0
            long r0 = (long) r7     // Catch:{ all -> 0x051d }
            goto L_0x0515
        L_0x0513:
            r0 = 50
        L_0x0515:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r0)     // Catch:{ all -> 0x051d }
            r6.start()     // Catch:{ all -> 0x051d }
            r0 = 1
            goto L_0x0539
        L_0x051d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x0531 }
            r1 = 0
            r0.setImageDrawable(r1)     // Catch:{ Exception -> 0x0531 }
            android.widget.FrameLayout r0 = r8.frameLayout     // Catch:{ Exception -> 0x0531 }
            android.widget.ImageView r1 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x0531 }
            r0.removeView(r1)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4     // Catch:{ Exception -> 0x0531 }
            goto L_0x0538
        L_0x0531:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0538
        L_0x0536:
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4
        L_0x0538:
            r0 = 0
        L_0x0539:
            r1 = r2[r4]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r1
            r3 = r2[r5]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r2 = r2[r12]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.actionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x0893
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.layersActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.rightActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            goto L_0x0893
        L_0x0564:
            int r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r3) goto L_0x0595
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0893
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r0 = r0.getChildCount()
        L_0x0574:
            if (r4 >= r0) goto L_0x0893
            org.telegram.ui.Components.RecyclerListView r2 = r8.sideMenu
            android.view.View r2 = r2.getChildAt(r4)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r3 == 0) goto L_0x0592
            r3 = r2
            org.telegram.ui.Cells.DrawerUserCell r3 = (org.telegram.ui.Cells.DrawerUserCell) r3
            int r3 = r3.getAccountNumber()
            int r5 = r1.intValue()
            if (r3 != r5) goto L_0x0592
            r2.invalidate()
            goto L_0x0893
        L_0x0592:
            int r4 = r4 + 1
            goto L_0x0574
        L_0x0595:
            int r3 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r3) goto L_0x05a4
            r0 = r2[r4]     // Catch:{ all -> 0x0893 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x0893 }
            r1 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r8, r1)     // Catch:{ all -> 0x0893 }
            goto L_0x0893
        L_0x05a4:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoaded
            if (r0 != r3) goto L_0x067a
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x05c3
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x05c3
            r8.updateAppUpdateViews(r5)
        L_0x05c3:
            java.lang.String r1 = r8.loadingThemeFileName
            if (r1 == 0) goto L_0x0649
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0893
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
            if (r1 == 0) goto L_0x0644
            java.lang.String r2 = r1.pathToWallpaper
            if (r2 == 0) goto L_0x062d
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r1.pathToWallpaper
            r2.<init>(r3)
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x062d
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r1.account
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73
            r3.<init>(r8, r1)
            r2.sendRequest(r0, r3)
            return
        L_0x062d:
            org.telegram.tgnet.TLRPC$TL_theme r1 = r8.loadingTheme
            java.lang.String r2 = r1.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r2, r1, r5)
            if (r10 == 0) goto L_0x0644
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r11 = 1
            r12 = 0
            r13 = 0
            r14 = 0
            r9 = r0
            r9.<init>(r10, r11, r12, r13, r14)
            r8.lambda$runLinkRequest$47(r0)
        L_0x0644:
            r16.onThemeLoadFinish()
            goto L_0x0893
        L_0x0649:
            java.lang.String r1 = r8.loadingThemeWallpaperName
            if (r1 == 0) goto L_0x0893
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0893
            r1 = 0
            r8.loadingThemeWallpaperName = r1
            r0 = r2[r5]
            java.io.File r0 = (java.io.File) r0
            boolean r1 = r8.loadingThemeAccent
            if (r1 == 0) goto L_0x066c
            org.telegram.tgnet.TLRPC$TL_theme r0 = r8.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r8.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r8.loadingThemeInfo
            r8.openThemeAccentPreview(r0, r1, r2)
            r16.onThemeLoadFinish()
            goto L_0x0893
        L_0x066c:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r8.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda53 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda53
            r3.<init>(r8, r1, r0)
            r2.postRunnable(r3)
            goto L_0x0893
        L_0x067a:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoadFailed
            if (r0 != r3) goto L_0x06ae
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r8.loadingThemeFileName
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x0692
            java.lang.String r1 = r8.loadingThemeWallpaperName
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0695
        L_0x0692:
            r16.onThemeLoadFinish()
        L_0x0695:
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x0893
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0893
            r8.updateAppUpdateViews(r5)
            goto L_0x0893
        L_0x06ae:
            int r3 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r3) goto L_0x06c5
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x06b7
            return
        L_0x06b7:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x06c0
            r16.onPasscodeResume()
            goto L_0x0893
        L_0x06c0:
            r16.onPasscodePause()
            goto L_0x0893
        L_0x06c5:
            int r3 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r3) goto L_0x06ce
            r16.lambda$onCreate$5()
            goto L_0x0893
        L_0x06ce:
            int r3 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r0 != r3) goto L_0x06fb
            int r0 = r2.length
            if (r0 <= r5) goto L_0x0893
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0893
            int r0 = r8.currentAccount
            r1 = r2[r15]
            org.telegram.tgnet.TLRPC$TL_error r1 = (org.telegram.tgnet.TLRPC$TL_error) r1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r6 = r3.size()
            int r6 = r6 - r5
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r2 = r2[r5]
            org.telegram.tgnet.TLObject r2 = (org.telegram.tgnet.TLObject) r2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.ui.Components.AlertsCreator.processError(r0, r1, r3, r2, r4)
            goto L_0x0893
        L_0x06fb:
            int r3 = org.telegram.messenger.NotificationCenter.stickersImportComplete
            if (r0 != r3) goto L_0x072a
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r18)
            r0 = r2[r4]
            r3 = r0
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r4 = 2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0720
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r5 = r0
            goto L_0x0721
        L_0x0720:
            r5 = 0
        L_0x0721:
            r6 = 0
            r7 = 1
            r2 = r16
            r1.toggleStickerSet(r2, r3, r4, r5, r6, r7)
            goto L_0x0893
        L_0x072a:
            int r1 = org.telegram.messenger.NotificationCenter.newSuggestionsAvailable
            if (r0 != r1) goto L_0x0735
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            r0.invalidateViews()
            goto L_0x0893
        L_0x0735:
            int r1 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r0 != r1) goto L_0x0825
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0893
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            boolean r1 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r1 == 0) goto L_0x0757
            org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r1 == 0) goto L_0x0757
            android.widget.FrameLayout r1 = r1.getContainer()
            r7 = r1
            goto L_0x0758
        L_0x0757:
            r7 = 0
        L_0x0758:
            if (r7 != 0) goto L_0x0769
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r5
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r9 = r1
            goto L_0x076a
        L_0x0769:
            r9 = 0
        L_0x076a:
            r3 = 0
            if (r0 != r12) goto L_0x079e
            r0 = r2[r5]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0780
            r0 = 2131628952(0x7f0e1398, float:1.8885211E38)
            java.lang.String r1 = "YourNameChanged"
            goto L_0x0785
        L_0x0780:
            r0 = 2131624846(0x7f0e038e, float:1.8876883E38)
            java.lang.String r1 = "CannelTitleChanged"
        L_0x0785:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x0791
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x0795
        L_0x0791:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x0795:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x0893
        L_0x079e:
            if (r0 != r15) goto L_0x07d0
            r0 = r2[r5]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x07b2
            r0 = 2131628935(0x7f0e1387, float:1.8885177E38)
            java.lang.String r1 = "YourBioChanged"
            goto L_0x07b7
        L_0x07b2:
            r0 = 2131624785(0x7f0e0351, float:1.887676E38)
            java.lang.String r1 = "CannelDescriptionChanged"
        L_0x07b7:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x07c3
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x07c7
        L_0x07c3:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x07c7:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x0893
        L_0x07d0:
            if (r0 != 0) goto L_0x07ff
            r0 = r2[r5]
            r5 = r0
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            org.telegram.ui.Components.StickerSetBulletinLayout r0 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = 0
            r1 = r2[r15]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r4 = r1.intValue()
            r6 = 0
            r1 = r0
            r2 = r16
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 1500(0x5dc, float:2.102E-42)
            if (r9 == 0) goto L_0x07f6
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r1)
            r0.show()
            goto L_0x0893
        L_0x07f6:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r1)
            r0.show()
            goto L_0x0893
        L_0x07ff:
            if (r0 != r5) goto L_0x0893
            if (r9 == 0) goto L_0x0814
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r9)
            r1 = r2[r5]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0893
        L_0x0814:
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r1 = r2[r5]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0893
        L_0x0825:
            int r1 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r0 != r1) goto L_0x082d
            r8.checkWasMutedByAdmin(r4)
            goto L_0x0893
        L_0x082d:
            int r1 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r0 != r1) goto L_0x0883
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.updateTextView
            if (r0 == 0) goto L_0x0893
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x0893
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            if (r1 == 0) goto L_0x0893
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0893
            r0 = r2[r5]
            java.lang.Long r0 = (java.lang.Long) r0
            r1 = r2[r15]
            java.lang.Long r1 = (java.lang.Long) r1
            long r2 = r0.longValue()
            float r0 = (float) r2
            long r1 = r1.longValue()
            float r1 = (float) r1
            float r0 = r0 / r1
            org.telegram.ui.Components.RadialProgress2 r1 = r8.updateLayoutIcon
            r1.setProgress(r0, r5)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r8.updateTextView
            r2 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r5
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3[r4] = r0
            java.lang.String r0 = "AppUpdateDownloading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            r1.setText(r0)
            goto L_0x0893
        L_0x0883:
            int r1 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r0 != r1) goto L_0x0893
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 != r5) goto L_0x0890
            r4 = 1
        L_0x0890:
            r8.updateAppUpdateViews(r4)
        L_0x0893:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$69(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$70(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$72(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda89(hashMap, i));
                lambda$runLinkRequest$47(locationActivity);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$71(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$76() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$78(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda40(this, tLObject, themeInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$77(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    public /* synthetic */ void lambda$didReceivedNotification$80(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda27(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$79() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$47(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$47(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
            Utilities.globalQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda28(this), 2000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$82() {
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
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda31(this));
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$81() {
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
            r9 = 2131624995(0x7f0e0423, float:1.8877185E38)
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
            r14 = 2131625477(0x7f0e0605, float:1.8878163E38)
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda14 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda14     // Catch:{ Exception -> 0x0122 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0122 }
            r5.setOnClickListener(r13)     // Catch:{ Exception -> 0x0122 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00c0:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0122 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0122 }
            r5 = 2131624996(0x7f0e0424, float:1.8877187E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0122 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0122 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0122 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0122 }
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0122 }
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r0, r8)     // Catch:{ Exception -> 0x0122 }
            r3.setBackground(r0)     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13     // Catch:{ Exception -> 0x0122 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0122 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0122 }
            r0 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r0)     // Catch:{ Exception -> 0x0122 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0122 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0122 }
            java.lang.String r0 = "OK"
            r2 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0122 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda10     // Catch:{ Exception -> 0x0122 }
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
    public static /* synthetic */ void lambda$showLanguageAlertInternal$83(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$84(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$47(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$85(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
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
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new LaunchActivity$$ExternalSyntheticLambda76(this, localeInfoArr, str2), 8);
                                TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                                tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                                tLRPC$TL_langpack_getStrings2.keys.add("English");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                                tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new LaunchActivity$$ExternalSyntheticLambda77(this, localeInfoArr, str2), 8);
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
    public /* synthetic */ void lambda$showLanguageAlert$87(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda34(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$86(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$89(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda35(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$88(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass18 r0 = new Runnable() {
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
            LaunchActivity$$ExternalSyntheticLambda29 launchActivity$$ExternalSyntheticLambda29 = null;
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
                launchActivity$$ExternalSyntheticLambda29 = new LaunchActivity$$ExternalSyntheticLambda29(this);
            }
            this.actionBarLayout.setTitleOverlayText(str, i2, launchActivity$$ExternalSyntheticLambda29);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$updateCurrentConnectionState$90() {
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
            r2.lambda$runLinkRequest$47(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$90():void");
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
