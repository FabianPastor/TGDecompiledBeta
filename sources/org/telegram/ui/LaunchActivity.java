package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
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
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final String EXTRA_ACTION_TOKEN = "actions.fulfillment.extra.ACTION_TOKEN";
    private static final int PLAY_SERVICES_REQUEST_CHECK_SETTINGS = 140;
    public static final int SCREEN_CAPTURE_REQUEST_CODE = 520;
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
    public ArrayList<TLRPC.User> contactsToSend;
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
    private boolean navigateToPremiumBot;
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

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
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
            int r1 = r13.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x0088
            android.content.Intent r1 = r13.getIntent()
            r2 = 0
            if (r1 == 0) goto L_0x0088
            java.lang.String r3 = r1.getAction()
            if (r3 == 0) goto L_0x0088
            java.lang.String r3 = r1.getAction()
            java.lang.String r4 = "android.intent.action.SEND"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x0081
            java.lang.String r3 = r1.getAction()
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0046
            goto L_0x0081
        L_0x0046:
            java.lang.String r3 = r1.getAction()
            java.lang.String r4 = "android.intent.action.VIEW"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0088
            android.net.Uri r3 = r1.getData()
            if (r3 == 0) goto L_0x0088
            java.lang.String r2 = r3.toString()
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r4 = "tg:proxy"
            boolean r4 = r2.startsWith(r4)
            if (r4 != 0) goto L_0x0080
            java.lang.String r4 = "tg://proxy"
            boolean r4 = r2.startsWith(r4)
            if (r4 != 0) goto L_0x0080
            java.lang.String r4 = "tg:socks"
            boolean r4 = r2.startsWith(r4)
            if (r4 != 0) goto L_0x0080
            java.lang.String r4 = "tg://socks"
            boolean r4 = r2.startsWith(r4)
            if (r4 == 0) goto L_0x0088
        L_0x0080:
            goto L_0x0088
        L_0x0081:
            super.onCreate(r14)
            r13.finish()
            return
        L_0x0088:
            r1 = 1
            r13.requestWindowFeature(r1)
            r2 = 2131689489(0x7f0var_, float:1.9007995E38)
            r13.setTheme(r2)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 0
            r4 = 21
            if (r2 < r4) goto L_0x00b5
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r5 = new android.app.ActivityManager$TaskDescription     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = "actionBarDefault"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ all -> 0x00ab }
            r6 = r6 | r2
            r5.<init>(r3, r3, r6)     // Catch:{ all -> 0x00ab }
            r13.setTaskDescription(r5)     // Catch:{ all -> 0x00ab }
            goto L_0x00ac
        L_0x00ab:
            r5 = move-exception
        L_0x00ac:
            android.view.Window r5 = r13.getWindow()     // Catch:{ all -> 0x00b4 }
            r5.setNavigationBarColor(r2)     // Catch:{ all -> 0x00b4 }
            goto L_0x00b5
        L_0x00b4:
            r2 = move-exception
        L_0x00b5:
            android.view.Window r2 = r13.getWindow()
            r5 = 2131166187(0x7var_eb, float:1.7946612E38)
            r2.setBackgroundDrawableResource(r5)
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x00d9
            boolean r2 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r2 != 0) goto L_0x00d9
            android.view.Window r2 = r13.getWindow()     // Catch:{ Exception -> 0x00d5 }
            r5 = 8192(0x2000, float:1.14794E-41)
            r2.setFlags(r5, r5)     // Catch:{ Exception -> 0x00d5 }
            goto L_0x00d9
        L_0x00d5:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00d9:
            super.onCreate(r14)
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 24
            if (r2 < r5) goto L_0x00e8
            boolean r2 = r13.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r2
        L_0x00e8:
            org.telegram.ui.ActionBar.Theme.createCommonChatResources()
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r13)
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash
            int r2 = r2.length()
            if (r2 == 0) goto L_0x0104
            boolean r2 = org.telegram.messenger.SharedConfig.appLocked
            if (r2 == 0) goto L_0x0104
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            int r2 = (int) r6
            org.telegram.messenger.SharedConfig.lastPauseTime = r2
        L_0x0104:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r13)
            org.telegram.ui.LaunchActivity$1 r2 = new org.telegram.ui.LaunchActivity$1
            r2.<init>(r13)
            r13.actionBarLayout = r2
            org.telegram.ui.LaunchActivity$2 r2 = new org.telegram.ui.LaunchActivity$2
            r2.<init>(r13)
            r13.frameLayout = r2
            android.view.ViewGroup$LayoutParams r6 = new android.view.ViewGroup$LayoutParams
            r7 = -1
            r6.<init>(r7, r7)
            r13.setContentView(r2, r6)
            int r2 = android.os.Build.VERSION.SDK_INT
            r6 = 8
            if (r2 < r4) goto L_0x012e
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r13)
            r13.themeSwitchImageView = r2
            r2.setVisibility(r6)
        L_0x012e:
            org.telegram.ui.LaunchActivity$3 r2 = new org.telegram.ui.LaunchActivity$3
            r2.<init>(r13)
            r13.drawerLayoutContainer = r2
            java.lang.String r8 = "windowBackgroundWhite"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r2.setBehindKeyboardColor(r8)
            android.widget.FrameLayout r2 = r13.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r13.drawerLayoutContainer
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r2.addView(r8, r10)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r4) goto L_0x0168
            org.telegram.ui.LaunchActivity$4 r2 = new org.telegram.ui.LaunchActivity$4
            r2.<init>(r13)
            r13.themeSwitchSunView = r2
            android.widget.FrameLayout r4 = r13.frameLayout
            r8 = 48
            r10 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r4.addView(r2, r8)
            android.view.View r2 = r13.themeSwitchSunView
            r2.setVisibility(r6)
        L_0x0168:
            android.widget.FrameLayout r2 = r13.frameLayout
            org.telegram.ui.Components.FireworksOverlay r4 = new org.telegram.ui.Components.FireworksOverlay
            r4.<init>(r13)
            r13.fireworksOverlay = r4
            r2.addView(r4)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r4 = 0
            if (r2 == 0) goto L_0x025d
            android.view.Window r2 = r13.getWindow()
            r8 = 16
            r2.setSoftInputMode(r8)
            org.telegram.ui.LaunchActivity$5 r2 = new org.telegram.ui.LaunchActivity$5
            r2.<init>(r13)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r8.addView(r2, r10)
            org.telegram.ui.LaunchActivity$6 r8 = new org.telegram.ui.LaunchActivity$6
            r8.<init>(r13)
            r13.backgroundTablet = r8
            r8.setOccupyStatusBar(r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r8 = r13.backgroundTablet
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r11 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r8.setBackgroundImage(r10, r11)
            org.telegram.ui.Components.SizeNotifierFrameLayout r8 = r13.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createRelative(r7, r7)
            r2.addView(r8, r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.actionBarLayout
            r2.addView(r8)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = new org.telegram.ui.ActionBar.ActionBarLayout
            r8.<init>(r13)
            r13.rightActionBarLayout = r8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = rightFragmentsStack
            r8.init(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.rightActionBarLayout
            r8.setDelegate(r13)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.rightActionBarLayout
            r2.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r13)
            r13.shadowTabletSide = r8
            r10 = 1076449908(0x40295274, float:2.6456575)
            r8.setBackgroundColor(r10)
            android.widget.FrameLayout r8 = r13.shadowTabletSide
            r2.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r13)
            r13.shadowTablet = r8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = layerFragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x01f1
            r10 = 8
            goto L_0x01f2
        L_0x01f1:
            r10 = 0
        L_0x01f2:
            r8.setVisibility(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            r10 = 2130706432(0x7var_, float:1.7014118E38)
            r8.setBackgroundColor(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            r2.addView(r8)
            android.widget.FrameLayout r8 = r13.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda14 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda14
            r10.<init>(r13)
            r8.setOnTouchListener(r10)
            android.widget.FrameLayout r8 = r13.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13 r10 = org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13.INSTANCE
            r8.setOnClickListener(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = new org.telegram.ui.ActionBar.ActionBarLayout
            r8.<init>(r13)
            r13.layersActionBarLayout = r8
            r8.setRemoveActionBarExtraHeight(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            android.widget.FrameLayout r10 = r13.shadowTablet
            r8.setBackgroundView(r10)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            r8.setUseAlphaAnimations(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r13.layersActionBarLayout
            r10 = 2131165291(0x7var_b, float:1.7944795E38)
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
            if (r10 == 0) goto L_0x024e
            goto L_0x024f
        L_0x024e:
            r6 = 0
        L_0x024f:
            r8.setVisibility(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r13.layersActionBarLayout
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r13.layersActionBarLayout
            r2.addView(r6)
            goto L_0x0269
        L_0x025d:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r13.actionBarLayout
            android.view.ViewGroup$LayoutParams r8 = new android.view.ViewGroup$LayoutParams
            r8.<init>(r7, r7)
            r2.addView(r6, r8)
        L_0x0269:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r13)
            r13.sideMenuContainer = r2
            org.telegram.ui.LaunchActivity$7 r2 = new org.telegram.ui.LaunchActivity$7
            r2.<init>(r13)
            r13.sideMenu = r2
            org.telegram.ui.Components.SideMenultItemAnimator r2 = new org.telegram.ui.Components.SideMenultItemAnimator
            org.telegram.ui.Components.RecyclerListView r6 = r13.sideMenu
            r2.<init>(r6)
            r13.itemAnimator = r2
            org.telegram.ui.Components.RecyclerListView r6 = r13.sideMenu
            r6.setItemAnimator(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r13.sideMenu
            java.lang.String r6 = "chats_menuBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r2.setBackgroundColor(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r13.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            r6.<init>(r13, r1, r4)
            r2.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r13.sideMenu
            r2.setAllowItemsInteractionDuringAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r13.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r6 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r8 = r13.itemAnimator
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r13.drawerLayoutContainer
            r6.<init>(r13, r8, r10)
            r13.drawerLayoutAdapter = r6
            r2.setAdapter(r6)
            android.widget.FrameLayout r2 = r13.sideMenuContainer
            org.telegram.ui.Components.RecyclerListView r6 = r13.sideMenu
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r2.addView(r6, r8)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r13.drawerLayoutContainer
            android.widget.FrameLayout r6 = r13.sideMenuContainer
            r2.setDrawerLayout(r6)
            android.widget.FrameLayout r2 = r13.sideMenuContainer
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r8 == 0) goto L_0x02da
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x02f1
        L_0x02da:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r6.x
            int r10 = r6.y
            int r9 = java.lang.Math.min(r9, r10)
            r10 = 1113587712(0x42600000, float:56.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r8 = java.lang.Math.min(r8, r9)
        L_0x02f1:
            r2.width = r8
            r2.height = r7
            android.widget.FrameLayout r8 = r13.sideMenuContainer
            r8.setLayoutParams(r2)
            org.telegram.ui.Components.RecyclerListView r8 = r13.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda97
            r9.<init>(r13)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r9)
            androidx.recyclerview.widget.ItemTouchHelper r8 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$8 r9 = new org.telegram.ui.LaunchActivity$8
            r10 = 3
            r9.<init>(r10, r4)
            r8.<init>(r9)
            org.telegram.ui.Components.RecyclerListView r9 = r13.sideMenu
            r8.attachToRecyclerView(r9)
            org.telegram.ui.Components.RecyclerListView r9 = r13.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda98
            r11.<init>(r13, r8)
            r9.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r11)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r13.actionBarLayout
            r9.setParentActionBarLayout(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r13.drawerLayoutContainer
            r9.setDrawerLayoutContainer(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            r9.init(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda29 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda29
            r11.<init>(r13)
            r9.setFragmentStackChangedListener(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            r9.setDelegate(r13)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            r13.checkCurrentAccount()
            int r9 = r13.currentAccount
            r13.updateCurrentConnectionState(r9)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r12 = new java.lang.Object[r1]
            r12[r4] = r13
            r9.postNotificationName(r11, r12)
            int r9 = r13.currentAccount
            org.telegram.tgnet.ConnectionsManager r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9)
            int r9 = r9.getConnectionState()
            r13.currentConnectionState = r9
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needShowAlert
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.reloadInterface
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.showBulletin
            r9.addObserver(r13, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r9.addObserver(r13, r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x04e9
            int r9 = r13.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            boolean r9 = r9.isClientActivated()
            if (r9 != 0) goto L_0x0400
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r13.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r9 = r13.getClientNotActivatedFragment()
            r3.addFragmentToStack(r9)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r13.drawerLayoutContainer
            r3.setAllowOpenDrawer(r4, r4)
            goto L_0x0415
        L_0x0400:
            org.telegram.ui.DialogsActivity r9 = new org.telegram.ui.DialogsActivity
            r9.<init>(r3)
            r3 = r9
            org.telegram.ui.Components.RecyclerListView r9 = r13.sideMenu
            r3.setSideMenu(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            r9.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r13.drawerLayoutContainer
            r9.setAllowOpenDrawer(r1, r4)
        L_0x0415:
            if (r14 == 0) goto L_0x04e7
            java.lang.String r3 = "fragment"
            java.lang.String r3 = r14.getString(r3)     // Catch:{ Exception -> 0x04e3 }
            if (r3 == 0) goto L_0x04e7
            java.lang.String r9 = "args"
            android.os.Bundle r9 = r14.getBundle(r9)     // Catch:{ Exception -> 0x04e3 }
            int r11 = r3.hashCode()     // Catch:{ Exception -> 0x04e3 }
            switch(r11) {
                case -1529105743: goto L_0x045f;
                case -1349522494: goto L_0x0455;
                case 3052376: goto L_0x044b;
                case 98629247: goto L_0x0441;
                case 738950403: goto L_0x0437;
                case 1434631203: goto L_0x042d;
                default: goto L_0x042c;
            }     // Catch:{ Exception -> 0x04e3 }
        L_0x042c:
            goto L_0x0468
        L_0x042d:
            java.lang.String r10 = "settings"
            boolean r10 = r3.equals(r10)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x042c
            r7 = 1
            goto L_0x0468
        L_0x0437:
            java.lang.String r11 = "channel"
            boolean r11 = r3.equals(r11)     // Catch:{ Exception -> 0x04e3 }
            if (r11 == 0) goto L_0x042c
            r7 = 3
            goto L_0x0468
        L_0x0441:
            java.lang.String r10 = "group"
            boolean r10 = r3.equals(r10)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x042c
            r7 = 2
            goto L_0x0468
        L_0x044b:
            java.lang.String r10 = "chat"
            boolean r10 = r3.equals(r10)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x042c
            r7 = 0
            goto L_0x0468
        L_0x0455:
            java.lang.String r10 = "chat_profile"
            boolean r10 = r3.equals(r10)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x042c
            r7 = 4
            goto L_0x0468
        L_0x045f:
            java.lang.String r10 = "wallpapers"
            boolean r10 = r3.equals(r10)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x042c
            r7 = 5
        L_0x0468:
            switch(r7) {
                case 0: goto L_0x04d0;
                case 1: goto L_0x04b5;
                case 2: goto L_0x04a2;
                case 3: goto L_0x048f;
                case 4: goto L_0x047c;
                case 5: goto L_0x046d;
                default: goto L_0x046b;
            }     // Catch:{ Exception -> 0x04e3 }
        L_0x046b:
            goto L_0x04e7
        L_0x046d:
            org.telegram.ui.WallpapersListActivity r7 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r4)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
            goto L_0x04e7
        L_0x047c:
            if (r9 == 0) goto L_0x04e7
            org.telegram.ui.ProfileActivity r7 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            boolean r10 = r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x048e
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
        L_0x048e:
            goto L_0x04e7
        L_0x048f:
            if (r9 == 0) goto L_0x04e7
            org.telegram.ui.ChannelCreateActivity r7 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            boolean r10 = r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x04a1
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
        L_0x04a1:
            goto L_0x04e7
        L_0x04a2:
            if (r9 == 0) goto L_0x04e7
            org.telegram.ui.GroupCreateFinalActivity r7 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            boolean r10 = r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x04b4
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
        L_0x04b4:
            goto L_0x04e7
        L_0x04b5:
            java.lang.String r7 = "user_id"
            int r10 = r13.currentAccount     // Catch:{ Exception -> 0x04e3 }
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)     // Catch:{ Exception -> 0x04e3 }
            long r10 = r10.clientUserId     // Catch:{ Exception -> 0x04e3 }
            r9.putLong(r7, r10)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ProfileActivity r7 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
            goto L_0x04e7
        L_0x04d0:
            if (r9 == 0) goto L_0x04e7
            org.telegram.ui.ChatActivity r7 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x04e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x04e3 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r13.actionBarLayout     // Catch:{ Exception -> 0x04e3 }
            boolean r10 = r10.addFragmentToStack(r7)     // Catch:{ Exception -> 0x04e3 }
            if (r10 == 0) goto L_0x04e2
            r7.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x04e3 }
        L_0x04e2:
            goto L_0x04e7
        L_0x04e3:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x04e7:
            goto L_0x0569
        L_0x04e9:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r7 = r3 instanceof org.telegram.ui.DialogsActivity
            if (r7 == 0) goto L_0x04ff
            r7 = r3
            org.telegram.ui.DialogsActivity r7 = (org.telegram.ui.DialogsActivity) r7
            org.telegram.ui.Components.RecyclerListView r9 = r13.sideMenu
            r7.setSideMenu(r9)
        L_0x04ff:
            r7 = 1
            boolean r9 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r9 == 0) goto L_0x0541
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 > r1) goto L_0x051c
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x051c
            r9 = 1
            goto L_0x051d
        L_0x051c:
            r9 = 0
        L_0x051d:
            r7 = r9
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 != r1) goto L_0x0541
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r4)
            boolean r9 = r9 instanceof org.telegram.ui.LoginActivity
            if (r9 != 0) goto L_0x0540
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r4)
            boolean r9 = r9 instanceof org.telegram.ui.IntroActivity
            if (r9 == 0) goto L_0x0541
        L_0x0540:
            r7 = 0
        L_0x0541:
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 != r1) goto L_0x0564
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r4)
            boolean r9 = r9 instanceof org.telegram.ui.LoginActivity
            if (r9 != 0) goto L_0x0563
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r4)
            boolean r9 = r9 instanceof org.telegram.ui.IntroActivity
            if (r9 == 0) goto L_0x0564
        L_0x0563:
            r7 = 0
        L_0x0564:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r13.drawerLayoutContainer
            r9.setAllowOpenDrawer(r7, r4)
        L_0x0569:
            r13.checkLayout()
            r13.checkSystemBarColors()
            android.content.Intent r3 = r13.getIntent()
            if (r14 == 0) goto L_0x0577
            r7 = 1
            goto L_0x0578
        L_0x0577:
            r7 = 0
        L_0x0578:
            r13.handleIntent(r3, r4, r7, r4)
            java.lang.String r3 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x05e0 }
            java.lang.String r7 = android.os.Build.USER     // Catch:{ Exception -> 0x05e0 }
            java.lang.String r9 = ""
            if (r3 == 0) goto L_0x0589
            java.lang.String r10 = r3.toLowerCase()     // Catch:{ Exception -> 0x05e0 }
            r3 = r10
            goto L_0x058a
        L_0x0589:
            r3 = r9
        L_0x058a:
            if (r7 == 0) goto L_0x0592
            java.lang.String r9 = r3.toLowerCase()     // Catch:{ Exception -> 0x05e0 }
            r7 = r9
            goto L_0x0593
        L_0x0592:
            r7 = r9
        L_0x0593:
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05e0 }
            if (r9 == 0) goto L_0x05b3
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05e0 }
            r9.<init>()     // Catch:{ Exception -> 0x05e0 }
            java.lang.String r10 = "OS name "
            r9.append(r10)     // Catch:{ Exception -> 0x05e0 }
            r9.append(r3)     // Catch:{ Exception -> 0x05e0 }
            java.lang.String r10 = " "
            r9.append(r10)     // Catch:{ Exception -> 0x05e0 }
            r9.append(r7)     // Catch:{ Exception -> 0x05e0 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x05e0 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x05e0 }
        L_0x05b3:
            boolean r9 = r3.contains(r0)     // Catch:{ Exception -> 0x05e0 }
            if (r9 != 0) goto L_0x05bf
            boolean r0 = r7.contains(r0)     // Catch:{ Exception -> 0x05e0 }
            if (r0 == 0) goto L_0x05df
        L_0x05bf:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05e0 }
            if (r0 > r5) goto L_0x05df
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r1     // Catch:{ Exception -> 0x05e0 }
            android.view.Window r0 = r13.getWindow()     // Catch:{ Exception -> 0x05e0 }
            android.view.View r0 = r0.getDecorView()     // Catch:{ Exception -> 0x05e0 }
            android.view.View r0 = r0.getRootView()     // Catch:{ Exception -> 0x05e0 }
            android.view.ViewTreeObserver r5 = r0.getViewTreeObserver()     // Catch:{ Exception -> 0x05e0 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16     // Catch:{ Exception -> 0x05e0 }
            r9.<init>(r0)     // Catch:{ Exception -> 0x05e0 }
            r13.onGlobalLayoutListener = r9     // Catch:{ Exception -> 0x05e0 }
            r5.addOnGlobalLayoutListener(r9)     // Catch:{ Exception -> 0x05e0 }
        L_0x05df:
            goto L_0x05e4
        L_0x05e0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05e4:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            r0.setBaseActivity(r13, r1)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r13)
            r13.updateAppUpdateViews(r4)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 23
            if (r0 < r1) goto L_0x05fa
            org.telegram.messenger.FingerprintController.checkKeyReady()
        L_0x05fa:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 28
            if (r0 < r1) goto L_0x062c
            java.lang.String r0 = "activity"
            java.lang.Object r0 = r13.getSystemService(r0)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r1 = r0.isBackgroundRestricted()
            if (r1 == 0) goto L_0x062c
            long r3 = java.lang.System.currentTimeMillis()
            long r9 = org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.getLastCheckedBackgroundActivity()
            long r3 = r3 - r9
            r9 = 86400000(0x5265CLASSNAME, double:4.2687272E-316)
            int r1 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r1 < 0) goto L_0x062c
            android.app.Dialog r1 = org.telegram.ui.Components.AlertsCreator.createBackgroundActivityDialog(r13)
            r1.show()
            long r3 = java.lang.System.currentTimeMillis()
            org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.setLastCheckedBackgroundActivity(r3)
        L_0x062c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    /* renamed from: lambda$onCreate$0$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ boolean m3651lambda$onCreate$0$orgtelegramuiLaunchActivity(View v, MotionEvent event) {
        if (this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int[] location = new int[2];
        this.layersActionBarLayout.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if (this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (this.layersActionBarLayout.getHeight() + viewY)))) {
            return false;
        }
        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                actionBarLayout2.removeFragmentFromStack(actionBarLayout2.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    static /* synthetic */ void lambda$onCreate$1(View v) {
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3653lambda$onCreate$3$orgtelegramuiLaunchActivity(View view, int position, float x, float y) {
        boolean z = true;
        if (position == 0) {
            DrawerProfileCell profileCell = (DrawerProfileCell) view;
            if (profileCell.isInAvatar(x, y)) {
                openSettings(profileCell.hasAvatar());
                return;
            }
            DrawerLayoutAdapter drawerLayoutAdapter2 = this.drawerLayoutAdapter;
            drawerLayoutAdapter2.setAccountsShown(!drawerLayoutAdapter2.isAccountsShown(), true);
        } else if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (view instanceof DrawerAddCell) {
            int freeAccounts = 0;
            Integer availableAccount = null;
            for (int a = 3; a >= 0; a--) {
                if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccounts++;
                    if (availableAccount == null) {
                        availableAccount = Integer.valueOf(a);
                    }
                }
            }
            if (UserConfig.hasPremiumOnAccounts() == 0) {
                freeAccounts--;
            }
            if (freeAccounts > 0 && availableAccount != null) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new LoginActivity(availableAccount.intValue()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (!UserConfig.hasPremiumOnAccounts() && this.actionBarLayout.fragmentsStack.size() > 0) {
                BaseFragment fragment = this.actionBarLayout.fragmentsStack.get(0);
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(fragment, this, 7, this.currentAccount);
                fragment.showDialog(limitReachedBottomSheet);
                limitReachedBottomSheet.onShowPremiumScreenRunnable = new LaunchActivity$$ExternalSyntheticLambda28(this);
            }
        } else {
            int id = this.drawerLayoutAdapter.getId(position);
            if (id == 2) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                args.putBoolean("allowSelf", false);
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ContactsActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                    m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ActionIntroActivity(0));
                    preferences.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle args2 = new Bundle();
                    args2.putInt("step", 0);
                    m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChannelCreateActivity(args2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle args3 = new Bundle();
                args3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(args3));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 12) {
                if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    boolean enabled = true;
                    if (Build.VERSION.SDK_INT >= 28) {
                        enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        try {
                            if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                                z = false;
                            }
                            enabled = z;
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (enabled) {
                        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PeopleNearbyActivity());
                    } else {
                        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 13) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* renamed from: lambda$onCreate$2$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3652lambda$onCreate$2$orgtelegramuiLaunchActivity() {
        this.drawerLayoutContainer.closeDrawer(false);
    }

    /* renamed from: lambda$onCreate$4$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ boolean m3654lambda$onCreate$4$orgtelegramuiLaunchActivity(ItemTouchHelper sideMenuTouchHelper, View view, int position) {
        if (!(view instanceof DrawerUserCell)) {
            return false;
        }
        final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
        if (accountNumber == this.currentAccount || AndroidUtilities.isTablet()) {
            sideMenuTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
            return false;
        }
        BaseFragment fragment = new DialogsActivity((Bundle) null) {
            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                super.onTransitionAnimationEnd(isOpen, backward);
                if (!isOpen && backward) {
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
        fragment.setCurrentAccount(accountNumber);
        this.actionBarLayout.presentFragmentAsPreview(fragment);
        this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(true);
        return true;
    }

    /* renamed from: lambda$onCreate$5$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3655lambda$onCreate$5$orgtelegramuiLaunchActivity() {
        checkSystemBarColors(true, false);
    }

    static /* synthetic */ void lambda$onCreate$6(View view) {
        int height = view.getMeasuredHeight();
        FileLog.d("height = " + height + " displayHeight = " + AndroidUtilities.displaySize.y);
        if (Build.VERSION.SDK_INT >= 21) {
            height -= AndroidUtilities.statusBarHeight;
        }
        if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
            AndroidUtilities.displaySize.y = height;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    public void addOnUserLeaveHintListener(Runnable callback) {
        this.onUserLeaveHintListeners.add(callback);
    }

    public void removeOnUserLeaveHintListener(Runnable callback) {
        this.onUserLeaveHintListeners.remove(callback);
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

    private void openSettings(boolean expanded) {
        Bundle args = new Bundle();
        args.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (expanded) {
            args.putBoolean("expandPhoto", true);
        }
        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ProfileActivity(args));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(false, true, !this.isNavigationBarColorFrozen);
    }

    private void checkSystemBarColors(boolean useCurrentFragment) {
        checkSystemBarColors(useCurrentFragment, true, !this.isNavigationBarColorFrozen);
    }

    private void checkSystemBarColors(boolean checkStatusBar, boolean checkNavigationBar) {
        checkSystemBarColors(false, checkStatusBar, checkNavigationBar);
    }

    private void checkSystemBarColors(boolean useCurrentFragment, boolean checkStatusBar, boolean checkNavigationBar) {
        BaseFragment currentFragment;
        boolean enable;
        BaseFragment baseFragment;
        boolean z = true;
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            currentFragment = arrayList.get(arrayList.size() - 1);
        } else {
            currentFragment = null;
        }
        if (currentFragment != null && (currentFragment.isRemovingFromStack() || currentFragment.isInPreviewMode())) {
            if (mainFragmentsStack.size() > 1) {
                ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                baseFragment = arrayList2.get(arrayList2.size() - 2);
            } else {
                baseFragment = null;
            }
            currentFragment = baseFragment;
        }
        boolean forceLightStatusBar = currentFragment != null && currentFragment.hasForceLightStatusBar();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkStatusBar) {
                if (currentFragment != null) {
                    enable = currentFragment.isLightStatusBar();
                } else {
                    enable = ColorUtils.calculateLuminance(Theme.getColor("actionBarDefault", (boolean[]) null, true)) > 0.699999988079071d;
                }
                AndroidUtilities.setLightStatusBar(getWindow(), enable, forceLightStatusBar);
            }
            if (Build.VERSION.SDK_INT >= 26 && checkNavigationBar && (!useCurrentFragment || currentFragment == null || !currentFragment.isInPreviewMode())) {
                Window window = getWindow();
                int color = (currentFragment == null || !useCurrentFragment) ? Theme.getColor("windowBackgroundGray", (boolean[]) null, true) : currentFragment.getNavigationBarColor();
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float brightness = AndroidUtilities.computePerceivedBrightness(color);
                    Window window2 = getWindow();
                    if (brightness < 0.721f) {
                        z = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window2, z);
                }
            }
        }
        if ((SharedConfig.noStatusBar != 0 || forceLightStatusBar) && Build.VERSION.SDK_INT >= 21 && checkStatusBar) {
            getWindow().setStatusBarColor(0);
        }
    }

    static /* synthetic */ DialogsActivity lambda$switchToAccount$7(Void obj) {
        return new DialogsActivity((Bundle) null);
    }

    public void switchToAccount(int account, boolean removeAll) {
        switchToAccount(account, removeAll, LaunchActivity$$ExternalSyntheticLambda65.INSTANCE);
    }

    public void switchToAccount(int account, boolean removeAll, GenericProvider<Void, DialogsActivity> dialogsActivityProvider) {
        if (account != UserConfig.selectedAccount && UserConfig.isValidAccount(account)) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = account;
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
            if (removeAll) {
                this.actionBarLayout.removeAllFragments();
            } else {
                this.actionBarLayout.removeFragmentFromStack(0);
            }
            DialogsActivity dialogsActivity = dialogsActivityProvider.provide(null);
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
            if (UserConfig.getInstance(account).unacceptedTermsOfService != null) {
                showTosActivity(account, UserConfig.getInstance(account).unacceptedTermsOfService);
            }
            updateCurrentConnectionState(this.currentAccount);
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int account = -1;
        int a = 0;
        while (true) {
            if (a >= 4) {
                break;
            } else if (UserConfig.getInstance(a).isClientActivated()) {
                account = a;
                break;
            } else {
                a++;
            }
        }
        TermsOfServiceView termsOfServiceView2 = this.termsOfServiceView;
        if (termsOfServiceView2 != null) {
            termsOfServiceView2.setVisibility(8);
        }
        if (account != -1) {
            switchToAccount(account, true);
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
        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new IntroActivity().setOnLogout());
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
        if (this.currentAccount != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
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
        this.currentAccount = UserConfig.selectedAccount;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
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
                    for (int a = 0; a < this.rightActionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                        BaseFragment chatFragment = this.rightActionBarLayout.fragmentsStack.get(a);
                        if (chatFragment instanceof ChatActivity) {
                            ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                        }
                        chatFragment.onPause();
                        this.rightActionBarLayout.fragmentsStack.remove(a);
                        this.actionBarLayout.fragmentsStack.add(chatFragment);
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
                for (int a2 = 1; a2 < this.actionBarLayout.fragmentsStack.size(); a2 = (a2 - 1) + 1) {
                    BaseFragment chatFragment2 = this.actionBarLayout.fragmentsStack.get(a2);
                    if (chatFragment2 instanceof ChatActivity) {
                        ((ChatActivity) chatFragment2).setIgnoreAttachOnPause(true);
                    }
                    chatFragment2.onPause();
                    this.actionBarLayout.fragmentsStack.remove(a2);
                    this.rightActionBarLayout.fragmentsStack.add(chatFragment2);
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

    private void showUpdateActivity(int account, TLRPC.TL_help_appUpdate update, boolean check) {
        if (this.blockingUpdateView == null) {
            AnonymousClass10 r0 = new BlockingUpdateView(this) {
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (visibility == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.blockingUpdateView = r0;
            this.drawerLayoutContainer.addView(r0, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(account, update, check);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int account, TLRPC.TL_help_termsOfService tos) {
        if (this.termsOfServiceView == null) {
            TermsOfServiceView termsOfServiceView2 = new TermsOfServiceView(this);
            this.termsOfServiceView = termsOfServiceView2;
            termsOfServiceView2.setAlpha(0.0f);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceView.TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int account) {
                    UserConfig.getInstance(account).unacceptedTermsOfService = null;
                    UserConfig.getInstance(account).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (LaunchActivity.mainFragmentsStack.size() > 0) {
                        ((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)).onResume();
                    }
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new LaunchActivity$11$$ExternalSyntheticLambda0(this)).start();
                }

                /* renamed from: lambda$onAcceptTerms$0$org-telegram-ui-LaunchActivity$11  reason: not valid java name */
                public /* synthetic */ void m3711lambda$onAcceptTerms$0$orgtelegramuiLaunchActivity$11() {
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }

                public void onDeclineTerms(int account) {
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }
            });
        }
        TLRPC.TL_help_termsOfService currentTos = UserConfig.getInstance(account).unacceptedTermsOfService;
        if (currentTos != tos && (currentTos == null || !currentTos.id.data.equals(tos.id.data))) {
            UserConfig.getInstance(account).unacceptedTermsOfService = tos;
            UserConfig.getInstance(account).saveConfig(false);
        }
        this.termsOfServiceView.show(account, tos);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.termsOfServiceView.animate().alpha(1.0f).setDuration(150).setInterpolator(AndroidUtilities.decelerateInterpolator).setListener((Animator.AnimatorListener) null).start();
    }

    public void showPasscodeActivity(boolean fingerprint, boolean animated, int x, int y, Runnable onShow, Runnable onStart) {
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
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null && messageObject.isRoundVideo()) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.passcodeView.onShow(fingerprint, animated, x, y, new LaunchActivity$$ExternalSyntheticLambda34(this, onShow), onStart);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda96(this));
        }
    }

    /* renamed from: lambda$showPasscodeActivity$8$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3708lambda$showPasscodeActivity$8$orgtelegramuiLaunchActivity(Runnable onShow) {
        this.actionBarLayout.setVisibility(4);
        if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.getVisibility() == 0) {
                this.layersActionBarLayout.setVisibility(4);
            }
            this.rightActionBarLayout.setVisibility(4);
        }
        if (onShow != null) {
            onShow.run();
        }
    }

    /* renamed from: lambda$showPasscodeActivity$9$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3709lambda$showPasscodeActivity$9$orgtelegramuiLaunchActivity() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v178, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v152, resolved type: java.lang.String} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x2262, code lost:
        if (r2.checkCanOpenChat(r3, r10.get(r10.size() - 1)) != false) goto L_0x2267;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x2319, code lost:
        if (r3.checkCanOpenChat(r2, r4.get(r4.size() - 1)) != false) goto L_0x231b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x016d, code lost:
        r3 = r53.getIntent().getExtras();
        r55 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0179, code lost:
        r57 = 0;
        r56 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        r39 = r3.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x018a, code lost:
        r3 = r3.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x018c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x21ea  */
    /* JADX WARNING: Removed duplicated region for block: B:1207:0x2847  */
    /* JADX WARNING: Removed duplicated region for block: B:1212:0x2861  */
    /* JADX WARNING: Removed duplicated region for block: B:1223:0x28ae  */
    /* JADX WARNING: Removed duplicated region for block: B:1234:0x28fb  */
    /* JADX WARNING: Removed duplicated region for block: B:1236:0x2907  */
    /* JADX WARNING: Removed duplicated region for block: B:1238:0x290f  */
    /* JADX WARNING: Removed duplicated region for block: B:1245:0x292b  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0494 A[Catch:{ Exception -> 0x048c }] */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0602  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0611  */
    /* JADX WARNING: Removed duplicated region for block: B:819:0x1b23  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01f8 A[SYNTHETIC, Splitter:B:84:0x01f8] */
    /* JADX WARNING: Removed duplicated region for block: B:883:0x1d41  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1d44  */
    /* JADX WARNING: Removed duplicated region for block: B:946:0x1e50 A[SYNTHETIC, Splitter:B:946:0x1e50] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0232  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r102, boolean r103, boolean r104, boolean r105) {
        /*
            r101 = this;
            r15 = r101
            r14 = r102
            r13 = r104
            boolean r0 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r101, r102)
            r12 = 1
            if (r0 == 0) goto L_0x0023
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0022
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r15.rightActionBarLayout
            r0.showLastFragment()
        L_0x0022:
            return r12
        L_0x0023:
            boolean r0 = org.telegram.ui.PhotoViewer.hasInstance()
            r11 = 0
            if (r0 == 0) goto L_0x0049
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0049
            if (r14 == 0) goto L_0x0042
            java.lang.String r0 = r102.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r12)
        L_0x0049:
            int r32 = r102.getFlags()
            java.lang.String r10 = r102.getAction()
            int[] r0 = new int[r12]
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r2 = "currentAccount"
            int r1 = r14.getIntExtra(r2, r1)
            r0[r11] = r1
            r9 = r0
            r0 = r9[r11]
            r15.switchToAccount(r0, r12)
            if (r10 == 0) goto L_0x006f
            java.lang.String r0 = "voip"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x006f
            r0 = 1
            goto L_0x0070
        L_0x006f:
            r0 = 0
        L_0x0070:
            r33 = r0
            if (r105 != 0) goto L_0x00a4
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r12)
            if (r0 != 0) goto L_0x0082
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x007f
            goto L_0x0082
        L_0x007f:
            r8 = r103
            goto L_0x00a6
        L_0x0082:
            r2 = 1
            r3 = 0
            r4 = -1
            r5 = -1
            r6 = 0
            r7 = 0
            r1 = r101
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r11)
            if (r33 != 0) goto L_0x00a1
            r15.passcodeSaveIntent = r14
            r8 = r103
            r15.passcodeSaveIntentIsNew = r8
            r15.passcodeSaveIntentIsRestore = r13
            return r11
        L_0x00a1:
            r8 = r103
            goto L_0x00a6
        L_0x00a4:
            r8 = r103
        L_0x00a6:
            r34 = 0
            r1 = 0
            r3 = 0
            r35 = 0
            r5 = 0
            r6 = 0
            r36 = -1
            r37 = -1
            r38 = 0
            r39 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r7 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r12 = 0
            r15.photoPathsArray = r12
            r15.videoPath = r12
            r15.sendingText = r12
            r15.documentsPathsArray = r12
            r15.documentsOriginalPathsArray = r12
            r15.documentsMimeType = r12
            r15.documentsUrisArray = r12
            r15.exportingChatUri = r12
            r15.contactsToSend = r12
            r15.contactsToSendUri = r12
            r15.importingStickers = r12
            r15.importingStickersEmoji = r12
            r15.importingStickersSoftware = r12
            r0 = 1048576(0x100000, float:1.469368E-39)
            r0 = r32 & r0
            java.lang.String r11 = "message_id"
            r12 = 0
            if (r0 != 0) goto L_0x21b0
            if (r14 == 0) goto L_0x219d
            java.lang.String r0 = r102.getAction()
            if (r0 == 0) goto L_0x219d
            if (r104 != 0) goto L_0x219d
            java.lang.String r0 = r102.getAction()
            java.lang.String r12 = "android.intent.action.SEND"
            boolean r0 = r12.equals(r0)
            java.lang.String r12 = "hash"
            java.lang.String r13 = "android.intent.extra.STREAM"
            r48 = r1
            java.lang.String r1 = "\n"
            java.lang.String r2 = ""
            if (r0 == 0) goto L_0x03ff
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x01e1
            if (r14 == 0) goto L_0x01e1
            android.os.Bundle r0 = r102.getExtras()
            if (r0 == 0) goto L_0x01e1
            android.os.Bundle r0 = r102.getExtras()
            r50 = r3
            java.lang.String r3 = "dialogId"
            r4 = r6
            r27 = r7
            r6 = 0
            long r39 = r0.getLong(r3, r6)
            r3 = 0
            int r0 = (r39 > r6 ? 1 : (r39 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x01c2
            android.os.Bundle r0 = r102.getExtras()     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r6)     // Catch:{ all -> 0x01b5 }
            if (r0 == 0) goto L_0x01aa
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01b5 }
            java.util.List r6 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r6)     // Catch:{ all -> 0x01b5 }
            r7 = 0
            int r52 = r6.size()     // Catch:{ all -> 0x01b5 }
            r53 = r52
        L_0x0155:
            r52 = r3
            r3 = r53
            if (r7 >= r3) goto L_0x01a1
            java.lang.Object r53 = r6.get(r7)     // Catch:{ all -> 0x019b }
            androidx.core.content.pm.ShortcutInfoCompat r53 = (androidx.core.content.pm.ShortcutInfoCompat) r53     // Catch:{ all -> 0x019b }
            r54 = r3
            java.lang.String r3 = r53.getId()     // Catch:{ all -> 0x019b }
            boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x019b }
            if (r3 == 0) goto L_0x018e
            android.content.Intent r3 = r53.getIntent()     // Catch:{ all -> 0x019b }
            android.os.Bundle r3 = r3.getExtras()     // Catch:{ all -> 0x019b }
            r55 = r0
            java.lang.String r0 = "dialogId"
            r57 = r4
            r56 = r5
            r4 = 0
            long r58 = r3.getLong(r0, r4)     // Catch:{ all -> 0x018c }
            r39 = r58
            r4 = 0
            java.lang.String r0 = r3.getString(r12, r4)     // Catch:{ all -> 0x018c }
            r3 = r0
            goto L_0x01b4
        L_0x018c:
            r0 = move-exception
            goto L_0x01bc
        L_0x018e:
            r55 = r0
            r57 = r4
            r56 = r5
            int r7 = r7 + 1
            r3 = r52
            r53 = r54
            goto L_0x0155
        L_0x019b:
            r0 = move-exception
            r57 = r4
            r56 = r5
            goto L_0x01bc
        L_0x01a1:
            r55 = r0
            r54 = r3
            r57 = r4
            r56 = r5
            goto L_0x01b2
        L_0x01aa:
            r55 = r0
            r52 = r3
            r57 = r4
            r56 = r5
        L_0x01b2:
            r3 = r52
        L_0x01b4:
            goto L_0x01d2
        L_0x01b5:
            r0 = move-exception
            r52 = r3
            r57 = r4
            r56 = r5
        L_0x01bc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r3 = r52
            goto L_0x01d2
        L_0x01c2:
            r52 = r3
            r57 = r4
            r56 = r5
            android.os.Bundle r0 = r102.getExtras()
            r3 = 0
            java.lang.String r0 = r0.getString(r12, r3)
            r3 = r0
        L_0x01d2:
            java.lang.String r0 = org.telegram.messenger.SharedConfig.directShareHash
            if (r0 == 0) goto L_0x01de
            java.lang.String r0 = org.telegram.messenger.SharedConfig.directShareHash
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L_0x01e9
        L_0x01de:
            r39 = 0
            goto L_0x01e9
        L_0x01e1:
            r50 = r3
            r56 = r5
            r57 = r6
            r27 = r7
        L_0x01e9:
            r3 = 0
            java.lang.String r4 = r102.getType()
            if (r4 == 0) goto L_0x0232
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0232
            android.os.Bundle r0 = r102.getExtras()     // Catch:{ Exception -> 0x022b }
            java.lang.Object r0 = r0.get(r13)     // Catch:{ Exception -> 0x022b }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x022b }
            if (r0 == 0) goto L_0x0228
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x022b }
            r2 = 0
            r5 = 0
            java.util.ArrayList r1 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r1, r5, r2, r2)     // Catch:{ Exception -> 0x022b }
            r15.contactsToSend = r1     // Catch:{ Exception -> 0x022b }
            int r1 = r1.size()     // Catch:{ Exception -> 0x022b }
            r2 = 5
            if (r1 <= r2) goto L_0x0225
            r1 = 0
            r15.contactsToSend = r1     // Catch:{ Exception -> 0x022b }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x022b }
            r1.<init>()     // Catch:{ Exception -> 0x022b }
            r15.documentsUrisArray = r1     // Catch:{ Exception -> 0x022b }
            r1.add(r0)     // Catch:{ Exception -> 0x022b }
            r15.documentsMimeType = r4     // Catch:{ Exception -> 0x022b }
            goto L_0x0230
        L_0x0225:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x022b }
            goto L_0x0230
        L_0x0228:
            r1 = 1
            r3 = r1
            goto L_0x0230
        L_0x022b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r3 = 1
        L_0x0230:
            goto L_0x03d2
        L_0x0232:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x0246
            java.lang.String r5 = "android.intent.extra.TEXT"
            java.lang.CharSequence r5 = r14.getCharSequenceExtra(r5)
            if (r5 == 0) goto L_0x0246
            java.lang.String r0 = r5.toString()
        L_0x0246:
            java.lang.String r5 = "android.intent.extra.SUBJECT"
            java.lang.String r5 = r14.getStringExtra(r5)
            boolean r6 = android.text.TextUtils.isEmpty(r0)
            if (r6 != 0) goto L_0x027e
            java.lang.String r6 = "http://"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0262
            java.lang.String r6 = "https://"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x027a
        L_0x0262:
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x027a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r1)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x027a:
            r15.sendingText = r0
            r1 = r0
            goto L_0x0287
        L_0x027e:
            boolean r1 = android.text.TextUtils.isEmpty(r5)
            if (r1 != 0) goto L_0x0286
            r15.sendingText = r5
        L_0x0286:
            r1 = r0
        L_0x0287:
            android.os.Parcelable r0 = r14.getParcelableExtra(r13)
            if (r0 == 0) goto L_0x03cb
            boolean r6 = r0 instanceof android.net.Uri
            if (r6 != 0) goto L_0x029b
            java.lang.String r6 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r6)
            r6 = r0
            goto L_0x029c
        L_0x029b:
            r6 = r0
        L_0x029c:
            r7 = r6
            android.net.Uri r7 = (android.net.Uri) r7
            if (r7 == 0) goto L_0x02a8
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r7)
            if (r0 == 0) goto L_0x02a8
            r3 = 1
        L_0x02a8:
            if (r3 != 0) goto L_0x03c8
            if (r7 == 0) goto L_0x03c8
            if (r4 == 0) goto L_0x02b6
            java.lang.String r0 = "image/"
            boolean r0 = r4.startsWith(r0)
            if (r0 != 0) goto L_0x02c6
        L_0x02b6:
            java.lang.String r0 = r7.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r12 = ".jpg"
            boolean r0 = r0.endsWith(r12)
            if (r0 == 0) goto L_0x02e1
        L_0x02c6:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x02d1
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x02d1:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r7
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray
            r2.add(r0)
            r52 = r1
            goto L_0x03ca
        L_0x02e1:
            java.lang.String r12 = r7.toString()
            r46 = 0
            int r0 = (r39 > r46 ? 1 : (r39 == r46 ? 0 : -1))
            if (r0 != 0) goto L_0x0363
            if (r12 == 0) goto L_0x0363
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0305
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r13 = "export path = "
            r0.append(r13)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0305:
            r13 = 0
            r0 = r9[r13]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r13 = r0.exportUri
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r7)
            r52 = r1
            java.lang.String r1 = org.telegram.messenger.FileLoader.fixFileName(r0)
            java.util.Iterator r53 = r13.iterator()
        L_0x031c:
            boolean r0 = r53.hasNext()
            if (r0 == 0) goto L_0x034c
            java.lang.Object r0 = r53.next()
            r54 = r0
            java.lang.String r54 = (java.lang.String) r54
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r54)     // Catch:{ Exception -> 0x0347 }
            java.util.regex.Matcher r55 = r0.matcher(r12)     // Catch:{ Exception -> 0x0347 }
            boolean r55 = r55.find()     // Catch:{ Exception -> 0x0347 }
            if (r55 != 0) goto L_0x0344
            java.util.regex.Matcher r55 = r0.matcher(r1)     // Catch:{ Exception -> 0x0347 }
            boolean r55 = r55.find()     // Catch:{ Exception -> 0x0347 }
            if (r55 == 0) goto L_0x0343
            goto L_0x0344
        L_0x0343:
            goto L_0x034b
        L_0x0344:
            r15.exportingChatUri = r7     // Catch:{ Exception -> 0x0347 }
            goto L_0x034c
        L_0x0347:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x034b:
            goto L_0x031c
        L_0x034c:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x0365
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r12.startsWith(r0)
            if (r0 == 0) goto L_0x0365
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r12.endsWith(r0)
            if (r0 == 0) goto L_0x0365
            r15.exportingChatUri = r7
            goto L_0x0365
        L_0x0363:
            r52 = r1
        L_0x0365:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x03ca
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r7)
            boolean r1 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r1 != 0) goto L_0x0377
            java.lang.String r1 = "file"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r7, r1)
        L_0x0377:
            if (r0 == 0) goto L_0x03b5
            java.lang.String r1 = "file:"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0387
            java.lang.String r1 = "file://"
            java.lang.String r0 = r0.replace(r1, r2)
        L_0x0387:
            if (r4 == 0) goto L_0x0394
            java.lang.String r1 = "video/"
            boolean r1 = r4.startsWith(r1)
            if (r1 == 0) goto L_0x0394
            r15.videoPath = r0
            goto L_0x03ca
        L_0x0394:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            if (r1 != 0) goto L_0x03a6
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsPathsArray = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsOriginalPathsArray = r1
        L_0x03a6:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            r1.add(r0)
            java.util.ArrayList<java.lang.String> r1 = r15.documentsOriginalPathsArray
            java.lang.String r2 = r7.toString()
            r1.add(r2)
            goto L_0x03ca
        L_0x03b5:
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray
            if (r1 != 0) goto L_0x03c0
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsUrisArray = r1
        L_0x03c0:
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray
            r1.add(r7)
            r15.documentsMimeType = r4
            goto L_0x03ca
        L_0x03c8:
            r52 = r1
        L_0x03ca:
            goto L_0x03d2
        L_0x03cb:
            r52 = r1
            java.lang.String r1 = r15.sendingText
            if (r1 != 0) goto L_0x03d2
            r3 = 1
        L_0x03d2:
            if (r3 == 0) goto L_0x03de
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x03de:
            r12 = r9
            r97 = r10
            r10 = r11
            r8 = r15
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
            r45 = 0
            goto L_0x21dc
        L_0x03ff:
            r50 = r3
            r56 = r5
            r57 = r6
            r27 = r7
            java.lang.String r0 = r102.getAction()
            java.lang.String r3 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x043f
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r13)     // Catch:{ all -> 0x042a }
            r15.importingStickers = r0     // Catch:{ all -> 0x042a }
            java.lang.String r0 = "STICKER_EMOJIS"
            java.util.ArrayList r0 = r14.getStringArrayListExtra(r0)     // Catch:{ all -> 0x042a }
            r15.importingStickersEmoji = r0     // Catch:{ all -> 0x042a }
            java.lang.String r0 = "IMPORTER"
            java.lang.String r0 = r14.getStringExtra(r0)     // Catch:{ all -> 0x042a }
            r15.importingStickersSoftware = r0     // Catch:{ all -> 0x042a }
            goto L_0x0435
        L_0x042a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r15.importingStickers = r1
            r15.importingStickersEmoji = r1
            r15.importingStickersSoftware = r1
        L_0x0435:
            r12 = r9
            r9 = r10
            r10 = r11
            r7 = r14
            r8 = r15
            r1 = 2
            r45 = 0
            goto L_0x21c2
        L_0x043f:
            java.lang.String r0 = r102.getAction()
            java.lang.String r3 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0625
            r1 = 0
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r13)     // Catch:{ Exception -> 0x0608 }
            java.lang.String r3 = r102.getType()     // Catch:{ Exception -> 0x0608 }
            if (r0 == 0) goto L_0x0491
            r4 = 0
        L_0x0457:
            int r5 = r0.size()     // Catch:{ Exception -> 0x048c }
            if (r4 >= r5) goto L_0x0483
            java.lang.Object r5 = r0.get(r4)     // Catch:{ Exception -> 0x048c }
            android.os.Parcelable r5 = (android.os.Parcelable) r5     // Catch:{ Exception -> 0x048c }
            boolean r6 = r5 instanceof android.net.Uri     // Catch:{ Exception -> 0x048c }
            if (r6 != 0) goto L_0x0470
            java.lang.String r6 = r5.toString()     // Catch:{ Exception -> 0x048c }
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x048c }
            r5 = r6
        L_0x0470:
            r6 = r5
            android.net.Uri r6 = (android.net.Uri) r6     // Catch:{ Exception -> 0x048c }
            if (r6 == 0) goto L_0x0480
            boolean r7 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r6)     // Catch:{ Exception -> 0x048c }
            if (r7 == 0) goto L_0x0480
            r0.remove(r4)     // Catch:{ Exception -> 0x048c }
            int r4 = r4 + -1
        L_0x0480:
            r5 = 1
            int r4 = r4 + r5
            goto L_0x0457
        L_0x0483:
            boolean r4 = r0.isEmpty()     // Catch:{ Exception -> 0x048c }
            if (r4 == 0) goto L_0x0491
            r0 = 0
            r4 = r0
            goto L_0x0492
        L_0x048c:
            r0 = move-exception
            r52 = r1
            goto L_0x060b
        L_0x0491:
            r4 = r0
        L_0x0492:
            if (r4 == 0) goto L_0x0602
            if (r3 == 0) goto L_0x04da
            java.lang.String r0 = "image/"
            boolean r0 = r3.startsWith(r0)     // Catch:{ Exception -> 0x048c }
            if (r0 == 0) goto L_0x04da
            r0 = 0
        L_0x049f:
            int r2 = r4.size()     // Catch:{ Exception -> 0x048c }
            if (r0 >= r2) goto L_0x04d6
            java.lang.Object r2 = r4.get(r0)     // Catch:{ Exception -> 0x048c }
            android.os.Parcelable r2 = (android.os.Parcelable) r2     // Catch:{ Exception -> 0x048c }
            boolean r5 = r2 instanceof android.net.Uri     // Catch:{ Exception -> 0x048c }
            if (r5 != 0) goto L_0x04b8
            java.lang.String r5 = r2.toString()     // Catch:{ Exception -> 0x048c }
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x048c }
            r2 = r5
        L_0x04b8:
            r5 = r2
            android.net.Uri r5 = (android.net.Uri) r5     // Catch:{ Exception -> 0x048c }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r6 = r15.photoPathsArray     // Catch:{ Exception -> 0x048c }
            if (r6 != 0) goto L_0x04c6
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x048c }
            r6.<init>()     // Catch:{ Exception -> 0x048c }
            r15.photoPathsArray = r6     // Catch:{ Exception -> 0x048c }
        L_0x04c6:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r6 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x048c }
            r6.<init>()     // Catch:{ Exception -> 0x048c }
            r6.uri = r5     // Catch:{ Exception -> 0x048c }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r7 = r15.photoPathsArray     // Catch:{ Exception -> 0x048c }
            r7.add(r6)     // Catch:{ Exception -> 0x048c }
            int r0 = r0 + 1
            goto L_0x049f
        L_0x04d6:
            r52 = r1
            goto L_0x05ff
        L_0x04da:
            r5 = 0
            r0 = r9[r5]     // Catch:{ Exception -> 0x0608 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x0608 }
            java.util.Set<java.lang.String> r0 = r0.exportUri     // Catch:{ Exception -> 0x0608 }
            r5 = r0
            r0 = 0
            r6 = r0
        L_0x04e6:
            int r0 = r4.size()     // Catch:{ Exception -> 0x0608 }
            if (r6 >= r0) goto L_0x05fa
            java.lang.Object r0 = r4.get(r6)     // Catch:{ Exception -> 0x0608 }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x0608 }
            boolean r7 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x0608 }
            if (r7 != 0) goto L_0x0500
            java.lang.String r7 = r0.toString()     // Catch:{ Exception -> 0x048c }
            android.net.Uri r7 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x048c }
            r0 = r7
            goto L_0x0501
        L_0x0500:
            r7 = r0
        L_0x0501:
            r0 = r7
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x0608 }
            r12 = r0
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r12)     // Catch:{ Exception -> 0x0608 }
            r13 = r0
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x0608 }
            if (r0 != 0) goto L_0x0515
            r0 = r13
            r52 = r1
            r1 = r0
            goto L_0x0518
        L_0x0515:
            r52 = r1
            r1 = r0
        L_0x0518:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x0533
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f8 }
            r0.<init>()     // Catch:{ Exception -> 0x05f8 }
            r53 = r4
            java.lang.String r4 = "export path = "
            r0.append(r4)     // Catch:{ Exception -> 0x05f8 }
            r0.append(r1)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05f8 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x05f8 }
            goto L_0x0535
        L_0x0533:
            r53 = r4
        L_0x0535:
            r46 = 0
            int r0 = (r39 > r46 ? 1 : (r39 == r46 ? 0 : -1))
            if (r0 != 0) goto L_0x05b0
            if (r1 == 0) goto L_0x05b0
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x05f8 }
            if (r0 != 0) goto L_0x05b0
            r4 = 0
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r12)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x05f8 }
            r54 = r0
            java.util.Iterator r55 = r5.iterator()     // Catch:{ Exception -> 0x05f8 }
        L_0x0550:
            boolean r0 = r55.hasNext()     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x0596
            java.lang.Object r0 = r55.next()     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x05f8 }
            r58 = r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r58)     // Catch:{ Exception -> 0x0589 }
            java.util.regex.Matcher r59 = r0.matcher(r1)     // Catch:{ Exception -> 0x0589 }
            boolean r59 = r59.find()     // Catch:{ Exception -> 0x0589 }
            if (r59 != 0) goto L_0x057c
            r59 = r4
            r4 = r54
            java.util.regex.Matcher r54 = r0.matcher(r4)     // Catch:{ Exception -> 0x0587 }
            boolean r54 = r54.find()     // Catch:{ Exception -> 0x0587 }
            if (r54 == 0) goto L_0x057b
            goto L_0x0580
        L_0x057b:
            goto L_0x0591
        L_0x057c:
            r59 = r4
            r4 = r54
        L_0x0580:
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x0587 }
            r54 = 1
            r59 = r54
            goto L_0x059a
        L_0x0587:
            r0 = move-exception
            goto L_0x058e
        L_0x0589:
            r0 = move-exception
            r59 = r4
            r4 = r54
        L_0x058e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x05f8 }
        L_0x0591:
            r54 = r4
            r4 = r59
            goto L_0x0550
        L_0x0596:
            r59 = r4
            r4 = r54
        L_0x059a:
            if (r59 == 0) goto L_0x059d
            goto L_0x05f0
        L_0x059d:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x05b0
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r1.endsWith(r0)     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x05b0
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x05f8 }
            goto L_0x05f0
        L_0x05b0:
            if (r13 == 0) goto L_0x05de
            java.lang.String r0 = "file:"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x05c1
            java.lang.String r0 = "file://"
            java.lang.String r0 = r13.replace(r0, r2)     // Catch:{ Exception -> 0x05f8 }
            r13 = r0
        L_0x05c1:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x05f8 }
            if (r0 != 0) goto L_0x05d3
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x05f8 }
            r0.<init>()     // Catch:{ Exception -> 0x05f8 }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x05f8 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x05f8 }
            r0.<init>()     // Catch:{ Exception -> 0x05f8 }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x05f8 }
        L_0x05d3:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x05f8 }
            r0.add(r13)     // Catch:{ Exception -> 0x05f8 }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x05f8 }
            r0.add(r1)     // Catch:{ Exception -> 0x05f8 }
            goto L_0x05f0
        L_0x05de:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x05f8 }
            if (r0 != 0) goto L_0x05e9
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x05f8 }
            r0.<init>()     // Catch:{ Exception -> 0x05f8 }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x05f8 }
        L_0x05e9:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x05f8 }
            r0.add(r12)     // Catch:{ Exception -> 0x05f8 }
            r15.documentsMimeType = r3     // Catch:{ Exception -> 0x05f8 }
        L_0x05f0:
            int r6 = r6 + 1
            r1 = r52
            r4 = r53
            goto L_0x04e6
        L_0x05f8:
            r0 = move-exception
            goto L_0x060b
        L_0x05fa:
            r52 = r1
            r53 = r4
        L_0x05ff:
            r1 = r52
            goto L_0x0607
        L_0x0602:
            r52 = r1
            r53 = r4
            r1 = 1
        L_0x0607:
            goto L_0x060f
        L_0x0608:
            r0 = move-exception
            r52 = r1
        L_0x060b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 1
        L_0x060f:
            if (r1 == 0) goto L_0x061b
            java.lang.String r0 = "Unsupported content"
            r2 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r2)
            r0.show()
        L_0x061b:
            r12 = r9
            r9 = r10
            r10 = r11
            r7 = r14
            r8 = r15
            r1 = 2
            r45 = 0
            goto L_0x21c2
        L_0x0625:
            java.lang.String r0 = r102.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x1ffd
            android.net.Uri r3 = r102.getData()
            if (r3 == 0) goto L_0x1fce
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r13 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r58 = 0
            r59 = 0
            r60 = 0
            r61 = 0
            r62 = 0
            r63 = 0
            r64 = 0
            r65 = 0
            r66 = 0
            r67 = 0
            r68 = 0
            r69 = 0
            r70 = 0
            r71 = 0
            r72 = 0
            r73 = 0
            r74 = -1
            r75 = 0
            r76 = 0
            r77 = 0
            r78 = 0
            r79 = r13
            java.lang.String r13 = r3.getScheme()
            r80 = r4
            java.lang.String r4 = "actions.fulfillment.extra.ACTION_TOKEN"
            r81 = r5
            java.lang.String r5 = "phone"
            if (r13 == 0) goto L_0x1cb6
            int r0 = r13.hashCode()
            r82 = r6
            switch(r0) {
                case 3699: goto L_0x069c;
                case 3213448: goto L_0x0692;
                case 99617003: goto L_0x0688;
                default: goto L_0x0687;
            }
        L_0x0687:
            goto L_0x06a6
        L_0x0688:
            java.lang.String r0 = "https"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0687
            r0 = 1
            goto L_0x06a7
        L_0x0692:
            java.lang.String r0 = "http"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0687
            r0 = 0
            goto L_0x06a7
        L_0x069c:
            java.lang.String r0 = "tg"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0687
            r0 = 2
            goto L_0x06a7
        L_0x06a6:
            r0 = -1
        L_0x06a7:
            java.lang.String r6 = "thread"
            r84 = r7
            r86 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r0) {
                case 0: goto L_0x175f;
                case 1: goto L_0x175f;
                case 2: goto L_0x06bd;
                default: goto L_0x06b0;
            }
        L_0x06b0:
            r90 = r9
            r89 = r10
            r91 = r13
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            goto L_0x1cc5
        L_0x06bd:
            java.lang.String r0 = r3.toString()
            java.lang.String r7 = "tg:resolve"
            boolean r7 = r0.startsWith(r7)
            java.lang.String r8 = "scope"
            r89 = r10
            java.lang.String r10 = "tg://telegram.org"
            if (r7 != 0) goto L_0x06e0
            java.lang.String r7 = "tg://resolve"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x06d8
            goto L_0x06e0
        L_0x06d8:
            r90 = r9
            r91 = r13
            r13 = r79
            goto L_0x0819
        L_0x06e0:
            java.lang.String r7 = "tg:resolve"
            java.lang.String r7 = r0.replace(r7, r10)
            r83 = r0
            java.lang.String r0 = "tg://resolve"
            java.lang.String r0 = r7.replace(r0, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r7 = "domain"
            java.lang.String r7 = r3.getQueryParameter(r7)
            if (r7 != 0) goto L_0x071b
            java.lang.String r7 = r3.getQueryParameter(r5)
            if (r7 == 0) goto L_0x0716
            r83 = r0
            java.lang.String r0 = "+"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x0713
            r90 = r9
            r9 = 1
            java.lang.String r0 = r7.substring(r9)
            r7 = r0
            goto L_0x071f
        L_0x0713:
            r90 = r9
            goto L_0x071f
        L_0x0716:
            r83 = r0
            r90 = r9
            goto L_0x071f
        L_0x071b:
            r83 = r0
            r90 = r9
        L_0x071f:
            java.lang.String r0 = "telegrampassport"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0792
            r0 = 0
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            java.lang.String r9 = r3.getQueryParameter(r8)
            boolean r79 = android.text.TextUtils.isEmpty(r9)
            if (r79 != 0) goto L_0x075a
            r80 = r0
            java.lang.String r0 = "{"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0757
            java.lang.String r0 = "}"
            boolean r0 = r9.endsWith(r0)
            if (r0 == 0) goto L_0x0757
            java.lang.String r0 = "nonce"
            java.lang.String r0 = r3.getQueryParameter(r0)
            r91 = r13
            java.lang.String r13 = "nonce"
            r7.put(r13, r0)
            goto L_0x0769
        L_0x0757:
            r91 = r13
            goto L_0x075e
        L_0x075a:
            r80 = r0
            r91 = r13
        L_0x075e:
            java.lang.String r0 = "payload"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.String r13 = "payload"
            r7.put(r13, r0)
        L_0x0769:
            java.lang.String r0 = "bot_id"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.String r13 = "bot_id"
            r7.put(r13, r0)
            r7.put(r8, r9)
            java.lang.String r0 = "public_key"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.String r13 = "public_key"
            r7.put(r13, r0)
            java.lang.String r0 = "callback_url"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.String r13 = "callback_url"
            r7.put(r13, r0)
            r13 = r7
            r0 = r83
            goto L_0x0819
        L_0x0792:
            r91 = r13
            java.lang.String r0 = "start"
            java.lang.String r53 = r3.getQueryParameter(r0)
            java.lang.String r0 = "startgroup"
            java.lang.String r54 = r3.getQueryParameter(r0)
            java.lang.String r0 = "startchannel"
            java.lang.String r55 = r3.getQueryParameter(r0)
            java.lang.String r0 = "admin"
            java.lang.String r58 = r3.getQueryParameter(r0)
            java.lang.String r0 = "game"
            java.lang.String r61 = r3.getQueryParameter(r0)
            java.lang.String r0 = "voicechat"
            java.lang.String r62 = r3.getQueryParameter(r0)
            java.lang.String r0 = "livestream"
            java.lang.String r63 = r3.getQueryParameter(r0)
            java.lang.String r0 = "startattach"
            java.lang.String r76 = r3.getQueryParameter(r0)
            java.lang.String r0 = "choose"
            java.lang.String r78 = r3.getQueryParameter(r0)
            java.lang.String r0 = "attach"
            java.lang.String r77 = r3.getQueryParameter(r0)
            java.lang.String r0 = "post"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x07e4
            r0 = 0
            r70 = r0
            goto L_0x07e6
        L_0x07e4:
            r70 = r0
        L_0x07e6:
            java.lang.String r0 = r3.getQueryParameter(r6)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x07f8
            r0 = 0
            r72 = r0
            goto L_0x07fa
        L_0x07f8:
            r72 = r0
        L_0x07fa:
            java.lang.String r0 = "comment"
            java.lang.String r0 = r3.getQueryParameter(r0)
            java.lang.Integer r73 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r73.intValue()
            if (r0 != 0) goto L_0x0813
            r73 = 0
            r80 = r7
            r13 = r79
            r0 = r83
            goto L_0x0819
        L_0x0813:
            r80 = r7
            r13 = r79
            r0 = r83
        L_0x0819:
            java.lang.String r7 = "tg:invoice"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x16fa
            java.lang.String r7 = "tg://invoice"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x082b
            goto L_0x16fa
        L_0x082b:
            java.lang.String r7 = "tg:privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x15ee
            java.lang.String r7 = "tg://privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x083d
            goto L_0x15ee
        L_0x083d:
            java.lang.String r6 = "tg:bg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x1393
            java.lang.String r6 = "tg://bg"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x084f
            goto L_0x1393
        L_0x084f:
            java.lang.String r6 = "tg:join"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x1328
            java.lang.String r6 = "tg://join"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0861
            goto L_0x1328
        L_0x0861:
            java.lang.String r6 = "tg:addstickers"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x12bd
            java.lang.String r6 = "tg://addstickers"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0873
            goto L_0x12bd
        L_0x0873:
            java.lang.String r6 = "tg:msg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x11ef
            java.lang.String r6 = "tg://msg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x11ef
            java.lang.String r6 = "tg://share"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x11ef
            java.lang.String r6 = "tg:share"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0895
            goto L_0x11ef
        L_0x0895:
            java.lang.String r1 = "tg:confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x1184
            java.lang.String r1 = "tg://confirmphone"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x08a7
            goto L_0x1184
        L_0x08a7:
            java.lang.String r1 = "tg:login"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x10fa
            java.lang.String r1 = "tg://login"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x08b9
            goto L_0x10fa
        L_0x08b9:
            java.lang.String r1 = "tg:openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x1066
            java.lang.String r1 = "tg://openmessage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x08cb
            goto L_0x1066
        L_0x08cb:
            java.lang.String r1 = "tg:passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0fa2
            java.lang.String r1 = "tg://passport"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0fa2
            java.lang.String r1 = "tg:secureid"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x08e5
            goto L_0x0fa2
        L_0x08e5:
            java.lang.String r1 = "tg:setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "tg://setlanguage"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x08f7
            goto L_0x0var_
        L_0x08f7:
            java.lang.String r1 = "tg:addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0ed0
            java.lang.String r1 = "tg://addtheme"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0909
            goto L_0x0ed0
        L_0x0909:
            java.lang.String r1 = "tg:settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0d02
            java.lang.String r1 = "tg://settings"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x091b
            goto L_0x0d02
        L_0x091b:
            java.lang.String r1 = "tg:search"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0c3e
            java.lang.String r1 = "tg://search"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x092d
            goto L_0x0c3e
        L_0x092d:
            java.lang.String r1 = "tg:calllog"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0bea
            java.lang.String r1 = "tg://calllog"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x093f
            goto L_0x0bea
        L_0x093f:
            java.lang.String r1 = "tg:call"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0aab
            java.lang.String r1 = "tg://call"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0951
            goto L_0x0aab
        L_0x0951:
            java.lang.String r1 = "tg:scanqr"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0a56
            java.lang.String r1 = "tg://scanqr"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0963
            goto L_0x0a56
        L_0x0963:
            java.lang.String r1 = "tg:addcontact"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x09e7
            java.lang.String r1 = "tg://addcontact"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0975
            goto L_0x09e7
        L_0x0975:
            java.lang.String r1 = "tg://"
            java.lang.String r1 = r0.replace(r1, r2)
            java.lang.String r6 = "tg:"
            java.lang.String r1 = r1.replace(r6, r2)
            r2 = 63
            int r2 = r1.indexOf(r2)
            r6 = r2
            if (r2 < 0) goto L_0x0992
            r2 = 0
            java.lang.String r1 = r1.substring(r2, r6)
            r52 = r1
            goto L_0x0994
        L_0x0992:
            r52 = r1
        L_0x0994:
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x09e7:
            java.lang.String r1 = "tg:addcontact"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://addcontact"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "name"
            java.lang.String r24 = r3.getQueryParameter(r1)
            java.lang.String r25 = r3.getQueryParameter(r5)
            r19 = 1
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0a56:
            r21 = 1
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0aab:
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x0b97
            java.lang.String r1 = "extra_force_call"
            int r6 = r15.currentAccount
            org.telegram.messenger.ContactsController r6 = org.telegram.messenger.ContactsController.getInstance(r6)
            boolean r6 = r6.contactsLoaded
            if (r6 != 0) goto L_0x0ae7
            java.lang.String r6 = "extra_force_call"
            boolean r6 = r14.hasExtra(r6)
            if (r6 == 0) goto L_0x0acc
            goto L_0x0ae7
        L_0x0acc:
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r14)
            r2.removeExtra(r4)
            java.lang.String r6 = "extra_force_call"
            r7 = 1
            r2.putExtra(r6, r7)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64
            r6.<init>(r15, r2)
            r7 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r6, r7)
            r1 = r48
            goto L_0x0b42
        L_0x0ae7:
            java.lang.String r6 = "format"
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.String r7 = "name"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = r3.getQueryParameter(r5)
            r9 = 0
            java.util.List r10 = r15.findContacts(r7, r8, r9)
            boolean r9 = r10.isEmpty()
            if (r9 == 0) goto L_0x0b0e
            if (r8 == 0) goto L_0x0b0e
            r2 = r7
            r9 = r8
            r12 = 1
            r24 = r2
            r25 = r9
            r20 = r12
            goto L_0x0b40
        L_0x0b0e:
            int r9 = r10.size()
            r12 = 1
            if (r9 != r12) goto L_0x0b23
            r9 = 0
            java.lang.Object r12 = r10.get(r9)
            org.telegram.tgnet.TLRPC$TL_contact r12 = (org.telegram.tgnet.TLRPC.TL_contact) r12
            r79 = r8
            long r8 = r12.user_id
            r48 = r8
            goto L_0x0b25
        L_0x0b23:
            r79 = r8
        L_0x0b25:
            r8 = 0
            int r12 = (r48 > r8 ? 1 : (r48 == r8 ? 0 : -1))
            if (r12 != 0) goto L_0x0b30
            if (r7 == 0) goto L_0x0b2e
            r2 = r7
        L_0x0b2e:
            r23 = r2
        L_0x0b30:
            java.lang.String r2 = "video"
            boolean r2 = r2.equalsIgnoreCase(r6)
            if (r2 == 0) goto L_0x0b3b
            r17 = 1
            goto L_0x0b3d
        L_0x0b3b:
            r16 = 1
        L_0x0b3d:
            r2 = 1
            r18 = r2
        L_0x0b40:
            r1 = r48
        L_0x0b42:
            r48 = r1
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0b97:
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0bea:
            r7 = 1
            r47 = r3
            r79 = r13
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r52 = r7
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r7 = r59
            r59 = r20
            goto L_0x1d0f
        L_0x0c3e:
            java.lang.String r1 = "tg:search"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://search"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "query"
            java.lang.String r1 = r3.getQueryParameter(r1)
            if (r1 == 0) goto L_0x0cad
            java.lang.String r22 = r1.trim()
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0cad:
            java.lang.String r22 = ""
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0d02:
            java.lang.String r1 = "themes"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0d60
            r6 = 2
            r47 = r3
            r57 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0d60:
            java.lang.String r1 = "devices"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0dbe
            r6 = 3
            r47 = r3
            r57 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0dbe:
            java.lang.String r1 = "folders"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0e1c
            r6 = 4
            r47 = r3
            r57 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0e1c:
            java.lang.String r1 = "change_number"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0e7a
            r6 = 5
            r47 = r3
            r57 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0e7a:
            r6 = 1
            r47 = r3
            r57 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0ed0:
            java.lang.String r1 = "tg:addtheme"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://addtheme"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r66 = r3.getQueryParameter(r1)
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0var_:
            java.lang.String r1 = "tg:setlanguage"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://setlanguage"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "lang"
            java.lang.String r65 = r3.getQueryParameter(r1)
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x0fa2:
            java.lang.String r1 = "tg:passport"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://passport"
            java.lang.String r1 = r1.replace(r2, r10)
            java.lang.String r2 = "tg:secureid"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r13 = r1
            java.lang.String r1 = r3.getQueryParameter(r8)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x0fe4
            java.lang.String r2 = "{"
            boolean r2 = r1.startsWith(r2)
            if (r2 == 0) goto L_0x0fe4
            java.lang.String r2 = "}"
            boolean r2 = r1.endsWith(r2)
            if (r2 == 0) goto L_0x0fe4
            java.lang.String r2 = "nonce"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r6 = "nonce"
            r13.put(r6, r2)
            goto L_0x0fef
        L_0x0fe4:
            java.lang.String r2 = "payload"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r6 = "payload"
            r13.put(r6, r2)
        L_0x0fef:
            java.lang.String r2 = "bot_id"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r6 = "bot_id"
            r13.put(r6, r2)
            r13.put(r8, r1)
            java.lang.String r2 = "public_key"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r6 = "public_key"
            r13.put(r6, r2)
            java.lang.String r2 = "callback_url"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r6 = "callback_url"
            r13.put(r6, r2)
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x1066:
            java.lang.String r1 = "tg:openmessage"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://openmessage"
            java.lang.String r1 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r1)
            java.lang.String r0 = "user_id"
            java.lang.String r2 = r3.getQueryParameter(r0)
            java.lang.String r0 = "chat_id"
            java.lang.String r6 = r3.getQueryParameter(r0)
            java.lang.String r7 = r3.getQueryParameter(r11)
            if (r2 == 0) goto L_0x1091
            long r8 = java.lang.Long.parseLong(r2)     // Catch:{ NumberFormatException -> 0x108f }
            r48 = r8
            goto L_0x109b
        L_0x108f:
            r0 = move-exception
            goto L_0x109b
        L_0x1091:
            if (r6 == 0) goto L_0x109b
            long r8 = java.lang.Long.parseLong(r6)     // Catch:{ NumberFormatException -> 0x109a }
            r50 = r8
            goto L_0x109b
        L_0x109a:
            r0 = move-exception
        L_0x109b:
            if (r7 == 0) goto L_0x10a3
            int r0 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x10a2 }
            goto L_0x10a5
        L_0x10a2:
            r0 = move-exception
        L_0x10a3:
            r0 = r56
        L_0x10a5:
            r56 = r0
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x10fa:
            java.lang.String r1 = "tg:login"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r6 = "tg://login"
            java.lang.String r0 = r1.replace(r6, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "token"
            java.lang.String r1 = r3.getQueryParameter(r1)
            java.lang.String r6 = "code"
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r6)
            int r6 = r6.intValue()
            if (r6 == 0) goto L_0x112f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            r7.append(r6)
            java.lang.String r67 = r7.toString()
        L_0x112f:
            r81 = r1
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x1184:
            java.lang.String r1 = "tg:confirmphone"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://confirmphone"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r60 = r3.getQueryParameter(r5)
            java.lang.String r64 = r3.getQueryParameter(r12)
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x11ef:
            java.lang.String r2 = "tg:msg"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r6 = "tg://msg"
            java.lang.String r2 = r2.replace(r6, r10)
            java.lang.String r6 = "tg://share"
            java.lang.String r2 = r2.replace(r6, r10)
            java.lang.String r6 = "tg:share"
            java.lang.String r0 = r2.replace(r6, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "url"
            java.lang.String r2 = r3.getQueryParameter(r2)
            if (r2 != 0) goto L_0x1215
            java.lang.String r2 = ""
        L_0x1215:
            java.lang.String r6 = "text"
            java.lang.String r6 = r3.getQueryParameter(r6)
            if (r6 == 0) goto L_0x1249
            int r6 = r2.length()
            if (r6 <= 0) goto L_0x1234
            r75 = 1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            r6.append(r1)
            java.lang.String r2 = r6.toString()
        L_0x1234:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            java.lang.String r7 = "text"
            java.lang.String r7 = r3.getQueryParameter(r7)
            r6.append(r7)
            java.lang.String r2 = r6.toString()
        L_0x1249:
            int r6 = r2.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r6 <= r7) goto L_0x1259
            r6 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r2 = r2.substring(r7, r6)
            goto L_0x125a
        L_0x1259:
            r7 = 0
        L_0x125a:
            boolean r6 = r2.endsWith(r1)
            if (r6 == 0) goto L_0x126b
            int r6 = r2.length()
            r8 = 1
            int r6 = r6 - r8
            java.lang.String r2 = r2.substring(r7, r6)
            goto L_0x125a
        L_0x126b:
            r7 = r2
            r47 = r3
            r79 = r13
            r59 = r20
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x12bd:
            java.lang.String r1 = "tg:addstickers"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://addstickers"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "set"
            java.lang.String r7 = r3.getQueryParameter(r1)
            r47 = r3
            r84 = r7
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x1328:
            java.lang.String r1 = "tg:join"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://join"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "invite"
            java.lang.String r6 = r3.getQueryParameter(r1)
            r47 = r3
            r82 = r6
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x1393:
            java.lang.String r1 = "tg:bg"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://bg"
            java.lang.String r1 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r1)
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r2 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r0 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r2.settings = r0
            java.lang.String r0 = "slug"
            java.lang.String r0 = r3.getQueryParameter(r0)
            r2.slug = r0
            java.lang.String r0 = r2.slug
            if (r0 != 0) goto L_0x13c4
            java.lang.String r0 = "color"
            java.lang.String r0 = r3.getQueryParameter(r0)
            r2.slug = r0
        L_0x13c4:
            r6 = 0
            java.lang.String r0 = r2.slug
            if (r0 == 0) goto L_0x13e8
            java.lang.String r0 = r2.slug
            int r0 = r0.length()
            r7 = 6
            if (r0 != r7) goto L_0x13e8
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x13e6 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x13e6 }
            r8 = 16
            int r7 = java.lang.Integer.parseInt(r7, r8)     // Catch:{ Exception -> 0x13e6 }
            r7 = r7 | r86
            r0.background_color = r7     // Catch:{ Exception -> 0x13e6 }
            r7 = 0
            r2.slug = r7     // Catch:{ Exception -> 0x13e6 }
            r6 = 1
        L_0x13e4:
            goto L_0x14a5
        L_0x13e6:
            r0 = move-exception
            goto L_0x13e4
        L_0x13e8:
            java.lang.String r0 = r2.slug
            if (r0 == 0) goto L_0x14a5
            java.lang.String r0 = r2.slug
            int r0 = r0.length()
            r7 = 13
            if (r0 < r7) goto L_0x14a5
            java.lang.String r0 = r2.slug
            r7 = 6
            char r0 = r0.charAt(r7)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)
            if (r0 == 0) goto L_0x14a5
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x14a4 }
            java.lang.String r8 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r9 = 0
            java.lang.String r8 = r8.substring(r9, r7)     // Catch:{ Exception -> 0x14a4 }
            r7 = 16
            int r8 = java.lang.Integer.parseInt(r8, r7)     // Catch:{ Exception -> 0x14a4 }
            r7 = r8 | r86
            r0.background_color = r7     // Catch:{ Exception -> 0x14a4 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x14a4 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r8 = 7
            r9 = 13
            java.lang.String r7 = r7.substring(r8, r9)     // Catch:{ Exception -> 0x14a4 }
            r8 = 16
            int r7 = java.lang.Integer.parseInt(r7, r8)     // Catch:{ Exception -> 0x14a4 }
            r7 = r7 | r86
            r0.second_background_color = r7     // Catch:{ Exception -> 0x14a4 }
            java.lang.String r0 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x14a4 }
            r7 = 20
            if (r0 < r7) goto L_0x1459
            java.lang.String r0 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r7 = 13
            char r0 = r0.charAt(r7)     // Catch:{ Exception -> 0x14a4 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x14a4 }
            if (r0 == 0) goto L_0x1459
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x14a4 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r8 = 14
            r9 = 20
            java.lang.String r7 = r7.substring(r8, r9)     // Catch:{ Exception -> 0x14a4 }
            r8 = 16
            int r7 = java.lang.Integer.parseInt(r7, r8)     // Catch:{ Exception -> 0x14a4 }
            r7 = r7 | r86
            r0.third_background_color = r7     // Catch:{ Exception -> 0x14a4 }
        L_0x1459:
            java.lang.String r0 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x14a4 }
            r7 = 27
            if (r0 != r7) goto L_0x1485
            java.lang.String r0 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r7 = 20
            char r0 = r0.charAt(r7)     // Catch:{ Exception -> 0x14a4 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x14a4 }
            if (r0 == 0) goto L_0x1485
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x14a4 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x14a4 }
            r8 = 21
            java.lang.String r7 = r7.substring(r8)     // Catch:{ Exception -> 0x14a4 }
            r8 = 16
            int r7 = java.lang.Integer.parseInt(r7, r8)     // Catch:{ Exception -> 0x14a4 }
            r7 = r7 | r86
            r0.fourth_background_color = r7     // Catch:{ Exception -> 0x14a4 }
        L_0x1485:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x149e }
            boolean r7 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x149e }
            if (r7 != 0) goto L_0x149d
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x149e }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x149e }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x149e }
            r7.rotation = r8     // Catch:{ Exception -> 0x149e }
        L_0x149d:
            goto L_0x149f
        L_0x149e:
            r0 = move-exception
        L_0x149f:
            r7 = 0
            r2.slug = r7     // Catch:{ Exception -> 0x14a4 }
            r6 = 1
            goto L_0x14a5
        L_0x14a4:
            r0 = move-exception
        L_0x14a5:
            if (r6 != 0) goto L_0x159b
            java.lang.String r0 = "mode"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 == 0) goto L_0x14e6
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r7 = " "
            java.lang.String[] r7 = r0.split(r7)
            if (r7 == 0) goto L_0x14e4
            int r8 = r7.length
            if (r8 <= 0) goto L_0x14e4
            r8 = 0
        L_0x14bf:
            int r9 = r7.length
            if (r8 >= r9) goto L_0x14e4
            r9 = r7[r8]
            java.lang.String r10 = "blur"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x14d2
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r10 = 1
            r9.blur = r10
            goto L_0x14e1
        L_0x14d2:
            r10 = 1
            r9 = r7[r8]
            java.lang.String r12 = "motion"
            boolean r9 = r12.equals(r9)
            if (r9 == 0) goto L_0x14e1
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r9.motion = r10
        L_0x14e1:
            int r8 = r8 + 1
            goto L_0x14bf
        L_0x14e4:
            r7 = r0
            goto L_0x14e7
        L_0x14e6:
            r7 = r0
        L_0x14e7:
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r2.settings
            java.lang.String r8 = "intensity"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)
            int r8 = r8.intValue()
            r0.intensity = r8
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x1580 }
            boolean r8 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1580 }
            if (r8 != 0) goto L_0x157f
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r2.settings     // Catch:{ Exception -> 0x1580 }
            r9 = 6
            r10 = 0
            java.lang.String r12 = r0.substring(r10, r9)     // Catch:{ Exception -> 0x1580 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x1580 }
            r9 = r10 | r86
            r8.background_color = r9     // Catch:{ Exception -> 0x1580 }
            int r8 = r0.length()     // Catch:{ Exception -> 0x1580 }
            r9 = 13
            if (r8 < r9) goto L_0x157f
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r2.settings     // Catch:{ Exception -> 0x1580 }
            r10 = 8
            java.lang.String r10 = r0.substring(r10, r9)     // Catch:{ Exception -> 0x1580 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1580 }
            r9 = r10 | r86
            r8.second_background_color = r9     // Catch:{ Exception -> 0x1580 }
            int r8 = r0.length()     // Catch:{ Exception -> 0x1580 }
            r9 = 20
            if (r8 < r9) goto L_0x1559
            r8 = 13
            char r8 = r0.charAt(r8)     // Catch:{ Exception -> 0x1580 }
            boolean r8 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r8)     // Catch:{ Exception -> 0x1580 }
            if (r8 == 0) goto L_0x1559
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r2.settings     // Catch:{ Exception -> 0x1580 }
            r9 = 14
            r10 = 20
            java.lang.String r9 = r0.substring(r9, r10)     // Catch:{ Exception -> 0x1580 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1580 }
            r9 = r9 | r86
            r8.third_background_color = r9     // Catch:{ Exception -> 0x1580 }
        L_0x1559:
            int r8 = r0.length()     // Catch:{ Exception -> 0x1580 }
            r9 = 27
            if (r8 != r9) goto L_0x157f
            r8 = 20
            char r8 = r0.charAt(r8)     // Catch:{ Exception -> 0x1580 }
            boolean r8 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r8)     // Catch:{ Exception -> 0x1580 }
            if (r8 == 0) goto L_0x157f
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r2.settings     // Catch:{ Exception -> 0x1580 }
            r9 = 21
            java.lang.String r9 = r0.substring(r9)     // Catch:{ Exception -> 0x1580 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1580 }
            r9 = r9 | r86
            r8.fourth_background_color = r9     // Catch:{ Exception -> 0x1580 }
        L_0x157f:
            goto L_0x1581
        L_0x1580:
            r0 = move-exception
        L_0x1581:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x159a }
            boolean r8 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x159a }
            if (r8 != 0) goto L_0x1599
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r2.settings     // Catch:{ Exception -> 0x159a }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x159a }
            int r9 = r9.intValue()     // Catch:{ Exception -> 0x159a }
            r8.rotation = r9     // Catch:{ Exception -> 0x159a }
        L_0x1599:
            goto L_0x159b
        L_0x159a:
            r0 = move-exception
        L_0x159b:
            r47 = r3
            r79 = r13
            r68 = r55
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r76 = r2
            r55 = r18
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x15ee:
            java.lang.String r1 = "tg:privatepost"
            java.lang.String r1 = r0.replace(r1, r10)
            java.lang.String r2 = "tg://privatepost"
            java.lang.String r0 = r1.replace(r2, r10)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "post"
            java.lang.String r1 = r3.getQueryParameter(r1)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            java.lang.String r2 = "channel"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)
            int r7 = r1.intValue()
            if (r7 == 0) goto L_0x1628
            long r7 = r2.longValue()
            r9 = 0
            int r12 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r12 != 0) goto L_0x1623
            goto L_0x1628
        L_0x1623:
            r70 = r1
            r71 = r2
            goto L_0x162e
        L_0x1628:
            r1 = 0
            r2 = 0
            r70 = r1
            r71 = r2
        L_0x162e:
            java.lang.String r1 = r3.getQueryParameter(r6)
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r2 = r1.intValue()
            if (r2 != 0) goto L_0x1640
            r1 = 0
            r72 = r1
            goto L_0x1642
        L_0x1640:
            r72 = r1
        L_0x1642:
            java.lang.String r1 = "comment"
            java.lang.String r1 = r3.getQueryParameter(r1)
            java.lang.Integer r73 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r1 = r73.intValue()
            if (r1 != 0) goto L_0x16a7
            r73 = 0
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x16a7:
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x16fa:
            java.lang.String r1 = "tg:invoice"
            java.lang.String r2 = "tg://invoice"
            java.lang.String r0 = r0.replace(r1, r2)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r1 = "slug"
            java.lang.String r69 = r3.getQueryParameter(r1)
            r47 = r3
            r79 = r13
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x175f:
            r90 = r9
            r89 = r10
            r91 = r13
            java.lang.String r0 = r3.getHost()
            java.lang.String r7 = r0.toLowerCase()
            java.lang.String r0 = "telegram.me"
            boolean r0 = r7.equals(r0)
            if (r0 != 0) goto L_0x178d
            java.lang.String r0 = "t.me"
            boolean r0 = r7.equals(r0)
            if (r0 != 0) goto L_0x178d
            java.lang.String r0 = "telegram.dog"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x1786
            goto L_0x178d
        L_0x1786:
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            goto L_0x1cc5
        L_0x178d:
            java.lang.String r0 = r3.getPath()
            if (r0 == 0) goto L_0x1CLASSNAME
            int r8 = r0.length()
            r9 = 1
            if (r8 <= r9) goto L_0x1CLASSNAME
            java.lang.String r8 = r0.substring(r9)
            java.lang.String r0 = "$"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x17b3
            java.lang.String r69 = r8.substring(r9)
            r6 = r82
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x17b3:
            java.lang.String r0 = "invoice/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x17cf
            r0 = 47
            int r0 = r8.indexOf(r0)
            int r0 = r0 + r9
            java.lang.String r69 = r8.substring(r0)
            r6 = r82
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x17cf:
            java.lang.String r0 = "bg/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x19f4
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r1 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r0 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r1.settings = r0
            java.lang.String r0 = "bg/"
            java.lang.String r0 = r8.replace(r0, r2)
            r1.slug = r0
            r2 = 0
            java.lang.String r0 = r1.slug
            if (r0 == 0) goto L_0x1811
            java.lang.String r0 = r1.slug
            int r0 = r0.length()
            r6 = 6
            if (r0 != r6) goto L_0x1811
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x180f }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x180f }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x180f }
            r6 = r6 | r86
            r0.background_color = r6     // Catch:{ Exception -> 0x180f }
            r6 = 0
            r1.slug = r6     // Catch:{ Exception -> 0x180f }
            r2 = 1
        L_0x180c:
            r13 = 0
            goto L_0x18d3
        L_0x180f:
            r0 = move-exception
            goto L_0x180c
        L_0x1811:
            java.lang.String r0 = r1.slug
            if (r0 == 0) goto L_0x18d2
            java.lang.String r0 = r1.slug
            int r0 = r0.length()
            r6 = 13
            if (r0 < r6) goto L_0x18d2
            java.lang.String r0 = r1.slug
            r6 = 6
            char r0 = r0.charAt(r6)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)
            if (r0 == 0) goto L_0x18d2
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x18cf }
            java.lang.String r9 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r6)     // Catch:{ Exception -> 0x18cf }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ Exception -> 0x18cf }
            r6 = r9 | r86
            r0.background_color = r6     // Catch:{ Exception -> 0x18cf }
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x18cf }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r9 = 7
            r10 = 13
            java.lang.String r6 = r6.substring(r9, r10)     // Catch:{ Exception -> 0x18cf }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x18cf }
            r6 = r6 | r86
            r0.second_background_color = r6     // Catch:{ Exception -> 0x18cf }
            java.lang.String r0 = r1.slug     // Catch:{ Exception -> 0x18cf }
            int r0 = r0.length()     // Catch:{ Exception -> 0x18cf }
            r6 = 20
            if (r0 < r6) goto L_0x1882
            java.lang.String r0 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r6 = 13
            char r0 = r0.charAt(r6)     // Catch:{ Exception -> 0x18cf }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x18cf }
            if (r0 == 0) goto L_0x1882
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x18cf }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r9 = 14
            r10 = 20
            java.lang.String r6 = r6.substring(r9, r10)     // Catch:{ Exception -> 0x18cf }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x18cf }
            r6 = r6 | r86
            r0.third_background_color = r6     // Catch:{ Exception -> 0x18cf }
        L_0x1882:
            java.lang.String r0 = r1.slug     // Catch:{ Exception -> 0x18cf }
            int r0 = r0.length()     // Catch:{ Exception -> 0x18cf }
            r6 = 27
            if (r0 != r6) goto L_0x18ae
            java.lang.String r0 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r6 = 20
            char r0 = r0.charAt(r6)     // Catch:{ Exception -> 0x18cf }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x18cf }
            if (r0 == 0) goto L_0x18ae
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x18cf }
            java.lang.String r6 = r1.slug     // Catch:{ Exception -> 0x18cf }
            r9 = 21
            java.lang.String r6 = r6.substring(r9)     // Catch:{ Exception -> 0x18cf }
            r9 = 16
            int r6 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ Exception -> 0x18cf }
            r6 = r6 | r86
            r0.fourth_background_color = r6     // Catch:{ Exception -> 0x18cf }
        L_0x18ae:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x18c7 }
            boolean r6 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x18c7 }
            if (r6 != 0) goto L_0x18c6
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r1.settings     // Catch:{ Exception -> 0x18c7 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x18c7 }
            int r9 = r9.intValue()     // Catch:{ Exception -> 0x18c7 }
            r6.rotation = r9     // Catch:{ Exception -> 0x18c7 }
        L_0x18c6:
            goto L_0x18c8
        L_0x18c7:
            r0 = move-exception
        L_0x18c8:
            r13 = 0
            r1.slug = r13     // Catch:{ Exception -> 0x18cd }
            r2 = 1
            goto L_0x18d3
        L_0x18cd:
            r0 = move-exception
            goto L_0x18d3
        L_0x18cf:
            r0 = move-exception
            r13 = 0
            goto L_0x18d3
        L_0x18d2:
            r13 = 0
        L_0x18d3:
            if (r2 != 0) goto L_0x19e7
            java.lang.String r0 = "mode"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 == 0) goto L_0x1915
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r6 = " "
            java.lang.String[] r6 = r0.split(r6)
            if (r6 == 0) goto L_0x1913
            int r9 = r6.length
            if (r9 <= 0) goto L_0x1913
            r9 = 0
        L_0x18ed:
            int r10 = r6.length
            if (r9 >= r10) goto L_0x1913
            r10 = r6[r9]
            java.lang.String r12 = "blur"
            boolean r10 = r12.equals(r10)
            if (r10 == 0) goto L_0x1900
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r12 = 1
            r10.blur = r12
            goto L_0x190f
        L_0x1900:
            r12 = 1
            r10 = r6[r9]
            java.lang.String r13 = "motion"
            boolean r10 = r13.equals(r10)
            if (r10 == 0) goto L_0x190f
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings
            r10.motion = r12
        L_0x190f:
            int r9 = r9 + 1
            r13 = 0
            goto L_0x18ed
        L_0x1913:
            r6 = r0
            goto L_0x1916
        L_0x1915:
            r6 = r0
        L_0x1916:
            java.lang.String r0 = "intensity"
            java.lang.String r9 = r3.getQueryParameter(r0)
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            if (r0 != 0) goto L_0x192f
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)
            int r10 = r10.intValue()
            r0.intensity = r10
            goto L_0x1935
        L_0x192f:
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings
            r10 = 50
            r0.intensity = r10
        L_0x1935:
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x19c8 }
            boolean r10 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x19c8 }
            if (r10 != 0) goto L_0x19bd
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r1.settings     // Catch:{ Exception -> 0x19c8 }
            r45 = r2
            r12 = 0
            r13 = 6
            java.lang.String r2 = r0.substring(r12, r13)     // Catch:{ Exception -> 0x19c6 }
            r12 = 16
            int r2 = java.lang.Integer.parseInt(r2, r12)     // Catch:{ Exception -> 0x19c6 }
            r2 = r2 | r86
            r10.background_color = r2     // Catch:{ Exception -> 0x19c6 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x19c6 }
            r10 = 13
            if (r2 < r10) goto L_0x19c5
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x19c6 }
            r12 = 7
            java.lang.String r12 = r0.substring(r12, r10)     // Catch:{ Exception -> 0x19c6 }
            r10 = 16
            int r12 = java.lang.Integer.parseInt(r12, r10)     // Catch:{ Exception -> 0x19c6 }
            r10 = r12 | r86
            r2.second_background_color = r10     // Catch:{ Exception -> 0x19c6 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x19c6 }
            r10 = 20
            if (r2 < r10) goto L_0x1996
            r2 = 13
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x19c6 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x19c6 }
            if (r2 == 0) goto L_0x1996
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x19c6 }
            r10 = 14
            r12 = 20
            java.lang.String r10 = r0.substring(r10, r12)     // Catch:{ Exception -> 0x19c6 }
            r12 = 16
            int r10 = java.lang.Integer.parseInt(r10, r12)     // Catch:{ Exception -> 0x19c6 }
            r10 = r10 | r86
            r2.third_background_color = r10     // Catch:{ Exception -> 0x19c6 }
        L_0x1996:
            int r2 = r0.length()     // Catch:{ Exception -> 0x19c6 }
            r10 = 27
            if (r2 != r10) goto L_0x19c5
            r2 = 20
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x19c6 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x19c6 }
            if (r2 == 0) goto L_0x19c5
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x19c6 }
            r10 = 21
            java.lang.String r10 = r0.substring(r10)     // Catch:{ Exception -> 0x19c6 }
            r12 = 16
            int r10 = java.lang.Integer.parseInt(r10, r12)     // Catch:{ Exception -> 0x19c6 }
            r10 = r10 | r86
            r2.fourth_background_color = r10     // Catch:{ Exception -> 0x19c6 }
            goto L_0x19c5
        L_0x19bd:
            r45 = r2
            r13 = 6
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x19c6 }
            r10 = -1
            r2.background_color = r10     // Catch:{ Exception -> 0x19c6 }
        L_0x19c5:
            goto L_0x19cc
        L_0x19c6:
            r0 = move-exception
            goto L_0x19cc
        L_0x19c8:
            r0 = move-exception
            r45 = r2
            r13 = 6
        L_0x19cc:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x19e5 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x19e5 }
            if (r2 != 0) goto L_0x19e4
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r1.settings     // Catch:{ Exception -> 0x19e5 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x19e5 }
            int r10 = r10.intValue()     // Catch:{ Exception -> 0x19e5 }
            r2.rotation = r10     // Catch:{ Exception -> 0x19e5 }
        L_0x19e4:
            goto L_0x19ea
        L_0x19e5:
            r0 = move-exception
            goto L_0x19ea
        L_0x19e7:
            r45 = r2
            r13 = 6
        L_0x19ea:
            r68 = r1
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x19f4:
            r13 = 6
            java.lang.String r0 = "login/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1a24
            java.lang.String r0 = "login/"
            java.lang.String r0 = r8.replace(r0, r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x1a1c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            r1.append(r0)
            java.lang.String r67 = r1.toString()
        L_0x1a1c:
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1a24:
            java.lang.String r0 = "joinchat/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1a38
            java.lang.String r0 = "joinchat/"
            java.lang.String r6 = r8.replace(r0, r2)
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1a38:
            java.lang.String r0 = "+"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1a5c
            java.lang.String r0 = "+"
            java.lang.String r6 = r8.replace(r0, r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isNumeric(r6)
            if (r0 == 0) goto L_0x1a56
            r0 = r6
            r6 = 0
            r80 = r0
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1a56:
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1a5c:
            java.lang.String r0 = "addstickers/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1a74
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r8.replace(r0, r2)
            r84 = r0
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1a74:
            java.lang.String r0 = "msg/"
            boolean r0 = r8.startsWith(r0)
            if (r0 != 0) goto L_0x1bf8
            java.lang.String r0 = "share/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1a8a
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1bfc
        L_0x1a8a:
            java.lang.String r0 = "confirmphone"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1aa2
            java.lang.String r60 = r3.getQueryParameter(r5)
            java.lang.String r64 = r3.getQueryParameter(r12)
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1aa2:
            java.lang.String r0 = "setlanguage/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1ab8
            r0 = 12
            java.lang.String r65 = r8.substring(r0)
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1ab8:
            java.lang.String r0 = "addtheme/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1ace
            r0 = 9
            java.lang.String r66 = r8.substring(r0)
            r6 = r82
            r10 = 2
            r12 = 3
            r45 = 0
            goto L_0x1CLASSNAME
        L_0x1ace:
            java.lang.String r0 = "c/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1b2d
            java.util.List r0 = r3.getPathSegments()
            int r1 = r0.size()
            r12 = 3
            if (r1 != r12) goto L_0x1b26
            r1 = 1
            java.lang.Object r2 = r0.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.Long r1 = org.telegram.messenger.Utilities.parseLong(r2)
            r10 = 2
            java.lang.Object r2 = r0.get(r10)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r9 = r2.intValue()
            if (r9 == 0) goto L_0x1b0d
            long r70 = r1.longValue()
            r45 = 0
            int r9 = (r70 > r45 ? 1 : (r70 == r45 ? 0 : -1))
            if (r9 != 0) goto L_0x1b08
            goto L_0x1b0f
        L_0x1b08:
            r71 = r1
            r70 = r2
            goto L_0x1b15
        L_0x1b0d:
            r45 = 0
        L_0x1b0f:
            r2 = 0
            r1 = 0
            r71 = r1
            r70 = r2
        L_0x1b15:
            java.lang.String r1 = r3.getQueryParameter(r6)
            java.lang.Integer r72 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r1 = r72.intValue()
            if (r1 != 0) goto L_0x1b29
            r72 = 0
            goto L_0x1b29
        L_0x1b26:
            r10 = 2
            r45 = 0
        L_0x1b29:
            r6 = r82
            goto L_0x1CLASSNAME
        L_0x1b2d:
            r10 = 2
            r12 = 3
            r45 = 0
            int r0 = r8.length()
            r1 = 1
            if (r0 < r1) goto L_0x1CLASSNAME
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r1 = r3.getPathSegments()
            r0.<init>(r1)
            int r1 = r0.size()
            if (r1 <= 0) goto L_0x1b5a
            r1 = 0
            java.lang.Object r2 = r0.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r9 = "s"
            boolean r2 = r2.equals(r9)
            if (r2 == 0) goto L_0x1b5b
            r0.remove(r1)
            goto L_0x1b5b
        L_0x1b5a:
            r1 = 0
        L_0x1b5b:
            int r2 = r0.size()
            if (r2 <= 0) goto L_0x1b82
            java.lang.Object r2 = r0.get(r1)
            r1 = r2
            java.lang.String r1 = (java.lang.String) r1
            int r2 = r0.size()
            r9 = 1
            if (r2 <= r9) goto L_0x1b84
            java.lang.Object r2 = r0.get(r9)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r70 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r2 = r70.intValue()
            if (r2 != 0) goto L_0x1b84
            r70 = 0
            goto L_0x1b84
        L_0x1b82:
            r1 = r80
        L_0x1b84:
            if (r70 == 0) goto L_0x1b8a
            int r74 = getTimestampFromLink(r3)
        L_0x1b8a:
            java.lang.String r2 = "start"
            java.lang.String r53 = r3.getQueryParameter(r2)
            java.lang.String r2 = "startgroup"
            java.lang.String r54 = r3.getQueryParameter(r2)
            java.lang.String r2 = "startchannel"
            java.lang.String r55 = r3.getQueryParameter(r2)
            java.lang.String r2 = "admin"
            java.lang.String r58 = r3.getQueryParameter(r2)
            java.lang.String r2 = "game"
            java.lang.String r61 = r3.getQueryParameter(r2)
            java.lang.String r2 = "voicechat"
            java.lang.String r62 = r3.getQueryParameter(r2)
            java.lang.String r2 = "livestream"
            java.lang.String r63 = r3.getQueryParameter(r2)
            java.lang.String r2 = "startattach"
            java.lang.String r76 = r3.getQueryParameter(r2)
            java.lang.String r2 = "choose"
            java.lang.String r78 = r3.getQueryParameter(r2)
            java.lang.String r2 = "attach"
            java.lang.String r77 = r3.getQueryParameter(r2)
            java.lang.String r2 = r3.getQueryParameter(r6)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r6 = r2.intValue()
            if (r6 != 0) goto L_0x1bd8
            r2 = 0
            r72 = r2
            goto L_0x1bda
        L_0x1bd8:
            r72 = r2
        L_0x1bda:
            java.lang.String r2 = "comment"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.Integer r73 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r2 = r73.intValue()
            if (r2 != 0) goto L_0x1bf2
            r73 = 0
            r80 = r1
            r6 = r82
            goto L_0x1CLASSNAME
        L_0x1bf2:
            r80 = r1
            r6 = r82
            goto L_0x1CLASSNAME
        L_0x1bf8:
            r10 = 2
            r12 = 3
            r45 = 0
        L_0x1bfc:
            java.lang.String r0 = "url"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 != 0) goto L_0x1CLASSNAME
            java.lang.String r0 = ""
        L_0x1CLASSNAME:
            java.lang.String r2 = "text"
            java.lang.String r2 = r3.getQueryParameter(r2)
            if (r2 == 0) goto L_0x1c3a
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x1CLASSNAME
            r75 = 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r1)
            java.lang.String r0 = r2.toString()
        L_0x1CLASSNAME:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r6 = "text"
            java.lang.String r6 = r3.getQueryParameter(r6)
            r2.append(r6)
            java.lang.String r0 = r2.toString()
        L_0x1c3a:
            int r2 = r0.length()
            r6 = 16384(0x4000, float:2.2959E-41)
            if (r2 <= r6) goto L_0x1c4a
            r2 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            java.lang.String r0 = r0.substring(r6, r2)
            goto L_0x1c4b
        L_0x1c4a:
            r6 = 0
        L_0x1c4b:
            boolean r2 = r0.endsWith(r1)
            if (r2 == 0) goto L_0x1c5c
            int r2 = r0.length()
            r9 = 1
            int r2 = r2 - r9
            java.lang.String r0 = r0.substring(r6, r2)
            goto L_0x1c4b
        L_0x1c5c:
            r59 = r0
            r6 = r82
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
        L_0x1CLASSNAME:
            r6 = r82
        L_0x1CLASSNAME:
            r47 = r3
            r82 = r6
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
            goto L_0x1d0f
        L_0x1cb6:
            r82 = r6
            r84 = r7
            r90 = r9
            r89 = r10
            r91 = r13
            r10 = 2
            r12 = 3
            r13 = 6
            r45 = 0
        L_0x1cc5:
            r47 = r3
            r7 = r59
            r9 = r60
            r8 = r64
            r83 = r71
            r85 = r72
            r86 = r73
            r87 = r74
            r88 = r75
            r92 = r76
            r93 = r77
            r94 = r78
            r59 = r20
            r60 = r21
            r64 = r25
            r71 = r62
            r72 = r63
            r73 = r65
            r74 = r66
            r75 = r67
            r76 = r68
            r77 = r69
            r78 = r70
            r62 = r23
            r63 = r24
            r65 = r52
            r66 = r53
            r67 = r54
            r68 = r55
            r69 = r58
            r70 = r61
            r53 = r16
            r54 = r17
            r55 = r18
            r58 = r19
            r61 = r22
            r52 = r27
        L_0x1d0f:
            boolean r0 = r14.hasExtra(r4)
            if (r0 == 0) goto L_0x1d59
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x1d2f
            java.lang.String r0 = "tg"
            r6 = r91
            boolean r0 = r0.equals(r6)
            if (r0 == 0) goto L_0x1d31
            if (r65 != 0) goto L_0x1d31
            r0 = 1
            goto L_0x1d32
        L_0x1d2f:
            r6 = r91
        L_0x1d31:
            r0 = 0
        L_0x1d32:
            com.google.firebase.appindexing.builders.AssistActionBuilder r1 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r1.<init>()
            java.lang.String r2 = r14.getStringExtra(r4)
            com.google.firebase.appindexing.builders.AssistActionBuilder r1 = r1.setActionToken(r2)
            if (r0 == 0) goto L_0x1d44
            java.lang.String r2 = "http://schema.org/CompletedActionStatus"
            goto L_0x1d46
        L_0x1d44:
            java.lang.String r2 = "http://schema.org/FailedActionStatus"
        L_0x1d46:
            com.google.firebase.appindexing.Action$Builder r1 = r1.setActionStatus(r2)
            com.google.firebase.appindexing.Action r1 = r1.build()
            com.google.firebase.appindexing.FirebaseUserActions r2 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r101)
            r2.end(r1)
            r14.removeExtra(r4)
            goto L_0x1d5b
        L_0x1d59:
            r6 = r91
        L_0x1d5b:
            if (r75 != 0) goto L_0x1d74
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x1d6a
            goto L_0x1d74
        L_0x1d6a:
            r99 = r11
            r8 = r15
            r97 = r89
            r95 = r90
            r1 = 2
            goto L_0x1fb1
        L_0x1d74:
            if (r9 != 0) goto L_0x1f2c
            if (r8 == 0) goto L_0x1d86
            r98 = r8
            r96 = r9
            r99 = r11
            r97 = r89
            r95 = r90
            r89 = r6
            goto L_0x1var_
        L_0x1d86:
            if (r80 != 0) goto L_0x1eb2
            if (r82 != 0) goto L_0x1eb2
            if (r84 != 0) goto L_0x1eb2
            if (r7 != 0) goto L_0x1eb2
            if (r70 != 0) goto L_0x1eb2
            if (r71 != 0) goto L_0x1eb2
            if (r79 != 0) goto L_0x1eb2
            if (r65 != 0) goto L_0x1eb2
            if (r73 != 0) goto L_0x1eb2
            if (r75 != 0) goto L_0x1eb2
            if (r76 != 0) goto L_0x1eb2
            if (r77 != 0) goto L_0x1eb2
            if (r83 != 0) goto L_0x1eb2
            if (r74 != 0) goto L_0x1eb2
            if (r81 == 0) goto L_0x1da8
            r29 = r11
            goto L_0x1eb4
        L_0x1da8:
            android.content.ContentResolver r16 = r101.getContentResolver()     // Catch:{ Exception -> 0x1e86 }
            android.net.Uri r17 = r102.getData()     // Catch:{ Exception -> 0x1e86 }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            android.database.Cursor r0 = r16.query(r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x1e86 }
            r1 = r0
            if (r1 == 0) goto L_0x1e56
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1e4a }
            if (r0 == 0) goto L_0x1e47
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1e4a }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1e4a }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ all -> 0x1e4a }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1e4a }
            r2 = 0
        L_0x1dd8:
            r3 = 4
            if (r2 >= r3) goto L_0x1dfb
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x1e4a }
            long r3 = r3.getClientUserId()     // Catch:{ all -> 0x1e4a }
            r29 = r11
            long r10 = (long) r0
            int r5 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x1df4
            r3 = 0
            r90[r3] = r2     // Catch:{ all -> 0x1e45 }
            r4 = r90[r3]     // Catch:{ all -> 0x1e45 }
            r11 = 1
            r15.switchToAccount(r4, r11)     // Catch:{ all -> 0x1e45 }
            goto L_0x1dfe
        L_0x1df4:
            r11 = 1
            int r2 = r2 + 1
            r11 = r29
            r10 = 2
            goto L_0x1dd8
        L_0x1dfb:
            r29 = r11
            r11 = 1
        L_0x1dfe:
            java.lang.String r2 = "data4"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x1e45 }
            long r2 = r1.getLong(r2)     // Catch:{ all -> 0x1e45 }
            r4 = 0
            r5 = r90[r4]     // Catch:{ all -> 0x1e45 }
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)     // Catch:{ all -> 0x1e45 }
            int r10 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x1e45 }
            java.lang.Object[] r11 = new java.lang.Object[r4]     // Catch:{ all -> 0x1e45 }
            r5.postNotificationName(r10, r11)     // Catch:{ all -> 0x1e45 }
            r4 = r2
            java.lang.String r10 = "mimetype"
            int r10 = r1.getColumnIndex(r10)     // Catch:{ all -> 0x1e40 }
            java.lang.String r10 = r1.getString(r10)     // Catch:{ all -> 0x1e40 }
            java.lang.String r11 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r11 = android.text.TextUtils.equals(r10, r11)     // Catch:{ all -> 0x1e40 }
            if (r11 == 0) goto L_0x1e2f
            r11 = 1
            r48 = r4
            r53 = r11
            goto L_0x1e58
        L_0x1e2f:
            java.lang.String r11 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r11 = android.text.TextUtils.equals(r10, r11)     // Catch:{ all -> 0x1e40 }
            if (r11 == 0) goto L_0x1e3d
            r11 = 1
            r48 = r4
            r54 = r11
            goto L_0x1e58
        L_0x1e3d:
            r48 = r4
            goto L_0x1e58
        L_0x1e40:
            r0 = move-exception
            r2 = r0
            r48 = r4
            goto L_0x1e4e
        L_0x1e45:
            r0 = move-exception
            goto L_0x1e4d
        L_0x1e47:
            r29 = r11
            goto L_0x1e58
        L_0x1e4a:
            r0 = move-exception
            r29 = r11
        L_0x1e4d:
            r2 = r0
        L_0x1e4e:
            if (r1 == 0) goto L_0x1e55
            r1.close()     // Catch:{ all -> 0x1e54 }
            goto L_0x1e55
        L_0x1e54:
            r0 = move-exception
        L_0x1e55:
            throw r2     // Catch:{ Exception -> 0x1e5e }
        L_0x1e56:
            r29 = r11
        L_0x1e58:
            if (r1 == 0) goto L_0x1e60
            r1.close()     // Catch:{ Exception -> 0x1e5e }
            goto L_0x1e60
        L_0x1e5e:
            r0 = move-exception
            goto L_0x1e89
        L_0x1e60:
            r8 = r15
            r99 = r29
            r3 = r50
            r7 = r52
            r16 = r53
            r17 = r54
            r18 = r55
            r5 = r56
            r6 = r57
            r19 = r58
            r20 = r59
            r21 = r60
            r22 = r61
            r23 = r62
            r24 = r63
            r25 = r64
            r97 = r89
            r95 = r90
            r1 = 2
            goto L_0x1fe0
        L_0x1e86:
            r0 = move-exception
            r29 = r11
        L_0x1e89:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = r15
            r99 = r29
            r3 = r50
            r7 = r52
            r16 = r53
            r17 = r54
            r18 = r55
            r5 = r56
            r6 = r57
            r19 = r58
            r20 = r59
            r21 = r60
            r22 = r61
            r23 = r62
            r24 = r63
            r25 = r64
            r97 = r89
            r95 = r90
            r1 = 2
            goto L_0x1fe0
        L_0x1eb2:
            r29 = r11
        L_0x1eb4:
            if (r7 == 0) goto L_0x1ed1
            java.lang.String r0 = "@"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x1ed1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = " "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r7 = r0.toString()
            r0 = r7
            goto L_0x1ed2
        L_0x1ed1:
            r0 = r7
        L_0x1ed2:
            r11 = 0
            r2 = r90[r11]
            r27 = 0
            r1 = r101
            r3 = r80
            r4 = r82
            r5 = r84
            r16 = r6
            r6 = r66
            r7 = r67
            r10 = r8
            r8 = r68
            r96 = r9
            r95 = r90
            r9 = r69
            r98 = r10
            r97 = r89
            r17 = 2
            r10 = r0
            r99 = r29
            r18 = 1
            r11 = r88
            r13 = 0
            r12 = r78
            r89 = r16
            r13 = r83
            r14 = r85
            r15 = r86
            r16 = r70
            r17 = r79
            r18 = r73
            r19 = r65
            r20 = r75
            r21 = r81
            r22 = r76
            r23 = r77
            r24 = r74
            r25 = r71
            r26 = r72
            r28 = r87
            r29 = r92
            r30 = r93
            r31 = r94
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31)
            r1 = 2
            r8 = r101
            goto L_0x1fb1
        L_0x1f2c:
            r98 = r8
            r96 = r9
            r99 = r11
            r97 = r89
            r95 = r90
            r89 = r6
        L_0x1var_:
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r9 = 3
            r8 = r101
            r0.<init>(r8, r9)
            r10 = 0
            r0.setCanCancel(r10)
            r0.show()
            org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode r1 = new org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode
            r1.<init>()
            r11 = r1
            r12 = r98
            r11.hash = r12
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = new org.telegram.tgnet.TLRPC$TL_codeSettings
            r1.<init>()
            r11.settings = r1
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = r11.settings
            r1.allow_flashcall = r10
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = r11.settings
            boolean r2 = org.telegram.messenger.ApplicationLoader.hasPlayServices
            r1.allow_app_hash = r2
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r2 = "mainconfig"
            android.content.SharedPreferences r13 = r1.getSharedPreferences(r2, r10)
            org.telegram.tgnet.TLRPC$TL_codeSettings r1 = r11.settings
            boolean r1 = r1.allow_app_hash
            if (r1 == 0) goto L_0x1var_
            android.content.SharedPreferences$Editor r1 = r13.edit()
            java.lang.String r2 = org.telegram.messenger.BuildVars.SMS_HASH
            java.lang.String r3 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.putString(r3, r2)
            r1.apply()
            goto L_0x1f8d
        L_0x1var_:
            android.content.SharedPreferences$Editor r1 = r13.edit()
            java.lang.String r2 = "sms_hash"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            r1.apply()
        L_0x1f8d:
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r14 = r1
            r15 = r96
            r14.putString(r5, r15)
            r4 = r15
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84
            r1 = r5
            r2 = r101
            r3 = r0
            r9 = r5
            r5 = r14
            r10 = r6
            r6 = r11
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 2
            r10.sendRequest(r11, r9, r1)
        L_0x1fb1:
            r3 = r50
            r7 = r52
            r16 = r53
            r17 = r54
            r18 = r55
            r5 = r56
            r6 = r57
            r19 = r58
            r20 = r59
            r21 = r60
            r22 = r61
            r23 = r62
            r24 = r63
            r25 = r64
            goto L_0x1fe0
        L_0x1fce:
            r95 = r9
            r97 = r10
            r99 = r11
            r8 = r15
            r1 = 2
            r45 = 0
            r7 = r27
            r3 = r50
            r5 = r56
            r6 = r57
        L_0x1fe0:
            r0 = r5
            r11 = r6
            r27 = r7
            r15 = r17
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r12 = r95
            r10 = r99
            r5 = r3
            r4 = r22
            r3 = r23
            goto L_0x21dc
        L_0x1ffd:
            r95 = r9
            r97 = r10
            r99 = r11
            r8 = r15
            r1 = 2
            r45 = 0
            java.lang.String r0 = r102.getAction()
            java.lang.String r2 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x2031
            r6 = 1
            r11 = r6
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r12 = r95
            r10 = r99
            goto L_0x21dc
        L_0x2031:
            java.lang.String r0 = r102.getAction()
            java.lang.String r2 = "new_dialog"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x205d
            r38 = 1
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
            r12 = r95
            r10 = r99
            goto L_0x21dc
        L_0x205d:
            java.lang.String r0 = r102.getAction()
            java.lang.String r2 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x2120
            java.lang.String r0 = "chatId"
            r7 = r102
            r2 = 0
            int r0 = r7.getIntExtra(r0, r2)
            long r3 = (long) r0
            java.lang.String r0 = "chatId"
            long r3 = r7.getLongExtra(r0, r3)
            java.lang.String r0 = "userId"
            int r0 = r7.getIntExtra(r0, r2)
            long r5 = (long) r0
            java.lang.String r0 = "userId"
            long r5 = r7.getLongExtra(r0, r5)
            java.lang.String r0 = "encId"
            int r0 = r7.getIntExtra(r0, r2)
            java.lang.String r9 = "appWidgetId"
            int r9 = r7.getIntExtra(r9, r2)
            if (r9 == 0) goto L_0x20a8
            r10 = 6
            r36 = r9
            java.lang.String r11 = "appWidgetType"
            int r37 = r7.getIntExtra(r11, r2)
            r6 = r10
            r3 = r50
            r5 = r56
            r12 = r95
            r10 = r99
            goto L_0x2109
        L_0x20a8:
            if (r56 != 0) goto L_0x20b1
            r10 = r99
            int r11 = r7.getIntExtra(r10, r2)
            goto L_0x20b5
        L_0x20b1:
            r10 = r99
            r11 = r56
        L_0x20b5:
            int r12 = (r3 > r45 ? 1 : (r3 == r45 ? 0 : -1))
            if (r12 == 0) goto L_0x20cd
            r12 = r95
            r13 = r12[r2]
            org.telegram.messenger.NotificationCenter r13 = org.telegram.messenger.NotificationCenter.getInstance(r13)
            int r14 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r15 = new java.lang.Object[r2]
            r13.postNotificationName(r14, r15)
            r13 = r3
            r5 = r11
            r6 = r57
            goto L_0x2109
        L_0x20cd:
            r12 = r95
            int r13 = (r5 > r45 ? 1 : (r5 == r45 ? 0 : -1))
            if (r13 == 0) goto L_0x20e9
            r13 = r12[r2]
            org.telegram.messenger.NotificationCenter r13 = org.telegram.messenger.NotificationCenter.getInstance(r13)
            int r14 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r15 = new java.lang.Object[r2]
            r13.postNotificationName(r14, r15)
            r13 = r5
            r5 = r11
            r48 = r13
            r3 = r50
            r6 = r57
            goto L_0x2109
        L_0x20e9:
            if (r0 == 0) goto L_0x2101
            r13 = r12[r2]
            org.telegram.messenger.NotificationCenter r13 = org.telegram.messenger.NotificationCenter.getInstance(r13)
            int r14 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r15 = new java.lang.Object[r2]
            r13.postNotificationName(r14, r15)
            r2 = r0
            r35 = r2
            r5 = r11
            r3 = r50
            r6 = r57
            goto L_0x2109
        L_0x2101:
            r2 = 1
            r41 = r2
            r5 = r11
            r3 = r50
            r6 = r57
        L_0x2109:
            r0 = r5
            r11 = r6
            r15 = r17
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r3
            r4 = r22
            r3 = r23
            goto L_0x21dc
        L_0x2120:
            r7 = r102
            r12 = r95
            r10 = r99
            java.lang.String r0 = r102.getAction()
            java.lang.String r2 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x214e
            r42 = 1
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
            goto L_0x21dc
        L_0x214e:
            java.lang.String r0 = r102.getAction()
            java.lang.String r2 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x2176
            r43 = 1
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
            goto L_0x21dc
        L_0x2176:
            java.lang.String r0 = "voip_chat"
            r9 = r97
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x21c2
            r44 = 1
            r97 = r9
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
            goto L_0x21dc
        L_0x219d:
            r48 = r1
            r50 = r3
            r56 = r5
            r57 = r6
            r27 = r7
            r45 = r12
            r7 = r14
            r8 = r15
            r1 = 2
            r12 = r9
            r9 = r10
            r10 = r11
            goto L_0x21c2
        L_0x21b0:
            r48 = r1
            r50 = r3
            r56 = r5
            r57 = r6
            r27 = r7
            r45 = r12
            r7 = r14
            r8 = r15
            r1 = 2
            r12 = r9
            r9 = r10
            r10 = r11
        L_0x21c2:
            r97 = r9
            r15 = r17
            r4 = r22
            r3 = r23
            r2 = r24
            r100 = r25
            r1 = r35
            r9 = r36
            r7 = r37
            r13 = r48
            r5 = r50
            r0 = r56
            r11 = r57
        L_0x21dc:
            r17 = r2
            int r2 = r8.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x2847
            if (r4 == 0) goto L_0x221f
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.getLastFragment()
            r22 = r3
            boolean r3 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x2219
            r3 = r2
            org.telegram.ui.DialogsActivity r3 = (org.telegram.ui.DialogsActivity) r3
            boolean r23 = r3.isMainDialogList()
            if (r23 == 0) goto L_0x2215
            android.view.View r23 = r3.getFragmentView()
            if (r23 == 0) goto L_0x220e
            r23 = r2
            r2 = 1
            r3.search(r4, r2)
            goto L_0x2218
        L_0x220e:
            r23 = r2
            r2 = 1
            r3.setInitialSearchString(r4)
            goto L_0x2218
        L_0x2215:
            r23 = r2
            r2 = 1
        L_0x2218:
            goto L_0x2222
        L_0x2219:
            r23 = r2
            r2 = 1
            r41 = 1
            goto L_0x2222
        L_0x221f:
            r22 = r3
            r2 = 1
        L_0x2222:
            int r3 = (r13 > r45 ? 1 : (r13 == r45 ? 0 : -1))
            if (r3 == 0) goto L_0x22e1
            if (r16 != 0) goto L_0x229a
            if (r15 == 0) goto L_0x222e
            r25 = r4
            goto L_0x229c
        L_0x222e:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.lang.String r2 = "user_id"
            r3.putLong(r2, r13)
            if (r0 == 0) goto L_0x223d
            r3.putInt(r10, r0)
        L_0x223d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x2265
            r2 = 0
            r10 = r12[r2]
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = mainFragmentsStack
            int r23 = r10.size()
            r25 = r4
            r24 = 1
            int r4 = r23 + -1
            java.lang.Object r4 = r10.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r2 = r2.checkCanOpenChat(r3, r4)
            if (r2 == 0) goto L_0x2287
            goto L_0x2267
        L_0x2265:
            r25 = r4
        L_0x2267:
            org.telegram.ui.ChatActivity r2 = new org.telegram.ui.ChatActivity
            r2.<init>(r3)
            r46 = r2
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r2
            boolean r2 = r45.presentFragment(r46, r47, r48, r49, r50)
            if (r2 == 0) goto L_0x2287
            r34 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r8.drawerLayoutContainer
            r2.closeDrawer()
        L_0x2287:
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x229a:
            r25 = r4
        L_0x229c:
            if (r18 == 0) goto L_0x22c6
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.getLastFragment()
            if (r2 == 0) goto L_0x22b5
            org.telegram.messenger.MessagesController r3 = r2.getMessagesController()
            java.lang.Long r4 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r2, r3, r15)
        L_0x22b5:
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2844
        L_0x22c6:
            r2 = 0
            r3 = r12[r2]
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r3)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r8, r13, r15, r2)
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2844
        L_0x22e1:
            r25 = r4
            int r2 = (r5 > r45 ? 1 : (r5 == r45 ? 0 : -1))
            if (r2 == 0) goto L_0x234e
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.String r3 = "chat_id"
            r2.putLong(r3, r5)
            if (r0 == 0) goto L_0x22f6
            r2.putInt(r10, r0)
        L_0x22f6:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x231b
            r3 = 0
            r4 = r12[r3]
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r10 = r4.size()
            r23 = 1
            int r10 = r10 + -1
            java.lang.Object r4 = r4.get(r10)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r3 = r3.checkCanOpenChat(r2, r4)
            if (r3 == 0) goto L_0x233b
        L_0x231b:
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r2)
            r46 = r3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r3
            boolean r3 = r45.presentFragment(r46, r47, r48, r49, r50)
            if (r3 == 0) goto L_0x233b
            r34 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r3.closeDrawer()
        L_0x233b:
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x234e:
            if (r1 == 0) goto L_0x238d
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.String r3 = "enc_id"
            r2.putInt(r3, r1)
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r2)
            r46 = r3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r3
            boolean r3 = r45.presentFragment(r46, r47, r48, r49, r50)
            if (r3 == 0) goto L_0x237a
            r34 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r3.closeDrawer()
        L_0x237a:
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x238d:
            if (r41 == 0) goto L_0x23df
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 != 0) goto L_0x239b
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            r2.removeAllFragments()
            goto L_0x23cb
        L_0x239b:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x23cb
            r2 = 0
        L_0x23a6:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            r4 = 1
            int r3 = r3 - r4
            if (r2 >= r3) goto L_0x23c5
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r3.fragmentsStack
            r10 = 0
            java.lang.Object r4 = r4.get(r10)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r3.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r4)
            int r2 = r2 + -1
            r3 = 1
            int r2 = r2 + r3
            goto L_0x23a6
        L_0x23c5:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.layersActionBarLayout
            r3 = 0
            r2.closeLastFragment(r3)
        L_0x23cb:
            r34 = 0
            r2 = 0
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x23df:
            if (r42 == 0) goto L_0x2416
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x2400
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.Components.AudioPlayerAlert r3 = new org.telegram.ui.Components.AudioPlayerAlert
            r10 = 0
            r3.<init>(r8, r10)
            r2.showDialog(r3)
            goto L_0x2401
        L_0x2400:
            r10 = 0
        L_0x2401:
            r34 = 0
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x2416:
            r10 = 0
            if (r43 == 0) goto L_0x2450
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x243b
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.Components.SharingLocationsAlert r3 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda99
            r4.<init>(r8, r12)
            r3.<init>(r8, r4, r10)
            r2.showDialog(r3)
        L_0x243b:
            r34 = 0
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x2450:
            android.net.Uri r2 = r8.exportingChatUri
            if (r2 == 0) goto L_0x246a
            java.util.ArrayList<android.net.Uri> r3 = r8.documentsUrisArray
            r8.runImportRequest(r2, r3)
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2844
        L_0x246a:
            java.util.ArrayList<android.os.Parcelable> r2 = r8.importingStickers
            if (r2 == 0) goto L_0x248b
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25
            r2.<init>(r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            r34 = 0
            r2 = r103
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x248b:
            java.lang.String r2 = r8.videoPath
            if (r2 != 0) goto L_0x2801
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r8.photoPathsArray
            if (r2 != 0) goto L_0x2801
            java.lang.String r2 = r8.sendingText
            if (r2 != 0) goto L_0x2801
            java.util.ArrayList<java.lang.String> r2 = r8.documentsPathsArray
            if (r2 != 0) goto L_0x2801
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r8.contactsToSend
            if (r2 != 0) goto L_0x2801
            java.util.ArrayList<android.net.Uri> r2 = r8.documentsUrisArray
            if (r2 == 0) goto L_0x24b4
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2810
        L_0x24b4:
            if (r11 == 0) goto L_0x256d
            r2 = 0
            r3 = 1
            if (r11 != r3) goto L_0x24d8
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            int r4 = r8.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            r23 = r11
            long r10 = r4.clientUserId
            java.lang.String r4 = "user_id"
            r3.putLong(r4, r10)
            org.telegram.ui.ProfileActivity r4 = new org.telegram.ui.ProfileActivity
            r4.<init>(r3)
            r3 = r4
            r10 = r23
            r3 = 6
            goto L_0x2516
        L_0x24d8:
            r23 = r11
            r10 = r23
            r3 = 2
            if (r10 != r3) goto L_0x24e8
            org.telegram.ui.ThemeActivity r3 = new org.telegram.ui.ThemeActivity
            r4 = 0
            r3.<init>(r4)
            r4 = r3
            r3 = 6
            goto L_0x2516
        L_0x24e8:
            r4 = 0
            r3 = 3
            if (r10 != r3) goto L_0x24f4
            org.telegram.ui.SessionsActivity r3 = new org.telegram.ui.SessionsActivity
            r3.<init>(r4)
            r4 = r3
            r3 = 6
            goto L_0x2516
        L_0x24f4:
            r3 = 4
            if (r10 != r3) goto L_0x24ff
            org.telegram.ui.FiltersSetupActivity r3 = new org.telegram.ui.FiltersSetupActivity
            r3.<init>()
            r4 = r3
            r3 = 6
            goto L_0x2516
        L_0x24ff:
            r3 = 5
            if (r10 != r3) goto L_0x250c
            org.telegram.ui.ActionIntroActivity r3 = new org.telegram.ui.ActionIntroActivity
            r4 = 3
            r3.<init>(r4)
            r2 = 1
            r4 = r3
            r3 = 6
            goto L_0x2516
        L_0x250c:
            r3 = 6
            if (r10 != r3) goto L_0x2515
            org.telegram.ui.EditWidgetActivity r4 = new org.telegram.ui.EditWidgetActivity
            r4.<init>(r7, r9)
            goto L_0x2516
        L_0x2515:
            r4 = 0
        L_0x2516:
            r11 = r2
            if (r10 != r3) goto L_0x252b
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r3
            r46 = r4
            r45.presentFragment(r46, r47, r48, r49, r50)
            goto L_0x2533
        L_0x252b:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60
            r3.<init>(r8, r4, r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x2533:
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x254e
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r3.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.rightActionBarLayout
            r3.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r23 = r1
            r1 = 0
            r3.setAllowOpenDrawer(r1, r1)
            r24 = r2
            goto L_0x2559
        L_0x254e:
            r23 = r1
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r24 = r2
            r2 = 1
            r3.setAllowOpenDrawer(r2, r1)
        L_0x2559:
            r34 = 1
            r2 = r103
            r50 = r5
            r35 = r23
            r11 = r100
            r23 = r22
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x256d:
            r23 = r1
            r10 = r11
            if (r38 == 0) goto L_0x25c5
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r2 = "destroyAfterSelect"
            r3 = 1
            r1.putBoolean(r2, r3)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            org.telegram.ui.ContactsActivity r3 = new org.telegram.ui.ContactsActivity
            r3.<init>(r1)
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r2
            r46 = r3
            r45.presentFragment(r46, r47, r48, r49, r50)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x25aa
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.rightActionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r8.drawerLayoutContainer
            r3 = 0
            r2.setAllowOpenDrawer(r3, r3)
            goto L_0x25b1
        L_0x25aa:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r8.drawerLayoutContainer
            r4 = 1
            r2.setAllowOpenDrawer(r4, r3)
        L_0x25b1:
            r34 = 1
            r2 = r103
            r50 = r5
            r35 = r23
            r11 = r100
            r23 = r22
            r22 = r0
            r0 = r25
            r25 = r7
            goto L_0x2857
        L_0x25c5:
            if (r22 == 0) goto L_0x2642
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r2 = "destroyAfterSelect"
            r3 = 1
            r1.putBoolean(r2, r3)
            java.lang.String r2 = "returnAsResult"
            r1.putBoolean(r2, r3)
            java.lang.String r2 = "onlyUsers"
            r1.putBoolean(r2, r3)
            java.lang.String r2 = "allowSelf"
            r3 = 0
            r1.putBoolean(r2, r3)
            org.telegram.ui.ContactsActivity r2 = new org.telegram.ui.ContactsActivity
            r2.<init>(r1)
            r3 = r22
            r2.setInitialSearchString(r3)
            r4 = r15
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda100 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda100
            r11.<init>(r8, r4, r12)
            r2.setDelegate(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r8.actionBarLayout
            r22 = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r11.getLastFragment()
            boolean r0 = r0 instanceof org.telegram.ui.ContactsActivity
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r11
            r46 = r2
            r47 = r0
            r45.presentFragment(r46, r47, r48, r49, r50)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2627
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r11 = 0
            r0.setAllowOpenDrawer(r11, r11)
            r24 = r1
            goto L_0x2630
        L_0x2627:
            r11 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r24 = r1
            r1 = 1
            r0.setAllowOpenDrawer(r1, r11)
        L_0x2630:
            r34 = 1
            r2 = r103
            r50 = r5
            r35 = r23
            r0 = r25
            r11 = r100
            r23 = r3
            r25 = r7
            goto L_0x2857
        L_0x2642:
            r3 = r22
            r22 = r0
            if (r21 == 0) goto L_0x2697
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r1 = 5
            r0.<init>(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda93 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda93
            r1.<init>(r8, r0)
            r0.setQrLoginDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r1
            r46 = r0
            r45.presentFragment(r46, r47, r48, r49, r50)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x267e
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.rightActionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r2 = 0
            r1.setAllowOpenDrawer(r2, r2)
            goto L_0x2685
        L_0x267e:
            r2 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r4 = 1
            r1.setAllowOpenDrawer(r4, r2)
        L_0x2685:
            r34 = 1
            r2 = r103
            r50 = r5
            r35 = r23
            r0 = r25
            r11 = r100
            r23 = r3
            r25 = r7
            goto L_0x2857
        L_0x2697:
            if (r19 == 0) goto L_0x2704
            org.telegram.ui.NewContactActivity r0 = new org.telegram.ui.NewContactActivity
            r0.<init>()
            if (r17 == 0) goto L_0x26b7
            java.lang.String r1 = " "
            r2 = r17
            r4 = 2
            java.lang.String[] r1 = r2.split(r1, r4)
            r4 = 0
            r11 = r1[r4]
            int r4 = r1.length
            r2 = 1
            if (r4 <= r2) goto L_0x26b3
            r4 = r1[r2]
            goto L_0x26b4
        L_0x26b3:
            r4 = 0
        L_0x26b4:
            r0.setInitialName(r11, r4)
        L_0x26b7:
            r11 = r100
            if (r11 == 0) goto L_0x26c4
            r1 = 1
            java.lang.String r2 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r11, r1)
            r1 = 0
            r0.setInitialPhoneNumber(r2, r1)
        L_0x26c4:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r47 = 0
            r48 = 1
            r49 = 1
            r50 = 0
            r45 = r1
            r46 = r0
            r45.presentFragment(r46, r47, r48, r49, r50)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x26ed
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.rightActionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r2 = 0
            r1.setAllowOpenDrawer(r2, r2)
            r4 = 1
            goto L_0x26f4
        L_0x26ed:
            r2 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r4 = 1
            r1.setAllowOpenDrawer(r4, r2)
        L_0x26f4:
            r34 = 1
            r2 = r103
            r50 = r5
            r35 = r23
            r0 = r25
            r23 = r3
            r25 = r7
            goto L_0x2857
        L_0x2704:
            r11 = r100
            r4 = 1
            if (r44 == 0) goto L_0x273a
            int r0 = r8.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r0 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            r35 = r23
            r1 = r101
            r23 = r3
            r3 = r0
            r0 = r25
            r4 = r24
            r50 = r5
            r5 = r26
            r6 = r28
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r1 == 0) goto L_0x2735
            r1 = 1
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r1
            r25 = r7
            goto L_0x2844
        L_0x2735:
            r1 = 1
            r25 = r7
            goto L_0x2844
        L_0x273a:
            r50 = r5
            r35 = r23
            r0 = r25
            r1 = 1
            r23 = r3
            if (r20 == 0) goto L_0x27cc
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.getLastFragment()
            if (r2 == 0) goto L_0x27c6
            android.app.Activity r3 = r2.getParentActivity()
            if (r3 == 0) goto L_0x27c6
            r3 = r17
            int r4 = r8.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()
            r5 = 0
            java.lang.String r4 = org.telegram.ui.NewContactActivity.getPhoneNumber(r8, r4, r11, r5)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r2.getParentActivity()
            r5.<init>((android.content.Context) r6)
            r6 = 2131626777(0x7f0e0b19, float:1.88808E38)
            java.lang.String r1 = "NewContactAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r5.setTitle(r1)
            r6 = 1
            java.lang.Object[] r5 = new java.lang.Object[r6]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r6 = r6.format(r4)
            r25 = 0
            r5[r25] = r6
            java.lang.String r6 = "NewContactAlertMessage"
            r25 = r7
            r7 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r7, r5)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r1.setMessage(r5)
            r5 = 2131626775(0x7f0e0b17, float:1.8880796E38)
            java.lang.String r6 = "NewContactAlertButton"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda92 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda92
            r6.<init>(r4, r3, r2)
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r1.setPositiveButton(r5, r6)
            r5 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r6 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 0
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = r1.setNegativeButton(r5, r6)
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            r2.showDialog(r1)
            r34 = 1
            goto L_0x27c8
        L_0x27c6:
            r25 = r7
        L_0x27c8:
            r2 = r103
            goto L_0x2857
        L_0x27cc:
            r25 = r7
            if (r27 == 0) goto L_0x2844
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            org.telegram.ui.CallLogActivity r2 = new org.telegram.ui.CallLogActivity
            r2.<init>()
            r3 = 0
            r4 = 1
            r5 = 1
            r6 = 0
            r1.presentFragment(r2, r3, r4, r5, r6)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x27f5
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.rightActionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r2 = 0
            r1.setAllowOpenDrawer(r2, r2)
            goto L_0x27fc
        L_0x27f5:
            r2 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r3 = 1
            r1.setAllowOpenDrawer(r3, r2)
        L_0x27fc:
            r34 = 1
            r2 = r103
            goto L_0x2857
        L_0x2801:
            r35 = r1
            r50 = r5
            r10 = r11
            r23 = r22
            r11 = r100
            r22 = r0
            r0 = r25
            r25 = r7
        L_0x2810:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 != 0) goto L_0x2825
            r1 = 0
            r2 = r12[r1]
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r1]
            r2.postNotificationName(r3, r4)
            goto L_0x2826
        L_0x2825:
            r1 = 0
        L_0x2826:
            int r2 = (r39 > r45 ? 1 : (r39 == r45 ? 0 : -1))
            if (r2 != 0) goto L_0x2832
            r8.openDialogsToSend(r1)
            r34 = 1
            r2 = r103
            goto L_0x2857
        L_0x2832:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r39)
            r1.add(r2)
            r2 = 0
            r3 = 0
            r8.didSelectDialogs(r2, r1, r2, r3)
        L_0x2844:
            r2 = r103
            goto L_0x2857
        L_0x2847:
            r22 = r0
            r35 = r1
            r23 = r3
            r0 = r4
            r50 = r5
            r25 = r7
            r10 = r11
            r11 = r100
            r2 = r103
        L_0x2857:
            if (r34 != 0) goto L_0x2905
            if (r2 != 0) goto L_0x2905
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x28ae
            int r1 = r8.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x2887
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x28f0
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r101.getClientNotActivatedFragment()
            r1.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r3 = 0
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x28f0
        L_0x2887:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x28f0
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r3 = 0
            r1.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r8.sideMenu
            r1.setSideMenu(r3)
            if (r0 == 0) goto L_0x28a1
            r1.setInitialSearchString(r0)
        L_0x28a1:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r3.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r4 = 1
            r5 = 0
            r3.setAllowOpenDrawer(r4, r5)
            goto L_0x28f0
        L_0x28ae:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x28f0
            int r1 = r8.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x28d4
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r101.getClientNotActivatedFragment()
            r1.addFragmentToStack(r3)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r8.drawerLayoutContainer
            r3 = 0
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x28f0
        L_0x28d4:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r3 = 0
            r1.<init>(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r8.sideMenu
            r1.setSideMenu(r3)
            if (r0 == 0) goto L_0x28e4
            r1.setInitialSearchString(r0)
        L_0x28e4:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r3.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r8.drawerLayoutContainer
            r4 = 1
            r5 = 0
            r3.setAllowOpenDrawer(r4, r5)
        L_0x28f0:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            r1.showLastFragment()
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x2905
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.layersActionBarLayout
            r1.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.rightActionBarLayout
            r1.showLastFragment()
        L_0x2905:
            if (r33 == 0) goto L_0x290d
            r1 = 0
            r1 = r12[r1]
            org.telegram.ui.VoIPFragment.show(r8, r1)
        L_0x290d:
            if (r44 != 0) goto L_0x292b
            r1 = r102
            r37 = r25
            if (r1 == 0) goto L_0x2921
            java.lang.String r3 = r102.getAction()
            java.lang.String r4 = "android.intent.action.MAIN"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x292f
        L_0x2921:
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r3 == 0) goto L_0x292f
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r3.dismiss()
            goto L_0x292f
        L_0x292b:
            r1 = r102
            r37 = r25
        L_0x292f:
            r3 = 0
            r1.setAction(r3)
            return r34
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* renamed from: lambda$handleIntent$10$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3642lambda$handleIntent$10$orgtelegramuiLaunchActivity(Intent copyIntent, boolean contactsLoaded) {
        handleIntent(copyIntent, true, false, false);
    }

    /* renamed from: lambda$handleIntent$12$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3644lambda$handleIntent$12$orgtelegramuiLaunchActivity(AlertDialog cancelDeleteProgressDialog, String finalPhone, Bundle params, TLRPC.TL_account_sendConfirmPhoneCode req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda57(this, cancelDeleteProgressDialog, error, finalPhone, params, response, req));
    }

    /* renamed from: lambda$handleIntent$11$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3643lambda$handleIntent$11$orgtelegramuiLaunchActivity(AlertDialog cancelDeleteProgressDialog, TLRPC.TL_error error, String finalPhone, Bundle params, TLObject response, TLRPC.TL_account_sendConfirmPhoneCode req) {
        cancelDeleteProgressDialog.dismiss();
        if (error == null) {
            m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new LoginActivity().cancelAccountDeletion(finalPhone, params, (TLRPC.TL_auth_sentCode) response));
        } else {
            AlertsCreator.processError(this.currentAccount, error, getActionBarLayout().getLastFragment(), req, new Object[0]);
        }
    }

    /* renamed from: lambda$handleIntent$14$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3645lambda$handleIntent$14$orgtelegramuiLaunchActivity(int[] intentAccount, LocationController.SharingLocationInfo info) {
        intentAccount[0] = info.messageObject.currentAccount;
        switchToAccount(intentAccount[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(info.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda4(intentAccount, info.messageObject.getDialogId()));
        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(locationActivity);
    }

    /* renamed from: lambda$handleIntent$15$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3646lambda$handleIntent$15$orgtelegramuiLaunchActivity() {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).showDialog(new StickersAlert((Context) this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
        }
    }

    /* renamed from: lambda$handleIntent$16$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3647lambda$handleIntent$16$orgtelegramuiLaunchActivity(BaseFragment fragment, boolean closePreviousFinal) {
        presentFragment(fragment, closePreviousFinal, false);
    }

    /* renamed from: lambda$handleIntent$17$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3648lambda$handleIntent$17$orgtelegramuiLaunchActivity(boolean videoCall, int[] intentAccount, TLRPC.User user, String param, ContactsActivity activity) {
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
        VoIPHelper.startCall(user, videoCall, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(intentAccount[0]));
    }

    /* renamed from: lambda$handleIntent$21$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3649lambda$handleIntent$21$orgtelegramuiLaunchActivity(ActionIntroActivity fragment, String code) {
        AlertDialog progressDialog = new AlertDialog(this, 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        byte[] token = Base64.decode(code.substring("tg://login?token=".length()), 8);
        TLRPC.TL_auth_acceptLoginToken req = new TLRPC.TL_auth_acceptLoginToken();
        req.token = token;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda68(progressDialog, fragment));
    }

    static /* synthetic */ void lambda$handleIntent$19(AlertDialog progressDialog, TLObject response, ActionIntroActivity fragment, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (!(response instanceof TLRPC.TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda20(fragment, error));
        }
    }

    static /* synthetic */ void lambda$handleIntent$18(ActionIntroActivity fragment, TLRPC.TL_error error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(fragment, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
    }

    static /* synthetic */ void lambda$handleIntent$22(String finalNewContactPhone, String finalNewContactName, BaseFragment lastFragment, DialogInterface d, int i) {
        NewContactActivity fragment = new NewContactActivity();
        fragment.setInitialPhoneNumber(finalNewContactPhone, false);
        if (finalNewContactName != null) {
            String[] names = finalNewContactName.split(" ", 2);
            fragment.setInitialName(names[0], names.length > 1 ? names[1] : null);
        }
        lastFragment.presentFragment(fragment);
    }

    public static int getTimestampFromLink(Uri data) {
        String timestampStr = null;
        if (data.getPathSegments().contains("video")) {
            timestampStr = data.getQuery();
        } else if (data.getQueryParameter("t") != null) {
            timestampStr = data.getQueryParameter("t");
        }
        int videoTimestamp = -1;
        if (timestampStr == null) {
            return -1;
        }
        try {
            videoTimestamp = Integer.parseInt(timestampStr);
        } catch (Throwable th) {
        }
        if (videoTimestamp != -1) {
            return videoTimestamp;
        }
        DateFormat dateFormat = new SimpleDateFormat("mm:ss");
        try {
            return (int) ((dateFormat.parse(timestampStr).getTime() - dateFormat.parse("00:00").getTime()) / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return videoTimestamp;
        }
    }

    private void openDialogsToSend(boolean animated) {
        boolean removeLast;
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putInt("dialogsType", 3);
        args.putBoolean("allowSwitchAccount", true);
        ArrayList<TLRPC.User> arrayList = this.contactsToSend;
        if (arrayList == null) {
            args.putString("selectAlertString", LocaleController.getString("SendMessagesToText", NUM));
            args.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroupText", NUM));
        } else if (arrayList.size() != 1) {
            args.putString("selectAlertString", LocaleController.getString("SendContactToText", NUM));
            args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", NUM));
        }
        AnonymousClass12 r1 = new DialogsActivity(args) {
            public boolean shouldShowNextButton(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                if (LaunchActivity.this.exportingChatUri != null) {
                    return false;
                }
                if (LaunchActivity.this.contactsToSend != null && LaunchActivity.this.contactsToSend.size() == 1 && !LaunchActivity.mainFragmentsStack.isEmpty()) {
                    return true;
                }
                if (dids.size() <= 1) {
                    if (LaunchActivity.this.videoPath != null) {
                        return true;
                    }
                    if (LaunchActivity.this.photoPathsArray == null || LaunchActivity.this.photoPathsArray.size() <= 0) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        };
        r1.setDelegate(this);
        if (AndroidUtilities.isTablet()) {
            removeLast = this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
        } else {
            removeLast = this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
        }
        this.actionBarLayout.presentFragment(r1, removeLast, !animated, true, false);
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (GroupCallActivity.groupCallInstance != null) {
            GroupCallActivity.groupCallInstance.dismiss();
        }
        if (!animated) {
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            if (AndroidUtilities.isTablet()) {
                this.actionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
                return;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
    }

    private int runCommentRequest(int intentAccount, AlertDialog progressDialog, Integer messageId, Integer commentId, Integer threadId, TLRPC.Chat chat) {
        if (chat == null) {
            return 0;
        }
        TLRPC.TL_messages_getDiscussionMessage req = new TLRPC.TL_messages_getDiscussionMessage();
        req.peer = MessagesController.getInputPeer(chat);
        req.msg_id = (commentId != null ? messageId : threadId).intValue();
        return ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda71(this, intentAccount, messageId, chat, req, commentId, threadId, progressDialog));
    }

    /* renamed from: lambda$runCommentRequest$24$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3657lambda$runCommentRequest$24$orgtelegramuiLaunchActivity(int intentAccount, Integer messageId, TLRPC.Chat chat, TLRPC.TL_messages_getDiscussionMessage req, Integer commentId, Integer threadId, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, response, intentAccount, messageId, chat, req, commentId, threadId, progressDialog));
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x00a0 A[SYNTHETIC, Splitter:B:17:0x00a0] */
    /* renamed from: lambda$runCommentRequest$23$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3656lambda$runCommentRequest$23$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r15, int r16, java.lang.Integer r17, org.telegram.tgnet.TLRPC.Chat r18, org.telegram.tgnet.TLRPC.TL_messages_getDiscussionMessage r19, java.lang.Integer r20, java.lang.Integer r21, org.telegram.ui.ActionBar.AlertDialog r22) {
        /*
            r14 = this;
            r1 = r15
            r0 = 0
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_discussionMessage
            r3 = 1
            if (r2 == 0) goto L_0x009a
            r2 = r1
            org.telegram.tgnet.TLRPC$TL_messages_discussionMessage r2 = (org.telegram.tgnet.TLRPC.TL_messages_discussionMessage) r2
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r16)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r2.users
            r6 = 0
            r4.putUsers(r5, r6)
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r16)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r2.chats
            r4.putChats(r5, r6)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r5 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r7 = r2.messages
            int r7 = r7.size()
        L_0x0029:
            if (r5 >= r7) goto L_0x0040
            org.telegram.messenger.MessageObject r8 = new org.telegram.messenger.MessageObject
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r10 = r2.messages
            java.lang.Object r10 = r10.get(r5)
            org.telegram.tgnet.TLRPC$Message r10 = (org.telegram.tgnet.TLRPC.Message) r10
            r8.<init>(r9, r10, r3, r3)
            r4.add(r8)
            int r5 = r5 + 1
            goto L_0x0029
        L_0x0040:
            boolean r5 = r4.isEmpty()
            if (r5 != 0) goto L_0x0096
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.Object r6 = r4.get(r6)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            long r6 = r6.getDialogId()
            long r6 = -r6
            java.lang.String r8 = "chat_id"
            r5.putLong(r8, r6)
            int r6 = r17.intValue()
            int r6 = java.lang.Math.max(r3, r6)
            java.lang.String r7 = "message_id"
            r5.putInt(r7, r6)
            org.telegram.ui.ChatActivity r6 = new org.telegram.ui.ChatActivity
            r6.<init>(r5)
            r13 = r19
            int r10 = r13.msg_id
            int r11 = r2.read_inbox_max_id
            int r12 = r2.read_outbox_max_id
            r7 = r6
            r8 = r4
            r9 = r18
            r7.setThreadMessages(r8, r9, r10, r11, r12)
            if (r20 == 0) goto L_0x0086
            int r7 = r20.intValue()
            r6.setHighlightMessageId(r7)
            goto L_0x008f
        L_0x0086:
            if (r21 == 0) goto L_0x008f
            int r7 = r17.intValue()
            r6.setHighlightMessageId(r7)
        L_0x008f:
            r7 = r14
            r14.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(r6)
            r0 = 1
            r2 = r0
            goto L_0x009e
        L_0x0096:
            r7 = r14
            r13 = r19
            goto L_0x009d
        L_0x009a:
            r7 = r14
            r13 = r19
        L_0x009d:
            r2 = r0
        L_0x009e:
            if (r2 != 0) goto L_0x00ce
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00ca }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x00ca }
            if (r0 != 0) goto L_0x00c9
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack     // Catch:{ Exception -> 0x00ca }
            int r4 = r0.size()     // Catch:{ Exception -> 0x00ca }
            int r4 = r4 - r3
            java.lang.Object r0 = r0.get(r4)     // Catch:{ Exception -> 0x00ca }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x00ca }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x00ca }
            java.lang.String r3 = "ChannelPostDeleted"
            r4 = 2131624945(0x7f0e03f1, float:1.8877084E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x00ca }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r3)     // Catch:{ Exception -> 0x00ca }
            r0.show()     // Catch:{ Exception -> 0x00ca }
        L_0x00c9:
            goto L_0x00ce
        L_0x00ca:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00ce:
            r22.dismiss()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d8
        L_0x00d2:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3656lambda$runCommentRequest$23$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    private void runImportRequest(Uri importUri, ArrayList<Uri> arrayList) {
        int intentAccount = UserConfig.selectedAccount;
        AlertDialog progressDialog = new AlertDialog(this, 3);
        int[] requestId = {0};
        InputStream inputStream = null;
        int linesCount = 0;
        try {
            InputStream inputStream2 = getContentResolver().openInputStream(importUri);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream2));
            StringBuilder total = new StringBuilder();
            while (true) {
                String readLine = r.readLine();
                String line = readLine;
                if (readLine == null || linesCount >= 100) {
                    String content = total.toString();
                } else {
                    total.append(line);
                    total.append(10);
                    linesCount++;
                }
            }
            String content2 = total.toString();
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            TLRPC.TL_messages_checkHistoryImport req = new TLRPC.TL_messages_checkHistoryImport();
            req.import_head = content2;
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda77(this, importUri, intentAccount, progressDialog));
            progressDialog.setOnCancelListener(new LaunchActivity$$ExternalSyntheticLambda15(intentAccount, requestId, (Runnable) null));
            try {
                progressDialog.showDelayed(300);
            } catch (Exception e) {
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
    }

    /* renamed from: lambda$runImportRequest$26$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3659lambda$runImportRequest$26$orgtelegramuiLaunchActivity(Uri importUri, int intentAccount, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, response, importUri, intentAccount, progressDialog), 2);
    }

    /* renamed from: lambda$runImportRequest$25$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3658lambda$runImportRequest$25$orgtelegramuiLaunchActivity(TLObject response, Uri importUri, int intentAccount, AlertDialog progressDialog) {
        if (!isFinishing()) {
            boolean removeLast = false;
            if (response == null || this.actionBarLayout == null) {
                if (this.documentsUrisArray == null) {
                    this.documentsUrisArray = new ArrayList<>();
                }
                this.documentsUrisArray.add(0, this.exportingChatUri);
                this.exportingChatUri = null;
                openDialogsToSend(true);
            } else {
                TLRPC.TL_messages_historyImportParsed res = (TLRPC.TL_messages_historyImportParsed) response;
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putString("importTitle", res.title);
                args.putBoolean("allowSwitchAccount", true);
                if (res.pm) {
                    args.putInt("dialogsType", 12);
                } else if (res.group) {
                    args.putInt("dialogsType", 11);
                } else {
                    String uri = importUri.toString();
                    boolean ok = false;
                    Iterator<String> it = MessagesController.getInstance(intentAccount).exportPrivateUri.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (uri.contains(it.next())) {
                                args.putInt("dialogsType", 12);
                                ok = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (!ok) {
                        Iterator<String> it2 = MessagesController.getInstance(intentAccount).exportGroupUri.iterator();
                        while (true) {
                            if (it2.hasNext()) {
                                if (uri.contains(it2.next())) {
                                    args.putInt("dialogsType", 11);
                                    ok = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!ok) {
                            args.putInt("dialogsType", 13);
                        }
                    }
                }
                if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                    SecretMediaViewer.getInstance().closePhoto(false, false);
                } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                    PhotoViewer.getInstance().closePhoto(false, true);
                } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                    ArticleViewer.getInstance().close(false, true);
                }
                if (GroupCallActivity.groupCallInstance != null) {
                    GroupCallActivity.groupCallInstance.dismiss();
                }
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                if (AndroidUtilities.isTablet()) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                } else {
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                }
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(this);
                if (AndroidUtilities.isTablet()) {
                    if (this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)) {
                        removeLast = true;
                    }
                } else if (this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)) {
                    removeLast = true;
                }
                this.actionBarLayout.presentFragment(fragment, removeLast, false, true, false);
            }
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ void lambda$runImportRequest$27(int intentAccount, int[] requestId, Runnable cancelRunnableFinal, DialogInterface dialog) {
        ConnectionsManager.getInstance(intentAccount).cancelRequest(requestId[0], true);
        if (cancelRunnableFinal != null) {
            cancelRunnableFinal.run();
        }
    }

    private void openGroupCall(AccountInstance accountInstance, TLRPC.Chat chat, String hash) {
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        VoIPHelper.startCall(chat, (TLRPC.InputPeer) null, hash, false, this, arrayList.get(arrayList.size() - 1), accountInstance);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v37, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v42, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v51, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v52, resolved type: org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runLinkRequest(int r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, java.lang.String r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, boolean r42, java.lang.Integer r43, java.lang.Long r44, java.lang.Integer r45, java.lang.Integer r46, java.lang.String r47, java.util.HashMap<java.lang.String, java.lang.String> r48, java.lang.String r49, java.lang.String r50, java.lang.String r51, java.lang.String r52, org.telegram.tgnet.TLRPC.TL_wallPaper r53, java.lang.String r54, java.lang.String r55, java.lang.String r56, java.lang.String r57, int r58, int r59, java.lang.String r60, java.lang.String r61, java.lang.String r62) {
        /*
            r32 = this;
            r15 = r32
            r14 = r33
            r13 = r34
            r12 = r35
            r11 = r36
            r10 = r41
            r9 = r44
            r8 = r48
            r7 = r49
            r6 = r50
            r5 = r53
            r4 = r54
            r3 = r55
            r2 = r58
            r0 = 2
            if (r2 != 0) goto L_0x0073
            int r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r1 < r0) goto L_0x0073
            if (r8 == 0) goto L_0x0073
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda94 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda94
            r1 = r0
            r2 = r32
            r3 = r33
            r4 = r34
            r5 = r35
            r6 = r36
            r7 = r37
            r8 = r38
            r9 = r39
            r10 = r40
            r11 = r41
            r12 = r42
            r13 = r43
            r14 = r44
            r15 = r45
            r16 = r46
            r17 = r47
            r18 = r48
            r19 = r49
            r20 = r50
            r21 = r51
            r22 = r52
            r23 = r53
            r24 = r54
            r25 = r55
            r26 = r56
            r27 = r57
            r28 = r59
            r29 = r60
            r30 = r61
            r31 = r62
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31)
            r15 = r32
            org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.Components.AlertsCreator.createAccountSelectDialog(r15, r0)
            r0.show()
            return
        L_0x0073:
            r1 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = "OK"
            r3 = 0
            r4 = 1
            r14 = 0
            if (r51 == 0) goto L_0x00c7
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            boolean r0 = r0.hasObservers(r5)
            if (r0 == 0) goto L_0x0097
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r14] = r51
            r0.postNotificationName(r1, r2)
            goto L_0x00c6
        L_0x0097:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r15)
            r5 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r6 = "AppName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setTitle(r5)
            r5 = 2131627120(0x7f0e0CLASSNAME, float:1.8881495E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r14] = r51
            java.lang.String r6 = "OtherLoginCode"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r5, r4)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            r0.setMessage(r4)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setPositiveButton(r1, r3)
            r15.showAlertDialog(r0)
        L_0x00c6:
            return
        L_0x00c7:
            if (r52 == 0) goto L_0x00f1
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r15)
            r4 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.String r5 = "AuthAnotherClient"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r4)
            r4 = 2131624535(0x7f0e0257, float:1.8876252E38)
            java.lang.String r5 = "AuthAnotherClientUrl"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setMessage(r4)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setPositiveButton(r1, r3)
            r15.showAlertDialog(r0)
            return
        L_0x00f1:
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog
            r2 = 3
            r1.<init>(r15, r2)
            r13 = r1
            int[] r1 = new int[r4]
            r1[r14] = r14
            r12 = r1
            r21 = 0
            r11 = r54
            if (r11 == 0) goto L_0x012a
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug r1 = new org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug
            r1.<init>()
            r1.slug = r11
            r0.invoice = r1
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r33)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda73
            r10 = r33
            r3.<init>(r15, r10, r11, r13)
            int r2 = r2.sendRequest(r0, r3)
            r12[r14] = r2
            r14 = r15
            r15 = r10
            r10 = r12
            r12 = r13
            r13 = r58
            goto L_0x0490
        L_0x012a:
            r10 = r33
            r9 = r34
            if (r9 == 0) goto L_0x0198
            boolean r0 = org.telegram.messenger.AndroidUtilities.isNumeric(r34)
            if (r0 == 0) goto L_0x013f
            org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone r0 = new org.telegram.tgnet.TLRPC$TL_contacts_resolvePhone
            r0.<init>()
            r0.phone = r9
            goto L_0x0147
        L_0x013f:
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r0 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r0.<init>()
            r0.username = r9
            r1 = r0
        L_0x0147:
            org.telegram.tgnet.ConnectionsManager r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r33)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r7 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
            r1 = r7
            r2 = r32
            r3 = r47
            r4 = r56
            r5 = r57
            r6 = r33
            r22 = r0
            r0 = r7
            r7 = r60
            r23 = r0
            r0 = r8
            r8 = r61
            r9 = r62
            r10 = r43
            r11 = r46
            r24 = r12
            r12 = r45
            r25 = r13
            r13 = r24
            r14 = r25
            r15 = r38
            r16 = r39
            r17 = r40
            r18 = r37
            r19 = r59
            r20 = r34
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r1 = r22
            r2 = r23
            int r0 = r0.sendRequest(r1, r2)
            r10 = r24
            r11 = 0
            r10[r11] = r0
            r14 = r32
            r15 = r33
            r13 = r58
            r12 = r25
            goto L_0x0490
        L_0x0198:
            r10 = r12
            r25 = r13
            r11 = 0
            r12 = r35
            if (r12 == 0) goto L_0x01e2
            r13 = r58
            if (r13 != 0) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r1 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r1.<init>()
            r1.hash = r12
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r33)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75
            r14 = r32
            r15 = r33
            r9 = r25
            r3.<init>(r14, r15, r9, r12)
            int r0 = r2.sendRequest(r1, r3, r0)
            r10[r11] = r0
            goto L_0x01df
        L_0x01c1:
            r14 = r32
            r15 = r33
            r9 = r25
            if (r13 != r4) goto L_0x01df
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r1 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r1.<init>()
            r1.hash = r12
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r33)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
            r3.<init>(r14, r15, r9)
            r2.sendRequest(r1, r3, r0)
            r12 = r9
            goto L_0x0490
        L_0x01df:
            r12 = r9
            goto L_0x0490
        L_0x01e2:
            r14 = r32
            r15 = r33
            r13 = r58
            r9 = r25
            r8 = r36
            if (r8 == 0) goto L_0x023f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x023e
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r8
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r4
            java.lang.Object r1 = r1.get(r2)
            r11 = r1
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            boolean r1 = r11 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x022f
            r16 = r11
            org.telegram.ui.ChatActivity r16 = (org.telegram.ui.ChatActivity) r16
            org.telegram.ui.Components.StickersAlert r17 = new org.telegram.ui.Components.StickersAlert
            r5 = 0
            org.telegram.ui.Components.ChatActivityEnterView r6 = r16.getChatActivityEnterViewForStickers()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r16.getResourceProvider()
            r1 = r17
            r2 = r32
            r3 = r11
            r4 = r0
            r1.<init>(r2, r3, r4, r5, r6, r7)
            boolean r2 = r16.isKeyboardVisible()
            r1.setCalcMandatoryInsets(r2)
            goto L_0x023b
        L_0x022f:
            org.telegram.ui.Components.StickersAlert r7 = new org.telegram.ui.Components.StickersAlert
            r5 = 0
            r6 = 0
            r1 = r7
            r2 = r32
            r3 = r11
            r4 = r0
            r1.<init>((android.content.Context) r2, (org.telegram.ui.ActionBar.BaseFragment) r3, (org.telegram.tgnet.TLRPC.InputStickerSet) r4, (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r5, (org.telegram.ui.Components.StickersAlert.StickersAlertDelegate) r6)
        L_0x023b:
            r11.showDialog(r1)
        L_0x023e:
            return
        L_0x023f:
            r7 = r41
            if (r7 == 0) goto L_0x0267
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r4)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r2)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2
            r6 = r42
            r2.<init>(r14, r6, r15, r7)
            r1.setDelegate(r2)
            r14.presentFragment(r1, r11, r4)
            r12 = r9
            goto L_0x0490
        L_0x0267:
            r6 = r42
            r5 = r48
            if (r5 == 0) goto L_0x02e5
            java.lang.String r0 = "bot_id"
            java.lang.Object r0 = r5.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            if (r0 != 0) goto L_0x0280
            return
        L_0x0280:
            java.lang.String r1 = "payload"
            java.lang.Object r1 = r5.get(r1)
            r16 = r1
            java.lang.String r16 = (java.lang.String) r16
            java.lang.String r1 = "nonce"
            java.lang.Object r1 = r5.get(r1)
            r17 = r1
            java.lang.String r17 = (java.lang.String) r17
            java.lang.String r1 = "callback_url"
            java.lang.Object r1 = r5.get(r1)
            r18 = r1
            java.lang.String r18 = (java.lang.String) r18
            org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm r1 = new org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm
            r1.<init>()
            r4 = r1
            long r1 = (long) r0
            r4.bot_id = r1
            java.lang.String r1 = "scope"
            java.lang.Object r1 = r5.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            r4.scope = r1
            java.lang.String r1 = "public_key"
            java.lang.Object r1 = r5.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            r4.public_key = r1
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r33)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda89 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda89
            r1 = r2
            r11 = r2
            r2 = r32
            r19 = r0
            r0 = r3
            r3 = r10
            r20 = r4
            r4 = r33
            r5 = r9
            r6 = r20
            r7 = r16
            r8 = r17
            r12 = r9
            r9 = r18
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r1 = r20
            int r0 = r0.sendRequest(r1, r11)
            r2 = 0
            r10[r2] = r0
            goto L_0x0490
        L_0x02e5:
            r12 = r9
            r9 = r50
            if (r9 == 0) goto L_0x0305
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r0 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r0.<init>()
            r0.path = r9
            int r1 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
            r2.<init>(r14, r12)
            int r1 = r1.sendRequest(r0, r2)
            r2 = 0
            r10[r2] = r1
            goto L_0x0490
        L_0x0305:
            java.lang.String r0 = "android"
            r11 = r49
            if (r11 == 0) goto L_0x0328
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r1 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r1.<init>()
            r1.lang_code = r11
            r1.lang_pack = r0
            int r0 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80
            r2.<init>(r14, r12)
            int r0 = r0.sendRequest(r1, r2)
            r2 = 0
            r10[r2] = r0
            goto L_0x0490
        L_0x0328:
            r8 = r53
            if (r8 == 0) goto L_0x03b0
            r1 = 0
            java.lang.String r0 = r8.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x038a
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r0 = r0.third_background_color     // Catch:{ Exception -> 0x0386 }
            if (r0 == 0) goto L_0x035d
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x0386 }
            java.lang.String r23 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r2 = r2.background_color     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r5 = r5.second_background_color     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r6 = r6.third_background_color     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r7 = r7.fourth_background_color     // Catch:{ Exception -> 0x0386 }
            r22 = r0
            r24 = r2
            r25 = r5
            r26 = r6
            r27 = r7
            r22.<init>(r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x0386 }
            goto L_0x0375
        L_0x035d:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x0386 }
            java.lang.String r2 = "c"
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r5 = r5.background_color     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r6 = r6.second_background_color     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r8.settings     // Catch:{ Exception -> 0x0386 }
            int r7 = r7.rotation     // Catch:{ Exception -> 0x0386 }
            r3 = 0
            int r7 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r7, r3)     // Catch:{ Exception -> 0x0386 }
            r0.<init>(r2, r5, r6, r7)     // Catch:{ Exception -> 0x0386 }
        L_0x0375:
            org.telegram.ui.ThemePreviewActivity r2 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x0386 }
            r3 = 0
            r5 = 0
            r2.<init>(r0, r3, r4, r5)     // Catch:{ Exception -> 0x0386 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62     // Catch:{ Exception -> 0x0386 }
            r3.<init>(r14, r2)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x0386 }
            r1 = 1
            goto L_0x038a
        L_0x0386:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x038a:
            if (r1 != 0) goto L_0x03ae
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r8.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r3 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda85
            r4.<init>(r14, r12, r8)
            int r3 = r3.sendRequest(r0, r4)
            r4 = 0
            r10[r4] = r3
        L_0x03ae:
            goto L_0x0490
        L_0x03b0:
            r7 = r55
            if (r7 == 0) goto L_0x03df
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30
            r1.<init>(r14)
            r21 = r1
            org.telegram.tgnet.TLRPC$TL_account_getTheme r1 = new org.telegram.tgnet.TLRPC$TL_account_getTheme
            r1.<init>()
            r1.format = r0
            org.telegram.tgnet.TLRPC$TL_inputThemeSlug r0 = new org.telegram.tgnet.TLRPC$TL_inputThemeSlug
            r0.<init>()
            r0.slug = r7
            r1.theme = r0
            int r2 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82
            r3.<init>(r14, r12)
            int r2 = r2.sendRequest(r1, r3)
            r3 = 0
            r10[r3] = r2
        L_0x03dd:
            goto L_0x0490
        L_0x03df:
            r6 = r44
            if (r6 == 0) goto L_0x03dd
            if (r43 == 0) goto L_0x03dd
            if (r45 == 0) goto L_0x0443
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r33)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 == 0) goto L_0x0405
            r1 = r32
            r2 = r33
            r3 = r12
            r4 = r43
            r5 = r46
            r6 = r45
            r7 = r0
            int r1 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r2 = 0
            r10[r2] = r1
            goto L_0x0442
        L_0x0405:
            org.telegram.tgnet.TLRPC$TL_channels_getChannels r1 = new org.telegram.tgnet.TLRPC$TL_channels_getChannels
            r1.<init>()
            r7 = r1
            org.telegram.tgnet.TLRPC$TL_inputChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r1.<init>()
            r6 = r1
            long r1 = r44.longValue()
            r6.channel_id = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputChannel> r1 = r7.id
            r1.add(r6)
            int r1 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda88
            r1 = r4
            r2 = r32
            r3 = r10
            r16 = r0
            r0 = r4
            r4 = r33
            r9 = r5
            r5 = r12
            r17 = r6
            r6 = r43
            r11 = r7
            r7 = r46
            r8 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            int r0 = r9.sendRequest(r11, r0)
            r1 = 0
            r10[r1] = r0
        L_0x0442:
            goto L_0x0490
        L_0x0443:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r1 = r44.longValue()
            java.lang.String r5 = "chat_id"
            r0.putLong(r5, r1)
            int r1 = r43.intValue()
            java.lang.String r2 = "message_id"
            r0.putInt(r2, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0470
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r4
            java.lang.Object r1 = r1.get(r2)
            r3 = r1
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
        L_0x0470:
            r9 = r3
            if (r9 == 0) goto L_0x047d
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r33)
            boolean r1 = r1.checkCanOpenChat(r0, r9)
            if (r1 == 0) goto L_0x0490
        L_0x047d:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda33 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda33
            r1 = r11
            r2 = r32
            r3 = r0
            r4 = r44
            r5 = r10
            r6 = r12
            r7 = r9
            r8 = r33
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
        L_0x0490:
            r1 = 0
            r0 = r10[r1]
            if (r0 == 0) goto L_0x04a6
            r1 = r21
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda26
            r0.<init>(r15, r10, r1)
            r12.setOnCancelListener(r0)
            r2 = 300(0x12c, double:1.48E-321)
            r12.showDelayed(r2)     // Catch:{ Exception -> 0x04a5 }
            goto L_0x04a6
        L_0x04a5:
            r0 = move-exception
        L_0x04a6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* renamed from: lambda$runLinkRequest$28$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3660lambda$runLinkRequest$28$orgtelegramuiLaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String botChannel, String botChatAdminParams, String message, boolean hasUrl, Integer messageId, Long channelId, Integer threadId, Integer commentId, String game, HashMap auth, String lang, String unsupportedUrl, String code, String loginToken, TLRPC.TL_wallPaper wallPaper, String inputInvoiceSlug, String theme, String voicechat, String livestream, int videoTimestamp, String setAsAttachBot, String attachMenuBotToOpen, String attachMenuBotChoose, int account) {
        int i = account;
        if (i != intentAccount) {
            switchToAccount(i, true);
        }
        runLinkRequest(account, username, group, sticker, botUser, botChat, botChannel, botChatAdminParams, message, hasUrl, messageId, channelId, threadId, commentId, game, auth, lang, unsupportedUrl, code, loginToken, wallPaper, inputInvoiceSlug, theme, voicechat, livestream, 1, videoTimestamp, setAsAttachBot, attachMenuBotToOpen, attachMenuBotChoose);
    }

    /* renamed from: lambda$runLinkRequest$30$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3662lambda$runLinkRequest$30$orgtelegramuiLaunchActivity(int intentAccount, String inputInvoiceSlug, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda47(this, error, response, intentAccount, inputInvoiceSlug, progressDialog));
    }

    /* renamed from: lambda$runLinkRequest$29$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3661lambda$runLinkRequest$29$orgtelegramuiLaunchActivity(TLRPC.TL_error error, TLObject response, int intentAccount, String inputInvoiceSlug, AlertDialog progressDialog) {
        if (error != null) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BulletinFactory.of(arrayList.get(arrayList.size() - 1)).createErrorBulletin(LocaleController.getString(NUM)).show();
        } else if (!isFinishing()) {
            if (response instanceof TLRPC.TL_payments_paymentForm) {
                TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                MessagesController.getInstance(intentAccount).putUsers(form.users, false);
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PaymentFormActivity(form, inputInvoiceSlug, getActionBarLayout().getLastFragment()));
            } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
            }
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$runLinkRequest$44$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3676lambda$runLinkRequest$44$orgtelegramuiLaunchActivity(String game, String voicechat, String livestream, int intentAccount, String setAsAttachBot, String attachMenuBotToOpen, String attachMenuBotChoose, Integer messageId, Integer commentId, Integer threadId, int[] requestId, AlertDialog progressDialog, String botChat, String botChannel, String botChatAdminParams, String botUser, int videoTimestamp, String username, TLObject response, TLRPC.TL_error error) {
        LaunchActivity$$ExternalSyntheticLambda43 launchActivity$$ExternalSyntheticLambda43 = r0;
        LaunchActivity$$ExternalSyntheticLambda43 launchActivity$$ExternalSyntheticLambda432 = new LaunchActivity$$ExternalSyntheticLambda43(this, response, error, game, voicechat, livestream, intentAccount, setAsAttachBot, attachMenuBotToOpen, attachMenuBotChoose, messageId, commentId, threadId, requestId, progressDialog, botChat, botChannel, botChatAdminParams, botUser, videoTimestamp, username);
        AndroidUtilities.runOnUIThread(launchActivity$$ExternalSyntheticLambda43, 2);
    }

    /* JADX WARNING: type inference failed for: r1v30, types: [int] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x049c A[SYNTHETIC, Splitter:B:190:0x049c] */
    /* JADX WARNING: Removed duplicated region for block: B:199:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$runLinkRequest$43$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3675lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r24, org.telegram.tgnet.TLRPC.TL_error r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, int r29, java.lang.String r30, java.lang.String r31, java.lang.String r32, java.lang.Integer r33, java.lang.Integer r34, java.lang.Integer r35, int[] r36, org.telegram.ui.ActionBar.AlertDialog r37, java.lang.String r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, int r42, java.lang.String r43) {
        /*
            r23 = this;
            r9 = r23
            r10 = r25
            r11 = r26
            r12 = r27
            r13 = r28
            r14 = r30
            r15 = r31
            r8 = r41
            r7 = r42
            boolean r0 = r23.isFinishing()
            if (r0 != 0) goto L_0x04a7
            r16 = 1
            r6 = r24
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r6 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r6
            r0 = 1
            if (r10 != 0) goto L_0x0422
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r9.actionBarLayout
            if (r1 == 0) goto L_0x0422
            if (r11 != 0) goto L_0x0029
            if (r12 == 0) goto L_0x0047
        L_0x0029:
            if (r11 == 0) goto L_0x0033
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r6.users
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0047
        L_0x0033:
            if (r12 == 0) goto L_0x003d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r6.chats
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0047
        L_0x003d:
            if (r13 == 0) goto L_0x0422
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r6.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0422
        L_0x0047:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r6.users
            r5 = 0
            r1.putUsers(r2, r5)
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r6.chats
            r1.putChats(r2, r5)
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r6.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r6.chats
            r1.putUsersAndChats(r2, r3, r5, r0)
            if (r14 == 0) goto L_0x0106
            if (r15 != 0) goto L_0x0106
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            org.telegram.tgnet.TLRPC$Peer r2 = r6.peer
            long r2 = r2.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r5 = r1.getUser(r2)
            if (r5 == 0) goto L_0x00da
            boolean r1 = r5.bot
            if (r1 == 0) goto L_0x00da
            boolean r1 = r5.bot_attach_menu
            if (r1 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot r0 = new org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot
            r0.<init>()
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            org.telegram.tgnet.TLRPC$Peer r2 = r6.peer
            long r2 = r2.user_id
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((long) r2)
            r0.bot = r1
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda72
            r1 = r3
            r2 = r23
            r10 = r3
            r3 = r29
            r14 = r4
            r4 = r32
            r17 = r5
            r18 = r6
            r6 = r30
            r15 = r7
            r7 = r18
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r14.sendRequest(r0, r10)
            goto L_0x00fe
        L_0x00b5:
            r17 = r5
            r18 = r6
            r15 = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r0
            java.lang.Object r0 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)
            r1 = 2131624708(0x7f0e0304, float:1.8876603E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x00fe
        L_0x00da:
            r17 = r5
            r18 = r6
            r15 = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r0
            java.lang.Object r0 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)
            r1 = 2131624732(0x7f0e031c, float:1.8876652E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
        L_0x00fe:
            r1 = r25
            r15 = r31
            r10 = r18
            goto L_0x049a
        L_0x0106:
            r18 = r6
            r15 = r7
            if (r33 == 0) goto L_0x014e
            if (r34 != 0) goto L_0x0114
            if (r35 == 0) goto L_0x0110
            goto L_0x0114
        L_0x0110:
            r10 = r18
            r14 = 0
            goto L_0x0151
        L_0x0114:
            r10 = r18
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r10.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x014c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r10.chats
            java.lang.Object r0 = r0.get(r5)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC.Chat) r7
            r1 = r23
            r2 = r29
            r3 = r37
            r4 = r33
            r14 = 0
            r5 = r34
            r6 = r35
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r36[r14] = r0
            r0 = r36[r14]
            if (r0 == 0) goto L_0x0146
            r16 = 0
            r1 = r25
            r15 = r31
            goto L_0x049a
        L_0x0146:
            r1 = r25
            r15 = r31
            goto L_0x049a
        L_0x014c:
            r14 = 0
            goto L_0x0151
        L_0x014e:
            r10 = r18
            r14 = 0
        L_0x0151:
            java.lang.String r1 = "dialogsType"
            java.lang.String r2 = "onlySelect"
            if (r11 == 0) goto L_0x0261
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putBoolean(r2, r0)
            java.lang.String r2 = "cantSendToChannels"
            r3.putBoolean(r2, r0)
            r3.putInt(r1, r0)
            r1 = 2131628187(0x7f0e109b, float:1.888366E38)
            java.lang.String r2 = "SendGameToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "selectAlertString"
            r3.putString(r2, r1)
            r1 = 2131628186(0x7f0e109a, float:1.8883658E38)
            java.lang.String r2 = "SendGameToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "selectAlertStringGroup"
            r3.putString(r2, r1)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda102 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda102
            r6 = r29
            r2.<init>(r9, r11, r6, r10)
            r1.setDelegate(r2)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x01bc
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x01b9
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r9.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            int r4 = r4 - r0
            java.lang.Object r2 = r2.get(r4)
            boolean r2 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x01b9
            r5 = 1
            goto L_0x01ba
        L_0x01b9:
            r5 = 0
        L_0x01ba:
            r2 = r5
            goto L_0x01df
        L_0x01bc:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 <= r0) goto L_0x01dd
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r9.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r9.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            int r4 = r4 - r0
            java.lang.Object r2 = r2.get(r4)
            boolean r2 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x01dd
            r5 = 1
            goto L_0x01de
        L_0x01dd:
            r5 = 0
        L_0x01de:
            r2 = r5
        L_0x01df:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r9.actionBarLayout
            r20 = 1
            r21 = 1
            r22 = 0
            r17 = r4
            r18 = r1
            r19 = r2
            r17.presentFragment(r18, r19, r20, r21, r22)
            boolean r4 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r4 == 0) goto L_0x0208
            org.telegram.ui.SecretMediaViewer r4 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r4 = r4.isVisible()
            if (r4 == 0) goto L_0x0208
            org.telegram.ui.SecretMediaViewer r4 = org.telegram.ui.SecretMediaViewer.getInstance()
            r4.closePhoto(r14, r14)
            goto L_0x0237
        L_0x0208:
            boolean r4 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r4 == 0) goto L_0x0220
            org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r4 = r4.isVisible()
            if (r4 == 0) goto L_0x0220
            org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.getInstance()
            r4.closePhoto(r14, r0)
            goto L_0x0237
        L_0x0220:
            boolean r4 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r4 == 0) goto L_0x0237
            org.telegram.ui.ArticleViewer r4 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r4 = r4.isVisible()
            if (r4 == 0) goto L_0x0237
            org.telegram.ui.ArticleViewer r4 = org.telegram.ui.ArticleViewer.getInstance()
            r4.close(r14, r0)
        L_0x0237:
            org.telegram.ui.GroupCallActivity r4 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r4 == 0) goto L_0x0240
            org.telegram.ui.GroupCallActivity r4 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r4.dismiss()
        L_0x0240:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r9.drawerLayoutContainer
            r4.setAllowOpenDrawer(r14, r14)
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x0256
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r9.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r9.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x025b
        L_0x0256:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r9.drawerLayoutContainer
            r4.setAllowOpenDrawer(r0, r14)
        L_0x025b:
            r1 = r25
            r15 = r31
            goto L_0x049a
        L_0x0261:
            r6 = r29
            r3 = 0
            if (r38 != 0) goto L_0x0379
            if (r39 == 0) goto L_0x026c
            r15 = r31
            goto L_0x037b
        L_0x026c:
            r1 = 0
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r7 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r10.chats
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0296
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r10.chats
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            long r4 = r2.id
            java.lang.String r2 = "chat_id"
            r7.putLong(r2, r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r10.chats
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            long r4 = r2.id
            long r4 = -r4
            goto L_0x02af
        L_0x0296:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            long r4 = r2.id
            java.lang.String r2 = "user_id"
            r7.putLong(r2, r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            long r4 = r2.id
        L_0x02af:
            if (r8 == 0) goto L_0x02ce
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x02ce
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x02ce
            java.lang.String r2 = "botUser"
            r7.putString(r2, r8)
            r1 = 1
            r17 = r1
            goto L_0x02d0
        L_0x02ce:
            r17 = r1
        L_0x02d0:
            boolean r1 = r9.navigateToPremiumBot
            if (r1 == 0) goto L_0x02db
            r9.navigateToPremiumBot = r14
            java.lang.String r1 = "premium_bot"
            r7.putBoolean(r1, r0)
        L_0x02db:
            if (r33 == 0) goto L_0x02e6
            int r1 = r33.intValue()
            java.lang.String r2 = "message_id"
            r7.putInt(r2, r1)
        L_0x02e6:
            if (r12 == 0) goto L_0x02ed
            java.lang.String r1 = "voicechat"
            r7.putString(r1, r12)
        L_0x02ed:
            if (r13 == 0) goto L_0x02f4
            java.lang.String r1 = "livestream"
            r7.putString(r1, r13)
        L_0x02f4:
            if (r15 < 0) goto L_0x02fb
            java.lang.String r1 = "video_timestamp"
            r7.putInt(r1, r15)
        L_0x02fb:
            r15 = r31
            if (r15 == 0) goto L_0x0304
            java.lang.String r1 = "attach_bot"
            r7.putString(r1, r15)
        L_0x0304:
            r2 = r30
            if (r2 == 0) goto L_0x030d
            java.lang.String r1 = "attach_bot_start_command"
            r7.putString(r1, r2)
        L_0x030d:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0325
            if (r12 != 0) goto L_0x0325
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r0
            java.lang.Object r0 = r1.get(r3)
            r3 = r0
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
        L_0x0325:
            r0 = r3
            if (r0 == 0) goto L_0x0332
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            boolean r1 = r1.checkCanOpenChat(r7, r0)
            if (r1 == 0) goto L_0x0375
        L_0x0332:
            if (r17 == 0) goto L_0x034a
            boolean r1 = r0 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x034a
            r1 = r0
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r18 = r1.getDialogId()
            int r1 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r1 != 0) goto L_0x034a
            r1 = r0
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            r1.setBotUser(r8)
            goto L_0x0375
        L_0x034a:
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r29)
            if (r33 != 0) goto L_0x0351
            goto L_0x0356
        L_0x0351:
            int r1 = r33.intValue()
            r14 = r1
        L_0x0356:
            org.telegram.ui.LaunchActivity$14 r1 = new org.telegram.ui.LaunchActivity$14
            r18 = r1
            r2 = r23
            r11 = r3
            r3 = r37
            r19 = r4
            r4 = r28
            r5 = r0
            r21 = r7
            r6 = r19
            r8 = r21
            r1.<init>(r3, r4, r5, r6, r8)
            r4 = r19
            r11.ensureMessagesLoaded(r4, r14, r1)
            r1 = 0
            r16 = r1
        L_0x0375:
            r1 = r25
            goto L_0x049a
        L_0x0379:
            r15 = r31
        L_0x037b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r10.users
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x038c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r10.users
            java.lang.Object r4 = r4.get(r14)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
            goto L_0x038d
        L_0x038c:
            r4 = r3
        L_0x038d:
            r8 = r4
            if (r8 == 0) goto L_0x03f3
            boolean r4 = r8.bot
            if (r4 == 0) goto L_0x0399
            boolean r4 = r8.bot_nochats
            if (r4 == 0) goto L_0x0399
            goto L_0x03f3
        L_0x0399:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            r11 = r4
            r11.putBoolean(r2, r0)
            r2 = 2
            r11.putInt(r1, r2)
            java.lang.String r1 = "resetDelegate"
            r11.putBoolean(r1, r14)
            java.lang.String r1 = "closeFragment"
            r11.putBoolean(r1, r14)
            if (r38 == 0) goto L_0x03b4
            r5 = 1
            goto L_0x03b5
        L_0x03b4:
            r5 = 0
        L_0x03b5:
            java.lang.String r1 = "allowGroups"
            r11.putBoolean(r1, r5)
            if (r39 == 0) goto L_0x03bd
            goto L_0x03be
        L_0x03bd:
            r0 = 0
        L_0x03be:
            java.lang.String r1 = "allowChannels"
            r11.putBoolean(r1, r0)
            boolean r0 = android.text.TextUtils.isEmpty(r38)
            if (r0 == 0) goto L_0x03d4
            boolean r0 = android.text.TextUtils.isEmpty(r39)
            if (r0 == 0) goto L_0x03d1
            r6 = r3
            goto L_0x03d6
        L_0x03d1:
            r6 = r39
            goto L_0x03d6
        L_0x03d4:
            r6 = r38
        L_0x03d6:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r11)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda101 r14 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda101
            r1 = r14
            r2 = r23
            r3 = r29
            r4 = r8
            r5 = r40
            r7 = r0
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.setDelegate(r14)
            r9.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(r0)
            r1 = r25
            goto L_0x049a
        L_0x03f3:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x041d }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x041d }
            if (r1 != 0) goto L_0x041c
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x041d }
            int r2 = r1.size()     // Catch:{ Exception -> 0x041d }
            int r2 = r2 - r0
            java.lang.Object r0 = r1.get(r2)     // Catch:{ Exception -> 0x041d }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x041d }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x041d }
            java.lang.String r1 = "BotCantJoinGroups"
            r2 = 2131624709(0x7f0e0305, float:1.8876605E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x041d }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x041d }
            r0.show()     // Catch:{ Exception -> 0x041d }
        L_0x041c:
            goto L_0x0421
        L_0x041d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0421:
            return
        L_0x0422:
            r10 = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0494 }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x0494 }
            if (r1 != 0) goto L_0x0491
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0494 }
            int r2 = r1.size()     // Catch:{ Exception -> 0x0494 }
            int r2 = r2 - r0
            java.lang.Object r0 = r1.get(r2)     // Catch:{ Exception -> 0x0494 }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x0494 }
            r1 = r25
            if (r1 == 0) goto L_0x045f
            java.lang.String r2 = r1.text     // Catch:{ Exception -> 0x048f }
            if (r2 == 0) goto L_0x045f
            java.lang.String r2 = r1.text     // Catch:{ Exception -> 0x048f }
            java.lang.String r3 = "FLOOD_WAIT"
            boolean r2 = r2.startsWith(r3)     // Catch:{ Exception -> 0x048f }
            if (r2 == 0) goto L_0x045f
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x048f }
            java.lang.String r3 = "FloodWait"
            r4 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x048f }
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r3)     // Catch:{ Exception -> 0x048f }
            r2.show()     // Catch:{ Exception -> 0x048f }
            goto L_0x0493
        L_0x045f:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isNumeric(r43)     // Catch:{ Exception -> 0x048f }
            if (r2 == 0) goto L_0x047a
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x048f }
            java.lang.String r3 = "NoPhoneFound"
            r4 = 2131626846(0x7f0e0b5e, float:1.888094E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x048f }
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r3)     // Catch:{ Exception -> 0x048f }
            r2.show()     // Catch:{ Exception -> 0x048f }
            goto L_0x0493
        L_0x047a:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x048f }
            java.lang.String r3 = "NoUsernameFound"
            r4 = 2131626877(0x7f0e0b7d, float:1.8881003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x048f }
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r3)     // Catch:{ Exception -> 0x048f }
            r2.show()     // Catch:{ Exception -> 0x048f }
            goto L_0x0493
        L_0x048f:
            r0 = move-exception
            goto L_0x0497
        L_0x0491:
            r1 = r25
        L_0x0493:
            goto L_0x049a
        L_0x0494:
            r0 = move-exception
            r1 = r25
        L_0x0497:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x049a:
            if (r16 == 0) goto L_0x04a8
            r37.dismiss()     // Catch:{ Exception -> 0x04a0 }
            goto L_0x04a8
        L_0x04a0:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x04a8
        L_0x04a7:
            r1 = r10
        L_0x04a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3675lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void");
    }

    /* renamed from: lambda$runLinkRequest$36$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3668lambda$runLinkRequest$36$orgtelegramuiLaunchActivity(int intentAccount, String attachMenuBotChoose, TLRPC.User user, String setAsAttachBot, TLRPC.TL_contacts_resolvedPeer res, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda40(this, response1, intentAccount, attachMenuBotChoose, user, setAsAttachBot, res));
    }

    /* renamed from: lambda$runLinkRequest$35$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3667lambda$runLinkRequest$35$orgtelegramuiLaunchActivity(TLObject response1, int intentAccount, String attachMenuBotChoose, TLRPC.User user, String setAsAttachBot, TLRPC.TL_contacts_resolvedPeer res) {
        DialogsActivity dialogsActivity;
        TLObject tLObject = response1;
        TLRPC.User user2 = user;
        String str = setAsAttachBot;
        if (tLObject instanceof TLRPC.TL_attachMenuBotsBot) {
            TLRPC.TL_attachMenuBotsBot attachMenuBotsBot = (TLRPC.TL_attachMenuBotsBot) tLObject;
            MessagesController.getInstance(intentAccount).putUsers(attachMenuBotsBot.users, false);
            TLRPC.TL_attachMenuBot attachMenuBot = attachMenuBotsBot.bot;
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            List<String> chooserTargets = new ArrayList<>();
            if (!TextUtils.isEmpty(attachMenuBotChoose)) {
                for (String target : attachMenuBotChoose.split(" ")) {
                    if (MediaDataController.canShowAttachMenuBotForTarget(attachMenuBot, target)) {
                        chooserTargets.add(target);
                    }
                }
            } else {
                String str2 = attachMenuBotChoose;
            }
            if (!chooserTargets.isEmpty()) {
                Bundle args = new Bundle();
                args.putInt("dialogsType", 14);
                args.putBoolean("onlySelect", true);
                args.putBoolean("allowGroups", chooserTargets.contains("groups"));
                args.putBoolean("allowUsers", chooserTargets.contains("users"));
                args.putBoolean("allowChannels", chooserTargets.contains("channels"));
                args.putBoolean("allowBots", chooserTargets.contains("bots"));
                DialogsActivity dialogsActivity2 = new DialogsActivity(args);
                dialogsActivity2.setDelegate(new LaunchActivity$$ExternalSyntheticLambda1(this, user2, str, intentAccount));
                dialogsActivity = dialogsActivity2;
            } else {
                int i = intentAccount;
                dialogsActivity = null;
            }
            if (attachMenuBot.inactive) {
                AttachBotIntroTopView introTopView = new AttachBotIntroTopView(this);
                introTopView.setColor(Theme.getColor("chat_attachContactIcon"));
                introTopView.setBackgroundColor(Theme.getColor("dialogTopBackground"));
                introTopView.setAttachBot(attachMenuBot);
                AlertDialog.Builder message = new AlertDialog.Builder((Context) this).setTopView(introTopView).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotRequestAttachPermission", NUM, UserObject.getUserName(user))));
                LaunchActivity$$ExternalSyntheticLambda5 launchActivity$$ExternalSyntheticLambda5 = r0;
                String string = LocaleController.getString(NUM);
                AttachBotIntroTopView attachBotIntroTopView = introTopView;
                DialogsActivity dialogsActivity3 = dialogsActivity;
                LaunchActivity$$ExternalSyntheticLambda5 launchActivity$$ExternalSyntheticLambda52 = new LaunchActivity$$ExternalSyntheticLambda5(this, intentAccount, res, dialogsActivity, lastFragment, user, setAsAttachBot);
                message.setPositiveButton(string, launchActivity$$ExternalSyntheticLambda5).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
            } else if (dialogsActivity != null) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(dialogsActivity);
            } else if (lastFragment instanceof ChatActivity) {
                ((ChatActivity) lastFragment).openAttachBotLayout(user2.id, str);
            } else {
                BulletinFactory.of(lastFragment).createErrorBulletin(LocaleController.getString(NUM)).show();
            }
        } else {
            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
            BulletinFactory.of(arrayList2.get(arrayList2.size() - 1)).createErrorBulletin(LocaleController.getString(NUM)).show();
        }
    }

    /* renamed from: lambda$runLinkRequest$31$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3663lambda$runLinkRequest$31$orgtelegramuiLaunchActivity(TLRPC.User user, String setAsAttachBot, int intentAccount, DialogsActivity fragment, ArrayList dids, CharSequence message1, boolean param) {
        String str = setAsAttachBot;
        long did = ((Long) dids.get(0)).longValue();
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did)) {
            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args1.putLong("user_id", did);
        } else {
            args1.putLong("chat_id", -did);
        }
        args1.putString("attach_bot", user.username);
        if (str != null) {
            args1.putString("attach_bot_start_command", str);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args1, fragment)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(args1), true, false, true, false);
            return;
        }
    }

    /* renamed from: lambda$runLinkRequest$34$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3666lambda$runLinkRequest$34$orgtelegramuiLaunchActivity(int intentAccount, TLRPC.TL_contacts_resolvedPeer res, DialogsActivity dialogsActivity, BaseFragment lastFragment, TLRPC.User user, String setAsAttachBot, DialogInterface dialog, int which) {
        TLRPC.TL_messages_toggleBotInAttachMenu botRequest = new TLRPC.TL_messages_toggleBotInAttachMenu();
        botRequest.bot = MessagesController.getInstance(intentAccount).getInputUser(res.peer.user_id);
        botRequest.enabled = true;
        ConnectionsManager.getInstance(intentAccount).sendRequest(botRequest, new LaunchActivity$$ExternalSyntheticLambda76(this, intentAccount, dialogsActivity, lastFragment, user, setAsAttachBot), 66);
    }

    /* renamed from: lambda$runLinkRequest$33$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3665lambda$runLinkRequest$33$orgtelegramuiLaunchActivity(int intentAccount, DialogsActivity dialogsActivity, BaseFragment lastFragment, TLRPC.User user, String setAsAttachBot, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, response2, intentAccount, dialogsActivity, lastFragment, user, setAsAttachBot));
    }

    /* renamed from: lambda$runLinkRequest$32$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3664lambda$runLinkRequest$32$orgtelegramuiLaunchActivity(TLObject response2, int intentAccount, DialogsActivity dialogsActivity, BaseFragment lastFragment, TLRPC.User user, String setAsAttachBot) {
        if (response2 instanceof TLRPC.TL_boolTrue) {
            MediaDataController.getInstance(intentAccount).loadAttachMenuBots(false, true);
            if (dialogsActivity != null) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(dialogsActivity);
            } else if (lastFragment instanceof ChatActivity) {
                ((ChatActivity) lastFragment).openAttachBotLayout(user.id, setAsAttachBot);
            }
        }
    }

    /* renamed from: lambda$runLinkRequest$37$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3669lambda$runLinkRequest$37$orgtelegramuiLaunchActivity(String game, int intentAccount, TLRPC.TL_contacts_resolvedPeer res, DialogsActivity fragment1, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        TLRPC.TL_inputMediaGame inputMediaGame = new TLRPC.TL_inputMediaGame();
        inputMediaGame.id = new TLRPC.TL_inputGameShortName();
        inputMediaGame.id.short_name = game;
        inputMediaGame.id.bot_id = MessagesController.getInstance(intentAccount).getInputUser(res.users.get(0));
        SendMessagesHelper.getInstance(intentAccount).sendGame(MessagesController.getInstance(intentAccount).getInputPeer(did), inputMediaGame, 0, 0);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did)) {
            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args1.putLong("user_id", did);
        } else {
            args1.putLong("chat_id", -did);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args1, fragment1)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(args1), true, false, true, false);
            return;
        }
    }

    /* renamed from: lambda$runLinkRequest$42$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3674lambda$runLinkRequest$42$orgtelegramuiLaunchActivity(int intentAccount, TLRPC.User user, String botChatAdminParams, String botHash, DialogsActivity fragment, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
        if (chat == null) {
            TLRPC.User user2 = user;
        } else if (chat.creator || (chat.admin_rights != null && chat.admin_rights.add_admins)) {
            MessagesController instance = MessagesController.getInstance(intentAccount);
            TLRPC.User user3 = user;
            LaunchActivity$$ExternalSyntheticLambda66 launchActivity$$ExternalSyntheticLambda66 = r0;
            LaunchActivity$$ExternalSyntheticLambda66 launchActivity$$ExternalSyntheticLambda662 = new LaunchActivity$$ExternalSyntheticLambda66(this, botChatAdminParams, botHash, intentAccount, chat, fragment, user3, did);
            instance.checkIsInChat(chat, user3, launchActivity$$ExternalSyntheticLambda66);
            return;
        } else {
            TLRPC.User user4 = user;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AddBot", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, UserObject.getUserName(user), chat == null ? "" : chat.title)));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("AddBot", NUM), new LaunchActivity$$ExternalSyntheticLambda6(this, did, intentAccount, user, botHash));
        builder.show();
    }

    /* renamed from: lambda$runLinkRequest$40$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3672lambda$runLinkRequest$40$orgtelegramuiLaunchActivity(String botChatAdminParams, String botHash, int intentAccount, TLRPC.Chat chat, DialogsActivity fragment, TLRPC.User user, long did, boolean isInChatAlready, TLRPC.TL_chatAdminRights currentRights, String currentRank) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda35(this, botChatAdminParams, currentRights, isInChatAlready, botHash, intentAccount, chat, fragment, user, did, currentRank));
    }

    /* renamed from: lambda$runLinkRequest$39$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3671lambda$runLinkRequest$39$orgtelegramuiLaunchActivity(String botChatAdminParams, TLRPC.TL_chatAdminRights currentRights, boolean isInChatAlready, String botHash, int intentAccount, TLRPC.Chat chat, DialogsActivity fragment, TLRPC.User user, long did, String currentRank) {
        TLRPC.TL_chatAdminRights requestingRights;
        TLRPC.TL_chatAdminRights editRights;
        String str = botChatAdminParams;
        final int i = intentAccount;
        TLRPC.Chat chat2 = chat;
        final DialogsActivity dialogsActivity = fragment;
        boolean z = true;
        if (str != null) {
            String[] adminParams = str.split("\\+| ");
            TLRPC.TL_chatAdminRights requestingRights2 = new TLRPC.TL_chatAdminRights();
            for (String adminParam : adminParams) {
                char c = 65535;
                switch (adminParam.hashCode()) {
                    case -2110462504:
                        if (adminParam.equals("ban_users")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -2095811475:
                        if (adminParam.equals("anonymous")) {
                            c = 14;
                            break;
                        }
                        break;
                    case -1654794275:
                        if (adminParam.equals("change_info")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1593320096:
                        if (adminParam.equals("delete_messages")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -939200543:
                        if (adminParam.equals("edit_messages")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 22162680:
                        if (adminParam.equals("manage_call")) {
                            c = 11;
                            break;
                        }
                        break;
                    case 22169074:
                        if (adminParam.equals("manage_chat")) {
                            c = 12;
                            break;
                        }
                        break;
                    case 106069776:
                        if (adminParam.equals("other")) {
                            c = 13;
                            break;
                        }
                        break;
                    case 449085338:
                        if (adminParam.equals("promote_members")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 632157522:
                        if (adminParam.equals("invite_users")) {
                            c = 8;
                            break;
                        }
                        break;
                    case 758599179:
                        if (adminParam.equals("post_messages")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1357805750:
                        if (adminParam.equals("pin_messages")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 1529816162:
                        if (adminParam.equals("add_admins")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1542893206:
                        if (adminParam.equals("restrict_members")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1641337725:
                        if (adminParam.equals("manage_video_chats")) {
                            c = 10;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        requestingRights2.change_info = true;
                        break;
                    case 1:
                        requestingRights2.post_messages = true;
                        break;
                    case 2:
                        requestingRights2.edit_messages = true;
                        break;
                    case 3:
                    case 4:
                        requestingRights2.add_admins = true;
                        break;
                    case 5:
                        requestingRights2.delete_messages = true;
                        break;
                    case 6:
                    case 7:
                        requestingRights2.ban_users = true;
                        break;
                    case 8:
                        requestingRights2.invite_users = true;
                        break;
                    case 9:
                        requestingRights2.pin_messages = true;
                        break;
                    case 10:
                    case 11:
                        requestingRights2.manage_call = true;
                        break;
                    case 12:
                    case 13:
                        requestingRights2.other = true;
                        break;
                    case 14:
                        requestingRights2.anonymous = true;
                        break;
                }
            }
            requestingRights = requestingRights2;
        } else {
            requestingRights = null;
        }
        if (requestingRights == null && currentRights == null) {
            editRights = null;
        } else if (requestingRights == null) {
            editRights = currentRights;
        } else if (currentRights == null) {
            editRights = requestingRights;
        } else {
            TLRPC.TL_chatAdminRights editRights2 = currentRights;
            editRights2.change_info = requestingRights.change_info || editRights2.change_info;
            editRights2.post_messages = requestingRights.post_messages || editRights2.post_messages;
            editRights2.edit_messages = requestingRights.edit_messages || editRights2.edit_messages;
            editRights2.add_admins = requestingRights.add_admins || editRights2.add_admins;
            editRights2.delete_messages = requestingRights.delete_messages || editRights2.delete_messages;
            editRights2.ban_users = requestingRights.ban_users || editRights2.ban_users;
            editRights2.invite_users = requestingRights.invite_users || editRights2.invite_users;
            editRights2.pin_messages = requestingRights.pin_messages || editRights2.pin_messages;
            editRights2.manage_call = requestingRights.manage_call || editRights2.manage_call;
            editRights2.anonymous = requestingRights.anonymous || editRights2.anonymous;
            if (!requestingRights.other && !editRights2.other) {
                z = false;
            }
            editRights2.other = z;
            editRights = editRights2;
        }
        if (!isInChatAlready || requestingRights != null || TextUtils.isEmpty(botHash)) {
            ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(user.id, -did, editRights, (TLRPC.TL_chatBannedRights) null, (TLRPC.TL_chatBannedRights) null, currentRank, 2, true, !isInChatAlready, botHash);
            chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                    dialogsActivity.removeSelfFromStack();
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }

                public void didChangeOwner(TLRPC.User user) {
                }
            });
            this.actionBarLayout.presentFragment(chatRightsEditActivity, false);
            return;
        }
        MessagesController.getInstance(this.currentAccount).addUserToChat(chat2.id, user, 0, botHash, fragment, true, new LaunchActivity$$ExternalSyntheticLambda32(this, i, chat2, dialogsActivity), (MessagesController.ErrorDelegate) null);
        TLRPC.User user2 = user;
        long j = did;
    }

    /* renamed from: lambda$runLinkRequest$38$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3670lambda$runLinkRequest$38$orgtelegramuiLaunchActivity(int intentAccount, TLRPC.Chat chat, DialogsActivity fragment) {
        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        args1.putLong("chat_id", chat.id);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args1, fragment)) {
            presentFragment(new ChatActivity(args1), true, false);
        }
    }

    /* renamed from: lambda$runLinkRequest$41$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3673lambda$runLinkRequest$41$orgtelegramuiLaunchActivity(long did, int intentAccount, TLRPC.User user, String botHash, DialogInterface di, int i) {
        long j = did;
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putLong("chat_id", -j);
        ChatActivity chatActivity = new ChatActivity(args12);
        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(intentAccount).addUserToChat(-j, user, 0, botHash, chatActivity, (Runnable) null);
        this.actionBarLayout.presentFragment(chatActivity, true, false, true, false);
    }

    /* renamed from: lambda$runLinkRequest$47$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3678lambda$runLinkRequest$47$orgtelegramuiLaunchActivity(int intentAccount, AlertDialog progressDialog, String group, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda49(this, error, response, intentAccount, progressDialog, group));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0085, code lost:
        if (r1.checkCanOpenChat(r12, r2.get(r2.size() - 1)) != false) goto L_0x0087;
     */
    /* renamed from: lambda$runLinkRequest$46$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3677lambda$runLinkRequest$46$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLRPC.TL_error r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.ui.ActionBar.AlertDialog r21, java.lang.String r22) {
        /*
            r17 = this;
            r7 = r17
            r8 = r18
            boolean r0 = r17.isFinishing()
            if (r0 != 0) goto L_0x0150
            r0 = 1
            r1 = 0
            if (r8 != 0) goto L_0x00dc
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            if (r2 == 0) goto L_0x00dc
            r9 = r19
            org.telegram.tgnet.TLRPC$ChatInvite r9 = (org.telegram.tgnet.TLRPC.ChatInvite) r9
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            r3 = 1
            if (r2 == 0) goto L_0x00b3
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r2)
            if (r2 == 0) goto L_0x003d
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = r2.kicked
            if (r2 != 0) goto L_0x00b3
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            java.lang.String r2 = r2.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x003d
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_chatInvitePeek
            if (r2 != 0) goto L_0x003d
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = r2.has_geo
            if (r2 == 0) goto L_0x00b3
        L_0x003d:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r4 = r9.chat
            r10 = 0
            r2.putChat(r4, r10)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r11 = r2
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            r11.add(r2)
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r20)
            r2.putUsersAndChats(r1, r11, r10, r3)
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r12 = r1
            org.telegram.tgnet.TLRPC$Chat r1 = r9.chat
            long r1 = r1.id
            java.lang.String r4 = "chat_id"
            r12.putLong(r4, r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0087
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r20)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r12, r2)
            if (r1 == 0) goto L_0x00b2
        L_0x0087:
            boolean[] r13 = new boolean[r3]
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda37 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda37
            r1.<init>(r13)
            r14 = r21
            r14.setOnCancelListener(r1)
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r1 = r9.chat
            long r1 = r1.id
            long r5 = -r1
            org.telegram.ui.LaunchActivity$15 r4 = new org.telegram.ui.LaunchActivity$15
            r1 = r4
            r2 = r17
            r3 = r21
            r10 = r4
            r4 = r13
            r16 = r13
            r13 = r5
            r5 = r12
            r6 = r9
            r1.<init>(r3, r4, r5, r6)
            r1 = 0
            r15.ensureMessagesLoaded(r13, r1, r10)
            r0 = 0
        L_0x00b2:
            goto L_0x00da
        L_0x00b3:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r4 = r2.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            r10 = r2
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            org.telegram.ui.Components.JoinGroupAlert r11 = new org.telegram.ui.Components.JoinGroupAlert
            boolean r2 = r10 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x00cc
            r1 = r10
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            org.telegram.ui.ChatActivity$ThemeDelegate r1 = r1.themeDelegate
        L_0x00cc:
            r6 = r1
            r1 = r11
            r2 = r17
            r3 = r9
            r4 = r22
            r5 = r10
            r1.<init>(r2, r3, r4, r5, r6)
            r10.showDialog(r11)
        L_0x00da:
            r1 = r0
            goto L_0x0143
        L_0x00dc:
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>((android.content.Context) r7)
            r3 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r4 = "AppName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setTitle(r3)
            java.lang.String r3 = r8.text
            java.lang.String r4 = "FLOOD_WAIT"
            boolean r3 = r3.startsWith(r4)
            if (r3 == 0) goto L_0x0104
            r3 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r4 = "FloodWait"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setMessage(r3)
            goto L_0x0133
        L_0x0104:
            java.lang.String r3 = r8.text
            java.lang.String r4 = "INVITE_HASH_EXPIRED"
            boolean r3 = r3.startsWith(r4)
            if (r3 == 0) goto L_0x0127
            r3 = 2131625789(0x7f0e073d, float:1.8878796E38)
            java.lang.String r4 = "ExpiredLink"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setTitle(r3)
            r3 = 2131626260(0x7f0e0914, float:1.8879751E38)
            java.lang.String r4 = "InviteExpired"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setMessage(r3)
            goto L_0x0133
        L_0x0127:
            r3 = 2131626325(0x7f0e0955, float:1.8879883E38)
            java.lang.String r4 = "JoinToGroupErrorNotExist"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setMessage(r3)
        L_0x0133:
            r3 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r4 = "OK"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setPositiveButton(r3, r1)
            r7.showAlertDialog(r2)
            r1 = r0
        L_0x0143:
            if (r1 == 0) goto L_0x0150
            r21.dismiss()     // Catch:{ Exception -> 0x0149 }
            goto L_0x0150
        L_0x0149:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0150:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3677lambda$runLinkRequest$46$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$runLinkRequest$45(boolean[] canceled, DialogInterface dialog) {
        canceled[0] = true;
    }

    /* renamed from: lambda$runLinkRequest$49$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3680lambda$runLinkRequest$49$orgtelegramuiLaunchActivity(int intentAccount, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(intentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda58(this, progressDialog, error, response, intentAccount));
    }

    /* renamed from: lambda$runLinkRequest$48$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3679lambda$runLinkRequest$48$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response, int intentAccount) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (error != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", NUM));
                } else if (error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                }
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                TLRPC.Updates updates = (TLRPC.Updates) response;
                if (!updates.chats.isEmpty()) {
                    TLRPC.Chat chat = updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(intentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(intentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putLong("chat_id", chat.id);
                    if (!mainFragmentsStack.isEmpty()) {
                        MessagesController instance = MessagesController.getInstance(intentAccount);
                        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                        if (!instance.checkCanOpenChat(args, arrayList.get(arrayList.size() - 1))) {
                            return;
                        }
                    }
                    ChatActivity fragment = new ChatActivity(args);
                    NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    this.actionBarLayout.presentFragment(fragment, false, true, true, false);
                }
            }
        }
    }

    /* renamed from: lambda$runLinkRequest$50$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3681lambda$runLinkRequest$50$orgtelegramuiLaunchActivity(boolean hasUrl, int intentAccount, String message, DialogsActivity fragment13, ArrayList dids, CharSequence m, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args13 = new Bundle();
        args13.putBoolean("scrollToTopOnResume", true);
        args13.putBoolean("hasUrl", hasUrl);
        if (DialogObject.isEncryptedDialog(did)) {
            args13.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args13.putLong("user_id", did);
        } else {
            args13.putLong("chat_id", -did);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args13, fragment13)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            MediaDataController.getInstance(intentAccount).saveDraft(did, 0, message, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(args13), true, false, true, false);
            return;
        }
    }

    /* renamed from: lambda$runLinkRequest$54$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3685lambda$runLinkRequest$54$orgtelegramuiLaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_account_authorizationForm authorizationForm = (TLRPC.TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda83(this, progressDialog, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
            TLRPC.TL_error tL_error = error;
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda56(this, progressDialog, error));
    }

    /* renamed from: lambda$runLinkRequest$52$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3683lambda$runLinkRequest$52$orgtelegramuiLaunchActivity(AlertDialog progressDialog, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda52(this, progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
    }

    /* renamed from: lambda$runLinkRequest$51$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3682lambda$runLinkRequest$51$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl) {
        TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response1 != null) {
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm.bot_id, tL_account_getAuthorizationForm.scope, tL_account_getAuthorizationForm.public_key, payload, nonce, callbackUrl, authorizationForm, (TLRPC.TL_account_password) response1));
            return;
        }
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = authorizationForm;
    }

    /* renamed from: lambda$runLinkRequest$53$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3684lambda$runLinkRequest$53$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$runLinkRequest$56$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3687lambda$runLinkRequest$56$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda51(this, progressDialog, response));
    }

    /* renamed from: lambda$runLinkRequest$55$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3686lambda$runLinkRequest$55$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_help_deepLinkInfo) {
            TLRPC.TL_help_deepLinkInfo res = (TLRPC.TL_help_deepLinkInfo) response;
            AlertsCreator.showUpdateAppAlert(this, res.message, res.update_app);
        }
    }

    /* renamed from: lambda$runLinkRequest$58$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3689lambda$runLinkRequest$58$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda53(this, progressDialog, response, error));
    }

    /* renamed from: lambda$runLinkRequest$57$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3688lambda$runLinkRequest$57$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TLRPC.TL_langPackLanguage) response));
        } else if (error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", NUM)));
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text));
        }
    }

    /* renamed from: lambda$runLinkRequest$61$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3692lambda$runLinkRequest$61$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_wallPaper wallPaper, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda54(this, progressDialog, response, wallPaper, error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$runLinkRequest$60$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3691lambda$runLinkRequest$60$orgtelegramuiLaunchActivity(org.telegram.ui.ActionBar.AlertDialog r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC.TL_wallPaper r19, org.telegram.tgnet.TLRPC.TL_error r20) {
        /*
            r16 = this;
            r1 = r16
            r2 = r18
            r3 = r19
            r17.dismiss()     // Catch:{ Exception -> 0x000a }
            goto L_0x0010
        L_0x000a:
            r0 = move-exception
            r4 = r0
            r0 = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0010:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r0 == 0) goto L_0x0068
            r0 = r2
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r0
            boolean r4 = r0.pattern
            r5 = 0
            if (r4 == 0) goto L_0x004e
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r4 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r7 = r0.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r8 = r6.background_color
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r9 = r6.second_background_color
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r10 = r6.third_background_color
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r11 = r6.fourth_background_color
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r6 = r6.rotation
            int r12 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r6, r5)
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            int r6 = r6.intensity
            float r6 = (float) r6
            r13 = 1120403456(0x42CLASSNAME, float:100.0)
            float r13 = r6 / r13
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            boolean r14 = r6.motion
            r15 = 0
            r6 = r4
            r6.<init>(r7, r8, r9, r10, r11, r12, r13, r14, r15)
            r4.pattern = r0
            goto L_0x004f
        L_0x004e:
            r4 = r0
        L_0x004f:
            org.telegram.ui.ThemePreviewActivity r6 = new org.telegram.ui.ThemePreviewActivity
            r7 = 0
            r8 = 1
            r6.<init>(r4, r7, r8, r5)
            r5 = r6
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r3.settings
            boolean r6 = r6.blur
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r3.settings
            boolean r7 = r7.motion
            r5.setInitialModes(r6, r7)
            r1.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(r5)
            r4 = r20
            goto L_0x0090
        L_0x0068:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r4 = 2131625657(0x7f0e06b9, float:1.8878528E38)
            java.lang.String r5 = "ErrorOccurred"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.append(r4)
            java.lang.String r4 = "\n"
            r0.append(r4)
            r4 = r20
            java.lang.String r5 = r4.text
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r1, r0)
            r1.showAlertDialog(r0)
        L_0x0090:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3691lambda$runLinkRequest$60$orgtelegramuiLaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$runLinkRequest$62$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3693lambda$runLinkRequest$62$orgtelegramuiLaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* renamed from: lambda$runLinkRequest$64$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3695lambda$runLinkRequest$64$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda44(this, response, progressDialog, error));
    }

    /* renamed from: lambda$runLinkRequest$63$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3694lambda$runLinkRequest$63$orgtelegramuiLaunchActivity(TLObject response, AlertDialog progressDialog, TLRPC.TL_error error) {
        TLRPC.TL_wallPaper object;
        int notFound = 2;
        if (response instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme t = (TLRPC.TL_theme) response;
            TLRPC.ThemeSettings settings = null;
            if (t.settings.size() > 0) {
                settings = t.settings.get(0);
            }
            if (settings != null) {
                Theme.ThemeInfo info = Theme.getTheme(Theme.getBaseThemeKey(settings));
                if (info != null) {
                    if (settings.wallpaper instanceof TLRPC.TL_wallPaper) {
                        object = (TLRPC.TL_wallPaper) settings.wallpaper;
                        if (!FileLoader.getInstance(this.currentAccount).getPathToAttach(object.document, true).exists()) {
                            this.loadingThemeProgressDialog = progressDialog;
                            this.loadingThemeAccent = true;
                            this.loadingThemeInfo = info;
                            this.loadingTheme = t;
                            this.loadingThemeWallpaper = object;
                            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(object.document);
                            FileLoader.getInstance(this.currentAccount).loadFile(object.document, object, 1, 1);
                            return;
                        }
                    } else {
                        object = null;
                    }
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    notFound = 0;
                    openThemeAccentPreview(t, object, info);
                } else {
                    notFound = 1;
                }
            } else if (t.document != null) {
                this.loadingThemeAccent = false;
                this.loadingTheme = t;
                this.loadingThemeFileName = FileLoader.getAttachFileName(t.document);
                this.loadingThemeProgressDialog = progressDialog;
                FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, t, 1, 1);
                notFound = 0;
            } else {
                notFound = 1;
            }
        } else if (error != null && "THEME_FORMAT_INVALID".equals(error.text)) {
            notFound = 1;
        }
        if (notFound != 0) {
            try {
                progressDialog.dismiss();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (notFound == 1) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNotSupported", NUM)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNotFound", NUM)));
            }
        }
    }

    /* renamed from: lambda$runLinkRequest$66$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3697lambda$runLinkRequest$66$orgtelegramuiLaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, Integer messageId, Integer commentId, Integer threadId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda46(this, response, requestId, intentAccount, progressDialog, messageId, commentId, threadId));
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x003d A[SYNTHETIC, Splitter:B:7:0x003d] */
    /* renamed from: lambda$runLinkRequest$65$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3696lambda$runLinkRequest$65$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r13, int[] r14, int r15, org.telegram.ui.ActionBar.AlertDialog r16, java.lang.Integer r17, java.lang.Integer r18, java.lang.Integer r19) {
        /*
            r12 = this;
            r8 = r12
            r9 = r13
            r0 = 1
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_chats
            if (r1 == 0) goto L_0x003a
            r10 = r9
            org.telegram.tgnet.TLRPC$TL_messages_chats r10 = (org.telegram.tgnet.TLRPC.TL_messages_chats) r10
            java.util.ArrayList r1 = r10.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x003a
            r0 = 0
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList r2 = r10.chats
            r11 = 0
            r1.putChats(r2, r11)
            java.util.ArrayList r1 = r10.chats
            java.lang.Object r1 = r1.get(r11)
            r7 = r1
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC.Chat) r7
            r1 = r12
            r2 = r15
            r3 = r16
            r4 = r17
            r5 = r18
            r6 = r19
            int r1 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r14[r11] = r1
            r1 = r0
            goto L_0x003b
        L_0x003a:
            r1 = r0
        L_0x003b:
            if (r1 == 0) goto L_0x0057
            r16.dismiss()     // Catch:{ Exception -> 0x0041 }
            goto L_0x0047
        L_0x0041:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0047:
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            java.lang.String r2 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r12, r0)
            r12.showAlertDialog(r0)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3696lambda$runLinkRequest$65$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* renamed from: lambda$runLinkRequest$69$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3700lambda$runLinkRequest$69$orgtelegramuiLaunchActivity(Bundle args, Long channelId, int[] requestId, AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount) {
        Bundle bundle = args;
        if (!this.actionBarLayout.presentFragment(new ChatActivity(args))) {
            TLRPC.TL_channels_getChannels req = new TLRPC.TL_channels_getChannels();
            TLRPC.TL_inputChannel inputChannel = new TLRPC.TL_inputChannel();
            inputChannel.channel_id = channelId.longValue();
            req.id.add(inputChannel);
            requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda86(this, progressDialog, lastFragment, intentAccount, args));
        }
    }

    /* renamed from: lambda$runLinkRequest$68$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3699lambda$runLinkRequest$68$orgtelegramuiLaunchActivity(AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount, Bundle args, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda55(this, progressDialog, response, lastFragment, intentAccount, args));
    }

    /* renamed from: lambda$runLinkRequest$67$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3698lambda$runLinkRequest$67$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, BaseFragment lastFragment, int intentAccount, Bundle args) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        boolean notFound = true;
        if (response instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            if (!res.chats.isEmpty()) {
                notFound = false;
                MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(0);
                if (lastFragment == null || MessagesController.getInstance(intentAccount).checkCanOpenChat(args, lastFragment)) {
                    this.actionBarLayout.presentFragment(new ChatActivity(args));
                }
            }
        }
        if (notFound) {
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LinkNotFound", NUM)));
        }
    }

    static /* synthetic */ void lambda$runLinkRequest$70(int intentAccount, int[] requestId, Runnable cancelRunnableFinal, DialogInterface dialog) {
        ConnectionsManager.getInstance(intentAccount).cancelRequest(requestId[0], true);
        if (cancelRunnableFinal != null) {
            cancelRunnableFinal.run();
        }
    }

    private List<TLRPC.TL_contact> findContacts(String userName, String userPhone, boolean allowSelf) {
        String userName2;
        String userPhone2;
        String userPhone3;
        String[] queries;
        ContactsController contactsController;
        List<TLRPC.TL_contact> contacts;
        MessagesController messagesController;
        boolean found;
        MessagesController messagesController2 = MessagesController.getInstance(this.currentAccount);
        ContactsController contactsController2 = ContactsController.getInstance(this.currentAccount);
        List<TLRPC.TL_contact> contacts2 = new ArrayList<>(contactsController2.contacts);
        List<TLRPC.TL_contact> foundContacts = new ArrayList<>();
        if (userPhone != null) {
            userPhone2 = PhoneFormat.stripExceptNumbers(userPhone);
            TLRPC.TL_contact contact = contactsController2.contactsByPhone.get(userPhone2);
            if (contact == null) {
                contact = contactsController2.contactsByShortPhone.get(userPhone2.substring(Math.max(0, userPhone2.length() - 7)));
            }
            if (contact != null) {
                TLRPC.User user = messagesController2.getUser(Long.valueOf(contact.user_id));
                if (user == null || (user.self && !allowSelf)) {
                    userName2 = null;
                } else {
                    foundContacts.add(contact);
                }
            }
            userName2 = userName;
        } else {
            userName2 = userName;
            userPhone2 = userPhone;
        }
        if (!foundContacts.isEmpty() || userName2 == null) {
            ContactsController contactsController3 = contactsController2;
            List<TLRPC.TL_contact> list = contacts2;
            String str = userPhone2;
        } else {
            String query1 = userName2.trim().toLowerCase();
            if (!TextUtils.isEmpty(query1)) {
                String query2 = LocaleController.getInstance().getTranslitString(query1);
                if (query1.equals(query2) || query2.length() == 0) {
                    query2 = null;
                }
                String[] queries2 = {query1, query2};
                int i = 0;
                int size = contacts2.size();
                while (i < size) {
                    TLRPC.TL_contact contact2 = contacts2.get(i);
                    if (contact2 != null) {
                        String[] queries3 = queries2;
                        TLRPC.User user2 = messagesController2.getUser(Long.valueOf(contact2.user_id));
                        if (user2 == null) {
                            queries = queries3;
                            messagesController = messagesController2;
                            contactsController = contactsController2;
                            contacts = contacts2;
                            userPhone3 = userPhone2;
                        } else if (!user2.self || allowSelf) {
                            String[] names = new String[3];
                            names[0] = ContactsController.formatName(user2.first_name, user2.last_name).toLowerCase();
                            names[1] = LocaleController.getInstance().getTranslitString(names[0]);
                            if (names[0].equals(names[1])) {
                                names[1] = null;
                            }
                            if (UserObject.isReplyUser(user2)) {
                                names[2] = LocaleController.getString("RepliesTitle", NUM).toLowerCase();
                            } else if (user2.self) {
                                names[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                            }
                            String[] queries4 = queries3;
                            int length = queries4.length;
                            boolean found2 = false;
                            int i2 = 0;
                            while (true) {
                                if (i2 >= length) {
                                    messagesController = messagesController2;
                                    contactsController = contactsController2;
                                    contacts = contacts2;
                                    queries = queries4;
                                    userPhone3 = userPhone2;
                                    break;
                                }
                                messagesController = messagesController2;
                                String q = queries4[i2];
                                if (q == null) {
                                    contactsController = contactsController2;
                                    contacts = contacts2;
                                    queries = queries4;
                                    userPhone3 = userPhone2;
                                    found = found2;
                                } else {
                                    contactsController = contactsController2;
                                    int j = 0;
                                    while (true) {
                                        contacts = contacts2;
                                        if (j >= names.length) {
                                            queries = queries4;
                                            userPhone3 = userPhone2;
                                            found = found2;
                                            break;
                                        }
                                        String name = names[j];
                                        if (name != null) {
                                            if (name.startsWith(q)) {
                                                queries = queries4;
                                                userPhone3 = userPhone2;
                                                break;
                                            }
                                            queries = queries4;
                                            StringBuilder sb = new StringBuilder();
                                            userPhone3 = userPhone2;
                                            sb.append(" ");
                                            sb.append(q);
                                            if (name.contains(sb.toString())) {
                                                break;
                                            }
                                        } else {
                                            queries = queries4;
                                            userPhone3 = userPhone2;
                                        }
                                        j++;
                                        contacts2 = contacts;
                                        queries4 = queries;
                                        userPhone2 = userPhone3;
                                    }
                                    found = true;
                                    if (!found && user2.username != null && user2.username.startsWith(q)) {
                                        found = true;
                                    }
                                    if (found) {
                                        foundContacts.add(contact2);
                                        break;
                                    }
                                }
                                i2++;
                                found2 = found;
                                messagesController2 = messagesController;
                                contacts2 = contacts;
                                contactsController2 = contactsController;
                                queries4 = queries;
                                userPhone2 = userPhone3;
                            }
                        } else {
                            queries = queries3;
                            messagesController = messagesController2;
                            contactsController = contactsController2;
                            contacts = contacts2;
                            userPhone3 = userPhone2;
                        }
                    } else {
                        messagesController = messagesController2;
                        contactsController = contactsController2;
                        contacts = contacts2;
                        userPhone3 = userPhone2;
                        queries = queries2;
                    }
                    i++;
                    messagesController2 = messagesController;
                    contacts2 = contacts;
                    contactsController2 = contactsController;
                    queries2 = queries;
                    userPhone2 = userPhone3;
                }
                ContactsController contactsController4 = contactsController2;
                List<TLRPC.TL_contact> list2 = contacts2;
                String str2 = userPhone2;
                String[] strArr = queries2;
            } else {
                ContactsController contactsController5 = contactsController2;
                List<TLRPC.TL_contact> list3 = contacts2;
                String str3 = userPhone2;
            }
        }
        return foundContacts;
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
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    if (this.lastGradientWidth != width) {
                        this.updateGradient = new LinearGradient(0.0f, 0.0f, (float) width, 0.0f, new int[]{-9846926, -11291731}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                        this.lastGradientWidth = width;
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
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda10(this));
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

    /* renamed from: lambda$createUpdateUI$71$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3631lambda$createUpdateUI$71$orgtelegramuiLaunchActivity(View v) {
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

    private void updateAppUpdateViews(boolean animated) {
        boolean showSize;
        if (this.sideMenuContainer != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                View prevUpdateLayout = this.updateLayout;
                createUpdateUI();
                this.updateSizeTextView.setText(AndroidUtilities.formatFileSize(SharedConfig.pendingAppUpdate.document.size));
                String fileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                if (FileLoader.getInstance(this.currentAccount).getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists()) {
                    this.updateLayoutIcon.setIcon(15, true, false);
                    this.updateTextView.setText(LocaleController.getString("AppUpdateNow", NUM));
                    showSize = false;
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.updateLayoutIcon.setIcon(3, true, false);
                    this.updateLayoutIcon.setProgress(0.0f, false);
                    Float p = ImageLoader.getInstance().getFileProgress(fileName);
                    SimpleTextView simpleTextView = this.updateTextView;
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf((int) ((p != null ? p.floatValue() : 0.0f) * 100.0f));
                    simpleTextView.setText(LocaleController.formatString("AppUpdateDownloading", NUM, objArr));
                    showSize = false;
                } else {
                    this.updateLayoutIcon.setIcon(2, true, false);
                    this.updateTextView.setText(LocaleController.getString("AppUpdate", NUM));
                    showSize = true;
                }
                if (showSize) {
                    if (this.updateSizeTextView.getTag() != null) {
                        if (animated) {
                            this.updateSizeTextView.setTag((Object) null);
                            this.updateSizeTextView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180).start();
                        } else {
                            this.updateSizeTextView.setAlpha(1.0f);
                            this.updateSizeTextView.setScaleX(1.0f);
                            this.updateSizeTextView.setScaleY(1.0f);
                        }
                    }
                } else if (this.updateSizeTextView.getTag() == null) {
                    if (animated) {
                        this.updateSizeTextView.setTag(1);
                        this.updateSizeTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180).start();
                    } else {
                        this.updateSizeTextView.setAlpha(0.0f);
                        this.updateSizeTextView.setScaleX(0.0f);
                        this.updateSizeTextView.setScaleY(0.0f);
                    }
                }
                if (this.updateLayout.getTag() == null) {
                    this.updateLayout.setVisibility(0);
                    this.updateLayout.setTag(1);
                    if (animated) {
                        this.updateLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener((Animator.AnimatorListener) null).setDuration(180).withEndAction(new LaunchActivity$$ExternalSyntheticLambda18(prevUpdateLayout)).start();
                    } else {
                        this.updateLayout.setTranslationY(0.0f);
                        if (prevUpdateLayout != null) {
                            ((ViewGroup) prevUpdateLayout.getParent()).removeView(prevUpdateLayout);
                        }
                    }
                    this.sideMenu.setPadding(0, 0, 0, AndroidUtilities.dp(44.0f));
                    return;
                }
                return;
            }
            FrameLayout frameLayout2 = this.updateLayout;
            if (frameLayout2 != null && frameLayout2.getTag() != null) {
                this.updateLayout.setTag((Object) null);
                if (animated) {
                    this.updateLayout.animate().translationY((float) AndroidUtilities.dp(44.0f)).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (LaunchActivity.this.updateLayout.getTag() == null) {
                                LaunchActivity.this.updateLayout.setVisibility(4);
                            }
                        }
                    }).setDuration(180).start();
                } else {
                    this.updateLayout.setTranslationY((float) AndroidUtilities.dp(44.0f));
                    this.updateLayout.setVisibility(4);
                }
                this.sideMenu.setPadding(0, 0, 0, 0);
            }
        }
    }

    static /* synthetic */ void lambda$updateAppUpdateViews$72(View prevUpdateLayout) {
        if (prevUpdateLayout != null) {
            ((ViewGroup) prevUpdateLayout.getParent()).removeView(prevUpdateLayout);
        }
    }

    public void checkAppUpdate(boolean force) {
        if (!force && BuildVars.DEBUG_VERSION) {
            return;
        }
        if (!force && !BuildVars.CHECK_UPDATES) {
            return;
        }
        if (force || Math.abs(System.currentTimeMillis() - SharedConfig.lastUpdateCheckTime) >= ((long) (MessagesController.getInstance(0).updateCheckDelay * 1000))) {
            TLRPC.TL_help_getAppUpdate req = new TLRPC.TL_help_getAppUpdate();
            try {
                req.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception e) {
            }
            if (req.source == null) {
                req.source = "";
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda69(this, this.currentAccount));
        }
    }

    /* renamed from: lambda$checkAppUpdate$74$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3628lambda$checkAppUpdate$74$orgtelegramuiLaunchActivity(int accountNum, TLObject response, TLRPC.TL_error error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (response instanceof TLRPC.TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda50(this, (TLRPC.TL_help_appUpdate) response, accountNum));
        }
    }

    /* renamed from: lambda$checkAppUpdate$73$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3627lambda$checkAppUpdate$73$orgtelegramuiLaunchActivity(TLRPC.TL_help_appUpdate res, int accountNum) {
        if ((SharedConfig.pendingAppUpdate == null || !SharedConfig.pendingAppUpdate.version.equals(res.version)) && SharedConfig.setNewAppVersionAvailable(res)) {
            if (res.can_not_skip) {
                showUpdateActivity(accountNum, res, false);
            } else {
                this.drawerLayoutAdapter.notifyDataSetChanged();
                try {
                    new UpdateAppAlertDialog(this, res, accountNum).show();
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
            this.visibleDialog.setOnDismissListener(new LaunchActivity$$ExternalSyntheticLambda9(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* renamed from: lambda$showAlertDialog$75$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3701lambda$showAlertDialog$75$orgtelegramuiLaunchActivity(DialogInterface dialog) {
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
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("proxy_enabled", false);
                editor.putBoolean("proxy_enabled_calls", false);
                editor.commit();
                ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                this.proxyErrorDialog = null;
            }
        }
        this.visibleDialog = null;
    }

    public void showBulletin(Function<BulletinFactory, Bulletin> createBulletin) {
        BaseFragment topFragment = null;
        if (!layerFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = layerFragmentsStack;
            topFragment = arrayList.get(arrayList.size() - 1);
        } else if (!rightFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
            topFragment = arrayList2.get(arrayList2.size() - 1);
        } else if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
            topFragment = arrayList3.get(arrayList3.size() - 1);
        }
        if (BulletinFactory.canShowBulletin(topFragment)) {
            createBulletin.apply(BulletinFactory.of(topFragment)).show();
        }
    }

    public void setNavigateToPremiumBot(boolean val) {
        this.navigateToPremiumBot = val;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        ChatActivity fragment;
        int attachesCount;
        int i;
        int i2;
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList;
        DialogsActivity dialogsActivity = dialogsFragment;
        ArrayList<Long> arrayList2 = dids;
        int account = dialogsActivity != null ? dialogsFragment.getCurrentAccount() : this.currentAccount;
        if (this.exportingChatUri != null) {
            Uri uri = this.exportingChatUri;
            ArrayList<Uri> documentsUris = this.documentsUrisArray != null ? new ArrayList<>(this.documentsUrisArray) : null;
            AlertDialog progressDialog = new AlertDialog(this, 3);
            SendMessagesHelper.getInstance(account).prepareImportHistory(arrayList2.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new LaunchActivity$$ExternalSyntheticLambda67(this, account, dialogsFragment, param, documentsUris, uri, progressDialog));
            try {
                progressDialog.showDelayed(300);
            } catch (Exception e) {
            }
        } else {
            boolean notify = dialogsActivity == null || dialogsActivity.notify;
            if (dids.size() <= 1) {
                long did = arrayList2.get(0).longValue();
                Bundle args = new Bundle();
                args.putBoolean("scrollToTopOnResume", true);
                if (!AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                if (DialogObject.isEncryptedDialog(did)) {
                    args.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                } else if (DialogObject.isUserDialog(did)) {
                    args.putLong("user_id", did);
                } else {
                    args.putLong("chat_id", -did);
                }
                if (MessagesController.getInstance(account).checkCanOpenChat(args, dialogsActivity)) {
                    fragment = new ChatActivity(args);
                } else {
                    return;
                }
            } else {
                fragment = null;
            }
            int attachesCount2 = 0;
            ArrayList<TLRPC.User> arrayList3 = this.contactsToSend;
            if (arrayList3 != null) {
                attachesCount2 = 0 + arrayList3.size();
            }
            if (this.videoPath != null) {
                attachesCount2++;
            }
            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList4 = this.photoPathsArray;
            if (arrayList4 != null) {
                attachesCount2 += arrayList4.size();
            }
            ArrayList<String> arrayList5 = this.documentsPathsArray;
            if (arrayList5 != null) {
                attachesCount2 += arrayList5.size();
            }
            ArrayList<Uri> arrayList6 = this.documentsUrisArray;
            if (arrayList6 != null) {
                attachesCount2 += arrayList6.size();
            }
            if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
                attachesCount = attachesCount2 + 1;
            } else {
                attachesCount = attachesCount2;
            }
            int i3 = 0;
            while (i3 < dids.size()) {
                if (!AlertsCreator.checkSlowMode(this, this.currentAccount, arrayList2.get(i3).longValue(), attachesCount > 1)) {
                    i3++;
                } else {
                    return;
                }
            }
            ArrayList<TLRPC.User> arrayList7 = this.contactsToSend;
            if (arrayList7 == null || arrayList7.size() != 1 || mainFragmentsStack.isEmpty()) {
                String captionToSend = null;
                int i4 = 0;
                while (i4 < dids.size()) {
                    long did2 = arrayList2.get(i4).longValue();
                    AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                    boolean photosEditorOpened = false;
                    boolean videoEditorOpened = false;
                    if (fragment != null) {
                        i2 = 1024;
                        i = i4;
                        this.actionBarLayout.presentFragment(fragment, dialogsActivity != null, dialogsActivity == null || this.videoPath != null || ((arrayList = this.photoPathsArray) != null && arrayList.size() > 0), true, false);
                        String str = this.videoPath;
                        if (str != null) {
                            fragment.openVideoEditor(str, this.sendingText);
                            this.sendingText = null;
                            videoEditorOpened = true;
                        } else {
                            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList8 = this.photoPathsArray;
                            if (arrayList8 != null && arrayList8.size() > 0) {
                                boolean photosEditorOpened2 = fragment.openPhotosEditor(this.photoPathsArray, (message == null || message.length() == 0) ? this.sendingText : message);
                                if (photosEditorOpened2) {
                                    this.sendingText = null;
                                }
                                photosEditorOpened = photosEditorOpened2;
                            }
                        }
                    } else {
                        i = i4;
                        i2 = 1024;
                        if (this.videoPath != null) {
                            String str2 = this.sendingText;
                            if (str2 != null && str2.length() <= 1024) {
                                captionToSend = this.sendingText;
                                this.sendingText = null;
                            }
                            ArrayList<String> arrayList9 = new ArrayList<>();
                            arrayList9.add(this.videoPath);
                            SendMessagesHelper.prepareSendingDocuments(accountInstance, arrayList9, arrayList9, (ArrayList<Uri>) null, captionToSend, (String) null, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, notify, 0);
                        }
                    }
                    if (this.photoPathsArray != null && !photosEditorOpened) {
                        String str3 = this.sendingText;
                        if (str3 != null && str3.length() <= i2 && this.photoPathsArray.size() == 1) {
                            this.photoPathsArray.get(0).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingMedia(accountInstance, this.photoPathsArray, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, notify, 0);
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        String str4 = this.sendingText;
                        if (str4 != null && str4.length() <= i2) {
                            ArrayList<String> arrayList10 = this.documentsPathsArray;
                            int size = arrayList10 != null ? arrayList10.size() : 0;
                            ArrayList<Uri> arrayList11 = this.documentsUrisArray;
                            if (size + (arrayList11 != null ? arrayList11.size() : 0) == 1) {
                                captionToSend = this.sendingText;
                                this.sendingText = null;
                            }
                        }
                        SendMessagesHelper.prepareSendingDocuments(accountInstance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, captionToSend, this.documentsMimeType, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, notify, 0);
                    }
                    String str5 = this.sendingText;
                    if (str5 != null) {
                        SendMessagesHelper.prepareSendingText(accountInstance, str5, did2, true, 0);
                    }
                    ArrayList<TLRPC.User> arrayList12 = this.contactsToSend;
                    if (arrayList12 != null && !arrayList12.isEmpty()) {
                        for (int a = 0; a < this.contactsToSend.size(); a++) {
                            SendMessagesHelper.getInstance(account).sendMessage(this.contactsToSend.get(a), did2, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, 0);
                        }
                    }
                    if (TextUtils.isEmpty(message) == 0 && !videoEditorOpened && !photosEditorOpened) {
                        SendMessagesHelper.prepareSendingText(accountInstance, message.toString(), did2, notify, 0);
                    }
                    i4 = i + 1;
                }
                int i5 = i4;
            } else {
                ArrayList<BaseFragment> arrayList13 = mainFragmentsStack;
                PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList13.get(arrayList13.size() - 1), (ContactsController.Contact) null, (TLRPC.User) null, this.contactsToSendUri, (File) null, (String) null, (String) null);
                LaunchActivity$$ExternalSyntheticLambda95 launchActivity$$ExternalSyntheticLambda95 = r4;
                LaunchActivity$$ExternalSyntheticLambda95 launchActivity$$ExternalSyntheticLambda952 = new LaunchActivity$$ExternalSyntheticLambda95(this, fragment, dids, account, message, notify);
                phonebookShareAlert.setDelegate(launchActivity$$ExternalSyntheticLambda95);
                ArrayList<BaseFragment> arrayList14 = mainFragmentsStack;
                arrayList14.get(arrayList14.size() - 1).showDialog(phonebookShareAlert);
            }
            if (dialogsActivity != null && fragment == null) {
                dialogsFragment.finishFragment();
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

    /* renamed from: lambda$didSelectDialogs$76$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3640lambda$didSelectDialogs$76$orgtelegramuiLaunchActivity(int account, DialogsActivity dialogsFragment, boolean param, ArrayList documentsUris, Uri uri, AlertDialog progressDialog, long result) {
        ArrayList arrayList = documentsUris;
        long j = result;
        if (j != 0) {
            Bundle args = new Bundle();
            args.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (DialogObject.isUserDialog(result)) {
                args.putLong("user_id", j);
            } else {
                args.putLong("chat_id", -j);
            }
            ChatActivity fragment = new ChatActivity(args);
            fragment.setOpenImport();
            this.actionBarLayout.presentFragment(fragment, dialogsFragment != null || param, dialogsFragment == null, true, false);
            Uri uri2 = uri;
        } else {
            this.documentsUrisArray = arrayList;
            if (arrayList == null) {
                this.documentsUrisArray = new ArrayList<>();
            }
            this.documentsUrisArray.add(0, uri);
            openDialogsToSend(true);
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$didSelectDialogs$77$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3641lambda$didSelectDialogs$77$orgtelegramuiLaunchActivity(ChatActivity fragment, ArrayList dids, int account, CharSequence message, boolean notify, TLRPC.User user, boolean notify2, int scheduleDate) {
        if (fragment != null) {
            this.actionBarLayout.presentFragment(fragment, true, false, true, false);
        }
        AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        for (int i = 0; i < dids.size(); i++) {
            long did = ((Long) dids.get(i)).longValue();
            SendMessagesHelper.getInstance(account).sendMessage(user, did, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify2, scheduleDate);
            if (!TextUtils.isEmpty(message)) {
                SendMessagesHelper.prepareSendingText(accountInstance, message.toString(), did, notify, 0);
            }
        }
        ArrayList arrayList = dids;
    }

    private void onFinish() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (!this.finished) {
            this.finished = true;
            if (this.currentAccount != -1) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
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
    public void m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VoIPService service;
        boolean z = false;
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onActivityResult");
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (requestCode != 105) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 520) {
                if (resultCode == -1 && (service = VoIPService.getSharedInstance()) != null) {
                    VideoCapturerDevice.mediaProjectionPermissionResultData = data;
                    service.createCaptureDevice(true);
                }
            } else if (requestCode == 140) {
                LocationController instance = LocationController.getInstance(this.currentAccount);
                if (resultCode == -1) {
                    z = true;
                }
                instance.startFusedLocationRequest(z);
            } else {
                ThemeEditorView editorView = ThemeEditorView.getInstance();
                if (editorView != null) {
                    editorView.onActivityResult(requestCode, resultCode, data);
                }
                if (this.actionBarLayout.fragmentsStack.size() != 0) {
                    this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                        this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
                    }
                    if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                        this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
                    }
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.onActivityResultReceived, Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
            if (canDrawOverlays) {
                if (GroupCallActivity.groupCallInstance != null) {
                    GroupCallActivity.groupCallInstance.dismissInternal();
                }
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda27(this), 200);
            }
        }
    }

    /* renamed from: lambda$onActivityResult$78$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3650lambda$onActivityResult$78$orgtelegramuiLaunchActivity() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissionsResult(requestCode, permissions, grantResults)) {
            if (this.actionBarLayout.fragmentsStack.size() != 0) {
                this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                    this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
                }
                if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                    this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
                }
            }
            VoIPFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.onRequestPermissionResultReceived, Integer.valueOf(requestCode), permissions, grantResults);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        isResumed = false;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda17(this.currentAccount));
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

    static /* synthetic */ void lambda$onPause$79(int account) {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
        if (VoIPService.getSharedInstance() == null) {
            MessagesController.getInstance(account).ignoreSetOnline = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = false;
        GroupCallPip.updateVisibility(this);
        if (GroupCallActivity.groupCallInstance != null) {
            GroupCallActivity.groupCallInstance.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = true;
        GroupCallPip.updateVisibility(this);
        if (GroupCallActivity.groupCallInstance != null) {
            GroupCallActivity.groupCallInstance.onPause();
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
        if (GroupCallActivity.groupCallInstance != null) {
            GroupCallActivity.groupCallInstance.dismissInternal();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, false);
        if (pipRoundVideoView != null) {
            pipRoundVideoView.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.destroy();
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
        for (Runnable callback : this.onUserLeaveHintListeners) {
            callback.run();
        }
        this.actionBarLayout.onUserLeaveHint();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MessageObject messageObject;
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
        if (!(PipRoundVideoView.getInstance() == null || !MediaController.getInstance().isMessagePaused() || (messageObject = MediaController.getInstance().getPlayingMessageObject()) == null)) {
            MediaController.getInstance().seekToProgress(messageObject, messageObject.audioProgress);
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            showTosActivity(UserConfig.selectedAccount, UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService);
        } else if (SharedConfig.pendingAppUpdate != null && SharedConfig.pendingAppUpdate.can_not_skip) {
            showUpdateActivity(UserConfig.selectedAccount, SharedConfig.pendingAppUpdate, true);
        }
        checkAppUpdate(false);
        if (Build.VERSION.SDK_INT >= 23) {
            ApplicationLoader.canDrawOverlays = Settings.canDrawOverlays(this);
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onResume();
        }
    }

    static /* synthetic */ void lambda$onResume$80() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        checkLayout();
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.onConfigurationChanged();
        }
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.onConfigurationChanged(newConfig);
        }
        PhotoViewer photoViewer = PhotoViewer.getPipInstance();
        if (photoViewer != null) {
            photoViewer.onConfigurationChanged(newConfig);
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        checkLayout();
    }

    /* JADX WARNING: type inference failed for: r26v0, types: [java.lang.Object[]] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r24, int r25, java.lang.Object... r26) {
        /*
            r23 = this;
            r8 = r23
            r9 = r24
            r10 = r25
            r11 = r26
            int r0 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r9 != r0) goto L_0x0011
            r23.switchToAvailableAccountOrLogout()
            goto L_0x0a08
        L_0x0011:
            int r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1 = 0
            if (r9 != r0) goto L_0x0022
            r0 = r11[r1]
            if (r0 == r8) goto L_0x0a08
            r23.onFinish()
            r23.finish()
            goto L_0x0a08
        L_0x0022:
            int r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r9 != r0) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            int r0 = r0.getConnectionState()
            int r1 = r8.currentConnectionState
            if (r1 == r0) goto L_0x004f
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x004a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "switch to state "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x004a:
            r8.currentConnectionState = r0
            r8.updateCurrentConnectionState(r10)
        L_0x004f:
            goto L_0x0a08
        L_0x0051:
            int r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r9 != r0) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r8.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x0a08
        L_0x005c:
            int r0 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r3 = "Cancel"
            r4 = 5
            r5 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r6 = "AppName"
            r7 = 4
            r12 = 6
            r13 = 3
            java.lang.String r15 = "OK"
            r2 = 2
            r14 = 1
            if (r9 != r0) goto L_0x01aa
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r1 = r0.intValue()
            if (r1 == r12) goto L_0x01a9
            int r1 = r0.intValue()
            if (r1 != r13) goto L_0x0085
            org.telegram.ui.ActionBar.AlertDialog r1 = r8.proxyErrorDialog
            if (r1 == 0) goto L_0x0085
            goto L_0x01a9
        L_0x0085:
            int r1 = r0.intValue()
            if (r1 != r7) goto L_0x0093
            r1 = r11[r14]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r1 = (org.telegram.tgnet.TLRPC.TL_help_termsOfService) r1
            r8.showTosActivity(r10, r1)
            return
        L_0x0093:
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r8)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setTitle(r5)
            int r5 = r0.intValue()
            if (r5 == r2) goto L_0x00bc
            int r5 = r0.intValue()
            if (r5 == r13) goto L_0x00bc
            r5 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            java.lang.String r6 = "MoreInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda48 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda48
            r6.<init>(r10)
            r1.setNegativeButton(r5, r6)
        L_0x00bc:
            int r5 = r0.intValue()
            if (r5 != r4) goto L_0x00db
            r2 = 2131626883(0x7f0e0b83, float:1.8881015E38)
            java.lang.String r3 = "NobodyLikesSpam3"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            goto L_0x018b
        L_0x00db:
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x00fa
            r2 = 2131626881(0x7f0e0b81, float:1.888101E38)
            java.lang.String r3 = "NobodyLikesSpam1"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            goto L_0x018b
        L_0x00fa:
            int r4 = r0.intValue()
            if (r4 != r14) goto L_0x0118
            r2 = 2131626882(0x7f0e0b82, float:1.8881013E38)
            java.lang.String r3 = "NobodyLikesSpam2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            goto L_0x018b
        L_0x0118:
            int r4 = r0.intValue()
            if (r4 != r2) goto L_0x015a
            r4 = r11[r14]
            java.lang.String r4 = (java.lang.String) r4
            r1.setMessage(r4)
            r2 = r11[r2]
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r4 = "AUTH_KEY_DROP_"
            boolean r4 = r2.startsWith(r4)
            if (r4 == 0) goto L_0x014e
            r4 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r4 = 0
            r1.setPositiveButton(r3, r4)
            r3 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            java.lang.String r4 = "LogOut"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda103 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda103
            r4.<init>(r8)
            r1.setNegativeButton(r3, r4)
            goto L_0x018a
        L_0x014e:
            r4 = 0
            r3 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r1.setPositiveButton(r3, r4)
            goto L_0x018a
        L_0x015a:
            int r2 = r0.intValue()
            if (r2 != r13) goto L_0x018a
            r2 = 2131627749(0x7f0e0ee5, float:1.8882771E38)
            java.lang.String r3 = "Proxy"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            r2 = 2131628794(0x7f0e12fa, float:1.888489E38)
            java.lang.String r3 = "UseProxyTelegramError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            org.telegram.ui.ActionBar.AlertDialog r2 = r8.showAlertDialog(r1)
            r8.proxyErrorDialog = r2
            return
        L_0x018a:
        L_0x018b:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x01a7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r14
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.create()
            r2.showDialog(r3)
        L_0x01a7:
            goto L_0x0a08
        L_0x01a9:
            return
        L_0x01aa:
            int r0 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r9 != r0) goto L_0x0204
            r0 = r11[r1]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r8)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setTitle(r2)
            r2 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            r2 = 2131628292(0x7f0e1104, float:1.8883873E38)
            java.lang.String r3 = "ShareYouLocationUnableManually"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7
            r3.<init>(r8, r0, r10)
            r1.setNegativeButton(r2, r3)
            r2 = 2131628291(0x7f0e1103, float:1.888387E38)
            java.lang.String r3 = "ShareYouLocationUnable"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0202
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r14
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.create()
            r2.showDialog(r3)
        L_0x0202:
            goto L_0x0a08
        L_0x0204:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r9 != r0) goto L_0x0226
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0215
            android.view.View r0 = r0.getChildAt(r1)
            if (r0 == 0) goto L_0x0215
            r0.invalidate()
        L_0x0215:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r8.backgroundTablet
            if (r0 == 0) goto L_0x0a08
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            goto L_0x0a08
        L_0x0226:
            int r0 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r9 != r0) goto L_0x025a
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r1 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x0246
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0246
            android.view.Window r0 = r23.getWindow()     // Catch:{ Exception -> 0x0240 }
            r0.setFlags(r1, r1)     // Catch:{ Exception -> 0x0240 }
            goto L_0x0244
        L_0x0240:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0244:
            goto L_0x0a08
        L_0x0246:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x0a08
            android.view.Window r0 = r23.getWindow()     // Catch:{ Exception -> 0x0254 }
            r0.clearFlags(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0258
        L_0x0254:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0258:
            goto L_0x0a08
        L_0x025a:
            int r0 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r9 != r0) goto L_0x0292
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r14) goto L_0x0276
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r14
            java.lang.Object r0 = r0.get(r2)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0276
            r1 = 1
        L_0x0276:
            r0 = r1
            if (r0 == 0) goto L_0x028d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r14
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ProfileActivity r1 = (org.telegram.ui.ProfileActivity) r1
            boolean r2 = r1.isSettings()
            if (r2 != 0) goto L_0x028d
            r0 = 0
        L_0x028d:
            r8.rebuildAllFragments(r0)
            goto L_0x0a08
        L_0x0292:
            int r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r9 != r0) goto L_0x029b
            r8.showLanguageAlert(r1)
            goto L_0x0a08
        L_0x029b:
            int r0 = org.telegram.messenger.NotificationCenter.openArticle
            if (r9 != r0) goto L_0x02cd
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02a8
            return
        L_0x02a8:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r14
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r0.setParentActivity(r8, r2)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r1 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_webPage r1 = (org.telegram.tgnet.TLRPC.TL_webPage) r1
            r2 = r11[r14]
            java.lang.String r2 = (java.lang.String) r2
            r0.open(r1, r2)
            goto L_0x0a08
        L_0x02cd:
            int r0 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r9 != r0) goto L_0x036c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            if (r0 == 0) goto L_0x036b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02df
            goto L_0x036b
        L_0x02df:
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r4 = r11[r14]
            java.util.HashMap r4 = (java.util.HashMap) r4
            r2 = r11[r2]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            r5 = r11[r13]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = r7.fragmentsStack
            int r7 = r7.size()
            int r7 = r7 - r14
            java.lang.Object r6 = r6.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r6 = (org.telegram.ui.ActionBar.BaseFragment) r6
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r7.<init>((android.content.Context) r8)
            r12 = 2131558494(0x7f0d005e, float:1.8742305E38)
            r13 = 72
            java.lang.String r14 = "dialogTopBackground"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r7.setTopAnimation(r12, r13, r1, r14)
            r12 = 2131628760(0x7f0e12d8, float:1.8884822E38)
            java.lang.String r13 = "UpdateContactsTitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r7.setTitle(r12)
            r12 = 2131628759(0x7f0e12d7, float:1.888482E38)
            java.lang.String r13 = "UpdateContactsMessage"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r7.setMessage(r12)
            r12 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda59 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda59
            r13.<init>(r10, r4, r2, r5)
            r7.setPositiveButton(r12, r13)
            r12 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70
            r12.<init>(r10, r4, r2, r5)
            r7.setNegativeButton(r3, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81
            r3.<init>(r10, r4, r2, r5)
            r7.setOnBackButtonListener(r3)
            org.telegram.ui.ActionBar.AlertDialog r3 = r7.create()
            r6.showDialog(r3)
            r3.setCanceledOnTouchOutside(r1)
            goto L_0x0a08
        L_0x036b:
            return
        L_0x036c:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r3 = 21
            if (r9 != r0) goto L_0x03fa
            r0 = r11[r1]
            r4 = r0
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r0 = r4.booleanValue()
            if (r0 != 0) goto L_0x03bf
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x03a7
            java.lang.String r5 = "chats_menuBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setBackgroundColor(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setGlowColor(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            java.lang.String r5 = "listSelectorSDK21"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setListSelectorColor(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x03a7:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x03bf
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x03be }
            java.lang.String r3 = "actionBarDefault"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)     // Catch:{ Exception -> 0x03be }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r5
            r5 = 0
            r0.<init>(r5, r5, r3)     // Catch:{ Exception -> 0x03be }
            r8.setTaskDescription(r0)     // Catch:{ Exception -> 0x03be }
            goto L_0x03bf
        L_0x03be:
            r0 = move-exception
        L_0x03bf:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            java.lang.String r3 = "windowBackgroundWhite"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBehindKeyboardColor(r3)
            r0 = 1
            int r3 = r11.length
            if (r3 <= r14) goto L_0x03d6
            r3 = r11[r14]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r0 = r3.booleanValue()
        L_0x03d6:
            int r3 = r11.length
            if (r3 <= r2) goto L_0x03e5
            r2 = r11[r2]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            if (r2 == 0) goto L_0x03e5
            r2 = 1
            goto L_0x03e6
        L_0x03e5:
            r2 = 0
        L_0x03e6:
            if (r0 == 0) goto L_0x03f5
            boolean r3 = r8.isNavigationBarColorFrozen
            if (r3 != 0) goto L_0x03f5
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            boolean r3 = r3.isTransitionAnimationInProgress()
            if (r3 != 0) goto L_0x03f5
            r1 = 1
        L_0x03f5:
            r8.checkSystemBarColors(r2, r14, r1)
            goto L_0x0a08
        L_0x03fa:
            int r0 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r9 != r0) goto L_0x0606
            r5 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x05d6
            r0 = r11[r2]
            if (r0 == 0) goto L_0x05d6
            android.widget.ImageView r0 = r8.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0410
            return
        L_0x0410:
            r0 = r11[r2]     // Catch:{ all -> 0x05bb }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x05bb }
            r3 = r11[r7]     // Catch:{ all -> 0x05bb }
            java.lang.Boolean r3 = (java.lang.Boolean) r3     // Catch:{ all -> 0x05bb }
            boolean r3 = r3.booleanValue()     // Catch:{ all -> 0x05bb }
            r4 = r11[r4]     // Catch:{ all -> 0x05bb }
            org.telegram.ui.Components.RLottieImageView r4 = (org.telegram.ui.Components.RLottieImageView) r4     // Catch:{ all -> 0x05bb }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x05bb }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x05bb }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r15 = r8.drawerLayoutContainer     // Catch:{ all -> 0x05bb }
            int r15 = r15.getMeasuredHeight()     // Catch:{ all -> 0x05bb }
            if (r3 != 0) goto L_0x0431
            r4.setVisibility(r7)     // Catch:{ all -> 0x05bb }
        L_0x0431:
            r7 = 0
            r8.rippleAbove = r7     // Catch:{ all -> 0x05bb }
            int r7 = r11.length     // Catch:{ all -> 0x05bb }
            if (r7 <= r12) goto L_0x043d
            r7 = r11[r12]     // Catch:{ all -> 0x05bb }
            android.view.View r7 = (android.view.View) r7     // Catch:{ all -> 0x05bb }
            r8.rippleAbove = r7     // Catch:{ all -> 0x05bb }
        L_0x043d:
            r8.isNavigationBarColorFrozen = r14     // Catch:{ all -> 0x05bb }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x05bb }
            r8.invalidateCachedViews(r7)     // Catch:{ all -> 0x05bb }
            android.view.View r7 = r8.rippleAbove     // Catch:{ all -> 0x05bb }
            if (r7 == 0) goto L_0x0457
            android.graphics.drawable.Drawable r7 = r7.getBackground()     // Catch:{ all -> 0x05bb }
            if (r7 == 0) goto L_0x0457
            android.view.View r7 = r8.rippleAbove     // Catch:{ all -> 0x05bb }
            android.graphics.drawable.Drawable r7 = r7.getBackground()     // Catch:{ all -> 0x05bb }
            r7.setAlpha(r1)     // Catch:{ all -> 0x05bb }
        L_0x0457:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x05bb }
            android.graphics.Bitmap r7 = org.telegram.messenger.AndroidUtilities.snapshotView(r7)     // Catch:{ all -> 0x05bb }
            android.view.View r12 = r8.rippleAbove     // Catch:{ all -> 0x05bb }
            if (r12 == 0) goto L_0x0472
            android.graphics.drawable.Drawable r12 = r12.getBackground()     // Catch:{ all -> 0x05bb }
            if (r12 == 0) goto L_0x0472
            android.view.View r12 = r8.rippleAbove     // Catch:{ all -> 0x05bb }
            android.graphics.drawable.Drawable r12 = r12.getBackground()     // Catch:{ all -> 0x05bb }
            r13 = 255(0xff, float:3.57E-43)
            r12.setAlpha(r13)     // Catch:{ all -> 0x05bb }
        L_0x0472:
            android.widget.FrameLayout r12 = r8.frameLayout     // Catch:{ all -> 0x05bb }
            android.widget.ImageView r13 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
            r12.removeView(r13)     // Catch:{ all -> 0x05bb }
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = -1
            if (r3 == 0) goto L_0x0491
            android.widget.FrameLayout r2 = r8.frameLayout     // Catch:{ all -> 0x05bb }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)     // Catch:{ all -> 0x05bb }
            r2.addView(r14, r1, r12)     // Catch:{ all -> 0x05bb }
            android.view.View r2 = r8.themeSwitchSunView     // Catch:{ all -> 0x05bb }
            r12 = 8
            r2.setVisibility(r12)     // Catch:{ all -> 0x05bb }
            goto L_0x04c4
        L_0x0491:
            android.widget.FrameLayout r2 = r8.frameLayout     // Catch:{ all -> 0x05bb }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)     // Catch:{ all -> 0x05bb }
            r13 = 1
            r2.addView(r14, r13, r12)     // Catch:{ all -> 0x05bb }
            android.view.View r2 = r8.themeSwitchSunView     // Catch:{ all -> 0x05bb }
            r12 = r0[r1]     // Catch:{ all -> 0x05bb }
            r13 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x05bb }
            int r12 = r12 - r14
            float r12 = (float) r12     // Catch:{ all -> 0x05bb }
            r2.setTranslationX(r12)     // Catch:{ all -> 0x05bb }
            android.view.View r2 = r8.themeSwitchSunView     // Catch:{ all -> 0x05bb }
            r12 = 1
            r14 = r0[r12]     // Catch:{ all -> 0x05bb }
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x05bb }
            int r14 = r14 - r12
            float r12 = (float) r14     // Catch:{ all -> 0x05bb }
            r2.setTranslationY(r12)     // Catch:{ all -> 0x05bb }
            android.view.View r2 = r8.themeSwitchSunView     // Catch:{ all -> 0x05bb }
            r2.setVisibility(r1)     // Catch:{ all -> 0x05bb }
            android.view.View r2 = r8.themeSwitchSunView     // Catch:{ all -> 0x05bb }
            r2.invalidate()     // Catch:{ all -> 0x05bb }
        L_0x04c4:
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
            r2.setImageBitmap(r7)     // Catch:{ all -> 0x05bb }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
            r2.setVisibility(r1)     // Catch:{ all -> 0x05bb }
            org.telegram.ui.Components.RLottieDrawable r2 = r4.getAnimatedDrawable()     // Catch:{ all -> 0x05bb }
            r8.themeSwitchSunDrawable = r2     // Catch:{ all -> 0x05bb }
            r2 = r0[r1]     // Catch:{ all -> 0x05bb }
            int r2 = r6 - r2
            r12 = r0[r1]     // Catch:{ all -> 0x05bb }
            int r12 = r6 - r12
            int r2 = r2 * r12
            r12 = 1
            r13 = r0[r12]     // Catch:{ all -> 0x05bb }
            int r13 = r15 - r13
            r14 = r0[r12]     // Catch:{ all -> 0x05bb }
            int r12 = r15 - r14
            int r13 = r13 * r12
            int r2 = r2 + r13
            double r12 = (double) r2     // Catch:{ all -> 0x05bb }
            double r12 = java.lang.Math.sqrt(r12)     // Catch:{ all -> 0x05bb }
            r2 = r0[r1]     // Catch:{ all -> 0x05bb }
            r14 = r0[r1]     // Catch:{ all -> 0x05bb }
            int r2 = r2 * r14
            r14 = 1
            r18 = r0[r14]     // Catch:{ all -> 0x05bb }
            int r19 = r15 - r18
            r20 = r0[r14]     // Catch:{ all -> 0x05bb }
            int r14 = r15 - r20
            int r19 = r19 * r14
            int r2 = r2 + r19
            double r1 = (double) r2     // Catch:{ all -> 0x05bb }
            double r1 = java.lang.Math.sqrt(r1)     // Catch:{ all -> 0x05bb }
            double r1 = java.lang.Math.max(r12, r1)     // Catch:{ all -> 0x05bb }
            float r1 = (float) r1     // Catch:{ all -> 0x05bb }
            r2 = 0
            r12 = r0[r2]     // Catch:{ all -> 0x05bb }
            int r12 = r6 - r12
            r13 = r0[r2]     // Catch:{ all -> 0x05bb }
            int r2 = r6 - r13
            int r12 = r12 * r2
            r2 = 1
            r13 = r0[r2]     // Catch:{ all -> 0x05bb }
            r19 = r0[r2]     // Catch:{ all -> 0x05bb }
            int r13 = r13 * r19
            int r12 = r12 + r13
            double r12 = (double) r12     // Catch:{ all -> 0x05bb }
            double r12 = java.lang.Math.sqrt(r12)     // Catch:{ all -> 0x05bb }
            r2 = 0
            r19 = r0[r2]     // Catch:{ all -> 0x05bb }
            r20 = r0[r2]     // Catch:{ all -> 0x05bb }
            int r19 = r19 * r20
            r2 = 1
            r20 = r0[r2]     // Catch:{ all -> 0x05bb }
            r21 = r0[r2]     // Catch:{ all -> 0x05bb }
            int r20 = r20 * r21
            int r2 = r19 + r20
            r19 = r15
            double r14 = (double) r2     // Catch:{ all -> 0x05bb }
            double r14 = java.lang.Math.sqrt(r14)     // Catch:{ all -> 0x05bb }
            double r12 = java.lang.Math.max(r12, r14)     // Catch:{ all -> 0x05bb }
            float r2 = (float) r12     // Catch:{ all -> 0x05bb }
            float r12 = java.lang.Math.max(r1, r2)     // Catch:{ all -> 0x05bb }
            r1 = r12
            if (r3 == 0) goto L_0x054a
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r8.drawerLayoutContainer     // Catch:{ all -> 0x05bb }
            goto L_0x054c
        L_0x054a:
            android.widget.ImageView r12 = r8.themeSwitchImageView     // Catch:{ all -> 0x05bb }
        L_0x054c:
            r13 = 0
            r15 = r0[r13]     // Catch:{ all -> 0x05bb }
            r13 = 1
            r14 = r0[r13]     // Catch:{ all -> 0x05bb }
            if (r3 == 0) goto L_0x0556
            r13 = 0
            goto L_0x0557
        L_0x0556:
            r13 = r1
        L_0x0557:
            if (r3 == 0) goto L_0x055c
            r22 = r1
            goto L_0x055f
        L_0x055c:
            r22 = r1
            r1 = 0
        L_0x055f:
            android.animation.Animator r1 = android.view.ViewAnimationUtils.createCircularReveal(r12, r15, r14, r13, r1)     // Catch:{ all -> 0x05bb }
            r12 = 400(0x190, double:1.976E-321)
            r1.setDuration(r12)     // Catch:{ all -> 0x05bb }
            android.view.animation.Interpolator r12 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x05bb }
            r1.setInterpolator(r12)     // Catch:{ all -> 0x05bb }
            org.telegram.ui.LaunchActivity$18 r12 = new org.telegram.ui.LaunchActivity$18     // Catch:{ all -> 0x05bb }
            r12.<init>(r3, r4)     // Catch:{ all -> 0x05bb }
            r1.addListener(r12)     // Catch:{ all -> 0x05bb }
            android.view.View r12 = r8.rippleAbove     // Catch:{ all -> 0x05bb }
            if (r12 == 0) goto L_0x059b
            r12 = 2
            float[] r12 = new float[r12]     // Catch:{ all -> 0x05bb }
            r13 = 0
            r14 = 0
            r12[r14] = r13     // Catch:{ all -> 0x05bb }
            r13 = 1065353216(0x3var_, float:1.0)
            r15 = 1
            r12[r15] = r13     // Catch:{ all -> 0x05bb }
            android.animation.ValueAnimator r12 = android.animation.ValueAnimator.ofFloat(r12)     // Catch:{ all -> 0x05bb }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0     // Catch:{ all -> 0x05bb }
            r13.<init>(r8)     // Catch:{ all -> 0x05bb }
            r12.addUpdateListener(r13)     // Catch:{ all -> 0x05bb }
            long r14 = r1.getDuration()     // Catch:{ all -> 0x05bb }
            r12.setDuration(r14)     // Catch:{ all -> 0x05bb }
            r12.start()     // Catch:{ all -> 0x05bb }
        L_0x059b:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda23 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda23     // Catch:{ all -> 0x05bb }
            r12.<init>(r8)     // Catch:{ all -> 0x05bb }
            if (r3 == 0) goto L_0x05b0
            r14 = 1
            r15 = r0[r14]     // Catch:{ all -> 0x05bb }
            int r15 = r19 - r15
            r14 = 1074790400(0x40100000, float:2.25)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x05bb }
            int r15 = r15 / r14
            long r14 = (long) r15     // Catch:{ all -> 0x05bb }
            goto L_0x05b2
        L_0x05b0:
            r14 = 50
        L_0x05b2:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r14)     // Catch:{ all -> 0x05bb }
            r1.start()     // Catch:{ all -> 0x05bb }
            r5 = 1
        L_0x05b9:
            r1 = 0
            goto L_0x05d9
        L_0x05bb:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            android.widget.ImageView r0 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x05d1 }
            r2 = 0
            r0.setImageDrawable(r2)     // Catch:{ Exception -> 0x05d1 }
            android.widget.FrameLayout r0 = r8.frameLayout     // Catch:{ Exception -> 0x05d1 }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x05d1 }
            r0.removeView(r2)     // Catch:{ Exception -> 0x05d1 }
            r2 = 0
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r2     // Catch:{ Exception -> 0x05d1 }
            goto L_0x05b9
        L_0x05d1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x05b9
        L_0x05d6:
            r1 = 0
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r1
        L_0x05d9:
            r0 = r11[r1]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0
            r1 = 1
            r1 = r11[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r2 = 3
            r2 = r11[r2]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.actionBarLayout
            r3.animateThemedValues(r0, r2, r1, r5)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x0604
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.layersActionBarLayout
            r3.animateThemedValues(r0, r2, r1, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.rightActionBarLayout
            r3.animateThemedValues(r0, r2, r1, r5)
        L_0x0604:
            goto L_0x0a08
        L_0x0606:
            int r0 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r9 != r0) goto L_0x063a
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0a08
            r1 = 0
            r1 = r11[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r0 = r0.getChildCount()
            r2 = 0
        L_0x0618:
            if (r2 >= r0) goto L_0x0638
            org.telegram.ui.Components.RecyclerListView r3 = r8.sideMenu
            android.view.View r3 = r3.getChildAt(r2)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r4 == 0) goto L_0x0635
            r4 = r3
            org.telegram.ui.Cells.DrawerUserCell r4 = (org.telegram.ui.Cells.DrawerUserCell) r4
            int r4 = r4.getAccountNumber()
            int r5 = r1.intValue()
            if (r4 != r5) goto L_0x0635
            r3.invalidate()
            goto L_0x0638
        L_0x0635:
            int r2 = r2 + 1
            goto L_0x0618
        L_0x0638:
            goto L_0x0a08
        L_0x063a:
            int r0 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r9 != r0) goto L_0x064c
            r1 = 0
            r0 = r11[r1]     // Catch:{ all -> 0x0649 }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x0649 }
            r1 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r8, r1)     // Catch:{ all -> 0x0649 }
            goto L_0x064a
        L_0x0649:
            r0 = move-exception
        L_0x064a:
            goto L_0x0a08
        L_0x064c:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoaded
            if (r9 != r0) goto L_0x072b
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x066d
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x066d
            r2 = 1
            r8.updateAppUpdateViews(r2)
        L_0x066d:
            java.lang.String r1 = r8.loadingThemeFileName
            if (r1 == 0) goto L_0x06fa
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0729
            r1 = 0
            r8.loadingThemeFileName = r1
            java.io.File r1 = new java.io.File
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "remote"
            r3.append(r4)
            org.telegram.tgnet.TLRPC$TL_theme r4 = r8.loadingTheme
            long r4 = r4.id
            r3.append(r4)
            java.lang.String r4 = ".attheme"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.<init>(r2, r3)
            org.telegram.tgnet.TLRPC$TL_theme r2 = r8.loadingTheme
            java.lang.String r2 = r2.title
            org.telegram.tgnet.TLRPC$TL_theme r3 = r8.loadingTheme
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.fillThemeValues(r1, r2, r3)
            if (r2 == 0) goto L_0x06f6
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x06d9
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r4 = r3.exists()
            if (r4 != 0) goto L_0x06d9
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r4 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r4.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r5 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r5.<init>()
            java.lang.String r6 = r2.slug
            r5.slug = r6
            r4.wallpaper = r5
            int r6 = r2.account
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87 r7 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda87
            r7.<init>(r8, r2)
            r6.sendRequest(r4, r7)
            return
        L_0x06d9:
            org.telegram.tgnet.TLRPC$TL_theme r3 = r8.loadingTheme
            java.lang.String r3 = r3.title
            org.telegram.tgnet.TLRPC$TL_theme r4 = r8.loadingTheme
            r5 = 1
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r1, r3, r4, r5)
            if (r3 == 0) goto L_0x06f6
            org.telegram.ui.ThemePreviewActivity r4 = new org.telegram.ui.ThemePreviewActivity
            r14 = 1
            r15 = 0
            r16 = 0
            r17 = 0
            r12 = r4
            r13 = r3
            r12.<init>(r13, r14, r15, r16, r17)
            r8.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(r4)
        L_0x06f6:
            r23.onThemeLoadFinish()
            goto L_0x0729
        L_0x06fa:
            java.lang.String r1 = r8.loadingThemeWallpaperName
            if (r1 == 0) goto L_0x0729
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0729
            r1 = 0
            r8.loadingThemeWallpaperName = r1
            r1 = 1
            r1 = r11[r1]
            java.io.File r1 = (java.io.File) r1
            boolean r2 = r8.loadingThemeAccent
            if (r2 == 0) goto L_0x071d
            org.telegram.tgnet.TLRPC$TL_theme r2 = r8.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r8.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r8.loadingThemeInfo
            r8.openThemeAccentPreview(r2, r3, r4)
            r23.onThemeLoadFinish()
            goto L_0x0729
        L_0x071d:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r8.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61
            r4.<init>(r8, r2, r1)
            r3.postRunnable(r4)
        L_0x0729:
            goto L_0x0a08
        L_0x072b:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoadFailed
            if (r9 != r0) goto L_0x0761
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r8.loadingThemeFileName
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x0744
            java.lang.String r1 = r8.loadingThemeWallpaperName
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0747
        L_0x0744:
            r23.onThemeLoadFinish()
        L_0x0747:
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x075f
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x075f
            r2 = 1
            r8.updateAppUpdateViews(r2)
        L_0x075f:
            goto L_0x0a08
        L_0x0761:
            int r0 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r9 != r0) goto L_0x0778
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x076a
            return
        L_0x076a:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x0773
            r23.onPasscodeResume()
            goto L_0x0a08
        L_0x0773:
            r23.onPasscodePause()
            goto L_0x0a08
        L_0x0778:
            int r0 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r9 != r0) goto L_0x0793
            int r0 = r11.length
            if (r0 <= 0) goto L_0x078c
            r1 = 0
            r0 = r11[r1]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x078c
            r1 = 1
            goto L_0x078d
        L_0x078c:
            r1 = 0
        L_0x078d:
            r0 = r1
            r8.checkSystemBarColors(r0)
            goto L_0x0a08
        L_0x0793:
            int r0 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r9 != r0) goto L_0x07c3
            int r0 = r11.length
            r1 = 1
            if (r0 <= r1) goto L_0x0a08
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a08
            int r0 = r8.currentAccount
            r2 = 2
            r2 = r11[r2]
            org.telegram.tgnet.TLRPC$TL_error r2 = (org.telegram.tgnet.TLRPC.TL_error) r2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r1
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r1 = r11[r1]
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.ui.Components.AlertsCreator.processError(r0, r2, r3, r1, r4)
            goto L_0x0a08
        L_0x07c3:
            int r0 = org.telegram.messenger.NotificationCenter.stickersImportComplete
            if (r9 != r0) goto L_0x07f4
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r25)
            r2 = 0
            r0 = r11[r2]
            r3 = r0
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r4 = 2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07ea
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            r5 = 1
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r5 = r0
            goto L_0x07eb
        L_0x07ea:
            r5 = 0
        L_0x07eb:
            r6 = 0
            r7 = 1
            r2 = r23
            r1.toggleStickerSet(r2, r3, r4, r5, r6, r7)
            goto L_0x0a08
        L_0x07f4:
            int r0 = org.telegram.messenger.NotificationCenter.newSuggestionsAvailable
            if (r9 != r0) goto L_0x07ff
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            r0.invalidateViews()
            goto L_0x0a08
        L_0x07ff:
            int r0 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r9 != r0) goto L_0x0957
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a08
            r1 = 0
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 0
            r2 = 0
            boolean r3 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r3 == 0) goto L_0x0826
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r3 == 0) goto L_0x0826
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            android.widget.FrameLayout r1 = r3.getContainer()
            r7 = r1
            goto L_0x0827
        L_0x0826:
            r7 = r1
        L_0x0827:
            if (r7 != 0) goto L_0x083a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r1 = r1.get(r3)
            r2 = r1
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r13 = r2
            goto L_0x083c
        L_0x083a:
            r4 = 1
            r13 = r2
        L_0x083c:
            r1 = 0
            switch(r0) {
                case 0: goto L_0x091e;
                case 1: goto L_0x08f9;
                case 2: goto L_0x08c9;
                case 3: goto L_0x0898;
                case 4: goto L_0x0863;
                case 5: goto L_0x0843;
                default: goto L_0x0841;
            }
        L_0x0841:
            goto L_0x0955
        L_0x0843:
            r1 = r11[r4]
            org.telegram.ui.LauncherIconController$LauncherIcon r1 = (org.telegram.ui.LauncherIconController.LauncherIcon) r1
            org.telegram.ui.Components.AppIconBulletinLayout r2 = new org.telegram.ui.Components.AppIconBulletinLayout
            r3 = 0
            r2.<init>(r8, r1, r3)
            r3 = 1500(0x5dc, float:2.102E-42)
            if (r13 == 0) goto L_0x085a
            org.telegram.ui.Components.Bulletin r4 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r13, (org.telegram.ui.Components.Bulletin.Layout) r2, (int) r3)
            r4.show()
            goto L_0x0955
        L_0x085a:
            org.telegram.ui.Components.Bulletin r4 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r2, (int) r3)
            r4.show()
            goto L_0x0955
        L_0x0863:
            if (r13 == 0) goto L_0x0880
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r13)
            r3 = 1
            r2 = r11[r3]
            java.lang.String r2 = (java.lang.String) r2
            r4 = 2
            r3 = r11[r4]
            java.lang.String r3 = (java.lang.String) r3
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r13.getResourceProvider()
            org.telegram.ui.Components.Bulletin r1 = r1.createErrorBulletinSubtitle(r2, r3, r4)
            r1.show()
            goto L_0x0955
        L_0x0880:
            r3 = 1
            r4 = 2
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r3 = r11[r3]
            java.lang.String r3 = (java.lang.String) r3
            r4 = r11[r4]
            java.lang.String r4 = (java.lang.String) r4
            org.telegram.ui.Components.Bulletin r1 = r2.createErrorBulletinSubtitle(r3, r4, r1)
            r1.show()
            goto L_0x0955
        L_0x0898:
            r3 = 1
            r3 = r11[r3]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x08ab
            r1 = 2131629291(0x7f0e14eb, float:1.8885899E38)
            java.lang.String r2 = "YourNameChanged"
            goto L_0x08b0
        L_0x08ab:
            r1 = 2131624969(0x7f0e0409, float:1.8877133E38)
            java.lang.String r2 = "CannelTitleChanged"
        L_0x08b0:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r7 == 0) goto L_0x08bc
            r2 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r2)
            goto L_0x08c0
        L_0x08bc:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r13)
        L_0x08c0:
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r1)
            r2.show()
            goto L_0x0955
        L_0x08c9:
            r3 = 1
            r3 = r11[r3]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x08dc
            r1 = 2131629274(0x7f0e14da, float:1.8885864E38)
            java.lang.String r2 = "YourBioChanged"
            goto L_0x08e1
        L_0x08dc:
            r1 = 2131624901(0x7f0e03c5, float:1.8876995E38)
            java.lang.String r2 = "CannelDescriptionChanged"
        L_0x08e1:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r7 == 0) goto L_0x08ed
            r2 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r2)
            goto L_0x08f1
        L_0x08ed:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r13)
        L_0x08f1:
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r1)
            r2.show()
            goto L_0x0955
        L_0x08f9:
            if (r13 == 0) goto L_0x090c
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r13)
            r2 = 1
            r2 = r11[r2]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r1 = r1.createErrorBulletin(r2)
            r1.show()
            goto L_0x0955
        L_0x090c:
            r2 = 1
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r2 = r11[r2]
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.ui.Components.Bulletin r1 = r1.createErrorBulletin(r2)
            r1.show()
            goto L_0x0955
        L_0x091e:
            r2 = 1
            r1 = r11[r2]
            r14 = r1
            org.telegram.tgnet.TLRPC$Document r14 = (org.telegram.tgnet.TLRPC.Document) r14
            r1 = 2
            r1 = r11[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r15 = r1.intValue()
            org.telegram.ui.Components.StickerSetBulletinLayout r16 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = 0
            r6 = 0
            r1 = r16
            r2 = r23
            r4 = r15
            r5 = r14
            r1.<init>(r2, r3, r4, r5, r6)
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r15 == r12) goto L_0x0941
            r3 = 7
            if (r15 != r3) goto L_0x0943
        L_0x0941:
            r2 = 3500(0xdac, float:4.905E-42)
        L_0x0943:
            if (r13 == 0) goto L_0x094d
            org.telegram.ui.Components.Bulletin r3 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r13, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r2)
            r3.show()
            goto L_0x0955
        L_0x094d:
            org.telegram.ui.Components.Bulletin r3 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r2)
            r3.show()
        L_0x0955:
            goto L_0x0a08
        L_0x0957:
            int r0 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r9 != r0) goto L_0x0961
            r1 = 0
            r8.checkWasMutedByAdmin(r1)
            goto L_0x0a08
        L_0x0961:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r9 != r0) goto L_0x09bd
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.updateTextView
            if (r0 == 0) goto L_0x0a08
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x0a08
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            if (r1 == 0) goto L_0x09bc
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x09bc
            r2 = 1
            r3 = r11[r2]
            r2 = r3
            java.lang.Long r2 = (java.lang.Long) r2
            r3 = 2
            r3 = r11[r3]
            java.lang.Long r3 = (java.lang.Long) r3
            long r4 = r2.longValue()
            float r4 = (float) r4
            long r5 = r3.longValue()
            float r5 = (float) r5
            float r4 = r4 / r5
            org.telegram.ui.Components.RadialProgress2 r5 = r8.updateLayoutIcon
            r6 = 1
            r5.setProgress(r4, r6)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r8.updateTextView
            r7 = 2131624380(0x7f0e01bc, float:1.8875938E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r12 = r12 * r4
            int r12 = (int) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13 = 0
            r6[r13] = r12
            java.lang.String r12 = "AppUpdateDownloading"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            r5.setText(r6)
        L_0x09bc:
            goto L_0x0a08
        L_0x09bd:
            int r0 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r9 != r0) goto L_0x09d1
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            r1 = 1
            if (r0 != r1) goto L_0x09cc
            r1 = 1
            goto L_0x09cd
        L_0x09cc:
            r1 = 0
        L_0x09cd:
            r8.updateAppUpdateViews(r1)
            goto L_0x0a08
        L_0x09d1:
            int r0 = org.telegram.messenger.NotificationCenter.currentUserShowLimitReachedDialog
            if (r9 != r0) goto L_0x0a08
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a08
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            r2 = 1
            int r1 = r1 - r2
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            android.app.Activity r1 = r0.getParentActivity()
            if (r1 == 0) goto L_0x0a08
            org.telegram.ui.Components.Premium.LimitReachedBottomSheet r1 = new org.telegram.ui.Components.Premium.LimitReachedBottomSheet
            android.app.Activity r2 = r0.getParentActivity()
            r3 = 0
            r3 = r11[r3]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r4 = r8.currentAccount
            r1.<init>(r0, r2, r3, r4)
            r0.showDialog(r1)
        L_0x0a08:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$81(int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(account);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* renamed from: lambda$didReceivedNotification$82$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3632lambda$didReceivedNotification$82$orgtelegramuiLaunchActivity(DialogInterface dialog, int which) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* renamed from: lambda$didReceivedNotification$84$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3633lambda$didReceivedNotification$84$orgtelegramuiLaunchActivity(HashMap waitingForLocation, int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity fragment = new LocationActivity(0);
                fragment.setDelegate(new LaunchActivity$$ExternalSyntheticLambda3(waitingForLocation, account));
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(fragment);
            }
        }
    }

    static /* synthetic */ void lambda$didReceivedNotification$83(HashMap waitingForLocation, int account, TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        for (Map.Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
            MessageObject messageObject = entry.getValue();
            SendMessagesHelper.getInstance(account).sendMessage(location, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
        }
    }

    /* renamed from: lambda$didReceivedNotification$88$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3634lambda$didReceivedNotification$88$orgtelegramuiLaunchActivity(ValueAnimator a) {
        this.frameLayout.invalidate();
    }

    /* renamed from: lambda$didReceivedNotification$89$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3635lambda$didReceivedNotification$89$orgtelegramuiLaunchActivity() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    /* renamed from: lambda$didReceivedNotification$91$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3637lambda$didReceivedNotification$91$orgtelegramuiLaunchActivity(Theme.ThemeInfo themeInfo, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda45(this, response, themeInfo));
    }

    /* renamed from: lambda$didReceivedNotification$90$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3636lambda$didReceivedNotification$90$orgtelegramuiLaunchActivity(TLObject response, Theme.ThemeInfo themeInfo) {
        if (response instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(wallPaper.document);
            this.loadingThemeWallpaper = wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    /* renamed from: lambda$didReceivedNotification$93$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3639lambda$didReceivedNotification$93$orgtelegramuiLaunchActivity(Theme.ThemeInfo info, File file) {
        info.createBackground(file, info.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda24(this));
    }

    /* renamed from: lambda$didReceivedNotification$92$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3638lambda$didReceivedNotification$92$orgtelegramuiLaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            Theme.ThemeInfo finalThemeInfo = Theme.applyThemeFile(new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme"), this.loadingTheme.title, this.loadingTheme, true);
            if (finalThemeInfo != null) {
                m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ThemePreviewActivity(finalThemeInfo, true, 0, false, false));
            }
            onThemeLoadFinish();
        }
    }

    private void invalidateCachedViews(View parent) {
        if (parent.getLayerType() != 0) {
            parent.invalidate();
        }
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateCachedViews(viewGroup.getChildAt(i));
            }
        }
    }

    private void checkWasMutedByAdmin(boolean checkOnly) {
        long did;
        VoIPService voIPService = VoIPService.getSharedInstance();
        boolean z = false;
        if (voIPService == null || voIPService.groupCall == null) {
            this.wasMutedByAdminRaisedHand = false;
            return;
        }
        boolean wasMuted = this.wasMutedByAdminRaisedHand;
        ChatObject.Call call = voIPService.groupCall;
        TLRPC.InputPeer peer = voIPService.getGroupCallPeer();
        if (peer == null) {
            did = UserConfig.getInstance(this.currentAccount).clientUserId;
        } else if (peer.user_id != 0) {
            did = peer.user_id;
        } else if (peer.chat_id != 0) {
            did = -peer.chat_id;
        } else {
            did = -peer.channel_id;
        }
        TLRPC.TL_groupCallParticipant participant = call.participants.get(did);
        boolean mutedByAdmin = participant != null && !participant.can_self_unmute && participant.muted;
        if (mutedByAdmin && participant.raise_hand_rating != 0) {
            z = true;
        }
        this.wasMutedByAdminRaisedHand = z;
        if (!checkOnly && wasMuted && !z && !mutedByAdmin && GroupCallActivity.groupCallInstance == null) {
            showVoiceChatTooltip(38);
        }
    }

    private void showVoiceChatTooltip(int action) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null && !mainFragmentsStack.isEmpty() && voIPService.groupCall != null && !mainFragmentsStack.isEmpty()) {
            TLRPC.Chat chat = voIPService.getChat();
            BaseFragment fragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            if (fragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) fragment;
                if (chatActivity.getDialogId() == (-chat.id)) {
                    chat = null;
                }
                chatActivity.getUndoView().showWithAction(0, action, (Object) chat);
            } else if (fragment instanceof DialogsActivity) {
                ((DialogsActivity) fragment).getUndoView().showWithAction(0, action, (Object) chat);
            } else if (fragment instanceof ProfileActivity) {
                ((ProfileActivity) fragment).getUndoView().showWithAction(0, action, (Object) chat);
            }
            if (action == 38 && VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().playAllowTalkSound();
            }
        }
    }

    private String getStringForLanguageAlert(HashMap<String, String> map, String key, int intKey) {
        String value = map.get(key);
        if (value == null) {
            return LocaleController.getString(key, intKey);
        }
        return value;
    }

    private void openThemeAccentPreview(TLRPC.TL_theme t, TLRPC.TL_wallPaper wallPaper, Theme.ThemeInfo info) {
        int lastId = info.lastAccentId;
        Theme.ThemeAccent accent = info.createNewAccent(t, this.currentAccount);
        info.prevAccentId = info.currentAccentId;
        info.setCurrentAccentId(accent.id);
        accent.pattern = wallPaper;
        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ThemePreviewActivity(info, lastId != info.lastAccentId, 0, false, false));
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
            Utilities.globalQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda22(this), 2000);
        }
    }

    /* renamed from: lambda$checkFreeDiscSpace$95$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3630lambda$checkFreeDiscSpace$95$orgtelegramuiLaunchActivity() {
        File path;
        long freeSpace;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs(preferences.getLong("last_space_check", 0) - System.currentTimeMillis()) >= NUM && (path = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(path.getAbsolutePath());
                    if (Build.VERSION.SDK_INT < 18) {
                        freeSpace = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                    } else {
                        freeSpace = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                    }
                    if (freeSpace < NUM) {
                        preferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda21(this));
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: lambda$checkFreeDiscSpace$94$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3629lambda$checkFreeDiscSpace$94$orgtelegramuiLaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable th) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0054 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0056 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005c A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005f A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0066 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006f A[SYNTHETIC, Splitter:B:28:0x006f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r18, org.telegram.messenger.LocaleController.LocaleInfo r19, java.lang.String r20) {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "ChooseYourLanguageOther"
            java.lang.String r2 = "ChooseYourLanguage"
            r3 = 0
            r1.loadingLocaleDialog = r3     // Catch:{ Exception -> 0x0130 }
            r4 = r18
            boolean r5 = r4.builtIn     // Catch:{ Exception -> 0x012e }
            r6 = 1
            if (r5 != 0) goto L_0x001d
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x012e }
            boolean r5 = r5.isCurrentLocalLocale()     // Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r5 = 0
            goto L_0x001e
        L_0x001d:
            r5 = 1
        L_0x001e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x012e }
            r7.<init>((android.content.Context) r1)     // Catch:{ Exception -> 0x012e }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x012e }
            r9 = 2131625126(0x7f0e04a6, float:1.8877451E38)
            java.lang.String r8 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x012e }
            r7.setTitle(r8)     // Catch:{ Exception -> 0x012e }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x012e }
            java.lang.String r2 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x012e }
            r7.setSubtitle(r2)     // Catch:{ Exception -> 0x012e }
            android.widget.LinearLayout r2 = new android.widget.LinearLayout     // Catch:{ Exception -> 0x012e }
            r2.<init>(r1)     // Catch:{ Exception -> 0x012e }
            r2.setOrientation(r6)     // Catch:{ Exception -> 0x012e }
            r8 = 2
            org.telegram.ui.Cells.LanguageCell[] r9 = new org.telegram.ui.Cells.LanguageCell[r8]     // Catch:{ Exception -> 0x012e }
            org.telegram.messenger.LocaleController$LocaleInfo[] r10 = new org.telegram.messenger.LocaleController.LocaleInfo[r6]     // Catch:{ Exception -> 0x012e }
            org.telegram.messenger.LocaleController$LocaleInfo[] r11 = new org.telegram.messenger.LocaleController.LocaleInfo[r8]     // Catch:{ Exception -> 0x012e }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x012e }
            java.lang.String r13 = "English"
            r14 = 2131625641(0x7f0e06a9, float:1.8878496E38)
            java.lang.String r12 = r1.getStringForLanguageAlert(r12, r13, r14)     // Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x0056
            r13 = r4
            goto L_0x0058
        L_0x0056:
            r13 = r19
        L_0x0058:
            r11[r3] = r13     // Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x005f
            r13 = r19
            goto L_0x0060
        L_0x005f:
            r13 = r4
        L_0x0060:
            r11[r6] = r13     // Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x0066
            r13 = r4
            goto L_0x0068
        L_0x0066:
            r13 = r19
        L_0x0068:
            r10[r3] = r13     // Catch:{ Exception -> 0x012e }
            r13 = 0
        L_0x006b:
            java.lang.String r16 = "dialogButtonSelector"
            if (r13 >= r8) goto L_0x00c4
            org.telegram.ui.Cells.LanguageCell r14 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x012e }
            r14.<init>(r1)     // Catch:{ Exception -> 0x012e }
            r9[r13] = r14     // Catch:{ Exception -> 0x012e }
            r14 = r9[r13]     // Catch:{ Exception -> 0x012e }
            r15 = r11[r13]     // Catch:{ Exception -> 0x012e }
            r3 = r11[r13]     // Catch:{ Exception -> 0x012e }
            r8 = r19
            if (r3 != r8) goto L_0x0082
            r3 = r12
            goto L_0x0083
        L_0x0082:
            r3 = 0
        L_0x0083:
            r14.setLanguage(r15, r3, r6)     // Catch:{ Exception -> 0x012c }
            r3 = r9[r13]     // Catch:{ Exception -> 0x012c }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x012c }
            r3.setTag(r14)     // Catch:{ Exception -> 0x012c }
            r3 = r9[r13]     // Catch:{ Exception -> 0x012c }
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r16)     // Catch:{ Exception -> 0x012c }
            r15 = 2
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r15)     // Catch:{ Exception -> 0x012c }
            r3.setBackground(r14)     // Catch:{ Exception -> 0x012c }
            r3 = r9[r13]     // Catch:{ Exception -> 0x012c }
            if (r13 != 0) goto L_0x00a3
            r14 = 1
            goto L_0x00a4
        L_0x00a3:
            r14 = 0
        L_0x00a4:
            r15 = 0
            r3.setLanguageSelected(r14, r15)     // Catch:{ Exception -> 0x012c }
            r3 = r9[r13]     // Catch:{ Exception -> 0x012c }
            r6 = -1
            r14 = 50
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r14)     // Catch:{ Exception -> 0x012c }
            r2.addView(r3, r6)     // Catch:{ Exception -> 0x012c }
            r3 = r9[r13]     // Catch:{ Exception -> 0x012c }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda12 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda12     // Catch:{ Exception -> 0x012c }
            r6.<init>(r10, r9)     // Catch:{ Exception -> 0x012c }
            r3.setOnClickListener(r6)     // Catch:{ Exception -> 0x012c }
            int r13 = r13 + 1
            r3 = 0
            r6 = 1
            r8 = 2
            goto L_0x006b
        L_0x00c4:
            r8 = r19
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x012c }
            r3.<init>(r1)     // Catch:{ Exception -> 0x012c }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x012c }
            r13 = 2131625127(0x7f0e04a7, float:1.8877453E38)
            java.lang.String r6 = r1.getStringForLanguageAlert(r6, r0, r13)     // Catch:{ Exception -> 0x012c }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x012c }
            java.lang.String r0 = r1.getStringForLanguageAlert(r14, r0, r13)     // Catch:{ Exception -> 0x012c }
            r3.setValue(r6, r0)     // Catch:{ Exception -> 0x012c }
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)     // Catch:{ Exception -> 0x012c }
            r6 = 2
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r0, r6)     // Catch:{ Exception -> 0x012c }
            r3.setBackground(r0)     // Catch:{ Exception -> 0x012c }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda11 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda11     // Catch:{ Exception -> 0x012c }
            r0.<init>(r1)     // Catch:{ Exception -> 0x012c }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x012c }
            r0 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r0)     // Catch:{ Exception -> 0x012c }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x012c }
            r7.setView(r2)     // Catch:{ Exception -> 0x012c }
            java.lang.String r0 = "OK"
            r6 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r6)     // Catch:{ Exception -> 0x012c }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8     // Catch:{ Exception -> 0x012c }
            r6.<init>(r1, r10)     // Catch:{ Exception -> 0x012c }
            r7.setNegativeButton(r0, r6)     // Catch:{ Exception -> 0x012c }
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r7)     // Catch:{ Exception -> 0x012c }
            r1.localeDialog = r0     // Catch:{ Exception -> 0x012c }
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x012c }
            android.content.SharedPreferences$Editor r6 = r0.edit()     // Catch:{ Exception -> 0x012c }
            java.lang.String r13 = "language_showed2"
            r14 = r20
            android.content.SharedPreferences$Editor r6 = r6.putString(r13, r14)     // Catch:{ Exception -> 0x012a }
            r6.commit()     // Catch:{ Exception -> 0x012a }
            goto L_0x013a
        L_0x012a:
            r0 = move-exception
            goto L_0x0137
        L_0x012c:
            r0 = move-exception
            goto L_0x0135
        L_0x012e:
            r0 = move-exception
            goto L_0x0133
        L_0x0130:
            r0 = move-exception
            r4 = r18
        L_0x0133:
            r8 = r19
        L_0x0135:
            r14 = r20
        L_0x0137:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x013a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    static /* synthetic */ void lambda$showLanguageAlertInternal$96(LocaleController.LocaleInfo[] selectedLanguage, LanguageCell[] cells, View v) {
        Integer tag = (Integer) v.getTag();
        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
        int a1 = 0;
        while (a1 < cells.length) {
            cells[a1].setLanguageSelected(a1 == tag.intValue(), true);
            a1++;
        }
    }

    /* renamed from: lambda$showLanguageAlertInternal$97$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3706x1067CLASSNAME(View v) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* renamed from: lambda$showLanguageAlertInternal$98$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3707x98980200(LocaleController.LocaleInfo[] selectedLanguage, DialogInterface dialog, int which) {
        LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    /* access modifiers changed from: private */
    public void drawRippleAbove(Canvas canvas, View parent) {
        View view;
        if (parent != null && (view = this.rippleAbove) != null && view.getBackground() != null) {
            if (this.tempLocation == null) {
                this.tempLocation = new int[2];
            }
            this.rippleAbove.getLocationInWindow(this.tempLocation);
            int[] iArr = this.tempLocation;
            int x = iArr[0];
            int y = iArr[1];
            parent.getLocationInWindow(iArr);
            int[] iArr2 = this.tempLocation;
            int y2 = y - iArr2[1];
            canvas.save();
            canvas.translate((float) (x - iArr2[0]), (float) y2);
            this.rippleAbove.getBackground().draw(canvas);
            canvas.restore();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e0 A[Catch:{ Exception -> 0x0191 }, LOOP:0: B:31:0x008d->B:50:0x00e0, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00df A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlert(boolean r18) {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "ChangeLanguageLater"
            java.lang.String r2 = "ChooseYourLanguageOther"
            java.lang.String r3 = "ChooseYourLanguage"
            java.lang.String r4 = "English"
            java.lang.String r5 = "-"
            int r6 = r1.currentAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            boolean r6 = r6.isClientActivated()
            if (r6 != 0) goto L_0x0019
            return
        L_0x0019:
            boolean r6 = r1.loadingLocaleDialog     // Catch:{ Exception -> 0x0191 }
            if (r6 != 0) goto L_0x0190
            boolean r6 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0191 }
            if (r6 == 0) goto L_0x0023
            goto L_0x0190
        L_0x0023:
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0191 }
            java.lang.String r7 = "language_showed2"
            java.lang.String r8 = ""
            java.lang.String r7 = r6.getString(r7, r8)     // Catch:{ Exception -> 0x0191 }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x0191 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ Exception -> 0x0191 }
            java.lang.String r8 = r8.suggestedLangCode     // Catch:{ Exception -> 0x0191 }
            if (r18 != 0) goto L_0x0058
            boolean r9 = r7.equals(r8)     // Catch:{ Exception -> 0x0191 }
            if (r9 == 0) goto L_0x0058
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0191 }
            if (r0 == 0) goto L_0x0057
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0191 }
            r0.<init>()     // Catch:{ Exception -> 0x0191 }
            java.lang.String r2 = "alert already showed for "
            r0.append(r2)     // Catch:{ Exception -> 0x0191 }
            r0.append(r7)     // Catch:{ Exception -> 0x0191 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0191 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0191 }
        L_0x0057:
            return
        L_0x0058:
            r9 = 2
            org.telegram.messenger.LocaleController$LocaleInfo[] r9 = new org.telegram.messenger.LocaleController.LocaleInfo[r9]     // Catch:{ Exception -> 0x0191 }
            boolean r10 = r8.contains(r5)     // Catch:{ Exception -> 0x0191 }
            r11 = 0
            if (r10 == 0) goto L_0x0069
            java.lang.String[] r10 = r8.split(r5)     // Catch:{ Exception -> 0x0191 }
            r10 = r10[r11]     // Catch:{ Exception -> 0x0191 }
            goto L_0x006a
        L_0x0069:
            r10 = r8
        L_0x006a:
            java.lang.String r12 = "in"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0191 }
            if (r12 == 0) goto L_0x0075
            java.lang.String r12 = "id"
            goto L_0x008c
        L_0x0075:
            java.lang.String r12 = "iw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0191 }
            if (r12 == 0) goto L_0x0080
            java.lang.String r12 = "he"
            goto L_0x008c
        L_0x0080:
            java.lang.String r12 = "jw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0191 }
            if (r12 == 0) goto L_0x008b
            java.lang.String r12 = "jv"
            goto L_0x008c
        L_0x008b:
            r12 = 0
        L_0x008c:
            r13 = 0
        L_0x008d:
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<org.telegram.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0191 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0191 }
            if (r13 >= r14) goto L_0x00e4
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<org.telegram.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0191 }
            java.lang.Object r14 = r14.get(r13)     // Catch:{ Exception -> 0x0191 }
            org.telegram.messenger.LocaleController$LocaleInfo r14 = (org.telegram.messenger.LocaleController.LocaleInfo) r14     // Catch:{ Exception -> 0x0191 }
            java.lang.String r15 = r14.shortName     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = "en"
            boolean r11 = r15.equals(r11)     // Catch:{ Exception -> 0x0191 }
            if (r11 == 0) goto L_0x00b2
            r11 = 0
            r9[r11] = r14     // Catch:{ Exception -> 0x0191 }
        L_0x00b2:
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0191 }
            java.lang.String r15 = "_"
            java.lang.String r11 = r11.replace(r15, r5)     // Catch:{ Exception -> 0x0191 }
            boolean r11 = r11.equals(r8)     // Catch:{ Exception -> 0x0191 }
            if (r11 != 0) goto L_0x00d3
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0191 }
            boolean r11 = r11.equals(r10)     // Catch:{ Exception -> 0x0191 }
            if (r11 != 0) goto L_0x00d3
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0191 }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x0191 }
            if (r11 == 0) goto L_0x00d1
            goto L_0x00d3
        L_0x00d1:
            r11 = 1
            goto L_0x00d6
        L_0x00d3:
            r11 = 1
            r9[r11] = r14     // Catch:{ Exception -> 0x0191 }
        L_0x00d6:
            r15 = 0
            r16 = r9[r15]     // Catch:{ Exception -> 0x0191 }
            if (r16 == 0) goto L_0x00e0
            r15 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            if (r15 == 0) goto L_0x00e0
            goto L_0x00e4
        L_0x00e0:
            int r13 = r13 + 1
            r11 = 0
            goto L_0x008d
        L_0x00e4:
            r5 = 0
            r11 = r9[r5]     // Catch:{ Exception -> 0x0191 }
            if (r11 == 0) goto L_0x018f
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            if (r13 == 0) goto L_0x018f
            r13 = r9[r5]     // Catch:{ Exception -> 0x0191 }
            r5 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            if (r13 != r5) goto L_0x00f6
            goto L_0x018f
        L_0x00f6:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0191 }
            if (r5 == 0) goto L_0x0124
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0191 }
            r5.<init>()     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = "show lang alert for "
            r5.append(r11)     // Catch:{ Exception -> 0x0191 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0191 }
            r5.append(r11)     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = " and "
            r5.append(r11)     // Catch:{ Exception -> 0x0191 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0191 }
            r5.append(r11)     // Catch:{ Exception -> 0x0191 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0191 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0191 }
        L_0x0124:
            r5 = 0
            r1.systemLocaleStrings = r5     // Catch:{ Exception -> 0x0191 }
            r1.englishLocaleStrings = r5     // Catch:{ Exception -> 0x0191 }
            r5 = 1
            r1.loadingLocaleDialog = r5     // Catch:{ Exception -> 0x0191 }
            org.telegram.tgnet.TLRPC$TL_langpack_getStrings r11 = new org.telegram.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0191 }
            r11.<init>()     // Catch:{ Exception -> 0x0191 }
            r5 = r9[r5]     // Catch:{ Exception -> 0x0191 }
            java.lang.String r5 = r5.getLangCode()     // Catch:{ Exception -> 0x0191 }
            r11.lang_code = r5     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0191 }
            r5.add(r4)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0191 }
            r5.add(r3)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0191 }
            r5.add(r2)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0191 }
            r5.add(r0)     // Catch:{ Exception -> 0x0191 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0191 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)     // Catch:{ Exception -> 0x0191 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda90 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda90     // Catch:{ Exception -> 0x0191 }
            r13.<init>(r1, r9, r8)     // Catch:{ Exception -> 0x0191 }
            r14 = 8
            r5.sendRequest(r11, r13, r14)     // Catch:{ Exception -> 0x0191 }
            org.telegram.tgnet.TLRPC$TL_langpack_getStrings r5 = new org.telegram.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0191 }
            r5.<init>()     // Catch:{ Exception -> 0x0191 }
            r11 = 0
            r11 = r9[r11]     // Catch:{ Exception -> 0x0191 }
            java.lang.String r11 = r11.getLangCode()     // Catch:{ Exception -> 0x0191 }
            r5.lang_code = r11     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r11 = r5.keys     // Catch:{ Exception -> 0x0191 }
            r11.add(r4)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r4 = r5.keys     // Catch:{ Exception -> 0x0191 }
            r4.add(r3)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r3 = r5.keys     // Catch:{ Exception -> 0x0191 }
            r3.add(r2)     // Catch:{ Exception -> 0x0191 }
            java.util.ArrayList<java.lang.String> r2 = r5.keys     // Catch:{ Exception -> 0x0191 }
            r2.add(r0)     // Catch:{ Exception -> 0x0191 }
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0191 }
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)     // Catch:{ Exception -> 0x0191 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda91     // Catch:{ Exception -> 0x0191 }
            r2.<init>(r1, r9, r8)     // Catch:{ Exception -> 0x0191 }
            r0.sendRequest(r5, r2, r14)     // Catch:{ Exception -> 0x0191 }
            goto L_0x0195
        L_0x018f:
            return
        L_0x0190:
            return
        L_0x0191:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0195:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlert(boolean):void");
    }

    /* renamed from: lambda$showLanguageAlert$100$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3702lambda$showLanguageAlert$100$orgtelegramuiLaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda38(this, keys, infos, systemLang));
    }

    /* renamed from: lambda$showLanguageAlert$99$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3705lambda$showLanguageAlert$99$orgtelegramuiLaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
        this.systemLocaleStrings = keys;
        if (this.englishLocaleStrings != null && keys != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    /* renamed from: lambda$showLanguageAlert$102$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3704lambda$showLanguageAlert$102$orgtelegramuiLaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda36(this, keys, infos, systemLang));
    }

    /* renamed from: lambda$showLanguageAlert$101$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3703lambda$showLanguageAlert$101$orgtelegramuiLaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
        this.englishLocaleStrings = keys;
        if (keys != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
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
            this.lockRunnable = new Runnable() {
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
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
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

    private void updateCurrentConnectionState(int account) {
        if (this.actionBarLayout != null) {
            String title = null;
            int titleId = 0;
            Runnable action = null;
            int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            this.currentConnectionState = connectionState;
            if (connectionState == 2) {
                title = "WaitingForNetwork";
                titleId = NUM;
            } else if (connectionState == 5) {
                title = "Updating";
                titleId = NUM;
            } else if (connectionState == 4) {
                title = "ConnectingToProxy";
                titleId = NUM;
            } else if (connectionState == 1) {
                title = "Connecting";
                titleId = NUM;
            }
            if (connectionState == 1 || connectionState == 4) {
                action = new LaunchActivity$$ExternalSyntheticLambda31(this);
            }
            this.actionBarLayout.setTitleOverlayText(title, titleId, action);
        }
    }

    /* renamed from: lambda$updateCurrentConnectionState$103$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3710x2dcba16c() {
        BaseFragment lastFragment = null;
        if (AndroidUtilities.isTablet()) {
            if (!layerFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = layerFragmentsStack;
                lastFragment = arrayList.get(arrayList.size() - 1);
            }
        } else if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
            lastFragment = arrayList2.get(arrayList2.size() - 1);
        }
        if (!(lastFragment instanceof ProxyListActivity) && !(lastFragment instanceof ProxySettingsActivity)) {
            m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ProxyListActivity());
        }
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BaseFragment lastFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                lastFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if ((lastFragment instanceof ChatActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat");
                } else if ((lastFragment instanceof GroupCreateFinalActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                } else if (lastFragment instanceof WallpapersListActivity) {
                    outState.putString("fragment", "wallpapers");
                } else if (lastFragment instanceof ProfileActivity) {
                    ProfileActivity profileActivity = (ProfileActivity) lastFragment;
                    if (profileActivity.isSettings()) {
                        outState.putString("fragment", "settings");
                    } else if (profileActivity.isChat() && args != null) {
                        outState.putBundle("args", args);
                        outState.putString("fragment", "chat_profile");
                    }
                } else if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "channel");
                }
                lastFragment.saveSelfArgs(outState);
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
                boolean cancel = false;
                if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    cancel = true ^ this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onBackPressed();
                }
                if (!cancel) {
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

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        this.visibleActionMode = mode;
        try {
            Menu menu = mode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (Build.VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(mode);
                this.layersActionBarLayout.onActionModeStarted(mode);
            }
        }
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (this.visibleActionMode == mode) {
            this.visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(mode);
                this.layersActionBarLayout.onActionModeFinished(mode);
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

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == 0 && (event.getKeyCode() == 24 || event.getKeyCode() == 25)) {
            boolean mute = true;
            if (VoIPService.getSharedInstance() != null) {
                if (Build.VERSION.SDK_INT >= 32) {
                    boolean oldValue = WebRtcAudioTrack.isSpeakerMuted();
                    AudioManager am = (AudioManager) getSystemService("audio");
                    if (!(am.getStreamVolume(0) == am.getStreamMinVolume(0) && event.getKeyCode() == 25)) {
                        mute = false;
                    }
                    WebRtcAudioTrack.setSpeakerMute(mute);
                    if (oldValue != WebRtcAudioTrack.isSpeakerMuted()) {
                        showVoiceChatTooltip(mute ? 42 : 43);
                    }
                }
            } else if (!mainFragmentsStack.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && event.getRepeatCount() == 0)) {
                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                BaseFragment fragment = arrayList.get(arrayList.size() - 1);
                if ((fragment instanceof ChatActivity) && ((ChatActivity) fragment).maybePlayVisibleVideo()) {
                    return true;
                }
                if (AndroidUtilities.isTablet() && !rightFragmentsStack.isEmpty()) {
                    ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                    BaseFragment fragment2 = arrayList2.get(arrayList2.size() - 1);
                    if ((fragment2 instanceof ChatActivity) && ((ChatActivity) fragment2).maybePlayVisibleVideo()) {
                        return true;
                    }
                }
            }
        }
        try {
            super.dispatchKeyEvent(event);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(keyCode, event);
                } else {
                    this.rightActionBarLayout.onKeyUp(keyCode, event);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(keyCode, event);
            } else if (!this.drawerLayoutContainer.isDrawerOpened()) {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            } else {
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            this.drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity) && !(fragment instanceof IntroActivity) && !(fragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
            if ((fragment instanceof DialogsActivity) && ((DialogsActivity) fragment).isMainDialogList() && layout != (actionBarLayout5 = this.actionBarLayout)) {
                actionBarLayout5.removeAllFragments();
                this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
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
            } else if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout6 = this.layersActionBarLayout;
                if (layout != actionBarLayout6) {
                    actionBarLayout6.setVisibility(0);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                    if (fragment instanceof LoginActivity) {
                        this.backgroundTablet.setVisibility(0);
                        this.shadowTabletSide.setVisibility(8);
                        this.shadowTablet.setBackgroundColor(0);
                    } else {
                        this.shadowTablet.setBackgroundColor(NUM);
                    }
                    this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                    return false;
                }
            } else {
                boolean result = this.tabletFullSize;
                if ((!result && layout == this.rightActionBarLayout) || (result && layout == this.actionBarLayout)) {
                    boolean result2 = (result && layout == (actionBarLayout2 = this.actionBarLayout) && actionBarLayout2.fragmentsStack.size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                            actionBarLayout7.removeFragmentFromStack(actionBarLayout7.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    if (!result2) {
                        this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false, false);
                    }
                    return result2;
                } else if (!result && layout != (actionBarLayout4 = this.rightActionBarLayout)) {
                    actionBarLayout4.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a2 = 0; a2 < this.layersActionBarLayout.fragmentsStack.size() - 1; a2 = (a2 - 1) + 1) {
                            ActionBarLayout actionBarLayout8 = this.layersActionBarLayout;
                            actionBarLayout8.removeFragmentFromStack(actionBarLayout8.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                } else if (!result || layout == (actionBarLayout3 = this.actionBarLayout)) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a3 = 0; a3 < this.layersActionBarLayout.fragmentsStack.size() - 1; a3 = (a3 - 1) + 1) {
                            ActionBarLayout actionBarLayout9 = this.layersActionBarLayout;
                            actionBarLayout9.removeFragmentFromStack(actionBarLayout9.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    ActionBarLayout actionBarLayout10 = this.actionBarLayout;
                    actionBarLayout10.presentFragment(fragment, actionBarLayout10.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    return false;
                } else {
                    actionBarLayout3.presentFragment(fragment, actionBarLayout3.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a4 = 0; a4 < this.layersActionBarLayout.fragmentsStack.size() - 1; a4 = (a4 - 1) + 1) {
                            ActionBarLayout actionBarLayout11 = this.layersActionBarLayout;
                            actionBarLayout11.removeFragmentFromStack(actionBarLayout11.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                }
            }
        } else {
            boolean allow = true;
            if ((fragment instanceof LoginActivity) || (fragment instanceof IntroActivity)) {
                if (mainFragmentsStack.size() == 0 || (mainFragmentsStack.get(0) instanceof IntroActivity)) {
                    allow = false;
                }
            } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
                allow = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        }
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        if (AndroidUtilities.isTablet()) {
            this.drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity) && !(fragment instanceof IntroActivity) && !(fragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
            if (fragment instanceof DialogsActivity) {
                if (((DialogsActivity) fragment).isMainDialogList() && layout != (actionBarLayout4 = this.actionBarLayout)) {
                    actionBarLayout4.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(fragment);
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
            } else if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout5 = this.layersActionBarLayout;
                if (layout != actionBarLayout5) {
                    actionBarLayout5.setVisibility(0);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                    if (fragment instanceof LoginActivity) {
                        this.backgroundTablet.setVisibility(0);
                        this.shadowTabletSide.setVisibility(8);
                        this.shadowTablet.setBackgroundColor(0);
                    } else {
                        this.shadowTablet.setBackgroundColor(NUM);
                    }
                    this.layersActionBarLayout.addFragmentToStack(fragment);
                    return false;
                }
            } else {
                boolean z = this.tabletFullSize;
                if (!z && layout != (actionBarLayout3 = this.rightActionBarLayout)) {
                    actionBarLayout3.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(fragment);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            ActionBarLayout actionBarLayout6 = this.layersActionBarLayout;
                            actionBarLayout6.removeFragmentFromStack(actionBarLayout6.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return false;
                } else if (z && layout != (actionBarLayout2 = this.actionBarLayout)) {
                    actionBarLayout2.addFragmentToStack(fragment);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a2 = 0; a2 < this.layersActionBarLayout.fragmentsStack.size() - 1; a2 = (a2 - 1) + 1) {
                            ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                            actionBarLayout7.removeFragmentFromStack(actionBarLayout7.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return false;
                }
            }
        } else {
            boolean allow = true;
            if ((fragment instanceof LoginActivity) || (fragment instanceof IntroActivity)) {
                if (mainFragmentsStack.size() == 0 || (mainFragmentsStack.get(0) instanceof IntroActivity)) {
                    allow = false;
                }
            } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
                allow = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        }
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (layout.fragmentsStack.size() >= 2 && !(layout.fragmentsStack.get(0) instanceof LoginActivity)) {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean last) {
        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.rebuildAllFragmentViews(last, last);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }

    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(last, last);
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }
}
