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
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.InputWallPaper;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_deepLinkInfo;
import org.telegram.tgnet.TLRPC.TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC.TL_help_getDeepLinkInfo;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getLanguage;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.TermsOfServiceView.TermsOfServiceViewDelegate;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UpdateAppAlertDialog;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;

public class LaunchActivity extends Activity implements NotificationCenterDelegate, ActionBarLayoutDelegate, DialogsActivityDelegate {
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
    private AlertDialog visibleDialog;

    /* JADX WARNING: Removed duplicated region for block: B:125:0x0781 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07a3 A:{SYNTHETIC, Splitter: B:131:0x07a3} */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x07be A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x07e0 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x07fc A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x081e A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0781 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07a3 A:{SYNTHETIC, Splitter: B:131:0x07a3} */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x07be A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x07e0 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x07fc A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x081e A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0781 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x07a3 A:{SYNTHETIC, Splitter: B:131:0x07a3} */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x07be A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x07e0 A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x07fc A:{Catch:{ Exception -> 0x079d }} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x081e A:{Catch:{ Exception -> 0x079d }} */
    protected void onCreate(android.os.Bundle r41) {
        /*
        r40 = this;
        org.telegram.messenger.ApplicationLoader.postInitApplication();
        r35 = r40.getResources();
        r35 = r35.getConfiguration();
        r0 = r40;
        r1 = r35;
        org.telegram.messenger.AndroidUtilities.checkDisplaySize(r0, r1);
        r35 = org.telegram.messenger.UserConfig.selectedAccount;
        r0 = r35;
        r1 = r40;
        r1.currentAccount = r0;
        r0 = r40;
        r0 = r0.currentAccount;
        r35 = r0;
        r35 = org.telegram.messenger.UserConfig.getInstance(r35);
        r35 = r35.isClientActivated();
        if (r35 != 0) goto L_0x013b;
    L_0x002a:
        r19 = r40.getIntent();
        r21 = 0;
        if (r19 == 0) goto L_0x00aa;
    L_0x0032:
        r35 = r19.getAction();
        if (r35 == 0) goto L_0x00aa;
    L_0x0038:
        r35 = "android.intent.action.SEND";
        r36 = r19.getAction();
        r35 = r35.equals(r36);
        if (r35 != 0) goto L_0x0052;
    L_0x0045:
        r35 = "android.intent.action.SEND_MULTIPLE";
        r36 = r19.getAction();
        r35 = r35.equals(r36);
        if (r35 == 0) goto L_0x0059;
    L_0x0052:
        super.onCreate(r41);
        r40.finish();
    L_0x0058:
        return;
    L_0x0059:
        r35 = "android.intent.action.VIEW";
        r36 = r19.getAction();
        r35 = r35.equals(r36);
        if (r35 == 0) goto L_0x00aa;
    L_0x0066:
        r32 = r19.getData();
        if (r32 == 0) goto L_0x00aa;
    L_0x006c:
        r35 = r32.toString();
        r33 = r35.toLowerCase();
        r35 = "tg:proxy";
        r0 = r33;
        r1 = r35;
        r35 = r0.startsWith(r1);
        if (r35 != 0) goto L_0x00a8;
    L_0x0081:
        r35 = "tg://proxy";
        r0 = r33;
        r1 = r35;
        r35 = r0.startsWith(r1);
        if (r35 != 0) goto L_0x00a8;
    L_0x008e:
        r35 = "tg:socks";
        r0 = r33;
        r1 = r35;
        r35 = r0.startsWith(r1);
        if (r35 != 0) goto L_0x00a8;
    L_0x009b:
        r35 = "tg://socks";
        r0 = r33;
        r1 = r35;
        r35 = r0.startsWith(r1);
        if (r35 == 0) goto L_0x0137;
    L_0x00a8:
        r21 = 1;
    L_0x00aa:
        r26 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r35 = "intro_crashed_time";
        r36 = 0;
        r0 = r26;
        r1 = r35;
        r2 = r36;
        r10 = r0.getLong(r1, r2);
        r35 = "fromIntro";
        r36 = 0;
        r0 = r19;
        r1 = r35;
        r2 = r36;
        r17 = r0.getBooleanExtra(r1, r2);
        if (r17 == 0) goto L_0x00e4;
    L_0x00ce:
        r35 = r26.edit();
        r36 = "intro_crashed_time";
        r38 = 0;
        r0 = r35;
        r1 = r36;
        r2 = r38;
        r35 = r0.putLong(r1, r2);
        r35.commit();
    L_0x00e4:
        if (r21 != 0) goto L_0x013b;
    L_0x00e6:
        r36 = java.lang.System.currentTimeMillis();
        r36 = r10 - r36;
        r36 = java.lang.Math.abs(r36);
        r38 = 120000; // 0x1d4c0 float:1.68156E-40 double:5.9288E-319;
        r35 = (r36 > r38 ? 1 : (r36 == r38 ? 0 : -1));
        if (r35 < 0) goto L_0x013b;
    L_0x00f7:
        if (r19 == 0) goto L_0x013b;
    L_0x00f9:
        if (r17 != 0) goto L_0x013b;
    L_0x00fb:
        r35 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r36 = "logininfo2";
        r37 = 0;
        r26 = r35.getSharedPreferences(r36, r37);
        r31 = r26.getAll();
        r35 = r31.isEmpty();
        if (r35 == 0) goto L_0x013b;
    L_0x0110:
        r20 = new android.content.Intent;
        r35 = org.telegram.ui.IntroActivity.class;
        r0 = r20;
        r1 = r40;
        r2 = r35;
        r0.<init>(r1, r2);
        r35 = r19.getData();
        r0 = r20;
        r1 = r35;
        r0.setData(r1);
        r0 = r40;
        r1 = r20;
        r0.startActivity(r1);
        super.onCreate(r41);
        r40.finish();
        goto L_0x0058;
    L_0x0137:
        r21 = 0;
        goto L_0x00aa;
    L_0x013b:
        r35 = 1;
        r0 = r40;
        r1 = r35;
        r0.requestWindowFeature(r1);
        r35 = NUM; // 0x7f0d000d float:1.8742141E38 double:1.053129784E-314;
        r0 = r40;
        r1 = r35;
        r0.setTheme(r1);
        r35 = android.os.Build.VERSION.SDK_INT;
        r36 = 21;
        r0 = r35;
        r1 = r36;
        if (r0 < r1) goto L_0x017c;
    L_0x0158:
        r35 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x0928 }
        r36 = 0;
        r37 = 0;
        r38 = "actionBarDefault";
        r38 = org.telegram.ui.ActionBar.Theme.getColor(r38);	 Catch:{ Exception -> 0x0928 }
        r39 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r38 = r38 | r39;
        r35.<init>(r36, r37, r38);	 Catch:{ Exception -> 0x0928 }
        r0 = r40;
        r1 = r35;
        r0.setTaskDescription(r1);	 Catch:{ Exception -> 0x0928 }
    L_0x0173:
        r35 = r40.getWindow();	 Catch:{ Exception -> 0x0925 }
        r36 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r35.setNavigationBarColor(r36);	 Catch:{ Exception -> 0x0925 }
    L_0x017c:
        r35 = r40.getWindow();
        r36 = NUM; // 0x7var_ float:1.7945693E38 double:1.052935775E-314;
        r35.setBackgroundDrawableResource(r36);
        r35 = org.telegram.messenger.SharedConfig.passcodeHash;
        r35 = r35.length();
        if (r35 <= 0) goto L_0x019d;
    L_0x018e:
        r35 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r35 != 0) goto L_0x019d;
    L_0x0192:
        r35 = r40.getWindow();	 Catch:{ Exception -> 0x06a1 }
        r36 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r37 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r35.setFlags(r36, r37);	 Catch:{ Exception -> 0x06a1 }
    L_0x019d:
        super.onCreate(r41);
        r35 = android.os.Build.VERSION.SDK_INT;
        r36 = 24;
        r0 = r35;
        r1 = r36;
        if (r0 < r1) goto L_0x01b0;
    L_0x01aa:
        r35 = r40.isInMultiWindowMode();
        org.telegram.messenger.AndroidUtilities.isInMultiwindow = r35;
    L_0x01b0:
        r35 = 0;
        r0 = r40;
        r1 = r35;
        org.telegram.ui.ActionBar.Theme.createChatResources(r0, r1);
        r35 = org.telegram.messenger.SharedConfig.passcodeHash;
        r35 = r35.length();
        if (r35 == 0) goto L_0x01d5;
    L_0x01c1:
        r35 = org.telegram.messenger.SharedConfig.appLocked;
        if (r35 == 0) goto L_0x01d5;
    L_0x01c5:
        r0 = r40;
        r0 = r0.currentAccount;
        r35 = r0;
        r35 = org.telegram.tgnet.ConnectionsManager.getInstance(r35);
        r35 = r35.getCurrentTime();
        org.telegram.messenger.SharedConfig.lastPauseTime = r35;
    L_0x01d5:
        r35 = r40.getResources();
        r36 = "status_bar_height";
        r37 = "dimen";
        r38 = "android";
        r28 = r35.getIdentifier(r36, r37, r38);
        if (r28 <= 0) goto L_0x01f6;
    L_0x01e8:
        r35 = r40.getResources();
        r0 = r35;
        r1 = r28;
        r35 = r0.getDimensionPixelSize(r1);
        org.telegram.messenger.AndroidUtilities.statusBarHeight = r35;
    L_0x01f6:
        r35 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.actionBarLayout = r0;
        r35 = new org.telegram.ui.ActionBar.DrawerLayoutContainer;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.drawerLayoutContainer = r0;
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r36 = new android.view.ViewGroup$LayoutParams;
        r37 = -1;
        r38 = -1;
        r36.<init>(r37, r38);
        r0 = r40;
        r1 = r35;
        r2 = r36;
        r0.setContentView(r1, r2);
        r35 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r35 == 0) goto L_0x06af;
    L_0x0232:
        r35 = r40.getWindow();
        r36 = 16;
        r35.setSoftInputMode(r36);
        r22 = new org.telegram.ui.LaunchActivity$1;
        r0 = r22;
        r1 = r40;
        r2 = r40;
        r0.<init>(r2);
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r36 = -1;
        r37 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r36 = org.telegram.ui.Components.LayoutHelper.createFrame(r36, r37);
        r0 = r35;
        r1 = r22;
        r2 = r36;
        r0.addView(r1, r2);
        r35 = new android.view.View;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.backgroundTablet = r0;
        r35 = r40.getResources();
        r36 = NUM; // 0x7var_ float:1.7944726E38 double:1.052935539E-314;
        r13 = r35.getDrawable(r36);
        r13 = (android.graphics.drawable.BitmapDrawable) r13;
        r35 = android.graphics.Shader.TileMode.REPEAT;
        r36 = android.graphics.Shader.TileMode.REPEAT;
        r0 = r35;
        r1 = r36;
        r13.setTileModeXY(r0, r1);
        r0 = r40;
        r0 = r0.backgroundTablet;
        r35 = r0;
        r0 = r35;
        r0.setBackgroundDrawable(r13);
        r0 = r40;
        r0 = r0.backgroundTablet;
        r35 = r0;
        r36 = -1;
        r37 = -1;
        r36 = org.telegram.ui.Components.LayoutHelper.createRelative(r36, r37);
        r0 = r22;
        r1 = r35;
        r2 = r36;
        r0.addView(r1, r2);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r22;
        r1 = r35;
        r0.addView(r1);
        r35 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.rightActionBarLayout = r0;
        r0 = r40;
        r0 = r0.rightActionBarLayout;
        r35 = r0;
        r36 = rightFragmentsStack;
        r35.init(r36);
        r0 = r40;
        r0 = r0.rightActionBarLayout;
        r35 = r0;
        r0 = r35;
        r1 = r40;
        r0.setDelegate(r1);
        r0 = r40;
        r0 = r0.rightActionBarLayout;
        r35 = r0;
        r0 = r22;
        r1 = r35;
        r0.addView(r1);
        r35 = new android.widget.FrameLayout;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.shadowTabletSide = r0;
        r0 = r40;
        r0 = r0.shadowTabletSide;
        r35 = r0;
        r36 = NUM; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r35.setBackgroundColor(r36);
        r0 = r40;
        r0 = r0.shadowTabletSide;
        r35 = r0;
        r0 = r22;
        r1 = r35;
        r0.addView(r1);
        r35 = new android.widget.FrameLayout;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.shadowTablet = r0;
        r0 = r40;
        r0 = r0.shadowTablet;
        r36 = r0;
        r35 = layerFragmentsStack;
        r35 = r35.isEmpty();
        if (r35 == 0) goto L_0x06a7;
    L_0x032c:
        r35 = 8;
    L_0x032e:
        r0 = r36;
        r1 = r35;
        r0.setVisibility(r1);
        r0 = r40;
        r0 = r0.shadowTablet;
        r35 = r0;
        r36 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r35.setBackgroundColor(r36);
        r0 = r40;
        r0 = r0.shadowTablet;
        r35 = r0;
        r0 = r22;
        r1 = r35;
        r0.addView(r1);
        r0 = r40;
        r0 = r0.shadowTablet;
        r35 = r0;
        r36 = new org.telegram.ui.LaunchActivity$$Lambda$0;
        r0 = r36;
        r1 = r40;
        r0.<init>(r1);
        r35.setOnTouchListener(r36);
        r0 = r40;
        r0 = r0.shadowTablet;
        r35 = r0;
        r36 = org.telegram.ui.LaunchActivity$$Lambda$1.$instance;
        r35.setOnClickListener(r36);
        r35 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.layersActionBarLayout = r0;
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r36 = 1;
        r35.setRemoveActionBarExtraHeight(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r40;
        r0 = r0.shadowTablet;
        r36 = r0;
        r35.setBackgroundView(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r36 = 1;
        r35.setUseAlphaAnimations(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r36 = NUM; // 0x7var_a float:1.7944696E38 double:1.0529355317E-314;
        r35.setBackgroundResource(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r36 = layerFragmentsStack;
        r35.init(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r35;
        r1 = r40;
        r0.setDelegate(r1);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r36 = r0;
        r35.setDrawerLayoutContainer(r36);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r36 = r0;
        r35 = layerFragmentsStack;
        r35 = r35.isEmpty();
        if (r35 == 0) goto L_0x06ab;
    L_0x03df:
        r35 = 8;
    L_0x03e1:
        r0 = r36;
        r1 = r35;
        r0.setVisibility(r1);
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r22;
        r1 = r35;
        r0.addView(r1);
    L_0x03f5:
        r35 = new org.telegram.ui.Components.RecyclerListView;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.sideMenu = r0;
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r35 = r35.getItemAnimator();
        r35 = (org.telegram.messenger.support.widget.DefaultItemAnimator) r35;
        r36 = 0;
        r35.setDelayAnimations(r36);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r36 = "chats_menuBackground";
        r36 = org.telegram.ui.ActionBar.Theme.getColor(r36);
        r35.setBackgroundColor(r36);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r36 = new org.telegram.messenger.support.widget.LinearLayoutManager;
        r37 = 1;
        r38 = 0;
        r0 = r36;
        r1 = r40;
        r2 = r37;
        r3 = r38;
        r0.<init>(r1, r2, r3);
        r35.setLayoutManager(r36);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r36 = new org.telegram.ui.Adapters.DrawerLayoutAdapter;
        r0 = r36;
        r1 = r40;
        r0.<init>(r1);
        r0 = r36;
        r1 = r40;
        r1.drawerLayoutAdapter = r0;
        r35.setAdapter(r36);
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r0 = r40;
        r0 = r0.sideMenu;
        r36 = r0;
        r35.setDrawerLayout(r36);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r23 = r35.getLayoutParams();
        r23 = (android.widget.FrameLayout.LayoutParams) r23;
        r29 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();
        r35 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r35 == 0) goto L_0x06c9;
    L_0x047c:
        r35 = NUM; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r35 = org.telegram.messenger.AndroidUtilities.dp(r35);
    L_0x0482:
        r0 = r35;
        r1 = r23;
        r1.width = r0;
        r35 = -1;
        r0 = r35;
        r1 = r23;
        r1.height = r0;
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r0 = r35;
        r1 = r23;
        r0.setLayoutParams(r1);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r36 = new org.telegram.ui.LaunchActivity$$Lambda$2;
        r0 = r36;
        r1 = r40;
        r0.<init>(r1);
        r35.setOnItemClickListener(r36);
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r0 = r40;
        r0 = r0.actionBarLayout;
        r36 = r0;
        r35.setParentActionBarLayout(r36);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r36 = r0;
        r35.setDrawerLayoutContainer(r36);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r36 = mainFragmentsStack;
        r35.init(r36);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r1 = r40;
        r0.setDelegate(r1);
        org.telegram.ui.ActionBar.Theme.loadWallpaper();
        r35 = new org.telegram.ui.Components.PasscodeView;
        r0 = r35;
        r1 = r40;
        r0.<init>(r1);
        r0 = r35;
        r1 = r40;
        r1.passcodeView = r0;
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r0 = r40;
        r0 = r0.passcodeView;
        r36 = r0;
        r37 = -1;
        r38 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r37 = org.telegram.ui.Components.LayoutHelper.createFrame(r37, r38);
        r35.addView(r36, r37);
        r40.checkCurrentAccount();
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r37 = 1;
        r0 = r37;
        r0 = new java.lang.Object[r0];
        r37 = r0;
        r38 = 0;
        r37[r38] = r40;
        r35.postNotificationName(r36, r37);
        r0 = r40;
        r0 = r0.currentAccount;
        r35 = r0;
        r35 = org.telegram.tgnet.ConnectionsManager.getInstance(r35);
        r35 = r35.getConnectionState();
        r0 = r35;
        r1 = r40;
        r1.currentConnectionState = r0;
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.reloadInterface;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.suggestedLangpack;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.didSetNewTheme;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.didSetPasscode;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r35 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r36 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.addObserver(r1, r2);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r35 = r35.isEmpty();
        if (r35 == 0) goto L_0x083f;
    L_0x05d3:
        r0 = r40;
        r0 = r0.currentAccount;
        r35 = r0;
        r35 = org.telegram.messenger.UserConfig.getInstance(r35);
        r35 = r35.isClientActivated();
        if (r35 != 0) goto L_0x06ed;
    L_0x05e3:
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r36 = new org.telegram.ui.LoginActivity;
        r36.<init>();
        r35.addFragmentToStack(r36);
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r36 = 0;
        r37 = 0;
        r35.setAllowOpenDrawer(r36, r37);
    L_0x05fe:
        if (r41 == 0) goto L_0x0624;
    L_0x0600:
        r35 = "fragment";
        r0 = r41;
        r1 = r35;
        r16 = r0.getString(r1);	 Catch:{ Exception -> 0x079d }
        if (r16 == 0) goto L_0x0624;
    L_0x060d:
        r35 = "args";
        r0 = r41;
        r1 = r35;
        r7 = r0.getBundle(r1);	 Catch:{ Exception -> 0x079d }
        r35 = -1;
        r36 = r16.hashCode();	 Catch:{ Exception -> 0x079d }
        switch(r36) {
            case -1529105743: goto L_0x0770;
            case -1349522494: goto L_0x075f;
            case 3052376: goto L_0x071b;
            case 98629247: goto L_0x073d;
            case 738950403: goto L_0x074e;
            case 1434631203: goto L_0x072c;
            default: goto L_0x0621;
        };
    L_0x0621:
        switch(r35) {
            case 0: goto L_0x0781;
            case 1: goto L_0x07a3;
            case 2: goto L_0x07be;
            case 3: goto L_0x07e0;
            case 4: goto L_0x07fc;
            case 5: goto L_0x081e;
            default: goto L_0x0624;
        };
    L_0x0624:
        r40.checkLayout();
        r36 = r40.getIntent();
        r37 = 0;
        if (r41 == 0) goto L_0x0911;
    L_0x062f:
        r35 = 1;
    L_0x0631:
        r38 = 0;
        r0 = r40;
        r1 = r36;
        r2 = r37;
        r3 = r35;
        r4 = r38;
        r0.handleIntent(r1, r2, r3, r4);
        r24 = android.os.Build.DISPLAY;	 Catch:{ Exception -> 0x091f }
        r25 = android.os.Build.USER;	 Catch:{ Exception -> 0x091f }
        if (r24 == 0) goto L_0x0915;
    L_0x0646:
        r24 = r24.toLowerCase();	 Catch:{ Exception -> 0x091f }
    L_0x064a:
        if (r25 == 0) goto L_0x091a;
    L_0x064c:
        r25 = r24.toLowerCase();	 Catch:{ Exception -> 0x091f }
    L_0x0650:
        r35 = "flyme";
        r0 = r24;
        r1 = r35;
        r35 = r0.contains(r1);	 Catch:{ Exception -> 0x091f }
        if (r35 != 0) goto L_0x066a;
    L_0x065d:
        r35 = "flyme";
        r0 = r25;
        r1 = r35;
        r35 = r0.contains(r1);	 Catch:{ Exception -> 0x091f }
        if (r35 == 0) goto L_0x0690;
    L_0x066a:
        r35 = 1;
        org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r35;	 Catch:{ Exception -> 0x091f }
        r35 = r40.getWindow();	 Catch:{ Exception -> 0x091f }
        r35 = r35.getDecorView();	 Catch:{ Exception -> 0x091f }
        r34 = r35.getRootView();	 Catch:{ Exception -> 0x091f }
        r35 = r34.getViewTreeObserver();	 Catch:{ Exception -> 0x091f }
        r36 = new org.telegram.ui.LaunchActivity$$Lambda$3;	 Catch:{ Exception -> 0x091f }
        r0 = r36;
        r1 = r34;
        r0.<init>(r1);	 Catch:{ Exception -> 0x091f }
        r0 = r36;
        r1 = r40;
        r1.onGlobalLayoutListener = r0;	 Catch:{ Exception -> 0x091f }
        r35.addOnGlobalLayoutListener(r36);	 Catch:{ Exception -> 0x091f }
    L_0x0690:
        r35 = org.telegram.messenger.MediaController.getInstance();
        r36 = 1;
        r0 = r35;
        r1 = r40;
        r2 = r36;
        r0.setBaseActivity(r1, r2);
        goto L_0x0058;
    L_0x06a1:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
        goto L_0x019d;
    L_0x06a7:
        r35 = 0;
        goto L_0x032e;
    L_0x06ab:
        r35 = 0;
        goto L_0x03e1;
    L_0x06af:
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r0 = r40;
        r0 = r0.actionBarLayout;
        r36 = r0;
        r37 = new android.view.ViewGroup$LayoutParams;
        r38 = -1;
        r39 = -1;
        r37.<init>(r38, r39);
        r35.addView(r36, r37);
        goto L_0x03f5;
    L_0x06c9:
        r35 = NUM; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r35 = org.telegram.messenger.AndroidUtilities.dp(r35);
        r0 = r29;
        r0 = r0.x;
        r36 = r0;
        r0 = r29;
        r0 = r0.y;
        r37 = r0;
        r36 = java.lang.Math.min(r36, r37);
        r37 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r37 = org.telegram.messenger.AndroidUtilities.dp(r37);
        r36 = r36 - r37;
        r35 = java.lang.Math.min(r35, r36);
        goto L_0x0482;
    L_0x06ed:
        r12 = new org.telegram.ui.DialogsActivity;
        r35 = 0;
        r0 = r35;
        r12.<init>(r0);
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r0 = r35;
        r12.setSideMenu(r0);
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0.addFragmentToStack(r12);
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r36 = 1;
        r37 = 0;
        r35.setAllowOpenDrawer(r36, r37);
        goto L_0x05fe;
    L_0x071b:
        r36 = "chat";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x0728:
        r35 = 0;
        goto L_0x0621;
    L_0x072c:
        r36 = "settings";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x0739:
        r35 = 1;
        goto L_0x0621;
    L_0x073d:
        r36 = "group";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x074a:
        r35 = 2;
        goto L_0x0621;
    L_0x074e:
        r36 = "channel";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x075b:
        r35 = 3;
        goto L_0x0621;
    L_0x075f:
        r36 = "chat_profile";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x076c:
        r35 = 4;
        goto L_0x0621;
    L_0x0770:
        r36 = "wallpapers";
        r0 = r16;
        r1 = r36;
        r36 = r0.equals(r1);	 Catch:{ Exception -> 0x079d }
        if (r36 == 0) goto L_0x0621;
    L_0x077d:
        r35 = 5;
        goto L_0x0621;
    L_0x0781:
        if (r7 == 0) goto L_0x0624;
    L_0x0783:
        r9 = new org.telegram.ui.ChatActivity;	 Catch:{ Exception -> 0x079d }
        r9.<init>(r7);	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r35 = r0.addFragmentToStack(r9);	 Catch:{ Exception -> 0x079d }
        if (r35 == 0) goto L_0x0624;
    L_0x0796:
        r0 = r41;
        r9.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x079d:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
        goto L_0x0624;
    L_0x07a3:
        r30 = new org.telegram.ui.SettingsActivity;	 Catch:{ Exception -> 0x079d }
        r30.<init>();	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r1 = r30;
        r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x079d }
        r0 = r30;
        r1 = r41;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x07be:
        if (r7 == 0) goto L_0x0624;
    L_0x07c0:
        r18 = new org.telegram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x079d }
        r0 = r18;
        r0.<init>(r7);	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r1 = r18;
        r35 = r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x079d }
        if (r35 == 0) goto L_0x0624;
    L_0x07d7:
        r0 = r18;
        r1 = r41;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x07e0:
        if (r7 == 0) goto L_0x0624;
    L_0x07e2:
        r8 = new org.telegram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x079d }
        r8.<init>(r7);	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r35 = r0.addFragmentToStack(r8);	 Catch:{ Exception -> 0x079d }
        if (r35 == 0) goto L_0x0624;
    L_0x07f5:
        r0 = r41;
        r8.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x07fc:
        if (r7 == 0) goto L_0x0624;
    L_0x07fe:
        r27 = new org.telegram.ui.ProfileActivity;	 Catch:{ Exception -> 0x079d }
        r0 = r27;
        r0.<init>(r7);	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r1 = r27;
        r35 = r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x079d }
        if (r35 == 0) goto L_0x0624;
    L_0x0815:
        r0 = r27;
        r1 = r41;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x081e:
        r30 = new org.telegram.ui.WallpapersListActivity;	 Catch:{ Exception -> 0x079d }
        r35 = 0;
        r0 = r30;
        r1 = r35;
        r0.<init>(r1);	 Catch:{ Exception -> 0x079d }
        r0 = r40;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x079d }
        r35 = r0;
        r0 = r35;
        r1 = r30;
        r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x079d }
        r0 = r30;
        r1 = r41;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x079d }
        goto L_0x0624;
    L_0x083f:
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r36 = 0;
        r15 = r35.get(r36);
        r15 = (org.telegram.ui.ActionBar.BaseFragment) r15;
        r0 = r15 instanceof org.telegram.ui.DialogsActivity;
        r35 = r0;
        if (r35 == 0) goto L_0x0866;
    L_0x0859:
        r15 = (org.telegram.ui.DialogsActivity) r15;
        r0 = r40;
        r0 = r0.sideMenu;
        r35 = r0;
        r0 = r35;
        r15.setSideMenu(r0);
    L_0x0866:
        r6 = 1;
        r35 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r35 == 0) goto L_0x08cb;
    L_0x086d:
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r35 = r35.size();
        r36 = 1;
        r0 = r35;
        r1 = r36;
        if (r0 > r1) goto L_0x090f;
    L_0x0885:
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r35 = r35.isEmpty();
        if (r35 == 0) goto L_0x090f;
    L_0x0897:
        r6 = 1;
    L_0x0898:
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r35 = r35.size();
        r36 = 1;
        r0 = r35;
        r1 = r36;
        if (r0 != r1) goto L_0x08cb;
    L_0x08b0:
        r0 = r40;
        r0 = r0.layersActionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r36 = 0;
        r35 = r35.get(r36);
        r0 = r35;
        r0 = r0 instanceof org.telegram.ui.LoginActivity;
        r35 = r0;
        if (r35 == 0) goto L_0x08cb;
    L_0x08ca:
        r6 = 0;
    L_0x08cb:
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r35 = r35.size();
        r36 = 1;
        r0 = r35;
        r1 = r36;
        if (r0 != r1) goto L_0x08fe;
    L_0x08e3:
        r0 = r40;
        r0 = r0.actionBarLayout;
        r35 = r0;
        r0 = r35;
        r0 = r0.fragmentsStack;
        r35 = r0;
        r36 = 0;
        r35 = r35.get(r36);
        r0 = r35;
        r0 = r0 instanceof org.telegram.ui.LoginActivity;
        r35 = r0;
        if (r35 == 0) goto L_0x08fe;
    L_0x08fd:
        r6 = 0;
    L_0x08fe:
        r0 = r40;
        r0 = r0.drawerLayoutContainer;
        r35 = r0;
        r36 = 0;
        r0 = r35;
        r1 = r36;
        r0.setAllowOpenDrawer(r6, r1);
        goto L_0x0624;
    L_0x090f:
        r6 = 0;
        goto L_0x0898;
    L_0x0911:
        r35 = 0;
        goto L_0x0631;
    L_0x0915:
        r24 = "";
        goto L_0x064a;
    L_0x091a:
        r25 = "";
        goto L_0x0650;
    L_0x091f:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
        goto L_0x0690;
    L_0x0925:
        r35 = move-exception;
        goto L_0x017c;
    L_0x0928:
        r35 = move-exception;
        goto L_0x0173;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    final /* synthetic */ boolean lambda$onCreate$0$LaunchActivity(View v, MotionEvent event) {
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
            int a = 0;
            while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                a = (a - 1) + 1;
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    static final /* synthetic */ void lambda$onCreate$1$LaunchActivity(View v) {
    }

    final /* synthetic */ void lambda$onCreate$2$LaunchActivity(View view, int position) {
        boolean z = false;
        if (position == 0) {
            DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
            if (!this.drawerLayoutAdapter.isAccountsShowed()) {
                z = true;
            }
            drawerLayoutAdapter.setAccountsShowed(z, true);
        } else if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (view instanceof DrawerAddCell) {
            int freeAccount = -1;
            for (int a = 0; a < 3; a++) {
                if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                }
            }
            if (freeAccount >= 0) {
                lambda$runLinkRequest$27$LaunchActivity(new LoginActivity(freeAccount));
            }
            this.drawerLayoutContainer.closeDrawer(false);
        } else {
            int id = this.drawerLayoutAdapter.getId(position);
            Bundle args;
            if (id == 2) {
                args = new Bundle();
                args.putBoolean("showFabButton", true);
                lambda$runLinkRequest$27$LaunchActivity(new GroupCreateActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                lambda$runLinkRequest$27$LaunchActivity(new ContactsActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 4) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                    lambda$runLinkRequest$27$LaunchActivity(new ChannelIntroActivity());
                    preferences.edit().putBoolean("channel_intro", true).commit();
                } else {
                    args = new Bundle();
                    args.putInt("step", 0);
                    lambda$runLinkRequest$27$LaunchActivity(new ChannelCreateActivity(args));
                }
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 6) {
                lambda$runLinkRequest$27$LaunchActivity(new ContactsActivity(null));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 7) {
                lambda$runLinkRequest$27$LaunchActivity(new InviteContactsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 8) {
                lambda$runLinkRequest$27$LaunchActivity(new SettingsActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 9) {
                Browser.openUrl((Context) this, LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 10) {
                lambda$runLinkRequest$27$LaunchActivity(new CallLogActivity());
                this.drawerLayoutContainer.closeDrawer(false);
            } else if (id == 11) {
                args = new Bundle();
                args.putInt("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                lambda$runLinkRequest$27$LaunchActivity(new ChatActivity(args));
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    static final /* synthetic */ void lambda$onCreate$3$LaunchActivity(View view) {
        int height = view.getMeasuredHeight();
        if (VERSION.SDK_INT >= 21) {
            height -= AndroidUtilities.statusBarHeight;
        }
        if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
            AndroidUtilities.displaySize.y = height;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    public void switchToAccount(int account, boolean removeAll) {
        if (account != UserConfig.selectedAccount) {
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
            if (UserConfig.getInstance(account).unacceptedTermsOfService != null) {
                showTosActivity(account, UserConfig.getInstance(account).unacceptedTermsOfService);
            }
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int account = -1;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                account = a;
                break;
            }
        }
        if (this.termsOfServiceView != null) {
            this.termsOfServiceView.setVisibility(8);
        }
        if (account != -1) {
            switchToAccount(account, true);
            return;
        }
        if (this.drawerLayoutAdapter != null) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
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
        }
        this.currentAccount = UserConfig.selectedAccount;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        updateCurrentConnectionState(this.currentAccount);
    }

    private void checkLayout() {
        int i = 0;
        int i2 = 8;
        if (AndroidUtilities.isTablet() && this.rightActionBarLayout != null) {
            int a;
            BaseFragment chatFragment;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                this.tabletFullSize = true;
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    a = 0;
                    while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                        chatFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(a);
                        if (chatFragment instanceof ChatActivity) {
                            ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                        }
                        chatFragment.onPause();
                        this.rightActionBarLayout.fragmentsStack.remove(a);
                        this.actionBarLayout.fragmentsStack.add(chatFragment);
                        a = (a - 1) + 1;
                    }
                    if (this.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                    }
                }
                this.shadowTabletSide.setVisibility(8);
                this.rightActionBarLayout.setVisibility(8);
                View view = this.backgroundTablet;
                if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i2 = 0;
                }
                view.setVisibility(i2);
                return;
            }
            int i3;
            this.tabletFullSize = false;
            if (this.actionBarLayout.fragmentsStack.size() >= 2) {
                for (a = 1; a < this.actionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                    chatFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(a);
                    if (chatFragment instanceof ChatActivity) {
                        ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                    }
                    chatFragment.onPause();
                    this.actionBarLayout.fragmentsStack.remove(a);
                    this.rightActionBarLayout.fragmentsStack.add(chatFragment);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            ActionBarLayout actionBarLayout = this.rightActionBarLayout;
            if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            actionBarLayout.setVisibility(i3);
            View view2 = this.backgroundTablet;
            if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                i3 = 0;
            } else {
                i3 = 8;
            }
            view2.setVisibility(i3);
            FrameLayout frameLayout = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    private void showUpdateActivity(int account, TL_help_appUpdate update) {
        if (this.blockingUpdateView == null) {
            this.blockingUpdateView = new BlockingUpdateView(this) {
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (visibility == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.drawerLayoutContainer.addView(this.blockingUpdateView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(account, update);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int account, TL_help_termsOfService tos) {
        if (this.termsOfServiceView == null) {
            this.termsOfServiceView = new TermsOfServiceView(this);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int account) {
                    UserConfig.getInstance(account).unacceptedTermsOfService = null;
                    UserConfig.getInstance(account).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }

                public void onDeclineTerms(int account) {
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }
            });
        }
        TL_help_termsOfService currentTos = UserConfig.getInstance(account).unacceptedTermsOfService;
        if (currentTos != tos && (currentTos == null || !currentTos.id.data.equals(tos.id.data))) {
            UserConfig.getInstance(account).unacceptedTermsOfService = tos;
            UserConfig.getInstance(account).saveConfig(false);
        }
        this.termsOfServiceView.show(account, tos);
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
            this.passcodeView.setDelegate(new LaunchActivity$$Lambda$4(this));
        }
    }

    final /* synthetic */ void lambda$showPasscodeActivity$4$LaunchActivity() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        if (this.passcodeSaveIntent != null) {
            handleIntent(this.passcodeSaveIntent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x015e  */
    /* JADX WARNING: Missing block: B:54:0x01bd, code:
            if (org.telegram.messenger.MessagesController.getInstance(r48[0]).checkCanOpenChat(r31, (org.telegram.ui.ActionBar.BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)) != false) goto L_0x01bf;
     */
    /* JADX WARNING: Missing block: B:85:0x028a, code:
            if (r71.startsWith("https://") != false) goto L_0x028c;
     */
    /* JADX WARNING: Missing block: B:104:0x02ea, code:
            if (r73.startsWith("image/") == false) goto L_0x02ec;
     */
    /* JADX WARNING: Missing block: B:106:0x02fb, code:
            if (r74.toString().toLowerCase().endsWith(".jpg") != false) goto L_0x02fd;
     */
    /* JADX WARNING: Missing block: B:108:0x0301, code:
            if (r79.photoPathsArray != null) goto L_0x030c;
     */
    /* JADX WARNING: Missing block: B:109:0x0303, code:
            r79.photoPathsArray = new java.util.ArrayList();
     */
    /* JADX WARNING: Missing block: B:110:0x030c, code:
            r47 = new org.telegram.messenger.SendMessagesHelper.SendingMediaInfo();
            r47.uri = r74;
            r79.photoPathsArray.add(r47);
     */
    /* JADX WARNING: Missing block: B:211:0x0597, code:
            if (r45.equals("telegram.dog") != false) goto L_0x0599;
     */
    /* JADX WARNING: Missing block: B:518:0x0fa3, code:
            if (org.telegram.messenger.MessagesController.getInstance(r48[0]).checkCanOpenChat(r31, (org.telegram.ui.ActionBar.BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)) != false) goto L_0x0fa5;
     */
    private boolean handleIntent(android.content.Intent r80, boolean r81, boolean r82, boolean r83) {
        /*
        r79 = this;
        r4 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r79, r80);
        if (r4 == 0) goto L_0x0024;
    L_0x0006:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0021;
    L_0x0013:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
    L_0x0021:
        r59 = 1;
    L_0x0023:
        return r59;
    L_0x0024:
        r4 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r4 == 0) goto L_0x004f;
    L_0x002a:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x004f;
    L_0x0034:
        if (r80 == 0) goto L_0x0043;
    L_0x0036:
        r4 = "android.intent.action.MAIN";
        r5 = r80.getAction();
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x004f;
    L_0x0043:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r5 = 0;
        r20 = 1;
        r0 = r20;
        r4.closePhoto(r5, r0);
    L_0x004f:
        r44 = r80.getFlags();
        r4 = 1;
        r0 = new int[r4];
        r48 = r0;
        r4 = 0;
        r5 = "currentAccount";
        r20 = org.telegram.messenger.UserConfig.selectedAccount;
        r0 = r80;
        r1 = r20;
        r5 = r0.getIntExtra(r5, r1);
        r48[r4] = r5;
        r4 = 0;
        r4 = r48[r4];
        r5 = 1;
        r0 = r79;
        r0.switchToAccount(r4, r5);
        if (r83 != 0) goto L_0x00a2;
    L_0x0073:
        r4 = 1;
        r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);
        if (r4 != 0) goto L_0x007e;
    L_0x007a:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r4 == 0) goto L_0x00a2;
    L_0x007e:
        r79.showPasscodeActivity();
        r0 = r80;
        r1 = r79;
        r1.passcodeSaveIntent = r0;
        r0 = r81;
        r1 = r79;
        r1.passcodeSaveIntentIsNew = r0;
        r0 = r82;
        r1 = r79;
        r1.passcodeSaveIntentIsRestore = r0;
        r0 = r79;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r5 = 0;
        r4.saveConfig(r5);
        r59 = 0;
        goto L_0x0023;
    L_0x00a2:
        r59 = 0;
        r63 = 0;
        r60 = 0;
        r61 = 0;
        r62 = 0;
        r53 = 0;
        r52 = 0;
        r38 = 0;
        r4 = org.telegram.messenger.SharedConfig.directShare;
        if (r4 == 0) goto L_0x00cd;
    L_0x00b6:
        if (r80 == 0) goto L_0x023d;
    L_0x00b8:
        r4 = r80.getExtras();
        if (r4 == 0) goto L_0x023d;
    L_0x00be:
        r4 = r80.getExtras();
        r5 = "dialogId";
        r24 = 0;
        r0 = r24;
        r38 = r4.getLong(r5, r0);
    L_0x00cd:
        r67 = 0;
        r69 = 0;
        r68 = 0;
        r4 = 0;
        r0 = r79;
        r0.photoPathsArray = r4;
        r4 = 0;
        r0 = r79;
        r0.videoPath = r4;
        r4 = 0;
        r0 = r79;
        r0.sendingText = r4;
        r4 = 0;
        r0 = r79;
        r0.documentsPathsArray = r4;
        r4 = 0;
        r0 = r79;
        r0.documentsOriginalPathsArray = r4;
        r4 = 0;
        r0 = r79;
        r0.documentsMimeType = r4;
        r4 = 0;
        r0 = r79;
        r0.documentsUrisArray = r4;
        r4 = 0;
        r0 = r79;
        r0.contactsToSend = r4;
        r4 = 0;
        r0 = r79;
        r0.contactsToSendUri = r4;
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r4 = r4 & r44;
        if (r4 != 0) goto L_0x016b;
    L_0x0106:
        if (r80 == 0) goto L_0x016b;
    L_0x0108:
        r4 = r80.getAction();
        if (r4 == 0) goto L_0x016b;
    L_0x010e:
        if (r82 != 0) goto L_0x016b;
    L_0x0110:
        r4 = "android.intent.action.SEND";
        r5 = r80.getAction();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x03b9;
    L_0x011d:
        r43 = 0;
        r73 = r80.getType();
        if (r73 == 0) goto L_0x024d;
    L_0x0125:
        r4 = "text/x-vcard";
        r0 = r73;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x024d;
    L_0x0130:
        r4 = r80.getExtras();	 Catch:{ Exception -> 0x0245 }
        r5 = "android.intent.extra.STREAM";
        r74 = r4.get(r5);	 Catch:{ Exception -> 0x0245 }
        r74 = (android.net.Uri) r74;	 Catch:{ Exception -> 0x0245 }
        if (r74 == 0) goto L_0x0241;
    L_0x013f:
        r0 = r79;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0245 }
        r5 = 0;
        r20 = 0;
        r23 = 0;
        r0 = r74;
        r1 = r20;
        r2 = r23;
        r4 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r0, r4, r5, r1, r2);	 Catch:{ Exception -> 0x0245 }
        r0 = r79;
        r0.contactsToSend = r4;	 Catch:{ Exception -> 0x0245 }
        r0 = r74;
        r1 = r79;
        r1.contactsToSendUri = r0;	 Catch:{ Exception -> 0x0245 }
    L_0x015c:
        if (r43 == 0) goto L_0x016b;
    L_0x015e:
        r4 = "Unsupported content";
        r5 = 0;
        r0 = r79;
        r4 = android.widget.Toast.makeText(r0, r4, r5);
        r4.show();
    L_0x016b:
        r0 = r79;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 == 0) goto L_0x01de;
    L_0x0179:
        if (r63 == 0) goto L_0x0f5f;
    L_0x017b:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "user_id";
        r0 = r31;
        r1 = r63;
        r0.putInt(r4, r1);
        if (r62 == 0) goto L_0x0196;
    L_0x018c:
        r4 = "message_id";
        r0 = r31;
        r1 = r62;
        r0.putInt(r4, r1);
    L_0x0196:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x01bf;
    L_0x019e:
        r4 = 0;
        r4 = r48[r4];
        r5 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = mainFragmentsStack;
        r20 = mainFragmentsStack;
        r20 = r20.size();
        r20 = r20 + -1;
        r0 = r20;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r31;
        r4 = r5.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x01de;
    L_0x01bf:
        r21 = new org.telegram.ui.ChatActivity;
        r0 = r21;
        r1 = r31;
        r0.<init>(r1);
        r0 = r79;
        r0 = r0.actionBarLayout;
        r20 = r0;
        r22 = 0;
        r23 = 1;
        r24 = 1;
        r25 = 0;
        r4 = r20.presentFragment(r21, r22, r23, r24, r25);
        if (r4 == 0) goto L_0x01de;
    L_0x01dc:
        r59 = 1;
    L_0x01de:
        if (r59 != 0) goto L_0x0235;
    L_0x01e0:
        if (r81 != 0) goto L_0x0235;
    L_0x01e2:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1373;
    L_0x01e8:
        r0 = r79;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 != 0) goto L_0x133f;
    L_0x01f6:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x021a;
    L_0x0202:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r4.addFragmentToStack(r5);
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x021a:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0235;
    L_0x0227:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
    L_0x0235:
        r4 = 0;
        r0 = r80;
        r0.setAction(r4);
        goto L_0x0023;
    L_0x023d:
        r38 = 0;
        goto L_0x00cd;
    L_0x0241:
        r43 = 1;
        goto L_0x015c;
    L_0x0245:
        r41 = move-exception;
        org.telegram.messenger.FileLog.e(r41);
        r43 = 1;
        goto L_0x015c;
    L_0x024d:
        r4 = "android.intent.extra.TEXT";
        r0 = r80;
        r71 = r0.getStringExtra(r4);
        if (r71 != 0) goto L_0x0267;
    L_0x0258:
        r4 = "android.intent.extra.TEXT";
        r0 = r80;
        r72 = r0.getCharSequenceExtra(r4);
        if (r72 == 0) goto L_0x0267;
    L_0x0263:
        r71 = r72.toString();
    L_0x0267:
        r4 = "android.intent.extra.SUBJECT";
        r0 = r80;
        r70 = r0.getStringExtra(r4);
        r4 = android.text.TextUtils.isEmpty(r71);
        if (r4 != 0) goto L_0x0322;
    L_0x0276:
        r4 = "http://";
        r0 = r71;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x028c;
    L_0x0281:
        r4 = "https://";
        r0 = r71;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x02ae;
    L_0x028c:
        r4 = android.text.TextUtils.isEmpty(r70);
        if (r4 != 0) goto L_0x02ae;
    L_0x0292:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r70;
        r4 = r4.append(r0);
        r5 = "\n";
        r4 = r4.append(r5);
        r0 = r71;
        r4 = r4.append(r0);
        r71 = r4.toString();
    L_0x02ae:
        r0 = r71;
        r1 = r79;
        r1.sendingText = r0;
    L_0x02b4:
        r4 = "android.intent.extra.STREAM";
        r0 = r80;
        r55 = r0.getParcelableExtra(r4);
        if (r55 == 0) goto L_0x03af;
    L_0x02bf:
        r0 = r55;
        r4 = r0 instanceof android.net.Uri;
        if (r4 != 0) goto L_0x02cd;
    L_0x02c5:
        r4 = r55.toString();
        r55 = android.net.Uri.parse(r4);
    L_0x02cd:
        r74 = r55;
        r74 = (android.net.Uri) r74;
        if (r74 == 0) goto L_0x02db;
    L_0x02d3:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r74);
        if (r4 == 0) goto L_0x02db;
    L_0x02d9:
        r43 = 1;
    L_0x02db:
        if (r43 != 0) goto L_0x015c;
    L_0x02dd:
        if (r74 == 0) goto L_0x032f;
    L_0x02df:
        if (r73 == 0) goto L_0x02ec;
    L_0x02e1:
        r4 = "image/";
        r0 = r73;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x02fd;
    L_0x02ec:
        r4 = r74.toString();
        r4 = r4.toLowerCase();
        r5 = ".jpg";
        r4 = r4.endsWith(r5);
        if (r4 == 0) goto L_0x032f;
    L_0x02fd:
        r0 = r79;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x030c;
    L_0x0303:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r79;
        r0.photoPathsArray = r4;
    L_0x030c:
        r47 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;
        r47.<init>();
        r0 = r74;
        r1 = r47;
        r1.uri = r0;
        r0 = r79;
        r4 = r0.photoPathsArray;
        r0 = r47;
        r4.add(r0);
        goto L_0x015c;
    L_0x0322:
        r4 = android.text.TextUtils.isEmpty(r70);
        if (r4 != 0) goto L_0x02b4;
    L_0x0328:
        r0 = r70;
        r1 = r79;
        r1.sendingText = r0;
        goto L_0x02b4;
    L_0x032f:
        r56 = org.telegram.messenger.AndroidUtilities.getPath(r74);
        if (r56 == 0) goto L_0x038f;
    L_0x0335:
        r4 = "file:";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x034c;
    L_0x0340:
        r4 = "file://";
        r5 = "";
        r0 = r56;
        r56 = r0.replace(r4, r5);
    L_0x034c:
        if (r73 == 0) goto L_0x0361;
    L_0x034e:
        r4 = "video/";
        r0 = r73;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0361;
    L_0x0359:
        r0 = r56;
        r1 = r79;
        r1.videoPath = r0;
        goto L_0x015c;
    L_0x0361:
        r0 = r79;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x0379;
    L_0x0367:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r79;
        r0.documentsPathsArray = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r79;
        r0.documentsOriginalPathsArray = r4;
    L_0x0379:
        r0 = r79;
        r4 = r0.documentsPathsArray;
        r0 = r56;
        r4.add(r0);
        r0 = r79;
        r4 = r0.documentsOriginalPathsArray;
        r5 = r74.toString();
        r4.add(r5);
        goto L_0x015c;
    L_0x038f:
        r0 = r79;
        r4 = r0.documentsUrisArray;
        if (r4 != 0) goto L_0x039e;
    L_0x0395:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r79;
        r0.documentsUrisArray = r4;
    L_0x039e:
        r0 = r79;
        r4 = r0.documentsUrisArray;
        r0 = r74;
        r4.add(r0);
        r0 = r73;
        r1 = r79;
        r1.documentsMimeType = r0;
        goto L_0x015c;
    L_0x03af:
        r0 = r79;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x015c;
    L_0x03b5:
        r43 = 1;
        goto L_0x015c;
    L_0x03b9:
        r4 = "android.intent.action.SEND_MULTIPLE";
        r5 = r80.getAction();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x052b;
    L_0x03c6:
        r43 = 0;
        r4 = "android.intent.extra.STREAM";
        r0 = r80;
        r75 = r0.getParcelableArrayListExtra(r4);	 Catch:{ Exception -> 0x0511 }
        r73 = r80.getType();	 Catch:{ Exception -> 0x0511 }
        if (r75 == 0) goto L_0x041b;
    L_0x03d7:
        r29 = 0;
    L_0x03d9:
        r4 = r75.size();	 Catch:{ Exception -> 0x0511 }
        r0 = r29;
        if (r0 >= r4) goto L_0x0413;
    L_0x03e1:
        r0 = r75;
        r1 = r29;
        r55 = r0.get(r1);	 Catch:{ Exception -> 0x0511 }
        r55 = (android.os.Parcelable) r55;	 Catch:{ Exception -> 0x0511 }
        r0 = r55;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x03f9;
    L_0x03f1:
        r4 = r55.toString();	 Catch:{ Exception -> 0x0511 }
        r55 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0511 }
    L_0x03f9:
        r0 = r55;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0511 }
        r74 = r0;
        if (r74 == 0) goto L_0x0410;
    L_0x0401:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r74);	 Catch:{ Exception -> 0x0511 }
        if (r4 == 0) goto L_0x0410;
    L_0x0407:
        r0 = r75;
        r1 = r29;
        r0.remove(r1);	 Catch:{ Exception -> 0x0511 }
        r29 = r29 + -1;
    L_0x0410:
        r29 = r29 + 1;
        goto L_0x03d9;
    L_0x0413:
        r4 = r75.isEmpty();	 Catch:{ Exception -> 0x0511 }
        if (r4 == 0) goto L_0x041b;
    L_0x0419:
        r75 = 0;
    L_0x041b:
        if (r75 == 0) goto L_0x0528;
    L_0x041d:
        if (r73 == 0) goto L_0x0478;
    L_0x041f:
        r4 = "image/";
        r0 = r73;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0511 }
        if (r4 == 0) goto L_0x0478;
    L_0x042a:
        r29 = 0;
    L_0x042c:
        r4 = r75.size();	 Catch:{ Exception -> 0x0511 }
        r0 = r29;
        if (r0 >= r4) goto L_0x0517;
    L_0x0434:
        r0 = r75;
        r1 = r29;
        r55 = r0.get(r1);	 Catch:{ Exception -> 0x0511 }
        r55 = (android.os.Parcelable) r55;	 Catch:{ Exception -> 0x0511 }
        r0 = r55;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x044c;
    L_0x0444:
        r4 = r55.toString();	 Catch:{ Exception -> 0x0511 }
        r55 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0511 }
    L_0x044c:
        r0 = r55;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0511 }
        r74 = r0;
        r0 = r79;
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x0461;
    L_0x0458:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0511 }
        r4.<init>();	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r0.photoPathsArray = r4;	 Catch:{ Exception -> 0x0511 }
    L_0x0461:
        r47 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;	 Catch:{ Exception -> 0x0511 }
        r47.<init>();	 Catch:{ Exception -> 0x0511 }
        r0 = r74;
        r1 = r47;
        r1.uri = r0;	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0511 }
        r0 = r47;
        r4.add(r0);	 Catch:{ Exception -> 0x0511 }
        r29 = r29 + 1;
        goto L_0x042c;
    L_0x0478:
        r29 = 0;
    L_0x047a:
        r4 = r75.size();	 Catch:{ Exception -> 0x0511 }
        r0 = r29;
        if (r0 >= r4) goto L_0x0517;
    L_0x0482:
        r0 = r75;
        r1 = r29;
        r55 = r0.get(r1);	 Catch:{ Exception -> 0x0511 }
        r55 = (android.os.Parcelable) r55;	 Catch:{ Exception -> 0x0511 }
        r0 = r55;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x049a;
    L_0x0492:
        r4 = r55.toString();	 Catch:{ Exception -> 0x0511 }
        r55 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0511 }
    L_0x049a:
        r0 = r55;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0511 }
        r74 = r0;
        r56 = org.telegram.messenger.AndroidUtilities.getPath(r74);	 Catch:{ Exception -> 0x0511 }
        r54 = r55.toString();	 Catch:{ Exception -> 0x0511 }
        if (r54 != 0) goto L_0x04ac;
    L_0x04aa:
        r54 = r56;
    L_0x04ac:
        if (r56 == 0) goto L_0x04f2;
    L_0x04ae:
        r4 = "file:";
        r0 = r56;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0511 }
        if (r4 == 0) goto L_0x04c5;
    L_0x04b9:
        r4 = "file://";
        r5 = "";
        r0 = r56;
        r56 = r0.replace(r4, r5);	 Catch:{ Exception -> 0x0511 }
    L_0x04c5:
        r0 = r79;
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x04dd;
    L_0x04cb:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0511 }
        r4.<init>();	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r0.documentsPathsArray = r4;	 Catch:{ Exception -> 0x0511 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0511 }
        r4.<init>();	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r0.documentsOriginalPathsArray = r4;	 Catch:{ Exception -> 0x0511 }
    L_0x04dd:
        r0 = r79;
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0511 }
        r0 = r56;
        r4.add(r0);	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r4 = r0.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x0511 }
        r0 = r54;
        r4.add(r0);	 Catch:{ Exception -> 0x0511 }
    L_0x04ef:
        r29 = r29 + 1;
        goto L_0x047a;
    L_0x04f2:
        r0 = r79;
        r4 = r0.documentsUrisArray;	 Catch:{ Exception -> 0x0511 }
        if (r4 != 0) goto L_0x0501;
    L_0x04f8:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0511 }
        r4.<init>();	 Catch:{ Exception -> 0x0511 }
        r0 = r79;
        r0.documentsUrisArray = r4;	 Catch:{ Exception -> 0x0511 }
    L_0x0501:
        r0 = r79;
        r4 = r0.documentsUrisArray;	 Catch:{ Exception -> 0x0511 }
        r0 = r74;
        r4.add(r0);	 Catch:{ Exception -> 0x0511 }
        r0 = r73;
        r1 = r79;
        r1.documentsMimeType = r0;	 Catch:{ Exception -> 0x0511 }
        goto L_0x04ef;
    L_0x0511:
        r41 = move-exception;
        org.telegram.messenger.FileLog.e(r41);
        r43 = 1;
    L_0x0517:
        if (r43 == 0) goto L_0x016b;
    L_0x0519:
        r4 = "Unsupported content";
        r5 = 0;
        r0 = r79;
        r4 = android.widget.Toast.makeText(r0, r4, r5);
        r4.show();
        goto L_0x016b;
    L_0x0528:
        r43 = 1;
        goto L_0x0517;
    L_0x052b:
        r4 = "android.intent.action.VIEW";
        r5 = r80.getAction();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0e98;
    L_0x0538:
        r36 = r80.getData();
        if (r36 == 0) goto L_0x016b;
    L_0x053e:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r15 = 0;
        r17 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r57 = 0;
        r14 = 0;
        r58 = 0;
        r16 = 0;
        r18 = 0;
        r19 = 0;
        r13 = 0;
        r12 = 0;
        r64 = r36.getScheme();
        if (r64 == 0) goto L_0x0602;
    L_0x055a:
        r4 = "http";
        r0 = r64;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0570;
    L_0x0565:
        r4 = "https";
        r0 = r64;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0823;
    L_0x0570:
        r4 = r36.getHost();
        r45 = r4.toLowerCase();
        r4 = "telegram.me";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0599;
    L_0x0583:
        r4 = "t.me";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0599;
    L_0x058e:
        r4 = "telegram.dog";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0602;
    L_0x0599:
        r56 = r36.getPath();
        if (r56 == 0) goto L_0x0602;
    L_0x059f:
        r4 = r56.length();
        r5 = 1;
        if (r4 <= r5) goto L_0x0602;
    L_0x05a6:
        r4 = 1;
        r0 = r56;
        r56 = r0.substring(r4);
        r4 = "bg/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x06ca;
    L_0x05b8:
        r19 = new org.telegram.tgnet.TLRPC$TL_wallPaper;
        r19.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
        r4.<init>();
        r0 = r19;
        r0.settings = r4;
        r4 = "bg/";
        r5 = "";
        r0 = r56;
        r4 = r0.replace(r4, r5);
        r0 = r19;
        r0.slug = r4;
        r0 = r19;
        r4 = r0.slug;
        if (r4 == 0) goto L_0x063d;
    L_0x05dc:
        r0 = r19;
        r4 = r0.slug;
        r4 = r4.length();
        r5 = 6;
        if (r4 != r5) goto L_0x063d;
    L_0x05e7:
        r0 = r19;
        r4 = r0.settings;	 Catch:{ Exception -> 0x13df }
        r0 = r19;
        r5 = r0.slug;	 Catch:{ Exception -> 0x13df }
        r20 = 16;
        r0 = r20;
        r5 = java.lang.Integer.parseInt(r5, r0);	 Catch:{ Exception -> 0x13df }
        r20 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = r5 | r20;
        r4.background_color = r5;	 Catch:{ Exception -> 0x13df }
    L_0x05fd:
        r4 = 0;
        r0 = r19;
        r0.slug = r4;
    L_0x0602:
        if (r18 != 0) goto L_0x0612;
    L_0x0604:
        r0 = r79;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 == 0) goto L_0x016b;
    L_0x0612:
        if (r57 != 0) goto L_0x0616;
    L_0x0614:
        if (r58 == 0) goto L_0x0da7;
    L_0x0616:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "phone";
        r0 = r31;
        r1 = r57;
        r0.putString(r4, r1);
        r4 = "hash";
        r0 = r31;
        r1 = r58;
        r0.putString(r4, r1);
        r4 = new org.telegram.ui.LaunchActivity$$Lambda$5;
        r0 = r79;
        r1 = r31;
        r4.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x016b;
    L_0x063d:
        r4 = "mode";
        r0 = r36;
        r49 = r0.getQueryParameter(r4);
        if (r49 == 0) goto L_0x068d;
    L_0x0648:
        r49 = r49.toLowerCase();
        r4 = " ";
        r0 = r49;
        r50 = r0.split(r4);
        if (r50 == 0) goto L_0x068d;
    L_0x0657:
        r0 = r50;
        r4 = r0.length;
        if (r4 <= 0) goto L_0x068d;
    L_0x065c:
        r29 = 0;
    L_0x065e:
        r0 = r50;
        r4 = r0.length;
        r0 = r29;
        if (r0 >= r4) goto L_0x068d;
    L_0x0665:
        r4 = "blur";
        r5 = r50[r29];
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x067a;
    L_0x0670:
        r0 = r19;
        r4 = r0.settings;
        r5 = 1;
        r4.blur = r5;
    L_0x0677:
        r29 = r29 + 1;
        goto L_0x065e;
    L_0x067a:
        r4 = "motion";
        r5 = r50[r29];
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0677;
    L_0x0685:
        r0 = r19;
        r4 = r0.settings;
        r5 = 1;
        r4.motion = r5;
        goto L_0x0677;
    L_0x068d:
        r0 = r19;
        r4 = r0.settings;
        r5 = "intensity";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r5 = org.telegram.messenger.Utilities.parseInt(r5);
        r5 = r5.intValue();
        r4.intensity = r5;
        r4 = "bg_color";
        r0 = r36;
        r32 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x06c7 }
        r4 = android.text.TextUtils.isEmpty(r32);	 Catch:{ Exception -> 0x06c7 }
        if (r4 != 0) goto L_0x0602;
    L_0x06b3:
        r0 = r19;
        r4 = r0.settings;	 Catch:{ Exception -> 0x06c7 }
        r5 = 16;
        r0 = r32;
        r5 = java.lang.Integer.parseInt(r0, r5);	 Catch:{ Exception -> 0x06c7 }
        r20 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = r5 | r20;
        r4.background_color = r5;	 Catch:{ Exception -> 0x06c7 }
        goto L_0x0602;
    L_0x06c7:
        r4 = move-exception;
        goto L_0x0602;
    L_0x06ca:
        r4 = "login/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x06e3;
    L_0x06d5:
        r4 = "login/";
        r5 = "";
        r0 = r56;
        r18 = r0.replace(r4, r5);
        goto L_0x0602;
    L_0x06e3:
        r4 = "joinchat/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x06fc;
    L_0x06ee:
        r4 = "joinchat/";
        r5 = "";
        r0 = r56;
        r7 = r0.replace(r4, r5);
        goto L_0x0602;
    L_0x06fc:
        r4 = "addstickers/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0715;
    L_0x0707:
        r4 = "addstickers/";
        r5 = "";
        r0 = r56;
        r8 = r0.replace(r4, r5);
        goto L_0x0602;
    L_0x0715:
        r4 = "msg/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x072b;
    L_0x0720:
        r4 = "share/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x079d;
    L_0x072b:
        r4 = "url";
        r0 = r36;
        r11 = r0.getQueryParameter(r4);
        if (r11 != 0) goto L_0x0739;
    L_0x0736:
        r11 = "";
    L_0x0739:
        r4 = "text";
        r0 = r36;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0779;
    L_0x0744:
        r4 = r11.length();
        if (r4 <= 0) goto L_0x075f;
    L_0x074a:
        r12 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "\n";
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x075f:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "text";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x0779:
        r4 = r11.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r4 <= r5) goto L_0x0788;
    L_0x0781:
        r4 = 0;
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r11 = r11.substring(r4, r5);
    L_0x0788:
        r4 = "\n";
        r4 = r11.endsWith(r4);
        if (r4 == 0) goto L_0x0602;
    L_0x0791:
        r4 = 0;
        r5 = r11.length();
        r5 = r5 + -1;
        r11 = r11.substring(r4, r5);
        goto L_0x0788;
    L_0x079d:
        r4 = "confirmphone";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x07bc;
    L_0x07a8:
        r4 = "phone";
        r0 = r36;
        r57 = r0.getQueryParameter(r4);
        r4 = "hash";
        r0 = r36;
        r58 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x07bc:
        r4 = "setlanguage/";
        r0 = r56;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x07d1;
    L_0x07c7:
        r4 = 12;
        r0 = r56;
        r16 = r0.substring(r4);
        goto L_0x0602;
    L_0x07d1:
        r4 = r56.length();
        r5 = 1;
        if (r4 < r5) goto L_0x0602;
    L_0x07d8:
        r66 = r36.getPathSegments();
        r4 = r66.size();
        if (r4 <= 0) goto L_0x0806;
    L_0x07e2:
        r4 = 0;
        r0 = r66;
        r6 = r0.get(r4);
        r6 = (java.lang.String) r6;
        r4 = r66.size();
        r5 = 1;
        if (r4 <= r5) goto L_0x0806;
    L_0x07f2:
        r4 = 1;
        r0 = r66;
        r4 = r0.get(r4);
        r4 = (java.lang.String) r4;
        r13 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r13.intValue();
        if (r4 != 0) goto L_0x0806;
    L_0x0805:
        r13 = 0;
    L_0x0806:
        r4 = "start";
        r0 = r36;
        r9 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r36;
        r10 = r0.getQueryParameter(r4);
        r4 = "game";
        r0 = r36;
        r14 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0823:
        r4 = "tg";
        r0 = r64;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0602;
    L_0x082e:
        r76 = r36.toString();
        r4 = "tg:resolve";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0848;
    L_0x083d:
        r4 = "tg://resolve";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0928;
    L_0x0848:
        r4 = "tg:resolve";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://resolve";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "domain";
        r0 = r36;
        r6 = r0.getQueryParameter(r4);
        r4 = "telegrampassport";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x08f7;
    L_0x0876:
        r6 = 0;
        r15 = new java.util.HashMap;
        r15.<init>();
        r4 = "scope";
        r0 = r36;
        r65 = r0.getQueryParameter(r4);
        r4 = android.text.TextUtils.isEmpty(r65);
        if (r4 != 0) goto L_0x08e7;
    L_0x088b:
        r4 = "{";
        r0 = r65;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x08e7;
    L_0x0896:
        r4 = "}";
        r0 = r65;
        r4 = r0.endsWith(r4);
        if (r4 == 0) goto L_0x08e7;
    L_0x08a1:
        r4 = "nonce";
        r5 = "nonce";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
    L_0x08b0:
        r4 = "bot_id";
        r5 = "bot_id";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        r4 = "scope";
        r0 = r65;
        r15.put(r4, r0);
        r4 = "public_key";
        r5 = "public_key";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        r4 = "callback_url";
        r5 = "callback_url";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        goto L_0x0602;
    L_0x08e7:
        r4 = "payload";
        r5 = "payload";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        goto L_0x08b0;
    L_0x08f7:
        r4 = "start";
        r0 = r36;
        r9 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r36;
        r10 = r0.getQueryParameter(r4);
        r4 = "game";
        r0 = r36;
        r14 = r0.getQueryParameter(r4);
        r4 = "post";
        r0 = r36;
        r4 = r0.getQueryParameter(r4);
        r13 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r13.intValue();
        if (r4 != 0) goto L_0x0602;
    L_0x0925:
        r13 = 0;
        goto L_0x0602;
    L_0x0928:
        r4 = "tg:bg";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x093e;
    L_0x0933:
        r4 = "tg://bg";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0a43;
    L_0x093e:
        r4 = "tg:bg";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://bg";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r19 = new org.telegram.tgnet.TLRPC$TL_wallPaper;
        r19.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
        r4.<init>();
        r0 = r19;
        r0.settings = r4;
        r4 = "slug";
        r0 = r36;
        r4 = r0.getQueryParameter(r4);
        r0 = r19;
        r0.slug = r4;
        r0 = r19;
        r4 = r0.slug;
        if (r4 != 0) goto L_0x0988;
    L_0x097b:
        r4 = "color";
        r0 = r36;
        r4 = r0.getQueryParameter(r4);
        r0 = r19;
        r0.slug = r4;
    L_0x0988:
        r0 = r19;
        r4 = r0.slug;
        if (r4 == 0) goto L_0x09b6;
    L_0x098e:
        r0 = r19;
        r4 = r0.slug;
        r4 = r4.length();
        r5 = 6;
        if (r4 != r5) goto L_0x09b6;
    L_0x0999:
        r0 = r19;
        r4 = r0.settings;	 Catch:{ Exception -> 0x13dc }
        r0 = r19;
        r5 = r0.slug;	 Catch:{ Exception -> 0x13dc }
        r20 = 16;
        r0 = r20;
        r5 = java.lang.Integer.parseInt(r5, r0);	 Catch:{ Exception -> 0x13dc }
        r20 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = r5 | r20;
        r4.background_color = r5;	 Catch:{ Exception -> 0x13dc }
    L_0x09af:
        r4 = 0;
        r0 = r19;
        r0.slug = r4;
        goto L_0x0602;
    L_0x09b6:
        r4 = "mode";
        r0 = r36;
        r49 = r0.getQueryParameter(r4);
        if (r49 == 0) goto L_0x0a06;
    L_0x09c1:
        r49 = r49.toLowerCase();
        r4 = " ";
        r0 = r49;
        r50 = r0.split(r4);
        if (r50 == 0) goto L_0x0a06;
    L_0x09d0:
        r0 = r50;
        r4 = r0.length;
        if (r4 <= 0) goto L_0x0a06;
    L_0x09d5:
        r29 = 0;
    L_0x09d7:
        r0 = r50;
        r4 = r0.length;
        r0 = r29;
        if (r0 >= r4) goto L_0x0a06;
    L_0x09de:
        r4 = "blur";
        r5 = r50[r29];
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x09f3;
    L_0x09e9:
        r0 = r19;
        r4 = r0.settings;
        r5 = 1;
        r4.blur = r5;
    L_0x09f0:
        r29 = r29 + 1;
        goto L_0x09d7;
    L_0x09f3:
        r4 = "motion";
        r5 = r50[r29];
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x09f0;
    L_0x09fe:
        r0 = r19;
        r4 = r0.settings;
        r5 = 1;
        r4.motion = r5;
        goto L_0x09f0;
    L_0x0a06:
        r0 = r19;
        r4 = r0.settings;
        r5 = "intensity";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r5 = org.telegram.messenger.Utilities.parseInt(r5);
        r5 = r5.intValue();
        r4.intensity = r5;
        r4 = "bg_color";
        r0 = r36;
        r32 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0a40 }
        r4 = android.text.TextUtils.isEmpty(r32);	 Catch:{ Exception -> 0x0a40 }
        if (r4 != 0) goto L_0x0602;
    L_0x0a2c:
        r0 = r19;
        r4 = r0.settings;	 Catch:{ Exception -> 0x0a40 }
        r5 = 16;
        r0 = r32;
        r5 = java.lang.Integer.parseInt(r0, r5);	 Catch:{ Exception -> 0x0a40 }
        r20 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = r5 | r20;
        r4.background_color = r5;	 Catch:{ Exception -> 0x0a40 }
        goto L_0x0602;
    L_0x0a40:
        r4 = move-exception;
        goto L_0x0602;
    L_0x0a43:
        r4 = "tg:join";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a59;
    L_0x0a4e:
        r4 = "tg://join";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0a80;
    L_0x0a59:
        r4 = "tg:join";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://join";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "invite";
        r0 = r36;
        r7 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0a80:
        r4 = "tg:addstickers";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a96;
    L_0x0a8b:
        r4 = "tg://addstickers";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0abd;
    L_0x0a96:
        r4 = "tg:addstickers";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://addstickers";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "set";
        r0 = r36;
        r8 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0abd:
        r4 = "tg:msg";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0ae9;
    L_0x0ac8:
        r4 = "tg://msg";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0ae9;
    L_0x0ad3:
        r4 = "tg://share";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0ae9;
    L_0x0ade:
        r4 = "tg:share";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0b8f;
    L_0x0ae9:
        r4 = "tg:msg";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://msg";
        r20 = "tg://telegram.org";
        r0 = r20;
        r4 = r4.replace(r5, r0);
        r5 = "tg://share";
        r20 = "tg://telegram.org";
        r0 = r20;
        r4 = r4.replace(r5, r0);
        r5 = "tg:share";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "url";
        r0 = r36;
        r11 = r0.getQueryParameter(r4);
        if (r11 != 0) goto L_0x0b2b;
    L_0x0b28:
        r11 = "";
    L_0x0b2b:
        r4 = "text";
        r0 = r36;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0b6b;
    L_0x0b36:
        r4 = r11.length();
        if (r4 <= 0) goto L_0x0b51;
    L_0x0b3c:
        r12 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "\n";
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x0b51:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "text";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x0b6b:
        r4 = r11.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r4 <= r5) goto L_0x0b7a;
    L_0x0b73:
        r4 = 0;
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r11 = r11.substring(r4, r5);
    L_0x0b7a:
        r4 = "\n";
        r4 = r11.endsWith(r4);
        if (r4 == 0) goto L_0x0602;
    L_0x0b83:
        r4 = 0;
        r5 = r11.length();
        r5 = r5 + -1;
        r11 = r11.substring(r4, r5);
        goto L_0x0b7a;
    L_0x0b8f:
        r4 = "tg:confirmphone";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0ba5;
    L_0x0b9a:
        r4 = "tg://confirmphone";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0bd5;
    L_0x0ba5:
        r4 = "tg:confirmphone";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://confirmphone";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "phone";
        r0 = r36;
        r57 = r0.getQueryParameter(r4);
        r4 = "hash";
        r0 = r36;
        r58 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0bd5:
        r4 = "tg:login";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0beb;
    L_0x0be0:
        r4 = "tg://login";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0beb:
        r4 = "tg:login";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://login";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "code";
        r0 = r36;
        r18 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0CLASSNAME:
        r4 = "tg:openmessage";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0c1d:
        r4 = "tg://openmessage";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = "tg:openmessage";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://openmessage";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "user_id";
        r0 = r36;
        r77 = r0.getQueryParameter(r4);
        r4 = "chat_id";
        r0 = r36;
        r33 = r0.getQueryParameter(r4);
        r4 = "message_id";
        r0 = r36;
        r51 = r0.getQueryParameter(r4);
        if (r77 == 0) goto L_0x0c6d;
    L_0x0CLASSNAME:
        r63 = java.lang.Integer.parseInt(r77);	 Catch:{ NumberFormatException -> 0x13cf }
    L_0x0CLASSNAME:
        if (r51 == 0) goto L_0x0602;
    L_0x0CLASSNAME:
        r62 = java.lang.Integer.parseInt(r51);	 Catch:{ NumberFormatException -> 0x13d5 }
        goto L_0x0602;
    L_0x0c6d:
        if (r33 == 0) goto L_0x0CLASSNAME;
    L_0x0c6f:
        r60 = java.lang.Integer.parseInt(r33);	 Catch:{ NumberFormatException -> 0x13d2 }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = "tg:passport";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0c7f:
        r4 = "tg://passport";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0c8a:
        r4 = "tg:secureid";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0d3d;
    L_0x0CLASSNAME:
        r4 = "tg:passport";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://passport";
        r20 = "tg://telegram.org";
        r0 = r20;
        r4 = r4.replace(r5, r0);
        r5 = "tg:secureid";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r15 = new java.util.HashMap;
        r15.<init>();
        r4 = "scope";
        r0 = r36;
        r65 = r0.getQueryParameter(r4);
        r4 = android.text.TextUtils.isEmpty(r65);
        if (r4 != 0) goto L_0x0d2d;
    L_0x0cd1:
        r4 = "{";
        r0 = r65;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0d2d;
    L_0x0cdc:
        r4 = "}";
        r0 = r65;
        r4 = r0.endsWith(r4);
        if (r4 == 0) goto L_0x0d2d;
    L_0x0ce7:
        r4 = "nonce";
        r5 = "nonce";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
    L_0x0cf6:
        r4 = "bot_id";
        r5 = "bot_id";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        r4 = "scope";
        r0 = r65;
        r15.put(r4, r0);
        r4 = "public_key";
        r5 = "public_key";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        r4 = "callback_url";
        r5 = "callback_url";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        goto L_0x0602;
    L_0x0d2d:
        r4 = "payload";
        r5 = "payload";
        r0 = r36;
        r5 = r0.getQueryParameter(r5);
        r15.put(r4, r5);
        goto L_0x0cf6;
    L_0x0d3d:
        r4 = "tg:setlanguage";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0d53;
    L_0x0d48:
        r4 = "tg://setlanguage";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0d7a;
    L_0x0d53:
        r4 = "tg:setlanguage";
        r5 = "tg://telegram.org";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg://setlanguage";
        r20 = "tg://telegram.org";
        r0 = r20;
        r76 = r4.replace(r5, r0);
        r36 = android.net.Uri.parse(r76);
        r4 = "lang";
        r0 = r36;
        r16 = r0.getQueryParameter(r4);
        goto L_0x0602;
    L_0x0d7a:
        r4 = "tg://";
        r5 = "";
        r0 = r76;
        r4 = r0.replace(r4, r5);
        r5 = "tg:";
        r20 = "";
        r0 = r20;
        r17 = r4.replace(r5, r0);
        r4 = 63;
        r0 = r17;
        r46 = r0.indexOf(r4);
        if (r46 < 0) goto L_0x0602;
    L_0x0d9c:
        r4 = 0;
        r0 = r17;
        r1 = r46;
        r17 = r0.substring(r4, r1);
        goto L_0x0602;
    L_0x0da7:
        if (r6 != 0) goto L_0x0dbb;
    L_0x0da9:
        if (r7 != 0) goto L_0x0dbb;
    L_0x0dab:
        if (r8 != 0) goto L_0x0dbb;
    L_0x0dad:
        if (r11 != 0) goto L_0x0dbb;
    L_0x0daf:
        if (r14 != 0) goto L_0x0dbb;
    L_0x0db1:
        if (r15 != 0) goto L_0x0dbb;
    L_0x0db3:
        if (r17 != 0) goto L_0x0dbb;
    L_0x0db5:
        if (r16 != 0) goto L_0x0dbb;
    L_0x0db7:
        if (r18 != 0) goto L_0x0dbb;
    L_0x0db9:
        if (r19 == 0) goto L_0x0de6;
    L_0x0dbb:
        if (r11 == 0) goto L_0x0dda;
    L_0x0dbd:
        r4 = "@";
        r4 = r11.startsWith(r4);
        if (r4 == 0) goto L_0x0dda;
    L_0x0dc6:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = " ";
        r4 = r4.append(r5);
        r4 = r4.append(r11);
        r11 = r4.toString();
    L_0x0dda:
        r4 = 0;
        r5 = r48[r4];
        r20 = 0;
        r4 = r79;
        r4.runLinkRequest(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x016b;
    L_0x0de6:
        r20 = r79.getContentResolver();	 Catch:{ Exception -> 0x0e72 }
        r21 = r80.getData();	 Catch:{ Exception -> 0x0e72 }
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r35 = r20.query(r21, r22, r23, r24, r25);	 Catch:{ Exception -> 0x0e72 }
        r20 = 0;
        if (r35 == 0) goto L_0x0e61;
    L_0x0dfe:
        r4 = r35.moveToFirst();	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        if (r4 == 0) goto L_0x0e61;
    L_0x0e04:
        r4 = "account_name";
        r0 = r35;
        r4 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r0 = r35;
        r4 = r0.getString(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r30 = r4.intValue();	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r29 = 0;
    L_0x0e1d:
        r4 = 3;
        r0 = r29;
        if (r0 >= r4) goto L_0x0e3a;
    L_0x0e22:
        r4 = org.telegram.messenger.UserConfig.getInstance(r29);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r4 = r4.getClientUserId();	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r0 = r30;
        if (r4 != r0) goto L_0x0e78;
    L_0x0e2e:
        r4 = 0;
        r48[r4] = r29;	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r4 = 0;
        r4 = r48[r4];	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r5 = 1;
        r0 = r79;
        r0.switchToAccount(r4, r5);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
    L_0x0e3a:
        r4 = "DATA4";
        r0 = r35;
        r4 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r0 = r35;
        r78 = r0.getInt(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r4 = 0;
        r4 = r48[r4];	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r5 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r23 = 0;
        r0 = r23;
        r0 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r23 = r0;
        r0 = r23;
        r4.postNotificationName(r5, r0);	 Catch:{ Throwable -> 0x0e80, all -> 0x13d8 }
        r63 = r78;
    L_0x0e61:
        if (r35 == 0) goto L_0x016b;
    L_0x0e63:
        if (r20 == 0) goto L_0x0e7b;
    L_0x0e65:
        r35.close();	 Catch:{ Throwable -> 0x0e6a }
        goto L_0x016b;
    L_0x0e6a:
        r4 = move-exception;
        r0 = r20;
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.addSuppressed(r0, r4);	 Catch:{ Exception -> 0x0e72 }
        goto L_0x016b;
    L_0x0e72:
        r41 = move-exception;
        org.telegram.messenger.FileLog.e(r41);
        goto L_0x016b;
    L_0x0e78:
        r29 = r29 + 1;
        goto L_0x0e1d;
    L_0x0e7b:
        r35.close();	 Catch:{ Exception -> 0x0e72 }
        goto L_0x016b;
    L_0x0e80:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x0e82 }
    L_0x0e82:
        r5 = move-exception;
        r20 = r4;
    L_0x0e85:
        if (r35 == 0) goto L_0x0e8c;
    L_0x0e87:
        if (r20 == 0) goto L_0x0e94;
    L_0x0e89:
        r35.close();	 Catch:{ Throwable -> 0x0e8d }
    L_0x0e8c:
        throw r5;	 Catch:{ Exception -> 0x0e72 }
    L_0x0e8d:
        r4 = move-exception;
        r0 = r20;
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.addSuppressed(r0, r4);	 Catch:{ Exception -> 0x0e72 }
        goto L_0x0e8c;
    L_0x0e94:
        r35.close();	 Catch:{ Exception -> 0x0e72 }
        goto L_0x0e8c;
    L_0x0e98:
        r4 = r80.getAction();
        r5 = "org.telegram.messenger.OPEN_ACCOUNT";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0ea9;
    L_0x0ea5:
        r53 = 1;
        goto L_0x016b;
    L_0x0ea9:
        r4 = r80.getAction();
        r5 = "new_dialog";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0eba;
    L_0x0eb6:
        r52 = 1;
        goto L_0x016b;
    L_0x0eba:
        r4 = r80.getAction();
        r5 = "com.tmessages.openchat";
        r4 = r4.startsWith(r5);
        if (r4 == 0) goto L_0x0f3d;
    L_0x0ec7:
        r4 = "chatId";
        r5 = 0;
        r0 = r80;
        r34 = r0.getIntExtra(r4, r5);
        r4 = "userId";
        r5 = 0;
        r0 = r80;
        r78 = r0.getIntExtra(r4, r5);
        r4 = "encId";
        r5 = 0;
        r0 = r80;
        r42 = r0.getIntExtra(r4, r5);
        if (r34 == 0) goto L_0x0var_;
    L_0x0ee7:
        r4 = 0;
        r4 = r48[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r20 = 0;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r0 = r20;
        r4.postNotificationName(r5, r0);
        r60 = r34;
        goto L_0x016b;
    L_0x0var_:
        if (r78 == 0) goto L_0x0f1d;
    L_0x0var_:
        r4 = 0;
        r4 = r48[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r20 = 0;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r0 = r20;
        r4.postNotificationName(r5, r0);
        r63 = r78;
        goto L_0x016b;
    L_0x0f1d:
        if (r42 == 0) goto L_0x0var_;
    L_0x0f1f:
        r4 = 0;
        r4 = r48[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r20 = 0;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r0 = r20;
        r4.postNotificationName(r5, r0);
        r61 = r42;
        goto L_0x016b;
    L_0x0var_:
        r67 = 1;
        goto L_0x016b;
    L_0x0f3d:
        r4 = r80.getAction();
        r5 = "com.tmessages.openplayer";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0f4e;
    L_0x0f4a:
        r69 = 1;
        goto L_0x016b;
    L_0x0f4e:
        r4 = r80.getAction();
        r5 = "org.tmessages.openlocations";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x016b;
    L_0x0f5b:
        r68 = 1;
        goto L_0x016b;
    L_0x0f5f:
        if (r60 == 0) goto L_0x0fc6;
    L_0x0var_:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "chat_id";
        r0 = r31;
        r1 = r60;
        r0.putInt(r4, r1);
        if (r62 == 0) goto L_0x0f7c;
    L_0x0var_:
        r4 = "message_id";
        r0 = r31;
        r1 = r62;
        r0.putInt(r4, r1);
    L_0x0f7c:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0fa5;
    L_0x0var_:
        r4 = 0;
        r4 = r48[r4];
        r5 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = mainFragmentsStack;
        r20 = mainFragmentsStack;
        r20 = r20.size();
        r20 = r20 + -1;
        r0 = r20;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r31;
        r4 = r5.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x01de;
    L_0x0fa5:
        r21 = new org.telegram.ui.ChatActivity;
        r0 = r21;
        r1 = r31;
        r0.<init>(r1);
        r0 = r79;
        r0 = r0.actionBarLayout;
        r20 = r0;
        r22 = 0;
        r23 = 1;
        r24 = 1;
        r25 = 0;
        r4 = r20.presentFragment(r21, r22, r23, r24, r25);
        if (r4 == 0) goto L_0x01de;
    L_0x0fc2:
        r59 = 1;
        goto L_0x01de;
    L_0x0fc6:
        if (r61 == 0) goto L_0x0ff8;
    L_0x0fc8:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "enc_id";
        r0 = r31;
        r1 = r61;
        r0.putInt(r4, r1);
        r21 = new org.telegram.ui.ChatActivity;
        r0 = r21;
        r1 = r31;
        r0.<init>(r1);
        r0 = r79;
        r0 = r0.actionBarLayout;
        r20 = r0;
        r22 = 0;
        r23 = 1;
        r24 = 1;
        r25 = 0;
        r4 = r20.presentFragment(r21, r22, r23, r24, r25);
        if (r4 == 0) goto L_0x01de;
    L_0x0ff4:
        r59 = 1;
        goto L_0x01de;
    L_0x0ff8:
        if (r67 == 0) goto L_0x104e;
    L_0x0ffa:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x100d;
    L_0x1000:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.removeAllFragments();
    L_0x1007:
        r59 = 0;
        r81 = 0;
        goto L_0x01de;
    L_0x100d:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x1007;
    L_0x1019:
        r29 = 0;
    L_0x101b:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r4 = r4 + -1;
        if (r4 <= 0) goto L_0x1045;
    L_0x1029:
        r0 = r79;
        r5 = r0.layersActionBarLayout;
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r20 = 0;
        r0 = r20;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r5.removeFragmentFromStack(r4);
        r29 = r29 + -1;
        r29 = r29 + 1;
        goto L_0x101b;
    L_0x1045:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r5 = 0;
        r4.closeLastFragment(r5);
        goto L_0x1007;
    L_0x104e:
        if (r69 == 0) goto L_0x1079;
    L_0x1050:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x1075;
    L_0x105c:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r5 = 0;
        r21 = r4.get(r5);
        r21 = (org.telegram.ui.ActionBar.BaseFragment) r21;
        r4 = new org.telegram.ui.Components.AudioPlayerAlert;
        r0 = r79;
        r4.<init>(r0);
        r0 = r21;
        r0.showDialog(r4);
    L_0x1075:
        r59 = 0;
        goto L_0x01de;
    L_0x1079:
        if (r68 == 0) goto L_0x10ad;
    L_0x107b:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x10a9;
    L_0x1087:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r5 = 0;
        r21 = r4.get(r5);
        r21 = (org.telegram.ui.ActionBar.BaseFragment) r21;
        r4 = new org.telegram.ui.Components.SharingLocationsAlert;
        r5 = new org.telegram.ui.LaunchActivity$$Lambda$6;
        r0 = r79;
        r1 = r48;
        r5.<init>(r0, r1);
        r0 = r79;
        r4.<init>(r0, r5);
        r0 = r21;
        r0.showDialog(r4);
    L_0x10a9:
        r59 = 0;
        goto L_0x01de;
    L_0x10ad:
        r0 = r79;
        r4 = r0.videoPath;
        if (r4 != 0) goto L_0x10d1;
    L_0x10b3:
        r0 = r79;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x10d1;
    L_0x10b9:
        r0 = r79;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x10d1;
    L_0x10bf:
        r0 = r79;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x10d1;
    L_0x10c5:
        r0 = r79;
        r4 = r0.contactsToSend;
        if (r4 != 0) goto L_0x10d1;
    L_0x10cb:
        r0 = r79;
        r4 = r0.documentsUrisArray;
        if (r4 == 0) goto L_0x129b;
    L_0x10d1:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x10ed;
    L_0x10d7:
        r4 = 0;
        r4 = r48[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r20 = 0;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r0 = r20;
        r4.postNotificationName(r5, r0);
    L_0x10ed:
        r4 = 0;
        r4 = (r38 > r4 ? 1 : (r38 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x127e;
    L_0x10f3:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "onlySelect";
        r5 = 1;
        r0 = r31;
        r0.putBoolean(r4, r5);
        r4 = "dialogsType";
        r5 = 3;
        r0 = r31;
        r0.putInt(r4, r5);
        r4 = "allowSwitchAccount";
        r5 = 1;
        r0 = r31;
        r0.putBoolean(r4, r5);
        r0 = r79;
        r4 = r0.contactsToSend;
        if (r4 == 0) goto L_0x11d9;
    L_0x1119:
        r0 = r79;
        r4 = r0.contactsToSend;
        r4 = r4.size();
        r5 = 1;
        if (r4 == r5) goto L_0x114c;
    L_0x1124:
        r4 = "selectAlertString";
        r5 = "SendContactTo";
        r20 = NUM; // 0x7f0CLASSNAMEf8 float:1.861333E38 double:1.0530984064E-314;
        r0 = r20;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r31;
        r0.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendContactToGroup";
        r20 = NUM; // 0x7f0CLASSNAMEeb float:1.8613303E38 double:1.0530984E-314;
        r0 = r20;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r31;
        r0.putString(r4, r5);
    L_0x114c:
        r21 = new org.telegram.ui.DialogsActivity;
        r0 = r21;
        r1 = r31;
        r0.<init>(r1);
        r0 = r21;
        r1 = r79;
        r0.setDelegate(r1);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1206;
    L_0x1162:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x1203;
    L_0x116e:
        r0 = r79;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r79;
        r5 = r0.layersActionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 + -1;
        r4 = r4.get(r5);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x1203;
    L_0x1188:
        r22 = 1;
    L_0x118a:
        r0 = r79;
        r0 = r0.actionBarLayout;
        r20 = r0;
        r23 = 1;
        r24 = 1;
        r25 = 0;
        r20.presentFragment(r21, r22, r23, r24, r25);
        r59 = 1;
        r4 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r4 == 0) goto L_0x1234;
    L_0x11a1:
        r4 = org.telegram.ui.SecretMediaViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x1234;
    L_0x11ab:
        r4 = org.telegram.ui.SecretMediaViewer.getInstance();
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.closePhoto(r5, r0);
    L_0x11b7:
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1270;
    L_0x11c9:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        goto L_0x01de;
    L_0x11d9:
        r4 = "selectAlertString";
        r5 = "SendMessagesTo";
        r20 = NUM; // 0x7f0CLASSNAMEf8 float:1.861333E38 double:1.0530984064E-314;
        r0 = r20;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r31;
        r0.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendMessagesToGroup";
        r20 = NUM; // 0x7f0CLASSNAMEf9 float:1.8613332E38 double:1.053098407E-314;
        r0 = r20;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r31;
        r0.putString(r4, r5);
        goto L_0x114c;
    L_0x1203:
        r22 = 0;
        goto L_0x118a;
    L_0x1206:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r5 = 1;
        if (r4 <= r5) goto L_0x1231;
    L_0x1213:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r79;
        r5 = r0.actionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 + -1;
        r4 = r4.get(r5);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x1231;
    L_0x122d:
        r22 = 1;
    L_0x122f:
        goto L_0x118a;
    L_0x1231:
        r22 = 0;
        goto L_0x122f;
    L_0x1234:
        r4 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r4 == 0) goto L_0x1252;
    L_0x123a:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x1252;
    L_0x1244:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r5 = 0;
        r20 = 1;
        r0 = r20;
        r4.closePhoto(r5, r0);
        goto L_0x11b7;
    L_0x1252:
        r4 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r4 == 0) goto L_0x11b7;
    L_0x1258:
        r4 = org.telegram.ui.ArticleViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x11b7;
    L_0x1262:
        r4 = org.telegram.ui.ArticleViewer.getInstance();
        r5 = 0;
        r20 = 1;
        r0 = r20;
        r4.close(r5, r0);
        goto L_0x11b7;
    L_0x1270:
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x01de;
    L_0x127e:
        r40 = new java.util.ArrayList;
        r40.<init>();
        r4 = java.lang.Long.valueOf(r38);
        r0 = r40;
        r0.add(r4);
        r4 = 0;
        r5 = 0;
        r20 = 0;
        r0 = r79;
        r1 = r40;
        r2 = r20;
        r0.didSelectDialogs(r4, r1, r5, r2);
        goto L_0x01de;
    L_0x129b:
        if (r53 == 0) goto L_0x12e4;
    L_0x129d:
        r0 = r79;
        r0 = r0.actionBarLayout;
        r23 = r0;
        r24 = new org.telegram.ui.SettingsActivity;
        r24.<init>();
        r25 = 0;
        r26 = 1;
        r27 = 1;
        r28 = 0;
        r23.presentFragment(r24, r25, r26, r27, r28);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x12d7;
    L_0x12b9:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x12d3:
        r59 = 1;
        goto L_0x01de;
    L_0x12d7:
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x12d3;
    L_0x12e4:
        if (r52 == 0) goto L_0x01de;
    L_0x12e6:
        r31 = new android.os.Bundle;
        r31.<init>();
        r4 = "destroyAfterSelect";
        r5 = 1;
        r0 = r31;
        r0.putBoolean(r4, r5);
        r0 = r79;
        r0 = r0.actionBarLayout;
        r23 = r0;
        r24 = new org.telegram.ui.ContactsActivity;
        r0 = r24;
        r1 = r31;
        r0.<init>(r1);
        r25 = 0;
        r26 = 1;
        r27 = 1;
        r28 = 0;
        r23.presentFragment(r24, r25, r26, r27, r28);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1332;
    L_0x1314:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x132e:
        r59 = 1;
        goto L_0x01de;
    L_0x1332:
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x132e;
    L_0x133f:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x021a;
    L_0x134b:
        r37 = new org.telegram.ui.DialogsActivity;
        r4 = 0;
        r0 = r37;
        r0.<init>(r4);
        r0 = r79;
        r4 = r0.sideMenu;
        r0 = r37;
        r0.setSideMenu(r4);
        r0 = r79;
        r4 = r0.actionBarLayout;
        r0 = r37;
        r4.addFragmentToStack(r0);
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x021a;
    L_0x1373:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x021a;
    L_0x137f:
        r0 = r79;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 != 0) goto L_0x13a7;
    L_0x138d:
        r0 = r79;
        r4 = r0.actionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r4.addFragmentToStack(r5);
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x021a;
    L_0x13a7:
        r37 = new org.telegram.ui.DialogsActivity;
        r4 = 0;
        r0 = r37;
        r0.<init>(r4);
        r0 = r79;
        r4 = r0.sideMenu;
        r0 = r37;
        r0.setSideMenu(r4);
        r0 = r79;
        r4 = r0.actionBarLayout;
        r0 = r37;
        r4.addFragmentToStack(r0);
        r0 = r79;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r20 = 0;
        r0 = r20;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x021a;
    L_0x13cf:
        r4 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x13d2:
        r4 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x13d5:
        r4 = move-exception;
        goto L_0x0602;
    L_0x13d8:
        r4 = move-exception;
        r5 = r4;
        goto L_0x0e85;
    L_0x13dc:
        r4 = move-exception;
        goto L_0x09af;
    L_0x13df:
        r4 = move-exception;
        goto L_0x05fd;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    final /* synthetic */ void lambda$handleIntent$5$LaunchActivity(Bundle args) {
        lambda$runLinkRequest$27$LaunchActivity(new CancelAccountDeletionActivity(args));
    }

    final /* synthetic */ void lambda$handleIntent$7$LaunchActivity(int[] intentAccount, SharingLocationInfo info) {
        intentAccount[0] = info.messageObject.currentAccount;
        switchToAccount(intentAccount[0], true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(info.messageObject);
        locationActivity.setDelegate(new LaunchActivity$$Lambda$53(intentAccount, info.messageObject.getDialogId()));
        lambda$runLinkRequest$27$LaunchActivity(locationActivity);
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, HashMap<String, String> auth, String lang, String unsupportedUrl, String code, TL_wallPaper wallPaper, int state) {
        if (state == 0 && UserConfig.getActivatedAccountsCount() >= 2 && auth != null) {
            AlertsCreator.createAccountSelectDialog(this, new LaunchActivity$$Lambda$7(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper)).show();
        } else if (code == null) {
            AlertDialog progressDialog = new AlertDialog(this, 3);
            int[] requestId = new int[]{0};
            TLObject req;
            if (username != null) {
                req = new TL_contacts_resolveUsername();
                req.username = username;
                requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$8(this, progressDialog, game, intentAccount, botChat, botUser, messageId));
            } else if (group != null) {
                if (state == 0) {
                    TLObject req2 = new TL_messages_checkChatInvite();
                    req2.hash = group;
                    requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req2, new LaunchActivity$$Lambda$9(this, progressDialog, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper), 2);
                } else if (state == 1) {
                    req = new TL_messages_importChatInvite();
                    req.hash = group;
                    ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$10(this, intentAccount, progressDialog), 2);
                }
            } else if (sticker != null) {
                if (!mainFragmentsStack.isEmpty()) {
                    TL_inputStickerSetShortName stickerset = new TL_inputStickerSetShortName();
                    stickerset.short_name = sticker;
                    BaseFragment fragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                    fragment.showDialog(new StickersAlert(this, fragment, stickerset, null, null));
                    return;
                }
                return;
            } else if (message != null) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                DialogsActivity fragment2 = new DialogsActivity(args);
                fragment2.setDelegate(new LaunchActivity$$Lambda$11(this, hasUrl, intentAccount, message));
                presentFragment(fragment2, false, true);
            } else if (auth != null) {
                int bot_id = Utilities.parseInt((String) auth.get("bot_id")).intValue();
                if (bot_id != 0) {
                    String payload = (String) auth.get("payload");
                    String nonce = (String) auth.get("nonce");
                    String callbackUrl = (String) auth.get("callback_url");
                    req = new TL_account_getAuthorizationForm();
                    req.bot_id = bot_id;
                    req.scope = (String) auth.get("scope");
                    req.public_key = (String) auth.get("public_key");
                    requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new LaunchActivity$$Lambda$12(this, requestId, intentAccount, progressDialog, req, payload, nonce, callbackUrl));
                } else {
                    return;
                }
            } else if (unsupportedUrl != null) {
                req = new TL_help_getDeepLinkInfo();
                req.path = unsupportedUrl;
                requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$13(this, progressDialog));
            } else if (lang != null) {
                req = new TL_langpack_getLanguage();
                req.lang_code = lang;
                req.lang_pack = "android";
                requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$14(this, progressDialog));
            } else if (wallPaper != null) {
                boolean ok = false;
                if (TextUtils.isEmpty(wallPaper.slug)) {
                    try {
                        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$15(this, new WallpaperActivity(new ColorWallpaper(-100, wallPaper.settings.background_color), null)));
                        ok = true;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (!ok) {
                    req = new TL_account_getWallPaper();
                    InputWallPaper inputWallPaperSlug = new TL_inputWallPaperSlug();
                    inputWallPaperSlug.slug = wallPaper.slug;
                    req.wallpaper = inputWallPaperSlug;
                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$16(this, progressDialog, wallPaper));
                }
            }
            if (requestId[0] != 0) {
                progressDialog.setOnCancelListener(new LaunchActivity$$Lambda$17(intentAccount, requestId));
                try {
                    progressDialog.show();
                } catch (Exception e2) {
                }
            }
        } else if (NotificationCenter.getGlobalInstance().hasObservers(NotificationCenter.didReceiveSmsCode)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, code);
        } else {
            Builder builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder = builder;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("OtherLoginCode", R.string.OtherLoginCode, code)));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showAlertDialog(builder);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$8$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TL_wallPaper wallPaper, int account) {
        if (account != intentAccount) {
            switchToAccount(account, true);
        }
        runLinkRequest(account, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper, 1);
    }

    final /* synthetic */ void lambda$runLinkRequest$12$LaunchActivity(AlertDialog progressDialog, String game, int intentAccount, String botChat, String botUser, Integer messageId, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$50(this, progressDialog, response, error, game, intentAccount, botChat, botUser, messageId));
    }

    final /* synthetic */ void lambda$null$11$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error, String game, int intentAccount, String botChat, String botUser, Integer messageId) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
            if (error != null || this.actionBarLayout == null || (game != null && (game == null || res.users.isEmpty()))) {
                try {
                    Toast.makeText(this, LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                    return;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                    return;
                }
            }
            MessagesController.getInstance(intentAccount).putUsers(res.users, false);
            MessagesController.getInstance(intentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(intentAccount).putUsersAndChats(res.users, res.chats, false, true);
            Bundle args;
            DialogsActivity fragment;
            if (game != null) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putBoolean("cantSendToChannels", true);
                args.putInt("dialogsType", 1);
                args.putString("selectAlertString", LocaleController.getString("SendGameTo", R.string.SendGameTo));
                args.putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", R.string.SendGameToGroup));
                fragment = new DialogsActivity(args);
                fragment.setDelegate(new LaunchActivity$$Lambda$51(this, game, intentAccount, res));
                boolean removeLast = AndroidUtilities.isTablet() ? this.layersActionBarLayout.fragmentsStack.size() > 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : this.actionBarLayout.fragmentsStack.size() > 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                this.actionBarLayout.presentFragment(fragment, removeLast, true, true, false);
                if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                    SecretMediaViewer.getInstance().closePhoto(false, false);
                } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                    PhotoViewer.getInstance().closePhoto(false, true);
                } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                    ArticleViewer.getInstance().close(false, true);
                }
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                if (AndroidUtilities.isTablet()) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                    return;
                }
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            } else if (botChat != null) {
                User user = !res.users.isEmpty() ? (User) res.users.get(0) : null;
                if (user == null || (user.bot && user.bot_nochats)) {
                    try {
                        Toast.makeText(this, LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                        return;
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                        return;
                    }
                }
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 2);
                args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                fragment = new DialogsActivity(args);
                fragment.setDelegate(new LaunchActivity$$Lambda$52(this, intentAccount, user, botChat));
                lambda$runLinkRequest$27$LaunchActivity(fragment);
            } else {
                boolean isBot = false;
                args = new Bundle();
                long dialog_id;
                if (res.chats.isEmpty()) {
                    args.putInt("user_id", ((User) res.users.get(0)).id);
                    dialog_id = (long) ((User) res.users.get(0)).id;
                } else {
                    args.putInt("chat_id", ((Chat) res.chats.get(0)).id);
                    dialog_id = (long) (-((Chat) res.chats.get(0)).id);
                }
                if (botUser != null && res.users.size() > 0 && ((User) res.users.get(0)).bot) {
                    args.putString("botUser", botUser);
                    isBot = true;
                }
                if (messageId != null) {
                    args.putInt("message_id", messageId.intValue());
                }
                BaseFragment lastFragment = !mainFragmentsStack.isEmpty() ? (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1) : null;
                if (lastFragment != null && !MessagesController.getInstance(intentAccount).checkCanOpenChat(args, lastFragment)) {
                    return;
                }
                if (isBot && (lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == dialog_id) {
                    ((ChatActivity) lastFragment).setBotUser(botUser);
                } else {
                    this.actionBarLayout.presentFragment(new ChatActivity(args));
                }
            }
        }
    }

    final /* synthetic */ void lambda$null$9$LaunchActivity(String game, int intentAccount, TL_contacts_resolvedPeer res, DialogsActivity fragment1, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        TL_inputMediaGame inputMediaGame = new TL_inputMediaGame();
        inputMediaGame.id = new TL_inputGameShortName();
        inputMediaGame.id.short_name = game;
        inputMediaGame.id.bot_id = MessagesController.getInstance(intentAccount).getInputUser((User) res.users.get(0));
        SendMessagesHelper.getInstance(intentAccount).sendGame(MessagesController.getInstance(intentAccount).getInputPeer((int) did), inputMediaGame, 0, 0);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args1.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args1.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args1.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args1.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args1, fragment1)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(args1), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$null$10$LaunchActivity(int intentAccount, User user, String botChat, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putInt("chat_id", -((int) did));
        if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount).checkCanOpenChat(args12, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            MessagesController.getInstance(intentAccount).addUserToChat(-((int) did), user, null, 0, botChat, null, null);
            this.actionBarLayout.presentFragment(new ChatActivity(args12), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$15$LaunchActivity(AlertDialog progressDialog, int intentAccount, String group, String username, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TL_wallPaper wallPaper, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$48(this, progressDialog, error, response, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper));
    }

    final /* synthetic */ void lambda$null$14$LaunchActivity(AlertDialog progressDialog, TL_error error, TLObject response, int intentAccount, String group, String username, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TL_wallPaper wallPaper) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            Builder builder;
            if (error != null || this.actionBarLayout == null) {
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showAlertDialog(builder);
                return;
            }
            ChatInvite invite = (ChatInvite) response;
            if (invite.chat != null && (!ChatObject.isLeftFromChat(invite.chat) || (!invite.chat.kicked && !TextUtils.isEmpty(invite.chat.username)))) {
                MessagesController.getInstance(intentAccount).putChat(invite.chat, false);
                ArrayList<Chat> chats = new ArrayList();
                chats.add(invite.chat);
                MessagesStorage.getInstance(intentAccount).putUsersAndChats(null, chats, false, true);
                Bundle args = new Bundle();
                args.putInt("chat_id", invite.chat.id);
                if (!mainFragmentsStack.isEmpty()) {
                    if (!MessagesController.getInstance(intentAccount).checkCanOpenChat(args, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        return;
                    }
                }
                ChatActivity fragment = new ChatActivity(args);
                NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.actionBarLayout.presentFragment(fragment, false, true, true, false);
            } else if (((invite.chat != null || (invite.channel && !invite.megagroup)) && (invite.chat == null || (ChatObject.isChannel(invite.chat) && !invite.chat.megagroup))) || mainFragmentsStack.isEmpty()) {
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                String str = "ChannelJoinTo";
                Object[] objArr = new Object[1];
                objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                builder.setMessage(LocaleController.formatString(str, R.string.ChannelJoinTo, objArr));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$49(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showAlertDialog(builder);
            } else {
                BaseFragment fragment2 = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                fragment2.showDialog(new JoinGroupAlert(this, invite, group, fragment2));
            }
        }
    }

    final /* synthetic */ void lambda$null$13$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TL_wallPaper wallPaper, DialogInterface dialogInterface, int i) {
        runLinkRequest(intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, game, auth, lang, unsupportedUrl, code, wallPaper, 1);
    }

    final /* synthetic */ void lambda$runLinkRequest$17$LaunchActivity(int intentAccount, AlertDialog progressDialog, TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(intentAccount).processUpdates((Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$47(this, progressDialog, error, response, intentAccount));
    }

    final /* synthetic */ void lambda$null$16$LaunchActivity(AlertDialog progressDialog, TL_error error, TLObject response, int intentAccount) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (error != null) {
                Builder builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                Updates updates = (Updates) response;
                if (!updates.chats.isEmpty()) {
                    Chat chat = (Chat) updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(intentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(intentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount).checkCanOpenChat(args, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        ChatActivity fragment = new ChatActivity(args);
                        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        this.actionBarLayout.presentFragment(fragment, false, true, true, false);
                    }
                }
            }
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(boolean hasUrl, int intentAccount, String message, DialogsActivity fragment13, ArrayList dids, CharSequence m, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args13 = new Bundle();
        args13.putBoolean("scrollToTopOnResume", true);
        args13.putBoolean("hasUrl", hasUrl);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args13.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args13.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args13.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args13.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args13, fragment13)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            DataQuery.getInstance(intentAccount).saveDraft(did, message, null, null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(args13), true, false, true, false);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$22$LaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response, TL_error error) {
        TL_account_authorizationForm authorizationForm = (TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TL_account_getPassword(), new LaunchActivity$$Lambda$44(this, progressDialog, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
            return;
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$45(this, progressDialog, error));
    }

    final /* synthetic */ void lambda$null$20$LaunchActivity(AlertDialog progressDialog, int intentAccount, TL_account_authorizationForm authorizationForm, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response1, TL_error error1) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$46(this, progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce, callbackUrl));
    }

    final /* synthetic */ void lambda$null$19$LaunchActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TL_account_authorizationForm authorizationForm, TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (response1 != null) {
            TL_account_password accountPassword = (TL_account_password) response1;
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            lambda$runLinkRequest$27$LaunchActivity(new PassportActivity(5, req.bot_id, req.scope, req.public_key, payload, nonce, callbackUrl, authorizationForm, accountPassword));
        }
    }

    final /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog progressDialog, TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$24$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$43(this, progressDialog, response));
    }

    final /* synthetic */ void lambda$null$23$LaunchActivity(AlertDialog progressDialog, TLObject response) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (response instanceof TL_help_deepLinkInfo) {
            TL_help_deepLinkInfo res = (TL_help_deepLinkInfo) response;
            AlertsCreator.showUpdateAppAlert(this, res.message, res.update_app);
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$26$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$42(this, progressDialog, response, error));
    }

    final /* synthetic */ void lambda$null$25$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (response instanceof TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TL_langPackLanguage) response));
        } else if (error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", R.string.LanguageUnsupportedError)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
            }
        }
    }

    final /* synthetic */ void lambda$runLinkRequest$29$LaunchActivity(AlertDialog progressDialog, TL_wallPaper wallPaper, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$41(this, progressDialog, response, wallPaper, error));
    }

    final /* synthetic */ void lambda$null$28$LaunchActivity(AlertDialog progressDialog, TLObject response, TL_wallPaper wallPaper, TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (response instanceof TL_wallPaper) {
            Object object;
            TL_wallPaper res = (TL_wallPaper) response;
            if (wallPaper.settings.background_color != 0) {
                ColorWallpaper colorWallpaper = new ColorWallpaper(-1, wallPaper.settings.background_color, res.id, ((float) wallPaper.settings.intensity) / 100.0f, wallPaper.settings.motion, null);
                colorWallpaper.pattern = res;
                object = colorWallpaper;
            } else {
                TL_wallPaper object2 = res;
            }
            WallpaperActivity wallpaperActivity = new WallpaperActivity(object2, null);
            wallpaperActivity.setInitialModes(wallPaper.settings.blur, wallPaper.settings.motion);
            lambda$runLinkRequest$27$LaunchActivity(wallpaperActivity);
            return;
        }
        showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
    }

    public void checkAppUpdate(boolean force) {
        if (!force && BuildVars.DEBUG_VERSION) {
            return;
        }
        if (!force && !BuildVars.CHECK_UPDATES) {
            return;
        }
        if (force || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(0).lastUpdateCheckTime) >= 86400000) {
            TL_help_getAppUpdate req = new TL_help_getAppUpdate();
            try {
                req.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception e) {
            }
            if (req.source == null) {
                req.source = "";
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$18(this, this.currentAccount));
        }
    }

    final /* synthetic */ void lambda$checkAppUpdate$32$LaunchActivity(int accountNum, TLObject response, TL_error error) {
        UserConfig.getInstance(0).lastUpdateCheckTime = System.currentTimeMillis();
        UserConfig.getInstance(0).saveConfig(false);
        if (response instanceof TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$40(this, (TL_help_appUpdate) response, accountNum));
        }
    }

    final /* synthetic */ void lambda$null$31$LaunchActivity(TL_help_appUpdate res, int accountNum) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            res.popup = Utilities.random.nextBoolean();
        }
        if (res.popup) {
            UserConfig.getInstance(0).pendingAppUpdate = res;
            UserConfig.getInstance(0).pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Throwable e) {
                FileLog.e(e);
                UserConfig.getInstance(0).pendingAppUpdateInstallTime = 0;
            }
            UserConfig.getInstance(0).saveConfig(false);
            showUpdateActivity(accountNum, res);
            return;
        }
        new UpdateAppAlertDialog(this, res, accountNum).show();
    }

    public AlertDialog showAlertDialog(Builder builder) {
        AlertDialog alertDialog = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (LaunchActivity.this.visibleDialog != null) {
                        if (LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                            try {
                                Toast.makeText(LaunchActivity.this, LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater), 1).show();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            LaunchActivity.this.localeDialog = null;
                        } else if (LaunchActivity.this.visibleDialog == LaunchActivity.this.proxyErrorDialog) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            Editor editor = MessagesController.getGlobalMainSettings().edit();
                            editor.putBoolean("proxy_enabled", false);
                            editor.putBoolean("proxy_enabled_calls", false);
                            editor.commit();
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
        } catch (Throwable e2) {
            FileLog.e(e2);
            return alertDialog;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        Bundle args = new Bundle();
        int account = dialogsFragment != null ? dialogsFragment.getCurrentAccount() : this.currentAccount;
        args.putBoolean("scrollToTopOnResume", true);
        if (!AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        if (lower_part == 0) {
            args.putInt("enc_id", high_id);
        } else if (high_id == 1) {
            args.putInt("chat_id", lower_part);
        } else if (lower_part > 0) {
            args.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(account).checkCanOpenChat(args, dialogsFragment)) {
            BaseFragment fragment = new ChatActivity(args);
            if (this.contactsToSend == null || this.contactsToSend.size() != 1) {
                boolean z;
                ActionBarLayout actionBarLayout = this.actionBarLayout;
                boolean z2 = dialogsFragment != null;
                if (dialogsFragment == null) {
                    z = true;
                } else {
                    z = false;
                }
                actionBarLayout.presentFragment(fragment, z2, z, true, false);
                if (this.videoPath != null) {
                    fragment.openVideoEditor(this.videoPath, this.sendingText);
                    this.sendingText = null;
                }
                if (this.photoPathsArray != null) {
                    if (this.sendingText != null && this.sendingText.length() <= 1024 && this.photoPathsArray.size() == 1) {
                        ((SendingMediaInfo) this.photoPathsArray.get(0)).caption = this.sendingText;
                        this.sendingText = null;
                    }
                    SendMessagesHelper.prepareSendingMedia(this.photoPathsArray, did, null, null, false, false, null);
                }
                if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                    String caption = null;
                    if (this.sendingText != null && this.sendingText.length() <= 1024) {
                        if ((this.documentsPathsArray != null ? this.documentsPathsArray.size() : 0) + (this.documentsUrisArray != null ? this.documentsUrisArray.size() : 0) == 1) {
                            caption = this.sendingText;
                            this.sendingText = null;
                        }
                    }
                    SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, caption, this.documentsMimeType, did, null, null, null);
                }
                if (this.sendingText != null) {
                    SendMessagesHelper.prepareSendingText(this.sendingText, did);
                }
                if (!(this.contactsToSend == null || this.contactsToSend.isEmpty())) {
                    for (int a = 0; a < this.contactsToSend.size(); a++) {
                        SendMessagesHelper.getInstance(account).sendMessage((User) this.contactsToSend.get(a), did, null, null, null);
                    }
                }
            } else if (this.contactsToSend.size() == 1) {
                boolean z3;
                boolean z4;
                PhonebookShareActivity contactFragment = new PhonebookShareActivity(null, this.contactsToSendUri, null, null);
                contactFragment.setDelegate(new LaunchActivity$$Lambda$19(this, fragment, account, did));
                ActionBarLayout actionBarLayout2 = this.actionBarLayout;
                if (dialogsFragment != null) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (dialogsFragment == null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                actionBarLayout2.presentFragment(contactFragment, z3, z4, true, false);
            }
            this.photoPathsArray = null;
            this.videoPath = null;
            this.sendingText = null;
            this.documentsPathsArray = null;
            this.documentsOriginalPathsArray = null;
            this.contactsToSend = null;
            this.contactsToSendUri = null;
        }
    }

    final /* synthetic */ void lambda$didSelectDialogs$33$LaunchActivity(ChatActivity fragment, int account, long did, User user) {
        this.actionBarLayout.presentFragment(fragment, true, false, true, false);
        SendMessagesHelper.getInstance(account).sendMessage(user, did, null, null, null);
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            if (this.lockRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
                this.lockRunnable = null;
            }
            if (this.currentAccount != -1) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
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
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$27$LaunchActivity(BaseFragment fragment) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onActivityResult(requestCode, resultCode, data);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 || requestCode == 4 || requestCode == 5 || requestCode == 19 || requestCode == 20 || requestCode == 22) {
            boolean showAlert = true;
            if (grantResults.length > 0 && grantResults[0] == 0) {
                if (requestCode == 4) {
                    ImageLoader.getInstance().checkMediaPaths();
                    return;
                } else if (requestCode == 5) {
                    ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    return;
                } else if (requestCode == 3) {
                    if (SharedConfig.inappCamera) {
                        CameraController.getInstance().initCamera(null);
                        return;
                    }
                    return;
                } else if (requestCode == 19 || requestCode == 20 || requestCode == 22) {
                    showAlert = false;
                }
            }
            if (showAlert) {
                Builder builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (requestCode == 3) {
                    builder.setMessage(LocaleController.getString("PermissionNoAudio", R.string.PermissionNoAudio));
                } else if (requestCode == 4) {
                    builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                } else if (requestCode == 5) {
                    builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                } else if (requestCode == 19 || requestCode == 20 || requestCode == 22) {
                    builder.setMessage(LocaleController.getString("PermissionNoCamera", R.string.PermissionNoCamera));
                }
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new LaunchActivity$$Lambda$20(this));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.show();
                return;
            }
        } else if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == 0) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
        }
    }

    final /* synthetic */ void lambda$onRequestPermissionsResult$34$LaunchActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    protected void onPause() {
        super.onPause();
        SharedConfig.lastAppPauseTime = System.currentTimeMillis();
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(LaunchActivity$$Lambda$21.$instance);
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        if (this.passcodeView != null) {
            this.passcodeView.onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        AndroidUtilities.unregisterUpdates();
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
    }

    static final /* synthetic */ void lambda$onPause$35$LaunchActivity() {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
    }

    protected void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    protected void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    protected void onDestroy() {
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
        if (StickerPreviewViewer.hasInstance()) {
            StickerPreviewViewer.getInstance().destroy();
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
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(LaunchActivity$$Lambda$22.$instance);
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
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                MediaController.getInstance().seekToProgress(messageObject, messageObject.audioProgress);
            }
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            showTosActivity(UserConfig.selectedAccount, UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService);
        } else if (UserConfig.getInstance(0).pendingAppUpdate != null) {
            showUpdateActivity(UserConfig.selectedAccount, UserConfig.getInstance(0).pendingAppUpdate);
        }
        checkAppUpdate(false);
    }

    static final /* synthetic */ void lambda$onResume$36$LaunchActivity() {
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
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        checkLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        Builder builder;
        View child;
        if (id == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
        } else if (id == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != this) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("switch to state " + state);
                }
                this.currentConnectionState = state;
                updateCurrentConnectionState(account);
            }
        } else if (id == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            if (reason.intValue() == 3 && this.proxyErrorDialog != null) {
                return;
            }
            if (reason.intValue() == 4) {
                showTosActivity(account, (TL_help_termsOfService) args[1]);
                return;
            }
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (!(reason.intValue() == 2 || reason.intValue() == 3)) {
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new LaunchActivity$$Lambda$23(account));
            }
            if (reason.intValue() == 5) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam3", R.string.NobodyLikesSpam3));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 0) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam1", R.string.NobodyLikesSpam1));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 1) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            } else if (reason.intValue() == 2) {
                builder.setMessage((String) args[1]);
                if (args[2].startsWith("AUTH_KEY_DROP_")) {
                    builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setNegativeButton(LocaleController.getString("LogOut", R.string.LogOut), new LaunchActivity$$Lambda$24(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                }
            } else if (reason.intValue() == 3) {
                builder.setMessage(LocaleController.getString("UseProxyTelegramError", R.string.UseProxyTelegramError));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                this.proxyErrorDialog = showAlertDialog(builder);
                return;
            }
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.wasUnableToFindCurrentLocation) {
            HashMap<String, MessageObject> waitingForLocation = args[0];
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", R.string.ShareYouLocationUnableManually), new LaunchActivity$$Lambda$25(this, waitingForLocation, account));
            builder.setMessage(LocaleController.getString("ShareYouLocationUnable", R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.sideMenu != null) {
                child = this.sideMenu.getChildAt(0);
                if (child != null) {
                    child.invalidate();
                }
            }
        } else if (id == NotificationCenter.didSetPasscode) {
            if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
                try {
                    getWindow().setFlags(8192, 8192);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            } else if (!MediaController.getInstance().hasFlagSecureFragment()) {
                try {
                    getWindow().clearFlags(8192);
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        } else if (id == NotificationCenter.reloadInterface) {
            boolean last = mainFragmentsStack.size() > 1 && (mainFragmentsStack.get(mainFragmentsStack.size() - 1) instanceof SettingsActivity);
            rebuildAllFragments(last);
        } else if (id == NotificationCenter.suggestedLangpack) {
            showLanguageAlert(false);
        } else if (id == NotificationCenter.openArticle) {
            if (!mainFragmentsStack.isEmpty()) {
                ArticleViewer.getInstance().setParentActivity(this, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1));
                ArticleViewer.getInstance().open((TL_webPage) args[0], (String) args[1]);
            }
        } else if (id == NotificationCenter.hasNewContactsToImport) {
            if (this.actionBarLayout != null && !this.actionBarLayout.fragmentsStack.isEmpty()) {
                int type = ((Integer) args[0]).intValue();
                HashMap<String, Contact> contactHashMap = args[1];
                boolean first = ((Boolean) args[2]).booleanValue();
                boolean schedule = ((Boolean) args[3]).booleanValue();
                BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("UpdateContactsTitle", R.string.UpdateContactsTitle));
                builder.setMessage(LocaleController.getString("UpdateContactsMessage", R.string.UpdateContactsMessage));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$26(account, contactHashMap, first, schedule));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new LaunchActivity$$Lambda$27(account, contactHashMap, first, schedule));
                builder.setOnBackButtonListener(new LaunchActivity$$Lambda$28(account, contactHashMap, first, schedule));
                AlertDialog dialog = builder.create();
                fragment.showDialog(dialog);
                dialog.setCanceledOnTouchOutside(false);
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            if (!args[0].booleanValue()) {
                if (this.sideMenu != null) {
                    this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
                    this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
                    this.sideMenu.getAdapter().notifyDataSetChanged();
                }
                if (VERSION.SDK_INT >= 21) {
                    try {
                        setTaskDescription(new TaskDescription(null, null, Theme.getColor("actionBarDefault") | -16777216));
                    } catch (Exception e3) {
                    }
                }
            }
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            ThemeInfo theme = args[0];
            boolean nigthTheme = ((Boolean) args[1]).booleanValue();
            this.actionBarLayout.animateThemedValues(theme, nigthTheme);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.animateThemedValues(theme, nigthTheme);
                this.rightActionBarLayout.animateThemedValues(theme, nigthTheme);
            }
        } else if (id == NotificationCenter.notificationsCountUpdated && this.sideMenu != null) {
            Integer accountNum = args[0];
            int count = this.sideMenu.getChildCount();
            for (int a = 0; a < count; a++) {
                child = this.sideMenu.getChildAt(a);
                if ((child instanceof DrawerUserCell) && ((DrawerUserCell) child).getAccountNumber() == accountNum.intValue()) {
                    child.invalidate();
                    return;
                }
            }
        }
    }

    static final /* synthetic */ void lambda$didReceivedNotification$37$LaunchActivity(int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController.getInstance(account).openByUserName("spambot", (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1), 1);
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$38$LaunchActivity(DialogInterface dialog, int which) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    final /* synthetic */ void lambda$didReceivedNotification$40$LaunchActivity(HashMap waitingForLocation, int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            LocationActivity fragment = new LocationActivity(0);
            fragment.setDelegate(new LaunchActivity$$Lambda$39(waitingForLocation, account));
            lambda$runLinkRequest$27$LaunchActivity(fragment);
        }
    }

    static final /* synthetic */ void lambda$null$39$LaunchActivity(HashMap waitingForLocation, int account, MessageMedia location, int live) {
        for (Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            SendMessagesHelper.getInstance(account).sendMessage(location, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    private String getStringForLanguageAlert(HashMap<String, String> map, String key, int intKey) {
        String value = (String) map.get(key);
        if (value == null) {
            return LocaleController.getString(key, intKey);
        }
        return value;
    }

    private void checkFreeDiscSpace() {
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new LaunchActivity$$Lambda$29(this), 2000);
        }
    }

    final /* synthetic */ void lambda$checkFreeDiscSpace$45$LaunchActivity() {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs(preferences.getLong("last_space_check", 0) - System.currentTimeMillis()) >= NUM) {
                    File path = FileLoader.getDirectory(4);
                    if (path != null) {
                        long freeSpace;
                        StatFs statFs = new StatFs(path.getAbsolutePath());
                        if (VERSION.SDK_INT < 18) {
                            freeSpace = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                        } else {
                            freeSpace = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                        }
                        if (freeSpace < NUM) {
                            preferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                            AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$38(this));
                        }
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    final /* synthetic */ void lambda$null$44$LaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable th) {
        }
    }

    private void showLanguageAlertInternal(LocaleInfo systemInfo, LocaleInfo englishInfo, String systemLang) {
        try {
            LocaleInfo localeInfo;
            this.loadingLocaleDialog = false;
            boolean firstSystem = systemInfo.builtIn || LocaleController.getInstance().isCurrentLocalLocale();
            Builder builder = new Builder((Context) this);
            builder.setTitle(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            builder.setSubtitle(getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            LanguageCell[] cells = new LanguageCell[2];
            LocaleInfo[] selectedLanguage = new LocaleInfo[1];
            LocaleInfo[] locales = new LocaleInfo[2];
            String englishName = getStringForLanguageAlert(this.systemLocaleStrings, "English", R.string.English);
            if (firstSystem) {
                localeInfo = systemInfo;
            } else {
                localeInfo = englishInfo;
            }
            locales[0] = localeInfo;
            if (firstSystem) {
                localeInfo = englishInfo;
            } else {
                localeInfo = systemInfo;
            }
            locales[1] = localeInfo;
            if (!firstSystem) {
                systemInfo = englishInfo;
            }
            selectedLanguage[0] = systemInfo;
            int a = 0;
            while (a < 2) {
                cells[a] = new LanguageCell(this, true);
                cells[a].setLanguage(locales[a], locales[a] == englishInfo ? englishName : null, true);
                cells[a].setTag(Integer.valueOf(a));
                cells[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
                cells[a].setLanguageSelected(a == 0);
                linearLayout.addView(cells[a], LayoutHelper.createLinear(-1, 50));
                cells[a].setOnClickListener(new LaunchActivity$$Lambda$30(selectedLanguage, cells));
                a++;
            }
            LanguageCell cell = new LanguageCell(this, true);
            cell.setValue(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
            cell.setOnClickListener(new LaunchActivity$$Lambda$31(this));
            linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
            builder.setView(linearLayout);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), new LaunchActivity$$Lambda$32(this, selectedLanguage));
            this.localeDialog = showAlertDialog(builder);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang).commit();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ void lambda$showLanguageAlertInternal$46$LaunchActivity(LocaleInfo[] selectedLanguage, LanguageCell[] cells, View v) {
        Integer tag = (Integer) v.getTag();
        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
        for (int a1 = 0; a1 < cells.length; a1++) {
            boolean z;
            LanguageCell languageCell = cells[a1];
            if (a1 == tag.intValue()) {
                z = true;
            } else {
                z = false;
            }
            languageCell.setLanguageSelected(z);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlertInternal$47$LaunchActivity(View v) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$27$LaunchActivity(new LanguageSelectActivity());
        if (this.visibleDialog != null) {
            this.visibleDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    final /* synthetic */ void lambda$showLanguageAlertInternal$48$LaunchActivity(LocaleInfo[] selectedLanguage, DialogInterface dialog, int which) {
        LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    private void showLanguageAlert(boolean force) {
        try {
            if (!this.loadingLocaleDialog && !ApplicationLoader.mainInterfacePaused) {
                String showedLang = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
                String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                if (force || !showedLang.equals(systemLang)) {
                    String arg;
                    LocaleInfo[] infos = new LocaleInfo[2];
                    if (systemLang.contains("-")) {
                        arg = systemLang.split("-")[0];
                    } else {
                        arg = systemLang;
                    }
                    String alias;
                    if ("in".equals(arg)) {
                        alias = "id";
                    } else if ("iw".equals(arg)) {
                        alias = "he";
                    } else if ("jw".equals(arg)) {
                        alias = "jv";
                    } else {
                        alias = null;
                    }
                    for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
                        LocaleInfo info = (LocaleInfo) LocaleController.getInstance().languages.get(a);
                        if (info.shortName.equals("en")) {
                            infos[0] = info;
                        }
                        if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || info.shortName.equals(alias)) {
                            infos[1] = info;
                        }
                        if (infos[0] != null && infos[1] != null) {
                            break;
                        }
                    }
                    if (infos[0] != null && infos[1] != null && infos[0] != infos[1]) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("show lang alert for " + infos[0].getKey() + " and " + infos[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TL_langpack_getStrings req = new TL_langpack_getStrings();
                        req.lang_code = infos[1].getLangCode();
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$33(this, infos, systemLang), 8);
                        req = new TL_langpack_getStrings();
                        req.lang_code = infos[0].getLangCode();
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LaunchActivity$$Lambda$34(this, infos, systemLang), 8);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("alert already showed for " + showedLang);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlert$50$LaunchActivity(LocaleInfo[] infos, String systemLang, TLObject response, TL_error error) {
        HashMap<String, String> keys = new HashMap();
        if (response != null) {
            Vector vector = (Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                LangPackString string = (LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$37(this, keys, infos, systemLang));
    }

    final /* synthetic */ void lambda$null$49$LaunchActivity(HashMap keys, LocaleInfo[] infos, String systemLang) {
        this.systemLocaleStrings = keys;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    final /* synthetic */ void lambda$showLanguageAlert$52$LaunchActivity(LocaleInfo[] infos, String systemLang, TLObject response, TL_error error) {
        HashMap<String, String> keys = new HashMap();
        if (response != null) {
            Vector vector = (Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                LangPackString string = (LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new LaunchActivity$$Lambda$36(this, keys, infos, systemLang));
    }

    final /* synthetic */ void lambda$null$51$LaunchActivity(HashMap keys, LocaleInfo[] infos, String systemLang) {
        this.englishLocaleStrings = keys;
        if (this.englishLocaleStrings != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
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
            } else if (SharedConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) SharedConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
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

    private void updateCurrentConnectionState(int account) {
        if (this.actionBarLayout != null) {
            String title = null;
            int titleId = 0;
            Runnable action = null;
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (this.currentConnectionState == 2) {
                title = "WaitingForNetwork";
                titleId = R.string.WaitingForNetwork;
            } else if (this.currentConnectionState == 5) {
                title = "Updating";
                titleId = R.string.Updating;
            } else if (this.currentConnectionState == 4) {
                title = "ConnectingToProxy";
                titleId = R.string.ConnectingToProxy;
            } else if (this.currentConnectionState == 1) {
                title = "Connecting";
                titleId = R.string.Connecting;
            }
            if (this.currentConnectionState == 1 || this.currentConnectionState == 4) {
                action = new LaunchActivity$$Lambda$35(this);
            }
            this.actionBarLayout.setTitleOverlayText(title, titleId, action);
        }
    }

    final /* synthetic */ void lambda$updateCurrentConnectionState$53$LaunchActivity() {
        BaseFragment lastFragment = null;
        if (AndroidUtilities.isTablet()) {
            if (!layerFragmentsStack.isEmpty()) {
                lastFragment = (BaseFragment) layerFragmentsStack.get(layerFragmentsStack.size() - 1);
            }
        } else if (!mainFragmentsStack.isEmpty()) {
            lastFragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
        }
        if (!(lastFragment instanceof ProxyListActivity) && !(lastFragment instanceof ProxySettingsActivity)) {
            lambda$runLinkRequest$27$LaunchActivity(new ProxyListActivity());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BaseFragment lastFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if ((lastFragment instanceof ChatActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat");
                } else if (lastFragment instanceof SettingsActivity) {
                    outState.putString("fragment", "settings");
                } else if ((lastFragment instanceof GroupCreateFinalActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                } else if (lastFragment instanceof WallpapersListActivity) {
                    outState.putString("fragment", "wallpapers");
                } else if ((lastFragment instanceof ProfileActivity) && ((ProfileActivity) lastFragment).isChat() && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat_profile");
                } else if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "channel");
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
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
                cancel = !((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() + -1)).onBackPressed();
            }
            if (!cancel) {
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

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        try {
            Menu menu = mode.getMenu();
            if (!(menu == null || this.actionBarLayout.extendActionMode(menu) || !AndroidUtilities.isTablet() || this.rightActionBarLayout.extendActionMode(menu))) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(mode);
                this.layersActionBarLayout.onActionModeStarted(mode);
            }
        }
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
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
        if (!mainFragmentsStack.isEmpty() && (!(PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) && event.getRepeatCount() == 0 && event.getAction() == 0 && (event.getKeyCode() == 24 || event.getKeyCode() == 25))) {
            BaseFragment fragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
            if ((fragment instanceof ChatActivity) && ((ChatActivity) fragment).maybePlayVisibleVideo()) {
                return true;
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
            } else if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
            } else {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if ((fragment instanceof DialogsActivity) && ((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
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
            } else if (fragment instanceof ChatActivity) {
                int a;
                if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                    boolean result = (this.tabletFullSize && layout == this.actionBarLayout && this.actionBarLayout.fragmentsStack.size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    if (result) {
                        return result;
                    }
                    this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false, false);
                    return result;
                } else if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                } else if (!this.tabletFullSize || layout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    this.actionBarLayout.presentFragment(fragment, this.actionBarLayout.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    return false;
                } else {
                    this.actionBarLayout.presentFragment(fragment, this.actionBarLayout.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                    }
                    return false;
                }
            } else if (layout == this.layersActionBarLayout) {
                return true;
            } else {
                this.layersActionBarLayout.setVisibility(0);
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
        }
        boolean allow = true;
        if (fragment instanceof LoginActivity) {
            if (mainFragmentsStack.size() == 0) {
                allow = false;
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            allow = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if (fragment instanceof DialogsActivity) {
                if (((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(fragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (this.tabletFullSize) {
                        return false;
                    }
                    this.shadowTabletSide.setVisibility(0);
                    if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    this.backgroundTablet.setVisibility(0);
                    return false;
                }
            } else if (fragment instanceof ChatActivity) {
                int a;
                if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                } else if (this.tabletFullSize && layout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                }
            } else if (layout != this.layersActionBarLayout) {
                this.layersActionBarLayout.setVisibility(0);
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
            return true;
        }
        boolean allow = true;
        if (fragment instanceof LoginActivity) {
            if (mainFragmentsStack.size() == 0) {
                allow = false;
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            allow = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(allow, false);
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
        if (this.layersActionBarLayout != null) {
            this.layersActionBarLayout.rebuildAllFragmentViews(last, last);
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
