package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import org.telegram.messenger.camera.CameraController;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
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
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.WallpapersListActivity;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final String EXTRA_ACTION_TOKEN = "actions.fulfillment.extra.ACTION_TOKEN";
    private static final int PLAY_SERVICES_REQUEST_CHECK_SETTINGS = 140;
    public static final int SCREEN_CAPTURE_REQUEST_CODE = 520;
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private ArrayList<TLRPC.User> contactsToSend;
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
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<String> importingStickersEmoji;
    private String importingStickersSoftware;
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
    private String videoPath;
    private ActionMode visibleActionMode;
    private AlertDialog visibleDialog;
    private boolean wasMutedByAdminRaisedHand;

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r15) {
        /*
            r14 = this;
            java.lang.String r0 = "flyme"
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.content.res.Resources r1 = r14.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            org.telegram.messenger.AndroidUtilities.checkDisplaySize(r14, r1)
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r14.currentAccount = r1
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x00f4
            android.content.Intent r1 = r14.getIntent()
            r4 = 0
            if (r1 == 0) goto L_0x008d
            java.lang.String r5 = r1.getAction()
            if (r5 == 0) goto L_0x008d
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.SEND"
            boolean r5 = r6.equals(r5)
            if (r5 != 0) goto L_0x0086
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.SEND_MULTIPLE"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0046
            goto L_0x0086
        L_0x0046:
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.VIEW"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x008d
            android.net.Uri r5 = r1.getData()
            if (r5 == 0) goto L_0x008d
            java.lang.String r6 = r5.toString()
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
            boolean r7 = r6.startsWith(r7)
            if (r7 == 0) goto L_0x0081
            goto L_0x0083
        L_0x0081:
            r7 = 0
            goto L_0x0084
        L_0x0083:
            r7 = 1
        L_0x0084:
            r4 = r7
            goto L_0x008d
        L_0x0086:
            super.onCreate(r15)
            r14.finish()
            return
        L_0x008d:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r6 = "intro_crashed_time"
            r7 = 0
            long r9 = r5.getLong(r6, r7)
            if (r1 == 0) goto L_0x00a5
            java.lang.String r11 = "fromIntro"
            boolean r11 = r1.getBooleanExtra(r11, r3)
            if (r11 == 0) goto L_0x00a5
            r11 = 1
            goto L_0x00a6
        L_0x00a5:
            r11 = 0
        L_0x00a6:
            if (r11 == 0) goto L_0x00b3
            android.content.SharedPreferences$Editor r12 = r5.edit()
            android.content.SharedPreferences$Editor r6 = r12.putLong(r6, r7)
            r6.commit()
        L_0x00b3:
            if (r4 != 0) goto L_0x00f4
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r9 - r6
            long r6 = java.lang.Math.abs(r6)
            r12 = 120000(0x1d4c0, double:5.9288E-319)
            int r8 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r8 < 0) goto L_0x00f4
            if (r1 == 0) goto L_0x00f4
            if (r11 != 0) goto L_0x00f4
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "logininfo2"
            android.content.SharedPreferences r5 = r6.getSharedPreferences(r7, r3)
            java.util.Map r6 = r5.getAll()
            boolean r7 = r6.isEmpty()
            if (r7 == 0) goto L_0x00f4
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.ui.IntroActivity> r2 = org.telegram.ui.IntroActivity.class
            r0.<init>(r14, r2)
            android.net.Uri r2 = r1.getData()
            r0.setData(r2)
            r14.startActivity(r0)
            super.onCreate(r15)
            r14.finish()
            return
        L_0x00f4:
            r14.requestWindowFeature(r2)
            r1 = 2131689489(0x7f0var_, float:1.9007995E38)
            r14.setTheme(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 0
            r5 = 21
            if (r1 < r5) goto L_0x0120
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r6 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0116 }
            java.lang.String r7 = "actionBarDefault"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x0116 }
            r7 = r7 | r1
            r6.<init>(r4, r4, r7)     // Catch:{ Exception -> 0x0116 }
            r14.setTaskDescription(r6)     // Catch:{ Exception -> 0x0116 }
            goto L_0x0117
        L_0x0116:
            r6 = move-exception
        L_0x0117:
            android.view.Window r6 = r14.getWindow()     // Catch:{ Exception -> 0x011f }
            r6.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x011f }
            goto L_0x0120
        L_0x011f:
            r1 = move-exception
        L_0x0120:
            android.view.Window r1 = r14.getWindow()
            r6 = 2131166150(0x7var_c6, float:1.7946537E38)
            r1.setBackgroundDrawableResource(r6)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0144
            boolean r1 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x0144
            android.view.Window r1 = r14.getWindow()     // Catch:{ Exception -> 0x0140 }
            r6 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r6, r6)     // Catch:{ Exception -> 0x0140 }
            goto L_0x0144
        L_0x0140:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0144:
            super.onCreate(r15)
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r1 < r6) goto L_0x0153
            boolean r1 = r14.isInMultiWindowMode()
            org.telegram.messenger.AndroidUtilities.isInMultiwindow = r1
        L_0x0153:
            org.telegram.ui.ActionBar.Theme.createCommonChatResources()
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r14)
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 == 0) goto L_0x016f
            boolean r1 = org.telegram.messenger.SharedConfig.appLocked
            if (r1 == 0) goto L_0x016f
            long r7 = android.os.SystemClock.elapsedRealtime()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r1 = (int) r7
            org.telegram.messenger.SharedConfig.lastPauseTime = r1
        L_0x016f:
            org.telegram.messenger.AndroidUtilities.fillStatusBarHeight(r14)
            org.telegram.ui.LaunchActivity$1 r1 = new org.telegram.ui.LaunchActivity$1
            r1.<init>(r14)
            r14.actionBarLayout = r1
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r14)
            r14.frameLayout = r1
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r8 = -1
            r7.<init>(r8, r8)
            r14.setContentView(r1, r7)
            int r1 = android.os.Build.VERSION.SDK_INT
            r7 = 8
            if (r1 < r5) goto L_0x0199
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r14)
            r14.themeSwitchImageView = r1
            r1.setVisibility(r7)
        L_0x0199:
            org.telegram.ui.LaunchActivity$2 r1 = new org.telegram.ui.LaunchActivity$2
            r1.<init>(r14)
            r14.drawerLayoutContainer = r1
            java.lang.String r9 = "windowBackgroundWhite"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r1.setBehindKeyboardColor(r9)
            android.widget.FrameLayout r1 = r14.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r1.addView(r9, r11)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r5) goto L_0x01d3
            org.telegram.ui.LaunchActivity$3 r1 = new org.telegram.ui.LaunchActivity$3
            r1.<init>(r14)
            r14.themeSwitchSunView = r1
            android.widget.FrameLayout r5 = r14.frameLayout
            r9 = 48
            r11 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r5.addView(r1, r9)
            android.view.View r1 = r14.themeSwitchSunView
            r1.setVisibility(r7)
        L_0x01d3:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x02b6
            android.view.Window r1 = r14.getWindow()
            r5 = 16
            r1.setSoftInputMode(r5)
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r14)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r14.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r5.addView(r1, r9)
            org.telegram.ui.LaunchActivity$5 r5 = new org.telegram.ui.LaunchActivity$5
            r5.<init>(r14)
            r14.backgroundTablet = r5
            r5.setOccupyStatusBar(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r14.backgroundTablet
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r11 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r5.setBackgroundImage(r9, r11)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r14.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createRelative(r8, r8)
            r1.addView(r5, r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.actionBarLayout
            r1.addView(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r14)
            r14.rightActionBarLayout = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = rightFragmentsStack
            r5.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.rightActionBarLayout
            r5.setDelegate(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.rightActionBarLayout
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r14)
            r14.shadowTabletSide = r5
            r9 = 1076449908(0x40295274, float:2.6456575)
            r5.setBackgroundColor(r9)
            android.widget.FrameLayout r5 = r14.shadowTabletSide
            r1.addView(r5)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r14)
            r14.shadowTablet = r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x024f
            r9 = 8
            goto L_0x0250
        L_0x024f:
            r9 = 0
        L_0x0250:
            r5.setVisibility(r9)
            android.widget.FrameLayout r5 = r14.shadowTablet
            r9 = 2130706432(0x7var_, float:1.7014118E38)
            r5.setBackgroundColor(r9)
            android.widget.FrameLayout r5 = r14.shadowTablet
            r1.addView(r5)
            android.widget.FrameLayout r5 = r14.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda8
            r9.<init>(r14)
            r5.setOnTouchListener(r9)
            android.widget.FrameLayout r5 = r14.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r9 = org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7.INSTANCE
            r5.setOnClickListener(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = new org.telegram.ui.ActionBar.ActionBarLayout
            r5.<init>(r14)
            r14.layersActionBarLayout = r5
            r5.setRemoveActionBarExtraHeight(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            android.widget.FrameLayout r9 = r14.shadowTablet
            r5.setBackgroundView(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            r5.setUseAlphaAnimations(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            r9 = 2131165301(0x7var_, float:1.7944815E38)
            r5.setBackgroundResource(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            r5.init(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            r5.setDelegate(r14)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r5.setDrawerLayoutContainer(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x02ac
            goto L_0x02ad
        L_0x02ac:
            r7 = 0
        L_0x02ad:
            r5.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.layersActionBarLayout
            r1.addView(r5)
            goto L_0x02c2
        L_0x02b6:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r14.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r14.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r8, r8)
            r1.addView(r5, r7)
        L_0x02c2:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r14)
            r14.sideMenuContainer = r1
            org.telegram.ui.LaunchActivity$6 r1 = new org.telegram.ui.LaunchActivity$6
            r1.<init>(r14)
            r14.sideMenu = r1
            org.telegram.ui.Components.SideMenultItemAnimator r1 = new org.telegram.ui.Components.SideMenultItemAnimator
            org.telegram.ui.Components.RecyclerListView r5 = r14.sideMenu
            r1.<init>(r5)
            r14.itemAnimator = r1
            org.telegram.ui.Components.RecyclerListView r5 = r14.sideMenu
            r5.setItemAnimator(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r14.sideMenu
            java.lang.String r5 = "chats_menuBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setBackgroundColor(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r14.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            r5.<init>(r14, r2, r3)
            r1.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r14.sideMenu
            r1.setAllowItemsInteractionDuringAnimation(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r14.sideMenu
            org.telegram.ui.Adapters.DrawerLayoutAdapter r5 = new org.telegram.ui.Adapters.DrawerLayoutAdapter
            org.telegram.ui.Components.SideMenultItemAnimator r7 = r14.itemAnimator
            r5.<init>(r14, r7)
            r14.drawerLayoutAdapter = r5
            r1.setAdapter(r5)
            android.widget.FrameLayout r1 = r14.sideMenuContainer
            org.telegram.ui.Components.RecyclerListView r5 = r14.sideMenu
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r1.addView(r5, r7)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r14.drawerLayoutContainer
            android.widget.FrameLayout r5 = r14.sideMenuContainer
            r1.setDrawerLayout(r5)
            android.widget.FrameLayout r1 = r14.sideMenuContainer
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x0331
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0348
        L_0x0331:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r5.x
            int r10 = r5.y
            int r9 = java.lang.Math.min(r9, r10)
            r10 = 1113587712(0x42600000, float:56.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r7 = java.lang.Math.min(r7, r9)
        L_0x0348:
            r1.width = r7
            r1.height = r8
            android.widget.FrameLayout r7 = r14.sideMenuContainer
            r7.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r7 = r14.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
            r9.<init>(r14)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r9)
            androidx.recyclerview.widget.ItemTouchHelper r7 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$7 r9 = new org.telegram.ui.LaunchActivity$7
            r10 = 3
            r9.<init>(r10, r3)
            r7.<init>(r9)
            org.telegram.ui.Components.RecyclerListView r9 = r14.sideMenu
            r7.attachToRecyclerView(r9)
            org.telegram.ui.Components.RecyclerListView r9 = r14.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75
            r11.<init>(r14, r7)
            r9.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r11)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r11 = r14.actionBarLayout
            r9.setParentActionBarLayout(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r14.drawerLayoutContainer
            r9.setDrawerLayoutContainer(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = mainFragmentsStack
            r9.init(r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            r9.setDelegate(r14)
            org.telegram.ui.ActionBar.Theme.loadWallpaper()
            r14.checkCurrentAccount()
            int r9 = r14.currentAccount
            r14.updateCurrentConnectionState(r9)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r12[r3] = r14
            r9.postNotificationName(r11, r12)
            int r9 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9)
            int r9 = r9.getConnectionState()
            r14.currentConnectionState = r9
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needShowAlert
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.reloadInterface
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetPasscode
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.screenStateChanged
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.showBulletin
            r9.addObserver(r14, r11)
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r9.addObserver(r14, r11)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x0536
            int r9 = r14.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            boolean r9 = r9.isClientActivated()
            if (r9 != 0) goto L_0x044e
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r14.actionBarLayout
            org.telegram.ui.LoginActivity r9 = new org.telegram.ui.LoginActivity
            r9.<init>()
            r4.addFragmentToStack(r9)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r14.drawerLayoutContainer
            r4.setAllowOpenDrawer(r3, r3)
            goto L_0x0463
        L_0x044e:
            org.telegram.ui.DialogsActivity r9 = new org.telegram.ui.DialogsActivity
            r9.<init>(r4)
            r4 = r9
            org.telegram.ui.Components.RecyclerListView r9 = r14.sideMenu
            r4.setSideMenu(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            r9.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r9.setAllowOpenDrawer(r2, r3)
        L_0x0463:
            if (r15 == 0) goto L_0x0535
            java.lang.String r4 = "fragment"
            java.lang.String r4 = r15.getString(r4)     // Catch:{ Exception -> 0x0531 }
            if (r4 == 0) goto L_0x0535
            java.lang.String r9 = "args"
            android.os.Bundle r9 = r15.getBundle(r9)     // Catch:{ Exception -> 0x0531 }
            int r11 = r4.hashCode()     // Catch:{ Exception -> 0x0531 }
            switch(r11) {
                case -1529105743: goto L_0x04ad;
                case -1349522494: goto L_0x04a3;
                case 3052376: goto L_0x0499;
                case 98629247: goto L_0x048f;
                case 738950403: goto L_0x0485;
                case 1434631203: goto L_0x047b;
                default: goto L_0x047a;
            }     // Catch:{ Exception -> 0x0531 }
        L_0x047a:
            goto L_0x04b6
        L_0x047b:
            java.lang.String r10 = "settings"
            boolean r10 = r4.equals(r10)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x047a
            r8 = 1
            goto L_0x04b6
        L_0x0485:
            java.lang.String r11 = "channel"
            boolean r11 = r4.equals(r11)     // Catch:{ Exception -> 0x0531 }
            if (r11 == 0) goto L_0x047a
            r8 = 3
            goto L_0x04b6
        L_0x048f:
            java.lang.String r10 = "group"
            boolean r10 = r4.equals(r10)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x047a
            r8 = 2
            goto L_0x04b6
        L_0x0499:
            java.lang.String r10 = "chat"
            boolean r10 = r4.equals(r10)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x047a
            r8 = 0
            goto L_0x04b6
        L_0x04a3:
            java.lang.String r10 = "chat_profile"
            boolean r10 = r4.equals(r10)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x047a
            r8 = 4
            goto L_0x04b6
        L_0x04ad:
            java.lang.String r10 = "wallpapers"
            boolean r10 = r4.equals(r10)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x047a
            r8 = 5
        L_0x04b6:
            switch(r8) {
                case 0: goto L_0x051e;
                case 1: goto L_0x0503;
                case 2: goto L_0x04f0;
                case 3: goto L_0x04dd;
                case 4: goto L_0x04ca;
                case 5: goto L_0x04bb;
                default: goto L_0x04b9;
            }     // Catch:{ Exception -> 0x0531 }
        L_0x04b9:
            goto L_0x0535
        L_0x04bb:
            org.telegram.ui.WallpapersListActivity r8 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r3)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
            goto L_0x0535
        L_0x04ca:
            if (r9 == 0) goto L_0x0535
            org.telegram.ui.ProfileActivity r8 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r10 = r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x04dc
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
        L_0x04dc:
            goto L_0x0535
        L_0x04dd:
            if (r9 == 0) goto L_0x0535
            org.telegram.ui.ChannelCreateActivity r8 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r10 = r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x04ef
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
        L_0x04ef:
            goto L_0x0535
        L_0x04f0:
            if (r9 == 0) goto L_0x0535
            org.telegram.ui.GroupCreateFinalActivity r8 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r10 = r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x0502
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
        L_0x0502:
            goto L_0x0535
        L_0x0503:
            java.lang.String r8 = "user_id"
            int r10 = r14.currentAccount     // Catch:{ Exception -> 0x0531 }
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)     // Catch:{ Exception -> 0x0531 }
            long r10 = r10.clientUserId     // Catch:{ Exception -> 0x0531 }
            r9.putLong(r8, r10)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ProfileActivity r8 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
            goto L_0x0535
        L_0x051e:
            if (r9 == 0) goto L_0x0535
            org.telegram.ui.ChatActivity r8 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x0531 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r14.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r10 = r10.addFragmentToStack(r8)     // Catch:{ Exception -> 0x0531 }
            if (r10 == 0) goto L_0x0530
            r8.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x0531 }
        L_0x0530:
            goto L_0x0535
        L_0x0531:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0535:
            goto L_0x059e
        L_0x0536:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            boolean r8 = r4 instanceof org.telegram.ui.DialogsActivity
            if (r8 == 0) goto L_0x054c
            r8 = r4
            org.telegram.ui.DialogsActivity r8 = (org.telegram.ui.DialogsActivity) r8
            org.telegram.ui.Components.RecyclerListView r9 = r14.sideMenu
            r8.setSideMenu(r9)
        L_0x054c:
            r8 = 1
            boolean r9 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r9 == 0) goto L_0x0582
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 > r2) goto L_0x0569
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x0569
            r9 = 1
            goto L_0x056a
        L_0x0569:
            r9 = 0
        L_0x056a:
            r8 = r9
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 != r2) goto L_0x0582
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r3)
            boolean r9 = r9 instanceof org.telegram.ui.LoginActivity
            if (r9 == 0) goto L_0x0582
            r8 = 0
        L_0x0582:
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            if (r9 != r2) goto L_0x0599
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r14.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r9.fragmentsStack
            java.lang.Object r9 = r9.get(r3)
            boolean r9 = r9 instanceof org.telegram.ui.LoginActivity
            if (r9 == 0) goto L_0x0599
            r8 = 0
        L_0x0599:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r9.setAllowOpenDrawer(r8, r3)
        L_0x059e:
            r14.checkLayout()
            r14.checkSystemBarColors()
            android.content.Intent r4 = r14.getIntent()
            if (r15 == 0) goto L_0x05ac
            r8 = 1
            goto L_0x05ad
        L_0x05ac:
            r8 = 0
        L_0x05ad:
            r14.handleIntent(r4, r3, r8, r3)
            java.lang.String r4 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x0615 }
            java.lang.String r8 = android.os.Build.USER     // Catch:{ Exception -> 0x0615 }
            java.lang.String r9 = ""
            if (r4 == 0) goto L_0x05be
            java.lang.String r10 = r4.toLowerCase()     // Catch:{ Exception -> 0x0615 }
            r4 = r10
            goto L_0x05bf
        L_0x05be:
            r4 = r9
        L_0x05bf:
            if (r8 == 0) goto L_0x05c7
            java.lang.String r9 = r4.toLowerCase()     // Catch:{ Exception -> 0x0615 }
            r8 = r9
            goto L_0x05c8
        L_0x05c7:
            r8 = r9
        L_0x05c8:
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0615 }
            if (r9 == 0) goto L_0x05e8
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0615 }
            r9.<init>()     // Catch:{ Exception -> 0x0615 }
            java.lang.String r10 = "OS name "
            r9.append(r10)     // Catch:{ Exception -> 0x0615 }
            r9.append(r4)     // Catch:{ Exception -> 0x0615 }
            java.lang.String r10 = " "
            r9.append(r10)     // Catch:{ Exception -> 0x0615 }
            r9.append(r8)     // Catch:{ Exception -> 0x0615 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0615 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x0615 }
        L_0x05e8:
            boolean r9 = r4.contains(r0)     // Catch:{ Exception -> 0x0615 }
            if (r9 != 0) goto L_0x05f4
            boolean r0 = r8.contains(r0)     // Catch:{ Exception -> 0x0615 }
            if (r0 == 0) goto L_0x0614
        L_0x05f4:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0615 }
            if (r0 > r6) goto L_0x0614
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x0615 }
            android.view.Window r0 = r14.getWindow()     // Catch:{ Exception -> 0x0615 }
            android.view.View r0 = r0.getDecorView()     // Catch:{ Exception -> 0x0615 }
            android.view.View r0 = r0.getRootView()     // Catch:{ Exception -> 0x0615 }
            android.view.ViewTreeObserver r6 = r0.getViewTreeObserver()     // Catch:{ Exception -> 0x0615 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9     // Catch:{ Exception -> 0x0615 }
            r9.<init>(r0)     // Catch:{ Exception -> 0x0615 }
            r14.onGlobalLayoutListener = r9     // Catch:{ Exception -> 0x0615 }
            r6.addOnGlobalLayoutListener(r9)     // Catch:{ Exception -> 0x0615 }
        L_0x0614:
            goto L_0x0619
        L_0x0615:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0619:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            r0.setBaseActivity(r14, r2)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r14)
            r14.updateAppUpdateViews(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    /* renamed from: lambda$onCreate$0$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ boolean m3084lambda$onCreate$0$orgtelegramuiLaunchActivity(View v, MotionEvent event) {
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

    /* renamed from: lambda$onCreate$2$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3085lambda$onCreate$2$orgtelegramuiLaunchActivity(View view, int position, float x, float y) {
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
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new LoginActivity(freeAccount));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(position);
            if (id == 2) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                args.putBoolean("allowSelf", false);
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ContactsActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                    m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ActionIntroActivity(0));
                    preferences.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle args2 = new Bundle();
                    args2.putInt("step", 0);
                    m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ChannelCreateActivity(args2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle args3 = new Bundle();
                args3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ChatActivity(args3));
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
                        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new PeopleNearbyActivity());
                    } else {
                        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 13) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ boolean m3086lambda$onCreate$3$orgtelegramuiLaunchActivity(ItemTouchHelper sideMenuTouchHelper, View view, int position) {
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

    static /* synthetic */ void lambda$onCreate$4(View view) {
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

    private void openSettings(boolean expanded) {
        Bundle args = new Bundle();
        args.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (expanded) {
            args.putBoolean("expandPhoto", true);
        }
        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ProfileActivity(args));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(true, true);
    }

    private void checkSystemBarColors(boolean checkStatusBar, boolean checkNavigationBar) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean z = true;
            if (checkStatusBar) {
                AndroidUtilities.setLightStatusBar(getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
            }
            if (Build.VERSION.SDK_INT >= 26 && checkNavigationBar) {
                Window window = getWindow();
                int color = Theme.getColor("windowBackgroundGray", (boolean[]) null, true);
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
        if (SharedConfig.noStatusBar != 0 && Build.VERSION.SDK_INT >= 21 && checkStatusBar) {
            getWindow().setStatusBarColor(0);
        }
    }

    public void switchToAccount(int account, boolean removeAll) {
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
            if (a >= 3) {
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
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
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
            AnonymousClass9 r0 = new BlockingUpdateView(this) {
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
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new LaunchActivity$10$$ExternalSyntheticLambda0(this)).start();
                }

                /* renamed from: lambda$onAcceptTerms$0$org-telegram-ui-LaunchActivity$10  reason: not valid java name */
                public /* synthetic */ void m3131lambda$onAcceptTerms$0$orgtelegramuiLaunchActivity$10() {
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
            this.passcodeView.onShow(fingerprint, animated, x, y, new LaunchActivity$$ExternalSyntheticLambda24(this, onShow), onStart);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda73(this));
        }
    }

    /* renamed from: lambda$showPasscodeActivity$5$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3127lambda$showPasscodeActivity$5$orgtelegramuiLaunchActivity(Runnable onShow) {
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

    /* renamed from: lambda$showPasscodeActivity$6$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3128lambda$showPasscodeActivity$6$orgtelegramuiLaunchActivity() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v279, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v159, resolved type: java.lang.String} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x202a, code lost:
        if (r6.checkCanOpenChat(r3, r9.get(r9.size() - 1)) != false) goto L_0x202c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0343, code lost:
        r15.exportingChatUri = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x016d, code lost:
        r3 = r52.getIntent().getExtras();
        r54 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0179, code lost:
        r56 = false;
        r55 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        r32 = r3.getLong("dialogId", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x018a, code lost:
        r3 = r3.getString("hash", (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x018c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1var_, code lost:
        if (r6.checkCanOpenChat(r11, r9.get(r9.size() - 1)) != false) goto L_0x1var_;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1173:0x25aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1175:0x25c5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1205:0x2686  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x03d3  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0494 A[Catch:{ Exception -> 0x048c }] */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0602  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0611  */
    /* JADX WARNING: Removed duplicated region for block: B:800:0x18d6  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01f8 A[SYNTHETIC, Splitter:B:84:0x01f8] */
    /* JADX WARNING: Removed duplicated region for block: B:917:0x1bb2 A[SYNTHETIC, Splitter:B:917:0x1bb2] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0231  */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1ee9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r95, boolean r96, boolean r97, boolean r98) {
        /*
            r94 = this;
            r15 = r94
            r14 = r95
            r13 = r97
            boolean r0 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r94, r95)
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
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r12)
        L_0x0049:
            int r25 = r95.getFlags()
            java.lang.String r10 = r95.getAction()
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
            r26 = r0
            if (r98 != 0) goto L_0x00a4
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r12)
            if (r0 != 0) goto L_0x0082
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x007f
            goto L_0x0082
        L_0x007f:
            r8 = r96
            goto L_0x00a6
        L_0x0082:
            r2 = 1
            r3 = 0
            r4 = -1
            r5 = -1
            r6 = 0
            r7 = 0
            r1 = r94
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r11)
            if (r26 != 0) goto L_0x00a1
            r15.passcodeSaveIntent = r14
            r8 = r96
            r15.passcodeSaveIntentIsNew = r8
            r15.passcodeSaveIntentIsRestore = r13
            return r11
        L_0x00a1:
            r8 = r96
            goto L_0x00a6
        L_0x00a4:
            r8 = r96
        L_0x00a6:
            r27 = 0
            r1 = 0
            r3 = 0
            r28 = 0
            r5 = 0
            r6 = 0
            r29 = -1
            r30 = -1
            r31 = 0
            r32 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
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
            r38 = 0
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
            r0 = r25 & r0
            java.lang.String r11 = "message_id"
            r12 = 0
            if (r0 != 0) goto L_0x1eb3
            if (r14 == 0) goto L_0x1ea0
            java.lang.String r0 = r95.getAction()
            if (r0 == 0) goto L_0x1ea0
            if (r97 != 0) goto L_0x1ea0
            java.lang.String r0 = r95.getAction()
            java.lang.String r12 = "android.intent.action.SEND"
            boolean r0 = r12.equals(r0)
            java.lang.String r12 = "android.intent.extra.STREAM"
            java.lang.String r13 = "\n"
            r46 = r1
            java.lang.String r1 = "hash"
            java.lang.String r2 = ""
            if (r0 == 0) goto L_0x03ff
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x01e1
            if (r14 == 0) goto L_0x01e1
            android.os.Bundle r0 = r95.getExtras()
            if (r0 == 0) goto L_0x01e1
            android.os.Bundle r0 = r95.getExtras()
            r48 = r3
            java.lang.String r3 = "dialogId"
            r4 = r6
            r50 = r7
            r6 = 0
            long r32 = r0.getLong(r3, r6)
            r3 = 0
            int r0 = (r32 > r6 ? 1 : (r32 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x01c2
            android.os.Bundle r0 = r95.getExtras()     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = "android.intent.extra.shortcut.ID"
            java.lang.String r0 = r0.getString(r6)     // Catch:{ all -> 0x01b5 }
            if (r0 == 0) goto L_0x01aa
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01b5 }
            java.util.List r6 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r6)     // Catch:{ all -> 0x01b5 }
            r7 = 0
            int r51 = r6.size()     // Catch:{ all -> 0x01b5 }
            r52 = r51
        L_0x0155:
            r51 = r3
            r3 = r52
            if (r7 >= r3) goto L_0x01a1
            java.lang.Object r52 = r6.get(r7)     // Catch:{ all -> 0x019b }
            androidx.core.content.pm.ShortcutInfoCompat r52 = (androidx.core.content.pm.ShortcutInfoCompat) r52     // Catch:{ all -> 0x019b }
            r53 = r3
            java.lang.String r3 = r52.getId()     // Catch:{ all -> 0x019b }
            boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x019b }
            if (r3 == 0) goto L_0x018e
            android.content.Intent r3 = r52.getIntent()     // Catch:{ all -> 0x019b }
            android.os.Bundle r3 = r3.getExtras()     // Catch:{ all -> 0x019b }
            r54 = r0
            java.lang.String r0 = "dialogId"
            r56 = r4
            r55 = r5
            r4 = 0
            long r57 = r3.getLong(r0, r4)     // Catch:{ all -> 0x018c }
            r32 = r57
            r4 = 0
            java.lang.String r0 = r3.getString(r1, r4)     // Catch:{ all -> 0x018c }
            r3 = r0
            goto L_0x01b4
        L_0x018c:
            r0 = move-exception
            goto L_0x01bc
        L_0x018e:
            r54 = r0
            r56 = r4
            r55 = r5
            int r7 = r7 + 1
            r3 = r51
            r52 = r53
            goto L_0x0155
        L_0x019b:
            r0 = move-exception
            r56 = r4
            r55 = r5
            goto L_0x01bc
        L_0x01a1:
            r54 = r0
            r53 = r3
            r56 = r4
            r55 = r5
            goto L_0x01b2
        L_0x01aa:
            r54 = r0
            r51 = r3
            r56 = r4
            r55 = r5
        L_0x01b2:
            r3 = r51
        L_0x01b4:
            goto L_0x01d2
        L_0x01b5:
            r0 = move-exception
            r51 = r3
            r56 = r4
            r55 = r5
        L_0x01bc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r3 = r51
            goto L_0x01d2
        L_0x01c2:
            r51 = r3
            r56 = r4
            r55 = r5
            android.os.Bundle r0 = r95.getExtras()
            r3 = 0
            java.lang.String r0 = r0.getString(r1, r3)
            r3 = r0
        L_0x01d2:
            java.lang.String r0 = org.telegram.messenger.SharedConfig.directShareHash
            if (r0 == 0) goto L_0x01de
            java.lang.String r0 = org.telegram.messenger.SharedConfig.directShareHash
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L_0x01e9
        L_0x01de:
            r32 = 0
            goto L_0x01e9
        L_0x01e1:
            r48 = r3
            r55 = r5
            r56 = r6
            r50 = r7
        L_0x01e9:
            r1 = 0
            java.lang.String r3 = r95.getType()
            if (r3 == 0) goto L_0x0231
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0231
            android.os.Bundle r0 = r95.getExtras()     // Catch:{ Exception -> 0x022a }
            java.lang.Object r0 = r0.get(r12)     // Catch:{ Exception -> 0x022a }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x022a }
            if (r0 == 0) goto L_0x0228
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x022a }
            r4 = 0
            r5 = 0
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r2, r5, r4, r4)     // Catch:{ Exception -> 0x022a }
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x022a }
            int r2 = r2.size()     // Catch:{ Exception -> 0x022a }
            r4 = 5
            if (r2 <= r4) goto L_0x0225
            r2 = 0
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x022a }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x022a }
            r2.<init>()     // Catch:{ Exception -> 0x022a }
            r15.documentsUrisArray = r2     // Catch:{ Exception -> 0x022a }
            r2.add(r0)     // Catch:{ Exception -> 0x022a }
            r15.documentsMimeType = r3     // Catch:{ Exception -> 0x022a }
            goto L_0x022f
        L_0x0225:
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x022a }
            goto L_0x022f
        L_0x0228:
            r1 = 1
            goto L_0x022f
        L_0x022a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 1
        L_0x022f:
            goto L_0x03d1
        L_0x0231:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x0245
            java.lang.String r4 = "android.intent.extra.TEXT"
            java.lang.CharSequence r4 = r14.getCharSequenceExtra(r4)
            if (r4 == 0) goto L_0x0245
            java.lang.String r0 = r4.toString()
        L_0x0245:
            java.lang.String r4 = "android.intent.extra.SUBJECT"
            java.lang.String r4 = r14.getStringExtra(r4)
            boolean r5 = android.text.TextUtils.isEmpty(r0)
            if (r5 != 0) goto L_0x027d
            java.lang.String r5 = "http://"
            boolean r5 = r0.startsWith(r5)
            if (r5 != 0) goto L_0x0261
            java.lang.String r5 = "https://"
            boolean r5 = r0.startsWith(r5)
            if (r5 == 0) goto L_0x0279
        L_0x0261:
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 != 0) goto L_0x0279
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r13)
            r5.append(r0)
            java.lang.String r0 = r5.toString()
        L_0x0279:
            r15.sendingText = r0
            r5 = r0
            goto L_0x0286
        L_0x027d:
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 != 0) goto L_0x0285
            r15.sendingText = r4
        L_0x0285:
            r5 = r0
        L_0x0286:
            android.os.Parcelable r0 = r14.getParcelableExtra(r12)
            if (r0 == 0) goto L_0x03cc
            boolean r6 = r0 instanceof android.net.Uri
            if (r6 != 0) goto L_0x029a
            java.lang.String r6 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r6)
            r6 = r0
            goto L_0x029b
        L_0x029a:
            r6 = r0
        L_0x029b:
            r7 = r6
            android.net.Uri r7 = (android.net.Uri) r7
            if (r7 == 0) goto L_0x02a7
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r7)
            if (r0 == 0) goto L_0x02a7
            r1 = 1
        L_0x02a7:
            if (r1 != 0) goto L_0x03c7
            if (r7 == 0) goto L_0x03c7
            if (r3 == 0) goto L_0x02b5
            java.lang.String r0 = "image/"
            boolean r0 = r3.startsWith(r0)
            if (r0 != 0) goto L_0x02c5
        L_0x02b5:
            java.lang.String r0 = r7.toString()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r12 = ".jpg"
            boolean r0 = r0.endsWith(r12)
            if (r0 == 0) goto L_0x02e0
        L_0x02c5:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x02d0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x02d0:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r0 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r7
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r15.photoPathsArray
            r2.add(r0)
            r51 = r1
            goto L_0x03c9
        L_0x02e0:
            java.lang.String r12 = r7.toString()
            r44 = 0
            int r0 = (r32 > r44 ? 1 : (r32 == r44 ? 0 : -1))
            if (r0 != 0) goto L_0x0362
            if (r12 == 0) goto L_0x0362
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0304
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r13 = "export path = "
            r0.append(r13)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0304:
            r13 = 0
            r0 = r9[r13]
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.Set<java.lang.String> r13 = r0.exportUri
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r7)
            r51 = r1
            java.lang.String r1 = org.telegram.messenger.FileLoader.fixFileName(r0)
            java.util.Iterator r52 = r13.iterator()
        L_0x031b:
            boolean r0 = r52.hasNext()
            if (r0 == 0) goto L_0x034b
            java.lang.Object r0 = r52.next()
            r53 = r0
            java.lang.String r53 = (java.lang.String) r53
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r53)     // Catch:{ Exception -> 0x0346 }
            java.util.regex.Matcher r54 = r0.matcher(r12)     // Catch:{ Exception -> 0x0346 }
            boolean r54 = r54.find()     // Catch:{ Exception -> 0x0346 }
            if (r54 != 0) goto L_0x0343
            java.util.regex.Matcher r54 = r0.matcher(r1)     // Catch:{ Exception -> 0x0346 }
            boolean r54 = r54.find()     // Catch:{ Exception -> 0x0346 }
            if (r54 == 0) goto L_0x0342
            goto L_0x0343
        L_0x0342:
            goto L_0x034a
        L_0x0343:
            r15.exportingChatUri = r7     // Catch:{ Exception -> 0x0346 }
            goto L_0x034b
        L_0x0346:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x034a:
            goto L_0x031b
        L_0x034b:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x0364
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r12.startsWith(r0)
            if (r0 == 0) goto L_0x0364
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r12.endsWith(r0)
            if (r0 == 0) goto L_0x0364
            r15.exportingChatUri = r7
            goto L_0x0364
        L_0x0362:
            r51 = r1
        L_0x0364:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x03c9
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r7)
            boolean r1 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r1 != 0) goto L_0x0376
            java.lang.String r1 = "file"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r7, r1)
        L_0x0376:
            if (r0 == 0) goto L_0x03b4
            java.lang.String r1 = "file:"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0386
            java.lang.String r1 = "file://"
            java.lang.String r0 = r0.replace(r1, r2)
        L_0x0386:
            if (r3 == 0) goto L_0x0393
            java.lang.String r1 = "video/"
            boolean r1 = r3.startsWith(r1)
            if (r1 == 0) goto L_0x0393
            r15.videoPath = r0
            goto L_0x03c9
        L_0x0393:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            if (r1 != 0) goto L_0x03a5
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsPathsArray = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsOriginalPathsArray = r1
        L_0x03a5:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray
            r1.add(r0)
            java.util.ArrayList<java.lang.String> r1 = r15.documentsOriginalPathsArray
            java.lang.String r2 = r7.toString()
            r1.add(r2)
            goto L_0x03c9
        L_0x03b4:
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray
            if (r1 != 0) goto L_0x03bf
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r15.documentsUrisArray = r1
        L_0x03bf:
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray
            r1.add(r7)
            r15.documentsMimeType = r3
            goto L_0x03c9
        L_0x03c7:
            r51 = r1
        L_0x03c9:
            r1 = r51
            goto L_0x03d1
        L_0x03cc:
            java.lang.String r2 = r15.sendingText
            if (r2 != 0) goto L_0x03d1
            r1 = 1
        L_0x03d1:
            if (r1 == 0) goto L_0x03dd
            java.lang.String r0 = "Unsupported content"
            r2 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r2)
            r0.show()
        L_0x03dd:
            r6 = r11
            r8 = r14
            r7 = r15
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
            r42 = 0
            r11 = r10
            r10 = r9
            r9 = 0
            goto L_0x1edd
        L_0x03ff:
            r48 = r3
            r55 = r5
            r56 = r6
            r50 = r7
            java.lang.String r0 = r95.getAction()
            java.lang.String r3 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x043f
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r12)     // Catch:{ all -> 0x042a }
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
            r6 = r11
            r8 = r14
            r7 = r15
            r42 = 0
            r11 = r10
            r10 = r9
            r9 = 0
            goto L_0x1ec5
        L_0x043f:
            java.lang.String r0 = r95.getAction()
            java.lang.String r3 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0625
            r1 = 0
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r12)     // Catch:{ Exception -> 0x0608 }
            java.lang.String r3 = r95.getType()     // Catch:{ Exception -> 0x0608 }
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
            r51 = r1
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
            r51 = r1
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
            r51 = r1
            r1 = r0
            goto L_0x0518
        L_0x0515:
            r51 = r1
            r1 = r0
        L_0x0518:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x0533
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f8 }
            r0.<init>()     // Catch:{ Exception -> 0x05f8 }
            r52 = r4
            java.lang.String r4 = "export path = "
            r0.append(r4)     // Catch:{ Exception -> 0x05f8 }
            r0.append(r1)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05f8 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x05f8 }
            goto L_0x0535
        L_0x0533:
            r52 = r4
        L_0x0535:
            r44 = 0
            int r0 = (r32 > r44 ? 1 : (r32 == r44 ? 0 : -1))
            if (r0 != 0) goto L_0x05b0
            if (r1 == 0) goto L_0x05b0
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x05f8 }
            if (r0 != 0) goto L_0x05b0
            r4 = 0
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r12)     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x05f8 }
            r53 = r0
            java.util.Iterator r54 = r5.iterator()     // Catch:{ Exception -> 0x05f8 }
        L_0x0550:
            boolean r0 = r54.hasNext()     // Catch:{ Exception -> 0x05f8 }
            if (r0 == 0) goto L_0x0594
            java.lang.Object r0 = r54.next()     // Catch:{ Exception -> 0x05f8 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x05f8 }
            r57 = r0
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r57)     // Catch:{ Exception -> 0x0587 }
            java.util.regex.Matcher r58 = r0.matcher(r1)     // Catch:{ Exception -> 0x0587 }
            boolean r58 = r58.find()     // Catch:{ Exception -> 0x0587 }
            if (r58 != 0) goto L_0x057c
            r58 = r4
            r4 = r53
            java.util.regex.Matcher r53 = r0.matcher(r4)     // Catch:{ Exception -> 0x0585 }
            boolean r53 = r53.find()     // Catch:{ Exception -> 0x0585 }
            if (r53 == 0) goto L_0x057b
            goto L_0x0580
        L_0x057b:
            goto L_0x058f
        L_0x057c:
            r58 = r4
            r4 = r53
        L_0x0580:
            r15.exportingChatUri = r12     // Catch:{ Exception -> 0x0585 }
            r53 = 1
            goto L_0x059a
        L_0x0585:
            r0 = move-exception
            goto L_0x058c
        L_0x0587:
            r0 = move-exception
            r58 = r4
            r4 = r53
        L_0x058c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x05f8 }
        L_0x058f:
            r53 = r4
            r4 = r58
            goto L_0x0550
        L_0x0594:
            r58 = r4
            r4 = r53
            r53 = r58
        L_0x059a:
            if (r53 == 0) goto L_0x059d
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
            r1 = r51
            r4 = r52
            goto L_0x04e6
        L_0x05f8:
            r0 = move-exception
            goto L_0x060b
        L_0x05fa:
            r51 = r1
            r52 = r4
        L_0x05ff:
            r1 = r51
            goto L_0x0607
        L_0x0602:
            r51 = r1
            r52 = r4
            r1 = 1
        L_0x0607:
            goto L_0x060f
        L_0x0608:
            r0 = move-exception
            r51 = r1
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
            r6 = r11
            r8 = r14
            r7 = r15
            r42 = 0
            r11 = r10
            r10 = r9
            r9 = 0
            goto L_0x1ec5
        L_0x0625:
            java.lang.String r0 = r95.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x1ced
            android.net.Uri r3 = r95.getData()
            if (r3 == 0) goto L_0x1cbd
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r12 = 0
            r51 = 0
            r52 = 0
            r53 = 0
            r54 = 0
            r57 = 0
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
            r69 = -1
            r70 = 0
            r71 = r12
            java.lang.String r12 = r3.getScheme()
            r72 = r4
            java.lang.String r4 = "actions.fulfillment.extra.ACTION_TOKEN"
            r73 = r5
            java.lang.String r5 = "phone"
            if (r12 == 0) goto L_0x1a40
            int r0 = r12.hashCode()
            r74 = r6
            switch(r0) {
                case 3699: goto L_0x068e;
                case 3213448: goto L_0x0684;
                case 99617003: goto L_0x067a;
                default: goto L_0x0679;
            }
        L_0x0679:
            goto L_0x0698
        L_0x067a:
            java.lang.String r0 = "https"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0679
            r0 = 1
            goto L_0x0699
        L_0x0684:
            java.lang.String r0 = "http"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0679
            r0 = 0
            goto L_0x0699
        L_0x068e:
            java.lang.String r0 = "tg"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0679
            r0 = 2
            goto L_0x0699
        L_0x0698:
            r0 = -1
        L_0x0699:
            java.lang.String r6 = "thread"
            r76 = r7
            r78 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r0) {
                case 0: goto L_0x153b;
                case 1: goto L_0x153b;
                case 2: goto L_0x06ad;
                default: goto L_0x06a2;
            }
        L_0x06a2:
            r82 = r9
            r81 = r10
            r10 = 2
            r43 = 6
            r44 = 0
            goto L_0x1a4d
        L_0x06ad:
            java.lang.String r0 = r3.toString()
            java.lang.String r7 = "tg:resolve"
            boolean r7 = r0.startsWith(r7)
            java.lang.String r8 = "payload"
            r81 = r10
            java.lang.String r10 = "scope"
            r82 = r9
            java.lang.String r9 = "tg://telegram.org"
            if (r7 != 0) goto L_0x13a7
            java.lang.String r7 = "tg://resolve"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x06cd
            goto L_0x13a7
        L_0x06cd:
            java.lang.String r7 = "tg:privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x12bf
            java.lang.String r7 = "tg://privatepost"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x06df
            goto L_0x12bf
        L_0x06df:
            java.lang.String r6 = "tg:bg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x1069
            java.lang.String r6 = "tg://bg"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x06f1
            goto L_0x1069
        L_0x06f1:
            java.lang.String r6 = "tg:join"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x1010
            java.lang.String r6 = "tg://join"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0703
            goto L_0x1010
        L_0x0703:
            java.lang.String r6 = "tg:addstickers"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0fb7
            java.lang.String r6 = "tg://addstickers"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0715
            goto L_0x0fb7
        L_0x0715:
            java.lang.String r6 = "tg:msg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0efc
            java.lang.String r6 = "tg://msg"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0efc
            java.lang.String r6 = "tg://share"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0efc
            java.lang.String r6 = "tg:share"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0737
            goto L_0x0efc
        L_0x0737:
            java.lang.String r6 = "tg:confirmphone"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0ea3
            java.lang.String r6 = "tg://confirmphone"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0749
            goto L_0x0ea3
        L_0x0749:
            java.lang.String r6 = "tg:login"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0e2b
            java.lang.String r6 = "tg://login"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x075b
            goto L_0x0e2b
        L_0x075b:
            java.lang.String r6 = "tg:openmessage"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0da9
            java.lang.String r6 = "tg://openmessage"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x076d
            goto L_0x0da9
        L_0x076d:
            java.lang.String r6 = "tg:passport"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0cfa
            java.lang.String r6 = "tg://passport"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0cfa
            java.lang.String r6 = "tg:secureid"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0787
            goto L_0x0cfa
        L_0x0787:
            java.lang.String r6 = "tg:setlanguage"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0ca3
            java.lang.String r6 = "tg://setlanguage"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0799
            goto L_0x0ca3
        L_0x0799:
            java.lang.String r6 = "tg:addtheme"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0c4c
            java.lang.String r6 = "tg://addtheme"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x07ab
            goto L_0x0c4c
        L_0x07ab:
            java.lang.String r6 = "tg:settings"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0ad8
            java.lang.String r6 = "tg://settings"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x07bd
            goto L_0x0ad8
        L_0x07bd:
            java.lang.String r6 = "tg:search"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0a38
            java.lang.String r6 = "tg://search"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x07cf
            goto L_0x0a38
        L_0x07cf:
            java.lang.String r6 = "tg:calllog"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x09f4
            java.lang.String r6 = "tg://calllog"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x07e1
            goto L_0x09f4
        L_0x07e1:
            java.lang.String r6 = "tg:call"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0917
            java.lang.String r6 = "tg://call"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x07f3
            goto L_0x0917
        L_0x07f3:
            java.lang.String r6 = "tg:scanqr"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x08d4
            java.lang.String r6 = "tg://scanqr"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0805
            goto L_0x08d4
        L_0x0805:
            java.lang.String r6 = "tg:addcontact"
            boolean r6 = r0.startsWith(r6)
            if (r6 != 0) goto L_0x0877
            java.lang.String r6 = "tg://addcontact"
            boolean r6 = r0.startsWith(r6)
            if (r6 == 0) goto L_0x0817
            goto L_0x0877
        L_0x0817:
            java.lang.String r6 = "tg://"
            java.lang.String r6 = r0.replace(r6, r2)
            java.lang.String r7 = "tg:"
            java.lang.String r2 = r6.replace(r7, r2)
            r6 = 63
            int r6 = r2.indexOf(r6)
            r7 = r6
            if (r6 < 0) goto L_0x0834
            r6 = 0
            java.lang.String r2 = r2.substring(r6, r7)
            r51 = r2
            goto L_0x0836
        L_0x0834:
            r51 = r2
        L_0x0836:
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0877:
            java.lang.String r2 = "tg:addcontact"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://addcontact"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "name"
            java.lang.String r24 = r3.getQueryParameter(r2)
            java.lang.String r38 = r3.getQueryParameter(r5)
            r19 = 1
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x08d4:
            r21 = 1
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0917:
            int r6 = r15.currentAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            boolean r6 = r6.isClientActivated()
            if (r6 == 0) goto L_0x09ed
            java.lang.String r6 = "extra_force_call"
            int r7 = r15.currentAccount
            org.telegram.messenger.ContactsController r7 = org.telegram.messenger.ContactsController.getInstance(r7)
            boolean r7 = r7.contactsLoaded
            if (r7 != 0) goto L_0x0951
            java.lang.String r7 = "extra_force_call"
            boolean r7 = r14.hasExtra(r7)
            if (r7 == 0) goto L_0x0938
            goto L_0x0951
        L_0x0938:
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r14)
            r2.removeExtra(r4)
            java.lang.String r7 = "extra_force_call"
            r8 = 1
            r2.putExtra(r7, r8)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda48 r7 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda48
            r7.<init>(r15, r2)
            r8 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r7, r8)
            goto L_0x09ac
        L_0x0951:
            java.lang.String r7 = "format"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = "name"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.String r9 = r3.getQueryParameter(r5)
            r10 = 0
            java.util.List r13 = r15.findContacts(r8, r9, r10)
            boolean r10 = r13.isEmpty()
            if (r10 == 0) goto L_0x0979
            if (r9 == 0) goto L_0x0979
            r2 = r8
            r10 = r9
            r20 = 1
            r24 = r2
            r75 = r6
            r38 = r10
            goto L_0x09ab
        L_0x0979:
            int r10 = r13.size()
            r75 = r6
            r6 = 1
            if (r10 != r6) goto L_0x098f
            r6 = 0
            java.lang.Object r10 = r13.get(r6)
            org.telegram.tgnet.TLRPC$TL_contact r10 = (org.telegram.tgnet.TLRPC.TL_contact) r10
            r6 = r9
            long r9 = r10.user_id
            r46 = r9
            goto L_0x0990
        L_0x098f:
            r6 = r9
        L_0x0990:
            r9 = 0
            int r77 = (r46 > r9 ? 1 : (r46 == r9 ? 0 : -1))
            if (r77 != 0) goto L_0x099b
            if (r8 == 0) goto L_0x0999
            r2 = r8
        L_0x0999:
            r23 = r2
        L_0x099b:
            java.lang.String r2 = "video"
            boolean r2 = r2.equalsIgnoreCase(r7)
            if (r2 == 0) goto L_0x09a6
            r17 = 1
            goto L_0x09a8
        L_0x09a6:
            r16 = 1
        L_0x09a8:
            r2 = 1
            r18 = r2
        L_0x09ab:
        L_0x09ac:
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x09ed:
            r10 = 2
            r43 = 6
            r44 = 0
            goto L_0x1a4d
        L_0x09f4:
            r7 = 1
            r50 = r7
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0a38:
            java.lang.String r2 = "tg:search"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://search"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "query"
            java.lang.String r2 = r3.getQueryParameter(r2)
            if (r2 == 0) goto L_0x0a95
            java.lang.String r22 = r2.trim()
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0a95:
            java.lang.String r22 = ""
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0ad8:
            java.lang.String r2 = "themes"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0b24
            r6 = 2
            r56 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0b24:
            java.lang.String r2 = "devices"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0b70
            r6 = 3
            r56 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0b70:
            java.lang.String r2 = "folders"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0bbc
            r6 = 4
            r56 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0bbc:
            java.lang.String r2 = "change_number"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0CLASSNAME
            r6 = 5
            r56 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0CLASSNAME:
            r6 = 1
            r56 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0c4c:
            java.lang.String r2 = "tg:addtheme"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://addtheme"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "slug"
            java.lang.String r62 = r3.getQueryParameter(r2)
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0ca3:
            java.lang.String r2 = "tg:setlanguage"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://setlanguage"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "lang"
            java.lang.String r61 = r3.getQueryParameter(r2)
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0cfa:
            java.lang.String r2 = "tg:passport"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://passport"
            java.lang.String r2 = r2.replace(r6, r9)
            java.lang.String r6 = "tg:secureid"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.lang.String r6 = r3.getQueryParameter(r10)
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 != 0) goto L_0x0d3b
            java.lang.String r7 = "{"
            boolean r7 = r6.startsWith(r7)
            if (r7 == 0) goto L_0x0d3b
            java.lang.String r7 = "}"
            boolean r7 = r6.endsWith(r7)
            if (r7 == 0) goto L_0x0d3b
            java.lang.String r7 = "nonce"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = "nonce"
            r2.put(r8, r7)
            goto L_0x0d42
        L_0x0d3b:
            java.lang.String r7 = r3.getQueryParameter(r8)
            r2.put(r8, r7)
        L_0x0d42:
            java.lang.String r7 = "bot_id"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = "bot_id"
            r2.put(r8, r7)
            r2.put(r10, r6)
            java.lang.String r7 = "public_key"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = "public_key"
            r2.put(r8, r7)
            java.lang.String r7 = "callback_url"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.String r8 = "callback_url"
            r2.put(r8, r7)
            r71 = r2
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0da9:
            java.lang.String r2 = "tg:openmessage"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://openmessage"
            java.lang.String r2 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r2)
            java.lang.String r0 = "user_id"
            java.lang.String r6 = r3.getQueryParameter(r0)
            java.lang.String r0 = "chat_id"
            java.lang.String r7 = r3.getQueryParameter(r0)
            java.lang.String r8 = r3.getQueryParameter(r11)
            if (r6 == 0) goto L_0x0dd4
            long r9 = java.lang.Long.parseLong(r6)     // Catch:{ NumberFormatException -> 0x0dd2 }
            r46 = r9
            goto L_0x0dde
        L_0x0dd2:
            r0 = move-exception
            goto L_0x0dde
        L_0x0dd4:
            if (r7 == 0) goto L_0x0dde
            long r9 = java.lang.Long.parseLong(r7)     // Catch:{ NumberFormatException -> 0x0ddd }
            r48 = r9
            goto L_0x0dde
        L_0x0ddd:
            r0 = move-exception
        L_0x0dde:
            if (r8 == 0) goto L_0x0de6
            int r0 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x0de5 }
            goto L_0x0de8
        L_0x0de5:
            r0 = move-exception
        L_0x0de6:
            r0 = r55
        L_0x0de8:
            r55 = r0
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0e2b:
            java.lang.String r6 = "tg:login"
            java.lang.String r6 = r0.replace(r6, r9)
            java.lang.String r7 = "tg://login"
            java.lang.String r0 = r6.replace(r7, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r6 = "token"
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.String r7 = "code"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)
            int r7 = r7.intValue()
            if (r7 == 0) goto L_0x0e60
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r2)
            r8.append(r7)
            java.lang.String r63 = r8.toString()
        L_0x0e60:
            r73 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0ea3:
            java.lang.String r2 = "tg:confirmphone"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://confirmphone"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r57 = r3.getQueryParameter(r5)
            java.lang.String r60 = r3.getQueryParameter(r1)
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0efc:
            java.lang.String r2 = "tg:msg"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://msg"
            java.lang.String r2 = r2.replace(r6, r9)
            java.lang.String r6 = "tg://share"
            java.lang.String r2 = r2.replace(r6, r9)
            java.lang.String r6 = "tg:share"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "url"
            java.lang.String r2 = r3.getQueryParameter(r2)
            if (r2 != 0) goto L_0x0var_
            java.lang.String r2 = ""
        L_0x0var_:
            java.lang.String r6 = "text"
            java.lang.String r6 = r3.getQueryParameter(r6)
            if (r6 == 0) goto L_0x0var_
            int r6 = r2.length()
            if (r6 <= 0) goto L_0x0var_
            r70 = 1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            r6.append(r13)
            java.lang.String r2 = r6.toString()
        L_0x0var_:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            java.lang.String r7 = "text"
            java.lang.String r7 = r3.getQueryParameter(r7)
            r6.append(r7)
            java.lang.String r2 = r6.toString()
        L_0x0var_:
            int r6 = r2.length()
            r7 = 16384(0x4000, float:2.2959E-41)
            if (r6 <= r7) goto L_0x0var_
            r6 = 16384(0x4000, float:2.2959E-41)
            r7 = 0
            java.lang.String r2 = r2.substring(r7, r6)
            goto L_0x0var_
        L_0x0var_:
            r7 = 0
        L_0x0var_:
            boolean r6 = r2.endsWith(r13)
            if (r6 == 0) goto L_0x0var_
            int r6 = r2.length()
            r8 = 1
            int r6 = r6 - r8
            java.lang.String r2 = r2.substring(r7, r6)
            goto L_0x0var_
        L_0x0var_:
            r41 = r16
            r54 = r20
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x0fb7:
            java.lang.String r2 = "tg:addstickers"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://addstickers"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "set"
            java.lang.String r7 = r3.getQueryParameter(r2)
            r76 = r7
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x1010:
            java.lang.String r2 = "tg:join"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://join"
            java.lang.String r0 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "invite"
            java.lang.String r6 = r3.getQueryParameter(r2)
            r74 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x1069:
            java.lang.String r2 = "tg:bg"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r6 = "tg://bg"
            java.lang.String r2 = r2.replace(r6, r9)
            android.net.Uri r3 = android.net.Uri.parse(r2)
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r6 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r0 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r6.settings = r0
            java.lang.String r0 = "slug"
            java.lang.String r0 = r3.getQueryParameter(r0)
            r6.slug = r0
            java.lang.String r0 = r6.slug
            if (r0 != 0) goto L_0x109a
            java.lang.String r0 = "color"
            java.lang.String r0 = r3.getQueryParameter(r0)
            r6.slug = r0
        L_0x109a:
            r7 = 0
            java.lang.String r0 = r6.slug
            if (r0 == 0) goto L_0x10be
            java.lang.String r0 = r6.slug
            int r0 = r0.length()
            r8 = 6
            if (r0 != r8) goto L_0x10be
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x10bc }
            java.lang.String r8 = r6.slug     // Catch:{ Exception -> 0x10bc }
            r9 = 16
            int r8 = java.lang.Integer.parseInt(r8, r9)     // Catch:{ Exception -> 0x10bc }
            r8 = r8 | r78
            r0.background_color = r8     // Catch:{ Exception -> 0x10bc }
            r8 = 0
            r6.slug = r8     // Catch:{ Exception -> 0x10bc }
            r7 = 1
        L_0x10ba:
            goto L_0x117b
        L_0x10bc:
            r0 = move-exception
            goto L_0x10ba
        L_0x10be:
            java.lang.String r0 = r6.slug
            if (r0 == 0) goto L_0x117b
            java.lang.String r0 = r6.slug
            int r0 = r0.length()
            r8 = 13
            if (r0 < r8) goto L_0x117b
            java.lang.String r0 = r6.slug
            r8 = 6
            char r0 = r0.charAt(r8)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)
            if (r0 == 0) goto L_0x117b
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x117a }
            java.lang.String r9 = r6.slug     // Catch:{ Exception -> 0x117a }
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r8)     // Catch:{ Exception -> 0x117a }
            r8 = 16
            int r9 = java.lang.Integer.parseInt(r9, r8)     // Catch:{ Exception -> 0x117a }
            r8 = r9 | r78
            r0.background_color = r8     // Catch:{ Exception -> 0x117a }
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x117a }
            java.lang.String r8 = r6.slug     // Catch:{ Exception -> 0x117a }
            r9 = 7
            r10 = 13
            java.lang.String r8 = r8.substring(r9, r10)     // Catch:{ Exception -> 0x117a }
            r9 = 16
            int r8 = java.lang.Integer.parseInt(r8, r9)     // Catch:{ Exception -> 0x117a }
            r8 = r8 | r78
            r0.second_background_color = r8     // Catch:{ Exception -> 0x117a }
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x117a }
            int r0 = r0.length()     // Catch:{ Exception -> 0x117a }
            r8 = 20
            if (r0 < r8) goto L_0x112f
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x117a }
            r8 = 13
            char r0 = r0.charAt(r8)     // Catch:{ Exception -> 0x117a }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x117a }
            if (r0 == 0) goto L_0x112f
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x117a }
            java.lang.String r8 = r6.slug     // Catch:{ Exception -> 0x117a }
            r9 = 14
            r10 = 20
            java.lang.String r8 = r8.substring(r9, r10)     // Catch:{ Exception -> 0x117a }
            r9 = 16
            int r8 = java.lang.Integer.parseInt(r8, r9)     // Catch:{ Exception -> 0x117a }
            r8 = r8 | r78
            r0.third_background_color = r8     // Catch:{ Exception -> 0x117a }
        L_0x112f:
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x117a }
            int r0 = r0.length()     // Catch:{ Exception -> 0x117a }
            r8 = 27
            if (r0 != r8) goto L_0x115b
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x117a }
            r8 = 20
            char r0 = r0.charAt(r8)     // Catch:{ Exception -> 0x117a }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x117a }
            if (r0 == 0) goto L_0x115b
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x117a }
            java.lang.String r8 = r6.slug     // Catch:{ Exception -> 0x117a }
            r9 = 21
            java.lang.String r8 = r8.substring(r9)     // Catch:{ Exception -> 0x117a }
            r9 = 16
            int r8 = java.lang.Integer.parseInt(r8, r9)     // Catch:{ Exception -> 0x117a }
            r8 = r8 | r78
            r0.fourth_background_color = r8     // Catch:{ Exception -> 0x117a }
        L_0x115b:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x1174 }
            boolean r8 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1174 }
            if (r8 != 0) goto L_0x1173
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r6.settings     // Catch:{ Exception -> 0x1174 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1174 }
            int r9 = r9.intValue()     // Catch:{ Exception -> 0x1174 }
            r8.rotation = r9     // Catch:{ Exception -> 0x1174 }
        L_0x1173:
            goto L_0x1175
        L_0x1174:
            r0 = move-exception
        L_0x1175:
            r8 = 0
            r6.slug = r8     // Catch:{ Exception -> 0x117a }
            r7 = 1
            goto L_0x117b
        L_0x117a:
            r0 = move-exception
        L_0x117b:
            if (r7 != 0) goto L_0x127c
            java.lang.String r0 = "mode"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 == 0) goto L_0x11bd
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r8 = " "
            java.lang.String[] r8 = r0.split(r8)
            if (r8 == 0) goto L_0x11bb
            int r9 = r8.length
            if (r9 <= 0) goto L_0x11bb
            r9 = 0
        L_0x1195:
            int r10 = r8.length
            if (r9 >= r10) goto L_0x11bb
            r10 = r8[r9]
            java.lang.String r13 = "blur"
            boolean r10 = r13.equals(r10)
            if (r10 == 0) goto L_0x11a8
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r6.settings
            r13 = 1
            r10.blur = r13
            goto L_0x11b8
        L_0x11a8:
            r13 = 1
            r10 = r8[r9]
            java.lang.String r13 = "motion"
            boolean r10 = r13.equals(r10)
            if (r10 == 0) goto L_0x11b8
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r6.settings
            r13 = 1
            r10.motion = r13
        L_0x11b8:
            int r9 = r9 + 1
            goto L_0x1195
        L_0x11bb:
            r8 = r0
            goto L_0x11be
        L_0x11bd:
            r8 = r0
        L_0x11be:
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings
            java.lang.String r9 = "intensity"
            java.lang.String r9 = r3.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r9 = r9.intValue()
            r0.intensity = r9
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x125e }
            boolean r9 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x125e }
            if (r9 != 0) goto L_0x125b
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r6.settings     // Catch:{ Exception -> 0x125e }
            r75 = r2
            r10 = 6
            r13 = 0
            java.lang.String r2 = r0.substring(r13, r10)     // Catch:{ Exception -> 0x1259 }
            r10 = 16
            int r2 = java.lang.Integer.parseInt(r2, r10)     // Catch:{ Exception -> 0x1259 }
            r2 = r2 | r78
            r9.background_color = r2     // Catch:{ Exception -> 0x1259 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x1259 }
            r9 = 13
            if (r2 < r9) goto L_0x125d
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x1259 }
            r10 = 8
            java.lang.String r10 = r0.substring(r10, r9)     // Catch:{ Exception -> 0x1259 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1259 }
            r9 = r10 | r78
            r2.second_background_color = r9     // Catch:{ Exception -> 0x1259 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x1259 }
            r9 = 20
            if (r2 < r9) goto L_0x1232
            r2 = 13
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x1259 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x1259 }
            if (r2 == 0) goto L_0x1232
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x1259 }
            r9 = 14
            r10 = 20
            java.lang.String r9 = r0.substring(r9, r10)     // Catch:{ Exception -> 0x1259 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1259 }
            r9 = r9 | r78
            r2.third_background_color = r9     // Catch:{ Exception -> 0x1259 }
        L_0x1232:
            int r2 = r0.length()     // Catch:{ Exception -> 0x1259 }
            r9 = 27
            if (r2 != r9) goto L_0x125d
            r2 = 20
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x1259 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x1259 }
            if (r2 == 0) goto L_0x125d
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x1259 }
            r9 = 21
            java.lang.String r9 = r0.substring(r9)     // Catch:{ Exception -> 0x1259 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1259 }
            r9 = r9 | r78
            r2.fourth_background_color = r9     // Catch:{ Exception -> 0x1259 }
            goto L_0x125d
        L_0x1259:
            r0 = move-exception
            goto L_0x1261
        L_0x125b:
            r75 = r2
        L_0x125d:
            goto L_0x1261
        L_0x125e:
            r0 = move-exception
            r75 = r2
        L_0x1261:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x127a }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x127a }
            if (r2 != 0) goto L_0x1279
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x127a }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x127a }
            int r9 = r9.intValue()     // Catch:{ Exception -> 0x127a }
            r2.rotation = r9     // Catch:{ Exception -> 0x127a }
        L_0x1279:
            goto L_0x127e
        L_0x127a:
            r0 = move-exception
            goto L_0x127e
        L_0x127c:
            r75 = r2
        L_0x127e:
            r41 = r16
            r64 = r53
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r70 = r6
            r53 = r19
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r38 = r3
            r51 = r17
            r52 = r18
            goto L_0x1a87
        L_0x12bf:
            java.lang.String r2 = "tg:privatepost"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r7 = "tg://privatepost"
            java.lang.String r0 = r2.replace(r7, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "post"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            java.lang.String r7 = "channel"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.Long r7 = org.telegram.messenger.Utilities.parseLong(r7)
            int r8 = r2.intValue()
            if (r8 == 0) goto L_0x12f9
            long r8 = r7.longValue()
            r44 = 0
            int r10 = (r8 > r44 ? 1 : (r8 == r44 ? 0 : -1))
            if (r10 != 0) goto L_0x12f4
            goto L_0x12f9
        L_0x12f4:
            r65 = r2
            r66 = r7
            goto L_0x12ff
        L_0x12f9:
            r2 = 0
            r7 = 0
            r65 = r2
            r66 = r7
        L_0x12ff:
            java.lang.String r2 = r3.getQueryParameter(r6)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r6 = r2.intValue()
            if (r6 != 0) goto L_0x1311
            r2 = 0
            r67 = r2
            goto L_0x1313
        L_0x1311:
            r67 = r2
        L_0x1313:
            java.lang.String r2 = "comment"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.Integer r68 = org.telegram.messenger.Utilities.parseInt(r2)
            int r2 = r68.intValue()
            if (r2 != 0) goto L_0x1366
            r68 = 0
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x1366:
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x13a7:
            java.lang.String r2 = "tg:resolve"
            java.lang.String r2 = r0.replace(r2, r9)
            java.lang.String r7 = "tg://resolve"
            java.lang.String r0 = r2.replace(r7, r9)
            android.net.Uri r3 = android.net.Uri.parse(r0)
            java.lang.String r2 = "domain"
            java.lang.String r2 = r3.getQueryParameter(r2)
            java.lang.String r7 = "telegrampassport"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x1461
            r2 = 0
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.lang.String r7 = r3.getQueryParameter(r10)
            boolean r9 = android.text.TextUtils.isEmpty(r7)
            if (r9 != 0) goto L_0x13f1
            java.lang.String r9 = "{"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x13f1
            java.lang.String r9 = "}"
            boolean r9 = r7.endsWith(r9)
            if (r9 == 0) goto L_0x13f1
            java.lang.String r8 = "nonce"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.String r9 = "nonce"
            r6.put(r9, r8)
            goto L_0x13f8
        L_0x13f1:
            java.lang.String r9 = r3.getQueryParameter(r8)
            r6.put(r8, r9)
        L_0x13f8:
            java.lang.String r8 = "bot_id"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.String r9 = "bot_id"
            r6.put(r9, r8)
            r6.put(r10, r7)
            java.lang.String r8 = "public_key"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.String r9 = "public_key"
            r6.put(r9, r8)
            java.lang.String r8 = "callback_url"
            java.lang.String r8 = r3.getQueryParameter(r8)
            java.lang.String r9 = "callback_url"
            r6.put(r9, r8)
            r72 = r2
            r71 = r6
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x1461:
            java.lang.String r7 = "start"
            java.lang.String r52 = r3.getQueryParameter(r7)
            java.lang.String r7 = "startgroup"
            java.lang.String r53 = r3.getQueryParameter(r7)
            java.lang.String r7 = "game"
            java.lang.String r58 = r3.getQueryParameter(r7)
            java.lang.String r7 = "voicechat"
            java.lang.String r59 = r3.getQueryParameter(r7)
            java.lang.String r7 = "post"
            java.lang.String r7 = r3.getQueryParameter(r7)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)
            int r8 = r7.intValue()
            if (r8 != 0) goto L_0x148d
            r7 = 0
            r65 = r7
            goto L_0x148f
        L_0x148d:
            r65 = r7
        L_0x148f:
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
            int r7 = r6.intValue()
            if (r7 != 0) goto L_0x14a1
            r6 = 0
            r67 = r6
            goto L_0x14a3
        L_0x14a1:
            r67 = r6
        L_0x14a3:
            java.lang.String r6 = "comment"
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.Integer r68 = org.telegram.messenger.Utilities.parseInt(r6)
            int r6 = r68.intValue()
            if (r6 != 0) goto L_0x14f8
            r68 = 0
            r72 = r2
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x14f8:
            r72 = r2
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r10 = 2
            r43 = 6
            r44 = 0
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x153b:
            r82 = r9
            r81 = r10
            java.lang.String r0 = r3.getHost()
            java.lang.String r7 = r0.toLowerCase()
            java.lang.String r0 = "telegram.me"
            boolean r0 = r7.equals(r0)
            if (r0 != 0) goto L_0x1567
            java.lang.String r0 = "t.me"
            boolean r0 = r7.equals(r0)
            if (r0 != 0) goto L_0x1567
            java.lang.String r0 = "telegram.dog"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x1560
            goto L_0x1567
        L_0x1560:
            r10 = 2
            r43 = 6
            r44 = 0
            goto L_0x1a4d
        L_0x1567:
            java.lang.String r0 = r3.getPath()
            if (r0 == 0) goto L_0x19f6
            int r8 = r0.length()
            r9 = 1
            if (r8 <= r9) goto L_0x19f6
            java.lang.String r8 = r0.substring(r9)
            java.lang.String r0 = "bg/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x17b2
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r6 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r0 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r6.settings = r0
            java.lang.String r0 = "bg/"
            java.lang.String r0 = r8.replace(r0, r2)
            r6.slug = r0
            r2 = 0
            java.lang.String r0 = r6.slug
            if (r0 == 0) goto L_0x15ba
            java.lang.String r0 = r6.slug
            int r0 = r0.length()
            r9 = 6
            if (r0 != r9) goto L_0x15ba
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x15b8 }
            java.lang.String r9 = r6.slug     // Catch:{ Exception -> 0x15b8 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x15b8 }
            r9 = r9 | r78
            r0.background_color = r9     // Catch:{ Exception -> 0x15b8 }
            r9 = 0
            r6.slug = r9     // Catch:{ Exception -> 0x15b8 }
            r2 = 1
        L_0x15b5:
            r10 = 0
            goto L_0x167c
        L_0x15b8:
            r0 = move-exception
            goto L_0x15b5
        L_0x15ba:
            java.lang.String r0 = r6.slug
            if (r0 == 0) goto L_0x167b
            java.lang.String r0 = r6.slug
            int r0 = r0.length()
            r9 = 13
            if (r0 < r9) goto L_0x167b
            java.lang.String r0 = r6.slug
            r9 = 6
            char r0 = r0.charAt(r9)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)
            if (r0 == 0) goto L_0x167b
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x1678 }
            java.lang.String r10 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r13 = 0
            java.lang.String r10 = r10.substring(r13, r9)     // Catch:{ Exception -> 0x1678 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x1678 }
            r9 = r10 | r78
            r0.background_color = r9     // Catch:{ Exception -> 0x1678 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x1678 }
            java.lang.String r9 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r10 = 7
            r13 = 13
            java.lang.String r9 = r9.substring(r10, r13)     // Catch:{ Exception -> 0x1678 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1678 }
            r9 = r9 | r78
            r0.second_background_color = r9     // Catch:{ Exception -> 0x1678 }
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x1678 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1678 }
            r9 = 20
            if (r0 < r9) goto L_0x162b
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r9 = 13
            char r0 = r0.charAt(r9)     // Catch:{ Exception -> 0x1678 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x1678 }
            if (r0 == 0) goto L_0x162b
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x1678 }
            java.lang.String r9 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r10 = 14
            r13 = 20
            java.lang.String r9 = r9.substring(r10, r13)     // Catch:{ Exception -> 0x1678 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1678 }
            r9 = r9 | r78
            r0.third_background_color = r9     // Catch:{ Exception -> 0x1678 }
        L_0x162b:
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x1678 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1678 }
            r9 = 27
            if (r0 != r9) goto L_0x1657
            java.lang.String r0 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r9 = 20
            char r0 = r0.charAt(r9)     // Catch:{ Exception -> 0x1678 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x1678 }
            if (r0 == 0) goto L_0x1657
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings     // Catch:{ Exception -> 0x1678 }
            java.lang.String r9 = r6.slug     // Catch:{ Exception -> 0x1678 }
            r10 = 21
            java.lang.String r9 = r9.substring(r10)     // Catch:{ Exception -> 0x1678 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ Exception -> 0x1678 }
            r9 = r9 | r78
            r0.fourth_background_color = r9     // Catch:{ Exception -> 0x1678 }
        L_0x1657:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x1670 }
            boolean r9 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x1670 }
            if (r9 != 0) goto L_0x166f
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r6.settings     // Catch:{ Exception -> 0x1670 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x1670 }
            int r10 = r10.intValue()     // Catch:{ Exception -> 0x1670 }
            r9.rotation = r10     // Catch:{ Exception -> 0x1670 }
        L_0x166f:
            goto L_0x1671
        L_0x1670:
            r0 = move-exception
        L_0x1671:
            r10 = 0
            r6.slug = r10     // Catch:{ Exception -> 0x1676 }
            r2 = 1
            goto L_0x167c
        L_0x1676:
            r0 = move-exception
            goto L_0x167c
        L_0x1678:
            r0 = move-exception
            r10 = 0
            goto L_0x167c
        L_0x167b:
            r10 = 0
        L_0x167c:
            if (r2 != 0) goto L_0x17a0
            java.lang.String r0 = "mode"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 == 0) goto L_0x16c7
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r9 = " "
            java.lang.String[] r9 = r0.split(r9)
            if (r9 == 0) goto L_0x16c4
            int r13 = r9.length
            if (r13 <= 0) goto L_0x16c4
            r13 = 0
        L_0x1696:
            int r10 = r9.length
            if (r13 >= r10) goto L_0x16c1
            r10 = r9[r13]
            r64 = r0
            java.lang.String r0 = "blur"
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x16ab
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings
            r10 = 1
            r0.blur = r10
            goto L_0x16bb
        L_0x16ab:
            r10 = 1
            r0 = r9[r13]
            java.lang.String r10 = "motion"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x16bb
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings
            r10 = 1
            r0.motion = r10
        L_0x16bb:
            int r13 = r13 + 1
            r0 = r64
            r10 = 0
            goto L_0x1696
        L_0x16c1:
            r64 = r0
            goto L_0x16c9
        L_0x16c4:
            r64 = r0
            goto L_0x16c9
        L_0x16c7:
            r64 = r0
        L_0x16c9:
            java.lang.String r0 = "intensity"
            java.lang.String r9 = r3.getQueryParameter(r0)
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            if (r0 != 0) goto L_0x16e2
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r9)
            int r10 = r10.intValue()
            r0.intensity = r10
            goto L_0x16e8
        L_0x16e2:
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r6.settings
            r10 = 50
            r0.intensity = r10
        L_0x16e8:
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x177f }
            boolean r10 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x177f }
            if (r10 != 0) goto L_0x1772
            org.telegram.tgnet.TLRPC$WallPaperSettings r10 = r6.settings     // Catch:{ Exception -> 0x177f }
            r43 = r2
            r83 = r7
            r2 = 0
            r13 = 6
            java.lang.String r7 = r0.substring(r2, r13)     // Catch:{ Exception -> 0x177d }
            r2 = 16
            int r7 = java.lang.Integer.parseInt(r7, r2)     // Catch:{ Exception -> 0x177d }
            r2 = r7 | r78
            r10.background_color = r2     // Catch:{ Exception -> 0x177d }
            int r2 = r0.length()     // Catch:{ Exception -> 0x177d }
            r7 = 13
            if (r2 < r7) goto L_0x177c
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x177d }
            r10 = 7
            java.lang.String r10 = r0.substring(r10, r7)     // Catch:{ Exception -> 0x177d }
            r7 = 16
            int r10 = java.lang.Integer.parseInt(r10, r7)     // Catch:{ Exception -> 0x177d }
            r7 = r10 | r78
            r2.second_background_color = r7     // Catch:{ Exception -> 0x177d }
            int r2 = r0.length()     // Catch:{ Exception -> 0x177d }
            r7 = 20
            if (r2 < r7) goto L_0x174b
            r2 = 13
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x177d }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x177d }
            if (r2 == 0) goto L_0x174b
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x177d }
            r7 = 14
            r10 = 20
            java.lang.String r7 = r0.substring(r7, r10)     // Catch:{ Exception -> 0x177d }
            r10 = 16
            int r7 = java.lang.Integer.parseInt(r7, r10)     // Catch:{ Exception -> 0x177d }
            r7 = r7 | r78
            r2.third_background_color = r7     // Catch:{ Exception -> 0x177d }
        L_0x174b:
            int r2 = r0.length()     // Catch:{ Exception -> 0x177d }
            r7 = 27
            if (r2 != r7) goto L_0x177c
            r2 = 20
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x177d }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x177d }
            if (r2 == 0) goto L_0x177c
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x177d }
            r7 = 21
            java.lang.String r7 = r0.substring(r7)     // Catch:{ Exception -> 0x177d }
            r10 = 16
            int r7 = java.lang.Integer.parseInt(r7, r10)     // Catch:{ Exception -> 0x177d }
            r7 = r7 | r78
            r2.fourth_background_color = r7     // Catch:{ Exception -> 0x177d }
            goto L_0x177c
        L_0x1772:
            r43 = r2
            r83 = r7
            r13 = 6
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x177d }
            r7 = -1
            r2.background_color = r7     // Catch:{ Exception -> 0x177d }
        L_0x177c:
            goto L_0x1785
        L_0x177d:
            r0 = move-exception
            goto L_0x1785
        L_0x177f:
            r0 = move-exception
            r43 = r2
            r83 = r7
            r13 = 6
        L_0x1785:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r3.getQueryParameter(r0)     // Catch:{ Exception -> 0x179e }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x179e }
            if (r2 != 0) goto L_0x179d
            org.telegram.tgnet.TLRPC$WallPaperSettings r2 = r6.settings     // Catch:{ Exception -> 0x179e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x179e }
            int r7 = r7.intValue()     // Catch:{ Exception -> 0x179e }
            r2.rotation = r7     // Catch:{ Exception -> 0x179e }
        L_0x179d:
            goto L_0x17a5
        L_0x179e:
            r0 = move-exception
            goto L_0x17a5
        L_0x17a0:
            r43 = r2
            r83 = r7
            r13 = 6
        L_0x17a5:
            r64 = r6
            r6 = r74
            r7 = r76
            r10 = 2
            r43 = 6
            r44 = 0
            goto L_0x1a01
        L_0x17b2:
            r83 = r7
            r43 = 6
            java.lang.String r0 = "login/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x17e6
            java.lang.String r0 = "login/"
            java.lang.String r0 = r8.replace(r0, r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x17dd
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            r6.append(r0)
            java.lang.String r63 = r6.toString()
        L_0x17dd:
            r6 = r74
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x17e6:
            java.lang.String r0 = "joinchat/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x17fb
            java.lang.String r0 = "joinchat/"
            java.lang.String r6 = r8.replace(r0, r2)
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x17fb:
            java.lang.String r0 = "+"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1810
            java.lang.String r0 = "+"
            java.lang.String r6 = r8.replace(r0, r2)
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x1810:
            java.lang.String r0 = "addstickers/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1825
            java.lang.String r0 = "addstickers/"
            java.lang.String r7 = r8.replace(r0, r2)
            r6 = r74
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x1825:
            java.lang.String r0 = "msg/"
            boolean r0 = r8.startsWith(r0)
            if (r0 != 0) goto L_0x198c
            java.lang.String r0 = "share/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x183a
            r10 = 2
            r44 = 0
            goto L_0x198f
        L_0x183a:
            java.lang.String r0 = "confirmphone"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1853
            java.lang.String r57 = r3.getQueryParameter(r5)
            java.lang.String r60 = r3.getQueryParameter(r1)
            r6 = r74
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x1853:
            java.lang.String r0 = "setlanguage/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x186a
            r0 = 12
            java.lang.String r61 = r8.substring(r0)
            r6 = r74
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x186a:
            java.lang.String r0 = "addtheme/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x1881
            r0 = 9
            java.lang.String r62 = r8.substring(r0)
            r6 = r74
            r7 = r76
            r10 = 2
            r44 = 0
            goto L_0x1a01
        L_0x1881:
            java.lang.String r0 = "c/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x18e2
            java.util.List r0 = r3.getPathSegments()
            int r2 = r0.size()
            r7 = 3
            if (r2 != r7) goto L_0x18d9
            r2 = 1
            java.lang.Object r7 = r0.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r7)
            r10 = 2
            java.lang.Object r7 = r0.get(r10)
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)
            int r9 = r7.intValue()
            if (r9 == 0) goto L_0x18c0
            long r65 = r2.longValue()
            r44 = 0
            int r9 = (r65 > r44 ? 1 : (r65 == r44 ? 0 : -1))
            if (r9 != 0) goto L_0x18bb
            goto L_0x18c2
        L_0x18bb:
            r66 = r2
            r65 = r7
            goto L_0x18c8
        L_0x18c0:
            r44 = 0
        L_0x18c2:
            r7 = 0
            r2 = 0
            r66 = r2
            r65 = r7
        L_0x18c8:
            java.lang.String r2 = r3.getQueryParameter(r6)
            java.lang.Integer r67 = org.telegram.messenger.Utilities.parseInt(r2)
            int r2 = r67.intValue()
            if (r2 != 0) goto L_0x18dc
            r67 = 0
            goto L_0x18dc
        L_0x18d9:
            r10 = 2
            r44 = 0
        L_0x18dc:
            r6 = r74
            r7 = r76
            goto L_0x1a01
        L_0x18e2:
            r10 = 2
            r44 = 0
            int r0 = r8.length()
            r2 = 1
            if (r0 < r2) goto L_0x19fd
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r2 = r3.getPathSegments()
            r0.<init>(r2)
            int r2 = r0.size()
            if (r2 <= 0) goto L_0x190e
            r2 = 0
            java.lang.Object r7 = r0.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.String r9 = "s"
            boolean r7 = r7.equals(r9)
            if (r7 == 0) goto L_0x190f
            r0.remove(r2)
            goto L_0x190f
        L_0x190e:
            r2 = 0
        L_0x190f:
            int r7 = r0.size()
            if (r7 <= 0) goto L_0x1936
            java.lang.Object r7 = r0.get(r2)
            r2 = r7
            java.lang.String r2 = (java.lang.String) r2
            int r7 = r0.size()
            r9 = 1
            if (r7 <= r9) goto L_0x1938
            java.lang.Object r7 = r0.get(r9)
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            java.lang.Integer r65 = org.telegram.messenger.Utilities.parseInt(r7)
            int r7 = r65.intValue()
            if (r7 != 0) goto L_0x1938
            r65 = 0
            goto L_0x1938
        L_0x1936:
            r2 = r72
        L_0x1938:
            if (r65 == 0) goto L_0x193e
            int r69 = getTimestampFromLink(r3)
        L_0x193e:
            java.lang.String r7 = "start"
            java.lang.String r52 = r3.getQueryParameter(r7)
            java.lang.String r7 = "startgroup"
            java.lang.String r53 = r3.getQueryParameter(r7)
            java.lang.String r7 = "game"
            java.lang.String r58 = r3.getQueryParameter(r7)
            java.lang.String r7 = "voicechat"
            java.lang.String r59 = r3.getQueryParameter(r7)
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
            int r7 = r6.intValue()
            if (r7 != 0) goto L_0x1968
            r6 = 0
            r67 = r6
            goto L_0x196a
        L_0x1968:
            r67 = r6
        L_0x196a:
            java.lang.String r6 = "comment"
            java.lang.String r6 = r3.getQueryParameter(r6)
            java.lang.Integer r68 = org.telegram.messenger.Utilities.parseInt(r6)
            int r6 = r68.intValue()
            if (r6 != 0) goto L_0x1984
            r68 = 0
            r72 = r2
            r6 = r74
            r7 = r76
            goto L_0x1a01
        L_0x1984:
            r72 = r2
            r6 = r74
            r7 = r76
            goto L_0x1a01
        L_0x198c:
            r10 = 2
            r44 = 0
        L_0x198f:
            java.lang.String r0 = "url"
            java.lang.String r0 = r3.getQueryParameter(r0)
            if (r0 != 0) goto L_0x1999
            java.lang.String r0 = ""
        L_0x1999:
            java.lang.String r2 = "text"
            java.lang.String r2 = r3.getQueryParameter(r2)
            if (r2 == 0) goto L_0x19cd
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x19b8
            r70 = 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r13)
            java.lang.String r0 = r2.toString()
        L_0x19b8:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r6 = "text"
            java.lang.String r6 = r3.getQueryParameter(r6)
            r2.append(r6)
            java.lang.String r0 = r2.toString()
        L_0x19cd:
            int r2 = r0.length()
            r6 = 16384(0x4000, float:2.2959E-41)
            if (r2 <= r6) goto L_0x19dd
            r2 = 16384(0x4000, float:2.2959E-41)
            r6 = 0
            java.lang.String r0 = r0.substring(r6, r2)
            goto L_0x19de
        L_0x19dd:
            r6 = 0
        L_0x19de:
            boolean r2 = r0.endsWith(r13)
            if (r2 == 0) goto L_0x19ef
            int r2 = r0.length()
            r7 = 1
            int r2 = r2 - r7
            java.lang.String r0 = r0.substring(r6, r2)
            goto L_0x19de
        L_0x19ef:
            r54 = r0
            r6 = r74
            r7 = r76
            goto L_0x1a01
        L_0x19f6:
            r83 = r7
            r10 = 2
            r43 = 6
            r44 = 0
        L_0x19fd:
            r6 = r74
            r7 = r76
        L_0x1a01:
            r74 = r6
            r76 = r7
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
            goto L_0x1a87
        L_0x1a40:
            r74 = r6
            r76 = r7
            r82 = r9
            r81 = r10
            r10 = 2
            r43 = 6
            r44 = 0
        L_0x1a4d:
            r41 = r16
            r2 = r54
            r13 = r57
            r9 = r60
            r75 = r65
            r77 = r66
            r78 = r67
            r79 = r68
            r80 = r69
            r83 = r70
            r54 = r20
            r57 = r21
            r60 = r24
            r65 = r58
            r66 = r59
            r67 = r61
            r68 = r62
            r69 = r63
            r70 = r64
            r58 = r22
            r59 = r23
            r61 = r38
            r62 = r51
            r63 = r52
            r64 = r53
            r38 = r3
            r51 = r17
            r52 = r18
            r53 = r19
        L_0x1a87:
            boolean r0 = r14.hasExtra(r4)
            if (r0 == 0) goto L_0x1acc
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x1aa5
            java.lang.String r0 = "tg"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x1aa5
            if (r62 != 0) goto L_0x1aa5
            r0 = 1
            goto L_0x1aa6
        L_0x1aa5:
            r0 = 0
        L_0x1aa6:
            com.google.firebase.appindexing.builders.AssistActionBuilder r3 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r3.<init>()
            java.lang.String r6 = r14.getStringExtra(r4)
            com.google.firebase.appindexing.builders.AssistActionBuilder r3 = r3.setActionToken(r6)
            if (r0 == 0) goto L_0x1ab8
            java.lang.String r6 = "http://schema.org/CompletedActionStatus"
            goto L_0x1aba
        L_0x1ab8:
            java.lang.String r6 = "http://schema.org/FailedActionStatus"
        L_0x1aba:
            com.google.firebase.appindexing.Action$Builder r3 = r3.setActionStatus(r6)
            com.google.firebase.appindexing.Action r3 = r3.build()
            com.google.firebase.appindexing.FirebaseUserActions r6 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r94)
            r6.end(r3)
            r14.removeExtra(r4)
        L_0x1acc:
            if (r69 != 0) goto L_0x1ae6
            int r0 = r15.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x1adb
            goto L_0x1ae6
        L_0x1adb:
            r87 = r11
            r7 = r15
            r42 = r44
            r86 = r81
            r84 = r82
            goto L_0x1ca0
        L_0x1ae6:
            if (r13 != 0) goto L_0x1CLASSNAME
            if (r9 == 0) goto L_0x1afa
            r85 = r9
            r87 = r11
            r39 = r12
            r88 = r13
            r42 = r44
            r86 = r81
            r84 = r82
            goto L_0x1CLASSNAME
        L_0x1afa:
            if (r72 != 0) goto L_0x1c0f
            if (r74 != 0) goto L_0x1c0f
            if (r76 != 0) goto L_0x1c0f
            if (r2 != 0) goto L_0x1c0f
            if (r65 != 0) goto L_0x1c0f
            if (r66 != 0) goto L_0x1c0f
            if (r71 != 0) goto L_0x1c0f
            if (r62 != 0) goto L_0x1c0f
            if (r67 != 0) goto L_0x1c0f
            if (r69 != 0) goto L_0x1c0f
            if (r70 != 0) goto L_0x1c0f
            if (r77 != 0) goto L_0x1c0f
            if (r68 != 0) goto L_0x1c0f
            if (r73 == 0) goto L_0x1b18
            goto L_0x1c0f
        L_0x1b18:
            android.content.ContentResolver r16 = r94.getContentResolver()     // Catch:{ Exception -> 0x1be4 }
            android.net.Uri r17 = r95.getData()     // Catch:{ Exception -> 0x1be4 }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            android.database.Cursor r0 = r16.query(r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x1be4 }
            r1 = r0
            if (r1 == 0) goto L_0x1bb8
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x1bae }
            if (r0 == 0) goto L_0x1bb8
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x1bae }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x1bae }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x1bae }
            int r0 = r0.intValue()     // Catch:{ all -> 0x1bae }
            r3 = 0
        L_0x1b48:
            r4 = 3
            if (r3 >= r4) goto L_0x1b66
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r3)     // Catch:{ all -> 0x1bae }
            long r4 = r4.getClientUserId()     // Catch:{ all -> 0x1bae }
            long r6 = (long) r0     // Catch:{ all -> 0x1bae }
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x1b62
            r4 = 0
            r82[r4] = r3     // Catch:{ all -> 0x1bae }
            r5 = r82[r4]     // Catch:{ all -> 0x1bae }
            r8 = 1
            r15.switchToAccount(r5, r8)     // Catch:{ all -> 0x1bae }
            goto L_0x1b67
        L_0x1b62:
            r8 = 1
            int r3 = r3 + 1
            goto L_0x1b48
        L_0x1b66:
            r8 = 1
        L_0x1b67:
            java.lang.String r3 = "data4"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x1bae }
            long r3 = r1.getLong(r3)     // Catch:{ all -> 0x1bae }
            r5 = 0
            r6 = r82[r5]     // Catch:{ all -> 0x1bae }
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)     // Catch:{ all -> 0x1bae }
            int r7 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x1bae }
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch:{ all -> 0x1bae }
            r6.postNotificationName(r7, r8)     // Catch:{ all -> 0x1bae }
            r5 = r3
            java.lang.String r7 = "mimetype"
            int r7 = r1.getColumnIndex(r7)     // Catch:{ all -> 0x1ba9 }
            java.lang.String r7 = r1.getString(r7)     // Catch:{ all -> 0x1ba9 }
            java.lang.String r8 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r8 = android.text.TextUtils.equals(r7, r8)     // Catch:{ all -> 0x1ba9 }
            if (r8 == 0) goto L_0x1b98
            r8 = 1
            r46 = r5
            r41 = r8
            goto L_0x1bb8
        L_0x1b98:
            java.lang.String r8 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r8 = android.text.TextUtils.equals(r7, r8)     // Catch:{ all -> 0x1ba9 }
            if (r8 == 0) goto L_0x1ba6
            r8 = 1
            r46 = r5
            r51 = r8
            goto L_0x1bb8
        L_0x1ba6:
            r46 = r5
            goto L_0x1bb8
        L_0x1ba9:
            r0 = move-exception
            r3 = r0
            r46 = r5
            goto L_0x1bb0
        L_0x1bae:
            r0 = move-exception
            r3 = r0
        L_0x1bb0:
            if (r1 == 0) goto L_0x1bb7
            r1.close()     // Catch:{ all -> 0x1bb6 }
            goto L_0x1bb7
        L_0x1bb6:
            r0 = move-exception
        L_0x1bb7:
            throw r3     // Catch:{ Exception -> 0x1be4 }
        L_0x1bb8:
            if (r1 == 0) goto L_0x1bbd
            r1.close()     // Catch:{ Exception -> 0x1be4 }
        L_0x1bbd:
            r87 = r11
            r7 = r15
            r16 = r41
            r42 = r44
            r1 = r46
            r3 = r48
            r17 = r51
            r18 = r52
            r19 = r53
            r20 = r54
            r5 = r55
            r6 = r56
            r21 = r57
            r22 = r58
            r23 = r59
            r24 = r60
            r38 = r61
            r86 = r81
            r84 = r82
            goto L_0x1cce
        L_0x1be4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r87 = r11
            r7 = r15
            r16 = r41
            r42 = r44
            r1 = r46
            r3 = r48
            r17 = r51
            r18 = r52
            r19 = r53
            r20 = r54
            r5 = r55
            r6 = r56
            r21 = r57
            r22 = r58
            r23 = r59
            r24 = r60
            r38 = r61
            r86 = r81
            r84 = r82
            goto L_0x1cce
        L_0x1c0f:
            if (r2 == 0) goto L_0x1c2c
            java.lang.String r0 = "@"
            boolean r0 = r2.startsWith(r0)
            if (r0 == 0) goto L_0x1c2c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = " "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r2 = r0.toString()
            r0 = r2
            goto L_0x1c2d
        L_0x1c2c:
            r0 = r2
        L_0x1c2d:
            r16 = 0
            r2 = r82[r16]
            r23 = 0
            r1 = r94
            r3 = r72
            r4 = r74
            r5 = r76
            r6 = r63
            r7 = r64
            r17 = 1
            r8 = r0
            r85 = r9
            r84 = r82
            r9 = r83
            r86 = r81
            r18 = 2
            r19 = 0
            r10 = r75
            r87 = r11
            r11 = r77
            r39 = r12
            r42 = r44
            r12 = r78
            r88 = r13
            r13 = r79
            r14 = r65
            r15 = r71
            r16 = r67
            r17 = r62
            r18 = r69
            r19 = r73
            r20 = r70
            r21 = r68
            r22 = r66
            r24 = r80
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24)
            r7 = r94
            goto L_0x1ca0
        L_0x1CLASSNAME:
            r85 = r9
            r87 = r11
            r39 = r12
            r88 = r13
            r42 = r44
            r86 = r81
            r84 = r82
        L_0x1CLASSNAME:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r3 = r88
            r0.putString(r5, r3)
            r4 = r85
            r0.putString(r1, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda21 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda21
            r7 = r94
            r1.<init>(r7, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x1ca0:
            r16 = r41
            r1 = r46
            r3 = r48
            r17 = r51
            r18 = r52
            r19 = r53
            r20 = r54
            r5 = r55
            r6 = r56
            r21 = r57
            r22 = r58
            r23 = r59
            r24 = r60
            r38 = r61
            goto L_0x1cce
        L_0x1cbd:
            r84 = r9
            r86 = r10
            r87 = r11
            r7 = r15
            r42 = 0
            r1 = r46
            r3 = r48
            r5 = r55
            r6 = r56
        L_0x1cce:
            r8 = r95
            r12 = r1
            r14 = r3
            r0 = r5
            r5 = r6
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r10 = r84
            r11 = r86
            r6 = r87
            r9 = 0
            goto L_0x1edd
        L_0x1ced:
            r84 = r9
            r86 = r10
            r87 = r11
            r7 = r15
            r42 = 0
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1d25
            r6 = 1
            r8 = r95
            r5 = r6
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r10 = r84
            r11 = r86
            r6 = r87
            r9 = 0
            goto L_0x1edd
        L_0x1d25:
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1d56
            r31 = 1
            r8 = r95
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
            r10 = r84
            r11 = r86
            r6 = r87
            r9 = 0
            goto L_0x1edd
        L_0x1d56:
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x1e20
            java.lang.String r0 = "chatId"
            r8 = r95
            r9 = 0
            int r0 = r8.getIntExtra(r0, r9)
            long r0 = (long) r0
            java.lang.String r2 = "chatId"
            long r0 = r8.getLongExtra(r2, r0)
            java.lang.String r2 = "userId"
            int r2 = r8.getIntExtra(r2, r9)
            long r2 = (long) r2
            java.lang.String r4 = "userId"
            long r2 = r8.getLongExtra(r4, r2)
            java.lang.String r4 = "encId"
            int r4 = r8.getIntExtra(r4, r9)
            java.lang.String r5 = "appWidgetId"
            int r5 = r8.getIntExtra(r5, r9)
            if (r5 == 0) goto L_0x1da4
            r6 = 6
            r29 = r5
            java.lang.String r10 = "appWidgetType"
            int r30 = r8.getIntExtra(r10, r9)
            r56 = r6
            r1 = r46
            r3 = r48
            r5 = r55
            r10 = r84
            r6 = r87
            goto L_0x1e07
        L_0x1da4:
            if (r55 != 0) goto L_0x1daf
            r6 = r87
            int r10 = r8.getIntExtra(r6, r9)
            r55 = r10
            goto L_0x1db1
        L_0x1daf:
            r6 = r87
        L_0x1db1:
            int r10 = (r0 > r42 ? 1 : (r0 == r42 ? 0 : -1))
            if (r10 == 0) goto L_0x1dcb
            r10 = r84
            r11 = r10[r9]
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getInstance(r11)
            int r12 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r13 = new java.lang.Object[r9]
            r11.postNotificationName(r12, r13)
            r11 = r0
            r3 = r11
            r1 = r46
            r5 = r55
            goto L_0x1e07
        L_0x1dcb:
            r10 = r84
            int r11 = (r2 > r42 ? 1 : (r2 == r42 ? 0 : -1))
            if (r11 == 0) goto L_0x1de5
            r11 = r10[r9]
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getInstance(r11)
            int r12 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r13 = new java.lang.Object[r9]
            r11.postNotificationName(r12, r13)
            r11 = r2
            r1 = r11
            r3 = r48
            r5 = r55
            goto L_0x1e07
        L_0x1de5:
            if (r4 == 0) goto L_0x1dfe
            r11 = r10[r9]
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getInstance(r11)
            int r12 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r13 = new java.lang.Object[r9]
            r11.postNotificationName(r12, r13)
            r11 = r4
            r28 = r11
            r1 = r46
            r3 = r48
            r5 = r55
            goto L_0x1e07
        L_0x1dfe:
            r11 = 1
            r34 = r11
            r1 = r46
            r3 = r48
            r5 = r55
        L_0x1e07:
            r12 = r1
            r14 = r3
            r0 = r5
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r5 = r56
            r11 = r86
            goto L_0x1edd
        L_0x1e20:
            r8 = r95
            r10 = r84
            r6 = r87
            r9 = 0
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1e51
            r35 = 1
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
            r11 = r86
            goto L_0x1edd
        L_0x1e51:
            java.lang.String r0 = r95.getAction()
            java.lang.String r1 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1e7b
            r36 = 1
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
            r11 = r86
            goto L_0x1edd
        L_0x1e7b:
            java.lang.String r0 = "voip_chat"
            r11 = r86
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L_0x1ec5
            r37 = 1
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
            goto L_0x1edd
        L_0x1ea0:
            r46 = r1
            r48 = r3
            r55 = r5
            r56 = r6
            r50 = r7
            r6 = r11
            r42 = r12
            r8 = r14
            r7 = r15
            r11 = r10
            r10 = r9
            r9 = 0
            goto L_0x1ec5
        L_0x1eb3:
            r46 = r1
            r48 = r3
            r55 = r5
            r56 = r6
            r50 = r7
            r6 = r11
            r42 = r12
            r8 = r14
            r7 = r15
            r11 = r10
            r10 = r9
            r9 = 0
        L_0x1ec5:
            r4 = r17
            r3 = r22
            r2 = r23
            r1 = r24
            r89 = r28
            r90 = r29
            r91 = r30
            r92 = r38
            r12 = r46
            r14 = r48
            r0 = r55
            r5 = r56
        L_0x1edd:
            int r9 = r7.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            boolean r9 = r9.isClientActivated()
            if (r9 == 0) goto L_0x25aa
            if (r3 == 0) goto L_0x1f1e
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r9 = r9.getLastFragment()
            r86 = r11
            boolean r11 = r9 instanceof org.telegram.ui.DialogsActivity
            if (r11 == 0) goto L_0x1var_
            r11 = r9
            org.telegram.ui.DialogsActivity r11 = (org.telegram.ui.DialogsActivity) r11
            boolean r17 = r11.isMainDialogList()
            if (r17 == 0) goto L_0x1var_
            android.view.View r17 = r11.getFragmentView()
            if (r17 == 0) goto L_0x1f0d
            r17 = r9
            r9 = 1
            r11.search(r3, r9)
            goto L_0x1var_
        L_0x1f0d:
            r17 = r9
            r9 = 1
            r11.setInitialSearchString(r3)
            goto L_0x1var_
        L_0x1var_:
            r17 = r9
            r9 = 1
        L_0x1var_:
            goto L_0x1var_
        L_0x1var_:
            r17 = r9
            r9 = 1
            r34 = 1
            goto L_0x1var_
        L_0x1f1e:
            r86 = r11
            r9 = 1
        L_0x1var_:
            int r11 = (r12 > r42 ? 1 : (r12 == r42 ? 0 : -1))
            if (r11 == 0) goto L_0x1ff2
            if (r16 != 0) goto L_0x1f9f
            if (r4 == 0) goto L_0x1f2d
            r23 = r3
            goto L_0x1fa1
        L_0x1f2d:
            android.os.Bundle r11 = new android.os.Bundle
            r11.<init>()
            java.lang.String r9 = "user_id"
            r11.putLong(r9, r12)
            if (r0 == 0) goto L_0x1f3c
            r11.putInt(r6, r0)
        L_0x1f3c:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x1var_
            r6 = 0
            r9 = r10[r6]
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r9)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = mainFragmentsStack
            int r17 = r9.size()
            r23 = r3
            r22 = 1
            int r3 = r17 + -1
            java.lang.Object r3 = r9.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            boolean r3 = r6.checkCanOpenChat(r11, r3)
            if (r3 == 0) goto L_0x1var_
            goto L_0x1var_
        L_0x1var_:
            r23 = r3
        L_0x1var_:
            org.telegram.ui.ChatActivity r3 = new org.telegram.ui.ChatActivity
            r3.<init>(r11)
            r39 = r3
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r3
            boolean r3 = r38.presentFragment(r39, r40, r41, r42, r43)
            if (r3 == 0) goto L_0x1var_
            r27 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r7.drawerLayoutContainer
            r3.closeDrawer()
        L_0x1var_:
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r17 = r89
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x1f9f:
            r23 = r3
        L_0x1fa1:
            if (r18 == 0) goto L_0x1fd1
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.getLastFragment()
            if (r3 == 0) goto L_0x1fba
            org.telegram.messenger.MessagesController r6 = r3.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r9)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r3, r6, r4)
        L_0x1fba:
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r17 = r89
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25a7
        L_0x1fd1:
            r3 = 0
            r6 = r10[r3]
            org.telegram.messenger.AccountInstance r3 = org.telegram.messenger.AccountInstance.getInstance(r6)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r12, r4, r3)
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r17 = r89
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25a7
        L_0x1ff2:
            r23 = r3
            int r3 = (r14 > r42 ? 1 : (r14 == r42 ? 0 : -1))
            if (r3 == 0) goto L_0x2065
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.lang.String r9 = "chat_id"
            r3.putLong(r9, r14)
            if (r0 == 0) goto L_0x2007
            r3.putInt(r6, r0)
        L_0x2007:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = mainFragmentsStack
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x202c
            r6 = 0
            r9 = r10[r6]
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r9)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = mainFragmentsStack
            int r11 = r9.size()
            r17 = 1
            int r11 = r11 + -1
            java.lang.Object r9 = r9.get(r11)
            org.telegram.ui.ActionBar.BaseFragment r9 = (org.telegram.ui.ActionBar.BaseFragment) r9
            boolean r6 = r6.checkCanOpenChat(r3, r9)
            if (r6 == 0) goto L_0x204c
        L_0x202c:
            org.telegram.ui.ChatActivity r6 = new org.telegram.ui.ChatActivity
            r6.<init>(r3)
            r39 = r6
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r6
            boolean r6 = r38.presentFragment(r39, r40, r41, r42, r43)
            if (r6 == 0) goto L_0x204c
            r27 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r7.drawerLayoutContainer
            r6.closeDrawer()
        L_0x204c:
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r17 = r89
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x2065:
            r9 = r89
            if (r9 == 0) goto L_0x20ac
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            java.lang.String r6 = "enc_id"
            r3.putInt(r6, r9)
            org.telegram.ui.ChatActivity r6 = new org.telegram.ui.ChatActivity
            r6.<init>(r3)
            r39 = r6
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r6
            boolean r6 = r38.presentFragment(r39, r40, r41, r42, r43)
            if (r6 == 0) goto L_0x2093
            r27 = 1
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r7.drawerLayoutContainer
            r6.closeDrawer()
        L_0x2093:
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r17 = r9
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x20ac:
            if (r34 == 0) goto L_0x210d
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 != 0) goto L_0x20bc
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            r3.removeAllFragments()
            r17 = r9
            goto L_0x20f5
        L_0x20bc:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x20f3
            r3 = 0
        L_0x20c7:
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            int r6 = r6.size()
            r11 = 1
            int r6 = r6 - r11
            if (r3 >= r6) goto L_0x20ea
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = r6.fragmentsStack
            r17 = r9
            r9 = 0
            java.lang.Object r11 = r11.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            r6.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r11)
            int r3 = r3 + -1
            r6 = 1
            int r3 = r3 + r6
            r9 = r17
            goto L_0x20c7
        L_0x20ea:
            r17 = r9
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.layersActionBarLayout
            r6 = 0
            r3.closeLastFragment(r6)
            goto L_0x20f5
        L_0x20f3:
            r17 = r9
        L_0x20f5:
            r27 = 0
            r3 = 0
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x210d:
            r17 = r9
            if (r35 == 0) goto L_0x214a
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x2130
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            r6 = 0
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            org.telegram.ui.Components.AudioPlayerAlert r6 = new org.telegram.ui.Components.AudioPlayerAlert
            r9 = 0
            r6.<init>(r7, r9)
            r3.showDialog(r6)
            goto L_0x2131
        L_0x2130:
            r9 = 0
        L_0x2131:
            r27 = 0
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x214a:
            r9 = 0
            if (r36 == 0) goto L_0x2188
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x216f
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            r6 = 0
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            org.telegram.ui.Components.SharingLocationsAlert r6 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76
            r11.<init>(r7, r10)
            r6.<init>(r7, r11, r9)
            r3.showDialog(r6)
        L_0x216f:
            r27 = 0
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x2188:
            android.net.Uri r3 = r7.exportingChatUri
            if (r3 == 0) goto L_0x21a6
            java.util.ArrayList<android.net.Uri> r6 = r7.documentsUrisArray
            r7.runImportRequest(r3, r6)
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25a7
        L_0x21a6:
            java.util.ArrayList<android.os.Parcelable> r3 = r7.importingStickers
            if (r3 == 0) goto L_0x21cb
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17
            r3.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            r27 = 0
            r3 = r96
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x21cb:
            java.lang.String r3 = r7.videoPath
            if (r3 != 0) goto L_0x2560
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r7.photoPathsArray
            if (r3 != 0) goto L_0x2560
            java.lang.String r3 = r7.sendingText
            if (r3 != 0) goto L_0x2560
            java.util.ArrayList<java.lang.String> r3 = r7.documentsPathsArray
            if (r3 != 0) goto L_0x2560
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r7.contactsToSend
            if (r3 != 0) goto L_0x2560
            java.util.ArrayList<android.net.Uri> r3 = r7.documentsUrisArray
            if (r3 == 0) goto L_0x21f8
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
            goto L_0x2573
        L_0x21f8:
            if (r5 == 0) goto L_0x22d2
            r3 = 0
            r6 = 1
            if (r5 != r6) goto L_0x221e
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            int r11 = r7.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            r84 = r10
            long r9 = r11.clientUserId
            java.lang.String r11 = "user_id"
            r6.putLong(r11, r9)
            org.telegram.ui.ProfileActivity r9 = new org.telegram.ui.ProfileActivity
            r9.<init>(r6)
            r6 = r9
            r11 = r90
            r10 = r91
            r6 = 6
            goto L_0x2272
        L_0x221e:
            r84 = r10
            r6 = 2
            if (r5 != r6) goto L_0x2230
            org.telegram.ui.ThemeActivity r6 = new org.telegram.ui.ThemeActivity
            r9 = 0
            r6.<init>(r9)
            r9 = r6
            r11 = r90
            r10 = r91
            r6 = 6
            goto L_0x2272
        L_0x2230:
            r9 = 0
            r6 = 3
            if (r5 != r6) goto L_0x2240
            org.telegram.ui.SessionsActivity r6 = new org.telegram.ui.SessionsActivity
            r6.<init>(r9)
            r9 = r6
            r11 = r90
            r10 = r91
            r6 = 6
            goto L_0x2272
        L_0x2240:
            r6 = 4
            if (r5 != r6) goto L_0x224f
            org.telegram.ui.FiltersSetupActivity r6 = new org.telegram.ui.FiltersSetupActivity
            r6.<init>()
            r9 = r6
            r11 = r90
            r10 = r91
            r6 = 6
            goto L_0x2272
        L_0x224f:
            r6 = 5
            if (r5 != r6) goto L_0x2260
            org.telegram.ui.ActionIntroActivity r6 = new org.telegram.ui.ActionIntroActivity
            r9 = 3
            r6.<init>(r9)
            r3 = 1
            r9 = r6
            r11 = r90
            r10 = r91
            r6 = 6
            goto L_0x2272
        L_0x2260:
            r6 = 6
            if (r5 != r6) goto L_0x226d
            org.telegram.ui.EditWidgetActivity r9 = new org.telegram.ui.EditWidgetActivity
            r11 = r90
            r10 = r91
            r9.<init>(r10, r11)
            goto L_0x2272
        L_0x226d:
            r11 = r90
            r10 = r91
            r9 = 0
        L_0x2272:
            r22 = r3
            if (r5 != r6) goto L_0x228c
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r6
            r39 = r9
            r38.presentFragment(r39, r40, r41, r42, r43)
            r24 = r0
            r0 = r22
            goto L_0x2298
        L_0x228c:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda43 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda43
            r24 = r0
            r0 = r22
            r6.<init>(r7, r9, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
        L_0x2298:
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r6 == 0) goto L_0x22b3
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.actionBarLayout
            r6.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.rightActionBarLayout
            r6.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r7.drawerLayoutContainer
            r22 = r3
            r3 = 0
            r6.setAllowOpenDrawer(r3, r3)
            r28 = r5
            goto L_0x22be
        L_0x22b3:
            r22 = r3
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r7.drawerLayoutContainer
            r28 = r5
            r5 = 1
            r6.setAllowOpenDrawer(r5, r3)
        L_0x22be:
            r27 = 1
            r3 = r96
            r30 = r1
            r29 = r2
            r93 = r23
            r56 = r28
            r9 = r84
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x22d2:
            r24 = r0
            r28 = r5
            r84 = r10
            r11 = r90
            r10 = r91
            r6 = 2
            if (r31 == 0) goto L_0x2332
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "destroyAfterSelect"
            r5 = 1
            r0.putBoolean(r3, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            org.telegram.ui.ContactsActivity r5 = new org.telegram.ui.ContactsActivity
            r5.<init>(r0)
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r3
            r39 = r5
            r38.presentFragment(r39, r40, r41, r42, r43)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x2317
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.actionBarLayout
            r3.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r7.rightActionBarLayout
            r3.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r7.drawerLayoutContainer
            r5 = 0
            r3.setAllowOpenDrawer(r5, r5)
            goto L_0x231e
        L_0x2317:
            r5 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r3 = r7.drawerLayoutContainer
            r6 = 1
            r3.setAllowOpenDrawer(r6, r5)
        L_0x231e:
            r27 = 1
            r3 = r96
            r30 = r1
            r29 = r2
            r93 = r23
            r56 = r28
            r9 = r84
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x2332:
            if (r2 == 0) goto L_0x23ad
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "destroyAfterSelect"
            r5 = 1
            r0.putBoolean(r3, r5)
            java.lang.String r3 = "returnAsResult"
            r0.putBoolean(r3, r5)
            java.lang.String r3 = "onlyUsers"
            r0.putBoolean(r3, r5)
            java.lang.String r3 = "allowSelf"
            r5 = 0
            r0.putBoolean(r3, r5)
            org.telegram.ui.ContactsActivity r3 = new org.telegram.ui.ContactsActivity
            r3.<init>(r0)
            r3.setInitialSearchString(r2)
            r5 = r4
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
            r9 = r84
            r6.<init>(r7, r5, r9)
            r3.setDelegate(r6)
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r7.actionBarLayout
            r22 = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.getLastFragment()
            boolean r0 = r0 instanceof org.telegram.ui.ContactsActivity
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r6
            r39 = r3
            r40 = r0
            r38.presentFragment(r39, r40, r41, r42, r43)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2394
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r6 = 0
            r0.setAllowOpenDrawer(r6, r6)
            r29 = r2
            goto L_0x239d
        L_0x2394:
            r6 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r29 = r2
            r2 = 1
            r0.setAllowOpenDrawer(r2, r6)
        L_0x239d:
            r27 = 1
            r3 = r96
            r30 = r1
            r93 = r23
            r56 = r28
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x23ad:
            r29 = r2
            r9 = r84
            if (r21 == 0) goto L_0x2400
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r2 = 5
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70
            r2.<init>(r7, r0)
            r0.setQrLoginDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r2
            r39 = r0
            r38.presentFragment(r39, r40, r41, r42, r43)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x23e9
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.rightActionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r3 = 0
            r2.setAllowOpenDrawer(r3, r3)
            goto L_0x23f0
        L_0x23e9:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r5 = 1
            r2.setAllowOpenDrawer(r5, r3)
        L_0x23f0:
            r27 = 1
            r3 = r96
            r30 = r1
            r93 = r23
            r56 = r28
            r28 = r92
            r23 = r4
            goto L_0x25c3
        L_0x2400:
            if (r19 == 0) goto L_0x2469
            org.telegram.ui.NewContactActivity r0 = new org.telegram.ui.NewContactActivity
            r0.<init>()
            if (r1 == 0) goto L_0x241d
            java.lang.String r2 = " "
            java.lang.String[] r2 = r1.split(r2, r6)
            r3 = 0
            r5 = r2[r3]
            int r3 = r2.length
            r6 = 1
            if (r3 <= r6) goto L_0x2419
            r3 = r2[r6]
            goto L_0x241a
        L_0x2419:
            r3 = 0
        L_0x241a:
            r0.setInitialName(r5, r3)
        L_0x241d:
            r6 = r92
            if (r6 == 0) goto L_0x242a
            r2 = 1
            java.lang.String r3 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r6, r2)
            r2 = 0
            r0.setInitialPhoneNumber(r3, r2)
        L_0x242a:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r40 = 0
            r41 = 1
            r42 = 1
            r43 = 0
            r38 = r2
            r39 = r0
            r38.presentFragment(r39, r40, r41, r42, r43)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x2452
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.rightActionBarLayout
            r2.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r3 = 0
            r2.setAllowOpenDrawer(r3, r3)
            goto L_0x2459
        L_0x2452:
            r3 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r5 = 1
            r2.setAllowOpenDrawer(r5, r3)
        L_0x2459:
            r27 = 1
            r3 = r96
            r30 = r1
            r93 = r23
            r56 = r28
            r23 = r4
            r28 = r6
            goto L_0x25c3
        L_0x2469:
            r6 = r92
            if (r37 == 0) goto L_0x2498
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r0 = 0
            r5 = 0
            r22 = 0
            r30 = r1
            r1 = r94
            r93 = r23
            r23 = r4
            r4 = r0
            r56 = r28
            r0 = r6
            r6 = r22
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r1 == 0) goto L_0x2494
            r1 = 1
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r1
            r28 = r0
            goto L_0x25a7
        L_0x2494:
            r28 = r0
            goto L_0x25a7
        L_0x2498:
            r30 = r1
            r0 = r6
            r93 = r23
            r56 = r28
            r23 = r4
            if (r20 == 0) goto L_0x252a
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r1.getLastFragment()
            if (r1 == 0) goto L_0x2524
            android.app.Activity r2 = r1.getParentActivity()
            if (r2 == 0) goto L_0x2524
            r2 = r30
            int r3 = r7.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
            r4 = 0
            java.lang.String r3 = org.telegram.ui.NewContactActivity.getPhoneNumber(r7, r3, r0, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r5 = r1.getParentActivity()
            r4.<init>((android.content.Context) r5)
            r5 = 2131626466(0x7f0e09e2, float:1.888017E38)
            java.lang.String r6 = "NewContactAlertTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            org.telegram.ui.ActionBar.AlertDialog$Builder r4 = r4.setTitle(r5)
            r6 = 1
            java.lang.Object[] r5 = new java.lang.Object[r6]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r6 = r6.format(r3)
            r28 = 0
            r5[r28] = r6
            java.lang.String r6 = "NewContactAlertMessage"
            r28 = r0
            r0 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r4.setMessage(r0)
            r4 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            java.lang.String r5 = "NewContactAlertButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda77
            r5.<init>(r3, r2, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setPositiveButton(r4, r5)
            r4 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r5 = "Cancel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 0
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r4, r5)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1.showDialog(r0)
            r27 = 1
            goto L_0x2526
        L_0x2524:
            r28 = r0
        L_0x2526:
            r3 = r96
            goto L_0x25c3
        L_0x252a:
            r28 = r0
            if (r50 == 0) goto L_0x25a7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r1 = new org.telegram.ui.CallLogActivity
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 0
            r0.presentFragment(r1, r2, r3, r4, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2553
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x255a
        L_0x2553:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r2 = 1
            r0.setAllowOpenDrawer(r2, r1)
        L_0x255a:
            r27 = 1
            r3 = r96
            goto L_0x25c3
        L_0x2560:
            r24 = r0
            r30 = r1
            r29 = r2
            r56 = r5
            r9 = r10
            r93 = r23
            r11 = r90
            r10 = r91
            r28 = r92
            r23 = r4
        L_0x2573:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x2588
            r1 = 0
            r0 = r9[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r3)
            goto L_0x2589
        L_0x2588:
            r1 = 0
        L_0x2589:
            int r0 = (r32 > r42 ? 1 : (r32 == r42 ? 0 : -1))
            if (r0 != 0) goto L_0x2595
            r7.openDialogsToSend(r1)
            r27 = 1
            r3 = r96
            goto L_0x25c3
        L_0x2595:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r1 = java.lang.Long.valueOf(r32)
            r0.add(r1)
            r1 = 0
            r2 = 0
            r7.didSelectDialogs(r1, r0, r1, r2)
        L_0x25a7:
            r3 = r96
            goto L_0x25c3
        L_0x25aa:
            r24 = r0
            r30 = r1
            r29 = r2
            r93 = r3
            r23 = r4
            r56 = r5
            r9 = r10
            r86 = r11
            r17 = r89
            r11 = r90
            r10 = r91
            r28 = r92
            r3 = r96
        L_0x25c3:
            if (r27 != 0) goto L_0x2682
            if (r3 != 0) goto L_0x2682
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2627
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x25fb
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x25f7
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            r1 = r93
            goto L_0x266c
        L_0x25f7:
            r1 = r93
            goto L_0x266c
        L_0x25fb:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x2624
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r1 = 0
            r0.<init>(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            r1 = r93
            if (r1 == 0) goto L_0x2617
            r0.setInitialSearchString(r1)
        L_0x2617:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r2.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r4 = 1
            r5 = 0
            r2.setAllowOpenDrawer(r4, r5)
            goto L_0x266c
        L_0x2624:
            r1 = r93
            goto L_0x266c
        L_0x2627:
            r1 = r93
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x266c
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x2650
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.LoginActivity r2 = new org.telegram.ui.LoginActivity
            r2.<init>()
            r0.addFragmentToStack(r2)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r2 = 0
            r0.setAllowOpenDrawer(r2, r2)
            goto L_0x266c
        L_0x2650:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r2 = 0
            r0.<init>(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r7.sideMenu
            r0.setSideMenu(r2)
            if (r1 == 0) goto L_0x2660
            r0.setInitialSearchString(r1)
        L_0x2660:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            r2.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r7.drawerLayoutContainer
            r4 = 1
            r5 = 0
            r2.setAllowOpenDrawer(r4, r5)
        L_0x266c:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2684
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x2684
        L_0x2682:
            r1 = r93
        L_0x2684:
            if (r26 == 0) goto L_0x268c
            r2 = 0
            r0 = r9[r2]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x268c:
            if (r37 != 0) goto L_0x26a5
            if (r8 == 0) goto L_0x269c
            java.lang.String r0 = r95.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x26a5
        L_0x269c:
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x26a5
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r0.dismiss()
        L_0x26a5:
            r2 = 0
            r8.setAction(r2)
            return r27
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* renamed from: lambda$handleIntent$7$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3081lambda$handleIntent$7$orgtelegramuiLaunchActivity(Intent copyIntent, boolean contactsLoaded) {
        handleIntent(copyIntent, true, false, false);
    }

    /* renamed from: lambda$handleIntent$8$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3082lambda$handleIntent$8$orgtelegramuiLaunchActivity(Bundle args) {
        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new CancelAccountDeletionActivity(args));
    }

    /* renamed from: lambda$handleIntent$10$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3076lambda$handleIntent$10$orgtelegramuiLaunchActivity(int[] intentAccount, LocationController.SharingLocationInfo info) {
        intentAccount[0] = info.messageObject.currentAccount;
        switchToAccount(intentAccount[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(info.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda83(intentAccount, info.messageObject.getDialogId()));
        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(locationActivity);
    }

    /* renamed from: lambda$handleIntent$11$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3077lambda$handleIntent$11$orgtelegramuiLaunchActivity() {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).showDialog(new StickersAlert((Context) this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
        }
    }

    /* renamed from: lambda$handleIntent$12$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3078lambda$handleIntent$12$orgtelegramuiLaunchActivity(BaseFragment fragment, boolean closePreviousFinal) {
        presentFragment(fragment, closePreviousFinal, false);
    }

    /* renamed from: lambda$handleIntent$13$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3079lambda$handleIntent$13$orgtelegramuiLaunchActivity(boolean videoCall, int[] intentAccount, TLRPC.User user, String param, ContactsActivity activity) {
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
        VoIPHelper.startCall(user, videoCall, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(intentAccount[0]));
    }

    /* renamed from: lambda$handleIntent$17$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3080lambda$handleIntent$17$orgtelegramuiLaunchActivity(ActionIntroActivity fragment, String code) {
        AlertDialog progressDialog = new AlertDialog(this, 3);
        progressDialog.setCanCacnel(false);
        progressDialog.show();
        byte[] token = Base64.decode(code.substring("tg://login?token=".length()), 8);
        TLRPC.TL_auth_acceptLoginToken req = new TLRPC.TL_auth_acceptLoginToken();
        req.token = token;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda50(progressDialog, fragment));
    }

    static /* synthetic */ void lambda$handleIntent$15(AlertDialog progressDialog, TLObject response, ActionIntroActivity fragment, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (!(response instanceof TLRPC.TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda13(fragment, error));
        }
    }

    static /* synthetic */ void lambda$handleIntent$14(ActionIntroActivity fragment, TLRPC.TL_error error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(fragment, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
    }

    static /* synthetic */ void lambda$handleIntent$18(String finalNewContactPhone, String finalNewContactName, BaseFragment lastFragment, DialogInterface d, int i) {
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
        DialogsActivity fragment = new DialogsActivity(args);
        fragment.setDelegate(this);
        if (AndroidUtilities.isTablet()) {
            removeLast = this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
        } else {
            removeLast = this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
        }
        this.actionBarLayout.presentFragment(fragment, removeLast, !animated, true, false);
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
        return ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda52(this, intentAccount, messageId, chat, req, commentId, threadId, progressDialog));
    }

    /* renamed from: lambda$runCommentRequest$20$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3088lambda$runCommentRequest$20$orgtelegramuiLaunchActivity(int intentAccount, Integer messageId, TLRPC.Chat chat, TLRPC.TL_messages_getDiscussionMessage req, Integer commentId, Integer threadId, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda27(this, response, intentAccount, messageId, chat, req, commentId, threadId, progressDialog));
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x00a0 A[SYNTHETIC, Splitter:B:17:0x00a0] */
    /* renamed from: lambda$runCommentRequest$19$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3087lambda$runCommentRequest$19$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r15, int r16, java.lang.Integer r17, org.telegram.tgnet.TLRPC.Chat r18, org.telegram.tgnet.TLRPC.TL_messages_getDiscussionMessage r19, java.lang.Integer r20, java.lang.Integer r21, org.telegram.ui.ActionBar.AlertDialog r22) {
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
            r14.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(r6)
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
            r4 = 2131624810(0x7f0e036a, float:1.887681E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3087lambda$runCommentRequest$19$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda56(this, importUri, intentAccount, progressDialog));
            progressDialog.setOnCancelListener(new LaunchActivity$$ExternalSyntheticLambda0(intentAccount, requestId, (Runnable) null));
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

    /* renamed from: lambda$runImportRequest$22$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3090lambda$runImportRequest$22$orgtelegramuiLaunchActivity(Uri importUri, int intentAccount, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda28(this, response, importUri, intentAccount, progressDialog), 2);
    }

    /* renamed from: lambda$runImportRequest$21$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3089lambda$runImportRequest$21$orgtelegramuiLaunchActivity(TLObject response, Uri importUri, int intentAccount, AlertDialog progressDialog) {
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

    static /* synthetic */ void lambda$runImportRequest$23(int intentAccount, int[] requestId, Runnable cancelRunnableFinal, DialogInterface dialog) {
        ConnectionsManager.getInstance(intentAccount).cancelRequest(requestId[0], true);
        if (cancelRunnableFinal != null) {
            cancelRunnableFinal.run();
        }
    }

    private void openGroupCall(AccountInstance accountInstance, TLRPC.Chat chat, String hash) {
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        VoIPHelper.startCall(chat, (TLRPC.InputPeer) null, hash, false, this, arrayList.get(arrayList.size() - 1), accountInstance);
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Long channelId, Integer threadId, Integer commentId, String game, HashMap<String, String> auth, String lang, String unsupportedUrl, String code, String loginToken, TLRPC.TL_wallPaper wallPaper, String theme, String voicechat, int state, int videoTimestamp) {
        AlertDialog progressDialog;
        int i;
        int[] requestId;
        WallpapersListActivity.ColorWallpaper colorWallpaper;
        StickersAlert alert;
        int i2 = intentAccount;
        String str = username;
        String str2 = group;
        String str3 = sticker;
        String str4 = message;
        Long l = channelId;
        HashMap<String, String> hashMap = auth;
        String str5 = lang;
        String str6 = unsupportedUrl;
        TLRPC.TL_wallPaper tL_wallPaper = wallPaper;
        String str7 = theme;
        if (state != 0 || UserConfig.getActivatedAccountsCount() < 2 || hashMap == null) {
            BaseFragment baseFragment = null;
            if (code != null) {
                if (NotificationCenter.getGlobalInstance().hasObservers(NotificationCenter.didReceiveSmsCode)) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, code);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("OtherLoginCode", NUM, code)));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            } else if (loginToken != null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) this);
                builder2.setTitle(LocaleController.getString("AuthAnotherClient", NUM));
                builder2.setMessage(LocaleController.getString("AuthAnotherClientUrl", NUM));
                builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder2);
            } else {
                AlertDialog progressDialog2 = new AlertDialog(this, 3);
                int[] requestId2 = {0};
                Runnable cancelRunnable = null;
                String str8 = username;
                if (str8 != null) {
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = str8;
                    LaunchActivity$$ExternalSyntheticLambda57 launchActivity$$ExternalSyntheticLambda57 = r1;
                    ConnectionsManager instance = ConnectionsManager.getInstance(intentAccount);
                    AlertDialog progressDialog3 = progressDialog2;
                    LaunchActivity$$ExternalSyntheticLambda57 launchActivity$$ExternalSyntheticLambda572 = new LaunchActivity$$ExternalSyntheticLambda57(this, game, voicechat, intentAccount, messageId, commentId, threadId, requestId2, progressDialog2, botChat, botUser, videoTimestamp);
                    requestId = requestId2;
                    requestId[0] = instance.sendRequest(req, launchActivity$$ExternalSyntheticLambda57);
                    i = intentAccount;
                    int i3 = state;
                    progressDialog = progressDialog3;
                } else {
                    requestId = requestId2;
                    AlertDialog progressDialog4 = progressDialog2;
                    String str9 = group;
                    if (str9 != null) {
                        int i4 = state;
                        if (i4 == 0) {
                            TLRPC.TL_messages_checkChatInvite req2 = new TLRPC.TL_messages_checkChatInvite();
                            req2.hash = str9;
                            i = intentAccount;
                            progressDialog = progressDialog4;
                            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req2, new LaunchActivity$$ExternalSyntheticLambda54(this, i, progressDialog, str9), 2);
                        } else {
                            i = intentAccount;
                            progressDialog = progressDialog4;
                            if (i4 == 1) {
                                TLRPC.TL_messages_importChatInvite req3 = new TLRPC.TL_messages_importChatInvite();
                                req3.hash = str9;
                                ConnectionsManager.getInstance(intentAccount).sendRequest(req3, new LaunchActivity$$ExternalSyntheticLambda53(this, i, progressDialog), 2);
                            }
                        }
                    } else {
                        i = intentAccount;
                        int i5 = state;
                        progressDialog = progressDialog4;
                        String str10 = sticker;
                        if (str10 == null) {
                            String str11 = message;
                            if (str11 != null) {
                                Bundle args = new Bundle();
                                args.putBoolean("onlySelect", true);
                                args.putInt("dialogsType", 3);
                                DialogsActivity fragment = new DialogsActivity(args);
                                fragment.setDelegate(new LaunchActivity$$ExternalSyntheticLambda81(this, hasUrl, i, str11));
                                presentFragment(fragment, false, true);
                            } else {
                                boolean z = hasUrl;
                                HashMap<String, String> hashMap2 = auth;
                                if (hashMap2 != null) {
                                    int bot_id = Utilities.parseInt(hashMap2.get("bot_id")).intValue();
                                    if (bot_id != 0) {
                                        TLRPC.TL_account_getAuthorizationForm req4 = new TLRPC.TL_account_getAuthorizationForm();
                                        req4.bot_id = (long) bot_id;
                                        req4.scope = hashMap2.get("scope");
                                        req4.public_key = hashMap2.get("public_key");
                                        int i6 = bot_id;
                                        LaunchActivity$$ExternalSyntheticLambda67 launchActivity$$ExternalSyntheticLambda67 = r1;
                                        ConnectionsManager instance2 = ConnectionsManager.getInstance(intentAccount);
                                        TLRPC.TL_account_getAuthorizationForm req5 = req4;
                                        LaunchActivity$$ExternalSyntheticLambda67 launchActivity$$ExternalSyntheticLambda672 = new LaunchActivity$$ExternalSyntheticLambda67(this, requestId, intentAccount, progressDialog, req5, hashMap2.get("payload"), hashMap2.get("nonce"), hashMap2.get("callback_url"));
                                        requestId[0] = instance2.sendRequest(req5, launchActivity$$ExternalSyntheticLambda67);
                                    } else {
                                        return;
                                    }
                                } else {
                                    String str12 = unsupportedUrl;
                                    if (str12 != null) {
                                        TLRPC.TL_help_getDeepLinkInfo req6 = new TLRPC.TL_help_getDeepLinkInfo();
                                        req6.path = str12;
                                        requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req6, new LaunchActivity$$ExternalSyntheticLambda58(this, progressDialog));
                                    } else {
                                        String str13 = lang;
                                        if (str13 != null) {
                                            TLRPC.TL_langpack_getLanguage req7 = new TLRPC.TL_langpack_getLanguage();
                                            req7.lang_code = str13;
                                            req7.lang_pack = "android";
                                            requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req7, new LaunchActivity$$ExternalSyntheticLambda59(this, progressDialog));
                                        } else {
                                            TLRPC.TL_wallPaper tL_wallPaper2 = wallPaper;
                                            if (tL_wallPaper2 != null) {
                                                boolean ok = false;
                                                if (TextUtils.isEmpty(tL_wallPaper2.slug)) {
                                                    try {
                                                        if (tL_wallPaper2.settings.third_background_color != 0) {
                                                            colorWallpaper = new WallpapersListActivity.ColorWallpaper("c", tL_wallPaper2.settings.background_color, tL_wallPaper2.settings.second_background_color, tL_wallPaper2.settings.third_background_color, tL_wallPaper2.settings.fourth_background_color);
                                                        } else {
                                                            colorWallpaper = new WallpapersListActivity.ColorWallpaper("c", tL_wallPaper2.settings.background_color, tL_wallPaper2.settings.second_background_color, AndroidUtilities.getWallpaperRotation(tL_wallPaper2.settings.rotation, false));
                                                        }
                                                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda46(this, new ThemePreviewActivity(colorWallpaper, (Bitmap) null, true, false)));
                                                        ok = true;
                                                    } catch (Exception e) {
                                                        FileLog.e((Throwable) e);
                                                    }
                                                }
                                                if (!ok) {
                                                    TLRPC.TL_account_getWallPaper req8 = new TLRPC.TL_account_getWallPaper();
                                                    TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                                    inputWallPaperSlug.slug = tL_wallPaper2.slug;
                                                    req8.wallpaper = inputWallPaperSlug;
                                                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req8, new LaunchActivity$$ExternalSyntheticLambda62(this, progressDialog, tL_wallPaper2));
                                                }
                                            } else {
                                                String str14 = theme;
                                                if (str14 != null) {
                                                    cancelRunnable = new LaunchActivity$$ExternalSyntheticLambda19(this);
                                                    TLRPC.TL_account_getTheme req9 = new TLRPC.TL_account_getTheme();
                                                    req9.format = "android";
                                                    TLRPC.TL_inputThemeSlug inputThemeSlug = new TLRPC.TL_inputThemeSlug();
                                                    inputThemeSlug.slug = str14;
                                                    req9.theme = inputThemeSlug;
                                                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req9, new LaunchActivity$$ExternalSyntheticLambda60(this, progressDialog));
                                                } else {
                                                    Long l2 = channelId;
                                                    if (!(l2 == null || messageId == null)) {
                                                        if (threadId != null) {
                                                            TLRPC.Chat chat = MessagesController.getInstance(intentAccount).getChat(l2);
                                                            if (chat != null) {
                                                                requestId[0] = runCommentRequest(intentAccount, progressDialog, messageId, commentId, threadId, chat);
                                                            } else {
                                                                TLRPC.TL_channels_getChannels req10 = new TLRPC.TL_channels_getChannels();
                                                                TLRPC.TL_inputChannel inputChannel = new TLRPC.TL_inputChannel();
                                                                inputChannel.channel_id = channelId.longValue();
                                                                req10.id.add(inputChannel);
                                                                TLRPC.Chat chat2 = chat;
                                                                LaunchActivity$$ExternalSyntheticLambda65 launchActivity$$ExternalSyntheticLambda65 = r1;
                                                                TLRPC.TL_inputChannel tL_inputChannel = inputChannel;
                                                                LaunchActivity$$ExternalSyntheticLambda65 launchActivity$$ExternalSyntheticLambda652 = new LaunchActivity$$ExternalSyntheticLambda65(this, requestId, intentAccount, progressDialog, messageId, commentId, threadId);
                                                                requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req10, launchActivity$$ExternalSyntheticLambda65);
                                                            }
                                                        } else {
                                                            Bundle args2 = new Bundle();
                                                            args2.putLong("chat_id", channelId.longValue());
                                                            args2.putInt("message_id", messageId.intValue());
                                                            if (!mainFragmentsStack.isEmpty()) {
                                                                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                                                                baseFragment = arrayList.get(arrayList.size() - 1);
                                                            }
                                                            BaseFragment lastFragment = baseFragment;
                                                            if (lastFragment == null || MessagesController.getInstance(intentAccount).checkCanOpenChat(args2, lastFragment)) {
                                                                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda23(this, args2, channelId, requestId, progressDialog, lastFragment, intentAccount));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (!mainFragmentsStack.isEmpty()) {
                            TLRPC.TL_inputStickerSetShortName stickerset = new TLRPC.TL_inputStickerSetShortName();
                            stickerset.short_name = str10;
                            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                            BaseFragment fragment2 = arrayList2.get(arrayList2.size() - 1);
                            if (fragment2 instanceof ChatActivity) {
                                ChatActivity chatActivity = (ChatActivity) fragment2;
                                alert = new StickersAlert(this, fragment2, stickerset, (TLRPC.TL_messages_stickerSet) null, chatActivity.getChatActivityEnterViewForStickers(), chatActivity.getResourceProvider());
                                alert.setCalcMandatoryInsets(chatActivity.isKeyboardVisible());
                            } else {
                                alert = new StickersAlert((Context) this, fragment2, (TLRPC.InputStickerSet) stickerset, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                            }
                            fragment2.showDialog(alert);
                            return;
                        } else {
                            return;
                        }
                    }
                }
                if (requestId[0] != 0) {
                    progressDialog.setOnCancelListener(new LaunchActivity$$ExternalSyntheticLambda11(i, requestId, cancelRunnable));
                    try {
                        progressDialog.showDelayed(300);
                    } catch (Exception e2) {
                    }
                }
            }
        } else {
            AlertsCreator.createAccountSelectDialog(this, new LaunchActivity$$ExternalSyntheticLambda71(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, channelId, threadId, commentId, game, auth, lang, unsupportedUrl, code, loginToken, wallPaper, theme, voicechat, videoTimestamp)).show();
        }
    }

    /* renamed from: lambda$runLinkRequest$24$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3091lambda$runLinkRequest$24$orgtelegramuiLaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Long channelId, Integer threadId, Integer commentId, String game, HashMap auth, String lang, String unsupportedUrl, String code, String loginToken, TLRPC.TL_wallPaper wallPaper, String theme, String voicechat, int videoTimestamp, int account) {
        int i = account;
        if (i != intentAccount) {
            switchToAccount(i, true);
        }
        runLinkRequest(account, username, group, sticker, botUser, botChat, message, hasUrl, messageId, channelId, threadId, commentId, game, auth, lang, unsupportedUrl, code, loginToken, wallPaper, theme, voicechat, 1, videoTimestamp);
    }

    /* renamed from: lambda$runLinkRequest$28$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3095lambda$runLinkRequest$28$orgtelegramuiLaunchActivity(String game, String voicechat, int intentAccount, Integer messageId, Integer commentId, Integer threadId, int[] requestId, AlertDialog progressDialog, String botChat, String botUser, int videoTimestamp, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda29(this, response, error, game, voicechat, intentAccount, messageId, commentId, threadId, requestId, progressDialog, botChat, botUser, videoTimestamp), 2);
    }

    /* JADX WARNING: type inference failed for: r9v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r9v3 */
    /* JADX WARNING: type inference failed for: r9v5 */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0375 A[SYNTHETIC, Splitter:B:143:0x0375] */
    /* JADX WARNING: Removed duplicated region for block: B:152:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b3  */
    /* renamed from: lambda$runLinkRequest$27$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3094lambda$runLinkRequest$27$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r25, org.telegram.tgnet.TLRPC.TL_error r26, java.lang.String r27, java.lang.String r28, int r29, java.lang.Integer r30, java.lang.Integer r31, java.lang.Integer r32, int[] r33, org.telegram.ui.ActionBar.AlertDialog r34, java.lang.String r35, java.lang.String r36, int r37) {
        /*
            r24 = this;
            r8 = r24
            r9 = r26
            r10 = r27
            r11 = r28
            r12 = r29
            r13 = r35
            r14 = r36
            r15 = r37
            boolean r0 = r24.isFinishing()
            if (r0 != 0) goto L_0x0380
            r16 = 1
            r7 = r25
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r7 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r7
            r0 = 1
            if (r9 != 0) goto L_0x0314
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r8.actionBarLayout
            if (r1 == 0) goto L_0x0314
            if (r10 != 0) goto L_0x0027
            if (r11 == 0) goto L_0x003b
        L_0x0027:
            if (r10 == 0) goto L_0x0031
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r7.users
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x003b
        L_0x0031:
            if (r11 == 0) goto L_0x0314
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r7.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0314
        L_0x003b:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            r6 = 0
            r1.putUsers(r2, r6)
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r7.chats
            r1.putChats(r2, r6)
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r29)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r7.chats
            r1.putUsersAndChats(r2, r3, r6, r0)
            if (r30 == 0) goto L_0x00a0
            if (r31 != 0) goto L_0x0064
            if (r32 == 0) goto L_0x0060
            goto L_0x0064
        L_0x0060:
            r17 = r7
            r9 = 0
            goto L_0x00a3
        L_0x0064:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r7.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x00a0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r7.chats
            java.lang.Object r0 = r0.get(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            r1 = r24
            r2 = r29
            r3 = r34
            r4 = r30
            r5 = r31
            r9 = 0
            r6 = r32
            r17 = r7
            r7 = r0
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r33[r9] = r0
            r0 = r33[r9]
            if (r0 == 0) goto L_0x0098
            r16 = 0
            r2 = r26
            r1 = r34
            r4 = r17
            goto L_0x0373
        L_0x0098:
            r2 = r26
            r1 = r34
            r4 = r17
            goto L_0x0373
        L_0x00a0:
            r17 = r7
            r9 = 0
        L_0x00a3:
            java.lang.String r1 = "dialogsType"
            java.lang.String r2 = "onlySelect"
            if (r10 == 0) goto L_0x01b3
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putBoolean(r2, r0)
            java.lang.String r2 = "cantSendToChannels"
            r3.putBoolean(r2, r0)
            r3.putInt(r1, r0)
            r1 = 2131627709(0x7f0e0ebd, float:1.888269E38)
            java.lang.String r2 = "SendGameToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "selectAlertString"
            r3.putString(r2, r1)
            r1 = 2131627708(0x7f0e0ebc, float:1.8882688E38)
            java.lang.String r2 = "SendGameToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "selectAlertStringGroup"
            r3.putString(r2, r1)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80
            r4 = r17
            r2.<init>(r8, r10, r12, r4)
            r1.setDelegate(r2)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x010e
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x010b
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            int r5 = r5 - r0
            java.lang.Object r2 = r2.get(r5)
            boolean r2 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x010b
            r6 = 1
            goto L_0x010c
        L_0x010b:
            r6 = 0
        L_0x010c:
            r2 = r6
            goto L_0x0131
        L_0x010e:
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r2 = r2.size()
            if (r2 <= r0) goto L_0x012f
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            int r5 = r5 - r0
            java.lang.Object r2 = r2.get(r5)
            boolean r2 = r2 instanceof org.telegram.ui.DialogsActivity
            if (r2 == 0) goto L_0x012f
            r6 = 1
            goto L_0x0130
        L_0x012f:
            r6 = 0
        L_0x0130:
            r2 = r6
        L_0x0131:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r8.actionBarLayout
            r21 = 1
            r22 = 1
            r23 = 0
            r18 = r5
            r19 = r1
            r20 = r2
            r18.presentFragment(r19, r20, r21, r22, r23)
            boolean r5 = org.telegram.ui.SecretMediaViewer.hasInstance()
            if (r5 == 0) goto L_0x015a
            org.telegram.ui.SecretMediaViewer r5 = org.telegram.ui.SecretMediaViewer.getInstance()
            boolean r5 = r5.isVisible()
            if (r5 == 0) goto L_0x015a
            org.telegram.ui.SecretMediaViewer r5 = org.telegram.ui.SecretMediaViewer.getInstance()
            r5.closePhoto(r9, r9)
            goto L_0x0189
        L_0x015a:
            boolean r5 = org.telegram.ui.PhotoViewer.hasInstance()
            if (r5 == 0) goto L_0x0172
            org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.getInstance()
            boolean r5 = r5.isVisible()
            if (r5 == 0) goto L_0x0172
            org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.getInstance()
            r5.closePhoto(r9, r0)
            goto L_0x0189
        L_0x0172:
            boolean r5 = org.telegram.ui.ArticleViewer.hasInstance()
            if (r5 == 0) goto L_0x0189
            org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.getInstance()
            boolean r5 = r5.isVisible()
            if (r5 == 0) goto L_0x0189
            org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.getInstance()
            r5.close(r9, r0)
        L_0x0189:
            org.telegram.ui.GroupCallActivity r5 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r5 == 0) goto L_0x0192
            org.telegram.ui.GroupCallActivity r5 = org.telegram.ui.GroupCallActivity.groupCallInstance
            r5.dismiss()
        L_0x0192:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r8.drawerLayoutContainer
            r5.setAllowOpenDrawer(r9, r9)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x01a8
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.rightActionBarLayout
            r0.showLastFragment()
            goto L_0x01ad
        L_0x01a8:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r8.drawerLayoutContainer
            r5.setAllowOpenDrawer(r0, r9)
        L_0x01ad:
            r2 = r26
            r1 = r34
            goto L_0x0373
        L_0x01b3:
            r4 = r17
            if (r13 == 0) goto L_0x023f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x01c8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r4.users
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            goto L_0x01c9
        L_0x01c8:
            r3 = 0
        L_0x01c9:
            if (r3 == 0) goto L_0x0210
            boolean r5 = r3.bot
            if (r5 == 0) goto L_0x01d4
            boolean r5 = r3.bot_nochats
            if (r5 == 0) goto L_0x01d4
            goto L_0x0210
        L_0x01d4:
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            r5.putBoolean(r2, r0)
            r2 = 2
            r5.putInt(r1, r2)
            r1 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r3)
            r2[r9] = r6
            java.lang.String r6 = "%1$s"
            r2[r0] = r6
            java.lang.String r0 = "AddToTheGroupAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            java.lang.String r1 = "addToGroupAlertString"
            r5.putString(r1, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r5)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
            r1.<init>(r8, r12, r3, r13)
            r0.setDelegate(r1)
            r8.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(r0)
            r2 = r26
            r1 = r34
            goto L_0x0373
        L_0x0210:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x023a }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x023a }
            if (r1 != 0) goto L_0x0239
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x023a }
            int r2 = r1.size()     // Catch:{ Exception -> 0x023a }
            int r2 = r2 - r0
            java.lang.Object r0 = r1.get(r2)     // Catch:{ Exception -> 0x023a }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x023a }
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x023a }
            java.lang.String r1 = "BotCantJoinGroups"
            r2 = 2131624611(0x7f0e02a3, float:1.8876407E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x023a }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x023a }
            r0.show()     // Catch:{ Exception -> 0x023a }
        L_0x0239:
            goto L_0x023e
        L_0x023a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x023e:
            return
        L_0x023f:
            r1 = 0
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0268
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            java.lang.Object r5 = r5.get(r9)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            long r5 = r5.id
            java.lang.String r7 = "chat_id"
            r2.putLong(r7, r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r4.chats
            java.lang.Object r5 = r5.get(r9)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            long r5 = r5.id
            long r5 = -r5
            goto L_0x0281
        L_0x0268:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            java.lang.Object r5 = r5.get(r9)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC.User) r5
            long r5 = r5.id
            java.lang.String r7 = "user_id"
            r2.putLong(r7, r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r4.users
            java.lang.Object r5 = r5.get(r9)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC.User) r5
            long r5 = r5.id
        L_0x0281:
            if (r14 == 0) goto L_0x029d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r4.users
            int r7 = r7.size()
            if (r7 <= 0) goto L_0x029d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r4.users
            java.lang.Object r7 = r7.get(r9)
            org.telegram.tgnet.TLRPC$User r7 = (org.telegram.tgnet.TLRPC.User) r7
            boolean r7 = r7.bot
            if (r7 == 0) goto L_0x029d
            java.lang.String r7 = "botUser"
            r2.putString(r7, r14)
            r1 = 1
        L_0x029d:
            if (r30 == 0) goto L_0x02a8
            int r7 = r30.intValue()
            java.lang.String r3 = "message_id"
            r2.putInt(r3, r7)
        L_0x02a8:
            if (r11 == 0) goto L_0x02af
            java.lang.String r3 = "voicechat"
            r2.putString(r3, r11)
        L_0x02af:
            if (r15 < 0) goto L_0x02b6
            java.lang.String r3 = "video_timestamp"
            r2.putInt(r3, r15)
        L_0x02b6:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x02cf
            if (r11 != 0) goto L_0x02cf
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r7 = r3.size()
            int r7 = r7 - r0
            java.lang.Object r0 = r3.get(r7)
            r3 = r0
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            goto L_0x02d0
        L_0x02cf:
            r3 = 0
        L_0x02d0:
            r0 = r3
            if (r0 == 0) goto L_0x02dd
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r29)
            boolean r3 = r3.checkCanOpenChat(r2, r0)
            if (r3 == 0) goto L_0x02f4
        L_0x02dd:
            if (r1 == 0) goto L_0x02f7
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x02f7
            r3 = r0
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            long r17 = r3.getDialogId()
            int r3 = (r17 > r5 ? 1 : (r17 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x02f7
            r3 = r0
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            r3.setBotUser(r14)
        L_0x02f4:
            r1 = r34
            goto L_0x0311
        L_0x02f7:
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r29)
            if (r30 != 0) goto L_0x02ff
            r7 = 0
            goto L_0x0303
        L_0x02ff:
            int r7 = r30.intValue()
        L_0x0303:
            org.telegram.ui.LaunchActivity$11 r9 = new org.telegram.ui.LaunchActivity$11
            r17 = r1
            r1 = r34
            r9.<init>(r1, r2)
            r3.ensureMessagesLoaded(r5, r7, r9)
            r16 = 0
        L_0x0311:
            r2 = r26
            goto L_0x0373
        L_0x0314:
            r1 = r34
            r4 = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack     // Catch:{ Exception -> 0x036d }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x036d }
            if (r2 != 0) goto L_0x036a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack     // Catch:{ Exception -> 0x036d }
            int r3 = r2.size()     // Catch:{ Exception -> 0x036d }
            int r3 = r3 - r0
            java.lang.Object r0 = r2.get(r3)     // Catch:{ Exception -> 0x036d }
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0     // Catch:{ Exception -> 0x036d }
            r2 = r26
            if (r2 == 0) goto L_0x0353
            java.lang.String r3 = r2.text     // Catch:{ Exception -> 0x0368 }
            if (r3 == 0) goto L_0x0353
            java.lang.String r3 = r2.text     // Catch:{ Exception -> 0x0368 }
            java.lang.String r5 = "FLOOD_WAIT"
            boolean r3 = r3.startsWith(r5)     // Catch:{ Exception -> 0x0368 }
            if (r3 == 0) goto L_0x0353
            org.telegram.ui.Components.BulletinFactory r3 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x0368 }
            java.lang.String r5 = "FloodWait"
            r6 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ Exception -> 0x0368 }
            org.telegram.ui.Components.Bulletin r3 = r3.createErrorBulletin(r5)     // Catch:{ Exception -> 0x0368 }
            r3.show()     // Catch:{ Exception -> 0x0368 }
            goto L_0x036c
        L_0x0353:
            org.telegram.ui.Components.BulletinFactory r3 = org.telegram.ui.Components.BulletinFactory.of(r0)     // Catch:{ Exception -> 0x0368 }
            java.lang.String r5 = "NoUsernameFound"
            r6 = 2131626562(0x7f0e0a42, float:1.8880364E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ Exception -> 0x0368 }
            org.telegram.ui.Components.Bulletin r3 = r3.createErrorBulletin(r5)     // Catch:{ Exception -> 0x0368 }
            r3.show()     // Catch:{ Exception -> 0x0368 }
            goto L_0x036c
        L_0x0368:
            r0 = move-exception
            goto L_0x0370
        L_0x036a:
            r2 = r26
        L_0x036c:
            goto L_0x0373
        L_0x036d:
            r0 = move-exception
            r2 = r26
        L_0x0370:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0373:
            if (r16 == 0) goto L_0x0383
            r34.dismiss()     // Catch:{ Exception -> 0x0379 }
            goto L_0x0383
        L_0x0379:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0383
        L_0x0380:
            r1 = r34
            r2 = r9
        L_0x0383:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3094lambda$runLinkRequest$27$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, int):void");
    }

    /* renamed from: lambda$runLinkRequest$25$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3092lambda$runLinkRequest$25$orgtelegramuiLaunchActivity(String game, int intentAccount, TLRPC.TL_contacts_resolvedPeer res, DialogsActivity fragment1, ArrayList dids, CharSequence message1, boolean param) {
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

    /* renamed from: lambda$runLinkRequest$26$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3093lambda$runLinkRequest$26$orgtelegramuiLaunchActivity(int intentAccount, TLRPC.User user, String botChat, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putLong("chat_id", -did);
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(intentAccount);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (!instance.checkCanOpenChat(args12, arrayList.get(arrayList.size() - 1))) {
                return;
            }
        }
        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(intentAccount).addUserToChat(-did, user, 0, botChat, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(args12), true, false, true, false);
    }

    /* renamed from: lambda$runLinkRequest$31$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3097lambda$runLinkRequest$31$orgtelegramuiLaunchActivity(int intentAccount, AlertDialog progressDialog, String group, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda34(this, error, response, intentAccount, progressDialog, group));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0085, code lost:
        if (r1.checkCanOpenChat(r12, r2.get(r2.size() - 1)) != false) goto L_0x0087;
     */
    /* renamed from: lambda$runLinkRequest$30$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3096lambda$runLinkRequest$30$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLRPC.TL_error r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.ui.ActionBar.AlertDialog r21, java.lang.String r22) {
        /*
            r17 = this;
            r7 = r17
            r8 = r18
            boolean r0 = r17.isFinishing()
            if (r0 != 0) goto L_0x0145
            r0 = 1
            r1 = 0
            if (r8 != 0) goto L_0x00ce
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r7.actionBarLayout
            if (r2 == 0) goto L_0x00ce
            r9 = r19
            org.telegram.tgnet.TLRPC$ChatInvite r9 = (org.telegram.tgnet.TLRPC.ChatInvite) r9
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            r3 = 1
            if (r2 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r2)
            if (r2 == 0) goto L_0x003d
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = r2.kicked
            if (r2 != 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            java.lang.String r2 = r2.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x003d
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_chatInvitePeek
            if (r2 != 0) goto L_0x003d
            org.telegram.tgnet.TLRPC$Chat r2 = r9.chat
            boolean r2 = r2.has_geo
            if (r2 == 0) goto L_0x00b5
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda22 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda22
            r1.<init>(r13)
            r14 = r21
            r14.setOnCancelListener(r1)
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r20)
            org.telegram.tgnet.TLRPC$Chat r1 = r9.chat
            long r1 = r1.id
            long r5 = -r1
            org.telegram.ui.LaunchActivity$12 r4 = new org.telegram.ui.LaunchActivity$12
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
            r3 = r22
            goto L_0x00cc
        L_0x00b5:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r3
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            org.telegram.ui.Components.JoinGroupAlert r2 = new org.telegram.ui.Components.JoinGroupAlert
            r3 = r22
            r2.<init>(r7, r9, r3, r1)
            r1.showDialog(r2)
        L_0x00cc:
            r1 = r0
            goto L_0x0137
        L_0x00ce:
            r3 = r22
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>((android.content.Context) r7)
            r4 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r5 = "AppName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setTitle(r4)
            java.lang.String r4 = r8.text
            java.lang.String r5 = "FLOOD_WAIT"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x00f8
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r5 = "FloodWait"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setMessage(r4)
            goto L_0x0127
        L_0x00f8:
            java.lang.String r4 = r8.text
            java.lang.String r5 = "INVITE_HASH_EXPIRED"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x011b
            r4 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.String r5 = "ExpiredLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setTitle(r4)
            r4 = 2131626011(0x7f0e081b, float:1.8879246E38)
            java.lang.String r5 = "InviteExpired"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setMessage(r4)
            goto L_0x0127
        L_0x011b:
            r4 = 2131626068(0x7f0e0854, float:1.8879362E38)
            java.lang.String r5 = "JoinToGroupErrorNotExist"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setMessage(r4)
        L_0x0127:
            r4 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r5 = "OK"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setPositiveButton(r4, r1)
            r7.showAlertDialog(r2)
            r1 = r0
        L_0x0137:
            if (r1 == 0) goto L_0x0144
            r21.dismiss()     // Catch:{ Exception -> 0x013d }
            goto L_0x0144
        L_0x013d:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0147
        L_0x0144:
            goto L_0x0147
        L_0x0145:
            r3 = r22
        L_0x0147:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3096lambda$runLinkRequest$30$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$runLinkRequest$29(boolean[] canceled, DialogInterface dialog) {
        canceled[0] = true;
    }

    /* renamed from: lambda$runLinkRequest$33$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3099lambda$runLinkRequest$33$orgtelegramuiLaunchActivity(int intentAccount, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(intentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, progressDialog, error, response, intentAccount));
    }

    /* renamed from: lambda$runLinkRequest$32$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3098lambda$runLinkRequest$32$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response, int intentAccount) {
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

    /* renamed from: lambda$runLinkRequest$34$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3100lambda$runLinkRequest$34$orgtelegramuiLaunchActivity(boolean hasUrl, int intentAccount, String message, DialogsActivity fragment13, ArrayList dids, CharSequence m, boolean param) {
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

    /* renamed from: lambda$runLinkRequest$38$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3104lambda$runLinkRequest$38$orgtelegramuiLaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_account_authorizationForm authorizationForm = (TLRPC.TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda61(this, progressDialog, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
            TLRPC.TL_error tL_error = error;
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, progressDialog, error));
    }

    /* renamed from: lambda$runLinkRequest$36$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3102lambda$runLinkRequest$36$orgtelegramuiLaunchActivity(AlertDialog progressDialog, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda37(this, progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
    }

    /* renamed from: lambda$runLinkRequest$35$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3101lambda$runLinkRequest$35$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl) {
        TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response1 != null) {
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm.bot_id, tL_account_getAuthorizationForm.scope, tL_account_getAuthorizationForm.public_key, payload, nonce, callbackUrl, authorizationForm, (TLRPC.TL_account_password) response1));
            return;
        }
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = authorizationForm;
    }

    /* renamed from: lambda$runLinkRequest$37$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3103lambda$runLinkRequest$37$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error) {
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

    /* renamed from: lambda$runLinkRequest$40$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3106lambda$runLinkRequest$40$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda36(this, progressDialog, response));
    }

    /* renamed from: lambda$runLinkRequest$39$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3105lambda$runLinkRequest$39$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response) {
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

    /* renamed from: lambda$runLinkRequest$42$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3108lambda$runLinkRequest$42$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda38(this, progressDialog, response, error));
    }

    /* renamed from: lambda$runLinkRequest$41$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3107lambda$runLinkRequest$41$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
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

    /* renamed from: lambda$runLinkRequest$45$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3111lambda$runLinkRequest$45$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLRPC.TL_wallPaper wallPaper, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, progressDialog, response, wallPaper, error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$runLinkRequest$44$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3110lambda$runLinkRequest$44$orgtelegramuiLaunchActivity(org.telegram.ui.ActionBar.AlertDialog r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC.TL_wallPaper r19, org.telegram.tgnet.TLRPC.TL_error r20) {
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
            r1.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(r5)
            r4 = r20
            goto L_0x0090
        L_0x0068:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r4 = 2131625436(0x7f0e05dc, float:1.887808E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3110lambda$runLinkRequest$44$orgtelegramuiLaunchActivity(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$runLinkRequest$46$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3112lambda$runLinkRequest$46$orgtelegramuiLaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* renamed from: lambda$runLinkRequest$48$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3114lambda$runLinkRequest$48$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda30(this, response, progressDialog, error));
    }

    /* renamed from: lambda$runLinkRequest$47$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3113lambda$runLinkRequest$47$orgtelegramuiLaunchActivity(TLObject response, AlertDialog progressDialog, TLRPC.TL_error error) {
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
                        if (!FileLoader.getPathToAttach(object.document, true).exists()) {
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

    /* renamed from: lambda$runLinkRequest$50$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3116lambda$runLinkRequest$50$orgtelegramuiLaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, Integer messageId, Integer commentId, Integer threadId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda32(this, response, requestId, intentAccount, progressDialog, messageId, commentId, threadId));
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x003d A[SYNTHETIC, Splitter:B:7:0x003d] */
    /* renamed from: lambda$runLinkRequest$49$org-telegram-ui-LaunchActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3115lambda$runLinkRequest$49$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject r13, int[] r14, int r15, org.telegram.ui.ActionBar.AlertDialog r16, java.lang.Integer r17, java.lang.Integer r18, java.lang.Integer r19) {
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
            r0 = 2131626156(0x7f0e08ac, float:1.887954E38)
            java.lang.String r2 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r12, r0)
            r12.showAlertDialog(r0)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.m3115lambda$runLinkRequest$49$orgtelegramuiLaunchActivity(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* renamed from: lambda$runLinkRequest$53$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3119lambda$runLinkRequest$53$orgtelegramuiLaunchActivity(Bundle args, Long channelId, int[] requestId, AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount) {
        Bundle bundle = args;
        if (!this.actionBarLayout.presentFragment(new ChatActivity(args))) {
            TLRPC.TL_channels_getChannels req = new TLRPC.TL_channels_getChannels();
            TLRPC.TL_inputChannel inputChannel = new TLRPC.TL_inputChannel();
            inputChannel.channel_id = channelId.longValue();
            req.id.add(inputChannel);
            requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda63(this, progressDialog, lastFragment, intentAccount, args));
        }
    }

    /* renamed from: lambda$runLinkRequest$52$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3118lambda$runLinkRequest$52$orgtelegramuiLaunchActivity(AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount, Bundle args, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda40(this, progressDialog, response, lastFragment, intentAccount, args));
    }

    /* renamed from: lambda$runLinkRequest$51$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3117lambda$runLinkRequest$51$orgtelegramuiLaunchActivity(AlertDialog progressDialog, TLObject response, BaseFragment lastFragment, int intentAccount, Bundle args) {
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

    static /* synthetic */ void lambda$runLinkRequest$54(int intentAccount, int[] requestId, Runnable cancelRunnableFinal, DialogInterface dialog) {
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
            AnonymousClass13 r0 = new FrameLayout(this) {
                private int lastGradientWidth;
                private Matrix matrix = new Matrix();
                private Paint paint = new Paint();
                private LinearGradient updateGradient;

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (this.updateGradient != null) {
                        this.paint.setColor(-1);
                        this.paint.setShader(this.updateGradient);
                        this.updateGradient.setLocalMatrix(this.matrix);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
                        LaunchActivity.this.updateLayoutIcon.setBackgroundGradientDrawable(this.updateGradient);
                        LaunchActivity.this.updateLayoutIcon.draw(canvas);
                    }
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
                this.updateLayout.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21"), (String) null));
            }
            this.sideMenuContainer.addView(this.updateLayout, LayoutHelper.createFrame(-1, 44, 83));
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda4(this));
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

    /* renamed from: lambda$createUpdateUI$55$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3067lambda$createUpdateUI$55$orgtelegramuiLaunchActivity(View v) {
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
        boolean z = animated;
        if (this.sideMenuContainer != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                createUpdateUI();
                this.updateSizeTextView.setText(AndroidUtilities.formatFileSize((long) SharedConfig.pendingAppUpdate.document.size));
                String fileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                if (FileLoader.getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists()) {
                    this.updateLayoutIcon.setIcon(15, true, z);
                    this.updateTextView.setText(LocaleController.getString("AppUpdateNow", NUM));
                    showSize = false;
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.updateLayoutIcon.setIcon(3, true, z);
                    Float p = ImageLoader.getInstance().getFileProgress(fileName);
                    SimpleTextView simpleTextView = this.updateTextView;
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf((int) ((p != null ? p.floatValue() : 0.0f) * 100.0f));
                    simpleTextView.setText(LocaleController.formatString("AppUpdateDownloading", NUM, objArr));
                    showSize = false;
                } else {
                    this.updateLayoutIcon.setIcon(2, true, z);
                    this.updateTextView.setText(LocaleController.getString("AppUpdate", NUM));
                    showSize = true;
                }
                if (showSize) {
                    if (this.updateSizeTextView.getTag() != null) {
                        if (z) {
                            this.updateSizeTextView.setTag((Object) null);
                            this.updateSizeTextView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180).start();
                        } else {
                            this.updateSizeTextView.setAlpha(1.0f);
                            this.updateSizeTextView.setScaleX(1.0f);
                            this.updateSizeTextView.setScaleY(1.0f);
                        }
                    }
                } else if (this.updateSizeTextView.getTag() == null) {
                    if (z) {
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
                    if (z) {
                        this.updateLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener((Animator.AnimatorListener) null).setDuration(180).start();
                    } else {
                        this.updateLayout.setTranslationY(0.0f);
                    }
                    this.sideMenu.setPadding(0, 0, 0, AndroidUtilities.dp(44.0f));
                    return;
                }
                return;
            }
            FrameLayout frameLayout2 = this.updateLayout;
            if (frameLayout2 != null && frameLayout2.getTag() != null) {
                this.updateLayout.setTag((Object) null);
                if (z) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$ExternalSyntheticLambda51(this, this.currentAccount));
        }
    }

    /* renamed from: lambda$checkAppUpdate$57$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3064lambda$checkAppUpdate$57$orgtelegramuiLaunchActivity(int accountNum, TLObject response, TLRPC.TL_error error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (response instanceof TLRPC.TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda35(this, (TLRPC.TL_help_appUpdate) response, accountNum));
        }
    }

    /* renamed from: lambda$checkAppUpdate$56$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3063lambda$checkAppUpdate$56$orgtelegramuiLaunchActivity(TLRPC.TL_help_appUpdate res, int accountNum) {
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
            this.visibleDialog.setOnDismissListener(new LaunchActivity$$ExternalSyntheticLambda3(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* renamed from: lambda$showAlertDialog$58$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3120lambda$showAlertDialog$58$orgtelegramuiLaunchActivity(DialogInterface dialog) {
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

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        ChatActivity fragment;
        int attachesCount;
        int i;
        int i2;
        DialogsActivity dialogsActivity = dialogsFragment;
        ArrayList<Long> arrayList = dids;
        int account = dialogsActivity != null ? dialogsFragment.getCurrentAccount() : this.currentAccount;
        if (this.exportingChatUri != null) {
            Uri uri = this.exportingChatUri;
            ArrayList<Uri> documentsUris = this.documentsUrisArray != null ? new ArrayList<>(this.documentsUrisArray) : null;
            AlertDialog progressDialog = new AlertDialog(this, 3);
            SendMessagesHelper.getInstance(account).prepareImportHistory(arrayList.get(0).longValue(), this.exportingChatUri, this.documentsUrisArray, new LaunchActivity$$ExternalSyntheticLambda49(this, account, dialogsFragment, param, documentsUris, uri, progressDialog));
            try {
                progressDialog.showDelayed(300);
            } catch (Exception e) {
            }
        } else {
            if (dids.size() <= 1) {
                long did = arrayList.get(0).longValue();
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
            ArrayList<TLRPC.User> arrayList2 = this.contactsToSend;
            if (arrayList2 != null) {
                attachesCount2 = 0 + arrayList2.size();
            }
            if (this.videoPath != null) {
                attachesCount2++;
            }
            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList3 = this.photoPathsArray;
            if (arrayList3 != null) {
                attachesCount2 += arrayList3.size();
            }
            ArrayList<String> arrayList4 = this.documentsPathsArray;
            if (arrayList4 != null) {
                attachesCount2 += arrayList4.size();
            }
            ArrayList<Uri> arrayList5 = this.documentsUrisArray;
            if (arrayList5 != null) {
                attachesCount2 += arrayList5.size();
            }
            if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
                attachesCount = attachesCount2 + 1;
            } else {
                attachesCount = attachesCount2;
            }
            int i3 = 0;
            while (i3 < dids.size()) {
                if (!AlertsCreator.checkSlowMode(this, this.currentAccount, arrayList.get(i3).longValue(), attachesCount > 1)) {
                    i3++;
                } else {
                    return;
                }
            }
            ArrayList<TLRPC.User> arrayList6 = this.contactsToSend;
            if (arrayList6 == null || arrayList6.size() != 1 || mainFragmentsStack.isEmpty()) {
                String captionToSend = null;
                int i4 = 0;
                while (i4 < dids.size()) {
                    long did2 = arrayList.get(i4).longValue();
                    AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                    if (fragment != null) {
                        i2 = 1024;
                        i = i4;
                        this.actionBarLayout.presentFragment(fragment, dialogsActivity != null, dialogsActivity == null, true, false);
                        String str = this.videoPath;
                        if (str != null) {
                            fragment.openVideoEditor(str, this.sendingText);
                            this.sendingText = null;
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
                            ArrayList<String> arrayList7 = new ArrayList<>();
                            arrayList7.add(this.videoPath);
                            SendMessagesHelper.prepareSendingDocuments(accountInstance, arrayList7, arrayList7, (ArrayList<Uri>) null, captionToSend, (String) null, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                        }
                    }
                    if (this.photoPathsArray != null) {
                        String str3 = this.sendingText;
                        if (str3 != null && str3.length() <= i2 && this.photoPathsArray.size() == 1) {
                            this.photoPathsArray.get(0).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingMedia(accountInstance, this.photoPathsArray, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, true, 0);
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        String str4 = this.sendingText;
                        if (str4 != null && str4.length() <= i2) {
                            ArrayList<String> arrayList8 = this.documentsPathsArray;
                            int size = arrayList8 != null ? arrayList8.size() : 0;
                            ArrayList<Uri> arrayList9 = this.documentsUrisArray;
                            if (size + (arrayList9 != null ? arrayList9.size() : 0) == 1) {
                                captionToSend = this.sendingText;
                                this.sendingText = null;
                            }
                        }
                        SendMessagesHelper.prepareSendingDocuments(accountInstance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, captionToSend, this.documentsMimeType, did2, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                    }
                    String str5 = this.sendingText;
                    if (str5 != null) {
                        SendMessagesHelper.prepareSendingText(accountInstance, str5, did2, true, 0);
                    }
                    ArrayList<TLRPC.User> arrayList10 = this.contactsToSend;
                    if (arrayList10 != null && !arrayList10.isEmpty()) {
                        for (int a = 0; a < this.contactsToSend.size(); a++) {
                            SendMessagesHelper.getInstance(account).sendMessage(this.contactsToSend.get(a), did2, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                    }
                    if (TextUtils.isEmpty(message) == 0) {
                        SendMessagesHelper.prepareSendingText(accountInstance, message.toString(), did2, true, 0);
                    }
                    i4 = i + 1;
                }
                int i5 = i4;
            } else {
                ArrayList<BaseFragment> arrayList11 = mainFragmentsStack;
                PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(arrayList11.get(arrayList11.size() - 1), (ContactsController.Contact) null, (TLRPC.User) null, this.contactsToSendUri, (File) null, (String) null, (String) null);
                phonebookShareAlert.setDelegate(new LaunchActivity$$ExternalSyntheticLambda72(this, fragment, arrayList, account));
                ArrayList<BaseFragment> arrayList12 = mainFragmentsStack;
                arrayList12.get(arrayList12.size() - 1).showDialog(phonebookShareAlert);
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

    /* renamed from: lambda$didSelectDialogs$59$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3074lambda$didSelectDialogs$59$orgtelegramuiLaunchActivity(int account, DialogsActivity dialogsFragment, boolean param, ArrayList documentsUris, Uri uri, AlertDialog progressDialog, long result) {
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

    /* renamed from: lambda$didSelectDialogs$60$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3075lambda$didSelectDialogs$60$orgtelegramuiLaunchActivity(ChatActivity fragment, ArrayList dids, int account, TLRPC.User user, boolean notify, int scheduleDate) {
        if (fragment != null) {
            this.actionBarLayout.presentFragment(fragment, true, false, true, false);
        }
        for (int i = 0; i < dids.size(); i++) {
            SendMessagesHelper.getInstance(account).sendMessage(user, ((Long) dids.get(i)).longValue(), (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
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
    public void m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(BaseFragment fragment) {
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
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
            if (canDrawOverlays) {
                if (GroupCallActivity.groupCallInstance != null) {
                    GroupCallActivity.groupCallInstance.dismissInternal();
                }
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda18(this), 200);
            }
        }
    }

    /* renamed from: lambda$onActivityResult$61$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3083lambda$onActivityResult$61$orgtelegramuiLaunchActivity() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null) {
            grantResults = new int[0];
        }
        if (permissions == null) {
            permissions = new String[0];
        }
        boolean granted = grantResults.length > 0 && grantResults[0] == 0;
        if (requestCode == 104) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("VoipNeedCameraPermission", NUM));
            } else if (GroupCallActivity.groupCallInstance != null) {
                GroupCallActivity.groupCallInstance.enableCamera();
            }
        } else if (requestCode == 4) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionStorage", NUM));
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (requestCode == 5) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionContacts", NUM));
                return;
            }
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
        } else if (requestCode == 3) {
            boolean audioGranted = true;
            boolean cameraGranted = true;
            int size = Math.min(permissions.length, grantResults.length);
            for (int i = 0; i < size; i++) {
                if ("android.permission.RECORD_AUDIO".equals(permissions[i])) {
                    audioGranted = grantResults[i] == 0;
                } else if ("android.permission.CAMERA".equals(permissions[i])) {
                    cameraGranted = grantResults[i] == 0;
                }
            }
            if (!audioGranted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoAudio", NUM));
            } else if (!cameraGranted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", NUM));
            } else if (SharedConfig.inappCamera) {
                CameraController.getInstance().initCamera((Runnable) null);
                return;
            } else {
                return;
            }
        } else if (requestCode == 18 || requestCode == 19 || requestCode == 20 || requestCode == 22) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", NUM));
            }
        } else if (requestCode == 2 && granted) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
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
    }

    private void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(message);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new LaunchActivity$$ExternalSyntheticLambda85(this));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* renamed from: lambda$showPermissionErrorAlert$62$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3129xCLASSNAMEa5b59(DialogInterface dialog, int which) {
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
        Utilities.stageQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda10(this.currentAccount));
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

    static /* synthetic */ void lambda$onPause$63(int account) {
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
    public void onResume() {
        MessageObject messageObject;
        super.onResume();
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
        checkWasMutedByAdmin(true);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(LaunchActivity$$ExternalSyntheticLambda47.INSTANCE);
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

    static /* synthetic */ void lambda$onResume$64() {
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

    /* JADX WARNING: type inference failed for: r27v0, types: [java.lang.Object[]] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0590  */
    /* JADX WARNING: Removed duplicated region for block: B:385:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r25, int r26, java.lang.Object... r27) {
        /*
            r24 = this;
            r8 = r24
            r9 = r25
            r10 = r26
            r11 = r27
            int r0 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r9 != r0) goto L_0x0011
            r24.switchToAvailableAccountOrLogout()
            goto L_0x08f5
        L_0x0011:
            int r0 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r1 = 0
            if (r9 != r0) goto L_0x0022
            r0 = r11[r1]
            if (r0 == r8) goto L_0x08f5
            r24.onFinish()
            r24.finish()
            goto L_0x08f5
        L_0x0022:
            int r0 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r9 != r0) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r26)
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
            goto L_0x08f5
        L_0x0051:
            int r0 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r9 != r0) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r8.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x08f5
        L_0x005c:
            int r0 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r3 = "Cancel"
            r4 = 5
            r5 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r6 = "AppName"
            r7 = 4
            r12 = 3
            java.lang.String r14 = "OK"
            r15 = 2
            r2 = 1
            if (r9 != r0) goto L_0x01aa
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r1 = r0.intValue()
            r13 = 6
            if (r1 == r13) goto L_0x01a9
            int r1 = r0.intValue()
            if (r1 != r12) goto L_0x0085
            org.telegram.ui.ActionBar.AlertDialog r1 = r8.proxyErrorDialog
            if (r1 == 0) goto L_0x0085
            goto L_0x01a9
        L_0x0085:
            int r1 = r0.intValue()
            if (r1 != r7) goto L_0x0093
            r1 = r11[r2]
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r1 = (org.telegram.tgnet.TLRPC.TL_help_termsOfService) r1
            r8.showTosActivity(r10, r1)
            return
        L_0x0093:
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r8)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setTitle(r5)
            int r5 = r0.intValue()
            if (r5 == r15) goto L_0x00bc
            int r5 = r0.intValue()
            if (r5 == r12) goto L_0x00bc
            r5 = 2131626434(0x7f0e09c2, float:1.8880104E38)
            java.lang.String r6 = "MoreInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda33 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda33
            r6.<init>(r10)
            r1.setNegativeButton(r5, r6)
        L_0x00bc:
            int r5 = r0.intValue()
            if (r5 != r4) goto L_0x00db
            r3 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r4 = "NobodyLikesSpam3"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r3)
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r4 = 0
            r1.setPositiveButton(r3, r4)
            goto L_0x018b
        L_0x00db:
            int r4 = r0.intValue()
            if (r4 != 0) goto L_0x00fa
            r3 = 2131626565(0x7f0e0a45, float:1.888037E38)
            java.lang.String r4 = "NobodyLikesSpam1"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r3)
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r4 = 0
            r1.setPositiveButton(r3, r4)
            goto L_0x018b
        L_0x00fa:
            int r4 = r0.intValue()
            if (r4 != r2) goto L_0x0118
            r3 = 2131626566(0x7f0e0a46, float:1.8880372E38)
            java.lang.String r4 = "NobodyLikesSpam2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r3)
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r4 = 0
            r1.setPositiveButton(r3, r4)
            goto L_0x018b
        L_0x0118:
            int r4 = r0.intValue()
            if (r4 != r15) goto L_0x015a
            r4 = r11[r2]
            java.lang.String r4 = (java.lang.String) r4
            r1.setMessage(r4)
            r4 = r11[r15]
            java.lang.String r4 = (java.lang.String) r4
            java.lang.String r5 = "AUTH_KEY_DROP_"
            boolean r5 = r4.startsWith(r5)
            if (r5 == 0) goto L_0x014e
            r5 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)
            r5 = 0
            r1.setPositiveButton(r3, r5)
            r3 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            java.lang.String r5 = "LogOut"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84
            r5.<init>(r8)
            r1.setNegativeButton(r3, r5)
            goto L_0x018a
        L_0x014e:
            r5 = 0
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r1.setPositiveButton(r3, r5)
            goto L_0x018a
        L_0x015a:
            int r3 = r0.intValue()
            if (r3 != r12) goto L_0x018a
            r2 = 2131627370(0x7f0e0d6a, float:1.8882003E38)
            java.lang.String r3 = "Proxy"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            r2 = 2131628266(0x7f0e10ea, float:1.888382E38)
            java.lang.String r3 = "UseProxyTelegramError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)
            r3 = 0
            r1.setPositiveButton(r2, r3)
            org.telegram.ui.ActionBar.AlertDialog r2 = r8.showAlertDialog(r1)
            r8.proxyErrorDialog = r2
            return
        L_0x018a:
        L_0x018b:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x01a7
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r2 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.create()
            r2.showDialog(r3)
        L_0x01a7:
            goto L_0x08f5
        L_0x01a9:
            return
        L_0x01aa:
            int r0 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation
            if (r9 != r0) goto L_0x0204
            r0 = r11[r1]
            java.util.HashMap r0 = (java.util.HashMap) r0
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r8)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setTitle(r3)
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r4 = 0
            r1.setPositiveButton(r3, r4)
            r3 = 2131627811(0x7f0e0var_, float:1.8882897E38)
            java.lang.String r4 = "ShareYouLocationUnableManually"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1
            r4.<init>(r8, r0, r10)
            r1.setNegativeButton(r3, r4)
            r3 = 2131627810(0x7f0e0var_, float:1.8882895E38)
            java.lang.String r4 = "ShareYouLocationUnable"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0202
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r2 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.create()
            r2.showDialog(r3)
        L_0x0202:
            goto L_0x08f5
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
            if (r0 == 0) goto L_0x08f5
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            goto L_0x08f5
        L_0x0226:
            int r0 = org.telegram.messenger.NotificationCenter.didSetPasscode
            if (r9 != r0) goto L_0x025a
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash
            int r0 = r0.length()
            r1 = 8192(0x2000, float:1.14794E-41)
            if (r0 <= 0) goto L_0x0246
            boolean r0 = org.telegram.messenger.SharedConfig.allowScreenCapture
            if (r0 != 0) goto L_0x0246
            android.view.Window r0 = r24.getWindow()     // Catch:{ Exception -> 0x0240 }
            r0.setFlags(r1, r1)     // Catch:{ Exception -> 0x0240 }
            goto L_0x0244
        L_0x0240:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0244:
            goto L_0x08f5
        L_0x0246:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x08f5
            android.view.Window r0 = r24.getWindow()     // Catch:{ Exception -> 0x0254 }
            r0.clearFlags(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0258
        L_0x0254:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0258:
            goto L_0x08f5
        L_0x025a:
            int r0 = org.telegram.messenger.NotificationCenter.reloadInterface
            if (r9 != r0) goto L_0x0292
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 <= r2) goto L_0x0276
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r3 = r0.size()
            int r3 = r3 - r2
            java.lang.Object r0 = r0.get(r3)
            boolean r0 = r0 instanceof org.telegram.ui.ProfileActivity
            if (r0 == 0) goto L_0x0276
            r1 = 1
        L_0x0276:
            r0 = r1
            if (r0 == 0) goto L_0x028d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ProfileActivity r1 = (org.telegram.ui.ProfileActivity) r1
            boolean r2 = r1.isSettings()
            if (r2 != 0) goto L_0x028d
            r0 = 0
        L_0x028d:
            r8.rebuildAllFragments(r0)
            goto L_0x08f5
        L_0x0292:
            int r0 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r9 != r0) goto L_0x029b
            r8.showLanguageAlert(r1)
            goto L_0x08f5
        L_0x029b:
            int r0 = org.telegram.messenger.NotificationCenter.openArticle
            if (r9 != r0) goto L_0x02cd
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02a8
            return
        L_0x02a8:
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r2
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r0.setParentActivity(r8, r3)
            org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.getInstance()
            r1 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_webPage r1 = (org.telegram.tgnet.TLRPC.TL_webPage) r1
            r2 = r11[r2]
            java.lang.String r2 = (java.lang.String) r2
            r0.open(r1, r2)
            goto L_0x08f5
        L_0x02cd:
            int r0 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r9 != r0) goto L_0x035e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            if (r0 == 0) goto L_0x035d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02df
            goto L_0x035d
        L_0x02df:
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r4 = r11[r2]
            java.util.HashMap r4 = (java.util.HashMap) r4
            r5 = r11[r15]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            r6 = r11[r12]
            java.lang.Boolean r6 = (java.lang.Boolean) r6
            boolean r6 = r6.booleanValue()
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = r7.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r8.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r12 = r12.fragmentsStack
            int r12 = r12.size()
            int r12 = r12 - r2
            java.lang.Object r2 = r7.get(r12)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r7.<init>((android.content.Context) r8)
            r12 = 2131628233(0x7f0e10c9, float:1.8883753E38)
            java.lang.String r13 = "UpdateContactsTitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r7.setTitle(r12)
            r12 = 2131628232(0x7f0e10c8, float:1.888375E38)
            java.lang.String r13 = "UpdateContactsMessage"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r7.setMessage(r12)
            r12 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda44 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda44
            r13.<init>(r10, r4, r5, r6)
            r7.setPositiveButton(r12, r13)
            r12 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda55 r12 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda55
            r12.<init>(r10, r4, r5, r6)
            r7.setNegativeButton(r3, r12)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda66 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda66
            r3.<init>(r10, r4, r5, r6)
            r7.setOnBackButtonListener(r3)
            org.telegram.ui.ActionBar.AlertDialog r3 = r7.create()
            r2.showDialog(r3)
            r3.setCanceledOnTouchOutside(r1)
            goto L_0x08f5
        L_0x035d:
            return
        L_0x035e:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r3 = 21
            if (r9 != r0) goto L_0x03cd
            r0 = r11[r1]
            r1 = r0
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r0 = r1.booleanValue()
            if (r0 != 0) goto L_0x03b1
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0399
            java.lang.String r4 = "chats_menuBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setGlowColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            java.lang.String r4 = "listSelectorSDK21"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setListSelectorColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x0399:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x03b1
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x03b0 }
            java.lang.String r3 = "actionBarDefault"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)     // Catch:{ Exception -> 0x03b0 }
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r4
            r4 = 0
            r0.<init>(r4, r4, r3)     // Catch:{ Exception -> 0x03b0 }
            r8.setTaskDescription(r0)     // Catch:{ Exception -> 0x03b0 }
            goto L_0x03b1
        L_0x03b0:
            r0 = move-exception
        L_0x03b1:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            java.lang.String r3 = "windowBackgroundWhite"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBehindKeyboardColor(r3)
            r0 = 1
            int r3 = r11.length
            if (r3 <= r2) goto L_0x03c8
            r3 = r11[r2]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r0 = r3.booleanValue()
        L_0x03c8:
            r8.checkSystemBarColors(r2, r0)
            goto L_0x08f5
        L_0x03cd:
            int r0 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r9 != r0) goto L_0x059c
            r5 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x0568
            r0 = r11[r15]
            if (r0 == 0) goto L_0x0568
            android.widget.ImageView r0 = r8.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03e3
            return
        L_0x03e3:
            r0 = r11[r15]     // Catch:{ all -> 0x054a }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x054a }
            r3 = r11[r7]     // Catch:{ all -> 0x054a }
            java.lang.Boolean r3 = (java.lang.Boolean) r3     // Catch:{ all -> 0x054a }
            boolean r3 = r3.booleanValue()     // Catch:{ all -> 0x054a }
            r4 = r11[r4]     // Catch:{ all -> 0x054a }
            org.telegram.ui.Components.RLottieImageView r4 = (org.telegram.ui.Components.RLottieImageView) r4     // Catch:{ all -> 0x054a }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x054a }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r13 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            int r13 = r13.getMeasuredHeight()     // Catch:{ all -> 0x054a }
            if (r3 != 0) goto L_0x040a
            r4.setVisibility(r7)     // Catch:{ all -> 0x0405 }
            goto L_0x040a
        L_0x0405:
            r0 = move-exception
            r20 = r5
            goto L_0x054d
        L_0x040a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            int r7 = r7.getMeasuredWidth()     // Catch:{ all -> 0x054a }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r14 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            int r14 = r14.getMeasuredHeight()     // Catch:{ all -> 0x054a }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x054a }
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r7, r14, r15)     // Catch:{ all -> 0x054a }
            android.graphics.Canvas r14 = new android.graphics.Canvas     // Catch:{ all -> 0x054a }
            r14.<init>(r7)     // Catch:{ all -> 0x054a }
            java.util.HashMap r15 = new java.util.HashMap     // Catch:{ all -> 0x054a }
            r15.<init>()     // Catch:{ all -> 0x054a }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            r8.invalidateCachedViews(r12)     // Catch:{ all -> 0x054a }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r12 = r8.drawerLayoutContainer     // Catch:{ all -> 0x054a }
            r12.draw(r14)     // Catch:{ all -> 0x054a }
            android.widget.FrameLayout r12 = r8.frameLayout     // Catch:{ all -> 0x054a }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ all -> 0x054a }
            r12.removeView(r2)     // Catch:{ all -> 0x054a }
            r12 = -1
            if (r3 == 0) goto L_0x0452
            android.widget.FrameLayout r1 = r8.frameLayout     // Catch:{ all -> 0x054a }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ all -> 0x054a }
            r20 = r5
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r5)     // Catch:{ all -> 0x0548 }
            r12 = 0
            r1.addView(r2, r12, r5)     // Catch:{ all -> 0x0548 }
            android.view.View r1 = r8.themeSwitchSunView     // Catch:{ all -> 0x0548 }
            r2 = 8
            r1.setVisibility(r2)     // Catch:{ all -> 0x0548 }
            goto L_0x048b
        L_0x0452:
            r20 = r5
            android.widget.FrameLayout r1 = r8.frameLayout     // Catch:{ all -> 0x0548 }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ all -> 0x0548 }
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r5)     // Catch:{ all -> 0x0548 }
            r12 = 1
            r1.addView(r2, r12, r5)     // Catch:{ all -> 0x0548 }
            android.view.View r1 = r8.themeSwitchSunView     // Catch:{ all -> 0x0548 }
            r2 = 0
            r5 = r0[r2]     // Catch:{ all -> 0x0548 }
            r2 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0548 }
            int r5 = r5 - r12
            float r5 = (float) r5     // Catch:{ all -> 0x0548 }
            r1.setTranslationX(r5)     // Catch:{ all -> 0x0548 }
            android.view.View r1 = r8.themeSwitchSunView     // Catch:{ all -> 0x0548 }
            r5 = 1
            r12 = r0[r5]     // Catch:{ all -> 0x0548 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0548 }
            int r12 = r12 - r2
            float r2 = (float) r12     // Catch:{ all -> 0x0548 }
            r1.setTranslationY(r2)     // Catch:{ all -> 0x0548 }
            android.view.View r1 = r8.themeSwitchSunView     // Catch:{ all -> 0x0548 }
            r2 = 0
            r1.setVisibility(r2)     // Catch:{ all -> 0x0548 }
            android.view.View r1 = r8.themeSwitchSunView     // Catch:{ all -> 0x0548 }
            r1.invalidate()     // Catch:{ all -> 0x0548 }
        L_0x048b:
            android.widget.ImageView r1 = r8.themeSwitchImageView     // Catch:{ all -> 0x0548 }
            r1.setImageBitmap(r7)     // Catch:{ all -> 0x0548 }
            android.widget.ImageView r1 = r8.themeSwitchImageView     // Catch:{ all -> 0x0548 }
            r2 = 0
            r1.setVisibility(r2)     // Catch:{ all -> 0x0548 }
            org.telegram.ui.Components.RLottieDrawable r1 = r4.getAnimatedDrawable()     // Catch:{ all -> 0x0548 }
            r8.themeSwitchSunDrawable = r1     // Catch:{ all -> 0x0548 }
            r1 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r1 = r6 - r1
            r5 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r2 = r6 - r5
            int r1 = r1 * r2
            r2 = 1
            r5 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r5 = r13 - r5
            r12 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r2 = r13 - r12
            int r5 = r5 * r2
            int r1 = r1 + r5
            double r1 = (double) r1     // Catch:{ all -> 0x0548 }
            double r1 = java.lang.Math.sqrt(r1)     // Catch:{ all -> 0x0548 }
            r5 = 0
            r12 = r0[r5]     // Catch:{ all -> 0x0548 }
            r19 = r0[r5]     // Catch:{ all -> 0x0548 }
            int r12 = r12 * r19
            r5 = 1
            r18 = r0[r5]     // Catch:{ all -> 0x0548 }
            int r19 = r13 - r18
            r21 = r0[r5]     // Catch:{ all -> 0x0548 }
            int r5 = r13 - r21
            int r19 = r19 * r5
            int r12 = r12 + r19
            r5 = r13
            double r12 = (double) r12     // Catch:{ all -> 0x0548 }
            double r12 = java.lang.Math.sqrt(r12)     // Catch:{ all -> 0x0548 }
            double r1 = java.lang.Math.max(r1, r12)     // Catch:{ all -> 0x0548 }
            float r1 = (float) r1     // Catch:{ all -> 0x0548 }
            r2 = 0
            r12 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r12 = r6 - r12
            r13 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r2 = r6 - r13
            int r12 = r12 * r2
            r2 = 1
            r13 = r0[r2]     // Catch:{ all -> 0x0548 }
            r19 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r13 = r13 * r19
            int r12 = r12 + r13
            double r12 = (double) r12     // Catch:{ all -> 0x0548 }
            double r12 = java.lang.Math.sqrt(r12)     // Catch:{ all -> 0x0548 }
            r2 = 0
            r19 = r0[r2]     // Catch:{ all -> 0x0548 }
            r21 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r19 = r19 * r21
            r2 = 1
            r21 = r0[r2]     // Catch:{ all -> 0x0548 }
            r22 = r0[r2]     // Catch:{ all -> 0x0548 }
            int r21 = r21 * r22
            int r2 = r19 + r21
            r21 = r5
            r19 = r6
            double r5 = (double) r2     // Catch:{ all -> 0x0548 }
            double r5 = java.lang.Math.sqrt(r5)     // Catch:{ all -> 0x0548 }
            double r5 = java.lang.Math.max(r12, r5)     // Catch:{ all -> 0x0548 }
            float r2 = (float) r5     // Catch:{ all -> 0x0548 }
            float r5 = java.lang.Math.max(r1, r2)     // Catch:{ all -> 0x0548 }
            r1 = r5
            if (r3 == 0) goto L_0x0516
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0548 }
            goto L_0x0518
        L_0x0516:
            android.widget.ImageView r5 = r8.themeSwitchImageView     // Catch:{ all -> 0x0548 }
        L_0x0518:
            r6 = 0
            r12 = r0[r6]     // Catch:{ all -> 0x0548 }
            r6 = 1
            r13 = r0[r6]     // Catch:{ all -> 0x0548 }
            if (r3 == 0) goto L_0x0522
            r6 = 0
            goto L_0x0523
        L_0x0522:
            r6 = r1
        L_0x0523:
            if (r3 == 0) goto L_0x0529
            r23 = r0
            r0 = r1
            goto L_0x052c
        L_0x0529:
            r23 = r0
            r0 = 0
        L_0x052c:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r5, r12, r13, r6, r0)     // Catch:{ all -> 0x0548 }
            r5 = 400(0x190, double:1.976E-321)
            r0.setDuration(r5)     // Catch:{ all -> 0x0548 }
            android.view.animation.Interpolator r5 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x0548 }
            r0.setInterpolator(r5)     // Catch:{ all -> 0x0548 }
            org.telegram.ui.LaunchActivity$15 r5 = new org.telegram.ui.LaunchActivity$15     // Catch:{ all -> 0x0548 }
            r5.<init>(r3, r4)     // Catch:{ all -> 0x0548 }
            r0.addListener(r5)     // Catch:{ all -> 0x0548 }
            r0.start()     // Catch:{ all -> 0x0548 }
            r5 = 1
            r1 = 0
            goto L_0x056f
        L_0x0548:
            r0 = move-exception
            goto L_0x054d
        L_0x054a:
            r0 = move-exception
            r20 = r5
        L_0x054d:
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            android.widget.ImageView r0 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x0562 }
            r2 = 0
            r0.setImageDrawable(r2)     // Catch:{ Exception -> 0x0562 }
            android.widget.FrameLayout r0 = r8.frameLayout     // Catch:{ Exception -> 0x0562 }
            android.widget.ImageView r2 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x0562 }
            r0.removeView(r2)     // Catch:{ Exception -> 0x0562 }
            r2 = 0
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r2     // Catch:{ Exception -> 0x0562 }
            goto L_0x0566
        L_0x0562:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0566:
            r1 = 0
            goto L_0x056d
        L_0x0568:
            r20 = r5
            r1 = 0
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r1
        L_0x056d:
            r5 = r20
        L_0x056f:
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
            if (r3 == 0) goto L_0x059a
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.layersActionBarLayout
            r3.animateThemedValues(r0, r2, r1, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r8.rightActionBarLayout
            r3.animateThemedValues(r0, r2, r1, r5)
        L_0x059a:
            goto L_0x08f5
        L_0x059c:
            int r0 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r9 != r0) goto L_0x05d0
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x08f5
            r1 = 0
            r1 = r11[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r0 = r0.getChildCount()
            r2 = 0
        L_0x05ae:
            if (r2 >= r0) goto L_0x05ce
            org.telegram.ui.Components.RecyclerListView r3 = r8.sideMenu
            android.view.View r3 = r3.getChildAt(r2)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r4 == 0) goto L_0x05cb
            r4 = r3
            org.telegram.ui.Cells.DrawerUserCell r4 = (org.telegram.ui.Cells.DrawerUserCell) r4
            int r4 = r4.getAccountNumber()
            int r5 = r1.intValue()
            if (r4 != r5) goto L_0x05cb
            r3.invalidate()
            goto L_0x05ce
        L_0x05cb:
            int r2 = r2 + 1
            goto L_0x05ae
        L_0x05ce:
            goto L_0x08f5
        L_0x05d0:
            int r0 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r9 != r0) goto L_0x05e2
            r1 = 0
            r0 = r11[r1]     // Catch:{ all -> 0x05df }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x05df }
            r1 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r8, r1)     // Catch:{ all -> 0x05df }
            goto L_0x05e0
        L_0x05df:
            r0 = move-exception
        L_0x05e0:
            goto L_0x08f5
        L_0x05e2:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoaded
            if (r9 != r0) goto L_0x06c1
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x0603
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x0603
            r2 = 1
            r8.updateAppUpdateViews(r2)
        L_0x0603:
            java.lang.String r1 = r8.loadingThemeFileName
            if (r1 == 0) goto L_0x0690
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x06bf
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
            if (r2 == 0) goto L_0x068c
            java.lang.String r3 = r2.pathToWallpaper
            if (r3 == 0) goto L_0x066f
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r2.pathToWallpaper
            r3.<init>(r4)
            boolean r4 = r3.exists()
            if (r4 != 0) goto L_0x066f
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r4 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r4.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r5 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r5.<init>()
            java.lang.String r6 = r2.slug
            r5.slug = r6
            r4.wallpaper = r5
            int r6 = r2.account
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64 r7 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64
            r7.<init>(r8, r2)
            r6.sendRequest(r4, r7)
            return
        L_0x066f:
            org.telegram.tgnet.TLRPC$TL_theme r3 = r8.loadingTheme
            java.lang.String r3 = r3.title
            org.telegram.tgnet.TLRPC$TL_theme r4 = r8.loadingTheme
            r5 = 1
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r1, r3, r4, r5)
            if (r3 == 0) goto L_0x068c
            org.telegram.ui.ThemePreviewActivity r4 = new org.telegram.ui.ThemePreviewActivity
            r14 = 1
            r15 = 0
            r16 = 0
            r17 = 0
            r12 = r4
            r13 = r3
            r12.<init>(r13, r14, r15, r16, r17)
            r8.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(r4)
        L_0x068c:
            r24.onThemeLoadFinish()
            goto L_0x06bf
        L_0x0690:
            java.lang.String r1 = r8.loadingThemeWallpaperName
            if (r1 == 0) goto L_0x06bf
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x06bf
            r1 = 0
            r8.loadingThemeWallpaperName = r1
            r1 = 1
            r1 = r11[r1]
            java.io.File r1 = (java.io.File) r1
            boolean r2 = r8.loadingThemeAccent
            if (r2 == 0) goto L_0x06b3
            org.telegram.tgnet.TLRPC$TL_theme r2 = r8.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r8.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r8.loadingThemeInfo
            r8.openThemeAccentPreview(r2, r3, r4)
            r24.onThemeLoadFinish()
            goto L_0x06bf
        L_0x06b3:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r8.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda45 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda45
            r4.<init>(r8, r2, r1)
            r3.postRunnable(r4)
        L_0x06bf:
            goto L_0x08f5
        L_0x06c1:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoadFailed
            if (r9 != r0) goto L_0x06f7
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r8.loadingThemeFileName
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x06da
            java.lang.String r1 = r8.loadingThemeWallpaperName
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x06dd
        L_0x06da:
            r24.onThemeLoadFinish()
        L_0x06dd:
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x06f5
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x06f5
            r2 = 1
            r8.updateAppUpdateViews(r2)
        L_0x06f5:
            goto L_0x08f5
        L_0x06f7:
            int r0 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r9 != r0) goto L_0x070e
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x0700
            return
        L_0x0700:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x0709
            r24.onPasscodeResume()
            goto L_0x08f5
        L_0x0709:
            r24.onPasscodePause()
            goto L_0x08f5
        L_0x070e:
            int r0 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r9 != r0) goto L_0x0717
            r24.checkSystemBarColors()
            goto L_0x08f5
        L_0x0717:
            int r0 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r9 != r0) goto L_0x0746
            int r0 = r11.length
            r1 = 1
            if (r0 <= r1) goto L_0x08f5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x08f5
            int r0 = r8.currentAccount
            r2 = r11[r15]
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
            goto L_0x08f5
        L_0x0746:
            int r0 = org.telegram.messenger.NotificationCenter.stickersImportComplete
            if (r9 != r0) goto L_0x0777
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r26)
            r2 = 0
            r0 = r11[r2]
            r3 = r0
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r4 = 2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x076d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            r5 = 1
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r5 = r0
            goto L_0x076e
        L_0x076d:
            r5 = 0
        L_0x076e:
            r6 = 0
            r7 = 1
            r2 = r24
            r1.toggleStickerSet(r2, r3, r4, r5, r6, r7)
            goto L_0x08f5
        L_0x0777:
            int r0 = org.telegram.messenger.NotificationCenter.newSuggestionsAvailable
            if (r9 != r0) goto L_0x0782
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            r0.invalidateViews()
            goto L_0x08f5
        L_0x0782:
            int r0 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r9 != r0) goto L_0x087e
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x08f5
            r1 = 0
            r0 = r11[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 0
            r2 = 0
            boolean r3 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r3 == 0) goto L_0x07a9
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r3 == 0) goto L_0x07a9
            org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.groupCallInstance
            android.widget.FrameLayout r1 = r3.getContainer()
            r7 = r1
            goto L_0x07aa
        L_0x07a9:
            r7 = r1
        L_0x07aa:
            if (r7 != 0) goto L_0x07bd
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r1 = r1.get(r3)
            r2 = r1
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            r12 = r2
            goto L_0x07bf
        L_0x07bd:
            r4 = 1
            r12 = r2
        L_0x07bf:
            r1 = 0
            r3 = 3
            if (r0 != r3) goto L_0x07f4
            r3 = r11[r4]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x07d6
            r1 = 2131628741(0x7f0e12c5, float:1.8884783E38)
            java.lang.String r2 = "YourNameChanged"
            goto L_0x07db
        L_0x07d6:
            r1 = 2131624829(0x7f0e037d, float:1.8876849E38)
            java.lang.String r2 = "CannelTitleChanged"
        L_0x07db:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r7 == 0) goto L_0x07e7
            r2 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r2)
            goto L_0x07eb
        L_0x07e7:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r12)
        L_0x07eb:
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r1)
            r2.show()
            goto L_0x087c
        L_0x07f4:
            if (r0 != r15) goto L_0x0826
            r3 = 1
            r3 = r11[r3]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0809
            r1 = 2131628724(0x7f0e12b4, float:1.8884749E38)
            java.lang.String r2 = "YourBioChanged"
            goto L_0x080e
        L_0x0809:
            r1 = 2131624768(0x7f0e0340, float:1.8876725E38)
            java.lang.String r2 = "CannelDescriptionChanged"
        L_0x080e:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r7 == 0) goto L_0x081a
            r2 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r2)
            goto L_0x081e
        L_0x081a:
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r12)
        L_0x081e:
            org.telegram.ui.Components.Bulletin r2 = r2.createErrorBulletin(r1)
            r2.show()
            goto L_0x087c
        L_0x0826:
            if (r0 != 0) goto L_0x0855
            r1 = 1
            r1 = r11[r1]
            r13 = r1
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            org.telegram.ui.Components.StickerSetBulletinLayout r14 = new org.telegram.ui.Components.StickerSetBulletinLayout
            r3 = 0
            r1 = r11[r15]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r4 = r1.intValue()
            r6 = 0
            r1 = r14
            r2 = r24
            r5 = r13
            r1.<init>(r2, r3, r4, r5, r6)
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r12 == 0) goto L_0x084d
            org.telegram.ui.Components.Bulletin r2 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r12, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r2)
            r2.show()
            goto L_0x087b
        L_0x084d:
            org.telegram.ui.Components.Bulletin r2 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r1, (int) r2)
            r2.show()
            goto L_0x087b
        L_0x0855:
            r1 = 1
            if (r0 != r1) goto L_0x087b
            if (r12 == 0) goto L_0x086a
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r12)
            r1 = r11[r1]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r1 = r2.createErrorBulletin(r1)
            r1.show()
            goto L_0x087c
        L_0x086a:
            r2 = 0
            org.telegram.ui.Components.BulletinFactory r2 = org.telegram.ui.Components.BulletinFactory.of(r7, r2)
            r1 = r11[r1]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r1 = r2.createErrorBulletin(r1)
            r1.show()
            goto L_0x087c
        L_0x087b:
        L_0x087c:
            goto L_0x08f5
        L_0x087e:
            int r0 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r9 != r0) goto L_0x0887
            r1 = 0
            r8.checkWasMutedByAdmin(r1)
            goto L_0x08f5
        L_0x0887:
            int r0 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r9 != r0) goto L_0x08e2
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.updateTextView
            if (r0 == 0) goto L_0x08f5
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x08f5
            r1 = 0
            r0 = r11[r1]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            if (r1 == 0) goto L_0x08e1
            boolean r2 = r1.equals(r0)
            if (r2 == 0) goto L_0x08e1
            r2 = 1
            r3 = r11[r2]
            r2 = r3
            java.lang.Long r2 = (java.lang.Long) r2
            r3 = r11[r15]
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
            r7 = 2131624305(0x7f0e0171, float:1.8875786E38)
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
        L_0x08e1:
            goto L_0x08f5
        L_0x08e2:
            r13 = 0
            int r0 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r9 != r0) goto L_0x08f5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            r1 = 1
            if (r0 != r1) goto L_0x08f1
            goto L_0x08f2
        L_0x08f1:
            r1 = 0
        L_0x08f2:
            r8.updateAppUpdateViews(r1)
        L_0x08f5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    static /* synthetic */ void lambda$didReceivedNotification$65(int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(account);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* renamed from: lambda$didReceivedNotification$66$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3068lambda$didReceivedNotification$66$orgtelegramuiLaunchActivity(DialogInterface dialog, int which) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* renamed from: lambda$didReceivedNotification$68$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3069lambda$didReceivedNotification$68$orgtelegramuiLaunchActivity(HashMap waitingForLocation, int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity fragment = new LocationActivity(0);
                fragment.setDelegate(new LaunchActivity$$ExternalSyntheticLambda82(waitingForLocation, account));
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(fragment);
            }
        }
    }

    static /* synthetic */ void lambda$didReceivedNotification$67(HashMap waitingForLocation, int account, TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        for (Map.Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
            MessageObject messageObject = entry.getValue();
            SendMessagesHelper.getInstance(account).sendMessage(location, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
        }
    }

    /* renamed from: lambda$didReceivedNotification$73$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3071lambda$didReceivedNotification$73$orgtelegramuiLaunchActivity(Theme.ThemeInfo themeInfo, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda31(this, response, themeInfo));
    }

    /* renamed from: lambda$didReceivedNotification$72$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3070lambda$didReceivedNotification$72$orgtelegramuiLaunchActivity(TLObject response, Theme.ThemeInfo themeInfo) {
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

    /* renamed from: lambda$didReceivedNotification$75$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3073lambda$didReceivedNotification$75$orgtelegramuiLaunchActivity(Theme.ThemeInfo info, File file) {
        info.createBackground(file, info.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda16(this));
    }

    /* renamed from: lambda$didReceivedNotification$74$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3072lambda$didReceivedNotification$74$orgtelegramuiLaunchActivity() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            Theme.ThemeInfo finalThemeInfo = Theme.applyThemeFile(new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme"), this.loadingTheme.title, this.loadingTheme, true);
            if (finalThemeInfo != null) {
                m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ThemePreviewActivity(finalThemeInfo, true, 0, false, false));
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
        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ThemePreviewActivity(info, lastId != info.lastAccentId, 0, false, false));
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
            Utilities.globalQueue.postRunnable(new LaunchActivity$$ExternalSyntheticLambda15(this), 2000);
        }
    }

    /* renamed from: lambda$checkFreeDiscSpace$77$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3066lambda$checkFreeDiscSpace$77$orgtelegramuiLaunchActivity() {
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
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda14(this));
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: lambda$checkFreeDiscSpace$76$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3065lambda$checkFreeDiscSpace$76$orgtelegramuiLaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable th) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0054 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0056 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005c A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005f A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0066 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006d A[Catch:{ Exception -> 0x0120 }] */
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
            boolean r5 = r4.builtIn     // Catch:{ Exception -> 0x0120 }
            r6 = 1
            if (r5 != 0) goto L_0x001d
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0120 }
            boolean r5 = r5.isCurrentLocalLocale()     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r5 = 0
            goto L_0x001e
        L_0x001d:
            r5 = 1
        L_0x001e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x0120 }
            r7.<init>((android.content.Context) r1)     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0120 }
            r9 = 2131624977(0x7f0e0411, float:1.8877149E38)
            java.lang.String r8 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0120 }
            r7.setTitle(r8)     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0120 }
            java.lang.String r2 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0120 }
            r7.setSubtitle(r2)     // Catch:{ Exception -> 0x0120 }
            android.widget.LinearLayout r2 = new android.widget.LinearLayout     // Catch:{ Exception -> 0x0120 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0120 }
            r2.setOrientation(r6)     // Catch:{ Exception -> 0x0120 }
            r8 = 2
            org.telegram.ui.Cells.LanguageCell[] r9 = new org.telegram.ui.Cells.LanguageCell[r8]     // Catch:{ Exception -> 0x0120 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r10 = new org.telegram.messenger.LocaleController.LocaleInfo[r6]     // Catch:{ Exception -> 0x0120 }
            org.telegram.messenger.LocaleController$LocaleInfo[] r11 = new org.telegram.messenger.LocaleController.LocaleInfo[r8]     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0120 }
            java.lang.String r13 = "English"
            r14 = 2131625422(0x7f0e05ce, float:1.8878052E38)
            java.lang.String r12 = r1.getStringForLanguageAlert(r12, r13, r14)     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x0056
            r13 = r4
            goto L_0x0058
        L_0x0056:
            r13 = r18
        L_0x0058:
            r11[r3] = r13     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x005f
            r13 = r18
            goto L_0x0060
        L_0x005f:
            r13 = r4
        L_0x0060:
            r11[r6] = r13     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x0066
            r13 = r4
            goto L_0x0068
        L_0x0066:
            r13 = r18
        L_0x0068:
            r10[r3] = r13     // Catch:{ Exception -> 0x0120 }
            r13 = 0
        L_0x006b:
            if (r13 >= r8) goto L_0x00c2
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0120 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0120 }
            r9[r13] = r3     // Catch:{ Exception -> 0x0120 }
            r3 = r9[r13]     // Catch:{ Exception -> 0x0120 }
            r14 = r11[r13]     // Catch:{ Exception -> 0x0120 }
            r15 = r11[r13]     // Catch:{ Exception -> 0x0120 }
            r8 = r18
            if (r15 != r8) goto L_0x0080
            r15 = r12
            goto L_0x0081
        L_0x0080:
            r15 = 0
        L_0x0081:
            r3.setLanguage(r14, r15, r6)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x011e }
            r3.setTag(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            java.lang.String r14 = "dialogButtonSelector"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)     // Catch:{ Exception -> 0x011e }
            r15 = 2
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r15)     // Catch:{ Exception -> 0x011e }
            r3.setBackgroundDrawable(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            if (r13 != 0) goto L_0x00a3
            r14 = 1
            goto L_0x00a4
        L_0x00a3:
            r14 = 0
        L_0x00a4:
            r3.setLanguageSelected(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            r14 = 50
            r15 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14)     // Catch:{ Exception -> 0x011e }
            r2.addView(r3, r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6 r14 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6     // Catch:{ Exception -> 0x011e }
            r14.<init>(r10, r9)     // Catch:{ Exception -> 0x011e }
            r3.setOnClickListener(r14)     // Catch:{ Exception -> 0x011e }
            int r13 = r13 + 1
            r3 = 0
            r8 = 2
            goto L_0x006b
        L_0x00c2:
            r8 = r18
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x011e }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x011e }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x011e }
            r13 = 2131624978(0x7f0e0412, float:1.887715E38)
            java.lang.String r6 = r1.getStringForLanguageAlert(r6, r0, r13)     // Catch:{ Exception -> 0x011e }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x011e }
            java.lang.String r0 = r1.getStringForLanguageAlert(r14, r0, r13)     // Catch:{ Exception -> 0x011e }
            r3.setValue(r6, r0)     // Catch:{ Exception -> 0x011e }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5     // Catch:{ Exception -> 0x011e }
            r0.<init>(r1)     // Catch:{ Exception -> 0x011e }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x011e }
            r0 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r0)     // Catch:{ Exception -> 0x011e }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x011e }
            r7.setView(r2)     // Catch:{ Exception -> 0x011e }
            java.lang.String r0 = "OK"
            r6 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r6)     // Catch:{ Exception -> 0x011e }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x011e }
            r6.<init>(r1, r10)     // Catch:{ Exception -> 0x011e }
            r7.setNegativeButton(r0, r6)     // Catch:{ Exception -> 0x011e }
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.showAlertDialog(r7)     // Catch:{ Exception -> 0x011e }
            r1.localeDialog = r0     // Catch:{ Exception -> 0x011e }
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x011e }
            android.content.SharedPreferences$Editor r6 = r0.edit()     // Catch:{ Exception -> 0x011e }
            java.lang.String r13 = "language_showed2"
            r14 = r19
            android.content.SharedPreferences$Editor r6 = r6.putString(r13, r14)     // Catch:{ Exception -> 0x011c }
            r6.commit()     // Catch:{ Exception -> 0x011c }
            goto L_0x012c
        L_0x011c:
            r0 = move-exception
            goto L_0x0129
        L_0x011e:
            r0 = move-exception
            goto L_0x0127
        L_0x0120:
            r0 = move-exception
            goto L_0x0125
        L_0x0122:
            r0 = move-exception
            r4 = r17
        L_0x0125:
            r8 = r18
        L_0x0127:
            r14 = r19
        L_0x0129:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x012c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    static /* synthetic */ void lambda$showLanguageAlertInternal$78(LocaleController.LocaleInfo[] selectedLanguage, LanguageCell[] cells, View v) {
        Integer tag = (Integer) v.getTag();
        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
        int a1 = 0;
        while (a1 < cells.length) {
            cells[a1].setLanguageSelected(a1 == tag.intValue());
            a1++;
        }
    }

    /* renamed from: lambda$showLanguageAlertInternal$79$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3125x2518c9dd(View v) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* renamed from: lambda$showLanguageAlertInternal$80$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3126xd93e4707(LocaleController.LocaleInfo[] selectedLanguage, DialogInterface dialog, int which) {
        LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d3 A[Catch:{ Exception -> 0x0184 }, LOOP:0: B:29:0x0080->B:48:0x00d3, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d2 A[SYNTHETIC] */
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
            boolean r6 = r1.loadingLocaleDialog     // Catch:{ Exception -> 0x0184 }
            if (r6 != 0) goto L_0x0183
            boolean r6 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0184 }
            if (r6 == 0) goto L_0x0016
            goto L_0x0183
        L_0x0016:
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r7 = "language_showed2"
            java.lang.String r8 = ""
            java.lang.String r7 = r6.getString(r7, r8)     // Catch:{ Exception -> 0x0184 }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r8 = r8.suggestedLangCode     // Catch:{ Exception -> 0x0184 }
            if (r18 != 0) goto L_0x004b
            boolean r9 = r7.equals(r8)     // Catch:{ Exception -> 0x0184 }
            if (r9 == 0) goto L_0x004b
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0184 }
            if (r0 == 0) goto L_0x004a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
            r0.<init>()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r2 = "alert already showed for "
            r0.append(r2)     // Catch:{ Exception -> 0x0184 }
            r0.append(r7)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0184 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0184 }
        L_0x004a:
            return
        L_0x004b:
            r9 = 2
            org.telegram.messenger.LocaleController$LocaleInfo[] r9 = new org.telegram.messenger.LocaleController.LocaleInfo[r9]     // Catch:{ Exception -> 0x0184 }
            boolean r10 = r8.contains(r5)     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            if (r10 == 0) goto L_0x005c
            java.lang.String[] r10 = r8.split(r5)     // Catch:{ Exception -> 0x0184 }
            r10 = r10[r11]     // Catch:{ Exception -> 0x0184 }
            goto L_0x005d
        L_0x005c:
            r10 = r8
        L_0x005d:
            java.lang.String r12 = "in"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x0068
            java.lang.String r12 = "id"
            goto L_0x007f
        L_0x0068:
            java.lang.String r12 = "iw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x0073
            java.lang.String r12 = "he"
            goto L_0x007f
        L_0x0073:
            java.lang.String r12 = "jw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x007e
            java.lang.String r12 = "jv"
            goto L_0x007f
        L_0x007e:
            r12 = 0
        L_0x007f:
            r13 = 0
        L_0x0080:
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<org.telegram.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0184 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0184 }
            if (r13 >= r14) goto L_0x00d7
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<org.telegram.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0184 }
            java.lang.Object r14 = r14.get(r13)     // Catch:{ Exception -> 0x0184 }
            org.telegram.messenger.LocaleController$LocaleInfo r14 = (org.telegram.messenger.LocaleController.LocaleInfo) r14     // Catch:{ Exception -> 0x0184 }
            java.lang.String r15 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = "en"
            boolean r11 = r15.equals(r11)     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x00a5
            r11 = 0
            r9[r11] = r14     // Catch:{ Exception -> 0x0184 }
        L_0x00a5:
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            java.lang.String r15 = "_"
            java.lang.String r11 = r11.replace(r15, r5)     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r8)     // Catch:{ Exception -> 0x0184 }
            if (r11 != 0) goto L_0x00c6
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r11 != 0) goto L_0x00c6
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x00c4
            goto L_0x00c6
        L_0x00c4:
            r11 = 1
            goto L_0x00c9
        L_0x00c6:
            r11 = 1
            r9[r11] = r14     // Catch:{ Exception -> 0x0184 }
        L_0x00c9:
            r15 = 0
            r16 = r9[r15]     // Catch:{ Exception -> 0x0184 }
            if (r16 == 0) goto L_0x00d3
            r15 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r15 == 0) goto L_0x00d3
            goto L_0x00d7
        L_0x00d3:
            int r13 = r13 + 1
            r11 = 0
            goto L_0x0080
        L_0x00d7:
            r5 = 0
            r11 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x0182
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r13 == 0) goto L_0x0182
            r13 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            r5 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r13 != r5) goto L_0x00e9
            goto L_0x0182
        L_0x00e9:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0184 }
            if (r5 == 0) goto L_0x0117
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
            r5.<init>()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = "show lang alert for "
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0184 }
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = " and "
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0184 }
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0184 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0184 }
        L_0x0117:
            r5 = 0
            r1.systemLocaleStrings = r5     // Catch:{ Exception -> 0x0184 }
            r1.englishLocaleStrings = r5     // Catch:{ Exception -> 0x0184 }
            r5 = 1
            r1.loadingLocaleDialog = r5     // Catch:{ Exception -> 0x0184 }
            org.telegram.tgnet.TLRPC$TL_langpack_getStrings r11 = new org.telegram.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0184 }
            r11.<init>()     // Catch:{ Exception -> 0x0184 }
            r5 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r5 = r5.getLangCode()     // Catch:{ Exception -> 0x0184 }
            r11.lang_code = r5     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r4)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r3)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r2)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r0)     // Catch:{ Exception -> 0x0184 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)     // Catch:{ Exception -> 0x0184 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda68 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda68     // Catch:{ Exception -> 0x0184 }
            r13.<init>(r1, r9, r8)     // Catch:{ Exception -> 0x0184 }
            r14 = 8
            r5.sendRequest(r11, r13, r14)     // Catch:{ Exception -> 0x0184 }
            org.telegram.tgnet.TLRPC$TL_langpack_getStrings r5 = new org.telegram.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0184 }
            r5.<init>()     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            r11 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r11.getLangCode()     // Catch:{ Exception -> 0x0184 }
            r5.lang_code = r11     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r11 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r11.add(r4)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r4 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r4.add(r3)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r3 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r3.add(r2)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r2 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r2.add(r0)     // Catch:{ Exception -> 0x0184 }
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)     // Catch:{ Exception -> 0x0184 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda69 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda69     // Catch:{ Exception -> 0x0184 }
            r2.<init>(r1, r9, r8)     // Catch:{ Exception -> 0x0184 }
            r0.sendRequest(r5, r2, r14)     // Catch:{ Exception -> 0x0184 }
            goto L_0x0188
        L_0x0182:
            return
        L_0x0183:
            return
        L_0x0184:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0188:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlert(boolean):void");
    }

    /* renamed from: lambda$showLanguageAlert$82$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3122lambda$showLanguageAlert$82$orgtelegramuiLaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda25(this, keys, infos, systemLang));
    }

    /* renamed from: lambda$showLanguageAlert$81$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3121lambda$showLanguageAlert$81$orgtelegramuiLaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
        this.systemLocaleStrings = keys;
        if (this.englishLocaleStrings != null && keys != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    /* renamed from: lambda$showLanguageAlert$84$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3124lambda$showLanguageAlert$84$orgtelegramuiLaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda26(this, keys, infos, systemLang));
    }

    /* renamed from: lambda$showLanguageAlert$83$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3123lambda$showLanguageAlert$83$orgtelegramuiLaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
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
                action = new LaunchActivity$$ExternalSyntheticLambda20(this);
            }
            this.actionBarLayout.setTitleOverlayText(title, titleId, action);
        }
    }

    /* renamed from: lambda$updateCurrentConnectionState$85$org-telegram-ui-LaunchActivity  reason: not valid java name */
    public /* synthetic */ void m3130x2a17177b() {
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
            m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new ProxyListActivity());
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
        if (passcodeView2 != null && passcodeView2.getVisibility() == 0) {
            finish();
        } else if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
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
        return super.dispatchKeyEvent(event);
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
            this.drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity) && !(fragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
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
            if (fragment instanceof LoginActivity) {
                if (mainFragmentsStack.size() == 0) {
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
            this.drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity) && !(fragment instanceof CountrySelectActivity) && this.layersActionBarLayout.getVisibility() != 0, true);
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
            if (fragment instanceof LoginActivity) {
                if (mainFragmentsStack.size() == 0) {
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
