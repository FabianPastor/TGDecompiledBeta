package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

public class LaunchActivity extends Activity implements NotificationCenterDelegate, ActionBarLayoutDelegate, DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList();
    private ActionBarLayout actionBarLayout;
    private View backgroundTablet;
    private ArrayList<User> contactsToSend;
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
    private ActionBarLayout rightActionBarLayout;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    private HashMap<String, String> systemLocaleStrings;
    private boolean tabletFullSize;
    private String videoPath;
    private AlertDialog visibleDialog;

    /* renamed from: org.telegram.ui.LaunchActivity$2 */
    class C14572 implements OnTouchListener {
        C14572() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty() != null || motionEvent.getAction() != 1) {
                return false;
            }
            view = motionEvent.getX();
            motionEvent = motionEvent.getY();
            int[] iArr = new int[2];
            LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (!LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) {
                if (view <= ((float) i) || view >= ((float) (i + LaunchActivity.this.layersActionBarLayout.getWidth())) || motionEvent <= ((float) i2) || motionEvent >= ((float) (i2 + LaunchActivity.this.layersActionBarLayout.getHeight()))) {
                    if (LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$3 */
    class C14593 implements OnClickListener {
        public void onClick(View view) {
        }

        C14593() {
        }
    }

    private class VcardData {
        String name;
        ArrayList<String> phones;

        private VcardData() {
            this.phones = new ArrayList();
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$4 */
    class C21674 implements OnItemClickListener {
        C21674() {
        }

        public void onItemClick(View view, int i) {
            if (i == 0) {
                LaunchActivity.this.drawerLayoutAdapter.setAccountsShowed(LaunchActivity.this.drawerLayoutAdapter.isAccountsShowed() ^ 1, true);
            } else if (view instanceof DrawerUserCell) {
                LaunchActivity.this.switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if ((view instanceof DrawerAddCell) != null) {
                view = -1;
                for (i = 0; i < 3; i++) {
                    if (!UserConfig.getInstance(i).isClientActivated()) {
                        view = i;
                        break;
                    }
                }
                if (view >= null) {
                    LaunchActivity.this.presentFragment(new LoginActivity(view));
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else {
                view = LaunchActivity.this.drawerLayoutAdapter.getId(i);
                if (view == 2) {
                    LaunchActivity.this.presentFragment(new GroupCreateActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 3) {
                    view = new Bundle();
                    view.putBoolean("onlyUsers", true);
                    view.putBoolean("destroyAfterSelect", true);
                    view.putBoolean("createSecretChat", true);
                    view.putBoolean("allowBots", false);
                    LaunchActivity.this.presentFragment(new ContactsActivity(view));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 4) {
                    view = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION != 0 || view.getBoolean("channel_intro", false) == 0) {
                        LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                        view.edit().putBoolean("channel_intro", true).commit();
                    } else {
                        view = new Bundle();
                        view.putInt("step", 0);
                        LaunchActivity.this.presentFragment(new ChannelCreateActivity(view));
                    }
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 6) {
                    LaunchActivity.this.presentFragment(new ContactsActivity(null));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 7) {
                    LaunchActivity.this.presentFragment(new InviteContactsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 8) {
                    LaunchActivity.this.presentFragment(new SettingsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 9) {
                    Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", C0446R.string.TelegramFaqUrl));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 10) {
                    LaunchActivity.this.presentFragment(new CallLogActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (view == 11) {
                    view = new Bundle();
                    view.putInt("user_id", UserConfig.getInstance(LaunchActivity.this.currentAccount).getClientUserId());
                    LaunchActivity.this.presentFragment(new ChatActivity(view));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$6 */
    class C21686 implements PasscodeViewDelegate {
        C21686() {
        }

        public void didAcceptedPassword() {
            SharedConfig.isWaitingForPasscodeEnter = false;
            if (LaunchActivity.this.passcodeSaveIntent != null) {
                LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
                LaunchActivity.this.passcodeSaveIntent = null;
            }
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            LaunchActivity.this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                LaunchActivity.this.layersActionBarLayout.showLastFragment();
                LaunchActivity.this.rightActionBarLayout.showLastFragment();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(android.os.Bundle r11) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r10 = this;
        org.telegram.messenger.ApplicationLoader.postInitApplication();
        r0 = r10.getResources();
        r0 = r0.getConfiguration();
        org.telegram.messenger.AndroidUtilities.checkDisplaySize(r10, r0);
        r0 = org.telegram.messenger.UserConfig.selectedAccount;
        r10.currentAccount = r0;
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.isClientActivated();
        r1 = 0;
        if (r0 != 0) goto L_0x00aa;
    L_0x001f:
        r0 = r10.getIntent();
        if (r0 == 0) goto L_0x004a;
    L_0x0025:
        r2 = r0.getAction();
        if (r2 == 0) goto L_0x004a;
    L_0x002b:
        r2 = "android.intent.action.SEND";
        r3 = r0.getAction();
        r2 = r2.equals(r3);
        if (r2 != 0) goto L_0x0043;
    L_0x0037:
        r2 = r0.getAction();
        r3 = "android.intent.action.SEND_MULTIPLE";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x004a;
    L_0x0043:
        super.onCreate(r11);
        r10.finish();
        return;
    L_0x004a:
        r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r3 = "intro_crashed_time";
        r4 = 0;
        r6 = r2.getLong(r3, r4);
        r3 = "fromIntro";
        r3 = r0.getBooleanExtra(r3, r1);
        if (r3 == 0) goto L_0x006b;
    L_0x005e:
        r2 = r2.edit();
        r8 = "intro_crashed_time";
        r2 = r2.putLong(r8, r4);
        r2.commit();
    L_0x006b:
        r4 = java.lang.System.currentTimeMillis();
        r8 = r6 - r4;
        r4 = java.lang.Math.abs(r8);
        r6 = 120000; // 0x1d4c0 float:1.68156E-40 double:5.9288E-319;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 < 0) goto L_0x00aa;
    L_0x007c:
        if (r0 == 0) goto L_0x00aa;
    L_0x007e:
        if (r3 != 0) goto L_0x00aa;
    L_0x0080:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = "logininfo2";
        r2 = r2.getSharedPreferences(r3, r1);
        r2 = r2.getAll();
        r2 = r2.isEmpty();
        if (r2 == 0) goto L_0x00aa;
    L_0x0092:
        r1 = new android.content.Intent;
        r2 = org.telegram.ui.IntroActivity.class;
        r1.<init>(r10, r2);
        r0 = r0.getData();
        r1.setData(r0);
        r10.startActivity(r1);
        super.onCreate(r11);
        r10.finish();
        return;
    L_0x00aa:
        r0 = 1;
        r10.requestWindowFeature(r0);
        r2 = NUM; // 0x7f0d000c float:1.874214E38 double:1.0531297835E-314;
        r10.setTheme(r2);
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        r4 = 0;
        if (r2 < r3) goto L_0x00cc;
    L_0x00bb:
        r2 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x00cc }
        r3 = "actionBarDefault";	 Catch:{ Exception -> 0x00cc }
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);	 Catch:{ Exception -> 0x00cc }
        r5 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;	 Catch:{ Exception -> 0x00cc }
        r3 = r3 | r5;	 Catch:{ Exception -> 0x00cc }
        r2.<init>(r4, r4, r3);	 Catch:{ Exception -> 0x00cc }
        r10.setTaskDescription(r2);	 Catch:{ Exception -> 0x00cc }
    L_0x00cc:
        r2 = r10.getWindow();
        r3 = NUM; // 0x7f0701f0 float:1.7945584E38 double:1.052935748E-314;
        r2.setBackgroundDrawableResource(r3);
        r2 = org.telegram.messenger.SharedConfig.passcodeHash;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x00f0;
    L_0x00de:
        r2 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r2 != 0) goto L_0x00f0;
    L_0x00e2:
        r2 = r10.getWindow();	 Catch:{ Exception -> 0x00ec }
        r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;	 Catch:{ Exception -> 0x00ec }
        r2.setFlags(r3, r3);	 Catch:{ Exception -> 0x00ec }
        goto L_0x00f0;
    L_0x00ec:
        r2 = move-exception;
        org.telegram.messenger.FileLog.m3e(r2);
    L_0x00f0:
        super.onCreate(r11);
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 24;
        if (r2 < r3) goto L_0x00ff;
    L_0x00f9:
        r2 = r10.isInMultiWindowMode();
        org.telegram.messenger.AndroidUtilities.isInMultiwindow = r2;
    L_0x00ff:
        org.telegram.ui.ActionBar.Theme.createChatResources(r10, r1);
        r2 = org.telegram.messenger.SharedConfig.passcodeHash;
        r2 = r2.length();
        if (r2 == 0) goto L_0x011a;
    L_0x010a:
        r2 = org.telegram.messenger.SharedConfig.appLocked;
        if (r2 == 0) goto L_0x011a;
    L_0x010e:
        r2 = r10.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        org.telegram.messenger.SharedConfig.lastPauseTime = r2;
    L_0x011a:
        r2 = r10.getResources();
        r3 = "status_bar_height";
        r5 = "dimen";
        r6 = "android";
        r2 = r2.getIdentifier(r3, r5, r6);
        if (r2 <= 0) goto L_0x0134;
    L_0x012a:
        r3 = r10.getResources();
        r2 = r3.getDimensionPixelSize(r2);
        org.telegram.messenger.AndroidUtilities.statusBarHeight = r2;
    L_0x0134:
        r2 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r2.<init>(r10);
        r10.actionBarLayout = r2;
        r2 = new org.telegram.ui.ActionBar.DrawerLayoutContainer;
        r2.<init>(r10);
        r10.drawerLayoutContainer = r2;
        r2 = r10.drawerLayoutContainer;
        r3 = new android.view.ViewGroup$LayoutParams;
        r5 = -1;
        r3.<init>(r5, r5);
        r10.setContentView(r2, r3);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        if (r2 == 0) goto L_0x0247;
    L_0x0155:
        r2 = r10.getWindow();
        r6 = 16;
        r2.setSoftInputMode(r6);
        r2 = new org.telegram.ui.LaunchActivity$1;
        r2.<init>(r10);
        r6 = r10.drawerLayoutContainer;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3);
        r6.addView(r2, r7);
        r6 = new android.view.View;
        r6.<init>(r10);
        r10.backgroundTablet = r6;
        r6 = r10.getResources();
        r7 = NUM; // 0x7f070052 float:1.7944744E38 double:1.0529355435E-314;
        r6 = r6.getDrawable(r7);
        r6 = (android.graphics.drawable.BitmapDrawable) r6;
        r7 = android.graphics.Shader.TileMode.REPEAT;
        r8 = android.graphics.Shader.TileMode.REPEAT;
        r6.setTileModeXY(r7, r8);
        r7 = r10.backgroundTablet;
        r7.setBackgroundDrawable(r6);
        r6 = r10.backgroundTablet;
        r7 = org.telegram.ui.Components.LayoutHelper.createRelative(r5, r5);
        r2.addView(r6, r7);
        r6 = r10.actionBarLayout;
        r2.addView(r6);
        r6 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r6.<init>(r10);
        r10.rightActionBarLayout = r6;
        r6 = r10.rightActionBarLayout;
        r7 = rightFragmentsStack;
        r6.init(r7);
        r6 = r10.rightActionBarLayout;
        r6.setDelegate(r10);
        r6 = r10.rightActionBarLayout;
        r2.addView(r6);
        r6 = new android.widget.FrameLayout;
        r6.<init>(r10);
        r10.shadowTabletSide = r6;
        r6 = r10.shadowTabletSide;
        r7 = NUM; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r6.setBackgroundColor(r7);
        r6 = r10.shadowTabletSide;
        r2.addView(r6);
        r6 = new android.widget.FrameLayout;
        r6.<init>(r10);
        r10.shadowTablet = r6;
        r6 = r10.shadowTablet;
        r7 = layerFragmentsStack;
        r7 = r7.isEmpty();
        r8 = 8;
        if (r7 == 0) goto L_0x01db;
    L_0x01d9:
        r7 = r8;
        goto L_0x01dc;
    L_0x01db:
        r7 = r1;
    L_0x01dc:
        r6.setVisibility(r7);
        r6 = r10.shadowTablet;
        r7 = NUM; // 0x7f000000 float:1.7014118E38 double:1.0527088494E-314;
        r6.setBackgroundColor(r7);
        r6 = r10.shadowTablet;
        r2.addView(r6);
        r6 = r10.shadowTablet;
        r7 = new org.telegram.ui.LaunchActivity$2;
        r7.<init>();
        r6.setOnTouchListener(r7);
        r6 = r10.shadowTablet;
        r7 = new org.telegram.ui.LaunchActivity$3;
        r7.<init>();
        r6.setOnClickListener(r7);
        r6 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r6.<init>(r10);
        r10.layersActionBarLayout = r6;
        r6 = r10.layersActionBarLayout;
        r6.setRemoveActionBarExtraHeight(r0);
        r6 = r10.layersActionBarLayout;
        r7 = r10.shadowTablet;
        r6.setBackgroundView(r7);
        r6 = r10.layersActionBarLayout;
        r6.setUseAlphaAnimations(r0);
        r6 = r10.layersActionBarLayout;
        r7 = NUM; // 0x7f070043 float:1.7944714E38 double:1.052935536E-314;
        r6.setBackgroundResource(r7);
        r6 = r10.layersActionBarLayout;
        r7 = layerFragmentsStack;
        r6.init(r7);
        r6 = r10.layersActionBarLayout;
        r6.setDelegate(r10);
        r6 = r10.layersActionBarLayout;
        r7 = r10.drawerLayoutContainer;
        r6.setDrawerLayoutContainer(r7);
        r6 = r10.layersActionBarLayout;
        r7 = layerFragmentsStack;
        r7 = r7.isEmpty();
        if (r7 == 0) goto L_0x023d;
    L_0x023c:
        goto L_0x023e;
    L_0x023d:
        r8 = r1;
    L_0x023e:
        r6.setVisibility(r8);
        r6 = r10.layersActionBarLayout;
        r2.addView(r6);
        goto L_0x0253;
    L_0x0247:
        r2 = r10.drawerLayoutContainer;
        r6 = r10.actionBarLayout;
        r7 = new android.view.ViewGroup$LayoutParams;
        r7.<init>(r5, r5);
        r2.addView(r6, r7);
    L_0x0253:
        r2 = new org.telegram.ui.Components.RecyclerListView;
        r2.<init>(r10);
        r10.sideMenu = r2;
        r2 = r10.sideMenu;
        r2 = r2.getItemAnimator();
        r2 = (org.telegram.messenger.support.widget.DefaultItemAnimator) r2;
        r2.setDelayAnimations(r1);
        r2 = r10.sideMenu;
        r6 = "chats_menuBackground";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r2.setBackgroundColor(r6);
        r2 = r10.sideMenu;
        r6 = new org.telegram.messenger.support.widget.LinearLayoutManager;
        r6.<init>(r10, r0, r1);
        r2.setLayoutManager(r6);
        r2 = r10.sideMenu;
        r6 = new org.telegram.ui.Adapters.DrawerLayoutAdapter;
        r6.<init>(r10);
        r10.drawerLayoutAdapter = r6;
        r2.setAdapter(r6);
        r2 = r10.drawerLayoutContainer;
        r6 = r10.sideMenu;
        r2.setDrawerLayout(r6);
        r2 = r10.sideMenu;
        r2 = r2.getLayoutParams();
        r2 = (android.widget.FrameLayout.LayoutParams) r2;
        r6 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();
        r7 = org.telegram.messenger.AndroidUtilities.isTablet();
        r8 = NUM; // 0x43a00000 float:320.0 double:5.605467397E-315;
        if (r7 == 0) goto L_0x02a6;
    L_0x02a1:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x02bd;
    L_0x02a6:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r6.x;
        r6 = r6.y;
        r6 = java.lang.Math.min(r8, r6);
        r8 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 - r8;
        r6 = java.lang.Math.min(r7, r6);
    L_0x02bd:
        r2.width = r6;
        r2.height = r5;
        r6 = r10.sideMenu;
        r6.setLayoutParams(r2);
        r2 = r10.sideMenu;
        r6 = new org.telegram.ui.LaunchActivity$4;
        r6.<init>();
        r2.setOnItemClickListener(r6);
        r2 = r10.drawerLayoutContainer;
        r6 = r10.actionBarLayout;
        r2.setParentActionBarLayout(r6);
        r2 = r10.actionBarLayout;
        r6 = r10.drawerLayoutContainer;
        r2.setDrawerLayoutContainer(r6);
        r2 = r10.actionBarLayout;
        r6 = mainFragmentsStack;
        r2.init(r6);
        r2 = r10.actionBarLayout;
        r2.setDelegate(r10);
        org.telegram.ui.ActionBar.Theme.loadWallpaper();
        r2 = new org.telegram.ui.Components.PasscodeView;
        r2.<init>(r10);
        r10.passcodeView = r2;
        r2 = r10.drawerLayoutContainer;
        r6 = r10.passcodeView;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3);
        r2.addView(r6, r3);
        r10.checkCurrentAccount();
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r6 = new java.lang.Object[r0];
        r6[r1] = r10;
        r2.postNotificationName(r3, r6);
        r2 = r10.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getConnectionState();
        r10.currentConnectionState = r2;
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.reloadInterface;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetNewTheme;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetPasscode;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper;
        r2.addObserver(r10, r3);
        r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated;
        r2.addObserver(r10, r3);
        r2 = r10.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 == 0) goto L_0x0489;
    L_0x036d:
        r2 = r10.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.isClientActivated();
        if (r2 != 0) goto L_0x0389;
    L_0x0379:
        r2 = r10.actionBarLayout;
        r3 = new org.telegram.ui.LoginActivity;
        r3.<init>();
        r2.addFragmentToStack(r3);
        r2 = r10.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r1);
        goto L_0x039d;
    L_0x0389:
        r2 = new org.telegram.ui.DialogsActivity;
        r2.<init>(r4);
        r3 = r10.sideMenu;
        r2.setSideMenu(r3);
        r3 = r10.actionBarLayout;
        r3.addFragmentToStack(r2);
        r2 = r10.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r0, r1);
    L_0x039d:
        if (r11 == 0) goto L_0x04f0;
    L_0x039f:
        r2 = "fragment";	 Catch:{ Exception -> 0x0483 }
        r2 = r11.getString(r2);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x03a7:
        r3 = "args";	 Catch:{ Exception -> 0x0483 }
        r3 = r11.getBundle(r3);	 Catch:{ Exception -> 0x0483 }
        r4 = r2.hashCode();	 Catch:{ Exception -> 0x0483 }
        switch(r4) {
            case -1529105743: goto L_0x03f1;
            case -1349522494: goto L_0x03e7;
            case 3052376: goto L_0x03dd;
            case 3108362: goto L_0x03d3;
            case 98629247: goto L_0x03c9;
            case 738950403: goto L_0x03bf;
            case 1434631203: goto L_0x03b5;
            default: goto L_0x03b4;
        };	 Catch:{ Exception -> 0x0483 }
    L_0x03b4:
        goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03b5:
        r4 = "settings";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03bd:
        r2 = r0;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03bf:
        r4 = "channel";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03c7:
        r2 = 3;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03c9:
        r4 = "group";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03d1:
        r2 = 2;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03d3:
        r4 = "edit";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03db:
        r2 = 4;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03dd:
        r4 = "chat";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03e5:
        r2 = r1;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03e7:
        r4 = "chat_profile";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03ef:
        r2 = 5;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03f1:
        r4 = "wallpapers";	 Catch:{ Exception -> 0x0483 }
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0483 }
        if (r2 == 0) goto L_0x03fb;	 Catch:{ Exception -> 0x0483 }
    L_0x03f9:
        r2 = 6;	 Catch:{ Exception -> 0x0483 }
        goto L_0x03fc;	 Catch:{ Exception -> 0x0483 }
    L_0x03fb:
        r2 = r5;	 Catch:{ Exception -> 0x0483 }
    L_0x03fc:
        switch(r2) {
            case 0: goto L_0x046f;
            case 1: goto L_0x0460;
            case 2: goto L_0x044c;
            case 3: goto L_0x0438;
            case 4: goto L_0x0424;
            case 5: goto L_0x0410;
            case 6: goto L_0x0401;
            default: goto L_0x03ff;
        };	 Catch:{ Exception -> 0x0483 }
    L_0x03ff:
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0401:
        r2 = new org.telegram.ui.WallpapersActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>();	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0410:
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0412:
        r2 = new org.telegram.ui.ProfileActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x041f:
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0424:
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0426:
        r2 = new org.telegram.ui.ChannelEditActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0433:
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0438:
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x043a:
        r2 = new org.telegram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0447:
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x044c:
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x044e:
        r2 = new org.telegram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x045b:
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0460:
        r2 = new org.telegram.ui.SettingsActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>();	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x046f:
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x0471:
        r2 = new org.telegram.ui.ChatActivity;	 Catch:{ Exception -> 0x0483 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0483 }
        r3 = r10.actionBarLayout;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.addFragmentToStack(r2);	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x04f0;	 Catch:{ Exception -> 0x0483 }
    L_0x047e:
        r2.restoreSelfArgs(r11);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04f0;
    L_0x0483:
        r2 = move-exception;
        org.telegram.messenger.FileLog.m3e(r2);
        goto L_0x04f0;
    L_0x0489:
        r2 = r10.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.get(r1);
        r2 = (org.telegram.ui.ActionBar.BaseFragment) r2;
        r3 = r2 instanceof org.telegram.ui.DialogsActivity;
        if (r3 == 0) goto L_0x049e;
    L_0x0497:
        r2 = (org.telegram.ui.DialogsActivity) r2;
        r3 = r10.sideMenu;
        r2.setSideMenu(r3);
    L_0x049e:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x04d3;
    L_0x04a4:
        r2 = r10.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.size();
        if (r2 > r0) goto L_0x04ba;
    L_0x04ae:
        r2 = r10.layersActionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 == 0) goto L_0x04ba;
    L_0x04b8:
        r2 = r0;
        goto L_0x04bb;
    L_0x04ba:
        r2 = r1;
    L_0x04bb:
        r3 = r10.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r0) goto L_0x04d4;
    L_0x04c5:
        r3 = r10.layersActionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r1);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x04d4;
    L_0x04d1:
        r2 = r1;
        goto L_0x04d4;
    L_0x04d3:
        r2 = r0;
    L_0x04d4:
        r3 = r10.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.size();
        if (r3 != r0) goto L_0x04eb;
    L_0x04de:
        r3 = r10.actionBarLayout;
        r3 = r3.fragmentsStack;
        r3 = r3.get(r1);
        r3 = r3 instanceof org.telegram.ui.LoginActivity;
        if (r3 == 0) goto L_0x04eb;
    L_0x04ea:
        r2 = r1;
    L_0x04eb:
        r3 = r10.drawerLayoutContainer;
        r3.setAllowOpenDrawer(r2, r1);
    L_0x04f0:
        r10.checkLayout();
        r2 = r10.getIntent();
        if (r11 == 0) goto L_0x04fb;
    L_0x04f9:
        r11 = r0;
        goto L_0x04fc;
    L_0x04fb:
        r11 = r1;
    L_0x04fc:
        r10.handleIntent(r2, r1, r11, r1);
        r11 = android.os.Build.DISPLAY;	 Catch:{ Exception -> 0x0542 }
        r1 = android.os.Build.USER;	 Catch:{ Exception -> 0x0542 }
        if (r11 == 0) goto L_0x050a;	 Catch:{ Exception -> 0x0542 }
    L_0x0505:
        r11 = r11.toLowerCase();	 Catch:{ Exception -> 0x0542 }
        goto L_0x050c;	 Catch:{ Exception -> 0x0542 }
    L_0x050a:
        r11 = "";	 Catch:{ Exception -> 0x0542 }
    L_0x050c:
        if (r1 == 0) goto L_0x0513;	 Catch:{ Exception -> 0x0542 }
    L_0x050e:
        r1 = r11.toLowerCase();	 Catch:{ Exception -> 0x0542 }
        goto L_0x0515;	 Catch:{ Exception -> 0x0542 }
    L_0x0513:
        r1 = "";	 Catch:{ Exception -> 0x0542 }
    L_0x0515:
        r2 = "flyme";	 Catch:{ Exception -> 0x0542 }
        r11 = r11.contains(r2);	 Catch:{ Exception -> 0x0542 }
        if (r11 != 0) goto L_0x0525;	 Catch:{ Exception -> 0x0542 }
    L_0x051d:
        r11 = "flyme";	 Catch:{ Exception -> 0x0542 }
        r11 = r1.contains(r11);	 Catch:{ Exception -> 0x0542 }
        if (r11 == 0) goto L_0x0546;	 Catch:{ Exception -> 0x0542 }
    L_0x0525:
        org.telegram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r0;	 Catch:{ Exception -> 0x0542 }
        r11 = r10.getWindow();	 Catch:{ Exception -> 0x0542 }
        r11 = r11.getDecorView();	 Catch:{ Exception -> 0x0542 }
        r11 = r11.getRootView();	 Catch:{ Exception -> 0x0542 }
        r1 = r11.getViewTreeObserver();	 Catch:{ Exception -> 0x0542 }
        r2 = new org.telegram.ui.LaunchActivity$5;	 Catch:{ Exception -> 0x0542 }
        r2.<init>(r11);	 Catch:{ Exception -> 0x0542 }
        r10.onGlobalLayoutListener = r2;	 Catch:{ Exception -> 0x0542 }
        r1.addOnGlobalLayoutListener(r2);	 Catch:{ Exception -> 0x0542 }
        goto L_0x0546;
    L_0x0542:
        r11 = move-exception;
        org.telegram.messenger.FileLog.m3e(r11);
    L_0x0546:
        r11 = org.telegram.messenger.MediaController.getInstance();
        r11.setBaseActivity(r10, r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    public void switchToAccount(int i, boolean z) {
        if (i != UserConfig.selectedAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = i;
            UserConfig.getInstance(0).saveConfig(false);
            checkCurrentAccount();
            if (AndroidUtilities.isTablet() != 0) {
                this.layersActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.removeAllFragments();
                if (this.tabletFullSize == 0) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty() != 0) {
                        this.backgroundTablet.setVisibility(0);
                    }
                }
            }
            if (z) {
                this.actionBarLayout.removeAllFragments();
            } else {
                this.actionBarLayout.removeFragmentFromStack(0);
            }
            i = new DialogsActivity(false);
            i.setSideMenu(this.sideMenu);
            this.actionBarLayout.addFragmentToStack(i, 0);
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet() != 0) {
                this.layersActionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
            if (ApplicationLoader.mainInterfacePaused == 0) {
                ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
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
        if (i != -1) {
            switchToAccount(i, true);
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
        }
        this.currentAccount = UserConfig.selectedAccount;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        updateCurrentConnectionState(this.currentAccount);
    }

    private void checkLayout() {
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout != null) {
                int i = 8;
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
                    if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                        i = 0;
                    }
                    view.setVisibility(i);
                } else {
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
                    this.rightActionBarLayout.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 8 : 0);
                    this.backgroundTablet.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 0 : 8);
                    FrameLayout frameLayout = this.shadowTabletSide;
                    if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                        i = 0;
                    }
                    frameLayout.setVisibility(i);
                }
            }
        }
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
            this.passcodeView.setDelegate(new C21686());
        }
    }

    private boolean handleIntent(android.content.Intent r44, boolean r45, boolean r46, boolean r47) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r43 = this;
        r14 = r43;
        r15 = r44;
        r1 = r46;
        r2 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r43, r44);
        r13 = 1;
        if (r2 == 0) goto L_0x000e;
    L_0x000d:
        return r13;
    L_0x000e:
        r2 = org.telegram.ui.PhotoViewer.hasInstance();
        r12 = 0;
        if (r2 == 0) goto L_0x0034;
    L_0x0015:
        r2 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r2.isVisible();
        if (r2 == 0) goto L_0x0034;
    L_0x001f:
        if (r15 == 0) goto L_0x002d;
    L_0x0021:
        r2 = "android.intent.action.MAIN";
        r3 = r44.getAction();
        r2 = r2.equals(r3);
        if (r2 != 0) goto L_0x0034;
    L_0x002d:
        r2 = org.telegram.ui.PhotoViewer.getInstance();
        r2.closePhoto(r12, r13);
    L_0x0034:
        r2 = r44.getFlags();
        r11 = new int[r13];
        r3 = "currentAccount";
        r4 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = r15.getIntExtra(r3, r4);
        r11[r12] = r3;
        r3 = r11[r12];
        r14.switchToAccount(r3, r13);
        if (r47 != 0) goto L_0x006a;
    L_0x004b:
        r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r13);
        if (r3 != 0) goto L_0x0055;
    L_0x0051:
        r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r3 == 0) goto L_0x006a;
    L_0x0055:
        r43.showPasscodeActivity();
        r14.passcodeSaveIntent = r15;
        r10 = r45;
        r14.passcodeSaveIntentIsNew = r10;
        r14.passcodeSaveIntentIsRestore = r1;
        r1 = r14.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1.saveConfig(r12);
        return r12;
    L_0x006a:
        r10 = r45;
        r3 = java.lang.Integer.valueOf(r12);
        r4 = java.lang.Integer.valueOf(r12);
        r16 = java.lang.Integer.valueOf(r12);
        r5 = java.lang.Integer.valueOf(r12);
        r17 = java.lang.Integer.valueOf(r12);
        r18 = java.lang.Integer.valueOf(r12);
        r6 = org.telegram.messenger.SharedConfig.directShare;
        r8 = 0;
        if (r6 == 0) goto L_0x009d;
    L_0x008a:
        if (r15 == 0) goto L_0x009d;
    L_0x008c:
        r6 = r44.getExtras();
        if (r6 == 0) goto L_0x009d;
    L_0x0092:
        r6 = r44.getExtras();
        r7 = "dialogId";
        r6 = r6.getLong(r7, r8);
        goto L_0x009e;
    L_0x009d:
        r6 = r8;
    L_0x009e:
        r13 = 0;
        r14.photoPathsArray = r13;
        r14.videoPath = r13;
        r14.sendingText = r13;
        r14.documentsPathsArray = r13;
        r14.documentsOriginalPathsArray = r13;
        r14.documentsMimeType = r13;
        r14.documentsUrisArray = r13;
        r14.contactsToSend = r13;
        r8 = r14.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.isClientActivated();
        if (r8 == 0) goto L_0x0b3a;
    L_0x00bb:
        r8 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r2 = r2 & r8;
        if (r2 != 0) goto L_0x0b3a;
    L_0x00c0:
        if (r15 == 0) goto L_0x0b3a;
    L_0x00c2:
        r2 = r44.getAction();
        if (r2 == 0) goto L_0x0b3a;
    L_0x00c8:
        if (r1 != 0) goto L_0x0b3a;
    L_0x00ca:
        r1 = "android.intent.action.SEND";
        r2 = r44.getAction();
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x041c;
    L_0x00d6:
        r1 = r44.getType();
        if (r1 == 0) goto L_0x02f1;
    L_0x00dc:
        r2 = "text/x-vcard";
        r2 = r1.equals(r2);
        if (r2 == 0) goto L_0x02f1;
    L_0x00e4:
        r1 = r44.getExtras();	 Catch:{ Exception -> 0x02e1 }
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x02e1 }
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x02e1 }
        r1 = (android.net.Uri) r1;	 Catch:{ Exception -> 0x02e1 }
        if (r1 == 0) goto L_0x02d8;	 Catch:{ Exception -> 0x02e1 }
    L_0x00f2:
        r2 = r43.getContentResolver();	 Catch:{ Exception -> 0x02e1 }
        r1 = r2.openInputStream(r1);	 Catch:{ Exception -> 0x02e1 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x02e1 }
        r2.<init>();	 Catch:{ Exception -> 0x02e1 }
        r8 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x02e1 }
        r9 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x02e1 }
        r13 = "UTF-8";	 Catch:{ Exception -> 0x02e1 }
        r9.<init>(r1, r13);	 Catch:{ Exception -> 0x02e1 }
        r8.<init>(r9);	 Catch:{ Exception -> 0x02e1 }
        r13 = 0;	 Catch:{ Exception -> 0x02e1 }
    L_0x010c:
        r9 = r8.readLine();	 Catch:{ Exception -> 0x02e1 }
        if (r9 == 0) goto L_0x0270;	 Catch:{ Exception -> 0x02e1 }
    L_0x0112:
        r24 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x02e1 }
        if (r24 == 0) goto L_0x0126;
    L_0x0116:
        org.telegram.messenger.FileLog.m0d(r9);	 Catch:{ Exception -> 0x011a }
        goto L_0x0126;
    L_0x011a:
        r0 = move-exception;
        r1 = r0;
        r26 = r3;
    L_0x011e:
        r27 = r4;
        r28 = r5;
        r29 = r6;
        goto L_0x02eb;
    L_0x0126:
        r12 = ":";	 Catch:{ Exception -> 0x02e1 }
        r9 = r9.split(r12);	 Catch:{ Exception -> 0x02e1 }
        r12 = r9.length;	 Catch:{ Exception -> 0x02e1 }
        r26 = r3;
        r3 = 2;
        if (r12 == r3) goto L_0x0136;
    L_0x0132:
        r3 = r26;
    L_0x0134:
        r12 = 0;
        goto L_0x010c;
    L_0x0136:
        r12 = 0;
        r3 = r9[r12];	 Catch:{ Exception -> 0x026d }
        r12 = "BEGIN";	 Catch:{ Exception -> 0x026d }
        r3 = r3.equals(r12);	 Catch:{ Exception -> 0x026d }
        if (r3 == 0) goto L_0x015a;
    L_0x0141:
        r3 = 1;
        r12 = r9[r3];	 Catch:{ Exception -> 0x0157 }
        r3 = "VCARD";	 Catch:{ Exception -> 0x0157 }
        r3 = r12.equals(r3);	 Catch:{ Exception -> 0x0157 }
        if (r3 == 0) goto L_0x015a;	 Catch:{ Exception -> 0x0157 }
    L_0x014c:
        r3 = new org.telegram.ui.LaunchActivity$VcardData;	 Catch:{ Exception -> 0x0157 }
        r12 = 0;	 Catch:{ Exception -> 0x0157 }
        r3.<init>();	 Catch:{ Exception -> 0x0157 }
        r2.add(r3);	 Catch:{ Exception -> 0x0157 }
        r13 = r3;
        goto L_0x0171;
    L_0x0157:
        r0 = move-exception;
        r1 = r0;
        goto L_0x011e;
    L_0x015a:
        r3 = 0;
        r12 = r9[r3];	 Catch:{ Exception -> 0x026d }
        r3 = "END";	 Catch:{ Exception -> 0x026d }
        r3 = r12.equals(r3);	 Catch:{ Exception -> 0x026d }
        if (r3 == 0) goto L_0x0171;
    L_0x0165:
        r3 = 1;
        r12 = r9[r3];	 Catch:{ Exception -> 0x0157 }
        r3 = "VCARD";	 Catch:{ Exception -> 0x0157 }
        r3 = r12.equals(r3);	 Catch:{ Exception -> 0x0157 }
        if (r3 == 0) goto L_0x0171;
    L_0x0170:
        r13 = 0;
    L_0x0171:
        if (r13 != 0) goto L_0x017b;
    L_0x0173:
        r27 = r4;
        r28 = r5;
        r29 = r6;
        goto L_0x0261;
    L_0x017b:
        r3 = 0;
        r12 = r9[r3];	 Catch:{ Exception -> 0x026d }
        r3 = "FN";	 Catch:{ Exception -> 0x026d }
        r3 = r12.startsWith(r3);	 Catch:{ Exception -> 0x026d }
        if (r3 != 0) goto L_0x01b8;
    L_0x0186:
        r3 = 0;
        r12 = r9[r3];	 Catch:{ Exception -> 0x0157 }
        r3 = "ORG";	 Catch:{ Exception -> 0x0157 }
        r3 = r12.startsWith(r3);	 Catch:{ Exception -> 0x0157 }
        if (r3 == 0) goto L_0x019a;	 Catch:{ Exception -> 0x0157 }
    L_0x0191:
        r3 = r13.name;	 Catch:{ Exception -> 0x0157 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x0157 }
        if (r3 == 0) goto L_0x019a;	 Catch:{ Exception -> 0x0157 }
    L_0x0199:
        goto L_0x01b8;	 Catch:{ Exception -> 0x0157 }
    L_0x019a:
        r3 = 0;	 Catch:{ Exception -> 0x0157 }
        r12 = r9[r3];	 Catch:{ Exception -> 0x0157 }
        r3 = "TEL";	 Catch:{ Exception -> 0x0157 }
        r3 = r12.startsWith(r3);	 Catch:{ Exception -> 0x0157 }
        if (r3 == 0) goto L_0x0173;	 Catch:{ Exception -> 0x0157 }
    L_0x01a5:
        r3 = 1;	 Catch:{ Exception -> 0x0157 }
        r9 = r9[r3];	 Catch:{ Exception -> 0x0157 }
        r9 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r9, r3);	 Catch:{ Exception -> 0x0157 }
        r3 = r9.length();	 Catch:{ Exception -> 0x0157 }
        if (r3 <= 0) goto L_0x0173;	 Catch:{ Exception -> 0x0157 }
    L_0x01b2:
        r3 = r13.phones;	 Catch:{ Exception -> 0x0157 }
        r3.add(r9);	 Catch:{ Exception -> 0x0157 }
        goto L_0x0173;
    L_0x01b8:
        r3 = 0;
        r12 = r9[r3];	 Catch:{ Exception -> 0x026d }
        r3 = ";";	 Catch:{ Exception -> 0x026d }
        r3 = r12.split(r3);	 Catch:{ Exception -> 0x026d }
        r12 = r3.length;	 Catch:{ Exception -> 0x026d }
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r4 = 0;
        r5 = 0;
        r6 = 0;
    L_0x01cb:
        if (r4 >= r12) goto L_0x0202;
    L_0x01cd:
        r7 = r3[r4];	 Catch:{ Exception -> 0x02d6 }
        r31 = r3;	 Catch:{ Exception -> 0x02d6 }
        r3 = "=";	 Catch:{ Exception -> 0x02d6 }
        r3 = r7.split(r3);	 Catch:{ Exception -> 0x02d6 }
        r7 = r3.length;	 Catch:{ Exception -> 0x02d6 }
        r10 = 2;	 Catch:{ Exception -> 0x02d6 }
        if (r7 == r10) goto L_0x01dc;	 Catch:{ Exception -> 0x02d6 }
    L_0x01db:
        goto L_0x01fb;	 Catch:{ Exception -> 0x02d6 }
    L_0x01dc:
        r7 = 0;	 Catch:{ Exception -> 0x02d6 }
        r10 = r3[r7];	 Catch:{ Exception -> 0x02d6 }
        r7 = "CHARSET";	 Catch:{ Exception -> 0x02d6 }
        r7 = r10.equals(r7);	 Catch:{ Exception -> 0x02d6 }
        if (r7 == 0) goto L_0x01ec;	 Catch:{ Exception -> 0x02d6 }
    L_0x01e7:
        r7 = 1;	 Catch:{ Exception -> 0x02d6 }
        r3 = r3[r7];	 Catch:{ Exception -> 0x02d6 }
        r6 = r3;	 Catch:{ Exception -> 0x02d6 }
        goto L_0x01fb;	 Catch:{ Exception -> 0x02d6 }
    L_0x01ec:
        r7 = 0;	 Catch:{ Exception -> 0x02d6 }
        r10 = r3[r7];	 Catch:{ Exception -> 0x02d6 }
        r7 = "ENCODING";	 Catch:{ Exception -> 0x02d6 }
        r7 = r10.equals(r7);	 Catch:{ Exception -> 0x02d6 }
        if (r7 == 0) goto L_0x01fb;	 Catch:{ Exception -> 0x02d6 }
    L_0x01f7:
        r7 = 1;	 Catch:{ Exception -> 0x02d6 }
        r3 = r3[r7];	 Catch:{ Exception -> 0x02d6 }
        r5 = r3;	 Catch:{ Exception -> 0x02d6 }
    L_0x01fb:
        r4 = r4 + 1;	 Catch:{ Exception -> 0x02d6 }
        r3 = r31;	 Catch:{ Exception -> 0x02d6 }
        r10 = r45;	 Catch:{ Exception -> 0x02d6 }
        goto L_0x01cb;	 Catch:{ Exception -> 0x02d6 }
    L_0x0202:
        r3 = 1;	 Catch:{ Exception -> 0x02d6 }
        r4 = r9[r3];	 Catch:{ Exception -> 0x02d6 }
        r13.name = r4;	 Catch:{ Exception -> 0x02d6 }
        if (r5 == 0) goto L_0x0261;	 Catch:{ Exception -> 0x02d6 }
    L_0x0209:
        r3 = "QUOTED-PRINTABLE";	 Catch:{ Exception -> 0x02d6 }
        r3 = r5.equalsIgnoreCase(r3);	 Catch:{ Exception -> 0x02d6 }
        if (r3 == 0) goto L_0x0261;	 Catch:{ Exception -> 0x02d6 }
    L_0x0211:
        r3 = r13.name;	 Catch:{ Exception -> 0x02d6 }
        r4 = "=";	 Catch:{ Exception -> 0x02d6 }
        r3 = r3.endsWith(r4);	 Catch:{ Exception -> 0x02d6 }
        if (r3 == 0) goto L_0x0249;	 Catch:{ Exception -> 0x02d6 }
    L_0x021b:
        if (r5 == 0) goto L_0x0249;	 Catch:{ Exception -> 0x02d6 }
    L_0x021d:
        r3 = r13.name;	 Catch:{ Exception -> 0x02d6 }
        r4 = r13.name;	 Catch:{ Exception -> 0x02d6 }
        r4 = r4.length();	 Catch:{ Exception -> 0x02d6 }
        r7 = 1;	 Catch:{ Exception -> 0x02d6 }
        r4 = r4 - r7;	 Catch:{ Exception -> 0x02d6 }
        r7 = 0;	 Catch:{ Exception -> 0x02d6 }
        r3 = r3.substring(r7, r4);	 Catch:{ Exception -> 0x02d6 }
        r13.name = r3;	 Catch:{ Exception -> 0x02d6 }
        r3 = r8.readLine();	 Catch:{ Exception -> 0x02d6 }
        if (r3 != 0) goto L_0x0235;	 Catch:{ Exception -> 0x02d6 }
    L_0x0234:
        goto L_0x0249;	 Catch:{ Exception -> 0x02d6 }
    L_0x0235:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02d6 }
        r4.<init>();	 Catch:{ Exception -> 0x02d6 }
        r7 = r13.name;	 Catch:{ Exception -> 0x02d6 }
        r4.append(r7);	 Catch:{ Exception -> 0x02d6 }
        r4.append(r3);	 Catch:{ Exception -> 0x02d6 }
        r3 = r4.toString();	 Catch:{ Exception -> 0x02d6 }
        r13.name = r3;	 Catch:{ Exception -> 0x02d6 }
        goto L_0x0211;	 Catch:{ Exception -> 0x02d6 }
    L_0x0249:
        r3 = r13.name;	 Catch:{ Exception -> 0x02d6 }
        r3 = r3.getBytes();	 Catch:{ Exception -> 0x02d6 }
        r3 = org.telegram.messenger.AndroidUtilities.decodeQuotedPrintable(r3);	 Catch:{ Exception -> 0x02d6 }
        if (r3 == 0) goto L_0x0261;	 Catch:{ Exception -> 0x02d6 }
    L_0x0255:
        r4 = r3.length;	 Catch:{ Exception -> 0x02d6 }
        if (r4 == 0) goto L_0x0261;	 Catch:{ Exception -> 0x02d6 }
    L_0x0258:
        r4 = new java.lang.String;	 Catch:{ Exception -> 0x02d6 }
        r4.<init>(r3, r6);	 Catch:{ Exception -> 0x02d6 }
        if (r4 == 0) goto L_0x0261;	 Catch:{ Exception -> 0x02d6 }
    L_0x025f:
        r13.name = r4;	 Catch:{ Exception -> 0x02d6 }
    L_0x0261:
        r3 = r26;
        r4 = r27;
        r5 = r28;
        r6 = r29;
        r10 = r45;
        goto L_0x0134;
    L_0x026d:
        r0 = move-exception;
        goto L_0x02e4;
    L_0x0270:
        r26 = r3;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r8.close();	 Catch:{ Exception -> 0x027f }
        r1.close();	 Catch:{ Exception -> 0x027f }
        goto L_0x0284;
    L_0x027f:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x02d6 }
    L_0x0284:
        r1 = 0;	 Catch:{ Exception -> 0x02d6 }
    L_0x0285:
        r3 = r2.size();	 Catch:{ Exception -> 0x02d6 }
        if (r1 >= r3) goto L_0x0408;	 Catch:{ Exception -> 0x02d6 }
    L_0x028b:
        r3 = r2.get(r1);	 Catch:{ Exception -> 0x02d6 }
        r3 = (org.telegram.ui.LaunchActivity.VcardData) r3;	 Catch:{ Exception -> 0x02d6 }
        r4 = r3.name;	 Catch:{ Exception -> 0x02d6 }
        if (r4 == 0) goto L_0x02d3;	 Catch:{ Exception -> 0x02d6 }
    L_0x0295:
        r4 = r3.phones;	 Catch:{ Exception -> 0x02d6 }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x02d6 }
        if (r4 != 0) goto L_0x02d3;	 Catch:{ Exception -> 0x02d6 }
    L_0x029d:
        r4 = r14.contactsToSend;	 Catch:{ Exception -> 0x02d6 }
        if (r4 != 0) goto L_0x02a8;	 Catch:{ Exception -> 0x02d6 }
    L_0x02a1:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x02d6 }
        r4.<init>();	 Catch:{ Exception -> 0x02d6 }
        r14.contactsToSend = r4;	 Catch:{ Exception -> 0x02d6 }
    L_0x02a8:
        r4 = 0;	 Catch:{ Exception -> 0x02d6 }
    L_0x02a9:
        r5 = r3.phones;	 Catch:{ Exception -> 0x02d6 }
        r5 = r5.size();	 Catch:{ Exception -> 0x02d6 }
        if (r4 >= r5) goto L_0x02d3;	 Catch:{ Exception -> 0x02d6 }
    L_0x02b1:
        r5 = r3.phones;	 Catch:{ Exception -> 0x02d6 }
        r5 = r5.get(r4);	 Catch:{ Exception -> 0x02d6 }
        r5 = (java.lang.String) r5;	 Catch:{ Exception -> 0x02d6 }
        r6 = new org.telegram.tgnet.TLRPC$TL_userContact_old2;	 Catch:{ Exception -> 0x02d6 }
        r6.<init>();	 Catch:{ Exception -> 0x02d6 }
        r6.phone = r5;	 Catch:{ Exception -> 0x02d6 }
        r5 = r3.name;	 Catch:{ Exception -> 0x02d6 }
        r6.first_name = r5;	 Catch:{ Exception -> 0x02d6 }
        r5 = "";	 Catch:{ Exception -> 0x02d6 }
        r6.last_name = r5;	 Catch:{ Exception -> 0x02d6 }
        r5 = 0;	 Catch:{ Exception -> 0x02d6 }
        r6.id = r5;	 Catch:{ Exception -> 0x02d6 }
        r5 = r14.contactsToSend;	 Catch:{ Exception -> 0x02d6 }
        r5.add(r6);	 Catch:{ Exception -> 0x02d6 }
        r4 = r4 + 1;
        goto L_0x02a9;
    L_0x02d3:
        r1 = r1 + 1;
        goto L_0x0285;
    L_0x02d6:
        r0 = move-exception;
        goto L_0x02ea;
    L_0x02d8:
        r26 = r3;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        goto L_0x02ee;
    L_0x02e1:
        r0 = move-exception;
        r26 = r3;
    L_0x02e4:
        r27 = r4;
        r28 = r5;
        r29 = r6;
    L_0x02ea:
        r1 = r0;
    L_0x02eb:
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x02ee:
        r13 = 1;
        goto L_0x0409;
    L_0x02f1:
        r26 = r3;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r2 = "android.intent.extra.TEXT";
        r2 = r15.getStringExtra(r2);
        if (r2 != 0) goto L_0x030d;
    L_0x0301:
        r3 = "android.intent.extra.TEXT";
        r3 = r15.getCharSequenceExtra(r3);
        if (r3 == 0) goto L_0x030d;
    L_0x0309:
        r2 = r3.toString();
    L_0x030d:
        r3 = "android.intent.extra.SUBJECT";
        r3 = r15.getStringExtra(r3);
        if (r2 == 0) goto L_0x034a;
    L_0x0315:
        r4 = r2.length();
        if (r4 == 0) goto L_0x034a;
    L_0x031b:
        r4 = "http://";
        r4 = r2.startsWith(r4);
        if (r4 != 0) goto L_0x032b;
    L_0x0323:
        r4 = "https://";
        r4 = r2.startsWith(r4);
        if (r4 == 0) goto L_0x0347;
    L_0x032b:
        if (r3 == 0) goto L_0x0347;
    L_0x032d:
        r4 = r3.length();
        if (r4 == 0) goto L_0x0347;
    L_0x0333:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r3 = "\n";
        r4.append(r3);
        r4.append(r2);
        r2 = r4.toString();
    L_0x0347:
        r14.sendingText = r2;
        goto L_0x0354;
    L_0x034a:
        if (r3 == 0) goto L_0x0354;
    L_0x034c:
        r2 = r3.length();
        if (r2 <= 0) goto L_0x0354;
    L_0x0352:
        r14.sendingText = r3;
    L_0x0354:
        r2 = "android.intent.extra.STREAM";
        r2 = r15.getParcelableExtra(r2);
        if (r2 == 0) goto L_0x0402;
    L_0x035c:
        r3 = r2 instanceof android.net.Uri;
        if (r3 != 0) goto L_0x0368;
    L_0x0360:
        r2 = r2.toString();
        r2 = android.net.Uri.parse(r2);
    L_0x0368:
        r2 = (android.net.Uri) r2;
        if (r2 == 0) goto L_0x0374;
    L_0x036c:
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r2);
        if (r3 == 0) goto L_0x0374;
    L_0x0372:
        r13 = 1;
        goto L_0x0375;
    L_0x0374:
        r13 = 0;
    L_0x0375:
        if (r13 != 0) goto L_0x0409;
    L_0x0377:
        if (r2 == 0) goto L_0x03ab;
    L_0x0379:
        if (r1 == 0) goto L_0x0383;
    L_0x037b:
        r3 = "image/";
        r3 = r1.startsWith(r3);
        if (r3 != 0) goto L_0x0393;
    L_0x0383:
        r3 = r2.toString();
        r3 = r3.toLowerCase();
        r4 = ".jpg";
        r3 = r3.endsWith(r4);
        if (r3 == 0) goto L_0x03ab;
    L_0x0393:
        r1 = r14.photoPathsArray;
        if (r1 != 0) goto L_0x039e;
    L_0x0397:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r14.photoPathsArray = r1;
    L_0x039e:
        r1 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;
        r1.<init>();
        r1.uri = r2;
        r2 = r14.photoPathsArray;
        r2.add(r1);
        goto L_0x0409;
    L_0x03ab:
        r3 = org.telegram.messenger.AndroidUtilities.getPath(r2);
        if (r3 == 0) goto L_0x03ef;
    L_0x03b1:
        r4 = "file:";
        r4 = r3.startsWith(r4);
        if (r4 == 0) goto L_0x03c1;
    L_0x03b9:
        r4 = "file://";
        r5 = "";
        r3 = r3.replace(r4, r5);
    L_0x03c1:
        if (r1 == 0) goto L_0x03ce;
    L_0x03c3:
        r4 = "video/";
        r1 = r1.startsWith(r4);
        if (r1 == 0) goto L_0x03ce;
    L_0x03cb:
        r14.videoPath = r3;
        goto L_0x0409;
    L_0x03ce:
        r1 = r14.documentsPathsArray;
        if (r1 != 0) goto L_0x03e0;
    L_0x03d2:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r14.documentsPathsArray = r1;
        r1 = new java.util.ArrayList;
        r1.<init>();
        r14.documentsOriginalPathsArray = r1;
    L_0x03e0:
        r1 = r14.documentsPathsArray;
        r1.add(r3);
        r1 = r14.documentsOriginalPathsArray;
        r2 = r2.toString();
        r1.add(r2);
        goto L_0x0409;
    L_0x03ef:
        r3 = r14.documentsUrisArray;
        if (r3 != 0) goto L_0x03fa;
    L_0x03f3:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r14.documentsUrisArray = r3;
    L_0x03fa:
        r3 = r14.documentsUrisArray;
        r3.add(r2);
        r14.documentsMimeType = r1;
        goto L_0x0409;
    L_0x0402:
        r1 = r14.sendingText;
        if (r1 != 0) goto L_0x0408;
    L_0x0406:
        goto L_0x02ee;
    L_0x0408:
        r13 = 0;
    L_0x0409:
        if (r13 == 0) goto L_0x0415;
    L_0x040b:
        r1 = "Unsupported content";
        r2 = 0;
        r1 = android.widget.Toast.makeText(r14, r1, r2);
        r1.show();
    L_0x0415:
        r6 = r11;
        r38 = r29;
        r1 = 1;
        r3 = 0;
        goto L_0x0b45;
    L_0x041c:
        r26 = r3;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r1 = r44.getAction();
        r2 = "android.intent.action.SEND_MULTIPLE";
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x0530;
    L_0x0430:
        r1 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x051c }
        r13 = r15.getParcelableArrayListExtra(r1);	 Catch:{ Exception -> 0x051c }
        r1 = r44.getType();	 Catch:{ Exception -> 0x051c }
        if (r13 == 0) goto L_0x046e;	 Catch:{ Exception -> 0x051c }
    L_0x043c:
        r2 = 0;	 Catch:{ Exception -> 0x051c }
    L_0x043d:
        r3 = r13.size();	 Catch:{ Exception -> 0x051c }
        if (r2 >= r3) goto L_0x0467;	 Catch:{ Exception -> 0x051c }
    L_0x0443:
        r3 = r13.get(r2);	 Catch:{ Exception -> 0x051c }
        r3 = (android.os.Parcelable) r3;	 Catch:{ Exception -> 0x051c }
        r4 = r3 instanceof android.net.Uri;	 Catch:{ Exception -> 0x051c }
        if (r4 != 0) goto L_0x0455;	 Catch:{ Exception -> 0x051c }
    L_0x044d:
        r3 = r3.toString();	 Catch:{ Exception -> 0x051c }
        r3 = android.net.Uri.parse(r3);	 Catch:{ Exception -> 0x051c }
    L_0x0455:
        r3 = (android.net.Uri) r3;	 Catch:{ Exception -> 0x051c }
        if (r3 == 0) goto L_0x0464;	 Catch:{ Exception -> 0x051c }
    L_0x0459:
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r3);	 Catch:{ Exception -> 0x051c }
        if (r3 == 0) goto L_0x0464;	 Catch:{ Exception -> 0x051c }
    L_0x045f:
        r13.remove(r2);	 Catch:{ Exception -> 0x051c }
        r2 = r2 + -1;	 Catch:{ Exception -> 0x051c }
    L_0x0464:
        r3 = 1;	 Catch:{ Exception -> 0x051c }
        r2 = r2 + r3;	 Catch:{ Exception -> 0x051c }
        goto L_0x043d;	 Catch:{ Exception -> 0x051c }
    L_0x0467:
        r2 = r13.isEmpty();	 Catch:{ Exception -> 0x051c }
        if (r2 == 0) goto L_0x046e;	 Catch:{ Exception -> 0x051c }
    L_0x046d:
        r13 = 0;	 Catch:{ Exception -> 0x051c }
    L_0x046e:
        if (r13 == 0) goto L_0x0521;	 Catch:{ Exception -> 0x051c }
    L_0x0470:
        if (r1 == 0) goto L_0x04af;	 Catch:{ Exception -> 0x051c }
    L_0x0472:
        r2 = "image/";	 Catch:{ Exception -> 0x051c }
        r2 = r1.startsWith(r2);	 Catch:{ Exception -> 0x051c }
        if (r2 == 0) goto L_0x04af;	 Catch:{ Exception -> 0x051c }
    L_0x047a:
        r1 = 0;	 Catch:{ Exception -> 0x051c }
    L_0x047b:
        r2 = r13.size();	 Catch:{ Exception -> 0x051c }
        if (r1 >= r2) goto L_0x051a;	 Catch:{ Exception -> 0x051c }
    L_0x0481:
        r2 = r13.get(r1);	 Catch:{ Exception -> 0x051c }
        r2 = (android.os.Parcelable) r2;	 Catch:{ Exception -> 0x051c }
        r3 = r2 instanceof android.net.Uri;	 Catch:{ Exception -> 0x051c }
        if (r3 != 0) goto L_0x0493;	 Catch:{ Exception -> 0x051c }
    L_0x048b:
        r2 = r2.toString();	 Catch:{ Exception -> 0x051c }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x051c }
    L_0x0493:
        r2 = (android.net.Uri) r2;	 Catch:{ Exception -> 0x051c }
        r3 = r14.photoPathsArray;	 Catch:{ Exception -> 0x051c }
        if (r3 != 0) goto L_0x04a0;	 Catch:{ Exception -> 0x051c }
    L_0x0499:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x051c }
        r3.<init>();	 Catch:{ Exception -> 0x051c }
        r14.photoPathsArray = r3;	 Catch:{ Exception -> 0x051c }
    L_0x04a0:
        r3 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;	 Catch:{ Exception -> 0x051c }
        r3.<init>();	 Catch:{ Exception -> 0x051c }
        r3.uri = r2;	 Catch:{ Exception -> 0x051c }
        r2 = r14.photoPathsArray;	 Catch:{ Exception -> 0x051c }
        r2.add(r3);	 Catch:{ Exception -> 0x051c }
        r1 = r1 + 1;	 Catch:{ Exception -> 0x051c }
        goto L_0x047b;	 Catch:{ Exception -> 0x051c }
    L_0x04af:
        r2 = 0;	 Catch:{ Exception -> 0x051c }
    L_0x04b0:
        r3 = r13.size();	 Catch:{ Exception -> 0x051c }
        if (r2 >= r3) goto L_0x051a;	 Catch:{ Exception -> 0x051c }
    L_0x04b6:
        r3 = r13.get(r2);	 Catch:{ Exception -> 0x051c }
        r3 = (android.os.Parcelable) r3;	 Catch:{ Exception -> 0x051c }
        r4 = r3 instanceof android.net.Uri;	 Catch:{ Exception -> 0x051c }
        if (r4 != 0) goto L_0x04c8;	 Catch:{ Exception -> 0x051c }
    L_0x04c0:
        r3 = r3.toString();	 Catch:{ Exception -> 0x051c }
        r3 = android.net.Uri.parse(r3);	 Catch:{ Exception -> 0x051c }
    L_0x04c8:
        r4 = r3;	 Catch:{ Exception -> 0x051c }
        r4 = (android.net.Uri) r4;	 Catch:{ Exception -> 0x051c }
        r5 = org.telegram.messenger.AndroidUtilities.getPath(r4);	 Catch:{ Exception -> 0x051c }
        r3 = r3.toString();	 Catch:{ Exception -> 0x051c }
        if (r3 != 0) goto L_0x04d6;	 Catch:{ Exception -> 0x051c }
    L_0x04d5:
        r3 = r5;	 Catch:{ Exception -> 0x051c }
    L_0x04d6:
        if (r5 == 0) goto L_0x0505;	 Catch:{ Exception -> 0x051c }
    L_0x04d8:
        r4 = "file:";	 Catch:{ Exception -> 0x051c }
        r4 = r5.startsWith(r4);	 Catch:{ Exception -> 0x051c }
        if (r4 == 0) goto L_0x04e8;	 Catch:{ Exception -> 0x051c }
    L_0x04e0:
        r4 = "file://";	 Catch:{ Exception -> 0x051c }
        r6 = "";	 Catch:{ Exception -> 0x051c }
        r5 = r5.replace(r4, r6);	 Catch:{ Exception -> 0x051c }
    L_0x04e8:
        r4 = r14.documentsPathsArray;	 Catch:{ Exception -> 0x051c }
        if (r4 != 0) goto L_0x04fa;	 Catch:{ Exception -> 0x051c }
    L_0x04ec:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x051c }
        r4.<init>();	 Catch:{ Exception -> 0x051c }
        r14.documentsPathsArray = r4;	 Catch:{ Exception -> 0x051c }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x051c }
        r4.<init>();	 Catch:{ Exception -> 0x051c }
        r14.documentsOriginalPathsArray = r4;	 Catch:{ Exception -> 0x051c }
    L_0x04fa:
        r4 = r14.documentsPathsArray;	 Catch:{ Exception -> 0x051c }
        r4.add(r5);	 Catch:{ Exception -> 0x051c }
        r4 = r14.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x051c }
        r4.add(r3);	 Catch:{ Exception -> 0x051c }
        goto L_0x0517;	 Catch:{ Exception -> 0x051c }
    L_0x0505:
        r3 = r14.documentsUrisArray;	 Catch:{ Exception -> 0x051c }
        if (r3 != 0) goto L_0x0510;	 Catch:{ Exception -> 0x051c }
    L_0x0509:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x051c }
        r3.<init>();	 Catch:{ Exception -> 0x051c }
        r14.documentsUrisArray = r3;	 Catch:{ Exception -> 0x051c }
    L_0x0510:
        r3 = r14.documentsUrisArray;	 Catch:{ Exception -> 0x051c }
        r3.add(r4);	 Catch:{ Exception -> 0x051c }
        r14.documentsMimeType = r1;	 Catch:{ Exception -> 0x051c }
    L_0x0517:
        r2 = r2 + 1;
        goto L_0x04b0;
    L_0x051a:
        r13 = 0;
        goto L_0x0522;
    L_0x051c:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x0521:
        r13 = 1;
    L_0x0522:
        if (r13 == 0) goto L_0x0415;
    L_0x0524:
        r1 = "Unsupported content";
        r2 = 0;
        r1 = android.widget.Toast.makeText(r14, r1, r2);
        r1.show();
        goto L_0x0415;
    L_0x0530:
        r1 = "android.intent.action.VIEW";
        r2 = r44.getAction();
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x0a68;
    L_0x053c:
        r1 = r44.getData();
        if (r1 == 0) goto L_0x0a4b;
    L_0x0542:
        r2 = r1.getScheme();
        if (r2 == 0) goto L_0x0941;
    L_0x0548:
        r3 = "http";
        r3 = r2.equals(r3);
        r4 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r3 != 0) goto L_0x079e;
    L_0x0552:
        r3 = "https";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x055c;
    L_0x055a:
        goto L_0x079e;
    L_0x055c:
        r3 = "tg";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0941;
    L_0x0564:
        r1 = r1.toString();
        r2 = "tg:resolve";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0749;
    L_0x0570:
        r2 = "tg://resolve";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x057a;
    L_0x0578:
        goto L_0x0749;
    L_0x057a:
        r2 = "tg:join";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0720;
    L_0x0582:
        r2 = "tg://join";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x058c;
    L_0x058a:
        goto L_0x0720;
    L_0x058c:
        r2 = "tg:addstickers";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x06f7;
    L_0x0594:
        r2 = "tg://addstickers";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x059e;
    L_0x059c:
        goto L_0x06f7;
    L_0x059e:
        r2 = "tg:msg";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0666;
    L_0x05a6:
        r2 = "tg://msg";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0666;
    L_0x05ae:
        r2 = "tg://share";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0666;
    L_0x05b6:
        r2 = "tg:share";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x05c0;
    L_0x05be:
        goto L_0x0666;
    L_0x05c0:
        r2 = "tg:confirmphone";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x0640;
    L_0x05c8:
        r2 = "tg://confirmphone";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x05d2;
    L_0x05d0:
        goto L_0x0640;
    L_0x05d2:
        r2 = "tg:openmessage";
        r2 = r1.startsWith(r2);
        if (r2 != 0) goto L_0x05e2;
    L_0x05da:
        r2 = "tg://openmessage";
        r2 = r1.startsWith(r2);
        if (r2 == 0) goto L_0x0941;
    L_0x05e2:
        r2 = "tg:openmessage";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://openmessage";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "user_id";
        r2 = r1.getQueryParameter(r2);
        r3 = "chat_id";
        r3 = r1.getQueryParameter(r3);
        r4 = "message_id";
        r1 = r1.getQueryParameter(r4);
        if (r2 == 0) goto L_0x0614;
    L_0x060a:
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x0622 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ NumberFormatException -> 0x0622 }
        r3 = r2;	 Catch:{ NumberFormatException -> 0x0622 }
        goto L_0x0624;	 Catch:{ NumberFormatException -> 0x0622 }
    L_0x0614:
        if (r3 == 0) goto L_0x0622;	 Catch:{ NumberFormatException -> 0x0622 }
    L_0x0616:
        r2 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x0622 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ NumberFormatException -> 0x0622 }
        r4 = r2;
        r3 = r26;
        goto L_0x0626;
    L_0x0622:
        r3 = r26;
    L_0x0624:
        r4 = r27;
    L_0x0626:
        if (r1 == 0) goto L_0x0637;
    L_0x0628:
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ NumberFormatException -> 0x0637 }
        r5 = java.lang.Integer.valueOf(r1);	 Catch:{ NumberFormatException -> 0x0637 }
        r26 = r3;
        r27 = r4;
        r28 = r5;
        goto L_0x063b;
    L_0x0637:
        r26 = r3;
        r27 = r4;
    L_0x063b:
        r1 = 0;
        r3 = 0;
        r4 = 0;
        goto L_0x073d;
    L_0x0640:
        r2 = "tg:confirmphone";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://confirmphone";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "phone";
        r13 = r1.getQueryParameter(r2);
        r2 = "hash";
        r1 = r1.getQueryParameter(r2);
        r6 = r13;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        goto L_0x073f;
    L_0x0666:
        r2 = "tg:msg";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://msg";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://share";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg:share";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "url";
        r2 = r1.getQueryParameter(r2);
        if (r2 != 0) goto L_0x0694;
    L_0x0692:
        r2 = "";
    L_0x0694:
        r3 = "text";
        r3 = r1.getQueryParameter(r3);
        if (r3 == 0) goto L_0x06cc;
    L_0x069c:
        r3 = r2.length();
        if (r3 <= 0) goto L_0x06b5;
    L_0x06a2:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r2 = "\n";
        r3.append(r2);
        r2 = r3.toString();
        r12 = 1;
        goto L_0x06b6;
    L_0x06b5:
        r12 = 0;
    L_0x06b6:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r2 = "text";
        r1 = r1.getQueryParameter(r2);
        r3.append(r1);
        r2 = r3.toString();
        goto L_0x06cd;
    L_0x06cc:
        r12 = 0;
    L_0x06cd:
        r1 = r2.length();
        if (r1 <= r4) goto L_0x06d9;
    L_0x06d3:
        r1 = 0;
        r2 = r2.substring(r1, r4);
        goto L_0x06da;
    L_0x06d9:
        r1 = 0;
    L_0x06da:
        r13 = r2;
    L_0x06db:
        r2 = "\n";
        r2 = r13.endsWith(r2);
        if (r2 == 0) goto L_0x06ee;
    L_0x06e3:
        r2 = r13.length();
        r3 = 1;
        r2 = r2 - r3;
        r13 = r13.substring(r1, r2);
        goto L_0x06db;
    L_0x06ee:
        r9 = r12;
        r5 = r13;
        r1 = 0;
        r3 = 0;
        r4 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        goto L_0x0742;
    L_0x06f7:
        r2 = "tg:addstickers";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://addstickers";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "set";
        r1 = r1.getQueryParameter(r2);
        r23 = r1;
        r1 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r13 = 0;
        goto L_0x094e;
    L_0x0720:
        r2 = "tg:join";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://join";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "invite";
        r1 = r1.getQueryParameter(r2);
        r4 = r1;
        r1 = 0;
        r3 = 0;
    L_0x073d:
        r5 = 0;
        r6 = 0;
    L_0x073f:
        r7 = 0;
        r8 = 0;
        r9 = 0;
    L_0x0742:
        r10 = 0;
        r12 = 0;
        r13 = 0;
        r23 = 0;
        goto L_0x094e;
    L_0x0749:
        r2 = "tg:resolve";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r2 = "tg://resolve";
        r3 = "tg://telegram.org";
        r1 = r1.replace(r2, r3);
        r1 = android.net.Uri.parse(r1);
        r2 = "domain";
        r2 = r1.getQueryParameter(r2);
        r3 = "start";
        r3 = r1.getQueryParameter(r3);
        r4 = "startgroup";
        r4 = r1.getQueryParameter(r4);
        r5 = "game";
        r5 = r1.getQueryParameter(r5);
        r6 = "post";
        r1 = r1.getQueryParameter(r6);
        r1 = org.telegram.messenger.Utilities.parseInt(r1);
        r6 = r1.intValue();
        if (r6 != 0) goto L_0x078f;
    L_0x0785:
        r7 = r3;
        r8 = r4;
        r12 = r5;
        r1 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r9 = 0;
        r10 = 0;
        goto L_0x0798;
    L_0x078f:
        r10 = r1;
        r7 = r3;
        r8 = r4;
        r12 = r5;
        r1 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r9 = 0;
    L_0x0798:
        r13 = 0;
        r23 = 0;
        r3 = r2;
        goto L_0x094e;
    L_0x079e:
        r2 = r1.getHost();
        r2 = r2.toLowerCase();
        r3 = "telegram.me";
        r3 = r2.equals(r3);
        if (r3 != 0) goto L_0x07c6;
    L_0x07ae:
        r3 = "t.me";
        r3 = r2.equals(r3);
        if (r3 != 0) goto L_0x07c6;
    L_0x07b6:
        r3 = "telegram.dog";
        r3 = r2.equals(r3);
        if (r3 != 0) goto L_0x07c6;
    L_0x07be:
        r3 = "telesco.pe";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0925;
    L_0x07c6:
        r2 = r1.getPath();
        if (r2 == 0) goto L_0x0925;
    L_0x07cc:
        r3 = r2.length();
        r5 = 1;
        if (r3 <= r5) goto L_0x0925;
    L_0x07d3:
        r2 = r2.substring(r5);
        r3 = "joinchat/";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x07f6;
    L_0x07df:
        r1 = "joinchat/";
        r3 = "";
        r13 = r2.replace(r1, r3);
        r1 = r13;
        r2 = 0;
    L_0x07e9:
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r13 = 0;
        r23 = 0;
        goto L_0x0932;
    L_0x07f6:
        r3 = "addstickers/";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x0809;
    L_0x07fe:
        r1 = "addstickers/";
        r3 = "";
        r13 = r2.replace(r1, r3);
        r2 = r13;
        r1 = 0;
        goto L_0x07e9;
    L_0x0809:
        r3 = "iv/";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x0834;
    L_0x0811:
        r2 = "url";
        r2 = r1.getQueryParameter(r2);
        r3 = 0;
        r13 = 0;
        r13[r3] = r2;
        r2 = "rhash";
        r1 = r1.getQueryParameter(r2);
        r2 = 1;
        r13[r2] = r1;
        r1 = r13[r3];
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0926;
    L_0x082c:
        r1 = r13[r2];
        r1 = android.text.TextUtils.isEmpty(r1);
        goto L_0x0926;
    L_0x0834:
        r13 = 0;
        r3 = "msg/";
        r3 = r2.startsWith(r3);
        if (r3 != 0) goto L_0x08b5;
    L_0x083d:
        r3 = "share/";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x0847;
    L_0x0845:
        goto L_0x08b5;
    L_0x0847:
        r3 = "confirmphone";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x0866;
    L_0x084f:
        r2 = "phone";
        r2 = r1.getQueryParameter(r2);
        r3 = "hash";
        r1 = r1.getQueryParameter(r3);
        r8 = r1;
        r6 = r2;
        r1 = r13;
        r2 = r1;
        r3 = r2;
        r4 = r3;
        r5 = r4;
        r7 = r5;
        r9 = r7;
        goto L_0x092f;
    L_0x0866:
        r2 = r2.length();
        r3 = 1;
        if (r2 < r3) goto L_0x0926;
    L_0x086d:
        r2 = r1.getPathSegments();
        r4 = r2.size();
        if (r4 <= 0) goto L_0x0896;
    L_0x0877:
        r4 = 0;
        r5 = r2.get(r4);
        r5 = (java.lang.String) r5;
        r4 = r2.size();
        if (r4 <= r3) goto L_0x0894;
    L_0x0884:
        r2 = r2.get(r3);
        r2 = (java.lang.String) r2;
        r2 = org.telegram.messenger.Utilities.parseInt(r2);
        r3 = r2.intValue();
        if (r3 != 0) goto L_0x0898;
    L_0x0894:
        r2 = r13;
        goto L_0x0898;
    L_0x0896:
        r2 = r13;
        r5 = r2;
    L_0x0898:
        r3 = "start";
        r3 = r1.getQueryParameter(r3);
        r4 = "startgroup";
        r4 = r1.getQueryParameter(r4);
        r6 = "game";
        r1 = r1.getQueryParameter(r6);
        r7 = r1;
        r9 = r2;
        r23 = r5;
        r1 = r13;
        r2 = r1;
        r5 = r2;
        r6 = r5;
        r8 = r6;
        goto L_0x0931;
    L_0x08b5:
        r2 = "url";
        r2 = r1.getQueryParameter(r2);
        if (r2 != 0) goto L_0x08bf;
    L_0x08bd:
        r2 = "";
    L_0x08bf:
        r3 = "text";
        r3 = r1.getQueryParameter(r3);
        if (r3 == 0) goto L_0x08f8;
    L_0x08c7:
        r3 = r2.length();
        if (r3 <= 0) goto L_0x08e0;
    L_0x08cd:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r2 = "\n";
        r3.append(r2);
        r2 = r3.toString();
        r3 = 1;
        goto L_0x08e1;
    L_0x08e0:
        r3 = 0;
    L_0x08e1:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = "text";
        r1 = r1.getQueryParameter(r2);
        r5.append(r1);
        r2 = r5.toString();
        r12 = r3;
        goto L_0x08f9;
    L_0x08f8:
        r12 = 0;
    L_0x08f9:
        r1 = r2.length();
        if (r1 <= r4) goto L_0x0905;
    L_0x08ff:
        r1 = 0;
        r2 = r2.substring(r1, r4);
        goto L_0x0906;
    L_0x0905:
        r1 = 0;
    L_0x0906:
        r3 = "\n";
        r3 = r2.endsWith(r3);
        if (r3 == 0) goto L_0x0919;
    L_0x090e:
        r3 = r2.length();
        r4 = 1;
        r3 = r3 - r4;
        r2 = r2.substring(r1, r3);
        goto L_0x0906;
    L_0x0919:
        r5 = r2;
        r1 = r13;
        r2 = r1;
        r3 = r2;
        r4 = r3;
        r6 = r4;
        r7 = r6;
        r8 = r7;
        r9 = r8;
        r23 = r9;
        goto L_0x0932;
    L_0x0925:
        r13 = 0;
    L_0x0926:
        r1 = r13;
        r2 = r1;
        r3 = r2;
        r4 = r3;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r8 = r7;
        r9 = r8;
    L_0x092f:
        r23 = r9;
    L_0x0931:
        r12 = 0;
    L_0x0932:
        r10 = r9;
        r9 = r12;
        r12 = r7;
        r7 = r3;
        r3 = r23;
        r23 = r2;
        r42 = r4;
        r4 = r1;
        r1 = r8;
        r8 = r42;
        goto L_0x094e;
    L_0x0941:
        r13 = 0;
        r1 = r13;
        r3 = r1;
        r4 = r3;
        r5 = r4;
        r6 = r5;
        r7 = r6;
        r8 = r7;
        r10 = r8;
        r12 = r10;
        r23 = r12;
        r9 = 0;
    L_0x094e:
        r24 = 0;
        if (r5 == 0) goto L_0x096d;
    L_0x0952:
        r2 = "@";
        r2 = r5.startsWith(r2);
        if (r2 == 0) goto L_0x096d;
    L_0x095a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r13 = " ";
        r2.append(r13);
        r2.append(r5);
        r2 = r2.toString();
        r13 = r2;
        goto L_0x096e;
    L_0x096d:
        r13 = r5;
    L_0x096e:
        if (r6 != 0) goto L_0x0a2d;
    L_0x0970:
        if (r1 == 0) goto L_0x0974;
    L_0x0972:
        goto L_0x0a2d;
    L_0x0974:
        if (r3 != 0) goto L_0x0a0a;
    L_0x0976:
        if (r4 != 0) goto L_0x0a0a;
    L_0x0978:
        if (r23 != 0) goto L_0x0a0a;
    L_0x097a:
        if (r13 != 0) goto L_0x0a0a;
    L_0x097c:
        if (r12 != 0) goto L_0x0a0a;
    L_0x097e:
        r32 = r43.getContentResolver();	 Catch:{ Exception -> 0x09f6 }
        r33 = r44.getData();	 Catch:{ Exception -> 0x09f6 }
        r34 = 0;	 Catch:{ Exception -> 0x09f6 }
        r35 = 0;	 Catch:{ Exception -> 0x09f6 }
        r36 = 0;	 Catch:{ Exception -> 0x09f6 }
        r37 = 0;	 Catch:{ Exception -> 0x09f6 }
        r1 = r32.query(r33, r34, r35, r36, r37);	 Catch:{ Exception -> 0x09f6 }
        if (r1 == 0) goto L_0x09f3;	 Catch:{ Exception -> 0x09f6 }
    L_0x0994:
        r2 = r1.moveToFirst();	 Catch:{ Exception -> 0x09f6 }
        if (r2 == 0) goto L_0x09eb;	 Catch:{ Exception -> 0x09f6 }
    L_0x099a:
        r2 = "account_name";	 Catch:{ Exception -> 0x09f6 }
        r2 = r1.getColumnIndex(r2);	 Catch:{ Exception -> 0x09f6 }
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x09f6 }
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Exception -> 0x09f6 }
        r2 = r2.intValue();	 Catch:{ Exception -> 0x09f6 }
        r3 = 0;
        r6 = 3;
    L_0x09ae:
        if (r3 >= r6) goto L_0x09cb;
    L_0x09b0:
        r4 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x09c8 }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x09c8 }
        if (r4 != r2) goto L_0x09c4;	 Catch:{ Exception -> 0x09c8 }
    L_0x09ba:
        r4 = 0;	 Catch:{ Exception -> 0x09c8 }
        r11[r4] = r3;	 Catch:{ Exception -> 0x09c8 }
        r2 = r11[r4];	 Catch:{ Exception -> 0x09c8 }
        r5 = 1;
        r14.switchToAccount(r2, r5);	 Catch:{ Exception -> 0x09f1 }
        goto L_0x09cc;	 Catch:{ Exception -> 0x09f1 }
    L_0x09c4:
        r5 = 1;	 Catch:{ Exception -> 0x09f1 }
        r3 = r3 + 1;	 Catch:{ Exception -> 0x09f1 }
        goto L_0x09ae;	 Catch:{ Exception -> 0x09f1 }
    L_0x09c8:
        r0 = move-exception;	 Catch:{ Exception -> 0x09f1 }
        r5 = 1;	 Catch:{ Exception -> 0x09f1 }
        goto L_0x09f9;	 Catch:{ Exception -> 0x09f1 }
    L_0x09cb:
        r5 = 1;	 Catch:{ Exception -> 0x09f1 }
    L_0x09cc:
        r2 = "DATA4";	 Catch:{ Exception -> 0x09f1 }
        r2 = r1.getColumnIndex(r2);	 Catch:{ Exception -> 0x09f1 }
        r2 = r1.getInt(r2);	 Catch:{ Exception -> 0x09f1 }
        r3 = 0;	 Catch:{ Exception -> 0x09f1 }
        r4 = r11[r3];	 Catch:{ Exception -> 0x09f1 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x09f1 }
        r7 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ Exception -> 0x09f1 }
        r8 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x09f1 }
        r4.postNotificationName(r7, r8);	 Catch:{ Exception -> 0x09f1 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x09f1 }
        r26 = r2;	 Catch:{ Exception -> 0x09f1 }
        goto L_0x09ed;	 Catch:{ Exception -> 0x09f1 }
    L_0x09eb:
        r5 = 1;	 Catch:{ Exception -> 0x09f1 }
        r6 = 3;	 Catch:{ Exception -> 0x09f1 }
    L_0x09ed:
        r1.close();	 Catch:{ Exception -> 0x09f1 }
        goto L_0x09fd;
    L_0x09f1:
        r0 = move-exception;
        goto L_0x09f9;
    L_0x09f3:
        r5 = 1;
        r6 = 3;
        goto L_0x09fd;
    L_0x09f6:
        r0 = move-exception;
        r5 = 1;
        r6 = 3;
    L_0x09f9:
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x09fd:
        r40 = r11;
        r3 = r26;
        r4 = r27;
        r5 = r28;
        r38 = r29;
        r20 = 0;
        goto L_0x0a57;
    L_0x0a0a:
        r5 = 1;
        r6 = 3;
        r19 = 0;
        r2 = r11[r19];
        r22 = 0;
        r1 = r14;
        r25 = r5;
        r5 = r23;
        r23 = r6;
        r38 = r29;
        r6 = r7;
        r7 = r8;
        r20 = 0;
        r8 = r13;
        r13 = r23;
        r40 = r11;
        r11 = r12;
        r12 = r24;
        r13 = r22;
        r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x0a51;
    L_0x0a2d:
        r40 = r11;
        r38 = r29;
        r20 = 0;
        r2 = new android.os.Bundle;
        r2.<init>();
        r3 = "phone";
        r2.putString(r3, r6);
        r3 = "hash";
        r2.putString(r3, r1);
        r1 = new org.telegram.ui.LaunchActivity$7;
        r1.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
        goto L_0x0a51;
    L_0x0a4b:
        r40 = r11;
        r38 = r29;
        r20 = 0;
    L_0x0a51:
        r3 = r26;
        r4 = r27;
        r5 = r28;
    L_0x0a57:
        r2 = r3;
        r7 = r16;
        r10 = r17;
        r11 = r18;
        r6 = r40;
        r1 = 1;
    L_0x0a61:
        r3 = 0;
        r8 = 0;
        r9 = 0;
        r41 = 0;
        goto L_0x0b57;
    L_0x0a68:
        r40 = r11;
        r38 = r29;
        r20 = 0;
        r1 = r44.getAction();
        r2 = "org.telegram.messenger.OPEN_ACCOUNT";
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x0a8e;
    L_0x0a7a:
        r1 = 1;
        r17 = java.lang.Integer.valueOf(r1);
    L_0x0a7f:
        r7 = r16;
        r10 = r17;
        r11 = r18;
        r2 = r26;
        r4 = r27;
        r5 = r28;
        r6 = r40;
        goto L_0x0a61;
    L_0x0a8e:
        r1 = 1;
        r2 = r44.getAction();
        r3 = "new_dialog";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0aa0;
    L_0x0a9b:
        r18 = java.lang.Integer.valueOf(r1);
        goto L_0x0a7f;
    L_0x0aa0:
        r2 = r44.getAction();
        r3 = "com.tmessages.openchat";
        r2 = r2.startsWith(r3);
        if (r2 == 0) goto L_0x0b17;
    L_0x0aac:
        r2 = "chatId";
        r3 = 0;
        r2 = r15.getIntExtra(r2, r3);
        r4 = "userId";
        r4 = r15.getIntExtra(r4, r3);
        r5 = "encId";
        r5 = r15.getIntExtra(r5, r3);
        if (r2 == 0) goto L_0x0ad7;
    L_0x0ac1:
        r6 = r40;
        r4 = r6[r3];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r4.postNotificationName(r5, r7);
        r2 = java.lang.Integer.valueOf(r2);
        r4 = r2;
        r12 = r3;
        goto L_0x0b0a;
    L_0x0ad7:
        r6 = r40;
        if (r4 == 0) goto L_0x0af2;
    L_0x0adb:
        r2 = r6[r3];
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r2.postNotificationName(r5, r7);
        r2 = java.lang.Integer.valueOf(r4);
        r26 = r2;
    L_0x0aee:
        r12 = r3;
    L_0x0aef:
        r4 = r27;
        goto L_0x0b0a;
    L_0x0af2:
        if (r5 == 0) goto L_0x0b08;
    L_0x0af4:
        r2 = r6[r3];
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r7 = new java.lang.Object[r3];
        r2.postNotificationName(r4, r7);
        r2 = java.lang.Integer.valueOf(r5);
        r16 = r2;
        goto L_0x0aee;
    L_0x0b08:
        r12 = r1;
        goto L_0x0aef;
    L_0x0b0a:
        r8 = r3;
        r9 = r8;
        r41 = r12;
        r7 = r16;
        r10 = r17;
        r11 = r18;
        r2 = r26;
        goto L_0x0b55;
    L_0x0b17:
        r6 = r40;
        r3 = 0;
        r2 = r44.getAction();
        r4 = "com.tmessages.openplayer";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0b29;
    L_0x0b26:
        r8 = r1;
        r9 = r3;
        goto L_0x0b49;
    L_0x0b29:
        r2 = r44.getAction();
        r4 = "org.tmessages.openlocations";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0b47;
    L_0x0b35:
        r9 = r1;
        r8 = r3;
        r41 = r8;
        goto L_0x0b4b;
    L_0x0b3a:
        r26 = r3;
        r27 = r4;
        r28 = r5;
        r38 = r6;
        r6 = r11;
        r3 = r12;
        r1 = 1;
    L_0x0b45:
        r20 = 0;
    L_0x0b47:
        r8 = r3;
        r9 = r8;
    L_0x0b49:
        r41 = r9;
    L_0x0b4b:
        r7 = r16;
        r10 = r17;
        r11 = r18;
        r2 = r26;
        r4 = r27;
    L_0x0b55:
        r5 = r28;
    L_0x0b57:
        r12 = r2.intValue();
        if (r12 == 0) goto L_0x0bb2;
    L_0x0b5d:
        r4 = new android.os.Bundle;
        r4.<init>();
        r7 = "user_id";
        r2 = r2.intValue();
        r4.putInt(r7, r2);
        r2 = r5.intValue();
        if (r2 == 0) goto L_0x0b7a;
    L_0x0b71:
        r2 = "message_id";
        r5 = r5.intValue();
        r4.putInt(r2, r5);
    L_0x0b7a:
        r2 = mainFragmentsStack;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0b9d;
    L_0x0b82:
        r2 = r6[r3];
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r5 = mainFragmentsStack;
        r6 = mainFragmentsStack;
        r6 = r6.size();
        r6 = r6 - r1;
        r5 = r5.get(r6);
        r5 = (org.telegram.ui.ActionBar.BaseFragment) r5;
        r2 = r2.checkCanOpenChat(r4, r5);
        if (r2 == 0) goto L_0x0bac;
    L_0x0b9d:
        r2 = new org.telegram.ui.ChatActivity;
        r2.<init>(r4);
        r4 = r14.actionBarLayout;
        r2 = r4.presentFragment(r2, r3, r1, r1);
        if (r2 == 0) goto L_0x0bac;
    L_0x0baa:
        r13 = r1;
        goto L_0x0bad;
    L_0x0bac:
        r13 = r3;
    L_0x0bad:
        r2 = r45;
    L_0x0baf:
        r4 = 0;
        goto L_0x0e61;
    L_0x0bb2:
        r2 = r4.intValue();
        if (r2 == 0) goto L_0x0c06;
    L_0x0bb8:
        r2 = new android.os.Bundle;
        r2.<init>();
        r7 = "chat_id";
        r4 = r4.intValue();
        r2.putInt(r7, r4);
        r4 = r5.intValue();
        if (r4 == 0) goto L_0x0bd5;
    L_0x0bcc:
        r4 = "message_id";
        r5 = r5.intValue();
        r2.putInt(r4, r5);
    L_0x0bd5:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0bf8;
    L_0x0bdd:
        r4 = r6[r3];
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r5 = mainFragmentsStack;
        r6 = mainFragmentsStack;
        r6 = r6.size();
        r6 = r6 - r1;
        r5 = r5.get(r6);
        r5 = (org.telegram.ui.ActionBar.BaseFragment) r5;
        r4 = r4.checkCanOpenChat(r2, r5);
        if (r4 == 0) goto L_0x0bac;
    L_0x0bf8:
        r4 = new org.telegram.ui.ChatActivity;
        r4.<init>(r2);
        r2 = r14.actionBarLayout;
        r2 = r2.presentFragment(r4, r3, r1, r1);
        if (r2 == 0) goto L_0x0bac;
    L_0x0c05:
        goto L_0x0baa;
    L_0x0c06:
        r2 = r7.intValue();
        if (r2 == 0) goto L_0x0c26;
    L_0x0c0c:
        r2 = new android.os.Bundle;
        r2.<init>();
        r4 = "enc_id";
        r5 = r7.intValue();
        r2.putInt(r4, r5);
        r4 = new org.telegram.ui.ChatActivity;
        r4.<init>(r2);
        r2 = r14.actionBarLayout;
        r13 = r2.presentFragment(r4, r3, r1, r1);
        goto L_0x0bad;
    L_0x0c26:
        if (r41 == 0) goto L_0x0c62;
    L_0x0c28:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 != 0) goto L_0x0c34;
    L_0x0c2e:
        r2 = r14.actionBarLayout;
        r2.removeAllFragments();
        goto L_0x0c5e;
    L_0x0c34:
        r2 = r14.layersActionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0c5e;
    L_0x0c3e:
        r2 = r14.layersActionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.size();
        r2 = r2 - r1;
        if (r2 <= 0) goto L_0x0c59;
    L_0x0c49:
        r2 = r14.layersActionBarLayout;
        r4 = r14.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.get(r3);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r2.removeFragmentFromStack(r4);
        goto L_0x0c3e;
    L_0x0c59:
        r2 = r14.layersActionBarLayout;
        r2.closeLastFragment(r3);
    L_0x0c5e:
        r2 = r3;
        r13 = r2;
        goto L_0x0baf;
    L_0x0c62:
        if (r8 == 0) goto L_0x0c85;
    L_0x0c64:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0c80;
    L_0x0c6e:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.get(r3);
        r2 = (org.telegram.ui.ActionBar.BaseFragment) r2;
        r4 = new org.telegram.ui.Components.AudioPlayerAlert;
        r4.<init>(r14);
        r2.showDialog(r4);
    L_0x0c80:
        r2 = r45;
        r13 = r3;
        goto L_0x0baf;
    L_0x0c85:
        if (r9 == 0) goto L_0x0ca9;
    L_0x0c87:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0c80;
    L_0x0c91:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.get(r3);
        r2 = (org.telegram.ui.ActionBar.BaseFragment) r2;
        r4 = new org.telegram.ui.Components.SharingLocationsAlert;
        r5 = new org.telegram.ui.LaunchActivity$8;
        r5.<init>(r6);
        r4.<init>(r14, r5);
        r2.showDialog(r4);
        goto L_0x0c80;
    L_0x0ca9:
        r2 = r14.videoPath;
        if (r2 != 0) goto L_0x0d2b;
    L_0x0cad:
        r2 = r14.photoPathsArray;
        if (r2 != 0) goto L_0x0d2b;
    L_0x0cb1:
        r2 = r14.sendingText;
        if (r2 != 0) goto L_0x0d2b;
    L_0x0cb5:
        r2 = r14.documentsPathsArray;
        if (r2 != 0) goto L_0x0d2b;
    L_0x0cb9:
        r2 = r14.contactsToSend;
        if (r2 != 0) goto L_0x0d2b;
    L_0x0cbd:
        r2 = r14.documentsUrisArray;
        if (r2 == 0) goto L_0x0cc2;
    L_0x0cc1:
        goto L_0x0d2b;
    L_0x0cc2:
        r2 = r10.intValue();
        if (r2 == 0) goto L_0x0cf2;
    L_0x0cc8:
        r2 = r14.actionBarLayout;
        r4 = new org.telegram.ui.SettingsActivity;
        r4.<init>();
        r2.presentFragment(r4, r3, r1, r1);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0ce8;
    L_0x0cd8:
        r2 = r14.actionBarLayout;
        r2.showLastFragment();
        r2 = r14.rightActionBarLayout;
        r2.showLastFragment();
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r3, r3);
        goto L_0x0ced;
    L_0x0ce8:
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r3);
    L_0x0ced:
        r2 = r45;
        r13 = r1;
        goto L_0x0baf;
    L_0x0cf2:
        r2 = r11.intValue();
        if (r2 == 0) goto L_0x0d28;
    L_0x0cf8:
        r2 = new android.os.Bundle;
        r2.<init>();
        r4 = "destroyAfterSelect";
        r2.putBoolean(r4, r1);
        r4 = r14.actionBarLayout;
        r5 = new org.telegram.ui.ContactsActivity;
        r5.<init>(r2);
        r4.presentFragment(r5, r3, r1, r1);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0d22;
    L_0x0d12:
        r2 = r14.actionBarLayout;
        r2.showLastFragment();
        r2 = r14.rightActionBarLayout;
        r2.showLastFragment();
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r3, r3);
        goto L_0x0ced;
    L_0x0d22:
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r3);
        goto L_0x0ced;
    L_0x0d28:
        r4 = 0;
        goto L_0x0e5e;
    L_0x0d2b:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 != 0) goto L_0x0d3e;
    L_0x0d31:
        r2 = r6[r3];
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r5 = new java.lang.Object[r3];
        r2.postNotificationName(r4, r5);
    L_0x0d3e:
        r6 = r38;
        r2 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1));
        if (r2 != 0) goto L_0x0e4e;
    L_0x0d44:
        r2 = new android.os.Bundle;
        r2.<init>();
        r4 = "onlySelect";
        r2.putBoolean(r4, r1);
        r4 = "dialogsType";
        r5 = 3;
        r2.putInt(r4, r5);
        r4 = "allowSwitchAccount";
        r2.putBoolean(r4, r1);
        r4 = r14.contactsToSend;
        r5 = NUM; // 0x7f0c05cd float:1.8612204E38 double:1.053098132E-314;
        if (r4 == 0) goto L_0x0d7a;
    L_0x0d60:
        r4 = "selectAlertString";
        r6 = "SendContactTo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r2.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendContactToGroup";
        r6 = NUM; // 0x7f0c05c0 float:1.8612178E38 double:1.0530981257E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r2.putString(r4, r5);
        goto L_0x0d93;
    L_0x0d7a:
        r4 = "selectAlertString";
        r6 = "SendMessagesTo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r2.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendMessagesToGroup";
        r6 = NUM; // 0x7f0c05ce float:1.8612206E38 double:1.0530981326E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r2.putString(r4, r5);
    L_0x0d93:
        r4 = new org.telegram.ui.DialogsActivity;
        r4.<init>(r2);
        r4.setDelegate(r14);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0dc4;
    L_0x0da1:
        r2 = r14.layersActionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x0dc2;
    L_0x0dab:
        r2 = r14.layersActionBarLayout;
        r2 = r2.fragmentsStack;
        r5 = r14.layersActionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 - r1;
        r2 = r2.get(r5);
        r2 = r2 instanceof org.telegram.ui.DialogsActivity;
        if (r2 == 0) goto L_0x0dc2;
    L_0x0dc0:
        r2 = r1;
        goto L_0x0de4;
    L_0x0dc2:
        r2 = r3;
        goto L_0x0de4;
    L_0x0dc4:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.size();
        if (r2 <= r1) goto L_0x0dc2;
    L_0x0dce:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r5 = r14.actionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 - r1;
        r2 = r2.get(r5);
        r2 = r2 instanceof org.telegram.ui.DialogsActivity;
        if (r2 == 0) goto L_0x0dc2;
    L_0x0de3:
        goto L_0x0dc0;
    L_0x0de4:
        r5 = r14.actionBarLayout;
        r5.presentFragment(r4, r2, r1, r1);
        r2 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r2 == 0) goto L_0x0e01;
    L_0x0def:
        r2 = org.telegram.ui.SecretMediaViewer.getInstance();
        r2 = r2.isVisible();
        if (r2 == 0) goto L_0x0e01;
    L_0x0df9:
        r2 = org.telegram.ui.SecretMediaViewer.getInstance();
        r2.closePhoto(r3, r3);
        goto L_0x0e30;
    L_0x0e01:
        r2 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r2 == 0) goto L_0x0e19;
    L_0x0e07:
        r2 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r2.isVisible();
        if (r2 == 0) goto L_0x0e19;
    L_0x0e11:
        r2 = org.telegram.ui.PhotoViewer.getInstance();
        r2.closePhoto(r3, r1);
        goto L_0x0e30;
    L_0x0e19:
        r2 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r2 == 0) goto L_0x0e30;
    L_0x0e1f:
        r2 = org.telegram.ui.ArticleViewer.getInstance();
        r2 = r2.isVisible();
        if (r2 == 0) goto L_0x0e30;
    L_0x0e29:
        r2 = org.telegram.ui.ArticleViewer.getInstance();
        r2.close(r3, r1);
    L_0x0e30:
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r3, r3);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0e47;
    L_0x0e3b:
        r2 = r14.actionBarLayout;
        r2.showLastFragment();
        r2 = r14.rightActionBarLayout;
        r2.showLastFragment();
        goto L_0x0ced;
    L_0x0e47:
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r3);
        goto L_0x0ced;
    L_0x0e4e:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r4 = java.lang.Long.valueOf(r6);
        r2.add(r4);
        r4 = 0;
        r14.didSelectDialogs(r4, r2, r4, r3);
    L_0x0e5e:
        r2 = r45;
        r13 = r3;
    L_0x0e61:
        if (r13 != 0) goto L_0x0eff;
    L_0x0e63:
        if (r2 != 0) goto L_0x0eff;
    L_0x0e65:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0eb0;
    L_0x0e6b:
        r2 = r14.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.isClientActivated();
        if (r2 != 0) goto L_0x0e91;
    L_0x0e77:
        r1 = r14.layersActionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x0eea;
    L_0x0e81:
        r1 = r14.layersActionBarLayout;
        r2 = new org.telegram.ui.LoginActivity;
        r2.<init>();
        r1.addFragmentToStack(r2);
        r1 = r14.drawerLayoutContainer;
        r1.setAllowOpenDrawer(r3, r3);
        goto L_0x0eea;
    L_0x0e91:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 == 0) goto L_0x0eea;
    L_0x0e9b:
        r2 = new org.telegram.ui.DialogsActivity;
        r2.<init>(r4);
        r5 = r14.sideMenu;
        r2.setSideMenu(r5);
        r5 = r14.actionBarLayout;
        r5.addFragmentToStack(r2);
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r3);
        goto L_0x0eea;
    L_0x0eb0:
        r2 = r14.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.isEmpty();
        if (r2 == 0) goto L_0x0eea;
    L_0x0eba:
        r2 = r14.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.isClientActivated();
        if (r2 != 0) goto L_0x0ed6;
    L_0x0ec6:
        r1 = r14.actionBarLayout;
        r2 = new org.telegram.ui.LoginActivity;
        r2.<init>();
        r1.addFragmentToStack(r2);
        r1 = r14.drawerLayoutContainer;
        r1.setAllowOpenDrawer(r3, r3);
        goto L_0x0eea;
    L_0x0ed6:
        r2 = new org.telegram.ui.DialogsActivity;
        r2.<init>(r4);
        r5 = r14.sideMenu;
        r2.setSideMenu(r5);
        r5 = r14.actionBarLayout;
        r5.addFragmentToStack(r2);
        r2 = r14.drawerLayoutContainer;
        r2.setAllowOpenDrawer(r1, r3);
    L_0x0eea:
        r1 = r14.actionBarLayout;
        r1.showLastFragment();
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r1 == 0) goto L_0x0eff;
    L_0x0ef5:
        r1 = r14.layersActionBarLayout;
        r1.showLastFragment();
        r1 = r14.rightActionBarLayout;
        r1.showLastFragment();
    L_0x0eff:
        r15.setAction(r4);
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    private void runLinkRequest(int r20, java.lang.String r21, java.lang.String r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, boolean r27, java.lang.Integer r28, java.lang.String r29, java.lang.String[] r30, int r31) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r19 = this;
        r14 = r19;
        r15 = r20;
        r5 = r21;
        r4 = r22;
        r6 = r23;
        r9 = r26;
        r0 = r31;
        r13 = new org.telegram.ui.ActionBar.AlertDialog;
        r1 = 1;
        r13.<init>(r14, r1);
        r2 = "Loading";
        r3 = NUM; // 0x7f0c0382 float:1.8611013E38 double:1.053097842E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r13.setMessage(r2);
        r2 = 0;
        r13.setCanceledOnTouchOutside(r2);
        r13.setCancelable(r2);
        if (r5 == 0) goto L_0x004e;
    L_0x0029:
        r8 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
        r8.<init>();
        r8.username = r5;
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r20);
        r10 = new org.telegram.ui.LaunchActivity$9;
        r0 = r10;
        r1 = r14;
        r2 = r13;
        r3 = r29;
        r4 = r15;
        r5 = r25;
        r6 = r24;
        r7 = r28;
        r0.<init>(r2, r3, r4, r5, r6, r7);
        r2 = r9.sendRequest(r8, r10);
        r5 = r13;
        r7 = r14;
        r3 = r15;
        goto L_0x00fc;
    L_0x004e:
        if (r4 == 0) goto L_0x00ac;
    L_0x0050:
        r12 = 2;
        if (r0 != 0) goto L_0x008d;
    L_0x0053:
        r11 = new org.telegram.tgnet.TLRPC$TL_messages_checkChatInvite;
        r11.<init>();
        r11.hash = r4;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r20);
        r8 = new org.telegram.ui.LaunchActivity$10;
        r0 = r8;
        r1 = r14;
        r2 = r13;
        r3 = r15;
        r7 = r24;
        r14 = r8;
        r8 = r25;
        r15 = r10;
        r10 = r27;
        r16 = r14;
        r14 = r11;
        r11 = r28;
        r17 = r14;
        r14 = r12;
        r12 = r29;
        r18 = r13;
        r13 = r30;
        r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r1 = r16;
        r0 = r17;
        r2 = r15.sendRequest(r0, r1, r14);
    L_0x0085:
        r5 = r18;
        r3 = r20;
        r7 = r19;
        goto L_0x00fc;
    L_0x008d:
        r14 = r12;
        r18 = r13;
        if (r0 != r1) goto L_0x0085;
    L_0x0092:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
        r0.<init>();
        r0.hash = r4;
        r3 = r20;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r20);
        r4 = new org.telegram.ui.LaunchActivity$11;
        r5 = r18;
        r7 = r19;
        r4.<init>(r3, r5);
        r1.sendRequest(r0, r4, r14);
        goto L_0x00fc;
    L_0x00ac:
        r5 = r13;
        r7 = r14;
        r3 = r15;
        if (r6 == 0) goto L_0x00de;
    L_0x00b1:
        r0 = mainFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x00dd;
    L_0x00b9:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
        r3.<init>();
        r3.short_name = r6;
        r0 = mainFragmentsStack;
        r2 = mainFragmentsStack;
        r2 = r2.size();
        r2 = r2 - r1;
        r0 = r0.get(r2);
        r6 = r0;
        r6 = (org.telegram.ui.ActionBar.BaseFragment) r6;
        r8 = new org.telegram.ui.Components.StickersAlert;
        r4 = 0;
        r5 = 0;
        r0 = r8;
        r1 = r7;
        r2 = r6;
        r0.<init>(r1, r2, r3, r4, r5);
        r6.showDialog(r8);
    L_0x00dd:
        return;
    L_0x00de:
        if (r9 == 0) goto L_0x00fc;
    L_0x00e0:
        r0 = new android.os.Bundle;
        r0.<init>();
        r4 = "onlySelect";
        r0.putBoolean(r4, r1);
        r4 = new org.telegram.ui.DialogsActivity;
        r4.<init>(r0);
        r0 = new org.telegram.ui.LaunchActivity$12;
        r6 = r27;
        r0.<init>(r6, r3, r9);
        r4.setDelegate(r0);
        r7.presentFragment(r4, r2, r1);
    L_0x00fc:
        if (r2 == 0) goto L_0x0113;
    L_0x00fe:
        r0 = -2;
        r1 = "Cancel";
        r4 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r4);
        r4 = new org.telegram.ui.LaunchActivity$13;
        r4.<init>(r3, r2);
        r5.setButton(r0, r1, r4);
        r5.show();	 Catch:{ Exception -> 0x0113 }
    L_0x0113:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.String, java.lang.String[], int):void");
    }

    public AlertDialog showAlertDialog(Builder builder) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    if (LaunchActivity.this.visibleDialog != null && LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                        try {
                            Toast.makeText(LaunchActivity.this, LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") != null ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", C0446R.string.ChangeLanguageLater), 1).show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        LaunchActivity.this.localeDialog = null;
                    }
                    LaunchActivity.this.visibleDialog = null;
                }
            });
            return this.visibleDialog;
        } catch (Throwable e2) {
            FileLog.m3e(e2);
            return null;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        arrayList = (int) longValue;
        z = (int) (longValue >> true);
        Bundle bundle = new Bundle();
        int currentAccount = dialogsActivity != null ? dialogsActivity.getCurrentAccount() : this.currentAccount;
        bundle.putBoolean("scrollToTopOnResume", true);
        if (!AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        if (arrayList == null) {
            bundle.putInt("enc_id", z);
        } else if (z) {
            bundle.putInt("chat_id", arrayList);
        } else if (arrayList > null) {
            bundle.putInt("user_id", arrayList);
        } else if (arrayList < null) {
            bundle.putInt("chat_id", -arrayList);
        }
        if (MessagesController.getInstance(currentAccount).checkCanOpenChat(bundle, dialogsActivity) != null) {
            arrayList = new ChatActivity(bundle);
            this.actionBarLayout.presentFragment(arrayList, dialogsActivity != null, dialogsActivity == null ? 1 : null, true);
            if (this.videoPath != null) {
                arrayList.openVideoEditor(this.videoPath, this.sendingText);
                this.sendingText = null;
            }
            if (this.photoPathsArray != null) {
                if (this.sendingText != null && this.sendingText.length() <= 200 && this.photoPathsArray.size() == 1) {
                    ((SendingMediaInfo) this.photoPathsArray.get(0)).caption = this.sendingText;
                    this.sendingText = null;
                }
                SendMessagesHelper.prepareSendingMedia(this.photoPathsArray, longValue, null, null, false, false);
            }
            if (this.sendingText != null) {
                SendMessagesHelper.prepareSendingText(this.sendingText, longValue);
            }
            if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, longValue, null, null);
            }
            if (this.contactsToSend != null && this.contactsToSend.isEmpty() == null) {
                dialogsActivity = this.contactsToSend.iterator();
                while (dialogsActivity.hasNext() != null) {
                    SendMessagesHelper.getInstance(currentAccount).sendMessage((User) dialogsActivity.next(), longValue, null, null, null);
                }
            }
            this.photoPathsArray = null;
            this.videoPath = null;
            this.sendingText = null;
            this.documentsPathsArray = null;
            this.documentsOriginalPathsArray = null;
            this.contactsToSend = null;
        }
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
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            }
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

    public void presentFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true);
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

    protected void onActivityResult(int i, int i2, Intent intent) {
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        super.onActivityResult(i, i2, intent);
        ThemeEditorView instance = ThemeEditorView.getInstance();
        if (instance != null) {
            instance.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        int i2 = 0;
        if (!(i == 3 || i == 4 || i == 5 || i == 19)) {
            if (i != 20) {
                if (i == 2 && iArr.length > 0 && iArr[0] == 0) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
                }
                if (this.actionBarLayout.fragmentsStack.size() != 0) {
                    ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                        ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                    if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                        ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                }
            }
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            if (i == 4) {
                ImageLoader.getInstance().checkMediaPaths();
                return;
            } else if (i == 5) {
                ContactsController.getInstance(this.currentAccount).forceImportContacts();
                return;
            } else if (i == 3) {
                if (SharedConfig.inappCamera != 0) {
                    CameraController.getInstance().initCamera();
                }
                return;
            } else {
                if (i != 19) {
                    if (i == 20) {
                    }
                }
                if (i2 != 0) {
                    strArr = new Builder((Context) this);
                    strArr.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    if (i == 3) {
                        strArr.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
                    } else if (i == 4) {
                        strArr.setMessage(LocaleController.getString("PermissionStorage", NUM));
                    } else if (i != 5) {
                        strArr.setMessage(LocaleController.getString("PermissionContacts", NUM));
                    } else if (i == 19 || i == 20) {
                        strArr.setMessage(LocaleController.getString("PermissionNoCamera", NUM));
                    }
                    strArr.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                        @TargetApi(9)
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                i = new StringBuilder();
                                i.append("package:");
                                i.append(ApplicationLoader.applicationContext.getPackageName());
                                dialogInterface.setData(Uri.parse(i.toString()));
                                LaunchActivity.this.startActivity(dialogInterface);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    });
                    strArr.setPositiveButton(LocaleController.getString("OK", NUM), null);
                    strArr.show();
                    return;
                }
                if (this.actionBarLayout.fragmentsStack.size() != 0) {
                    ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                        ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                    if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                        ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                }
            }
        }
        i2 = 1;
        if (i2 != 0) {
            strArr = new Builder((Context) this);
            strArr.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (i == 3) {
                strArr.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
            } else if (i == 4) {
                strArr.setMessage(LocaleController.getString("PermissionStorage", NUM));
            } else if (i != 5) {
                strArr.setMessage(LocaleController.getString("PermissionNoCamera", NUM));
            } else {
                strArr.setMessage(LocaleController.getString("PermissionContacts", NUM));
            }
            strArr.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), /* anonymous class already generated */);
            strArr.setPositiveButton(LocaleController.getString("OK", NUM), null);
            strArr.show();
            return;
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
        }
    }

    protected void onPause() {
        super.onPause();
        SharedConfig.lastAppPauseTime = System.currentTimeMillis();
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                ApplicationLoader.mainInterfacePausedStageQueue = true;
                ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
            }
        });
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        showLanguageAlert(false);
        ApplicationLoader.mainInterfacePaused = false;
        NotificationsController.lastNoDataNotificationTime = 0;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                ApplicationLoader.mainInterfacePausedStageQueue = false;
                ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
            }
        });
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
        configuration = ThemeEditorView.getInstance();
        if (configuration != null) {
            configuration.onConfigurationChanged();
        }
    }

    public void onMultiWindowModeChanged(boolean z) {
        AndroidUtilities.isInMultiwindow = z;
        checkLayout();
    }

    public void didReceivedNotification(int r18, int r19, java.lang.Object... r20) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r17 = this;
        r7 = r17;
        r1 = r18;
        r8 = r19;
        r3 = org.telegram.messenger.NotificationCenter.appDidLogout;
        if (r1 != r3) goto L_0x000f;
    L_0x000a:
        r17.switchToAvailableAccountOrLogout();
        goto L_0x0312;
    L_0x000f:
        r3 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r9 = 0;
        if (r1 != r3) goto L_0x0020;
    L_0x0014:
        r1 = r20[r9];
        if (r1 == r7) goto L_0x0312;
    L_0x0018:
        r17.onFinish();
        r17.finish();
        goto L_0x0312;
    L_0x0020:
        r3 = org.telegram.messenger.NotificationCenter.didUpdatedConnectionState;
        if (r1 != r3) goto L_0x004f;
    L_0x0024:
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r19);
        r1 = r1.getConnectionState();
        r2 = r7.currentConnectionState;
        if (r2 == r1) goto L_0x0312;
    L_0x0030:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0048;
    L_0x0034:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "switch to state ";
        r2.append(r3);
        r2.append(r1);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.m0d(r2);
    L_0x0048:
        r7.currentConnectionState = r1;
        r7.updateCurrentConnectionState(r8);
        goto L_0x0312;
    L_0x004f:
        r3 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged;
        if (r1 != r3) goto L_0x005a;
    L_0x0053:
        r1 = r7.drawerLayoutAdapter;
        r1.notifyDataSetChanged();
        goto L_0x0312;
    L_0x005a:
        r3 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r4 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        r5 = 2;
        r6 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r10 = 0;
        r11 = 1;
        if (r1 != r3) goto L_0x00ec;
    L_0x0067:
        r1 = r20[r9];
        r1 = (java.lang.Integer) r1;
        r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r3.<init>(r7);
        r9 = "AppName";
        r4 = org.telegram.messenger.LocaleController.getString(r9, r4);
        r3.setTitle(r4);
        r4 = "OK";
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r3.setPositiveButton(r4, r10);
        r4 = r1.intValue();
        if (r4 == r5) goto L_0x0099;
    L_0x0088:
        r4 = "MoreInfo";
        r6 = NUM; // 0x7f0c03df float:1.8611202E38 double:1.053097888E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r6 = new org.telegram.ui.LaunchActivity$18;
        r6.<init>(r8);
        r3.setNegativeButton(r4, r6);
    L_0x0099:
        r4 = r1.intValue();
        if (r4 != 0) goto L_0x00ac;
    L_0x009f:
        r1 = "NobodyLikesSpam1";
        r2 = NUM; // 0x7f0c041d float:1.8611328E38 double:1.0530979187E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r3.setMessage(r1);
        goto L_0x00cc;
    L_0x00ac:
        r4 = r1.intValue();
        if (r4 != r11) goto L_0x00bf;
    L_0x00b2:
        r1 = "NobodyLikesSpam2";
        r2 = NUM; // 0x7f0c041e float:1.861133E38 double:1.053097919E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r3.setMessage(r1);
        goto L_0x00cc;
    L_0x00bf:
        r1 = r1.intValue();
        if (r1 != r5) goto L_0x00cc;
    L_0x00c5:
        r1 = r20[r11];
        r1 = (java.lang.String) r1;
        r3.setMessage(r1);
    L_0x00cc:
        r1 = mainFragmentsStack;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x0312;
    L_0x00d4:
        r1 = mainFragmentsStack;
        r2 = mainFragmentsStack;
        r2 = r2.size();
        r2 = r2 - r11;
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        r2 = r3.create();
        r1.showDialog(r2);
        goto L_0x0312;
    L_0x00ec:
        r3 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation;
        if (r1 != r3) goto L_0x0148;
    L_0x00f0:
        r1 = r20[r9];
        r1 = (java.util.HashMap) r1;
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2.<init>(r7);
        r3 = "AppName";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r2.setTitle(r3);
        r3 = "OK";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r6);
        r2.setPositiveButton(r3, r10);
        r3 = "ShareYouLocationUnableManually";
        r4 = NUM; // 0x7f0c05f9 float:1.8612293E38 double:1.053098154E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r4 = new org.telegram.ui.LaunchActivity$19;
        r4.<init>(r1, r8);
        r2.setNegativeButton(r3, r4);
        r1 = "ShareYouLocationUnable";
        r3 = NUM; // 0x7f0c05f8 float:1.8612291E38 double:1.0530981534E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r3);
        r2.setMessage(r1);
        r1 = mainFragmentsStack;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x0312;
    L_0x0130:
        r1 = mainFragmentsStack;
        r3 = mainFragmentsStack;
        r3 = r3.size();
        r3 = r3 - r11;
        r1 = r1.get(r3);
        r1 = (org.telegram.ui.ActionBar.BaseFragment) r1;
        r2 = r2.create();
        r1.showDialog(r2);
        goto L_0x0312;
    L_0x0148:
        r3 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper;
        if (r1 != r3) goto L_0x015d;
    L_0x014c:
        r1 = r7.sideMenu;
        if (r1 == 0) goto L_0x0312;
    L_0x0150:
        r1 = r7.sideMenu;
        r1 = r1.getChildAt(r9);
        if (r1 == 0) goto L_0x0312;
    L_0x0158:
        r1.invalidate();
        goto L_0x0312;
    L_0x015d:
        r3 = org.telegram.messenger.NotificationCenter.didSetPasscode;
        if (r1 != r3) goto L_0x018f;
    L_0x0161:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;
        r1 = r1.length();
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        if (r1 <= 0) goto L_0x017f;
    L_0x016b:
        r1 = org.telegram.messenger.SharedConfig.allowScreenCapture;
        if (r1 != 0) goto L_0x017f;
    L_0x016f:
        r1 = r17.getWindow();	 Catch:{ Exception -> 0x0178 }
        r1.setFlags(r2, r2);	 Catch:{ Exception -> 0x0178 }
        goto L_0x0312;
    L_0x0178:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        goto L_0x0312;
    L_0x017f:
        r1 = r17.getWindow();	 Catch:{ Exception -> 0x0188 }
        r1.clearFlags(r2);	 Catch:{ Exception -> 0x0188 }
        goto L_0x0312;
    L_0x0188:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        goto L_0x0312;
    L_0x018f:
        r3 = org.telegram.messenger.NotificationCenter.reloadInterface;
        if (r1 != r3) goto L_0x0198;
    L_0x0193:
        r7.rebuildAllFragments(r9);
        goto L_0x0312;
    L_0x0198:
        r3 = org.telegram.messenger.NotificationCenter.suggestedLangpack;
        if (r1 != r3) goto L_0x01a1;
    L_0x019c:
        r7.showLanguageAlert(r9);
        goto L_0x0312;
    L_0x01a1:
        r3 = org.telegram.messenger.NotificationCenter.openArticle;
        if (r1 != r3) goto L_0x01d5;
    L_0x01a5:
        r1 = mainFragmentsStack;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x01ae;
    L_0x01ad:
        return;
    L_0x01ae:
        r1 = org.telegram.ui.ArticleViewer.getInstance();
        r3 = mainFragmentsStack;
        r4 = mainFragmentsStack;
        r4 = r4.size();
        r4 = r4 - r11;
        r3 = r3.get(r4);
        r3 = (org.telegram.ui.ActionBar.BaseFragment) r3;
        r1.setParentActivity(r7, r3);
        r1 = org.telegram.ui.ArticleViewer.getInstance();
        r3 = r20[r9];
        r3 = (org.telegram.tgnet.TLRPC.TL_webPage) r3;
        r2 = r20[r11];
        r2 = (java.lang.String) r2;
        r1.open(r3, r2);
        goto L_0x0312;
    L_0x01d5:
        r3 = org.telegram.messenger.NotificationCenter.hasNewContactsToImport;
        if (r1 != r3) goto L_0x0276;
    L_0x01d9:
        r1 = r7.actionBarLayout;
        if (r1 == 0) goto L_0x0275;
    L_0x01dd:
        r1 = r7.actionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x01e9;
    L_0x01e7:
        goto L_0x0275;
    L_0x01e9:
        r1 = r20[r9];
        r1 = (java.lang.Integer) r1;
        r1.intValue();
        r1 = r20[r11];
        r10 = r1;
        r10 = (java.util.HashMap) r10;
        r1 = r20[r5];
        r1 = (java.lang.Boolean) r1;
        r12 = r1.booleanValue();
        r1 = 3;
        r1 = r20[r1];
        r1 = (java.lang.Boolean) r1;
        r13 = r1.booleanValue();
        r1 = r7.actionBarLayout;
        r1 = r1.fragmentsStack;
        r2 = r7.actionBarLayout;
        r2 = r2.fragmentsStack;
        r2 = r2.size();
        r2 = r2 - r11;
        r1 = r1.get(r2);
        r11 = r1;
        r11 = (org.telegram.ui.ActionBar.BaseFragment) r11;
        r14 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r14.<init>(r7);
        r1 = "UpdateContactsTitle";
        r2 = NUM; // 0x7f0c0678 float:1.861255E38 double:1.0530982166E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r14.setTitle(r1);
        r1 = "UpdateContactsMessage";
        r2 = NUM; // 0x7f0c0677 float:1.8612549E38 double:1.053098216E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r14.setMessage(r1);
        r1 = "OK";
        r15 = org.telegram.messenger.LocaleController.getString(r1, r6);
        r6 = new org.telegram.ui.LaunchActivity$20;
        r1 = r6;
        r2 = r7;
        r3 = r8;
        r4 = r10;
        r5 = r12;
        r9 = r6;
        r6 = r13;
        r1.<init>(r3, r4, r5, r6);
        r14.setPositiveButton(r15, r9);
        r1 = "Cancel";
        r2 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r15 = new org.telegram.ui.LaunchActivity$21;
        r1 = r15;
        r2 = r7;
        r1.<init>(r3, r4, r5, r6);
        r14.setNegativeButton(r9, r15);
        r9 = new org.telegram.ui.LaunchActivity$22;
        r1 = r9;
        r1.<init>(r3, r4, r5, r6);
        r14.setOnBackButtonListener(r9);
        r1 = r14.create();
        r11.showDialog(r1);
        r3 = 0;
        r1.setCanceledOnTouchOutside(r3);
        goto L_0x0312;
    L_0x0275:
        return;
    L_0x0276:
        r3 = r9;
        r4 = org.telegram.messenger.NotificationCenter.didSetNewTheme;
        if (r1 != r4) goto L_0x02c0;
    L_0x027b:
        r1 = r20[r3];
        r1 = (java.lang.Boolean) r1;
        r1 = r1.booleanValue();
        if (r1 != 0) goto L_0x0312;
    L_0x0285:
        r1 = r7.sideMenu;
        if (r1 == 0) goto L_0x02a8;
    L_0x0289:
        r1 = r7.sideMenu;
        r2 = "chats_menuBackground";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setBackgroundColor(r2);
        r1 = r7.sideMenu;
        r2 = "chats_menuBackground";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setGlowColor(r2);
        r1 = r7.sideMenu;
        r1 = r1.getAdapter();
        r1.notifyDataSetChanged();
    L_0x02a8:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 < r2) goto L_0x0312;
    L_0x02ae:
        r1 = new android.app.ActivityManager$TaskDescription;	 Catch:{ Exception -> 0x0312 }
        r2 = "actionBarDefault";	 Catch:{ Exception -> 0x0312 }
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);	 Catch:{ Exception -> 0x0312 }
        r3 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;	 Catch:{ Exception -> 0x0312 }
        r2 = r2 | r3;	 Catch:{ Exception -> 0x0312 }
        r1.<init>(r10, r10, r2);	 Catch:{ Exception -> 0x0312 }
        r7.setTaskDescription(r1);	 Catch:{ Exception -> 0x0312 }
        goto L_0x0312;
    L_0x02c0:
        r3 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        if (r1 != r3) goto L_0x02df;
    L_0x02c4:
        r3 = 0;
        r1 = r20[r3];
        r1 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r1;
        r2 = r7.actionBarLayout;
        r2.animateThemedValues(r1);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0312;
    L_0x02d4:
        r2 = r7.layersActionBarLayout;
        r2.animateThemedValues(r1);
        r2 = r7.rightActionBarLayout;
        r2.animateThemedValues(r1);
        goto L_0x0312;
    L_0x02df:
        r3 = org.telegram.messenger.NotificationCenter.notificationsCountUpdated;
        if (r1 != r3) goto L_0x0312;
    L_0x02e3:
        r1 = r7.sideMenu;
        if (r1 == 0) goto L_0x0312;
    L_0x02e7:
        r3 = 0;
        r1 = r20[r3];
        r1 = (java.lang.Integer) r1;
        r2 = r7.sideMenu;
        r2 = r2.getChildCount();
    L_0x02f2:
        if (r3 >= r2) goto L_0x0312;
    L_0x02f4:
        r4 = r7.sideMenu;
        r4 = r4.getChildAt(r3);
        r5 = r4 instanceof org.telegram.ui.Cells.DrawerUserCell;
        if (r5 == 0) goto L_0x030f;
    L_0x02fe:
        r5 = r4;
        r5 = (org.telegram.ui.Cells.DrawerUserCell) r5;
        r5 = r5.getAccountNumber();
        r6 = r1.intValue();
        if (r5 != r6) goto L_0x030f;
    L_0x030b:
        r4.invalidate();
        goto L_0x0312;
    L_0x030f:
        r3 = r3 + 1;
        goto L_0x02f2;
    L_0x0312:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = (String) hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void checkFreeDiscSpace() {
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.LaunchActivity$23$1 */
                class C14541 implements Runnable {
                    C14541() {
                    }

                    public void run() {
                        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                        /*
                        r1 = this;
                        r0 = org.telegram.ui.LaunchActivity.AnonymousClass23.this;	 Catch:{ Throwable -> 0x000b }
                        r0 = org.telegram.ui.LaunchActivity.this;	 Catch:{ Throwable -> 0x000b }
                        r0 = org.telegram.ui.Components.AlertsCreator.createFreeSpaceDialog(r0);	 Catch:{ Throwable -> 0x000b }
                        r0.show();	 Catch:{ Throwable -> 0x000b }
                    L_0x000b:
                        return;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.23.1.run():void");
                    }
                }

                public void run() {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r7 = this;
                    r0 = org.telegram.ui.LaunchActivity.this;
                    r0 = r0.currentAccount;
                    r0 = org.telegram.messenger.UserConfig.getInstance(r0);
                    r0 = r0.isClientActivated();
                    if (r0 != 0) goto L_0x0011;
                L_0x0010:
                    return;
                L_0x0011:
                    r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Throwable -> 0x007d }
                    r1 = "last_space_check";	 Catch:{ Throwable -> 0x007d }
                    r2 = 0;	 Catch:{ Throwable -> 0x007d }
                    r1 = r0.getLong(r1, r2);	 Catch:{ Throwable -> 0x007d }
                    r3 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x007d }
                    r5 = r1 - r3;	 Catch:{ Throwable -> 0x007d }
                    r1 = java.lang.Math.abs(r5);	 Catch:{ Throwable -> 0x007d }
                    r3 = 259200000; // 0xf731400 float:1.1984677E-29 double:1.280618154E-315;	 Catch:{ Throwable -> 0x007d }
                    r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ Throwable -> 0x007d }
                    if (r5 < 0) goto L_0x007d;	 Catch:{ Throwable -> 0x007d }
                L_0x002e:
                    r1 = 4;	 Catch:{ Throwable -> 0x007d }
                    r1 = org.telegram.messenger.FileLoader.getDirectory(r1);	 Catch:{ Throwable -> 0x007d }
                    if (r1 != 0) goto L_0x0036;	 Catch:{ Throwable -> 0x007d }
                L_0x0035:
                    return;	 Catch:{ Throwable -> 0x007d }
                L_0x0036:
                    r2 = new android.os.StatFs;	 Catch:{ Throwable -> 0x007d }
                    r1 = r1.getAbsolutePath();	 Catch:{ Throwable -> 0x007d }
                    r2.<init>(r1);	 Catch:{ Throwable -> 0x007d }
                    r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x007d }
                    r3 = 18;	 Catch:{ Throwable -> 0x007d }
                    if (r1 >= r3) goto L_0x0054;	 Catch:{ Throwable -> 0x007d }
                L_0x0045:
                    r1 = r2.getAvailableBlocks();	 Catch:{ Throwable -> 0x007d }
                    r2 = r2.getBlockSize();	 Catch:{ Throwable -> 0x007d }
                    r1 = r1 * r2;	 Catch:{ Throwable -> 0x007d }
                    r1 = java.lang.Math.abs(r1);	 Catch:{ Throwable -> 0x007d }
                    r1 = (long) r1;	 Catch:{ Throwable -> 0x007d }
                    goto L_0x005d;	 Catch:{ Throwable -> 0x007d }
                L_0x0054:
                    r3 = r2.getAvailableBlocksLong();	 Catch:{ Throwable -> 0x007d }
                    r1 = r2.getBlockSizeLong();	 Catch:{ Throwable -> 0x007d }
                    r1 = r1 * r3;	 Catch:{ Throwable -> 0x007d }
                L_0x005d:
                    r0 = r0.edit();	 Catch:{ Throwable -> 0x007d }
                    r3 = "last_space_check";	 Catch:{ Throwable -> 0x007d }
                    r4 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x007d }
                    r0 = r0.putLong(r3, r4);	 Catch:{ Throwable -> 0x007d }
                    r0.commit();	 Catch:{ Throwable -> 0x007d }
                    r3 = 104857600; // 0x6400000 float:3.6111186E-35 double:5.1806538E-316;	 Catch:{ Throwable -> 0x007d }
                    r0 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ Throwable -> 0x007d }
                    if (r0 >= 0) goto L_0x007d;	 Catch:{ Throwable -> 0x007d }
                L_0x0075:
                    r0 = new org.telegram.ui.LaunchActivity$23$1;	 Catch:{ Throwable -> 0x007d }
                    r0.<init>();	 Catch:{ Throwable -> 0x007d }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Throwable -> 0x007d }
                L_0x007d:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.23.run():void");
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    private void showLanguageAlertInternal(LocaleInfo localeInfo, LocaleInfo localeInfo2, String str) {
        try {
            boolean z;
            Builder builder;
            View linearLayout;
            final LanguageCell[] languageCellArr;
            final LocaleInfo[] localeInfoArr;
            LocaleInfo[] localeInfoArr2;
            String stringForLanguageAlert;
            int i;
            View languageCell;
            this.loadingLocaleDialog = false;
            LocaleInfo localeInfo3 = localeInfo;
            if (!localeInfo3.builtIn) {
                if (!LocaleController.getInstance().isCurrentLocalLocale()) {
                    z = false;
                    builder = new Builder(r1);
                    builder.setTitle(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguage", C0446R.string.ChooseYourLanguage));
                    builder.setSubtitle(getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguage", C0446R.string.ChooseYourLanguage));
                    linearLayout = new LinearLayout(r1);
                    linearLayout.setOrientation(1);
                    languageCellArr = new LanguageCell[2];
                    localeInfoArr = new LocaleInfo[1];
                    localeInfoArr2 = new LocaleInfo[2];
                    stringForLanguageAlert = getStringForLanguageAlert(r1.systemLocaleStrings, "English", C0446R.string.English);
                    localeInfoArr2[0] = z ? localeInfo3 : localeInfo2;
                    localeInfoArr2[1] = z ? localeInfo2 : localeInfo3;
                    if (z) {
                        localeInfo3 = localeInfo2;
                    }
                    localeInfoArr[0] = localeInfo3;
                    i = 0;
                    while (i < 2) {
                        languageCellArr[i] = new LanguageCell(r1, true);
                        languageCellArr[i].setLanguage(localeInfoArr2[i], localeInfoArr2[i] != localeInfo2 ? stringForLanguageAlert : null, true);
                        languageCellArr[i].setTag(Integer.valueOf(i));
                        languageCellArr[i].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                        languageCellArr[i].setLanguageSelected(i != 0);
                        linearLayout.addView(languageCellArr[i], LayoutHelper.createLinear(-1, 48));
                        languageCellArr[i].setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                Integer num = (Integer) view.getTag();
                                localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
                                view = null;
                                while (view < languageCellArr.length) {
                                    languageCellArr[view].setLanguageSelected(view == num.intValue());
                                    view++;
                                }
                            }
                        });
                        i++;
                    }
                    languageCell = new LanguageCell(r1, true);
                    languageCell.setValue(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguageOther", C0446R.string.ChooseYourLanguageOther), getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguageOther", C0446R.string.ChooseYourLanguageOther));
                    languageCell.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            LaunchActivity.this.localeDialog = null;
                            LaunchActivity.this.drawerLayoutContainer.closeDrawer(true);
                            LaunchActivity.this.presentFragment(new LanguageSelectActivity());
                            if (LaunchActivity.this.visibleDialog != null) {
                                LaunchActivity.this.visibleDialog.dismiss();
                                LaunchActivity.this.visibleDialog = null;
                            }
                        }
                    });
                    linearLayout.addView(languageCell, LayoutHelper.createLinear(-1, 48));
                    builder.setView(linearLayout);
                    builder.setNegativeButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, LaunchActivity.this.currentAccount);
                            LaunchActivity.this.rebuildAllFragments(true);
                        }
                    });
                    r1.localeDialog = showAlertDialog(builder);
                    MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str).commit();
                }
            }
            z = true;
            builder = new Builder(r1);
            builder.setTitle(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguage", C0446R.string.ChooseYourLanguage));
            builder.setSubtitle(getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguage", C0446R.string.ChooseYourLanguage));
            linearLayout = new LinearLayout(r1);
            linearLayout.setOrientation(1);
            languageCellArr = new LanguageCell[2];
            localeInfoArr = new LocaleInfo[1];
            localeInfoArr2 = new LocaleInfo[2];
            stringForLanguageAlert = getStringForLanguageAlert(r1.systemLocaleStrings, "English", C0446R.string.English);
            if (z) {
            }
            localeInfoArr2[0] = z ? localeInfo3 : localeInfo2;
            if (z) {
            }
            localeInfoArr2[1] = z ? localeInfo2 : localeInfo3;
            if (z) {
                localeInfo3 = localeInfo2;
            }
            localeInfoArr[0] = localeInfo3;
            i = 0;
            while (i < 2) {
                languageCellArr[i] = new LanguageCell(r1, true);
                if (localeInfoArr2[i] != localeInfo2) {
                }
                languageCellArr[i].setLanguage(localeInfoArr2[i], localeInfoArr2[i] != localeInfo2 ? stringForLanguageAlert : null, true);
                languageCellArr[i].setTag(Integer.valueOf(i));
                languageCellArr[i].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                if (i != 0) {
                }
                languageCellArr[i].setLanguageSelected(i != 0);
                linearLayout.addView(languageCellArr[i], LayoutHelper.createLinear(-1, 48));
                languageCellArr[i].setOnClickListener(/* anonymous class already generated */);
                i++;
            }
            languageCell = new LanguageCell(r1, true);
            languageCell.setValue(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguageOther", C0446R.string.ChooseYourLanguageOther), getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguageOther", C0446R.string.ChooseYourLanguageOther));
            languageCell.setOnClickListener(/* anonymous class already generated */);
            linearLayout.addView(languageCell, LayoutHelper.createLinear(-1, 48));
            builder.setView(linearLayout);
            builder.setNegativeButton(LocaleController.getString("OK", C0446R.string.OK), /* anonymous class already generated */);
            r1.localeDialog = showAlertDialog(builder);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str).commit();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void showLanguageAlert(boolean z) {
        try {
            if (!this.loadingLocaleDialog) {
                String string = MessagesController.getGlobalMainSettings().getString("language_showed2", TtmlNode.ANONYMOUS_REGION_ID);
                final String toLowerCase = LocaleController.getSystemLocaleStringIso639().toLowerCase();
                if (z || !string.equals(toLowerCase)) {
                    z = new LocaleInfo[true];
                    Object obj = toLowerCase.contains("-") ? toLowerCase.split("-")[0] : toLowerCase;
                    Object obj2 = "in".equals(obj) ? TtmlNode.ATTR_ID : "iw".equals(obj) ? "he" : "jw".equals(obj) ? "jv" : null;
                    for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
                        LocaleInfo localeInfo = (LocaleInfo) LocaleController.getInstance().languages.get(i);
                        if (localeInfo.shortName.equals("en")) {
                            z[0] = localeInfo;
                        }
                        if (localeInfo.shortName.replace("_", "-").equals(toLowerCase) || localeInfo.shortName.equals(obj) || (r3 != null && localeInfo.shortName.equals(r3))) {
                            z[1] = localeInfo;
                        }
                        if (z[0] != null && z[1] != null) {
                            break;
                        }
                    }
                    if (!(z[0] == null || z[1] == null)) {
                        if (z[0] != z[1]) {
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("show lang alert for ");
                                stringBuilder.append(z[0].getKey());
                                stringBuilder.append(" and ");
                                stringBuilder.append(z[1].getKey());
                                FileLog.m0d(stringBuilder.toString());
                            }
                            this.systemLocaleStrings = null;
                            this.englishLocaleStrings = null;
                            this.loadingLocaleDialog = true;
                            TLObject tL_langpack_getStrings = new TL_langpack_getStrings();
                            tL_langpack_getStrings.lang_code = z[1].shortName.replace("_", "-");
                            tL_langpack_getStrings.keys.add("English");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguage");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                            tL_langpack_getStrings.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    tL_error = new HashMap();
                                    if (tLObject != null) {
                                        Vector vector = (Vector) tLObject;
                                        for (int i = 0; i < vector.objects.size(); i++) {
                                            LangPackString langPackString = (LangPackString) vector.objects.get(i);
                                            tL_error.put(langPackString.key, langPackString.value);
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            LaunchActivity.this.systemLocaleStrings = tL_error;
                                            if (LaunchActivity.this.englishLocaleStrings != null && LaunchActivity.this.systemLocaleStrings != null) {
                                                LaunchActivity.this.showLanguageAlertInternal(z[1], z[0], toLowerCase);
                                            }
                                        }
                                    });
                                }
                            }, 8);
                            tL_langpack_getStrings = new TL_langpack_getStrings();
                            tL_langpack_getStrings.lang_code = z[0].shortName.replace("_", "-");
                            tL_langpack_getStrings.keys.add("English");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguage");
                            tL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                            tL_langpack_getStrings.keys.add("ChangeLanguageLater");
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    tL_error = new HashMap();
                                    if (tLObject != null) {
                                        Vector vector = (Vector) tLObject;
                                        for (int i = 0; i < vector.objects.size(); i++) {
                                            LangPackString langPackString = (LangPackString) vector.objects.get(i);
                                            tL_error.put(langPackString.key, langPackString.value);
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            LaunchActivity.this.englishLocaleStrings = tL_error;
                                            if (LaunchActivity.this.englishLocaleStrings != null && LaunchActivity.this.systemLocaleStrings != null) {
                                                LaunchActivity.this.showLanguageAlertInternal(z[1], z[0], toLowerCase);
                                            }
                                        }
                                    });
                                }
                            }, 8);
                            return;
                        }
                    }
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    z = new StringBuilder();
                    z.append("alert already showed for ");
                    z.append(string);
                    FileLog.m0d(z.toString());
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                                FileLog.m0d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("didn't pass lock check");
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

    private void updateCurrentConnectionState(int i) {
        if (this.actionBarLayout != 0) {
            Runnable anonymousClass30;
            String str = null;
            if (this.currentConnectionState == 2) {
                i = LocaleController.getString("WaitingForNetwork", C0446R.string.WaitingForNetwork);
            } else {
                if (this.currentConnectionState == 1) {
                    i = LocaleController.getString("Connecting", C0446R.string.Connecting);
                    anonymousClass30 = new Runnable() {
                        public void run() {
                            if (AndroidUtilities.isTablet()) {
                                if (!LaunchActivity.layerFragmentsStack.isEmpty() && (LaunchActivity.layerFragmentsStack.get(LaunchActivity.layerFragmentsStack.size() - 1) instanceof ProxySettingsActivity)) {
                                    return;
                                }
                            } else if (!LaunchActivity.mainFragmentsStack.isEmpty() && (LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1) instanceof ProxySettingsActivity)) {
                                return;
                            }
                            LaunchActivity.this.presentFragment(new ProxySettingsActivity());
                        }
                    };
                } else if (this.currentConnectionState == 5) {
                    i = LocaleController.getString("Updating", C0446R.string.Updating);
                } else if (this.currentConnectionState == 4) {
                    str = LocaleController.getString("ConnectingToProxy", C0446R.string.ConnectingToProxy);
                    i = LocaleController.getString("ConnectingToProxyTapToDisable", C0446R.string.ConnectingToProxyTapToDisable);
                    anonymousClass30 = new Runnable() {

                        /* renamed from: org.telegram.ui.LaunchActivity$31$1 */
                        class C14581 implements DialogInterface.OnClickListener {
                            C14581() {
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface = MessagesController.getGlobalMainSettings().edit();
                                dialogInterface.putBoolean("proxy_enabled", false);
                                dialogInterface.commit();
                                for (dialogInterface = null; dialogInterface < 3; dialogInterface++) {
                                    ConnectionsManager.native_setProxySettings(dialogInterface, TtmlNode.ANONYMOUS_REGION_ID, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
                                }
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                            }
                        }

                        public void run() {
                            if (LaunchActivity.this.actionBarLayout != null) {
                                if (!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) {
                                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                                    BaseFragment baseFragment = (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1);
                                    Builder builder = new Builder(LaunchActivity.this);
                                    builder.setTitle(LocaleController.getString("Proxy", C0446R.string.Proxy));
                                    builder.setMessage(LocaleController.formatString("ConnectingToProxyDisableAlert", C0446R.string.ConnectingToProxyDisableAlert, globalMainSettings.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID)));
                                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyDisable", C0446R.string.ConnectingToProxyDisable), new C14581());
                                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                    baseFragment.showDialog(builder.create());
                                }
                            }
                        }
                    };
                    String str2 = str;
                    str = i;
                    i = str2;
                } else {
                    i = 0;
                    anonymousClass30 = i;
                }
                this.actionBarLayout.setTitleOverlayText(i, str, anonymousClass30);
            }
            anonymousClass30 = null;
            this.actionBarLayout.setTitleOverlayText(i, str, anonymousClass30);
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
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
                if ((baseFragment instanceof ChatActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat");
                } else if (baseFragment instanceof SettingsActivity) {
                    bundle.putString("fragment", "settings");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "group");
                } else if (baseFragment instanceof WallpapersActivity) {
                    bundle.putString("fragment", "wallpapers");
                } else if ((baseFragment instanceof ProfileActivity) && ((ProfileActivity) baseFragment).isChat() && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat_profile");
                } else if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "channel");
                } else if ((baseFragment instanceof ChannelEditActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "edit");
                }
                baseFragment.saveSelfArgs(bundle);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                z = ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed() ^ 1;
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
        try {
            Menu menu = actionMode.getMenu();
            if (!(menu == null || this.actionBarLayout.extendActionMode(menu) || !AndroidUtilities.isTablet() || this.rightActionBarLayout.extendActionMode(menu))) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout) {
        boolean z3 = true;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z4 = baseFragment instanceof LoginActivity;
            boolean z5 = (z4 || (baseFragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z5, true);
            if ((baseFragment instanceof DialogsActivity) && ((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
                this.actionBarLayout.presentFragment(baseFragment, z, z2, false);
                this.layersActionBarLayout.removeAllFragments();
                this.layersActionBarLayout.setVisibility(8);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                if (this.tabletFullSize == null) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty() != null) {
                        this.backgroundTablet.setVisibility(0);
                    }
                }
                return false;
            } else if (baseFragment instanceof ChatActivity) {
                if ((!this.tabletFullSize && actionBarLayout == this.rightActionBarLayout) || (this.tabletFullSize && actionBarLayout == this.actionBarLayout)) {
                    if (this.tabletFullSize && actionBarLayout == this.actionBarLayout) {
                        if (this.actionBarLayout.fragmentsStack.size()) {
                            z = false;
                            if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                                while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                                    this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                                }
                                this.layersActionBarLayout.closeLastFragment(z2 ^ 1);
                            }
                            if (!z) {
                                this.actionBarLayout.presentFragment(baseFragment, false, z2, false);
                            }
                            return z;
                        }
                    }
                    z = true;
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(z2 ^ 1);
                    }
                    if (z) {
                        this.actionBarLayout.presentFragment(baseFragment, false, z2, false);
                    }
                    return z;
                } else if (!this.tabletFullSize && actionBarLayout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(baseFragment, z, true, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(z2 ^ 1);
                    }
                    return false;
                } else if (!this.tabletFullSize || actionBarLayout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - true <= false) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(z2 ^ 1);
                    }
                    z = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        z3 = false;
                    }
                    z.presentFragment(baseFragment, z3, z2, false);
                    return false;
                } else {
                    this.actionBarLayout.presentFragment(baseFragment, this.actionBarLayout.fragmentsStack.size() > 1 ? 1 : null, z2, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(z2 ^ 1);
                    }
                    return false;
                }
            } else if (actionBarLayout == this.layersActionBarLayout) {
                return true;
            } else {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (z4) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(baseFragment, z, z2, false);
                return false;
            }
        }
        if (!(baseFragment instanceof LoginActivity)) {
            if ((baseFragment instanceof CountrySelectActivity) != null && mainFragmentsStack.size() == 1) {
            }
            baseFragment = 1;
            this.drawerLayoutContainer.setAllowOpenDrawer(baseFragment, false);
            return true;
        }
        baseFragment = null;
        this.drawerLayoutContainer.setAllowOpenDrawer(baseFragment, false);
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = baseFragment instanceof LoginActivity;
            boolean z2 = (z || (baseFragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z2, true);
            if (baseFragment instanceof DialogsActivity) {
                if (((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout != this.actionBarLayout) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(baseFragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (this.tabletFullSize == null) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.fragmentsStack.isEmpty() != null) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            } else if (baseFragment instanceof ChatActivity) {
                if (!this.tabletFullSize && actionBarLayout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(baseFragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return false;
                } else if (this.tabletFullSize && actionBarLayout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(baseFragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return false;
                }
            } else if (actionBarLayout != this.layersActionBarLayout) {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (z) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.addFragmentToStack(baseFragment);
                return false;
            }
            return true;
        }
        if ((baseFragment instanceof LoginActivity) == null) {
            if ((baseFragment instanceof CountrySelectActivity) != null && mainFragmentsStack.size() == 1) {
            }
            baseFragment = 1;
            this.drawerLayoutContainer.setAllowOpenDrawer(baseFragment, false);
            return true;
        }
        baseFragment = null;
        this.drawerLayoutContainer.setAllowOpenDrawer(baseFragment, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.rightActionBarLayout) {
                if (this.tabletFullSize == null) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() != null && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (actionBarLayout.fragmentsStack.size() >= 2 && (actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity) == null) {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean z) {
        if (this.layersActionBarLayout != null) {
            this.layersActionBarLayout.rebuildAllFragmentViews(z, z);
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
