package org.telegram.ui;

import android.animation.Animator;
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
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
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

public class LaunchActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout backgroundTablet;
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
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<String> importingStickersEmoji;
    private String importingStickersSoftware;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$1(View view) {
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
            r1 = 2131689489(0x7f0var_, float:1.9007995E38)
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
            r6 = 2131166164(0x7var_d4, float:1.7946566E38)
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
            org.telegram.ui.ActionBar.Theme.createCommonChatResources()
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r13)
            java.lang.String r7 = org.telegram.messenger.SharedConfig.passcodeHash
            int r7 = r7.length()
            if (r7 == 0) goto L_0x0168
            boolean r7 = org.telegram.messenger.SharedConfig.appLocked
            if (r7 == 0) goto L_0x0168
            long r7 = android.os.SystemClock.elapsedRealtime()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r8 = (int) r7
            org.telegram.messenger.SharedConfig.lastPauseTime = r8
        L_0x0168:
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
            if (r1 < r5) goto L_0x0190
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r13)
            r13.themeSwitchImageView = r8
            r8.setVisibility(r7)
        L_0x0190:
            org.telegram.ui.LaunchActivity$2 r8 = new org.telegram.ui.LaunchActivity$2
            r8.<init>(r13, r13)
            r13.drawerLayoutContainer = r8
            java.lang.String r10 = "windowBackgroundWhite"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setBehindKeyboardColor(r10)
            android.widget.FrameLayout r8 = r13.frameLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r13.drawerLayoutContainer
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r8.addView(r10, r12)
            if (r1 < r5) goto L_0x01c8
            org.telegram.ui.LaunchActivity$3 r1 = new org.telegram.ui.LaunchActivity$3
            r1.<init>(r13)
            r13.themeSwitchSunView = r1
            android.widget.FrameLayout r5 = r13.frameLayout
            r8 = 48
            r10 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r10)
            r5.addView(r1, r8)
            android.view.View r1 = r13.themeSwitchSunView
            r1.setVisibility(r7)
        L_0x01c8:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x02ab
            android.view.Window r1 = r13.getWindow()
            r5 = 16
            r1.setSoftInputMode(r5)
            org.telegram.ui.LaunchActivity$4 r1 = new org.telegram.ui.LaunchActivity$4
            r1.<init>(r13)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r5 = r13.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r5.addView(r1, r8)
            org.telegram.ui.LaunchActivity$5 r5 = new org.telegram.ui.LaunchActivity$5
            r5.<init>(r13, r13)
            r13.backgroundTablet = r5
            r5.setOccupyStatusBar(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r13.backgroundTablet
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r10 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r5.setBackgroundImage(r8, r10)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r13.backgroundTablet
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
            if (r8 == 0) goto L_0x0244
            r8 = 8
            goto L_0x0245
        L_0x0244:
            r8 = 0
        L_0x0245:
            r5.setVisibility(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            r8 = 2130706432(0x7var_, float:1.7014118E38)
            r5.setBackgroundColor(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            r1.addView(r5)
            android.widget.FrameLayout r5 = r13.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17 r8 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda17
            r8.<init>(r13)
            r5.setOnTouchListener(r8)
            android.widget.FrameLayout r5 = r13.shadowTablet
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16 r8 = org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda16.INSTANCE
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
            r8 = 2131165302(0x7var_, float:1.7944817E38)
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
            if (r8 == 0) goto L_0x02a1
            goto L_0x02a2
        L_0x02a1:
            r7 = 0
        L_0x02a2:
            r5.setVisibility(r7)
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.layersActionBarLayout
            r1.addView(r5)
            goto L_0x02b7
        L_0x02ab:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r13.actionBarLayout
            android.view.ViewGroup$LayoutParams r7 = new android.view.ViewGroup$LayoutParams
            r7.<init>(r9, r9)
            r1.addView(r5, r7)
        L_0x02b7:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r13)
            r13.sideMenuContainer = r1
            org.telegram.ui.LaunchActivity$6 r1 = new org.telegram.ui.LaunchActivity$6
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
            android.widget.FrameLayout r1 = r13.sideMenuContainer
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r11)
            r1.addView(r5, r7)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            android.widget.FrameLayout r5 = r13.sideMenuContainer
            r1.setDrawerLayout(r5)
            android.widget.FrameLayout r1 = r13.sideMenuContainer
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            r8 = 1134559232(0x43a00000, float:320.0)
            if (r7 == 0) goto L_0x0324
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x033b
        L_0x0324:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r8, r5)
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            int r5 = java.lang.Math.min(r7, r5)
        L_0x033b:
            r1.width = r5
            r1.height = r9
            android.widget.FrameLayout r5 = r13.sideMenuContainer
            r5.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r13.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda78
            r5.<init>(r13)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r5)
            androidx.recyclerview.widget.ItemTouchHelper r1 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.LaunchActivity$7 r5 = new org.telegram.ui.LaunchActivity$7
            r7 = 3
            r5.<init>(r7, r3)
            r1.<init>(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            r1.attachToRecyclerView(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r13.sideMenu
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79 r8 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda79
            r8.<init>(r13, r1)
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
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            r1.addObserver(r13, r5)
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0536
            int r1 = r13.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 != 0) goto L_0x043f
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            org.telegram.ui.LoginActivity r4 = new org.telegram.ui.LoginActivity
            r4.<init>()
            r1.addFragmentToStack(r4)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            r1.setAllowOpenDrawer(r3, r3)
            goto L_0x0453
        L_0x043f:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r4)
            org.telegram.ui.Components.RecyclerListView r4 = r13.sideMenu
            r1.setSideMenu(r4)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            r4.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r13.drawerLayoutContainer
            r1.setAllowOpenDrawer(r2, r3)
        L_0x0453:
            if (r14 == 0) goto L_0x059d
            java.lang.String r1 = "fragment"
            java.lang.String r1 = r14.getString(r1)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x059d
            java.lang.String r4 = "args"
            android.os.Bundle r4 = r14.getBundle(r4)     // Catch:{ Exception -> 0x0531 }
            int r5 = r1.hashCode()     // Catch:{ Exception -> 0x0531 }
            r8 = 5
            r10 = 4
            r11 = 2
            switch(r5) {
                case -1529105743: goto L_0x04a0;
                case -1349522494: goto L_0x0496;
                case 3052376: goto L_0x048c;
                case 98629247: goto L_0x0482;
                case 738950403: goto L_0x0478;
                case 1434631203: goto L_0x046e;
                default: goto L_0x046d;
            }     // Catch:{ Exception -> 0x0531 }
        L_0x046d:
            goto L_0x04a9
        L_0x046e:
            java.lang.String r5 = "settings"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 1
            goto L_0x04a9
        L_0x0478:
            java.lang.String r5 = "channel"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 3
            goto L_0x04a9
        L_0x0482:
            java.lang.String r5 = "group"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 2
            goto L_0x04a9
        L_0x048c:
            java.lang.String r5 = "chat"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 0
            goto L_0x04a9
        L_0x0496:
            java.lang.String r5 = "chat_profile"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 4
            goto L_0x04a9
        L_0x04a0:
            java.lang.String r5 = "wallpapers"
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0531 }
            if (r1 == 0) goto L_0x04a9
            r9 = 5
        L_0x04a9:
            if (r9 == 0) goto L_0x051e
            if (r9 == r2) goto L_0x0502
            if (r9 == r11) goto L_0x04ee
            if (r9 == r7) goto L_0x04da
            if (r9 == r10) goto L_0x04c6
            if (r9 == r8) goto L_0x04b7
            goto L_0x059d
        L_0x04b7:
            org.telegram.ui.WallpapersListActivity r1 = new org.telegram.ui.WallpapersListActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x04c6:
            if (r4 == 0) goto L_0x059d
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            if (r4 == 0) goto L_0x059d
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x04da:
            if (r4 == 0) goto L_0x059d
            org.telegram.ui.ChannelCreateActivity r1 = new org.telegram.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            if (r4 == 0) goto L_0x059d
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x04ee:
            if (r4 == 0) goto L_0x059d
            org.telegram.ui.GroupCreateFinalActivity r1 = new org.telegram.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            if (r4 == 0) goto L_0x059d
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x0502:
            java.lang.String r1 = "user_id"
            int r5 = r13.currentAccount     // Catch:{ Exception -> 0x0531 }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x0531 }
            long r7 = r5.clientUserId     // Catch:{ Exception -> 0x0531 }
            r4.putLong(r1, r7)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x051e:
            if (r4 == 0) goto L_0x059d
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity     // Catch:{ Exception -> 0x0531 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0531 }
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout     // Catch:{ Exception -> 0x0531 }
            boolean r4 = r4.addFragmentToStack(r1)     // Catch:{ Exception -> 0x0531 }
            if (r4 == 0) goto L_0x059d
            r1.restoreSelfArgs(r14)     // Catch:{ Exception -> 0x0531 }
            goto L_0x059d
        L_0x0531:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x059d
        L_0x0536:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r4 = r1 instanceof org.telegram.ui.DialogsActivity
            if (r4 == 0) goto L_0x054b
            org.telegram.ui.DialogsActivity r1 = (org.telegram.ui.DialogsActivity) r1
            org.telegram.ui.Components.RecyclerListView r4 = r13.sideMenu
            r1.setSideMenu(r4)
        L_0x054b:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0580
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 > r2) goto L_0x0567
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0567
            r1 = 1
            goto L_0x0568
        L_0x0567:
            r1 = 0
        L_0x0568:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0581
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0581
            r1 = 0
            goto L_0x0581
        L_0x0580:
            r1 = 1
        L_0x0581:
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 != r2) goto L_0x0598
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r13.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            java.lang.Object r4 = r4.get(r3)
            boolean r4 = r4 instanceof org.telegram.ui.LoginActivity
            if (r4 == 0) goto L_0x0598
            r1 = 0
        L_0x0598:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r13.drawerLayoutContainer
            r4.setAllowOpenDrawer(r1, r3)
        L_0x059d:
            r13.checkLayout()
            r13.checkSystemBarColors()
            android.content.Intent r1 = r13.getIntent()
            if (r14 == 0) goto L_0x05ab
            r14 = 1
            goto L_0x05ac
        L_0x05ab:
            r14 = 0
        L_0x05ac:
            r13.handleIntent(r1, r3, r14, r3)
            java.lang.String r14 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x0610 }
            java.lang.String r1 = android.os.Build.USER     // Catch:{ Exception -> 0x0610 }
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x05bc
            java.lang.String r14 = r14.toLowerCase()     // Catch:{ Exception -> 0x0610 }
            goto L_0x05bd
        L_0x05bc:
            r14 = r4
        L_0x05bd:
            if (r1 == 0) goto L_0x05c3
            java.lang.String r4 = r14.toLowerCase()     // Catch:{ Exception -> 0x0610 }
        L_0x05c3:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0610 }
            if (r1 == 0) goto L_0x05e3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0610 }
            r1.<init>()     // Catch:{ Exception -> 0x0610 }
            java.lang.String r5 = "OS name "
            r1.append(r5)     // Catch:{ Exception -> 0x0610 }
            r1.append(r14)     // Catch:{ Exception -> 0x0610 }
            java.lang.String r5 = " "
            r1.append(r5)     // Catch:{ Exception -> 0x0610 }
            r1.append(r4)     // Catch:{ Exception -> 0x0610 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0610 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0610 }
        L_0x05e3:
            boolean r14 = r14.contains(r0)     // Catch:{ Exception -> 0x0610 }
            if (r14 != 0) goto L_0x05ef
            boolean r14 = r4.contains(r0)     // Catch:{ Exception -> 0x0610 }
            if (r14 == 0) goto L_0x0614
        L_0x05ef:
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0610 }
            if (r14 > r6) goto L_0x0614
            org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x0610 }
            android.view.Window r14 = r13.getWindow()     // Catch:{ Exception -> 0x0610 }
            android.view.View r14 = r14.getDecorView()     // Catch:{ Exception -> 0x0610 }
            android.view.View r14 = r14.getRootView()     // Catch:{ Exception -> 0x0610 }
            android.view.ViewTreeObserver r0 = r14.getViewTreeObserver()     // Catch:{ Exception -> 0x0610 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda18     // Catch:{ Exception -> 0x0610 }
            r1.<init>(r14)     // Catch:{ Exception -> 0x0610 }
            r13.onGlobalLayoutListener = r1     // Catch:{ Exception -> 0x0610 }
            r0.addOnGlobalLayoutListener(r1)     // Catch:{ Exception -> 0x0610 }
            goto L_0x0614
        L_0x0610:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x0614:
            org.telegram.messenger.MediaController r14 = org.telegram.messenger.MediaController.getInstance()
            r14.setBaseActivity(r13, r2)
            org.telegram.messenger.AndroidUtilities.startAppCenter(r13)
            r13.updateAppUpdateViews(r3)
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
    public /* synthetic */ void lambda$onCreate$2(View view, int i, float f, float f2) {
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
                lambda$runLinkRequest$43(new LoginActivity(i2));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(i);
            if (id == 2) {
                lambda$runLinkRequest$43(new GroupCreateActivity(new Bundle()));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("createSecretChat", true);
                bundle.putBoolean("allowBots", false);
                bundle.putBoolean("allowSelf", false);
                lambda$runLinkRequest$43(new ContactsActivity(bundle));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$43(new ActionIntroActivity(0));
                    globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("step", 0);
                    lambda$runLinkRequest$43(new ChannelCreateActivity(bundle2));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$43(new ContactsActivity((Bundle) null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$43(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                openSettings(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$43(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                Bundle bundle3 = new Bundle();
                bundle3.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$43(new ChatActivity(bundle3));
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
                        lambda$runLinkRequest$43(new PeopleNearbyActivity());
                    } else {
                        lambda$runLinkRequest$43(new ActionIntroActivity(4));
                    }
                    this.drawerLayoutContainer.closeDrawer(false);
                    return;
                }
                lambda$runLinkRequest$43(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 13) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFeaturesUrl", NUM));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$3(ItemTouchHelper itemTouchHelper, View view, int i) {
        if (!(view instanceof DrawerUserCell)) {
            return false;
        }
        final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
        if (accountNumber == this.currentAccount || AndroidUtilities.isTablet()) {
            itemTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
            return false;
        }
        AnonymousClass8 r2 = new DialogsActivity((Bundle) null) {
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
    public static /* synthetic */ void lambda$onCreate$4(View view) {
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
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (z) {
            bundle.putBoolean("expandPhoto", true);
        }
        lambda$runLinkRequest$43(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(true, true);
    }

    private void checkSystemBarColors(boolean z, boolean z2) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            boolean z3 = true;
            if (z) {
                AndroidUtilities.setLightStatusBar(getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
            }
            if (i >= 26 && z2) {
                Window window = getWindow();
                int color = Theme.getColor("windowBackgroundGray", (boolean[]) null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
                    Window window2 = getWindow();
                    if (computePerceivedBrightness < 0.721f) {
                        z3 = false;
                    }
                    AndroidUtilities.setLightNavigationBar(window2, z3);
                }
            }
        }
        if (SharedConfig.noStatusBar && i >= 21 && z) {
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
            AnonymousClass9 r0 = new BlockingUpdateView(this) {
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
                    LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new LaunchActivity$10$$ExternalSyntheticLambda0(this)).start();
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
            this.passcodeView.onShow(z, z2, i, i2, new LaunchActivity$$ExternalSyntheticLambda32(this, runnable), runnable2);
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new LaunchActivity$$ExternalSyntheticLambda77(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPasscodeActivity$5(Runnable runnable) {
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
    public /* synthetic */ void lambda$showPasscodeActivity$6() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: org.telegram.ui.EditWidgetActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: org.telegram.ui.EditWidgetActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: org.telegram.ui.ActionIntroActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: org.telegram.ui.ProfileActivity} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v177, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v180, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v188, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v0, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v34, resolved type: boolean} */
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v1, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v3, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v227, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v7, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v8, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v9, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v10, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v11, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v12, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v14, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v14, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v15, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v15, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v16, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v16, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v17, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v18, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v18, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v19, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v274, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v290, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v300, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v22, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v21, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v170, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v327, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v23, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v22, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v333, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v24, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v23, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v339, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v25, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v24, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v24, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v348, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v353, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v360, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v31, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v361, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v32, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v25, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v34, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v387, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v393, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v254, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v36, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v26, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v395, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v255, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v27, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v27, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v38, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v397, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v262, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v409, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v413, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v415, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v197, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v297, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v310, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v247, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v55, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v56, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v57, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v58, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v59, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v60, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v61, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v62, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v63, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v64, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r10v2, types: [android.os.Bundle, java.lang.String] */
    /* JADX WARNING: type inference failed for: r6v1, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r10v6 */
    /* JADX WARNING: type inference failed for: r10v8 */
    /* JADX WARNING: type inference failed for: r10v9 */
    /* JADX WARNING: type inference failed for: r10v10 */
    /* JADX WARNING: type inference failed for: r10v11 */
    /* JADX WARNING: type inference failed for: r6v6 */
    /* JADX WARNING: type inference failed for: r6v8 */
    /* JADX WARNING: type inference failed for: r6v11 */
    /* JADX WARNING: type inference failed for: r6v12 */
    /* JADX WARNING: type inference failed for: r45v5 */
    /* JADX WARNING: type inference failed for: r45v10 */
    /* JADX WARNING: type inference failed for: r45v11 */
    /* JADX WARNING: type inference failed for: r45v12 */
    /* JADX WARNING: type inference failed for: r45v13 */
    /* JADX WARNING: type inference failed for: r45v14 */
    /* JADX WARNING: type inference failed for: r45v16 */
    /* JADX WARNING: type inference failed for: r45v17 */
    /* JADX WARNING: type inference failed for: r45v18 */
    /* JADX WARNING: type inference failed for: r45v19 */
    /* JADX WARNING: type inference failed for: r45v20 */
    /* JADX WARNING: type inference failed for: r45v21 */
    /* JADX WARNING: type inference failed for: r45v22 */
    /* JADX WARNING: type inference failed for: r45v23 */
    /* JADX WARNING: type inference failed for: r10v99 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0326, code lost:
        if (r15.sendingText == null) goto L_0x019a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0951, code lost:
        if (r2.intValue() == 0) goto L_0x0953;
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
        if (r4.equals(r0) != false) goto L_0x0157;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x145b, code lost:
        if (r2.longValue() == 0) goto L_0x1460;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1ad0, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r6)) != false) goto L_0x1ad2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1b46, code lost:
        if (r1.checkCanOpenChat(r0, r2.get(r2.size() - r6)) != false) goto L_0x1b48;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:887:0x17b7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1073:0x1ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:1074:0x1cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:1077:0x1cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:1078:0x1ccf  */
    /* JADX WARNING: Removed duplicated region for block: B:1146:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1157:0x1f5d  */
    /* JADX WARNING: Removed duplicated region for block: B:1168:0x1fa9  */
    /* JADX WARNING: Removed duplicated region for block: B:1170:0x1fb5  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x03c3 A[Catch:{ Exception -> 0x04e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x04f0  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0677  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0769 A[Catch:{ Exception -> 0x0777 }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0ff8 A[SYNTHETIC, Splitter:B:639:0x0ff8] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0165 A[SYNTHETIC, Splitter:B:70:0x0165] */
    /* JADX WARNING: Removed duplicated region for block: B:725:0x12f1  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x13d2 A[Catch:{ Exception -> 0x13e0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x13df  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:892:0x17c0 A[SYNTHETIC, Splitter:B:892:0x17c0] */
    /* JADX WARNING: Removed duplicated region for block: B:956:0x1a69  */
    /* JADX WARNING: Removed duplicated region for block: B:970:0x1a9c  */
    /* JADX WARNING: Removed duplicated region for block: B:987:0x1b13  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r71, boolean r72, boolean r73, boolean r74) {
        /*
            r70 = this;
            r15 = r70
            r14 = r71
            r0 = r73
            boolean r1 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r70, r71)
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
            java.lang.String r1 = r71.getAction()
            java.lang.String r2 = "android.intent.action.MAIN"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0049
        L_0x0042:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            r1.closePhoto(r12, r13)
        L_0x0049:
            int r8 = r71.getFlags()
            java.lang.String r9 = r71.getAction()
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
            if (r74 != 0) goto L_0x009c
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
            r1 = r70
            r1.showPasscodeActivity(r2, r3, r4, r5, r6, r7)
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            r1.saveConfig(r12)
            if (r26 != 0) goto L_0x009c
            r15.passcodeSaveIntent = r14
            r10 = r72
            r15.passcodeSaveIntentIsNew = r10
            r15.passcodeSaveIntentIsRestore = r0
            return r12
        L_0x009c:
            r10 = r72
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
            if (r1 != 0) goto L_0x1a2f
            java.lang.String r1 = r71.getAction()
            if (r1 == 0) goto L_0x1a2f
            if (r0 != 0) goto L_0x1a2f
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "android.intent.action.SEND"
            boolean r0 = r1.equals(r0)
            java.lang.String r1 = "android.intent.extra.STREAM"
            java.lang.String r2 = "\n"
            java.lang.String r4 = "hash"
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x0346
            boolean r0 = org.telegram.messenger.SharedConfig.directShare
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r71.getExtras()
            if (r0 == 0) goto L_0x0155
            android.os.Bundle r0 = r71.getExtras()
            java.lang.String r9 = "dialogId"
            long r19 = r0.getLong(r9, r5)
            int r0 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0142
            android.os.Bundle r0 = r71.getExtras()     // Catch:{ all -> 0x013c }
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
            java.lang.String r0 = r0.getString(r4, r7)     // Catch:{ all -> 0x0134 }
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
            android.os.Bundle r0 = r71.getExtras()
            r7 = 0
            java.lang.String r0 = r0.getString(r4, r7)
        L_0x014b:
            java.lang.String r4 = org.telegram.messenger.SharedConfig.directShareHash
            if (r4 == 0) goto L_0x0155
            boolean r0 = r4.equals(r0)
            if (r0 != 0) goto L_0x0157
        L_0x0155:
            r19 = r5
        L_0x0157:
            java.lang.String r4 = r71.getType()
            if (r4 == 0) goto L_0x019d
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x019d
            android.os.Bundle r0 = r71.getExtras()     // Catch:{ Exception -> 0x0196 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x0196 }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x0196 }
            if (r0 == 0) goto L_0x019a
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0196 }
            r2 = 0
            r3 = 0
            java.util.ArrayList r1 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r1, r3, r2, r2)     // Catch:{ Exception -> 0x0196 }
            r15.contactsToSend = r1     // Catch:{ Exception -> 0x0196 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0196 }
            r3 = 5
            if (r1 <= r3) goto L_0x0192
            r15.contactsToSend = r2     // Catch:{ Exception -> 0x0196 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0196 }
            r1.<init>()     // Catch:{ Exception -> 0x0196 }
            r15.documentsUrisArray = r1     // Catch:{ Exception -> 0x0196 }
            r1.add(r0)     // Catch:{ Exception -> 0x0196 }
            r15.documentsMimeType = r4     // Catch:{ Exception -> 0x0196 }
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
            r9.append(r2)
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
            android.os.Parcelable r0 = r14.getParcelableExtra(r1)
            if (r0 == 0) goto L_0x0324
            boolean r1 = r0 instanceof android.net.Uri
            if (r1 != 0) goto L_0x0202
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x0202:
            r1 = r0
            android.net.Uri r1 = (android.net.Uri) r1
            if (r1 == 0) goto L_0x020f
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)
            if (r0 == 0) goto L_0x020f
            r2 = 1
            goto L_0x0210
        L_0x020f:
            r2 = 0
        L_0x0210:
            if (r2 != 0) goto L_0x0322
            if (r1 == 0) goto L_0x0322
            if (r4 == 0) goto L_0x021e
            java.lang.String r0 = "image/"
            boolean r0 = r4.startsWith(r0)
            if (r0 != 0) goto L_0x022e
        L_0x021e:
            java.lang.String r0 = r1.toString()
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
            r0.uri = r1
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray
            r1.add(r0)
            goto L_0x0322
        L_0x0247:
            java.lang.String r7 = r1.toString()
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
            java.lang.String r9 = org.telegram.messenger.MediaController.getFileName(r1)
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
            r15.exportingChatUri = r1     // Catch:{ Exception -> 0x02a5 }
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
            r15.exportingChatUri = r1
        L_0x02c0:
            android.net.Uri r0 = r15.exportingChatUri
            if (r0 != 0) goto L_0x0322
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r1)
            boolean r7 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r7 != 0) goto L_0x02d2
            java.lang.String r0 = "file"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r1, r0)
        L_0x02d2:
            if (r0 == 0) goto L_0x0310
            java.lang.String r7 = "file:"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x02e2
            java.lang.String r7 = "file://"
            java.lang.String r0 = r0.replace(r7, r3)
        L_0x02e2:
            if (r4 == 0) goto L_0x02ef
            java.lang.String r3 = "video/"
            boolean r3 = r4.startsWith(r3)
            if (r3 == 0) goto L_0x02ef
            r15.videoPath = r0
            goto L_0x0322
        L_0x02ef:
            java.util.ArrayList<java.lang.String> r3 = r15.documentsPathsArray
            if (r3 != 0) goto L_0x0301
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15.documentsPathsArray = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15.documentsOriginalPathsArray = r3
        L_0x0301:
            java.util.ArrayList<java.lang.String> r3 = r15.documentsPathsArray
            r3.add(r0)
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray
            java.lang.String r1 = r1.toString()
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
            r0.add(r1)
            r15.documentsMimeType = r4
        L_0x0322:
            r0 = r2
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
            r12 = r2
            r61 = r12
            r5 = r8
            r7 = r15
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            goto L_0x18d7
        L_0x0346:
            java.lang.String r0 = r71.getAction()
            java.lang.String r7 = "org.telegram.messenger.CREATE_STICKER_PACK"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0377
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r1)     // Catch:{ all -> 0x036a }
            r15.importingStickers = r0     // Catch:{ all -> 0x036a }
            java.lang.String r0 = "STICKER_EMOJIS"
            java.util.ArrayList r0 = r14.getStringArrayListExtra(r0)     // Catch:{ all -> 0x036a }
            r15.importingStickersEmoji = r0     // Catch:{ all -> 0x036a }
            java.lang.String r0 = "IMPORTER"
            java.lang.String r0 = r14.getStringExtra(r0)     // Catch:{ all -> 0x036a }
            r15.importingStickersSoftware = r0     // Catch:{ all -> 0x036a }
            goto L_0x1a2f
        L_0x036a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r15.importingStickers = r1
            r15.importingStickersEmoji = r1
            r15.importingStickersSoftware = r1
            goto L_0x1a2f
        L_0x0377:
            java.lang.String r0 = r71.getAction()
            java.lang.String r7 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0504
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r1)     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r1 = r71.getType()     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x03c0
            r2 = 0
        L_0x038e:
            int r4 = r0.size()     // Catch:{ Exception -> 0x04e9 }
            if (r2 >= r4) goto L_0x03b8
            java.lang.Object r4 = r0.get(r2)     // Catch:{ Exception -> 0x04e9 }
            android.os.Parcelable r4 = (android.os.Parcelable) r4     // Catch:{ Exception -> 0x04e9 }
            boolean r7 = r4 instanceof android.net.Uri     // Catch:{ Exception -> 0x04e9 }
            if (r7 != 0) goto L_0x03a6
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04e9 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x04e9 }
        L_0x03a6:
            android.net.Uri r4 = (android.net.Uri) r4     // Catch:{ Exception -> 0x04e9 }
            if (r4 == 0) goto L_0x03b5
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x04e9 }
            if (r4 == 0) goto L_0x03b5
            r0.remove(r2)     // Catch:{ Exception -> 0x04e9 }
            int r2 = r2 + -1
        L_0x03b5:
            r4 = 1
            int r2 = r2 + r4
            goto L_0x038e
        L_0x03b8:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x04e9 }
            if (r2 == 0) goto L_0x03c0
            r2 = 0
            goto L_0x03c1
        L_0x03c0:
            r2 = r0
        L_0x03c1:
            if (r2 == 0) goto L_0x04ed
            if (r1 == 0) goto L_0x0402
            java.lang.String r0 = "image/"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x0402
            r0 = 0
        L_0x03ce:
            int r1 = r2.size()     // Catch:{ Exception -> 0x04e9 }
            if (r0 >= r1) goto L_0x04e7
            java.lang.Object r1 = r2.get(r0)     // Catch:{ Exception -> 0x04e9 }
            android.os.Parcelable r1 = (android.os.Parcelable) r1     // Catch:{ Exception -> 0x04e9 }
            boolean r3 = r1 instanceof android.net.Uri     // Catch:{ Exception -> 0x04e9 }
            if (r3 != 0) goto L_0x03e6
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04e9 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x04e9 }
        L_0x03e6:
            android.net.Uri r1 = (android.net.Uri) r1     // Catch:{ Exception -> 0x04e9 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r3 = r15.photoPathsArray     // Catch:{ Exception -> 0x04e9 }
            if (r3 != 0) goto L_0x03f3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x04e9 }
            r3.<init>()     // Catch:{ Exception -> 0x04e9 }
            r15.photoPathsArray = r3     // Catch:{ Exception -> 0x04e9 }
        L_0x03f3:
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x04e9 }
            r3.<init>()     // Catch:{ Exception -> 0x04e9 }
            r3.uri = r1     // Catch:{ Exception -> 0x04e9 }
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r1 = r15.photoPathsArray     // Catch:{ Exception -> 0x04e9 }
            r1.add(r3)     // Catch:{ Exception -> 0x04e9 }
            int r0 = r0 + 1
            goto L_0x03ce
        L_0x0402:
            r4 = 0
            r0 = r11[r4]     // Catch:{ Exception -> 0x04e9 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x04e9 }
            java.util.Set<java.lang.String> r4 = r0.exportUri     // Catch:{ Exception -> 0x04e9 }
            r7 = 0
        L_0x040c:
            int r0 = r2.size()     // Catch:{ Exception -> 0x04e9 }
            if (r7 >= r0) goto L_0x04e7
            java.lang.Object r0 = r2.get(r7)     // Catch:{ Exception -> 0x04e9 }
            android.os.Parcelable r0 = (android.os.Parcelable) r0     // Catch:{ Exception -> 0x04e9 }
            boolean r9 = r0 instanceof android.net.Uri     // Catch:{ Exception -> 0x04e9 }
            if (r9 != 0) goto L_0x0424
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04e9 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x04e9 }
        L_0x0424:
            r9 = r0
            android.net.Uri r9 = (android.net.Uri) r9     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r12 = org.telegram.messenger.AndroidUtilities.getPath(r9)     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04e9 }
            if (r0 != 0) goto L_0x0433
            r13 = r12
            goto L_0x0434
        L_0x0433:
            r13 = r0
        L_0x0434:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x044c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04e9 }
            r0.<init>()     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r5 = "export path = "
            r0.append(r5)     // Catch:{ Exception -> 0x04e9 }
            r0.append(r13)     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04e9 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x04e9 }
        L_0x044c:
            if (r13 == 0) goto L_0x04a2
            android.net.Uri r0 = r15.exportingChatUri     // Catch:{ Exception -> 0x04e9 }
            if (r0 != 0) goto L_0x04a2
            java.lang.String r0 = org.telegram.messenger.MediaController.getFileName(r9)     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r5 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x04e9 }
            java.util.Iterator r6 = r4.iterator()     // Catch:{ Exception -> 0x04e9 }
        L_0x045e:
            boolean r0 = r6.hasNext()     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x048b
            java.lang.Object r0 = r6.next()     // Catch:{ Exception -> 0x04e9 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x04e9 }
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x0486 }
            java.util.regex.Matcher r23 = r0.matcher(r13)     // Catch:{ Exception -> 0x0486 }
            boolean r23 = r23.find()     // Catch:{ Exception -> 0x0486 }
            if (r23 != 0) goto L_0x0482
            java.util.regex.Matcher r0 = r0.matcher(r5)     // Catch:{ Exception -> 0x0486 }
            boolean r0 = r0.find()     // Catch:{ Exception -> 0x0486 }
            if (r0 == 0) goto L_0x045e
        L_0x0482:
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x0486 }
            r0 = 1
            goto L_0x048c
        L_0x0486:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04e9 }
            goto L_0x045e
        L_0x048b:
            r0 = 0
        L_0x048c:
            if (r0 == 0) goto L_0x048f
            goto L_0x04e1
        L_0x048f:
            java.lang.String r0 = "content://com.kakao.talk"
            boolean r0 = r13.startsWith(r0)     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x04a2
            java.lang.String r0 = "KakaoTalkChats.txt"
            boolean r0 = r13.endsWith(r0)     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x04a2
            r15.exportingChatUri = r9     // Catch:{ Exception -> 0x04e9 }
            goto L_0x04e1
        L_0x04a2:
            if (r12 == 0) goto L_0x04cf
            java.lang.String r0 = "file:"
            boolean r0 = r12.startsWith(r0)     // Catch:{ Exception -> 0x04e9 }
            if (r0 == 0) goto L_0x04b2
            java.lang.String r0 = "file://"
            java.lang.String r12 = r12.replace(r0, r3)     // Catch:{ Exception -> 0x04e9 }
        L_0x04b2:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04e9 }
            if (r0 != 0) goto L_0x04c4
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04e9 }
            r0.<init>()     // Catch:{ Exception -> 0x04e9 }
            r15.documentsPathsArray = r0     // Catch:{ Exception -> 0x04e9 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04e9 }
            r0.<init>()     // Catch:{ Exception -> 0x04e9 }
            r15.documentsOriginalPathsArray = r0     // Catch:{ Exception -> 0x04e9 }
        L_0x04c4:
            java.util.ArrayList<java.lang.String> r0 = r15.documentsPathsArray     // Catch:{ Exception -> 0x04e9 }
            r0.add(r12)     // Catch:{ Exception -> 0x04e9 }
            java.util.ArrayList<java.lang.String> r0 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x04e9 }
            r0.add(r13)     // Catch:{ Exception -> 0x04e9 }
            goto L_0x04e1
        L_0x04cf:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04e9 }
            if (r0 != 0) goto L_0x04da
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04e9 }
            r0.<init>()     // Catch:{ Exception -> 0x04e9 }
            r15.documentsUrisArray = r0     // Catch:{ Exception -> 0x04e9 }
        L_0x04da:
            java.util.ArrayList<android.net.Uri> r0 = r15.documentsUrisArray     // Catch:{ Exception -> 0x04e9 }
            r0.add(r9)     // Catch:{ Exception -> 0x04e9 }
            r15.documentsMimeType = r1     // Catch:{ Exception -> 0x04e9 }
        L_0x04e1:
            int r7 = r7 + 1
            r5 = 0
            goto L_0x040c
        L_0x04e7:
            r0 = 0
            goto L_0x04ee
        L_0x04e9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04ed:
            r0 = 1
        L_0x04ee:
            if (r0 == 0) goto L_0x04fa
            java.lang.String r0 = "Unsupported content"
            r1 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r15, r0, r1)
            r0.show()
        L_0x04fa:
            r5 = r8
            r8 = r14
            r7 = r15
            r10 = 0
            r60 = -1
            r61 = 0
            goto L_0x1a37
        L_0x0504:
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "android.intent.action.VIEW"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x18b2
            android.net.Uri r0 = r71.getData()
            if (r0 == 0) goto L_0x1868
            java.lang.String r1 = r0.getScheme()
            java.lang.String r5 = "actions.fulfillment.extra.ACTION_TOKEN"
            java.lang.String r6 = "phone"
            if (r1 == 0) goto L_0x164a
            int r7 = r1.hashCode()
            switch(r7) {
                case 3699: goto L_0x053f;
                case 3213448: goto L_0x0534;
                case 99617003: goto L_0x0529;
                default: goto L_0x0527;
            }
        L_0x0527:
            r7 = -1
            goto L_0x0549
        L_0x0529:
            java.lang.String r7 = "https"
            boolean r7 = r1.equals(r7)
            if (r7 != 0) goto L_0x0532
            goto L_0x0527
        L_0x0532:
            r7 = 2
            goto L_0x0549
        L_0x0534:
            java.lang.String r7 = "http"
            boolean r7 = r1.equals(r7)
            if (r7 != 0) goto L_0x053d
            goto L_0x0527
        L_0x053d:
            r7 = 1
            goto L_0x0549
        L_0x053f:
            java.lang.String r7 = "tg"
            boolean r7 = r1.equals(r7)
            if (r7 != 0) goto L_0x0548
            goto L_0x0527
        L_0x0548:
            r7 = 0
        L_0x0549:
            java.lang.String r9 = "thread"
            r23 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            switch(r7) {
                case 0: goto L_0x0abe;
                case 1: goto L_0x0552;
                case 2: goto L_0x0552;
                default: goto L_0x0550;
            }
        L_0x0550:
            goto L_0x164a
        L_0x0552:
            java.lang.String r7 = r0.getHost()
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r13 = "telegram.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0572
            java.lang.String r13 = "t.me"
            boolean r13 = r7.equals(r13)
            if (r13 != 0) goto L_0x0572
            java.lang.String r13 = "telegram.dog"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x164a
        L_0x0572:
            java.lang.String r7 = r0.getPath()
            if (r7 == 0) goto L_0x0a50
            int r13 = r7.length()
            r12 = 1
            if (r13 <= r12) goto L_0x0a50
            java.lang.String r7 = r7.substring(r12)
            java.lang.String r12 = "bg/"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0794
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r2.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r9 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r9.<init>()
            r2.settings = r9
            java.lang.String r9 = "bg/"
            java.lang.String r3 = r7.replace(r9, r3)
            r2.slug = r3
            if (r3 == 0) goto L_0x05bc
            int r3 = r3.length()
            r7 = 6
            if (r3 != r7) goto L_0x05bc
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0674 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x0674 }
            r7 = r7 | r23
            r3.background_color = r7     // Catch:{ Exception -> 0x0674 }
            r3 = 0
            r2.slug = r3     // Catch:{ Exception -> 0x0674 }
        L_0x05b9:
            r3 = 1
            goto L_0x0675
        L_0x05bc:
            java.lang.String r3 = r2.slug
            if (r3 == 0) goto L_0x0674
            int r3 = r3.length()
            r7 = 13
            if (r3 < r7) goto L_0x0674
            java.lang.String r3 = r2.slug
            r7 = 6
            char r3 = r3.charAt(r7)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)
            if (r3 == 0) goto L_0x0674
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0674 }
            java.lang.String r9 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r12 = 0
            java.lang.String r9 = r9.substring(r12, r7)     // Catch:{ Exception -> 0x0674 }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x0674 }
            r7 = r9 | r23
            r3.background_color = r7     // Catch:{ Exception -> 0x0674 }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0674 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r9 = 7
            r12 = 13
            java.lang.String r7 = r7.substring(r9, r12)     // Catch:{ Exception -> 0x0674 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x0674 }
            r7 = r7 | r23
            r3.second_background_color = r7     // Catch:{ Exception -> 0x0674 }
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x0674 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x0674 }
            r7 = 20
            if (r3 < r7) goto L_0x062b
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r7 = 13
            char r3 = r3.charAt(r7)     // Catch:{ Exception -> 0x0674 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x0674 }
            if (r3 == 0) goto L_0x062b
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0674 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r9 = 14
            r12 = 20
            java.lang.String r7 = r7.substring(r9, r12)     // Catch:{ Exception -> 0x0674 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x0674 }
            r7 = r7 | r23
            r3.third_background_color = r7     // Catch:{ Exception -> 0x0674 }
        L_0x062b:
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x0674 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x0674 }
            r7 = 27
            if (r3 != r7) goto L_0x0657
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r7 = 20
            char r3 = r3.charAt(r7)     // Catch:{ Exception -> 0x0674 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x0674 }
            if (r3 == 0) goto L_0x0657
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0674 }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x0674 }
            r9 = 21
            java.lang.String r7 = r7.substring(r9)     // Catch:{ Exception -> 0x0674 }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x0674 }
            r7 = r7 | r23
            r3.fourth_background_color = r7     // Catch:{ Exception -> 0x0674 }
        L_0x0657:
            java.lang.String r3 = "rotation"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x066f }
            boolean r7 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x066f }
            if (r7 != 0) goto L_0x066f
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x066f }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x066f }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x066f }
            r7.rotation = r3     // Catch:{ Exception -> 0x066f }
        L_0x066f:
            r3 = 0
            r2.slug = r3     // Catch:{ Exception -> 0x0674 }
            goto L_0x05b9
        L_0x0674:
            r3 = 0
        L_0x0675:
            if (r3 != 0) goto L_0x0776
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x06b4
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r7 = " "
            java.lang.String[] r3 = r3.split(r7)
            if (r3 == 0) goto L_0x06b4
            int r7 = r3.length
            if (r7 <= 0) goto L_0x06b4
            r7 = 0
        L_0x068f:
            int r9 = r3.length
            if (r7 >= r9) goto L_0x06b4
            r9 = r3[r7]
            java.lang.String r12 = "blur"
            boolean r9 = r12.equals(r9)
            if (r9 == 0) goto L_0x06a2
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r12 = 1
            r9.blur = r12
            goto L_0x06b1
        L_0x06a2:
            r12 = 1
            r9 = r3[r7]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x06b1
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r9.motion = r12
        L_0x06b1:
            int r7 = r7 + 1
            goto L_0x068f
        L_0x06b4:
            java.lang.String r3 = "intensity"
            java.lang.String r3 = r0.getQueryParameter(r3)
            boolean r7 = android.text.TextUtils.isEmpty(r3)
            if (r7 != 0) goto L_0x06cd
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r3 = r3.intValue()
            r7.intensity = r3
            goto L_0x06d3
        L_0x06cd:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings
            r7 = 50
            r3.intensity = r7
        L_0x06d3:
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0756 }
            boolean r7 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0756 }
            if (r7 != 0) goto L_0x0758
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0756 }
            r9 = 6
            r12 = 0
            java.lang.String r13 = r3.substring(r12, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r13, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = r12 | r23
            r7.background_color = r9     // Catch:{ Exception -> 0x0756 }
            int r7 = r3.length()     // Catch:{ Exception -> 0x0756 }
            r9 = 13
            if (r7 < r9) goto L_0x0756
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0756 }
            r12 = 7
            java.lang.String r12 = r3.substring(r12, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = r12 | r23
            r7.second_background_color = r9     // Catch:{ Exception -> 0x0756 }
            int r7 = r3.length()     // Catch:{ Exception -> 0x0756 }
            r9 = 20
            if (r7 < r9) goto L_0x0730
            r7 = 13
            char r7 = r3.charAt(r7)     // Catch:{ Exception -> 0x0756 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x0756 }
            if (r7 == 0) goto L_0x0730
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0756 }
            r12 = 14
            java.lang.String r12 = r3.substring(r12, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = 16
            int r12 = java.lang.Integer.parseInt(r12, r9)     // Catch:{ Exception -> 0x0756 }
            r9 = r12 | r23
            r7.third_background_color = r9     // Catch:{ Exception -> 0x0756 }
        L_0x0730:
            int r7 = r3.length()     // Catch:{ Exception -> 0x0756 }
            r9 = 27
            if (r7 != r9) goto L_0x0756
            r7 = 20
            char r7 = r3.charAt(r7)     // Catch:{ Exception -> 0x0756 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x0756 }
            if (r7 == 0) goto L_0x0756
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0756 }
            r9 = 21
            java.lang.String r3 = r3.substring(r9)     // Catch:{ Exception -> 0x0756 }
            r9 = 16
            int r3 = java.lang.Integer.parseInt(r3, r9)     // Catch:{ Exception -> 0x0756 }
            r3 = r3 | r23
            r7.fourth_background_color = r3     // Catch:{ Exception -> 0x0756 }
        L_0x0756:
            r12 = -1
            goto L_0x075d
        L_0x0758:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0756 }
            r12 = -1
            r3.background_color = r12     // Catch:{ Exception -> 0x075d }
        L_0x075d:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x0777 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0777 }
            if (r3 != 0) goto L_0x0777
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x0777 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0777 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0777 }
            r3.rotation = r0     // Catch:{ Exception -> 0x0777 }
            goto L_0x0777
        L_0x0776:
            r12 = -1
        L_0x0777:
            r34 = r2
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
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
            goto L_0x0a6b
        L_0x0794:
            r12 = -1
            java.lang.String r13 = "login/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x07d9
            java.lang.String r0 = "login/"
            java.lang.String r0 = r7.replace(r0, r3)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x07bd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x07be
        L_0x07bd:
            r0 = 0
        L_0x07be:
            r33 = r0
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
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
            goto L_0x0a69
        L_0x07d9:
            java.lang.String r13 = "joinchat/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x07eb
            java.lang.String r0 = "joinchat/"
            java.lang.String r0 = r7.replace(r0, r3)
        L_0x07e7:
            r2 = r0
            r0 = 0
            goto L_0x0a52
        L_0x07eb:
            java.lang.String r13 = "+"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x07fa
            java.lang.String r0 = "+"
            java.lang.String r0 = r7.replace(r0, r3)
            goto L_0x07e7
        L_0x07fa:
            java.lang.String r13 = "addstickers/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x080f
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r7.replace(r0, r3)
            r9 = r0
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
            goto L_0x0a55
        L_0x080f:
            java.lang.String r13 = "msg/"
            boolean r13 = r7.startsWith(r13)
            if (r13 != 0) goto L_0x09e2
            java.lang.String r13 = "share/"
            boolean r13 = r7.startsWith(r13)
            if (r13 == 0) goto L_0x0821
            goto L_0x09e2
        L_0x0821:
            java.lang.String r2 = "confirmphone"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0846
            java.lang.String r2 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r30 = r0
            r24 = r2
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            goto L_0x0a63
        L_0x0846:
            java.lang.String r2 = "setlanguage/"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x086b
            r0 = 12
            java.lang.String r0 = r7.substring(r0)
            r31 = r0
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            goto L_0x0a65
        L_0x086b:
            java.lang.String r2 = "addtheme/"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0892
            r0 = 9
            java.lang.String r0 = r7.substring(r0)
            r32 = r0
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
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
            goto L_0x0a67
        L_0x0892:
            java.lang.String r2 = "c/"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0904
            java.util.List r2 = r0.getPathSegments()
            int r3 = r2.size()
            r7 = 3
            if (r3 != r7) goto L_0x08dd
            r3 = 1
            java.lang.Object r7 = r2.get(r3)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r7)
            r13 = 2
            java.lang.Object r2 = r2.get(r13)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r7 = r2.intValue()
            if (r7 == 0) goto L_0x08cb
            long r16 = r3.longValue()
            r19 = 0
            int r7 = (r16 > r19 ? 1 : (r16 == r19 ? 0 : -1))
            if (r7 != 0) goto L_0x08cd
        L_0x08cb:
            r2 = 0
            r3 = 0
        L_0x08cd:
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r7 = r0.intValue()
            if (r7 != 0) goto L_0x08e1
            r0 = 0
            goto L_0x08e1
        L_0x08dd:
            r13 = 2
            r0 = 0
            r2 = 0
            r3 = 0
        L_0x08e1:
            r37 = r0
            r35 = r2
            r36 = r3
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = -1
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
            r34 = 0
            goto L_0x0a71
        L_0x0904:
            r13 = 2
            int r2 = r7.length()
            r3 = 1
            if (r2 < r3) goto L_0x0a50
            java.util.ArrayList r2 = new java.util.ArrayList
            java.util.List r3 = r0.getPathSegments()
            r2.<init>(r3)
            int r3 = r2.size()
            if (r3 <= 0) goto L_0x092e
            r3 = 0
            java.lang.Object r7 = r2.get(r3)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.String r12 = "s"
            boolean r7 = r7.equals(r12)
            if (r7 == 0) goto L_0x092f
            r2.remove(r3)
            goto L_0x092f
        L_0x092e:
            r3 = 0
        L_0x092f:
            int r7 = r2.size()
            if (r7 <= 0) goto L_0x0955
            java.lang.Object r7 = r2.get(r3)
            r3 = r7
            java.lang.String r3 = (java.lang.String) r3
            int r7 = r2.size()
            r12 = 1
            if (r7 <= r12) goto L_0x0953
            java.lang.Object r2 = r2.get(r12)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r7 = r2.intValue()
            if (r7 != 0) goto L_0x0957
        L_0x0953:
            r2 = 0
            goto L_0x0957
        L_0x0955:
            r2 = 0
            r3 = 0
        L_0x0957:
            if (r2 == 0) goto L_0x095e
            int r7 = getTimestampFromLink(r0)
            goto L_0x095f
        L_0x095e:
            r7 = -1
        L_0x095f:
            java.lang.String r12 = "start"
            java.lang.String r12 = r0.getQueryParameter(r12)
            java.lang.String r13 = "startgroup"
            java.lang.String r13 = r0.getQueryParameter(r13)
            r73 = r2
            java.lang.String r2 = "game"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r23 = r2
            java.lang.String r2 = "voicechat"
            java.lang.String r2 = r0.getQueryParameter(r2)
            r24 = r2
            java.lang.String r2 = "livestream"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r27 = r9.intValue()
            if (r27 != 0) goto L_0x0995
            r27 = r2
            r9 = 0
            goto L_0x0997
        L_0x0995:
            r27 = r2
        L_0x0997:
            java.lang.String r2 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r2 = r0.intValue()
            r35 = r73
            if (r2 != 0) goto L_0x09c3
            r37 = r9
            r28 = r24
            r29 = r27
            r0 = 0
            r2 = 0
            r9 = 0
            r24 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r36 = 0
            r38 = 0
            goto L_0x09dc
        L_0x09c3:
            r38 = r0
            r37 = r9
            r28 = r24
            r29 = r27
            r0 = 0
            r2 = 0
            r9 = 0
            r24 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r36 = 0
        L_0x09dc:
            r27 = r23
            r23 = 0
            goto L_0x0a73
        L_0x09e2:
            java.lang.String r7 = "url"
            java.lang.String r7 = r0.getQueryParameter(r7)
            if (r7 != 0) goto L_0x09eb
            goto L_0x09ec
        L_0x09eb:
            r3 = r7
        L_0x09ec:
            java.lang.String r7 = "text"
            java.lang.String r7 = r0.getQueryParameter(r7)
            if (r7 == 0) goto L_0x0a22
            int r7 = r3.length()
            if (r7 <= 0) goto L_0x0a0b
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            r7.append(r2)
            java.lang.String r3 = r7.toString()
            r7 = 1
            goto L_0x0a0c
        L_0x0a0b:
            r7 = 0
        L_0x0a0c:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r9.append(r0)
            java.lang.String r3 = r9.toString()
            goto L_0x0a23
        L_0x0a22:
            r7 = 0
        L_0x0a23:
            int r0 = r3.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x0a33
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r3.substring(r9, r0)
            goto L_0x0a35
        L_0x0a33:
            r9 = 0
            r0 = r3
        L_0x0a35:
            boolean r3 = r0.endsWith(r2)
            if (r3 == 0) goto L_0x0a46
            int r3 = r0.length()
            r12 = 1
            int r3 = r3 - r12
            java.lang.String r0 = r0.substring(r9, r3)
            goto L_0x0a35
        L_0x0a46:
            r23 = r0
            r0 = r7
            r2 = 0
            r3 = 0
            r7 = -1
            r9 = 0
            r12 = 0
            r13 = 0
            goto L_0x0a59
        L_0x0a50:
            r0 = 0
            r2 = 0
        L_0x0a52:
            r3 = 0
            r7 = -1
            r9 = 0
        L_0x0a55:
            r12 = 0
            r13 = 0
            r23 = 0
        L_0x0a59:
            r24 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
        L_0x0a63:
            r31 = 0
        L_0x0a65:
            r32 = 0
        L_0x0a67:
            r33 = 0
        L_0x0a69:
            r34 = 0
        L_0x0a6b:
            r35 = 0
            r36 = 0
            r37 = 0
        L_0x0a71:
            r38 = 0
        L_0x0a73:
            r58 = r7
            r7 = r12
            r10 = r23
            r44 = r27
            r56 = r28
            r57 = r29
            r46 = r31
            r55 = r32
            r48 = r33
            r54 = r34
            r40 = r35
            r41 = r36
            r42 = r37
            r43 = r38
            r19 = 0
            r27 = 0
            r28 = 0
            r29 = 0
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
            r23 = r0
            r12 = r2
            r0 = r9
            r9 = r24
            r2 = r30
            r30 = 0
            goto L_0x1692
        L_0x0abe:
            java.lang.String r7 = r0.toString()
            java.lang.String r12 = "tg:resolve"
            boolean r12 = r7.startsWith(r12)
            java.lang.String r13 = "scope"
            java.lang.String r10 = "tg://telegram.org"
            if (r12 != 0) goto L_0x14ba
            java.lang.String r12 = "tg://resolve"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0ad8
            goto L_0x14ba
        L_0x0ad8:
            java.lang.String r12 = "tg:privatepost"
            boolean r12 = r7.startsWith(r12)
            if (r12 != 0) goto L_0x1427
            java.lang.String r12 = "tg://privatepost"
            boolean r12 = r7.startsWith(r12)
            if (r12 == 0) goto L_0x0aea
            goto L_0x1427
        L_0x0aea:
            java.lang.String r9 = "tg:bg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x11e7
            java.lang.String r9 = "tg://bg"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0afc
            goto L_0x11e7
        L_0x0afc:
            java.lang.String r9 = "tg:join"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x1191
            java.lang.String r9 = "tg://join"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b0e
            goto L_0x1191
        L_0x0b0e:
            java.lang.String r9 = "tg:addstickers"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x1174
            java.lang.String r9 = "tg://addstickers"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b20
            goto L_0x1174
        L_0x0b20:
            java.lang.String r9 = "tg:msg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10e7
            java.lang.String r9 = "tg://msg"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10e7
            java.lang.String r9 = "tg://share"
            boolean r9 = r7.startsWith(r9)
            if (r9 != 0) goto L_0x10e7
            java.lang.String r9 = "tg:share"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x0b42
            goto L_0x10e7
        L_0x0b42:
            java.lang.String r2 = "tg:confirmphone"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x1085
            java.lang.String r2 = "tg://confirmphone"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0b54
            goto L_0x1085
        L_0x0b54:
            java.lang.String r2 = "tg:login"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x1012
            java.lang.String r2 = "tg://login"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0b66
            goto L_0x1012
        L_0x0b66:
            java.lang.String r2 = "tg:openmessage"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0fc1
            java.lang.String r2 = "tg://openmessage"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0b78
            goto L_0x0fc1
        L_0x0b78:
            java.lang.String r2 = "tg:passport"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0f1d
            java.lang.String r2 = "tg://passport"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0f1d
            java.lang.String r2 = "tg:secureid"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0b92
            goto L_0x0f1d
        L_0x0b92:
            java.lang.String r2 = "tg:setlanguage"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0ed1
            java.lang.String r2 = "tg://setlanguage"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0ba4
            goto L_0x0ed1
        L_0x0ba4:
            java.lang.String r2 = "tg:addtheme"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0e77
            java.lang.String r2 = "tg://addtheme"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0bb6
            goto L_0x0e77
        L_0x0bb6:
            java.lang.String r2 = "tg:settings"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0dfb
            java.lang.String r2 = "tg://settings"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0bc8
            goto L_0x0dfb
        L_0x0bc8:
            java.lang.String r2 = "tg:search"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0dbd
            java.lang.String r2 = "tg://search"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0bda
            goto L_0x0dbd
        L_0x0bda:
            java.lang.String r2 = "tg:calllog"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0da9
            java.lang.String r2 = "tg://calllog"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0bec
            goto L_0x0da9
        L_0x0bec:
            java.lang.String r2 = "tg:call"
            boolean r2 = r7.startsWith(r2)
            if (r2 != 0) goto L_0x0cd4
            java.lang.String r2 = "tg://call"
            boolean r2 = r7.startsWith(r2)
            if (r2 == 0) goto L_0x0bfe
            goto L_0x0cd4
        L_0x0bfe:
            java.lang.String r0 = "tg:scanqr"
            boolean r0 = r7.startsWith(r0)
            if (r0 != 0) goto L_0x0cb4
            java.lang.String r0 = "tg://scanqr"
            boolean r0 = r7.startsWith(r0)
            if (r0 == 0) goto L_0x0CLASSNAME
            goto L_0x0cb4
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
            java.lang.String r2 = "tg:"
            java.lang.String r0 = r0.replace(r2, r3)
            r2 = 63
            int r2 = r0.indexOf(r2)
            if (r2 < 0) goto L_0x0c3a
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r2)
        L_0x0c3a:
            r47 = r0
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11dd
        L_0x0CLASSNAME:
            java.lang.String r0 = "tg:addcontact"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://addcontact"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "name"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r39 = r0
            r38 = r2
            r0 = 0
            r2 = 0
            r3 = 0
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
            r33 = 1
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            goto L_0x11cd
        L_0x0cb4:
            r0 = 0
            r2 = 0
            r3 = 0
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
            r35 = 1
            goto L_0x11c5
        L_0x0cd4:
            int r2 = r15.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x164a
            int r2 = r15.currentAccount
            org.telegram.messenger.ContactsController r2 = org.telegram.messenger.ContactsController.getInstance(r2)
            boolean r2 = r2.contactsLoaded
            if (r2 != 0) goto L_0x0d16
            java.lang.String r2 = "extra_force_call"
            boolean r2 = r14.hasExtra(r2)
            if (r2 == 0) goto L_0x0cf3
            goto L_0x0d16
        L_0x0cf3:
            android.content.Intent r0 = new android.content.Intent
            r0.<init>(r14)
            r0.removeExtra(r5)
            java.lang.String r2 = "extra_force_call"
            r3 = 1
            r0.putExtra(r2, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda54 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda54
            r2.<init>(r15, r0)
            r9 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.ContactsLoadingObserver.observe(r2, r9)
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            goto L_0x0d6b
        L_0x0d16:
            java.lang.String r2 = "format"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "name"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r0 = r0.getQueryParameter(r6)
            r9 = 0
            java.util.List r10 = r15.findContacts(r7, r0, r9)
            boolean r12 = r10.isEmpty()
            if (r12 == 0) goto L_0x0d3e
            if (r0 == 0) goto L_0x0d3e
            r23 = r0
            r13 = r7
            r0 = 0
            r2 = 1
            r3 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            goto L_0x0d6b
        L_0x0d3e:
            int r0 = r10.size()
            r12 = 1
            if (r0 != r12) goto L_0x0d4e
            java.lang.Object r0 = r10.get(r9)
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0
            long r9 = r0.user_id
            goto L_0x0d50
        L_0x0d4e:
            r9 = 0
        L_0x0d50:
            r12 = 0
            int r0 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x0d5a
            if (r7 == 0) goto L_0x0d5b
            r3 = r7
            goto L_0x0d5b
        L_0x0d5a:
            r3 = 0
        L_0x0d5b:
            java.lang.String r0 = "video"
            boolean r0 = r0.equalsIgnoreCase(r2)
            r2 = r0 ^ 1
            r7 = r0
            r12 = r3
            r0 = 1
            r13 = 0
            r23 = 0
            r3 = r2
            r2 = 0
        L_0x0d6b:
            r32 = r0
            r34 = r2
            r30 = r3
            r31 = r7
            r50 = r9
            r37 = r12
            r38 = r13
            r39 = r23
            r0 = 0
            r2 = 0
            r3 = 0
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
            r33 = 0
            r35 = 0
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
            goto L_0x11e3
        L_0x0da9:
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11b9
        L_0x0dbd:
            java.lang.String r0 = "tg:search"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://search"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "query"
            java.lang.String r0 = r0.getQueryParameter(r2)
            if (r0 == 0) goto L_0x0dd9
            java.lang.String r3 = r0.trim()
        L_0x0dd9:
            r36 = r3
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11c7
        L_0x0dfb:
            java.lang.String r0 = "themes"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e15
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 2
            goto L_0x11b7
        L_0x0e15:
            java.lang.String r0 = "devices"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e2f
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 3
            goto L_0x11b7
        L_0x0e2f:
            java.lang.String r0 = "folders"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e4a
            r0 = 4
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 4
            goto L_0x11b7
        L_0x0e4a:
            java.lang.String r0 = "change_number"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x0e65
            r0 = 5
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 5
            goto L_0x11b7
        L_0x0e65:
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            r27 = 0
            r28 = 1
            goto L_0x11b7
        L_0x0e77:
            java.lang.String r0 = "tg:addtheme"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://addtheme"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "slug"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r55 = r0
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x168c
        L_0x0ed1:
            java.lang.String r0 = "tg:setlanguage"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://setlanguage"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "lang"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r46 = r0
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11db
        L_0x0f1d:
            java.lang.String r0 = "tg:passport"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://passport"
            java.lang.String r0 = r0.replace(r2, r10)
            java.lang.String r2 = "tg:secureid"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.lang.String r3 = r0.getQueryParameter(r13)
            boolean r7 = android.text.TextUtils.isEmpty(r3)
            if (r7 != 0) goto L_0x0f5e
            java.lang.String r7 = "{"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x0f5e
            java.lang.String r7 = "}"
            boolean r7 = r3.endsWith(r7)
            if (r7 == 0) goto L_0x0f5e
            java.lang.String r7 = "nonce"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "nonce"
            r2.put(r9, r7)
            goto L_0x0var_
        L_0x0f5e:
            java.lang.String r7 = "payload"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "payload"
            r2.put(r9, r7)
        L_0x0var_:
            java.lang.String r7 = "bot_id"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "bot_id"
            r2.put(r9, r7)
            r2.put(r13, r3)
            java.lang.String r3 = "public_key"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r7 = "public_key"
            r2.put(r7, r3)
            java.lang.String r3 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r3)
            java.lang.String r3 = "callback_url"
            r2.put(r3, r0)
            r45 = r2
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11d9
        L_0x0fc1:
            java.lang.String r0 = "tg:openmessage"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://openmessage"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "user_id"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r3 = "chat_id"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r0 = r0.getQueryParameter(r8)
            if (r2 == 0) goto L_0x0fe8
            long r2 = java.lang.Long.parseLong(r2)     // Catch:{ NumberFormatException -> 0x0ff2 }
            goto L_0x0ff4
        L_0x0fe8:
            if (r3 == 0) goto L_0x0ff2
            long r2 = java.lang.Long.parseLong(r3)     // Catch:{ NumberFormatException -> 0x0ff2 }
            r9 = r2
            r2 = 0
            goto L_0x0ff6
        L_0x0ff2:
            r2 = 0
        L_0x0ff4:
            r9 = 0
        L_0x0ff6:
            if (r0 == 0) goto L_0x0ffd
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0ffd }
            goto L_0x0ffe
        L_0x0ffd:
            r0 = 0
        L_0x0ffe:
            r27 = r0
            r50 = r2
            r52 = r9
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            r23 = 0
            goto L_0x165c
        L_0x1012:
            java.lang.String r0 = "tg:login"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://login"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "token"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "code"
            java.lang.String r0 = r0.getQueryParameter(r7)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x1048
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            goto L_0x1049
        L_0x1048:
            r0 = 0
        L_0x1049:
            r48 = r0
            r49 = r2
            r0 = 0
            r2 = 0
            r3 = 0
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
            goto L_0x11e1
        L_0x1085:
            java.lang.String r0 = "tg:confirmphone"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://confirmphone"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = r0.getQueryParameter(r6)
            java.lang.String r0 = r0.getQueryParameter(r4)
            r9 = r2
            r3 = 0
            r7 = 0
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
            r55 = 0
            r56 = 0
            r57 = 0
            r58 = -1
            r2 = r0
            r0 = 0
            goto L_0x1692
        L_0x10e7:
            java.lang.String r0 = "tg:msg"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r7 = "tg://msg"
            java.lang.String r0 = r0.replace(r7, r10)
            java.lang.String r7 = "tg://share"
            java.lang.String r0 = r0.replace(r7, r10)
            java.lang.String r7 = "tg:share"
            java.lang.String r0 = r0.replace(r7, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r7 = "url"
            java.lang.String r7 = r0.getQueryParameter(r7)
            if (r7 != 0) goto L_0x110c
            goto L_0x110d
        L_0x110c:
            r3 = r7
        L_0x110d:
            java.lang.String r7 = "text"
            java.lang.String r7 = r0.getQueryParameter(r7)
            if (r7 == 0) goto L_0x1143
            int r7 = r3.length()
            if (r7 <= 0) goto L_0x112c
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            r7.append(r2)
            java.lang.String r3 = r7.toString()
            r7 = 1
            goto L_0x112d
        L_0x112c:
            r7 = 0
        L_0x112d:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r3)
            java.lang.String r3 = "text"
            java.lang.String r0 = r0.getQueryParameter(r3)
            r9.append(r0)
            java.lang.String r3 = r9.toString()
            goto L_0x1144
        L_0x1143:
            r7 = 0
        L_0x1144:
            int r0 = r3.length()
            r9 = 16384(0x4000, float:2.2959E-41)
            if (r0 <= r9) goto L_0x1154
            r0 = 16384(0x4000, float:2.2959E-41)
            r9 = 0
            java.lang.String r0 = r3.substring(r9, r0)
            goto L_0x1156
        L_0x1154:
            r9 = 0
            r0 = r3
        L_0x1156:
            boolean r3 = r0.endsWith(r2)
            if (r3 == 0) goto L_0x1167
            int r3 = r0.length()
            r10 = 1
            int r3 = r3 - r10
            java.lang.String r0 = r0.substring(r9, r3)
            goto L_0x1156
        L_0x1167:
            r10 = r0
            r23 = r7
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            goto L_0x11b3
        L_0x1174:
            java.lang.String r0 = "tg:addstickers"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://addstickers"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "set"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            goto L_0x11ae
        L_0x1191:
            java.lang.String r0 = "tg:join"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://join"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "invite"
            java.lang.String r0 = r0.getQueryParameter(r2)
            r12 = r0
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
        L_0x11ae:
            r13 = 0
            r19 = 0
            r23 = 0
        L_0x11b3:
            r27 = 0
            r28 = 0
        L_0x11b7:
            r29 = 0
        L_0x11b9:
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
        L_0x11c5:
            r36 = 0
        L_0x11c7:
            r37 = 0
            r38 = 0
            r39 = 0
        L_0x11cd:
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
        L_0x11d9:
            r46 = 0
        L_0x11db:
            r47 = 0
        L_0x11dd:
            r48 = 0
            r49 = 0
        L_0x11e1:
            r50 = 0
        L_0x11e3:
            r52 = 0
            goto L_0x1688
        L_0x11e7:
            java.lang.String r0 = "tg:bg"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://bg"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = new org.telegram.tgnet.TLRPC$TL_wallPaper
            r2.<init>()
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r3 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r3.<init>()
            r2.settings = r3
            java.lang.String r3 = "slug"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r2.slug = r3
            if (r3 != 0) goto L_0x1215
            java.lang.String r3 = "color"
            java.lang.String r3 = r0.getQueryParameter(r3)
            r2.slug = r3
        L_0x1215:
            java.lang.String r3 = r2.slug
            if (r3 == 0) goto L_0x1235
            int r3 = r3.length()
            r7 = 6
            if (r3 != r7) goto L_0x1235
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x12ed }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x12ed }
            r7 = r7 | r23
            r3.background_color = r7     // Catch:{ Exception -> 0x12ed }
            r3 = 0
            r2.slug = r3     // Catch:{ Exception -> 0x12ed }
            r3 = 1
            r12 = 0
            goto L_0x12ef
        L_0x1235:
            java.lang.String r3 = r2.slug
            if (r3 == 0) goto L_0x12ed
            int r3 = r3.length()
            r7 = 13
            if (r3 < r7) goto L_0x12ed
            java.lang.String r3 = r2.slug
            r7 = 6
            char r3 = r3.charAt(r7)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)
            if (r3 == 0) goto L_0x12ed
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x12ed }
            java.lang.String r9 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r7)     // Catch:{ Exception -> 0x12ed }
            r7 = 16
            int r9 = java.lang.Integer.parseInt(r9, r7)     // Catch:{ Exception -> 0x12ed }
            r7 = r9 | r23
            r3.background_color = r7     // Catch:{ Exception -> 0x12ed }
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x12ed }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r9 = 7
            r10 = 13
            java.lang.String r7 = r7.substring(r9, r10)     // Catch:{ Exception -> 0x12ed }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x12ed }
            r7 = r7 | r23
            r3.second_background_color = r7     // Catch:{ Exception -> 0x12ed }
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x12ed }
            int r3 = r3.length()     // Catch:{ Exception -> 0x12ed }
            r7 = 20
            if (r3 < r7) goto L_0x12a4
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r7 = 13
            char r3 = r3.charAt(r7)     // Catch:{ Exception -> 0x12ed }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x12ed }
            if (r3 == 0) goto L_0x12a4
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x12ed }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r9 = 14
            r10 = 20
            java.lang.String r7 = r7.substring(r9, r10)     // Catch:{ Exception -> 0x12ed }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x12ed }
            r7 = r7 | r23
            r3.third_background_color = r7     // Catch:{ Exception -> 0x12ed }
        L_0x12a4:
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x12ed }
            int r3 = r3.length()     // Catch:{ Exception -> 0x12ed }
            r7 = 27
            if (r3 != r7) goto L_0x12d0
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r7 = 20
            char r3 = r3.charAt(r7)     // Catch:{ Exception -> 0x12ed }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x12ed }
            if (r3 == 0) goto L_0x12d0
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x12ed }
            java.lang.String r7 = r2.slug     // Catch:{ Exception -> 0x12ed }
            r9 = 21
            java.lang.String r7 = r7.substring(r9)     // Catch:{ Exception -> 0x12ed }
            r9 = 16
            int r7 = java.lang.Integer.parseInt(r7, r9)     // Catch:{ Exception -> 0x12ed }
            r7 = r7 | r23
            r3.fourth_background_color = r7     // Catch:{ Exception -> 0x12ed }
        L_0x12d0:
            java.lang.String r3 = "rotation"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x12e8 }
            boolean r7 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x12e8 }
            if (r7 != 0) goto L_0x12e8
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x12e8 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x12e8 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x12e8 }
            r7.rotation = r3     // Catch:{ Exception -> 0x12e8 }
        L_0x12e8:
            r12 = 0
            r2.slug = r12     // Catch:{ Exception -> 0x12ee }
            r3 = 1
            goto L_0x12ef
        L_0x12ed:
            r12 = 0
        L_0x12ee:
            r3 = 0
        L_0x12ef:
            if (r3 != 0) goto L_0x13df
            java.lang.String r3 = "mode"
            java.lang.String r3 = r0.getQueryParameter(r3)
            if (r3 == 0) goto L_0x132e
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r7 = " "
            java.lang.String[] r3 = r3.split(r7)
            if (r3 == 0) goto L_0x132e
            int r7 = r3.length
            if (r7 <= 0) goto L_0x132e
            r7 = 0
        L_0x1309:
            int r9 = r3.length
            if (r7 >= r9) goto L_0x132e
            r9 = r3[r7]
            java.lang.String r10 = "blur"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x131c
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r10 = 1
            r9.blur = r10
            goto L_0x132b
        L_0x131c:
            r10 = 1
            r9 = r3[r7]
            java.lang.String r13 = "motion"
            boolean r9 = r13.equals(r9)
            if (r9 == 0) goto L_0x132b
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r2.settings
            r9.motion = r10
        L_0x132b:
            int r7 = r7 + 1
            goto L_0x1309
        L_0x132e:
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings
            java.lang.String r7 = "intensity"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)
            int r7 = r7.intValue()
            r3.intensity = r7
            java.lang.String r3 = "bg_color"
            java.lang.String r3 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x13c5 }
            boolean r7 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x13c5 }
            if (r7 != 0) goto L_0x13c5
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x13c5 }
            r9 = 0
            r13 = 6
            java.lang.String r10 = r3.substring(r9, r13)     // Catch:{ Exception -> 0x13c6 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = r10 | r23
            r7.background_color = r9     // Catch:{ Exception -> 0x13c6 }
            int r7 = r3.length()     // Catch:{ Exception -> 0x13c6 }
            r9 = 13
            if (r7 < r9) goto L_0x13c6
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x13c6 }
            r10 = 8
            java.lang.String r10 = r3.substring(r10, r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = r10 | r23
            r7.second_background_color = r9     // Catch:{ Exception -> 0x13c6 }
            int r7 = r3.length()     // Catch:{ Exception -> 0x13c6 }
            r9 = 20
            if (r7 < r9) goto L_0x139e
            r7 = 13
            char r7 = r3.charAt(r7)     // Catch:{ Exception -> 0x13c6 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x13c6 }
            if (r7 == 0) goto L_0x139e
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x13c6 }
            r10 = 14
            java.lang.String r10 = r3.substring(r10, r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = 16
            int r10 = java.lang.Integer.parseInt(r10, r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = r10 | r23
            r7.third_background_color = r9     // Catch:{ Exception -> 0x13c6 }
        L_0x139e:
            int r7 = r3.length()     // Catch:{ Exception -> 0x13c6 }
            r9 = 27
            if (r7 != r9) goto L_0x13c6
            r7 = 20
            char r7 = r3.charAt(r7)     // Catch:{ Exception -> 0x13c6 }
            boolean r7 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r7)     // Catch:{ Exception -> 0x13c6 }
            if (r7 == 0) goto L_0x13c6
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x13c6 }
            r9 = 21
            java.lang.String r3 = r3.substring(r9)     // Catch:{ Exception -> 0x13c6 }
            r9 = 16
            int r3 = java.lang.Integer.parseInt(r3, r9)     // Catch:{ Exception -> 0x13c6 }
            r3 = r3 | r23
            r7.fourth_background_color = r3     // Catch:{ Exception -> 0x13c6 }
            goto L_0x13c6
        L_0x13c5:
            r13 = 6
        L_0x13c6:
            java.lang.String r3 = "rotation"
            java.lang.String r0 = r0.getQueryParameter(r3)     // Catch:{ Exception -> 0x13e0 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x13e0 }
            if (r3 != 0) goto L_0x13e0
            org.telegram.tgnet.TLRPC$WallPaperSettings r3 = r2.settings     // Catch:{ Exception -> 0x13e0 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x13e0 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x13e0 }
            r3.rotation = r0     // Catch:{ Exception -> 0x13e0 }
            goto L_0x13e0
        L_0x13df:
            r13 = 6
        L_0x13e0:
            r54 = r2
            r0 = r12
            r2 = r0
            r3 = r2
            r7 = r3
            r9 = r7
            r10 = r9
            r13 = r10
            r36 = r13
            r37 = r36
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
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r50 = 0
            r52 = 0
            goto L_0x1690
        L_0x1427:
            r12 = 0
            r13 = 6
            java.lang.String r0 = "tg:privatepost"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://privatepost"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "post"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r2)
            java.lang.String r2 = "channel"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)
            int r3 = r7.intValue()
            if (r3 == 0) goto L_0x145e
            long r23 = r2.longValue()
            r19 = 0
            int r3 = (r23 > r19 ? 1 : (r23 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x1462
            goto L_0x1460
        L_0x145e:
            r19 = 0
        L_0x1460:
            r2 = r12
            r7 = r2
        L_0x1462:
            java.lang.String r3 = r0.getQueryParameter(r9)
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)
            int r9 = r3.intValue()
            if (r9 != 0) goto L_0x1471
            r3 = r12
        L_0x1471:
            java.lang.String r9 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r9)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r9 = r0.intValue()
            if (r9 != 0) goto L_0x149b
            r41 = r2
            r42 = r3
            r40 = r7
            r0 = r12
            r2 = r0
            r3 = r2
            r7 = r3
            r9 = r7
            r10 = r9
            r13 = r10
            r36 = r13
            r37 = r36
            r38 = r37
            r39 = r38
            r43 = r39
            r44 = r43
            goto L_0x14b4
        L_0x149b:
            r43 = r0
            r41 = r2
            r42 = r3
            r40 = r7
            r0 = r12
            r2 = r0
            r3 = r2
            r7 = r3
            r9 = r7
            r10 = r9
            r13 = r10
            r36 = r13
            r37 = r36
            r38 = r37
            r39 = r38
            r44 = r39
        L_0x14b4:
            r45 = r44
            r46 = r45
            goto L_0x1553
        L_0x14ba:
            r3 = 6
            r12 = 0
            r19 = 0
            java.lang.String r0 = "tg:resolve"
            java.lang.String r0 = r7.replace(r0, r10)
            java.lang.String r2 = "tg://resolve"
            java.lang.String r0 = r0.replace(r2, r10)
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "domain"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.String r7 = "telegrampassport"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x157b
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.lang.String r7 = r0.getQueryParameter(r13)
            boolean r9 = android.text.TextUtils.isEmpty(r7)
            if (r9 != 0) goto L_0x1507
            java.lang.String r9 = "{"
            boolean r9 = r7.startsWith(r9)
            if (r9 == 0) goto L_0x1507
            java.lang.String r9 = "}"
            boolean r9 = r7.endsWith(r9)
            if (r9 == 0) goto L_0x1507
            java.lang.String r9 = "nonce"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "nonce"
            r2.put(r10, r9)
            goto L_0x1512
        L_0x1507:
            java.lang.String r9 = "payload"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "payload"
            r2.put(r10, r9)
        L_0x1512:
            java.lang.String r9 = "bot_id"
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.String r10 = "bot_id"
            r2.put(r10, r9)
            r2.put(r13, r7)
            java.lang.String r7 = "public_key"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r9 = "public_key"
            r2.put(r9, r7)
            java.lang.String r7 = "callback_url"
            java.lang.String r0 = r0.getQueryParameter(r7)
            java.lang.String r7 = "callback_url"
            r2.put(r7, r0)
            r45 = r2
            r0 = r12
            r2 = r0
            r3 = r2
            r7 = r3
            r9 = r7
            r10 = r9
            r13 = r10
            r36 = r13
            r37 = r36
            r38 = r37
            r39 = r38
            r40 = r39
            r41 = r40
            r42 = r41
            r43 = r42
            r44 = r43
            r46 = r44
        L_0x1553:
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
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            goto L_0x1690
        L_0x157b:
            java.lang.String r7 = "start"
            java.lang.String r7 = r0.getQueryParameter(r7)
            java.lang.String r10 = "startgroup"
            java.lang.String r10 = r0.getQueryParameter(r10)
            java.lang.String r13 = "game"
            java.lang.String r13 = r0.getQueryParameter(r13)
            java.lang.String r3 = "voicechat"
            java.lang.String r3 = r0.getQueryParameter(r3)
            java.lang.String r12 = "livestream"
            java.lang.String r12 = r0.getQueryParameter(r12)
            r73 = r2
            java.lang.String r2 = "post"
            java.lang.String r2 = r0.getQueryParameter(r2)
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)
            int r23 = r2.intValue()
            if (r23 != 0) goto L_0x15ac
            r2 = 0
        L_0x15ac:
            java.lang.String r9 = r0.getQueryParameter(r9)
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r23 = r9.intValue()
            if (r23 != 0) goto L_0x15be
            r23 = r2
            r9 = 0
            goto L_0x15c0
        L_0x15be:
            r23 = r2
        L_0x15c0:
            java.lang.String r2 = "comment"
            java.lang.String r0 = r0.getQueryParameter(r2)
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)
            int r2 = r0.intValue()
            if (r2 != 0) goto L_0x1603
            r56 = r3
            r42 = r9
            r57 = r12
            r44 = r13
            r50 = r19
            r52 = r50
            r40 = r23
            r0 = 0
            r2 = 0
            r9 = 0
            r12 = 0
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
            goto L_0x1635
        L_0x1603:
            r43 = r0
            r56 = r3
            r42 = r9
            r57 = r12
            r44 = r13
            r50 = r19
            r52 = r50
            r40 = r23
            r0 = 0
            r2 = 0
            r9 = 0
            r12 = 0
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
        L_0x1635:
            r45 = 0
            r46 = 0
            r47 = 0
            r48 = 0
            r49 = 0
            r54 = 0
            r55 = 0
            r58 = -1
            r3 = r73
            r13 = r10
            r10 = 0
            goto L_0x1692
        L_0x164a:
            r19 = 0
            r50 = r19
            r52 = r50
            r0 = 0
            r2 = 0
            r3 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r23 = 0
            r27 = 0
        L_0x165c:
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
        L_0x1688:
            r54 = 0
            r55 = 0
        L_0x168c:
            r56 = 0
            r57 = 0
        L_0x1690:
            r58 = -1
        L_0x1692:
            boolean r24 = r14.hasExtra(r5)
            if (r24 == 0) goto L_0x16dc
            r59 = r8
            int r8 = r15.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            boolean r8 = r8.isClientActivated()
            if (r8 == 0) goto L_0x16b2
            java.lang.String r8 = "tg"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x16b2
            if (r47 != 0) goto L_0x16b2
            r1 = 1
            goto L_0x16b3
        L_0x16b2:
            r1 = 0
        L_0x16b3:
            com.google.firebase.appindexing.builders.AssistActionBuilder r8 = new com.google.firebase.appindexing.builders.AssistActionBuilder
            r8.<init>()
            r73 = r4
            java.lang.String r4 = r14.getStringExtra(r5)
            com.google.firebase.appindexing.builders.AssistActionBuilder r4 = r8.setActionToken(r4)
            if (r1 == 0) goto L_0x16c7
            java.lang.String r1 = "http://schema.org/CompletedActionStatus"
            goto L_0x16c9
        L_0x16c7:
            java.lang.String r1 = "http://schema.org/FailedActionStatus"
        L_0x16c9:
            com.google.firebase.appindexing.Action$Builder r1 = r4.setActionStatus(r1)
            com.google.firebase.appindexing.Action r1 = r1.build()
            com.google.firebase.appindexing.FirebaseUserActions r4 = com.google.firebase.appindexing.FirebaseUserActions.getInstance(r70)
            r4.end(r1)
            r14.removeExtra(r5)
            goto L_0x16e0
        L_0x16dc:
            r73 = r4
            r59 = r8
        L_0x16e0:
            if (r48 != 0) goto L_0x16fa
            int r1 = r15.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x16ef
            goto L_0x16fa
        L_0x16ef:
            r66 = r11
            r7 = r15
            r61 = r19
            r68 = r59
            r60 = -1
            goto L_0x1861
        L_0x16fa:
            if (r9 != 0) goto L_0x1842
            if (r2 == 0) goto L_0x1700
            goto L_0x1842
        L_0x1700:
            if (r3 != 0) goto L_0x17e2
            if (r12 != 0) goto L_0x17e2
            if (r0 != 0) goto L_0x17e2
            if (r10 != 0) goto L_0x17e2
            if (r44 != 0) goto L_0x17e2
            if (r56 != 0) goto L_0x17e2
            if (r45 != 0) goto L_0x17e2
            if (r47 != 0) goto L_0x17e2
            if (r46 != 0) goto L_0x17e2
            if (r48 != 0) goto L_0x17e2
            if (r54 != 0) goto L_0x17e2
            if (r41 != 0) goto L_0x17e2
            if (r55 != 0) goto L_0x17e2
            if (r49 == 0) goto L_0x171e
            goto L_0x17e2
        L_0x171e:
            android.content.ContentResolver r60 = r70.getContentResolver()     // Catch:{ Exception -> 0x17cb }
            android.net.Uri r61 = r71.getData()     // Catch:{ Exception -> 0x17cb }
            r62 = 0
            r63 = 0
            r64 = 0
            r65 = 0
            android.database.Cursor r1 = r60.query(r61, r62, r63, r64, r65)     // Catch:{ Exception -> 0x17cb }
            if (r1 == 0) goto L_0x17ba
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x17b1 }
            if (r0 == 0) goto L_0x17ba
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x17b1 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x17b1 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x17b1 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x17b1 }
            r2 = 0
            r6 = 3
        L_0x174e:
            if (r2 >= r6) goto L_0x176d
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x176b }
            long r3 = r3.getClientUserId()     // Catch:{ all -> 0x176b }
            long r7 = (long) r0     // Catch:{ all -> 0x176b }
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x1767
            r3 = 0
            r11[r3] = r2     // Catch:{ all -> 0x176b }
            r0 = r11[r3]     // Catch:{ all -> 0x176b }
            r9 = 1
            r15.switchToAccount(r0, r9)     // Catch:{ all -> 0x17af }
            goto L_0x176e
        L_0x1767:
            r9 = 1
            int r2 = r2 + 1
            goto L_0x174e
        L_0x176b:
            r0 = move-exception
            goto L_0x17b3
        L_0x176d:
            r9 = 1
        L_0x176e:
            java.lang.String r0 = "data4"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x17af }
            long r2 = r1.getLong(r0)     // Catch:{ all -> 0x17af }
            r4 = 0
            r0 = r11[r4]     // Catch:{ all -> 0x17af }
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)     // Catch:{ all -> 0x17af }
            int r5 = org.telegram.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x17af }
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ all -> 0x17af }
            r0.postNotificationName(r5, r7)     // Catch:{ all -> 0x17af }
            java.lang.String r0 = "mimetype"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x17ab }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x17ab }
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"
            boolean r4 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x17ab }
            if (r4 == 0) goto L_0x179c
            r50 = r2
            r4 = 1
            goto L_0x17be
        L_0x179c:
            java.lang.String r4 = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"
            boolean r0 = android.text.TextUtils.equals(r0, r4)     // Catch:{ all -> 0x17ab }
            r50 = r2
            r4 = r30
            if (r0 == 0) goto L_0x17be
            r31 = 1
            goto L_0x17be
        L_0x17ab:
            r0 = move-exception
            r50 = r2
            goto L_0x17b4
        L_0x17af:
            r0 = move-exception
            goto L_0x17b4
        L_0x17b1:
            r0 = move-exception
            r6 = 3
        L_0x17b3:
            r9 = 1
        L_0x17b4:
            r1.close()     // Catch:{ all -> 0x17b7 }
        L_0x17b7:
            throw r0     // Catch:{ Exception -> 0x17b8 }
        L_0x17b8:
            r0 = move-exception
            goto L_0x17ce
        L_0x17ba:
            r6 = 3
            r9 = 1
            r4 = r30
        L_0x17be:
            if (r1 == 0) goto L_0x17c8
            r1.close()     // Catch:{ Exception -> 0x17c4 }
            goto L_0x17c8
        L_0x17c4:
            r0 = move-exception
            r30 = r4
            goto L_0x17ce
        L_0x17c8:
            r30 = r4
            goto L_0x17d1
        L_0x17cb:
            r0 = move-exception
            r6 = 3
            r9 = 1
        L_0x17ce:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x17d1:
            r66 = r11
            r7 = r15
            r61 = r19
            r12 = r27
            r13 = r28
            r5 = r50
            r68 = r59
            r60 = -1
            goto L_0x188d
        L_0x17e2:
            r6 = 3
            r9 = 1
            if (r10 == 0) goto L_0x1801
            java.lang.String r1 = "@"
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x1801
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = " "
            r1.append(r2)
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            r8 = r1
            goto L_0x1802
        L_0x1801:
            r8 = r10
        L_0x1802:
            r21 = 0
            r2 = r11[r21]
            r24 = 0
            r1 = r70
            r10 = 6
            r60 = -1
            r4 = r12
            r61 = r19
            r12 = 2
            r5 = r0
            r6 = r7
            r7 = r13
            r13 = r59
            r16 = 1
            r9 = r23
            r10 = r40
            r66 = r11
            r11 = r41
            r12 = r42
            r68 = r13
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
            r7 = r70
            goto L_0x1861
        L_0x1842:
            r66 = r11
            r61 = r19
            r68 = r59
            r60 = -1
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r0.putString(r6, r9)
            r1 = r73
            r0.putString(r1, r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda30
            r7 = r70
            r1.<init>(r7, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x1861:
            r12 = r27
            r13 = r28
            r5 = r50
            goto L_0x188d
        L_0x1868:
            r68 = r8
            r66 = r11
            r7 = r15
            r60 = -1
            r61 = 0
            r5 = r61
            r52 = r5
            r12 = 0
            r13 = 0
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
        L_0x188d:
            r2 = r5
            r10 = r12
            r8 = r13
            r67 = r30
            r15 = r31
            r9 = r36
            r1 = r37
            r6 = r38
            r73 = r39
            r12 = r52
            r19 = r61
            r11 = r66
            r5 = r68
            r0 = -1
            r4 = -1
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 0
            r23 = 0
            goto L_0x1a5b
        L_0x18b2:
            r68 = r8
            r66 = r11
            r7 = r15
            r60 = -1
            r61 = 0
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "org.telegram.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x18db
            r2 = r61
            r12 = r2
            r19 = r12
            r11 = r66
            r5 = r68
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 1
        L_0x18d7:
            r9 = 0
            r10 = 0
            goto L_0x1a44
        L_0x18db:
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1904
            r2 = r61
            r12 = r2
            r19 = r12
            r11 = r66
            r5 = r68
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 1
            goto L_0x1a4d
        L_0x1904:
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x19c7
            java.lang.String r0 = "chatId"
            r8 = r71
            r10 = 0
            int r0 = r8.getIntExtra(r0, r10)
            long r0 = (long) r0
            java.lang.String r2 = "chatId"
            long r0 = r8.getLongExtra(r2, r0)
            java.lang.String r2 = "userId"
            int r2 = r8.getIntExtra(r2, r10)
            long r2 = (long) r2
            java.lang.String r4 = "userId"
            long r2 = r8.getLongExtra(r4, r2)
            java.lang.String r4 = "encId"
            int r4 = r8.getIntExtra(r4, r10)
            java.lang.String r5 = "appWidgetId"
            int r5 = r8.getIntExtra(r5, r10)
            if (r5 == 0) goto L_0x194f
            java.lang.String r0 = "appWidgetType"
            int r4 = r8.getIntExtra(r0, r10)
            r60 = r5
            r0 = r61
            r2 = r0
            r11 = r66
            r5 = r68
            r6 = 0
            r9 = 6
        L_0x194c:
            r12 = 0
        L_0x194d:
            r13 = 0
            goto L_0x19a0
        L_0x194f:
            r5 = r68
            int r6 = r8.getIntExtra(r5, r10)
            int r9 = (r0 > r61 ? 1 : (r0 == r61 ? 0 : -1))
            if (r9 == 0) goto L_0x196d
            r11 = r66
            r2 = r11[r10]
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r10]
            r2.postNotificationName(r3, r4)
            r2 = r61
        L_0x196a:
            r4 = -1
            r9 = 0
            goto L_0x194c
        L_0x196d:
            r11 = r66
            int r0 = (r2 > r61 ? 1 : (r2 == r61 ? 0 : -1))
            if (r0 == 0) goto L_0x1983
            r0 = r11[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r10]
            r0.postNotificationName(r1, r4)
            r0 = r61
            goto L_0x196a
        L_0x1983:
            if (r4 == 0) goto L_0x1999
            r0 = r11[r10]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r0.postNotificationName(r1, r2)
            r12 = r4
            r0 = r61
            r2 = r0
            r4 = -1
            r9 = 0
            goto L_0x194d
        L_0x1999:
            r0 = r61
            r2 = r0
            r4 = -1
            r9 = 0
            r12 = 0
            r13 = 1
        L_0x19a0:
            r10 = r6
            r8 = r9
            r69 = r12
            r16 = r13
            r19 = r61
            r73 = 0
            r6 = 0
            r9 = 0
            r15 = 0
            r17 = 0
            r18 = 0
            r21 = 0
            r23 = 0
            r29 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r67 = 0
            r12 = r0
            r0 = r60
            r1 = 0
            goto L_0x1a5d
        L_0x19c7:
            r8 = r71
            r11 = r66
            r5 = r68
            r10 = 0
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x19ed
            r2 = r61
            r12 = r2
            r19 = r12
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            r9 = 0
            r15 = 0
            r16 = 0
            r17 = 1
            goto L_0x1a49
        L_0x19ed:
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x1a0e
            r2 = r61
            r12 = r2
            r19 = r12
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            r9 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            goto L_0x1a4b
        L_0x1a0e:
            java.lang.String r0 = "voip_chat"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x1a37
            r2 = r61
            r12 = r2
            r19 = r12
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            r9 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r21 = 0
            r23 = 1
            goto L_0x1a4f
        L_0x1a2f:
            r61 = r5
            r5 = r8
            r8 = r14
            r7 = r15
            r10 = 0
            r60 = -1
        L_0x1a37:
            r2 = r61
            r12 = r2
            r19 = r12
            r73 = 0
            r0 = -1
            r1 = 0
            r4 = -1
            r6 = 0
            r8 = 0
            r9 = 0
        L_0x1a44:
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x1a49:
            r18 = 0
        L_0x1a4b:
            r21 = 0
        L_0x1a4d:
            r23 = 0
        L_0x1a4f:
            r29 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r67 = 0
        L_0x1a5b:
            r69 = 0
        L_0x1a5d:
            int r14 = r7.currentAccount
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
            boolean r14 = r14.isClientActivated()
            if (r14 == 0) goto L_0x1var_
            if (r9 == 0) goto L_0x1a95
            org.telegram.ui.ActionBar.ActionBarLayout r14 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r14 = r14.getLastFragment()
            r74 = r6
            boolean r6 = r14 instanceof org.telegram.ui.DialogsActivity
            if (r6 == 0) goto L_0x1a91
            org.telegram.ui.DialogsActivity r14 = (org.telegram.ui.DialogsActivity) r14
            boolean r6 = r14.isMainDialogList()
            if (r6 == 0) goto L_0x1a8f
            android.view.View r6 = r14.getFragmentView()
            if (r6 == 0) goto L_0x1a8a
            r6 = 1
            r14.search(r9, r6)
            goto L_0x1a98
        L_0x1a8a:
            r6 = 1
            r14.setInitialSearchString(r9)
            goto L_0x1a98
        L_0x1a8f:
            r6 = 1
            goto L_0x1a98
        L_0x1a91:
            r6 = 1
            r16 = 1
            goto L_0x1a98
        L_0x1a95:
            r74 = r6
            goto L_0x1a8f
        L_0x1a98:
            int r14 = (r2 > r61 ? 1 : (r2 == r61 ? 0 : -1))
            if (r14 == 0) goto L_0x1b13
            if (r67 != 0) goto L_0x1aec
            if (r15 == 0) goto L_0x1aa1
            goto L_0x1aec
        L_0x1aa1:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putLong(r1, r2)
            if (r10 == 0) goto L_0x1ab0
            r0.putInt(r5, r10)
        L_0x1ab0:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1ad2
            r1 = 0
            r2 = r11[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r6
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1b89
        L_0x1ad2:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1b89
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1b87
        L_0x1aec:
            if (r32 == 0) goto L_0x1b07
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            if (r0 == 0) goto L_0x1var_
            org.telegram.messenger.MessagesController r1 = r0.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            org.telegram.ui.Components.AlertsCreator.createCallDialogAlert(r0, r1, r15)
            goto L_0x1var_
        L_0x1b07:
            r1 = 0
            r0 = r11[r1]
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.voip.VoIPPendingCall.startOrSchedule(r7, r2, r15, r0)
            goto L_0x1var_
        L_0x1b13:
            int r2 = (r12 > r61 ? 1 : (r12 == r61 ? 0 : -1))
            if (r2 == 0) goto L_0x1b61
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "chat_id"
            r0.putLong(r1, r12)
            if (r10 == 0) goto L_0x1b26
            r0.putInt(r5, r10)
        L_0x1b26:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1b48
            r1 = 0
            r2 = r11[r1]
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = mainFragmentsStack
            int r3 = r2.size()
            int r3 = r3 - r6
            java.lang.Object r2 = r2.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r1 = r1.checkCanOpenChat(r0, r2)
            if (r1 == 0) goto L_0x1b89
        L_0x1b48:
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1b89
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
            goto L_0x1b87
        L_0x1b61:
            r10 = r69
            if (r10 == 0) goto L_0x1b90
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enc_id"
            r0.putInt(r1, r10)
            org.telegram.ui.ChatActivity r13 = new org.telegram.ui.ChatActivity
            r13.<init>(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            boolean r0 = r12.presentFragment(r13, r14, r15, r16, r17)
            if (r0 == 0) goto L_0x1b89
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.closeDrawer()
        L_0x1b87:
            r13 = 1
            goto L_0x1b8a
        L_0x1b89:
            r13 = 0
        L_0x1b8a:
            r2 = r72
            r8 = 1
            r10 = 0
            goto L_0x1var_
        L_0x1b90:
            if (r16 == 0) goto L_0x1bce
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1b9e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.removeAllFragments()
            goto L_0x1bc9
        L_0x1b9e:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1bc9
        L_0x1ba8:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            int r0 = r0 - r6
            if (r0 <= 0) goto L_0x1bc2
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsStack
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r0.removeFragmentFromStack((org.telegram.ui.ActionBar.BaseFragment) r1)
            goto L_0x1ba8
        L_0x1bc2:
            r2 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.closeLastFragment(r2)
            goto L_0x1bca
        L_0x1bc9:
            r2 = 0
        L_0x1bca:
            r8 = 1
            r10 = 0
            goto L_0x1var_
        L_0x1bce:
            r2 = 0
            if (r17 == 0) goto L_0x1bf5
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1bef
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.AudioPlayerAlert r1 = new org.telegram.ui.Components.AudioPlayerAlert
            r10 = 0
            r1.<init>(r7, r10)
            r0.showDialog(r1)
            goto L_0x1bf0
        L_0x1bef:
            r10 = 0
        L_0x1bf0:
            r2 = r72
            r8 = 1
            goto L_0x1var_
        L_0x1bf5:
            r10 = 0
            if (r18 == 0) goto L_0x1c1b
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x1bf0
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.Components.SharingLocationsAlert r1 = new org.telegram.ui.Components.SharingLocationsAlert
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda80
            r2.<init>(r7, r11)
            r1.<init>(r7, r2, r10)
            r0.showDialog(r1)
            goto L_0x1bf0
        L_0x1c1b:
            android.net.Uri r2 = r7.exportingChatUri
            if (r2 == 0) goto L_0x1CLASSNAME
            java.util.ArrayList<android.net.Uri> r0 = r7.documentsUrisArray
            r7.runImportRequest(r2, r0)
            r8 = 1
            goto L_0x1var_
        L_0x1CLASSNAME:
            java.util.ArrayList<android.os.Parcelable> r2 = r7.importingStickers
            if (r2 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda25
            r0.<init>(r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x1bf0
        L_0x1CLASSNAME:
            java.lang.String r2 = r7.videoPath
            if (r2 != 0) goto L_0x1ed2
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r2 = r7.photoPathsArray
            if (r2 != 0) goto L_0x1ed2
            java.lang.String r2 = r7.sendingText
            if (r2 != 0) goto L_0x1ed2
            java.util.ArrayList<java.lang.String> r2 = r7.documentsPathsArray
            if (r2 != 0) goto L_0x1ed2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.contactsToSend
            if (r2 != 0) goto L_0x1ed2
            java.util.ArrayList<android.net.Uri> r2 = r7.documentsUrisArray
            if (r2 == 0) goto L_0x1c4e
            goto L_0x1ed2
        L_0x1c4e:
            if (r8 == 0) goto L_0x1cd6
            if (r8 != r6) goto L_0x1c6d
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            java.lang.String r3 = "user_id"
            r0.putLong(r3, r1)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r13 = r1
        L_0x1c6a:
            r0 = 0
        L_0x1c6b:
            r1 = 6
            goto L_0x1ca2
        L_0x1c6d:
            r2 = 2
            if (r8 != r2) goto L_0x1CLASSNAME
            org.telegram.ui.ThemeActivity r0 = new org.telegram.ui.ThemeActivity
            r1 = 0
            r0.<init>(r1)
        L_0x1CLASSNAME:
            r13 = r0
            goto L_0x1c6a
        L_0x1CLASSNAME:
            r1 = 0
            r2 = 3
            if (r8 != r2) goto L_0x1CLASSNAME
            org.telegram.ui.SessionsActivity r0 = new org.telegram.ui.SessionsActivity
            r0.<init>(r1)
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r1 = 4
            if (r8 != r1) goto L_0x1c8b
            org.telegram.ui.FiltersSetupActivity r0 = new org.telegram.ui.FiltersSetupActivity
            r0.<init>()
            goto L_0x1CLASSNAME
        L_0x1c8b:
            r1 = 5
            if (r8 != r1) goto L_0x1CLASSNAME
            org.telegram.ui.ActionIntroActivity r0 = new org.telegram.ui.ActionIntroActivity
            r0.<init>(r2)
            r13 = r0
            r0 = 1
            goto L_0x1c6b
        L_0x1CLASSNAME:
            r1 = 6
            if (r8 != r1) goto L_0x1ca0
            org.telegram.ui.EditWidgetActivity r2 = new org.telegram.ui.EditWidgetActivity
            r2.<init>(r4, r0)
            r13 = r2
            goto L_0x1ca1
        L_0x1ca0:
            r13 = r10
        L_0x1ca1:
            r0 = 0
        L_0x1ca2:
            if (r8 != r1) goto L_0x1cb0
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            goto L_0x1cb8
        L_0x1cb0:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda50 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda50
            r1.<init>(r7, r13, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x1cb8:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1ccf
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d10
        L_0x1ccf:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r1)
            goto L_0x1d10
        L_0x1cd6:
            r2 = 2
            if (r21 == 0) goto L_0x1d15
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "destroyAfterSelect"
            r0.putBoolean(r1, r6)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            org.telegram.ui.ContactsActivity r13 = new org.telegram.ui.ContactsActivity
            r13.<init>(r0)
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d0a
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d10
        L_0x1d0a:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r1)
        L_0x1d10:
            r2 = r72
            r8 = 1
            goto L_0x1ef0
        L_0x1d15:
            if (r1 == 0) goto L_0x1d76
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r2 = "destroyAfterSelect"
            r0.putBoolean(r2, r6)
            java.lang.String r2 = "returnAsResult"
            r0.putBoolean(r2, r6)
            java.lang.String r2 = "onlyUsers"
            r0.putBoolean(r2, r6)
            java.lang.String r2 = "allowSelf"
            r3 = 0
            r0.putBoolean(r2, r3)
            org.telegram.ui.ContactsActivity r2 = new org.telegram.ui.ContactsActivity
            r2.<init>(r0)
            r2.setInitialSearchString(r1)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda81
            r0.<init>(r7, r15, r11)
            r2.setDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.getLastFragment()
            boolean r1 = r1 instanceof org.telegram.ui.ContactsActivity
            r19 = 1
            r20 = 1
            r21 = 0
            r16 = r0
            r17 = r2
            r18 = r1
            r16.presentFragment(r17, r18, r19, r20, r21)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d6f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d10
        L_0x1d6f:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r1)
            goto L_0x1d10
        L_0x1d76:
            if (r35 == 0) goto L_0x1db1
            org.telegram.ui.ActionIntroActivity r13 = new org.telegram.ui.ActionIntroActivity
            r0 = 5
            r13.<init>(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda74
            r0.<init>(r7, r13)
            r13.setQrLoginDelegate(r0)
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1da9
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d10
        L_0x1da9:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r1)
            goto L_0x1d10
        L_0x1db1:
            r1 = 0
            if (r33 == 0) goto L_0x1e05
            org.telegram.ui.NewContactActivity r13 = new org.telegram.ui.NewContactActivity
            r13.<init>()
            if (r74 == 0) goto L_0x1dcf
            java.lang.String r0 = " "
            r3 = r74
            java.lang.String[] r0 = r3.split(r0, r2)
            r2 = r0[r1]
            int r3 = r0.length
            if (r3 <= r6) goto L_0x1dcb
            r0 = r0[r6]
            goto L_0x1dcc
        L_0x1dcb:
            r0 = r10
        L_0x1dcc:
            r13.setInitialName(r2, r0)
        L_0x1dcf:
            if (r73 == 0) goto L_0x1dda
            r0 = r73
            java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0, r6)
            r13.setInitialPhoneNumber(r0, r1)
        L_0x1dda:
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r7.actionBarLayout
            r14 = 0
            r15 = 1
            r16 = 1
            r17 = 0
            r12.presentFragment(r13, r14, r15, r16, r17)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1dfd
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1d10
        L_0x1dfd:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r6, r1)
            goto L_0x1d10
        L_0x1e05:
            r0 = r73
            r3 = r74
            if (r23 == 0) goto L_0x1e24
            int r0 = r7.currentAccount
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = 0
            r1 = r70
            r8 = 1
            r6 = r0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1var_
            org.telegram.ui.GroupCallActivity.groupCallUiVisible = r8
            goto L_0x1var_
        L_0x1e24:
            r8 = 1
            if (r34 == 0) goto L_0x1ea4
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            org.telegram.ui.ActionBar.BaseFragment r1 = r1.getLastFragment()
            if (r1 == 0) goto L_0x1ea0
            android.app.Activity r2 = r1.getParentActivity()
            if (r2 == 0) goto L_0x1ea0
            int r2 = r7.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r4 = 0
            java.lang.String r0 = org.telegram.ui.NewContactActivity.getPhoneNumber(r7, r2, r0, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r1.getParentActivity()
            r2.<init>((android.content.Context) r4)
            r4 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            java.lang.String r5 = "NewContactAlertTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r2.setTitle(r4)
            r4 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.Object[] r5 = new java.lang.Object[r8]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r6 = r6.format(r0)
            r12 = 0
            r5[r12] = r6
            java.lang.String r6 = "NewContactAlertMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r2.setMessage(r4)
            r4 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r5 = "NewContactAlertButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7 r5 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda7
            r5.<init>(r0, r3, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r2.setPositiveButton(r4, r5)
            r2 = 2131624697(0x7f0e02f9, float:1.8876581E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r2, r10)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1.showDialog(r0)
            r13 = 1
            goto L_0x1ea1
        L_0x1ea0:
            r13 = 0
        L_0x1ea1:
            r2 = r72
            goto L_0x1var_
        L_0x1ea4:
            if (r29 == 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.CallLogActivity r1 = new org.telegram.ui.CallLogActivity
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 0
            r0.presentFragment(r1, r2, r3, r4, r5)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1ecb
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1eee
        L_0x1ecb:
            r1 = 0
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1eee
        L_0x1ed2:
            r1 = 0
            r8 = 1
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x1ee7
            r0 = r11[r1]
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r0.postNotificationName(r2, r3)
        L_0x1ee7:
            int r0 = (r19 > r61 ? 1 : (r19 == r61 ? 0 : -1))
            if (r0 != 0) goto L_0x1ef2
            r7.openDialogsToSend(r1)
        L_0x1eee:
            r2 = r72
        L_0x1ef0:
            r13 = 1
            goto L_0x1var_
        L_0x1ef2:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Long r2 = java.lang.Long.valueOf(r19)
            r0.add(r2)
            r7.didSelectDialogs(r10, r0, r10, r1)
            goto L_0x1var_
        L_0x1var_:
            r8 = 1
            r10 = 0
        L_0x1var_:
            r2 = r72
        L_0x1var_:
            r13 = 0
        L_0x1var_:
            if (r13 != 0) goto L_0x1fb3
            if (r2 != 0) goto L_0x1fb3
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1f5d
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1f9e
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1f9e
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1f9e
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r10)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r9 == 0) goto L_0x1var_
            r0.setInitialSearchString(r9)
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
            goto L_0x1f9e
        L_0x1f5d:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x1f9e
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x1var_
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            org.telegram.ui.LoginActivity r1 = new org.telegram.ui.LoginActivity
            r1.<init>()
            r0.addFragmentToStack(r1)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r1, r1)
            goto L_0x1f9e
        L_0x1var_:
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r10)
            org.telegram.ui.Components.RecyclerListView r1 = r7.sideMenu
            r0.setSideMenu(r1)
            if (r9 == 0) goto L_0x1var_
            r0.setInitialSearchString(r9)
        L_0x1var_:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r7.actionBarLayout
            r1.addFragmentToStack(r0)
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r7.drawerLayoutContainer
            r1 = 0
            r0.setAllowOpenDrawer(r8, r1)
        L_0x1f9e:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.actionBarLayout
            r0.showLastFragment()
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1fb3
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.layersActionBarLayout
            r0.showLastFragment()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r7.rightActionBarLayout
            r0.showLastFragment()
        L_0x1fb3:
            if (r26 == 0) goto L_0x1fbb
            r1 = 0
            r0 = r11[r1]
            org.telegram.ui.VoIPFragment.show(r7, r0)
        L_0x1fbb:
            if (r23 != 0) goto L_0x1fd0
            java.lang.String r0 = r71.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x1fd0
            org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r0 == 0) goto L_0x1fd0
            r0.dismiss()
        L_0x1fd0:
            r1 = r71
            r1.setAction(r10)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$7(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$8(Bundle bundle) {
        lambda$runLinkRequest$43(new CancelAccountDeletionActivity(bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$10(int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        iArr[0] = sharingLocationInfo.messageObject.currentAccount;
        switchToAccount(iArr[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda86(iArr, sharingLocationInfo.messageObject.getDialogId()));
        lambda$runLinkRequest$43(locationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$11() {
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.fragmentsStack.get(0).showDialog(new StickersAlert((Context) this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$12(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$13(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$17(ActionIntroActivity actionIntroActivity, String str) {
        AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new LaunchActivity$$ExternalSyntheticLambda56(alertDialog, actionIntroActivity));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$15(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!(tLObject instanceof TLRPC$TL_authorization)) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda22(actionIntroActivity, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$14(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        String string = LocaleController.getString("AuthAnotherClient", NUM);
        AlertsCreator.showSimpleAlert(actionIntroActivity, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleIntent$18(String str, String str2, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
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
            r4 = 2131627802(0x7f0e0f1a, float:1.8882879E38)
            java.lang.String r5 = "selectAlertString"
            if (r1 == 0) goto L_0x003d
            int r1 = r1.size()
            if (r1 == r2) goto L_0x0052
            java.lang.String r1 = "SendContactToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627778(0x7f0e0var_, float:1.888283E38)
            java.lang.String r4 = "SendContactToGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.putString(r3, r1)
            goto L_0x0052
        L_0x003d:
            java.lang.String r1 = "SendMessagesToText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)
            r0.putString(r5, r1)
            r1 = 2131627801(0x7f0e0var_, float:1.8882877E38)
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
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new LaunchActivity$$ExternalSyntheticLambda58(this, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runCommentRequest$20(int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda35(this, tLObject, i, num, tLRPC$Chat, tLRPC$TL_messages_getDiscussionMessage, num2, num3, alertDialog));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0094 A[SYNTHETIC, Splitter:B:15:0x0094] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runCommentRequest$19(org.telegram.tgnet.TLObject r12, int r13, java.lang.Integer r14, org.telegram.tgnet.TLRPC$Chat r15, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r16, java.lang.Integer r17, java.lang.Integer r18, org.telegram.ui.ActionBar.AlertDialog r19) {
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
            r11.lambda$runLinkRequest$43(r2)
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
            r3 = 2131624815(0x7f0e036f, float:1.887682E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$19(org.telegram.tgnet.TLObject, int, java.lang.Integer, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Integer, java.lang.Integer, org.telegram.ui.ActionBar.AlertDialog):void");
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda61
            r6.<init>(r10, r11, r12, r0)
            int r11 = r5.sendRequest(r4, r6)
            r1[r2] = r11
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0 r11 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda0
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
    public /* synthetic */ void lambda$runImportRequest$22(Uri uri, int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda36(this, tLObject, uri, i, alertDialog), 2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011b, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0137, code lost:
        if ((r10.get(r10.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x0139;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runImportRequest$21(org.telegram.tgnet.TLObject r10, android.net.Uri r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runImportRequest$21(org.telegram.tgnet.TLObject, android.net.Uri, int, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runImportRequest$23(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x030a  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x040c  */
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda75
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
            r4 = 2131626776(0x7f0e0b18, float:1.8880798E38)
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
            r1 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626821(0x7f0e0b45, float:1.888089E38)
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
            r1 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r2 = "AuthAnotherClient"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131624456(0x7f0e0208, float:1.8876092E38)
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
            if (r0 == 0) goto L_0x0139
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r12 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r12.<init>()
            r12.username = r0
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda62
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
            r28 = r0
            r0 = r12
            r12 = r32
            r13 = r31
            r14 = r50
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            r1 = r28
            int r0 = r1.sendRequest(r0, r15)
            r7 = r47
            r10 = 0
            r7[r10] = r0
            r11 = r26
            r13 = r27
            r14 = r48
        L_0x0136:
            r2 = 0
            goto L_0x0407
        L_0x0139:
            r48 = r10
            r10 = 0
            if (r5 == 0) goto L_0x0179
            if (r1 != 0) goto L_0x015d
            org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60
            r11 = r26
            r13 = r27
            r14 = r48
            r3.<init>(r11, r13, r14, r5)
            int r0 = r1.sendRequest(r0, r3, r2)
            r7[r10] = r0
            goto L_0x0136
        L_0x015d:
            r11 = r26
            r13 = r27
            r14 = r48
            if (r1 != r8) goto L_0x0136
            org.telegram.tgnet.TLRPC$TL_messages_importChatInvite r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            r0.<init>()
            r0.hash = r5
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r27)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda59 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda59
            r3.<init>(r11, r13, r14)
            r1.sendRequest(r0, r3, r2)
            goto L_0x0136
        L_0x0179:
            r11 = r26
            r13 = r27
            r14 = r48
            if (r6 == 0) goto L_0x01df
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01de
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName
            r0.<init>()
            r0.short_name = r6
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r8
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x01c8
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
            goto L_0x01db
        L_0x01c8:
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
        L_0x01db:
            r1.showDialog(r3)
        L_0x01de:
            return
        L_0x01df:
            if (r9 == 0) goto L_0x0204
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "onlySelect"
            r0.putBoolean(r1, r8)
            java.lang.String r1 = "dialogsType"
            r0.putInt(r1, r4)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda84
            r2 = r34
            r0.<init>(r11, r2, r13, r9)
            r1.setDelegate(r0)
            r11.presentFragment(r1, r10, r8)
            goto L_0x0136
        L_0x0204:
            r0 = r40
            if (r0 == 0) goto L_0x0272
            java.lang.String r1 = "bot_id"
            java.lang.Object r1 = r0.get(r1)
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            if (r1 != 0) goto L_0x021b
            return
        L_0x021b:
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda71
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
            goto L_0x0136
        L_0x0272:
            r0 = r42
            if (r0 == 0) goto L_0x0290
            org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo r1 = new org.telegram.tgnet.TLRPC$TL_help_getDeepLinkInfo
            r1.<init>()
            r1.path = r0
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda64
            r2.<init>(r11, r14)
            int r0 = r0.sendRequest(r1, r2)
            r7[r10] = r0
            goto L_0x0136
        L_0x0290:
            java.lang.String r0 = "android"
            r1 = r41
            if (r1 == 0) goto L_0x02b2
            org.telegram.tgnet.TLRPC$TL_langpack_getLanguage r2 = new org.telegram.tgnet.TLRPC$TL_langpack_getLanguage
            r2.<init>()
            r2.lang_code = r1
            r2.lang_pack = r0
            int r0 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda63
            r1.<init>(r11, r14)
            int r0 = r0.sendRequest(r2, r1)
            r7[r10] = r0
            goto L_0x0136
        L_0x02b2:
            r1 = r45
            if (r1 == 0) goto L_0x032d
            java.lang.String r0 = r1.slug
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0306
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r1.settings     // Catch:{ Exception -> 0x0300 }
            int r2 = r0.third_background_color     // Catch:{ Exception -> 0x0300 }
            if (r2 == 0) goto L_0x02de
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x0300 }
            java.lang.String r4 = "c"
            int r5 = r0.background_color     // Catch:{ Exception -> 0x0300 }
            int r6 = r0.second_background_color     // Catch:{ Exception -> 0x0300 }
            int r0 = r0.fourth_background_color     // Catch:{ Exception -> 0x0300 }
            r28 = r3
            r29 = r4
            r30 = r5
            r31 = r6
            r32 = r2
            r33 = r0
            r28.<init>(r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x0300 }
            goto L_0x02ef
        L_0x02de:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper     // Catch:{ Exception -> 0x0300 }
            java.lang.String r2 = "c"
            int r4 = r0.background_color     // Catch:{ Exception -> 0x0300 }
            int r5 = r0.second_background_color     // Catch:{ Exception -> 0x0300 }
            int r0 = r0.rotation     // Catch:{ Exception -> 0x0300 }
            int r0 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r10)     // Catch:{ Exception -> 0x0300 }
            r3.<init>(r2, r4, r5, r0)     // Catch:{ Exception -> 0x0300 }
        L_0x02ef:
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity     // Catch:{ Exception -> 0x0300 }
            r2 = 0
            r0.<init>(r3, r2, r8, r10)     // Catch:{ Exception -> 0x02fe }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda52 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda52     // Catch:{ Exception -> 0x02fe }
            r3.<init>(r11, r0)     // Catch:{ Exception -> 0x02fe }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x02fe }
            goto L_0x0308
        L_0x02fe:
            r0 = move-exception
            goto L_0x0302
        L_0x0300:
            r0 = move-exception
            r2 = 0
        L_0x0302:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0307
        L_0x0306:
            r2 = 0
        L_0x0307:
            r8 = 0
        L_0x0308:
            if (r8 != 0) goto L_0x0407
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r3.<init>()
            java.lang.String r4 = r1.slug
            r3.slug = r4
            r0.wallpaper = r3
            int r3 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda67 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda67
            r4.<init>(r11, r14, r1)
            int r0 = r3.sendRequest(r0, r4)
            r7[r10] = r0
            goto L_0x0407
        L_0x032d:
            r2 = 0
            if (r3 == 0) goto L_0x0358
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda24 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda24
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda65
            r3.<init>(r11, r14)
            int r0 = r0.sendRequest(r2, r3)
            r7[r10] = r0
            goto L_0x0408
        L_0x0358:
            if (r12 == 0) goto L_0x0407
            if (r35 == 0) goto L_0x0407
            if (r37 == 0) goto L_0x03b5
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r27)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            if (r0 == 0) goto L_0x037e
            r28 = r26
            r29 = r27
            r30 = r14
            r31 = r35
            r32 = r38
            r33 = r37
            r34 = r0
            int r0 = r28.runCommentRequest(r29, r30, r31, r32, r33, r34)
            r7[r10] = r0
            goto L_0x0407
        L_0x037e:
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda70
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
            goto L_0x0407
        L_0x03b5:
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
            if (r1 != 0) goto L_0x03e2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r8
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            goto L_0x03e3
        L_0x03e2:
            r1 = r2
        L_0x03e3:
            if (r1 == 0) goto L_0x03ef
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r27)
            boolean r3 = r3.checkCanOpenChat(r0, r1)
            if (r3 == 0) goto L_0x0407
        L_0x03ef:
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda31 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda31
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
        L_0x0407:
            r1 = r2
        L_0x0408:
            r0 = r7[r10]
            if (r0 == 0) goto L_0x0419
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda1
            r0.<init>(r13, r7, r1)
            r14.setOnCancelListener(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r14.showDelayed(r0)     // Catch:{ Exception -> 0x0419 }
        L_0x0419:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, int, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$24(int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Long l, Integer num2, Integer num3, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str12, String str13, String str14, int i2, int i3) {
        int i4 = i3;
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i3, str, str2, str3, str4, str5, str6, z, num, l, num2, num3, str7, hashMap, str8, str9, str10, str11, tLRPC$TL_wallPaper, str12, str13, str14, 1, i2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$28(String str, String str2, String str3, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str4, String str5, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity$$ExternalSyntheticLambda37 launchActivity$$ExternalSyntheticLambda37 = r0;
        LaunchActivity$$ExternalSyntheticLambda37 launchActivity$$ExternalSyntheticLambda372 = new LaunchActivity$$ExternalSyntheticLambda37(this, tLObject, tLRPC$TL_error, str, str2, str3, i, num, num2, num3, iArr, alertDialog, str4, str5, i2);
        AndroidUtilities.runOnUIThread(launchActivity$$ExternalSyntheticLambda37, 2);
    }

    /* JADX WARNING: type inference failed for: r1v11 */
    /* JADX WARNING: type inference failed for: r1v12, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r1v18, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r1v45 */
    /* JADX WARNING: type inference failed for: r1v46 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00f1, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0110, code lost:
        if ((r1.get(r1.size() - 1) instanceof org.telegram.ui.DialogsActivity) != false) goto L_0x00f3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x018c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$27(org.telegram.tgnet.TLObject r16, org.telegram.tgnet.TLRPC$TL_error r17, java.lang.String r18, java.lang.String r19, java.lang.String r20, int r21, java.lang.Integer r22, java.lang.Integer r23, java.lang.Integer r24, int[] r25, org.telegram.ui.ActionBar.AlertDialog r26, java.lang.String r27, java.lang.String r28, int r29) {
        /*
            r15 = this;
            r8 = r15
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r27
            r6 = r28
            r7 = r29
            boolean r9 = r15.isFinishing()
            if (r9 != 0) goto L_0x0346
            r9 = r16
            org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer r9 = (org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer) r9
            r10 = 1
            r11 = 0
            if (r0 != 0) goto L_0x02e8
            org.telegram.ui.ActionBar.ActionBarLayout r12 = r8.actionBarLayout
            if (r12 == 0) goto L_0x02e8
            if (r1 != 0) goto L_0x0027
            if (r2 == 0) goto L_0x0045
        L_0x0027:
            if (r1 == 0) goto L_0x0031
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            boolean r12 = r12.isEmpty()
            if (r12 == 0) goto L_0x0045
        L_0x0031:
            if (r2 == 0) goto L_0x003b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 == 0) goto L_0x0045
        L_0x003b:
            if (r3 == 0) goto L_0x02e8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x02e8
        L_0x0045:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r21)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            r0.putUsers(r12, r11)
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r21)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r9.chats
            r0.putChats(r12, r11)
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r21)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r9.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r13 = r9.chats
            r0.putUsersAndChats(r12, r13, r11, r10)
            if (r22 == 0) goto L_0x0093
            if (r23 != 0) goto L_0x0068
            if (r24 == 0) goto L_0x0093
        L_0x0068:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0093
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r9.chats
            java.lang.Object r0 = r0.get(r11)
            r7 = r0
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7
            r1 = r15
            r2 = r21
            r3 = r26
            r4 = r22
            r5 = r23
            r6 = r24
            int r0 = r1.runCommentRequest(r2, r3, r4, r5, r6, r7)
            r25[r11] = r0
            r0 = r25[r11]
            if (r0 == 0) goto L_0x02cd
            r4 = r26
        L_0x0090:
            r10 = 0
            goto L_0x033b
        L_0x0093:
            java.lang.String r0 = "dialogsType"
            java.lang.String r12 = "onlySelect"
            if (r1 == 0) goto L_0x0193
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r2.putBoolean(r12, r10)
            java.lang.String r3 = "cantSendToChannels"
            r2.putBoolean(r3, r10)
            r2.putInt(r0, r10)
            r0 = 2131627784(0x7f0e0var_, float:1.8882842E38)
            java.lang.String r3 = "SendGameToText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            java.lang.String r3 = "selectAlertString"
            r2.putString(r3, r0)
            r0 = 2131627783(0x7f0e0var_, float:1.888284E38)
            java.lang.String r3 = "SendGameToGroupText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            java.lang.String r3 = "selectAlertStringGroup"
            r2.putString(r3, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda83
            r2.<init>(r15, r1, r4, r9)
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
            r16 = r2
            r17 = r0
            r18 = r1
            r19 = r3
            r20 = r4
            r21 = r5
            r16.presentFragment(r17, r18, r19, r20, r21)
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
            goto L_0x02cd
        L_0x018c:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            r0.setAllowOpenDrawer(r10, r11)
            goto L_0x02cd
        L_0x0193:
            r1 = 0
            if (r5 == 0) goto L_0x0218
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
            r3 = 2
            r2.putInt(r0, r3)
            r0 = 2131624247(0x7f0e0137, float:1.8875668E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r1)
            r3[r11] = r6
            java.lang.String r6 = "%1$s"
            r3[r10] = r6
            java.lang.String r6 = "AddToTheGroupAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r0, r3)
            java.lang.String r3 = "addToGroupAlertString"
            r2.putString(r3, r0)
            org.telegram.ui.DialogsActivity r0 = new org.telegram.ui.DialogsActivity
            r0.<init>(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda82
            r2.<init>(r15, r4, r1, r5)
            r0.setDelegate(r2)
            r15.lambda$runLinkRequest$43(r0)
            goto L_0x02cd
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
            r2 = 2131624616(0x7f0e02a8, float:1.8876417E38)
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
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r9.chats
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0240
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r9.chats
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            long r12 = r5.id
            java.lang.String r5 = "chat_id"
            r0.putLong(r5, r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r9.chats
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            long r12 = r5.id
            long r12 = -r12
            goto L_0x0259
        L_0x0240:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r9.users
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            long r12 = r5.id
            java.lang.String r5 = "user_id"
            r0.putLong(r5, r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r9.users
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            long r12 = r5.id
        L_0x0259:
            if (r6 == 0) goto L_0x0276
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r9.users
            int r5 = r5.size()
            if (r5 <= 0) goto L_0x0276
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r9.users
            java.lang.Object r5 = r5.get(r11)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            boolean r5 = r5.bot
            if (r5 == 0) goto L_0x0276
            java.lang.String r5 = "botUser"
            r0.putString(r5, r6)
            r5 = 1
            goto L_0x0277
        L_0x0276:
            r5 = 0
        L_0x0277:
            if (r22 == 0) goto L_0x0282
            int r9 = r22.intValue()
            java.lang.String r14 = "message_id"
            r0.putInt(r14, r9)
        L_0x0282:
            if (r2 == 0) goto L_0x0289
            java.lang.String r9 = "voicechat"
            r0.putString(r9, r2)
        L_0x0289:
            if (r3 == 0) goto L_0x0290
            java.lang.String r9 = "livestream"
            r0.putString(r9, r3)
        L_0x0290:
            if (r7 < 0) goto L_0x0297
            java.lang.String r3 = "video_timestamp"
            r0.putInt(r3, r7)
        L_0x0297:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = mainFragmentsStack
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x02ae
            if (r2 != 0) goto L_0x02ae
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r2 = r1.size()
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
        L_0x02ae:
            if (r1 == 0) goto L_0x02ba
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r21)
            boolean r2 = r2.checkCanOpenChat(r0, r1)
            if (r2 == 0) goto L_0x02cd
        L_0x02ba:
            if (r5 == 0) goto L_0x02d0
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x02d0
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r2 = r1.getDialogId()
            int r5 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x02d0
            r1.setBotUser(r6)
        L_0x02cd:
            r4 = r26
            goto L_0x033b
        L_0x02d0:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r21)
            if (r22 != 0) goto L_0x02d8
            r2 = 0
            goto L_0x02dc
        L_0x02d8:
            int r2 = r22.intValue()
        L_0x02dc:
            org.telegram.ui.LaunchActivity$11 r3 = new org.telegram.ui.LaunchActivity$11
            r4 = r26
            r3.<init>(r4, r0)
            r1.ensureMessagesLoaded(r12, r2, r3)
            goto L_0x0090
        L_0x02e8:
            r4 = r26
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0337 }
            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x0337 }
            if (r1 != 0) goto L_0x033b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack     // Catch:{ Exception -> 0x0337 }
            int r2 = r1.size()     // Catch:{ Exception -> 0x0337 }
            int r2 = r2 - r10
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0337 }
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1     // Catch:{ Exception -> 0x0337 }
            if (r0 == 0) goto L_0x0322
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x0337 }
            if (r0 == 0) goto L_0x0322
            java.lang.String r2 = "FLOOD_WAIT"
            boolean r0 = r0.startsWith(r2)     // Catch:{ Exception -> 0x0337 }
            if (r0 == 0) goto L_0x0322
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x0337 }
            java.lang.String r1 = "FloodWait"
            r2 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0337 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0337 }
            r0.show()     // Catch:{ Exception -> 0x0337 }
            goto L_0x033b
        L_0x0322:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r1)     // Catch:{ Exception -> 0x0337 }
            java.lang.String r1 = "NoUsernameFound"
            r2 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0337 }
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)     // Catch:{ Exception -> 0x0337 }
            r0.show()     // Catch:{ Exception -> 0x0337 }
            goto L_0x033b
        L_0x0337:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x033b:
            if (r10 == 0) goto L_0x0346
            r26.dismiss()     // Catch:{ Exception -> 0x0341 }
            goto L_0x0346
        L_0x0341:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0346:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$27(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, java.lang.String, java.lang.String, java.lang.String, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, int[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$25(String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    public /* synthetic */ void lambda$runLinkRequest$26(int i, TLRPC$User tLRPC$User, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
    public /* synthetic */ void lambda$runLinkRequest$31(int i, AlertDialog alertDialog, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda41(this, tLRPC$TL_error, tLObject, i, alertDialog, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r8.chat.has_geo != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0079, code lost:
        if (r10.checkCanOpenChat(r7, r11.get(r11.size() - 1)) != false) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$30(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.String r14) {
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
            org.telegram.ui.LaunchActivity$12 r0 = new org.telegram.ui.LaunchActivity$12
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
            r12 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r14 = "AppName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r11.setTitle(r12)
            java.lang.String r12 = r10.text
            java.lang.String r14 = "FLOOD_WAIT"
            boolean r12 = r12.startsWith(r14)
            if (r12 == 0) goto L_0x00da
            r10 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.String r12 = "FloodWait"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0109
        L_0x00da:
            java.lang.String r10 = r10.text
            java.lang.String r12 = "INVITE_HASH_EXPIRED"
            boolean r10 = r10.startsWith(r12)
            if (r10 == 0) goto L_0x00fd
            r10 = 2131625581(0x7f0e066d, float:1.8878374E38)
            java.lang.String r12 = "ExpiredLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setTitle(r10)
            r10 = 2131626029(0x7f0e082d, float:1.8879283E38)
            java.lang.String r12 = "InviteExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
            goto L_0x0109
        L_0x00fd:
            r10 = 2131626086(0x7f0e0866, float:1.8879398E38)
            java.lang.String r12 = "JoinToGroupErrorNotExist"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r11.setMessage(r10)
        L_0x0109:
            r10 = 2131626776(0x7f0e0b18, float:1.8880798E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$30(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$runLinkRequest$29(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$33(int i, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda49(this, alertDialog, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$32(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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
    public /* synthetic */ void lambda$runLinkRequest$34(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
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
    public /* synthetic */ void lambda$runLinkRequest$38(int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new LaunchActivity$$ExternalSyntheticLambda66(this, alertDialog, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda48(this, alertDialog, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$36(AlertDialog alertDialog, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda44(this, alertDialog, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$35(AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm2 = tLRPC$TL_account_getAuthorizationForm;
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$43(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm2.bot_id, tLRPC$TL_account_getAuthorizationForm2.scope, tLRPC$TL_account_getAuthorizationForm2.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$TL_account_password) tLObject));
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$37(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$40(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda43(this, alertDialog, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$39(AlertDialog alertDialog, TLObject tLObject) {
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
    public /* synthetic */ void lambda$runLinkRequest$42(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda45(this, alertDialog, tLObject, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$41(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$runLinkRequest$45(AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda46(this, alertDialog, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$44(org.telegram.ui.ActionBar.AlertDialog r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC$TL_wallPaper r13, org.telegram.tgnet.TLRPC$TL_error r14) {
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
            r10.lambda$runLinkRequest$43(r11)
            goto L_0x0073
        L_0x004d:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 2131625452(0x7f0e05ec, float:1.8878112E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$44(org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$46() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$48(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda38(this, tLObject, alertDialog, tLRPC$TL_error));
    }

    /* JADX WARNING: type inference failed for: r8v11, types: [org.telegram.tgnet.TLRPC$WallPaper] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0095 A[SYNTHETIC, Splitter:B:29:0x0095] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$47(org.telegram.tgnet.TLObject r6, org.telegram.ui.ActionBar.AlertDialog r7, org.telegram.tgnet.TLRPC$TL_error r8) {
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
            r6 = 2131628167(0x7f0e1087, float:1.888362E38)
            java.lang.String r7 = "Theme"
            if (r2 != r1) goto L_0x00b9
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628189(0x7f0e109d, float:1.8883664E38)
            java.lang.String r8 = "ThemeNotSupported"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
            goto L_0x00cd
        L_0x00b9:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131628188(0x7f0e109c, float:1.8883662E38)
            java.lang.String r8 = "ThemeNotFound"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r5, r6, r7)
            r5.showAlertDialog(r6)
        L_0x00cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$47(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.AlertDialog, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$50(int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda40(this, tLObject, iArr, i, alertDialog, num, num2, num3));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037 A[SYNTHETIC, Splitter:B:7:0x0037] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runLinkRequest$49(org.telegram.tgnet.TLObject r11, int[] r12, int r13, org.telegram.ui.ActionBar.AlertDialog r14, java.lang.Integer r15, java.lang.Integer r16, java.lang.Integer r17) {
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
            r0 = 2131626180(0x7f0e08c4, float:1.8879589E38)
            java.lang.String r1 = "LinkNotFound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createSimpleAlert(r10, r0)
            r10.showAlertDialog(r0)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$49(org.telegram.tgnet.TLObject, int[], int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Integer, java.lang.Integer, java.lang.Integer):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$53(Bundle bundle, Long l, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        if (!this.actionBarLayout.presentFragment(new ChatActivity(bundle))) {
            TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = l.longValue();
            tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
            iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new LaunchActivity$$ExternalSyntheticLambda68(this, alertDialog, baseFragment, i, bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$52(AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda47(this, alertDialog, tLObject, baseFragment, i, bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runLinkRequest$51(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
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
    public static /* synthetic */ void lambda$runLinkRequest$54(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
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
            r13 = 2131627545(0x7f0e0e19, float:1.8882357E38)
            java.lang.String r15 = "RepliesTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r14[r8] = r13
            goto L_0x0107
        L_0x00f4:
            boolean r13 = r12.self
            if (r13 == 0) goto L_0x0107
            r13 = 2131627678(0x7f0e0e9e, float:1.8882627E38)
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
            AnonymousClass13 r0 = new FrameLayout(this) {
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
            this.updateLayout.setOnClickListener(new LaunchActivity$$ExternalSyntheticLambda14(this));
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
    public /* synthetic */ void lambda$createUpdateUI$55(View view) {
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
            r7 = 2131624308(0x7f0e0174, float:1.8875792E38)
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
            r10 = 2131624307(0x7f0e0173, float:1.887579E38)
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
            r7 = 2131624304(0x7f0e0170, float:1.8875784E38)
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
            org.telegram.ui.LaunchActivity$14 r0 = new org.telegram.ui.LaunchActivity$14
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
    public static /* synthetic */ void lambda$updateAppUpdateViews$56(View view) {
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new LaunchActivity$$ExternalSyntheticLambda57(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$58(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda42(this, (TLRPC$TL_help_appUpdate) tLObject, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAppUpdate$57(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
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
            this.visibleDialog.setOnDismissListener(new LaunchActivity$$ExternalSyntheticLambda12(this));
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAlertDialog$59(DialogInterface dialogInterface) {
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

    /* JADX WARNING: Removed duplicated region for block: B:135:0x0257  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x025c  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02ad A[LOOP:2: B:153:0x02a5->B:155:0x02ad, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02d3  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02e1 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didSelectDialogs(org.telegram.ui.DialogsActivity r30, java.util.ArrayList<java.lang.Long> r31, java.lang.CharSequence r32, boolean r33) {
        /*
            r29 = this;
            r8 = r29
            r3 = r30
            r0 = r31
            if (r3 == 0) goto L_0x000d
            int r1 = r30.getCurrentAccount()
            goto L_0x000f
        L_0x000d:
            int r1 = r8.currentAccount
        L_0x000f:
            r2 = r1
            android.net.Uri r6 = r8.exportingChatUri
            r1 = 0
            r9 = 0
            if (r6 == 0) goto L_0x0054
            java.util.ArrayList<android.net.Uri> r4 = r8.documentsUrisArray
            if (r4 == 0) goto L_0x0023
            java.util.ArrayList r4 = new java.util.ArrayList
            java.util.ArrayList<android.net.Uri> r5 = r8.documentsUrisArray
            r4.<init>(r5)
            r5 = r4
            goto L_0x0024
        L_0x0023:
            r5 = r9
        L_0x0024:
            org.telegram.ui.ActionBar.AlertDialog r10 = new org.telegram.ui.ActionBar.AlertDialog
            r4 = 3
            r10.<init>(r8, r4)
            org.telegram.messenger.SendMessagesHelper r11 = org.telegram.messenger.SendMessagesHelper.getInstance(r2)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Long r0 = (java.lang.Long) r0
            long r12 = r0.longValue()
            android.net.Uri r14 = r8.exportingChatUri
            java.util.ArrayList<android.net.Uri> r15 = r8.documentsUrisArray
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda55 r16 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda55
            r0 = r16
            r1 = r29
            r3 = r30
            r4 = r33
            r7 = r10
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r11.prepareImportHistory(r12, r14, r15, r16)
            r0 = 300(0x12c, double:1.48E-321)
            r10.showDelayed(r0)     // Catch:{ Exception -> 0x02ed }
            goto L_0x02ed
        L_0x0054:
            int r4 = r31.size()
            r5 = 1
            if (r4 > r5) goto L_0x00b3
            java.lang.Object r4 = r0.get(r1)
            java.lang.Long r4 = (java.lang.Long) r4
            long r6 = r4.longValue()
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            java.lang.String r10 = "scrollToTopOnResume"
            r4.putBoolean(r10, r5)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 != 0) goto L_0x0080
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r11 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r12 = new java.lang.Object[r1]
            r10.postNotificationName(r11, r12)
        L_0x0080:
            boolean r10 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r10 == 0) goto L_0x0090
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.String r7 = "enc_id"
            r4.putInt(r7, r6)
            goto L_0x00a2
        L_0x0090:
            boolean r10 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r10 == 0) goto L_0x009c
            java.lang.String r10 = "user_id"
            r4.putLong(r10, r6)
            goto L_0x00a2
        L_0x009c:
            long r6 = -r6
            java.lang.String r10 = "chat_id"
            r4.putLong(r10, r6)
        L_0x00a2:
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r6 = r6.checkCanOpenChat(r4, r3)
            if (r6 != 0) goto L_0x00ad
            return
        L_0x00ad:
            org.telegram.ui.ChatActivity r6 = new org.telegram.ui.ChatActivity
            r6.<init>(r4)
            goto L_0x00b4
        L_0x00b3:
            r6 = r9
        L_0x00b4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r8.contactsToSend
            if (r4 == 0) goto L_0x00be
            int r4 = r4.size()
            int r4 = r4 + r1
            goto L_0x00bf
        L_0x00be:
            r4 = 0
        L_0x00bf:
            java.lang.String r7 = r8.videoPath
            if (r7 == 0) goto L_0x00c5
            int r4 = r4 + 1
        L_0x00c5:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r7 = r8.photoPathsArray
            if (r7 == 0) goto L_0x00ce
            int r7 = r7.size()
            int r4 = r4 + r7
        L_0x00ce:
            java.util.ArrayList<java.lang.String> r7 = r8.documentsPathsArray
            if (r7 == 0) goto L_0x00d7
            int r7 = r7.size()
            int r4 = r4 + r7
        L_0x00d7:
            java.util.ArrayList<android.net.Uri> r7 = r8.documentsUrisArray
            if (r7 == 0) goto L_0x00e0
            int r7 = r7.size()
            int r4 = r4 + r7
        L_0x00e0:
            java.lang.String r7 = r8.videoPath
            if (r7 != 0) goto L_0x00f6
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r7 = r8.photoPathsArray
            if (r7 != 0) goto L_0x00f6
            java.util.ArrayList<java.lang.String> r7 = r8.documentsPathsArray
            if (r7 != 0) goto L_0x00f6
            java.util.ArrayList<android.net.Uri> r7 = r8.documentsUrisArray
            if (r7 != 0) goto L_0x00f6
            java.lang.String r7 = r8.sendingText
            if (r7 == 0) goto L_0x00f6
            int r4 = r4 + 1
        L_0x00f6:
            r7 = 0
        L_0x00f7:
            int r10 = r31.size()
            if (r7 >= r10) goto L_0x0118
            java.lang.Object r10 = r0.get(r7)
            java.lang.Long r10 = (java.lang.Long) r10
            long r10 = r10.longValue()
            int r12 = r8.currentAccount
            if (r4 <= r5) goto L_0x010d
            r13 = 1
            goto L_0x010e
        L_0x010d:
            r13 = 0
        L_0x010e:
            boolean r10 = org.telegram.ui.Components.AlertsCreator.checkSlowMode(r8, r12, r10, r13)
            if (r10 == 0) goto L_0x0115
            return
        L_0x0115:
            int r7 = r7 + 1
            goto L_0x00f7
        L_0x0118:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r8.contactsToSend
            if (r4 == 0) goto L_0x0161
            int r4 = r4.size()
            if (r4 != r5) goto L_0x0161
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0161
            org.telegram.ui.Components.PhonebookShareAlert r1 = new org.telegram.ui.Components.PhonebookShareAlert
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = mainFragmentsStack
            int r7 = r4.size()
            int r7 = r7 - r5
            java.lang.Object r4 = r4.get(r7)
            r11 = r4
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            r12 = 0
            r13 = 0
            android.net.Uri r14 = r8.contactsToSendUri
            r15 = 0
            r16 = 0
            r17 = 0
            r10 = r1
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda76
            r4.<init>(r8, r6, r0, r2)
            r1.setDelegate(r4)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r0.showDialog(r1)
            goto L_0x02e6
        L_0x0161:
            r7 = r9
            r4 = 0
        L_0x0163:
            int r10 = r31.size()
            if (r4 >= r10) goto L_0x02e6
            java.lang.Object r10 = r0.get(r4)
            java.lang.Long r10 = (java.lang.Long) r10
            long r25 = r10.longValue()
            int r10 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r27 = org.telegram.messenger.AccountInstance.getInstance(r10)
            r15 = 1024(0x400, float:1.435E-42)
            if (r6 == 0) goto L_0x01c3
            org.telegram.ui.ActionBar.ActionBarLayout r10 = r8.actionBarLayout
            if (r3 == 0) goto L_0x0183
            r12 = 1
            goto L_0x0184
        L_0x0183:
            r12 = 0
        L_0x0184:
            if (r3 != 0) goto L_0x0188
            r13 = 1
            goto L_0x0189
        L_0x0188:
            r13 = 0
        L_0x0189:
            r14 = 1
            r16 = 0
            r11 = r6
            r1 = 1024(0x400, float:1.435E-42)
            r15 = r16
            r10.presentFragment(r11, r12, r13, r14, r15)
            java.lang.String r10 = r8.videoPath
            if (r10 == 0) goto L_0x01a0
            java.lang.String r11 = r8.sendingText
            r6.openVideoEditor(r10, r11)
            r8.sendingText = r9
            goto L_0x01f9
        L_0x01a0:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r10 = r8.photoPathsArray
            if (r10 == 0) goto L_0x01f9
            int r10 = r10.size()
            if (r10 <= 0) goto L_0x01f9
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r10 = r8.photoPathsArray
            if (r32 == 0) goto L_0x01b8
            int r11 = r32.length()
            if (r11 != 0) goto L_0x01b5
            goto L_0x01b8
        L_0x01b5:
            r11 = r32
            goto L_0x01ba
        L_0x01b8:
            java.lang.String r11 = r8.sendingText
        L_0x01ba:
            boolean r10 = r6.openPhotosEditor(r10, r11)
            if (r10 == 0) goto L_0x01fa
            r8.sendingText = r9
            goto L_0x01fa
        L_0x01c3:
            r1 = 1024(0x400, float:1.435E-42)
            java.lang.String r10 = r8.videoPath
            if (r10 == 0) goto L_0x01f9
            java.lang.String r10 = r8.sendingText
            if (r10 == 0) goto L_0x01d7
            int r10 = r10.length()
            if (r10 > r1) goto L_0x01d7
            java.lang.String r7 = r8.sendingText
            r8.sendingText = r9
        L_0x01d7:
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            java.lang.String r10 = r8.videoPath
            r13.add(r10)
            r14 = 0
            r16 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 1
            r24 = 0
            r11 = r27
            r12 = r13
            r15 = r7
            r17 = r25
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r11, r12, r13, r14, r15, r16, r17, r19, r20, r21, r22, r23, r24)
        L_0x01f9:
            r10 = 0
        L_0x01fa:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r11 = r8.photoPathsArray
            if (r11 == 0) goto L_0x023f
            if (r10 != 0) goto L_0x023f
            java.lang.String r10 = r8.sendingText
            if (r10 == 0) goto L_0x0222
            int r10 = r10.length()
            if (r10 > r1) goto L_0x0222
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r10 = r8.photoPathsArray
            int r10 = r10.size()
            if (r10 != r5) goto L_0x0222
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r10 = r8.photoPathsArray
            r15 = 0
            java.lang.Object r10 = r10.get(r15)
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r10 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r10
            java.lang.String r11 = r8.sendingText
            r10.caption = r11
            r8.sendingText = r9
            goto L_0x0223
        L_0x0222:
            r15 = 0
        L_0x0223:
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$SendingMediaInfo> r12 = r8.photoPathsArray
            r10 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 0
            r11 = r27
            r13 = r25
            r28 = 0
            r15 = r10
            org.telegram.messenger.SendMessagesHelper.prepareSendingMedia(r11, r12, r13, r15, r16, r17, r18, r19, r20, r21, r22)
            goto L_0x0241
        L_0x023f:
            r28 = 0
        L_0x0241:
            java.util.ArrayList<java.lang.String> r10 = r8.documentsPathsArray
            if (r10 != 0) goto L_0x0249
            java.util.ArrayList<android.net.Uri> r10 = r8.documentsUrisArray
            if (r10 == 0) goto L_0x028c
        L_0x0249:
            java.lang.String r10 = r8.sendingText
            if (r10 == 0) goto L_0x026e
            int r10 = r10.length()
            if (r10 > r1) goto L_0x026e
            java.util.ArrayList<java.lang.String> r1 = r8.documentsPathsArray
            if (r1 == 0) goto L_0x025c
            int r15 = r1.size()
            goto L_0x025d
        L_0x025c:
            r15 = 0
        L_0x025d:
            java.util.ArrayList<android.net.Uri> r1 = r8.documentsUrisArray
            if (r1 == 0) goto L_0x0266
            int r1 = r1.size()
            goto L_0x0267
        L_0x0266:
            r1 = 0
        L_0x0267:
            int r15 = r15 + r1
            if (r15 != r5) goto L_0x026e
            java.lang.String r7 = r8.sendingText
            r8.sendingText = r9
        L_0x026e:
            java.util.ArrayList<java.lang.String> r12 = r8.documentsPathsArray
            java.util.ArrayList<java.lang.String> r13 = r8.documentsOriginalPathsArray
            java.util.ArrayList<android.net.Uri> r14 = r8.documentsUrisArray
            java.lang.String r1 = r8.documentsMimeType
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 1
            r24 = 0
            r11 = r27
            r15 = r7
            r16 = r1
            r17 = r25
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocuments(r11, r12, r13, r14, r15, r16, r17, r19, r20, r21, r22, r23, r24)
        L_0x028c:
            java.lang.String r12 = r8.sendingText
            if (r12 == 0) goto L_0x029a
            r15 = 1
            r16 = 0
            r11 = r27
            r13 = r25
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r11, r12, r13, r15, r16)
        L_0x029a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r8.contactsToSend
            if (r1 == 0) goto L_0x02cd
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x02cd
            r1 = 0
        L_0x02a5:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r10 = r8.contactsToSend
            int r10 = r10.size()
            if (r1 >= r10) goto L_0x02cd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r10 = r8.contactsToSend
            java.lang.Object r10 = r10.get(r1)
            r12 = r10
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC$User) r12
            org.telegram.messenger.SendMessagesHelper r11 = org.telegram.messenger.SendMessagesHelper.getInstance(r2)
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 1
            r20 = 0
            r13 = r25
            r11.sendMessage((org.telegram.tgnet.TLRPC$User) r12, (long) r13, (org.telegram.messenger.MessageObject) r15, (org.telegram.messenger.MessageObject) r16, (org.telegram.tgnet.TLRPC$ReplyMarkup) r17, (java.util.HashMap<java.lang.String, java.lang.String>) r18, (boolean) r19, (int) r20)
            int r1 = r1 + 1
            goto L_0x02a5
        L_0x02cd:
            boolean r1 = android.text.TextUtils.isEmpty(r32)
            if (r1 != 0) goto L_0x02e1
            java.lang.String r12 = r32.toString()
            r15 = 1
            r16 = 0
            r11 = r27
            r13 = r25
            org.telegram.messenger.SendMessagesHelper.prepareSendingText(r11, r12, r13, r15, r16)
        L_0x02e1:
            int r4 = r4 + 1
            r1 = 0
            goto L_0x0163
        L_0x02e6:
            if (r3 == 0) goto L_0x02ed
            if (r6 != 0) goto L_0x02ed
            r30.finishFragment()
        L_0x02ed:
            r8.photoPathsArray = r9
            r8.videoPath = r9
            r8.sendingText = r9
            r8.documentsPathsArray = r9
            r8.documentsOriginalPathsArray = r9
            r8.contactsToSend = r9
            r8.contactsToSendUri = r9
            r8.exportingChatUri = r9
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didSelectDialogs(org.telegram.ui.DialogsActivity, java.util.ArrayList, java.lang.CharSequence, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectDialogs$60(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
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
    public /* synthetic */ void lambda$didSelectDialogs$61(ChatActivity chatActivity, ArrayList arrayList, int i, TLRPC$User tLRPC$User, boolean z, int i2) {
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
    public void lambda$runLinkRequest$43(BaseFragment baseFragment) {
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
                AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda23(this), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$62() {
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
        } else if (i == 2) {
            if (z) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionDenied, new Object[0]);
            }
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
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new LaunchActivity$$ExternalSyntheticLambda8(this));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPermissionErrorAlert$63(DialogInterface dialogInterface, int i) {
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
    public static /* synthetic */ void lambda$onPause$64(int i) {
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
        Utilities.stageQueue.postRunnable(LaunchActivity$$ExternalSyntheticLambda53.INSTANCE);
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
    public static /* synthetic */ void lambda$onResume$65() {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v91, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v98, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v111, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v113, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v52, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v31, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v139, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v148, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v162, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v164, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v181, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v186, resolved type: org.telegram.tgnet.TLRPC$TL_help_termsOfService} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v2 */
    /* JADX WARNING: type inference failed for: r4v6, types: [int] */
    /* JADX WARNING: type inference failed for: r4v13 */
    /* JADX WARNING: type inference failed for: r4v25 */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x027c, code lost:
        if (((org.telegram.ui.ProfileActivity) r1.get(r1.size() - 1)).isSettings() == false) goto L_0x0280;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0542  */
    /* JADX WARNING: Removed duplicated region for block: B:358:? A[RETURN, SYNTHETIC] */
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
            goto L_0x087d
        L_0x0011:
            int r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities
            r4 = 0
            if (r0 != r3) goto L_0x0022
            r0 = r2[r4]
            if (r0 == r8) goto L_0x087d
            r16.onFinish()
            r16.finish()
            goto L_0x087d
        L_0x0022:
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateConnectionState
            if (r0 != r3) goto L_0x0051
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r18)
            int r0 = r0.getConnectionState()
            int r2 = r8.currentConnectionState
            if (r2 == r0) goto L_0x087d
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
            goto L_0x087d
        L_0x0051:
            int r3 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            if (r0 != r3) goto L_0x005c
            org.telegram.ui.Adapters.DrawerLayoutAdapter r0 = r8.drawerLayoutAdapter
            r0.notifyDataSetChanged()
            goto L_0x087d
        L_0x005c:
            int r3 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.String r6 = "Cancel"
            r7 = 5
            r9 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r10 = "AppName"
            r11 = 4
            r12 = 3
            r13 = 2131626776(0x7f0e0b18, float:1.8880798E38)
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
            r4 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.String r9 = "MoreInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3
            r9.<init>(r1)
            r3.setNegativeButton(r4, r9)
        L_0x00bf:
            int r1 = r0.intValue()
            if (r1 != r7) goto L_0x00db
            r0 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
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
            r0 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.String r2 = "NobodyLikesSpam1"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3.setMessage(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r3.setPositiveButton(r0, r1)
            goto L_0x017d
        L_0x00f7:
            int r4 = r0.intValue()
            if (r4 != r5) goto L_0x0111
            r0 = 2131626590(0x7f0e0a5e, float:1.888042E38)
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
            r0 = 2131624697(0x7f0e02f9, float:1.8876581E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r1 = 0
            r3.setPositiveButton(r0, r1)
            r0 = 2131626222(0x7f0e08ee, float:1.8879674E38)
            java.lang.String r1 = "LogOut"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda9
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
            r0 = 2131627395(0x7f0e0d83, float:1.8882053E38)
            java.lang.String r1 = "Proxy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setTitle(r0)
            r0 = 2131628351(0x7f0e113f, float:1.8883992E38)
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
            if (r0 != 0) goto L_0x087d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r3.create()
            r0.showDialog(r1)
            goto L_0x087d
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
            r3 = 2131627886(0x7f0e0f6e, float:1.888305E38)
            java.lang.String r4 = "ShareYouLocationUnableManually"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda10 r4 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda10
            r4.<init>(r8, r0, r1)
            r2.setNegativeButton(r3, r4)
            r0 = 2131627885(0x7f0e0f6d, float:1.8883047E38)
            java.lang.String r1 = "ShareYouLocationUnable"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2.setMessage(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x087d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            org.telegram.ui.ActionBar.AlertDialog r1 = r2.create()
            r0.showDialog(r1)
            goto L_0x087d
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
            if (r0 == 0) goto L_0x087d
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            goto L_0x087d
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
            goto L_0x087d
        L_0x0230:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x087d
        L_0x0236:
            boolean r0 = org.telegram.messenger.AndroidUtilities.hasFlagSecureFragment()
            if (r0 != 0) goto L_0x087d
            android.view.Window r0 = r16.getWindow()     // Catch:{ Exception -> 0x0245 }
            r0.clearFlags(r1)     // Catch:{ Exception -> 0x0245 }
            goto L_0x087d
        L_0x0245:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x087d
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
            goto L_0x087d
        L_0x0285:
            int r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack
            if (r0 != r3) goto L_0x028e
            r8.showLanguageAlert(r4)
            goto L_0x087d
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
            goto L_0x087d
        L_0x02c0:
            int r3 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport
            if (r0 != r3) goto L_0x0348
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.actionBarLayout
            if (r0 == 0) goto L_0x0347
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02d1
            goto L_0x0347
        L_0x02d1:
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
            r9 = 2131628318(0x7f0e111e, float:1.8883925E38)
            java.lang.String r10 = "UpdateContactsTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setTitle(r9)
            r9 = 2131628317(0x7f0e111d, float:1.8883923E38)
            java.lang.String r10 = "UpdateContactsMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r7.setMessage(r9)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5 r10 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda5
            r10.<init>(r1, r0, r3, r2)
            r7.setPositiveButton(r9, r10)
            r9 = 2131624697(0x7f0e02f9, float:1.8876581E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6 r9 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda6
            r9.<init>(r1, r0, r3, r2)
            r7.setNegativeButton(r6, r9)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda4
            r6.<init>(r1, r0, r3, r2)
            r7.setOnBackButtonListener(r6)
            org.telegram.ui.ActionBar.AlertDialog r0 = r7.create()
            r5.showDialog(r0)
            r0.setCanceledOnTouchOutside(r4)
            goto L_0x087d
        L_0x0347:
            return
        L_0x0348:
            int r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme
            r6 = 21
            if (r0 != r3) goto L_0x03b7
            r0 = r2[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L_0x039a
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x0382
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
        L_0x0382:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x039a
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x0399 }
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)     // Catch:{ Exception -> 0x0399 }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1 = r1 | r3
            r3 = 0
            r0.<init>(r3, r3, r1)     // Catch:{ Exception -> 0x0399 }
            r8.setTaskDescription(r0)     // Catch:{ Exception -> 0x0399 }
            goto L_0x039a
        L_0x0399:
        L_0x039a:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r8.drawerLayoutContainer
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBehindKeyboardColor(r1)
            int r0 = r2.length
            if (r0 <= r5) goto L_0x03b1
            r0 = r2[r5]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            goto L_0x03b2
        L_0x03b1:
            r0 = 1
        L_0x03b2:
            r8.checkSystemBarColors(r5, r0)
            goto L_0x087d
        L_0x03b7:
            int r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            if (r0 != r3) goto L_0x054e
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x0520
            r0 = r2[r15]
            if (r0 == 0) goto L_0x0520
            android.widget.ImageView r0 = r8.themeSwitchImageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03cc
            return
        L_0x03cc:
            r0 = r2[r15]     // Catch:{ all -> 0x0507 }
            int[] r0 = (int[]) r0     // Catch:{ all -> 0x0507 }
            r1 = r2[r11]     // Catch:{ all -> 0x0507 }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x0507 }
            boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x0507 }
            r3 = r2[r7]     // Catch:{ all -> 0x0507 }
            org.telegram.ui.Components.RLottieImageView r3 = (org.telegram.ui.Components.RLottieImageView) r3     // Catch:{ all -> 0x0507 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r6 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            int r6 = r6.getMeasuredWidth()     // Catch:{ all -> 0x0507 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            int r7 = r7.getMeasuredHeight()     // Catch:{ all -> 0x0507 }
            if (r1 != 0) goto L_0x03ed
            r3.setVisibility(r11)     // Catch:{ all -> 0x0507 }
        L_0x03ed:
            org.telegram.ui.ActionBar.DrawerLayoutContainer r9 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            int r9 = r9.getMeasuredWidth()     // Catch:{ all -> 0x0507 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r10 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            int r10 = r10.getMeasuredHeight()     // Catch:{ all -> 0x0507 }
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0507 }
            android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r9, r10, r11)     // Catch:{ all -> 0x0507 }
            android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x0507 }
            r10.<init>(r9)     // Catch:{ all -> 0x0507 }
            java.util.HashMap r11 = new java.util.HashMap     // Catch:{ all -> 0x0507 }
            r11.<init>()     // Catch:{ all -> 0x0507 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            r8.invalidateCachedViews(r11)     // Catch:{ all -> 0x0507 }
            org.telegram.ui.ActionBar.DrawerLayoutContainer r11 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            r11.draw(r10)     // Catch:{ all -> 0x0507 }
            android.widget.FrameLayout r10 = r8.frameLayout     // Catch:{ all -> 0x0507 }
            android.widget.ImageView r11 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
            r10.removeView(r11)     // Catch:{ all -> 0x0507 }
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r1 == 0) goto L_0x0432
            android.widget.FrameLayout r13 = r8.frameLayout     // Catch:{ all -> 0x0507 }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x0507 }
            r13.addView(r14, r4, r10)     // Catch:{ all -> 0x0507 }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x0507 }
            r11 = 8
            r10.setVisibility(r11)     // Catch:{ all -> 0x0507 }
            goto L_0x0463
        L_0x0432:
            android.widget.FrameLayout r13 = r8.frameLayout     // Catch:{ all -> 0x0507 }
            android.widget.ImageView r14 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)     // Catch:{ all -> 0x0507 }
            r13.addView(r14, r5, r10)     // Catch:{ all -> 0x0507 }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x0507 }
            r11 = r0[r4]     // Catch:{ all -> 0x0507 }
            r13 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x0507 }
            int r11 = r11 - r14
            float r11 = (float) r11     // Catch:{ all -> 0x0507 }
            r10.setTranslationX(r11)     // Catch:{ all -> 0x0507 }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x0507 }
            r11 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x0507 }
            int r11 = r11 - r13
            float r11 = (float) r11     // Catch:{ all -> 0x0507 }
            r10.setTranslationY(r11)     // Catch:{ all -> 0x0507 }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x0507 }
            r10.setVisibility(r4)     // Catch:{ all -> 0x0507 }
            android.view.View r10 = r8.themeSwitchSunView     // Catch:{ all -> 0x0507 }
            r10.invalidate()     // Catch:{ all -> 0x0507 }
        L_0x0463:
            android.widget.ImageView r10 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
            r10.setImageBitmap(r9)     // Catch:{ all -> 0x0507 }
            android.widget.ImageView r9 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
            r9.setVisibility(r4)     // Catch:{ all -> 0x0507 }
            org.telegram.ui.Components.RLottieDrawable r9 = r3.getAnimatedDrawable()     // Catch:{ all -> 0x0507 }
            r8.themeSwitchSunDrawable = r9     // Catch:{ all -> 0x0507 }
            r9 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r9 = r6 - r9
            r10 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r10 = r6 - r10
            int r9 = r9 * r10
            r10 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r10 = r7 - r10
            r11 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r11 = r7 - r11
            int r10 = r10 * r11
            int r9 = r9 + r10
            double r9 = (double) r9     // Catch:{ all -> 0x0507 }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x0507 }
            r11 = r0[r4]     // Catch:{ all -> 0x0507 }
            r13 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r11 = r11 * r13
            r13 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r13 = r7 - r13
            r14 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r7 = r7 - r14
            int r13 = r13 * r7
            int r11 = r11 + r13
            double r13 = (double) r11     // Catch:{ all -> 0x0507 }
            double r13 = java.lang.Math.sqrt(r13)     // Catch:{ all -> 0x0507 }
            double r9 = java.lang.Math.max(r9, r13)     // Catch:{ all -> 0x0507 }
            float r7 = (float) r9     // Catch:{ all -> 0x0507 }
            r9 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r9 = r6 - r9
            r10 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r6 = r6 - r10
            int r9 = r9 * r6
            r6 = r0[r5]     // Catch:{ all -> 0x0507 }
            r10 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r6 = r6 * r10
            int r9 = r9 + r6
            double r9 = (double) r9     // Catch:{ all -> 0x0507 }
            double r9 = java.lang.Math.sqrt(r9)     // Catch:{ all -> 0x0507 }
            r6 = r0[r4]     // Catch:{ all -> 0x0507 }
            r11 = r0[r4]     // Catch:{ all -> 0x0507 }
            int r6 = r6 * r11
            r11 = r0[r5]     // Catch:{ all -> 0x0507 }
            r13 = r0[r5]     // Catch:{ all -> 0x0507 }
            int r11 = r11 * r13
            int r6 = r6 + r11
            double r13 = (double) r6     // Catch:{ all -> 0x0507 }
            double r13 = java.lang.Math.sqrt(r13)     // Catch:{ all -> 0x0507 }
            double r9 = java.lang.Math.max(r9, r13)     // Catch:{ all -> 0x0507 }
            float r6 = (float) r9     // Catch:{ all -> 0x0507 }
            float r6 = java.lang.Math.max(r7, r6)     // Catch:{ all -> 0x0507 }
            if (r1 == 0) goto L_0x04dc
            org.telegram.ui.ActionBar.DrawerLayoutContainer r7 = r8.drawerLayoutContainer     // Catch:{ all -> 0x0507 }
            goto L_0x04de
        L_0x04dc:
            android.widget.ImageView r7 = r8.themeSwitchImageView     // Catch:{ all -> 0x0507 }
        L_0x04de:
            r9 = r0[r4]     // Catch:{ all -> 0x0507 }
            r0 = r0[r5]     // Catch:{ all -> 0x0507 }
            r10 = 0
            if (r1 == 0) goto L_0x04e7
            r11 = 0
            goto L_0x04e8
        L_0x04e7:
            r11 = r6
        L_0x04e8:
            if (r1 == 0) goto L_0x04eb
            goto L_0x04ec
        L_0x04eb:
            r6 = 0
        L_0x04ec:
            android.animation.Animator r0 = android.view.ViewAnimationUtils.createCircularReveal(r7, r9, r0, r11, r6)     // Catch:{ all -> 0x0507 }
            r6 = 400(0x190, double:1.976E-321)
            r0.setDuration(r6)     // Catch:{ all -> 0x0507 }
            android.view.animation.Interpolator r6 = org.telegram.ui.Components.Easings.easeInOutQuad     // Catch:{ all -> 0x0507 }
            r0.setInterpolator(r6)     // Catch:{ all -> 0x0507 }
            org.telegram.ui.LaunchActivity$15 r6 = new org.telegram.ui.LaunchActivity$15     // Catch:{ all -> 0x0507 }
            r6.<init>(r1, r3)     // Catch:{ all -> 0x0507 }
            r0.addListener(r6)     // Catch:{ all -> 0x0507 }
            r0.start()     // Catch:{ all -> 0x0507 }
            r0 = 1
            goto L_0x0523
        L_0x0507:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            android.widget.ImageView r0 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x051b }
            r1 = 0
            r0.setImageDrawable(r1)     // Catch:{ Exception -> 0x051b }
            android.widget.FrameLayout r0 = r8.frameLayout     // Catch:{ Exception -> 0x051b }
            android.widget.ImageView r1 = r8.themeSwitchImageView     // Catch:{ Exception -> 0x051b }
            r0.removeView(r1)     // Catch:{ Exception -> 0x051b }
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4     // Catch:{ Exception -> 0x051b }
            goto L_0x0522
        L_0x051b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0522
        L_0x0520:
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r4
        L_0x0522:
            r0 = 0
        L_0x0523:
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
            if (r4 == 0) goto L_0x087d
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.layersActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r8.rightActionBarLayout
            r4.animateThemedValues(r1, r2, r3, r0)
            goto L_0x087d
        L_0x054e:
            int r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated
            if (r0 != r3) goto L_0x057f
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            if (r0 == 0) goto L_0x087d
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r0 = r0.getChildCount()
        L_0x055e:
            if (r4 >= r0) goto L_0x087d
            org.telegram.ui.Components.RecyclerListView r2 = r8.sideMenu
            android.view.View r2 = r2.getChildAt(r4)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.DrawerUserCell
            if (r3 == 0) goto L_0x057c
            r3 = r2
            org.telegram.ui.Cells.DrawerUserCell r3 = (org.telegram.ui.Cells.DrawerUserCell) r3
            int r3 = r3.getAccountNumber()
            int r5 = r1.intValue()
            if (r3 != r5) goto L_0x057c
            r2.invalidate()
            goto L_0x087d
        L_0x057c:
            int r4 = r4 + 1
            goto L_0x055e
        L_0x057f:
            int r3 = org.telegram.messenger.NotificationCenter.needShowPlayServicesAlert
            if (r0 != r3) goto L_0x058e
            r0 = r2[r4]     // Catch:{ all -> 0x087d }
            com.google.android.gms.common.api.Status r0 = (com.google.android.gms.common.api.Status) r0     // Catch:{ all -> 0x087d }
            r1 = 140(0x8c, float:1.96E-43)
            r0.startResolutionForResult(r8, r1)     // Catch:{ all -> 0x087d }
            goto L_0x087d
        L_0x058e:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoaded
            if (r0 != r3) goto L_0x0664
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x05ad
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x05ad
            r8.updateAppUpdateViews(r5)
        L_0x05ad:
            java.lang.String r1 = r8.loadingThemeFileName
            if (r1 == 0) goto L_0x0633
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x087d
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
            if (r1 == 0) goto L_0x062e
            java.lang.String r2 = r1.pathToWallpaper
            if (r2 == 0) goto L_0x0617
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r1.pathToWallpaper
            r2.<init>(r3)
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x0617
            org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r2.<init>()
            java.lang.String r3 = r1.slug
            r2.slug = r3
            r0.wallpaper = r2
            int r2 = r1.account
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda69 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda69
            r3.<init>(r8, r1)
            r2.sendRequest(r0, r3)
            return
        L_0x0617:
            org.telegram.tgnet.TLRPC$TL_theme r1 = r8.loadingTheme
            java.lang.String r2 = r1.title
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r2, r1, r5)
            if (r10 == 0) goto L_0x062e
            org.telegram.ui.ThemePreviewActivity r0 = new org.telegram.ui.ThemePreviewActivity
            r11 = 1
            r12 = 0
            r13 = 0
            r14 = 0
            r9 = r0
            r9.<init>(r10, r11, r12, r13, r14)
            r8.lambda$runLinkRequest$43(r0)
        L_0x062e:
            r16.onThemeLoadFinish()
            goto L_0x087d
        L_0x0633:
            java.lang.String r1 = r8.loadingThemeWallpaperName
            if (r1 == 0) goto L_0x087d
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x087d
            r1 = 0
            r8.loadingThemeWallpaperName = r1
            r0 = r2[r5]
            java.io.File r0 = (java.io.File) r0
            boolean r1 = r8.loadingThemeAccent
            if (r1 == 0) goto L_0x0656
            org.telegram.tgnet.TLRPC$TL_theme r0 = r8.loadingTheme
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r8.loadingThemeWallpaper
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r8.loadingThemeInfo
            r8.openThemeAccentPreview(r0, r1, r2)
            r16.onThemeLoadFinish()
            goto L_0x087d
        L_0x0656:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r8.loadingThemeInfo
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda51 r3 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda51
            r3.<init>(r8, r1, r0)
            r2.postRunnable(r3)
            goto L_0x087d
        L_0x0664:
            int r3 = org.telegram.messenger.NotificationCenter.fileLoadFailed
            if (r0 != r3) goto L_0x0698
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r8.loadingThemeFileName
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x067c
            java.lang.String r1 = r8.loadingThemeWallpaperName
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x067f
        L_0x067c:
            r16.onThemeLoadFinish()
        L_0x067f:
            boolean r1 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r1 == 0) goto L_0x087d
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x087d
            r8.updateAppUpdateViews(r5)
            goto L_0x087d
        L_0x0698:
            int r3 = org.telegram.messenger.NotificationCenter.screenStateChanged
            if (r0 != r3) goto L_0x06af
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 == 0) goto L_0x06a1
            return
        L_0x06a1:
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x06aa
            r16.onPasscodeResume()
            goto L_0x087d
        L_0x06aa:
            r16.onPasscodePause()
            goto L_0x087d
        L_0x06af:
            int r3 = org.telegram.messenger.NotificationCenter.needCheckSystemBarColors
            if (r0 != r3) goto L_0x06b8
            r16.checkSystemBarColors()
            goto L_0x087d
        L_0x06b8:
            int r3 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            if (r0 != r3) goto L_0x06e5
            int r0 = r2.length
            if (r0 <= r5) goto L_0x087d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x087d
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
            goto L_0x087d
        L_0x06e5:
            int r3 = org.telegram.messenger.NotificationCenter.stickersImportComplete
            if (r0 != r3) goto L_0x0714
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r18)
            r0 = r2[r4]
            r3 = r0
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r4 = 2
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x070a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r2 = r0.size()
            int r2 = r2 - r5
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            r5 = r0
            goto L_0x070b
        L_0x070a:
            r5 = 0
        L_0x070b:
            r6 = 0
            r7 = 1
            r2 = r16
            r1.toggleStickerSet(r2, r3, r4, r5, r6, r7)
            goto L_0x087d
        L_0x0714:
            int r1 = org.telegram.messenger.NotificationCenter.newSuggestionsAvailable
            if (r0 != r1) goto L_0x071f
            org.telegram.ui.Components.RecyclerListView r0 = r8.sideMenu
            r0.invalidateViews()
            goto L_0x087d
        L_0x071f:
            int r1 = org.telegram.messenger.NotificationCenter.showBulletin
            if (r0 != r1) goto L_0x080f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x087d
            r0 = r2[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            boolean r1 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r1 == 0) goto L_0x0741
            org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.groupCallInstance
            if (r1 == 0) goto L_0x0741
            android.widget.FrameLayout r1 = r1.getContainer()
            r7 = r1
            goto L_0x0742
        L_0x0741:
            r7 = 0
        L_0x0742:
            if (r7 != 0) goto L_0x0753
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = mainFragmentsStack
            int r3 = r1.size()
            int r3 = r3 - r5
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            r9 = r1
            goto L_0x0754
        L_0x0753:
            r9 = 0
        L_0x0754:
            r3 = 0
            if (r0 != r12) goto L_0x0788
            r0 = r2[r5]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x076a
            r0 = 2131628826(0x7f0e131a, float:1.8884956E38)
            java.lang.String r1 = "YourNameChanged"
            goto L_0x076f
        L_0x076a:
            r0 = 2131624834(0x7f0e0382, float:1.8876859E38)
            java.lang.String r1 = "CannelTitleChanged"
        L_0x076f:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x077b
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x077f
        L_0x077b:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x077f:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x087d
        L_0x0788:
            if (r0 != r15) goto L_0x07ba
            r0 = r2[r5]
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x079c
            r0 = 2131628809(0x7f0e1309, float:1.8884921E38)
            java.lang.String r1 = "YourBioChanged"
            goto L_0x07a1
        L_0x079c:
            r0 = 2131624773(0x7f0e0345, float:1.8876735E38)
            java.lang.String r1 = "CannelDescriptionChanged"
        L_0x07a1:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r7 == 0) goto L_0x07ad
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            goto L_0x07b1
        L_0x07ad:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r9)
        L_0x07b1:
            org.telegram.ui.Components.Bulletin r0 = r1.createErrorBulletin(r0)
            r0.show()
            goto L_0x087d
        L_0x07ba:
            if (r0 != 0) goto L_0x07e9
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
            if (r9 == 0) goto L_0x07e0
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r1)
            r0.show()
            goto L_0x087d
        L_0x07e0:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r7, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r1)
            r0.show()
            goto L_0x087d
        L_0x07e9:
            if (r0 != r5) goto L_0x087d
            if (r9 == 0) goto L_0x07fe
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r9)
            r1 = r2[r5]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x087d
        L_0x07fe:
            r1 = 0
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r7, r1)
            r1 = r2[r5]
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x087d
        L_0x080f:
            int r1 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            if (r0 != r1) goto L_0x0817
            r8.checkWasMutedByAdmin(r4)
            goto L_0x087d
        L_0x0817:
            int r1 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r0 != r1) goto L_0x086d
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.updateTextView
            if (r0 == 0) goto L_0x087d
            boolean r0 = org.telegram.messenger.SharedConfig.isAppUpdateAvailable()
            if (r0 == 0) goto L_0x087d
            r0 = r2[r4]
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = org.telegram.messenger.SharedConfig.pendingAppUpdate
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r1)
            if (r1 == 0) goto L_0x087d
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x087d
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
            r2 = 2131624307(0x7f0e0173, float:1.887579E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r5
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3[r4] = r0
            java.lang.String r0 = "AppUpdateDownloading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            r1.setText(r0)
            goto L_0x087d
        L_0x086d:
            int r1 = org.telegram.messenger.NotificationCenter.appUpdateAvailable
            if (r0 != r1) goto L_0x087d
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = mainFragmentsStack
            int r0 = r0.size()
            if (r0 != r5) goto L_0x087a
            r4 = 1
        L_0x087a:
            r8.updateAppUpdateViews(r4)
        L_0x087d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$66(int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(i);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$67(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$69(HashMap hashMap, int i, DialogInterface dialogInterface, int i2) {
        if (!mainFragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (AndroidUtilities.isGoogleMapsInstalled(arrayList.get(arrayList.size() - 1))) {
                LocationActivity locationActivity = new LocationActivity(0);
                locationActivity.setDelegate(new LaunchActivity$$ExternalSyntheticLambda85(hashMap, i));
                lambda$runLinkRequest$43(locationActivity);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$didReceivedNotification$68(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        for (Map.Entry value : hashMap.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            SendMessagesHelper.getInstance(i).sendMessage(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$74(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda39(this, tLObject, themeInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$73(TLObject tLObject, Theme.ThemeInfo themeInfo) {
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
    public /* synthetic */ void lambda$didReceivedNotification$76(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda26(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$75() {
        if (this.loadingTheme != null) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            File file = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
            TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
            Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
            if (applyThemeFile != null) {
                lambda$runLinkRequest$43(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
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
        lambda$runLinkRequest$43(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
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
    public /* synthetic */ void lambda$checkFreeDiscSpace$78() {
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
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda28(this));
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFreeDiscSpace$77() {
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
            r9 = 2131624983(0x7f0e0417, float:1.8877161E38)
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
            r14 = 2131625438(0x7f0e05de, float:1.8878084E38)
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
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15 r13 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda15     // Catch:{ Exception -> 0x0115 }
            r13.<init>(r10, r9)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r13)     // Catch:{ Exception -> 0x0115 }
            int r4 = r4 + 1
            r3 = 0
            goto L_0x006a
        L_0x00bf:
            org.telegram.ui.Cells.LanguageCell r3 = new org.telegram.ui.Cells.LanguageCell     // Catch:{ Exception -> 0x0115 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0115 }
            r5 = 2131624984(0x7f0e0418, float:1.8877163E38)
            java.lang.String r4 = r1.getStringForLanguageAlert(r4, r0, r5)     // Catch:{ Exception -> 0x0115 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = r1.getStringForLanguageAlert(r6, r0, r5)     // Catch:{ Exception -> 0x0115 }
            r3.setValue(r4, r0)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13 r0 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda13     // Catch:{ Exception -> 0x0115 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0115 }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x0115 }
            r0 = 50
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r0)     // Catch:{ Exception -> 0x0115 }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x0115 }
            r7.setView(r2)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r0 = "OK"
            r2 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x0115 }
            org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda11     // Catch:{ Exception -> 0x0115 }
            r2.<init>(r1, r10)     // Catch:{ Exception -> 0x0115 }
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showLanguageAlertInternal$79(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue());
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$80(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$43(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlertInternal$81(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
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
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new LaunchActivity$$ExternalSyntheticLambda72(this, localeInfoArr, str2), 8);
                            TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                            tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                            tLRPC$TL_langpack_getStrings2.keys.add("English");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                            tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new LaunchActivity$$ExternalSyntheticLambda73(this, localeInfoArr, str2), 8);
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
    public /* synthetic */ void lambda$showLanguageAlert$83(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda33(this, hashMap, localeInfoArr, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$82(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings != null && hashMap != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLanguageAlert$85(LocaleController.LocaleInfo[] localeInfoArr, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$showLanguageAlert$84(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
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
            AnonymousClass16 r0 = new Runnable() {
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
    public /* synthetic */ void lambda$updateCurrentConnectionState$86() {
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
            r2.lambda$runLinkRequest$43(r0)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$86():void");
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
